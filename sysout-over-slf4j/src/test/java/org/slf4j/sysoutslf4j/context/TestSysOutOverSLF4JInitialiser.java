package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import javassist.ClassPool;
import javassist.CtClass;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.testutils.LoggingUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ch.qos.logback.classic.Logger.class })
public class TestSysOutOverSLF4JInitialiser {
	
	static {
		LoggingUtils.turnOffRootLogging();
	}
	
	private SysOutOverSLF4JInitialiser initialiser;
	private LoggingSystemRegister loggingSystemRegister;
	private Logger loggerImplementation;
	private ch.qos.logback.classic.Logger initialiserLogger;
	private ListAppender<ILoggingEvent> appender;
	
	public TestSysOutOverSLF4JInitialiser() {
		setUpLogger();
		loggingSystemRegister = createStrictMock(LoggingSystemRegister.class);
		initialiser = new SysOutOverSLF4JInitialiser(loggingSystemRegister);
	}
	
	private void setUpLogger() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		initialiserLogger = lc.getLogger(SysOutOverSLF4JInitialiser.class);
		initialiserLogger.setLevel(Level.DEBUG);
		initialiserLogger.detachAndStopAllAppenders();
		appender = new ListAppender<ILoggingEvent>();
		appender.setContext(lc);
		appender.start();
		initialiserLogger.addAppender(appender);
	}
	
	@After
	public void verifyAllMocks() {
		try {
			replayAll();
		} catch (IllegalStateException ise) {
			// ignore
		}
		verifyAll();
	}

	@Test
	public void initialiseWithX4JuliRegistersLoggingPackageAutomatically() throws Exception {
		Logger x4juliLogger = makeMockLogger("org.x4juli.X4JuliLogger");
		
		givenLoggerIsA(x4juliLogger);
		expectX4juliToBeRegistered();
		whenInitialiseIsCalled();
	}
	
	@Test
	public void initialiseWithGrleaSimpleLoggerRegistersLoggingPackageAutomatically() throws Exception {
		Logger grleaSimpleLogger = makeMockLogger("org.grlea.log.adapters.slf4j.Slf4jAdapter");
		
		givenLoggerIsA(grleaSimpleLogger);
		expectGrleaSimpleLoggerToBeRegistered();
		whenInitialiseIsCalled();
	}
	
	@Test
	public void initialiseWithSlf4jSimpleLoggerRegistersLoggingPackageAutomatically() throws Exception {
		Logger slf4jSimpleLogger = makeMockLogger("org.slf4j.impl.SimpleLogger");
		
		givenLoggerIsA(slf4jSimpleLogger);
		expectSlf4jSimpleLoggerToBeRegistered();
		whenInitialiseIsCalled();
	}
	
	private Logger makeMockLogger(String loggerClassName) throws Exception {
		Class<Logger> mockLoggerClass = makeMockLoggerClass(loggerClassName);
		return createStrictMock(mockLoggerClass);
	}

	@SuppressWarnings("unchecked")
	private Class<Logger> makeMockLoggerClass(String loggerClassName) throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc;
		try {
			cc = pool.getCtClass(loggerClassName);
		} catch (Exception e) {
			cc = pool.makeClass(loggerClassName);
			cc.setInterfaces(new CtClass[] {pool.getCtClass(Logger.class.getName())});
		}
		Class<Logger> mockLoggerClass = (Class<Logger>) cc.toClass(getClass().getClassLoader(), null);
		return mockLoggerClass;
	}
	
	private void givenLoggerIsA(Logger logger) throws Exception {
		loggerImplementation = logger;
	}
	
	private void whenInitialiseIsCalled() {
		initialiser.initialise(loggerImplementation);
	}
	
	private void expectX4juliToBeRegistered() {
		loggingSystemRegister.registerLoggingSystem("org.x4juli.");
		replayAll();
	}
	
	private void expectGrleaSimpleLoggerToBeRegistered() {
		loggingSystemRegister.registerLoggingSystem("org.grlea.log.");
		replayAll();
	}
	
	private void expectSlf4jSimpleLoggerToBeRegistered() {
		loggingSystemRegister.registerLoggingSystem("org.slf4j.impl.SimpleLogger");
		replayAll();
	}

	@Test
	public void initialiseWithLogbackLoggerLogsDebugMessageToSayNoRegistrationNecessary() throws Exception {
		Logger logbackLogger = createStrictMock(ch.qos.logback.classic.Logger.class);
		
		givenLoggerIsA(logbackLogger);
		whenInitialiseIsCalled();
		thenADebugMessageToSayNoRegistrationNecessaryShouldBeLogged(logbackLogger.getClass());
	}
	
	@Test
	public void initialiseWithNLog4JLoggerLogsDebugMessageToSayNoRegistrationNecessary() throws Exception {
		Logger nlog4jLogger = makeMockLogger("org.apache.log4j.NLogger");
		
		givenLoggerIsA(nlog4jLogger);
		whenInitialiseIsCalled();
		thenADebugMessageToSayNoRegistrationNecessaryShouldBeLogged(nlog4jLogger.getClass());
	}

	@Test
	public void initialiseWithLog4JLoggerLogsDebugMessageToSayNoRegistrationNecessary() throws Exception {
		Logger log4jLogger = makeMockLogger("org.slf4j.impl.Log4jLoggerAdapter");
		
		givenLoggerIsA(log4jLogger);
		whenInitialiseIsCalled();
		thenADebugMessageToSayNoRegistrationNecessaryShouldBeLogged(log4jLogger.getClass());
	}

	@Test
	public void initialiseWithJDK14LoggerLogsDebugMessageToSayNoRegistrationNecessary() throws Exception {
		Logger jdk14Logger = makeMockLogger("org.slf4j.impl.JDK14LoggerAdapter");
		
		givenLoggerIsA(jdk14Logger);
		whenInitialiseIsCalled();
		thenADebugMessageToSayNoRegistrationNecessaryShouldBeLogged(jdk14Logger.getClass());
	}

	private void thenADebugMessageToSayNoRegistrationNecessaryShouldBeLogged(Class<? extends Logger> knownLoggerImplementationClass) {
		assertSoleLoggingEvent(Level.DEBUG, "Your logging framework {} should not need access to the standard println methods on "
		+ "the console, so you should not need to register a logging system package.", knownLoggerImplementationClass);
	}
	
	private void assertSoleLoggingEvent(Level level, String message, Object... args) {
		assertEquals(1, appender.list.size());
		ILoggingEvent loggingEvent = appender.list.get(0);
		assertEquals(level, loggingEvent.getLevel());
		assertEquals(message, loggingEvent.getMessage());
		assertArrayEquals(args, loggingEvent.getArgumentArray());
	}

	@Test
	public void initialiseWithUnknownLoggingSystemLogsWarnMessage() throws Exception {
		Logger unknownLogger = createStrictMock(Logger.class);
		givenLoggerIsA(unknownLogger);
		whenInitialiseIsCalled();
		thenAWarnMessageToSayRegistrationMayBeNecessaryShouldBeLogged(unknownLogger.getClass());
	}
	
	private void thenAWarnMessageToSayRegistrationMayBeNecessaryShouldBeLogged(Class<? extends Logger> unknownLoggerClass) {
		assertSoleLoggingEvent(Level.WARN, "Your logging framework {} is not known - if it needs access to the standard println methods on "
					+ "the console you will need to register it by calling registerLoggingSystemPackage", unknownLoggerClass);
	}
}
