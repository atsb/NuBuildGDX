// This file is part of PowerslaveGDX.
// Copyright (C) 2019-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Powerslave.Type;

import java.util.ArrayList;
import java.util.List;

public class EpisodeInfo {

	public String Title;
	public String path;
	public boolean packed;
	public List<MapInfo> gMapInfo;

	public EpisodeInfo() {
		gMapInfo = new ArrayList<MapInfo>();
	}
	
	public EpisodeInfo(String title) {
		this();
		this.Title = title;
	}

	public void clear()
	{
		Title = null;
		path = null;
		packed = false;
		gMapInfo.clear();
	}
	
	public int maps() {
		return gMapInfo.size();
	}
}
