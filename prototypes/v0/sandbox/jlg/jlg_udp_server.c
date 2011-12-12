#include "jlg_udp_server.h"

#include <sys/socket.h> // for socket(), ...
#include <netinet/in.h> // for sockaddr_in
#include <unistd.h> // for closing socket and parsing options
#include <arpa/inet.h> // for inet_ntop

#define UDP_MAX_MESSAGE_SIZE 65536

typedef struct _udp_server_request_info_t {
	udp_server_t *udp_serverp;
	char *incoming_message;
	char *client_hostname;
	int client_port;
} udp_server_request_info_t;

void *udp_server_handle_client_socket_thread(void *infop) {
	
	udp_server_t *udp_serverp = ((udp_server_request_info_t *) infop)->udp_serverp;
	char *incoming_message = ((udp_server_request_info_t *) infop)->incoming_message;
	char *client_hostname = ((udp_server_request_info_t *) infop)->client_hostname;
	int client_port = ((udp_server_request_info_t *) infop)->client_port;

	// now free the infop
	free(infop);
	
       
	udp_serverp->protocol(udp_serverp, incoming_message, client_hostname, client_port);
	
	// and free the incoming message and client hostname
	free(incoming_message);
	free(client_hostname);

//cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return NULL;
}

void *udp_server_listen_thread(void *my_udp_serverp) {
	udp_server_t *udp_serverp = (udp_server_t *) my_udp_serverp;
	JLG_DEBUG("starting udp server on port %d", udp_serverp->port);
	int server_socket = 0;
	// Domain AF_INET : IPv4 Internet protocols
	// SOCK_DGRAM (UDP), IPPROTO_UDP : Provides UDP socket (one way socket)
	server_socket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	// test for error
	// Upon successful completion, socket() returns a nonnegative integer, the socket file descriptor. Otherwise a value of -1 is returned and errno is set to indicate the error.
	JLG_CHECK(server_socket < 0, "Error creating socket");
	
	struct sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port        = htons(udp_serverp->port);

	JLG_CHECK(bind(server_socket, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "Error calling bind");
	
	while (true) {
		// Wait for a connection, then accept() it
		
		JLG_DEBUG("udp server waiting for incoming request...");
		char *incoming_message = (char *) malloc(sizeof(char) * UDP_MAX_MESSAGE_SIZE);
		incoming_message[0] = '\0';
		struct sockaddr_in *clientaddrp = (struct sockaddr_in *) malloc(sizeof(struct sockaddr_in));
		int slen = sizeof(struct sockaddr_in);
		JLG_CHECK(recvfrom(server_socket, incoming_message, UDP_MAX_MESSAGE_SIZE, 0, (struct sockaddr *) clientaddrp, &slen) == -1, "Error calling recvfrom");
		char *client_hostname = (char *) malloc(sizeof(char) * INET_ADDRSTRLEN);
		client_hostname[0] = '\0';
		JLG_CHECK(inet_ntop(AF_INET, &(clientaddrp->sin_addr), client_hostname, INET_ADDRSTRLEN) == NULL, "error calling inet_ntop");
		int client_port = ntohs(clientaddrp->sin_port);

		JLG_DEBUG("accepting incoming message from (%s:%d) : %s",
			client_hostname, client_port, incoming_message);
		pthread_t my_thread;
		// must be freed by the udp_server_handle_client_socket_thread function
		udp_server_request_info_t *infop = (udp_server_request_info_t *) malloc(sizeof(udp_server_request_info_t));
		infop->udp_serverp = udp_serverp;
		infop->incoming_message = incoming_message;
		infop->client_hostname = client_hostname;
		infop->client_port = client_port;
		int rc = pthread_create(&my_thread, NULL, udp_server_handle_client_socket_thread, (void *)infop);
		JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d\n", rc);
	}
cleanup:
	return NULL;
}

int udp_server_start(udp_server_t *udp_serverp) {	
	// start the server in a separate thread
	int rc = pthread_create(&(udp_serverp->server_thread), NULL, udp_server_listen_thread, udp_serverp);
	JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d", rc);
cleanup:
	return JLG_RETURN_CODE;
}

int udp_server_stop(udp_server_t *udp_serverp) {	
	JLG_CHECK(pthread_cancel(udp_serverp->server_thread), "Error while cancelling server_thread");
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}

udp_server_t *udp_server_create() {
	JLG_CREATE(p, udp_server_t);
	return p;
}

int udp_server_delete(udp_server_t **udp_serverpp) {
	JLG_FREE(udp_serverpp);
	return 0;
}

