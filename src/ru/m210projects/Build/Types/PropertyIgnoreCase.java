//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Types;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class PropertyIgnoreCase extends Properties {

	private static final long serialVersionUID = 7511088737858527084L;

	public String getPropertyIgnoreCase(String key) {
		return getPropertyIgnoreCase(key, null);
	}

	public String getPropertyIgnoreCase(String key, String defaultV) {
		String value = getProperty(key);
	 	if (value != null)
	 		return value;

	 	// Not matching with the actual key then
		Set<Entry<Object, Object>> s = entrySet();
		Iterator<Entry<Object, Object>> it = s.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			if (key.equalsIgnoreCase((String) entry.getKey()))
				return (String) entry.getValue();
		}
		return defaultV;
	}
}
