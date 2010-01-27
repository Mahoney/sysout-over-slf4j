package org.slf4j.testutils;

import javassist.ClassPool;
import javassist.CtClass;

public final class ClassCreationUtils {

	public static Class<?> makeClass(String className, Class<?>... interfaces) throws Exception {
		final ClassPool pool = ClassPool.getDefault();
		CtClass cc;
		try {
			cc = pool.getCtClass(className);
		} catch (Exception e) {
			cc = pool.makeClass(className);
			CtClass[] interfaceCtClasses = new CtClass[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				interfaceCtClasses[i] = pool.getCtClass(interfaces[i].getName());
			}
			cc.setInterfaces(interfaceCtClasses);
		}
		Class<?> newClass = cc.toClass(ClassCreationUtils.class.getClassLoader(), null);
		return newClass;
	}
}
