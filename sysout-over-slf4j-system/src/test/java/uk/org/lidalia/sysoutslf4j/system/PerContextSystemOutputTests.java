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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({PerContextPrintStream.class, PerContextSystemOutput.class})
public class PerContextSystemOutputTests extends SysOutOverSLF4JTestCase {

    @Test
    public void isPerContextPrintStreamReturnsFalseWhenSystemOutIsPerContextPrintStream() {
        assertFalse(PerContextSystemOutput.OUT.isPerContextPrintStream());
        assertFalse(PerContextSystemOutput.ERR.isPerContextPrintStream());
    }

    @Test
    public void isPerContextPrintStreamReturnsTrueWhenSystemOutIsPerContextPrintStream() {
        System.setOut(new PerContextPrintStream(System.out));
        assertTrue(PerContextSystemOutput.OUT.isPerContextPrintStream());

        System.setErr(new PerContextPrintStream(System.err));
        assertTrue(PerContextSystemOutput.ERR.isPerContextPrintStream());
    }

    @Test
    public void restoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream() {
        assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
        assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PrintStream original = output.get();
        perContextOutput.restoreOriginalPrintStream();
        assertSame(original, output.get());
    }

    @Test
    public void restoreOriginalPrintStreamDoesNothingIfOutputIsNotPerContextPrintStream() {
        assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotPerContextPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
        assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotPerContextPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotPerContextPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PrintStream other = new PrintStream(new ByteArrayOutputStream());
        output.set(other);
        perContextOutput.restoreOriginalPrintStream();
        assertSame(other, output.get());
    }

    @Test
    public void restoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsPerContextPrintStream() {
        assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsPerContextPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
        assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsPerContextPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsPerContextPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PrintStream original = output.get();
        output.set(new PerContextPrintStream(output.get()));
        perContextOutput.restoreOriginalPrintStream();
        assertSame(original, output.get());
    }

    @Test
    public void getOriginalPrintStreamReturnsOriginalWhenOutputIsOriginalPrintStream() {
        assertSame(SystemOutput.OUT.get(), PerContextSystemOutput.OUT.getOriginalPrintStream());
        assertSame(SystemOutput.ERR.get(), PerContextSystemOutput.ERR.getOriginalPrintStream());
    }

    @Test
    public void getOriginalPrintStreamReturnsCurrentWhenOutputIsNotPerContextPrintStream() {
        getOriginalPrintStreamReturnsCurrentWhenOutputIsNotPerContextPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
        getOriginalPrintStreamReturnsCurrentWhenOutputIsNotPerContextPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void getOriginalPrintStreamReturnsCurrentWhenOutputIsNotPerContextPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PrintStream other = new PrintStream(new ByteArrayOutputStream());
        output.set(other);
        assertSame(other, perContextOutput.getOriginalPrintStream());
    }

    @Test
    public void getOriginalPrintStreamReturnsOriginalWhenOutputIsPerContextPrintStream() {
        getOriginalPrintStreamReturnsOriginalWhenOutputIsPerContextPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
        getOriginalPrintStreamReturnsOriginalWhenOutputIsPerContextPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void getOriginalPrintStreamReturnsOriginalWhenOutputIsPerContextPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PrintStream original = output.get();
        output.set(new PerContextPrintStream(output.get()));
        assertSame(original, perContextOutput.getOriginalPrintStream());
    }

    @Test
    public void registerLoggerAppenderMakesPerContextPrintStreamAndRegistersLoggerAppenderIfSysOutIsNotPerContextPrintStream() throws Exception {
        registerLoggerAppenderMakesPerContextPrintStreamAndRegistersLoggerAppenderIfOutputIsNotPerContextPrintStream(
                SystemOutput.OUT, PerContextSystemOutput.OUT);
    }

    @Test
    public void registerLoggerAppenderMakesPerContextPrintStreamAndRegistersLoggerAppenderIfSysErrIsNotPerContextPrintStream() throws Exception {
        registerLoggerAppenderMakesPerContextPrintStreamAndRegistersLoggerAppenderIfOutputIsNotPerContextPrintStream(
                SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void registerLoggerAppenderMakesPerContextPrintStreamAndRegistersLoggerAppenderIfOutputIsNotPerContextPrintStream(
            final SystemOutput output, final PerContextSystemOutput perContextOutput) throws Exception {
        PrintStream original = output.get();
        PrintStream toRegister = new PrintStream(new ByteArrayOutputStream());
        PerContextPrintStream perContextPrintStreamMock = mock(PerContextPrintStream.class);

        whenNew(PerContextPrintStream.class).withArguments(original).thenReturn(perContextPrintStreamMock);

        perContextOutput.registerPrintStreamForThisContext(toRegister);

        assertSame(perContextPrintStreamMock, output.get());
        verify(perContextPrintStreamMock).registerPrintStreamForThisContext(toRegister);
    }

    @Test
    public void registerLoggerAppenderRegistersLoggerAppenderIfSystemOutIsPerContextPrintStream() {
        registerLoggerAppenderRegistersLoggerAppenderIfOutputIsPerContextPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
    }

    @Test
    public void registerLoggerAppenderRegistersLoggerAppenderIfSystemErrIsPerContextPrintStream() {
        registerLoggerAppenderRegistersLoggerAppenderIfOutputIsPerContextPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void registerLoggerAppenderRegistersLoggerAppenderIfOutputIsPerContextPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PerContextPrintStream perContextPrintStreamMock = mock(PerContextPrintStream.class);
        PrintStream toRegister = new PrintStream(new ByteArrayOutputStream());

        output.set(perContextPrintStreamMock);
        perContextOutput.registerPrintStreamForThisContext(toRegister);

        verify(perContextPrintStreamMock).registerPrintStreamForThisContext(toRegister);
    }

    @Test
    public void deregisterLoggerAppenderDoesNothingIfOutputIsNotPerContextPrintStream() {
        PerContextSystemOutput.OUT.deregisterPrintStreamForThisContext();
        PerContextSystemOutput.ERR.deregisterPrintStreamForThisContext();
        // Nothing happens
    }

    @Test
    public void deregisterLoggerAppenderDeregistersAppenderIfSystemOutIsPerContextPrintStream() {
        deregisterLoggerAppenderDeregistersAppenderIfOutputIsPerContextPrintStream(SystemOutput.OUT, PerContextSystemOutput.OUT);
    }

    @Test
    public void deregisterLoggerAppenderDeregistersAppenderIfSystemErrIsPerContextPrintStream() {
        deregisterLoggerAppenderDeregistersAppenderIfOutputIsPerContextPrintStream(SystemOutput.ERR, PerContextSystemOutput.ERR);
    }

    private void deregisterLoggerAppenderDeregistersAppenderIfOutputIsPerContextPrintStream(SystemOutput output, PerContextSystemOutput perContextOutput) {
        PerContextPrintStream perContextPrintStreamMock = mock(PerContextPrintStream.class);

        output.set(perContextPrintStreamMock);
        perContextOutput.deregisterPrintStreamForThisContext();

        verify(perContextPrintStreamMock).deregisterPrintStreamForThisContext();
    }
}
