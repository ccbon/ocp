// This example use the Thread mechanism
#include "rra.h"

#include <pthread.h>
#include <unistd.h> // for sleep

#define NUM_THREADS     5

static int s_thread_exit_status[NUM_THREADS];

void *PrintHello(void *threadid)
{
   long tid;
   tid = (long)threadid;
   printf("Thread %ld: Hello World! It's me, thread #%ld!\n", tid, tid);
   sleep(1);
   printf("Thread %ld: Done.\n", tid); 
   pthread_exit(&s_thread_exit_status[tid]);
   return NULL;
}


int main(int argc, char **argv) {
	DEBUG_START();
	DEBUG("We start!");	
	
	pthread_t threads[NUM_THREADS];
	
	int rc;
	long t;
	for(t = 0; t < NUM_THREADS; t++) {
		printf("In main: creating thread %ld\n", t);
		
		
		// init the thread exit status to 0;
		s_thread_exit_status[t]= 1000 + t;
		// int pthread_create(pthread_t *restrict thread, const pthread_attr_t *restrict attr, void *(*start_routine)(void*), void *restrict arg);

		rc = pthread_create(&threads[t], NULL, PrintHello, (void *)t);
		CHECK_ERROR(rc, "ERROR; return code from pthread_create() is %d\n", rc);
	}
	
	// wait for all the threads terminate
	// for instance using pthread_join
	for (t = 0; t < NUM_THREADS; t++) {
		void *ptr = NULL;
		int ret = pthread_join(threads[t], &ptr);
		CHECK_ERROR(ret, "Error while using pthread_join. Error code returned: %d\n", ret);
		printf("Return value of thread #%ld: %d\n", t,  *((int *) ptr));
	}
	
	

	printf("That's it. All the thread should be finished...\n");
	
	/* Last thing that main() should do */
	pthread_exit(NULL);
	DEBUG_END();
cleanup:
	return s_return_code;
}
