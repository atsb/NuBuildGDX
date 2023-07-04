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


import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.View.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Globals.*;

public class Cheats {

	public static final String cheatCode[] = {
		"LOBOCOP",
		"LOBOSWAG",
		"LOBODEITY",
		"LOBOPICK",
		"LOBOSPHERE",
		"LOBOSLIP",
		"LOBOSNAKE",
		"KIMBERLY",
		
		"LOBOLITE",
		"LOBOXY"
		
	};
	public static final int kCheatMax = cheatCode.length;
	
	public static boolean IsCheatCode(String message, int... opt)
	{
		for(int nCheatCode = 0; nCheatCode < kCheatMax; nCheatCode++)
		{
			if(message.equalsIgnoreCase(cheatCode[nCheatCode]))
			{
				switch(nCheatCode)
				{
				case 0:
					FillWeapons(nLocalPlayer);
					break;
				case 1:
					FillItems(nLocalPlayer);
					break;
				case 2:
					boolean god = PlayerList[nLocalPlayer].invisibility == -1;
					if(!god)
						PlayerList[nLocalPlayer].invisibility = -1;
					else PlayerList[nLocalPlayer].invisibility = 0;
					
					StatusMessage(750, "Deity mode " + (god ? "OFF" : "ON") + " for player " + nLocalPlayer, nLocalPlayer);
					break;
				case 3:
					PlayerList[nLocalPlayer].KeysBitMask |= (7+8) << 12;
					StatusMessage(750, "All keys for player " + nLocalPlayer, nLocalPlayer);
					break;
				case 4:
					GrabMap();
					StatusMessage(750, "Full map", nLocalPlayer);
					break;
				case 5:
					bSlipMode = !bSlipMode;
					StatusMessage(750, "Slip mode " + (bSlipMode ? "ON" : "OFF"), nLocalPlayer);
					break;
				case 6:
					bSnakeCam = !bSnakeCam;
					StatusMessage(750, "Snake camera mode " + (bSnakeCam ? "ON" : "OFF"), nLocalPlayer);
					break;
				case 7:
					StatusMessage(750, "HI SWEETIE, I LOVE YOU", nLocalPlayer);
					break;
				}
				return true;
			}
		}
		return false;
	}
}
