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

import static ru.m210projects.Blood.AI.AIUNICULT.*;
import static ru.m210projects.Blood.AI.Ai.*;
import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Gib.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Strings.*;
import static ru.m210projects.Blood.Trig.*;
import static ru.m210projects.Blood.Types.DudeInfo.*;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Weapon.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Blood.Types.DEMO.*;

import static ru.m210projects.Blood.SECTORFX.shadeCount;
import static ru.m210projects.Blood.SECTORFX.shadeList;
import static ru.m210projects.Blood.SECTORFX.panCount;
import static ru.m210projects.Blood.SECTORFX.panList;

import java.util.Arrays;

import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.BUSY;
import ru.m210projects.Blood.Types.BUSYPROC;
import ru.m210projects.Blood.Types.MapInfo;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SECTOR;


import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Blood.Types.DudeInfo;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Trigger {
	public static final int kMaxBusyArray =	128;
	public static int  gBusyCount = 0;
	public static BUSY[] gBusy = new BUSY[kMaxBusyArray];

	public static Vector2[] kwall = new Vector2[kMaxWalls];
	public static Vector3[] ksprite = new Vector3[kMaxSprites];

	public static int[] secFloorZ = new int[kMaxSectors];
	public static int[] secCeilZ = new int[kMaxSectors];
	public static int[] secPath = new int[kMaxSectors];

	public static final int kRemoteDelay	= 18;
	public static final int kProxDelay		= 30;


	public static final int kBusyOk = 0;
	public static final int	kBusyRestore = 1;
	public static final int	kBusyReverse = 2;
	public static final int	kBusyComplete = 3;

	public static BUSYPROC[] gBusyProc = {
			/*0*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return VCrushBusy(nIndex, nBusy); } },
			/*1*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return VSpriteBusy(nIndex, nBusy); } },
			/*2*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return VDoorBusy(nIndex, nBusy); } },
			/*3*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return HDoorBusy(nIndex, nBusy); } },
			/*4*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return RDoorBusy(nIndex, nBusy); } },
			/*5*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return SRDoorBusy(nIndex, nBusy); } },
			/*6*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy)
				{
				if(!(nIndex >= 0 && nIndex < numsectors)) game.dassert("nSector >= 0 && nSector < numsectors");
				SECTOR pSector = sector[nIndex];
				int nXSector = pSector.extra;
				if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
				XSECTOR pXSector = xsector[nXSector];

				pXSector.busy = nBusy;

				if ( pXSector.command == kCommandLink && pXSector.txID != 0 )
					evSend(nIndex, SS_SECTOR, pXSector.txID, kCommandLink);

				if ( (nBusy & kFluxMask) == 0 )
				{
				 	SetSectorState(nIndex, pXSector, nBusy >> 16);
				 	sfxSectorStop(nIndex, nBusy >> 16);
					return kBusyComplete;
				}

			 	return kBusyOk;
			} },
			/*7*/ new BUSYPROC() { @Override
			public int run(int nIndex, int nBusy) { return PathSectorBusy(nIndex, nBusy); } },
	};

	public static boolean valueIsBetween(int val, int min, int max){
		return (val > min && val < max);
	}

	public static int Lin2Sin(int nBusy, int opt)
	{
		switch ( opt )
		{
	    case 0:
	    	return (1 << 15) - (Cos(nBusy * kAngle180 / kMaxBusyValue) >> 15);
	    case 2:
	    	return (1 << 16) - (Cos(nBusy * kAngle90 / kMaxBusyValue) >> 14);
	    case 3:
	    	return (Sin(nBusy * kAngle90 / kMaxBusyValue) >> 14);
		}
		return nBusy;
	}

	// by NoOne: almost same as SetSpriteState() but designed for modern types to complete some special tasks.
	public static int gdxTypeSetSpriteState( int nSprite, XSPRITE pXSprite, int state )
	{
		if ( (pXSprite.busy & kFluxMask) == 0 && pXSprite.state == state )
			return state ^ pXSprite.state;

		pXSprite.busy = state << 16;
		pXSprite.state = (short) state;

		checkEventList(nSprite, SS_SPRITE);
		if ( (sprite[nSprite].hitag & kAttrRespawn) != 0 && (sprite[nSprite].zvel >= kDudeBase && sprite[nSprite].zvel < kDudeMax) )
		{
			pXSprite.respawnPending = 3;
			evPostCallback(nSprite, 3, pGameInfo.nMonsterRespawnTime, kCallbackRespawn);
			return 1;
		}

		if ( state != pXSprite.restState && pXSprite.waitTime > 0 ) {
			evPost(nSprite, SS_SPRITE, pXSprite.waitTime * kTimerRate / 10,
				pXSprite.restState != 0 ? kCommandOn : kCommandOff );
		}

		if (pXSprite.txID != 0 && ((pXSprite.triggerOn && pXSprite.state == 1) || (pXSprite.triggerOff && pXSprite.state == 0))) {

		   // by NoOne: Sending new command instead of link is *required*, because types above
		   //are universal and can paste properties in different objects.
		   switch (pXSprite.command) {
		       case kCommandLink:
		       case kGDXCommandPaste:
		           evSend(nSprite, 3, pXSprite.txID, kGDXCommandPaste); // just send command to change properties
		           break;
			   case kCommandUnlock:
			       evSend(nSprite, 3, pXSprite.txID, pXSprite.command); // send normal command first
			       evSend(nSprite, 3, pXSprite.txID, kGDXCommandPaste); // then send command to change properties
			       break;
			   default:
			       evSend(nSprite, 3, pXSprite.txID, kGDXCommandPaste); // send first command to change properties
			       evSend(nSprite, 3, pXSprite.txID, pXSprite.command); // then send normal command
			       break;
		     }
		}

		return 1;
	}

	/*public static int SetSpriteState( int nSprite, XSPRITE pXSprite, int state )
	{
		if ( (pXSprite.busy & kFluxMask) == 0 && pXSprite.state == state )
			return state ^ pXSprite.state;

		pXSprite.busy = state << 16;
		pXSprite.state = (short) state;

		checkEventList(nSprite, SS_SPRITE);
		if ( (sprite[nSprite].hitag & kAttrRespawn) != 0 && (sprite[nSprite].zvel >= kDudeBase && sprite[nSprite].zvel < kDudeMax) )
		{
			pXSprite.respawnPending = 3;
			evPostCallback(nSprite, 3, pGameInfo.nMonsterRespawnTime, kCallbackRespawn);
			return 1;
		}

		if ( state != pXSprite.restState && pXSprite.waitTime > 0 ) {
			evPost(nSprite, SS_SPRITE, pXSprite.waitTime * kTimerRate / 10,
				pXSprite.restState != 0 ? kCommandOn : kCommandOff );
		}

		if (pXSprite.txID != 0 && ((pXSprite.triggerOn && pXSprite.state == 1) || (pXSprite.triggerOff && pXSprite.state == 0))) {

		   switch (sprite[nSprite].lotag) {
			   case kGDXObjPropertiesChanger:
			   case kGDXObjPicnumChanger:
			   case kGDXObjSizeChanger:
			   case kGDXSectorFXChanger:
			   case kGDXObjDataChanger:
			   case kGDXSpriteDamager:
				   // by NoOne: Sending new command instead of link is *required*, because types above
				   //are universal and can paste properties in different objects.
				   switch (pXSprite.command) {
				       case kCommandLink:
				       case kGDXCommandPaste:
				           evSend(nSprite, 3, pXSprite.txID, kGDXCommandPaste); // just send command to change properties
				           return 1;
					   case kCommandUnlock:
					       evSend(nSprite, 3, pXSprite.txID, pXSprite.command); // send normal command first
					       evSend(nSprite, 3, pXSprite.txID, kGDXCommandPaste); // then send command to change properties
					       return 1;
					   default:
					       evSend(nSprite, 3, pXSprite.txID, kGDXCommandPaste); // send first command to change properties
					       evSend(nSprite, 3, pXSprite.txID, pXSprite.command); // then send normal command
					       return 1;
				       }
			   default:
			       if (pXSprite.command != kCommandLink) evSend(nSprite, 3, pXSprite.txID, pXSprite.command);
			       break;
		   }
		}

		return 1;
	}*/

	public static int SetSpriteState( int nSprite, XSPRITE pXSprite, int state )
	{
		if ( (pXSprite.busy & kFluxMask) == 0 && pXSprite.state == state )
			return state ^ pXSprite.state;

		pXSprite.busy = state << 16;
		pXSprite.state = (short) state;

		checkEventList(nSprite, SS_SPRITE);
		if ( (sprite[nSprite].hitag & kAttrRespawn) != 0 && (sprite[nSprite].zvel >= kDudeBase && sprite[nSprite].zvel < kDudeMax) )
		{
			pXSprite.respawnPending = 3;
			evPostCallback(nSprite, 3, pGameInfo.nMonsterRespawnTime, kCallbackRespawn);
			return 1;
		}

		if ( state != pXSprite.restState && pXSprite.waitTime > 0 ) {
			evPost(nSprite, SS_SPRITE, pXSprite.waitTime * kTimerRate / 10,
				pXSprite.restState != 0 ? kCommandOn : kCommandOff );
		}

		if ( pXSprite.txID != 0 )
		{
			if (pXSprite.command == kCommandLink)
				return 1;

			if (pXSprite.triggerOn && pXSprite.state == 1)
				evSend(nSprite, SS_SPRITE, pXSprite.txID, pXSprite.command);

			if (pXSprite.triggerOff && pXSprite.state == 0)
				evSend(nSprite, SS_SPRITE, pXSprite.txID, pXSprite.command);
		}
		return 1;
	}

	public static int SetWallState( int nWall, XWALL pXWall, int state )
	{
		if ( (pXWall.busy & kFluxMask) == 0 && pXWall.state == state )
			return state ^ pXWall.state;

		pXWall.busy = state << 16;
		pXWall.state = (short) state;

		checkEventList(nWall, SS_WALL);

		if ( state != pXWall.restState && pXWall.waitTime > 0 )
			evPost(nWall, SS_WALL, pXWall.waitTime * kTimerRate / 10,
				pXWall.restState !=0 ? kCommandOn : kCommandOff);

		if ( pXWall.txID != 0 )
		{
			if (pXWall.command == kCommandLink)
				return 1;

			if (pXWall.triggerOn && pXWall.state == 1)
			{
//				dprintf( "evSend(%i,SS_WALL, %i, %i)\n", nWall, pXWall.txID, pXWall.command );
				evSend(nWall, SS_WALL, pXWall.txID, pXWall.command);
			}
			if (pXWall.triggerOff && pXWall.state == 0)
			{
//				dprintf( "evSend(%i,SS_WALL, %i, %i)\n", nWall, pXWall.txID, pXWall.command );
				evSend(nWall, SS_WALL, pXWall.txID, pXWall.command);
			}
		}
		return 1;
	}

	public static void SetSectorState( int nSector, XSECTOR pXSector, int state )
	{
		if ( (pXSector.busy & kFluxMask) == 0 && pXSector.state == state )
			return;

		pXSector.busy = state << 16;
		pXSector.state = (short) state;

		checkEventList(nSector, SS_SECTOR);

		if ( state == 1 ) {
			if (pXSector.command != kCommandLink && pXSector.triggerOn ) {
				if ( pXSector.txID != 0 ) {
					evSend(nSector, SS_SECTOR, pXSector.txID, pXSector.command);
				}
			}
			if ( pXSector.stopFlag[kCommandOn])
			{
				pXSector.stopFlag[kCommandOn] = false;
				pXSector.stopFlag[kCommandOff] = false;
				return;
			}
			if ( pXSector.waitFlag[kCommandOn] )
				evPost(nSector, SS_SECTOR, pXSector.waitTime[kCommandOn] * kTimerRate / 10, kCommandOff);
			return;
		}

		if (pXSector.command != kCommandLink && pXSector.triggerOff ) {
			if ( pXSector.txID != 0 ) {
				evSend(nSector, SS_SECTOR, pXSector.txID, pXSector.command);
			}
		}
		if ( pXSector.stopFlag[kCommandOff])
		{
			pXSector.stopFlag[kCommandOn] = false;
			pXSector.stopFlag[kCommandOff] = false;
			return;
		}
		if ( pXSector.waitFlag[kCommandOff] )
			evPost(nSector, SS_SECTOR, pXSector.waitTime[kCommandOff] * kTimerRate / 10, kCommandOn);
	}

	public static void AddBusy( int nIndex, int busyProc, int nDelta )
	{
		if(nDelta == 0) game.dassert("nDelta != 0");
		int i;
		// find an existing nIndex busy, or an unused busy slot
		for (i = 0; i < gBusyCount; i++)
		{
			if ((nIndex == gBusy[i].nIndex) && (busyProc == gBusy[i].busyProc))
				break;
		}

		// adding a new busy?
		if (i == gBusyCount)
		{
			if ( gBusyCount == kMaxBusyArray )
			{
//				dprintf("OVERFLOW: AddBusy() ignored\n");
				return;
			}

			gBusy[i].nIndex = nIndex;
			gBusy[i].busyProc = busyProc;
			gBusy[i].nBusy = (nDelta > 0) ? 0 : kMaxBusyValue;
			gBusyCount++;
		}
		gBusy[i].nDelta = nDelta;
	}

	public static void ReverseBusy( int nIndex, int busyProc )
	{
		// find an existing nIndex busy, or an unused busy slot
		for (int i = 0; i < gBusyCount; i++)
		{
			if ( nIndex == gBusy[i].nIndex && busyProc == gBusy[i].busyProc )
			{
				gBusy[i].nDelta = -gBusy[i].nDelta;
				return;
			}
		}
//		dprintf("ReverseBusy: matching busy not found!\n");
	}

	public static int GetSourceBusy( int event )
	{
		int nXIndex;

		switch ( getType(event) )
		{
			case SS_SECTOR:
				nXIndex = sector[getIndex(event)].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXSectors)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSectors");
				return xsector[nXIndex].busy;

			case SS_WALL:
				nXIndex = wall[getIndex(event)].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXWalls)) game.dassert("nXIndex > 0 && nXIndex < kMaxXWalls");
				return xwall[nXIndex].busy;

			case SS_SPRITE:
				nXIndex = sprite[getIndex(event)].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXSprites)) game.dassert("nXIndex > 0 && nXIndex < kMaxXSprites");
				return xsprite[nXIndex].busy;
		}

		// shouldn't reach this point
		return 0;
	}

	public static void OperateSprite( int nSprite, XSPRITE pXSprite, int evCommand )
	{
		SPRITE pSprite = sprite[nSprite];

		// special handling for lock/unlock commands
		switch ( evCommand )
		{
			case kCommandLock:
				pXSprite.Locked = 1;
				switch (pSprite.lotag) {
					case kGDXWindGenerator:
						stopWindOnSectors(pXSprite);
						break;
				}
				return;

			case kCommandUnlock:
				pXSprite.Locked = 0;
				return;

			case kCommandToggleLock:
				pXSprite.Locked ^= 1;
				switch (pSprite.lotag) {
					case kGDXWindGenerator:
						if (pXSprite.Locked == 1) stopWindOnSectors(pXSprite);
						break;
				}
				return;
		}

		if(pSprite.statnum == kStatDude) {
			if (IsDudeSprite(pSprite)) {
				switch( evCommand )
				{
					case kCommandOff:
						SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandOn:
					case kCommandSpritePush:
					case kCommandSpriteTouch:
						if ( pXSprite.state == 0 )
					        SetSpriteState(nSprite, pXSprite, 1);
					    aiActivateDude( pSprite, pXSprite );
					    break;
					case kCommandSpriteProximity:
						if ( pXSprite.state == 0 ) {
					        SetSpriteState(nSprite, pXSprite, 1);
					        aiActivateDude( pSprite, pXSprite );
						}
					    break;
				}
				return;
			}
		}

		switch (pSprite.lotag)
		{

			/* - Random Event Switch takes random data field and uses it as TX ID - */
			case kGDXRandomTX:

				int tx = 0; int maxRetries = 10;
				if (pXSprite.data1 > 0 && pXSprite.data2 <= 0 && pXSprite.data3 <= 0 && pXSprite.data4 > 0) {

					// data1 must be less than data4
					if (pXSprite.data1 > pXSprite.data4){
						int tmp = pXSprite.data1;
						pXSprite.data1 = (short) pXSprite.data4;
						pXSprite.data4 = tmp;
					}

					int total = pXSprite.data4 - pXSprite.data1;
					while(maxRetries > 0) {
						if (pGameInfo.nGameType != kNetModeOff || numplayers > 1)  tx = Gameutils.Random(total) + pXSprite.data1;
						else tx = (int) (total*Math.random()) + pXSprite.data1;

						if (tx != pXSprite.txID) break;
						maxRetries--;
					}

				} else {

					while(maxRetries > 0) {
						if ((tx = GetRandDataVal(null,pSprite)) > 0 && tx != pXSprite.txID) break;
						maxRetries--;
					}

				}

				pXSprite.txID = (short) tx;
				SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
				break;

				/* - Sequential Switch takes values from data fields starting from data1 and uses it as TX ID - */
			    /* - ranged TX ID is now supported also - */
			    case kGDXSequentialTX:
			    	boolean range = false; int cnt = 3; tx = 0;
			        // set range of TX ID if data2 and data3 is empty.
			        if (pXSprite.data1 > 0 && pXSprite.data2 <= 0 && pXSprite.data3 <= 0 && pXSprite.data4 > 0) {

			            // data1 must be less than data4
			            if (pXSprite.data1 > pXSprite.data4) {
			                int tmp = pXSprite.data1;
			                pXSprite.data1 = (short) pXSprite.data4;
			                pXSprite.data4 = tmp;
			            }

			            // force send command to all TX id in a range
			        	if (pSprite.hitag == 1) {
			        		for (int i = pXSprite.data1; i <= pXSprite.data4; i++)
			        			evSend(nSprite,SS_SPRITE,i,pXSprite.command);

			        		pXSprite.dropMsg = 0;
			        		return;
			        	}

			            // Make sure dropMsg is correct as we store current index of TX ID here.
			            if (pXSprite.dropMsg < pXSprite.data1) pXSprite.dropMsg = pXSprite.data1;
			            else if (pXSprite.dropMsg > pXSprite.data4) pXSprite.dropMsg = (short) pXSprite.data4;

			            range = true;

			        } else {

			        	// force send command to all TX id by data index
			        	if (pSprite.hitag == 1) {
			        		for (int i = 0; i < 4; i++) {
			        			if ((tx = GetDataVal(pSprite,i)) > 0)
			        				evSend(nSprite,SS_SPRITE,tx,pXSprite.command);
			        		}

			        		pXSprite.dropMsg = 0;
			        		return;
			        	}

			        	// Make sure dropMsg is correct as we store current index of data field here.
			            if (pXSprite.dropMsg > 3) pXSprite.dropMsg = 0;
			            else if (pXSprite.dropMsg < 0) pXSprite.dropMsg = 3;
			        }

			        switch ( evCommand ) {
			            case kCommandOff:
			                if (!range) {
			                    while (cnt-- >= 0) { // skip empty data fields
			                        pXSprite.dropMsg--;
			                        if (pXSprite.dropMsg < 0) pXSprite.dropMsg = 3;
			                        tx = GetDataVal(pSprite, pXSprite.dropMsg);
			                        if (tx < 0) game.ThrowError(" -- Current data index is negative");
			                        if (tx > 0) break;
			                        continue;
			                    }
			                } else {
			                    pXSprite.dropMsg--;
			                    if (pXSprite.dropMsg < pXSprite.data1) {
			                        pXSprite.dropMsg = (short)pXSprite.data4;
			                    }
			                    tx = pXSprite.dropMsg;
			                }
			                break;

			            default:
			                if (!range) {
			                    while (cnt-- >= 0) { // skip empty data fields
			                        if (pXSprite.dropMsg > 3) pXSprite.dropMsg = 0;
			                        tx = GetDataVal(pSprite, pXSprite.dropMsg);
			                        if (tx < 0) game.ThrowError(" ++ Current data index is negative");
			                        pXSprite.dropMsg++;
			                        if (tx > 0) break;
			                        continue;
			                    }
			                } else {
			                    tx = pXSprite.dropMsg;
			                    if (pXSprite.dropMsg >= pXSprite.data4) {
			                        pXSprite.dropMsg = pXSprite.data1;
			                        break;
			                    }
			                    pXSprite.dropMsg++;
			                }
			                break;
			        }

		        	pXSprite.txID = (short)tx;
		        	SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
		        	break;
			case kGDXCustomDudeSpawn:
				if(pGameInfo.nMonsterSettings != 0 && actSpawnCustomDude(pSprite,-1) != null) totalKills += 1;
				break;
			case kDudeSpawn:
				if( pGameInfo.nMonsterSettings != 0 && pXSprite.data1 >= kDudeBase && pXSprite.data1 < kDudeMax ) {
					SPRITE pSpawn = null;
					// Random dude works only if at least 2 data fields
					// are not empty and is not original demo.
					if (!IsOriginalDemo()) {
						if ((pSpawn = spawnRandomDude(pSprite)) == null)
							pSpawn = actSpawnDude(pSprite, pXSprite.data1, -1);

					} else {
						pSpawn = actSpawnDude(pSprite, pXSprite.data1, -1);
					}

					if(pSpawn != null)
					{
						totalKills += 1;
						switch(pSpawn.lotag)
						{
							case kDudeBurning:
							case kDudeCultistBurning:
							case kDudeAxeZombieBurning:
							case kDudeBloatedButcherBurning:
							case kDudeTinyCalebburning:
							case kDudeTheBeastburning:
								XSPRITE pXSpawn = xsprite[pSpawn.extra];
								pXSpawn.health = dudeInfo[pXSprite.data1 - kDudeBase].startHealth << 4;
								pXSpawn.burnTime = 10;
								pXSpawn.target = -1;
								aiActivateDude(pSpawn, pXSpawn);
								break;
						}
					}
				}
				break;
			case kEarthQuake:
				if( pGameInfo.nGameType < 2) {
					pXSprite.isTriggered = true;
					pXSprite.triggerOn = false;
					SetSpriteState(nSprite, pXSprite, 1);
					for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
					{
						int dx = (pSprite.x - gPlayer[i].pSprite.x) >> 4;
						int dy = (pSprite.y - gPlayer[i].pSprite.y) >> 4;
						int dz = (pSprite.z - gPlayer[i].pSprite.z) >> 8;
						gPlayer[i].quakeTime = (pXSprite.data1 << 16) / (0x40000 + dx * dx + dy * dy + dz * dz);
					}
				}
				break;

			// by NoOne: various modern types
			case kMarkerWarpDest:
				if (pXSprite.txID <= 0) {
					if (SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1) == 1)
						useTeleportTarget(pXSprite,null);
					break;
				}
				gdxTypeSetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
				break;
			case kGDXSpriteDamager:
				if (pXSprite.txID <= 0) {
					if (SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1) == 1)
						useSpriteDamager(pXSprite,null);
					break;
				}
				gdxTypeSetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
				break;
			case kGDXObjPropertiesChanger:
		    case kGDXObjPicnumChanger:
		    case kGDXObjSizeChanger:
		    case kGDXSectorFXChanger:
		    case kGDXObjDataChanger:
		        gdxTypeSetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
		        break;
			case kSwitchToggle:
				switch( evCommand )
				{
					case kCommandOff:
						if(SetSpriteState(nSprite, pXSprite, 0) == 1) {
							sfxStart3DSound(pSprite, pXSprite.data2, 0, 0);
						}
						break;
					case kCommandOn:
						if(SetSpriteState(nSprite, pXSprite, 1) == 1) {
							sfxStart3DSound(pSprite, pXSprite.data1, 0, 0);
						}
						break;
					default:
						if(SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1) == 1) {
							if(pXSprite.state == 0)
								sfxStart3DSound(pSprite, pXSprite.data2, 0, 0);
							else sfxStart3DSound(pSprite, pXSprite.data1, 0, 0);
						}
						break;
				}
				break;
			case kSwitchMomentary:
				switch( evCommand )
				{
					case kCommandOff:
						if(SetSpriteState(nSprite, pXSprite, 0) == 1) {
							sfxStart3DSound(pSprite, pXSprite.data2, 0, 0);
						}
						break;
					case kCommandOn:
						if(SetSpriteState(nSprite, pXSprite, 1) == 1) {
							sfxStart3DSound(pSprite, pXSprite.data1, 0, 0);
						}
						break;
					default:
						if(SetSpriteState(nSprite, pXSprite, pXSprite.restState ^ 1) == 1) {
							if(pXSprite.state == 0)
								sfxStart3DSound(pSprite, pXSprite.data2, 0, 0);
							else sfxStart3DSound(pSprite, pXSprite.data1, 0, 0);
						}
						break;
				}
				break;

			// by NoOne: add linking for markers and stacks
			case kMarkerLowerWater:
			case kMarkerUpperWater:
			case kMarkerUpperGoo:
			case kMarkerLowerGoo:
			case kMarkerUpperLink:
			case kMarkerLowerLink:
			case kMarkerUpperStack:
			case kMarkerLowerStack:
				if (pXSprite.command == kCommandLink && pXSprite.txID != 0)
					evSend(nSprite, SS_SPRITE, pXSprite.txID, kCommandLink);
				break;
			// by NoOne: add triggering sprite feature. Path sector will trigger the marker after
			// it gets reached so it can send commands.
			case kPathMarker:
				switch (evCommand) {
					case kCommandOff:
						SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandOn:
						SetSpriteState(nSprite, pXSprite, 1);
						break;
					case kCommandLink:
						if (pXSprite.txID != 0)
							evSend(nSprite, SS_SPRITE, pXSprite.txID, kCommandLink); // don't forget linking!
						break;
					default:
						SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
						break;
				}
				break;
			case kSwitchCombination:
				switch( evCommand )
				{
					case kCommandOff:
						if (--pXSprite.data1 < 0)				// underflow?
							pXSprite.data1 += pXSprite.data3;
						break;

					default:
						if (++pXSprite.data1 >= pXSprite.data3)	// overflow?
							pXSprite.data1 -= pXSprite.data3;
						break;
				}

				// handle master switches
				if ( pXSprite.command == kCommandLink && pXSprite.txID != 0 )
					evSend(nSprite, SS_SPRITE, pXSprite.txID, kCommandLink);

				sfxStart3DSound(pSprite, pXSprite.data4, -1, 0);

				// at right combination?
				if (pXSprite.data1 == pXSprite.data2)
					SetSpriteState(nSprite, pXSprite, 1);
				else
					SetSpriteState(nSprite, pXSprite, 0);
				break;
			case kSwitchPadlock:
				switch( evCommand )
				{
					case kCommandOff:
						SetSpriteState(nSprite, pXSprite, 0);
						break;

					case kCommandOn:
						SetSpriteState(nSprite, pXSprite, 1);
						seqSpawn(getSeq(kPadlock), SS_SPRITE, pSprite.extra, null);
						break;

					default:
						SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
						if (pXSprite.state != 0)
						{
							seqSpawn(getSeq(kPadlock), SS_SPRITE, pSprite.extra, null);
						}
						break;
				}
				break;
			case kThingTNTRem:
				if ( sprite[nSprite].statnum == kStatRespawn )
					break;

				switch ( evCommand )
				{
					case kCommandOn:
						sfxStart3DSound(pSprite, 454, 0, 0); //kSfxTNTDetRemote
						evPost(nSprite, SS_SPRITE, kRemoteDelay, kCommandOff);
						break;

					default:
						actExplodeSprite((short)nSprite);
						break;
				}
				break;
			case kThingCrateFace:
			case kThingWallCrack:
				if( SetSpriteState(nSprite, pXSprite, 0) == 0 )
					return;
				actPostSprite(nSprite, kStatFree);
				break;
			case kThingTNTBarrel:
				if((pSprite.hitag & 0x10) == 0)
					actExplodeSprite(nSprite);
				break;
			case 418:
			case kThingTNTBundle:
			case kThingSprayBundle:
				actExplodeSprite(nSprite);
				break;
			case kThingTNTProx:
			case kGDXThingTNTProx:
				if ( sprite[nSprite].statnum == kStatRespawn )
					break;

				switch ( evCommand )
				{
					case kCommandOn:
						sfxStart3DSound(pSprite, 451, 0, 0); //kSfxTNTDetProx
						pXSprite.Proximity = true;
						break;

					case kCommandSpriteProximity:
						if ( pXSprite.state != 0 )						// don't trigger it if already triggered
							break;

						sfxStart3DSound(pSprite, 452, 0, 0); //kSfxTNTArmProx
						evPost(nSprite, SS_SPRITE, kProxDelay, kCommandOff);
						pXSprite.state = 1;
						break;

					default:
						actExplodeSprite(nSprite);
						break;
				}
				break;

			case kThingMachineGun:
				if (pXSprite.health > 0)
				{
					if ( evCommand == kCommandOn )
					{
						if (SetSpriteState(nSprite, pXSprite, 1) != 0)
						{
							seqSpawn(getSeq(kMGunOpen), SS_SPRITE, pSprite.extra, callbacks[MGunOpenCallback]);

							// data1 = max ammo (defaults to infinite)
							// data2 = dynamic ammo if data1 > 0
							if (pXSprite.data1 > 0)
								pXSprite.data2 = pXSprite.data1;
						}
					}
					else if ( evCommand == kCommandOff )
					{
						if (SetSpriteState(nSprite, pXSprite, 0) != 0)
							seqSpawn(getSeq(kMGunClose), SS_SPRITE, pSprite.extra, null);
					}
				}
				break;
			case kThingFlameTrap:
				if ( evCommand == kCommandOn )
				{
					if (SetSpriteState(nSprite, pXSprite, 1) != 0)
					{
						seqSpawn(getSeq(kMGunOpen), SS_SPRITE, pSprite.extra, null);
						sfxStart3DSound(pSprite, 441, 0, 0);
					}
				}
				else if ( evCommand == kCommandOff )
				{
					if (SetSpriteState(nSprite, pXSprite, 0) != 0) {
						seqSpawn(getSeq(kMGunClose), SS_SPRITE, pSprite.extra, null);
						sfxKill3DSound(pSprite, -1, -1);
					}
				}
				break;
			case kThingFallingRock:
		        if( SetSpriteState(nSprite, pXSprite, 1) != 0 )
		        	pSprite.hitag |= (kAttrMove | kAttrGravity | kAttrFalling);
				break;
			case kThingGibObject:
			case kThingExplodeObject:
			case 425:
		    case 426:
		    case kThingZombieHead:
				switch( evCommand )
				{
					case kCommandOff:
						if(SetSpriteState(nSprite, pXSprite, 0) == 1)
							actGibObject( pSprite, pXSprite );
						break;
					case kCommandOn:
						if(SetSpriteState(nSprite, pXSprite, 1) == 1)
							actGibObject( pSprite, pXSprite );
						break;
					default:
						if(SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1) == 1)
							actGibObject( pSprite, pXSprite );
						break;
				}
				break;
			case kTrapPoweredZap:
				switch( evCommand )
				{
					case kCommandOff:
						pXSprite.state = 0;
						pSprite.cstat |= kSpriteInvisible;
						pSprite.cstat &= ~kSpriteBlocking;
						break;

					case kCommandOn:
						pXSprite.state = 1;
						pSprite.cstat &= ~kSpriteInvisible;
						pSprite.cstat |= kSpriteBlocking;
						break;

					case kCommandToggle:
						pXSprite.state ^= 1;
						pSprite.cstat ^= kSpriteInvisible;
						pSprite.cstat ^= kSpriteBlocking;
						break;
				}
				break;

			case kThingHiddenExploder:
				if ( evCommand != kCommandOn )
			    {
			    	pSprite.cstat &= ~kSpriteInvisible;
			    	actExplodeSprite(pSprite.xvel);
			    	return;
			    }
			    SetSpriteState(nSprite, pXSprite, 1);
				break;
			case kThingLifeLeech:
				LeechOperate(pSprite, pXSprite, evCommand);
				break;
			case kGDXThingCustomDudeLifeLeech:
				dudeLeechOperate(pSprite,pXSprite,evCommand);
				break;
			case kGDXSeqSpawner:
			case kGDXEffectSpawner:
				switch (evCommand) {
					case kCommandOff:
						if (pXSprite.state == 1) SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandOn:
						checkEventList(nSprite,SS_SPRITE); // queue overflow protect
						if (pXSprite.state == 0) SetSpriteState(nSprite, pXSprite, 1);
						// go below
					case kCommandRespawn:
						if (pXSprite.txID <= 0) {
							if (pSprite.lotag == kGDXSeqSpawner) useSeqSpawnerGen(pXSprite, 3, pSprite.xvel);
							else useEffectGen(pXSprite,pSprite);
						} else {

							switch(pXSprite.command) {
							case kCommandLink:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // just send command to change properties
								break;
							case kCommandUnlock:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // send normal command first
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste);  // then send command to change properties
								break;
							default:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // send first command to change properties
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // then send normal command
								break;

							}
						}

						if (pXSprite.busyTime > 0)
							evPost(nSprite, SS_SPRITE, (pXSprite.busyTime + BiRandom(pXSprite.data1)) * kTimerRate / 10, kCommandRespawn);

						break;
					default:
						if(pXSprite.state == 0) evPost(nSprite, SS_SPRITE, 0, kCommandOn);
						else evPost(nSprite, SS_SPRITE, 0, kCommandOff);
						break;
				}

				break;

			case kGDXWindGenerator:
				switch (evCommand) {
					case kCommandOff:
						stopWindOnSectors(pXSprite);
						if (pXSprite.state == 1) SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandOn:
						checkEventList(nSprite,SS_SPRITE); // queue overflow protect
						if (pXSprite.state == 0) SetSpriteState(nSprite, pXSprite, 1);
						// go below
					case kCommandRespawn:
						if (pXSprite.txID <= 0) useSectorWindGen(pXSprite,sector[pSprite.sectnum]);
						else {

							switch(pXSprite.command) {
							case kCommandLink:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // just send command to change properties
								break;
							case kCommandUnlock:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // send normal command first
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste);  // then send command to change properties
								break;
							default:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // send first command to change properties
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // then send normal command
								break;

							}

						}

						if (pXSprite.busyTime > 0)
							evPost(nSprite, SS_SPRITE, pXSprite.busyTime, kCommandRespawn);

						break;
					default:
						if (pXSprite.state == 0) evPost(nSprite, SS_SPRITE, 0, kCommandOn);
						else evPost(nSprite, SS_SPRITE, 0, kCommandOff);
						break;
				}

				break;
			case kGDXDudeTargetChanger:

				// this one is required if data4 of generator was dynamically changed
				// it turns monsters in normal idle state instead of genIdle, so they
				// not ignore the world.
				boolean activated = false;
				if (pXSprite.dropMsg == 3 && 3 != pXSprite.data4) {
					activateDudes(pXSprite.txID);
					activated = true;
				}

				switch (evCommand) {
					case kCommandOff:
						if (pXSprite.data4 == 3 && !activated) activateDudes(pXSprite.txID);
						if (pXSprite.state == 1) SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandOn:
						checkEventList(nSprite,SS_SPRITE); // queue overflow protect
						if (pXSprite.state == 0) SetSpriteState(nSprite, pXSprite, 1);
						// go below
					case kCommandRespawn:

						if (pXSprite.txID <= 0 || !getDudesForTargetChg(pXSprite)) {
							evPost(nSprite, SS_SPRITE, 0, kCommandOff);
							break;
						} else {

							switch(pXSprite.command) {
							case kCommandLink:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // just send command to change properties
								break;
							case kCommandUnlock:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // send normal command first
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste);  // then send command to change properties
								break;
							default:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // send first command to change properties
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // then send normal command
								break;

							}

						}
						if (pXSprite.busyTime > 0)
							evPost(nSprite, SS_SPRITE, pXSprite.busyTime, kCommandRespawn);
						break;
					default:
						if(pXSprite.state == 0) evPost(nSprite, SS_SPRITE, 0, kCommandOn);
						else evPost(nSprite, SS_SPRITE, 0, kCommandOff);
						break;
				}

				pXSprite.dropMsg = (short) pXSprite.data4;
				break;

			case kGDXObjDataAccumulator:
				switch (evCommand) {
					case kCommandOff:
						if (pXSprite.state == 1) SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandOn:
						checkEventList(nSprite,SS_SPRITE); // queue overflow protect
						if (pXSprite.state == 0) SetSpriteState(nSprite, pXSprite, 1);
						// go below
					case kCommandRespawn:

						// force OFF after *all* TX objects reach the goal value
						if (pXSprite.txID <= 0 || (pSprite.hitag == 0 && goalValueIsReached(pXSprite))) {
							//System.err.println("GOING OFF");
							evPost(nSprite, SS_SPRITE, 0, kCommandOff);
							break;
						}

						if (pXSprite.txID > 0 && pXSprite.data1 > 0 && pXSprite.data1 <= 4) {

							switch(pXSprite.command) {
							case kCommandLink:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // just send command to change properties
								break;
							case kCommandUnlock:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // send normal command first
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste);  // then send command to change properties
								break;
							default:
								evSend(nSprite,SS_SPRITE,pXSprite.txID,kGDXCommandPaste); // send first command to change properties
								evSend(nSprite,SS_SPRITE,pXSprite.txID,pXSprite.command); // then send normal command
								break;
							}

							if (pXSprite.busyTime > 0)
								evPost(nSprite, SS_SPRITE, pXSprite.busyTime, kCommandRespawn);
						}

						break;
					default:
						if(pXSprite.state == 0) evPost(nSprite, SS_SPRITE, 0, kCommandOn);
						else evPost(nSprite, SS_SPRITE, 0, kCommandOff);
						break;
				}
				break;
			case kGenEctoSkull: // can shot any projectile, default 307 (ectoskull)
				if (!IsOriginalDemo()) {
					switch( evCommand ) {
						case kCommandOff:
							if (pXSprite.state == 1) SetSpriteState(nSprite, pXSprite, 0);
							break;
						case kCommandOn:
							checkEventList(nSprite,SS_SPRITE); // queue overflow protect
							if (pXSprite.state == 0) SetSpriteState(nSprite, pXSprite, 1);
							// go below
						case kCommandRespawn:
								ActivateGenerator( nSprite );
								if (pXSprite.txID != 0)
									evSend(nSprite, SS_SPRITE, pXSprite.txID, pXSprite.command);
								if (pXSprite.busyTime > 0)
									evPost(nSprite, SS_SPRITE, (pXSprite.busyTime * kTimerRate) / 10, kCommandRespawn);
								break;
						default:
							if(pXSprite.state == 0) evPost(nSprite, SS_SPRITE, 0, kCommandOn);
							else evPost(nSprite, SS_SPRITE, 0, kCommandOff);
							break;
					}
				}
				break;
			case kWeaponItemBase: // Random weapon
			case kAmmoItemRandom: // Random ammo
				if (!IsOriginalDemo()) {
					switch(evCommand) {
						case kCommandOff:
							if (pXSprite.state == 1) SetSpriteState(nSprite, pXSprite, 0);
							break;
						case kCommandOn:
							checkEventList(nSprite,SS_SPRITE); // queue overflow protect
							if (pXSprite.state == 0) SetSpriteState(nSprite, pXSprite, 1);
							// go below
						case kCommandRespawn:
								ActivateGenerator(nSprite);
								if (pXSprite.busyTime > 0)
									evPost(nSprite, SS_SPRITE, (pXSprite.busyTime * kTimerRate) / 10, kCommandRespawn);
								break;
						default:
							if(pXSprite.state == 0) evPost(nSprite, SS_SPRITE, 0, kCommandOn);
							else evPost(nSprite, SS_SPRITE, 0, kCommandOff);
							break;
					}
				}
				break;
			case kGenTrigger:
			case kGenWaterDrip:
			case kGenBloodDrip:
			case kGenFireball:
			case kGenDart:
			case kGenSFX:
			case kGenBubble:
			case kGenMultiBubble:
				switch( evCommand )
				{
					case kCommandOff:
						SetSpriteState(nSprite, pXSprite, 0);
						break;
					case kCommandRespawn:
							if ( pSprite.lotag != kGenTrigger )
								ActivateGenerator( nSprite );
							if ( pXSprite.txID != 0 )
								evSend(nSprite, SS_SPRITE, pXSprite.txID, pXSprite.command);
							if ( pXSprite.busyTime > 0 )
								evPost(nSprite, SS_SPRITE, (pXSprite.busyTime + BiRandom(pXSprite.data1)) * kTimerRate / 10, kCommandRespawn);
							break;
					default:
						if( pXSprite.state == 0) {
							SetSpriteState(nSprite, pXSprite, 1);
							evPost(nSprite, SS_SPRITE, 0, kCommandRespawn);
						}
						break;
				}
				break;

			case kGenPlayerSFX:
				if ( pGameInfo.nGameType == 0 )
				{
					if(gMe == null || gMe.pXsprite == null || gMe.pXsprite.health == 0)
						return;
					gMe.stayTime = 0;
				}

				sndStartSample(pXSprite.data1, -1, 1, false);
				break;
			default:
				switch( evCommand )
				{
					case kCommandOff:
						SetSpriteState(nSprite, pXSprite, 0);
						break;

					case kCommandOn:
						SetSpriteState(nSprite, pXSprite, 1);
						break;

					default:
						SetSpriteState(nSprite, pXSprite, pXSprite.state ^ 1);
						break;
				}
				break;
		}
	}

	private static final Vector3 wallVel = new Vector3(100, 100, 250);
	public static void OperateWall( int nWall, XWALL pXWall, int evCommand )
	{
		WALL pWall = wall[nWall];

		// special handling for lock/unlock commands
		switch ( evCommand )
		{
			case kCommandLock:
				pXWall.locked = 1;
				return;

			case kCommandUnlock:
				pXWall.locked = 0;
				return;

			case kCommandToggleLock:
				pXWall.locked ^= 1;
				return;
		}

		switch (pWall.lotag)
		{
			case kWallGlass:
				int action = 0;
				switch( evCommand )
				{
					case kCommandOff:
						action = SetWallState(nWall, pXWall, 0);
						break;

					case kCommandOn:
					case kCommandWallImpact:
						action = SetWallState(nWall, pXWall, 1);
						break;

					default:
						action = (SetWallState(nWall, pXWall, pXWall.state ^ 1));
						break;
				}
				if(action != 0) {
					WALL nextWall = null;
					if(pWall.nextwall >= 0)
						nextWall = wall[pWall.nextwall];
					if( pXWall.state != 0 ) {
						pWall.cstat &= ~( kWallBlocking | kWallHitscan);
						if(nextWall != null) {
							nextWall.cstat &= ~( kWallBlocking | kWallHitscan);
							pWall.cstat &= ~kWallMasked;
							nextWall.cstat &= ~kWallMasked;
						}
					} else {
						pWall.cstat |= 1;
						if(pXWall.triggerVector)
							pWall.cstat |= kWallHitscan;
						if ( nextWall != null)
					    {
							nextWall.cstat &= ~kWallBlocking;
							if(pXWall.triggerVector)
								nextWall.cstat |= kWallHitscan;
					      	pWall.cstat |= kWallMasked;
					      	nextWall.cstat |= kWallMasked;
					    }
					}

					if(pXWall.state != 0) {
						int spartType = ClipRange(pXWall.data, 0, 31);
						if(spartType > 0)
							walGenerateGib(nWall, spartType, wallVel);
					}
				}
				break;

			default:
				switch( evCommand )
				{
					case kCommandOff:
						SetWallState(nWall, pXWall, 0);
						break;

					case kCommandOn:
						SetWallState(nWall, pXWall, 1);
						break;

					default:
						SetWallState(nWall, pXWall, pXWall.state ^ 1);
						break;
				}
			break;
		}
	}

	public static void sfxSectorBusy(int nSector, int nBusy) {
		for ( int i = headspritesect[nSector]; i >= 0; i = nextspritesect[i] )
		{
			SPRITE pSprite = sprite[i];
	    	if ( pSprite.statnum == 0 && pSprite.lotag == kGenSectorSFX )
	    	{
	    		int nXSprite = pSprite.extra;
	    		/*
			  	Embraced
				filename blood.ini
				Title Cold Blood

				nXSprite > 0 && nXSprite < kMaxXSprites
	    		*/
	    		if(!(nXSprite > 0 && nXSprite < kMaxXSprites)) game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites: " + nXSprite);
	    		XSPRITE pXSPrite = xsprite[nXSprite];
	    		if ( nBusy != 0 )
	    		{
	    			if ( pXSPrite.data3 != 0 )
	    				sfxStart3DSound(pSprite, pXSPrite.data3, 0, 0);
	    		}
		        else
		        {
		        	if ( pXSPrite.data1 != 0 )
		        		sfxStart3DSound(pSprite, pXSPrite.data1, 0, 0);
		        }
	    	}
		}
	}

	public static void sfxSectorBusy(int nSector, int soundId, int flags) {
		for ( int i = headspritesect[nSector]; i >= 0; i = nextspritesect[i] )
		{
			SPRITE pSprite = sprite[i];
	    	if ( pSprite.statnum == 0 && pSprite.lotag == 709 )
	    	{
	    		sfxStart3DSound(pSprite, soundId, 0, flags);
	    	}
		}
	}

	public static void sfxSectorStop(int nSector, int nBusy) {
		for ( int i = headspritesect[nSector]; i >= 0; i = nextspritesect[i] )
		{
			SPRITE pSprite = sprite[i];
	    	if ( pSprite.statnum == 0 && pSprite.lotag == 709 )
	    	{
	    		int nXSprite = pSprite.extra;
	    		if(!(nXSprite > 0 && nXSprite < kMaxXSprites)) game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
	    		XSPRITE pXSPrite = xsprite[nXSprite];
	    		if ( nBusy != 0 )
	    		{
	    			if ( pXSPrite.data2 != 0 )
	    				sfxStart3DSound(pSprite, pXSPrite.data2, 0, 0);
	    		}
		        else
		        {
		        	if ( pXSPrite.data4 != 0 )
	    				sfxStart3DSound(pSprite, pXSPrite.data4, 0, 0);
		        }
	    	}
		}
	}

	public static void TranslateSector( int nSector, int i0, int i1, int cx, int cy, int x0, int y0, int a0,
			int x1, int y1, int a1, boolean fAllWalls )
	{
		XSECTOR pXSector = xsector[sector[nSector].extra];
		int x, y;

		int vX = x1 - x0;
		int vY = y1 - y0;
		int vA = a1 - a0;

		int Xi0 = x0 + mulscale(i0, vX, 16);
		int Xi1 = x0 + mulscale(i1, vX, 16);
		int dX = Xi1 - Xi0;

		int Yi0 = y0 + mulscale(i0, vY, 16);
		int Yi1 = y0 + mulscale(i1, vY, 16);
		int dY = Yi1 - Yi0;

		int Ai0 = a0 + mulscale(i0, vA, 16);
		int Ai1 = a0 + mulscale(i1, vA, 16);
		int dA = Ai1 - Ai0;

		short nWall = sector[nSector].wallptr;
		if ( fAllWalls )
		{
			for (int i = 0; i < sector[nSector].wallnum; nWall++, i++)
			{
				x = (int) kwall[nWall].x;
				y = (int) kwall[nWall].y;

				if ( Ai1 != 0 ) {
					Point out = RotatePoint(x, y, Ai1, cx, cy);
					x = out.getX();
					y = out.getY();
				}
				// move vertex
				engine.dragpoint(nWall, x + Xi1 - cx, y + Yi1 - cy);
			}
		}
		else
		{
			for (int i = 0; i < sector[nSector].wallnum; nWall++, i++)
			{
				short nWall2 = wall[nWall].point2;

				x = (int) kwall[nWall].x;
				y = (int) kwall[nWall].y;

				if ( (wall[nWall].cstat & kWallMoveForward) != 0 )
				{
					if ( Ai1 != 0 ) {
						Point out = RotatePoint(x, y, Ai1, cx, cy);
						x = out.getX();
						y = out.getY();
					}

					engine.dragpoint(nWall, x + Xi1 - cx, y + Yi1 - cy);

					// move next vertex if not explicitly tagged
					if ( (wall[nWall2].cstat & kWallMoveMask) == 0 )
					{
						x = (int) kwall[nWall2].x;
						y = (int) kwall[nWall2].y;

						if ( Ai1 != 0 ) {
							Point out = RotatePoint(x, y, Ai1, cx, cy);
							x = out.getX();
							y = out.getY();
						}

						engine.dragpoint(nWall2, x + Xi1 - cx, y + Yi1 - cy);
					}
				}
				else if ( (wall[nWall].cstat & kWallMoveBackward) != 0 )
				{
					if ( Ai1 != 0 ) {
						Point out = RotatePoint(x, y, -Ai1, cx, cy);
						x = out.getX();
						y = out.getY();
					}

					engine.dragpoint(nWall, x - (Xi1 - cx), y - (Yi1 - cy));

					// move next vertex if not explicitly tagged
					if ( (wall[nWall2].cstat & kWallMoveMask) == 0 )
					{
						x = (int) kwall[nWall2].x;
						y = (int) kwall[nWall2].y;

						if ( Ai1 != 0 ) {
							Point out = RotatePoint(x, y, -Ai1, cx, cy);
							x = out.getX();
							y = out.getY();
						}

						engine.dragpoint(nWall2, x - (Xi1 - cx), y - (Yi1 - cy));
					}
				}
			}
		}

		for (short nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
		{
			SPRITE pSprite = sprite[nSprite];

			// don't move markers if their hitag != 1
			if ( pSprite.statnum == kStatMarker || pSprite.statnum == kStatMarker2 ){
				if (pSprite.hitag != 0x0001) continue;
			}

			x = (int) ksprite[nSprite].x;
			y = (int) ksprite[nSprite].y;

			if ( (sprite[nSprite].cstat & kSpriteMoveForward) != 0 )
			{
				if ( Ai1 != 0 ) {
					Point out = RotatePoint(x, y, Ai1, cx, cy);
					x = out.getX();
					y = out.getY();
				}

				viewBackupSpriteLoc( nSprite, pSprite );
				pSprite.ang = (short)((pSprite.ang + dA) & kAngleMask);
				if(IsPlayerSprite(pSprite)) {
					PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];
					pPlayer.ang = BClampAngle(pPlayer.ang + dA);
				}
				pSprite.x = x + Xi1 - cx;
				pSprite.y = y + Yi1 - cy;
			}
			else if ( (sprite[nSprite].cstat & kSpriteMoveReverse) != 0 )
			{
				if ( Ai1 != 0 ) {
					Point out = RotatePoint(x, y, -Ai1, cx, cy);
					x = out.getX();
					y = out.getY();
				}

				viewBackupSpriteLoc( nSprite, pSprite );
				pSprite.ang = (short)((pSprite.ang - dA) & kAngleMask);
				if(IsPlayerSprite(pSprite)) {
					PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];
					pPlayer.ang = BClampAngle(pPlayer.ang + dA);
				}
				pSprite.x = x - (Xi1 - cx);
				pSprite.y = y - (Yi1 - cy);
			}
			else if ( pXSector.Drag )
			{
				GetSpriteExtents(pSprite);
				int z = engine.getflorzofslope((short) nSector, pSprite.x, pSprite.y);

				if ( (pSprite.cstat & kSpriteRMask) == 0 && extents_zBot >= z )
				{
					// translate relatively (degenerative)
					if ( dA != 0 ) {
						Point out = RotatePoint(pSprite.x, pSprite.y, dA, Xi0, Yi0);
						pSprite.x = out.getX();
						pSprite.y = out.getY();
					}

					viewBackupSpriteLoc( nSprite, pSprite );
					pSprite.ang = (short)((pSprite.ang + dA) & kAngleMask);
					if(IsPlayerSprite(pSprite)) {
						PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];
						pPlayer.ang = BClampAngle(pPlayer.ang + dA);
					}
					pSprite.x += dX;
					pSprite.y += dY;
				}
			}
		}
	}

	public static void zMotion(int nSector, XSECTOR pXSector, int busy, int opt) {
		SECTOR pSector = sector[nSector];
		viewBackupSectorLoc(nSector, pSector);

		int floorZRange = pXSector.onFloorZ - pXSector.offFloorZ;
		if ( floorZRange != 0)
		{
			int oldFloorZ = pSector.floorz;
			pSector.floorz = mulscale(floorZRange, Lin2Sin(busy, opt), 16) + pXSector.offFloorZ;
			secFloorZ[nSector] = pSector.floorz;
			floorVel[nSector] += (pSector.floorz - oldFloorZ) << 8;

			// adjust the z for any floor relative sprites or face sprites in the floor
			for (int nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
			{
				SPRITE pSprite = sprite[nSprite];
				//if( pSprite.statnum != 10 && pSprite.statnum != 16 ) { //!lower water %% alignate region

					GetSpriteExtents(pSprite);
					if((pSprite.cstat & kSpriteMoveFloor) != 0) {
						viewBackupSpriteLoc( nSprite, pSprite );
						pSprite.z += (pSector.floorz - oldFloorZ);
					}
					else if ( (pSprite.hitag & kAttrGravity) != 0)
						pSprite.hitag |= kAttrFalling;
					else if ( oldFloorZ <= extents_zBot && (pSprite.cstat & kSpriteRMask) == 0 )
					{
						viewBackupSpriteLoc( nSprite, pSprite );
						pSprite.z += (pSector.floorz - oldFloorZ);
					}
				//}
			}
		}

		int ceilZRange = pXSector.onCeilZ - pXSector.offCeilZ;
		if ( ceilZRange != 0)
		{
			int oldCeilZ = pSector.ceilingz;
			pSector.ceilingz = mulscale(ceilZRange, Lin2Sin(busy, opt), 16) + pXSector.offCeilZ;
			secCeilZ[nSector] = pSector.ceilingz;
			ceilingVel[nSector] += (pSector.ceilingz - oldCeilZ) << 8;

			// adjust the z for any floor relative sprites or face sprites in the floor
			for (int nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
			{
				SPRITE pSprite = sprite[nSprite];
				//if( pSprite.statnum != 10 && pSprite.statnum != 16 ) { //!lower water %% alignate region

					GetSpriteExtents(pSprite);
					if((pSprite.cstat & kSpriteMoveCeiling) != 0) {
						viewBackupSpriteLoc( nSprite, pSprite );
						pSprite.z += (pSector.ceilingz - oldCeilZ);
					}
				//}
			}
		}
	}

	public static int getHighestZ;
	public static int GetHighestSprite( int nSector, int nStatus )
	{
		getHighestZ = sector[nSector].floorz;
		int retSprite = -1;

		for (short nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
		{
			if ( sprite[nSprite].statnum == nStatus || nStatus == kMaxStatus)
			{
				SPRITE pSprite = sprite[nSprite];
				GetSpriteExtents(pSprite);

				if ( pSprite.z - extents_zTop < getHighestZ )
				{
					getHighestZ = pSprite.z - extents_zTop;
					retSprite = nSprite;
				}
			}
		}
		return retSprite;
	}

	public static int getRangepzTop, getRangepzBot;
	public static int GetSpriteRange( int nSector )
	{
		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		int retSprite = -1;

		int ceilingz = sector[nSector].ceilingz;
		for (short nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
		{
			SPRITE pSprite = sprite[nSprite];
			if(pSprite.statnum == kStatThing || pSprite.statnum == kStatDude) {
				GetSpriteExtents(pSprite);

				if( ceilingz > extents_zTop) {
					ceilingz = extents_zTop;
					getRangepzTop = extents_zTop;
					getRangepzBot = extents_zBot;
					retSprite = nSprite;
				}
			}
		}

		return retSprite;
	}

	public static int VCrushBusy(int nSector, int nBusy) {
		System.out.println("crush");
		return 0;
	}

	public static int VSpriteBusy(int nSector, int nBusy) {
		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		SECTOR pSector = sector[nSector];
		int nXSector = pSector.extra;
		if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		XSECTOR pXSector = xsector[nXSector];

		int opt = getWave(pXSector, nBusy);

		int floorZRange = pXSector.onFloorZ - pXSector.offFloorZ;
		if ( floorZRange != 0 )
		{
			// adjust the z for any floor relative sprites or face sprites in the floor
			for (short nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
			{
				SPRITE pSprite = sprite[nSprite];

				if ( (pSprite.cstat & kSpriteMoveFloor) != 0 ) {
					viewBackupSpriteLoc( nSprite, pSprite );
					pSprite.z = (int) (ksprite[nSprite].z + mulscale(floorZRange, Lin2Sin(nBusy, opt), 16));
				}
			}
		}

		int ceilZRange = pXSector.onCeilZ - pXSector.offCeilZ;
		if ( ceilZRange != 0 )
		{
			// adjust the z for any ceiling relative sprites or face sprites in the ceiling
			for (short nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
			{
				SPRITE pSprite = sprite[nSprite];

				if ( (pSprite.cstat & kSpriteMoveCeiling) != 0) {
					viewBackupSpriteLoc( nSprite, pSprite );
					pSprite.z = (int) (ksprite[nSprite].z + mulscale(ceilZRange, Lin2Sin(nBusy, opt), 16));
				}
			}
		}

		pXSector.busy = nBusy;

		if ( pXSector.command == kCommandLink && pXSector.txID != 0 )
			evSend(nSector, SS_SECTOR, pXSector.txID, kCommandLink);

		if ( (nBusy & kFluxMask) == 0 )
		{
		 	SetSectorState(nSector, pXSector, nBusy >> 16);
		 	sfxSectorStop(nSector, nBusy >> 16);
			return kBusyComplete;
		}

	 	return kBusyOk;
	}

	public static int VDoorBusy(int nSector, int nBusy) {
		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		SECTOR pSector = sector[nSector];
		int nXSector = pSector.extra;

		if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		XSECTOR pXSector = xsector[nXSector];
		int nDelta;
		if(pXSector.state != 0) {
			nDelta = kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOn] * kTimerRate / 10, 1);
		} else nDelta = -kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOff] * kTimerRate / 10, 1);

		int nSprite = GetSpriteRange( nSector );
		if( nSprite >= 0) {
			SPRITE pSprite = sprite[nSprite];

			if(!(pSprite.extra > 0 && pSprite.extra < kMaxXSprites)) game.dassert("VDoorBusy pSprite.extra > 0 && pSprite.extra < kMaxXSprites");
			XSPRITE pXSprite = xsprite[pSprite.extra];

			if(nBusy < pXSector.busy)
			{
				if( pXSector.offCeilZ > pXSector.onCeilZ || pXSector.offFloorZ < pXSector.onFloorZ ) {
					if(pXSector.interruptable) {
						if( pXSector.Crush ) {
							if(pXSprite.health == 0)
								return kBusyReverse;
					    	actDamageSprite(nSprite, pSprite, kDamageFall, pXSector.data != 0 ? (pXSector.data << 4) : 8000 );
						}
						nBusy = ClipRange(nBusy + kFrameTicks * (nDelta / 2), 0, kMaxBusyValue);
					}
					else
					{
						if( pXSector.Crush && pXSprite.health > 0 ) {
							actDamageSprite(nSprite, pSprite, kDamageFall, pXSector.data != 0 ? (pXSector.data << 4) : 8000 );
							nBusy = ClipRange(nBusy + kFrameTicks * (nDelta / 2), 0, kMaxBusyValue);
						}
					}
				}
			}
			else if(nBusy > pXSector.busy)
			{
				if( pXSector.onCeilZ > pXSector.offCeilZ || pXSector.onFloorZ < pXSector.offFloorZ ) {
					if(pXSector.interruptable) {
						if( pXSector.Crush ) {
							if(pXSprite.health == 0)
								return kBusyReverse;
					    	actDamageSprite(nSprite, pSprite, kDamageFall, pXSector.data != 0 ? (pXSector.data << 4) : 8000 );
						}
						nBusy = ClipRange(nBusy - kFrameTicks * (nDelta / 2), 0, kMaxBusyValue);
					}
					else
					{
						if( pXSector.Crush && pXSprite.health > 0 ) {
							actDamageSprite(nSprite, pSprite, kDamageFall, pXSector.data != 0 ? (pXSector.data << 4) : 8000 );
							nBusy = ClipRange(nBusy - kFrameTicks * (nDelta / 2), 0, kMaxBusyValue);
						}
					}
				}
			}
		}

		zMotion(nSector, pXSector, nBusy, getWave(pXSector, nBusy));

		pXSector.busy = nBusy;

		if ( pXSector.command == kCommandLink && pXSector.txID != 0 )
			evSend(nSector, SS_SECTOR, pXSector.txID, kCommandLink);

		if ( (nBusy & kFluxMask) == 0 )
		{
		 	SetSectorState(nSector, pXSector, nBusy >> 16);
		 	sfxSectorStop(nSector, nBusy >> 16);
			return kBusyComplete;
		}

	 	return kBusyOk;
	}

	public static int HDoorBusy(int nSector, int nBusy) {

		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		SECTOR pSector = sector[nSector];
		int nXSector = pSector.extra;
		if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		XSECTOR pXSector = xsector[nXSector];

		int opt = getWave(pXSector, nBusy);

		SPRITE pMark0 = sprite[pXSector.marker0];
		SPRITE pMark1 = sprite[pXSector.marker1];

		TranslateSector(nSector, Lin2Sin(pXSector.busy, opt), Lin2Sin(nBusy, opt),
			pMark0.x, pMark0.y, pMark0.x, pMark0.y, pMark0.ang, pMark1.x, pMark1.y, pMark1.ang,
			pSector.lotag == kSectorSlide);
		zMotion(nSector, pXSector, nBusy, opt);

		pXSector.busy = nBusy;

		if ( pXSector.command == kCommandLink && pXSector.txID != 0 )
			evSend(nSector, SS_SECTOR, pXSector.txID, kCommandLink);

		if ( (nBusy & kFluxMask) == 0 )
		{
		 	SetSectorState(nSector, pXSector, nBusy >> 16);
		 	sfxSectorStop(nSector, nBusy >> 16);
			return kBusyComplete;
		}

		return kBusyOk;

	}

	public static int RDoorBusy(int nSector, int nBusy) {
		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		SECTOR pSector = sector[nSector];
		int nXSector = pSector.extra;
		if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		XSECTOR pXSector = xsector[nXSector];

		int opt = getWave(pXSector, nBusy);

		SPRITE pMark = sprite[pXSector.marker0];
		TranslateSector(nSector, Lin2Sin(pXSector.busy, opt), Lin2Sin(nBusy, opt),
			pMark.x, pMark.y, pMark.x, pMark.y, 0, pMark.x, pMark.y, pMark.ang,
			pSector.lotag == kSectorRotate);
		zMotion(nSector, pXSector, nBusy, opt);

		pXSector.busy = nBusy;

		if ( pXSector.command == kCommandLink && pXSector.txID != 0 )
			evSend(nSector, SS_SECTOR, pXSector.txID, kCommandLink);

		if ( (nBusy & kFluxMask) == 0 )
		{
		 	SetSectorState(nSector, pXSector, nBusy >> 16);
		 	sfxSectorStop(nSector, nBusy >> 16);
			return kBusyComplete;
		}

		return kBusyOk;
	}

	public static int SRDoorBusy(int nSector, int nBusy) {
		int a0, a1, opt, data;
		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		SECTOR pSector = sector[nSector];
		int nXSector = pSector.extra;
		if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		XSECTOR pXSector = xsector[nXSector];

		SPRITE pMark = sprite[pXSector.marker0];
		opt = getWave(pXSector, nBusy);

		if ( pXSector.busy >= nBusy ) {
			a0 = pXSector.data - pMark.ang;
			a1 = pXSector.data;
			data = pXSector.data - pMark.ang;
		} else {
			a0 = pXSector.data;
			a1 = pXSector.data + pMark.ang;
			data = pXSector.data + pMark.ang;
		}

		TranslateSector(nSector, Lin2Sin(pXSector.busy, opt), Lin2Sin(nBusy, opt),
				pMark.x, pMark.y, pMark.x, pMark.y, a0, pMark.x, pMark.y, a1, true);

		pXSector.busy = nBusy;

		if ( pXSector.command == kCommandLink && pXSector.txID != 0 )
			evSend(nSector, SS_SECTOR, pXSector.txID, kCommandLink);

		if ( (nBusy & kFluxMask) == 0 )
		{
		 	SetSectorState(nSector, pXSector, nBusy >> 16);
		 	pXSector.data = data;
		 	sfxSectorStop(nSector, nBusy >> 16);
			return kBusyComplete;
		}

		return kBusyOk;
	}

	public static int PathSectorBusy( int nSector, int nBusy ) {

		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");
		SECTOR pSector = sector[nSector];
		int nXSector = pSector.extra;
		if(!(nXSector > 0 && nXSector < kMaxXSectors))game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		XSECTOR pXSector = xsector[nXSector];

		SPRITE pMarker0 = sprite[pXSector.marker0];
		XSPRITE pXMarker0 = xsprite[pMarker0.extra];
		SPRITE pMarker1 = sprite[pXSector.marker1];
		XSPRITE pXMarker1 = xsprite[pMarker1.extra];
		SPRITE pPivot = sprite[secPath[nSector]];

		TranslateSector(
				nSector, Lin2Sin(pXSector.busy, pXMarker0.wave), Lin2Sin(nBusy, pXMarker0.wave),
				pPivot.x, pPivot.y, pMarker0.x, pMarker0.y, pMarker0.ang, pMarker1.x, pMarker1.y, pMarker1.ang, true);

		zMotion(nSector, pXSector, nBusy, pXMarker0.wave);

		pXSector.busy = nBusy;

		if ( (nBusy & kFluxMask) == 0 )
		{
			evPost(nSector, SS_SECTOR, pXMarker1.waitTime * kTimerRate / 10, kCommandOn);
			pXSector.busy = 0;
			pXSector.state = 0;

			if(pXMarker0.data4 != 0) {
				 sfxSectorBusy(nSector, pXMarker0.data4, pXSector.busy);
			}
			pXSector.marker0 = pXSector.marker1;
			pXSector.data = pXMarker1.data1;

			return kBusyComplete;
		}

	 	return kBusyOk;
	}

	public static void OperateDoor( int nSector, XSECTOR pXSector, int evCommand, int busyProc )
	{
		int nDelta;
		switch ( evCommand )
		{
			case kCommandOff:
				if ( pXSector.busy != 0 ) {
					nDelta = -kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOff] * kTimerRate / 10, 1);
					AddBusy(nSector, busyProc, nDelta);
					sfxSectorBusy(nSector, 1);
				}
				break;

			case kCommandOn:
				if ( pXSector.busy != kMaxBusyValue ) {
					nDelta = kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOn] * kTimerRate / 10, 1);
					AddBusy(nSector, busyProc, nDelta);
					sfxSectorBusy(nSector, 0);
				}
				break;

			default:

				if ( (pXSector.busy & kFluxMask) != 0 ) {
					if(pXSector.interruptable) {
						ReverseBusy(nSector, busyProc);
						pXSector.state = (short) ((pXSector.state == 0) ? 1 : 0);
					}
				}
				else {
					if(pXSector.state == 0) {
						nDelta = kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOn] * kTimerRate / 10, 1);
					} else nDelta = -kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOff] * kTimerRate / 10, 1);
					AddBusy(nSector, busyProc, nDelta);
					sfxSectorBusy(nSector, pXSector.state);
				}

				break;
		}
	}

	public static boolean TeleportDudes(int sectnum) {
		for ( int i = headspritesect[sectnum]; i >= 0; i = nextspritesect[i] )
		{
			if ( sprite[i].statnum == kStatDude )
		    	return true;
		}
		return false;
	}

	public static void TeleportDamage(int nSource, int nSector)
	{
		for ( int i = headspritesect[nSector]; i >= 0; i = nextspritesect[i] )
		{
			SPRITE pSprite = sprite[i];
		    if ( pSprite.statnum == kStatDude )
		    {
		    	actDamageSprite(nSource, pSprite, kDamageExplode, 4000);
		    }
		    else if ( pSprite.statnum == kStatThing )
		    {
		    	actDamageSprite(nSource, pSprite, kDamageExplode, 8000);
		    }
		}
	}

	public static void OperateTeleport( int nSector, XSECTOR pXSector ) {
		if(!(nSector < numsectors)) game.dassert("nSector < numsectors " + nSector + " < " + numsectors);
		int nDest = pXSector.marker0;
		if(nDest >= kMaxSprites) game.dassert("nDest < kMaxSprites");
		SPRITE pDest = sprite[nDest];
		if(pDest.statnum != kStatMarker) game.dassert("pDest.statnum != kStatMarker");
		if(pDest.lotag != kMarkerWarpDest) game.dassert("pDest.type != kMarkerWarpDest");
		if(!(pDest.sectnum >= 0 && pDest.sectnum < kMaxSectors)) game.dassert("pDest.sectnum >= 0 && pDest.sectnum < kMaxSectors");
		for ( int i = headspritesect[nSector]; i >= 0; i = nextspritesect[i] )
		{
			SPRITE pSprite = sprite[i];
			if(pSprite.statnum != kStatDude)
				continue;

			PLAYER pPlayer = null;
			if(IsPlayerSprite(pSprite))
				pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];

			if(pPlayer != null || !TeleportDudes(pDest.sectnum)) {
//XXX			if ( (byte_1A9C79 & 2) == 0 )
					TeleportDamage(pXSector.data, pDest.sectnum);

				pSprite.x = pDest.x;
				pSprite.y = pDest.y;
				pSprite.z += (sector[pDest.sectnum].floorz - sector[nSector].floorz);
				pSprite.ang = pDest.ang;
				changespritesect( (short)i, pDest.sectnum );
				sfxStart3DSound(pDest, 201, -1, 0);

		        sprXVel[i] = 0;
		        sprYVel[i] = 0;
		        sprZVel[i] = 0;
		        viewBackupSpriteLoc(i, pSprite);

				if (pPlayer != null)
				{
					// fix backup position for interpolation to work properly
					viewUpdatePlayerLoc(pPlayer);
					gPlayer[pPlayer.nPlayer].weapOffdZ = gPlayer[pPlayer.nPlayer].viewOffdZ = 0;
					gPlayer[pPlayer.nPlayer].ang = pSprite.ang;
				}
			}
		}
	}

	public static void OperatePath( int nSector, XSECTOR pXSector, int evCommand ) {
		if(!(nSector < numsectors)) game.dassert("nSector < numsectors");

		if(pXSector.marker0 == -1)
			return;

		SPRITE pMarker = sprite[pXSector.marker0];
		XSPRITE pXMarker = xsprite[pMarker.extra];

		int nMarker2 = -1;
		for ( nMarker2 = headspritestat[kStatMarker2]; nMarker2 >= 0; nMarker2 = nextspritestat[nMarker2])
		{
			SPRITE pMarker2 = sprite[nMarker2];
		    if (pMarker2.lotag == kPathMarker && xsprite[pMarker2.extra].data1 == pXMarker.data2)
		      break;
		}

		if(nMarker2 == -1) {
			System.err.println("Unable to find path marker with id " + pXMarker.data2);
			return;
		}

		// by NoOne: trigger marker after it gets reached
		if (pXMarker.state != 1)
			trTriggerSprite(pMarker.xvel,pXMarker,kCommandOn);

		pXSector.marker1 = nMarker2;
		pXSector.onFloorZ = sprite[nMarker2].z;
		pXSector.offFloorZ = pMarker.z;

		if(evCommand == kCommandOn) {
			pXSector.busy = 0;
			pXSector.state = 0;
    		int nDelta = kMaxBusyValue / ClipLow(pXMarker.busyTime * kTimerRate / 10, 1);

    		AddBusy(nSector, 7, nDelta);
			if( pXMarker.data3 != 0)
				sfxSectorBusy(nSector, pXMarker.data3, 0);
		}
	}

	public static void OperateSector( int nSector, XSECTOR pXSector, int evCommand )
	{
		SECTOR pSector = sector[nSector];

		// special handling for lock/unlock commands
		switch ( evCommand )
		{
			case kCommandLock:
				pXSector.locked = 1;
				break;

			case kCommandUnlock:
				pXSector.locked = 0;
				if (pSector.lotag == kSectorCounter) {
					pXSector.state=0;
					evPostCallback(nSector, SS_SECTOR, 0, kCommandCounter);
				}
				break;

			case kCommandToggleLock:
				pXSector.locked ^= 1;
				if (pSector.lotag == kSectorCounter && pXSector.locked != 1) {
					pXSector.state=0;
					evPostCallback(nSector, SS_SECTOR, 0, kCommandCounter);
				}
				break;

			case kCommandStopOff:
				pXSector.stopFlag[0] = true;
				break;

			case kCommandStopOn:
				pXSector.stopFlag[1] = true;
				break;

			case kCommandStopNext:
				pXSector.stopFlag[0] = true;
				pXSector.stopFlag[1] = true;
				break;

			default:
				switch ( pSector.lotag )
			    {
				    case kSectorZSprite:
				    	OperateDoor(nSector, pXSector, evCommand, 1);
				    	break;
				    case kSectorZMotion:
				    	OperateDoor(nSector, pXSector, evCommand, 2);
				    	break;
				    case kSectorSlideMarked:
			        case kSectorSlide:
			        	OperateDoor(nSector, pXSector, evCommand, 3);
			        	break;
			        case kSectorRotateMarked:
			        case kSectorRotate:
			        	OperateDoor(nSector, pXSector, evCommand, 4);
			        	break;
			        case kSectorRotateStep:
			        	if( evCommand == kCommandOn) {
			        		pXSector.busy = 0;
			        		pXSector.state = 0;
			        		int nDelta = kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOn] * kTimerRate / 10, 1);
							AddBusy(nSector, 5, nDelta);
							sfxSectorBusy(nSector, 0);
			        	}

			        	if( evCommand == kCommandOff) {
			        		pXSector.busy = 65536;
			        		pXSector.state = 1;
			        		int nDelta = -kMaxBusyValue / ClipLow(pXSector.busyTime[kCommandOff] * kTimerRate / 10, 1);
							AddBusy(nSector, 5, nDelta);
							sfxSectorBusy(nSector, 0);
			        	}
			        	break;
			        case kSectorTeleport:
			        	 OperateTeleport(nSector, pXSector);
			        	break;
			        case kSectorPath:
			        	OperatePath(nSector, pXSector, evCommand);
			        	break;
			        default:
			        	if ( pXSector.busyTime[0] != 0 || pXSector.busyTime[1] != 0 )
			        		OperateDoor(nSector, pXSector, evCommand, 6);
			        	else
			        	{
				        	if( evCommand == kCommandOff)
				        		SetSectorState(nSector, pXSector, 0);
				        	else if( evCommand == kCommandOn)
				        		SetSectorState(nSector, pXSector, 1);
				        	else
				        		SetSectorState(nSector, pXSector, pXSector.state ^ 1);
			            }

			        	break;
			    }
				break;
		}
	}

	public static void PathSectorInit( int nSector, XSECTOR pXSector ) {
		if(!(nSector >= 0 && nSector < numsectors))game.dassert("nSector >= 0 && nSector < numsectors");


		int nMarker2 = -1;
		for ( nMarker2 = headspritestat[kStatMarker2]; nMarker2 >= 0; nMarker2 = nextspritestat[nMarker2])
		{
			SPRITE pSprite = sprite[nMarker2];
		    if ( pSprite.lotag == kPathMarker && xsprite[pSprite.extra].data1 == pXSector.data )
		      break;
		}

		pXSector.marker0 = nMarker2;
		secPath[nSector] = nMarker2;
		if ( pXSector.state != 0 )
		    evPost(nSector, SS_SECTOR, 0, kCommandOn);
	}

	public static void LinkSector( int nSector, XSECTOR pXSector, int event )
	{
		SECTOR pSector = sector[nSector];
		int nBusy = GetSourceBusy(event);

		switch ( pSector.lotag )
		{
			case kSectorZSprite:
				VSpriteBusy( nSector, nBusy );
				break;

			case kSectorZMotion:
				VDoorBusy( nSector, nBusy );
				break;

			case kSectorSlide:
			case kSectorSlideMarked:
				HDoorBusy( nSector, nBusy );
				break;

			case kSectorRotate:
			case kSectorRotateMarked:
				RDoorBusy( nSector, nBusy );
				break;

			/*
				add link support for counter sectors
				they can now change necessary type and count of types
			*/
			case kSectorCounter:
				int nXIndex;
				nXIndex = sector[getIndex(event)].extra;
				if(!(nXIndex > 0 && nXIndex < kMaxXSectors))
					game.dassert("nXIndex > 0 && nXIndex < kMaxXSectors");
				pXSector.waitTime[1]=xsector[nXIndex].waitTime[1];
				pXSector.data=xsector[nXIndex].data;
				break;

			default:

				pXSector.busy = nBusy;
				if ( (nBusy & kFluxMask) == 0 )
					SetSectorState(nSector, pXSector, nBusy >> 16);
				break;
	 	}
	}

	public static void LinkSprite( int nSprite, XSPRITE pXSprite, int event )
	{
		SPRITE pSprite = sprite[nSprite];
		int nBusy = GetSourceBusy(event);
		//System.out.println("LINKING: "+pSprite.lotag);
		switch ( pSprite.lotag )
		{
			// These can be linked too now
			case kMarkerLowerWater:
			case kMarkerUpperWater:
			case kMarkerUpperGoo:
			case kMarkerLowerGoo:
			case kMarkerUpperLink:
			case kMarkerLowerLink:
			case kMarkerUpperStack:
			case kMarkerLowerStack:
				if (getType(event) != SS_SPRITE) break;
				SPRITE pSprite2 = sprite[getIndex(event)];
				if (pSprite2.extra < 0) break;
				XSPRITE pXSprite2 = xsprite[pSprite2.extra];

				// Only lower to lower and upper to upper linking allowed.
				switch(pSprite.lotag) {
					case kMarkerLowerWater:
					case kMarkerLowerLink:
					case kMarkerLowerStack:
					case kMarkerLowerGoo:
						switch(pSprite2.lotag){
							case kMarkerLowerWater:
							case kMarkerLowerLink:
							case kMarkerLowerStack:
							case kMarkerLowerGoo:
								break;
							default:
								return;
						}
						break;

					case kMarkerUpperWater:
					case kMarkerUpperLink:
					case kMarkerUpperStack:
					case kMarkerUpperGoo:
						switch(pSprite2.lotag){
							case kMarkerUpperWater:
							case kMarkerUpperLink:
							case kMarkerUpperStack:
							case kMarkerUpperGoo:
								break;
							default:
								return;
						}
						break;
				}

				// swap link location
				/*short tmp1 = pXSprite2.data1;*/
				/*pXSprite2.data1 = pXSprite.data1;*/
				/*pXSprite.data1 = tmp1;*/

				if (pXSprite.data2 < Globals.kMaxPalettes &&
					pXSprite2.data2 < Globals.kMaxPalettes)
				{
					// swap palette
					short tmp2 = pXSprite2.data2;
					pXSprite2.data2 = pXSprite.data2;
					pXSprite.data2 = tmp2;
				}


				// swap link type						// swap link owners (sectors)
				short tmp3 = pSprite2.lotag;			//short tmp7 = pSprite2.owner;
				pSprite2.lotag = pSprite.lotag;			//pSprite2.owner = pSprite.owner;
				pSprite.lotag = tmp3;					//pSprite.owner = tmp7;

				// Deal with linked sectors
				SECTOR pSector = sector[pSprite.sectnum];
				SECTOR pSector2 = sector[pSprite2.sectnum];

				// Check for underwater
				XSECTOR pXSector = null;	XSECTOR pXSector2 = null;
				if (pSector.extra > 0) pXSector = xsector[pSector.extra];
				if (pSector2.extra > 0) pXSector2 = xsector[pSector2.extra];
				if (pXSector != null && pXSector2 != null){
						boolean tmp6 = pXSector.Underwater;
						pXSector.Underwater = pXSector2.Underwater;
						pXSector2.Underwater = tmp6;
				}

				// optionally swap floorpic
				if (pXSprite2.data3 == 1){
					short tmp4 = pSector.floorpicnum;
					pSector.floorpicnum = pSector2.floorpicnum;
					pSector2.floorpicnum = tmp4;
				}

				// optionally swap ceilpic
				if (pXSprite2.data4 == 1){
					short tmp5 = pSector.ceilingpicnum;
					pSector.ceilingpicnum = pSector2.ceilingpicnum;
					pSector2.ceilingpicnum = tmp5;
				}
				break;
			// add a way to link between path markers, so path sectors can change their path.
			case kPathMarker:

				// only path marker to path marker link allowed
				if (getType(event) == SS_SPRITE)
				{
					int nXSprite2 = sprite[getIndex(event)].extra;
					// get master path marker data fields
					pXSprite.data1 = xsprite[nXSprite2].data1;
					pXSprite.data2 = xsprite[nXSprite2].data2;
					pXSprite.data3 = xsprite[nXSprite2].data3; // include soundId(?)

					// get master path marker busy and wait times
					pXSprite.busyTime = xsprite[nXSprite2].busyTime;
					pXSprite.waitTime = xsprite[nXSprite2].waitTime;

				}
				break;

			case kSwitchCombination:
				// should only be linked to a master switch
				if (getType(event) == SS_SPRITE)
				{
					int nXSprite2 = sprite[getIndex(event)].extra;
					if(!(nXSprite2 > 0 && nXSprite2 < kMaxXSprites))
						game.dassert("nXSprite2 > 0 && nXSprite2 < kMaxXSprites");

					// get master switch selection
					pXSprite.data1 = xsprite[nXSprite2].data1;

					// at right combination?
					if (pXSprite.data1 == pXSprite.data2)
						SetSpriteState(nSprite, pXSprite, 1);
					else
						SetSpriteState(nSprite, pXSprite, 0);
				}
				break;

			default:
				pXSprite.busy = nBusy;
				if ( (nBusy & kFluxMask) == 0 )
				 	SetSpriteState(nSprite, pXSprite, nBusy >> 16);
				break;
	 	}
	}

	public static void LinkWall( int nWall, XWALL pXWall, int event )
	{
		WALL pWall = wall[nWall];

		int nBusy = GetSourceBusy(event);

		switch ( pWall.lotag )
		{
			default:
				pXWall.busy = nBusy;
				if ( (nBusy & kFluxMask) == 0 )
				 	SetWallState(nWall, pXWall, nBusy >> 16);
				break;
	 	}
	}

	public static void trTriggerSector( int nSector, XSECTOR pXSector, int command )
	{
//		dprintf("TriggerSector: nSector=%i, command=%i\n", nSector, command);

		// bypass locked XObjects
		if ( pXSector.locked != 0 )
			return;

		// bypass triggered one-shots
		if ( pXSector.isTriggered )
			return;

		if ( pXSector.triggerOnce )
			pXSector.isTriggered = true;

		if ( pXSector.decoupled )
		{
			if ( pXSector.txID != 0 )
				evSend(nSector, SS_SECTOR, pXSector.txID, pXSector.command);
		}
		else
		{
			// operate the sector
			OperateSector( nSector, pXSector, command );
		}
	}

	public static void pastePropertiesInObj(int type, int nDest, int event){

		if (getType(event) != SS_SPRITE) return;
		SPRITE pSource = sprite[getIndex(event)]; XSPRITE pXSource = xsprite[pSource.extra];

		if (pSource.lotag == kMarkerWarpDest) {
			/* - Allows teleport any sprite from any location to the source destination - */
			useTeleportTarget(pXSource,sprite[nDest]);
			return;
		} else if (pSource.lotag == kGDXSpriteDamager) {
			/* - damages xsprite via TX ID	- */
			if (xsprite[sprite[nDest].extra].health > 0) useSpriteDamager(pXSource,sprite[nDest]);
			return;

		} else if (pSource.lotag == kGDXEffectSpawner) {
			/* - Effect Spawner can spawn any effect passed in data2 on it's or txID sprite - */
			if (pXSource.data2 < 0 || pXSource.data2 >= kFXMax) return;
			else if (type == SS_SPRITE)  useEffectGen(pXSource,sprite[nDest]);
			return;
		} else if (pSource.lotag == kGDXSeqSpawner) {
			/* - SEQ Spawner takes data2 as SEQ ID and spawns it on it's or TX ID object - */
			if (pXSource.data2 > 0 && !BuildGdx.cache.contains(pXSource.data2, seq)) return;
			else if (pXSource.data2 >= 0) useSeqSpawnerGen(pXSource, type, nDest);
			return;
		} else if (pSource.lotag == kGDXWindGenerator){
			/* - Wind generator via TX or for current sector if TX ID not specified - */
			/* - sprite.ang = sector wind direction									- */
			/* - data1 = randomness settings										- */
			/* - 		 0: no randomness											- */
			/* - 		 1: randomize wind velocity in data2						- */
			/* - 		 2: randomize current generator sprite angle				- */
			/* - 		 3: randomize both wind velocity and sprite angle			- */
			/* - data2 = wind velocity												- */
			/* - data3 = pan floor and ceiling settings								- */
			/* - 		 0: use sector pan settings									- */
			/* - 		 1: pan only floor											- */
			/* - 		 2: pan only ceiling										- */
			/* - 		 3: pan both												- */

			/* - hi-tag = 1: force windAlways and panAlways							- */

			if (type != SS_SECTOR) return;
			useSectorWindGen(pXSource,sector[nDest]);
			return;
		} else if (pSource.lotag == kGDXObjDataAccumulator) {
			/* - Object Data Accumulator allows to perform sum and sub operations in data fields of object - */
			/* - data1 = destination data index 															- */
			/* - data2 = min value																			- */
			/* - data3 = max value																			- */
			/* - data4 = step value																			- */
			/* - min > max = sub, 	min < max = sum															- */

			/* - hitag: 0 = force OFF if goal value was reached for all objects		     					- */
			/* - hitag: 2 = force swap min and max if goal value was reached								- */
			/* - hitag: 3 = force reset counter	                                           					- */

			long data = getDataFieldOfObject(type,nDest,pXSource.data1);
			if (data == -65535)
				game.ThrowError("Can't get data field #"+pXSource.data1+" of object #"+nDest+", type "+type+"!\n");

			if (pXSource.data2 < pXSource.data3) {

				if (data < pXSource.data2) data = pXSource.data2;
				if (data > pXSource.data3) data = pXSource.data3;

				if ((data+=pXSource.data4) >= pXSource.data3) {

					switch (pSource.hitag) {
						case 0:
						case 1:
							if (data > pXSource.data3) data = pXSource.data3;
							break;
						case 2:
							if (data > pXSource.data3) data = pXSource.data3;
							if (!goalValueIsReached(pXSource)) break;
							short tmp = pXSource.data3;
							pXSource.data3 = pXSource.data2;
							pXSource.data2 = tmp;
							break;
						case 3:
							if (data > pXSource.data3) data = pXSource.data2;
							break;
					}
				}

				//System.err.println("++ "+pXSource.txID+": "+data);

			} else if (pXSource.data2 > pXSource.data3) {

				if (data > pXSource.data2) data = pXSource.data2;
				if (data < pXSource.data3) data = pXSource.data3;

				if ((data-=pXSource.data4) <= pXSource.data3) {
					switch (pSource.hitag) {
						case 0:
						case 1:
							if (data < pXSource.data3) data = pXSource.data3;
							break;
						case 2:
							if (data < pXSource.data3) data = pXSource.data3;
							if (!goalValueIsReached(pXSource)) break;
							short tmp = pXSource.data3;
							pXSource.data3 = pXSource.data2;
							pXSource.data2 = tmp;
							break;
						case 3:
							if (data < pXSource.data3) data = pXSource.data2;
							break;
					}
				}

				//System.err.println("-- "+pXSource.txID+": "+data);
			}

			setDataValueOfObject(type,nDest,pXSource.data1,(short) data);
			return;

		} else if (pSource.lotag == kGDXObjDataChanger) {

			/* - Data field changer via TX - */
			/* - data1 = sprite data1 / sector data / wall data	- */
			/* - data2 = sprite data2	- */
			/* - data3 = sprite data3	- */
			/* - data4 = sprite data4	- */

			/* - hitag: 1 = treat "ignore value" as actual value - */

			switch(type){
				case SS_SECTOR:
					if ((pSource.hitag & 1) != 0 || (pXSource.data1 != -1 && pXSource.data1 != 32767))
						xsector[sector[nDest].extra].data = pXSource.data1;
					break;

				case SS_SPRITE:
					if ((pSource.hitag & 1) != 0 || (pXSource.data1 != -1 && pXSource.data1 != 32767))
						xsprite[sprite[nDest].extra].data1 = pXSource.data1;

					if ((pSource.hitag & 1) != 0 || (pXSource.data2 != -1 && pXSource.data2 != 32767))
						xsprite[sprite[nDest].extra].data2 = pXSource.data2;

					if ((pSource.hitag & 1) != 0 || (pXSource.data3 != -1 && pXSource.data3 != 32767))
						xsprite[sprite[nDest].extra].data3 = pXSource.data3;

					if ((pSource.hitag & 1) != 0 || (pXSource.data4 != -1 && pXSource.data1 != 65535))
						xsprite[sprite[nDest].extra].data4 = pXSource.data4;
					break;

				case SS_WALL:
					if ((pSource.hitag & 1) != 0 || (pXSource.data1 != -1 && pXSource.data1 != 32767))
						xwall[wall[nDest].extra].data = pXSource.data1;
					break;
			}

		} else if (pSource.lotag == kGDXSectorFXChanger) {

			/* - FX Wave changer for sector via TX - */
			/* - data1 = Wave 	- */
			/* - data2 = Amplitude	- */
			/* - data3 = Freq	- */
			/* - data4 = Phase	- */

			if (type != SS_SECTOR) return;
			XSECTOR pXSector = xsector[sector[nDest].extra];

			if (valueIsBetween(pXSource.data1,-1,32767))
				pXSector.wave = (pXSource.data1 > 11) ? 11 : pXSource.data1;

			int oldAmplitude = pXSector.amplitude;
			if (pXSource.data2 >= 0) pXSector.amplitude = (pXSource.data2 > 127) ? 127 : pXSource.data2;
			else if (pXSource.data2 < -1) pXSector.amplitude = (pXSource.data2 < -127) ? -127 : pXSource.data2;

			if (valueIsBetween(pXSource.data3,-1,32767))
				pXSector.freq = (pXSource.data3 > 255) ? 255 : pXSource.data3;

			if (valueIsBetween(pXSource.data4,-1,65535))
				pXSector.phase = (pXSource.data4 > 255) ? 255 : (short) pXSource.data4;

			// force shadeAlways
			if ((pSource.hitag & 1) != 0)
				pXSector.shadeAlways = true;

			// add to shadeList if amplitude was set to 0 previously
			if (oldAmplitude == 0 && pXSector.amplitude != 0) {
				for (int i = shadeCount; i >= 0; i--) {
					if (shadeList[i] == sector[nDest].extra) break;
					else if (i == 0) shadeList[shadeCount++] = sector[nDest].extra;
				}
			}


		} else if (pSource.lotag == kGDXDudeTargetChanger) {

			/* - Target changer for dudes via TX																		- */

			/* - data1 = target dude data1 value (can be zero)															- */
			/* 			 666: attack everyone, even if data1 id does not fit, except mates (if any)						- */
			/* - data2 = 0: AI deathmatch mode																			- */
			/*			 1: AI team deathmatch mode																		- */
			/* - data3 = 0: do not force target to fight dude back and *do not* awake some inactive monsters in sight	- */
			/* 			 1: force target to fight dude back	and *do not* awake some inactive monsters in sight			- */
			/*			 2: force target to fight dude back	and awake some inactive monsters in sight					- */
			/* - data4 = 0: do not ignore player(s) (even if enough targets in sight)									- */
			/*			 1: try to ignore player(s) (while enough targets in sight)										- */
			/*			 2: ignore player(s) (attack only when no targets in sight at all)								- */
			/*			 3: go to idle state if no targets in sight and ignore player(s) always							- */
			/*			 4: follow player(s) when no targets in sight, attack targets if any in sight					- */

			if (type != SS_SPRITE || !IsDudeSprite(sprite[nDest]) || sprite[nDest].statnum != 6) return;
			SPRITE pSprite = sprite[nDest]; XSPRITE pXSprite = xsprite[pSprite.extra];
			SPRITE pTarget = null; XSPRITE pXTarget = null; int receiveHp = 33 + Gameutils.Random(33);
			DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase]; int matesPerEnemy = 1;

			// dude is burning?
			if (pXSprite.burnTime > 0 && pXSprite.burnSource >= 0 && pXSprite.burnSource < kMaxSprites){
				if (!IsBurningDude(pSprite))
				{
					SPRITE pBurnSource = sprite[pXSprite.burnSource];
					if (pBurnSource.extra >= 0) {
						if (pXSource.data2 == 1 && isMateOf(pXSprite,xsprite[pBurnSource.extra])) {
							pXSprite.burnTime = 0;

							// heal dude a bit in case of friendly fire
							if (pXSprite.data4 > 0 && pXSprite.health < pXSprite.data4)
								actHealDude(pXSprite, receiveHp, pXSprite.data4);
							else if (pXSprite.health < pDudeInfo.startHealth)
								actHealDude(pXSprite, receiveHp, pDudeInfo.startHealth);

							//aiClock[pSprite.xvel] = gFrameClock;

						} else if (pBurnSource.xvel == pSprite.xvel || xsprite[pBurnSource.extra].health <= 0) {
							pXSprite.burnTime = 0;
						}
					}
				} else {
					actKillSprite(pSource.xvel,pSprite,0,65535);
					return;
				}
			}

			SPRITE pPlayer = targetIsPlayer(pXSprite);
			// special handling for player(s) if target changer data4 > 2.
			if (pPlayer != null && pXSprite.aiState != genIdle) {
				if (pXSource.data4 == 3) {
					aiSetTarget(pXSprite,pSprite.x,pSprite.y,pSprite.z);
					aiNewState(pSprite, pXSprite, genIdle);
					aiActive[pSprite.xvel] = 0;
					if (pSprite.lotag == 254)
						removeLeech(leechIsDropped(pSprite));
				} else if (pXSource.data4 == 4) {
					aiSetTarget(pXSprite,pPlayer.x,pPlayer.y,pPlayer.z);
					if (pSprite.lotag == 254)
						removeLeech(leechIsDropped(pSprite));
				}
			}

			int maxAlarmDudes = 8 + Random(8);
			if (pXSprite.target > -1 && sprite[pXSprite.target].extra > -1 && pPlayer == null) {
				pTarget = sprite[pXSprite.target]; pXTarget = xsprite[pTarget.extra];

				if (unitCanFly(pSprite) && isMeleeUnit(pTarget) && !unitCanFly(pTarget))
					pSprite.hitag |= 0x0002;
				else if (unitCanFly(pSprite))
					pSprite.hitag &= ~0x0002;

				if (!IsDudeSprite(pTarget) || pXTarget.health < 1 || !dudeCanSeeTarget(pXSprite,pDudeInfo,pTarget)) {
					aiSetTarget(pXSprite,pSprite.x,pSprite.y,pSprite.z);

				// dude attack or attacked by target that does not fit by data id?
				} else if (pXSource.data1 != 666 && pXTarget.data1 != pXSource.data1) {
					if (affectedByTargetChg(pXTarget) != null) {

						// force stop attack target
						aiSetTarget(pXSprite,pSprite.x,pSprite.y,pSprite.z);
						if (pXSprite.burnSource == pTarget.xvel) {
							pXSprite.burnTime = 0;
							pXSprite.burnSource = -1;
						}

						// force stop attack dude
						aiSetTarget(pXTarget,pTarget.x,pTarget.y,pTarget.z);
						if (pXTarget.burnSource == pSprite.xvel) {
							pXTarget.burnTime = 0;
							pXTarget.burnSource = -1;
						}
					}
				// instantly kill annoying spiders, rats, hands etc if dude is big enough
				} else if (isAnnoyingUnit(pTarget) && !isAnnoyingUnit(pSprite) && engine.getTile(pSprite.picnum).getHeight() >= 60 &&
					getTargetDist(pSprite,pDudeInfo,pTarget) < 2) {

					actKillSprite(pSource.xvel,pTarget,0,65535);
					aiSetTarget(pXSprite,pSprite.x,pSprite.y,pSprite.z);

				} else if (pXSource.data2 == 1 && isMateOf(pXSprite,pXTarget)) {
					SPRITE pMate = pTarget; XSPRITE pXMate = pXTarget;

					// heal dude
					if (pXSprite.data4 > 0 && pXSprite.health < pXSprite.data4)
						actHealDude(pXSprite, receiveHp, pXSprite.data4);
					else if (pXSprite.health < pDudeInfo.startHealth)
						actHealDude(pXSprite, receiveHp, pDudeInfo.startHealth);

					// heal mate
					if (pXMate.data4 > 0 && pXMate.health < pXMate.data4)
						actHealDude(pXMate, receiveHp, pXMate.data4);
					else {
						DudeInfo pTDudeInfo = dudeInfo[pMate.lotag - kDudeBase];
						if (pXMate.health < pTDudeInfo.startHealth)
							actHealDude(pXMate, receiveHp, pTDudeInfo.startHealth);
					}

					if (pXMate.target > -1 && sprite[pXMate.target].extra >= 0) {
						pTarget = sprite[pXMate.target];
						// force mate stop attack dude, if he does
						if (pXMate.target == pSprite.xvel) {
							aiSetTarget(pXMate,pMate.x,pMate.y,pMate.z);
						} else if (!isMateOf(pXSprite,xsprite[pTarget.extra])) {
							// force dude to attack same target that mate have
							//System.out.println("---------------> MATE CHANGES TARGET OF DUDE TO: "+pTarget.lotag);
							aiSetTarget(pXSprite,pTarget.xvel);
							return;

						} else {
							// force mate to stop attack another mate
							aiSetTarget(pXMate,pMate.x,pMate.y,pMate.z);
						}
					}

					// force dude stop attack mate, if target was not changed previously
					if (pXSprite.target == pMate.xvel)
						aiSetTarget(pXSprite,pSprite.x,pSprite.y,pSprite.z);

				// check if targets aims player then force this target to fight with dude
				} else if (targetIsPlayer(pXTarget) != null) {
						//System.err.println("!!!!!!!! TARGET AIMS TO PLAYER");
						aiSetTarget(pXTarget,pSprite.xvel);
				}

				int mDist = 3; if (isMeleeUnit(pSprite)) mDist = 2;
				//int mDist = 2;
				if (pXSprite.target >= 0 && getTargetDist(pSprite,pDudeInfo,sprite[pXSprite.target]) < mDist) {
					//System.out.println("====> CONTINUING FIGHT WITH TARGET "+sprite[pXSprite.target].lotag);
					//aiSetTarget(pXSprite,sprite[pXSprite.target].xvel);
					if (!isActive(pSprite.xvel)) aiActivateDude(pSprite, pXSprite);
					return;
				// lets try to look for target that fits better by distance
				} else if ((gFrameClock & 256) != 0 && (pXSprite.target < 0 || getTargetDist(pSprite,pDudeInfo,pTarget) >= mDist)) {
					pTarget = getTargetInRange(pSprite,0,mDist,pXSource.data1,pXSource.data2);
					if (pTarget != null){
						pXTarget = xsprite[pTarget.extra];

						// Make prev target not aim in dude
						if (pXSprite.target > -1) {
							//System.out.println(" => FOUND BETTER TARGET");
							SPRITE prvTarget = sprite[pXSprite.target];
							aiSetTarget(xsprite[prvTarget.extra],prvTarget.x,prvTarget.y,prvTarget.z);
							if (!isActive(pTarget.xvel))
								aiActivateDude(pTarget, pXTarget);
						}

						// Change target for dude
						aiSetTarget(pXSprite,pTarget.xvel);
						if (!isActive(pSprite.xvel))
							aiActivateDude(pSprite, pXSprite);

						// ...and change target of target to dude to force it fight
						if (pXSource.data3 > 0 && pXTarget.target != pSprite.xvel){
							aiSetTarget(pXTarget,pSprite.xvel);
							if (!isActive(pTarget.xvel))
								aiActivateDude(pTarget, pXTarget);
						}
						//System.out.println(" => FOUND BETTER TARGET");
						return;
					}
				}
			}

			if ((pXSprite.target < 0 || pPlayer != null) && (gFrameClock & 32) != 0) {
				// try find first target that dude can see
				for (int nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
					pTarget = sprite[nSprite]; pXTarget = xsprite[pTarget.extra];

					if (pXTarget.target == pSprite.xvel){
						//System.out.println(" ====> TARGET ALREADY AIMING DUDE");
						aiSetTarget(pXSprite,pTarget.xvel);
						return;
					}

					// skip non-dudes and players
					if (!IsDudeSprite(pTarget) || (IsPlayerSprite(pTarget) && pXSource.data4 > 0))
						continue;
					// avoid self aiming, those who dude can't see, and those who dude own
					else if (!dudeCanSeeTarget(pXSprite,pDudeInfo,pTarget) || pSprite.xvel == pTarget.xvel || pTarget.owner == pSprite.xvel) {
						continue;
					// if Target Changer have data1 = 666, everyone can be target, except AI team mates.
					} else if (pXSource.data1 != 666) {
						if (pXSource.data1 != pXTarget.data1)
							continue;
					}

					// don't attack immortal, burning dudes and mates
					if (IsBurningDude(pTarget) || !IsKillableDude(pTarget) || (pXSource.data2 == 1 && isMateOf(pXSprite,pXTarget)))
						continue;

					if (pXSource.data2 == 0 || (pXSource.data2 == 1 && !isMatesHaveSameTarget(pXSprite,pTarget,matesPerEnemy))) {

						// Change target for dude
						aiSetTarget(pXSprite,pTarget.xvel);
						if (!isActive(pSprite.xvel))
							aiActivateDude(pSprite, pXSprite);

						// ...and change target of target to dude to force it fight
						if (pXSource.data3 > 0 && pXTarget.target != pSprite.xvel){
							aiSetTarget(pXTarget,pSprite.xvel);
							if (!isActive(pTarget.xvel))
								aiActivateDude(pTarget, pXTarget);

							if (pXSource.data3 == 2)
								disturbDudesInSight(pTarget,maxAlarmDudes);
						}
						return;
					}
					break;
				}
			}

			// got no target - let's ask mates if they have targets
			if ((pXSprite.target < 0 || pPlayer != null) && pXSource.data2 == 1 && (gFrameClock & 64) != 0) {
				SPRITE pMateTarget = null;
				//System.out.println("/////// REQUESTING TARGETS");
				if ((pMateTarget = getMateTargets(pXSprite)) != null && pMateTarget.extra > 0){
					XSPRITE pXMateTarget = xsprite[pMateTarget.extra];
					if (dudeCanSeeTarget(pXSprite,pDudeInfo,pMateTarget)) {
						//System.out.println("/////// GOING TO HELP MATE");
						if (pXMateTarget.target < 0) {
							aiSetTarget(pXMateTarget,pSprite.xvel);
							if (IsDudeSprite(pMateTarget) && !isActive(pMateTarget.xvel))
								aiActivateDude(pMateTarget, pXMateTarget);
						}

						aiSetTarget(pXSprite,pMateTarget.xvel);
						if (!isActive(pSprite.xvel))
							aiActivateDude(pSprite, pXSprite);
						return;

					// try walk in mate direction in case if not see the target
					} else if (pXMateTarget.target >= 0 && dudeCanSeeTarget(pXSprite,pDudeInfo,sprite[pXMateTarget.target])) {
						//System.out.println("?????????? CAN'T SEE THE TARGET");
						SPRITE pMate = sprite[pXMateTarget.target];
						pXSprite.target = pMateTarget.xvel;
						//pXSprite.target = -1;
						pXSprite.targetX = pMate.x;
						pXSprite.targetY = pMate.y;
						pXSprite.targetZ = pMate.z;
						if (!isActive(pSprite.xvel))
							aiActivateDude(pSprite, pXSprite);
						return;
					}
				}
			}

		} else if (pSource.lotag == kGDXObjSizeChanger) {

			/* - size and pan changer of sprite/wall/sector via TX ID 	- */
			/* - data1 = sprite xrepeat / wall xrepeat / floor xpan 	- */
			/* - data2 = sprite yrepeat / wall yrepeat / floor ypan 	- */
			/* - data3 = sprite xoffset / wall xoffset / ceil xpan 		- */
			/* - data3 = sprite yoffset / wall yoffset / ceil ypan 		- */

			if (pXSource.data1 > 255) pXSource.data1 = 255;
			if (pXSource.data2 > 255) pXSource.data2 = 255;
			if (pXSource.data3 > 255) pXSource.data3 = 255;
			if (valueIsBetween(pXSource.data4,255,65535))
				pXSource.data4 = 255;

			switch(type){
				case SS_SECTOR:
					if (valueIsBetween(pXSource.data1,-1,32767))
						sector[nDest].floorxpanning = pXSource.data1;

					if (valueIsBetween(pXSource.data2,-1,32767))
						sector[nDest].floorypanning = pXSource.data2;

					if (valueIsBetween(pXSource.data3,-1,32767))
						sector[nDest].ceilingxpanning = pXSource.data3;

					if (valueIsBetween(pXSource.data4,-1,65535))
						sector[nDest].ceilingypanning = (short) pXSource.data4;
					break;

				case SS_SPRITE:
					if (valueIsBetween(pXSource.data1,-1,32767)) {
						if (pXSource.data1 < 1) sprite[nDest].xrepeat = 0;
						else sprite[nDest].xrepeat = pXSource.data1;
					}

					if (valueIsBetween(pXSource.data2,-1,32767)) {
						if (pXSource.data2 < 1) sprite[nDest].yrepeat = 0;
						else sprite[nDest].yrepeat = pXSource.data2;
					}

					if (valueIsBetween(pXSource.data3,-1,32767))
						sprite[nDest].xoffset = pXSource.data3;

					if (valueIsBetween(pXSource.data4,-1,65535))
						sprite[nDest].yoffset = (short) pXSource.data4;

					break;

				case SS_WALL:
					if (valueIsBetween(pXSource.data1,-1,32767))
						wall[nDest].xrepeat = pXSource.data1;

					if (valueIsBetween(pXSource.data2,-1,32767))
						wall[nDest].yrepeat = pXSource.data2;

					if (valueIsBetween(pXSource.data3,-1,32767))
						wall[nDest].xpanning = pXSource.data3;

					if (valueIsBetween(pXSource.data4,-1,65535))
						wall[nDest].ypanning = (short) pXSource.data4;

					break;
			}

		} else if (pSource.lotag == kGDXObjPicnumChanger) {

			/* - picnum changer can change picnum of sprite/wall/sector via TX ID - */
			/* - data1 = sprite pic / wall pic / sector floor pic 				 - */
			/* - data3 = sprite pal / wall pal / sector floor pic				- */

			switch (type){
				case SS_SECTOR:
					if (valueIsBetween(pXSource.data1,-1,32767))
						sector[nDest].floorpicnum = pXSource.data1;

					if (valueIsBetween(pXSource.data2,-1,32767))
						sector[nDest].ceilingpicnum = pXSource.data2;

					XSECTOR pXSector = xsector[sector[nDest].extra];
					if (valueIsBetween(pXSource.data3,-1,32767)) {
						sector[nDest].floorpal = pXSource.data3;
						if ((pSource.hitag & 0x0001) != 0)
							pXSector.floorpal = pXSource.data3;
					}

					if (valueIsBetween(pXSource.data4,-1,65535)) {
						sector[nDest].ceilingpal = (short) pXSource.data4;
						if ((pSource.hitag & 0x0001) != 0)
							pXSector.ceilpal = (short) pXSource.data4;
					}
					break;

				case SS_SPRITE:
					if (valueIsBetween(pXSource.data1,-1,32767))
						sprite[nDest].picnum = pXSource.data1;

	                if (pXSource.data2 >= 0)
	                	sprite[nDest].shade = (byte) ((pXSource.data2 > 127) ? 127 : pXSource.data2);
	                else if (pXSource.data2 < -1)
	                	sprite[nDest].shade = (byte) ((pXSource.data2 < -127) ? -127 : pXSource.data2);

					if (valueIsBetween(pXSource.data3,-1,32767))
						sprite[nDest].pal = pXSource.data3;

					break;

				case SS_WALL:
					if (valueIsBetween(pXSource.data1,-1,32767))
						wall[nDest].picnum = pXSource.data1;

					if (valueIsBetween(pXSource.data2,-1,32767))
						wall[nDest].overpicnum = pXSource.data2;

					if (valueIsBetween(pXSource.data3,-1,32767))
						wall[nDest].pal = pXSource.data3;

					break;
			}

		} else if (pSource.lotag == kGDXObjPropertiesChanger) {

			/* - properties changer can change various properties of sprite/wall/sector via TX ID 	- */
			/* - data1 = sector depth level 														- */
			/* - data2 = sector visibility 															- */
			/* - data3 = sector ceiling cstat / sprite / wall hitag 								- */
			/* - data4 = sector floor / sprite / wall cstat 										- */

			switch (type) {
				case SS_SECTOR:
					XSECTOR pXSector = xsector[sector[nDest].extra];
					switch (pXSource.data1) {
						case 0:
							pXSector.Underwater = false;
							break;
						case 1:
							pXSector.Underwater = true;
							break;
						case 2:
							pXSector.Depth = 0;
							break;
						case 3:
							pXSector.Depth = 1;
							break;
						case 4:
							pXSector.Depth = 2;
							break;
						case 5:
							pXSector.Depth = 3;
							break;
						case 6:
							pXSector.Depth = 4;
							break;
						case 7:
							pXSector.Depth = 5;
							break;
						case 8:
							pXSector.Depth = 6;
							break;
						case 9:
							pXSector.Depth = 7;
							break;
					}

					if (valueIsBetween(pXSource.data2,-1,32767)) {
						if (pXSource.data2 > 255) sector[nDest].visibility = 255;
						else sector[nDest].visibility = pXSource.data2;
					}

					if (valueIsBetween(pXSource.data3,-1,32767))
						sector[nDest].ceilingstat = pXSource.data3;

					if (valueIsBetween(pXSource.data4,-1,65535))
						sector[nDest].floorstat = (short) pXSource.data4;
					break;

				case SS_SPRITE:

					if (valueIsBetween(pXSource.data3,-1,32767))
						sprite[nDest].hitag =  pXSource.data3;

					if (valueIsBetween(pXSource.data4,-1,65535)) {
						pXSource.data4 |= kSpriteOriginAlign;
						sprite[nDest].cstat = (short) pXSource.data4;
					}


					break;
				case SS_WALL:

					if (valueIsBetween(pXSource.data3,-1,32767))
						wall[nDest].hitag = pXSource.data3;

					if (valueIsBetween(pXSource.data4,-1,65535))
						wall[nDest].cstat = (short) pXSource.data4;

					break;
			}
		}
	}

	public static void trMessageSector( int nSector, int event )
	{
		if(nSector >= numsectors)
			game.dassert( "nSector < numsectors " + nSector + " < " + numsectors);
		if(!(sector[nSector].extra > 0 && sector[nSector].extra < kMaxXSectors))
		game.dassert("sector[nSector].extra > 0 && sector[nSector].extra < kMaxXSectors");
		XSECTOR pXSector = xsector[sector[nSector].extra];

		// don't send it to the originator
		if ( pXSector.locked != 0 && getCommand(event) != kCommandUnlock && getCommand(event) != kCommandToggleLock)
			return;

		// operate the sector

		else if ( getCommand(event) == kCommandLink )
			LinkSector( nSector, pXSector, event );
		else if (getCommand(event) == kGDXCommandPaste)
			pastePropertiesInObj(SS_SECTOR,nSector,event);
		else
			OperateSector( nSector, pXSector, getCommand(event) );
	}

	public static void trTriggerWall( int nWall, XWALL pXWall, int command )
	{
//		System.out.println("TriggerWall: nWall=" + nWall + ", command=" + command);

		if(nWall >= numwalls)
			game.dassert( "nWall < numwalls");
		// bypass locked XObjects
		if ( pXWall.locked != 0 )
			return;

		// bypass triggered one-shots
		if ( pXWall.isTriggered )
			return;

		if ( pXWall.triggerOnce )
			pXWall.isTriggered = true;

		if ( pXWall.decoupled )
		{
			if ( pXWall.txID != 0 )
				evSend(nWall, SS_WALL, pXWall.txID, pXWall.command);
		}
		else
		{
			// operate the wall
			OperateWall( nWall, pXWall, command );
		}
	}

	public static void trMessageWall( int nWall, int event )
	{
		if(nWall >= numwalls)
			game.dassert( "nWall < numwalls");
		if(!(wall[nWall].extra > 0 && wall[nWall].extra < kMaxXWalls))
			game.dassert("wall[nWall].extra > 0 && wall[nWall].extra < kMaxXWalls");
		XWALL pXWall = xwall[wall[nWall].extra];

		// don't send it to the originator
		if ( pXWall.locked != 0 && getCommand(event) != kCommandUnlock && getCommand(event) != kCommandToggleLock)
			return;

		// operate the wall
		if ( getCommand(event) == kCommandLink )
			LinkWall( nWall, pXWall, event );
		else if (getCommand(event) == kGDXCommandPaste)
			pastePropertiesInObj(SS_WALL,nWall,event);
		else
			OperateWall( nWall, pXWall, getCommand(event) );
	}

	public static void trTriggerSprite( int nSprite, XSPRITE pXSprite, int command )
	{
//		dprintf("TriggerSprite: nSprite=%i, type= %i command=%i\n", nSprite, sprite[nSprite].type, command);

		// bypass locked XObjects
		if ( pXSprite.Locked != 0)
			return;

		// bypass triggered one-shots
		if ( pXSprite.isTriggered )
			return;

		if ( pXSprite.triggerOnce )
			pXSprite.isTriggered = true;

		if ( pXSprite.Decoupled )
		{
			if ( pXSprite.txID != 0 )
				evSend(nSprite, SS_SPRITE, pXSprite.txID, pXSprite.command);
		}
		else
		{
			// operate the sprite
			OperateSprite(nSprite, pXSprite, command);
		}
	}

	public static void trMessageSprite( int nSprite, int event )
	{

		// return immediately if sprite has been deleted
		if ( sprite[nSprite].statnum == kMaxStatus )
			return;
		if(!(sprite[nSprite].extra > 0 && sprite[nSprite].extra < kMaxXSprites))
			game.dassert("sprite[nSprite].extra > 0 && sprite[nSprite].extra < kMaxXSprites");
		XSPRITE pXSprite = xsprite[sprite[nSprite].extra];

		// don't send it to the originator
		if ( pXSprite.Locked != 0 && getCommand(event) != kCommandUnlock && getCommand(event) != kCommandToggleLock)
			return;

		// operate the sprite
		if ( getCommand(event) == kCommandLink )
			LinkSprite( nSprite, pXSprite, event );
		else if (getCommand(event) == kGDXCommandPaste)
			pastePropertiesInObj(SS_SPRITE,nSprite,event);
		else
			OperateSprite( nSprite, pXSprite, getCommand(event) );

	}

	public static void trProcessMotion() {

		for(int nSector = 0; nSector < numsectors; nSector++) {
			SECTOR pSector = sector[nSector];

			if(pSector.extra > 0) {
				XSECTOR pXSector = xsector[pSector.extra];

				if(pXSector.bobSpeed != 0) {


					if(pXSector.bobAlways) {
						pXSector.bobTheta += pXSector.bobSpeed;
					} else {
						if(pXSector.busy == 0)
							continue;
						pXSector.bobTheta += mulscale(pXSector.busy,pXSector.bobSpeed, 16);
					}

					int phase = mulscale(pXSector.bobZRange << 8, Sin(pXSector.bobTheta), 30);
					for ( int nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite] )
					{
						 SPRITE pSprite = sprite[nSprite];
						 if( (pSprite.cstat & 0x6000) != 0) {
							 viewBackupSpriteLoc(nSprite, pSprite);
							 pSprite.z += phase;
						 }
					}

					if(pXSector.bobFloor) {
						int oldFloorz = pSector.floorz;
						viewBackupSectorLoc(nSector, pSector);
						pSector.floorz = phase + secFloorZ[nSector];
						for ( int nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite] )
						{
							SPRITE pSprite = sprite[nSprite];
							if( (pSprite.hitag & kAttrGravity) != 0) {
								pSprite.hitag |= kAttrFalling;
							} else {
								GetSpriteExtents(pSprite);
								if ( extents_zBot >= oldFloorz && (pSprite.cstat & 0x30) == 0 )
						        {
									viewBackupSpriteLoc(nSprite, pSprite);
									 pSprite.z += phase;
						        }
							}
						}
					}
					if(pXSector.bobCeiling) {
						int oldCeilz = pSector.ceilingz;
						viewBackupSectorLoc(nSector, pSector);
						pSector.ceilingz = phase + secCeilZ[nSector];

						for ( int nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite] )
						{
							SPRITE pSprite = sprite[nSprite];

							GetSpriteExtents(pSprite);
							if ( extents_zTop <= oldCeilz && (pSprite.cstat & 0x30) == 0 )
					        {
								viewBackupSpriteLoc(nSprite, pSprite);
								 pSprite.z += phase;
					        }
						}
					}
				}
			}
		}
	}

	public static void trProcessSlope() {
		for(int nSector = 0; nSector < numsectors; nSector++) {
			SECTOR pSector = sector[nSector];
			if(pSector.filler != 0) {
				WALL pWall = wall[pSector.filler + pSector.wallptr];
				WALL pWall2 = wall[pWall.point2];
				if( pWall.nextsector >= 0) {
					int dx = (pWall2.x + pWall.x) / 2;
			        int dy = (pWall2.y + pWall.y) / 2;
			        viewBackupSectorLoc(nSector, pSector);
			        int floorslope = engine.getflorzofslope(pWall.nextsector, dx, dy);
			        engine.alignflorslope((short)nSector, dx, dy, floorslope);
			        int ceilingslope = engine.getceilzofslope(pWall.nextsector, dx, dy);
			        engine.alignceilslope((short)nSector, dx, dy, ceilingslope);
				}
			}
		}
	}

	public static void trProcessBusy()
	{
		Arrays.fill(floorVel, 0);
		Arrays.fill(ceilingVel, 0);

		for ( int i = gBusyCount - 1; i >= 0; i-- )
		{
			// temporarily store nBusy before normalization
			int tempBusy = gBusy[i].nBusy;
			gBusy[i].nBusy = ClipRange(gBusy[i].nBusy + gBusy[i].nDelta * kFrameTicks, 0, kMaxBusyValue);
			int rcode = gBusyProc[gBusy[i].busyProc].run( gBusy[i].nIndex, gBusy[i].nBusy );

			switch (rcode)
			{
				case kBusyRestore:
					gBusy[i].nBusy = tempBusy; // restore previous nBusy
					break;
				case kBusyReverse:
					gBusy[i].nBusy = tempBusy; // restore previous nBusy
					gBusy[i].nDelta = -gBusy[i].nDelta; // reverse delta
					break;
				case kBusyComplete:
					gBusyCount--;
					gBusy[i].nIndex = gBusy[gBusyCount].nIndex;
					gBusy[i].nBusy = gBusy[gBusyCount].nBusy;
					gBusy[i].nDelta = gBusy[gBusyCount].nDelta;
					gBusy[i].busyProc = gBusy[gBusyCount].busyProc;
					break;
			}
		}

		trProcessMotion();
		trProcessSlope();
	}

	public static void trInitStructs()
	{
		gBusyCount = 0;
		for (int i = 0; i < kMaxBusyArray; i++) {
			if(gBusy[i] == null)
				gBusy[i] = new BUSY();
			else gBusy[i].clear();
		}

		for (int nWall = 0; nWall < kMaxWalls; nWall++) {
			if(kwall[nWall] == null)
				kwall[nWall] = new Vector2();
			else kwall[nWall].set(0, 0);
		}

		for (int nSprite = 0; nSprite < kMaxSprites; nSprite++) {
			if(ksprite[nSprite] == null)
				ksprite[nSprite] = new Vector3();
			else ksprite[nSprite].set(0,0,0);
		}
	}

	public static void trInit() {

		int i;
		int nSector, nWall, nSprite;

		trInitStructs();

		// get wall vertice positions
		for (nWall = 0; nWall < numwalls; nWall++)
		{
			kwall[nWall].x = wall[nWall].x;
			kwall[nWall].y = wall[nWall].y;
		}

		// get sprite positions
		for ( nSprite = 0; nSprite < kMaxSprites; nSprite++ )
		{
			if ( sprite[nSprite].statnum < kMaxStatus )
			{
				ksprite[nSprite].x = sprite[nSprite].x;
				ksprite[nSprite].y = sprite[nSprite].y;
				ksprite[nSprite].z = sprite[nSprite].z;
				sprite[nSprite].zvel = sprite[nSprite].lotag;
			} else sprite[nSprite].zvel = -1;
		}

		// init wall trigger masks (must be done first)
		for (nWall = 0; nWall < numwalls; nWall++)
		{
			if ( wall[nWall].extra > 0 )
			{
				int nXWall = wall[nWall].extra;
				if(nXWall >= kMaxXWalls)
					game.dassert("nXWall < kMaxXWalls");

				XWALL pXWall = xwall[nXWall];

				if ( pXWall.state != 0 )
					pXWall.busy = kMaxBusyValue;
			}
		}

		// init sector trigger masks
		if(!((numsectors >= 0) && (numsectors < kMaxSectors)))
			game.dassert("(numsectors >= 0) && (numsectors < kMaxSectors)");

		for ( nSector = 0; nSector < numsectors; nSector++ )
		{
			SECTOR pSector = sector[nSector];

			secFloorZ[nSector] = pSector.floorz;
			secCeilZ[nSector] = pSector.ceilingz;

			int nXSector = pSector.extra;
			if ( nXSector > 0 )
			{
				if(nXSector >= kMaxXSectors)
					game.dassert("nXSector < kMaxXSectors");
				XSECTOR pXSector = xsector[nXSector];

				if ( pXSector.state != 0 )
					pXSector.busy = kMaxBusyValue;

				switch( pSector.lotag )
				{
					case kSectorZMotion:
					case kSectorZSprite:
						zMotion(nSector, pXSector, pXSector.busy, 1);
						break;
					case kSectorPath:
						PathSectorInit(nSector, pXSector);
						break;
					case kSectorSlideMarked:
					case kSectorSlide:

						SPRITE pMark0 = sprite[pXSector.marker0];
						SPRITE pMark1 = sprite[pXSector.marker1];

						// move door to off position by reversing markers
						TranslateSector(nSector, 0, -0x10000,
							pMark0.x, pMark0.y, pMark0.x, pMark0.y, pMark0.ang, pMark1.x, pMark1.y, pMark1.ang,
							pSector.lotag == kSectorSlide);

						// grab updated positions of walls
						for (i = 0; i < pSector.wallnum; i++)
						{
							nWall = pSector.wallptr + i;
							kwall[nWall].x = wall[nWall].x;
							kwall[nWall].y = wall[nWall].y;
						}

						// grab updated positions of sprites
						for (nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
						{
							ksprite[nSprite].x = sprite[nSprite].x;
							ksprite[nSprite].y = sprite[nSprite].y;
							ksprite[nSprite].z = sprite[nSprite].z;
						}

						// open door if necessary
						TranslateSector(nSector, 0, pXSector.busy,
							pMark0.x, pMark0.y, pMark0.x, pMark0.y, pMark0.ang, pMark1.x, pMark1.y, pMark1.ang,
							pSector.lotag == kSectorSlide);

						zMotion(nSector, pXSector, pXSector.busy, 1);

					break;
					case kSectorRotate:
					case kSectorRotateMarked:

						SPRITE pMark = sprite[pXSector.marker0];

						// move door to off position
						TranslateSector(nSector, 0, -0x10000,
							pMark.x, pMark.y, pMark.x, pMark.y, 0, pMark.x, pMark.y, pMark.ang,
							pSector.lotag == kSectorRotate);

						// grab updated positions of walls
						for (i = 0; i < pSector.wallnum; i++)
						{
							nWall = pSector.wallptr + i;
							kwall[nWall].x = wall[nWall].x;
							kwall[nWall].y = wall[nWall].y;
						}

						// grab updated positions of sprites
						for (nSprite = headspritesect[nSector]; nSprite >= 0; nSprite = nextspritesect[nSprite])
						{
							ksprite[nSprite].x = sprite[nSprite].x;
							ksprite[nSprite].y = sprite[nSprite].y;
							ksprite[nSprite].z = sprite[nSprite].z;
						}

						// open door if necessary
						TranslateSector(nSector, 0, pXSector.busy,
							pMark.x, pMark.y, pMark.x, pMark.y, 0, pMark.x, pMark.y, pMark.ang,
							pSector.lotag == kSectorRotate);

						zMotion(nSector, pXSector, pXSector.busy, 1);

						break;

					case kSectorCounter:
						/* No need to trigger once it, instead lock so it can be
						   unlocked and used again */
						//pXSector.triggerOnce = true;
						evPostCallback(nSector, SS_SECTOR, 0, kCommandCounter);
						break;
				}
			}
		}

		// init sprite trigger masks
		for ( nSprite = 0; nSprite < kMaxSprites; nSprite++ )
		{
			int nXSprite = sprite[nSprite].extra;
			if ( sprite[nSprite].statnum < kMaxStatus && nXSprite > 0 )
			{
				if(nXSprite >= kMaxXSprites) game.dassert("nXSprite < kMaxXSprites");
				XSPRITE pXSprite = xsprite[nXSprite];

				if ( pXSprite.state != 0)
					pXSprite.busy = kMaxBusyValue;

				// special initialization for implicit trigger types
				switch ( sprite[nSprite].lotag )
				{
					case kSwitchPadlock:
						pXSprite.triggerOnce = true;	// force trigger once
						break;
					case kThingTNTProx:
					case kGDXThingTNTProx:
						pXSprite.Proximity = true;
						break;
					case kThingFallingRock:
						if( pXSprite.state != 0 )
							sprite[nSprite].hitag |= kAttrMove | kAttrGravity | kAttrFalling;
						else sprite[nSprite].hitag &= ~(kAttrMove | kAttrGravity | kAttrFalling);

						break;

					case kWeaponItemBase: // Random weapon
					case kAmmoItemRandom: // Random ammo
					case kGDXSeqSpawner:
					case kGDXObjDataAccumulator:
					case kGDXDudeTargetChanger:
					case kGDXEffectSpawner:
					case kGDXWindGenerator:
					case kGenTrigger:
					case kGenWaterDrip:
					case kGenBloodDrip:
					case kGenFireball:
					case kGenEctoSkull:  // can shot any projectile, default 307 (ectoskull)
					case kGenDart:
					case kGenSFX:
					case kGenBubble:
					case kGenMultiBubble:
						InitGenerator( nSprite );
						break;
				}

				if ( pXSprite.Push )
					sprite[nSprite].cstat |= kSpritePushable;
				if ( pXSprite.Vector )
					sprite[nSprite].cstat |= kSpriteHitscan;
			}
		}

		evSend( 0, 0, kChannelTriggerStart, kCommandOn );

		if (pGameInfo.nGameType == kNetModeCoop)
			evSend( 0, 0, kChannelTriggerCoop, kCommandOn );
		if (pGameInfo.nGameType == kNetModeBloodBath || pGameInfo.nGameType == kNetModeTeams)
			evSend( 0, 0, kChannelTriggerMatch, kCommandOn );
		if (pGameInfo.nGameType == kNetModeTeams)
			evSend( 0, 0, kChannelTriggerTeam, kCommandOn );
	}

	public static void useTeleportTarget(XSPRITE pXSource, SPRITE pSprite) {
		SPRITE pSource = sprite[pXSource.reference];
		XSECTOR pXSector = (sector[pSource.sectnum].extra >= 0) ? xsector[sector[pSource.sectnum].extra] : null;

		if (pSprite == null) {

			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {

				if (pXSource.data1 < kMaxPlayers)  // relative to connected players
					if (pXSource.data1 != (i + 1))
						continue;
				else if (pXSource.data1 < (kDudePlayer1 + kMaxPlayers))	// absolute lotag
					if (pXSource.data1 >= kDudePlayer1 && (pXSource.data1 + (kDudePlayer1 - 1)) == gPlayer[i].pSprite.lotag)
						continue;

				useTeleportTarget(pXSource, gPlayer[i].pSprite);
				return;

			}

			return;
		}

		pSprite.x = pSource.x;	pSprite.y = pSource.y;
		pSprite.z += (sector[pSource.sectnum].floorz - sector[pSprite.sectnum].floorz);

		if ((pSource.hitag & 1) != 0) // force telefrag
			TeleportDamage(pSprite.xvel,pSource.sectnum);

		changespritesect(pSprite.xvel, pSource.sectnum);
		if (pXSector != null && pXSector.Underwater) xsprite[pSprite.extra].palette = 1;
		else xsprite[pSprite.extra].palette = 0;

		if (pXSource.data2 == 1)
			pSprite.ang = pSource.ang;

		if (pXSource.data3 == 1)
			sprXVel[pSprite.xvel] = sprYVel[pSprite.xvel] = sprZVel[pSprite.xvel] = 0;

		viewBackupSpriteLoc(pSprite.xvel, pSprite);

		if (pXSource.data4 > 0)
			sfxStart3DSound(pSource, pXSource.data4, -1, 0);

		if (IsPlayerSprite(pSprite)) {

			PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];

			viewUpdatePlayerLoc(pPlayer);
			if (pXSource.data2 == 1) {
				gPlayer[pPlayer.nPlayer].weapOffdZ = gPlayer[pPlayer.nPlayer].viewOffdZ = 0;
				gPlayer[pPlayer.nPlayer].ang = pSprite.ang;
			}

		}
	}


	public static void useSpriteDamager(XSPRITE pXSource, SPRITE pSprite){

		int dmg = (pXSource.data4 == 0 || pXSource.data4 > 65534) ? 65535 : pXSource.data4;
		int dmgType = (pXSource.data3 >= kDamageMax) ? Random(6) : ((pXSource.data3 < 0) ? 0 : pXSource.data3);

		// just damage / heal TX ID sprite
		if (pSprite != null) {
			actDamageSprite(pSprite.xvel,pSprite,dmgType,dmg);
			return;

		// or damage / heal player# specified in data2 (or all players if data2 is empty)
		} else if (pXSource.data2 > 0 && pXSource.data2 <= kMaxPlayers) {

			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				if (pXSource.data1 < kMaxPlayers)  // relative to connected players
					if (pXSource.data1 != (i + 1))
						continue;
				else if (pXSource.data1 < (kDudePlayer1 + kMaxPlayers))	// absolute lotag
					if (pXSource.data1 >= kDudePlayer1 && (pXSource.data1 + (kDudePlayer1 - 1)) == gPlayer[i].pSprite.lotag)
						continue;
				actDamageSprite(sprite[pXSource.reference].xvel,gPlayer[i].pSprite,dmgType,dmg);
				return;
			}
		}
	}

	public static void useSeqSpawnerGen(XSPRITE pXSource, int objType, int index) {
		switch (objType) {
			case SS_SECTOR:
				if (pXSource.data2 <= 0) {
					if (pXSource.data3 == 3 || pXSource.data3 == 1)
						seqKill(SS_FLOOR, sector[index].extra);
					if (pXSource.data3 == 3 || pXSource.data3 == 2)
						seqKill(SS_CEILING, sector[index].extra);
				} else {
					if (pXSource.data3 == 3 || pXSource.data3 == 1)
						seqSpawn(pXSource.data2, SS_FLOOR,sector[index].extra, null);
					if (pXSource.data3 == 3 || pXSource.data3 == 2)
						seqSpawn(pXSource.data2, SS_CEILING,sector[index].extra, null);
				}
				return;

			case SS_WALL:
				if (pXSource.data2 <= 0) {
					if (pXSource.data3 == 3 || pXSource.data3 == 1)
						seqKill(SS_WALL, wall[index].extra);
					if ((pXSource.data3 == 3 || pXSource.data3 == 2) && (wall[index].cstat & kWallMasked) != 0)
						seqKill(SS_MASKED, wall[index].extra);
				} else {

					if (pXSource.data3 == 3 || pXSource.data3 == 1)
						seqSpawn(pXSource.data2, SS_WALL,wall[index].extra, null);
					if (pXSource.data3 == 3 || pXSource.data3 == 2) {

						if (wall[index].nextwall < 0) {
							if (pXSource.data3 == 3)
								seqSpawn(pXSource.data2, SS_WALL,wall[index].extra, null);

						} else {
							if ((wall[index].cstat & kWallMasked) == 0) wall[index].cstat |= kWallMasked;
							seqSpawn(pXSource.data2, SS_MASKED,wall[index].extra, null);
						}
					}

					if (pXSource.data4 > 0) {

						int cx = (wall[wall[index].point2].x + wall[index].x) / 2;
						int cy = (wall[wall[index].point2].y + wall[index].y) / 2;
						int nSector = engine.sectorofwall((short)index);
						engine.getzsofslope((short)nSector, cx, cy,zofslope);
						int floorz = zofslope[FLOOR];
						int ceilz = zofslope[CEIL];
						if(wall[index].nextsector != -1)
							engine.getzsofslope(wall[index].nextsector, cx, cy,zofslope);
						int nextfloorz = zofslope[FLOOR];
						int nextceilz = zofslope[CEIL];

						int cz = ceilz; int fz = floorz;
						if ( ceilz < nextceilz ) cz = nextceilz;
						if ( floorz >= nextfloorz )fz = nextfloorz;


						sfxCreate3DSound(cx, cy, (fz + cz) >> 1, pXSource.data4, nSector);
					}

				}
				return;

			case SS_SPRITE:
				if (pXSource.data2 <= 0) seqKill(SS_SPRITE,sprite[index].extra);
				else {
					seqSpawn(pXSource.data2, SS_SPRITE, sprite[index].extra, (pXSource.data3 > 0) ? callbacks[pXSource.data3] : null);
					if (pXSource.data4 > 0)
						sfxStart3DSound(sprite[index], pXSource.data4, -1, 0);
				}
				return;
		}
	}

	public static void useSectorWindGen(XSPRITE pXSource, SECTOR pSector) {
		SPRITE pSource = sprite[pXSource.reference];
		XSECTOR pXSector = null; boolean forceWind = false;

		if (pSector.extra >= 0) pXSector = xsector[pSector.extra];
		else {

			int nXSector = dbInsertXSector(pSource.sectnum);
			if (nXSector < 0) return;

			pXSector = xsector[nXSector];
			forceWind = true;

		}

		if ((pSource.hitag & 0x0001) != 0) {
			pXSector.panAlways = true;
			pXSector.windAlways = true;
		} else if (forceWind)
			pXSector.windAlways = true;

		if (pXSource.data2 > 32766) pXSource.data2 = 32767;

		if (pXSource.data1 == 1 || pXSource.data1 == 3) pXSector.windVel = Random(pXSource.data2);
		else pXSector.windVel = pXSource.data2;

		if (pXSource.data1 == 2 || pXSource.data1 == 3) {
			short ang = pSource.ang;
			while(pSource.ang == ang)
				pSource.ang = (short) BiRandom(kAngle360);
		}

		pXSector.windAng = pSource.ang;

		if (pXSource.data3 > 0 && pXSource.data3 < 4) {
			switch (pXSource.data3) {
				case 1:
					pXSector.panFloor = true;
					pXSector.panCeiling = false;
					break;
				case 2:
					pXSector.panFloor = false;
					pXSector.panCeiling = true;
					break;
				case 3:
					pXSector.panFloor = true;
					pXSector.panCeiling = true;
					break;
			}

			short oldPan = pXSector.panVel;
			pXSector.panAngle = (short) pXSector.windAng;
			pXSector.panVel = (short) pXSector.windVel;

			// add to panList if panVel was set to 0 previously
			if (oldPan == 0 && pXSector.panVel != 0) {
				for (int i = panCount; i >= 0; i--) {
					if (panList[i] == pSector.extra) break;
					else if (i == 0) panList[panCount++] = pSector.extra;
				}
			}
		}


	}

	public static void useEffectGen(XSPRITE pXSource, SPRITE pSprite) {
		if (pSprite.extra < 0) return;

		GetSpriteExtents(pSprite); SPRITE pEffect = null;
		int dx = 0, dy = 0;  int cnt = Math.min(pXSource.data4, 32);
		while (cnt-- >= 0){

			if (cnt > 0) {
				dx = BiRandom(250);
				dy = BiRandom(150);
			}

			pEffect = actSpawnEffect(pXSource.data2, pSprite.sectnum, pSprite.x + dx, pSprite.y + dy, extents_zTop, 0);
			if (pEffect != null) {
				if (pEffect.pal <= 0) pEffect.pal = pSprite.pal;
				if (pEffect.xrepeat <= 0) pEffect.xrepeat = pSprite.xrepeat;
				if (pEffect.yrepeat <= 0) pEffect.yrepeat = pSprite.yrepeat;
				if (pEffect.shade == 0) pEffect.shade = pSprite.shade;
			}
		}

		if (pXSource.data3 > 0)
			sfxStart3DSound(pSprite, pXSource.data3, -1, 0);

	}


	public static void stopWindOnSectors(XSPRITE pXSource) {
		SPRITE pSource = sprite[pXSource.reference];

		if (pXSource.txID <= 0) {

			if (sector[pSource.sectnum].extra >= 0)
				xsector[sector[pSource.sectnum].extra].windVel = 0;

			return;
		}

		for (int i = bucketHead[pXSource.txID]; i < bucketHead[pXSource.txID + 1]; i++) {
			if (rxBucket[i].type != SS_SECTOR) continue;
			XSECTOR pXSector = xsector[sector[rxBucket[i].index].extra];
			if ((pXSector.state == 1 && !pXSector.windAlways) || pSource.hitag == 1)
				pXSector.windVel = 0;
		}
	}

	public static boolean targetMustBeForced(SPRITE pDude){
		switch (pDude.lotag){
			case kDudeEarthZombie:
			case kDudeSleepZombie:
				return true;
			default:
				return false;
		}
	}

	public static boolean dudeCanSeeTarget(XSPRITE pXDude, DudeInfo pDudeInfo, SPRITE pTarget){
		SPRITE pDude = sprite[pXDude.reference];
		int dx = pTarget.x - pDude.x;
		int dy = pTarget.y - pDude.y;

		int dist = (int) engine.qdist(dx, dy);

		// check target
		if (dist < pDudeInfo.seeDist) {
			int eyeAboveZ = pDudeInfo.eyeHeight * pDude.yrepeat << 2;

			// is there a line of sight to the target
			//// 360deg periphery here ////
			//int nAngle = engine.getangle(dx, dy);
			//int losAngle = ((kAngle180 + nAngle - pDude.ang) & kAngleMask) - kAngle180;
			// is the target visible?
			//if (ru.m210projects.Build.Pragmas.klabs(losAngle) < Globals.kAngle360 )
			return engine.cansee(pDude.x, pDude.y, pDude.z, pDude.sectnum,
					pTarget.x, pTarget.y, pTarget.z - eyeAboveZ, pTarget.sectnum);
		}

		return false;

	}

	public static void disturbDudesInSight(SPRITE pSprite,int max){
		SPRITE pDude = null; XSPRITE pXDude = null;
		XSPRITE pXSprite = xsprite[pSprite.extra];
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		for (int nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			pDude = sprite[nSprite];
			if (pDude.xvel == pSprite.xvel || !IsDudeSprite(pDude) || pDude.extra < 0)
					continue;
			pXDude = xsprite[pDude.extra];
			if (dudeCanSeeTarget(pXSprite,pDudeInfo,pDude)){
				if (pXDude.target != -1 || pXDude.rxID > 0)
					continue;

				aiSetTarget(pXDude, pDude.x,pDude.y,pDude.z);
				aiActivateDude(pDude, pXDude);
				//System.out.println("//////////// => DUDE DISTURBED "+pDude.lotag);
				if (max-- < 1)
					break;
			}
		}
	}

	public static int getTargetDist(SPRITE pSprite, DudeInfo pDudeInfo, SPRITE pTarget){
		int x = pTarget.x; int y = pTarget.y; //int z = pTarget.z;
		int dx = x - pSprite.x; int dy = y - pSprite.y;

		int dist = (int) engine.qdist(dx, dy);
		if (dist <= pDudeInfo.meleeDist) return 0;
		if (dist >= pDudeInfo.seeDist) return 13;
		if (dist <= pDudeInfo.seeDist / 12) return 1;
		if (dist <= pDudeInfo.seeDist / 11) return 2;
		if (dist <= pDudeInfo.seeDist / 10) return 3;
		if (dist <= pDudeInfo.seeDist / 9) return 4;
		if (dist <= pDudeInfo.seeDist / 8) return 5;
		if (dist <= pDudeInfo.seeDist / 7) return 6;
		if (dist <= pDudeInfo.seeDist / 6) return 7;
		if (dist <= pDudeInfo.seeDist / 5) return 8;
		if (dist <= pDudeInfo.seeDist / 4) return 9;
		if (dist <= pDudeInfo.seeDist / 3) return 10;
		if (dist <= pDudeInfo.seeDist / 2) return 11;
		return 12;
	}

	public static int getFineTargetDist(SPRITE pSprite, DudeInfo pDudeInfo, SPRITE pTarget){
		int x = pTarget.x; int y = pTarget.y;
		int dx = x - pSprite.x; int dy = y - pSprite.y;

		int dist = (int) engine.qdist(dx, dy);
		return dist;
	}

	public static SPRITE getTargetInRange(SPRITE pSprite, int minDist, int maxDist, short data, short teamMode){
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		XSPRITE pXSprite = xsprite[pSprite.extra];
		SPRITE pTarget = null; XSPRITE pXTarget = null; SPRITE cTarget = null;
		for (int nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			pTarget = sprite[nSprite];  pXTarget = xsprite[pTarget.extra];
			if (!dudeCanSeeTarget(pXSprite,pDudeInfo,pTarget)) continue;

			int dist = getTargetDist(pSprite,pDudeInfo,pTarget);
			if (dist < minDist || dist > maxDist) continue;
			else if (pXSprite.target == pTarget.xvel) return pTarget;
			else if (!IsDudeSprite(pTarget) || pTarget.xvel == pSprite.xvel || IsPlayerSprite(pTarget)) continue;
			else if (IsBurningDude(pTarget) || !IsKillableDude(pTarget) || pTarget.owner == pSprite.xvel) continue;
			else if ((teamMode == 1 && isMateOf(pXSprite, pXTarget)) || isMatesHaveSameTarget(pXSprite,pTarget,1)) continue;
			else if (data == 666 || pXTarget.data1 == data) {

				if (pXSprite.target > 0){
					cTarget = sprite[pXSprite.target];
					int fineDist1 = getFineTargetDist(pSprite,pDudeInfo,cTarget);
					int fineDist2 = getFineTargetDist(pSprite,pDudeInfo,pTarget);
					if (fineDist1 < fineDist2)
						continue;
				}
				//System.out.println(" => FOUND BETTER TARGET FOR: "+pSprite.lotag+",TARGET LOTAG: "+pTarget.lotag+", TARGET RX ID:"+pXTarget.rxID+", TARGET DATA1: "+pXTarget.data1);
				return pTarget;
			}
		}

	//System.out.println("!!!!!!! => NO TARGETS IN SIGHT");
	return null;
	}

	public static boolean isActive(int nSprite) {
		Type type = xsprite[sprite[nSprite].extra].aiState.getType();
		if(type == Type.idle) return false;

        if ((gFrameClock & 48) != 0) {
        	if(type == Type.tgoto || type == Type.search)
        		return false;
			//System.err.println("NOT ACTIVE"+sprite[nSprite].lotag+" / "+aiActive[nSprite]);
			return aiActive[nSprite] > 0;
        }
        return true;
	}

	public static boolean isAnnoyingUnit(SPRITE pDude){
		switch(pDude.lotag){
			case kDudeHand:
			case kDudeBrownSpider:
			case kDudeRedSpider:
			case kDudeBlackSpider:
			case kDudeMotherSpider:
			case kDudeEel:
			case kDudeBat:
			case kDudeRat:
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
			case kDudeMotherTentacle:
			case kDudeGreenPod:
			case kDudeFirePod:
				return true;
			default:
				return false;
		}
	}

	public static boolean unitCanFly(SPRITE pDude){
		switch(pDude.lotag){
			case kDudeBat:
			case kDudeFleshGargoyle:
			case kDudeStoneGargoyle:
			case kDudePhantasm:
				return true;
			default:
				return false;
		}
	}

	public static boolean isMeleeUnit(SPRITE pDude){
		switch (pDude.lotag){
			case kDudeAxeZombie:
			case kDudeEarthZombie:
			case kDudeFleshGargoyle:
			case kDudeFleshStatue:
			case kDudeHand:
			case kDudeBrownSpider:
			case kDudeRedSpider:
			case kDudeBlackSpider:
			case kDudeMotherSpider:
			case kDudeGillBeast:
			case kDudeEel:
			case kDudeBat:
			case kDudeRat:
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
			case kDudeMotherTentacle:
			case kDudeSleepZombie:
			case kDudeInnocent:
			case kDudeTinyCaleb:
			case kDudeTheBeast:
				return true;
			case kGDXDudeUniversalCultist:
				if (dudeIsMelee(xsprite[pDude.extra]))
					return true;
			default:
				return false;
		}
	}

	public static SPRITE targetIsPlayer(XSPRITE pXSprite){
		if (pXSprite.target >= 0 && IsPlayerSprite(sprite[pXSprite.target])) return sprite[pXSprite.target];
		return null;
	}

	public static boolean isTargetAimsDude(XSPRITE pXTarget, SPRITE pDude){
		return (pXTarget.target == pDude.xvel);
	}

	public static boolean isMateOf(XSPRITE pXDude, XSPRITE pXSprite){
		return (pXDude.rxID == pXSprite.rxID);
	}


	public static boolean getDudesForTargetChg(XSPRITE pXSprite) {
		for (int i = bucketHead[pXSprite.txID]; i < bucketHead[pXSprite.txID + 1]; i++) {
			if (rxBucket[i].type != SS_SPRITE) continue;
			else if (IsDudeSprite(sprite[rxBucket[i].index]) &&
					xsprite[sprite[rxBucket[i].index].extra].health > 0) return true;
		}

		return false;
	}

	public static XSPRITE affectedByTargetChg(XSPRITE pXDude){
		if (pXDude.rxID <= 0 || pXDude.Locked == 1) return null;
		for (int nSprite = headspritestat[kStatGDXDudeTargetChanger]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			XSPRITE pXSprite = (sprite[nSprite].extra >= 0) ? xsprite[sprite[nSprite].extra] : null;
			if (pXSprite == null || pXSprite.txID <= 0 || pXSprite.state != 1) continue;
			for (int i = bucketHead[pXSprite.txID]; i < bucketHead[pXSprite.txID + 1]; i++){
				if (rxBucket[i].type != SS_SPRITE) continue;

				SPRITE pSprite = sprite[rxBucket[i].index];
				if (pSprite.extra < 0 || !IsDudeSprite(pSprite)) continue;
				else if (pSprite.xvel == sprite[pXDude.reference].xvel) return pXSprite;
			}
		}
		return null;
	}

	public static int getDataFieldOfObject(int objType, int objIndex, int dataIndex) {
		int data = -65535;
		switch (objType) {
			case SS_SPRITE:
				switch (dataIndex) {
					case 1:
						return xsprite[sprite[objIndex].extra].data1;
					case 2:
						return xsprite[sprite[objIndex].extra].data2;
					case 3:
						return xsprite[sprite[objIndex].extra].data3;
					case 4:
						return xsprite[sprite[objIndex].extra].data4;
					default:
						return data;
				}
			case SS_SECTOR:
				return xsector[sector[objIndex].extra].data;
			case SS_WALL:
				return xwall[wall[objIndex].extra].data;
			default:
				return data;
		}
	}

	public static boolean setDataValueOfObject(int objType, int objIndex, int dataIndex, int value) {
		switch (objType) {
			case SS_SPRITE:
				switch (dataIndex) {
					case 1:
						xsprite[sprite[objIndex].extra].data1 = (short) value;
						return true;
					case 2:
						xsprite[sprite[objIndex].extra].data2 = (short) value;
						return true;
					case 3:
						xsprite[sprite[objIndex].extra].data3 = (short) value;
						return true;
					case 4:
						xsprite[sprite[objIndex].extra].data4 = (short) value;
						return true;
					default:
						return false;
				}
			case SS_SECTOR:
				xsector[sector[objIndex].extra].data = (short) value;
				return true;
			case SS_WALL:
				xwall[wall[objIndex].extra].data = (short) value;
				return true;
			default:
				return false;
		}
	}

	public static boolean goalValueIsReached(XSPRITE pXSprite) {
		for (int i = bucketHead[pXSprite.txID]; i < bucketHead[pXSprite.txID + 1]; i++){
			if (getDataFieldOfObject(rxBucket[i].type,rxBucket[i].index,pXSprite.data1) != pXSprite.data3)
				return false;
		}
		return true;
	}

	public static void activateDudes(int rx){
		for ( int i = bucketHead[rx]; i < bucketHead[rx + 1]; i++ ){
			if (rxBucket[i].type != SS_SPRITE || !IsDudeSprite(sprite[rxBucket[i].index])) continue;
			aiInit(sprite[rxBucket[i].index],false);
		}
	}

	public static SPRITE getMateTargets(XSPRITE pXSprite){
		int rx = pXSprite.rxID; SPRITE pMate = null; XSPRITE pXMate = null;
		for ( int i = bucketHead[rx]; i < bucketHead[rx + 1]; i++ ){

			if (rxBucket[i].type == SS_SPRITE) {
				pMate = sprite[rxBucket[i].index];
				if (pMate.extra < 0 || pMate.xvel == sprite[pXSprite.reference].xvel || !Actor.IsDudeSprite(pMate))
					continue;

				pXMate = xsprite[pMate.extra];
				if (pXMate.target > -1){
					if (!IsPlayerSprite(sprite[pXMate.target]))
						return sprite[pXMate.target];
				}

			}
		}

		return null;
	}

	public static boolean isMatesHaveSameTarget(XSPRITE pXLeader, SPRITE pTarget, int allow){
		int rx = pXLeader.rxID; SPRITE pMate = null; XSPRITE pXMate = null;
		//if (rx <= 0) return false;
		for ( int i = bucketHead[rx]; i < bucketHead[rx + 1]; i++ ){

			if (rxBucket[i].type != SS_SPRITE)
				continue;

			pMate = sprite[rxBucket[i].index];
			if (pMate.extra < 0 || pMate.xvel == sprite[pXLeader.reference].xvel || !IsDudeSprite(pMate))
				continue;

			pXMate = xsprite[pMate.extra];

			if (pXMate.target == pTarget.xvel && allow-- <= 0)
				return true;
		}

		return false;

	}


	public static void trTextOver( int nMessage )
	{
		if(nMessage >= kMaxMessages)
			game.dassert("nMessage < kMaxMessages");
		if(currentEpisode != null) {
			MapInfo pMap = currentEpisode.gMapInfo[pGameInfo.nLevel];
			if ( pMap != null && pMap.gMessage[nMessage] != null )
				viewSetMessage(pMap.gMessage[nMessage], gPlayer[gViewIndex].nPlayer, 2);
		}
	}

	public static void FireballTrapCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		int dx, dy, dz;

		if ( (pSprite.cstat & kSpriteFloor) != 0)		// floor sprite
		{
			dx = dy = 0;
			if ( (pSprite.cstat & kSpriteFlipY) != 0 )	// face down floor sprite
				dz = 1 << 14;
			else									// face up floor sprite
				dz = -1 << 14;
		}
		else										// wall sprite or face sprite
		{
			dx = Cos(pSprite.ang) >> 16;
			dy = Sin(pSprite.ang) >> 16;
			dz = 0;
		}
		actFireMissile( pSprite, 0, 0, dx, dy, dz, kMissileFireball);
	}

	public static void UniMissileTrapCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex]; int dx = 0, dy = 0, dz = 0;
		SPRITE pSprite = sprite[pXSprite.reference];

		if (pXSprite.data1 < kMissileBase || pXSprite.data1 >= kMissileMax)
			return;

		if ((pSprite.cstat & kSpriteFloor) != 0) {
			if ((pSprite.cstat & kSpriteFlipY) != 0) dz = 1 << 14;
			else dz = -1 << 14;
		} else {
			dx = Cos(pSprite.ang) >> 16;
			dy = Sin(pSprite.ang) >> 16;
			dz = pXSprite.data3 << 6; // add slope controlling
		    if (dz > 0x10000) dz = 0x10000;
		    else if (dz < -0x10000) dz = -0x10000;
		}


		SPRITE pMissile = null;
		pMissile = actFireMissile(pSprite, 0, 0, dx, dy, dz, pXSprite.data1);
		if (pMissile != null) {

	        // inherit some properties of the generator
	        if ((pSprite.hitag & 0x0001) != 0) {

	        	pMissile.xrepeat = pSprite.xrepeat;
            	pMissile.yrepeat = pSprite.yrepeat;

	            pMissile.pal = pSprite.pal;
	            pMissile.shade = pSprite.shade;

	        }

	        // add velocity controlling
			if (pXSprite.data2 > 0) {

				long velocity = pXSprite.data2 << 12;
				sprXVel[pMissile.xvel] = mulscale(dx, velocity, 14);
				sprYVel[pMissile.xvel] = mulscale(dy, velocity, 14);
				sprZVel[pMissile.xvel] = mulscale(dz, velocity, 14);

			}

			// add bursting for missiles
			if (pMissile.lotag != kMissileStarburstFlare && pXSprite.data4 > 0)
				evPostCallback(pMissile.xvel, SS_SPRITE, (pXSprite.data4 > 500) ? 500 : pXSprite.data4 - 1, 23);

		}
	}

	public static void MGunOpenCallback( int nXIndex )
	{
		seqSpawn(getSeq(kMGunFire), SS_SPRITE, nXIndex, callbacks[MGunFireCallback]);
	}

	public static void MGunFireCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		// if dynamic ammo left or infinite ammo
		if ( pXSprite.data2 > 0 || pXSprite.data1 == 0)
		{
		    if ( pXSprite.data2 > 0 )
		    {
		    	pXSprite.data2--;	// subtract ammo
				if (pXSprite.data2 == 0)
					evPost(nSprite, SS_SPRITE, 1, kCommandOff);	// empty! turn it off
		    }
		    int dx = (Cos(pSprite.ang) >> 16) + BiRandom(1000);
			int dy = (Sin(pSprite.ang) >> 16) + BiRandom(1000);
			int dz = BiRandom(1000);

		    actFireVector(pSprite, 0, 0, dx, dy, dz, 2); //kVectorBullet
		    sfxStart3DSound(pSprite, 359, -1, 0); //kSfxTomFire
		}
	}

	public static int getWave(XSECTOR pXSector, int nBusy) {
		int wave;

		if(pXSector.busy >= nBusy)
			wave = pXSector.waveTime[0];
		else wave = pXSector.waveTime[1];

		return wave;
	}

	public static void InitGenerator( int nSprite )
	{
		if(nSprite >= kMaxSprites)
			game.dassert("nSprite < kMaxSprites");
		SPRITE pSprite = sprite[nSprite];
		if(pSprite.statnum == kMaxStatus)
			game.dassert("pSprite.statnum != kMaxStatus");
		int nXSprite = pSprite.extra;
		if(nXSprite <= 0)
			game.dassert("nXSprite > 0");
		XSPRITE pXSprite = xsprite[nXSprite];

		switch (pSprite.lotag)
		{
			case kGDXDudeTargetChanger:
				pSprite.cstat &= ~kSpriteBlocking;
				pSprite.cstat |= kSpriteInvisible;
				if (pXSprite.busyTime <= 0) pXSprite.busyTime = 5;
				if (pXSprite.state != pXSprite.restState)
					evPost(nSprite, SS_SPRITE, 0, kCommandRespawn);
				return;
			case kGDXObjDataAccumulator:
			case kGDXSeqSpawner:
			case kGDXEffectSpawner:
				pSprite.cstat &= ~kSpriteBlocking;
				pSprite.cstat |= kSpriteInvisible;
				if (pXSprite.state != pXSprite.restState)
					evPost(nSprite, SS_SPRITE, 0, kCommandRespawn);
				return;
			case kGDXWindGenerator:
				pSprite.cstat &= ~kSpriteBlocking;
				if (pXSprite.state != pXSprite.restState)
					evPost(nSprite, SS_SPRITE, 0, kCommandRespawn);
				return;
			case kWeaponItemBase: // Random weapon
			case kAmmoItemRandom: // Random ammo
				pSprite.cstat &= ~kSpriteBlocking;
				pSprite.cstat |= kSpriteInvisible;
				if (pXSprite.state != pXSprite.restState)
					evPost(nSprite, SS_SPRITE, 0, kCommandRespawn);
				return;
			case kGenTrigger:
				pSprite.cstat &= ~kSpriteBlocking;
				pSprite.cstat |= kSpriteInvisible;
				break;
			default:
				break;
		}

		if ( pXSprite.state != pXSprite.restState && pXSprite.busyTime > 0)
			evPost(nSprite, SS_SPRITE, (pXSprite.busyTime + BiRandom(pXSprite.data1)) * kTimerRate / 10, kCommandRespawn);
	}

	public static void ActivateGenerator( int nSprite) {
		if(nSprite >= kMaxSprites)
			game.dassert("nSprite < kMaxSprites");
		SPRITE pSprite = sprite[nSprite];
		if(pSprite.statnum == kMaxStatus)
			game.dassert("pSprite.statnum != kMaxStatus");

		int nXSprite = pSprite.extra;
		if(nXSprite <= 0)
			game.dassert("nXSprite > 0");
		XSPRITE pXSprite = xsprite[nXSprite];

		switch ( pSprite.lotag ) {
		case kWeaponItemBase:
		case kAmmoItemRandom:
			// let's first search for previously dropped items and remove it
			if (pXSprite.dropMsg > 0) {
				for (short nItem = headspritestat[kStatItem]; nItem >= 0; nItem = nextspritestat[nItem]) {
					SPRITE pItem = sprite[nItem];
					if (pItem.lotag == pXSprite.dropMsg && pItem.x == pSprite.x && pItem.y == pSprite.y && pItem.z == pSprite.z) {
						actSpawnEffect(29, pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 0);
						deletesprite(nItem);
						break;
					}
				}
			}
			// then drop item
			DropRandomPickupObject(pSprite,pXSprite.dropMsg);
			break;
		case kGenWaterDrip:
			GetSpriteExtents(pSprite);
			actSpawnThing( pSprite.sectnum, pSprite.x, pSprite.y, extents_zBot, kThingWaterDrip);
			break;
		case kGenBloodDrip:
			GetSpriteExtents(pSprite);
			actSpawnThing( pSprite.sectnum, pSprite.x, pSprite.y, extents_zBot, kThingBloodDrip);
			break;
		case kGenSFX:
			if (IsOriginalDemo()) sfxStart3DSound(pSprite, pXSprite.data2, -1, 0);
			else {
				long pitch = ((long) pXSprite.data4 << 1); int volume = pXSprite.data3;
				sfxStart3DSoundCP(pSprite, pXSprite.data2, -1, 0, (pitch < 2000) ? 0 : pitch, volume);
			}
			break;
		case kGenEctoSkull: // can shot any projectile, default 307 (ectoskull)
			if (!IsOriginalDemo()) UniMissileTrapCallback(nXSprite);
			break;
		case kGenDart:
			break;
		case kGenFireball:
			switch(pXSprite.data2)
			{
				case 0:
					FireballTrapCallback( nXSprite );
					break;
				case 1:
					seqSpawn(getSeq(kFireTrap1), SS_SPRITE, nXSprite, callbacks[FireballTrapCallback]);
					break;
				case 2:
					seqSpawn(getSeq(kFireTrap2), SS_SPRITE, nXSprite, callbacks[FireballTrapCallback]);
					break;
			}
			break;
		case kGenBubble:
			GetSpriteExtents(pSprite);
			actSpawnEffect(23, pSprite.sectnum, pSprite.x, pSprite.y, extents_zTop, 0);
			break;
		case kGenMultiBubble:
			GetSpriteExtents(pSprite);
			actSpawnEffect(26, pSprite.sectnum, pSprite.x, pSprite.y, extents_zTop, 0);
			break;
		}
	}

}
