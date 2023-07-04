package ru.m210projects.Wang.Type;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Game.isOriginal;
import static ru.m210projects.Wang.Gameutils.MAX_SW_PLAYERS;
import static ru.m210projects.Wang.Gameutils.MAX_WEAPONS;
import static ru.m210projects.Wang.Gameutils.NUM_KEYS;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Inv.MAX_INVENTORY;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Player.Player_Action_Func;

public class PlayerStr {

	public final static int sizeof = 1430; //+4 lookang

	// variable that fit in the sprite or user structure
	public int posx, posy, posz;
	// interpolation
	public int oposx, oposy, oposz;
	public int obob_z, obob_amt;
	public float oang, ohoriz;
	
	// holds last valid move position
	public short lv_sectnum;
	public int lv_x, lv_y, lv_z;

	public int remote_sprite;
	public Remote_Control remote;
	public int sop_remote;
	public int sop; // will either be sop_remote or sop_control

	public int jump_count, jump_speed; // jumping
	public short down_speed, up_speed; // diving
	public int z_speed, oz_speed; // used for diving and flying instead of down_speed, up_speed
	public int climb_ndx;
	public int hiz, loz;
	public int ceiling_dist, floor_dist;
	public int hi_sectp = -1, lo_sectp = -1; // sector
	public int hi_sp = -1, lo_sp = -1; // sprite

	public int last_camera_sp;
	public int camera_dist; // view mode dist
	public int circle_camera_dist;
	public int six, siy, siz; // save player interp position for PlayerSprite
	public short siang;

	public int xvect, yvect;
	public int oxvect, oyvect;
	public int friction;
	public int slide_xvect, slide_yvect;
	public short slide_ang;
	public int slide_dec;
	public float drive_angvel;
	public float drive_oangvel;

	// scroll 2D mode stuff
	public int scr_x, scr_y, oscr_x, oscr_y;
	public int scr_xvect, scr_yvect;
	public short scr_ang, oscr_ang, scr_sectnum;

	public short view_outside_dang; // outside view delta ang
	public short circle_camera_ang;
	public short camera_check_time_delay;

	public short cursectnum, lastcursectnum;
	public short turn180_target; // 180 degree turn
	public float pang, horiz, horizbase, horizoff;

	// variables that do not fit into sprite structure
	public int hvel, tilt, tilt_dest;
	public short recoil_amt;
	public short recoil_speed;
	public short recoil_ndx;
	public short recoil_horizoff;

	public int oldposx, oldposy, oldposz;
	public int RevolveX, RevolveY;
	public short RevolveDeltaAng, RevolveAng;

	// under vars are for wading and swimming
	public short PlayerSprite, PlayerUnderSprite;

	public short pnum; // carry aint the player number

	public short LadderSector, LadderAngle;
	public int lx, ly; // ladder x and y
	public short JumpDuration;
	public short WadeDepth;
	public short bob_amt;
	public short bob_ndx;
	public short bcnt; // bob count
	public int bob_z;

	// Multiplayer variables
	public Input input = new Input();

	// must start out as 0
	public int playerreadyflag;

	public Player_Action_Func DoPlayerAction;
	public int Flags, Flags2;
	public int KeyPressFlags;

	public int sop_control;
	public int sop_riding;

	public byte[] HasKey = new byte[NUM_KEYS];

	// Weapon stuff
	public short SwordAng;
	public int WpnGotOnceFlags; // for no respawn mode where weapons are allowed grabbed only once
	public int WpnFlags;
	public short WpnAmmo[] = new short[MAX_WEAPONS];
	public Panel_Sprite PanelSpriteList = new Panel_Sprite().InitList();
	public Panel_Sprite CurWpn;
	public Panel_Sprite[] Wpn = new Panel_Sprite[MAX_WEAPONS];
	public Panel_Sprite Chops;
	public byte WpnRocketType; // rocket type
	public byte WpnRocketHeat; // 5 to 0 range
	public byte WpnRocketNuke; // 1, you have it, or you don't
	public byte WpnFlameType; // Guardian weapons fire
	public byte WpnFirstType; // First weapon type - Sword/Shuriken
	public byte WeaponType; // for weapons with secondary functions
	public short FirePause; // for sector objects - limits rapid firing
	//
	// Inventory Vars
	//
	public short InventoryNum;
	public short InventoryBarTics;

	public Panel_Sprite[] InventorySprite = new Panel_Sprite[MAX_INVENTORY];
	public Panel_Sprite InventorySelectionBox;

	public short InventoryTics[] = new short[MAX_INVENTORY];
	public short InventoryPercent[] = new short[MAX_INVENTORY];
	public short InventoryAmount[] = new short[MAX_INVENTORY];
	public boolean InventoryActive[] = new boolean[MAX_INVENTORY];

	public short DiveTics;
	public short DiveDamageTics;

	// Death stuff
	public short DeathType;
	public short Kills;
	public short Killer; // who killed me
	public short KilledPlayer[] = new short[MAX_SW_PLAYERS];
	public short SecretsFound;

	// Health
	public short Armor;
	public short MaxHealth;

	public String PlayerName; // 32 characters

	public boolean UziShellLeftAlt;
	public boolean UziShellRightAlt;
	public byte TeamColor; // used in team play and also used in regular mulit-play for show

	// palette fading up and down for player hit and get items
	public short FadeTics; // Tics between each fade cycle
	public short FadeAmt; // Current intensity of fade
	public boolean NightVision; // Is player's night vision active?
	public byte StartColor; // Darkest color in color range being used
	public byte[] temp_pal = new byte[768]; // temporary working palette
	public short fta, ftq; // First time active and first time quote, for talking in multiplayer games
	public short NumFootPrints; // Number of foot prints left to lay down
	public boolean PlayerTalking; // Is player currently talking
	public int TalkVocnum; // Number of sound that player is using
	public VOC3D TalkVocHandle; // Handle of sound in sound queue, to access in Dose's code
	public byte WpnUziType; // Toggle between single or double uzi's if you own 2.
	public byte WpnShotgunType; // Shotgun has normal or fully automatic fire
	public byte WpnShotgunAuto; // 50-0 automatic shotgun rounds
	public byte WpnShotgunLastShell; // Number of last shell fired
	public byte WpnRailType; // Normal Rail Gun or EMP Burst Mode
	public boolean Bloody; // Is player gooey from the slaughter?
	public VOC3D nukevochandle; // Stuff for the Nuke
	public boolean InitingNuke;
	public boolean TestNukeInit;
	public boolean NukeInitialized; // Nuke already has counted down
	public short FistAng; // KungFu attack angle
	public byte WpnKungFuMove; // KungFu special moves
	public boolean BunnyMode; // Can shoot Bunnies out of rocket launcher
	public short HitBy; // SpriteNum of whatever player was last hit by
	public short Reverb; // Player's current reverb setting
	public short Heads; // Number of Accursed Heads orbiting player
	
	public byte last_used_weapon; //GDX last choosed weapon 
	public int lookang;

	public SPRITE getSprite() {
		return PlayerSprite != -1 ? sprite[PlayerSprite] : null;
	}

	public SPRITE getUnderSprite() {
		return PlayerUnderSprite != -1 ? sprite[PlayerUnderSprite] : null;
	}
	
	public int getWeapon()
	{
		return (PlayerSprite != -1 && pUser[PlayerSprite] != null) ? pUser[PlayerSprite].WeaponNum : 0;
	}

	public String getName()
	{
		return PlayerName == null || PlayerName.isEmpty() ? "Player" + (pnum + 1) : PlayerName;
	}
	
	public short getAnglei()
	{
		return (short) ((int)pang & 0x7FF);
	}
	
	public float getAnglef()
	{
		if(isOriginal())
			return getAnglei();
		return pang;
	}
	
	public float getHorizf()
	{
		if(isOriginal())
			return getHorizi();
		return horiz;
	}
	
	public short getHorizi()
	{
		return (short) horiz;
	}
	
