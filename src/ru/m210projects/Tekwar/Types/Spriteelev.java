package ru.m210projects.Tekwar.Types;
import static ru.m210projects.Tekwar.Tektag.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Spriteelev {

	private static ByteBuffer buffer;
	public static final int sizeof = 292;
	
	public int  state;
    public int  parts;
    public int[]  sprnum = new int[MAXPARTS];
    public int[]  door= new int[MAXELEVDOORS];
    public int doorpos;
    public int[] startz= new int[MAXPARTS];
    public int[] floorz= new int[MAXELEVFLOORS];
    public int  curfloor;
    public int  curdir;
    public int  delay;
    public int  floors;
    public int floorpos;
    public int  doors;
    
    public void load(Resource bb) {
    	state = bb.readInt();
    	parts = bb.readInt();
    	for(int i = 0; i < MAXPARTS; i++) 
    		sprnum[i] = bb.readInt();
    	for(int i = 0; i < MAXELEVDOORS; i++) 
    		door[i] = bb.readInt();
    	doorpos = bb.readInt();
    	for(int i = 0; i < MAXPARTS; i++) 
    		startz[i] = bb.readInt();
    	for(int i = 0; i < MAXELEVFLOORS; i++) 
    		floorz[i] = bb.readInt();
    	curfloor = bb.readInt();
    	curdir = bb.readInt();
    	delay = bb.readInt();
    	floors = bb.readInt();
    	floorpos = bb.readInt();
    	doors = bb.readInt();
    }
    
    public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putInt(state);
		buffer.putInt(parts);
		for (int j = 0; j < MAXPARTS; j++)
			buffer.putInt(sprnum[j]);
		for (int j = 0; j < MAXELEVDOORS; j++)
			buffer.putInt(door[j]);
		buffer.putInt(doorpos);
		for (int j = 0; j < MAXPARTS; j++)
			buffer.putInt(startz[j]);
		for (int j = 0; j < MAXELEVFLOORS; j++)
			buffer.putInt(floorz[j]);
		buffer.putInt(curfloor);
		buffer.putInt(curdir);
		buffer.putInt(delay);
		buffer.putInt(floors);
		buffer.putInt(floorpos);
		buffer.putInt(doors);
		
		System.err.println("spriteelev " + buffer.position());
		
		return buffer.array();
	}

	public void reset() {
		state = 0;
    	parts = 0;
    	for(int i = 0; i < MAXPARTS; i++) 
    		sprnum[i] = 0;
    	for(int i = 0; i < MAXELEVDOORS; i++) 
    		door[i] = 0;
    	doorpos = 0;
    	for(int i = 0; i < MAXPARTS; i++) 
    		startz[i] = 0;
    	for(int i = 0; i < MAXELEVFLOORS; i++) 
    		floorz[i] = 0;
    	curfloor = 0;
    	curdir = 0;
    	delay = 0;
    	floors = 0;
    	floorpos = 0;
    	doors = 0;
	}
}
