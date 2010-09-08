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
