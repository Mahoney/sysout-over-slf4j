package org.slf4j.sysoutslf4j.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Enumeration representing the five levels on an SLF4J Logger.
 * A call to log on a particular level will call the corresponding
 * method on the given Logger.
 */
public enum LogLevel {
	
	TRACE {
		@Override
		public void log(final Logger logger, final String message) {
			logger.trace(message);
		}
		@Override
		public void log(final Logger logger, final Marker marker, final String message) {
			logger.trace(marker, message);
		}
	},
	
	DEBUG {
		@Override
		public void log(final Logger logger, final String message) {
			logger.debug(message);
		}
		@Override
		public void log(final Logger logger, final Marker marker, final String message) {
			logger.debug(marker, message);
		}
	},
	
	INFO {
		@Override
		public void log(final Logger logger, final String message) {
			logger.info(message);
		}
		@Override
		public void log(final Logger logger, final Marker marker, final String message) {
			logger.info(marker, message);
		}
	},
	
	WARN {
		@Override
		public void log(final Logger logger, final String message) {
			logger.warn(message);
		}
		@Override
		public void log(final Logger logger, final Marker marker, final String message) {
			logger.warn(marker, message);
		}
	},
	
	ERROR {
		@Override
		public void log(final Logger logger, final String message) {
			logger.error(message);
		}
		@Override
		public void log(final Logger logger, final Marker marker, final String message) {
			logger.error(marker, message);
		}
	};
	
	/**
	 * Calls logger.&lt;level&gt;(message)
	 * @param logger
	 * @param message
	 */
	public abstract void log(Logger logger, String message);
	
	/**
	 * Calls logger.&lt;level&gt;(marker, message)
	 * @param logger
	 * @param message
	 */
	public abstract void log(Logger logger, Marker marker, String message);
}
