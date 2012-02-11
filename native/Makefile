JAVA_INCLUDE=-I/usr/lib/jvm/java-7-openjdk-amd64/include -I/usr/lib/jvm/java-7-openjdk-amd64/include/linux
CC=gcc
CPP=g++

all: libfuseconnector.so

clean:
	rm -f *.o
	rm -f *.so
	rm -rf jni

jni: jni/warrenfalk_fuselaj_Fuselaj.h

libfuseconnector.so : fuseconnector.c jni/warrenfalk_fuselaj_Fuselaj.h
	$(CC) -o libfuseconnector.so -shared $(JAVA_INCLUDE) -fPIC -D_FILE_OFFSET_BITS=64 fuseconnector.c

jni/warrenfalk_fuselaj_Fuselaj.h : ../bin/warrenfalk/fuselaj/Fuselaj.class
	javah -d jni -jni -classpath ../bin warrenfalk.fuselaj.Fuselaj
	
