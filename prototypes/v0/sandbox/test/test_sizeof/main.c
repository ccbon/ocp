#include "jlg.h"


#include <stdlib.h>
#include <unistd.h> // for closing socket and parsing options

typedef struct _truc_t {
	char *path;
	char *name;
} truc_t;


int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	truc_t t;
	truc_t *p;
	p = &t;
	JLG_DEBUG("sizeof(*p) = %d", sizeof(*p));
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}
