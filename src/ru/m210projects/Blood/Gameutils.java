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

import static ru.m210projects.Blood.Actor.kAttrAiming;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudePlayer8;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.DB.xwall;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Warp.gLowerLink;
import static ru.m210projects.Blood.Warp.gUpperLink;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.muldiv;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Strhandler.buildString;

import java.util.Arrays;

import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Types.Hitscan;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;

import com.badlogic.gdx.math.Vector2;

public class Gameutils {

	public static int M2Z(double n) {
		return (int)((n) * kZUnitsPerMeter);
	}

	public static int M2X(double n) {
		return (int)((n) * kXUnitsPerMeter);
	}

	public static int extents_zTop, extents_zBot;
	public static void GetSpriteExtents( SPRITE pSprite )
	{
		extents_zTop = extents_zBot = pSprite.z;
		int nTile = pSprite.picnum;

		if ( nTile == -1 || (pSprite.cstat & kSpriteRMask) == kSpriteFloor )
			return;

		Tile pic = engine.getTile(nTile);
		extents_zTop -= (pic.getOffsetY() + pic.getHeight() / 2) * (pSprite.yrepeat << 2);
		extents_zBot += (pic.getHeight() - (pic.getHeight() / 2 + pic.getOffsetY())) * (pSprite.yrepeat << 2);
	}

	public static Point RotatePoint(int x, int y, int daang, int xpivot, int ypivot)
	{
		return engine.rotatepoint(xpivot, ypivot, x, y, (short)daang);
	}

	public static boolean IsPlayerSprite(SPRITE pSprite) {
		return pSprite != null && pSprite.lotag >= kDudePlayer1 && pSprite.lotag <= kDudePlayer8;
	}

	public static int ClipRange(int value, int min, int max) {
		if(value < min)
			value = min;
		if(value > max)
			value = max;

		return value;
	}

	public static int ClipLow(int value, int min) {
		if(value < min)
			value = min;

		return value;
	}

	public static int ClipHigh(int value, int max) {
		if(value > max)
			value = max;

		return value;
	}

	public static float ClipLow(float value, int min) {
		if(value < min)
			value = min;

		return value;
	}

	public static float ClipHigh(float value, int max) {
		if(value > max)
			value = max;

		return value;
	}

	/*******************************************************************************
	FUNCTION:		GetWallAngle()

	DESCRIPTION:	Get the angle for a wall vector

	RETURNS:		Angle in the range 0 - kMax360

	NOTES:			Add kAngle90 to get the wall Normal
	*******************************************************************************/
	public static int GetWallAngle( int nWall )
	{
		int dx = wall[wall[nWall].point2].x - wall[nWall].x;
		int dy = wall[wall[nWall].point2].y - wall[nWall].y;
		return engine.getangle( dx, dy );
	}

	public static Vector2 normal = new Vector2();
	public static void GetWallNormal( int nWall )
	{
		if(!(nWall >= 0 && nWall < kMaxWalls))
			game.dassert("nWall >= 0 && nWall < kMaxWalls");

		int nx = -(wall[wall[nWall].point2].y - wall[nWall].y) >> 4;
		int ny = (wall[wall[nWall].point2].x - wall[nWall].x) >> 4;

		int length = engine.ksqrt(nx * nx + ny * ny);
		if(length <= 0)
			length = 1;

		normal.set(divscale(nx, length, 16), divscale(ny, length, 16));
	}

