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

package ru.m210projects.Blood;

import static ru.m210projects.Blood.Actor.*;

import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqKill;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqSpawn;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.AI.Ai.*;
import static ru.m210projects.Blood.Strings.seq;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.RotateVector;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trig.rotated;
import static ru.m210projects.Blood.Trigger.*;
import static ru.m210projects.Blood.VERSION.getPlayerSeq;
import static ru.m210projects.Blood.VERSION.kPlayerFatality;
import static ru.m210projects.Blood.View.viewSetMessage;
import static ru.m210projects.Blood.Weapon.LeechCallback;
import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.mulscaler;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Blood.PriorityQueue.BPriorityQueue;
import ru.m210projects.Blood.PriorityQueue.IPriorityQueue;
import ru.m210projects.Blood.PriorityQueue.JPriorityQueue;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;

public class EVENT {
	int index; 		//: 13; 	// object array index (sprite[], sector[], wall[])
	int type; 		//:  3;	// 0=sprite, 1=sector, 2=wall
	int to;       	//: 10; 	// objects with matching rxID will receive message
	int command; 	//:  6; 	// kCommandOn, kCommandOff, etc.

	public static final int kMaxChannels = 4096;
	public static final int kMaxID		 = 1024;

	public static final int kCommandOff			= 0;
	public static final int kCommandOn			= 1;
	public static final int kCommandState		= 2;
	public static final int kCommandToggle		= 3;
	public static final int kCommandNotState	= 4;
	public static final int kCommandLink		= 5;
	public static final int kCommandLock		= 6;
	public static final int kCommandUnlock		= 7;
	public static final int kCommandToggleLock	= 8;
	public static final int kCommandStopOff		= 9;
	public static final int kCommandStopOn		= 10;
	public static final int kCommandStopNext	= 11;
	public static final int kCommandCounter		= 12;
	public static final int kCommandRespawn		= 21;

	public static final int kCommandCallback 	= 20;
	public static final int kCallbackRespawn = 9;

	public static final int kCommandSpritePush = 30;
	public static final int kCommandSpriteImpact = 31;
	public static final int kCommandSpritePickup = 32;
	public static final int kCommandSpriteTouch = 33;
	public static final int kCommandSpriteSight = 34;
	public static final int kCommandSpriteProximity = 35;
	public static final int kCommandSpriteExplode = 36;

	public static final int kCommandSectorPush = 40;
	public static final int kCommandSectorImpact = 41;
	public static final int kCommandSectorEnter = 42;
	public static final int kCommandSectorExit = 43;

	public static final int kCommandWallPush = 50;
	public static final int kCommandWallImpact = 51;
	public static final int kCommandWallTouch = 52; // by NoOne: renamed from kCommandWallCross (see processTouchObjects())

	public static final int kGDXCommandPaste = 53; // used by some new types

	public static final int kCommandNumbered	= 64;

	public static final int kChannelNull = 0;
	public static final int kChannelSetupSecret = 1;
	public static final int kChannelSecret = 2;
	public static final int kChannelTextOver		= 3;
	public static final int kChannelEndLevelA		= 4;
	public static final int kChannelEndLevelB		= 5;

	public static final int kGDXChannelEndLevel = 6; // allows to select next map via kCommandNumbered

	public static final int kChannelTriggerStart	= 7;	// channel triggered at startup
	public static final int kChannelTriggerMatch	= 8;	// channel triggered at startup for BloodBath mode
	public static final int kChannelTriggerCoop		= 9;	// channel triggered at startup for Coop mode
	public static final int kChannelTriggerTeam		= 10;

	public static final int kChannelTeamADeath		= 15;
	public static final int kChannelTeamBDeath		= 16;

	public static final int kChannelFlag0Captured = 80;
	public static final int kChannelFlag1Captured = 81;

	public static final int kChannelRemoteFire1	= 90;
	public static final int	kChannelRemoteFire2 = 91;
	public static final int	kChannelRemoteFire3 = 92;
	public static final int	kChannelRemoteFire4 = 93;
	public static final int	kChannelRemoteFire5 = 94;
	public static final int	kChannelRemoteFire6 = 95;
	public static final int	kChannelRemoteFire7 = 96;
	public static final int	kChannelRemoteFire8 = 97;

	public static final int	kUserChannelStart		= 100;

	public static RXBUCKET[] rxBucket = new RXBUCKET[kMaxChannels];
	public static short[] bucketHead = new short[kMaxID + 1];
	public static IPriorityQueue eventQ;
	public static BPriorityQueue origEventQ;
	public static JPriorityQueue gdxEventQ;
	public static final int kPQueueSize = 1024;

