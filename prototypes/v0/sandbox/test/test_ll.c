#include "jlg.h"
#include "jlg_linked_list.h"

int main2(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	linked_list_t list;
	
	ll_create(&list);
	ll_push(&list, "coucou");
	ll_push(&list, "hello");

	ll_node_t *nodep = list.nodep;
	while (nodep) {
		JLG_DEBUG("value = %s", (char *) nodep->valuep);
		nodep = nodep->nextp;
	}
	
	void *buffer = NULL;
	while (ll_pop(&list, &buffer) != EOLL) {
		JLG_DEBUG("item = %s", (char *) buffer);
	}

//cleanup:
	return JLG_RETURN_CODE;
}


