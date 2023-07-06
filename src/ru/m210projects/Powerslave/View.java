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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Type.StatusAnim.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Snake.*;

import java.util.Arrays;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Powerslave.Type.StatusAnim;

public class View {

	public static int[] nDigit = new int[3];
	public static int nOverhead = 0;
	public static int zoom = 768;

	// 1 - ~16
	// 2 - 2
	// 4 - 1
	public static void overwritesprite(int thex, int they, int tilenum, int shade, int stat, int dapalnum) {
		engine.rotatesprite(thex << 16, they << 16, 78848, (stat & 8) << 7, tilenum, shade, dapalnum,
				(((stat & 1) ^ 1) << 4) + (stat & 2) // 16 + 2
						+ ((stat & 4) >> 2) // 1
						+ (((stat & 16) >> 2) ^ ((stat & 8) >> 1)) + ((stat & 32) >> 2) + (stat & 512) + (stat & 256),
				windowx1, windowy1, windowx2, windowy2);
	}

	public static void SetHealthFrame(int var) {
		if (healthperline == 0)
			return;

		nHealthLevel = (800 - PlayerList[nLocalPlayer].HealthAmount) / healthperline;
		if (nHealthLevel >= nMeterRange)
			nHealthLevel = nMeterRange - 1;
		if (nHealthLevel < 0)
			nHealthLevel = 0;

		if (var < 0)
			BuildStatusAnim(4, 0);
	}

	public static void SetMagicFrame() {
		if (magicperline == 0)
			return;

		nMagicLevel = (1000 - PlayerList[nLocalPlayer].MagicAmount) / magicperline;
		if (nMagicLevel >= nMeterRange)
			nMagicLevel = nMeterRange - 1;
		if (nMagicLevel < 0)
			nMagicLevel = 0;
	}

	public static void UpdateScreenSize() {

		engine.setview(0, 0, xdim - 1, ydim - 1);
		RefreshStatus();
	}

	public static void StatusMessage(int time, String text, int nPlayer) {
		message_timer = time;
		message_text = text;
		Console.Println(text);
	}

	public static void InitStatus() {
		nStatusSeqOffset = SeqOffsets[26];
		nHealthFrames = SeqSize[SeqOffsets[26] + 1];
		int tile = GetSeqPicnum(26, 1, 0);
		nMagicFrames = SeqSize[nStatusSeqOffset + 129];

		nHealthFrame = 0;
		nMagicFrame = 0;
		nHealthLevel = 0;
		nMagicLevel = 0;
		nMeterRange = engine.getTile(tile).getHeight();
		if(nMeterRange <= 0) {
			game.ThrowError("Error: Tile #" + tile + " is not found");
			return;
		}
		magicperline = 1000 / nMeterRange;
		healthperline = 800 / nMeterRange;
		nAirFrames = SeqSize[nStatusSeqOffset + 133];
		if (nAirFrames != 0)
			airperline = 100 / nAirFrames;
		nCounter = 0;
		nCounterDest = 0;
		Arrays.fill(nDigit, 0);

		SetCounter(0);
		SetHealthFrame(0);
		SetMagicFrame();
		SetAirFrame();

		for (byte i = 0; i < 50; i++) {
			StatusAnimsFree[i] = i;
			if (StatusAnim[i] == null)
				StatusAnim[i] = new StatusAnim();
		}

		nLastAnim = -1;
		nFirstAnim = -1;
		nItemSeq = -1;
		nAnimsFree_X_1 = 50;

		message_timer = 0;
	}

	public static void RefreshStatus() {
		BuildStatusAnim(2 * nPlayerLives[nLocalPlayer] + 145, 0);
		int keys = PlayerList[nLocalPlayer].KeysBitMask >> 12;
		for (int i = 0, s = 37; i < 4; i++, s += 2)
			if ((keys & (1 << i)) != 0)
				BuildStatusAnim(s, 0);

		if (nPlayerItem[nLocalPlayer] != -1) {
			if (nItemMagic[nPlayerItem[nLocalPlayer]] <= PlayerList[nLocalPlayer].MagicAmount)
				nItemAltSeq = 0;
			else
				nItemAltSeq = 2;
			nItemSeq = nItemAltSeq + nItemSeqOffset[nPlayerItem[nLocalPlayer]];
			nItemFrames = SeqSize[nStatusSeqOffset + nItemSeq];
			BuildStatusAnim(2 * PlayerList[nLocalPlayer].ItemsAmount[nPlayerItem[nLocalPlayer]] + 156, 0);
		}

		SetHealthFrame(0);
		SetMagicFrame();
	}