	public static final CALLPROC[] gCallback = { //v1.21 ok
		/*0*/ new CALLPROC() { @Override
		public void run(int nIndex ) { FireEffect(nIndex); } },
		/*1*/ new CALLPROC() { @Override
		public void run(int nIndex ) { KillSpriteCallback(nIndex); } },
		/*2*/ new CALLPROC() { @Override
		public void run(int nIndex ) { FlareStarburstCallback(nIndex); } },
		/*3*/ new CALLPROC() { @Override
		public void run(int nIndex ) { FlareFireEffect(nIndex); } },
		/*4*/ new CALLPROC() { @Override
		public void run(int nIndex ) { FlareFireEffect2(nIndex); } }, //not used?
		/*5*/ new CALLPROC() { @Override
		public void run(int nIndex ) { ZombieOverHead(nIndex); } },
		/*6*/ new CALLPROC() { @Override
		public void run(int nIndex ) { BloodTrail(nIndex); } },
		/*7*/ new CALLPROC() { @Override
		public void run(int nIndex ) { LeechSparks(nIndex); } },
		/*8*/ new CALLPROC() { @Override
		public void run(int nIndex ) { SpawnSmoke(nIndex); } },
		/*9*/ new CALLPROC() { @Override
		public void run(int nIndex ) { RespawnCallback(nIndex); } },
		/*10*/ new CALLPROC() { @Override
		public void run(int nIndex ) { PlayerWaterBubblesCallback(nIndex); } },
		/*11*/ new CALLPROC() { @Override
		public void run(int nIndex ) { WaterBubblesCallback(nIndex); } },
		/*12*/ new CALLPROC() { @Override
		public void run(int nIndex ) { CounterCheck(nIndex); } },
		/*13*/ new CALLPROC() { @Override
		public void run(int nIndex ) { FatalityCallback(nIndex); } },
		/*14*/ new CALLPROC() { @Override
		public void run(int nIndex ) { BloodSplat(nIndex); } },
		/*15*/ new CALLPROC() { @Override
		public void run(int nIndex ) { AltTeslaEffect(nIndex); } },
		/*16*/ new CALLPROC() { @Override
		public void run(int nIndex ) { BulletFloor(nIndex); } },
		/*17*/ new CALLPROC() { @Override
		public void run(int nIndex ) { FlagCaptureCallback(nIndex); } },
		/*18*/ new CALLPROC() { @Override
		public void run(int nIndex ) { PodBloodTrail(nIndex); } },
		/*19*/ new CALLPROC() { @Override
		public void run(int nIndex ) { PodBloodSplat(nIndex); } },
		/*20*/ new CALLPROC() { @Override
		public void run(int nIndex ) { LeechCallback(nIndex); } },
		/*21*/ new CALLPROC() { @Override
		public void run(int nIndex ) { } }, //not used, weapon callback
		/*22*/ new CALLPROC() { @Override
		public void run(int nIndex ) { gdxFragLimitCallback(nIndex); } }, //GDX FragLimit callback
		/*23*/ new CALLPROC() { @Override
		public void run(int nIndex ) { UniMissileBurstCallback(nIndex); } }, // by NoOne: similar to startBurstFlare, but for all missiles
		/*24*/ new CALLPROC() { @Override
		public void run(int nIndex ) { makeMissileBlocking(nIndex); } }, // by NoOne: also required for bursting missiles

	};
	public static final int kCallbackMax = 25;

	public static void makeMissileBlocking(int nSprite) {
		if(!(nSprite >= 0 && nSprite < kMaxSprites)) game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		sprite[nSprite].cstat |= kSpriteBlocking;
	}

	public static void UniMissileBurstCallback(int nSprite) {
		if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		int nAngle = engine.getangle((int)sprXVel[nSprite], (int)sprYVel[nSprite]);
		int nVel = 0x55555;
		SPRITE pSprite = sprite[nSprite];
		for (int i = 0; i < 8; i++)
		{
			SPRITE pBurst = actCloneSprite(pSprite, kStatMissile);
			pBurst.lotag = pSprite.lotag;
			pBurst.shade = pSprite.shade;
			pBurst.picnum = pSprite.picnum;

			pBurst.cstat = pSprite.cstat;
			if ((pBurst.cstat & kSpriteBlocking) != 0) {
				pBurst.cstat &= ~kSpriteBlocking; // we don't want missiles impact each other
				evPostCallback(pBurst.xvel, SS_SPRITE, 100, 24); // so set blocking flag a bit later
			}

			pBurst.pal = pSprite.pal;
			pBurst.xrepeat = (short) (pSprite.xrepeat / 2);
			pBurst.yrepeat = (short) (pSprite.yrepeat / 2);
			pBurst.clipdist = pSprite.clipdist / 2;
			pBurst.hitag = pSprite.hitag;
			pBurst.ang = (short) ((pSprite.ang + gMissileData[pSprite.lotag - kMissileBase].angleOfs) & kAngleMask);
			pBurst.owner = pSprite.owner;

			actBuildMissile(pBurst,pBurst.extra,pSprite);

			int dxVel = 0;
			int dyVel = mulscaler(nVel, Sin(i * kAngle360 / 8), 30);
			int dzVel = mulscaler(nVel, -Cos(i * kAngle360 / 8), 30);

			if ( (i & 1) != 0 )
		    {
				dyVel >>= 1;
				dzVel >>= 1;
		    }

			RotateVector(dxVel, dyVel, nAngle);
			sprXVel[pBurst.xvel] += (int) rotated.x;
			sprYVel[pBurst.xvel] += (int) rotated.y;
			sprZVel[pBurst.xvel] += dzVel;

			evPostCallback(pBurst.xvel, SS_SPRITE, 960, 1);
		}
		evPostCallback(nSprite, SS_SPRITE, 0, 1);
	}

	public static XSPRITE getNextIncarnation(XSPRITE pXSprite) {
		if (pXSprite.txID <= 0) return null;
		for (int i = bucketHead[pXSprite.txID]; i < bucketHead[pXSprite.txID + 1]; i++){
			if (rxBucket[i].type != SS_SPRITE) continue;
			else if (IsDudeSprite(sprite[rxBucket[i].index]) && sprite[rxBucket[i].index].statnum == kStatInactive)
				return xsprite[sprite[rxBucket[i].index].extra];
		}
		return null;
	}


	public static void gdxFragLimitCallback(int nIndex)
	{
		levelEndLevel(0);
	}

	public static void FlagCaptureCallback(int nIndex)
	{
		SPRITE pFlag = sprite[nIndex];
		if ( pFlag.owner >= 0 && pFlag.owner < 4096 )
		{
		    SPRITE pOwner = sprite[pFlag.owner];
		    int type = pFlag.lotag;
		    XSPRITE pXBase = xsprite[pOwner.extra];

		    if ( type == kItemBlueFlag )
		    {
		        trTriggerSprite(pOwner.xvel, pXBase, 1);
		        sndStartSample(8003, 255, 2, false);
		        viewSetMessage("Blue Flag returned to base.", -1, 10);
		    }

		    if ( type == kItemRedFlag )
		    {
		    	trTriggerSprite(pOwner.xvel, pXBase, 1);
		        sndStartSample(8002, 255, 2, false);
		        viewSetMessage("Red Flag returned to base.", -1, 7);
		    }
		}
		evPostCallback(nIndex, SS_SPRITE, 0, 1);
	}

