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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import uk.org.lidalia.sysoutslf4j.system.SystemOutput;
import uk.org.lidalia.testutils.SimpleClassloader;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class TestSysOutOverSLF4JInClassLoader extends SysOutOverSLF4JTestCase {

    private final ClassLoader app1ClassLoader = SimpleClassloader.make();

    @Test
    public void sysOutOverSLF4JWorksInsideAnotherClassLoader() throws Exception {
        callSendSystemOutAndErrToSLF4JInClassLoader(app1ClassLoader);

        ISysOutUser sysOutUser1 = newInstanceInClassLoader(ISysOutUser.class, app1ClassLoader, SysOutUser.class, new Class[]{});

        Thread.currentThread().setContextClassLoader(app1ClassLoader);
        sysOutUser1.useSysOut();

        List<?> list1 = getLoggingEvents(app1ClassLoader);
        assertEquals(1, list1.size());
        Class<?> loggingEventClass = app1ClassLoader.loadClass(LoggingEvent.class.getName());
        assertEquals("Logged", loggingEventClass.getDeclaredMethod("getMessage").invoke(list1.get(0)));
    }

    private <E> E newInstanceInClassLoader(
            Class<E> classToReturn, ClassLoader classLoader, Class<? extends E> classToGetInstanceOf,
            Class<?>[] constructorArgTypes, Object... constructorArgs) throws Exception {
        Class<?> class1 = classLoader.loadClass(classToGetInstanceOf.getName());
        Object newInstance = Whitebox.invokeConstructor(class1, constructorArgTypes, constructorArgs);
        return CrossClassLoaderTestUtils.moveToCurrentClassLoader(classToReturn, newInstance);
    }

    static void clearTestLoggerFactory(ClassLoader classLoader) throws Exception {
        Class<?> clazz = classLoader.loadClass(TestSysOutOverSLF4JInClassLoader.class.getName());
        clazz.getDeclaredMethod("clearTestLoggerFactory").invoke(clazz);
    }

    public static void clearTestLoggerFactory() {
        TestLoggerFactory.clear();
    }

    static List<?> getLoggingEvents(ClassLoader classLoader) throws Exception {
        Class<?> testLoggerFactoryClass = classLoader.loadClass(TestLoggerFactory.class.getName());
        Object sysOutUserLogger = testLoggerFactoryClass.getDeclaredMethod("getTestLogger", String.class).invoke(testLoggerFactoryClass, SysOutUser.class.getName());
        Class<?> testLoggerClass = classLoader.loadClass(TestLogger.class.getName());
        Object loggingEvents = testLoggerClass.getDeclaredMethod("getLoggingEvents").invoke(sysOutUserLogger);
        return CrossClassLoaderTestUtils.moveToCurrentClassLoader(List.class, loggingEvents);
    }

    @Test
    public void systemOutStillGoesToSystemOutInClassLoaderThatHasNotSentSysOutToLSF4J() throws Exception {
        OutputStream sysOutMock = setUpMockSystemOutput(SystemOutput.OUT);
        callSendSystemOutAndErrToSLF4JInClassLoader(app1ClassLoader);

        System.out.println("Hello again");

        assertThat(sysOutMock.toString(), containsString("Hello again" + System.getProperty("line.separator")));
    }

    private OutputStream setUpMockSystemOutput(SystemOutput systemOutput) {
        OutputStream sysOutMock = new ByteArrayOutputStream();
        systemOutput.set(new PrintStream(sysOutMock));
        return sysOutMock;
    }

    protected void callSendSystemOutAndErrToSLF4JInClassLoader(ClassLoader classLoader) throws Exception {
        Class<?> sysOutOverSLF4JClass = classLoader.loadClass(SysOutOverSLF4J.class.getName());
        Thread.currentThread().setContextClassLoader(classLoader);
        sysOutOverSLF4JClass.getMethod("sendSystemOutAndErrToSLF4J").invoke(sysOutOverSLF4JClass);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    protected void callStopSendingSystemOutAndErrToSLF4JInClassLoader(ClassLoader classLoader) throws Exception {
        Class<?> sysOutOverSLF4JClass = classLoader.loadClass(SysOutOverSLF4J.class.getName());
        Thread.currentThread().setContextClassLoader(classLoader);
        sysOutOverSLF4JClass.getMethod("stopSendingSystemOutAndErrToSLF4J").invoke(sysOutOverSLF4JClass);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }
}
