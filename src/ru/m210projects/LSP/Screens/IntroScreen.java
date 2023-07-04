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

package ru.m210projects.LSP.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.LSP.Sounds.*;

import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

public class IntroScreen extends SkippableAdapter {

	private int toffs;
	private int state;
	private float time;

	public IntroScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void show() {
		toffs = 0;
		state = 0;
		time = 0;

		stopallsounds();
		game.pNet.ResetTimers();
		startmusic(14);
	}
	
	@Override
	public void draw(float delta) {
		switch(state)
		{
		case 0:
			screen1(delta);
			break;
		case 1:
			screen2(delta);
			break;
		case 2:
			screen3(delta);
			break;
		}
	}
	
	private void screen1(float delta)
	{
		if ((time += delta) >= 0.2f) {
			if (++toffs >= 320)
				toffs = 0;
			time = 0;
		}

		int xscreen = coordsConvertXScaled(1, ConvertType.Normal);
		engine.rotatesprite(-toffs << 16, 0, 65536, 0, 1102, 0, 0, 2 | 8 | 16, xscreen, 0, xdim, ydim);
		engine.rotatesprite((319 - toffs) << 16, 0, 65536, 0, 1102, 0, 0, 2 | 8 | 16, 0, 0, xdim - xscreen, ydim);
		engine.rotatesprite(0, 10 << 16, 65536, 0, 1100, 0, 0, 2 | 8 | 16, 0, 0, xdim, ydim);

		if (totalclock < 762) {
			int angle = 8 * totalclock;
			int scale = angle << 5;
			if (totalclock >= 256) {
				if (totalclock >= 514) {
					angle = 8 * (totalclock + 1286);
					scale = (762 - totalclock) << 8;
				} else {
					scale = 65536;
					angle = 0;
				}
			}
			engine.rotatesprite(160 << 16, 100 << 16, scale, angle, 610, 0, 0, 2 | 8, xscreen, 0, xdim - xscreen, ydim);// accend inc
		}

		if (totalclock > 800) {
			int count = (totalclock - 800);
			int xoffs = count >> 3;
			if (count >= 2464)
				xoffs = 308;

			engine.rotatesprite((320 - xoffs) << 16, 129 << 16, 65536, 0, 1103, 0, 0, 2 | 8 | 16, 0, 0, xdim - xscreen, ydim);

			if (count <= 2464) {
				if(count > 1440)
					engine.rotatesprite(160 << 16, 176 << 16, (count - 1440) << 6, 0, 1105, 0, 0, 2 | 8, 0, 0, xdim, ydim);
				if(count > 1952)
					engine.rotatesprite(((177 * sintable[(count + 96) & 0x7FF] >> 14) + 104) << 16,
						((177 * sintable[(count - 1952 + 1536) & 0x7FF] >> 14) + 189) << 16, 65536, 4 * (count - 1952),
						1107, 0, 0, 2 | 8, 0, 0, xdim, ydim);
			} else if (count > 2464) {
				stopmusic();
				engine.rotatesprite(160 << 16, 176 << 16, 65536, 0, 1105, 0, 0, 2 | 8, 0, 0, xdim, ydim);
				engine.rotatesprite(263 << 16, 177 << 16, 65536, 0, 1107, 0, 0, 2 | 8 | 16, 0, 0, xdim, ydim);
			}	
			
			if (totalclock > 3600) {
				int angle = 8 * (totalclock - 3600);
				int scale = angle << 5;
				if(scale >= 65536) {
					scale = 65536;
					angle = 0;
					toffs = 0;
					state = 1;
					time = 0;
					game.pNet.ResetTimers();
					startmusic(3);
				}
				
				engine.rotatesprite(160 << 16, 100 << 16, scale, angle, 770, 0, 0, 2 | 8, xscreen, 0, xdim - xscreen, ydim);
			}
		}
	}

	private void screen2(float delta)
	{
		if ((time += delta) >= 0.5f) {
			if (++toffs >= 18)
				toffs = 0;
			time = 0;
		}

		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 770 + toffs, 0, 0, 2 | 8, 0, 0, xdim, ydim);
		
		int yoffs = totalclock >> 5;
        if ( totalclock >= 6240 )
          yoffs = 195;
        
        engine.rotatesprite(20 << 16, (199 - yoffs) << 16, 65536, 0, 788, klabs(13 - (totalclock >> 8)), 0, 2 | 8 | 16, 0, 0, xdim, ydim);
        if(totalclock >= 7680)
        {
        	toffs = 0;
        	state = 2;
			game.pNet.ResetTimers();
        }
	}
	
	private void screen3(float delta)
	{
		if ((time += delta) >= 0.005f) {
			if (++toffs > 256) {
				toffs = 256;
				skip();
			}
			time = 0;
		}
//		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 611, 0, 0, 2 | 8, 0, 0, xdim, ydim);
		
		engine.rotatesprite(0 << 16, 0, 65536, 0, 1102, 0, 0, 2 | 8 | 16, 0, 0, xdim, ydim);
		engine.rotatesprite(0, 10 << 16, 65536, 0, 1100, 0, 0, 2 | 8 | 16, 0, 0, xdim, ydim);
		
		engine.rotatesprite(160 << 16, 100 << 16, (256 - toffs) << 8, 8 * toffs, 770, 0, 0, 2 | 8, 0, 0, xdim, ydim);
		engine.rotatesprite(160 << 16, 100 << 16, (256 - toffs) << 8, 8 * (2048 - toffs), 788, 0, 0, 2 | 8, 0, 0, xdim, ydim);
	}
}
