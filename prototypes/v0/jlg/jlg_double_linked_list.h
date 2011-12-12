#ifndef _JLG_DOUBLE_LINKED_LIST_H_
#define _JLG_DOUBLE_LINKED_LIST_H_
typedef struct _double_linked_list_node_t {
	void *valuep;
	struct _double_linked_list_node_t *nextp;
	struct _double_linked_list_node_t *previousp;
} dll_node_t;

typedef struct _double_linked_list_t {
	dll_node_t *first;
	void (*free_func)(void *);
} double_linked_list_t;

#define EODLL -1

double_linked_list_t *dll_create();
int dll_delete(double_linked_list_t **listpp);

int dll_push(double_linked_list_t *listp, void *valuep);
int dll_pop(double_linked_list_t *listp, void **valuepp);
int dll_remove(double_linked_list_t *listp, dll_node_t *nodep);

#endif // _JLG_DOUBLE_LINKED_LIST_H_