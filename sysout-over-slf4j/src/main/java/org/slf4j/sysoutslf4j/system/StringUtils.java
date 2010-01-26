package org.slf4j.sysoutslf4j.system;

final class StringUtils {

	static String stripEnd(final String str, final String stripChars) {
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
