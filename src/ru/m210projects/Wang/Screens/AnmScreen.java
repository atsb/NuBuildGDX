package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Wang.Digi.DIGI_HOTHEADSWITCH;
import static ru.m210projects.Wang.Digi.DIGI_INTRO_SLASH;
import static ru.m210projects.Wang.Digi.DIGI_INTRO_WHIRL;
import static ru.m210projects.Wang.Digi.DIGI_JG41012;
import static ru.m210projects.Wang.Digi.DIGI_JG41028;
import static ru.m210projects.Wang.Digi.DIGI_JG94024;
import static ru.m210projects.Wang.Digi.DIGI_JG94039;
import static ru.m210projects.Wang.Digi.DIGI_NOMESSWITHWANG;
import static ru.m210projects.Wang.Digi.DIGI_SERPTAUNTWANG;
import static ru.m210projects.Wang.Digi.DIGI_SHAREND_TELEPORT;
import static ru.m210projects.Wang.Digi.DIGI_SHAREND_UGLY1;
import static ru.m210projects.Wang.Digi.DIGI_SHAREND_UGLY2;
import static ru.m210projects.Wang.Digi.DIGI_WANGTAUNTSERP1;
import static ru.m210projects.Wang.Digi.DIGI_Z16043;
import static ru.m210projects.Wang.Digi.DIGI_ZC1;
import static ru.m210projects.Wang.Digi.DIGI_ZC2;
import static ru.m210projects.Wang.Digi.DIGI_ZC3;
import static ru.m210projects.Wang.Digi.DIGI_ZC4;
import static ru.m210projects.Wang.Digi.DIGI_ZC5;
import static ru.m210projects.Wang.Digi.DIGI_ZC6;
import static ru.m210projects.Wang.Digi.DIGI_ZC7;
import static ru.m210projects.Wang.Digi.DIGI_ZC8;
import static ru.m210projects.Wang.Digi.DIGI_ZC9;
import static ru.m210projects.Wang.Game.ANIM_INTRO;
import static ru.m210projects.Wang.Game.ANIM_SERP;
import static ru.m210projects.Wang.Game.ANIM_SUMO;
import static ru.m210projects.Wang.Game.ANIM_ZILLA;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.StopSound;
import static ru.m210projects.Wang.Sound.v3df_none;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Type.AnimFile;

public class AnmScreen extends MovieScreen {

	private String ANIMname[] = { "sw.anm", "swend.anm", "sumocinm.anm", "zfcin.anm" };
	private int ANIMnum;
	private float ANIMrate;

	public AnmScreen(BuildGame game) {
		super(game, MAXTILES - 2);

		this.nFlags = 4 | 8 | 64;
	}

	public boolean init(int anim_num) {
		if (isInited())
			return false;

		if(anim_num < 0 || anim_num >= ANIMname.length)
			return false;

		if (!open(ANIMname[anim_num]))
			return false;

		Tile pic = engine.getTile(TILE_MOVIE);
		float kt = pic.getHeight() / (float) pic.getWidth();
		float kv = xdim / (float) ydim;

		float scale = 1.0f;

		if(kv >= kt)
        	scale = (ydim / (float) pic.getWidth());
        else scale = (xdim / (float) pic.getHeight());

        nScale = (int) (scale * 65536.0f);
        nPosX = (xdim / 2);
        nPosY = (ydim / 2);

		ANIMnum = anim_num;
		ANIMrate = 0;
		frame = 1;

		return true;
	}

	@Override
	protected MovieFile GetFile(String file) {
		byte[] animbuf = BuildGdx.cache.getBytes(file, 0);
		if (animbuf == null)
			return null;

		return new AnimFile(animbuf) {
			@Override
			public float getRate() {
				return ANIMrate;
			}
		};
	}

	@Override
	protected void StopAllSounds() {
		COVER_SetReverb(0);
		if(ANIMnum != ANIM_INTRO)
			StopSound();
	}

	@Override
	protected byte[] DoDrawFrame(int i) {
		ANIMrate = 0;
		switch (ANIMnum) {
		case ANIM_INTRO:
			AnimShareIntro(i, mvfil.getFrames());
			break;
		case ANIM_SERP:
			AnimSerp(i, mvfil.getFrames());
			break;
		case ANIM_SUMO:
			AnimSumo(i, mvfil.getFrames());
			break;
		case ANIM_ZILLA:
			AnimZilla(i, mvfil.getFrames());
			break;
		}

		return mvfil.getFrame(frame);
	}

