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

import static ru.m210projects.Blood.Actor.actSpawnEffect;
import static ru.m210projects.Blood.Actor.actSpawnThing;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.Gameutils.BiRandom;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;

import static ru.m210projects.Blood.SOUND.sfxCreate3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

import com.badlogic.gdx.math.Vector3;

public class Gib {
	
	THINKFX[] kThinkFX;
	int nThinkFX;
	GIBFX[] kGibFX;
	int nGibFx;
	int nSoundId;
	
	public Gib(GIBFX[] kGibFX, int nGibFx, THINKFX[] kThinkFX, int nThinkFX, int nSoundId ) {
		this.kThinkFX = kThinkFX;
		this.nThinkFX = nThinkFX;
		this.kGibFX = kGibFX;
		this.nGibFx = nGibFx;
		this.nSoundId = nSoundId;
	}

	public final static THINKFX[] kThinkFXHuman = {
		new THINKFX(425, 1454, 917504, 300, 900),
		new THINKFX(425, 1454, 917504, 300, 900),
		new THINKFX(425, 1267, 917504, 300, 900),
		new THINKFX(425, 1267, 917504, 300, 900),
		new THINKFX(425, 1268, 917504, 300, 900),
		new THINKFX(425, 1269, 917504, 300, 900),
		new THINKFX(425, 1456, 917504, 300, 900),
	};
	
	public final static THINKFX[] kThinkFXMime = {
		new THINKFX(425, 2405, 917504, 300, 900),
		new THINKFX(425, 2405, 917504, 300, 900),
		new THINKFX(425, 2404, 917504, 300, 900),
		new THINKFX(425, 1268, 32768, 300, 900),
		new THINKFX(425, 1269, 32768, 300, 900),
		new THINKFX(425, 1456, 32768, 300, 900),
	};
	
	public final static THINKFX[] kThinkFXHound = {
		new THINKFX(425, 1326, 917504, 300, 900),
		new THINKFX(425, 1268, 32768, 300, 900),
		new THINKFX(425, 1269, 32768, 300, 900),
		new THINKFX(425, 1456, 32768, 300, 900),
	};
	
	public final static THINKFX[] kThinkFXGargoyle = {
		new THINKFX(425, 1369, 917504, 300, 900),
		new THINKFX(425, 1361, 917504, 300, 900),
		new THINKFX(425, 1268, 32768, 300, 900),
		new THINKFX(425, 1269, 32768, 300, 900),
		new THINKFX(425, 1456, 32768, 300, 900),
	};
	
	public final static THINKFX[] kThinkFXZombie = {
		new THINKFX(427, 3405, 917504, 0, 0),
	};
	
