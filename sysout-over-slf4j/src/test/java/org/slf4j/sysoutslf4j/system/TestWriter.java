package org.slf4j.sysoutslf4j.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class TestWriter {

	@Test
	public void notInstantiable() throws Exception {
		try {
			Whitebox.invokeConstructor(Wirer.class);
			fail();
		} catch (UnsupportedOperationException oue) {
			assertEquals("Not instantiable", oue.getMessage());
		}
	}
}
