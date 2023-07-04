package ru.m210projects.Wang.Type;

import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Wang.Game.DemoPlaying;
import static ru.m210projects.Wang.Game.DemoRecording;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.RECSYNCBUFSIZ;
import static ru.m210projects.Wang.Game.isDemoRecording;
import static ru.m210projects.Wang.Game.rec;
import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Net.Mmulti;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Wang.Game;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;

public class DemoFile {

	private static final String header = "SWDEM";

	public int rcnt;
	public int reccnt;
	public static Resource recfilep;
	public Input recsync[][];

	public int totalreccnt;
	public byte[] recbuf;

	public String mapname;
	public int numplayers;
	public int Episode;
	public int Level;
	public int Skill;
	public String LevelSong;
	public int[] Flags;

	// gNet
	public int KillLimit;
	public int TimeLimit;
	public int TimeLimitClock;
	public MultiGameTypes MultiGameType;
	public boolean TeamPlay;
	public boolean HurtTeammate;
	public boolean SpawnMarkers;
	public boolean AutoAim;
	public boolean NoRespawn;
	public boolean Nuke;
	public int nVersion;

	public DemoFile(String filename) throws Exception {
		recfilep = BuildGdx.cache.open(filename, 0);
		if (recfilep == null)
			throw new Exception("File not found");

		nVersion = 1;
		mapname = recfilep.readString(16);
		if (mapname.startsWith(header)) {
			recfilep.seek(5, Whence.Set);
			nVersion = recfilep.readInt();
			reccnt = recfilep.readInt();

			numplayers = recfilep.readByte();
			Episode = recfilep.readByte();
			Level = recfilep.readByte();

			Flags = new int[numplayers];
			for (int p = 0; p < numplayers; p++)
				Flags[p] = recfilep.readInt();
		} else {
			numplayers = recfilep.readByte();
			Episode = recfilep.readByte();
			Level = recfilep.readByte();
			LevelSong = recfilep.readString(16);

			Flags = new int[numplayers];

			for (int p = 0; p < numplayers; p++) {
				recfilep.readInt(); // x
				recfilep.readInt(); // y
				recfilep.readInt(); // z
				Flags[p] = recfilep.readInt();
				recfilep.readShort(); // pang
			}
		}
		Skill = recfilep.readShort();

		// gNet
		KillLimit = recfilep.readInt();
		TimeLimit = recfilep.readInt();
		TimeLimitClock = recfilep.readInt();
		int i = recfilep.readShort();
		MultiGameType = i != -1 ? MultiGameTypes.values()[i] : null;
		TeamPlay = recfilep.readBoolean();
		HurtTeammate = recfilep.readBoolean();
		SpawnMarkers = recfilep.readBoolean();
		AutoAim = recfilep.readBoolean();
		NoRespawn = recfilep.readBoolean();
		Nuke = recfilep.readBoolean();
	
		int inputsize = getInputSize();
		int swpackets = recfilep.remaining() / (numplayers * inputsize);

		recsync = new Input[numplayers][swpackets];

		int rcnt = 0;
		while (recfilep.hasRemaining()) {
			for (int p = 0; p < numplayers; p++)
				recsync[p][rcnt] = readInput(recfilep);
			rcnt++;
		}
		reccnt = rcnt;
	}

	public DemoFile(int ver) {
		if (DemoPlaying)
			recfilep.close();
		rec = null;

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

		if (ver != 1) {
			res.writeBytes(header);
			res.writeInt(ver);
			res.writeInt(0);

			res.writeByte(Mmulti.numplayers);
			int level = Game.Level;
			int episode = 0;
			if (level < 5) {
				episode = 0;
			} else if (level < 23) {
				episode = 1;
				level -= 4;
			} else {
				episode = 2;
				level -= 22;
			}

			res.writeByte(episode);
			res.writeByte(level);
			for (int p = 0; p < Mmulti.numplayers; p++)
				res.writeInt(Player[p].Flags);
			res.writeShort(Game.Skill);

			res.writeInt(gNet.KillLimit);
			res.writeInt(gNet.TimeLimit);
			res.writeInt(gNet.TimeLimitClock);
			res.writeShort(gNet.MultiGameType != null ? gNet.MultiGameType.ordinal() : -1);
			res.writeByte(gNet.TeamPlay ? 1 : 0);
			res.writeByte(gNet.HurtTeammate ? 1 : 0);
			res.writeByte(gNet.SpawnMarkers ? 1 : 0);
			res.writeByte(gs.AutoAim ? 1 : 0);
			res.writeByte(gNet.NoRespawn ? 1 : 0);
			res.writeByte(gNet.Nuke ? 1 : 0);
		}

		nVersion = ver;
		totalreccnt = 0;
		reccnt = 0;
		recbuf = new byte[RECSYNCBUFSIZ * getInputSize()];

		gDemoScreen.demofiles.add(fn);
	}

	private int getInputSize() {
		return (nVersion == 1 ? 12 : 16);
	}

	private ByteBuffer InputBuffer;

	public byte[] getBytes(Input i) {
		if (InputBuffer == null || InputBuffer.capacity() != getInputSize()) {
			InputBuffer = ByteBuffer.allocate(getInputSize()).order(ByteOrder.LITTLE_ENDIAN);
		} else
			InputBuffer.clear();

		InputBuffer.putShort((short) i.vel);
		InputBuffer.putShort((short) i.svel);

		if (nVersion == 1) {
			InputBuffer.put((byte) i.angvel);
			InputBuffer.put((byte) i.aimvel);
			recfilep.readShort(); // pad
		} else {
			InputBuffer.putFloat(i.angvel);
			InputBuffer.putFloat(i.aimvel);
		}

		InputBuffer.putInt(i.bits);

		return InputBuffer.array();
	}

	public Input readInput(Resource recfilep) {
		Input pInput = new Input();
		pInput.vel = recfilep.readShort();
		pInput.svel = recfilep.readShort();
		if (nVersion == 1) {
			pInput.angvel = recfilep.readByte();
			pInput.aimvel = recfilep.readByte();
			recfilep.readShort(); // pad
		} else {
			pInput.angvel = recfilep.readFloat();
			pInput.aimvel = recfilep.readFloat();
		}
		pInput.bits = recfilep.readInt();
		return pInput;
	}

	public void record() {
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			PlayerStr pp = Player[i];

			int len = getInputSize();
			System.arraycopy(getBytes(pp.input), 0, recbuf, reccnt * len, len);
			reccnt++;
			totalreccnt++;

			if (reccnt >= RECSYNCBUFSIZ) {
				((FileResource) recfilep).writeBytes(recbuf, reccnt * len);
				reccnt = 0;
			}
		}
	}

	public void close() {
		if (DemoRecording) {
			try {
				if (reccnt > 0) {
					int len = getInputSize();
					((FileResource) recfilep).writeBytes(recbuf, reccnt * len);
				}
				recfilep.seek(9, Whence.Set);
				((FileResource) recfilep).writeInt(totalreccnt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Console.Println("Stop recording");

			DemoRecording = false;
			isDemoRecording = false;
			rec = null;
			nVersion = 0;
		}
		recfilep.close();
	}
}
