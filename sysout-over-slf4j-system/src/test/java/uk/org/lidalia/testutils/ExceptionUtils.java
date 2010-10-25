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

package uk.org.lidalia.testutils;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public final class ExceptionUtils {

	public static RuntimeException asRuntimeException(final Throwable throwable) {
		final RuntimeException result;
		if (throwable == null) {
			throw new IllegalArgumentException("Throwable argument cannot be null");
		} else if (throwable instanceof Error) {
			throw (Error) throwable;
		} else if (throwable instanceof RuntimeException) {
			result = (RuntimeException) throwable;
		} else if (throwable instanceof InterruptedException || throwable instanceof InterruptedIOException) {
			throw new IllegalArgumentException(
					"An interrupted exception needs to be handled to end the thread, or the interrupted status needs to be " +
					"restored, or the exception needs to be propagated explicitly - it should not be used as an argument to " +
					"this method", throwable);
		} else if (throwable instanceof InvocationTargetException) {
			result = asRuntimeException(throwable.getCause());
		} else {
			result = new WrappedCheckedException(throwable);
		}
		return result;
	}

	public static <ResultType> ResultType doUnchecked(final Callable<ResultType> work) {
		try {
			return work.call();
		} catch (Exception e) {
			throw asRuntimeException(e);
		}
	}

	private ExceptionUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