	public static void SetAirFrame() {
		if (airperline == 0)
			return;

		airframe = PlayerList[nLocalPlayer].AirAmount / airperline;
		if (airframe < nAirFrames) {
			if (airframe < 0)
				airframe = 0;
		} else
			airframe = nAirFrames - 1;
	}

	public static void MoveStatus() {
		if (++nHealthFrame >= nHealthFrames)
			nHealthFrame = 0;
		if (++nMagicFrame >= nMagicFrames)
			nMagicFrame = 0;

		if (nItemSeq >= 0 && ++nItemFrame >= nItemFrames) {
			if (nItemSeq == 67) {
				SetItemSeq();
			} else {
				nItemSeq -= nItemAltSeq;
				if (nItemAltSeq != 0 || (totalmoves & 0x1F) != 0) {
					if (nItemAltSeq < 2)
						nItemAltSeq = 0;
				} else {
					nItemAltSeq = 1;
				}
				nItemFrame = 0;
				nItemSeq += nItemAltSeq;
				nItemFrames = SeqSize[(nItemAltSeq + nItemSeq) + nStatusSeqOffset];
			}
		}
		int v1 = message_timer;
		if (message_timer != 0) {
			message_timer -= 4;
			if ((v1 - 4) <= 0) {
				message_timer = 0;
			}
		}
		MoveStatusAnims();
		if (nCounter == nCounterDest) {
			nCounter = nCounterDest;
			ammodelay = 3;
			return;
		}
		if (--ammodelay == 0)
			return;

		int v2 = nCounterDest - nCounter;
		if (nCounterDest - nCounter <= 0) {
			if (v2 < -30) {
				nCounter += (nCounterDest - nCounter) >> 1;
				SetCounterDigits();
				return;
			}

			for (int i = 0; i < 3; ++i) {
				int v7 = i;
				int v8 = nDigit[v7] - 1;
				nDigit[v7] = v8;
				if (v8 < 0)
					nDigit[v7] = v8 + 30;
				if (nDigit[i] < 27)
					break;
			}
		} else {
			if (v2 > 30) {
				nCounter += (nCounterDest - nCounter) >> 1;
				SetCounterDigits();
				return;
			}
			for (int j = 0; j < 3; ++j) {
				int v4 = j;
				int v5 = nDigit[v4] + 1;
				nDigit[v4] = v5;
				if (v5 <= 27)
					break;
				if (v5 >= 30)
					nDigit[v4] = v5 - 30;
			}
		}
		if ((nDigit[0] % 3) == 0)
			nCounter = nDigit[0] / 3 + 100 * (nDigit[2] / 3) + 10 * (nDigit[1] / 3);
		int v9 = nCounterDest - nCounter;
		if (nCounterDest - nCounter < 0)
			v9 = -v9;
		ammodelay = 4 - (v9 >> 1);
		if (ammodelay >= 1)
			return;

		ammodelay = 1;

	}

	public static void MoveStatusAnims() {
		int v7;
		for (int i = nFirstAnim;; i = StatusAnim[v7].field_6) {
			v7 = i;
			int v1 = i;
			if (i == -1)
				break;
			int v2 = (StatusAnim[i].field_0 + nStatusSeqOffset);
			MoveSequence(-1, StatusAnim[i].field_0 + nStatusSeqOffset, StatusAnim[i].field_2);
			int v4 = StatusAnim[i].field_2 + 1;
			int v5 = SeqSize[v2];
			StatusAnim[i].field_2 = (short) v4;
			if (v4 >= v5) {
				int v6;
				if ((StatusAnimFlags[v1] & 0x10) != 0)
					v6 = 0;
				else
					v6 = v5 - 1;
				StatusAnim[i].field_2 = (short) v6;
			}
		}
	}