	private static boolean checkDistance(int nPoint2, int x, int y, int distance)
	{
		int x1 = wall[nPoint2].x;
		int y1 = wall[nPoint2].y;

		nPoint2 = wall[nPoint2].point2;
		int x2 = wall[nPoint2].x;
		int y2 = wall[nPoint2].y;

		distance *= 16;
		int dist2 = distance * distance;
		int dwx = x2 - x1;
		int dwy = y2 - y1;
		int dsx1 = x - x1;
		int dsx2 = x - x2;
		int dsy1 = y - y1;
		int dsy2 = y - y2;

		if ( x1 >= x2 )
		{
			if ( x <= x2 - distance || x >= x1 + distance )
				return false;

			if ( x1 == x2 )
		    {
				if ( y1 >= y2 )
				{
					if ( y <= y2 - distance || y >= y1 + distance )
						return false;
					if ( y < y2 )
						return dsy2 * dsy2 + dsx2 * dsx2 < dist2;
					if ( y > y1 )
						return dsy1 * dsy1 + dsx1 * dsx1 < dist2;
				}
				else
				{
					if ( y <= y1 - distance || y >= y2 + distance )
						return false;
					if ( y < y1 )
						return dsy1 * dsy1 + dsx1 * dsx1 < dist2;
					if ( y > y2 )
						return dsy2 * dsy2 + dsx2 * dsx2 < dist2;
				}
				return true;
		    }
		}
		else if ( x <= x1 - distance || x >= x2 + distance )
			return false;

		if ( y2 <= y1 )
		{
			if ( y <= y2 - distance || y >= y1 + distance )
				return false;

			if ( y1 == y2 )
		    {
			    if ( x1 >= x2 )
			    {
			    	if ( x <= x2 - distance || x >= x1 + distance )
			        	return false;
			        if ( x < x2 )
			        	return dsy2 * dsy2 + dsx2 * dsx2 < dist2;
			        if ( x > x1 )
			        	return dsy2 * dsy2 + dsx1 * dsx1 < dist2;
			    }
			    else
			    {
			        if ( x <= x1 - distance || x >= x2 + distance )
			        	return false;
			        if ( x < x1 )
			        	return dsy2 * dsy2 + dsx1 * dsx1 < dist2;
			        if ( x > x2 )
			        	return dsy2 * dsy2 + dsx2 * dsx2 < dist2;
			    }
		    }
		}
		else if ( y <= y1 - distance || y >= y2 + distance )
			return false;

		if ( dsy2 * dwy + dwx * dsx2 >= 0 )
			return dsy2 * dsy2 + dsx2 * dsx2 < dist2;
		if ( dsy1 * dwy + dwx * dsx1 <= 0 )
			return dsy1 * dsy1 + dsx1 * dsx1 < dist2;

		return ((dwy * dsx1 - dsy1 * dwx) * (dwy * dsx1 - dsy1 * dwx)) < (dist2 * (dwx * dwx + dwy * dwy));
	}

	private static final short[] handled = new short[(kMaxSectors+7)>>3];
	public static void NearSectors(int nSector, int x, int y, int distance, int[] pSectors, byte[] pSprites, int[] pWalls) {
		if(pSectors == null)
			game.dassert("pSectors != null");

		int sectorcount = 1;
		int nextsectnum = 1;
		pSectors[0] = nSector;

		Arrays.fill(handled, (short)0);
		handled[nSector >> 3] |= 1 << (nSector & 7);

		int secIndex = 0;
		int xwallcount = 0;

		if ( pSprites != null )
		{
			Arrays.fill(pSprites, (byte)0);
		    pSprites[nSector >> 3] |= 1 << (nSector & 7);
		}

		while ( secIndex < sectorcount )
		{
			int cursect = pSectors[secIndex];
			if(sector[cursect] == null) { secIndex++; continue; }
			int startwall = sector[cursect].wallptr;
			int endwall = startwall + sector[cursect].wallnum;
			if(startwall < 0 || endwall < 0) { secIndex++; continue; }
			WALL pWall = wall[startwall];
			while ( startwall < endwall )
			{
				if(pWall == null) { startwall++; continue; }
				int	nextsector = pWall.nextsector;
			    if ( nextsector >= 0 )
			    {
			        if ( (handled[nextsector >> 3] & (1 << (nextsector & 7))) == 0 )
			        {
			        	handled[nextsector >> 3] |= 1 << (nextsector & 7);
			        	int nPoint2 = wall[startwall].point2;
			        	if(checkDistance(nPoint2, x, y, distance))
			        	{
		            		if ( pSprites != null )
		            			pSprites[nextsector >> 3] |= 1 << (nextsector & 7);
		            		pSectors[sectorcount++] = nextsector;
		                    nextsectnum++;
		                    if ( pWalls != null)
		                    {
		                     	int nXWall = pWall.extra;
		                     	if ( nXWall > 0 )
		                     	{
		                     		XWALL pXWall = xwall[nXWall];
		                     		if ( pXWall.triggerVector )
			                        	if ( pXWall.state == 0 && !pXWall.isTriggered )
			                        		pWalls[xwallcount++] = startwall;
		                     	}
		                    }
			            }
			        }
			    }
			    startwall++;
			    pWall = wall[startwall];
			}
			secIndex++;
		}
		pSectors[nextsectnum] = -1;
		if ( pWalls != null )
			pWalls[xwallcount] = -1;
	}




