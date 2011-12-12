#include "jlg.h"
#include <sys/msg.h>
#include <sys/ipc.h>

#define MSG_R   0400    /* read permission */
#define MSG_W   0200    /* write permission */


int main(int argc, char **argv) {
	jlg_init();
	JLG_DEBUG_ON();
	JLG_LOG("starting");

	// create the queue	
	int qd = msgget(IPC_PRIVATE, MSG_R|MSG_W );
	JLG_CHECK(qd == -1, "Error while calling msgget");
	
	// get info on the queue
	struct msqid_ds queue_info;
	JLG_CHECK(msgctl(qd, IPC_STAT, &queue_info) == -1, "Error while calling msgctl");
	JLG_DEBUG("msg_qbytes = %d", queue_info.msg_qbytes);
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;	
}
