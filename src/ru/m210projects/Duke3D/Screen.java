//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Duke3D;

import static java.lang.Math.max;
import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.cfg;
import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Main.game;
import static ru.m210projects.Duke3D.Names.AIRTANK_ICON;
import static ru.m210projects.Duke3D.Names.ARROW;
import static ru.m210projects.Duke3D.Names.BIGHOLE;
import static ru.m210projects.Duke3D.Names.BOOT_ICON;
import static ru.m210projects.Duke3D.Names.BOTTOMSTATUSBAR;
import static ru.m210projects.Duke3D.Names.DIGITALNUM;
import static ru.m210projects.Duke3D.Names.FIRSTAID_ICON;
import static ru.m210projects.Duke3D.Names.HEAT_ICON;
import static ru.m210projects.Duke3D.Names.HOLODUKE_ICON;
import static ru.m210projects.Duke3D.Names.JETPACK_ICON;
import static ru.m210projects.Duke3D.Names.STEROIDS_ICON;
import static ru.m210projects.Duke3D.Names.THREEBYFIVE;
import static ru.m210projects.Duke3D.Player.slimepal;
import static ru.m210projects.Duke3D.Player.waterpal;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Script.TextureHDInfo;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class Screen {

	public static int changepalette;
	public static boolean restorepalette;
	public static int screensize;

	public static void vscrn(int size) {
		int ss, x1, x2, y1, y2;

		if (size < 0)
			size = 0;
		else if (size > 3)
			size = 3;

		ss = max(size - 3, 0);

		x1 = scale(ss, xdim, 160);
		x2 = xdim - x1;

		y1 = 4 * ss;
		y2 = 200;
//	     if ( size > 0 /*&& ud.coop != 1*/ && ud.multimode > 1)
//		 {
//	         j = 0;
//	         for(i=connecthead;i>=0;i=connectpoint2[i])
//	             if(i > j) j = i;
//
//	         if (j >= 1) y1 += 8;
//	         if (j >= 4) y1 += 8;
//	         if (j >= 8) y1 += 8;
//	         if (j >= 12) y1 += 8;
//		 }

		if (size >= 3)
			y2 -= (3 * (ss) + 32);

		y1 = scale(y1, ydim, 200);
		y2 = scale(y2, ydim, 200);

		engine.setview(x1, y1, x2 - 1, y2 - 1);
		screensize = size;
	}

	public static void setup3dscreen(int w, int h) {
		if (!engine.setgamemode(cfg.fullscreen, w, h))
			cfg.fullscreen = 0;

		cfg.ScreenWidth = BuildGdx.graphics.getWidth();
		cfg.ScreenHeight = BuildGdx.graphics.getHeight();

		engine.setbrightness(ud.brightness >> 2, ps[myconnectindex].palette, GLInvalidateFlag.All);
	}

	public static void setgamepalette(PlayerStruct player, byte[] pal, GLInvalidateFlag set) {
		if (player != ps[screenpeek]) {
			// another head
			player.palette = pal;
			return;
		}
		player.palette = pal;
		engine.setbrightness(ud.brightness >> 2, pal, set);
		engine.setpalettefade(0, 0, 0, 0);

		if (game.currentDef != null) {
			TextureHDInfo hdInfo = game.currentDef.texInfo;
			if (pal == waterpal)
				hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 160, 160, 255, 0);
			else if (pal == slimepal)
				hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 180, 200, 50, 0);
			else
				hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 255, 255, 255, 0);
		}
	}

	public static void palto(int r, int g, int b, int count) {
		if (engine.glrender() != null) {
			if (count > 0) {
				int fr = 0, fg = 0, fb = 0;
				if (r > 0)
					fr = Math.min(count - 128, r / 2);
				if (g > 0)
					fg = Math.min(count - 128, g / 2);
				if (b > 0)
					fb = Math.min(count - 128, b / 2);

				engine.setpalettefade(fr, fg, fb, 1);
				engine.showfade();
			}
		} else
			engine.setpalettefade(r, g, b, count & 127);
	}

	public static void scrReset() {
		engine.setpalettefade(0, 0, 0, 1);
		setgamepalette(ps[myconnectindex], palette, GLInvalidateFlag.All);
	}

	public static void myospal(int x, int y, int tilenum, int shade, int orientation, int p) {
		short a = 0;
		if ((orientation & 4) != 0)
			a = 1024;
		engine.rotatesprite(x << 16, y << 16, 65536, a, tilenum, shade, p, 10 | orientation, windowx1, windowy1,
				windowx2, windowy2);
	}

	public static void myos(int x, int y, int tilenum, int shade, int orientation) {
		int a = 0;
		if ((orientation & 4) != 0)
			a = 1024;

		int p = sector[ps[screenpeek].cursectnum].floorpal;
		engine.rotatesprite(x << 16, y << 16, 65536, a, tilenum, shade, p, 10 | orientation, windowx1, windowy1,
				windowx2, windowy2);
	}

	public static void weaponnum(int ind, int x, int y, int num1, int num2, int ha) {
		engine.rotatesprite((x - 7) << 16, y << 16, 65536, 0, THREEBYFIVE + ind + 1, ha - 10, 7, 10, 0, 0, xdim - 1,
				ydim - 1);
		engine.rotatesprite((x - 3) << 16, y << 16, 65536, 0, THREEBYFIVE + 10, ha, 0, 10, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite((x + 9) << 16, y << 16, 65536, 0, THREEBYFIVE + 11, ha, 0, 10, 0, 0, xdim - 1, ydim - 1);

		if (num1 > 99)
			num1 = 99;
		if (num2 > 99)
			num2 = 99;

		Bitoa(num1, buf);
		if (num1 > 9) {
			engine.rotatesprite((x) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[1] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
		} else
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);

		Bitoa(num2, buf);
		if (num2 > 9) {
			engine.rotatesprite((x + 13) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x + 17) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[1] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
		} else
			engine.rotatesprite((x + 13) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
	}

	public static void weaponnum999(int ind, int x, int y, int num1, int num2, int ha) {
		engine.rotatesprite((x - 7) << 16, y << 16, 65536, 0, THREEBYFIVE + ind + 1, ha - 10, 7, 10, 0, 0, xdim - 1,
				ydim - 1);
		engine.rotatesprite((x - 4) << 16, y << 16, 65536, 0, THREEBYFIVE + 10, ha, 0, 10, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite((x + 13) << 16, y << 16, 65536, 0, THREEBYFIVE + 11, ha, 0, 10, 0, 0, xdim - 1, ydim - 1);

		Bitoa(num1, buf);
		if (num1 > 99) {
			engine.rotatesprite((x) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[1] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
			engine.rotatesprite((x + 8) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[2] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
		} else if (num1 > 9) {
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
			engine.rotatesprite((x + 8) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[1] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);
		} else
			engine.rotatesprite((x + 8) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0, xdim - 1,
					ydim - 1);

		Bitoa(num2, buf);
		if (num2 > 99) {
			engine.rotatesprite((x + 17) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x + 21) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[1] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x + 25) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[2] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
		} else if (num2 > 9) {
			engine.rotatesprite((x + 17) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x + 21) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[1] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
		} else
			engine.rotatesprite((x + 25) << 16, y << 16, 65536, 0, THREEBYFIVE + buf[0] - '0', ha, 0, 10, 0, 0,
					xdim - 1, ydim - 1);
	}

	public static void weapon_amounts(PlayerStruct p, int x, int y) {
		int cw = p.curr_weapon;
		weaponnum999(PISTOL_WEAPON, x, y, p.ammo_amount[PISTOL_WEAPON],
				currentGame.getCON().max_ammo_amount[PISTOL_WEAPON], 12 - ((cw == PISTOL_WEAPON) ? 20 : 0));

		weaponnum999(SHOTGUN_WEAPON, x, y + 6, p.ammo_amount[SHOTGUN_WEAPON],
				currentGame.getCON().max_ammo_amount[SHOTGUN_WEAPON],
				(!p.gotweapon[SHOTGUN_WEAPON] ? 9 : 0) + 12 - ((cw == SHOTGUN_WEAPON) ? 18 : 0));

		weaponnum999(CHAINGUN_WEAPON, x, y + 12, p.ammo_amount[CHAINGUN_WEAPON],
				currentGame.getCON().max_ammo_amount[CHAINGUN_WEAPON],
				(!p.gotweapon[CHAINGUN_WEAPON] ? 9 : 0) + 12 - ((cw == CHAINGUN_WEAPON) ? 18 : 0));

		weaponnum(RPG_WEAPON, x + 39, y, p.ammo_amount[RPG_WEAPON], currentGame.getCON().max_ammo_amount[RPG_WEAPON],
				(!p.gotweapon[RPG_WEAPON] ? 9 : 0) + 12 - ((cw == RPG_WEAPON) ? 19 : 0));

		weaponnum(HANDBOMB_WEAPON, x + 39, y + 6, p.ammo_amount[HANDBOMB_WEAPON],
				currentGame.getCON().max_ammo_amount[HANDBOMB_WEAPON],
				(((p.ammo_amount[HANDBOMB_WEAPON] == 0) ? 1 : 0) | (!p.gotweapon[HANDBOMB_WEAPON] ? 1 : 0)) * 9 + 12
						- ((cw == HANDBOMB_WEAPON || cw == HANDREMOTE_WEAPON) ? 19 : 0));

		if ((p.subweapon & (1 << EXPANDER_WEAPON)) != 0)
			weaponnum(SHRINKER_WEAPON, x + 39, y + 12, p.ammo_amount[EXPANDER_WEAPON],
					currentGame.getCON().max_ammo_amount[EXPANDER_WEAPON],
					(!p.gotweapon[EXPANDER_WEAPON] ? 9 : 0) + 12 - ((cw == EXPANDER_WEAPON) ? 18 : 0));
		else
			weaponnum(SHRINKER_WEAPON, x + 39, y + 12, p.ammo_amount[SHRINKER_WEAPON],
					currentGame.getCON().max_ammo_amount[SHRINKER_WEAPON],
					(!p.gotweapon[SHRINKER_WEAPON] ? 9 : 0) + 12 - ((cw == SHRINKER_WEAPON) ? 18 : 0));

		weaponnum(DEVASTATOR_WEAPON, x + 70, y, p.ammo_amount[DEVASTATOR_WEAPON],
				currentGame.getCON().max_ammo_amount[DEVASTATOR_WEAPON],
				(!p.gotweapon[DEVASTATOR_WEAPON] ? 9 : 0) + 12 - ((cw == DEVASTATOR_WEAPON) ? 18 : 0));

		weaponnum(TRIPBOMB_WEAPON, x + 70, y + 6, p.ammo_amount[TRIPBOMB_WEAPON],
				currentGame.getCON().max_ammo_amount[TRIPBOMB_WEAPON],
				(!p.gotweapon[TRIPBOMB_WEAPON] ? 9 : 0) + 12 - ((cw == TRIPBOMB_WEAPON) ? 18 : 0));

		// 20th Anniversary World Tour - allow Incinerator to appear on hud as well
		// Why wasn't this simple fix done anyway?  Oh well, Fixed by the Gib!
		if ((p.subweapon & (1 << INCINERATOR_WEAPON)) != 0)
			weaponnum(-1, x + 70, y + 12, p.ammo_amount[INCINERATOR_WEAPON],
					currentGame.getCON().max_ammo_amount[INCINERATOR_WEAPON],
				(!p.gotweapon[INCINERATOR_WEAPON] ? 9 : 0) + 12 - ((cw == INCINERATOR_WEAPON) ? 18 : 0));
		else
			weaponnum(-1, x + 70, y + 12, p.ammo_amount[FREEZE_WEAPON],
					currentGame.getCON().max_ammo_amount[FREEZE_WEAPON],
				(!p.gotweapon[FREEZE_WEAPON] ? 9 : 0) + 12 - ((cw == FREEZE_WEAPON) ? 18 : 0));
	}

	public static void patchstatusbar(int x1, int y1, int x2, int y2) {
		if (ud.screen_size >= 2) {
			Tile pic = engine.getTile(BIGHOLE);

			int framesx = xdim / pic.getWidth();
			int framesy = ydim - scale(engine.getTile(BOTTOMSTATUSBAR).getHeight(), ydim, 200);

			int x = 0;
			for (int i = 0; i <= framesx; i++) {
				engine.rotatesprite(x << 16, framesy << 16, 0x10000, 0, BIGHOLE, 0, 0, 8 | 16 | 256, 0, 0, xdim - 1,
						ydim - 1);
				x += pic.getWidth();
			}
		}

		engine.rotatesprite(160 << 16, 197 << 16, 65536, 0, BOTTOMSTATUSBAR, 4, 0, 10 + 64, scale(x1, xdim, 320),
				scale(y1, ydim, 200), scale(x2, xdim, 320) - 1, scale(y2, ydim, 200) - 1);
	}

	public static void displayinventory(PlayerStruct p) {
		int n, j, xoff, y;

		j = xoff = 0;

		n = (p.jetpack_amount > 0) ? 1 << 3 : 0;
		if ((n & 8) != 0)
			j++;
		n |= (p.scuba_amount > 0) ? 1 << 5 : 0;
		if ((n & 32) != 0)
			j++;
		n |= (p.steroids_amount > 0) ? 1 << 1 : 0;
		if ((n & 2) != 0)
			j++;
		n |= (p.holoduke_amount > 0) ? 1 << 2 : 0;
		if ((n & 4) != 0)
			j++;
		n |= (p.firstaid_amount > 0) ? 1 : 0;
		if ((n & 1) != 0)
			j++;
		n |= (p.heat_amount > 0) ? 1 << 4 : 0;
		if ((n & 16) != 0)
			j++;
		n |= (p.boot_amount > 0) ? 1 << 6 : 0;
		if ((n & 64) != 0)
			j++;

		xoff = 160 - (j * 11);

		j = 0;

		if (ud.screen_size > 1)
			y = 134;
		else
			y = 178;

		if (ud.screen_size == 1) {
			if (ud.multimode > 1)
				xoff += 56;
			else
				xoff += 65;
		}

		while (j <= 9) {
			if ((n & (1 << j)) != 0) {
				switch (n & (1 << j)) {
				case 1:
					engine.rotatesprite(xoff << 16, y << 16, 65536, 0, FIRSTAID_ICON, 0, 0, 10 + 16, windowx1, windowy1,
							windowx2, windowy2);
					break;
				case 2:
					engine.rotatesprite((xoff + 1) << 16, y << 16, 65536, 0, STEROIDS_ICON, 0, 0, 10 + 16, windowx1,
							windowy1, windowx2, windowy2);
					break;
				case 4:
					engine.rotatesprite((xoff + 2) << 16, y << 16, 65536, 0, HOLODUKE_ICON, 0, 0, 10 + 16, windowx1,
							windowy1, windowx2, windowy2);
					break;
				case 8:
					engine.rotatesprite(xoff << 16, y << 16, 65536, 0, JETPACK_ICON, 0, 0, 10 + 16, windowx1, windowy1,
							windowx2, windowy2);
					break;
				case 16:
					engine.rotatesprite(xoff << 16, y << 16, 65536, 0, HEAT_ICON, 0, 0, 10 + 16, windowx1, windowy1,
							windowx2, windowy2);
					break;
				case 32:
					engine.rotatesprite(xoff << 16, y << 16, 65536, 0, AIRTANK_ICON, 0, 0, 10 + 16, windowx1, windowy1,
							windowx2, windowy2);
					break;
				case 64:
					engine.rotatesprite(xoff << 16, (y - 1) << 16, 65536, 0, BOOT_ICON, 0, 0, 10 + 16, windowx1,
							windowy1, windowx2, windowy2);
					break;
				}

				xoff += 22;

				if (p.inven_icon == j + 1)
					engine.rotatesprite((xoff - 2) << 16, (y + 19) << 16, 65536, 1024, ARROW, -32, 0, 10 + 16, windowx1,
							windowy1, windowx2, windowy2);
			}

			j++;
		}
	}

	public static void invennum(int x, int y, int num1, int ha, int sbits) {
		char[] dabuf = Globals.buf;

		Bitoa(num1, dabuf);

		if (num1 > 99) {
			engine.rotatesprite((x - 4) << 16, y << 16, 65536, 0, THREEBYFIVE + dabuf[0] - '0', ha, 0, sbits, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x) << 16, y << 16, 65536, 0, THREEBYFIVE + dabuf[1] - '0', ha, 0, sbits, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + dabuf[2] - '0', ha, 0, sbits, 0, 0,
					xdim - 1, ydim - 1);
		} else if (num1 > 9) {
			engine.rotatesprite((x) << 16, y << 16, 65536, 0, THREEBYFIVE + dabuf[0] - '0', ha, 0, sbits, 0, 0,
					xdim - 1, ydim - 1);
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + dabuf[1] - '0', ha, 0, sbits, 0, 0,
					xdim - 1, ydim - 1);
		} else
			engine.rotatesprite((x + 4) << 16, y << 16, 65536, 0, THREEBYFIVE + dabuf[0] - '0', ha, 0, sbits, 0, 0,
					xdim - 1, ydim - 1);
	}

	public static void digitalnumber(int x, int y, int n, int s, int cs) {
		int i, j, k, p, c;
		char[] b = Globals.buf;
		i = Bitoa(n, b); // ltoa(n,b,10);
		j = 0;

		for (k = 0; k < i; k++) {
			p = DIGITALNUM + b[k] - '0';
			j += engine.getTile(p).getWidth() + 1;
		}
		c = x - (j >> 1);

		j = 0;
		for (k = 0; k < i; k++) {
			p = DIGITALNUM + b[k] - '0';
			engine.rotatesprite((c + j) << 16, y << 16, 65536, 0, p, s, 0, cs, 0, 0, xdim - 1, ydim - 1);
			j += engine.getTile(p).getWidth() + 1;
		}
	}

}