	/*******************************************************************************
	FUNCTION:		HitScan()

	DESCRIPTION:	Returns the object hit, prioritized in sprite/wall/object
					order.

	PARAMETERS:

	RETURNS:		SS_SPRITE, SS_WALL, SS_FLOOR, SS_CEILING, or -1

	NOTES:			To simplify return object handling: SS_SPRITE is returned
					for flat and floor sprites. SS_WALL is returned for masked
					and one-way walls. In addition to the standard return
					value, HitScan fills the specified hitInfo structure with
					relevant hit data.
	*******************************************************************************/
	public static int HitScan( SPRITE pSprite, int z, int dx, int dy, int dz, Hitscan hitInfo, int clipmask, int dist)
	{
		if(pSprite == null)
			return -1;

		hitInfo.hitsect = -1;
		hitInfo.hitwall = -1;
		hitInfo.hitsprite = -1;

		// offset the starting point for the vector so we don't hit the source!
		int x = pSprite.x; // + mulscale(16, Cos(pSprite.ang), 30);
		int y = pSprite.y; // + mulscale(16, Sin(pSprite.ang), 30);
		short nSector = pSprite.sectnum;

		short oldcstat = pSprite.cstat;
		pSprite.cstat &= ~0x0100;

		if ( dist != 0 )
		{
			hitscangoalx = x + mulscale(dist << 4, Cos(pSprite.ang), 30);
			hitscangoaly = y + mulscale(dist << 4, Sin(pSprite.ang), 30);
		}
		else
		{
			hitscangoaly = 0x1FFFFFFF;
			hitscangoalx = 0x1FFFFFFF;
		}

		engine.hitscan(x, y, z, nSector, dx, dy, dz << 4, hitInfo, clipmask);

		hitscangoaly = 0x1FFFFFFF;
		hitscangoalx = 0x1FFFFFFF;

		pSprite.cstat = oldcstat;

		if (hitInfo.hitsprite >= 0)
			return SS_SPRITE;

		if (hitInfo.hitwall >= 0)
		{
			short nNextSector = wall[hitInfo.hitwall].nextsector;

			if ( nNextSector == -1 )	// single-sided wall
				return SS_WALL;
			else						// double-sided wall, check if we hit a masked wall
			{
				engine.getzsofslope(nNextSector, hitInfo.hitx, hitInfo.hity, zofslope);

				if ( hitInfo.hitz <= zofslope[CEIL] || hitInfo.hitz >= zofslope[FLOOR] )
					return SS_WALL;

				return SS_MASKED;
			}
		}

		if (hitInfo.hitsect >= 0)
			return (hitInfo.hitz > z) ? SS_FLOOR : SS_CEILING;

		return -1;
	}


