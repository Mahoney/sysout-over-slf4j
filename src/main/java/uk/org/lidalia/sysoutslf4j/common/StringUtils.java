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

package uk.org.lidalia.sysoutslf4j.common;

public final class StringUtils {

	public static String stripEnd(final String str, final String stripChars) {
		return stripEnd(str, str.length() - 1, stripChars);
	}
	
	private static String stripEnd(final String string, final int index, final String stripChars) {
		final String result;
		if (index == -1) {
			result = "";
		} else {
			final char candidateToBeStripped = string.charAt(index);
			final boolean candidateShouldNotBeStripped = stripChars.indexOf(candidateToBeStripped) == -1;
			if (candidateShouldNotBeStripped) {
				result = string.substring(0, index + 1);
			} else {
				result = stripEnd(string, index - 1, stripChars);
			}
		}
		return result;
	}
	
	private StringUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
