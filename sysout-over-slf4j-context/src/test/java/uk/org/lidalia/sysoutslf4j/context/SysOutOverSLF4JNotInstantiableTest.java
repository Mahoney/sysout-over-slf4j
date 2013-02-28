package uk.org.lidalia.sysoutslf4j.context;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static uk.org.lidalia.test.Assert.isNotInstantiable;

public class SysOutOverSLF4JNotInstantiableTest {

    @Test
    public void notInstantiable() throws Throwable {
        assertThat(SysOutOverSLF4J.class, isNotInstantiable());
    }
}
