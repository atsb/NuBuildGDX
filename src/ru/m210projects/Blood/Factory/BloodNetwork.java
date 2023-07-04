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

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeMax;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kMaxXSectors;
import static ru.m210projects.Blood.DB.kMaxXSprites;
import static ru.m210projects.Blood.DB.kSectorPath;
import static ru.m210projects.Blood.DB.kSectorRotate;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.NETWORKGAME;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.ClipMove;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.GetWallAngle;
import static ru.m210projects.Blood.Gameutils.GetZRange;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.bRandom;
import static ru.m210projects.Blood.Gameutils.clipm_pnsectnum;
import static ru.m210projects.Blood.Gameutils.clipm_px;
import static ru.m210projects.Blood.Gameutils.clipm_py;
import static ru.m210projects.Blood.Gameutils.clipm_pz;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Gameutils.gz_ceilHit;
import static ru.m210projects.Blood.Gameutils.gz_ceilZ;
import static ru.m210projects.Blood.Gameutils.gz_floorHit;
import static ru.m210projects.Blood.Gameutils.gz_floorZ;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LOADSAVE.gdxSave;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.gGameScreen;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.PLAYER.inventoryCheck;
import static ru.m210projects.Blood.PLAYER.kHorizDownMax;
import static ru.m210projects.Blood.PLAYER.kHorizUpMax;
import static ru.m210projects.Blood.PLAYER.kInventoryJumpBoots;
import static ru.m210projects.Blood.PLAYER.kLookMax;
import static ru.m210projects.Blood.PLAYER.kMoveCrouch;
import static ru.m210projects.Blood.PLAYER.kMoveSwim;
import static ru.m210projects.Blood.PLAYER.kMoveWalk;
import static ru.m210projects.Blood.SOUND.sndStartSample;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.VERSION.GAMEVER;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Warp.gLowerLink;
import static ru.m210projects.Blood.Warp.gUpperLink;
import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.pushmove_sectnum;
import static ru.m210projects.Build.Engine.pushmove_x;
import static ru.m210projects.Build.Engine.pushmove_y;
import static ru.m210projects.Build.Engine.pushmove_z;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.io.File;
import java.util.Arrays;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.PROFILE;
import ru.m210projects.Blood.Menus.MenuNetwork;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.INPUT;
import ru.m210projects.Blood.Types.PLOCATION;
import ru.m210projects.Blood.Types.POSTURE;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SPRITE;

public class BloodNetwork extends BuildNet {

	public final int nNetVersion = 534; //v = 532 in BloodGDX < 0.798

	public final byte	kPacketBroadcast	= 2;
	public final byte	kPacketMessage		= 3;
	public final byte	kPacketSound		= 4;
	public final byte 	kPacketContentRequest = 6;
	public final byte	kPacketChangeTeam   = 8;
	public final byte 	kPacketContentAnswer = 9;

	public byte[] gContentFound = new byte[kMaxPlayers];

	public PROFILE[] gProfile = new PROFILE[kMaxPlayers];
	public PLOCATION[] predictFifo = new PLOCATION[kNetFifoSize];
	public PLOCATION predict = new PLOCATION(), predictOld = new PLOCATION();
	public SPRITE predictSprite = new SPRITE();
	public Main app;

	public BloodNetwork(Main game) {
		super(game);
		this.app = game;
		for (int i = 0; i < kNetFifoSize; i++)
			predictFifo[i] = new PLOCATION();

		for (int i = 0; i < kMaxPlayers; i++) {
			if(i != myconnectindex)
				gProfile[i] = new PROFILE("Player " + i, true, true);
		}

		gProfile[myconnectindex] = new PROFILE(cfg.pName, cfg.gAutoAim, cfg.gSlopeTilt);
		Arrays.fill(gContentFound,  (byte)-1);
	}

	public String getPlayerName(int i)
	{
		return gProfile[i].name;
	}

	@Override
	public NetInput newInstance() {
		return new INPUT();
	}

