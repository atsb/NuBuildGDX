// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Factory;

import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.scale;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.badlogic.gdx.Screen;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Build.Types.Palette;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.SmallTextFont;
import ru.m210projects.Build.Types.TextFont;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.LSP.Fonts.StandartFont;
import ru.m210projects.LSP.Types.SECTORV4;
import ru.m210projects.LSP.Types.WALLV4;

public class LSPEngine extends BuildEngine {

	public static final String sIntroTiles = "L0ART000.DAT";
	public static final String sGameTiles = "L1ART000.DAT";

	public LSPEngine(BuildGame game) throws Exception {
		super(game, 4);
		tilesPath = sIntroTiles;
		fpscol = 4;
	}

//	private boolean key = false;
//	@Override
//	public void sampletimer() {
//		if (timerfreq == 0)
//			return;
//
//		long n = (getticks() * timerticspersec / timerfreq) - timerlastsample;
//		if (n > 0) {
//			if(game.isCurrentScreen(gDemoScreen)) {
////				totalclock += 8;
//				if(BuildGdx.input.isKeyPressed(Keys.UP)) {
//					if(!key) {
//						totalclock += 4;
//						if(totalclock < 18860)
//							totalclock = 18860;
//						if(BuildGdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
//							key = true;
//						}
//					}
//				} else key = false;
//			} else totalclock += n;
//
//			timerlastsample += n;
//		}
//	}

