//This file is part of BuildGDX.
//Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import java.util.ArrayList;
import java.util.List;

public class ThreadProcessor {
	
	private final List<Thread> list = new ArrayList<Thread>();
	public Thread add(Runnable runnable)
	{
		Thread thread = new Thread(runnable);
		list.add(thread);
		thread.start();
		
		return thread;
	}
	
	public boolean isBusy()
	{
		for(Thread t : list) 
			if(t.isAlive())
				return true;

		return false;
	}
	
	public void await()
	{
		while(isBusy());
		
		list.clear();
	}
}
