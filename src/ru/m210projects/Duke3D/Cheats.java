// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.Sector.*;
import static ru.m210projects.Duke3D.View.*;
import static ru.m210projects.Duke3D.Weapons.*;

import ru.m210projects.Build.OnSceenDisplay.Console;

public class Cheats {

	private static final int kCheatMax = 20;

	public static final String[] cheatCode = { /* 0 */"EODPSOIPMJP", // dncornholio
			/* 1 */"EOTUVGG", // dnstuff
			/* 2 */"EOTDPUUZ", // dnscotty
			/* 3 */"EODPPSET", // dncoords
			/* 4 */"EOWJFX", // dnview
			/* 5 */"EOIZQFS", // dnhyper
			/* 6 */"EOVOMPDL", // dnunlock
			/* 7 */"EODBTINBO", // dncashman
			/* 8 */"EOJUFNT", // dnitems
			/* 9 */"EOTLJMM", // dnskill
			/* 10 */"EONPOTUFST", // dnmonsters
			/* 11 */"EOUPEE", // dntodd
			/* 12 */"EOTIPXNBQ", // dnshowmap
			/* 13 */"EOLSP{", // dnkroz
			/* 14 */"EOBMMFO", // dnallen
			/* 15 */"EODMJQ", // dnclip
			/* 16 */"EOXFBQPOT", // dnweapons
			/* 17 */"EOJOWFOUPSZ", // dninventory
			/* 18 */"EOLFZT", // dnkeys
			/* 19 */"EOCFUB", // dnbeta
	};