	public final static GIBFX[] kGibFXGlassT = {
		new GIBFX (18, 0, 65536, 3, 200, 400),
		new GIBFX (31, 0, 32768, 5, 200, 400)
	};
	public final static GIBFX[] kGibFXGlassS   = { new GIBFX(18, 0, 65536, 8, 200, 400) };
	public final static GIBFX[] kGibFXBurnShard   = { new GIBFX(16, 0, 65536, 12, 500, 1000) }; 
	public final static GIBFX[] kGibFXWoodShard   = { new GIBFX(17, 0, 65536, 12, 500, 1000) };
	public final static GIBFX[] kGibFXMetalShard   = { new GIBFX(30, 0, 65536, 12, 500, 1000) }; 
	public final static GIBFX[] kGibFXFireSpark   = { new GIBFX(14, 0, 65536, 8, 500, 1000) }; 
	public final static GIBFX[] kGibFXShockSpark   = { new GIBFX(15, 0, 65536, 8, 500, 1000) }; 
	public final static GIBFX[] kGibFXBloodChunks   = { new GIBFX(13, 0, 65536, 8, 90, 600) }; //Blood
	public final static GIBFX[] kGibFXFlames2   = { new GIBFX(56, 0, 65536, 8, 100, 0) }; 
	public final static GIBFX[] kGibFXFlames1   = { new GIBFX(32, 0, 65536, 8, 100, 0) }; 
	public final static GIBFX[] kGibFXBubblesS   = { new GIBFX(25, 0, 65536, 8, 200, 400) };
	public final static GIBFX[] kGibFXBubblesM   = { new GIBFX(24, 0, 65536, 8, 200, 400) };
	public final static GIBFX[] kGibFXBubblesL   = { new GIBFX(23, 0, 65536, 8, 200, 400) };
	public final static GIBFX[] kGibFXIcicles   = { new GIBFX(31, 0, 65536, 15, 200, 400) };                    
	public final static GIBFX[] kGibFXGlassCombo1   = {  
		new GIBFX(18, 0, 65536, 15, 200, 400),               
		new GIBFX(31, 0, 65536, 10, 200, 400),
	};
    public final static GIBFX[] kGibFXGlassCombo2   = {  
    	new GIBFX(18, 0, 65536, 5, 200, 400),         
	    new GIBFX(20, 0, 53248, 5, 200, 400),
	    new GIBFX(21, 0, 53248, 5, 200, 400),
	    new GIBFX(19, 0, 53248, 5, 200, 400),
	    new GIBFX(22, 0, 53248, 5, 200, 400),  
    };
    public final static GIBFX[] kGibFXWoodCombo   = {  
    	new GIBFX(16, 0, 65536, 8, 500, 1000),                
	    new GIBFX(17, 0, 65536, 8, 500, 1000),
	    new GIBFX(14, 0, 65536, 8, 500, 1000),  
	};
    public final static GIBFX[] kGibFXMedicCombo   = {  
    	new GIBFX(18, 0, 32768, 7, 200, 400),             
	    new GIBFX(30, 0, 65536, 7, 500, 1000),
	    new GIBFX(13, 0, 65536, 10, 90, 600),
	    new GIBFX(14, 0, 32768, 7, 500, 1000),  
	};
    public final static GIBFX[] kGibFXFlareSpark   = { new GIBFX(28, 0, 32768, 15, 128, -128) };                     
    public final static GIBFX[] kGibFXBloodBits   = { new GIBFX(13, 0, 45056, 8, 90, 600) };                       
    public final static GIBFX[] kGibFXRockShards   = {  
    	new GIBFX(46, 0, 65536, 10, 300, 800),                 
    	new GIBFX(31, 0, 32768, 10, 200, 1000),  
    };
    public final static GIBFX[] kGibFXPaperCombo1   = { 
    	new GIBFX(47, 0, 65536, 12, 300, 600),                    
    	new GIBFX(14, 0, 65536, 8, 500, 1000) 
    };
    public final static GIBFX[] kGibFXPlantCombo1   = {
    	new GIBFX(44, 0, 45056, 8, 400, 800),                   
    	new GIBFX(45, 0, 45056, 8, 300, 800),
    	new GIBFX(14, 0, 45056, 6, 500, 1000) 
    };
    public final static GIBFX[] kGibFXShockGibs1   = { new GIBFX(49, 0, 65536, 4, 80, 300),  };                       
    public final static GIBFX[] kGibFXShockGibs2   = { new GIBFX(50, 0, 65536, 4, 80, 0),   };
    public final static GIBFX[] kGibFXShockGibs3   = { 
    	new GIBFX(50, 0, 65536, 20, 800, -40),                     
    	new GIBFX(15, 0, 65536, 15, 400, 10) 
    };

    public static final int kGibMax = 31;
	public final static Gib[] gGIBInfo = { //v1.21 ok
		/*  0 */ new Gib(kGibFXGlassT, 2, null, 0, 300),
		/*  1 */ new Gib( kGibFXGlassS, 1, null, 0, 300 ),
		/*  2 */ new Gib( kGibFXBurnShard, 1, null, 0, 0 ),
		/*  3 */ new Gib( kGibFXWoodShard, 1, null, 0, 0 ),
		/*  4 */ new Gib( kGibFXMetalShard, 1, null, 0, 0 ),
		/*  5 */ new Gib( kGibFXFireSpark, 1, null, 0, 0 ),
		/*  6 */ new Gib( kGibFXShockSpark, 1, null, 0, 0 ),
		/*  7 */ new Gib( kGibFXBloodChunks, 1, null, 0, 0 ), //Blood 
		/*  8 */ new Gib( kGibFXBubblesS, 1, null, 0, 0 ),
		/*  9 */ new Gib( kGibFXBubblesM, 1, null, 0, 0 ),
		/* 10 */ new Gib( kGibFXBubblesL, 1, null, 0, 0 ), 
		/* 11 */ new Gib( kGibFXIcicles, 1, null, 0, 0 ),
		/* 12 */ new Gib( kGibFXGlassCombo1, 2, null, 0, 300 ),
		/* 13 */ new Gib( kGibFXGlassCombo2, 5, null, 0, 300 ),
		/* 14 */ new Gib( kGibFXWoodCombo, 3, null, 0, 0 ),
		/* 15 */ new Gib( null, 0, kThinkFXHuman, 7, 0 ),
		/* 16 */ new Gib( kGibFXMedicCombo, 4, null, 0, 0 ),
		/* 17 */ new Gib( kGibFXFlareSpark, 1, null, 0, 0 ),
		/* 18 */ new Gib( kGibFXBloodBits, 1, null, 0, 0 ),
		/* 19 */ new Gib( kGibFXRockShards, 2, null, 0, 0 ),
		/* 20 */ new Gib( kGibFXPaperCombo1, 2, null, 0, 0 ),
		/* 21 */ new Gib( kGibFXPlantCombo1, 3, null, 0, 0 ),
		/* 22 */ new Gib( kGibFXShockGibs1, 1, null, 0, 0 ),
		/* 23 */ new Gib( kGibFXShockGibs2, 1, null, 0, 0 ), // Floor spread
		/* 24 */ new Gib( kGibFXShockGibs3, 2, null, 0, 0 ), // Tesla
		/* 25 */ new Gib( kGibFXFlames1, 1, null, 0, 0 ), 
		/* 26 */ new Gib( kGibFXFlames2, 1, null, 0, 0 ), 
		/* 27 */ new Gib( null, 0, kThinkFXZombie, 1, 0 ),
		/* 28 */ new Gib( null, 0, kThinkFXMime, 6, 0 ),
		/* 29 */ new Gib( null, 0, kThinkFXHound, 4, 0 ),
		/* 30 */ new Gib( null, 0, kThinkFXGargoyle, 5, 0 )
	};

	
	public static Vector3 startVel = new Vector3();
	public static Vector3 startPos = new Vector3();
	
