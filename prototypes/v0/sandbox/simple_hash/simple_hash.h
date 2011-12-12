#ifndef _SIMPLE_HASH_H_
#define _SIMPLE_HASH_H_
#include "jlg.h"

#include <openssl/evp.h>

struct _simple_hash_t {
	char dir[BUFFER_SIZE];
	char algo_name[BUFFER_SIZE];
	const EVP_MD *md;
};

typedef struct _simple_hash_t simple_hash_t;


int simple_hash_init();
int simple_hash_finalize();
int simple_hash_create(simple_hash_t *hashp, const char *algo_name, const char *storage_dir_buffer);
int simple_hash_set(simple_hash_t *hashp, const char *keyp, const char *valuep);
int simple_hash_get(simple_hash_t *hashp, const char *keyp, char *valuep);
int simple_hash_remove(simple_hash_t *hashp, const char *keyp);

#endif // _SIMPLE_HASH_H_