#include "jlg_dht_default.h"
#include "jlg_md.h"
#include "jlg_tcp_client.h"
#include "jlg_string.h"

#include <sys/time.h>
#include <sys/socket.h>
#include <netinet/in.h> // for sockaddr_in
#include <arpa/inet.h>

// Simple Naive implementation
// - a node must know all other node and the responsability of each one
// 

// Concept : order (or request)
// example : find this key, or store this key...
// order has :
// - an agent orderer (a client)
// - the order description (verb and args)
// an order is routed amongs the agents to the agent that has the responsability to fulfill the order

dht_default_t *dht_default_create() {
	JLG_CREATE(p, dht_default_t);
	return p;
}

int dht_default_delete(dht_default_t **dht_defaultpp) {
	if (dht_defaultpp && *dht_defaultpp) {
		dht_default_t *dht_defaultp = *dht_defaultpp;
		hash_delete(&(dht_defaultp->network));
	}
	JLG_FREE(dht_defaultpp);
	return 0;
}

int dht_default_init(dht_t *dhtp) {


	dht_default_t *dht_defaultp = dht_default_create();
	dhtp->impl = dht_defaultp;

	char *key_length_buffer = NULL;
	hash_get(dhtp->p->hashp, "default.key_length", (void **) &key_length_buffer);
	int key_length = 0;
	if (key_length_buffer) {
		key_length = JLG_TRY(jlg_atoi(key_length_buffer));
	}
	dht_defaultp->key_length = key_length;

	char *md_algo_name = NULL;
	hash_get(dhtp->p->hashp, "default.md_algo_name", (void **) &md_algo_name);
	if (md_algo_name) {
		strlcpy(dht_defaultp->md_algo_name, md_algo_name, 8);
	} else {
		strlcpy(dht_defaultp->md_algo_name, "sha1", 8);
	}
	

cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_md(dht_t *dhtp, char *mdkey, char *key) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	jlg_md_str(key, mdkey, dht_defaultp->key_length, dht_defaultp->md_algo_name);
	return 0;
}

int dht_default_generate_node_id(dht_t *dhtp, char *node_id) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	struct timeval tv;
	struct timezone tz;
	gettimeofday(&tv, &tz);
	char now_str[BUFFER_SIZE] = "";
	snprintf(now_str, BUFFER_SIZE, "%ld", tv.tv_usec);
	jlg_md_str(now_str, node_id, dht_defaultp->key_length, dht_defaultp->md_algo_name);
	
	// check that it is not already taken on the network
	// probably very rare but for better stability
	dll_node_t *nodep = dht_defaultp->network->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		if (EQUALS(pairp->key, node_id)) {
			dht_default_generate_node_id(dhtp, node_id);
			break;
		}
		nodep = nodep->nextp;
	}
	
//cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}

int dht_default_set_node_id(dht_t *dhtp) {
	// node id is sticky (ie remanent).
	// if it is in the state properties file
	// then take it
	JLG_DEBUG("dht_default_set_node_id");
	properties_t *p = properties_create();
	properties_set_filename(p, "./temp.properties");
	properties_reload(p);
	char *node_id_buffer = NULL;
	hash_get(p->hashp, "node_id", (void **) &node_id_buffer);
	char node_id_string[BUFFER_SIZE] = "";
	if (!node_id_buffer) {
		JLG_TRY(dht_default_generate_node_id(dhtp, node_id_string));
		node_id_buffer = node_id_string;
		properties_set(p, "node_id", node_id_buffer);
		properties_save(p);
	}
	dht_agent_set_node_id(dhtp->agentp, node_id_buffer);
	JLG_DEBUG("node_id = %s", dhtp->agentp->node_id);

cleanup:
	JLG_LOG_ERROR_IF_ANY;
	properties_delete(&p);
	return JLG_RETURN_CODE;	
}

