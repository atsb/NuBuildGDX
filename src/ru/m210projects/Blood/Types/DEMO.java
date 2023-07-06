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

package ru.m210projects.Blood.Types;

import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.FileHandle.Compat.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class DEMO {

	public static List<String> demofiles = new ArrayList<String>();
	public static final String kBloodDemSig	=	"DEM\032";

	public int 	   nInputCount;			   // number of INPUT structures recorded
	public int     nNetPlayers;            // number of players recorded
	public short   nMyConnectIndex;        // the ID of the player
	public short   nConnectHead;           // index into connectPoints of Player
	public short[] connectPoints = new short[kMaxPlayers];      // IDs of players

	public String signature;
	public int nVersion;
	public int rcnt;

	public INPUT[][] pDemoInput; // = new INPUT[16384][kMaxPlayers];

	public DEMO(Resource bb) {
		byte[] buf = new byte[4];
		bb.read(buf);
		signature = new String(buf);
		nVersion = bb.readShort();

		if(nVersion == 277)
			bb.readInt(); //int nBuild - for demos > 1.10

		/*
		   // build of Blood that created the demo
           // builds are:
           // SW, SWCD:   2
           // Registered: 3
           // Plasma Pak: 4
		*/

		nInputCount = bb.readInt();
		nNetPlayers = bb.readInt();
		nMyConnectIndex = bb.readShort();
		nConnectHead = bb.readShort();
		for(int i = 0; i < 8; i++)
			connectPoints[i] = bb.readShort();

		//GameInfo
		pGameInfo.nGameType = bb.readByte() & 0xFF;
		pGameInfo.nDifficulty = bb.readByte() & 0xFF;
		pGameInfo.nEpisode = bb.readInt();
		pGameInfo.nLevel = bb.readInt();

		buf = new byte[144];
		bb.read(buf);
		String name = new String(buf);
		int index = -1;
		if((index = name.indexOf(0)) != -1)
			name = name.substring(0, index).toLowerCase();
		if(FileUtils.isExtension(name, "map"))
			name = name.substring(0, name.lastIndexOf('.'));

		pGameInfo.zLevelName = name;
		bb.read(buf);
		pGameInfo.zLevelSong = new String(buf);
		if((index = pGameInfo.zLevelSong.indexOf(0)) != -1)
			pGameInfo.zLevelSong = pGameInfo.zLevelSong.substring(0, index);
		pGameInfo.nTrackNumber = bb.readInt();
        for(int i = 0; i < 4; i++)
        	bb.readInt(); //szSaveGameName
        for(int i = 0; i < 4; i++)
        	bb.readInt(); //szUserGameName
        bb.readShort(); //nSaveGameSlot
        bb.readInt(); //picEntry
        pGameInfo.uMapCRC = bb.readInt(); //uMapCRC

        pGameInfo.nMonsterSettings = bb.readByte() & 0xFF;
        pGameInfo.uGameFlags = bb.readInt();
        pGameInfo.uNetGameFlags = bb.readInt();
        pGameInfo.nWeaponSettings = bb.readByte() & 0xFF;
        pGameInfo.nItemSettings   = bb.readByte() & 0xFF;
        pGameInfo.nRespawnSettings = bb.readByte() & 0xFF;
        pGameInfo.nTeamSettings = bb.readByte() & 0xFF;
        pGameInfo.nMonsterRespawnTime = bb.readInt();
        pGameInfo.nWeaponRespawnTime  = bb.readInt();
        pGameInfo.nItemRespawnTime  = bb.readInt();
        pGameInfo.nSpecialRespawnTime  = bb.readInt();

        pGameInfo.nEnemyQuantity = pGameInfo.nDifficulty;
        pGameInfo.nEnemyDamage = pGameInfo.nDifficulty;
        pGameInfo.nPitchforkOnly = false;

        if(pDemoInput == null || pDemoInput.length <= nInputCount)
        	pDemoInput = new INPUT[nInputCount][kMaxPlayers];

		buf = new byte[22];
		for(int rcnt = 0; rcnt < nInputCount; rcnt++)
			for ( int i = nConnectHead; i >= 0; i = connectPoints[i] )
		    {
				if((bb.position() + buf.length) > bb.size()) return;
				bb.read(buf);
				pDemoInput[rcnt][i] = new INPUT(buf, nVersion);
		    }

		rcnt = 0;
	}

	public static int nDemonum = -1;
	public static DEMO demfile = null;
	public static final char[] demfilename = { 'B', 'L', 'O', 'O', 'D', 'X',
			'X', 'X', '.', 'D', 'E', 'M' };

	public static boolean demoscan(FileEntry file, boolean showErrorMessage) {
		boolean out = false;
		Resource fil = null;
		if ((fil = BuildGdx.compat.open(file)) != null) {
			String signature = fil.readString(4);
			int version = fil.readShort();
			if (signature.equals(DEMO.kBloodDemSig) && (version == 277))
				out = true;
			else if(showErrorMessage) {
				/*
				 * 266 - v1.10 267 - v1.11 277 - v1.21
				 */
				String demVersion = "unknown";
				if(version == 256)
					demVersion = "v1.00";
				if(version == 266)
					demVersion = "v1.10";
				else if(version == 267)
					demVersion = "v1.11";

				String name = file.getFile().getName();
				Console.Println("Wrong version of the demofile found: " + name + " (" + demVersion + " != v1.21)", Console.OSDTEXT_RED);
			}
			fil.close();
		}
		return out;
	}

	public static void demoscan() {
		for (FileEntry file : BuildGdx.compat.getDirectory(Path.Game).getFiles().values()) {
			if (file.getExtension().equals("dem")) {
				String name = file.getFile().getName();
				if (demoscan(file, true))
					demofiles.add(name);
			}
		}

		if(demofiles.size() != 0)
			Collections.sort(demofiles);
		Console.Println("There are " + demofiles.size() + " demo(s) in the loop", OSDTEXT_GOLD);

		if(cfg.gDemoSeq == 2)
		{
			int nextnum = nDemonum;
			if(demofiles.size() > 1) {
				while(nextnum == nDemonum)
					nextnum = (int) (Math.random() * (demofiles.size()));
			}
			nDemonum = nextnum;
		}
	}

	public static boolean IsOriginalDemo() {
		return (pGameInfo.nGameType == kNetModeOff && cfg.gVanilla) || (game.isCurrentScreen(gDemoScreen) && demfile != null && demfile.nVersion == 277);
	}

}
