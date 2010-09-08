/* 
 * Copyright (c) 2009-2010 Robert Elliot
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
