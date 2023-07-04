// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.Cheats.*;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Screen.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Blood.Config.BloodKeys;
import ru.m210projects.Blood.Types.INPUT;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;

public class BloodInput extends BuildControls {

	private int turnAccel = 0;
	
	public BloodInput(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
	}

	@Override
	public void ctrlGetInput(NetInput input) {
		INPUT gInput = (INPUT) input;
		
		gInput.Reset();
		
		if ( gTextInput ) 
	    {
			int textInput = getInput().putMessage(getInput().getMessageBuffer().length, true, false, true);
	    	if(textInput != 0) gTextInput = false;
			if(textInput == 1) {
	    		char[] lockeybuf = getInput().getMessageBuffer();
	    		String message = new String(lockeybuf, 0, getInput().getMessageLength());
	    		int i = 0;
	    		while(i < lockeybuf.length && lockeybuf[i] != 0)
	    			lockeybuf[i++] += 1;
	    		String cheat = new String(lockeybuf, 0, getInput().getMessageLength()).toUpperCase();

	    		int ep = -1, lvl = -1;
	    		boolean wrap1 = false;
	    		boolean wrap2 = false;
	    		
	    		if(pGameInfo.nGameType == kNetModeOff)
	    		{
					boolean isMario = false;
		    		if(cheat.startsWith(cheatCode[17]) || cheat.startsWith(cheatCode[18]) || cheat.startsWith(cheatCode[35]))
		    		{
						isMario = true;

		    			i = 0;
		    			while(i < message.length() && message.charAt(i) != 0 && message.charAt(i) != ' ') i++;
		    			cheat = cheat.substring(0, i);
		    			message = message.replaceAll("[\\s]{2,}", " ");
			    		int startpos = ++i;
			    		while(i < message.length() && message.charAt(i) != 0 && message.charAt(i) != ' ')
			    			i++;
			    		if(i <= message.length()) {
				    		String nEpisode = message.substring(startpos, i);
				    		nEpisode = nEpisode.replaceAll("[^0-9]", ""); 
				    		if(!nEpisode.isEmpty()) {
				    			try {
									ep = Integer.parseInt(nEpisode);
									wrap1 = true;
									startpos = ++i;
						    		while(i < message.length() && message.charAt(i) != 0 && message.charAt(i) != ' ')
						    			i++;
						    		if(i <= message.length()) {
							    		String nLevel = message.substring(startpos, i);
							    		nLevel = nLevel.replaceAll("[^0-9]", ""); 
							    		if(!nLevel.isEmpty()) {
											lvl = Integer.parseInt(nLevel);
											wrap2 = true;
							    		}
						    		}
				    			} catch(Exception ignored) { }
				    		}
			    		}
		    		}
	
		    		if(wrap1)
		    		{
		    			if(wrap2) {
		    				if(!IsCheatCode(cheat, ep, lvl))
			    				viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
		    			} 
		    			else
		    			{
		    				if(!IsCheatCode(cheat, ep))
			    				viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
		    			}
		    		} else {
						if(isMario) {
							viewSetMessage("mario <level> or <episode> <level>", gPlayer[gViewIndex].nPlayer);
						} else if(!IsCheatCode(cheat))
		    				viewSetMessage(message, gPlayer[gViewIndex].nPlayer);
		    		}
	    		} 
	    		else
	    		{
	    			game.net.SendNetMessage(message);
	    			viewSetMessage(message, -1);
	    		}
	    	}
			return;
	    }

		if ( Console.IsShown() || game.menu.gShowMenu )
			 return;
	
		if(pGameInfo.nGameType > 0)
		{
			if(ctrlKeyStatus(Keys.ALT_LEFT) || ctrlKeyStatus(Keys.SHIFT_LEFT))
			{
				int fkey = -1;
				for(int i = 0; i < 10; i++)
					if(ctrlKeyStatusOnce(i + Keys.NUM_1))
					{
						fkey = i;
						break;
					}

				if(ctrlKeyStatus(Keys.ALT_LEFT) && fkey != -1)
					game.net.TauntSound(fkey);
				if(ctrlKeyStatus(Keys.SHIFT_LEFT) && fkey != -1)
					game.net.TauntMessage(fkey);
				return;
			}
		}
		
		if(ctrlGetInputKey(BloodKeys.AutoRun, true))
		{
			if(!cfg.gAutoRun) {
        		cfg.gAutoRun = true;
        		viewSetMessage("Auto run On", gPlayer[gViewIndex].nPlayer);
        	} else
        	{
        		cfg.gAutoRun = false;
        		viewSetMessage("Auto run Off", gPlayer[gViewIndex].nPlayer);
        	}
		}
		
		if(gViewMode == kView2DIcon || gViewMode == kView2D) {
			if(ctrlGetInputKey(BloodKeys.Map_Follow_Mode, true))
			{
				gMapScrollMode = !gMapScrollMode;
			}
		}

		if(ctrlGetInputKey(GameKeys.Map_Toggle, true)) 
			scrSetView(gViewMode);
		if(ctrlGetInputKey(GameKeys.Next_Weapon, true))
			gInput.NextWeapon = true;
		if(ctrlGetInputKey(GameKeys.Previous_Weapon, true)) 
			gInput.PrevWeapon = true;
		
		gInput.Jump = ctrlGetInputKey(GameKeys.Jump, false);
		gInput.Crouch = ctrlGetInputKey(GameKeys.Crouch, false);
		gInput.Shoot = ctrlGetInputKey(GameKeys.Weapon_Fire, false); 
		gInput.AltShoot = ctrlGetInputKey(BloodKeys.Weapon_Special_Fire, false);
		gInput.Run = ctrlGetInputKey(GameKeys.Run, false); 
		gInput.TurnAround = ctrlGetInputKey(GameKeys.Turn_Around, true); 
		gInput.Use = ctrlGetInputKey(GameKeys.Open, true); 
		gInput.InventoryLeft = ctrlGetInputKey(BloodKeys.Inventory_Left, true); 
		gInput.InventoryRight = ctrlGetInputKey(BloodKeys.Inventory_Right, true);
		gInput.InventoryUse = ctrlGetInputKey(BloodKeys.Inventory_Use, true); 
		gInput.HolsterWeapon = ctrlGetInputKey(BloodKeys.Holster_Weapon, true); 
		gInput.CrouchMode = ctrlGetInputKey(BloodKeys.Crouch_toggle,  gMe.moveState != kMoveSwim);
		gInput.LastWeapon = ctrlGetInputKey(BloodKeys.Last_Used_Weapon, true);

		for(int i = 0; i < 10; i++) 
			if(ctrlGetInputKey(cfg.keymap[i + BloodKeys.Weapon_1.getNum()], true)) 
				gInput.newWeapon = i + 1;

		if(ctrlGetInputKey(BloodKeys.See_Chase_View, true))
			gViewPos ^= 1;
		
		if(ctrlGetInputKey(BloodKeys.Show_Opponents_Weapon, true))
			cfg.gShowWeapon = !cfg.gShowWeapon;
		
		if(ctrlGetInputKey(BloodKeys.See_Coop_View, true))
		{
			if(numplayers > 1 && (pGameInfo.nGameType == kNetModeCoop || pGameInfo.nGameType == kNetModeTeams) && !kFakeMultiplayer)
			{
				if(pGameInfo.nGameType == kNetModeCoop) {
					gViewIndex = connectpoint2[gViewIndex];
					if ( gViewIndex == -1 )
						gViewIndex = connecthead;
				} 
				else 
				{
					int index = gViewIndex;
					do
					{
						gViewIndex = connectpoint2[gViewIndex];
						if ( gViewIndex == -1 )
							gViewIndex = connecthead ;
						if ( index == gViewIndex )
							break;
				        if ( gMe.teamID == gPlayer[gViewIndex].teamID )
				        	break;
					}
					while ( index != gViewIndex );
				}

//				do
//				{
//					gViewIndex++;
//					if (gViewIndex >= gNetPlayers) 
//						gViewIndex = 0;
//					if(pGameInfo.nGameType == kNetModeCoop 
//							|| gPlayer[gViewIndex].teamID == gMe.teamID)
//						break;
//				}
//				while(true);
			}
		}
		
		gInput.UseBeastVision = ctrlGetInputKey(BloodKeys.BeastVision, true); 
		gInput.UseCrystalBall = ctrlGetInputKey(BloodKeys.CrystalBall, true); 
		gInput.UseJumpBoots = ctrlGetInputKey(BloodKeys.JumpBoots, true); 
		gInput.UseMedKit = ctrlGetInputKey(BloodKeys.MedKit, true); 
		
		if(ctrlGetInputKey(BloodKeys.ProximityBombs, true))
			gInput.newWeapon = 11;
		if(ctrlGetInputKey(BloodKeys.RemoteBombs, true))
			gInput.newWeapon = 12;

		boolean up = ctrlGetInputKey(GameKeys.Move_Forward, false); 
		boolean down = ctrlGetInputKey(GameKeys.Move_Backward, false); 
		int run = ((!cfg.gAutoRun && gInput.Run) || (!gInput.Run && cfg.gAutoRun)) ? 1:0;
		
		if ( up )
	    	gInput.Forward += kFrameTicks * (run + 1);
	    if ( down )
	        gInput.Forward -= kFrameTicks * (run + 1);
	    
	    boolean strafe = ctrlGetInputKey(GameKeys.Strafe, false); 
	    boolean sleft = ctrlGetInputKey(GameKeys.Strafe_Left, false); 
	    boolean sright = ctrlGetInputKey(GameKeys.Strafe_Right, false); 
	    
	    if ( sleft )
        	gInput.Strafe += kFrameTicks * (run + 1);
        if ( sright )
        	gInput.Strafe -= kFrameTicks * (run + 1);
	        
		boolean left = ctrlGetInputKey(GameKeys.Turn_Left, false);
		boolean right = ctrlGetInputKey(GameKeys.Turn_Right, false);
		
		if(left || right)
			turnAccel += kFrameTicks;
		else turnAccel = 0;
		
		if(!strafe) {
			if ( left )
	        	gInput.Turn -= ClipHigh(turnAccel * 12, cfg.gTurnSpeed);
	        if ( right )
	        	gInput.Turn += ClipHigh(turnAccel * 12, cfg.gTurnSpeed);
	        gInput.Turn = BClipRange(gInput.Turn + ctrlGetMouseTurn(), -1024, 1024);
		}
		else
		{
			if ( left )
				gInput.Strafe += kFrameTicks * (run + 1);
	        if ( right )
	        	gInput.Strafe -= kFrameTicks * (run + 1);
	        gInput.Strafe = BClipRange(gInput.Strafe - (int) ctrlGetMouseStrafe(), -kFrameTicks * (run + 1), kFrameTicks * (run + 1));
		}
        
        if ( (gInput.Run || cfg.gAutoRun) && turnAccel > 24 )
            gInput.Turn *= 2;

        if(ctrlGetInputKey(GameKeys.Mouse_Aiming, true))
        {
        	if(!cfg.gMouseAim) {
        		cfg.gMouseAim = true;
        		viewSetMessage("Mouse aiming on", gPlayer[gViewIndex].nPlayer);
        	} else
        	{
        		cfg.gMouseAim = false;
        		gInput.LookCenter = true;
        		viewSetMessage("Mouse aiming off", gPlayer[gViewIndex].nPlayer);
        	}
        }
        
        if(ctrlGetInputKey(BloodKeys.Toggle_Crosshair, true))
        {
        	if(!cfg.gCrosshair) {
        		cfg.gCrosshair = true;
        		viewSetMessage("Crosshair on", gPlayer[gViewIndex].nPlayer);
        	} else
        	{
        		cfg.gCrosshair = false;
        		viewSetMessage("Crosshair off", gPlayer[gViewIndex].nPlayer);
        	}
        }
        
        gInput.LookLeft = ctrlGetInputKey(BloodKeys.Tilt_Left, false);
		gInput.LookRight = ctrlGetInputKey(BloodKeys.Tilt_Right, false);
       
        gInput.Lookup = false;
    	gInput.Lookdown = false;
    	if(ctrlGetInputKey(BloodKeys.Aim_Up, false))
    		gInput.Lookup = true;
    	if(ctrlGetInputKey(BloodKeys.Aim_Down, false))
    		gInput.Lookdown = true;
    	if(ctrlGetInputKey(GameKeys.Look_Up, false))
    	{
    		gInput.Lookup = true;
    		gInput.LookCenter = true;
    	}
    	
    	if(ctrlGetInputKey(GameKeys.Look_Down, false))
    	{
    		gInput.Lookdown = true;
    		gInput.LookCenter = true;
    	}
    	if(ctrlGetInputKey(BloodKeys.Aim_Center, true))
    		gInput.LookCenter = true;

        if(cfg.gMouseAim && gViewMode != kView2DIcon) {
        	gInput.mlook = BClipRange(ctrlGetMouseLook(!cfg.gInvertmouse), -177, 178);
        } else 
            gInput.Forward =  ClipRange(gInput.Forward - (int)ctrlGetMouseMove(), -kFrameTicks * (run + 1), kFrameTicks * (run + 1));
        
        
        Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);
		
