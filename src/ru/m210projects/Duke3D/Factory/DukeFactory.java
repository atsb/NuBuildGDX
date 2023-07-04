// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Factory;

import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Build.Engine.*;

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
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Fonts.GameFont;
import ru.m210projects.Duke3D.Fonts.MenuFont;
import ru.m210projects.Duke3D.Fonts.MiniFont;
import ru.m210projects.Duke3D.Fonts.StandartFont;

public class DukeFactory extends BuildFactory {

	private final Main app;

	public DukeFactory(Main app) {
		super(app.mainGrp);
		this.app = app;
	}

	@Override
	public BuildEngine engine() throws Exception {
		MAXSECTORS = MAXSECTORSV8;
		MAXWALLS = MAXWALLSV8;
		MAXSPRITES = MAXSPRITESV8;

		return Main.engine = new DukeEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		if (type == RenderType.Software)
			return new DukeSoftware(app.pEngine);
		else if (type == RenderType.PolyGDX)
			return new DukePolygdx(app.pEngine);
		else
			return new DukePolymost(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new DukeInput(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new DukeOSDFunc(app.pEngine);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = new DukeMenuHandler(app);
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
		return new DukeNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new DukeSliderDrawable();
	}

	@Override
	public void drawInitScreen() {
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, LOADSCREEN, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
	}

}
