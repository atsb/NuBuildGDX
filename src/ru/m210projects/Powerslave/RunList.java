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

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Switch.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Grenade.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Slide.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Snake.*;
import static ru.m210projects.Powerslave.Sector.*;
import static ru.m210projects.Powerslave.Enemies.Spider.*;
import static ru.m210projects.Powerslave.Enemies.Anubis.*;
import static ru.m210projects.Powerslave.Enemies.Mummy.*;
import static ru.m210projects.Powerslave.Enemies.Fish.*;
import static ru.m210projects.Powerslave.Enemies.LavaDude.*;
import static ru.m210projects.Powerslave.Enemies.Scorp.*;
import static ru.m210projects.Powerslave.Enemies.Rat.*;
import static ru.m210projects.Powerslave.Enemies.Lion.*;
import static ru.m210projects.Powerslave.Enemies.Roach.*;
import static ru.m210projects.Powerslave.Enemies.Wasp.*;
import static ru.m210projects.Powerslave.Enemies.Rex.*;
import static ru.m210projects.Powerslave.Enemies.Queen.*;
import static ru.m210projects.Powerslave.Enemies.Ra.*;
import static ru.m210projects.Powerslave.Enemies.Set.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Powerslave.Type.Channel;
import ru.m210projects.Powerslave.Type.FuncProc;
import ru.m210projects.Powerslave.Type.RunData;
import ru.m210projects.Powerslave.Type.SafeLoader;

import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Globals.*;

public class RunList {

	public static int ChannelList;
	public static int ChannelLast;
	public static Channel[] channel = new Channel[4096];
	
	public static final int MAXRUN = 25600;

	public static int NewRun;
	public static int RunCount = -1;
	public static int RunChain;
	public static boolean word_966BE;
	public static int nStackCount;
	public static short[] RunFree = new short[MAXRUN];
	public static RunData[] RunData = new RunData[MAXRUN];
	
