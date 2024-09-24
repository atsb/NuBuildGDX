// This file is part of BuildGDX.
// Copyright (C) 2017-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Render.TextureHandle;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;

public class TileAtlas {

	public int atlasWidth, atlasHeight; // maximum one atlas size
	public int gridWidth, gridHeight;
	public int tilesPerAtlas;
	public int colsPerAtlas;
	public int rowsPerAtlas;
	protected PixelFormat fmt;
	protected int pos = 0;

	public List<TileData> atlas = new ArrayList<TileData>();
	public HashMap<Integer, TileUnit> units = new HashMap<Integer, TileUnit>();

	public TileAtlas(PixelFormat fmt, int gridWidth, int gridHeight, int numtiles) {
	}

	public TileAtlas(PixelFormat fmt, int atlasWidth, int atlasHeight, int gridWidth, int gridHeight, boolean pow2) {
		this.fmt = fmt;

		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;

		this.colsPerAtlas = atlasWidth / gridWidth;
		this.rowsPerAtlas = atlasHeight / gridHeight;

		this.tilesPerAtlas = colsPerAtlas * rowsPerAtlas;
	}

	public void reloadTile(int nTile, TileData pic) {
		TileUnit tile = getTile(nTile);
		if (tile == null) {
			addTile(nTile, pic);
			return;
		}

		if (pic.getWidth() > gridWidth || pic.getHeight() > gridHeight) {
			System.err.println("Error: the grid size should be increased!");
			return;
		}

		if (pic.getPixelFormat() != fmt) {
			System.err.println("Error: different pixel format!");
			return;
		}

		clearTile(nTile);

		int atlasnum = tile.atlasnum;
		int u1 = (int) (tile.u1 * atlasWidth / gridWidth);
		int v1 = (int) (tile.v1 * atlasHeight / gridHeight);
		int offs = (u1 * gridWidth + v1 * atlasWidth * gridHeight) * fmt.getLength();

		ByteBuffer buf = atlas.get(atlasnum).getPixels();
		ByteBuffer symbolBuffer = pic.getPixels();

		int dptr = offs;
		for (int i = 0, j, k; i < pic.getHeight(); i++) {
			dptr = (i * fmt.getLength() * atlasWidth) + offs;
			for (j = 0; j < pic.getWidth(); j++) {
				for (k = 0; k < fmt.getLength(); k++)
					buf.put(dptr++, symbolBuffer.get());
			}
		}

		tile.u2 = ((u1 + 1) * (float) pic.getWidth()) / atlasWidth;
		tile.v2 = ((v1 + 1) * (float) pic.getHeight()) / atlasHeight;
	}

	public void clearTile(int nTile) {
		TileUnit tile = getTile(nTile);
		if (tile == null)
			return;

		int atlasnum = tile.atlasnum;
		int u1 = (int) (tile.u1 * atlasWidth / gridWidth);
		int v1 = (int) (tile.v1 * atlasHeight / gridHeight);
		int offs = (u1 * gridWidth + v1 * atlasWidth * gridHeight) * fmt.getLength();

		ByteBuffer buf = atlas.get(atlasnum).getPixels();
		int dptr = offs;
		for (int i = 0, j, k; i < gridHeight; i++) {
			dptr = (i * fmt.getLength() * atlasWidth) + offs;
			for (j = 0; j < gridWidth; j++) {
				for (k = 0; k < fmt.getLength(); k++)
					buf.put(dptr++, fmt == PixelFormat.Pal8 ? (byte) -1 : 0);
			}
		}
	}

	public void addTile(int nTile, TileData pic) {
		if (pic.getWidth() > gridWidth || pic.getHeight() > gridHeight) {
			System.err.println("Error: the grid size should be increased!");
			return;
		}

		if (pic.getPixelFormat() != fmt) {
			System.err.println("Error: different pixel format!");
			return;
		}

		int atlasnum = pos / tilesPerAtlas;
		int symbolnum = pos % tilesPerAtlas;

		int u1 = (symbolnum % colsPerAtlas);
		int v1 = (symbolnum / colsPerAtlas);
		int offs = (u1 * gridWidth + v1 * atlasWidth * gridHeight) * fmt.getLength();

		if ((atlas.size() - 1) < atlasnum) {
			TileData data = new DummyTileData(fmt, atlasWidth, atlasHeight);
			ByteBuffer buf = data.getPixels();
			for (int j = 0; j < buf.capacity(); j++)
				buf.put(fmt == PixelFormat.Pal8 ? (byte) -1 : 0);
			atlas.add(data);
		}

		ByteBuffer buf = atlas.get(atlasnum).getPixels();
		ByteBuffer symbolBuffer = pic.getPixels();

		int dptr = offs;
		for (int i = 0, j, k; i < pic.getHeight(); i++) {
			dptr = (i * fmt.getLength() * atlasWidth) + offs;
			for (j = 0; j < pic.getWidth(); j++) {
				for (k = 0; k < fmt.getLength(); k++)
					buf.put(dptr++, symbolBuffer.get());
			}
		}

		TileUnit symb = new TileUnit(nTile);
		symb.atlasnum = atlasnum;
		symb.u1 = (u1 * (float) gridWidth) / atlasWidth;
		symb.v1 = (v1 * (float) gridHeight) / atlasHeight;
		symb.u2 = ((u1 + 1) * (float) pic.getWidth()) / atlasWidth;
		symb.v2 = ((v1 + 1) * (float) pic.getHeight()) / atlasHeight;

		units.put(nTile, symb);
		pos++;
	}

	public TileUnit getTile(int unit) {
		return units.get(unit);
	}

	public void saveToFile(String path) {
		for (int i = 0; i < atlas.size(); i++) {
			TileData dat = atlas.get(i);
			dat.save(path + i);
		}
	}

	private static int[] getAtlasSize(int numtiles) {
		int x = (int) Math.floor(Math.sqrt(numtiles)), y;
		if (x * x == numtiles)
			y = x;
		else {
			y = x + 1;
			if (x * y < numtiles)
				x++;
		}

		return new int[] { x, y };
	}

	public static class TileUnit {
		public float u1, v1;
		public float u2, v2;
		public int nTile;
		public int atlasnum;

		public TileUnit(int nTile) {
			this.nTile = nTile;
		}
	}
}
