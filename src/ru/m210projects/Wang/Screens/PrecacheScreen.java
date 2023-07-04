package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.boardfilename;
import static ru.m210projects.Wang.Game.getMapName;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG2;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.kGameCrash;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R0;
import static ru.m210projects.Wang.Names.CARGIRL_R0;
import static ru.m210projects.Wang.Names.COOLG_RUN_R0;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R0;
import static ru.m210projects.Wang.Names.EEL_RUN_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R0;
import static ru.m210projects.Wang.Names.GORO_RUN_R0;
import static ru.m210projects.Wang.Names.HORNET_RUN_R0;
import static ru.m210projects.Wang.Names.LAVA_RUN_R0;
import static ru.m210projects.Wang.Names.MECHANICGIRL_R0;
import static ru.m210projects.Wang.Names.NINJA_CRAWL_R0;
import static ru.m210projects.Wang.Names.NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.PRUNEGIRL_R0;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R0;
import static ru.m210projects.Wang.Names.SAILORGIRL_R0;
import static ru.m210projects.Wang.Names.SERP_RUN_R0;
import static ru.m210projects.Wang.Names.SKEL_RUN_R0;
import static ru.m210projects.Wang.Names.SKULL_R0;
import static ru.m210projects.Wang.Names.STAT_CEILING_FLOOR_PIC_OVERRIDE;
import static ru.m210projects.Wang.Names.SUMO_RUN_R0;
import static ru.m210projects.Wang.Names.TOILETGIRL_R0;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.WASHGIRL_R0;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Screens.MenuScreen.DrawBackground;
import static ru.m210projects.Wang.Sound.CacheSound;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Main.UserFlag;

public class PrecacheScreen extends PrecacheAdapter {

	private Main app;
	// player weaponry, item usage, etc. Precache every time.
	private short Player_SCTable[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, // weapons
			11, 12, 14, 16, 18, 20, 22, 23, 24, 25, 26, 27, 28, 30, 31, 32, 33, 34, 35, 40, 145, 291, 445, 362, 269,
			158, 476, // underwater
			47, 359, // cloaking
			48, 50, // dead head
			196, 52, 53, 54, 55, // splash & getting items
			56, 57, 58, 73, 74, 410, // bodies hitting ground
			484, 442, // teleport & respawn
			417, 418, // healing
			238, 239, 240, 241, 242, 243, 244, // bring weapons up
			181, 182, 183, 184, 187, 216, // explosions
			272, 273, 274, 275, 486, // nuke associated sounds
			276, 277, 278, 279, 477, // chem bomb
			280, 281, 282, 283, 284, 288, 289, 290, 450, // various player sounds
			295, // armor hit
			312, // sword clank
			324, 325, 209, // unlocking sound
			395, 396, 411, // gibs
			175, // drip
			435, 436, 311, 221, 220, 227, // breakage
			246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, // common player talk
			257, 258, 259, 260, 261, 262, 263, 264, 266, 267, 438, 439, 440, 441, 326, // ancient chinese secret
			330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, // player kill talk
			341, 342, 343, 344, 345, 346, 347, 348, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, // more
																												// asst.
																												// talking
			370, 443 // repair talk
	};

	// Actor specific sound tables. Cache only if the actor appears on the map.
	// Exceptions would include ghosts, which are spawned by coolies, and
	// rippers, which are spawned by the serpent boss.
	private short Coolie_SCTable[] = { 75, 76, 77, 78, 79 };

	private short Ghost_SCTable[] = { 80, 81, 82, 83, 84, 85, 86, 87, 213 };

	private short Ninja_SCTable[] = { 88, 89, 90, 91, 92, 93, 94, 412, 319, // firing sound
			430 // death by sword
	};

	private short Ripper_SCTable[] = { 95, 96, 97, 98, 99, 100, 431 };

	private short Ripper2_SCTable[] = { 313, 314, 315, 316, 317, 318, 431 };

	private short Head_SCTable[] = { 115, 116, 117, 118 // accursed heads
	};

	private short Hornet_SCTable[] = { 119, 120, 121, 122 };