// node arrival
int dht_default_attach(dht_t *dhtp) {
	JLG_DEBUG("dht_default_attach");
	
	JLG_TRY(dht_default_set_network(dhtp));
	
	JLG_TRY(dht_default_set_node_id(dhtp));
	
	// add node_id to the network
	JLG_TRY(dht_default_add_node_id(dhtp));
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_set_network(dht_t *dhtp) {
	char **agent_array = NULL;
	char **agent_field_array = NULL;
	
	JLG_DEBUG("dht_default_set_network");
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	hash_delete(&(dht_defaultp->network));
	hash_config_t *cfgp = hash_config_create();
	cfgp->free_func = dht_agent_free;
	cfgp->copy_func = dht_agent_copy;
	dht_defaultp->network = hash_create_ex(cfgp);

	if (dhtp->sponsor_agentp == NULL) {
		// no sponsor so this is the first agent.
		goto cleanup;
	}

	// copy the network of the sponsor
	tcp_client_t *tcp_clientp = tcp_client_create();
	tcp_client_set_hostname(tcp_clientp, dhtp->sponsor_agentp->hostname);
	tcp_clientp->port = dhtp->sponsor_agentp->tcp_port;
	char *request = "NETWORK";
	char response[BUFFER_SIZE] = "";
	JLG_TRY(tcp_client_send(tcp_clientp, request, response, BUFFER_SIZE));
	// response format is:
	// <agent1>:<agent2>:...:<agentN>
	// <agentX> = <node_id>;<hostname>;<tcp_port>;<udp_port>
	// n=12341234;h=localhost;p=4444:
	// transform the response to the hash
	if (NOT_CONTAINS(response, ";")) {
		goto cleanup;
	}
	int agent_nbr = 0;
	strsplit(&agent_array, &agent_nbr, response, ":");
	int i = 0;
	for (i = 0; i < agent_nbr; i++) {
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, agent_array[i], ";");
		if (agent_field_nbr != 4) {
			JLG_THROW_ERROR("DHT Protocol Error: agent_field_nbr[=%d] != 3)", agent_field_nbr);
		}
		dht_agent_t *agentp = dht_agent_create();
		agentp->node_id = strdup(agent_field_array[0]);
		agentp->hostname = strdup(agent_field_array[1]);
		agentp->tcp_port = JLG_TRY(atoi(agent_field_array[2]));
		agentp->udp_port = JLG_TRY(atoi(agent_field_array[3]));
		strsplit_free(&agent_field_array);
		hash_put(dht_defaultp->network, agentp->node_id, agentp);
	}
	
	JLG_STOP_ON_ERROR;
	JLG_DEBUG("dht_default_set_network end");
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	strsplit_free(&agent_field_array);
	strsplit_free(&agent_array);
	return JLG_RETURN_CODE;	
}

int dht_default_add_node_id(dht_t *dhtp) {
	tcp_client_t *tcp_clientp = NULL;
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	
	// contact all the network and add me to their network hashtable
	dll_node_t *nodep = dht_defaultp->network->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		nodep = nodep->nextp;
		dht_agent_t *agentp = (dht_agent_t *) pairp->value;
		char request[BUFFER_SIZE] = "";
		snprintf(request, BUFFER_SIZE, "ATTACH:%s:%d:%d", dhtp->agentp->node_id, dhtp->agentp->tcp_port, dhtp->agentp->udp_port);
		char response[BUFFER_SIZE] = "";
		JLG_TRY(dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE));
	}
	JLG_STOP_ON_ERROR;
	// then add me as well
	hash_set(dht_defaultp->network, dhtp->agentp->node_id, dhtp->agentp);
	
	
	
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	tcp_client_delete(&tcp_clientp);
	return JLG_RETURN_CODE;
}

int dht_default_detach(dht_t *dhtp, dht_agent_t *detach_agentp) {
	JLG_DEBUG("dht_default_detach");
	tcp_client_t *tcp_clientp = NULL;
	char *node_id = strdup(detach_agentp->node_id);
	
	// send a DETACH:<node_id> order to the whole network except the one to detach.
	// for the one to detach, remove it from the network
	// if some of the network does not answer, put them in a stack and detach them as well
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	dll_node_t *nodep = dht_defaultp->network->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		char buffer[BUFFER_SIZE] = "";
		dht_agent_to_string((dht_agent_t *) pairp->value, buffer, BUFFER_SIZE);
		JLG_DEBUG("%s->%s", pairp->key, buffer);
		nodep = nodep->nextp;
	}
	
	hash_remove(dht_defaultp->network, detach_agentp->node_id);
	JLG_DEBUG("after removing %s", detach_agentp->node_id);
	nodep = dht_defaultp->network->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		char buffer[BUFFER_SIZE] = "";
		dht_agent_to_string((dht_agent_t *) pairp->value, buffer, BUFFER_SIZE);
		JLG_DEBUG("%s->%s", pairp->key, buffer);
		nodep = nodep->nextp;
	}

	//dll_node_t *nodep = dht_defaultp->network->dlistp->nodep;
	nodep = dht_defaultp->network->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		nodep = nodep->nextp;
		dht_agent_t *agentp = (dht_agent_t *) pairp->value;
		JLG_DEBUG("agentp->node_id = %s", agentp->node_id);
		if (EQUALS(agentp->node_id, dhtp->agentp->node_id)) {
			continue;
		}
		tcp_clientp = tcp_client_create();
		tcp_client_set_hostname(tcp_clientp, agentp->hostname);
		tcp_clientp->port = agentp->tcp_port;
		char request[BUFFER_SIZE] = "";
		snprintf(request, BUFFER_SIZE, "DETACH:%s", node_id);
		char response[BUFFER_SIZE] = "";
		if (tcp_client_send(tcp_clientp, request, response, BUFFER_SIZE)) {
			jlg_reset_error_message();
			// this agent is off.
			char agent_str[BUFFER_SIZE] = "";
			dht_agent_to_string(agentp, agent_str, BUFFER_SIZE);
			JLG_DEBUG("dht_default_detach. The agent %s is not reachable.", agent_str);
			
		} else {
			JLG_DEBUG("send succeed");
		}
		tcp_client_delete(&tcp_clientp);
	}
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	tcp_client_delete(&tcp_clientp);
	JLG_FREE(&node_id);
	return JLG_RETURN_CODE;
}

