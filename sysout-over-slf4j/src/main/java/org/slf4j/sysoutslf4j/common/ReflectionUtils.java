package org.slf4j.sysoutslf4j.common;

import java.lang.reflect.Method;

public class ReflectionUtils {
	
	public static Object invokeMethod(String methodName, Object target) {
		Method method = getMethod(methodName, target.getClass());
		return invokeMethod(method, target);
	}
	
	public static Object invokeMethod(String methodName, Object target, Class<?> argType, Object arg) {
		Method method = getMethod(methodName, target.getClass(), argType);
		return invokeMethod(method, target, arg);
	}
	
	public static Object invokeStaticMethod(String methodName, Class<?> targetClass) {
		Method method = getMethod(methodName, targetClass);
		return invokeMethod(method, targetClass);
	}

	public static Object invokeStaticMethod(String methodName, Class<?> targetClass, Class<?> argType, Object arg) {
		Method method = getMethod(methodName, targetClass, argType);
		return invokeMethod(method, targetClass, arg);
	}

	private static Method getMethod(String methodName, Class<?> classWithMethod, Class<?>... argTypes) {
		try {
			return classWithMethod.getDeclaredMethod(methodName, argTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (Exception e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}
	
	private ReflectionUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
