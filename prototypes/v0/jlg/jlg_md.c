#include "jlg_md.h"
#include <openssl/evp.h>

static int s_md_init = 0;

int jlg_md_str(const char *src, char *dst, int md_length, const char *algo_name) {
	if (s_md_init == 0) {
		OpenSSL_add_all_digests();
		s_md_init = 1;
	}
	const EVP_MD *evp_mdp = EVP_get_digestbyname(algo_name);
	EVP_MD_CTX mdctx;  //context EVP for MD (message digest)
	EVP_MD_CTX_init(&mdctx);
	EVP_DigestInit_ex(&mdctx, evp_mdp, NULL);
	EVP_DigestUpdate(&mdctx, src, strlen(src));
	unsigned char md_value[EVP_MAX_MD_SIZE];
	unsigned int md_len;
	EVP_DigestFinal_ex(&mdctx, md_value, &md_len);
	EVP_MD_CTX_cleanup(&mdctx);

	unsigned int i = 0;
	char buffer[BUFFER_SIZE] = "";
	for (i = 0; i < md_len; i++) {
		char buf[3] = "";
		snprintf(buf, 3, "%02x", md_value[i]);
		strlcat(buffer, buf, BUFFER_SIZE);
	}
	strlcpy(dst, buffer, md_length + 1);
	JLG_STOP_ON_ERROR;
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	return JLG_RETURN_CODE;
}