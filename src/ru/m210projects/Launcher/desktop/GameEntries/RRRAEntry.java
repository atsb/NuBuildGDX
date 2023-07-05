package ru.m210projects.Launcher.desktop.GameEntries;

import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Launcher.desktop.Main.appversion;
import static ru.m210projects.Launcher.desktop.Main.headerPath;
import static ru.m210projects.Launcher.desktop.Main.iconPath;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Launcher.desktop.CheckFiles;
import ru.m210projects.Redneck.Config;
import ru.m210projects.Redneck.Main;

public abstract class RRRAEntry extends GameEntry {

	public RRRAEntry() {
		super("NuRedneckAgainGDX", appversion);
	}

	@Override
	public URL getLogo() {
		return this.getClass().getResource("/" +headerPath + "/headerra.png");
	}

	@Override
	public URL getIcon16() {
		return this.getClass().getResource("/" +iconPath + "/RRRA/rr16.png");
	}

	@Override
	public String getResourceName() {
		return "RR Rides Again";
	}

	@Override
	public ResFile[] getResourceFiles() {
		ResFile[] resources = {
				new ResFile("redneck.grp")
			};
			return resources;
	}

	@Override
	public String[] getIcons() {
		String icons[] = {
			iconPath + "/RRRA/rr16.png",
			iconPath + "/RRRA/rr32.png",
			iconPath + "/RRRA/rr128.png",
		};

		return icons;
	}

	@Override
	public CheckFiles checkResources(String path, boolean errorMessage) {
		File directory = new File(path);

		File[] fList = directory.listFiles();
		if(fList == null)
			return new CheckFiles(false, null);

		HashMap<String, File> filesMap = new HashMap<String, File>();
		for (File file : fList)
			filesMap.put(toLowerCase(file.getName()), file);

		ResFile[] resources = getResourceFiles();

		String missingFiles = "<html>";

		//System.out.println("test");

		boolean found = true;
		for (int i = 0; i < resources.length; i++) {
			if (filesMap.get(resources[i].name) == null) {
				found = false;
				if(errorMessage) Console.Println(appname + " error: " + resources[i].name + " is missing!");
				missingFiles = missingFiles + resources[i].name.toUpperCase() + " is missing<br>";
			}

			else {

			if(resources[i].name.equals("redneck.grp") && filesMap.get(resources[i].name).length() == 141174222 ) {
				found = false;
				if(errorMessage) Console.Println(appname + " error: " + resources[i].name + " is wrong!");
				missingFiles = missingFiles + resources[i].name.toUpperCase() + " is wrong<br>";
			}
		}	}
		if(found) return new CheckFiles(true, null);
		else return new CheckFiles(false, missingFiles + "</html>");
	}

	@Override
	public BuildConfig buildConfig(String path) {
		return (currentConfig = new Config(path, appname + ".ini"));
	}

	@Override
	public BuildGame getGame(String[] args) {
		return new Main(getConfig(), appname, sversion, true);
	}
}
