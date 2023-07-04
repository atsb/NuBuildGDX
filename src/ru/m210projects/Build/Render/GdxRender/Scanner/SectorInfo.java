package ru.m210projects.Build.Render.GdxRender.Scanner;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.wall;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Engine.Clockdir;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SECTOR.SectorIterator;
import ru.m210projects.Build.Types.WALL;

public class SectorInfo {

	public Clockdir[] loopinfo = new Clockdir[MAXWALLS];
	public boolean[] isOccluder = new boolean[MAXWALLS];
	public boolean[] isContour = new boolean[MAXWALLS];
	public int[] numloops = new int[MAXSECTORS];
	public boolean[] hasOccluders = new boolean[MAXSECTORS];
	public boolean[] isCorrupt = new boolean[MAXSECTORS];

	public void init(Engine engine) {
		for (int i = 0; i < numsectors; i++) {
			SECTOR sec = sector[i];
			SectorIterator it = sec.iterator();

			hasOccluders[i] = false;
			Clockdir dir = engine.clockdir(sec.wallptr);
			boolean hasContour = false;
			int numloops = 0;
			while (it.hasNext()) {
				short z = it.nexti();
				loopinfo[z] = dir == null ? (dir = engine.clockdir(z)) : dir;
				WALL wal = wall[z];
				int nextsector = wal.nextsector;
				if (dir == Clockdir.CCW && nextsector == -1) {
					isOccluder[z] = true;
					hasOccluders[i] = true;
				} else
					isOccluder[z] = false;

                isContour[z] = dir == Clockdir.CW && nextsector == -1;

				if (wal.point2 < z) {
					numloops++;
					if (dir == Clockdir.CW)
						hasContour = true;
					dir = null;
				}
			}

			this.numloops[i] = numloops;
			if (numloops > 0 && !hasContour) {
				isCorrupt[i] = true;
				System.err.println("Error: sector " + i + " has not contour!");
			} else
				isCorrupt[i] = false;
		}
	}

	public boolean isCorruptSector(int i) {
		return isCorrupt[i];
	}

	public boolean isInnerWall(int i) {
		return loopinfo[i] == Clockdir.CCW;
	}

	public boolean isContourWall(int i) {
		return isContour[i];
	}

	public boolean isOccluderWall(int i) {
		return isOccluder[i];
	}

	public boolean hasOccluders(int sectnum) {
		return hasOccluders[sectnum];
	}

	public int getNumloops(int sectnum) {
		return numloops[sectnum];
	}
}
