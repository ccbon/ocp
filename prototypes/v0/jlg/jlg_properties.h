/* purpose of properties is to load easily a properties file that configure an application */
#ifndef _JLG_PROPERTIES_H_
#define _JLG_PROPERTIES_H_

#include "jlg_hash.h"

typedef struct _properties_t {
	char *filename;
	hash_t *hashp;
} properties_t;

properties_t *properties_create();
int properties_delete(properties_t **p);

int properties_set_filename(properties_t *p, char *filename);

int properties_reload(properties_t *p);
int properties_save(properties_t *p);
int properties_set(properties_t *p, char *key, char *value);

#endif // _JLG_PROPERTIES_H_