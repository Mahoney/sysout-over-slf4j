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
public final class PerContextPrintStream extends PrintStream { // NOPMD superclass has too many methods

	private final PerContextStore<PrintStream> printStreamStore;

	PerContextPrintStream(final PrintStream originalPrintStream) {
		// This ByteArrayOutputStream will be unused - we aren't going to touch
		// the super class.
		super(new ByteArrayOutputStream());
		this.printStreamStore = new PerContextStore<PrintStream>(originalPrintStream);
	}

	@Override
	public synchronized void println(final String string) {
		printStreamStore.get().println(string);
	}

	@Override
	public synchronized void println(final Object object) {
		printStreamStore.get().println(object);
	}

	@Override
	public synchronized void println() {
		printStreamStore.get().println();
	}

	@Override
	public synchronized void println(final boolean bool) {
		printStreamStore.get().println(bool);
	}

	@Override
	public synchronized void println(final char character) {
		printStreamStore.get().println(character);
	}

	@Override
	public synchronized void println(final char[] charArray) {
		printStreamStore.get().println(charArray);
	}

	@Override
	public synchronized void println(final double doub) {
		printStreamStore.get().println(doub);
	}

	@Override
	public synchronized void println(final float floa) {
		printStreamStore.get().println(floa);
	}

	@Override
	public synchronized void println(final int integer) {
		printStreamStore.get().println(integer);
	}

	@Override
	public synchronized void println(final long lon) {
		printStreamStore.get().println(lon);
	}

	@Override
	public synchronized PrintStream append(final char character) {
		return printStreamStore.get().append(character); //QUERY should we return the delegate or the top level PrintStream?
	}

	@Override
	public synchronized PrintStream append(final CharSequence csq, final int start, final int end) {
		return printStreamStore.get().append(csq, start, end);
	}

	@Override
	public synchronized PrintStream append(final CharSequence csq) {
		return printStreamStore.get().append(csq);
	}

	@Override
	public boolean checkError() {
		return printStreamStore.get().checkError();
	}

	@Override
	protected void setError() {
		throw new UnsupportedOperationException("Setting an error on a PerContextPrintStream does not make sense");
	}

	@Override
	public synchronized void close() {
		printStreamStore.get().close();
	}

	@Override
	public synchronized void flush() {
		printStreamStore.get().flush();
	}

	@Override
	public synchronized PrintStream format(final Locale locale, final String format, final Object... args) {
		return printStreamStore.get().format(locale, format, args);
	}

	@Override
	public synchronized PrintStream format(final String format, final Object... args) {
		return printStreamStore.get().format(format, args);
	}

	@Override
	public synchronized void print(final boolean bool) {
		printStreamStore.get().print(bool);
	}

	@Override
	public synchronized void print(final char character) {
		printStreamStore.get().print(character);
	}

	@Override
	public synchronized void print(final char[] charArray) {
		printStreamStore.get().print(charArray);
	}

	@Override
	public synchronized void print(final double doubl) {
		printStreamStore.get().print(doubl);
	}

	@Override
	public synchronized void print(final float floa) {
		printStreamStore.get().print(floa);
	}

	@Override
	public synchronized void print(final int integer) {
		printStreamStore.get().print(integer);
	}

	@Override
	public synchronized void print(final long lon) {
		printStreamStore.get().print(lon);
	}

	@Override
	public synchronized void print(final Object object) {
		printStreamStore.get().print(object);
	}

	@Override
	public synchronized void print(final String string) {
		printStreamStore.get().print(string);
	}

	@Override
	public synchronized PrintStream printf(final Locale locale, final String format, final Object... args) {
		return printStreamStore.get().printf(locale, format, args);
	}

	@Override
	public synchronized PrintStream printf(final String format, final Object... args) {
		return printStreamStore.get().printf(format, args);
	}

	@Override
	public synchronized void write(final byte[] buf, final int off, final int len) {
		printStreamStore.get().write(buf, off, len);
	}

	@Override
	public synchronized void write(final int integer) {
		printStreamStore.get().write(integer);
	}

	@Override
	public synchronized void write(final byte[] bytes) throws IOException {
		printStreamStore.get().write(bytes);
	}

	void registerPrintStreamForThisContext(final PrintStream printStreamForThisContext) {
		printStreamStore.put(printStreamForThisContext);
	}

	void deregisterPrintStreamForThisContext() {
		printStreamStore.remove();
	}

	PrintStream getOriginalPrintStream() {
		return printStreamStore.getDefaultValue();
	}
}
