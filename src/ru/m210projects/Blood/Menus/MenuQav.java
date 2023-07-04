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

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Globals.gFrameClock;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.QAV.kQFrameScale;
import static ru.m210projects.Blood.QAV.kQFrameUnclipped;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Blood.QAV;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuQav extends MenuItem {
	public String qFileName;
	public int qFileId;
	public QAV pQAV;
	public int duration;
	public int qGameClock;
	public boolean clearScreen;

	public MenuQav(int x, int y, int qFileId) {
		super(null, null);
		this.flags = 1;
		this.m_pMenu = null;
		this.width = 0;
		this.x = x;
		this.y = y;
		this.qFileId = qFileId;
		this.qFileName = null;
		this.clearScreen = false;
	}

	public MenuQav(int x, int y, String qFileName) {
		super(null, null);
		this.flags = 1;
		this.m_pMenu = null;
		this.width = 0;
		this.x = x;
		this.y = y;
		this.qFileName = qFileName;
		this.qFileId = -1;
		this.clearScreen = false;
	}

	@Override
	public void draw(MenuHandler handler) {
		if (pQAV != null) {
			if (clearScreen)
				engine.getrender().clearview(0);
			int oldFrameClock = gFrameClock;
			gFrameClock = totalclock;
			int ticks = totalclock - qGameClock;
			qGameClock = totalclock;
			duration -= ticks;
			if (duration <= 0 || duration > pQAV.duration)
				duration = pQAV.duration;

			int t = pQAV.duration - duration;
			pQAV.Play(t - ticks, t, -1, null);

			int oldwx1 = windowx1;
			int oldwy1 = windowy1;
			int oldwx2 = windowx2;
			int oldwy2 = windowy2;
			windowx1 = 0;
			windowy1 = 0;
			windowx2 = xdim - 1;
			windowy2 = ydim - 1;
			pQAV.Draw(t, 0, (kQFrameUnclipped | kQFrameScale), 0, 65536);
			windowx1 = oldwx1;
			windowy1 = oldwy1;
			windowx2 = oldwx2;
			windowy2 = oldwy2;
			gFrameClock = oldFrameClock;
		}
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {

		switch (opt) {
		case LEFT:
		case BSPACE:
			m_pMenu.mNavUp();
			return false;
		case RIGHT:
		case ENTER:
		case SPACE:
			m_pMenu.mNavDown();
			return false;
		case UP:
		case DW:
		case ESC:
			return m_pMenu.mNavigation(opt);
		default:
			return false;
		}
	}

	@Override
	public void open() {
		if (qFileName == null && qFileId == -1)
			return;

		if (pQAV != null) {
			duration = pQAV.duration;
			qGameClock = totalclock;
			return;
		}
		Resource hQAV = null;
		if (qFileName != null)
			hQAV = BuildGdx.cache.open(qFileName, 0);
		else
			hQAV = BuildGdx.cache.open(qFileId, "qav");

		if (hQAV == null) {
			System.err.println("Could not load QAV " + qFileName);
			return;
		}

		pQAV = new QAV(hQAV);
		pQAV.origin.x = x;
		pQAV.origin.y = y;
		duration = pQAV.duration;
		qGameClock = totalclock;

		pQAV.Preload();

		hQAV.close();
	}

	@Override
	public void close() {
	}

	@Override
	public boolean mouseAction(int x, int y) {
		return false;
	}
}
