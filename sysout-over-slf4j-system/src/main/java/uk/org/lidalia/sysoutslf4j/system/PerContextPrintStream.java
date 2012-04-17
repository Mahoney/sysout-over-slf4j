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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

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
