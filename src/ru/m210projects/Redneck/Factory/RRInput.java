// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Factory.RRNetwork.kPacketMessage;
import static ru.m210projects.Redneck.Factory.RRNetwork.kPacketSound;
import static ru.m210projects.Redneck.Globals.MODE_TYPE;
import static ru.m210projects.Redneck.Globals.TICRATE;
import static ru.m210projects.Redneck.Globals.fricxv;
import static ru.m210projects.Redneck.Globals.fricyv;
import static ru.m210projects.Redneck.Globals.gamequit;
import static ru.m210projects.Redneck.Globals.multiflag;
import static ru.m210projects.Redneck.Globals.multipos;
import static ru.m210projects.Redneck.Globals.multiwhat;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.tempbuf;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Types.RTS.RTS_GetSound;
import static ru.m210projects.Redneck.Types.RTS.RTS_NumSounds;
import static ru.m210projects.Redneck.Types.RTS.rtsplaying;
import static ru.m210projects.Redneck.View.FTA;
import static ru.m210projects.Redneck.View.adduserquote;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.Loader.WAVLoader;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Redneck.Config.RRKeys;
import ru.m210projects.Redneck.Input;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.VOC;

public class RRInput extends BuildControls {
	
	public final int TURBOTURNTIME = (TICRATE/8); // 7
	public final int NORMALTURN  = 15;
	public final int PREAMBLETURN = 5;
	public final int NORMALKEYMOVE = 40;
	public final int MAXVEL     =  ((NORMALKEYMOVE*2)+10);
	public final int MAXSVEL     = ((NORMALKEYMOVE*2)+10);
	public static final int MAXANGVEL  =  127;
	public static final int MAXHORIZ   =  127;
	
	public int turnheldtime; //MED
	private int lastcontroltime; //MED

	private short vel, svel;
	private float horiz, angvel;

	public RRInput(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
		
		GameKeys.Run.setName("Run / Handbrake");
	}
	
	@Override
	public float getScaleX()
	{
		return 1 / 2.0f;
	}
	
	@Override
	public float getScaleY()
	{
		return 1.0f;
	}