	public static void DrawStatus() {
		int gView = cfg.nScreenSize;
		if (gView == 2) {
			DrawStatusSequence(nStatusSeqOffset, 0, 0);

			DrawStatusSequence(nStatusSeqOffset + 128, 0, 0); // black hole health
			DrawStatusSequence(nStatusSeqOffset + 127, 0, 0); // black hole mana

			DrawStatusSequence(nStatusSeqOffset + 1, nHealthFrame, nHealthLevel); // health
			DrawStatusSequence(nStatusSeqOffset + 129, nMagicFrame, nMagicLevel); // manna
			DrawStatusSequence(nStatusSeqOffset + 125, 0, 0); // health icon

			DrawStatusSequence(nStatusSeqOffset + 130, 0, 0); // health border
			DrawStatusSequence(nStatusSeqOffset + 131, 0, 0); // mana border

			if (nItemSeq >= 0)
				DrawStatusSequence((nItemSeq + nStatusSeqOffset), nItemFrame, 0);
			DrawStatusAnims(false); // air and lives
			if ((SectFlag[nPlayerViewSect[nLocalPlayer]] & 0x2000) != 0) {
				DrawStatusSequence((nStatusSeqOffset + 133), airframe, 0);
			}

			DrawCustomSequence(nStatusSeqOffset + 35, ((inita + 128) & 0x7FF) >> 8,
					(inita >= 128 && inita <= 892) ? 1 : 0, 0, 0, 0, 0); // compass

			DrawStatusSequence((nStatusSeqOffset + 44), nDigit[2], 0);
			DrawStatusSequence((nStatusSeqOffset + 45), nDigit[1], 0);
			DrawStatusSequence((nStatusSeqOffset + 46), nDigit[0], 0);
		}
		else if (gView == 1) // GDX HUD
		{
			engine.rotatesprite(66 << 16, 179 << 16, 65536, 0, HUDLEFT, 0, 0, 10 | 256, 0, 0, xdim - 1, ydim - 1);
			engine.rotatesprite(253 << 16, 179 << 16, 65536, 0, HUDRIGHT, 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);

			DrawCustomSequence(nStatusSeqOffset + 1, nHealthFrame, 0, nHealthLevel, 0, 0, 512); // health
			DrawCustomSequence(nStatusSeqOffset + 129, nMagicFrame, 0, nMagicLevel, 0, 0, 256); // manna
			DrawCustomSequence(nStatusSeqOffset + 125, 0, 0, 0, 0, 0, 512); // health icon
			if (nItemSeq >= 0)
				DrawCustomSequence((nItemSeq + nStatusSeqOffset), nItemFrame, 0, 0, 0, 0, 256);

			DrawCustomSequence(nStatusSeqOffset + 130, 0, 0, 0, 0, 0, 512); // health border
			DrawCustomSequence(nStatusSeqOffset + 131, 0, 0, 0, 0, 0, 256); // mana border

			engine.rotatesprite(76 << 16, 186 << 16, 65536, 0, 883, 0, 0, 10 | 256, 0, 0, xdim - 1, ydim - 1); // first
																												// air
																												// icon

			DrawStatusAnims(true); // air and lives
			if ((SectFlag[nPlayerViewSect[nLocalPlayer]] & 0x2000) != 0)
				DrawCustomSequence((nStatusSeqOffset + 133), airframe, -7, 0, 0, 0, 256);

			DrawCustomSequence((nStatusSeqOffset + 44), nDigit[2], -13, 0, 0, 0, 256);
			DrawCustomSequence((nStatusSeqOffset + 45), nDigit[1], -13, 0, 0, 0, 256);
			DrawCustomSequence((nStatusSeqOffset + 46), nDigit[0], -13, 0, 0, 0, 256);
		}

		DrawItemTimer(300, 20, nLocalPlayer);

		if (nNetPlayerCount != 0) {

		}

		if (cfg.gShowMessages) {
			if (nSnakeCam < 0) {
				if (message_timer != 0) {
					if (message_text == deathMessage1) {
						game.getFont(2).drawText(0, 5, deathMessage2, 2 * 65536, 0, 0, TextAlign.Left, 0, true);
						game.getFont(2).drawText(0, 25, deathMessage1, 2 * 65536, 0, 0, TextAlign.Left, 0, true);
					} else
						game.getFont(2).drawText(0, 5, message_text, 2 * 65536, 0, 0, TextAlign.Left, 0, true);
				}
			} else {
				game.getFont(2).drawText(2, 5, "S E R P E N T   C A M", 2 * 65536, 0, 0, TextAlign.Left, 0, true);
			}
		}

		if (cfg.gCrosshair) {
			int col = 187;
			engine.getrender().drawline256((xdim - mulscale(cfg.gCrossSize, 16, 16)) << 11, ydim << 11,
					(xdim - mulscale(cfg.gCrossSize, 4, 16)) << 11, ydim << 11, col);
			engine.getrender().drawline256((xdim + mulscale(cfg.gCrossSize, 4, 16)) << 11, ydim << 11,
					(xdim + mulscale(cfg.gCrossSize, 16, 16)) << 11, ydim << 11, col);
			engine.getrender().drawline256(xdim << 11, (ydim - mulscale(cfg.gCrossSize, 16, 16)) << 11, xdim << 11,
					(ydim - mulscale(cfg.gCrossSize, 4, 16)) << 11, col);
			engine.getrender().drawline256(xdim << 11, (ydim + mulscale(cfg.gCrossSize, 4, 16)) << 11, xdim << 11,
					(ydim + mulscale(cfg.gCrossSize, 16, 16)) << 11, col);
		}

		RefreshStatus();
	}

