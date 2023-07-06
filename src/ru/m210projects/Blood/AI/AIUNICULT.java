package ru.m210projects.Blood.AI;

import static ru.m210projects.Blood.AI.Ai.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.totalKills;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Tile.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Strings.seq;
import static ru.m210projects.Blood.Trig.*;
import static ru.m210projects.Blood.Types.DudeInfo.*;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.Trigger.*;
import static ru.m210projects.Build.Pragmas.*;


import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.EXPLODE;
import ru.m210projects.Blood.Types.THINGINFO;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.Seq.SeqInst;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Blood.Types.VECTORDATA;
import ru.m210projects.Blood.Types.GENDUDESND;

public class AIUNICULT {

	public static final int kSlopeThrow = -8192;
	public static final int kMaxGenDudeSndMode = 11;
	
	public static AISTATE[] GDXGenDudeIdle = new AISTATE[3];
	public static AISTATE[] GDXGenDudeSearch = new AISTATE[3];
	public static AISTATE[] GDXGenDudeGoto = new AISTATE[3];
	public static AISTATE[] GDXGenDudeDodge = new AISTATE[3];
	public static AISTATE[] GDXGenDudeDodgeDmg = new AISTATE[3];
	public static AISTATE[] GDXGenDudeChase = new AISTATE[3];
	public static AISTATE[] GDXGenDudeFire = new AISTATE[3];
	public static AISTATE[] GDXGenDudeRecoil = new AISTATE[3];
	public static AISTATE 	GDGenDudeThrow;
	public static AISTATE 	GDGenDudeThrow2;
	public static AISTATE   GDXGenDudePunch;
	public static AISTATE 	GDXGenDudeRTesla;
	
	public static final GENDUDESND[] gCustomDudeSnd = {
		new GENDUDESND(1003, 2, 0, true), 	// spot sound
		new GENDUDESND(1013, 2, 2, true), 	// pain sound
		new GENDUDESND(1018, 2, 4, false), 	// death sound
		new GENDUDESND(1031, 2, 6, true), 	// burning state sound
		new GENDUDESND(1018, 2, 8, false),	// explosive death or end of burning state sound
		new GENDUDESND(4021, 2, 10, true),	// target of dude is dead
		new GENDUDESND(1005, 2, 12, true),	// chase sound
		new GENDUDESND(-1, 0, 14, false),	// weapon attack
		new GENDUDESND(-1, 0, 15, false),	// throw attack
		new GENDUDESND(-1, 0, 16, false),	// melee attack
		new GENDUDESND(9008, 0, 17, false),	// transforming in other dude
	};
	
	public static void Init()
	{

		GDXGenDudeIdle[LAND] = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};

		GDXGenDudeIdle[WATER] = new AISTATE(Type.idle, 13, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};

		GDXGenDudeSearch[LAND] = new AISTATE(Type.search, 9, null, 600, false, true, true, GDXGenDudeIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};

		GDXGenDudeSearch[WATER] = new AISTATE(Type.search, 13,	null, 600, false, true, true, GDXGenDudeIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};

		GDXGenDudeGoto[LAND] = new AISTATE(Type.other, 9, null, 600, false, true, true, GDXGenDudeIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};

		GDXGenDudeGoto[WATER] = new AISTATE(Type.tgoto, 13, null, 600, false, true, true, GDXGenDudeIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};

