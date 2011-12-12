#include "jlg_udp_client.h"

#include <sys/socket.h> // for socket(), ...
#include <netdb.h> // for gethostbyname(), ...
#include <unistd.h> // for closing socket

udp_client_t *udp_client_create() {
	JLG_CREATE(p, udp_client_t);
	return p;
}

int udp_client_delete(udp_client_t **udp_clientpp) {
	if (udp_clientpp && *udp_clientpp) {
		udp_client_t *udp_clientp = *udp_clientpp;
		JLG_FREE(&(udp_clientp->hostname));
	}
	JLG_FREE(udp_clientpp);
	return 0;
}

int udp_client_set_hostname(udp_client_t *udp_clientp, char *hostname) {
	JLG_FREE(&(udp_clientp->hostname));
	udp_clientp->hostname = strdup(hostname);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int udp_client_send(udp_client_t *udp_clientp, char *message) {
	JLG_DEBUG("about to send to server %s:%d the udp message |%s|", udp_clientp->hostname, udp_clientp->port, message);
	int client_socket = 0;
	JLG_CHECK((client_socket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0, "ERROR opening socket");

	// retrieve host by name
	struct hostent *server;
	server = gethostbyname(udp_clientp->hostname);
	JLG_CHECK((h_errno), "ERROR, no such host as %s", udp_clientp->hostname);
	
	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
	memmove((void *)&servaddr.sin_addr.s_addr, (void *)server->h_addr, server->h_length);
    servaddr.sin_port        = htons(udp_clientp->port);

	int bytes_sent = sendto(client_socket, message, strlen(message) + 1, 0, (struct sockaddr *) &servaddr, sizeof(servaddr));
	JLG_CHECK(bytes_sent == -1, "Error while sending the udp message");
	JLG_DEBUG("message sent to %s:%d (%d bytes sent): |%s|", udp_clientp->hostname, udp_clientp->port, bytes_sent, message);
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}