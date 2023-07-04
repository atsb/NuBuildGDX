package ru.m210projects.Build.Render.ModelHandle;

import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelData;

public class VoxelInfo extends ModelInfo {

	protected VoxelData vox;
	public VoxelInfo(VoxelData vox) {
		super(null, Type.Voxel);
		this.vox = vox;
		this.scale = 65536.0f;
	}

	public VoxelData getData() {
		return vox;
	}

}
