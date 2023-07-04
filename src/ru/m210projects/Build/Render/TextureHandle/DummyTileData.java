package ru.m210projects.Build.Render.TextureHandle;

import static com.badlogic.gdx.graphics.GL20.GL_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_LUMINANCE;
import static com.badlogic.gdx.graphics.GL20.GL_RGB;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DummyTileData extends TileData {

	public final ByteBuffer data;
	public final int width, height;
	public final PixelFormat fmt;
	public int format, internalformat;

	public DummyTileData(PixelFormat fmt, int width, int height) {
		this.width = width;
		this.height = height;
		this.fmt = fmt;
		this.data = ByteBuffer.allocateDirect(width * height * fmt.getLength()).order(ByteOrder.LITTLE_ENDIAN);

		switch (fmt) {
		case Rgba:
			format = GL_RGBA;
			internalformat = GL_RGBA;
			break;
		case Rgb:
			format = GL_RGB;
			internalformat = GL_RGB;
			break;
		case Pal8:
			format = GL_LUMINANCE;
			internalformat = GL_LUMINANCE;
			break;
		case Bitmap:
			format = GL_ALPHA;
			internalformat = GL_ALPHA;
			break;
		}
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
	public ByteBuffer getPixels() {
		data.rewind();
		return data;
	}

	@Override
	public int getGLType() {
		return GL_UNSIGNED_BYTE;
	}

	@Override
	public int getGLInternalFormat() {
		return internalformat;
	}

	@Override
	public int getGLFormat() {
		return format;
	}

	@Override
	public PixelFormat getPixelFormat() {
		return fmt;
	}

	@Override
	public boolean hasAlpha() {
		return true;
	}

	@Override
	public boolean isClamped() {
		return false;
	}

	@Override
	public boolean isHighTile() {
		return false;
	}
}
