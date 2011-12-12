#include "jlg.h"
#include "jlg_double_linked_list.h"

int main4(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	double_linked_list_t list;
	
	dll_create(&list);
	dll_push(&list, "coucou");
	dll_node_t *coucou_nodep = list.nodep;
	dll_push(&list, "hello");
	dll_push(&list, "salut");

	dll_node_t *nodep = list.nodep;
	while (nodep) {
		JLG_DEBUG("value = %s", (char *) nodep->valuep);
		nodep = nodep->nextp;
	}
	
	dll_remove(&list, coucou_nodep);
	
	void *buffer = NULL;
	while (dll_pop(&list, &buffer) != EODLL) {
		JLG_DEBUG("item = %s", (char *) buffer);
	}

//cleanup:
	return JLG_RETURN_CODE;
}


