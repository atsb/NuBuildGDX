// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Redneck.Types;

public class MapInfo {
	
	public String path;
	public String title;
	public int partime;
	public int designertime;
	
	public MapInfo(String path, String title, int partime, int designertime) {
		this.path = path;
		this.title = title;
		this.partime = partime;
		this.designertime = designertime;
	}
	
	public void clear()
	{
		path = null;
		title = null;
		partime = 0;
		designertime = 0;
	}
}
