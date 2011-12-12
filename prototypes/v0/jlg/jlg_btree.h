#ifndef _BTREE_H_
#define _BTREE_H_

#include "jlg_linked_list.h"

#define BTREE_ERR_NOT_FOUND -2
#define BTREE_ERR_EMPTY     -3

typedef struct _btree_config_t {
	int (*cmp_func)(void *, void *);
	void (*free_func)(void *);
	void * (*copy_func)(void *);
	size_t (*str_func)(void *, char *, size_t);
} btree_config_t;

typedef struct _btree_node_t {
	struct _btree_node_t *left;
	struct _btree_node_t *right;
	void *value;
} btree_node_t;

typedef struct _btree_t {
	btree_config_t *cfg;
	btree_node_t *root;
	linked_list_t *listp;
} btree_t;

btree_config_t *btree_config_create();
int btree_config_delete(btree_config_t **cfgpp);

btree_node_t *btree_node_create(void *value, bool copy, btree_config_t *cfg);
int btree_node_delete(btree_node_t **nodepp, btree_config_t *cfg) ;


btree_t *btree_create();
btree_t *btree_create_ex(btree_config_t *cfg);
int btree_delete(btree_t **btreepp);
void btree_free(void *btreep);

int btree_set(btree_t *btreep, void *value);
int btree_put(btree_t *btreep, void *value);

int btree_get(btree_t *btreep, void *value_key, void **valuepp);

int btree_get_next(btree_t *btreep, void *value_key, void **valuepp);
int btree_get_previous(btree_t *btreep, void *value_key, void **valuepp);

bool btree_contains(btree_t *btreep, void *value_key);

int btree_remove(btree_t *btreep, void *value_key);

int btree_to_string(btree_t *btreep, char *dst, size_t size);

linked_list_t *btree_ordered_list(btree_t *btreep);

#endif // _BTREE_H_
