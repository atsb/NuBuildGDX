package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.*;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Names.S_MENUSOUND1;
import static ru.m210projects.Tekwar.Tekmap.*;
import static ru.m210projects.Tekwar.Teksnd.menusong;
import static ru.m210projects.Tekwar.Teksnd.playsound;
import static ru.m210projects.Tekwar.Teksnd.stopallsounds;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Tekwar.Main;
import ru.m210projects.Tekwar.Factory.TekMenuHandler;

public class MissionScreen extends SmkMenu {

	protected boolean onlymatrix;
	protected int lastmission;
	
	public MissionScreen(Main game)
	{
		super(game);
		
		this.setCallback(new Runnable() {
			@Override
			public void run() {
				startmission(currmission);
			}
		});
	}
	
	public void startmission(int map)
	{
		mission = map;
		Console.Println("start mission" + mission);

		mUserFlag = UserFlag.None;
		String name = null;
		switch( mission ) {
		case 0:
			name = "DOLLAR1.SMK";
			break;
	    case 1:
	    	name = "DIMARCO1.SMK";
	        break;
	    case 2:
	    	name = "ROSSI1.SMK";
	        break;
	    case 3:
	        name = "LOWELL1.SMK";
	        break;
	    case 4:
	        name = "SONNY1.SMK";
	        break;
	    case 5:
	        name = "CONNOR1.SMK";
	        break;
	    case 6:
	        name = "JANUS1.SMK";
	        break;
		}
		
		if(name != null && gCutsceneScreen.init(name)) {
			gCutsceneScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					donewgame();
				}	
			}).escSkipping(true);
			Main.game.changeScreen(gCutsceneScreen);
		} else donewgame();
	}

	@Override
	public int skip() {
		if(TEKDEMO) {
	    	 currmission = 10;
	    	 startmission(currmission);
	    	 return currmission;
	    }
		
		if( allsymsdeposited == 2) {
			currmission = 9;
			allsymsdeposited = 3;
			startmission(currmission);
			return 9;
	    }
		
		return -1;
	}

	@Override
	public void keyUp() {
		if(!onlymatrix) matrixMission();
	}

	@Override
	public void keyDown() {
		if(!onlymatrix && currmission == 7)
			backMission();
	}

	@Override
	public void keyLeft() {
		if(!onlymatrix) prevMission();
	}

	@Override
	public void keyRight() {
		if(!onlymatrix) nextMission();
	}

	@Override
	public int loadGame() {
		TekMenuHandler m = Main.game.menu;
		m.mOpen(m.mMenus[LOADGAME], -1);
		currmission = 8;
		return currmission;
	}
	
	private void backMission()
	{
		currmission = lastmission;
	}
	
	private void matrixMission()
	{
		if( !symbols[0] && !symbols[1] 
			&& !symbols[2] && !symbols[3] 
			&& !symbols[4] && !symbols[5] 
			&& !symbols[6] ) 
				AccessWarning();
	    else {
	    	playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);
	    	if(currmission != 7)
	    		lastmission = currmission;
	    	currmission = 7;
	    }
	}
	
	private void prevMission() 
	{
		playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);
		do {
			currmission--;
			if(currmission < 0)
				currmission = 6;
		} while(symbols[currmission]);	
	}
	
	private void nextMission() 
	{
		playsound(S_MENUSOUND1,0,0,0,ST_IMMEDIATE);
		do {
			currmission++;
			if(currmission > 6)
				currmission = 0;
		} while(symbols[currmission]);		
	}

	@Override
	public String init() {
		if(!TEKDEMO) {
			menusong(0);
		    
		    onlymatrix = false;
		    lastmission = currmission = -1;

		    stopallsounds();

		    boolean allsymbols = true;
		    for(int i = 0; i < 7; i++)
		    	if(!symbols[i]) {
		    		allsymbols = false;
		    		break;
		    	}

		    if(!allsymbols)
		    	nextMission();
		    else{
		    	lastmission = currmission = 7;
		    	onlymatrix = true;
		    }
		    
		    return "smkmm.smk";
		}
		
		return null;
	}
	
	@Override
	public void rebuildFrame()
	{
		DrawFrame(1);
		
		DrawFrame(5 + (currmission & 7) * 4); //choosed mission
		for(int i = 0; i < MAXSYMBOLS; i++) //defeated bosses
			if(symbols[i])
				DrawFrame(35 + i * 2);
		
		if(message != MessageType.NONE) //message 
			DrawFrame(49 + message.get() * 2);
	}
	
	

	protected final short[] coord = {
		8,
		51,
		95,
		138,
		181,
		224,
		268
	};
	
	@Override
	public boolean mouseHandler(int x, int y) {

		int oxdim = xdim;
		int xdim = (4 * ydim) / 3;
		int normxofs = x - oxdim / 2;
		int touchX = scale(normxofs, 320, xdim) + 320 / 2;
		int touchY = (int) mulscale(y, divscale(200, ydim, 16), 16);
	
		if(touchY >= 8 && touchY <= 43) //matrix
		{
			if(touchX >= 112 && touchX <= 206)
			{
				if(!symbols[0] && !symbols[1] 
						&& !symbols[2] && !symbols[3] 
						&& !symbols[4] && !symbols[5] 
						&& !symbols[6])
					return false;
				
				
				if(lastmission != 7) {
					currmission = 7;
					lastmission = 7;
					resetStatus();
					return true;
				}
			}
		}
		
		if(touchY >= 114 && touchY <= 192) //missions
		{
			for(int i = 0; i < 7; i++) {
				if(touchX >= coord[i] && touchX <= coord[i] + 40) {
					if(lastmission != i && !symbols[i]) {
						currmission = i;
						lastmission = i;
						resetStatus();
						return true;
					}
				}
			}
			
			if(currmission != -1 && BuildGdx.input.isTouched())
				startmission(currmission);
		}

		return false;
	}
}