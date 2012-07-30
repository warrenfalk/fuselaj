JAVA_INCLUDE=`javac -classpath . JavaHelp.java && java -classpath . JavaHelp include`
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

$(OUTPUT)/libfuselaj.so : fuselaj.c $(SRCGEN)/warrenfalk_fuselaj_FilesystemClass.c $(SRCGEN)/warrenfalk_fuselaj_FilesystemClass.h $(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.c $(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.h $(SRCGEN)/warrenfalk_fuselaj_Filesystem.h $(SRCGEN)/warrenfalk_fuselaj_DirBuffer.h
	$(CC) $(CFLAGS) -o $@ -shared $(JAVA_INCLUDE) $(SRCGEN)/warrenfalk_fuselaj_FilesystemClass.c $(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.c fuselaj.c `pkg-config fuse --libs --cflags` -fPIC

$(SRCGEN)/warrenfalk_fuselaj_Filesystem.h : $(OUTPUT)/warrenfalk/fuselaj/Filesystem.class
	javah -d $(SRCGEN) -jni -classpath $(OUTPUT) warrenfalk.fuselaj.Filesystem

$(SRCGEN)/warrenfalk_fuselaj_DirBuffer.h : $(OUTPUT)/warrenfalk/fuselaj/DirBuffer.class
	javah -d $(SRCGEN) -jni -classpath $(OUTPUT) warrenfalk.fuselaj.DirBuffer
	
$(SRCGEN)/warrenfalk_fuselaj_FilesystemClass.h : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/Filesystem.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen header $@ warrenfalk.fuselaj.Filesystem

$(SRCGEN)/warrenfalk_fuselaj_FilesystemClass.c : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/Filesystem.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen impl $@ warrenfalk.fuselaj.Filesystem

$(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.h : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/DirBuffer.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen header $@ warrenfalk.fuselaj.DirBuffer

$(SRCGEN)/warrenfalk_fuselaj_DirBufferClass.c : $(OUTPUT)/warrenfalk/jni/JniGen.class $(OUTPUT)/warrenfalk/fuselaj/DirBuffer.class
	java -classpath $(OUTPUT) warrenfalk.jni.JniGen impl $@ warrenfalk.fuselaj.DirBuffer

$(OUTPUT)/gen: gen.c
	gcc -D_FILE_OFFSET_BITS=64 -o $@ gen.c