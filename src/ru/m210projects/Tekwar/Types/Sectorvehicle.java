package ru.m210projects.Tekwar.Types;

import static ru.m210projects.Tekwar.Tektag.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;

public class Sectorvehicle {
	
	private static ByteBuffer buffer;
	public static final int sizeof = 2570;
	
	public short acceleration,accelto;
	public short speed,speedto,movespeed;                              
	public short angle,angleto;
	public int pivotx,pivoty;
	public short numpoints;
	public short[] point = new short[MAXVEHICLEPOINTS];
	public int[] pointx= new int[MAXVEHICLEPOINTS];
	public int[] pointy= new int[MAXVEHICLEPOINTS];
	public short track;
	public short tracknum;
	public int[] trackx = new int[MAXVEHICLETRACKS];
    public int[] tracky = new int[MAXVEHICLETRACKS];
    public char[] stop = new char[MAXVEHICLETRACKS];                                
    public int distx,disty;
    public short[] sector = new short[MAXVEHICLESECTORS];                            
    public short numsectors;                                           
    public int waittics,waitdelay;                                    
    public short stoptrack;
    public short[] killw=new short[4];
    public int	soundindex;  
    
    public void reset()
    {
    	acceleration = 0;
    	accelto = 0;
    	speed = 0;
    	speedto = 0;
    	movespeed = 0;
    	angle = 0;
    	angleto = 0;
    	pivotx = 0;
    	pivoty = 0;
    	numpoints = 0; 
    	for(int i = 0; i < MAXVEHICLEPOINTS; i++) 
    		point[i] = 0;
    	for(int i = 0; i < MAXVEHICLEPOINTS; i++) 
    		pointx[i] = 0;
    	for(int i = 0; i < MAXVEHICLEPOINTS; i++) 
    		pointy[i] = 0;
    	track = 0;
    	tracknum = 0;
    	for(int i = 0; i < MAXVEHICLETRACKS; i++) 
    		trackx[i] = 0;
    	for(int i = 0; i < MAXVEHICLETRACKS; i++) 
    		tracky[i] = 0;
    	for(int i = 0; i < MAXVEHICLETRACKS; i++) 
    		stop[i] = 0;
    	distx = 0;
    	disty = 0;
    	for(int i = 0; i < MAXVEHICLESECTORS; i++) 
    		sector[i] = 0;
    	numsectors = 0;
    	waittics = 0;
    	waitdelay = 0;
    	stoptrack = 0;
    	for(int i = 0; i < 4; i++) 
    		killw[i] = 0;
    	soundindex = -1;
    }
    
    public void load(Resource bb) {
    	acceleration = bb.readShort();
    	accelto = bb.readShort();
    	speed = bb.readShort();
    	speedto = bb.readShort();
    	movespeed = bb.readShort();
    	angle = bb.readShort();
    	angleto = bb.readShort();
    	pivotx = bb.readInt();
    	pivoty = bb.readInt();
    	numpoints = bb.readShort(); 
    	for(int i = 0; i < MAXVEHICLEPOINTS; i++) 
    		point[i] = bb.readShort();
    	for(int i = 0; i < MAXVEHICLEPOINTS; i++) 
    		pointx[i] = bb.readInt();
    	for(int i = 0; i < MAXVEHICLEPOINTS; i++) 
    		pointy[i] = bb.readInt();
    	track = bb.readShort();
    	tracknum = bb.readShort();
    	for(int i = 0; i < MAXVEHICLETRACKS; i++) 
    		trackx[i] = bb.readInt();
    	for(int i = 0; i < MAXVEHICLETRACKS; i++) 
    		tracky[i] = bb.readInt();
    	for(int i = 0; i < MAXVEHICLETRACKS; i++) 
    		stop[i] = (char) (bb.readByte()&0xFF);
    	distx = bb.readInt();
    	disty = bb.readInt();
    	for(int i = 0; i < MAXVEHICLESECTORS; i++) 
    		sector[i] = bb.readShort();
    	numsectors = bb.readShort();
    	waittics = bb.readInt();
    	waitdelay = bb.readInt();
    	stoptrack = bb.readShort();
    	for(int i = 0; i < 4; i++) 
    		killw[i] = bb.readShort();
    	soundindex = bb.readInt();
	}
    
    public byte[] getBytes()
	{
		if(buffer == null) {
			buffer = ByteBuffer.allocate(sizeof); 
			buffer.order( ByteOrder.LITTLE_ENDIAN);
		}
		buffer.clear();
		
		buffer.putShort(acceleration);
		buffer.putShort(accelto);
		buffer.putShort(speed);
		buffer.putShort(speedto);
		buffer.putShort(movespeed);
		buffer.putShort(angle);
		buffer.putShort(angleto);
		buffer.putInt(pivotx);
		buffer.putInt(pivoty);
		buffer.putShort(numpoints);
		for (int j = 0; j < MAXVEHICLEPOINTS; j++)
			buffer.putShort(point[j]);
		for (int j = 0; j < MAXVEHICLEPOINTS; j++)
			buffer.putInt(pointx[j]);
		for (int j = 0; j < MAXVEHICLEPOINTS; j++)
			buffer.putInt(pointy[j]);
		buffer.putShort(track);
		buffer.putShort(tracknum);
		for (int j = 0; j < MAXVEHICLETRACKS; j++)
			buffer.putInt(trackx[j]);
		for (int j = 0; j < MAXVEHICLETRACKS; j++)
			buffer.putInt(tracky[j]);
		for (int j = 0; j < MAXVEHICLETRACKS; j++)
			buffer.put((byte) stop[j]);
		buffer.putInt(distx);
		buffer.putInt(disty);
		for (int j = 0; j < MAXVEHICLESECTORS; j++)
			buffer.putShort(sector[j]);
		buffer.putShort(numsectors);
		buffer.putInt(waittics);
		buffer.putInt(waitdelay);
		buffer.putShort(stoptrack);
		for (int j = 0; j < 4; j++)
			buffer.putShort(killw[j]);
		buffer.putInt(soundindex);

		return buffer.array();
	}
}
