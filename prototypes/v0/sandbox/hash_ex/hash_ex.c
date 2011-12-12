#include "jlg.h"
#include <string.h> // for strncpy
#include <openssl/evp.h>
#include <unistd.h> // getopt
#include <ctype.h> // for isprint

char s_algo_name[BUFFER_SIZE] = "sha1";

int parse_options(int argc, char **argv) {
	JLG_DEBUG("parse option start");
	
	int c;
     
	while ((c = getopt(argc, argv, "a:")) != -1) {
		switch (c) {
        	case 'a' :
        		strncpy(s_algo_name, optarg, BUFFER_SIZE);
				break;
			case '?':
				if (optopt == 'a') {
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
	JLG_DEBUG("parse option end with s_algo_name = %s", s_algo_name);
cleanup:
	return s_return_code;
}
int main(int argc, char **argv) {
	
	JLG_DEBUG_ON();
	JLG_DEBUG("starting");
	JLG_TRY(parse_options(argc, argv));
	
	
	char *mess1 = "Test Message\n";
	char *mess2 = "Hello World\n";
	
	

	// add in a open_ssl private hashtable all digest algorithm
	OpenSSL_add_all_digests();

	const EVP_MD *md;  // message digest algorithm type
	md = EVP_get_digestbyname(s_algo_name);
	JLG_CHECK(!md, "Unknown message digest %s\n", s_algo_name);

	EVP_MD_CTX mdctx;  //context EVP for MD (message digest)
	EVP_MD_CTX_init(&mdctx);
	EVP_DigestInit_ex(&mdctx, md, NULL);
	EVP_DigestUpdate(&mdctx, mess1, strlen(mess1));
	EVP_DigestUpdate(&mdctx, mess2, strlen(mess2));
	unsigned char md_value[EVP_MAX_MD_SIZE];
	unsigned int md_len;
	EVP_DigestFinal_ex(&mdctx, md_value, &md_len);
	EVP_MD_CTX_cleanup(&mdctx);

	printf("Digest is: ");
	unsigned int i = 0;
	for (i = 0; i < md_len; i++) {
		printf("%02x", md_value[i]);
	}
	printf("\n");
	
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	EVP_cleanup();
	return s_return_code;
}