	public static int VectorScan( SPRITE pActor, int xoffset, int zoffset, int dx, int dy, int dz, int dist, int flags )
	{
		SECTOR hitsect = null;
		SECTOR nextsect = null;
		if(pActor == null)
			game.dassert( "pSprite != null" );

		pHitInfo.hitsect = -1;
		pHitInfo.hitwall = -1;
		pHitInfo.hitsprite = -1;

		// offset the starting point for the vector so we don't hit the source!
		int x = pActor.x + mulscale(xoffset, Cos(pActor.ang + kAngle90), 30);
		int y = pActor.y + mulscale(xoffset, Sin(pActor.ang + kAngle90), 30);
		int z = pActor.z + zoffset;

		short oldcstat = pActor.cstat;
		pActor.cstat &= ~kSpriteHitscan;

		if ( dist != 0 )
		{
			hitscangoalx = x + mulscale(dist << 4, Cos(pActor.ang), 30);
			hitscangoaly = y + mulscale(dist << 4, Sin(pActor.ang), 30);
		}
		else
		{
			hitscangoaly = 0x1FFFFFFF;
			hitscangoalx = 0x1FFFFFFF;
		}

		engine.hitscan(x, y, z, pActor.sectnum, dx, dy, dz << 4, pHitInfo, 0x1000040);
		pActor.cstat = oldcstat;

		int hitcount = 256;
		while(hitcount != -1)
		{
			hitcount--;
			if (pHitInfo.hitsprite >= kMaxSprites || pHitInfo.hitwall >= kMaxWalls || pHitInfo.hitsect >= kMaxSectors)
	            return -1;

			if ( dist != 0)
		    {
				int hx = pHitInfo.hity - pActor.y;
		        int hy = pHitInfo.hitx - pActor.x;

		        if ( engine.qdist(hx, hy) > dist )
		        	return -1;
		    }

			if (pHitInfo.hitsprite >= 0)
			{
				SPRITE pSprite = sprite[pHitInfo.hitsprite];

				if ( (pSprite.hitag & kAttrAiming) != 0 && (flags & 1) == 0 )
			    	return SS_SPRITE;

				if ((pSprite.cstat & kSpriteRMask) == kSpriteFace) {
					int nTile = pSprite.picnum;
					Tile pic = engine.getTile(nTile);

					if (pic.getWidth() == 0 || pic.getHeight() == 0)
						return SS_SPRITE;

					int height = (pic.getHeight() * pSprite.yrepeat << 2);
					int zBot = pSprite.z;
					if ((pSprite.cstat & kSpriteOriginAlign) != 0)
						zBot += height / 2;

					if (pic.getOffsetY() != 0)
						zBot -= pic.getOffsetY() * pSprite.yrepeat << 2;

					if (height <= 0)
						game.dassert("height > 0");

					int texy = muldiv(zBot - pHitInfo.hitz, pic.getHeight(), height);
					if ((pSprite.cstat & kSpriteFlipY) == 0)
						texy = pic.getHeight() - texy;

					if (texy >= 0 && texy < pic.getHeight()) {

						int width = pSprite.xrepeat * pic.getWidth() >> 2;

						width = width * 3 / 4;    // aspect ratio correction?

						int top = dx * (y - pSprite.y) - dy * (x - pSprite.x);
						int bot = engine.ksqrt(dx * dx + dy * dy);
						if (width <= 0)
							game.dassert("width > 0");

						int texx = muldiv(top / bot, pic.getWidth(), width);
						texx += pic.getWidth() / 2 + pic.getOffsetX();

						if (texx >= 0 && texx < pic.getWidth()) {
							byte[] pTile = engine.loadtile(nTile);
							int texel = pTile[texx * pic.getHeight() + texy] & 0xFF;

							if (texel != 255)
								return SS_SPRITE;
						}
					}

					// clear hitscan bits on sprite
					oldcstat = pSprite.cstat;
					pSprite.cstat &= ~kSpriteHitscan;

					pHitInfo.hitsect = -1;
					pHitInfo.hitwall = -1;
					pHitInfo.hitsprite = -1;

					x = pHitInfo.hitx;
					y = pHitInfo.hity;
					z = pHitInfo.hitz;

					engine.hitscan(x, y, z, pSprite.sectnum, dx, dy, dz << 4, pHitInfo, 0x1000040);

					// restore the hitscan bits
					pSprite.cstat = oldcstat;

					continue;
				}
				return SS_SPRITE;
			}

			if (pHitInfo.hitwall >= 0) {
				WALL pWall = wall[pHitInfo.hitwall];
				int nextSector = pWall.nextsector;
				if(nextSector == -1)
					return SS_WALL;
				else
				{
					hitsect = sector[pHitInfo.hitsect];
				    nextsect = sector[nextSector];
				    engine.getzsofslope((short)nextSector, pHitInfo.hitx, pHitInfo.hity, zofslope);
				    if ( pHitInfo.hitz <= zofslope[CEIL] )
				    	return SS_WALL;
				    if ( pHitInfo.hitz >= zofslope[FLOOR] ) {
				    	if ( (hitsect != null && (hitsect.floorstat & 1) == 0) || (nextsect != null && (nextsect.floorstat & 1) == 0) )
							return SS_WALL;
						return SS_FLOOR;
				    }
				    if ( (pWall.cstat & (kWallOneWay | kWallMasked)) == 0 )
					    return SS_WALL;

				    // must be a masked wall
					int zOrg;
					if ( (pWall.cstat & kWallBottomOrg) != 0 )
						zOrg = ClipHigh(hitsect.floorz, nextsect.floorz);
					else
						zOrg = ClipLow(hitsect.ceilingz, nextsect.ceilingz);

					int zOff = (pHitInfo.hitz - zOrg) >> 8;
					if ( (pWall.cstat & kWallFlipY) != 0)
						zOff = -zOff;

					int nTile = pWall.overpicnum;
					Tile pic = engine.getTile(nTile);

					int tsizx = pic.getWidth();
					int tsizy = pic.getHeight();
					boolean xnice = (1 << (picsiz[nTile] & 15)) == tsizx;
					boolean ynice = (1 << (picsiz[nTile] >> 4)) == tsizy;
					if(tsizx == 0 || tsizy == 0)
				    	return SS_WALL;

					// calculate y texel coord
					int texy = zOff * pWall.yrepeat / 8 + pWall.ypanning * tsizy / 256;

					int len = (int) engine.qdist(pWall.x - wall[pWall.point2].x, pWall.y - wall[pWall.point2].y);
					int distance, texel;

					if ( (pWall.cstat & kWallFlipX) != 0 )
						distance = (int) engine.qdist(pHitInfo.hitx - wall[pWall.point2].x, pHitInfo.hity - wall[pWall.point2].y);
					else
						distance = (int) engine.qdist(pHitInfo.hitx - pWall.x, pHitInfo.hity - pWall.y);

					// calculate x texel coord
					int texx = distance * pWall.xrepeat * 8 / len + pWall.xpanning;

					if ( xnice )
						texx &= (tsizx - 1);
					else
						texx %= tsizx;

					if ( ynice )
						texy &= (tsizy - 1);
					else
						texy %= tsizy;

					byte[] pTile = engine.loadtile(nTile);

					if ( ynice )
						texel = pTile[(texx << (picsiz[nTile] >> 4)) + texy] & 0xFF;
					else {
						if((texx * pic.getHeight() + texy) >= 0)
							texel = pTile[texx * pic.getHeight() + texy] & 0xFF;
						else texel = 0;
					}

				    if ( texel == 255 )
				    {
					    // clear hitscan bits on both sides of the wall
						short oldcstat1 = pWall.cstat;
						pWall.cstat &= ~kWallHitscan;
						short oldcstat2 = wall[pWall.nextwall].cstat;
						wall[pWall.nextwall].cstat &= ~kWallHitscan;

						pHitInfo.hitsect = -1;
						pHitInfo.hitwall = -1;
						pHitInfo.hitsprite = -1;

						x = pHitInfo.hitx;
						y = pHitInfo.hity;
						z = pHitInfo.hitz;

						engine.hitscan(x, y, z,  pWall.nextsector, dx, dy, dz << 4, pHitInfo, 0x1000040);

						// restore the hitscan bits
						pWall.cstat = oldcstat1;
						wall[pWall.nextwall].cstat = oldcstat2;

						continue;
				    }
				    return SS_MASKED;
				}
			}

			if (pHitInfo.hitsect >= 0) {
				if( dz <= 0) {
					int nLower = gLowerLink[pHitInfo.hitsect];
					if( nLower < 0)
						return SS_CEILING;
					int nUpper = sprite[nLower].owner;
					if( nUpper < 0)
						return SS_CEILING;
					pHitInfo.hitsect = -1;
			        pHitInfo.hitwall = -1;
			        pHitInfo.hitsprite = -1;

			        x = pHitInfo.hitx + sprite[nUpper].x - sprite[nLower].x;
					y = pHitInfo.hity + sprite[nUpper].y - sprite[nLower].y;
					z = pHitInfo.hitz + sprite[nUpper].z - sprite[nLower].z;

					engine.hitscan(x, y, z, sprite[nUpper].sectnum, dx, dy, dz << 4, pHitInfo, 0x1000040);
					continue;
				} else {
					int nUpper = gUpperLink[pHitInfo.hitsect];
					if( nUpper < 0)
						return SS_FLOOR;
					int nLower = sprite[nUpper].owner;
					if( nLower < 0)
						return SS_FLOOR;
					pHitInfo.hitsect = -1;
			        pHitInfo.hitwall = -1;
			        pHitInfo.hitsprite = -1;

			        x = pHitInfo.hitx + sprite[nLower].x - sprite[nUpper].x;
					y = pHitInfo.hity + sprite[nLower].y - sprite[nUpper].y;
					z = pHitInfo.hitz + sprite[nLower].z - sprite[nUpper].z;

					engine.hitscan(x, y, z, sprite[nLower].sectnum, dx, dy, dz << 4, pHitInfo, 0x1000040);
					continue;
				}
			}
			return -1;
		}
		return -1;
	}

