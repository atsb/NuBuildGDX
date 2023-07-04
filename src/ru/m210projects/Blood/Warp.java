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

import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.ZONE;
import ru.m210projects.Build.Types.SPRITE;

public class Warp {
	public static ZONE[] gStartZone = new ZONE[kMaxPlayers];
	public static ZONE[] gStartZoneTeam1 = new ZONE[kMaxPlayers];
	public static ZONE[] gStartZoneTeam2 = new ZONE[kMaxPlayers];
	public static int[] gUpperLink = new int[kMaxSectors], gLowerLink = new int[kMaxSectors];
	
	public static boolean gTeamsSpawnUsed = false;
	
	public static void InitPlayerStartZones()
	{
		int nSprite, nXSprite;

		// clear link values
		for ( int nSector = 0; nSector < kMaxSectors; nSector++ )
		{
			gUpperLink[nSector] = -1;
			gLowerLink[nSector] = -1;
		}
		
		int team1 = 0; int team2 = 0; // increment if team start positions specified.
		for ( nSprite = 0; nSprite < kMaxSprites; nSprite++ ){
			if (sprite[nSprite].statnum < kMaxStatus)
			{
				SPRITE pSprite = sprite[nSprite];
				nXSprite = pSprite.extra;
				if ( nXSprite > 0 )
				{
					XSPRITE pXSprite = xsprite[nXSprite];
					
					switch( pSprite.lotag )
					{
						case kMarkerPlayerStart:
						{
							if ( (pGameInfo.nGameType < 2)
							&& (pXSprite.data1 >= 0 && pXSprite.data1 < kMaxPlayers) )
							{
								ZONE pZone = gStartZone[pXSprite.data1];
								pZone.x = pSprite.x;
								pZone.y = pSprite.y;
								pZone.z = pSprite.z;
								pZone.sector = pSprite.sectnum;
								pZone.angle = pSprite.ang;
							}
							deletesprite( (short)nSprite );
							break;
						}

						case kMarkerDeathStart:
							if (pXSprite.data1 >= 0 && pXSprite.data1 < kMaxPlayers) {
								if (pGameInfo.nGameType >= 2) {
									// default if BB or teams without data2 specified
									ZONE pZone = gStartZone[pXSprite.data1];
									pZone.x = pSprite.x;
									pZone.y = pSprite.y;
									pZone.z = pSprite.z;
									pZone.sector = pSprite.sectnum;
									pZone.angle = pSprite.ang;
									
									if (pGameInfo.nGameType == 3){
										// team 1
										if (pXSprite.data2 == 1) {
											pZone = gStartZoneTeam1[team1];									
											pZone.x = pSprite.x;
											pZone.y = pSprite.y;
											pZone.z = pSprite.z;
											pZone.sector = pSprite.sectnum;
											pZone.angle = pSprite.ang;
											team1++;
											
										// team 2	
										} else if (pXSprite.data2 == 2) {
											pZone = gStartZoneTeam2[team2];
											pZone.x = pSprite.x;
											pZone.y = pSprite.y;
											pZone.z = pSprite.z;
											pZone.sector = pSprite.sectnum;
											pZone.angle = pSprite.ang;
											team2++;
										}
									}
								}
							}
							deletesprite( (short)nSprite );
							break;

						case kMarkerUpperLink:
							gUpperLink[pSprite.sectnum] = (short)nSprite;
							pSprite.cstat |= kSpriteInvisible | kSpriteMapNever;
							pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;
							break;

						case kMarkerLowerLink:
							gLowerLink[pSprite.sectnum] = (short)nSprite;
							pSprite.cstat |= kSpriteInvisible | kSpriteMapNever;
							pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;
							break;
						case kMarkerUpperWater:
				        case kMarkerUpperStack:
				        case kMarkerUpperGoo:
				            gUpperLink[pSprite.sectnum] = (short)nSprite;
				            pSprite.cstat |= kSpriteInvisible | kSpriteMapNever;
							pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;
							pSprite.z = engine.getflorzofslope(pSprite.sectnum, pSprite.x, pSprite.y);
				            break;
				        case kMarkerLowerWater:
				        case kMarkerLowerStack:
				        case kMarkerLowerGoo:
				        	gLowerLink[pSprite.sectnum] = (short)nSprite;
				        	pSprite.cstat |= kSpriteInvisible | kSpriteMapNever;
							pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;
							pSprite.z = engine.getceilzofslope(pSprite.sectnum, pSprite.x, pSprite.y);
				            break;
					}
				}
			}
		}
		
		// check if there is enough start positions for teams, if any used
		if (team1 > 0 || team2 > 0) {
			gTeamsSpawnUsed = true;
			if (team1 < kMaxPlayers / 2 || team2 < kMaxPlayers / 2)
				game.GameMessage("At least "+(kMaxPlayers / 2)+" spawn positions for each team is recommended.");
		}
		
		// verify links have mates and connect them
		for (int nFrom = 0; nFrom < kMaxSectors; nFrom++)
		{
			if ( gUpperLink[nFrom] >= 0 )
			{
				SPRITE pFromSprite = sprite[gUpperLink[nFrom]];
				nXSprite = pFromSprite.extra;
				if(!(nXSprite > 0 && nXSprite < kMaxXSprites)) game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
				XSPRITE pXSprite = xsprite[nXSprite];
				int nID = pXSprite.data1;

				for (int nTo = 0; nTo < kMaxSectors; nTo++)
				{
					if ( gLowerLink[nTo] >= 0 )
					{
						SPRITE pToSprite = sprite[gLowerLink[nTo]];
						nXSprite = pToSprite.extra;
						if(!(nXSprite > 0 && nXSprite < kMaxXSprites)) game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
	
						if ( xsprite[nXSprite].data1 == nID )
						{
							pFromSprite.owner = (short)gLowerLink[nTo];
							pToSprite.owner = (short)gUpperLink[nFrom];
						}
					}
				}
			}
		}
	}
	
