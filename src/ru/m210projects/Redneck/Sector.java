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
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Types.ANIMATION.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Weapons.*;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Sector {

	public static boolean ceilingspace(short sectnum) {
		if ((sector[sectnum].ceilingstat & 1) != 0
				&& sector[sectnum].ceilingpal == 0) {
			switch (sector[sectnum].ceilingpicnum) {
			case 1022:
			case 1026:
				return true;
			}
		}
		return false;
	}

	public static boolean floorspace(short sectnum) {
		if ((sector[sectnum].floorstat & 1) != 0
				&& sector[sectnum].ceilingpal == 0) {
			switch (sector[sectnum].floorpicnum) {
			case 1022:
			case 1026:
				return true;
			}
		}
		return false;
	}

	public static boolean wallswitchcheck(int i)
	{
	    switch(sprite[i].picnum)
	    {
		    case 82:
		    case 84:
		    case 85:
		    case 86:
		    case 87:
		    case 88:
		    case 89:
		    case 90:
		    case 91:
		    case 92:
		    case 93:
		    case 94:
		    case 95:
		    case 98:
		    case 99:
		    case 100:
		    case 101:
		    case 121:
		    case 122:
		    case 123:
		    case 124:
		    case 125:
		    case 126:
		    case 127:
		    case 128:
		    case 129:
		    case 250:
		    case 251:
		    case 1278:
		    case 1279:
		    case 2222:
		    case 2223:
		    case 2224:
		    case 2225:
		    case 2226:
		    case 2227:
		    case 2249:
		    case 2250:
		    case 2254:
		    case 2255:
		    case 2259:
		    case 2260:

		    case 8048: //RA
		    case 8049:
		    case 8050:
		    case 8051:
		    case 8464:
		    case 8465:
	            return true;
	    }
	    return false;
	}

	public static boolean haltsoundhack;
	public static int callsound(int sn, int whatsprite)
	{
		if(haltsoundhack)
	    {
	        haltsoundhack = false;
	        return -1;
	    }

	    short i = headspritesect[sn];
	    while(i >= 0)
	    {
	        if( sprite[i].picnum == MUSICANDSFX && sprite[i].lotag < 1000 )
	        {
	            if(whatsprite == -1) whatsprite = i;

	            if(hittype[i].temp_data[0] == 0)
	            {
	                if( sprite[i].lotag < NUM_SOUNDS && (currentGame.getCON().soundm[sprite[i].lotag]&16) == 0)
	                {
	                    if(sprite[i].lotag != 0)
	                    {
	                        spritesound(sprite[i].lotag,whatsprite);
	                        if(sprite[i].hitag != 0 && sprite[i].lotag != sprite[i].hitag && sprite[i].hitag < NUM_SOUNDS)
	                            stopsound(sprite[i].hitag);
	                    }

	                    if( (sector[sprite[i].sectnum].lotag&0xff) != 22)
	                    	hittype[i].temp_data[0] = 1;
	                }
	            }
	            else if(sprite[i].hitag < NUM_SOUNDS)
	            {
	                if(sprite[i].hitag != 0) spritesound(sprite[i].hitag,whatsprite);
	                if( (currentGame.getCON().soundm[sprite[i].lotag]&1) != 0 || ( sprite[i].hitag != 0 && sprite[i].hitag != sprite[i].lotag ) )
	                    stopsound(sprite[i].lotag);
	                hittype[i].temp_data[0] = 0;
	            }
	            return sprite[i].lotag;
	        }
	        i = nextspritesect[i];
	    }
	    return -1;
	}

	public static boolean check_activator_motion( int lotag )
	{
	    int i = headspritestat[8];
	    while ( i >= 0 )
	    {
	        if ( sprite[i].lotag == lotag )
	        {
	        	SPRITE s = sprite[i];

	        	for ( int j = gAnimationCount-1; j >= 0; j-- )
	                if ( s.sectnum == gAnimationData[j].sect )
	                    return( true );

	            int j = headspritestat[3];
	            while ( j >= 0 )
	            {
	                if(s.sectnum == sprite[j].sectnum)
	                    switch(sprite[j].lotag)
	                    {
	                        case 11:
	                        case 30:
	                            if ( hittype[j].temp_data[4] != 0 )
	                                return( true );
	                            break;
	                        case 20:
	                        case 31:
	                        case 32:
	                        case 18:
	                            if ( hittype[j].temp_data[0] != 0 )
	                                return( true );
	                            break;
	                    }

	                j = nextspritestat[j];
	            }
	        }
	        i = nextspritestat[i];
	    }
	    return( false );
	}

	public static boolean isadoorwall(int dapic)
	{
		switch(dapic)
		{
			case 102:
			case 103:
			case 104:
			case 105:
			case 106:
			case 107:
			case 108:
			case 109:
			case 110:
			case 111:
			case 115:
			case 116:
			case 117:
			case 119:
			case 229:
			case 230:
			case 232:
			case 1856: //METAL
			case 1877: //WOOD
			case 2261:
			case 2263:
			case 2267:
			case 2268:
				return true;
		}
		return false;
	}

	public static boolean isadoorwall2(int dapic)
	{
	    switch(dapic)
	    {
		    case 1792:
		    case 1801:
		    case 1805:
		    case 1807:
		    case 1808:
		    case 1812:
		    case 1821:
		    case 1826:
		    case 1850:
		    case 1851:
		    case 1856:
		    case 1877:
		    case 1938:
		    case 1942:
		    case 1944:
		    case 1945:
		    case 1951:
		    case 1961:
		    case 1964:
		    case 1985:
		    case 1995:
		    case 2022:
		    case 2052:
		    case 2053:
		    case 2060:
		    case 2074:
		    case 2132:
		    case 2136:
		    case 2139:
		    case 2150:
		    case 2178:
		    case 2186:
		    case 2319:
		    case 2321:
		    case 2326:
		    case 2329:
		    case 2578:
		    case 2581:
		    case 2610:
		    case 2613:
		    case 2621:
		    case 2622:
		    case 2676:
		    case 2732:
		    case 2831:
		    case 2832:
		    case 2842:
		    case 2940:
		    case 2970:
		    case 3083:
		    case 3100:
		    case 3155:
		    case 3195:
		    case 3232:
		    case 3600:
		    case 3631:
		    case 3635:
		    case 3637:
		    case 3645:
		    case 3646:
		    case 3647:
		    case 3652:
		    case 3653:
		    case 3671:
		    case 3673:
		    case 3684:
		    case 3708:
		    case 3714:
		    case 3716:
		    case 3723:
		    case 3725:
		    case 3737:
		    case 3754:
		    case 3762:
		    case 3763:
		    case 3764:
		    case 3765:
		    case 3767:
		    case 3793:
		    case 3814:
		    case 3815:
		    case 3819:
		    case 3827:
		    case 3837:

		    	//RA
		    case 1996:
		    case 2382:
		    case 2961:
		    case 3804:
		    case 7430:
		    case 7467:
		    case 7469:
		    case 7470:
		    case 7475:
		    case 7566:
		    case 7576:
		    case 7716:
		    case 8063:
		    case 8067:
		    case 8076:
		    case 8106:
		    case 8379:
		    case 8380:
		    case 8565:
		    case 8605:
	            return true;
	    }
	    return false;
	}

	public static boolean isanunderoperator(int lotag)
	{
	    switch(lotag&0xff)
	    {
	        case 15:
	        case 16:
	        case 17:
	        case 18:
	        case 19:
	        case 26:
	            return true;
	    }
	    return false;
	}

	public static boolean isanearoperator(int lotag)
	{
	    switch(lotag&0xff)
	    {
		    case 9:
		    case 15:
		    case 16:
		    case 17:
		    case 18:
		    case 19:
		    case 20:
		    case 21:
		    case 22:
		    case 23:
		    case 25:
		    case 26:
		    case 29:
		    case 41:
	            return true;
	    }
	    return false;
	}

	public static int checkcursectnums(int sect)
	{
	    for(int i=connecthead;i>=0;i=connectpoint2[i])
	        if( sprite[ps[i].i].sectnum == sect ) return i;
	    return -1;
	}

	public static int ldist(SPRITE s1, SPRITE s2)
	{
	    int vx = s1.x - s2.x;
	    int vy = s1.y - s2.y;
	    return(FindDistance2D(vx,vy) + 1);
	}

	public static int dist(SPRITE s1, SPRITE s2)
	{
		int vx = s1.x - s2.x;
		int vy = s1.y - s2.y;
		int vz = s1.z - s2.z;
	    return(FindDistance3D(vx,vy,vz>>4));
	}

	public static int player_dist;
	public static int findplayer(SPRITE s)
	{
	    if(ud.multimode < 2)
	    {
	    	player_dist = klabs(ps[myconnectindex].oposx-s.x) + klabs(ps[myconnectindex].oposy-s.y) + ((klabs(ps[myconnectindex].oposz-s.z+(28<<8)))>>4);
	        return myconnectindex;
	    }

	    int closest = 0x7fffffff;
	    int closest_player = 0;

	    for(int j=connecthead;j>=0;j=connectpoint2[j])
	    {
	        int x = klabs(ps[j].oposx-s.x) + klabs(ps[j].oposy-s.y) + ((klabs(ps[j].oposz-s.z+(28<<8)))>>4);
	        if( x < closest && sprite[ps[j].i].extra > 0 )
	        {
	            closest_player = j;
	            closest = x;
	        }
	    }

	    player_dist = closest;
	    return closest_player;
	}

	public static int findotherplayer(int p)
	{
	    int closest = 0x7fffffff;
	    int closest_player = p;

	    for(int j=connecthead;j>=0;j=connectpoint2[j])
	        if(p != j && sprite[ps[j].i].extra > 0)
	    {
	        int x = klabs(ps[j].oposx-ps[p].posx) + klabs(ps[j].oposy-ps[p].posy) + (klabs(ps[j].oposz-ps[p].posz)>>4);

	        if( x < closest )
	        {
	            closest_player = j;
	            closest = x;
	        }
	    }

	    player_dist = closest;
	    return closest_player;
	}

	public static void animatewalls()
	{
	    int i, j, p, t;

	    if ( ps[connecthead].isSea )
	    {
	    	for ( i = 0; i < MAXWALLS; ++i )
	    	{
	    		if(wall[i] == null) continue;
	    		if ( wall[i].picnum == 7873 )
	    			wall[i].xpanning += 6;
	    		else if ( wall[i].picnum == 7870 )
	    			wall[i].xpanning += 6;
	    	}
	    }

	    for(p=0;p < numanimwalls ;p++)
	    {
	        i = animwall[p].wallnum;
	        j = wall[i].picnum;

	            switch(j)
	            {
		            case 159:
		            case 160:
		            case 161:
		            case 162:
		            case 163:
		            case 167:
		            case 168:
		            case 169:
		            case 170:
		            case 171:
	                    if( (engine.krand()&255) < 16)
	                    {
	                        animwall[p].tag = wall[i].picnum;
	                        wall[i].picnum = SCREENBREAK6;
	                    }

	                    continue;

	                case SCREENBREAK6:
	                case SCREENBREAK7:
	                case SCREENBREAK8:

	                    if(animwall[p].tag >= 0 )
	                        wall[i].picnum = (short) animwall[p].tag;
	                    else
	                    {
	                        wall[i].picnum++;
	                        if(wall[i].picnum == (SCREENBREAK6+3) )
	                            wall[i].picnum = SCREENBREAK6;
	                    }
	                    continue;

	            }

	        if((wall[i].cstat&16) != 0)
	            switch(wall[i].overpicnum)
	        {
	            case W_FORCEFIELD:
	            case W_FORCEFIELD+1:
	            case W_FORCEFIELD+2:

	                t = animwall[p].tag;

	                if((wall[i].cstat&254) != 0)
	                {
	                    wall[i].xpanning -= t>>10;
	                    wall[i].ypanning -= t>>10;

	                    if(wall[i].extra == 1)
	                    {
	                        wall[i].extra = 0;
	                        animwall[p].tag = 0;
	                    }
	                    else
	                        animwall[p].tag+=128;

	                    if( animwall[p].tag < (128<<4) )
	                    {
	                        if( (animwall[p].tag&128) != 0 )
	                            wall[i].overpicnum = W_FORCEFIELD;
	                        else wall[i].overpicnum = W_FORCEFIELD+1;
	                    }
	                    else
	                    {
	                        if( (engine.krand()&255) < 32 )
	                            animwall[p].tag = 128<<(engine.krand()&3);
	                        else wall[i].overpicnum = W_FORCEFIELD+1;
	                    }
	                }

	                break;
	        }
	    }
	}

	public static boolean activatewarpelevators(int s, int d) //Parm = sectoreffectornum
	{
	    short i, sn;

	    sn = sprite[s].sectnum;

	    // See if the sector exists

	    i = headspritestat[3];
	    while(i >= 0)
	    {
	        if( sprite[i].lotag == 17 )
	            if( sprite[i].hitag == sprite[s].hitag )
	                if( (klabs(sector[sn].floorz-hittype[s].temp_data[2]) > sprite[i].yvel) ||
	                    (sector[sprite[i].sectnum].hitag == (sector[sn].hitag-d) ) )
	                        break;
	        i = nextspritestat[i];
	    }

	    if(i==-1)
	    {
	        d = 0;
	        return true; // No find
	    }
	    else
	    {
	        if(d == 0)
	            spritesound(ELEVATOR_OFF,s);
	        else spritesound(ELEVATOR_ON,s);
	    }


	    i = headspritestat[3];
	    while(i >= 0)
	    {
	        if( sprite[i].lotag == 17 )
	            if( sprite[i].hitag == sprite[s].hitag )
	            {
	                hittype[i].temp_data[0] = d;
	                hittype[i].temp_data[1] = d; //Make all check warp
	            }
	        i = nextspritestat[i];
	    }
	    return false;
	}

	public static int[] wallfind = new int[2];
	public static void operatesectors(int sn, int ii)
	{
	    int i, j = 0, l, q, startwall, endwall;
	    SECTOR sptr = sector[sn];
	    switch(sptr.lotag&(0xffff-49152))
	    {
	    	case 41:
	    		if ( numjaildoors > 0 )
	    	    {
	    			for(i = 0; i < numjaildoors; i++)
	    			{
	    				if(sptr.hitag == jailunique[i])
	    				{
	    					if ( jailstatus[i] == 0 )
	    		            {
	    		              jailstatus[i] = 1;
	    		              jailcount2[i] = jaildistance[i];
	    		              if(jailsound[i] != 0)
	    		            	  spritesound(jailsound[i], ps[screenpeek].i);
	    		            }
	    		            if ( jailstatus[i] == 2 )
	    		            {
	    		              jailstatus[i] = 3;
	    		              jailcount2[i] = jaildistance[i];
	    		              if(jailsound[i] != 0)
	    		            	  spritesound(jailsound[i], ps[screenpeek].i);
	    		            }
	    				}
	    			}
	    	    }
	    		break;
	    	case 7:
	    		startwall = sptr.wallptr;
	            endwall = startwall+sptr.wallnum;
	            for(i = startwall; i < endwall; i++)
	            {
	            	setanimation(sn,i, wall[i].x+1024,4, WALLX);
	            	setanimation(sn,wall[i].point2, wall[wall[i].point2].x+1024,4, WALLX);
	            }
	    		break;
	        case 30:
	            j = sector[sn].hitag;
	            if( hittype[j].tempang == 0 ||
	                hittype[j].tempang == 256)
	                    callsound(sn,ii);
	            if(sprite[j].extra == 1)
	                sprite[j].extra = 3;
	            else sprite[j].extra = 1;
	            break;

	        case 31:

	            j = sector[sn].hitag;
	            if(hittype[j].temp_data[4] == 0)
	                hittype[j].temp_data[4] = 1;

	            callsound(sn,ii);
	            break;

	        case 26: //The split doors
	            i = getanimationgoal(sptr, CEILZ);
	            if(i == -1) //if the door has stopped
	            {
	            	haltsoundhack = true;
	                sptr.lotag &= 0xff00;
	                sptr.lotag |= 22;
	                operatesectors(sn,ii);
	                sptr.lotag &= 0xff00;
	                sptr.lotag |= 9;
	                operatesectors(sn,ii);
	                sptr.lotag &= 0xff00;
	                sptr.lotag |= 26;
	            }
	            return;

	        case 9:
	        {
	            int dax,day,dax2,day2,sp;

	            startwall = sptr.wallptr;
	            endwall = startwall+sptr.wallnum-1;

	            sp = sptr.extra>>4;

	            //first find center point by averaging all points
	            dax = 0; day = 0;
	            for(i=startwall;i<=endwall;i++)
	            {
	                dax += wall[i].x;
	                day += wall[i].y;
	            }
	            dax /= (endwall-startwall+1);
	            day /= (endwall-startwall+1);

	            //find any points with either same x or same y coordinate
	            //  as center (dax, day) - should be 2 points found.
	            wallfind[0] = -1;
	            wallfind[1] = -1;
	            for(i=startwall;i<=endwall;i++)
	                if ((wall[i].x == dax) || (wall[i].y == day))
	                {
	                    if (wallfind[0] == -1)
	                        wallfind[0] = i;
	                    else wallfind[1] = i;
	                }

	            for(j=0;j<2;j++)
	            {
	                if ((wall[wallfind[j]].x == dax) && (wall[wallfind[j]].y == day))
	                {
	                    //find what direction door should open by averaging the
	                    //  2 neighboring points of wallfind[0] & wallfind[1].
	                    i = wallfind[j]-1; if (i < startwall) i = endwall;
	                    dax2 = ((wall[i].x+wall[wall[wallfind[j]].point2].x)>>1)-wall[wallfind[j]].x;
	                    day2 = ((wall[i].y+wall[wall[wallfind[j]].point2].y)>>1)-wall[wallfind[j]].y;
	                    if (dax2 != 0)
	                    {
	                        dax2 = wall[wall[wall[wallfind[j]].point2].point2].x;
	                        dax2 -= wall[wall[wallfind[j]].point2].x;
	                        setanimation(sn,wallfind[j],wall[wallfind[j]].x+dax2,sp, WALLX);
	                        setanimation(sn,i,wall[i].x+dax2,sp, WALLX);
	                        setanimation(sn,wall[wallfind[j]].point2,wall[wall[wallfind[j]].point2].x+dax2,sp, WALLX);
	                        callsound(sn,ii);
	                    }
	                    else if (day2 != 0)
	                    {
	                        day2 = wall[wall[wall[wallfind[j]].point2].point2].y;
	                        day2 -= wall[wall[wallfind[j]].point2].y;
	                        setanimation(sn,wallfind[j],wall[wallfind[j]].y+day2,sp, WALLY);
	                        setanimation(sn,i,wall[i].y+day2,sp, WALLY);
	                        setanimation(sn,wall[wallfind[j]].point2,wall[wall[wallfind[j]].point2].y+day2,sp, WALLY);
	                        callsound(sn,ii);
	                    }
	                }
	                else
	                {
	                    i = wallfind[j]-1; if (i < startwall) i = endwall;
	                    dax2 = ((wall[i].x+wall[wall[wallfind[j]].point2].x)>>1)-wall[wallfind[j]].x;
	                    day2 = ((wall[i].y+wall[wall[wallfind[j]].point2].y)>>1)-wall[wallfind[j]].y;
	                    if (dax2 != 0)
	                    {
	                    	//setanimation(short animsect,long *animptr, long thegoal, long thevel)
	                        setanimation(sn,wallfind[j],dax,sp, WALLX);
	                        setanimation(sn,i,dax+dax2,sp, WALLX);
	                        setanimation(sn,wall[wallfind[j]].point2,dax+dax2,sp, WALLX);
	                        callsound(sn,ii);
	                    }
	                    else if (day2 != 0)
	                    {
	                        setanimation(sn,wallfind[j],day,sp, WALLY);
	                        setanimation(sn,i,day+day2,sp, WALLY);
	                        setanimation(sn,wall[wallfind[j]].point2,day+day2,sp, WALLY);
	                        callsound(sn,ii);
	                    }
	                }
	            }

	        }
	        return;

	        case 15://Warping elevators

	            if(sprite[ii].picnum != APLAYER) return;

	            i = headspritesect[sn];
	            while(i >= 0)
	            {
	                if(sprite[i].picnum==SECTOREFFECTOR && sprite[i].lotag == 17 ) break;
	                i = nextspritesect[i];
	            }

	            if(i == -1) return;

	            if(sprite[ii].sectnum == sn)
	            {
	                if( activatewarpelevators(i,-1) )
	                    activatewarpelevators(i,1);
	                else if( activatewarpelevators(i,1) )
	                    activatewarpelevators(i,-1);
	                return;
	            }
	            else
	            {
	                if(sptr.floorz > sprite[i].z)
	                    activatewarpelevators(i,-1);
	                else
	                    activatewarpelevators(i,1);
	            }

	            return;

	        case 16:
	        case 17:

	            i = getanimationgoal(sptr, FLOORZ);
	            if(i == -1)
	            {
	                i = engine.nextsectorneighborz(sn,sptr.floorz,1,1);
	                if( i == -1 )
	                {
	                    i = engine.nextsectorneighborz(sn,sptr.floorz,1,-1);
	                    if( i == -1 ) return;
	                    j = sector[i].floorz;
	                    setanimation(sn,sn,j,sptr.extra, FLOORZ);
	                }
	                else
	                {
	                    j = sector[i].floorz;
	                    setanimation(sn,sn,j,sptr.extra, FLOORZ);
	                }
	                callsound(sn,ii);
	            }

	            return;

	        case 18:
	        case 19:

	            i = getanimationgoal(sptr,FLOORZ);

	            if(i==-1)
	            {
	                i = engine.nextsectorneighborz(sn,sptr.floorz,1,-1);
	                if(i==-1) i = engine.nextsectorneighborz(sn,sptr.floorz,1,1);
	                if(i==-1) return;
	                j = sector[i].floorz;
	                q = sptr.extra;
	                l = sptr.ceilingz-sptr.floorz;
	                setanimation(sn,sn,j,q,FLOORZ);
	                setanimation(sn,sn,j+l,q,CEILZ);
	                callsound(sn,ii);
	            }
	            return;

	        case 29:

	            if((sptr.lotag&0x8000) != 0)
	                j = sector[engine.nextsectorneighborz(sn,sptr.ceilingz,1,1)].floorz;
	            else
	                j = sector[engine.nextsectorneighborz(sn,sptr.ceilingz,-1,-1)].ceilingz;

	            i = headspritestat[3]; //Effectors
	            while(i >= 0)
	            {
	                if( (sprite[i].lotag == 22) &&
	                    (sprite[i].hitag == sptr.hitag) )
	                {
	                    sector[sprite[i].sectnum].extra = (short) -sector[sprite[i].sectnum].extra;

	                    hittype[i].temp_data[0] = sn;
	                    hittype[i].temp_data[1] = 1;
	                }
	                i = nextspritestat[i];
	            }

	            sptr.lotag ^= 0x8000;

	            setanimation(sn,sn,j,sptr.extra, CEILZ);

	            callsound(sn,ii);

	            return;

	        case 20:

	            boolean REDODOOR;
	            do {
	            	REDODOOR = false;
		            if((sptr.lotag&0x8000) != 0)
		            {
		                i = headspritesect[sn];
		                while(i >= 0)
		                {
		                    if(sprite[i].statnum == 3 && sprite[i].lotag==9)
		                    {
		                        j = sprite[i].z;
		                        break;
		                    }
		                    i = nextspritesect[i];
		                }
		                if(i==-1)
		                    j = sptr.floorz;
		            }
		            else
		            {
		                j = engine.nextsectorneighborz(sn,sptr.ceilingz,-1,-1);

		                if(j >= 0) j = sector[j].ceilingz;
		                else
		                {
		                    sptr.lotag |= 32768;
		                    REDODOOR = true;
		                }
		            }
	            } while(REDODOOR);

	            sptr.lotag ^= 0x8000;

	            setanimation(sn, sn,j,sptr.extra,CEILZ);
	            callsound(sn,ii);

	            return;

	        case 21:
	        	i = getanimationgoal(sptr, FLOORZ);
	            if (i >= 0)
	            {
	                if (gAnimationData[i].goal == sptr.ceilingz)
	                	gAnimationData[i].goal = sector[engine.nextsectorneighborz(sn,sptr.ceilingz,1,1)].floorz;
	                else gAnimationData[i].goal = sptr.ceilingz;
	                j = gAnimationData[i].goal;
	            }
	            else
	            {
	                if (sptr.ceilingz == sptr.floorz)
	                    j = sector[engine.nextsectorneighborz(sn,sptr.ceilingz,1,1)].floorz;
	                else j = sptr.ceilingz;

	                sptr.lotag ^= 0x8000;

	                if(setanimation(sn,sn,j,sptr.extra, FLOORZ) >= 0)
	                    callsound(sn,ii);
	            }
	            return;

	        case 22:

	            // REDODOOR22:

	            if ( (sptr.lotag&0x8000) != 0 )
	            {
	                q = (sptr.ceilingz+sptr.floorz)>>1;
	                j = setanimation(sn,sn,q,sptr.extra,FLOORZ);
	                j = setanimation(sn,sn,q,sptr.extra,CEILZ);
	            }
	            else
	            {
	            	int fsect = engine.nextsectorneighborz(sn,sptr.floorz,1,1);
	            	if(fsect != -1) {
		                q = sector[fsect].floorz;
		                j = setanimation(sn,sn,q,sptr.extra,FLOORZ);
	            	}
	            	int csect = engine.nextsectorneighborz(sn,sptr.ceilingz,-1,-1);
	            	if(csect != -1) {
		                q = sector[csect].ceilingz;
		                j = setanimation(sn,sn,q,sptr.extra,CEILZ);
	            	}
	            }

	            sptr.lotag ^= 0x8000;

	            callsound(sn,ii);

	            return;

	        case 23: //Swingdoor

	            j = -1;
	            q = 0;

	            i = headspritestat[3];
	            while(i >= 0)
	            {
	                if( sprite[i].lotag == 11 && sprite[i].sectnum == sn && hittype[i].temp_data[4] == 0)
	                {
	                    j = i;
	                    break;
	                }
	                i = nextspritestat[i];
	            }

	            if(i < 0)
	            	return;
	            l = sector[sprite[i].sectnum].lotag&0x8000;

	            if(j >= 0)
	            {
	                i = headspritestat[3];
	                while(i >= 0)
	                {
	                    if( l == (sector[sprite[i].sectnum].lotag&0x8000) && sprite[i].lotag == 11 && sprite[j].hitag == sprite[i].hitag && hittype[i].temp_data[4] == 0 )
	                    {
	                        if((sector[sprite[i].sectnum].lotag&0x8000) != 0) sector[sprite[i].sectnum].lotag &= 0x7fff;
	                        else sector[sprite[i].sectnum].lotag |= 0x8000;
	                        hittype[i].temp_data[4] = 1;
	                        hittype[i].temp_data[3] = -hittype[i].temp_data[3];
	                        if(q == 0)
	                        {
	                            callsound(sn,i);
	                            q = 1;
	                        }
	                    }
	                    i = nextspritestat[i];
	                }
	            }
	            return;

	        case 25: //Subway type sliding doors

	            j = headspritestat[3];
	            while(j >= 0)//Find the sprite
	            {
	                if( (sprite[j].lotag) == 15 && sprite[j].sectnum == sn )
	                    break; //Found the sectoreffector.
	                j = nextspritestat[j];
	            }

	            if(j < 0)
	                return;

	            i = headspritestat[3];
	            while(i >= 0)
	            {
	                if( sprite[i].hitag==sprite[j].hitag )
	                {
	                    if( sprite[i].lotag == 15 )
	                    {
	                        sector[sprite[i].sectnum].lotag ^= 0x8000; // Toggle the open or close
	                        sprite[i].ang += 1024;
	                        if(hittype[i].temp_data[4] != 0) callsound(sprite[i].sectnum,i);
	                        callsound(sprite[i].sectnum,i);
	                        if((sector[sprite[i].sectnum].lotag&0x8000) != 0) hittype[i].temp_data[4] = 1;
	                        else hittype[i].temp_data[4] = 2;
	                    }
	                }
	                i = nextspritestat[i];
	            }
	            return;

	        case 27:  //Extended bridge

	            j = headspritestat[3];
	            while(j >= 0)
	            {
	                if( (sprite[j].lotag&0xff)==20 && sprite[j].sectnum == sn) //Bridge
	                {

	                    sector[sn].lotag ^= 0x8000;
	                    if((sector[sn].lotag&0x8000) != 0) //OPENING
	                        hittype[j].temp_data[0] = 1;
	                    else hittype[j].temp_data[0] = 2;
	                    callsound(sn,ii);
	                    break;
	                }
	                j = nextspritestat[j];
	            }
	            return;


	        case 28:
	            //activate the rest of them

	            j = headspritesect[sn];
	            while(j >= 0)
	            {
	                if(sprite[j].statnum==3 && (sprite[j].lotag&0xff)==21)
	                    break; //Found it
	                j = nextspritesect[j];
	            }

	            if(j != -1) j = sprite[j].hitag;

	            l = headspritestat[3];
	            while(l >= 0)
	            {
	                if( (sprite[l].lotag&0xff)==21 && hittype[l].temp_data[0] == 0 &&
	                    (sprite[l].hitag) == j )
	                    hittype[l].temp_data[0] = 1;
	                l = nextspritestat[l];
	            }
	            callsound(sn,ii);

	            return;
	    }
	}

	public static void operaterespawns(int low)
	{
	    short i, j, nexti;

	    i = headspritestat[11];
	    while(i >= 0)
	    {
	        nexti = nextspritestat[i];
	        if(sprite[i].lotag == low) switch(sprite[i].picnum)
	        {
	            case RESPAWN:
	                if( sprite[i].hitag < 0 || sprite[i].hitag >= MAXTILES || (badguypic(sprite[i].hitag) && ud.monsters_off) )
	                	break;

	                j = (short) spawn(i,TRANSPORTERSTAR);
	                sprite[j].z -= (32<<8);

	                sprite[i].extra = 66-12;   // Just a way to killit
	                break;
	            case 7424:
	            	if(!ud.monsters_off)
	            		engine.changespritestat(i, (short)119);
	            	break;
	        }
	        i = nexti;
	    }
	}

	public static void operateactivators(int low, int snum)
	{
	    short k, p[];
	    for(int i=numcyclers-1;i>=0;i--)
	    {
	        p = cyclers[i];

	        if(p[4] == low)
	        {
	            p[5] ^= 1;

	            sector[p[0]].floorshade = sector[p[0]].ceilingshade = (byte) p[3];
	            int startwall = sector[p[0]].wallptr;
	            int endwall = startwall + sector[p[0]].wallnum;

	            for(int j=startwall;j < endwall;j++) {
	            	WALL wal = wall[j];
	                wal.shade = (byte) p[3];
	            }
	        }
	    }

	    int i = headspritestat[8];
	    k = -1;
	    while(i >= 0)
	    {
	        if(sprite[i].lotag == low)
	        {
	            if( sprite[i].picnum == ACTIVATORLOCKED )
	            {
	                if((sector[sprite[i].sectnum].lotag&16384) != 0)
	                    sector[sprite[i].sectnum].lotag &= 65535-16384;
	                else
	                    sector[sprite[i].sectnum].lotag |= 16384;

	                if(snum >= 0)
	                {
	                    if((sector[sprite[i].sectnum].lotag&16384) != 0)
	                        FTA(4,ps[snum]);
	                    else FTA(8,ps[snum]);
	                }
	            }
	            else
	            {
	                switch(sprite[i].hitag)
	                {
	                    case 0:
	                        break;
	                    case 1:
	                        if(sector[sprite[i].sectnum].floorz != sector[sprite[i].sectnum].ceilingz)
	                        {
	                            i = nextspritestat[i];
	                            continue;
	                        }
	                        break;
	                    case 2:
	                        if(sector[sprite[i].sectnum].floorz == sector[sprite[i].sectnum].ceilingz)
	                        {
	                            i = nextspritestat[i];
	                            continue;
	                        }
	                        break;
	                }

	                if( sector[sprite[i].sectnum].lotag < 3 )
	                {
	                    int j = headspritesect[sprite[i].sectnum];
	                    while(j >= 0)
	                    {
	                        if( sprite[j].statnum == 3 ) switch(sprite[j].lotag)
	                        {
	                            case 36:
	                            case 31:
	                            case 32:
	                            case 18:
	                                hittype[j].temp_data[0] = 1-hittype[j].temp_data[0];
	                                callsound(sprite[i].sectnum,j);
	                                break;
	                        }
	                        j = nextspritesect[j];
	                    }
	                }

	                if( k == -1 && (sector[sprite[i].sectnum].lotag&0xff) == 22 )
	                    k = (short) callsound(sprite[i].sectnum,i);

	                operatesectors(sprite[i].sectnum,i);
	            }
	        }
	        i = nextspritestat[i];
	     }

	    operaterespawns(low);
	}

	public static void operatemasterswitches(int low)
	{
	    int i = headspritestat[6];
	    while(i >= 0)
	    {
	        if( sprite[i].picnum == MASTERSWITCH && sprite[i].lotag == low && sprite[i].yvel == 0 )
	        	sprite[i].yvel = 1;
	        i = nextspritestat[i];
	    }
	}

	public static void operateforcefields(int s, int low)
	{
	    for(int p=numanimwalls;p>=0;p--)
	    {
	        int i = animwall[p].wallnum;

	        if(low == wall[i].lotag || low == -1)
	            switch(wall[i].overpicnum)
	        {
	            case BIGFORCE:

	                animwall[p].tag = 0;

	                if( wall[i].cstat != 0)
	                {
	                    wall[i].cstat   = 0;

	                    if( s >= 0 && sprite[s].picnum == SECTOREFFECTOR &&
	                        sprite[s].lotag == 30)
	                            wall[i].lotag = 0;
	                }
	                else
	                    wall[i].cstat = 85;
	                break;
	        }
	    }
	}

	private static int[] chitsw = new int[3];
	public static boolean checkhitswitch(int snum,int w,int switchtype)
	{
	    int switchpal;
	    int lotag,hitag,picnum,correctdips,numdips;
	    int sx,sy;
	    short x, i;

	    if(w < 0) return false;
	    correctdips = 1;
	    numdips = 0;

	    if(switchtype == 1) // A wall sprite
	    {
	        lotag = sprite[w].lotag; if(lotag == 0) return false;
	        hitag = sprite[w].hitag;
	        sx = sprite[w].x;
	        sy = sprite[w].y;
	        picnum = sprite[w].picnum;
	        switchpal = sprite[w].pal;
	    }
	    else
	    {
	        lotag = wall[w].lotag; if(lotag == 0) return false;
	        hitag = wall[w].hitag;
	        sx = wall[w].x;
	        sy = wall[w].y;
	        picnum = wall[w].picnum;
	        switchpal = wall[w].pal;
	    }

	    switch(picnum)
	    {
		    case 121:
		    case 122:
		    case 125:
		    case 126:
		    case 2259:
		    case 2260:
	            break;
	        case 82:
	        case 129:
	            if(ps[snum].access_incs == 0)
	            {
	            	if(currentGame.getCON().key_quotes == null)
	            	{
	            		currentGame.getCON().key_quotes = new char[3][64];
	            		System.arraycopy(currentGame.getCON().fta_quotes[70], 0, currentGame.getCON().key_quotes[0], 0, 64);
	            		System.arraycopy(currentGame.getCON().fta_quotes[71], 0, currentGame.getCON().key_quotes[1], 0, 64);
	            		System.arraycopy(currentGame.getCON().fta_quotes[72], 0, currentGame.getCON().key_quotes[2], 0, 64);
	            	}

	                if( switchpal == 0 )
	                {
	                    if( ps[snum].gotkey[1] != 0 )
	                        ps[snum].access_incs = 1;
	                    else {
	                    	if(cfg.gColoredKeys)
	                    		buildString(currentGame.getCON().fta_quotes[70], 0, "BLUE KEY REQUIRED"); //v0.751
	                    	else System.arraycopy(currentGame.getCON().key_quotes[0], 0, currentGame.getCON().fta_quotes[70], 0, 64);
	                    	FTA(70,ps[snum]);
	                    }
	                }
	                else if( switchpal == 21 )
	                {
	                    if( ps[snum].gotkey[2] != 0 )
	                        ps[snum].access_incs = 1;
	                    else {
	                    	if(cfg.gColoredKeys)
	                    		buildString(currentGame.getCON().fta_quotes[71], 0, "RED KEY REQUIRED");
	                    	else System.arraycopy(currentGame.getCON().key_quotes[1], 0, currentGame.getCON().fta_quotes[71], 0, 64);
	                    	FTA(71,ps[snum]);
	                    }
	                }
	                else if( switchpal == 23 )
	                {
	                    if( ps[snum].gotkey[3] != 0 )
	                        ps[snum].access_incs = 1;
	                    else {
	                    	if(cfg.gColoredKeys)
	                    		buildString(currentGame.getCON().fta_quotes[72], 0, "BROWN KEY REQUIRED");
	                    	else System.arraycopy(currentGame.getCON().key_quotes[2], 0, currentGame.getCON().fta_quotes[72], 0, 64);
	                    	FTA(72,ps[snum]);
	                    }
	                }

	                if( ps[snum].access_incs == 1 )
	                {
	                    if(switchtype == 0)
	                        ps[snum].access_wallnum = (short) w;
	                    else
	                        ps[snum].access_spritenum = (short) w;
	                }

	                return false;
	            }
	        case 84:
	        case 85:
	        case 86:
	        case 87:
	        case 88:
	        case 89:
	        case 90:
	        case 91:
	        case 92:
	        case 93:
	        case 94:
	        case 95:
	        case 98:
	        case 99:
	        case 100:
	        case 101:
	        case 123:
	        case 124:
	        case 127:
	        case 128:
	        case 250:
	        case 251:
	        case 2214:
	        case 2222:
	        case 2223:
	        case 2224:
	        case 2225:
	        case 2226:
	        case 2227:
	        case 2249:
	        case 2250:
	        case 2254:
	        case 2255:
	        case 2697:
	        case 2698:
	        case 2707:
	        case 2708:

	        		//RA
	        case 8048:
	        case 8049:
	        case 8050:
	        case 8051:
	        case 8464:
	        case 8660:
	            if( check_activator_motion( lotag ) ) return false;
	            break;
	        default:
	            if( !isadoorwall(picnum) ) return false;
	            break;
	    }

	    i = headspritestat[0];
	    while(i >= 0)
	    {
	        if( lotag == sprite[i].lotag ) switch(sprite[i].picnum)
	        {
		        case 85:
		        case 87:
		        case 89:
		        case 91:
		        case 93:
		        case 95:
		        case 124:
		        case 128:
		        case 251:
		        case 2223:
		        case 2225:
		        case 2227:
		        case 2250:
		        case 2255:
		        case 2698:
		        case 2708:
		        	if ( sprite[i].picnum == 95 )
		        		plantProcess = true;
		        	if ( sprite[i].hitag != 999 )
		        		sprite[i].picnum--;
		        	break;
		        case 82:
		        case 84:
		        case 86:
		        case 88:
		        case 90:
		        case 92:
		        case 94:
		        case 123:
		        case 127:
		        case 129:
		        case 250:
		        case 2222:
		        case 2224:
		        case 2226:
		        case 2249:
		        case 2254:
		        case 2697:
		        case 2707:
		        case 8660:
		        	if ( sprite[i].picnum != 127 || sprite[i].hitag != 999 )
		        	{
		        		if ( sprite[i].picnum == 94 )
		        			plantProcess = false;
		        		if ( sprite[i].picnum == 8660 ) {
		        			BellTime = 132;
		                    word_119BE0 = i;
		        		}
		                sprite[i].picnum++;
		        	}
		        	else
		        	{
		        		x = headspritestat[107];
		                while(x >= 0)
		                {
		                	short next = nextspritestat[x];

		                	if(sprite[x].picnum == 3410)
		                	{
		                		sprite[x].picnum = 3411;
		                		sprite[x].hitag = 100;
		                		sprite[x].extra = 0;
		                		spritesound(474, x);
		                	} else if(sprite[x].picnum == 295) {
		                		engine.deletesprite(x);
		                	}

		                	x = next;
		                }
		                sprite[i].picnum++;
		        	}
		        	break;

		        case MULTISWITCH:
	            case MULTISWITCH+1:
	            case MULTISWITCH+2:
	            case MULTISWITCH+3:
	                sprite[i].picnum++;
	                if( sprite[i].picnum > (MULTISWITCH+3) )
	                    sprite[i].picnum = MULTISWITCH;
	                break;

	            case 121:
	            case 125:
	            case 2259:
	                if( switchtype == 1 && w == i ) sprite[i].picnum++;
	                else if( sprite[i].hitag == 0 ) correctdips++;
	                numdips++;
	                break;
	            case 122:
	            case 126:
	            case 2260:
	                if( switchtype == 1 && w == i ) sprite[i].picnum--;
	                else if( sprite[i].hitag == 1 ) correctdips++;
	                numdips++;
	                break;
	            case 2214:
//	            	checknextlevel();
	            	++sprite[i].picnum;
	            	break;

	            	//RA
	            case 8048:
		        case 8049:
		        case 8050:
		        case 8051:
		        	sprite[i].picnum++;
		        	if ( sprite[i].picnum > 8051 )
		        		sprite[i].picnum = 8048;
		        	break;
	        }
	        i = nextspritestat[i];
	    }

	    for(i=0;i<numwalls;i++)
	    {
	        x = i;
	        if(lotag == wall[x].lotag)
	            switch(wall[x].picnum)
	        {
		        case 121:
		        case 125:
		        case 2259:
	                if( switchtype == 0 && i == w ) wall[x].picnum++;
	                else if( wall[x].hitag == 0 ) correctdips++;
	                numdips++;
	                break;
		        case 122:
		        case 126:
		        case 2260:
	                if( switchtype == 0 && i == w ) wall[x].picnum--;
	                else if( wall[x].hitag == 1 ) correctdips++;
	                numdips++;
	                break;
	            case MULTISWITCH:
	            case MULTISWITCH+1:
	            case MULTISWITCH+2:
	            case MULTISWITCH+3:
	                wall[x].picnum++;
	                if(wall[x].picnum > (MULTISWITCH+3) )
	                    wall[x].picnum = MULTISWITCH;
	                break;
	            case 82:
	            case 84:
	            case 86:
	            case 88:
	            case 90:
	            case 123:
	            case 127:
	            case 129:
	            case 250:
	            case 2222:
	            case 2224:
	            case 2226:
	            case 2249:
	            case 2254:
	            case 2697:
	            case 2707:
	            case 8660:
	                wall[x].picnum++;
	                break;
	            case 85:
	            case 87:
	            case 89:
	            case 91:
	            case 124:
	            case 128:
	            case 251:
	            case 2223:
	            case 2225:
	            case 2227:
	            case 2250:
	            case 2255:
	            case 2698:
	            case 2708:
	                wall[x].picnum--;
	                break;

	                //RA
	            case 8048:
		        case 8049:
		        case 8050:
		        case 8051:
		        	sprite[i].picnum++;
		        	if ( sprite[i].picnum > 8051 )
		        		sprite[i].picnum = 8048;
		        	break;
	        }
	    }

	    if(lotag == (short) 65535)
	    {
            LeaveMap();
	        if(ud.from_bonus != 0)
	        {
	            ud.level_number = ud.from_bonus;
	            ud.from_bonus = 0;
	        }
	        else
	        {
	            ud.level_number++;
	        }
	        return true;
	    }

	    switch(picnum)
	    {
	        default:
	            if(!isadoorwall(picnum)) break;

	        case 121:
	        case 122:
	        case 125:
	        case 126:
	        case 2259:
	        case 2260:

	            if( picnum == 121  || picnum == 122 ||
	                picnum == 125 || picnum == 126 ||
	                picnum == 2259 || picnum == 2260 )
	            {
	                if( picnum == 2259 || picnum == 2260)
	                {
	                    if(switchtype == 1)
	                        xyzsound(ALIEN_SWITCH1,w,sx,sy,ps[snum].posz);
	                    else xyzsound(ALIEN_SWITCH1,ps[snum].i,sx,sy,ps[snum].posz);
	                }
	                else
	                {
	                    if(switchtype == 1)
	                        xyzsound(SWITCH_ON,w,sx,sy,ps[snum].posz);
	                    else xyzsound(SWITCH_ON,ps[snum].i,sx,sy,ps[snum].posz);
	                }
	                if(numdips != correctdips) break;
	                xyzsound(END_OF_LEVEL_WARN,ps[snum].i,sx,sy,ps[snum].posz);
	            }

	        case 84:
	        case 85:
	        case 86:
	        case 87:
	        case 88:
	        case 89:
	        case 90:
	        case 91:
	        case 92:
	        case 93:
	        case 250:
	        case 251:
	        case 2249:
	        case 2250:
	        case 2707:
	        case 2708:

	        case 82:
	        case 123:
	        case 124:
	        case 127:
	        case 128:
	        case 129:
	        case 2222:
	        case 2223:
	        case 2224:
	        case 2225:
	        case 2226:
	        case 2227:
	        case 2254:
	        case 2255:
	        case 2697:
	        case 2698:
	        case MULTISWITCH:
	        case MULTISWITCH+1:
	        case MULTISWITCH+2:
	        case MULTISWITCH+3:

	        	//RA
            case 8048:
	        case 8049:
	        case 8050:
	        case 8051:
	        case 8660:
	        case 8464:

	        	if( picnum == 8660)
	        	{
	        		 BellTime = 132;
	        		 word_119BE0 = (short) w;
	        		 sprite[w].picnum++;
	        	}

	        	if(picnum == 8464)
	        	{
	        		sprite[w].picnum++;
	        		if(hitag == 10001)
	        		{
	        			if ( ps[snum].SeaSick == 0 )
	        				ps[snum].SeaSick = 350;
	        			operateactivators(668, snum);
	        			operatemasterswitches(668);
	        			spritesound(328, ps[snum].i);
	        			return true;
	        		}
	        	} else {
	        		if(hitag == 10000)
	        		{
	        			if( picnum == MULTISWITCH
        					|| picnum == (MULTISWITCH+1)
        					|| picnum == (MULTISWITCH+2)
        					|| picnum == (MULTISWITCH+3)
                            || picnum == 8048
                            || picnum == 8049
                            || picnum == 8050
                            || picnum == 8051)
	        			{
	        				xyzsound(76, w, sx, sy, ps[snum].posz);

	        				int count = 0;
	        				for ( int k = 0; k < MAXSPRITES; ++k )
	        				{
	        			        if ( (sprite[k].picnum == 98 || sprite[k].picnum == 8048) && sprite[k].hitag == 10000 && count < 3 )
	        			        	chitsw[count++] = k;
	        				}

	        				if ( count == 3 )
	        				{
	        			        xyzsound(78, w, sx, sy, ps[snum].posz);
	        			        for ( int k = 0; k < count; ++k )
	        			        {
	        			          sprite[chitsw[k]].hitag = 0;
	        			          if ( picnum < 8048 )
	        			        	  sprite[chitsw[k]].picnum = 101;
	        			          else
	        			        	  sprite[chitsw[k]].picnum = 8051;
	        			          checkhitswitch(snum, chitsw[k], 1);
	        			        }
	        				}

	        				return true;
	        			}
	        		}
	        	}

	        	//411
                if( picnum == MULTISWITCH || picnum == (MULTISWITCH+1) ||
                    picnum == (MULTISWITCH+2) || picnum == (MULTISWITCH+3) )
                        lotag += picnum-MULTISWITCH;

                if( picnum == 8048 || picnum == 8049 ||
	                    picnum == 8050 || picnum == 8051 )
	                        lotag += picnum-8048;

                x = headspritestat[3];
                while(x >= 0)
                {
                   if( ((sprite[x].hitag) == lotag) )
                   {
                       switch(sprite[x].lotag)
                       {
                           case 12:
                        	   //RA
                           case 46:
                           case 47:
                           case 48:
                                sector[sprite[x].sectnum].floorpal = 0;
                                hittype[x].temp_data[0]++;
                                if(hittype[x].temp_data[0] == 2)
                                   hittype[x].temp_data[0]++;
                                break;
                           case 24:
                           case 34:
                           case 25:
                               hittype[x].temp_data[4] ^= 1;
                               if(hittype[x].temp_data[4] != 0)
                                   FTA(15, ps[snum]);
                               else FTA(2, ps[snum]);
                               break;
                           case 21:
                               FTA(2, ps[screenpeek]);
                               break;
                       }
                   }
                   x = nextspritestat[x];
                }

                operateactivators(lotag,snum);
                operateforcefields(ps[snum].i,lotag);
                operatemasterswitches(lotag);

                if( picnum == 121  || picnum == 122 ||
    	                picnum == 125 || picnum == 126 ||
    	                picnum == 2259 || picnum == 2260 ) return true;

                if( hitag == 0 && !isadoorwall(picnum) )
                {
                    if(switchtype == 1)
                        xyzsound(SWITCH_ON,w,sx,sy,ps[snum].posz);
                    else xyzsound(SWITCH_ON,ps[snum].i,sx,sy,ps[snum].posz);
                }
                else if(hitag != 0)
                {
                	if(hitag < NUM_SOUNDS) {
	                    if(switchtype == 1 && (currentGame.getCON().soundm[hitag]&4) == 0)
	                        xyzsound(hitag,w,sx,sy,ps[snum].posz);
	                    else spritesound(hitag,ps[snum].i);
                	}
                }

               return true;
	    }
	    return false;
	}

	public static void activatebysector(int sect,int j)
	{
	    int i = headspritesect[sect];
	    while(i >= 0)
	    {
	        if(sprite[i].picnum == ACTIVATOR)
	        {
	            operateactivators(sprite[i].lotag,-1);
	        }
	        i = nextspritesect[i];
	    }

	    if(sector[sect].lotag != 22)
	        operatesectors(sect,j);
	}

	public static void checkplayerhurt(PlayerStruct p, int j)
	{
	    if( (j&kHitTypeMask) == kHitSprite )
	    {
	        j &= kHitIndexMask;

	        switch(sprite[j].picnum)
	        {
	            case 1194:
	            //RA
	            case 2430:
	            case 2431:
	            case 2432:
	            case 2443:
	            case 2446:
	            case 2451:
	            case 2455:
	                if(p.hurt_delay < 8 )
	                {
	                    sprite[p.i].extra -= 5;

	                    p.hurt_delay = 16;
	                    p.pals_time = 32;
	                    p.pals[0] = 32;
	                    p.pals[1] = 0;
	                    p.pals[2] = 0;
	                    spritesound(DUKE_LONGTERM_PAIN,p.i);
	                }
	                break;
	        }
	        return;
	    }

	    if( (j&49152) != 32768) return;
	    j &= (MAXWALLS-1);

	    if( p.hurt_delay > 0 ) p.hurt_delay--;
	    else if( (wall[j].cstat&85) != 0 ) switch(wall[j].overpicnum)
	    {
	        case BIGFORCE:
	            p.hurt_delay = 26;
	            if(IsOriginalDemo()) {
	            	checkhitwall(p.i,j,
            			p.posx+(sintable[((int)p.ang+512)&2047]>>9),
            			p.posy+(sintable[(int)p.ang&2047]>>9),
            			p.posz,-1);
	            } else {
		            checkhitwall(p.i,j,
		            	(int)(p.posx+(BCosAngle(BClampAngle(p.ang)) / 512.0f)),
		            	(int)(p.posy+(BSinAngle(BClampAngle(p.ang)) / 512.0f)),
		                p.posz,-1);
	            }
	            break;

	    }
	}

	public static void allignwarpelevators()
	{
	    int i = headspritestat[3];
	    while(i >= 0)
	    {
	        if( sprite[i].lotag == 17 && sprite[i].shade > 16)
	        {
	            int j = headspritestat[3];
	            while(j >= 0)
	            {
	                if( (sprite[j].lotag) == 17 && i != j &&
	                    (sprite[i].hitag) == (sprite[j].hitag) )
	                {
	                    sector[sprite[j].sectnum].floorz =
	                        sector[sprite[i].sectnum].floorz;
	                    sector[sprite[j].sectnum].ceilingz =
	                        sector[sprite[i].sectnum].ceilingz;
	                }

	                j = nextspritestat[j];
	            }
	        }
	        i = nextspritestat[i];
	    }
	}

	public static void breakwall(int newpn,int spr,int dawallnum)
	{
	    wall[dawallnum].picnum = (short) newpn;
	    spritesound(VENT_BUST,spr);
	    spritesound(GLASS_HEAVYBREAK,spr);
	    lotsofglass2(spr,dawallnum,10);
	}

	public static void checkhitwall(int spr,int dawallnum,int x,int y,int z,int atwith)
	{
	    int j, darkestwall;
	    short i, sn = -1;

	    WALL wal = wall[dawallnum];

	    if(wal.overpicnum == MIRROR)
	    {
	        switch(atwith)
	        {
		        case 26:
		        case 1228:
		        case 1273:
		        case 1315:
		        case 1324:
		        case 1426:
		        case 1774:
	                lotsofglass2(spr,dawallnum,70);
	                wal.cstat &= ~16;
	                wal.overpicnum = 70;
	                spritesound(GLASS_HEAVYBREAK,spr);
	                return;
	        }
	    }

	    if( ( (wal.cstat&16)!= 0 || wal.overpicnum == BIGFORCE ) && wal.nextsector >= 0 )
	       if( sector[wal.nextsector].floorz > z )
	           if( sector[wal.nextsector].floorz-sector[wal.nextsector].ceilingz != 0 )
	               switch(wal.overpicnum)
	    {
	        case 210:
	            wal.overpicnum = FANSPRITEBROKE;
	            wal.cstat &= 65535-65;
	            if(wal.nextwall >= 0)
	            {
	                wall[wal.nextwall].overpicnum = FANSPRITEBROKE;
	                wall[wal.nextwall].cstat &= 65535-65;
	            }
	            spritesound(VENT_BUST,spr);
	            spritesound(GLASS_BREAKING,spr);
	            return;

	        case GLASS:
	        case 1973:
	        	sn = engine.updatesector(x,y,sn);
	        	if( sn < 0 ) return;
	            wal.overpicnum=GLASS2;
	            if(wal.overpicnum == 1973)
	            	lotsofglass(spr,dawallnum,64);
	            else lotsofglass2(spr,dawallnum,10);
	            wal.cstat = 0;

	            if(wal.nextwall >= 0)
	                wall[wal.nextwall].cstat = 0;

	            i = EGS(sn,x,y,z,SECTOREFFECTOR,0,0,0,(short)ps[0].ang,0,0,spr,(short)3);
	            sprite[i].lotag = 128; hittype[i].temp_data[1] = 2; hittype[i].temp_data[2] = dawallnum;
	            spritesound(GLASS_BREAKING,i);
	            return;
	        case 1063:
	        	sn = engine.updatesector(x,y,sn); if( sn < 0 ) return;
	            lotsofcolourglass(spr,dawallnum,80);
	            wal.cstat = 0;
	            if(wal.nextwall >= 0)
	                wall[wal.nextwall].cstat = 0;
	            spritesound(VENT_BUST,spr);
	            spritesound(GLASS_BREAKING,spr);
	            return;
	    }

	    switch(wal.picnum)
	    {
	            case COLAMACHINE:
	            case VENDMACHINE:
	                breakwall(wal.picnum+2,spr,dawallnum);
	                spritesound(VENT_BUST,spr);
	                return;
	            case 3643: //Car textures
	            case 3644:
	            case 3645:
	            case 3646:
	            	if(wal.nextwall != -1) {
	            		i = headspritesect[wall[wal.nextwall].nextsector];
	            		while(i >= 0)
	                    {
	                    	short next = nextspritesect[i];  //v0.751
	                    	SPRITE nspr = sprite[i];
	                    	if(nspr.lotag == 6)
	                    	{
	                    		for(int k = 0; k < 16; k++)
	                    			RANDOMSCRAP(nspr,i);

		                    	nspr.detail++;
		                    	if(nspr.detail == 25 && nspr.sectnum < MAXSECTORS)
		                    	{
		                    		int startwall = sector[nspr.sectnum].wallptr;
		            	            int endwall = startwall + sector[nspr.sectnum].wallnum;
		            	            for(int k=startwall;k<endwall && wall[k].nextsector != -1;k++)
		            	            	sector[wall[k].nextsector].lotag = 0;
		            	            sector[nspr.sectnum].lotag = 0;
		            	            stopsound(nspr.lotag);
		                            spritesound(400, i);
		                            engine.deletesprite(i);
		                    	}
	                    	}
	                    	i = next;
	                    }
	            	}
	            	return;

	            case 164:
	            case 165:
	            case 166:
	            case 2217:

	                lotsofglass2(spr,dawallnum,30);
	                wal.picnum=(short) (199+(engine.krand()%3));
	                spritesound(GLASS_HEAVYBREAK,spr);
	                return;
	            case 2229:
	            	wal.picnum = 2233;
	                lotsofmoney(sprite[spr], ((engine.krand() & 7) + 1));
	                spritesound(20, spr);
	            	return;
	            case 72:
	            case 74:
	            case 76:
	            case 244:
	            case 246:
	            case 1814:
	            case 1939:
	            case 1986:
	            case 1988:
	            case 2123:
	            case 2125:
	            case 2636:
	            case 2878:
	            case 2898:
	            case 3200:
	            case 3202:
	            case 3204:
	            case 3206:
	            case 3208:

	                if( rnd(128) )
	                    spritesound(GLASS_HEAVYBREAK,spr);
	                else spritesound(GLASS_BREAKING,spr);
	                lotsofglass2(spr,dawallnum,30);

	                if ( wal.picnum == 1814 )
	                	wal.picnum = 1817;
	                if ( wal.picnum == 1986 )
	                	wal.picnum = 1987;
	                if ( wal.picnum == 1939 )
	                	wal.picnum = 2004;
	                if ( wal.picnum == 1988 )
	                	wal.picnum = 2005;
	                if ( wal.picnum == 2898 )
	                    wal.picnum = 2899;
	                if ( wal.picnum == 2878 )
	                    wal.picnum = 2879;
	                if ( wal.picnum == 2123 )
	                    wal.picnum = 2124;
	                if ( wal.picnum == 2125 )
	                	wal.picnum = 2126;
	                if ( wal.picnum == 3200 )
	                	wal.picnum = 3201;
	                if ( wal.picnum == 3202 )
	                    wal.picnum = 3203;
	                if ( wal.picnum == 3204 )
	                	  wal.picnum = 3205;
	                if ( wal.picnum == 3206 )
	                    wal.picnum = 3207;
	                if ( wal.picnum == 3208 )
	                    wal.picnum = 3209;
	                if ( wal.picnum == 2636 )
	                    wal.picnum = 2637;
	                if ( wal.picnum == 246 )
	                    wal.picnum = 247;
	                if ( wal.picnum == 244 )
	                    wal.picnum = 245;
	                if ( wal.picnum == 76 )
	                    wal.picnum = 77;
	                if ( wal.picnum == 72 )
	                    wal.picnum = 73;
	                if ( wal.picnum == 74 )
	                	wal.picnum = 75;

	                if(wal.lotag == 0) return;

	                sn = wal.nextsector;
	                if(sn < 0) return;
	                darkestwall = 0;

	                short startwall = sector[sn].wallptr;
	                int endwall = startwall + sector[sn].wallnum;

	                for(i=startwall; i < endwall; i++) {
	                	wal = wall[i];
	                    if(wal.shade > darkestwall)
	                        darkestwall=wal.shade;
	                }

	                j = engine.krand()&1;
	                i= headspritestat[3];
	                while(i >= 0)
	                {
	                    if(sprite[i].hitag == wall[dawallnum].lotag && sprite[i].lotag == 3 )
	                    {
	                    	hittype[i].temp_data[2] = j;
	                    	hittype[i].temp_data[3] = darkestwall;
	                    	hittype[i].temp_data[4] = 1;
	                    }
	                    i = nextspritestat[i];
	                }
	                break;

	                //RA
	            case 7433:
	            	wal.picnum = 5018;
	                spritesound(20, spr);
	            	return;
	            case 7441:
	            	wal.picnum = 5016;
	                spritesound(20, spr);
	            	return;
	            case 7453:
	            	wal.picnum = 5035;
	                spritesound(495, spr);
	            	return;
	            case 7540:
	            	wal.picnum = 5023;
	                spritesound(20, spr);
	            	return;
	            case 7552:
	            	wal.picnum = 5021;
	                spritesound(20, spr);
	            	return;
	            case 7553:
	            	wal.picnum = 5020;
	                spritesound(20, spr);
	            	return;
	            case 7554:
	            	wal.picnum = 5025;
	                spritesound(20, spr);
	            	return;
	            case 7555:
	            	wal.picnum = 5015;
	                spritesound(20, spr);
	            	return;
	            case 7557:
	            	wal.picnum = 5019;
	                spritesound(20, spr);
	            	return;
	            case 7558:
	            	wal.picnum = 5024;
	                spritesound(20, spr);
	            	return;
	            case 7559:
	            	wal.picnum = 5017;
	                spritesound(20, spr);
	            	return;
	            case 7561:
	            	wal.picnum = 5027;
	                spritesound(20, spr);
	            	return;
	            case 7568:
	            	wal.picnum = 5022;
	                spritesound(20, spr);
	            	return;
	            case 7579:
	            	wal.picnum = 5026;
	                spritesound(20, spr);
	            	return;
	            case 7580:
	            	wal.picnum = 5037;
	                spritesound(20, spr);
	            	return;
	            case 7657:
	            	wal.picnum = 7659;
	                spritesound(20, spr);
	            	return;
	            case 7859:
	            	wal.picnum = 5081;
	                spritesound(20, spr);
	            	return;
	            case 8227:
	            	wal.picnum = 5070;
	                spritesound(20, spr);
	            	return;
	            case 8496:
	            	wal.picnum = 5061;
	                spritesound(20, spr);
	            	return;
	            case 8497:
	            	wal.picnum = 5076;
	                spritesound(495, spr);
	            	return;
	            case 8503:
	            	wal.picnum = 5079;
	                spritesound(20, spr);
	            	return;
	            case 8567:
	            case 8568:
	            case 8569:
	            case 8570:
	            case 8571:
	            	wal.picnum = 5082;
	                spritesound(20, spr);
	            	return;
	            case 8617:
	            	if(numplayers < 2) {
		            	wal.picnum = 8618;
		                spritesound(47, spr);
	            	}
	            	return;
	            case 8620:
	            	wal.picnum = 8621;
	                spritesound(47, spr);
	            	return;
	            case 8622:
	            	wal.picnum = 8623;
	                spritesound(495, spr);
	            	return;
	    }
	}

	public static boolean checkhitceiling(short sn)
	{
	    int i, j;

	    switch(sector[sn].ceilingpicnum)
	    {
		    case 72:
		    case 74:
		    case 76:
		    case 244:
		    case 246:
		    case 1939:
		    case 1986:
		    case 1988:
		    case 2123:
		    case 2125:
		    case 2878:
		    case 2898:
                ceilingglass(ps[myconnectindex].i,sn,10);
                spritesound(GLASS_BREAKING,ps[screenpeek].i);

                if ( sector[sn].ceilingpicnum == 246 )
                	sector[sn].ceilingpicnum = 247;
                if ( sector[sn].ceilingpicnum == 244 )
                	sector[sn].ceilingpicnum = 245;
                if ( sector[sn].ceilingpicnum == 76 )
                	sector[sn].ceilingpicnum = 77;
                if ( sector[sn].ceilingpicnum == 72 )
                	sector[sn].ceilingpicnum = 73;
                if ( sector[sn].ceilingpicnum == 74 )
                	sector[sn].ceilingpicnum = 75;
                if ( sector[sn].ceilingpicnum == 1986 )
                	sector[sn].ceilingpicnum = 1987;
                if ( sector[sn].ceilingpicnum == 1939 )
                	sector[sn].ceilingpicnum = 2004;
                if ( sector[sn].ceilingpicnum == 1988 )
                	sector[sn].ceilingpicnum = 2005;
                if ( sector[sn].ceilingpicnum == 2898 )
                	sector[sn].ceilingpicnum = 2899;
                if ( sector[sn].ceilingpicnum == 2878 )
                	sector[sn].ceilingpicnum = 2879;
                if ( sector[sn].ceilingpicnum == 2123 )
                	sector[sn].ceilingpicnum = 2124;
                if ( sector[sn].ceilingpicnum == 2125 )
                	sector[sn].ceilingpicnum = 2126;

                if(sector[sn].hitag == 0)
                {
                    i = headspritesect[sn];
                    while(i >= 0)
                    {
                        if( sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 12 )
                        {
                            j = headspritestat[3];
                            while(j >= 0)
                            {
                                if( sprite[j].hitag == sprite[i].hitag )
                                    hittype[j].temp_data[3] = 1;
                                j = nextspritestat[j];
                            }
                            break;
                        }
                        i = nextspritesect[i];
                    }
                }

                i = headspritestat[3];
                j = engine.krand()&1;
                while(i >= 0)
                {
                    if(sprite[i].hitag == (sector[sn].hitag) && sprite[i].lotag == 3 )
                    {
                    	hittype[i].temp_data[2] = j;
                    	hittype[i].temp_data[4] = 1;
                    }
                    i = nextspritestat[i];
                }

        	return true;
	    }

	    return false;
	}

	public static void checkhitsprite(short i,short sn)
	{
	    short j, k, p;
	    SPRITE s;

	    i &= (MAXSPRITES-1);

	    switch(sprite[i].picnum)
	    {
	    	case 1194:
	    		switch(sprite[sn].picnum)
	            {
	            	case 26:
		            case 27:
		            case 1228:
		            case 1426:
		            case 1774:
		            case 2095:
		            case 3420:
		            case 3471:
		            case 3475:
	                    for(k=0;k<64;k++)
	                    {
	                        j = EGS( sprite[i].sectnum,sprite[i].x, sprite[i].y, sprite[i].z-(engine.krand()%(48<<8)),SCRAP3+(engine.krand()&3),-8,48,48,engine.krand()&2047,(engine.krand()&63)+64,-(engine.krand()&4095)-(sprite[i].zvel>>2),i,(short)5);
	                        sprite[j].pal = 8;
	                    }

	                    if( sprite[i].picnum == 1194)
	                    	sprite[i].picnum = 1203;
	                    sprite[i].cstat &= ~257;
	                    break;
	            }
	    		break;
	    	case 1141:
	        case 1150:
	        case 1152:
	        case 1157:
	        case 1158:
	        case 1163:
	        case 1164:
	        case 1165:
	        case 1166:
	            spritesound(GLASS_HEAVYBREAK,i);
	            s = sprite[i];
	            for(j=0;j<16;j++) RANDOMSCRAP(s, i);
	            engine.deletesprite(i);
	            break;
	        case FANSPRITE:
	            sprite[i].picnum = FANSPRITEBROKE;
	            sprite[i].cstat &= (65535-257);
	            if( sprite[i].picnum == 234 ) {
	            	sprite[i].picnum = 235;
	            	spritesound(18,i);
	            }
	            spritesound(GLASS_HEAVYBREAK,i);
	            s = sprite[i];
	            for(j=0;j<16;j++) RANDOMSCRAP(s,i);
	            break;
	        case GRATE1:
	            sprite[i].picnum = BGRATE1;
	            sprite[i].cstat &= (65535-256-1);
	            spritesound(VENT_BUST,i);
	            break;

	        case 1085:
	        case 1086:
	            sprite[i].picnum = 1088;
	            sprite[i].cstat = 0;
	            break;
	        case WATERFOUNTAIN:
	        case WATERFOUNTAIN+1:
	        case WATERFOUNTAIN+2:
	        case WATERFOUNTAIN+3:
	        	spawn(i,1196);
	        	break;
	        case 1098:
	            sprite[i].picnum = 1120;
	            sprite[i].cstat |= (engine.krand()&1)<<2;
	            sprite[i].cstat &= ~257;
	            spawn(i,1196);
	            spritesound(GLASS_BREAKING,i);
	            break;
	        case 1100:
	            sprite[i].picnum = 1102;
	            sprite[i].cstat |= (engine.krand()&1)<<2;
	            sprite[i].cstat &= ~257;
	            spawn(i,1196);
	            spritesound(GLASS_HEAVYBREAK,i);
	            break;
	        case 1066:
	        case 1067:
	        case 1114:
	        case 1117:
	            if(sprite[sn].extra != currentGame.getCON().script[currentGame.getCON().actorscrptr[SHOTSPARK1]] )
	            {
	                for(j=0;j<15;j++)
	                    EGS(sprite[i].sectnum,sprite[i].x,sprite[i].y,sector[sprite[i].sectnum].floorz-(12<<8)-(j<<9),SCRAP1+(engine.krand()&15),-8,64,64,
	                        engine.krand()&2047,(engine.krand()&127)+64,-(engine.krand()&511)-256,i,(short)5);
	                spawn(i,EXPLOSION2);
	                engine.deletesprite(i);
	            }
	            break;
	        case 1221:
	        	//RA
	        case 2654:
	        case 2656:
	        case 3172:
	            spritesound(GLASS_BREAKING,i);
	            lotsofglass2(i,-1,10);
	            engine.deletesprite(i);
	            break;
	        case 3430:
	        	sprite[sn].xvel = (short) ((sprite[i].xvel>>1)+(sprite[i].xvel>>2));
	        	sprite[sn].ang -= engine.krand() & 16;
	        	spritesound(355, i);
	        	break;
	        case QUEBALL:
	        case STRIPEBALL:
	        case 3440:
	        case 3441:
	        case 4897:
	        case 4898:
	        	if(sprite[sn].picnum == QUEBALL || sprite[sn].picnum == STRIPEBALL)
	            {
	                sprite[sn].xvel = (short) ((sprite[i].xvel>>1)+(sprite[i].xvel>>2));
	                sprite[sn].ang -= (sprite[i].ang<<1)+1024;
	                sprite[i].ang = (short) (engine.getangle(sprite[i].x-sprite[sn].x,sprite[i].y-sprite[sn].y)-512);
	                if(Sound[POOLBALLHIT].num < 2)
	                    spritesound(POOLBALLHIT,i);
	                break;
	            }

            	if(sprite[sn].picnum == 3440 || sprite[sn].picnum == 3441)
            	{
            		sprite[sn].xvel = (short) ((sprite[i].xvel>>1)+(sprite[i].xvel>>2));
            		sprite[sn].ang -= (sprite[i].ang<<1)+engine.krand() & 0x40;
            		sprite[i].ang += engine.krand() & 0x10;
            		spritesound(355, i);
            		break;
            	}

            	if(sprite[sn].picnum == 4897 || sprite[sn].picnum == 4898)
            	{
            		sprite[sn].xvel = (short) ((sprite[i].xvel>>1)+(sprite[i].xvel>>2));
            		sprite[sn].ang -= (sprite[i].ang<<1)+engine.krand() & 0x10;
            		sprite[i].ang += engine.krand() & 0x10;
            		spritesound(355, i);
            		break;
            	}

                if( (engine.krand()&3) != 0)
                {
                    sprite[i].xvel = 164;
                    sprite[i].ang = sprite[sn].ang;
                }

	            break;
	        case 3152:
	        	sprite[i].picnum = 3218;
	        	break;
	        case 3153:
	        	sprite[i].picnum = 3219;
	        	break;
	        case 2893:
	        case 2915:
	        case 3115:
	        case 3171:
	            switch(sprite[i].picnum)
	            {
	                case 2893:sprite[i].picnum=2978;break;
	                case 2915:sprite[i].picnum=2977;break;
	                case 3115:sprite[i].picnum=3116;break;
	                case 3171:sprite[i].picnum=3216;break;
	            }

	            spritesound(19, i);
	            lotsofglass2(i, -1, 10);
	            break;
	        case 2876:
	        	sprite[i].picnum = 2990;
	        	break;
	        case 3114:
	        	sprite[i].picnum = 3117;
	        	break;
	        case 2251:
	        	sprite[i].picnum = 2252;
	        	sprite[i].cstat &= (65535-256-1);
	            spritesound(VENT_BUST,i);
	        	break;
	        case 1080:
	        case 1168:
	        case 1172:
	        case 1174:
	        case 1175:
	        case 1176:
	        case 1178:
	        case 1180:
	        case 1215:
	        case 1216:
	        case 1217:
	        case 1218:
	        case 1219:
	        case 1220:
	        case 1222:
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
	        case 1824: //RA
	        case 2215:
	        case 2231:
	            if(sprite[i].picnum == 1280)
	                lotsofmoney(sprite[i],4+(engine.krand()&3));
	            else if(sprite[i].picnum == 1168 || sprite[i].picnum == 2231)
	            {
	                lotsofcolourglass(i,-1,40);
	                spritesound(GLASS_HEAVYBREAK,i);
	            }
	            else if(sprite[i].picnum == 1172)
	                lotsofglass2(i,-1,40);

	            spritesound(GLASS_BREAKING,i);
	            sprite[i].ang = (short) (engine.krand()&2047);
	            lotsofglass2(i,-1,8);
	            engine.deletesprite(i);
	            break;
	        case 1228:
	        	sprite[i].picnum = 1210;
	            spawn(i,1196);
	            spritesound(GLASS_HEAVYBREAK,i);
	        	break;
	        case FORCESPHERE:
	            sprite[i].xrepeat = 0;
	            hittype[sprite[i].owner].temp_data[0] = 32;
	            hittype[sprite[i].owner].temp_data[1] ^= 1;
	            hittype[sprite[i].owner].temp_data[2] ++;
	            spawn(i,EXPLOSION2);
	            break;
	        case 1121:
	        case 1123:
	        case 1124:
	        case 1232:
	        case 1233:
	        case 1234:
	            switch(sprite[i].picnum)
	            {
	                case 1121:sprite[i].picnum=1126;break;
	                case 1123:sprite[i].picnum=1132;break;
	                case 1124:sprite[i].picnum=1122;break;
	                case 1232:sprite[i].picnum=1239;break;
	                case 1233:sprite[i].picnum=1337;break;
	                case 1234:sprite[i].picnum=1235;break;
	            }

	            j = (short) spawn(i,STEAM);
	            sprite[j].z = sector[sprite[i].sectnum].floorz-(32<<8);
	            break;
	        case 1191:
	        case 1193:
	        case 1211:
	        case 1230:
	            switch(sprite[sn].picnum)
	            {
		            case 26:
		            case 27:
		            case 1228:
		            case 1426:
		            case 1774:
		            case 2095:
		            case 3420:
		            case 3471:
		            case 3475:
	                    if(hittype[i].temp_data[0] == 0)
	                    {
	                        sprite[i].cstat &= ~257;
	                        hittype[i].temp_data[0] = 1;
	                        spawn(i,BURNING);
	                    }
	                    break;
	            }
	            break;
	        case 2030:
	        	sprite[i].picnum = 2034;
	            spritesound(19,i);
	            lotsofglass2(i, -1, 10);
	        	break;
	        case 2156:
	        case 2158:
	        case 2160:
	        case 2175:
	        case 2137:
	        case 2151:
	        case 2152:
	        	sprite[i].picnum++;
	            spritesound(19,i);
	            lotsofglass2(i, -1, 10);
	        	for(j=0;j<6;j++)
	        	{
	        		EGS(sprite[i].sectnum,sprite[i].x,sprite[i].y,sector[sprite[i].sectnum].floorz-(8<<8),SCRAP6+(engine.krand()&15),-8,48,48,
	                        engine.krand()&2047,(engine.krand()&63)+64,-(engine.krand()&4095)-sprite[i].zvel>>2,i,(short)5);
	        	}
	        	break;



	        	//RA
	        case FANSPRITEWORK:
	        	sprite[i].picnum = FANSPRITEBROKE;
	            sprite[i].cstat &= (65535-257);
	            spritesound(GLASS_HEAVYBREAK,i);
	            s = sprite[i];
	            for(j=0;j<16;j++) RANDOMSCRAP(s,i);
	            break;
	        case 74:
	        	sprite[i].picnum = 75;
	            spritesound(20, i);
	        	break;
	        case 2123:
	        	sprite[i].picnum = 2124;
	        	spritesound(19, i);
	        	lotsofglass2(i, -1, 10);
	        	break;
	        case 2431:
	        	if ( sprite[i].pal != 4 )
                {
	        		sprite[i].picnum = 2451;
	        		if ( sprite[i].lotag != 0 )
	        		{
	        			for ( j = 0; j < MAXSPRITES; ++j )
	        			{
	        				if ( sprite[j].picnum == 2431 && sprite[j].pal == 4 && sprite[i].lotag == sprite[j].lotag )
	        					sprite[j].picnum = 2451;
	        			}
	        		}
                }
	        	break;
	        case 2437:
	        	spritesound(439, i);
	        	break;
	        case 2443:
                if ( sprite[i].pal != 19 )
	                sprite[i].picnum = 2455;
	        	break;
	        case 2445:
	        	sprite[i].picnum = 2450;
	            spritesound(20, i);
	        	break;
	        case 2451:
	        	if ( sprite[i].pal != 4 )
                {
	        		spritesound(69, i);

	        		if ( sprite[i].lotag != 0 )
	        		{
	        			for ( int l = 0; l < 4096; ++l )
	        			{
	        				if ( sprite[l].picnum == 2451 && sprite[l].pal == 4 && sprite[i].lotag == sprite[l].lotag )
	        				{
	        					guts(sprite[i], 2460, 12, myconnectindex);
	        					guts(sprite[i], 2465, 3, myconnectindex);
	        					sprite[l].xrepeat = 0;
	        					sprite[l].yrepeat = 0;
	        					sprite[i].xrepeat = 0;
	        					sprite[i].yrepeat = 0;
	        				}
	        			}
                  }
                  else
                  {
                	  guts(sprite[i], 2460, 12, myconnectindex);
                	  guts(sprite[i], 2465, 3, myconnectindex);
                	  sprite[i].xrepeat = sprite[i].yrepeat = 0;
                  }
                }
	        	break;
	        case 2455:
	        	spritesound(69, i);
	        	guts(sprite[i], 2465, 3, myconnectindex);
	        	engine.deletesprite(i);
	        	break;
	        case 3462:
	        	sprite[i].picnum = 5074;
	        	spritesound(20, i);
	        	break;
	        case 3475:
	        	sprite[i].picnum = 5075;
	        	spritesound(20, i);
	        	break;
	        case 3497:
	        	sprite[i].picnum = 5076;
	        	spritesound(20, i);
	        	break;
	        case 3498:
	        	sprite[i].picnum = 5077;
	        	spritesound(20, i);
	        	break;
	        case 3499:
	        	sprite[i].picnum = 5078;
	        	spritesound(20, i);
	        	break;
	        case 3584:
	        	sprite[i].picnum = 8681;
	        	spritesound(495, i);
	        	hitradius(i, 250, 0, 0, 1, 1);
	        	break;
	        case 3773:
	        	sprite[i].picnum = 8651;
                spritesound(19, i);
                lotsofglass2(i, -1, 10);
	        	break;
	        case 7441:
	        	sprite[i].picnum = 5016;
                spritesound(20, i);
	        	break;
	        case 7478:
	        	sprite[i].picnum = 5035;
	        	spritesound(20, i);
	        	break;
	        case 7533:
	        	sprite[i].picnum = 5035;
	        	spritesound(495, i);
	        	hitradius(i, 10, 0, 0, 1, 1);
	        	break;
	        case 7534:
	        	sprite[i].picnum = 5029;
	        	spritesound(20, i);
	        	break;
	        case 7545:
	        	sprite[i].picnum = 5030;
	        	spritesound(20, i);
	        	break;
	        case 7547:
	        	sprite[i].picnum = 5031;
	        	spritesound(20, i);
	        	break;
            case 7553:
            	sprite[i].picnum = 5035;
                spritesound(20, i);
                break;
            case 7574:
            	sprite[i].picnum = 5032;
                spritesound(20, i);
                break;
            case 7575:
            	sprite[i].picnum = 5033;
                spritesound(20, i);
                break;
            case 7578:
            	sprite[i].picnum = 5034;
                spritesound(20, i);
                break;
            case 7636:
            case 7875:
            	sprite[i].picnum += 3;
                spritesound(18, i);
            	break;

            case 8567:
            case 8568:
            case 8569:
            case 8570:
            case 8571:
            	sprite[i].picnum = 5082;
                spritesound(20, i);
            	break;
            case 7640:
            	sprite[i].picnum += 2;
                spritesound(18, i);
            	break;
            case 7696:
            	sprite[i].picnum = 7697;
                spritesound(47, i);
            	break;
            case 7806:
            	sprite[i].picnum = 5043;
                spritesound(20, i);
            	break;
            case 7879:
            	sprite[i].picnum++;
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
            	break;
            case 7886:
            	sprite[i].picnum = 5046;
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
            	break;
            case 7887:
            	sprite[i].picnum = 5044;
                spritesound(20, i);
                hitradius(i, 10, 0, 0, 1, 1);
            	break;
            case 7900:
            	sprite[i].picnum = 5047;
                spritesound(20, i);
            	break;
            case 7901:
            	sprite[i].picnum = 5080;
            	spritesound(20, i);
            	break;
            case 7906:
            	sprite[i].picnum = 5048;
            	spritesound(20, i);
            	break;
            case 7912:
            case 7913:
            	sprite[i].picnum = 5049;
                spritesound(20, i);
            	break;
            case 8047:
            	sprite[i].picnum = 5050;
                spritesound(20, i);
            	break;
            case 8059:
            	 sprite[i].picnum = 5051;
                 spritesound(20, i);
            	break;
            case 8060:
            	sprite[i].picnum = 5052;
                spritesound(20, i);
            	break;
            case 8099:
            	if ( sprite[i].lotag == 5 )
                {
            		sprite[i].lotag = 0;
            		sprite[i].picnum = 5087;
            		spritesound(340, i);
            		for ( int l = 0; l < MAXSPRITES; ++l )
            		{
            			if ( sprite[l].picnum == 8094 )
            				sprite[l].picnum = 5088;
            		}
                }
            	break;
            case 8215:
            	sprite[i].picnum = 5064;
                spritesound(20, i);
            	break;
            case 8216:
            	sprite[i].picnum = 5065;
                spritesound(20, i);
                break;
            case 8217:
            	sprite[i].picnum = 5066;
                spritesound(20, i);
                break;
            case 8218:
            	sprite[i].picnum = 5067;
                spritesound(20, i);
            	break;
            case 8220:
            	sprite[i].picnum = 5068;
                spritesound(20, i);
                break;
            case 8221:
            	sprite[i].picnum = 5069;
                spritesound(20, i);
                break;
            case 8222:
            	sprite[i].picnum = 5053;
            	spritesound(20, i);
            	break;
            case 8223:
            	sprite[i].picnum = 5054;
                spritesound(20, i);
                break;
            case 8224:
            	 sprite[i].picnum = 5055;
                 spritesound(20, i);
                 break;
            case 8312:
            	sprite[i].picnum = 5071;
                spritesound(472, i);
                break;
            case 8370:
            	sprite[i].picnum = 5056;
                spritesound(20, i);
            	break;
            case 8371:
            	sprite[i].picnum = 5057;
                spritesound(20, i);
                break;
            case 8372:
                sprite[i].picnum = 5058;
                spritesound(20, i);
            	break;
            case 8373:
            	sprite[i].picnum = 5059;
                spritesound(20, i);
            	break;
            case 8385:
            	sprite[i].picnum = 8386;
            	spritesound(20, i);
            	break;
            case 8387:
            	sprite[i].picnum = 8388;
            	spritesound(20, i);
            	break;
            case 8389:
            	sprite[i].picnum = 8390;
                spritesound(20, i);
            	break;
            case 8391:
            	sprite[i].picnum = 8392;
                spritesound(20, i);
            	break;
            case 8394:
            	sprite[i].picnum = 5072;
            	spritesound(495, i);
            	break;
            case 8395:
            	sprite[i].picnum = 5072;
            	spritesound(20, i);
            	break;
            case 8396:
            	sprite[i].picnum = 5038;
                spritesound(20, i);
            	break;
            case 8397:
            	sprite[i].picnum = 5039;
                spritesound(20, i);
                break;
            case 8398:
            	sprite[i].picnum = 5040;
                spritesound(20, i);
            	break;
            case 8399:
            	sprite[i].picnum = 5041;
                spritesound(20, i);
            	break;
            case 8423:
            	sprite[i].picnum = 5073;
                spritesound(20, i);
            	break;
            case 8461:
            case 8462:
            	sprite[i].picnum = 5074;
            	spritesound(20, i);
                break;
            case 8475:
            	sprite[i].picnum = 5075;
                spritesound(472, i);
            	break;
            case 8497:
            	sprite[i].picnum = 5076;
            	spritesound(20, i);
            	break;
            case 8498:
            	sprite[i].picnum = 5077;
                spritesound(20, i);
            	break;
            case 8499:
            	sprite[i].picnum = 5078;
                spritesound(20, i);
            	break;
            case 8503:
            	sprite[i].picnum = 5079;
                spritesound(20, i);
            	break;
            case 8525:
            	sprite[i].picnum = 5036;
                spritesound(20, i);
            	break;
            case 8537:
            	sprite[i].picnum = 5062;
                spritesound(20, i);
            	break;
            case 8579:
            	sprite[i].picnum = 5014;
                spritesound(20, i);
            	break;
            case 8596:
            	sprite[i].picnum = 8598;
                spritesound(20, i);
            	break;
            case 8608:
            	sprite[i].picnum = 5083;
                spritesound(20, i);
            	break;
            case 8609:
            	sprite[i].picnum = 5084;
                spritesound(20, i);
            	break;
            case 8611:
            	sprite[i].picnum = 5086;
                spritesound(20, i);
            	break;
            case 8640:
            	sprite[i].picnum = 5085;
            	spritesound(20, i);
            	break;
            case 8679:
            	sprite[i].picnum = 8680;
                spritesound(47, i);
                hitradius(i, 10, 0, 0, 1, 1);
                if ( sprite[i].lotag != 0 )
                {
                	for ( int l = 0; l < MAXSPRITES; ++l )
                	{
                		if ( sprite[l].picnum == 8679 && sprite[l].pal == 4 && sprite[i].lotag == sprite[l].lotag )
                			sprite[l].picnum = 8680;
                	}
                }
            	break;
            case 8682:
            	sprite[i].picnum = 8683;
            	spritesound(20, i);
            	break;
            case 8487:
            case 8489:
            	spritesound(471, i);
                sprite[i].picnum++;
            	break;

            case 8162:
            case 8163:
            case 8164:
            case 8165:
            case 8166:
            case 8167:
            case 8168:
            	engine.changespritestat(i, (short)5);
                sprite[i].picnum = 5063;
                spritesound(20, i);
                break;

            case 8589:
            case 8590:
            case 8591:
            case 8592:
            case 8593:
            case 8594:
            case 8595:
            	engine.changespritestat(i, (short)5);
                sprite[i].picnum = 8588;
                spritesound(20, i);
            	break;
            case 7648:
            case 7694:
            case 7700:
            case 7702:
            case 7711:
            	sprite[i].picnum++;
            	spritesound(47, i);
            	break;
            case 7638:
            case 7644:
            case 7646:
            case 7650:
            case 7653:
            case 7655:
            case 7691:
            case 7881:
            case 7883:
            case 7876:
            	sprite[i].picnum++;
            	spritesound(18, i);
            	break;
            case 7595:
            case 7704:
            	sprite[i].picnum = 7705;
                spritesound(495, i);
            	break;
            case 7885:
            case 7890:
            	sprite[i].picnum = 5045;
                spritesound(495, i);
                hitradius(i, 10, 0, 0, 1, 1);
            	break;

	        case PLAYERONWATER:
	            i = sprite[i].owner;
	        default:
	            if( (sprite[i].cstat&16) != 0 && sprite[i].hitag == 0 && sprite[i].lotag == 0 && sprite[i].statnum == 0)
	                break;

	            if( ( sprite[sn].picnum == ALIENBLAST || sprite[sn].owner != i ) && sprite[i].statnum != 4)
	            {
	                if( badguy(sprite[i]) )
	                {
	                    if(sprite[sn].picnum == CROSSBOW) sprite[sn].extra <<= 1;

	                    if( (sprite[i].picnum != MOSQUITO) )
	                        if(sprite[sn].picnum != ALIENBLAST )
	                            if( currentGame.getCON().actortype[sprite[i].picnum] == 0 )
	                    {
	                        j = (short) spawn(sn,JIBS6);
	                        if(sprite[sn].pal == 6)
	                            sprite[j].pal = 6;
	                        sprite[j].z += (4<<8);
	                        sprite[j].xvel = 16;
	                        sprite[j].xrepeat = sprite[j].yrepeat = 24;
	                        sprite[j].ang += 32-(engine.krand()&63);
	                    }

	                    j = sprite[sn].owner;

	                    if( j >= 0 && sprite[j].picnum == APLAYER && sprite[i].picnum != MOSQUITO )
	                        if( ps[sprite[j].yvel].curr_weapon == SHOTGUN_WEAPON )
	                    {
	                        shoot(i,BLOODSPLAT3);
	                        shoot(i,BLOODSPLAT1);
	                        shoot(i,BLOODSPLAT2);
	                        shoot(i,BLOODSPLAT4);
	                    }

	                    if(sprite[i].statnum == 2)
	                    {
	                    	engine.changespritestat(i,(short)1);
	                        hittype[i].timetosleep = SLEEPTIME;
	                    }
	                }

	                if( sprite[i].statnum != 2 )
	                {
	                	if( sprite[sn].picnum == ALIENBLAST) {
		                    if( (sprite[i].picnum == APLAYER && sprite[i].pal == 1 ) )
		                        return;

		                    if ( currentGame.getCON().freezerhurtowner == 0 )
		                    {
		                    	if ( sprite[sn].owner == i)
		                    		return;
		                    }
	                	}

	                    hittype[i].picnum = sprite[sn].picnum;
	                    hittype[i].extra += sprite[sn].extra;
	                    hittype[i].ang = sprite[sn].ang;
	                    hittype[i].owner = sprite[sn].owner;
	                }

	                if(sprite[i].statnum == 10)
	                {
	                    p = sprite[i].yvel;
	                    if(ps[p].newowner >= 0)
	                    {
	                        ps[p].newowner = -1;
	                        ps[p].posx = ps[p].oposx;
	                        ps[p].posy = ps[p].oposy;
	                        ps[p].posz = ps[p].oposz;
	                        ps[p].ang = ps[p].oang;

	                        ps[p].cursectnum = engine.updatesector(ps[p].posx,ps[p].posy,ps[p].cursectnum);
	                        setpal(ps[p]);

	                        j = headspritestat[1];
	                        while(j >= 0)
	                        {
	                            if(sprite[j].picnum==CAMERA1) sprite[j].yvel = 0;
	                            j = nextspritestat[j];
	                        }
	                    }

	                    if( sprite[hittype[i].owner].picnum != APLAYER)
	                        if(ud.player_skill >= 3)
	                            sprite[sn].extra += (sprite[sn].extra>>1);
	                }

	            }
	            break;
	    }
	}

	public static void checksectors(int snum)
	{
	    int i = -1,oldz;
	    int hitscanwall;

	    PlayerStruct p = ps[snum];

	    if(p.cursectnum != -1) {
		    switch(sector[p.cursectnum].lotag)
		    {

		        case 32767:
		            sector[p.cursectnum].lotag = 0;
		            FTA(9,p);
		            ps[connecthead].secret_rooms++;
		            return;
		        case -1:
		            LeaveMap();
		            sector[p.cursectnum].lotag = 0;
		            if(ud.from_bonus != 0)
		            {
		                ud.level_number = ud.from_bonus;
		                ud.from_bonus = 0;
		            } else ud.level_number++;
		            return;
		        case -2:
		            sector[p.cursectnum].lotag = 0;
		            p.timebeforeexit = 26*8;
		            p.customexitsound = sector[p.cursectnum].hitag;
		            return;
		        default:
		            if(sector[p.cursectnum].lotag >= 10000)
		            {
		                if(snum == screenpeek || ud.coop == 1 )
		                    spritesound(sector[p.cursectnum].lotag-10000,p.i);
		                sector[p.cursectnum].lotag = 0;
		            }
		            break;

		    }
	    }

	    //After this point the the player effects the map with space

	    if(sprite[p.i].extra <= 0) return;

	    if( ud.cashman != 0 && (sync[snum].bits&(1<<29)) != 0 )
	        lotsofmoney(sprite[p.i],2);

	    if(p.newowner >= 0)
	    {
	        if( klabs(sync[snum].svel) > 768 || klabs(sync[snum].fvel) > 768 )
	        {
	        	CLEARCAMERAS(p, -1);
	        	return;
	        }
	    }

	    if( (sync[snum].bits&(1<<29)) == 0 && (sync[snum].bits&(1<<31)) == 0)
	        p.toggle_key_flag = 0;

	    else if(p.toggle_key_flag == 0)
	    {

	        if( (sync[snum].bits&(1<<31)) != 0 )
	        {
	            if( p.newowner >= 0 )
	            	CLEARCAMERAS(p, -1);
	            return;
	        }

	        neartagsprite = -1;
	        p.toggle_key_flag = 1;
	        hitscanwall = -1;

	        i = hitawall(p);
	        hitscanwall = pHitInfo.hitwall;

	        if(hitscanwall >= 0 && wall[hitscanwall].overpicnum == MIRROR)
	            if( wall[hitscanwall].lotag > 0 && Sound[wall[hitscanwall].lotag].num == 0 && snum == screenpeek)
	        {
	            if(currentGame.getCON().type == RRRA)
	            {
	            	if(screenpeek == myconnectindex) {
		            	if ( Sound[27].num == 0 && Sound[28].num == 0 && Sound[29].num == 0 && Sound[257].num == 0 && Sound[258].num == 0 )
		            	{
		            		switch ( engine.krand() % 5 )
		            		{
		            		case 0:
		            			spritesound(27, p.i);
		            			break;
		            		case 1:
		            			spritesound(28, p.i);
		            			break;
		            		case 2:
		            			spritesound(29, p.i);
		            			break;
		            		case 3:
		                        spritesound(257, p.i);
		                        break;
		                      default:
		                        spritesound(258, p.i);
		                        break;
		                    }
		            	}
	            	}
	            	return;
	            }

	            spritesound(wall[hitscanwall].lotag,p.i);
	            return;
	        }

	        if(hitscanwall >= 0 && (wall[hitscanwall].cstat&16) != 0 )
	            switch(wall[hitscanwall].overpicnum)
	        {
	            default:
	                if(wall[hitscanwall].lotag != 0)
	                    return;
	        }

	        if ( p.OnMotorcycle )
	        {
	        	if ( p.CarSpeed < 20 )
	        		leaveMoto(p);
	        	return;
	        }
	        if ( p.OnBoat )
	        {
	        	if ( p.CarSpeed < 20 )
	        		leaveBoard(p);
	        	return;
	        }

	        if(p.newowner >= 0)
	            neartag(p.oposx,p.oposy,p.oposz,sprite[p.i].sectnum,(short)p.oang,1280,1);
	        else
	        {
	        	neartag(p.posx,p.posy,p.posz,sprite[p.i].sectnum,(short)p.oang,1280,1);
	            if(neartagsprite == -1 && neartagwall == -1 && neartagsector == -1)
	            	neartag(p.posx,p.posy,p.posz+(8<<8),sprite[p.i].sectnum,(short)p.oang,1280,1);
	            if(neartagsprite == -1 && neartagwall == -1 && neartagsector == -1)
	            	neartag(p.posx,p.posy,p.posz+(16<<8),sprite[p.i].sectnum,(short)p.oang,1280,1);
	            if(neartagsprite == -1 && neartagwall == -1 && neartagsector == -1)
	            {
	            	neartag(p.posx,p.posy,p.posz+(16<<8),sprite[p.i].sectnum,(short)p.oang,1280,3);
	                if(neartagsprite >= 0)
	                {
	                    switch(sprite[neartagsprite].picnum)
	                    {
		                    case 1115:
		                    case 1168:
		                    case 5581:
		                    case 5583:
	                            return;
	                        case 5317:
	                        	 sprite[neartagsprite].detail = 1;
	                        	return;
	                    }
	                }

	                neartagsprite = -1;
	                neartagwall = -1;
	                neartagsector = -1;
	            }
	        }

	        if(p.newowner == -1 && neartagsprite == -1 && neartagsector == -1 && neartagwall == -1 )
	            if( isanunderoperator(sector[sprite[p.i].sectnum].lotag) )
	                neartagsector = sprite[p.i].sectnum;

	        if( neartagsector >= 0 && (sector[neartagsector].lotag&16384) != 0 )
	            return;

	        if( neartagsprite == -1 && neartagwall == -1)
	            if(sector[p.cursectnum].lotag == 2 )
	            {
	                oldz = hitasprite(p.i);
	                neartagsprite = pHitInfo.hitsprite;
	                if(oldz > 1280) neartagsprite = -1;
	            }

	        if(neartagsprite >= 0)
	        {
	            if( checkhitswitch(snum,neartagsprite,1) ) return;

	            switch(sprite[neartagsprite].picnum)
	            {
		            case 1098:
		            case 1100:
		            case 2121:
		            case 2122:
	                    if(p.last_pissed_time == 0)
	                    {
	                        if(ud.lockout == 0) spritesound(435,p.i);

	                        p.last_pissed_time = 26*220;
	                        p.transporter_hold = 29*2;
	                        if(p.holster_weapon == 0)
	                        {
	                            p.holster_weapon = 1;
	                            p.weapon_pos = -1;
	                        }
	                        if(sprite[p.i].extra <= (currentGame.getCON().max_player_health-(currentGame.getCON().max_player_health/10) ) )
	                        {
	                            sprite[p.i].extra += currentGame.getCON().max_player_health/10;
	                            p.last_extra = sprite[p.i].extra;
	                        }
	                        else if(sprite[p.i].extra < currentGame.getCON().max_player_health )
	                             sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
	                    }
	                    else if(Sound[38].num == 0)
	                        spritesound(38,p.i);
	                    return;

	                case WATERFOUNTAIN:
	                    if(hittype[neartagsprite].temp_data[0] != 1)
	                    {
	                        hittype[neartagsprite].temp_data[0] = 1;
	                        sprite[neartagsprite].owner = p.i;

	                        if(sprite[p.i].extra < currentGame.getCON().max_player_health)
	                        {
	                            sprite[p.i].extra++;
	                            spritesound(DUKE_DRINKING,p.i);
	                        }
	                    }
	                    return;
	                case 1272:
	                    spritesound(SHORT_CIRCUIT,p.i);
	                    sprite[p.i].extra -= 2+(engine.krand()&3);
	                    p.pals[0] = 48;
	                    p.pals[1] = 48;
	                    p.pals[2] = 64;
	                    p.pals_time = 32;
	                    break;
	                case MOTORCYCLE:
	                	getMoto(p, neartagsprite);
	                	return;
	                case SWAMPBUGGY:
	                	getBoard(p, neartagsprite);
	                	return;
	                case 8448:
	                	if ( Sound[340].num == 0 )
	                		spritesound(340, neartagsprite);
	                	return;
	                case 8704:
	                	if ( numplayers == 1 )
	                	{
	                		if ( Sound[445].num != 0 || dword_119C08 != 0 )
	                		{
			                    if ( Sound[445].num == 0 && Sound[446].num == 0 && Sound[447].num == 0 && dword_119C08 != 0 )
			                    {
			                    	if ( (engine.krand() % 2) == 1 )
			                    		spritesound(446, neartagsprite);
			                    	else
			                    		spritesound(447, neartagsprite);
			                    }
	                		}
	                		else
	                		{
	                			spritesound(445, neartagsprite);
	                			dword_119C08 = 1;
	                		}
	                }
	                return;
	                case 8164:
	                case 8165:
	                case 8166:
	                case 8167:
	                case 8168:
	                case 8591:
	                case 8592:
	                case 8593:
	                case 8594:
	                case 8595:
	                	sprite[neartagsprite].extra = 60;
	                    spritesound(235, neartagsprite);
	                	return;
	            }
	        }

	        if( (sync[snum].bits&(1<<29)) == 0 ) return;

	        if(neartagwall == -1 && neartagsector == -1 && neartagsprite == -1)
	            if( klabs(hits(p.i)) < 512 )
	        {
	            if( (engine.krand()&255) < 16 )
	                spritesound(DUKE_SEARCH2,p.i);
	            else spritesound(DUKE_SEARCH,p.i);
	            return;
	        }

	        if( neartagwall >= 0 )
	        {
	            if( wall[neartagwall].lotag > 0 && isadoorwall(wall[neartagwall].picnum) )
	            {
	                if(hitscanwall == neartagwall || hitscanwall == -1)
	                    checkhitswitch(snum,neartagwall,0);
	                return;
	            }
	        }

	        if( neartagsector >= 0 && (sector[neartagsector].lotag&16384) == 0 && isanearoperator(sector[neartagsector].lotag) )
	        {
	            i = headspritesect[neartagsector];
	            while(i >= 0)
	            {
	                if( sprite[i].picnum == ACTIVATOR || sprite[i].picnum == MASTERSWITCH )
	                    return;
	                i = nextspritesect[i];
	            }
	            if ( checkaccess(neartagsector, snum) )
	            	operatesectors(neartagsector,p.i);
	            else {
	            	if ( sector[neartagsector].filler > 3 )
                		spritesound(99, p.i);
                    else spritesound(419, p.i);

	            	if(currentGame.getCON().key_quotes == null)
	            	{
	            		currentGame.getCON().key_quotes = new char[3][64];
	            		System.arraycopy(currentGame.getCON().fta_quotes[70], 0, currentGame.getCON().key_quotes[0], 0, 64);
	            		System.arraycopy(currentGame.getCON().fta_quotes[71], 0, currentGame.getCON().key_quotes[1], 0, 64);
	            		System.arraycopy(currentGame.getCON().fta_quotes[72], 0, currentGame.getCON().key_quotes[2], 0, 64);
	            	}

	            	if( sector[neartagsector].filler == 1 ) { //v0.751
	            		if(cfg.gColoredKeys)
	            			buildString(currentGame.getCON().fta_quotes[70], 0, "BLUE KEY REQUIRED");
	            		else System.arraycopy(currentGame.getCON().key_quotes[0], 0, currentGame.getCON().fta_quotes[70], 0, 64);
	                    FTA(70,ps[snum]);
	            	}
	                else if( sector[neartagsector].filler == 2 ) {
	                	if(cfg.gColoredKeys)
	                		buildString(currentGame.getCON().fta_quotes[71], 0, "RED KEY REQUIRED");
	                	else System.arraycopy(currentGame.getCON().key_quotes[1], 0, currentGame.getCON().fta_quotes[71], 0, 64);
	                    FTA(71,ps[snum]);
	                }
	                else if( sector[neartagsector].filler == 3 ) {
	                	if(cfg.gColoredKeys)
	                		buildString(currentGame.getCON().fta_quotes[72], 0, "BROWN KEY REQUIRED");
	                	else System.arraycopy(currentGame.getCON().key_quotes[2], 0, currentGame.getCON().fta_quotes[72], 0, 64);
	                    FTA(72,ps[snum]);
	                }
	                else FTA(41, ps[snum]);
	            }
	        }
	        else if( (sector[sprite[p.i].sectnum].lotag&16384) == 0 )
	        {
	            if( isanunderoperator(sector[sprite[p.i].sectnum].lotag) )
	            {
	                i = headspritesect[sprite[p.i].sectnum];
	                while(i >= 0)
	                {
	                    if(sprite[i].picnum == ACTIVATOR || sprite[i].picnum == MASTERSWITCH) return;
	                    i = nextspritesect[i];
	                }

	                if ( checkaccess(neartagsector, snum) )
	                	operatesectors(sprite[p.i].sectnum,p.i);
	                else {
	                	if ( neartagsector != -1 && sector[neartagsector].filler > 3 )
	                		spritesound(99, p.i);
	                    else spritesound(419, p.i);
	                    FTA(41, ps[snum]);
	                }
	            }
	            else checkhitswitch(snum,neartagwall,0);
	        }
	    }


	}

	public static void pushwall(int nwal, short nsect, int snum)
	{
		int box = wall[nwal].nextsector;
		int hitag = sector[nsect].hitag;

		if(hitag == 0)
			hitag = 4;
		if ( hitag > 16 )
			hitag = 16;

		int minx = 131072;
		int miny = 131072;
		int maxx = -131072;
		int maxy = -131072;

		int wx, wy;
		short wallptr = sector[box].wallptr;
		int wallnum = wallptr + sector[box].wallnum;
		for(int i = wallptr; i < wallnum; i++)
		{
			wx = wall[i].x;
		    wy = wall[i].y;
		    if(wx > maxx ) maxx = wx;
		    if(wy > maxy ) maxy = wy;
		    if(wx < minx ) minx = wx;
		    if(wy < miny ) miny = wy;
		}

		maxx += hitag + 1;
		maxy += hitag + 1;
		minx -= hitag + 1;
		miny -= hitag + 1;

		boolean inside = true;
		if ( engine.inside(maxx, maxy, nsect) == 0 )
			inside = false;
		if ( engine.inside(maxx, miny, nsect) == 0 )
			inside = false;
		if ( engine.inside(minx, miny, nsect) == 0 )
			inside = false;
		if ( engine.inside(minx, maxy, nsect) == 0 )
			inside = false;

		if ( inside )
		{
			if ( Sound[389].num == 0)
				spritesound(389, ps[snum].i);
			for(short i = wallptr; i < wallnum; i++)
			{
				wx = wall[i].x;
			    wy = wall[i].y;
			    int dir = wall[nwal].lotag - 40;
			    if(dir < 4)
			    {
			    	switch(dir)
			    	{
			    		case 0: wy -= hitag; break;
			    		case 1: wx -= hitag; break;
			    		case 2: wy += hitag; break;
			    		case 3: wx += hitag; break;
			    	}
			    	engine.dragpoint(i, wx, wy);
			    }
			}
		}
		else
		{
			for(short i = wallptr; i < wallnum; i++)
			{
				wx = wall[i].x;
			    wy = wall[i].y;
			    int dir = wall[nwal].lotag - 40;
			    if(dir < 4)
			    {
			    	switch(dir)
			    	{
			    		case 0: wy += hitag - 2; break;
			    		case 1: wx += hitag - 2; break;
			    		case 2: wy -= hitag - 2; break;
			    		case 3: wx -= hitag - 2; break;
			    	}
			    	engine.dragpoint(i, wx, wy);
			    }
			}
		}
	}

	public static void CLEARCAMERAS(PlayerStruct p, int i)
	{
		if(i < 0)
        {
            p.posx = p.oposx;
            p.posy = p.oposy;
            p.posz = p.oposz;
            p.ang = p.oang;
            p.newowner = -1;

            p.cursectnum = engine.updatesector(p.posx,p.posy,p.cursectnum);
            setpal(p);

            i = headspritestat[1];
            while(i >= 0)
            {
                if(sprite[i].picnum==CAMERA1) sprite[i].yvel = 0;
                i = nextspritestat[i];
            }
        }
        else if(p.newowner >= 0)
            p.newowner = -1;
	}

	public static void movejails()
	{
		for(int i = 0; i < numjaildoors; i++)
		{
			int speed = jailspeed[i];
			if ( speed < 2 ) speed = 2;

			if(jailstatus[i] == 1 || jailstatus[i] == 3)
			{
				jailcount2[i] -= speed;
				if(jailcount2[i] > 0)
				{
					int wx = 0, wy = 0;
					int sect = jailsect[i];
					short wallptr = sector[sect].wallptr;
					int wallnum = wallptr + sector[sect].wallnum;
					for(short w = wallptr; w < wallnum; w++)
					{
						switch(jaildirection[i])
						{
							case 10:
								wx = wall[w].x;
								wy = wall[w].y + speed;
								break;
							case 20:
								wx = wall[w].x - speed;
								wy = wall[w].y;
								break;
							case 30:
								wx = wall[w].x;
								wy = wall[w].y - speed;
								break;
							case 40:
								wx = wall[w].x + speed;
								wy = wall[w].y;
								break;
						}
						engine.dragpoint(w, wx, wy);
					}
				}
				else
				{
					jailcount2[i] = 0;
					if(jailstatus[i] == 1)
						jailstatus[i] = 2;
					else if(jailstatus[i] == 3)
						jailstatus[i] = 0;

					switch(jaildirection[i])
					{
						case 10:
							jaildirection[i] = 30;
							break;
						case 20:
							jaildirection[i] = 40;
							break;
						case 30:
							jaildirection[i] = 10;
							break;
						case 40:
							jaildirection[i] = 20;
							break;
					}
				}
			}
		}
	}

	public static void movecarts()
	{
		for(int i = 0; i < numminecart; i++)
		{
			int speed = minespeed[i];
			if ( speed < 2 ) speed = 2;

			if(minestatus[i] == 1 || minestatus[i] == 2)
			{
				minedistance[i] -= speed;
				if(minedistance[i] > 0)
				{
					int wx = 0, wy = 0;
					int sect = mineparent[i];
					short wallptr = sector[sect].wallptr;
					int wallnum = wallptr + sector[sect].wallnum;
					for(short w = wallptr; w < wallnum; w++)
					{
						switch(minedirection[i])
						{
							case 10:
								wx = wall[w].x;
								wy = wall[w].y + speed;
								break;
							case 20:
								wx = wall[w].x - speed;
								wy = wall[w].y;
								break;
							case 30:
								wx = wall[w].x;
								wy = wall[w].y - speed;
								break;
							case 40:
								wx = wall[w].x + speed;
								wy = wall[w].y;
								break;
						}
						engine.dragpoint(w, wx, wy);
					}
				}
				else
				{
					minedistance[i] = minefulldist[i];
					if(minestatus[i] == 1)
						minestatus[i] = 2;
					else minestatus[i] = 1;

					switch(minedirection[i])
					{
						case 10:
							minedirection[i] = 30;
							break;
						case 20:
							minedirection[i] = 40;
							break;
						case 30:
							minedirection[i] = 10;
							break;
						case 40:
							minedirection[i] = 20;
							break;
					}
				}
			}



			int sect = minechild[i];
			int minx = 131072;
			int miny = 131072;
			int maxx = -131072;
			int maxy = -131072;

			int wx, wy;
			int wallptr = sector[sect].wallptr;
			int wallnum = wallptr + sector[sect].wallnum;
			for(int w = wallptr; w < wallnum; w++)
			{
				wx = wall[w].x;
			    wy = wall[w].y;
			    if(wx > maxx ) maxx = wx;
			    if(wy > maxy ) maxy = wy;
			    if(wx < minx ) minx = wx;
			    if(wy < miny ) miny = wy;
			}

			short s = headspritesect[sect];
		    while(s >= 0)
		    {
		    	if ( badguy(sprite[s]) )
		        	engine.setsprite(s, (minx + maxx) >> 1, (maxy + miny) >> 1, sprite[s].z);

		    	s = nextspritesect[s];
		    }

		}
	}

	public static void torchesprocess()
	{
		int shade = engine.krand() & 8;
		for(int i = 0; i < numtorcheffects; i++)
		{
			int tshade = torchshade[i] - shade;
			short sectnum = torchsector[i];
			switch ( torchflags[i] )
			{
				case 0:
					sector[sectnum].floorshade = (byte) tshade;
					sector[sectnum].ceilingshade = (byte) tshade;
					break;
				case 1:
		        case 4:
		        	sector[sectnum].ceilingshade = (byte) tshade;
		        	break;
		        case 2:
		        case 5:
		        	sector[sectnum].floorshade = (byte) tshade;
		        	break;
			}


			int wallptr = sector[sectnum].wallptr;
			int wallnum = wallptr + sector[sectnum].wallnum;

			for(int w = wallptr; w < wallnum; w++)
			{
				if ( wall[w].lotag != 1 )
				{
					switch ( torchflags[i] )
		            {
		              case 0:
		              case 1:
		              case 2:
		              case 3:
		            	  wall[w].shade = (byte) tshade;
		            	  break;
		            }
				}
			}
		}
	}

	public static int dword_18D0A8, dword_18D0A0, dword_18D0AC, dword_18D0A4;
	public static byte byte_18D0BB;

	public static void lightningprocess(int snum)
	{
		int tourcheffect = 0;
		byte brightness = (byte) (ud.brightness >> 2);

		if ( dword_18D0A0 != 0 )
		{
			dword_18D0A8 -= 4;
			if ( dword_18D0A8 < 0 )
			{
				byte_18D0BB = brightness;
				dword_18D0A0 = 0;
				visibility = gVisibility;

//				engine.setbrightness(brightness, palette);
				for(tourcheffect = 0; tourcheffect < numlightnineffects; tourcheffect++)
				{
		          	int sect = lightninsector[tourcheffect];
					sector[sect].floorshade = (byte) lightninshade[tourcheffect];
					sector[sect].ceilingshade = (byte) lightninshade[tourcheffect];
					int wallptr = sector[sect].wallptr;
					int wallnum = wallptr + sector[sect].wallnum;
					for(int w = wallptr; w < wallnum; w++)
						wall[w].shade = (byte) lightninshade[tourcheffect];
				}
		    }
		}
		else if ( (gotpic[322] & 2) != 0 )
		{
		    gotpic[322] &= ~2;
		    if ( engine.getTile(2577).isLoaded() )
		    {
		    	visibility = 256;
		    	if ( engine.krand() > 65000 )
		    	{
		    		dword_18D0A8 = 256;
		    		dword_18D0A0 = 1;
		    		sound((engine.rand() % 3 + 351));
		    	}
		    }
		}
		else
		{
			byte_18D0BB = brightness;
		    visibility = gVisibility;
		}
		if ( dword_18D0A4 != 0 )
		{
			dword_18D0AC -= 4;
			if ( dword_18D0AC < 0 )
			{
				dword_18D0A4 = 0;
				for(tourcheffect = 0; tourcheffect < numlightnineffects; tourcheffect++)
				{
		          	int sect = lightninsector[tourcheffect];
					sector[sect].floorshade = (byte) lightninshade[tourcheffect];
					sector[sect].ceilingshade = (byte) lightninshade[tourcheffect];
					int wallptr = sector[sect].wallptr;
					int wallnum = wallptr + sector[sect].wallnum;
					for(int w = wallptr; w < wallnum; w++)
						wall[w].shade = (byte) lightninshade[tourcheffect];
				}
		    }
		}
		else if ( (gotpic[320] & 4) != 0 )
		{
		    gotpic[320] &= ~4;
		    if ( engine.getTile(2562).isLoaded() && engine.krand() > 65000 )
		    {
		    	dword_18D0A4 = 1;
		    	dword_18D0AC = 128;
		    	sound((engine.rand() % 3 + 351));
		    }
		}

		if ( dword_18D0A0 == 1 )
		{
			int chance = engine.krand() & 4;
			byte_18D0BB += chance;
		    switch ( chance )
		    {
		      case 0:
		        visibility = 2048;
		        break;
		      case 1:
		        visibility = 1024;
		        break;
		      case 2:
		        visibility = 512;
		        break;
		      case 3:
		        visibility = 256;
		        break;
		      default:
		        visibility = 4096;
		        break;
		    }
		    if ( byte_18D0BB > 8 )
		    	byte_18D0BB = 0;

//		    engine.setbrightness(byte_18D0BB, palette);
		    for(int i = 0; i < numlightnineffects; i++)
			{
				int sect = lightninsector[i];
				byte tshade = (byte) (lightninshade[i] - byte_18D0BB << 4);
				sector[sect].floorshade = tshade;
				sector[sect].ceilingshade = tshade;
				int wallptr = sector[sect].wallptr;
				int wallnum = wallptr + sector[sect].wallnum;
				for(int w = wallptr; w < wallnum; w++)
					wall[w].shade = tshade;
			}
		}

		if ( dword_18D0A4 == 1 )
		{
			int shade = engine.krand() & 8 + torchshade[tourcheffect];
			for(int i = 0; i < numlightnineffects; i++)
			{
				int sect = lightninsector[i];
				byte tshade = (byte) (lightninshade[i] - shade);
				sector[sect].floorshade = tshade;
				sector[sect].ceilingshade = tshade;
				int wallptr = sector[sect].wallptr;
				int wallnum = wallptr + sector[sect].wallnum;
				for(int w = wallptr; w < wallnum; w++)
					wall[w].shade = tshade;
			}
		}
	}

	public static void setsectinterpolate(int i)
	{
		 int j, k, startwall,endwall;

	    startwall = sector[sprite[i].sectnum].wallptr;
	    endwall = startwall+sector[sprite[i].sectnum].wallnum;

	    for(j=startwall;j<endwall;j++)
	    {
	    	game.pInt.setwallinterpolate(j, wall[j]);
	        k = wall[j].nextwall;
	        if(k >= 0)
	        {
	        	game.pInt.setwallinterpolate(k, wall[k]);
	            k = wall[k].point2;
	            game.pInt.setwallinterpolate(k, wall[k]);
	        }
	    }
	}
}
