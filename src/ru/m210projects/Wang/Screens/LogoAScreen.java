package ru.m210projects.Wang.Screens;

import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.THREED_REALMS_PIC;
import static ru.m210projects.Wang.Sound.CDAudio_Play;
import static ru.m210projects.Wang.Sound.StopSound;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LogoScreen;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;

public class LogoAScreen extends LogoScreen {
	
	public LogoAScreen(BuildGame game, float gShowTime)
	{
		super(game, gShowTime);
		this.setTile(THREED_REALMS_PIC);
	}

	@Override
	public void show()
	{
		super.show();

		StopSound();
		CDAudio_Play(2, true);
		
		Resource fil = BuildGdx.cache.open("3drealms.pal", 0);
		if(fil != null) {
			byte[] pal = new byte[768];
			fil.read(pal);
			fil.close();
			engine.setbrightness(gs.brightness, pal, GLInvalidateFlag.All);
		}
	}
}
