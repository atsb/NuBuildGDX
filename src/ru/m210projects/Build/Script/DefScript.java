/*
 * Definitions file parser for Build
 * by Jonathon Fowler (jf@jonof.id.au)
 * Remixed substantially by Ken Silverman
 * See the included license file "BUILDLIC.TXT" for license info.
 *
 * This file has been modified
 * by the EDuke32 team (development@voidpoint.com)
 * by Alexander Makarov-[M210] (m210-2007@mail.ru)
 */

package ru.m210projects.Build.Script;

import static ru.m210projects.Build.Engine.DETAILPAL;
import static ru.m210projects.Build.Engine.GLOWPAL;
import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.NORMALPAL;
import static ru.m210projects.Build.Engine.RESERVEDPALS;
import static ru.m210projects.Build.Engine.SPECULARPAL;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Render.ModelHandle.MDInfo;
import ru.m210projects.Build.Render.ModelHandle.VoxelInfo;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo;
import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD2.MD2Info;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MD3.MD3Info;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelData;
import ru.m210projects.Build.Types.Tile;

public class DefScript {

	protected boolean disposable;
	public final TextureHDInfo texInfo;
	public final ModelsInfo mdInfo;
	public final AudioInfo audInfo;
	public final MapHackInfo mapInfo;
	protected final Engine engine;

	protected FileEntry currentAddon;
	protected HashMap<String, List<String>> addonsIncludes;

	protected static class DefTile {
		public long crc32;
		public byte[] waloff;
		public short sizx, sizy;
		public byte xoffset, yoffset;
		public String hrp;
		public byte alphacut;
		public final boolean internal;
		public int optional;

		public DefTile next;

		public DefTile(DefTile src) {
			this.crc32 = src.crc32;
			this.waloff = src.waloff;
			this.sizx = src.sizx;
			this.sizy = src.sizy;
			this.xoffset = src.xoffset;
			this.yoffset = src.yoffset;
			this.hrp = src.hrp;
			this.alphacut = src.alphacut;
			this.internal = src.internal;
			this.optional = src.optional;

			if (src.next != null)
				this.next = new DefTile(src.next);
		}

		public DefTile(int sizx, int sizy, long crc32, boolean internal) {
			this.sizx = (short) sizx;
			this.sizy = (short) sizy;
			this.crc32 = crc32;
			this.internal = internal;
		}

		public DefTile getLast() {
			DefTile out = this;
			while (true) {
				DefTile n = out.next;
				if (n == null)
					return out;
				out = n;
			}
		}
	}

	protected DefTile[] tiles = new DefTile[MAXTILES];

	public DefScript(DefScript src, FileEntry addon) {
		this.disposable = true;
		this.texInfo = new TextureHDInfo(src.texInfo);
		this.mdInfo = new ModelsInfo(src.mdInfo, src.disposable);
		this.audInfo = new AudioInfo(src.audInfo);
		this.mapInfo = createMapHackInfo(src.mapInfo);
		this.engine = src.engine;
		for (int i = 0; i < MAXTILES; i++) {
			if (src.tiles[i] == null)
				continue;

			this.tiles[i] = new DefTile(src.tiles[i]);
		}

		if (src.addonsIncludes != null) {
			addonsIncludes = new HashMap<String, List<String>>();

			for (String key : src.addonsIncludes.keySet()) {
				List<String> list = src.addonsIncludes.get(key);
				List<String> clone = new ArrayList<String>(list.size());
				clone.addAll(list);

				addonsIncludes.put(key, clone);
			}
		}

		this.currentAddon = addon;
	}

	public DefScript(BuildEngine engine, boolean disposable) {
		this.disposable = disposable;
		texInfo = new TextureHDInfo();
		mdInfo = new ModelsInfo();
		audInfo = new AudioInfo();
		mapInfo = createMapHackInfo(null);

		this.engine = engine;
	}

	protected MapHackInfo createMapHackInfo(MapHackInfo src) {
		if (src != null)
			return new MapHackInfo(src);
		return new MapHackInfo();
	}

	public boolean loadScript(FileEntry file) {
		if (file == null) {
			Console.Println("Def error: script not found", OSDTEXT_RED);
			return false;
		}

		Resource res = BuildGdx.compat.open(file);
		byte[] data = res.getBytes();
		res.close();

		if (data == null) {
			Console.Println("File is exists, but data == null! Path:" + file.getPath());
			return false;
		}

		Scriptfile script = new Scriptfile(file.getPath(), data);
		script.path = file.getParent().getRelativePath();

		try {
			defsparser(script);
		} catch (Exception e) {
			e.printStackTrace();
			Console.Println("Def error: the script " + file.getPath() + " has errors", OSDTEXT_RED);
			return false;
		}

		return true;
	}

	public boolean loadScript(String name, byte[] buf) {
		if (buf == null) {
			Console.Println("Def error: script not found", OSDTEXT_RED);
			return false;
		}

		try {
			defsparser(new Scriptfile(name, buf));
		} catch (Exception e) {
			e.printStackTrace();
			Console.Println("Def error: the script " + name + " has errors", OSDTEXT_RED);
			return false;
		}

		return true;
	}

	protected void addToken(String name, Token token) {
		basetokens.put(name, token);
	}

	protected boolean addDefTile(DefTile texstatus, int tile) {
		DefTile def = tiles[tile];
		if (def != null && def.crc32 != 0) {
			if (texstatus.crc32 == 0) {
				texstatus.next = def;
				tiles[tile] = texstatus;
			} else if (def.crc32 != texstatus.crc32) {
				def = tiles[tile].getLast();
				def.next = texstatus;
			} else if (def.internal || disposable)
				tiles[tile] = texstatus;
		} else if (def == null || def.internal || disposable) {
			tiles[tile] = texstatus;
		} else
			return false;

		return true;
	}

	protected void defsparser(Scriptfile script) {
		Console.Println("Loading " + script.filename + "...");
		while (true) {
			Token basetoken = (Token) gettoken(script, basetokens);
			if (basetoken != null) {
				if (basetoken == BaseToken.EOF)
					return;

				synchronized (Engine.lock) {
					basetoken.parse(script);
				}
			}
		}
	}

	protected Object gettoken(Scriptfile sf, Map<String, ?> list) {
		int tok;
		if (sf == null)
			return BaseToken.Error;
		if ((tok = sf.gettoken()) == -2)
			return BaseToken.EOF;

		Object out = list.get(toLowerCase(sf.textbuf.substring(tok, sf.textptr)));
		if (out != null)
			return out;

		sf.errorptr = sf.textptr;
		return BaseToken.Error;
	}

