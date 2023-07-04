package ru.m210projects.Build.Render.ModelHandle.Voxel;

import static ru.m210projects.Build.Engine.curpalette;
import static ru.m210projects.Build.Engine.palookup;

import ru.m210projects.Build.Render.TextureHandle.DummyTileData;
import ru.m210projects.Build.Types.Tile;

public class VoxelSkin extends DummyTileData {

	public VoxelSkin(PixelFormat fmt, Tile tile, int dapal) {
		super(fmt, tile.getWidth(), tile.getHeight());

		if (fmt != PixelFormat.Pal8) {
			int wpptr, wp, dacol;
			for (int x, y = 0; y < height; y++) {
				wpptr = y * width;
				for (x = 0; x < width; x++, wpptr++) {
					wp = wpptr << 2;
					dacol = tile.data[wpptr] & 0xFF;
					dacol = palookup[dapal][dacol] & 0xFF;

					data.putInt(wp, curpalette.getRGB(dacol) + (255 << 24));
				}
			}
		} else
			data.put(tile.data, 0, width * height);
	}

	@Override
	public boolean isClamped() {
		return true;
	}

	@Override
	public boolean hasAlpha() {
		return false;
	}

}
