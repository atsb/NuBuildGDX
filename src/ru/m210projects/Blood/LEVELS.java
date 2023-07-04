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

import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.DB.kDudeBat;
import static ru.m210projects.Blood.DB.kDudeBurning;
import static ru.m210projects.Blood.DB.kDudeInnocent;
import static ru.m210projects.Blood.DB.kDudeRat;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kMaxTiles;
import static ru.m210projects.Blood.Globals.kNetModeCoop;
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.appdef;
import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.Main.mUserFlag;
import static ru.m210projects.Blood.Mirror.InitMirrorTiles;
import static ru.m210projects.Blood.PLAYER.playerReset;
import static ru.m210projects.Blood.PLAYER.resetInventory;
import static ru.m210projects.Blood.SOUND.pSFXs;
import static ru.m210projects.Blood.SOUND.usertrack;
import static ru.m210projects.Blood.Tile.shadeInit;
import static ru.m210projects.Blood.Tile.tileInit;
import static ru.m210projects.Blood.Tile.voxelsInit;
import static ru.m210projects.Blood.Types.GAMEINFO.CutsceneB;
import static ru.m210projects.Blood.Types.GAMEINFO.EndOfGame;
import static ru.m210projects.Blood.Types.GAMEINFO.EndOfLevel;
import static ru.m210projects.Blood.VERSION.hasQFN;
import static ru.m210projects.Blood.View.InitBallBuffer;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Blood.View.viewSetMessage;
import static ru.m210projects.Blood.Weapon.WeaponInit;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Blood.Main.UserFlag;
import ru.m210projects.Blood.Fonts.QFNFont;
import ru.m210projects.Blood.Types.BloodDef;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.EpisodeInfo;
import ru.m210projects.Blood.Types.MapInfo;
import ru.m210projects.Blood.Types.Seq.SeqType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.PackedZipGroup;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.UserGroup;
import ru.m210projects.Build.FileHandle.ZipGroup;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.Tools.IniFile;
import ru.m210projects.Build.Script.CueScript;
import ru.m210projects.Build.Types.SPRITE;

public class LEVELS {

	public static final int kMinEpisode = 1;
	public static final int kMaxEpisode = 6;
	public static final int kMinMap     = 1;
	public static final int kMaxMap     = 16;
	public static final int kMaxMessages = 128;

	//Statistics
//	public static int gEndLevelFlag = 0;
//	public static int gOldEndLevelFlag = 0;
//	public static int gLeaveLevel = 0;
	public static int foundSecret = 0;
	public static int totalSecrets = 0;
	public static int autoTotalSecrets = 0;
	public static int superSecrets = 0;
	public static int totalKills = 0;
	public static int kills = 0;

	public static int gNextMap;
	public static int nEpisodeCount;

	public static EpisodeInfo currentEpisode;
	public static EpisodeInfo[] gEpisodeInfo = new EpisodeInfo[kMaxEpisode];
	public static EpisodeInfo[] gUserEpisodeInfo = new EpisodeInfo[kMaxEpisode];

	public static HashMap<String, BloodIniFile> episodes;

	public static MapInfo gUserMapInfo = new MapInfo();

//	public static HashMap<String, Integer> bldIDs = new HashMap<String, Integer>(); //for replace blood.rff resources


	public static void levelCalcKills() {
		totalKills = 0;
		for ( int i = headspritestat[kStatDude]; i >= 0; i = nextspritestat[i] )
		{
			SPRITE pSprite = sprite[i];
			if(!IsDudeSprite(pSprite)) {
				Console.Println("Warning: pDude.type >= kDudeBase && pDude.type < kDudeMax : " + pSprite.lotag);
				continue;
			}

			if(pSprite.statnum == kStatDude)
			{
				if ( pSprite.lotag != kDudeBat && pSprite.lotag != kDudeRat && pSprite.lotag != kDudeInnocent && pSprite.lotag != kDudeBurning )
			    	++totalKills;
			}
		}
	}

	public static String levelEpisodePath(BloodIniFile ini)
	{
		if(ini.isPackage())
			return ini.getFile().getPath() + ":" + ini.getName();
		else return ini.getFile().getPath();
	}

