#ifndef _JLG_UDP_CLIENT_H_
#define _JLG_UDP_CLIENT_H_

#include "jlg.h"

typedef struct _udp_client_t {
	int port;
	char *hostname;
} udp_client_t;

udp_client_t *udp_client_create();
int udp_client_delete(udp_client_t **udp_clientpp);

int udp_client_set_hostname(udp_client_t *udp_clientp, char *hostname);

int udp_client_send(udp_client_t *udp_clientp, char *message);

#endif // _JLG_UDP_CLIENT_H_