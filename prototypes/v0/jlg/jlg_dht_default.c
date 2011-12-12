#include "jlg_dht_default.h"
#include "jlg_md.h"
#include "jlg_tcp_client.h"
#include "jlg_string.h"
#include "jlg_btree.h"

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
	p->hashp = hash_create();
	hash_config_t *cfg = hash_config_create();
	cfg->free_func = btree_free;
	p->meta_hashp = hash_create_ex(cfg);
	return p;
}

int dht_default_delete(dht_default_t **dht_defaultpp) {
	if (dht_defaultpp && *dht_defaultpp) {
		dht_default_t *dht_defaultp = *dht_defaultpp;
		btree_delete(&(dht_defaultp->network));
		hash_delete(&(dht_defaultp->hashp));
		hash_delete(&(dht_defaultp->meta_hashp));
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

	char *backup_nbr_buffer = NULL;
	hash_get(dhtp->p->hashp, "default.backup_nbr", (void **) &backup_nbr_buffer);
	int backup_nbr = 0;
	if (backup_nbr_buffer) {
		backup_nbr = JLG_TRY(jlg_atoi(backup_nbr_buffer));
	}
	dht_defaultp->backup_nbr = backup_nbr;


cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_md(dht_t *dhtp, char *mdkey, char *key) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	jlg_md_str(key, mdkey, dht_defaultp->key_length, dht_defaultp->md_algo_name);
	return 0;
}

dht_agent_t *dht_default_get_predecessor_agent(dht_t *dhtp) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	dht_agent_t *predecessor_agentp = NULL;
	ll_node_t *nodep = btree_ordered_list(dht_defaultp->network)->first;
	while (nodep) {
		dht_agent_t *agentp = nodep->valuep;
		char agent_str[BUFFER_SIZE] = "";
		dht_agent_to_string(agentp, agent_str, BUFFER_SIZE);
		JLG_DEBUG("agent = %s", agent_str);
		nodep = nodep->nextp;
	}
	char agent_str[BUFFER_SIZE] = "";
	dht_agent_to_string(dhtp->agentp, agent_str, BUFFER_SIZE);
	JLG_DEBUG("dhtp->agent = %s", agent_str);
	
	btree_get_previous(dht_defaultp->network, dhtp->agentp, (void **) &predecessor_agentp);
	
	if (!predecessor_agentp) {
		return predecessor_agentp;
	}
	dht_agent_to_string(predecessor_agentp, agent_str, BUFFER_SIZE);
	JLG_DEBUG("predecessor_agentp = %s", agent_str);

	if (!dht_agent_ping(predecessor_agentp)) {
		dht_default_detach(dhtp, predecessor_agentp);
		predecessor_agentp = dht_default_get_predecessor_agent(dhtp);
	}
//cleanup:
	return predecessor_agentp;
}

int dht_default_save_data(dht_t *dhtp) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	// is there still agent on the network (at least the predecessor) ?
	dht_agent_t *predecessor_agentp = dht_default_get_predecessor_agent(dhtp);
	if (predecessor_agentp == NULL) {
		JLG_DEBUG("It seems I am the last agent of the network... so I cannot save my data on another agent.");
		goto cleanup;
	}
	char agent_str[BUFFER_SIZE] = "";
	dht_agent_to_string(predecessor_agentp, agent_str, BUFFER_SIZE);
	JLG_DEBUG("About to transfer data to predecessor agent %s", agent_str);
	dll_node_t *nodep = dht_defaultp->hashp->dlistp->first;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		dht_default_set(dhtp, pairp->key, pairp->value);
		nodep = nodep->nextp;
	}
	// TODO: save metadata
	// dht_default_transfer_metadata(dhtp);

cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}

