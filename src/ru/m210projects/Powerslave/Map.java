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

package ru.m210projects.Powerslave;

import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Sector.*;
import static ru.m210projects.Powerslave.Switch.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Snake.*;
import static ru.m210projects.Powerslave.Grenade.*;
import static ru.m210projects.Powerslave.Slide.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Energy.*;
import static ru.m210projects.Powerslave.SpiritHead.*;
import static ru.m210projects.Powerslave.Enemies.Spider.*;
import static ru.m210projects.Powerslave.Enemies.Anubis.*;
import static ru.m210projects.Powerslave.Enemies.Mummy.*;
import static ru.m210projects.Powerslave.Enemies.Fish.*;
import static ru.m210projects.Powerslave.Enemies.Rat.*;
import static ru.m210projects.Powerslave.Enemies.Lion.*;
import static ru.m210projects.Powerslave.Enemies.Roach.*;
import static ru.m210projects.Powerslave.Enemies.Wasp.*;
import static ru.m210projects.Powerslave.Enemies.Scorp.*;
import static ru.m210projects.Powerslave.Enemies.Rex.*;
import static ru.m210projects.Powerslave.Enemies.LavaDude.*;
import static ru.m210projects.Powerslave.Enemies.Set.*;
import static ru.m210projects.Powerslave.Enemies.Queen.*;
import static ru.m210projects.Powerslave.Enemies.Ra.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Powerslave.LoadSave.gAutosaveRequest;
import static ru.m210projects.Powerslave.LoadSave.gClassicMode;
import static ru.m210projects.Powerslave.Cinema.*;
import static ru.m210projects.Build.Engine.*;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Type.BobStruct;
import ru.m210projects.Powerslave.Type.BubbleMachineStruct;
import ru.m210projects.Powerslave.Type.TrailPointStruct;
import ru.m210projects.Powerslave.Type.TrailStruct;
import ru.m210projects.Powerslave.Type.TrapStruct;

public class Map {

	public static boolean bMapCrash;
	public static int nPushBlocks;
	public static int nRegenerates;
	public static int nFirstRegenerate;

	public static int nSwitchSound;
	public static int nStoneSound;
	public static int nElevSound;
	public static int nStopSound;
	public static int ldMapZoom;
	public static int lMapZoom;

	public static int nTraps;
	public static TrapStruct sTrap[] = new TrapStruct[40];
	public static short[] nTrapInterval = new short[40];

	private static Runnable showCredits = new Runnable() {
		@Override
		public void run() {
			GoToTheCinema(9, game.rMenu);
			nPlayerLives[0] = 0;
		}
	};

	public static Runnable changeMap = new Runnable() {
		@Override
		public void run() {
			gAutosaveRequest = true;
			gGameScreen.changemap(levelnew, null);
		}
	};

	private static Runnable toNextMap = new Runnable() {
		@Override
		public void run() {
			levelnew = levelnum + 1;
			checkNextMap();
		}
	};