	public void reset() {
		this.posx = 0;
		this.posy = 0;
		this.posz = 0;
		this.oposx = 0;
		this.oposy = 0;
		this.oposz = 0;
		this.oang = 0;
		this.ohoriz = 0;
		this.obob_z = 0;
		this.obob_amt = 0;
		this.lv_sectnum = 0;
		this.lv_x = 0;
		this.lv_y = 0;
		this.lv_z = 0;
		this.remote_sprite = -1;
		this.remote = null;
		this.sop_remote = -1;
		this.sop = -1;
		this.jump_count = 0;
		this.jump_speed = 0;
		this.down_speed = 0;
		this.up_speed = 0;
		this.z_speed = 0;
		this.oz_speed = 0;
		this.climb_ndx = 0;
		this.hiz = 0;
		this.loz = 0;
		this.ceiling_dist = 0;
		this.floor_dist = 0;
		this.hi_sectp = -1;
		this.lo_sectp = -1;
		this.hi_sp = -1;
		this.lo_sp = -1;
		this.last_camera_sp = -1;
		this.camera_dist = 0;
		this.circle_camera_dist = 0;
		this.six = 0;
		this.siy = 0;
		this.siz = 0;
		this.siang = 0;
		this.xvect = 0;
		this.yvect = 0;
		this.oxvect = 0;
		this.oyvect = 0;
		this.friction = 0;
		this.slide_xvect = 0;
		this.slide_yvect = 0;
		this.slide_ang = 0;
		this.slide_dec = 0;
		this.drive_angvel = 0;
		this.drive_oangvel = 0;
		this.scr_x = 0;
		this.scr_y = 0;
		this.oscr_x = 0;
		this.oscr_y = 0;
		this.scr_xvect = 0;
		this.scr_yvect = 0;
		this.scr_ang = 0;
		this.oscr_ang = 0;
		this.scr_sectnum = 0;
		this.view_outside_dang = 0;
		this.circle_camera_ang = 0;
		this.camera_check_time_delay = 0;
		this.pang = 0;
		this.cursectnum = 0;
		this.lastcursectnum = 0;
		this.turn180_target = 0;
		this.horizbase = 0;
		this.horiz = 0;
		this.horizoff = 0;
		this.hvel = 0;
		this.tilt = 0;
		this.tilt_dest = 0;
		this.recoil_amt = 0;
		this.recoil_speed = 0;
		this.recoil_ndx = 0;
		this.recoil_horizoff = 0;
		this.oldposx = 0;
		this.oldposy = 0;
		this.oldposz = 0;
		this.RevolveX = 0;
		this.RevolveY = 0;
		this.RevolveDeltaAng = 0;
		this.RevolveAng = 0;
		this.PlayerSprite = -1;
		this.PlayerUnderSprite = -1;
		this.pnum = 0;
		this.LadderSector = 0;
		this.LadderAngle = 0;
		this.lx = 0;
		this.ly = 0;
		this.JumpDuration = 0;
		this.WadeDepth = 0;
		this.bob_amt = 0;
		this.bob_ndx = 0;
		this.bcnt = 0;
		this.bob_z = 0;
		this.input.Reset();
		this.playerreadyflag = 0;
		this.DoPlayerAction = null;
		this.Flags = 0;
		this.Flags2 = 0;
		this.KeyPressFlags = 0;
		this.sop_control = -1;
		this.sop_riding = -1;
		List.Init(PanelSpriteList);
		Arrays.fill(HasKey, (byte) 0);
		this.SwordAng = 0;
		this.WpnGotOnceFlags = 0;
		this.WpnFlags = 0;
		Arrays.fill(WpnAmmo, (short) 0);
		this.CurWpn = null;
		Arrays.fill(Wpn, null);
		this.Chops = null;
		this.WpnRocketType = 0;
		this.WpnRocketHeat = 0;
		this.WpnRocketNuke = 0;
		this.WpnFlameType = 0;
		this.WpnFirstType = 0;
		this.WeaponType = 0;
		this.FirePause = 0;
		this.InventoryNum = 0;
		this.InventoryBarTics = 0;
		Arrays.fill(InventorySprite, null);
		this.InventorySelectionBox = null;
		Arrays.fill(InventoryTics, (short) 0);
		Arrays.fill(InventoryPercent, (short) 0);
		Arrays.fill(InventoryAmount, (short) 0);
		Arrays.fill(InventoryActive, (boolean) false);
		this.DiveTics = 0;
		this.DiveDamageTics = 0;
		this.DeathType = 0;
		this.Kills = 0;
		this.Killer = 0;
		Arrays.fill(KilledPlayer, (short) 0);
		this.SecretsFound = 0;
		this.Armor = 0;
		this.MaxHealth = 0;
		this.PlayerName = null;
		this.UziShellLeftAlt = false;
		this.UziShellRightAlt = false;
		this.TeamColor = 0;
		this.FadeTics = 0;
		this.FadeAmt = 0;
		this.NightVision = false;
		this.StartColor = 0;
		Arrays.fill(temp_pal, (byte) 0);
		this.fta = 0;
		this.ftq = 0;
		this.NumFootPrints = 0;
		this.PlayerTalking = false;
		this.TalkVocnum = -1;
		this.TalkVocHandle = null;
		this.WpnUziType = 0;
		this.WpnShotgunType = 0;
		this.WpnShotgunAuto = 0;
		this.WpnShotgunLastShell = 0;
		this.WpnRailType = 0;
		this.Bloody = false;
		this.nukevochandle = null;
		this.InitingNuke = false;
		this.TestNukeInit = false;
		this.NukeInitialized = false;
		this.FistAng = 0;
		this.WpnKungFuMove = 0;
		this.BunnyMode = false;
		this.HitBy = 0;
		this.Reverb = 0;
		this.Heads = 0;
		
		this.last_used_weapon = 0;
		this.lookang = 0;
	}

	private static final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order(ByteOrder.LITTLE_ENDIAN);

	public byte[] getBytes() {
		buffer.clear();

		buffer.putInt(posx);
		buffer.putInt(posy);
		buffer.putInt(posz);
		buffer.putInt(oposx);
		buffer.putInt(oposy);
		buffer.putInt(oposz);
		buffer.putFloat(oang);
		buffer.putFloat(ohoriz);
		buffer.putInt(obob_z);
		buffer.putInt(obob_amt);
		buffer.putShort(lv_sectnum);
		buffer.putInt(lv_x);
		buffer.putInt(lv_y);
		buffer.putInt(lv_z);
		buffer.putInt(remote_sprite);

		if (remote == null)
			remote = new Remote_Control();

		buffer.putShort(remote.cursectnum);
		buffer.putShort(remote.lastcursectnum);
		buffer.putShort(remote.pang);

		buffer.putInt(remote.xvect);
		buffer.putInt(remote.yvect);
		buffer.putInt(remote.oxvect);
		buffer.putInt(remote.oyvect);
		buffer.putInt(remote.slide_xvect);
		buffer.putInt(remote.slide_yvect);

		buffer.putInt(remote.posx);
		buffer.putInt(remote.posy);
		buffer.putInt(remote.posz);

		buffer.putInt(remote.oposx);
		buffer.putInt(remote.oposy);
		buffer.putInt(remote.oposz);

		buffer.putInt(sop_remote);
		buffer.putInt(sop);
		buffer.putInt(jump_count);
		buffer.putInt(jump_speed);
		buffer.putShort(down_speed);
		buffer.putShort(up_speed);
		buffer.putInt(z_speed);
		buffer.putInt(oz_speed);
		buffer.putInt(climb_ndx);
		buffer.putInt(hiz);
		buffer.putInt(loz);
		buffer.putInt(ceiling_dist);
		buffer.putInt(floor_dist);
		buffer.putInt(hi_sectp);
		buffer.putInt(lo_sectp);
		buffer.putInt(hi_sp);
		buffer.putInt(lo_sp);
		buffer.putInt(last_camera_sp);
		buffer.putInt(camera_dist);
		buffer.putInt(circle_camera_dist);
		buffer.putInt(xvect);
		buffer.putInt(yvect);
		buffer.putInt(oxvect);
		buffer.putInt(oyvect);
		buffer.putInt(friction);
		buffer.putInt(slide_xvect);
		buffer.putInt(slide_yvect);
		buffer.putShort(slide_ang);
		buffer.putInt(slide_dec);
		buffer.putFloat(drive_angvel);
		buffer.putFloat(drive_oangvel);
		buffer.putInt(scr_x);
		buffer.putInt(scr_y);
		buffer.putInt(oscr_x);
		buffer.putInt(oscr_y);
		buffer.putInt(scr_xvect);
		buffer.putInt(scr_yvect);
		buffer.putShort(scr_ang);
		buffer.putShort(oscr_ang);
		buffer.putShort(scr_sectnum);
		buffer.putShort(view_outside_dang);
		buffer.putShort(circle_camera_ang);
		buffer.putShort(camera_check_time_delay);
		buffer.putFloat(pang);
		buffer.putShort(cursectnum);
		buffer.putShort(lastcursectnum);
		buffer.putShort(turn180_target);
		buffer.putFloat(horizbase);
		buffer.putFloat(horiz);
		buffer.putFloat(horizoff);
		buffer.putInt(hvel);
		buffer.putInt(tilt);
		buffer.putInt(tilt_dest);
		buffer.putShort(recoil_amt);
		buffer.putShort(recoil_speed);
		buffer.putShort(recoil_ndx);
		buffer.putShort(recoil_horizoff);
		buffer.putInt(oldposx);
		buffer.putInt(oldposy);
		buffer.putInt(oldposz);
		buffer.putInt(RevolveX);
		buffer.putInt(RevolveY);
		buffer.putShort(RevolveDeltaAng);
		buffer.putShort(RevolveAng);
		buffer.putShort(PlayerSprite);
		buffer.putShort(PlayerUnderSprite);

		buffer.putShort(pnum);
		buffer.putShort(LadderSector);
		buffer.putShort(LadderAngle);
		buffer.putInt(lx);
		buffer.putInt(ly);
		buffer.putShort(JumpDuration);
		buffer.putShort(WadeDepth);
		buffer.putShort(bob_amt);
		buffer.putShort(bob_ndx);
		buffer.putShort(bcnt);
		buffer.putInt(bob_z);
		buffer.putInt(playerreadyflag);
		buffer.putInt(DoPlayerAction != null ? DoPlayerAction.ordinal() : -1);
		buffer.putInt(Flags);
		buffer.putInt(Flags2);
		buffer.putInt(KeyPressFlags);
		buffer.putInt(sop_control);
		buffer.putInt(sop_riding);

		buffer.put(HasKey);
		buffer.putShort(SwordAng);
		buffer.putInt(WpnGotOnceFlags);
		buffer.putInt(WpnFlags);

		for (int i = 0; i < MAX_WEAPONS; i++)
			buffer.putShort(WpnAmmo[i]);

		buffer.putInt(List.PanelSpriteToNdx(PanelSpriteList, CurWpn));
		for (int ndx = 0; ndx < MAX_WEAPONS; ndx++)
			buffer.putInt(List.PanelSpriteToNdx(PanelSpriteList, Wpn[ndx]));
		buffer.putInt(List.PanelSpriteToNdx(PanelSpriteList, Chops));

		buffer.put(WpnRocketType);
		buffer.put(WpnRocketHeat);
		buffer.put(WpnRocketNuke);
		buffer.put(WpnFlameType);
		buffer.put(WpnFirstType);
		buffer.put(WeaponType);
		buffer.putShort(FirePause);
		buffer.putShort(InventoryNum);
		buffer.putShort(InventoryBarTics);

		for (int ndx = 0; ndx < MAX_INVENTORY; ndx++)
			buffer.putInt(List.PanelSpriteToNdx(PanelSpriteList, InventorySprite[ndx]));
		buffer.putInt(List.PanelSpriteToNdx(PanelSpriteList, InventorySelectionBox));

		for (int i = 0; i < MAX_INVENTORY; i++) {
			buffer.putShort(InventoryTics[i]);
			buffer.putShort(InventoryPercent[i]);
			buffer.putShort(InventoryAmount[i]);
			buffer.put(InventoryActive[i] ? (byte) 1 : 0);
		}
		buffer.putShort(DiveTics);
		buffer.putShort(DiveDamageTics);
		buffer.putShort(DeathType);
		buffer.putShort(Kills);
		buffer.putShort(Killer);
		for (int i = 0; i < MAX_SW_PLAYERS; i++)
			buffer.putShort(KilledPlayer[i]);

		buffer.putShort(SecretsFound);
		buffer.putShort(Armor);
		buffer.putShort(MaxHealth);

		buffer.put(UziShellLeftAlt ? (byte) 1 : 0);
		buffer.put(UziShellRightAlt ? (byte) 1 : 0);
		buffer.put(TeamColor);
		buffer.putShort(FadeTics);
		buffer.putShort(FadeAmt);
		buffer.put(NightVision ? (byte) 1 : 0);
		buffer.put(StartColor);
		buffer.put(temp_pal);
		buffer.putShort(fta);
		buffer.putShort(ftq);
		buffer.putShort(NumFootPrints);
//		buffer.put(PlayerTalking ? (byte) 1 : 0);
//		buffer.putInt(TalkVocnum);

		buffer.put(WpnUziType);
		buffer.put(WpnShotgunType);
		buffer.put(WpnShotgunAuto);
		buffer.put(WpnShotgunLastShell);
		buffer.put(WpnRailType);
		buffer.put(Bloody ? (byte) 1 : 0);

		buffer.put(InitingNuke ? (byte) 1 : 0);
		buffer.put(TestNukeInit ? (byte) 1 : 0);
		buffer.put(NukeInitialized ? (byte) 1 : 0);
		buffer.putShort(FistAng);
		buffer.put(WpnKungFuMove);
		buffer.put(BunnyMode ? (byte) 1 : 0);
		buffer.putShort(HitBy);
		buffer.putShort(Reverb);
		buffer.putShort(Heads);
		buffer.put(last_used_weapon);
		
		return buffer.array();
	}

