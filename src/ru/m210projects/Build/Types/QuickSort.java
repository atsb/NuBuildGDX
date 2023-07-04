package ru.m210projects.Build.Types;

import java.util.Comparator;

public class QuickSort {

	private static <E> void swap(E[] elementData, int i, int j) {
		E temp = elementData[i];
		elementData[i] = elementData[j];
		elementData[j] = temp;
	}

	private static final int[] base_stack = new int[4 * 8];
	private static final int[] n_stack = new int[4 * 8];

	private static <E> int med3(E[] elementData, Comparator<? super E> comp, int a, int b, int c) {
//		return CompareChannels(rxBucket[a], rxBucket[b]) < 0 ?
//		       (CompareChannels(rxBucket[b], rxBucket[c]) < 0 ? b : (CompareChannels(rxBucket[a], rxBucket[c]) < 0 ? c : a ))
//	              :(CompareChannels(rxBucket[b], rxBucket[c]) > 0 ? b : (CompareChannels(rxBucket[a], rxBucket[c]) < 0 ? a : c ));

		if (comp.compare(elementData[a], elementData[b]) > 0) {
			if (comp.compare(elementData[a], elementData[c]) > 0) {
				if (comp.compare(elementData[b], elementData[c]) > 0)
					return (b);
				return (c);
			}
			return (a);
		}

		if (comp.compare(elementData[a], elementData[c]) >= 0)
			return (a);

		if (comp.compare(elementData[b], elementData[c]) > 0)
			return (c);

		return (b);
	}

	public static <E> void sort(E[] elementData, int size, Comparator<? super E> c) {
		if(size == 0)
			return;

		int n = size;

		int base = 0, sp = 0;
		while (true) {
			while (n > 1) {
				if (n < 16) {
					for (int shell = 3; shell > 0; shell -= 2) {
						for (int p1 = base + shell; p1 < base + n; p1 += shell) {
							for (int p2 = p1; p2 > base && c.compare(elementData[p2 - shell], elementData[p2]) > 0; p2 -= shell) {
								swap(elementData, p2, p2 - shell);
							}
						}
					}
					break;
				} else {
					int mid = base + (n >> 1);
					if (n > 29) {
						int p1 = base;
						int p2 = base + (n - 1);
						if (n > 42) {
							int s = (n >> 3);
							p1 = med3(elementData, c, p1, p1 + s, p1 + (s << 1));
							mid = med3(elementData, c, mid - s, mid, mid + s);
							p2 = med3(elementData, c, p2 - (s << 1), p2 - s, p2);
						}
						mid = med3(elementData, c, p1, mid, p2);
					}

					E pv = elementData[mid];

					int pa, pb, pc, comparison;
					pa = pb = base;
					int pd = base + (n - 1);

					for (pc = base + (n - 1);; pc--) {
						while (pb <= pc && (comparison = c.compare(elementData[pb], pv)) <= 0) {
							if (comparison == 0) {
								swap(elementData, pa, pb);
								pa++;
							}
							pb++;
						}

						while (pb <= pc && (comparison = c.compare(elementData[pc], pv)) >= 0) {
							if (comparison == 0) {
								swap(elementData, pc, pd);
								pd--;
							}
							pc--;
						}

						if (pb > pc)
							break;

						swap(elementData, pb, pc);
						pb++;
					}

					int pn = base + n;
					int s = Math.min(pa - base, pb - pa);
					if (s > 0) {
						for (int i = 0; i < s; i++) {
							swap(elementData, base + i, pb - s + i);
						}
					}
					s = Math.min(pd - pc, pn - pd - 1);
					if (s > 0) {
						for (int i = 0; i < s; i++) {
							swap(elementData, pb + i, pn - s + i);
						}
					}

					int r = pb - pa;
					s = pd - pc;
					if (s >= r) {
						base_stack[sp] = pn - s;
						n_stack[sp] = s;
						n = r;
					} else {
						if (r <= 1)
							break;
						base_stack[sp] = base;
						n_stack[sp] = r;
						base = pn - s;
						n = s;
					}
					sp++;
				}
			}

			if (sp-- == 0)
				break;

			base = base_stack[sp];
			n = n_stack[sp];
		}
	}

	public interface IntComparator {
		int compare(int o1, int o2);
	}

	private static void swap(int[] elementData, int i, int j) {
		int temp = elementData[i];
		elementData[i] = elementData[j];
		elementData[j] = temp;
	}

	private static int med3(int[] elementData, IntComparator comp, int a, int b, int c) {
		if (comp.compare(elementData[a], elementData[b]) > 0) {
			if (comp.compare(elementData[a], elementData[c]) > 0) {
				if (comp.compare(elementData[b], elementData[c]) > 0)
					return (b);
				return (c);
			}
			return (a);
		}

		if (comp.compare(elementData[a], elementData[c]) >= 0)
			return (a);

		if (comp.compare(elementData[b], elementData[c]) > 0)
			return (c);

		return (b);
	}

	public static void sort(int[] elementData, int size, IntComparator c) {
		if(size == 0)
			return;

		int n = size;

		int base = 0, sp = 0;
		while (true) {
			while (n > 1) {
				if (n < 16) {
					for (int shell = 3; shell > 0; shell -= 2) {
						for (int p1 = base + shell; p1 < base + n; p1 += shell) {
							for (int p2 = p1; p2 > base && c.compare(elementData[p2 - shell], elementData[p2]) > 0; p2 -= shell) {
								swap(elementData, p2, p2 - shell);
							}
						}
					}
					break;
				} else {
					int mid = base + (n >> 1);
					if (n > 29) {
						int p1 = base;
						int p2 = base + (n - 1);
						if (n > 42) {
							int s = (n >> 3);
							p1 = med3(elementData, c, p1, p1 + s, p1 + (s << 1));
							mid = med3(elementData, c, mid - s, mid, mid + s);
							p2 = med3(elementData, c, p2 - (s << 1), p2 - s, p2);
						}
						mid = med3(elementData, c, p1, mid, p2);
					}

					int pv = elementData[mid];

					int pa, pb, pc, comparison;
					pa = pb = base;
					int pd = base + (n - 1);

					for (pc = base + (n - 1);; pc--) {
						while (pb <= pc && (comparison = c.compare(elementData[pb], pv)) <= 0) {
							if (comparison == 0) {
								swap(elementData, pa, pb);
								pa++;
							}
							pb++;
						}

						while (pb <= pc && (comparison = c.compare(elementData[pc], pv)) >= 0) {
							if (comparison == 0) {
								swap(elementData, pc, pd);
								pd--;
							}
							pc--;
						}

						if (pb > pc)
							break;

						swap(elementData, pb, pc);
						pb++;
					}

					int pn = base + n;
					int s = Math.min(pa - base, pb - pa);
					if (s > 0) {
						for (int i = 0; i < s; i++) {
							swap(elementData, base + i, pb - s + i);
						}
					}
					s = Math.min(pd - pc, pn - pd - 1);
					if (s > 0) {
						for (int i = 0; i < s; i++) {
							swap(elementData, pb + i, pn - s + i);
						}
					}

					int r = pb - pa;
					s = pd - pc;
					if (s >= r) {
						base_stack[sp] = pn - s;
						n_stack[sp] = s;
						n = r;
					} else {
						if (r <= 1)
							break;
						base_stack[sp] = base;
						n_stack[sp] = r;
						base = pn - s;
						n = s;
					}
					sp++;
				}
			}

			if (sp-- == 0)
				break;

			base = base_stack[sp];
			n = n_stack[sp];
		}
	}

}