	@Override
	protected BuildFont GetFont() {
		return game.getFont(0);
	}

	@Override
	protected void DrawEscText(BuildFont font, int pal) {
		int shade = 16 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
		font.drawText(160, 5, "Press ESC to skip", shade, pal, TextAlign.Center, 2, true);
	}

	protected void AnimShareIntro(int frame, int numframes) {
		if (frame == numframes - 1)
			ANIMrate += 1000.0f;
		else if (frame == 1) {
			PlaySound(DIGI_NOMESSWITHWANG, null, v3df_none);
			ANIMrate += 1000.0f * 3;
		} else
			ANIMrate += 60.0f;

		if (frame == 5) {
			PlaySound(DIGI_INTRO_SLASH, null, v3df_none);
		} else if (frame == 15) {
			PlaySound(DIGI_INTRO_WHIRL, null, v3df_none);
		}
	}

	protected void AnimSerp(int frame, int numframes) {
		ANIMrate += 130.0f;

		if (frame == numframes - 1)
			ANIMrate += 1250.0f;

		if (frame == 1) {
			PlaySound(DIGI_SERPTAUNTWANG, null, v3df_none);
		} else if (frame == 16) {
			PlaySound(DIGI_SHAREND_TELEPORT, null, v3df_none);
		} else if (frame == 35) {
			PlaySound(DIGI_WANGTAUNTSERP1, null, v3df_none);
		} else if (frame == 51) {
			PlaySound(DIGI_SHAREND_UGLY1, null, v3df_none);
		} else if (frame == 64) {
			PlaySound(DIGI_SHAREND_UGLY2, null, v3df_none);
		}
	}

	protected void AnimSumo(int frame, int numframes) {
		ANIMrate += 60.0f;

		if (frame == numframes - 1)
			ANIMrate += 1000.0f;

		if (frame == 1)
			ANIMrate += 250.0f;

		if (frame == 2) {
			// hungry
			PlaySound(DIGI_JG41012, null, v3df_none);
		} else if (frame == 30) {
			PlaySound(DIGI_HOTHEADSWITCH, null, v3df_none);
		} else if (frame == 42) {
			PlaySound(DIGI_HOTHEADSWITCH, null, v3df_none);
		} else if (frame == 59) {
			PlaySound(DIGI_JG41028, null, v3df_none);
		}
	}

	protected void AnimZilla(int frame, int numframes) {
		ANIMrate += 130.0f;

		if (frame == numframes - 1)
			ANIMrate += 1250.0f;

		if (frame == 1) {
			PlaySound(DIGI_ZC1, null, v3df_none);
		} else if (frame == 5) {
			PlaySound(DIGI_JG94024, null, v3df_none);
		} else if (frame == 14) {
			PlaySound(DIGI_ZC2, null, v3df_none);
		} else if (frame == 30) {
			PlaySound(DIGI_ZC3, null, v3df_none);
		} else if (frame == 32) {
			PlaySound(DIGI_ZC4, null, v3df_none);
		} else if (frame == 37) {
			PlaySound(DIGI_ZC5, null, v3df_none);
		} else if (frame == 63) {
			PlaySound(DIGI_Z16043, null, v3df_none);
			PlaySound(DIGI_ZC6, null, v3df_none);
			PlaySound(DIGI_ZC7, null, v3df_none);
		} else if (frame == 72) {
			PlaySound(DIGI_ZC7, null, v3df_none);
		} else if (frame == 73) {
			PlaySound(DIGI_ZC4, null, v3df_none);
		} else if (frame == 77) {
			PlaySound(DIGI_ZC5, null, v3df_none);
		} else if (frame == 87) {
			PlaySound(DIGI_ZC8, null, v3df_none);
		} else if (frame == 103) {
			PlaySound(DIGI_ZC7, null, v3df_none);
		} else if (frame == 108) {
			PlaySound(DIGI_ZC9, null, v3df_none);
		} else if (frame == 120) {
			PlaySound(DIGI_JG94039, null, v3df_none);
		}
	}

	public int getAnim() {
		return ANIMnum;
	}
}
