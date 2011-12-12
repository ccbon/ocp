/*
	Distributed Hash Table
	
	Default implementation
	
*/
#ifndef _JLG_DHT_DEFAULT_H_
#define _JLG_DHT_DEFAULT_H_
#include "jlg_dht.h"

typedef struct _dht_default_t {
	int key_length; // all node_id are in a space of length 2^key_length
	char md_algo_name[8]; // sha1, md5, etc. (see openssl)
	hash_t *network; // hash node_id->agent with all agent in the network
	dht_agent_t *successor;
	dht_agent_t *predecessor;
	hash_t *hashp; // a hashtable key->value with key = dht_md(key) and value = pair(key, value)
} dht_default_t;

dht_default_t *dht_default_create();
int dht_default_delete(dht_default_t **dht_defaultpp);

int dht_default_init(dht_t *dhtp);

// node arrival
int dht_default_attach(dht_t *dhtp);

// node departure
int dht_default_detach(dht_t *dhtp, dht_agent_t *detach_agentp);

// get all info on the network
int dht_default_set_network(dht_t *dhtp);

// add node_id to all the network member and the new agent itself
int dht_default_add_node_id(dht_t *dhtp);

// TCP server protocol part
void dht_default_tcp_protocol(void *p, int client_socket);

// get/set a pair key->value
int dht_default_set(dht_t *dhtp, char *key, char *valuep);
int dht_default_get(dht_t *dhtp, char *key, char **valuepp);

int dht_default_md(dht_t *dhtp, char *mdkey, char *key);

dht_agent_t *dht_default_get_agent_from_mdkey(dht_t *dhtp, char *mdkey);


#endif // _JLG_DHT_DEFAULT_H_
