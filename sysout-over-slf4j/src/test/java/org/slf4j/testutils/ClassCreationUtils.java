package org.slf4j.testutils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

public final class ClassCreationUtils {

	public static Class<?> makeClass(String className, Class<?>... interfaces) throws Exception {
		
		final ClassPool pool = ClassPool.getDefault();
		LoaderClassPath ccPath = new LoaderClassPath(Thread.currentThread().getContextClassLoader());
		pool.insertClassPath(ccPath);
		
		CtClass cc;
		try {
			cc = pool.get(className);
		} catch (Exception e) {
			cc = pool.makeClass(className);
			CtClass[] interfaceCtClasses = new CtClass[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				interfaceCtClasses[i] = pool.get(interfaces[i].getName());
			}
			cc.setInterfaces(interfaceCtClasses);
		}
		Class<?> newClass = cc.toClass(ClassCreationUtils.class.getClassLoader(), null);
		return newClass;
	}
}
