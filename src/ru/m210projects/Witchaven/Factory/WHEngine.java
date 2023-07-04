package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Witchaven.Main.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Witchaven.Globals.*;

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
import ru.m210projects.Build.Types.Palette;
import ru.m210projects.Build.Types.SmallTextFont;
import ru.m210projects.Build.Types.TextFont;

public class WHEngine extends BuildEngine {

	public WHEngine(BuildGame game) throws Exception {
		super(game, TICSPERFRAME);
	}

	@Override
	public void loadtables() throws Exception {
		if (tablesloaded == 0) {
			initksqrt();

			sintable = new short[2048];
			textfont = new byte[2048];
			smalltextfont = new byte[2048];
			radarang = new short[1280]; // 1024

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

				res.read(textfont, 0, 1024);
				res.read(smalltextfont, 0, 1024);

				pTextfont = new TextFont();
				pSmallTextfont = new SmallTextFont();

				/* kread(fil, britable, 1024); */

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
			return (short) (((radarang[160 + scale(160, yvect, xvect)] >> 6) + ((xvect < 0 ? 1 : 0) << 10)) & 2047);
		}
		return (short) (((radarang[160 - scale(160, xvect, yvect)] >> 6) + 512 + ((yvect < 0 ? 1 : 0) << 10)) & 2047);
	}

	@Override
	public boolean setrendermode(Renderer render) {
		if (this.render != null && this.render != render) {
			if (render.getType() != RenderType.Software) {
				final Screen screen = game.getScreen();
				if (screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if (game.isCurrentScreen(gGameScreen))
								game.pNet.ready2send = true;
						}
					});
					game.changeScreen(gPrecacheScreen);
				}
			}
		}

		return super.setrendermode(render);
	}

	@Override
	public int animateoffs(int tilenum, int nInfo) {
		long clock, index = 0;

		int speed = getTile(tilenum).getSpeed();
		clock = totalclock >> speed;

		int frames = getTile(tilenum).getFrames();

		if (frames > 0) {
			switch (getTile(tilenum).getType()) {
			case Oscil:
				index = clock % (frames * 2);
				if (index >= frames)
					index = frames * 2 - index;
				break;
			case Forward:
				index = clock % (frames + 1);
				break;
			case Backward:
				index = -(clock % (frames + 1));
				break;
			default: // None
				break;
			}
		}
		return (int) index;
	}

	@Override
	public short insertsprite(int sectnum, int statnum) {
		short j = super.insertsprite(sectnum, statnum);
		if (j != -1)
			sprite[j].detail = 0;
		return j;
	}

	@Override
	public short deletesprite(int spritenum) {
		sprite[spritenum].detail = 0;
		return super.deletesprite(spritenum);
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

		fil.read(palette);
		boolean hastransluc = false;

		if (game.WH2) {
			numshades = fil.readShort();
		} else {
			int file_len = fil.size();
			numshades = (short) ((file_len - 768) >> 7);
			if ((((file_len - 768) >> 7) & 1) <= 0)
				numshades >>= 1;
			else {
				numshades = (short) ((numshades - 255) >> 1);
				hastransluc = true;
			}
		}
		if (palookup[0] == null)
			palookup[0] = new byte[numshades << 8];
		if (transluc == null)
			transluc = new byte[65536];

		globalpal = 0;
		Console.Println("Loading gamma correction tables");
		fil.read(palookup[globalpal], 0, numshades << 8);
		Console.Println("Loading translucency table");
		if (game.WH2)
			fil.read(transluc);
		else {
			if (hastransluc) {
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
		}

		fil.close();

		initfastcolorlookup(30, 59, 11);

		paletteloaded = 1;
	}
}
