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

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.View.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Type.StatusAnim.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Grenade.*;
import static ru.m210projects.Powerslave.Enemies.Ra.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile.AnimType;
import ru.m210projects.Powerslave.Main.UserFlag;

public class Seq {

	private static short nGunPicnum[] = { 488, 490, 491, 878, 899, 3455 };

	public static final int SEQMAX = 4096;
	public static int sequences;
	public static short[] SeqBase = new short[SEQMAX];
	public static short[] SeqSize = new short[SEQMAX];
	public static short[] SeqFlag = new short[SEQMAX];

	public static int[] SeqOffsets = new int[78];

	public static final int FRAMEMAX = 18000;
	public static int frames;
	public static short[] FrameBase = new short[FRAMEMAX];
	public static short[] FrameSize = new short[FRAMEMAX];
	public static short[] FrameFlag = new short[FRAMEMAX]; // 4(shade - 100) 16 64 128
	public static short[] FrameSound = new short[FRAMEMAX];

	public static final int CHUNKMAX = 21000;
	public static int chunks;
	public static short[] ChunkXpos = new short[CHUNKMAX];
	public static short[] ChunkYpos = new short[CHUNKMAX];
	public static short[] ChunkPict = new short[CHUNKMAX];
	public static short[] ChunkFlag = new short[CHUNKMAX];

	public static int centerx;
	public static int centery;

	public static void LoadSequences() {
		String SeqNames[] = { "rothands.seq", "sword.seq", "pistol.seq", "m_60.seq", "flamer.seq", "grenade.seq",
				"cobra.seq", "bonesaw.seq", "scramble.seq", "glove.seq", "mummy.seq", "skull.seq", "poof.seq",
				"kapow.seq", "fireball.seq", "bubble.seq", "spider.seq", "anubis.seq", "anuball.seq", "fish.seq",
				"snakehed.seq", "snakbody.seq", "wasp.seq", "cobrapow.seq", "scorp.seq", "joe.seq", "status.seq",
				"dead.seq", "deadex.seq", "anupoof.seq", "skulpoof.seq", "bullet.seq", "shadow.seq", "grenroll.seq",
				"grenboom.seq", "splash.seq", "grenpow.seq", "skulstrt.seq", "firepoof.seq", "bloodhit.seq", "lion.seq",
				"items.seq", "lavag.seq", "lsplash.seq", "lavashot.seq", "smokebal.seq", "firepot.seq", "rex.seq",
				"set.seq", "queen.seq", "roach.seq", "hawk.seq", "setghost.seq", "setgblow.seq", "bizztail.seq",
				"bizzpoof.seq", "queenegg.seq", "roacshot.seq", "backgrnd.seq", "screens.seq", "arrow.seq", "fonts.seq",
				"drips.seq", "firetrap.seq", "magic2.seq", "creepy.seq", "slider.seq", "ravolt.seq", "eyehit.seq",
				"font2.seq", "seebubbl.seq", "blood.seq", "drum.seq", "poof2.seq", "deadbrn.seq", "grenbubb.seq",
				"rochfire.seq", "rat.seq" };

		for (int i = 0; i < 78; i++) {
			SeqOffsets[i] = sequences;
			if (!ReadSequence(SeqNames[i]))
				Console.Println("Error loading " + SeqNames[i]);
		}

		nShadowPic = GetFirstSeqPicnum(32);
		nShadowWidth = engine.getTile(nShadowPic).getWidth();
		nFlameHeight = engine.getTile(GetFirstSeqPicnum(38)).getHeight(); // firepoof
		nBackgroundPic = GetFirstSeqPicnum(58); // backgrnd

		nPilotLightBase = SeqBase[SeqOffsets[4] + 3]; // flamer
		nPilotLightCount = SeqSize[SeqOffsets[4] + 3];
		nPilotLightFrame = 0;
		nFontFirstChar = GetFirstSeqPicnum(69); // font2
		for (int i = 0; i < SeqSize[SeqOffsets[69]]; i++) {
			engine.getTile(i + nFontFirstChar).anm &= 0xFF0000FF;
		}

		InitStatus();
	}

