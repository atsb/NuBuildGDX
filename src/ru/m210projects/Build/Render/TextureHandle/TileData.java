package ru.m210projects.Build.Render.TextureHandle;

import java.nio.ByteBuffer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.Render.GLInfo;

public abstract class TileData {

	public enum PixelFormat {
		Rgb(3), Rgba(4), Pal8(1), Bitmap(1);

		private final int bytes;

		PixelFormat(int bytes) {
			this.bytes = bytes;
		}

		public int getLength() {
			return bytes;
		}
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract ByteBuffer getPixels();

	public abstract int getGLType();

	public abstract int getGLInternalFormat();

	public abstract int getGLFormat();

	public abstract PixelFormat getPixelFormat();

	public abstract boolean hasAlpha();

	public abstract boolean isClamped();

	public abstract boolean isHighTile();

	public void dispose() {
		/* for implementing */
	}

	public void save(String name) {
		int width = getWidth();
		int height = getHeight();

		ByteBuffer pixels = getPixels();
		int bytes = getPixelFormat().getLength();
		if (bytes == 1) {
			FileResource raw = BuildGdx.compat.open(name + ".raw", Path.Absolute, Mode.Write);
			String path = raw.getPath();
			raw.writeBytes(pixels, width * height);
			raw.close();

			System.out.println(path + " saved!");
			return;
		}

		name += ".png";
		Pixmap pixmap = new Pixmap(width, height,
				getPixelFormat() == PixelFormat.Rgba ? Format.RGBA8888 : Format.RGB888);
		float[] color = new float[4];
		if(bytes == 3)
			color[3] = 1.0f;

		for (int i = 0; i < (width * height); i++) {
			for (int c = 0; c < bytes; c++)
				color[c] = (pixels.get((i * bytes) + c) & 0xFF) / 255.f;

			pixmap.setColor(color[0], color[1], color[2], color[3]);
			int row = (int) Math.floor(i / width);
			int col = i % width;
			pixmap.drawPixel(col, row);
		}

		FileHandle f = new FileHandle(name);
		PixmapIO.writePNG(f, pixmap);

		System.out.println(f.file().getAbsolutePath() + " saved!");
		pixmap.dispose();
	}

	protected int calcSize(int size) {
		int nsize = 1;
		if (GLInfo.texnpot == 0) {
			for (; nsize < size; nsize *= 2)
				;
			return nsize;
		}
		return size == 0 ? 1 : size;
	}
}
