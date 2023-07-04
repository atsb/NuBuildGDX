package ru.m210projects.Build.Types;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class LinkedList<T> implements Iterable<T> {

	private final class LinkedListIterator implements Iterator<T> {
		Node<T> node = head;

		@Override
		public boolean hasNext() {
			return node != null;
		}

		@Override
		public T next() {
			Node<T> node = this.node;
			this.node = node.next;
			return node.getValue();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}

		public Iterator<T> init() {
			this.node = head;

			return this;
		}
	}

	public abstract static class Node<T> {
		protected Node<T> next;
		protected Node<T> prev;

		public final Node<T> unlink() {
			Node<T> t = next;
			if (prev != null)
				prev.next = next;
			if (next != null)
				next.prev = prev;
			next = prev = null;
			return t;
		}

		protected final void insertAfter(Node<T> node) {
			if (node.equals(this))
				return;

			node.next = next;
			node.prev = this;
			if (next != null)
				next.prev = node;
			next = node;
		}

		protected final void insertBefore(Node<T> node) {
			if (node.equals(this))
				return;

			node.next = this;
			node.prev = prev;
			if (prev != null)
				prev.next = node;
			prev = node;
		}

		public boolean hasNext() {
			return next != null;
		}

		public Node<T> next() {
			return next;
		}

		public Node<T> prev() {
			return prev;
		}

		public abstract T getValue();
	}

	private LinkedListIterator it;
	protected Node<T> head, tail;
	protected int size;

	public void clear() {
		for (Node<T> node = head; node != null; node = node.next) {
			node.prev = null;
			node.next = null;
		}
		head = tail = null;
		size = 0;
	}

	public int size() {
		return size;
	}

	public boolean remove(Node<T> item) {
		if (size == 0)
			return false;

		if (item.equals(tail)) {
			removeLast();
			return true;
		}

		if (item.equals(head)) {
			removeFirst();
			return true;
		}

		for (Node<T> node = head; node != null; node = node.next) {
			if (item.equals(node)) {
				node.unlink();
				size--;
				return true;
			}
		}
		return false;
	}

	public void removeFirst() {
		head = head.unlink();
		if (--size == 0)
			tail = null;
	}

	public void removeLast() {
		final Node<T> prev = tail.prev;
		tail.unlink();
		tail = prev;
		if (--size == 0)
			head = null;
	}

	public void add(Node<T> item, Comparator<T> c) {
		if(item.next != null || item.prev != null)
			return; //already in the list
		insertBefore(compare(item, c), item);
	}

	public void add(Node<T> node) {
		if (node == null || node.equals(tail) || node.equals(head))
			return;

		if (tail != null) {
			tail.next = node;
			node.prev = tail;
		} else
			head = node;
		tail = node;
		size++;
	}

	public Node<T> compare(Node<T> item, Comparator<T> c) {
		Node<T> node = head;
		while (node != null) {
			if (c.compare(item.getValue(), node.getValue()) < 0)
				break;
			node = node.next();
		}
		return node;
	}

	public void insertBefore(Node<T> node, Node<T> item) {
		if (node == null) {
			add(item);
			return;
		}

		node.insertBefore(item);
		if (node.equals(head))
			head = item;
		size++;
	}

	public void insertAfter(Node<T> node, Node<T> item) {
		if (node == null) {
			add(item);
			return;
		}

		node.insertAfter(item);
		if (node.equals(tail))
			tail = item;
		size++;
	}

	public T next(Node<T> node) {
		if (node.next != null)
			return node.next.getValue();

		return null;
	}

	public T prev(Node<T> node) {
		if (node.prev != null)
			return node.prev.getValue();

		return null;
	}

	public T getFirst() {
		if (head != null)
			return head.getValue();

		return null;
	}

	public T getLast() {
		if (tail != null)
			return tail.getValue();

		return null;
	}

	@Override
	public Iterator<T> iterator() {
		if (it == null)
			it = new LinkedListIterator();
		return it.init();
	}

	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (Node<T> x = head; x != null; x = x.next)
			result[i++] = x;
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sort(Comparator<T> c) {
		if (size == 0)
			return;

		Object[] a = this.toArray();
		Arrays.sort(a, (Comparator) c);

		head = (Node<T>) a[0];
		tail = (Node<T>) a[size - 1];

		Node<T> node = head;
		Node<T> prev = null;
		for (int i = 1; i < size; i++) {
			node.next = (Node<T>) a[i];
			node.prev = prev;
			prev = node;
			node = node.next;
		}
		node.next = null;
		node.prev = prev;
	}

	@Override
	public String toString() {
		Iterator<T> it = iterator();
		if (!it.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			T e = it.next();
			sb.append(e);
			if (!it.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
}
