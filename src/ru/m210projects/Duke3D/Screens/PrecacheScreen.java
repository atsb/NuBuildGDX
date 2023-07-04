package ru.m210projects.Duke3D.Screens;

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
import static ru.m210projects.Duke3D.Actors.badguy;
import static ru.m210projects.Duke3D.Globals.Sound;
import static ru.m210projects.Duke3D.Globals.boardfilename;
import static ru.m210projects.Duke3D.Globals.currentGame;
import static ru.m210projects.Duke3D.Globals.kGameCrash;
import static ru.m210projects.Duke3D.Globals.ud;
import static ru.m210projects.Duke3D.Main.mUserFlag;
import static ru.m210projects.Duke3D.Names.APLAYER;
import static ru.m210projects.Duke3D.Names.ATOMICHEALTH;
import static ru.m210projects.Duke3D.Names.BIGALPHANUM;
import static ru.m210projects.Duke3D.Names.BOSS1;
import static ru.m210projects.Duke3D.Names.BOSS2;
import static ru.m210projects.Duke3D.Names.BOSS3;
import static ru.m210projects.Duke3D.Names.BOTTOMSTATUSBAR;
import static ru.m210projects.Duke3D.Names.BROKEFIREHYDRENT;
import static ru.m210projects.Duke3D.Names.BULLETHOLE;
import static ru.m210projects.Duke3D.Names.BURNING;
import static ru.m210projects.Duke3D.Names.BURNING2;
import static ru.m210projects.Duke3D.Names.CAMERA1;
import static ru.m210projects.Duke3D.Names.COMMANDER;
import static ru.m210projects.Duke3D.Names.COMMANDERSTAYPUT;
import static ru.m210projects.Duke3D.Names.CRACKKNUCKLES;
import static ru.m210projects.Duke3D.Names.DRONE;
import static ru.m210projects.Duke3D.Names.ENDALPHANUM;
import static ru.m210projects.Duke3D.Names.EXPLODINGBARREL;
import static ru.m210projects.Duke3D.Names.EXPLOSION2;
import static ru.m210projects.Duke3D.Names.FEMPIC1;
import static ru.m210projects.Duke3D.Names.FIRSTGUN;
import static ru.m210projects.Duke3D.Names.FIRSTGUNRELOAD;
import static ru.m210projects.Duke3D.Names.FOOTPRINTS;
import static ru.m210projects.Duke3D.Names.FRAGBAR;
import static ru.m210projects.Duke3D.Names.HEADJIB1;
import static ru.m210projects.Duke3D.Names.HORSEONSIDE;
import static ru.m210projects.Duke3D.Names.HYDRENT;
import static ru.m210projects.Duke3D.Names.JIBS1;
import static ru.m210projects.Duke3D.Names.JIBS5;
import static ru.m210projects.Duke3D.Names.LA;
import static ru.m210projects.Duke3D.Names.LEGJIB1;
import static ru.m210projects.Duke3D.Names.LIZMAN;
import static ru.m210projects.Duke3D.Names.LIZMANFEEDING;
import static ru.m210projects.Duke3D.Names.LIZMANHEAD1;
import static ru.m210projects.Duke3D.Names.LIZMANJUMP;
import static ru.m210projects.Duke3D.Names.LIZMANLEG1;
import static ru.m210projects.Duke3D.Names.LIZMANSPITTING;
import static ru.m210projects.Duke3D.Names.LIZTROOP;
import static ru.m210projects.Duke3D.Names.LIZTROOPDUCKING;
import static ru.m210projects.Duke3D.Names.LIZTROOPJETPACK;
import static ru.m210projects.Duke3D.Names.LIZTROOPONTOILET;
import static ru.m210projects.Duke3D.Names.LIZTROOPRUNNING;
import static ru.m210projects.Duke3D.Names.LIZTROOPSHOOT;
import static ru.m210projects.Duke3D.Names.LOADSCREEN;
import static ru.m210projects.Duke3D.Names.MINIFONT;
import static ru.m210projects.Duke3D.Names.NEWBEAST;
import static ru.m210projects.Duke3D.Names.NEWBEASTSTAYPUT;
import static ru.m210projects.Duke3D.Names.NUKEBARREL;
import static ru.m210projects.Duke3D.Names.OCTABRAIN;
import static ru.m210projects.Duke3D.Names.OCTABRAINSTAYPUT;
import static ru.m210projects.Duke3D.Names.OOZFILTER;
import static ru.m210projects.Duke3D.Names.PIGCOP;
import static ru.m210projects.Duke3D.Names.PIGCOPDIVE;
import static ru.m210projects.Duke3D.Names.RECON;
import static ru.m210projects.Duke3D.Names.RUBBERCAN;
import static ru.m210projects.Duke3D.Names.SCRAP1;
import static ru.m210projects.Duke3D.Names.SCREENBREAK6;
import static ru.m210projects.Duke3D.Names.SCREENBREAK7;
import static ru.m210projects.Duke3D.Names.SCREENBREAK8;
import static ru.m210projects.Duke3D.Names.SCREENBREAK9;
import static ru.m210projects.Duke3D.Names.SEENINE;
import static ru.m210projects.Duke3D.Names.SHARK;
import static ru.m210projects.Duke3D.Names.SMALLSMOKE;
import static ru.m210projects.Duke3D.Names.STALL;
import static ru.m210projects.Duke3D.Names.STALLBROKE;
import static ru.m210projects.Duke3D.Names.STARTALPHANUM;
import static ru.m210projects.Duke3D.Names.TECHLIGHT2;
import static ru.m210projects.Duke3D.Names.TECHLIGHT4;
import static ru.m210projects.Duke3D.Names.TOILET;
import static ru.m210projects.Duke3D.Names.TOILETBROKE;
import static ru.m210projects.Duke3D.Names.TOILETWATER;
import static ru.m210projects.Duke3D.Names.VIEWSCREEN;
import static ru.m210projects.Duke3D.Names.WATERTILE2;
import static ru.m210projects.Duke3D.Names.WOODENHORSE;
import static ru.m210projects.Duke3D.Names.W_FORCEFIELD;
import static ru.m210projects.Duke3D.Premap.getsound;
import static ru.m210projects.Duke3D.Sounds.NUM_SOUNDS;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Main.UserFlag;

