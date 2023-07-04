// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Screens;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.frags;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.*;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Redneck.Main;

public class DisconnectScreen extends StatisticScreen {

	public List<Integer> playerList;

	public DisconnectScreen(Main app) {
		super(app);
		playerList = new ArrayList<Integer>();
	}

	public void updateList() {
		playerList.clear();
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			playerList.add(i);
		}
	}
	
	@Override
	public void hide () {
		updateList();
	}

	@Override
	public void render(float delta) {
		engine.clearview(0);
		engine.sampletimer();

		if (numplayers > 1)
			game.pNet.GetPackets();

		if (dobonus(true)) {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					game.show();
				}
			});
		}

		engine.nextpage();
	}

	public boolean dobonus(boolean disconnect) {
		int i, y, xfragtotal, yfragtotal;

		engine.rotatesprite(0, 0, 65536, 0, MENUSCREEN, 16, 0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(160 << 16, 34 << 16, 32768, 0, INGAMELNRDTHREEDEE, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);

		app.getFont(1).drawText(160, 58 + 5, "MULTIPLAYER TOTALS", 0, 0, TextAlign.Center, 2, false);
		app.getFont(1).drawText(160, 58 + 15, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title, 0, 0, TextAlign.Center, 2, false);

		int pos = 90;
		int t = 0;
		app.getFont(0).drawText(23, pos, "   NAME                                           KILLS", 0, 0, TextAlign.Left,2, false);
		if(ud.coop != 1) 
		{
			for (int num = 0; num < playerList.size(); num++) {
				Bitoa(playerList.get(num) + 1, bonusbuf);
				app.getFont(0).drawText(92 + (num * 23), pos, bonusbuf, 0, 3, TextAlign.Left, 2, false);
			}
			
			for (int num = 0; num < playerList.size(); num++) {
				xfragtotal = 0;
				i = playerList.get(num);
				Bitoa(i + 1, bonusbuf);
	
				app.getFont(0).drawText(30, pos + 10 + t, bonusbuf, 0, 3, TextAlign.Left, 2, false);
				app.getFont(0).drawText(38, pos + 10 + t, ud.user_name[i], 0, ps[i].palookup, TextAlign.Left, 2, false);
	
				for (int num1 = 0; num1 < playerList.size(); num1++) {
					y = playerList.get(num1);
					if (i == y) {
						Bitoa(ps[y].fraggedself, bonusbuf);
						app.getFont(0).drawText(92 + (y * 23), pos + 10 + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);
						xfragtotal -= ps[y].fraggedself;
					} else {
						Bitoa(frags[i][y], bonusbuf);
						app.getFont(0).drawText(92 + (y * 23), pos + 10 + t, bonusbuf, 0, 0, TextAlign.Left, 2, false);
						xfragtotal += frags[i][y];
					}
				}
	
				Bitoa(xfragtotal, bonusbuf);
				app.getFont(0).drawText(101 + (8 * 23), pos + 10 + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);
	
				t += 7;
			}

			for (int num1 = 0; num1 < playerList.size(); num1++) {
				y = playerList.get(num1);
				yfragtotal = 0;
				for (int num = 0; num < playerList.size(); num++) {
					i = playerList.get(num);
					if (i == y)
						yfragtotal += ps[i].fraggedself;
					yfragtotal += frags[i][y];
				}
				Bitoa(yfragtotal, bonusbuf);
				app.getFont(0).drawText(92 + (y * 23), pos + 16 + (8 * 7), bonusbuf, 0, 2, TextAlign.Left, 2, false);
			}
	
			app.getFont(0).drawText(45, pos + 16 + (8 * 7), "DEATHS", 0, 8, TextAlign.Left, 2, false);
		} 
		else {
			pos += 10;
			for (int num = 0; num < playerList.size(); num++) {
				xfragtotal = 0;
				i = playerList.get(num);
				Bitoa(i + 1, bonusbuf);
	
				app.getFont(0).drawText(30, pos + t, bonusbuf, 0, 3, TextAlign.Left, 2, false);
				app.getFont(0).drawText(38, pos + t, ud.user_name[i], 0, ps[i].palookup, TextAlign.Left, 2, false);
				
				Bitoa(ps[i].frag, bonusbuf);
				app.getFont(0).drawText(101 + (8 * 23), pos + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);
				
				t += 7;
			}
		}
		app.getFont(1).drawText(160, 175, "PRESS ANY KEY TO CONTINUE", 0, 2, TextAlign.Center, 2, false);

		if ((game.pInput.ctrlKeyStatusOnce(ANYKEY)) && totalclock > (60 * 2))
			return true;
		
		return false;
	}

}
