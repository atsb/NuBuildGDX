//This file is part of BuildGDX.
//Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.FileHandle;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import ru.m210projects.Build.Gameutils;

import static ru.m210projects.Build.Strhandler.toLowerCase;

public class FileResource implements Resource {

//	private static Unsafe unsafe;
//	static {
//		try {
//			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
//			theUnsafe.setAccessible(true);
//			unsafe = (Unsafe) theUnsafe.get(null);
//		} catch (Exception e) {
//		}
//	}

	private final byte[] tmpbuf = new byte[1024];

	public enum Mode {
		Read, Write
	}

	private RandomAccessFile raf;
	private Mode mode;
	private String ext, path;
	private ByteBuffer fbuf;

	protected FileResource open(File file, Mode mode) {
		this.mode = mode;
		try {
			switch (mode) {
			case Read:
				raf = new RandomAccessFile(file, "r");
				try {
					FileChannel ch = raf.getChannel();

					fbuf = ByteBuffer.allocate((int) ch.size());
					ch.read(fbuf);
					fbuf.flip();

//					fbuf = ch.map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
				} catch (Exception e) {
					e.printStackTrace();
				}

				handle(file);
				return this;
			case Write:
				raf = new RandomAccessFile(file, "rw");
				raf.setLength(0);
				handle(file);
				return this;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void handle(File file) {
		String filename = toLowerCase(file.getName());
		ext = filename.substring(filename.lastIndexOf('.') + 1);
		path = file.getPath();
	}

	public String getPath() {
		if (isClosed())
			return null;

//		try {
//			Field path = raf.getClass().getDeclaredField("path");
//			path.setAccessible(true);
//			return (String) path.get(raf);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return path;
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public boolean isClosed() {
		return raf == null;
	}

	@Override
	public void close() {
		if (isClosed())
			return;

		try {
			if (fbuf != null)
				fbuf = null;
			raf.close();
			raf = null;
			path = null;
			ext = null;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

//	private void freeJRE8(MappedByteBuffer bb) throws Exception {
//		Object cleaner = ((sun.nio.ch.DirectBuffer) bb).cleaner();
//		Method invokeCleaner = cleaner.getClass().getDeclaredMethod("clean");
//		invokeCleaner.setAccessible(true);
//		invokeCleaner.invoke(cleaner);
//	}
//
//	private void freeJRE9(MappedByteBuffer bb) throws Exception {
//		Method invokeCleaner = Unsafe.class.getMethod("invokeCleaner", ByteBuffer.class);
//		invokeCleaner.invoke(unsafe, bb);
//	}
//
//	private void free(MappedByteBuffer bb) {
//		try {
//			if (BuildGdx.app.getPlatform() != Platform.Android && BuildGdx.app.getVersion() < 9) {
//				freeJRE8(bb);
//			} else {
//				freeJRE9(bb);
//			}
//		} catch (Throwable ignored) {
//		}
//	}

	@Override
	public int seek(long offset, Whence whence) {
		int var = -1;
		if (isClosed())
			return var;

		try {
			if (whence == Whence.Set) {
				if (offset < 0)
					return -1;
			} else if (whence == Whence.Current) {
				offset += position();
			} else if (whence == Whence.End) {
				offset += size();
			}

			if (fbuf != null)
				fbuf.position((int) offset);
			else
				raf.getChannel().position(offset);

			var = position();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return var;
	}

	@Override
	public int read(byte[] buf, int offset, int len) {
		int var = -1;
		if (isClosed())
			return var;

		try {
			if (fbuf != null) {
				if (fbuf.remaining() >= len) {
					fbuf.get(buf, offset, len);
					var = len;
				} else if (fbuf.remaining() > 0) {
					len = fbuf.remaining();
					fbuf.get(buf, offset, len);
					var = len;
				}
			} else
				var = raf.read(buf, offset, len);
		} catch (EOFException e) {
			return -1;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't read file \r\n" + e.getMessage());
		}
		return var;
	}

	@Override
	public int read(byte[] buf) {
		return read(buf, 0, buf.length);
	}

	@Override
	public int read(ByteBuffer bb, int offset, int len) {
		try {
			int var;
			bb.position(offset);
			int p = 0;
			while (len > 0) {
				if (fbuf != null) {
					var = Math.min(len, tmpbuf.length);
					if (var > fbuf.remaining())
						return p;
					fbuf.get(tmpbuf, 0, var);
				} else {
					if ((var = raf.read(tmpbuf, 0, Math.min(len, tmpbuf.length))) == -1)
						return p;
				}
				bb.put(tmpbuf, 0, var);
				len -= var;
				p += var;
			}
			return p;
		} catch (EOFException e) {
			return -1;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't read file \r\n" + e.getMessage());
		}
	}

	@Override
	public String readString(int len) {
		byte[] data;
		if (len < tmpbuf.length)
			data = tmpbuf;
		else
			data = new byte[len];
		if (read(data, 0, len) != len)
			return null;

		return new String(data, 0, len);
	}

	@Override
	public Integer readInt() {
		if (read(tmpbuf, 0, 4) == 4)
			return ((tmpbuf[3] & 0xFF) << 24) + ((tmpbuf[2] & 0xFF) << 16) + ((tmpbuf[1] & 0xFF) << 8)
					+ (tmpbuf[0] & 0xFF);
		return null;
	}

	@Override
	public Long readLong() {
		if (read(tmpbuf, 0, 8) == 8)
			return (((long) tmpbuf[7] & 0xFF) << 56) + (((long) tmpbuf[6] & 0xFF) << 48)
					+ (((long) tmpbuf[5] & 0xFF) << 40) + (((long) tmpbuf[4] & 0xFF) << 32)
					+ (((long) tmpbuf[3] & 0xFF) << 24) + (((long) tmpbuf[2] & 0xFF) << 16)
					+ (((long) tmpbuf[1] & 0xFF) << 8) + (((long) tmpbuf[0] & 0xFF));
		return null;
	}

	@Override
	public Float readFloat() {
		Integer i = readInt();
		if (i != null)
			return Float.intBitsToFloat(i);
		return null;
	}

	@Override
	public Short readShort() {
		if (read(tmpbuf, 0, 2) == 2)
			return (short) (((tmpbuf[1] & 0xFF) << 8) + (tmpbuf[0] & 0xFF));
		return null;
	}

	@Override
	public Byte readByte() {
		if (isClosed())
			return null;

		try {
			if (fbuf != null)
				return fbuf.get();
			return raf.readByte();
		} catch (EOFException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't read file \r\n" + e.getMessage());
		}
	}

	@Override
	public Boolean readBoolean() {
		Byte var = readByte();
		if (var != null)
			return var == 1;
		return null;
	}

//	protected ByteBuffer readBuffer(int len) {
//		ByteBuffer out;
//		if (isClosed())
//			return null;
//
//		try {
//			FileChannel ch = raf.getChannel();
//			long pos = ch.position();
//			out = ch.map(FileChannel.MapMode.READ_ONLY, pos, len);
//			ch.position(pos + len);
//		} catch (EOFException e) {
//			return null;
//		} catch (Exception e) {
//			throw new RuntimeException("Couldn't read file \r\n" + e.getMessage());
//		}
//
//		return out;
//	}

	public int writeBytes(Object array) {
		int len;
		if (array instanceof byte[])
			len = ((byte[]) array).length;
		else if (array instanceof ByteBuffer)
			len = ((ByteBuffer) array).capacity();
		else if (array instanceof short[])
			len = ((short[]) array).length;
		else if (array instanceof int[])
			len = ((int[]) array).length;
		else if (array instanceof char[])
			len = ((char[]) array).length;
		else if (array instanceof String)
			len = ((String) array).getBytes().length;
		else
			throw new UnsupportedOperationException("Couldn't write to file. \r\n + Not implemented!");

		if (len != 0)
			return writeBytes(array, len);

		return -1;
	}

	public int writeBytes(Object obj, int len) {
		int var = -1;
		if (isClosed() || getMode() != Mode.Write)
			return var;

		try {
			byte[] data = null;
			if (obj instanceof Byte) {
				int rem = len;
				while (rem > 0) {
					int l = Math.min(rem, tmpbuf.length);
					Gameutils.fill(tmpbuf, 0, l, (byte) obj);
					raf.write(tmpbuf, 0, l);
					rem -= l;
				}
				return len;
			} else if (obj instanceof byte[])
				data = (byte[]) obj;
			else if (obj instanceof char[]) {
				data = new byte[len];
				char[] src = (char[]) obj;
				for (int i = 0; i < Math.min(len, src.length); i++)
					data[i] = (byte) src[i];
			} else if (obj instanceof ByteBuffer) {
				ByteBuffer buf = (ByteBuffer) obj;
				buf.rewind();
				if (!buf.isDirect())
					data = buf.array();
				else {
					data = new byte[Math.min(len, buf.capacity())];
					buf.get(data);
				}
			} else if (obj instanceof short[]) {
				var = 0;
				short[] shortArr = (short[]) obj;
				len = Math.min(len, shortArr.length);
				for (int i = 0; i < len; i++)
					var += writeShort(shortArr[i]);
			} else if (obj instanceof int[]) {
				var = 0;
				int[] intArr = (int[]) obj;
				len = Math.min(len, intArr.length);
				for (int i = 0; i < len; i++)
					var += writeInt(intArr[i]);
			} else if (obj instanceof String) {
				data = ((String) obj).getBytes();
			} else
				throw new UnsupportedOperationException("Couldn't write to file. \r\n + Not implemented!");

			if (data != null) {
				len = Math.min(len, data.length);
				raf.write(data, 0, len);
				var = len;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't write to file \r\n" + e.getMessage());
		}
		return var;
	}

	public int writeByte(int value) {
		if (isClosed() || getMode() != Mode.Write)
			return -1;

		tmpbuf[0] = (byte) value;
		try {
			raf.write(tmpbuf, 0, 1);
			return 1;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't write to file \r\n" + e.getMessage());
		}
	}

	public int writeShort(int value) {
		if (isClosed() || getMode() != Mode.Write)
			return -1;

		tmpbuf[0] = (byte) (value & 0xFF);
		tmpbuf[1] = (byte) ((value >>> 8) & 0xFF);

		try {
			raf.write(tmpbuf, 0, 2);
			return 2;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't write to file \r\n" + e.getMessage());
		}
	}

	public int writeInt(int value) {
		int var = -1;
		if (isClosed() || getMode() != Mode.Write)
			return var;

		tmpbuf[0] = (byte) (value & 0xFF);
		tmpbuf[1] = (byte) ((value >>> 8) & 0xFF);
		tmpbuf[2] = (byte) ((value >>> 16) & 0xFF);
		tmpbuf[3] = (byte) ((value >>> 24) & 0xFF);

		try {
			raf.write(tmpbuf, 0, 4);
			var = 4;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't write to file \r\n" + e.getMessage());
		}

		return var;
	}

	@Override
	public int size() {
		int var = -1;
		if (isClosed())
			return var;

		try {
			if (fbuf != null)
				var = fbuf.capacity();
			else
				var = (int) raf.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return var;
	}

	@Override
	public int position() {
		int var = -1;
		if (isClosed())
			return var;

		try {
			if (fbuf != null)
				var = fbuf.position();
			else
				var = (int) raf.getChannel().position();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return var;
	}

	@Override
	public void toMemory() {
		/* nothing */ }

	@Override
	public byte[] getBytes() {
		int size = this.size();
		if (size > 0) {
			byte[] data = new byte[size];
			if (this.read(data) != -1)
				return data;
		}
		return null;
	}

	@Override
	public Group getParent() {
		return null;
	}

	@Override
	public String getExtension() {
		return ext;
	}

	@Override
	public int remaining() {
		return size() - position();
	}

	@Override
	public boolean hasRemaining() {
		return position() < size();
	}

	@Override
	public String getFullName() {
		return FileUtils.getFullName(getPath());
	}
}
