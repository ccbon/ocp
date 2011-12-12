#include "jlg.h"

int agent_start() {
	// retrieve the agent node id
	// in a separate thread, start the agent listener so other agent can ask him some task
}

int main(int argc, char **argv) {
	
	JLG_DEBUG_ON();
	JLG_DEBUG("starting");

	JLG_TRY(agent_start());	

	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return s_return_code;
}
