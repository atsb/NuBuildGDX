// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Blood.Screens;

import static ru.m210projects.Blood.Actor.gAmmoItemData;
import static ru.m210projects.Blood.Actor.gEffectInfo;
import static ru.m210projects.Blood.Actor.gItemInfo;
import static ru.m210projects.Blood.Actor.gMissileData;
import static ru.m210projects.Blood.Actor.gWeaponItemData;
import static ru.m210projects.Blood.Actor.kFXMax;
import static ru.m210projects.Blood.Actor.thingInfo;
import static ru.m210projects.Blood.DB.gSkyCount;
import static ru.m210projects.Blood.DB.kAmmoItemBase;
import static ru.m210projects.Blood.DB.kAmmoItemMax;
import static ru.m210projects.Blood.DB.kDudeAxeZombie;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeBat;
import static ru.m210projects.Blood.DB.kDudeBeastCultist;
import static ru.m210projects.Blood.DB.kDudeBlackSpider;
import static ru.m210projects.Blood.DB.kDudeBrownSpider;
import static ru.m210projects.Blood.DB.kDudeButcher;
import static ru.m210projects.Blood.DB.kDudeCerberus;
import static ru.m210projects.Blood.DB.kDudeCerberus2;
import static ru.m210projects.Blood.DB.kDudeDynamiteCultist;
import static ru.m210projects.Blood.DB.kDudeEarthZombie;
import static ru.m210projects.Blood.DB.kDudeEel;
import static ru.m210projects.Blood.DB.kDudeFirePod;
import static ru.m210projects.Blood.DB.kDudeFireTentacle;
import static ru.m210projects.Blood.DB.kDudeFleshGargoyle;
import static ru.m210projects.Blood.DB.kDudeFleshStatue;
import static ru.m210projects.Blood.DB.kDudeGillBeast;
import static ru.m210projects.Blood.DB.kDudeGreenPod;
import static ru.m210projects.Blood.DB.kDudeGreenTentacle;
import static ru.m210projects.Blood.DB.kDudeHand;
import static ru.m210projects.Blood.DB.kDudeHound;
import static ru.m210projects.Blood.DB.kDudeInnocent;
import static ru.m210projects.Blood.DB.kDudeMotherPod;
import static ru.m210projects.Blood.DB.kDudeMotherSpider;
import static ru.m210projects.Blood.DB.kDudeMotherTentacle;
import static ru.m210projects.Blood.DB.kDudePhantasm;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudeRat;
import static ru.m210projects.Blood.DB.kDudeRedSpider;
import static ru.m210projects.Blood.DB.kDudeShotgunCultist;
import static ru.m210projects.Blood.DB.kDudeSleepZombie;
import static ru.m210projects.Blood.DB.kDudeStoneGargoyle;
import static ru.m210projects.Blood.DB.kDudeStoneStatue;
import static ru.m210projects.Blood.DB.kDudeTchernobog;
import static ru.m210projects.Blood.DB.kDudeTeslaCultist;
import static ru.m210projects.Blood.DB.kDudeTheBeast;
import static ru.m210projects.Blood.DB.kDudeTinyCaleb;
import static ru.m210projects.Blood.DB.kDudeTommyCultist;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemMax;
import static ru.m210projects.Blood.DB.kMissileAltTesla;
import static ru.m210projects.Blood.DB.kMissileBase;
import static ru.m210projects.Blood.DB.kMissileFlare;
import static ru.m210projects.Blood.DB.kMissileTesla;
import static ru.m210projects.Blood.DB.kThingAltNapalm;
import static ru.m210projects.Blood.DB.kThingBase;
import static ru.m210projects.Blood.DB.kThingClearGlass;
import static ru.m210projects.Blood.DB.kThingFlammableTree;
import static ru.m210projects.Blood.DB.kThingFluorescent;
import static ru.m210projects.Blood.DB.kThingGibObject;
import static ru.m210projects.Blood.DB.kThingMachineGun;
import static ru.m210projects.Blood.DB.kThingMetalGrate1;
import static ru.m210projects.Blood.DB.kThingSprayBundle;
import static ru.m210projects.Blood.DB.kThingTNTBundle;
import static ru.m210projects.Blood.DB.kThingTNTProx;
import static ru.m210projects.Blood.DB.kThingTNTRem;
import static ru.m210projects.Blood.DB.kThingTNTStick;
import static ru.m210projects.Blood.DB.kThingWeb;
import static ru.m210projects.Blood.DB.kThingZombieHead;
import static ru.m210projects.Blood.DB.kWeaponItemBase;
import static ru.m210projects.Blood.DB.kWeaponItemMax;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Globals.kGameCrash;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kMaxStatus;
import static ru.m210projects.Blood.Globals.kMaxTiles;
import static ru.m210projects.Blood.Globals.kSectorParallax;
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Globals.kStatFree;
import static ru.m210projects.Blood.Globals.kStatThing;
import static ru.m210projects.Blood.Main.gGameScreen;
import static ru.m210projects.Blood.PLAYER.weaponQAVs;
import static ru.m210projects.Blood.SOUND.sndPreloadSounds;
import static ru.m210projects.Blood.Strings.loading;
import static ru.m210projects.Blood.Strings.wait;
import static ru.m210projects.Blood.Tile.tilePreloadTile;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.VERSION.GAMEVER;
import static ru.m210projects.Blood.VERSION.SHAREWARE;
import static ru.m210projects.Blood.VERSION.VER100;
import static ru.m210projects.Blood.View.viewHandAnim;
import static ru.m210projects.Blood.View.viewShowLoadingTile;
import static ru.m210projects.Blood.Weapon.kQAVEnd;
import static ru.m210projects.Blood.Weapon.kQAVEnd100;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.Seq.SeqType;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;
import ru.m210projects.Build.Types.SPRITE;

