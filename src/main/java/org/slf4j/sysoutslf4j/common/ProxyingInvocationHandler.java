package org.slf4j.sysoutslf4j.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyingInvocationHandler implements InvocationHandler {

	private final Object target;
	
	public ProxyingInvocationHandler(Object target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method methodOnTarget = target.getClass().getMethod(method.getName(), method.getParameterTypes());
		return ReflectionUtils.invokeMethod(methodOnTarget, target, args);
	}
}
