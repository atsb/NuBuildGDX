package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.MAXPLAYERS;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Tekwar.Types.Input;

public class Player {
	
	public final static int sizeof = 128;
	private static final byte[] buf = new byte[sizeof];
	
	public static Player[] gPlayer = new Player[MAXPLAYERS];

	//Player struct
	
	public Input pInput;
	public int posx, posy, posz, oposx, oposy, oposz;
	public float horiz, ang, ohoriz, oang;
	public short cursectnum, ocursectnum;
	public int hvel;

	// additional player variables
	
	public short playersprite, deaths, numbombs;
	public int health, score, saywatchit, invredcards, invbluecards, invaccutrak;
	public boolean ouse, ofire, dead, noclip, godMode;
	public int fireseq, weapons, lastchaingun, updategun, lastgun, firedonetics, drawweap;
	public short[] ammo = new short[8];
	public boolean autocenter;
	
	public Player()
	{
		pInput = new Input();
	}
	
	public byte[] getBytes() {
		
		int ptr = 0;
		LittleEndian.putInt(buf, ptr, posx); ptr+=4;
		LittleEndian.putInt(buf, ptr, posy); ptr+=4;
		LittleEndian.putInt(buf, ptr, posz); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposx); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposy); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, ang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, horiz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, oang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, ohoriz); ptr+=4;
		LittleEndian.putInt(buf, ptr, hvel); ptr+=4;
		LittleEndian.putShort(buf, ptr, cursectnum); ptr+=2;
		LittleEndian.putShort(buf, ptr, ocursectnum); ptr+=2;
		LittleEndian.putShort(buf, ptr, playersprite); ptr+=2;
		LittleEndian.putShort(buf, ptr, deaths); ptr+=2;
		LittleEndian.putShort(buf, ptr, numbombs); ptr+=2;

		LittleEndian.putInt(buf, ptr, health); ptr+=4;
		LittleEndian.putInt(buf, ptr, score); ptr+=4;
		LittleEndian.putInt(buf, ptr, saywatchit); ptr+=4;
		LittleEndian.putInt(buf, ptr, invredcards); ptr+=4;
		LittleEndian.putInt(buf, ptr, invbluecards); ptr+=4;
		LittleEndian.putInt(buf, ptr, invaccutrak); ptr+=4;
		
		for(int i = 0; i < 8; i++) {
			LittleEndian.putShort(buf, ptr, ammo[i]); ptr+=2;
		}
		
		buf[ptr++] = ouse ? (byte) 1: 0;
		buf[ptr++] = ofire ? (byte) 1: 0;
		buf[ptr++] = dead ? (byte) 1: 0;
		
		buf[ptr++] = autocenter ? (byte) 1: 0;
		buf[ptr++] = noclip ? (byte) 1: 0;
		buf[ptr++] = godMode ? (byte) 1: 0;
		
		LittleEndian.putInt(buf, ptr, fireseq); ptr+=4;
		LittleEndian.putInt(buf, ptr, weapons); ptr+=4;
		LittleEndian.putInt(buf, ptr, lastchaingun); ptr+=4;
		LittleEndian.putInt(buf, ptr, updategun); ptr+=4;
		LittleEndian.putInt(buf, ptr, lastgun); ptr+=4;
		LittleEndian.putInt(buf, ptr, firedonetics); ptr+=4;
		LittleEndian.putInt(buf, ptr, drawweap); ptr+=4;
		
		return buf;
	}
	
	public void copy(Player src)
	{
		posx = src.posx;
		posy = src.posy;
		posz = src.posz;
		oposx = src.oposx;
		oposy = src.oposy;
		oposz = src.oposz;
		ang = src.ang;
		horiz = src.horiz;
		oang = src.oang;
		ohoriz = src.ohoriz;
		hvel = src.hvel;   
		cursectnum = src.cursectnum;
		ocursectnum = src.ocursectnum;
		playersprite = src.playersprite;
		deaths = src.deaths;
		numbombs = src.numbombs;
		
		health = src.health;
		score = src.score;
		saywatchit = src.saywatchit;
		invredcards = src.invredcards;
		invbluecards = src.invbluecards;
		invaccutrak = src.invaccutrak;
		
		System.arraycopy(src.ammo, 0, ammo, 0, 8);
		
		ouse = src.ouse;
		ofire = src.ofire;
		dead = src.dead;
		
		autocenter = src.autocenter;
		noclip = src.noclip;
		godMode = src.godMode;
		
		fireseq = src.fireseq;
		weapons = src.weapons;
		lastchaingun = src.lastchaingun;
		updategun = src.updategun;
		lastgun = src.lastgun;
		firedonetics = src.firedonetics;
		drawweap = src.drawweap;
		
	}
	
	public void set(Resource bb)
	{
		posx = bb.readInt();
		posy = bb.readInt();
		posz = bb.readInt();
		oposx = bb.readInt();
		oposy = bb.readInt();
		oposz = bb.readInt();
		ang = bb.readFloat();
		horiz = bb.readFloat();
		oang = bb.readFloat();
		ohoriz = bb.readFloat();
		hvel = bb.readInt();   
		cursectnum = bb.readShort();
		ocursectnum = bb.readShort();
		playersprite = bb.readShort();
		deaths = bb.readShort();
		numbombs = bb.readShort();
		
		health = bb.readInt();
		score = bb.readInt();
		saywatchit = bb.readInt();
		invredcards = bb.readInt();
		invbluecards = bb.readInt();
		invaccutrak = bb.readInt();
		
		for(int i = 0; i < 8; i++) 
			ammo[i] = bb.readShort();

		ouse = bb.readBoolean();
		ofire = bb.readBoolean();
		dead = bb.readBoolean();
		
		autocenter = bb.readBoolean();
		noclip = bb.readBoolean();
		godMode = bb.readBoolean();
		
		fireseq = bb.readInt();
		weapons = bb.readInt();
		lastchaingun = bb.readInt();
		updategun = bb.readInt();
		lastgun = bb.readInt();
		firedonetics = bb.readInt();
		drawweap = bb.readInt();
	}
}
