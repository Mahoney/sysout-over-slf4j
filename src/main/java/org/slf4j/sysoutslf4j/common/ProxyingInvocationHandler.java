package org.slf4j.sysoutslf4j.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProxyingInvocationHandler implements InvocationHandler {

	private final Object target;
	private final Map<Method, Method> methods = new HashMap<Method, Method>();
	
	ProxyingInvocationHandler(Object target, Class<?> interfaceClass) {
		this.target = target;
		for (Method method : interfaceClass.getMethods()) {
			try {
				Method methodOnTarget = target.getClass().getMethod(method.getName(), method.getParameterTypes());
				methods.put(method, methodOnTarget);
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Target " + target + " does not have methods to match all method signatures on class " + interfaceClass);
			}
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method methodOnTarget = methods.get(method);
		return methodOnTarget.invoke(target, args);
	}
	
	public Object getTarget() {
		return target;
	}
}
