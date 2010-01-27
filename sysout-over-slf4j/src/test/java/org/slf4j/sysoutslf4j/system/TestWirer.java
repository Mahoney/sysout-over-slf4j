package org.slf4j.sysoutslf4j.system;

import static org.slf4j.testutils.Assert.assertNotInstantiable;

import org.junit.Test;

public class TestWirer {

	@Test
	public void notInstantiable() throws Exception {
		assertNotInstantiable(Wirer.class);
	}
}
