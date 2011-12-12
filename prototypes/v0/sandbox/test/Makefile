TARGET_NAME=jlg

DEBUG_OPTS=-O0 -g
all : $(wildcard *.c *.h)
	gcc $(DEBUG_OPTS) -Wall -o $(TARGET_NAME) *.c -lcrypto

clean:
	rm -rf $(TARGET_NAME)
