package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertYScaled;
import static ru.m210projects.Witchaven.Main.engine;

import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Types.Tile;

public class WHSliderDrawable extends SliderDrawable {

	@Override
	public int getSliderWidth() {
		return 9;
	}

	@Override
	public int getSliderRange() {
		return 50;
	}

	@Override
	public void drawSliderBackground(int x, int y, int shade, int pal) {
		int sx = x + 25;
		int sy = (int) ((y + 5.5f) * 65536.0f); //y + 5 << 16;

		if(shade == -127) //disabled
			shade = 16;
		else shade = 0;

		engine.rotatesprite(sx - 7 << 16, sy, 32768, 0, 319, shade, 0, 10, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(sx + 7 << 16, sy, 32768, 0, 319, shade, 0, 10, 0, 0, xdim - 1, ydim - 1);

//		this.drawSliderBackground(engine, x, y, game.getFont(0).getHeight(), 31);
	}

	@Override
	public void drawSlider(int x, int y, int shade, int pal) {
		if(shade == -127) //disabled
			shade = 16;

		engine.rotatesprite(x - 3 << 16, y + 1 << 16, 32768, 0, 511, shade, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);

//		this.drawSlider(engine, x, y, game.getFont(0).getHeight(), 31);
	}

	@Override
	public int getScrollerWidth() {
		return 10;
	}

	@Override
	public int getScrollerHeight() {
		return 10;
	}

	@Override
	public void drawScrollerBackground(int x, int y, int height, int shade, int pal) {
		int SLIDEBAR = 319;

		int ang = 512;
		int sy = y;

		engine.rotatesprite(x + 9 << 16, y - 3 << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, 0, xdim-1, coordsConvertYScaled(sy));
		Tile pic = engine.getTile(SLIDEBAR);

		int clen = height;
		int dy = (pic.getWidth()) - 20;

		int posy = sy;
		while(clen > 0)
		{
			if(dy > clen) dy = clen;
			engine.rotatesprite(x + 9 << 16, (posy-9) << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(posy), xdim-1, coordsConvertYScaled(posy + dy));
			posy += dy;
			clen -= dy;
		}


		int y2 = sy + height - 10;
		engine.rotatesprite(x + 9 << 16, (y2 - (pic.getWidth()) + 13) << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(y2), xdim-1, ydim-1);

//		this.drawScrollerBackground(engine, x, y, height, 31);
	}

	@Override
	public void drawScroller(int x, int y, int shade, int pal) {
		engine.rotatesprite((int) ((x - 2.5f) * 65536.0f), y << 16, 32768, 0, 511, shade, 0, 10 | 16, 0, 0, xdim-1, ydim-1);

//		this.drawScroller(engine, x, y, 31);
	}

}
