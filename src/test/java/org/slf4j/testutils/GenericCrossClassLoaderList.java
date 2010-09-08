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
