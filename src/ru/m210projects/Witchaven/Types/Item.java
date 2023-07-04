package ru.m210projects.Witchaven.Types;

public class Item {

	public interface Callback {
		public void pickup(PLAYER plr, short i);
	}
	
	public final int sizx, sizy;
	public final boolean treasures, cflag;
	private final Callback callback;
	
	public Item(int sizx, int sizy, boolean treasure, boolean cflag, Callback call)
	{
		this.sizx = sizx;
		this.sizy = sizy;
		this.treasures = treasure;
		this.cflag = cflag;
		this.callback = call;
	}
	
	public void pickup(PLAYER plr, short i) {
		callback.pickup(plr, i);
	}
	
}
