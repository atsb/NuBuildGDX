package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.gGameScreen;
import static ru.m210projects.Witchaven.Main.mUserFlag;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.WHPLR.*;

import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuAudio;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Main.UserFlag;

public class WHMenuAudio extends MenuAudio {

	private Main app;
	public WHMenuAudio(Main app) {
		super(app, 10, 50, 300, app.getFont(0).getHeight() - 2, 8, app.getFont(0));

		this.setListener(listener);
		
		if(!app.WH2) {
			sSoundDrv.listFont = app.getFont(1);
			sMusicDrv.listFont = app.getFont(1);
	
			sSound.sliderNumbers = app.getFont(1);
			sVoices.sliderNumbers = app.getFont(1);
			sMusic.sliderNumbers = app.getFont(1);
	
			sResampler.listFont = app.getFont(1);
			sMusicType.listFont = app.getFont(1);
			
			sSoundSwitch.switchFont = app.getFont(1);
			sMusicSwitch.switchFont = app.getFont(1);
		}
		
		this.app = app;
	}
	
	private AudioListener listener = new AudioAdapter() {
		@Override
		public void PreDrvChange(Driver drv) {
			sndStopMusic();
			if(drv == Driver.Sound) 
				stopallsounds();
		}
		
		@Override
		public void PostDrvChange() {
			if(app.isCurrentScreen(gGameScreen))
			{
				sndStopMusic();
				if(mUserFlag != UserFlag.UserMap)
					startmusic(mapon - 1);
			}
		}	
		
		@Override
		public void SoundOff() {
			stopallsounds();
		}
	};

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WHTitle(text, 90, 0);
	}

	@Override
	protected char[][] getMusicTypeList()
	{
		char[][] list = new char[2][];
		list[0] = "Midi".toCharArray();
		list[1] = "External".toCharArray();
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