	public static void SpawnSmoke(int nIndex) {
		if ( sprZVel[nIndex] != 0 ) {
			SPRITE pSprite = sprite[nIndex];
			int radius = (engine.getTile(pSprite.picnum).getWidth() / 2) * pSprite.xrepeat >> 2;

			SPRITE pEffect = actSpawnEffect(7, pSprite.sectnum,
					pSprite.x + mulscale(radius, Cos(pSprite.ang - kAngle90), 30),
					pSprite.y + mulscale(radius, Sin(pSprite.ang - kAngle90), 30),
					pSprite.z, 0);

			if ( pEffect != null )
			{
			    sprXVel[pEffect.xvel] = sprXVel[nIndex];
			    sprYVel[pEffect.xvel] = sprYVel[nIndex];
			    sprZVel[pEffect.xvel] = sprZVel[nIndex];
			}
		}
		if(IsOriginalDemo() || eventQ.getSize() < 768)
			evPostCallback(nIndex, SS_SPRITE, 12, 8);
	}

	public static void KillSpriteCallback(int nIndex) {
		checkEventList(nIndex, SS_SPRITE);
		if(sprite[nIndex].extra > 0)
			seqKill(SS_SPRITE, sprite[nIndex].extra);
		sfxKill3DSound(sprite[nIndex], -1, -1);
		deletesprite( (short) nIndex );
	}

	public static void FlareFireEffect(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		SPRITE pEffect = actSpawnEffect(28, pSprite.sectnum,
				pSprite.x,
				pSprite.y,
				pSprite.z, 0);

		if ( pEffect != null )
		{
		    sprXVel[pEffect.xvel] = sprXVel[nIndex] + BiRandom(109226);
		    sprYVel[pEffect.xvel] = sprYVel[nIndex] + BiRandom(109226);
		    sprZVel[pEffect.xvel] = sprZVel[nIndex] - Random(109226);
		}
		evPostCallback(nIndex, SS_SPRITE, 4, 3);
	}

	public static void FlareFireEffect2(int nIndex) {
		System.out.println("gCallback 4");
		SPRITE pSprite = sprite[nIndex];
		SPRITE pEffect = actSpawnEffect(28, pSprite.sectnum,
				pSprite.x,
				pSprite.y,
				pSprite.z, 0);
		if ( pEffect != null )
		{
		    sprXVel[pEffect.xvel] = sprXVel[nIndex] + BiRandom(109226);
		    sprYVel[pEffect.xvel] = sprYVel[nIndex] + BiRandom(109226);
		    sprZVel[pEffect.xvel] = sprZVel[nIndex] - Random(109226);
		}
		evPostCallback(nIndex, SS_SPRITE, 12, 4);
	}

	public static void AltTeslaEffect(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		SPRITE pEffect = actSpawnEffect(49, pSprite.sectnum,
				pSprite.x,
				pSprite.y,
				pSprite.z, 0);
		if ( pEffect != null )
		{
		    sprXVel[pEffect.xvel] = sprXVel[nIndex] + BiRandom(109226);
		    sprYVel[pEffect.xvel] = sprYVel[nIndex] + BiRandom(109226);
		    sprZVel[pEffect.xvel] = sprZVel[nIndex] - Random(109226);
		}

		if(IsOriginalDemo() || eventQ.getSize() < 768)
			evPostCallback(nIndex, SS_SPRITE, 3, 15);
	}

	public static void FatalityCallback(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		XSPRITE pXSprite = xsprite[pSprite.extra];
		if ( checkPlayerSeq(gPlayer[pSprite.lotag - kDudePlayer1], getPlayerSeq(kPlayerFatality)) )
		{
		    if ( pXSprite.target == gMe.nSprite ) {
		    	sndStartSample(3313, -1, 2, false);
		    }
		}
	}

	public static void WaterBubblesCallback(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		GetSpriteExtents(pSprite);
		for ( int i = 0; i < klabs(sprZVel[nIndex]) >> 18; i++ )
		{
			int radius = pSprite.xrepeat * (engine.getTile(pSprite.picnum).getWidth() / 2) >> 2;
			int nAngle = Random(2048);
			int dx = mulscale(Cos(nAngle), radius, 30);
			int dy = mulscale(Sin(nAngle), radius, 30);

			int range = extents_zBot - Random(extents_zBot - extents_zTop);

			SPRITE pEffect = actSpawnEffect(Random(3) + 23,pSprite.sectnum, pSprite.x + dx, pSprite.y + dy, range, 0);
			if(pEffect != null) {
				sprXVel[pEffect.xvel] = sprXVel[nIndex] + BiRandom(109226);
			    sprYVel[pEffect.xvel] = sprYVel[nIndex] + BiRandom(109226);
			    sprZVel[pEffect.xvel] = sprZVel[nIndex] + BiRandom(109226);
			}
		}

		if(IsOriginalDemo() || eventQ.getSize() < 768)
			evPostCallback(nIndex, SS_SPRITE, 4, 11);
	}

	public static void LeechSparks(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		SPRITE pEffect = actSpawnEffect(15, pSprite.sectnum,
				pSprite.x,
				pSprite.y,
				pSprite.z, 0);
		if ( pEffect != null )
		{
		    sprXVel[pEffect.xvel] = BiRandom(65536) + sprXVel[nIndex];
		    sprYVel[pEffect.xvel] = BiRandom(65536) + sprYVel[nIndex];
		    sprZVel[pEffect.xvel] = sprZVel[nIndex] - Random(0x1AAAA);
		}
		evPostCallback(nIndex, SS_SPRITE, 3, 7);
	}