	public static short GetSeqPicnum(int seq, int offset, int a3) {
		if((SeqBase[SeqOffsets[seq] + offset] + a3) == -1) return 0;
		return ChunkPict[FrameBase[SeqBase[SeqOffsets[seq] + offset] + a3]];
	}

	public static int GetSeqPicnum2(int seq, int a2) {
		if((SeqBase[seq] + a2) == -1) return 0;
		return ChunkPict[FrameBase[SeqBase[seq] + a2]];
	}

	public static int GetFirstSeqPicnum(int seq) {
		if(SeqBase[seq] == -1) return 0;
		return ChunkPict[FrameBase[SeqBase[SeqOffsets[seq]]]];
	}

	public static void DrawStatusSequence(int seq, int frameOffs, int yOffset) {
		int v3 = (SeqBase[seq] + frameOffs);
		if(v3 == -1) return;

		int chunkNum = FrameBase[v3];
		int count = FrameSize[v3];

		while (--count >= 0) {
			int ang = 0;
			int stat = 2 | 8;
			int orientation = ChunkFlag[chunkNum];
			int x = ChunkXpos[chunkNum] + 160;
			int y = ChunkYpos[chunkNum] + 100 + yOffset;

			if ((orientation & 3) != 0) {
				if ((orientation & 1) != 0)
					ang = 1024;
				if (orientation != 3)
					stat |= 4;
			}

			engine.rotatesprite(x << 16, y << 16, 65536, ang, ChunkPict[chunkNum++], 0, 0, stat, 0, 0, xdim, ydim);
		}
	}

	public static void DrawCustomSequence(int baseSeq, int frameOffs, int xoffs, int yoffs, int shade, int pal,
			int dastat) {
		if (baseSeq == -1)
			return;

		int v3 = (SeqBase[baseSeq] + frameOffs);
		if(v3 == -1)
			return;

		int chunkNum = FrameBase[v3];
		int count = FrameSize[v3];

		while (--count >= 0) {
			int ang = 0;
			int stat = 2 | 8 | dastat;
			int orientation = ChunkFlag[chunkNum];
			int x = ChunkXpos[chunkNum] + 160 + xoffs;
			int y = ChunkYpos[chunkNum] + 100 + yoffs;

			if ((orientation & 3) != 0) {
				if ((orientation & 1) != 0)
					ang = 1024;
				if (orientation != 3)
					stat |= 4;
			}

			engine.rotatesprite(x << 16, y << 16, 65536, ang, ChunkPict[chunkNum++], shade, pal, stat, 0, 0, xdim,
					ydim);
		}
	}

	public static void DrawGunSequence(int baseSeq, int frameOffs, int xoffs, int yoffs, int shade, int pal, int extstat) {
		if (baseSeq == -1)
			return;

		int v3 = (SeqBase[baseSeq] + frameOffs);
		if(v3 == -1) return;

		int chunkNum = FrameBase[v3];
		int count = FrameSize[v3];
		int flags = FrameFlag[v3];

		while (--count >= 0) {
			int ang = 0;
			int stat = 2 | 8 | extstat;
			int orientation = ChunkFlag[chunkNum];
			int x = ChunkXpos[chunkNum] + 160 + xoffs;
			int y = ChunkYpos[chunkNum] + 100 + yoffs;

			if ((orientation & 3) != 0) {
				if ((orientation & 1) != 0)
					ang = 1024;
				if (orientation != 3)
					stat |= 4;
			}

			if ((flags & 4) != 0)
				shade -= 100;

			if (nPlayerInvisible[nLocalPlayer] != 0)
				stat |= 33;

			engine.rotatesprite(x << 16, y << 16, 65536, ang, ChunkPict[chunkNum++], shade, pal, stat, 0, 0, xdim,
					ydim);
		}
	}

