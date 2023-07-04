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

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Actor.kAttrGravity;
import static ru.m210projects.Blood.DB.xwall;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Trig.GetOctant;
import static ru.m210projects.Blood.Trig.RotateVector;
import static ru.m210projects.Blood.Trig.rotated;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Blood.View.viewBackupSpriteLoc;
import static ru.m210projects.Blood.View.viewInsertTSprite;
import static ru.m210projects.Blood.View.viewProcessSprites;
import static ru.m210projects.Blood.Warp.gUpperLink;
import static ru.m210projects.Build.Engine.*;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import ru.m210projects.Blood.Types.BloodTile;

public class Mirror {

	public static final int MIRROR = 504;
	public static final int MIRRORLABEL = 4080;
//	public static final int MIRRORLABEL = 4095;
	public static final int MAXMIRRORS = 16;
	public static int mirrorcnt;
	public static boolean display_mirror;

	public static int[] MirrorType = new int[MAXMIRRORS];
	public static int[] MirrorX = new int[MAXMIRRORS];
	public static int[] MirrorY = new int[MAXMIRRORS];
	public static int[] MirrorZ = new int[MAXMIRRORS];
	public static int[] MirrorLower = new int[MAXMIRRORS];
	public static int[] MirrorUpper = new int[MAXMIRRORS];
	public static int MirrorSector;
	public static int[] MirrorWall = new int[4];

	public static void InitMirrors()
	{
		int i;

		//Scan wall tags
		mirrorcnt = 0;
		InitMirrorTiles();

		for( i = numwalls - 1; i >= 0 && mirrorcnt != MAXMIRRORS; i--)
		{
			if (mirrorcnt == MAXMIRRORS)
			{
				Console.Println("Maximum mirror count reached.", OSDTEXT_YELLOW);
				break;
			}

			if (wall[i].overpicnum == MIRROR )
			{
				if ( wall[i].extra > 0 && wall[i].lotag == 501 )
				{
					Console.Println("Initializing wall overpicnum mirror for", 0);

					wall[i].cstat |= kWallOneWay;
					wall[i].overpicnum = (short) (mirrorcnt + MIRRORLABEL);
					MirrorType[mirrorcnt] = 0;
					int nXWall = wall[i].extra;
					MirrorUpper[mirrorcnt] = i;
					int data = xwall[nXWall].data;

					int nWall = numwalls;
					while ( --nWall >= 0 )
					{
						if ( nWall != i )
						{
							int nXWall2 = wall[nWall].extra;
							if ( nXWall2 > 0 && wall[nWall].lotag == 501 && xwall[nXWall2].data == data )
							{
								wall[i].hitag = (short) nWall;
								wall[nWall].hitag = (short) i;
								MirrorLower[mirrorcnt] = nWall;
								break;
							}
						}
					}

					if ( nWall < 0 )
					{
						game.dassert("wall[" + i + "] has no matching wall link! (data=" + xwall[nXWall].data + ")");
					}
					++mirrorcnt;
				}
			}
			else
			if ( wall[i].picnum == MIRROR )
			{
//				Console.Println("Initializing wall picnum mirror for wal");
				wall[i].cstat |= kWallOneWay;
				wall[i].picnum = (short) (mirrorcnt + MIRRORLABEL);
			    MirrorType[mirrorcnt] = 0;
			    MirrorLower[mirrorcnt] = i;
			    MirrorUpper[mirrorcnt] = i;
			    mirrorcnt++;
			}
		}

		for( i = numsectors - 1; i >= 0 && mirrorcnt < (MAXMIRRORS - 1); i--)
		{
			if (mirrorcnt == MAXMIRRORS)
			{
				Console.Println("Maximum mirror count reached.", OSDTEXT_YELLOW);
				break;
			}

			if ( sector[i].floorpicnum == MIRROR )
		    {
				int nUpper = gUpperLink[i];
				if(nUpper >= 0) {
					int nLower = sprite[nUpper].owner;
					if(nLower == -1)
						continue;
					if ( sector[sprite[nLower].sectnum].ceilingpicnum != MIRROR )
						Console.Println("Lower link sector " + i + " doesn't have mirror picnum!", OSDTEXT_RED);
//					Console.Println("Initializing floor mirror for sector " + i);
					MirrorType[mirrorcnt] = 2;
					MirrorX[mirrorcnt] = sprite[nLower].x - sprite[nUpper].x;
					MirrorY[mirrorcnt] = sprite[nLower].y - sprite[nUpper].y;
					MirrorZ[mirrorcnt] = sprite[nLower].z - sprite[nUpper].z;
					MirrorLower[mirrorcnt] = sprite[nLower].sectnum;
					MirrorUpper[mirrorcnt] = i;
					sector[i].floorpicnum = (short) (MIRRORLABEL + mirrorcnt++);
//					Console.Println("Initializing ceiling mirror for sector " + i);
					MirrorType[mirrorcnt] = 1;
					MirrorX[mirrorcnt] = sprite[nUpper].x - sprite[nLower].x;
					MirrorY[mirrorcnt] = sprite[nUpper].y - sprite[nLower].y;
					MirrorZ[mirrorcnt] = sprite[nUpper].z - sprite[nLower].z;
					MirrorLower[mirrorcnt] = i;
					MirrorUpper[mirrorcnt] = sprite[nLower].sectnum;
					sector[sprite[nLower].sectnum].ceilingpicnum = (short) (MIRRORLABEL + mirrorcnt++);
				}
		    }
		}

		initMirrorWall();
//		Console.Println(mirrorcnt + " mirrors initialized");
	}

