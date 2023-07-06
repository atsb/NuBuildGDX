package ru.m210projects.Build.Render.Polymost;

import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static ru.m210projects.Build.Engine.DETAILPAL;
import static ru.m210projects.Build.Engine.GLOWPAL;
import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.RESERVEDPALS;
import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Render.Types.GL10.GL_MODELVIEW;
import static ru.m210projects.Build.Render.Types.GL10.GL_TEXTURE0;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.ModelHandle.GLModel;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo;
import ru.m210projects.Build.Render.ModelHandle.ModelManager;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDSkinmap;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD2.MD2Info;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD2.MD2ModelGL10;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD3.MD3Info;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD3.MD3ModelGL10;
import ru.m210projects.Build.Render.ModelHandle.Voxel.GLVoxel;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelData;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelGL10;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelSkin;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.TextureHandle.Hicreplctyp;
import ru.m210projects.Build.Render.TextureHandle.PixmapTileData;
import ru.m210projects.Build.Render.TextureHandle.TileData;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class PolymostModelManager extends ModelManager {

	protected Polymost parent;

	public PolymostModelManager(Polymost parent) {
		this.parent = parent;
	}

	@Override
	public GLVoxel allocateVoxel(VoxelData vox, int voxmip, int flags) {
		return new VoxelGL10(vox, voxmip, flags, true) {

			@Override
			public GLTile getSkin(int pal) {
				PixelFormat fmt = parent.getTextureFormat();
				if (palookup[pal] == null || fmt == PixelFormat.Pal8)
					pal = 0;

				if (texid[pal] == null) {
//					long startticks = System.nanoTime();
					TileData dat = new VoxelSkin(fmt, skinData, pal);
					GLTile dst = parent.textureCache.newTile(dat, pal, false);

					dst.unsafeSetFilter(TextureFilter.Nearest, TextureFilter.Nearest, true);
					dst.unsafeSetAnisotropicFilter(1, true);
					texid[pal] = dst;
                }

				return texid[pal];
			}

			@Override
			public void setTextureParameters(GLTile tile, int pal, int shade, int visibility, float alpha) {
				if (tile.getPixelFormat() == PixelFormat.Pal8) {
					if (!parent.getShader().isBinded())
						parent.getShader().begin();
					parent.getShader().setTextureParams(pal, shade);
					parent.getShader().setDrawLastIndex(true);
					parent.getShader().setTransparent(alpha);
					parent.getShader().setVisibility(visibility);
				}
			}
		};
	}

	@Override
	public GLModel allocateModel(ModelInfo modelInfo) {
		switch (modelInfo.getType()) {
		case Md3:
			return new MD3ModelGL10((MD3Info) modelInfo) {
				@Override
				protected GLTile loadTexture(String skinfile, int palnum) {
					return loadMDTexture(this, skinfile, palnum);
				}

				@Override
				protected int bindSkin(int pal, int skinnum, int surfnum) {
					return bindMDSkin(this, pal, skinnum, surfnum);
				}
			};
		case Md2:
			return new MD2ModelGL10((MD2Info) modelInfo) {
				@Override
				protected int bindSkin(int pal, int skinnum) {
					return bindMDSkin(this, pal, skinnum, 0);
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
			parent.bind(texid);

			texunits = GL_TEXTURE0;
			if (Console.Geti("r_detailmapping") != 0) {
				if ((texid = m.getSkin(DETAILPAL, skinnum, surfnum)) != null) {
					if (!texid.isDetailTexture())
						System.err.println("Wtf detail!");
					BuildGdx.gl.glActiveTexture(++texunits);
					BuildGdx.gl.glEnable(GL_TEXTURE_2D);
					parent.bind(texid);
					parent.setupTextureDetail(texid);

					for (MDSkinmap sk = m.skinmap; sk != null; sk = sk.next)
						if (sk.palette == DETAILPAL && skinnum == sk.skinnum && surfnum == sk.surfnum) {
							float f = sk.param;
							BuildGdx.gl.glMatrixMode(GL_TEXTURE);
							BuildGdx.gl.glLoadIdentity();
							BuildGdx.gl.glScalef(f, f, 1.0f);
							BuildGdx.gl.glMatrixMode(GL_MODELVIEW);
						}
				}
			}

			if (Console.Geti("r_glowmapping") != 0) {
				if ((texid = m.getSkin(GLOWPAL, skinnum, surfnum)) != null) {
					if (!texid.isGlowTexture())
						System.err.println("Wtf glow! " + surfnum);

					BuildGdx.gl.glActiveTexture(++texunits);
					BuildGdx.gl.glEnable(GL_TEXTURE_2D);
					parent.bind(texid);
					parent.setupTextureGlow(texid);
				}
			}
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
