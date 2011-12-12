TARGET_NAME=simple_hash

all : $(wildcard *.c *.h)
	gcc -Wall -o $(TARGET_NAME) *.c -lcrypto

clean:
	rm -rf $(TARGET_NAME)