	/*******************************************************************************
	FUNCTION:		FindSector()

	DESCRIPTION:	This function works like Build's updatesector() function
					except it takes into account z position, which makes it
					give correct values in areas where sectors overlap.

	PARAMETERS:		You should supplies a starting search sector in the nSector
	variable.

	RETURNS:		TRUE if point found and updates nSector, FALSE otherwise.
	*******************************************************************************/
	public static short foundSector;
	public static boolean FindSector(int x, int y, int z, short nSector)
	{
		foundSector = nSector;
		if ( engine.inside(x, y, nSector) != 0)
		{
			engine.getzsofslope(nSector, x, y, zofslope);
			if (z >= zofslope[CEIL] && z <= zofslope[FLOOR]) {
				return true;
			}
		}

		int wallid = sector[nSector].wallptr;
		for (int i = sector[nSector].wallnum; i > 0; i--, wallid++)
		{
			short j = wall[wallid].nextsector;
			if ( j >= 0 && engine.inside(x, y, j) != 0 )
			{
				engine.getzsofslope(j, x, y, zofslope);
				if (z >= zofslope[CEIL] && z <= zofslope[FLOOR])
				{
					nSector = j;
					foundSector = nSector;
					return true;
				}
			}
		}

		for (int i = 0; i < numsectors; i++)
		{
			if ( engine.inside(x, y, (short)i) != 0)
			{
				engine.getzsofslope((short)i, x, y, zofslope);
				if (z >= zofslope[CEIL] && z <= zofslope[FLOOR])
				{
					nSector = (short)i;
					foundSector = nSector;
					return true;
				}
			}
		}

		return false;
	}

