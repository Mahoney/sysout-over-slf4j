package uk.org.lidalia.sysoutslf4j.context;

import org.junit.Test;

import static uk.org.lidalia.test.Assert.assertNotInstantiable;

public class SysOutOverSLF4JNotInstantiableTest {

    @Test
    public void notInstantiable() throws Throwable {
        assertNotInstantiable(SysOutOverSLF4J.class);
    }
}
