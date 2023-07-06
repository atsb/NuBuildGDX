package ru.m210projects.Build.Render.GdxRender;

import static ru.m210projects.Build.Engine.DETAILPAL;
import static ru.m210projects.Build.Engine.GLOWPAL;
import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.RESERVEDPALS;
import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE0;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.GdxRender.Shaders.ShaderManager.Shader;
import ru.m210projects.Build.Render.ModelHandle.GLModel;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo;
import ru.m210projects.Build.Render.ModelHandle.ModelManager;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDSkinmap;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD2.MD2Info;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD2.MD2ModelGL20;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD3.MD3Info;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD3.MD3ModelGL20;
import ru.m210projects.Build.Render.ModelHandle.Voxel.GLVoxel;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelData;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelGL20;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelSkin;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.Hicreplctyp;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;
import ru.m210projects.Build.Render.TextureHandle.PixmapTileData;
import ru.m210projects.Build.Render.TextureHandle.TileData;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class GDXModelManager extends ModelManager {

	private final GDXRenderer parent;

	public GDXModelManager(GDXRenderer parent) {
		this.parent = parent;
	}

	@Override
	public GLVoxel allocateVoxel(VoxelData vox, int voxmip, int flags) {
		return new VoxelGL20(vox, voxmip, flags) {

			@Override
			public GLTile getSkin(int pal) {
				PixelFormat fmt = parent.getTexFormat();
				if (palookup[pal] == null || fmt == PixelFormat.Pal8)
					pal = 0;

				if (texid[pal] == null) {
					long startticks = System.nanoTime();
					TileData dat = new VoxelSkin(fmt, skinData, pal);
					GLTile dst = parent.textureCache.newTile(dat, pal, false);

					dst.unsafeSetFilter(TextureFilter.Nearest, TextureFilter.Nearest, true);
					dst.unsafeSetAnisotropicFilter(1, true);
					texid[pal] = dst;
					long etime = System.nanoTime() - startticks;
					System.out.println("Load voxskin: p" + pal + " for tile " + getTile(this) + "... "
							+ (etime / 1000000.0f) + " ms");
				}

				return texid[pal];
			}

			@Override
			public void setTextureParameters(GLTile tile, int pal, int shade, int visibility, float alpha) {
				if (tile.getPixelFormat() == TileData.PixelFormat.Pal8) {
					parent.manager.textureTransform(parent.texture_transform.idt(), 0);
					parent.manager.textureParams8(pal, shade, alpha, true);
					((IndexedShader) parent.manager.getProgram()).setVisibility((int) (-visibility / 64.0f));
				} else {
					parent.manager.color(1.0f, 1.0f, 1.0f, alpha);
					parent.calcFog(pal, shade, visibility);
				}
			}

			@Override
			public ShaderProgram getShader() {
				return parent.manager.getProgram();
			}
		};
	}

	@Override
	public GLModel allocateModel(ModelInfo modelInfo) {
		switch (modelInfo.getType()) {
		case Md2:
			return new MD2ModelGL20((MD2Info) modelInfo) {
				@Override
				public ShaderProgram getShader() {
					return parent.manager.getProgram();
				}

				@Override
				protected int bindSkin(int pal, int skinnum) {
					return bindMDSkin(this, pal, skinnum, 0);
				}

				@Override
				protected GLTile loadTexture(String skinfile, int palnum) {
					return loadMDTexture(this, skinfile, palnum);
				}
			};
		case Md3:
			return new MD3ModelGL20((MD3Info) modelInfo) {
				@Override
				public ShaderProgram getShader() {
					return parent.manager.getProgram();
				}

				@Override
				protected int bindSkin(int pal, int skinnum, int surfnum) {
					return bindMDSkin(this, pal, skinnum, surfnum);
				}

				@Override
				protected GLTile loadTexture(String skinfile, int palnum) {
					return loadMDTexture(this, skinfile, palnum);
				}
			};
		default:
			return null;
		}
	}

	protected GLTile loadMDTexture(MDModel m, String skinfile, int palnum) {
		GLTile texidx = findLoadedMultitexture(skinfile, palnum);
		if (texidx != null)
			return texidx;

		Resource res = BuildGdx.cache.open(skinfile, 0);
		if (res == null) {
			Console.Println("Skin " + skinfile + " not found.", Console.OSDTEXT_YELLOW);
			return null;
		}

//		long startticks = System.currentTimeMillis();
		try {
			byte[] data = res.getBytes();
			Pixmap pix = new Pixmap(data, 0, data.length);
			texidx = parent.textureCache.newTile(new PixmapTileData(pix, true, 0), 0, true);
			if (palnum == DETAILPAL || palnum == GLOWPAL)
				texidx.setHighTile(new Hicreplctyp(palnum));
			m.usesalpha = true;
		} catch (Exception e) {
			Console.Println("Couldn't load file: " + skinfile, Console.OSDTEXT_YELLOW);
			return null;
		} finally {
			res.close();
		}
		texidx.setupTextureWrap(TextureWrap.Repeat);

        return texidx;
	}

	protected int bindMDSkin(MDModel m, int pal, int skinnum, int surfnum) {
		int texunits = -1;
		GLTile texid = m.getSkin(pal, skinnum, surfnum);
		if (texid != null) {
			parent.textureCache.bind(texid);
			if (parent.manager.getShader() == null || parent.isSkyShader()
					|| texid.getPixelFormat() != parent.manager.getPixelFormat()) {
				parent.switchShader(
						texid.getPixelFormat() != PixelFormat.Pal8 ? Shader.RGBWorldShader : Shader.IndexedWorldShader);
			}

			texunits = GL_TEXTURE0;
        }

		return texunits;
	}

	protected GLTile findLoadedMultitexture(String skinfile, int palnum) {
		// possibly fetch an already loaded multitexture :_)
		if (palnum >= (MAXPALOOKUPS - RESERVEDPALS)) {
			for (int i = MAXTILES - 1; i >= 0; i--) {
				GLModel m = models[i];
				if (!(m instanceof MDModel))
					continue;

				for (MDSkinmap sk = ((MDModel) m).skinmap; sk != null; sk = sk.next)
					if (sk.fn.equalsIgnoreCase(skinfile) && sk.texid != null) {
						if (sk.palette != palnum) {
							GLTile texidx = sk.texid.clone();
							if (palnum == DETAILPAL || palnum == GLOWPAL) {
								texidx.setHighTile(new Hicreplctyp(palnum));
								texidx.update(null, palnum, true);
								return texidx;
							}
						}

						return sk.texid;
					}
			}
		}

		return null;
	}

}
