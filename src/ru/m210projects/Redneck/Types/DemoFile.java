// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Types;

import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.ResourceHandler.levelGetEpisode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Strhandler;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.LZWDecoder;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Main.UserFlag;

public class DemoFile {

	public int rcnt = 0;
	public Input recsync[][];
	public static Resource recfilep;
	public static LZWDecoder recorder;
	private boolean unpacked = false; //debug
	
	public int reccnt;
	public int version;
	public int volume_number, level_number, player_skill;
	public int coop;
	public int ffire;
	public int multimode;
	public boolean monsters_off;
	public boolean respawn_monsters;
	public boolean respawn_items;
	public boolean respawn_inventory;
	public int playerai;
	public String[] user_name = new String[MAXPLAYERS];
	public int auto_run;
	public String boardfilename;
	public int[] aim_mode = new int[MAXPLAYERS], auto_aim = new int[MAXPLAYERS];
	public GameInfo addon;

	//Record
	
	public int totalreccnt;
	public int recversion;
	public byte[] recbuf;

	public DemoFile(String filename) throws Exception {
		rcnt = 0;
		recversion = -1;
		ud.rec = null;
		recfilep = BuildGdx.cache.open(filename, loadfromgrouponly);
		if(recfilep == null) throw new Exception("File not found");
		reccnt = recfilep.readInt();
		version = recfilep.readByte() & 0xFF;
		
		if( version != BYTEVERSIONRR && version != GDXBYTEVERSION)
		{
			recfilep.close();
			throw new Exception("Wrong version!");
		}
	     
		volume_number = recfilep.readByte();
		level_number = recfilep.readByte();
		player_skill = recfilep.readByte();
		
		coop = recfilep.readByte();
		ffire = recfilep.readByte();
		multimode = recfilep.readShort();
		monsters_off = recfilep.readShort()==1;
		respawn_monsters = recfilep.readInt()==1;
		respawn_items = recfilep.readInt()==1;
		respawn_inventory = recfilep.readInt()==1;
		playerai = recfilep.readInt();
		for ( int i = 0; i < MAXPLAYERS; i++ ) {
			recfilep.read(tempbuf, 0, 32);
			user_name[i] = new String(tempbuf, 0, 32).trim();
		}

		if(version >= GDXBYTEVERSION)
		{
			String addonName = Strhandler.toLowerCase(recfilep.readString(144).trim());
			addon = levelGetEpisode(addonName);
		}

		for(int i=0;i<multimode;i++) {
			aim_mode[i] = recfilep.readByte();
			if(version >= GDXBYTEVERSION)
				auto_aim[i] = recfilep.readByte();
		}

		recsync = new Input[reccnt][MAXPLAYERS];
		int dasizeof = Input.sizeof(version)*multimode;
		byte[] recsyncbuf = new byte[dasizeof * RECSYNCBUFSIZ];
		
		LZWDecoder decoder = null;
		if(!unpacked) 
			decoder = new LZWDecoder(recfilep, dasizeof);
		
		int rccnt = 0;
		for(int c = 0; c <= reccnt / RECSYNCBUFSIZ; c++)
		{
			int l = min(reccnt - rccnt, RECSYNCBUFSIZ);
			if(decoder != null) 
				decoder.read(recsyncbuf, l / multimode);
			else recfilep.read(recsyncbuf, 0, Input.sizeof(version) * l);

			ByteBuffer bb = ByteBuffer.wrap(recsyncbuf);
			bb.order( ByteOrder.LITTLE_ENDIAN);
			
			for(int rcnt = rccnt; rcnt < rccnt + l; rcnt += multimode)
				for ( int i = 0; i < multimode; i++ ) 
					recsync[rcnt / multimode][i] = new Input(bb, version);
			rccnt += RECSYNCBUFSIZ;
		}
		
		if(decoder != null) 
			decoder.close();
		else recfilep.close();
	}
	
