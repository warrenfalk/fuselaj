JAVA_INCLUDE=/usr/lib/jvm/java-7-openjdk-amd64/include
CC=gcc

all: libfuseconnector.so

jni: jni/warrenfalk_fuselaj_Fuselaj.h

libfuseconnector.so : fuseconnector.o
	$(CC) -shared -Wl,-soname,libfuseconnector -o libfuseconnector.so fuseconnector.o

fuseconnector.o : fuseconnector.c jni/warrenfalk_fuselaj_Fuselaj.h
	$(CC) -c -fPIC -I$(JAVA_INCLUDE) -D_FILE_OFFSET_BITS=64 fuseconnector.c -o fuseconnector.o

jni/warrenfalk_fuselaj_Fuselaj.h : ../bin/warrenfalk/fuselaj/Fuselaj.class
	javah -d jni -jni -classpath ../bin warrenfalk.fuselaj.Fuselaj
	touch jni/warrenfalk_fuselaj_Fuselaj.h
	
