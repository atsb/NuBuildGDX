// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Weapons.*;
import static ru.m210projects.Redneck.Main.*;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;

public class Cheats {

	private static final int kCheatMax = 19;

	public static final String cheatCode[] = {
		/*0*/"SEBMM", // rdall
		/*1*/"SEDMJQ", // rdclip
		/*2*/"SEFMWJT", // rdelvis
		/*3*/"SEHVOT", // rdguns
		/*4*/"SEJOWFOUPSZ", // rdinventory
		/*5*/"SEJUFNT", // rditems
		/*6*/"SELFZT", // rdkeys
		/*7*/"SETLJMM", // rdskill
		/*8*/"SEVOMPDL", // rdunlock
		/*9*/"SESBGBFM", // rdrafael
		/*10*/"SENFBEPX", // rdmeadow
		/*11*/"SEUFBDIFST", // rdteachers
		/*12*/"SEWJFX", // rdview
		/*13*/"SEDMVDL", // rdcluck
		/*14*/"SEZFSBU", // rdyerat
		/*15*/"SENPPOTIJOF", // rdmoonshine
		/*16*/"SEDSJUUFST", // rdcritters
		/*17*/"SETIPXNBQ", // rdshowmap
		/*18*/"SEIPVOEEPH", // rdhounddog
	};
	