int dht_default_load_data(dht_t *dhtp) {
	// is there still agent on the network (at least the predecessor) ?
	dht_agent_t *predecessor_agentp = dht_default_get_predecessor_agent(dhtp);
	if (predecessor_agentp == NULL) {
		JLG_DEBUG("It seems I am the last agent of the network...");
		goto cleanup;
	}
	char agent_str[BUFFER_SIZE] = "";
	dht_agent_to_string(dhtp->agentp, agent_str, BUFFER_SIZE);
	JLG_DEBUG("I am agent %s", agent_str);
	dht_agent_to_string(predecessor_agentp, agent_str, BUFFER_SIZE);
	JLG_DEBUG("About to transfer data from predecessor agent %s", agent_str);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "RELOAD");
	char response[BUFFER_SIZE] = "";
	dht_tcp_request(dhtp, predecessor_agentp, request, response, BUFFER_SIZE);
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_generate_node_id(dht_t *dhtp, char *node_id) {
	dht_agent_t *key_agentp = NULL;
	
	JLG_DEBUG("dht_default_generate_node_id");
	
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	struct timeval tv;
	struct timezone tz;
	gettimeofday(&tv, &tz);
	char now_str[BUFFER_SIZE] = "";
	snprintf(now_str, BUFFER_SIZE, "%ld", tv.tv_usec);
	jlg_md_str(now_str, node_id, dht_defaultp->key_length, dht_defaultp->md_algo_name);
	
	// check that it is not already taken on the network
	// probably very rare but for better stability
	key_agentp = dht_agent_create();
	dht_agent_set_node_id(key_agentp, node_id);
	if (btree_contains(dht_defaultp->network, key_agentp)) {
		JLG_DEBUG("node_id (%s) already existing in the network.", node_id);
		dht_default_generate_node_id(dhtp, node_id);
	}
	
//cleanup:
	JLG_LOG_ERROR_IF_ANY;
	dht_agent_delete(&key_agentp);
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

	dht_default_load_data(dhtp);
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_set_network(dht_t *dhtp) {
	char **agent_array = NULL;
	char **agent_field_array = NULL;
	
	JLG_DEBUG("dht_default_set_network");
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	btree_delete(&(dht_defaultp->network));
	btree_config_t *cfg = btree_config_create();
	cfg->cmp_func = dht_agent_cmp;
	cfg->free_func = dht_agent_free;
	cfg->copy_func = dht_agent_copy;
	cfg->str_func = dht_agent_to_string;
	dht_defaultp->network = btree_create_ex(cfg);

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
		btree_put(dht_defaultp->network, agentp);
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
	ll_node_t *nodep = btree_ordered_list(dht_defaultp->network)->first;
	while (nodep) {
		dht_agent_t *agentp = (dht_agent_t *) nodep->valuep;
		nodep = nodep->nextp;		
		char request[BUFFER_SIZE] = "";
		snprintf(request, BUFFER_SIZE, "ATTACH:%s:%d:%d", dhtp->agentp->node_id, dhtp->agentp->tcp_port, dhtp->agentp->udp_port);
		char response[BUFFER_SIZE] = "";
		dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE);
	}
	JLG_STOP_ON_ERROR;
	// then add me as well (duplicate)
	btree_set(dht_defaultp->network, dhtp->agentp);
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	tcp_client_delete(&tcp_clientp);
	return JLG_RETURN_CODE;
}

int dht_default_nice_detach(dht_t *dhtp) {
	JLG_DEBUG("dht_default_nice_detach");
	
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "DETACH:%s", dhtp->agentp->node_id);
	
	// send a DETACH:<node_id> order to the whole network except the one to detach.
	// for the one to detach, remove it from the network
	// if some of the network does not answer, ignore because they will be detached next time we
	// use them
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	btree_remove(dht_defaultp->network, dhtp->agentp);
	ll_node_t *nodep = btree_ordered_list(dht_defaultp->network)->first;
	while (nodep) {
		dht_agent_t *agentp = (dht_agent_t *) nodep->valuep;
		nodep = nodep->nextp;
		
		char response[BUFFER_SIZE] = "";
		JLG_TRY(dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE));
	}
	
	dht_default_save_data(dhtp);
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}