	public static void actGenerateGibs( SPRITE pSprite, int nGibType, Vector3 startPos, Vector3 startVel ) {
		if(pSprite == null) game.dassert("pSprite != null");
		if(!(nGibType >= 0 && nGibType < kGibMax))
			game.dassert("nGibType >= 0 && nGibType < kGibMax");

		if(pSprite.sectnum >= 0 && pSprite.sectnum < numsectors) {
			Gib pGib = gGIBInfo[nGibType];
		    for (int nGib = 0; nGib < pGib.nGibFx; nGib++ )
		    {
		    	GIBFX pGibFX = pGib.kGibFX[nGib];
		    	if(pGibFX.Chance <= 0)
		    		game.dassert("pGibFX.Chance > 0");

		    	actGenerateGibFX(pSprite, pGibFX, startPos, startVel);
		    } 
		    for (int nThink = 0; nThink < pGib.nThinkFX; nThink++ )
		    {
		    	THINKFX pGibThing = pGib.kThinkFX[nThink];
		    	if(pGibThing.Chance <= 0)
		    		game.dassert("pGibThing.Chance > 0");
		    	actGenerateThinkFX(pSprite, pGibThing, startPos, startVel);
		    }
		}
	}
	
	public static void actGenerateGibFX( SPRITE pSprite, GIBFX pGibFX, Vector3 startPos, Vector3 startVel) {
		if ( !cfg.gParentalLock || pGameInfo.nGameType > 0 || pGibFX.nType != 13 )
		{
			int x = pSprite.x;
		    int y = pSprite.y;
		    int z = pSprite.z;
		    
		    if ( startPos != null )
		    {
		    	x = (int)startPos.x;
		    	y = (int)startPos.y;
		    	z = (int)startPos.z;
		    }  
		    engine.getzsofslope(pSprite.sectnum, x, y,zofslope);
		    int quantity = gibCalcQuantity(pGibFX.Chance, pGibFX.Quantity);
		    int fz = zofslope[FLOOR] - z;
		    int cz = z - zofslope[CEIL];
		    GetSpriteExtents(pSprite);

		    for(int i = 0; i < quantity; i++) {
		    	if ( startPos == null && (pSprite.cstat & kSpriteRMask) == 0 ) {
			    	int nAngle = Random(2048);
			    	x = pSprite.x + mulscale(Cos(nAngle), pSprite.clipdist << 2, 30);
			        y = pSprite.y + mulscale(Sin(nAngle), pSprite.clipdist << 2, 30);
			        z = extents_zBot - Random(extents_zBot - extents_zTop);
			    }

		    	SPRITE pSpawn = actSpawnEffect(pGibFX.nType, pSprite.sectnum, x, y, z, 0);
		    	if( pSpawn != null) {
		    		if ( pSpawn.pal < 0 )
						pSpawn.pal = pSprite.pal;
		    		if( startVel != null ) {
			    		sprXVel[pSpawn.xvel] = BiRandom(pGibFX.Velocity) + (int)startVel.x / 2;
			    		sprYVel[pSpawn.xvel] = BiRandom(pGibFX.Velocity) + (int)startVel.y / 2;
			    		sprZVel[pSpawn.xvel] = (int)startVel.z - Random(pGibFX.zVelocity);
		    		} else {
		    			int vel = divscale(pGibFX.Velocity, 120, 18);
		    			int zvel = divscale(pGibFX.zVelocity, 120, 18);
		    			
		    			sprXVel[pSpawn.xvel] = BiRandom(vel);
		    			sprYVel[pSpawn.xvel] = BiRandom(vel);
	
		    			if ( (pSprite.cstat & kSpriteRMask) == kSpriteWall ) {
		    				sprZVel[pSpawn.xvel] = BiRandom(zvel);
		    			} 
		    			else if (fz <= cz || cz >= 16384) {
		    				if (fz >= cz || fz >= 16384) {
		    					if( zvel >= 0) {
		    						sprZVel[pSpawn.xvel] = BiRandom(zvel);
		    					} else {
		    						zvel = divscale(klabs(pGibFX.zVelocity), 120, 18);
		    						sprZVel[pSpawn.xvel] = -Random(zvel); 
		    					}
		    				} else { 
		    					zvel = divscale(klabs(pGibFX.zVelocity), 120, 18);
		    					sprZVel[pSpawn.xvel] = -Random(zvel); 
		    				}
		    			} else sprZVel[pSpawn.xvel] = 0;
		    		}
		    	}
		    }
		}
	}
	
