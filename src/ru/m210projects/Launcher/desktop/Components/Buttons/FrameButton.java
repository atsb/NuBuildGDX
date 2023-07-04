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

import javax.swing.JButton;

import ru.m210projects.Launcher.desktop.JLauncher;

public class FrameButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	
	public String logoName, panelName;

	public FrameButton(final JLauncher main, final String logoName, final String panelName)
	{
		this.logoName = logoName;
		this.panelName = panelName;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.showPanel(FrameButton.this);
			}
		});
	}
	
}
