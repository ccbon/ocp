#include "jlg_double_linked_list.h"
#include "jlg.h"
#include <stdlib.h>

double_linked_list_t *dll_create() {
	JLG_CREATE(p, double_linked_list_t);
	return p;
}


int dll_delete(double_linked_list_t **listpp) {
	if (listpp && *listpp) {
		double_linked_list_t *listp = *listpp;
		while (listp->first) {
			if (listp->free_func) {
				listp->free_func(listp->first->valuep);
			}
			dll_node_t *nodep = nodep->nextp;
			free(listp->first);
			listp->first = nodep;
		}
	}
	JLG_FREE(listpp);
	return 0;
}


int dll_push(double_linked_list_t *listp, void *valuep) {
	if (!listp) {
		return 1;
	}
	dll_node_t *nodep = (dll_node_t *) malloc(sizeof(dll_node_t));
	nodep->valuep = valuep;
	nodep->previousp = NULL;
	if (!listp->first) {
		nodep->nextp = NULL;
	} else {
		nodep->nextp = listp->first;
		nodep->nextp->previousp = nodep;
	}
	listp->first = nodep;
	return 0;
}

int dll_pop(double_linked_list_t *listp, void **valuepp) {
	if (!listp) {
		return 1;
	}
	if (!valuepp) {
		return 1;
	}
	dll_node_t *nodep = listp->first;
	if (!nodep) {
		// empty list
		return EODLL;
	}
	
	*valuepp = nodep->valuep;
	listp->first = nodep->nextp;
	if (listp->first) {
		listp->first->previousp = NULL;
	}
	free(nodep);
	return 0;
}

// remove a node from a dll
int dll_remove(double_linked_list_t *listp, dll_node_t *nodep) {
	if (!listp) {
		return 1;
	}
	if (!nodep) {
		return 1;
	}
	if (listp->first == nodep) {
		listp->first = nodep->nextp;
		return 0;
	}
	nodep->previousp->nextp = nodep->nextp;
	if (nodep->nextp) {
		nodep->nextp->previousp = nodep->previousp;
	}
	return 0;
}