public class PrecacheScreen extends PrecacheAdapter {

	private final Main app;
	public PrecacheScreen(Main game) {
		super(game);
		this.app = game;

		addQueue("Preload sounds...", new Runnable() {
			@Override
			public void run() {
				sndPreloadSounds();
			}
		});

		addQueue("Preload floor and ceiling tiles...", new Runnable() {
			@Override
			public void run() {
				int nSkyTile = -1;
				for (int i = 0; i < numsectors; i++) {
					tilePreloadTile(sector[i].floorpicnum);
					tilePreloadTile(sector[i].ceilingpicnum);
					if ((sector[i].ceilingstat & kSectorParallax) != 0) {
						if(nSkyTile == -1)
							nSkyTile = sector[i].ceilingpicnum;
					}
				}
				doprecache(0);

				if(nSkyTile > -1 && nSkyTile < kMaxTiles)
					for (int j = 1; j < gSkyCount; j++)
						tilePreloadTile(nSkyTile + j);
				doprecache(1);
			}
		});

		addQueue("Preload wall tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numwalls; i++) {
					tilePreloadTile(wall[i].picnum);
					if (wall[i].overpicnum >= 0)
						tilePreloadTile(wall[i].overpicnum);
				}
				doprecache(0);
			}
		});

		addQueue("Preload sprite tiles...", new Runnable() {
			@Override
			public void run() {
				for(int i = dudeInfo[kDudePlayer1 - kDudeBase].seqStartID;
						i <= dudeInfo[kDudePlayer1 - kDudeBase].seqStartID + 18; i++)
					SeqPreload(i);

				for (int i = 0; i < kMaxSprites; i++) {
					if (sprite[i] != null && sprite[i].statnum < kMaxStatus) {
						if (sprite[i].statnum == kStatDude)
							DudePrecache(sprite[i]);
						else if (sprite[i].statnum == kStatThing)
							ThingPrecache(sprite[i]);
						tilePreloadTile(sprite[i].picnum);
					}

					if (sprite[i] != null && sprite[i].statnum != kStatFree && sprite[i].extra > 0) {
						XSPRITE pXSprite = xsprite[sprite[i].extra];

						int nObject = pXSprite.dropMsg;
						if (nObject >= kItemBase && nObject < kItemMax) {
							int item = pXSprite.dropMsg - kItemBase;
							if (item >= 0 && item < gItemInfo.length) {
								int nTile = gItemInfo[item].picnum;
								tilePreloadTile(nTile);
							}
						} else if (nObject >= kAmmoItemBase && nObject < kAmmoItemMax) {
							int ammo = pXSprite.dropMsg - kAmmoItemBase;
							if (ammo >= 0 && ammo < gAmmoItemData.length) {
								int nTile = gAmmoItemData[ammo].picnum;
								tilePreloadTile(nTile);
							}
						} else if (nObject >= kWeaponItemBase
								&& nObject < kWeaponItemMax) {
							int weap = pXSprite.dropMsg - kWeaponItemBase;
							if (weap >= 0 && weap < gWeaponItemData.length) {
								int nTile = gWeaponItemData[weap].picnum;
								tilePreloadTile(nTile);
							}
						}

						if (pXSprite.key > 0 && pXSprite.key < 7) {
							int nTile = gItemInfo[pXSprite.key - 1].picnum;
							tilePreloadTile(nTile);
						}
					}
				}

				// Cultists pickup preload voxels
				tilePreloadTile(gWeaponItemData[1].picnum); // Shotgun
				tilePreloadTile(gWeaponItemData[2].picnum); // TommyGun

				tilePreloadTile(gAmmoItemData[7].picnum);
				tilePreloadTile(gAmmoItemData[9].picnum);

				doprecache(1);
			}
		});

		addQueue("Preload qav animation...", new Runnable() {
			@Override
			public void run() {
				int kEnd = kQAVEnd;
				if(GAMEVER == VER100)
					kEnd = kQAVEnd100;

				for(int i = 0; i < kEnd; i++) {
					if(weaponQAVs[i] != null)
						weaponQAVs[i].Preload();
				}
				if(!SHAREWARE)
					viewHandAnim.pQAV.Preload();
				doprecache(1);
			}
		});

		addQueue("Preload effects...", new Runnable() {
			@Override
			public void run() {
				//Explosions
				SeqPreload(3);
				SeqPreload(4);
				SeqPreload(5);
				SeqPreload(9);
				//LifeLeech sparks
				SeqPreload(55);
				SeqPreload(56);
				//TeslaCannon sparks
				SeqPreload(11);
				SeqPreload(30);
				//SprayCan fire
				SeqPreload(0);
				SeqPreload(1);
				//NaPalm weapon projectile
				SeqPreload(60);
				SeqPreload(61);
				//Tommygun eject
				SeqPreload(62);
				SeqPreload(63);
				SeqPreload(64);
				//Shotgun eject
				SeqPreload(65);
				SeqPreload(66);
				SeqPreload(67);
				//Ricochet
				SeqPreload(44);
				SeqPreload(45);
				SeqPreload(46);
				//Blood
				SeqPreload(49);
				SeqPreload(50);
				SeqPreload(51);
				SeqPreload(52);
				SeqPreload(57);
				SeqPreload(58);
				SeqPreload(59);

				SeqPreload(7); //Water Drip
				SeqPreload(8); //Blood Drip

				Console.Println("Preload effects");
				for(int i = 0; i < kFXMax; i++)
					tilePreloadTile(gEffectInfo[i].picnum);

				doprecache(1);
			}
		});

		addQueue("Preload projectiles...", new Runnable() {
			@Override
			public void run() {
				tilePreloadTile(thingInfo[kThingZombieHead - kThingBase].picnum);
				tilePreloadTile(thingInfo[kThingAltNapalm - kThingBase].picnum);
				tilePreloadTile(thingInfo[kThingTNTStick - kThingBase].picnum);
				tilePreloadTile(thingInfo[kThingTNTBundle - kThingBase].picnum);
				tilePreloadTile(thingInfo[kThingSprayBundle - kThingBase].picnum);
				tilePreloadTile(thingInfo[kThingTNTProx - kThingBase].picnum);
				tilePreloadTile(thingInfo[kThingTNTRem - kThingBase].picnum);
				tilePreloadTile(gMissileData[kMissileFlare - kMissileBase].picnum);
				tilePreloadTile(gMissileData[kMissileAltTesla - kMissileBase].picnum);
				tilePreloadTile(gMissileData[kMissileTesla - kMissileBase].picnum);

				doprecache(1);
			}
		});

		addQueue("Preload other tiles...", new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < MAXTILES; i++)
				{
					if(engine.getTile(i).data == null)
						engine.loadtile(i);
				}
			}
		});
	}

	@Override
	public void draw(String title, int index) {
		if(kGameCrash) {
			app.rMenu.run();
			return;
		}

		viewShowLoadingTile();
		engine.rotatesprite(160 << 16, 20 << 16, 65536, 0, 2038, -128, 0, 2 | 4 | 8 | 64, 0, 0, xdim - 1, ydim - 1);

		app.getFont(1).drawText(160, 13, loading,  -128, 0, TextAlign.Center, 2, false);
		app.getFont(3).drawText(160, 134, wait,  -128, 0, TextAlign.Center, 2, false);

		String mapname = gGameScreen.getTitle();
		if(mapname != null)
			app.getFont(1).drawText(160, 60, mapname,  -128, 0, TextAlign.Center, 2, true);


		app.getFont(3).drawText(160, 60 + 32, title,  -128, 0, TextAlign.Center, 2, false);
	}

	private void SeqPreload(int nSeqID) {
		SeqType pSeq = SeqType.getInstance(nSeqID);
		if(pSeq != null)
			pSeq.Preload();
	}

	private void DudePrecache(SPRITE pSprite)
	{
		int type = pSprite.lotag - kDudeBase;
		SeqPreload(dudeInfo[type].seqStartID);
		SeqPreload(dudeInfo[type].seqStartID + 1);
		SeqPreload(dudeInfo[type].seqStartID + 2);
		SeqPreload(dudeInfo[type].seqStartID + 3);
		SeqPreload(dudeInfo[type].seqStartID + 5);
		switch(pSprite.lotag)
		{
			case kDudeEel:
				SeqPreload(dudeInfo[type].seqStartID + 7);
				break;
			case kDudeTinyCaleb:
				SeqPreload(dudeInfo[type].seqStartID + 4);
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 8);
				SeqPreload(dudeInfo[type].seqStartID + 10);
				break;
			case kDudeFleshGargoyle:
			case kDudeStoneGargoyle:
			case kDudeHand:
			case kDudeCerberus:
			case kDudeCerberus2:
			case kDudeBat:
			case kDudeRat:
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 7);
				break;
			case kDudeTommyCultist:
			case kDudeShotgunCultist:
			case kDudeTeslaCultist:
			case kDudeDynamiteCultist:
			case kDudeBeastCultist:
				SeqPreload(dudeInfo[type].seqStartID + 3);
				SeqPreload(dudeInfo[type].seqStartID + 4);
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 7);
				SeqPreload(dudeInfo[type].seqStartID + 8);
				SeqPreload(dudeInfo[type].seqStartID + 9);
				SeqPreload(dudeInfo[type].seqStartID + 13);
				SeqPreload(dudeInfo[type].seqStartID + 14);
				SeqPreload(dudeInfo[type].seqStartID + 15);
				SeqPreload(dudeInfo[type].seqStartID + 17);
				SeqPreload(4099); //cultist burning
				SeqPreload(4111); //cultist burned
				SeqPreload(4112); //cultist burned2
				break;

			case kDudeInnocent:
				SeqPreload(12551); //Innocent burned
			case kDudePhantasm:
				SeqPreload(dudeInfo[type].seqStartID + 4);
			case kDudeFleshStatue:
			case kDudeStoneStatue:
				SeqPreload(dudeInfo[type].seqStartID + 6);
				break;
			case kDudeGillBeast:
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 7);
				SeqPreload(dudeInfo[type].seqStartID + 9);
				SeqPreload(dudeInfo[type].seqStartID + 10);
				break;
			case kDudeTheBeast:
				SeqPreload(dudeInfo[type].seqStartID + 9);
				SeqPreload(2576);
			case kDudeHound:
				SeqPreload(dudeInfo[type].seqStartID + 4);
			case kDudeGreenPod:
			case kDudeGreenTentacle:
			case kDudeFirePod:
			case kDudeFireTentacle:
			case kDudeMotherPod:
			case kDudeMotherTentacle:
			case kDudeBrownSpider:
			case kDudeRedSpider:
			case kDudeBlackSpider:
			case kDudeMotherSpider:
			case kDudeTchernobog:
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 7);
				SeqPreload(dudeInfo[type].seqStartID + 8);
				break;
			case kDudeButcher:
				SeqPreload(dudeInfo[type].seqStartID + 4);
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 8);
				SeqPreload(dudeInfo[type].seqStartID + 9);
				SeqPreload(dudeInfo[type].seqStartID + 10);
				break;
			case kDudeAxeZombie:
			case kDudeEarthZombie:
			case kDudeSleepZombie:
				SeqPreload(dudeInfo[type].seqStartID + 4);
				SeqPreload(dudeInfo[type].seqStartID + 6);
				SeqPreload(dudeInfo[type].seqStartID + 7);
				SeqPreload(dudeInfo[type].seqStartID + 8);
				SeqPreload(dudeInfo[type].seqStartID + 11);
				SeqPreload(dudeInfo[type].seqStartID + 13);
				SeqPreload(dudeInfo[type].seqStartID + 14);
				if(pSprite.lotag == kDudeEarthZombie)
				{
					SeqPreload(dudeInfo[type].seqStartID + 9);
					SeqPreload(dudeInfo[type].seqStartID + 12);
				}
				if(pSprite.lotag == kDudeSleepZombie)
					SeqPreload(dudeInfo[type].seqStartID + 10);
				break;
		}
	}

	private void ThingPrecache(SPRITE pSprite)
	{
		switch ( pSprite.lotag )
		{
			case kThingClearGlass:
		    case kThingFluorescent:
		    	SeqPreload(12);
		    	break;
		    case kThingWeb:
		    	SeqPreload(15);
		      	break;
		    case kThingMetalGrate1:
		    	SeqPreload(21);
		    	break;
		    case kThingFlammableTree:
		    	SeqPreload(25);
		    	SeqPreload(26);
		      break;
		    case kThingMachineGun:
		    	SeqPreload(38);
		    	SeqPreload(40);
		    	SeqPreload(28);
		    	break;
		    case kThingGibObject:
		    	break;
		}
	}
}