	@Override
	public int GetPackets(byte[] p, int ptr, int len, final int nPlayer) {
		switch( p[ptr++] )
		{
		case kPacketMessage:
			retransmit(nPlayer, packbuf, len);

			int fromPlayer = p[ptr++];
			int mlen = LittleEndian.getInt(p, ptr);
			int toPlayer = packbuf[6];
			if(p[6] != '/' || toPlayer == 0 || toPlayer >= '1' && toPlayer <= '8' && toPlayer - '1' != myconnectindex)
			{
				String messageFrom = gProfile[fromPlayer].name + " : ";
				viewSetMessage(messageFrom + new String(p, 6, mlen), fromPlayer);
				sndStartSample("DMRADIO.RAW", 128, -1);
			}
			break;
		case kPacketSound:
			retransmit(nPlayer, packbuf,len);
			sndStartSample((p[ptr++]&0xFF) + 4400, 128, 1, false);
			break;
		case kPacketContentAnswer:
			gContentFound[nPlayer] = p[ptr];
			return 1;
		case kPacketContentRequest:
			int pathlen = LittleEndian.getInt(p, ptr); ptr += 4;
			if(pathlen >= p.length - ptr)
				pathlen = p.length - ptr - 1;

			String path = FileUtils.getCorrectPath(new String(p, ptr, pathlen));
			FileEntry fil = BuildGdx.compat.checkFile(path);
			BloodIniFile ini = levelGetEpisode(path);

			boolean found = false;
			if(fil != null || ini != null) {
				MenuNetwork network = (MenuNetwork) app.menu.mMenus[NETWORKGAME];
				if(ini != null) {
					found = true;
					network.setEpisode(ini);
				}
				else if(fil != null && fil.getExtension().equals("map"))
				{
					found = true;
					network.setMap(fil);
				}
			} else {
				Console.Println("Player" + nPlayer + " - " + gProfile[nPlayer].name + " tried to set user content. User content not found!", OSDTEXT_RED);
				Console.Println("Be sure that you have content to same path: " + File.separator + path, OSDTEXT_RED);
				Console.toggle();
			}

			packbuf[0] = kPacketContentAnswer;
			packbuf[1] = found ? (byte) 1 : 0;
			sendpacket(nPlayer, packbuf, 2);
			return 1;

			case kPacketDisconnect:
				return GetDisconnectPacket(p, ptr, len, nPlayer, new DisconnectCallback() {
					@Override
					public void invoke(int nDelete) {
						viewSetMessage(gProfile[nDelete].name + " left the game with " + gPlayer[nDelete].fragCount + " frags.", -1, 7);
						if(game.isCurrentScreen(gGameScreen) && gPlayer[nDelete].pSprite != null) {
							seqKill( SS_SPRITE, gPlayer[nDelete].pSprite.extra );
						    actPostSprite(gPlayer[nDelete].nSprite, kStatFree);
						    if ( nDelete == gViewIndex )
						    	gViewIndex = myconnectindex;
						}
					}
				});

			// master start
			case kPacketMasterStart:
				retransmit(nPlayer, packbuf,len);

				int num = p[ptr++] & 0xFF;
				gProfile[num].autoaim = p[ptr++] == 1;
				gProfile[num].slopetilt = p[ptr++] == 1;
				gProfile[num].skill = p[ptr++];
				gProfile[num].team = p[ptr++];
				gProfile[num].name = new String(p, ptr, 14).trim(); ptr += 14;
				break;

			case kPacketChangeTeam:
				retransmit(nPlayer, packbuf,len);

				int nOther = p[ptr++] & 0xFF;
				gProfile[nOther].team = p[ptr++];
				String message = gProfile[nOther].name + " changed team to ";
				switch(gProfile[nOther].team) {
					case 0: message += "default"; break;
					case 1: message += "blue"; break;
					case 2: message += "red"; break;
				}
				viewSetMessage(message, -1);
				sndStartSample("DMRADIO.RAW", 128, -1);

				break;

			case kPacketLevelStart:
				retransmit(nPlayer, packbuf,len);

				ptr = 5;
				int nCheckVersion = LittleEndian.getInt(p, ptr); ptr += 4;
				short nRFFVersion = LittleEndian.getUByte(p, ptr); ptr++;
				pNetInfo.set(p, ptr);

				if ( nCheckVersion != nNetVersion || GAMEVER != nRFFVersion)
				{
			        game.GameMessage("These versions of Blood cannot play together.");
			        NetDisconnect(myconnectindex);
			        return -1;
				}

				if(WaitForAllPlayers(0))
					gGameScreen.newgame(true, ((MenuNetwork) app.menu.mMenus[NETWORKGAME]).getFile(), pNetInfo.nEpisode, pNetInfo.nLevel, pNetInfo.nDifficulty, pNetInfo.nDifficulty, pNetInfo.nDifficulty, false);

				break;

			case kPacketLogout:
				game.gExit = true;
				break;
		}
		return 0;
	}

	public void SendNetMessage(String message)
	{
		if ( numplayers > 1 )
		{
			packbuf[0] = kPacketMessage;
			packbuf[1] = (byte) myconnectindex;
			int len = message.length();
			LittleEndian.putInt(packbuf, 2, message.length());

			if(len + 6 >= packbuf.length)
				app.dassert("ptr + size < packbuf.length");
			System.arraycopy(message.getBytes(), 0, packbuf, 6, len);
			sendtoall(packbuf, len + 6);
		}
	}

	public void ChangeTeam(int nPlayer, int team)
	{
		if(gProfile[nPlayer].team == team)
			return;

		packbuf[0] = kPacketChangeTeam;
		packbuf[1] =  (byte) nPlayer;
		gProfile[nPlayer].team = team;
		packbuf[2] = (byte) gProfile[nPlayer].team;

		sendtoall(packbuf, 3);
	}

