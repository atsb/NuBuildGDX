// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Names.*;

import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.OSDFunc;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.FontHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Fonts.GameFont;
import ru.m210projects.Redneck.Fonts.MenuFont;
import ru.m210projects.Redneck.Fonts.MiniFont;
import ru.m210projects.Redneck.Fonts.StandartFont;

public class RRFactory extends BuildFactory {

	private Main app;

	public RRFactory(Main app) {
		super("redneck.grp");
		this.app = app;
	}

	@Override
	public void drawInitScreen() {
		app.pEngine.rotatesprite(160 << 16, 100 << 16, 65536, 0, LOADSCREEN, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public BuildEngine engine() throws Exception {
		return Main.engine = new RREngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		return new RRSoftware(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new RRInput(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new RROSDFunc(app.pEngine);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = new RRMenuHandler(app);
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(5) {
			@Override
			protected BuildFont init(int i) {
				if (i == 0)
					return new MiniFont(app.pEngine);
				if (i == 1)
					return new GameFont(app.pEngine);
				if (i == 2)
					return new MenuFont(app.pEngine);

				return new StandartFont(app.pEngine);
			}
		};
	}

	@Override
	public BuildNet net() {
		return new RRNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new RRSliderDrawable();
	}

}
