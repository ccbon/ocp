/*
	Distributed Hash Table
	
	Default implementation
	
*/
#ifndef _JLG_DHT_DEFAULT_H_
#define _JLG_DHT_DEFAULT_H_
#include "jlg_dht.h"
#include "jlg_btree.h"

typedef struct _dht_default_t {
	int key_length; // all node_id are in a space of length 2^key_length
	char md_algo_name[8]; // sha1, md5, etc. (see openssl)
	int backup_nbr; // nbr of backup for a pair
	btree_t *network; // btree with all agent in the network
	hash_t *hashp; // a hashtable key->value with backup
	hash_t *meta_hashp; // a hashtable storing meta info (ex: key list for node_id)
} dht_default_t;

dht_default_t *dht_default_create();
int dht_default_delete(dht_default_t **dht_defaultpp);

int dht_default_init(dht_t *dhtp);

// node arrival
int dht_default_attach(dht_t *dhtp);
int dht_default_local_filter(dht_t *dhtp);

// node departure
int dht_default_nice_detach(dht_t *dhtp);
int dht_default_save_data(dht_t *dhtp);
dht_agent_t *dht_default_get_predecessor_agent(dht_t *dhtp);
int dht_default_detach(dht_t *dhtp, dht_agent_t *detach_agentp);
int dht_default_restore_data(dht_t *dhtp, char *detach_node_id);


// get all info on the network
int dht_default_set_network(dht_t *dhtp);

// add node_id to all the network member and the new agent itself
int dht_default_add_node_id(dht_t *dhtp);

// TCP server protocol part
void dht_default_tcp_protocol(void *p, int client_socket);

// get/set a pair key->value
int dht_default_backup_set(dht_t *dhtp, char *key, char *value);
int dht_default_backup_get(dht_t *dhtp, char *key, char **valuepp);
int dht_default_backup_remove(dht_t *dhtp, char *key);

int dht_default_local_set(dht_t *dhtp, char *key, char *value);
int dht_default_local_get(dht_t *dhtp, char *key, char **valuepp);
int dht_default_local_remove(dht_t *dhtp, char *key);

int dht_default_set(dht_t *dhtp, char *key, char *value);
int dht_default_get(dht_t *dhtp, char *key, char **valuepp);
int dht_default_remove(dht_t *dhtp, char *key);

char *make_bkp_key(char *key, int i);
char *make_orig_key(char *key);

// update meta_hashp
int dht_default_mset(dht_t *dhtp, dht_agent_t *agentp, char *key);
int dht_default_local_mset(dht_t *dhtp, char *bkp_node_id, char *key);

int dht_default_mremove(dht_t *dhtp, dht_agent_t *agentp, char *key);
int dht_default_local_mremove(dht_t *dhtp, char *bkp_node_id, char *key);

int dht_default_mget(dht_t *dhtp, char *node_id, linked_list_t **listpp);
int dht_default_local_mget(dht_t *dhtp, char *bkp_node_id, btree_t **btreepp);

int dht_default_mdelete(dht_t *dhtp, char *detach_node_id);
int dht_default_local_mdelete(dht_t *dhtp, char *bkp_node_id);

int dht_default_md(dht_t *dhtp, char *mdkey, char *key);

dht_agent_t *dht_default_get_agent_from_key(dht_t *dhtp, char *key);
dht_agent_t *dht_default_get_agent_from_mdkey(dht_t *dhtp, char *mdkey);



// info debugging
hash_t *dht_default_get_local_content(dht_t *dhtp);

#endif // _JLG_DHT_DEFAULT_H_
