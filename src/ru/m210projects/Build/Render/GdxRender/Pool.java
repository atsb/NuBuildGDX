package ru.m210projects.Build.Render.GdxRender;

import java.util.ArrayList;

public abstract class Pool<T> {
	private int free;
	private final ArrayList<T> instances = new ArrayList<T>();

	protected abstract T newObject();

	public void reset() {
		free = instances.size();
	}

	public T obtain() {
		if (free == 0) {
			T v = newObject();
			instances.add(v);
			return v;
		} else {
			return reset(instances.get(--free));
		}
	}

	protected T reset(T object) {
		if (object instanceof Poolable)
			((Poolable) object).reset();
		return object;
	}

	public int getSize() {
		return instances.size();
	}

	@Override
	public String toString() {
		String text = "\r\n";
		text += "[free / size]: " + free + " / " + instances.size() + "\r\n";
		text += "used : " + (((instances.size() - free) * 100) / instances.size()) + "% \r\n";
		return text;
	}

	public interface Poolable {
		void reset();
	}
}
