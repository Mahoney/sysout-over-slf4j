package org.slf4j.sysoutslf4j.common;

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
	
	public static String substringBefore(final String toBeSubstringed, final String separator) {
		final String substring;
        if (toBeSubstringed == null || toBeSubstringed.length() == 0 || separator == null) {
        	substring = toBeSubstringed;
        } else if (separator.length() == 0) {
        	substring = "";
        } else {
	        final int pos = toBeSubstringed.indexOf(separator);
	        if (pos == -1) {
	        	substring = toBeSubstringed;
	        } else {
	        	substring = toBeSubstringed.substring(0, pos);
	        }
        }
        return substring;
    }
	
	private StringUtils() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