	public void InitProfile(int nPlayer)
	{
		if ( numplayers > 1 )
		{
			Arrays.fill(packbuf, 0, PROFILE.sizeof + 1, (byte)0);
			int ptr = 0;
			packbuf[ptr++] = kPacketMasterStart;
			packbuf[ptr++] = (byte) nPlayer;
			packbuf[ptr++] = (byte) (gProfile[nPlayer].autoaim?1:0);
			packbuf[ptr++] = (byte) (gProfile[nPlayer].slopetilt?1:0);
			packbuf[ptr++] = gProfile[nPlayer].skill;
			packbuf[ptr++] = (byte) gProfile[nPlayer].team;
			System.arraycopy(gProfile[nPlayer].name.getBytes(), 0, packbuf, ptr, Math.min(gProfile[nPlayer].name.length(), 14));
			sendtoall(packbuf, PROFILE.sizeof + 2);
		}
	}

	public void TauntSound(int sndId)
	{
		if ( numplayers > 1 )
		{
		    packbuf[0] = kPacketSound;
		    packbuf[1] = (byte) sndId;
		    sendtoall(packbuf, 2);
		}
		sndStartSample(sndId + 4400, 128, 1, false);
	}

	public void TauntMessage(int msgId)
	{
		if(msgId >= 0 && msgId < 10)
			SendNetMessage(cfg.macros[msgId]);
		viewSetMessage(cfg.macros[msgId], myconnectindex);
	}

	@Override
	public void UpdatePrediction(NetInput input) {
		predictOld.copy(predict);

		SPRITE pSprite = gMe.pSprite;
		short cstat = pSprite.cstat;
		pSprite.cstat = 0;

		viewMovePrediction((INPUT) input);

		if (IsDudeSprite(pSprite)) {
			int nXSprite = pSprite.extra;
			if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
				app.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");

			int airvel = 128;
			// special sector processing
			if (sector[predict.sectnum].extra > 0) {
				int nXSector = sector[predict.sectnum].extra;
				if (!(nXSector > 0 && nXSector < kMaxXSectors))
					app.dassert("nXSector > 0 && nXSector < kMaxXSectors");
				if (xsector[nXSector].reference != predict.sectnum)
					app.dassert("xsector[nXSector].reference == nSector");

				XSECTOR pXSector = xsector[nXSector];
				if (pXSector != null) {
					GetSpriteExtents(pSprite);
					int zBot = extents_zBot;

					if (engine.getflorzofslope(predict.sectnum, predict.x, predict.y) <= zBot) {
						int panVel = 0;
						int panAngle = pXSector.panAngle;
						if (pXSector.panAlways || pXSector.state != 0 || pXSector.busy != 0) {
							panVel = (pXSector.panVel & 0xFF) << 9;
							if (!pXSector.panAlways && pXSector.busy != 0)
								panVel = mulscale(panVel, pXSector.busy, 16);
						}

						if ((sector[predict.sectnum].floorstat & kSectorRelAlign) != 0) {
							panAngle = (GetWallAngle(sector[predict.sectnum].wallptr) + panAngle + kAngle90)
									& kAngleMask;
						}

						int pushX = mulscale(panVel, Cos(panAngle), 30);
						int pushY = mulscale(panVel, Sin(panAngle), 30);
						predict.xvel += pushX;
						predict.yvel += pushY;
					}

					if (pXSector != null && pXSector.Underwater)
						airvel = 5376;
				}
			}

			viewAirDrag(airvel);
			if ((predict.flags & kAttrFalling) != 0 || predict.xvel != 0 || predict.yvel != 0 || predict.zvel != 0
					|| floorVel[predict.sectnum] != 0 || ceilingVel[predict.sectnum] != 0)
				viewMoveDude();
		}

		pSprite.cstat = cstat;

		predictFifo[gPredictTail & kFifoMask].copy(predict);
		gPredictTail++;
	}

	@Override
	public void CorrectPrediction() {
		if (pGameInfo.nGameType == kNetModeOff)
			return;

		PLOCATION pFifo = predictFifo[(gNetFifoTail - 1) & kFifoMask];

		if (pFifo.ang == gMe.pSprite.ang && pFifo.horiz == gMe.horiz && pFifo.x == gMe.pSprite.x
				&& pFifo.y == gMe.pSprite.y && pFifo.z == gMe.pSprite.z)
			return;

		PredictReset();
		predictOld.copy(gPrevView[myconnectindex]);

		gPredictTail = gNetFifoTail;
		while (gPredictTail < gNetFifoHead[myconnectindex])
			UpdatePrediction(gFifoInput[gPredictTail & kFifoMask][myconnectindex]);
	}

