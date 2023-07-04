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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Object.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Slide {
	
	public static class SlideStruct {
		
		public static final int size = 64;
		
		public int nWall1;
		public int nWall2;
		public int nWall3;
		public int nWall4;
		public int x1, y1;
		public int x2, y2;
		public int x3, y3;
		public int x4, y4;
		public int x5, y5;
		public int x6, y6;
		
		public void save(ByteBuffer bb)
		{
			bb.putInt(nWall1);
			bb.putInt(nWall2);
			bb.putInt(nWall3);
			bb.putInt(nWall4);
			
			bb.putInt(x1);
			bb.putInt(y1);
			bb.putInt(x2);
			bb.putInt(y2);
			
			bb.putInt(x3);
			bb.putInt(y3);
			bb.putInt(x4);
			bb.putInt(y4);
			
			bb.putInt(x5);
			bb.putInt(y5);
			bb.putInt(x6);
			bb.putInt(y6);
		}
		
		public void load(Resource bb)
		{
			nWall1 = bb.readInt();
			nWall2 = bb.readInt();
			nWall3 = bb.readInt();
			nWall4 = bb.readInt();
			
			x1 = bb.readInt();
			y1 = bb.readInt();
			x2 = bb.readInt();
			y2 = bb.readInt();
			
			x3 = bb.readInt();
			y3 = bb.readInt();
			x4 = bb.readInt();
			y4 = bb.readInt();
			
			x5 = bb.readInt();
			y5 = bb.readInt();
			x6 = bb.readInt();
			y6 = bb.readInt();
		}

		public void copy(SlideStruct src) {
			nWall1 = src.nWall1;
			nWall2 = src.nWall2;
			nWall3 = src.nWall3;
			nWall4 = src.nWall4;
			
			x1 = src.x1;
			y1 = src.y1;
			x2 = src.x2;
			y2 = src.y2;
			
			x3 = src.x3;
			y3 = src.y3;
			x4 = src.x4;
			y4 = src.y4;
			
			x5 = src.x5;
			y5 = src.y5;
			x6 = src.x6;
			y6 = src.y6;
		}
	}

	public static class SlideStruct2 {
		
		public static final int size = 10;
		
		public short channel;
		public short field_2;
		public short field_4;
		public short field_6;
		public short field_8;
		
		public void save(ByteBuffer bb)
		{
			bb.putShort(channel);
			bb.putShort(field_2);
			bb.putShort(field_4);
			bb.putShort(field_6);
			bb.putShort(field_8);
		}
		
		public void load(Resource bb)
		{
			channel = bb.readShort();
			field_2 = bb.readShort();
			field_4 = bb.readShort();
			field_6 = bb.readShort();
			field_8 = bb.readShort();
		}

		public void copy(SlideStruct2 src) {
			channel = src.channel;
			field_2 = src.field_2;
			field_4 = src.field_4;
			field_6 = src.field_6;
			field_8 = src.field_8;
		}
	}

	public static class PointStruct {
		
		public static final int size = 16;
		
		public short field_0;
		public short field_2;
		public short field_4;
		public short field_6;
		public short field_8;
		public short field_A;
		public short field_C;
		public short field_E;
		
		public void save(ByteBuffer bb)
		{
			bb.putShort(field_0);
			bb.putShort(field_2);
			bb.putShort(field_4);
			bb.putShort(field_6);
			bb.putShort(field_8);
			bb.putShort(field_A);
			bb.putShort(field_C);
			bb.putShort(field_E);
		}
		
		public void load(Resource bb)
		{
			field_0 = bb.readShort();
			field_2 = bb.readShort();
			field_4 = bb.readShort();
			field_6 = bb.readShort();
			field_8 = bb.readShort();
			field_A = bb.readShort();
			field_C = bb.readShort();
			field_E = bb.readShort();
		}

		public void copy(PointStruct src) {
			field_0 = src.field_0;
			field_2 = src.field_2;
			field_4 = src.field_4;
			field_6 = src.field_6;
			field_8 = src.field_8;
			field_A = src.field_A;
			field_C = src.field_C;
			field_E = src.field_E;
		}
	}

	public static int PointCount;
	public static short[] PointFree = new short[1024];

	public static int SlideCount;
	public static short[] SlideFree = new short[128];

	public static PointStruct PointList[] = new PointStruct[1024];
	public static SlideStruct SlideData[] = new SlideStruct[128];
	public static SlideStruct2 SlideData2[] = new SlideStruct2[128];

	public static void InitPoint() {
		PointCount = 0;
		for (int i = 0; i < 1024; i++) {
			PointFree[i] = (short) i;
			if (PointList[i] == null)
				PointList[i] = new PointStruct();
		}
	}
	
	public static ByteBuffer saveSlide()
	{
		ByteBuffer bb = ByteBuffer.allocate((PointCount * PointStruct.size) + 2 + 2048 + 
				((128 - SlideCount) * (SlideStruct.size + SlideStruct2.size)) + 2 + 256);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		saveData(bb);
		savePoints(bb);

		return bb;
	}
	
	public static void loadSlide(SafeLoader loader, Resource bb)
	{
		loadData(loader, bb);
		loadPoints(loader, bb);
	}
	
 	private static ByteBuffer saveData(ByteBuffer bb)
	{
		bb.putShort((short)SlideCount);
		for(int i = SlideCount; i < 128; i++)
		{
			SlideData[i].save(bb);
			SlideData2[i].save(bb);
		}
		
		for(int i = 0; i < 128; i++)
			bb.putShort(SlideFree[i]);

		return bb;
	}
	
	private static void loadData(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.SlideCount = bb.readShort();
			for(int i = loader.SlideCount; i < 128; i++)
			{
				if(loader.SlideData[i] == null)
					loader.SlideData[i] = new SlideStruct();
				loader.SlideData[i].load(bb);
				if(loader.SlideData2[i] == null)
					loader.SlideData2[i] = new SlideStruct2();
				loader.SlideData2[i].load(bb);
			}
			for(int i = 0; i < 128; i++)
				loader.SlideFree[i] = bb.readShort();
		}
		else
		{
			SlideCount = loader.SlideCount;
			for(int i = loader.SlideCount; i < 128; i++)
			{
				if(SlideData[i] == null)
					SlideData[i] = new SlideStruct();
				SlideData[i].copy(loader.SlideData[i]);
				if(SlideData2[i] == null)
					SlideData2[i] = new SlideStruct2();
				SlideData2[i].copy(loader.SlideData2[i]);
			}
			System.arraycopy(loader.SlideFree, 0, SlideFree, 0, 128);
		}
	}
	
	private static ByteBuffer savePoints(ByteBuffer bb)
	{
		bb.putShort((short)PointCount);
		for(int i = 0; i < PointCount; i++)
			PointList[i].save(bb);
		for(int i = 0; i < 1024; i++)
			bb.putShort(PointFree[i]);

		return bb;
	}
	
	private static void loadPoints(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.PointCount = bb.readShort();
			for(int i = 0; i < loader.PointCount; i++) {
				if(loader.PointList[i] == null)
					loader.PointList[i] = new PointStruct();
				loader.PointList[i].load(bb);
			}
			for(int i = 0; i < 1024; i++)
				loader.PointFree[i] = bb.readShort();
		}
		else
		{
			PointCount = loader.PointCount;
			for(int i = 0; i < loader.PointCount; i++) {
				if(PointList[i] == null)
					PointList[i] = new PointStruct();
				PointList[i].copy(loader.PointList[i]);
			}
			System.arraycopy(loader.PointFree, 0, PointFree, 0, 1024);
		}
	}

	public static int GrabPoint() {
		return PointFree[PointCount++];
	}

	public static void InitSlide() {
		SlideCount = 128;
		for (int i = 0; i < SlideCount; i++) {
			SlideFree[i] = (short) i;
			if (SlideData2[i] == null)
				SlideData2[i] = new SlideStruct2();
			if (SlideData[i] == null)
				SlideData[i] = new SlideStruct();
		}
	}

	public static int IdentifySector(int a1) {
		for (int i = 0, j; i < numsectors; ++i) {
			for (j = 0; j < sector[i].wallnum; j++) {
				if (sector[i].wallptr + j == a1)
					return i;
			}
		}
		return -1;
	}

	public static int BuildSlide(int a1, int a2, int a3, int a4, int a5, int a6, int a7) {
		if (SlideCount <= 0) {
			game.ThrowError("Too many slides!");
			return -1;
		}

		SlideCount--;
		short sect = (short) IdentifySector(a2);
		SlideData2[SlideCount].channel = (short) a1;
		SlideData2[SlideCount].field_4 = -1;

		int v14 = GrabPoint();
		SlideData2[SlideCount].field_2 = (short) v14;

		PointList[v14].field_E = -1;
		PointList[v14].field_0 = sect;

		NEXTWALL: for (int w = sector[sect].wallptr; w < sector[sect].wallptr + sector[sect].wallnum; w++) {
			WALL pWall = wall[w];
			for (int i = SlideData2[SlideCount].field_2; i >= 0; i = PointList[i].field_E) {
				if (pWall.nextsector == PointList[i].field_0)
					break NEXTWALL;
			}

			if (pWall.nextsector >= 0) {
				int v24 = GrabPoint();
				PointList[v24].field_E = SlideData2[SlideCount].field_2;
				PointList[v24].field_0 = pWall.nextsector;
				SlideData2[SlideCount].field_2 = (short) v24;
			}
		}

		SlideData[SlideCount].nWall1 = a2;
		SlideData[SlideCount].nWall2 = a3;
		SlideData[SlideCount].nWall3 = a5;
		SlideData[SlideCount].nWall4 = a6;

		SlideData[SlideCount].x1 = wall[a2].x;
		SlideData[SlideCount].y1 = wall[a2].y;
		SlideData[SlideCount].x2 = wall[a5].x;
		SlideData[SlideCount].y2 = wall[a5].y;
		SlideData[SlideCount].x3 = wall[a3].x;
		SlideData[SlideCount].y3 = wall[a3].y;
		SlideData[SlideCount].x4 = wall[a6].x;
		SlideData[SlideCount].y4 = wall[a6].y;
		SlideData[SlideCount].x5 = wall[a4].x;
		SlideData[SlideCount].y5 = wall[a4].y;
		SlideData[SlideCount].x6 = wall[a7].x;
		SlideData[SlideCount].y6 = wall[a7].y;

		short spr = engine.insertsprite(sect, (short) 899);
		SlideData2[SlideCount].field_6 = spr;
		sprite[spr].cstat = -32768;
		sprite[spr].x = wall[a2].x;
		sprite[spr].y = wall[a2].y;
		sprite[spr].z = sector[sect].floorz;
		SlideData2[SlideCount].field_8 = 0;
		return 0x80000 | SlideCount;
	}

	public static void FuncSlide(int a1, int a2, int RunPtr) {
		int nSlide = RunData[RunPtr].RunEvent & 0xFFFF;

		if (nSlide < 0 || nSlide >= 128) {
			game.ThrowError("slide>=0 && slide<MAXSLIDE");
			return;
		}

		short nChannel = channel[SlideData2[nSlide].channel].field_4;

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			int v4 = 0;
			if (nChannel == 1) {
				short nWall = (short) SlideData[nSlide].nWall2;
				int wx = wall[nWall].x;
		        int wy = wall[nWall].y;
		        int xvel = LongSeek(wx, SlideData[nSlide].x5, 20, 20);
		        wx = longSeek_out;
		        int yvel = LongSeek(wy, SlideData[nSlide].y5, 20, 20);
		        wy = longSeek_out;
		        engine.dragpoint(nWall, wx, wy);
		        engine.movesprite(SlideData2[nSlide].field_6, xvel << 14, yvel << 14, 0, 0, 0, 1);
		        if ( xvel == 0 && yvel == 0 )
		          v4 = 1;
		       
		        nWall = (short) SlideData[nSlide].nWall1;
		        engine.dragpoint(nWall, xvel + wall[nWall].x, yvel + wall[nWall].y);

		        nWall = (short) SlideData[nSlide].nWall4;
				wx = wall[nWall].x;
		        wy = wall[nWall].y;
		        xvel = LongSeek(wx, SlideData[nSlide].x6, 20, 20);
		        wx = longSeek_out;
		        yvel = LongSeek(wy, SlideData[nSlide].y6, 20, 20);
		        wy = longSeek_out;
		        engine.dragpoint(nWall, wx, wy);
		        
		        if ( yvel == 0 && xvel == 0)
		            v4++;
		        
		        nWall = (short) SlideData[nSlide].nWall3;
		        engine.dragpoint(nWall, xvel + wall[nWall].x, yvel + wall[nWall].y); 
			} else if ( nChannel == 0 ) {
				short nWall = (short) SlideData[nSlide].nWall1;
				int wx = wall[nWall].x;
		        int wy = wall[nWall].y;
		        int xvel = LongSeek(wx, SlideData[nSlide].x1, 20, 20);
		        wx = longSeek_out;
		        int yvel = LongSeek(wy, SlideData[nSlide].y1, 20, 20);
		        wy = longSeek_out;
		        engine.dragpoint(nWall, wx, wy);
		        if ( xvel == 0 && yvel == 0 )
			          v4 = 1;
		       
		        nWall = (short) SlideData[nSlide].nWall2;
		        engine.dragpoint(nWall, xvel + wall[nWall].x, yvel + wall[nWall].y);
		        
		        nWall = (short) SlideData[nSlide].nWall3;
				wx = wall[nWall].x;
		        wy = wall[nWall].y;
		        xvel = LongSeek(wx, SlideData[nSlide].x2, 20, 20);
		        wx = longSeek_out;
		        yvel = LongSeek(wy, SlideData[nSlide].y2, 20, 20);
		        wy = longSeek_out;
		        engine.dragpoint(nWall, wx, wy);
		        if ( yvel == 0 && xvel == 0)
		            v4++;
		        
		        nWall = (short) SlideData[nSlide].nWall4;
		        engine.dragpoint(nWall, xvel + wall[nWall].x, yvel + wall[nWall].y); 
			}
			
			if (v4 >= 2) {
				SubRunRec(SlideData2[nSlide].field_4);
				SlideData2[nSlide].field_4 = -1;
				D3PlayFX(StaticSound[nStopSound], SlideData2[nSlide].field_6);
				ReadyChannel(SlideData2[nSlide].channel);
			}
			return;
		case 0x10000:
			if (SlideData2[nSlide].field_4 >= 0) {
				SubRunRec(SlideData2[nSlide].field_4);
				SlideData2[nSlide].field_4 = -1;
			}
			if (nChannel == 0 || nChannel == 1) {
				SlideData2[nSlide].field_4 = (short) AddRunRec(NewRun, RunData[RunPtr].RunEvent);
				if (SlideData2[nSlide].field_8 != nChannel) {
					D3PlayFX(StaticSound[23], SlideData2[nSlide].field_6);
					SlideData2[nSlide].field_8 = nChannel;
				}
			}
			return;
		}
	}
}
