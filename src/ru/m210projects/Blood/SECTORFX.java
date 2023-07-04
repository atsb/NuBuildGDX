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

import static ru.m210projects.Blood.DB.kFluxMask;
import static ru.m210projects.Blood.DB.kMaxXSectors;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.DB.xwall;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle360;
import static ru.m210projects.Blood.Globals.kAngle90;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kMaxSectors;
import static ru.m210projects.Blood.Globals.kSectorExpand;
import static ru.m210projects.Blood.Globals.kSectorRelAlign;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Build.Types.SECTOR;

public class SECTORFX {
	
	public static short[] shadeList = new short[kMaxXSectors];
	public static short[] panList = new short[kMaxXSectors];
	public static int shadeCount = 0, panCount = 0;
	
	public static short[] wallPanList = new short[kMaxXSectors];
	public static int wallPanCount = 0;
	
	public static final int kPanScale =	10;
	
	public static final int kWaveNone = 0;
	public static final int kWaveSquare = 1;
	public static final int kWaveSaw = 2;
	public static final int kWaveRampup = 3;
	public static final int kWaveRampdown = 4;
	public static final int kWaveSine = 5;
	public static final int kWaveFlicker1 = 6;
	public static final int kWaveFlicker2 = 7;
	public static final int kWaveFlicker3 = 8;
	public static final int kWaveFlicker4 = 9;
	public static final int kWaveStrobe = 10;
	public static final int kWaveSearch = 11;
	
	// monotonic flicker -- very doom like
	public static final int[] flicker1 =
	{
		0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0,
		1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1,
		0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1,
	}; 
	// organic flicker -- good for torches
	public static final int[] flicker2 =
	{
		1, 2, 4, 2, 3, 4, 3, 2, 0, 0, 1, 2, 4, 3, 2, 0,
		2, 1, 0, 1, 0, 2, 3, 4, 3, 2, 1, 1, 2, 0, 0, 1,
		1, 2, 3, 4, 4, 3, 2, 1, 2, 3, 4, 4, 2, 1, 0, 1,
		0, 0, 0, 0, 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2,
	}; 	
	// mostly on flicker -- good for flaky fluourescents
	public static final int[] flicker3 =
	{
		4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 2, 4, 3, 4, 4,
		4, 4, 2, 1, 3, 3, 3, 4, 3, 4, 4, 4, 4, 4, 2, 4,
		4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 2, 1, 0, 1,
		0, 1, 0, 1, 0, 2, 3, 4, 4, 4, 4, 4, 4, 4, 3, 4,
	}; 
	public static final int[] flicker4 =
	{
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		4, 0, 0, 3, 0, 1, 0, 1, 0, 4, 4, 4, 4, 4, 2, 0,
		0, 0, 0, 4, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 2, 1, 2, 1, 2, 1, 2, 1, 4, 3, 2,
	};
	public static final int[] strobe = {
		64, 64, 64, 48, 36, 27, 20, 15, 11, 9, 6, 5, 4, 3, 2, 2,
		1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	};

	public static void InitSectorFX()
	{
		int i;
		shadeCount = 0;
		panCount = 0;
		wallPanCount = 0;

		for (i = 0; i < numsectors; i++)
		{
			short nXSector = sector[i].extra;
			if ( nXSector > 0 )
			{
				XSECTOR pXSector = xsector[nXSector];
				if ( pXSector.amplitude != 0 )
					shadeList[shadeCount++] = nXSector;
				if ( pXSector.panVel != 0 )
					panList[panCount++] = nXSector;
			}
		}

		for (i = 0; i < numwalls; i++)
		{
			short nXWall = wall[i].extra;
			if ( nXWall > 0 )
			{
				XWALL pXWall = xwall[nXWall];
				if ( pXWall.panXVel != 0 || pXWall.panYVel != 0)
					wallPanList[wallPanCount++] = nXWall;
			}
		}
	}
	
