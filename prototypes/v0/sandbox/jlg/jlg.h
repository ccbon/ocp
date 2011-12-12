/*
	Purpose of this header file is to be included in most of your code
	for making it more readable, affordant and resilient
	
	Please read the documentation README.txt
	
*/
#ifndef _JLG_H_
#define _JLG_H_
#include <stdlib.h>
#include <pthread.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>

// types

typedef enum _bool {
	false,
	true
} bool;

typedef enum _loglevel_t {
	error_level,
	warning_level,
	log_level,
	debug_level
} loglevel_t;

typedef void (*free_ptr_function_t) (void *ptr);


// macro constant

#define BUFFER_SIZE 1024

#define FALSE 0
#define TRUE  1

extern bool s_debug_flag;

extern int s_thread_return_success;
extern int s_thread_return_fail;

// logging
#define JLG_DEBUG_ON() { s_debug_flag = true; }
#define JLG_DEBUG_OFF() { s_debug_flag = false; }
#define JLG_DEBUG(...) if (is_debug_mode()) {	jlg_log(debug_level, __FILE__, __LINE__, __VA_ARGS__); }
#define JLG_LOG(...) jlg_log(log_level, __FILE__, __LINE__, __VA_ARGS__)
#define JLG_ERROR(...) jlg_log(error_level, __FILE__, __LINE__, __VA_ARGS__)

// error management
#define JLG_RETURN_CODE (*(jlg_return_code_address()))

#define JLG_THROW_ERROR(...) { \
		jlg_set_error_message(__VA_ARGS__); \
		goto cleanup; \
	} \

#define JLG_CHECK(condition, ...) \
	if ((condition)) { \
		JLG_THROW_ERROR(__VA_ARGS__); \
	} \

#define JLG_STOP_ON_ERROR if (errno || (!IS_EMPTY(jlg_get_error_message()))) goto cleanup

#define JLG_LOG_ERROR_IF_ANY if (errno) { \
		jlg_set_error_message("Unknown error"); \
		JLG_ERROR(jlg_get_error_message()); \
	} else if (!IS_EMPTY(jlg_get_error_message())) { \
		JLG_ERROR(jlg_get_error_message()); \
	} \

#define JLG_TRY(statement) \
	statement; \
	JLG_STOP_ON_ERROR \

// Math
#define MAX(a, b) (((a) > (b)) ? (a) : (b))
#define MIN(a, b) (((a) > (b)) ? (b) : (a))

#define RESET_STRUCT(objectp) memset(objectp, 0, sizeof(*objectp));

#define JLG_NEW(object, type, type_create) \
	object = (type *) malloc(sizeof(type)); \
	type_create(object);

#define JLG_CREATE(p, type) \
	type *p = (type *) malloc(sizeof(type)); \
	RESET_STRUCT(p); \

#define EQUALS(s1, s2) (s1 != NULL && strcmp(s1, s2) == 0)

#define NOT_EQUALS(s1, s2) !(s1 != NULL && strcmp(s1, s2) == 0)

#define CONTAINS(s1, s2) (s1 != NULL && strstr(s1, s2))

#define NOT_CONTAINS(s1, s2) !(s1 != NULL && strstr(s1, s2))

#define IS_EMPTY(s) (s == NULL || strlen(s) == 0)

#define STARTS_WITH(s1, s2) (s1 != NULL && s2 != NULL && (strncmp(s1, s2, strlen(s2)) == 0))

// number conversion (gives a jlg error message
long jlg_strtol(const char *S, char **PTR,int BASE);
#define jlg_atoi(S) jlg_strtol(S, NULL, 10)

// Fonctions

void jlg_free(void **pp);
#define JLG_FREE(x) jlg_free((void **) x);

// Error Management functions
void jlg_init();
void jlg_finalize();

void
jlg_set_error_message(
	char *format, ...);
	
char *jlg_get_error_message();

void
jlg_reset_error_message();

int *jlg_return_code_address();

// logging function

bool is_debug_mode();

void
jlg_log(
	loglevel_t level,
	char *     file_name,
	long       line_number,
	char *     format,
	...);

void
jlg_log_message(
	loglevel_t level,
	char *     filename,
	long       line_number,
	char *     message);

// file system

bool file_exists(const char *filename);
bool dir_exists(const char *directory);



#endif // _JLG_H_