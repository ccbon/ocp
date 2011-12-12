#include "jlg_dht.h"
#include "jlg_hash.h"
#include "jlg_string.h"
#include "jlg_tcp_server.h"
#include "jlg_tcp_client.h"
#include "jlg_dht_default.h"

#include <unistd.h> // for sleep
#include <stdlib.h>
#include <assert.h>

void dht_read_message(int client_socket, char *input_message) {
	input_message[0] = '\0';
	char buffer[BUFFER_SIZE] = "";
	while (true) {
		int qty_read = 0;
		JLG_CHECK((qty_read = read(client_socket, buffer, BUFFER_SIZE - 1)) < 0, "Error while reading the socket content.\n");
		buffer[BUFFER_SIZE - 1] = '\0';
		if (qty_read != BUFFER_SIZE - 1) {
			// we suppose it's because the end of stream
			break;
		}
	}
	strlcpy(input_message, buffer, BUFFER_SIZE);
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return;
}

void dht_write_message(int client_socket, char *output_message) {
	JLG_DEBUG("TCP Server response: %s", output_message);
	int ret = write(client_socket, output_message, strlen(output_message));
	JLG_CHECK(ret < 0, "Error while answering");
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return;
}

void tcp_dht_protocol(void *tcp_serverp, int client_socket) {
	// The protocol depends on the implementation
	dht_default_tcp_protocol(tcp_serverp, client_socket);
}

void udp_dht_protocol(void *p, char *incoming_message, char *client_hostname, int client_port) {
		
	JLG_DEBUG("Received from %s:%d |%s|", client_hostname, client_port, incoming_message);
//cleanup:
	return;
}

dht_agent_t *dht_agent_create() {
	JLG_CREATE(p, dht_agent_t);
	return p;
}

int dht_agent_delete(dht_agent_t **agentpp) {
	if (agentpp && *agentpp) {
		dht_agent_t *agentp = *agentpp;
		JLG_FREE(&(agentp->hostname));
		JLG_FREE(&(agentp->node_id));
	}
	JLG_FREE(agentpp);
	return 0;
}

void dht_agent_free(void *p) {
	dht_agent_t *dht_agentp = (dht_agent_t *) p;
	dht_agent_delete(&dht_agentp);
}


int dht_agent_set_hostname(dht_agent_t *agentp, char *hostname) {
	JLG_FREE(&(agentp->hostname));
	agentp->hostname = strdup(hostname);
	return 0;
}

int dht_agent_set_node_id(dht_agent_t *agentp, char *node_id) {
	JLG_FREE(&(agentp->node_id));
	agentp->node_id = strdup(node_id);
	return 0;
}

int dht_agent_cmp(void *agent1p, void *agent2p) {
	// for comparing 2 agents, compare their key (node_id)
	return strcmp(((dht_agent_t *) agent1p)->node_id, ((dht_agent_t *) agent2p)->node_id);
}

size_t dht_agent_to_string(void *p, char *dst, size_t len) {
	dht_agent_t *agentp = (dht_agent_t *) p;
	assert(agentp);
	snprintf(dst, len, "%s:%s:%d:%d", agentp->node_id, agentp->hostname, agentp->tcp_port, agentp->udp_port);
	return 0;
}

void *dht_agent_copy(void *p) {
	dht_agent_t *agentp = (dht_agent_t *) p;
	dht_agent_t *new_agentp = dht_agent_create();
	*new_agentp = *agentp;
	new_agentp->node_id = strdup(agentp->node_id);
	new_agentp->hostname = strdup(agentp->hostname);
	return new_agentp;
}