int dht_default_detach(dht_t *dhtp, dht_agent_t *detach_agentp) {
	tcp_client_t *tcp_clientp = NULL;
	char *detach_node_id = NULL;
	
	JLG_DEBUG("dht_default_detach");
	if (dht_agent_cmp(detach_agentp, dhtp->agentp) == 0) {
		JLG_DEBUG("detaching myself");
		dht_default_nice_detach(dhtp);
		goto cleanup;
	}
	detach_node_id = strdup(detach_agentp->node_id);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "DETACH:%s", detach_node_id);
	
	// send a DETACH:<node_id> order to the whole network except the one to detach.
	// for the one to detach, remove it from the network
	// if some of the network does not answer, ignore because they will be detached next time we
	// use them
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	btree_remove(dht_defaultp->network, detach_agentp);
	
	ll_node_t *nodep = btree_ordered_list(dht_defaultp->network)->first;
	while (nodep) {
		dht_agent_t *agentp = (dht_agent_t *) nodep->valuep;
		nodep = nodep->nextp;
		
		JLG_DEBUG("agentp->node_id = %s", agentp->node_id);
		if (EQUALS(agentp->node_id, dhtp->agentp->node_id)) {
			continue;
		}
		tcp_clientp = tcp_client_create();
		tcp_client_set_hostname(tcp_clientp, agentp->hostname);
		tcp_clientp->port = agentp->tcp_port;
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
	
	// backup data for the detached agent.
	dht_default_restore_data(dhtp, detach_node_id);
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	tcp_client_delete(&tcp_clientp);
	return JLG_RETURN_CODE;
}

int dht_default_restore_data(dht_t *dhtp, char *detach_node_id) {
	// retrieve all key from the detached node.
	linked_list_t *listp = NULL;
	char *value = NULL;
	char *orig_key = NULL;
	
	JLG_DEBUG("dht_default_restore_data");
	if (dht_default_mget(dhtp, detach_node_id, &listp) == DHT_ERR_NOT_FOUND) {
		JLG_DEBUG("no data were stored for the detached node.");
		goto cleanup;
	}
	JLG_DEBUG("foreach key");
	ll_node_t *nodep = listp->first;
	while (nodep) {
		char *key = nodep->valuep;
		JLG_DEBUG("key = %s", key);
		orig_key  = make_orig_key(key);
		JLG_DEBUG("orig_key = %s", orig_key);
		dht_default_backup_get(dhtp, orig_key, &value);
		dht_default_set(dhtp, key, value);

		JLG_FREE(&value);
		JLG_FREE(&orig_key);
		nodep = nodep->nextp;
	}
	
	dht_default_mdelete(dhtp, detach_node_id);
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	ll_delete(&listp);
	JLG_FREE(&value);
	JLG_FREE(&orig_key);
	return JLG_RETURN_CODE;
	
}

int dht_default_set(dht_t *dhtp, char *key, char *value) {
	dht_agent_t *agentp = dht_get_agent_from_key(dhtp, key);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "SET:%s:%s", key, value);
	char response[BUFFER_SIZE] = "";
	dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE);
	if (EQUALS(response, "0")) {
		JLG_DEBUG("job done");
	}

	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}


int dht_default_backup_set(dht_t *dhtp, char *key, char *value) {
	char *bkp_key = NULL;
	
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_key = make_bkp_key(key, i);
		dht_default_set(dhtp, bkp_key, value);
		JLG_FREE(&bkp_key);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	JLG_FREE(&bkp_key);
	return JLG_RETURN_CODE;
}

int dht_default_get(dht_t *dhtp, char *key, char **valuepp) {
	int status = 0;
	// find the node which is responsible for the key
	if (!valuepp) {
		return 1;
	}
	*valuepp = NULL;
	dht_agent_t *agentp = dht_get_agent_from_key(dhtp, key);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "GET:%s", key);
	char response[BUFFER_SIZE] = "";
	while (dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE) == DHT_ERR_NOT_REACHABLE) {
		agentp = dht_get_agent_from_key(dhtp, key);
	}
	// expected response:
	// NOT_FOUND
	// or
	// value
	if (EQUALS(response, "NOT_FOUND")) {
		status = DHT_ERR_NOT_FOUND;
		goto cleanup;
	}
	*valuepp = strdup(response);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return status;
}

int dht_default_backup_get(dht_t *dhtp, char *key, char **valuepp) {
	char *bkp_key = NULL;
	int status = DHT_ERR_NOT_FOUND;
	
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	// try to find the first available backup.
	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_key = make_bkp_key(key, i);
		status = dht_default_get(dhtp, bkp_key, valuepp);
		if (status != DHT_ERR_NOT_FOUND) {
			break;
		}
		JLG_FREE(&bkp_key);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return status;	
}

