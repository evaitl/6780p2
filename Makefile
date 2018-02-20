SRCS:=$(wildcard x780p2/client/*.java)
SRCS+=$(wildcard x780p2/server/*.java)

CLASSES:= $(SRCS:%.java=%.class)

JARS:=client.jar server.jar

.PHONY: all indent clean

all: myftp myftpserver
	$(MAKE) -C docs $@

indent:
	uncrustify --no-backup -c docs/uncrustify.cfg $(SRCS)

clean:
	-rm $(CLASSES) $(JARS)
	-rm myftp myftpserver

%.class: %.java
	javac $<

client.jar: $(CLASSES) x780p2/clientmanifest
	jar cfm $@ x780p2/clientmanifest x780p2/client

server.jar: $(CLASSES) x780p2/servermanifest
	jar cfm $@ x780p2/servermanifest x780p2/server

myftp: client.jar
	echo "#!/bin/sh" > $@
	echo 'java -jar $(realpath $<) $$@' >> $@
	chmod +x $@

myftpserver: server.jar
	echo "#!/bin/sh" > $@
	echo 'java -jar $(realpath $<) $$@' >> $@
	chmod +x $@
