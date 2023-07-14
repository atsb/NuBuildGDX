package ru.m210projects.Tekwar.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

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
import ru.m210projects.Tekwar.Main;
import ru.m210projects.Tekwar.Fonts.MenuFont;
import ru.m210projects.Tekwar.Fonts.StandartFont;
import ru.m210projects.Tekwar.Fonts.TekFontA;
import ru.m210projects.Tekwar.Fonts.TekFontB;
import ru.m210projects.Tekwar.Menus.TekSliderDrawable;

public class TekFactory extends BuildFactory {

	private Main app;

	public TekFactory(Main app) {
		super("tekwar.dat");
		this.app = app;
	}

	@Override
	public void drawInitScreen() {
		app.pEngine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 321, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public BuildEngine engine() throws Exception {
		return Main.engine = new TekEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		if (type == RenderType.Software)
			return new TekSoftware(app.pEngine);
		else
			return new TekPolymost(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new TekControl(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new TekwarOSDFunc(app);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = new TekMenuHandler(app, 32);
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(4) {
			@Override
			protected BuildFont init(int i) {
				if (i == 0)
					return new MenuFont(app.pEngine);
				if (i == 1)
					return new TekFontA(app.pEngine);
				if (i == 2)
					return new TekFontB(app.pEngine);

				return new StandartFont(app.pEngine);
			}
		};
	}

	@Override
	public BuildNet net() {
		return new TekNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new TekSliderDrawable(app);
	}

}
