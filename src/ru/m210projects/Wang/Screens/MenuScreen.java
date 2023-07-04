package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Wang.Factory.WangMenuHandler.MAIN;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.LOADSCREEN;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Text.DrawFragBar;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;

public class MenuScreen extends MenuAdapter {

	private WangMenuHandler menu;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
	}

	@Override
	public void show() {
		engine.setbrightness(gs.brightness, palette, GLInvalidateFlag.All);
		if(!menu.gShowMenu)
			menu.mOpen(menu.mMenus[MAIN], -1);
		CDAudio_Play(2, true);
		COVER_SetReverb(0);
	}

	@Override
	public void process(float delta) {
		if (numplayers > 1 || gNet.FakeMultiplayer)
			DrawFragBar();

		if (!game.gPaused)
			game.pNet.GetPackets();
	}

	@Override
	public void draw(float delta) {
		engine.clearview(117);

		DrawBackground(engine);
	}

	public static void DrawBackground(Engine engine)
	{
		int scale = divscale(ydim, 240, 16);
		int width = mulscale(engine.getTile(9220).getWidth(), scale, 16);

		if(width != 0) {
			int framesnum = xdim / width;
			int fy = scale(20, ydim, 240);

			int statusx1 = coordsConvertXScaled(160 - engine.getTile(LOADSCREEN).getWidth() / 2, ConvertType.Normal);
			int statusx2 = coordsConvertXScaled(160 + engine.getTile(LOADSCREEN).getWidth() / 2, ConvertType.Normal);

			int x = 0;
			for (int i = 0; i <= framesnum; i++) {
				if (x - width <= statusx1 || x + width >= statusx2) {
					engine.rotatesprite(x << 16, fy << 16, scale, 0, 9220, 0, 0, 8 | 16 | 256, 0, 0, xdim - 1, ydim - 1);
				}
				x += width;
			}
		}
		engine.rotatesprite(xdim << 15, ydim << 15, scale, 0, LOADSCREEN, 0, 0, 8, 0, 0, xdim - 1, ydim - 1);
	}
}
