// This file is part of BuildSmacker.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildSmacker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildSmacker is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildSmacker.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.BuildSmacker;

import java.util.Arrays;

public abstract class HuffmanTree {

	public static class Node {
		private Node b0;

		private static class Union {
			Node b1;
			int value;
			short escapecode;
		}

		private Union u = new Union();

		private Node() {}

		private Node(BitBuffer b, int cache[], Node low8, Node hi8) throws Exception {
			if (b.getBit() != 0) {
				b0 = new Node(b, cache, low8, hi8);
				u.b1 = new Node(b, cache, low8, hi8);
				return;
			}

			int lowval = low8.lookup(b);
			u.value = hi8.lookup(b);
			u.value = lowval | (u.value << 8);

			if (u.value == cache[0]) {
				u.escapecode = 0;
			} else if (u.value == cache[1]) {
				u.escapecode = 1;
			} else if (u.value == cache[2]) {
				u.escapecode = 2;
			} else {
				u.escapecode = 0xFF;
			}
		}

		private Node build(BitBuffer b) throws Exception {
			if (b.getBit() != 0) {
				b0 = new Node().build(b);
				u.b1 = new Node().build(b);
				return this;
			}

			u.value = b.getByte();
			u.escapecode = 0xFF;
			return this;
		}
		
		public Node(BitBuffer b) throws Exception {
			if (b.getBit() == 0) {
				throw new Exception("Warning: initial getBit returned 0");
			}

			build(b);

			if (b.getBit() != 0) {
				throw new Exception("Error: final getBit returned 1");
			}
		}

		public int lookup(BitBuffer b) {
			if (b0 == null) {
				return u.value;
			}
			if (b.getBit() != 0) {
				return u.b1.lookup(b);
			}
			return b0.lookup(b);
		}
	}

	private Node t;
	private int[] cache;

	public HuffmanTree(BitBuffer b) throws Exception {
		cache = new int[3];

		if (b == null) {
			throw new Exception("HuffmanTree BitBuffer == null");
		}

		if (b.getBit() == 0) {
			throw new Exception("HuffmanTree initialization failed!");
		}

		Node low8 = new Node(b);
		Node hi8 = new Node(b);

		for (int i = 0; i < 3; i++) {
			short lowval = b.getByte();
			cache[i] = b.getByte();
			cache[i] = lowval | (cache[i] << 8);
		}

		t = new Node(b, cache, low8, hi8);
		if (b.getBit() != 0) {
			throw new Exception("ERROR: final getBit returned 1");
		}
	}
	
	private int lookup(BitBuffer b, Node t) {
		if (t == null) {
			return -1;
		}

		if (t.b0 == null) {
			int val = t.u.value;
			if (t.u.escapecode != 0xFF) {
				val = cache[t.u.escapecode];
			}

			if (cache[0] != val) {
				cache[2] = cache[1];
				cache[1] = cache[0];
				cache[0] = val;
			}
			return val;
		}

		if (b.getBit() != 0) {
			return lookup(b, t.u.b1);
		}
		return lookup(b, t.b0);
	}

	protected abstract void message(String message);

	public int getCode(BitBuffer b) {
		int s = lookup(b, t);
		if (s < 0) {
			message("HuffmanTree lookup error");
			return -1;
		}
		return s;
	}

	public void reset() {
		Arrays.fill(cache, 0);
	}
}
