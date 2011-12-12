#include "jlg_hash.h"
#include "jlg_md.h"
#include "jlg_linked_list.h"
#include <openssl/evp.h>
#include <string.h>
// hashtable implementation

// hash table

typedef unsigned long long hash_key_t;

static void *objdup(void *srcp) {
	size_t s = strlen((char *) srcp) + 1;
	void *p = (void *) malloc(s);
	memmove(p, srcp, s);
	return p;
}

static hash_key_t hash_compute_md(hash_t *hashp, const char *source) {
	char dst[BUFFER_SIZE] = "";
	jlg_md_str(source, dst, BUFFER_SIZE, hashp->cfg->open_ssl_algo_name);
	
	hash_key_t result = 0;
	int i = 0;
	for (i = 0; i < strlen(dst); i++) {
		result <<= 8;
		result += dst[i];
	}
	result %= hashp->cfg->key_size;
	return result;
}

hash_config_t *hash_config_create() {
	JLG_CREATE(p, hash_config_t);
	p->key_size = HASH_DEFAULT_KEY_SIZE;
	p->open_ssl_algo_name = "sha1";
	p->free_func = free;
	p->copy_func = objdup;
	return p;
}

// create a hash table with key size optionally specified
// this function allocates memory so hash_delete has to be called to free memory
hash_t *hash_create() {
	return hash_create_ex(hash_config_create());
}

int hash_config_delete(hash_config_t **cfgpp) {
	JLG_FREE(cfgpp);
	return 0;
}

hash_t *hash_create_ex(hash_config_t *cfg) {
	JLG_CREATE(hashp, hash_t);
	hashp->cfg = cfg;
	// allocate the array and setup all pointers to NULL
	hashp->array = (linked_list_t **) malloc(sizeof(linked_list_t *) * hashp->cfg->key_size);
	memset(hashp->array, 0, sizeof(linked_list_t *) * hashp->cfg->key_size);
	
	// dlistp (for hash_keys)
	hashp->dlistp = dll_create();
	return hashp;
}


int hash_delete(hash_t **hashpp) {
	if (hashpp && *hashpp) {
		hash_t *hashp = *hashpp;
		
		// go through the double linked list and remove all
		double_linked_list_t *listp = hashp->dlistp;
		while (listp->nodep) {
			// remove from pair both key and value (and the pair itself)
			hash_pair_t *pairp = (hash_pair_t *) listp->nodep->valuep;
			free(pairp->key);
			hashp->cfg->free_func(pairp->value);
			free(pairp);
			
			// remove the dll_node
			dll_node_t *nodep = listp->nodep->nextp;
			free(listp->nodep);
			listp->nodep = nodep;
		}
		JLG_FREE(&(hashp->dlistp));
		// go through the array and remove any linked list (without the values which are already freed)
		int i = 0;
		for (i = 0; i < hashp->cfg->key_size; i++) {
			if (hashp->array[i]) {
				ll_delete(&(hashp->array[i]));
			}
		}
		JLG_FREE(&(hashp->array));
		
		// remove the config if special
		hash_config_delete(&(hashp->cfg));
	}
	JLG_FREE(hashpp);
	return 0;
}

// set a pair key value.
static int hash_add(hash_t *hashp, char *key, void *valuep, bool copy) {
	hash_key_t md = hash_compute_md(hashp, key);
	linked_list_t *listp = NULL;
	if (hashp->array[md] == NULL) {
		listp = ll_create();
		hashp->array[md] = listp;
	} else {
		listp = hashp->array[md];
		ll_node_t *nodep = listp->nodep;
		while (nodep) {
			hash_pair_t *pairp = (hash_pair_t *) nodep->valuep;
			if (EQUALS(key, pairp->key)) {
				hashp->cfg->free_func(pairp->value);
				if (copy) {
					pairp->value = hashp->cfg->copy_func(valuep);
				} else {
					pairp->value = valuep;
				}
				return 0;
			}
			nodep = nodep->nextp;
		}
	}
	
	hash_pair_t *pairp = (hash_pair_t *) malloc(sizeof(hash_pair_t));
	pairp->key = strdup(key);
	if (copy) {
		pairp->value = hashp->cfg->copy_func(valuep);
	} else {
		pairp->value = valuep;
	}
	ll_push(listp, pairp);
	
	dll_push(hashp->dlistp, pairp);
	pairp->nodep = hashp->dlistp->nodep;

	return 0;
}

int hash_set(hash_t *hashp, char *key, void *valuep) {
	return hash_add(hashp, key, valuep, true);
}

int hash_put(hash_t *hashp, char *key, void *valuep) {
	return hash_add(hashp, key, valuep, false);
}

// get a value without modifying the structure
int hash_get(hash_t *hashp, char *key, void **valuepp) {
	if (!valuepp) {
		return -1;
	}
	*valuepp = NULL;
	hash_key_t md = hash_compute_md(hashp, key);
	if (hashp->array[md] == NULL) {
		return HASH_ERR_NOT_FOUND;
	}
	ll_node_t *nodep = hashp->array[md]->nodep;
	while (nodep) {
		hash_pair_t *pairp = (hash_pair_t *) nodep->valuep;
		if (EQUALS(key, pairp->key)) {
			*valuepp = pairp->value;
			return 0;
		}
		nodep = nodep->nextp;
	}
	return HASH_ERR_NOT_FOUND;
}

int hash_remove(hash_t *hashp, char *key) {
	hash_key_t md = hash_compute_md(hashp, key);
	if (hashp->array[md] == NULL) {
		return HASH_ERR_NOT_FOUND;
	}
	ll_node_t *nodep = hashp->array[md]->nodep;
	ll_node_t *previous_nodep = NULL;
	while (nodep) {
		hash_pair_t *pairp = (hash_pair_t *) nodep->valuep;
		if (EQUALS(key, pairp->key)) {
			hashp->cfg->free_func(pairp->value);
			free(pairp->key);
			// remove this node from the linked list
			if (previous_nodep) {
				previous_nodep->nextp = nodep->nextp;
			} else {
				hashp->array[md]->nodep = nodep->nextp;
			}
			// remove this node from the double linked list as well
			dll_remove(hashp->dlistp, pairp->nodep);
			JLG_FREE(&(pairp->nodep));
			JLG_FREE(&pairp);
			
			return 0;
		}
		previous_nodep = nodep;
		nodep = nodep->nextp;
	}
	return HASH_ERR_NOT_FOUND;
}

