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

package uk.org.lidalia.sysoutslf4j.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategy;
import uk.org.lidalia.sysoutslf4j.context.exceptionhandlers.ExceptionHandlingStrategyFactory;
import uk.org.lidalia.sysoutslf4j.system.LoggerAppender;
import uk.org.lidalia.sysoutslf4j.system.SLF4JSystemOutput;

class SLF4JPrintStreamManager {

	private static final Logger LOG = LoggerFactory.getLogger(SysOutOverSLF4J.class);

	void sendSystemOutAndErrToSLF4J(final LogLevel outLevel, final LogLevel errLevel,
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory) {
		registerNewLoggerAppender(exceptionHandlingStrategyFactory, SLF4JSystemOutput.OUT, outLevel);
		registerNewLoggerAppender(exceptionHandlingStrategyFactory, SLF4JSystemOutput.ERR, errLevel);
		LOG.info("Redirected System.out and System.err to SLF4J for this context");
	}

//		if (SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams()) {
//			LOG.debug("System.out and System.err are already SLF4JPrintStreams");
//		} else {
//			LOG.info("Replaced standard System.out and System.err PrintStreams with SLF4JPrintStreams");
//		}

	private void registerNewLoggerAppender(
			final ExceptionHandlingStrategyFactory exceptionHandlingStrategyFactory,
			final SLF4JSystemOutput slf4jSystemOutput, final LogLevel logLevel) {

		final ExceptionHandlingStrategy exceptionHandlingStrategy = 
			exceptionHandlingStrategyFactory.makeExceptionHandlingStrategy(logLevel, slf4jSystemOutput.getOriginalPrintStream());
		final LoggerAppender loggerAppender = new LoggerAppenderImpl(
				logLevel, exceptionHandlingStrategy, slf4jSystemOutput.getOriginalPrintStream());
		ReferenceHolder.preventGarbageCollectionForLifeOfClassLoader(loggerAppender);
		slf4jSystemOutput.registerLoggerAppender(loggerAppender);
	}

	void stopSendingSystemOutAndErrToSLF4J() {
		for (SLF4JSystemOutput systemOutput : SLF4JSystemOutput.values()) {
			systemOutput.deregisterLoggerAppender();
		}
//			LOG.warn("Cannot stop sending System.out and System.err to SLF4J - they are not being sent there at the moment");
	}

	void restoreOriginalSystemOutputsIfNecessary() {
		for (SLF4JSystemOutput systemOutput : SLF4JSystemOutput.values()) {
			systemOutput.restoreOriginalPrintStream();
		}
//		if (SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams()) {
//			LOG.info("Restored original System.out and System.err");
//		} else {
//			LOG.warn("System.out and System.err are not SLF4JPrintStreams - cannot restore");
//		}
	}
}
