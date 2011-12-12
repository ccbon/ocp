/*
	Purpose of this header file is to be included in most of your code
	for making it more readable, affordant and resilient
	
	Please read the documentation README.txt
	
*/
#ifndef _RRA_H_
#define _RRA_H_
#include <stdio.h>

#define BUFFER_SIZE 1024

#define FALSE 0
#define TRUE  1

static int s_return_code = 0;
static int s_debug_flag = FALSE;
static char *s_debug_prefix_info = "DEBUG: ";
static char s_error_message[BUFFER_SIZE] = "";
static int s_error_code = 0;

#define DEBUG_START() { s_debug_flag = TRUE; }
#define DEBUG_END() { s_debug_flag = FALSE; }
// think later to multithread env.
#define DEBUG(...) if (s_debug_flag) {	printf(s_debug_prefix_info); printf(__VA_ARGS__); printf("\n"); }

#define ERROR_OUTPUT(...) fprintf(stderr, __VA_ARGS__);

#define CHECK_ERROR(condition, ...) \
	if (condition) { \
		char my_buffer[BUFFER_SIZE] = ""; \
		snprintf(my_buffer, BUFFER_SIZE, __VA_ARGS__); \
		ERROR_OUTPUT(my_buffer); \
		s_return_code = 1; \
		goto cleanup; \
	} \


// when we free memory, the pointer should always point to NULL
// for not freeing more than once, we test if x is not already set to NULL
#define FREE(x) if (x) { free(x); x = NULL; } 

#endif // _RRA_H_