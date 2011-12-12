#include <string.h>
#include <stdlib.h>

// like chomp in perl
void chomp(char *string) {
	int length = strlen(string);
	if (string[length - 1] == '\n') {
		string[length - 1] = '\0';
	}
}

// substring tool
void strsub(char *buffer, const char *src, int start, int length) {
	int i = 0;
	for (i = 0; i < length; i++) {
		buffer[i] = src[start + i];
	}
	buffer[length] = '\0';
}

// splitter
// return NULL if error.
// cursor is only one character long.
void strsplit(char ***arrayp, int *length, const char *line, const char *sep) {
	
	if (line == NULL) {
		*length = 0;
		*arrayp = NULL;
	}
	
	char *dupline = strdup(line);
	// count the number of column
	int colnbr = 1;
	char *cursor = dupline;
	while ((cursor = strstr(cursor, sep)) != NULL) {
		cursor[0] = '\0';
		cursor++;
		colnbr++;
	}
	
	*arrayp = (char **) malloc(sizeof(char *) * colnbr);
	(*arrayp)[0] = dupline;
	int i = 0;
	for (i = 1; i < colnbr; i++) {
		(*arrayp)[i] = (*arrayp)[i-1] + strlen((*arrayp)[i-1]) + 1;
	}
	*length = colnbr;
}

void strsplit_free(char ***arrayp) {
	if (arrayp && *arrayp) {
		char **r = *arrayp;
		if (r[0]) {
			free(r[0]); // remove all in one because it was allocated all in one...
		}
		free(r);
	}
	*arrayp = NULL;
}

