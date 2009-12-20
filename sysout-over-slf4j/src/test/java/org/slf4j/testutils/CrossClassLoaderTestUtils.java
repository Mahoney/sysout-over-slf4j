package org.slf4j.testutils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.sysoutslf4j.common.ExceptionUtils;
import org.slf4j.sysoutslf4j.common.ReflectionUtils;

public class CrossClassLoaderTestUtils {

	private static class ReflectionInvocationHandler implements MethodHandler, InvocationHandler {
		
		private final Object target;
		
		private ReflectionInvocationHandler(Object target) {
			this.target = target;
		}
	
		public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
			return doWork(method, args);
		}
	
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return doWork(method, args);
		}
		
		private Object doWork(Method method, Object[] args) throws Exception {
			Method targetMethod = target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
			targetMethod.setAccessible(true);
			Object result = ReflectionUtils.invokeMethod(targetMethod, target, args);
			if (result == null) {
				return null;
			}
			Class<?> resultClass = method.getReturnType();
			if (resultClass.isPrimitive()) {
				return result;
			} else {
				String resultClassName = resultClass.getName();
				Class<?> resultClassLoadedInThisClassLoader = getClass().getClassLoader().loadClass(resultClassName);
				return moveToCurrentClassLoader(resultClassLoadedInThisClassLoader, result);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <E> E moveToCurrentClassLoader(Class<E> destinationClass, Object target) {
		if (target.getClass().isPrimitive()) {
			return (E) target;
		} else if (destinationClass.isAssignableFrom(target.getClass())) {
			return (E) target;
		} else if (destinationClass.isInterface()) {
			return createProxyInterface(destinationClass, new ReflectionInvocationHandler(target));
		} else if (target instanceof Enum<?>) {
			return (E) getLocalEnumInstance((Enum<?>) target, destinationClass);
		} else if (Modifier.isFinal(destinationClass.getModifiers())) {
			if (target instanceof Serializable) {
				return (E) moveToCurrentClassLoaderViaSerialization((Serializable) target);
			} else {
				return (E) target;
			}
		} else {
			return createProxyClass(destinationClass, new ReflectionInvocationHandler(target));
		}
	}

	@SuppressWarnings("unchecked")
	private static Object getLocalEnumInstance(Enum<?> enumInstance, Class destinationClass) {
		try {
			return Enum.valueOf(destinationClass, enumInstance.name());
		} catch (Exception e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <E> E createProxyInterface(Class<E> classToProxy, ReflectionInvocationHandler handler) {
		return (E) Proxy.newProxyInstance(ReflectionUtils.class.getClassLoader(), new Class[] {classToProxy}, handler);
	}

	@SuppressWarnings("unchecked")
	private static <E> E createProxyClass(Class<E> classToProxy, ReflectionInvocationHandler handler) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(classToProxy);
		try {
			return (E) proxyFactory.create(classToProxy.getDeclaredConstructors()[0].getParameterTypes(), new Object[classToProxy.getDeclaredConstructors()[0].getParameterTypes().length], handler);
		} catch (Exception e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}

	private static Object moveToCurrentClassLoaderViaSerialization(Serializable objectFromOtherClassLoader) {
		try {
			byte[] objectAsBytes = SerializationUtils.serialize(objectFromOtherClassLoader);
			Object object = SerializationUtils.deserialize(objectAsBytes);
			return object;
		} catch (Exception e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}
}
