/*
	Distributed Hash Table
	
	To each given key is mapped a hash value.
	Depending on the hash value is the node
	
*/
#ifndef _JLG_DHT_H_
#define _JLG_DHT_H_
#include "jlg.h"
#include "jlg_properties.h"
#include "jlg_tcp_server.h"
#include "jlg_udp_server.h"

#define DHT_IMPL_DEFAULT  0 // default (jlg custom)
#define DHT_IMPL_CHORD    1

typedef struct _dht_agent_t {
	char *hostname;
	int tcp_port;
	int udp_port;
	char *node_id; // node_id of the agent (define the agent identity and area of responsability)
} dht_agent_t;

dht_agent_t *dht_agent_create();
int dht_agent_delete(dht_agent_t **dht_agentpp);
void dht_agent_free(void *p);
size_t dht_agent_to_string(void *agentp, char *dst, size_t len);
void *dht_agent_copy(void *agentp);

int dht_agent_set_hostname(dht_agent_t *agentp, char *hostname);
int dht_agent_set_node_id(dht_agent_t *agentp, char *node_id);

int dht_agent_cmp(void *agent1p, void *agent2p);


typedef struct _dht_t {
	properties_t *p;
	tcp_server_t *tcp_serverp;
	udp_server_t *udp_serverp;
	int implementation;
	void *impl;
	dht_agent_t *agentp; // myself
	dht_agent_t *sponsor_agentp; // my sponsor
} dht_t;

dht_t *dht_create();
int dht_delete(dht_t **dhtpp);

int dht_start(dht_t *dhtp);
int dht_stop(dht_t *dhtp);


hash_t *dht_get_network(dht_t *dhtp);

dht_agent_t *dht_get_agent_from_mdkey(dht_t *dhtp, char *mdkey);

int dht_md(dht_t *dhtp, char *mdkey, char *key);

// set a pair key value. value is duplicated
int dht_set(dht_t *dhtp, char *key, char *valuep);
// get a value without modifying the structure
int dht_get(dht_t *dhtp, char *key, char **valuepp);

int dht_remove(dht_t *dhtp, char *key);

// TCP Server useful functions
void dht_write_message(int client_socket, char *output_message);
void dht_read_message(int client_socket, char *input_message);

// TCP Client useful functions
int dht_tcp_request(dht_t *dhtp, dht_agent_t *agentp, char *request, char *response, int length);

// IMPLEMENTATION DEPENDANT

// init
int dht_init(dht_t *dhtp);

// node arrival
int dht_attach(dht_t *dhtp);

// node departure
int dht_detach(dht_t *dhtp, dht_agent_t *agentp);

#endif // _JLG_DHT_H_