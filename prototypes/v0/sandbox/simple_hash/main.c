#include "simple_hash.h"

int main(int argc, char **argv) {
	
	JLG_DEBUG_ON();
	JLG_DEBUG("starting");
	
	simple_hash_init();

	simple_hash_t hash;

	simple_hash_create(&hash, "sha1", "/cygdrive/c/jlouis/sandbox/simple_hash/data");
	simple_hash_set(&hash, "ma_cle", "mon bloc de donnees");
	simple_hash_set(&hash, "mon_autre_cle", "mon autre bloc de donnees");
	
	char buffer[BUFFER_SIZE] = "";
	int ret = 0;
	ret = simple_hash_get(&hash, "ma_cle", buffer);
	JLG_CHECK(ret, "Not found");
	JLG_DEBUG("buffer = %s", buffer);
	
	JLG_TRY(simple_hash_remove(&hash, "ma_cle"));
	ret = simple_hash_get(&hash, "ma_cle", buffer);
	JLG_CHECK(ret, "Not found");
	JLG_DEBUG("buffer = %s", buffer);

	
	ret = simple_hash_get(&hash, "n'importe quoi", buffer);
	JLG_CHECK(ret, "Not found");
	JLG_DEBUG("buffer = %s", buffer);
	
cleanup:
	JLG_LOG_ERROR_IF_ANY;
	simple_hash_finalize();
	return s_return_code;
}