	public static void PlotArrowSequence(int nSprite, int baseSeq, short frameOffs) {
		int ang = ((tsprite[nSprite].ang + 512
				- engine.GetMyAngle(nCamerax - tsprite[nSprite].x, nCameray - tsprite[nSprite].y) + 128) & 0x7FF) >> 8;
		int seq = SeqBase[baseSeq + ang] + frameOffs;
		if(seq == -1) return;
		int frm = FrameBase[seq];

		int cstat = tsprite[nSprite].cstat | 0x80;
		byte shade = tsprite[nSprite].shade;

		if ((ang & 3) != 0)
			cstat |= 0x18;
		else
			cstat &= ~0x18;
		if ((FrameFlag[seq] & 4) != 0)
			shade -= 100;

		tsprite[nSprite].cstat = (short) cstat;
		tsprite[nSprite].shade = shade;
		tsprite[nSprite].statnum = FrameSize[seq];
		if ((ChunkFlag[frm] & 1) != 0) {
			tsprite[nSprite].xoffset = ChunkXpos[frm];
			tsprite[nSprite].cstat |= 4;
		} else
			tsprite[nSprite].xoffset = (short) -ChunkXpos[frm];

		tsprite[nSprite].yoffset = (short) -ChunkYpos[frm];
		tsprite[nSprite].picnum = ChunkPict[frm];
	}

	public static void PlotSequence(int a1, int a2, int a3, int a4) {
		SPRITE pTSprite = tsprite[(short) (a1 & 0xFFFF)];

		int dang = engine.GetMyAngle(nCamerax - pTSprite.x, nCameray - pTSprite.y);
		int nShade = pTSprite.shade;
		if((SeqBase[a2] + a3) == -1) return;
		if ((FrameFlag[SeqBase[a2] + a3] & 4) != 0)
			nShade -= 100;

		int v8 = ((a4 & 1) == 0) ? ((((pTSprite.ang) - dang + 128) & 0x7FF) >> 8) : 0;
		int seq = (SeqBase[v8 + a2] + a3);
		int frm = FrameBase[seq];
		int frmSize = FrameSize[seq];
		int nStatnum = 100;
		if ((a4 & 0x100) != 0)
			nStatnum = -3;

		int nOwner = pTSprite.owner;
		int nTSprite = spritesortcnt;
		int nTile = ChunkPict[frm];
		int xoffs = 0, yoffs = 0;
		for (int i = frmSize; i > 0; i--) {
			if (tsprite[nTSprite] == null)
				tsprite[nTSprite] = new SPRITE();

			xoffs = mulscale(frmSize - i, sintable[(dang + 512) & 0x7FF], 10);
			yoffs = mulscale(frmSize - i, sintable[dang & 0x7FF], 10);

			tsprite[nTSprite].x = pTSprite.x + xoffs;
			tsprite[nTSprite].y = pTSprite.y + yoffs;
			tsprite[nTSprite].z = pTSprite.z;
			tsprite[nTSprite].shade = (byte) nShade;
			tsprite[nTSprite].pal = pTSprite.pal;
			tsprite[nTSprite].xrepeat = pTSprite.xrepeat;
			tsprite[nTSprite].yrepeat = pTSprite.yrepeat;
			tsprite[nTSprite].ang = (short) (ChunkPict[frm] == 736 ? dang : pTSprite.ang);
			tsprite[nTSprite].owner = pTSprite.owner;
			tsprite[nTSprite].sectnum = pTSprite.sectnum;
			tsprite[nTSprite].cstat = (short) (pTSprite.cstat | 0x80);
			tsprite[nTSprite].statnum = (short) (i + nStatnum + 1);
			if ((ChunkFlag[frm] & 1) != 0) {
				tsprite[nTSprite].xoffset = ChunkXpos[frm];
				tsprite[nTSprite].cstat |= 4;
			} else
				tsprite[nTSprite].xoffset = (short) -ChunkXpos[frm];
			tsprite[nTSprite].yoffset = (short) -ChunkYpos[frm];
			tsprite[nTSprite].picnum = ChunkPict[frm];
			spritesortcnt++;
			nTSprite++;
			frm++;
		}

		if ((pTSprite.cstat & 0x101) != 0 && (sprite[nOwner].statnum != 100 || nNetPlayerCount == 0)) {
			int fz = sector[pTSprite.sectnum].floorz;
			if (fz > initz + PlayerList[nLocalPlayer].eyelevel) {


				if(!cfg.bNewShadows) {
					short siz = (short) Math.max(((32 * engine.getTile(nTile).getWidth() / nShadowWidth) - ((fz - pTSprite.z) >> 10)), 1);
					pTSprite.picnum = (short) nShadowPic;
					pTSprite.cstat = 2 | 32;
					pTSprite.xrepeat = pTSprite.yrepeat = siz;
				}
				else {
					pTSprite.x -= mulscale(sintable[(dang + 512)& 2047], 100, 16);
					pTSprite.y += mulscale(sintable[(dang + 1024) & 2047], 100, 16);

					short siz = (short) Math.max((48 - ((fz - pTSprite.z) >> 10)), 1);
					pTSprite.picnum = (short) nTile;
					pTSprite.xrepeat = siz;
					pTSprite.yrepeat = (short) ( pTSprite.yrepeat>>3 );
	                if(pTSprite.yrepeat < 4) pTSprite.yrepeat = 4;

	                pTSprite.shade = 127;
	                pTSprite.cstat |= 2;
				}

				pTSprite.z = fz + 1;
				pTSprite.statnum = -3;
                pTSprite.pal = 0;
				return;
			}
		}

		pTSprite.owner = -1;
	}

