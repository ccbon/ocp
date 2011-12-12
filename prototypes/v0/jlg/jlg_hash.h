/* 
	hash_t - Hash table
	
	Structure choices :
	- Key and value are owned by the hash. It's because most of the time, this will be
	  the hashtable which only own pointer on values and keys. Thus :
		* they are freed when freeing the hash table
		* when using the hash_set function, key and value are duplicated. if replacement old value are freed.
		* optimize by using hash_put if you want to pass a value that will be not duplicated. But be careful, if you
		  destroy the hash table, the value will be destroyed as well
		* you can specify the free function for the value in the hash config. By default it is 'free'
*/
#ifndef _JLG_HASH_H_
#define _JLG_HASH_H_
#include "jlg_linked_list.h"
#include "jlg_double_linked_list.h"
#define HASH_DEFAULT_KEY_SIZE 8
#define HASH_ERR_NOT_FOUND 2

typedef struct _hash_pair_t {
	char *key;
	void *value;
	dll_node_t *nodep;
} hash_pair_t;

typedef struct _hash_config_t {
	long long key_size;
	char *open_ssl_algo_name;
	void (*free_func)(void *);
	void * (*copy_func)(void *);
} hash_config_t;

typedef struct _hash_t {
	hash_config_t *cfg;
	linked_list_t **array;
	double_linked_list_t *dlistp;
} hash_t;

// create a hash table with key size optionally specified
// this function allocates memory so hash_delete has to be called to free memory
hash_config_t *hash_config_create();
int hash_config_delete(hash_config_t **cfgpp);

hash_t *hash_create();
hash_t *hash_create_ex(hash_config_t *cfg);
int hash_delete(hash_t **hashp);

// set a pair key value. value is duplicated
int hash_set(hash_t *hashp, char *key, void *valuep);
// put a pair key value. value is not duplicated
int hash_put(hash_t *hashp, char *key, void *valuep);

// get a value without modifying the structure
int hash_get(hash_t *hashp, char *key, void **valuepp);

int hash_remove(hash_t *hashp, char *key);

// get a list of keys
// by going through the double linked list


#endif // _JLG_HASH_H_