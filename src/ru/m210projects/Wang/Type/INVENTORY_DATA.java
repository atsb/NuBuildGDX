package ru.m210projects.Wang.Type;

import ru.m210projects.Wang.Inv.Inventory_Stop_Func;
import ru.m210projects.Wang.Player.Player_Action_Func;

public class INVENTORY_DATA {
	public String Name;
	public Player_Action_Func init;
	public Inventory_Stop_Func stop;
	public Panel_State State;
	public short DecPerSec;
	public short MaxInv;
	public int  Scale;
	public short Flags;
	
	public INVENTORY_DATA(String name, Player_Action_Func init, Inventory_Stop_Func stop, Panel_State[] State, int DecPerSec, int MaxInv, int  Scale, int Flags)
	{
		this.Name = name;
		this.init = init;
		this.stop = stop;
		this.State = State[0];
		this.DecPerSec = (short)DecPerSec;
		this.MaxInv = (short)MaxInv;
		this.Scale = Scale;
		this.Flags = (short)Flags;
	}
}
