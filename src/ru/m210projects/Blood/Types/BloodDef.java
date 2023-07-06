// This file is part of BloodGDX.
// Copyright (C) 2017-2020 Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Types;

import static ru.m210projects.Blood.Globals.kHitCeiling;
import static ru.m210projects.Blood.Globals.kHitFloor;
import static ru.m210projects.Blood.Globals.kHitIndexMask;
import static ru.m210projects.Blood.Globals.kHitSprite;
import static ru.m210projects.Blood.Globals.kHitTypeMask;
import static ru.m210projects.Blood.Globals.kHitWall;
import static ru.m210projects.Blood.Globals.kMaxTiles;
import static ru.m210projects.Blood.Tile.*;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.UserGroup;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Script.Scriptfile;
import ru.m210projects.Build.Types.Tile;

public class BloodDef extends DefScript {

    public byte[] surfType = new byte[kMaxTiles];

	private final DefineIdToken rffid = new DefineIdToken();
	private final NBloodDefineIdToken nbrffid = new NBloodDefineIdToken();
	private final TileFromTextureToken tft = new BloodTileFromTextureToken();
	private UserGroup usergroup;

	private final Map<String, Integer> fileids = new HashMap<String, Integer>();

	public BloodDef(BuildEngine engine, boolean disposable) {
		super(engine, disposable);

		this.addToken("definefileid", rffid);
		this.addToken("rffdefineid", nbrffid);
		this.addToken("tilefromtexture", tft);

		surfaceInit("SURFACE.DAT");
	}

	public BloodDef(BloodDef src, FileEntry addon) {
		super(src, addon);

		this.addToken("definefileid", rffid);
		this.addToken("rffdefineid", nbrffid);
		this.addToken("tilefromtexture", tft);

		System.arraycopy(src.surfType, 0, surfType, 0, src.surfType.length);
	}

    public void surfaceInit(String name) {
		Resource data = BuildGdx.cache.open(name, 0);
		if (data == null)
			return;

		int pos = 0;
		while (data.hasRemaining()) {
			data.read(surfType, pos, Math.min(surfType.length, data.remaining()));
			pos += surfType.length;
		}
		data.close();
	}

	public class DefineIdToken implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {
			String name = null;
			Integer fileId;

			if ((name = getFile(script)) == null)
				return BaseToken.Error;

			if (!BuildGdx.cache.contains(name, -1)) {
				byte[] data = BuildGdx.cache.getBytes(name, 0);
				if (data == null) { // search as external file
					Console.Println("DefineId error: file " + name + " not found!", Console.OSDTEXT_RED);
					return BaseToken.Error;
				} else {
					if (usergroup == null)
						usergroup = BuildGdx.cache.add("UserDef", disposable);
					usergroup.add(name, data, -1);
				}
			}

			if ((fileId = script.getsymbol()) == null)
				return BaseToken.Error;

			fileids.put(name, fileId);