	public static void actGenerateThinkFX( SPRITE pSprite, THINKFX pGibThing, Vector3 startPos, Vector3 startVel) {
		int x, y, z;
		
		if ( !cfg.gParentalLock || pGameInfo.nGameType > 0 || pGibThing.nType < 425 || (pGibThing.nType > 425 && pGibThing.nType != 427) )
		{
			if( pGibThing.Chance == 65536 || Chance(pGibThing.Chance >> 1) ) {
				
				GetSpriteExtents(pSprite);
			    if ( startPos != null )
			    {
			    	x = (int)startPos.x;
			    	y = (int)startPos.y;
			    	z = (int)startPos.z;
			    }  
			    else 
			    {
			    	int nAngle = Random(2048);
			    	x = pSprite.x + mulscale(Cos(nAngle), pSprite.clipdist << 2, 30);
			        y = pSprite.y + mulscale(Sin(nAngle), pSprite.clipdist << 2, 30);
			        z = extents_zBot - Random(extents_zBot - extents_zTop);
			    }
			    
			    engine.getzsofslope(pSprite.sectnum, x, y,zofslope);
			    int fz = zofslope[FLOOR] - z;
			    int cz = z - zofslope[CEIL];

			    SPRITE pSpawn = actSpawnThing(pSprite.sectnum, x, y, z, pGibThing.nType);
			    if(pSpawn == null)
			    	game.dassert("pSpawn != null");
			    if ( pGibThing.nTile > -1 )
			    	pSpawn.picnum = (short) pGibThing.nTile;

	    		if( startVel != null ) {
		    		sprXVel[pSpawn.xvel] = BiRandom(pGibThing.Velocity) + (int)startVel.x;
		    		sprYVel[pSpawn.xvel] = BiRandom(pGibThing.Velocity) + (int)startVel.y;
		    		sprZVel[pSpawn.xvel] = (int)startVel.z - Random(pGibThing.zVelocity);
	    		} else {
	    			int vel = divscale(pGibThing.Velocity, 120, 18);
	    			int zvel = divscale(pGibThing.zVelocity, 120, 18);
	    			
	    			sprXVel[pSpawn.xvel] = BiRandom(vel);
	    			sprYVel[pSpawn.xvel] = BiRandom(vel);
	    			if ( (pSprite.cstat & kSpriteRMask) == kSpriteWall ) {
	    				sprZVel[pSpawn.xvel] = BiRandom(divscale(pGibThing.zVelocity, 120, 18));
	    			} else 
	    			if (fz <= cz || cz >= 16384) {
	    				if (fz >= cz || fz >= 16384) {
	    					sprZVel[pSpawn.xvel] = BiRandom(zvel);
	    				} else sprZVel[pSpawn.xvel] = -Random(zvel);
	    			} else sprZVel[pSpawn.xvel] = 0;
	    		}
		    }
		}
	}
	
