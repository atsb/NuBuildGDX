package ru.m210projects.Launcher.desktop.GameEntries;

import static ru.m210projects.Launcher.desktop.Main.appversion;
import static ru.m210projects.Launcher.desktop.Main.headerPath;
import static ru.m210projects.Launcher.desktop.Main.iconPath;

import java.net.URL;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Witchaven.Config;
import ru.m210projects.Witchaven.Main;

public abstract class WH2Entry extends GameEntry {

	public boolean isWH2 = true;
	
	protected WH2Entry(String appname)
	{
		super(appname, appversion);
	}
	
	public WH2Entry() {
		super("Witchaven2GDX", appversion);
	}

	@Override
	public URL getLogo() {
		return this.getClass().getResource("/" +headerPath + "/headerwh2.png");
	}
	
	@Override
	public URL getIcon16() {
		return this.getClass().getResource("/" +iconPath + "/WH2/whii16.png");
	}

	@Override
	public String[] getIcons() {
		String icons[] = {
			iconPath + "/WH2/whii16.png",
			iconPath + "/WH2/whii32.png",
			iconPath + "/WH2/whii128.png",
		};
		
		return icons;
	}

	@Override
	public String getResourceName() {
		return "Witchaven II";
	}
	
	public ResFile[] getResourceFiles() {
		ResFile[] resources = { 
			new ResFile("f_songs"),
			new ResFile("joesnd"), 
			new ResFile("tiles000.art"),
			new ResFile("tables.dat"), 
			new ResFile("palette.dat"), 
			new ResFile("lookup.dat"), 
			new ResFile("level1.map"),
		};
		return resources;
	}

    @Override
	public BuildConfig buildConfig(String path) {
		return (currentConfig = new Config(path, appname + ".ini"));
	}

	@Override
	public BuildGame getGame(String[] args) {
		return new Main(getConfig(), appname, sversion, true, isWH2);
	}
}
