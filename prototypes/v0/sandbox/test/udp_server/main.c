#include "jlg.h"
#include "jlg_udp_server.h"

#include <stdlib.h>
#include <unistd.h> // for closing socket and parsing options

void echo_protocol(void *p, char *incoming_message, char *client_hostname, int client_port) {
		
	JLG_DEBUG("Received from %s:%d |%s|", client_hostname, client_port, incoming_message);
//cleanup:
	return;
}

int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	if (argc != 2) {
		printf("Syntax: udp_server <port>\n");
		exit(1);
	}
	
	udp_server_t *udp_serverp = udp_server_create();
	udp_serverp->port = JLG_TRY(jlg_atoi(argv[1]));
	udp_serverp->protocol = echo_protocol;
	udp_server_start(udp_serverp);
	
	// wait
	int ret = pthread_join(udp_serverp->server_thread, NULL);
	JLG_CHECK(ret, "Error while using pthread_join. Error code returned: %d\n", ret);
	
cleanup:
	udp_server_delete(&udp_serverp);
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}
