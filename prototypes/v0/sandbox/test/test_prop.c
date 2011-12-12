#include "jlg.h"
#include "jlg_properties.h"
#include "jlg_string.h"

int main10(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	properties_t prop;
	properties_create(&prop);
	prop.name = "jlg";
	prop.path = ".";

	properties_reload(&prop);
	dll_node_t *nodep = prop.hashp->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		JLG_DEBUG("%s->%s", pairp->key, (char *) pairp->value);
		nodep = nodep->nextp;
	}
	
	prop.name = "jlg2";
	properties_reload(&prop);
	nodep = prop.hashp->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		JLG_DEBUG("%s->%s", pairp->key, (char *) pairp->value);
		nodep = nodep->nextp;
	}

//cleanup:
	return JLG_RETURN_CODE;
}

int main11(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	char *buffer = "coucou--kiki-gege";
	char **array = NULL;
	int length = 0;
	strsplit(&array, &length, buffer, "--");
	int i = 0;
	for (i = 0; i < length; i++) {
		JLG_DEBUG("array[%d] = %s", i, array[i]);
	}
	strsplit_free(&array);
//cleanup:
	return JLG_RETURN_CODE;
}

int main12(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("testing errno - try to open a not existing file");
	JLG_CHECK(1, "oops ...");
	FILE *fd = NULL;
	JLG_CHECK((fd = fopen("whatever.txt", "r")) == NULL, "Cannot open whatever.txt");
cleanup:
	return JLG_RETURN_CODE;
}
