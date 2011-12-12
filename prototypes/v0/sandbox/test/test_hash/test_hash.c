#include "jlg.h"
#include "jlg_hash.h"

typedef struct _truc_t {
	int nbr;
	char *string;
} truc_t;

truc_t *truc_create() {
	JLG_CREATE(p, truc_t)
	return p;
}

int truc_delete(truc_t **trucpp) {
	if (trucpp && *trucpp) {
		truc_t *trucp = *trucpp;
		JLG_FREE(&(trucp->string));
	}
	JLG_FREE(trucpp);
	return 0;
}

void truc_free(void *p) {
	truc_t *trucp = (truc_t *) p;
	truc_delete(&trucp);
}

int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");

	hash_config_t *cfgp = hash_config_create();
	cfgp->free_func = truc_free;
	hash_t *hashp = hash_create_ex(cfgp);
	
	int i = 0;
	for (i = 0; i < 10; i++) {
		truc_t *tp = truc_create();
		tp->nbr = i;
		char buffer[BUFFER_SIZE] = "";
		snprintf(buffer, BUFFER_SIZE, "my nbr is %d", i);
		tp->string = strdup(buffer);
		snprintf(buffer, BUFFER_SIZE, "truc%d", i);
		hash_put(hashp, buffer, tp);
	}

	truc_t *tp = truc_create();
	tp->nbr = 3;
	tp->string = strdup("kiki");
	hash_put(hashp, "truc2", tp);
	hash_remove(hashp, "truc1");
	
	dll_node_t *nodep = hashp->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		truc_t *tp = (truc_t *) pairp->value;
		JLG_DEBUG("%s->%s", pairp->key, (char *) tp->string);
		nodep = nodep->nextp;
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	hash_delete(&hashp);
	return JLG_RETURN_CODE;
}
