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

package ru.m210projects.Build.Pattern;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Date {

	private final DateFormat dateFormat;
	private final long launchDate;
	
	public Date(String format)
	{
		dateFormat = new SimpleDateFormat(format, Locale.US);
		launchDate = getCurrentDate();
	}
	
	public String getDate(long time)
	{
		return dateFormat.format(time);
	}
	
	public String getLaunchDate()
	{
		return dateFormat.format(launchDate);
	}
	
	public long getCurrentDate()
	{
		return System.currentTimeMillis();
	}
}
