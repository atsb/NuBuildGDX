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

package ru.m210projects.LSP.Types;

import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.LSP.Globals.RECSYNCBUFSIZ;
import static ru.m210projects.LSP.Globals.gPlayer;
import static ru.m210projects.LSP.Globals.m_recstat;
import static ru.m210projects.LSP.Globals.mapnum;
import static ru.m210projects.LSP.Globals.nDifficult;
import static ru.m210projects.LSP.Globals.rec;
import static ru.m210projects.LSP.Globals.recstat;
import static ru.m210projects.LSP.Main.gDemoScreen;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class DemoFile {

	public int rcnt = 0;
	public Input recsync[][];
	public static Resource recfilep;

	public int reccnt;
	public int version;
	public int map, skill;

	//Record
	
	public int totalreccnt;
	public int recversion;
	public byte[] recbuf;

	public DemoFile(String filename) throws Exception {
		rcnt = 0;
		recversion = -1;
		rec = null;
		recfilep = BuildGdx.cache.open(filename, 0);
		if(recfilep == null) throw new Exception("File not found");
		reccnt = recfilep.readInt();
		version = recfilep.readByte() & 0xFF;
		
		if( version != 100 )
		{
			recfilep.close();
			throw new Exception("Wrong version!");
		}

		map = recfilep.readByte();
		skill = recfilep.readByte();

		recsync = new Input[reccnt][MAXPLAYERS];
		int rccnt = 0;
		for(int c = 0; c <= reccnt / RECSYNCBUFSIZ; c++)
		{
			int l = min(reccnt - rccnt, RECSYNCBUFSIZ);
			
			for(int rcnt = rccnt; rcnt < rccnt + l; rcnt++)
				for ( int i = 0; i < 1; i++ ) 
					recsync[rcnt][i] = new Input(recfilep);
			rccnt += RECSYNCBUFSIZ;
		}
		recfilep.close();
	}
	
	public DemoFile(int nVersion)
	{
		if (recstat == 2)
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

		res.writeByte(mapnum);
		res.writeByte(nDifficult);

		totalreccnt = 0;
		reccnt = 0;
		recversion = nVersion;
		recbuf = new byte[RECSYNCBUFSIZ * Input.sizeof(100)];
	
		gDemoScreen.demofiles.add(fn);
	}
	
	public void record() {
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			int len = Input.sizeof(recversion);
			System.arraycopy(gPlayer[i].pInput.getBytes(), 0, recbuf, reccnt * len, len);
			reccnt++;
			totalreccnt++;

			if (reccnt >= RECSYNCBUFSIZ) {
				((FileResource) recfilep).writeBytes(recbuf, reccnt * len);
				reccnt = 0;
			}
		}
	}
	
	public void close()
	{
		if (recstat == 1) {
			try {
				if (reccnt > 0) {
					int len = Input.sizeof(recversion);
					((FileResource) recfilep).writeBytes(recbuf, reccnt * len);
				}
				recfilep.seek(0, Whence.Set);
				((FileResource) recfilep).writeInt(totalreccnt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Console.Println("Stop recording");
			
			recstat = m_recstat = 0;
			rec = null;
			recversion = 0;
			
			recfilep.close();
		}
	}

}