	public static int gibCalcQuantity(int Chance, int Quantity)
	{
		int out = Quantity;
		if ( Chance < 65536 )
		{
		    for ( int i = 0; i < Quantity; i++ )
		    {
		    	if ( !Chance(Chance / 2) )
		    		--out;
		    }
		}
	 	return out;
	}

	public static void walGenerateGib(int nWall, int nGibType, Vector3 startVel) {
		if(!(nWall >= 0 && nWall < numwalls))
		game.dassert("nWall >= 0 && nWall < numwalls");
		if(!(nGibType >= 0 && nGibType < kGibMax))
		game.dassert("nGibType >= 0 && nGibType < kGibMax");

		WALL pWall = wall[nWall];
		int cx = (wall[pWall.point2].x + pWall.x) / 2;
		int cy = (wall[pWall.point2].y + pWall.y) / 2;
		int nSector = engine.sectorofwall((short)nWall);
		engine.getzsofslope((short)nSector, cx, cy,zofslope);
		int floorz = zofslope[FLOOR];
		int ceilz = zofslope[CEIL];
		if(pWall.nextsector != -1)
			engine.getzsofslope(pWall.nextsector, cx, cy,zofslope);
		int nextfloorz = zofslope[FLOOR];
		int nextceilz = zofslope[CEIL];

		int cz = Math.max(ceilz, nextceilz);
		int fz = Math.min(floorz, nextfloorz);

		int dx = wall[pWall.point2].x - pWall.x;
		int dy = wall[pWall.point2].y - pWall.y;
		int dz = fz - cz;
		Gib pGib = gGIBInfo[nGibType];
		sfxCreate3DSound(cx, cy, (fz + cz) >> 1, pGib.nSoundId, nSector);
		for (int nGib = 0; nGib < pGib.nGibFx; nGib++ )
	    {
	    	GIBFX pGibFX = pGib.kGibFX[nGib];
	    	if(pGibFX.Chance <= 0)
	    	game.dassert("pGibFX.Chance > 0");
	    	walGenerateGibFX(nWall, pGibFX, cz, dx, dy, dz, startVel);
	    }
	}
	
	public static void walGenerateGibFX(int nWall, GIBFX pGibFX, int cz, int dx, int dy, int dz, Vector3 startVel) {
		int wallx, wally, wallz;
		if(!(nWall >= 0 && nWall < numwalls))
		game.dassert("nWall >= 0 && nWall < numwalls");
		WALL pWall = wall[nWall];
		int nSector = engine.sectorofwall((short)nWall);
		
		int quantity = gibCalcQuantity(pGibFX.Chance, pGibFX.Quantity);
		for(int i = 0; i < quantity; i++) {
			wallz = cz + Random(dz);
			wally = pWall.y + Random(dy);
			wallx = pWall.x + Random(dx);

			SPRITE pSpawn = actSpawnEffect(pGibFX.nType, (short) nSector, wallx, wally, wallz, 0);

			if( pSpawn != null) {
				if ( pSpawn.pal < 0 )
					pSpawn.pal = (byte) pWall.pal;
				
				if( startVel != null ) {
					sprXVel[pSpawn.xvel] = BiRandom(divscale((int)startVel.x, 120, 18));
					sprYVel[pSpawn.xvel] = BiRandom(divscale((int)startVel.y, 120, 18));
					sprZVel[pSpawn.xvel] = -Random(divscale((int)startVel.z, 120, 18));
				} else {
					sprXVel[pSpawn.xvel] = BiRandom(divscale(pGibFX.Velocity, 120, 18));
					sprYVel[pSpawn.xvel] = BiRandom(divscale(pGibFX.Velocity, 120, 18));
					sprZVel[pSpawn.xvel] = -Random(divscale(pGibFX.zVelocity, 120, 18));
				}
			}
		}
	}
}

class THINKFX {
	int nType;
	int nTile;
	int Chance;
	int Velocity;
	int zVelocity;
	
	public THINKFX(int nType, int nTile, int Chance, int Velocity, int zVelocity) {
		this.nTile = nTile;
		this.nType = nType;
		this.Chance = Chance;
		this.Velocity = Velocity;
		this.zVelocity = zVelocity;
	}
}

class GIBFX {
	int nType;
	int Chance;
	int Quantity;
	int Velocity;
	int zVelocity;
	
	public GIBFX(int nType, int pad, int Chance, int Quantity, int Velocity, int zVelocity) {
		this.nType = nType;
		this.Quantity = Quantity;
		this.Chance = Chance;
		this.Velocity = Velocity;
		this.zVelocity = zVelocity;
	}
}