public class PrecacheScreen extends PrecacheAdapter {

	private final Main app;

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
						if (sector[i].ceilingpicnum == LA)
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
					case TECHLIGHT2:
					case TECHLIGHT4:
						addTile(wall[i].picnum);
						break;
					case SCREENBREAK6:
					case SCREENBREAK7:
					case SCREENBREAK8:
						for (int k = SCREENBREAK6; k < SCREENBREAK9; k++)
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
				for(int i = 0; i < MAXTILES; i++)
				{
					if(!engine.getTile(i).isLoaded())
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

//		engine.clearview(129);
		engine.rotatesprite(320 << 15, 200 << 15, 65536, 0, LOADSCREEN, 0, 0, 2 + 8 + 64, 0, 0, xdim - 1, ydim - 1);

		if(mUserFlag != UserFlag.UserMap || boardfilename == null) {
			app.getFont(2).drawText(160, 90, toCharArray("Entering "),  -128, 0, TextAlign.Center, 2, false);
			if(currentGame.episodes[ud.volume_number] != null && currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number] != null && currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title != null)
				game.getFont(2).drawText(160, 90 + 16 + 8, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title, -128, 0, TextAlign.Center, 2, false);
		}
		else if(boardfilename != null) {
			app.getFont(2).drawText(160, 90, toCharArray("Entering usermap"),  -128, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 90 + 16 + 8, boardfilename,  -128, 0, TextAlign.Center, 2, false);
		}

