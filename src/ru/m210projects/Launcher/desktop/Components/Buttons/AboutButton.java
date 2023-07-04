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

package ru.m210projects.Launcher.desktop.Components.Buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ru.m210projects.Launcher.desktop.JLauncher;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;

public class AboutButton extends FrameButton {
	private static final long serialVersionUID = 1L;
	
	private final ActionListener showAbout;
	private final ActionListener goBack;
	private GameEntry lastEntry;
	
	public AboutButton(final JLauncher main, String logoName, String panelName) {
		super(main, logoName, panelName);
		setText("About");
		setEnabled(false);
		showAbout = getActionListeners()[0];
		goBack = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lastEntry != null)
					main.showEntry(lastEntry);
			}
		};
	}
	
	public void setEntry(GameEntry entry)
	{
		this.lastEntry = entry;
		setEnabled(entry != null);
	}
	
	public void setAction(boolean back)
	{
		removeListeners();
		setSize(80, getHeight());
		setPreferredSize(getSize());
		if(!back) {
			setText("About");
			addActionListener(showAbout);
		} else if(lastEntry != null) {
			setText("Back");
			addActionListener(goBack);
		}
	}
	
	private void removeListeners()
	{
		for( ActionListener al : getActionListeners() ) 
	        removeActionListener( al );
	}
}

