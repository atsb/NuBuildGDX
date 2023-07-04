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
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Gameutils.FindDistance2D;
import static ru.m210projects.Redneck.Gameutils.rnd;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Premap.shadeEffect;
import static ru.m210projects.Redneck.Screen.myos;
import static ru.m210projects.Redneck.Screen.myospal;
import static ru.m210projects.Redneck.Sector.checkhitceiling;
import static ru.m210projects.Redneck.Sector.checkhitsprite;
import static ru.m210projects.Redneck.Sector.checkhitswitch;
import static ru.m210projects.Redneck.Sector.checkhitwall;
import static ru.m210projects.Redneck.Sector.dist;
import static ru.m210projects.Redneck.Sector.findplayer;
import static ru.m210projects.Redneck.Sector.isadoorwall;
import static ru.m210projects.Redneck.Sector.isadoorwall2;
import static ru.m210projects.Redneck.Sector.ldist;
import static ru.m210projects.Redneck.Sector.player_dist;
import static ru.m210projects.Redneck.SoundDefs.CHAINGUN_FIRE;
import static ru.m210projects.Redneck.SoundDefs.DUKE_LONGTERM_PAIN;
import static ru.m210projects.Redneck.SoundDefs.EJECT_CLIP;
import static ru.m210projects.Redneck.SoundDefs.INSERT_CLIP;
import static ru.m210projects.Redneck.SoundDefs.KICK_HIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_FIRE;
import static ru.m210projects.Redneck.SoundDefs.PISTOL_RICOCHET;
import static ru.m210projects.Redneck.SoundDefs.RPG_EXPLODE;
import static ru.m210projects.Redneck.SoundDefs.RPG_SHOOT;
import static ru.m210projects.Redneck.SoundDefs.SHOTGUN_COCK;
import static ru.m210projects.Redneck.SoundDefs.SHOTGUN_FIRE;
import static ru.m210projects.Redneck.SoundDefs.SHRINKER_FIRE;
import static ru.m210projects.Redneck.Sounds.sound;
import static ru.m210projects.Redneck.Sounds.spritesound;
import static ru.m210projects.Redneck.Sounds.xyzsound;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.View.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Weapons {

	public static final short weapon_sprites[] = {
			3340,
			FIRSTGUNSPRITE,
			SHOTGUNSPRITE,
			22,
			26,
			23,
			25,
			29,
			27,
			24,
			1409,
			25,
			3437
	};

	public static final short aimstats[] = {10,13,1,2};
	public static int aim(SPRITE s, int aang)
	{
	    int i, j;
	    int xv, yv;
	    int a = s.ang;

	    if(s.picnum == APLAYER && (ps[s.yvel].auto_aim == 0
	    		&& ps[s.yvel].curr_weapon != CHICKENBOW_WEAPON
	    		&& ps[s.yvel].curr_weapon != MOTO_WEAPON
	    		&& ps[s.yvel].curr_weapon != BOAT_WEAPON)) return -1;

	    j = -1;

	    int smax = 0x7fffffff;

	    int dx1 = sintable[(a+512-aang)&2047];
	    int dy1 = sintable[(a-aang)&2047];
	    int dx2 = sintable[(a+512+aang)&2047];
	    int dy2 = sintable[(a+aang)&2047];

	    int dx3 = sintable[(a+512)&2047];
	    int dy3 = sintable[a&2047];

	    for(int k=0;k<4;k++)
	    {
	        if( j >= 0 )
	            break;
	        for(i=headspritestat[aimstats[k]];i >= 0;i=nextspritestat[i])
	            if( sprite[i].xrepeat > 0 && sprite[i].extra >= 0 && (sprite[i].cstat&(257+32768)) == 257)
	                if( badguy(sprite[i]) || k < 2 )
	            {
	                if(!badguy(sprite[i]) && sprite[i].picnum != APLAYER && sprite[i].picnum != 5501
	                	|| sprite[i].picnum != APLAYER || ud.ffire != 0 || ud.coop != 1 || s.picnum != APLAYER || s == sprite[i])
	                {
		                xv = (sprite[i].x-s.x);
		                yv = (sprite[i].y-s.y);

		                if( (dy1*xv) <= (dx1*yv) )
		                    if( ( dy2*xv ) >= (dx2*yv) )
		                {
		                    int sdist = mulscale(dx3,xv,14) + mulscale(dy3,yv,14);
		                    if( sdist > 512 && sdist < smax )
		                    {
		                        if(s.picnum == APLAYER)
		                            a = ((klabs(scale(sprite[i].z-s.z,10,sdist)-((int)ps[s.yvel].horiz+ps[s.yvel].horizoff-100)) < 100)?1:0);
		                        else a = 1;

		                        if( a != 0 && engine.cansee(sprite[i].x,sprite[i].y,sprite[i].z-(32<<8),sprite[i].sectnum,s.x,s.y,s.z-(32<<8),s.sectnum) )
		                        {
		                            smax = sdist;
		                            j = i;
		                        }
		                    }
		                }
	                }
	            }
	    }

	    return j;
	}

	private static void tracers(int x1,int y1,int z1,int x2,int y2,int z2,int n)
	{
	     int i, xv, yv, zv;
	     short sect = -1;

		 i = n+1;
		 xv = (x2-x1)/i;
		 yv = (y2-y1)/i;
		 zv = (z2-z1)/i;

	     if( ( klabs(x1-x2)+klabs(y1-y2) ) < 3084 )
	         return;

		 for(i=n;i>0;i--)
		 {
			  x1 += xv;
			  y1 += yv;
			  z1 += zv;
			  sect = engine.updatesector(x1,y1,sect);
	          if(sect >= 0)
	          {
	        	  if(sector[sect].lotag == 2) {
	            	  int va = engine.krand()&2047;
	            	  int vy = 4+(engine.krand()&3);
	            	  int vx = 4+(engine.krand()&3);
	                  EGS(sect,x1,y1,z1,WATERBUBBLE,-32,vx,vy,va,0,0,ps[0].i,(short)5);
	              } else
	                  EGS(sect,x1,y1,z1,SMALLSMOKE,-32,14,14,0,0,0,ps[0].i,(short)5);
	          }
		 }
	}

	public static void addammo(int weapon, PlayerStruct p, int amount)
	{
		p.ammo_amount[weapon] += amount;

		if (p.ammo_amount[weapon] > currentGame.getCON().max_ammo_amount[weapon])
			p.ammo_amount[weapon] = currentGame.getCON().max_ammo_amount[weapon];
	}

	public static void addweapon(PlayerStruct p, int weapon) {

		if ( p.OnMotorcycle || p.OnBoat )
		{
			p.gotweapon[KNEE_WEAPON] = true;
			switch ( weapon )
		    {
		    	case THROWSAW_WEAPON:
		        p.gotweapon[BUZSAW_WEAPON] = true;
		        p.gotweapon[THROWSAW_WEAPON] = true;
		        p.ammo_amount[BUZSAW_WEAPON] = 1;
		        break;
		    	case CROSSBOW_WEAPON:
		        p.gotweapon[CROSSBOW_WEAPON] = true;
		        p.gotweapon[CHICKENBOW_WEAPON] = true;
		        break;
		    	case RATE_WEAPON:
		        p.gotweapon[RATE_WEAPON] = true;
		        break;
		    }
			return;
		}

		if (!p.gotweapon[weapon]) {
			p.gotweapon[weapon] = true;
			if (weapon == THROWSAW_WEAPON) {
				p.gotweapon[THROWSAW_WEAPON] = true;
				p.gotweapon[BUZSAW_WEAPON] = true;
				p.ammo_amount[BUZSAW_WEAPON] = 1;
			}
			if(currentGame.getCON().type == RRRA) {
				if (weapon == CROSSBOW_WEAPON) {
					p.gotweapon[CROSSBOW_WEAPON] = true;
					p.gotweapon[CHICKENBOW_WEAPON] = true;
				}
				if (weapon == RATE_WEAPON) {
					p.gotweapon[RATE_WEAPON] = true;
				}
			}
		}

		if (weapon == DYNAMITE_WEAPON)
			p.last_weapon = -1;

		p.random_club_frame = 0;

		if (p.holster_weapon == 0) {
			p.weapon_pos = -1;
			p.last_weapon = p.curr_weapon;
		} else {
			p.weapon_pos = 10;
			p.holster_weapon = 0;
			p.last_weapon = -1;
		}

		p.kickback_pic = 0;
		if(p.last_used_weapon != p.curr_weapon && p.curr_weapon != weapon)
			p.last_used_weapon = (byte) p.curr_weapon;

		p.curr_weapon = (short) weapon;

		switch (weapon) {
		case SHOTGUN_WEAPON:
			spritesound(169, p.i);
			break;
		case PISTOL_WEAPON:
			spritesound(5, p.i);
			break;
		case KNEE_WEAPON:
		case RATE_WEAPON:
		case DYNAMITE_WEAPON:
		case POWDERKEG_WEAPON:
		case HANDREMOTE_WEAPON:
			break;
		default:
			spritesound(4, p.i);
			break;
		}
	}

	public static void checkweapons(PlayerStruct p)
	{
	    short cw = p.curr_weapon;

	    if(cw < 1 || cw >= MAX_WEAPONS) return;

	    if(cw != 0)
	    {
//	        if((engine.krand()&1) != 0)
	            spawn(p.i,weapon_sprites[cw]);
//	        else switch(cw)
//	        {
//	            case DYNAMITE_WEAPON:
//	            case CROSSBOW_WEAPON:
//	                spawn(p.i,EXPLOSION2);
//	                break;
//	        }

//	        for(int i = 0; i < 5; i++)
//	        {
//	        	if ( p.gotkey[i] != 0 )
//	            {
//	            	int sp = spawn(p.i, DOORKEY);
//	            	switch(i)
//	            	{
//	            		case 0: sprite[sp].lotag = 100; break;
//	            		case 1: sprite[sp].lotag = 101; break;
//	            		case 2: sprite[sp].lotag = 102; break;
//	            		case 3: sprite[sp].lotag = 103; break;
//	            	}
//	            }
//	        }
	    }
	}

	public static void shoot(int i,int atwith)
	{
	    short sect, l, sa, p, j, k, scount;
	    int sx, sy, sz, vel = 0, zvel, x, oldzvel, dal, sp;
	    short sizx,sizy;
	    int hitx, hity, hitz;
	    short hitsect, hitsprite, hitwall;

	    SPRITE s = sprite[i];
	    sect = s.sectnum;
	    zvel = 0;

	    if(sect < 0 || sect >= MAXSECTORS) return;

	    if( s.picnum == APLAYER )
	    {
	        p = s.yvel;

	        sx = ps[p].posx;
	        sy = ps[p].posy;
	        sz = ps[p].posz+ps[p].pyoff+(4<<8);
	        sa = (short) ps[p].ang;

	        ps[p].crack_time = 777;

	    }
	    else
	    {
	        p = -1;
	        sa = s.ang;
	        sx = s.x;
	        sy = s.y;
	        sz = s.z-((s.yrepeat*engine.getTile(s.picnum).getHeight())<<1);
            sz -= (3<<8);
            if(badguy(s))
            {
                sx += (sintable[(sa+1024+96)&2047]>>7);
                sy += (sintable[(sa+512+96)&2047]>>7);
            }

	    }

	    switch(atwith)
	    {
	    	case POWDERKEGSPRITE:
	    		sp = spawn(i, atwith);
	    		sprite[sp].xvel = 32;
	            sprite[sp].ang = sprite[i].ang;
	            sprite[atwith].z -= 1280;
	    		break;
	        case BLOODSPLAT1:
	        case BLOODSPLAT2:
	        case BLOODSPLAT3:
	        case BLOODSPLAT4:

	            if(p >= 0)
	                sa += 64 - (engine.krand()&127);
	            else sa += 1024 + 64 - (engine.krand()&127);
	            zvel = 1024-(engine.krand()&2047);
	        case NEWCROWBAR:
	        case BUZSAW:
	        case 3510:
	            if(atwith == NEWCROWBAR || atwith == BUZSAW || atwith == 3510)
	            {
	                if(p >= 0)
	                {
	                    zvel = (100-(int)ps[p].horiz-ps[p].horizoff)<<5;
	                    sz += (6<<8);
	                    sa += 15;
	                }
	                else
	                {
	                    j = ps[findplayer(s)].i;
	                    x = player_dist;
	                    zvel = ( (sprite[j].z-sz)<<8 ) / (x+1);
	                    sa = engine.getangle(sprite[j].x-sx,sprite[j].y-sy);
	                }
	            }

	            engine.hitscan(sx,sy,sz,sect,
	                sintable[(sa+512)&2047],
	                sintable[sa&2047],zvel<<6,
	                pHitInfo,CLIPMASK1);

	            hitsect = pHitInfo.hitsect;
	            hitsprite = pHitInfo.hitsprite;
	            hitwall = pHitInfo.hitwall;
	            hitx = pHitInfo.hitx;
	            hity = pHitInfo.hity;
	            hitz = pHitInfo.hitz;

	            if ( (hitsect != -1 && (sector[hitsect].lotag == 160 && zvel > 0 || sector[hitsect].lotag == 161 && zvel < 0))
                    && hitsprite == -1
                    && hitwall == -1 )
	            {
	            	for(int si = 0; si < MAXSPRITES; si++)
	            	{
	            		if ( sprite[si].sectnum == hitsect && sprite[si].picnum == 1 && sprite[si].lotag == 7 )
	                    {
	            			int z = sector[sprite[sprite[si].owner].sectnum].ceilingz;
	            			if ( sector[hitsect].lotag == 161 )
	            				z = sector[sprite[sprite[si].owner].sectnum].floorz;

	            			engine.hitscan(
	            					sprite[sprite[si].owner].x + sprite[si].x + hitx,
	            					sprite[sprite[si].owner].y + sprite[si].y + hity,
	            					z,
	            					sprite[sprite[si].owner].sectnum,
	            	                sintable[(sa+512)&2047],
	            	                sintable[sa&2047],zvel<<6,
	            	                pHitInfo,CLIPMASK1);

            	            hitsect = pHitInfo.hitsect;
            	            hitsprite = pHitInfo.hitsprite;
            	            hitwall = pHitInfo.hitwall;
            	            hitx = pHitInfo.hitx;
            	            hity = pHitInfo.hity;
            	            hitz = pHitInfo.hitz;
            	            break;
	                    }
	            	}
	            }

	            if( atwith == BLOODSPLAT1 ||
	                atwith == BLOODSPLAT2 ||
	                atwith == BLOODSPLAT3 ||
	                atwith == BLOODSPLAT4 )
	            {
	                if( FindDistance2D(sx-hitx,sy-hity) < 1024 )
	                    if( hitwall >= 0 && wall[hitwall].overpicnum != BIGFORCE )
	                        if( ( wall[hitwall].nextsector >= 0 && hitsect >= 0 &&
	                            sector[wall[hitwall].nextsector].lotag == 0 &&
	                                sector[hitsect].lotag == 0 &&
	                                    sector[wall[hitwall].nextsector].lotag == 0 &&
	                                        (sector[hitsect].floorz-sector[wall[hitwall].nextsector].floorz) > (16<<8) ) ||
	                                            ( wall[hitwall].nextsector == -1 && sector[hitsect].lotag == 0 ) )
	                                                if( (wall[hitwall].cstat&16) == 0)
	                {
	                    if(wall[hitwall].nextsector >= 0)
	                    {
	                        k = headspritesect[wall[hitwall].nextsector];
	                        while(k >= 0)
	                        {
	                            if(sprite[k].statnum == 3 && sprite[k].lotag == 13)
	                                return;
	                            k = nextspritesect[k];
	                        }
	                    }

	                    if( wall[hitwall].nextwall >= 0 &&
	                        wall[wall[hitwall].nextwall].hitag != 0 )
	                            return;

	                    if(wall[hitwall].hitag == 0)
	                    {
	                        k = (short) spawn(i,atwith);
	                        sprite[k].xvel = -12;
	                        sprite[k].ang = (short) (engine.getangle(wall[hitwall].x-wall[wall[hitwall].point2].x,
	                            wall[hitwall].y-wall[wall[hitwall].point2].y)+512);
	                        sprite[k].x = hitx;
	                        sprite[k].y = hity;
	                        sprite[k].z = hitz;
	                        sprite[k].cstat |= (engine.krand()&4);
	                        ssp(k,CLIPMASK0);
	                        engine.setsprite(k,sprite[k].x,sprite[k].y,sprite[k].z);
	                        if( sprite[i].picnum == OOZFILTER )
	                            sprite[k].pal = 6;
	                    }
	                }
	                return;
	            }

	            if(hitsect < 0) break;

	            if( ( klabs(sx-hitx)+klabs(sy-hity) ) < 1024 )
	            {
	                if(hitwall >= 0 || hitsprite >= 0)
	                {
	                	if( atwith == 3510 )
	                	{
	                		j = EGS(hitsect,hitx,hity,hitz,3510,-15,0,0,sa,32,0,i,(short)4);
	                		 sprite[j].extra += 50;
	                	} else {
		                    j = EGS(hitsect,hitx,hity,hitz,NEWCROWBAR,-15,0,0,sa,32,0,i,(short)4);
		                    sprite[j].extra += (engine.krand()&7);
	                	}
	                    if(p >= 0)
	                    {
	                        k = (short) spawn(j,SMALLSMOKE);
	                        sprite[k].z -= (8<<8);
	                        if ( atwith == NEWCROWBAR )
	                        	spritesound(KICK_HIT,j);
	                        if ( atwith == 3510 )
	                        	spritesound(260,j);
	                    }

	                    if ( p >= 0 && ps[p].moonshine_amount > 0 && ps[p].moonshine_amount < 400 )
	                        sprite[j].extra += (currentGame.getCON().max_player_health>>2);

	                    if( hitsprite >= 0 && sprite[hitsprite].picnum != 129 && sprite[hitsprite].picnum != 82 )
	                    {
	                        checkhitsprite(hitsprite,j);
	                        if(p >= 0) checkhitswitch(p,hitsprite,1);
	                    }

	                    else if( hitwall >= 0 )
	                    {
	                        if( (wall[hitwall].cstat&2) != 0 )
	                            if(wall[hitwall].nextsector >= 0)
	                                if(hitz >= (sector[wall[hitwall].nextsector].floorz) )
	                                    hitwall = wall[hitwall].nextwall;

	                        if( hitwall >= 0 && wall[hitwall].picnum != 129 && wall[hitwall].picnum != 82 )
	                        {
	                            checkhitwall(j,hitwall,hitx,hity,hitz,atwith);
	                            if(p >= 0) checkhitswitch(p,hitwall,0);
	                        }
	                    }
	                }
	                else if(p >= 0 && zvel > 0 && sector[hitsect].lotag == 1)
	                {
	                    j = (short) spawn(ps[p].i,WATERSPLASH2);
	                    sprite[j].x = hitx;
	                    sprite[j].y = hity;
	                    sprite[j].ang = (short) ps[p].ang; // Total tweek
	                    sprite[j].xvel = 32;
	                    ssp((short)i,CLIPMASK0);
	                    sprite[j].xvel = 0;

	                }
	            }

	            break;

	        case SHOTSPARK1:
	        case NEWSHOTGUN:
	        case RIFLE:
	            if( s.extra >= 0 ) s.shade = -96;

	            if(p >= 0)
	            {
	                j = (short) aim( s, AUTO_AIM_ANGLE );
	                if(j >= 0)
	                {
	                    dal = ((sprite[j].xrepeat*engine.getTile(sprite[j].picnum).getHeight())<<1)+(5<<8);
	                    zvel = ( ( sprite[j].z-sz-dal )<<8 ) / ldist(sprite[ps[p].i], sprite[j]) ;
	                    sa = engine.getangle(sprite[j].x-sx,sprite[j].y-sy);
	                }

	                if(atwith == SHOTSPARK1)
	                {
	                    if(j == -1)
	                    {
	                        sa += 16-(engine.krand()&31);
	                        zvel = (100-(int)ps[p].horiz-ps[p].horizoff)<<5;
	                        zvel += 128-(engine.krand()&255);
	                    }
	                }
	                else
	                {
	                	if ( atwith == NEWSHOTGUN )
	                		sa += 64-(engine.krand()&127);
	                	else sa += 16-(engine.krand()&31);
	                    if(j == -1) zvel = (100-(int)ps[p].horiz-ps[p].horizoff)<<5;
	                    zvel += 128-(engine.krand()&255);
	                }
	                sz -= (2<<8);
	            }
	            else
	            {
	                j = (short) findplayer(s);
	                x = player_dist;
	                sz -= (4<<8);
	                zvel = ( (ps[j].posz-sz) <<8 ) / (ldist(sprite[ps[j].i], s ) );
	                if(s.picnum != 4477)
	                {
	                    zvel += 128-(engine.krand()&255);
	                    sa += 32-(engine.krand()&63);
	                }
	                else
	                {
	                    zvel += 128-(engine.krand()&255);
	                    sa = (short) (engine.getangle(ps[j].posx-sx,ps[j].posy-sy)+64-(engine.krand()&127));
	                }
	            }

	            s.cstat &= ~257;
	            engine.hitscan(sx,sy,sz,sect,
	                sintable[(sa+512)&2047],
	                sintable[sa&2047],
	                zvel<<6,pHitInfo,CLIPMASK1);

	            hitsect = pHitInfo.hitsect;
	            hitsprite = pHitInfo.hitsprite;
	            hitwall = pHitInfo.hitwall;
	            hitx = pHitInfo.hitx;
	            hity = pHitInfo.hity;
	            hitz = pHitInfo.hitz;

	            s.cstat |= 257;

	            if(hitsect < 0) return;

	            if ( atwith == NEWSHOTGUN && sector[hitsect].lotag == 1 && (engine.krand() & 1) == 0)
	            	return;

	            if( (engine.krand()&15) == 0 && sector[hitsect].lotag == 2 )
	                tracers(hitx,hity,hitz,sx,sy,sz,8-(ud.multimode>>1));

	            if(p >= 0)
	            {
	                k = EGS(hitsect,hitx,hity,hitz,SHOTSPARK1,-15,10,10,sa,0,0,i,(short)4);
	                sprite[k].extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[atwith]];
	                sprite[k].extra += (engine.krand()%6);

	                if( hitwall == -1 && hitsprite == -1)
	                {
	                    if( zvel < 0 )
	                    {
	                        if( (sector[hitsect].ceilingstat&1) != 0 )
	                        {
	                            sprite[k].xrepeat = 0;
	                            sprite[k].yrepeat = 0;
	                            return;
	                        }
	                        else
	                            checkhitceiling(hitsect);
	                    }
	                    if ( sector[hitsect].lotag != 1 )
	                    	spawn(k,SMALLSMOKE);
	                }

	                if(hitsprite >= 0)
	                {
	                	if ( sprite[hitsprite].picnum == TORNADO )
	                        return;
	                    checkhitsprite(hitsprite,k);
	                    if( sprite[hitsprite].picnum == APLAYER && (ud.coop != 1 || ud.ffire == 1) )
	                    {
	                        l = (short) spawn(k,JIBS6);
	                        sprite[k].xrepeat = sprite[k].yrepeat = 0;
	                        sprite[l].z += (4<<8);
	                        sprite[l].xvel = 16;
	                        sprite[l].xrepeat = sprite[l].yrepeat = 24;
	                        sprite[l].ang += 64-(engine.krand()&127);
	                    }
	                    else spawn(k,SMALLSMOKE);

	                    if(p >= 0 && (
	                        sprite[hitsprite].picnum == 121 ||
	                        sprite[hitsprite].picnum == 122 ||
	                        sprite[hitsprite].picnum == 123 ||
	                        sprite[hitsprite].picnum == 124 ||
	                        sprite[hitsprite].picnum == 127 ||
	                        sprite[hitsprite].picnum == 128 ||
	                        sprite[hitsprite].picnum == 2249 ||
	                        sprite[hitsprite].picnum == 8660 ||
	                        sprite[hitsprite].picnum == 2250) )
	                    {
	                        checkhitswitch(p,hitsprite,1);
	                        return;
	                    }
	                }
	                else if( hitwall >= 0 )
	                {
	                    spawn(k,SMALLSMOKE);

	                    SKIPBULLETHOLE:
	                    do {
		                    if( isadoorwall(wall[hitwall].picnum) && isadoorwall2(wall[hitwall].picnum) )
		                        break SKIPBULLETHOLE;
		                    if(p >= 0 && (
		                        wall[hitwall].picnum == 121 ||
		                        wall[hitwall].picnum == 122 ||
		                        wall[hitwall].picnum == 123 ||
		                        wall[hitwall].picnum == 124 ||
		                        wall[hitwall].picnum == 127 ||
		                        wall[hitwall].picnum == 128 ||
		                        wall[hitwall].picnum == 2249 ||
		                        wall[hitwall].picnum == 8660 ||
		                        wall[hitwall].picnum == 2250) )
		                    {
		                        checkhitswitch(p,hitwall,0);
		                        return;
		                    }

		                    if(wall[hitwall].hitag != 0 || ( wall[hitwall].nextwall >= 0 && wall[wall[hitwall].nextwall].hitag != 0 ) )
		                        break SKIPBULLETHOLE;

		                    if( hitsect >= 0 && sector[hitsect].lotag == 0 )
		                        if( wall[hitwall].overpicnum != BIGFORCE )
		                            if( (wall[hitwall].nextsector >= 0 && sector[wall[hitwall].nextsector].lotag == 0 ) ||
		                                ( wall[hitwall].nextsector == -1 && sector[hitsect].lotag == 0 ) )
		                                    if( (wall[hitwall].cstat&16) == 0)
		                    {
		                        if(wall[hitwall].nextsector >= 0)
		                        {
		                            l = headspritesect[wall[hitwall].nextsector];
		                            while(l >= 0)
		                            {
		                                if(sprite[l].statnum == 3 && sprite[l].lotag == 13)
		                                	break SKIPBULLETHOLE;
		                                l = nextspritesect[l];
		                            }
		                        }

		                        l = headspritestat[5];
		                        while(l >= 0)
		                        {
		                            if(sprite[l].picnum == BULLETHOLE)
		                                if(dist(sprite[l],sprite[k]) < (12+(engine.krand()&7)) )
		                                	break SKIPBULLETHOLE;
		                            l = nextspritestat[l];
		                        }
		                        l = (short) spawn(k,BULLETHOLE);
		                        sprite[l].xvel = -1;
		                        sprite[l].ang = (short) (engine.getangle(wall[hitwall].x-wall[wall[hitwall].point2].x,
		                            wall[hitwall].y-wall[wall[hitwall].point2].y)+512);
		                        ssp(l,CLIPMASK0);
		                    }
	                    } while(false);

	                    if( (wall[hitwall].cstat&2) != 0 )
	                        if(wall[hitwall].nextsector >= 0)
	                            if(hitz >= (sector[wall[hitwall].nextsector].floorz) )
	                                hitwall = wall[hitwall].nextwall;

	                    checkhitwall(k,hitwall,hitx,hity,hitz,SHOTSPARK1);
	                }
	            }
	            else
	            {
	                k = EGS(hitsect,hitx,hity,hitz,SHOTSPARK1,-15,24,24,sa,0,0,i,(short)4);
	                sprite[k].extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[atwith]];

	                if( hitsprite >= 0 )
	                {
	                    checkhitsprite(hitsprite,k);
	                    if( sprite[hitsprite].picnum != APLAYER )
	                        spawn(k,SMALLSMOKE);
	                    else sprite[k].xrepeat = sprite[k].yrepeat = 0;
	                }
	                else if( hitwall >= 0 )
	                    checkhitwall(k,hitwall,hitx,hity,hitz,SHOTSPARK1);
	            }

	            if( (engine.krand()&255) < 4 )
	                xyzsound(PISTOL_RICOCHET,k,hitx,hity,hitz);

	            return;

	        case OWHIP:
	        case UWHIP:

	            if( s.extra >= 0 ) s.shade = -96;

	            scount = 1;
	            if(atwith == OWHIP)
	            {
	            	sz -= 3840;
	                vel = 300;
	            }
	            else if(atwith == UWHIP)
	            {
	            	sz += 1024;
	                vel = 300;
	            }


	            if(p >= 0)
	            {
	                j = (short) aim( s, AUTO_AIM_ANGLE );
	                sx += sintable[(s.ang+672)&2047]>>6;
                    sy += sintable[(s.ang+160)&2047]>>6;
	                if(j >= 0)
	                {
	                    dal = ((sprite[j].xrepeat*engine.getTile(sprite[j].picnum).getHeight())<<1)-(12<<8);
	                    zvel = ((sprite[j].z-sz-dal)*vel ) / ldist(sprite[ps[p].i], sprite[j]) ;
	                    sa = engine.getangle(sprite[j].x-sx,sprite[j].y-sy);
	                }
	                else
	                    zvel = (100-(int)ps[p].horiz-ps[p].horizoff)*98;
	            }
	            else
	            {
	                j = (short) findplayer(s);
	                x = player_dist;
	                if ( s.picnum == VIXEN ) {
	                	sa -= engine.krand()&16;
	                }
	                else if ( s.picnum != UFOBEAM )
	                	sa += 16-(engine.krand()&31);
	                zvel = ( ( (ps[j].oposz - sz + (3<<8) ) )*vel ) / ldist(sprite[ps[j].i],s);
	            }

	            oldzvel = zvel;

	            if(p >= 0)
	            {
	            	sizx = 7;
                    sizy = 7;
	            } else {
	            	sizx = 8;
                    sizy = 8;
	            }

	            while(scount > 0)
	            {
	                j = EGS(sect,sx,sy,sz,atwith,-127,sizx,sizy,sa,vel,zvel,i,(short)4);
	                sprite[j].extra += (engine.krand()&7);
	                sprite[j].cstat = 128;
	                sprite[j].clipdist = 4;

	                sa = (short) (s.ang+32-(engine.krand()&63));
	                zvel = oldzvel+512-(engine.krand()&1023);

	                scount--;
	            }

	            return;

	        case FIRELASER:
	        case SHITBALL:
	        case DILDO:

	            if( s.extra >= 0 ) s.shade = -96;

	            scount = 1;
	            if(atwith == SHITBALL)
	            	vel = 400;
	            else
	            {
	            	vel = 840;
	                sz -= (4<<7);

	                if(s.picnum == HULK) {
	                	sx += sintable[(sa+768)&2047]>>6;
                        sy += sintable[(sa+256)&2047]>>6;
	                    sz += 3072;
	                }
	                if ( s.picnum == VIXEN )
	                    sz -= 3072;
	            }

	            if(p >= 0)
	            {
	                j = (short) aim( s, AUTO_AIM_ANGLE );
	                sx += sintable[(s.ang+672)&2047]>>6;
                    sy += sintable[(s.ang+160)&2047]>>6;
	                if(j >= 0)
	                {
	                    dal = ((sprite[j].xrepeat*engine.getTile(sprite[j].picnum).getHeight())<<1)-(12<<8);
	                    zvel = ((sprite[j].z-sz-dal)*vel ) / ldist(sprite[ps[p].i], sprite[j]) ;
	                    sa = engine.getangle(sprite[j].x-sx,sprite[j].y-sy);
	                }
	                else
	                    zvel = (100-(int)ps[p].horiz-ps[p].horizoff)*98;
	            }
	            else
	            {
	                j = (short) findplayer(s);
	                x = player_dist;

	                if(s.picnum == HULK)
	                	sa -= engine.krand()&31;
	                else if ( s.picnum == VIXEN )
	                	sa -= engine.krand()&16;
	                else if ( s.picnum != UFOBEAM )
	                	sa += 16-(engine.krand()&31);
	                zvel = ( ( (ps[j].oposz - sz + (3<<8) ) )*vel ) / ldist(sprite[ps[j].i],s);
	            }

	            oldzvel = zvel;

	            if(atwith == SHITBALL)
	            {
	            	sizx = 18;
	            	sizy = 18;
	            	if ( s.picnum == 8705 )
	            		sz -= (20<<8);
	            	else sz -= (10<<8);
	            }
	            else
	            {
	            	if( atwith == DILDO )
	            	{
	            		sizx = 8;
                        sizy = 8;
	            	}
	            	else if( atwith == FIRELASER )
	                {
	                    if(p >= 0)
	                    {

	                        sizx = 34;
	                        sizy = 34;
	                    }
	                    else
	                    {
	                        sizx = 18;
	                        sizy = 18;
	                    }
	                }
	                else
	                {
	                    sizx = 18;
	                    sizy = 18;
	                }
	            }

	            if(p >= 0) { sizx = 7; sizy = 7; }

	            while(scount > 0)
	            {
	                j = EGS(sect,sx,sy,sz,atwith,-127,sizx,sizy,sa,vel,zvel,i,(short)4);
	                sprite[j].extra += (engine.krand()&7);

	                if(atwith == FIRELASER)
	                {
	                	sprite[j].xrepeat = sprite[j].yrepeat = 8;
	                }

	                sprite[j].cstat = 128;
	                sprite[j].clipdist = 4;

	                sa = (short) (s.ang+32-(engine.krand()&63));
	                zvel = oldzvel+512-(engine.krand()&1023);

	                scount--;
	            }

	            return;

	        case ALIENBLAST:
	            sz += (3<<8);
	        case CROSSBOW:
	        case CIRCLESAW:
	        case CHIKENCROSSBOW:
	        case 1790:
	            if( s.extra >= 0 ) s.shade = -96;

	            scount = 1;
	            vel = 644;

	            j = -1;
	            short id = 0;
	            if(p >= 0)
	            {
	                j = (short) aim( s, 48 );
	                if(j >= 0)
	                {
	                	id = j;
	                	if ( atwith == CHIKENCROSSBOW )
	                    {
	                		if ( sprite[j].picnum == 4861 && sprite[j].picnum == 4862 )
	                			id = ps[myconnectindex].i;
	                    }
	                    dal = ((sprite[j].xrepeat*engine.getTile(sprite[j].picnum).getHeight())<<1)+(8<<8);
	                    zvel = ( (sprite[j].z-sz-dal)*vel ) / ldist(sprite[ps[p].i], sprite[j]);
	                    if( sprite[j].picnum != 4989 )
	                        sa = engine.getangle(sprite[j].x-sx,sprite[j].y-sy);
	                }
	                else zvel = (100-(int)ps[p].horiz-ps[p].horizoff)*81;

	                switch ( atwith )
	                {
	                	case CROSSBOW: spritesound(RPG_SHOOT,i); break;
	                	case CHIKENCROSSBOW: spritesound(244, i); break;
	                	case 1790: spritesound(94, i); break;
	                }
	            }
	            else
	            {
	                j = (short) findplayer(s);
	                x = player_dist;
	                sa = engine.getangle(ps[j].oposx-sx,ps[j].oposy-sy);
	                if(sprite[i].picnum == 4607)
	                    sz -= (32<<8);
	                else if(sprite[i].picnum == 4557)
	                {
	                    vel = 772;
	                    sz += 24<<8;
	                }

	                l = (short) ldist(sprite[ps[j].i],s);
	                zvel = ( (ps[j].oposz-sz)*vel) / l;

	                if( badguy(s) && (s.hitag&face_player_smart) != 0 )
	                    sa = (short) (s.ang+(engine.krand()&31)-16);
	            }

	            if( p >= 0 && j >= 0)
	               l = j;
	            else l = -1;

	            if ( atwith == 1790 )
	            {
	            	zvel = -2560;
	            	vel *= 2;
	            }

	            j = EGS(sect,
	                sx+(sintable[(348+sa+512)&2047]/448),
	                sy+(sintable[(sa+348)&2047]/448),
	                sz-(1<<8),atwith,0,14,14,sa,vel,zvel,i,(short)4);

	            if ( atwith == 1790 )
	            {
	            	sprite[j].extra = 10;
	            	sprite[j].zvel = -2560;
	            }
	            else if ( atwith == CHIKENCROSSBOW )
	            {
	            	sprite[j].lotag = id;
	            	sprite[j].hitag = 0;
	            	lotsofmoney(sprite[j], (engine.krand() & 3) + 1);
	            }

	            sprite[j].extra += (engine.krand()&7);
	            if(atwith != ALIENBLAST)
	                sprite[j].yvel = l;
	            else
	            {
	                sprite[j].yvel = (short) currentGame.getCON().numfreezebounces;
	                sprite[j].xrepeat >>= 1;
	                sprite[j].yrepeat >>= 1;
	                sprite[j].zvel -= (2<<4);
	            }

	            if(p == -1)
	            {
	                if(sprite[i].picnum == 4649)
	                {
	                    sprite[j].xrepeat = 8;
	                    sprite[j].yrepeat = 8;
	                }
	                else if(atwith != ALIENBLAST)
	                {
	                    sprite[j].xrepeat = 30;
	                    sprite[j].yrepeat = 30;
	                    sprite[j].extra >>= 2;
	                }
	            }
	            else if(ps[p].curr_weapon == TIT_WEAPON)
	            {
	                sprite[j].extra >>= 2;
	                sprite[j].ang += 16-(engine.krand()&31);
	                sprite[j].zvel += 256-(engine.krand()&511);

	                if( (ps[p].hbomb_hold_delay) != 0 )
	                {
	                    sprite[j].x -= sintable[sa&2047]/644;
	                    sprite[j].y -= sintable[(sa+1024+512)&2047]/644;
	                }
	                else
	                {
	                    sprite[j].x += sintable[sa&2047]>>8;
	                    sprite[j].y += sintable[(sa+1024+512)&2047]>>8;
	                }
	                sprite[j].xrepeat >>= 1;
	                sprite[j].yrepeat >>= 1;
	            }

	            sprite[j].cstat = 128;
	            if(atwith == CROSSBOW)
	                sprite[j].clipdist = 4;
	            else if(atwith == CHIKENCROSSBOW)
	            	sprite[j].clipdist = 4;
	            else
	                sprite[j].clipdist = 40;

	            break;

	        case BOUNCEMINE:
	        case MORTER:
	        case 3464:

	            if( s.extra >= 0 ) s.shade = -96;

	            j = ps[findplayer(s)].i;
	            x = ldist(sprite[j],s);

	            zvel = -x>>1;

	            if(zvel < -4096)
	                zvel = -2048;
	            vel = x>>4;

	            int sizex = 32;
	            int sizey = 32;
	            if(atwith == 3464)
	            {
	 	            sizex = 16;
	 	            sizey = 16;
	            }

	            EGS(sect,
	                sx+(sintable[(512+sa+512)&2047]>>8),
	                sy+(sintable[(sa+512)&2047]>>8),
	                sz+(6<<8),atwith,-64,sizex,sizey,sa,vel,zvel,i,(short)1);
	            break;
            case BOWLINGBALL:
                sp = spawn(i, atwith);
	    		sprite[sp].xvel = 250;
	            sprite[sp].ang = sprite[i].ang;
	            sprite[atwith].z -= 3840;
            	break;
	    }
	}

	public static final int rake_x[] = { 580,676,310,491,356,210,310,614,0,0 };
	public static final int rake_y[] = { 369,363,300,323,371,400,300,440,0,0 };

	public static final byte crowbar_frames[] = {0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7};
	public static final int crowbar_x[] = { 310,342,364,418,350,316,282,288,0,0 };
	public static final int crowbar_y[] = { 300,362,320,268,248,248,277,420,0,0 };

	public static final byte pistol_frames[] = {0,0,1,1,2,2,3,3,4,4,6,6,6,6,5,5,4,4,3,3,0,0};
	public static final int pistol_x[] = { 194,190,185,208,215,215,216,216,201,170 };
	public static final int pistol_y[] = { 256,249,248,238,228,218,208,256,245,258 };

	public static final byte rpistol_frames[] = {0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,};
	public static final int rpistol_x[] = { 244, 244, 244 };
	public static final int rpistol_y[] = { 256, 249, 248 };

	public static final byte shotgun_frames[] = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 0, 0,20,20,21,21,21,21,20,20,20,20, 0, 0, };
	public static final byte shotgun_frames2[] = { 0,0,1,1,2,2,5,5,6,6,7,7,8,8,0,0,0,0,0,0,0 };
	public static final byte shotgun_frames3[] = { 0,0,3,3,4,4,5,5,6,6,7,7,8,8,0,0,20,20,21,21,21,21,20,20,20,20,0,0 };

	public static final short shotgun_x[] = { 300,300,300,300,300,330,320,310,305,306,302 };
	public static final short shotgun_y[] = { 315,300,302,305,302,302,303,306,302,404,384, };


	public static final byte dynamite_flames[] = { 1,1,1,1,1,2,2,2,2,3,3,3,4,4,4,5,5,5,5,5,6,6,6,0};
	public static final byte weap5_frames[] = { 0,1,1,2,2,3,2,3,2,3,2,2,2,2,2,2,2,2,2,4,4,4,4,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,7 };
	public static final byte weap7_frames[] = { 0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 };
	public static final byte weap8_frames[] = { 1,1,1,1,1,2,2,2,2,2,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0 };

	public static void moveweapons() {
		int j, k, p, q;
		int dax, day, daz, x, ll;
		int qq;

		short i = headspritestat[4];
		short nexti = i >= 0 ? nextspritestat[i] : 0;

		for (; i >= 0; i = nexti) {
			nexti = nextspritestat[i];
			SPRITE s = sprite[i];

			if (s.sectnum < 0) {
				engine.deletesprite(i);
				continue;
			}

			game.pInt.setsprinterpolate(i, s);

			switch (s.picnum) {
			case RADIUSEXPLOSION:
				engine.deletesprite(i);
				continue;
			case TONGUE:
				hittype[i].temp_data[0] = sintable[(hittype[i].temp_data[1]) & 2047] >> 9;
				hittype[i].temp_data[1] += 32;
				if (hittype[i].temp_data[1] > 2047) {
					engine.deletesprite(i);
					continue;
				}

				if (sprite[s.owner].statnum == MAXSTATUS)
					if (!badguy(sprite[s.owner])) {
						engine.deletesprite(i);
						continue;
					}

				s.ang = sprite[s.owner].ang;
				s.x = sprite[s.owner].x;
				s.y = sprite[s.owner].y;
				if (sprite[s.owner].picnum == APLAYER)
					s.z = sprite[s.owner].z - (34 << 8);
				for (k = 0; k < hittype[i].temp_data[0]; k++) {
					q = EGS(s.sectnum,
							s.x + ((k * sintable[(s.ang + 512) & 2047]) >> 9),
							s.y + ((k * sintable[s.ang & 2047]) >> 9),
							s.z + ((k * ksgn(s.zvel)) * klabs(s.zvel / 12)),
							TONGUE, -40 + (k << 1), 8, 8, 0, 0, 0, i, (short)5);
					sprite[q].cstat = 128;
					sprite[q].pal = 8;
				}
				q = EGS(s.sectnum,
						s.x + ((k * sintable[(s.ang + 512) & 2047]) >> 9),
						s.y + ((k * sintable[s.ang & 2047]) >> 9),
						s.z + ((k * ksgn(s.zvel)) * klabs(s.zvel / 12)),
						INNERJAW, -40, 32, 32, 0, 0, 0, i, (short)5);
				sprite[q].cstat = 128;
				if (hittype[i].temp_data[1] > 512
						&& hittype[i].temp_data[1] < (1024))
					sprite[q].picnum = INNERJAW + 1;

				continue;

			case ALIENBLAST:
				if (s.yvel < 1 || s.extra < 2 || (s.xvel | s.zvel) == 0) {
					j = spawn(i, TRANSPORTERSTAR);
					sprite[j].pal = 1;
					sprite[j].xrepeat = 32;
					sprite[j].yrepeat = 32;
					engine.deletesprite(i);
					continue;
				}

			case CROSSBOW:
			case CHIKENCROSSBOW:
			case FIRELASER:
			case SHITBALL:
			case CIRCLESAW:
			case UWHIP:
			case OWHIP:
			case DILDO:
			case 1790:
				p = -1;

				if (s.picnum == CROSSBOW && sector[s.sectnum].lotag == 2) {
					k = s.xvel >> 1;
					ll = s.zvel >> 1;
				} else {
					k = s.xvel;
					ll = s.zvel;
				}

				dax = s.x;
				day = s.y;
				daz = s.z;

				getglobalz(i);
				qq = CLIPMASK1;

				switch (s.picnum) {
				case CROSSBOW:
					if (hittype[i].picnum != 4557 && s.xrepeat >= 10
							&& sector[s.sectnum].lotag != 2) {
						j = spawn(i, SMALLSMOKE);
						sprite[j].z += (1 << 8);
					}
					break;
				case CHIKENCROSSBOW:
					s.hitag++;
					if (hittype[i].picnum != 4557 && s.xrepeat >= 10
							&& sector[s.sectnum].lotag != 2) {
						j = spawn(i, SMALLSMOKE);
						sprite[j].z += (1 << 8);
						if ( (engine.krand() & 0xF) == 2 )
					          j = spawn(i, FEATHERS);
					}
					if ( sprite[s.lotag].extra <= 0 )
						s.lotag = 0;

					if ( s.lotag != 0 && s.hitag > 5 )
					{
						SPRITE spr = sprite[s.lotag];
						short ang = engine.getangle(spr.x - s.x, spr.y - s.y);
						int seekang = ang - s.ang;
						if(seekang >= 100)
						{
							if(seekang == 100)
								s.ang = ang;
							if(klabs(seekang) <= 1023)
								s.ang += 51;
							else s.ang -= 51;
						} else if(klabs(seekang) <= 1023)
							s.ang -= 51;
						else s.ang += 51;

						if ( s.hitag > 180 && s.zvel <= 0 )
					          s.zvel += 200;
					}
					break;
				case 1790:
					if ( s.extra != 0 )
					{
						s.zvel = (short) (250 * s.extra);
						s.zvel = (short) -s.zvel;
				        s.extra--;
					}
					else makeitfall(currentGame.getCON(), i);

					if ( s.xrepeat >= 10 && sector[s.sectnum].lotag != 2 )
					{
				        j = spawn(i, SMALLSMOKE);
				        sprite[j].z += 256;
					}
					break;
				}

				j = movesprite(i, (k * (sintable[(s.ang + 512) & 2047])) >> 14,
						(k * (sintable[s.ang & 2047])) >> 14, ll, qq);

				if(s.yvel >= 0)
				{
					switch(s.picnum)
					{
					case CROSSBOW:
					case CHIKENCROSSBOW:
					case 1790:
						if (FindDistance2D(s.x - sprite[s.yvel].x, s.y
								- sprite[s.yvel].y) < 256)
							j = 49152 | s.yvel;
						break;
					}
				}

				if (s.sectnum < 0) {
					engine.deletesprite(i);
					continue;
				}

				if (sector[s.sectnum].filler == 800) {
					engine.deletesprite(i);
					continue;
				} else if ((j & kHitTypeMask) != kHitSprite)

					if (s.picnum != ALIENBLAST) {
						if (s.z < hittype[i].ceilingz) {
							j = kHitSector | (s.sectnum);
							s.zvel = -1;
						} else if ((s.z > hittype[i].floorz && sector[s.sectnum].lotag != 1)
								|| (s.z > hittype[i].floorz + (16 << 8) && sector[s.sectnum].lotag == 1)) {
							j = kHitSector | (s.sectnum);
							if (sector[s.sectnum].lotag != 1)
								s.zvel = 1;
						}
					}

				if (s.picnum == FIRELASER) {
					int shade = -52;
					for (k = -3; k < 2; k++) {
						x = EGS(s.sectnum,
								s.x
										+ ((k * sintable[(s.ang + 512) & 2047]) >> 9),
								s.y + ((k * sintable[s.ang & 2047]) >> 9),
								s.z + ((k * ksgn(s.zvel)) * klabs(s.zvel / 24)),
								FIRELASER, shade, s.xrepeat, s.yrepeat, 0, 0,
								0, s.owner, (short)5);

						sprite[x].cstat = 128;
						sprite[x].pal = s.pal;
						shade += 4;
					}
				} else if (s.picnum == SHITBALL)
					if (s.zvel < 6144)
						s.zvel += currentGame.getCON().gc - 112;

				if (j != 0) {
					if ((j & kHitTypeMask) == kHitSprite) {
						j &= kHitIndexMask;

						if(currentGame.getCON().type == RRRA) {
							if ( sprite[j].picnum == MINION
									&& (s.picnum == CROSSBOW || s.picnum == CHIKENCROSSBOW)
									&& sprite[j].pal == 19 )
							{
								spritesound(9, i);
								j = spawn(i, EXPLOSION2);
								sprite[j].x = s.x;
								sprite[j].y = s.y;
								sprite[j].z = s.z;
								continue;
							}
						} else {
							if (s.picnum == ALIENBLAST && sprite[j].pal == 1)
								if (badguy(sprite[j])
										|| sprite[j].picnum == APLAYER) {
									j = spawn(i, TRANSPORTERSTAR);
									sprite[j].pal = 1;
									sprite[j].xrepeat = 32;
									sprite[j].yrepeat = 32;

									engine.deletesprite(i);
									continue;
								}
						}

						checkhitsprite((short)j, i);

						if (sprite[j].picnum == APLAYER) {
							p = sprite[j].yvel;
							spritesound(PISTOL_BODYHIT, j);

							if (s.picnum == SHITBALL) {
								if ( sprite[s.owner].picnum == MAMAJACKOLOPE )
					            {
									guts(s, 7387, 2, myconnectindex);
									guts(s, 7392, 2, myconnectindex);
									guts(s, 7397, 2, myconnectindex);
					            }
								ps[p].horiz += 32;
								ps[p].return_to_center = 8;

								if (ps[p].loogcnt == 0) {
									if (Sound[DUKE_LONGTERM_PAIN].num < 1)
										spritesound(DUKE_LONGTERM_PAIN, ps[p].i);

									j = 3 + (engine.krand() & 3);
									ps[p].numloogs = j;
									ps[p].loogcnt = 24 * 4;
									for (x = 0; x < j; x++) {
										int lx = engine.krand()%xdim;
                                    	int ly = engine.krand()%ydim;
                                    	if(p == screenpeek) {
                                    		loogiex[x] = lx;
                                    		loogiey[x] = ly;
                                    	}
									}
								}
							}
						}
					} else if ((j & kHitTypeMask) == kHitWall) {
						j &= kHitIndexMask;
						if ( sprite[s.owner].picnum == MAMAJACKOLOPE )
			            {
							guts(s, 7387, 2, myconnectindex);
							guts(s, 7392, 2, myconnectindex);
							guts(s, 7397, 2, myconnectindex);
			            }


						if (s.picnum != CROSSBOW
								&& s.picnum != CHIKENCROSSBOW
								&& s.picnum != ALIENBLAST
								&& s.picnum != SHITBALL
								&& s.picnum != CIRCLESAW
								&& (wall[j].overpicnum == MIRROR || wall[j].picnum == MIRROR)) {
							k = engine.getangle(wall[wall[j].point2].x
									- wall[j].x, wall[wall[j].point2].y
									- wall[j].y);
							s.ang = (short) (((k << 1) - s.ang) & 2047);
							s.owner = i;
							spawn(i, TRANSPORTERSTAR);
							continue;
						} else {
							engine.setsprite(i, dax, day, daz);
							checkhitwall(i, j, s.x, s.y, s.z, s.picnum);

							if(currentGame.getCON().type != RRRA) {
								if (s.picnum == ALIENBLAST) {
									if (wall[j].overpicnum != MIRROR
											&& wall[j].picnum != MIRROR) {
										s.extra >>= 1;
										s.yvel--;
										if (s.xrepeat > 8)
											s.xrepeat -= 2;
										if (s.yrepeat > 8)
											s.yrepeat -= 2;
									}

									k = engine.getangle(wall[wall[j].point2].x
											- wall[j].x, wall[wall[j].point2].y
											- wall[j].y);
									s.ang = (short) (((k << 1) - s.ang) & 2047);
									continue;
								}
							}

							if (s.picnum == CIRCLESAW) {
								if (wall[j].picnum < 3643 || wall[j].picnum >= 3646) {
									if (s.extra > 0) {
										if (wall[j].overpicnum != MIRROR && wall[j].picnum != MIRROR) {
											s.extra -= 20;
											s.yvel--;
										}
										k = engine.getangle(wall[wall[j].point2].x - wall[j].x,
												wall[wall[j].point2].y - wall[j].y);
										s.ang = (short) (((k << 1) - s.ang) & 2047);
									} else {
										s.x += sintable[(s.ang + 512) & 2047] >> 7;
										s.y += sintable[(s.ang) & 2047] >> 7;
										if ( sprite[s.owner].picnum != DAISYMAE && sprite[s.owner].picnum != DAISYMAE+1 )
										{
											int nspawn = spawn(i, CIRCLESTUCK);
											sprite[nspawn].xrepeat = 8;
											sprite[nspawn].yrepeat = 8;
											sprite[nspawn].cstat = 16;
											sprite[nspawn].ang = (short) ((sprite[i].ang + 512) & 0x7FF);
											sprite[nspawn].clipdist = mulscale(engine.getTile(s.picnum).getWidth(), s.xrepeat, 7);
										}
										engine.deletesprite(i);
									}
								} else
									engine.deletesprite(i);
								continue;
							}
						}
					} else if ((j & kHitTypeMask) == kHitSector) {
						engine.setsprite(i, dax, day, daz);

						if ( sprite[s.owner].picnum == MAMAJACKOLOPE )
			            {
							guts(s, 7387, 2, myconnectindex);
							guts(s, 7392, 2, myconnectindex);
							guts(s, 7397, 2, myconnectindex);
			            }

						if (s.zvel < 0) {
							if ((sector[s.sectnum].ceilingstat & 1) != 0)
								if (sector[s.sectnum].ceilingpal == 0) {
									engine.deletesprite(i);
									continue;
								}

							checkhitceiling(s.sectnum);
						}

						if (s.picnum == ALIENBLAST) {
							bounce(i);
							ssp(i, qq);
							s.extra >>= 1;
							if (s.xrepeat > 8)
								s.xrepeat -= 2;
							if (s.yrepeat > 8)
								s.yrepeat -= 2;
							s.yvel--;
							continue;
						}
					}

					if (s.picnum != SHITBALL) {
						switch(s.picnum)
						{
						case CROSSBOW:
						case CHIKENCROSSBOW:
						case 1790:
							if(s.picnum == 1790)
								s.extra = 160;
							k = spawn(i, EXPLOSION2);
							sprite[k].x = dax;
							sprite[k].y = day;
							sprite[k].z = daz;

							if (s.xrepeat < 10) {
								sprite[k].xrepeat = 6;
								sprite[k].yrepeat = 6;
							} else if ((j & kHitTypeMask) == kHitSector) {
								sprite[k].cstat |= 8;
								sprite[k].z += (48 << 8);
							}
							break;
						default:
							if (s.picnum != CIRCLESAW
								&& s.picnum != ALIENBLAST
								&& s.picnum != FIRELASER)
							{
								k = spawn(i, EXPLOSION2);
								sprite[k].xrepeat = sprite[k].yrepeat = (short) (s.xrepeat >> 1);
								if ((j & kHitTypeMask) == kHitSector) {
									if (s.zvel < 0) {
										sprite[k].cstat |= 8;
										sprite[k].z += (72 << 8);
									}
								}
							}
							break;
						}

						switch(s.picnum)
						{
						case CROSSBOW:
						case CHIKENCROSSBOW:
						case 1790:
							if(s.picnum != CHIKENCROSSBOW)
								spritesound(RPG_EXPLODE, i);
							else spritesound(247, i);
							if(s.picnum == CHIKENCROSSBOW)
								s.extra = 150;
							if(s.picnum == 1790)
								s.extra = 160;

							if (s.xrepeat >= 10) {
								x = s.extra;
								hitradius(i, currentGame.getCON().crossbowblastradius, x >> 2, x >> 1, x
										- (x >> 2), x);
							} else {
								x = s.extra + (global_random & 3);
								hitradius(i, (currentGame.getCON().crossbowblastradius >> 1), x >> 2,
										x >> 1, x - (x >> 2), x);
							}
							break;
						}
					}

					engine.deletesprite(i);
					continue;

				}
				if ((s.picnum == CROSSBOW || s.picnum == CHIKENCROSSBOW) && sector[s.sectnum].lotag == 2
						&& s.xrepeat >= 10 && rnd(140))
					spawn(i, WATERBUBBLE);
				continue;
			case SHOTSPARK1:
				p = findplayer(s);
				execute(currentGame.getCON(), i, p, player_dist);
				continue;
			}
		}
	}

	public static short fistsign;
	public static void displayweapon(int snum)
	{
	    int gun_pos, looking_arc, cw;
	    int weapon_xoffset, i;
	    int o,pal;
	    int gs, posx, posy;

	    PlayerStruct p = ps[snum];
	    short kb = p.kickback_pic;

	    o = 0;
	    looking_arc = klabs(p.look_ang)/9;

	    gs = sprite[p.i].shade;
	    if ( shadeEffect[p.cursectnum] )
	        gs = 16;
	    if(gs > 24) gs = 24;

	    if(p.newowner >= 0 || ud.camerasprite >= 0 || over_shoulder_on > 0 || (sprite[p.i].pal != 1 && sprite[p.i].extra <= 0) )
	        return;

	    gun_pos = 80-p.weapon_pos*p.weapon_pos;

	    weapon_xoffset =  (160)-90;
	    weapon_xoffset -= (sintable[((p.weapon_sway>>1)+512)&2047]/(1024+512));
	    weapon_xoffset -= 58 + p.weapon_ang;
	    if( sprite[p.i].xrepeat < 8 )
	        gun_pos -= klabs(sintable[(p.weapon_sway<<2)&2047]>>9);
	    else gun_pos -= klabs(sintable[(p.weapon_sway>>1)&2047]>>10);

	    gun_pos -= (p.hard_landing<<3);

	    if(ud.screen_size > 2)
	    	gun_pos += engine.getTile(BOTTOMSTATUSBAR).getHeight() / 4;

	    if(ud.screen_size > 3)
		    gun_pos += engine.getTile(1649).getHeight() / 4;

	    if(p.last_weapon >= 0)
	        cw = p.last_weapon;
	    else cw = p.curr_weapon;

	    if( sprite[p.i].xrepeat < 8 )
	    {
	        if(p.jetpack_on == 0 )
	        {
	            i = sprite[p.i].xvel;
	            looking_arc += 32-(i>>1);
	            fistsign += i>>1;
	        }
	        cw = weapon_xoffset;
	        weapon_xoffset += sintable[(fistsign)&2047]>>10;
	        myos(weapon_xoffset+250-(p.look_ang>>1),
	             looking_arc+258-(klabs(sintable[(fistsign)&2047]>>8)),
	             1408,gs,o);
	        weapon_xoffset = cw;
	        weapon_xoffset -= sintable[(fistsign)&2047]>>10;
	        myos(weapon_xoffset+40-(p.look_ang>>1),
	        	 looking_arc+200+(klabs(sintable[(fistsign)&2047]>>8)),
	        	 1408,gs,o|4);
	    }
	    else if(p.OnMotorcycle)
	    {
	    	if(over_shoulder_on != 0)
	    		return;
	    	int pic = 7170;
	    	if(numplayers == 1)
	    	{
	    		if(kb != 0)
	    		{
	    			gs = 0;
	    			if ( kb == 1 )
	    			{
	    	      	     if ( (engine.krand() & 1) == 1 )
	    	      	    	 pic = 7171;
	    	      	     else
	    	      	    	 pic = 7172;
	    			}
	    			else if ( kb == 4 )
	    			{
	    				if ( (engine.krand() & 1) == 1 )
	    					pic = 7173;
	    				else
	    					pic = 7174;
	    			}
	    		}
	    	}
	    	else
	    	{
	    		if(kb != 0)
	    		{
	    			gs = 0;
	    			switch ( kb )
	    	        {
	    	        case 1:
	    	        	pic = 7171;
	    	        	break;
	    	        case 2:
	    	        	pic = 7172;
	    	        	break;
	    	        case 3:
	    	        	pic = 7173;
	    	        	break;
	    	        case 4:
	    	            pic = 7174;
	    	            break;
	    	        default:
	    	        	pic = 7170;
	    	        	break;
	    	        }
	    		}
	    	}

	    	if(sprite[p.i].pal == 1)
                pal = 1;
            else
            {
                pal = sector[p.cursectnum].floorpal;
                if(pal == 0)
                    pal = p.palookup;
            }

	    	if ( p.TiltStatus < 0 )
	    		engine.rotatesprite((160-(p.look_ang>>1))<<16,174<<16,0x8800, 5 * p.TiltStatus + 2047,pic,gs,pal,10,windowx1,windowy1,windowx2,windowy2);
	        else
	        	engine.rotatesprite((160-(p.look_ang>>1))<<16,174<<16,0x8800, 5 * p.TiltStatus,pic,gs,pal,10,windowx1,windowy1,windowx2,windowy2);
	    }
	    else if(p.OnBoat)
	    {
	    	if(over_shoulder_on != 0)
	    		return;
	    	int pic = 7175;
	    	if(p.TiltStatus <= 0)
	    	{
	    		if(p.TiltStatus == 0)
	    		{
	    			if(kb != 0)
	    			{
	    				if(kb > 3)
	    				{
	    					if(kb <= 6)
	    					{
	    						pic = 7179;
	    						gs = -96;
	    					}
	    				} else {
	    					pic = 7178;
	    					gs = -96;
	    				}
	    			}
	    		}
	    		else if(kb != 0)
	    		{
	    			if(kb > 3)
    				{
    					if(kb > 6)
    					{
    						pic = 7177;
    					} else {
    						pic = 7183;
    						gs = -96;
    					}
    				} else {
    					pic = 7182;
    					gs = -96;
    				}
	    		} else pic = 7177;
	    	} else if(kb != 0) {
	    		if(kb > 3)
				{
					if(kb > 6)
					{
						pic = 7176;
					} else {
						pic = 7181;
						gs = -96;
					}
				} else {
					pic = 7180;
					gs = -96;
				}
	    	} else pic = 7176;


	    	if(sprite[p.i].pal == 1)
                pal = 1;
            else
            {
                pal = sector[p.cursectnum].floorpal;
                if(pal == 0) pal = p.palookup;
            }

	    	posy = 170;
	    	if ( p.NotOnWater == 0 )
	            posy = (kb >> 2) + 170;

	    	if ( p.TiltStatus < 0 )
	    		engine.rotatesprite((160-(p.look_ang>>1))<<16,posy<<16,0x10200, p.TiltStatus + 2047,pic,gs,pal,10,windowx1,windowy1,windowx2,windowy2);
	        else
	        	engine.rotatesprite((160-(p.look_ang>>1))<<16,posy<<16,0x10200, p.TiltStatus,pic,gs,pal,10,windowx1,windowy1,windowx2,windowy2);

	    }
	    else switch(cw)
	    {
	        case KNEE_WEAPON:
	        case RATE_WEAPON:
                if(sprite[p.i].pal == 1)
                    pal = 1;
                else
                {
                    pal = sector[p.cursectnum].floorpal;
                    if(pal == 0)
                        pal = p.palookup;
                }

                if(cw == RATE_WEAPON)
                	myospal(((rake_x[crowbar_frames[kb]]>>1) - 12 + weapon_xoffset)-(p.look_ang>>1) + 20,
                    		looking_arc+210-(244 - rake_y[crowbar_frames[kb]]) - gun_pos - 80, 32768, 3510+crowbar_frames[kb],gs,o,pal);
                else
                	myospal(((crowbar_x[
                	                    crowbar_frames[
                	                                   kb]]>>1) - 12 + weapon_xoffset)-(p.look_ang>>1),
                			looking_arc+200-(244 - crowbar_y[crowbar_frames[kb]]) - gun_pos, 32768, 3340+crowbar_frames[kb],gs,o,pal);
	            break;

	        case POWDERKEG_WEAPON:
	        case BOWLING_WEAPON:
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;

	            posx = weapon_xoffset + 170;
	            posy = looking_arc + 214 - gun_pos;

	            weapon_xoffset += 8;
	            gun_pos -= 10;

	            if(cw == 12)
	            {
	            	if ( p.ammo_amount[12] != 0 )
	                {
	            		myospal(posx - (p.look_ang>>1), posy + 11 + ((kb)<<3), 32768, 3428, gs, o, pal);
	            		return;
	                }
	            }
	            else if ( p.ammo_amount[POWDERKEG_WEAPON] != 0 )
	            {
	            	myospal(weapon_xoffset + 180 - (p.look_ang>>1), posy + ((kb)<<3), 36700, 3438, gs, o, pal);
	            	myospal(weapon_xoffset + 90 - (p.look_ang>>1), posy + ((kb)<<3), 36700, 3438, gs, o | 4, pal);
	            	return;
	            }
	            myospal(posx - (p.look_ang>>1), posy + 11, 36700, 3365, gs, o, pal);
	            break;

	        case CROSSBOW_WEAPON:
	        case CHICKENBOW_WEAPON:
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;

	            posx = weapon_xoffset + 210;
	            posy = looking_arc + 255 - gun_pos;

	            if(cw == CROSSBOW_WEAPON) {
		            if(kb != 0) {
			            switch(weap5_frames[kb])
			            {
			            	case 2:
			            		myospal( posx - (p.look_ang>>1) - 3, posy - 5, 36700, weap5_frames[kb] + 3452,gs,o, pal);
			            		break;
			            	case 3:
			            		myospal( posx - (p.look_ang>>1) - 10, posy - 5, 36700, weap5_frames[kb] + 3452, gs, o, pal);
			            		break;
			            	case 1:
			            		myospal( posx - (p.look_ang>>1) - 10, posy - 5, 36700, weap5_frames[kb] + 3452, gs, o, pal);
			            		break;
			            	default:
			            		myospal( posx - (p.look_ang>>1), posy, 36700, weap5_frames[kb] +  3452, gs, o, pal);
			            		break;
			            }
		            } else myospal(posx - (p.look_ang>>1), posy, 36700, 3452, gs, o, pal);
	            }

	            if(cw == CHICKENBOW_WEAPON) {
	            	posy -= 40;
		            if(kb != 0) {
			            switch(weap5_frames[kb])
			            {
			            	case 2:
			            		myospal( posx - (p.look_ang>>1) - 10, posy + 33, 36700, weap5_frames[kb] + 3482,gs,o, pal);
			            		break;
			            	case 3:
			            		myospal( posx - (p.look_ang>>1) - 7, posy + 48, 36700, weap5_frames[kb] + 3482, gs, o, pal);
			            		break;
			            	case 1:
			            		myospal( posx - (p.look_ang>>1) - 10, posy + 10, 36700, weap5_frames[kb] + 3482, gs, o, pal);
			            		break;
			            	default:
			            		myospal( posx - (p.look_ang>>1), posy, 36700, weap5_frames[kb] +  3482, gs, o, pal);
			            		break;
			            }
		            } else {
		            	if(ud.multimode >= 2)
		            		myospal(posx - (p.look_ang>>1), posy - 3, 36700, 3482, gs, o, pal);
		            	else {
		            		if(p.chiken_phase != 0)
		            			myospal(posx - (p.look_ang>>1), posy - 3, 36700, 3489, gs, o, pal);
		            		else if(p.chiken_pic == 5)
		            		{
		            			myospal(posx - (p.look_ang>>1), posy - 3, 36700, 3489, gs, o, pal);
		            			p.chiken_phase = 6;
		            			spritesound(327, p.i);
		            		} else
		            			myospal(posx - (p.look_ang>>1), posy, 36700, 3482, gs, o, pal);
		            	}
		            }
	            }
	        	break;

	        case SHOTGUN_WEAPON:
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;

	            if ( p.shotgun_splitshot != 0 )
	            {
	            	if ( kb >= 26 ) //reload
	                {
            			if ( shotgun_frames[kb] <= 0 )
            			{
            				posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb]] >> 1) - 12;
            				posy = 244 - shotgun_y[shotgun_frames[kb]];
            			}
            			else
            			{
            				posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb-10]] >> 1) - 12;
            				posy = 244 - shotgun_y[shotgun_frames[kb-10]];
            			}
            			posy = 180 - posy;
                     	if ( kb == 23 )
                     		posy += 60;
                     	if ( kb == 24 )
                     		posy += 30;

                     	myospal(posx + 64 - (p.look_ang>>1), looking_arc + posy - gun_pos, 32768, shotgun_frames[kb] + 3350, gs, 0, pal);
                     	if ( shotgun_frames[kb] == 21 )
                     		myospal(posx + 96 - (p.look_ang>>1), looking_arc + posy - gun_pos, 32768, 3372, gs, 0, pal);
	                }
	            	else
	                {
	            		if ( shotgun_frames[kb] == 3 || shotgun_frames[kb] == 4 )
	            			gs = 0;
	                    posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb]] >> 1) - 12;
	                    posy = 180 - (244 - shotgun_y[shotgun_frames[kb]]);
	                    myospal(posx + 64 - (p.look_ang>>1),
	                    		looking_arc + posy - gun_pos, 32768,
	                    		shotgun_frames[kb] + 3350, gs, 0, pal);
	                }
	            }
	            else
	            {
	            	if ( kb >= 16 ) //reload
	            	{
	            		if ( p.shotgunstatus != 0 )
	            		{
	            			if ( shotgun_frames3[kb] <= 0 )
	            			{
	            				posx = weapon_xoffset + (shotgun_x[shotgun_frames3[kb]] >> 1) - 12;
	            				posy = 244 - shotgun_y[shotgun_frames3[kb]];
	            			}
	            			else
	            			{
	            				posx = weapon_xoffset + (shotgun_x[shotgun_frames[kb]] >> 1) - 12;
	            				posy = 244 - shotgun_y[shotgun_frames[kb]];
	            			}
	                     	posy = 180 - posy;
	                     	if ( kb == 23 )
	                     		posy += 60;
	                     	if ( kb == 24 )
	                     		posy += 30;

	                     	myospal(posx + 64 - (p.look_ang>>1), looking_arc + posy - gun_pos, 32768, shotgun_frames3[kb] + 3350, gs, 0, pal);
	                     	if ( shotgun_frames3[kb] == 21 )
	                     		myospal(posx + 96 - (p.look_ang>>1), looking_arc + posy - gun_pos, 32768, 3372, gs, 0, pal);
	            		}
	            	}
	            	else if ( p.shotgunstatus != 0 )
	                {
	            		if ( shotgun_frames3[kb] == 3 || shotgun_frames3[kb] == 4 )
	            			gs = 0;
	            		myospal((weapon_xoffset + (shotgun_x[shotgun_frames3[kb]] >> 1) - 12) + 64 - (p.look_ang>>1),
	            				looking_arc + (180 - (244 - shotgun_y[shotgun_frames3[kb]])) - gun_pos,
	            				32768, shotgun_frames3[kb] + 3350, gs, 0, pal);
	                }
	            	else
	                {
	            		if ( shotgun_frames2[kb] == 1 || shotgun_frames2[kb] == 2 )
	            			gs = 0;
	            		myospal((weapon_xoffset + (shotgun_x[shotgun_frames2[kb]] >> 1) - 12) + 64 - (p.look_ang>>1),
	            				(180 - (244 - shotgun_y[shotgun_frames2[kb]])) + looking_arc - gun_pos,
	            				32768, shotgun_frames2[kb] + 3350, gs, 0, pal);
	                }
	            }
	            break;

	        case RIFLEGUN_WEAPON:
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;

	            if(kb > 0)
	                gun_pos -= sintable[(kb)<<7]>>12;

	            if(kb > 0 && sprite[p.i].pal != 1) weapon_xoffset += 1-(engine.rand()&3);

	            switch(kb)
	            {
	                case 0:
	                    myospal(weapon_xoffset+178-(p.look_ang>>1)+ 30,looking_arc+233-gun_pos+5,32768, 3380,gs,o,pal);
	                    break;
	                default:
	                    if(kb < 8)
	                    {
	                        i = engine.rand()&7;
	                        myospal(weapon_xoffset+178-(p.look_ang>>1)+ 30 + i,looking_arc+233-gun_pos+5,32768, 3381,gs,o,pal);
	                    }
	                    else myospal(weapon_xoffset+178-(p.look_ang>>1) + 30,looking_arc+233-gun_pos+5, 32768, 3382,gs,o,pal);
	                    break;
	            }
	            break;

            case PISTOL_WEAPON:
            	if(sprite[p.i].pal == 1)
            		pal = 1;
            	else
            		pal = sector[p.cursectnum].floorpal;

            	if( (kb) < 22)
            		myospal(pistol_x[pistol_frames[kb]] - 12 - (p.look_ang>>1) + weapon_xoffset, 244 - (244 - pistol_y[pistol_frames[kb]] + 10) + looking_arc - gun_pos, 36700, pistol_frames[kb] + 3328, gs, 0, pal);
            	else
            	{
            		int xoffset = 0, yoffset = 0;
            		switch ( kb )
            		{
            			case 28:
            			case 36:
            				yoffset = 10;
            				xoffset = 5;
            				break;
            			case 29:
            			case 35:
            				yoffset = 20;
            				xoffset = 10;
            				break;
            			case 30:
            			case 34:
            				yoffset = 30;
            				xoffset = 15;
            				break;
            			case 31:
            			case 33:
            				yoffset = 40;
            				xoffset = 20;
            				break;
            			case 32:
            				yoffset = 50;
            				xoffset = 25;
            				break;
            		}
            		myospal(rpistol_x[rpistol_frames[kb-22]] - (p.look_ang>>1) - xoffset,
            				looking_arc + (244 - (244 - rpistol_y[rpistol_frames[kb-22]])) - gun_pos + yoffset,
            				36700, rpistol_frames[kb-22] + 3336, gs, 0, pal);
            	}
            	break;

	        case DYNAMITE_WEAPON:
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else pal = sector[p.cursectnum].floorpal;

	            weapon_xoffset -= sintable[(768+((kb)<<7))&2047]>>11;
	            gun_pos -= 9 * kb;
	            myospal(weapon_xoffset+190-(p.look_ang>>1),looking_arc+260-gun_pos,36700,3360,gs,o,pal);
	            break;

	        case HANDREMOTE_WEAPON:
	        	if(kb < 20)
	        	{
	        		if(sprite[p.i].pal == 1)
	        			pal = 1;
	        		else pal = sector[p.cursectnum].floorpal;
	        		posx = weapon_xoffset + 290;
	        		posy = looking_arc + 258 - gun_pos - 64;
	        		if(kb == 0 || kb < 5 )
	        			myospal(posx - (p.look_ang>>1) - 25, posy + p.detonate_count - 20, 36700, 1752, 0, o, pal);

	        		if ( kb != 0 )
	        			myospal(posx - (p.look_ang>>1), looking_arc + 258 - gun_pos - 20, dynamite_flames[kb] + 3360, gs, o, pal);
	        		else
	        			myospal(posx - (p.look_ang>>1), looking_arc + 258 - gun_pos - 20, 36700, 3361, gs, o, pal);
	        	}
	        	break;

	        case TIT_WEAPON:
	        	if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;

	            if(kb == 0)
	                myospal( (weapon_xoffset >> 1) + 150-(p.look_ang>>1),(looking_arc >> 1) + 266 - gun_pos,3446,gs,o,pal);
	            else myospal( (weapon_xoffset >> 1) + 150-(p.look_ang>>1),(looking_arc >> 1) + 266 - gun_pos,3445,0,o,pal);

	            break;

	        case ALIENBLASTER_WEAPON:
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;

	            posx = weapon_xoffset + 260;
	            posy = looking_arc + 215 - gun_pos;

	            if(kb != 0)
	            	myospal(posx - (p.look_ang>>1), posy, 36700, weap7_frames[kb] + 3415, -32, o, pal);
	            else
	            	myospal(posx - (p.look_ang>>1), posy, 36700, 3415, gs, o, pal);
	            break;



	        case THROWSAW_WEAPON:
	        case BUZSAW_WEAPON:
	            weapon_xoffset += 28;
	            looking_arc += 18;
	            if(sprite[p.i].pal == 1)
	                pal = 1;
	            else
	                pal = sector[p.cursectnum].floorpal;
	            if((kb) == 0)
	            {
	            	myospal(weapon_xoffset+188-(p.look_ang>>1),
	            			looking_arc+240-gun_pos,44040, 3384,gs,o,pal);
	            }
	            else
	            {
	                if(sprite[p.i].pal != 1)
	                {
	                    weapon_xoffset += engine.rand()&3;
	                    gun_pos += (engine.rand()&3);
	                }

	                if(cw == BUZSAW_WEAPON)
	                {
	                	myospal(weapon_xoffset+184-(p.look_ang>>1),
	                			looking_arc+240-gun_pos,44040,(kb & 2) + BUZSAW,gs,
	                			o,pal);
	                }
	                else
	                {
	                	myospal(weapon_xoffset+184-(p.look_ang>>1),
	                			looking_arc+240-gun_pos,44040, 3384+ weap8_frames[kb],gs,
	                			o,pal);
	                }
	            }
	            break;
	        case MOTO_WEAPON:
	        case BOAT_WEAPON:
	        	return;
	    }
	}

	public static void weaponprocess(int snum)
	{
		PlayerStruct p = ps[snum];
		int pi = p.i;
		SPRITE s = sprite[pi];
		boolean shrunk = (s.yrepeat < 8);
		int sb_snum = 0;
		if(p.cheat_phase <= 0) sb_snum = sync[snum].bits;

		int j, i, k;

		int psectlotag = sector[p.cursectnum].lotag;

		 if( p.curr_weapon == THROWSAW_WEAPON || p.curr_weapon == BUZSAW_WEAPON )
		        p.random_club_frame += 64; // Glowing
		    if ( p.curr_weapon == 8 || p.curr_weapon == 12 )
		        p.random_club_frame += 64;

		    if(p.rapid_fire_hold == 1)
		    {
		        if( (sb_snum&(1<<2)) != 0 ) return;
		        p.rapid_fire_hold = 0;
		    }

		    if(shrunk || p.tipincs != 0 || p.access_incs != 0)
		        sb_snum &= ~(1<<2);
		    else if ( !shrunk && ((sb_snum&(1<<2)) != 0) && (p.kickback_pic) == 0 && p.fist_incs == 0 &&
		         p.last_weapon == -1 && ( p.weapon_pos == 0 || p.holster_weapon == 1 ) )
		    {
		        p.crack_time = 777;

		        if(p.holster_weapon == 1)
		        {
		            if( p.last_pissed_time <= (26*218) && p.weapon_pos == -9)
		            {
		                p.holster_weapon = 0;
		                p.weapon_pos = 10;
		                FTA(74,p);
		            }
		        }
		        else switch(p.curr_weapon)
		        {
		            case CROSSBOW_WEAPON:
		                if( p.ammo_amount[CROSSBOW_WEAPON] > 0 )
		                    (p.kickback_pic)=1;
		                break;
		            case CHICKENBOW_WEAPON:
		                if( p.ammo_amount[CHICKENBOW_WEAPON] > 0 )
		                    (p.kickback_pic)=1;
		                break;
		            case HANDREMOTE_WEAPON:
		                p.hbomb_hold_delay = 0;
		                (p.kickback_pic) = 1;
		                break;

		            case PISTOL_WEAPON:
		                if( p.ammo_amount[PISTOL_WEAPON] > 0 )
		                {
		                    p.ammo_amount[PISTOL_WEAPON]--;
		                    (p.kickback_pic) = 1;
		                }
		                break;


		            case RIFLEGUN_WEAPON:
		                if( p.ammo_amount[RIFLEGUN_WEAPON] > 0 )
		                    (p.kickback_pic)=1;
		                break;

		            case SHOTGUN_WEAPON:
		                if( p.ammo_amount[SHOTGUN_WEAPON] > 0 && p.random_club_frame == 0 )
		                    (p.kickback_pic)=1;
		                break;

		            case POWDERKEG_WEAPON:
		            case BOWLING_WEAPON:
		            	if ( p.curr_weapon == 12 )
		            	{
		            		if ( p.ammo_amount[12] > 0 )
		            			p.kickback_pic = 1;
		            	}
		            	else if ( p.ammo_amount[8] > 0 )
		            	{
		            		p.kickback_pic = 1;
		            	}
		                break;

		            case THROWSAW_WEAPON:
		            case BUZSAW_WEAPON:
		                if( p.curr_weapon == BUZSAW_WEAPON )
		                {
		                    if( p.ammo_amount[BUZSAW_WEAPON] > 0 )
		                    {
		                        (p.kickback_pic) = 1;
		                        spritesound(431,pi);
		                    }
		                }
		                else if( p.ammo_amount[THROWSAW_WEAPON] > 0)
		                {
		                    (p.kickback_pic) = 1;
		                    spritesound(SHRINKER_FIRE,pi);
		                }
		                break;

		            case TIT_WEAPON:
		                if( p.ammo_amount[TIT_WEAPON] > 0 )
		                {
		                    (p.kickback_pic) = 1;
		                    p.hbomb_hold_delay ^= 1;
		                }
		                break;
		            case ALIENBLASTER_WEAPON:
		                if( p.ammo_amount[ALIENBLASTER_WEAPON] > 0 )
		                    (p.kickback_pic) = 1;
		                break;

		            case DYNAMITE_WEAPON:
		                if ( p.ammo_amount[DYNAMITE_WEAPON] > 0)
		                    (p.kickback_pic) = 1;
		                break;
		            case KNEE_WEAPON:
		            case RATE_WEAPON:
		            	if(p.curr_weapon != RATE_WEAPON)
		            	{
		            		if(/*p.ammo_amount[KNEE_WEAPON] != 0 && */ p.quick_kick == 0)
			                	(p.kickback_pic) = 1;
		            	} else {
		            		if(/*p.ammo_amount[RATE_WEAPON] != 0 && */ p.quick_kick == 0)
			                	(p.kickback_pic) = 1;
		            	}
		                break;
		            case MOTO_WEAPON:
		            	if(p.ammo_amount[MOTO_WEAPON] != 0 ) {
		                	(p.kickback_pic) = 1;
		                	p.hbomb_hold_delay ^= 1;
		            	}
		            	break;
		            case BOAT_WEAPON:
		            	if(p.ammo_amount[BOAT_WEAPON] != 0)
		                	(p.kickback_pic) = 1;
		            	break;
		        }
		    }
		    else if(p.kickback_pic != 0)
		    {
		        switch( p.curr_weapon )
		        {
			        case DYNAMITE_WEAPON:
		                (p.kickback_pic)++;
		                if( (p.kickback_pic) == 1 )
		                	sound(401);

		                if( (p.kickback_pic) == 6 && (sb_snum&(1<<2)) != 0 )
		                    p.rapid_fire_hold = 1;

		                if( (p.kickback_pic) > 19 )
		                {
		                	p.kickback_pic = 0;
		                    p.curr_weapon = 10;
		                    p.last_weapon = -1;
		                    p.weapon_pos = 10;
		                    p.field_57C = 45;
		                    p.detonate_count = 1;
		                    sound(402);
		                }
		                break;
			        case HANDREMOTE_WEAPON:

		                (p.kickback_pic)++;
		                if ( p.field_57C < 0 )
		                    p.hbomb_on = 0;
		                if ( p.kickback_pic == 39 )
		                {
		                	p.hbomb_on = 0;
		                	p.field_290 = 0x2000;
		                	sub_64EF0(snum);
		                }

		                if((p.kickback_pic)==12)
		                {
		                    p.ammo_amount[4]--;
		                    if(p.ammo_amount[5] > 0)
		                    	p.ammo_amount[5]--;

		                    if(p.on_ground && (sb_snum&2) != 0 )
		                    {
		                        k = 15;
		                        i = (int) ((p.horiz+p.horizoff-100)*20);
		                    }
		                    else
		                    {
		                        k = 140;
		                        i = (int) (-512-((p.horiz+p.horizoff-100)*20));
		                    }

		                    j = EGS(p.cursectnum,
		                        p.posx+(sintable[((int)p.ang+512)&2047]>>6),
		                        p.posy+(sintable[(int)p.ang&2047]>>6),
		                        p.posz,DYNAMITE,-16,9,9,
		                        (int)p.ang,2*(k+(p.hbomb_hold_delay<<5)),i,pi,(short)1);

		                    if(k == 15)
		                    {
		                        sprite[j].yvel = 3;
		                        sprite[j].z += (8<<8);
		                    }

		                    k = hits(pi);
		                    if( k < 512 )
		                    {
		                        sprite[j].ang += 1024;
		                        sprite[j].zvel /= 3;
		                        sprite[j].xvel /= 3;
		                    }

		                    p.hbomb_on = 1;

		                } else if( (p.kickback_pic) < 12 && (sb_snum&(1<<2)) != 0)
		                    p.hbomb_hold_delay++;

		                if( (p.kickback_pic) == 40 )
		                {
		                    (p.kickback_pic) = 0;
		                    p.curr_weapon = 4;
		                    p.last_weapon = -1;
		                    p.detonate_count = 0;
		                    p.field_57C = 45;
		                    if ( p.ammo_amount[4] <= 0 ) {
		                    	checkavailweapon(p);
		                    	break;
		                    }
		                    addweapon(p, 4);
		                    p.weapon_pos = -9;
		                }

		                break;

			        case PISTOL_WEAPON:
		                if( (p.kickback_pic)==1)
		                {
		                    shoot(pi,SHOTSPARK1);
		                    spritesound(PISTOL_FIRE,pi);

		                    lastvisinc = totalclock+32;
		                    if(snum == screenpeek)
		                    	gVisibility = 0;
		                    p.field_290 = 0x2000;
		                    sub_64EF0(snum);

		                    if(psectlotag != 857)
		                    {
		                    	p.posxv -= 16 * sintable[((int)p.ang+512)&2047];
				                p.posyv -= 16 * sintable[(int)p.ang&2047];
		                    }
		                }
		                else if((p.kickback_pic) == 2 && p.ammo_amount[1] <= 0)
		                {
		                	p.kickback_pic = 0;
		                	checkavailweapon(p);
		                }

		                (p.kickback_pic)++;

		                if((p.kickback_pic) >= 22)
		                {
		                    if( p.ammo_amount[PISTOL_WEAPON] <= 0 )
		                    {
		                        (p.kickback_pic)=0;
		                        checkavailweapon(p);
		                    }
		                    else
		                    {
		                    	if ( (p.ammo_amount[1] % 6) != 0)
		                         	p.kickback_pic = 38;
		                    	else {
			                        switch((p.kickback_pic))
			                        {
			                            case 24:
			                                spritesound(EJECT_CLIP,pi);
			                                break;
			                            case 30:
			                                spritesound(INSERT_CLIP,pi);
			                                break;
			                        }
		                        }
		                    }
		                }

		                if((p.kickback_pic) == 38)
		                {
		                    (p.kickback_pic) = 0;
		                    checkavailweapon(p);
		                }
		                break;

			        case SHOTGUN_WEAPON:

		                (p.kickback_pic)++;
		                if ( p.kickback_pic == 6 && p.shotgunstatus == 0 && p.ammo_amount[2] > 1 && (sb_snum&(1<<2)) != 0 )
		                    p.shotgun_splitshot = 1;

		                if(p.kickback_pic == 4)
		                {
		                    shoot(pi,NEWSHOTGUN);
		                    shoot(pi,NEWSHOTGUN);
		                    shoot(pi,NEWSHOTGUN);
		                    shoot(pi,NEWSHOTGUN);
		                    shoot(pi,NEWSHOTGUN);
		                    shoot(pi,NEWSHOTGUN);
		                    shoot(pi,NEWSHOTGUN);

		                    p.ammo_amount[SHOTGUN_WEAPON]--;

		                    spritesound(SHOTGUN_FIRE,pi);
		                    p.field_290 = 0x2000;
		                    sub_64EF0(snum);
		                    lastvisinc = totalclock+32;
		                    if(snum == screenpeek)
		                    	gVisibility = 0;
		                }

		                if ( p.kickback_pic == 7 )
		                {
		                	if ( p.shotgun_splitshot != 0 )
		                	{
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);
		                		shoot(pi,NEWSHOTGUN);

		                		p.ammo_amount[2]--;
		                		spritesound(109, pi);
		                	}

		                	if ( psectlotag != 857 )
		                    {
		                		p.posxv -= 32 * sintable[((int)p.ang+512)&2047];
				                p.posyv -= 32 * sintable[(int)p.ang&2047];
		                    }
		                }

		                if ( p.shotgunstatus != 0 )
		                {
		                	switch(p.kickback_pic)
			                {
			                    case 16:
			                        checkavailweapon(p);
			                        break;
			                    case 17:
			                    	spritesound(SHOTGUN_COCK, pi);
			                        break;
			                    case 28:
			                        p.kickback_pic = 0;
			                        p.shotgunstatus = 0;
				                    p.shotgun_splitshot = 0;
			                        break;
			                }
		                }
		                else if ( p.shotgun_splitshot != 0 )
		                {
		                	switch(p.kickback_pic)
			                {
			                    case 26:
			                        checkavailweapon(p);
			                        break;
			                    case 27:
			                    	spritesound(SHOTGUN_COCK, pi);
			                        break;
			                    case 38:
			                        p.kickback_pic = 0;
			                        p.shotgunstatus = 0;
				                    p.shotgun_splitshot = 0;
			                        break;
			                }
		                }
		                else
		                {
		                	if ( p.kickback_pic == 16 )
		                	{
		                		checkavailweapon(p);
		                		p.kickback_pic = 0;
		                		p.shotgunstatus = 1;
		                		p.shotgun_splitshot = 0;
		                	}
		                }
		                break;
			        case RIFLEGUN_WEAPON:

		                (p.kickback_pic)++;
		                p.horiz++;
		                p.kickback++;

		                if( p.kickback_pic <= 12 )
		                {
		                    if( ((p.kickback_pic)%3) == 0 )
		                    {
		                        p.ammo_amount[RIFLEGUN_WEAPON]--;

		                        if( (p.kickback_pic%3) == 0 )
		                        {
		                            j = spawn(pi,SHELL);

		                            sprite[j].ang += 1024;
		                            sprite[j].ang &= 2047;
		                            sprite[j].xvel += 32;
		                            sprite[j].z += (3<<8);
		                            ssp((short)j,CLIPMASK0);
		                        }

		                        spritesound(CHAINGUN_FIRE,pi);
		                        shoot(pi,RIFLE);
		                        lastvisinc = totalclock+32;
		                        if(snum == screenpeek)
			                    	gVisibility = 0;
		                        p.field_290 = 0x2000;
			                	sub_64EF0(snum);
			                	if ( psectlotag != 857 )
			                    {
			                		p.posxv -= 16 * sintable[((int)p.ang+512)&2047];
					                p.posyv -= 16 * sintable[(int)p.ang&2047];
			                    }

		                        checkavailweapon(p);

		                        if( ( sb_snum&(1<<2) ) == 0 )
		                        {
		                            p.kickback_pic = 0;
		                            break;
		                        }
		                    }
		                }
		                else if((p.kickback_pic) > 10)
		                {
		                    if( (sb_snum&(1<<2)) != 0 ) p.kickback_pic = 1;
		                    else p.kickback_pic = 0;
		                }

		                break;

			        case THROWSAW_WEAPON:
		            case BUZSAW_WEAPON:

		                if(p.curr_weapon == BUZSAW_WEAPON)
		                {
		                    if((p.kickback_pic) > 3)
		                    {
		                        p.kickback_pic = 0;
		                        shoot(pi,BUZSAW);
		                        p.field_290 = 1024;
			                	sub_64EF0(snum);
		                        checkavailweapon(p);
		                    }
		                    else (p.kickback_pic)++;
		                }
		                else
		                {
		                    if( (p.kickback_pic) == 1)
		                    {
		                        p.ammo_amount[THROWSAW_WEAPON]--;
		                        shoot(pi,CIRCLESAW);
		                        checkavailweapon(p);
		                    }


		                    (p.kickback_pic)++;
		                    if( (p.kickback_pic) > 20)
		                    	p.kickback_pic = 0;
		                }
		                break;

		            case TIT_WEAPON:
		            	(p.kickback_pic)++;
		            	if( (p.kickback_pic) == 2 || (p.kickback_pic) == 4 )
		            	{
		            		if(snum == screenpeek)
		                    	gVisibility = 0;
		            		lastvisinc = totalclock+32;
		            		spritesound(CHAINGUN_FIRE,pi);
		            		shoot(pi,SHOTSPARK1);
		            		p.field_290 = 0x4000;
		            		sub_64EF0(snum);
		            		p.ammo_amount[TIT_WEAPON]--;
		            		checkavailweapon(p);
		            	}
		            	if ( p.kickback_pic == 2 )
		            		p.ang += 16;
		            	if ( p.kickback_pic == 4 )
		            		p.ang -= 16;
		            	if ( p.kickback_pic > 4 )
		            		p.kickback_pic = 1;
		            	if( (sb_snum&(1<<2)) == 0)
		            		p.kickback_pic = 0;
		            	break;

		            case ALIENBLASTER_WEAPON:
		            	(p.kickback_pic)++;
		            	if ( p.kickback_pic >= 7 && p.kickback_pic <= 11 )
		            		shoot(pi, FIRELASER);
		            	if ( p.kickback_pic == 5 )
		                {
		            		spritesound(10, pi);
		            		p.field_290 = 2048;
		            		sub_64EF0(snum);
		                }
		            	else if ( p.kickback_pic == 9 )
		            	{
		            		p.ammo_amount[ALIENBLASTER_WEAPON]--;
		            		if(snum == screenpeek)
		                    	gVisibility = 0;
		            		lastvisinc = totalclock+32;
		            		checkavailweapon(p);
		            	}
		            	else if ( p.kickback_pic == 12 )
		                {
		            		p.posxv -= 16 * sintable[((int)p.ang+512)&2047];
			                p.posyv -= 16 * sintable[(int)p.ang&2047];
			                p.kickback += 20;
		            		p.horiz += 20;
		                }
		                if ( p.kickback_pic > 20 )
		                	p.kickback_pic = 0;
		                break;
		            case POWDERKEG_WEAPON:
		            case BOWLING_WEAPON:
		            	if ( p.curr_weapon == POWDERKEG_WEAPON )
		            	{
		            		if( (p.kickback_pic) == 3 )
		            		{
		            			p.gotweapon[POWDERKEG_WEAPON] = false;
		            			p.ammo_amount[POWDERKEG_WEAPON]--;

		            			if(p.on_ground && (sb_snum&2) != 0 )
		            			{
		            				k = 15;
		 	                        i = (int)((p.horiz+p.horizoff-100)*20);
		            			}
		            			else
		            			{
		            				k = 32;
		            				i = (int)(-512-((p.horiz+p.horizoff-100)*20));
		            			}

		            			j = EGS(p.cursectnum,
		     	                        p.posx+(sintable[((int)p.ang+512)&2047]>>6),
		     	                        p.posy+(sintable[(int)p.ang&2047]>>6),
		     	                        p.posz,27,-16,9,9,
		     	                       (int)p.ang,2 * k,i,pi,(short)1);
		            		}
		            		p.kickback_pic++;
		            		if((p.kickback_pic) > 20)
		 	                {
		            			 p.kickback_pic = 0;
		            			 checkavailweapon(p);
		 	                }
		            	}
		            	else
		            	{
		            		if ( p.kickback_pic == 30 )
		            		{
		            			p.ammo_amount[12]--;
		            			spritesound(354, pi);
		            			shoot(pi, BOWLINGBALL);
		            			p.field_290 = 1024;
		                      	sub_64EF0(snum);
		                    }
		                    if ( p.kickback_pic < 30 )
		                    {
		                    	p.posxv += 16 * sintable[((int)p.ang+512)&2047];
				                p.posyv += 16 * sintable[(int)p.ang&2047];
		                    }

		                    p.kickback_pic++;
		                    if ( p.kickback_pic > 40 )
		                    {
		                    	p.kickback_pic = 0;
		                    	p.gotweapon[12] = false;
		                    	checkavailweapon(p);
		                    }
		            	}
		            	break;
		            case KNEE_WEAPON:
		                (p.kickback_pic)++;

		                if( (p.kickback_pic) == 3)
		                	spritesound(426, pi);
		                else if( (p.kickback_pic) == 12)
		                {
		                	shoot(pi, NEWCROWBAR);
		                    p.field_290 = 1024;
		                    sub_64EF0(snum);
		                }
		                else if( (p.kickback_pic) == 16)
		                	p.kickback_pic = 0;

		                if(p.wantweaponfire >= 0)
		                    checkavailweapon(p);
		                break;

		            case RATE_WEAPON:
		                (p.kickback_pic)++;

		                if( (p.kickback_pic) == 3)
		                	spritesound(252, pi);
		                else if( (p.kickback_pic) == 8)
		                {
		                	shoot(pi, 3510);
		                    p.field_290 = 1024;
		                    sub_64EF0(snum);
		                }
		                else if( (p.kickback_pic) == 16)
		                	p.kickback_pic = 0;

		                if(p.wantweaponfire >= 0)
		                    checkavailweapon(p);
		                break;
		            case CROSSBOW_WEAPON:
		            case CHICKENBOW_WEAPON:
		            	(p.kickback_pic)++;
		            	switch(p.kickback_pic)
		            	{
		            	case 4:
		            		if(p.curr_weapon == CROSSBOW_WEAPON) {
			                    p.ammo_amount[CROSSBOW_WEAPON]--;
			                    if ( p.ammo_amount[DYNAMITE_WEAPON] != 0 )
			                    	p.ammo_amount[DYNAMITE_WEAPON]--;
			                    shoot(pi, CROSSBOW);
		            		} else {
		            			p.ammo_amount[CHICKENBOW_WEAPON]--;
		            			shoot(pi, CHIKENCROSSBOW);
		            		}

		                    lastvisinc = totalclock + 32;
		                    if(snum == screenpeek)
		                    	gVisibility = 0;

		                    p.field_290 = 0x8000;
		                    sub_64EF0(snum);
		                    checkavailweapon(p);
		            		break;
		            	case 16:
		            		 spritesound(450, pi);
		            		break;
		            	case 34:
		            		p.kickback_pic = 0;
		            		break;
		            	}
		            	break;
		            case MOTO_WEAPON:
		            	(p.kickback_pic)++;
		            	if( (p.kickback_pic) == 2 || (p.kickback_pic) == 4 )
		            	{
		            		if(snum == screenpeek)
		                    	gVisibility = 0;
		            		lastvisinc = totalclock+32;
		            		spritesound(CHAINGUN_FIRE,pi);
		            		shoot(pi,RIFLE);
		            		p.field_290 = 0x4000;
		            		sub_64EF0(snum);
		            		p.ammo_amount[MOTO_WEAPON]--;
		            		if(p.ammo_amount[MOTO_WEAPON] > 0)
		            			checkavailweapon(p);
		            		else {
		            			p.kickback_pic = 0;
		            			break;
		            		}
		            	}
		            	if ( p.kickback_pic == 2 )
		            		p.ang += 4;
		            	if ( p.kickback_pic == 4 )
		            		p.ang -= 4;
		            	if ( p.kickback_pic > 4 )
		            		p.kickback_pic = 1;
		            	if( (sb_snum&(1<<2)) == 0)
		            		p.kickback_pic = 0;

		            	break;
		            case BOAT_WEAPON:
		            	if( (p.kickback_pic) == 3 )
	            		{
		            		p.CarSpeed -= 20;
		            		shoot(pi,1790);
		            		p.ammo_amount[BOAT_WEAPON]--;
	            		}
		            	p.kickback_pic++;
	            		if((p.kickback_pic) > 20)
	 	                {
	            			 p.kickback_pic = 0;
	            			 checkavailweapon(p);
	 	                }

	            		if( p.ammo_amount[BOAT_WEAPON] <= 0 )
	            			p.kickback_pic = 0;
	                    else checkavailweapon(p);
		            	break;
		        }
		    }
	}
}
