package org.slf4j.testutils;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;


public class GenericCrossClassLoaderList<E> extends GenericCrossClassLoaderCollection<E> implements List<E> {

	private final List<?> wrappedList;
	
	public GenericCrossClassLoaderList(Class<E> genericType, List<?> wrappedList) {
		super(genericType, wrappedList);
		this.wrappedList = wrappedList;
	}

	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public E get(int index) {
		return CrossClassLoaderTestUtils.moveToCurrentClassLoader(getGenericType(), wrappedList.get(index));
	}

	public int indexOf(Object o) {
		return wrappedList.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return wrappedList.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

}