	public static int checkWarping(SPRITE pSprite) {
		int nUpper = gUpperLink[pSprite.sectnum]; int nLower = gLowerLink[pSprite.sectnum];	
		
		if ( nUpper >= 0 )
		{
			SPRITE pUpper = sprite[nUpper];
			int uz;
			if(pUpper.lotag != kMarkerUpperLink)
				uz = engine.getflorzofslope(pSprite.sectnum,pSprite.x,pSprite.y);
			else 
				uz =  pUpper.z;

			if(uz <= pSprite.z) {
				nLower = sprite[nUpper].owner;
				if(!(nLower >= 0 && nLower < kMaxSprites)) return 0;
				if(!(sprite[nLower].sectnum >= 0 && sprite[nLower].sectnum < kMaxSectors))
					game.dassert("sprite[nLower].sectnum >= 0 && sprite[nLower].sectnum < kMaxSectors");
				changespritesect(pSprite.xvel, sprite[nLower].sectnum);
				pSprite.x += (sprite[nLower].x - sprite[nUpper].x);
				pSprite.y += (sprite[nLower].y - sprite[nUpper].y);
				int lz;
				if ( sprite[nLower].lotag == kMarkerLowerLink )
					lz = sprite[nLower].z;
			    else lz = engine.getceilzofslope(pSprite.sectnum, pSprite.x, pSprite.y);

			    pSprite.z += (lz - uz);
			    game.pInt.clearspriteinterpolate(pSprite.xvel);

				return sprite[nUpper].lotag;
			}
		} 
		
		if ( nLower >= 0 )
		{
			SPRITE pLower = sprite[nLower];
			int lz;
			if(pLower.lotag != kMarkerLowerLink)
				lz = engine.getceilzofslope(pSprite.sectnum,pSprite.x,pSprite.y);
			else lz =  pLower.z;

			if(lz >= pSprite.z) {
				nUpper = sprite[nLower].owner;
				if(!(nUpper >= 0 && nUpper < kMaxSprites)) return 0;
				if(!(sprite[nUpper].sectnum >= 0 && sprite[nUpper].sectnum < kMaxSectors)) game.dassert("sprite[nUpper].sectnum >= 0 && sprite[nUpper].sectnum < kMaxSectors");
				changespritesect(pSprite.xvel, sprite[nUpper].sectnum);
				pSprite.x += (sprite[nUpper].x - sprite[nLower].x);
				pSprite.y += (sprite[nUpper].y - sprite[nLower].y);

				int uz;
				if ( sprite[nUpper].lotag == kMarkerUpperLink )
					uz = sprite[nUpper].z;
			    else uz = engine.getflorzofslope(pSprite.sectnum, pSprite.x, pSprite.y);
				pSprite.z += (uz - lz);
				game.pInt.clearspriteinterpolate(pSprite.xvel);

				return sprite[nLower].lotag;
			}
		}
		return 0;   
	}
	
