#include "dht_client.h"
#include "jlg_string.h"

dht_client_t *dht_client_create(dht_t *dhtp) {
	JLG_CREATE(p, dht_client_t);
	p->dhtp = dhtp;
	return p;
}

int dht_client_delete(dht_client_t **dht_clientpp) {
	JLG_FREE(dht_clientpp);
	return 0;
}

int dht_client_parse_command(dht_client_t *dht_clientp, char *command) {
	JLG_DEBUG("parsing command = %s", command);
	if (EQUALS(command, "help") || EQUALS(command, "h")) {
		printf(
		"Commands are:\n"
		"help, h : print help\n"
		"quit, q : quit\n"
		"network, n : show network agents\n"
		"\n");
	} else if (EQUALS(command, "network") || EQUALS(command, "n")) {
		hash_t *hashp = dht_get_network(dht_clientp->dhtp);
		dll_node_t *nodep = hashp->dlistp->nodep;
		while (nodep) {
			hash_pair_t *pairp = nodep->valuep;
			char buffer[BUFFER_SIZE] = "";
			dht_agent_to_string((dht_agent_t *) pairp->value, buffer, BUFFER_SIZE);
			printf("%s->%s\n", pairp->key, buffer);
			nodep = nodep->nextp;
		}
	} else {
		fprintf(stderr, "-dht_client: bad command: %s\n", command);
	}
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int dht_client_start(dht_client_t *dht_clientp) {

	// give a prompt to the user
	while (true) {
		char command[BUFFER_SIZE] = "";
		char prompt[BUFFER_SIZE] = "$>";
		printf("%s", prompt);
		fgets(command, BUFFER_SIZE, stdin);
		chomp(command);
		JLG_DEBUG("command = %s", command);
		if (EQUALS(command, "quit") || EQUALS(command, "q")) {
			break;
		}
		dht_client_parse_command(dht_clientp, command);
	}
	
	
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

