package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;

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
import ru.m210projects.Build.Render.GdxRender.GDXRenderer;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Fonts.HealthFont;
import ru.m210projects.Witchaven.Fonts.MenuFont;
import ru.m210projects.Witchaven.Fonts.PotionFont;
import ru.m210projects.Witchaven.Fonts.ScoreFont;
import ru.m210projects.Witchaven.Fonts.StandartFont;
import ru.m210projects.Witchaven.Fonts.TheFont;
import ru.m210projects.Witchaven.Fonts.WH2Fonts;

public class WHFactory extends BuildFactory {

	private Main app;

	public WHFactory(Main app) {
		super("stuff.dat");
		this.app = app;
	}

	@Override
	public void drawInitScreen() {
		app.pEngine.rotatesprite(160 << 16, 100 << 16, 65536, 0, MAINMENU, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public BuildEngine engine() throws Exception {
		return engine = new WHEngine(app);
	}

	@Override
	public Renderer renderer(RenderType type) {
		if (type == RenderType.Software)
			return new WHSoftware(app.pEngine);
		else if (type == RenderType.PolyGDX)
			return new GDXRenderer(app.pEngine, new WHMapSettings());
		else
			return new WHPolymost(app.pEngine);
	}

	@Override
	public DefScript getBaseDef(BuildEngine engine) {
		return new DefScript(engine, false);
	}

	@Override
	public BuildControls input(BuildControllers gpmanager) {
		return new WHControl(app.pCfg, gpmanager);
	}

	@Override
	public OSDFunc console() {
		return new WHOSDFunc(app);
	}

	@Override
	public MenuHandler menus() {
		return app.menu = (game.WH2 ? new WH2MenuHandler(app) : new WHMenuHandler(app));
	}

	@Override
	public FontHandler fonts() {
		return new FontHandler(6) {
			@Override
			protected BuildFont init(int i) {
				if (i == 0) {
					if (game.WH2)
						return new WH2Fonts(app.pEngine);
					return new MenuFont(app.pEngine);
				}
				if (i == 1)
					return new TheFont(app.pEngine);
				if (i == 2)
					return new HealthFont(app.pEngine);
				if (i == 3)
					return new PotionFont(app.pEngine);
				if (i == 4)
					return new ScoreFont(app.pEngine);

				return new StandartFont(app.pEngine);
			}
		};
	}

	@Override
	public BuildNet net() {
		return new WHNetwork(app);
	}

	@Override
	public SliderDrawable slider() {
		return new WHSliderDrawable();
	}

}
