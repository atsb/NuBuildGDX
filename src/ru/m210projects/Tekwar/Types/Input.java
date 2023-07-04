package ru.m210projects.Tekwar.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;

public class Input implements NetInput {
	
	public final static int sizeof = 17;
	
	public byte vel;
	public short svel;
	public float angvel;
	public int selectedgun;
	public float mlook;
	
	public boolean Jump;
	public boolean Crouch;
	public boolean Look_Up;
	public boolean Look_Down;
	public boolean Center;
	public boolean Holster_Weapon;
	public boolean Master;
	public boolean Run;
	public boolean Use;
	public boolean Fire;
	
	@Override
	public void Reset()
	{
		vel = 0;
		svel = 0;
		angvel = 0;
		selectedgun = -1;
		mlook = 0;
		
		Jump = false;
		Crouch = false;
		Look_Up = false;
		Look_Down = false;
		Center = false;
		Holster_Weapon = false;
		Master = false;
		Run = false;
		Use = false;
		Fire = false;
	}
	
	private static final ByteBuffer InputBuffer = ByteBuffer.allocate(sizeof).order(ByteOrder.LITTLE_ENDIAN); 
	public byte[] getBytes()
	{
		InputBuffer.clear();
		
		InputBuffer.putInt(selectedgun);
		InputBuffer.put(vel);
		InputBuffer.putShort(svel);
		InputBuffer.putFloat(angvel);
		
		short bits = 0;		
		bits |= Jump?1:0;
		bits |= Crouch?2:0; 
		bits |= Look_Up?4:0;
		bits |= Look_Down?8:0; 
		bits |= Center?64:0;
		bits |= Holster_Weapon?128:0;
		bits |= Run?256:0;
		bits |= Master?512:0;
		bits |= Use?1024:0; 
		bits |= Fire?2048:0;
		InputBuffer.putShort(bits);
		InputBuffer.putFloat(mlook);
	
		return InputBuffer.array();
	}
	
	@Override
	public NetInput Copy(NetInput src) {
		Input tekinput = (Input) src;
		
		this.vel = tekinput.vel;
		this.svel = tekinput.svel;
		this.angvel = tekinput.angvel;
		this.selectedgun = tekinput.selectedgun;
		this.mlook = tekinput.mlook;
		
		this.Jump = tekinput.Jump;
		this.Crouch = tekinput.Crouch;
		this.Look_Up = tekinput.Look_Up;
		this.Look_Down = tekinput.Look_Down;
		this.Center = tekinput.Center;
		this.Holster_Weapon = tekinput.Holster_Weapon;
		this.Master = tekinput.Master;
		this.Run = tekinput.Run;
		this.Use = tekinput.Use;
		this.Fire = tekinput.Fire;
		
		return this;
	}
	
	public void load(Resource bb)
	{
		this.selectedgun = bb.readInt();
		this.vel = bb.readByte();
		this.svel = bb.readShort();
		this.angvel = bb.readFloat();
		short bits = bb.readShort();
		Jump = (bits & 1) == 1;
		Crouch = ((bits & 2) >> 1) == 1;
		Look_Up = ((bits & 4) >> 2) == 1;
		Look_Down = ((bits & 8) >> 3) == 1;
		Center = ((bits & 64) >> 6) == 1;
		Holster_Weapon = ((bits & 128) >> 7) == 1;
		Run = ((bits & 256) >> 8) == 1;
		Master = ((bits & 512) >> 9) == 1;
		Use = ((bits & 1024) >> 10) == 1;
		Fire = ((bits & 2048) >> 11) == 1;
	
		this.mlook = bb.readFloat();
	}

	@Override
	public int GetInput(byte[] p, int offset, NetInput oldInput) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int PutInput(byte[] p, int offset, NetInput oldInput) {
		// TODO Auto-generated method stub
		return 0;
	}
}
