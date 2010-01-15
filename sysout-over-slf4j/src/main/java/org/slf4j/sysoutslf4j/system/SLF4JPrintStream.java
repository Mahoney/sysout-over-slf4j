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
public final class SLF4JPrintStream extends PrintStream {

	private final PrintStream originalPrintStream;
	private final SLF4JPrintStreamDelegater delegater;

	SLF4JPrintStream(PrintStream originalPrintStream, SLF4JPrintStreamDelegater delegater) {
		// This ByteArrayOutputStream will be unused - we aren't going to touch
		// the super class.
		super(new ByteArrayOutputStream());
		this.originalPrintStream = originalPrintStream;
		this.delegater = delegater;
	}

	@Override
	public void println(String s) {
		delegater.delegatePrintln(s);
	}

	@Override
	public void println(Object x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println() {
		delegater.delegatePrintln(String.valueOf(""));
	}

	@Override
	public void println(boolean x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println(char x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println(char[] x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println(double x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println(float x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println(int x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public void println(long x) {
		delegater.delegatePrintln(String.valueOf(x));
	}

	@Override
	public PrintStream append(char c) {
		delegater.delegatePrint(String.valueOf(c));
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		delegater.delegatePrint(csq.subSequence(start, end).toString());
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
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
	public PrintStream format(Locale l, String format, Object... args) {
		String s = String.format(l, format, args);
		delegater.delegatePrint(s);
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		String s = String.format(format, args);
		delegater.delegatePrint(s);
		return this;
	}

	@Override
	public void print(boolean b) {
		delegater.delegatePrint(String.valueOf(b));
	}

	@Override
	public void print(char c) {
		delegater.delegatePrint(String.valueOf(c));
	}

	@Override
	public void print(char[] s) {
		delegater.delegatePrint(String.valueOf(s));
	}

	@Override
	public void print(double d) {
		delegater.delegatePrint(String.valueOf(d));
	}

	@Override
	public void print(float f) {
		delegater.delegatePrint(String.valueOf(f));
	}

	@Override
	public void print(int i) {
		delegater.delegatePrint(String.valueOf(i));
	}

	@Override
	public void print(long l) {
		delegater.delegatePrint(String.valueOf(l));
	}

	@Override
	public void print(Object obj) {
		delegater.delegatePrint(String.valueOf(obj));
	}

	@Override
	public void print(String s) {
		delegater.delegatePrint(String.valueOf(s));
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		String s = String.format(l, format, args);
		delegater.delegatePrint(s);
		return this;
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		String s = String.format(format, args);
		delegater.delegatePrint(s);
		return this;
	}
	
	@Override
	public synchronized void write(byte[] buf, int off, int len) {
		originalPrintStream.write(buf, off, len);
	}

	@Override
	public synchronized void write(int b) {
		originalPrintStream.write(b);
	}

	@Override
	public synchronized void write(byte[] b) throws IOException {
		originalPrintStream.write(b);
	}

	public void registerLoggerAppender(Object loggerAppender) {
		delegater.registerLoggerAppender(loggerAppender);
	}

	public PrintStream getOriginalPrintStream() {
		return originalPrintStream;
	}
}
