#ifndef _JLG_TCP_SERVER_H_
#define _JLG_TCP_SERVER_H_

#include <pthread.h>

// function that take in input the tcp_server_t object itself (void *)
// and a socket (read, write on the socket descriptor)
typedef void (*tcp_server_protocol_t)(void *, int);

typedef struct _tcp_server_t {
	int port;
	int max_queue;
	tcp_server_protocol_t protocol;
	pthread_t server_thread; // thread where the server is started
	void *contextp; // for puting anything you want the protocol may access
} tcp_server_t;

tcp_server_t *tcp_server_create();
int tcp_server_delete(tcp_server_t **tcp_serverpp);

int tcp_server_start(tcp_server_t *tcp_serverp);
int tcp_server_stop(tcp_server_t *tcp_serverp);

#endif // _JLG_TCP_SERVER_H_