#ifndef _JLG_UDP_SERVER_H_
#define _JLG_UDP_SERVER_H_

#include "jlg.h"

// function that take in input the udp_server_t object itself (void *)
// and the incoming message, the client hostname and port it comes from
// if you want to send an answer to the client, use a udp_client_t object
// (you need to know where to answer back, it is not necessarely the original address)
typedef void (*udp_server_protocol_t)(void *, char *, char *, int);

typedef struct _udp_server_t {
	int port;
	udp_server_protocol_t protocol;
	pthread_t server_thread; // thread where the server is started
	void *contextp; // for puting anything you want the protocol may access
} udp_server_t;

udp_server_t *udp_server_create();
int udp_server_delete(udp_server_t **udp_serverpp);

int udp_server_start(udp_server_t *udp_serverp);
int udp_server_stop(udp_server_t *udp_serverp);

#endif // _JLG_UDP_SERVER_H_