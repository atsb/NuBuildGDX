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

public class GAMEINFO {
	
	public static final int EndOfLevel = 1;
	public static final int EndOfGame = 2;
	public static final int CutsceneA = 4;
	public static final int CutsceneB = 8;

	public int nGameType;
	public int nDifficulty;
	public int nEpisode;
	public int nLevel;
	public String zLevelName;
	public String zLevelSong;
	public int nTrackNumber;
	public String szSaveGameName;
	public String szUserGameName;
	public int nSaveGameSlot;
	public int picEntry;
	public long uMapCRC;
	public int nMonsterSettings; //0 - no monsters, 1 - no respawn monsters, 2 - respawn monsters
	public int uGameFlags;
	public int uNetGameFlags;
	public int nWeaponSettings; //0 - none, 1 - are permanent, 2 - respawn, 3 - respawn with marker
	public int nItemSettings;  // 0 - none, 1 - respawn, 2 - respawn with marker
	public int nRespawnSettings;
	public int nTeamSettings;
	public int nMonsterRespawnTime;
	public int nWeaponRespawnTime;
	public int nItemRespawnTime;
	public int nSpecialRespawnTime;
	
	//BloodGDX options
	public int nEnemyQuantity = -1;
	public int nEnemyDamage = -1;
	public boolean nPitchforkOnly;
	public short nFriendlyFire;
	public boolean nReviveMode;
	public int nFragLimit;
	
	public GAMEINFO() {}

    public GAMEINFO(
			int nGameType,
			int nDifficulty,
			int nEpisode,
			int nLevel,
			String zLevelName, //e1m1.map
			String zLevelSong,
			int nTrackNumber,
			String szSaveGameName, //gamexxxx.sav
			String szUserGameName,
			int nSaveGameSlot,
			int picEntry,
			int uMapCRC,
			int nMonsterSettings,
			int uGameFlags,
			int uNetGameFlags,
			int nWeaponSettings,
			int nItemSettings,
			int nRespawnSettings,
			int nTeamSettings,
			int nMonsterRespawnTime,
			int nWeaponRespawnTime,
			int nItemRespawnTime,
			int nSpecialRespawnTime)
	{
		this.nGameType = nGameType;
		this.nDifficulty = nDifficulty;
		this.nEpisode = nEpisode;
		this.nLevel = nLevel;
		this.zLevelName = zLevelName;
		this.zLevelSong = zLevelSong;
		this.nTrackNumber = nTrackNumber;
		this.szSaveGameName = szSaveGameName;
		this.szUserGameName = szUserGameName;
		this.nSaveGameSlot = nSaveGameSlot;
		this.picEntry = picEntry;
		this.uMapCRC = uMapCRC;
		this.nMonsterSettings = nMonsterSettings;
		this.uGameFlags = uGameFlags;
		this.uNetGameFlags = uNetGameFlags;
		this.nWeaponSettings = nWeaponSettings;
		this.nItemSettings = nItemSettings;
		this.nRespawnSettings = nRespawnSettings;
		this.nTeamSettings = nTeamSettings;
		this.nMonsterRespawnTime = nMonsterRespawnTime;
		this.nWeaponRespawnTime = nWeaponRespawnTime;
		this.nItemRespawnTime = nItemRespawnTime;
		this.nSpecialRespawnTime = nSpecialRespawnTime;
		this.nFragLimit = 0;
	}
	
	public void copy(GAMEINFO src)
	{
		this.nGameType = src.nGameType;
		this.nDifficulty = src.nDifficulty;
		this.nEpisode = src.nEpisode;
		this.nLevel = src.nLevel;
		this.zLevelName = src.zLevelName;
		this.zLevelSong = src.zLevelSong;
		this.nTrackNumber = src.nTrackNumber;
		this.szSaveGameName = src.szSaveGameName;
		this.szUserGameName = src.szUserGameName;
		this.nSaveGameSlot = src.nSaveGameSlot;
		this.picEntry = src.picEntry;
		this.uMapCRC = src.uMapCRC;
		this.nMonsterSettings = src.nMonsterSettings;
		this.uGameFlags = src.uGameFlags;
		this.uNetGameFlags = src.uNetGameFlags;
		this.nWeaponSettings = src.nWeaponSettings;
		this.nItemSettings = src.nItemSettings;
		this.nRespawnSettings = src.nRespawnSettings;
		this.nTeamSettings = src.nTeamSettings;
		this.nMonsterRespawnTime = src.nMonsterRespawnTime;
		this.nWeaponRespawnTime = src.nWeaponRespawnTime;
		this.nItemRespawnTime = src.nItemRespawnTime;
		this.nSpecialRespawnTime = src.nSpecialRespawnTime;
		
		this.nEnemyQuantity = src.nEnemyQuantity;
		this.nEnemyDamage = src.nEnemyDamage;
		this.nPitchforkOnly = src.nPitchforkOnly;
		this.nFriendlyFire = src.nFriendlyFire;
		this.nReviveMode = src.nReviveMode;
		this.nFragLimit = src.nFragLimit;
	}
	
	
}
