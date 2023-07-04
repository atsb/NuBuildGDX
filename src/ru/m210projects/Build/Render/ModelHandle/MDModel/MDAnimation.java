/*
* MDAnimation for Polymost
* by Jonathon Fowler
* See the included license file "BUILDLIC.TXT" for license info.
* 
* This file has been ported to Java by Alexander Makarov-[M210] (m210-2007@mail.ru)
*/

package ru.m210projects.Build.Render.ModelHandle.MDModel;

public class MDAnimation {
	
	public static final int MDANIM_LOOP = 0;
	public static final int MDANIM_ONESHOT = 1;
	public static long mdtims, omdtims;
	public static int mdpause;
	
	public int startframe, endframe;
	public int fpssc, flags;
	public MDAnimation next;
}
