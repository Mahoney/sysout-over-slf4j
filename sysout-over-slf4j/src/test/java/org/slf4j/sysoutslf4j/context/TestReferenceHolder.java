package org.slf4j.sysoutslf4j.context;

import static org.junit.Assert.assertFalse;
import static org.slf4j.testutils.Assert.assertNotInstantiable;

import java.lang.ref.WeakReference;

import org.junit.Test;

public class TestReferenceHolder {
	
	@Test
	public void preventGarbageCollectionForLifeOfClassLoaderMaintainsInstance() {
		Object object = new Object();
		WeakReference<Object> ref = new WeakReference<Object>(object);
		
		ReferenceHolder.preventGarbageCollectionForLifeOfClassLoader(object);
		object = null;
		System.gc();
		
		assertFalse(ref.isEnqueued());
	}

	@Test
	public void notInstantiable() throws Throwable {
		assertNotInstantiable(ReferenceHolder.class);
	}
}