int dht_default_remove(dht_t *dhtp, char *key) {
	int status = 0;
	// find the node which is responsible for the key
	dht_agent_t *agentp = dht_get_agent_from_key(dhtp, key);
	char request[BUFFER_SIZE] = "";
	snprintf(request, BUFFER_SIZE, "REMOVE:%s", key);
	char response[BUFFER_SIZE] = "";
	dht_tcp_request(dhtp, agentp, request, response, BUFFER_SIZE);
	// expected response:
	// NOT_FOUND
	// or
	// value
	if (EQUALS(response, "NOT_FOUND")) {
		status = DHT_ERR_NOT_FOUND;
		goto cleanup;
	}
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return status;
}

int dht_default_backup_remove(dht_t *dhtp, char *key) {
	// remove all the backup
	char *bkp_key = NULL;
	int status = DHT_ERR_NOT_FOUND;
	
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;

	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_key = make_bkp_key(key, i);
		int bkp_status = dht_default_remove(dhtp, bkp_key);
		if (bkp_status != DHT_ERR_NOT_FOUND) {
			status = bkp_status;
		}
		JLG_FREE(&bkp_key);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return status;		
}

int dht_default_local_set(dht_t *dhtp, char *key, char *valuep) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	hash_set(dht_defaultp->hashp, key, valuep);
	
	// update meta_hashp of nurse agents
	dht_default_mset(dhtp, dhtp->agentp, key);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_local_get(dht_t *dhtp, char *key, char **valuepp) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	if (hash_get(dht_defaultp->hashp, key, (void **) valuepp) == HASH_ERR_NOT_FOUND) {
		return DHT_ERR_NOT_FOUND;
	}
	return 0;
}

int dht_default_local_remove(dht_t *dhtp, char *key) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	if (hash_remove(dht_defaultp->hashp, key) == HASH_ERR_NOT_FOUND) {
		return DHT_ERR_NOT_FOUND;
	}
	// update meta_hashp of nurse agents
	dht_default_mremove(dhtp, dhtp->agentp, key);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}


dht_agent_t *dht_default_get_agent_from_mdkey(dht_t *dhtp, char *mdkey) {
	// an agent A is responsable for all mdkey such A<=mdkey<successor(A)
	// if there is one agent, the sucessor is itself.

	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	dht_agent_t *agentp = NULL;
	dht_agent_t *key_agentp = dht_agent_create();
	dht_agent_set_node_id(key_agentp, mdkey);
	// very rare : mdkey = A
	int ret = btree_get(dht_defaultp->network, key_agentp, (void **) &agentp);
	if (ret != BTREE_ERR_NOT_FOUND) {
		goto cleanup;
	}
	// most often 99.999% of cases
	btree_get_previous(dht_defaultp->network, key_agentp, (void **) &agentp);
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	dht_agent_delete(&key_agentp);
	return agentp;
}


dht_agent_t *dht_default_get_agent_from_key(dht_t *dhtp, char *key) {
	
	char mdkey[BUFFER_SIZE] = "";
	dht_default_md(dhtp, mdkey, key);
	JLG_DEBUG("mdkey(%s)=%s", key, mdkey);
	
	return dht_default_get_agent_from_mdkey(dhtp, mdkey);
}

hash_t *dht_default_get_local_content(dht_t *dhtp) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	return dht_defaultp->hashp;
}

int dht_default_local_filter(dht_t *dhtp) {
	// remove the local data where the agent is not responsible anymore.
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	dll_node_t *nodep = dht_defaultp->hashp->dlistp->first;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		nodep = nodep->nextp;
		dht_agent_t *agentp = dht_default_get_agent_from_key(dhtp, pairp->key);
		if (dht_agent_cmp(dhtp->agentp, agentp) != 0) {
			hash_remove(dht_defaultp->hashp, pairp->key);
		}
	}
	nodep = dht_defaultp->meta_hashp->dlistp->first;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		nodep = nodep->nextp;
		dht_agent_t *agentp = dht_default_get_agent_from_key(dhtp, pairp->key);
		if (dht_agent_cmp(dhtp->agentp, agentp) != 0) {
			hash_remove(dht_defaultp->meta_hashp, pairp->key);
		}
	}

	return JLG_RETURN_CODE;
}