	public static BloodIniFile levelGetEpisode(String filepath)
	{
		if(filepath == null) return null;

		String fullname = filepath;
		String packedIni = null;
		int filenameIndex = -1;
		if((filenameIndex = fullname.indexOf(":")) != -1)
		{
			filepath = fullname.substring(0, filenameIndex);
			packedIni = fullname.substring(filenameIndex+1);
		}

		FileEntry file = BuildGdx.compat.checkFile(filepath);
		if(file != null)
		{
			BloodIniFile ini = null;
			if(filenameIndex == -1 && (ini = episodes.get(file.getPath())) == null)
			{
				if(file.getExtension().equals("ini")) {
					Resource res = BuildGdx.compat.open(file);
					byte[] data = res.getBytes();
					res.close();
					if(data == null) {
						Console.Println("file is exists, but data == null!" + file.getPath());
					}
					ini = new BloodIniFile(data, file.getName(), file);
					episodes.put(file.getPath(), ini);
				}
			}
			else if(filenameIndex != -1 && (ini = episodes.get(fullname)) == null)
			{
				if(file.getExtension().equals("zip")
					|| file.getExtension().equals("grp")
					|| file.getExtension().equals("rff"))
				{
					try {
						Group res = BuildGdx.cache.isGroup(file.getPath());
						if(res != null)
						{
							for(GroupResource files : res.getList()) {
								if(files.getExtension().equals("ini") && files.getFullName().equals(packedIni)) {
									Resource gres = res.open(files.getFullName());
									byte[] data = gres.getBytes();
									ini = new BloodIniFile(data, packedIni, file);
									ini.setPackage(true);
									episodes.put(fullname, ini);
									gres.close();
								}
							}
						}
						res.dispose();
						res = null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
			return ini;
		}

		return null;
	}

	public static void loadUserMapInfo(String mLevelName)
	{
		pGameInfo.zLevelName = gUserMapInfo.MapName = gUserMapInfo.Title = mLevelName;
		gUserMapInfo.Song = null;
		gUserMapInfo.Track = 0;

		String defFile = mLevelName.substring(0, mLevelName.lastIndexOf('.')) + ".def";
		FileEntry file = BuildGdx.compat.checkFile(defFile);
		if(file != null) {
			FileResource res = BuildGdx.compat.open(file);
			BloodIniFile ini = new BloodIniFile(res.getBytes(), defFile, file);
			res.close();
			ini.initDef();

			gUserMapInfo.Title = ini.GetKeyString("Title");
			if(gUserMapInfo.Title == null)
				gUserMapInfo.Title = gUserMapInfo.MapName;
			String song = ini.GetKeyString("Song");
			String currPath = ini.getFile().getParent().getName();
			if(ini.getFile().getParent() != BuildGdx.compat.getDirectory(Path.Game))
				song = currPath + File.separator + song;
			gUserMapInfo.Song = song;
			gUserMapInfo.Track = ini.GetKeyInt("Track");
		} else {
			file = BuildGdx.compat.checkFile(gUserMapInfo.Title);
			if(file != null) gUserMapInfo.Title = file.getName();
		}

		pGameInfo.nEpisode = 0;
		pGameInfo.nLevel = kMaxMap;

		pGameInfo.zLevelSong = gUserMapInfo.Song;
		pGameInfo.nTrackNumber = gUserMapInfo.Track;
		currentEpisode = null;
	}

	public static void levelSetupSecret(int command) {
		if(cfg.useautosecretcount)
			totalSecrets = autoTotalSecrets;
		else
			totalSecrets = command;
	}

	public static void initEpisodeInfo(BloodIniFile MainINI) { //Main.java only
		for(int i = 0; i < kMaxEpisode; i++)
		{
			gEpisodeInfo[i] = new EpisodeInfo();
			gEpisodeInfo[i].filename = MainINI.getName();
			gUserEpisodeInfo[i] = new EpisodeInfo();
		}
		nEpisodeCount = kMaxEpisode;

		episodes = new HashMap<String, BloodIniFile>();
		String path = MainINI.getFile().getPath();
		if(path == null)
			path = MainINI.getFile().getName();
		episodes.put(path, MainINI);
	}

	private static String handleCutscenePath(String path)
	{
		String out = path;
//		int index;
		if(path != null) {
			if(out.startsWith("."))
				out = out.substring(1);

			out = FileUtils.getCorrectPath(out);

			/*if((index = out.indexOf(":" + File.separator)) != -1)
				out = out.substring(index + 2);
			else*/ if(out.startsWith(File.separator))
				out = out.substring(1);
		}

		return out;
	}

	public static int getEpisodeInfo(EpisodeInfo[] pEpisodes, BloodIniFile INI) {
		int nEpisodeCount = 0;
		for(int i = 0; i < kMaxEpisode; i++)
		{
			EpisodeInfo pEpisode = pEpisodes[i];
			pEpisode.clear();
			if(INI.set("Episode" + (i + 1))) {
				if(INI.isPackage())
					pEpisode.filename = INI.getFile().getName() + ":" + INI.getName();
				else
					pEpisode.filename = INI.getName();
				String title = INI.GetKeyString("Title");
				if(title == null || title.isEmpty())
					title = "Episode" + (i + 1);
				pEpisode.Title = title;
				pEpisode.CutSceneA = handleCutscenePath(INI.GetKeyString("CutSceneA"));
				String episodePath = INI.getFile().getParent().getRelativePath();
				pEpisode.CutWavA = handleCutscenePath(INI.GetKeyString("CutWavA"));
				pEpisode.CutSceneB = handleCutscenePath(INI.GetKeyString("CutSceneB"));
				pEpisode.CutWavB = handleCutscenePath(INI.GetKeyString("CutWavB"));
				if(episodePath != null || INI.isPackage())
				{
					pEpisode.CutSceneA = game.getFilename(pEpisode.CutSceneA);
					pEpisode.CutSceneB = game.getFilename(pEpisode.CutSceneB);
					pEpisode.CutWavA = game.getFilename(pEpisode.CutWavA);
					pEpisode.CutWavB = game.getFilename(pEpisode.CutWavB);
				}
				String BloodBatchOnly = INI.GetKeyString("BloodBathOnly");
				if(BloodBatchOnly != null && BloodBatchOnly.charAt(0) == '1')
					pEpisode.BBOnly = 1;
				else pEpisode.BBOnly = 0;

				pEpisode.CutSceneALevel = 0;
				int CutSceneALevel = INI.GetKeyInt("CutSceneALevel");
				if(CutSceneALevel != -1)
					pEpisode.CutSceneALevel = CutSceneALevel - 1;

				int nMaps = 0;
				for(int j = kMinMap; j < kMaxMap; j++) {
					INI.set("Episode" + (i + 1));
					String mapName = INI.GetKeyString("Map" + j);
					if(mapName != null) {
						if(pEpisode.gMapInfo[j - 1] == null)
							pEpisode.gMapInfo[j - 1] = new MapInfo();
						MapInfo pMap = pEpisode.gMapInfo[j - 1];
						INI.set(mapName);
						pMap.MapName = mapName;
						levelLoadDef(pMap, INI);
						nMaps++;
					}
				}
				if(nMaps == 0)
					pEpisode.clear();
				else {
					pEpisode.nMaps = nMaps;
					pEpisode.setDirectory(INI.getFile().getParent());
					nEpisodeCount++;
				}
			}
		}
		return nEpisodeCount;
	}

	public static void levelCountSecret(int command) {
		if(command < 0) {
			System.err.println("Invalid secret type " + command + " triggered");
			return;
		}
		if ( command != 0 )
			superSecrets++;
		else
			foundSecret++;

		if( pGameInfo.nGameType == 0) {
			if( Random(2) == 1 )
				viewSetMessage("You found a secret.", gPlayer[gViewIndex].nPlayer, 9);
			else
				viewSetMessage("A secret is revealed.", gPlayer[gViewIndex].nPlayer, 9);
		}
	}

	public static void levelResetKills()
	{
		kills = totalKills = 0;
	}

	public static void levelResetSecrets()
	{
		foundSecret = totalSecrets = superSecrets = 0;
	}

	public static void levelAddKills(SPRITE pSprite)
	{
		if ( pSprite.statnum == kStatDude )
		{
			if ( pSprite.lotag != kDudeBat && pSprite.lotag != kDudeRat && pSprite.lotag != kDudeInnocent && pSprite.lotag != kDudeBurning )
		    	++kills;
		}
	}

	public static void prepareusergroup(Group group) throws Exception
	{
		if(group.type == PackageType.Zip) //Correct path in archive (files shouldn't be in a subfolder)
			((ZipGroup) group).removeFolders();
		else if(group.type == PackageType.PackedZip)
			((PackedZipGroup) group).removeFolders();

		if(!group.containsType("rff") && !group.containsType("grp") && !group.containsType("zip")
				&& !group.containsType("rfs") && !group.containsType("cue"))
			return; //nothing to search
		List<GroupResource> list = group.getList();

		//Searching and loading rfs scripts
		for(GroupResource res : list) {
			switch (res.getExtension()) {
				case "rff":
				case "grp":
				case "zip":
					BuildGdx.cache.add(res, group.isRemovable());
//				res.close();
					break;
				case "rfs":
					new RFScript(group, res.getFullName(), res.getBytes());
					res.flush(); //flush the buffer after getBytes() call

					break;
				case "cue":
					CueScript cdTracks = new CueScript(res.getFullName(), res.getBytes());
					String[] cdtracks = cdTracks.getTracks();
					int num = 0;
					for (int i = 0; i < cdtracks.length; i++)
						if (cdtracks[i] != null) {
							usertrack[i] = game.getFilename(cdtracks[i]);
							num++;
						}
					Console.Println(num + " cd tracks found...");
					res.flush(); //flush the buffer after getBytes() call

					break;
			}
		}

//		//Apply rfs script to files
//		if(haveRFS && !removable)
//			for(GroupResource res : list)
//				res.setIdentification(rfsfileid(res.getFullName()));
	}

	private static UserGroup usergroup;
	public static boolean usecustomarts;
	public static boolean usecustomqavs;
	public static void searchEpisodeResources(DirectoryEntry cache, HashMap<String, Integer> fileids)
	{
		//search rfs scripts first
		for (FileEntry file : cache.getFiles().values()) {
			Resource res;
			if (file.getExtension().equals("rfs")) {
				res = BuildGdx.compat.open(file);
				if (res != null) {
					RFScript scr = new RFScript(null, file.getPath(), res.getBytes());
					fileids.putAll(scr.getIds());
					res.close();
				}
			} else if (file.getExtension().equals("cue")) {
				res = BuildGdx.compat.open(file);
				if (res != null) {
					CueScript cdTracks = new CueScript(res.getFullName(), res.getBytes());
					String[] cdtracks = cdTracks.getTracks();
					int num = 0;
					for (int i = 0; i < cdtracks.length; i++)
						if (cdtracks[i] != null) {
							usertrack[i] = game.getFilename(cdtracks[i]);
							num++;
						}
					Console.Println(num + " cd tracks found...");
					res.close();
				}
			}
		}

		if(cache.getDirectories().size() > 0)
		{
			for (DirectoryEntry dir : cache.getDirectories().values()) {
				dir.InitDirectory(dir.getAbsolutePath());
				if (dir != BuildGdx.compat.getDirectory(Path.User))
					searchEpisodeResources(dir, fileids);
			}
		}

		for (FileEntry file : cache.getFiles().values()) {
			if (file.getExtension().equals("zip")
					|| file.getExtension().equals("grp")
					|| file.getExtension().equals("rff")) {
				try {
					Group gr = BuildGdx.cache.add(file.getPath());
					gr.setFlags(true, true);
					prepareusergroup(gr);
				} catch (Exception e) {
					Console.Println("Error to load " + file.getName(), OSDTEXT_RED);
				}
			}
		}

		if(usergroup == null)
			usergroup = BuildGdx.cache.add("User", true);

		for (FileEntry file : cache.getFiles().values()) {
			if (!file.getExtension().equals("ini")
					&& !file.getExtension().equals("rfs")
					&& !file.getExtension().equals("zip")
					&& !file.getExtension().equals("grp")
					&& !file.getExtension().equals("rff")) {
				Integer id = fileids.get(file.getName());
				usergroup.add(file, id != null ? id : -1);
			}
		}
	}

	public static void checkEpisodeResources(BloodIniFile ini)
	{
		if(ini == null)
			return;

		resetEpisodeResources();

		BloodDef addonScript = new BloodDef(game.getBaseDef(), ini.getFile());

		DirectoryEntry currdir = ini.getFile().getParent();
		if(ini.isPackage()) //if in main blood folder
		{
			try {
				Group gr = BuildGdx.cache.add(ini.getFile().getPath());
				gr.setFlags(true, true);

				Resource res = gr.open(appdef); //load def scripts before delete folders
				if(res != null)
				{
					addonScript.loadScript(gr.name + " script", res.getBytes());
					res.close();
				}

				prepareusergroup(gr);
			} catch(Exception e) {
				game.GameMessage("Error found in " + ini.getFile().getName() + "\r\n" + e.getMessage());
				return;
			}
		} else if(currdir != BuildGdx.compat.getDirectory(Path.Game)) {
			HashMap<String, Integer> fileids = new HashMap<String, Integer>();
			searchEpisodeResources(ini.getFile().getParent(), fileids);

			if(currdir != null) {
				FileEntry def = currdir.checkFile(appdef);
				if(def != null) {
					addonScript.loadScript(def);
				}
			}
		}

		if(ini.getName().equals("cryptic.ini")) //official addon
		{

			String artname = "CPART07.AR_";
			if(currdir != BuildGdx.compat.getDirectory(Path.Game))
				artname = currdir.getName() + File.separator + artname;
			engine.loadpic(artname);
			artname = "CPART15.AR_";
			if(currdir != BuildGdx.compat.getDirectory(Path.Game))
				artname = currdir.getName() + File.separator + artname;
			engine.loadpic(artname);
			usecustomarts = true;
		}

		InitGroupResources(addonScript, BuildGdx.cache.getDynamicResources());
		game.setDefs(addonScript);

		InitBallBuffer();
	}

	public static void InitGroupResources(BloodDef script, List<GroupResource> list)
	{
		for(GroupResource res : list) {
//			Integer fileid;
//			if((fileid = bldIDs.get(res.getFullName())) != null && res.getIdentification() <= 0)
//				res.setIdentification(fileid);

			if(res.getExtension().equals("art")) {
				engine.loadpic(res.getFullName());
				usecustomarts = true;
			} else if(res.getFullName().equals("voxel.dat"))
			{
				voxelsInit(res.getFullName());
				System.err.println("Found voxel.dat. Loading... ");
			} else if(res.getFullName().equals("surface.dat")) {
				script.surfaceInit(res.getFullName());
				System.err.println("Found surface.dat. Loading... ");
			} else if(res.getFullName().equals("shade.dat")) {
				shadeInit(res.getFullName());
				System.err.println("Found shade.dat. Loading... ");
			} else if(res.getExtension().equals("qav"))
				usecustomqavs = true;
		}

		if(usecustomqavs) {
			System.err.println("Found qav files. Loading... ");
			WeaponInit(); //reload qavs animations
		}
	}

	public static void resetEpisodeResources()
	{
		BuildGdx.cache.clearDynamicResources();

		usergroup = null;

		Arrays.fill(pSFXs, null); //reset user sounds
		SeqType.flushCache(); //reset user seqs
		Arrays.fill(usertrack, null); //reset cd tracks

		if(usecustomqavs) {
			WeaponInit();
			usecustomqavs = false;
		}

		if(!usecustomarts) {
			game.setDefs(game.baseDef);
			return;
		}

		//Reset to default resources

		System.err.println("Reset to default resources");
		for(int i = 0; i < kMaxTiles; i++) //don't touch usertiles
			engine.getTile(i).clear();

		if(engine.loadpics() == 0) {
			game.dassert("ART files not found " + new File(Path.Game.getPath() + engine.tilesPath).getAbsolutePath());
			return;
		}

		tileInit();
		game.setDefs(game.baseDef);

		InitMirrorTiles();

		if(hasQFN) {
			for(int i = 0; i < 5; i++)
				((QFNFont)game.getFont(i)).rebuildChar();
		}


		InitBallBuffer();
		usecustomarts = false;
	}

	public static void loadMapInfo(int nEp, int nMap) {
		pGameInfo.nEpisode = nEp;
		pGameInfo.nLevel = nMap;

		if(mUserFlag != UserFlag.Addon)
			currentEpisode = gEpisodeInfo[pGameInfo.nEpisode];
		else currentEpisode = gUserEpisodeInfo[pGameInfo.nEpisode];

		if(currentEpisode.gMapInfo[nMap] != null && currentEpisode.gMapInfo[nMap].MapName != null) {
			pGameInfo.zLevelSong = currentEpisode.gMapInfo[nMap].Song;
			pGameInfo.zLevelName = currentEpisode.gMapInfo[nMap].MapName;
			pGameInfo.nTrackNumber = currentEpisode.gMapInfo[nMap].Track;
		} else if(mUserFlag != UserFlag.UserMap) {
			pGameInfo.zLevelName = "Unknown";
			pGameInfo.nLevel = 0;
		} // else zLevelName and nLevel already loaded
	}

	public static void levelLoadDef(MapInfo pMap, IniFile INI) {
		pMap.Title = INI.GetKeyString("Title");
		if(pMap.Title == null)
			pMap.Title = pMap.MapName;
		pMap.Author = INI.GetKeyString("Author");
		pMap.Song = INI.GetKeyString("Song");
		String Song = INI.GetKeyString("Song");
//		if(Song != null && midPath != null) {
//			int hResource = kOpen(Song + "." + mid, 0);
//			if(hResource == -1) {
//				Song = midPath + File.separator + Song;
//			} else
//				kClose(hResource);
//		}
		pMap.Song = Song;
		pMap.Track = INI.GetKeyInt("Track");
		pMap.EndingA = INI.GetKeyInt("EndingA");
		pMap.EndingB = INI.GetKeyInt("EndingB");
		pMap.Fog = INI.GetKeyInt("Fog") == 1;
		pMap.Weather = INI.GetKeyInt("Weather") == 1;

		for( int i = 1; i < kMaxMessages; i++) {
			pMap.gMessage[i-1] = INI.GetKeyString("Message" + i);
		}
	}


	public static void levelEndLevel(int levType) {

		if(numplayers > 1 && game.pNet.bufferJitter > 1 && myconnectindex == connecthead)
			for(int i = 0; i < game.pNet.bufferJitter; i++)
				game.pNet.GetNetworkInput(); //wait for other player before level end

		if(!game.pNet.WaitForAllPlayers(5000)) {
			game.pNet.NetDisconnect(myconnectindex);
			return;
		}

		if(pGameInfo.nGameType == kNetModeCoop && pGameInfo.nReviveMode)
		{
			for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
			{
				if(gPlayer[i].deathTime > 0) {
					resetInventory(gPlayer[i]);
			    	playerReset(i);
				}
			}
		}

		pGameInfo.uGameFlags |= EndOfLevel;
		getInput().setKey(ANYKEY, 0);
		MapInfo pMap = null;

		if(currentEpisode == null || (pMap = currentEpisode.gMapInfo[pGameInfo.nLevel]) == null) //usermap
		{
			pGameInfo.uGameFlags |= EndOfGame;
			pGameInfo.nLevel = 0;
		    return;
		}

		int pnEndingA = pMap.EndingA;
		if(pnEndingA >= 0)
			pnEndingA--;
		int pnEndingB = pMap.EndingB;
		if(pnEndingB >= 0)
			pnEndingB--;

		if(levType == 1)
		{
			if ( pnEndingB == -1 )
		    {
				if ( pGameInfo.nEpisode + 1 < nEpisodeCount )
			    {
			        if ( currentEpisode.CutSceneB != null)
			        	pGameInfo.uGameFlags |= CutsceneB;
			        pGameInfo.nLevel = 0;
			        pGameInfo.uGameFlags |= EndOfGame;
			        return;
			    }

				pGameInfo.uGameFlags |= EndOfLevel;
				pGameInfo.nLevel = 0;
			    return;
		    }
			gNextMap = pnEndingB;
			return;
		}

		if ( pnEndingA != -1 ) {
			gNextMap = pnEndingA;
			return;
		}

		if ( currentEpisode.CutSceneB != null )
			pGameInfo.uGameFlags |= CutsceneB;
		pGameInfo.uGameFlags |= EndOfGame;
		pGameInfo.nLevel = 0;
	}

	/* - This method can be called via sending kCommandNumbered to TX kGDXChannelEndLevel - */
	/* - kCommandNumbered is a level number  -*/
	public static void levelEndLevelCustom(int nLevel) {

		if(!game.pNet.WaitForAllPlayers(5000)) {
			game.pNet.NetDisconnect(myconnectindex);
			return;
		}

		if(pGameInfo.nGameType == kNetModeCoop && pGameInfo.nReviveMode)
		{
			for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
			{
				if(gPlayer[i].deathTime > 0) {
					resetInventory(gPlayer[i]);
			    	playerReset(i);
				}
			}
		}

		pGameInfo.uGameFlags |= EndOfLevel;
		getInput().setKey(ANYKEY, 0);

		if(mUserFlag == UserFlag.UserMap || nLevel >= kMaxMap || nLevel < 0)
		{

			pGameInfo.uGameFlags |= EndOfGame;
			pGameInfo.nLevel = 0;
		    return;
		}

		gNextMap = nLevel;
	}
}