	private static final FuncProc funclist[] = new FuncProc[] {
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncElev(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSwReady(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSwPause(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSwStepOn(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSwNotOnPause(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSwPressSector(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSwPressWall(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncWallFace(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSlide(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncAnubis(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncPlayer(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncBullet(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSpider(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncCreatureChunk(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncMummy(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncGrenade(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncAnim(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSnake(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncFish(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncLion(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncBubble(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncLava(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncLavaLimb(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncObject(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncRex(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSet(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncQueen(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncQueenHead(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncRoach(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncQueenEgg(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncWasp(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncTrap(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncFishLimb(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncRa(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncScorp(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSoul(a1, a2, RunPtr);
			}
		},
	
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncRat(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncEnergyBlock(a1, a2, RunPtr);
			}
		},
		
		new FuncProc() {
			public void run(int a1, int a2, int RunPtr) {
				FuncSpark(a1, a2, RunPtr);
			}
		}
	};
	
	public static int[] sRunStack = new int[200];

	public static short GrabRun() {
		if (RunCount <= 0 || RunCount > MAXRUN)
			game.ThrowError("RunCount >0 && RunCount<=MAXRUN");
		return RunFree[--RunCount];
	}

	public static ByteBuffer saveRunList()
	{
		ByteBuffer bb = ByteBuffer.allocate(12 + MAXRUN * 2 + MAXRUN * 8 + 8 + 4096 * Channel.size);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putInt(RunCount);
		bb.putInt(NewRun);
		bb.putInt(RunChain);

		for(int i = 0; i < MAXRUN; i++)
			bb.putShort(RunFree[i]);
		for(int i = 0; i < MAXRUN; i++)
			RunData[i].save(bb);

		bb.putInt(ChannelList);
		bb.putInt(ChannelLast);
		for(int i = 0; i < 4096; i++)
			channel[i].save(bb);
		
		return bb;
	}
	
	public static void loadRunList(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			loader.RunCount = bb.readInt();
			loader.NewRun = bb.readInt();
			loader.RunChain = bb.readInt();
	
			for(int i = 0; i < MAXRUN; i++)
				loader.RunFree[i] = bb.readShort();
			for(int i = 0; i < MAXRUN; i++) {
				if(loader.RunData[i] == null)
					loader.RunData[i] = new RunData();
				loader.RunData[i].load(bb);
			}

			loader.ChannelList = bb.readInt();
			loader.ChannelLast = bb.readInt();
			
			for(int i = 0; i < 4096; i++) {
				if(loader.channel[i] == null)
					loader.channel[i] = new Channel();
				loader.channel[i].load(bb);
			}
		}
		else
		{
			RunCount = loader.RunCount;
			NewRun = loader.NewRun;
			RunChain = loader.RunChain;
	
			System.arraycopy(loader.RunFree, 0, RunFree, 0, MAXRUN);
			for(int i = 0; i < MAXRUN; i++) {
				if(RunData[i] == null)
					RunData[i] = new RunData();
				RunData[i].copy(loader.RunData[i]);
			}

			ChannelList = loader.ChannelList;
			ChannelLast = loader.ChannelLast;
			
			for(int i = 0; i < 4096; i++) {
				if(channel[i] == null)
					channel[i] = new Channel();
				channel[i].copy(loader.channel[i]);
			}
		}
	}
	
	public static void FreeRun(int run) {
		if (RunCount < 0 || RunCount >= MAXRUN)
			game.ThrowError("RunCount >= 0 && RunCount<MAXRUN: " + RunCount);
		if (run < 0 || run >= MAXRUN)
			game.ThrowError("run>=0 && run<MAXRUN: " + run);

		RunFree[RunCount] = (short) run;
		RunCount++;

		RunData[run].RunNum = -1;
		RunData[run].RunEvent = -1;
		RunData[run].RunPtr = -1;
	}

	public static short HeadRun() {
		short run = GrabRun();
		RunData[run].RunPtr = -1;
		RunData[run].RunNum = -1;
		return run;
	}

	public static void InitRun() {
		RunCount = MAXRUN;
		nStackCount = 0;
		for (short i = 0; i < MAXRUN; ++i) {
			if(RunData[i] == null)
				RunData[i] = new RunData();
			RunData[i].RunEvent = -1;
			RunData[i].RunNum = -1;
			RunFree[i] = i;
			RunData[i].RunPtr = -1;
		}
		int ptr = HeadRun();
		RunChain = ptr;
		NewRun = ptr;

		for (int i = 0; i < 8; i++)
			PlayerList[i].RunFunc = -1;

		nRadialSpr = -1;
	}

	public static void UnlinkRun(int RunNum) {
		if (RunCount < 0 || RunCount >= MAXRUN)
			game.ThrowError("RunNum >= 0 && RunNum<MAXRUN: " + RunCount);

		RunData data = RunData[RunNum];
		if (RunNum == RunChain) {
			RunChain = data.RunPtr;
		} else {
			if (data.RunNum >= 0) 
				RunData[data.RunNum].RunPtr = data.RunPtr;
			if (data.RunPtr >= 0)
				RunData[data.RunPtr].RunNum = data.RunNum;
		}
	}

	public static void InsertRun(int RunLst, int RunNum) {
		if (RunLst < 0 || RunLst >= MAXRUN)
			game.ThrowError("RunLst >= 0 && RunLst<MAXRUN: " + RunLst);
		if (RunNum < 0 || RunNum >= MAXRUN)
			game.ThrowError(" RunNum >= 0 && RunNum<MAXRUN: " + RunNum);

		RunData data = RunData[RunNum];
		data.RunNum = (short) RunLst;
		data.RunPtr = RunData[RunLst].RunPtr;
		if (data.RunPtr >= 0)
			RunData[data.RunPtr].RunNum = (short) RunNum;
		RunData[RunLst].RunPtr = (short) RunNum;
	}

	public static int AddRunRec(int RunLst, int RunEvent) {
		int RunNum = GrabRun();
		RunData[RunNum].RunEvent = RunEvent;
		InsertRun(RunLst, RunNum); 
		return RunNum;
	}

	public static void DoSubRunRec(int RunPtr) {
		if (RunPtr < 0 || RunPtr >= MAXRUN)
			game.ThrowError("RunPtr>=0 && RunPtr<MAXRUN: " + RunPtr);
		UnlinkRun(RunPtr);
		FreeRun(RunPtr);
	}

	public static void CleanRunRecs() {
		int NxtPtr = RunChain;
		if (NxtPtr >= 0) {
			if (NxtPtr >= MAXRUN)
				game.ThrowError("NxtPtr<MAXRUN: " + NxtPtr);
			NxtPtr = RunData[RunChain].RunPtr;
		}
		while (true) {
			int RunPtr = NxtPtr;
			if (RunPtr < 0)
				return;
			if (RunPtr >= MAXRUN)
				break;
			NxtPtr = RunData[NxtPtr].RunPtr;
			if ((RunData[RunPtr].RunEvent >> 16) < 0)
				DoSubRunRec(RunPtr);
		}
		game.ThrowError("RunPtr<MAXRUN");
	}

	public static void SubRunRec(int RunPtr) {
		if (RunPtr < 0 || RunPtr >= MAXRUN)
			game.ThrowError("RunPtr>=0 && RunPtr<MAXRUN: " + RunPtr);
		RunData[RunPtr].RunEvent = -totalmoves; 
	}

	public static void SendMessageToRunRec(int RunPtr, int a2, int a3) {
		int func = RunData[RunPtr].RunEvent >> 16;
		if (func >= 0 && func <= 38) {
			if(funclist[func] != null) 
				funclist[func].run(a2, a3, RunPtr);
		}
	}

	public static void ExplodeSignalRun() {
		int NxtPtr = RunChain;
		if (RunChain >= 0) {
			if (RunChain >= MAXRUN)
				game.ThrowError("NxtPtr<MAXRUN: " + RunChain);
			NxtPtr = RunData[RunChain].RunPtr;
		}
		while (true) {
			int RunPtr = NxtPtr;
			if (RunPtr < 0)
				return;
			if (RunPtr >= MAXRUN)
				break;

			NxtPtr = RunData[NxtPtr].RunPtr;
			if (RunData[RunPtr].RunEvent >= 0)
				SendMessageToRunRec(RunPtr, 0xA0000, 0);
		}
		game.ThrowError("RunPtr<MAXRUN");
	}

	public static void PushMoveRun(int stack) {
		if (nStackCount < 200) {
			sRunStack[nStackCount] = stack;
			nStackCount++;
		}
	}

	public static int PopMoveRun() {
		if (nStackCount <= 0)
			game.ThrowError(" PopMoveRun() called inappropriately");
		return sRunStack[--nStackCount];
	}

	public static void SignalRun(int run, int stack) {
		int NxtPtr = run;
		if (NxtPtr == RunChain && word_966BE) {
			PushMoveRun(stack);
		} else {
			while (true) {
				word_966BE = true;
				if (NxtPtr >= 0) {
					if (NxtPtr >= MAXRUN)
						game.ThrowError("NxtPtr<MAXRUN");
					NxtPtr = RunData[NxtPtr].RunPtr;
				}
				while (true) {
					int RunPtr = NxtPtr;
					if (RunPtr < 0)
						break;
					if (RunPtr >= MAXRUN)
						game.ThrowError("RunPtr<MAXRUN");
					NxtPtr = RunData[NxtPtr].RunPtr;
					if (RunData[RunPtr].RunEvent >= 0) 
						SendMessageToRunRec(RunPtr, stack, 0);
				}
				
				word_966BE = false;
				if (nStackCount == 0)
					break;
				stack = PopMoveRun();
				NxtPtr = RunChain;
			}
		}
	}

	public static void InitChan() {
		ChannelList = -1;
		ChannelLast = -1;
		for (int i = 0; i < 4096; i++) {
			if(channel[i] == null)
				channel[i] = new Channel();
			channel[i].head = HeadRun();
			channel[i].next = -1;
			channel[i].field_4 = -1;
			channel[i].field_6 = 0;
		}
	}

	public static void ChangeChannel(int ch, int a2) {
		if (channel[ch].next < 0) {
			short tmp = (short) ChannelList;
			ChannelList = ch;
			channel[ch].next = tmp;
		}

		channel[ch].field_4 = (short) a2;
		channel[ch].field_6 |= 2;
	}

	public static void ReadyChannel(int ch) {
		if (channel[ch].next < 0) {
			short tmp = (short) ChannelList;
			ChannelList = ch;
			channel[ch].next = tmp;
		}
		channel[ch].field_6 |= 1;
	}

	public static void ProcessChannels() {
		int ch1, ch2;
		do {
			ch2 = -1;
			ch1 = -1;
			while (ChannelList >= 0) {
				Channel v2 = channel[ChannelList];
				int v3 = v2.field_6;
				int next = v2.next;
				if ((v3 & 2) != 0) {
					v2.field_6 ^= 2;
					SignalRun(v2.head, ChannelList | 0x10000);
				}
				if ((v3 & 1) != 0) {
					v2.field_6 ^= 1;
					SignalRun(v2.head, 0x30000);
				}
				if (v2.field_6 != 0) {
					if (ch1 == -1) {
						ch1 = ChannelList;
						ch2 = ChannelList;
					} else {
						channel[ch2].next = (short) ChannelList;
						ch2 = ChannelList;
					}
				} else {
					channel[ChannelList].next = -1;
				}
				ChannelList = next;
			}
			ChannelList = ch1;
		} while (ch1 != -1);
	}

	public static int FindChannel(int ch) {
		for (int i = 0; i < 4096; i++) {
			if (channel[i].field_4 == -1) {
				channel[i].field_4 = (short) ch;
				return i;
			}
		}
		return -1;
	}

	public static int AllocChannel(int ch) {
		if (ch != 0) {
			for (int i = 0; i < 4096; i++) {
				if (channel[i].field_4 == ch)
					return i;
			}
		}
		return FindChannel(ch);
	}

	public static void ExecObjects() {
		ProcessChannels();
		SignalRun(RunChain, nEventProcess);
	}
}
