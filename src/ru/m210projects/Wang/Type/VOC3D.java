package ru.m210projects.Wang.Type;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Types.SPRITE;

import static ru.m210projects.Wang.Sound.sndCoords;

import com.badlogic.gdx.utils.Pool.Poolable;

public class VOC3D implements Poolable {
	public VOC_INFO vp; // Pointer to the sound

	public VOC3D prev, next; // Linked voc list

	public Object obj; // Pointer to obj

	public int fx, fy, fz; // Non-Follow literal values
	public int flags; // 3d voc sound flags
	public Source handle; // Current handle to the voc
	public short doplr_delta; // Change in distance since last call
	public short owner = -1; // Hold index into user array to delete looping sounds
	public int num = -1; // Digital Entry number used for callback of looping sounds
	// If sound is active but user == 0, stop the sound
	public short dist; // Current distance of sound from player
	public int priority;
	public int tics; // Tics used to count to next sound occurance
	public int maxtics; // Tics until next sound occurance for intermittent sounds

	public boolean deleted; // Has sound been marked for deletion?

	@Override
	public void reset() {
		prev = null;
		next = null;
		
		obj = null;
		fx = fy = fz = 0;
		flags = 0;
		stop();
		vp = null;
		num = owner = -1;
		doplr_delta = 0;
		priority = 0;

		deleted = true;

		dist = 0;
		tics = 0;
		maxtics = 0;
	}

	public Vector3i getCoords() {
		if (obj == null)
			return sndCoords.set(0, 0, 0);

		if (obj == sndCoords)
			return sndCoords;

		if (obj instanceof VOC3D) {
			VOC3D p = (VOC3D) obj;
			return sndCoords.set(p.fx, p.fy, p.fz);
		}

		if (obj instanceof Sector_Object) {
			Sector_Object p = (Sector_Object) obj;
			return sndCoords.set(p.xmid, p.ymid, p.zmid);
		}

		if (obj instanceof SPRITE) {
			SPRITE p = (SPRITE) obj;
			return sndCoords.set(p.x, p.y, p.z);
		}

		if (obj instanceof PlayerStr) {
			PlayerStr p = (PlayerStr) obj;
			return sndCoords.set(p.posx, p.posy, p.posz);
		}

		return null;
	}

	public boolean isActive() {
		return handle != null && handle.isActive();
	}

	public void stop() {
		if (handle != null) 
			handle.dispose();
		handle = null;
	}
}