char *make_bkp_key(char *key, int i) {
	size_t len = strlen(key) + 10;
	char *bkp_key = (char *) malloc(sizeof(char *) * len);
	snprintf(bkp_key, len, "%s[%02d]", key, i);
	return bkp_key;
}

char *make_orig_key(char *key) {
	char *orig_key = strdup(key);
	strsub(orig_key, key, 0, -4);
	return orig_key;
}

int dht_default_mset(dht_t *dhtp, dht_agent_t *agentp, char *key) {
	char *bkp_node_id = NULL;
	// send to my nurses (node_id[xx]) an update on key_list
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	
	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_node_id = make_bkp_key(agentp->node_id, i);
		dht_agent_t *nurse_agentp = dht_get_agent_from_key(dhtp, bkp_node_id);
		char request[BUFFER_SIZE] = "";
		snprintf(request, BUFFER_SIZE, "MSET:%s:%s", bkp_node_id, key);
		char response[BUFFER_SIZE] = "";
		dht_tcp_request(dhtp, nurse_agentp, request, response, BUFFER_SIZE);
		JLG_FREE(&bkp_node_id);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_FREE(&bkp_node_id);
	return JLG_RETURN_CODE;
}

int dht_default_local_mset(dht_t *dhtp, char *bkp_node_id, char *key) {
	char *node_id = NULL;
	linked_list_t *listp = NULL;
	bool bAddExisting = false;
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	btree_t *btreep = NULL;
	if (hash_get(dht_defaultp->meta_hashp, bkp_node_id, (void **) &btreep) == HASH_ERR_NOT_FOUND) {
		// retrieve another backup for this node_id and copy all its value
		node_id = make_orig_key(bkp_node_id);
		if (dht_default_mget(dhtp, node_id, &listp) != DHT_ERR_NOT_FOUND) {
			bAddExisting = true;
		}

		btreep = btree_create();
		hash_put(dht_defaultp->meta_hashp, bkp_node_id, btreep);
		if (bAddExisting) {
			ll_node_t *nodep = listp->first;
			while (nodep) {
				JLG_DEBUG("set in tree: %s", (char *) nodep->valuep);
				btree_set(btreep, (char *) nodep->valuep);
				nodep = nodep->nextp;
			}

		}
	}
	btree_set(btreep, key);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	JLG_FREE(&node_id);
	ll_delete(&listp);
	return JLG_RETURN_CODE;
}

int dht_default_mremove(dht_t *dhtp, dht_agent_t *agentp, char *key) {
	char *bkp_node_id = NULL;
	// send to my nurses (node_id[xx]) an update on key_list
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	
	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_node_id = make_bkp_key(agentp->node_id, i);
		dht_agent_t *nurse_agentp = dht_get_agent_from_key(dhtp, bkp_node_id);
		char request[BUFFER_SIZE] = "";
		snprintf(request, BUFFER_SIZE, "MREMOVE:%s:%s", bkp_node_id, key);
		char response[BUFFER_SIZE] = "";
		dht_tcp_request(dhtp, nurse_agentp, request, response, BUFFER_SIZE);
		JLG_FREE(&bkp_node_id);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_FREE(&bkp_node_id);
	return JLG_RETURN_CODE;
}

