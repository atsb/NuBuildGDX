package ru.m210projects.Tekwar.Menus;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Teksnd.*;
import static ru.m210projects.Tekwar.Tektag.MAXSECTORVEHICLES;
import static ru.m210projects.Tekwar.Tektag.ambsubloop;
import static ru.m210projects.Tekwar.Tektag.mapsndfx;
import static ru.m210projects.Tekwar.Tektag.sectorvehicle;
import static ru.m210projects.Tekwar.Tektag.subwaytrackcnt;
import static ru.m210projects.Tekwar.Tektag.totalmapsndfx;
import static ru.m210projects.Tekwar.Tekprep.*;

public class TekMenuAudio extends MenuAudio {

	private BuildGame app;
	public TekMenuAudio(BuildGame app) {
		super(app, 40, 15, 240, app.getFont(0).getHeight() + 4, app.getFont(0).getHeight(), app.getFont(0));
		this.setListener(listener);
		this.app = app;
	}
	
	private AudioListener listener = new AudioAdapter() {
		
		@Override
		public void PreDrvChange(Driver drv) {
			if(drv == Driver.Sound) {
				stopallsounds();
				if (tekcfg.musicType == cdaudio)
					sndStopMusic();
			}
			if (drv == Driver.Music)
				sndStopMusic();
		}
		
		
		@Override
		public void PostDrvChange() {
			if(app.isCurrentScreen(gGameScreen))
				playsong();
		}
		
		@Override
		public void SoundOff() {
			ambsubloop = -1;
			for (int i=0 ; i < MAXSECTORVEHICLES ; i++) 
		     	sectorvehicle[i].soundindex=-1;
			for(int i=0;i<subwaytrackcnt;i++)
				subwaysound[i] = -1;
			for( int i=0; i<totalmapsndfx; i++ ) 
				mapsndfx[i].id = -1;
			stopallsounds();
		}
	};

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
		title.pal = 3;
		
		return title;
	}

	@Override
	protected char[][] getMusicTypeList()
	{
		char[][] list = new char[2][];
		list[0] = "midi".toCharArray();
		list[1] = "cd audio".toCharArray();
		return list;
	}
	
	@Override
	public boolean SoundRestart(int voices, int resampler) {
		return sndRestart(voices, resampler);
	}

	@Override
	public boolean MusicRestart() {
		return midRestart();
	}
}