	private static int[] gItemPic = { 736, 752, 754, 726, 744 };
	private static void DrawItemTimer(int posx, int posy, int plr) {
		for(int i = 0; i < 5; i++) {
			int value = ItemTimer(i, plr);
			if(value > 0) {
				int tile = gItemPic[i];
				Tile pic = engine.getTile(tile);
				int x = (posx - (pic.getWidth() / 8));
				int y = (posy - (pic.getHeight() / 8));
				engine.rotatesprite(x << 16, y << 16, 32768, 0, gItemPic[i], 0, 0, 10 | 512, 0, 0, xdim - 1, ydim - 1);

				int offs = Bitoa(value, statbuffer);
				offs = buildString(statbuffer, offs, "%");
				game.getFont(2).drawText(x, posy + (pic.getHeight() / 8) + 4, statbuffer, 32768, 24, 0, TextAlign.Center, 2 | 512, true);
				posy += 25;
			}
		}
	}

	private static int ItemTimer(int num, int plr) {
		switch(num) {
		case 0: //Scarab item
			return (PlayerList[plr].invisibility * 100) / 900;
		case 1: //Hand item
			return (nPlayerDouble[plr] * 100) / 1350;
		case 2: //Mask
			return (PlayerList[plr].AirMaskAmount * 100) / 1350;
		case 3: //Invisible
			return (nPlayerInvisible[plr] * 100) / 900;
		case 4: //Torch
			return (nPlayerTorch[plr] * 100) / 900;
		}

		return -1;
	}

