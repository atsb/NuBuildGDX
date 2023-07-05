package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.WHScreen.*;

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Witchaven.Config.WhKeys;
import ru.m210projects.Witchaven.Types.Input;

public class WHControl extends BuildControls {
	
	private static final int NORMALTURN = 64;
	public static final int NORMALKEYMOVE = 15;
	public static final int WH2NORMALKEYMOVE = 20;
	
	public static int YDIM = 200;
	private static final int MAXVEL = 201;
	private static final int MAXSVEL = 127;
	private static final int MAXANGVEL =  127;
	
	public static final int CtrlJump = 1;
	public static final int CtrlCrouch = 1 << 1;
	public static final int CtrlWeapon_Fire = 1 << 2;
	public static final int CtrlAim_Up = 1 << 3;
	public static final int CtrlAim_Down = 1 << 4;
	public static final int CtrlAim_Center = 1 << 5;
	public static final int CtrlRun = 1 << 6;
	public static final int CtrlCastspell = 1 << 7;
	
	//1 << 8 - 1 << 12 - weapons
	//1 << 12 - 1 << 16 - spells
	//1 << 16 - 1 << 18 - potions

	public static final int CtrlInventory_Use = 1 << 19;
	
	public static final int CtrlMouseAim = 1 << 20;
	public static final int CtrlOpen = 1 << 21;
	public static final int CtrlTurnAround = 1 << 22;
	
	public static final int CtrlFlyup = 1 << 23;
	public static final int CtrlFlydown = 1 << 24;
	public static final int CtrlEndflying = 1 << 25;

	private int vel, svel;
	private float horiz, angvel;
	private int nonsharedtimer;
	private int lPlayerXVel, lPlayerYVel;
	
	public WHControl(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
	}
	
	public void reset()
	{
		lPlayerXVel = lPlayerYVel = 0;
	}

