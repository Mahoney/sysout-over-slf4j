/* 
 * Copyright (c) 2009-2012 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.org.lidalia.sysoutslf4j.integration;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import org.apache.commons.lang3.SerializationUtils;

import uk.org.lidalia.lang.Exceptions;

class CrossClassLoaderTestUtils {

	private static class ReflectionInvocationHandler implements MethodHandler, InvocationHandler {
		
		private final Object target;
		
		private ReflectionInvocationHandler(Object target) {
			this.target = target;
		}
	
		@Override
		public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
			return doWork(method, args);
		}
	
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return doWork(method, args);
		}
		
		private Object doWork(Method method, Object[] args) throws Exception {
			args = args == null ? new Object[0] : args;
			Method targetMethod = target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
			targetMethod.setAccessible(true);
			Object result = targetMethod.invoke(target, args);
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
			Exceptions.throwUnchecked(e);
			throw new AssertionError("Unreachable");
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <E> E createProxyInterface(Class<E> classToProxy, ReflectionInvocationHandler handler) {
		return (E) Proxy.newProxyInstance(CrossClassLoaderTestUtils.class.getClassLoader(), new Class[] {classToProxy}, handler);
	}

	@SuppressWarnings("unchecked")
	private static <E> E createProxyClass(Class<E> classToProxy, ReflectionInvocationHandler handler) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(classToProxy);
		try {
			return (E) proxyFactory.create(classToProxy.getDeclaredConstructors()[0].getParameterTypes(), new Object[classToProxy.getDeclaredConstructors()[0].getParameterTypes().length], handler);
		} catch (Exception e) {
			Exceptions.throwUnchecked(e);
			throw new AssertionError("Unreachable");
		}
	}

	private static Object moveToCurrentClassLoaderViaSerialization(Serializable objectFromOtherClassLoader) {
		try {
			byte[] objectAsBytes = SerializationUtils.serialize(objectFromOtherClassLoader);
            return SerializationUtils.deserialize(objectAsBytes);
		} catch (Exception e) {
			Exceptions.throwUnchecked(e);
			throw new AssertionError("Unreachable");
		}
	}
}
