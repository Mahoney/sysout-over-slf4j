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

package uk.org.lidalia.sysoutslf4j.context;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, CallOrigin.class, LoggingSystemRegister.class, LoggingOutputStream.class})
public class LoggingOutputStreamTests {

	private static final String CLASS_IN_LOGGING_SYSTEM = "org.logging.LoggerClass";
	private static final String CLASS_NAME = "org.something.SomeClass";

	private LogLevel level = LogLevel.INFO;

	private ExceptionHandlingStrategy exceptionHandlingStrategyMock = mock(ExceptionHandlingStrategy.class);
	private PrintStream origPrintStreamMock = mock(PrintStream.class);
	private Logger loggerMock = mock(Logger.class);
	private LoggingSystemRegister loggingSystemRegisterMock = mock(LoggingSystemRegister.class);
	private LoggingOutputStream outputStream = new LoggingOutputStream(level, exceptionHandlingStrategyMock, origPrintStreamMock, loggingSystemRegisterMock);

    private final Logger loggingOutputStreamLoggerMock = mock(Logger.class);

	@Before
	public void setUp() {
        Whitebox.setInternalState(LoggingOutputStream.class, loggingOutputStreamLoggerMock);
		mockStatic(LoggerFactory.class);
		when(LoggerFactory.getLogger(anyString())).thenReturn(mock(Logger.class));
		when(LoggerFactory.getLogger(CLASS_NAME)).thenReturn(loggerMock);
		
		mockGettingCallOrigin(false, false, CLASS_NAME);
	}

	@Test
	public void flushLogsWhenMessageEndsWithUnixLineBreak() throws Exception {
		outputStream.write("the message\n".getBytes("UTF-8"));
		outputStream.flush();
		verify(loggerMock).info("the message");
	}

	@Test
	public void flushLogsWhenMessageEndsWithWindowsLineBreak() throws Exception {
		outputStream.write("the message\r\n".getBytes("UTF-8"));
		outputStream.flush();
		verify(loggerMock).info("the message");
	}

	@Test
	public void flushWritesToOriginalPrintStreamIfInLoggingSystem() throws Exception {
		mockGettingCallOrigin(false, true, CLASS_IN_LOGGING_SYSTEM);
		
		byte[] bytes = "twelve chars".getBytes("UTF-8");
		outputStream.write(bytes);
		outputStream.flush();
		
		byte[] expected = Arrays.copyOf(bytes, 32);
		verify(origPrintStreamMock).write(expected, 0, 12);
		verify(exceptionHandlingStrategyMock).notifyNotStackTrace();
		verify(exceptionHandlingStrategyMock, never()).handleExceptionLine(anyString(), any(Logger.class));
	}

    @Test
    public void flushWarnsOnceIfInLoggingSystem() throws Exception {
        mockGettingCallOrigin(false, true, CLASS_IN_LOGGING_SYSTEM);

        byte[] bytes = "twelve chars".getBytes("UTF-8");
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.write(bytes);
        outputStream.flush();

        verify(loggingOutputStreamLoggerMock, times(1)).warn(LoggingMessages.PERFORMANCE_WARNING);
    }

	@Test
	public void flushNonStackTraceNotifiesNotStackTrace() throws Exception {
		outputStream.write("some text\n".getBytes("UTF-8"));
		outputStream.flush();
		verify(exceptionHandlingStrategyMock).notifyNotStackTrace();
		verify(exceptionHandlingStrategyMock, never()).handleExceptionLine(anyString(), any(Logger.class));
	}

	@Test
	public void flushStackTraceCallsExceptionHandlingStrategy() throws Exception {
		mockGettingCallOrigin(true, false, CLASS_NAME);
		
		outputStream.write("exception line\n".getBytes("UTF-8"));
		outputStream.flush();
		
		verify(exceptionHandlingStrategyMock).handleExceptionLine("exception line", loggerMock);
		verify(exceptionHandlingStrategyMock, never()).notifyNotStackTrace();
		verifyZeroInteractions(loggerMock);
	}

	@Test
	public void flushResetsBuffer() throws Exception {
		outputStream.write("1".getBytes("UTF-8"));
		outputStream.write("2\n".getBytes("UTF-8"));
		outputStream.flush();
		
		verify(loggerMock).info("12");
		
		outputStream.write("3".getBytes("UTF-8"));
		outputStream.write("4\n".getBytes("UTF-8"));
		outputStream.flush();
		
		verify(loggerMock).info("34");
	}

	private void mockGettingCallOrigin(boolean isStackTrace, boolean inLoggingSystem, String className) {
		CallOrigin callOriginMock = mock(CallOrigin.class);
		when(callOriginMock.isPrintingStackTrace()).thenReturn(isStackTrace);
		when(callOriginMock.getClassName()).thenReturn(className);
		when(callOriginMock.isInLoggingSystem()).thenReturn(inLoggingSystem);

		mockStatic(CallOrigin.class);
		when(CallOrigin.getCallOrigin(loggingSystemRegisterMock)).thenReturn(callOriginMock);
	}
}
