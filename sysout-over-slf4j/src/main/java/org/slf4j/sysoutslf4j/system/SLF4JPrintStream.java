package org.slf4j.sysoutslf4j.system;

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
 *         made the call, via an SLF4J {@link Logger}.
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
 *         {@link SysOutOverSLF4J} helper class.
 *         </p>
 * 
 *         <p>
 *         Calls to {@link Throwable#printStackTrace()} and
 *         {@link Throwable#printStackTrace(PrintStream)} (in the case that the
 *         PrintStream passed to the latter is either the {@link System#out} or
 *         the {@link System#err} {@link PrintStream}) are handled by
 *         calculating the name of the class which called printStackTrace and
 *         logging using a logger named after that class. This approach has the
 *         unavoidable limitation that each line of the stack trace is likely to
 *         be printed by the logging system with date, logger etc. information.
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
public final class SLF4JPrintStream extends PrintStream { // NOPMD - too many methods but can't be helped

	private final PrintStream originalPrintStream;
	private final SLF4JPrintStreamDelegater delegater;

	SLF4JPrintStream(final PrintStream originalPrintStream, final SLF4JPrintStreamDelegater delegater) {
		// This ByteArrayOutputStream will be unused - we aren't going to touch
		// the super class.
		super(new ByteArrayOutputStream());
		this.originalPrintStream = originalPrintStream;
		this.delegater = delegater;
	}

	@Override
	public void println(final String string) {
		delegater.delegatePrintln(string);
	}

	@Override
	public void println(final Object object) {
		delegater.delegatePrintln(String.valueOf(object));
	}

	@Override
	public void println() {
		delegater.delegatePrintln("");
	}

	@Override
	public void println(final boolean bool) {
		delegater.delegatePrintln(String.valueOf(bool));
	}

	@Override
	public void println(final char character) {
		delegater.delegatePrintln(String.valueOf(character));
	}

	@Override
	public void println(final char[] charArray) {
		delegater.delegatePrintln(String.valueOf(charArray));
	}

	@Override
	public void println(final double doub) {
		delegater.delegatePrintln(String.valueOf(doub));
	}

	@Override
	public void println(final float floa) {
		delegater.delegatePrintln(String.valueOf(floa));
	}

	@Override
	public void println(final int integer) {
		delegater.delegatePrintln(String.valueOf(integer));
	}

	@Override
	public void println(final long lon) {
		delegater.delegatePrintln(String.valueOf(lon));
	}

	@Override
	public PrintStream append(final char character) {
		delegater.delegatePrint(String.valueOf(character));
		return this;
	}

	@Override
	public PrintStream append(final CharSequence csq, final int start, final int end) {
		delegater.delegatePrint(csq.subSequence(start, end).toString());
		return this;
	}

	@Override
	public PrintStream append(final CharSequence csq) {
		delegater.delegatePrint(csq.toString());
		return this;
	}

	@Override
	public boolean checkError() {
		return originalPrintStream.checkError();
	}

	@Override
	protected void setError() {
		originalPrintStream.println("WARNING - calling setError on SLFJPrintStream does nothing");
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
		delegater.delegatePrint(string);
		return this;
	}

	@Override
	public PrintStream format(final String format, final Object... args) {
		final String string = String.format(format, args);
		delegater.delegatePrint(string);
		return this;
	}

	@Override
	public void print(final boolean bool) {
		delegater.delegatePrint(String.valueOf(bool));
	}

	@Override
	public void print(final char character) {
		delegater.delegatePrint(String.valueOf(character));
	}

	@Override
	public void print(final char[] charArray) {
		delegater.delegatePrint(String.valueOf(charArray));
	}

	@Override
	public void print(final double doubl) {
		delegater.delegatePrint(String.valueOf(doubl));
	}

	@Override
	public void print(final float floa) {
		delegater.delegatePrint(String.valueOf(floa));
	}

	@Override
	public void print(final int integer) {
		delegater.delegatePrint(String.valueOf(integer));
	}

	@Override
	public void print(final long lon) {
		delegater.delegatePrint(String.valueOf(lon));
	}

	@Override
	public void print(final Object object) {
		delegater.delegatePrint(String.valueOf(object));
	}

	@Override
	public void print(final String string) {
		delegater.delegatePrint(String.valueOf(string));
	}

	@Override
	public PrintStream printf(final Locale locale, final String format, final Object... args) {
		final String string = String.format(locale, format, args);
		delegater.delegatePrint(string);
		return this;
	}

	@Override
	public PrintStream printf(final String format, final Object... args) {
		final String string = String.format(format, args);
		delegater.delegatePrint(string);
		return this;
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

	public void registerLoggerAppender(final Object loggerAppender) {
		delegater.registerLoggerAppender(loggerAppender);
	}

	public PrintStream getOriginalPrintStream() {
		return originalPrintStream;
	}
}
