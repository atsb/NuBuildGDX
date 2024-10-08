// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.LSP.Main.gMapGroup;
import static ru.m210projects.LSP.Main.gResGroup;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
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
import ru.m210projects.LSP.Main;
import ru.m210projects.LSP.Fonts.FontA;
import ru.m210projects.LSP.Fonts.MenuFont;
import ru.m210projects.LSP.Fonts.StandartFont;
import ru.m210projects.LSP.Types.LSPGroup;

public class LSPFactory extends BuildFactory {

	private Main app;

	public LSPFactory(Main app) {
		super("stuff.dat");
		this.app = app;
	}

	@Override
	public void drawInitScreen() {
		app.pEngine.rotatesprite(0, 0, 65536, 0, 770, -128, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public BuildEngine engine() throws Exception {
		gResGroup = new LSPGroup("LVART000.DAT", 135);
		gMapGroup = new LSPGroup("LMART000.DAT", 36);
		BuildGdx.cache.add(gResGroup);

		Console.Println("Found " + gResGroup.numfiles + " files in " + gResGroup.name + " archive");
		Console.Println("Found " + gMapGroup.numfiles + " files in " + gMapGroup.name + " archive");

		return Main.engine = new LSPEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		return new LSPSoftware(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new LSPInput(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new LSPOSDFunc(app);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = new LSPMenuHandler(app);
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(3) {
			@Override
			protected BuildFont init(int i) {
				if (i == 0)
					return new FontA(app.pEngine);
				if (i == 1)
					return new StandartFont(app.pEngine);
				if (i == 2)
					return new MenuFont(app.pEngine);

				return null;
			}
		};
	}

	@Override
	public BuildNet net() {
		return new LSPNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new LSPSliderDrawable();
	}
}