	@Override
	public void CalcChecksum() {
		if ((gFrame & (8 * MovesPerPacket - 1)) == 0) {
			CalcGameChecksum(gdxSave);
			for(int i = 0; i < 4; i++)
				LittleEndian.putInt(gCheckFifo[myconnectindex], CheckSize * (gCheckHead[myconnectindex] & kFifoMask) + 4 * i, gChecksum[i]);
			gCheckHead[myconnectindex]++;
		}
	}

	public void PredictReset() {
		predict.ang = gMe.pSprite.ang;
		predict.look = gMe.look;
		predict.horiz = gMe.horiz;
		predict.slope = gMe.slope;
		predict.horizOff = gMe.horizOff;
		predict.fNoJump = gMe.pJump ? 1 : 0;
		predict.Run = gMe.Run;
		predict.underwater = gMe.Underwater;
		predict.jump = gMe.pInput.Jump;
		predict.x = gMe.pSprite.x;
		predict.y = gMe.pSprite.y;
		predict.z = gMe.pSprite.z;
		predict.sectnum = gMe.pSprite.sectnum;
		predict.flags = gMe.pSprite.hitag;
		predict.xvel = sprXVel[gMe.pSprite.xvel];
		predict.yvel = sprYVel[gMe.pSprite.xvel];
		predict.zvel = sprZVel[gMe.pSprite.xvel];
		predict.height = gMe.pXsprite.height;
		predict.moveState = gMe.moveState;
		predict.Turn_Around = gMe.TurnAround;
		predict.center = gMe.pInput.LookCenter;
		predict.moveHit = gSpriteHit[gMe.pSprite.extra].moveHit;
		predict.ceilHit = gSpriteHit[gMe.pSprite.extra].ceilHit;
		predict.floorHit = gSpriteHit[gMe.pSprite.extra].floorHit;

		predict.bobAmp = gMe.bobAmp;
		predict.bobPhase = gMe.bobPhase;
		predict.bobHeight = gMe.bobHeight;
		predict.bobWidth = gMe.bobWidth;
		predict.swayAmp = gMe.swayAmp;
		predict.swayPhase = gMe.swayPhase;
		predict.swayHeight = gMe.swayHeight;
		predict.swayWidth = gMe.swayWidth;
		predict.weapOffZ = gMe.weaponAboveZ - gMe.viewOffZ - 3072;
		predict.viewOffZ = gMe.viewOffZ;
		predict.viewOffdZ = gMe.viewOffdZ;
		predict.weaponAboveZ = gMe.weaponAboveZ;
		predict.weapOffdZ = gMe.weapOffdZ;

		predictOld.copy(predict);
	}

