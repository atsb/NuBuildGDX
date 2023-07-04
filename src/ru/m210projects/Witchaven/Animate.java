
package ru.m210projects.Witchaven;

import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Witchaven.Types.ANIMATION;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Types.ANIMATION.CEILZ;
import static ru.m210projects.Witchaven.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Witchaven.Types.ANIMATION.WALLX;
import static ru.m210projects.Witchaven.Types.ANIMATION.WALLY;

public class Animate {
	
	//These variables are for animating x, y, or z-coordinates of sectors,
	//walls, or sprites (They are NOT to be used for changing the [].picnum's)
	//See the setanimation(), and getanimategoal() functions for more details.
	public static final int MAXANIMATES = 512;
	public static int gAnimationCount = 0;
	public static final ANIMATION[] gAnimationData = new ANIMATION[MAXANIMATES];
	
	public static void initanimations()
	{
		for(int i = 0; i < MAXANIMATES; i++)
			gAnimationData[i] = new ANIMATION();
	}
	
	public static Object getobject(int index, int type)
	{
		Object object = null;
		switch(type)
		{
			case WALLX:
			case WALLY:
				object = wall[index];
				break;
			case FLOORZ:
			case CEILZ:
				object = sector[index];
				break;
		}

		return object;
	}
	
	public static int getanimationgoal(Object object, int type)
	{
		int j = -1;
		for(int i = gAnimationCount - 1; i >= 0; i--)
			if (object == gAnimationData[i].ptr && type == gAnimationData[i].type)
				{ j = i; break; }
		return(j);
	}
	
	public static int setanimation(int index, int thegoal, int thevel, int theacc, int type)
	{
		if (gAnimationCount >= MAXANIMATES) return -1;
		
		Object object = getobject(index, type);
		if(object == null) return -1;
		
		int j = getanimationgoal(object, type);
		if(j == -1) j = gAnimationCount;
		
		ANIMATION gAnm = gAnimationData[j];
		gAnm.ptr = object;
		gAnm.id = (short) index;
		gAnm.goal = thegoal;
		gAnm.vel = thevel;
		gAnm.acc = theacc;	
		gAnm.type = (byte) type;

		if (j == gAnimationCount) gAnimationCount++;
		
		return j;
	}
	
	public static void doanimations()
	{
		int j = 0;
		for(int i = gAnimationCount - 1; i >= 0; i--)
		{
			Interpolation gInt = game.pInt;
			ANIMATION gAnm = gAnimationData[i];
			Object obj = gAnm.ptr;
			switch(gAnm.type)
			{
				case WALLX:
					gInt.setwallinterpolate(gAnm.id, (WALL)obj);
					j = ((WALL)obj).x;
					if (j < gAnm.goal)
						((WALL)obj).x = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((WALL)obj).x = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
				case WALLY:
					gInt.setwallinterpolate(gAnm.id, (WALL)obj);
					j = ((WALL)obj).y;
					if (j < gAnm.goal)
						((WALL)obj).y = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((WALL)obj).y = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
				case FLOORZ:
					gInt.setfloorinterpolate(gAnm.id, (SECTOR)obj);
					j = ((SECTOR)obj).floorz;
					if (j < gAnm.goal)
						((SECTOR)obj).floorz = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((SECTOR)obj).floorz = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
				case CEILZ:
					gInt.setceilinterpolate(gAnm.id, (SECTOR)obj);
					j = ((SECTOR)obj).ceilingz;
					if (j < gAnm.goal)
						((SECTOR)obj).ceilingz = Math.min(j+gAnm.vel*TICSPERFRAME, gAnm.goal);
					else
						((SECTOR)obj).ceilingz = Math.max(j-gAnm.vel*TICSPERFRAME, gAnm.goal);
					break;
			}
			gAnm.vel += gAnm.acc;
			
			if (j == gAnm.goal)
			{
				gAnimationCount--;
				if (i != gAnimationCount)
					gAnm.copy(gAnimationData[gAnimationCount]);
			}
		}
	}
}