	@Override
	public void ctrlGetInput(NetInput input) {
		float daang;
	    int tics;
	    boolean running;
	    int turnamount;
	    int keymove;

	    Input loc = (Input) input;
	    loc.Reset();
	    
	    if( game.menu.gShowMenu || Console.IsShown() || MODE_TYPE /*|| (game.gPaused && !ctrlKeyStatus(KEY_PAUSE))*/ )
	    {
	    	loc.fvel = vel = 0;
	    	loc.svel = svel = 0;
	    	loc.avel = angvel = 0;
	    	loc.horz = horiz = 0;
	    	loc.bits = ((gamequit)<<26);
	    	
	    	loc.fvel += fricxv;
	    	loc.svel += fricyv;

	    	if(Console.IsShown())
	    		MODE_TYPE = false;
	         
	    	if(MODE_TYPE)
	    	{
	    		int out = getInput().putMessage(getInput().getMessageBuffer().length, false, false, true);
	 	    	if(out != 0) MODE_TYPE = false;
	 	    	if(out == 1) { 
	 	    		game.net.SendMessage(numplayers, getInput().getMessageBuffer(), getInput().getMessageLength());
	 	    	}
	    	}

	    	return;
	    }

	    if(multiflag == 1)
	    {
	        loc.bits =   1<<17;
	        loc.bits |=   multiwhat<<18;
	        loc.bits |=   multipos<<19;
	        multiflag = 0;
	        return;
	    }
	    
	    if(ctrlKeyStatus(Keys.SHIFT_LEFT) || ctrlKeyStatus(Keys.CONTROL_LEFT))
		{
	    	int fkey = -1;
			for(int i = 0; i < 10; i++)
			{
				if(ctrlKeyStatusOnce(i + Keys.F1)) {
					fkey = i;
					break;
				}
			}

			if(!cfg.noSound && ( RTS_NumSounds() > 0 ) && rtsplaying == 0 && cfg.VoiceToggle)
			{
				if(fkey >= 0) {
					if(ctrlKeyStatus(Keys.CONTROL_LEFT)) {
						byte[] rtsptr = RTS_GetSound(fkey);
						if(rtsptr != null && rtsptr.length > 0) {
							if (rtsptr[0] == 'C') {
					    		VOC voc = new VOC(rtsptr);
					    		Source voice = BuildGdx.audio.newSound(voc.sampledata, voc.samplerate, voc.samplesize, 255);
					    		if(voice != null)
					    		{
					    			voice.setGlobal(1);
					    			voice.play(1.0f); 
					    		}
					    	}
					    	else {
								try {
									WAVLoader wav = new WAVLoader(rtsptr);
									Source voice = BuildGdx.audio.newSound(wav.data, wav.rate, wav.bits, 255);
									if(voice != null)
									{
										voice.setGlobal(1);
										voice.play(1.0f); 
									}
								} catch (Exception e) {}
					    	}
							
							rtsplaying = 7;
							
							if(ud.multimode > 1)
			                {
			                    tempbuf[0] = kPacketSound;
			                    tempbuf[1] = (byte) fkey;
			                    		
			                    game.net.sendtoall(tempbuf, 2);
			                }
						}
					}
					
					if(ctrlKeyStatus(Keys.SHIFT_LEFT))
					{
						adduserquote(ud.ridecule[fkey]);

		                if(ud.multimode > 1) 
		                {
			                tempbuf[0] = kPacketMessage;
							tempbuf[1] = (byte) 255;
			                tempbuf[2] = 0;
			                
			                for(int i = 0; i < ud.ridecule[fkey].length; i++)
			                	tempbuf[2 + i] = (byte) ud.ridecule[fkey][i];

			                game.net.sendtoall(tempbuf, 2 + ud.ridecule[fkey].length);
		                }
					}
					return;
				}
			}
		}

	    tics = totalclock-lastcontroltime;
	    lastcontroltime = totalclock;

	    PlayerStruct p = ps[myconnectindex];
	    
	    svel = vel = 0;
	    horiz = angvel = 0;
	    
	    if ( p.OnMotorcycle ) {
	    	motoinput(loc, p, tics);
	    	return;
	    }
	    
	    if(p.OnBoat) {
	    	boatinput(loc, p, tics);
	    	return;
	    }

	    if(ctrlGetInputKey(GameKeys.Mouse_Aiming, true))
	    {
	    	cfg.gMouseAim = !cfg.gMouseAim;
    		FTA(44+(cfg.gMouseAim?1:0),p);
	    }

	    loc.bits =   ctrlGetInputKey(GameKeys.Jump, false)?1:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Crouch, false)?2:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Aim_Up, false)?8:0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Aim_Down, false)?16:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Run, false)?32:0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Tilt_Right, false)?128:0;
	    
	    if ( p.alcohol_amount <= 88 )
	    	loc.bits |= ctrlGetInputKey(RRKeys.Tilt_Left, false)?64:0;
	    else loc.bits = loc.bits | 64;
	    
	    if ( p.alcohol_amount > 99 )
	    	loc.bits |= 1 << 14;
	   
	    //if(!ctrlKeyStatus(Keys.ALT_LEFT) && !ctrlKeyStatus(Keys.SHIFT_LEFT)) {
		    for(int i = 0; i < 10; i++) 
				if(ctrlGetInputKey(cfg.keymap[i + RRKeys.Weapon_1.getNum()], true)) 
					loc.bits |= (i + 1)<<8;
	    //}

	    if(ctrlGetInputKey(GameKeys.Previous_Weapon, true)) 
	    	loc.bits |= (11)<<8;
	    if(ctrlGetInputKey(GameKeys.Next_Weapon, true)) 
	    	loc.bits |= (12)<<8;
	    if(ctrlGetInputKey(RRKeys.Last_Weap_Switch, true)) 
	    	loc.bits |= (13)<<8;
	    
	    loc.bits |=   ctrlGetInputKey(RRKeys.Moonshine, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Look_Up, false)? 1 << 13 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Look_Down, false)? 1 << 14 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Yeehaa, false)? 1 << 15 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Wiskey, false)? 1 << 16 : 0; 
	    loc.bits |=   ctrlGetInputKey(RRKeys.Aim_Center, false)? 1 << 18 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Holster_Weapon, false)? 1 << 19 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   ctrlKeyStatusOnce(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Quick_pee, false)? 1 << 22 : 0;
	    loc.bits |=   cfg.gMouseAim? 1 << 23 : 0;
	    
	    loc.bits |=   ctrlGetInputKey(RRKeys.Beer, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Cowpie, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Turn_Around, false)? 1 << 28 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Use, false)? 1 << 30 : 0;
