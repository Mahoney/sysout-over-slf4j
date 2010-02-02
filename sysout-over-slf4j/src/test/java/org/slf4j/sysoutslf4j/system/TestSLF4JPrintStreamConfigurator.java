package org.slf4j.sysoutslf4j.system;

import static org.slf4j.testutils.Assert.assertNotInstantiable;

import org.junit.Test;

public class TestSLF4JPrintStreamConfigurator {

	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(SLF4JPrintStreamConfigurator.class);
	}
}
