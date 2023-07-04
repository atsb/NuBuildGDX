// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Powerslave.Globals.PlayerList;
import static ru.m210projects.Powerslave.Main.game;
import static ru.m210projects.Powerslave.Map.nSwitchSound;
import static ru.m210projects.Powerslave.RunList.AddRunRec;
import static ru.m210projects.Powerslave.RunList.ChangeChannel;
import static ru.m210projects.Powerslave.RunList.NewRun;
import static ru.m210projects.Powerslave.RunList.RunData;
import static ru.m210projects.Powerslave.RunList.SubRunRec;
import static ru.m210projects.Powerslave.RunList.channel;
import static ru.m210projects.Powerslave.Sector.LinkMap;
import static ru.m210projects.Powerslave.Sound.PlayFXAtXYZ;
import static ru.m210projects.Powerslave.Sound.StaticSound;
import static ru.m210projects.Powerslave.View.StatusMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Powerslave.Type.Channel;
import ru.m210projects.Powerslave.Type.SafeLoader;
import ru.m210projects.Powerslave.Type.SwitchStruct;

public class Switch {
	
	public static final int MAXSWITCH = 1024;
	
	public static int SwitchCount;
	public static SwitchStruct[] SwitchData = new SwitchStruct[MAXSWITCH];

	public static void InitSwitch() {
		SwitchCount = MAXSWITCH;
		for (int i = 0; i < MAXSWITCH; i++) {
			if (SwitchData[i] == null)
				SwitchData[i] = new SwitchStruct();
			SwitchData[i].clear();
		}
	}
	
	public static ByteBuffer saveSwitches()
	{
		ByteBuffer bb = ByteBuffer.allocate((MAXSWITCH - SwitchCount) * SwitchStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)SwitchCount);
		for(int i = SwitchCount; i < MAXSWITCH; i++)
			SwitchData[i].save(bb);
		
