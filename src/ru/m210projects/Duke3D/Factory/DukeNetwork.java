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

import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.ResourceHandler.*;
import static ru.m210projects.Duke3D.Types.RTS.*;
import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.Premap.*;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.Sector.*;
import static ru.m210projects.Duke3D.View.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Screen.*;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Net.Mmulti.sendpacket;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Duke3D.Globals.*;

import java.io.File;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.Loader.WAVLoader;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Duke3D.Input;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Menus.NetworkMenu;
import ru.m210projects.Duke3D.Types.GameInfo;
import ru.m210projects.Duke3D.Types.PLocation;
import ru.m210projects.Duke3D.Types.PlayerStruct;
import ru.m210projects.Duke3D.Types.VOC;

public class DukeNetwork extends BuildNet {
	
	public int PlayerSyncRequest = -1;
	public int PlayerSyncTrail = -1;
	
	public PLocation[] predictFifo = new PLocation[kNetFifoSize];
	public PLocation predict = new PLocation(), predictOld = new PLocation();
	
	private final char[] recbuf = new char[80];
	public final int nNetVersion = 500;

public static final byte kPacketMessage = 4;
	public static final byte kPacketSound = 5;
	public static final byte kPacketProfile = 6;
//	public static final byte kPacketDisconnect = 7;
	public static final byte kPacketContentRequest = 8;
	public static final byte kPacketContentAnswer = 9;
	public static final byte kPacketPlayer		= 10;

	public byte[] gContentFound = new byte[MAXPLAYERS];

	public Main app;

	public DukeNetwork(Main app) {
		super(app);
		this.app = app;
		
		for(int i = 0; i < kNetFifoSize; i++)
			predictFifo[i] = new PLocation();
		
		Arrays.fill(gContentFound,  (byte)-1);
	}

	@Override
	public NetInput newInstance() {
		return new Input();
	}

	@Override
	public int GetPackets(byte[] p, int ptr, int len, final int nPlayer) {
		int i;
		switch (p[ptr++]) {
		case kPacketContentAnswer:
			gContentFound[nPlayer] = p[ptr];
			return 1;
		case kPacketPlayer:
			int num = packbuf[ptr++];
			int trail = LittleEndian.getInt(p, ptr);
			PlayerSyncRequest = num;
			PlayerSyncTrail = trail;
			break;
		case kPacketContentRequest:
			int pathlen = LittleEndian.getInt(p, ptr);
			ptr += 4;
			if(pathlen >= p.length - ptr) 
				pathlen = p.length - ptr - 1;

			String path = FileUtils.getCorrectPath(new String(p, ptr, pathlen));
			ptr += pathlen;
			long crc32 = LittleEndian.getUInt(p, ptr);
			long mycrc = -1;

			FileEntry fil = BuildGdx.compat.checkFile(path);
			GameInfo ini = levelGetEpisode(path);

			byte found = 0;
			if (fil != null || ini != null) {
				NetworkMenu network = (NetworkMenu) app.menu.mMenus[NETWORKGAME];
				if (ini != null) {
					mycrc = ini.getFile().getChecksum();
					if(mycrc == crc32) {
						found = 1;
						network.setEpisode(ini);
					} else {
						found = 2;
						Console.Println("Player" + nPlayer + " - " + ud.user_name[nPlayer]
								+ " tried to set user content. User content found, but has a different checksum!", OSDTEXT_RED);
						Console.Println("Make sure that you have the same content: " + File.separator + path, OSDTEXT_RED);
						if(!Console.IsShown())
							Console.toggle();
					}
				} else if (fil != null && fil.getExtension().equals("map")) {
					mycrc = fil.getChecksum();
					if(mycrc == crc32) {
						found = 1;
						network.setMap(fil);
					} else {
						found = 2;
						Console.Println("Player" + nPlayer + " - " + ud.user_name[nPlayer]
								+ " tried to set user content. User content found, but has a different checksum!", OSDTEXT_RED);
						Console.Println("Make sure that you have the same content: " + File.separator + path, OSDTEXT_RED);
						if(!Console.IsShown())
							Console.toggle();
					}
				}
			} else {
				Console.Println("Player" + nPlayer + " - " + ud.user_name[nPlayer]
						+ " tried to set user content. User content not found!", OSDTEXT_RED);
				Console.Println("Make sure that you have content at the same path: " + File.separator + path, OSDTEXT_RED);
				if(!Console.IsShown())
					Console.toggle();
			}

			packbuf[0] = kPacketContentAnswer;
			packbuf[1] = found;
			sendpacket(nPlayer, packbuf, 2);
			return 1;
		case kPacketLevelStart:
			retransmit(nPlayer, packbuf, len);

			ptr = 5;
			int nCheckVersion = LittleEndian.getInt(p, ptr);
			ptr += 4;
			pNetInfo.set(p, ptr);

			for(i=connecthead;i>=0;i=connectpoint2[i])
            {
                resetweapons(i);
                resetinventory(i);
            }

			if (nCheckVersion != nNetVersion) {
				game.GameMessage("These versions of Duke3D cannot play together.");
				NetDisconnect(myconnectindex);
				return -1;
			}

			if (WaitForAllPlayers(0))
				gGameScreen.newgame(true, ((NetworkMenu) app.menu.mMenus[NETWORKGAME]).getFile(), pNetInfo.nEpisode,
						pNetInfo.nLevel, pNetInfo.nDifficulty);

			break;
			
		case kPacketDisconnect:
			return GetDisconnectPacket(p, ptr, len, nPlayer, new DisconnectCallback() {
				@Override
				public void invoke(int nDelete) {
					if(ud.rec != null)
						ud.rec.close();

					ud.multimode = numplayers - 1;

					if(game.isCurrentScreen(gGameScreen)) {
						quickkill(ps[nDelete]);
						engine.deletesprite(ps[nDelete].i);
					}

					buildString(buf, 0, ud.user_name[nDelete], " is history!");
					
					vscrn(ud.screen_size);

					adduserquote(buf);
				}
			});
			case kPacketSound:
				retransmit(nPlayer, packbuf, len);

            	if (cfg.noSound || ud.lockout == 1 || !BuildGdx.audio.IsInited(Driver.Sound) || !RTS_Started)
            		break;
            	
            	byte[] rtsptr = RTS_GetSound(packbuf[1]);
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
					} catch (Exception e) {
						break;
					}
            	}
            	
