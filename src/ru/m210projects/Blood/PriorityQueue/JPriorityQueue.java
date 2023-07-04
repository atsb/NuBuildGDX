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

package ru.m210projects.Blood.PriorityQueue;

import static ru.m210projects.Blood.EVENT.getFuncID;
import static ru.m210projects.Blood.EVENT.getIndex;
import static ru.m210projects.Blood.EVENT.getType;
import static ru.m210projects.Blood.Main.*;

public class JPriorityQueue extends java.util.PriorityQueue<PriorityItem> implements IPriorityQueue
{
	private static final long serialVersionUID = 1L;

	private final int kPQueueSize;
	private final PriorityItem[] qList;
	
	public JPriorityQueue(int size) {
		super(size);
		this.kPQueueSize = size;
		qList = new PriorityItem[size];
		for(int i = 0; i < size; i++)
			qList[i] = new PriorityItem(0, 0);
	}
	
	@Override
	public void flush()
	{
		clear();
		for(int i = 0; i < qList.length; i++)
			qList[i].isFree = true;
	}
	
	@Override
	public void Insert(long time, int event) 
	{
		if(size() >= kPQueueSize) 
			game.dassert("fNodeCount < kPQueueSize");

		int fNodeCount = 0;
		while(!qList[fNodeCount].isFree)
			fNodeCount++;

		qList[fNodeCount].priority = time;
		qList[fNodeCount].event = event;
		qList[fNodeCount].isFree = false;

		add(qList[fNodeCount]);
	}
	
	@Override
	public int Remove()
	{
		PriorityItem item = remove();
		item.isFree = true;
		return item.event;
	}

	@Override
	public void checkList(int nIndex, int nType) {
		int i = 0;
		Object[] priorityList = toArray();
		while ( i < size() )
		{
			PriorityItem item = (PriorityItem)priorityList[i];
			if ( nIndex == getIndex(item.event) && nType == getType(item.event) ) {
				item.isFree = true;
				remove(item);
				priorityList = toArray();
			}
		    else
		    	++i;
		}
	}
	
	@Override
	public void checkList(int nIndex, int nType, int funcId) {
		int i = 0;
		Object[] priorityList = toArray();
		while ( i < size() )
		{
			PriorityItem item = (PriorityItem)priorityList[i];
			if ( nIndex == getIndex(item.event) && nType == getType(item.event) && funcId == getFuncID(item.event) ) {
				item.isFree = true;
				remove(item);
				priorityList = toArray();
			}
		    else
		    	++i;
		}
	}

	@Override
	public boolean Check(long time) {
		return size() > 0 && time >= element().priority;
	}

	@Override
	public PriorityItem getItem(int i) {
		return (PriorityItem) toArray()[i];
	}

	@Override
	public int getSize() {
		return size();
	}
}
