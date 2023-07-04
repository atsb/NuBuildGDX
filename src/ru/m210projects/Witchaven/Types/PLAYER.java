package ru.m210projects.Witchaven.Types;

import static ru.m210projects.Witchaven.Weapons.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.LittleEndian;

import static ru.m210projects.Witchaven.Potions.*;
import static ru.m210projects.Witchaven.Globals.*;

public class PLAYER {
	
	public final static int sizeof = 574; //488;
	
	public Input pInput;
	public int x,y,z;
	public float ang;
    public float horiz, jumphoriz;
    public int height;
    public int hvel;               
    public short sector;
	public short oldsector;
	public short spritenum;
	public boolean keytoggle;
	public int flags;
	public int[] weapon = new int[MAXWEAPONS], preenchantedweapon = new int[MAXWEAPONS];
	public int[] ammo = new int[MAXWEAPONS], preenchantedammo = new int[MAXWEAPONS];
	public int[] orbammo = new int[MAXNUMORBS];
	public int[] treasure = new int[MAXTREASURES];
	public int[] orbactive = new int[MAXNUMORBS];
	public int[] orb = new int[MAXNUMORBS];
	public int[] potion = new int[MAXPOTIONS];
    public int lvl;
    public int score;
    public int health;
    public int maxhealth;
    public int armor;
    public int armortype;
    public int onsomething;
    public int fallz;
    
    public boolean dead;
    public short turnAround;
    
	public int shadowtime;
	public int helmettime;
	public int scoretime;
	public int vampiretime;
	
    public int selectedgun;
    public int currweapon;
	public int currweapontics;
	public int currweaponanim;
	public int currweaponframe;
	public int currweaponfired;
	public int currweaponattackstyle;
	public int currweaponflip;
	public int hasshot;
	
	public int currentpotion;
	public int strongtime, manatime, invisibletime=-1;
	
	public int orbshot;
	public int spellbooktics;
	public int spellbook;
	public int spellbookframe;
	public int spellbookflip;
	public int nightglowtime;

	public int showbook;
	public int showbooktype;
	public int showbookflip;
	public int showbookanim;
	public int currentorb;

	public int spelltime;
	
	public int shieldpoints;
	public int shieldtype;

	public int poisoned;
	public int poisontime;
	public int shockme = -1;
	public int invincibletime;
	
	public int spiked;
	public int spiketics;
	public int spikeframe;
	public int currspikeframe;
	public boolean godMode;
	public boolean noclip;
    
	public PLAYER()
	{
		pInput = new Input();
	}

