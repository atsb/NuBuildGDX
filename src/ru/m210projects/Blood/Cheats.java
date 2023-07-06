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

import static ru.m210projects.Blood.Actor.actDamageSprite;
import static ru.m210projects.Blood.Actor.actHealDude;
import static ru.m210projects.Blood.Actor.actSetBurnSource;
import static ru.m210projects.Blood.Actor.kDamageBullet;
import static ru.m210projects.Blood.Actor.kDamageExplode;
import static ru.m210projects.Blood.EVENT.evPostCallback;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Blood.LEVELS.kMaxEpisode;
import static ru.m210projects.Blood.LEVELS.kMaxMap;
import static ru.m210projects.Blood.LEVELS.levelEndLevel;
import static ru.m210projects.Blood.LEVELS.loadMapInfo;
import static ru.m210projects.Blood.PLAYER.PickupInventryItem;
import static ru.m210projects.Blood.PLAYER.gAmmoInfo;
import static ru.m210projects.Blood.PLAYER.getInventoryAmount;
import static ru.m210projects.Blood.PLAYER.kInventoryMax;
import static ru.m210projects.Blood.PLAYER.playerGodMode;
import static ru.m210projects.Blood.PLAYER.powerupActivate;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.PLAYER.powerupDeactivate;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Blood.View.viewSetMessage;

