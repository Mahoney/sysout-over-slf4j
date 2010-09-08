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

package org.slf4j.testutils;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

public class ThrowableEquals implements IArgumentMatcher {
	
	private Throwable expected;

    public ThrowableEquals(Throwable expected) {
        this.expected = expected;
    }


	public void appendTo(StringBuffer buffer) {
		buffer.append("eqExceptionCause(");
        buffer.append(expected.toString());
        buffer.append(" with cause \"");
        buffer.append(expected.getCause());
        buffer.append("\")");

	}

	public boolean matches(Object actual) {
		if (!(actual instanceof Throwable)) {
            return false;
        }
        Throwable actualThrowable = (Throwable) actual;
        return expected == actualThrowable.getCause();
	}
	
	public static <T extends Throwable> T eqExceptionCause(T in) {
	    EasyMock.reportMatcher(new ThrowableEquals(in));
	    return null;
	}


}
