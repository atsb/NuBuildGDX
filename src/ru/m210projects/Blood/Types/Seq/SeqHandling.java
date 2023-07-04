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

package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Trigger.FireballTrapCallback;
import static ru.m210projects.Blood.Trigger.MGunFireCallback;
import static ru.m210projects.Blood.Trigger.MGunOpenCallback;
import static ru.m210projects.Blood.Trigger.UniMissileTrapCallback; // For EctoSkull
import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class SeqHandling {

	public static final int kMaxSequences = kMaxSprites; //16384
	
	public static WallInst[] siWall = new WallInst[kMaxXWalls];
	public static MaskedWallInst[] siMasked = new MaskedWallInst[kMaxXWalls];
	public static CeilingInst[] siCeiling = new CeilingInst[kMaxXSectors];
	public static FloorInst[] siFloor = new FloorInst[kMaxXSectors];
	public static SpriteInst[] siSprite = new SpriteInst[kMaxXSprites];
	public static ActiveList activeList = new ActiveList(kMaxSequences);

	public static final int AIEnemyCallback = 0;
	public static final int ReviveCallback = 1; //45
	public static final int MGunOpenCallback = 2; //44
	public static final int MGunFireCallback = 3; //43
	public static final int FireballTrapCallback = 4; //42
	public static final int DamageTreeCallback = 5; //51
	public static final int SmokeCallback = 6; //49
	public static final int FireballCallback = 7; //47
	public static final int TchernobogCallback1 = 8; //50
	public static final int TchernobogCallback2 = 9; //48
	public static final int DamageDude = 10; //52
	public static final int DamageDude2 = 11; //53
	public static final int FatalityDead = 12; //46
	public static final int UniMissileTrapCallback = 13; // Add new callback for EctoSkullGen and DartGen
	
	public static CALLPROC[] callbacks = {
		new CALLPROC() { public void run(int nIndex) { /* dummy */ } },
		new CALLPROC() { public void run(int nIndex) { FReviveCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { MGunOpenCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { MGunFireCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { FireballTrapCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { DamageTreeCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { SmokeCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { FireballCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { TchernobogCallback1( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { TchernobogCallback2( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { DamageDude( nIndex, 8 ); } },
		new CALLPROC() { public void run(int nIndex) { DamageDude( nIndex, 3 ); } },
		new CALLPROC() { public void run(int nIndex) { FatalityDeadCallback( nIndex ); } },
		new CALLPROC() { public void run(int nIndex) { UniMissileTrapCallback( nIndex ); } },
	};
	
	public static int getIndex(CALLPROC callback)
	{
		for(int i = 0; i < callbacks.length; i++)
			if(callback == callbacks[i])
				return i;
		
		if(callback != null) //enemy callback
			return 0;
		
		return -1;
	}

	public static void SeqInit() {
		Console.Println("Initializing SEQ animation", 0);
		for(int i = 0; i < kMaxXWalls; i++)
			siWall[i] = new WallInst();
		for(int i = 0; i < kMaxXWalls; i++)
			siMasked[i] = new MaskedWallInst();
		for(int i = 0; i < kMaxXSectors; i++)
			siCeiling[i] = new CeilingInst();
		for(int i = 0; i < kMaxXSectors; i++)
			siFloor[i] = new FloorInst();
		for(int i = 0; i < kMaxXSprites; i++)
			siSprite[i] = new SpriteInst();
	}

	public static SeqInst GetInstance( int type, int nXIndex )
	{
		switch ( type )
		{
			case SS_WALL:
				if(!(nXIndex > 0 && nXIndex < kMaxXWalls)) game.dassert("nXIndex > 0 && nXIndex < kMaxXWalls: " + nXIndex);
				return siWall[nXIndex];

			case SS_CEILING:
				if(!(nXIndex > 0 && nXIndex < kMaxXSectors)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSectors: " + nXIndex);
				return siCeiling[nXIndex];

			case SS_FLOOR:
				if(!(nXIndex > 0 && nXIndex < kMaxXSectors)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSectors: " + nXIndex);
				return siFloor[nXIndex];

			case SS_SPRITE:
				if(!(nXIndex > 0 && nXIndex < kMaxXSprites)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSprites: " + nXIndex);
				return siSprite[nXIndex];

			case SS_MASKED:
				if(!(nXIndex > 0 && nXIndex < kMaxXWalls)) game.dassert("nXIndex > 0 && nXIndex < kMaxXWalls: " + nXIndex);
				return siMasked[nXIndex];
		}

		System.err.println("Unexcepted object type");
		return null;
	}

	public static void seqKill( int type, int nXIndex )
	{
		SeqInst pInst = GetInstance(type, nXIndex);
		if ( pInst != null && pInst.isPlaying )
		{
			if(activeList.remove(pInst) == -1)
				game.dassert("i < activeCount");	// should have been found
			
			UnlockInstance( pInst );
		}
	}
	
	public static void seqKillAll()
	{
		int i;
		
		for ( i = 0; i < kMaxXWalls; i++)
		{
			if ( siWall[i].isPlaying )
				UnlockInstance( siWall[i] );

			if ( siMasked[i].isPlaying )
				UnlockInstance( siMasked[i] );
		}

		for (i = 0; i < kMaxXSectors; i++)
		{
			if ( siCeiling[i].isPlaying )
				UnlockInstance( siCeiling[i] );
			if ( siFloor[i].isPlaying )
				UnlockInstance( siFloor[i] );
		}

		for (i = 0; i < kMaxXSprites; i++)
		{
			if ( siSprite[i].isPlaying )
				UnlockInstance( siSprite[i] );
		}

		activeList.clear();
	}
	
	public static void seqSpawn(int nSeqID, int type, int nXIndex, CALLPROC callback )
	{
		SeqInst pInst = GetInstance(type, nXIndex);
		if(!BuildGdx.cache.contains(nSeqID, "SEQ"))
			game.dassert("hSeq != null, id = " + nSeqID );

		boolean inList = false;
		if ( pInst.isPlaying )
		{
			// already playing this sequence?
			if (pInst.nSeqID == nSeqID) //hSeqID - filenum in RFF {
				return;
			
			inList = activeList.getIndex(pInst) != -1;
			UnlockInstance( pInst );
		}
		
		SeqType pSequence = SeqType.getInstance(nSeqID);

		pInst.pSequence = pSequence;
		pInst.nSeqID = nSeqID;
		pInst.callback = callback;
		pInst.isPlaying = true;
		pInst.timeCounter = (short) pSequence.ticksPerFrame;
		pInst.frameIndex = 0;

		if(!inList) {
			if(!activeList.add(pInst, nXIndex))
				game.dassert("activeCount < kMaxSequences");
		}

		pInst.update(nXIndex);
	}
		
	private static void UnlockInstance( SeqInst pInst )
	{
		if(pInst == null) game.dassert( "pInst != null" );
		if(pInst.pSequence == null) game.dassert( "pInst.pSequence != null" );

//		gSysRes.Unlock( pInst.hSeq );
		pInst.pSequence = null;
		pInst.isPlaying = false;
	}
	
	public static void clearInstances()
	{
		for(int i = 0; i < kMaxXWalls; i++) 
			siWall[i].clear();
		for(int i = 0; i < kMaxXWalls; i++) 
			siMasked[i].clear();
		for(int i = 0; i < kMaxXSectors; i++) 
			siCeiling[i].clear();
		for(int i = 0; i < kMaxXSectors; i++) 
			siFloor[i].clear();
		for(int i = 0; i < kMaxXSprites; i++) 
			siSprite[i].clear();
	}
	
	public static int seqFrame( int type, int nXIndex )
	{
		SeqInst pInst = GetInstance(type, nXIndex);
		if ( pInst.isPlaying )
			return pInst.frameIndex;
		return -1;
	}
	
	public static void seqProcess( int nTicks )
	{
		for(int i = 0; i < activeList.getSize(); i++)
		{
			short index = activeList.getIndex(i);
			SeqInst pInst = activeList.getInst(i);
			SeqType pSeq = pInst.pSequence;
			
			if(pInst.frameIndex >= pSeq.nFrames)
				game.dassert("pInst.frameIndex < pSeq.nFrames");
	
			pInst.timeCounter -= nTicks;
			while (pInst.timeCounter < 0)
			{
				pInst.timeCounter += pSeq.ticksPerFrame;
				++pInst.frameIndex;

				if (pInst.frameIndex == pSeq.nFrames)
				{
					if ( !pSeq.isLooping() )
					{
						UnlockInstance( pInst );

						if ( pSeq.isRemovable() )
						{
							if(pInst instanceof SpriteInst)
							{
								short nSprite = (short) xsprite[index].reference;
								if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
								checkEventList(nSprite, SS_SPRITE);
								if ( (sprite[nSprite].hitag & kAttrRespawn) != 0 && (sprite[nSprite].zvel >= kDudeBase && sprite[nSprite].zvel < kDudeMax) )
					            	evPostCallback(nSprite, SS_SPRITE, pGameInfo.nWeaponSettings, 9); //XXX Check
								else deletesprite(nSprite);	// safe to not use actPostSprite here
							}
							
							if(pInst instanceof MaskedWallInst)
							{
								int nWall = xwall[index].reference;
								if(!(nWall >= 0 && nWall < kMaxWalls)) game.dassert("nWall >= 0 && nWall < kMaxWalls");
 								wall[nWall].cstat &= ~kWallMasked & ~kWallFlipX & ~kWallOneWay;
								if ((wall[nWall].nextwall) != -1)
	 								wall[wall[nWall].nextwall].cstat &= ~kWallMasked & ~kWallFlipX & ~kWallOneWay;
							}
						}
						
						// remove it from the active list
						activeList.remove(i--);
						break;
					} else
						pInst.frameIndex = 0;
				}
				
				pInst.update(index);
			}
		}
	}
}