	@Override
	public void ctrlGetInput(NetInput input) {
		Input loc = (Input) input;
		
		if (Console.IsShown())
			return;

		loc.Reset();
		vel = svel = 0;
		horiz = angvel = 0;

		if (game.menu.gShowMenu)
			return;
		
		if (ctrlGetInputKey(GameKeys.Mouse_Aiming, true)) {
			if (!whcfg.gMouseAim) {
				whcfg.gMouseAim = true;
				showmessage("Mouse aiming on", 240);
			} else {
				whcfg.gMouseAim = false;
				autohoriz = 1;
				showmessage("Mouse aiming off", 240);
			}
		}
		
		if(ctrlGetInputKey(WhKeys.Toggle_Crosshair, true))
        {
        	if(!whcfg.gCrosshair) {
        		whcfg.gCrosshair = true;
        		showmessage("Crosshair on", 240);
        	}
        	else
        	{
        		whcfg.gCrosshair = false;
        		showmessage("Crosshair off", 240);
        	}
        }
		
		if (ctrlGetInputKey(WhKeys.AutoRun, true)) {
			if (!whcfg.gAutoRun) {
				whcfg.gAutoRun = true;
				showmessage("Auto run On", 240);
			} else {
				whcfg.gAutoRun = false;
				showmessage("Auto run Off", 240);
			}
		}
		
		if(ctrlGetInputKey(WhKeys.See_Coop_View, true)) 
		{
			if(numplayers > 1)
			{
				pyrn = connectpoint2[pyrn];
				if (pyrn < 0) pyrn = connecthead;
			}
		}
		
		if (ctrlGetInputKey(GameKeys.Map_Toggle, true)) {
			if (dimension == 3)
				dimension = 2;
			else dimension = 3;
		}
	
		if (dimension == 2) {
			int j = totalclock-nonsharedtimer; nonsharedtimer += j;
			if ( ctrlGetInputKey(GameKeys.Enlarge_Hud, false) )
				zoom += mulscale(j, Math.max(zoom,256), 6);
			if ( ctrlGetInputKey(GameKeys.Shrink_Hud, false) )
				zoom -= mulscale(j, Math.max(zoom,256), 6);
            zoom = BClipRange(zoom, 48, 4096);
            
			if (ctrlGetInputKey(WhKeys.Map_Follow_Mode, true)) {
				followmode = !followmode;
				if (followmode) {
					followx = player[myconnectindex].x;
					followy = player[myconnectindex].y;
					followa = (int) player[myconnectindex].ang;
				}
			}
		} else {
			if ( ctrlGetInputKey(GameKeys.Enlarge_Hud, true) && whcfg.gViewSize > 0)
				whcfg.gViewSize--;
			if ( ctrlGetInputKey(GameKeys.Shrink_Hud, true) && whcfg.gViewSize < 1)
				whcfg.gViewSize++;
		}
		
		if (ctrlGetInputKey(WhKeys.Make_Screenshot, true)) {
			
			String name = "scrxxxx.png";
			if(mUserFlag == UserFlag.UserMap) 
				name = "scr-" + FileUtils.getFullName(boardfilename) + "-xxxx.png";
			else
				name = "scr-map" + mapon + "-xxxx.png";

			String filename = engine.screencapture(name);
			if(filename != null)
				showmessage(filename + " saved", 240);
			else showmessage("Screenshot not saved. Access denied!", 240);
		}
		
		loc.bits =   ctrlGetInputKey(GameKeys.Jump, false)?CtrlJump:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Crouch, false)?CtrlCrouch:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Weapon_Fire, false)?CtrlWeapon_Fire:0;
	    loc.bits |=   ctrlGetInputKey(WhKeys.Aim_Up, false)?CtrlAim_Up:0;
	    loc.bits |=   ctrlGetInputKey(WhKeys.Aim_Down, false)?CtrlAim_Down:0;
	    loc.bits |=   ctrlGetInputKey(WhKeys.Aim_Center, true)? CtrlAim_Center : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Run, false)?CtrlRun:0;
	    loc.bits |=   ctrlGetInputKey(WhKeys.Cast_spell, true)?CtrlCastspell:0;
	   
	    for(int i = 0; i < 10; i++) 
			if(ctrlGetInputKey(whcfg.keymap[i + WhKeys.Weapon_1.getNum()], true)) 
				loc.bits |= (i + 1) << 8; //1 << 12
	    if(ctrlGetInputKey(GameKeys.Previous_Weapon, true)) 
	    	loc.bits |= (11)<<8;
	    if(ctrlGetInputKey(GameKeys.Next_Weapon, true)) 
	    	loc.bits |= (12)<<8;

	    for(int i = 0; i < 8; i++)
			if(ctrlGetInputKey(whcfg.keymap[i + WhKeys.Scare_Spell.getNum()], true))
				loc.bits |= (i + 1) << 12;
	    if(ctrlGetInputKey(WhKeys.Spell_prev, true)) 
	    	loc.bits |= (9)<<12;
	    if(ctrlGetInputKey(WhKeys.Spell_next, true)) 
	    	loc.bits |= (10)<<12; //1 << 16
	   
	    if(ctrlGetInputKey(WhKeys.Health_potion, true)) 
	    	loc.bits |= (1)<<16; 
	    if(ctrlGetInputKey(WhKeys.Strength_potion, true)) 
	    	loc.bits |= (2)<<16; // 1 << 17
	    if(ctrlGetInputKey(WhKeys.Curepoison_potion, true)) 
	    	loc.bits |= (3)<<16; 
	    if(ctrlGetInputKey(WhKeys.Fireresist_potion, true)) 
	    	loc.bits |= (4)<<16; // 1 << 18
	    if(ctrlGetInputKey(WhKeys.Invisibility_potion, true)) 
	    	loc.bits |= (5)<<16; 
	    if(ctrlGetInputKey(WhKeys.Inventory_Left, true)) 
	    	loc.bits |= (6)<<16;
	    if(ctrlGetInputKey(WhKeys.Inventory_Right, true)) 
	    	loc.bits |= (7)<<16;
	    loc.bits |=   ctrlGetInputKey(WhKeys.Inventory_Use, true)? CtrlInventory_Use : 0;
	    
	    loc.bits |=   whcfg.gMouseAim ? CtrlMouseAim : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Open, true)? CtrlOpen : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Turn_Around, true)? CtrlTurnAround : 0;
	    
	    loc.bits |=   ctrlGetInputKey(WhKeys.Fly_up, false)?CtrlFlyup:0;
	    loc.bits |=   ctrlGetInputKey(WhKeys.Fly_down, false)?CtrlFlydown:0;
	    loc.bits |=   ctrlGetInputKey(WhKeys.End_flying, true)?CtrlEndflying:0;
		
	    boolean running = ((!whcfg.gAutoRun && (loc.bits&CtrlRun) != 0) || ((loc.bits&CtrlRun) == 0 && whcfg.gAutoRun));
	    int keymove, turnamount;
	    
	    if (running)
	    {
	        turnamount = NORMALTURN<<1;
	        keymove = game.WH2 ? (WH2NORMALKEYMOVE << 1) : (NORMALKEYMOVE << 1);
	    }
	    else
	    {
	        turnamount = NORMALTURN;
	        keymove = game.WH2 ? WH2NORMALKEYMOVE : NORMALKEYMOVE;
	    }

	    boolean left = ctrlGetInputKey(GameKeys.Turn_Left, false);
		boolean right = ctrlGetInputKey(GameKeys.Turn_Right, false);

		if(whcfg.gMouseAim) {
			horiz = BClipRange(horiz - ctrlGetMouseLook(whcfg.gInvertmouse), -(YDIM >> 1), 100 + (YDIM >> 1));
        } else 
        	vel = BClipRange(vel - (int)ctrlGetMouseMove(), -MAXVEL, MAXVEL);
        
		if(!ctrlGetInputKey(GameKeys.Strafe, false)) {
			if (left) 
				angvel = (int) BClipLow(angvel - turnamount, -MAXANGVEL);
			if (right) 
				angvel = (int) BClipHigh(angvel + turnamount, MAXANGVEL);
			angvel = BClipRange(angvel + ctrlGetMouseTurn(), -1024, 1024);
		}
		else {
			if (left) 
				svel = BClipHigh(svel + keymove, MAXSVEL);
			if (right) 
				svel =  BClipLow(svel - keymove, -MAXSVEL);
			svel = (int) BClipRange(svel - (int) ctrlGetMouseStrafe(), -MAXSVEL, MAXSVEL);
		}
		
		Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);
		
		float lookx = stick1.x;
		float looky = stick1.y;
		if (whcfg.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 4.0f;
			horiz = BClipRange(horiz - k * looky * whcfg.gJoyLookSpeed / 65536f, -(ydim >> 1), 100 + (ydim >> 1));
		}

		if (lookx != 0) {
			float k = 80.0f;
			angvel = (short) BClipRange(angvel + k * lookx * whcfg.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0) {
			float k = keymove;
			vel = (short) BClipRange(vel - (k * stick2.y), -keymove, keymove);
		}
		if (stick2.x != 0) {
			float k = keymove;
			svel = (short) BClipRange(svel - (k * stick2.x), -keymove, keymove);
		}

		if (ctrlGetInputKey(GameKeys.Move_Forward, false))
			vel = BClipHigh(vel + keymove, MAXVEL);
		if (ctrlGetInputKey(GameKeys.Move_Backward, false))
			vel = BClipLow(vel - keymove, -MAXVEL);
		if (ctrlGetInputKey(GameKeys.Strafe_Left, false))
			svel = BClipHigh(svel + keymove, MAXSVEL);
		if (ctrlGetInputKey(GameKeys.Strafe_Right, false))
			svel = BClipLow(svel - keymove, -MAXSVEL);
		
		float k = 0.92f;
		float angle = BClampAngle(player[myconnectindex].ang);
		
		double xvel = (vel * BCosAngle(angle) + svel * BSinAngle(angle));
		double yvel = (vel * BSinAngle(angle) - svel * BCosAngle(angle));

		double len = Math.sqrt(xvel * xvel + yvel * yvel);
		if(len > (keymove << 14))
		{
			xvel = (xvel / len) * (keymove << 14);
			yvel = (yvel / len) * (keymove << 14);
		}
		
		lPlayerXVel += xvel * k;
		lPlayerYVel += yvel * k;

		lPlayerXVel = mulscale(lPlayerXVel, 0xD000, 16);
		lPlayerYVel = mulscale(lPlayerYVel, 0xD000, 16);

		if( klabs(lPlayerXVel) < 2048 && klabs(lPlayerYVel) < 2048 )
			lPlayerXVel = lPlayerYVel = 0;

		if (followmode && dimension == 2) {
			followvel = vel;
			followsvel = svel;
			followang = angvel;

			loc.fvel = 0;
	        loc.svel = 0;
	        loc.angvel = 0;
	        loc.horiz = 0;
	        return;
		}
		
		loc.fvel = lPlayerXVel; //vel;
	    loc.svel = lPlayerYVel; //svel;
	    loc.angvel = angvel;
	    loc.horiz = horiz;
	}

}
