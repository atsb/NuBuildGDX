// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.Build.Engine.*;

import ru.m210projects.LSP.Main.UserFlag;
import ru.m210projects.LSP.Types.DemoFile;
import ru.m210projects.LSP.Types.PlayerStruct;
import ru.m210projects.LSP.Types.SwingDoor;

public class Globals {
	
	public static final int WHITEPAL = 168;
	
	public static final short INANIMATE = 0;
	public static final short CHASE = 1;
	public static final short GUARD = 2;
	
	public static final short SHOTSPARK = 3;

	public static final short EXPLOSION = 5;
	public static final short PROJECTILE = 6;
	
	public static final short DYING = 20;
	public static final short ATTACK = 21;
	
	public static final short BLUEDUDE = 1536;
	public static final short GREENDUDE = 1792;
	public static final short REDDUDE = 2048;
	public static final short PURPLEDUDE = 2304;
	public static final short YELLOWDUDE = 2560;
	public static final short PLAYER = 197; //249
	public static final short TILE_ANIM = (short) (MAXTILES - 2);
	public static final short ANIM_PAL = (short) (NORMALPAL - 1);
	
	public static final short MAXHEALTH = 160;
	public static final short MAXMANNA = 160;
	public static final short MAXAMMO = 160;
	
	public static short nNextMap;
	public static boolean followmode = false;
	public static int followx, followy;
	public static int followvel, followsvel, followa;
	public static float followang;
	public static UserFlag mUserFlag = UserFlag.None;
	public static int nKickSprite;
	public static int nKickClock;
	public static int nMusicClock;
	
	public static final class MapStruct {
		public final byte num;
		public final byte nextmap[] = new byte[3];
		public final byte music;

		public MapStruct(int num, int map1, int map2, int map3, int music) {
			this.num = (byte) num;
			this.nextmap[0] = (byte) map1;
			this.nextmap[1] = (byte) map2;
			this.nextmap[2] = (byte) map3;
			this.music = (byte) music;
		}
	}

	public static final MapStruct maps[] = { 
			new MapStruct(111, 1, 1, 1, 4), 
			new MapStruct(111, 2, 3, 0, 9),
			new MapStruct(121, 3, 4, 5, 11), 
			new MapStruct(131, 4, 5, 0, 9), 
			new MapStruct(211, 5, 6, 7, 1),
			new MapStruct(221, 6, 7, 0, 12), 
			new MapStruct(231, 7, 8, 0, 11), 
			new MapStruct(112, 8, 0, 0, 13),
			new MapStruct(122, 9, 10, 0, 16), 
			new MapStruct(132, 10, 0, 0, 13), 
			new MapStruct(142, 11, 0, 0, 1),
			new MapStruct(152, 12, 0, 0, 16), 
			new MapStruct(113, 13, 0, 0, 6), 
			new MapStruct(123, 14, 0, 0, 17),
			new MapStruct(133, 15, 16, 0, 6), 
			new MapStruct(143, 16, 17, 0, 17), 
			new MapStruct(153, 17, 0, 0, 6),
			new MapStruct(114, 18, 0, 0, 10), 
			new MapStruct(214, 19, 0, 0, 14), 
			new MapStruct(115, 20, 21, 0, 1),
			new MapStruct(125, 22, 0, 0, 9), 
			new MapStruct(135, 22, 0, 0, 1), 
			new MapStruct(145, 23, 0, 0, 9),
			new MapStruct(155, 24, 0, 0, 6), 
			new MapStruct(165, 25, 0, 0, 9), 
			new MapStruct(116, 26, 27, 0, 7),
			new MapStruct(126, 27, 28, 0, 11), 
			new MapStruct(136, 29, 0, 0, 7), 
			new MapStruct(146, 29, 30, 0, 17),
			new MapStruct(216, 30, 31, 0, 6), 
			new MapStruct(226, 31, 32, 0, 7), 
			new MapStruct(236, 32, 0, 0, 11),
			new MapStruct(246, 33, 0, 0, 6), 
			new MapStruct(117, 99, 99, 99, 4), 
			new MapStruct(117, 99, 99, 99, 4), };
	
	
	public static PlayerStruct gPlayer[] = new PlayerStruct[MAXPLAYERS];
	public static short nDiffDoor, nDiffDoorBack, nTrainWall;
	public static boolean bActiveTrain;
	public static boolean bTrainSoundSwitch;
	public static final int TICSPERFRAME = 4;
	public static int lockclock;
	public static int totalmoves;
	public static int screenpeek = 0;
	public static int mapnum = -1;
	public static short nDifficult = 1;
	public static short nPlayerFirstWeapon = 17;
	public static int oldchoose;
	public static short oldpic;
	
	public static int book;
	public static int chapter;
	public static int verse;
	
	public static int recstat, m_recstat;
	public static DemoFile rec;
	public static final int RECSYNCBUFSIZ = 2520;

	public static short waterfountainwall[] = new short[MAXPLAYERS], waterfountaincnt[] = new short[MAXPLAYERS];

	public static short ypanningwallcnt;
	public static short[] ypanningwalllist = new short[16];

	public static short floorpanningcnt;
	public static short[] floorpanninglist = new short[16];

	public static short warpsectorcnt;
	public static short[] warpsectorlist = new short[16];

	public static short xpanningsectorcnt;
	public static short[] xpanningsectorlist = new short[16];

	public static short warpsector2cnt;
	public static short[] warpsector2list = new short[32];

	public static short subwaytracksector[][] = new short[5][128], subwaynumsectors[] = new short[5], subwaytrackcnt;
	public static int[] subwaystop[] = new int[5][8], subwaystopcnt = new int[5];
	public static int[] subwaytrackx1 = new int[5], subwaytracky1 = new int[5];
	public static int[] subwaytrackx2 = new int[5], subwaytracky2 = new int[5];
	public static int[] subwayx = new int[5], subwaygoalstop = new int[5], subwayvel = new int[5],
			subwaypausetime = new int[5];

	public static short revolvesector[] = new short[4], revolveang[] = new short[4], revolvecnt;
	public static int[][] revolvex = new int[4][48], revolvey = new int[4][48];
	public static int[] revolvepivotx = new int[4], revolvepivoty = new int[4];
	
	public static short swingcnt;
	public static final int   MAXSWINGDOORS = 32;
	public static SwingDoor[] swingdoor = new SwingDoor[MAXSWINGDOORS];

	public static short dragsectorlist[] = new short[16], dragxdir[] = new short[16], dragydir[] = new short[16],
			dragsectorcnt;
	public static int[] dragx1 = new int[16], dragy1 = new int[16], dragx2 = new int[16], dragy2 = new int[16],
			dragfloorz = new int[16];
}
