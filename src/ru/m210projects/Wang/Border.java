package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Panel.BORDER_BAR;
import static ru.m210projects.Wang.Panel.BORDER_NONE;

import ru.m210projects.Wang.Type.PlayerStr;

public class Border {

	public static void SetBorder(PlayerStr pp, int value) {
		if (pp != Player[myconnectindex])
			return;

		if (value >= 0) // just refresh
			gs.BorderNum = value;

		if (gs.BorderNum < BORDER_NONE) {
			gs.BorderNum = BORDER_NONE;
		}

		if (gs.BorderNum > BORDER_BAR) {
			gs.BorderNum = BORDER_BAR;
			return;
		}

		BorderSetView(gs.BorderNum);
	}

	public static void BorderSetView(int size) {
		int ss, x1, x2, y1, y2;

		 if(size < 0) size = 0;
		 else if(size > BORDER_BAR) size = BORDER_BAR;

		 ss = Math.max(size-BORDER_BAR,0);

		 x1 = scale(ss,xdim,160);
		 x2 = xdim-x1;

		 y1 = 3*ss; y2 = 200;
		 if (size >= BORDER_BAR) y2 -= (3*(ss)+47);

		 y1 = scale(y1,ydim,200);
		 y2 = scale(y2,ydim,200);

		 engine.setview(x1,y1,x2-1,y2-1);
	}
}