	public static void DoSectorPanning()
	{
		int i;

		for (i = 0; i < panCount; i++)
		{
			int nXSector = panList[i];
			XSECTOR pXSector = xsector[nXSector];
			int nSector = pXSector.reference;
			if(!(nSector >= 0 && nSector < kMaxSectors)) game.dassert("nSector >= 0 && nSector < kMaxSectors");
			SECTOR pSector = sector[nSector];

			if(pSector.extra != nXSector)
				game.dassert("pSector.extra == nXSector");

			if ( pXSector.panAlways || pXSector.busy != 0)
			{
				int panAngle = pXSector.panAngle + kAngle180;
			
				int panVel = (pXSector.panVel & 0xFF) << kPanScale;

				if ( !pXSector.panAlways && (pXSector.busy & kFluxMask) != 0 )
					panVel = mulscale(panVel, pXSector.busy, 16);

				if ( pXSector.panFloor )
				{
					int nTile = pSector.floorpicnum;
					if ( (pSector.floorstat & kSectorRelAlign) != 0 )
				          panAngle -= kAngle90;

					int panX = (pSector.floorxpanning << 8) + pXSector.floorxpanFrac;
					int panY = (pSector.floorypanning << 8) + pXSector.floorypanFrac;

					panX += mulscale(kFrameTicks * panVel, Cos(panAngle), 30) >>
						((picsiz[nTile] & 0xF) - ((pSector.floorstat & kSectorExpand) != 0 ? 1 : 0));
					panY -= mulscale(kFrameTicks * panVel, Sin(panAngle), 30) >>
						((picsiz[nTile] / 16) - ((pSector.floorstat & kSectorExpand) != 0 ? 1 : 0));

					pSector.floorxpanning = (byte)(panX >> 8);
					pSector.floorypanning = (byte)(panY >> 8);
					pXSector.floorxpanFrac = panX & 0xFF;
					pXSector.floorypanFrac = panY & 0xFF;
				}

				if ( pXSector.panCeiling )
				{
					int nTile = pSector.ceilingpicnum;
					if ( (pSector.ceilingstat & kSectorRelAlign) != 0 )
				          panAngle -= kAngle90;
					
					int panX = (pSector.ceilingxpanning << 8) + pXSector.ceilxpanFrac;
					int panY = (pSector.ceilingypanning << 8) + pXSector.ceilypanFrac;

					panX += mulscale(kFrameTicks * panVel, Cos(panAngle), 30) >>
						((picsiz[nTile] & 0xF) - ((pSector.ceilingstat & kSectorExpand) != 0 ? 1 : 0));
					panY += mulscale(kFrameTicks * panVel, Sin(panAngle), 30) >>
						((picsiz[nTile] / 16) - ((pSector.ceilingstat & kSectorExpand) != 0 ? 1 : 0));

					pSector.ceilingxpanning = (byte)(panX >> 8);
					pSector.ceilingypanning = (byte)(panY >> 8);
					pXSector.ceilxpanFrac = panX & 0xFF;
					pXSector.ceilypanFrac = panY & 0xFF;
				}
			}
		}

		for (i = 0; i < wallPanCount; i++)
		{
			int nXWall = wallPanList[i];
			XWALL pXWall = xwall[nXWall];
			int nWall = pXWall.reference;
			if(wall[nWall].extra != nXWall)
				game.dassert("wall[nWall].extra == nXWall");

			if ( pXWall.panAlways || pXWall.busy != 0 )
			{
				int panXVel = pXWall.panXVel << 10;
				int panYVel = pXWall.panYVel << 10;

				if ( !pXWall.panAlways && (pXWall.busy & kFluxMask) != 0 )
				{
					panXVel = mulscale(panXVel, pXWall.busy, 16);
					panYVel = mulscale(panYVel, pXWall.busy, 16);
				}

				int nTile = wall[nWall].picnum;

				int panX = (wall[nWall].xpanning << 8) + pXWall.xpanFrac;
				int panY = (wall[nWall].ypanning << 8) + pXWall.ypanFrac;

				panX += kFrameTicks * panXVel >> (picsiz[nTile] & 0xF);
				panY += kFrameTicks * panYVel >> (picsiz[nTile] / 16);

				wall[nWall].xpanning = (byte)(panX >> 8);
				wall[nWall].ypanning = (byte)(panY >> 8);
				pXWall.xpanFrac = panX & 0xFF;
				pXWall.ypanFrac = panY & 0xFF;
			}
		}
	}
	
