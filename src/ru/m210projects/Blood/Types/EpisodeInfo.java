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

import static ru.m210projects.Blood.LEVELS.kMaxMap;

import ru.m210projects.Build.FileHandle.DirectoryEntry;

public class EpisodeInfo {
	
	public String filename;
	public String Title;
	public int nMaps;
	
	public MapInfo[] gMapInfo;
	public int BBOnly;
	public int CutSceneALevel;
	
	public String CutSceneA;
	public String CutSceneB;
	public int CutA;
	public int CutB;
	public String CutWavA;
	public String CutWavB;
	
	public DirectoryEntry resDir; 

	public EpisodeInfo() {
		gMapInfo = new MapInfo[kMaxMap + 1];
	}
	
	public void setDirectory(DirectoryEntry resDir)
	{
		this.resDir = resDir;
	}
	
	public DirectoryEntry getDirectory()
	{
		return resDir;
	}
	
	public void clear()
	{
		filename = null;
		Title = null;
		nMaps = 0;
		BBOnly = 0;
		CutSceneALevel = 0;
		
		for(int i = 0; i <= kMaxMap; i++)
			if(gMapInfo[i] != null) 
				gMapInfo[i].clear();
		
		CutSceneA = null;
		CutSceneB = null;
		CutA = 0;
		CutB = 0;
		CutWavA = null;
		CutWavB = null;
	}
	
	public boolean hasCutsceneA(int nLvl)
	{
		return CutSceneALevel == nLvl && CutSceneA != null && !CutSceneA.isEmpty();	
	}
	
	public boolean hasCutsceneB(int nLvl)
	{
		return CutSceneB != null && !CutSceneB.isEmpty();	
	}
}