	public void load(Resource res) {
		posx = res.readInt();
		posy = res.readInt();
		posz = res.readInt();
		oposx = res.readInt();
		oposy = res.readInt();
		oposz = res.readInt();
		oang = res.readFloat();
		ohoriz = res.readInt();
		obob_z = res.readInt();
		obob_amt = res.readInt();
		lv_sectnum = res.readShort();
		lv_x = res.readInt();
		lv_y = res.readInt();
		lv_z = res.readInt();
		remote_sprite = res.readInt();

		remote = new Remote_Control();
		remote.cursectnum = res.readShort();
		remote.lastcursectnum = res.readShort();
		remote.pang = res.readShort();

		remote.xvect = res.readInt();
		remote.yvect = res.readInt();
		remote.oxvect = res.readInt();
		remote.oyvect = res.readInt();
		remote.slide_xvect = res.readInt();
		remote.slide_yvect = res.readInt();

		remote.posx = res.readInt();
		remote.posy = res.readInt();
		remote.posz = res.readInt();

		remote.oposx = res.readInt();
		remote.oposy = res.readInt();
		remote.oposz = res.readInt();

		sop_remote = res.readInt();
		sop = res.readInt();
		jump_count = res.readInt();
		jump_speed = res.readInt();
		down_speed = res.readShort();
		up_speed = res.readShort();
		z_speed = res.readInt();
		oz_speed = res.readInt();
		climb_ndx = res.readInt();
		hiz = res.readInt();
		loz = res.readInt();
		ceiling_dist = res.readInt();
		floor_dist = res.readInt();
		hi_sectp = res.readInt();
		lo_sectp = res.readInt();
		hi_sp = res.readInt();
		lo_sp = res.readInt();
		last_camera_sp = res.readInt();
		camera_dist = res.readInt();
		circle_camera_dist = res.readInt();
		xvect = res.readInt();
		yvect = res.readInt();
		oxvect = res.readInt();
		oyvect = res.readInt();
		friction = res.readInt();
		slide_xvect = res.readInt();
		slide_yvect = res.readInt();
		slide_ang = res.readShort();
		slide_dec = res.readInt();
		drive_angvel = res.readFloat();
		drive_oangvel = res.readFloat();
		scr_x = res.readInt();
		scr_y = res.readInt();
		oscr_x = res.readInt();
		oscr_y = res.readInt();
		scr_xvect = res.readInt();
		scr_yvect = res.readInt();
		scr_ang = res.readShort();
		oscr_ang = res.readShort();
		scr_sectnum = res.readShort();
		view_outside_dang = res.readShort();
		circle_camera_ang = res.readShort();
		camera_check_time_delay = res.readShort();
		pang = res.readFloat();
		cursectnum = res.readShort();
		lastcursectnum = res.readShort();
		turn180_target = res.readShort();
		horizbase = res.readFloat();
		horiz = res.readFloat();
		horizoff = res.readFloat();
		hvel = res.readInt();
		tilt = res.readInt();
		tilt_dest = res.readInt();
		recoil_amt = res.readShort();
		recoil_speed = res.readShort();
		recoil_ndx = res.readShort();
		recoil_horizoff = res.readShort();
		oldposx = res.readInt();
		oldposy = res.readInt();
		oldposz = res.readInt();
		RevolveX = res.readInt();
		RevolveY = res.readInt();
		RevolveDeltaAng = res.readShort();
		RevolveAng = res.readShort();
		PlayerSprite = res.readShort();
		PlayerUnderSprite = res.readShort();
		pnum = res.readShort();
		LadderSector = res.readShort();
		LadderAngle = res.readShort();
		lx = res.readInt();
		ly = res.readInt();
		JumpDuration = res.readShort();
		WadeDepth = res.readShort();
		bob_amt = res.readShort();
		bob_ndx = res.readShort();
		bcnt = res.readShort();
		bob_z = res.readInt();
		playerreadyflag = res.readInt();
		int func = res.readInt();
		DoPlayerAction = func != -1 ? Player_Action_Func.values()[func] : null;
		Flags = res.readInt();
		Flags2 = res.readInt();
		KeyPressFlags = res.readInt();
		sop_control = res.readInt();
		sop_riding = res.readInt();

		res.read(HasKey);
		SwordAng = res.readShort();
		WpnGotOnceFlags = res.readInt();
		WpnFlags = res.readInt();

		for (int i = 0; i < MAX_WEAPONS; i++)
			WpnAmmo[i] = res.readShort();

		CurWpn = List.PanelNdxToSprite(PanelSpriteList, res.readInt());
		for (int ndx = 0; ndx < MAX_WEAPONS; ndx++)
			Wpn[ndx] = List.PanelNdxToSprite(PanelSpriteList, res.readInt());
		Chops = List.PanelNdxToSprite(PanelSpriteList, res.readInt());

		WpnRocketType = res.readByte();
		WpnRocketHeat = res.readByte();
		WpnRocketNuke = res.readByte();
		WpnFlameType = res.readByte();
		WpnFirstType = res.readByte();
		WeaponType = res.readByte();
		FirePause = res.readShort();
		InventoryNum = res.readShort();
		InventoryBarTics = res.readShort();

		for (int ndx = 0; ndx < MAX_INVENTORY; ndx++)
			InventorySprite[ndx] = List.PanelNdxToSprite(PanelSpriteList, res.readInt());
		InventorySelectionBox = List.PanelNdxToSprite(PanelSpriteList, res.readInt());

		for (int i = 0; i < MAX_INVENTORY; i++) {
			InventoryTics[i] = res.readShort();
			InventoryPercent[i] = res.readShort();
			InventoryAmount[i] = res.readShort();
			InventoryActive[i] = res.readBoolean();
		}

		DiveTics = res.readShort();
		DiveDamageTics = res.readShort();
		DeathType = res.readShort();
		Kills = res.readShort();
		Killer = res.readShort();
		for (int i = 0; i < MAX_SW_PLAYERS; i++)
			KilledPlayer[i] = res.readShort();

		SecretsFound = res.readShort();
		Armor = res.readShort();
		MaxHealth = res.readShort();
		UziShellLeftAlt = res.readBoolean();
		UziShellRightAlt = res.readBoolean();
		TeamColor = res.readByte();
		FadeTics = res.readShort();
		FadeAmt = res.readShort();
		NightVision = res.readBoolean();

		StartColor = res.readByte();
		res.read(temp_pal);
		fta = res.readShort();
		ftq = res.readShort();
		NumFootPrints = res.readShort();

		PlayerTalking = false;
		TalkVocnum = -1;
		nukevochandle = null;
		TalkVocHandle = null;

		WpnUziType = res.readByte();
		WpnShotgunType = res.readByte();
		WpnShotgunAuto = res.readByte();
		WpnShotgunLastShell = res.readByte();
		WpnRailType = res.readByte();

		Bloody = res.readBoolean();

		InitingNuke = res.readBoolean();
		TestNukeInit = res.readBoolean();
		NukeInitialized = res.readBoolean();
		FistAng = res.readShort();
		WpnKungFuMove = res.readByte();
		BunnyMode = res.readBoolean();
		HitBy = res.readShort();
		Reverb = res.readShort();
		Heads = res.readShort();
		last_used_weapon = res.readByte();
	}