	public static void RespawnCallback(int nSprite) {
		SPRITE pSprite = sprite[nSprite];
		int nXSprite = pSprite.extra;
		if(!(nXSprite > 0 && nXSprite < kMaxXSprites)) game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
		XSPRITE pXSprite = xsprite[nXSprite];
		if ( pSprite.statnum != kStatRespawn && pSprite.statnum != kStatThing )
			Console.Println("Sprite " + nSprite + " is not on Respawn list", OSDTEXT_RED);
		if ( (pSprite.hitag & kAttrRespawn) == 0 )
		  	Console.Println("Sprite " + nSprite + " does not have the respawn attribute", OSDTEXT_RED);
		int respawnTime = 0;
		switch(pXSprite.respawnPending) {
			case 1: //kRespawnRed
			    respawnTime = mulscale(actGetRespawnTime(pSprite), 16384, 16);
				pXSprite.respawnPending = 2;
				evPostCallback(nSprite, SS_SPRITE, respawnTime, kCallbackRespawn);
				return;
			case 2: //kRespawnYellow
			    respawnTime = mulscale(actGetRespawnTime(pSprite), 8192, 16);
				pXSprite.respawnPending = 3;
				evPostCallback(nSprite, SS_SPRITE, respawnTime, kCallbackRespawn);
			    return;
			case 3: //kRespawnGreen
				if(pSprite.owner == kStatRespawn)
					game.dassert("pSprite.owner != kStatRespawn");
				changespritestat((short)nSprite, pSprite.owner);
				pSprite.owner = -1;
				pSprite.lotag = pSprite.zvel;
				pSprite.hitag &= ~kAttrRespawn;
				sprZVel[nSprite] = 0;
			    sprYVel[nSprite] = 0;
			    sprXVel[nSprite] = 0;

			    pXSprite.respawnPending = 0;
			    pXSprite.isTriggered = false;
			    pXSprite.burnTime = 0;

			    if(IsDudeSprite(pSprite)) {
			        pXSprite.key = 0;

			        pSprite.x = (int) ksprite[nSprite].x;
			        pSprite.y = (int) ksprite[nSprite].y;
			        pSprite.z = (int) ksprite[nSprite].z;
			        pSprite.cstat |= 0x1101;
			        int nType = pSprite.lotag;
			        pSprite.clipdist = dudeInfo[nType - kDudeBase].clipdist;
			        pXSprite.health = dudeInfo[nType - kDudeBase].startHealth << 4;

					if ((BuildGdx.cache.contains(dudeInfo[nType - kDudeBase].seqStartID + kSeqDudeIdle, seq)))
						seqSpawn(dudeInfo[nType - kDudeBase].seqStartID + kSeqDudeIdle,
								SS_SPRITE, pSprite.extra, null);
			        aiInit(pSprite, IsOriginalDemo());
			    }

			    if ( pSprite.lotag == 400 )
			    {
			    	pSprite.cstat |= (kSpriteBlocking | kSpriteHitscan);
			        pSprite.cstat &= ~kSpriteInvisible;
			    }
			    actSpawnEffect(29, pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 0);
			    sfxStart3DSound(pSprite, 350, -1, 0);
				return;
		}

		Console.Println("Unexpected respawnPending value = " + pXSprite.respawn);
	}

	public static void ZombieOverHead(int nSprite) {
		if(!(nSprite >= 0 && nSprite < kMaxSprites))
			game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		SPRITE pSprite = sprite[nSprite];
		int nXSprite = pSprite.extra;
		if(!(nXSprite > 0 && nXSprite < kMaxXSprites))
			game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
		XSPRITE pXSprite = xsprite[nXSprite];
		GetSpriteExtents(pSprite);
		SPRITE pEffect = actSpawnEffect(27, pSprite.sectnum, pSprite.x, pSprite.y, extents_zTop, 0);
		if(pEffect != null) {
			sprXVel[pEffect.xvel] = sprXVel[nSprite] + BiRandom(69905);
		    sprYVel[pEffect.xvel] = sprYVel[nSprite] + BiRandom(69905);
		    sprZVel[pEffect.xvel] = sprZVel[nSprite] - 436906;
		}

		if ( pXSprite.data1 <= 0 )
		{
			if ( pXSprite.data2 > 0 )
		    {
		    	evPostCallback(nSprite, SS_SPRITE, 60, 5);
		    	pXSprite.data1 = 40;
		      	pXSprite.data2--;
		    }
		}
		else
		{
		    evPostCallback(nSprite, SS_SPRITE, 4, 5);
		    pXSprite.data1 -= kFrameTicks;
		}
	}

	public static void FireEffect(int nSprite) {
		SPRITE pSprite = sprite[nSprite];
		GetSpriteExtents(pSprite);
		for(int i = 0; i < 3; i++) {
			int size = pSprite.xrepeat * (engine.getTile(pSprite.picnum).getWidth() / 2) >> 3;
			int nAngle = Random(2048);
			int dx = mulscale(Cos(nAngle), size, 30);
			int dy = mulscale(Sin(nAngle), size, 30);

			SPRITE pEffect = actSpawnEffect(32,pSprite.sectnum, pSprite.x + dx, pSprite.y + dy,
					extents_zBot - Random(extents_zBot - extents_zTop), 0);
			if(pEffect != null) {
				sprXVel[pEffect.xvel] = sprXVel[nSprite] + BiRandom(-dx);
			    sprYVel[pEffect.xvel] = sprYVel[nSprite] + BiRandom(-dy);
			    sprZVel[pEffect.xvel] = sprZVel[nSprite] - Random(109226);
			}
		}
		if(pSprite != null && pSprite.extra > 0 && xsprite[pSprite.extra].burnTime != 0)
			evPostCallback(nSprite, SS_SPRITE, 5, 0);
	}