	public static void StartDeathSeq(int player, int seq) {
		FreeRa(player);
		PlayerList[player].HealthAmount = 0;
		short nSprite = PlayerList[player].spriteId;
		int lotag = sector[sprite[nSprite].sectnum].lotag;
		if (lotag > 0)
			SignalRun((lotag - 1), nEvent6 | player);

		if (nPlayerGrenade[player] < 0) {
			if (nNetPlayerCount != 0) {
				if (PlayerList[player].currentWeapon > 0 && PlayerList[player].currentWeapon <= 6) {
					short sect = sprite[nSprite].sectnum;
					if (SectBelow[sect] > -1)
						sect = SectBelow[sect];
					short spr = GrabBodyGunSprite();

					engine.changespritesect(spr, sect);

					sprite[spr].x = sprite[nSprite].x;
					sprite[spr].y = sprite[nSprite].y;
					sprite[spr].z = sector[sect].floorz - 512;
					engine.changespritestat(spr, (short) (nActionEyeLevel[PlayerList[player].currentWeapon + 20] + 900));
					sprite[spr].picnum = nGunPicnum[PlayerList[player].currentWeapon - 1];
					BuildItemAnim(spr);
				}
			}
		} else {
			ThrowGrenade(player, 0, 0, 0, -10000);
		}

		StopFiringWeapon(player);
		PlayerList[player].horiz = 92;
		PlayerList[player].eyelevel = -14080;
		nPlayerInvisible[player] = 0;
		dVertPan[player] = 15;
		sprite[nSprite].cstat &= ~0x8101;
		SetNewWeaponImmediate(player, -2);
		if (SectDamage[sprite[nSprite].sectnum] <= 0)
			nDeathType[player] = (short) seq;
		else
			nDeathType[player] = 2;

		if (seq != 0 || (SectFlag[sprite[nSprite].sectnum] & 0x2000) == 0)
			PlayerList[player].anim_ = (short) (2 * seq + 17);
		else
			PlayerList[player].anim_ = 16;
		PlayerList[player].animCount = 0;

		if (numplayers == 1) {
			if (nPlayerLives[player] > 0)
				BuildStatusAnim(3 * (nPlayerLives[player] - 1) + 7, 0);
			if (levelnum > 0 || mUserFlag == UserFlag.UserMap)
				nPlayerLives[player]--;

			if (nPlayerLives[player] < 0)
				nPlayerLives[player] = 0;
		}
		totalvel[player] = 0;
	}

	public static void DrawPilotLightSeq(int x, int y) {
		if ((SectFlag[nPlayerViewSect[nLocalPlayer]] & 0x2000) == 0) {
			int v3 = nPilotLightBase + nPilotLightFrame;
			int seq = FrameBase[v3];
			int count = FrameSize[v3];
			while (--count >= 0) {
				engine.rotatesprite((x + 160 + ChunkXpos[seq]) << 16, (y + 100 + ChunkYpos[seq]) << 16, 65536,
						-8 * (int)sPlayerInput[nLocalPlayer].avel, ChunkPict[seq], -127, 1, 2 | 8, 0, 0, xdim, ydim);
				seq++;
			}
		}
	}