	public String toString() {
		String out = "posx " + posx + " \r\n";
		out += "posy " + posy + " \r\n";
		out += "posz " + posz + " \r\n";
		out += "oposx " + oposx + " \r\n";
		out += "oposy " + oposy + " \r\n";
		out += "oposz " + oposz + " \r\n";
		out += "oang " + oang + " \r\n";
		out += "ohoriz " + ohoriz + " \r\n";
		out += "obob_z " + obob_z + " \r\n";
		out += "obob_amt " + obob_amt + " \r\n";
		out += "lv_sectnum " + lv_sectnum + " \r\n";
		out += "lv_x " + lv_x + " \r\n";
		out += "lv_y " + lv_y + " \r\n";
		out += "lv_z " + lv_z + " \r\n";
		out += "remote_sprite " + remote_sprite + " \r\n";
		out += "Class remote " + remote + " \r\n";
		out += "sop_remote " + sop_remote + " \r\n";
		out += "sop " + sop + " \r\n";
		out += "jump_count " + jump_count + " \r\n";
		out += "jump_speed " + jump_speed + " \r\n";
		out += "down_speed " + down_speed + " \r\n";
		out += "up_speed " + up_speed + " \r\n";
		out += "z_speed " + z_speed + " \r\n";
		out += "oz_speed " + oz_speed + " \r\n";
		out += "climb_ndx " + climb_ndx + " \r\n";
		out += "hiz " + hiz + " \r\n";
		out += "loz " + loz + " \r\n";
		out += "ceiling_dist " + ceiling_dist + " \r\n";
		out += "floor_dist " + floor_dist + " \r\n";
		out += "hi_sectp " + hi_sectp + " \r\n";
		out += "lo_sectp " + lo_sectp + " \r\n";
		out += "hi_sp " + hi_sp + " \r\n";
		out += "lo_sp " + lo_sp + " \r\n";
		out += "last_camera_sp " + last_camera_sp + " \r\n";
		out += "camera_dist " + camera_dist + " \r\n";
		out += "circle_camera_dist " + circle_camera_dist + " \r\n";
		out += "six " + six + " \r\n";
		out += "siy " + siy + " \r\n";
		out += "siz " + siz + " \r\n";
		out += "siang " + siang + " \r\n";
		out += "xvect " + xvect + " \r\n";
		out += "yvect " + yvect + " \r\n";
		out += "oxvect " + oxvect + " \r\n";
		out += "oyvect " + oyvect + " \r\n";
		out += "friction " + friction + " \r\n";
		out += "slide_xvect " + slide_xvect + " \r\n";
		out += "slide_yvect " + slide_yvect + " \r\n";
		out += "slide_ang " + slide_ang + " \r\n";
		out += "slide_dec " + slide_dec + " \r\n";
		out += "drive_angvel " + drive_angvel + " \r\n";
		out += "drive_oangvel " + drive_oangvel + " \r\n";
		out += "scr_x " + scr_x + " \r\n";
		out += "scr_y " + scr_y + " \r\n";
		out += "oscr_x " + oscr_x + " \r\n";
		out += "oscr_y " + oscr_y + " \r\n";
		out += "scr_xvect " + scr_xvect + " \r\n";
		out += "scr_yvect " + scr_yvect + " \r\n";
		out += "scr_ang " + scr_ang + " \r\n";
		out += "oscr_ang " + oscr_ang + " \r\n";
		out += "scr_sectnum " + scr_sectnum + " \r\n";
		out += "view_outside_dang " + view_outside_dang + " \r\n";
		out += "circle_camera_ang " + circle_camera_ang + " \r\n";
		out += "camera_check_time_delay " + camera_check_time_delay + " \r\n";
		out += "pang " + pang + " \r\n";
		out += "cursectnum " + cursectnum + " \r\n";
		out += "lastcursectnum " + lastcursectnum + " \r\n";
		out += "turn180_target " + turn180_target + " \r\n";
		out += "horizbase " + horizbase + " \r\n";
		out += "horiz " + horiz + " \r\n";
		out += "horizoff " + horizoff + " \r\n";
		out += "hvel " + hvel + " \r\n";
		out += "tilt " + tilt + " \r\n";
		out += "tilt_dest " + tilt_dest + " \r\n";
		out += "recoil_amt " + recoil_amt + " \r\n";
		out += "recoil_speed " + recoil_speed + " \r\n";
		out += "recoil_ndx " + recoil_ndx + " \r\n";
		out += "recoil_horizoff " + recoil_horizoff + " \r\n";
		out += "oldposx " + oldposx + " \r\n";
		out += "oldposy " + oldposy + " \r\n";
		out += "oldposz " + oldposz + " \r\n";
		out += "RevolveX " + RevolveX + " \r\n";
		out += "RevolveY " + RevolveY + " \r\n";
		out += "RevolveDeltaAng " + RevolveDeltaAng + " \r\n";
		out += "RevolveAng " + RevolveAng + " \r\n";
		out += "PlayerSprite " + PlayerSprite + " \r\n";
		out += "PlayerUnderSprite " + PlayerUnderSprite + " \r\n";
		out += "pnum " + pnum + " \r\n";
		out += "LadderSector " + LadderSector + " \r\n";
		out += "LadderAngle " + LadderAngle + " \r\n";
		out += "lx " + lx + " \r\n";
		out += "ly " + ly + " \r\n";
		out += "JumpDuration " + JumpDuration + " \r\n";
		out += "WadeDepth " + WadeDepth + " \r\n";
		out += "bob_amt " + bob_amt + " \r\n";
		out += "bob_ndx " + bob_ndx + " \r\n";
		out += "bcnt " + bcnt + " \r\n";
		out += "bob_z " + bob_z + " \r\n";

		out += "playerreadyflag " + playerreadyflag + " \r\n";
		out += "DoPlayerAction " + DoPlayerAction + " \r\n";
		out += "Flags " + Flags + " \r\n";
		out += "Flags2 " + Flags2 + " \r\n";
		out += "KeyPressFlags " + KeyPressFlags + " \r\n";
		out += "sop_control " + sop_control + " \r\n";
		out += "sop_riding " + sop_riding + " \r\n";
		for (int i = 0; i < NUM_KEYS; i++)
			out += "HasKey[" + i + "] " + HasKey[i] + " \r\n";
		out += "SwordAng " + SwordAng + " \r\n";
		out += "WpnGotOnceFlags " + WpnGotOnceFlags + " \r\n";
		out += "WpnFlags " + WpnFlags + " \r\n";
		for (int i = 0; i < MAX_WEAPONS; i++)
			out += "WpnAmmo[" + i + "] " + WpnAmmo[i] + " \r\n";
		out += "Class CurWpn " + CurWpn + " \r\n";
//		Arrays.fill(Wpn, null); XXX
		out += "Class Chops " + Chops + " \r\n";
		out += "WpnRocketType " + WpnRocketType + " \r\n";
		out += "WpnRocketHeat " + WpnRocketHeat + " \r\n";
		out += "WpnRocketNuke " + WpnRocketNuke + " \r\n";
		out += "WpnFlameType " + WpnFlameType + " \r\n";
		out += "WpnFirstType " + WpnFirstType + " \r\n";
		out += "WeaponType " + WeaponType + " \r\n";
		out += "FirePause " + FirePause + " \r\n";
		out += "InventoryNum " + InventoryNum + " \r\n";
		out += "InventoryBarTics " + InventoryBarTics + " \r\n";
//		Arrays.fill(InventorySprite, null); XXX
		out += "Class InventorySelectionBox " + InventorySelectionBox + " \r\n";
		for (int i = 0; i < MAX_INVENTORY; i++)
			out += "InventoryTics[" + i + "] " + InventoryTics[i] + " \r\n";
		for (int i = 0; i < MAX_INVENTORY; i++)
			out += "InventoryPercent[" + i + "] " + InventoryPercent[i] + " \r\n";
		for (int i = 0; i < MAX_INVENTORY; i++)
			out += "InventoryAmount[" + i + "] " + InventoryAmount[i] + " \r\n";
		for (int i = 0; i < MAX_INVENTORY; i++)
			out += "InventoryActive[" + i + "] " + InventoryActive[i] + " \r\n";
		out += "DiveTics " + DiveTics + " \r\n";
		out += "DiveDamageTics " + DiveDamageTics + " \r\n";
		out += "DeathType " + DeathType + " \r\n";
		out += "Kills " + Kills + " \r\n";
		out += "Killer " + Killer + " \r\n";
		for (int i = 0; i < MAX_SW_PLAYERS; i++)
			out += "KilledPlayer[" + i + "] " + KilledPlayer[i] + " \r\n";
		out += "SecretsFound " + SecretsFound + " \r\n";
		out += "Armor " + Armor + " \r\n";
		out += "MaxHealth " + MaxHealth + " \r\n";
		out += "PlayerName " + PlayerName + " \r\n";
		out += "UziShellLeftAlt " + UziShellLeftAlt + " \r\n";
		out += "UziShellRightAlt " + UziShellRightAlt + " \r\n";
		out += "TeamColor " + TeamColor + " \r\n";
		out += "FadeTics " + FadeTics + " \r\n";
		out += "FadeAmt " + FadeAmt + " \r\n";
		out += "NightVision " + NightVision + " \r\n";
		out += "StartColor " + StartColor + " \r\n";
		out += "Pal CRC32 " + CRC32.getChecksum(temp_pal) + " \r\n";
		out += "fta " + fta + " \r\n";
		out += "ftq " + ftq + " \r\n";
		out += "NumFootPrints " + NumFootPrints + " \r\n";
		out += "PlayerTalking " + PlayerTalking + " \r\n";
		out += "TalkVocnum " + TalkVocnum + " \r\n";
		out += "WpnUziType " + WpnUziType + " \r\n";
		out += "WpnShotgunType " + WpnShotgunType + " \r\n";
		out += "WpnShotgunAuto " + WpnShotgunAuto + " \r\n";
		out += "WpnShotgunLastShell " + WpnShotgunLastShell + " \r\n";
		out += "WpnRailType " + WpnRailType + " \r\n";
		out += "Bloody " + Bloody + " \r\n";
		out += "InitingNuke " + InitingNuke + " \r\n";
		out += "TestNukeInit " + TestNukeInit + " \r\n";
		out += "NukeInitialized " + NukeInitialized + " \r\n";
		out += "FistAng " + FistAng + " \r\n";
		out += "WpnKungFuMove " + WpnKungFuMove + " \r\n";
		out += "BunnyMode " + BunnyMode + " \r\n";
		out += "HitBy " + HitBy + " \r\n";
		out += "Reverb " + Reverb + " \r\n";
		out += "Heads " + Heads + " \r\n";
		out += "last_used_weapon " + last_used_weapon + " \r\n";
		out += "lookang " + lookang + " \r\n";

		return out;
	}

