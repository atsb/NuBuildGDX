package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Names.*;
import static ru.m210projects.Wang.JSector.*;
import static ru.m210projects.Wang.Gameutils.*;

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
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Fonts.GameFont;
import ru.m210projects.Wang.Fonts.MenuFont;
import ru.m210projects.Wang.Fonts.MiniFont;
import ru.m210projects.Wang.Fonts.StandartFont;

public class WangFactory extends BuildFactory {

	private Main app;

	public WangFactory(Main app) {
		super("sw.grp");
		this.app = app;
	}

	@Override
	public BuildEngine engine() throws Exception {
		return Main.engine = new WangEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		return new WangSoftware(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new WangInput(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new WangOSDFunc(app);
	}

	@Override
	public MenuHandler menus() {
		return new WangMenuHandler(app);
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(3) {
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
		return gNet = app.net = new WangNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new WangSliderDrawable();
	}

	@Override
	public void drawInitScreen() {
		engine.clearview(117);
		engine.rotatesprite(xdim << 15, ydim << 15, divscale(ydim, 240, 16), 0, LOADSCREEN, 0, 0, 8, 0, 0, xdim - 1,
				ydim - 1);
	}
}
