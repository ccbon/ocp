
#define NODE_SIZE 20 // use of sha1 for message digest
#define NETWORK_NAME_SIZE 1024

struct _simple_agent_config_t {
	char network_name[NETWORK_NAME_SIZE];
	simple_agent_config_t config;
};

typedef struct _simple_agent_t simple_agent_t;


struct _simple_agent_t {
	char node_id[NODE_SIZE];
	simple_agent_config_t config;
};

typedef struct _simple_agent_t simple_agent_t;

// init the agent structure
// set the network name given in input (included in network_data)
// give as well a list of at least one agent normally alive
// if no agent are alive, then this is the first agent to be run on this domain
int agent_init(simple_agent_t *agentp, simple_agent_config_t *configp);

// get_node_id role is to retrieve the node_id used by this agent from a previous run
// if it is the first run of this agent, then compute it based on computer characteristic and time
// check as well it is not already used by another agent of the network domain
int get_node_id(char *node_id);
