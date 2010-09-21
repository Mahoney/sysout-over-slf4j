/* 
 * Copyright (c) 2009-2010 Robert Elliot
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

package uk.org.lidalia.sysoutslf4j.common;

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
			if (classWithMethod.getSuperclass() == null) {
				throw new WrappedCheckedException(noSuchMethodException);
			} else {
				return getMethod(methodName, classWithMethod.getSuperclass(), argTypes);
			}
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
	public static <TypeInThisClassLoader> TypeInThisClassLoader wrap(
			final Object target, final Class<TypeInThisClassLoader> interfaceClass) {
		final TypeInThisClassLoader result;
		if (interfaceClass.isAssignableFrom(target.getClass())) {
			result = (TypeInThisClassLoader) target;
		} else {
			result = (TypeInThisClassLoader) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
					new Class[]{interfaceClass}, new ProxyingInvocationHandler(target, interfaceClass));
		}
		return result;
	}
}
