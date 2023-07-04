package ru.m210projects.Build.Render.TextureHandle;

import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;

public class PixmapTileData extends TileData {

	private Pixmap pixmap;
	private final boolean clamped;
	private final int width;
	private final int height;

	public PixmapTileData(Pixmap pixmap, boolean clamped, int expflag) {
		if (pixmap.getFormat() == Format.Alpha || pixmap.getFormat() == Format.Intensity
				|| pixmap.getFormat() == Format.LuminanceAlpha)
			pixmap = convert(pixmap);

		this.pixmap = pixmap;
		this.clamped = clamped;
		this.width = pixmap.getWidth();
		this.height = pixmap.getHeight();

		int xsiz = width;
		int ysiz = height;
		if ((expflag & 1) != 0)
			xsiz = calcSize(width);
		if ((expflag & 2) != 0)
			ysiz = calcSize(height);

		if (xsiz != width || ysiz != height) {
			Pixmap npix = new Pixmap(xsiz, ysiz, !clamped ? pixmap.getFormat() : Format.RGBA8888);
			npix.setFilter(Filter.NearestNeighbour);

			if (!clamped) {
				for (int x = 0, y; x < xsiz; x += width) {
					for (y = 0; y < ysiz; y += height) {
						npix.drawPixmap(pixmap, x, y);
					}
				}
			} else
				npix.drawPixmap(pixmap, 0, 0);

			pixmap.dispose();
			this.pixmap = npix;
		}
	}

	private Pixmap convert(Pixmap pixmap) {
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();

		Pixmap npix = new Pixmap(width, height, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		boolean bytes2 = pixmap.getFormat() == Format.LuminanceAlpha;

		for (int i = 0; i < (width * height); i++) {
			float c = (pixels.get() & 0xFF) / 255.f;
			float a = 1.0f;
			if(bytes2)
				a = (pixels.get() & 0xFF) / 255.f;
			npix.setColor(c, c, c, a);
			int row = (int) Math.floor(i / width);
			int col = i % width;
			npix.drawPixel(col, row);
		}

		pixmap.dispose();
		return npix;
	}

	@Override
	public boolean hasAlpha() {
		return pixmap.getFormat() == Format.RGBA4444 || pixmap.getFormat() == Format.RGBA8888;
	}

	@Override
	public boolean isClamped() {
		return clamped;
	}

	public int getTileWidth() {
		return width;
	}

	public int getTileHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return pixmap.getWidth();
	}

	@Override
	public int getHeight() {
		return pixmap.getHeight();
	}

	@Override
	public ByteBuffer getPixels() {
		return pixmap.getPixels();
	}

	@Override
	public int getGLType() {
		return pixmap.getGLType();
	}

	@Override
	public int getGLInternalFormat() {
		return pixmap.getGLInternalFormat();
	}

	@Override
	public int getGLFormat() {
		return pixmap.getGLFormat();
	}

	@Override
	public boolean isHighTile() {
		return true;
	}

	@Override
	public PixelFormat getPixelFormat() {
		return PixelFormat.Rgb;
	}

	@Override
	public void dispose() {
		pixmap.dispose();
	}
}