int dht_default_local_mremove(dht_t *dhtp, char *bkp_node_id, char *key) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	btree_t *btreep = NULL;
	if (hash_get(dht_defaultp->meta_hashp, bkp_node_id, (void **) &btreep) != HASH_ERR_NOT_FOUND) {
		btree_remove(btreep, key);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_default_mget(dht_t *dhtp, char *node_id, linked_list_t **listpp) {
	char *bkp_node_id = NULL;
	char **agent_field_array = NULL;
	
	JLG_DEBUG("dht_default_mget");
	int status = 0;
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	char request[BUFFER_SIZE] = "";
	char response[BUFFER_SIZE] = "NOT_FOUND";
	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_node_id = make_bkp_key(node_id, i);
		JLG_DEBUG("bkp_node_id = %s", bkp_node_id);
		snprintf(request, BUFFER_SIZE, "MGET:%s", bkp_node_id);
		dht_agent_t *nurse_agentp = dht_get_agent_from_key(dhtp, bkp_node_id);
		JLG_FREE(&bkp_node_id);
		if (dht_agent_ping(nurse_agentp)) {
			dht_tcp_request(dhtp, nurse_agentp, request, response, BUFFER_SIZE);
			break;
		}
	}
	if (EQUALS(response, "NOT_FOUND")) {
		status = DHT_ERR_NOT_FOUND;
		goto cleanup;
	}
	// convert the response in linked list.
	int agent_field_nbr = 0;
	JLG_DEBUG("response mget = %s", response);
	strsplit(&agent_field_array, &agent_field_nbr, response, ":");
	linked_list_t *listp = ll_create();
	for (i = 0; i < agent_field_nbr; i++) {
		ll_push(listp, strdup(agent_field_array[i]));
	}
	JLG_STOP_ON_ERROR;
	*listpp = listp;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	JLG_FREE(&bkp_node_id);
	strsplit_free(&agent_field_array);
	return status;	
}

int dht_default_local_mget(dht_t *dhtp, char *bkp_node_id, btree_t **btreepp) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	if (hash_get(dht_defaultp->meta_hashp, bkp_node_id, (void **) btreepp) == HASH_ERR_NOT_FOUND) {
		return DHT_ERR_NOT_FOUND;
	}
	return 0;
}

