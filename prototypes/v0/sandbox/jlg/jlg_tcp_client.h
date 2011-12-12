#ifndef _JLG_TCP_CLIENT_H_
#define _JLG_TCP_CLIENT_H_

#include "jlg.h"

typedef struct _tcp_client_t {
	int port;
	char *hostname;
} tcp_client_t;

tcp_client_t *tcp_client_create();
int tcp_client_delete(tcp_client_t **tcp_clientpp);

int tcp_client_set_hostname(tcp_client_t *tcp_clientp, char *hostname);

int tcp_client_send(tcp_client_t *tcp_clientp, char *request, char *response, size_t response_size);

#endif // _JLG_TCP_CLIENT_H_