	public static void checkNextMap() {
		if(gCurrentEpisode == gOriginalEpisode) {
			if (nNetPlayerCount == 0 && !bPlayback && levelnew > 0 && levelnew <= 20) {
				if (levelnum > nBestLevel)
					nBestLevel = levelnum;
				gMap.showMap(levelnum, levelnew, nBestLevel);
			}
		} else if(mUserFlag != UserFlag.UserMap && levelnew > 0) {
			if(levelnew <= gCurrentEpisode.maps()) {
				if (levelnum > nBestLevel)
					nBestLevel = levelnum;
				if(levelnew > 1 || gClassicMode)
					gAutosaveRequest = true;
				gGameScreen.changemap(levelnew, null);
			} else {
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						showCredits.run();
					}
				});
			}
		}
	}

	public static void FinishLevel() {
		bCamera = false;
		bMapMode = false;
		StopAllSounds();
		if (gCurrentEpisode != gOriginalEpisode || levelnum != 20) {
			System.err.println("Map.java 139: PlayLocalSound(StaticSound[59], 0)");
			SetLocalChan(1);
			PlayLocalSound(StaticSound[59], 0);
			SetLocalChan(0);
		}

		if(mUserFlag == UserFlag.UserMap || gCurrentEpisode == gTrainingEpisode) {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					nPlayerLives[0] = 0;
					game.EndGame();
				}
			});
		} else {
			if (gCurrentEpisode != gTrainingEpisode) {
				if(gCurrentEpisode == gOriginalEpisode) {
					if(!DoAfterCinemaScene(levelnum, levelnum == 20 ? showCredits : toNextMap))
						levelnew = levelnum + 1;
				} else levelnew = levelnum + 1;
			}
		}
	}

	public static void UpdateMap() {
		if (sector[initsect].ceilingpal != 3 || nPlayerTorch[nLocalPlayer] != 0)
			MarkSectorSeen(initsect);
	}

	private static void MarkSectorSeen(int sect) {
		if (((1 << (sect & 7)) & show2dsector[sect >> 3]) == 0) {
			show2dsector[sect >> 3] |= 1 << (sect & 7);

			int startwall = sector[sect].wallptr;
			int endwall = startwall + sector[sect].wallnum;

			for (int j = startwall; j < endwall; j++)
				show2dwall[j >> 3] |= (1 << (j & 7));
		}
	}

	public static void GrabMap()
	{
		for(int i = 0; i < numsectors; i++)
			MarkSectorSeen(i);
	}

	public static boolean LoadLevel(String name, int num) {
		BuildPos out = null;
		try {
			out = engine.loadboard(name);
		} catch (FileNotFoundException | InvalidVersionException | RuntimeException e) {
			game.GameMessage(e.getMessage());
			return false;
		}

		if (num == 20) {
			lCountDown = 81000;
			nAlarmTicks = 30;
			nRedTicks = 0;
			nClockVal = 0;
			nEnergyTowers = 0;
		}

		nCreaturesLeft = 0;
		nCreaturesMax = 0;
		nFreeze = 0;
		nSpiritSprite = -1;
		InitLion();
		InitRexs();
		InitSets();
		InitQueens();
		InitRoach();
		InitWasps();
		InitRats();
		InitBullets();
		InitWeapons();
		InitGrenades();
		InitAnims();
		InitSnakes();
		InitFishes();
		InitLights();
		InitMap();
		InitBubbles();
		InitObjects();
		InitLava();
		nPushBlocks = 0;
		InitAnubis();
		InitSpider();
		InitMummy();
		InitScorp();
		InitPlayer();
		nRegenerates = 0;
		nFirstRegenerate = -1;
		nMagicCount = 0;
//		InitInput();
		if (num == 20)
			InitEnergyTile();

		if (num <= 15) {
			nSwitchSound = 33;
			nStoneSound = 23;
			nElevSound = 23;
			nStopSound = 66;
		} else {
			nSwitchSound = 35;
			nStoneSound = 23;
			nElevSound = 51;
			nStopSound = 35;
		}

		initx = out.x;
		inity = out.y;
		initz = out.z;
		inita = out.ang;
		initsect = out.sectnum;

		pskyoff[0] = 0;
		pskyoff[1] = 0;
		pskyoff[2] = 0;
		pskyoff[3] = 0;
		parallaxtype = 0;
		visibility = 1024; //2048;
		parallaxyoffs = 256;
		flash = 0;
		pskybits = 2;
		// precache()
		LoadObjects();
		levelnum = num;
//		LoadSong("lev" + num + ".xmi");

		return true;
	}

	public static void LoadObjects() {
		InitRun();
		InitChan();
		LinkCount = 1024; // InitLink();
		InitPoint();
		InitSlide();
		InitSwitch();
		InitElev();
		InitWallFace();
		InitSectFlag();

		for (short i = 0; i < numsectors; ++i) {
			short hitag = sector[i].hitag;
			short lotag = sector[i].lotag;
			sector[i].hitag = 0;
			sector[i].lotag = 0;
			sector[i].extra = -1;
			if (hitag != 0 || lotag != 0) {
				sector[i].lotag = (short) (HeadRun() + 1);
				sector[i].hitag = lotag;
				ProcessSectorTag(i, lotag, hitag);
			}
		}

		for (short j = 0; j < numwalls; ++j) {
			wall[j].extra = -1;
			short lotag = wall[j].lotag;
			wall[j].lotag = 0;
			if (wall[j].hitag != 0 || lotag != 0) {
				wall[j].lotag = (short) (HeadRun() + 1);
				ProcessWallTag(j, lotag, wall[j].hitag);
			}
		}
		ExamineSprites();
		PostProcess();
		InitRa();
		InitChunks();

		for (short i = 0; i < 4096; ++i) {
			ChangeChannel(i, 0);
			ReadyChannel(i);
		}

		nCamerax = initx;
		nCameray = inity;
		nCameraz = initz;
	}

	public static void PostProcess() {
		int v0 = 0;
		int v1 = 0;
		int v2 = 0;
		while (true) {
			if (v1 >= nMoveSects) {
				int v9 = 0;
				int v42 = 0;
				int v36 = 0;

				while (v9 < nBobs) {
					if (sBob[v42].field_3 == 0) {
						int v10 = 0;
						int v11 = 0;
						int v45 = sBobID[v36];
						while (v10 < nBobs) {
							if (v10 != v9 && sBob[v42].field_3 != 0 && v45 == sBobID[v11])
								SnapSectors(v9, v10, 0);
							++v11;
							++v10;
						}
					}
					++v9;
					++v42;
					++v36;
				}
				if (levelnew == 20) {
					int v12 = 0;
					int v13 = 0;
					int v43 = 0;
					while (v12 < numsectors) {
						short v14 = sector[v13].wallptr;
						short v15 = sector[v13].wallnum;
						SectSoundSect[v43] = (short) v12;
						short v16 = v14;
						int v17 = v14 + v15;

						SectSound[v43] = (short) StaticSound[62];
						while (v16 < v17) {
							if (wall[v16].picnum == 3603) {
								wall[v16].pal = 1;
								int i = engine.insertsprite((short) v12, (short) 407);
								sprite[i].cstat = (short) 32768;
							}
							++v16;
							++v14;
						}
						++v13;
						++v12;
						++v43;
					}
					int v19 = 0;
					int v20 = 0;
					do {
						if (sprite[v20].statnum < 1024 && sprite[v20].picnum == 3603) {
							engine.changespritestat((short) v19, (short) 407);
							sprite[v20].pal = 1;
						}
						++v19;
						++v20;
					} while (v19 < 4096);
				} else {
					int v21 = 0;
					int v40 = 0;
					int v37 = 0;
					int v35 = 0;
					while (v21 < numsectors) {
						int v44 = 30000;
						if (SectSpeed[v37] != 0 && SectDepth[v40] != 0 && (SectFlag[v37] & 0x4000) == 0) {
							SectSoundSect[v37] = (short) v21;
							SectSound[v37] = (short) StaticSound[43];
						} else {
							int v22 = 0;
							int v23 = 0;
							int v39 = 0;
							while (v22 < numsectors) {
								if (v21 != v22 && SectSpeed[v23] != 0 && (SectFlag[v37] & 0x4000) == 0) {
									short v24 = sector[v35].wallptr;
									short v25 = sector[v39].wallptr;
									int v41 = wall[v24].x - wall[v25].x;
									int v26 = v41;
									if (v41 < 0)
										v26 = -v41;
									int v38 = v26;
									int v27 = wall[v24].y - wall[v25].y;
									if (v27 < 0)
										v27 = -v27;
									if (v38 < 15000 && v27 < 15000) {
										int v28 = v38 + v27;
										if (v28 < v44) {
											int v29 = StaticSound[43];
											v44 = v28;
											SectSoundSect[v37] = (short) v22;
											SectSound[v37] = (short) v29;
										}
									}
								}
								++v23;
								++v22;
								++v39;
							}
						}
						++v21;
						++v40;
						++v37;
						++v35;
					}
				}

				short v30 = 0;
				int v31 = 0;
				while (true) {
					if (v30 >= ObjectCount)
						return;
					if (sprite[ObjectList[v31].field_6].statnum == 152) {
						if (ObjectList[v31].obj_A != 0) {
							int v32 = ObjectList[v31].obj_A;
							short v33 = 0;
							int v34 = 0;
							ObjectList[v31].obj_A = -1;
							while (true) {
								if (v33 >= ObjectCount)
									break;
								if (v33 != v30 && sprite[ObjectList[v34].field_6].statnum == 152
										&& v32 == ObjectList[v34].obj_A) {
									ObjectList[v31].obj_A = v33;
									ObjectList[v34].obj_A = v30;
									break;
								}
								++v34;
								++v33;
							}

						} else {
							ObjectList[v31].obj_A = -1;
						}
					}
					++v31;
					++v30;
				}
			}
			sMoveSect[v0].field_4 = sTrail[sMoveSect[v0].field_2].field_0;
			if ((sMoveSect[v0].field_6 & 0x40) != 0)
				ChangeChannel(sMoveSect[v0].field_E, 1);
			int v3 = sMoveSect[v0].field_0;
			if ((SectFlag[v3] & 0x2000) != 0) {
				SECTOR v4 = sector[v3];
				v4.ceilingstat |= 0x40;
				int v5 = 0;
				v4.floorstat &= ~0x40;
				int v6 = 0;
				while (true) {
					if (v5 >= nMoveSects)
						break;
					if (v1 != v5 && sMoveSect[v0].field_2 == sMoveSect[v6].field_2) {
						int v7 = sMoveSect[v0].field_0;
						sMoveSect[v6].field_8 = sMoveSect[v0].field_0;
						SnapSectors(sMoveSect[v6].field_0, v7, 0);
						sMoveSect[v2].field_0 = (short) v5;
						break;
					}
					++v6;
					++v5;
				}

			}
			++v2;
			++v1;
			++v0;
		}
	}

	public static void InitSectFlag() {
		for (int i = 0; i < 1024; i++) {
			SectSoundSect[i] = -1;
			SectSound[i] = -1;
			SectAbove[i] = -1;
			SectBelow[i] = -1;
			SectDepth[i] = 0;
			SectFlag[i] = 0;
			SectSpeed[i] = 0;
			SectDamage[i] = 0;
		}
	}

	public static void InitObjects() {
		ObjectCount = 0;
		nTraps = 0;
		nDrips = 0;
		nBobs = 0;
		nTrails = 0;
		nTrailPoints = 0;
		nMoveSects = 0;

		nEnergyBlocks = 0;
		nDronePitch = 0;
		nFinaleStage = 0;
		lFinaleStart = 0;
		nSmokeSparks = 0;

		for (int i = 0; i < 40; i++) {
			if (sTrail[i] == null)
				sTrail[i] = new TrailStruct();
			sTrail[i].clear();
		}
	}

	public static void InitMap() {
		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);
		ldMapZoom = 64;
		lMapZoom = 1000;
	}

	public static void InitPlayer() {
		for (int i = 0; i < 8; i++) {
			PlayerList[i].spriteId = -1;
			PlayerList[i].crouch_toggle = false;
		}
	}

	public static void SnapBobs(int a1, int a2) {
		int v3 = -1;
		int v6 = -1;
		int v8;
		for (int v4 = 0; v4 < nBobs; v4++) {
			int v7 = sBob[v4].field_0;
			if (v7 == a1) {
				v8 = v3;
				v6 = v4;
			} else {
				if (a2 != v7)
					continue;
				v8 = v6;
				v3 = v4;
			}
			if (v8 != -1)
				break;
		}
		if (v3 <= -1 && v6 <= -1)
			return;

		sBob[v3].field_2 = sBob[v6].field_2;
	}

	public static void BuildBubbleMachine(int a1) {
		if (nMachineCount >= 125) {
			game.ThrowError("too many bubble machines in level " + levelnew);
			return;
		}
		if (Machine[nMachineCount] == null)
			Machine[nMachineCount] = new BubbleMachineStruct();

		BubbleMachineStruct v2 = Machine[nMachineCount];
		v2.field_4 = 75;
		v2.field_2 = (short) a1;
		v2.field_0 = v2.field_4;
		++nMachineCount;
		sprite[a1].cstat = (short) 32768;
	}

	public static void AddSectorBob(int a1, int a2, int a3) {
		int v3 = a1;
		int v10 = a2;
		if (nBobs >= 200) {
			game.ThrowError("Too many bobs!");
			return;
		}

		if (sBob[nBobs] == null)
			sBob[nBobs] = new BobStruct();

		sBob[nBobs].field_3 = (byte) a3;
		int nSector;
		if (a3 != 0)
			nSector = sector[a1].ceilingz;
		else nSector = sector[a1].floorz;
		sBob[nBobs].field_4 = nSector;
		sBob[nBobs].field_2 = (byte) (16 * v10);
		sBobID[nBobs] = (short) v10;
		sBob[nBobs].field_0 = (short) v3;
		SectFlag[v3] |= 0x1000;
		nBobs++;
	}

	public static void ProcessWallTag(short wallnum, int nLotag, int nHitag) {
		int real_chan = AllocChannel(nHitag % 1000);
		if (real_chan < 0 || real_chan >= 4096) {
			game.ThrowError("real_chan>=0 && real_chan <MAXCHAN");
			return;
		}

		int nVelocity = nLotag / 1000;
		if (nVelocity == 0)
			nVelocity = 1;

		switch (nLotag % 1000) {
		case 1:
			AddRunRec(channel[real_chan].head,
					BuildWallFace(real_chan, wallnum, 2, wall[wallnum].picnum, wall[wallnum].picnum + 1));
			AddRunRec(channel[real_chan].head, BuildSwPressWall(real_chan, BuildLink(2, 1, 0), wallnum));
			return;
		case 6:
			AddRunRec(channel[real_chan].head, BuildSwPressWall(real_chan, BuildLink(2, 1, 0), wallnum));
			return;
		case 7:
			AddRunRec(channel[real_chan].head,
					BuildWallFace(real_chan, wallnum, 2, wall[wallnum].picnum, wall[wallnum].picnum + 1));
			AddRunRec(channel[real_chan].head, BuildSwPressWall(real_chan, BuildLink(1, 1), wallnum));
			return;
		case 8:
			AddRunRec(channel[real_chan].head,
					BuildWallFace(real_chan, wallnum, 2, wall[wallnum].picnum, wall[wallnum].picnum + 1));
			AddRunRec(channel[real_chan].head, BuildSwPressWall(real_chan, BuildLink(2, -1, 0), wallnum));
			return;
		case 9:
			AddRunRec(channel[real_chan].head, BuildSwPressWall(real_chan, BuildLink(2, 1, 1), wallnum));
			return;
		case 10:
			AddRunRec(channel[real_chan].head, BuildSwPressWall(real_chan, BuildLink(2, -1, 0), wallnum));
			return;
		case 12:
		case 14:
		case 16:
		case 19:
		case 20:
			int w1 = 0, w2 = 0;
			for (int i = wall[wallnum].point2; i != wallnum; i = wall[i].point2) {
				w2 = w1;
				w1 = i;
			}
			AddRunRec(channel[real_chan].head, BuildSlide(real_chan, wallnum, w1, w2, wall[wallnum].point2,
					wall[wall[wallnum].point2].point2, wall[wall[wall[wallnum].point2].point2].point2));
			return;
			case 24:
			AddFlow(wallnum, 4 * nVelocity, 3);
			return;
		case 25:
			AddFlow(wallnum, 4 * nVelocity, 2);
			return;
		}
	}

	private static int[] word_14032 = { 938, 937, 57, 56, 55, 54, 52, 51, 50, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38,
			37, 36, 35, 34, 33, 32, 29, 28, 27, 19, 18, 17, 16, 15, 14, 10, 9, 2, 1, 0 };

	public static void ProcessTrailSprite(short spritenum, int a2, int a3) {
		if (nTrailPoints >= 100) {
			game.ThrowError("Too many trail point sprites (900-949)... increase MAX_TRAILPOINT");
			return;
		}

		int p = nTrailPoints;
		if (sTrailPoint[p] == null)
			sTrailPoint[p] = new TrailPointStruct();

		sTrailPoint[p].x = sprite[spritenum].x;
		sTrailPoint[p].y = sprite[spritenum].y;
		nTrailPoints++;

		int v8 = FindTrail(a3);
		int v9 = sTrail[v8].field_0;
		nTrailPointVal[p] = (short) ((a2 + 124) & 0xFF);
		if (v9 == -1) {
			sTrail[v8].field_0 = (short) p;
			sTrail[v8].field_4 = (short) p;
			nTrailPointNext[p] = -1;
			nTrailPointPrev[p] = -1;
		} else {
			int v10 = -1;
			int v11 = v9;
			while (true) {
				if (v9 == -1)
					break;

				v11 = v9;
				if (nTrailPointVal[v9] > a2 - 900) {
					nTrailPointPrev[p] = nTrailPointPrev[v11];
					nTrailPointPrev[v11] = (short) p;
					nTrailPointNext[p] = (short) v9;
					if (v9 == sTrail[v8].field_0)
						sTrail[v8].field_0 = (short) p;
					break;
				}

				v10 = v9;
				v9 = nTrailPointNext[v9];
			}

			if (v9 == -1) {
				nTrailPointNext[v10] = (short) p;
				nTrailPointPrev[p] = (short) v10;
				nTrailPointNext[p] = -1;
				sTrail[v8].field_4 = (short) p;
			}
		}

		engine.deletesprite(spritenum);
	}

	public static void BuildItemAnim(int a1) {
		int v1 = a1;
		int v2 = a1;
		int result = a1;
		int v4 = (sprite[result].statnum - 906);
		int v5 = v4;

		if (nItemAnimInfo[v4].field_0 < 0) {
			short v9 = nItemAnimInfo[v5].field_2;
			sprite[result].owner = -1;
			sprite[result].yrepeat = v9;
			sprite[result].xrepeat = v9;
		} else {
			int v10 = BuildAnim(v2, 41, nItemAnimInfo[v5].field_0, sprite[result].x, sprite[result].y, sprite[result].z,
					sprite[result].sectnum, nItemAnimInfo[v5].field_2, 20);
			short v6 = (short) GetAnimSprite(v10);
			if (v4 == 44)
				sprite[v6].cstat |= 2;
			int v7 = v1;
			int v8 = v6;
			engine.changespritestat(v6, sprite[v7].statnum);
			result = v8;
			sprite[result].owner = (short) v10;
			sprite[result].hitag = sprite[v7].hitag;
		}

	}

	public static void SetBelow(int a1, short a2) {
		SectBelow[a1] = a2;
	}

	public static void SetAbove(int a1, short a2) {
		SectAbove[a1] = a2;
	}

	public static void ProcessSpriteTag(short spritenum, int a2, short a3) {
		short nSprite = spritenum;
		int nStatnum = a2;

		if((sprite[nSprite].cstat & 48) == 48)
			sprite[nSprite].cstat &= ~48;

		int ch = AllocChannel(a3 % 1000);
		if (nStatnum < 6 || nStatnum > 60) {
			if (nStatnum >= 900 && nStatnum <= 949) {
				ProcessTrailSprite(nSprite, nStatnum, a3);
				return;
			}
			int nVelocity = nStatnum / 1000;
			if (nVelocity == 0)
				nVelocity = 1;

			int v7 = nStatnum % 1000;
			if (!bNoCreatures || v7 < 100 || v7 > 118) {
				if ((v7 - 61) > 0x3AA) {
					engine.mydeletesprite(nSprite);
					return;
				}

				int v8 = 39;
				for (int v9 = 0; v8 >= 0 && v9 < word_14032.length; v8--) {
					if (word_14032[v9++] == v7 - 61)
						break;
				}

				switch (v8) {
				case 0:
					engine.mydeletesprite(nSprite);
					return;
				case 1:
					nSpiritSprite = nSprite;
					sprite[nSprite].cstat |= 0x8000;
					return;
				case 2:
					nNetStartSprite[nNetStartSprites] = nSprite;
					sprite[nSprite].cstat = (short) 32768;
					++nNetStartSprites;
					return;
				case 3:
					engine.changespritestat(nSprite, (short) 405);
					sprite[nSprite].cstat = (short) 32768;
					return;
				case 4:
					BuildDrip(nSprite);
					return;
				case 5:
					AddRunRec(channel[ch].head, BuildFireBall(nSprite, a3, nVelocity));
					return;
				case 6:
					BuildObject(nSprite, 1, a3);
					return;
				case 7:
					BuildObject(nSprite, 0, a3);
					return;
				case 8:
					AddRunRec(channel[ch].head, BuildArrow(nSprite, nVelocity));
					return;
				case 9:
					AddFlow(nSprite, nVelocity, 1);
					SectFlag[sprite[nSprite].sectnum] |= 800;
					engine.mydeletesprite(nSprite);
					return;
				case 10:
				case 13:
					SectSpeed[sprite[nSprite].sectnum] = (short) nVelocity;
					SectFlag[sprite[nSprite].sectnum] = (short) (sprite[nSprite].ang
							| SectFlag[sprite[nSprite].sectnum]);
					engine.mydeletesprite(nSprite);
					return;
				case 11:
					SectFlag[sprite[nSprite].sectnum] |= 0x2000;
					engine.mydeletesprite(nSprite);
					return;
				case 12:
					AddFlow(nSprite, nVelocity, 0);
					break;
				case 14:
					BuildObject(nSprite, 3, a3);
					return;
				case 15:
					BuildBubbleMachine(nSprite);
					return;
				case 16:
					SectDepth[sprite[nSprite].sectnum] = (short) (a3 << 8);
					break;
				case 17:
					AddSectorBob(sprite[nSprite].sectnum, a3, 0);
					break;
				case 18:
					short v20 = (short) (a3 >> 2);
					if (v20 == 0)
						v20 = 1;

					SectFlag[sprite[nSprite].sectnum] |= 0x4000;
					SectDamage[sprite[nSprite].sectnum] = v20;
					engine.mydeletesprite(nSprite);
					return;
				case 19:
					AddSectorBob(sprite[nSprite].sectnum, a3, 1);
					break;
				case 20:
					SetBelow(sprite[nSprite].sectnum, a3);
					SnapSectors(sprite[nSprite].sectnum, a3, 1);
					break;
				case 21:
					SetAbove(sprite[nSprite].sectnum, a3);
					SectFlag[sprite[nSprite].sectnum] |= 0x2000;
					break;
				case 22:
					if (bNoCreatures)
						break;
					BuildAnubis(nSprite, 0, 0, 0, 0, 0, 0);
					return;
				case 23:
					if (bNoCreatures)
						break;
					BuildMummy(nSprite, 0, 0, 0, 0, 0);
					return;
				case 24:
					if (bNoCreatures)
						break;
					BuildLion(nSprite, 0, 0, 0, 0, 0);
					return;
				case 25:
					if (bNoCreatures)
						break;
					BuildRoach(0, nSprite, 0, 0, 0, 0, 0);
					return;
				case 26:
					if (bNoCreatures)
						break;
					BuildRoach(1, nSprite, 0, 0, 0, 0, 0);
					return;
				case 27:
					if (bNoCreatures)
						break;
					BuildSpider(nSprite, 0, 0, 0, 0, 0);
					return;
				case 28:
					if (bNoCreatures)
						break;
					BuildFish(nSprite, 0, 0, 0, 0, 0);
					return;
				case 29:
					if (bNoCreatures)
						break;
					BuildRex(nSprite, 0, 0, 0, 0, 0, ch);
					return;
				case 30:
					if (bNoCreatures)
						break;
					BuildLava(nSprite, 0, 0, 0, 0, 0, ch);
					return;
				case 31:
					if (bNoCreatures)
						break;
					BuildSet(nSprite, 0, 0, 0, 0, 0, ch);
					return;
				case 32:
					BuildScorp(nSprite, 0, 0, 0, 0, 0, ch);
					return;
				case 33:
					BuildQueen(nSprite, 0, 0, 0, 0, 0, ch);
					return;
				case 34:
					BuildRat(nSprite, 0, 0, 0, 0, 0);
					return;
				case 35:
					BuildRat(nSprite, 0, 0, 0, 0, -1);
					return;
				case 36:
					if (bNoCreatures)
						break;
					BuildWasp(nSprite, 0, 0, 0, 0, 0);
					return;
				case 37:
					if (bNoCreatures)
						break;
					BuildAnubis(nSprite, 0, 0, 0, 0, 0, 1);
					return;
				case 38:
					AddGlow(sprite[nSprite].sectnum, nVelocity);
					break;
				case 39:
					AddFlicker(sprite[nSprite].sectnum, nVelocity);
					break;
				}
			}
		} else {
			if (nStatnum < 16) {
				if (nStatnum < 12) {
					if (nStatnum == 8)
						a3 = (short) (3 * (a3 / 3));
				} else if (nStatnum <= 12) {
					a3 = 40;
				} else if (nStatnum <= 13) {
					a3 = 160;
				} else if (nStatnum == 14) {
					a3 = -200;
				}

				sprite[nSprite].hitag = a3;
				engine.changespritestat(nSprite, (short) (nStatnum + 900));
				sprite[nSprite].cstat &= ~0x101;
				BuildItemAnim(nSprite);
				return;
			}

			if (nStatnum > 16) {
				if(!gClassicMode)
				{
					switch(nStatnum) {
						case 25: //extra life
							int index = -1;
							while(true) {
								index = (int) (7 + (44 * Math.random())); //(7 - 50)
								if(changeitem(nSprite, index))
									break;
							}
							nStatnum = index;

							engine.changespritestat(nSprite, (short) (nStatnum + 900));
							sprite[nSprite].cstat &= ~0x101;
							BuildItemAnim(nSprite);
							return;
						case 59: //checkpoint
						engine.mydeletesprite(nSprite);
						return;
					}
				}

				if (nStatnum == 27) {
					sprite[nSprite].hitag = 1;
					engine.changespritestat(nSprite, (short) 909);
					sprite[nSprite].cstat &= ~0x101;
					BuildItemAnim(nSprite);
					return;
				}

				if ((nStatnum == 25 || nStatnum == 59) && nNetPlayerCount != 0) {
					engine.mydeletesprite(nSprite);
					return;
				}

				sprite[nSprite].hitag = a3;
				engine.changespritestat(nSprite, (short) (nStatnum + 900));
				sprite[nSprite].cstat &= ~0x101;
				BuildItemAnim(nSprite);
				return;
			}
		}

		engine.mydeletesprite(nSprite);
	}

	public static boolean changeitem(int spr, int index)
	{
		switch(index) {
		case 11: //the map
		case 18: //heart
		case 19: //scarabey
		case 21: //invisibility
		case 22: //torch
		case 37: //cobra
		case 38: //raw energy
		case 49: //magic
		case 13: //health
		case 14: //venom
			sprite[spr].hitag = 1;
			return true;
		case 50: //soul
			sprite[spr].hitag = 10;
			return true;
		case 6: //speed loader
			sprite[spr].hitag = 6;
			sprite[spr].picnum = 877;
			return true;
		case 7: //fuel
			sprite[spr].hitag = 25;
			sprite[spr].picnum = 879;
			return true;
		case 8: //m60 ammo
			sprite[spr].hitag = 50;
			sprite[spr].picnum = 882;
			return true;
		case 27: //grenade
			sprite[spr].hitag = 1;
			sprite[spr].picnum = 878;
			return true;
		case 20: //increased weapon power
			sprite[spr].hitag = 1;
			sprite[spr].picnum = 752;
			return true;
		case 23: //sobek mask
			sprite[spr].hitag = 1;
			sprite[spr].picnum = 754;
			return true;
		case 28: //magnum
			sprite[spr].hitag = 6;
			sprite[spr].picnum = 488;
			return true;
		case 29: //m60
			sprite[spr].hitag = 1;
			sprite[spr].picnum = 490;
			return true;
		case 30: //flame thower
			sprite[spr].hitag = 1;
			sprite[spr].picnum = 491;
			return true;
		case 32: //cobra staff
			sprite[spr].picnum = 899;
			sprite[spr].hitag = 1;
			return true;
		case 33: //eye of ra
			sprite[spr].hitag = 1;
			sprite[spr].picnum = 3455;
			return true;
		}

		return false;
	}

	public static void ExamineSprites() {
		nNetStartSprites = 0;
		nCurStartSprite = 0;

		for (short i = 0; i < MAXSPRITES; i++) {
			SPRITE spr = sprite[i];
			if (spr.statnum == 0) {
				if (spr.lotag != 0) {
					int lotag = spr.lotag;
					short hitag = spr.hitag;
					spr.lotag = 0;
					spr.hitag = 0;
					ProcessSpriteTag(i, lotag, hitag);
				} else
					engine.changespritestat(i, (short) 0);
			}
		}

		if (nNetPlayerCount == 0)
			return;

		int i = engine.insertsprite(initsect, (short) 0);
		sprite[i].x = initx;
		sprite[i].y = inity;
		sprite[i].z = initz;
		sprite[i].cstat = (short) 32768;

		nNetStartSprite[nNetStartSprites] = (short) i;
		nNetStartSprites++;
	}
}
