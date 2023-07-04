package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Names.MAINMENU;
import static ru.m210projects.Witchaven.WHPLR.mapon;
import static ru.m210projects.Witchaven.WHSND.SND_CheckLoops;
import static ru.m210projects.Witchaven.WHSND.SND_Sound;
import static ru.m210projects.Witchaven.WHSND.S_CHAINDOOR1;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Witchaven.Types.PLAYER;

public class StatisticsScreen extends CutsceneScreen {

	private boolean inited = false;
	private String ratings[] = { "poor", "average", "good", "perfect" };
	private int bonus, rating;

	public StatisticsScreen(BuildGame game) {
		super(game);
	}

	public void show(PLAYER plr, Runnable callback) {
		SND_Sound(S_CHAINDOOR1);

		inited = false;
		if(init("stairs.smk"))
			inited = true;

		if (kills > killcnt) 
			kills = killcnt;
		int killp = (kills * 100) / (killcnt + 1);
		if (treasuresfound > treasurescnt) 
			treasuresfound = treasurescnt;
		int treap = (treasuresfound * 100) / (treasurescnt + 1);
		rating = (killp + treap) / 2;
		if (rating >= 95) {
			rating = 3;
		} else if (rating >= 70) 
			rating = 2;
		else if (rating >= 40)
			rating = 1;
		else rating = 0;	
		bonus = rating * 500;
		plr.score += bonus;

		game.changeScreen(this.setSkipping(callback));
	}
	
	@Override
	public void show() {
		SND_CheckLoops();

		if(game.pMenu.gShowMenu)
			game.pMenu.mClose();

		engine.sampletimer();
		LastMS = engine.getticks();
		totalclock = 0;
	}
	

	@Override
	public void draw(float delta) {
		if (inited) {
			if (!play())
				frame = 0;
		} else {
			engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, MAINMENU, 24, 0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);
		}
		
		if(gCurrentEpisode != null && gCurrentEpisode.getMap(mapon) != null)
			game.getFont(1).drawText(10, 13, gCurrentEpisode.getMap(mapon).title, 0, 0, TextAlign.Left, 2, false);
		game.getFont(1).drawText(10, 31, "Level conquered", 0, 0, TextAlign.Left, 2, false);

		game.getFont(1).drawText(10, 64, "Enemies killed", 0, 0, TextAlign.Left, 2, false);
		game.getFont(1).drawText(160 + 48 + 14, 64, kills + " of " + killcnt, 0, 0, TextAlign.Left, 2, false);

		game.getFont(1).drawText(10, 64 + 18, "Treasures found", 0, 0, TextAlign.Left, 2, false);
		game.getFont(1).drawText(160 + 48 + 14, 64 + 18, treasuresfound + " of " + treasurescnt, 0, 0, TextAlign.Left, 2, false);

		game.getFont(1).drawText(10, 64 + 2 * 18, "Experience gained", 0, 0, TextAlign.Left, 2, false);
		game.getFont(1).drawText(160 + 48 + 14, 64 + 2 * 18, "" + (expgained + bonus), 0, 0, TextAlign.Left, 2, false);

		game.getFont(1).drawText(10, 64 + 3 * 18, "Rating", 0, 0, TextAlign.Left, 2, false);
		game.getFont(1).drawText(160 + 48 + 14, 64 + 3 * 18, "" + ratings[rating], 0, 0, TextAlign.Left, 2, false);

		game.getFont(1).drawText(10, 64 + 4 * 18, "Bonus", 0, 0, TextAlign.Left, 2, false);
		game.getFont(1).drawText(160 + 48 + 14, 64 + 4 * 18, "" + bonus, 0, 0, TextAlign.Left, 2, false);

	}

}