	protected String getFile(Scriptfile script) {
		String fn = script.getstring();
		if (fn == null)
			return null;

		fn = FileUtils.getCorrectPath(fn);
		if (script.path != null)
			fn = script.path + File.separator + fn;

		return fn;
	}

	protected boolean check_tile(String defcmd, int tile, Scriptfile script, int cmdtokptr) {
		if (tile >= MAXTILES) {
			Console.Println("Error: " + defcmd + ": Invalid tile number on line " + script.filename + ":"
					+ script.getlinum(cmdtokptr), OSDTEXT_RED);
			return true;
		}

		return false;
	}

	protected boolean check_tile_range(String defcmd, int tilebeg, int tileend, Scriptfile script, int cmdtokptr) {
		if (tileend < tilebeg) {
			Console.Println("Warning: " + defcmd + ": backwards tile range on line " + script.filename + ":"
					+ script.getlinum(cmdtokptr), OSDTEXT_YELLOW);
			int tmp = tilebeg;
			tilebeg = tileend;
			tileend = tmp;
		}

		if (tilebeg >= MAXTILES || tileend >= MAXTILES) {
			Console.Println("Error: " + defcmd + ": Invalid tile range on line " + script.filename + ":"
					+ script.getlinum(cmdtokptr), OSDTEXT_RED);
			return true;
		}

		return false;
	}

	protected int getPtr(Scriptfile script, int line) {
		if (line <= 2)
			return script.lineoffs[0];

		if (line <= script.linenum)
			return script.lineoffs[line - 2];
		return script.eof;
	}

	public void dispose() {
		if (!disposable)
			return;

		engine.loadpics();
		for (int i = 0; i < MAXTILES; i++) {
			if (tiles[i] == null)
				continue;

			texInfo.remove(i, 0);

			engine.getTile(i).data = null;
			tiles[i] = null;
		}

		mdInfo.dispose();
	}

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

			pic.anm &= ~0x00FFFF00;
			pic.anm |= (tile.xoffset & 0xFF) << 8;
			pic.anm |= (tile.yoffset & 0xFF) << 16;

			engine.setpicsiz(i);

