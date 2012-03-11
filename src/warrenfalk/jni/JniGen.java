package warrenfalk.jni;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class JniGen {
	public static void main(String[] args) {
		try {
			String type = args[0];
			File output = new File(args[1]).getAbsoluteFile();
			Class<?> input = Class.forName(args[2]);
			PrintWriter writer = new PrintWriter(output);
			try {
				String className = input.getSimpleName();
				if ("impl".equals(type)) {
					writer.println("#include <jni.h>");
					writer.println("#include <assert.h>");
					writer.println("static jclass Class;");
					for (Field field : input.getDeclaredFields())
						writer.println("static jfieldID " + field.getName() + "_field;");
					for (Constructor<?> constructor : input.getDeclaredConstructors())
						writer.println("static jmethodID " + getUniqueMethodName("Create", constructor.getParameterTypes()) + ";");
					for (Method method : input.getDeclaredMethods())
						if (!Modifier.isNative(method.getModifiers()))
							writer.println("static jmethodID " + getUniqueMethodName(method.getName(), method.getParameterTypes()) + "_method;");
					writer.println("");
					writer.println("jobject to_global(JNIEnv *env, jobject obj);");
					writer.println("");
					writer.println("void " + className + "_ClassInitialize(JNIEnv* env) {");
					writer.println("\tClass = to_global(env, (*env)->FindClass(env, \"" + input.getCanonicalName().replace('.', '/') + "\"));");
					writer.println("\tassert (Class != 0);");
					for (Field field : input.getDeclaredFields()) {
						boolean isStatic = Modifier.isStatic(field.getModifiers());
						if (!isStatic) {
							writer.println("\t" + field.getName() + "_field = (*env)->GetFieldID(env, Class, \"" + field.getName() + "\", \"" + sigName(field.getType()) + "\");");
							writer.println("\tassert (" + field.getName() + "_field != 0);");
						}
						else {
							writer.println("\t" + field.getName() + "_field = (*env)->GetStaticFieldID(env, Class, \"" + field.getName() + "\", \"" + sigName(field.getType()) + "\");");
							writer.println("\tassert (" + field.getName() + "_field != 0);");
						}
					}
					for (Constructor<?> constructor : input.getDeclaredConstructors()) {
						String signature = getSig(constructor);
						writer.println("\t" + getUniqueMethodName("Create", constructor.getParameterTypes()) + " = (*env)->GetMethodID(env, Class, \"<init>\", \"" + signature + "\");");
						writer.println("\tassert (" + getUniqueMethodName("Create", constructor.getParameterTypes()) + " != 0);");
					}
					for (Method method : input.getDeclaredMethods()) {
						if (Modifier.isNative(method.getModifiers()))
							continue;
						String signature = getSig(method);
						writer.println("\t" + getUniqueMethodName(method.getName(), method.getParameterTypes()) + "_method = (*env)->Get" + (Modifier.isStatic(method.getModifiers()) ? "Static" : "") + "MethodID(env, Class, \"" + method.getName() + "\", \"" + signature + "\");");
						writer.println("\tassert (" + getUniqueMethodName(method.getName(), method.getParameterTypes()) + "_method != 0);");
					}
					writer.println("}");
					for (Field field : input.getDeclaredFields()) {
						boolean isStatic = Modifier.isStatic(field.getModifiers());
						writer.println("");
						writer.println(getJniType(field.getType()) + " " + className + "_get_" + field.getName() + "(JNIEnv* env, jobject obj) {");
						writer.println("\treturn (*env)->Get" + (isStatic ? "Static" : "") + getCallType(field.getType()) + "Field(env, " + (isStatic ? "Class" : "obj") + ", " + field.getName() + "_field);");
						writer.println("}");
						writer.println("");
						writer.println("void " + className + "_set_" + field.getName() + "(JNIEnv* env, jobject obj, " + getJniType(field.getType()) + " value) {");
						writer.println("\t(*env)->Set" + (isStatic ? "Static" : "") + getCallType(field.getType()) + "Field(env, " + (isStatic ? "Class" : "obj") + ", " + field.getName() + "_field, value);");
						writer.println("}");
					}
					for (Constructor<?> constructor : input.getDeclaredConstructors()) {
						String paramList = getParamList(constructor);
						String paramNameList = getParamNameList(constructor);
						writer.println("");
						writer.println("jobject " + className + "_" + getUniqueMethodName("Create", constructor.getParameterTypes()) + "(JNIEnv* env" + paramList + ") {");
						writer.println("\treturn (*env)->NewObject(env, Class, " + getUniqueMethodName("Create", constructor.getParameterTypes()) + paramNameList + ");");
						writer.println("}");
					}
					for (Method method : input.getDeclaredMethods()) {
						if (Modifier.isNative(method.getModifiers()))
							continue;
						String paramList = getParamList(method);
						String paramNameList = getParamNameList(method);
						writer.println("");
						writer.println(getJniType(method.getReturnType()) + " " + className + "_call_" + getUniqueMethodName(method.getName(), method.getParameterTypes()) + "(JNIEnv* env, jobject obj" + paramList + ") {");
						writer.println("\treturn (*env)->Call" + (Modifier.isStatic(method.getModifiers()) ? "Static" : "") + getCallType(method.getReturnType()) + "Method(env, obj, " + getUniqueMethodName(method.getName(), method.getParameterTypes()) + "_method" + paramNameList + ");");
						writer.println("}");
					}
				}
				else if ("header".equals(type)) {
					writer.println("#include <jni.h>");
					writer.println("void " + className + "_ClassInitialize(JNIEnv* env);");
					for (Field field : input.getDeclaredFields()) {
						writer.println(getJniType(field.getType()) + " " + className + "_get_" + field.getName() + "(JNIEnv* env, jobject obj);");
						writer.println("void " + className + "_set_" + field.getName() + "(JNIEnv* env, jobject obj, " + getJniType(field.getType()) + " value);");
					}
					for (Constructor<?> constructor : input.getDeclaredConstructors()) {
						String paramList = getParamList(constructor);
						writer.println("jobject " + className + "_" + getUniqueMethodName("Create", constructor.getParameterTypes()) + "(JNIEnv* env" + paramList + ");");
					}
					for (Method method : input.getDeclaredMethods()) {
						if (Modifier.isNative(method.getModifiers()))
							continue;
						String paramList = getParamList(method);
						writer.println(getJniType(method.getReturnType()) + " " + className + "_call_" + getUniqueMethodName(method.getName(), method.getParameterTypes()) + "(JNIEnv* env, jobject obj" + paramList + ");");
					}
				}
			}
			finally {
				writer.close();
			}
		}
		catch (Throwable t) {
			System.exit(1);
		}
	}

	static HashMap<String,Integer> sigToIndex = new HashMap<String,Integer>();
	static HashMap<String,Integer> nameToNextIndex = new HashMap<String,Integer>();
	private static String getUniqueMethodName(String name, Class<?>[] parameterTypes) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for (Class<?> ptype : parameterTypes)
			sb.append(";").append(ptype.getName());
		String sig = sb.toString();
		Integer index = sigToIndex.get(sig);
		if (index == null) {
			index = nameToNextIndex.get(name);
			if (index == null)
				index = 0;
			nameToNextIndex.put(name, index + 1);
			sigToIndex.put(sig, index);
		}
		if (index == 0)
			return name;
		else
			return name + index;
	}

	private static String getSig(Class<?>[] paramTypes, Class<?> returnType) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Class<?> ptype : paramTypes) {
			sb.append(sigName(ptype));
		}
		sb.append(')');
		sb.append(sigName(returnType));
		return sb.toString();
	}

	private static String getSig(Method method) {
		return getSig(method.getParameterTypes(), method.getReturnType());
	}

	private static String getSig(Constructor<?> constructor) {
		return getSig(constructor.getParameterTypes(), void.class);
	}

	private static String sigName(Class<?> type) {
		if (type.isArray()) {
			return "[" + sigName(type.getComponentType());
		}
		if (type.isPrimitive()) {
			if ("void".equals(type.getName()))
				return "V";
			if ("boolean".equals(type.getName()))
				return "Z";
			if ("byte".equals(type.getName()))
				return "B";
			if ("char".equals(type.getName()))
				return "C";
			if ("short".equals(type.getName()))
				return "S";
			if ("int".equals(type.getName()))
				return "I";
			if ("long".equals(type.getName()))
				return "J";
			if ("float".equals(type.getName()))
				return "F";
			if ("double".equals(type.getName()))
				return "D";
			throw new RuntimeException("Shouldn't get here");
		}
		return "L" + type.getName().replace('.', '/') + ";";
	}

	private static String getCallType(Class<?> rtype) {
		if (rtype.getName().contains("."))
			return "Object";
		else {
			String type = rtype.getName();
			return type.substring(0, 1).toUpperCase() + type.substring(1);
		}
	}
	
	private static String getParamList(Class<?>[] paramTypes) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (Class<?> ptype : paramTypes) {
			sb.append(", ").append(getJniType(ptype)).append(" ").append("arg").append(index++);
		}
		return sb.toString();
	}

	private static String getParamList(Constructor<?> constructor) {
		return getParamList(constructor.getParameterTypes());
	}

	public static String getParamList(Method method) {
		return getParamList(method.getParameterTypes());
	}
	
	private static String getJniType(Class<?> ptype) {
		if (ptype.isArray()) {
			if (ptype.getComponentType().isPrimitive())
				return getJniType(ptype.getComponentType()) + "Array";
			else 
				return "jobjectArray";
		}
		if (ptype.getName().equals("java.lang.String"))
			return "jstring";
		if (ptype.getName().equals("java.lang.Class"))
			return "jclass";
		if (ptype.getName().equals("void"))
			return "void";
		if (ptype.isPrimitive()) {
			return "j" + ptype.getName();
		}
		return "jobject";
	}

	public static String getParamNameList(Constructor<?> constructor) {
		return getParamNameList(constructor.getParameterTypes(), null);
	}
	
	public static String getParamNameList(Method method) {
		return getParamNameList(method.getParameterTypes(), null);
	}
	
	public static String getParamNameList(Class<?>[] paramTypes, String[] paramNames) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < paramTypes.length; i++) {
			String name = paramNames == null ? "arg" + i : paramNames[i];
			sb.append(", ").append(name);
		}
		return sb.toString();
	}
}
