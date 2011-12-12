#include <stdio.h>
#include <stdlib.h>
#include <string.h> // for calling memset

#include <sys/socket.h> // for socket(), ...
#include <netinet/in.h> // for sockaddr_in
#include <unistd.h> // for closing socket

#define PORT 22222
#define LISTEN_QUEUE_SIZE 5

#define TRUE 1
#define FALSE 0

////////////// error checking macro

#define BUFFER_SIZE 1024

static int s_return_code = 0;

#define CHECK_ERROR(condition, ...) \
	if (condition) { \
		char my_buffer[BUFFER_SIZE] = ""; \
		snprintf(my_buffer, BUFFER_SIZE, __VA_ARGS__); \
		fprintf(stderr, my_buffer); \
		s_return_code = 1; \
		goto cleanup; \
	} \

int handle_connection(int socket) {
	// here a socket is like a stream referenced by an integer
	// print on the screen the stream...
	
	while (TRUE) {
		int size = 16;
		int qty_read = 0;
		char buffer[16] = "";
		CHECK_ERROR((qty_read = read(socket, buffer, size - 1)) < 0, "Error while reading the socket content.\n");
		buffer[15] = '\0';
		printf("Socket content: %s\n", buffer);
		if (qty_read != size - 1) {
			// we suppose it's because the end of stream
			break;
		}
	}
	
	// write on the socket file descriptor an answer.
	char *answer = "Message received!";
	int ret = write(socket, answer, strlen(answer));
	CHECK_ERROR(ret < 0, "Error while answering");
	
cleanup:
	return s_return_code;
}
	
int main() {
	printf("Starting TCP Server on Port %d.\n", PORT);
	
	// start the server
	// create a socket pointer
	// int socket(int domain, int type, int protocol);
	// domain : AF_UNIX (File system pathnames.) or AF_INET (Internet address.)
	// type : SOCK_STREAM, etc. (always SOCK_STREAM for a bidirectional socket (to be investigated later)
	int server_socket = 0;
	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	// test for error
	// Upon successful completion, socket() returns a nonnegative integer, the socket file descriptor. Otherwise a value of -1 is returned and errno is set to indicate the error.
	CHECK_ERROR(server_socket < 0, "ECHOSERV: Error creating listening socket on port %d.\n", PORT);

	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port        = htons(PORT);

	CHECK_ERROR(bind(server_socket, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "ECHOSERV: Error calling bind()\n");
	
	CHECK_ERROR(listen(server_socket, LISTEN_QUEUE_SIZE) < 0, "ECHOSERV: Error calling listen()\n");
	
	while (TRUE) {
	
		// Wait for a connection, then accept() it
		int client_socket = 0;
		CHECK_ERROR((client_socket = accept(server_socket, NULL, NULL)) < 0, "ECHOSERV: Error calling accept()\n");

		CHECK_ERROR(handle_connection(client_socket), "Error while handling a connection\n");
		
		// Close the connected socket
		CHECK_ERROR(close(client_socket) < 0, "ECHOSERV: Error calling close()\n");
	
	}

	
cleanup:
	return s_return_code;
}

