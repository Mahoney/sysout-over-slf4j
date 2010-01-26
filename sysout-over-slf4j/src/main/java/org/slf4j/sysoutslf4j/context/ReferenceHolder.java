package org.slf4j.sysoutslf4j.context;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ReferenceHolder {
	
	private static final Map<Object, Object> REFERENCES = Collections.synchronizedMap(new IdentityHashMap<Object, Object>()); 

	public static void preventGarbageCollectionForLifeOfClassLoader(final Object objectToBeMaintained) {
		REFERENCES.put(objectToBeMaintained, objectToBeMaintained);
	}
	
	private ReferenceHolder() {
		throw new UnsupportedOperationException("Not instantiable");
	}
}
