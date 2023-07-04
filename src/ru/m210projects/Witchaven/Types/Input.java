
package ru.m210projects.Witchaven.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Pattern.BuildNet.NetInput;

public class Input implements NetInput {
	
	private static final int sizeof = 20;
	
	public float angvel;
	public float horiz;
	public int fvel, svel;
	public int bits;
	
	public Input(){}
	public Input(Object data)
	{
		setBytes(data);
	}

	public int setBytes(Object data, int... offs) {
		int offset = 0;
		if(offs.length == 1) offset = offs[0];
		
		ByteBuffer bb = null;
		if(data instanceof byte[]) {
			bb = ByteBuffer.wrap((byte[])data);
			bb.order(ByteOrder.LITTLE_ENDIAN); 
			bb.position(offset);
		} else if(data instanceof ByteBuffer)
			bb = (ByteBuffer) data;
		else return offset;

		angvel = bb.getFloat();
		horiz = bb.getFloat();

		fvel = bb.getInt();
		svel = bb.getInt();
		
		bits = bb.getInt();  

		return bb.position();
	}
	
	private ByteBuffer InputBuffer;
	public byte[] getBytes()
	{
		if(InputBuffer == null) {
			InputBuffer = ByteBuffer.allocate(sizeof); 
		} else InputBuffer.clear();
		InputBuffer.order(ByteOrder.LITTLE_ENDIAN); 

		InputBuffer.putFloat(angvel);
		InputBuffer.putFloat(horiz);

		InputBuffer.putInt(fvel);
		InputBuffer.putInt(svel);

		InputBuffer.putInt(bits);

		return InputBuffer.array();
	}
	
	@Override
	public void Reset() {
		this.fvel = 0;
		this.svel = 0;
		this.angvel = 0;
		this.bits = 0;
		this.horiz = 0;
	}
	@Override
	public NetInput Copy(NetInput src) {
		Input input = (Input) src;
		
		this.fvel = input.fvel;
		this.svel = input.svel;
		this.angvel = input.angvel;
		this.bits = input.bits;
		this.horiz = input.horiz;
		
		return this;
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
