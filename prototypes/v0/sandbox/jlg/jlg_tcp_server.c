#include "jlg_tcp_server.h"
#include "jlg.h"
#include <stdlib.h>
#include <sys/socket.h> // for socket(), ...
#include <netinet/in.h> // for sockaddr_in
#include <unistd.h> // for closing socket and parsing options


typedef struct _tcp_server_request_info_t {
	tcp_server_t *tcp_serverp;
	int client_socket;
} tcp_server_request_info_t;

void *tcp_server_handle_client_socket_thread(void *infop) {
	int client_socket = ((tcp_server_request_info_t *) infop)->client_socket;
	tcp_server_t *tcp_serverp = ((tcp_server_request_info_t *) infop)->tcp_serverp;
	// now free the infop
	free(infop);
	
	tcp_serverp->protocol(tcp_serverp, client_socket);
	// Close the connected socket
	JLG_CHECK(close(client_socket) < 0, "Cannot close correctly the client socket");
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return NULL;
}

void *tcp_server_listen_thread(void *my_tcp_serverp) {
	tcp_server_t *tcp_serverp = (tcp_server_t *) my_tcp_serverp;
	JLG_DEBUG("starting tcp server on port %d", tcp_serverp->port);
	int server_socket = 0;
	// Domain AF_INET : IPv4 Internet protocols
	// SOCK_STREAM (TCP) : Provides sequenced, reliable, two-way, connection-based byte streams
	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	// test for error
	// Upon successful completion, socket() returns a nonnegative integer, the socket file descriptor. Otherwise a value of -1 is returned and errno is set to indicate the error.
	JLG_CHECK(server_socket < 0, "Error creating socket");
	
	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port        = htons(tcp_serverp->port);

	JLG_CHECK(bind(server_socket, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "Error calling bind");
	
	JLG_CHECK(listen(server_socket, tcp_serverp->max_queue) < 0, "Error calling listen");
	
	while (true) {
		// Wait for a connection, then accept() it
		int client_socket = 0;
		JLG_DEBUG("tcp server waiting for incoming request...");
		JLG_CHECK((client_socket = accept(server_socket, NULL, NULL)) < 0, "Error calling accept");
		JLG_DEBUG("accepting incoming client socket = %d", client_socket);
		pthread_t my_thread;
		// must be freed by the tcp_server_handle_client_socket_thread function
		tcp_server_request_info_t *infop = (tcp_server_request_info_t *) malloc(sizeof(tcp_server_request_info_t));
		infop->tcp_serverp = tcp_serverp;
		infop->client_socket = client_socket;
		int rc = pthread_create(&my_thread, NULL, tcp_server_handle_client_socket_thread, (void *)infop);
		JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d\n", rc);
	}
cleanup:
	return NULL;
}

int tcp_server_start(tcp_server_t *tcp_serverp) {
	// start the server in a separate thread
	int rc = pthread_create(&(tcp_serverp->server_thread), NULL, tcp_server_listen_thread, tcp_serverp);
	JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d", rc);
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int tcp_server_stop(tcp_server_t *tcp_serverp) {
	
	JLG_CHECK(pthread_cancel(tcp_serverp->server_thread), "Error while cancelling server_thread");
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}

tcp_server_t *tcp_server_create() {
	JLG_CREATE(p, tcp_server_t);
	return p;
}

int tcp_server_delete(tcp_server_t **tcp_serverpp) {
	JLG_FREE(tcp_serverpp);
	return 0;
}