	@Override
	public boolean setrendermode(Renderer render) {
		if(this.render != null && this.render != render)
		{
			if(render.getType() != RenderType.Software)
			{
				((StandartFont) game.getFont(1)).reinit();
				final Screen screen = game.getScreen();
				if(screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if(game.isCurrentScreen(gGameScreen))
								game.pNet.ready2send = true;
						}
					});
					game.changeScreen(gPrecacheScreen);
				}
			}
		}

		return super.setrendermode(render);
	}

	public void setTilesPath(int mapnum)
	{
		String newPath = sIntroTiles;
		if(mapnum != 0)
			newPath = sGameTiles;

		if(newPath.equals(tilesPath))
			return;

		int tile = 770;
		Tile pic = engine.getTile(770);
		byte[] data = null;
		int sizx = 0, sizy = 0;
		if(newPath.equals(sGameTiles)) {
			if(engine.loadtile(tile) != null) {
				sizx = pic.getWidth();
				sizy = pic.getHeight();
				data = new byte[sizx * sizy];
				System.arraycopy(pic.data, 0, data, 0, data.length);
			}
		}

		int kMaxTiles = MAXTILES - USERTILES; //don't touch usertiles
		System.err.println("Reset to default resources");
		for(int i = 0; i < kMaxTiles; i++) //don't touch usertiles
			engine.getTile(i).clear();

		tilesPath = newPath;
		engine.loadpics();

		if(data != null) {
			pic.data = data;
			pic.setWidth(sizx);
			pic.setHeight(sizy);
		}

		if(newPath.equals(sGameTiles))
			game.setDefs(new DefScript(game.baseDef, null));
		else  game.setDefs(game.baseDef);
	}

	@Override
	public byte getclosestcol(byte[] palette, int r, int g, int b) {
		int i;
		int dr, dg, db, dist, matchDist, match = 0;

		matchDist = 0x7FFFFFFF;

		for ( i = 0; i < 256; i++ )
		{
			dist = 0;

			dg = (palette[3 * i + 1] & 0xFF) - g;
			dist += 1 * dg * dg;
			if ( dist >= matchDist )
				continue;

			dr = (palette[3 * i] & 0xFF) - r;
			dist += 1 * dr * dr;
			if ( dist >= matchDist )
				continue;

			db = (palette[3 * i + 2] & 0xFF) - b;
			dist += 1 * db * db;
			if ( dist >= matchDist )
				continue;

			matchDist = dist;
			match = i;

			if (dist == 0)
				break;
		}

		return (byte) match;
	}

	public BuildPos loadboard(Resource fil) throws InvalidVersionException {
		BuildPos pos = new BuildPos();

		mapversion = fil.readInt();
		if (mapversion != 4) {
			fil.close();
			throw new InvalidVersionException(fil.getFullName() + ": invalid map version( v" + mapversion + " )!");
		}

		initspritelists();

		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);

		pos.x = fil.readInt();
		pos.y = fil.readInt();
		pos.z = fil.readInt();
		pos.ang = fil.readShort();
		pos.sectnum = fil.readShort();

		numsectors = fil.readShort();
		numwalls = fil.readShort();
		numsprites = fil.readShort();

		for (int sectorid = 0; sectorid < numsectors; sectorid++)
			sector[sectorid] = new SECTORV4(fil);
		for (int wallid = 0; wallid < numwalls; wallid++)
			wall[wallid] = new WALLV4(fil);

		int sizeof = 40;
		byte[] sprites = new byte[sizeof * numsprites];
		fil.read(sprites);
		ByteBuffer bb = ByteBuffer.wrap(sprites).order(ByteOrder.LITTLE_ENDIAN);

		for (int spriteid = 0; spriteid < numsprites; spriteid++) {
			SPRITE spr = sprite[spriteid];

			spr.x = bb.getInt();
			spr.y = bb.getInt();
			spr.z = bb.getInt();
			spr.cstat = bb.get();
			spr.shade = bb.get();
			spr.xrepeat = (short) (bb.get() & 0xFF);
			spr.yrepeat = (short) (bb.get() & 0xFF);
			spr.picnum = bb.getShort();
			spr.ang = bb.getShort();
			spr.xvel = bb.getShort();
			spr.yvel = bb.getShort();
			spr.zvel = bb.getShort();
			spr.owner = bb.getShort();
			spr.sectnum = bb.getShort();
			spr.statnum = bb.getShort();
			spr.lotag = bb.getShort();
			spr.hitag = bb.getShort();
			bb.getInt();
			spr.extra = -1;
			spr.clipdist = 32;

			insertsprite(spr.sectnum, spr.statnum);
		}

		// Must be after loading sectors, etc!
		pos.sectnum = updatesector(pos.x, pos.y, pos.sectnum);

		fil.close();

		if(inside(pos.x, pos.y, pos.sectnum) == -1)
			throw new RuntimeException("Player should be in a sector!");

		return pos;
	}

	public short movesprite(short spritenum, int dx, int dy, int dz, int clipdist, int ceildist, int flordist,
			int cliptype, int vel) {
		int daz, zoffs;
		short retval, dasectnum;

		SPRITE spr = sprite[spritenum];
		game.pInt.setsprinterpolate(spritenum, spr);

		if ((spr.cstat & 128) == 0)
			zoffs = -((engine.getTile(spr.picnum).getHeight() * spr.yrepeat) << 1);
		else zoffs = 0;

		dasectnum = spr.sectnum;
		daz = spr.z + zoffs;

		retval = (short) clipmove(spr.x, spr.y, daz, dasectnum, vel * dx << 11, vel * dy << 11, clipdist, ceildist, flordist, cliptype);
		if (clipmove_sectnum != -1) {
			spr.x = clipmove_x;
			spr.y = clipmove_y;
			daz = clipmove_z;
			dasectnum = clipmove_sectnum;
		}

		if (dasectnum != spr.sectnum && dasectnum >= 0)
			engine.changespritesect(spritenum, dasectnum);

		short oldcstat = spr.cstat;
		spr.cstat &= ~1;
		engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, clipdist, cliptype);
		spr.cstat = oldcstat;

		daz = spr.z + zoffs + (vel * dz >> 3);
		if ((daz <= zr_ceilz) || (daz > zr_florz)) {
			if (retval != 0)
				return (retval);
			return (short) (16384 | dasectnum);
		}
		spr.z = (daz - zoffs);
		return (retval);
	}

	@Override
	public BuildPos loadboard(String filename) throws InvalidVersionException, FileNotFoundException, RuntimeException {
		Resource fil;
		if ((fil = BuildGdx.cache.open(filename, 0)) == null) {
			mapversion = 4;
			throw new FileNotFoundException("Map " + filename + " not found!");
		}

		BuildPos pos = loadboard(fil);
		if (pos != null) {

			return pos;
		}
		else return super.loadboard(filename);
	}

	@Override
	public void loadpalette() throws Exception {
		Resource fil;
		if (paletteloaded != 0)
			return;

		palette = new byte[768];
		curpalette = new Palette();
		palookup = new byte[MAXPALOOKUPS][];

		Console.Println("Loading palettes");
		if ((fil = BuildGdx.cache.open("palette.dat", 0)) == null)
			throw new Exception("Failed to load \"palette.dat\"!");

		numshades = (short) ((fil.size() - 768) >> 7);
		if ((numshades & 1) <= 0)
			numshades >>= 1;
		else {
			numshades = (short) ((numshades - 255) >> 1);
			if (transluc == null)
				transluc = new byte[65536];
		}

		fil.read(palette);

		globalpal = 0;
		Console.Println("Loading gamma correction tables");
		if (palookup[globalpal] == null)
			palookup[globalpal] = new byte[numshades << 8];
		fil.read(palookup[globalpal], 0, numshades << 8);

		palookup[ANIM_PAL] = new byte[numshades << 8];
		for (int i = 0; i < MAXPALOOKUPS; i++)
			palookup[ANIM_PAL][i] = (byte) i;

		if (transluc != null) {
			Console.Println("Loading translucency table");
			byte[] tmp = new byte[256];
			for (int i = 0; i < 255; i++) {
				fil.read(tmp, 0, 255 - i);
				System.arraycopy(tmp, 0, transluc, (i << 8) + i + 1, 255 - i);
				for (int j = i + 1; j < 256; j++)
					transluc[(j << 8) + i] = transluc[(i << 8) + j];
			}
			for (int i = 0; i < 256; i++)
				transluc[(i << 8) + i] = (byte) i;
		}

		fil.close();

		initfastcolorlookup(30, 59, 11);

//		palette1 = BuildGdx.cache.getBytes(2, ""); //game palette
//		palette2 = BuildGdx.cache.getBytes(1, ""); //damage palette

		paletteloaded = 1;

		byte[] remapbuf = new byte[256];
		remapbuf[56] = remapbuf[57] = remapbuf[58] = 96;
		makepalookup(96, remapbuf, 0, 0, 0, 1); //disabled font

		remapbuf[56] = remapbuf[57] = remapbuf[58] = (byte) 227;
		makepalookup(228, remapbuf, 0, 0, 0, 1); //font color

		remapbuf[56] = remapbuf[57] = remapbuf[58] = (byte) 17; //choosed font
		remapbuf[227] = 17; //pink
		remapbuf[163] = (byte) 174;
		remapbuf[0] = 28;
		makepalookup(70, remapbuf, 0, 0, 0, 1);
	}

	@Override
	public void loadtables() throws Exception {
		if (tablesloaded == 0) {
			initksqrt();

			sintable = new short[2048];
			textfont = new byte[2048];
			smalltextfont = new byte[2048];
			radarang = new short[640];

			Resource res = BuildGdx.cache.open("tables.dat", 0);
			if (res != null) {
				byte[] buf = new byte[2048 * 2];

				res.read(buf);
				ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sintable);

				res.seek(4096, Whence.Current); // tantable

				buf = new byte[640];
				res.read(buf);
				ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(radarang, 0, 320);
				radarang[320] = 0x4000;

				res.read(textfont);
				res.read(smalltextfont);

				pTextfont = new TextFont();
				pSmallTextfont = new SmallTextFont();

				calcbritable();
				res.close();
			} else
				throw new Exception("ERROR: Failed to load TABLES.DAT!");

			tablesloaded = 1;
		}
	}

	@Override
	public short getangle(int xvect, int yvect) {
		if ((xvect | yvect) == 0)
			return (0);
		if (xvect == 0)
			return (short) (512 + ((yvect < 0 ? 1 : 0) << 10));
		if (yvect == 0)
			return (short) ((xvect < 0 ? 1 : 0) << 10);
		if (xvect == yvect)
			return (short) (256 + ((xvect < 0 ? 1 : 0) << 10));
		if (xvect == -yvect)
			return (short) (768 + ((xvect > 0 ? 1 : 0) << 10));

		if (klabs(xvect) > klabs(yvect)) {
			return (short) (((radarang[160 + scale(160, yvect, xvect)]) + ((xvect < 0 ? 1 : 0) << 10)) & 2047);
		}
		return (short) (((radarang[160 - scale(160, xvect, yvect)]) + 512 + ((yvect < 0 ? 1 : 0) << 10)) & 2047);
	}

}
