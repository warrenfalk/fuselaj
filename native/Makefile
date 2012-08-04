JAVA_INCLUDE:=$(shell javac -classpath . JavaHelp.java && java -classpath . JavaHelp include)
OUTPUT=../bin
SRCGEN=../src-gen
CC=gcc
CFLAGS=-g -ggdb
CPP=g++

all: $(OUTPUT)/libfuselaj.so

clean:
	rm -f $(OUTPUT)/*.so
	rm -f $(OUTPUT)/*.o
	rm -f $(SRCGEN)/*

$(OUTPUT)/libfuselaj.so : fuselaj.c $(SRCGEN)/warrenfalk_fuselaj_FuselajFsClass.c $(SRCGEN)/warrenfalk_fuselaj_FuselajFsClass.h $(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.c $(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.h $(SRCGEN)/warrenfalk_fuselaj_FuselajFs.h $(SRCGEN)/warrenfalk_fuselaj_DirBuffer.h
	$(CC) $(CFLAGS) -o $@ -shared $(JAVA_INCLUDE) $(SRCGEN)/warrenfalk_fuselaj_FuselajFsClass.c $(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.c fuselaj.c `pkg-config fuse --libs --cflags` -fPIC

$(SRCGEN)/warrenfalk_fuselaj_FuselajFs.h : $(OUTPUT)/warrenfalk/fuselaj/FuselajFs.class
	javah -d $(SRCGEN) -jni -classpath $(OUTPUT) warrenfalk.fuselaj.FuselajFs

$(SRCGEN)/warrenfalk_fuselaj_DirBuffer.h : $(OUTPUT)/warrenfalk/fuselaj/DirBuffer.class
	javah -d $(SRCGEN) -jni -classpath $(OUTPUT) warrenfalk.fuselaj.DirBuffer
	
$(SRCGEN)/warrenfalk_fuselaj_FuselajFsClass.h : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/FuselajFs.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen header $@ warrenfalk.fuselaj.FuselajFs

$(SRCGEN)/warrenfalk_fuselaj_FuselajFsClass.c : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/FuselajFs.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen impl $@ warrenfalk.fuselaj.FuselajFs

$(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.h : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/DirBuffer.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen header $@ warrenfalk.fuselaj.DirBuffer

$(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.c : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/DirBuffer.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen impl $@ warrenfalk.fuselaj.DirBuffer

$(OUTPUT)/gen: gen.c
	gcc -D_FILE_OFFSET_BITS=64 -o $@ gen.c
