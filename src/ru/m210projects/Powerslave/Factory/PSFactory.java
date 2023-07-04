// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Powerslave.Globals.BACKGROUND;

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
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Fonts.FontA;
import ru.m210projects.Powerslave.Fonts.GameFont;
import ru.m210projects.Powerslave.Fonts.MenuFont;
import ru.m210projects.Powerslave.Fonts.StandartFont;

public class PSFactory extends BuildFactory {

	private Main app;

	public PSFactory(Main app) {
		super("stuff.dat");
		this.app = app;
	}

	@Override
	public void drawInitScreen() {
		app.pEngine.rotatesprite(0, 0, 65536, 0, BACKGROUND, -128, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public BuildEngine engine() throws Exception {
		return Main.engine = new PSEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		if (type == RenderType.Software)
			return new PSSoftware(app.pEngine);
		else if (type == RenderType.PolyGDX)
			return new PSPolygdx(app.pEngine);
		else
			return new PSPolymost(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new PSInput(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new PSOSDFunc(app);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = new PSMenuHandler(app);
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(4) {
			@Override
			protected BuildFont init(int i) {
				if (i == 0)
					return new GameFont(app.pEngine);
				if (i == 1)
					return new MenuFont(app.pEngine);
				if (i == 2)
					return new FontA(app.pEngine);
				return new StandartFont(app.pEngine); // 3
			}
		};
	}

	@Override
	public BuildNet net() {
		return new PSNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new PSSliderDrawable();
	}
}
