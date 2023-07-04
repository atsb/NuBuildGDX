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

package ru.m210projects.Duke3D.Screens;

import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Pragmas.mulscale;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Duke3D.Main;

public class EndScreen extends ScreenAdapter {
	private final Main app;
	private int bonuscnt = 0;
	protected int gCutsClock;

	private final int[] bossmove =
    {
         0, 120,VICTORY1+3,86,59,
       220, 260,VICTORY1+4,86,59,
       260, 290,VICTORY1+5,86,59,
       290, 320,VICTORY1+6,86,59,
       320, 350,VICTORY1+7,86,59,
       350, 380,VICTORY1+8,86,59
    };

	private final int[] breathe =
    {
         0,  30,VICTORY1+1,176,59,
        30,  60,VICTORY1+2,176,59,
        60,  90,VICTORY1+1,176,59,
        90, 120,0         ,176,59
    };

	public EndScreen(Main app)
	{
		this.app = app;
	}

	@Override
	public void show() {
		if(ud.volume_number == 0) {
			 engine.setbrightness(ud.brightness>>2, endingpal, GLInvalidateFlag.All);
		} else engine.setbrightness(ud.brightness>>2, palette, GLInvalidateFlag.All);
		bonuscnt = 0;
		gCutsClock = totalclock = 0;
	}

	@Override
	public void render (float delta) {
		engine.clearview(0);
		engine.sampletimer();

		switch(ud.volume_number)
		{
		case 0:
			if(bonuscnt < 4) {
				engine.rotatesprite(0,50<<16,65536,0,VICTORY1,0,0,2+8+16+64+128,0,0,xdim-1,ydim-1);

				// boss
                if( totalclock > 390 && totalclock < 780 )
                    for(int t=0;t<bossmove.length;t+=5) if( bossmove[t+2] != 0 && (totalclock%390) > bossmove[t] && (totalclock%390) <= bossmove[t+1] )
                {
                    if(t==10 && bonuscnt == 1) { sound(SHOTGUN_FIRE);sound(SQUISHED); bonuscnt++; }
                    engine.rotatesprite(bossmove[t+3]<<16,bossmove[t+4]<<16,65536,0,bossmove[t+2],0,0,2+8+16+64+128,0,0,xdim-1,ydim-1);
                }

                // Breathe
                if( totalclock < 450 || totalclock >= 750 )
                {
                    if(totalclock >= 750)
                    {
                    	engine.rotatesprite(86<<16,59<<16,65536,0,VICTORY1+8,0,0,2+8+16+64+128,0,0,xdim-1,ydim-1);
                        if(totalclock >= 750 && bonuscnt == 2) { sound(DUKETALKTOBOSS); bonuscnt++; }
                    }

                    for(int t=0;t<breathe.length;t+=5)
                        if( breathe[t+2] != 0 && (totalclock%120) > breathe[t] && (totalclock%120) <= breathe[t+1] )
                    {
                            if(t==5 && bonuscnt == 0)
                            {
                                sound(BOSSTALKTODUKE);
                                bonuscnt++;
                            }
                            engine.rotatesprite(breathe[t+3]<<16,breathe[t+4]<<16,65536,0,breathe[t+2],0,0,2+8+16+64+128,0,0,xdim-1,ydim-1);
                    }
                }

                if(isSkipped())
                {
                	bonuscnt = 4;
                	engine.setbrightness(ud.brightness>>2, palette, GLInvalidateFlag.All);
                	StopAllSounds();
                	game.pInput.ctrlResetKeyStatus();
                }
			} else {
                engine.rotatesprite(0,10<<16,63000,0,3292,0,0,2+8+16+64, 0,0,xdim-1,ydim-1);

                if(isSkipped())
                {
                	engine.setbrightness(ud.brightness>>2, palette, GLInvalidateFlag.All);
                	sound(PIPEBOMB_EXPLODE);
                	game.pInput.ctrlResetKeyStatus();
                	game.changeScreen(gStatisticScreen);
                }
			}
			break;
		case 1:
			engine.rotatesprite(0,0,65536,0,3293,0,0,2+8+16+64, 0,0,xdim-1,ydim-1);

			if(isSkipped())
            {
            	engine.setbrightness(ud.brightness>>2, palette, GLInvalidateFlag.All);
            	sound(PIPEBOMB_EXPLODE);
            	game.pInput.ctrlResetKeyStatus();
            	game.changeScreen(gStatisticScreen);
            }
			break;
		case 3:
			app.getFont(2).drawText(160, 60, "THANKS TO ALL OUR", 0, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 60+16, "FANS FOR GIVING", 0, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 60+16+16, "US BIG HEADS.", 0, 0, TextAlign.Center, 2, false);

			app.getFont(2).drawText(160, 70+16+16+16, "LOOK FOR A DUKE NUKEM 3D", 0, 0, TextAlign.Center, 2, false);
			app.getFont(2).drawText(160, 70+16+16+16+16, "SEQUEL SOON.", 0, 0, TextAlign.Center, 2, false);


            if(isSkipped())
            {
            	gAnmScreen.setCallback(new Runnable() {
					@Override
					public void run() {
						game.changeScreen(gStatisticScreen);
					}
            	});
				gAnmScreen.init("duketeam.anm",4);
				game.setScreen(gAnmScreen.escSkipping(false));
            	return;
            }
			break;
		case 4:
			engine.rotatesprite(0,0,65536,0,5367,0,0,2+8+16+64, 0,0,xdim-1,ydim-1);
			if(isSkipped())
            {
            	engine.setbrightness(ud.brightness>>2, palette, GLInvalidateFlag.All);
            	sound(PIPEBOMB_EXPLODE);
            	game.pInput.ctrlResetKeyStatus();
            	game.changeScreen(gStatisticScreen);
            }
			break;
		}

		if (game.pInput.ctrlKeyStatus(ANYKEY))
			gCutsClock = totalclock;

		if (totalclock - gCutsClock < 200) // 2 sec
			DrawEscText(game.getFont(0), MAXPALOOKUPS - RESERVEDPALS - 1);

		engine.nextpage();
	}

