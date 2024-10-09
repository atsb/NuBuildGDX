package ru.m210projects.Build.Render.TextureHandle;

import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE;
import static ru.m210projects.Build.Engine.DETAILPAL;
import static ru.m210projects.Build.Engine.GLOWPAL;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class GLTile extends GLTexture implements Comparable<GLTile> {

	public enum FlagType {
		Clamped(0), HighTile(1), SkyboxFace(2), HasAlpha(3), Invalidated(7);

		private final int bit;

		FlagType(int bit) {
			this.bit = (1 << bit);
		}

		public int getBit() {
			return bit;
		}

		public boolean hasBit(int flags) {
			return (flags & bit) != 0;
		}
	}

	protected int width, height;
	private boolean isAllocated;
	protected PixelFormat fmt;
	protected float anisotropicFilterLevel = 1.0f;

	protected int flags;
	protected byte skyface;
	protected Hicreplctyp hicr;
	protected float scalex, scaley;

	protected int palnum;
	protected GLTile next;

	protected GLTile(PixelFormat fmt, int width, int height) {
		super(GL_TEXTURE_2D);
		this.width = width;
		this.height = height;
		this.fmt = fmt;
		this.isAllocated = false;

		this.scalex = this.scaley = 1.0f;
	}

	protected GLTile(GLTile src) {
		super(src.glTarget, src.glHandle);
		this.width = src.width;
		this.height = src.height;
		this.fmt = src.fmt;
		this.isAllocated = src.isAllocated;
		this.palnum = src.palnum;
		this.anisotropicFilterLevel = src.anisotropicFilterLevel;
		this.flags = src.flags;
		this.skyface = src.skyface;
		this.hicr = src.hicr;

		this.scalex = src.scalex;
		this.scaley = src.scaley;
	}

	public GLTile(TileData pic, int palnum, boolean useMipMaps) {
		this(pic.getPixelFormat(), pic.getWidth(), pic.getHeight());
		this.palnum = palnum;

		alloc(pic);

		setClamped(pic.isClamped());
		setHasAlpha(pic.hasAlpha());

		this.scalex = this.scaley = 1.0f;
	}

	protected void alloc(TileData pic) {
		setupTextureWrap(!pic.isClamped() ? TextureWrap.Repeat : TextureWrap.ClampToEdge);

		this.isAllocated = true;
	}

	public PixelFormat getPixelFormat() {
		return fmt;
	}

	public void update(TileData pic, int pal, boolean useMipMaps) {
		this.bind();

		if(pic != null) {
			int width = pic.getWidth();
			int height = pic.getHeight();
			if (pic instanceof PixmapTileData) {
				width = ((PixmapTileData) pic).getTileWidth();
				height = ((PixmapTileData) pic).getTileHeight();
			}

			// Realloc, because the texture size isn't match
			if ((getWidth() != width || getHeight() != height)) {
				delete();

				this.glHandle = BuildGdx.gl.glGenTexture();
				this.width = pic.getWidth();
				this.height = pic.getHeight();

				alloc(pic);
			} else {
				if (!isAllocated)
					alloc(pic);
				else
					BuildGdx.gl.glTexSubImage2D(glTarget, 0, 0, 0, pic.getWidth(), pic.getHeight(), pic.getGLFormat(),
							GL_UNSIGNED_BYTE, pic.getPixels());
			}

			setClamped(pic.isClamped());
			setHasAlpha(pic.hasAlpha());

			pic.dispose();
		}

		this.palnum = pal;
	}

	public void setupTextureWrap(TextureWrap wrap) {
		unsafeSetWrap(wrap, wrap, true);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void unbind() {
		BuildGdx.gl.glBindTexture(glTarget, 0);
	}

	@Override
	public void bind() {
		BuildGdx.gl.glBindTexture(glTarget, glHandle);
	}

	@Override
	public void delete() {
		if (glHandle != 0) {
			if (BuildGdx.gl != null)
				BuildGdx.gl.glDeleteTexture(glHandle);
			glHandle = 0;
		}
	}

	@Override
	public GLTile clone() {
		return new GLTile(this);
	}

	public boolean isClamped() {
		return FlagType.Clamped.hasBit(flags);
	}

	public void setClamped(boolean mode) {
		setBit(mode, FlagType.Clamped);
	}

	public boolean isHighTile() {
		return FlagType.HighTile.hasBit(flags);
	}

	public void setHighTile(Hicreplctyp si) {
		this.hicr = si;
		setBit(si != null, FlagType.HighTile);
	}

	public boolean isSkyboxFace() {
		return FlagType.SkyboxFace.hasBit(flags);
	}

	public void setSkyboxFace(int facen) {
		this.skyface = (byte) facen;
		if (facen > 0)
			setBit(true, FlagType.SkyboxFace);
	}

	public boolean hasAlpha() {
		return FlagType.HasAlpha.hasBit(flags);
	}

	public void setHasAlpha(boolean mode) {
		setBit(mode, FlagType.HasAlpha);
	}

	public boolean isInvalidated() {
		return FlagType.Invalidated.hasBit(flags);
	}

	public void setInvalidated(boolean mode) {
		setBit(mode, FlagType.Invalidated);
	}

	public int getPal() {
		return palnum;
	}

	public float getHiresXScale() {
		return hicr.xscale;
	}

	public float getHiresYScale() {
		return hicr.yscale;
	}

	public boolean isGlowTexture() {
		return hicr != null && (hicr.palnum == GLOWPAL);
	}

	public boolean isDetailTexture() {
		return hicr != null && (hicr.palnum == DETAILPAL);
	}

	public float getXScale() {
		return scalex;
	}

	public float getYScale() {
		return scaley;
	}

	public float getAlphaCut() {
		return hicr != null ? hicr.alphacut : 0.0f;
	}

	private void setBit(boolean mode, FlagType bit) {
		if (mode)
			flags |= bit.getBit();
		else
			flags &= ~bit.getBit();
	}

	@Override
	public String toString() {
		String out = "id = " + glHandle + " [" + width + "x" + height + ", ";
		out += "pal = " + palnum + ", ";
		out += "clamp = " + isClamped() + "]";

		return out;
	}

	@Override
	public int compareTo(GLTile src) {
		if (src == null)
			return 0;

		return this.palnum - src.palnum;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public boolean isManaged() {
		return false;
	}

	@Override
	protected void reload() {
	}
}
