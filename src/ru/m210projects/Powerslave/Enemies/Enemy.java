// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Enemies;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Random.*;

import java.nio.ByteBuffer;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;

import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Light.*;

public class Enemy {

	public static class EnemyStruct {
		public static final int size = 16;
		
		public short nHealth;
		public short nSeq;
		public short nState;
		public short nSprite;
		public short nTarget;
		public short field_A;
		public short field_C;
		public short nFunc = -1;
		
		public void save(ByteBuffer bb)
		{
			bb.putShort(nHealth);
			bb.putShort(nSeq);
			bb.putShort(nState);
			bb.putShort(nSprite);
			bb.putShort(nTarget);
			bb.putShort(field_A);
			bb.putShort(field_C);
			bb.putShort(nFunc);
		}
		
		public void load(Resource bb)
		{
			nHealth = bb.readShort();
			nSeq = bb.readShort();
			nState = bb.readShort();
			nSprite = bb.readShort();
			nTarget = bb.readShort();
			field_A = bb.readShort();
			field_C = bb.readShort();
			nFunc = bb.readShort();
		}
		
		public EnemyStruct copy(EnemyStruct src)
		{
			nHealth = src.nHealth;
			nSeq = src.nSeq;
			nState = src.nState;
			nSprite = src.nSprite;
			nTarget = src.nTarget;
			field_A = src.field_A;
			field_C = src.field_C;
			nFunc = src.nFunc;
			
			return this;
		}
	}

	public static int MoveCreature(int nSprite) {
		return engine.movesprite((short) nSprite, sprite[nSprite].xvel << 8, sprite[nSprite].yvel << 8,
				sprite[nSprite].zvel, 15360, -5120, 0);
	}

	public static int MoveCreatureWithCaution(int nSprite) {
		int x = sprite[nSprite].x;
		int y = sprite[nSprite].y;
		int z = sprite[nSprite].z;

		short nOldSector = sprite[nSprite].sectnum;
		int hitMove = MoveCreature(nSprite);
		int nSector = sprite[nSprite].sectnum;
		if (nSector != nOldSector) {
			if (klabs(sector[nOldSector].floorz - sector[nSector].floorz) > 15360 || (SectFlag[nSector] & 0x2000) != 0
					|| SectBelow[nSector] > -1 && SectFlag[SectBelow[nSector]] != 0 || SectDamage[nSector] != 0) {

				sprite[nSprite].x = x;
				sprite[nSprite].y = y;
				sprite[nSprite].z = z;
				engine.mychangespritesect((short) nSprite, nOldSector);

				sprite[nSprite].ang = (short) ((sprite[nSprite].ang + 256) & 0x7FF);
				sprite[nSprite].xvel = (short) (sintable[(sprite[nSprite].ang + 512) & 0x7FF] >> 2);
				sprite[nSprite].yvel = (short) (sintable[sprite[nSprite].ang & 0x7FF] >> 2);

				return 0;
			}
		}

		return hitMove;
	}

	public static void DropMagic(int nSprite) {
		if (lFinaleStart == 0 && --nMagicCount <= 0) {
			int v1 = BuildAnim(-1, 64, 0, sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z,
					sprite[nSprite].sectnum, 0x30, 4);
			int spr = GetAnimSprite(v1);

			sprite[spr].owner = (short) v1;
			AddFlash(sprite[spr].sectnum, sprite[spr].x, sprite[spr].y, sprite[spr].z, 128);
			engine.changespritestat((short) spr, (short) 950);
			nMagicCount = RandomSize(2);
		}
	}

	public static int UpdateEnemy(int nTarget) {
		if (nTarget >= 0) {
			if ((sprite[nTarget].cstat & 0x101) == 0)
				nTarget = -1;
			return nTarget;
		}

		return -1;
	}
	
	public static int PlotCourseToSprite(int a1, int a2) {
		if (a1 >= 0 && a2 >= 0) {
			SPRITE v3 = sprite[a1];
			SPRITE v4 = sprite[a2];

			int dx = v4.x - v3.x;
			int dy = v4.y - v3.y;
			v3.ang = engine.GetMyAngle(dx, dy);
			return engine.ksqrt(dx * dx + dy * dy);
		}

		return -1;
	}

	public static int FindPlayer(int nDude, int dist) {
		boolean bCourse = true;
		if (nDude < 0) {
			nDude = -nDude;
			bCourse = false;
		}

		if (dist < 0)
			dist = 100;
		dist <<= 8;

		SPRITE pDude = sprite[nDude];
		int dz = pDude.z - GetSpriteHeight(nDude);

		for (int i = 0; i < numplayers; i++) {
			SPRITE pPlayer = sprite[PlayerList[i].spriteId];
			if ((pPlayer.cstat & 257) != 0 && (pPlayer.cstat & 0x8000) == 0) {
				if (klabs(pPlayer.x - pDude.x) < dist && klabs(pPlayer.y - pDude.y) < dist && engine.cansee(pPlayer.x,
						pPlayer.y, pPlayer.z - 7680, pPlayer.sectnum, pDude.x, pDude.y, dz, pDude.sectnum)) {
					if (bCourse)
						PlotCourseToSprite(nDude, PlayerList[i].spriteId);
					return PlayerList[i].spriteId;
				}
			}
		}

		return -1;
	}

}
