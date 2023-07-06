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

import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Blood.LEVELS.foundSecret;
import static ru.m210projects.Blood.LEVELS.kills;
import static ru.m210projects.Blood.LEVELS.superSecrets;
import static ru.m210projects.Blood.LEVELS.totalKills;
import static ru.m210projects.Blood.LEVELS.totalSecrets;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Mirror.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.QAV.*;
import static ru.m210projects.Blood.SECTORFX.DoSectorLighting;
import static ru.m210projects.Blood.Screen.*;
import static ru.m210projects.Blood.Strings.killsstat;
import static ru.m210projects.Blood.Strings.of;
import static ru.m210projects.Blood.Strings.qav;
import static ru.m210projects.Blood.Strings.secrets;
import static ru.m210projects.Blood.Strings.supersecret;
import static ru.m210projects.Blood.Tile.shadeTable;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.GetOctant;
import static ru.m210projects.Blood.Trig.RotateVector;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trig.rotated;
import static ru.m210projects.Blood.VERSION.SHAREWARE;
import static ru.m210projects.Blood.Warp.checkWarping;
import static ru.m210projects.Blood.Warp.checkWs;
import static ru.m210projects.Blood.Warp.checkWx;
import static ru.m210projects.Blood.Warp.checkWy;
import static ru.m210projects.Blood.Warp.checkWz;
import static ru.m210projects.Blood.Warp.gLowerLink;
import static ru.m210projects.Blood.Warp.gUpperLink;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.util.Arrays;

import ru.m210projects.Blood.Factory.BloodNetwork;
import ru.m210projects.Blood.Menus.MenuInterfaceSet;
import ru.m210projects.Blood.Types.BURN;
import ru.m210projects.Blood.Types.BloodTile;
import ru.m210projects.Blood.Types.BloodTile.ViewType;
import ru.m210projects.Blood.Types.HANDANIM;
import ru.m210projects.Blood.Types.PLOCATION;
import ru.m210projects.Blood.Types.POSTURE;
import ru.m210projects.Blood.Types.QUOTE;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.Hud.AltHud;
import ru.m210projects.Blood.Types.Hud.FullHud;
import ru.m210projects.Blood.Types.Hud.HudRenderer;
import ru.m210projects.Blood.Types.Hud.HudScaledRenderer;
import ru.m210projects.Blood.Types.Hud.MiniHud;
import ru.m210projects.Blood.Types.Hud.SplitHud;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Build.Types.Tile;

import com.badlogic.gdx.math.Vector3;

public class View {

	protected static HudRenderer[] hudlist; // HudScaledRenderer
	protected static HudRenderer splitHud;

	public static int smoothratio;
	public static int viewWeaponX, viewWeaponY;
	public static int viewCrossX, viewCrossY, viewCrossZoom;
	public static int gPlayerIndex;

	private static int nextTime;
	public static int nextY;
	private static final int kMaxQuotes = 16;
	public static int numQuotes;
	private static int hideQuotes;
	private static int totalQuotes;
	private static final QUOTE[] quotes = new QUOTE[kMaxQuotes];
	public static int yOffset = 11;
	public static int kMapZoom = 1024;
	private static int viewThirdDist = -1;
	private static int viewThirdClock = 0;

	private static final char[] number_buffer = new char[256];

	public static int PaletteView = kPalNormal;

	public static final int kStatBarHeight = 25;
	private static final int kLensSize = 80;
	private static byte[] lensTable;

	private static final byte[] otherMirrorGotpic = new byte[8]; // mirror gotpics for crystal ball
	private static final byte[] bakMirrorGotpic = new byte[8]; // backup for mirror gotpics

	public static final int kView2D = 2;
	public static final int kView3D = 3;
	public static final int kView2DIcon = 4;
	public static final int kViewPosCenter = 0;

	public static int gViewIndex = 0;
	public static int gViewPos = kViewPosCenter;
	public static int gViewMode = kView3D;

	public static int deliriumTilt = 0;
	public static int deliriumTurn = 0;
	public static int deliriumPitch = 0;

	private static final int kViewEffectShadow = 0;
	private static final int kViewEffectFlareHalo = 1;
	private static final int kViewEffectCeilGlow = 2;
	private static final int kViewEffectFloorGlow = 3;
	private static final int kViewEffectTorchHigh = 4;
	private static final int kViewEffectTorchLow = 5;
	private static final int kViewEffectSmokeHigh = 6;
	private static final int kViewEffectSmokeLow = 7;
	private static final int kViewEffectFlame = 8;
	private static final int kViewEffectSpear = 9;
	private static final int kViewEffectTrail = 10;
	private static final int kViewEffectPhase = 11;
	private static final int kViewEffectShowWeapon = 12;
	private static final int kViewEffectReflectiveBall = 13;
	private static final int kViewEffectShoot = 14;
	private static final int kViewEffectTesla = 15;
	private static final int kViewEffectFlag = 16;
	private static final int kViewEffectBigFlag = 17;
	private static final int kViewEffectAtom = 18;
	private static final int kViewEffectMax = 19;

	public static int scrollX, scrollOX;
	public static int scrollY, scrollOY;
	public static short scrollAng, scrollOAng;

	public static HANDANIM viewHandAnim;

	public static int gViewX0, gViewY0, gViewX1, gViewY1;
	public static int gViewX0Scaled;
	public static int gViewX1Scaled;
	public static int gViewY0Scaled;
	public static int gViewY1Scaled;

	private static final short[] viewWeaponTile = { -1, -1, 524, 559, 558, 526, 589, 618, 539, 800, 525, 811, 810, -1 };

	private static final int kViewDistance = (80 << 4);
	private static int othercameradist = kViewDistance, othercameraclock = 0;

	private static final int TA_LEFT = 0;
	private static final int TA_CENTER = 1;

	private static final int kDrawYFlip = 0x0004;
	// viewDrawSprite specific flags
	private static final int kDrawXFlip = 0x0800;

	private static final int[] gEffectDetail = { 0, 4, 4, 4, 0, 0, 0, 0, 0, 1, 4, 4, 0, 0, 0, 1, 0, 0, 0 };

	private static final Vector3[] atomEffectVelocity = new Vector3[16];

	public static void viewHandInit() {
		viewHandAnim = new HANDANIM();
		Resource hQAV = BuildGdx.cache.open(518, qav);
		if (hQAV == null) {
			Console.Println("Could not load QAVID 518", OSDTEXT_RED);
			SHAREWARE = true;
			return;
		}

		viewHandAnim.pQAV = new QAV(hQAV);
		viewHandAnim.duration = viewHandAnim.pQAV.duration;
		viewHandAnim.clock = totalclock;
		hQAV.close();
	}

	public static void resetQuotes() {
		numQuotes = 0;
		totalQuotes = 0;
		hideQuotes = 0;
	}

	public static void viewResizeView(int size) {
		gViewX0Scaled = (xdim << 16) / 320;
		gViewY0Scaled = (ydim << 16) / 200;
		gViewX1Scaled = (320 << 16) / xdim;
		gViewY1Scaled = (200 << 16) / ydim;

		cfg.gViewSize = ClipRange(size, 0, hudlist.length - 1);
		if (cfg.gViewSize > (hudlist.length - 1)) {
			gViewX0 = 0;
			gViewY0 = 0;
			gViewX1 = xdim - 1;
			gViewY1 = ydim - 1 - scale(kStatBarHeight, ydim, 200);

			int vsiz = cfg.gViewSize - (hudlist.length - 1);

			gViewX0 += vsiz * xdim / 16;
			gViewX1 -= vsiz * xdim / 16;

			gViewY0 += vsiz * ydim / 16;
			gViewY1 -= vsiz * ydim / 16;

			int dView = (gViewY1 - gViewY0) * vsiz;

			gViewY0 += dView / 16;
			gViewY1 -= dView / 16;

			engine.setview(gViewX0, gViewY0, gViewX1, gViewY1);
		} else {
			// full screen mode
			gViewX0 = 0;
			gViewY0 = 0;
			gViewX1 = xdim - 1;
			gViewY1 = ydim - 1;

			engine.setview(gViewX0, gViewY0, gViewX1, gViewY1);
		}
	}

	public static QUOTE viewSetMessage(String message, int nPlayer) {
		return viewSetMessage(message, nPlayer, 0);
	}

	public static QUOTE viewSetMessage(String message, int nPlayer, int pal) {
		if (message.isEmpty())
			return null;
//		if ( field_21 ) // == 15
		{
			QUOTE quote = quotes[totalQuotes];
			quote.messageText = message;
			if (nPlayer != -1 && nPlayer != myconnectindex)
				quote.pal = gPlayer[nPlayer].pSprite.pal;
			else
				quote.pal = pal;

			Console.Println(message, pal);

			quote.messageTime = kTimerRate * cfg.quoteTime + gFrameClock;

			totalQuotes += 1;
			totalQuotes %= kMaxQuotes;
			numQuotes += 1;
			if (numQuotes > cfg.showQuotes) {
				hideQuotes += 1;
				hideQuotes %= kMaxQuotes;
				nextTime = 0;
				numQuotes = cfg.showQuotes;
				nextY = yOffset;
			}
			return quote;
		}
	}

	public static void InitBallBuffer() {
		byte[] pLens = engine.allocatepermanenttile(BALLBUFFER2, kLensSize, kLensSize); // tileAllocTile
		Arrays.fill(pLens, (byte) 0xFF); // clear to mask color
	}

	public static void viewInit() {
		Console.Println("Initializing status bar", 0);

		hudlist = new HudRenderer[] { null, new MiniHud(), new AltHud(), new FullHud(), };
		splitHud = new SplitHud();

		viewResizeView(cfg.gViewSize);

		for (int i = 0; i < kMaxQuotes; i++) {
			quotes[i] = new QUOTE();
		}

		lensTable = BuildGdx.cache.getBytes("LENS.DAT", 0);
		if (lensTable == null)
			game.dassert("lens.dat == null");
		if (lensTable.length != kLensSize * kLensSize * 4)
			game.dassert("gSysRes.Size(hLens) != kLensSize * kLensSize * sizeof(int)");

		InitBallBuffer();

		for (int i = 0; i < 16; i++) {
			atomEffectVelocity[i] = new Vector3();
			atomEffectVelocity[i].x = Random(2048);
			atomEffectVelocity[i].y = Random(2048);
			atomEffectVelocity[i].z = Random(2048);
		}
	}

