#include "simple_hash.h"

#include <dirent.h>
#include <sys/stat.h>
#include <string.h>
#include <unistd.h>

int simple_hash_init() {
	JLG_DEBUG("simple_hash_init start");
	OpenSSL_add_all_digests();
	return 0;
}
int simple_hash_finalize() {
	EVP_cleanup();
	return 0;
}

static int message_digest(simple_hash_t *hashp, char *md, const char *source) {
	EVP_MD_CTX mdctx;  //context EVP for MD (message digest)
	EVP_MD_CTX_init(&mdctx);
	EVP_DigestInit_ex(&mdctx, hashp->md, NULL);
	EVP_DigestUpdate(&mdctx, source, strlen(source));
	unsigned char md_value[EVP_MAX_MD_SIZE];
	unsigned int md_len;
	EVP_DigestFinal_ex(&mdctx, md_value, &md_len);
	EVP_MD_CTX_cleanup(&mdctx);

	md[0] = '\0';
	unsigned int i = 0;
	for (i = 0; i < md_len; i++) {
		char buf[8] = "";
		snprintf(buf, 8, "%02x", md_value[i]);
		strlcat(md, buf, BUFFER_SIZE);
	}
	JLG_DEBUG("md(%s) = %s", source, md);
//cleanup:
	return s_return_code;
}

int create_file(const char *filename, const char *content) {
	FILE *fd = fopen(filename, "w");
	fputs(content, fd);
	fclose(fd);
	return 0;
}

int simple_hash_create(simple_hash_t *hashp, const char *algo_name, const char *storage_dir_buffer) {
	JLG_DEBUG("simple_hash_create start");
	strncpy(hashp->dir, storage_dir_buffer, BUFFER_SIZE);
	// make sure the directory exists or create it.
	if (!is_dir(hashp->dir)) {
		mkdir(hashp->dir, 0755);
	}
	
	hashp->md = EVP_get_digestbyname(algo_name);
	JLG_CHECK(hashp->md == NULL, "Unknown message digest %s\n", algo_name);
cleanup:
	return s_return_code;
}

int simple_hash_set(simple_hash_t *hashp, const char *keyp, const char *valuep) {
	JLG_DEBUG("simple_hash_set start");
	char md[BUFFER_SIZE] = "";
	JLG_TRY(message_digest(hashp, md, keyp));
	// collision will have to be managed...
	char filename[BUFFER_SIZE] = "";
	snprintf(filename, BUFFER_SIZE, "%s/%s.key", hashp->dir, md);
	create_file(filename, keyp);
	snprintf(filename, BUFFER_SIZE, "%s/%s.val", hashp->dir, md);
	create_file(filename, valuep);
cleanup:
	return s_return_code;
}

int get_file(const char *filename, char *valuep) {
	FILE *fd = NULL;
	fd = fopen(filename, "r");
	memset(valuep, 0, BUFFER_SIZE);
	fread(valuep, sizeof(char), BUFFER_SIZE, fd);
//cleanup:
	fclose(fd);
	return s_return_code;
}

int simple_hash_get(simple_hash_t *hashp, const char *keyp, char *valuep) {
	JLG_DEBUG("simple_hash_get start");
	// before all, empty the valuep
	valuep[0] = '\0';
	
	char md[BUFFER_SIZE] = "";
	JLG_TRY(message_digest(hashp, md, keyp));
	// collision will have to be managed...
	char filename[BUFFER_SIZE] = "";
	snprintf(filename, BUFFER_SIZE, "%s/%s.val", hashp->dir, md);
	if (file_exists(filename)) {
		get_file(filename, valuep);
	} else {
		JLG_THROW_ERROR("Not found");
	}
cleanup:
	return s_return_code;
}

int simple_hash_remove(simple_hash_t *hashp, const char *keyp) {
	// Note: collision not managed.
	JLG_DEBUG("simple_hash_remove start");
	char md[BUFFER_SIZE] = "";
	JLG_TRY(message_digest(hashp, md, keyp));
	
	char filename[BUFFER_SIZE] = "";
	snprintf(filename, BUFFER_SIZE, "%s/%s.key", hashp->dir, md);
	if (!file_exists(filename)) {
		goto cleanup;
	}
	JLG_CHECK(unlink(filename), "Error while deleting %s", filename);
	// delete also the value file
	snprintf(filename, BUFFER_SIZE, "%s/%s.val", hashp->dir, md);
	JLG_CHECK(unlink(filename), "Error while deleting %s", filename);
cleanup:
	return s_return_code;
}

