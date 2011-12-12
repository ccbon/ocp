TARGET_NAME=$(shell basename `pwd`)
JLG_DIR=/cygdrive/c/jlouis/sandbox/jlg

DEBUG_OPTS=-O0 -g
all : $(wildcard *.c *.h)
	gcc $(DEBUG_OPTS) -Wall -o $(TARGET_NAME) *.c -I$(JLG_DIR) -L$(JLG_DIR) -lcrypto -ljlg

clean:
	rm -rf $(TARGET_NAME)
