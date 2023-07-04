//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Gameutils.FindDistance2D;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.SoundDefs.SUBWAY;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;

public class Spawn {

	public static short EGS(short whatsect,int s_x,int s_y,int s_z,int s_pn, int s_s,int s_xr,int s_yr,int s_a,int s_ve,int s_zv,int s_ow,short s_ss)
	{
		if(whatsect < 0 || whatsect >= MAXSECTORS)
			game.dassert("Wrong sector! " + whatsect);

	    short i = engine.insertsprite(whatsect,s_ss);
	    if( i < 0 ) {
	    	game.GameMessage(" Too many sprites spawned.");
			game.show();
			return 0;
		 }

	    SPRITE s = sprite[i];

	    s.x = s_x;
	    s.y = s_y;
	    s.z = s_z;
	    s.cstat = 0;
	    s.picnum = (short) s_pn;
	    s.shade = (byte) s_s;
	    s.xrepeat = (short) s_xr;
	    s.yrepeat = (short) s_yr;

	    s.ang = (short) s_a;
	    s.xvel = (short) s_ve;
	    s.zvel = (short) s_zv;
	    s.owner = (short) s_ow;
	    s.xoffset = 0;
	    s.yoffset = 0;
	    s.yvel = 0;
	    s.clipdist = 0;
	    s.pal = 0;
	    s.lotag = 0;

	    if(s_ow != -1)
	    	hittype[i].picnum = sprite[s_ow].picnum;

	    hittype[i].lastvx = 0;
	    hittype[i].lastvy = 0;

	    hittype[i].timetosleep = 0;
	    hittype[i].actorstayput = -1;
	    hittype[i].extra = -1;
	    hittype[i].owner = s_ow;
	    hittype[i].cgg = 0;
	    hittype[i].movflag = 0;
	    hittype[i].tempang = 0;
	    hittype[i].dispicnum = 0;
	    if(s_ow != -1) {
		    hittype[i].floorz = hittype[s_ow].floorz;
		    hittype[i].ceilingz = hittype[s_ow].ceilingz;
	    }

	    hittype[i].temp_data[0]=hittype[i].temp_data[2]=hittype[i].temp_data[3]=hittype[i].temp_data[5]=0;
	    if( s_pn < MAXTILES && currentGame.getCON().actorscrptr[s_pn] != 0 )
	    {
	        s.extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn]];
	        hittype[i].temp_data[4] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn]+1];
	        hittype[i].temp_data[1] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn]+2];
	        s.hitag = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn]+3];
	    }
	    else
	    {
	    	hittype[i].temp_data[1] = hittype[i].temp_data[4] = 0;
	        s.extra = 0;
	        s.hitag = 0;
	    }

	    if ((show2dsector[s.sectnum>>3]&(1<<(s.sectnum&7))) != 0) show2dsprite[i>>3] |= (1<<(i&7));
	    else show2dsprite[i>>3] &= ~(1<<(i&7));

	    game.pInt.clearspriteinterpolate(i);
		game.pInt.setsprinterpolate(i, s);

	    return(i);
	}

	public static int tempwallptr;
	public static int spawn( int j, int pn )
	{
	    int endwall, clostest = 0;
	    int x, y, d;
	    short i, s, startwall, sect;
	    SPRITE sp;

	    if(j >= 0)
	    {
	        i = EGS(sprite[j].sectnum,sprite[j].x,sprite[j].y,sprite[j].z
	            ,pn,0,0,0,0,0,0,j,(short)0);
	        hittype[i].picnum = sprite[j].picnum;
	    }
	    else
	    {
	        i = (short) pn;

	        hittype[i].picnum = sprite[i].picnum;
	        hittype[i].timetosleep = 0;
	        hittype[i].extra = -1;

	        sprite[i].owner = (short) (hittype[i].owner = i);
	        hittype[i].cgg = 0;
	        hittype[i].movflag = 0;
	        hittype[i].tempang = 0;
	        hittype[i].dispicnum = 0;
	        hittype[i].floorz = sector[sprite[i].sectnum].floorz;
	        hittype[i].ceilingz = sector[sprite[i].sectnum].ceilingz;

	        hittype[i].lastvx = 0;
	        hittype[i].lastvy = 0;
	        hittype[i].actorstayput = -1;

	        hittype[i].temp_data[0] = hittype[i].temp_data[1] = hittype[i].temp_data[2] = hittype[i].temp_data[3] = hittype[i].temp_data[4] = hittype[i].temp_data[5] = 0;

	       if((sprite[i].cstat&48) != 0 )
	            if( !(sprite[i].picnum >= CRACK1 && sprite[i].picnum <= CRACK4) )
	        {
	            	game.pInt.setsprinterpolate(i, sprite[i]);
	            if(sprite[i].shade == 127) return i;
	            if( wallswitchcheck(i) && (sprite[i].cstat&16) != 0 )
	            {
	                if( sprite[i].picnum != 82 && sprite[i].picnum != 129 && sprite[i].pal != 0)
	                {
	                    if( (ud.multimode < 2) || (ud.multimode > 1 && ud.coop==1) )
	                    {
	                        sprite[i].xrepeat = sprite[i].yrepeat = 0;
	                        sprite[i].cstat = sprite[i].lotag = sprite[i].hitag = 0;
	                        return i;
	                    }
	                }
	                sprite[i].cstat |= 257;
	                if( sprite[i].pal != 0 && sprite[i].picnum != 82 && sprite[i].picnum != 129)
	                    sprite[i].pal = 0;
	                return i;
	            }

	            if( sprite[i].hitag != 0 )
	            {
	                engine.changespritestat(i,(short)12);
	                sprite[i].cstat |=  257;
	                sprite[i].extra = (short) currentGame.getCON().impact_damage;
	                return i;
	            }
	        }

	        s = sprite[i].picnum;

	        if( (sprite[i].cstat&1) != 0 ) sprite[i].cstat |= 256;

	        if( currentGame.getCON().actorscrptr[s] != 0 )
	        {
	            sprite[i].extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s]];
	            hittype[i].temp_data[4] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s]+1];
	            hittype[i].temp_data[1] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s]+2];
	            if( currentGame.getCON().script[currentGame.getCON().actorscrptr[s]+3] != 0 && sprite[i].hitag == 0 )
	                sprite[i].hitag = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s]+3];
	        }
	        else hittype[i].temp_data[1] = hittype[i].temp_data[4] = 0;
	    }

	    game.pInt.clearspriteinterpolate(i);

	    sp = sprite[i];
	    sect = sp.sectnum;

	    switch(sp.picnum)
	    {
	            default:

	                if( currentGame.getCON().actorscrptr[sp.picnum] != 0 )
	                {
	                    if( j == -1 && sp.lotag > ud.player_skill )
	                    {
	                        sp.xrepeat=sp.yrepeat=0;
	                        engine.changespritestat(i,(short)5);
	                        break;
	                    }

	                        //  Init the size
	                    if(sp.xrepeat == 0 || sp.yrepeat == 0)
	                        sp.xrepeat = sp.yrepeat = 1;

	                    if( (currentGame.getCON().actortype[sp.picnum] & 3) != 0)
	                    {
	                        if( ud.monsters_off )
	                        {
	                            sp.xrepeat=sp.yrepeat=0;
	                            engine.changespritestat(i,(short)5);
	                            break;
	                        }

	                        makeitfall(currentGame.getCON(), i);

	                        if( (currentGame.getCON().actortype[sp.picnum] & 2) != 0)
	                            hittype[i].actorstayput = sp.sectnum;

	                        if ( checkaddkills(sp) )
	                        	ps[connecthead].max_actors_killed++;

	                        sp.clipdist = 80;
	                        if(j >= 0)
	                        {
	                            if(sprite[j].picnum == RESPAWN)
	                                hittype[i].tempang = sprite[i].pal = sprite[j].pal;
	                            engine.changespritestat(i,(short)1);
	                        }
	                        else engine.changespritestat(i,(short)2);
	                    }
	                    else
	                    {
	                        sp.clipdist = 40;
	                        sp.owner = i;
	                        engine.changespritestat(i,(short)1);
	                    }

	                    hittype[i].timetosleep = 0;

	                    if(j >= 0)
	                        sp.ang = sprite[j].ang;
	                }
	                break;
	            case BLOODPOOL:
                {
                    short s1 = engine.updatesector(sp.x+108,sp.y+108,sp.sectnum);
                    if(s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz)
                    {
                    	s1 = engine.updatesector(sp.x-108,sp.y-108,s1);
                        if(s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz)
                        {
                        	s1 = engine.updatesector(sp.x+108,sp.y-108,s1);
                            if(s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz)
                            {
                            	s1 = engine.updatesector(sp.x-108,sp.y+108,s1);
                                if(s1 >= 0 && sector[s1].floorz != sector[sp.sectnum].floorz)
                                { sp.xrepeat = sp.yrepeat = 0;engine.changespritestat(i,(short)5);break;}
                            }
                            else { sp.xrepeat = sp.yrepeat = 0;engine.changespritestat(i,(short)5);break;}
                        }
                        else { sp.xrepeat = sp.yrepeat = 0;engine.changespritestat(i,(short)5);break;}
                    }
                    else { sp.xrepeat = sp.yrepeat = 0;engine.changespritestat(i,(short)5);break;}
                }

                if( sector[sprite[i].sectnum].lotag == 1 )
                {
                    engine.changespritestat(i,(short)5);
                    break;
                }

                if(j >= 0)
                {
                    if( sprite[j].pal == 1)
                        sp.pal = 1;
                    else if( sprite[j].pal != 6 && sprite[j].picnum != 1304 && sprite[j].picnum != TIRE )
                    {
                        sp.pal = 2;
                    }
                    else sp.pal = 0;

                    if(sprite[j].picnum == TIRE)
                        sp.shade = 127;
                }
                sp.cstat |= 32;

            case BLOODSPLAT1:
            case BLOODSPLAT2:
            case BLOODSPLAT3:
            case BLOODSPLAT4:
                sp.cstat |= 16;
                sp.xrepeat = (short) (7+(engine.krand()&7));
                sp.yrepeat = (short) (7+(engine.krand()&7));
                sp.z -= (16<<8);
                if(j >= 0 && sprite[j].pal == 6)
                    sp.pal = 6;
                insertspriteq(i);
                engine.changespritestat(i,(short)5);
                break;

            case BULLETHOLE:
                sp.xrepeat = sp.yrepeat = 3;
                sp.cstat = (short) (16+(engine.krand()&12));
                insertspriteq(i);
            case FEATHERS:
            	if ( sp.picnum == FEATHERS )
                {
            		 hittype[i].temp_data[0] = engine.krand()&2047;
                     sp.cstat = (short) (engine.krand()&12);
                     sp.xrepeat = sp.yrepeat = 8;
                     sp.ang = (short) (engine.krand()&2047);
                }
            	engine.changespritestat(i,(short) 5);
            	break;

            case EXPLOSION2:
            case EXPLOSION3:
            case BURNING:
            case SMALLSMOKE:
                if(j >= 0)
                {
                    sp.ang = sprite[j].ang;
                    sp.shade = -64;
                    sp.cstat = (short) (128|(engine.krand()&4));
                }
                switch ( sp.picnum )
                {
                  case EXPLOSION2:
                	  sp.xrepeat = 48;
                      sp.yrepeat = 48;
                      sp.shade = -127;
                      sp.cstat |= 128;
                      break;
                  case EXPLOSION3:
                	  sp.xrepeat = 128;
                      sp.yrepeat = 128;
                      sp.shade = -127;
                      sp.cstat |= 128;
                      break;
                  case SMALLSMOKE:
                	  sp.xrepeat = 12;
                      sp.yrepeat = 12;
                      break;
                  case BURNING:
                	  sp.xrepeat = 4;
                      sp.yrepeat = 4;
                      break;
                }

                if(j >= 0)
                {
                    x = engine.getflorzofslope(sp.sectnum,sp.x,sp.y);
                    if(sp.z > x-(12<<8) )
                        sp.z = x-(12<<8);
                }

                engine.changespritestat(i,(short)5);
                break;

            case 1268:
            case 1309:
                sp.extra = 0;
            case 1187:
            case 1251:
            case 1304:
            case 1305:
            case 1306:
            case 1315:
            case 1317:
            case 1388:
                if(j >= 0)
                    sp.xrepeat = sp.yrepeat = 32;
                sp.clipdist = 72;
                makeitfall(currentGame.getCON(), i);
                if(j >= 0)
                    sp.owner = (short) j;
                else sp.owner = i;
            case 1147:
                if( ud.monsters_off && sp.picnum == 1147 )
                {
                    sp.xrepeat = sp.yrepeat = 0;
                    engine.changespritestat(i,(short)5);
                }
                else
                {
                    if(sp.picnum == 1147)
                        sp.clipdist = 24;
                    sp.cstat = (short) (257|(engine.krand()&4));
                    engine.changespritestat(i,(short)2);
                }
                break;
            case 1272:
                sp.lotag = 9999;
                engine.changespritestat(i,(short)6);
                break;
            case CRANE:

                sp.cstat |= 64|257;

                sp.picnum += 2;
                sp.z = sector[sect].ceilingz+(48<<8);
                hittype[i].temp_data[4] = tempwallptr;

                msx[tempwallptr] = sp.x;
                msy[tempwallptr] = sp.y;
                msx[tempwallptr+2] = sp.z;

                s = headspritestat[0];
                while(s >= 0)
                {
                    if( sprite[s].picnum == 1298 && sprite[i].hitag == (sprite[s].hitag) )
                    {
                        msy[tempwallptr+2] = s;

                        hittype[i].temp_data[1] = sprite[s].sectnum;

                        sprite[s].xrepeat = 48;
                        sprite[s].yrepeat = 128;

                        msx[tempwallptr+1] = sprite[s].x;
                        msy[tempwallptr+1] = sprite[s].y;

                        sprite[s].x = sp.x;
                        sprite[s].y = sp.y;
                        sprite[s].z = sp.z;
                        sprite[s].shade = sp.shade;

                        engine.setsprite(s,sprite[s].x,sprite[s].y,sprite[s].z);
                        break;
                    }
                    s = nextspritestat[s];
                }

                tempwallptr += 3;
                sp.owner = -1;
                sp.extra = 8;
                engine.changespritestat(i,(short)6);
                break;
            case FANSPRITEWORK:
            case 234:
            case 1066:
            case 1067:
            case 1085:
            case 1086:
            case 1114:
            case 1117:
            case 1121:
            case 1123:
            case 1124:
            case 1141:
            case 1150:
            case 1152:
            case 1157:
            case 1158:
            case 1163:
            case 1164:
            case 1165:
            case 1166:
            case 1172:
            case 1174:
            case 1175:
            case 1176:
            case 1178:
            case 1180:
            case 1194:
            case 1203:
            case 1215:
            case 1216:
            case 1217:
            case 1218:
            case 1219:
            case 1220:
            case 1221:
            case 1222:
            case 1228:
            case 1232:
            case 1233:
            case 1234:
            case 1280:
            case 1281:
            case 1282:
            case 1283:
            case 1284:
            case 1285:
            case 1286:
            case 1287:
            case 1288:
            case 1289:
            case 2215:
            	sp.clipdist = 32;
                sp.cstat |= 257;
                engine.changespritestat(i,(short)0);
            	break;
            case WATERSPLASH2:
            case MUD:
                if(j >= 0)
                {
                    engine.setsprite(i,sprite[j].x,sprite[j].y,sprite[j].z);
                    sp.xrepeat = sp.yrepeat = (short) (8+(engine.krand()&7));
                }
                else sp.xrepeat = sp.yrepeat = (short) (16+(engine.krand()&15));

                sp.shade = -16;
                sp.cstat |= 128;

                if(j >= 0)
                {
                    if(sector[sprite[j].sectnum].lotag == 2)
                    {
                        sp.z = engine.getceilzofslope(sprite[i].sectnum,sprite[i].x,sprite[i].y)+(16<<8);
                        sp.cstat |= 8;
                    }
                    else if( sector[sprite[j].sectnum].lotag == 1)
                        sp.z = engine.getflorzofslope(sprite[i].sectnum,sprite[i].x,sprite[i].y);
                }

                if(sector[sect].floorpicnum == FLOORSLIME ||
                    sector[sect].ceilingpicnum == FLOORSLIME)
                        sp.pal = 7;
            case 1080:
            case NEON1:
            case NEON2:
            case NEON3:
            case NEON4:
            case NEON5:
            case NEON6:
            	if(sp.picnum != WATERSPLASH2)
                    sp.cstat |= 257;
                if(sp.picnum == 1080)
                    sp.cstat |= 257;
            case JIBS1:
            case JIBS2:
            case JIBS3:
            case JIBS4:
            case JIBS5:
            case JIBS6:
            case 4041:
            case 4046:
            case 4055:
            case 4235:
            case 4244:
            case 4748:
            case 4753:
            case 4758:
            case 5290:
            case 5295:
            case 5300:
            case 5602:
            case 5607:
            case 5616:
            case 2460: //RA green jibs1
            case 2465: //RA green jibs2
        	case 5872: //RA motowheel
        	case 5877: //RA mototank
        	case 5882: //RA boarddebris
        	case 6112: //RA bikenbody
        	case 6117: //RA bikenhead
        	case 6121: //RA bikerhead2
        	case 6127: //RA bikerhand
        	case 7000: //RA babahead
        	case 7005: //RA bababody
        	case 7010: //RA babafoot
        	case 7015: //RA babahand
        	case 7020: //RA debris
        	case 7025: //RA debbris
        	case 7387: //RA deepjibs
        	case 7392: //RA deepjibs2
        	case 7397: //RA deepjibs3
        	case 8890: //RA deep2jibs
        	case 8895: //RA deep2jibs2

        		if(currentGame.getCON().type != RRRA
        				&& (sp.picnum == 2460
    		            || sp.picnum == 2465
    		        	|| sp.picnum == 5872
    		        	|| sp.picnum == 5877
    		        	|| sp.picnum == 5882
    		        	|| sp.picnum == 6112
    		        	|| sp.picnum == 6117
    		        	|| sp.picnum == 6121
    		        	|| sp.picnum == 6127
    		        	|| sp.picnum == 7000
    		        	|| sp.picnum == 7005
    		        	|| sp.picnum == 7010
    		        	|| sp.picnum == 7015
    		        	|| sp.picnum == 7020
    		        	|| sp.picnum == 7025
    		        	|| sp.picnum == 7387
    		        	|| sp.picnum == 7392
    		        	|| sp.picnum == 7397
    		        	|| sp.picnum == 8890
    		        	|| sp.picnum == 8895))
        			break;


        		switch(sp.picnum)
        		{
        			case JIBS6:
        				sp.xrepeat >>= 1;
                        sp.yrepeat >>= 1;
        				break;
	    			case 7387:
	                    sp.xrepeat = 18;
	                    sp.yrepeat = 18;
	                    break;
	    			case 7392:
	                    sp.xrepeat = 36;
	                    sp.yrepeat = 36;
	                    break;
	    			case 7397:
	                    sp.xrepeat = 54;
	                    sp.yrepeat = 54;
	                    break;
        		}
                engine.changespritestat(i,(short)5);
                break;
            case 1196:
            	sp.shade = -16;
            	engine.changespritestat(i,(short) 6);
            	break;
            case 1247:
            	hittype[i].temp_data[0] = sp.x;
                hittype[i].temp_data[1] = sp.y;
            	break;
            case WATERFOUNTAIN:
                sprite[i].lotag = 1;
            case 1191:
            case 1193:
            case TIRE:
            	sprite[i].cstat = 257; // Make it hitable
                sprite[i].extra = 1;
                engine.changespritestat(i,(short)6);
            	break;

            case LNRDLYINGDEAD:
            	if ( j >= 0 && sprite[j].picnum == APLAYER )
                {
            		sp.xrepeat = sprite[j].xrepeat;
            		sp.yrepeat = sprite[j].yrepeat;
            		sp.shade = sprite[j].shade;
            		sp.pal = ps[sprite[j].yvel].palookup;
                }
                sp.cstat = 0;
                sp.extra = 1;
                sp.xvel = 292;
                sp.zvel = 360;
            case RESPAWNMARKERRED:
                if(sp.picnum == RESPAWNMARKERRED)
                {
                    sp.xrepeat = sp.yrepeat = 8;
                    if(j >= 0) sp.z = hittype[j].floorz;
                }
                else
                {
                    sp.cstat |= 257;
                    sp.clipdist = 128;
                }
            case 1170:
                if(sp.picnum == 1170)
                    sp.yvel = sp.hitag;
                engine.changespritestat(i,(short)1);
                break;
            case FOOTPRINTS:
            case FOOTPRINTS2:
            case FOOTPRINTS3:
            case FOOTPRINTS4:
                if(j >= 0)
                {
                    short s1 = engine.updatesector(sp.x+84,sp.y+84,sp.sectnum);
                    if(s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz)
                    {
                    	s1 = engine.updatesector(sp.x-84,sp.y-84,s1);
                        if(s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz)
                        {
                        	s1 = engine.updatesector(sp.x+84,sp.y-84,s1);
                            if(s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz)
                            {
                            	s1 = engine.updatesector(sp.x-84,sp.y+84,s1);
                                if(s1 >= 0 && sector[s1].floorz != sector[sp.sectnum].floorz)
                                { sp.xrepeat = sp.yrepeat = 0;engine.changespritestat(i,(short)5);break;}
                            }
                            else { sp.xrepeat = sp.yrepeat = 0;break;}
                        }
                        else { sp.xrepeat = sp.yrepeat = 0;break;}
                    }
                    else { sp.xrepeat = sp.yrepeat = 0;break;}

                    sp.cstat = (short) (32+((ps[sprite[j].yvel].footprintcount&1)<<2));
                    sp.ang = sprite[j].ang;
                }

                sp.z = sector[sect].floorz;
                if(sector[sect].lotag != 1 && sector[sect].lotag != 2)
                    sp.xrepeat = sp.yrepeat = 32;

                insertspriteq(i);
                engine.changespritestat(i,(short)5);
                break;
            case BOLT1:
            case BOLT1+1:
            case BOLT1+2:
            case BOLT1+3:
                hittype[i].temp_data[0] = sp.xrepeat;
                hittype[i].temp_data[1] = sp.yrepeat;
            case MASTERSWITCH:
                if(sp.picnum == MASTERSWITCH)
                    sp.cstat |= 32768;
                sp.yvel = 0;
                engine.changespritestat(i,(short)6);
                break;
            case WATERDRIP:
                if(j >= 0 && (sprite[j].statnum == 10 || sprite[j].statnum == 1))
                {
                    sp.shade = 32;
                    if(sprite[j].pal != 1)
                    {
                        sp.pal = 2;
                        sp.z -= (18<<8);
                    }
                    else sp.z -= (13<<8);
                    sp.ang = engine.getangle(ps[connecthead].posx-sp.x,ps[connecthead].posy-sp.y);
                    sp.xvel = (short) (48-(engine.krand()&31));
                    ssp(i,CLIPMASK0);
                }
                else if(j == -1)
                {
                    sp.z += (4<<8);
                    hittype[i].temp_data[0] = sp.z;
                    hittype[i].temp_data[1] = engine.krand()&127;
                }
            case TRASH:
                if(sp.picnum != WATERDRIP)
                    sp.ang = (short) (engine.krand()&2047);
                sp.xrepeat = 24;
                sp.yrepeat = 24;
                engine.changespritestat(i,(short)6);
                break;

            case CRACK1:
            case CRACK2:
            case CRACK3:
            case CRACK4:
                sp.cstat |= 17;
                sp.extra = 1;
                if( ud.multimode < 2 && sp.pal != 0)
                {
                    sp.xrepeat = sp.yrepeat = 0;
                    engine.changespritestat(i,(short)5);
                    break;
                }
                sp.pal = 0;
                sp.owner = i;
                engine.changespritestat(i,(short)6);
                sp.xvel = 8;
                ssp(i,CLIPMASK0);
                break;
            case 1097:
            case 1106:
            	sp.cstat &= ~257;
            	engine.changespritestat(i,(short) 0);
            	break;
            case 285:
            case 286:
            case 287:
            case 288:
            case 289:
            case 290:
            case 291:
            case 292:
            case 293:
            	sp.cstat = 0;
                sp.xrepeat = 0;
                sp.yrepeat = 0;
                sp.clipdist = 0;
                sp.cstat |= 32768;
                sp.lotag = 0;
                engine.changespritestat(i,(short) 106);
            	break;
            case BOWLLINE:
            case 281:
            case 282:
            case 283:
            case 2025:
            case 2026:
            case 2027:
            case 2028:
            	sp.cstat = 0;
                sp.xrepeat = 0;
                sp.yrepeat = 0;
                sp.clipdist = 0;
                sp.extra = 0;
                sp.cstat |= 32768;
                engine.changespritestat(i,(short) 105);
            	break;
            case 63:
            	sp.xrepeat = 1;
                sp.yrepeat = 1;
                sp.clipdist = 1;
                sp.cstat |= 32768;
            	engine.changespritestat(i,(short) 100);
            	break;
            case 295:
            	sp.cstat |= 32768;
            	engine.changespritestat(i,(short) 107);
            	break;
            case 296:
            case 297:
            	sp.xrepeat = 64;
            	sp.yrepeat = 64;
            	sp.clipdist = 64;
            	engine.changespritestat(i,(short) 108);
	            break;
            case WATERBUBBLE:
                if(j >= 0 && sprite[j].picnum == APLAYER)
                    sp.z -= (16<<8);
                if( sp.picnum == WATERBUBBLE)
                {
                    if( j >= 0 )
                        sp.ang = sprite[j].ang;
                    sp.xrepeat = sp.yrepeat = 4;
                }
                else sp.xrepeat = sp.yrepeat = 32;

                engine.changespritestat(i,(short)5);
                break;
            case TOUCHPLATE:
                hittype[i].temp_data[2] = sector[sect].floorz;
                if(sector[sect].lotag != 1 && sector[sect].lotag != 2)
                    sector[sect].floorz = sp.z;
                if(sp.pal != 0 && ud.multimode > 1)
                {
                    sp.xrepeat=sp.yrepeat=0;
                    engine.changespritestat(i,(short)5);
                    break;
                }
            case WATERBUBBLEMAKER:
                sp.cstat |= 32768;
                engine.changespritestat(i,(short)6);
                break;

            case 14: //RA
            case FIRSTGUNSPRITE:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 37:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 59:
            case 61:
            case 78:
            case 1350:
            case 3437:
            case 5595:
            case 8460: //RA BOX
                if(j >= 0)
                {
                    sp.lotag = 0;
                    if ( sp.picnum == BOWLINGBALLSPRITE )
                    {
                      sp.zvel = 0;
                    } else {
                    	sp.zvel = -1024;
                    	sp.z -= (32<<8);
                    }
                    ssp(i,CLIPMASK0);
                    sp.cstat = (short) (engine.krand()&4);
                }
                else
                {
                    sp.owner = i;
                    sp.cstat = 0;
                }

                if( ( ud.multimode < 2 && sp.pal != 0) || (sp.lotag > ud.player_skill) )
                {
                    sp.xrepeat = sp.yrepeat = 0;
                    engine.changespritestat(i,(short)5);
                    break;
                }

                sp.pal = 0;
                if(sp.sectnum >= 0 && sp.sectnum < MAXSECTORS)
                	sp.shade = sector[sp.sectnum].floorshade;
            case DOORKEY:

                if(sp.picnum == ECLAIRHEALTH)
                    sp.cstat |= 128;

                if(ud.multimode > 1 && ud.coop != 1 && sp.picnum == DOORKEY)
                {
                    sp.xrepeat = sp.yrepeat = 0;
                    engine.changespritestat(i,(short)5);
                    break;
                }
                else
                {
                    if(sp.picnum == AMMO)
                        sp.xrepeat = sp.yrepeat = 16;
                    else sp.xrepeat = sp.yrepeat = 32;
                }

                if(sp.picnum == DOORKEY)
                	sp.shade = -17;

                if(j >= 0) engine.changespritestat(i,(short)1);
                else
                {
                    engine.changespritestat(i,(short)2);
                    makeitfall(currentGame.getCON(), i);
                }

                switch(sp.picnum)
                {
                	case 78:
                		sp.xrepeat = 23;
                		sp.yrepeat = 23;
                		break;
                	case FIRSTGUNSPRITE:
                		sp.xrepeat = 16;
                		sp.yrepeat = 16;
                		break;
                	case CROSSBOWSPRITE:
                	case 8460:
                		sp.xrepeat = 16;
                		sp.yrepeat = 14;
                		break;
                	case TEATGUN:
                		sp.xrepeat = 17;
                		sp.yrepeat = 16;
                		break;
                	case BUZSAWSPRITE:
                		sp.xrepeat = 22;
                        sp.yrepeat = 13;
                		break;
                	case POWDERKEGSPRITE:
                	case 3437:
                		sp.xrepeat = 11;
                        sp.yrepeat = 11;
                        if(sp.picnum == 27) {
	                        sp.xvel = 32;
	                        sp.yvel = 4;
                        }
                		break;
                	case ALIENARMGUN:
                	case SHOTGUNAMMO:
                		sp.xrepeat = 18;
                        sp.yrepeat = 17;
                		break;
                	case TEATAMMO:
                	case ALIENBLASTERAMMO:
                		sp.xrepeat = 10;
                        sp.yrepeat = 9;
                        break;
                	case AMMO:
                		sp.xrepeat = 9;
                		sp.yrepeat = 9;
                		break;
                	case RIFLEAMMO:
                		sp.xrepeat = 15;
                		sp.yrepeat = 15;
                		break;
                	case BLADEAMMO:
                		sp.xrepeat = 12;
                		sp.yrepeat = 7;
                		break;
                	case BEER:
                		sp.xrepeat = 5;
                		sp.yrepeat = 4;
                		break;
                	case WHISKEY:
                	case 5595:
                		sp.xrepeat = 8;
                		sp.yrepeat = 8;
                		break;
                	case PORKBALLS:
                	case MOONSHINE:
                		 sp.xrepeat = 13;
                         sp.yrepeat = 9;
                		break;
                	case SNORKLE:
                		sp.xrepeat = 19;
                		sp.yrepeat = 16;
                		break;
                	case COWPIE:
                		sp.xrepeat = 8;
                		sp.yrepeat = 6;
                		break;
                	case 59:
                		sp.xrepeat = 6;
                		sp.yrepeat = 4;
                		break;
                	case DOORKEY:
                		sp.xrepeat = 11;
                		sp.yrepeat = 12;
                		break;
                	case 14:
                		sp.xrepeat = 20;
                		sp.yrepeat = 20;
                		break;
                }
                break;
            case DOORSHOCK:
                sp.cstat |= 1+256;
                sp.shade = -12;
                engine.changespritestat(i,(short)6);
                break;
            case SOUNDFX:
            	sp.cstat |= 32768;
            	engine.changespritestat(i,(short)2);
            	break;
            case LOCATORS:
                sp.cstat |= 32768;
                engine.changespritestat(i,(short)7);
                break;
            case DYNAMITE:
                sp.yvel = 4;
                sp.owner = i;
                sp.xrepeat = sp.yrepeat = 9;
            case REACTOR2:
            case REACTOR:
            case 4989:
                if(sp.picnum == 4989)
                {
                    if( sp.lotag > ud.player_skill )
                    {
                        sp.xrepeat = sp.yrepeat = 0;
                        engine.changespritestat(i,(short)5);
                        game.pInt.setsprinterpolate(i, sprite[i]);
                        return i;
                    }
                    if ( checkaddkills(sp) )
                        ps[connecthead].max_actors_killed++;

                    hittype[i].temp_data[5] = 0;
                    if(ud.monsters_off)
                    {
                        sp.xrepeat = sp.yrepeat = 0;
                        engine.changespritestat(i,(short)5);
                        break;
                    }
                    sp.extra = 130;
                }

                if(sp.picnum == REACTOR || sp.picnum == REACTOR2)
                    sp.extra = (short) currentGame.getCON().impact_damage;

                sprite[i].cstat |= 257; // Make it hitable

                if( ud.multimode < 2 && sp.pal != 0)
                {
                    sp.xrepeat = sp.yrepeat = 0;
                    engine.changespritestat(i,(short)5);
                    break;
                }
                sp.pal = 0;
                sprite[i].shade = -17;

                engine.changespritestat(i,(short)2);
                break;
            case RESPAWN:
                sp.extra = 66-13;
            case MUSICANDSFX:
                if( ud.multimode < 2 && sp.pal == 1)
                {
                    sp.xrepeat = sp.yrepeat = 0;
                    engine.changespritestat(i,(short)5);
                    break;
                }
                sp.cstat = (short)32768;
                engine.changespritestat(i,(short)11);
                break;
            case ACTIVATORLOCKED:
            case ACTIVATOR:
                sp.cstat |= 32768;
                if(sp.picnum == ACTIVATORLOCKED)
                    sector[sp.sectnum].lotag |= 16384;
                engine.changespritestat(i,(short)8);
                break;
            case SECTOREFFECTOR:
                sp.yvel = sector[sect].extra;
                sp.cstat |= 32768;
                sp.xrepeat = sp.yrepeat = 0;

                switch(sp.lotag)
                {
                    case 7: // Transporters!!!!
                    case 23:// XPTR END
                        if(sp.lotag != 23)
                        {
                            for(j=0;j<MAXSPRITES;j++)
                                if(sprite[j].statnum < MAXSTATUS && sprite[j].picnum == SECTOREFFECTOR && ( sprite[j].lotag == 7 || sprite[j].lotag == 23 ) && i != j && sprite[j].hitag == sprite[i].hitag)
                                {
                                    sprite[i].owner = (short) j;
                                    break;
                                }
                        }
                        else sprite[i].owner = i;

                        hittype[i].temp_data[4] = (sector[sect].floorz == sprite[i].z)?1:0;
                        sp.cstat = 0;
                        engine.changespritestat(i,(short)9);
                        game.pInt.setsprinterpolate(i, sprite[i]);
                        return i;
                    case 1:
                        sp.owner = -1;
                        hittype[i].temp_data[0] = 1;
                        break;
                    case 18:

                        if(sp.ang == 512)
                        {
                            hittype[i].temp_data[1] = sector[sect].ceilingz;
                            if(sp.pal != 0)
                                sector[sect].ceilingz = sp.z;
                        }
                        else
                        {
                            hittype[i].temp_data[1] = sector[sect].floorz;
                            if(sp.pal != 0)
                                sector[sect].floorz = sp.z;
                        }

                        sp.hitag <<= 2;
                        break;

                    case 25: // Pistons
                        hittype[i].temp_data[3] = sector[sect].ceilingz;
                        hittype[i].temp_data[4] = 1;
                        sector[sect].ceilingz = sp.z;
                        game.pInt.setceilinterpolate(sect, sector[sect]); //ceilinz
                        break;
                    case 35:
                        sector[sect].ceilingz = sp.z;
                        break;
                    case 27:
                        if(ud.recstat == 1)
                        {
                            sp.xrepeat=sp.yrepeat=64;
                            sp.cstat &= 32767;
                        }
                        break;
                    case 12:
                    case 47:
                    case 48: //RA
                        hittype[i].temp_data[1] = sector[sect].floorshade;
                        hittype[i].temp_data[2] = sector[sect].ceilingshade;
                        break;

                    case 13:

                        hittype[i].temp_data[0] = sector[sect].ceilingz;
                        hittype[i].temp_data[1] = sector[sect].floorz;

                        if( klabs(hittype[i].temp_data[0]-sp.z) < klabs(hittype[i].temp_data[1]-sp.z) )
                            sp.owner = 1;
                        else sp.owner = 0;

                        if(sp.ang == 512)
                        {
                            if(sp.owner != 0)
                                sector[sect].ceilingz = sp.z;
                            else
                                sector[sect].floorz = sp.z;
                        }
                        else
                            sector[sect].ceilingz = sector[sect].floorz = sp.z;

                        if( (sector[sect].ceilingstat&1) != 0 )
                        {
                            sector[sect].ceilingstat ^= 1;
                            hittype[i].temp_data[3] = 1;

                            if(sp.owner == 0 && sp.ang==512)
                            {
                                sector[sect].ceilingstat ^= 1;
                                hittype[i].temp_data[3] = 0;
                            }

                            sector[sect].ceilingshade =
                                sector[sect].floorshade;

                            if(sp.ang==512)
                            {
                                startwall = sector[sect].wallptr;
                                endwall = startwall+sector[sect].wallnum;
                                for(j=startwall;j<endwall;j++)
                                {
                                    x = wall[j].nextsector;
                                    if(x >= 0)
                                        if( (sector[x].ceilingstat&1) == 0)
                                    {
                                        sector[sect].ceilingpicnum =
                                            sector[x].ceilingpicnum;
                                        sector[sect].ceilingshade =
                                            sector[x].ceilingshade;
                                        break; //Leave earily
                                    }
                                }
                            }
                        }

                        break;

                    case 17:

                        hittype[i].temp_data[2] = sector[sect].floorz; //Stopping loc

                        j = engine.nextsectorneighborz(sect,sector[sect].floorz,-1,-1);
                        hittype[i].temp_data[3] = sector[j].ceilingz;

                        j = engine.nextsectorneighborz(sect,sector[sect].ceilingz,1,1);
                        hittype[i].temp_data[4] = sector[j].floorz;

                        if(numplayers < 2) {
                        	game.pInt.setceilinterpolate(sect, sector[sect]);
                        	game.pInt.setfloorinterpolate(sect, sector[sect]);
                        }
                        break;

                    case 24:
                        sp.yvel <<= 1;
                    case 36:
                        break;
                    case 88:
                         hittype[i].temp_data[0] = sector[sect].floorshade;
                         hittype[i].temp_data[1] = sector[sect].ceilingshade;
                         startwall = sector[sect].wallptr;
                         endwall = startwall+sector[sect].wallnum;

                         for(s=startwall;s<endwall;s++)
                         {
                        	 if(wall[s].shade > hittype[i].temp_data[2])
                        		 hittype[i].temp_data[2] = wall[s].shade;
                         }
                         hittype[i].temp_data[3] = 1;
                         break;

                    case 20:
                    {
                        long q;

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        //find the two most clostest wall x's and y's
                        q = 0x7fffffff;

                        for(s=startwall;s<endwall;s++)
                        {
                            x = wall[s].x;
                            y = wall[s].y;

                            d = FindDistance2D(sp.x-x,sp.y-y);
                            if( d < q )
                            {
                                q = d;
                                clostest = s;
                            }
                        }

                        hittype[i].temp_data[1] = clostest;

                        q = 0x7fffffff;

                        for(s=startwall;s<endwall;s++)
                        {
                            x = wall[s].x;
                            y = wall[s].y;
                            d = FindDistance2D(sp.x-x,sp.y-y);

                            if(d < q && s != hittype[i].temp_data[1])
                            {
                                q = d;
                                clostest = s;
                            }
                        }

                        hittype[i].temp_data[2] = clostest;
                    }

                    break;

                    case 3:

                        hittype[i].temp_data[3]=sector[sect].floorshade;

                        sector[sect].floorshade = sp.shade;
                        sector[sect].ceilingshade = sp.shade;

                        sp.owner = (short) (sector[sect].ceilingpal<<8);
                        sp.owner |= sector[sect].floorpal;

                        //fix all the walls;

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        for(s=startwall;s<endwall;s++)
                        {
                            if((wall[s].hitag&1)==0)
                                wall[s].shade=sp.shade;
                            if( (wall[s].cstat&2) != 0 && wall[s].nextwall >= 0)
                                wall[wall[s].nextwall].shade = sp.shade;
                        }
                        break;

                    case 31:
                        hittype[i].temp_data[1] = sector[sect].floorz;
                        if(sp.ang != 1536) sector[sect].floorz = sp.z;

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        for(s=startwall;s<endwall;s++)
                            if(wall[s].hitag == 0) wall[s].hitag = 9999;

                        game.pInt.setfloorinterpolate(sect, sector[sect]);  //floorz

                        break;
                    case 32:
                        hittype[i].temp_data[1] = sector[sect].ceilingz;
                        hittype[i].temp_data[2] = sp.hitag;
                        if(sp.ang != 1536) sector[sect].ceilingz = sp.z;

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        for(s=startwall;s<endwall;s++)
                            if(wall[s].hitag == 0) wall[s].hitag = 9999;

                        game.pInt.setceilinterpolate(sect, sector[sect]);  //ceiling

                        break;

                    case 4: //Flashing lights

                        hittype[i].temp_data[2] = sector[sect].floorshade;

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        sp.owner = (short) (sector[sect].ceilingpal<<8);
                        sp.owner |= sector[sect].floorpal;

                        for(s=startwall;s<endwall;s++)
                            if(wall[s].shade > hittype[i].temp_data[3])
                                hittype[i].temp_data[3] = wall[s].shade;

                        break;

                    case 9:
                        if( sector[sect].lotag != 0 &&
                            klabs(sector[sect].ceilingz-sp.z) > 1024)
                                sector[sect].lotag |= 32768; //If its open
                    case 8:
                        //First, get the ceiling-floor shade

                    	if ( sector[sect].lotag != 0 )
                    	{
                    		if ( (sector[sect].ceilingz - sp.z) > 1024 )
                    			sector[sect].lotag |= 0x8000;
                    	}

                        hittype[i].temp_data[0] = sector[sect].floorshade;
                        hittype[i].temp_data[1] = sector[sect].ceilingshade;

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        for(s=startwall;s<endwall;s++)
                            if(wall[s].shade > hittype[i].temp_data[2])
                                hittype[i].temp_data[2] = wall[s].shade;

                        hittype[i].temp_data[3] = 1; //Take Out;

                        break;

                    case 11://Pivitor rotater
                        if(sp.ang>1024) hittype[i].temp_data[3] = 2;
                        else hittype[i].temp_data[3] = -2;
                    case 0:
                    case 2://Earthquakemakers
                    case 5://Boss Creature
                    case 6://Subway
                    case 14://Caboos
                    case 15://Subwaytype sliding door
                    case 16://That rotating blocker reactor thing
                    case 26://ESCELATOR
                    case 30://No rotational subways

                        if(sp.lotag == 0)
                        {
                            if( sector[sect].lotag == 30 )
                            {
                                if(sp.pal != 0) sprite[i].clipdist = 1;
                                else sprite[i].clipdist = 0;
                                hittype[i].temp_data[3] = sector[sect].floorz;
                                sector[sect].hitag = i;
                            }

                            for(j = 0;j < MAXSPRITES;j++)
                            {
                                if( sprite[j].statnum < MAXSTATUS )
                                if( sprite[j].picnum == SECTOREFFECTOR &&
                                    sprite[j].lotag == 1 &&
                                    sprite[j].hitag == sp.hitag)
                                {
                                    if( sp.ang == 512 )
                                    {
                                        sp.x = sprite[j].x;
                                        sp.y = sprite[j].y;
                                    }
                                    break;
                                }
                            }
                            if(j == MAXSPRITES)
                            {
                            	game.dassert("Found lonely Sector Effector (lotag 0) at ("+sp.x + "," + sp.y + ")");
                            }
                            sp.owner = (short) j;
                        }

                        startwall = sector[sect].wallptr;
                        endwall = startwall+sector[sect].wallnum;

                        hittype[i].temp_data[1] = tempwallptr;
                        for(s=startwall;s<endwall;s++)
                        {
                            msx[tempwallptr] = wall[s].x-sp.x;
                            msy[tempwallptr] = wall[s].y-sp.y;
                            tempwallptr++;
                            if(tempwallptr > 2047)
                            {
                            	game.dassert("Too many moving sectors at ("+wall[s].x + "," + wall[s].y + ")");
                            }
                        }
                        if( sp.lotag == 30 || sp.lotag == 6 || sp.lotag == 14 || sp.lotag == 5 )
                        {

                            startwall = sector[sect].wallptr;
                            endwall = startwall+sector[sect].wallnum;

                            if(sector[sect].hitag == -1)
                                sp.extra = 0;
                            else sp.extra = 1;

                            sector[sect].hitag = i;

                            j = 0;

                            for(s=startwall;s<endwall;s++)
                            {
                            	if ( wall[s].nextsector >= 0
                        			&& sector[wall[ s ].nextsector].hitag == 0
                                    && (sector[wall[ s ].nextsector].lotag < 3
                                     || sector[wall[ s ].nextsector].lotag == 160) )
                                {
                                    s = wall[s].nextsector;
                                    j = 1;
                                    break;
                                }
                            }

                            if(j == 0)
                            {
                            	game.dassert("Subway found no zero'd sectors with locators\nat ("+sp.x + "," + sp.y + ")");
                            }

                            sp.owner = -1;
                            hittype[i].temp_data[0] = s;

                            if(sp.lotag != 30)
                                hittype[i].temp_data[3] = sp.hitag;
                        }

                        else if(sp.lotag == 16)
                            hittype[i].temp_data[3] = sector[sect].ceilingz;

                        else if( sp.lotag == 26 )
                        {
                            hittype[i].temp_data[3] = sp.x;
                            hittype[i].temp_data[4] = sp.y;
                            if(sp.shade==sector[sect].floorshade) //UP
                                sp.zvel = -256;
                            else
                                sp.zvel = 256;

                            sp.shade = 0;
                        }
                        else if( sp.lotag == 2)
                        {
                            hittype[i].temp_data[5] = sector[sp.sectnum].floorheinum;
                            sector[sp.sectnum].floorheinum = 0;
                        }
                }

                switch(sp.lotag)
                {
                    case 6:
                    case 14:
                        j = callsound(sect,i);
                        if(j == -1) {
                        	if ( sector[sp.sectnum].floorpal == 7 )
                            	j = 456;
                           	else
                           		j = SUBWAY;
                        }
                        hittype[i].lastvx = j;
                    case 30:
                        if(numplayers > 1) break;
                    case 0:
                    case 1:
                    case 5:
                    case 11:
                    case 15:
                    case 16:
                    case 26:
                        setsectinterpolate(i);
                        break;
                }

                switch(sprite[i].lotag)
                {
                    case 150:
                    case 151:

                    case 152:
    	            case 153:
                        engine.changespritestat(i,(short)15);
//                        if(rorcnt < 16) {
//	                        if(sprite[i].lotag == 151)
//	                        	rortype[rorcnt] = 1; //ceiling
//	                        if(sprite[i].lotag == 150)
//	                        	rortype[rorcnt] = 2; //floor
//	                        rorsector[rorcnt++] = sprite[i].sectnum;
//	                        System.err.println("ROR in " + rorsector[rorcnt-1] + " " + rortype[rorcnt-1]);
//                        }
                        break;
                    default:
                        engine.changespritestat(i,(short)3);
                        break;
                }

                break;

            case SEENINE:
            case OOZFILTER:

                sp.shade = -16;
                if(sp.xrepeat <= 8)
                {
                    sp.cstat = (short)32768;
                    sp.xrepeat=sp.yrepeat=0;
                }
                else sp.cstat = 1+256;
                sp.extra = (short) (currentGame.getCON().impact_damage<<2);
                sp.owner = i;

                engine.changespritestat(i,(short)6);
                break;

            case HENSTAND:
	            sp.cstat = 257;
	            sp.clipdist = 48;
	            sp.xrepeat = 21;
	            sp.yrepeat = 15;
	            engine.changespritestat(i,(short) 2);
	            break;

            case 4770: //RA elvisbubba
            case 6659: //RA baba
	            if(currentGame.getCON().type != RRRA)
            		break;
            case 4163:
            case 4249:
            case 4504:
            case 4650:
            case 4862:
            case 4946:
            case 5015:
            case 5121:
            case 5377:
            	hittype[i].actorstayput = sp.sectnum;
            case 256:
            case 264:
            case 1344:
            case 1930:
            case 4147:
            case 4162:
            case 4260:
            case 4352:
            case 4649:
            case 4861:
            case MOSQUITO:
            case 4945:
            case MINION:
            case 5270:
            case 5274:
            case 5278:
            case 5282:
            case 5286:
            case 5317:
            case 5376:
            case 5501:
            case 5635:
            case MINIONUFO: //RA
            case 5890: //RA biker
            case 5891: //RA biker
            case 5995: //RA biker
        	case 6225: //RA biker + baba
        	case 6401: //RA biker + baba + moto
        	case 6658: //RA baba
            case 7030: //RA banjocoot
            case 7035: //RA banjobilly
            case 7192: //RA board
            case 7199: //RA henboard
        	case 7206: //RA bababoard
        	case 7280: //RA deep
        	case 8035: //RA rock
        	case 8036: //RA rock2
            case 8663: //RA green rock
            case 8705: //RA deep2

            	if(currentGame.getCON().type != RRRA
            			&& (sp.picnum == MINIONUFO
	    				|| sp.picnum == 5890
			            || sp.picnum == 5891
			            || sp.picnum == 5995
			        	|| sp.picnum == 6225
			        	|| sp.picnum == 6401
			        	|| sp.picnum == 6658
			            || sp.picnum == 7030
			            || sp.picnum == 7035
			            || sp.picnum == 7192
			            || sp.picnum == 7199
			        	|| sp.picnum == 7206
			        	|| sp.picnum == 7280
			        	|| sp.picnum == 8035
			        	|| sp.picnum == 8036
			            || sp.picnum == 8663
			            || sp.picnum == 8705))
            		break;

            	Tile pic = engine.getTile(sp.picnum);
            	switch(sp.picnum)
            	{
            		case 5376:
            		case 5377:
            		case 7030:
                        sp.xrepeat = 24;
                        sp.yrepeat = 18;
                        sp.clipdist = 4 * (pic.getWidth() * sp.xrepeat >> 7);
            			break;
            		case 5635:
            			if ( sp.pal == 34 )
                        {
            				sp.xrepeat = 22;
            				sp.yrepeat = 21;
                        }
                        else
                        {
                        	sp.xrepeat = 22;
                        	sp.yrepeat = 20;
                        }
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
            			break;
	                case 5270:
	    			case 5274:
	    			case 5278:
	    			case 5282:
	    			case 5286:
	    				sp.extra = 50;
        			case 5317:
        			case 4409:
        			case 4410:
        			case 4429:
        			case 4649:
        			case 4650:
        				sp.xrepeat = 32;
                        sp.yrepeat = 32;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
        				break;
        			case MOSQUITO:
        				sp.xrepeat = 14;
                        sp.yrepeat = 7;
                        sp.clipdist = 128;
        				break;
        			case 5015:
        				sp.xrepeat = 48;
                        sp.yrepeat = 48;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
        				break;
        			case 1930:
        				sp.xrepeat = 64;
                        sp.yrepeat = 128;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                        sp.cstat = 2;
        				break;
        			case 4260:
        			case 4945:
        			case MINION:
        			case MINION+1:
        				sp.xrepeat = 16;
                        sp.yrepeat = 16;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                        if(sp.picnum == MINION || sp.picnum == MINION+1)
                        {
                        	if ( ps[connecthead].isSwamp )
                                sp.pal = 8;
                        }
        				break;
        			case 4352:
        				sp.xrepeat = 24;
                        sp.yrepeat = 22;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
        				break;
        			case 4861:
        			case 4862:
        			case 4897:
        				if ( sp.pal != 35 )
        				{
        	                sp.xrepeat = 21;
        	                sp.yrepeat = 15;
        	                sp.clipdist = 64;
        	                break;
        				}
        				sp.xrepeat = 42;
                        sp.yrepeat = 30;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
        				break;
        			case 4147:
        			case 4162:
        			case 4163:
        			case 4249:
        			case 4504:
        			case 7035:
        			case 4770:
        				sp.xrepeat = 25;
                        sp.yrepeat = 21;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
        				break;
                    case 6658:
                    case 6659:
                    	sp.xrepeat = 20;
                        sp.yrepeat = 20;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                    	break;
                    case 7206:
                    	sp.xrepeat = 32;
                        sp.yrepeat = 32;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                    	break;
                    case 7280:
                    	sp.xrepeat = 18;
                        sp.yrepeat = 18;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                    	break;
                    case 8035:
                    case 8036:
                    	sp.xrepeat = 64;
                        sp.yrepeat = 64;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                    	break;
                    case 8663:
                    	sp.xrepeat = 64;
                        sp.yrepeat = 64;
                        sp.cstat = 514;
                        sp.x += (engine.krand() & 0x7FF) - 1024;
                        sp.y += (engine.krand() & 0x7FF) - 1024;
                        sp.z += (engine.krand() & 0x7FF) - 1024;
                    	break;
                    case 8705:
                    	if ( sp.pal == 30 )
                    	{
                    		sp.xrepeat = 26;
                    		sp.yrepeat = 26;
                    		sp.clipdist = 75;
                        }
                        else if ( sp.pal == 31 )
                        {
                        	sp.xrepeat = 36;
                        	sp.yrepeat = 36;
                        	sp.clipdist = 100;
                        }
                        else
                        {
                        	sp.xrepeat = 50;
                        	sp.yrepeat = 50;
                        	sp.clipdist = 100;
                        }
                    	break;
                    case 7192:
                    	sp.xrepeat = 16;
                        sp.yrepeat = 16;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                    	break;
                    case 7199:
                    	sp.xrepeat = 48;
                        sp.yrepeat = 48;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
                    	break;
                    case 5890:
    	            case 5891:
    	            case 6401:
    	            	sp.xrepeat = 28;
                        sp.yrepeat = 22;
                        sp.clipdist = 72;
                    	break;
    	            case 5995:
    	            	sp.xrepeat = 28;
                        sp.yrepeat = 22;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
    	            	break;
    	            case 6225:
    	            	sp.xrepeat = 26;
                        sp.yrepeat = 26;
                        sp.clipdist = (pic.getWidth() * sp.xrepeat) >> 7;
    	            	break;
    	            default:
    	            	sp.xrepeat = 40;
                        sp.yrepeat = 40;
    	            	break;
            	}

                if(j >= 0) sp.lotag = 0;

                if( ( sp.lotag > ud.player_skill ) || ud.monsters_off )
                {
                    sp.xrepeat=sp.yrepeat=0;
                    engine.changespritestat(i,(short)5);
                    break;
                }
                else
                {
                    makeitfall(currentGame.getCON(), i);

                    if(sp.picnum == RAT)
                    {
                        sp.ang = (short) (engine.krand()&2047);
                        sp.xrepeat = sp.yrepeat = 48;
                        sp.cstat = 0;
                    }
                    else
                    {
                        sp.cstat |= 257;

                        if(sp.picnum != 5501 && checkaddkills(sp))
                            ps[connecthead].max_actors_killed++;
                    }

                    if(j >= 0)
                    {
                        hittype[i].timetosleep = 0;
                        check_fta_sounds(i);
                        engine.changespritestat(i,(short)1);
                    }
                    else engine.changespritestat(i,(short)2);
                }
            	break;
            case TONGUE:
                if(j >= 0)
                    sp.ang = sprite[j].ang;
                sp.z -= 38<<8;
                sp.zvel = (short) (256-(engine.krand()&511));
                sp.xvel = (short) (64-(engine.krand()&127));
                engine.changespritestat(i,(short)4);
                break;
            case OOZ:
                sp.shade = -12;

                if(j >= 0)
                {
                    if( sprite[j].picnum == 1304 )
                        sp.pal = 8;
                    insertspriteq(i);
                }

                engine.changespritestat(i,(short)1);

                getglobalz(i);

                j = (hittype[i].floorz-hittype[i].ceilingz)>>9;

                sp.yrepeat = (short) j;
                sp.xrepeat = (short) (25-(j>>1));
                sp.cstat |= (engine.krand()&4);

                break;
            case STEAM:
                if(j >= 0)
                {
                    sp.ang = sprite[j].ang;
                    sp.cstat = 16+128+2;
                    sp.xrepeat=sp.yrepeat=1;
                    sp.xvel = -8;
                    ssp(i,CLIPMASK0);
                }
            case CEILINGSTEAM:
                engine.changespritestat(i,(short)6);
                break;
            case TRANSPORTERSTAR:
            case TRANSPORTERBEAM:
                if(j == -1) break;
                if(sp.picnum == TRANSPORTERBEAM)
                {
                    sp.xrepeat = 31;
                    sp.yrepeat = 1;
                    sp.z = sector[sprite[j].sectnum].floorz-(40<<8);
                }
                else
                {
                    if(sprite[j].statnum == 4)
                    {
                        sp.xrepeat = 8;
                        sp.yrepeat = 8;
                    }
                    else
                    {
                        sp.xrepeat = 48;
                        sp.yrepeat = 64;
                        if(sprite[j].statnum == 10 || badguy(sprite[j]) )
                            sp.z -= (32<<8);
                    }
                }

                sp.shade = -127;
                sp.cstat = 128|2;
                sp.ang = sprite[j].ang;

                sp.xvel = 128;
                engine.changespritestat(i,(short)5);
                ssp(i,CLIPMASK0);
                engine.setsprite(i,sp.x,sp.y,sp.z);
                break;
            case BLOOD:
	               sp.xrepeat = sp.yrepeat = 4;
	               sp.z -= (26<<8);
	               engine.changespritestat(i,(short)5);
	               break;
            case 2264:
            	j = sp.cstat&60;
                sp.cstat = (short) (j|1);
                engine.changespritestat(i,(short)0);
            	break;
            case FRAMEEFFECT1:
                if(j >= 0)
                {
                    sp.xrepeat = sprite[j].xrepeat;
                    sp.yrepeat = sprite[j].yrepeat;
                    if ( sprite[j].picnum == APLAYER )
                    	hittype[i].temp_data[1] = 1554;
                    else hittype[i].temp_data[1] = sprite[j].picnum;
                }
                else sp.xrepeat = sp.yrepeat = 0;

                engine.changespritestat(i,(short)5);
                break;
            case 1098:
            case 1100:
            case 2121:
            case 2122:
            	sp.lotag = 1;
            	sp.clipdist = 8;
            	sp.owner = i;
            	sp.cstat |= 257;
            	break;
            case FORCESPHERE:
                if(j == -1 )
                {
                    sp.cstat = (short) 32768;
                    engine.changespritestat(i,(short)2);
                }
                else
                {
                    sp.xrepeat = sp.yrepeat = 1;
                    engine.changespritestat(i,(short)5);
                }
                break;
            case SHELL: //From the player
            case SHOTGUNSHELL:
                if( j >= 0 )
                {
                    short snum,a;

                    if(sprite[j].picnum == APLAYER)
                    {
                        snum = sprite[j].yvel;
                        a = (short) (ps[snum].ang-(engine.krand()&63)+8);  //Fine tune

                        hittype[i].temp_data[0] = engine.krand()&1;
                        if(sp.picnum == SHOTGUNSHELL)
                            sp.z = (6<<8)+ps[snum].pyoff+ps[snum].posz-(((ps[snum].horizoff+(int)ps[snum].horiz-100)<<4) - 1600);
                        else sp.z = (3<<8)+ps[snum].pyoff+ps[snum].posz-(((ps[snum].horizoff+(int)ps[snum].horiz-100)<<4) - 1600);
                        sp.zvel = (short) -(engine.krand()&255);
                    }
                    else
                    {
                        a = sp.ang;
                        sp.z = sprite[j].z-8448;
                    }

                    sp.x = sprite[j].x+(sintable[(a+512)&2047]>>7);
                    sp.y = sprite[j].y+(sintable[a&2047]>>7);

                    sp.shade = -8;

                    sp.ang = (short) (a-512);
                    sp.xvel = 20;
                    if(sp.picnum == SHELL)
                    	sp.xrepeat=sp.yrepeat=2;
                    else sp.xrepeat=sp.yrepeat=4;

                    engine.changespritestat(i,(short)5);
                }
                break;
            case LOAFTILE:
            	sp.cstat = 257;
                sp.clipdist = 8;
                sp.xrepeat = 12;
                sp.yrepeat = 10;
                sp.xvel = 32;
                engine.changespritestat(i,(short) 1);
            	break;
            case BONELESSTILE:
            	sp.cstat = 257;
                sp.clipdist = 8;
                sp.xrepeat = 17;
                sp.yrepeat = 12;
                sp.xvel = 32;
                engine.changespritestat(i,(short) 1);
            	break;
            case HEAD1TILE:
            	sp.cstat = 257;
                sp.clipdist = 8;
                sp.xrepeat = 13;
                sp.yrepeat = 10;
                sp.xvel = 0;
                engine.changespritestat(i,(short) 1);
            	break;
            case CHICKENATILE:
            case CHICKENBTILE:
            case CHICKENCTILE:
            	sp.cstat = 257;
                sp.clipdist = 8;
                sp.xrepeat = 32;
                sp.yrepeat = 26;
                sp.xvel = 32;
                engine.changespritestat(i,(short) 1);
            	break;
            case NUGGETTILE:
            	sp.cstat = 257;
                sp.clipdist = 2;
                sp.xrepeat = 8;
                sp.yrepeat = 6;
                sp.xvel = 16;
                engine.changespritestat(i,(short) 1);
            	break;
            case BROASTEDTILE:
            	sp.cstat = 257;
                sp.clipdist = 8;
                sp.xrepeat = 13;
                sp.yrepeat = 13;
                sp.xvel = 16;
                engine.changespritestat(i,(short) 1);
            	break;
            case 3410:
            	sp.extra = 0;
            	engine.changespritestat(i,(short) 107);
            	break;
            case BOWLINGBALL:
            	sp.cstat = 256;
                sp.clipdist = 64;
                sp.xrepeat = 11;
                sp.yrepeat = 9;
                engine.changespritestat(i,(short) 2);
            	break;
            case 3440:
            	sp.cstat = 257;
                sp.clipdist = 48;
                sp.xrepeat = 23;
                sp.yrepeat = 23;
                engine.changespritestat(i,(short) 2);
            	break;
            case APLAYER:
                sp.xrepeat = sp.yrepeat = 0;
                j = ud.coop;
                if(j == 2) j = 0;

                if( ud.multimode < 2 || (ud.multimode > 1 && j != sp.lotag) )
                    engine.changespritestat(i,(short)5);
                else
                    engine.changespritestat(i,(short)10);
                break;
            case PLAYERONWATER:
                if(j >= 0)
                {
                    sp.xrepeat = sprite[j].xrepeat;
                    sp.yrepeat = sprite[j].yrepeat;
                    sp.zvel = 128;
                    if(sector[sp.sectnum].lotag != 2)
                        sp.cstat |= 32768;
                }
                engine.changespritestat(i,(short)13);
                break;
            case 1115:
            case 1168:
            case 5581:
            case 5583:
                sp.yvel = sp.hitag;
                sp.hitag = -1;
            case QUEBALL:
            case STRIPEBALL:
                if(sp.picnum == QUEBALL || sp.picnum == STRIPEBALL)
                {
                    sp.cstat = 256;
                    sp.clipdist = 8;
                }
                else
                {
                    sp.cstat |= 257;
                    sp.clipdist = 32;
                }

                engine.changespritestat(i,(short)2);
                break;

            //RA
            case MOTORCYCLE:
            	if ( ud.multimode < 2 && sp.pal == 1 )
            		sp.xrepeat = sp.yrepeat = 0;
                else
                {
                	sp.pal = 0;
                	sp.xrepeat = 18;
                	sp.yrepeat = 18;
                	sp.clipdist = engine.getTile(sp.picnum).getWidth() * sp.xrepeat >> 7;
                  	sp.owner = 100;
                  	sp.cstat |= 257;
                  	sp.lotag = 1;
                  	engine.changespritestat(i, (short)1);
                }
            	break;
            case SWAMPBUGGY:
            	if ( ud.multimode < 2 && sp.pal == 1 )
            		sp.xrepeat = sp.yrepeat = 0;
                else
                {
                	sp.pal = 0;
                	sp.xrepeat = 32;
                	sp.yrepeat = 32;
                	sp.clipdist = engine.getTile(sp.picnum).getWidth() * sp.xrepeat >> 7;
                  	sp.owner = 20;
                  	sp.cstat |= 257;
                  	sp.lotag = 1;
                  	engine.changespritestat(i, (short)1);
                }
            	break;
            case AIRPLANE:
            	sp.xrepeat = 64;
            	sp.yrepeat = 64;
            	sp.extra = sp.lotag;
              	sp.cstat |= 257;
              	engine.changespritestat(i, (short)116);
            	break;
            case 8192:
            	sp.xrepeat = sp.yrepeat = 0;
            	for (int p = connecthead; p >= 0; p = connectpoint2[p])
            		ps[p].isSwamp = true;
                break;
            case 1083:
        	case 1134:
        	case 1135:
        	case 1136:
        	case 1137:
        	case 1138:
        		sp.extra = 1;
        		if ( currentGame.getCON().camerashitable != 0 )
        			sp.cstat = 257;
        		else sp.cstat = 0;
        		if ( ud.multimode < 2 && sp.pal != 0 )
        		{
        			sp.xrepeat = sp.yrepeat = 0;
        			engine.changespritestat(i, (short)5);
        		}
        		else
        		{
        			sp.pal = 0;
        			if ( sp.picnum != 1083 )
        			{
        				sp.picnum = 1134;
        				engine.changespritestat(i, (short)1);
        			}
        		}
        		break;
        	case 6144:
        		sp.xrepeat = sp.yrepeat = 0;
        		for (int p = connecthead; p >= 0; p = connectpoint2[p])
        			ps[p].isSea = true;
        		break;
        	case 4956:
        		sp.xrepeat = 16;
                sp.yrepeat = 16;
                sp.clipdist = 0;
                sp.extra = 0;
                sp.cstat = 0;
                engine.changespritestat(i, (short)121);
        		break;
        	case 7424:
        		sp.extra = 0;
                sp.xrepeat = 0;
                sp.yrepeat = 0;
                engine.changespritestat(i, (short)11);
                break;
        	case 7936:
        		sp.xrepeat = sp.yrepeat = 0;
        		applyfog(2);
        		for (int p = connecthead; p >= 0; p = connectpoint2[p])
        			ps[p].fogtype = 2;
        		break;
        	case 8099:
        		sp.lotag = 5;
        		sp.clipdist = 0;
        		engine.changespritestat(i, (short)123);
        		break;
        	case 8165:
        		sp.lotag = 1;
                sp.clipdist = 0;
                sp.owner = i;
                sp.extra = 0;
        		engine.changespritestat(i, (short)115);
        		break;
        	case 8193:
        		sp.xrepeat = sp.yrepeat = 0;
        		for (int p = connecthead; p >= 0; p = connectpoint2[p])
        			ps[p].field_601 = 1;
        		break;
        	case 8448:
        	case 8704:
        		sp.lotag = 1;
                sp.clipdist = 0;
        		break;
        	case 8487:
        	case 8489:
        		sp.xrepeat = 32;
        		sp.yrepeat = 32;
        		sp.extra = 0;
        		sp.cstat |= 257;
        		sp.hitag = 0;
        		engine.changespritestat(i, (short)117);
        		break;
        	case 8593:
        		sp.lotag = 1;
                sp.clipdist = 0;
                sp.owner = i;
                sp.extra = 0;
        		engine.changespritestat(i, (short)122);
        		break;
	    }
	    game.pInt.setsprinterpolate(i, sprite[i]);
	    return i;
	}

	public static void vglass(int x,int y,int a,int wn,int n)
	{
	    int z, zincs;
	    short sect;

	    sect = wall[wn].nextsector;
	    if(sect == -1) return;
	    zincs = ( sector[sect].floorz-sector[sect].ceilingz ) / n;

	    for(z = sector[sect].ceilingz;z < sector[sect].floorz; z += zincs )
	        EGS(sect,x,y,z-(engine.krand()&8191),GLASSPIECES+(z&(engine.krand()%3)),-32,36,36,a+128-(engine.krand()&255),16+(engine.krand()&31),0,-1,(short)5);
	}

	public static void lotsofglass(int i,int wallnum,int n)
	{
	     int j, xv, yv, z, x1, y1;
	     short sect, a;

	     sect = -1;

	     if(wallnum < 0)
	     {
	        for(j=n-1; j >= 0 ;j--)
	        {
	            a = (short) (sprite[i].ang-256+(engine.krand()&511)+1024);
	            EGS(sprite[i].sectnum,sprite[i].x,sprite[i].y,sprite[i].z,2021,-32,36,36,a,32+(engine.krand()&63),1024-(engine.krand()&1023),i,(short)5);
	        }
	        return;
	     }

	     j = n+1;

	     x1 = wall[wallnum].x;
	     y1 = wall[wallnum].y;

	     xv = wall[wall[wallnum].point2].x-x1;
	     yv = wall[wall[wallnum].point2].y-y1;

	     x1 -= ksgn(yv);
	     y1 += ksgn(xv);

	     xv /= j;
	     yv /= j;

	     for(j=n;j>0;j--)
         {
              x1 += xv;
              y1 += yv;

	          sect = engine.updatesector(x1,y1,sect);
	          if(sect >= 0)
	          {
	              z = sector[sect].floorz-(engine.krand()&(klabs(sector[sect].ceilingz-sector[sect].floorz)));
	              if( z < -(32<<8) || z > (32<<8) )
	                  z = sprite[i].z-(32<<8)+(engine.krand()&((64<<8)-1));
	              a = (short) (sprite[i].ang-1024);
	              EGS(sprite[i].sectnum,x1,y1,z,2021,-32,36,36,a,32+(engine.krand()&63),-(engine.krand()&1023),i,(short)5);
	          }
         }
	}

	public static void lotsofglass2(int i,int wallnum,int n)
	{
	     int j, xv, yv, z, x1, y1;
	     short sect, a;

	     sect = -1;

	     if(wallnum < 0)
	     {
	        for(j=n-1; j >= 0 ;j--)
	        {
	            a = (short) (sprite[i].ang-256+(engine.krand()&511)+1024);
	            EGS(sprite[i].sectnum,sprite[i].x,sprite[i].y,sprite[i].z,1256 + j%3,-32,36,36,a,32+(engine.krand()&63),1024-(engine.krand()&1023),i,(short)5);
	        }
	        return;
	     }

	     j = n+1;

	     x1 = wall[wallnum].x;
	     y1 = wall[wallnum].y;

	     xv = wall[wall[wallnum].point2].x-x1;
	     yv = wall[wall[wallnum].point2].y-y1;

	     x1 -= ksgn(yv);
	     y1 += ksgn(xv);

	     xv /= j;
	     yv /= j;

	     for(j=n;j>0;j--)
         {
              x1 += xv;
              y1 += yv;

	          sect = engine.updatesector(x1,y1,sect);
	          if(sect >= 0)
	          {
	              z = sector[sect].floorz-(engine.krand()&(klabs(sector[sect].ceilingz-sector[sect].floorz)));
	              if( z < -(32<<8) || z > (32<<8) )
	                  z = sprite[i].z-(32<<8)+(engine.krand()&((64<<8)-1));
	              a = (short) (sprite[i].ang-1024);
	              EGS(sprite[i].sectnum,x1,y1,z,1256 + j%3,-32,36,36,a,32+(engine.krand()&63),-(engine.krand()&1023),i,(short)5);
	          }
         }
	}

	public static void spriteglass(int i,int n)
	{
	    int j, k, a, z;

	    for(j=n;j>0;j--)
	    {
	        a = engine.krand()&2047;
	        z = sprite[i].z-((engine.krand()&16)<<8);
	        k = EGS(sprite[i].sectnum,sprite[i].x,sprite[i].y,z,GLASSPIECES+(j%3),engine.krand()&15,36,36,a,32+(engine.krand()&63),-512-(engine.krand()&2047),i,(short)5);
	        sprite[k].pal = sprite[i].pal;
	    }
	}

	public static void ceilingglass(int i,short sectnum,int n)
	{
	     int j, xv, yv, z, x1, y1, endwall;
	     short a,s, startwall;

	     startwall = sector[sectnum].wallptr;
	     endwall = (startwall+sector[sectnum].wallnum);

	     for(s=startwall;s<(endwall-1);s++)
	     {
	         x1 = wall[s].x;
	         y1 = wall[s].y;

	         xv = (wall[s+1].x-x1)/(n+1);
	         yv = (wall[s+1].y-y1)/(n+1);

	         for(j=n;j>0;j--)
	         {
	              x1 += xv;
	              y1 += yv;
	              a = (short) (engine.krand()&2047);
	              z = sector[sectnum].ceilingz+((engine.krand()&15)<<8);
	              EGS(sectnum,x1,y1,z,GLASSPIECES+(j%3),-32,36,36,a,(engine.krand()&31),0,i,(short)5);
	          }
	     }
	}

	public static void lotsofcolourglass(int i,int wallnum,int n)
	{
	     int j, xv, yv, z, x1, y1;
	     short sect = -1, a, k;

	     if(wallnum < 0)
	     {
	        for(j=n-1; j >= 0 ;j--)
	        {
	            a = (short) (engine.krand()&2047);
	            k = EGS(sprite[i].sectnum,sprite[i].x,sprite[i].y,sprite[i].z-(engine.krand()&(63<<8)),GLASSPIECES+(j%3),-32,36,36,a,32+(engine.krand()&63),1024-(engine.krand()&2047),i,(short)5);
	            sprite[k].pal = (short) (engine.krand()&15);
	        }
	        return;
	     }

	     j = n+1;
	     x1 = wall[wallnum].x;
	     y1 = wall[wallnum].y;

	     xv = (wall[wall[wallnum].point2].x-wall[wallnum].x)/j;
	     yv = (wall[wall[wallnum].point2].y-wall[wallnum].y)/j;

	     for(j=n;j>0;j--)
	     {
	    	 x1 += xv;
	    	 y1 += yv;

	    	 sect = engine.updatesector(x1,y1,sect);
	    	 z = sector[sect].floorz-(engine.krand()&(klabs(sector[sect].ceilingz-sector[sect].floorz)));
	    	 if( z < -(32<<8) || z > (32<<8) )
	              z = sprite[i].z-(32<<8)+(engine.krand()&((64<<8)-1));
	    	 a = (short) (sprite[i].ang-1024);
	    	 k = EGS(sprite[i].sectnum,x1,y1,z,GLASSPIECES+(j%3),-32,36,36,a,32+(engine.krand()&63),-(engine.krand()&2047),i,(short)5);
	    	 sprite[k].pal = (short) (engine.krand()&7);
	     }
	}

	public static void lotsofmoney(SPRITE s, int n) {
		for (int i = n; i > 0; i--) {
			int j = EGS(s.sectnum, s.x, s.y,
					s.z - (engine.krand() % (47 << 8)), FEATHERS, -32, 8, 8,
					engine.krand() & 2047, 0, 0, 0, (short)5);
			sprite[j].cstat = (short) (engine.krand() & 12);
		}
	}

	public static void guts(SPRITE s, int gtype, int n, int p) {
		if(!isValidSector(s.sectnum))
			return;

		int gutz, floorz;
		int i;
		char sx, sy;
		byte pal;

		if (badguy(s) && s.xrepeat < 16)
			sx = sy = 8;
		else
			sx = sy = 32;

		gutz = s.z - (8 << 8);
		floorz = engine.getflorzofslope(s.sectnum, s.x, s.y);

		if (gutz > (floorz - (8 << 8)))
			gutz = floorz - (8 << 8);

		if (badguy(s) && s.pal == 6)
			pal = 6;
		else {
			if(currentGame.getCON().type == RRRA) {
				if ( s.picnum == MINION && s.pal == 8 )
					pal = 8;
				else if ( s.picnum == MINION && s.pal == 19 )
					pal = 19;
				else pal  = 0;
			} else
			pal = 0;
		}

		for (int j = 0; j < n; j++) {
			int a = engine.krand() & 2047;
			i = EGS(s.sectnum, s.x + (engine.krand() & 255) - 128, s.y
					+ (engine.krand() & 255) - 128, gutz
					- (engine.krand() & 8191), gtype, -32, sx >> 1, sy >> 1, a,
					48 + (engine.krand() & 31), -512 - (engine.krand() & 2047),
					ps[p].i, (short)5);

			 switch ( pal )
			 {
			 case 6:
				 sprite[i].pal = 6;
				 break;
			 case 8:
				 sprite[i].pal = 8;
				 break;
			 case 19:
				 sprite[i].pal = 19;
				 break;
			 }
		}
	}

	public static void gutsdir(SPRITE s, int gtype, int n, int p) {
		int gutz, floorz;
		char sx, sy;

		if (badguy(s) && s.xrepeat < 16)
			sx = sy = 8;
		else
			sx = sy = 32;

		gutz = s.z - (8 << 8);
		floorz = engine.getflorzofslope(s.sectnum, s.x, s.y);

		if (gutz > (floorz - (8 << 8)))
			gutz = floorz - (8 << 8);

		for (int j = 0; j < n; j++) {
			int a = engine.krand() & 2047;
			EGS(s.sectnum, s.x, s.y, gutz, gtype, -32, sx, sy, a,
					256 + (engine.krand() & 127), -512
							- (engine.krand() & 2047), ps[p].i,(short)5);
		}
	}
}
