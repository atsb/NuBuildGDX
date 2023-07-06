// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Main.*;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.Tile;

public class MovieScreen extends SkippableAdapter {

	private LmfFile lmf;
	private Runnable callback;
	private int gCutsClock;

	private long movtime;
	private long LastMS;
	private long lastCrc32;

	private int backgroundCol = 0;
	private int nScale, nAngle;
	private ByteBuffer audbuf = ByteBuffer.allocateDirect(4096);

	private static class Subtitle {
		public String[] text;
		public int from, to;

		public Subtitle(String[] text, int from, int to) {
			this.text = text;
			this.from = from;
			this.to = to;
		}
	}

	private int currSub;
	private Subtitle[] subs = {
			new Subtitle(new String[]{"During the time of the pharaohs, the city of Karnak",
					"was a shining example of a civilization that all", "others nations could only hope to emulate"},
					35, 132),

			new Subtitle(new String[]{"Today Karnak lives on, surrounded by the spirits",
					"of the past, however something has gone terribly wrong."}, 143, 228),

			new Subtitle(new String[]{"Unknown forces have seized the city and great",
					"turmoil is spreading into neighboring lands."}, 242, 302),

			new Subtitle(new String[]{"World leaders from all parts of the globe have sent",
					"forces into the Karnak Valley, but none have returned."}, 308, 384),

			new Subtitle(new String[]{"The great power of this new empire is quickly crushing",
					"the best forces the human world has to offer."}, 392, 456),

			new Subtitle(new String[]{"The only known information about this crisis came from",
					"a Karnak villager, found wandering through the desert",
					"miles from his home, dazed, dehydrated and close to death."}, 477, 594),

			new Subtitle(new String[]{"In his final moments among the living, the villager told",
					"horrifying stories of fierce alien creatures that invaded",
					"the city, devoured the women and children, and made slaves", "of the men."}, 610, 733),

			new Subtitle(
					new String[]{"Many of the unfortunate victims were", "skinned alive or brutally dismembered."},
					741, 794),

			new Subtitle(new String[]{"Others were subjected to unbearable tortures, ",
					"injected with strange substances and ", "then mummified while still alive."}, 803, 886),

			new Subtitle(new String[]{"According to the villager, even the mummified body",
					"of the great King Ramses was ", "unearthed and taken away."}, 893, 967),

			new Subtitle(new String[]{"You have been chosen from a group of the best",
					"operatives in the world to infiltrate Karnak and ", "destroy the threatening forces."}, 979, 1055),

			new Subtitle(new String[]{"But as your helicopter nears the Karnak Valley, ",
					"it is shot down. You barely escape with your life."}, 1059, 1128),

			new Subtitle(new String[]{"With no possible contact to the outside world,",}, 1134, 1164),

			new Subtitle(new String[]{"you begin your adventure,",}, 1168, 1185),

			new Subtitle(new String[]{"ready to accomplish your mission...",}, 1190, 1208),

			new Subtitle(new String[]{"praying to return alive.",}, 1215, 1235), };

	private class LmfFile {

		private Resource res;
		private byte[] framebuf;
		public int framenum;

		public LmfFile(String fn) throws Exception {
			res = BuildGdx.cache.open(fn, 0);
			if (res == null)
				throw new FileNotFoundException();

			int size = res.size();
			if (size < 32 || !res.readString(4).equals("LMF ")) {
				res.close();
				throw new Exception("Wrong file format");
			}

			res.seek(28, Whence.Current);
			framebuf = new byte[200 * 320];

			lastCrc32 = 0;
			framenum = 0;
			currSub = -1;
			backgroundCol = 96;
			nScale = 0;
			nAngle = 1536;
		}

		public boolean ReadFrame() {
			while (true) {
				Byte id = res.readByte();
				if (id == null || id == 0)
					return false;

				int cSize = res.readInt();
				switch (id) {
				case 1:
					byte[] pal = new byte[768];
					res.read(pal);
					res.readByte();

					for (int i = 0; i < 768; i++)
						pal[i] <<= 2;

					changepalette(pal);
					continue;
				case 2: // Audio chunk
					res.read(audbuf, 0, cSize);
					audbuf.rewind().limit(cSize);
					Source hVoice = BuildGdx.audio.newSound(audbuf, 22050, 8, 255);
					if (hVoice != null) {
						hVoice.setGlobal(1);
						hVoice.play(255);
					}
					continue;
				case 3: // Video chunk
					if (cSize != 0) {
						int ptr = 200 * res.readShort();
						cSize -= 2;
						while (cSize > 0) {
							ptr += (res.readByte() & 0xFF);
							int len = res.readByte() & 0xFF;
							cSize -= 2;
							if (len != 0) {
								res.read(framebuf, ptr, len);
								ptr += len;
								cSize -= len;
							}
						}
					}
					continue;
				case 4: // End of frame
					framenum++;
					int nextSub = currSub + 1;
					if (nextSub < subs.length && framenum >= subs[currSub + 1].from)
						currSub++;
					return true;
				default:
					continue;
				}
			}
		}

