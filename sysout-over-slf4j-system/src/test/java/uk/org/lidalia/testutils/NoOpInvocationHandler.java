package uk.org.lidalia.testutils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class NoOpInvocationHandler implements InvocationHandler {
	
	public static NoOpInvocationHandler INSTANCE = new NoOpInvocationHandler();
	
	private NoOpInvocationHandler() {
		super();
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return null;
	}
}
