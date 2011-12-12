TARGET_NAME=jlg

DEBUG_OPTS=-O0 -g
all : $(wildcard *.c *.h)
	gcc -shared $(DEBUG_OPTS) -Wall -o lib$(TARGET_NAME).dll *.c -lcrypto

clean:
	rm -rf $(TARGET_NAME)
