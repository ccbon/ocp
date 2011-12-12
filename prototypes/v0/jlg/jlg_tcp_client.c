#include "jlg_tcp_client.h"

#include <sys/socket.h> // for socket(), ...
#include <netdb.h> // for gethostbyname(), ...
#include <unistd.h> // for closing socket

tcp_client_t *tcp_client_create() {
	JLG_CREATE(p, tcp_client_t);
	return p;
}

int tcp_client_delete(tcp_client_t **tcp_clientpp) {
	if (tcp_clientpp && *tcp_clientpp) {
		tcp_client_t *tcp_clientp = *tcp_clientpp;
		JLG_FREE(&(tcp_clientp->hostname));
	}
	JLG_FREE(tcp_clientpp);
	return 0;
}

int tcp_client_set_hostname(tcp_client_t *tcp_clientp, char *hostname) {
	JLG_FREE(&(tcp_clientp->hostname));
	tcp_clientp->hostname = strdup(hostname);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int tcp_client_send(tcp_client_t *tcp_clientp, char *request, char *response, size_t response_size) {
	JLG_DEBUG("about to send to server %s:%d the request |%s|", tcp_clientp->hostname, tcp_clientp->port, request);
	int client_socket = 0;
	JLG_CHECK((client_socket = socket(AF_INET, SOCK_STREAM, 0)) < 0, "ERROR opening socket");
	JLG_DEBUG("socket opened");
	// retrieve host by name
	struct hostent *server;
	server = gethostbyname(tcp_clientp->hostname);
	JLG_CHECK((h_errno), "ERROR, no such host as %s", tcp_clientp->hostname);
	
	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
	memmove((void *)&servaddr.sin_addr.s_addr, (void *)server->h_addr, server->h_length);
    servaddr.sin_port        = htons(tcp_clientp->port);

	JLG_CHECK(connect(client_socket, (const struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "ERROR connecting");
	
    int ret = 0;
	ret = write(client_socket, request, strlen(request));
	JLG_CHECK(ret < 0, "Error while writing the message on the socket file descriptor");

	response[0] = '\0';
	while (true) {
		int size = BUFFER_SIZE;
		int qty_read = 0;
		char buffer[BUFFER_SIZE] = "";
		JLG_CHECK((qty_read = read(client_socket, buffer, size - 1)) < 0, "Error while reading the socket content.\n");
		buffer[BUFFER_SIZE - 1] = '\0';
		strlcat(response, buffer, response_size);
		if (qty_read == 0) {
			// we suppose it's because the end of stream
			break;
		}
	}
	
	JLG_CHECK(close(client_socket) < 0, "Error while closing the socket");
	JLG_DEBUG("response = %s", response);
	JLG_DEBUG("errno = %d", errno);
cleanup:
	JLG_DEBUG("JLG_RETURN_CODE = %d", JLG_RETURN_CODE);
	return JLG_RETURN_CODE;
}
