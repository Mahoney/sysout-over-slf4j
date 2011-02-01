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

package uk.org.lidalia.sysoutslf4j.system;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 * 
 * @author Robert Elliot
 * 
 *         <p>
 *         Instances of this class wrap the existing {@link System#out} or
 *         {@link System#err} {@link PrintStream}s. They intercept all calls to
 *         System.out and System.err and log them at info (for System.out) or
 *         error (for System.err) level on a logger named after the class which
 *         made the call, via an SLF4J {@link org.slf4j.Logger}.
 *         </p>
 * 
 *         <p>
 *         The sole exceptions are calls to {@link #write(byte[])},
 *         {@link #write(int)} and {@link #write(byte[], int, int)}, which pass
 *         the calls through to the original PrintStream on the basis that these
 *         methods are highly unlikely to be used for "normal" printing to the
 *         Console but are used by major logging frameworks in their Console
 *         appenders. This allows this interceptor to have a minimal impact for
 *         most logging frameworks when sending output to the Console.
 *         </p>
 * 
 *         <p>
 *         Instances should only be created and assigned by the
 *         {@link uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J} helper class.
 *         </p>
 * 
 *         <p>
 *         Calls to {@link Throwable#printStackTrace()} and
 *         {@link Throwable#printStackTrace(PrintStream)} (in the case that the
 *         PrintStream passed to the latter is either the {@link System#out} or
 *         the {@link System#err} {@link PrintStream}) are handled by
 *         calculating the name of the class which called printStackTrace and
 *         logging using a logger named after that class.
 *         </p>
 * 
 *         <p>
 *         It is important to note that there are performance overheads for
 *         every call to the methods other than write on System.out and
 *         System.err using this proxy. It is intended for use with legacy
 *         compiled code that has a few calls to System.out/err; it is not
 *         intended to encourage use of System.out/err in preference to Loggers
 *         in new code. The assumption is that no legacy code prints to
 *         System.out or System.err on such a regular basis that the performance
 *         hit is heavy, otherwise that code would be rendering the console
 *         unusable and itself be a performance drain.
 *         </p>
 * 
 *         <p>
 *         There should be hardly any performance implications for calls to the
 *         write methods on System.out/err. LogBack, Log4J and JULI
 *         ConsoleAppenders all use the write methods on System.out/err, and so
 *         there should be minimal performance overhead for them.
 *         </p>
 * 
 *         <p>
 *         Where an existing logging system needs to use println on
 *         System.out/err, this is handled without redirecting that call back to
 *         SLF4J, though a performance hit will occur.
 *         </p>
 */
final class SLF4JPrintStream extends PrintStream { // NOPMD superclass has too many methods

	private final PrintStream originalPrintStream;
	private final SLF4JPrintStreamDelegate delegate;

	SLF4JPrintStream(final PrintStream originalPrintStream, final SLF4JPrintStreamDelegate delegate) {
		// This ByteArrayOutputStream will be unused - we aren't going to touch
		// the super class.
		super(new ByteArrayOutputStream());
		this.originalPrintStream = originalPrintStream;
		this.delegate = delegate;
	}

	@Override
	public void println(final String string) {
		doPrintln(string);
	}

	@Override
	public void println(final Object object) {
		doPrintln(String.valueOf(object));
	}

	@Override
	public void println() {
		doPrintln("");
	}

	@Override
	public void println(final boolean bool) {
		doPrintln(String.valueOf(bool));
	}

	@Override
	public void println(final char character) {
		doPrintln(String.valueOf(character));
	}

	@Override
	public void println(final char[] charArray) {
		doPrintln(String.valueOf(charArray));
	}

	@Override
	public void println(final double doub) {
		doPrintln(String.valueOf(doub));
	}

	@Override
	public void println(final float floa) {
		doPrintln(String.valueOf(floa));
	}

	@Override
	public void println(final int integer) {
		doPrintln(String.valueOf(integer));
	}

	@Override
	public void println(final long lon) {
		doPrintln(String.valueOf(lon));
	}

	@Override
	public PrintStream append(final char character) {
		doPrint(String.valueOf(character));
		return this;
	}

	@Override
	public PrintStream append(final CharSequence csq, final int start, final int end) {
		doPrint(csq.subSequence(start, end).toString());
		return this;
	}

	@Override
	public PrintStream append(final CharSequence csq) {
		doPrint(csq.toString());
		return this;
	}

	@Override
	public boolean checkError() {
		return originalPrintStream.checkError();
	}

	@Override
	protected void setError() {
		originalPrintStream.checkError();
	}

	@Override
	public void close() {
		originalPrintStream.close();
	}

	@Override
	public void flush() {
		originalPrintStream.flush();
	}

	@Override
	public PrintStream format(final Locale locale, final String format, final Object... args) {
		final String string = String.format(locale, format, args);
		doPrint(string);
		return this;
	}

	@Override
	public PrintStream format(final String format, final Object... args) {
		return format(Locale.getDefault(), format, args);
	}

	@Override
	public void print(final boolean bool) {
		doPrint(String.valueOf(bool));
	}

	@Override
	public void print(final char character) {
		doPrint(String.valueOf(character));
	}

	@Override
	public void print(final char[] charArray) {
		doPrint(String.valueOf(charArray));
	}

	@Override
	public void print(final double doubl) {
		doPrint(String.valueOf(doubl));
	}

	@Override
	public void print(final float floa) {
		doPrint(String.valueOf(floa));
	}

	@Override
	public void print(final int integer) {
		doPrint(String.valueOf(integer));
	}

	@Override
	public void print(final long lon) {
		doPrint(String.valueOf(lon));
	}

	@Override
	public void print(final Object object) {
		doPrint(String.valueOf(object));
	}

	@Override
	public void print(final String string) {
		doPrint(String.valueOf(string));
	}

	@Override
	public PrintStream printf(final Locale locale, final String format, final Object... args) {
		return format(locale, format, args);
	}

	@Override
	public PrintStream printf(final String format, final Object... args) {
		return format(format, args);
	}

	@Override
	public void write(final byte[] buf, final int off, final int len) {
		originalPrintStream.write(buf, off, len);
	}

	@Override
	public void write(final int integer) {
		originalPrintStream.write(integer);
	}

	@Override
	public void write(final byte[] bytes) throws IOException {
		originalPrintStream.write(bytes);
	}
	
	private synchronized void doPrint(String string) {
		delegate.delegatePrint(string);
	}
	
	private synchronized void doPrintln(String string) {
		delegate.delegatePrintln(string);
	}

	public void registerLoggerAppender(final LoggerAppender loggerAppender) {
		delegate.registerLoggerAppender(loggerAppender);
	}

	public void deregisterLoggerAppender() {
		delegate.deregisterLoggerAppender();
	}

	public PrintStream getOriginalPrintStream() {
		return originalPrintStream;
	}
}
