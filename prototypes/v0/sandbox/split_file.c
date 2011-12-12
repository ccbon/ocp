#include <stdio.h>
#include <dirent.h>


#define BUFFER_SIZE 1024
#define N 100

int file_exists(const char *filename) {
	FILE *stream = NULL;
	if ((stream = fopen(filename, "r")) != NULL) {
		fclose(stream);
		return 1;
	} else {
		return 0;
	}
}

int is_dir(char *in_string)
{
	DIR *dir; // <dirent.h>

	// Try to open the object as a directory stream
	dir = opendir(in_string);
	if (dir != NULL) {
		closedir(dir);
		return 1;
	}

	// All that's left is for it to be is a file. Return the "is a file" flag
	return 0;
}

 int main(int argc, char **argv) {
	printf("starting split file: argc=%d\n", argc);
	int i = 0;
	for (i = 0; i < argc; i++) {
		printf("arg %d = %s\n", i, argv[i]);
	}
	// this program takes two args: a filename and a pathname
	if (argc != 3) {
		printf("error: programs must take 2 args\n");
		return 1;
	}
	// arg1 is a filename that must exists.
	char *filename = NULL;
	filename = argv[1];
	printf("filename = %s\n", filename);
	if (!file_exists(filename)) {
		printf("error: filename does not exist.\n");
		return 1;
	}
	printf("file %s exists.\n", filename);
	char *pathname = NULL;
	pathname = argv[2];
	printf("pathname = %s\n", pathname);
	
	// check the pathname exists.
	if (!is_dir(pathname)) {
		printf("error: pathname does not exist.\n");
		return 1;	
	}
	
	// cut the file in slice of N bytes.
	FILE *stream = fopen(filename, "r");
	// allocate N foreach buffer
	char buffer[N];
	i = 0;
	int n = fread(buffer, sizeof(char), N, stream);
	if (ferror(stream)) {
		printf("Cannot read the file\n");
		return 1;
	}
	while (n > 0) {
		// write the buffer in a new file
		char output_filename[BUFFER_SIZE] = "";
		snprintf(output_filename, BUFFER_SIZE, "%s/f%03d.txt", pathname, i);
		FILE *ostream = fopen(output_filename, "w");
		if (fwrite(buffer, sizeof(char), n, ostream) != n) {
			printf("Error while writing in output file\n");
			return 1;
		}
		fclose(ostream);
		i++;
		if ((n != N) && (!feof(stream))) {
			printf("error: File not read properly\n");
			return 1;
		}
		n = fread(buffer, sizeof(char), N, stream);
		if (ferror(stream)) {
			printf("Cannot read the file\n");
			return 1;
		}
	}
	return 0;
}
