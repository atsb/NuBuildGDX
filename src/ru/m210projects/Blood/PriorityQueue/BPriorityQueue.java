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

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.EVENT.getFuncID;
import static ru.m210projects.Blood.EVENT.getIndex;
import static ru.m210projects.Blood.EVENT.getType;

public class BPriorityQueue implements IPriorityQueue {

	
	private final int kPQueueSize;
	public int fNodeCount;
	public PriorityItem[] qList;
	
	public BPriorityQueue(int size) {
		kPQueueSize = size;
		qList = new PriorityItem[size];
		for(int i = 0; i < size; i++)
			qList[i] = new PriorityItem(0, 0);
		fNodeCount = 0;
	}
	
	
	@Override
	public void flush() {
		for(int i = 0; i < kPQueueSize; i++)
			qList[i].event = (int) (qList[i].priority = 0);
		fNodeCount = 0;
	}

	@Override
	public void Insert(long time, int event) {
		if(fNodeCount >= kPQueueSize) 
			game.dassert("fNodeCount < kPQueueSize");
		fNodeCount++;
		qList[fNodeCount].priority = time & 0xFFFFFFFFL; //unsigned
		qList[fNodeCount].event = event;

		insert();
	}

	@Override
	public int Remove() {
		int out = qList[1].event;
		qList[1].priority = qList[fNodeCount].priority;
		qList[1].event = qList[fNodeCount].event;
		fNodeCount--;
		remove(1);
		return out;  
	}
	
	private int Remove(int k) 
	{
		if(k > fNodeCount) 
			game.dassert("k <= fNodeCount");
		qList[k].priority = qList[fNodeCount].priority;
		qList[k].event = qList[fNodeCount].event;
		fNodeCount--;
		return remove(k);
	}

	@Override
	public void checkList(int nIndex, int nType) {
		int i = 0;
		while ( i <= fNodeCount )
		{
			PriorityItem item = qList[i];
			if ( nIndex == getIndex(item.event) && nType == getType(item.event) )
				Remove(i);
		    else
		    	++i;
		}
	}

	@Override
	public void checkList(int nIndex, int nType, int funcId) {
		int i = 0;
		while ( i <= fNodeCount )
		{
			PriorityItem item = qList[i];
			if ( nIndex == getIndex(item.event) && nType == getType(item.event) && funcId == getFuncID(item.event) )
				Remove(i);
		    else
		    	++i;
		}
	}

	@Override
	public boolean Check(long time) {
		return fNodeCount > 0 && time >= qList[1].priority;
	}

	@Override
	public int getSize() {
		return fNodeCount;
	}

	@Override
	public PriorityItem getItem(int i) {
		return qList[i];
	}

	private void insert() {
		long priority = qList[fNodeCount].priority;
		int event = qList[fNodeCount].event;
		
		int tNodeCount = fNodeCount;
		qList[0].priority = 0;
		while (priority < qList[tNodeCount >> 1].priority) {

			PriorityItem qList1 = qList[tNodeCount >> 1];
			PriorityItem qList2 = qList[tNodeCount];
			qList2.priority = qList1.priority;
			qList2.event = qList1.event;
			tNodeCount >>= 1;
		}
		qList[tNodeCount].priority = priority;
		qList[tNodeCount].event = event;
	}

	private int remove(int item) {
		/*
		PriorityItem heap = new PriorityItem(0, 0);
		PriorityItem heapItem = heap;
		PriorityItem pItem = qList[item];
		while ( true )
		{
			heapItem.priority = pItem.priority;
			heapItem.event = pItem.event;
			if ( fNodeCount / 2 < item )
		    	break;
			
		    int k = 2 * item;
		    if ( k < fNodeCount && qList[k].priority > qList[k + 1].priority )
		    	++k;
		    pItem = qList[k];
		    if ( heap.priority <= pItem.priority )
		    	break;
		    
		    heapItem = qList[item];
		    item = k;
		}
		qList[item].priority = heap.priority;
		qList[item].event = heap.event;
		return heapItem.event;
		*/
		
		PriorityItem qItem = qList[item];
		long priority = qItem.priority;
		int event = qItem.event;
		while ( true )
		{
			if ( fNodeCount / 2 < item )
		    	break;
			
		    int k = 2 * item;
		    if ( k < fNodeCount && qList[k].priority > qList[k + 1].priority )
		    	++k;
		
		    if ( priority <= qList[k].priority )
		    	break;
		    
		    qList[item].priority = qList[k].priority;
		    qList[item].event = qList[k].event;
		    item = k;
		}
		qList[item].priority = priority;
		qList[item].event = event;
		return event;
		
		
		
	}
}
