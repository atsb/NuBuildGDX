// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Factory;

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
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Duke3D.Factory.DukeNetwork.kPacketMessage;
import static ru.m210projects.Duke3D.Factory.DukeNetwork.kPacketSound;
import static ru.m210projects.Duke3D.Globals.MODE_TYPE;
import static ru.m210projects.Duke3D.Globals.TICRATE;
import static ru.m210projects.Duke3D.Globals.currentGame;
import static ru.m210projects.Duke3D.Globals.fricxv;
import static ru.m210projects.Duke3D.Globals.fricyv;
import static ru.m210projects.Duke3D.Globals.gamequit;
import static ru.m210projects.Duke3D.Globals.multiflag;
import static ru.m210projects.Duke3D.Globals.multipos;
import static ru.m210projects.Duke3D.Globals.multiwhat;
import static ru.m210projects.Duke3D.Globals.musiclevel;
import static ru.m210projects.Duke3D.Globals.musicvolume;
import static ru.m210projects.Duke3D.Globals.ps;
import static ru.m210projects.Duke3D.Globals.tempbuf;
import static ru.m210projects.Duke3D.Globals.ud;
import static ru.m210projects.Duke3D.Main.cfg;
import static ru.m210projects.Duke3D.Main.game;
import static ru.m210projects.Duke3D.Sounds.sndPlayMusic;
import static ru.m210projects.Duke3D.Types.RTS.RTS_GetSound;
import static ru.m210projects.Duke3D.Types.RTS.RTS_NumSounds;
import static ru.m210projects.Duke3D.Types.RTS.rtsplaying;
import static ru.m210projects.Duke3D.View.FTA;
import static ru.m210projects.Duke3D.View.adduserquote;
import static ru.m210projects.Duke3D.View.fta;
import static ru.m210projects.Duke3D.View.ftq;

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
import ru.m210projects.Duke3D.Config.DukeKeys;
import ru.m210projects.Duke3D.Input;
import ru.m210projects.Duke3D.Types.PlayerStruct;
import ru.m210projects.Duke3D.Types.VOC;

public class DukeInput extends BuildControls {
	
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

	public DukeInput(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
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

			if(ctrlKeyStatus(Keys.SHIFT_LEFT) && fkey == 4 && fta > 0 && ftq == 26) //F5
			{
				int music_select = 11 * musicvolume + musiclevel;
				music_select++;
				if(music_select >= 44) 
					music_select = 0;
				
				musicvolume = music_select / 11;
				musiclevel = music_select % 11;
				
				buildString(currentGame.getCON().fta_quotes[26], 0, "PLAYING ", currentGame.getCON().music_fn[musicvolume][musiclevel]);
				sndPlayMusic(currentGame.getCON().music_fn[musicvolume][musiclevel]);
	            FTA(26, ps[myconnectindex]);
	            return;
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
								} catch (Exception ignored) {}
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
		
		if( ctrlKeyStatusOnce(Keys.F5) && !cfg.muteMusic )
        {
            buildString(currentGame.getCON().fta_quotes[26], 0, currentGame.getCON().music_fn[musicvolume][musiclevel], ".  USE SHIFT-F5 TO CHANGE.");
			FTA(26, ps[myconnectindex]);
        }

	    tics = totalclock-lastcontroltime;
	    lastcontroltime = totalclock;
	    
	    PlayerStruct p = ps[myconnectindex];
	    if(ctrlGetInputKey(GameKeys.Mouse_Aiming, true))
	    {
	    	cfg.gMouseAim = !cfg.gMouseAim;
    		FTA(44+(cfg.gMouseAim?1:0),p);
	    }
	    
	    loc.bits =   ctrlGetInputKey(GameKeys.Jump, false)?1:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Crouch, false)?2:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Weapon_Fire, false)?4:0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Aim_Up, false)?8:0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Aim_Down, false)?16:0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Run, false)?32:0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Tilt_Left, false)?64:0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Tilt_Right, false)?128:0;
	   
	    for(int i = 0; i < 10; i++) 
			if(ctrlGetInputKey(cfg.keymap[i + DukeKeys.Weapon_1.getNum()], true)) 
				loc.bits |= (i + 1)<<8;

	    if(ctrlGetInputKey(GameKeys.Previous_Weapon, true)) 
	    	loc.bits |= (11)<<8;
	    if(ctrlGetInputKey(GameKeys.Next_Weapon, true)) 
	    	loc.bits |= (12)<<8;
	    if(ctrlGetInputKey(DukeKeys.Last_Weap_Switch, true)) 
	    	loc.bits |= (13)<<8;
	    
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Steroids, false)? 1 << 12 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Look_Up, false)? 1 << 13 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Look_Down, false)? 1 << 14 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.NightVision, false)? 1 << 15 : 0;
	    
	    loc.bits |=   ctrlGetInputKey(DukeKeys.MedKit, false)? 1 << 16 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Aim_Center, false)? 1 << 18 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Holster_Weapon, false)? 1 << 19 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Inventory_Left, false)? 1 << 20 : 0;
	    loc.bits |=   ctrlKeyStatusOnce(KEY_PAUSE)? 1 << 21 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Quick_Kick, false)? 1 << 22 : 0;
	    loc.bits |=   cfg.gMouseAim? 1 << 23 : 0;
	    
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Holo_Duke, false)? 1 << 24 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Jetpack, false)? 1 << 25 : 0;
	    loc.bits |=   gamequit << 26;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Inventory_Right, false)? 1 << 27 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Turn_Around, false)? 1 << 28 : 0;
	    loc.bits |=   ctrlGetInputKey(GameKeys.Open, false)? 1 << 29 : 0;
	    loc.bits |=   ctrlGetInputKey(DukeKeys.Inventory_Use, false)? 1 << 30 : 0;
//	    loc.bits |=   ctrlKeyStatusOnce(Keys.ESCAPE)? 1 << 31 : 0;
	    
	    if((loc.bits&2) != 0) p.crouch_toggle = 0;
	    
	    boolean CrouchMode = ctrlGetInputKey(DukeKeys.Crouch_toggle, sector[p.cursectnum].lotag != 2);
	    if(sector[p.cursectnum].lotag == 2) {
	    	p.crouch_toggle = CrouchMode ? (byte) 1 : 0;
		} else if(CrouchMode) p.crouch_toggle ^= 1;

	    if(p.crouch_toggle == 1)
	    	loc.bits |= 2;
	    
	    running = ((ud.auto_run == 0 && (loc.bits&32) != 0) || ((loc.bits&32) == 0 && ud.auto_run != 0));

	    svel = vel = 0;
	    horiz = angvel = 0;

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
	        svel = (short) BClipRange(svel - 20 * ctrlGetMouseStrafe(), -keymove, keymove);
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
			svel -= keymove;
	    if ( ctrlGetInputKey(GameKeys.Move_Forward, false) )
	        vel += keymove;
	    if ( ctrlGetInputKey(GameKeys.Move_Backward, false) )
			vel -= keymove;
	    
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
}