	public static boolean IsCheatCode(String message, int... opt) {
		for (int nCheatCode = 0; nCheatCode < kCheatMax; nCheatCode++) {
			if (message.equalsIgnoreCase(cheatCode[nCheatCode])) {
				switch (nCheatCode) {
				case 0: // dncornholio
				case 13: // dnkroz
					ud.god = !ud.god;

					if (ud.god) {
						sprite[ps[myconnectindex].i].cstat = 257;

						hittype[ps[myconnectindex].i].temp_data[0] = 0;
						hittype[ps[myconnectindex].i].temp_data[1] = 0;
						hittype[ps[myconnectindex].i].temp_data[2] = 0;
						hittype[ps[myconnectindex].i].temp_data[3] = 0;
						hittype[ps[myconnectindex].i].temp_data[4] = 0;
						hittype[ps[myconnectindex].i].temp_data[5] = 0;

						sprite[ps[myconnectindex].i].hitag = 0;
						sprite[ps[myconnectindex].i].lotag = 0;
						sprite[ps[myconnectindex].i].pal = ps[myconnectindex].palookup;

						FTA(17, ps[myconnectindex]);
					} else {
						ud.god = false;
						sprite[ps[myconnectindex].i].extra = (short) currentGame.getCON().max_player_health;
						hittype[ps[myconnectindex].i].extra = -1;
						ps[myconnectindex].last_extra = (short) currentGame.getCON().max_player_health;
						FTA(18, ps[myconnectindex]);
					}

					sprite[ps[myconnectindex].i].extra = (short) currentGame.getCON().max_player_health;
					hittype[ps[myconnectindex].i].extra = 0;
					break;
				case 1: // dnstuff
					for (int weapon = PISTOL_WEAPON; weapon < MAX_WEAPONS; weapon++)
						ps[myconnectindex].gotweapon[weapon] = true;

					for (int weapon = PISTOL_WEAPON; weapon < (MAX_WEAPONS); weapon++)
						addammo(weapon, ps[myconnectindex], currentGame.getCON().max_ammo_amount[weapon]);

					ps[myconnectindex].ammo_amount[GROW_WEAPON] = 50;

					ps[myconnectindex].steroids_amount = 400;
					ps[myconnectindex].heat_amount = 1200;
					ps[myconnectindex].boot_amount = 200;
					ps[myconnectindex].shield_amount = 100;
					ps[myconnectindex].scuba_amount = 6400;
					ps[myconnectindex].holoduke_amount = 2400;
					ps[myconnectindex].jetpack_amount = 1600;
					ps[myconnectindex].firstaid_amount = (short) currentGame.getCON().max_player_health;

					ps[myconnectindex].got_access = 7;
					FTA(5, ps[myconnectindex]);
					ps[myconnectindex].inven_icon = 1;
					break;
				case 3: // dncoords
					ud.coords = 1 - ud.coords;
					break;
				case 4: // dnview
					if (over_shoulder_on != 0)
						over_shoulder_on = 0;
					else {
						over_shoulder_on = 1;
						cameradist = 0;
						cameraclock = totalclock;
					}
					FTA(22, ps[myconnectindex]);
					break;
				case 5: // dnhyper
					ps[myconnectindex].steroids_amount = 399;
					ps[myconnectindex].heat_amount = 1200;
					ps[myconnectindex].cheat_phase = 0;
					FTA(37, ps[myconnectindex]);
					break;
				case 6: // dnunlock
					for (int i = numsectors - 1; i >= 0; i--) // Unlock
					{
						int j = sector[i].lotag;
						if (j == -1 || j == 32767)
							continue;
						if ((j & 0x7fff) > 2) {
							if ((j & (0xffff - 16384)) != 0)
								sector[i].lotag &= (0xffff - 16384);
							operatesectors(i, ps[myconnectindex].i);
						}
					}
					operateforcefields(ps[myconnectindex].i, -1);

					FTA(100, ps[myconnectindex]);
					break;
				case 7: // dncashman
					ud.cashman = 1 - ud.cashman;
					break;
				case 8: // dnitems
					ps[myconnectindex].steroids_amount = 400;
					ps[myconnectindex].heat_amount = 1200;
					ps[myconnectindex].boot_amount = 200;
					ps[myconnectindex].shield_amount = 100;
					ps[myconnectindex].scuba_amount = 6400;
					ps[myconnectindex].holoduke_amount = 2400;
					ps[myconnectindex].jetpack_amount = 1600;

					ps[myconnectindex].firstaid_amount = (short) currentGame.getCON().max_player_health;
					ps[myconnectindex].got_access = 7;
					FTA(5, ps[myconnectindex]);
					break;
				case 2: // dnscotty
					int volnume = ud.volume_number;
					int levnume = ud.level_number;
					if (opt.length == 2) {
						volnume = opt[0] - 1;
						levnume = opt[1] - 1;
						if (volnume >= currentGame.nEpisodes || volnume < 0)
							volnume = ud.volume_number;
						if (levnume >= currentGame.episodes[volnume].nMaps || levnume < 0)
							levnume = ud.level_number;
						ud.volume_number = volnume;
						ud.level_number = levnume;

						if (mUserFlag == UserFlag.UserMap)
							mUserFlag = UserFlag.None;
					} else if (opt.length == 1) {
						levnume = opt[0] - 1;
						if ((currentGame.episodes[ud.volume_number] != null && levnume >= currentGame.episodes[ud.volume_number].nMaps) || levnume < 0)
							levnume = ud.level_number;
						ud.level_number = levnume;

						if (mUserFlag == UserFlag.UserMap)
							mUserFlag = UserFlag.None;
					} else
						return false;
					gGameScreen.enterlevel(gGameScreen.getTitle(), volnume, levnume);
					if (Console.IsShown())
						Console.toggle();
					break;
				case 9: // dnskill
					if (opt.length == 1) {
						int skill = opt[0];
						if (skill < 1 || skill > 4)
							return false;

						ud.player_skill = skill;

						if (numplayers > 1 && myconnectindex == connecthead) {
							tempbuf[0] = 5;
							tempbuf[1] = (byte) ud.level_number;
							tempbuf[2] = (byte) ud.volume_number;
							tempbuf[3] = (byte) ud.player_skill;
							tempbuf[4] = ud.monsters_off ? (byte) 1 : 0;
							tempbuf[5] = ud.respawn_monsters ? (byte) 1 : 0;
							tempbuf[6] = ud.respawn_items ? (byte) 1 : 0;
							tempbuf[7] = ud.respawn_inventory ? (byte) 1 : 0;
							tempbuf[8] = (byte) ud.coop;
							tempbuf[9] = (byte) ud.marker;
							tempbuf[10] = (byte) ud.ffire;

							for (int i = connecthead; i >= 0; i = connectpoint2[i])
								sendpacket(i, tempbuf, 11);
						}
						gGameScreen.enterlevel(gGameScreen.getTitle(), ud.volume_number, ud.level_number);
						if (Console.IsShown())
							Console.toggle();
					} else
						return false;
					break;
				case 10: // dnmonsters
					if (actor_tog == 3)
						actor_tog = 0;
					actor_tog++;
					break;
				case 11: // dntodd
					FTA(99, ps[myconnectindex]);
					break;
				case 12: // dnshowmap
					ud.showallmap = 1 - ud.showallmap;
					if (ud.showallmap != 0) {
						for (int i = 0; i < (MAXSECTORS >> 3); i++)
							show2dsector[i] = (byte) 255;
						for (int i = 0; i < (MAXWALLS >> 3); i++)
							show2dwall[i] = (byte) 255;
						FTA(111, ps[myconnectindex]);
					} else {
						for (int i = 0; i < (MAXSECTORS >> 3); i++)
							show2dsector[i] = 0;
						for (int i = 0; i < (MAXWALLS >> 3); i++)
							show2dwall[i] = 0;
						FTA(1, ps[myconnectindex]);
					}
					break;
				case 14: // dnallen
					FTA(79, ps[myconnectindex]);
					break;
				case 15: // dnclip
					ud.clipping = !ud.clipping;
					ps[myconnectindex].cheat_phase = 0;
					FTA(112 + (ud.clipping ? 1 : 0), ps[myconnectindex]);
					break;
				case 16: // dnweapons
					for (int weapon = PISTOL_WEAPON; weapon < MAX_WEAPONS; weapon++) {
						addammo(weapon, ps[myconnectindex], currentGame.getCON().max_ammo_amount[weapon]);
						ps[myconnectindex].gotweapon[weapon] = true;
					}

					FTA(119, ps[myconnectindex]);
					break;
				case 17: // dninventory
					ps[myconnectindex].cheat_phase = 0;
					ps[myconnectindex].steroids_amount = 400;
					ps[myconnectindex].heat_amount = 1200;
					ps[myconnectindex].boot_amount = 200;
					ps[myconnectindex].shield_amount = 100;
					ps[myconnectindex].scuba_amount = 6400;
					ps[myconnectindex].holoduke_amount = 2400;
					ps[myconnectindex].jetpack_amount = 1600;
					ps[myconnectindex].firstaid_amount = (short) currentGame.getCON().max_player_health;
					FTA(120, ps[myconnectindex]);
					break;
				case 18: // dnkeys
					ps[myconnectindex].got_access = 7;
					FTA(121, ps[myconnectindex]);
					break;
				case 19: // dnbeta
					FTA(105, ps[myconnectindex]);
					break;
				}
				ps[myconnectindex].cheat_phase = 0;
				return true;
			}
		}

		return false;
	}
}
