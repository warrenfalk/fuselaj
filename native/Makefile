JAVA_INCLUDE=-I/usr/lib/jvm/java-7-openjdk-amd64/include -I/usr/lib/jvm/java-7-openjdk-amd64/include/linux
OUTPUT=../bin
SRCGEN=../src-gen
CC=gcc
CPP=g++

all: $(OUTPUT)/libfuselaj.so

clean:
	rm -f $(OUTPUT)/*.so
	rm -f $(OUTPUT)/*.o
	rm -f $(SRCGEN)/*.h

$(OUTPUT)/libfuselaj.so : fuselaj.c $(SRCGEN)/warrenfalk_fuselaj_Fuselaj.h
	$(CC) -o $@ -shared $(JAVA_INCLUDE) fuselaj.c `pkg-config fuse --libs --cflags` -fPIC

$(SRCGEN)/warrenfalk_fuselaj_Fuselaj.h : $(OUTPUT)/warrenfalk/fuselaj/Fuselaj.class
	javah -d $(SRCGEN) -jni -classpath $(OUTPUT) warrenfalk.fuselaj.Fuselaj

