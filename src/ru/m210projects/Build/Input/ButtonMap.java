// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Input;

import static ru.m210projects.Build.Input.BuildControllers.MAXBUTTONS;
import com.badlogic.gdx.utils.ObjectIntMap;

public class ButtonMap {
	private static ObjectIntMap<String> butNames;

	public static int valueOf (String keyname) {
		if (butNames == null) initializeKeyNames();
		return butNames.get(keyname, -1);
	}

	private static void initializeKeyNames () {
		butNames = new ObjectIntMap<String>();
		for (int i = 0; i < MAXBUTTONS; i++) {
			String name = "JOY" + i;
			if (name != null) butNames.put(name, i);
		}
	}
	
	public static String buttonName(int num)
	{
		if (butNames == null) initializeKeyNames();
		if(num < 0) return "N/A";
		return butNames.findKey(num);
	}
}
