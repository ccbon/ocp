#ifndef _DHT_CLIENT_H_
#define _DHT_CLIENT_H_

#include "jlg_dht.h"

typedef struct _dht_client_t {
	dht_t *dhtp;
} dht_client_t;

dht_client_t *dht_client_create(dht_t *dhtp);
int dht_client_delete(dht_client_t **dht_clientpp);

int dht_client_start();



#endif // _DHT_CLIENT_H_