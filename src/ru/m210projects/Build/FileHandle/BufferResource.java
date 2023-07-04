package ru.m210projects.Build.FileHandle;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import ru.m210projects.Build.Gameutils;

public class BufferResource extends ByteArrayOutputStream {

	private static final byte[] tmpbuf = new byte[1024];

	public BufferResource() {
		super();
	}

	public BufferResource(int size) {
		super(size);
	}

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
			throw new UnsupportedOperationException("Couldn't write. \r\n + Not implemented!");

		if (len != 0)
			return writeBytes(array, len);

		return -1;
	}

	public int writeBytes(Object obj, int len) {
		int var = -1;

		try {
			byte[] data = null;
			if (obj instanceof Byte) {
				int rem = len;
				while (rem > 0) {
					int l = Math.min(rem, tmpbuf.length);
					Gameutils.fill(tmpbuf, 0, l, (byte) obj);
					this.write(tmpbuf, 0, l);
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
				this.write(data, 0, len);
				var = len;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't write to file \r\n" + e.getMessage());
		}
		return var;
	}

	public int writeBoolean(boolean value) {
		return writeByte(value ? 1 : 0);
	}

	public int writeByte(int value) {
		tmpbuf[0] = (byte) value;

		this.write(tmpbuf, 0, 1);
		return 1;
	}

	public int writeShort(int value) {
		tmpbuf[0] = (byte) ((value) & 0xFF);
		tmpbuf[1] = (byte) ((value >>> 8) & 0xFF);

		this.write(tmpbuf, 0, 2);
		return 2;
	}

	public int writeInt(int value) {
		tmpbuf[0] = (byte) ((value) & 0xFF);
		tmpbuf[1] = (byte) ((value >>> 8) & 0xFF);
		tmpbuf[2] = (byte) ((value >>> 16) & 0xFF);
		tmpbuf[3] = (byte) ((value >>> 24) & 0xFF);

		this.write(tmpbuf, 0, 4);
		return 4;
	}

	public int writeLong(long value) {
		tmpbuf[0] = (byte) ((value) & 0xFF);
		tmpbuf[1] = (byte) ((value >>> 8) & 0xFF);
		tmpbuf[2] = (byte) ((value >>> 16) & 0xFF);
		tmpbuf[3] = (byte) ((value >>> 24) & 0xFF);
		tmpbuf[4] = (byte) ((value >>> 32) & 0xFF);
		tmpbuf[5] = (byte) ((value >>> 40) & 0xFF);
		tmpbuf[6] = (byte) ((value >>> 48) & 0xFF);
		tmpbuf[7] = (byte) ((value >>> 56) & 0xFF);

		this.write(tmpbuf, 0, 8);
		return 8;
	}

	public void skip(int count) {
		tmpbuf[0] = 0;
		while (count-- > 0)
			this.write(tmpbuf, 0, 1);
	}

	@Override
	public int size() {
		return count;
	}

	public byte[] getBytes() {
		return buf;
	}
}