int dht_default_mdelete(dht_t *dhtp, char *detach_node_id) {
	char *bkp_node_id = NULL;
	// send to my nurses (node_id[xx]) an update on key_list
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	
	int i = 0;
	for (i = 0; i < dht_defaultp->backup_nbr; i++) {
		bkp_node_id = make_bkp_key(detach_node_id, i);
		dht_agent_t *nurse_agentp = dht_get_agent_from_key(dhtp, bkp_node_id);
		char request[BUFFER_SIZE] = "";
		snprintf(request, BUFFER_SIZE, "MDELETE:%s", bkp_node_id);
		char response[BUFFER_SIZE] = "";
		dht_tcp_request(dhtp, nurse_agentp, request, response, BUFFER_SIZE);
		JLG_FREE(&bkp_node_id);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_FREE(&bkp_node_id);
	return JLG_RETURN_CODE;	
}

int dht_default_local_mdelete(dht_t *dhtp, char *bkp_node_id) {
	dht_default_t *dht_defaultp = (dht_default_t *) dhtp->impl;
	hash_remove(dht_defaultp->meta_hashp, bkp_node_id);
	return 0;
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
		ll_node_t *nodep = btree_ordered_list(dht_defaultp->network)->first;
		bool first = true;
		while (nodep) {
			if (!first) {
				dht_write_message(client_socket, ":");
			}
			dht_agent_t *agentp = (dht_agent_t *) nodep->valuep;
			char buffer[BUFFER_SIZE] = "";
			snprintf(buffer, BUFFER_SIZE, "%s;%s;%d;%d", agentp->node_id, agentp->hostname, agentp->tcp_port, agentp->udp_port);
			dht_write_message(client_socket, buffer);
			nodep = nodep->nextp;
			first = false;
		}
	} else if (STARTS_WITH(input_message, "ATTACH:")) {
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
		btree_put(dht_defaultp->network, agentp);
		
		dht_write_message(client_socket, "0");
	} else if (STARTS_WITH(input_message, "DETACH")) {
		JLG_DEBUG("Attach request: %s", input_message);
		
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 2) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad DETACH message.");
		}
		char *node_id = agent_field_array[1];
		dht_agent_t *agentp = dht_agent_create();
		dht_agent_set_node_id(agentp, node_id);
		btree_remove(dht_defaultp->network, agentp);
		dht_agent_delete(&agentp);
		strsplit_free(&agent_field_array);
		dht_write_message(client_socket, "0");		

	} else if (STARTS_WITH(input_message, "GET:")) {
		JLG_DEBUG("Get request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 2) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad GET message.");
		}
		char *key = agent_field_array[1];
		char *response = NULL;
		if (dht_default_local_get(dhtp, key, &response) == DHT_ERR_NOT_FOUND) {
			dht_write_message(client_socket, "NOT_FOUND");
		} else {		
			dht_write_message(client_socket, response);
		}
	} else if (STARTS_WITH(input_message, "REMOVE:")) {
		JLG_DEBUG("Get request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 2) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad REMOVE message.");
		}
		char *key = agent_field_array[1];
		if (dht_default_local_remove(dhtp, key) == DHT_ERR_NOT_FOUND) {
			dht_write_message(client_socket, "NOT_FOUND");
		} else {		
			dht_write_message(client_socket, "0");
		}
	} else if (STARTS_WITH(input_message, "SET:")) {
		JLG_DEBUG("Set request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 3) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad SET message.");
		}
		char *key = agent_field_array[1];
		char *value = agent_field_array[2];
		if (dht_default_local_set(dhtp, key, value) == 0) {
			dht_write_message(client_socket, "0");
		} else {		
			dht_write_message(client_socket, "1");
		}

	} else if (STARTS_WITH(input_message, "MSET:")) {
		JLG_DEBUG("MSet request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 3) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad MSET message.");
		}
		char *bkp_node_id = agent_field_array[1];
		char *key = agent_field_array[2];
		if (dht_default_local_mset(dhtp, bkp_node_id, key) == 0) {
			dht_write_message(client_socket, "0");
		} else {		
			dht_write_message(client_socket, "1");
		}
	} else if (STARTS_WITH(input_message, "MREMOVE:")) {
		JLG_DEBUG("MRemove request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 3) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad MREMOVE message.");
		}
		char *bkp_node_id = agent_field_array[1];
		char *key = agent_field_array[2];
		if (dht_default_local_mremove(dhtp, bkp_node_id, key) == 0) {
			dht_write_message(client_socket, "0");
		} else {		
			dht_write_message(client_socket, "1");
		}
	} else if (STARTS_WITH(input_message, "MGET:")) {
		JLG_DEBUG("MGet request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 2) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad MGET message.");
		}
		char *key = agent_field_array[1];
		btree_t *btreep = NULL;
		if (dht_default_local_mget(dhtp, key, &btreep) == DHT_ERR_NOT_FOUND) {
			dht_write_message(client_socket, "NOT_FOUND");
		} else {
			char response[BUFFER_SIZE] = "";
			ll_node_t *nodep = btree_ordered_list(btreep)->first;
			bool first = true;
			while (nodep) {
				if (!first) {
					strlcat(response, ":", BUFFER_SIZE);
				} else {
					first = false;
				}
				JLG_DEBUG("node value: %s", (char *) nodep->valuep);
				strlcat(response, (char *) nodep->valuep, BUFFER_SIZE);
				nodep = nodep->nextp;
			}
			dht_write_message(client_socket, response);
		}
	} else if (STARTS_WITH(input_message, "MDELETE:")) {
		JLG_DEBUG("MDelete request: %s", input_message);
		int agent_field_nbr = 0;
		strsplit(&agent_field_array, &agent_field_nbr, input_message, ":");
		if (agent_field_nbr != 2) {
			JLG_THROW_ERROR("DHT Protocol Error: Bad MDELETE message.");
		}
		char *bkp_node_id = agent_field_array[1];
		if (dht_default_local_mdelete(dhtp, bkp_node_id) == 0) {
			dht_write_message(client_socket, "0");
		} else {		
			dht_write_message(client_socket, "1");
		}
	} else if (STARTS_WITH(input_message, "RELOAD")) {
		JLG_DEBUG("Set request: %s", input_message);
		dll_node_t *nodep = dht_defaultp->hashp->dlistp->first;
		while (nodep) {
			hash_pair_t *pairp = nodep->valuep;
			dht_default_set(dhtp, pairp->key, pairp->value);
			nodep = nodep->nextp;
		}
		dht_default_local_filter(dhtp);
		dht_write_message(client_socket, "0");
		
	} else {
		dht_write_message(client_socket, "1");
	}
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	strsplit_free(&agent_field_array);
	return;
}

