//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.ScreenAdapters;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.inet;
import static ru.m210projects.Build.Net.Mmulti.initmultiplayers;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Strhandler.toCharArray;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;

public abstract class ConnectAdapter extends ScreenAdapter {

	protected BuildGame game;
	private NetFlag gNetFlag;
	private String[] gNetParam;
	private int ConnectStep = 0;
	private final int nTile;
	private final BuildFont style;
	
	public enum NetFlag {
		Create, Connect
    }
	
	public ConnectAdapter(BuildGame game, int nTile, BuildFont style)
	{
		this.game = game;
		this.nTile = nTile;
		this.style = style;
	}

	public abstract void back();
	
	public abstract void connect();
	
	@Override
	public void show() {
		game.pNet.ResetNetwork();
		initmultiplayers(gNetParam, 0);
		ConnectStep = 0;
	}

	public ScreenAdapter setFlag(NetFlag flag, String[] param) {
		this.gNetFlag = flag;
		this.gNetParam = param;
		return this;
	}

	@Override
	public void render(float delta) {
		game.pEngine.clearview(0);
		game.pEngine.rotatesprite(160 << 16, 100 << 16, 65536, 0, nTile, 0, 0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);

		switch (gNetFlag) {
		case Create:
		case Connect:
			if (inet.waiting()) {
				if (myconnectindex == connecthead) {
					style.drawText(160, 150, toCharArray("Local IP: " + inet.myip), -128, 0, TextAlign.Center, 2, false);
					if (inet.useUPnP) {
						String extip = "Public IP: ";
						if (inet.extip != null)
							extip += inet.extip;

						style.drawText(160, 160, toCharArray(extip), -128, 0, TextAlign.Center, 2, false);
					}
				}

				if (inet.message != null && !inet.message.isEmpty())
					style.drawText(160, 180, toCharArray(inet.message), -128, 0, TextAlign.Center, 2, false);
				else
					style.drawText(160, 180, toCharArray("Initializing..."), -128, 0, TextAlign.Center, 2, false);
					
				game.pEngine.nextpage();
				return;
			}

			if (inet.netready == 0) {
				Console.Println(inet.message, OSDTEXT_YELLOW);
				back();
				
				game.pEngine.nextpage();
				return;
			}

			if (ConnectStep == 0) {
				if (inet.message != null)
					style.drawText(160, 180, toCharArray(inet.message), -128, 0, TextAlign.Center, 2, false);
				else
					style.drawText(160, 180, toCharArray("Connected! Waiting for other players..."), -128, 0, TextAlign.Center, 2, false);
				ConnectStep = 1;

				game.pNet.StartWaiting(5000);
				
				game.pEngine.nextpage();
				return;
			}

			connect();
		}

		game.pEngine.nextpage();
	}
	
	@Override
	public void pause () {
	}

	@Override
	public void resume () {
		game.updateColorCorrection();
	}
}