	public static void FlareStarburstCallback(int nSprite) {
		if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		int nAngle = engine.getangle((int)sprXVel[nSprite], (int)sprYVel[nSprite]);
		int nVel = 0x55555;
		SPRITE pSprite = sprite[nSprite];

		for (int i = 0; i < 8; i++)
		{
			SPRITE pBurst = actCloneSprite( pSprite, kStatMissile );

			pBurst.picnum = 2424;
			pBurst.shade = -128;
			pBurst.yrepeat = 32;
			pBurst.lotag = kMissileStarburstFlare;
			pBurst.clipdist = 2;
			pBurst.owner = pSprite.owner;
			pBurst.xrepeat = pBurst.yrepeat;

			int dxVel = 0;
			int dyVel = mulscaler(nVel, Sin(i * kAngle360 / 8), 30);
			int dzVel = mulscaler(nVel, -Cos(i * kAngle360 / 8), 30);

			if ( (i & 1) != 0 )
		    {
				dyVel >>= 1;
				dzVel >>= 1;
		    }

			RotateVector(dxVel, dyVel, nAngle);
			sprXVel[pBurst.xvel] += (int) rotated.x;
			sprYVel[pBurst.xvel] += (int) rotated.y;
			sprZVel[pBurst.xvel] += dzVel;

			evPostCallback(pBurst.xvel, SS_SPRITE, 960, 1);
		}
		evPostCallback(nSprite, SS_SPRITE, 0, 1);
	}

	public static void BulletFloor(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		GetZRange(pSprite, pSprite.clipdist, CLIPMASK0);

		GetSpriteExtents(pSprite);

		pSprite.z += (gz_floorZ - extents_zBot);
		int velocity = (int) (sprZVel[nIndex] - floorVel[pSprite.sectnum]);
		if(velocity <= 0) {
			if ( sprZVel[nIndex] != 0 )
				return;

			updateBullet(pSprite);
			return;
		}
		GravityVector(sprXVel[nIndex], sprYVel[nIndex], velocity, pSprite.sectnum, 36864);
		sprXVel[nIndex] = refl_x;
		sprYVel[nIndex] = refl_y;
		sprZVel[nIndex] = refl_z;

		if ( floorVel[pSprite.sectnum] == 0 )
		{
			if ( klabs( sprZVel[nIndex] ) < 0x20000 ) {
				updateBullet(pSprite);
		    	return;
			}
		}

		int nChannel = (pSprite.xvel & 2) + 28;
    	if(nChannel > 31)
    		game.dassert("nChannel < 32");

    	pSprite.ang += (vRandom() & kAngleMask);

		if(pSprite.lotag < 37 || pSprite.lotag > 39) { //40, 41 - shotgun shells
			int snd = 612;
			if(Chance(0x4000))
				snd = 610;
			sfxStart3DSound(pSprite, snd, nChannel, 1);
		} else { //37, 38, 39 - tommygun shells
			bRandom();
			sfxStart3DSound(pSprite, Random(2) + 608, nChannel, 1);
		}
	}

	public static void updateBullet(SPRITE pSprite) {
		sprZVel[pSprite.xvel] = 0;
		sprYVel[pSprite.xvel] = 0;
		sprXVel[pSprite.xvel] = 0;
		if ( pSprite.extra > 0 )
			seqKill(SS_SPRITE, pSprite.extra);
		sfxKill3DSound(pSprite, -1, -1);

		if ( pSprite.lotag < 37 || pSprite.lotag > 39 ) {
			pSprite.picnum = 2464;
		} else {
			pSprite.picnum = 2465;
		}
		pSprite.lotag = 51;
		pSprite.yrepeat = 10;
		pSprite.xrepeat = pSprite.yrepeat;
		return;
	}

	public static void BloodSplat(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		GetZRange(pSprite, pSprite.clipdist, CLIPMASK0);

		GetSpriteExtents(pSprite);

		pSprite.z += (gz_floorZ - extents_zBot);

		int nAngle = Random(2048);
		int Dist = Random(16) << 4;
		int x = pSprite.x + mulscale(Cos(nAngle), Dist, 28);
		int y = pSprite.y + mulscale(Sin(nAngle), Dist, 28);

		actSpawnEffect(48, pSprite.sectnum, x, y, pSprite.z, 0);
		if(pSprite.ang == 1024)
		{
		    int nChannel = (pSprite.xvel & 2) + 28;
		    if(nChannel >= 32)
	    		game.dassert("nChannel < 32");
		    sfxStart3DSound(pSprite, BLUDSPLT, nChannel, 1);
		}

		if ( Chance(10240) )
		{
		    SPRITE pSpawn = actSpawnEffect(36, pSprite.sectnum, x, y, gz_floorZ - 64, 0);
		    if ( pSpawn != null ) {
		    	if(!IsOriginalDemo() && pSprite.sectnum != pSpawn.sectnum)
		    		pSpawn.z = engine.getflorzofslope(pSpawn.sectnum, x, y) - 64;
		    	pSpawn.ang = (short) nAngle;
		    }
		}
		actDeleteEffect2(nIndex);
	}

	public static void PodBloodSplat(int nIndex) {
		SPRITE pSprite = sprite[nIndex];
		GetZRange(pSprite, pSprite.clipdist, CLIPMASK0);

		GetSpriteExtents(pSprite);

		pSprite.z += (gz_floorZ - extents_zBot);

		int nAngle = Random(2048);
		int Dist = Random(16) << 4;
		int x = pSprite.x + mulscale(Cos(nAngle), Dist, 28);
		int y = pSprite.y + mulscale(Sin(nAngle), Dist, 28);

		if(pSprite.ang == 1024 && pSprite.lotag == 53)
		{
		    int nChannel = (pSprite.xvel & 2) + 28;
		    if(nChannel > 31)
	    		game.dassert("nChannel < 32");
		    sfxStart3DSound(pSprite, BLUDSPLT, nChannel, 1);
		}

		if(pSprite.lotag != 53 && pSprite.lotag != kThingPodGreen) {
			SPRITE pSpawn = actSpawnEffect(32, pSprite.sectnum, x, y, gz_floorZ - 64, 0);
		    if ( pSpawn != null ) {
		    	if(pSprite.sectnum != pSpawn.sectnum)
		    		pSpawn.z = engine.getflorzofslope(pSpawn.sectnum, x, y) - 64;
		    	pSpawn.ang = (short) nAngle;
		    }

		} else
		{
			if ( Chance(640) || pSprite.lotag == kThingPodGreen ) {
				SPRITE pSpawn = actSpawnEffect(55, pSprite.sectnum, x, y, gz_floorZ - 64, 0);
				if ( pSpawn != null ) {
					if(pSprite.sectnum != pSpawn.sectnum)
						pSpawn.z = engine.getflorzofslope(pSpawn.sectnum, x, y) - 64;
					pSpawn.ang = (short) nAngle;
				}
			}
		}

		actDeleteEffect2(nIndex);
	}

