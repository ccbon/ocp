#include "jlg.h"

// Misc
#include <stdlib.h>
#include <string.h> // for calling memset
#include <ctype.h> // for isprint

// Network
#include <sys/socket.h> // for socket(), ...
#include <netinet/in.h> // for sockaddr_in
#include <unistd.h> // for closing socket and parsing options
#include <netdb.h> // for gethostbyname(), ...

// Threading
#include <pthread.h>

int s_thread_success_exit_status = 0;

#define LISTEN_QUEUE_SIZE 5
#define SERVER_HOSTNAME "127.0.0.1"

int s_port = 22222;
pthread_t s_server_thread;

int parse_options(int argc, char **argv) {
	JLG_DEBUG("parse option start");
	
	 //int aflag = 0;
       //int bflag = 0;
       //char *cvalue = NULL;
       //int index;
	int c;
     
       //opterr = 0;
     
	while ((c = getopt(argc, argv, "p:")) != -1) {
		switch (c) {
        	case 'p' :
				s_port = atoi(optarg);
				break;
			case '?':
				if (optopt == 'p') {
					JLG_THROW_ERROR("Option -%c requires an argument.", optopt);
				} else if (isprint(optopt)) {
					JLG_THROW_ERROR("Unknown option `-%c'.", optopt);
				} else {
					JLG_THROW_ERROR("Unknown option character `\\x%x'.", optopt);
				}
           default:
             JLG_THROW_ERROR("Unexpected Error.");
           }
	}     
	JLG_DEBUG("parse option end with s_port = %d", s_port);
cleanup:
	return s_return_code;
}


void *thread_server(void *p) {	
	int my_socket;
	my_socket = (int) p;
	JLG_DEBUG("thread server id: %d", my_socket);

	char buffer[BUFFER_SIZE] = "";	
	while (TRUE) {
		sleep(10);
		int size = BUFFER_SIZE;
		int qty_read = 0;
		JLG_DEBUG("about to read the client socket content");
		JLG_CHECK((qty_read = read(my_socket, buffer, size - 1)) < 0, "Error while reading the socket content.\n");
		JLG_DEBUG("client socket content read");
		buffer[15] = '\0';
		printf("Socket content: %s\n", buffer);
		if (qty_read != size - 1) {
			// we suppose it's because the end of stream
			break;
		}
	}
	
	// write on the socket file descriptor an answer.
	char answer[BUFFER_SIZE];
	snprintf(answer, BUFFER_SIZE, "Message received! (%s)", buffer);
	int ret = write(my_socket, answer, strlen(answer));
	JLG_CHECK(ret < 0, "Error while answering");
	
	// Close the connected socket
	JLG_CHECK(close(my_socket) < 0, "ECHOSERV: Error calling close()\n");
		
	JLG_DEBUG("thread server id: %d END", my_socket);
cleanup:
	return NULL;
}


int handle_connection(int socket) {
	JLG_DEBUG("handle_connection with socket = %d", socket);
	// open a new thread and handle the connection inside the thread
	pthread_t my_thread;
	int rc = pthread_create(&my_thread, NULL, thread_server, (void *)socket);
	JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d\n", rc);
cleanup:
	return s_return_code;
}



void *thread_server_start(void *unused) {
	JLG_DEBUG("starting server");
	int server_socket = 0;
	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	// test for error
	// Upon successful completion, socket() returns a nonnegative integer, the socket file descriptor. Otherwise a value of -1 is returned and errno is set to indicate the error.
	JLG_CHECK(server_socket < 0, "ECHOSERV: Error creating listening socket on port %d.\n", s_port);
	
	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port        = htons(s_port);

	JLG_CHECK(bind(server_socket, (struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "ECHOSERV: Error calling bind()\n");
	
	JLG_CHECK(listen(server_socket, LISTEN_QUEUE_SIZE) < 0, "ECHOSERV: Error calling listen()\n");
	
	while (TRUE) {
	
		// Wait for a connection, then accept() it
		int client_socket = 0;
		JLG_CHECK((client_socket = accept(server_socket, NULL, NULL)) < 0, "ECHOSERV: Error calling accept()\n");

		JLG_CHECK(handle_connection(client_socket), "Error while handling a connection\n");
		
		
	
	}
cleanup:
	pthread_exit(&s_thread_success_exit_status);
	return NULL;
}

void send_message(const int port, const char *message) {
	JLG_DEBUG("about to send to server %d the message |%s|", port, message);
	int client_socket = 0;
	JLG_CHECK((client_socket = socket(AF_INET, SOCK_STREAM, 0)) < 0, "ERROR opening socket");

	// retrieve host by name
	char *hostname = SERVER_HOSTNAME;
	struct hostent *server;
	JLG_CHECK((server = gethostbyname(hostname)) < 0, "ERROR, no such host as %s\n", hostname);
	
	struct    sockaddr_in servaddr;
	// set all the fields to 0
	memset(&servaddr, 0, sizeof(servaddr));
	// set some fields to the desired values
    servaddr.sin_family      = AF_INET;
	memmove((void *)&servaddr.sin_addr.s_addr, (void *)server->h_addr, server->h_length);
    servaddr.sin_port        = htons(port);

	JLG_CHECK(connect(client_socket, (const struct sockaddr *) &servaddr, sizeof(servaddr)) < 0, "ERROR connecting");
	
    int ret = 0;
	ret = write(client_socket, message, strlen(message));
	JLG_CHECK(ret < 0, "Error while writing the message on the socket file descriptor");
	
	while (TRUE) {
		int size = BUFFER_SIZE;
		int qty_read = 0;
		char buffer[BUFFER_SIZE] = "";
		JLG_CHECK((qty_read = read(client_socket, buffer, size - 1)) < 0, "Error while reading the socket content.\n");
		buffer[BUFFER_SIZE - 1] = '\0';
		printf("answer: %s\n", buffer);
		if (qty_read != size - 1) {
			// we suppose it's because the end of stream
			break;
		}
	}
	
	JLG_CHECK(close(client_socket) < 0, "Error while closing the socket");
cleanup:
	return;
	
}

void agent_client() {
	// print the menu
	char *menu_buffer =
		"Enter your choice:\n"
		"1) Send a string to a server\n"
		"q) Quit\n";
	while (TRUE) {
		printf(menu_buffer);
		char answer[BUFFER_SIZE] = "";
		scanf("%s", answer);
		if (EQUALS(answer, "1")) {
			printf("Server port:\n");
			int port = 0;
			scanf("%d", &port);
			printf("Message to send:\n");
			char message[BUFFER_SIZE] = "";
			scanf("%s", message);
			JLG_TRY(send_message(port, message));
		} else if (EQUALS(answer, "q")) {
			// ask the server to stop
			JLG_DEBUG("about to cancel s_server_thread");
			int ret = 0;
			ret = pthread_cancel(s_server_thread);
			JLG_CHECK(ret, "Error while cancelling thread err = %d", ret);
			JLG_DEBUG("done with ret = %d", ret);
			break;
		} else {
			printf("Message not understood.\n");
		}
	}
cleanup:
	return;
}


int main(int argc, char **argv) {
	JLG_DEBUG_ON();
	JLG_DEBUG("starting");
	JLG_TRY(parse_options(argc, argv));
	
	// start a server in a separate thread
	
	int rc = pthread_create(&s_server_thread, NULL, thread_server_start, NULL);
	JLG_CHECK(rc, "ERROR; return code from pthread_create() is %d\n", rc);
	
	// read on order from entry
	agent_client();
	
	JLG_STOP_ON_ERROR;
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return s_return_code;
}