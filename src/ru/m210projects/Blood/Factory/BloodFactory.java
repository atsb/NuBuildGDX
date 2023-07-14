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

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Blood.VERSION.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Fonts.BloodFont;
import ru.m210projects.Blood.Fonts.QFNFont;
import ru.m210projects.Blood.Types.BloodDef;
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

public class BloodFactory extends BuildFactory {

	private final Main app;

	public BloodFactory(Main app) {
		super("BLOOD.RFF", "SOUNDS.RFF");
		this.app = app;
	}

	@Override
	public void drawInitScreen() {
		app.pEngine.rotatesprite(0, 0, 65536, 0, 2046, -128, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public BuildEngine engine() throws Exception {
		TRANSLUSCENT1 = 0.33f;
		TRANSLUSCENT2 = 0.66f;
		MAXWALLS = 9600;
		versionInit();
		return Main.engine = new BloodEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		if (type == RenderType.Software)
			return new BloodSoftware(app.pEngine);
		else
			return new BloodPolymost(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new BloodDef(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new BloodInput(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new BloodOSDFunc(app.pEngine);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = new BloodMenuHandler(app);
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(5) {
			@Override
			protected BuildFont init(int i) {
				if (i == 0)
					return hasQFN ? new QFNFont(app.pEngine, 4096, 0) : new BloodFont(app.pEngine, 4096, 0);
				if (i == 1)
					return hasQFN ? new QFNFont(app.pEngine, 4192, 1) : new BloodFont(app.pEngine, 4192, 1);
				if (i == 2)
					return hasQFN ? new QFNFont(app.pEngine, 4288, 2) : new BloodFont(app.pEngine, 4288, 1);
				if (i == 3)
					return hasQFN ? new QFNFont(app.pEngine, 4384, 3) : new BloodFont(app.pEngine, 4384, 1);
				if (i == 4)
					return hasQFN ? new QFNFont(app.pEngine, 4480, 4) : new BloodFont(app.pEngine, 4480, 1);

				return null;
			}
		};
	}

	@Override
	public BuildNet net() {
		return new BloodNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new BLSliderDrawable();
	}
}
