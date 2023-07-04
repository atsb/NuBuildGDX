package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.Potions.MAXPOTIONS;
import static ru.m210projects.Witchaven.Spellbooks.sspellbookanim;
import static ru.m210projects.Witchaven.Weapons.spikeanimtics;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;

public class PrecacheScreen extends PrecacheAdapter {

	public PrecacheScreen(BuildGame game) {
		super(game);
		
		addQueue("Preload floor and ceiling tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numsectors; i++) {
					addTile(sector[i].floorpicnum);
					addTile(sector[i].ceilingpicnum);
				}
				doprecache(0);
			}
		});

		addQueue("Preload wall tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numwalls; i++) {
					addTile(wall[i].picnum);
					if (wall[i].overpicnum >= 0) {
						addTile(wall[i].overpicnum);
					}
				}
				doprecache(0);
			}
		});

		addQueue("Preload sprite tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < MAXSPRITES; i++) {
					if (sprite[i].statnum < MAXSTATUS) 
						cachespritenum(i);
				}
				
				addTile(BAT);
				addTile(GOBLINATTACK);
				addTile(MINOTAURATTACK);
				addTile(KOBOLDATTACK);
				addTile(FREDATTACK);
				addTile(DEVILATTACK);
	
				doprecache(1);
			}
		});
		
		addQueue("Preload hud tiles...", new Runnable() {
			@Override
			public void run() {
				for(int i = KNIFEREADY; i <= BIGAXEDRAW10; i++) //hud weapons
					addTile(i);
				for(int i = THEFONT; i < CRYSTALSTAFF; i++) //small font
					addTile(i);
				
				addTile(SSTATUSBAR);
				for(int i = 0; i < 8; i++)
					addTile(sspellbookanim[i][0].daweaponframe);
				
				addTile(ANNIHILATE);
				addTile(HELMET);
				addTile(SSCOREBACKPIC);
				addTile(SHEALTHBACK);
				
				for(int i = 0; i < 4; i++) {
					addTile(SKEYBLANK+i);
					addTile(SCARY+i);
				}
				
				addTile(SPOTIONBACKPIC);
				for(int i = 0; i < MAXPOTIONS; i++) {
					addTile(SPOTIONARROW+i);
					addTile(SFLASKBLUE+i);
				}
				addTile(SFLASKBLACK);
				for(int i = 0; i < 5; i++)
					addTile(spikeanimtics[i].daweaponframe);
	
				doprecache(1);
			}
		});
	}

	@Override
	protected void draw(String title, int index) {
		engine.clearview(77);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, MAINMENU, -128,
				0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);

		game.getFont(1).drawText(160,100, toCharArray("Loading"), 0, 0, TextAlign.Center, 2, true);
		game.getFont(1).drawText(160,114, toCharArray("please wait..."), 0, 0, TextAlign.Center, 2, true);
		
		game.getFont(1).drawText(160, 130, title, 0, 0, TextAlign.Center, 2, true);
	}
	
	private void cachespritenum(int i)
	{
		int maxc = 1;
		if(sprite[i].picnum == RAT || sprite[i].picnum == GUARDIAN)
			maxc = 15;
		if(sprite[i].picnum == HANGMAN)
			maxc = 40;
		
		if(sprite[i].picnum == GRONHAL || sprite[i].picnum == GRONMU || sprite[i].picnum == GRONSW)
			maxc = 19;
		
		switch(sprite[i].picnum)
		{
			case GOBLINSTAND:
			case GOBLIN:
				maxc = 21;
				break;
			case KOBOLD:
				maxc = 24;
				break;
			case DEVILSTAND:
			case DEVIL:
				maxc = 25;
				break;
			case DRAGON:
				maxc = 11;
				break;
			
			case SPIDER:
				maxc = 39;
				break;
			case MINOTAUR:
				maxc = 35;
				break;
			case FATWITCH:
				maxc = 19;
				break;
			case SKULLY:
				maxc = 20;
				break;
			case JUDYSIT:
			case JUDY:
				maxc = 18;
				break;
		}
		for(int j = sprite[i].picnum; j < (sprite[i].picnum+maxc); j++)
			addTile(j);
	}

}