	public void compare(PlayerStr src) {
		Console.Println("Comparing...", Console.OSDTEXT_GREEN);
		if (this.posx != src.posx)
			Console.Println("Not match posx: " + this.posx + " != " + src.posx, Console.OSDTEXT_RED);
		if (this.posy != src.posy)
			Console.Println("Not match posy: " + this.posy + " != " + src.posy, Console.OSDTEXT_RED);
		if (this.posz != src.posz)
			Console.Println("Not match posz: " + this.posz + " != " + src.posz, Console.OSDTEXT_RED);
		if (this.oposx != src.oposx)
			Console.Println("Not match oposx: " + this.oposx + " != " + src.oposx, Console.OSDTEXT_RED);
		if (this.oposy != src.oposy)
			Console.Println("Not match oposy: " + this.oposy + " != " + src.oposy, Console.OSDTEXT_RED);
		if (this.oposz != src.oposz)
			Console.Println("Not match oposz: " + this.oposz + " != " + src.oposz, Console.OSDTEXT_RED);
		if (this.oang != src.oang)
			Console.Println("Not match oang: " + this.oang + " != " + src.oang, Console.OSDTEXT_RED);
		if (this.ohoriz != src.ohoriz)
			Console.Println("Not match ohoriz: " + this.ohoriz + " != " + src.ohoriz, Console.OSDTEXT_RED);
		if (this.obob_z != src.obob_z)
			Console.Println("Not match oang: " + this.obob_z + " != " + src.obob_z, Console.OSDTEXT_RED);
		if (this.obob_amt != src.obob_amt)
			Console.Println("Not match ohoriz: " + this.obob_amt + " != " + src.obob_amt, Console.OSDTEXT_RED);
		if (this.lv_sectnum != src.lv_sectnum)
			Console.Println("Not match lv_sectnum: " + this.lv_sectnum + " != " + src.lv_sectnum, Console.OSDTEXT_RED);
		if (this.lv_x != src.lv_x)
			Console.Println("Not match lv_x: " + this.lv_x + " != " + src.lv_x, Console.OSDTEXT_RED);
		if (this.lv_y != src.lv_y)
			Console.Println("Not match lv_y: " + this.lv_y + " != " + src.lv_y, Console.OSDTEXT_RED);
		if (this.lv_z != src.lv_z)
			Console.Println("Not match lv_z: " + this.lv_z + " != " + src.lv_z, Console.OSDTEXT_RED);
		if (this.remote_sprite != src.remote_sprite)
			Console.Println("Not match remote_sprite: " + this.remote_sprite + " != " + src.remote_sprite,
					Console.OSDTEXT_RED);

//		if (this.remote != src.remote) XXX
//			Console.Println("Not match remote: " + this.remote + " != " + src.remote, Console.OSDTEXT_RED);

		if (this.sop_remote != src.sop_remote)
			Console.Println("Not match sop_remote: " + this.sop_remote + " != " + src.sop_remote, Console.OSDTEXT_RED);
		if (this.sop != src.sop)
			Console.Println("Not match sop: " + this.sop + " != " + src.sop, Console.OSDTEXT_RED);
		if (this.jump_count != src.jump_count)
			Console.Println("Not match jump_count: " + this.jump_count + " != " + src.jump_count, Console.OSDTEXT_RED);
		if (this.jump_speed != src.jump_speed)
			Console.Println("Not match jump_speed: " + this.jump_speed + " != " + src.jump_speed, Console.OSDTEXT_RED);
		if (this.down_speed != src.down_speed)
			Console.Println("Not match down_speed: " + this.down_speed + " != " + src.down_speed, Console.OSDTEXT_RED);
		if (this.up_speed != src.up_speed)
			Console.Println("Not match up_speed: " + this.up_speed + " != " + src.up_speed, Console.OSDTEXT_RED);
		if (this.z_speed != src.z_speed)
			Console.Println("Not match z_speed: " + this.z_speed + " != " + src.z_speed, Console.OSDTEXT_RED);
		if (this.oz_speed != src.oz_speed)
			Console.Println("Not match oz_speed: " + this.oz_speed + " != " + src.oz_speed, Console.OSDTEXT_RED);
		if (this.climb_ndx != src.climb_ndx)
			Console.Println("Not match climb_ndx: " + this.climb_ndx + " != " + src.climb_ndx, Console.OSDTEXT_RED);
		if (this.hiz != src.hiz)
			Console.Println("Not match hiz: " + this.hiz + " != " + src.hiz, Console.OSDTEXT_RED);
		if (this.loz != src.loz)
			Console.Println("Not match loz: " + this.loz + " != " + src.loz, Console.OSDTEXT_RED);
		if (this.ceiling_dist != src.ceiling_dist)
			Console.Println("Not match ceiling_dist: " + this.ceiling_dist + " != " + src.ceiling_dist,
					Console.OSDTEXT_RED);
		if (this.floor_dist != src.floor_dist)
			Console.Println("Not match floor_dist: " + this.floor_dist + " != " + src.floor_dist, Console.OSDTEXT_RED);
		if (this.hi_sectp != src.hi_sectp)
			Console.Println("Not match hi_sectp: " + this.hi_sectp + " != " + src.hi_sectp, Console.OSDTEXT_RED);
		if (this.lo_sectp != src.lo_sectp)
			Console.Println("Not match lo_sectp: " + this.lo_sectp + " != " + src.lo_sectp, Console.OSDTEXT_RED);
		if (this.hi_sp != src.hi_sp)
			Console.Println("Not match hi_sp: " + this.hi_sp + " != " + src.hi_sp, Console.OSDTEXT_RED);
		if (this.lo_sp != src.lo_sp)
			Console.Println("Not match lo_sp: " + this.lo_sp + " != " + src.lo_sp, Console.OSDTEXT_RED);
		if (this.last_camera_sp != src.last_camera_sp)
			Console.Println("Not match last_camera_sp: " + this.last_camera_sp + " != " + src.last_camera_sp,
					Console.OSDTEXT_RED);
		if (this.camera_dist != src.camera_dist)
			Console.Println("Not match camera_dist: " + this.camera_dist + " != " + src.camera_dist,
					Console.OSDTEXT_RED);
		if (this.circle_camera_dist != src.circle_camera_dist)
			Console.Println(
					"Not match circle_camera_dist: " + this.circle_camera_dist + " != " + src.circle_camera_dist,
					Console.OSDTEXT_RED);
		if (this.xvect != src.xvect)
			Console.Println("Not match xvect: " + this.xvect + " != " + src.xvect, Console.OSDTEXT_RED);
		if (this.yvect != src.yvect)
			Console.Println("Not match yvect: " + this.yvect + " != " + src.yvect, Console.OSDTEXT_RED);
		if (this.oxvect != src.oxvect)
			Console.Println("Not match oxvect: " + this.oxvect + " != " + src.oxvect, Console.OSDTEXT_RED);
		if (this.oyvect != src.oyvect)
			Console.Println("Not match oyvect: " + this.oyvect + " != " + src.oyvect, Console.OSDTEXT_RED);
		if (this.friction != src.friction)
			Console.Println("Not match friction: " + this.friction + " != " + src.friction, Console.OSDTEXT_RED);
		if (this.slide_xvect != src.slide_xvect)
			Console.Println("Not match slide_xvect: " + this.slide_xvect + " != " + src.slide_xvect,
					Console.OSDTEXT_RED);
		if (this.slide_yvect != src.slide_yvect)
			Console.Println("Not match slide_yvect: " + this.slide_yvect + " != " + src.slide_yvect,
					Console.OSDTEXT_RED);
		if (this.slide_ang != src.slide_ang)
			Console.Println("Not match slide_ang: " + this.slide_ang + " != " + src.slide_ang, Console.OSDTEXT_RED);
		if (this.slide_dec != src.slide_dec)
			Console.Println("Not match slide_dec: " + this.slide_dec + " != " + src.slide_dec, Console.OSDTEXT_RED);
		if (this.drive_angvel != src.drive_angvel)
			Console.Println("Not match drive_angvel: " + this.drive_angvel + " != " + src.drive_angvel,
					Console.OSDTEXT_RED);
		if (this.drive_oangvel != src.drive_oangvel)
			Console.Println("Not match drive_oangvel: " + this.drive_oangvel + " != " + src.drive_oangvel,
					Console.OSDTEXT_RED);
		if (this.scr_x != src.scr_x)
			Console.Println("Not match scr_x: " + this.scr_x + " != " + src.scr_x, Console.OSDTEXT_RED);
		if (this.scr_y != src.scr_y)
			Console.Println("Not match scr_y: " + this.scr_y + " != " + src.scr_y, Console.OSDTEXT_RED);
		if (this.oscr_x != src.oscr_x)
			Console.Println("Not match oscr_x: " + this.oscr_x + " != " + src.oscr_x, Console.OSDTEXT_RED);
		if (this.oscr_y != src.oscr_y)
			Console.Println("Not match oscr_y: " + this.oscr_y + " != " + src.oscr_y, Console.OSDTEXT_RED);
		if (this.scr_xvect != src.scr_xvect)
			Console.Println("Not match scr_xvect: " + this.scr_xvect + " != " + src.scr_xvect, Console.OSDTEXT_RED);
		if (this.scr_yvect != src.scr_yvect)
			Console.Println("Not match scr_yvect: " + this.scr_yvect + " != " + src.scr_yvect, Console.OSDTEXT_RED);
		if (this.scr_ang != src.scr_ang)
			Console.Println("Not match scr_ang: " + this.scr_ang + " != " + src.scr_ang, Console.OSDTEXT_RED);
		if (this.oscr_ang != src.oscr_ang)
			Console.Println("Not match oscr_ang: " + this.oscr_ang + " != " + src.oscr_ang, Console.OSDTEXT_RED);
		if (this.scr_sectnum != src.scr_sectnum)
			Console.Println("Not match scr_sectnum: " + this.scr_sectnum + " != " + src.scr_sectnum,
					Console.OSDTEXT_RED);
		if (this.view_outside_dang != src.view_outside_dang)
			Console.Println("Not match view_outside_dang: " + this.view_outside_dang + " != " + src.view_outside_dang,
					Console.OSDTEXT_RED);
		if (this.circle_camera_ang != src.circle_camera_ang)
			Console.Println("Not match circle_camera_ang: " + this.circle_camera_ang + " != " + src.circle_camera_ang,
					Console.OSDTEXT_RED);
		if (this.camera_check_time_delay != src.camera_check_time_delay)
			Console.Println("Not match camera_check_time_delay: " + this.camera_check_time_delay + " != "
					+ src.camera_check_time_delay, Console.OSDTEXT_RED);
		if (this.pang != src.pang)
			Console.Println("Not match pang: " + this.pang + " != " + src.pang, Console.OSDTEXT_RED);
		if (this.cursectnum != src.cursectnum)
			Console.Println("Not match cursectnum: " + this.cursectnum + " != " + src.cursectnum, Console.OSDTEXT_RED);
		if (this.lastcursectnum != src.lastcursectnum)
			Console.Println("Not match lastcursectnum: " + this.lastcursectnum + " != " + src.lastcursectnum,
					Console.OSDTEXT_RED);
		if (this.turn180_target != src.turn180_target)
			Console.Println("Not match turn180_target: " + this.turn180_target + " != " + src.turn180_target,
					Console.OSDTEXT_RED);
		if (this.horizbase != src.horizbase)
			Console.Println("Not match horizbase: " + this.horizbase + " != " + src.horizbase, Console.OSDTEXT_RED);
		if (this.horiz != src.horiz)
			Console.Println("Not match horiz: " + this.horiz + " != " + src.horiz, Console.OSDTEXT_RED);
		if (this.horizoff != src.horizoff)
			Console.Println("Not match horizoff: " + this.horizoff + " != " + src.horizoff, Console.OSDTEXT_RED);
		if (this.hvel != src.hvel)
			Console.Println("Not match hvel: " + this.hvel + " != " + src.hvel, Console.OSDTEXT_RED);
		if (this.tilt != src.tilt)
			Console.Println("Not match tilt: " + this.tilt + " != " + src.tilt, Console.OSDTEXT_RED);
		if (this.tilt_dest != src.tilt_dest)
			Console.Println("Not match tilt_dest: " + this.tilt_dest + " != " + src.tilt_dest, Console.OSDTEXT_RED);
		if (this.recoil_amt != src.recoil_amt)
			Console.Println("Not match recoil_amt: " + this.recoil_amt + " != " + src.recoil_amt, Console.OSDTEXT_RED);
		if (this.recoil_speed != src.recoil_speed)
			Console.Println("Not match recoil_speed: " + this.recoil_speed + " != " + src.recoil_speed,
					Console.OSDTEXT_RED);
		if (this.recoil_ndx != src.recoil_ndx)
			Console.Println("Not match recoil_ndx: " + this.recoil_ndx + " != " + src.recoil_ndx, Console.OSDTEXT_RED);
		if (this.recoil_horizoff != src.recoil_horizoff)
			Console.Println("Not match recoil_horizoff: " + this.recoil_horizoff + " != " + src.recoil_horizoff,
					Console.OSDTEXT_RED);
		if (this.oldposx != src.oldposx)
			Console.Println("Not match oldposx: " + this.oldposx + " != " + src.oldposx, Console.OSDTEXT_RED);
		if (this.oldposy != src.oldposy)
			Console.Println("Not match oldposy: " + this.oldposy + " != " + src.oldposy, Console.OSDTEXT_RED);
		if (this.oldposz != src.oldposz)
			Console.Println("Not match oldposz: " + this.oldposz + " != " + src.oldposz, Console.OSDTEXT_RED);
		if (this.RevolveX != src.RevolveX)
			Console.Println("Not match RevolveX: " + this.RevolveX + " != " + src.RevolveX, Console.OSDTEXT_RED);
		if (this.RevolveY != src.RevolveY)
			Console.Println("Not match RevolveY: " + this.RevolveY + " != " + src.RevolveY, Console.OSDTEXT_RED);
		if (this.RevolveDeltaAng != src.RevolveDeltaAng)
			Console.Println("Not match RevolveDeltaAng: " + this.RevolveDeltaAng + " != " + src.RevolveDeltaAng,
					Console.OSDTEXT_RED);
		if (this.RevolveAng != src.RevolveAng)
			Console.Println("Not match RevolveAng: " + this.RevolveAng + " != " + src.RevolveAng, Console.OSDTEXT_RED);
		if (this.PlayerSprite != src.PlayerSprite)
			Console.Println("Not match PlayerSprite: " + this.PlayerSprite + " != " + src.PlayerSprite,
					Console.OSDTEXT_RED);
		if (this.PlayerUnderSprite != src.PlayerUnderSprite)
			Console.Println("Not match PlayerUnderSprite: " + this.PlayerUnderSprite + " != " + src.PlayerUnderSprite,
					Console.OSDTEXT_RED);
		if (this.pnum != src.pnum)
			Console.Println("Not match pnum: " + this.pnum + " != " + src.pnum, Console.OSDTEXT_RED);
		if (this.LadderSector != src.LadderSector)
			Console.Println("Not match LadderSector: " + this.LadderSector + " != " + src.LadderSector,
					Console.OSDTEXT_RED);
		if (this.LadderAngle != src.LadderAngle)
			Console.Println("Not match LadderAngle: " + this.LadderAngle + " != " + src.LadderAngle,
					Console.OSDTEXT_RED);
		if (this.lx != src.lx)
			Console.Println("Not match lx: " + this.lx + " != " + src.lx, Console.OSDTEXT_RED);
		if (this.ly != src.ly)
			Console.Println("Not match ly: " + this.ly + " != " + src.ly, Console.OSDTEXT_RED);
		if (this.JumpDuration != src.JumpDuration)
			Console.Println("Not match JumpDuration: " + this.JumpDuration + " != " + src.JumpDuration,
					Console.OSDTEXT_RED);
		if (this.WadeDepth != src.WadeDepth)
			Console.Println("Not match WadeDepth: " + this.WadeDepth + " != " + src.WadeDepth, Console.OSDTEXT_RED);
		if (this.bob_amt != src.bob_amt)
			Console.Println("Not match bob_amt: " + this.bob_amt + " != " + src.bob_amt, Console.OSDTEXT_RED);
		if (this.bob_ndx != src.bob_ndx)
			Console.Println("Not match bob_ndx: " + this.bob_ndx + " != " + src.bob_ndx, Console.OSDTEXT_RED);
		if (this.bcnt != src.bcnt)
			Console.Println("Not match bcnt: " + this.bcnt + " != " + src.bcnt, Console.OSDTEXT_RED);
		if (this.bob_z != src.bob_z)
			Console.Println("Not match bob_z: " + this.bob_z + " != " + src.bob_z, Console.OSDTEXT_RED);

		if (this.playerreadyflag != src.playerreadyflag)
			Console.Println("Not match playerreadyflag: " + this.playerreadyflag + " != " + src.playerreadyflag,
					Console.OSDTEXT_RED);
		if (this.DoPlayerAction != src.DoPlayerAction)
			Console.Println("Not match DoPlayerAction: " + this.DoPlayerAction + " != " + src.DoPlayerAction,
					Console.OSDTEXT_RED);
		if (this.Flags != src.Flags)
			Console.Println("Not match Flags: " + this.Flags + " != " + src.Flags, Console.OSDTEXT_RED);
		if (this.Flags2 != src.Flags2)
			Console.Println("Not match Flags2: " + this.Flags2 + " != " + src.Flags2, Console.OSDTEXT_RED);
		if (this.KeyPressFlags != src.KeyPressFlags)
			Console.Println("Not match KeyPressFlags: " + this.KeyPressFlags + " != " + src.KeyPressFlags,
					Console.OSDTEXT_RED);
		if (this.sop_control != src.sop_control)
			Console.Println("Not match sop_control: " + this.sop_control + " != " + src.sop_control,
					Console.OSDTEXT_RED);
		if (this.sop_riding != src.sop_riding)
			Console.Println("Not match sop_riding: " + this.sop_riding + " != " + src.sop_riding, Console.OSDTEXT_RED);

		for (int i = 0; i < NUM_KEYS; i++) {
			if (this.HasKey[i] != src.HasKey[i])
				Console.Println("Not match HasKey[" + i + "] " + this.HasKey[i] + " != " + src.HasKey[i],
						Console.OSDTEXT_RED);
		}

		if (this.SwordAng != src.SwordAng)
			Console.Println("Not match SwordAng: " + this.SwordAng + " != " + src.SwordAng, Console.OSDTEXT_RED);
		if (this.WpnGotOnceFlags != src.WpnGotOnceFlags)
			Console.Println("Not match WpnGotOnceFlags: " + this.WpnGotOnceFlags + " != " + src.WpnGotOnceFlags,
					Console.OSDTEXT_RED);
		if (this.WpnFlags != src.WpnFlags)
			Console.Println("Not match WpnFlags: " + this.WpnFlags + " != " + src.WpnFlags, Console.OSDTEXT_RED);
		for (int i = 0; i < MAX_WEAPONS; i++) {
			if (this.WpnAmmo[i] != src.WpnAmmo[i])
				Console.Println("Not match WpnAmmo[" + i + "] " + this.WpnAmmo[i] + " != " + src.WpnAmmo[i],
						Console.OSDTEXT_RED);
		}
		if (this.WpnRocketType != src.WpnRocketType)
			Console.Println("Not match WpnRocketType: " + this.WpnRocketType + " != " + src.WpnRocketType,
					Console.OSDTEXT_RED);
		if (this.WpnRocketHeat != src.WpnRocketHeat)
			Console.Println("Not match WpnRocketHeat: " + this.WpnRocketHeat + " != " + src.WpnRocketHeat,
					Console.OSDTEXT_RED);
		if (this.WpnRocketNuke != src.WpnRocketNuke)
			Console.Println("Not match WpnRocketNuke: " + this.WpnRocketNuke + " != " + src.WpnRocketNuke,
					Console.OSDTEXT_RED);
		if (this.WpnFlameType != src.WpnFlameType)
			Console.Println("Not match WpnFlameType: " + this.WpnFlameType + " != " + src.WpnFlameType,
					Console.OSDTEXT_RED);
		if (this.WpnFirstType != src.WpnFirstType)
			Console.Println("Not match WpnFirstType: " + this.WpnFirstType + " != " + src.WpnFirstType,
					Console.OSDTEXT_RED);
		if (this.WeaponType != src.WeaponType)
			Console.Println("Not match WeaponType: " + this.WeaponType + " != " + src.WeaponType, Console.OSDTEXT_RED);
		if (this.FirePause != src.FirePause)
			Console.Println("Not match FirePause: " + this.FirePause + " != " + src.FirePause, Console.OSDTEXT_RED);
		if (this.InventoryNum != src.InventoryNum)
			Console.Println("Not match InventoryNum: " + this.InventoryNum + " != " + src.InventoryNum,
					Console.OSDTEXT_RED);
		if (this.InventoryBarTics != src.InventoryBarTics)
			Console.Println("Not match InventoryBarTics: " + this.InventoryBarTics + " != " + src.InventoryBarTics,
					Console.OSDTEXT_RED);

		for (int i = 0; i < MAX_INVENTORY; i++) {
			if (this.InventoryTics[i] != src.InventoryTics[i])
				Console.Println(
						"Not match InventoryTics[" + i + "] " + this.InventoryTics[i] + " != " + src.InventoryTics[i],
						Console.OSDTEXT_RED);
			if (this.InventoryPercent[i] != src.InventoryPercent[i])
				Console.Println("Not match InventoryPercent[" + i + "] " + this.InventoryPercent[i] + " != "
						+ src.InventoryPercent[i], Console.OSDTEXT_RED);
			if (this.InventoryAmount[i] != src.InventoryAmount[i])
				Console.Println("Not match InventoryAmount[" + i + "] " + this.InventoryAmount[i] + " != "
						+ src.InventoryAmount[i], Console.OSDTEXT_RED);
			if (this.InventoryActive[i] != src.InventoryActive[i])
				Console.Println("Not match InventoryActive[" + i + "] " + this.InventoryActive[i] + " != "
						+ src.InventoryActive[i], Console.OSDTEXT_RED);
		}

		if (this.DiveTics != src.DiveTics)
			Console.Println("Not match DiveTics: " + this.DiveTics + " != " + src.DiveTics, Console.OSDTEXT_RED);
		if (this.DiveDamageTics != src.DiveDamageTics)
			Console.Println("Not match DiveDamageTics: " + this.DiveDamageTics + " != " + src.DiveDamageTics,
					Console.OSDTEXT_RED);
		if (this.DeathType != src.DeathType)
			Console.Println("Not match DeathType: " + this.DeathType + " != " + src.DeathType, Console.OSDTEXT_RED);
		if (this.Kills != src.Kills)
			Console.Println("Not match Kills: " + this.Kills + " != " + src.Kills, Console.OSDTEXT_RED);
		if (this.Killer != src.Killer)
			Console.Println("Not match Killer: " + this.Killer + " != " + src.Killer, Console.OSDTEXT_RED);

		for (int i = 0; i < MAX_SW_PLAYERS; i++) {
			if (this.KilledPlayer[i] != src.KilledPlayer[i])
				Console.Println(
						"Not match KilledPlayer[" + i + "] " + this.KilledPlayer[i] + " != " + src.KilledPlayer[i],
						Console.OSDTEXT_RED);
		}

		if (this.SecretsFound != src.SecretsFound)
			Console.Println("Not match SecretsFound: " + this.SecretsFound + " != " + src.SecretsFound,
					Console.OSDTEXT_RED);
		if (this.Armor != src.Armor)
			Console.Println("Not match Armor: " + this.Armor + " != " + src.Armor, Console.OSDTEXT_RED);
		if (this.MaxHealth != src.MaxHealth)
			Console.Println("Not match MaxHealth: " + this.MaxHealth + " != " + src.MaxHealth, Console.OSDTEXT_RED);
		if (this.UziShellLeftAlt != src.UziShellLeftAlt)
			Console.Println("Not match UziShellLeftAlt: " + this.UziShellLeftAlt + " != " + src.UziShellLeftAlt,
					Console.OSDTEXT_RED);
		if (this.UziShellRightAlt != src.UziShellRightAlt)
			Console.Println("Not match UziShellRightAlt: " + this.UziShellRightAlt + " != " + src.UziShellRightAlt,
					Console.OSDTEXT_RED);
		if (this.TeamColor != src.TeamColor)
			Console.Println("Not match TeamColor: " + this.TeamColor + " != " + src.TeamColor, Console.OSDTEXT_RED);
		if (this.FadeTics != src.FadeTics)
			Console.Println("Not match FadeTics: " + this.FadeTics + " != " + src.FadeTics, Console.OSDTEXT_RED);
		if (this.FadeAmt != src.FadeAmt)
			Console.Println("Not match FadeAmt: " + this.FadeAmt + " != " + src.FadeAmt, Console.OSDTEXT_RED);
		if (this.NightVision != src.NightVision)
			Console.Println("Not match NightVision: " + this.NightVision + " != " + src.NightVision,
					Console.OSDTEXT_RED);
		if (this.StartColor != src.StartColor)
			Console.Println("Not match StartColor: " + this.StartColor + " != " + src.StartColor, Console.OSDTEXT_RED);
		long c1 = CRC32.getChecksum(this.temp_pal);
		long c2 = CRC32.getChecksum(src.temp_pal);
		if (c1 != c2)
			Console.Println("Not match temp_pal crc32: " + c1 + " != " + c2, Console.OSDTEXT_RED);

		if (this.fta != src.fta)
			Console.Println("Not match fta: " + this.fta + " != " + src.fta, Console.OSDTEXT_RED);
		if (this.ftq != src.ftq)
			Console.Println("Not match ftq: " + this.ftq + " != " + src.ftq, Console.OSDTEXT_RED);
		if (this.NumFootPrints != src.NumFootPrints)
			Console.Println("Not match NumFootPrints: " + this.NumFootPrints + " != " + src.NumFootPrints,
					Console.OSDTEXT_RED);
		if (this.PlayerTalking != src.PlayerTalking)
			Console.Println("Not match PlayerTalking: " + this.PlayerTalking + " != " + src.PlayerTalking,
					Console.OSDTEXT_RED);
		if (this.TalkVocnum != src.TalkVocnum)
			Console.Println("Not match TalkVocnum: " + this.TalkVocnum + " != " + src.TalkVocnum, Console.OSDTEXT_RED);
		if (this.WpnUziType != src.WpnUziType)
			Console.Println("Not match WpnUziType: " + this.WpnUziType + " != " + src.WpnUziType, Console.OSDTEXT_RED);
		if (this.WpnShotgunType != src.WpnShotgunType)
			Console.Println("Not match WpnShotgunType: " + this.WpnShotgunType + " != " + src.WpnShotgunType,
					Console.OSDTEXT_RED);
		if (this.WpnShotgunAuto != src.WpnShotgunAuto)
			Console.Println("Not match WpnShotgunAuto: " + this.WpnShotgunAuto + " != " + src.WpnShotgunAuto,
					Console.OSDTEXT_RED);
		if (this.WpnShotgunLastShell != src.WpnShotgunLastShell)
			Console.Println(
					"Not match WpnShotgunLastShell: " + this.WpnShotgunLastShell + " != " + src.WpnShotgunLastShell,
					Console.OSDTEXT_RED);
		if (this.WpnRailType != src.WpnRailType)
			Console.Println("Not match WpnRailType: " + this.WpnRailType + " != " + src.WpnRailType,
					Console.OSDTEXT_RED);
		if (this.Bloody != src.Bloody)
			Console.Println("Not match Bloody: " + this.Bloody + " != " + src.Bloody, Console.OSDTEXT_RED);
		if (this.InitingNuke != src.InitingNuke)
			Console.Println("Not match InitingNuke: " + this.InitingNuke + " != " + src.InitingNuke,
					Console.OSDTEXT_RED);
		if (this.TestNukeInit != src.TestNukeInit)
			Console.Println("Not match TestNukeInit: " + this.TestNukeInit + " != " + src.TestNukeInit,
					Console.OSDTEXT_RED);
		if (this.NukeInitialized != src.NukeInitialized)
			Console.Println("Not match NukeInitialized: " + this.NukeInitialized + " != " + src.NukeInitialized,
					Console.OSDTEXT_RED);
		if (this.FistAng != src.FistAng)
			Console.Println("Not match FistAng: " + this.FistAng + " != " + src.FistAng, Console.OSDTEXT_RED);
		if (this.WpnKungFuMove != src.WpnKungFuMove)
			Console.Println("Not match WpnKungFuMove: " + this.WpnKungFuMove + " != " + src.WpnKungFuMove,
					Console.OSDTEXT_RED);
		if (this.BunnyMode != src.BunnyMode)
			Console.Println("Not match BunnyMode: " + this.BunnyMode + " != " + src.BunnyMode, Console.OSDTEXT_RED);
		if (this.HitBy != src.HitBy)
			Console.Println("Not match HitBy: " + this.HitBy + " != " + src.HitBy, Console.OSDTEXT_RED);
		if (this.Reverb != src.Reverb)
			Console.Println("Not match Reverb: " + this.Reverb + " != " + src.Reverb, Console.OSDTEXT_RED);
		if (this.Heads != src.Heads)
			Console.Println("Not match Heads: " + this.Heads + " != " + src.Heads, Console.OSDTEXT_RED);
		if (this.last_used_weapon != src.last_used_weapon)
			Console.Println("Not match last_used_weapon: " + this.last_used_weapon + " != " + src.last_used_weapon, Console.OSDTEXT_RED);
		if (this.lookang != src.lookang)
			Console.Println("Not match lookang: " + this.lookang + " != " + src.lookang, Console.OSDTEXT_RED);

		Console.Println("Compare completed", Console.OSDTEXT_GREEN);
	}

