TARGET_NAME=simple_agent

all : $(wildcard *.c *.h)
	gcc -shared -Wall -o $(TARGET_NAME) *.c -lcrypto

clean:
	rm -rf $(TARGET_NAME)