			// replace hrp info
			texInfo.addTexture(i, 0, tile.hrp, (0xFF - (tile.alphacut & 0xFF)) * (1.0f / 255.0f), 1.0f, 1.0f, 1.0f,
					1.0f, 0);
		}
	}

	// Tokens logic

	public interface Token {
		BaseToken parse(Scriptfile script);
	}

	public enum BaseToken implements Token {
		Ok, Warning, Error, EOF;

		@Override
		public BaseToken parse(Scriptfile script) {
			return this;
		}
	}

	private final Map<String, Token> basetokens = new HashMap<String, Token>() {
		private static final long serialVersionUID = 1L;
		{
			Token tok = new IncludeToken();
			put("#include", tok);
			put("include", tok);

			// deprecated style
			put("defineskybox", new DefineSkyboxToken());
			put("definetint", new DefineTint());

			// new style
			put("model", new ModelToken());
			put("voxel", new VoxelToken());
			put("skybox", new SkyboxToken());
			put("tint", new TintToken());

			tok = new TextureToken();
			put("tile", tok);
			put("texture", tok);

			// other stuff
			put("tilefromtexture", new TileFromTextureToken());
			put("animtilerange", new AnimRangeToken());
			put("mapinfo", new MaphackToken());

			// gdx
			tok = new AudioToken();
			put("sound", tok);
			put("music", tok);
			put("includeif", new AddonToken());
			put("echo", new EchoToken());
		}
	};

	public enum MapHackTokens {
		FILE, MHK, MD4, TITLE
	}

	protected class MaphackToken implements Token {
		private final Map<String, MapHackTokens> maptokens = new HashMap<String, MapHackTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("mapfile", MapHackTokens.FILE);
				put("mhkfile", MapHackTokens.MHK);
				put("mapmd4", MapHackTokens.MD4);
				put("maptitle", MapHackTokens.TITLE);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			int end;
			if ((end = script.getbraces()) == -1)
				return BaseToken.Error;

			Object tk;
			String file = null, mhk = null, md4 = null;
			while (script.textptr < end) {
				tk = gettoken(script, maptokens);
				if (checkErrorToken(script, tk))
					continue;

				switch ((MapHackTokens) tk) {
				default:
					break;
				case TITLE:
					script.getstring(); // XXX
					break;
				case FILE:
					file = getFile(script);
					break;
				case MHK:
					mhk = getFile(script);
					break;
				case MD4:
					md4 = script.getstring();
					break;
				}
			}

			if (mapInfo.addMapInfo(file, mhk, md4))
				return BaseToken.Ok;

			return BaseToken.Error;
		}
	}

	protected class IncludeToken implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {
			String fn;

			if ((fn = DefScript.this.getFile(script)) == null)
				return BaseToken.Warning;

			include(DefScript.this, fn, script, script.ltextptr);
			return BaseToken.Ok;
		}

		private void include(DefScript def, String fn, Scriptfile script, int cmdtokptr) {
			byte[] data = BuildGdx.cache.getBytes(fn, 0);
			if (data == null) {
				if (cmdtokptr == 0)
					Console.Println("Warning: Failed including " + fn + " as module", OSDTEXT_YELLOW);
				else
					Console.Println("Warning: Failed including " + fn + " on line " + script.filename + ":"
							+ script.getlinum(cmdtokptr), OSDTEXT_YELLOW);
				return;
			}

			Scriptfile included = new Scriptfile(fn, data);
			included.path = script.path;
			def.defsparser(included);
		}
	}

	protected class AddonToken implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {
			DefScript def = DefScript.this;
			String fn;

			if (def.addonsIncludes == null)
				def.addonsIncludes = new HashMap<String, List<String>>();
			String addon = script.getstring();
			if (addon != null && (fn = def.getFile(script)) != null) {
				if (def.addonsIncludes.get(addon) == null) {
					List<String> list = new ArrayList<String>();
					def.addonsIncludes.put(addon, list);
				}

				def.addonsIncludes.get(addon).add(script.path);
				def.addonsIncludes.get(addon).add(fn);
			}

			return BaseToken.Ok;
		}
	}

	protected class EchoToken implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {
			String message = script.getstring();
			if(message != null)
				Console.Println(message, Console.OSDTEXT_GOLD);
			else Console.Println("");
			return BaseToken.Ok;
		}
	}

	protected class AnimRangeToken implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {
			Integer tile0, tile1, speed, anm;
			if ((tile0 = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((tile1 = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((speed = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((anm = script.getsymbol()) == null)
				return BaseToken.Error;

			int length = (tile1 - tile0);
			if (length <= 0) {
				Console.Println("Warning: Animation lenght < 0, skipping", Console.OSDTEXT_RED);
				return BaseToken.Warning;
			}

			Tile pic = engine.getTile(tile0);

			pic.anm &= ~0x0F0000FF;
			pic.anm |= ((anm & 3) << 6) | ((speed & 15) << 24) | length & 0x3F;

			return BaseToken.Ok;
		}
	}

	public enum TileTextureTokens {
		FILE, ALPHACUT, XOFFSET, YOFFSET, TEXTURE, CRC
	}

	protected class TileFromTextureToken implements Token {
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
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			DefScript def = DefScript.this;

			int ttexturetokptr = script.ltextptr, ttextureend;
			String fn = null;
			Integer tile, value;
			int talphacut = 255;
			Byte xoffset = null, yoffset = null;
			long tilecrc = 0;
			boolean istexture = false;

			if ((tile = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((ttextureend = script.getbraces()) == -1)
				return BaseToken.Error;

			while (script.textptr < ttextureend) {
				Object tk = def.gettoken(script, tilefromtexturetokens);
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
				default:
					break;
				case FILE:
					fn = def.getFile(script);
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
				case CRC:
					tilecrc = script.getsymbol() & 0xFFFFFFFFL;
					break;
				}
			}
			script.skipbrace(ttextureend); // close bracke

			if (addTile(script, fn, tile, xoffset, yoffset, tilecrc, talphacut, istexture, ttexturetokptr) != null)
				return BaseToken.Ok;
			return BaseToken.Error;
		}

		protected DefTile addTile(Scriptfile script, String fn, Integer tile, Byte xoffset, Byte yoffset, long tilecrc,
				int talphacut, boolean istexture, int ttexturetokptr) {
			DefScript def = DefScript.this;

			if (tile < 0 || tile >= MAXTILES) {
				Console.Println("Error: missing or invalid 'tile number' for texture definition near line "
						+ script.filename + ":" + script.getlinum(ttexturetokptr), OSDTEXT_RED);
				return null;
			}

			if (fn == null) {
				// tilefromtexture <tile> { texhitscan } sets the bit but doesn't change tile
				// data

				String ext = FileUtils.getExtension(script.filename);
				DefTile deftile = new DefTile(engine.getTile(tile).getWidth(), engine.getTile(tile).getHeight(),
						tilecrc, ext != null && ext.equals("dat"));
				if (xoffset != null)
					deftile.xoffset = xoffset;
				if (yoffset != null)
					deftile.yoffset = yoffset;

				if (xoffset == null && yoffset == null)
					Console.Println("Error: missing 'file name' for tilefromtexture definition near line "
							+ script.filename + ":" + script.getlinum(ttexturetokptr), OSDTEXT_RED);

				if (def.addDefTile(deftile, tile))
					return deftile;

				return null;
			}

			String ext = FileUtils.getExtension(script.filename);
			DefTile texstatus = ImportTileFromTexture(fn, tile, tilecrc, talphacut, istexture,
					ext != null && ext.equals("dat"));
			if (texstatus == null)
				return null;

			if (xoffset != null)
				texstatus.xoffset = xoffset;
			if (yoffset != null)
				texstatus.yoffset = yoffset;

			if (!def.addDefTile(texstatus, tile)) {
				Console.Println("Error: \"" + fn + "\" has more than one tile, in tilefromtexture definition near line "
						+ script.filename + ":" + script.getlinum(ttexturetokptr), OSDTEXT_RED);
				return null;
			}

			return texstatus;
		}

		protected DefTile ImportTileFromTexture(String fn, int tile, long crc32, int alphacut, boolean istexture,
				boolean internal) {
			DefScript def = DefScript.this;

			byte[] data = BuildGdx.cache.getBytes(fn, 0);
			if (data == null) {
				Console.Println("ImportTileFromTexture error: file " + fn + " not found!", Console.OSDTEXT_RED);
				return null;
			}

			Pixmap pix;
			try {
				pix = new Pixmap(data, 0, data.length);
			} catch (Throwable e) { // if native code didn't load
				Console.Println("ImportTileFromTexture error: " + e.getMessage(), Console.OSDTEXT_RED);
				return null;
			}

			pix.setFilter(Filter.NearestNeighbour);

			Format fmt = pix.getFormat();

			int xsiz = pix.getWidth();
			int ysiz = pix.getHeight();

			DefTile deftile = new DefTile(xsiz, ysiz, crc32, internal);
			deftile.waloff = new byte[xsiz * ysiz];

			ByteBuffer bb = pix.getPixels();
			byte[] waloff = deftile.waloff;

			for (int y = 0; y < ysiz; y++)
				for (int x = 0; x < xsiz; x++) {
					int r = (bb.get() & 0xFF) >> 2;
					int g = (bb.get() & 0xFF) >> 2;
					int b = (bb.get() & 0xFF) >> 2;
					if (fmt == Format.RGBA4444 || fmt == Format.RGBA8888) {
						if (bb.get() == 0)
							waloff[x * ysiz + y] = -1;
						else
							waloff[x * ysiz + y] = def.engine.getclosestcol(palette, r, g, b);
					} else
						waloff[x * ysiz + y] = def.engine.getclosestcol(palette, r, g, b);
				}

			if (istexture) {
				deftile.hrp = fn;
				deftile.alphacut = (byte) alphacut;
			}

			return deftile;
		}
	}

	protected enum ModelTokens {
		SCALE, SHADE, XADD, YADD, ZADD, FRAME, FRAME0, FRAME1, ANIM, SKIN, HUD, TILE, TILE0, TILE1, FPS, FLAGS, PAL,
		FILE, SURF, ANGADD, FOV, HIDE, NOBOB, FLIPPED, NODEPTH, DETAIL, NORMAL, SPECULAR, GLOW, SPECPOWER, SPECFACTOR,
		PARAM, PARALLAXSCALE, PARALLAXBIAS, SMOOTHDURATION
	}

	protected class ModelToken implements Token {
		protected int modelskin = -1, lastmodelskin = -1, seenframe = 0;

		protected final Map<String, ModelTokens> modeltokens = new HashMap<String, ModelTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("scale", ModelTokens.SCALE);
				put("shade", ModelTokens.SHADE);
				put("zadd", ModelTokens.ZADD);
				put("frame", ModelTokens.FRAME);
				put("anim", ModelTokens.ANIM);
				put("skin", ModelTokens.SKIN);
				put("detail", ModelTokens.DETAIL);
				put("glow", ModelTokens.GLOW);
				put("specular", ModelTokens.SPECULAR);
				put("normal", ModelTokens.NORMAL);
				put("hud", ModelTokens.HUD);
				put("flags", ModelTokens.FLAGS);
			}
		};

		private final Map<String, ModelTokens> modelframetokens = new HashMap<String, ModelTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("frame", ModelTokens.FRAME);
				put("name", ModelTokens.FRAME);
				put("tile", ModelTokens.TILE);
				put("tile0", ModelTokens.TILE0);
				put("tile1", ModelTokens.TILE1);
				put("smoothduration", ModelTokens.SMOOTHDURATION);
				put("pal", ModelTokens.PAL);
			}
		};

		private final Map<String, ModelTokens> modelanimtokens = new HashMap<String, ModelTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("frame0", ModelTokens.FRAME0);
				put("frame1", ModelTokens.FRAME1);
				put("fps", ModelTokens.FPS);
				put("flags", ModelTokens.FLAGS);
			}
		};

		private final Map<String, ModelTokens> modelskintokens = new HashMap<String, ModelTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("pal", ModelTokens.PAL);
				put("file", ModelTokens.FILE);
				put("surf", ModelTokens.SURF);
				put("surface", ModelTokens.SURF);

				put("specpower", ModelTokens.SPECPOWER);
				put("specfactor", ModelTokens.SPECFACTOR);
				put("parallaxscale", ModelTokens.PARALLAXSCALE);
				put("parallaxbias", ModelTokens.PARALLAXBIAS);
			}
		};

		private final Map<String, ModelTokens> modelhudtokens = new HashMap<String, ModelTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("tile", ModelTokens.TILE);
				put("tile0", ModelTokens.TILE0);
				put("tile1", ModelTokens.TILE1);
				put("xadd", ModelTokens.XADD);
				put("yadd", ModelTokens.YADD);
				put("zadd", ModelTokens.ZADD);
				put("fov", ModelTokens.FOV);
				put("angadd", ModelTokens.ANGADD);
				put("hide", ModelTokens.HIDE);
				put("nobob", ModelTokens.NOBOB);
				put("flipped", ModelTokens.FLIPPED);
				put("nodepth", ModelTokens.NODEPTH);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			ModelTokens token;
			Object tk;
			Resource res;
			Integer ivalue;
			Double dvalue;

			int modelend;
			String modelfn;
			double mdscale = 1.0, mzadd = 0.0, myoffset = 0.0;
			int /* shadeoffs = 0, */ mdflags = 0, mdpal = 0;
			int model_ok = 1;

			modelskin = lastmodelskin = 0;
			seenframe = 0;

			if ((modelfn = script.getstring()) == null)
				return BaseToken.Error;
			if ((modelend = script.getbraces()) == -1)
				return BaseToken.Error;

			res = BuildGdx.cache.open(modelfn, 0);
			if (res == null) {
				Console.Println("Warning: File not found" + modelfn, OSDTEXT_YELLOW);
				script.textptr = modelend + 1;
				return BaseToken.Warning;
			}

			ModelInfo m = null;
			try {
				int sign = res.readInt();
				res.seek(0, Whence.Set);
				switch (sign) {
				case 0x32504449: // IDP2
					m = new MD2Info(res, modelfn);
					break;
				case 0x33504449: // IDP3
					m = new MD3Info(res, modelfn);
					break;
				default:
					if (res.getExtension().equals("kvx"))
						m = new ModelInfo(modelfn, Type.Voxel);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			res.close();

			if (m == null) {
				Console.Println("Warning: Failed loading model " + modelfn, OSDTEXT_YELLOW);
				script.textptr = modelend + 1;
				return BaseToken.Warning;
			}

			while (script.textptr < modelend) {
				tk = gettoken(script, modeltokens);
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

				token = (ModelTokens) tk;
				switch (token) {
				default:
					break;
				case SCALE:
					dvalue = script.getdouble();
					if (dvalue != null)
						mdscale = dvalue;
					break;
				case SHADE: // XXX
					if ((ivalue = script.getsymbol()) != null) {
//						shadeoffs = ivalue;
					}
					break;
				case ZADD:
					if ((dvalue = script.getdouble()) != null)
						mzadd = dvalue;
					break;
//				case YOFFSET:
//					if ((dvalue = script.getdouble()) != null)
//						myoffset = dvalue;
//					break;
				case FLAGS:
					if ((ivalue = script.getsymbol()) != null)
						mdflags = ivalue;
					break;
				case FRAME: {
					int frametokptr = script.ltextptr;
					int frameend, happy = 1;
					String framename = null;
					int ftilenume = -1, ltilenume = -1, tilex;
					double smoothduration = 0.1;

					if ((frameend = script.getbraces()) == -1)
						break;

					while (script.textptr < frameend) {
						tk = gettoken(script, modelframetokens);
						if (tk instanceof BaseToken) {
							int line = script.getlinum(script.ltextptr);
							Console.Println(
									script.filename + " has unknown token \""
											+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
											+ "\" on line: " + toLowerCase(script.textbuf
													.substring(getPtr(script, line), getPtr(script, line + 1))),
									OSDTEXT_RED);
							continue;
						}

						switch ((ModelTokens) tk) {
						default:
							break;
						case PAL:
							if ((ivalue = script.getsymbol()) != null)
								mdpal = ivalue;
							break;
						case FRAME:
							framename = script.getstring();
							break;
						case TILE:
							if ((ivalue = script.getsymbol()) != null) {
								ftilenume = ivalue;
								ltilenume = ftilenume;
							}
							break;
						case TILE0:
							if ((ivalue = script.getsymbol()) != null)
								ftilenume = ivalue;
							break; // first tile number
						case TILE1:
							if ((ivalue = script.getsymbol()) != null)
								ltilenume = ivalue;
							break; // last tile number (inclusive)
						case SMOOTHDURATION:
							if ((dvalue = script.getdouble()) != null)
								smoothduration = dvalue;
							break;
						}
					}
					script.skipbrace(frameend); // close bracke

					if (check_tile_range("model: frame", ftilenume, ltilenume, script, frametokptr)) {
						model_ok = 0;
						break;
					}

					for (tilex = ftilenume; tilex <= ltilenume && happy != 0; tilex++) {
						switch (mdInfo.addModelInfo(m, tilex, framename, Math.max(0, modelskin), (float) smoothduration,
								mdpal)) {
						case -1:
							happy = 0;
							break; // invalid model id!?
						case -2:
							Console.Println("Invalid tile number on line " + script.filename + ":"
									+ script.getlinum(frametokptr), OSDTEXT_RED);
							happy = 0;
							break;
						case -3:
							Console.Println("Invalid frame name on line " + script.filename + ":"
									+ script.getlinum(frametokptr), OSDTEXT_RED);
							happy = 0;
							break;
						default:
							break;
						}

						model_ok &= happy;
					}

					seenframe = 1;
				}
					break;
				case ANIM: {
					int animtokptr = script.ltextptr;
					int animend, happy = 1;
					String startframe = null, endframe = null;
					int flags = 0;
					double dfps = 1.0;

					if ((animend = script.getbraces()) == -1)
						break;
					while (script.textptr < animend) {
						tk = gettoken(script, modelanimtokens);
						if (tk instanceof BaseToken) {
							int line = script.getlinum(script.ltextptr);
							Console.Println(
									script.filename + " has unknown token \""
											+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
											+ "\" on line: " + toLowerCase(script.textbuf
													.substring(getPtr(script, line), getPtr(script, line + 1))),
									OSDTEXT_RED);
							continue;
						}

						switch ((ModelTokens) tk) {
						default:
							break;
						case FRAME0:
							startframe = script.getstring();
							break;
						case FRAME1:
							endframe = script.getstring();
							break;
						case FPS:
							if ((dvalue = script.getdouble()) != null)
								dfps = dvalue;
							break; // animation frame rate
						case FLAGS:
							if ((ivalue = script.getsymbol()) != null)
								flags = ivalue;
							break;
						}
					}
					script.skipbrace(animend); // close bracke

					if (startframe == null) {
						Console.Println("Error: missing 'start frame' for anim definition near line " + script.filename
								+ ":" + script.getlinum(animtokptr), OSDTEXT_RED);
						happy = 0;
					}

					if (endframe == null) {
						Console.Println("Error: missing 'end frame' for anim definition near line " + script.filename
								+ ":" + script.getlinum(animtokptr), OSDTEXT_RED);
						happy = 0;
					}
					model_ok &= happy;
					if (happy == 0 || m.getType() == Type.Voxel)
						break;

					switch (((MDInfo) m).setAnimation(startframe, endframe, (int) (dfps * (65536.0 * .001)), flags)) {
					case -2:
						Console.Println("Invalid starting frame name on line " + script.filename + ":"
								+ script.getlinum(animtokptr), OSDTEXT_RED);
						model_ok = 0;
						break;
					case -3:
						Console.Println("Invalid ending frame name on line " + script.filename + ":"
								+ script.getlinum(animtokptr), OSDTEXT_RED);
						model_ok = 0;
						break;
					}
				}
					break;
				case SKIN:
				case DETAIL:
				case GLOW:
				case SPECULAR:
				case NORMAL:
					int skintokptr = script.ltextptr;
					int skinend;
					String skinfn = null;
					int palnum = 0, surfnum = 0;
					double param = 1.0, specpower = 1.0, specfactor = 1.0;

					if ((skinend = script.getbraces()) == -1)
						break;

					while (script.textptr < skinend) {
						tk = gettoken(script, modelskintokens);
						if (tk instanceof BaseToken) {
							int line = script.getlinum(script.ltextptr);
							Console.Println(
									script.filename + " has unknown token \""
											+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
											+ "\" on line: " + toLowerCase(script.textbuf
													.substring(getPtr(script, line), getPtr(script, line + 1))),
									OSDTEXT_RED);
							continue;
						}

						switch ((ModelTokens) tk) {
						default:
							break;
						case PAL:
							palnum = script.getsymbol();
							break;
						case PARAM:
							if ((dvalue = script.getdouble()) != null)
								param = dvalue;
							break;
						case PARALLAXSCALE:
							script.getdouble(); // XXX
							break;

						case PARALLAXBIAS:
							script.getdouble(); // XXX
							break;
						case SPECPOWER:
							if ((dvalue = script.getdouble()) != null)
								specpower = dvalue;
							break;
						case SPECFACTOR:
							if ((dvalue = script.getdouble()) != null)
								specfactor = dvalue;
							break;
						case FILE:
							skinfn = getFile(script);
							break; // skin filename
						case SURF:
							if ((ivalue = script.getsymbol()) != null)
								surfnum = ivalue;
							break; // getnumber
						}
					}
					script.skipbrace(skinend); // close bracke

					if (skinfn == null) {
						Console.Println("Error: missing 'skin filename' for skin definition near line "
								+ script.filename + ":" + script.getlinum(skintokptr), OSDTEXT_RED);
						model_ok = 0;
						break;
					}

					if (seenframe != 0) {
						modelskin = ++lastmodelskin;
					}
					seenframe = 0;

					switch (token) {
					default:
						break;
					case DETAIL:
						palnum = DETAILPAL;
						param = 1.0f / param;
						break;
					case GLOW:
						palnum = GLOWPAL;
						break;
					case SPECULAR:
						palnum = SPECULARPAL;
						break;
					case NORMAL:
						palnum = NORMALPAL;
						break;
					}

					if (!BuildGdx.cache.contains(skinfn, 0) || m.getType() == Type.Voxel)
						break;

					switch (((MDInfo) m).setSkin(skinfn, palnum, Math.max(0, modelskin), surfnum, param, specpower,
							specfactor)) {
					case -2:
						Console.Println(
								"Invalid skin filename on line " + script.filename + ":" + script.getlinum(skintokptr),
								OSDTEXT_RED);
						model_ok = 0;
						break;
					case -3:
						Console.Println(
								"Invalid palette number on line " + script.filename + ":" + script.getlinum(skintokptr),
								OSDTEXT_RED);
						model_ok = 0;
						break;
					}

					break;
				case HUD:
					int hudtokptr = script.ltextptr;
					int happy = 1, frameend;
					int ftilenume = -1, ltilenume = -1, tilex, flags = 0, fov = -1;
					double xadd = 0.0, yadd = 0.0, zadd = 0.0, angadd = 0.0;

					if ((frameend = script.getbraces()) == -1)
						break;

					while (script.textptr < frameend) {
						tk = gettoken(script, modelhudtokens);
						if (tk instanceof BaseToken) {
							int line = script.getlinum(script.ltextptr);
							Console.Println(
									script.filename + " has unknown token \""
											+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
											+ "\" on line: " + toLowerCase(script.textbuf
													.substring(getPtr(script, line), getPtr(script, line + 1))),
									OSDTEXT_RED);
							continue;
						}

						switch ((ModelTokens) tk) {
						default:
							break;
						case TILE:
							if ((ivalue = script.getsymbol()) != null)
								ftilenume = ivalue;
							ltilenume = ftilenume;
							break;
						case TILE0:
							if ((ivalue = script.getsymbol()) != null)
								ftilenume = ivalue;
							break; // first tile number
						case TILE1:
							if ((ivalue = script.getsymbol()) != null)
								ltilenume = ivalue;
							break; // last tile number (inclusive)
						case XADD:
							if ((dvalue = script.getdouble()) != null)
								xadd = dvalue;
							break;
						case YADD:
							if ((dvalue = script.getdouble()) != null)
								yadd = dvalue;
							break;
						case ZADD:
							if ((dvalue = script.getdouble()) != null)
								zadd = dvalue;
							break;
						case ANGADD:
							if ((dvalue = script.getdouble()) != null)
								angadd = dvalue;
							break;
						case FOV:
							if ((ivalue = script.getsymbol()) != null)
								fov = ivalue;
							break;
						case HIDE:
							flags |= 1;
							break;
						case NOBOB:
							flags |= 2;
							break;
						case FLIPPED:
							flags |= 4;
							break;
						case NODEPTH:
							flags |= 8;
							break;
						}
					}

					script.skipbrace(frameend); // close bracke

					if (check_tile_range("hud", ftilenume, ltilenume, script, hudtokptr)) {
						model_ok = 0;
						break;
					}

					for (tilex = ftilenume; tilex <= ltilenume && happy != 0; tilex++) {
						if (mdInfo.addHudInfo(tilex, xadd, yadd, zadd, (short) angadd, flags, fov) == -2) {
							Console.Println(
									"Invalid tile number on line " + script.filename + ":" + script.getlinum(hudtokptr),
									OSDTEXT_RED);
							happy = 0;
						}

						model_ok &= happy;
					}

					break;
				}
			}
			script.skipbrace(modelend); // close bracke

			if (model_ok == 0) {
				if (m != null) {
					Console.Println("Removing model " + modelfn + " due to errors.", OSDTEXT_YELLOW);
					mdInfo.removeModelInfo(m);
				}
				return BaseToken.Error;
			}

			m.setMisc((float) mdscale, (float) mzadd, (float) myoffset, mdflags);

			modelskin = lastmodelskin = 0;
			seenframe = 0;

			return BaseToken.Ok;
		}
	}

	protected enum TextureTokens {
		FILE, PAL, DETAIL, GLOW, SPECULAR, NORMAL, ALPHACUT, XSCALE, YSCALE, SPECPOWER, SPECFACTOR, NOCOMPRESS,
		NODOWNSIZE, PARALLAXBIAS, PARALLAXSCALE
	}

	protected class TextureToken implements Token {
		private final Map<String, TextureTokens> texturetokens = new HashMap<String, TextureTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("pal", TextureTokens.PAL);
				put("detail", TextureTokens.DETAIL);
				put("glow", TextureTokens.GLOW);
				put("specular", TextureTokens.SPECULAR);
				put("normal", TextureTokens.NORMAL);
				put("file", TextureTokens.FILE);
				put("name", TextureTokens.FILE);
				put("alphacut", TextureTokens.ALPHACUT);
				put("detailscale", TextureTokens.XSCALE);
				put("scale", TextureTokens.XSCALE);
				put("xscale", TextureTokens.XSCALE);
				put("intensity", TextureTokens.XSCALE);
				put("yscale", TextureTokens.YSCALE);
				put("specpower", TextureTokens.SPECPOWER);
				put("specularpower", TextureTokens.SPECPOWER);
				put("parallaxscale", TextureTokens.PARALLAXSCALE);
				put("specfactor", TextureTokens.SPECFACTOR);
				put("specularfactor", TextureTokens.SPECFACTOR);
				put("parallaxbias", TextureTokens.PARALLAXBIAS);
				put("nocompress", TextureTokens.NOCOMPRESS);
				put("nodownsize", TextureTokens.NODOWNSIZE);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			int textureend;
			Double dvalue;
			Integer ttile;
			Object tk;

			if ((ttile = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((textureend = script.getbraces()) == -1)
				return BaseToken.Error;

			while (script.textptr < textureend) {
				tk = gettoken(script, texturetokens);
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

				TextureTokens token = (TextureTokens) tk;
				switch (token) {
				default:
					break;
				case PAL:
				case DETAIL:
				case GLOW:
				case SPECULAR:
				case NORMAL:
					Integer tpal = -1;
					String tfn = null;
					double alphacut = -1.0, xscale = 1.0, yscale = 1.0, specpower = 1.0, specfactor = 1.0;
					int flags = 0;
					int palend;

					if (token == TextureTokens.PAL && (tpal = script.getsymbol()) == null)
						break;
					if ((palend = script.getbraces()) == -1)
						break;
					while (script.textptr < palend) {
						tk = gettoken(script, texturetokens);
						if (tk instanceof BaseToken) {
							int line = script.getlinum(script.ltextptr);
							Console.Println(
									script.filename + " has unknown token \""
											+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
											+ "\" on line: " + toLowerCase(script.textbuf
													.substring(getPtr(script, line), getPtr(script, line + 1))),
									OSDTEXT_RED);
							continue;
						}

						switch ((TextureTokens) tk) {
						default:
							break;
						case FILE:
							tfn = getFile(script);
							break;
						case ALPHACUT:
							if (token != TextureTokens.PAL)
								break;
							if ((dvalue = script.getdouble()) != null)
								alphacut = dvalue;
							break;
						case XSCALE:
							if ((dvalue = script.getdouble()) != null)
								xscale = dvalue;
							break;
						case YSCALE:
							if ((dvalue = script.getdouble()) != null)
								yscale = dvalue;
							break;
						case SPECPOWER:
							if ((dvalue = script.getdouble()) != null)
								specpower = dvalue;
							break;
						case SPECFACTOR:
							if ((dvalue = script.getdouble()) != null)
								specfactor = dvalue;
							break;

						case PARALLAXSCALE:
							script.getdouble(); // XXX
							break;

						case PARALLAXBIAS:
							script.getdouble(); // XXX
							break;

						case NOCOMPRESS:
							flags |= 1;
							break;
						case NODOWNSIZE:
							flags |= 16;
							break;
						}
					}
					script.skipbrace(palend); // close bracke

					switch (token) {
					default:
						break;
					case PAL:
						xscale = 1.0f / xscale;
						yscale = 1.0f / yscale;
						break;
					case DETAIL:
						tpal = DETAILPAL;
						xscale = 1.0f / xscale;
						yscale = 1.0f / yscale;
						break;
					case GLOW:
						tpal = GLOWPAL;
						break;
					case SPECULAR:
						tpal = SPECULARPAL;
						break;
					case NORMAL:
						tpal = NORMALPAL;
						break;
					}

					if (ttile >= MAXTILES)
						break; // message is printed later
					if (token == TextureTokens.PAL && tpal >= MAXPALOOKUPS - RESERVEDPALS) {
						Console.Println("Error: missing or invalid 'palette number' for texture definition near line "
								+ script.filename + ":" + script.getlinum(script.ltextptr), OSDTEXT_RED);
						return BaseToken.Error;
					}
					if (tfn == null) {
						Console.Println("Error: missing 'file name' for texture definition near line " + script.filename
								+ ":" + script.getlinum(script.ltextptr), OSDTEXT_RED);
						return BaseToken.Error;
					}

					if (!BuildGdx.cache.contains(tfn, 0)) {
						Console.Println("Error: file \"" + tfn + "\" not found for texture definition near line "
								+ script.filename + ":" + script.getlinum(script.ltextptr), OSDTEXT_RED);
						return BaseToken.Error;
					}
//                  Console.Println("Loading hires texture \"" + tfn + "\"");

					texInfo.addTexture(ttile.intValue(), tpal.intValue(), tfn, (float) alphacut, (float) xscale,
							(float) yscale, (float) specpower, (float) specfactor, flags);
					break;
				}
			}
			script.skipbrace(textureend); // close bracke

			if (ttile >= MAXTILES) {
				Console.Println("Error: missing or invalid 'tile number' for texture definition near line "
						+ script.filename + ":" + script.getlinum(script.ltextptr), OSDTEXT_RED);
				return BaseToken.Error;
			}

			return BaseToken.Ok;
		}
	}

	public enum VoxelTokens {
		TILE, TILE0, TILE1, SCALE, ROTATE
	}

	protected class VoxelToken implements Token {

		private final Map<String, VoxelTokens> voxeltokens = new HashMap<String, VoxelTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("tile", VoxelTokens.TILE);
				put("tile0", VoxelTokens.TILE0);
				put("tile1", VoxelTokens.TILE1);
				put("scale", VoxelTokens.SCALE);
				put("rotate", VoxelTokens.ROTATE);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			String fn;
			Integer ivalue;
			Double dvalue;

			int vmodelend;
			double vscale = 1.0;
			int tile0 = MAXTILES, tile1 = -1, tilex;
			boolean vrotate = false;

			if ((fn = getFile(script)) == null) // voxel filename
				return BaseToken.Error;

			if ((vmodelend = script.getbraces()) == -1)
				return BaseToken.Error;

			Resource res = BuildGdx.cache.open(fn, 0);
			if (res == null) {
				Console.Println("Warning: File not found" + fn, OSDTEXT_YELLOW);
				script.textptr = vmodelend + 1;
				return BaseToken.Warning;
			}

			VoxelInfo vox = null;
			try {
				vox = new VoxelInfo(new VoxelData(res));
			} catch (Exception e) {
				e.printStackTrace();
			}
			res.close();

			if (vox == null) {
				Console.Println("Warning: Failed loading voxel model " + fn, OSDTEXT_YELLOW);
				script.textptr = vmodelend + 1;

				return BaseToken.Warning;
			}

			while (script.textptr < vmodelend) {
				Object tk = gettoken(script, voxeltokens);
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

				switch ((VoxelTokens) tk) {
				case TILE:
					tilex = script.getsymbol();
					if (check_tile("voxel", tilex, script, script.ltextptr))
						break;

					mdInfo.addVoxelInfo(vox, tilex);
					break;
				case TILE0:
					if ((ivalue = script.getsymbol()) != null)
						tile0 = ivalue;
					break; // 1st tile #

				case TILE1:
					if ((ivalue = script.getsymbol()) != null)
						tile1 = ivalue;

					if (check_tile_range("voxel", tile0, tile1, script, script.ltextptr))
						break;
					for (tilex = tile0; tilex <= tile1; tilex++) {
						mdInfo.addVoxelInfo(vox, tilex);
					}
					break; // last tile number (inclusive)
				case SCALE:
					if ((dvalue = script.getdouble()) != null)
						vscale = dvalue;
					break;
				case ROTATE:
					vrotate = true;
					break;
				default:
					break;
				}
			}
			script.skipbrace(vmodelend); // close bracke

			vox.setMisc((float) vscale * 65536, 0, 0, vrotate ? ModelInfo.MD_ROTATE : 0);
			return BaseToken.Ok;
		}
	}

	public enum SkyboxTokens {
		TILE, PAL, FRONT, RIGHT, BACK, LEFT, TOP, BOTTOM, NOCOMPRESS, NODOWNSIZE
	}

	protected class SkyboxToken implements Token {

		private final String[] skyfaces = { "front face", "right face", "back face", "left face", "top face",
				"bottom face" };

		private final Map<String, SkyboxTokens> skyboxtokens = new HashMap<String, SkyboxTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("tile", SkyboxTokens.TILE);
				put("pal", SkyboxTokens.PAL);
				put("ft", SkyboxTokens.FRONT);
				put("front", SkyboxTokens.FRONT);
				put("forward", SkyboxTokens.FRONT);
				put("rt", SkyboxTokens.RIGHT);
				put("right", SkyboxTokens.RIGHT);
				put("bk", SkyboxTokens.BACK);
				put("back", SkyboxTokens.BACK);
				put("lf", SkyboxTokens.LEFT);
				put("left", SkyboxTokens.LEFT);
				put("lt", SkyboxTokens.LEFT);
				put("up", SkyboxTokens.TOP);
				put("top", SkyboxTokens.TOP);
				put("ceiling", SkyboxTokens.TOP);
				put("ceil", SkyboxTokens.TOP);
				put("dn", SkyboxTokens.BOTTOM);
				put("bottom", SkyboxTokens.BOTTOM);
				put("floor", SkyboxTokens.BOTTOM);
				put("down", SkyboxTokens.BOTTOM);

				put("nocompress", SkyboxTokens.NOCOMPRESS);
				put("nodownsize", SkyboxTokens.NODOWNSIZE);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			int sskyend, stile = -1, spal = 0;
			Integer ivalue;
			String[] sfn = new String[6];

			if ((sskyend = script.getbraces()) == -1)
				return BaseToken.Error;

			while (script.textptr < sskyend) {
				try {
					Object tk = gettoken(script, skyboxtokens);
					if (tk instanceof BaseToken) {
						int line = script.getlinum(script.ltextptr);
						Console.Println(script.filename + " has unknown token \""
								+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
								+ "\" on line: "
								+ toLowerCase(script.textbuf.substring(getPtr(script, line), getPtr(script, line + 1))),
								OSDTEXT_RED);
						continue;
					}

					switch ((SkyboxTokens) tk) {
					case TILE:
						if ((ivalue = script.getsymbol()) != null)
							stile = ivalue;
						break;
					case PAL:
						if ((ivalue = script.getsymbol()) != null)
							spal = ivalue;
						break;
					case FRONT:
						sfn[0] = getFile(script);
						break;
					case RIGHT:
						sfn[1] = getFile(script);
						break;
					case BACK:
						sfn[2] = getFile(script);
						break;
					case LEFT:
						sfn[3] = getFile(script);
						break;
					case TOP:
						sfn[4] = getFile(script);
						break;
					case BOTTOM:
						sfn[5] = getFile(script);
						break;

					case NOCOMPRESS: // XXX

						break;
					case NODOWNSIZE:

						break;

					default:
						break;
					}
				} catch (Exception e) {
				}
			}
			script.skipbrace(sskyend); // close bracke
			if (addSkybox(script, stile, spal, sfn))
				return BaseToken.Ok;
			return BaseToken.Error;
		}

		public boolean addSkybox(Scriptfile script, int stile, int spal, String[] sfn) {
			if (stile < 0) {
				Console.Println("Error: skybox: missing 'tile number' near line " + script.filename + ":"
						+ script.getlinum(script.ltextptr), OSDTEXT_RED);
				return false;
			}

			for (int i = 0; i < 6; i++) {
				if (sfn[i] == null) {
					Console.Println("Error: skybox: missing " + skyfaces[i] + " filename' near line " + script.filename
							+ ":" + script.getlinum(script.ltextptr), OSDTEXT_RED);
					return false;
				}

				if (!BuildGdx.cache.contains(sfn[i], 0)) {
					Console.Println("Error: file \"" + sfn[i] + "\" does not exist", OSDTEXT_RED);
					return false;
				}
			}

			texInfo.addSkybox(stile, spal, sfn);
			return true;
		}
	}

	protected class DefineSkyboxToken extends SkyboxToken {
		@Override
		public BaseToken parse(Scriptfile script) {
			int stile = -1, spal = 0;
			Integer ivalue;
			String[] sfn = new String[6];

			if ((ivalue = script.getsymbol()) != null)
				stile = ivalue;
			if ((ivalue = script.getsymbol()) != null)
				spal = ivalue;
			script.getsymbol();

			sfn[0] = getFile(script);
			sfn[1] = getFile(script);
			sfn[2] = getFile(script);
			sfn[3] = getFile(script);
			sfn[4] = getFile(script);
			sfn[5] = getFile(script);

			if (addSkybox(script, stile, spal, sfn))
				return BaseToken.Ok;
			return BaseToken.Error;
		}
	}

	protected class DefineTint implements Token {
		@Override
		public BaseToken parse(Scriptfile script) {

			Integer pal, r, g, b, f;

			if ((pal = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((r = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((g = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((b = script.getsymbol()) == null)
				return BaseToken.Error;
			if ((f = script.getsymbol()) == null)
				return BaseToken.Error;

			Console.Println("Loading tint " + pal);
			texInfo.setPaletteTint(pal.intValue(), r.intValue(), g.intValue(), b.intValue(), f.intValue());

			return BaseToken.Ok;
		}
	}

	protected enum TintTokens {
		PAL, RED, GREEN, BLUE, FLAGS
	}

	protected class TintToken implements Token {

		private final Map<String, TintTokens> tinttokens = new HashMap<String, TintTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("pal", TintTokens.PAL);
				put("red", TintTokens.RED);
				put("green", TintTokens.GREEN);
				put("blue", TintTokens.BLUE);
				put("flags", TintTokens.FLAGS);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {
			int pal = -1, r = 255, g = 255, b = 255, f = 0;
			Integer ivalue;
			int send;

			if ((send = script.getbraces()) == -1)
				return BaseToken.Error;

			while (script.textptr < send) {
				try {
					Object tk = gettoken(script, tinttokens);
					if (tk instanceof BaseToken) {
						int line = script.getlinum(script.ltextptr);
						Console.Println(script.filename + " has unknown token \""
								+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr))
								+ "\" on line: "
								+ toLowerCase(script.textbuf.substring(getPtr(script, line), getPtr(script, line + 1))),
								OSDTEXT_RED);
						continue;
					}

					switch ((TintTokens) tk) {
					case PAL:
						if ((ivalue = script.getsymbol()) != null)
							pal = ivalue;
						break;
					case RED:
						if ((ivalue = script.getsymbol()) != null)
							r = ivalue;
						break;
					case GREEN:
						if ((ivalue = script.getsymbol()) != null)
							g = ivalue;
						break;
					case BLUE:
						if ((ivalue = script.getsymbol()) != null)
							b = ivalue;
						break;
					case FLAGS:
						if ((ivalue = script.getsymbol()) != null)
							f = ivalue;
						break;

					default:
						break;
					}
				} catch (Exception e) {
				}
			}
			script.skipbrace(send); // close bracke
			if (pal == -1) {
				Console.Println("Tint palette is not found!", OSDTEXT_RED);
				return BaseToken.Error;
			}

			Console.Println("Loading tint " + pal);
			texInfo.setPaletteTint((int) pal, r, g, b, f);

			return BaseToken.Ok;
		}
	}

	public enum AudioTokens {
		ID, FILE
	}

	protected class AudioToken implements Token {
		private final Map<String, AudioTokens> sound_musictokens = new HashMap<String, AudioTokens>() {
			private static final long serialVersionUID = 1L;
			{
				put("id", AudioTokens.ID);
				put("midi", AudioTokens.ID);
				put("map", AudioTokens.ID);
				put("file", AudioTokens.FILE);
			}
		};

		@Override
		public BaseToken parse(Scriptfile script) {

			int dummy;
			String t_id = null, t_file = null;

			if ((dummy = script.getbraces()) == -1)
				return BaseToken.Error;
			while (script.textptr < dummy) {
				Object tk = gettoken(script, sound_musictokens);
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

				switch ((AudioTokens) tk) {
				default:
					break;
				case ID:
					String t = script.getstring();
					if (t != null)
						t_id = t.trim();
					break;
				case FILE:
					t_file = getFile(script);
					break;
				}
			}
			script.skipbrace(dummy); // close bracke

			audInfo.addDigitalInfo(t_id, t_file);

			return BaseToken.Ok;
		}
	}

	protected boolean checkErrorToken(Scriptfile script, Object tk) {
		if (tk instanceof BaseToken) {
			int line = script.getlinum(script.ltextptr);
			Console.Println(
					script.filename + " has unknown token \""
							+ toLowerCase(script.textbuf.substring(script.ltextptr, script.textptr)) + "\" on line: "
							+ toLowerCase(script.textbuf.substring(getPtr(script, line), getPtr(script, line + 1))),
					OSDTEXT_RED);
			return true;
		}

		return false;
	}
}
