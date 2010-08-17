package org.slf4j.testutils;

import java.util.Collection;
import java.util.Iterator;


public class GenericCrossClassLoaderCollection<E> implements Collection<E> {

	final Class<E> genericType;
	final Collection<?> wrappedCollection;
	
	public GenericCrossClassLoaderCollection(Class<E> genericType, Collection<?> wrappedCollection) {
		this.genericType = genericType;
		this.wrappedCollection = wrappedCollection;
	}
	
	Class<E> getGenericType() {
		return genericType;
	}
	
	Collection<?> getWrappedCollection() {
		return wrappedCollection;
	}

	public boolean add(E o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		return wrappedCollection.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return wrappedCollection.containsAll(c);
	}

	public boolean isEmpty() {
		return wrappedCollection.isEmpty();
	}

	public Iterator<E> iterator() {
		return new GenericCrossClassLoaderIterator();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return wrappedCollection.size();
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
	
	private final class GenericCrossClassLoaderIterator implements Iterator<E> {
		private final Iterator<?> wrappedIterator = wrappedCollection.iterator();

		public boolean hasNext() {
			return wrappedIterator.hasNext();
		}

		public E next() {
			return CrossClassLoaderTestUtils.moveToCurrentClassLoader(genericType, wrappedIterator.next());
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
