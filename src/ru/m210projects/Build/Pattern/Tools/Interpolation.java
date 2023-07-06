//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.Tools;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.mulscale;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class Interpolation {

	protected boolean requestUpdating;

	public enum InterpolationType {
		WallX, WallY, FloorZ, CeilZ, FloorH, CeilH
	}

	public static class IData {
		public Object ptr;
		public InterpolationType type;
		public int oldpos;
		public int bakpos;
	}

	public static class ILoc {
		public int x, y, z;
		public short ang;
	}

	protected final int MAXINTERPOLATIONS = 4096;
	protected ILoc[] gOldSpriteLoc = new ILoc[MAXSPRITES];
	protected int InterpolationCount = 0;
	protected IData[] gInterpolationData = new IData[MAXINTERPOLATIONS];

	protected int[] gWallLoc = new int[MAXWALLS >> 3];
	protected int[] gFloorHeinumLoc = new int[MAXSECTORS >> 3];
	protected int[] gFloorLoc = new int[MAXSECTORS >> 3];
	protected int[] gCeilLoc = new int[MAXSECTORS >> 3];
	protected int[] gCeilHeinumLoc = new int[MAXSECTORS >> 3];
	protected int[] gSpriteLoc = new int[MAXSPRITES >> 3];

	public Interpolation() {
		for (int i = 0; i < MAXINTERPOLATIONS; i++)
			gInterpolationData[i] = new IData();
		for (int i = 0; i < MAXSPRITES; i++)
			gOldSpriteLoc[i] = new ILoc();
	}

	protected void setinterpolation(Object obj, InterpolationType type) {
		if (InterpolationCount == MAXINTERPOLATIONS) {
			Console.Println("Too many interpolations", Console.OSDTEXT_RED);
			return;
		}

		IData data = gInterpolationData[InterpolationCount++];

		data.ptr = obj;
		data.type = type;

		switch (type) {
		case WallX:
			data.oldpos = ((WALL) obj).x;
			break;
		case WallY:
			data.oldpos = ((WALL) obj).y;
			break;
		case FloorZ:
			data.oldpos = ((SECTOR) obj).floorz;
			break;
		case CeilZ:
			data.oldpos = ((SECTOR) obj).ceilingz;
			break;
		case FloorH:
			data.oldpos = ((SECTOR) obj).floorheinum;
			break;
		case CeilH:
			data.oldpos = ((SECTOR) obj).ceilingheinum;
			break;
		}
	}

	protected void stopinterpolation(Object obj, InterpolationType type) {
		for (int i = InterpolationCount - 1; i >= 0; i--) {
			IData data = gInterpolationData[i];
			if (obj == data.ptr && data.type == type) {
				InterpolationCount--;
				gInterpolationData[i] = gInterpolationData[InterpolationCount];
			}
		}
	}

	public boolean clearinterpolations() {
		if (!requestUpdating)
			return false;

		InterpolationCount = 0;
		Arrays.fill(gWallLoc, 0);
		Arrays.fill(gFloorHeinumLoc, 0);
		Arrays.fill(gCeilHeinumLoc, 0);
		Arrays.fill(gFloorLoc, 0);
		Arrays.fill(gCeilLoc, 0);
		Arrays.fill(gSpriteLoc, 0);
		requestUpdating = false;

		return true;
	}

	public void requestUpdating() {
		requestUpdating = true;
	}

	public void dospriteinterp(SPRITE tsp, int smoothratio) {
		ILoc oldLoc = getsprinterpolate(tsp.owner);
		if (oldLoc != null) {
			int x = oldLoc.x;
			int y = oldLoc.y;
			int z = oldLoc.z;
			short nAngle = oldLoc.ang;

			// interpolate sprite position
			x += mulscale(tsp.x - oldLoc.x, smoothratio, 16);
			y += mulscale(tsp.y - oldLoc.y, smoothratio, 16);
			z += mulscale(tsp.z - oldLoc.z, smoothratio, 16);
			nAngle += mulscale(((tsp.ang - oldLoc.ang + 1024) & 0x7FF) - 1024, smoothratio, 16);

			tsp.x = x;
			tsp.y = y;
			tsp.z = z;
			tsp.ang = nAngle;
		}
	}

	public int getValue(IData obj) {
		switch (obj.type) {
		case WallX:
			return ((WALL) obj.ptr).x;
		case WallY:
			return ((WALL) obj.ptr).y;
		case FloorZ:
			return ((SECTOR) obj.ptr).floorz;
		case CeilZ:
			return ((SECTOR) obj.ptr).ceilingz;
		case FloorH:
			return ((SECTOR) obj.ptr).floorheinum;
		case CeilH:
			return ((SECTOR) obj.ptr).ceilingheinum;
		}

		return 0;
	}

	public void dointerpolations(float smoothratio) {
		for (int i = 0; i < InterpolationCount; i++) {
			IData gInt = gInterpolationData[i];
			Object obj = gInt.ptr;

			int value = gInt.bakpos = getValue(gInt);
			value = (int) (gInt.oldpos + ((value - gInt.oldpos) * smoothratio / 65536.0f));

			switch (gInt.type) {
			case WallX:
				((WALL) obj).x = value;
				break;
			case WallY:
				((WALL) obj).y = value;
				break;
			case FloorZ:
				((SECTOR) obj).floorz = value;
				break;
			case CeilZ:
				((SECTOR) obj).ceilingz = value;
				break;
			case FloorH:
				((SECTOR) obj).floorheinum = (short) value;
				break;
			case CeilH:
				((SECTOR) obj).ceilingheinum = (short) value;
				break;
			}
		}
	}

	public void restoreinterpolations() {
		for (int i = 0; i < InterpolationCount; i++) {
			IData gInt = gInterpolationData[i];
			Object obj = gInt.ptr;
			switch (gInt.type) {
			case WallX:
				((WALL) obj).x = gInt.bakpos;
				break;
			case WallY:
				((WALL) obj).y = gInt.bakpos;
				break;
			case FloorZ:
				((SECTOR) obj).floorz = gInt.bakpos;
				break;
			case CeilZ:
				((SECTOR) obj).ceilingz = gInt.bakpos;
				break;
			case FloorH:
				((SECTOR) obj).floorheinum = (short) gInt.bakpos;
				break;
			case CeilH:
				((SECTOR) obj).ceilingheinum = (short) gInt.bakpos;
				break;
			}
		}
	}

	public boolean setsprinterpolate(int nSprite, SPRITE pSprite) {
		if ((gSpriteLoc[nSprite >> 3] & pow2char[nSprite & 7]) == 0) {
			ILoc pLocation = gOldSpriteLoc[nSprite];
			pLocation.x = pSprite.x;
			pLocation.y = pSprite.y;
			pLocation.z = pSprite.z;
			pLocation.ang = pSprite.ang;
			gSpriteLoc[nSprite >> 3] |= pow2char[nSprite & 7];
			return true;
		}

		return false;
	}

	public void clearspriteinterpolate(int nSprite) {
		gSpriteLoc[nSprite >> 3] &= ~pow2char[nSprite & 7];
	}

	public ILoc getsprinterpolate(int nSprite) {
		if ((gSpriteLoc[nSprite >> 3] & pow2char[nSprite & 7]) != 0)
			return gOldSpriteLoc[nSprite];
		return null;
	}

	public void setwallinterpolate(int nWall, WALL pWall) {
		if ((gWallLoc[nWall >> 3] & pow2char[nWall & 7]) == 0) {
			setinterpolation(pWall, InterpolationType.WallX);
			setinterpolation(pWall, InterpolationType.WallY);
			gWallLoc[nWall >> 3] |= pow2char[nWall & 7];
		}
	}

	public void clearwallinterpolate(int nWall, WALL pWall) {
		if ((gWallLoc[nWall >> 3] & pow2char[nWall & 7]) != 0) {
			stopinterpolation(pWall, InterpolationType.WallX);
			stopinterpolation(pWall, InterpolationType.WallY);
			gWallLoc[nWall >> 3] &= ~pow2char[nWall & 7];
		}
	}

	public void setfheinuminterpolate(int nSector, SECTOR pSector) {
		if ((gFloorHeinumLoc[nSector >> 3] & pow2char[nSector & 7]) == 0) {
			setinterpolation(pSector, InterpolationType.FloorH);
			gFloorHeinumLoc[nSector >> 3] |= pow2char[nSector & 7];
		}
	}

	public void clearfheinuminterpolate(int nSector, SECTOR pSector) {
		if ((gFloorHeinumLoc[nSector >> 3] & pow2char[nSector & 7]) != 0) {
			stopinterpolation(pSector, InterpolationType.FloorH);
			gFloorHeinumLoc[nSector >> 3] &= ~pow2char[nSector & 7];
		}
	}

	public void setcheinuminterpolate(int nSector, SECTOR pSector) {
		if ((gCeilHeinumLoc[nSector >> 3] & pow2char[nSector & 7]) == 0) {
			setinterpolation(pSector, InterpolationType.CeilH);
			gCeilHeinumLoc[nSector >> 3] |= pow2char[nSector & 7];
		}
	}

	public void clearcheinuminterpolate(int nSector, SECTOR pSector) {
		if ((gCeilHeinumLoc[nSector >> 3] & pow2char[nSector & 7]) != 0) {
			stopinterpolation(pSector, InterpolationType.CeilH);
			gCeilHeinumLoc[nSector >> 3] &= ~pow2char[nSector & 7];
		}
	}

	public boolean setfloorinterpolate(int nSector, SECTOR pSector) {
		if ((gFloorLoc[nSector >> 3] & pow2char[nSector & 7]) == 0) {
			setinterpolation(pSector, InterpolationType.FloorZ);
			gFloorLoc[nSector >> 3] |= pow2char[nSector & 7];
			return true;
		}
		return false;
	}

	public void clearfloorinterpolate(int nSector, SECTOR pSector) {
		if ((gFloorLoc[nSector >> 3] & pow2char[nSector & 7]) != 0) {
			stopinterpolation(pSector, InterpolationType.FloorZ);
			gFloorLoc[nSector >> 3] &= ~pow2char[nSector & 7];
		}
	}

	public boolean setceilinterpolate(int nSector, SECTOR pSector) {
		if ((gCeilLoc[nSector >> 3] & pow2char[nSector & 7]) == 0) {
			setinterpolation(pSector, InterpolationType.CeilZ);
			gCeilLoc[nSector >> 3] |= pow2char[nSector & 7];
			return true;
		}
		return false;
	}

	public void clearceilinterpolate(int nSector, SECTOR pSector) {
		if ((gCeilLoc[nSector >> 3] & pow2char[nSector & 7]) != 0) {
			stopinterpolation(pSector, InterpolationType.CeilZ);
			gCeilLoc[nSector >> 3] &= ~pow2char[nSector & 7];
		}
	}

}
