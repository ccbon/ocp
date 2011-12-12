#include "jlg.h"

#include <unistd.h> // for sleep

#define THREADS_NBR 5


void *thread_hello(void *datap)
{
	pthread_t tid = pthread_self();
	JLG_DEBUG("Thread %u: Hello World! It's me, thread #%u!", tid, tid);
	sleep(1);
	JLG_DEBUG("Thread %u: Done.", tid);
	JLG_THROW_ERROR("eh merde");
cleanup:
	pthread_exit(jlg_return_code_address());
	return NULL;
}

int main1(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");
	
	pthread_t tid = pthread_self();
	JLG_DEBUG("Thread %u: Main... It's me, thread #%u!", tid, tid);
	
	pthread_t threads[THREADS_NBR];
	
	int i = 0;
	for (i = 0; i < THREADS_NBR; i++) {
		JLG_DEBUG("In main: creating thread %d", i);
		
		int rc = pthread_create(&threads[i], NULL, thread_hello, NULL);
		JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d", rc);
	}
	
	// wait for all the threads terminate
	// for instance using pthread_join
	for (i = 0; i < THREADS_NBR; i++) {
		void *ptr = NULL;
		int ret = pthread_join(threads[i], &ptr);
		JLG_CHECK(ret, "Error while using pthread_join. Error code returned: %d\n", ret);
		JLG_DEBUG("Return value of thread #%ld: %d\n", i,  *((int *) ptr));
	}
	
	

	JLG_DEBUG("That's it. All the thread should be finished...\n");
cleanup:
	return JLG_RETURN_CODE;
}