		app.getFont(1).drawText(160, 90 + 48 + 8, title, -128, 10, TextAlign.Center, 2, false);
	}

	private void precachenecessarysounds() {
		for (int i = 0; i < NUM_SOUNDS; i++)
			if (Sound[i].ptr == null) {
				getsound(i);
			}
	}

	private void cachespritenum(int i) {
		char maxc;
		short j;

		if (ud.monsters_off && badguy(sprite[i]))
			return;

		maxc = 1;

		switch (sprite[i].picnum) {
		case HYDRENT:
			addTile(BROKEFIREHYDRENT);
			for (j = TOILETWATER; j < (TOILETWATER + 4); j++)
				addTile(j);
			break;
		case TOILET:
			addTile(TOILETBROKE);
			for (j = TOILETWATER; j < (TOILETWATER + 4); j++)
				addTile(j);
			break;
		case STALL:
			addTile(STALLBROKE);
			for (j = TOILETWATER; j < (TOILETWATER + 4); j++)
				addTile(j);
			break;
		case RUBBERCAN:
			maxc = 2;
			break;
		case TOILETWATER:
			maxc = 4;
			break;
		case FEMPIC1:
			maxc = 44;
			break;
		case LIZTROOP:
		case LIZTROOPRUNNING:
		case LIZTROOPSHOOT:
		case LIZTROOPJETPACK:
		case LIZTROOPONTOILET:
		case LIZTROOPDUCKING:
			for (j = LIZTROOP; j < (LIZTROOP + 72); j++)
				addTile(j);
			for (j = HEADJIB1; j < LEGJIB1 + 3; j++)
				addTile(j);
			maxc = 0;
			break;
		case WOODENHORSE:
			maxc = 5;
			for (j = HORSEONSIDE; j < (HORSEONSIDE + 4); j++)
				addTile(j);
			break;
		case NEWBEAST:
		case NEWBEASTSTAYPUT:
			maxc = 90;
			break;
		case BOSS1:
		case BOSS2:
		case BOSS3:
			maxc = 30;
			break;
		case OCTABRAIN:
		case OCTABRAINSTAYPUT:
		case COMMANDER:
		case COMMANDERSTAYPUT:
			maxc = 38;
			break;
		case RECON:
			maxc = 13;
			break;
		case PIGCOP:
		case PIGCOPDIVE:
			maxc = 61;
			break;
		case SHARK:
			maxc = 30;
			break;
		case LIZMAN:
		case LIZMANSPITTING:
		case LIZMANFEEDING:
		case LIZMANJUMP:
			for (j = LIZMANHEAD1; j < LIZMANLEG1 + 3; j++)
				addTile(j);
			maxc = 80;
			break;
		case APLAYER:
			maxc = 0;
			if (ud.multimode > 1) {
				maxc = 5;
				for (j = 1420; j < 1420 + 106; j++)
					addTile(j);
			}
			break;
		case ATOMICHEALTH:
			maxc = 14;
			break;
		case DRONE:
			maxc = 10;
			break;
		case EXPLODINGBARREL:
		case SEENINE:
		case OOZFILTER:
			maxc = 3;
			break;
		case NUKEBARREL:
		case CAMERA1:
			maxc = 5;
			break;
		}

		for (j = sprite[i].picnum; j < (sprite[i].picnum + maxc); j++)
			addTile(j);
	}

	private void cachegoodsprites() {
		short i;

		if (ud.screen_size >= 2) {
			addTile(BOTTOMSTATUSBAR);
			if (ud.multimode > 1) {
				addTile(FRAGBAR);
				for (i = MINIFONT; i < MINIFONT + 63; i++)
					addTile(i);
			}
		}

		addTile(VIEWSCREEN);

		for (i = STARTALPHANUM; i < ENDALPHANUM + 1; i++)
			addTile(i);

		for (i = FOOTPRINTS; i < FOOTPRINTS + 3; i++)
			addTile(i);

		for (i = BIGALPHANUM; i < BIGALPHANUM + 82; i++)
			addTile(i);

		for (i = BURNING; i < BURNING + 14; i++)
			addTile(i);

		for (i = BURNING2; i < BURNING2 + 14; i++)
			addTile(i);

		for (i = CRACKKNUCKLES; i < CRACKKNUCKLES + 4; i++)
			addTile(i);

		for (i = FIRSTGUN; i < FIRSTGUN + 3; i++)
			addTile(i);

		for (i = EXPLOSION2; i < EXPLOSION2 + 21; i++)
			addTile(i);

		addTile(BULLETHOLE);

		for (i = FIRSTGUNRELOAD; i < FIRSTGUNRELOAD + 8; i++)
			addTile(i);

		addTile(FOOTPRINTS);

		for (i = JIBS1; i < (JIBS5 + 5); i++)
			addTile(i);

		for (i = SCRAP1; i < (SCRAP1 + 19); i++)
			addTile(i);

		for (i = SMALLSMOKE; i < (SMALLSMOKE + 4); i++)
			addTile(i);
	}

}