int dht_default_set(dht_t *dhtp, char *key, char *valuep) {
	char mdkey[BUFFER_SIZE] = "";
	dht_md(dhtp, mdkey, key);
	dht_agent_t *agentp = dht_get_agent_from_mdkey(dhtp, mdkey);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "SET:%s:%s", key, valuep);
	char response[BUFFER_SIZE] = "";
	dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE);
	if (EQUALS(response, "OK")) {
		JLG_DEBUG("job done");
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_get(dht_t *dhtp, char *key, char **valuepp) {

	// find the node which is responsible for the key
	if (!valuepp) {
		return 1;
	}
	*valuepp = NULL;
	char mdkey[BUFFER_SIZE] = "";
	dht_md(dhtp, mdkey, key);
	dht_agent_t *agentp = dht_get_agent_from_mdkey(dhtp, mdkey);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "GET:%s", key);
	char response[BUFFER_SIZE] = "";
	dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE);
	// expected response:
	// NOT_FOUND
	// or
	// value
	if (EQUALS(response, "NOT_FOUND")) {
		goto cleanup;
	}
	*valuepp = strdup(response);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

dht_agent_t *dht_default_get_agent_from_mdkey(dht_t *dhtp, char *mdkey) {
	// an agent is responsable for all mdkey between its node_id and the node_id of its successor.
	// if there is one agent, the sucessor is itself.
	
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return NULL;
}


void dht_default_tcp_protocol(void *p, int client_socket) {
	char **agent_field_array = NULL;
	
	tcp_server_t *tcp_serverp = (tcp_server_t *) p;
	dht_t *dhtp = (dht_t *) tcp_serverp->contextp;
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;

	JLG_DEBUG("I am the protocol...");
	// read the input message
	// by convention for the time being, an input message size cannot exceed 1024 bytes
	// if it exceed, then it is truncated.
	char input_message[BUFFER_SIZE] = "";
	dht_read_message(client_socket, input_message);
	JLG_DEBUG("input_message = |%s|", input_message);
	if (EQUALS(input_message, "PING")) {
		dht_write_message(client_socket, "0");
	} else if (EQUALS(input_message, "NETWORK")) {
		// give all the network agent list included myself
		dll_node_t *nodep = dht_defaultp->network->dlistp->nodep;
		bool first = true;
		while (nodep) {
			if (!first) {
				dht_write_message(client_socket, ":");
			}
			hash_pair_t *pairp = nodep->valuep;
			dht_agent_t *agentp = (dht_agent_t *) pairp->value;
			char buffer[BUFFER_SIZE] = "";
			snprintf(buffer, BUFFER_SIZE, "%s;%s;%d;%d", agentp->node_id, agentp->hostname, agentp->tcp_port, agentp->udp_port);
			dht_write_message(client_socket, buffer);
			nodep = nodep->nextp;
			first = false;
		}
	} else if (STARTS_WITH(input_message, "ATTACH")) {
		JLG_DEBUG("Attach request: %s", input_message);
		
		struct    sockaddr address;
		// set all the fields to 0
		memset(&address, 0, sizeof(address));
		socklen_t address_len = sizeof(address);
		JLG_CHECK(getpeername(client_socket, &address, &address_len) == -1, "Error in getpeername.");
		struct sockaddr_in *s = (struct sockaddr_in *)&address;
		// int port = ntohs(s->sin_port);
		char ipstr[BUFFER_SIZE] = "";
		inet_ntop(AF_INET, &s->sin_addr, ipstr, sizeof(ipstr));
		
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 4) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad ATTACH message.");
		}
		dht_agent_t *agentp = dht_agent_create();
		agentp->node_id = strdup(agent_field_array[1]);
		agentp->hostname = strdup(ipstr);
		agentp->tcp_port = JLG_TRY(atoi(agent_field_array[2]));
		agentp->udp_port = JLG_TRY(atoi(agent_field_array[3]));
		strsplit_free(&agent_field_array);
		hash_put(dht_defaultp->network, agentp->node_id, agentp);
		
		dht_write_message(client_socket, "0");
	} else if (STARTS_WITH(input_message, "DETACH")) {
		JLG_DEBUG("Attach request: %s", input_message);
		
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 2) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad DETACH message.");
		}
		char *node_id = agent_field_array[1];
		hash_remove(dht_defaultp->network, node_id);
		strsplit_free(&agent_field_array);
		dht_write_message(client_socket, "0");		
	} else {
		dht_write_message(client_socket, "1");
	}
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	strsplit_free(&agent_field_array);
	return;

}