	public static void DrawStatusAnims(boolean nCustomHud) {
		for (int a1 = nFirstAnim; a1 != -1; a1 = StatusAnim[a1].field_6) {
			int v2 = StatusAnim[a1].field_0 + nStatusSeqOffset;
			if (nCustomHud) {
				int stat = 0, xoffs = 0;
				switch (StatusAnim[a1].field_0) {
				case 36:
				case 37: // power key
					xoffs = 12;
					stat = 512;
					break;
				case 38:
				case 39: // time key
					xoffs = 9;
					stat = 512;
					break;
				case 40:
				case 41: // war key
					xoffs = 7;
					stat = 512;
					break;
				case 42:
				case 43: // earth key
					xoffs = 3;
					stat = 512;
					break;
				case 4: // damage border
				case 7:
				case 10:
				case 13:
				case 19:
				case 145: // no lives
				case 147: // lives count 1
				case 149: // lives count 2
				case 151: // lives count 3
				case 152:
				case 153: // lives count 4
				case 154:
				case 155: // lives count 5
					stat = 512;
					break;
				case 132: // air
					xoffs = -7;
				case 156: // item count 0
				case 158: // item count 1
				case 160: // item count 2
				case 162: // item count 3
				case 164: // item count 4
				case 166: // item count 5
					stat = 256;
					break;
				default:
					Console.Println("Anim " + StatusAnim[a1].field_0, Console.OSDTEXT_RED);
					break;
				}

				DrawCustomSequence(v2, StatusAnim[a1].field_2, xoffs, 0, 0, 0, stat);
			} else
				DrawStatusSequence(v2, StatusAnim[a1].field_2, 0);

			if (StatusAnim[a1].field_2 >= SeqSize[v2] - 1 && (StatusAnimFlags[a1] & 0x10) == 0) {
				StatusAnim[a1].field_4--;
				if (StatusAnim[a1].field_4 <= 0)
					DestroyStatusAnim(a1);
			}
		}
	}

	public static void DestroyStatusAnim(int result) {
		int v1 = StatusAnim[result].field_6;
		int v2 = StatusAnim[result].field_7;
		if (v2 >= 0)
			StatusAnim[StatusAnim[result].field_7].field_6 = (byte) v1;
		if (v1 >= 0)
			StatusAnim[v1].field_7 = (byte) v2;
		if (result == nFirstAnim)
			nFirstAnim = v1;
		if (result == nLastAnim)
			nLastAnim = v2;
		int v3 = nAnimsFree_X_1 + 1;
		StatusAnimsFree[nAnimsFree_X_1] = (byte) result;
		nAnimsFree_X_1 = v3;
	}

