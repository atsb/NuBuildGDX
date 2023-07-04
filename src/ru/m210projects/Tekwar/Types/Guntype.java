package ru.m210projects.Tekwar.Types;

public class Guntype {
	public short pic;               // gun frame when carrying weapon
	public short firepic;           // 1st gun frame when firing weapon
	public short endfirepic;        // last gun frame when firing weapon
	public byte rep;                // 1=automatic weapon, 0=semi-automatic
	public byte[] action; // 8 frame action bytes - 1=shootgun()
	public byte pos;                // position on screen 0=center, 1=left, 2=right
	public short tics; 
	
	public Guntype(int pic, int firepic, int endfirepic, int rep, byte[] action, int pos, int tics) {
		this.pic = (short) pic;            
		this.firepic =  (short) firepic;
		this.endfirepic = (short) endfirepic;        
		this.rep = (byte) rep;            
		this.action = action;       
		this.pos = (byte) pos;                
		this.tics = (short) tics; 
	}
}
