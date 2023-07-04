package ru.m210projects.Tekwar.Types;

import static ru.m210projects.Tekwar.Tekprep.spriteXT;

import ru.m210projects.Build.FileHandle.Resource;

public class XTsavetype {
	public static final int sizeof = 2 + SpriteXT.sizeof;
	public short XTnum;

    public boolean load(Resource data) {
    	Short val = data.readShort();
    	if(val == null) return false;
    	
    	XTnum = val;
    	spriteXT[XTnum].load(data);
    	return true;
    }
}
