#include "jlg.h"
#include "jlg_dht.h"
#include "jlg_hash.h"
#include "jlg_dht_default.h"
#include "dht_client.h"

int main(int argc, char **argv) {
	jlg_init();
	
	JLG_LOG("starting");
	JLG_DEBUG_ON();
	dht_t *dhtp = dht_create();
	properties_set_filename(dhtp->p, "./dht.properties");
	properties_reload(dhtp->p);

	
	JLG_DEBUG("starting dht");
	JLG_TRY(dht_start(dhtp));
	
	// demarrer un client interactif permettant:
	// imprimer statut reseau
	// stocker une cle/valeur
	// retrouver une cle/valeur
	// trouver le node responsable d'une cle
	// quitter l'appli en fermant l'agent
	dht_client_t *clientp = dht_client_create(dhtp);
	dht_client_start(clientp);
	
	
	dll_node_t *nodep = dhtp->p->hashp->dlistp->nodep;
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		JLG_DEBUG("%s->%s", pairp->key, (char *) pairp->value);
		nodep = nodep->nextp;
	}

	dht_stop(dhtp);
	
	int ret = pthread_join(dhtp->tcp_serverp->server_thread, NULL);
	JLG_CHECK(ret, "Error while using pthread_join. Error code returned: %d\n", ret);
	
	dht_delete(&dhtp);
cleanup:
	return JLG_RETURN_CODE;
}