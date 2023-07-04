package ru.m210projects.Build.Script;

import static ru.m210projects.Build.Strhandler.*;

import java.util.HashMap;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.Types.Spriteext;
import ru.m210projects.Build.Types.MD4;

public class MapHackInfo {

	protected Maphack maphack;
	protected HashMap<String, String> hacklist;

	public MapHackInfo() {
		hacklist = new HashMap<String, String>();
	}

	public MapHackInfo(MapHackInfo src) {
		hacklist = new HashMap<String, String>(src.hacklist);
	}

	public boolean addMapInfo(String map, String mhkscript, String md4) {
		byte[] bytes = BuildGdx.cache.getBytes(map, 0);
		if(bytes != null && mhkscript != null) {
			if(md4 == null || MD4.getChecksum(bytes).equals(md4.toUpperCase())) {
				hacklist.put(toLowerCase(map), toLowerCase(mhkscript));
				return true;
			}
		}
		return false;
	}

	public boolean load(String mapname) {
		unload();

		String mhk = hacklist.get(toLowerCase(mapname));
		if(mhk != null) {
			this.maphack = new Maphack(mhk);
			return true;
		}

		return false;
	}

	public boolean isLoaded() {
		return maphack != null;
	}

	public void load(Maphack info) {
		this.maphack = info;
	}

	public void unload() {
		this.maphack = null;
	}

	public boolean hasMaphack(String mapname) {
		return hacklist.get(toLowerCase(mapname)) != null;
	}

	public Spriteext getSpriteInfo(int spriteid) {
		if(maphack != null)
			return maphack.getSpriteInfo(spriteid);
		return null;
	}

}
