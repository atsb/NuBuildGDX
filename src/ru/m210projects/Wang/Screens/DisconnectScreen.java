package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Names.STAT_SCREEN_PIC;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER0;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Sound.StopSound;
import static ru.m210projects.Wang.Text.DisplayMiniBarSmString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.PlayerStr;

public class DisconnectScreen extends StatisticScreen {

	protected List<Integer> playerList;
	protected boolean skipRequest;
	
	protected final int STAT_START_X = 20;
	protected final int STAT_START_Y = 85;
	protected final int STAT_OFF_Y = 9;
	protected final int STAT_HEADER_Y = 14;

	protected final int STAT_TABLE_X = (STAT_START_X + 15 * 4);
	protected final int STAT_TABLE_XOFF = 6 * 4;
	protected final int[] death_total = new int[8];
	protected final int[] kills = new int[8];

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
	public void show() {
		super.show();
		
		skipRequest = false;
		COVER_SetReverb(0);
		StopSound();
	}

	@Override
	public void hide() {
		updateList();
	}

	@Override
	public void draw(float delta) {
		engine.sampletimer();

		checkMusic();

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

	@Override
	public void skip() {
		skipRequest = true;
	}

	public boolean dobonus(boolean disconnect) {
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, STAT_SCREEN_PIC, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);

		app.getFont(1).drawText(160, 68, "MULTIPLAYER TOTALS", 0, 0, TextAlign.Center, 2, false);

		int x = STAT_START_X;
		int y = STAT_START_Y;
		
		PlayerStr mpp = Player[myconnectindex];

		buildString(bonusbuf, 0, "  NAME ");
		DisplayMiniBarSmString(mpp, x, y, 0, bonusbuf, 0);
		x = STAT_TABLE_X;
		for (int i = 0; i < 8; i++) {
			Bitoa(i + 1, bonusbuf);
			DisplayMiniBarSmString(mpp, x, y, 0, bonusbuf, 0);
			x += STAT_TABLE_XOFF;
		}
		buildString(bonusbuf, 0, "  KILLS ");
		DisplayMiniBarSmString(mpp, x, y, 0, bonusbuf, 0);

		int rows = playerList.size();
		int cols = playerList.size();

		y += STAT_HEADER_Y;

		if(gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COOPERATIVE) {
			
			Arrays.fill(death_total, 0);
			Arrays.fill(kills, 0);
			
			for (int i = 0; i < rows; i++) {
				int num = playerList.get(i);
				x = STAT_START_X;
				PlayerStr pp = Player[num];
	
				int offs = Bitoa(i + 1, bonusbuf);
				buildString(bonusbuf, offs, " ", pp.getName());
				DisplayMiniBarSmString(mpp, x, y,
						(pp.PlayerSprite != -1 && pUser[pp.PlayerSprite] != null) ? pUser[pp.PlayerSprite].spal : (PALETTE_PLAYER0 + pp.TeamColor), bonusbuf, 0);
	
				x = STAT_TABLE_X;
				for (int j = 0; j < cols; j++) {
					int pal = 0;
					death_total[j] += pp.KilledPlayer[j];
	
					if (i == j) {
						// don't add kill for self or team player
						pal = PALETTE_PLAYER0 + 4;
						kills[i] -= pp.KilledPlayer[j]; // subtract self kills
					} else if (gNet.TeamPlay && pp.PlayerSprite != -1) {
						if (pUser[pp.PlayerSprite].spal == pUser[Player[j].PlayerSprite].spal) {
							// don't add kill for self or team player
							pal = PALETTE_PLAYER0 + 4;
							kills[i] -= pp.KilledPlayer[j]; // subtract self kills
						} else
							kills[i] += pp.KilledPlayer[j]; // kills added here
					} else {
						kills[i] += pp.KilledPlayer[j]; // kills added here
					}
	
					Bitoa(pp.KilledPlayer[j], bonusbuf);
					DisplayMiniBarSmString(mpp, x, y, pal, bonusbuf, 0);
					x += STAT_TABLE_XOFF;
				}
	
				y += STAT_OFF_Y;
			}
	
			// Deaths
	
			x = STAT_START_X;
			y += STAT_OFF_Y;
	
			buildString(bonusbuf, 0, "   DEATHS");
			DisplayMiniBarSmString(mpp, x, y, 0, bonusbuf, 0);
			x = STAT_TABLE_X;
	
			for (int j = 0; j < cols; j++) {
				Bitoa(death_total[j], bonusbuf);
				DisplayMiniBarSmString(mpp, x, y, 0, bonusbuf, 0);
				x += STAT_TABLE_XOFF;
			}
	
			x = STAT_START_X;
			y += STAT_OFF_Y;
	
			// Kills
			x = STAT_TABLE_X + 50 * 4;
			y = STAT_START_Y + STAT_HEADER_Y;
	
			for (int i = 0; i < rows; i++) {
				Bitoa(kills[i], bonusbuf);
				DisplayMiniBarSmString(mpp, x, y, 0, bonusbuf, 0);
	
				y += STAT_OFF_Y;
			}
		} else {
			for (int num = 0; num < rows; num++) {
				int i = playerList.get(num);
				
				PlayerStr pp = Player[i];
				
				x = STAT_START_X;
				int offs = Bitoa(i + 1, bonusbuf);
				buildString(bonusbuf, offs, " ", pp.getName());
				DisplayMiniBarSmString(mpp, x, y,
						(pp.PlayerSprite != -1 && pUser[pp.PlayerSprite] != null) ? pUser[pp.PlayerSprite].spal : (PALETTE_PLAYER0 + pp.TeamColor), bonusbuf, 0);
				
				x = STAT_TABLE_X + 50 * 4;
				Bitoa(pp.Kills, bonusbuf);
				app.getFont(0).drawText(x, y, bonusbuf, 0, 2, TextAlign.Left, 2, false);
				y += STAT_OFF_Y;
			}
		}
		
		app.getFont(1).drawText(160, 189, "PRESS ANY KEY TO CONTINUE", 0, 2, TextAlign.Center, 2, false);

		if (skipRequest && totalclock > (60 * 2))
			return true;

		return false;
	}

}