import ru.m210projects.Blood.Main.UserFlag;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class Cheats {

	private static final int kCheatMax = 37;

	public static final String[] cheatCode = {
		/*0*/"NQLGB",
		/*1*/"DBQJONZBTT",
		/*2*/"OPDBQJONZBTT",
		/*3*/"J!XBOOB!CF!MJLF!LFWJO",
		/*4*/"JEBIP",
		/*5*/"NPOUBOB",
		/*6*/"HSJTXPME",
		/*7*/"FENBSL",
		/*8*/"UFRVJMB",
		/*9*/"CVO{",
		/*10*/"GVOLZ!TIPFT",
		/*11*/"HBUFLFFQFS",
		/*12*/"LFZNBTUFS",
		/*13*/"KPKP",
		/*14*/"TBUDIFM",
		/*15*/"TQPSL",
		/*16*/"POFSJOH",
		/*17*/"NBSJP",
		/*18*/"DBMHPO",
		/*19*/"LFWPSLJBO",
		/*20*/"NDHFF",
		/*21*/"LSVFHFS",
		/*22*/"DIFFTFIFBE",
		/*23*/"DPVTUFBV",
		/*24*/"WPPSIFFT",
		/*25*/"MBSB!DSPGU",
		/*26*/"IPOHLPOH",
		/*27*/"GSBOLFOTUFJO",
		/*28*/"TUFSOP",
		/*29*/"DMBSJDF",
		/*30*/"GPSL!ZPV",
		/*31*/"MJFCFSNBO",
		/*32*/"FWB!HBMMJ",
		/*33*/"SBUF",
		/*34*/"HPPOJFT",
		/*35*/"TQJFMCFSH",
		/*36*/"FOE",
//		/*37*/"KFUQBDL"
	};

	public static boolean IsCheatCode(String message, int... opt)
	{
		for(int nCheatCode = 0; nCheatCode < kCheatMax; nCheatCode++)
		{
			if(message.equalsIgnoreCase(cheatCode[nCheatCode]))
			{
				switch(nCheatCode)
				{
				case 0: //MPKFA
					cheatGodMode(gMe.godMode?0:1);
	    			break;
				case 1: //CAPINMYASS
					cheatGodMode(0);
					break;
				case 2: //NOCAPINMYASS
				case 3: //I WANNA BE LIKE KEVIN
					cheatGodMode(1);
					break;
				case 4: //IDAHO
					cheatAmmuntion(true);
					break;
				case 5: //MONTANA
					cheatAmmuntion(true);
	    			cheatInventory(true);
	    			break;
				case 6: //GRISWOLD
					cheatArmor(true);
					break;
				case 7: //EDMARK
					actDamageSprite(gMe.nSprite, gMe.pSprite, kDamageExplode, 8000);
			        viewSetMessage("Ahhh...those were the days.", gPlayer[gViewIndex].nPlayer);
					break;
				case 8: //TEQUILA
					cheatAkimbo();
					break;
				case 9: //BUNZ
					cheatAmmuntion(true);
			        cheatAkimbo(true);
					break;
				case 10: //FUNKY SHOES
					cheatJumpBoots();
					break;
				case 12: //KEYMASTER
					cheatKeys(true);
					break;
				case 13: //JOJO
					cheatDelirious();
					break;
				case 14: //SATCHEL
					cheatInventory(true);
					break;
				case 15: //SPORK
					actHealDude(gMe.pXsprite, 200, 200);
					break;
				case 16: //ONERING
					 cheatInvisible();
					break;
				case 17: //MARIO
				case 18: //CALGON
				case 35: //SPIELBERG + start record demo ExMx.DEM
					if(opt.length == 2)
					{
						int nEpisode = opt[0] - 1;
						int nLevel = opt[1] - 1;
						if(nEpisode >= kMaxEpisode || nEpisode < 0)
							nEpisode = pGameInfo.nEpisode;
						if(nLevel >= kMaxMap || nLevel < 0)
							nLevel = pGameInfo.nLevel;

						if(mUserFlag == UserFlag.UserMap)
							mUserFlag = UserFlag.None;

						loadMapInfo(nEpisode, nLevel);

						gGameScreen.loadboard(pGameInfo.zLevelName, null).setTitle(gGameScreen.getTitle());
					} else if(opt.length == 1)
					{
						int nLevel = opt[0] - 1;
						if(nLevel >= kMaxMap || nLevel < 0)
							nLevel = pGameInfo.nLevel;

						if(mUserFlag == UserFlag.UserMap)
							mUserFlag = UserFlag.None;

						loadMapInfo(pGameInfo.nEpisode, nLevel);

						gGameScreen.loadboard(pGameInfo.zLevelName, null).setTitle(gGameScreen.getTitle());
					} else
						levelEndLevel(0);
					if(Console.IsShown())
						Console.toggle(); //hide console
					break;
				case 19: //KEVORKIAN
					actDamageSprite(gMe.nSprite, gMe.pSprite, kDamageBullet, 8000);
			        viewSetMessage("Kevorkian approves.", gPlayer[gViewIndex].nPlayer);
					break;
				case 20: //MCGEE
					 if ( gMe.pXsprite.burnTime == 0 )
				     	evPostCallback(gMe.nSprite, 3, 0, 0);
					 gMe.pXsprite.burnTime = ClipHigh(gMe.pXsprite.burnTime + 2400, 2400);
				     gMe.pXsprite.burnSource = actSetBurnSource(gMe.nSprite);
				     viewSetMessage("You're fired!", gPlayer[gViewIndex].nPlayer);
					break;
				case 21: //KRUEGER
					actHealDude(gMe.pXsprite, 200, 200);
					gMe.ArmorAmount[1] = 200;
					if ( gMe.pXsprite.burnTime == 0 )
				     	evPostCallback(gMe.nSprite, 3, 0, 0);
					 gMe.pXsprite.burnTime = ClipHigh(gMe.pXsprite.burnTime + 2400, 2400);
				     gMe.pXsprite.burnSource = actSetBurnSource(gMe.nSprite);
				     viewSetMessage("Flame retardant!", gPlayer[gViewIndex].nPlayer);
					break;
				case 22: //CHEESEHEAD
					gMe.Inventory[1].amount = 100;
					break;
				case 23: //COUSTEAU
					actHealDude(gMe.pXsprite, 200, 200);
					gMe.Inventory[1].amount = 100;
					break;
				case 24: //VOORHEES
					cheatInvulnerable();
					break;
				case 25: //LARA CROFT
					cheatInfinityAmmo(!gInfiniteAmmo);
			        cheatAmmuntion(gInfiniteAmmo);
					break;
				case 26: //HONGKONG
					cheatAmmuntion(true);
			        cheatInfinityAmmo(true);
					break;
				case 27: //FRANKENSTEIN
					gMe.Inventory[0].amount = 100;
					break;
				case 28: //STERNO
					gMe.blindEffect = 240;
					break;
				case 30: //FORK YOU
					cheatInfinityAmmo(false);
			        cheatFullMap(false);
			        cheatAmmuntion(false);
			        cheatAmmo(false);
			        cheatArmor(false);
			        cheatInventory(false);
			        cheatKeys(false);
			        cheatAkimbo();
			        powerupActivate(gMe, 28);
			        gMe.pXsprite.health = 16;
			        gMe.hasWeapon[1] = true;
			        gMe.currentWeapon = 0;
			        gMe.updateWeapon = 1;
					break;
				case 32: //EVA GALLI
					gNoClip = !gNoClip;
					if ( gNoClip )
			            viewSetMessage("Unclipped movement.", gPlayer[gViewIndex].nPlayer);
					else
						viewSetMessage("Normal movement.", gPlayer[gViewIndex].nPlayer);
					break;
				case 33: //RATE
					break;
				case 34: //GOONIES
					cheatFullMap(!gFullMap);
					break;
				case 36: //END
					levelEndLevel(0);
					break;
                    case 11: //GATEKEEPER
				case 29: //CLARICE
				case 31: //LIEBERMAN
					break;
				}
				cheatsOn = true;
				return true;
			}
		}
		return false;
	}

	public static void cheatFullMap(boolean type)
	{
		gFullMap = type;
		if ( type )
		  	viewSetMessage("You have the map.", gPlayer[gViewIndex].nPlayer);
		else
		    viewSetMessage("You have no map.", gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatInfinityAmmo(boolean type)
	{
		gInfiniteAmmo = type;
		if ( type )
		  	viewSetMessage("You have infinite ammo.", gPlayer[gViewIndex].nPlayer);
		else
		    viewSetMessage("You have limited ammo.", gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatInvulnerable()
	{
		if(powerupCheck(gMe, 14) == 0) {
			viewSetMessage("You are invulnerable.", gPlayer[gViewIndex].nPlayer);
			powerupActivate(gMe, 14);
		}
		else {
			viewSetMessage("You are vulnerable.", gPlayer[gViewIndex].nPlayer);
			powerupDeactivate(gMe, 14);
		}
	}

	public static void cheatInvisible()
	{
		if(powerupCheck(gMe, 13) == 0) {
			viewSetMessage("You are invisible.", gPlayer[gViewIndex].nPlayer);
			powerupActivate(gMe, 13);
		}
		else {
			viewSetMessage("You are visible.", gPlayer[gViewIndex].nPlayer);
			powerupDeactivate(gMe, 13);
		}
	}

	public static void cheatDelirious()
	{
		if(powerupCheck(gMe, 28) == 0) {
			viewSetMessage("You are delirious.", gPlayer[gViewIndex].nPlayer);
			powerupActivate(gMe, 28);
		}
		else {
			viewSetMessage("You are not delirious.", gPlayer[gViewIndex].nPlayer);
			powerupDeactivate(gMe, 28);
		}
	}

	public static void cheatJumpBoots()
	{
		if(getInventoryAmount(gMe, 4) == 0) {
			viewSetMessage("You have the Jumping Boots.", gPlayer[gViewIndex].nPlayer);

			PickupInventryItem(gMe, 4);
		}
		else {
			viewSetMessage("You have no Jumping Boots.", gPlayer[gViewIndex].nPlayer);

			gMe.Inventory[4].amount = 0;
			gMe.Inventory[4].activated = false;
		}
	}

	public static void cheatAkimbo()
	{
		cheatAkimbo(powerupCheck(gMe, 17) == 0);
	}

	public static void cheatAkimbo(boolean type)
	{
		if ( type )
		{
			if(powerupCheck(gMe, 17) == 0)
				powerupActivate(gMe, 17);
		}
		else
		{
		    if ( powerupCheck(gMe, 17) != 0 )
		    	powerupDeactivate(gMe, 17);
		}
	}

	public static void cheatArmor(boolean mode)
	{
		int amount = 3200;
		String message;
		if ( mode )
			message = "You have full armor.";
		else {
			message = "You have no armor.";
			amount = 0;
		}

		for ( int i = 0; i < 3; i++ )
			gMe.ArmorAmount[i] = amount;

		viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatKeys(boolean mode)
	{
		for ( int i = 0; i <= 6; i++ )
			gMe.hasKey[i] = mode;
		String message;
		if ( mode )
			message = "You have all keys.";
		else
			message = "You have no keys.";
		viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatGodMode(int mode)
	{
		if((mode == 1 && !gMe.godMode) || mode == 0 && gMe.godMode)
			playerGodMode(gMe, mode);
		String message;
		if ( gMe.godMode )
		    message = "You are immortal.";
		else
			message = "You are mortal.";
		viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatAmmuntion(boolean mode)
	{
		for ( int i = 0; i < 14; i++ )
			gMe.hasWeapon[i] = mode;

		if(SHAREWARE)
		{
			for(int i = 7; i < 14; i++)
				if(i != 10)
					gMe.hasWeapon[i] = false;
		}

		cheatAmmo(mode);
		String message;
		if ( mode )
			message = "You have all weapons.";
		else
			message = "You have no weapons.";
		viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatAmmo(boolean mode)
	{
		String message;
		if(mode)
		{
			for ( int i = 0; i < 12; i++ )
				gMe.ammoCount[i] = gAmmoInfo[i].max;
			message = "You have full ammo.";

			if(SHAREWARE)
			{
				for(int i = 6; i < 12; i++)
					if(i != 9)
						gMe.ammoCount[i] = 0;
			}
		}
		else
		{
			for ( int i = 0; i < 12; i++ )
				gMe.ammoCount[i] = 0;
			message = "You have no ammo.";
		}
		viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatAddInventory(PLAYER pPlayer)
	{
		for ( int i = 0; i < kInventoryMax; i++ ) {
			if (i == 5) continue;
			PickupInventryItem(pPlayer, i);
		}
	}

	public static void cheatAddJetpack(PLAYER pPlayer)
	{
		PickupInventryItem(pPlayer, 5);
		viewSetMessage("You have a jetpack.", gPlayer[gViewIndex].nPlayer);
	}

	public static void cheatSubInventory(PLAYER pPlayer)
	{
		pPlayer.choosedInven = 0;
		for ( int i = 0; i < 5; i++ ) {
			pPlayer.Inventory[i].amount = 0;
			pPlayer.Inventory[i].activated = false;
		}
	}

	public static void cheatInventory(boolean mode)
	{
		if(mode)
		{
			cheatAddInventory(gMe);
			viewSetMessage("Your inventory is full.", gPlayer[gViewIndex].nPlayer);
		} else
		{
			cheatSubInventory(gMe);
			viewSetMessage("Your inventory is empty.", gPlayer[gViewIndex].nPlayer);
		}
	}

}
