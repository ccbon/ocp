#include "jlg.h"
#include "jlg_btree.h"
#include "jlg_dht.h"

int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");

	char buffer[BUFFER_SIZE]= "";
	
	btree_config_t *cfg = btree_config_create();
	cfg->cmp_func = dht_agent_cmp;
	cfg->free_func = dht_agent_free;
	cfg->copy_func = dht_agent_copy;
	cfg->str_func = dht_agent_to_string;
	
	btree_t *btreep = btree_create_ex(cfg);
	dht_agent_t *agentp = dht_agent_create();
	dht_agent_set_hostname(agentp, "localhost");
	dht_agent_set_node_id(agentp, "1234");
	btree_put(btreep, agentp);

	agentp = dht_agent_create();
	dht_agent_set_hostname(agentp, "localhost");
	dht_agent_set_node_id(agentp, "2345");
	btree_put(btreep, agentp);
	
	agentp = dht_agent_create();
	dht_agent_set_hostname(agentp, "localhost");
	dht_agent_set_node_id(agentp, "0123");
	btree_put(btreep, agentp);
	
	btree_to_string(btreep, buffer, BUFFER_SIZE);
	JLG_DEBUG(buffer);

	linked_list_t *listp = NULL;
	btree_ordered_list(btreep, &listp);
	ll_node_t *nodep = listp->nodep;
	while (nodep) {
		char agent_buffer[BUFFER_SIZE] = "";
		dht_agent_to_string(nodep->valuep, agent_buffer, BUFFER_SIZE);
		JLG_DEBUG("agent %s", agent_buffer);
		nodep = nodep->nextp;
	}
	ll_delete(&listp);

	btree_remove(btreep, agentp);
	btree_to_string(btreep, buffer, BUFFER_SIZE);
	JLG_DEBUG(buffer);


	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	btree_delete(&btreep);
	return JLG_RETURN_CODE;
}
