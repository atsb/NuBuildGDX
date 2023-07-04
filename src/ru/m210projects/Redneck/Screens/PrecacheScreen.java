package ru.m210projects.Redneck.Screens;

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Redneck.Actors.badguy;
import static ru.m210projects.Redneck.Globals.Sound;
import static ru.m210projects.Redneck.Globals.boardfilename;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.kGameCrash;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.mUserFlag;
import static ru.m210projects.Redneck.Names.ACCESS_ICON;
import static ru.m210projects.Redneck.Names.ALIENARMGUN;
import static ru.m210projects.Redneck.Names.AMMO;
import static ru.m210projects.Redneck.Names.AMMOBOX;
import static ru.m210projects.Redneck.Names.APLAYER;
import static ru.m210projects.Redneck.Names.ARROW;
import static ru.m210projects.Redneck.Names.BANJOCOOTER;
import static ru.m210projects.Redneck.Names.BEER_ICON;
import static ru.m210projects.Redneck.Names.BIGALPHANUM;
import static ru.m210projects.Redneck.Names.BIKERRIDE;
import static ru.m210projects.Redneck.Names.BIKERRIDEDAISY;
import static ru.m210projects.Redneck.Names.BIKERSTAND;
import static ru.m210projects.Redneck.Names.BILLYJIBB;
import static ru.m210projects.Redneck.Names.BILLYRAY;
import static ru.m210projects.Redneck.Names.BILLYWALK;
import static ru.m210projects.Redneck.Names.BLOOD;
import static ru.m210projects.Redneck.Names.BOOTS;
import static ru.m210projects.Redneck.Names.BOOT_ICON;
import static ru.m210projects.Redneck.Names.BOTTOMSTATUSBAR;
import static ru.m210projects.Redneck.Names.BUBBASCRATCH;
import static ru.m210projects.Redneck.Names.BUBBASTAND;
import static ru.m210projects.Redneck.Names.BULLETHOLE;
import static ru.m210projects.Redneck.Names.BURNING;
import static ru.m210projects.Redneck.Names.BURNING2;
import static ru.m210projects.Redneck.Names.BUSTAWIN4A;
import static ru.m210projects.Redneck.Names.BUSTAWIN4B;
import static ru.m210projects.Redneck.Names.BUSTAWIN5A;
import static ru.m210projects.Redneck.Names.BUSTAWIN5B;
import static ru.m210projects.Redneck.Names.BUZSAW;
import static ru.m210projects.Redneck.Names.CIRCLESTUCK;
import static ru.m210projects.Redneck.Names.COOT;
import static ru.m210projects.Redneck.Names.COOTJIBA;
import static ru.m210projects.Redneck.Names.COWPIE_ICON;
import static ru.m210projects.Redneck.Names.CROSSHAIR;
import static ru.m210projects.Redneck.Names.DAISYMAE;
import static ru.m210projects.Redneck.Names.DIGITALNUM;
import static ru.m210projects.Redneck.Names.DOGATTACK;
import static ru.m210projects.Redneck.Names.DOGRUN;
import static ru.m210projects.Redneck.Names.ECLAIRHEALTH;
import static ru.m210projects.Redneck.Names.ENDALPHANUM;
import static ru.m210projects.Redneck.Names.EXPLOSION2;
import static ru.m210projects.Redneck.Names.FIRSTGUNSPRITE;
import static ru.m210projects.Redneck.Names.FOOTPRINTS;
import static ru.m210projects.Redneck.Names.FORCERIPPLE;
import static ru.m210projects.Redneck.Names.FRAGBAR;
import static ru.m210projects.Redneck.Names.GUITARBILLY;
import static ru.m210projects.Redneck.Names.HEALTHBOX;
import static ru.m210projects.Redneck.Names.HEN;
import static ru.m210projects.Redneck.Names.HULK;
import static ru.m210projects.Redneck.Names.HULKJIBA;
import static ru.m210projects.Redneck.Names.HULKJIBC;
import static ru.m210projects.Redneck.Names.INVENTORYBOX;
import static ru.m210projects.Redneck.Names.JACKOLOPE;
import static ru.m210projects.Redneck.Names.JIBS1;
import static ru.m210projects.Redneck.Names.JIBS5;
import static ru.m210projects.Redneck.Names.JIBS6;
import static ru.m210projects.Redneck.Names.KILLSICON;
import static ru.m210projects.Redneck.Names.LOADSCREEN;
import static ru.m210projects.Redneck.Names.LTH;
import static ru.m210projects.Redneck.Names.MAMAJACKOLOPE;
import static ru.m210projects.Redneck.Names.MINIFONT;
import static ru.m210projects.Redneck.Names.MINION;
import static ru.m210projects.Redneck.Names.MINJIBA;
import static ru.m210projects.Redneck.Names.MINJIBC;
import static ru.m210projects.Redneck.Names.MOONSHINE_ICON;
import static ru.m210projects.Redneck.Names.MOSQUITO;
import static ru.m210projects.Redneck.Names.NEWCROSSHAIR;
import static ru.m210projects.Redneck.Names.NEWCROWBAR;
import static ru.m210projects.Redneck.Names.NEWDYNAMITE;
import static ru.m210projects.Redneck.Names.NEWPISTOL;
import static ru.m210projects.Redneck.Names.NEWSHOTGUN;
import static ru.m210projects.Redneck.Names.OOZFILTER;
import static ru.m210projects.Redneck.Names.PIG;
import static ru.m210projects.Redneck.Names.PIGSTAYPUT;
import static ru.m210projects.Redneck.Names.RIFLE;
import static ru.m210projects.Redneck.Names.SCRAP1;
import static ru.m210projects.Redneck.Names.SCRAP6;
import static ru.m210projects.Redneck.Names.SCREENBREAK6;
import static ru.m210projects.Redneck.Names.SCREENBREAK7;
import static ru.m210projects.Redneck.Names.SCREENBREAK8;
import static ru.m210projects.Redneck.Names.SEENINE;
import static ru.m210projects.Redneck.Names.SHELL;
import static ru.m210projects.Redneck.Names.SHOTSPARK1;
import static ru.m210projects.Redneck.Names.SMALLSMOKE;
import static ru.m210projects.Redneck.Names.SNORKLE_ICON;
import static ru.m210projects.Redneck.Names.STARTALPHANUM;
import static ru.m210projects.Redneck.Names.TEATAMMO;
import static ru.m210projects.Redneck.Names.TORNADO;
import static ru.m210projects.Redneck.Names.UFO1;
import static ru.m210projects.Redneck.Names.UFO2;
import static ru.m210projects.Redneck.Names.UFO3;
import static ru.m210projects.Redneck.Names.UFO4;
import static ru.m210projects.Redneck.Names.UFO5;
import static ru.m210projects.Redneck.Names.VIXEN;
import static ru.m210projects.Redneck.Names.WATERTILE2;
import static ru.m210projects.Redneck.Names.WHISHKEY_ICON;
import static ru.m210projects.Redneck.Names.W_FORCEFIELD;
import static ru.m210projects.Redneck.Premap.getsound;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Main.UserFlag;