	private short Guardian_SCTable[] = { 101, 102, 103, 104, 105, 106, 107 };

	private short Serpent_SCTable[] = { 123, 124, 125, 126, 127, 128, 129, 130, 131 // serpent boss
	};

	private short Sumo_SCTable[] = { 320, 321, 322, 323 // sumo boss
	};

	private short Bunny_SCTable[] = { 424, 425, 426, 427, 428 };

	private short Toilet_SCTable[] = { 388, 389, 390, 391, 392, 393, 488, 489, 490 // anime girl on toilet
	}; // I suspect some of these are no longer in use

	private short Trash_SCTable[] = { 416 // I heard the trash can was an actor, so here is is
	};

	private short Pachinko_SCTable[] = { 419, 420, 421, 422, 423 };

	public PrecacheScreen(Main app) {
		super(app);
		this.app = app;

		addQueue("Preload player sounds...", new Runnable() {
			@Override
			public void run() {
				PreCacheSoundList(Player_SCTable, Player_SCTable.length);
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
			}
		});

		addQueue("Preload wall tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numwalls; i++) {
					addTile(wall[i].picnum);
					if (wall[i].overpicnum >= 0)
						addTile(wall[i].overpicnum);
				}
				doprecache(0);
			}
		});

		addQueue("Preload things...", new Runnable() {
			@Override
			public void run() {
				// weapons
				PreCacheRange(2000, 2227);
				PreCacheRange(4090, 4093);
				// Explosions
				PreCacheRange(3072, 3225);
				// ninja player character
				PreCacheRange(1024, 1175);
				// console
				PreCacheRange(2380, 2409);
				PreCacheRange(3600, 3645);
				PreCacheRange(2434, 2435);
				// common
				PreCacheRange(204, 208);
				// message font
				PreCacheRange(4608, 4701);
				// gibs
				PreCacheRange(1150, 1568);
				PreCacheRange(1685, 1690);
				PreCacheRange(900, 944);
				PreCacheRange(1670, 1681);
				// blood
				PreCacheRange(1710, 1715);
				PreCacheRange(2410, 2425);
				PreCacheRange(389, 389); // blood puddle by itself in art file
				PreCacheRange(2500, 2503);
				// shrap
				PreCacheRange(3840, 3911);
				PreCacheRange(3924, 3947);
				PreCacheRange(1397, 1398);
				// water *** animated tiles, can be deleted now ***
				// PreCacheRange(780,794);
				// switches
				PreCacheRange(561, 584);
				PreCacheRange(551, 552);
				PreCacheRange(1846, 1847);
				PreCacheRange(1850, 1859);
				// bullet smoke
				PreCacheRange(1748, 1753);
				// small blue font
				PreCacheRange(2930, 3023);
				// gas can
				PreCacheRange(3038, 3042);
				// lava *** animated tiles, can be deleted now ***
				// PreCacheRange(175,182);
				// gas clouds & teleport effect
				PreCacheRange(3240, 3277);
				// nuke mushroom cloud
				PreCacheRange(3280, 3300);
				// blood drops
				PreCacheRange(1718, 1721);
				// smoke
				PreCacheRange(3948, 3968);
				// footprints
				PreCacheRange(2490, 2492);
				// player fists
				PreCacheRange(4070, 4077);
				PreCacheRange(4050, 4051);
				PreCacheRange(4090, 4093);
				// fish actor
				PreCacheRange(3760, 3771);
				PreCacheRange(3780, 3795);
				// coins
				PreCacheRange(2531, 2533);
				// respawn markers & console keys
				PreCacheRange(2440, 2467);
				// light/torch sprites
				PreCacheRange(537, 548);
				PreCacheRange(521, 528);
				PreCacheRange(512, 515);
				PreCacheRange(396, 399);
				PreCacheRange(443, 446);
				// bubbles
				PreCacheRange(716, 720);
				// bullet splashes
				PreCacheRange(772, 776);
				doprecache(1);
			}
		});

		addQueue("Preload actors...", new Runnable() {
			@Override
			public void run() {
				int pic;
				for (int i = 0; i < MAXSPRITES; i++) {
					if (sprite[i].statnum >= MAXSTATUS)
						continue;

					if (pUser[i] != null)
						pic = pUser[i].ID;
					else
						pic = sprite[i].picnum;

					switch (pic) {
					case COOLIE_RUN_R0:
						PreCacheCoolie();
						break;

					case NINJA_RUN_R0:
					case NINJA_CRAWL_R0:
						PreCacheNinja();
						break;

					case GORO_RUN_R0:
						PreCacheGuardian();
						break;

					case 1441:
					case COOLG_RUN_R0:
						PreCacheGhost();
						break;

					case EEL_RUN_R0:
						PreCacheEel();
						break;

					case SUMO_RUN_R0:
						PreCacheZilla();
						break;

					case ZILLA_RUN_R0:
						PreCacheSumo();
						break;

					case TOILETGIRL_R0:
						PreCacheToiletGirl();
						break;

					case WASHGIRL_R0:
						PreCacheWashGirl();
						break;

					case CARGIRL_R0:
						PreCacheCarGirl();
						break;

					case MECHANICGIRL_R0:
						PreCacheMechanicGirl();
						break;

					case SAILORGIRL_R0:
						PreCacheSailorGirl();
						break;

					case PRUNEGIRL_R0:
						PreCachePruneGirl();
						break;

					case TRASHCAN:
						PreCacheTrash();
						break;

					case BUNNY_RUN_R0:
						PreCacheBunny();
						break;

					case RIPPER_RUN_R0:
						PreCacheRipper();
						break;

					case RIPPER2_RUN_R0:
						PreCacheRipper2();
						break;

					case SERP_RUN_R0:
						PreCacheSerpent();
						break;

					case LAVA_RUN_R0:
						break;

					case SKEL_RUN_R0:
						PreCacheSkel();
						break;

					case HORNET_RUN_R0:
						PreCacheHornet();
						break;

					case SKULL_R0:
						PreCacheSkull();
						break;

					case BETTY_R0:
						PreCacheBetty();
						break;

					case GIRLNINJA_RUN_R0:
						PreCacheNinjaGirl();
						break;

					case 623: // Pachinko win light
					case PACHINKO1:
					case PACHINKO2:
					case PACHINKO3:
					case PACHINKO4:
						PreCachePachinko();
						break;
					}
				}

				doprecache(1);
			}
		});

		addQueue("Preload override...", new Runnable() {
			@Override
			public void run() {
				int nexti;
				for (int i = headspritestat[STAT_CEILING_FLOOR_PIC_OVERRIDE]; i != -1; nexti = nextspritestat[i], i = nexti) {
					int tag2 = SPRITE_TAG2(i);
					if (tag2 >= 0 && tag2 <= MAXTILES)
						addTile(tag2);
				}
				doprecache(0);
			}
		});
	}

	@Override
	protected void draw(String title, int index) {
		if (kGameCrash) {
			app.show();
			return;
		}

		engine.clearview(117);
		DrawBackground(engine);
		
		if (mUserFlag != UserFlag.UserMap || boardfilename == null) {
			app.getFont(2).drawText(160, 110, "Entering", -128, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 110 + 16 + 8, getMapName(Level), -128, 0, TextAlign.Center, 2, false);
		} else if (boardfilename != null) {
			app.getFont(2).drawText(160, 110, toCharArray("Entering usermap"), -128, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 110 + 16 + 8, getMapName(Level), -128, 0, TextAlign.Center, 2, false);
		}

		app.getFont(1).drawText(160, 170 + 15, title, -128, 16, TextAlign.Center, 2, false);
	}

	private void PreCacheRange(int start_pic, int end_pic) {
		for (int j = start_pic; j <= end_pic; j++) {
			addTile(j);
		}
	}

	private void PreCacheRipper() {
		PreCacheSoundList(Ripper_SCTable, Ripper_SCTable.length);
		PreCacheRange(1580, 1644);
	}

	private void PreCacheRipper2() {
		PreCacheSoundList(Ripper2_SCTable, Ripper2_SCTable.length);
		PreCacheRange(4320, 4427);
	}

	private void PreCacheCoolie() {
		PreCacheGhost();
		PreCacheSoundList(Coolie_SCTable, Coolie_SCTable.length);
		PreCacheRange(1400, 1440);
		PreCacheRange(4260, 4276); // coolie explode
	}

	private void PreCacheGhost() {
		PreCacheSoundList(Ghost_SCTable, Ghost_SCTable.length);
		PreCacheRange(4277, 4312);
	}

	private void PreCacheSerpent() {
		PreCacheSoundList(Serpent_SCTable, Serpent_SCTable.length);
		PreCacheRange(960, 1016);
		PreCacheRange(1300, 1314);
	}

	private void PreCacheGuardian() {
		PreCacheSoundList(Guardian_SCTable, Guardian_SCTable.length);
		PreCacheRange(1469, 1497);
	}

	private void PreCacheNinja() {
		PreCacheSoundList(Ninja_SCTable, Ninja_SCTable.length);
		PreCacheRange(4096, 4239);
	}

	private void PreCacheNinjaGirl() {
		// PreCacheSoundList(NinjaGirl_SCTable, NinjaGirl_SCTable.length);
		PreCacheRange(5162, 5260);
	}

	private void PreCacheSumo() {
		PreCacheSoundList(Sumo_SCTable, Sumo_SCTable.length);
		PreCacheRange(4490, 4544);
	}

	private void PreCacheZilla() {
		PreCacheSoundList(Sumo_SCTable, Sumo_SCTable.length);
		PreCacheRange(4490, 4544);
	}

	private void PreCacheEel() {
		PreCacheRange(4430, 4479);
	}

	private void PreCacheToiletGirl() {
		PreCacheSoundList(Toilet_SCTable, Toilet_SCTable.length);
		PreCacheRange(5023, 5027);
	}

	private void PreCacheWashGirl() {
		PreCacheSoundList(Toilet_SCTable, Toilet_SCTable.length);
		PreCacheRange(5032, 5035);
	}

	private void PreCacheCarGirl() {
		PreCacheSoundList(Toilet_SCTable, Toilet_SCTable.length);
		PreCacheRange(4594, 4597);
	}

	private void PreCacheMechanicGirl() {
		PreCacheSoundList(Toilet_SCTable, Toilet_SCTable.length);
		PreCacheRange(4590, 4593);
	}

	private void PreCacheSailorGirl() {
		PreCacheSoundList(Toilet_SCTable, Toilet_SCTable.length);
		PreCacheRange(4600, 4602);
	}

	private void PreCachePruneGirl() {
		PreCacheSoundList(Toilet_SCTable, Toilet_SCTable.length);
		PreCacheRange(4604, 4604);
	}

	private void PreCacheTrash() {
		PreCacheSoundList(Trash_SCTable, Trash_SCTable.length);
		PreCacheRange(2540, 2546);
	}

	private void PreCacheBunny() {
		PreCacheSoundList(Bunny_SCTable, Bunny_SCTable.length);
		PreCacheRange(4550, 4584);
	}

	private void PreCacheSkel() {
		PreCacheRange(1320, 1396);
	}

	private void PreCacheHornet() {
		PreCacheSoundList(Hornet_SCTable, Hornet_SCTable.length);
		PreCacheRange(800, 811);
	}

	private void PreCacheSkull() {
		PreCacheSoundList(Head_SCTable, Head_SCTable.length);
		PreCacheRange(820, 854);
	}

	private void PreCacheBetty() {
		PreCacheRange(817, 819);
	}

	private void PreCachePachinko() {
		PreCacheRange(618, 623);
		PreCacheRange(618, 623);
		PreCacheRange(4768, 4790);
		PreCacheRange(4792, 4814);
		PreCacheRange(4816, 4838);
		PreCacheRange(4840, 4863);
		PreCacheSoundList(Pachinko_SCTable, Pachinko_SCTable.length);
	}

	private void PreCacheSoundList(short table[], int num) {
		for (int j = 0; j < num; j++) {
			CacheSound(table[j]);
		}
	}
}
