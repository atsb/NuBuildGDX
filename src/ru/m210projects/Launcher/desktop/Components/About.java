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

package ru.m210projects.Launcher.desktop.Components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static ru.m210projects.Launcher.desktop.Main.appversion;

public class About extends FramePanel {

	private static final long serialVersionUID = 1L;

	public About() {
		super(false);
	}

	public void init() {
		this.setLayout(new MigLayout("", "[grow,center][grow,center][grow,center]", "[][][][][::15px][]"));

		JLabel labelPortName = new JLabel("NuBuildGDX " + appversion);
		labelPortName.setFont(new Font("Tahoma", Font.BOLD, 18));
		this.add(labelPortName, "cell 0 0 3 1,alignx center");

		JLabel labelPortBy = new JLabel("\nby [Gibbon] - based on BuildGDX by M210");
		labelPortBy.setFont(new Font("Tahoma", Font.PLAIN, 12));
		this.add(labelPortBy, "cell 0 1 3 1,alignx center");
	}

	@Override
	public void update() {

	}
}