	public DemoFile(int nVersion)
	{
		if (ud.recstat == 2)
			recfilep.close();
		int a, b, c, d, democount = 0;
		String fn = null;
		do {
			if (democount > 9999)
				return;

			a = ((democount / 1000) % 10);
			b = ((democount / 100) % 10);
			c = ((democount / 10) % 10);
			d = (democount % 10);

			fn = "demo" + a + b + c + d + ".dmo";
			if (BuildGdx.compat.checkFile(fn) == null)
				break;

			democount++;
		} while (true);
		
		if (fn == null || (recfilep = BuildGdx.compat.open(fn, Path.Game, Mode.Write)) == null)
			return;

		Console.Println("Start recording to " + fn);
		FileResource res = (FileResource) recfilep;
		
		res.writeInt(0);
		res.writeByte(nVersion);

		res.writeByte(ud.volume_number);
		res.writeByte(ud.level_number);
		res.writeByte(ud.player_skill);
		res.writeByte(ud.coop);
		res.writeByte(ud.ffire);
		res.writeShort(ud.multimode);
		res.writeShort(ud.monsters_off ? 1 : 0);
		res.writeInt(ud.respawn_monsters ? 1 : 0);
		res.writeInt(ud.respawn_items ? 1 : 0);
		res.writeInt(ud.respawn_inventory ? 1 : 0);
		res.writeInt(ud.playerai);

		for (int i = 0; i < MAXPLAYERS; i++) {
			Strhandler.buildString(buf, 0, ud.user_name[i]);
			res.writeBytes(buf, 32);
		}

		if(nVersion >= GDXBYTEVERSION) {
			byte[] name = new byte[144];
			if(mUserFlag == UserFlag.Addon && currentGame != null)
			{
				FileEntry addon = currentGame.getFile();
				if(addon != null) {
					String path = addon.getPath();
					path += ":" + currentGame.ConName;
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				}
				else {
					String path = currentGame.getDirectory().checkFile(currentGame.ConName).getPath();
					System.arraycopy(path.getBytes(), 0, name, 0, Math.min(path.length(), 144));
				}
			}
			res.writeBytes(name, name.length);
		}
		
		for (int i = 0; i < ud.multimode; i++) {
			res.writeByte(ps[i].aim_mode);
			if (nVersion >= GDXBYTEVERSION) // JBF 20031126
				res.writeByte(ps[i].auto_aim);
		}

		totalreccnt = 0;
		reccnt = 0;
		recversion = nVersion;
		recbuf = new byte[RECSYNCBUFSIZ * Input.sizeof(BYTEVERSION)];
		
		recorder = new LZWDecoder(res, Input.sizeof(recversion) * ud.multimode);
		
		gDemoScreen.demofiles.add(fn);
	}
	
	public void record() {
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			int len = Input.sizeof(recversion);
			System.arraycopy(sync[i].getBytes(recversion), 0, recbuf, reccnt * len, len);
			reccnt++;
			totalreccnt++;

			if (reccnt >= RECSYNCBUFSIZ) {
				if (recorder != null) {
					try {
						recorder.write(recbuf, reccnt / ud.multimode);
					} catch (Exception e) {
						Console.Println(e.getMessage(), OSDTEXT_RED);
						close();
					}
				} else 
					((FileResource) recfilep).writeBytes(recbuf, reccnt * len);
				reccnt = 0;
			}
		}
	}
	
	public void close()
	{
		if (ud.recstat == 1) {
			try {
				if (reccnt > 0) {
					int len = Input.sizeof(recversion);
					if (recorder != null) {
						recorder.write(recbuf, reccnt / ud.multimode);
					} else
						((FileResource) recfilep).writeBytes(recbuf, reccnt * len);
				}
				recfilep.seek(0, Whence.Set);
				((FileResource) recfilep).writeInt(totalreccnt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Console.Println("Stop recording");
			
			ud.recstat = ud.m_recstat = 0;
			ud.rec = null;
			recversion = 0;
			
			if(recorder != null)
				recorder.close();
			else recfilep.close();
		}
	}

}
