#include "jlg.h"

// Misc
#include <stdlib.h>
#include <string.h> // for calling memset

// Network
#include <sys/socket.h> // for socket(), ...
#include <netinet/in.h> // for sockaddr_in
#include <unistd.h> // for closing socket

// Threading
#include <pthread.h>

#define PORT 22222
#define LISTEN_QUEUE_SIZE 5


void *thread_server(void *p) {	
	int my_socket;
	my_socket = (int) p;
	JLG_DEBUG("thread server id: %d", my_socket);
	
	while (TRUE) {
		sleep(10);
		int size = 16;
		int qty_read = 0;
		char buffer[16] = "";
		JLG_DEBUG("about to read the client socket content");
		JLG_CHECK((qty_read = read(my_socket, buffer, size - 1)) < 0, "Error while reading the socket content.\n");
		JLG_DEBUG("client socket content read");
		buffer[15] = '\0';
		printf("Socket content: %s\n", buffer);
		if (qty_read != size - 1) {
			// we suppose it's because the end of stream
			break;
		}
	}
	
	// write on the socket file descriptor an answer.
	char *answer = "Message received!";
	int ret = write(my_socket, answer, strlen(answer));
	JLG_CHECK(ret < 0, "Error while answering");
	
	// Close the connected socket
	JLG_CHECK(close(my_socket) < 0, "ECHOSERV: Error calling close()\n");
		
	JLG_DEBUG("thread server id: %d END", my_socket);
cleanup:
	return NULL;
}


int handle_connection(int socket) {
	JLG_DEBUG("handle_connection with socket = %d", socket);
	// open a new thread and handle the connection inside the thread
	pthread_t my_thread;
	int rc = pthread_create(&my_thread, NULL, thread_server, (void *)socket);
	JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d\n", rc);
cleanup:
	return s_return_code;
}



void server_start(int port) {
	JLG_DEBUG("starting server");
	int server_socket = 0;
	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	// test for error
	// Upon successful completion, socket() returns a nonnegative integer, the socket file descriptor. Otherwise a value of -1 is returned and errno is set to indicate the error.
	JLG_CHECK(server_socket < 0, "ECHOSERV: Error creating listening socket on port %d.\n", PORT);
	
	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port        = htons(PORT);

	JLG_CHECK(bind(server_socket, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "ECHOSERV: Error calling bind()\n");
	
	JLG_CHECK(listen(server_socket, LISTEN_QUEUE_SIZE) < 0, "ECHOSERV: Error calling listen()\n");
	
	while (TRUE) {
	
		// Wait for a connection, then accept() it
		int client_socket = 0;
		JLG_CHECK((client_socket = accept(server_socket, NULL, NULL)) < 0, "ECHOSERV: Error calling accept()\n");

		JLG_CHECK(handle_connection(client_socket), "Error while handling a connection\n");
		
		
	
	}
cleanup:
	return;
}

int main() {
	JLG_DEBUG_ON();
	JLG_DEBUG("starting");
	server_start(PORT);
	JLG_STOP_ON_ERROR;
	JLG_DEBUG("closing server");
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return s_return_code;
}