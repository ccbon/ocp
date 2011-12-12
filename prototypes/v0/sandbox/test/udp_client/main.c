#include "jlg_udp_client.h"

int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	if (argc != 4) {
		printf("Syntax: udp_client <hostname> <port> <message>\n");
		exit(1);
	}
	
	udp_client_t *udp_clientp = udp_client_create();
	udp_client_set_hostname(udp_clientp, argv[1]);
	JLG_DEBUG("argv[2] = %s", argv[2]);
	udp_clientp->port = JLG_TRY(jlg_atoi(argv[2]));
	char *request = argv[3];
	udp_client_send(udp_clientp, request);
	
cleanup:
	udp_client_delete(&udp_clientp);
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}
