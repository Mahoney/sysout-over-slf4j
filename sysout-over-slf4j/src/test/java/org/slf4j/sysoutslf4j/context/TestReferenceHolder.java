package org.slf4j.sysoutslf4j.context;

import static org.slf4j.testutils.Assert.assertNotInstantiable;

import org.junit.Test;

public class TestReferenceHolder {

	@Test
	public void notInstantiable() throws Exception {
		assertNotInstantiable(ReferenceHolder.class);
	}
}