			return BaseToken.Ok;
		}
	}

	public class NBloodDefineIdToken implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {
			String name = null;
			String ext = null;
			Integer fileId;

			if ((name = getFile(script)) == null)
				return BaseToken.Error;

			if ((ext = script.getstring()) == null)
				return BaseToken.Error;

			String filename = name + "." + ext;
			if (!BuildGdx.cache.contains(filename, -1)) {
				byte[] data = BuildGdx.cache.getBytes(filename, 0);
				if (data == null) { // search as external file
					Console.Println("DefineId error: file " + filename + " not found!", Console.OSDTEXT_RED);
					return BaseToken.Error;
				} else {
					if (usergroup == null)
						usergroup = BuildGdx.cache.add("UserDef", disposable);
					usergroup.add(filename, data, -1);
				}
			}

			if ((fileId = script.getsymbol()) == null)
				return BaseToken.Error;

			script.getstring();

			fileids.put(filename, fileId);

			return BaseToken.Ok;
		}
	}

	public enum TileTextureTokens {
		FILE, ALPHACUT, XOFFSET, YOFFSET, TEXTURE, CRC, EXTRA, SURF
	}

	private class BloodTileFromTextureToken extends TileFromTextureToken {
		protected final Map<String, TileTextureTokens> tilefromtexturetokens = new HashMap<String, TileTextureTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("file", TileTextureTokens.FILE);
				put("name", TileTextureTokens.FILE);
				put("alphacut", TileTextureTokens.ALPHACUT);
				put("xoffset", TileTextureTokens.XOFFSET);
				put("xoff", TileTextureTokens.XOFFSET);
				put("yoffset", TileTextureTokens.YOFFSET);
				put("yoff", TileTextureTokens.YOFFSET);
				put("texture", TileTextureTokens.TEXTURE);
				put("ifcrc", TileTextureTokens.CRC);
				put("extra", TileTextureTokens.EXTRA);
				put("surface", TileTextureTokens.SURF);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			int ttexturetokptr = script.ltextptr, ttextureend;
			String fn = null;
			Integer tile = -1, value;
			int talphacut = 255;
			Byte xoffset = null, yoffset = null;
			long tilecrc = 0;
			boolean istexture = false;
			Integer extra = null, surf = null;

			if ((tile = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((ttextureend = script.getbraces()) == -1)
				return BaseToken.Error;

			while (script.textptr < ttextureend) {
				Object tk = gettoken(script, tilefromtexturetokens);
				if (tk instanceof BaseToken) {
					int line = script.getlinum(script.ltextptr);
					Console.Println(
							script.filename + " has unknown token \""
									+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
									+ "\" on line: "
									+ toLowerCase(
											script.textbuf.substring(getPtr(script, line), getPtr(script, line + 1))),
							OSDTEXT_RED);
					continue;
				}

				switch ((TileTextureTokens) tk) {
				case EXTRA:
					extra = script.getsymbol();
					break;
				case FILE:
					fn = getFile(script);
					break;
				case ALPHACUT:
					value = script.getsymbol();
					if (value != null)
						talphacut = value;
					talphacut = BClipRange(talphacut, 0, 255);
					break;
				case XOFFSET:
					String xoffs = script.getstring();
					if (xoffs.equalsIgnoreCase("ART"))
						xoffset = engine.getTile(tile).getOffsetX();
					else {
						try {
							xoffset = Byte.parseByte(xoffs);
						} catch (Exception e) {
							Console.Println("Xoffset value out of range. Value: \"" + xoffs + "\" was disabled.",
									OSDTEXT_RED);
							break;
						}
					}
					break;
				case YOFFSET:
					String yoffs = script.getstring();
					if (yoffs.equalsIgnoreCase("ART"))
						yoffset = engine.getTile(tile).getOffsetY();
					else {
						try {
							yoffset = Byte.parseByte(yoffs);
						} catch (Exception e) {
							Console.Println("Yoffset value out of range. Value: \"" + yoffs + "\" was disabled.",
									OSDTEXT_RED);
							break;
						}
					}
					break;
				case TEXTURE:
					istexture = true;
					break;
				case SURF:
					value = script.getsymbol();
					if (value != null)
						surf = BClipRange(value, 0, 255);
					break;
				case CRC:
					tilecrc = script.getsymbol() & 0xFFFFFFFFL;
					break;
				}
			}

			DefTile dt;
			if ((dt = addTile(script, fn, tile, xoffset, yoffset, tilecrc, talphacut, istexture,
					ttexturetokptr)) != null) {
				if (extra != null)
					dt.optional = extra;
				if (surf != null) {
					byte s = (byte) ((int) surf);
					if (s >= kSurfMax) {
						Console.Println("Warning! surfaceType out of range: " + s + " resetting...",
								Console.OSDTEXT_RED);
						s = 0;
					}
					surfType[tile] = s;
				}
				return BaseToken.Ok;
			}
			return BaseToken.Error;
		}
	}

	@Override
	public void apply() {
		List<String> defs;
		if (addonsIncludes != null && currentAddon != null
				&& (defs = addonsIncludes.get(currentAddon.getName())) != null) {

			for (int i = 0; i < defs.size() / 2; i++) {
				String fn = defs.get(2 * i + 1);
				Resource res = BuildGdx.cache.open(fn, 0);
				if (res == null) {
					Console.Println("Warning: Failed including " + fn + " as module", OSDTEXT_RED);
					continue;
				}

				Scriptfile included = new Scriptfile(fn, res.getBytes());
				included.path = defs.get(2 * i);

				defsparser(included);
				res.close();
			}
		}

		if (fileids.size() > 0) {
			if (disposable) {
				for (GroupResource res : BuildGdx.cache.getDynamicResources()) {
					Integer fileid = null;
					if ((fileid = fileids.get(res.getFullName())) != null)
						res.setIdentification(fileid);
				}
			} else {
				for (String file : fileids.keySet()) {
					GroupResource res = (GroupResource) BuildGdx.cache.open(file, -1); // search in groups only
					if (res != null)
						res.setIdentification(fileids.get(file));
				}
			}
		}

		for (int i = 0; i < MAXTILES; i++) {
			if (tiles[i] == null)
				continue;

			DefTile tile = tiles[i];
			Tile pic = engine.getTile(i);

			if (tile.crc32 != 0) {
				byte[] data = pic.data;
				if (data == null)
					data = engine.loadtile(i);

				long crc32 = data != null ? CRC32.getChecksum(data) : -1;
				if (crc32 != tile.crc32) {
					boolean found = false;
					while (tile.next != null) {
						tile = tile.next;
						if (tile.crc32 == 0 || crc32 == tile.crc32) {
							found = true;
							break;
						}
					}

					if (!found)
						continue;
				}
			}

			if (tile.waloff == null)
				continue;

			engine.getrender().invalidatetile(i, -1, -1);

			pic.data = new byte[tile.waloff.length];
			System.arraycopy(tile.waloff, 0, pic.data, 0, tile.waloff.length);

			pic.setWidth(tile.sizx);
			pic.setHeight(tile.sizy);

			pic.anm &= ~0x70FFFF00;
			pic.anm |= (tile.xoffset & 0xFF) << 8;
			pic.anm |= (tile.yoffset & 0xFF) << 16;
			pic.anm |= (tile.optional & 0x07) << 28;

			engine.setpicsiz(i);

			// replace hrp info
			texInfo.addTexture(i, 0, tile.hrp, (0xFF - (tile.alphacut & 0xFF)) * (1.0f / 255.0f), 1.0f, 1.0f, 1.0f,
					1.0f, 0);
		}
	}

	public byte GetSurfType(int nHit) {
		int nHitType = nHit & kHitTypeMask;
		int nHitIndex = nHit & kHitIndexMask;

		switch (nHitType) {
		case kHitFloor:
			return surfType[sector[nHitIndex].floorpicnum];

		case kHitCeiling:
			return surfType[sector[nHitIndex].ceilingpicnum];

		case kHitWall:
			return surfType[wall[nHitIndex].picnum];

		case kHitSprite:
			return surfType[sprite[nHitIndex].picnum];
		}
		return 0;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (disposable)
			usergroup = null;
	}
}