public class PrecacheScreen extends PrecacheAdapter {

	private Main app;

	public PrecacheScreen(Main app) {
		super(app);
		this.app = app;

		addQueue("Preload sounds...", new Runnable() {
			@Override
			public void run() {
				precachenecessarysounds();
			}
		});

		addQueue("Preload floor and ceiling tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numsectors; i++) {
					addTile(sector[i].floorpicnum);
					addTile(sector[i].ceilingpicnum);
				}
				doprecache(0);

				for (int i = 0; i < numsectors; i++) {
					if ((sector[i].ceilingstat & 1) != 0) {
						if (sector[i].ceilingpicnum == 1031)
							for (int j = 0; j < 5; j++)
								addTile(sector[i].ceilingpicnum + j);
						else
							addTile(sector[i].ceilingpicnum);
					}
				}

				doprecache(1);
			}
		});

		addQueue("Preload wall tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numwalls; i++) {
					addTile(wall[i].picnum);
					switch (wall[i].picnum) {
					case WATERTILE2:
						for (int j = 0; j < 3; j++)
							addTile(wall[i].picnum + j);
						break;
					case BUSTAWIN4A:
					case BUSTAWIN4B:
					case BUSTAWIN5A:
					case BUSTAWIN5B:
						addTile(wall[i].picnum);
						break;
					case SCREENBREAK6:
					case SCREENBREAK7:
					case SCREENBREAK8:
						for (int k = SCREENBREAK6; k < SCREENBREAK8; k++)
							addTile(k);
						break;
					}
					if (wall[i].overpicnum >= 0) {
						addTile(wall[i].overpicnum);
						switch (wall[i].overpicnum) {
						case W_FORCEFIELD:
							for (int j = 0; j < 3; j++)
								addTile(W_FORCEFIELD + j);
						}
					}
				}
				doprecache(0);
			}
		});

		addQueue("Preload sprite tiles...", new Runnable() {
			@Override
			public void run() {
				cachegoodsprites();
				for (int i = 0; i < numsectors; i++) {
					int j = headspritesect[i];
					while (j >= 0) {
						if (sprite[j].xrepeat != 0 && sprite[j].yrepeat != 0 && (sprite[j].cstat & 32768) == 0)
							cachespritenum(j);
						j = nextspritesect[j];
					}
				}

				doprecache(1);
			}
		});

		addQueue("Preload other tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < MAXTILES; i++) {
					if (!engine.getTile(i).isLoaded())
						engine.loadtile(i);
				}
			}
		});
	}

	@Override
	public void draw(String title, int i) {
		if (kGameCrash) {
			app.show();
			return;
		}

		engine.clearview(129);
		engine.rotatesprite(320 << 15, 200 << 15, 65536, 0, LOADSCREEN, 0, 0, 2 + 8 + 64, 0, 0, xdim - 1, ydim - 1);

		if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
			app.getFont(2).drawText(160, 90, toCharArray("Entering "), -128, 0, TextAlign.Center, 2, false);
			if (currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null
					&& currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title != null)
				game.getFont(2).drawText(160, 90 + 16 + 8,
						currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title, -128, 0,
						TextAlign.Center, 2, false);
		} else if (boardfilename != null) {
			app.getFont(2).drawText(160, 90, toCharArray("Entering usermap"), -128, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 90 + 16 + 8, boardfilename, -128, 0, TextAlign.Center, 2, false);
		}

		app.getFont(1).drawText(160, 90 + 48 + 8, title, -128, 0, TextAlign.Center, 2, false);
	}

	private void precachenecessarysounds() {
		for (int i = 0; i < NUM_SOUNDS; i++)
			if (Sound[i].ptr == null) {
				getsound(i);
			}
	}

	private void cachespritenum(int i) {
		short j;

		if (ud.monsters_off && badguy(sprite[i]))
			return;

		int maxc = 1;
		switch (sprite[i].picnum) {
		case APLAYER:
			maxc = 0;
			if (ud.multimode > 1) {
				maxc = 5;
				for (j = 1420; j < 1420 + 106; j++)
					addTile(j);
			}
			break;
		case 2121:
		case 2122:
			maxc = 1;
			break;
		case BILLYRAY:
			for (j = BILLYWALK; j < BILLYJIBB + 4; j++)
				addTile(j);
			maxc = 0;
			break;
		case DOGRUN:
			for (j = DOGATTACK; j <= 4095; j++)
				addTile(j);
			for (j = DOGRUN; j <= 4340; j++)
				addTile(j);
			maxc = 0;
			break;
		case LTH:
			for (j = LTH; j < 4457; j++)
				addTile(j);
			maxc = 0;
			break;
		case BUBBASTAND:
			for (j = BUBBASCRATCH; j < 4511; j++)
				addTile(j);
			maxc = 0;
			break;
		case HULK:
			for (j = (short) (sprite[i].picnum - 41); j < sprite[i].picnum - 1; j++)
				addTile(j);
			for (j = HULKJIBA; j <= HULKJIBC + 4; j++)
				addTile(j);
			maxc = 0;
			break;
		case 4770: // BUBBAELVIS
			for (j = 4770; j <= 4799; j++)
				addTile(j);
			maxc = 0;
			break;
		case BIKERRIDE:
		case BIKERRIDE + 1:
			for (j = BIKERRIDE; j <= 5994; j++)
				addTile(j);
			maxc = 0;
			break;
		case BIKERSTAND:
			for (j = BIKERSTAND; j <= 6111; j++)
				addTile(j);
			for (j = 6145; j <= 6249; j++)
				addTile(j);
			maxc = 0;
			break;
		case BIKERRIDEDAISY:
			for (j = BIKERRIDEDAISY; j <= 6484; j++)
				addTile(j);
			for (j = 6558; j <= 6641; j++)
				addTile(j);
			maxc = 0;
			break;
		case DAISYMAE:
		case DAISYMAE + 1:
			for (j = DAISYMAE; j <= 6702; j++)
				addTile(j);
			for (j = 6705; j <= 6916; j++)
				addTile(j);
			for (j = 6920; j <= 6992; j++)
				addTile(j);
			maxc = 0;
			break;
		case BANJOCOOTER:
			for (j = BANJOCOOTER; j <= 7034; j++)
				addTile(j);
			maxc = 0;
			break;
		case GUITARBILLY:
			for (j = GUITARBILLY; j <= 7037; j++)
				addTile(j);
			maxc = 0;
			break;
		case JACKOLOPE:
			for (j = 7280; j <= 7334; j++)
				addTile(j);
			for (j = 7336; j <= 7385; j++)
				addTile(j);
			maxc = 0;
			break;
		case MAMAJACKOLOPE:
			for (j = 8705; j <= 8783; j++)
				addTile(j);
			for (j = 8785; j <= 8792; j++)
				addTile(j);
			for (j = 8795; j <= 8889; j++)
				addTile(j);
			maxc = 0;
			break;
		case MINION:
			for (j = sprite[i].picnum; j < sprite[i].picnum + 141; j++)
				addTile(j);
			for (j = MINJIBA; j <= MINJIBC + 4; j++)
				addTile(j);
			maxc = 0;
			break;
		case 5317: // TCOW:
			maxc = 56;
			break;
		case COOT:
			for (j = COOT; j <= 8783; j++)
				addTile(j);
			for (j = COOTJIBA; j < 5620; j++)
				addTile(j);
			maxc = 0;
			break;
		case ECLAIRHEALTH:
			maxc = 14;
			break;
		case VIXEN:
			maxc = 214;
			break;
		case MOSQUITO:
			maxc = 6;
			break;
		case HEN:
			maxc = 34;
			break;
		case PIG:
		case PIGSTAYPUT:
			maxc = 68;
			break;
		case FORCERIPPLE:
			maxc = 9;
			break;
		case SEENINE:
		case OOZFILTER:
			maxc = 3;
			break;
		case TORNADO:
			maxc = 7;
			break;
		case UFO1:
		case UFO2:
		case UFO3:
		case UFO4:
		case UFO5:
			maxc = 4;
			break;
		}

		for (j = sprite[i].picnum; j < (sprite[i].picnum + maxc); j++)
			addTile(j);
	}

	private void cachegoodsprites() {
		short i;

		// HUD
		addTile(BOTTOMSTATUSBAR);
		if (ud.multimode > 1) {
			addTile(FRAGBAR);
			addTile(KILLSICON);
		}
		for (i = DIGITALNUM; i < DIGITALNUM + 10; i++)
			addTile(i);

		for (i = 3374; i < 3379; i++) // MASK
			addTile(i);

		addTile(ARROW);
		addTile(INVENTORYBOX);
		addTile(HEALTHBOX);
		addTile(AMMOBOX);
		addTile(WHISHKEY_ICON);
		addTile(BOOT_ICON);
		addTile(COWPIE_ICON);
		addTile(SNORKLE_ICON);
		addTile(MOONSHINE_ICON);
		addTile(BEER_ICON);
		addTile(ACCESS_ICON);
		addTile(NEWCROSSHAIR);
		addTile(CROSSHAIR);
		addTile(FRAGBAR - 1);

		for (i = 920; i < 924; i++)
			addTile(i);
		for (i = 930; i < 939; i++)
			addTile(i);

		// FONTS
		for (i = STARTALPHANUM; i < ENDALPHANUM + 1; i++)
			addTile(i);
		for (i = BIGALPHANUM; i < BIGALPHANUM + 82; i++)
			addTile(i);
		for (i = MINIFONT; i < MINIFONT + 63; i++)
			addTile(i);

		// WEAPONS
		for (i = NEWCROWBAR; i < NEWCROWBAR + 8; i++)
			addTile(i);
		for (i = NEWPISTOL; i < NEWPISTOL + 11; i++)
			addTile(i);
		for (i = NEWSHOTGUN; i < NEWSHOTGUN + 9; i++)
			addTile(i);
		for (i = 3370; i < 3373; i++)
			addTile(i);
		for (i = RIFLE; i < RIFLE + 3; i++)
			addTile(i);
		for (i = SHELL; i < SHELL + 2; i++) // Dynamite
			addTile(i);
		for (i = 1752; i < 1757; i++) // Dynamite
			addTile(i);
		for (i = NEWDYNAMITE; i < NEWDYNAMITE + 7; i++)
			addTile(i);
		for (i = CIRCLESTUCK - 5; i < CIRCLESTUCK; i++)
			addTile(i);
		for (i = BUZSAW; i < BUZSAW + 3; i++)
			addTile(i);
		for (i = 3415; i < 3419; i++)
			addTile(i);
		for (i = 3427; i < 2429; i++)
			addTile(i);
		addTile(3438);
		for (i = 3445; i < 3448; i++)
			addTile(i);
		for (i = 3452; i < 3459; i++)
			addTile(i);

		// PICKUPS
		for (i = FIRSTGUNSPRITE; i <= ALIENARMGUN; i++)
			addTile(i);
		for (i = AMMO; i <= BOOTS + 1; i++)
			addTile(i);
		addTile(TEATAMMO);

		for (i = SHOTSPARK1; i <= SHOTSPARK1 + 3; i++)
			addTile(i);
		for (i = FOOTPRINTS; i < FOOTPRINTS + 3; i++)
			addTile(i);
		for (i = BURNING; i < BURNING + 14; i++)
			addTile(i);
		for (i = BURNING2; i < BURNING2 + 14; i++)
			addTile(i);
		for (i = EXPLOSION2; i < EXPLOSION2 + 21; i++)
			addTile(i);
		addTile(BULLETHOLE);
		for (i = JIBS1; i < (JIBS5 + 5); i++)
			addTile(i);
		for (i = JIBS6; i < (JIBS6 + 8); i++)
			addTile(i);
		for (i = SCRAP6; i < (SCRAP1 + 19); i++)
			addTile(i);
		for (i = SMALLSMOKE; i < (SMALLSMOKE + 4); i++)
			addTile(i);
		for (i = BLOOD; i < (BLOOD + 4); i++)
			addTile(i);
	}

}