	public static int clipm_px, clipm_py, clipm_pz, clipm_pnsectnum;
	public static boolean clipmove_error;
	public static int ClipMove( int px, int py, int pz, short pnSector, long dx, long dy,
	int wallDist, int ceilDist, int floorDist, int clipType )
	{
		clipmove_error = false;
		int ccode = engine.clipmove(px, py, pz, pnSector, dx << 14, dy << 14,
			wallDist, ceilDist, floorDist, clipType);

		clipm_px = clipmove_x; clipm_py = clipmove_y; clipm_pz = clipmove_z;
		clipm_pnsectnum = clipmove_sectnum;

		// force temporary fix to ken's inside() bug
		if ( clipmove_sectnum == -1)
		{
			// return to last known good location
			clipmove_error = true;
			clipm_px = px;
			clipm_py = py;
			clipm_pz = pz;
			clipm_pnsectnum = pnSector;
		}
		return ccode;
	}


	/*******************************************************************************
		FUNCTION:		GetZRange()

		DESCRIPTION:	Cover function for Ken's getzrange

		PARAMETERS:

		RETURNS:

		NOTES:
	*******************************************************************************/
	public static int gz_ceilZ, gz_ceilHit, gz_floorZ, gz_floorHit;
	public static void GetZRange( SPRITE pSprite, int clipdist, int cliptype )
	{
		if(pSprite == null) game.dassert("pSprite != null");

		short oldcstat = pSprite.cstat;
		pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;

		engine.getzrange(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, clipdist, cliptype);

		gz_ceilZ = zr_ceilz; gz_ceilHit = zr_ceilhit;
		gz_floorZ = zr_florz; gz_floorHit = zr_florhit;

		if ( (gz_floorHit & kHitTypeMask) == kHitSector ) {
		    int sectnum = gz_floorHit & kHitIndexMask;
		    if ( (cliptype & 0x2000) == 0 && (sector[sectnum].floorstat & 1) != 0 )
		    	gz_floorZ = 0x7FFFFFFF;

		    int nXSector = sector[sectnum].extra;
		    if ( nXSector > 0 )
		    	gz_floorZ += xsector[nXSector].Depth << 10;

		    int nUpper = gUpperLink[sectnum];
		    if ( nUpper >= 0 ) {
		    	int nLower = sprite[nUpper].owner;
		    	if(nLower >= 0) {
		    		engine.getzrange(pSprite.x + sprite[nLower].x - sprite[nUpper].x, pSprite.y + sprite[nLower].y - sprite[nUpper].y, pSprite.z + sprite[nLower].z - sprite[nUpper].z, sprite[nLower].sectnum, clipdist, cliptype);
			    	gz_floorZ = zr_florz; gz_floorHit = zr_florhit;
			    	gz_floorZ -= (sprite[nLower].z - sprite[nUpper].z);
		    	}
		    }
		}

		if ( (gz_ceilHit & kHitTypeMask) == kHitSector ) {
			int sectnum = gz_ceilHit & kHitIndexMask;
			if ( (cliptype & 0x1000) == 0 && (sector[sectnum].ceilingstat & 1) != 0 )
				gz_ceilZ = 0x80000000;
		    int nLower = gLowerLink[sectnum];
		    if ( nLower >= 0 ) {
		    	int nUpper = sprite[nLower].owner;
		    	if(nUpper >= 0) {
		    		engine.getzrange(pSprite.x + sprite[nUpper].x - sprite[nLower].x, pSprite.y + sprite[nUpper].y - sprite[nLower].y, pSprite.z + sprite[nUpper].z - sprite[nLower].z, sprite[nUpper].sectnum, clipdist, cliptype);
			    	gz_ceilZ = zr_ceilz; gz_ceilHit = zr_ceilhit;
			    	gz_ceilZ -= (sprite[nUpper].z - sprite[nLower].z);
		    	}
		    }
		}
		pSprite.cstat = oldcstat;
	}