	public static boolean IsCheatCode(String message, int... opt)
	{
		for(int nCheatCode = 0; nCheatCode < kCheatMax; nCheatCode++)
		{
			if(message.equalsIgnoreCase(cheatCode[nCheatCode]))
			{
				switch(nCheatCode)
				{
				case 16: //critters
					if ( actor_tog == 3 )
                        actor_tog = 0;
					actor_tog++;
					break;
				case 15: //moonshine
					ps[myconnectindex].moonshine_amount = 399;
					FTA(37, ps[myconnectindex]);
					break;
				case 11: //teachers
					FTA(105, ps[myconnectindex]);
					break;
				case 9: //rdrafael
					FTA(99, ps[myconnectindex]);
					break;
				case 17: //rdshowmap
					ud.showallmap = 1-ud.showallmap;
                    if(ud.showallmap != 0)
                    {
                        for(int i=0;i<(MAXSECTORS>>3);i++)
                            show2dsector[i] = (byte) 255;
                        for(int i=0;i<(MAXWALLS>>3);i++)
                            show2dwall[i] = (byte) 255;
                        FTA(111, ps[myconnectindex]);
                    }
                    else
                    {
                        for(int i=0;i<(MAXSECTORS>>3);i++)
                            show2dsector[i] = 0;
                        for(int i=0;i<(MAXWALLS>>3);i++)
                            show2dwall[i] = 0;
                        FTA(1,ps[myconnectindex]);
                    }	
					break;
				case 5: //rditems
					ps[myconnectindex].moonshine_amount =         	400;
                    ps[myconnectindex].boot_amount      =    		2000;
                    ps[myconnectindex].shield_amount 	=           100;
                    ps[myconnectindex].snorkle_amount 	=           6400;
                    ps[myconnectindex].beer_amount 		=         	2400;
                    ps[myconnectindex].cowpie_amount 	=          	600;
                    ps[myconnectindex].whishkey_amount 	=         	(short) currentGame.getCON().max_player_health;

                    Arrays.fill(ps[myconnectindex].gotkey, (short)1);
                    FTA(5,ps[myconnectindex]);
					break;
				case 13: // dncashman
					ud.cashman = 1-ud.cashman;
					break;
				case 8: // rdunlock
					for(int i=numsectors-1;i>=0;i--) //Unlock
                    {
                        int j = sector[i].lotag;
                        if(j == -1 || j == 32767) continue;
                        if( (j & 0x7fff) > 2 )
                        {
                            if( (j&(0xffff-16384)) != 0 )
                                sector[i].lotag &= (0xffff-16384);
                            operatesectors(i,ps[myconnectindex].i);
                        }
                    }
                    operateforcefields(ps[myconnectindex].i,-1);

                    FTA(100, ps[myconnectindex]);		
					break;
				case 12: // rdview
					if( over_shoulder_on != 0 )
                        over_shoulder_on = 0;
                    else
                    {
                        over_shoulder_on = 1;
                        cameradist = 0;
                        cameraclock = totalclock;
                    }
                    FTA(22,ps[myconnectindex]);
					break;
				case 14:
					ud.coords = 1 - ud.coords;
					break;
				case 10: //rdmeadow
					if(opt.length == 2)
					{
						int volnume = opt[0] - 1;
						int levnume = opt[1] - 1;
						if(volnume >= currentGame.nEpisodes || volnume < 0)
							volnume = ud.volume_number;
						if((currentGame.episodes[volnume] != null
								&& levnume >= currentGame.episodes[volnume].nMaps) || levnume < 0)
							levnume = ud.level_number;
						ud.volume_number = volnume;
                        ud.level_number = levnume;
                       
						if(mUserFlag == UserFlag.UserMap)
							mUserFlag = UserFlag.None;
					} else return false;
					gGameScreen.enterlevel(gGameScreen.getTitle());
					if(Console.IsShown())
						Console.toggle();
					break;
				case 7: //rdskill
					if(opt.length == 1)
					{
						int skill = opt[0];
						if(skill < 1 || skill > 5)
							return false;
						ud.player_skill = skill-1; //XXX Check in RR

		                if(numplayers > 1 && myconnectindex == connecthead)
		                {
		                    tempbuf[0] = 5;
		                    tempbuf[1] = (byte) ud.level_number;
		                    tempbuf[2] = (byte) ud.volume_number;
		                    tempbuf[3] = (byte) ud.player_skill;
		                    tempbuf[4] = ud.monsters_off?(byte)1:0;
		                    tempbuf[5] = ud.respawn_monsters?(byte)1:0;
		                    tempbuf[6] = ud.respawn_items?(byte)1:0;
		                    tempbuf[7] = ud.respawn_inventory?(byte)1:0;
		                    tempbuf[8] = (byte) ud.coop;
		                    tempbuf[9] = (byte) ud.marker;
		                    tempbuf[10] = (byte) ud.ffire;
		
		                    for(int i=connecthead;i>=0;i=connectpoint2[i])
		                        sendpacket(i,tempbuf,11);
		                }
		                gGameScreen.enterlevel(gGameScreen.getTitle());
						if(Console.IsShown())
							Console.toggle();
					} else return false;
					break;
					
				case 3: //rdguns
					for ( int weapon = PISTOL_WEAPON;weapon < MAX_WEAPONSRA;weapon++ )
						ps[myconnectindex].gotweapon[weapon]  = true;
					for ( int weapon = PISTOL_WEAPON; weapon < (MAX_WEAPONSRA); weapon++ )
						addammo( weapon, ps[myconnectindex], currentGame.getCON().max_ammo_amount[weapon] );
					FTA(119,ps[myconnectindex]);
					break;
				case 4: //rdinventory
					ps[myconnectindex].moonshine_amount =         	400;
					ps[myconnectindex].boot_amount      =    		2000;
					ps[myconnectindex].shield_amount 	=           100;
					ps[myconnectindex].snorkle_amount 	=           6400;
					ps[myconnectindex].beer_amount 		=         	2400;
					ps[myconnectindex].cowpie_amount 	=          	600;
					ps[myconnectindex].whishkey_amount 	=         	(short) currentGame.getCON().max_player_health;
					FTA(120,ps[myconnectindex]);
					break;
				case 6: //rdkeys
					Arrays.fill(ps[myconnectindex].gotkey, (short)1);
                    FTA(121,ps[myconnectindex]);
					break;
				case 0: //rdall
					for ( int weapon = PISTOL_WEAPON;weapon < MAX_WEAPONSRA;weapon++ )
	                       ps[myconnectindex].gotweapon[weapon]  = true;

                    for ( int weapon = PISTOL_WEAPON; weapon < (MAX_WEAPONSRA); weapon++ )
                        addammo( weapon, ps[myconnectindex], currentGame.getCON().max_ammo_amount[weapon] );

                    ps[myconnectindex].moonshine_amount =         	400;
                    ps[myconnectindex].boot_amount      =    		2000;
                    ps[myconnectindex].shield_amount 	=           100;
                    ps[myconnectindex].snorkle_amount 	=           6400;
                    ps[myconnectindex].beer_amount 		=         	2400;
                    ps[myconnectindex].cowpie_amount 	=          	600;
                    ps[myconnectindex].whishkey_amount 	=         	(short) currentGame.getCON().max_player_health;

                    Arrays.fill(ps[myconnectindex].gotkey, (short)1);
                    FTA(5,ps[myconnectindex]);
                    ps[myconnectindex].inven_icon = 1;
					break;
				case 1: //rdclip
					ud.clipping = !ud.clipping;
                    ps[myconnectindex].cheat_phase = 0;
                    FTA(112+(ud.clipping?1:0),ps[myconnectindex]);
					break;
				case 2: //rdelvis
				case 18: //rdhounddog
					ud.god = !ud.god;

                    if(ud.god)
                    {
                        sprite[ps[myconnectindex].i].cstat = 257;

                        hittype[ps[myconnectindex].i].temp_data[0] = 0;
                        hittype[ps[myconnectindex].i].temp_data[1] = 0;
                        hittype[ps[myconnectindex].i].temp_data[2] = 0;
                        hittype[ps[myconnectindex].i].temp_data[3] = 0;
                        hittype[ps[myconnectindex].i].temp_data[4] = 0;
                        hittype[ps[myconnectindex].i].temp_data[5] = 0;

                        sprite[ps[myconnectindex].i].hitag = 0;
                        sprite[ps[myconnectindex].i].lotag = 0;
                        sprite[ps[myconnectindex].i].pal =
                            ps[myconnectindex].palookup;

                        FTA(17,ps[myconnectindex]);
                    }
                    else
                    {
                        ud.god = false;
                        sprite[ps[myconnectindex].i].extra = (short) currentGame.getCON().max_player_health;
                        hittype[ps[myconnectindex].i].extra = -1;
                        ps[myconnectindex].last_extra = (short) currentGame.getCON().max_player_health;
                        FTA(18,ps[myconnectindex]);
                    }

                    sprite[ps[myconnectindex].i].extra = (short) currentGame.getCON().max_player_health;
                    hittype[ps[myconnectindex].i].extra = 0;
					break;
				}
				ps[myconnectindex].cheat_phase = 0;
				return true;
			}
		}
		
		return false;
	}
}
