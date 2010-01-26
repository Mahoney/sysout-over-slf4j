package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class TestReferenceHolder {

	@Test
	public void notInstantiable() throws Exception {
		try {
			Whitebox.invokeConstructor(ReferenceHolder.class);
			fail();
		} catch (UnsupportedOperationException oue) {
			assertEquals("Not instantiable", oue.getMessage());
		}
	}
}
