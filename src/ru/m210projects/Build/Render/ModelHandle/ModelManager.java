package ru.m210projects.Build.Render.ModelHandle;

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDModel;
import ru.m210projects.Build.Render.ModelHandle.Voxel.GLVoxel;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelData;
import ru.m210projects.Build.Render.TextureHandle.GLTile;
import ru.m210projects.Build.Render.Types.GLFilter;
import ru.m210projects.Build.Render.Types.Tile2model;
import ru.m210projects.Build.Script.ModelsInfo;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Settings.GLSettings;

public abstract class ModelManager {

	protected ModelsInfo mdInfo;
	protected GLModel[] models = new GLModel[MAXTILES];
	protected HashMap<ModelInfo, GLModel> tile2model = new HashMap<ModelInfo, GLModel>();

	public ModelManager setModelsInfo(ModelsInfo mdInfo) {
		this.dispose();

		this.mdInfo = mdInfo;
		return this;
	}

	public Iterator<GLTile> getSkins(int tile) {
		GLModel model = models[tile];
		if (model != null) {
			return model.getSkins();
		}

		return null;
	}

	public void setTextureFilter(GLFilter filter, int anisotropy) {
		if (mdInfo == null)
			return;

		Iterator<GLModel> it = tile2model.values().iterator();
		while (it.hasNext()) {
			GLModel model = it.next();

			if (model == null || model.getType() == Type.Voxel)
				continue;

			Iterator<GLTile> sk = model.getSkins();
			while (sk.hasNext()) {
				GLTile tex = sk.next();
				tex.bind();
				tex.setupTextureFilter(filter, anisotropy);
			}
		}
	}

	public void clearSkins(int tile, boolean bit8only) {
		GLModel model = models[tile];

		if (model != null && !bit8only) {
			model.clearSkins();

			Tile2model param = mdInfo.getParams(tile);
			while (param.next != null) {
				param = param.next;
				model = tile2model.get(param.model);
				if (model != null)
					model.clearSkins();
			}
		}
	}

	public boolean hasModelInfo(int tile) {
		if (mdInfo == null)
			return false;

		return mdInfo.getModelInfo(tile) != null;
	}

	public boolean hasVoxelInfo(int tile) {
		if (mdInfo == null)
			return false;

		return mdInfo.getVoxelInfo(tile) != null;
	}

	public GLModel getModel(int tile, int pal) {
		if (mdInfo == null)
			return null;

		Tile2model param = mdInfo.getParams(tile);
		if (param == null)
			return null;

		ModelInfo model;
		if ((model = param.model) != null && model.getType() != Type.Voxel) {

			if (param.next != null && param.palette != pal) {
				while (param.next != null) {
					param = param.next;
					if (param.palette == pal) {
						model = param.model;

						GLModel glmodel;
						if ((glmodel = tile2model.get(model)) != null && glmodel.getType() != Type.Voxel)
							return glmodel;
						return loadModel(model);
					}
				}
			}

			GLModel glmodel = models[tile];
			if (glmodel != null && glmodel.getType() != Type.Voxel)
				return glmodel;

			if ((glmodel = tile2model.get(model)) != null && glmodel.getType() != Type.Voxel) {
				models[tile] = glmodel;
				return glmodel;
			}

			GLModel out = loadModel(model);
			if (out != null)
				return models[tile] = out;
			else {
				Console.Println("Removing model of tile " + tile + " due to errors.", OSDTEXT_RED);
				mdInfo.removeModelInfo(model);
			}
		}

		return null;
	}

	protected GLModel loadModel(ModelInfo model) {
		try {
//			long startticks = System.nanoTime();
			GLModel out = allocateModel(model);
//			long etime = System.nanoTime() - startticks;
//			System.out
//					.println("Load " + model.getType() + " model: " + tile + "... " + (etime / 1000000.0f) + " ms");
			if (out != null) {
				tile2model.put(model, out);
				return out;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public GLModel getVoxel(int tile) {
		if (mdInfo == null)
			return null;

		VoxelInfo model;
		if ((model = mdInfo.getVoxelInfo(tile)) != null) {
			GLModel glmodel = models[tile];
			if (glmodel != null && glmodel.getType() == Type.Voxel)
				return glmodel;

			if ((glmodel = tile2model.get(model)) != null && glmodel.getType() == Type.Voxel) {
				models[tile] = glmodel;
				return glmodel;
			}

//			long startticks = System.nanoTime();
			GLModel out = allocateVoxel(model.getData(), 0, model.getFlags());
//			long etime = System.nanoTime() - startticks;
//			System.out.println("Load voxel model: " + tile + "... " + (etime / 1000000.0f) + " ms");

			if (out != null) {
				tile2model.put(model, out);
				return models[tile] = out;
			} else {
				Console.Println("Removing voxel of tile " + tile + " due to errors.", OSDTEXT_RED);
				mdInfo.removeModelInfo(model);
			}
		}

		return null;
	}

	public abstract GLVoxel allocateVoxel(VoxelData vox, int voxmip, int flags);

	public abstract GLModel allocateModel(ModelInfo modelInfo);

	public void dispose() {
		Iterator<GLModel> it = tile2model.values().iterator();
		while (it.hasNext()) {
			GLModel glmodel = it.next();
			glmodel.dispose();
		}

		Arrays.fill(models, null);
		tile2model.clear();
		mdInfo = null;
	}

	protected int getTile(GLModel model) {
		for (int i = MAXTILES - 1; i >= 0; i--) {
			GLModel glmodel = models[i];
			if (glmodel == null)
				continue;

			if (glmodel == model) {
				return i;
			}
		}

		return -1;
	}

	public int getNumModels() {
		return tile2model.values().size();
	}

	public void preload(int picnum, int pal, boolean skinPreload) {
		if (GLSettings.useModels.get() && hasModelInfo(picnum)) {
			GLModel model = getModel(picnum, pal);
			if (model != null && skinPreload) {
				if(model instanceof MDModel) {
					int skinnum = mdInfo.getParams(picnum).skinnum;
					((MDModel) model).loadSkins(pal, skinnum);
				}
				return;
			}
		}

		if (BuildSettings.useVoxels.get() && hasVoxelInfo(picnum)) {
			GLVoxel voxel = (GLVoxel) getVoxel(picnum);
			if (voxel != null && skinPreload) {
				voxel.getSkin(pal);
			}
		}
	}
}