		return bb;
	}
	
	public static void loadSwitches(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.SwitchCount = bb.readShort();
			for(int i = loader.SwitchCount; i < MAXSWITCH; i++) {
				if(loader.SwitchData[i] == null)
					loader.SwitchData[i] = new SwitchStruct();
				loader.SwitchData[i].load(bb);
			}
		}
		else
		{
			SwitchCount = loader.SwitchCount;
			for(int i = loader.SwitchCount; i < MAXSWITCH; i++) {
				if(SwitchData[i] == null)
					SwitchData[i] = new SwitchStruct();
				SwitchData[i].copy(loader.SwitchData[i]);
			}
		}
	}

	public static int BuildSwStepOn(int a1, int a2, int a3) {
		if (SwitchCount <= 0 || a2 < 0 || a3 < 0) {
			game.ThrowError("Too many switches!");
			return -1;
		}
		SwitchCount--;
		SwitchData[SwitchCount].nChannel = (short) a1;
		SwitchData[SwitchCount].nLink = (short) a2;
		SwitchData[SwitchCount].nSector = (short) a3;
		SwitchData[SwitchCount].field_C = -1;
		return SwitchCount | 0x30000;
	}

	public static void FuncSwStepOn(int a1, int a2, int a3) {
		short sw = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (sw < 0 || sw >= MAXSWITCH) {
			game.ThrowError("sw>=0 && sw<MAXSWITCH");
			return;
		}

		SwitchStruct pSwitch = SwitchData[sw];
		Channel pChannel = channel[pSwitch.nChannel];
		
		if(pChannel.field_4 == -1)
			return; //XXX ?

		int v16 = LinkMap[pSwitch.nLink][pChannel.field_4];
		int nSector = pSwitch.nSector;
		if ((a1 & 0x7F0000) == 0x10000) {
			if (pSwitch.field_C >= 0) {
				SubRunRec(pSwitch.field_C);
				pSwitch.field_C = -1;
			}
			if (v16 >= 0) {
				pSwitch.field_C = (short) AddRunRec(sector[nSector].lotag - 1, RunData[a3].RunEvent);
			}
		} else if ((a1 & 0x7F0000) == 0x50000 && v16 != pChannel.field_4) {
			PlayFXAtXYZ(StaticSound[nSwitchSound], wall[sector[nSector].wallptr].x, wall[sector[nSector].wallptr].y,
					sector[nSector].floorz, nSector);
			ChangeChannel(pSwitch.nChannel, LinkMap[pSwitch.nLink][pChannel.field_4]);
		}
	}

	public static int BuildSwReady(int a1, int a2) {
		if (SwitchCount <= 0 || a2 < 0) {
			game.ThrowError("Too many switch readys!");
			return -1;
		}
		SwitchCount--;
		SwitchData[SwitchCount].nChannel = (short) a1;
		SwitchData[SwitchCount].nLink = (short) a2;
		return 0x10000 | SwitchCount;
	}

	public static void FuncSwReady(int a1, int a2, int a3) {
		short sw = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (sw < 0 || sw >= MAXSWITCH) {
			game.ThrowError("sw>=0 && sw<MAXSWITCH");
			return;
		}
		
		SwitchStruct pSwitch = SwitchData[sw];
		Channel pChannel = channel[pSwitch.nChannel];

		if ((a1 & 0x7F0000) == 0x30000) {
			if (LinkMap[pSwitch.nLink][pChannel.field_4] >= 0)
				ChangeChannel(pSwitch.nChannel, LinkMap[pSwitch.nLink][pChannel.field_4]);
		}
	}

	public static int BuildSwNotOnPause(int nChannel, int nLink, int nSector, int nPause) {
		if (SwitchCount <= 0 || nLink < 0 || nSector < 0) {
			game.ThrowError("Too many switches!");
			return -1;
		}
		SwitchCount--;

		SwitchData[SwitchCount].nChannel = (short) nChannel;
		SwitchData[SwitchCount].nLink = (short) nLink;
		SwitchData[SwitchCount].nPause = (short) nPause;
		SwitchData[SwitchCount].nSector = (short) nSector;
		SwitchData[SwitchCount].field_8 = -1;
		SwitchData[SwitchCount].field_C = -1;
		return SwitchCount | 0x40000;
	}

	public static void FuncSwNotOnPause(int a1, int a2, int RunPtr) {
		short sw = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (sw < 0 || sw >= MAXSWITCH) {
			game.ThrowError("sw>=0 && sw<MAXSWITCH");
			return;
		}
		
		SwitchStruct pSwitch = SwitchData[sw];
		Channel pChannel = channel[pSwitch.nChannel];

		switch (a1 & 0x7F0000) {
		case 0x10000:
			if (pSwitch.field_C >= 0) {
				SubRunRec(pSwitch.field_C);
				pSwitch.field_C = -1;
			}
			if (pSwitch.field_8 >= 0) {
				SubRunRec(pSwitch.field_8);
				pSwitch.field_8 = -1;
			}
			break;
		case 0x20000:
			pSwitch.field_0 -= 4;
			if (pSwitch.field_0 <= 0) {
				ChangeChannel(pSwitch.nChannel, LinkMap[pSwitch.nLink][pChannel.field_4]);
			}
			break;
		case 0x30000:
			if (LinkMap[pSwitch.nLink][pChannel.field_4] >= 0 && pSwitch.field_8 == -1) {
				pSwitch.field_8 = (short) AddRunRec(NewRun, RunData[RunPtr].RunEvent);
				pSwitch.field_0 = pSwitch.nPause;
				pSwitch.field_C = (short) AddRunRec(sector[pSwitch.nSector].lotag - 1,
						RunData[RunPtr].RunEvent);
			}
			break;
		case 0x50000:
			pSwitch.field_0 = pSwitch.nPause;
			break;
		}
	}

	public static int BuildSwPause(int a1, int a2, int a3) {
		for (int i = MAXSWITCH - 1; i >= SwitchCount; --i) {
			if (a1 == SwitchData[i].nChannel && SwitchData[i].nPause != 0)
				return i | 0x20000;
		}
		if (SwitchCount <= 0 || a2 < 0) {
			game.ThrowError("Too many switches!");
			return -1;
		}

		SwitchCount--;
		SwitchData[SwitchCount].nChannel = (short) a1;
		SwitchData[SwitchCount].nLink = (short) a2;
		SwitchData[SwitchCount].nPause = (short) a3;
		SwitchData[SwitchCount].field_8 = -1;
		return SwitchCount | 0x20000;
	}

	public static void FuncSwPause(int a1, int a2, int a3) {
		short sw = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (sw < 0 || sw >= MAXSWITCH) {
			game.ThrowError("sw>=0 && sw<MAXSWITCH");
			return;
		}
		
		SwitchStruct pSwitch = SwitchData[sw];
		Channel pChannel = channel[pSwitch.nChannel];

		switch (a1 & 0x7F0000) {
		case 0x10000:
			if (pSwitch.field_8 >= 0) {
				SubRunRec(pSwitch.field_8);
				pSwitch.field_8 = -1;
			}
			break;
		case 0x20000:
			if (--pSwitch.field_0 <= 0) {
				SubRunRec(pSwitch.field_8);
				pSwitch.field_8 = -1;
				ChangeChannel(pSwitch.nChannel, LinkMap[pSwitch.nLink][pChannel.field_4]);
			}
			break;
		case 0x30000:
			if (LinkMap[pSwitch.nLink][pChannel.field_4] >= 0 && pSwitch.field_8 < 0) {
				pSwitch.field_8 = (short) AddRunRec(NewRun, RunData[a3].RunEvent);
				short nPause = pSwitch.nPause;
				if (nPause <= 0)
					nPause = 100;
				pSwitch.field_0 = nPause;
			}
			break;
		}
	}

	public static int BuildSwPressSector(int a1, int a2, int a3, int a4) {
		if (SwitchCount <= 0 || a2 < 0 || a3 < 0) {
			game.ThrowError("Too many switches!");
			return -1;
		}

		SwitchCount--;
		SwitchData[SwitchCount].nChannel = (short) a1;
		SwitchData[SwitchCount].nLink = (short) a2;
		SwitchData[SwitchCount].nSector = (short) a3;
		SwitchData[SwitchCount].field_12 = (short) a4;
		SwitchData[SwitchCount].field_C = -1;
		return SwitchCount | 0x50000;
	}

	public static void FuncSwPressSector(int a1, int a2, int a3) {
		short sw = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (sw < 0 || sw >= MAXSWITCH) {
			game.ThrowError("sw>=0 && sw<MAXSWITCH");
			return;
		}

		SwitchStruct pSwitch = SwitchData[sw];
		Channel pChannel = channel[pSwitch.nChannel];

		short plr = (short) (a1 & 0xFFFF);
		if ((a1 & 0x7F0000) == 0x40000) {
			if ((pSwitch.field_12 & PlayerList[plr].KeysBitMask) == pSwitch.field_12) {
				ChangeChannel(pSwitch.nChannel, LinkMap[pSwitch.nLink][pChannel.field_4]);
				return;
			}

			if (pSwitch.field_12 != 0)
			{
				PlayFXAtXYZ(StaticSound[nSwitchSound], sprite[PlayerList[plr].spriteId].x,
						sprite[PlayerList[plr].spriteId].y, 0, sprite[PlayerList[plr].spriteId].sectnum);
				StatusMessage(300, "YOU NEED THE KEY FOR THIS DOOR", plr);
			}
			return;
		}

		if (pSwitch.field_C >= 0) {
			SubRunRec(pSwitch.field_C);
			pSwitch.field_C = -1;
		}

		if (LinkMap[pSwitch.nLink][pChannel.field_4] >= 0)
			pSwitch.field_C = (short) AddRunRec(sector[pSwitch.nSector].lotag - 1, RunData[a3].RunEvent);
	}

	public static int BuildSwPressWall(int a1, int a2, int a3) {
		if (SwitchCount <= 0 || a2 < 0 || a3 < 0) {
			game.ThrowError("Too many switches!");
			return -1;
		}
		SwitchCount--;
		SwitchData[SwitchCount].nChannel = (short) a1;
		SwitchData[SwitchCount].nLink = (short) a2;
		SwitchData[SwitchCount].nWall = (short) a3;
		SwitchData[SwitchCount].field_10 = -1;
//		SwitchData[SwitchCount].field_14 = 0;

		return SwitchCount | 0x60000;
	}

	public static void FuncSwPressWall(int a1, int a2, int a3) {
		short sw = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (sw < 0 || sw >= MAXSWITCH) {
			game.ThrowError("sw>=0 && sw<MAXSWITCH");
			return;
		}
		
		SwitchStruct pSwitch = SwitchData[sw];
		Channel pChannel = channel[pSwitch.nChannel];
		
		if(pChannel.field_4 == -1)
			return; //XXX ?

		byte pLinkMap = LinkMap[pSwitch.nLink][pChannel.field_4];
		if ((a1 & 0x7F0000) == 0x30000) {
			if (pSwitch.field_10 >= 0) {
				SubRunRec(pSwitch.field_10);
				pSwitch.field_10 = -1;
			}
			if (pLinkMap >= 0) {
				pSwitch.field_10 = (short) AddRunRec(wall[pSwitch.nWall].lotag - 1,
						RunData[a3].RunEvent);
			}
		} else if ((a1 & 0x7F0000) == 0x40000) {
			ChangeChannel(pSwitch.nChannel, pLinkMap);
			if (pLinkMap < 0 || LinkMap[pSwitch.nLink][pLinkMap] < 0) {
				SubRunRec(pSwitch.field_10);
				pSwitch.field_10 = -1;
			}
			PlayFXAtXYZ(StaticSound[nSwitchSound], wall[pSwitch.nWall].x, wall[pSwitch.nWall].y, 0,
					pSwitch.nSector);
		}
	}

}