		public boolean isEof() {
			return res.position() >= res.size();
		}

		public void close() {
			res.close();
		}
	}

	private void changepalette(byte[] pal) {
		if (pal == null)
			return;

		engine.changepalette(pal);

		int white = -1;
		int k = 0;
		for (int i = 0; i < 256; i += 3) {
			int j = (pal[3 * i] & 0xFF) + (pal[3 * i + 1] & 0xFF) + (pal[3 * i + 2] & 0xFF);
			if (j > k) {
				k = j;
				white = i;
			}
		}

		backgroundCol = 0;
		k = 255;
		for (int i = 0; i < 256; i += 3) {
			int j = (pal[3 * i] & 0xFF) + (pal[3 * i + 1] & 0xFF) + (pal[3 * i + 2] & 0xFF);
			if (j < k) {
				k = j;
				backgroundCol = i;
			}
		}

		if (white == -1)
			return;

		int palnum = MAXPALOOKUPS - RESERVEDPALS - 1;
		byte[] remapbuf = new byte[768];
		for (int i = 0; i < 768; i++)
			remapbuf[i] = (byte) white;
		engine.makepalookup(palnum, remapbuf, 0, 1, 0, 1);

		for (int i = 0; i < 256; i++) {
			int tile = game.getFont(0).getTile(i);
			if (tile >= 0)
				engine.getrender().invalidatetile(tile, palnum, -1);
		}
	}

	public MovieScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (game.pMenu.gShowMenu)
			game.pMenu.mClose();

		engine.sampletimer();
		LastMS = engine.getticks();
		gCutsClock = totalclock = 0;

		StopAllSounds();
		StopMusic();
	}

	@Override
	public void hide() {
		engine.setbrightness(BuildSettings.paletteGamma.get(), palette, GLInvalidateFlag.All);
	}

	@Override
	public void skip() {
		lmfClose();
		super.skip();
	}

	public MovieScreen setCallback(Runnable callback) {
		this.callback = callback;
		this.setSkipping(callback);
		return this;
	}

	public boolean init(String fn) {
		if (lmf != null)
			return false;

		try {
			engine.loadtile(764);
			lmf = new LmfFile(fn);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private int oldAngle, oldScale;

	private boolean play() {
		if (lmf != null) {
			Tile pic = engine.getTile(764);
			if (lmf.isEof() || pic.getWidth() <= 0)
				return false;

			if (LastMS == -1)
				LastMS = engine.getticks();

			long ms = engine.getticks();
			long dt = ms - LastMS;
			movtime += dt;
			float tick = 1000f / 10f;
			if (movtime >= tick) {
				if (lmf.ReadFrame()) {
					pic.data = lmf.framebuf;
					long crc32 = CRC32.getChecksum(lmf.framebuf);
					if (lastCrc32 != crc32) {
						engine.getrender().invalidatetile(764, 0, -1);
						lastCrc32 = crc32;
					}
				}
				oldScale = nScale;
				if (nScale < 65536)
					nScale += 2048;

				oldAngle = nAngle;
				if (nAngle != 0) {
					nAngle += 16;
					if (nAngle == 2048)
						nAngle = 0;
				}
				movtime -= tick;
			}

			LastMS = ms;
			if (pic.data != null) {
				int ang = oldAngle;
				int scale = oldScale;

				if (nAngle != oldAngle && nScale != oldScale) {
					int smoothratio = (int) ((movtime / tick) * 65536.0f);
					ang += mulscale(((nAngle - oldAngle + 1024) & 0x7FF) - 1024, smoothratio, 16);
					scale += mulscale(nScale - oldScale, smoothratio, 16);
				}

				engine.rotatesprite(160 << 16, 100 << 16, scale, ang, 764, 0, 0, 10 | 64, 0, 0, xdim - 1, ydim - 1);
			}

			if (cfg.bSubtitles && (currSub != -1 && lmf.framenum < subs[currSub].to)) {
				String text[] = subs[currSub].text;
				int pos = 190 - 8 * text.length;
				for (String s : text)
					game.getFont(0).drawText(160, pos += 8, s, 50000, 0, MAXPALOOKUPS - RESERVEDPALS - 1,
							TextAlign.Center, 2, true);
			}
			return true;
		}

		return false;
	}

	@Override
	public void draw(float delta) {
		engine.clearview(backgroundCol);
		if (!play() && skipCallback != null) {
			lmfClose();
			if (callback != null) {
				BuildGdx.app.postRunnable(callback);
				callback = null;
			}
		}

		if (game.pInput.ctrlKeyStatus(ANYKEY))
			gCutsClock = totalclock;

		int shade = 16 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
		if (totalclock - gCutsClock < 200 && escSkip) // 2 sec
			game.getFont(0).drawText(160, 5, "Press ESC to skip", shade, MAXPALOOKUPS - RESERVEDPALS - 1,
					TextAlign.Center, 2, true);
	}

	private void lmfClose() {
		if (lmf != null)
			lmf.close();
		lmf = null;
		nAngle = oldAngle = 0;
		oldScale = nScale = 0;
	}
}
