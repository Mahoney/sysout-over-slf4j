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