	protected void DrawEscText(BuildFont font, int pal) {
		int shade = 16 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
		font.drawText(160, 5, "Press ESC to skip", shade, pal, TextAlign.Center, 2, true);
	}

	public void episode1()
	{
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				ud.volume_number = 0;
				game.setScreen(gEndScreen);
			}
		});
	}

	public void episode2()
	{
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				ud.volume_number = 1;
				sndStopMusic();
				gAnmScreen.init("cineov2.anm", 1);
				gAnmScreen.setCallback(new Runnable() {
					@Override
					public void run() {
						game.setScreen(gEndScreen);
					}
				});
				game.setScreen(gAnmScreen.escSkipping(true));
			}
		});
	}

	public void episode3()
	{
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				ud.volume_number = 2;
				sndStopMusic();
				gAnmScreen.init("cineov3.anm", 2);
				gAnmScreen.setCallback(new Runnable() {
					@Override
					public void run() {
						gAnmScreen.init("radlogo.anm", 3);
						gAnmScreen.setCallback(new Runnable() {
							@Override
							public void run() {
								game.changeScreen(gStatisticScreen);
							}
						});
						gAnmScreen.escSkipping(true);
					}
				});
				game.setScreen(gAnmScreen.escSkipping(true));
			}
		});
	}

	public void episode4()
	{
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				ud.volume_number = 3;
				sndStopMusic();
				gAnmScreen.init("vol4e1.anm", 8);
				gAnmScreen.setCallback(new Runnable() {
					@Override
					public void run() {
						sound(ENDSEQVOL3SND4);
						game.setScreen(gEndScreen);
					}
				});
				game.setScreen(gAnmScreen.escSkipping(true));
			}
		});
	}

	public void episode5()
	{
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				ud.volume_number = 4;
				sndStopMusic();
				sound(E5L7_DUKE_QUIT_YOU);
				game.setScreen(gEndScreen);
			}
		});
	}

	public boolean isSkipped() {
		return game.pInput.ctrlGetInputKey(GameKeys.Menu_Toggle, true)
				|| game.pInput.ctrlPadStatusOnce(GameKeys.Menu_Toggle);
	}
}
