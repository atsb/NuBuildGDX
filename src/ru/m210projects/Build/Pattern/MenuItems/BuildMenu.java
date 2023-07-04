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

package ru.m210projects.Build.Pattern.MenuItems;

import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class BuildMenu {

	public short m_nItems = 0;
	public short m_nFocus = -1;
	public short m_nFirst = -1;
	public MenuItem[] m_pItems;
	public final short nMaxGameMenuItems = 32;
	
	public BuildMenu()
	{
		m_pItems = new MenuItem[nMaxGameMenuItems];
	}

	public int addItem(MenuItem pItem, boolean nFirstItem)
	{
		if(pItem == null) return -1;

	  	m_pItems[m_nItems] = pItem;
	  	pItem.m_pMenu = this;
	  	if ( nFirstItem )
	  	{
	  		m_nFirst = m_nItems;
	  		m_nFocus = m_nItems;
	  	}
	  	m_nItems++;
	  	return m_nItems;
	}
	
	public int removeItem(MenuItem pItem)
	{
		if(pItem == null) return -1;
		
		for(int i = 0; i < m_nItems; i++)
			if(m_pItems[i] == pItem)
			{
				int pos = i;
				while(pos < m_nItems) 
					m_pItems[pos] = ++pos < m_nItems ? m_pItems[pos] : null;
				m_nItems--;
				return i;
			}
		
		return -1;
	}
	
	public int open(MenuHandler handler, int nItem)
	{
		if ( nItem >= 0 && nItem < nMaxGameMenuItems) {
			if ( mCheckItemsFlags(nItem) )
				m_nFocus = m_nFirst = (short) nItem;
		}

		mLoadRes(handler, MenuOpt.Open);
	    
	    return 1;
	}
	
	public boolean mLoadRes(MenuHandler handler, MenuOpt opt)
	{
		if ( m_nItems <= 0 )
			return true;
		
		if(m_nFocus >= 0 && m_pItems[m_nFocus] != null)
			handler.mSound(m_pItems[m_nFocus], opt);
		
		switch(opt) {
		case Open:
			if ( m_nFirst >= 0 )
				m_nFocus = m_nFirst;
			for ( int i = 0; i < m_nItems; i++ )
				m_pItems[i].open();
			return false;
		case Close:
			for ( int i = 0; i < m_nItems; i++ )
				m_pItems[i].close();
			return false;
		default:
			if ( m_nFocus >= 0 && mCheckItemsFlags(m_nFocus)) 
		    	return m_pItems[m_nFocus].callback(handler, opt);
		}
		return false;
	}
	
	protected boolean mCheckItemsFlags(int nItem) {
		if ( nItem < 0 || nItem >= nMaxGameMenuItems || m_pItems[nItem] == null ) 
			return false;
			
		MenuItem pItem = m_pItems[nItem];
		return (pItem.flags & 1) != 0 && (pItem.flags & 2) != 0;
	}
	
	public boolean mCheckMouseFlag(int nItem) {
		if ( nItem < 0 || nItem >= nMaxGameMenuItems || m_pItems[nItem] == null ) 
			return false;
			
		MenuItem pItem = m_pItems[nItem];
		return (pItem.flags & 2) != 0;
	}
	
	public boolean mGetFocusedItem(MenuItem m_pItem) {
		if ( m_nFocus >= 0 && m_nFocus < nMaxGameMenuItems) 
		    return m_pItem == m_pItems[m_nFocus];
		
		return false;
	}
	
	public void mDraw(MenuHandler handler)
	{
		for ( int i = 0; i < m_nItems; ++i )
		{
			if ( i == m_nFocus || i != m_nFocus && (m_pItems[i].flags & 8) == 0 )
				m_pItems[i].draw(handler);
		}
	}
	
	public int mNavUp()
	{
		int nItem;
		do
		{
			nItem = m_nFocus - 1;
			if ( nItem < 0 )
				nItem += m_nItems;
			m_nFocus = (short) nItem;
		}
		while ( !mCheckItemsFlags(nItem) );
		return nItem;
	}
	
	public int mNavDown()
	{
		int nItem;
		do
		{
			nItem = m_nFocus + 1;
			if ( nItem >= m_nItems )
				nItem = 0;
			m_nFocus = (short) nItem;
		}
		while ( !mCheckItemsFlags(nItem) );
		return nItem;
	}
	
	public boolean mNavigation(MenuOpt opt) 
	{
		switch(opt) {
			case UP:
				mNavUp();
			    return false;
			case DW:
				mNavDown();
			    return false;
			case ESC:
			case RMB:
				return true;
			default:
				return false;
		}
	}
}