	public static void CounterCheck(int nSector)
	{
		if(!(nSector >= 0 && nSector < kMaxSectors)) game.dassert("nSector >= 0 && nSector < kMaxSectors");
		SECTOR pSector = sector[nSector];
		// remove check below, so every sector can be counter if command 12 (this callback) received.
		// besides, it's useless anyway.
		//if ( pSector.lotag != kSectorCounter ) return;
		int nXSector = pSector.extra;
		if ( nXSector > 0 )
		{
			XSECTOR pXSector = xsector[nXSector];
			int data = pXSector.data;
			int waitTime = pXSector.waitTime[1];
			if (data != 0 && waitTime != 0)
			{
				int cnt = 0;
				for (short nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
				{
					if ( sprite[nSprite].lotag == data )
						++cnt;
				}
				if ( cnt < waitTime )
					evPostCallback(nSector, SS_SECTOR, 5, kCommandCounter);
				else
				{
					//pXSector.waitTime[1] = 0; // do not reset necessary objects counter to zero
					trTriggerSector(nSector, pXSector, kCommandOn);
					//Println("HEHE");

					pXSector.locked = 1; //lock sector, so it can be opened again later
				}
			}
		}
	}

	public static void BloodTrail( int nSprite )
	{
		SPRITE pSprite = sprite[nSprite];
		SPRITE pEffect = actSpawnEffect(27, pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 0);
		if ( pEffect != null )
		{
			pEffect.ang = 0;
		    sprXVel[pEffect.xvel] = sprXVel[nSprite] >> 8;
		    sprYVel[pEffect.xvel] = sprYVel[nSprite] >> 8;
		    sprZVel[pEffect.xvel] = sprZVel[nSprite] >> 8;
		}
		evPostCallback(nSprite, SS_SPRITE, 6, 6);
	}

	public static void PodBloodTrail( int nSprite )
	{
		SPRITE pSprite = sprite[nSprite];

		int type = 53;
		if(pSprite.lotag != 53)
			type = 54;

		SPRITE pEffect = actSpawnEffect(type, pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 0);
		if ( pEffect != null )
		{
			pEffect.ang = 0;
		    sprXVel[pEffect.xvel] = sprXVel[nSprite] >> 8;
		    sprYVel[pEffect.xvel] = sprYVel[nSprite] >> 8;
		    sprZVel[pEffect.xvel] = sprZVel[nSprite] >> 8;
		}
		evPostCallback(nSprite, SS_SPRITE, 6, 18);
	}

	public static int getEvent(int index, int type, int command, int funcId) {
		return ((index & 0x1FFF) | (type & 7) << 13 | (command << 16) | (funcId << 24));
	}

	public static int getIndex(int event) {
		return (event & 0x1FFF);
	}

	public static int getType(int event) {
		return (event & 0xE000) >> 13;
	}

	public static int getCommand(int event) {
		return (event & 0xFF0000) >> 16;
	}

	public static int getFuncID(int event) {
		return event >> 24;
	}

	static int GetBucketChannel( RXBUCKET pBucket )
	{
		int nXIndex;
		switch (pBucket.type)
		{
			case SS_SECTOR:
				nXIndex = sector[pBucket.index].extra;
				if(nXIndex <= 0) game.dassert("Sector nXIndex > 0");
				return xsector[nXIndex].rxID;

			case SS_WALL:
				nXIndex = wall[pBucket.index].extra;
				if(nXIndex <= 0) game.dassert("Wall nXIndex > 0");
				return xwall[nXIndex].rxID;

			case SS_SPRITE:
				nXIndex = sprite[pBucket.index].extra;
				if(nXIndex <= 0) game.dassert("Sprite nXIndex > 0");
				return xsprite[nXIndex].rxID;
		}

		Console.Println("Unexpected rxBucket type " + pBucket.type, OSDTEXT_RED);
		return 0;
	}

	public static int CompareChannels( RXBUCKET ref1, RXBUCKET ref2 )
	{
		return GetBucketChannel(ref1) - GetBucketChannel(ref2);
	}

	public static void evInit(boolean isOriginal) {
		int i, j;
		int nCount = 0;

		if(origEventQ == null)
			origEventQ = new BPriorityQueue(kPQueueSize);
		if(gdxEventQ == null)
			gdxEventQ = new JPriorityQueue(kPQueueSize);

		if(isOriginal)
			eventQ = origEventQ;
		else eventQ = gdxEventQ;

		for (i = 0; i < kMaxChannels; i++)
		{
			if(rxBucket[i] == null)
				rxBucket[i] = new RXBUCKET();
			else rxBucket[i].flush();
		}

		eventQ.flush();

		// add all the tags to the bucket array
		for (i = 0; i < numsectors; i++)
		{
			if(sector[i] == null)
				continue;

			int nXSector = sector[i].extra;
			if (nXSector > 0 && xsector[nXSector].rxID > 0)
			{
				if(nCount >= kMaxChannels)
					game.dassert("nCount < kMaxChannels");
				rxBucket[nCount].type = SS_SECTOR;
				rxBucket[nCount].index = i;
				nCount++;
			}
		}

		for (i = 0; i < numwalls; i++)
		{
			if(wall[i] == null)
				continue;

			int nXWall = wall[i].extra;
			if (nXWall > 0 && xwall[nXWall].rxID > 0)
			{
				if(nCount >= kMaxChannels)
					game.dassert("nCount < kMaxChannels");
				rxBucket[nCount].type = SS_WALL;
				rxBucket[nCount].index = i;
				nCount++;
			}
		}

		for (i = 0; i < kMaxSprites; i++)
		{
			if (sprite[i].statnum < kMaxStatus)
			{
				int nXSprite = sprite[i].extra;
				if (nXSprite > 0 && xsprite[nXSprite].rxID > 0)
				{
					if(nCount >= kMaxChannels)
						game.dassert("nCount < kMaxChannels");
					rxBucket[nCount].type = SS_SPRITE;
					rxBucket[nCount].index = i;
					nCount++;
				}
			}
		}

		// sort the array on rx tags
//		Arrays.sort(rxBucket, 0, nCount);

		qsort(nCount);

		// create the list of header indices
		j = 0;
		for (i = 0; i < kMaxID; i++)
		{
			bucketHead[i] = (short)j;
			while ( j < nCount && GetBucketChannel(rxBucket[j]) == i) {
				j++;
			}
		}
		bucketHead[i] = (short)j;
	}

	public static boolean evGetSourceState( int type, int nIndex )
	{
		int nXIndex;

		switch ( type )
		{
			case SS_SECTOR:
				nXIndex = sector[nIndex].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXSectors)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSectors");
				return xsector[nXIndex].state != 0;

			case SS_WALL:
				nXIndex = wall[nIndex].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXWalls)) game.dassert("nXIndex > 0 && nXIndex < kMaxXWalls");
				return xwall[nXIndex].state != 0;