int dht_agent_ping(dht_agent_t *agentp) {
	int status = 0;
	JLG_DEBUG("pinging agent at %s:%d", agentp->hostname, agentp->tcp_port);
	// sending the command PING
	// expecting the answer PING_OK
	tcp_client_t *tcp_clientp = tcp_client_create();
	tcp_client_set_hostname(tcp_clientp, agentp->hostname);
	tcp_clientp->port = agentp->tcp_port;
	char *request = "PING";
	char response[BUFFER_SIZE] = "";
	JLG_TRY(tcp_client_send(tcp_clientp, request, response, BUFFER_SIZE));
	if (EQUALS(response, "0")) {
		status = 1;
	} else {
		JLG_DEBUG("bad answer. expecting PING_OK, received |%s|", response);
	}
cleanup:
	// any network error must be erased.
	jlg_reset_error_message();
	tcp_client_delete(&tcp_clientp);
	return status;
}


int dht_agent_get_first_available(dht_t *dhtp) {
	int status = 0;
	char **pair = NULL;
	
	int i = 0;
	dht_agent_t *agentp = dht_agent_create();
	for (i = 0; i < 10; i++) {
		char key[BUFFER_SIZE] = "";
		snprintf(key, BUFFER_SIZE, "agent_%d", i);
		char *value = NULL;
		hash_get(dhtp->p->hashp, key, (void **) &value);
		if (!value) {
			continue;
		}
		int length = 0;
		strsplit(&pair, &length, value, ":");
		JLG_CHECK(length != 2, "bad format in properties file");
		
		dht_agent_set_hostname(agentp, pair[0]);
		agentp->tcp_port = JLG_TRY(jlg_strtol(pair[1], NULL, 0));
		strsplit_free(&pair);
		if (dht_agent_ping(agentp)) {
			dhtp->sponsor_agentp = agentp;
			agentp = NULL;
			JLG_DEBUG("*(dhtp->sponsor_agentp) = %s:%d", dhtp->sponsor_agentp->hostname, dhtp->sponsor_agentp->tcp_port);
			status = 1;
			break;
		}
	}

cleanup:
	dht_agent_delete(&agentp);
	strsplit_free(&pair);
	return status;
}

