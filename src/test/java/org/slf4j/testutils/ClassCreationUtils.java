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