			case SS_SPRITE:
				nXIndex = sprite[nIndex].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXSprites)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSprites");
				return xsprite[nXIndex].state != 0;
		}

		// shouldn't reach this point
		return false;
	}

	public static void evSend( int index, int type, int to, int command )
	{
		if ( command == kCommandState )
			command = evGetSourceState(type, index) ? kCommandOn : kCommandOff;
		else if ( command == kCommandNotState )
			command = evGetSourceState(type, index) ? kCommandOff : kCommandOn;

		int pEvent = getEvent(index, type, command, 0);

		// handle transmit-only system triggers
		if (to > kChannelNull)
		{
			switch(to)
			{
			case kChannelSetupSecret:
				if (command < kCommandNumbered)
					Console.Println("Invalid SetupSecret command by xobject " + index, OSDTEXT_RED);
				levelSetupSecret(command - kCommandNumbered);
				break;
			case kChannelSecret:
				if (command < kCommandNumbered)
					Console.Println("Invalid Secret command by xobject " + index, OSDTEXT_RED);
				levelCountSecret(command - kCommandNumbered);
				break;
			case kChannelEndLevelA:
				levelEndLevel(0);
				// Hooray.  You finished the level.
				return;
			case kChannelEndLevelB:
				levelEndLevel(1);
				// Hooray.  You finished the level via the secret ending.
				return;
			case kGDXChannelEndLevel:
				// finished level and load custom level � via kCommandNumbered.
				levelEndLevelCustom(command - kCommandNumbered);
				return;
			case kChannelTextOver:
				if (command < kCommandNumbered) {
					Console.Println("Invalid TextOver command by xobject " + index, OSDTEXT_RED);
					viewSetMessage("Invalid TextOver command", -1, 7);
					return;
				}
				trTextOver(command - kCommandNumbered);
				return;

			case kChannelRemoteFire1:
			case kChannelRemoteFire2:
			case kChannelRemoteFire3:
			case kChannelRemoteFire4:
			case kChannelRemoteFire5:
			case kChannelRemoteFire6:
			case kChannelRemoteFire7:
			case kChannelRemoteFire8:
				// these can't use the rx buckets since they are dynamically created
				for ( int nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite] )
			    {
					SPRITE pSprite = sprite[nSprite];
					if ( (pSprite.hitag & kAttrFree) == 0 )
				    {
				    	if ( pSprite.extra > 0 )
				        {
				    		if ( xsprite[pSprite.extra].rxID == to )
				    			trMessageSprite(nSprite, pEvent);
				        }
				    }
			    }
				return;
			case kChannelFlag0Captured:
			case kChannelFlag1Captured:
				for ( int nSprite = headspritestat[kStatItem]; nSprite >= 0; nSprite = nextspritestat[nSprite] )
			    {
					SPRITE pSprite = sprite[nSprite];
					if ( (pSprite.hitag & kAttrFree) == 0 )
				    {
				    	if ( pSprite.extra > 0 )
				        {
				    		if ( xsprite[pSprite.extra].rxID == to )
				    			trMessageSprite(nSprite, pEvent);
				        }
				    }
			    }
				return;
			}
		}

		// the event is a broadcast message
		for ( int i = bucketHead[to]; i < bucketHead[to + 1]; i++ )
		{
			// don't send it to the originator
			if ( rxBucket[i].type == getType(pEvent) && rxBucket[i].index == getIndex(pEvent) )
				continue;

			switch ( rxBucket[i].type )
			{
				case SS_SECTOR:
					trMessageSector( rxBucket[i].index, pEvent );
					break;

		 		case SS_WALL:
		  			trMessageWall( rxBucket[i].index, pEvent );
					break;

				case SS_SPRITE:
					SPRITE pSprite = sprite[rxBucket[i].index];
					if((pSprite.hitag & kAttrFree) == 0 && pSprite.extra > 0 && xsprite[pSprite.extra].rxID != 0)
						trMessageSprite( rxBucket[i].index, pEvent );
					break;

				default:
		 			break;
			}
		}
	}

	public static void checkEventList(int nIndex, int nType) {
		eventQ.checkList(nIndex, nType);
	}

	public static void checkEventList(int nIndex, int nType, int funcId) {
		eventQ.checkList(nIndex, nType, funcId);
	}

	public static void evPost( int index, int type, long time, int command )
	{
		if(command == kCommandCallback)
			game.dassert("command != kCommandCallback");

		if ( command == kCommandState )
			command = evGetSourceState(type, index) ? kCommandOn : kCommandOff;
		else if ( command == kCommandNotState )
			command = evGetSourceState(type, index) ? kCommandOff : kCommandOn;

		int pEvent = getEvent(index, type, command, 0);
		eventQ.Insert(gFrameClock + time, pEvent);
	}

	public static void evPostCallback( int index, int type, long time, int funcId )
	{
		int pEvent = getEvent(index, type, kCommandCallback, funcId);
		eventQ.Insert(gFrameClock + time, pEvent);
	}

	public static void evProcess( long time )
	{
		// while there are events to be processed
		while ( eventQ.Check(time) )
		{
			int event = eventQ.Remove();
			if(event == 0) continue;

			// is it a special callback event?
			if (getCommand(event) == kCommandCallback) {
				int nFunc = getFuncID(event);
				if(nFunc >= kCallbackMax)
					game.dassert("event.funcID < kCallbackMax");
				if(gCallback[nFunc] == null)
					game.dassert("gCallback[event.funcID] != null");
				gCallback[nFunc].run(getIndex(event));
				continue;
			}

		 	switch (getType(event) )
			{
				case SS_SECTOR:
		 			trMessageSector( getIndex(event), event );
					break;

		 		case SS_WALL:
		  			trMessageWall( getIndex(event), event );
					break;

				case SS_SPRITE:
		 			trMessageSprite( getIndex(event), event );
					break;
			}
		}
	}

	private static void qsort(int n) {
		int base = 0, sp = 0;

		int[] base_stack = new int[4 * 8];
		int[] n_stack = new int[4 * 8];

		while(true) {
        	while( n > 1 ) {
        		if( n < 16 ) {
        			for( int shell = 3; shell > 0; shell -= 2 ) {
        				for(int p1 = base + shell; p1 < base + n; p1 += shell ) {
        					for( int p2 = p1;
        						p2 > base && CompareChannels( rxBucket[p2 - shell], rxBucket[p2] ) > 0;
        						p2 -= shell ) {
        						swap( p2, p2 - shell );
                           }
                       }
        			}
        			break;
                } else {
                    int mid = base + (n >> 1);
                    if( n > 29 ) {
                        int p1 = base;
                        int p2 = base + ( n - 1 );
                        if( n > 42 ) {
                            int s = (n >> 3);
                            p1  = med3( p1, p1 + s, p1 + (s << 1) );
                            mid = med3( mid - s, mid, mid + s );
                            p2  = med3( p2 - (s << 1), p2 - s, p2 );
                        }
                        mid = med3( p1, mid, p2 );
                    }

                    RXBUCKET pv = rxBucket[mid];

                    int pa, pb, pc, comparison;
                    pa = pb = base;
                    int pd = base + ( n - 1 );

                    for( pc = base + ( n - 1 ); ; pc-- ) {
                        while( pb <= pc && (comparison = CompareChannels(rxBucket[pb], pv )) <= 0 ) {
                            if( comparison == 0 ) {
                                swap( pa, pb );
                                pa++;
                            }
                            pb++;
                        }
                        while( pb <= pc && (comparison = CompareChannels(rxBucket[pc], pv )) >= 0 ) {
                            if( comparison == 0 ) {
                                swap( pc, pd );
                                pd--;
                            }
                            pc--;
                        }
                        if( pb > pc ) break;
                        swap( pb, pc );
                        pb++;
                    }
                    int pn = base + n;
                    int s = Math.min( pa - base, pb - pa );
                    if( s > 0 ) {
                    	for(int i = 0; i < s; i++) {
                    		swap(base + i, pb - s + i);
                    	}
                    }
                    s = Math.min( pd - pc, pn - pd - 1);
                    if( s > 0 ) {
                    	for(int i = 0; i < s; i++) {
                    		swap(pb + i, pn - s + i);
                    	}
                    }

                    int r = pb - pa;
                    s = pd - pc;
                    if( s >= r ) {
                        base_stack[sp] = pn - s;
                        n_stack[sp] = s;
                        n = r;
                    } else {
                        if( r <= 1 ) break;
                        base_stack[sp] = base;
                        n_stack[sp] = r;
                        base = pn - s;
                        n = s;
                    }
                    ++sp;
                }
            }
            if( sp-- == 0 ) break;
            base = base_stack[sp];
            n    = n_stack[sp];
        }
	}

	private static void swap(int i, int j) {
		RXBUCKET temp = rxBucket[i];
		rxBucket[i] = rxBucket[j];
		rxBucket[j] = temp;
    }

	private static int med3(int a, int b, int c)
	{

        if( CompareChannels( rxBucket[a], rxBucket[b] ) > 0 ) {
	        if( CompareChannels( rxBucket[a], rxBucket[c] ) > 0 ) {
	            if( CompareChannels( rxBucket[b], rxBucket[c] ) > 0 )
	                return( b );
	            return( c );
	        }
	        return( a );
	    }

        if( CompareChannels( rxBucket[a], rxBucket[c] ) >= 0 )
            return( a );

        if( CompareChannels( rxBucket[b], rxBucket[c] ) > 0 )
            return( c );

        return( b );
	}
}

class RXBUCKET implements Comparable<RXBUCKET>
{
	int index = 0;	//: 13; 	// object array index (sprite[], sector[], wall[])
	int type = 0;	//: 3;	// 0=sprite, 1=sector, 2=wall

	public void flush() {
		index = 0;
		type = 0;
	}
	public RXBUCKET() { }

	public RXBUCKET(int index, int type) { //for debug
		this.index = index;
		this.type = type;
	}

	@Override
	public int compareTo(RXBUCKET refl) {
		return EVENT.CompareChannels(this, refl);
	}
}