int dht_start_listener(dht_t *dhtp) {
	// if tcp port specified, start a TCP server
	char *tcp_listener_port = NULL;
	hash_get(dhtp->p->hashp, "tcp_listener_port", (void **) &tcp_listener_port);
	if (tcp_listener_port) {
		int port = JLG_TRY(jlg_atoi(tcp_listener_port));
		dhtp->tcp_serverp = tcp_server_create();
		dhtp->tcp_serverp->port = port;
		dhtp->tcp_serverp->max_queue = 5;
		dhtp->tcp_serverp->protocol = tcp_dht_protocol;
		dhtp->tcp_serverp->contextp = dhtp;
		JLG_DEBUG("start the tcp server");
		JLG_TRY(tcp_server_start(dhtp->tcp_serverp));
		
		dhtp->agentp->tcp_port = port;
	}
	char *udp_listener_port = NULL;
	hash_get(dhtp->p->hashp, "udp_listener_port", (void **) &udp_listener_port);
	if (udp_listener_port) {
		int port = JLG_TRY(jlg_atoi(udp_listener_port));
		dhtp->udp_serverp = udp_server_create();
		dhtp->udp_serverp->port = port;
		dhtp->udp_serverp->protocol = udp_dht_protocol;
		dhtp->udp_serverp->contextp = dhtp;
		JLG_DEBUG("start the udp server");
		JLG_TRY(udp_server_start(dhtp->udp_serverp));
		
		dhtp->agentp->udp_port = port;
	}
	
		
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_start(dht_t *dhtp) {
	
	// get the implementation
	dhtp->implementation = DHT_IMPL_DEFAULT;
	char *implementation_buffer = NULL;
	hash_get(dhtp->p->hashp, "implementation", (void **) &implementation_buffer);
	if (EQUALS(implementation_buffer, "CHORD")) {
		dhtp->implementation = DHT_IMPL_CHORD;
	}
	JLG_TRY(dht_init(dhtp));
	dht_agent_set_hostname(dhtp->agentp, "localhost");

	// if a listener is asked to start, then start the dht listener
	char *is_listening = NULL;
	hash_get(dhtp->p->hashp, "is_listening", (void **) &is_listening);
	if (EQUALS(is_listening, "yes")) {
		JLG_DEBUG("starting the listener");
		dht_start_listener(dhtp);
	}
	
	// get the first available agent
	JLG_DEBUG("getting first available agent");
	if (dht_agent_get_first_available(dhtp)) {
		JLG_DEBUG("We found one ! It is %s:%d", dhtp->agentp->hostname, dhtp->agentp->tcp_port);
	} else {
		JLG_DEBUG("No agent found on the network !!!!");
		char *first_agent = NULL;
		hash_get(dhtp->p->hashp, "first_agent", (void **) &first_agent);
		if (NOT_EQUALS(first_agent, "yes")) {
			JLG_THROW_ERROR("Agent is not first agent and cannot see other agent.");
		}
		JLG_DEBUG("I am the first agent of this network");
	}
	JLG_TRY(dht_attach(dhtp));
	
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_stop(dht_t *dhtp) {
	JLG_DEBUG("dht_stop");
	
	// stop the tcp server if there is one
	if (dhtp->tcp_serverp) {
		JLG_TRY(tcp_server_stop(dhtp->tcp_serverp));
	}

	// stop the udp server if there is one
	if (dhtp->udp_serverp) {
		JLG_TRY(udp_server_stop(dhtp->udp_serverp));
	}
	
	JLG_TRY(dht_detach(dhtp, dhtp->agentp));

cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

dht_t *dht_create() {
	JLG_CREATE(p, dht_t);
	p->p = properties_create();
	p->agentp = dht_agent_create();
	p->sponsor_agentp = NULL;
	return p;
}

int dht_delete(dht_t **dhtpp) {
	if (dhtpp) {
		dht_t *dhtp = *dhtpp;
		if (dhtp) {
			properties_delete(&(dhtp->p));
			dht_default_delete((dht_default_t **) &(dhtp->impl));
			JLG_FREE(&(dhtp->sponsor_agentp));
		}
	}
	JLG_FREE(dhtpp);
	return 0;
}

dht_agent_t *dht_get_agent_from_mdkey(dht_t *dhtp, char *mdkey) {
	return dht_default_get_agent_from_mdkey(dhtp, mdkey);
}

int dht_md(dht_t *dhtp, char *mdkey, char *key) {
	return dht_default_md(dhtp, mdkey, key);
}

// set a pair key value. value is duplicated
int dht_set(dht_t *dhtp, char *key, char *valuep) {
	return dht_default_set(dhtp, key, valuep);
}

// get a value without modifying the structure
int dht_get(dht_t *dhtp, char *key, char **valuepp);

int dht_remove(dht_t *dhtp, char *key);

int dht_init(dht_t *dhtp) {
	return dht_default_init(dhtp);
}

// node arrival
int dht_attach(dht_t *dhtp) {
	return dht_default_attach(dhtp);
}

hash_t *dht_get_network(dht_t *dhtp) {
	return ((dht_default_t *) dhtp->impl)->network;
}

// node departure
int dht_detach(dht_t *dhtp, dht_agent_t *agentp) {
	return dht_default_detach(dhtp, agentp);
}

int dht_tcp_request(dht_t *dhtp, dht_agent_t *agentp, char *request, char *response, int length) {
	tcp_client_t *tcp_clientp = tcp_client_create();
	tcp_client_set_hostname(tcp_clientp, agentp->hostname);
	tcp_clientp->port = agentp->tcp_port;
	if (tcp_client_send(tcp_clientp, request, response, length)) {
		jlg_reset_error_message();
		// this agent is off.
		char agent_str[BUFFER_SIZE] = "";
		dht_agent_to_string(agentp, agent_str, BUFFER_SIZE);
		JLG_DEBUG("oops. The agent %s is not reachable. Detach it.", agent_str);
		dht_detach(dhtp, agentp);
	} else {
		JLG_DEBUG("send succeed");
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	tcp_client_delete(&tcp_clientp);
	return JLG_RETURN_CODE;
}

