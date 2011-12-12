#ifndef _JLG_LINKED_LIST_H_
#define _JLG_LINKED_LIST_H_

#include "jlg.h"

typedef struct _linked_list_node_t {
	void *valuep;
	struct _linked_list_node_t *nextp;
} ll_node_t;

// wrapping is necessary to have a fix reference for the beginning of the linked list
typedef struct _linked_list_t {
	ll_node_t *first;
	void (*free_func)(void **);
} linked_list_t;

// end of linked list
#define EOLL -1

linked_list_t *ll_create();
int ll_delete(linked_list_t **listp);

int ll_push(linked_list_t *listp, void *valuep);
int ll_pop(linked_list_t *listp, void **valuepp);

#endif // _JLG_LINKED_LIST_H_