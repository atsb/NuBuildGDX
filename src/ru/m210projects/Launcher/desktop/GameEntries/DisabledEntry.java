package ru.m210projects.Launcher.desktop.GameEntries;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;

public abstract class DisabledEntry extends GameEntry {

	public DisabledEntry(String appname) {
		super(appname, "v0.0");
	}

	@Override
	public ResFile[] getResourceFiles() {
		ResFile[] resources = { 	
			new ResFile("")
		};
		return resources;
	}
	
	@Override
	public String[] getIcons() {
		return null;
	}
	
	@Override
	public BuildConfig buildConfig(String path) {
		return null;
	}
	
	@Override
	public BuildGame getGame(String[] args) {
		return null;
	}
	
	@Override
	public void startGame(String gamePath) {
		
	}
}