	public static long checkWx, checkWy, checkWz;
	public static short checkWs;
	public static int checkWarping(long x, long y, long z, int nSector) {
		if(nSector != -1) {
			int nUpper = gUpperLink[nSector]; int nLower = gLowerLink[nSector];	
			if ( nUpper >= 0 )
			{
				SPRITE pUpper = sprite[nUpper];
				int uz;
				if(pUpper.lotag != kMarkerUpperLink)
					uz = engine.getflorzofslope((short)nSector, (int)x, (int)y);
				else uz =  pUpper.z;

				if(uz <= z) {
					if(!(nLower >= 0 && nLower < kMaxSprites)) return 0;
					if(!(sprite[nLower].sectnum >= 0 && sprite[nLower].sectnum < kMaxSectors))
						game.dassert("sprite[nLower].sectnum >= 0 && sprite[nLower].sectnum < kMaxSectors");
					nSector = sprite[nUpper].sectnum;
					x += sprite[nLower].x - sprite[nUpper].x;
					y += sprite[nLower].y - sprite[nUpper].y;
					int lz;
					if ( sprite[nLower].lotag == kMarkerLowerLink )
						lz = sprite[nLower].z;
				    else lz = engine.getceilzofslope((short)nSector, (int)x, (int)y);
				    z += lz - uz;
					
				    checkWx = x;
				    checkWy = y;
				    checkWz = z;
				    checkWs = (short) nSector;
				    
					return sprite[nUpper].lotag;
				}
			} 
			
			if ( nLower >= 0 )
			{
				SPRITE pLower = sprite[nLower];
				int lz;
				if(pLower.lotag != kMarkerLowerLink)
					lz = engine.getceilzofslope((short)nSector, (int)x, (int)y);
				else lz =  pLower.z;

				if(lz >= z) {
					nUpper = sprite[nLower].owner;
					if(!(nUpper >= 0 && nUpper < kMaxSprites)) return 0;
					if(!(sprite[nUpper].sectnum >= 0 && sprite[nUpper].sectnum < kMaxSectors)) game.dassert("sprite[nUpper].sectnum >= 0 && sprite[nUpper].sectnum < kMaxSectors");
					nSector = sprite[nUpper].sectnum;
					x += sprite[nUpper].x - sprite[nLower].x;
					y += sprite[nUpper].y - sprite[nLower].y;
		
					int uz;
					if ( sprite[nUpper].lotag == kMarkerUpperLink )
						uz = sprite[nUpper].z;
				    else uz = engine.getflorzofslope((short)nSector, (int)x, (int)y);
					z += uz - lz;
		
					checkWx = x;
				    checkWy = y;
				    checkWz = z;
				    checkWs = (short) nSector;

//				    int nMirror = sector[nSector].floorpicnum;
//				    if ( (gotpic[nMirror >> 3] & pow2char[nMirror & 7]) == 0 )
//				    {
//				    	gotpic[Mirror.MIRRORLABEL >> 3] = 0;
//				    	gotpic[(Mirror.MIRRORLABEL >> 3) + 1] = 0;
//				    	engine.setgotpic(nMirror);
//				    }

					return sprite[nLower].lotag;
				}
			}
		}
		
		checkWx = x;
	    checkWy = y;
	    checkWz = z;
	    checkWs = (short) nSector;

//	    if(nSector != -1 && gLowerLink[nSector] != -1) { // 12.12.2018 I forgot why I need this tweak. Maybe just an old code?
//		    int nMirror1 = sector[nSector].ceilingpicnum;
//		    if ( (gotpic[nMirror1 >> 3] & pow2char[nMirror1 & 7]) == 0 )
//		    {
//		    	gotpic[Mirror.MIRRORLABEL >> 3] = 0;
//		    	gotpic[(Mirror.MIRRORLABEL >> 3) + 1] = 0;
//		    	engine.setgotpic(nMirror1);
//		    }
//	    }

		return 0;
	}
}
