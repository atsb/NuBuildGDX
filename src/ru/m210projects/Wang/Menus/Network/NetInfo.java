package ru.m210projects.Wang.Menus.Network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NetInfo {

	public final int sizeof = 14;
	private final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order(ByteOrder.LITTLE_ENDIAN);

	public int nGameType;
	public int nEpisode;
	public int nLevel;
	public int nDifficulty;
	public int nMonsters;
	public int nFriendlyFire;

	public boolean SpawnMarkers = true;
	public boolean TeamPlay = false;
	public boolean NetNuke = true;
	public int TimeLimit = 0;
	public int KillLimit = 0;

	public void set(ByteBuffer bb) {
		nGameType = bb.get();
		nEpisode = (short) (bb.get() & 0xFF);
		nLevel = (short) (bb.get() & 0xFF);
		nDifficulty = bb.get();
		nMonsters = bb.get();
		nFriendlyFire = bb.get();

		SpawnMarkers = bb.get() == 1;
		TeamPlay = bb.get() == 1;
		NetNuke = bb.get() == 1;
		TimeLimit = (short) (bb.get() & 0xFF);
		KillLimit = (short) (bb.get() & 0xFF);
	}

	public byte[] getBytes() {
		buffer.clear();

		buffer.put((byte) nGameType);
		buffer.put((byte) nEpisode);
		buffer.put((byte) nLevel);
		buffer.put((byte) nDifficulty);
		buffer.put((byte) nMonsters);
		buffer.put((byte) nFriendlyFire);

		buffer.put(SpawnMarkers ? (byte) 1 : 0);
		buffer.put(TeamPlay ? (byte) 1 : 0);
		buffer.put(NetNuke ? (byte) 1 : 0);
		buffer.put((byte) TimeLimit);
		buffer.put((byte) KillLimit);

		return buffer.array();
	}

}