    private byte[] buf = new byte[sizeof];
	public byte[] getBytes() {
		int ptr = 0;
		LittleEndian.putInt(buf, ptr, x); ptr+=4;
		LittleEndian.putInt(buf, ptr, y); ptr+=4;
		LittleEndian.putInt(buf, ptr, z); ptr+=4;
		LittleEndian.putFloat(buf, ptr, ang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, horiz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, jumphoriz); ptr+=4;
		LittleEndian.putInt(buf, ptr, height); ptr+=4;
		LittleEndian.putInt(buf, ptr, hvel); ptr+=4;
		LittleEndian.putShort(buf, ptr, sector); ptr+=2;
		LittleEndian.putShort(buf, ptr, oldsector); ptr+=2;
		LittleEndian.putShort(buf, ptr, spritenum); ptr+=2;
		buf[ptr++] = keytoggle?(byte)1:0;
		LittleEndian.putInt(buf, ptr, flags); ptr+=4;
		for(int i = 0; i < MAXWEAPONS; i++) {
			LittleEndian.putInt(buf, ptr, weapon[i]); ptr+=4;
			LittleEndian.putInt(buf, ptr, ammo[i]); ptr+=4;
			LittleEndian.putInt(buf, ptr, preenchantedweapon[i]); ptr+=4;
			LittleEndian.putInt(buf, ptr, preenchantedammo[i]); ptr+=4;
		}
		for(int i = 0; i < MAXNUMORBS; i++) {
			LittleEndian.putInt(buf, ptr, orbammo[i]); ptr+=4;
			LittleEndian.putInt(buf, ptr, orbactive[i]); ptr+=4;
			LittleEndian.putInt(buf, ptr, orb[i]); ptr+=4;
		}
		for(int i = 0; i < MAXTREASURES; i++) {
			LittleEndian.putInt(buf, ptr, treasure[i]); ptr+=4;
		}
		for(int i = 0; i < MAXPOTIONS; i++) {
			LittleEndian.putInt(buf, ptr, potion[i]); ptr+=4;
		}

		LittleEndian.putInt(buf, ptr, lvl); ptr+=4;
		LittleEndian.putInt(buf, ptr, score); ptr+=4;
		LittleEndian.putInt(buf, ptr, health); ptr+=4;
		LittleEndian.putInt(buf, ptr, maxhealth); ptr+=4;
		LittleEndian.putInt(buf, ptr, armor); ptr+=4;
		LittleEndian.putInt(buf, ptr, armortype); ptr+=4;
		LittleEndian.putInt(buf, ptr, onsomething); ptr+=4;
		LittleEndian.putInt(buf, ptr, fallz); ptr+=4;
	
		buf[ptr++] = (byte) (dead?1:0);
		LittleEndian.putShort(buf, ptr, turnAround); ptr+=2;
		
		LittleEndian.putInt(buf, ptr, shadowtime); ptr+=4;
		LittleEndian.putInt(buf, ptr, helmettime); ptr+=4;
		LittleEndian.putInt(buf, ptr, scoretime); ptr+=4;
		
		LittleEndian.putInt(buf, ptr, selectedgun); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweapon); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweapontics); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweaponanim); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweaponframe); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweaponfired); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweaponattackstyle); ptr+=4;
		LittleEndian.putInt(buf, ptr, currweaponflip); ptr+=4;
		LittleEndian.putInt(buf, ptr, hasshot); ptr+=4;
		
		LittleEndian.putInt(buf, ptr, currentpotion); ptr+=4;
		LittleEndian.putInt(buf, ptr, strongtime); ptr+=4;
		LittleEndian.putInt(buf, ptr, manatime); ptr+=4;
		LittleEndian.putInt(buf, ptr, invisibletime); ptr+=4;
		
		LittleEndian.putInt(buf, ptr, orbshot); ptr+=4;
		LittleEndian.putInt(buf, ptr, spellbooktics); ptr+=4;
		LittleEndian.putInt(buf, ptr, spellbook); ptr+=4;
		LittleEndian.putInt(buf, ptr, spellbookframe); ptr+=4;
		LittleEndian.putInt(buf, ptr, spellbookflip); ptr+=4;
		LittleEndian.putInt(buf, ptr, nightglowtime); ptr+=4;

		LittleEndian.putInt(buf, ptr, showbook); ptr+=4;
		LittleEndian.putInt(buf, ptr, showbooktype); ptr+=4;
		LittleEndian.putInt(buf, ptr, showbookflip); ptr+=4;
		LittleEndian.putInt(buf, ptr, showbookanim); ptr+=4;
		LittleEndian.putInt(buf, ptr, currentorb); ptr+=4;

		LittleEndian.putInt(buf, ptr, spelltime); ptr+=4;
		LittleEndian.putInt(buf, ptr, shieldtype); ptr+=4;

		LittleEndian.putInt(buf, ptr, shockme); ptr+=4;
		LittleEndian.putInt(buf, ptr, invincibletime); ptr+=4;
		
		LittleEndian.putInt(buf, ptr, spiketics); ptr+=4;
		LittleEndian.putInt(buf, ptr, spikeframe); ptr+=4;
		LittleEndian.putInt(buf, ptr, currspikeframe); ptr+=4;
		
		LittleEndian.putUShort(buf, ptr, shieldpoints); ptr+=2;
		if (vampiretime > 7200) 
			vampiretime = 0; /* Discard vampiretime if it's out of line */
		LittleEndian.putUShort(buf, ptr, vampiretime); ptr+=2;
		LittleEndian.putUShort(buf, ptr, poisoned); ptr+=2;
		LittleEndian.putUShort(buf, ptr, poisontime); ptr+=2;
		LittleEndian.putUShort(buf, ptr, spiked); ptr+=2;
		
		buf[ptr++] = (byte) (godMode?1:0);
		buf[ptr++] = (byte) (noclip?1:0);

		return buf;
	}
	
	public void copy(PLAYER src)
	{
		x = src.x;
		y = src.y;
		z = src.z;
		ang = src.ang;
		horiz = src.horiz;
		jumphoriz = src.jumphoriz;
		height = src.height;
		hvel = src.hvel;            
		sector = src.sector;
		oldsector = src.oldsector;
		spritenum = src.spritenum;
		keytoggle = src.keytoggle;
		flags = src.flags;
		System.arraycopy(src.weapon, 0, weapon, 0, MAXWEAPONS);
		System.arraycopy(src.ammo, 0, ammo, 0, MAXWEAPONS);
		System.arraycopy(src.preenchantedweapon, 0, preenchantedweapon, 0, MAXWEAPONS);
		System.arraycopy(src.preenchantedammo, 0, preenchantedammo, 0, MAXWEAPONS);
		System.arraycopy(src.orbammo, 0, orbammo, 0, MAXNUMORBS);
		System.arraycopy(src.orbactive, 0, orbactive, 0, MAXNUMORBS);
		System.arraycopy(src.orb, 0, orb, 0, MAXNUMORBS);
		System.arraycopy(src.treasure, 0, treasure, 0, MAXTREASURES);
		System.arraycopy(src.potion, 0, potion, 0, MAXPOTIONS);
		
		lvl = src.lvl;
		score = src.score;
		health = src.health;
		maxhealth = src.maxhealth;
		armor = src.armor;
		armortype = src.armortype;
		onsomething = src.onsomething;
		fallz = src.fallz;
		
		dead = src.dead;
		turnAround = src.turnAround;
		
		shadowtime = src.shadowtime;
		helmettime = src.helmettime;
		scoretime = src.scoretime;
		
	    selectedgun = src.selectedgun;
	    currweapon = src.currweapon;
		currweapontics = src.currweapontics;
		currweaponanim = src.currweaponanim;
		currweaponframe = src.currweaponframe;
		currweaponfired = src.currweaponfired;
		currweaponattackstyle = src.currweaponattackstyle;
		currweaponflip = src.currweaponflip;
		hasshot = src.hasshot;
		
		currentpotion = src.currentpotion;
		strongtime = src.strongtime;
		manatime = src.manatime;
		invisibletime = src.invisibletime;
		
		orbshot= src.orbshot;
		spellbooktics= src.spellbooktics;
		spellbook= src.spellbook;
		spellbookframe= src.spellbookframe;
		spellbookflip= src.spellbookflip;
		nightglowtime= src.nightglowtime;

		showbook= src.showbook;
		showbooktype= src.showbooktype;
		showbookflip= src.showbookflip;
		showbookanim= src.showbookanim;
		currentorb= src.currentorb;

		spelltime= src.spelltime;
		shieldtype= src.shieldtype;

		shockme = src.shockme;
		invincibletime= src.invincibletime;
		
		spiketics= src.spiketics;
		spikeframe= src.spikeframe;
		currspikeframe= src.currspikeframe;
		
		shieldpoints = src.shieldpoints;
		vampiretime = src.vampiretime;
		poisoned = src.poisoned;
		poisontime = src.poisontime;
		spiked = src.spiked;
		
		godMode = src.godMode;
		noclip = src.noclip;
	}
	
	public void set(Resource bb)
	{
		x = bb.readInt();
		y = bb.readInt();
		z = bb.readInt();
		ang = bb.readFloat();
		horiz = bb.readFloat();
		jumphoriz = bb.readFloat();
		height = bb.readInt();
		hvel = bb.readInt();             
		sector = bb.readShort();
		oldsector = bb.readShort();
		spritenum = bb.readShort();
		keytoggle = bb.readBoolean();
		flags = bb.readInt();
		for(int i = 0; i < MAXWEAPONS; i++) {
			weapon[i] = bb.readInt();
			ammo[i] = bb.readInt();
			preenchantedweapon[i] = bb.readInt();
			preenchantedammo[i] = bb.readInt();
		}
		for(int i = 0; i < MAXNUMORBS; i++) {
			orbammo[i] = bb.readInt();
			orbactive[i] = bb.readInt();
			orb[i] = bb.readInt();
		}
		for(int i = 0; i < MAXTREASURES; i++) 
			treasure[i] = bb.readInt();
		for(int i = 0; i < MAXPOTIONS; i++) 
			potion[i] = bb.readInt();	

		lvl = bb.readInt();
		score = bb.readInt();
		health = bb.readInt();
		maxhealth = bb.readInt();
		armor = bb.readInt();
		armortype = bb.readInt();
		onsomething = bb.readInt();
		fallz = bb.readInt();
		
		dead = bb.readBoolean();
		turnAround = bb.readShort();

		shadowtime = bb.readInt();
		helmettime = bb.readInt();
		scoretime = bb.readInt();
		
	    selectedgun = bb.readInt();
	    currweapon = bb.readInt();
		currweapontics = bb.readInt();
		currweaponanim = bb.readInt();
		currweaponframe = bb.readInt();
		currweaponfired = bb.readInt();
		currweaponattackstyle = bb.readInt();
		currweaponflip = bb.readInt();
		hasshot = bb.readInt();
		
		currentpotion = bb.readInt();
		strongtime = bb.readInt();
		manatime = bb.readInt();
		invisibletime = bb.readInt();
		
		orbshot= bb.readInt();
		spellbooktics= bb.readInt();
		spellbook= bb.readInt();
		spellbookframe= bb.readInt();
		spellbookflip= bb.readInt();
		nightglowtime= bb.readInt();

		showbook= bb.readInt();
		showbooktype= bb.readInt();
		showbookflip= bb.readInt();
		showbookanim= bb.readInt();
		currentorb= bb.readInt();

		spelltime= bb.readInt();
		shieldtype= bb.readInt();

		shockme = bb.readInt();
		invincibletime= bb.readInt();
		
		spiketics= bb.readInt();
		spikeframe= bb.readInt();
		currspikeframe= bb.readInt();
		
		shieldpoints = bb.readShort();
		vampiretime = bb.readShort();
		if (vampiretime > 7200) 
			vampiretime = 0; /* Discard vampiretime if it's out of line */
		poisoned = bb.readShort();
		poisontime = bb.readShort();
		spiked = bb.readShort();
		godMode = bb.readBoolean();
		noclip = bb.readBoolean();
	}
}
