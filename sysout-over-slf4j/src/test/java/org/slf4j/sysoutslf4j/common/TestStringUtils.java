package org.slf4j.sysoutslf4j.common;

import static org.junit.Assert.assertEquals;
import static org.slf4j.testutils.Assert.assertNotInstantiable;
import static org.slf4j.testutils.Assert.shouldThrow;

import java.util.concurrent.Callable;

import org.junit.Test;

public class TestStringUtils {

	@Test
	public void substringBeforeReturnsStringBeforeSuppliedString() {
		assertEquals("hello ", StringUtils.substringBefore("hello world from this world", "world"));
	}
	
	@Test
	public void substringBeforeReturnsEmptyStringIfInputStringEmpty() {
		assertEquals("", StringUtils.substringBefore("", "ignored"));
	}
	
	@Test
	public void substringBeforeReturnsEmptyStringIfSeparatorEmpty() {
		assertEquals("", StringUtils.substringBefore("hello", ""));
	}
	
	@Test
	public void substringBeforeReturnsInputStringIfSeparatorNotPresent() {
		assertEquals("hello", StringUtils.substringBefore("hello", "blah"));
	}
	
	@Test
	public void substringBeforeThrowsNullPointerExceptionIfInputIsNull() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StringUtils.substringBefore(null, "irrelevant");
				return null;
			}
		});
	}
	
	@Test
	public void substringBeforeThrowsNullPointerExceptionIfSeparatorIsNull() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StringUtils.substringBefore("irrelevant", null);
				return null;
			}
		});
	}
	
	@Test
	public void stripEndStripsEnd() {
		assertEquals("hello wo", StringUtils.stripEnd("hello world", "elders"));
	}
	
	@Test
	public void stripEndReturnsEmptyStringIfEmptyStringPassedIn() {
		assertEquals("", StringUtils.stripEnd("", "irrelevant"));
	}
	
	@Test
	public void stripEndReturnsInputIfEmptyStripCharsPassedIn() {
		assertEquals("hello", StringUtils.stripEnd("hello", ""));
	}
	
	@Test
	public void stripEndThrowsNullPointerExceptionIfInputIsNull() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StringUtils.stripEnd(null, "irrelevant");
				return null;
			}
		});
	}
	
	@Test
	public void stripEndThrowsNullPointerExceptionIfStripCharsIsNull() throws Throwable {
		shouldThrow(NullPointerException.class, new Callable<Void>() {
			public Void call() throws Exception {
				StringUtils.stripEnd("irrelevant", null);
				return null;
			}
		});
	}
	
	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(StringUtils.class);
	}
}