	public static void analyzesprites(int smoothratio) {
		for (int i = 0; i < spritesortcnt; i++) {
			SPRITE pTSprite = tsprite[i];
			if (pTSprite.owner == -1)
				continue;

			if ((pTSprite.picnum == 338 || pTSprite.picnum == 350) && (pTSprite.cstat & 128) == 0) { // Torch bouncing fix
				pTSprite.cstat |= 128;
				pTSprite.z -= ((engine.getTile(pTSprite.picnum).getHeight() * pTSprite.yrepeat) << 1);
			}

			if (nSnakeCam >= 0) {
				short enemy = SnakeList[nSnakeCam].nTarget;
				if (enemy > -1 && pTSprite.owner == enemy && (totalmoves & 1) == 0) {
					pTSprite.pal = 5;
				}
			}

			ILoc oldLoc = game.pInt.getsprinterpolate(pTSprite.owner);
			if (oldLoc != null) {
				int x = oldLoc.x;
				int y = oldLoc.y;
				int z = oldLoc.z;
				short nAngle = oldLoc.ang;

				// interpolate sprite position
				x += mulscale(pTSprite.x - oldLoc.x, smoothratio, 16);
				y += mulscale(pTSprite.y - oldLoc.y, smoothratio, 16);
				z += mulscale(pTSprite.z - oldLoc.z, smoothratio, 16);
				nAngle += mulscale(((pTSprite.ang - oldLoc.ang + 1024) & 0x7FF) - 1024, smoothratio, 16);

				pTSprite.x = x;
				pTSprite.y = y;
				pTSprite.z = z;
				pTSprite.ang = nAngle;
			}

			if (isValidSector(pTSprite.sectnum)) {
				int shade = (sector[pTSprite.sectnum].floorshade + 10);
				if ((sector[pTSprite.sectnum].ceilingstat & 1) != 0)
					shade = (sector[pTSprite.sectnum].ceilingshade + 10);
				pTSprite.shade = (byte) BClipRange(pTSprite.shade + shade, -127, 127);
			}
		}

		int nPlayer = PlayerList[nLocalPlayer].spriteId;
		int v29 = 20;
		int v31 = 30000;

		SPRITE pPlayer = sprite[nPlayer];
		besttarget = -1;

		int px = pPlayer.x;
		int py = pPlayer.y;
		int pz = pPlayer.z - GetSpriteHeight(nPlayer) / 2;
		short nSector = pPlayer.sectnum;
		int pang = (2048 - pPlayer.ang) & 0x7FF;

		int spr = spritesortcnt;
		while (--spr >= 0) {
			SPRITE pTSprite = tsprite[spr];
			short nOwner = pTSprite.owner;
			SPRITE pSprite = sprite[nOwner];

			if (!bCamera && (nOwner == nPlayer || nOwner == nDoppleSprite[nLocalPlayer])) {
				pTSprite.owner = -1;
				continue;
			}

			if (pSprite.statnum > 0) {
				SignalRun((pSprite.lotag - 1), 0x90000 | spr);
				if (pSprite.statnum < 150 && (pSprite.cstat & 0x101) != 0 && nOwner != nPlayer) {
					int dx = pSprite.x - px;
					int dy = pSprite.y - py;
					short cos = sintable[(pang + 512) & 0x7FF];
					short sin = sintable[pang];

					int v17 = klabs((cos * dx - sin * dy) >> 14);
					if (v17 != 0) {
						int v20 = 32 * klabs((sin * dx + dy * cos) >> 14) / v17;
						if (v17 >= 1000 || v17 >= v31 || v20 >= 10) {
							if (v17 < 30000) {
								int v22 = v29 - v20;
								if (v22 > 3 || v17 < v31 && klabs(v22) < 5) {
									v29 = v20;
									v31 = v17;
									besttarget = nOwner;
								}
							}
						} else {
							v29 = v20;
							v31 = v17;
							besttarget = nOwner;
						}
					}
				}
			}
		}

		if (besttarget != -1) {
			nCreepyTimer = nCreepyTime;
			if (!engine.cansee(px, py, pz, nSector, sprite[besttarget].x, sprite[besttarget].y,
					sprite[besttarget].z - GetSpriteHeight(besttarget), sprite[besttarget].sectnum))
				besttarget = -1;
		}
	}

	private static char[] statbuffer = new char[80];

	public static void viewDrawStats(int x, int y, int zoom) {
		if (cfg.gShowStat == 0 || cfg.gShowStat == 2 && nOverhead == 0)
			return;

		float viewzoom = (zoom / 65536.0f);
		BuildFont f = game.getFont(2);

		buildString(statbuffer, 0, "K: ");
		int alignx = f.getWidth(statbuffer);

		int yoffset = (int) (2.5f * f.getHeight() * viewzoom);
		y -= yoffset;

		int statx = x;
		int staty = y;

		f.drawText(statx, staty, statbuffer, zoom, 0, 20, TextAlign.Left, 10 | 256, true);

		int offs = Bitoa((nCreaturesMax - nCreaturesLeft), statbuffer);
		offs = buildString(statbuffer, offs, " / ", nCreaturesMax);
		f.drawText(statx += (alignx + 2) * viewzoom, staty, statbuffer, zoom, 0, 15, TextAlign.Left, 10 | 256, true);

		statx = x;
		staty = y + (int) (8 * viewzoom);

		buildString(statbuffer, 0, "T: ");
		f.drawText(statx, staty, statbuffer, zoom, 0, 20, TextAlign.Left, 10 | 256, true);
		alignx = f.getWidth(statbuffer);

		int sec = (totalmoves / 30) % 60;
		int minutes = (totalmoves / (30 * 60)) % 60;
		int hours = (totalmoves / (30 * 3600)) % 60;

		offs = Bitoa(hours, statbuffer, 2);
		offs = buildString(statbuffer, offs, ":", minutes, 2);
		offs = buildString(statbuffer, offs, ":", sec, 2);
		f.drawText(statx += (alignx + 2) * viewzoom, staty, statbuffer, zoom, 0, 15, TextAlign.Left, 10 | 256, true);
	}
}