//	    loc.bits |=   ctrlKeyStatusOnce(Keys.ESCAPE)? 1 << 31 : 0;
	    
	    if((loc.bits&2) != 0) p.crouch_toggle = 0;
	    
	    boolean CrouchMode = ctrlGetInputKey(RRKeys.Crouch_toggle, sector[p.cursectnum].lotag != 2);
	    if(sector[p.cursectnum].lotag == 2) {
	    	p.crouch_toggle = CrouchMode ? (byte) 1 : 0;
		} else if(CrouchMode) p.crouch_toggle ^= 1;
	    
	    if(p.crouch_toggle == 1)
	    	loc.bits |= 2;
	    
	    running = ((ud.auto_run == 0 && (loc.bits&32) != 0) || ((loc.bits&32) == 0 && ud.auto_run != 0));

	    if (running)
	    {
	        turnamount = NORMALTURN<<1;
	        keymove = NORMALKEYMOVE<<1;
	    }
	    else
	    {
	        turnamount = NORMALTURN;
	        keymove = NORMALKEYMOVE;
	    }

	    if (ctrlGetInputKey(GameKeys.Strafe, false))
	    {
	        if ( ctrlGetInputKey(GameKeys.Turn_Left, false))
	           svel -= -keymove;
	        if ( ctrlGetInputKey(GameKeys.Turn_Right, false))
	           svel -= keymove; 
	        svel = (short) BClipRange(svel - (int) 20 * ctrlGetMouseStrafe(), -keymove, keymove);
	    }
	    else
	    {
	        if ( ctrlGetInputKey(GameKeys.Turn_Left, false) ) {
	           turnheldtime += tics;
	           if (turnheldtime>=TURBOTURNTIME)
	              angvel -= turnamount;
	           else
	              angvel -= PREAMBLETURN;  
	        }
	        else if ( ctrlGetInputKey(GameKeys.Turn_Right, false) )
	        {
	           turnheldtime += tics;
	           if (turnheldtime>=TURBOTURNTIME)
	              angvel += turnamount;
	           else
	              angvel += PREAMBLETURN;
	        } else turnheldtime=0;
	        angvel = BClipRange(angvel + ctrlGetMouseTurn(), -1024, 1024);  
	    }

	    if ( ctrlGetInputKey(GameKeys.Strafe_Left, false) )
	        svel += keymove;
	    if ( ctrlGetInputKey(GameKeys.Strafe_Right, false) )
	        svel += -keymove;

	    if ( p.alcohol_amount < 66 || p.alcohol_amount > 87)
	    {
		    if ( ctrlGetInputKey(GameKeys.Move_Forward, false) )
		        vel += keymove;
		    if ( ctrlGetInputKey(GameKeys.Move_Backward, false) )
		        vel += -keymove;
	    } else {
	    	if ( ctrlGetInputKey(GameKeys.Move_Forward, false) ) 
	    	{
		        vel += keymove;
		        if ( (p.alcohol_amount & 1) != 0 )
		        	svel += keymove;
		        else svel -= keymove;
	    	}
		    if ( ctrlGetInputKey(GameKeys.Move_Backward, false) ) 
		    {
		        vel += -keymove;
		        if ( (p.alcohol_amount & 1) != 0 )
		        	svel -= keymove;
		        else svel += keymove;
		    }
	    }

	    if(cfg.gMouseAim) {
			horiz = BClipRange(ctrlGetMouseLook(!cfg.gInvertmouse), -(ydim>>1), 100+(ydim>>1));
        } else 
        	vel =  (short) BClipRange(vel - ctrlGetMouseMove(), -4 * keymove, 4 * keymove);
	    
	    Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);
		
		float lookx = stick1.x;
		float looky = stick1.y;
		if (cfg.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 8.0f;
			horiz = BClipRange(horiz - k * looky * cfg.gJoyLookSpeed / 65536f, -(ydim>>1), 100+(ydim>>1));
		}

		if (lookx != 0) {
			float k = 8.0f;
			angvel = BClipRange(angvel + k * lookx * cfg.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0) {
			vel = (short) BClipRange(vel - (keymove * stick2.y), -4 * keymove, 4 * keymove);
		}
		if (stick2.x != 0) {
			svel = (short) BClipRange(svel - (keymove * stick2.x), -4 * keymove, 4 * keymove);
		}

	    if(vel < -MAXVEL) vel = -MAXVEL;
	    if(vel > MAXVEL) vel = MAXVEL;
	    if(svel < -MAXSVEL) svel = -MAXSVEL;
	    if(svel > MAXSVEL) svel = MAXSVEL;
	    if(angvel < -MAXANGVEL) angvel = -MAXANGVEL;
	    if(angvel > MAXANGVEL) angvel = MAXANGVEL;
	    if(horiz < -MAXHORIZ) horiz = -MAXHORIZ;
	    if(horiz > MAXHORIZ) horiz = MAXHORIZ;

	    if(ud.scrollmode && ud.overhead_on != 0)
	    {
	        ud.folfvel = vel;
	        ud.folavel = angvel;
	        loc.fvel = 0;
	        loc.svel = 0;
	        loc.avel = 0;
	        loc.horz = 0;
	        return;
	    }

	    daang = p.ang;

	    int momx = (int) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
	    int momy = (int) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

	    momx += (int) (svel * BSinAngle(BClampAngle(daang )) / 512.0f);
	    momy += (int) (svel * BCosAngle(BClampAngle(daang + 1024)) / 512.0f);

	    momx += fricxv;
	    momy += fricyv;

	    loc.fvel = (short) momx;
	    loc.svel = (short) momy;
	    loc.avel = angvel;
	    loc.horz = horiz;
	}
	
	public void motoinput(Input loc, PlayerStruct p, int tics)
	{
	    loc.bits =   ctrlGetInputKey(GameKeys.Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Moonshine, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Yeehaa, false)? 1 << 15 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Wiskey, false)? 1 << 16 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   getInput().keyStatus(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Beer, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Cowpie, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Use, false)? 1 << 30 : 0;

	    boolean left = ctrlGetInputKey(GameKeys.Turn_Left, false) || ctrlGetInputKey(GameKeys.Strafe_Left, false);
	    boolean right = ctrlGetInputKey(GameKeys.Turn_Right, false) || ctrlGetInputKey(GameKeys.Strafe_Right, false);
	    int bike_turn = 0;
	    
	    Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);

	    if ( stick1.x != 0 && stick1.x < 0.5f ) left = true;
	    if ( stick1.x != 0 && stick1.x > 0.5f ) right = true;
	    
	    if ( stick2.y != 0 && stick2.y < 0.5f ) loc.bits |= 1;
	    if ( stick2.y != 0 && stick2.y > 0.5f ) loc.bits |= 8;

	    if ( p.CarVar1 == 0 )
	    {
	    	loc.bits |=   ctrlGetInputKey(GameKeys.Move_Forward, false)?1:0;
	    	loc.bits |=   ctrlGetInputKey(GameKeys.Run, false)?2:0;
	    	loc.bits |=   ctrlGetInputKey(GameKeys.Move_Backward, false)?8:0;
	    }
	    
	    if(left) loc.bits |= 16;
	    if(right) loc.bits |= 64;
	    
	    boolean revers = (p.CarSpeed <= 0);
	    if ( p.CarSpeed != 0 && p.on_ground )
	    {
	    	if ( left || p.CarVar2 < 0 )
	        {
	    		turnheldtime += tics;
//	    		p.TiltStatus = BClipLow(p.TiltStatus-1, -10);
	    		if ( turnheldtime >= 15 && p.CarSpeed > 0 )
	            {
	    			if ( bike_turn != 0 )
	                	angvel -= 20;
	                else angvel -= 10; 
	            }
	    		else {
	    			if ( bike_turn != 0 )
		    			angvel -= 10 * (revers ? -1 : 1);
		            else angvel -= 3 * (revers ? -1 : 1);
	    		}
	        }
	    	else if ( right || p.CarVar2 > 0 )
	    	{
	    		turnheldtime += tics;
//	    		p.TiltStatus = BClipHigh(p.TiltStatus+1, 10);
	    		if ( turnheldtime >= 15 && p.CarSpeed > 0 )
	            {
	    			if ( bike_turn != 0 )
	                	angvel += 20;
	                else angvel += 10; 
	            }
	    		else {
	    			if ( bike_turn != 0 )
		    			angvel += 10 * (revers ? -1 : 1);
		            else angvel += 3 * (revers ? -1 : 1);
	    		}
	    	}
	    	else
	        {
	    		turnheldtime = 0;
			}
	    }

		vel += p.CarSpeed;
	    if ( vel < -15 ) vel = -15;
	    if ( vel > 120 ) vel = 120;
	    if(angvel < -MAXANGVEL) angvel = -MAXANGVEL;
	    if(angvel > MAXANGVEL) angvel = MAXANGVEL;

	    if(ud.scrollmode && ud.overhead_on != 0)
	    {
	        ud.folfvel = vel;
	        ud.folavel = angvel;
	        loc.fvel = 0;
	        loc.svel = 0;
	        loc.avel = 0;
	        loc.horz = 0;
	        return;
	    }

	    float daang = p.ang;
	    
	    short momx = (short) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
	    short momy = (short) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

	    momx += fricxv;
	    momy += fricyv;

	    loc.fvel = momx;
	    loc.svel = momy;

	    loc.avel = angvel;
	    loc.horz = horiz;
	    
	    if(p.CarSpeed < 80) 
	    	loc.carang = (byte) BClipRange(ctrlGetMouseTurn(), -127, 127);
	}
	
	public void boatinput(Input loc, PlayerStruct p, int tics)
	{
	    loc.bits =   ctrlGetInputKey(GameKeys.Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Moonshine, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Yeehaa, false)? 1 << 15 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Wiskey, false)? 1 << 16 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   getInput().keyStatus(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Beer, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Cowpie, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(RRKeys.Inventory_Use, false)? 1 << 30 : 0;

	    boolean left = ctrlGetInputKey(GameKeys.Turn_Left, false) || ctrlGetInputKey(GameKeys.Strafe_Left, false);
	    boolean right = ctrlGetInputKey(GameKeys.Turn_Right, false) || ctrlGetInputKey(GameKeys.Strafe_Right, false);
	    int bike_turn = 0; 
	    Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);

		if ( stick1.x != 0 && stick1.x < 0.5f ) left = true;
	    if ( stick1.x != 0 && stick1.x > 0.5f ) right = true;
	    
	    if ( stick2.y != 0 && stick2.y < 0.5f ) loc.bits |= 1;
	    if ( stick2.y != 0 && stick2.y > 0.5f ) loc.bits |= 8;
	    
	    if ( p.CarVar1 == 0 )
	    {
	    	loc.bits |=   ctrlGetInputKey(GameKeys.Move_Forward, false)?1:0;
	    	loc.bits |=   ctrlGetInputKey(GameKeys.Run, false)?2:0;
	    	loc.bits |=   ctrlGetInputKey(GameKeys.Move_Backward, false)?8:0;
	    }
	    
	    if(left) loc.bits |= 16;
	    if(right) loc.bits |= 64;

	    if ( p.CarSpeed != 0 )
	    {
	    	if ( left || p.CarVar2 < 0 )
	        {
	    		turnheldtime += tics;
				if ( turnheldtime >= 15 && p.CarSpeed != 0 )
	            {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel -= 6;
	    				else angvel -= 3; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel -= 20;
		                else angvel -= 10; 
	    			}
	            }
	    		else if ( turnheldtime < 15 && p.CarSpeed != 0 ) {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel -= 2;
	    				else angvel--; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel -= 6;
		                else angvel -= 3; 
	    			}
	    		}
	        }
	    	else if ( right || p.CarVar2 > 0 )
	    	{
	    		turnheldtime += tics;
				if ( turnheldtime >= 15 && p.CarSpeed != 0 )
	            {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel += 6;
	    				else angvel += 3; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel += 20;
		                else angvel += 10; 
	    			}
	            }
	    		else if ( turnheldtime < 15 && p.CarSpeed != 0 ) {
	    			if ( p.NotOnWater != 0)
	    			{
	    				if ( bike_turn != 0 )
	    					angvel += 2;
	    				else angvel++; 
	    			} else {
		    			if ( bike_turn != 0 )
		                	angvel += 6;
		                else angvel += 3; 
	    			}
	    		}
	    	}
	    	else if ( p.NotOnWater == 0)
	        {
	    		turnheldtime = 0;
			}
	    }

		vel += p.CarSpeed;
	    if ( vel < -15 ) vel = -15;
	    if ( vel > 120 ) vel = 120;
	    if(angvel < -MAXANGVEL) angvel = -MAXANGVEL;
	    if(angvel > MAXANGVEL) angvel = MAXANGVEL;

	    if(ud.scrollmode && ud.overhead_on != 0)
	    {
	        ud.folfvel = vel;
	        ud.folavel = angvel;
	        loc.fvel = 0;
	        loc.svel = 0;
	        loc.avel = 0;
	        loc.horz = 0;
	        return;
	    }

	    float daang = p.ang;
	    
	    short momx = (short) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
	    short momy = (short) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

	    momx += fricxv;
	    momy += fricyv;

	    loc.fvel = momx;
	    loc.svel = momy;

	    loc.avel = angvel;
	    loc.horz = horiz;
	    
	    if(p.CarSpeed < 80) {
	    	loc.carang = (byte) BClipRange(ctrlGetMouseTurn(), -127, 127);
	    }
	}
}
