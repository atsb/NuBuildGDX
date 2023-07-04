package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.JSector.MAXMIRRORMONSTERS;

import java.util.Arrays;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class MirrorType {
	
	public enum MirrorState
    {
		m_normal, m_viewon, m_pissed
    };
	
	public short mirrorwall;                   // Wall number containing the mirror
    // tile
	public short mirrorsector;                 // nextsector used internally to draw
    // mirror rooms
	public short camera;                       // Contains number of ST1 sprite used
    // as a camera
	public short camsprite;                    // sprite pointing to campic
	public short campic;                       // Editart tile number to draw a
                                        // screen to
	public short numspawnspots;                // Number of spawnspots used
	public short[] spawnspots = new short[MAXMIRRORMONSTERS];// One spot for each possible skill
    // level for a
    // max of up to 4 coolie ghosts to spawn.
	public boolean ismagic;                       // Is this a magic mirror?
	public MirrorState mstate;                 // What state the mirror is currently
    // in
	public int maxtics;                       // Tic count used to time mirror
    // events
	public int tics;                          // How much viewing time has been
    // used on mirror?
	
	
	public void reset()
	{
		mirrorwall = -1;
		mirrorsector = -1;
		
		camera = -1;
		camsprite = -1;
		campic = -1;
		numspawnspots = -1;
		Arrays.fill(spawnspots, (short) -1);
		ismagic = false;
		mstate = null;
		maxtics = -1;
		tics = -1;   
	}


	public void save(BufferResource fil) {
		fil.writeShort(mirrorwall);
		fil.writeShort(mirrorsector);
		
		fil.writeShort(camera);
		fil.writeShort(camsprite);
		fil.writeShort(campic);
		fil.writeShort(numspawnspots);
		fil.writeBytes(spawnspots);
		fil.writeBoolean(ismagic);
		fil.writeByte(mstate != null ? mstate.ordinal() : -1);
		
		fil.writeInt(maxtics);
		fil.writeInt(tics);
	}
	
	public void load(Resource res) {
		mirrorwall = res.readShort();
		mirrorsector = res.readShort();
		
		camera = res.readShort();
		camsprite = res.readShort();
		campic = res.readShort();
		numspawnspots = res.readShort();
		for(int i = 0; i < MAXMIRRORMONSTERS; i++)
			spawnspots[i] = res.readShort();
		ismagic = res.readBoolean();
		int i = res.readByte();
		mstate = i != -1 ? MirrorState.values()[i] : null;
	
		maxtics = res.readInt();
		tics = res.readInt();
	}


	public void copy(MirrorType src) {
		this.mirrorwall = src.mirrorwall;
		this.mirrorsector = src.mirrorsector;
		this.camera = src.camera;
		this.camsprite = src.camsprite;
		this.campic = src.campic;
		this.numspawnspots = src.numspawnspots;
		System.arraycopy(src.spawnspots, 0, spawnspots, 0, MAXMIRRORMONSTERS);
		this.ismagic = src.ismagic;
		this.mstate = src.mstate;
		this.maxtics = src.maxtics;
		this.tics = src.tics;
	}
}
