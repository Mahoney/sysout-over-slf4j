package uk.org.lidalia.sysoutslf4j.system;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.org.lidalia.sysoutslf4j.SysOutOverSLF4JTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SLF4JPrintStream.class)
public class SLF4JSystemOutputTests extends SysOutOverSLF4JTestCase {

	@Test
	public void isSLF4JPrintStreamReturnsFalseWhenSystemOutIsSLF4JPrintStream() {
		assertFalse(SLF4JSystemOutput.OUT.isSLF4JPrintStream());
		assertFalse(SLF4JSystemOutput.ERR.isSLF4JPrintStream());
	}

	@Test
	public void isSLF4JPrintStreamReturnsTrueWhenSystemOutIsSLF4JPrintStream() {
		System.setOut(new SLF4JPrintStream(System.out, null));
		assertTrue(SLF4JSystemOutput.OUT.isSLF4JPrintStream());
		
		System.setErr(new SLF4JPrintStream(System.err, null));
		assertTrue(SLF4JSystemOutput.ERR.isSLF4JPrintStream());
	}

	@Test
	public void restoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream() {
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput.OUT, SLF4JSystemOutput.OUT);
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput.ERR, SLF4JSystemOutput.ERR);
	}

	private void assertRestoreOriginalPrintStreamDoesNothingIfOutputIsOriginalPrintStream(SystemOutput output, SLF4JSystemOutput slf4jOutput) {
		PrintStream original = output.get();
		slf4jOutput.restoreOriginalPrintStream();
		assertSame(original, output.get());
	}

	@Test
	public void restoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream() {
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream(SystemOutput.OUT, SLF4JSystemOutput.OUT);
		assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream(SystemOutput.ERR, SLF4JSystemOutput.ERR);
	}

	private void assertRestoreOriginalPrintStreamDoesNothingIfOutputIsNotSLF4JPrintStream(SystemOutput output, SLF4JSystemOutput slf4jOutput) {
		PrintStream other = new PrintStream(new ByteArrayOutputStream());
		output.set(other);
		slf4jOutput.restoreOriginalPrintStream();
		assertSame(other, output.get());
	}

	@Test
	public void restoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream() {
		assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream(SystemOutput.OUT, SLF4JSystemOutput.OUT);
		assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream(SystemOutput.ERR, SLF4JSystemOutput.ERR);
	}

	private void assertRestoreOriginalPrintStreamRestoresOriginalPrintStreamIfOutputIsSLF4JPrintStream(SystemOutput output, SLF4JSystemOutput slf4jOutput) {
		PrintStream original = output.get();
		output.set(new SLF4JPrintStream(output.get(), null));
		slf4jOutput.restoreOriginalPrintStream();
		assertSame(original, output.get());
	}

	@Test
	public void getOriginalPrintStreamReturnsOriginalWhenOutputIsOriginalPrintStream() {
		assertSame(SystemOutput.OUT.get(), SLF4JSystemOutput.OUT.getOriginalPrintStream());
		assertSame(SystemOutput.ERR.get(), SLF4JSystemOutput.ERR.getOriginalPrintStream());
	}

	@Test
	public void getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream() {
		getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream(SystemOutput.OUT, SLF4JSystemOutput.OUT);
		getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream(SystemOutput.ERR, SLF4JSystemOutput.ERR);
	}

	private void getOriginalPrintStreamReturnsCurrentWhenOutputIsNotSLF4JPrintStream(SystemOutput output, SLF4JSystemOutput slf4jOutput) {
		PrintStream other = new PrintStream(new ByteArrayOutputStream());
		output.set(other);
		assertSame(other, slf4jOutput.getOriginalPrintStream());
	}

	@Test
	public void getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream() {
		getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream(SystemOutput.OUT, SLF4JSystemOutput.OUT);
		getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream(SystemOutput.ERR, SLF4JSystemOutput.ERR);
	}
	
	private void getOriginalPrintStreamReturnsOriginalWhenOutputIsSLF4JPrintStream(SystemOutput output, SLF4JSystemOutput slf4jOutput) {
		PrintStream original = output.get();
		output.set(new SLF4JPrintStream(output.get(), null));
		assertSame(original, slf4jOutput.getOriginalPrintStream());
	}

	@Test
	public void registerLoggerAppenderMakesSLF4JPrintStreamAndRegistersLoggeAppenderIfOutputIsNotSLF4JPrintStream() {
		fail();
	}

	@Test
	public void registerLoggerAppenderRegistersLoggerAppenderIfSystemOutIsSLF4JPrintStream() {
		registerLoggerAppenderRegistersLoggerAppenderIfOutputIsSLF4JPrintStream(SystemOutput.OUT, SLF4JSystemOutput.OUT);
	}

	@Test
	public void registerLoggerAppenderRegistersLoggerAppenderIfSystemErrIsSLF4JPrintStream() {
		registerLoggerAppenderRegistersLoggerAppenderIfOutputIsSLF4JPrintStream(SystemOutput.ERR, SLF4JSystemOutput.ERR);
	}

	private void registerLoggerAppenderRegistersLoggerAppenderIfOutputIsSLF4JPrintStream(
			SystemOutput output, SLF4JSystemOutput slf4jOutput) {
		SLF4JPrintStream slf4jPrintStreamMock = createMock(SLF4JPrintStream.class);
		LoggerAppender logAppenderMock = createMock(LoggerAppender.class);
		slf4jPrintStreamMock.registerLoggerAppender(logAppenderMock);
		output.set(slf4jPrintStreamMock);
		replayAll();
		
		slf4jOutput.registerLoggerAppender(logAppenderMock);
	}

	@Test
	public void deregisterLoggerAppenderDoesNothingIfOutputIsNotSLF4JPrintStream() {
		SLF4JSystemOutput.OUT.deregisterLoggerAppender();
		SLF4JSystemOutput.ERR.deregisterLoggerAppender();
		// Nothing happens
	}
}
