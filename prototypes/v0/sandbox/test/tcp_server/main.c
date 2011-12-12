#include "jlg.h"
#include "jlg_tcp_server.h"

#include <stdlib.h>
#include <unistd.h> // for closing socket and parsing options

void echo_protocol(void *p, int socket) {
	char buffer[BUFFER_SIZE] = "";	
	while (true) {
		int size = BUFFER_SIZE;
		int qty_read = 0;
		JLG_DEBUG("about to read the client socket content");
		JLG_CHECK((qty_read = read(socket, buffer, size - 1)) < 0, "Error while reading the socket content.\n");
		JLG_DEBUG("client socket content read");
		buffer[BUFFER_SIZE - 1] = '\0';
		printf("Socket content: %s\n", buffer);
		if (qty_read != size - 1) {
			// we suppose it's because the end of stream
			break;
		}
	}
	
	// write on the socket file descriptor an answer.
	char answer[BUFFER_SIZE];
	snprintf(answer, BUFFER_SIZE, "Message received! (%s)", buffer);
	int ret = 0;
	ret = write(socket, answer, strlen(answer));
	JLG_CHECK(ret < 0, "Error while answering");
	sleep(3);
	ret = write(socket, answer, strlen(answer));
	JLG_CHECK(ret < 0, "Error while answering");
		
	JLG_DEBUG("thread server id: %d END", socket);
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return;
}

int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	if (argc != 2) {
		printf("Syntax: tcp_server <port>\n");
		exit(1);
	}
	
	tcp_server_t tcp_server;
	tcp_server_create(&tcp_server);
	tcp_server.port = JLG_TRY(jlg_atoi(argv[1]));
	tcp_server.max_queue = 5;
	tcp_server.protocol = echo_protocol;
	tcp_server_start(&tcp_server);
	
	// wait
	int ret = pthread_join(tcp_server.server_thread, NULL);
	JLG_CHECK(ret, "Error while using pthread_join. Error code returned: %d\n", ret);
	
cleanup:
	return JLG_RETURN_CODE;	
}
