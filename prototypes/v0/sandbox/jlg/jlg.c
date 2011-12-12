#include "jlg.h"

#include <stdarg.h> // for va_arg
#include <assert.h> // for assert
#include <dirent.h> // for directories
#include <stdlib.h> // for exit
#include <string.h> // for strlcpy
#include <openssl/evp.h> // for hash
#include <errno.h> // for strtol and errno


// Error Management
pthread_key_t s_error_tk;

int s_thread_return_success = 0;
int s_thread_return_fail = 1;


bool s_debug_flag = false;
pthread_t s_main_thread;


void jlg_free(void **pp) {
	if (pp && *(pp)) { free(*(pp)); *(pp) = NULL; }
}

// Error Management functions
void jlg_init() {
	// setup the error management key
	int ret = 0;
	if ((ret = pthread_key_create(&s_error_tk, NULL)) != 0) {
		fprintf(stderr, "Cannot create a pthread key. Error %d\n", ret);
		exit(EXIT_FAILURE);
	}
	s_main_thread = pthread_self();
}

void jlg_finalize() {
	int ret = 0;
	if ((ret = pthread_key_delete(s_error_tk)) != 0) {
		fprintf(stderr, "Cannot delete a pthread key. Error %d\n", ret);
		exit(EXIT_FAILURE);
	}
}

int *jlg_return_code_address() {
	if (errno) {
		return &s_thread_return_fail; // fail
	}
	char *allocated_message = NULL;
	if ((allocated_message = pthread_getspecific(s_error_tk)) != NULL) {
		return &s_thread_return_fail; // fail
	}
	return &s_thread_return_success; // success
}

void
jlg_set_error_message(
	char *format, ...) {
	
	char message[BUFFER_SIZE] = "";
	va_list ap;
	
	assert(format != NULL);
	
	// Build message with the arguments
	va_start(ap, format);
	vsnprintf(message, sizeof(message), format, ap);
	va_end(ap);
	
	char error_message[BUFFER_SIZE] = "";
	// add errno info if necessary
	if (errno) {
		snprintf(error_message, BUFFER_SIZE, "%s [errno info: %d - %s]", message, errno, strerror(errno));
	} else {
		strlcpy(error_message, message, BUFFER_SIZE);
	}
	
	// delete any previous message
	char *allocated_message = NULL;
	if ((allocated_message = pthread_getspecific(s_error_tk)) != NULL) {
		JLG_FREE(&allocated_message);
	}
	allocated_message = (char *) malloc(sizeof(char) * BUFFER_SIZE);
	strlcpy(allocated_message, error_message, BUFFER_SIZE);
	if (pthread_setspecific(s_error_tk, allocated_message)) {
		abort();
	}
}

char *jlg_get_error_message() {
	return pthread_getspecific(s_error_tk);
}

void
jlg_reset_error_message() {
	// delete any previous message
	errno = 0;
	char *allocated_message = NULL;
	if ((allocated_message = pthread_getspecific(s_error_tk)) != NULL) {
		JLG_FREE(&allocated_message);
	}
	if (pthread_setspecific(s_error_tk, NULL)) {
		abort();
	}
}

// logging function
bool is_debug_mode() {
	return s_debug_flag;
}

void
jlg_log(
	loglevel_t level,
	char *     filename,
	long       line_number,
	char *     format,
	...) {
    
	char message[BUFFER_SIZE];
	va_list ap;

	if (format == NULL) {
		jlg_log_message(level, filename, line_number, "bug: format is null");
		return;
	}
	assert(format != NULL);
	
	// Build message with the arguments
	va_start(ap, format);
	vsnprintf(message, sizeof(message), format, ap);
	va_end(ap);
	
	jlg_log_message(level, filename, line_number, message);
}


void
jlg_log_message(
	loglevel_t level,
	char *     filename,
	long       line_number,
	char *     message) {
	// thinking to log on files, stdout, or other way
	FILE *fd = NULL;
	fd = stdout;
	char *loglevel_buffer = NULL;
	switch (level) {
		case debug_level:
			loglevel_buffer = "D";
			break;
		case warning_level:
			loglevel_buffer = "W";
			break;
		case error_level:
			loglevel_buffer = "E";
			break;
		default:
			loglevel_buffer = "L";
	}
	char tid_buffer[BUFFER_SIZE] = "main";
	if (pthread_self() != s_main_thread) {
		snprintf(tid_buffer, BUFFER_SIZE, "%u", (unsigned int) pthread_self());
	}
	fprintf(fd, "%s [%s:%ld] [tid:%s]: %s\n", loglevel_buffer, filename, line_number, tid_buffer, message);
	fflush(fd);
}

// number conversion
long jlg_strtol(const char *S, char **PTR, int BASE) {
	if (!S) {
		JLG_THROW_ERROR("Cannot convert a null string to a long integer");
	}
	errno = 0;
	long result = strtol(S, PTR, BASE);
	if ((result == 0) && (NOT_EQUALS(S, "0"))) {
		JLG_THROW_ERROR("Cannot convert |%s| to a long integer", S);
	}
cleanup:
	return result;
}

// file system stuff
bool file_exists(const char *filename) {
	// a version with access (unistd.h) could be done as well
	FILE *stream = NULL;
	if ((stream = fopen(filename, "r")) != NULL) {
		fclose(stream);
		return true;
	}
	errno = 0;
	return false;
}

bool dir_exists(const char *directory)
{
	DIR *dir; // <dirent.h>

	// Try to open the object as a directory stream
	dir = opendir(directory);
	if (dir != NULL) {
		closedir(dir);
		return true;
	}

	// All that's left is for it to be is a file. Return the "is a file" flag
	return false;
}

