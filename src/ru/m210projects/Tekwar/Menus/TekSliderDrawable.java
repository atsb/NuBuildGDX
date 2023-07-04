package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Tekwar.Names.SLIDERBARPIC;
import static ru.m210projects.Tekwar.Names.SLIDERKNOBPIC;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Types.Tile;

public class TekSliderDrawable extends SliderDrawable {

	protected BuildGame app;
	public TekSliderDrawable(BuildGame app) {
		this.app = app;
	}

	@Override
	public void drawSliderBackground(int x, int y, int shade, int pal) {
		if(pal == 4) pal = 0;
		app.pEngine.rotatesprite(x - 6 << 16, (y - 2 << 16), 32768, 0, SLIDERBARPIC, shade, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);

//		this.dbDrawBackground(app.engine, x, y, app.getFont(0).nHeight, 31);
	}

	@Override
	public void drawSlider(int x, int y, int shade, int pal) {
		app.pEngine.rotatesprite(x - 1 << 16, y - 2 << 16, 32768, 0, SLIDERKNOBPIC, shade, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);

//		this.dbDrawSlider(app.engine, x, y, app.getFont(0).nHeight, 31);
	}

	@Override
	public int getSliderWidth() {
		return 3;
	}

	@Override
	public int getSliderRange() {
		return 58;
	}

	@Override
	public int getScrollerHeight() {
		return 3;
	}

	@Override
	public int getScrollerWidth() {
		return 8;
	}

	@Override
	public void drawScrollerBackground(int x, int y, int height, int shade, int pal) {
		height-= 12;
		int ang = 512;
		int sy = y + 4;

		app.pEngine.rotatesprite(x + 7 << 16, y - 5 << 16, 32768, ang, SLIDERBARPIC, 8, 0, 10 | 16, 0, 0, xdim-1, coordsConvertYScaled(sy));

		Tile pic = app.pEngine.getTile(SLIDERBARPIC);
		int clen = height;
		int dy = (pic.getWidth() / 2) - 17;

		int posy = sy;
		while(clen > 0)
		{
			if(dy > clen) dy = clen;
			app.pEngine.rotatesprite(x + 7 << 16, (posy-9) << 16, 32768, ang, SLIDERBARPIC, 8, 0, 10 | 16, 0, coordsConvertYScaled(posy), xdim-1, coordsConvertYScaled(posy + dy));
			posy += dy;
			clen -= dy;
		}


		int y2 = sy + height;
		app.pEngine.rotatesprite(x + 7 << 16, (y2 - (pic.getWidth() / 2) + 13) << 16, 32768, ang, SLIDERBARPIC, 8, 0, 10 | 16, 0, coordsConvertYScaled(y2), xdim-1, ydim-1);

//		this.drawScrollerBackground(app.pEngine, x, y, getScrollerWidth(), height + 12, 30);
	}

	@Override
	public void drawScroller(int x, int y, int shade, int pal) {
		app.pEngine.rotatesprite(x + 7 << 16, y << 16, 32768, 512, SLIDERKNOBPIC, shade, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);

//		this.drawScroller(app.pEngine, x, y, getScrollerWidth(), 30);
	}



}