	public static void initMirrorWall()
	{
		if(numsectors >= kMaxSectors)
			return;

		MirrorSector = numsectors;
		for (int i = 0; i < 4; i++) {
			int nWall = numwalls + i;
			MirrorWall[i] = nWall;
			WALL pWall = new WALL();

			pWall.picnum = 504;
			pWall.overpicnum = 504;
			pWall.cstat = 0;
			pWall.nextsector = -1;
			pWall.nextwall = -1;
			pWall.point2 = (short) (nWall + 1);

		    wall[nWall] = pWall;
		}
		wall[MirrorWall[3]].point2 = (short) MirrorWall[0];

		SECTOR pSector = new SECTOR();
		pSector.ceilingpicnum = 504;
		pSector.floorpicnum = 504;
		pSector.wallnum = 4;
		pSector.wallptr = (short) MirrorWall[0];
		sector[MirrorSector] = pSector;
	}

	public static void InitMirrorTiles()
	{
		engine.getTile(MIRROR).clear();
		for( int i = 0; i < MAXMIRRORS; i++)
			engine.getTile(i + MIRRORLABEL).clear();
	}

	public static void processMirror(long nViewX, long nViewY) {
		for(int i = spritesortcnt - 1; i >= 0; --i)
			tsprite[i].xrepeat = tsprite[i].yrepeat = 0;

		for( int i = mirrorcnt - 1; i >= 0; i-- )
		{
			int nMirror = i + MIRRORLABEL;

			if ( (gotpic[nMirror >> 3] & pow2char[nMirror & 7]) != 0 )
			{
				int type = MirrorType[i];
				if(type == 1 || type == 2) {

					for ( int j = headspritesect[MirrorLower[i]]; j >= 0; j = nextspritesect[j] )
					{
						SPRITE pSprite = sprite[j];
						if ( pSprite != gPlayer[gViewIndex].pSprite )
				        {
							GetSpriteExtents(pSprite);
							engine.getzsofslope((short)MirrorLower[i], pSprite.x, pSprite.y, zofslope);
							if ( pSprite.lotag == kStatDude )
				            {
								if ( extents_zTop < zofslope[CEIL] || extents_zBot > zofslope[FLOOR] )
					            {
					            	int cnt;
					            	if ( type == 2 )
					                	cnt = i + 1;
					                else
					                    cnt = i - 1;
					            	int mirX = MirrorX[cnt];
					                int mirY = MirrorY[cnt];
					                int mirZ = MirrorZ[cnt];

					                SPRITE tSprite = viewInsertTSprite( MirrorUpper[i], 0, null );
					                tSprite.reset((byte)0);

					                tSprite.lotag = pSprite.lotag;
					                tSprite.xvel = pSprite.xvel;
					                tSprite.sectnum = (short) MirrorUpper[i];
					                tSprite.x = mirX + pSprite.x;
					                tSprite.y = mirY + pSprite.y;
					                tSprite.z = mirZ + pSprite.z;
					                tSprite.ang = pSprite.ang;
					                tSprite.picnum = pSprite.picnum;
					                tSprite.shade = pSprite.shade;
					                tSprite.pal = pSprite.pal;
					                tSprite.xrepeat = pSprite.xrepeat;
					                tSprite.yrepeat = pSprite.yrepeat;
					                tSprite.xoffset = pSprite.xoffset;
					                tSprite.yoffset = pSprite.yoffset;
					                tSprite.statnum = 0;
					                tSprite.cstat = pSprite.cstat;
					                tSprite.owner = pSprite.xvel;
					                tSprite.extra = pSprite.extra;
					                tSprite.hitag = (short) (pSprite.hitag | kAttrGravity);

					                viewBackupSpriteLoc(pSprite.xvel, pSprite);
					            }
				            }
				        }
					}
				}
			}
		}

		for(int nTSprite = spritesortcnt - 1; nTSprite >= 0; --nTSprite)
		{
			SPRITE pTSprite = tsprite[nTSprite];
			int nFrames = 0, dx, dy;

			BloodTile pic = engine.getTile(pTSprite.picnum);
			switch (  pic.getView() ) {
				case kSpriteView5Full:
					dx = (int) (nViewX - pTSprite.x);
					dy = (int) (nViewY - pTSprite.y);

					RotateVector(dx, dy, -pTSprite.ang + kAngle45 / 2);
					nFrames = GetOctant((int)rotated.x, (int)rotated.y);

					if ( nFrames > 4 )
			        {
			            nFrames = 8 - nFrames;
			            pTSprite.cstat |= kSpriteFlipX;
			        }
			        else
			        	pTSprite.cstat &= ~kSpriteFlipX;
					break;
				case kSpriteView8Full:
					// Calculate which of the 8 angles of the sprite to draw (0-7)
					dx = (int) (nViewX - pTSprite.x);
					dy = (int) (nViewY - pTSprite.y);

					RotateVector(dx, dy, -pTSprite.ang + kAngle45 / 2);
					nFrames = GetOctant((int)rotated.x, (int)rotated.y);
					break;
				default:
					break;
			}
			while ( nFrames > 0 )
		    {
		        --nFrames;
		        pTSprite.picnum += pic.getFrames() + 1;
		    }
		}

	}