		float lookx = stick1.x;
		float looky = stick1.y;
		if (cfg.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 1.5f;
			gInput.mlook = BClipRange(gInput.mlook - k * looky * cfg.gJoyLookSpeed / 65536f, -177, 178);
		}

		if (lookx != 0) {
			float k = 64;
			gInput.Turn = BClipRange(gInput.Turn + k * lookx * cfg.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0) {
			gInput.Forward = (short) BClipRange(gInput.Forward - (20.0f * stick2.y), -kFrameTicks * (run + 1), kFrameTicks * (run + 1));
		}
		if (stick2.x != 0) {
			gInput.Strafe = (short) BClipRange(gInput.Strafe - (20.0f * stick2.x), -kFrameTicks * (run + 1), kFrameTicks * (run + 1));
		}

        if(ctrlKeyStatusOnce(KEY_PAUSE))
     		gInput.Pause  = !gInput.Pause;
        
        if(ctrlGetInputKey(GameKeys.Send_Message, false))
    	{
        	gTextInput = true;
        	getInput().initMessageInput(null);
    	}
        
        if( ( gViewMode == kView2DIcon || gViewMode == kView2D ) && gMapScrollMode) {
        	scrollOX = scrollX;
        	scrollOY = scrollY;
        	scrollOAng = scrollAng;
        	
			if( gInput.Forward != 0 ) {
				scrollX += mulscale(Cos(scrollAng), gInput.Forward, 24);
				scrollY += mulscale(Sin(scrollAng), gInput.Forward, 24);
				gInput.Forward = 0;
			}
			if( gInput.Strafe != 0 ) {
				scrollX += mulscale(Sin(scrollAng), gInput.Strafe, 24);
				scrollY -= mulscale(Cos(scrollAng), gInput.Strafe, 24);
				gInput.Strafe = 0;
			}
			if ( gInput.Turn != 0 ) {
				scrollAng = (short) ((scrollAng + (kFrameTicks * (int) gInput.Turn >> 4)) & kAngleMask);
				gInput.Turn = 0;
			}
		}
	}

}