            	rtsplaying = 7;
            	break;
			
		case kPacketMessage: 
			retransmit(nPlayer, packbuf, len);
			for(i = 0; i < len-2; i++)
				recbuf[i] = (char) packbuf[i + 2];
			recbuf[len-2] = 0;
			adduserquote(recbuf);
			sound(EXITMENUSOUND);
			break;

		case kPacketProfile:
        	retransmit(nPlayer, packbuf,len);

            int nP = packbuf[1];

			len = 0;
			for (i=3;packbuf[i] != 0;i++, len++);
			
			ud.user_name[nP] = new String(packbuf, 3, len);
			i++;

			int j = i; //This used to be Duke packet #9... now concatenated with Duke packet #6
			for (;i-j<10;i++) 
				ud.wchoice[nP][i-j] = packbuf[i];
			
			ps[nP].aim_mode = packbuf[i++];
			ps[nP].auto_aim = packbuf[i++];

            break;
		case kPacketLogout:
			game.gExit = true;
			break;
		}
		return 0;
	}

	@Override
	public void UpdatePrediction(NetInput input) {
        int i, j, k, doubvel, fz, cz, hz, lz, x, y;
        int sb_snum;
        short psect, psectlotag, tempsect, backcstat;
        boolean shrunk;
		short spritebridge;

		Input syn = (Input) input;
		predictOld.copy(predict);

		PlayerStruct p = ps[myconnectindex];

        backcstat = sprite[p.i].cstat;
        sprite[p.i].cstat &= ~257;

        sb_snum = syn.bits;

        psect = predict.sectnum;
		if(!isValidSector(psect))
			return;

        psectlotag = sector[psect].lotag;
        spritebridge = 0;

        shrunk = (sprite[p.i].yrepeat < 32);

        engine.getzrange(predict.x,predict.y,predict.z,psect,163,CLIPMASK0);
        cz = zr_ceilz;
	    hz = zr_ceilhit;
	    fz = zr_florz;
	    lz = zr_florhit;

        j = engine.getflorzofslope(psect,predict.x,predict.y);

        if( (lz&49152) == 16384 && psectlotag == 1 && klabs(predict.z-j) > PHEIGHT+(16<<8) )
            psectlotag = 0;

        if( p.aim_mode == 0 && predict.onground && psectlotag != 2 && (sector[psect].floorstat&2) != 0 )
        {
            x = (int) (predict.x+(BCosAngle(BClampAngle(predict.ang)) / 32.0f));
            y = (int) (predict.y+(BSinAngle(BClampAngle(predict.ang)) / 32.0f));
            tempsect = psect;
            tempsect = engine.updatesector(x,y, tempsect);
            if (tempsect >= 0)
            {
                 k = engine.getflorzofslope(psect,x,y);
                 if (psect == tempsect)
                      predict.horizoff += mulscale(j-k,160, 16);
                 else if (klabs(engine.getflorzofslope(tempsect,x,y)-k) <= (4<<8))
                      predict.horizoff += mulscale(j-k,160, 16);
            }
        }
        if (predict.horizoff > 0) predict.horizoff -= ((predict.horizoff>>3)+1);
        else if (predict.horizoff < 0) predict.horizoff += (((-predict.horizoff)>>3)+1);

        if(hz >= 0 && (hz&49152) == 49152)
        {
                hz &= (MAXSPRITES-1);
                if (sprite[hz].statnum == 1 && sprite[hz].extra >= 0)
                {
                    hz = 0;
                    cz = engine.getceilzofslope(psect,predict.x,predict.y);
                }
        }

        if(lz >= 0 && (lz&49152) == 49152)
        {
                 j = lz&(MAXSPRITES-1);
                 if ((sprite[j].cstat&33) == 33)
                 {
                        psectlotag = 0;
                        spritebridge = 1;
                 }
                 if(badguy(sprite[j]) && sprite[j].xrepeat > 24 && klabs(sprite[p.i].z-sprite[j].z) < (84<<8) )
                 {
                    j = engine.getangle( sprite[j].x-predict.x,sprite[j].y-predict.y);
                    predict.xvel -= sintable[(j+512)&2047]<<4;
                    predict.yvel -= sintable[j&2047]<<4;
                }
        }

        if( sprite[p.i].extra <= 0 )
        {
                 if( psectlotag == 2 )
                 {
                            if(p.on_warping_sector == 0)
                            {
                                     if( klabs(predict.z-fz) > (PHEIGHT>>1))
                                             predict.z += 348;
                            }
                            engine.clipmove(predict.x,predict.y,predict.z,predict.sectnum,0,0,164,(4<<8),(4<<8),CLIPMASK0);
                            if(clipmove_sectnum != -1) {
                            	predict.x = clipmove_x;
            		            predict.y = clipmove_y;
            		            predict.z = clipmove_z;
            		            predict.sectnum = clipmove_sectnum;
            	            }
                 }

                 short sect = engine.updatesector(predict.x,predict.y,predict.sectnum);
                 if(sect != -1)
                	 predict.sectnum = sect;
                 engine.pushmove(predict.x,predict.y,predict.z,predict.sectnum,128,(4<<8),(20<<8),CLIPMASK0);
                 if(pushmove_sectnum != -1) {
	                 predict.x = pushmove_x;
	                 predict.y = pushmove_y;
	                 predict.z = pushmove_z;
	                 predict.sectnum = pushmove_sectnum;
                 }

                predict.horiz = 100;
                predict.horizoff = 0;

    	        predictFifo[gPredictTail & kFifoMask].copy(predict);
    	        gPredictTail++;

    	        sprite[p.i].cstat = backcstat;
    	        return;
        }

        doubvel = TICSPERFRAME;

        if(p.on_crane < 0) 
        {
	        if(p.one_eighty_count < 0) predict.ang += 128;

	        i = 40;

	        if( psectlotag == 2)
	        {
	                 predict.jumpingcounter = 0;

	                 if ( (sb_snum&1) != 0 )
	                 {
	                            if(predict.zvel > 0) predict.zvel = 0;
	                            predict.zvel -= 348;
	                            if(predict.zvel < -(256*6)) predict.zvel = -(256*6);
	                 }
	                 else if ((sb_snum&(1<<1)) != 0)
	                 {
	                            if(predict.zvel < 0) predict.zvel = 0;
	                            predict.zvel += 348;
	                            if(predict.zvel > (256*6)) predict.zvel = (256*6);
	                 }
	                 else
	                 {
	                    if(predict.zvel < 0)
	                    {
	                        predict.zvel += 256;
	                        if(predict.zvel > 0)
	                            predict.zvel = 0;
	                    }
	                    if(predict.zvel > 0)
	                    {
	                        predict.zvel -= 256;
	                        if(predict.zvel < 0)
	                            predict.zvel = 0;
	                    }
	                }

	                if(predict.zvel > 2048) predict.zvel >>= 1;

	                 predict.z += predict.zvel;

	                 if(predict.z > (fz-(15<<8)) )
	                            predict.z += ((fz-(15<<8))-predict.z)>>1;

	                 if(predict.z < (cz+(4<<8)) )
	                 {
	                            predict.z = cz+(4<<8);
	                            predict.zvel = 0;
	                 }
	        }

	        else if(p.jetpack_on != 0)
	        {
	                 predict.onground = false;
	                 predict.jumpingcounter = 0;
	                 predict.hardlanding = 0;

	                 if(p.jetpack_on < 11)
	                            predict.z -= (p.jetpack_on<<7); //Goin up

	                 if(shrunk) j = 512;
	                 else j = 2048;

	                 if ((sb_snum&1) != 0)                            //A
	                            predict.z -= j;
	                 if ((sb_snum&(1<<1)) != 0)                       //Z
	                            predict.z += j;

	                 if(!shrunk && ( psectlotag == 0 || psectlotag == 2 ) ) k = 32;
	                 else k = 16;

	                 if(predict.z > (fz-(k<<8)) )
	                            predict.z += ((fz-(k<<8))-predict.z)>>1;
	                 if(predict.z < (cz+(18<<8)) )
	                            predict.z = cz+(18<<8);
	        }
	        else if( psectlotag != 2 )
	        {
	            if (psectlotag == 1 && p.spritebridge == 0)
	            {
	                 if(!shrunk) i = 34;
	                 else i = 12;
	            }
	                 if(predict.z < (fz-(i<<8)) && !floorspace(psect) && !ceilingspace(psect) ) //falling
	                 {
	                            if( (sb_snum&3) == 0 && predict.onground && (sector[psect].floorstat&2) != 0 && predict.z >= (fz-(i<<8)-(16<<8) ) )
	                                     predict.z = fz-(i<<8);
	                            else
	                            {
	                                     predict.onground = false;

	                                     predict.zvel += (currentGame.getCON().gc+80);

	                                     if(predict.zvel >= (4096+2048)) predict.zvel = (4096+2048);
	                            }
	                 }
	                 else
	                 {
	                            if(psectlotag != 1 && psectlotag != 2 && !predict.onground && predict.zvel > (6144>>1))
	                                 predict.hardlanding = (byte) (predict.zvel>>10);
	                            predict.onground = true;

	                            if(i==40)
	                            {
	                                     //Smooth on the ground

	                                     k = ((fz-(i<<8))-predict.z)>>1;
	                                     if( klabs(k) < 256 ) k = 0;
	                                     predict.z += k; // ((fz-(i<<8))-predict.z)>>1;
	                                     predict.zvel -= 768; // 412;
	                                     if(predict.zvel < 0) predict.zvel = 0;
	                            }
	                            else if(predict.jumpingcounter == 0)
	                            {
	                                predict.z += ((fz-(i<<7))-predict.z)>>1; //Smooth on the water
	                                if(p.on_warping_sector == 0 && predict.z > fz-(16<<8))
	                                {
	                                    predict.z = fz-(16<<8);
	                                    predict.zvel >>= 1;
	                                }
	                            }

	                            if( (sb_snum&2) != 0 )
	                                     predict.z += (2048+768);

	                            if( (sb_snum&1) == 0 && predict.jumpingtoggle == 1)
	                                     predict.jumpingtoggle = 0;

	                            else if( (sb_snum&1) != 0 && predict.jumpingtoggle == 0 )
	                            {
	                                     if( predict.jumpingcounter == 0 )
	                                             if( (fz-cz) > (56<<8) )
	                                             {
	                                                predict.jumpingcounter = 1;
	                                                predict.jumpingtoggle = 1;
	                                             }
	                            }
	                            if( predict.jumpingcounter != 0 && (sb_snum&1) == 0 )
	                                predict.jumpingcounter = 0;
	                 }

	                 if(predict.jumpingcounter != 0)
	                 {
	                            if( (sb_snum&1) == 0 && predict.jumpingtoggle == 1)
	                                     predict.jumpingtoggle = 0;

	                            if( predict.jumpingcounter < (1024+256) )
	                            {
	                                     if(psectlotag == 1 && predict.jumpingcounter > 768)
	                                     {
	                                             predict.jumpingcounter = 0;
	                                             predict.zvel = -512;
	                                     }
	                                     else
	                                     {
	                                             predict.zvel -= (sintable[(2048-128+predict.jumpingcounter)&2047])/12;
	                                             predict.jumpingcounter += 180;

	                                             predict.onground = false;
	                                     }
	                            }
	                            else
	                            {
	                                     predict.jumpingcounter = 0;
	                                     predict.zvel = 0;
	                            }
	                 }

	                 predict.z += predict.zvel;

	                 if(predict.z < (cz+(4<<8)) )
	                 {
	                            predict.jumpingcounter = 0;
	                            if(predict.zvel < 0) predict.xvel = predict.yvel = 0;
	                            predict.zvel = 128;
	                            predict.z = cz+(4<<8);
	                 }

	        }

	        if ( p.fist_incs != 0 ||
	                     p.transporter_hold > 2 ||
	                     predict.hardlanding != 0 ||
	                     p.access_incs > 0 ||
	                     p.knee_incs > 0 ||
	                     (p.curr_weapon == TRIPBOMB_WEAPON &&
	                      p.kickback_pic > 1 &&
	                      p.kickback_pic < 4 ) )
	        {
	                 doubvel = 0;
	                 predict.xvel = 0;
	                 predict.yvel = 0;
	        }
	        else if ( syn.avel != 0)          //p.ang += syncangvel * constant
	        {                         //ENGINE calculates angvel for you
	            long tempang;

	            tempang = (long) (syn.avel * 2);

	            if(psectlotag == 2)
	                predict.ang += (tempang-(tempang>>3))*ksgn(doubvel);
	            else predict.ang += (tempang)*ksgn(doubvel);
	            predict.ang = BClampAngle(predict.ang);
	        }

	        if ( predict.xvel != 0 || predict.yvel != 0 || syn.fvel != 0 || syn.svel != 0 )
	        {
	                 if(p.steroids_amount > 0 && p.steroids_amount < 400)
	                     doubvel <<= 1;

	                 predict.xvel += ((syn.fvel*doubvel)<<6);
	                 predict.yvel += ((syn.svel*doubvel)<<6);

	                 if( ( p.curr_weapon == KNEE_WEAPON && p.kickback_pic > 10 && predict.onground ) || ( predict.onground && (sb_snum&2) != 0 ) )
	                 {
	                            predict.xvel = mulscale(predict.xvel,currentGame.getCON().dukefriction-0x2000, 16);
	                            predict.yvel = mulscale(predict.yvel,currentGame.getCON().dukefriction-0x2000, 16);
	                 }
	                 else
	                 {
	                    if(psectlotag == 2)
	                    {
	                        predict.xvel = mulscale(predict.xvel,currentGame.getCON().dukefriction-0x1400, 16);
	                        predict.yvel = mulscale(predict.yvel,currentGame.getCON().dukefriction-0x1400, 16);
	                    }
	                    else
	                    {
	                        predict.xvel = mulscale(predict.xvel,currentGame.getCON().dukefriction, 16);
	                        predict.yvel = mulscale(predict.yvel,currentGame.getCON().dukefriction, 16);
	                    }
	                 }

	                 if( klabs(predict.xvel) < 2048 && klabs(predict.yvel) < 2048 )
	                     predict.xvel = predict.yvel = 0;

	                 if( shrunk )
	                 {
	                     predict.xvel =
	                         mulscale(predict.xvel,(currentGame.getCON().dukefriction)-(currentGame.getCON().dukefriction>>1)+(currentGame.getCON().dukefriction>>2), 16);
	                     predict.yvel =
	                         mulscale(predict.yvel,(currentGame.getCON().dukefriction)-(currentGame.getCON().dukefriction>>1)+(currentGame.getCON().dukefriction>>2), 16);
	                 }
	        }
        }

        if(psectlotag == 1 || spritebridge == 1) i = (4<<8); else i = (20<<8);

        engine.clipmove(predict.x,predict.y,predict.z,predict.sectnum,predict.xvel,predict.yvel,164,4<<8,i,CLIPMASK0);
        if(clipmove_sectnum != -1) {
        	predict.x = clipmove_x;
            predict.y = clipmove_y;
            predict.z = clipmove_z;
            predict.sectnum = clipmove_sectnum;
        }
        
        engine.pushmove(predict.x,predict.y,predict.z,predict.sectnum,164, 4<<8,4<<8,CLIPMASK0);
        if(pushmove_sectnum != -1) {
	        predict.x = pushmove_x;
            predict.y = pushmove_y;
            predict.z = pushmove_z;
            predict.sectnum = pushmove_sectnum;
        }
        
        if( p.jetpack_on == 0 && psectlotag != 1 && psectlotag != 2 && shrunk)
            predict.z += 30<<8;

        if (((sb_snum&(1<<18)) != 0) || predict.hardlanding != 0)
            predict.returntocenter = 9;

        if ((sb_snum&(1<<13)) != 0)
        {
                predict.returntocenter = 9;
                if ((sb_snum&(1<<5)) != 0) predict.horiz += 6;
                predict.horiz += 6;
        }
        else if ((sb_snum&(1<<14)) != 0)
        {
                predict.returntocenter = 9;
                if ((sb_snum&(1<<5)) != 0) predict.horiz -= 6;
                predict.horiz -= 6;
        }
        else if ((sb_snum&(1<<3)) != 0)
        {
                if ((sb_snum&(1<<5)) != 0) predict.horiz += 6;
                predict.horiz += 6;
        }
        else if ((sb_snum&(1<<4)) != 0)
        {
                if ((sb_snum&(1<<5)) != 0) predict.horiz -= 6;
                predict.horiz -= 6;
        }

        if (predict.returntocenter > 0)
            if ((sb_snum&(1<<13)) == 0 && (sb_snum&(1<<14)) == 0)
        {
             predict.returntocenter--;
             predict.horiz += 33-(predict.horiz/3);
        }

        if(p.aim_mode != 0)
            predict.horiz += syn.horz / 2;
        else
        {
            if( predict.horiz > 95 && predict.horiz < 105) predict.horiz = 100;
            if( predict.horizoff > -5 && predict.horizoff < 5) predict.horizoff = 0;
        }

        if (predict.hardlanding > 0)
        {
            predict.hardlanding--;
            predict.horiz -= (predict.hardlanding<<4);
        }

        if (predict.horiz > 299) predict.horiz = 299;
        else if (predict.horiz < -99) predict.horiz = -99;

        if(p.knee_incs > 0)
        {
            predict.horiz -= 48;
            predict.returntocenter = 9;
        }
        predictFifo[gPredictTail & kFifoMask].copy(predict);
        gPredictTail++;

        sprite[p.i].cstat = backcstat;
	}

	@Override
	public void CorrectPrediction() {
		if (numplayers < 2)
			return;
		
		PLocation pFifo = predictFifo[(gNetFifoTail - 1) & kFifoMask];
		PlayerStruct p = ps[myconnectindex];
		
		if (pFifo.ang == p.ang && pFifo.horiz == p.horiz && pFifo.x == p.posx
				&& pFifo.y == p.posy && pFifo.z == p.posz 
				&& pFifo.lookang == p.look_ang && pFifo.rotscrnang == p.rotscrnang)
			return;
		
		predict.reset();
		predictOld.copy(p.prevView);

		gPredictTail = gNetFifoTail;
		while (gPredictTail < gNetFifoHead[myconnectindex]) 
			UpdatePrediction(gFifoInput[gPredictTail & kFifoMask][myconnectindex]);
	}

	@Override
	public void CalcChecksum() {
		if ((numplayers >= 2 || mFakeMultiplayer) && ((gNetFifoTail & 7) == 7)) // build sync variables
		{
			Arrays.fill(gChecksum, 0);
			gChecksum[0] = engine.getrand();
			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				gChecksum[1] ^= Checksum(ps[i].getBytes(), PlayerStruct.sizeof);
				gChecksum[2] ^= Checksum(sprite[ps[i].i].getBytes(), SPRITE.sizeof);
			}
			for(int i = 0; i < gChecksum.length; i++)
				LittleEndian.putInt(gCheckFifo[myconnectindex], CheckSize * (gCheckHead[myconnectindex] & kFifoMask) + 4 * i, gChecksum[i]);
			gCheckHead[myconnectindex]++;
		}
		
		if(PlayerSyncRequest != -1)
	    {
			if(myconnectindex != connecthead && gNetFifoTail < PlayerSyncTrail)
				return;
			
	    	int pnum = PlayerSyncRequest;
	    	
	    	Console.Println("Player: " + pnum);
			Console.Println(ps[pnum].toString());
			Console.Println("Sprite: ");
			Console.Println(sprite[ps[pnum].i].toString());
			Console.Println("gNetFifoTail: " + gNetFifoTail);

			PlayerSyncTrail = -1;
	    	PlayerSyncRequest = -1;
	    }
	}
	
	@Override
	public void NetDisconnect(int nPlayer)
	{
		super.NetDisconnect(nPlayer);
		app.Disconnect();
	}

	public boolean WaitForContentCheck(String filepath, long crc32, int timeout) {
		Arrays.fill(gContentFound, (byte) -1);
		if (numplayers < 2)
			return true;

		WaitForSend();

		int ptr = 0;
		packbuf[ptr++] = kPacketContentRequest;
		int len = Math.min(filepath.length(), 246); //255 - 1 - 4 - 4
		LittleEndian.putInt(packbuf, ptr, len);  ptr += 4;
		System.arraycopy(filepath.getBytes(), 0, packbuf, ptr, len); ptr += len;
		LittleEndian.putUInt(packbuf, ptr, crc32); ptr += 4;
		sendtoall(packbuf, ptr);
		gContentFound[myconnectindex] = 1;

		long starttime = System.currentTimeMillis();
		while (true) {
			long time = System.currentTimeMillis() - starttime;
			if ((timeout != 0 && time > timeout)) {
				Console.Println("Connection timed out!", OSDTEXT_YELLOW);
				return false;
			}

			GetPackets();

			int i;
			for (i = connecthead; i >= 0; i = connectpoint2[i]) {
				if (gContentFound[i] == -1)
					break;
				if (myconnectindex != connecthead) {
					i = -1;
					break;
				} // slaves in M/S mode only wait for master
			}

			if (i < 0) {
				for (i = connecthead; i >= 0; i = connectpoint2[i]) {
					if (gContentFound[i] != 1)
						return false;
				}
				return true;
			}
		}
	}

	public void getnames() {
		int i, l;
		ud.user_name[myconnectindex] = cfg.pName;
		
		byte[] buf = new byte[256];
		if (numplayers > 1) {
			buf[0] = kPacketProfile;
			buf[1] = (byte) myconnectindex;
			buf[2] = (byte) BYTEVERSION;
			l = 3;

			// null terminated player name to send

			char[] name = toCharArray(cfg.pName);
			for (i = 0; i < cfg.pName.length() && name[i] != 0; i++)
				buf[l++] = (byte) name[i];
			buf[l++] = 0;

			for (i = 0; i < 10; i++) {
				ud.wchoice[myconnectindex][i] = ud.wchoice[0][i];
				buf[l++] = (byte) ud.wchoice[0][i];
			}

			buf[l++] = (byte) ps[myconnectindex].aim_mode;
			buf[l++] = (byte) ps[myconnectindex].auto_aim;
			
			WaitForSend();
			sendtoall(buf, l);

			GetPackets();
		}
	}
	
	public void SendMessage(int sendmessagecommand, char[] buf, int len)
	{
		if(sendmessagecommand != -1 || ud.multimode < 3)
		{
			tempbuf[0] = kPacketMessage;
			tempbuf[2] = 0;
			recbuf[0]  = 0;
	              
			if(ud.multimode < 3)
				sendmessagecommand = 2;

			int pos = buildString(recbuf, 0, ud.user_name[myconnectindex], ": ");
			System.arraycopy(buf, 0, recbuf, pos, len);
			pos += len;
			recbuf[pos] = 0;
			for(int i = 0; i < recbuf.length; i++)
				tempbuf[2 + i] = (byte) recbuf[i];
			
			if(sendmessagecommand >= ud.multimode)
			{
				tempbuf[1] = (byte)255;
				sendtoall(tempbuf,pos+2);
				adduserquote(recbuf);
				quotebot += 8;
				quotebotgoal = quotebot;
			}
			else if(sendmessagecommand >= 0)
			{
				tempbuf[1] = (byte)sendmessagecommand;
				if (myconnectindex != connecthead)
					sendmessagecommand = connecthead;
				sendpacket(sendmessagecommand,tempbuf,pos+2);
			}
		}
	}

	@Override
	public void ComputerInput(int i) {
		if (ud.playerai != 0)
			computergetinput(i, (Input) gFifoInput[gNetFifoHead[i] & 0xFF][i]);
	}
	
}