	public static int GetWaveValue( int nWave, long time, int freq, int amplitude )
	{
		switch (nWave)
		{
			case kWaveNone:
				return amplitude;

			case kWaveSquare:
				return (int) (((time * freq >> 10) & 1) * amplitude);

			case kWaveSaw:
				return (int) (klabs(0x80 - ((time * freq >> 3) & 0xFF)) * amplitude >> 7);

			case kWaveRampup:
				return (int) (((time * freq >> 3) & 0xFF) * amplitude >> 8);

			case kWaveRampdown:
				return (int) ((0xFF - ((time * freq >> 3) & 0xFF)) * amplitude >> 8);

			case kWaveSine:
				return (amplitude + mulscale(amplitude, Sin((int)(time * freq)), 30)) >> 1;

			case kWaveFlicker1:
				return flicker1[(int) ((time * freq >> 5) & 0x3F)] * amplitude;

			case kWaveFlicker2:
				return flicker2[(int) ((time * freq >> 5) & 0x3F)] * amplitude >> 2;

			case kWaveFlicker3:
				return flicker3[(int) ((time * freq >> 5) & 0x3F)] * amplitude >> 2;

			case kWaveFlicker4:
				return flicker4[(int) ((time * freq >> 5) & 0x3F)] * amplitude >> 2;

			case kWaveStrobe:
				return strobe[(int) ((time * freq >> 5) & 0x3F)] * amplitude >> 6;

			case kWaveSearch:
			{
				int phi = (int) (((time * freq) & kAngleMask) << 2);
				if (phi > kAngle360)
					return 0;
				return (amplitude - mulscale(amplitude, Cos(phi), 30)) >> 1;
			}
		}
		return 0;
	}

	public static void DoSectorLighting() {
		int i, nXSector, nSector;
		int nWave, freq, amplitude, value;
		int nWall, startwall, endwall;

		for (i = 0; i < shadeCount; i++)
		{
			nXSector = shadeList[i];
			XSECTOR pXSector = xsector[nXSector];
			nSector = pXSector.reference;
			if(sector[nSector].extra != nXSector)
				game.dassert("sector[nSector].extra == nXSector");
			
			if( pXSector.shade != 0 ) {
				value = pXSector.shade;
				
				if ( pXSector.shadeFloor ) {
					sector[nSector].floorshade -= value;
					if ( pXSector.color )
			        {
						short floorpal = pXSector.floorpal;
						short pal = sector[nSector].floorpal;
						pXSector.floorpal = pal;
			        	sector[nSector].floorpal = floorpal;
			        }
				}
				if ( pXSector.shadeCeiling ) {
					sector[nSector].ceilingshade -= value;
					
					if ( pXSector.color )
			        {
						short ceilingpal = pXSector.ceilpal;
						short pal = sector[nSector].ceilingpal;
						pXSector.ceilpal = pal;
			        	sector[nSector].ceilingpal = ceilingpal;
			        }
				}
				if ( pXSector.shadeWalls )
				{
					startwall = sector[nSector].wallptr;
					endwall = startwall + sector[nSector].wallnum-1;
					for (nWall = startwall; nWall <= endwall; nWall++) {
						wall[nWall].shade -= value;
						if ( pXSector.color )
							wall[nWall].pal = sector[nSector].floorpal;
					}
				}
				
				pXSector.shade = 0;
			}
			
			if ( pXSector.shadeAlways || pXSector.busy != 0 )
			{
				freq = pXSector.freq;
				nWave = pXSector.wave;
				amplitude = pXSector.amplitude;
				
				if ( !pXSector.shadeAlways && pXSector.busy != 0 )
					amplitude = mulscale(amplitude, pXSector.busy, 16);
				
				value = GetWaveValue(nWave, totalclock + pXSector.phase, freq, amplitude);
				
				if ( pXSector.shadeFloor ) {
					sector[nSector].floorshade = (byte)ClipRange(sector[nSector].floorshade + value, -128, 127);
					if ( pXSector.color && value != 0 )
			        {
						short floorpal = pXSector.floorpal;
						short pal = sector[nSector].floorpal;
						pXSector.floorpal = pal;
			        	sector[nSector].floorpal = floorpal;
			        }
				}

				if ( pXSector.shadeCeiling ) {
					sector[nSector].ceilingshade = (byte)ClipRange(sector[nSector].ceilingshade + value, -128, 127);
					if ( pXSector.color && value != 0 )
			        {
						short ceilingpal = pXSector.ceilpal;
						short pal = sector[nSector].ceilingpal;
						pXSector.ceilpal = pal;
			        	sector[nSector].ceilingpal = ceilingpal;
			        }
				}

				if ( pXSector.shadeWalls )
				{
					startwall = sector[nSector].wallptr;
					endwall = startwall + sector[nSector].wallnum-1;
					for (nWall = startwall; nWall <= endwall; nWall++) {
						wall[nWall].shade = (byte)ClipRange(wall[nWall].shade + value, -128, 127);
						if ( pXSector.color && value != 0 )
							wall[nWall].pal = sector[nSector].floorpal;
					}
				}

				pXSector.shade = value;
			}
		}
	}
}
