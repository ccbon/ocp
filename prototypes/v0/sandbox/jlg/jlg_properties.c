#include "jlg_properties.h"
#include "jlg.h"
#include "jlg_string.h"
#include <stdlib.h>

#define LINE_SIZE 2048

properties_t *properties_create() {
	JLG_CREATE(p, properties_t);
	p->hashp = hash_create();
	return p;
}

int properties_delete(properties_t **pp) {
	if (pp) {
		properties_t *p = *pp;
		if (p) {
			hash_delete(&(p->hashp));
			JLG_FREE(&(p->filename));
		}
	}
	JLG_FREE(pp);
	return 0;
}


int properties_reload(properties_t *p) {
	if (p->hashp) {
		// recreate the hashtable
		hash_delete(&(p->hashp));
		p->hashp = hash_create();
	} else {
		// normally this code is not reached
		p->hashp = hash_create();
	}
	JLG_DEBUG("filename = %s", p->filename);
	if (file_exists(p->filename)) {
		FILE *fd = NULL;
		JLG_CHECK((fd = fopen(p->filename, "r")) == NULL, "Cannot open %s", p->filename);
		
		char line[LINE_SIZE] = "";
		int line_nbr = 0;
		while (fgets(line, LINE_SIZE, fd) != NULL) {
			JLG_CHECK(ferror(fd), "Cannot read the file %s", p->filename);
			line_nbr++;
			chomp(line);
			
			char **pair = NULL;
			int length = 0;
			strsplit(&pair, &length, line, "=");
			if (length != 2) {
				// ignore this line
				continue;
			}
			hash_set(p->hashp, pair[0], pair[1]);
			strsplit_free(&pair);		
		}
		fclose(fd);
	}
cleanup:
	return 0;
}

int properties_save(properties_t *p) {
	JLG_CHECK(!p->filename, "filename is NULL");
	JLG_CHECK(!p->hashp, "hashp is NULL");
	
	// we go through all the hash keys
	dll_node_t *nodep = p->hashp->dlistp->nodep;
	FILE *fd = fopen(p->filename, "w");
	JLG_CHECK(!fd, "Cannot open file %s", p->filename);
	while (nodep) {
		hash_pair_t *pairp = nodep->valuep;
		fprintf(fd, "%s=%s\n", pairp->key, (char *) pairp->value);
		nodep = nodep->nextp;
	}
	JLG_CHECK(fclose(fd), "Cannot close file %s", p->filename);
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}

int properties_set(properties_t *p, char *key, char *value) {
	return hash_set(p->hashp, key, value);
}

int properties_set_filename(properties_t *p, char *filename) {
	p->filename = JLG_TRY(strdup(filename));
cleanup:
	return JLG_RETURN_CODE;
}
