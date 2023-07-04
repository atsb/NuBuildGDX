package ru.m210projects.Build.Types;

import ru.m210projects.Build.CRC32;

public class Palette {
	
	private long crc32;
	private final byte[] bytes;
	private final int[] values;
	
	public Palette() {
		bytes = new byte[768];
		values = new int[256];
	}
	
	public void update(byte[] palette)
	{
		System.arraycopy(palette, 0, bytes, 0, palette.length);
		int p = 0;
		int len = palette.length / 3;
		for(int i = 0; i < len; i++) 
			values[i] = (bytes[p++] & 0xFF) | ( (bytes[p++] & 0xFF) << 8 ) | ( (bytes[p++] & 0xFF) << 16 ) | (255 << 24);
		crc32 = CRC32.getChecksum(bytes);
	}
	
	public long getCrc32()
	{
		return crc32;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
	public int getRed(int index)
	{
		return (values[index] & 0x000000FF);
	}
	
	public int getGreen(int index)
	{
		return (values[index] & 0x0000FF00) >> 8;
	}
	
	public int getBlue(int index)
	{
		return (values[index] & 0x00FF0000) >> 16;
	}
	
	public int getRGB(int index)
	{
		return values[index] & 0x00FFFFFF;
	}
	
	public int getRGBA(int index, byte alphaMask)
	{
		return getRGB(index) | (alphaMask << 24);
	}
	
	public int getRGBA(int index)
	{
		return values[index];
	}
}
