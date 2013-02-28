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

package uk.org.lidalia.sysoutslf4j.system;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.lang.Classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static uk.org.lidalia.test.ShouldThrow.shouldThrow;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PerContextPrintStream.class)
public class PerContextPrintStreamTests {

    private PrintStream originalPrintStreamMock;
    private PerContextStore<PrintStream> perContextStoreMock;
    private PrintStream contextPrintStreamMock;
    private PerContextPrintStream perContextPrintStream;

    @SuppressWarnings("unchecked")
    public void setUpMocks() throws Exception {
        originalPrintStreamMock = mock(PrintStream.class);
        perContextStoreMock = mock(PerContextStore.class);
        contextPrintStreamMock = mock(PrintStream.class);
        when(perContextStoreMock.get()).thenReturn(contextPrintStreamMock);
        whenNew(PerContextStore.class).withArguments(originalPrintStreamMock).thenReturn(perContextStoreMock);
        perContextPrintStream = new PerContextPrintStream(originalPrintStreamMock);
    }

    @Test
    public void allPublicPrintStreamMethodsOnPerContextPrintStreamDelegateToTheSameMethodOnAPrintStreamRetrievedFromTheStore() throws Exception {
        List<Method> printStreamMethods = new ArrayList<Method>(Arrays.asList(PrintStream.class.getMethods()));
        printStreamMethods.removeAll(Arrays.asList(Object.class.getMethods()));

        for (Method method : printStreamMethods) {
            setUpMocks();
            Object[] args = getParameterValuesFor(method);
            if (method.getReturnType() == void.class) {
                assertMethodDelegatesToContextPrintStream(method, args);
            } else {
                assertMethodDelegatesToContextPrintStreamAndReturnsResult(method, args);
            }
        }
    }

    private void assertMethodDelegatesToContextPrintStreamAndReturnsResult(Method method, Object[] args) throws Exception {
        Object result = getValueFor(method.getReturnType());
        when(method.invoke(contextPrintStreamMock, args)).thenReturn(result);

        Object actualResult = method.invoke(perContextPrintStream, args);

        assertEquals(method + " on " + PerContextPrintStream.class +  " return value: ",
                result, actualResult);
    }

    private void assertMethodDelegatesToContextPrintStream(Method method, Object[] args) throws Exception {
        method.invoke(perContextPrintStream, args);
        method.invoke(verify(contextPrintStreamMock), args);
    }

    @Test
    public void setErrorThrowsUnsupportedOperationException() throws Exception {
        setUpMocks();
        UnsupportedOperationException exception = shouldThrow(UnsupportedOperationException.class, new Runnable() {
            @Override
            public void run() {
                perContextPrintStream.setError();
            }
        });
        assertEquals("Setting an error on a PerContextPrintStream does not make sense", exception.getMessage());
    }

    @Test
    public void registerPrintStreamForThisContextDelgatesToStore() throws Exception {
        setUpMocks();
        perContextPrintStream.registerPrintStreamForThisContext(contextPrintStreamMock);
        verify(perContextStoreMock).put(contextPrintStreamMock);
    }

    @Test
    public void deregisterPrintStreamForThisContextDelegatesToStore() throws Exception {
        setUpMocks();
        perContextPrintStream.deregisterPrintStreamForThisContext();
        verify(perContextStoreMock).remove();
    }

    @Test
    public void getOriginalPrintStreamReturnsOriginalPrintStream() {
        perContextPrintStream = new PerContextPrintStream(System.err);
        assertSame(System.err, perContextPrintStream.getOriginalPrintStream());
    }

    private static Object[] getParameterValuesFor(Method method) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Object> results = new ArrayList<Object>();
        for (Class<?> type : parameterTypes) {
            results.add(getValueFor(type));
        }
        return results.toArray();
    }

    private static Object getValueFor(Class<?> type)
            throws InstantiationException, IllegalAccessException {
        Object value;
        if (type == char.class) {
            value = 'a';
        } else if (type == boolean.class) {
            value = true;
        } else if (type == byte.class) {
            value = (byte) 5;
        } else if (type == short.class) {
            value = (short) 10;
        } else if (type == int.class) {
            value = 20;
        } else if (type == long.class) {
            value = 30L;
        } else if (type == float.class) {
            value = 12.0f;
        } else if (type == double.class) {
            value = 24.0f;
        } else if (type.isArray()) {
            value = Array.newInstance(type.getComponentType(), 2);
            Array.set(value, 0, getValueFor(type.getComponentType()));
        } else if (Classes.hasConstructor(type)) {
            value = type.newInstance();
        } else if (type == CharSequence.class) {
            value = "charseq";
        } else if (type == PrintStream.class) {
            value = new PrintStream(new ByteArrayOutputStream());
        } else {
            value = null;
        }
        return value;
    }
}
