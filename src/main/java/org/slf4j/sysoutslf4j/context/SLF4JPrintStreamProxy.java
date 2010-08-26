package org.slf4j.sysoutslf4j.context;

import java.lang.reflect.Proxy;

import org.slf4j.sysoutslf4j.common.ProxyingInvocationHandler;
import org.slf4j.sysoutslf4j.common.SLF4JPrintStream;

final class SLF4JPrintStreamProxy {
	
	static SLF4JPrintStream wrap(final Object targetPrintStream) {
		final SLF4JPrintStream result;
		if (targetPrintStream instanceof SLF4JPrintStream) {
			result = (SLF4JPrintStream) targetPrintStream;
		} else {
			result = (SLF4JPrintStream) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{SLF4JPrintStream.class}, new ProxyingInvocationHandler(targetPrintStream));
		}
		return result;
	}
}
