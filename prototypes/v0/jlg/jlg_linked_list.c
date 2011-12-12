#include "jlg_linked_list.h"
#include "jlg.h"
#include <stdlib.h>

linked_list_t *ll_create() {
	JLG_CREATE(p, linked_list_t);
	return p;
}

int ll_delete(linked_list_t **listpp) {
	if (listpp && *listpp) {
		linked_list_t *listp = *listpp;
		while (listp->first) {
			if (listp->free_func) {
				listp->free_func(&(listp->first->valuep));
			}
			ll_node_t *nodep = listp->first->nextp;
			free(listp->first);
			listp->first = nodep;
		}
	}
	JLG_FREE(listpp);
	return 0;
}

int ll_push(linked_list_t *listp, void *valuep) {
	if (!listp) {
		return 1;
	}
	ll_node_t *nodep = (ll_node_t *) malloc(sizeof(ll_node_t));
	nodep->nextp = listp->first;
	nodep->valuep = valuep;
	listp->first = nodep;
	return 0;
}

int ll_pop(linked_list_t *listp, void **valuepp) {
	if (!valuepp) {
		return 1;
	}
	if (!listp) {
		return 1;
	}
	ll_node_t *nodep = listp->first;
	if (!nodep) {
		// empty list
		return EOLL;
	}
	
	*valuepp = nodep->valuep;
	listp->first = nodep->nextp;
	free(nodep);
	return 0;
}