	public static boolean ReadSequence(String name) {
		int sequence = 0;
		Resource bb = BuildGdx.cache.open(name, 1);
		if (bb == null) {
			System.err.println("Unable to open " + name);
			return false;
		}

		String sign = bb.readString(2); // DS or IH

		centerx = bb.readShort();
		centery = bb.readShort();
		int nSeqs = bb.readShort();

		if (nSeqs <= 0 || nSeqs + sequences >= SEQMAX) {
			System.err.println("Not enough sequences available!  Increase array");
			bb.close();
			return false;
		}

		for (int i = 0; i < nSeqs; i++)
			SeqBase[i + sequences] = bb.readShort();
		for (int i = 0; i < nSeqs; i++)
			SeqSize[i + sequences] = bb.readShort();
		for (int i = 0; i < nSeqs; i++)
			SeqFlag[i + sequences] = bb.readShort();
		for (int i = 0; i < nSeqs; i++)
			SeqBase[i + sequences] += frames;
		int oldSeqFrameOffset = frames;

		int nFrames = bb.readShort(); // 778
		if (nFrames <= 0 || frames + nFrames >= FRAMEMAX) {
			System.err.println("Not enough frames available!  Increase FRAMEMAX");
			bb.close();
			return false;
		}

		for (int i = 0; i < nFrames; i++)
			FrameBase[i + frames] = bb.readShort();
		for (int i = 0; i < nFrames; i++)
			FrameSize[i + frames] = bb.readShort();
		for (int i = 0; i < nFrames; i++)
			FrameFlag[i + frames] = bb.readShort();
		for (int i = 0; i < nFrames; i++)
			FrameSound[i + frames] = -1;

		for (int i = 0; i < nFrames; i++)
			FrameBase[i + frames] += chunks;

		int nChunks = bb.readShort(); // 1043
		if (nChunks < 0 || chunks + nChunks >= CHUNKMAX) {
			System.err.println("Not enough chunks available!  Increase CHUNKMAX");
			bb.close();
			return false;
		}

		for (int i = 0; i < nChunks; i++)
			ChunkXpos[i + chunks] = bb.readShort();
		for (int i = 0; i < nChunks; i++)
			ChunkYpos[i + chunks] = bb.readShort();
		for (int i = 0; i < nChunks; i++)
			ChunkPict[i + chunks] = bb.readShort();

		for (int i = 0; i < nChunks; i++) {
			if ((engine.getTile(ChunkPict[i + chunks]).getType() != AnimType.None))
				System.err.println(
						"sequence " + sequence + " tile " + ChunkPict[i + chunks] + " has Ken animation attached!");
		}

		for (int i = 0; i < nChunks; i++)
			ChunkFlag[i + chunks] = bb.readShort();

		for (int i = 0; i < nChunks; i++) {
			ChunkXpos[i + chunks] -= centerx;
			ChunkYpos[i + chunks] -= centery;
		}

		sequences += nSeqs;
		frames += nFrames;
		chunks += nChunks;
		SeqBase[sequences] = (short) frames;
		FrameBase[frames] = (short) chunks;

		if (sign.equals("DS")) {
			short numSounds = bb.readShort();

			String[] sounds = new String[20];
			byte[] data = new byte[8];

			for (int i = 0; i < numSounds; i++) {
				bb.read(data);
				sounds[i] = new String(data).trim();
			}

			int v52 = bb.readShort();
			for (int i = 0; i < v52; i++) {
				int index = bb.readShort();
				short v46 = bb.readShort();

				FrameSound[index + oldSeqFrameOffset] = (short) ((v46 & 0xFE00) | LoadSound(sounds[v46 & 0x1FF]));
			}
		}

		bb.close();

		return true;
	}

	public static int GetFrameFlag(int a1, int a2) {
		return FrameFlag[a2 + SeqBase[a1]];
	}

	public static void MoveSequence(int nSprite, int a2, int a3) {
		int v4 = GetFrameSound(a2, a3);
		if (v4 != -1) {
			if (nSprite <= -1)
				PlayLocalSound(v4, 0);
			else
				D3PlayFX(v4, nSprite);
		}
	}

}
