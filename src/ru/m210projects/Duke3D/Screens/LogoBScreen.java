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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Player.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LogoScreen;


public class LogoBScreen extends LogoScreen {

	private int soundanm;
	public LogoBScreen(BuildGame game) {
		super(game, 3.0f);
		this.setTile(BETASCREEN);
	}
	
	@Override
	public void show()
	{
		super.show();
		soundanm = 0;
		totalclock = 0;
		engine.setbrightness(ud.brightness>>2, titlepal, true);
	}
	
	@Override
	public void draw(float delta) {
		super.draw(delta);
		
		if( totalclock < (250) ) gTicks = 0;

		if( totalclock > 120 && totalclock < (120+60) )
        {
            if(soundanm == 0)
            {
                soundanm = 1;
                sound(PIPEBOMB_EXPLODE);
            }
            engine.rotatesprite(160<<16,104<<16,(totalclock-120)<<10,0,DUKENUKEM,0,0,2+8,0,0,xdim-1,ydim-1);
        }
        else if( totalclock >= (120+60) )
        	engine.rotatesprite(160<<16,(104)<<16,60<<10,0,DUKENUKEM,0,0,2+8,0,0,xdim-1,ydim-1);

        if( totalclock > 220 && totalclock < (220+30) )
        {
            if( soundanm == 1)
            {
                soundanm = 2;
                sound(PIPEBOMB_EXPLODE);
            }

            engine.rotatesprite(160<<16,(104)<<16,60<<10,0,DUKENUKEM,0,0,2+8,0,0,xdim-1,ydim-1);
            engine.rotatesprite(160<<16,(129)<<16,(totalclock - 220 )<<11,0,THREEDEE,0,0,2+8,0,0,xdim-1,ydim-1);
        }
        else if( totalclock >= (220+30) ) 
        	engine.rotatesprite(160<<16,(129)<<16,30<<11,0,THREEDEE,0,0,2+8,0,0,xdim-1,ydim-1);
        
        if (currentGame.getCON().PLUTOPAK) {	// JBF 20030804
	        if( totalclock >= 280 && totalclock < 395 )
	        {
	        	engine.rotatesprite(160<<16,(151)<<16,(410-totalclock)<<12,0,PLUTOPAKSPRITE+1,0,0,2+8,0,0,xdim-1,ydim-1);
	            if(soundanm == 2)
	            {
	                soundanm = 3;
	                sound(FLY_BY);
	            }
	        }
	        else if( totalclock >= 395 )
	        {
	            if(soundanm == 3)
	            {
	                soundanm = 4;
	                sound(PIPEBOMB_EXPLODE);
	            }
	            engine.rotatesprite(160<<16,(151)<<16,30<<11,0,PLUTOPAKSPRITE+1,0,0,2+8,0,0,xdim-1,ydim-1);
	        }
        }
	}

}