	public void copy(PlayerStr src) {
		this.posx = src.posx;
		this.posy = src.posy;
		this.posz = src.posz;
		this.oposx = src.oposx;
		this.oposy = src.oposy;
		this.oposz = src.oposz;
		this.oang = src.oang;
		this.ohoriz = src.ohoriz;
		this.obob_z = src.obob_z;
		this.obob_amt = src.obob_amt;
		this.lv_sectnum = src.lv_sectnum;
		this.lv_x = src.lv_x;
		this.lv_y = src.lv_y;
		this.lv_z = src.lv_z;
		this.remote_sprite = src.remote_sprite;
		if (src.remote != null) {
			if (this.remote == null)
				this.remote = new Remote_Control();
			this.remote.copy(src.remote);
		} else
			this.remote = null;
		this.sop_remote = src.sop_remote;
		this.sop = src.sop;
		this.jump_count = src.jump_count;
		this.jump_speed = src.jump_speed;
		this.down_speed = src.down_speed;
		this.up_speed = src.up_speed;
		this.z_speed = src.z_speed;
		this.oz_speed = src.oz_speed;
		this.climb_ndx = src.climb_ndx;
		this.hiz = src.hiz;
		this.loz = src.loz;
		this.ceiling_dist = src.ceiling_dist;
		this.floor_dist = src.floor_dist;
		this.hi_sectp = src.hi_sectp;
		this.lo_sectp = src.lo_sectp;
		this.hi_sp = src.hi_sp;
		this.lo_sp = src.lo_sp;
		this.last_camera_sp = src.last_camera_sp;
		this.camera_dist = src.camera_dist;
		this.circle_camera_dist = src.circle_camera_dist;
		this.xvect = src.xvect;
		this.yvect = src.yvect;
		this.oxvect = src.oxvect;
		this.oyvect = src.oyvect;
		this.friction = src.friction;
		this.slide_xvect = src.slide_xvect;
		this.slide_yvect = src.slide_yvect;
		this.slide_ang = src.slide_ang;
		this.slide_dec = src.slide_dec;
		this.drive_angvel = src.drive_angvel;
		this.drive_oangvel = src.drive_oangvel;
		this.scr_x = src.scr_x;
		this.scr_y = src.scr_y;
		this.oscr_x = src.oscr_x;
		this.oscr_y = src.oscr_y;
		this.scr_xvect = src.scr_xvect;
		this.scr_yvect = src.scr_yvect;
		this.scr_ang = src.scr_ang;
		this.oscr_ang = src.oscr_ang;
		this.scr_sectnum = src.scr_sectnum;
		this.view_outside_dang = src.view_outside_dang;
		this.circle_camera_ang = src.circle_camera_ang;
		this.camera_check_time_delay = src.camera_check_time_delay;
		this.pang = src.pang;
		this.cursectnum = src.cursectnum;
		this.lastcursectnum = src.lastcursectnum;
		this.turn180_target = src.turn180_target;
		this.horizbase = src.horizbase;
		this.horiz = src.horiz;
		this.horizoff = src.horizoff;
		this.hvel = src.hvel;
		this.tilt = src.tilt;
		this.tilt_dest = src.tilt_dest;
		this.recoil_amt = src.recoil_amt;
		this.recoil_speed = src.recoil_speed;
		this.recoil_ndx = src.recoil_ndx;
		this.recoil_horizoff = src.recoil_horizoff;
		this.oldposx = src.oldposx;
		this.oldposy = src.oldposy;
		this.oldposz = src.oldposz;
		this.RevolveX = src.RevolveX;
		this.RevolveY = src.RevolveY;
		this.RevolveDeltaAng = src.RevolveDeltaAng;
		this.RevolveAng = src.RevolveAng;
		this.PlayerSprite = src.PlayerSprite;
		this.PlayerUnderSprite = src.PlayerUnderSprite;
		this.pnum = src.pnum;
		this.LadderSector = src.LadderSector;
		this.LadderAngle = src.LadderAngle;
		this.lx = src.lx;
		this.ly = src.ly;
		this.JumpDuration = src.JumpDuration;
		this.WadeDepth = src.WadeDepth;
		this.bob_amt = src.bob_amt;
		this.bob_ndx = src.bob_ndx;
		this.bcnt = src.bcnt;
		this.bob_z = src.bob_z;
		this.input.Copy(src.input);
		this.playerreadyflag = src.playerreadyflag;
		this.DoPlayerAction = src.DoPlayerAction;
		this.Flags = src.Flags;
		this.Flags2 = src.Flags2;
		this.KeyPressFlags = src.KeyPressFlags;
		this.sop_control = src.sop_control;
		this.sop_riding = src.sop_riding;
		System.arraycopy(src.HasKey, 0, HasKey, 0, NUM_KEYS);
		this.SwordAng = src.SwordAng;
		this.WpnGotOnceFlags = src.WpnGotOnceFlags;
		this.WpnFlags = src.WpnFlags;
		System.arraycopy(src.WpnAmmo, 0, WpnAmmo, 0, MAX_WEAPONS);

		this.PanelSpriteList = src.PanelSpriteList;
//		this.PanelSpriteList.copy(src.PanelSpriteList);
		this.CurWpn = src.CurWpn;
//		this.CurWpn.copy(src.CurWpn);
		this.Wpn = src.Wpn;
//		for (int ndx = 0; ndx < MAX_WEAPONS; ndx++)
//			this.Wpn[ndx].copy(src.Wpn[ndx]);
		this.Chops = src.Chops;
//		this.Chops.copy(src.Chops);
		this.WpnRocketType = src.WpnRocketType;
		this.WpnRocketHeat = src.WpnRocketHeat;
		this.WpnRocketNuke = src.WpnRocketNuke;
		this.WpnFlameType = src.WpnFlameType;
		this.WpnFirstType = src.WpnFirstType;
		this.WeaponType = src.WeaponType;
		this.FirePause = src.FirePause;
		this.InventoryNum = src.InventoryNum;
		this.InventoryBarTics = src.InventoryBarTics;

		this.InventorySprite = src.InventorySprite;
		this.InventorySelectionBox = src.InventorySelectionBox;
//		for (int ndx = 0; ndx < MAX_INVENTORY; ndx++) 
//			this.InventorySprite[ndx].copy(src.InventorySprite[ndx]);
//		this.InventorySelectionBox.copy(src.InventorySelectionBox);

		System.arraycopy(src.InventoryTics, 0, InventoryTics, 0, MAX_INVENTORY);
		System.arraycopy(src.InventoryPercent, 0, InventoryPercent, 0, MAX_INVENTORY);
		System.arraycopy(src.InventoryAmount, 0, InventoryAmount, 0, MAX_INVENTORY);
		System.arraycopy(src.InventoryActive, 0, InventoryActive, 0, MAX_INVENTORY);

		this.DiveTics = src.DiveTics;
		this.DiveDamageTics = src.DiveDamageTics;
		this.DeathType = src.DeathType;
		this.Kills = src.Kills;
		this.Killer = src.Killer;
		System.arraycopy(src.KilledPlayer, 0, KilledPlayer, 0, MAX_SW_PLAYERS);

		this.SecretsFound = src.SecretsFound;
		this.Armor = src.Armor;
		this.MaxHealth = src.MaxHealth;
		this.UziShellLeftAlt = src.UziShellLeftAlt;
		this.UziShellRightAlt = src.UziShellRightAlt;
		this.TeamColor = src.TeamColor;
		this.FadeTics = src.FadeTics;
		this.FadeAmt = src.FadeAmt;
		this.NightVision = src.NightVision;
		this.StartColor = src.StartColor;

		System.arraycopy(src.temp_pal, 0, temp_pal, 0, 768);
		this.fta = src.fta;
		this.ftq = src.ftq;
		this.NumFootPrints = src.NumFootPrints;

		this.PlayerTalking = src.PlayerTalking;
		this.TalkVocnum = src.TalkVocnum;
		this.nukevochandle = src.nukevochandle;
		this.TalkVocHandle = src.TalkVocHandle;

		this.WpnUziType = src.WpnUziType;
		this.WpnShotgunType = src.WpnShotgunType;
		this.WpnShotgunAuto = src.WpnShotgunAuto;
		this.WpnShotgunLastShell = src.WpnShotgunLastShell;
		this.WpnRailType = src.WpnRailType;
		this.Bloody = src.Bloody;
		this.InitingNuke = src.InitingNuke;
		this.TestNukeInit = src.TestNukeInit;
		this.NukeInitialized = src.NukeInitialized;
		this.FistAng = src.FistAng;
		this.WpnKungFuMove = src.WpnKungFuMove;
		this.BunnyMode = src.BunnyMode;
		this.HitBy = src.HitBy;
		this.Reverb = src.Reverb;
		this.Heads = src.Heads;
		this.last_used_weapon = src.last_used_weapon;
		this.lookang = src.lookang;
	}
}
