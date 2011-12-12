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
		while (listp->nodep) {
			if (listp->free_func) {
				listp->free_func(&(listp->nodep->valuep));
			}
			ll_node_t *nodep = listp->nodep->nextp;
			free(listp->nodep);
			listp->nodep = nodep;
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
	nodep->nextp = listp->nodep;
	nodep->valuep = valuep;
	listp->nodep = nodep;
	return 0;
}

int ll_pop(linked_list_t *listp, void **valuepp) {
	if (!valuepp) {
		return 1;
	}
	if (!listp) {
		return 1;
	}
	ll_node_t *nodep = listp->nodep;
	if (!nodep) {
		// empty list
		return EOLL;
	}
	
	*valuepp = nodep->valuep;
	listp->nodep = nodep->nextp;
	free(nodep);
	return 0;
}

