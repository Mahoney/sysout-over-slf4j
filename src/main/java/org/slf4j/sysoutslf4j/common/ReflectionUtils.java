package org.slf4j.sysoutslf4j.common;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ReflectionUtils {
	
	public static Object invokeMethod(final String methodName, final Object target) {
		final Method method = getMethod(methodName, target.getClass());
		return invokeMethod(method, target);
	}
	
	public static Object invokeMethod(final String methodName, final Object target, final Class<?> argType, final Object arg) {
		final Method method = getMethod(methodName, target.getClass(), argType);
		return invokeMethod(method, target, arg);
	}

	public static Object invokeStaticMethod(final String methodName, final Class<?> targetClass) {
		final Method method = getMethod(methodName, targetClass);
		return invokeMethod(method, targetClass);
	}

	public static Object invokeStaticMethod(
			final String methodName, final Class<?> targetClass, final Class<?> argType, final Object arg) {
		final Method method = getMethod(methodName, targetClass, argType);
		return invokeMethod(method, targetClass, arg);
	}

	private static Method getMethod(final String methodName, final Class<?> classWithMethod, final Class<?>... argTypes) {
		try {
			return classWithMethod.getDeclaredMethod(methodName, argTypes);
		} catch (NoSuchMethodException noSuchMethodException) {
			throw new WrappedCheckedException(noSuchMethodException);
		}
	}

	public static Object invokeMethod(final Method method, final Object target, final Object... args) {
		try {
			if (!method.isAccessible()) {
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
					public Void run() {
						method.setAccessible(true);
						return null;
					}
				});
			}
			return method.invoke(target, args);
		} catch (Exception exception) {
			throw ExceptionUtils.asRuntimeException(exception);
		}
	}
	
	private ReflectionUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}

	@SuppressWarnings("unchecked")
	public static <E> E wrap(final Object targetPrintStream, final Class<E> interfaceClass) {
		final E result;
		if (interfaceClass.isAssignableFrom(targetPrintStream.getClass())) {
			result = (E) targetPrintStream;
		} else {
			result = (E) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{interfaceClass}, new ProxyingInvocationHandler(targetPrintStream));
		}
		return result;
	}
}
