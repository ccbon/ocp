#include "jlg.h"
#include "jlg_tcp_client.h"

int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	if (argc != 4) {
		printf("Syntax: tcp_client <hostname> <port> <message>\n");
		exit(1);
	}
	
	tcp_client_t *tcp_clientp = tcp_client_create();
	tcp_client_set_hostname(tcp_clientp, argv[1]);
	JLG_DEBUG("argv[2] = %s", argv[2]);
	tcp_clientp->port = JLG_TRY(jlg_atoi(argv[2]));
	char *request = argv[3];
	char response[BUFFER_SIZE] = "";
	JLG_TRY(tcp_client_send(tcp_clientp, request, response, BUFFER_SIZE));
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}
