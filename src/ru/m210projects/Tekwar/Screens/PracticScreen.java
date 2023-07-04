package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.LOADGAME;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Teksnd.*;

import ru.m210projects.Tekwar.Main;
import ru.m210projects.Tekwar.Factory.TekMenuHandler;

public class PracticScreen extends SmkMenu {

	private int currmap;
	private int currframe;
	
	public PracticScreen(Main game) {
		super(game);
		
		this.setCallback(new Runnable() {
			@Override
			public void run() {
				if(currmap == 4) //next set
	    		{
	    			currframe++;
	    			currframe &= 1;
	    		} else {
	    			currmission = currmap + currframe * 9;
	    			
	    			System.err.println("Start game " + currmission);
	    		}
			}
		});
	}

	@Override
	public int skip() {
		return -1;
	}

	@Override
	public void keyUp() {
		playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);
		
		int col = (currmap % 3);
		int row = (currmap / 3);
		row = BClipLow(row - 1, 0);
		currmap = (col + 3 * row);
	}

	@Override
	public void keyDown() {
		playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);
		
		int col = (currmap % 3);
		int row = (currmap / 3);
		row = BClipHigh(row + 1, 2);
		currmap = (col + 3 * row);
	}

	@Override
	public void keyLeft() {
		playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);

		int col = (currmap % 3);
		int row = (currmap / 3);
		col = BClipLow(col - 1, 0);
		currmap = (col + 3 * row);
	}

	@Override
	public void keyRight() {
		playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);
		
		int col = (currmap % 3);
		int row = (currmap / 3);
		col = BClipHigh(col + 1, 2);
		currmap = (col + 3 * row);
	}

	@Override
	public int loadGame() {
		TekMenuHandler m = game.menu;
		m.mOpen(m.mMenus[LOADGAME], -1);
		return 18;
	}

	@Override
	public String init() {
		currmap = 0;
		currframe = 0;
		
		MessageType.EXIT.set(0);
		MessageType.HELP.set(1);
		
		return "smkgm.smk";
	}

	@Override
	public void rebuildFrame() {
		int firstframe = 7;
		if(currframe == 0)
			DrawFrame(1);
		else {
			DrawFrame(3);
			firstframe = 43;
		}

		DrawFrame(firstframe + currmap * 4); //choosed mission
		if(message != MessageType.NONE)
			DrawFrame(77 + message.get() * 2);
	}
	
	@Override
	public boolean mouseHandler(int x, int y) {
		return false;	
	}

}