		GDXGenDudeChase[LAND] = new AISTATE(Type.other, 9, null, 0, false,	true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		GDXGenDudeChase[DUCK] = new AISTATE(Type.other, 14, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		GDXGenDudeChase[WATER] = new AISTATE(Type.other, 13, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiGenDudeMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		GDXGenDudeDodge[LAND] = new AISTATE(Type.other, 9, null, 90, false,	true,	false, GDXGenDudeChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);

			}
		};

		GDXGenDudeDodge[DUCK] = new AISTATE(Type.other, 14, null, 90, false,	true,	false, GDXGenDudeChase[DUCK]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};

		GDXGenDudeDodge[WATER] = new AISTATE(Type.other, 13, null, 90, false,	true,	false, GDXGenDudeChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};

		// Those is for dodging after receive the damage
		// -----------------------------------------
		GDXGenDudeDodgeDmg[LAND] = new AISTATE(Type.other, 9, null, 60, false,	true,	false, GDXGenDudeChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);

			}
		};

		GDXGenDudeDodgeDmg[DUCK] = new AISTATE(Type.other, 14, null, 60, false,	true,	false, GDXGenDudeChase[DUCK]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};

		GDXGenDudeDodgeDmg[WATER] = new AISTATE(Type.other, 13, null, 60, false,	true,	false, GDXGenDudeChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		// -----------------------------------------
		
		CALLPROC attack = new CALLPROC() {
			public void run(int nXSprite) {
				GDXCultistAttack1(nXSprite);
			}
		};
		
		GDXGenDudeFire[LAND] = new AISTATE(Type.other, kSeqDudeAttack, attack, 0, false, true, true, GDXGenDudeFire[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		GDXGenDudeFire[WATER] = new AISTATE(Type.other, 8, attack, 0, false, true, true, GDXGenDudeFire[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		GDXGenDudeFire[DUCK] = new AISTATE(Type.other, 8, attack, 0, false, true, true, GDXGenDudeFire[DUCK]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		GDGenDudeThrow = new AISTATE(Type.other, 7, new CALLPROC() {
			public void run(int nXSprite) {
				ThrowCallback(nXSprite,true);
			}
		}, 0, false, false, false, GDXGenDudeChase[LAND]);

		GDGenDudeThrow2 = new AISTATE(Type.other, 7, new CALLPROC() {
			public void run(int nXSprite) {
				ThrowCallback(nXSprite,false);
			}
		}, 0, false, false, false, GDXGenDudeChase[LAND]);

		GDXGenDudeRTesla = new AISTATE(Type.other, kSeqDudeTesla, null, 0, false, false, false, GDXGenDudeDodge[LAND]);

		GDXGenDudeRecoil[LAND] = new AISTATE(Type.other, kSeqDudeRecoil, null, 0, false, false, false, GDXGenDudeChase[LAND]);
		GDXGenDudeRecoil[WATER] = new AISTATE(Type.other, kSeqDudeRecoil, null, 0, false, false, false, GDXGenDudeChase[WATER]);
		GDXGenDudeRecoil[DUCK] = new AISTATE(Type.other, kSeqDudeRecoil, null, 0, false, false, false, GDXGenDudeChase[DUCK]);

		GDXGenDudePunch = new AISTATE(Type.other,10, new CALLPROC() {
			public void run(int nXSprite) {
				punchCallback(nXSprite);
//				punch = true;
			}
		}, 0, false,false,true,GDXGenDudeChase[LAND]){
			final boolean punch = false;
			public void think(SPRITE sprite, XSPRITE xsprite) {
				// Required for those who don't have fire trigger in punch seq
				if (!punch && seqFrame(SS_SPRITE,sprite.extra) == -1){
					int nXSprite = sprite.extra;
					punchCallback(nXSprite);
				}
			}
		};
	}

		
		private static void punchCallback(int nXIndex){
			XSPRITE pXSprite = xsprite[nXIndex];
			int nSprite = pXSprite.reference;
			SPRITE pSprite = sprite[nSprite];

			int nAngle = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);

			int nZOffset1 = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight;
			int nZOffset2 = 0;
			if(pXSprite.target != -1) {
				SPRITE pTarget = sprite[pXSprite.target];
				if(IsDudeSprite(pTarget))
					nZOffset2 = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight;

				int dx = Cos(nAngle) >> 16;
				int dy = Sin(nAngle) >> 16;
				int dz = nZOffset1 - nZOffset2;
				
				if (!sfxPlayGDXGenDudeSound(pSprite,9))
					sfxStart3DSound(pSprite, 530, 1, 0);
				
				actFireVector(pSprite, 0, 0, dx, dy, dz,22);
			}
		}

		private static void GDXCultistAttack1( int nXIndex ) {
			XSPRITE pXSprite = xsprite[nXIndex]; int nSprite = pXSprite.reference;
			SPRITE pSprite = sprite[nSprite]; int dx, dy, dz; int weapon = pXSprite.data1;
			if (weapon >= 0 && weapon < kVectorMax){
	
				int vector = weapon;
				dx = Cos(pSprite.ang) >> 16;
				dy = Sin(pSprite.ang) >> 16;
				dz = gDudeSlope[nXIndex];
	
				VECTORDATA pVectorData = gVectorData[vector];
				int vdist = pVectorData.maxDist;
				
				// dispersal modifiers here in case if non-melee enemy
				if (vdist <= 0 || vdist > 1280) {
					dx += BiRandom2(3000 - 1000 * pGameInfo.nDifficulty);
					dy += BiRandom2(3000 - 1000 * pGameInfo.nDifficulty);
					dz += BiRandom2(1000 - 500 * pGameInfo.nDifficulty);
				}
	
				actFireVector(pSprite, 0, 0, dx, dy, dz,vector);
				if (!sfxPlayGDXGenDudeSound(pSprite,7))
					sfxPlayVectorSound(pSprite,vector);
				
			} else if (weapon >= kDudeBase && weapon < kDudeMax) {
				
				SPRITE pSpawned = null; int dist = pSprite.clipdist*6;
				if((pSpawned = actSpawnDude(pSprite, weapon, dist)) == null)
					return;
					
				aiThinkTime[nSprite]++;
				pSpawned.owner = (short) nSprite;
				pSpawned.x+=dist+(BiRandom(dist));
				if (pSpawned.extra > -1) {
					xsprite[pSpawned.extra].target = pXSprite.target;
					if (pXSprite.target > -1)
						aiActivateDude(pSpawned,xsprite[pSpawned.extra]);
				}
				totalKills++;
				
				
				if (!sfxPlayGDXGenDudeSound(pSprite,7))
					sfxStart3DSoundCP(pSprite, 379, 1, 0,0x10000 - BiRandom(0x3000), 0);

				if (Chance(0x3500)) {
					int state = checkAttackState(pSprite, pXSprite);
					switch (state){
					case 1:
						aiNewState(pSprite, pXSprite, GDXGenDudeDodge[WATER]);
						break;
					case 2:
						aiNewState(pSprite, pXSprite, GDXGenDudeDodge[DUCK]);
						break;
					default:
						aiNewState(pSprite, pXSprite, GDXGenDudeDodge[LAND]);
						break;
					}
				}

				
			} else if (weapon >= kMissileBase && weapon < kMissileMax) {
	
				dx = Cos(pSprite.ang) >> 16;
				dy = Sin(pSprite.ang) >> 16;
				dz = gDudeSlope[nXIndex];
	
				// dispersal modifiers here
				dx += BiRandom2(3000 - 1000 * pGameInfo.nDifficulty);
				dy += BiRandom2(3000 - 1000 * pGameInfo.nDifficulty);
				dz += BiRandom2(1000 - 500 * pGameInfo.nDifficulty);
	
				actFireMissile(pSprite, 0, 0, dx, dy, dz, weapon);
				if (!sfxPlayGDXGenDudeSound(pSprite,7))
					sfxPlayMissileSound(pSprite,weapon);
			}
	}

	private static void ThrowCallback(int nXIndex, boolean impact)
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		if(!(pXSprite.target >= 0 && pXSprite.target < kMaxSprites))
			return;
			//dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");

		SPRITE pTarget = sprite[pXSprite.target];
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			return;
			//dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");

		int thingType = pXSprite.data1;
		if (thingType >= kThingBase && thingType < kThingMax) {
			
			THINGINFO pThinkInfo = thingInfo[thingType - kThingBase];
			if (pThinkInfo.allowThrow == 1){
				
				if (!sfxPlayGDXGenDudeSound(pSprite,8))
					sfxStart3DSound(pSprite, 455, -1, 0);
				
				int dx = pTarget.x - pSprite.x;
				int dy = pTarget.y - pSprite.y;
				int dz = pTarget.z - pSprite.z;

				int dist = (int) engine.qdist(dx, dy);	int zThrow = 14500;
				SPRITE pThing = null; SPRITE pLeech = null; XSPRITE pXLeech = null;
				if (thingType == kGDXThingCustomDudeLifeLeech){
					if ((pLeech = leechIsDropped(pSprite)) != null){
						// pickup life leech before throw it again
						pXLeech = xsprite[pLeech.extra];
						removeLeech(pLeech);
					}
					
					zThrow = 5000;
				}
					
				pThing = actFireThing(nSprite, 0, 0, (dz / 128) - zThrow, thingType, divscale(dist / 540, kTimerRate, 23));
				if (pThing == null) return;
				if (pThing.lotag != kGDXThingThrowableRock) {
					if (pThinkInfo.picnum < 0) pThing.picnum = 0;
					else tileLoadVoxel(pThinkInfo.picnum);
				}
				
				pThing.owner = pSprite.xvel;
				switch(thingType){
					case 428:
						impact = true;
						pThing.xrepeat = 24;
						pThing.yrepeat = 24;
						xsprite[pThing.extra].data4 = 3+pGameInfo.nDifficulty;
						break;
					
					case kGDXThingThrowableRock:
						int[] sPics; sPics = new int[6];
						sPics[0] = 2406;	sPics[1] = 2280;
						sPics[2] = 2185;	sPics[3] = 2155;
						sPics[4] = 2620;	sPics[5] = 3135;
						
						pThing.picnum = (short) sPics[Random(5)];
						pThing.pal = 5;	pThing.cstat |= 0x0001;
						pThing.xrepeat = (short) (24 + Random(42));
						pThing.yrepeat = (short) (24 + Random(42));
						
						
						if (Chance(0x3000)) pThing.cstat |= 0x0004;
						if (Chance(0x3000)) pThing.cstat |= 0x0008;
						
						if (pThing.xrepeat > 60) xsprite[pThing.extra].data1 = 43;
						else if (pThing.xrepeat > 40) xsprite[pThing.extra].data1 = 33;
						else if (pThing.xrepeat > 30) xsprite[pThing.extra].data1 = 23;
						else xsprite[pThing.extra].data1 = 12;
							
						impact = false;
						return;
					case kThingTNTBarrel:
					case kThingTNTProx:
					case kThingSprayBundle:
						impact = false;
						break;
					case kGDXThingTNTProx:
						xsprite[pThing.extra].state = 0;
						xsprite[pThing.extra].Proximity = true;
						return;
					case kThingLifeLeech:
					case kGDXThingCustomDudeLifeLeech:
						XSPRITE pXThing = xsprite[pThing.extra];
						if (pLeech != null) pXThing.health = pXLeech.health;
						else pXThing.health = 300*pGameInfo.nDifficulty;

						sfxStart3DSound(pSprite, 490, -1, 0);
						
						if (pGameInfo.nDifficulty <= 2) pXThing.data3 = 32700;
						else pXThing.data3 = (short) Random(10);
						pThing.pal = 6;
						pXThing.target = pTarget.xvel;
						pXThing.Proximity = true;
						pXThing.stateTimer = 1;
						evPostCallback(pThing.xvel, SS_SPRITE, 80, 20);
						return;
				}

				if(impact && dist <= 7680) xsprite[pThing.extra].Impact = true;
				else {
					xsprite[pThing.extra].Impact = false;
					evPost(pThing.xvel, SS_SPRITE, kTimerRate * Random(2) + kTimerRate, 1);
				}
				return;
			}
			
		}

	}

	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{

        aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget2(pSprite, pXSprite);
	}

	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		
		int dx, dy, dist;
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax)) 
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(10.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery ) {
			if(spriteIsUnderwater(pSprite,false))
				aiNewState(pSprite, pXSprite, GDXGenDudeSearch[WATER]);
			else
				aiNewState(pSprite, pXSprite, GDXGenDudeSearch[LAND]);
		}
		aiThinkTarget(pSprite, pXSprite);
	}

	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite ) {

		if(pSprite.lotag < kDudeBase || pSprite.lotag >= kDudeMax)
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		else if (pXSprite.target <= -1) {
			if(spriteIsUnderwater(pSprite,false)) aiNewState(pSprite, pXSprite, GDXGenDudeGoto[WATER]);
			else aiNewState(pSprite, pXSprite, GDXGenDudeGoto[LAND]);
			return;
		} else if (pXSprite.target >= kMaxSprites)
			game.dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");
		
		int dx, dy; DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = (!IsDudeSprite(pTarget) || pTarget.extra < 0) ? null : xsprite[pTarget.extra];
		
		// check target
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;

		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));

		if (pXTarget == null || pXTarget.health == 0) {
			// target is dead
			if(spriteIsUnderwater(pSprite,false)) 
				aiNewState(pSprite, pXSprite, GDXGenDudeSearch[WATER]);
			else {
				aiNewState(pSprite, pXSprite, GDXGenDudeSearch[LAND]);
				sfxPlayGDXGenDudeSound(pSprite,5);
			}
			return;
		} else if (IsPlayerSprite(pTarget)) {
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if (powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 ) {
				if(spriteIsUnderwater(pSprite,false)) aiNewState(pSprite, pXSprite, GDXGenDudeSearch[WATER]);
				else aiNewState(pSprite, pXSprite, GDXGenDudeSearch[LAND]);
				return;
			}
		}

		int dist = (int) engine.qdist(dx, dy); if (dist == 0) dist = 1;
		int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;
		if (dist > pDudeInfo.seeDist || !engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum)) {
			
			if(spriteIsUnderwater(pSprite,false)) aiNewState(pSprite, pXSprite, GDXGenDudeGoto[WATER]);
			else aiNewState(pSprite, pXSprite, GDXGenDudeGoto[LAND]);
			pXSprite.target = -1;
			return;
		}

		int nAngle = engine.getangle(dx, dy);
		int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
		// is the target visible?
		if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery) {
			
			if (pXSprite.target < 0) aiSetTarget(pXSprite, pXSprite.target);
			if ((gFrameClock & 64) == 0 && Chance(0x1000) && !spriteIsUnderwater(pSprite,false))
				sfxPlayGDXGenDudeSound(pSprite,6);

			gDudeSlope[sprite[pXSprite.reference].extra] = divscale(pTarget.z - pSprite.z, dist, 10);

			SPRITE pLeech = null;	VECTORDATA meleeVector = gVectorData[22];
			if (pXSprite.data1 >= kThingBase && pXSprite.data1 < kThingMax) {
				if (pXSprite.data1 == kThingLifeLeech) pXSprite.data1 = kGDXThingCustomDudeLifeLeech;
				if ((pLeech = leechIsDropped(pSprite)) != null && xsprite[pLeech.extra].target != pXSprite.target)
						xsprite[pLeech.extra].target = pXSprite.target;

				if (klabs(losAngle) < kAngle15) {
					if (dist < 12264 && dist > 7680 && !spriteIsUnderwater(pSprite,false) && pXSprite.data1 != kGDXThingCustomDudeLifeLeech){
						int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
						switch(pHit){
							case SS_WALL:
							case SS_MASKED:
								return;
							default:
								aiNewState(pSprite, pXSprite, GDGenDudeThrow);
								return;
						}

					} else if (dist > 4072 && dist <= 9072 && !spriteIsUnderwater(pSprite,false) && pSprite.owner != 32666) {
						switch (pXSprite.data1) {
							case kGDXThingCustomDudeLifeLeech:
								if (pLeech == null){
									aiNewState(pSprite, pXSprite, GDGenDudeThrow2);
									GDGenDudeThrow2.next = GDXGenDudeDodge[LAND];
									return;
								}
								
								XSPRITE pXLeech = xsprite[pLeech.extra];
								int ldist = getTargetDist(pTarget,pDudeInfo,pLeech);
								//System.out.println("LDIST: "+ldist);
								if (ldist > 3 || !engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
													pLeech.x, pLeech.y, pLeech.z, pLeech.sectnum) || pXLeech.target == -1) {
														
									aiNewState(pSprite, pXSprite, GDGenDudeThrow2);
									GDGenDudeThrow2.next = GDXGenDudeDodge[LAND];
									
								} else {
									GDGenDudeThrow2.next = GDXGenDudeChase[LAND];
									if (pXLeech.target != pXSprite.target) pXLeech.target = pXSprite.target;
									if (dist > 5072 && Chance(0x3000)) {
										
										if (!canDuck(pSprite) || Chance(0x2000)) 
											aiNewState(pSprite, pXSprite, GDXGenDudeDodge[LAND]);
										else 
											aiNewState(pSprite, pXSprite, GDXGenDudeDodge[DUCK]);
										
									} else {
										aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
									}
								}
								return;
							case kGDXThingThrowableRock:
								if (Chance(0x2000)) aiNewState(pSprite, pXSprite, GDGenDudeThrow2);
								else sfxPlayGDXGenDudeSound(pSprite,0);
								return;
							default:
								aiNewState(pSprite, pXSprite, GDGenDudeThrow2);
								return;
						}

					} else if (dist <= meleeVector.maxDist) {
						
						if (spriteIsUnderwater(pSprite,false)) {
							if (Chance(0x7000)) aiNewState(pSprite, pXSprite, GDXGenDudePunch);
							else aiNewState(pSprite, pXSprite, GDXGenDudeDodge[WATER]);

						} 
						else if (Chance(0x7000)) aiNewState(pSprite, pXSprite, GDXGenDudePunch);
						else aiNewState(pSprite, pXSprite, GDXGenDudeDodge[LAND]);
						return;

					} else {
						int state = checkAttackState(pSprite, pXSprite);
						if(state == 1) aiNewState(pSprite, pXSprite, GDXGenDudeChase[WATER]);
						else if(state == 2) {
							if (Chance(0x3000)) aiNewState(pSprite, pXSprite, GDXGenDudeChase[DUCK]);
							else aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
						} 
						else  aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
						return;
					}
				}

			} else {
				
				int defDist = 17920; int vdist = defDist;
				
				if (pXSprite.data1 > 0 && pXSprite.data1 < kVectorMax) {

					switch(pXSprite.data1){
						case 19:
							pXSprite.data1 = 2;
							break;
					}
					
					VECTORDATA pVectorData = gVectorData[pXSprite.data1]; 
					vdist = pVectorData.maxDist;
					if (vdist <= 0 || vdist > defDist) 
						vdist = defDist;
					
				} else if (pXSprite.data1 >= kDudeBase && pXSprite.data1 < kDudeMax) {
					
					if (aiThinkTime[pSprite.xvel] > 0) { 
						updateTargetOfSlaves(pSprite);
						if (pXSprite.target >= 0 && sprite[pXSprite.target].owner == pSprite.xvel) {
							aiSetTarget(pXSprite, pSprite.x,pSprite.y,pSprite.z);
							return;
						}
					}
					
					int state = checkAttackState(pSprite, pXSprite);
					if(aiThinkTime[pSprite.xvel] <= pGameInfo.nDifficulty && dist > meleeVector.maxDist) {
						vdist = (vdist / 2) + Random(vdist / 2);
					} else if (dist <= meleeVector.maxDist) {
						aiNewState(pSprite, pXSprite, GDXGenDudePunch);
						return;
					} else {
						
						if (state == 1) aiNewState(pSprite, pXSprite, GDXGenDudeChase[WATER]);
						else if (state == 2) aiNewState(pSprite, pXSprite, GDXGenDudeChase[DUCK]);
						else aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
						return;
					}
					
					
					
				} else if (pXSprite.data1 >= kMissileBase && pXSprite.data1 < kMissileMax){
					// special handling for flame, explosive and life leech missiles
					int state = checkAttackState(pSprite, pXSprite);
					int mdist = (pXSprite.data1 != kMissileStarburstFlare) ? 3000 : 2500;
					switch(pXSprite.data1){
						case kMissileLifeLeech:
							// pickup life leech if it was thrown previously
							if ((pLeech = leechIsDropped(pSprite)) != null) removeLeech(pLeech);
							break;
						case kMissileStarburstFlare:
						case kMissileFireball:
						case kMissileNapalm:
						case kMissileTchernobog:
						case kMissileTchernobog2:
							if (dist > mdist || pXSprite.Locked == 1) break;
							else if (dist <= meleeVector.maxDist && Chance(0x7000)) 
								aiNewState(pSprite, pXSprite, GDXGenDudePunch);
							else if (state == 1) aiNewState(pSprite, pXSprite, GDXGenDudeChase[WATER]);
							else if (state == 2) aiNewState(pSprite, pXSprite, GDXGenDudeChase[DUCK]);
							else aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
							return;
						case kMissileSprayFlame:
						case kMissileHoundFire:
							if (spriteIsUnderwater(pSprite,false)){
								if (dist > meleeVector.maxDist) aiNewState(pSprite, pXSprite, GDXGenDudeChase[WATER]);
								else if (Chance(0x4000)) aiNewState(pSprite, pXSprite, GDXGenDudePunch);
								else aiNewState(pSprite, pXSprite, GDXGenDudeDodge[WATER]);
								return;
							}

							vdist = 4200; if ((gFrameClock & 16) == 0) vdist += Random(800);
							break;
					}

				} else if (pXSprite.data1 >= kThingHiddenExploder && pXSprite.data1 < (kThingHiddenExploder + kExplodeMax) -1) {

					int nType = pXSprite.data1 - kThingHiddenExploder; EXPLODE pExpl = gExplodeData[nType];
					boolean inRange = CheckProximity(pSprite, pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum, pExpl.radius / 2);
					if (pExpl != null && inRange && doExplosion(pSprite,nType)) actKillSprite(pSprite.xvel,pSprite,3,65535);
					return;
					
				// scared dude - no weapon. Still can punch you sometimes.	
				} else {
					
					int state = checkAttackState(pSprite, pXSprite);
					if (Chance(0x0500) && !spriteIsUnderwater(pSprite,false))
						sfxPlayGDXGenDudeSound(pSprite,6);
					
					if (Chance(0x0200)) {
						if (dist <= meleeVector.maxDist) aiNewState(pSprite, pXSprite, GDXGenDudePunch);
						else if (state == 1) aiNewState(pSprite, pXSprite, GDXGenDudeDodge[WATER]);
						else if (state == 2) aiNewState(pSprite, pXSprite, GDXGenDudeDodge[DUCK]);
						else aiNewState(pSprite, pXSprite, GDXGenDudeDodge[LAND]);
					}
					else if (state == 1) aiNewState(pSprite, pXSprite, GDXGenDudeSearch[WATER]);
					else aiNewState(pSprite, pXSprite, GDXGenDudeSearch[LAND]);
					
					aiSetTarget(pXSprite,pSprite.x,pSprite.y,pSprite.z);
					return;
				}

				
				if (dist <= vdist && pXSprite.aiState == GDXGenDudeChase[DUCK])
					aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
				
				int state = checkAttackState(pSprite, pXSprite);
				if (dist < vdist /*&& klabs(losAngle) < 32*/ && klabs(losAngle) < kAngle5) {
					
					switch(state) {
						case 1:
							aiNewState(pSprite, pXSprite, GDXGenDudeFire[WATER]);
							pXSprite.aiState.next = GDXGenDudeFire[WATER];
							return;
						case 2:
							aiNewState(pSprite, pXSprite, GDXGenDudeFire[DUCK]);
							pXSprite.aiState.next = GDXGenDudeFire[DUCK];
							return;
						default:
							aiNewState(pSprite, pXSprite, GDXGenDudeFire[LAND]);
							pXSprite.aiState.next = GDXGenDudeFire[LAND];
							return;
					}
					
				} else {
					int nSeq = (state < 3) ? 8 : 6;
					
					SeqInst pInst = GetInstance(SS_SPRITE, pSprite.extra);
					if (pInst.getSeqIndex() == (xsprite[pSprite.extra].data2 + nSeq)) {
						//System.err.println("SAME SEQ FOUND "+(xsprite[pSprite.extra].data2 + nSeq));
						if (state == 1) pXSprite.aiState.next = GDXGenDudeChase[WATER];
						else if (state == 2) pXSprite.aiState.next = GDXGenDudeChase[DUCK];
						else pXSprite.aiState.next = GDXGenDudeChase[LAND];
					
					} else if (state == 1 && pXSprite.aiState != GDXGenDudeChase[WATER] && pXSprite.aiState != GDXGenDudeFire[WATER]) {
						aiNewState(pSprite, pXSprite, GDXGenDudeChase[WATER]);
						pXSprite.aiState.next = GDXGenDudeFire[WATER];
					
					} else if (state == 2  && pXSprite.aiState != GDXGenDudeChase[DUCK] && pXSprite.aiState != GDXGenDudeFire[DUCK]) {
						aiNewState(pSprite, pXSprite, GDXGenDudeChase[DUCK]);
						pXSprite.aiState.next = GDXGenDudeFire[DUCK];
					
					} else if (pXSprite.aiState != GDXGenDudeChase[LAND] && pXSprite.aiState != GDXGenDudeFire[LAND]) {
						aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
						pXSprite.aiState.next = GDXGenDudeFire[LAND];
					}
					
				}
			}
		}
	}

	public static int checkAttackState(SPRITE pSprite, XSPRITE pXSprite) {
		//DUCK - seq 14
		if (checkUniCultistSeq(pSprite, 14) || spriteIsUnderwater(pSprite,false)) {
        	if (!checkUniCultistSeq(pSprite, 14) || spriteIsUnderwater(pSprite,false)) {
				if (spriteIsUnderwater(pSprite,false))
        			return 1; //water
        	}
        	else
        		return 2; //duck
        }
        else
        	return 3; //land
		return 0;
	}

	public static boolean checkUniCultistSeq(SPRITE pSprite, int nSeqID) {
		if (pSprite.statnum == kStatDude && IsDudeSprite(pSprite)) {
			SeqInst pInst = GetInstance(SS_SPRITE, pSprite.extra);
			return pInst.getSeqIndex() == (xsprite[pSprite.extra].data2 + nSeqID) && seqFrame(SS_SPRITE, pSprite.extra) >= 0;
		}
		return false;
	}

	public static boolean TargetNearThing(SPRITE pSprite, int thingType) {
		for ( int nSprite = headspritesect[pSprite.sectnum]; nSprite >= 0; nSprite = nextspritesect[nSprite] )
		{
			// check for required things or explosions in the same sector as the target
			if ( sprite[nSprite].lotag == thingType || sprite[nSprite].statnum == kStatExplosion )
				return true; // indicate danger
		}
		return false;
	}
	
	///// For gen dude
	
	public static long getGenDudeMoveSpeed(SPRITE pSprite,int which, boolean mul, boolean shift){
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		XSPRITE pXSprite = xsprite[pSprite.extra];
		int speed = -1; int step = 2500; int maxSpeed = 146603;
		switch(which){
			case 0:
				speed = pDudeInfo.frontSpeed;
				break;
			case 1:
				speed = pDudeInfo.sideSpeed;
				break;
			case 2:
				speed = pDudeInfo.backSpeed;
				break;
			case 3:
				speed = pDudeInfo.angSpeed;
				break;
			default:
				return -1;
		}
		if (pXSprite.busyTime > 0) speed /=3;
		if (speed > 0 && mul) {
			
			//System.err.println(pXSprite.busyTime);
			if (pXSprite.busyTime > 0)
				speed += (step * pXSprite.busyTime);
		}
		
		if (shift) speed *= kFrameTicks >> 4;
		if (speed > maxSpeed) speed = maxSpeed;
		
		return speed;
	}
	
	public static void aiGenDudeMoveForward( SPRITE pSprite, XSPRITE pXSprite ) {
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
	
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;

		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);
		
		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 )
			return;
		
		int sin = Sin(pSprite.ang);
		int cos = Cos(pSprite.ang);

		long frontSpeed = getGenDudeMoveSpeed(pSprite,0,true,false);
		sprXVel[pSprite.xvel] += mulscale(cos, frontSpeed, 30);
		sprYVel[pSprite.xvel] += mulscale(sin, frontSpeed, 30);
	}
	
	public static boolean sfxPlayGDXGenDudeSound(SPRITE pSprite, int mode){

		if (mode < 0 || mode >= kMaxGenDudeSndMode) return false;
		GENDUDESND sndInfo = gCustomDudeSnd[mode]; boolean gotSnd = false;
		short sndStartId = xsprite[pSprite.extra].data3; int rand = sndInfo.randomRange;
		int sndId = (sndStartId <= 0) ? sndInfo.defaultSndId : sndStartId + sndInfo.sndIdOffset;

		if (sndId < 0) return false;
		else if (sndStartId <= 0) { sndId += Random(rand); gotSnd = true; }
		else {

			// Let's try to get random snd
			int maxRetries = 5;
			while(maxRetries-- > 0){
				int random = Random(rand);
				if (!BuildGdx.cache.contains(sndId + random, "sfx")) continue;
				sndId = sndId + random;
				gotSnd = true;
				break;
			}
			
			// If no success in getting random snd, get first existing one
			if (!gotSnd){
				while(sndId++ <= sndId + rand) {
					if (!BuildGdx.cache.contains(sndId, "sfx")) continue;
					gotSnd = true;
					break;
				}
			}
			
		}
		
		if (!gotSnd) return false;
		else if (sndInfo.aiPlaySound) aiPlaySound(pSprite, sndId, 2, -1);
		else sfxStart3DSound(pSprite, sndId, -1, 0);
		return true;
	}
	
	
	public static boolean spriteIsUnderwater(SPRITE pSprite,boolean oldWay){
		if (oldWay){
			return xsprite[pSprite.extra].palette == 1 || xsprite[pSprite.extra].palette == 2;
		}
		
		if (sector[pSprite.sectnum].extra < 0) return false;
		else return xsector[sector[pSprite.sectnum].extra].Underwater;
	}
	
	public static SPRITE leechIsDropped(SPRITE pSprite){
		for (int nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			if (sprite[nSprite].lotag == kGDXThingCustomDudeLifeLeech && sprite[nSprite].owner == pSprite.xvel)
				return sprite[nSprite];
		}
		
		return null;
		
	}
	
	public static void removeDudeStuff(SPRITE pSprite){
		for (short nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			if (sprite[nSprite].owner != pSprite.xvel) continue;
			switch(sprite[nSprite].lotag){
				case 401:
				case 402:
				case 433:
					deletesprite(nSprite);
					break;
				case kGDXThingCustomDudeLifeLeech:
					killDudeLeech(sprite[nSprite]);
					break;
			}
		}
		
		for (short nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			if (sprite[nSprite].owner != pSprite.xvel) continue;
			actDamageSprite(sprite[nSprite].owner,sprite[nSprite],kDamageFall,65535);
		}
	}

	public static void removeLeech(SPRITE pLeech){
		if (pLeech != null) {
			SPRITE pEffect = actSpawnEffect(52,pLeech.sectnum,pLeech.x,pLeech.y,pLeech.z,pLeech.ang);
			if (pEffect != null) {
				pEffect.cstat = kSpriteFace;
				pEffect.pal = 6;
				int repeat = 64 + Random(50);
				pEffect.xrepeat = (short) repeat;
				pEffect.yrepeat = (short) repeat;
			}
			sfxStart3DSoundCP(pLeech, 490, -1, 0,60000, 0);
			deletesprite(pLeech.xvel);
		}
	}
	
	public static void killDudeLeech(SPRITE pLeech){
		actDamageSprite(pLeech.owner,pLeech,3,65535);
		removeLeech(pLeech);				
		sfxStart3DSoundCP(pLeech, 522, -1, 0,60000, 0);
	}

	
	
	public static void updateTargetOfSlaves(SPRITE pSprite) {
		for (short nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			if (sprite[nSprite].owner != pSprite.xvel || sprite[nSprite].extra < 0) continue;
			else if (xsprite[pSprite.extra].target != xsprite[sprite[nSprite].extra].target 
					&& IsDudeSprite(sprite[xsprite[pSprite.extra].target])) {
						aiSetTarget(xsprite[sprite[nSprite].extra],xsprite[pSprite.extra].target);
			}

			if (xsprite[sprite[nSprite].extra].target >= 0) {
				// don't attack mates
				if (sprite[xsprite[sprite[nSprite].extra].target].owner == sprite[nSprite].owner)
					aiSetTarget(xsprite[sprite[nSprite].extra], pSprite.x,pSprite.y,pSprite.z);
			}
			
			if (!isActive(sprite[nSprite].xvel) && xsprite[sprite[nSprite].extra].target >= 0)
				aiActivateDude(sprite[nSprite], xsprite[sprite[nSprite].extra]);
		}
		
		return;
	}

	public static boolean dudeIsMelee(XSPRITE pXSprite) {
		int meleeDist = 2048; int vdist = meleeDist;
		if (pXSprite.data1 >= 0 && pXSprite.data1 < kVectorMax){
			int vector = pXSprite.data1; if (vector <= 0) vector = 2;
			VECTORDATA pVectorData = ru.m210projects.Blood.Actor.gVectorData[vector];
			vdist = pVectorData.maxDist;

			return vdist > 0 && vdist <= meleeDist;
			
		} else {

			return pXSprite.data1 >= kThingHiddenExploder && pXSprite.data1 < (kThingHiddenExploder + kExplodeMax) - 1;

        }
	}
	
	public static int getBaseChanceModifier(int baseChance) {
	    return ((pGameInfo.nDifficulty > 0) ? baseChance - (0x0300 * pGameInfo.nDifficulty) : baseChance);
	}
	
	public static int getRecoilChance(SPRITE pSprite) {
		int mass = getDudeMassBySpriteSize(pSprite);
		int cumulDmg = 0; int baseChance = getBaseChanceModifier(0x6000);
		if (pSprite.extra >= 0) {
			XSPRITE pXSprite = xsprite[pSprite.extra];
			baseChance+=(pXSprite.burnTime / 2);
			cumulDmg = (cumulDamage[pSprite.extra]);
			if (dudeIsMelee(pXSprite)) 
				baseChance = 0x500;
		}
		
		baseChance+=cumulDmg;
		int chance = ((baseChance / mass) << 7);
		return chance;
		
	}

	public static int getDodgeChance(SPRITE pSprite) {
		int mass = getDudeMassBySpriteSize(pSprite); int baseChance = getBaseChanceModifier(0x2000);
		if (pSprite.extra >= 0) {
			XSPRITE pXSprite = xsprite[pSprite.extra];
			baseChance+=pXSprite.burnTime;
			if (dudeIsMelee(pXSprite)) 
				baseChance = 0x200;
		}

		
		int chance = ((baseChance / mass) << 7);
		return chance;
	}
	
	public static void dudeLeechOperate(SPRITE pSprite, XSPRITE pXSprite, int evCommand) {
		if (evCommand == kCommandOff) {
			actPostSprite(pSprite.xvel, kStatFree);
			return;
		}

		if(pXSprite.target >= 0 && pXSprite.target < kMaxSprites && pXSprite.stateTimer == 0) {
			SPRITE pTarget = sprite[pXSprite.target];

			if(IsDudeSprite(pTarget) && (pTarget.hitag & kAttrFree) == 0)
			{
				if(pTarget.extra > 0 && pTarget.extra < kMaxXSprites)
				{
					GetSpriteExtents(pSprite);
					DudeInfo pDudeInfo = dudeInfo[pTarget.lotag - kDudeBase];
					int dz = extents_zTop - pSprite.z - 256;
					long dist = engine.qdist(pTarget.x - pSprite.x, pTarget.y - pSprite.y);
					if(dist != 0) 
					{
						if(engine.cansee(pSprite.x, pSprite.y, extents_zTop, pSprite.sectnum, 
								pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum))
						{
							short oldang = pSprite.ang;
							long vel = divscale(dist, 0x1AAAAA, 12);
							pSprite.ang = engine.getangle(
								mulscale(vel, sprXVel[pTarget.xvel], 12) + pTarget.x - pSprite.x,
								mulscale(vel, sprYVel[pTarget.xvel], 12) + pTarget.y - pSprite.y);
							int eyeAboveZ = pTarget.z - (pDudeInfo.aimHeight * pTarget.yrepeat << 2) - extents_zTop - 256;
							int ax = Cos(pSprite.ang) >> 16;
							int ay = Sin(pSprite.ang) >> 16;
							int az = divscale(eyeAboveZ, dist, 10);
							int time = 12;
							int missileType = kMissileAltLeech1;
							if(pXSprite.data3 != 0) { //has ammo
								time = 36;
								missileType += 1;
							}

							SPRITE pMissile = actFireMissile(pSprite, 0, dz, ax, ay, az, missileType);
							if(pMissile != null)
							{
								pMissile.owner = pSprite.owner;
								pXSprite.stateTimer = 1;
								pXSprite.data3 = (short) ClipLow(pXSprite.data3 - 1, 0);
								evPostCallback(pSprite.xvel, SS_SPRITE, time, 20);
							}
							pSprite.ang = oldang;
						}
					}
				}
			}
		}

	}
	
	public static boolean ceilIsTooLow(SPRITE pSprite){
		if (pSprite != null) {
			
			SECTOR pSector = sector[pSprite.sectnum];
			int a = pSector.ceilingz - pSector.floorz;
			GetSpriteExtents(pSprite);
			int b = extents_zTop - extents_zBot;
			///System.err.println(a +" / "+ b);
			return a > b;
        }
		
		return false;
	}
	
	public static boolean doExplosion(SPRITE pSprite, int nType) {
		int nExplosion = actSpawnSprite(pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, kStatExplosion, true);
		int nSeq = 4; int nSnd = 304;
		
		EXPLODE pExpl = gExplodeData[nType];
		if (nExplosion >= 0) {
			
			SPRITE pExplosion = sprite[nExplosion];
			pExplosion.yrepeat = pExpl.size;
			pExplosion.xrepeat = pExpl.size;
			pExplosion.lotag = (short) (nType);
			pExplosion.cstat |= (short) kSpriteInvisible | kSpriteOriginAlign;
			pExplosion.owner = pSprite.xvel;
			
			if (pExplosion.extra >= 0) {
				xsprite[pExplosion.extra].target = 0;
				xsprite[pExplosion.extra].data1 = (short) pExpl.liveCount;
				xsprite[pExplosion.extra].data2 = (short) pExpl.quake;
				xsprite[pExplosion.extra].data3 = (short) pExpl.used3;
			
			
				if (nType == 0) { nSeq = 3; nSnd = 303; pExplosion.z = pSprite.z;}
				else if (nType == 2) { nSeq = 4; nSnd = 305; }
				else if (nType == 3) { nSeq = 9; nSnd = 307; }
				else if (nType == 4) { nSeq = 5; nSnd = 307; }
				else if (nType <= 6) { nSeq = 4; nSnd = 303; }
				else if (nType == 7) { nSeq = 4; nSnd = 303; }
				
				
				if (BuildGdx.cache.contains(nSeq, seq)) seqSpawn(nSeq, SS_SPRITE, pExplosion.extra, null);
				sfxStart3DSound(pExplosion, nSnd, -1, 0);
				
				return true;
			}
		}
		
		return false;
		
	}
	
	public static boolean canSwim(SPRITE pSprite) {
		return (BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 8,seq) && BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 13,seq)
				&& BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 17,seq));
	}
	
	
	public static boolean canDuck(SPRITE pSprite) {
		return (BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 8,seq) && BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 14,seq));
		
	}
	
	public static boolean CDCanMove(SPRITE pSprite) {
		return (BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 9,seq) && BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 13,seq)
				&& BuildGdx.cache.contains(xsprite[pSprite.extra].data2 + 14,seq));
		
	}
	
	public static boolean inDodge(AISTATE aiState) {
		return (aiState == GDXGenDudeDodgeDmg[WATER] || aiState == GDXGenDudeDodgeDmg[DUCK] || aiState == GDXGenDudeDodgeDmg[LAND]);
	}
	
	public static boolean inIdle(AISTATE aiState) { 
		return (aiState == GDXGenDudeIdle[WATER] || aiState == GDXGenDudeIdle[DUCK] || aiState == GDXGenDudeIdle[LAND]);
	}
	
	public static int getSeqStartId(XSPRITE pXSprite) {
		
		int seqStartId = dudeInfo[sprite[pXSprite.reference].lotag - kDudeBase].seqStartID;
		// Store seqStartId in data2 field
		if (pXSprite.data2 > 0) {
			seqStartId = pXSprite.data2;
			// check for full set of animations
			for (int i = seqStartId; i <= seqStartId + 19; i++){

				// exceptions
				switch(i - seqStartId){
					case 3:		// burning dude
					case 4: 	// electrocution
					case 8:		// attack u/d
					case 11: 	// reserved
					case 12:	// reserved
					case 13:	// move u
					case 14: 	// move d
					case 16:	// burning death 2
					case 17:	// idle w
					case 18:	// transformation in another dude
					case 19:	// reserved
						continue;
				}

				if (!BuildGdx.cache.contains(i, seq)) {
					game.GameWarning("No SEQ file with ID "+i+" found for custom dude #"+pXSprite.reference+"!\nData2 (SEQ Base): "+pXSprite.data2+"\nSwitching to default animation!");
					pXSprite.data2 = (short) dudeInfo[sprite[pXSprite.reference].lotag - kDudeBase].seqStartID;
					return pXSprite.data2;
				}
				
			}

		} else {
			pXSprite.data2 = (short) seqStartId;
		}
		
		return seqStartId;
	}

}