	public static void GetZRange( int x, int y, int z, short nSector, int clipdist, int cliptype )
	{
		engine.getzrange(x, y, z, nSector, clipdist, cliptype);

		gz_ceilZ = zr_ceilz; gz_ceilHit = zr_ceilhit;
		gz_floorZ = zr_florz; gz_floorHit = zr_florhit;

		if ( (gz_floorHit & kHitTypeMask) == kHitSector ) {
		    int sectnum = gz_floorHit & 0x1FFF;
		    if ( (cliptype & 0x2000) == 0 && (sector[sectnum].floorstat & 1) != 0 )
		    	gz_floorZ = 0x7FFFFFFF;
		    int nXSector = sector[sectnum].extra;
		    if ( nXSector > 0 )
		    	gz_floorZ += xsector[nXSector].Depth << 10;
		    int nUpper = gUpperLink[sectnum]; int nLower;
		    if ( nUpper >= 0 ) {
		    	nLower = sprite[nUpper].owner;
		    	if(nLower >= 0) {
		    		engine.getzrange(x + sprite[nLower].x - sprite[nUpper].x, y + sprite[nLower].y - sprite[nUpper].y, z + sprite[nLower].z - sprite[nUpper].z, sprite[nLower].sectnum, clipdist, cliptype);

			    	gz_floorZ = zr_florz; gz_floorHit = zr_florhit;
			    	gz_floorZ -= (sprite[nLower].z - sprite[nUpper].z);
		    	}
		    }
		}

		if ( (gz_ceilHit & kHitTypeMask) == kHitSector ) {
			int sectnum = gz_ceilHit & 0x1FFF;
			if ( (cliptype & 0x1000) == 0 && (sector[sectnum].ceilingstat & 1) != 0 )
				gz_ceilZ = 0x80000000;
		    int nLower = gLowerLink[sectnum];  int nUpper;
		    if ( nLower >= 0 ) {
		    	nUpper = sprite[nLower].owner;
		    	if(nUpper >= 0) {
		    		engine.getzrange(x + sprite[nUpper].x - sprite[nLower].x, y + sprite[nUpper].y - sprite[nLower].y, z + sprite[nUpper].z - sprite[nLower].z, sprite[nUpper].sectnum, clipdist, cliptype);
			    	gz_ceilZ = zr_ceilz; gz_ceilHit = zr_ceilhit;
			    	gz_ceilZ -= (sprite[nUpper].z - sprite[nLower].z);
		    	}
		    }
		}
	}