	private void viewMoveDude() {
		predictSprite.set(gMe.pSprite);
		predictSprite.x = predict.x;
		predictSprite.y = predict.y;
		predictSprite.z = predict.z;
		predictSprite.sectnum = predict.sectnum;

		PLAYER pPlayer = null;
		if (IsPlayerSprite(predictSprite))
			pPlayer = gPlayer[predictSprite.lotag - kDudePlayer1];

		if (!(predictSprite.lotag >= kDudeBase && predictSprite.lotag < kDudeMax))
			app.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");

		int zTop, zBot;
		GetSpriteExtents(predictSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		int floorDist = (zBot - predict.z) / 4;
		int ceilDist = (predict.z - zTop) / 4;
		int clipDist = predictSprite.clipdist << 2;

		short nSector = predict.sectnum;
		if (!(nSector >= 0 && nSector < kMaxSectors))
			app.dassert("nSector >= 0 && nSector < kMaxSectors");

		if (predict.xvel != 0 || predict.yvel != 0) {
			if (pPlayer != null && gNoClip) {
				predict.x += predict.xvel >> 12;
				predict.y += predict.yvel >> 12;

				nSector = engine.updatesector(predict.x, predict.y, nSector);
				if (nSector == -1)
					nSector = predict.sectnum;
			} else {
				predict.moveHit = ClipMove(predict.x, predict.y, predict.z, nSector, predict.xvel >> 12,
						predict.yvel >> 12, clipDist, ceilDist, floorDist, CLIPMASK0 | 0x3000);

				predict.x = clipm_px;
				predict.y = clipm_py;
				predict.z = clipm_pz;
				nSector = (short) clipm_pnsectnum;

				if (nSector == -1)
					nSector = predict.sectnum;

				if (sector[nSector].lotag >= kSectorPath && sector[nSector].lotag <= kSectorRotate) {
					engine.pushmove(predict.x, predict.y, predict.z, nSector, clipDist, ceilDist, floorDist, CLIPMASK0);
					predict.x = pushmove_x;
					predict.y = pushmove_y;
					predict.z = pushmove_z;

					if (pushmove_sectnum != -1)
						nSector = pushmove_sectnum;
				}

				if (!(nSector >= 0 && nSector < kMaxSectors))
					app.dassert("nSector >= 0 && nSector < kMaxSectors");
			}

			if ((predict.moveHit & kHitTypeMask) == kHitWall) {
				int nWall = predict.moveHit & kHitIndexMask;
				ReflectVector(predict.xvel, predict.yvel, nWall, 0);

				predict.xvel = refl_x;
				predict.yvel = refl_y;
			}
		}

		predictSprite.x = predict.x;
		predictSprite.y = predict.y;
		predictSprite.z = predict.z;

		if (predict.sectnum != nSector) {
			if (!(nSector >= 0 && nSector < kMaxSectors))
				app.dassert("nSector >= 0 && nSector < kMaxSectors");

			predictSprite.sectnum = predict.sectnum = nSector;
		}

		boolean underwater = false, depth = false;
		if (sector[nSector].extra > 0) {
			if (xsector[sector[nSector].extra].Underwater)
				underwater = true;
			if (xsector[sector[nSector].extra].Depth != 0)
				depth = true;
		}

		int nUpper = gUpperLink[nSector], nLower = gLowerLink[nSector];
		if (nUpper >= 0) {
			if (sprite[nUpper].lotag == kStatPurge || sprite[nUpper].lotag == kStatSpares)
				depth = true;
		}
		if (nLower >= 0) {
			if (sprite[nLower].lotag == kStatMarker || sprite[nLower].lotag == kStatFlare)
				depth = true;
		}

		if (pPlayer != null)
			clipDist += 16;

		if (predict.zvel != 0)
			predict.z += predict.zvel >> 8;

		predictSprite.z = predict.z;

		int ceilz, ceilhit, floorz, floorhit;

		GetZRange(predict.x, predict.y, predict.z, predict.sectnum, clipDist, CLIPMASK0 | 0x3000);
		ceilz = gz_ceilZ;
		ceilhit = gz_ceilHit;
		floorz = gz_floorZ;
		floorhit = gz_floorHit;
		GetSpriteExtents(predictSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if ((predict.flags & kAttrGravity) != 0) {
			int G = 58254;

			if (!depth) {
				if (underwater || zBot >= floorz)
					G = 0; // если ноги ниже уровня пола
			} else {
				if (underwater) {
					int ceilzofslope = engine.getceilzofslope(nSector, predict.x, predict.y);
					if (ceilzofslope > zTop) // если голова выше уровня воды
					{
						if (zBot != zTop)
							G += -80099 * (zBot - ceilzofslope) / (zBot - zTop);
					} else
						G = 0;
				} else {
					int florzofslope = engine.getflorzofslope(nSector, predict.x, predict.y);
					if (florzofslope < zBot && (zBot != zTop)) // если ноги ушли под воду
						G += -80099 * (zBot - florzofslope) / (zBot - zTop);
				}
			}

			if (G != 0) {
				predict.z += 2 * G >> 8;
				predict.zvel += G;
			}
		}

		predictSprite.z = predict.z;
		GetSpriteExtents(predictSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if (pPlayer != null && zBot >= floorz) {
			int ofloorz = floorz;
			int ofloorhit = floorhit;
			GetZRange(predict.x, predict.y, predict.z, predict.sectnum, gMe.pSprite.clipdist << 2, CLIPMASK0 | 0x3000);

			ceilz = gz_ceilZ;
			ceilhit = gz_ceilHit;
			floorz = gz_floorZ;
			floorhit = gz_floorHit;

			if (zBot <= floorz && predict.z - ofloorz < floorDist) {
				floorz = ofloorz;
				floorhit = ofloorhit;
			}
		}

		if (floorz > zBot) {
			predict.floorHit = 0;
			if ((predict.flags & kAttrGravity) != 0)
				predict.flags |= kAttrFalling;
		} else { // hit floor?
			predict.floorHit = floorhit;
			predict.z += (floorz - zBot);

			int dZvel = (int) (predict.zvel - floorVel[predict.sectnum]);
			if (dZvel <= 0) {
				if (predict.zvel == 0)
					predict.flags &= ~kAttrFalling;
			} else {
				GravityVector(predict.xvel, predict.yvel, dZvel, predict.sectnum, 0);

				predict.xvel = refl_x;
				predict.yvel = refl_y;
				predict.zvel = refl_z;

				if (klabs(dZvel) >= 65536) {
					predict.flags |= kAttrFalling;
				} else {
					predict.zvel = floorVel[predict.sectnum];
					predict.flags &= ~kAttrFalling;
				}
			}
		}

		if (ceilz < zTop) {
			predict.ceilHit = 0;
		} else // hit ceiling
		{
			predict.ceilHit = ceilhit;
			if (ceilz - zTop > 0)
				predict.z += ceilz - zTop;

			if (predict.zvel <= 0 && (predict.flags & kAttrFalling) != 0)
				predict.zvel = (-predict.zvel / 8);
		}

		predictSprite.z = predict.z;
		GetSpriteExtents(predictSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if (floorz - zBot > 0)
			predict.height = (floorz - zBot) >> 8;
		else
			predict.height = 0;

		// drag and friction
		if (predict.xvel != 0 || predict.yvel != 0) {
			if ((floorhit & kHitTypeMask) == kHitSprite) {
				int nUnderSprite = floorhit & kHitIndexMask;
				if ((sprite[nUnderSprite].cstat & kSpriteRMask) == kSpriteFace) {
					// push it off the face sprite
					predict.xvel += mulscale(kFrameTicks, predict.x - sprite[nUnderSprite].x, 2);
					predict.yvel += mulscale(kFrameTicks, predict.y - sprite[nUnderSprite].y, 2);
					return;
				}
			}

			nSector = predict.sectnum;
			if (sector[nSector].extra <= 0 || !xsector[sector[nSector].extra].Underwater) {
				if (predict.height < 256) {
					int kv = kDudeDrag;
					if (predict.height != 0)
						kv -= mulscale(predict.height, kv, 8);

					predict.xvel -= dmulscale(kv, predict.xvel, 0x8000, 1, 16);
					predict.yvel -= dmulscale(kv, predict.yvel, 0x8000, 1, 16);

					if (engine.qdist(predict.xvel, predict.yvel) < kMinDudeVel) {
						predict.xvel = predict.yvel = 0;
					}
				}
			}
		}
	}

	public boolean WaitForContentCheck(String filepath, int timeout)
	{
		Arrays.fill(gContentFound,  (byte)-1);
		if (numplayers < 2) return true;

		WaitForSend();

		packbuf[0] = kPacketContentRequest;
		int len = Math.min(filepath.length(), 250);
		LittleEndian.putInt(packbuf, 1, len);
		System.arraycopy(filepath.getBytes(), 0, packbuf, 5, len);
		sendtoall(packbuf, len + 5);
		gContentFound[myconnectindex] = 1;

		long starttime = System.currentTimeMillis();
		while (true)
		{
			long time = System.currentTimeMillis() - starttime;
			if ((timeout != 0 && time > timeout))
			{
				Console.Println("Connection timed out!", OSDTEXT_YELLOW);
				return false;
			}

			GetPackets();

			int i;
			for(i=connecthead;i>=0;i=connectpoint2[i])
			{
				if (gContentFound[i] == -1) break;
				if (myconnectindex != connecthead) { i = -1; break; } //slaves in M/S mode only wait for master
			}

			if (i < 0) {
				for(i=connecthead;i>=0;i=connectpoint2[i])
				{
					if(gContentFound[i] != 1)
						return false;
				}
				return true;
			}
		}
	}

	private void viewMovePrediction(INPUT pInput) {
		POSTURE cp = gPosture[gMe.nLifeMode][predict.moveState];
		SPRITE pSprite = gMe.pSprite;

		GetSpriteExtents(pSprite);

		int clipDist = pSprite.clipdist << 2;
		int floorDist = (extents_zBot - pSprite.z) / 4;
		int ceilDist = (pSprite.z - extents_zTop) / 4;

		if (!gNoClip) {
			engine.pushmove(predict.x, predict.y, predict.z, predict.sectnum, clipDist, ceilDist, floorDist, CLIPMASK0);

			predict.x = pushmove_x;
			predict.y = pushmove_y;
			predict.z = pushmove_z;

			if (pushmove_sectnum != -1)
				predict.sectnum = pushmove_sectnum;
		}

		long vel;
		predict.Run = pInput.Run;
		if (!pInput.Jump)
			predict.jump = false;

		if (predict.moveState == 1) {
			if (pInput.Forward != 0) {
				predict.xvel += mulscale(Cos((short) predict.ang), pInput.Forward * cp.frontAccel, 30);
				predict.yvel += mulscale(Sin((short) predict.ang), pInput.Forward * cp.frontAccel, 30);
			}

			if (pInput.Strafe != 0) {
				predict.xvel += mulscale(Sin((short) predict.ang), pInput.Strafe * cp.sideAccel, 30);
				predict.yvel -= mulscale(Cos((short) predict.ang), pInput.Strafe * cp.sideAccel, 30);
			}
		} else {
			if (predict.height < 256) {
				int zvel = 65536;
				if (predict.height != 0)
					zvel -= (predict.height << 16) / 256;

				if (pInput.Forward != 0) {
					if (pInput.Forward > 0)
						vel = pInput.Forward * cp.frontAccel;
					else
						vel = pInput.Forward * cp.backAccel;

					if (predict.height != 0)
						vel = mulscale(zvel, vel, 16);

					predict.xvel += mulscale(Cos((short) predict.ang), vel, 30);
					predict.yvel += mulscale(Sin((short) predict.ang), vel, 30);
				}

				if (pInput.Strafe != 0) {
					vel = pInput.Strafe * cp.sideAccel;
					if (predict.height != 0)
						vel = mulscale(zvel, vel, 16);

					predict.xvel += mulscale(Sin((short) predict.ang), vel, 30);
					predict.yvel -= mulscale(Cos((short) predict.ang), vel, 30);
				}
			}
		}

		if (pInput.Turn != 0)
			predict.ang = BClampAngle(predict.ang + (kFrameTicks * pInput.Turn / 16.0f));

		if (pInput.TurnAround) {
			if (predict.Turn_Around == 0)
				predict.Turn_Around = -1024;
		}

		if (predict.Turn_Around < 0) {
			int angSpeed;
			if (predict.moveState == 1)
				angSpeed = 64;
			else
				angSpeed = 128;

			predict.Turn_Around = ClipHigh(predict.Turn_Around + angSpeed, 0);
			predict.ang = BClampAngle(predict.ang + angSpeed);
		}

		if (!predict.jump)
			predict.fNoJump = predict.jump ? 1 : 0;

		switch (predict.moveState) {
		case kMoveWalk:
			if (!predict.jump && pInput.Jump && predict.height == 0) {
				if (inventoryCheck(gMe, kInventoryJumpBoots))
					predict.zvel = -1529173;
				else
					predict.zvel = -764586;
				predict.fNoJump = 1;
			}
			if (pInput.Crouch)
				predict.moveState = kMoveCrouch;

			break;
		case kMoveSwim:
			if (pInput.Jump)
				predict.zvel -= 23301;
			if (pInput.Crouch)
				predict.zvel += 23301;
			break;
		case kMoveCrouch:
			if (!pInput.Crouch)
				predict.moveState = kMoveWalk;
			break;
		}

		predict.look = BClipRange(predict.look + pInput.mlook, -kLookMax, kLookMax);

		if (pInput.LookCenter && !pInput.Lookup && !pInput.Lookdown) {
			if (predict.look < 0)
				predict.look = ClipHigh(predict.look + kFrameTicks, 0);

			if (predict.look > 0)
				predict.look = ClipLow(predict.look - kFrameTicks, 0);

			if (predict.look == 0)
				predict.center = false;
		} else {
			if (pInput.Lookup)
				predict.look = ClipHigh(predict.look + kFrameTicks, kLookMax);

			if (pInput.Lookdown)
				predict.look = ClipLow(predict.look - kFrameTicks, -kLookMax);
		}

		if (predict.look > 0)
			predict.horiz = (float) (BSinAngle(predict.look * (kAngle90 / kLookMax)) * kHorizUpMax / 16384.0f);
		else if (predict.look < 0)
			predict.horiz = (float) (BSinAngle(predict.look * (kAngle90 / kLookMax)) * kHorizDownMax / 16384.0f);
		else
			predict.horiz = 0;

		int floorhit = (predict.floorHit & kHitTypeMask);
		if (predict.height < 16 && (floorhit == kHitFloor || floorhit == 0)
				&& (sector[predict.sectnum].floorstat & 2) != 0) {
			int oldslope = engine.getflorzofslope(predict.sectnum, predict.x, predict.y);
			int dx = mulscale(64, Cos((short) predict.ang), 30) + predict.x;
			int dy = mulscale(64, Sin((short) predict.ang), 30) + predict.y;

			short nSector = engine.updatesector(dx, dy, predict.sectnum);
			if (nSector == predict.sectnum) {
				int newslope = engine.getflorzofslope(nSector, dx, dy);
				int slope = (((oldslope - newslope) >> 3) - predict.slope) << 14;
				predict.slope += (slope >> 16);
			}
		} else {
			int slope = predict.slope;
			int newslope = -slope << 14;
			newslope = slope + (newslope >> 16);
			predict.slope = newslope;
			if (newslope < 0)
				newslope = -(slope + ((-slope << 14) >> 16));
			if (newslope < 4)
				predict.slope = 0;
		}

		predict.horizOff = -128 * predict.horiz;

		int moveDist = (int) (engine.qdist(predict.xvel, predict.yvel) >> 16);

		predict.viewOffdZ += mulscale(28672, predict.zvel - predict.viewOffdZ, 16);
		int dZv = predict.z - cp.viewSpeed - predict.viewOffZ;
		if (dZv > 0)
			predict.viewOffdZ += mulscale(40960, dZv << 8, 16);
		else
			predict.viewOffdZ += mulscale(6144, dZv << 8, 16);
		predict.viewOffZ += predict.viewOffdZ >> 8;

		predict.weapOffdZ += mulscale(20480, predict.zvel - predict.weapOffdZ, 16);
		int dZw = predict.z - cp.weapSpeed - predict.weaponAboveZ;
		if (dZw > 0)
			predict.weapOffdZ += mulscale(32768, dZw << 8, 16);
		else
			predict.weapOffdZ += mulscale(3072, dZw << 8, 16);
		predict.weaponAboveZ += predict.weapOffdZ >> 8;

		predict.bobAmp = ClipLow(predict.bobAmp - kFrameTicks, 0);

		if (predict.moveState == kMoveSwim) {
			predict.bobPhase = (predict.bobPhase + kFrameTicks * kAngle360 / kTimerRate / 4) & kAngleMask;
			predict.swayPhase = (predict.swayPhase + kFrameTicks * kAngle360 / kTimerRate / 4) & kAngleMask;

			predict.bobHeight = mulscale(cp.bobV * 10, Sin(predict.bobPhase * 2), 30);
			predict.bobWidth = mulscale(cp.bobH * predict.bobAmp, Sin(predict.bobPhase - kAngle45), 30);
			predict.swayHeight = mulscale(cp.swayV * predict.bobAmp, Sin(predict.swayPhase * 2), 30);
			predict.swayWidth = mulscale(cp.swayH * predict.bobAmp, Sin(predict.swayPhase - kAngle60), 30);
		} else {
			if (predict.height < 256) {

				boolean Run = false;

				predict.bobPhase = (predict.bobPhase + kFrameTicks * cp.pace[Run ? 1 : 0]) & kAngleMask;
				predict.swayPhase = (predict.swayPhase + kFrameTicks * cp.pace[Run ? 1 : 0] / 2) & kAngleMask;

				if (Run) {
					if (predict.bobAmp < 60)
						predict.bobAmp = ClipHigh(predict.bobAmp + moveDist, 60);
				} else {
					if (predict.bobAmp < 30)
						predict.bobAmp = ClipHigh(predict.bobAmp + moveDist, 30);
				}

				predict.bobHeight = mulscale(cp.bobV * predict.bobAmp, Sin(predict.bobPhase * 2), 30);
				predict.bobWidth = mulscale(cp.bobH * predict.bobAmp, Sin(predict.bobPhase - kAngle45), 30);
				predict.swayHeight = mulscale(cp.swayV * predict.bobAmp, Sin(predict.swayPhase * 2), 30);
				predict.swayWidth = mulscale(cp.swayH * predict.bobAmp, Sin(predict.swayPhase - kAngle60), 30);
			}
		}

		predict.underwater = false;
		if (predict.moveState == kMoveSwim) {
			predict.underwater = true;

			if (gLowerLink[predict.sectnum] > 0) {
				int type = sprite[gLowerLink[predict.sectnum]].lotag;
				if (type == 14 || type == 10) {
					if (engine.getceilzofslope(predict.sectnum, predict.x, predict.y) > predict.viewOffZ)
						predict.underwater = false;
				}
			}
		}
	}

	private void viewAirDrag(int nDrag) {
		int windX = 0, windY = 0;

		int nSector = predict.sectnum;
		if (nSector >= 0 && nSector < kMaxSectors) {
			if (sector[nSector].extra > 0) {
				int nXSector = sector[nSector].extra;
				if (nXSector > 0 && nXSector < kMaxXSectors) {
					XSECTOR pXSector = xsector[nXSector];
					if (pXSector.windVel != 0 && (pXSector.windAlways || pXSector.busy != 0)) {
						int windVel = pXSector.windVel << 12;
						if (!pXSector.windAlways && pXSector.busy != 0)
							windVel = mulscale(pXSector.busy, windVel, 16);

						windX = mulscale(Cos(pXSector.windAng), windVel, 30);
						windY = mulscale(Sin(pXSector.windAng), windVel, 30);
					}
				}
			}
		}

		predict.xvel += mulscale(nDrag, windX - predict.xvel, 16);
		predict.yvel += mulscale(nDrag, windY - predict.yvel, 16);
		predict.zvel -= mulscale(nDrag, predict.zvel, 16);
	}

	private void CalcGameChecksum(int nVersion)
	{
		Arrays.fill(gChecksum, 0);
		gChecksum[0] = bRandom();
		if(numplayers > 1) {
			for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
			{
				gChecksum[1] ^= Checksum(gPlayer[i].getBytes(nVersion), 865);
				gChecksum[2] ^= Checksum(gPlayer[i].pSprite.getBytes(), SPRITE.sizeof);
				gChecksum[3] ^= Checksum(gPlayer[i].pXsprite.getBytes(), XSPRITE.sizeof);
			}
		}
	}

	@Override
	public void NetDisconnect(int nPlayer) {
		super.NetDisconnect(nPlayer);
		app.Disconnect();
	}

	@Override
	public void ComputerInput(int i) {
		/* nothing */
	}
}