	public static void viewUpdatePlayerLoc(PLAYER pPlayer) {
		POSTURE cp = gPosture[pPlayer.nLifeMode][pPlayer.moveState];

		pPlayer.viewOffZ = pPlayer.pSprite.z - cp.viewSpeed;
		pPlayer.weaponAboveZ = pPlayer.pSprite.z - cp.weapSpeed;
		viewBackupView(pPlayer.nPlayer);
	}

	public static void viewBackupView(int nPlayer) {
		SPRITE pSprite = gPlayer[nPlayer].pSprite;
		PLOCATION pPLocation = gPrevView[nPlayer];
		pPLocation.x = pSprite.x;
		pPLocation.y = pSprite.y;
		pPLocation.ang = gPlayer[nPlayer].ang;
		pPLocation.horiz = gPlayer[nPlayer].horiz;
		pPLocation.horizOff = gPlayer[nPlayer].horizOff;
		pPLocation.slope = gPlayer[nPlayer].slope;

		pPLocation.viewOffZ = gPlayer[nPlayer].viewOffZ;
		pPLocation.weapOffZ = gPlayer[nPlayer].weaponAboveZ - gPlayer[nPlayer].viewOffZ - 3072;

		pPLocation.bobHeight = gPlayer[nPlayer].bobHeight;
		pPLocation.bobWidth = gPlayer[nPlayer].bobWidth;
		pPLocation.swayHeight = gPlayer[nPlayer].swayHeight;
		pPLocation.swayWidth = gPlayer[nPlayer].swayWidth;
	}

	public static void viewDrawSplitHUD(PLAYER gView) {
		splitHud.draw(gView, 0, 0);
	}

	private static boolean showItem(int item) {
		switch (item + kItemBase) {
		case kItemFeatherFall:
		case kItemLtdInvisibility:
		case kItemInvulnerability:
		case kItemRavenFlight:
		case kItemGunsAkimbo:
		case kItemGasMask:
		case kItemClone:
		case kItemDecoy:
		case kItemDoppleganger:
		case kItemReflectiveShots:
		case kItemShadowCloak:
		case kItemShroomRage:
		case kItemShroomDelirium:
		case kItemShroomGrow:
		case kItemShroomShrink:
		case kItemDeathMask:
		case kItemAsbestosArmor:
			return true;
		}
		return false;
	}

	public static void viewDrawHUD(PLAYER gView) {
		if (cfg.gViewSize >= 0 && cfg.gViewSize < hudlist.length && hudlist[cfg.gViewSize] != null) {
			// hudlist[cfg.gViewSize].setScale(cfg.gHudSize);
			hudlist[cfg.gViewSize].draw(gView, 0, 0);
		}

		int fx, posy = 20;
		for (int i = 0; i < kMaxPowerUps; i++) {
			if (powerupCheck(gView, i) > 0 && showItem(i)) {
				Tile pic = engine.getTile(gItemInfo[i].picnum);

				DrawStatSprite(gItemInfo[i].picnum, 280
						- engine.getTile(gItemInfo[i].picnum + engine.animateoffs(gItemInfo[i].picnum, 0)).getWidth()
								/ 4,
						posy - (pic.getHeight() / 4), 0, 0, 512 | 16, 32768);
				fx = viewDrawNumber(3, (gView.powerUpTimer[i] * 100) / Math.max(gPowerUpInfo[i].addPower, 1), 276,
						posy + (pic.getHeight() / 4) + 4, 65535, 32, 0, TextAlign.Center, 512, false);
				game.getFont(3).drawText(277 + fx, posy + (pic.getHeight() / 4) + 4, "%", 65535, 24, 0,
						TextAlign.Center, 2 | 512, true);
				posy += 40;
			}
		}

//		debugView(10, 30);

		if (pGameInfo.nGameType == kNetModeOff)
			return;

		if (pGameInfo.nGameType == kNetModeTeams) {
			if (nTeamClock[0] == 0 || (totalclock & 8) != 0) {
				game.getFont(0).drawText(1, 1, toCharArray("Blue"), -128, 10, TextAlign.Left, 2 | 256, false);
				nTeamClock[0] = ClipLow(nTeamClock[0] - kFrameTicks, 0);
				viewDrawNumber(0, nTeamCount[0], 1, 11, 65536, -128, 10, TextAlign.Left, 256, false);
			}

			if (nTeamClock[1] == 0 || (totalclock & 8) != 0) {
				game.getFont(0).drawText(319, 1, toCharArray("Red"), -128, 7, TextAlign.Right, 2 | 512, false);
				nTeamClock[1] = ClipLow(nTeamClock[1] - kFrameTicks, 0);
				viewDrawNumber(0, nTeamCount[1], 319, 11, 65536, -128, 7, TextAlign.Right, 512, false);
			}
		} else
			viewNetPlayers(0, true);

		if (gView != gMe) {
			final String viewed = "View from ";
			System.arraycopy(viewed.toCharArray(), 0, number_buffer, 0, viewed.length());
			int len = game.net.gProfile[gView.nPlayer].name.length();
			System.arraycopy(game.net.gProfile[gView.nPlayer].name.toCharArray(), 0, number_buffer, viewed.length(),
					len);
			number_buffer[viewed.length() + len] = 0;
			int shade = 32 - (totalclock & 0x3F);
			game.getFont(0).drawText(160, 150, number_buffer, shade, 0, TextAlign.Center, 2, false);
		}

		// ----------End Draw HUD
	}

