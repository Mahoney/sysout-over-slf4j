package org.slf4j.sysoutslf4j.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

public enum LogLevel {
	
	TRACE {
		@Override
		public void log(Logger logger, String message) {
			logger.trace(message);
		}
		@Override
		public void log(Logger logger, Marker marker, String message) {
			logger.trace(marker, message);
		}
	},
	
	DEBUG {
		@Override
		public void log(Logger logger, String message) {
			logger.debug(message);
		}
		@Override
		public void log(Logger logger, Marker marker, String message) {
			logger.debug(marker, message);
		}
	},
	
	INFO {
		@Override
		public void log(Logger logger, String message) {
			logger.info(message);
		}
		@Override
		public void log(Logger logger, Marker marker, String message) {
			logger.info(marker, message);
		}
	},
	
	WARN {
		@Override
		public void log(Logger logger, String message) {
			logger.warn(message);
		}
		@Override
		public void log(Logger logger, Marker marker, String message) {
			logger.warn(marker, message);
		}
	},
	
	ERROR {
		@Override
		public void log(Logger logger, String message) {
			logger.error(message);
		}
		@Override
		public void log(Logger logger, Marker marker, String message) {
			logger.error(marker, message);
		}
	};
	
	public abstract void log(Logger logger, String message);
	public abstract void log(Logger logger, Marker marker, String message);
}