	public static boolean CheckProximity( SPRITE pSprite, int x, int y, int z, int nSector, int dist )
	{
		if(pSprite == null) game.dassert("pSprite != null");

		long dx = klabs(x - pSprite.x) >> 4;
		if (dx < dist)
		{
			long dy = klabs(y - pSprite.y) >> 4;
			if (dy < dist)
			{
				long dz = klabs(z - pSprite.z) >> 8;
				if ( dz < dist && engine.qdist(dx, dy) < dist )
				{
					GetSpriteExtents(pSprite);
					if ( engine.cansee(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, x, y, z, (short)nSector) )
						return true;
					if ( engine.cansee(pSprite.x, pSprite.y, extents_zTop, pSprite.sectnum, x, y, z, (short)nSector) )
					    return true;
					return engine.cansee(pSprite.x, pSprite.y, extents_zBot, pSprite.sectnum, x, y, z, (short) nSector);
				}
			}
		}
		return false;
	}

	public static int Dist3d(int dx, int dy, int dz)
	{
		// Euclidean 3D distance
		dx >>= 4;
		dy >>= 4;
		dz >>= 8;
		return engine.ksqrt(dx * dx + dy * dy + dz * dz);

	}

	public static int bseed;
	public static int vseed = 1;
	public static void sRandom(long set)
	{
		bseed = (int)set;
	}

	public static int bRandom()
	{
		bseed = (bseed*1103515245)+12345;
		return ( bseed >> 16 ) & 0x7FFF;
	}

	public static int vRandom()
	{
		int var = 2 * vseed;
		if ( (byte)vseed > ((byte)vseed + (byte)vseed) )
			var = var ^ 0x20000004 | 1;
		vseed = var;
		return var & 0x7FFF;
	}

	public static int ViRandom(int var) {
		return mulscale(var, vRandom(), 14) - var;
	}

	public static boolean Chance(int var) {
		return bRandom() < var;
	}

	public static int BiRandom(int var) {
		return mulscale(var, bRandom(), 14) - var;
	}

	public static int BiRandom2(int var) {
		return mulscale(var, bRandom() + bRandom(), 15) - var;
	}

	public static int Random(int var) {
		return mulscale(var, bRandom(), 15);
	}

	private static final char[] buf = new char[80];
	public static char[] toCharArray(String... text)
	{
		buildString(buf, 0, text);
		return buf;
	}
}