	public static void viewNetPlayers(int yoffset, boolean showpalette) {
		int row = (numplayers - 1) / 4;
		if (row >= 0) {
			if (yoffset > 0)
				yoffset -= 9 * row;

			Tile pic = engine.getTile(2229);
			for (int r = 0; r <= row; r++)
				for (int i = 0; i < 4; i++)
					DrawStatSprite(2229, 80 * i + 40, (9 * r + 8) - pic.getHeight() / 2 + yoffset, 16, 0, 10, 65536);

			int plu = (gPlayer[myconnectindex].teamID & 3) + 11;
			if (plu == 13)
				plu = 4; // green
			if (!showpalette)
				plu = 0;

			if (game.isCurrentScreen(gGameScreen) && pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop
					&& gMe.pXsprite.health <= 0) {
				int shade = 32 - (totalclock & 0x3F);
				DrawStatSprite(2229, 0, yoffset, shade, 2, 10 | 16, 65536);
			}

			game.getFont(4).drawText(4, yoffset + 1, toCharArray(game.net.gProfile[myconnectindex].name), -128, plu,
					TextAlign.Left, 2, false);
			viewDrawNumber(4, gPlayer[myconnectindex].fragCount, 76, yoffset + 1, 65536, -128, plu, TextAlign.Right, 0,
					false);

			int p = 0;
			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				if (i == myconnectindex)
					continue;
				p++;
				int posx = 80 * (p & 3);
				int posy = 9 * (p / 4);
				plu = (gPlayer[i].teamID & 3) + 11;
				if (plu == 13)
					plu = 4; // green
				if (!showpalette)
					plu = 0;

				if (game.isCurrentScreen(gGameScreen) && pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop
						&& gPlayer[i].pXsprite.health <= 0) {
					int shade = 32 - (totalclock & 0x3F);
					DrawStatSprite(2229, posx, posy + yoffset, shade, 2, 10 | 16, 65536);
				}

				game.getFont(4).drawText(posx + 4, posy + yoffset + 1, toCharArray(game.net.gProfile[i].name), -128,
						plu, TextAlign.Left, 2, false);
				viewDrawNumber(4, gPlayer[i].fragCount, posx + 76, posy + yoffset + 1, 65536, -128, plu,
						TextAlign.Right, 0, false);
			}
		}
	}

	private static int lastDacUpdate;

	public static void viewPaletteHandler(PLAYER gView) {
		int vPalette = kPalNormal;
		lastDacUpdate = totalclock;

		if (powerupCheck(gView, kItemInvulnerability - kItemBase) > 0)
			vPalette = kPalInvuln1;
		else if (powerupCheck(gView, kItemReflectiveShots - kItemBase) > 0)
			vPalette = kPalWater;
		else if (gView.Underwater) {
			vPalette = gView.pXsprite.palette;
			// Can be removed
        } else if (gView.nLifeMode == 1)
			vPalette = kPalBeast;

		if (vPalette != PaletteView) {
			scrSetPalette(vPalette);
			PaletteView = vPalette;
		}

		if (engine.getrender().getType().getFrameType() == FrameType.GL)
			scrGLSetDac(totalclock - lastDacUpdate);
		else
			scrSetDac(totalclock - lastDacUpdate);
		lastDacUpdate = totalclock;
	}

	public static void viewDrawScreen(int gViewIndex, int smooth) {
		PLAYER gView = gPlayer[gViewIndex];
		if (gView == null || gView.pSprite == null) { // try switch to own screen
			gViewIndex = View.gViewIndex = myconnectindex;
			gView = gPlayer[gViewIndex];
		}

		if (gView == null || gView.pSprite == null)
			return;

		smoothratio = smooth;
		gPlayerIndex = -1;

		if (!gMapScrollMode) {
			scrollOX = scrollX = gView.pSprite.x;
			scrollOY = scrollY = gView.pSprite.y;
			scrollOAng = scrollAng = gView.pSprite.ang;
		}

		if ((gViewMode == kView3D) || gViewMode == kView2D) {
			DoSectorLighting();

			long x = gView.pSprite.x;
			long y = gView.pSprite.y;
			long z = gView.viewOffZ;

			float nAngle = gView.ang;
			float nHoriz = gView.horiz;
			int bobWidth = gView.bobWidth;
			int bobHeight = gView.bobHeight;
			int swayWidth = gView.swayWidth;
			int swayHeight = gView.swayHeight;
			int nSlope = gView.slope;

			short nSector = gView.pSprite.sectnum;
			if (nSector == -1)
				return;

			int weapOffZ = gView.weaponAboveZ - gView.viewOffZ - 3072;
			if (cfg.gInterpolation != 0
					&& ((!game.menu.gShowMenu && !Console.IsShown()) || game.isCurrentScreen(gDemoScreen))) {
				if (numplayers > 1 && gView == gMe && gMe.pXsprite.health != 0) {
					BloodNetwork net = (BloodNetwork) game.pNet;

//					weapOffZ = net.predict.weaponAboveZ - net.predict.viewOffZ - 3072;
					nSector = net.predict.sectnum;
					x = net.predictOld.x + mulscale(net.predict.x - net.predictOld.x, smoothratio, 16);
					y = net.predictOld.y + mulscale(net.predict.y - net.predictOld.y, smoothratio, 16);
					z = net.predictOld.viewOffZ
							+ mulscale(net.predict.viewOffZ - net.predictOld.viewOffZ, smoothratio, 16);
					weapOffZ = net.predictOld.weapOffZ
							+ mulscale(net.predict.weapOffZ - net.predictOld.weapOffZ, smoothratio, 16);
					nHoriz = net.predictOld.horiz
							+ ((net.predict.horiz - net.predictOld.horiz) * smoothratio) / 65536.0f;
					nAngle = net.predictOld.ang
							+ ((BClampAngle(net.predict.ang - net.predictOld.ang + kAngle180) - kAngle180)
									* smoothratio) / 65536.0f;
					bobWidth = net.predictOld.bobWidth
							+ mulscale(net.predict.bobWidth - net.predictOld.bobWidth, smoothratio, 16);
					bobHeight = net.predictOld.bobHeight
							+ mulscale(net.predict.bobHeight - net.predictOld.bobHeight, smoothratio, 16);
					swayWidth = net.predictOld.swayWidth
							+ mulscale(net.predict.swayWidth - net.predictOld.swayWidth, smoothratio, 16);
					swayHeight = net.predictOld.swayHeight
							+ mulscale(net.predict.swayHeight - net.predictOld.swayHeight, smoothratio, 16);
					nSlope = net.predictOld.slope + mulscale(net.predict.slope - net.predictOld.slope, smoothratio, 16);
				} else {
					x = gPrevView[gViewIndex].x + mulscale(x - gPrevView[gViewIndex].x, smoothratio, 16);
					y = gPrevView[gViewIndex].y + mulscale(y - gPrevView[gViewIndex].y, smoothratio, 16);
					z = gPrevView[gViewIndex].viewOffZ + mulscale(z - gPrevView[gViewIndex].viewOffZ, smoothratio, 16);
					weapOffZ = gPrevView[gViewIndex].weapOffZ
							+ mulscale(weapOffZ - gPrevView[gViewIndex].weapOffZ, smoothratio, 16);
					nHoriz = gPrevView[gViewIndex].horiz
							+ ((nHoriz - gPrevView[gViewIndex].horiz) * smoothratio) / 65536.0f;
					nAngle = gPrevView[gViewIndex].ang
							+ ((BClampAngle(nAngle - gPrevView[gViewIndex].ang + kAngle180) - kAngle180) * smoothratio)
									/ 65536.0f;
					bobWidth = gPrevView[gViewIndex].bobWidth
							+ mulscale(bobWidth - gPrevView[gViewIndex].bobWidth, smoothratio, 16);
					bobHeight = gPrevView[gViewIndex].bobHeight
							+ mulscale(bobHeight - gPrevView[gViewIndex].bobHeight, smoothratio, 16);
					swayWidth = gPrevView[gViewIndex].swayWidth
							+ mulscale(swayWidth - gPrevView[gViewIndex].swayWidth, smoothratio, 16);
					swayHeight = gPrevView[gViewIndex].swayHeight
							+ mulscale(swayHeight - gPrevView[gViewIndex].swayHeight, smoothratio, 16);
					nSlope = gPrevView[gViewIndex].slope
							+ mulscale(nSlope - gPrevView[gViewIndex].slope, smoothratio, 16);
				}
			}

			if (gView.explosion != 0) {
				int explCount = ClipRange(gView.explosion * 8, 0, 2000);
				x += ViRandom(explCount >> 4);
				y += ViRandom(explCount >> 4);
				z += ViRandom(explCount);

				nHoriz += ViRandom(explCount >> 8);
				nAngle += ViRandom(explCount >> 8);

				swayWidth += ViRandom(explCount);
				swayHeight += ViRandom(explCount);
			}

			if (gView.quakeTime != 0) {
				int explCount = ClipRange(gView.quakeTime * 8, 0, 2000);
				x += ViRandom(explCount >> 4);
				y += ViRandom(explCount >> 4);
				z += ViRandom(explCount);

				nHoriz += ViRandom(explCount >> 8);
				nAngle += ViRandom(explCount >> 8);

				swayWidth += ViRandom(explCount);
				swayHeight += ViRandom(explCount);
			}

			nHoriz += mulscale(30, (0x40000000L - Cos(4 * gView.tilt)), 30);
			nAngle += gView.lookang;

			if (gViewPos == kViewPosCenter) {
				if (cfg.gBobWidth) {
					x -= bobWidth * BSinAngle(nAngle) / 261568.0f;
					y += bobWidth * BCosAngle(nAngle) / 261568.0f;
				}
				if (cfg.gBobHeight)
					z += bobHeight;

				if (game.net.gProfile[myconnectindex].slopetilt)
					nHoriz += nSlope;

				z += 10 * nHoriz;
				viewThirdDist = -1;
				viewThirdClock = totalclock;
			} else {
				float dx = (float) (kViewDistance * -BCosAngle(nAngle) / 16384.0f);
				float dy = (float) (kViewDistance * -BSinAngle(nAngle) / 16384.0f);

				int dz = 160 * (int) nHoriz - (16 << 8);
				short oldcstat = gView.pSprite.cstat;
				gView.pSprite.cstat &= ~kSpriteHitscan;

				if (!(nSector >= 0 && nSector < kMaxSectors))
					game.dassert("nSector >= 0 && nSector < kMaxSectors");
				FindSector((int) x, (int) y, (int) z, nSector);
				nSector = foundSector;

				hitscangoalx = 0x1FFFFFFF;
				hitscangoaly = 0x1FFFFFFF;
				engine.hitscan((int) x, (int) y, (int) z, nSector, (int) dx, (int) dy, dz, pHitInfo, 16777280);

				int hx = (int) (pHitInfo.hitx - x);
				int hy = (int) (pHitInfo.hity - y);

                if ((klabs(hx) + klabs(hy)) - (Math.abs(dx) + Math.abs(dy)) < 1024) {
					nSector = pHitInfo.hitsect;

					int wx = 1;
					if (dx < 0)
						wx = -1;
					int wy = 1;
					if (dy < 0)
						wy = -1;

					hx -= wx << 9;
					hy -= wy << 9;

					int dist = 0;
					if (dx != 0 && dy != 0) {
						if (Math.abs(dx) <= Math.abs(dy))
							dist = (int) ClipHigh((hy << 16) / dy, viewThirdDist);
						else
							dist = (int) ClipHigh((hx << 16) / dx, viewThirdDist);
					}
					viewThirdDist = dist;
				}

				x += mulscale(viewThirdDist, (int) dx, 16);
				y += mulscale(viewThirdDist, (int) dy, 16);
				z += mulscale(viewThirdDist, dz, 16);

				viewThirdDist = ClipHigh(viewThirdDist + ((totalclock - viewThirdClock) << 10), 65536);
				viewThirdClock = totalclock;
				if (!(nSector >= 0 && nSector < kMaxSectors))
					game.dassert("nSector >= 0 && nSector < kMaxSectors");
				FindSector((int) x, (int) y, (int) z, nSector);
				nSector = foundSector;
				gView.pSprite.cstat = oldcstat;
			}

			checkWarping(x, y, z, nSector);
			x = checkWx;
			y = checkWy;
			z = checkWz;
			nSector = checkWs;

			GLRenderer gl = engine.glrender();
			boolean bDelirious = (powerupCheck(gView, kItemShroomDelirium - kItemBase) > 0);
			if (deliriumTilt != 0 || bDelirious) {
				if (gl != null)
					gl.setdrunk(deliriumTilt);
				else {
					Tile pic = engine.getTile(TILTBUFFER);

					if (pic.data == null || pic.getWidth() == 0 || pic.getHeight() == 0)
						engine.allocatepermanenttile(TILTBUFFER, 320, 320);
					engine.setviewtotile(TILTBUFFER, 320, 320);

					int tilt = (deliriumTilt & 511);
					if (tilt > 256)
						tilt = 512 - tilt;
					engine.setaspect(dmulscale(256000, Cos(tilt), 160000, Sin(tilt), 32), yxaspect);
				}
			} else {
				if (gl != null)
					gl.setdrunk(0);
				if ((powerupCheck(gView, kItemCrystalBall - kItemBase) > 0) && (numplayers > 1
						|| (game.isCurrentScreen(gGameScreen) && kFakeMultiplayer && nFakePlayers > 1))) {
					int nP = numplayers;
					if (kFakeMultiplayer)
						nP = nFakePlayers;

					int nViewed, nEyes = (totalclock / (2 * kTimerRate)) % (nP - 1);
					if (!kFakeMultiplayer) {
						nViewed = connecthead;
						while (true) {
							if (nViewed == gViewIndex)
								nViewed = connectpoint2[nViewed];

							if (nEyes == 0)
								break;

							nViewed = connectpoint2[nViewed];
							nEyes--;
						}
					} else
						nViewed = nEyes + 1;

					PLAYER pViewed = gPlayer[nViewed];
					Tile pic = engine.getTile(BALLBUFFER);

					if (pic.data == null || pic.getWidth() == 0 || pic.getHeight() == 0)
						engine.allocatepermanenttile(BALLBUFFER, 128, 128);

					engine.setviewtotile(BALLBUFFER, 128, 128);
					engine.setaspect(0x10000, 0x13333);

					int cx = pViewed.pSprite.x;
					int cy = pViewed.pSprite.y;
					int cz = pViewed.viewOffZ;
					short cnSector = pViewed.pSprite.sectnum;
					int cnAngle = pViewed.pSprite.ang;
					int cnHoriz = 0;

					if (pViewed.explosion != 0) {
						int explCount = ClipRange(gView.explosion * 8, 0, 2000);
						cx += ViRandom(explCount >> 4);
						cy += ViRandom(explCount >> 4);
						cz += ViRandom(explCount);

						cnHoriz += ViRandom(explCount >> 8);
						cnAngle += ViRandom(explCount >> 8);
					}

					if (pViewed.quakeTime != 0) {
						int explCount = ClipRange(gView.quakeTime * 8, 0, 2000);
						cx += ViRandom(explCount >> 4);
						cy += ViRandom(explCount >> 4);
						cz += ViRandom(explCount);

						cnHoriz += ViRandom(explCount >> 8);
						cnAngle += ViRandom(explCount >> 8);
					}

					int nx = mulscale(-Cos(cnAngle), kViewDistance, 30);
					int ny = mulscale(-Sin(cnAngle), kViewDistance, 30);
					int nz = mulscale(cnHoriz, kViewDistance, 3);
					nz -= 16 << 8;

					SPRITE sp = pViewed.pSprite;
					short bakcstat = sp.cstat;
					sp.cstat &= ~kSpriteHitscan;

					FindSector(cx, cy, cz, cnSector);
					cnSector = foundSector;

					engine.hitscan(cx, cy, cz, cnSector, nx, ny, nz, pHitInfo, 16777280);

					int hx = pHitInfo.hitx - cx;
					int hy = pHitInfo.hity - cy;

					if (klabs(nx) + klabs(ny) > klabs(hx) + klabs(hy)) {
						cnSector = pHitInfo.hitsect;

						hx -= ksgn(nx) * 4 << 4;
						hy -= ksgn(ny) * 4 << 4;

						if (klabs(nx) > klabs(ny))
							othercameradist = ClipHigh(othercameradist, divscale(hx, nx, 16));
						else
							othercameradist = ClipHigh(othercameradist, divscale(hy, ny, 16));
					}
					cx += mulscale(nx, othercameradist, 16);
					cy += mulscale(ny, othercameradist, 16);
					cz += mulscale(nz, othercameradist, 16);
					othercameradist += ((totalclock - othercameraclock) << 10);
					if (othercameradist > 65536)
						othercameradist = 65536;

					othercameraclock = totalclock;

					FindSector(cx, cy, cz, cnSector);
					cnSector = foundSector;

					sp.cstat = bakcstat;

					checkWarping(cx, cy, cz, cnSector); // make sure view works through linked sectors
					cx = (int) checkWx;
					cy = (int) checkWy;
					cz = (int) checkWz;
					cnSector = checkWs;

					// backup mirror gotpics
					System.arraycopy(gotpic, MIRRORLABEL >> 3, bakMirrorGotpic, 0, 8);

					// restore other gotpics
					System.arraycopy(otherMirrorGotpic, 0, gotpic, MIRRORLABEL >> 3, 8);

					visibility = ClipLow(gVisibility - pViewed.visibility * 32, 0);

					DrawMirrors(cx, cy, cz, cnAngle, kHorizDefault + cnHoriz);
					engine.drawrooms(cx, cy, cz, cnAngle, kHorizDefault + cnHoriz, cnSector);

					// save other mirror gotpics
					System.arraycopy(gotpic, MIRRORLABEL >> 3, otherMirrorGotpic, 0, 8);

					// restore original gotpics
					System.arraycopy(bakMirrorGotpic, 0, gotpic, MIRRORLABEL >> 3, 8);

					viewProcessSprites(cx, cy, cz);
					engine.drawmasks();
					engine.setviewback();
				} else {
					othercameradist = -1;
					othercameraclock = totalclock;
				}
			}

			int vis = 0;
			for (int i = headspritestat[kStatExplosion]; i >= 0; i = nextspritestat[i]) {
				SPRITE pSprite = sprite[i];
				int nXSprite = pSprite.extra;
				if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
					game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
				int sectnum = pSprite.sectnum;
				if ((gotsector[sectnum >> 3] & pow2char[sectnum & 7]) != 0)
					vis += 32 * xsprite[nXSprite].data3;
			}
			for (int i = headspritestat[kStatMissile]; i >= 0; i = nextspritestat[i]) {
				SPRITE pSprite = sprite[i];
				switch (pSprite.lotag) {
				case kMissileFlare:
				case kMissileAltTesla:
				case kMissileStarburstFlare:
				case kMissileTesla:
					if ((gotsector[pSprite.sectnum >> 3] & pow2char[pSprite.sectnum & 7]) != 0)
						vis += 256;
					break;
				}
			}
			visibility = ClipLow(gVisibility - 32 * gView.visibility - vis, 0);
			nAngle = BClampAngle(deliriumTurn + nAngle);

			int nUpper = gUpperLink[nSector], nLower = gLowerLink[nSector];

			engine.getzsofslope(nSector, (int) x, (int) y, zofslope);
			int lz = 4 << 8;
			long crossDz = 0;
			if (z < zofslope[CEIL] + lz) {
				if (nLower == -1 || (engine.getrender().getType().equals(RenderType.Polymost) // Polymost zNear Plane
																								// tweak
						&& sector[nSector].ceilingpicnum < MIRRORLABEL
						|| sector[nSector].ceilingpicnum >= MIRRORLABEL + MAXMIRRORS)) {
					crossDz = z;
					z = zofslope[CEIL] + lz;
					crossDz -= z;
				}
			}
			if (z > zofslope[FLOOR] - lz) {
				if (nUpper == -1 || (engine.getrender().getType().equals(RenderType.Polymost)
						&& sector[nSector].floorpicnum < MIRRORLABEL
						|| sector[nSector].floorpicnum >= MIRRORLABEL + MAXMIRRORS)) {
					crossDz = z;
					z = zofslope[FLOOR] - lz;
					crossDz -= z;
				}
			}

			nHoriz = BClipRange(nHoriz, -200, 200);
			int defHoriz = kHorizDefault;
			if (newHoriz && !IsOriginalDemo())
				defHoriz = newHorizDefault;

			engine.getrender().settiltang(deliriumTilt);
			DrawMirrors(x, y, z, nAngle, defHoriz + nHoriz + deliriumPitch);
			byte gotfire = gotpic[fire.getPicture() >> 3];
			int oldcstat = gView.pSprite.cstat;
			if (gViewPos == kViewPosCenter)
				gView.pSprite.cstat |= kSpriteInvisible;
			else
				gView.pSprite.cstat |= (kSpriteTranslucent | kSpriteTranslucentR);
			engine.drawrooms(x, y, z, nAngle, defHoriz + nHoriz + deliriumPitch, nSector);
			if ((gotfire & pow2char[fire.getPicture() & 7]) != 0)
				gotpic[fire.getPicture() >> 3] |= pow2char[fire.getPicture() & 7];
			viewProcessSprites(x, y, z);
			setMirrorParalax(true);
			engine.drawmasks();
			setMirrorParalax(false);
			processMirror(x, y);
			gView.pSprite.cstat = (short) oldcstat;

			if (deliriumTilt != 0 || bDelirious) {
				if (gl == null) {
					if (engine.getTile(TILTBUFFER).data == null)
						game.dassert("waloff[ TILTBUFFER ] != null");

					engine.setviewback();

					int nFlags = kRotateScale | kRotateNoMask | 1024;
					if (bDelirious)
						nFlags |= kRotateTranslucent | kRotateTranslucentR;

					if(engine.glrender() == null)
						nFlags |= kRotateYFlip;

					int tilt = (deliriumTilt & 511);
					if (tilt > 256)
						tilt = 512 - tilt;

					engine.rotatesprite(160 << 16, 100 << 16, dmulscale(256000, Cos(tilt), 160000, Sin(tilt), 32),
							deliriumTilt + (engine.glrender() != null ? 0 : 512), TILTBUFFER, 0, kPLUNormal, nFlags, gViewX0, gViewY0, gViewX1, gViewY1);
				}
			}

			if (!bDelirious) {
				deliriumTilt = 0;
				deliriumTurn = 0;
				deliriumPitch = 0;
			}

			if (gViewPos == kViewPosCenter) { // Calc weapon and crosshair coordinates for drawHud()
				if ((!game.menu.gShowMenu || (game.menu.getCurrentMenu() instanceof MenuInterfaceSet))
						&& cfg.gCrosshair) {
					viewCrossX = 160 - (gView.lookang >> 1);
					viewCrossY = 90;
					if (newHoriz && !IsOriginalDemo())
						viewCrossY = 100;
					viewCrossY += (klabs(gView.lookang) / 9);
					viewCrossY += crossDz >> 7;
					int zoom = cfg.gCrossSize;
					if (SplitScreen)
						zoom >>= 1;

					viewCrossX = divscale(viewCrossX, gViewX1Scaled, 16);
					viewCrossY = divscale(viewCrossY, gViewY1Scaled, 16);
					viewCrossZoom = divscale(zoom, gViewY1Scaled, 16);
				}

				viewWeaponX = 160 + (swayWidth >> 8);
				viewWeaponY = 220 + (swayHeight >> 8) + (weapOffZ >> 7);
				if (SplitScreen) {
					if (gViewIndex == 0)
						viewWeaponY = 120 + (swayHeight >> 8) + (weapOffZ >> 7);
				}
			}
		}

    }

	public static void updateviewmap() {
		int i;
		if ((i = gPlayer[gViewIndex].pSprite.sectnum) > -1) {
			int wallid = sector[i].wallptr;
			WALL wal; // = wall[wallid];

			show2dsector[i >> 3] |= (1 << (i & 7));
			for (int j = sector[i].wallnum; j > 0; j--) {
				wal = wall[wallid++];
				i = wal.nextsector;
				if (i < 0)
					continue;
				if ((wal.cstat & 0x0071) != 0)
					continue;
				if (wall[wal.nextwall] != null && (wall[wal.nextwall].cstat & 0x0071) != 0)
					continue;
				if (sector[i] != null && sector[i].ceilingz >= sector[i].floorz)
					continue;
				show2dsector[i >> 3] |= (1 << (i & 7));
			}
		}
	}

	private static final BURN[] burnTable = { new BURN(2101, 2, 0, 118784, 10, 220),
			new BURN(2101, 2, 0, 110592, 40, 220), new BURN(2101, 2, 0, 81920, 85, 220),
			new BURN(2101, 2, 0, 69632, 120, 220), new BURN(2101, 2, 0, 61440, 160, 220),
			new BURN(2101, 2, 0, 73728, 200, 220), new BURN(2101, 2, 0, 77824, 235, 220),
			new BURN(2101, 2, 0, 110592, 275, 220), new BURN(2101, 2, 0, 122880, 310, 220) };

	public static void viewDrawBurn(int burnTime) {
		if (burnTime == 0)
			return;
		for (int i = 0; i < 9; i++) {
			int nTile = burnTable[i].nTile + engine.animateoffs((short) burnTable[i].nTile, i - 32768);
			int nPal = burnTable[i].nPal;
			int nFlags = burnTable[i].nFlags;
			int zoom = burnTable[i].zoom;
			if (burnTime < 600)
				zoom = (burnTime * (zoom / 600));

			viewDrawSprite(burnTable[i].x << 16, burnTable[i].y << 16, zoom, 0, nTile, 0, nPal, nFlags | 1024, windowx1,
					windowy1, windowx2, windowy2);
		}
	}

	public static void viewDisplayMessage(int nPlayer) {
		if (!cfg.MessageState || game.menu.gShowMenu)
			return;

		int x = 0, y = 0;
		int nShade = ClipHigh(numQuotes << 3, 48);

		if (gViewMode == 3) {
			x = mulscale(gViewX0, gViewX1Scaled, 16);
			y = mulscale(gViewY0, gViewY1Scaled, 16);
		}
		y += nextY;

		if (pGameInfo.nGameType != kNetModeOff && pGameInfo.nGameType != kNetModeTeams) {
			int row = (numplayers - 1) / 4;
			y += (row + 1) * 9;
		}
		if (pGameInfo.nGameType == kNetModeTeams)
			y += 22;

		for (int i = 0; i < numQuotes; i++) {
			QUOTE quote = quotes[(i + hideQuotes) % kMaxQuotes];
			if (gFrameClock < quote.messageTime) {
				game.getFont(cfg.MessageFont).drawText(x + 1, y, toCharArray(quote.messageText), nShade, quote.pal,
						TextAlign.Left, 2 | 256, false);
				y += yOffset;
				nShade = ClipLow(nShade - 64 / numQuotes, -128);
			} else {
				numQuotes--;
				hideQuotes += 1;
				hideQuotes %= kMaxQuotes;
			}
		}
		if (nextY != 0) {
			nextY = nextTime * yOffset / kTimerRate;
			nextTime += gTicks;
		}
	}

	public static int viewDrawNumber(int nFontId, int number, int x, int y, int zoom, int shade, int nPLU,
			TextAlign nAlign, int nFlags, boolean textShadow) {
		Bitoa(number, number_buffer);
		return game.getFont(nFontId).drawText(x, y, number_buffer, zoom, shade, nPLU, nAlign, 2 | nFlags, textShadow);
	}

	public static void viewDrawInputText(int nFontId, char[] text, int textlength, int x, int y, int zoom, int shade,
			int nPLU, int nAlign, int nFlags, boolean textShadow) {
		if (nFontId >= 0 && nFontId < 5 && text != null) {
			if (nAlign != TA_LEFT) {
				int nWidth = game.getFont(nFontId).getWidth(text);
				if (nAlign == TA_CENTER)
					nWidth >>= 1;
				x -= nWidth;
			}
			int pos = 0;
			while (pos < textlength && text[pos] != 0) {
				x += game.getFont(nFontId).drawChar(x, y, text[pos], zoom, shade, nPLU, 2 | nFlags, textShadow);
				pos++;
			}
		}
	}

	private static void viewDrawSprite(int sx, int sy, int nZoom, int nAngle, int nTile, int nShade, int nPLU,
			int nFlags, int wx1, int wy1, int wx2, int wy2) {
		// convert x-flipping
		if ((nFlags & kDrawXFlip) != 0) {
			nAngle = (nAngle + kAngle180) & kAngleMask;
			nFlags ^= kDrawYFlip;
		}

		// call engine.rotatesprite passing only compatible bits in nFlags
		engine.rotatesprite(sx, sy, nZoom, (short) nAngle, nTile, nShade, nPLU, nFlags, wx1, wy1, wx2, wy2);
	}

	public static void viewProcessSprites(long nViewX, long nViewY, long nViewZ) {
		PLAYER gView = gPlayer[gViewIndex];
		if (gView == null)
			return;

		int nTSprite;
		short nXSprite;
		long dx, dy;

		if (spritesortcnt > kMaxViewSprites)
			return; // game.dassert("spritesortcnt <= kMaxViewSprites");

		int ospritesortcnt = spritesortcnt;

		int maphack_sprite = -1;
		if (maphack_highlight)
			maphack_sprite = game.getSprite();

		// count down so we don't process shadows
		for (nTSprite = spritesortcnt - 1; nTSprite >= 0; nTSprite--) {
			SPRITE pTSprite = tsprite[nTSprite];
			XSPRITE pTXSprite = null;
			nXSprite = pTSprite.extra;

			if (pTSprite.detail > cfg.gDetail || pTSprite.sectnum == -1) {
				pTSprite.xrepeat = 0;
				continue;
			}

			if (nXSprite > 0)
				pTXSprite = xsprite[nXSprite];

			int nTile = pTSprite.picnum;
			if (nTile < 0 || nTile >= kMaxTiles) {
				System.err.println("tsprite[].cstat = " + pTSprite.cstat);
				System.err.println("tsprite[].shade = " + pTSprite.shade);
				System.err.println("tsprite[].pal = " + pTSprite.pal);
				System.err.println("tsprite[].picnum = " + pTSprite.picnum);
				System.err.println("tsprite[].ang = " + pTSprite.ang);
				System.err.println("tsprite[].owner = " + pTSprite.owner);
				System.err.println("tsprite[].sectnum = " + pTSprite.sectnum);
				System.err.println("tsprite[].statnum = " + pTSprite.statnum);
				System.err.println("tsprite[].type = " + pTSprite.lotag);
				System.err.println("tsprite[].flags = " + pTSprite.hitag);
				System.err.println("tsprite[].extra = " + pTSprite.extra);
				if (!(nTile >= 0 && nTile < kMaxTiles))
					game.dassert("nTile >= 0 && nTile < kMaxTiles");
			}

			// only interpolate certain moving things
			ILoc oldLoc = game.pInt.getsprinterpolate(pTSprite.owner);
			if (cfg.gInterpolation == 1 && oldLoc != null && (pTSprite.hitag & 0x0200) == 0) {
				int x = oldLoc.x;
				int y = oldLoc.y;
				int z = oldLoc.z;
				short nAngle = oldLoc.ang;

				// interpolate sprite position
				x += mulscale(pTSprite.x - oldLoc.x, smoothratio, 16);
				y += mulscale(pTSprite.y - oldLoc.y, smoothratio, 16);
				z += mulscale(pTSprite.z - oldLoc.z, smoothratio, 16);
				nAngle += mulscale(((pTSprite.ang - oldLoc.ang + kAngle180) & kAngleMask) - kAngle180, smoothratio, 16);

				pTSprite.x = x;
				pTSprite.y = y;
				pTSprite.z = z;
				pTSprite.ang = nAngle;
			}

			int nFrames = 0;
			BloodTile pic = engine.getTile(pTSprite.picnum);

			switch (pic.getView()) {
			case kSpriteViewSingle:
				if (nXSprite > 0) {
					if (nXSprite >= kMaxXSprites)
						game.dassert("nXSprite < kMaxXSprites");
					switch (pTSprite.lotag) {
					case kSwitchToggle:
					case kSwitchMomentary:
						if (xsprite[nXSprite].state != 0)
							nFrames = 1;
						break;

					case kSwitchCombination:
						nFrames = xsprite[nXSprite].data1;
						break;

					}
				}
				break;
			case kSpriteView5Full:
				dx = nViewX - pTSprite.x;
				dy = nViewY - pTSprite.y;

				RotateVector(dx, dy, -pTSprite.ang + kAngle45 / 2);
				nFrames = GetOctant((int) rotated.x, (int) rotated.y);

				if (nFrames > 4) {
					nFrames = 8 - nFrames;
					pTSprite.cstat |= kSpriteFlipX;
				} else
					pTSprite.cstat &= ~kSpriteFlipX;
				break;
			case kSpriteView8Full:
				// Calculate which of the 8 angles of the sprite to draw (0-7)
				dx = nViewX - pTSprite.x;
				dy = nViewY - pTSprite.y;

				RotateVector(dx, dy, -pTSprite.ang + kAngle45 / 2);
				nFrames = GetOctant((int) rotated.x, (int) rotated.y);
				break;

			case kSpriteView5Half:
				if (nXSprite <= 0) {
					GetSpriteExtents(pTSprite);
					if (engine.getflorzofslope(pTSprite.sectnum, pTSprite.x, pTSprite.y) > extents_zBot)
						nFrames = 1;
				} else if (gSpriteHit[nXSprite].floorHit == 0)
					nFrames = 1;
				break;
			case kSpriteViewVoxel:
			case kSpriteViewSpinVoxel:
                if (cfg.gDetail >= 4 && (pTSprite.hitag & kAttrRespawn) == 0) {
//		            pTSprite.picnum = gVoxelData[pTSprite.picnum];
					if (pic.getView() == ViewType.kSpriteViewSpinVoxel)
						pTSprite.ang = (short) ((8 * totalclock) & kAngleMask);
				}
				break;

			default:
				break;
			}

			while (nFrames > 0) {
				pTSprite.picnum += engine.getTile(pTSprite.picnum).getFrames() + 1;
				if (pTSprite.picnum >= kMaxTiles - 1)
					break;
				nFrames--;
			}

			int sprshade, shade1, shade2;

			if (pTSprite.sectnum < 0 || pTSprite.sectnum > numsectors)
				continue;

			sprshade = pTSprite.shade;
			SECTOR pSector = sector[pTSprite.sectnum];
			XSECTOR pXSector = null;

			if (pSector.extra > 0)
				pXSector = xsector[pSector.extra];

			if ((pSector.ceilingstat & 1) == 0 || (pSector.floorstat & kSectorFloorShade) != 0) {
				shade1 = pSector.floorshade;
				shade2 = shadeTable[pSector.floorpicnum];
			} else {
				shade1 = pSector.ceilingshade;
				shade2 = shadeTable[pSector.ceilingpicnum];
			}
			sprshade = pTSprite.shade + shade1 + shade2 + ((pTSprite.picnum >= 0) ? shadeTable[pTSprite.picnum] : 0);
			pTSprite.shade = (byte) ClipRange(sprshade, -128, numshades - 1);

			if ((pTSprite.hitag & kAttrRespawn) != 0 && sprite[pTSprite.owner].owner == 3) {
				if (pTXSprite == null)
					game.dassert("pTXSprite != NULL");

				pTSprite.picnum = (short) (2272 + (2 * pTXSprite.respawnPending));
				pTSprite.xrepeat = 48;
				pTSprite.yrepeat = 48;
				pTSprite.shade = -128;
				pTSprite.cstat &= ~(kSpriteTranslucentR + kSpriteTranslucent);

				if ((IsItemSprite(pTSprite) || IsAmmoSprite(pTSprite)) && pGameInfo.nItemSettings == 2
						|| IsWeaponSprite(pTSprite) && pGameInfo.nWeaponSettings == 3)
					pTSprite.yrepeat = 48;
				else
					pTSprite.yrepeat = 0;

				pTSprite.xrepeat = pTSprite.yrepeat;
			}

			if (spritesortcnt < kMaxViewSprites) {
				if (pTXSprite != null && actGetBurnTime(pTXSprite) > 0)
					pTSprite.shade = (byte) ClipRange(pTSprite.shade - mulscale(8, vRandom(), 15) - 16, -128, 127);

				if ((pTSprite.hitag & kAttrSmoke) != 0)
					viewAddEffect(nTSprite, kViewEffectSmokeHigh);

				if (display_mirror) {
//		        	pTSprite.cstat |= kSpriteFlipX;
				}

				if ((pTSprite.hitag & kAttrFlipX) != 0)
					pTSprite.cstat |= kSpriteFlipX;
				if ((pTSprite.hitag & kAttrFlipY) != 0)
					pTSprite.cstat |= kSpriteFlipY;

				switch (pTSprite.statnum) {
				case kStatDefault:
					switch (pTSprite.lotag) {
					case 30:
						if (pTXSprite != null) {
							if ((pTXSprite.state) != 0) {
								++pTSprite.picnum;
								viewAddEffect(nTSprite, kViewEffectTorchHigh);
							} else {
								viewAddEffect(nTSprite, kViewEffectSmokeHigh);
							}
						} else {
							++pTSprite.picnum;
							viewAddEffect(nTSprite, kViewEffectTorchHigh);
						}
						break;
					case 32:
						if (pTXSprite != null && (pTXSprite.state) == 0) {
							pTSprite.shade = -8;
						} else {
							pTSprite.shade = -128;
							viewAddEffect(nTSprite, kViewEffectPhase);
						}
						break;
					default:
						if (pXSector != null) {
							if (pXSector.color)
								pTSprite.pal = (byte) pSector.floorpal;
						}
						break;
					}
					break;
				case kStatItem:
					switch (pTSprite.lotag) {
					case kItemBlueTeamBase:
						if (pTXSprite != null) {
							if ((pTXSprite.state) != 0) {
								if (pGameInfo.nGameType == kNetModeTeams) {
									SPRITE pEffect;
									if ((pEffect = viewAddEffect(nTSprite, kViewEffectBigFlag)) != null)
										pEffect.pal = 10;
								}
							}
						}
						break;
					case kItemRedTeamBase:
						if (pTXSprite != null) {
							if ((pTXSprite.state) != 0) {
								if (pGameInfo.nGameType == kNetModeTeams) {
									SPRITE pEffect;
									if ((pEffect = viewAddEffect(nTSprite, kViewEffectBigFlag)) != null)
										pEffect.pal = 7;
								}
							}
						}
						break;
					case kItemBlueFlag:
						pTSprite.pal = 10;
						pTSprite.cstat |= 4;
						break;
					case kItemRedFlag:
						pTSprite.pal = 7;
						pTSprite.cstat |= 4;
						break;
					default:
						if (pTSprite.lotag >= kItemKey1 && pTSprite.lotag <= kItemKey7)
							pTSprite.shade = -128;
						if (pXSector != null && pXSector.color)
							pTSprite.pal = (byte) pSector.floorpal;
						break;
					}
					break;
				case kStatMissile:
					switch (pTSprite.lotag) {
					case kMissileAltTesla:
						pTSprite.cstat |= 0x20;
						pTSprite.shade = -128;
						break;
					case kMissileTesla:
						viewAddEffect(nTSprite, 15);
						break;
					case kMissileButcherKnife:
						viewAddEffect(nTSprite, 10);
						break;
					case kMissileFlare:
					case kMissileStarburstFlare:
						if (pTSprite.statnum != 14) {
							viewAddEffect(nTSprite, 1);
							if (pTSprite.lotag == kMissileFlare) {
								if ((pSector.ceilingstat & 1) == 0 && (pTSprite.z - pSector.ceilingz) >> 8 < 64
										&& pSector.ceilingpicnum < MIRRORLABEL)
									viewAddEffect(nTSprite, 2);
								if ((pSector.floorstat & 1) == 0 && (pSector.floorz - pTSprite.z) >> 8 < 64
										&& pSector.floorpicnum < MIRRORLABEL)
									viewAddEffect(nTSprite, 3);
							}
							break;
						}
						if (pTXSprite == null)
							game.dassert("pTXSprite != NULL");

						if (pTXSprite.target != gView.nSprite) {
							viewAddEffect(nTSprite, 1);
							if (pTSprite.lotag == kMissileFlare) {
								if ((pSector.ceilingstat & 1) == 0 && (pTSprite.z - pSector.ceilingz) >> 8 < 64
										&& pSector.ceilingpicnum < MIRRORLABEL)
									viewAddEffect(nTSprite, 2);
								if ((pSector.floorstat & 1) == 0 && (pSector.floorz - pTSprite.z) >> 8 < 64
										&& pSector.floorpicnum < MIRRORLABEL)
									viewAddEffect(nTSprite, 3);
							}
						} else
							pTSprite.xrepeat = 0;

						break;
					default:
						continue;
					}
					break;
				case kStatDude:
					if (pTSprite.lotag == kDudeBloatedButcherBurning)
						pTSprite.xrepeat = 48; // make BloatedButcherBurning fatter

                    if (sector[pTSprite.sectnum].extra > 0 && xsector[sector[pTSprite.sectnum].extra].color) {
						switch (pTSprite.lotag) {
						case kDudeTommyCultist:
						case kDudeShotgunCultist:
						case kDudeFanaticProne:
						case kDudeCultistProne:
						case kDudeTeslaCultist:
						case kDudeDynamiteCultist:
						case kDudeBeastCultist:
							break;
						default:
							pTSprite.pal = (byte) sector[pTSprite.sectnum].floorpal;
							break;
						}
					}

					if (powerupCheck(gView, kItemBeastVision - kItemBase) > 0)
						pTSprite.shade = -128;

					if (IsPlayerSprite(pTSprite)) {
						PLAYER pPlayer = gPlayer[pTSprite.lotag - kDudePlayer1];

						if (pTSprite.owner != gView.pSprite.xvel) {
							int tx = pTSprite.x - (int) nViewX;
							int ty = pTSprite.y - (int) nViewY;
							int nAngle = engine.getangle(tx, ty);
							int losAngle = ((kAngle180 + nAngle - gView.pSprite.ang) & kAngleMask) - kAngle180;
							long dist = engine.qdist(tx, ty);

							if (klabs(mulscale(losAngle, dist, 14)) < 4) {
								long z1 = mulscale(dist, (int) gView.horizOff, 10) + nViewZ;
								GetSpriteExtents(pTSprite);

								if ((z1 < extents_zBot) && (z1 > extents_zTop)) {
									if (engine.cansee((int) nViewX, (int) nViewY, (int) nViewZ, gView.pSprite.sectnum,
											pTSprite.x, pTSprite.y, pTSprite.z, pTSprite.sectnum))
										gPlayerIndex = pPlayer.nPlayer;
								}
							}
						}

						if (powerupCheck(pPlayer, kItemLtdInvisibility - kItemBase) == 0
								|| powerupCheck(gView, kItemBeastVision - kItemBase) > 0) {
							if (powerupCheck(pPlayer, kItemInvulnerability - kItemBase) != 0) {
								pTSprite.shade = -128;
								pTSprite.pal = 5;
							} else if (powerupCheck(pPlayer, kItemDoppleganger - kItemBase) != 0) {
								pTSprite.pal = (byte) ((gView.teamID & 3) + 11);
							}
						} else {
							pTSprite.cstat |= 2;
							pTSprite.pal = 5;
						}

						if (powerupCheck(pPlayer, kItemReflectiveShots - kItemBase) != 0)
							viewAddEffect(nTSprite, 13);
						if (cfg.gShowWeapon && pGameInfo.nGameType > kNetModeOff && pPlayer != gView)
							viewAddEffect(nTSprite, kViewEffectShowWeapon);

						if ((pPlayer.fireEffect & 1) != 0 && (pPlayer != gView || gViewPos != 0)) {
							SPRITE pEffect;
							if ((pEffect = viewAddEffect(nTSprite, 14)) != null) {
								POSTURE cp = gPosture[pPlayer.nLifeMode][pPlayer.moveState];
								pEffect.x += mulscale(Cos(pTSprite.ang), cp.xoffset, 28);
								pEffect.y += mulscale(Sin(pTSprite.ang), cp.xoffset, 28);
								pEffect.z = pPlayer.pSprite.z - cp.zoffset;
							}
						}

						if (pPlayer.hasFlag > 0 && pGameInfo.nGameType == kNetModeTeams) {
							if ((pPlayer.hasFlag & 1) != 0) {
								SPRITE pEffect = viewAddEffect(nTSprite, 16);
								if (pEffect != null) {
									pEffect.pal = 10;
									pEffect.cstat |= 4;
								}
							}
							if ((pPlayer.hasFlag & 2) != 0) {
								SPRITE pEffect = viewAddEffect(nTSprite, 16);
								if (pEffect != null) {
									pEffect.pal = 7;
									pEffect.cstat |= 4;
								}
							}
						}
					}

					if (pTSprite.owner != gView.pSprite.xvel || gViewPos != 0) {
						if (engine.getflorzofslope(pTSprite.sectnum, pTSprite.x, pTSprite.y) >= nViewZ) {
							SPRITE pTSpr = viewAddEffect(nTSprite, 0);
							if (pTSpr != null) {
								int camangle = engine.getangle((int) nViewX - pTSprite.x, (int) nViewY - pTSprite.y);
								pTSpr.x -= mulscale(sintable[(camangle + 512) & 2047], 300, 16);
								pTSpr.y += mulscale(sintable[(camangle + 1024) & 2047], 300, 16);
							}
						}
					}

					break;
				case kStatTraps:
					if (pTSprite.lotag == 454) {
						if (pTXSprite.state == 1) {
							if (pTXSprite.data1 != 0) {
								pTSprite.picnum = 772;
								if (pTXSprite.data2 != 0)
									viewAddEffect(nTSprite, 9);
							}
						} else if (pTXSprite.data1 != 0) {
							pTSprite.picnum = 773;
						} else {
							pTSprite.picnum = 656;
						}
					}
					break;
				case kStatThing:
					if (pXSector != null && pXSector.color)
						pTSprite.pal = (byte) pSector.floorpal;
					if ((pTSprite.hitag & 1) != 0) {
						if (engine.getflorzofslope(pTSprite.sectnum, pTSprite.x, pTSprite.y) >= nViewZ) {
							if (pTSprite.lotag < 400 || pTSprite.lotag >= 436
									|| gSpriteHit[pTSprite.extra].floorHit == 0)
								viewAddEffect(nTSprite, 0);
						}
					}
					break;
				default:
					break;
				}

				if (maphack_sprite != -1 && maphack_sprite == pTSprite.owner) {
					pTSprite.shade = (byte) (32 - totalclock & 32);
				}
			}
		}

		for (nTSprite = spritesortcnt - 1; nTSprite >= ospritesortcnt; --nTSprite) {
			SPRITE pTSprite = tsprite[nTSprite];

			int nFrames = 0;
			switch (engine.getTile(pTSprite.picnum).getView()) {
			case kSpriteView5Full:
				// Calculate which of the 8 angles of the sprite to draw (0-7)
				dx = nViewX - pTSprite.x;
				dy = nViewY - pTSprite.y;

				RotateVector(dx, dy, -pTSprite.ang + kAngle45 / 2);
				nFrames = GetOctant((int) rotated.x, (int) rotated.y);

				if (nFrames > 4) {
					nFrames = 8 - nFrames;
					pTSprite.cstat |= kSpriteFlipX;
				} else {
					pTSprite.cstat &= ~kSpriteFlipX;
				}
				break;
			case kSpriteView8Full:
				// Calculate which of the 8 angles of the sprite to draw (0-7)
				dx = nViewX - pTSprite.x;
				dy = nViewY - pTSprite.y;

				RotateVector(dx, dy, -pTSprite.ang + kAngle45 / 2);
				nFrames = GetOctant((int) rotated.x, (int) rotated.y);
				break;
			default:
				break;
			}
			while (nFrames > 0) {
				--nFrames;
				pTSprite.picnum += engine.getTile(pTSprite.picnum).getFrames() + 1;
			}
		}
	}

	public static void viewShowLoadingTile() {
		int flags = kQFrameNoMask | kQFrameScale | kQFrameUnclipped;
		int pic = 2049;

		switch (4 * ydim / xdim) {
		default:
		case 3:
			pic = 2049;
			break;
		case 2:
			if (engine.getTile(kWideLoading).data != null)
				pic = kWideLoading;
			break;
		case 1:
			if (engine.getTile(kUltraWideLoading).data != null)
				pic = kUltraWideLoading;
			break;
		}
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, pic, 0, 0, flags, 0, 0, xdim - 1, ydim - 1);
	}

	public static void DoLensEffect() {
		byte[] d = engine.getTile(BALLBUFFER2).data;
		if (d == null)
			game.dassert("d != null");
		byte[] s = engine.getTile(BALLBUFFER).data;
		if (s == null)
			game.dassert("s != null");

		for (int i = 0, dptr = 0; i < kLensSize * kLensSize; i++, dptr++) {
			int lensData = LittleEndian.getInt(lensTable, 4 * i);
			if (lensData >= 0)
				d[dptr] = s[lensData];
		}

		engine.getrender().invalidatetile(BALLBUFFER2, 0, -1);
	}

	public static void viewSecretStat() {
		game.getFont(1).drawText(75, 70, secrets, -128, 0, TextAlign.Left, 2, true);

		viewDrawNumber(1, foundSecret, 160, 70, 65536, -128, 0, TextAlign.Left, 0, true);
		game.getFont(1).drawText(190, 70, of, -128, 0, TextAlign.Left, 2, true);
		viewDrawNumber(1, totalSecrets, 220, 70, 65536, -128, 0, TextAlign.Left, 0, true);
		if (superSecrets > 0)
			game.getFont(1).drawText(160, 100, supersecret, -128, 2, TextAlign.Center, 2, true);
	}

	public static void viewFragStat() {
		game.getFont(1).drawText(75, 50, killsstat, -128, 0, TextAlign.Left, 2, true);

		viewDrawNumber(1, kills, 160, 50, 65536, -128, 0, TextAlign.Left, 0, true);
		game.getFont(1).drawText(190, 50, of, -128, 0, TextAlign.Left, 2, true);
		viewDrawNumber(1, totalKills, 220, 50, 65536, -128, 0, TextAlign.Left, 0, true);
	}

	public static void viewBackupSpriteLoc(int nSprite, SPRITE pSprite) {
		game.pInt.setsprinterpolate(nSprite, pSprite);
	}

	public static void viewBackupSectorLoc(int nSector, SECTOR pSector) {
		game.pInt.setceilinterpolate(nSector, pSector);
		game.pInt.setfheinuminterpolate(nSector, pSector);
		game.pInt.setfloorinterpolate(nSector, pSector);
	}

	public static SPRITE viewInsertTSprite(int nSector, int nStatus, SPRITE pSource) {
		short nTSprite = -1;

		nTSprite = (short) spritesortcnt;
		if (tsprite[nTSprite] == null)
			tsprite[nTSprite] = new SPRITE();
		SPRITE pTSprite = tsprite[nTSprite];

		pTSprite.reset((byte) 0);
		pTSprite.lotag = kNothing;
		pTSprite.sectnum = (short) nSector;
		pTSprite.statnum = (short) nStatus;
		pTSprite.cstat = kSpriteOriginAlign;
		pTSprite.xrepeat = 64;
		pTSprite.yrepeat = 64;
		pTSprite.owner = -1;
		pTSprite.extra = -1;
		spritesortcnt++;

		if (pSource != null) {
			pTSprite.x = pSource.x;
			pTSprite.y = pSource.y;
			pTSprite.z = pSource.z;
			pTSprite.owner = pSource.owner;
			pTSprite.ang = pSource.ang;
		}

		return pTSprite;
	}

	private static SPRITE viewAddEffect(int nTSprite, int nViewEffect) {
		if (!(nViewEffect >= 0 && nViewEffect < kViewEffectMax))
			game.dassert("nViewEffect >= 0 && nViewEffect < kViewEffectMax " + nViewEffect);
		SPRITE pTSprite = tsprite[nTSprite];
		SPRITE pTEffect;
		short size;

		if (cfg.gDetail < gEffectDetail[nViewEffect] || spritesortcnt >= 1024 || nViewEffect > kViewEffectMax - 1)
			return null;

		switch (nViewEffect) {
		case kViewEffectShadow:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.z = engine.getflorzofslope(pTSprite.sectnum, pTEffect.x, pTEffect.y);
			pTEffect.cstat |= kSpriteTranslucent;

			if (engine.getTile(sprite[pTSprite.owner].picnum).getView() == ViewType.kSpriteView5Full) {
				int dx = gPlayer[gViewIndex].pSprite.x - pTEffect.x;
				int dy = gPlayer[gViewIndex].pSprite.y - pTEffect.y;

				RotateVector(dx, dy, -pTEffect.ang + kAngle45 / 2);
				int nFrames = GetOctant((int) rotated.x, (int) rotated.y);

				if (nFrames > 4) {
					nFrames = 8 - nFrames;
					pTEffect.cstat |= kSpriteFlipX;
				} else
					pTEffect.cstat &= ~kSpriteFlipX;
			}

			pTEffect.shade = 127;
			pTEffect.xrepeat = pTSprite.xrepeat;
			pTEffect.yrepeat = (short) (pTSprite.yrepeat >> 2);
			pTEffect.picnum = pTSprite.picnum;
			pTEffect.pal = 5;
			Tile pic = engine.getTile(pTEffect.picnum);
			pTEffect.z -= (pic.getHeight() - (pic.getOffsetY() + pic.getHeight() / 2)) * pTEffect.yrepeat << 2;
			return pTEffect;
		case kViewEffectFlareHalo:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.shade = -128;
			pTEffect.pal = 2;
			pTEffect.z = pTSprite.z;
			pTEffect.cstat |= kSpriteTranslucent;
			pTEffect.picnum = 2427;
			pTEffect.xrepeat = pTSprite.xrepeat;
			pTEffect.yrepeat = pTSprite.yrepeat;
			break;
		case kViewEffectCeilGlow:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.x = pTSprite.x;
			pTEffect.y = pTSprite.y;
			pTEffect.pal = 2;
			pTEffect.xrepeat = 64;
			pTEffect.yrepeat = 64;
			pTEffect.picnum = 624;
			pTEffect.z = sector[pTSprite.sectnum].ceilingz;
			pTEffect.shade = (byte) ((pTSprite.z - sector[pTSprite.sectnum].ceilingz >> 8) - 64);
			pTEffect.cstat |= (kSpriteTranslucent | kSpriteFlipY | kSpriteFloor | kSpriteOneSided);
			pTEffect.ang = pTSprite.ang;
			pTEffect.owner = pTSprite.owner;
			break;
		case kViewEffectFloorGlow:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.x = pTSprite.x;
			pTEffect.y = pTSprite.y;
			pTEffect.pal = 2;
			pTEffect.picnum = 624;
			pTEffect.z = sector[pTSprite.sectnum].floorz;
			pTEffect.shade = (byte) (((sector[pTSprite.sectnum].floorz - pTSprite.z) >> 8) - 32);
			pTEffect.xrepeat = (short) ((sector[pTSprite.sectnum].floorz - pTSprite.z) >> 8);
			pTEffect.yrepeat = (short) ((sector[pTSprite.sectnum].floorz - pTSprite.z) >> 8);
			pTEffect.cstat |= (kSpriteTranslucent | kSpriteFloor | kSpriteOneSided);
			pTEffect.ang = pTSprite.ang;
			pTEffect.owner = pTSprite.owner;
			break;
		case kViewEffectTorchHigh:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			GetSpriteExtents(pTSprite);
			pTEffect.picnum = 2101;
			pTEffect.shade = -128;
			pTEffect.z = extents_zTop;
			size = (short) (engine.getTile(pTSprite.picnum).getWidth() * pTSprite.xrepeat / 32);
			pTEffect.yrepeat = size;
			pTEffect.xrepeat = size;
			break;
		case kViewEffectTorchLow:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			GetSpriteExtents(pTSprite);
			pTEffect.picnum = 2101;
			pTEffect.shade = -128;
			pTEffect.z = extents_zBot;
			size = (short) (engine.getTile(pTSprite.picnum).getWidth() * pTSprite.xrepeat / 32);
			pTEffect.yrepeat = size;
			pTEffect.xrepeat = size;
			break;
		case kViewEffectSmokeHigh:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			GetSpriteExtents(pTSprite);
			pTEffect.z = extents_zTop;
			if (pTSprite.lotag >= 200 && pTSprite.lotag < 247)
				pTEffect.picnum = 672;
			else
				pTEffect.picnum = 754;
			pTEffect.shade = 8;
			pTEffect.cstat |= kSpriteTranslucent;
			pTEffect.xrepeat = pTSprite.xrepeat;
			pTEffect.yrepeat = pTSprite.yrepeat;
			break;
		case kViewEffectSmokeLow:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			GetSpriteExtents(pTSprite);
			pTEffect.z = extents_zBot;
			if (pTSprite.lotag >= 200 && pTSprite.lotag < 247)
				pTEffect.picnum = 672;
			else
				pTEffect.picnum = 754;
			pTEffect.shade = 8;
			pTEffect.cstat |= kSpriteTranslucent;
			pTEffect.xrepeat = pTSprite.xrepeat;
			pTEffect.yrepeat = pTSprite.yrepeat;
			break;
		case kViewEffectFlame:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.shade = -128;
			pTEffect.z = pTSprite.z;
			size = (short) ((engine.getTile(pTSprite.picnum).getWidth() * pTSprite.xrepeat) / 64);
			pTEffect.picnum = 908;
			pTEffect.statnum = 0;
			pTEffect.yrepeat = size;
			pTEffect.xrepeat = size;
			break;
		case kViewEffectSpear:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.z = pTSprite.z;
			if (cfg.gDetail > 1)
				pTEffect.cstat |= 0x202;
			pTEffect.shade = (byte) ClipLow(pTSprite.shade - 32, -128);
			pTEffect.yrepeat = 64;
			pTEffect.picnum = 775;
			pTEffect.xrepeat = pTSprite.xrepeat;
			break;
		case kViewEffectTrail:
			int nSector, nAngle = pTSprite.ang;
			if ((pTSprite.cstat & kSpriteWall) != 0)
				nAngle += 512;
			else
				nAngle += 1024;
			nAngle &= 0x7FF;

			for (int i = 0; i < 5 && spritesortcnt < 1024; i++) {
				nSector = pTSprite.sectnum;
				pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, null);
				pTEffect.ang = pTSprite.ang;
				pTEffect.x = pTSprite.x + mulscale(Cos(nAngle), (i << 7) + 128, 30);
				pTEffect.y = pTSprite.y + mulscale(Sin(nAngle), (i << 7) + 128, 30);
				pTEffect.z = pTSprite.z;
				if (!(nSector >= 0 && nSector < kMaxSectors))
					game.dassert("nSector >= 0 && nSector < kMaxSectors");
				FindSector(pTEffect.x, pTEffect.y, pTEffect.z, pTSprite.sectnum);

				pTEffect.owner = pTSprite.owner;
				pTEffect.picnum = pTSprite.picnum;
				pTEffect.cstat |= kSpriteTranslucent;
				if (i < 2)
					pTEffect.cstat |= 0x202;
				pTEffect.shade = (byte) ClipLow(pTSprite.shade - 16, -128);
				pTEffect.xrepeat = pTSprite.xrepeat;
				pTEffect.yrepeat = pTSprite.yrepeat;
				pTEffect.picnum = pTSprite.picnum;
			}
			break;
		case kViewEffectPhase:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			GetSpriteExtents(pTSprite);
			pTEffect.shade = 26;
			pTEffect.pal = 0;
			pTEffect.yrepeat = 24;
			pTEffect.picnum = 626;
			pTEffect.z = extents_zTop;
			pTEffect.cstat |= kSpriteTranslucent;
			pTEffect.xrepeat = pTEffect.yrepeat;
			break;
		case kViewEffectShowWeapon:
			if (!IsPlayerSprite(pTSprite))
				game.dassert("pTSprite.type >= kDudePlayer1 && pTSprite.type <= kDudePlayer8");
			PLAYER pPlayer = gPlayer[pTSprite.lotag - kDudePlayer1];
			if (viewWeaponTile[pPlayer.currentWeapon] == -1)
				break;
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.x = pTSprite.x;
			pTEffect.y = pTSprite.y;
			pTEffect.z = pTSprite.z - 0x2000;
			pTEffect.picnum = viewWeaponTile[pPlayer.currentWeapon];
			pTEffect.shade = pTSprite.shade;
			pTEffect.xrepeat = 32;
			pTEffect.yrepeat = 32;

//		        spriteext[pTEffect.owner].flags |= 1; //SPREXT_NOTMD

			break;
		case kViewEffectReflectiveBall:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.shade = 26;
			pTEffect.pal = 0;
			pTEffect.yrepeat = 64;
			pTEffect.picnum = 2089;
			pTEffect.cstat |= kSpriteTranslucent;
			pTEffect.xrepeat = pTEffect.yrepeat;
			break;
		case kViewEffectShoot:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.shade = -128;
			pTEffect.pal = 0;
			pTEffect.yrepeat = 64;
			pTEffect.picnum = 2605;
			pTEffect.xrepeat = pTEffect.yrepeat;
			return pTEffect;
		case kViewEffectTesla:
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.z = pTSprite.z;
			pTEffect.shade = -128;
			pTEffect.cstat |= kSpriteTranslucent;
			pTEffect.xrepeat = pTSprite.xrepeat;
			pTEffect.picnum = 2135;
			pTEffect.yrepeat = pTSprite.yrepeat;
			break;
		case kViewEffectFlag:
		case kViewEffectBigFlag:
			GetSpriteExtents(pTSprite);
			pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
			pTEffect.shade = -128;
			pTEffect.pal = 0;
			pTEffect.z = extents_zTop;
			if (nViewEffect == 16)
				pTEffect.yrepeat = kViewEffectFlag;
			else
				pTEffect.yrepeat = 64;
			pTEffect.xrepeat = pTEffect.yrepeat;
			pTEffect.picnum = 3558; // team flag
			return pTEffect;
		case kViewEffectAtom:
			for (int i = 0; i < 16; i++) {
				pTEffect = viewInsertTSprite(pTSprite.sectnum, 0x7FFF, pTSprite);
				int velocity = (int) (divscale(gFrameClock, 120, 11) + atomEffectVelocity[i].z);

				int dx = mulscale(Cos(velocity), 512, 30);
				int dy = mulscale(Sin(velocity), 512, 30);
				dy = (int) RotateVector(dy, 0, (int) atomEffectVelocity[i].x).x;
				int dz = (int) RotateVector(dx, (int) rotated.y, (int) atomEffectVelocity[i].y).y;
				dx = (int) rotated.x;

				pTEffect.x = pTSprite.x + dx;
				pTEffect.y = pTSprite.y + dy;
				pTEffect.z = pTSprite.z + (dz << 4);
				pTEffect.picnum = 1720;
				pTEffect.shade = -128;
			}
			break;
		}
		return null;
	}

	private static void DrawStatSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlag, int nScale) {
		engine.rotatesprite(x << 16, y << 16, nScale, 0, (short) nTile, nShade, nPLU, nFlag | kRotateStatus, gViewX0,
				gViewY0, gViewX1, gViewY1);
	}
}
