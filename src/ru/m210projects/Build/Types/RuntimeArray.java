package ru.m210projects.Build.Types;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;

public class RuntimeArray<E> extends AbstractList<E> {

	private static final Object[] EMPTY_ELEMENTDATA = {};
	protected E[] elementData;
	protected int size;

	@SuppressWarnings("unchecked")
	public RuntimeArray() {
		this.elementData = (E[]) EMPTY_ELEMENTDATA;
	}

	@Override
	public boolean add(E e) {
		ensureExplicitCapacity(size + 1);
		elementData[size++] = e;
		return true;
	}

	@Override
	public void add(int index, E element) {
		ensureExplicitCapacity(size + 1);
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		elementData[index] = element;
		size++;
	}

	@Override
	public E get(int index) {
		return elementData[index];
	}

	@Override
	public E set(int index, E element) {
		E oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
	}

	@Override
	public E remove(int index) {
		E oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		elementData[--size] = null; // clear to let GC do its work

		return oldValue;
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < size; i++)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (o.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size - 1; i >= 0; i--)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (o.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public E[] getArray() {
		return elementData;
	}

	public void sort(Comparator<? super E> c) {
//		Arrays.sort(elementData, 0, size, c);
		QuickSort.sort(elementData, size, c);
	}

	private void ensureExplicitCapacity(int minCapacity) {
		if (minCapacity - elementData.length > 0)
			grow(minCapacity);
	}

	private void grow(int minCapacity) {
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		elementData = Arrays.copyOf(elementData, newCapacity);
	}

}
