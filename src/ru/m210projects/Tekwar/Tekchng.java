package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.View.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;

import static ru.m210projects.Tekwar.Tekgun.*;
import static ru.m210projects.Tekwar.Tekldsv.lastload;
import static ru.m210projects.Tekwar.Teksnd.*;
import static ru.m210projects.Tekwar.Tekspr.*;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.LASTSAVE;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Player.*;

public class Tekchng {

	public static final int MAXSTUNPOINTS = 1000;
	static int[] stun = new int[MAXPLAYERS], stuntics = new int[MAXPLAYERS];
	static int[] fallz = new int[MAXPLAYERS];

	public static void tekhealstun(int snum) {
		if (gPlayer[snum].godMode) {
			return;
		}

		if (stun[snum] < MAXSTUNPOINTS) {
			if (stuntics[snum] >= CLKIPS) {
				stuntics[snum] -= CLKIPS;
				tekchangestun(snum, 1);
			}
		}
	}

	public static boolean changehealth(int snum, int deltahealth) {
		if (!validplayer(snum)) {
			game.ThrowError("changehealth: bad plrnum", null);
		}
		if (gPlayer[snum].godMode) {
			return (false);
		}
		if (gPlayer[snum].health <= 0) {
			return (true);
		}

		if (game.nNetMode == NetMode.Single) {
			if (deltahealth < 0) {
				switch (gDifficulty) {
				case 0:
				case 1:
					deltahealth >>= 3;
					deltahealth -= 5;
					break;
				case 2:
					deltahealth >>= 2;
					break;
				case 3:
					deltahealth >>= 1;
					break;
				}
			}
		}

		if (snum == screenpeek) {
			if (deltahealth < 0) {
				if (deltahealth <= -90) {
					startredflash(100);
				} else {
					startredflash(4);
				}
			} else {
				startwhiteflash(8);
			}
		}

		if (gPlayer[snum].health > 0) {
			gPlayer[snum].health += deltahealth;
			if (gPlayer[snum].health > MAXHEALTH)
				gPlayer[snum].health = MAXHEALTH;

			if (gPlayer[snum].health <= 0) {
				
				if(game.nNetMode != NetMode.Multiplayer) {
					if (lastload != null && !lastload.isEmpty() && BuildGdx.compat.checkFile(lastload, Path.User) != null)
						game.menu.mOpen(game.menu.mMenus[LASTSAVE], -1);
				}
				
				gPlayer[snum].health = -1;
				playsound(S_PLAYERDIE, gPlayer[snum].posx, gPlayer[snum].posy, 0, ST_NOUPDATE);
			}
		}

		if ((gPlayer[snum].health <= 0) && game.nNetMode == NetMode.Multiplayer) {
			playerdropitems(snum);
		}

		return (gPlayer[snum].health <= 0);
	}

	public static void changescore(int snum, int deltascore) {
		if (gPlayer[snum].godMode) {
			return;
		}

		gPlayer[snum].score += deltascore;
		gPlayer[snum].score &= 0xFFFFFFFE;

		if ((gPlayer[snum].score < 0) || (gPlayer[snum].score > 99999999)) {
			gPlayer[snum].score = 0;
		}

	}

	public static void tekchangefallz(int snum, int loz, int hiz) {
		boolean died = false;

		gPlayer[snum].posz += gPlayer[snum].hvel;
		if (gPlayer[snum].noclip)
			return;

		fallz[snum] += gPlayer[snum].hvel;
		if (gPlayer[snum].posz > loz - (KENSPLAYERHEIGHT << 8)) {
			if (fallz[snum] < 0) {
				fallz[snum] = 0;
			}
			if (gPlayer[snum].posz > loz - (4 << 8)) {
				gPlayer[snum].posz = loz - (4 << 8);
				gPlayer[snum].hvel = 0;
			}
			switch (fallz[snum] >> 15) {
			case 0:
			case 1:
				break;
			case 2:
				tekchangestun(snum, -200);
				break;
			case 3:
				tekchangestun(snum, -400);
				died = changehealth(snum, -200);
				break;
			case 4:
				tekchangestun(snum, -800);
				died = changehealth(snum, -400);
				break;
			case 5:
				tekchangestun(snum, -1000);
				died = changehealth(snum, -1000);
				break;
			default:
				died = changehealth(snum, -9999);
				break;
			}
			fallz[snum] = 0;
		}
		if (died && goreflag) {
			tekexplodebody(gPlayer[snum].playersprite);
		}

		if (gPlayer[snum].posz < hiz + (4 << 8)) {
			gPlayer[snum].posz = hiz + (4 << 8);
			gPlayer[snum].hvel = 0;
		}
	}

	public static void tekhealstun(short snum) {
		if (gPlayer[snum].godMode) {
			return;
		}

		if (stun[snum] < MAXSTUNPOINTS) {
			if (stuntics[snum] >= CLKIPS) {
				stuntics[snum] -= CLKIPS;
				tekchangestun(snum, 1);
			}
		}
	}
}
