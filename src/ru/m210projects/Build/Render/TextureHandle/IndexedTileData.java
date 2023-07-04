package ru.m210projects.Build.Render.TextureHandle;

import static com.badlogic.gdx.graphics.GL20.GL_LUMINANCE;
import static com.badlogic.gdx.graphics.GL20.GL_RGB;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Types.Tile;

public class IndexedTileData extends TileData {

	public final ByteBuffer data;
	public final boolean hasalpha;
	public final int width, height;
	public final boolean clamped;

	public IndexedTileData(Tile tile, boolean clamped, boolean alpha, int expflag) {
		byte[] data = tile.data;
		int tsizx = tile.getWidth();
		int tsizy = tile.getHeight();

		if (data != null && (data.length == 0 || tile.getSize() > data.length))
			data = null;

		int xsiz = tsizx;
		int ysiz = tsizy;
		if ((expflag & 1) != 0)
			xsiz = calcSize(tsizx);
		if ((expflag & 2) != 0)
			ysiz = calcSize(tsizy);

		ByteBuffer buffer = ByteBuffer.allocateDirect(data != null ? xsiz * ysiz : 1).order(ByteOrder.LITTLE_ENDIAN);

		boolean hasalpha = false;
		if (data == null) {
			buffer.put(0, (byte) 255);
			// tsizx = tsizy = 1;
			hasalpha = true;
			// if (buffer.getBuffer().capacity() < xsiz * ysiz)
			xsiz = ysiz = 1;
		} else {
			int dptr;
			int sptr = 0;
			if (clamped) {
				for (int y = ysiz - 1; y >= 0; y--) {
					sptr = y >= tsizy ? 0 : tsizx;
					dptr = (xsiz * y + sptr);
					for (int x = sptr; x < xsiz; x++)
						buffer.put(dptr++, (byte) 255);
				}

				sptr = 0;
				for (int i = 0, j; i < tsizx; i++) {
					dptr = i;
					for (j = 0; j < tsizy; j++) {
						buffer.put(dptr, data[sptr++]);
						dptr += xsiz;
					}
				}
				hasalpha = true;
			} else {
				int p, len = data.length;
				for (int i = 0, j; i < xsiz; i++) {
					p = 0;
					dptr = i;
					for (j = 0; j < ysiz; j++) {
						buffer.put(dptr, data[sptr + p++]);
						dptr += xsiz;
						if (p >= tsizy)
							p = 0;
					}
					if ((sptr += tsizy) >= len)
						sptr = 0;
				}
			}
		}

		this.width = xsiz;
		this.height = ysiz;
		this.hasalpha = hasalpha;
		this.data = buffer;
		this.clamped = clamped;
	}

	@Override
	public int getGLType() {
		return GL_UNSIGNED_BYTE;
	}

	@Override
	public ByteBuffer getPixels() {
		data.rewind();
		return data;
	}

	@Override
	public int getGLInternalFormat() {
		return (hasalpha ? GL_RGBA : GL_RGB);
	}

	@Override
	public int getGLFormat() {
		return GL_LUMINANCE; // GL_RED;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public PixelFormat getPixelFormat() {
		return PixelFormat.Pal8;
	}

	@Override
	public boolean hasAlpha() {
		return hasalpha;
	}

	@Override
	public boolean isClamped() {
		return clamped;
	}

	@Override
	public boolean isHighTile() {
		return false;
	}
}