	public static void setMirrorParalax(boolean mirror)
	{
		for( int i = mirrorcnt - 1; i >= 0; i-- )
		{
			int nMirror = i + MIRRORLABEL;
			if ( (gotpic[nMirror >> 3] & pow2char[nMirror & 7]) != 0 )
			{
				int type = MirrorType[i];
				int nSector = MirrorUpper[i];
				if(type == 1)
				{
					if ( mirror )
						sector[nSector].ceilingstat |= kSectorParallax;
			        else
			        	sector[nSector].ceilingstat &= ~kSectorParallax;
				}
				else if(type == 2)
				{
					if ( mirror )
						sector[nSector].floorstat |= kSectorParallax;
			        else
			        	sector[nSector].floorstat &= ~kSectorParallax;
				}
			}
		}
	}

	public static void DrawMirrors( long x, long y, long z, float ang, float horiz ) {
		for( int i = mirrorcnt - 1; i >= 0; i-- )
		{
			int nMirror = i + MIRRORLABEL;

			if ( (gotpic[nMirror >> 3] & pow2char[nMirror & 7]) != 0 )
			{
				int type = MirrorType[i];
				if ( type == 0 )  //wall mirror
				{
					int nWall = MirrorLower[i];
			        WALL pWall = wall[nWall];
			        int nSector = engine.sectorofwall((short)nWall);
			        short oldNextwall = pWall.nextwall;
			        short oldNextsector = pWall.nextsector ;
			        pWall.nextwall = (short) MirrorWall[0];
			        pWall.nextsector = (short) MirrorSector;
			        wall[MirrorWall[0]].nextwall = (short) nWall;
			        wall[MirrorWall[0]].nextsector = (short) nSector;
			        wall[MirrorWall[0]].x = wall[pWall.point2].x;
			        wall[MirrorWall[0]].y = wall[pWall.point2].y;
			        wall[MirrorWall[1]].x = pWall.x;
			        wall[MirrorWall[1]].y = pWall.y;
			        wall[MirrorWall[2]].x = 16 * (wall[MirrorWall[1]].x - wall[MirrorWall[0]].x) + wall[MirrorWall[1]].x;
			        wall[MirrorWall[2]].y = 16 * (wall[MirrorWall[1]].y - wall[MirrorWall[0]].y) + wall[MirrorWall[1]].y;
			        wall[MirrorWall[3]].x = 16 * (wall[MirrorWall[0]].x - wall[MirrorWall[1]].x) + wall[MirrorWall[0]].x;
			        wall[MirrorWall[3]].y = 16 * (wall[MirrorWall[0]].y - wall[MirrorWall[1]].y) + wall[MirrorWall[0]].y;
			        sector[MirrorSector].floorz = sector[nSector].floorz;
			        sector[MirrorSector].ceilingz = sector[nSector].ceilingz;
			        int mirX = 0, mirY = 0;
			        float mirAng = 0;
			        if ( pWall.lotag == 501 )
			        {
			        	mirX = (int) (x - (wall[pWall.hitag].x - wall[pWall.point2].x));
			        	mirY = (int) (y - (wall[pWall.hitag].y - wall[pWall.point2].y));
			            mirAng = ang;
			        }
			        else
			        {
			        	engine.preparemirror((int)x, (int)y, (int)z, ang, horiz, nWall, MirrorSector);
			            mirX = mirrorx;
			            mirY = mirrory;
			            mirAng = mirrorang;
			        }

			        engine.drawrooms(mirX, mirY, z, mirAng, horiz, (short) (MirrorSector | MAXSECTORS));
			        display_mirror = true;
			        viewProcessSprites(mirX, mirY, z);
			        display_mirror = false;
			        engine.drawmasks();
			        if ( pWall.lotag != 501 )
			        	engine.completemirror();
			        pWall.nextwall = oldNextwall;
			        pWall.nextsector = oldNextsector;
				} else
				if ( type == 1) //ceiling mirror
			    {
			    	int nSector = MirrorLower[i];
			    	engine.drawrooms(x + MirrorX[i], MirrorY[i] + y, z + MirrorZ[i], ang, horiz, (short) (nSector | MAXSECTORS));
			        viewProcessSprites(x + MirrorX[i], MirrorY[i] + y, z + MirrorZ[i]);
			        short oldstat = sector[nSector].floorstat;
			        sector[nSector].floorstat |= kSectorParallax;
			        engine.drawmasks();
			        sector[nSector].floorstat = oldstat;
			    } else
				if ( type == 2) //floor mirror
			    {
			        int nSector = MirrorLower[i];
			        engine.drawrooms(x + MirrorX[i], MirrorY[i] + y, z + MirrorZ[i], ang, horiz, (short) (nSector | MAXSECTORS));
			        viewProcessSprites(x + MirrorX[i], MirrorY[i] + y, z + MirrorZ[i]);
			        short oldstat = sector[nSector].ceilingstat;
			        sector[nSector].ceilingstat |= kSectorParallax;
			        engine.drawmasks();
			        sector[nSector].ceilingstat = oldstat;
			    }

				gotpic[(nMirror) >> 3] &= ~pow2char[nMirror & 7];
				return;
			}
		}
	}
}
