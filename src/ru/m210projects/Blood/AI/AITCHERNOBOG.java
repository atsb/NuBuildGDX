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

package ru.m210projects.Blood.AI;

import static ru.m210projects.Blood.AI.Ai.aiActivateDude;
import static ru.m210projects.Blood.AI.Ai.aiActive;
import static ru.m210projects.Blood.AI.Ai.aiChooseDirection;
import static ru.m210projects.Blood.AI.Ai.aiMoveForward;
import static ru.m210projects.Blood.AI.Ai.aiMoveTurn;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTime;
import static ru.m210projects.Blood.AI.Ai.gDudeSlope;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireMissile;
import static ru.m210projects.Blood.Actor.actSetBurnSource;
import static ru.m210projects.Blood.Actor.kAttrAiming;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.kMissileTchernobog2;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.EVENT.evPostCallback;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle45;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kSeqDudeRecoil;
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trigger.ksprite;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Weapon.kAimMaxSlope;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;

import com.badlogic.gdx.math.Vector3;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.WeaponAim;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AITCHERNOBOG {

	public static AISTATE tchernobogIdle;
	public static AISTATE tchernobogSearch;
	public static AISTATE tchernobogChase;
	public static AISTATE tchernobogDodge;
	public static AISTATE tchernobogRecoil;
	public static AISTATE tchernobogGoto;
	public static AISTATE tchernobogTurn;
	public static AISTATE tchernobogBurn;
	public static AISTATE tchernobogShoot;
	public static AISTATE tchernobogShootTarget;
	
	public static void Init() {
		tchernobogIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				myThinkTarget(sprite, xsprite);
			}
		};
		tchernobogSearch = new AISTATE(Type.search,8, null, 1800, false, true, true, tchernobogIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		tchernobogChase = new AISTATE(Type.other,8, null, 1800, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		tchernobogRecoil = new AISTATE(Type.other,kSeqDudeRecoil, null, 0, false, false, false, tchernobogSearch);
		tchernobogGoto = new AISTATE(Type.tgoto,8, null, 600, false, true, true, tchernobogIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		tchernobogShoot	= new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				ShootCallback(nXSprite);
			}
		}, 60, false, false, false, tchernobogChase );
		tchernobogShootTarget = new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				ShootTargetCallback(nXSprite);
			}
		}, 60, false, false, false, tchernobogChase );
		tchernobogBurn = new AISTATE(Type.other,7, new CALLPROC() {
			public void run(int nXSprite) {
				BurnCallback(nXSprite);
			}
		}, 60, false, false, false, tchernobogChase );
		
		tchernobogTurn = new AISTATE(Type.other,8, null, 60, false, true, false, tchernobogChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
		};
	}
	
	private static final WeaponAim gMissileData = new WeaponAim(65536, 65536, 256, 85, 0x100000);

	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, tchernobogGoto);
			return;
		}
		
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;
		int dx, dy, dist;
		
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;
		
		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));
		
		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			aiNewState(pSprite, pXSprite, tchernobogSearch);
			return;
		}
		
		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, tchernobogSearch);
				return;
			}
		}
		
		dist = (int) engine.qdist(dx, dy);
		
		if ( dist <= pDudeInfo.seeDist ) 
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;
			
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum) )
			{	
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{	
					aiSetTarget(pXSprite, pXSprite.target);
					if ( klabs(losAngle) < kAngle15 )
					{
						if ( dist > 1280 && dist < 2816)
							aiNewState(pSprite, pXSprite, tchernobogShoot);
						else if ( dist > 2816 && dist < 3328)
							aiNewState(pSprite, pXSprite, tchernobogShootTarget);
						else if ( dist > 3328 && dist < 7936)
							aiNewState(pSprite, pXSprite, tchernobogBurn);
					}
					return;
				}
			}
		}
		aiNewState(pSprite, pXSprite, tchernobogGoto);
		pXSprite.target = -1;
	}

	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget(pSprite, pXSprite);
	}

	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;

		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		if ( dist < 512 && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
			aiNewState(pSprite, pXSprite, tchernobogSearch);

		aiThinkTarget(pSprite, pXSprite);
	}

	private static void myThinkTarget( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		if(aiActive[pSprite.xvel] != 0)
		{
			if ( aiThinkTime[pSprite.xvel] >= 10 )
			{
				pXSprite.goalAng += kAngle45;
				Vector3 kSprite = ksprite[pSprite.xvel];
				aiSetTarget(pXSprite, (int)kSprite.x, (int)kSprite.y, (int)kSprite.z);
				aiNewState(pSprite, pXSprite, tchernobogTurn);
				return;
			} else aiThinkTime[pSprite.xvel]++;
		}
		
		if(!Chance(pDudeInfo.alertChance / 2))
			return;
		
		for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
	    {
			PLAYER pPlayer = gPlayer[i];
			
			if (pPlayer.pXsprite.health == 0
			|| powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
				continue;

			int x = pPlayer.pSprite.x;
			int y = pPlayer.pSprite.y;
			int z = pPlayer.pSprite.z;
			short nSector = pPlayer.pSprite.sectnum;

			int dx = x - pSprite.x;
			int dy = y - pSprite.y;

			int dist = (int) engine.qdist(dx, dy);
			
			if ( dist <= pDudeInfo.seeDist || dist <= pDudeInfo.hearDist )
			{
				int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

				// is there a line of sight to the player?
				if ( engine.cansee(x, y, z, nSector, pSprite.x, pSprite.y, pSprite.z - eyeAboveZ,
					pSprite.sectnum) )
				{
					int nAngle = engine.getangle(dx, dy);
					int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
					
					if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
					{
						aiThinkTime[pSprite.xvel] = 0;
						aiSetTarget( pXSprite, pPlayer.nSprite );
						aiActivateDude( pSprite, pXSprite );
						return;
					}
					
					if ( dist < pDudeInfo.hearDist )
					{
						aiThinkTime[pSprite.xvel] = 0;
						aiSetTarget(pXSprite, x, y, z);
						aiActivateDude( pSprite, pXSprite );
						return;
					}
				}
			}
		}
	}

	private static void BurnCallback ( int nXIndex ) {
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		if(pXSprite.target == -1)
			return;
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;
		if(pXTarget == null)
			return;
		
		if ( pXTarget.burnTime == 0 )
			evPostCallback(pXSprite.target, 3, 0, 0);
		
		int burnTime;
		if ( pTarget.statnum == kStatDude )
		burnTime = 2400;
		  else
		burnTime = 1200;	

		pXTarget.burnTime = ClipHigh(pXTarget.burnTime + 40, burnTime);
		pXTarget.burnSource = actSetBurnSource(nSprite);
		
		if ( Chance(12288) )
			aiNewState(pSprite, pXSprite, tchernobogShootTarget);
	}

	private static void ShootCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

		int x = pSprite.x;
		int y = pSprite.y;
		
		int ax = Cos(pSprite.ang) >> 16;
		int x2 = ax;
		int ay = Sin(pSprite.ang) >> 16;
		int y2 = ay;
		int az = gDudeSlope[nXIndex];

		int closest = 0x7FFFFFFF;
		for (int nDude = headspritestat[kStatDude]; nDude >= 0; nDude = nextspritestat[nDude])
		{
			SPRITE pDude = sprite[nDude];
			
			// don't target yourself!
			if ( pDude == pSprite )
				continue;

	    	if ( (pDude.hitag & kAttrAiming) == 0 )
	    		continue;
	        
    		int tx, ty, tz;

			tx = pDude.x;
			ty = pDude.y;
			tz = pDude.z;

			int dist = (int)engine.qdist(tx - x, ty - y);

			if ( dist == 0 || dist > 10240 )
				continue;

			if ( gMissileData.kSeeker != 0 )
			{
                int k = (dist << 12) / gMissileData.kSeeker;
                tx += k * sprXVel[nDude] >> 12;
                ty += k * sprYVel[nDude] >> 12;
                tz += k * sprZVel[nDude] >> 8;
			}

			int z1 = mulscale(dist, gDudeSlope[nXIndex], 10) + eyeAboveZ;
			int z2 = mulscale(kAimMaxSlope, dist, 10);
			
			GetSpriteExtents(pDude);

			if ( (z1 - z2 > extents_zBot) || (z1 + z2 < extents_zTop) )
				continue;

			int dx = mulscale(dist, Cos(pSprite.ang), 30) + x;
            int dy = mulscale(dist, Sin(pSprite.ang), 30) + y;

			int dist2 = engine.ksqrt(
					((dx - tx) >> 4) * ((dx - tx) >> 4) 
					+ ((dy - ty) >> 4) * ((dy - ty) >> 4)
					+ ((z1 - tz) >> 8) * ((z1 - tz) >> 8));
			
			if( dist2 < closest )
			{
				int ang = engine.getangle(tx - x, ty - y);
				if ( klabs(((ang - pSprite.ang + kAngle180) & kAngleMask) - kAngle180) > gMissileData.kDudeAngle )
					continue;
				
				int dz = pDude.z - pSprite.z;

				if(engine.cansee(x, y, eyeAboveZ, pSprite.sectnum, tx, ty, tz, pDude.sectnum) )
				{
					closest = dist2;
	
					ax = Cos(ang) >> 16;
					ay = Sin(ang) >> 16;
					az = divscale(dz, dist, 10);
				} else az = dz;
			}
		}

		actFireMissile(pSprite, 350, 0, ax, ay, -az, kMissileTchernobog2);
		actFireMissile(pSprite, -350, 0, x2, y2, 0, kMissileTchernobog2);
	}
	
	private static void ShootTargetCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

		int x = pSprite.x;
		int y = pSprite.y;
		
		int ax = Cos(pSprite.ang) >> 16;
		int ay = Sin(pSprite.ang) >> 16;
		int az = gDudeSlope[nXIndex];

		int closest = 0x7FFFFFFF;
		for (int nDude = headspritestat[kStatDude]; nDude >= 0; nDude = nextspritestat[nDude])
		{
			SPRITE pDude = sprite[nDude];
			
			// don't target yourself!
			if ( pDude == pSprite )
				continue;

	    	if ( (pDude.hitag & kAttrAiming) == 0 )
	    		continue;
	        
    		int tx, ty, tz;

			tx = pDude.x;
			ty = pDude.y;
			tz = pDude.z;

			int dist = (int)engine.qdist(tx - x, ty - y);

			if ( dist == 0 || dist > 10240 )
				continue;

			if ( gMissileData.kSeeker != 0 )
			{
                int k = (dist << 12) / gMissileData.kSeeker;
                tx += k * sprXVel[nDude] >> 12;
                ty += k * sprYVel[nDude] >> 12;
                tz += k * sprZVel[nDude] >> 8;
			}

			int z1 = mulscale(dist, gDudeSlope[nXIndex], 10) + eyeAboveZ;
			int z2 = mulscale(kAimMaxSlope, dist, 10);
			
			GetSpriteExtents(pDude);

			if ( (z1 - z2 > extents_zBot) || (z1 + z2 < extents_zTop) )
				continue;

			int dx = mulscale(dist, Cos(pSprite.ang), 30) + x;
            int dy = mulscale(dist, Sin(pSprite.ang), 30) + y;

			int dist2 = engine.ksqrt(
					((dx - tx) >> 4) * ((dx - tx) >> 4) 
					+ ((dy - ty) >> 4) * ((dy - ty) >> 4)
					+ ((z1 - tz) >> 8) * ((z1 - tz) >> 8));
			
			if( dist2 < closest )
			{
				int ang = engine.getangle(tx - x, ty - y);
				if ( klabs(((ang - pSprite.ang + kAngle180) & kAngleMask) - kAngle180) > gMissileData.kDudeAngle )
					continue;
				
				int dz = pDude.z - pSprite.z;

				if(engine.cansee(x, y, eyeAboveZ, pSprite.sectnum, tx, ty, tz, pDude.sectnum) )
				{
					closest = dist2;
	
					ax = Cos(ang) >> 16;
					ay = Sin(ang) >> 16;
					az = divscale(dz, dist, 10);
				} else az = dz;
			}
		}

		actFireMissile(pSprite, -350, 0, ax, ay, az, kMissileTchernobog2);
		actFireMissile(pSprite, 350, 0, ax, ay, az, kMissileTchernobog2);
	}

}
