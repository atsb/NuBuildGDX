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

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Gameutils.FindDistance2D;
import static ru.m210projects.Duke3D.Gameutils.rnd;
import static ru.m210projects.Duke3D.Gameutils.sgn;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Screen.myos;
import static ru.m210projects.Duke3D.Screen.myospal;
import static ru.m210projects.Duke3D.Sector.checkhitceiling;
import static ru.m210projects.Duke3D.Sector.checkhitsprite;
import static ru.m210projects.Duke3D.Sector.checkhitswitch;
import static ru.m210projects.Duke3D.Sector.checkhitwall;
import static ru.m210projects.Duke3D.Sector.dist;
import static ru.m210projects.Duke3D.Sector.findplayer;
import static ru.m210projects.Duke3D.Sector.isadoorwall;
import static ru.m210projects.Duke3D.Sector.ldist;
import static ru.m210projects.Duke3D.Sector.player_dist;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Sounds.spritesound;
import static ru.m210projects.Duke3D.Sounds.xyzsound;
import static ru.m210projects.Duke3D.Spawn.EGS;
import static ru.m210projects.Duke3D.Spawn.spawn;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.View.*;

import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.Screen.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Duke3D.Menus.InterfaceMenu;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class Weapons {

    private static final FireProj[] fire = new FireProj[MAXSPRITES];

    private static class FireProj {
        public int x, y, z;
        public short xv, yv, zv;

        public void set(SPRITE spr) {
            this.x = spr.x;
            this.y = spr.y;
            this.z = spr.z;
            this.xv = spr.xvel;
            this.yv = spr.yvel;
            this.zv = spr.zvel;
        }
    }

    public static final short[] aimstats = {10, 13, 1, 2};

    public static int aim(SPRITE s, int aang) {
        if (s.picnum == APLAYER && ps[s.yvel].auto_aim == 0)
            return -1;

        int i, j;
        int xv, yv;
        int a = s.ang;
        boolean cans;

        j = -1;

        boolean gotshrinker = s.picnum == APLAYER && ps[s.yvel].curr_weapon == SHRINKER_WEAPON;
        boolean gotfreezer = s.picnum == APLAYER && ps[s.yvel].curr_weapon == FREEZE_WEAPON;

        int smax = 0x7fffffff;

        int dx1 = sintable[(a + 512 - aang) & 2047];
        int dy1 = sintable[(a - aang) & 2047];
        int dx2 = sintable[(a + 512 + aang) & 2047];
        int dy2 = sintable[(a + aang) & 2047];

        int dx3 = sintable[(a + 512) & 2047];
        int dy3 = sintable[a & 2047];

        for (int k = 0; k < 4; k++) {
            if (j >= 0)
                break;
            for (i = headspritestat[aimstats[k]]; i >= 0; i = nextspritestat[i])
                if (sprite[i].xrepeat > 0 && sprite[i].extra >= 0 && (sprite[i].cstat & (257 + 32768)) == 257)
                    if (badguy(sprite[i]) || k < 2) {
                        if (badguy(sprite[i]) || sprite[i].picnum == APLAYER || sprite[i].picnum == SHARK) {
                            if (sprite[i].picnum == APLAYER && ud.coop == 1 && s.picnum == APLAYER && s != sprite[i])
                                continue;

                            if (gotshrinker && sprite[i].xrepeat < 30) {
                                switch (sprite[i].picnum) {
                                    case SHARK:
                                        if (sprite[i].xrepeat < 20)
                                            continue;
                                        continue;
                                    case GREENSLIME:
                                    case GREENSLIME + 1:
                                    case GREENSLIME + 2:
                                    case GREENSLIME + 3:
                                    case GREENSLIME + 4:
                                    case GREENSLIME + 5:
                                    case GREENSLIME + 6:
                                    case GREENSLIME + 7:
                                        break;
                                    default:
                                        continue;
                                }
                            }
                            if (gotfreezer && sprite[i].pal == 1)
                                continue;
                        }

                        xv = (sprite[i].x - s.x);
                        yv = (sprite[i].y - s.y);

                        if ((dy1 * xv) <= (dx1 * yv))
                            if ((dy2 * xv) >= (dx2 * yv)) {
                                int sdist = mulscale(dx3, xv, 14) + mulscale(dy3, yv, 14);
                                if (sdist > 512 && sdist < smax) {
                                    if (s.picnum == APLAYER)
                                        a = ((klabs(scale(sprite[i].z - s.z, 10, sdist)
                                                - (int) (ps[s.yvel].horiz + ps[s.yvel].horizoff - 100)) < 100) ? 1 : 0);
                                    else
                                        a = 1;

                                    if (sprite[i].picnum == ORGANTIC || sprite[i].picnum == ROTATEGUN)
                                        cans = engine.cansee(sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].sectnum,
                                                s.x, s.y, s.z - (32 << 8), s.sectnum);
                                    else
                                        cans = engine.cansee(sprite[i].x, sprite[i].y, sprite[i].z - (32 << 8),
                                                sprite[i].sectnum, s.x, s.y, s.z - (32 << 8), s.sectnum);

                                    if (a != 0 && cans) {
                                        smax = sdist;
                                        j = i;
                                    }
                                }
                            }
                    }
        }

        return j;
    }

    public static void tracers(int x1, int y1, int z1, int x2, int y2, int z2, int n) {
        int i, xv, yv, zv;
        short sect = -1;

        i = n + 1;
        xv = (x2 - x1) / i;
        yv = (y2 - y1) / i;
        zv = (z2 - z1) / i;

        if ((klabs(x1 - x2) + klabs(y1 - y2)) < 3084)
            return;

        for (i = n; i > 0; i--) {
            x1 += xv;
            y1 += yv;
            z1 += zv;
            sect = engine.updatesector(x1, y1, sect);
            if (sect >= 0) {
                if (sector[sect].lotag == 2) {
                    int va = engine.krand() & 2047;
                    int vy = 4 + (engine.krand() & 3);
                    int vx = 4 + (engine.krand() & 3);
                    EGS(sect, x1, y1, z1, WATERBUBBLE, -32, vx, vy, va, 0, 0, ps[0].i, (short) 5);
                } else
                    EGS(sect, x1, y1, z1, SMALLSMOKE, -32, 14, 14, 0, 0, 0, ps[0].i, (short) 5);
            }
        }
    }

    public static void shoot(int i, int atwith) {
        short sect, l, sa, p, j, k, scount;
        int sx, sy, sz, vel, zvel, x, oldzvel, dal;
        short sizx, sizy;
        short hitsect, hitsprite, hitwall;
        int hitx, hity, hitz;

        SPRITE s = sprite[i];
        sect = s.sectnum;
        zvel = 0;

        if (sect < 0 || sect >= MAXSECTORS)
            return;

        if (s.picnum == APLAYER) {
            p = s.yvel;

            sx = ps[p].posx;
            sy = ps[p].posy;
            sz = ps[p].posz + ps[p].pyoff + (4 << 8);
            sa = (short) ps[p].ang;

            ps[p].crack_time = 777;

        } else {
            p = -1;
            sa = s.ang;
            sx = s.x;
            sy = s.y;
            sz = s.z - ((s.yrepeat * engine.getTile(s.picnum).getHeight()) << 1) + (4 << 8);
            if (s.picnum != ROTATEGUN) {
                sz -= (7 << 8);
                if (badguy(s) && sprite[i].picnum != COMMANDER) {
                    sx += (sintable[(sa + 1024 + 96) & 2047] >> 7);
                    sy += (sintable[(sa + 512 + 96) & 2047] >> 7);
                }
            }
        }

        if (currentGame.getCON().type == 20) { // Twentieth Anniversary World Tour
            switch (atwith) {
                case FIREBALL:
                    if (s.extra >= 0)
                        s.shade = -96;

                    sz -= (4 << 7);
                    if (sprite[i].picnum != BOSS5)
                        vel = 840;
                    else {
                        vel = 968;
                        sz += 6144;
                    }

                    if (p < 0) {
                        sa += 16 - (engine.krand() & 31);
                        j = (short) findplayer(s);
                        zvel = (((ps[j].oposz - sz + (3 << 8))) * vel) / ldist(sprite[ps[j].i], s);
                    } else {
                        zvel = 98 * (100 - ps[p].horizoff - (int) ps[p].horiz);
                        sx += sintable[(sa + 860) & 0x7FF] / 448;
                        sy += sintable[(sa + 348) & 0x7FF] / 448;
                        sz += (3 << 8);
                    }

                    sizx = 18;
                    sizy = 18;
                    if (p >= 0) {
                        sizx = 7;
                        sizy = 7;
                    }

                    j = EGS(sect, sx, sy, sz, atwith, -127, sizx, sizy, sa, vel, zvel, i, (short) 4);
                    SPRITE spr = sprite[j];
                    spr.extra += (engine.krand() & 7);
                    if (sprite[i].picnum == BOSS5 || p >= 0) {
                        spr.xrepeat = 40;
                        spr.yrepeat = 40;
                    }
                    spr.yvel = p;
                    spr.cstat = 128;
                    spr.clipdist = 4;
                    return;
                case FLAMETHROWERFLAME:
                    if (s.extra >= 0)
                        s.shade = -96;
                    vel = 400;

                    k = -1;
                    if (p < 0) {
                        j = (short) findplayer(s);
                        x = player_dist;
                        sa = engine.getangle(ps[j].oposx - sx, ps[j].oposy - sy);

                        if (sprite[i].picnum == BOSS5) {
                            vel = 528;
                            sz += 6144;
                        } else if (sprite[i].picnum == BOSS3)
                            sz -= 8192;

                        l = (short) ldist(sprite[ps[j].i], s);
                        if (l != 0)
                            zvel = ((ps[j].oposz - sz) * vel) / l;

                        if (badguy(s) && (s.hitag & face_player_smart) != 0)
                            sa = (short) (s.ang + (engine.krand() & 31) - 16);

                    } else {
                        zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) * 81;
                        if (sprite[ps[p].i].xvel != 0)
                            vel = (int) ((((512 - (1024
                                    - klabs(klabs(engine.getangle(sx - ps[p].oposx, sy - ps[p].oposy) - sa) - 1024)))
                                    * 0.001953125f) * sprite[ps[p].i].xvel) + 400);
                    }
                    if (sector[s.sectnum].lotag == 2 && (engine.krand() % 5) == 0)
                        k = (short) spawn(i, WATERBUBBLE);

                    if (k == -1) {
                        k = (short) spawn(i, atwith);
                        sprite[k].xvel = (short) vel;
                        sprite[k].zvel = (short) zvel;
                    }

                    sprite[k].x = sx + sintable[(sa + 630) & 0x7FF] / 448;
                    sprite[k].y = sy + sintable[(sa + 112) & 0x7FF] / 448;
                    sprite[k].z = sz - 256;
                    sprite[k].sectnum = sect;
                    sprite[k].cstat = 0x80;
                    sprite[k].ang = sa;
                    sprite[k].xrepeat = 2;
                    sprite[k].yrepeat = 2;
                    sprite[k].clipdist = 40;
                    sprite[k].yvel = p;
                    sprite[k].owner = (short) i;

                    if (p == -1) {
                        if (sprite[i].picnum == BOSS5) {
                            sprite[k].x -= sintable[sa & 2047] / 56;
                            sprite[k].y -= sintable[(sa + 1024 + 512) & 2047] / 56;
                            sprite[k].xrepeat = 10;
                            sprite[k].yrepeat = 10;
                        }
                    }
                    return;
                case FIREFLY: // BOSS5 shot
                    k = (short) spawn(i, atwith);
                    sprite[k].sectnum = sect;
                    sprite[k].x = sx;
                    sprite[k].y = sy;
                    sprite[k].z = sz;
                    sprite[k].ang = sa;
                    sprite[k].xvel = 500;
                    sprite[k].zvel = 0;
                    return;
            }
        }

        switch (atwith) {
            case BLOODSPLAT1:
            case BLOODSPLAT2:
            case BLOODSPLAT3:
            case BLOODSPLAT4:

                if (p >= 0)
                    sa += 64 - (engine.krand() & 127);
                else
                    sa += 1024 + 64 - (engine.krand() & 127);
                zvel = 1024 - (engine.krand() & 2047);
            case KNEE:
                if (atwith == KNEE) {
                    if (p >= 0) {
                        zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) << 5;
                        sz += (6 << 8);
                        sa += 15;
                    } else {
                        j = ps[findplayer(s)].i;
                        x = player_dist;
                        zvel = ((sprite[j].z - sz) << 8) / (x + 1);
                        sa = engine.getangle(sprite[j].x - sx, sprite[j].y - sy);
                    }
                }

                engine.hitscan(sx, sy, sz, sect, sintable[(sa + 512) & 2047], sintable[sa & 2047], zvel << 6, pHitInfo,
                        CLIPMASK1);

                hitsect = pHitInfo.hitsect;
                hitsprite = pHitInfo.hitsprite;
                hitwall = pHitInfo.hitwall;
                hitx = pHitInfo.hitx;
                hity = pHitInfo.hity;
                hitz = pHitInfo.hitz;

                if (atwith == BLOODSPLAT1 || atwith == BLOODSPLAT2 || atwith == BLOODSPLAT3 || atwith == BLOODSPLAT4) {
                    if (FindDistance2D(sx - hitx, sy - hity) < 1024)
                        if (hitwall >= 0 && wall[hitwall].overpicnum != BIGFORCE)
                            if ((wall[hitwall].nextsector >= 0 && hitsect >= 0
                                    && sector[hitsect].lotag == 0
                                    && sector[wall[hitwall].nextsector].lotag == 0
                                    && (sector[hitsect].floorz - sector[wall[hitwall].nextsector].floorz) > (16 << 8))
                                    || (wall[hitwall].nextsector == -1 && sector[hitsect].lotag == 0))
                                if ((wall[hitwall].cstat & 16) == 0) {
                                    if (wall[hitwall].nextsector >= 0) {
                                        k = headspritesect[wall[hitwall].nextsector];
                                        while (k >= 0) {
                                            if (sprite[k].statnum == 3 && sprite[k].lotag == 13)
                                                return;
                                            k = nextspritesect[k];
                                        }
                                    }

                                    if (wall[hitwall].nextwall >= 0 && wall[wall[hitwall].nextwall].hitag != 0)
                                        return;

                                    if (wall[hitwall].hitag == 0) {
                                        k = (short) spawn(i, atwith);
                                        sprite[k].xvel = -12;
                                        sprite[k].ang = (short) (engine.getangle(
                                                wall[hitwall].x - wall[wall[hitwall].point2].x,
                                                wall[hitwall].y - wall[wall[hitwall].point2].y) + 512);
                                        sprite[k].x = hitx;
                                        sprite[k].y = hity;
                                        sprite[k].z = hitz;
                                        sprite[k].cstat |= (engine.krand() & 4);
                                        ssp(k, CLIPMASK0);
                                        engine.setsprite(k, sprite[k].x, sprite[k].y, sprite[k].z);
                                        if (sprite[i].picnum == OOZFILTER || sprite[i].picnum == NEWBEAST)
                                            sprite[k].pal = 6;
                                    }
                                }
                    return;
                }

                if (hitsect < 0)
                    break;

                if ((klabs(sx - hitx) + klabs(sy - hity)) < 1024) {
                    if (hitwall >= 0 || hitsprite >= 0) {
                        j = EGS(hitsect, hitx, hity, hitz, KNEE, -15, 0, 0, sa, 32, 0, i, (short) 4);
                        sprite[j].extra += (engine.krand() & 7);
                        if (p >= 0) {
                            k = (short) spawn(j, SMALLSMOKE);
                            sprite[k].z -= (8 << 8);
                            spritesound(KICK_HIT, j);
                        }

                        if (p >= 0 && ps[p].steroids_amount > 0 && ps[p].steroids_amount < 400)
                            sprite[j].extra += (currentGame.getCON().max_player_health >> 2);

                        if (hitsprite >= 0 && sprite[hitsprite].picnum != ACCESSSWITCH
                                && sprite[hitsprite].picnum != ACCESSSWITCH2) {
                            checkhitsprite(hitsprite, j);
                            if (p >= 0)
                                checkhitswitch(p, hitsprite, 1);
                        } else if (hitwall >= 0) {
                            if ((wall[hitwall].cstat & 2) != 0)
                                if (wall[hitwall].nextsector >= 0)
                                    if (hitz >= (sector[wall[hitwall].nextsector].floorz))
                                        hitwall = wall[hitwall].nextwall;

                            if (hitwall >= 0 && wall[hitwall].picnum != ACCESSSWITCH
                                    && wall[hitwall].picnum != ACCESSSWITCH2) {
                                checkhitwall(j, hitwall, hitx, hity, hitz, atwith);
                                if (p >= 0)
                                    checkhitswitch(p, hitwall, 0);
                            }
                        }
                    } else if (p >= 0 && zvel > 0 && sector[hitsect].lotag == 1) {
                        j = (short) spawn(ps[p].i, WATERSPLASH2);
                        sprite[j].x = hitx;
                        sprite[j].y = hity;
                        sprite[j].ang = (short) ps[p].ang; // Total tweek
                        sprite[j].xvel = 32;
                        ssp((short) i, CLIPMASK0);
                        sprite[j].xvel = 0;

                    }
                }

                break;

            case SHOTSPARK1:
            case SHOTGUN:
            case CHAINGUN:

                if (s.extra >= 0)
                    s.shade = -96;

                if (p >= 0) {
                    j = (short) aim(s, AUTO_AIM_ANGLE);
                    if (j >= 0) {
                        dal = ((sprite[j].xrepeat * engine.getTile(sprite[j].picnum).getHeight()) << 1) + (5 << 8);
                        switch (sprite[j].picnum) {
                            case GREENSLIME:
                            case GREENSLIME + 1:
                            case GREENSLIME + 2:
                            case GREENSLIME + 3:
                            case GREENSLIME + 4:
                            case GREENSLIME + 5:
                            case GREENSLIME + 6:
                            case GREENSLIME + 7:
                            case ROTATEGUN:
                                dal -= (8 << 8);
                                break;
                        }
                        zvel = ((sprite[j].z - sz - dal) << 8) / ldist(sprite[ps[p].i], sprite[j]);
                        sa = engine.getangle(sprite[j].x - sx, sprite[j].y - sy);
                    }

                    if (atwith == SHOTSPARK1) {
                        if (j == -1) {
                            sa += 16 - (engine.krand() & 31);
                            zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) << 5;
                            zvel += 128 - (engine.krand() & 255);
                        }
                    } else {
                        sa += 16 - (engine.krand() & 31);
                        if (j == -1)
                            zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) << 5;
                        zvel += 128 - (engine.krand() & 255);
                    }
                    sz -= (2 << 8);
                } else {
                    j = (short) findplayer(s);
                    x = player_dist;
                    sz -= (4 << 8);
                    zvel = ((ps[j].posz - sz) << 8) / (ldist(sprite[ps[j].i], s));
                    if (s.picnum != BOSS1) {
                        zvel += 128 - (engine.krand() & 255);
                        sa += 32 - (engine.krand() & 63);
                    } else {
                        zvel += 128 - (engine.krand() & 255);
                        sa = (short) (engine.getangle(ps[j].posx - sx, ps[j].posy - sy) + 64 - (engine.krand() & 127));
                    }
                }

                s.cstat &= ~257;
                engine.hitscan(sx, sy, sz, sect, sintable[(sa + 512) & 2047], sintable[sa & 2047], zvel << 6, pHitInfo,
                        CLIPMASK1);

                hitsect = pHitInfo.hitsect;
                hitsprite = pHitInfo.hitsprite;
                hitwall = pHitInfo.hitwall;
                hitx = pHitInfo.hitx;
                hity = pHitInfo.hity;
                hitz = pHitInfo.hitz;

                s.cstat |= 257;

                if (hitsect < 0)
                    return;

                if ((engine.krand() & 15) == 0 && sector[hitsect].lotag == 2)
                    tracers(hitx, hity, hitz, sx, sy, sz, 8 - (ud.multimode >> 1));

                if (p >= 0) {
                    k = EGS(hitsect, hitx, hity, hitz, SHOTSPARK1, -15, 10, 10, sa, 0, 0, i, (short) 4);
                    sprite[k].extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[atwith]];
                    sprite[k].extra += (engine.krand() % 6);

                    if (hitwall == -1 && hitsprite == -1) {
                        if (zvel < 0) {
                            if ((sector[hitsect].ceilingstat & 1) != 0) {
                                sprite[k].xrepeat = 0;
                                sprite[k].yrepeat = 0;
                                return;
                            } else
                                checkhitceiling(hitsect);
                        }
                        spawn(k, SMALLSMOKE);
                    }

                    if (hitsprite >= 0) {
                        checkhitsprite(hitsprite, k);
                        if (sprite[hitsprite].picnum == APLAYER && (ud.coop != 1 || ud.ffire == 1)) {
                            l = (short) spawn(k, JIBS6);
                            sprite[k].xrepeat = sprite[k].yrepeat = 0;
                            sprite[l].z += (4 << 8);
                            sprite[l].xvel = 16;
                            sprite[l].xrepeat = sprite[l].yrepeat = 24;
                            sprite[l].ang += 64 - (engine.krand() & 127);
                        } else
                            spawn(k, SMALLSMOKE);

                        if (p >= 0 && (sprite[hitsprite].picnum == DIPSWITCH || sprite[hitsprite].picnum == DIPSWITCH + 1
                                || sprite[hitsprite].picnum == DIPSWITCH2 || sprite[hitsprite].picnum == DIPSWITCH2 + 1
                                || sprite[hitsprite].picnum == DIPSWITCH3 || sprite[hitsprite].picnum == DIPSWITCH3 + 1
                                || sprite[hitsprite].picnum == HANDSWITCH || sprite[hitsprite].picnum == HANDSWITCH + 1)) {
                            checkhitswitch(p, hitsprite, 1);
                            return;
                        }
                    } else if (hitwall >= 0) {
                        spawn(k, SMALLSMOKE);

                        SKIPBULLETHOLE:
                        do {
                            if (isadoorwall(wall[hitwall].picnum))
                                break SKIPBULLETHOLE; // goto SKIPBULLETHOLE;
                            if (p >= 0 && (wall[hitwall].picnum == DIPSWITCH || wall[hitwall].picnum == DIPSWITCH + 1
                                    || wall[hitwall].picnum == DIPSWITCH2 || wall[hitwall].picnum == DIPSWITCH2 + 1
                                    || wall[hitwall].picnum == DIPSWITCH3 || wall[hitwall].picnum == DIPSWITCH3 + 1
                                    || wall[hitwall].picnum == HANDSWITCH || wall[hitwall].picnum == HANDSWITCH + 1)) {
                                checkhitswitch(p, hitwall, 0);
                                return;
                            }

                            if (wall[hitwall].hitag != 0
                                    || (wall[hitwall].nextwall >= 0 && wall[wall[hitwall].nextwall].hitag != 0))
                                break SKIPBULLETHOLE; // goto SKIPBULLETHOLE;

                            if (hitsect >= 0 && sector[hitsect].lotag == 0)
                                if (wall[hitwall].overpicnum != BIGFORCE)
                                    if ((wall[hitwall].nextsector >= 0 && sector[wall[hitwall].nextsector].lotag == 0)
                                            || (wall[hitwall].nextsector == -1 && sector[hitsect].lotag == 0))
                                        if ((wall[hitwall].cstat & 16) == 0) {
                                            if (wall[hitwall].nextsector >= 0) {
                                                l = headspritesect[wall[hitwall].nextsector];
                                                while (l >= 0) {
                                                    if (sprite[l].statnum == 3 && sprite[l].lotag == 13)
                                                        break SKIPBULLETHOLE; // goto SKIPBULLETHOLE;
                                                    l = nextspritesect[l];
                                                }
                                            }

                                            l = headspritestat[5];
                                            while (l >= 0) {
                                                if (sprite[l].picnum == BULLETHOLE)
                                                    if (dist(sprite[l], sprite[k]) < (12 + (engine.krand() & 7)))
                                                        break SKIPBULLETHOLE; // goto SKIPBULLETHOLE;
                                                l = nextspritestat[l];
                                            }
                                            l = (short) spawn(k, BULLETHOLE);
                                            sprite[l].xvel = -1;
                                            sprite[l].ang = (short) (engine.getangle(
                                                    wall[hitwall].x - wall[wall[hitwall].point2].x,
                                                    wall[hitwall].y - wall[wall[hitwall].point2].y) + 512);
                                            ssp(l, CLIPMASK0);
                                        }
                        } while (false);

                        if ((wall[hitwall].cstat & 2) != 0)
                            if (wall[hitwall].nextsector >= 0)
                                if (hitz >= (sector[wall[hitwall].nextsector].floorz))
                                    hitwall = wall[hitwall].nextwall;

                        checkhitwall(k, hitwall, hitx, hity, hitz, SHOTSPARK1);
                    }
                } else {
                    k = EGS(hitsect, hitx, hity, hitz, SHOTSPARK1, -15, 24, 24, sa, 0, 0, i, (short) 4);
                    sprite[k].extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[atwith]];

                    if (hitsprite >= 0) {
                        checkhitsprite(hitsprite, k);
                        if (sprite[hitsprite].picnum != APLAYER)
                            spawn(k, SMALLSMOKE);
                        else
                            sprite[k].xrepeat = sprite[k].yrepeat = 0;
                    } else if (hitwall >= 0)
                        checkhitwall(k, hitwall, hitx, hity, hitz, SHOTSPARK1);
                }

                if ((engine.krand() & 255) < 4)
                    xyzsound(PISTOL_RICOCHET, k, hitx, hity, hitz);

                return;

            case FIRELASER:
            case SPIT:
            case COOLEXPLOSION1:

                if (s.extra >= 0)
                    s.shade = -96;

                scount = 1;
                if (atwith == SPIT)
                    vel = 292;
                else {
                    if (atwith == COOLEXPLOSION1) {
                        if (s.picnum == BOSS2)
                            vel = 644;
                        else
                            vel = 348;
                    } else {
                        vel = 840;
                    }
                    sz -= (4 << 7);
                }

                if (p >= 0) {
                    j = (short) aim(s, AUTO_AIM_ANGLE);

                    if (j >= 0) {
                        dal = ((sprite[j].xrepeat * engine.getTile(sprite[j].picnum).getHeight()) << 1) - (12 << 8);
                        zvel = ((sprite[j].z - sz - dal) * vel) / ldist(sprite[ps[p].i], sprite[j]);
                        sa = engine.getangle(sprite[j].x - sx, sprite[j].y - sy);
                    } else
                        zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) * 98;
                } else {
                    j = (short) findplayer(s);
                    x = player_dist;
                    sa += 16 - (engine.krand() & 31);
                    zvel = (((ps[j].oposz - sz + (3 << 8))) * vel) / ldist(sprite[ps[j].i], s);
                }

                oldzvel = zvel;

                if (atwith == SPIT) {
                    sizx = 18;
                    sizy = 18;
                    sz -= (10 << 8);
                } else {
                    if (atwith == FIRELASER) {
                        if (p >= 0) {

                            sizx = 34;
                            sizy = 34;
                        } else {
                            sizx = 18;
                            sizy = 18;
                        }
                    } else {
                        sizx = 18;
                        sizy = 18;
                    }
                }

                if (p >= 0) {
                    sizx = 7;
                    sizy = 7;
                }

                while (scount > 0) {
                    j = EGS(sect, sx, sy, sz, atwith, -127, sizx, sizy, sa, vel, zvel, i, (short) 4);
                    sprite[j].extra += (engine.krand() & 7);

                    if (atwith == COOLEXPLOSION1) {
                        sprite[j].shade = 0;
                        if (sprite[i].picnum == BOSS2) {
                            l = sprite[j].xvel;
                            sprite[j].xvel = 1024;
                            ssp(j, CLIPMASK0);
                            sprite[j].xvel = l;
                            sprite[j].ang += 128 - (engine.krand() & 255);
                        }
                    }

                    sprite[j].cstat = 128;
                    sprite[j].clipdist = 4;

                    sa = (short) (s.ang + 32 - (engine.krand() & 63));
                    zvel = oldzvel + 512 - (engine.krand() & 1023);

                    scount--;
                }

                return;

            case FREEZEBLAST:
                sz += (3 << 8);
            case RPG:

                if (s.extra >= 0)
                    s.shade = -96;

                scount = 1;
                vel = 644;

                j = -1;

                if (p >= 0) {
                    j = (short) aim(s, 48);
                    if (j >= 0) {
                        dal = ((sprite[j].xrepeat * engine.getTile(sprite[j].picnum).getHeight()) << 1) + (8 << 8);
                        zvel = ((sprite[j].z - sz - dal) * vel) / ldist(sprite[ps[p].i], sprite[j]);
                        if (sprite[j].picnum != RECON)
                            sa = engine.getangle(sprite[j].x - sx, sprite[j].y - sy);
                    } else
                        zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) * 81;
                    if (atwith == RPG)
                        spritesound(RPG_SHOOT, i);

                } else {
                    j = (short) findplayer(s);
                    x = player_dist;
                    sa = engine.getangle(ps[j].oposx - sx, ps[j].oposy - sy);
                    if (sprite[i].picnum == BOSS3) {
                        int zoffs = 32 << 8;
                        if (currentGame.getCON().type == 20) // Twentieth Anniversary World Tour
                            zoffs = (int) ((sprite[i].yrepeat / 80.0f) * zoffs);

                        sz -= zoffs;
                    } else if (sprite[i].picnum == BOSS2) {
                        vel += 128;
                        int zoffs = 24 << 8;
                        if (currentGame.getCON().type == 20) // Twentieth Anniversary World Tour
                            zoffs = (int) ((sprite[i].yrepeat / 80.0f) * zoffs);

                        sz += zoffs;
                    }

                    l = (short) ldist(sprite[ps[j].i], s);
                    zvel = ((ps[j].oposz - sz) * vel) / l;

                    if (badguy(s) && (s.hitag & face_player_smart) != 0)
                        sa = (short) (s.ang + (engine.krand() & 31) - 16);
                }

                if (p >= 0 && j >= 0)
                    l = j;
                else
                    l = -1;

                j = EGS(sect, sx + (sintable[(348 + sa + 512) & 2047] / 448), sy + (sintable[(sa + 348) & 2047] / 448),
                        sz - (1 << 8), atwith, 0, 14, 14, sa, vel, zvel, i, (short) 4);

                sprite[j].extra += (engine.krand() & 7);
                if (atwith != FREEZEBLAST)
                    sprite[j].yvel = l;
                else {
                    sprite[j].yvel = (short) currentGame.getCON().numfreezebounces;
                    sprite[j].xrepeat >>= 1;
                    sprite[j].yrepeat >>= 1;
                    sprite[j].zvel -= (2 << 4);
                }

                if (p == -1) {
                    if (sprite[i].picnum == BOSS3) {
                        int xoffs = sintable[sa & 2047] >> 6;
                        int yoffs = sintable[(sa + 1024 + 512) & 2047] >> 6;
                        int aoffs = 4;
                        if ((engine.krand() & 1) != 0) {
                            xoffs = -xoffs;
                            yoffs = -yoffs;
                            aoffs = -8;
                        }

                        if (currentGame.getCON().type == 20) { // Twentieth Anniversary World Tour
                            float siz = sprite[i].yrepeat / 80.0f;
                            xoffs *= siz;
                            yoffs *= siz;
                            aoffs *= siz;
                        }

                        sprite[j].x += xoffs;
                        sprite[j].y += yoffs;
                        sprite[j].ang += aoffs;

                        sprite[j].xrepeat = 42;
                        sprite[j].yrepeat = 42;
                    } else if (sprite[i].picnum == BOSS2) {
                        int xoffs = sintable[sa & 2047] / 56;
                        int yoffs = sintable[(sa + 1024 + 512) & 2047] / 56;
                        int aoffs = 8 + (engine.krand() & 255) - 128;

                        if (currentGame.getCON().type == 20) { // Twentieth Anniversary World Tour
                            float siz = sprite[i].yrepeat / 80.0f;
                            xoffs *= siz;
                            yoffs *= siz;
                            aoffs *= siz;
                        }

                        sprite[j].x -= xoffs;
                        sprite[j].y -= yoffs;
                        sprite[j].ang -= aoffs;

                        sprite[j].xrepeat = 24;
                        sprite[j].yrepeat = 24;
                    } else if (atwith != FREEZEBLAST) {
                        sprite[j].xrepeat = 30;
                        sprite[j].yrepeat = 30;
                        sprite[j].extra >>= 2;
                    }
                } else if (ps[p].curr_weapon == DEVISTATOR_WEAPON) {
                    sprite[j].extra >>= 2;
                    sprite[j].ang += 16 - (engine.krand() & 31);
                    sprite[j].zvel += 256 - (engine.krand() & 511);

                    if ((ps[p].hbomb_hold_delay) != 0) {
                        sprite[j].x -= sintable[sa & 2047] / 644;
                        sprite[j].y -= sintable[(sa + 1024 + 512) & 2047] / 644;
                    } else {
                        sprite[j].x += sintable[sa & 2047] >> 8;
                        sprite[j].y += sintable[(sa + 1024 + 512) & 2047] >> 8;
                    }
                    sprite[j].xrepeat >>= 1;
                    sprite[j].yrepeat >>= 1;
                }

                sprite[j].cstat = 128;
                if (atwith == RPG)
                    sprite[j].clipdist = 4;
                else
                    sprite[j].clipdist = 40;

                break;

            case HANDHOLDINGLASER:

                if (p >= 0)
                    zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) * 32;
                else
                    zvel = 0;

                engine.hitscan(sx, sy, sz - ps[p].pyoff, sect, sintable[(sa + 512) & 2047], sintable[sa & 2047], zvel << 6,
                        pHitInfo, CLIPMASK1);

                hitsect = pHitInfo.hitsect;
                hitsprite = pHitInfo.hitsprite;
                hitwall = pHitInfo.hitwall;
                hitx = pHitInfo.hitx;
                hity = pHitInfo.hity;
                hitz = pHitInfo.hitz;

                j = 0;
                if (hitsprite >= 0)
                    break;

                if (hitwall >= 0 && hitsect >= 0)
                    if (((hitx - sx) * (hitx - sx) + (hity - sy) * (hity - sy)) < (290 * 290)) {
                        if (wall[hitwall].nextsector >= 0) {
                            if (sector[wall[hitwall].nextsector].lotag <= 2 && sector[hitsect].lotag <= 2)
                                j = 1;
                        } else if (sector[hitsect].lotag <= 2)
                            j = 1;
                    }

                if (j == 1) {
                    k = EGS(hitsect, hitx, hity, hitz, TRIPBOMB, -16, 4, 5, sa, 0, 0, i, (short) 6);

                    sprite[k].hitag = k;
                    spritesound(LASERTRIP_ONWALL, k);
                    sprite[k].xvel = -20;
                    ssp(k, CLIPMASK0);
                    sprite[k].cstat = 16;
                    hittype[k].temp_data[5] = sprite[k].ang = (short) (engine.getangle(
                            wall[hitwall].x - wall[wall[hitwall].point2].x, wall[hitwall].y - wall[wall[hitwall].point2].y)
                            - 512);

                    if (p >= 0)
                        ps[p].ammo_amount[TRIPBOMB_WEAPON]--;

                }
                return;

            case BOUNCEMINE:
            case MORTER:

                if (s.extra >= 0)
                    s.shade = -96;

                j = ps[findplayer(s)].i;
                x = ldist(sprite[j], s);

                zvel = -x >> 1;

                if (zvel < -4096)
                    zvel = -2048;
                vel = x >> 4;

                EGS(sect, sx + (sintable[(512 + sa + 512) & 2047] >> 8), sy + (sintable[(sa + 512) & 2047] >> 8),
                        sz + (6 << 8), atwith, -64, 32, 32, sa, vel, zvel, i, (short) 1);
                break;

            case GROWSPARK:

                if (p >= 0) {
                    j = (short) aim(s, AUTO_AIM_ANGLE);
                    if (j >= 0) {
                        dal = ((sprite[j].xrepeat * engine.getTile(sprite[j].picnum).getHeight()) << 1) + (5 << 8);
                        switch (sprite[j].picnum) {
                            case GREENSLIME:
                            case GREENSLIME + 1:
                            case GREENSLIME + 2:
                            case GREENSLIME + 3:
                            case GREENSLIME + 4:
                            case GREENSLIME + 5:
                            case GREENSLIME + 6:
                            case GREENSLIME + 7:
                            case ROTATEGUN:
                                dal -= (8 << 8);
                                break;
                        }
                        zvel = ((sprite[j].z - sz - dal) << 8) / (ldist(sprite[ps[p].i], sprite[j]));
                        sa = engine.getangle(sprite[j].x - sx, sprite[j].y - sy);
                    } else {
                        sa += 16 - (engine.krand() & 31);
                        zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) << 5;
                        zvel += 128 - (engine.krand() & 255);
                    }

                    sz -= (2 << 8);
                } else {
                    j = (short) findplayer(s);
                    x = player_dist;
                    sz -= (4 << 8);
                    zvel = ((ps[j].posz - sz) << 8) / (ldist(sprite[ps[j].i], s));
                    zvel += 128 - (engine.krand() & 255);
                    sa += 32 - (engine.krand() & 63);
                }

                k = 0;
                s.cstat &= ~257;
                engine.hitscan(sx, sy, sz, sect, sintable[(sa + 512) & 2047], sintable[sa & 2047], zvel << 6, pHitInfo,
                        CLIPMASK1);

                hitsect = pHitInfo.hitsect;
                hitsprite = pHitInfo.hitsprite;
                hitwall = pHitInfo.hitwall;
                hitx = pHitInfo.hitx;
                hity = pHitInfo.hity;
                hitz = pHitInfo.hitz;

                s.cstat |= 257;

                j = EGS(sect, hitx, hity, hitz, GROWSPARK, -16, 28, 28, sa, 0, 0, i, (short) 1);

                sprite[j].pal = 2;
                sprite[j].cstat |= 130;
                sprite[j].xrepeat = sprite[j].yrepeat = 1;

                if (hitwall == -1 && hitsprite == -1 && hitsect >= 0) {
                    if (zvel < 0 && (sector[hitsect].ceilingstat & 1) == 0)
                        checkhitceiling(hitsect);
                } else if (hitsprite >= 0)
                    checkhitsprite(hitsprite, j);
                else if (hitwall >= 0 && wall[hitwall].picnum != ACCESSSWITCH && wall[hitwall].picnum != ACCESSSWITCH2)
                    checkhitwall(j, hitwall, hitx, hity, hitz, atwith);

                break;
            case SHRINKER:
                if (s.extra >= 0)
                    s.shade = -96;
                if (p >= 0) {
                    j = (short) aim(s, AUTO_AIM_ANGLE);
                    if (j >= 0) {
                        dal = ((sprite[j].xrepeat * engine.getTile(sprite[j].picnum).getHeight()) << 1);
                        zvel = ((sprite[j].z - sz - dal - (4 << 8)) * 768) / (ldist(sprite[ps[p].i], sprite[j]));
                        sa = engine.getangle(sprite[j].x - sx, sprite[j].y - sy);
                    } else
                        zvel = (int) (100 - ps[p].horiz - ps[p].horizoff) * 98;
                } else if (s.statnum != 3) {
                    j = (short) findplayer(s);
                    x = player_dist;
                    l = (short) ldist(sprite[ps[j].i], s);
                    zvel = ((ps[j].oposz - sz) * 512) / l;
                } else
                    zvel = 0;

                j = EGS(sect, sx + (sintable[(512 + sa + 512) & 2047] >> 12), sy + (sintable[(sa + 512) & 2047] >> 12),
                        sz + (2 << 8), SHRINKSPARK, -16, 28, 28, sa, 768, zvel, i, (short) 4);

                sprite[j].cstat = 128;
                sprite[j].clipdist = 32;

                return;
        }
        return;
    }

    private static final short[] weapon_sprites = {KNEE, FIRSTGUNSPRITE, SHOTGUNSPRITE, CHAINGUNSPRITE, RPGSPRITE,
            HEAVYHBOMB, SHRINKERSPRITE, DEVISTATORSPRITE, TRIPBOMBSPRITE, FREEZESPRITE, HEAVYHBOMB, SHRINKERSPRITE,

            // Twentieth Anniversary World Tour
            FLAMETHROWERSPRITE};

    public static void checkweapons(PlayerStruct p) {
        short cw = p.curr_weapon;

        if (cw < 1 || cw >= MAX_WEAPONS)
            return;

        if (cw != 0) {
            if ((engine.krand() & 1) != 0)
                spawn(p.i, weapon_sprites[cw]);
            else
                switch (cw) {
                    case RPG_WEAPON:
                    case HANDBOMB_WEAPON:
                        spawn(p.i, EXPLOSION2);
                        break;
                }
        }
    }

    public static void addammo(int weapon, PlayerStruct p, int amount) {
        p.ammo_amount[weapon] += amount;

        if (p.ammo_amount[weapon] > currentGame.getCON().max_ammo_amount[weapon])
            p.ammo_amount[weapon] = currentGame.getCON().max_ammo_amount[weapon];
    }

    public static void addweapon(PlayerStruct p, int weapon) {
        if (!p.gotweapon[weapon]) {
            p.gotweapon[weapon] = true;
            if (currentGame.getCON().PLUTOPAK && weapon == SHRINKER_WEAPON)
                p.gotweapon[GROW_WEAPON] = true;
        }

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
        if (p.last_used_weapon != p.curr_weapon)
            p.last_used_weapon = (byte) p.curr_weapon;
        p.curr_weapon = (short) weapon;

        switch (weapon) {
            case KNEE_WEAPON:
            case TRIPBOMB_WEAPON:
            case HANDREMOTE_WEAPON:
            case HANDBOMB_WEAPON:
                break;
            case SHOTGUN_WEAPON:
                spritesound(SHOTGUN_COCK, p.i);
                break;
            case PISTOL_WEAPON:
                spritesound(INSERT_CLIP, p.i);
                break;
            default:
                spritesound(SELECT_WEAPON, p.i);
                break;
        }
    }

    public static void weaponprocess(int snum) {
        PlayerStruct p = ps[snum];
        int pi = p.i;
        SPRITE s = sprite[pi];
        boolean shrunk = (s.yrepeat < 8);
        int sb_snum = 0;
        if (p.cheat_phase <= 0)
            sb_snum = sync[snum].bits;

        int j, i, k;

        if (p.curr_weapon == SHRINKER_WEAPON || p.curr_weapon == GROW_WEAPON)
            p.random_club_frame += 64; // Glowing

        if (p.rapid_fire_hold == 1) {
            if ((sb_snum & (1 << 2)) != 0)
                return;
            p.rapid_fire_hold = 0;
        }

        if (shrunk || p.tipincs != 0 || p.access_incs != 0)
            sb_snum &= ~(1 << 2);
        else if (!shrunk && ((sb_snum & (1 << 2)) != 0) && (p.kickback_pic) == 0 && p.fist_incs == 0
                && p.last_weapon == -1 && (p.weapon_pos == 0 || p.holster_weapon == 1)) {

            p.crack_time = 777;

            if (p.holster_weapon == 1) {
                if (p.last_pissed_time <= (26 * 218) && p.weapon_pos == -9) {
                    p.holster_weapon = 0;
                    p.weapon_pos = 10;
                    FTA(74, p);
                }
            } else
                switch (p.curr_weapon) {
                    case HANDBOMB_WEAPON:
                        p.hbomb_hold_delay = 0;
                        if (p.ammo_amount[HANDBOMB_WEAPON] > 0)
                            (p.kickback_pic) = 1;
                        break;
                    case HANDREMOTE_WEAPON:
                        p.hbomb_hold_delay = 0;
                        (p.kickback_pic) = 1;
                        break;

                    case PISTOL_WEAPON:
                        if (p.ammo_amount[PISTOL_WEAPON] > 0) {
                            p.ammo_amount[PISTOL_WEAPON]--;
                            (p.kickback_pic) = 1;
                        }
                        break;

                    case CHAINGUN_WEAPON:
                        if (p.ammo_amount[CHAINGUN_WEAPON] > 0) // && p.random_club_frame == 0)
                            (p.kickback_pic) = 1;
                        break;

                    case SHOTGUN_WEAPON:
                        if (p.ammo_amount[SHOTGUN_WEAPON] > 0 && p.random_club_frame == 0)
                            (p.kickback_pic) = 1;
                        break;

                    case TRIPBOMB_WEAPON:
                        if (p.ammo_amount[TRIPBOMB_WEAPON] > 0) {
                            int sx, sy, sz;
                            short sect, hw, hitsp;

                            if (IsOriginalDemo()) {
                                engine.hitscan(p.posx, p.posy, p.posz, p.cursectnum, sintable[((int) p.ang + 512) & 2047],
                                        sintable[(int) p.ang & 2047], (100 - (int) p.horiz - p.horizoff) * 32, pHitInfo,
                                        CLIPMASK1);
                            } else
                                engine.hitscan(p.posx, p.posy, p.posz, p.cursectnum, (int) BCosAngle(BClampAngle(p.ang)),
                                        (int) BSinAngle(BClampAngle(p.ang)), (int) (100 - p.horiz - p.horizoff) * 32,
                                        pHitInfo, CLIPMASK1);

                            sect = pHitInfo.hitsect;
                            hw = pHitInfo.hitwall;
                            hitsp = pHitInfo.hitsprite;
                            sx = pHitInfo.hitx;
                            sy = pHitInfo.hity;
                            sz = pHitInfo.hitz;

                            if (sect < 0 || hitsp >= 0)
                                break;

                            if (hw >= 0 && sector[sect].lotag > 2)
                                break;

                            if (hw >= 0 && wall[hw].overpicnum >= 0)
                                if (wall[hw].overpicnum == BIGFORCE)
                                    break;

                            j = headspritesect[sect];
                            while (j >= 0) {
                                if (sprite[j].picnum == TRIPBOMB && klabs(sprite[j].z - sz) < (12 << 8)
                                        && ((sprite[j].x - sx) * (sprite[j].x - sx)
                                        + (sprite[j].y - sy) * (sprite[j].y - sy)) < (290 * 290))
                                    break;
                                j = nextspritesect[j];
                            }

                            if (j == -1 && hw >= 0 && (wall[hw].cstat & 16) == 0)
                                if ((wall[hw].nextsector >= 0 && sector[wall[hw].nextsector].lotag <= 2)
                                        || (wall[hw].nextsector == -1 && sector[sect].lotag <= 2))
                                    if (((sx - p.posx) * (sx - p.posx) + (sy - p.posy) * (sy - p.posy)) < (290 * 290)) {
                                        p.posz = p.oposz;
                                        p.poszv = 0;
                                        (p.kickback_pic) = 1;
                                    }
                        }
                        break;

                    case SHRINKER_WEAPON:
                    case GROW_WEAPON:
                        if (currentGame.getCON().PLUTOPAK && p.curr_weapon == GROW_WEAPON) {
                            if (p.ammo_amount[GROW_WEAPON] > 0) {
                                (p.kickback_pic) = 1;
                                spritesound(EXPANDERSHOOT, pi);
                            }
                        } else if (p.ammo_amount[SHRINKER_WEAPON] > 0) {
                            (p.kickback_pic) = 1;
                            spritesound(SHRINKER_FIRE, pi);
                        }
                        break;

                    case FREEZE_WEAPON:
                        if (p.ammo_amount[FREEZE_WEAPON] > 0) {
                            (p.kickback_pic) = 1;
                            spritesound(CAT_FIRE, pi);
                        }
                        break;
                    case DEVISTATOR_WEAPON:
                        if (p.ammo_amount[DEVISTATOR_WEAPON] > 0) {
                            (p.kickback_pic) = 1;
                            p.hbomb_hold_delay ^= 1;
                            spritesound(CAT_FIRE, pi);
                        }
                        break;

                    case RPG_WEAPON:
                        if (p.ammo_amount[RPG_WEAPON] > 0)
                            (p.kickback_pic) = 1;
                        break;

                    case FLAMETHROWER_WEAPON: // Twentieth Anniversary World Tour
                        if (p.ammo_amount[FLAMETHROWER_WEAPON] > 0) {
                            p.kickback_pic = 1;
                            if (sector[p.cursectnum].lotag != 2)
                                spritesound(FLAMETHROWER_INTRO, pi);
                        }
                        break;

                    case KNEE_WEAPON:
                        if (p.quick_kick == 0)
                            (p.kickback_pic) = 1;
                        break;
                }
        } else if (p.kickback_pic != 0) {
            switch (p.curr_weapon) {
                case HANDBOMB_WEAPON:

                    if ((p.kickback_pic) == 6 && (sb_snum & (1 << 2)) != 0) {
                        p.rapid_fire_hold = 1;
                        break;
                    }
                    (p.kickback_pic)++;
                    if ((p.kickback_pic) == 12) {
                        p.ammo_amount[HANDBOMB_WEAPON]--;

                        if (p.on_ground && (sb_snum & 2) != 0) {
                            k = 15;
                            i = (int) ((p.horiz + p.horizoff - 100) * 20);
                        } else {
                            k = 140;
                            i = -512 - ((int) (p.horiz + p.horizoff - 100) * 20);
                        }

                        if (IsOriginalDemo()) {
                            j = EGS(p.cursectnum, p.posx + (sintable[((int) p.ang + 512) & 2047] >> 6),
                                    p.posy + (sintable[(int) p.ang & 2047] >> 6), p.posz, HEAVYHBOMB, -16, 9, 9,
                                    (short) p.ang, (k + (p.hbomb_hold_delay << 5)), i, pi, (short) 1);
                        } else {
                            j = EGS(p.cursectnum, (int) (p.posx + (BCosAngle(BClampAngle(p.ang)) / 64.0f)),
                                    (int) (p.posy + (BSinAngle(BClampAngle(p.ang)) / 64.0f)), p.posz, HEAVYHBOMB, -16, 9, 9,
                                    (short) p.ang, (k + (p.hbomb_hold_delay << 5)), i, pi, (short) 1);
                        }

                        if (k == 15) {
                            sprite[j].yvel = 3;
                            sprite[j].z += (8 << 8);
                        }

                        k = hits(pi);
                        if (k < 512) {
                            sprite[j].ang += 1024;
                            sprite[j].zvel /= 3;
                            sprite[j].xvel /= 3;
                        }

                        p.hbomb_on = 1;

                    } else if ((p.kickback_pic) < 12 && (sb_snum & (1 << 2)) != 0)
                        p.hbomb_hold_delay++;
                    else if ((p.kickback_pic) > 19) {
                        (p.kickback_pic) = 0;
                        p.curr_weapon = HANDREMOTE_WEAPON;
                        p.last_weapon = -1;
                        p.weapon_pos = 10;
                    }

                    break;

                case HANDREMOTE_WEAPON:

                    (p.kickback_pic)++;

                    if ((p.kickback_pic) == 2) {
                        p.hbomb_on = 0;
                    }

                    if ((p.kickback_pic) == 10) {
                        (p.kickback_pic) = 0;
                        if (p.ammo_amount[HANDBOMB_WEAPON] > 0)
                            addweapon(p, HANDBOMB_WEAPON);
                        else
                            checkavailweapon(p);
                    }
                    break;

                case PISTOL_WEAPON:
                    if ((p.kickback_pic) == 1) {
                        shoot(pi, SHOTSPARK1);
                        spritesound(PISTOL_FIRE, pi);

                        lastvisinc = totalclock + 32;
                        if (snum == screenpeek)
                            gVisibility = 0;
                    } else if ((p.kickback_pic) == 2)
                        spawn(pi, SHELL);

                    (p.kickback_pic)++;

                    if ((p.kickback_pic) >= 5) {
                        if (p.ammo_amount[PISTOL_WEAPON] <= 0 || (p.ammo_amount[PISTOL_WEAPON] % 12) != 0) {
                            (p.kickback_pic) = 0;
                            checkavailweapon(p);
                        } else {
                            switch ((p.kickback_pic)) {
                                case 5:
                                    spritesound(EJECT_CLIP, pi);
                                    break;
                                case 8:
                                    spritesound(INSERT_CLIP, pi);
                                    break;
                            }
                        }
                    }

                    if ((p.kickback_pic) == 27) {
                        (p.kickback_pic) = 0;
                        checkavailweapon(p);
                    }

                    break;

                case SHOTGUN_WEAPON:

                    (p.kickback_pic)++;

                    if (p.kickback_pic == 4) {
                        shoot(pi, SHOTGUN);
                        shoot(pi, SHOTGUN);
                        shoot(pi, SHOTGUN);
                        shoot(pi, SHOTGUN);
                        shoot(pi, SHOTGUN);
                        shoot(pi, SHOTGUN);
                        shoot(pi, SHOTGUN);

                        p.ammo_amount[SHOTGUN_WEAPON]--;

                        spritesound(SHOTGUN_FIRE, pi);

                        lastvisinc = totalclock + 32;

                        if (snum == screenpeek)
                            gVisibility = 0;
                    }

                    switch (p.kickback_pic) {
                        case 13:
                            checkavailweapon(p);
                            break;
                        case 15:
                            spritesound(SHOTGUN_COCK, pi);
                            break;
                        case 17:
                        case 20:
                            p.kickback_pic++;
                            break;
                        case 24:
                            j = spawn(pi, SHOTGUNSHELL);
                            sprite[j].ang += 1024;
                            ssp((short) j, CLIPMASK0);
                            sprite[j].ang += 1024;
                            p.kickback_pic++;
                            break;
                        case 31:
                            p.kickback_pic = 0;
                            return;
                    }
                    break;

                case CHAINGUN_WEAPON:

                    (p.kickback_pic)++;

                    if (p.kickback_pic <= 12) {
                        if (((p.kickback_pic) % 3) == 0) {
                            p.ammo_amount[CHAINGUN_WEAPON]--;

                            if ((p.kickback_pic % 3) == 0) {
                                j = spawn(pi, SHELL);

                                sprite[j].ang += 1024;
                                sprite[j].ang &= 2047;
                                sprite[j].xvel += 32;
                                sprite[j].z += (3 << 8);
                                ssp((short) j, CLIPMASK0);
                            }

                            spritesound(CHAINGUN_FIRE, pi);
                            shoot(pi, CHAINGUN);
                            lastvisinc = totalclock + 32;
                            if (snum == screenpeek)
                                gVisibility = 0;
                            checkavailweapon(p);

                            if ((sb_snum & (1 << 2)) == 0) {
                                p.kickback_pic = 0;
                                break;
                            }
                        }
                    } else if ((p.kickback_pic) > 10) {
                        if ((sb_snum & (1 << 2)) != 0)
                            p.kickback_pic = 1;
                        else
                            p.kickback_pic = 0;
                    }

                    break;

                case SHRINKER_WEAPON:
                case GROW_WEAPON:

                    if (currentGame.getCON().PLUTOPAK && p.curr_weapon == GROW_WEAPON) {
                        if ((p.kickback_pic) > 3) {
                            p.kickback_pic = 0;
                            p.ammo_amount[GROW_WEAPON]--;
                            shoot(pi, GROWSPARK);

                            if (snum == screenpeek)
                                gVisibility = 0;
                            lastvisinc = totalclock + 32;
                            checkavailweapon(p);
                        } else
                            (p.kickback_pic)++;
                    } else {
                        if ((p.kickback_pic) > 10) {
                            (p.kickback_pic) = 0;

                            p.ammo_amount[SHRINKER_WEAPON]--;
                            shoot(pi, SHRINKER);

                            if (snum == screenpeek)
                                gVisibility = 0;
                            lastvisinc = totalclock + 32;
                            checkavailweapon(p);
                        } else
                            (p.kickback_pic)++;
                    }
                    break;

                case DEVISTATOR_WEAPON:
                    if (p.kickback_pic != 0) {
                        (p.kickback_pic)++;

                        if ((p.kickback_pic & 1) != 0) {
                            if (snum == screenpeek)
                                gVisibility = 0;
                            lastvisinc = totalclock + 32;
                            shoot(pi, RPG);
                            p.ammo_amount[DEVISTATOR_WEAPON]--;
                            checkavailweapon(p);
                        }
                        if ((p.kickback_pic) > 5)
                            (p.kickback_pic) = 0;
                    }
                    break;
                case FREEZE_WEAPON:

                    if ((p.kickback_pic) < 4) {
                        (p.kickback_pic)++;
                        if ((p.kickback_pic) == 3) {
                            p.ammo_amount[FREEZE_WEAPON]--;
                            if (snum == screenpeek)
                                gVisibility = 0;
                            lastvisinc = totalclock + 32;
                            shoot(pi, FREEZEBLAST);
                            checkavailweapon(p);
                        }
                        if (s.xrepeat < 32) {
                            p.kickback_pic = 0;
                            break;
                        }
                    } else {
                        if ((sb_snum & (1 << 2)) != 0) {
                            p.kickback_pic = 1;
                            spritesound(CAT_FIRE, pi);
                        } else
                            p.kickback_pic = 0;
                    }
                    break;
                case FLAMETHROWER_WEAPON:
                    if (currentGame.getCON().type != 20) // Twentieth Anniversary World Tour
                        break;
                    (p.kickback_pic)++;
                    if ((p.kickback_pic) == 2) {
                        if (sector[p.cursectnum].lotag != 2) {
                            p.ammo_amount[FLAMETHROWER_WEAPON]--;
                            if (snum == screenpeek)
                                gVisibility = 0;
                            shoot(pi, FIREBALL);
                        }
                        checkavailweapon(p);
                    } else if ((p.kickback_pic) == 16) {
                        if ((sb_snum & (1 << 2)) != 0) {
                            p.kickback_pic = 1;
                            spritesound(FLAMETHROWER_INTRO, pi);
                        } else
                            p.kickback_pic = 0;
                    }
                    break;

                case TRIPBOMB_WEAPON:
                    if (p.kickback_pic < 4) {
                        p.posz = p.oposz;
                        p.poszv = 0;
                        if ((p.kickback_pic) == 3)
                            shoot(pi, HANDHOLDINGLASER);
                    }
                    if ((p.kickback_pic) == 16) {
                        (p.kickback_pic) = 0;
                        checkavailweapon(p);
                        p.weapon_pos = -9;
                    } else
                        (p.kickback_pic)++;
                    break;
                case KNEE_WEAPON:
                    (p.kickback_pic)++;

                    if ((p.kickback_pic) == 7)
                        shoot(pi, KNEE);
                    else if ((p.kickback_pic) == 14) {
                        if ((sb_snum & (1 << 2)) != 0)
                            p.kickback_pic = (short) (1 + (engine.krand() & 3));
                        else
                            p.kickback_pic = 0;
                    }

                    if (p.wantweaponfire >= 0)
                        checkavailweapon(p);
                    break;

                case RPG_WEAPON:
                    (p.kickback_pic)++;
                    if ((p.kickback_pic) == 4) {
                        p.ammo_amount[RPG_WEAPON]--;
                        lastvisinc = totalclock + 32;
                        if (snum == screenpeek)
                            gVisibility = 0;
                        shoot(pi, RPG);
                        checkavailweapon(p);
                    } else if (p.kickback_pic == 20)
                        p.kickback_pic = 0;
                    break;
            }
        }
    }

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
                case KNEE:
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
                        q = EGS(s.sectnum, s.x + ((k * sintable[(s.ang + 512) & 2047]) >> 9),
                                s.y + ((k * sintable[s.ang & 2047]) >> 9),
                                s.z + ((k * ksgn(s.zvel)) * klabs(s.zvel / 12)), TONGUE, -40 + (k << 1), 8, 8, 0, 0,
                                0, i, (short) 5);
                        sprite[q].cstat = 128;
                        sprite[q].pal = 8;
                    }
                    q = EGS(s.sectnum, s.x + ((k * sintable[(s.ang + 512) & 2047]) >> 9),
                            s.y + ((k * sintable[s.ang & 2047]) >> 9),
                            s.z + ((k * ksgn(s.zvel)) * klabs(s.zvel / 12)), INNERJAW, -40, 32, 32, 0, 0, 0, i,
                            (short) 5);
                    sprite[q].cstat = 128;
                    if (hittype[i].temp_data[1] > 512 && hittype[i].temp_data[1] < (1024))
                        sprite[q].picnum = INNERJAW + 1;

                    continue;

                case FREEZEBLAST:
                    if (s.yvel < 1 || s.extra < 2 || (s.xvel | s.zvel) == 0) {
                        j = spawn(i, TRANSPORTERSTAR);
                        sprite[j].pal = 1;
                        sprite[j].xrepeat = 32;
                        sprite[j].yrepeat = 32;
                        engine.deletesprite(i);
                        continue;
                    }
                case SHRINKSPARK:
                case RPG:
                case FIRELASER:
                case SPIT:
                case COOLEXPLOSION1:
                case FIREBALL:
                    // Twentieth Anniversary World Tour
                    if (s.picnum == FIREBALL && currentGame.getCON().type != 20)
                        break;

                    if (s.picnum == COOLEXPLOSION1)
                        if (Sound[WIERDSHOT_FLY].num == 0)
                            spritesound(WIERDSHOT_FLY, i);

                    p = -1;

                    if (s.picnum == RPG && sector[s.sectnum].lotag == 2) {
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
                        case RPG:
                            if (hittype[i].picnum != BOSS2 && s.xrepeat >= 10 && sector[s.sectnum].lotag != 2) {
                                j = spawn(i, SMALLSMOKE);
                                sprite[j].z += (1 << 8);
                            }
                            break;
                        case FIREBALL:
                            if (sector[s.sectnum].lotag == 2) {
                                engine.deletesprite(i);
                                continue;
                            }

                            if (s.picnum == FIREBALL && sprite[s.owner].picnum != FIREBALL) {
                                if (hittype[i].temp_data[0] >= 1 && hittype[i].temp_data[0] < 6) {
                                    float siz = 1.0f - (hittype[i].temp_data[0] * 0.2f);
                                    int trail = hittype[i].temp_data[1];
                                    j = hittype[i].temp_data[1] = spawn(i, FIREBALL);

                                    SPRITE spr = sprite[j];
                                    spr.xvel = sprite[i].xvel;
                                    spr.yvel = sprite[i].yvel;
                                    spr.zvel = sprite[i].zvel;
                                    if (hittype[i].temp_data[0] > 1) {
                                        FireProj proj = fire[trail];
                                        if (proj != null) {
                                            spr.x = proj.x;
                                            spr.y = proj.y;
                                            spr.z = proj.z;
                                            spr.xvel = proj.xv;
                                            spr.yvel = proj.yv;
                                            spr.zvel = proj.zv;
                                        }
                                    }
                                    spr.yrepeat = spr.xrepeat = (short) (sprite[i].xrepeat * siz);
                                    spr.cstat = sprite[i].cstat;
                                    spr.extra = 0;

                                    if (fire[j] == null)
                                        fire[j] = new FireProj();
                                    fire[j].set(spr);
                                    engine.changespritestat((short) j, (short) 4);
                                }
                                hittype[i].temp_data[0]++;
                            }

                            if (s.zvel < 15000)
                                s.zvel += 200;
                            break;
                    }

                    j = movesprite(i, (k * (sintable[(s.ang + 512) & 2047])) >> 14, (k * (sintable[s.ang & 2047])) >> 14,
                            ll, qq);

                    if (s.picnum == RPG && s.yvel >= 0)
                        if (FindDistance2D(s.x - sprite[s.yvel].x, s.y - sprite[s.yvel].y) < 256)
                            j = 49152 | s.yvel;

                    if (s.sectnum < 0) {
                        engine.deletesprite(i);
                        continue;
                    }

                    if ((j & kHitTypeMask) != kHitSprite)
                        if (s.picnum != FREEZEBLAST) {
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
                        for (k = -3; k < 2; k++) {
                            x = EGS(s.sectnum, s.x + ((k * sintable[(s.ang + 512) & 2047]) >> 9),
                                    s.y + ((k * sintable[s.ang & 2047]) >> 9),
                                    s.z + ((k * ksgn(s.zvel)) * klabs(s.zvel / 24)), FIRELASER, -40 + (k << 2),
                                    s.xrepeat, s.yrepeat, 0, 0, 0, s.owner, (short) 5);

                            sprite[x].cstat = 128;
                            sprite[x].pal = s.pal;
                        }
                    } else if (s.picnum == SPIT)
                        if (s.zvel < 6144)
                            s.zvel += currentGame.getCON().gc - 112;

                    if (j != 0) {
                        if (s.picnum == COOLEXPLOSION1) {
                            if ((j & kHitTypeMask) == kHitSprite && sprite[j & kHitIndexMask].picnum != APLAYER)
                                continue;
                            s.xvel = 0;
                            s.zvel = 0;
                        }

                        if ((j & kHitTypeMask) == kHitSprite) {
                            j &= kHitIndexMask;

                            if (s.picnum == FREEZEBLAST && sprite[j].pal == 1)
                                if (badguy(sprite[j]) || sprite[j].picnum == APLAYER) {
                                    j = spawn(i, TRANSPORTERSTAR);
                                    sprite[j].pal = 1;
                                    sprite[j].xrepeat = 32;
                                    sprite[j].yrepeat = 32;

                                    engine.deletesprite(i);
                                    continue;
                                }

                            if (s.picnum != FIREBALL || s.picnum == FIREBALL && sprite[s.owner].picnum != FIREBALL)
                                checkhitsprite((short) j, i);

                            if (sprite[j].picnum == APLAYER) {
                                p = sprite[j].yvel;
                                if (ud.multimode >= 2 && s.picnum == FIREBALL && sprite[s.owner].picnum != FIREBALL
                                        && sprite[s.owner].picnum == APLAYER) {
                                    ps[p].numloogs = -1 - sprite[i].yvel;
//			                    sub_A2F7E0(p, v133, s, sprite[i].yvel);
                                }

                                spritesound(PISTOL_BODYHIT, j);

                                if (s.picnum == SPIT) {
                                    ps[p].horiz += 32;
                                    ps[p].return_to_center = 8;

                                    if (ps[p].loogcnt == 0) {
                                        if (Sound[DUKE_LONGTERM_PAIN].num < 1)
                                            spritesound(DUKE_LONGTERM_PAIN, ps[p].i);

                                        j = 3 + (engine.krand() & 3);
                                        ps[p].numloogs = j;
                                        ps[p].loogcnt = 24 * 4;
                                        for (x = 0; x < j; x++) {
                                            int lx = engine.krand() % xdim;
                                            int ly = engine.krand() % ydim;
                                            if (p == screenpeek) {
                                                loogiex[x] = lx;
                                                loogiey[x] = ly;
                                            }
                                        }
                                    }
                                }
                            }
                        } else if ((j & kHitTypeMask) == kHitWall) {
                            j &= kHitIndexMask;

                            if (s.picnum != FIREBALL && s.picnum != RPG && s.picnum != FREEZEBLAST && s.picnum != SPIT
                                    && (wall[j].overpicnum == MIRROR || wall[j].picnum == MIRROR)) {
                                k = engine.getangle(wall[wall[j].point2].x - wall[j].x, wall[wall[j].point2].y - wall[j].y);
                                s.ang = (short) (((k << 1) - s.ang) & 2047);
                                s.owner = i;
                                spawn(i, TRANSPORTERSTAR);
                                continue;
                            } else {
                                engine.setsprite(i, dax, day, daz);
                                checkhitwall(i, j, s.x, s.y, s.z, s.picnum);

                                if (s.picnum == FREEZEBLAST) {
                                    if (wall[j].overpicnum != MIRROR && wall[j].picnum != MIRROR) {
                                        s.extra >>= 1;
                                        s.yvel--;
                                    }

                                    k = engine.getangle(wall[wall[j].point2].x - wall[j].x,
                                            wall[wall[j].point2].y - wall[j].y);
                                    s.ang = (short) (((k << 1) - s.ang) & 2047);
                                    continue;
                                }
                            }
                        } else if ((j & kHitTypeMask) == kHitSector) {
                            engine.setsprite(i, dax, day, daz);

                            if (s.zvel < 0) {
                                if ((sector[s.sectnum].ceilingstat & 1) != 0)
                                    if (sector[s.sectnum].ceilingpal == 0) {
                                        engine.deletesprite(i);
                                        continue;
                                    }

                                checkhitceiling(s.sectnum);
                            } else if (s.picnum == FIREBALL && sprite[s.owner].picnum != FIREBALL) {
                                j = spawn(i, LAVAPOOL);
                                sprite[j].owner = sprite[i].owner;
                                sprite[j].yvel = sprite[i].yvel;
                                hittype[j].owner = sprite[i].owner;
                                engine.deletesprite(i);
                                continue;
                            }

                            if (s.picnum == FREEZEBLAST) {
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

                        if (s.picnum != SPIT) {
                            if (s.picnum == RPG) {
                                k = spawn(i, EXPLOSION2);
                                sprite[k].x = dax;
                                sprite[k].y = day;
                                sprite[k].z = daz;

                                if (s.xrepeat < 10) {
                                    sprite[k].xrepeat = 6;
                                    sprite[k].yrepeat = 6;
                                } else if ((j & kHitTypeMask) == kHitSector) {
                                    if (s.zvel > 0)
                                        spawn(i, EXPLOSION2BOT);
                                    else {
                                        sprite[k].cstat |= 8;
                                        sprite[k].z += (48 << 8);
                                    }
                                }
                            } else if (s.picnum == SHRINKSPARK) {
                                spawn(i, SHRINKEREXPLOSION);
                                spritesound(SHRINKER_HIT, i);
                                hitradius(i, currentGame.getCON().shrinkerblastradius, 0, 0, 0, 0);
                            } else if (s.picnum != COOLEXPLOSION1 && s.picnum != FREEZEBLAST && s.picnum != FIRELASER
                                    && s.picnum != FIREBALL) {
                                k = spawn(i, EXPLOSION2);
                                sprite[k].xrepeat = sprite[k].yrepeat = (short) (s.xrepeat >> 1);
                                if ((j & kHitTypeMask) == kHitSector) {
                                    if (s.zvel < 0) {
                                        sprite[k].cstat |= 8;
                                        sprite[k].z += (72 << 8);
                                    }
                                }
                            }
                            if (s.picnum == RPG) {
                                spritesound(RPG_EXPLODE, i);

                                if (s.xrepeat >= 10) {
                                    x = s.extra;
                                    hitradius(i, currentGame.getCON().rpgblastradius, x >> 2, x >> 1, x - (x >> 2), x);
                                } else {
                                    x = s.extra + (global_random & 3);
                                    hitradius(i, (currentGame.getCON().rpgblastradius >> 1), x >> 2, x >> 1, x - (x >> 2),
                                            x);
                                }
                            }
                            if (s.picnum == FIREBALL && sprite[s.owner].picnum != FIREBALL) {
                                j = spawn(i, EXPLOSION2);
                                sprite[j].xrepeat = sprite[j].yrepeat = (short) (s.xrepeat >> 1);
                            }
                        }
                        if (s.picnum != COOLEXPLOSION1) {
                            engine.deletesprite(i);
                            continue;
                        }
                    }
                    if (s.picnum == COOLEXPLOSION1) {
                        s.shade++;
                        if (s.shade >= 40) {
                            engine.deletesprite(i);
                            continue;
                        }
                    } else if (s.picnum == RPG && sector[s.sectnum].lotag == 2 && s.xrepeat >= 10 && rnd(140))
                        spawn(i, WATERBUBBLE);

                    continue;

                case SHOTSPARK1:
                    p = findplayer(s);
                    execute(currentGame.getCON(), i, p, player_dist);
                    continue;
            }
        }
    }

    public static boolean animatefist(int gs, int snum) {
        int looking_arc, fisti, fistpal;
        int fistzoom, fistz;

        fisti = ps[snum].fist_incs;
        if (fisti > 32)
            fisti = 32;
        if (fisti <= 0)
            return false;

        looking_arc = klabs(ps[snum].look_ang) / 9;

        fistzoom = 65536 - (sintable[(512 + (fisti << 6)) & 2047] << 2);
        if (fistzoom > 90612)
            fistzoom = 90612;
        if (fistzoom < 40920)
            fistzoom = 40290;
        fistz = 194 + (sintable[((6 + fisti) << 7) & 2047] >> 9);

        if (sprite[ps[snum].i].pal == 1)
            fistpal = 1;
        else
            fistpal = sector[ps[snum].cursectnum].floorpal;

        engine.rotatesprite((-fisti + 222 + (int) (sync[snum].avel / 16f)) << 16, (looking_arc + fistz) << 16, fistzoom,
                0, FIST, gs, fistpal, 2 | 8, 0, 0, xdim - 1, ydim - 1);

        return true;
    }

    public static final short[] knee_y = {0, -8, -16, -32, -64, -84, -108, -108, -108, -72, -32, -8};

    public static boolean animateknee(int gs, int snum) {

        int looking_arc, pal;

        if (ps[snum].knee_incs > 11 || ps[snum].knee_incs == 0 || sprite[ps[snum].i].extra <= 0)
            return false;

        looking_arc = knee_y[ps[snum].knee_incs] + klabs(ps[snum].look_ang) / 9;

        looking_arc -= (ps[snum].hard_landing << 3);

        if (sprite[ps[snum].i].pal == 1)
            pal = 1;
        else {
            pal = sector[ps[snum].cursectnum].floorpal;
            if (pal == 0)
                pal = ps[snum].palookup;
        }

        myospal(105 + (int) (sync[snum].avel / 16f) - (ps[snum].look_ang >> 1) + (knee_y[ps[snum].knee_incs] >> 2),
                looking_arc + 280 - (((int) ps[snum].horiz - ps[snum].horizoff) >> 4), KNEE, gs, 4, pal);

        return true;
    }

    public static final short[] knuckle_frames = {0, 1, 2, 2, 3, 3, 3, 2, 2, 1, 0};

    public static boolean animateknuckles(int gs, int snum) {

        short looking_arc, pal;

        if (ps[snum].knuckle_incs == 0 || sprite[ps[snum].i].extra <= 0)
            return false;

        looking_arc = (short) (klabs(ps[snum].look_ang) / 9);

        looking_arc -= (ps[snum].hard_landing << 3);

        if (sprite[ps[snum].i].pal == 1)
            pal = 1;
        else
            pal = sector[ps[snum].cursectnum].floorpal;

        myospal(160 + (int) (sync[snum].avel / 16f) - (ps[snum].look_ang >> 1),
                looking_arc + 180 - (((int) ps[snum].horiz - ps[snum].horizoff) >> 4),
                CRACKKNUCKLES + knuckle_frames[ps[snum].knuckle_incs >> 1], gs, 4, pal);

        return true;
    }

    public static final byte[] kb_frames = {0, 1, 2, 0, 0};
    public static final byte[] remote_frames = {0, 1, 1, 2, 1, 1, 0, 0, 0, 0, 0};
    public static final byte[] cycloidy = {0, 4, 12, 24, 12, 4, 0};
    public static final byte[] throw_frames = {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2};
    public static final byte[] cat_frames = {0, 0, 1, 1, 2, 2};

    public static short fistsign;

    public static void displayweapon(int snum) {
        int gun_pos, looking_arc, cw;
        int weapon_xoffset, i, j;
        int o, pal;
        int gs;

        PlayerStruct p = ps[snum];
        short kb = p.kickback_pic;

        o = 0;

        looking_arc = klabs(p.look_ang) / 9;

        gs = sprite[p.i].shade;
        if (gs > 24)
            gs = 24;

        if (p.newowner >= 0 || ud.camerasprite >= 0 || over_shoulder_on > 0
                || (sprite[p.i].pal != 1 && sprite[p.i].extra <= 0) || animatefist(gs, snum)
                || animateknuckles(gs, snum) || animatetip(gs, snum) || animateaccess(gs, snum))
            return;

        animateknee(gs, snum);

        gun_pos = 80 - (p.weapon_pos * p.weapon_pos);

        weapon_xoffset = (160) - 90;
        weapon_xoffset -= (sintable[((p.weapon_sway >> 1) + 512) & 2047] / (1024 + 512));
        weapon_xoffset -= 58 + p.weapon_ang;
        if (sprite[p.i].xrepeat < 32)
            gun_pos -= klabs(sintable[(p.weapon_sway << 2) & 2047] >> 9);
        else
            gun_pos -= klabs(sintable[(p.weapon_sway >> 1) & 2047] >> 10);

        gun_pos -= (p.hard_landing << 3);

        if (screensize > 2
                || (game.menu.gShowMenu && (game.menu.getCurrentMenu() instanceof InterfaceMenu) && ud.screen_size > 2))
            gun_pos += engine.getTile(BOTTOMSTATUSBAR).getHeight() / 2;

        if (p.last_weapon >= 0)
            cw = p.last_weapon;
        else
            cw = p.curr_weapon;

        j = 14 - p.quick_kick;
        if (j != 14) {
            if (sprite[p.i].pal == 1)
                pal = 1;
            else {
                pal = sector[p.cursectnum].floorpal;
                if (pal == 0)
                    pal = p.palookup;
            }

            if (j < 5 || j > 9)
                myospal(weapon_xoffset + 80 - (p.look_ang >> 1), looking_arc + 250 - gun_pos, KNEE, gs, o | 4, pal);
            else
                myospal(weapon_xoffset + 160 - 16 - (p.look_ang >> 1), looking_arc + 214 - gun_pos, KNEE + 1, gs, o | 4,
                        pal);
        }

        if (sprite[p.i].xrepeat < 40) {
            if (p.jetpack_on == 0) {
                i = sprite[p.i].xvel;
                looking_arc += 32 - (i >> 1);
                fistsign += i >> 1;
            }
            cw = weapon_xoffset;
            weapon_xoffset += sintable[(fistsign) & 2047] >> 10;
            myos(weapon_xoffset + 250 - (p.look_ang >> 1),
                    looking_arc + 258 - (klabs(sintable[(fistsign) & 2047] >> 8)), FIST, gs, o);
            weapon_xoffset = cw;
            weapon_xoffset -= sintable[(fistsign) & 2047] >> 10;
            myos(weapon_xoffset + 40 - (p.look_ang >> 1),
                    looking_arc + 200 + (klabs(sintable[(fistsign) & 2047] >> 8)), FIST, gs, o | 4);
        } else
            switch (cw) {
                case KNEE_WEAPON:
                    if (kb > 0) {
                        if (sprite[p.i].pal == 1)
                            pal = 1;
                        else {
                            pal = sector[p.cursectnum].floorpal;
                            if (pal == 0)
                                pal = p.palookup;
                        }

                        if ((kb) < 5 || (kb) > 9)
                            myospal(weapon_xoffset + 220 - (p.look_ang >> 1), looking_arc + 250 - gun_pos, KNEE, gs, o,
                                    pal);
                        else
                            myospal(weapon_xoffset + 160 - (p.look_ang >> 1), looking_arc + 214 - gun_pos, KNEE + 1, gs, o,
                                    pal);
                    }
                    break;

                case TRIPBOMB_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    weapon_xoffset += 8;
                    gun_pos -= 10;

                    if ((kb) > 6)
                        looking_arc += ((kb) << 3);
                    else if ((kb) < 4)
                        myospal(weapon_xoffset + 142 - (p.look_ang >> 1), looking_arc + 234 - gun_pos, HANDHOLDINGLASER + 3,
                                gs, o, pal);

                    myospal(weapon_xoffset + 130 - (p.look_ang >> 1), looking_arc + 249 - gun_pos,
                            HANDHOLDINGLASER + ((kb) >> 2), gs, o, pal);
                    myospal(weapon_xoffset + 152 - (p.look_ang >> 1), looking_arc + 249 - gun_pos,
                            HANDHOLDINGLASER + ((kb) >> 2), gs, o | 4, pal);

                    break;

                case RPG_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    weapon_xoffset -= sintable[(768 + ((kb) << 7)) & 2047] >> 11;
                    gun_pos += sintable[(768 + ((kb) << 7) & 2047)] >> 11;

                    if (kb > 0) {
                        if (kb < 8) {
                            myospal(weapon_xoffset + 164, (looking_arc << 1) + 176 - gun_pos, RPGGUN + ((kb) >> 1), gs, o,
                                    pal);
                        }
                    }

                    myospal(weapon_xoffset + 164, (looking_arc << 1) + 176 - gun_pos, RPGGUN, gs, o, pal);
                    break;

                case SHOTGUN_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    weapon_xoffset -= 8;

                    switch (kb) {
                        case 1:
                        case 2:
                            myospal(weapon_xoffset + 168 - (p.look_ang >> 1), looking_arc + 201 - gun_pos, SHOTGUN + 2, -128, o,
                                    pal);
                        case 0:
                        case 6:
                        case 7:
                        case 8:
                            myospal(weapon_xoffset + 146 - (p.look_ang >> 1), looking_arc + 202 - gun_pos, SHOTGUN, gs, o, pal);
                            break;
                        case 3:
                        case 4:
                        case 5:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                            if (kb > 1 && kb < 5) {
                                gun_pos -= 40;
                                weapon_xoffset += 20;

                                myospal(weapon_xoffset + 178 - (p.look_ang >> 1), looking_arc + 194 - gun_pos,
                                        SHOTGUN + 1 + ((kb - 1) >> 1), -128, o, pal);
                            }

                            myospal(weapon_xoffset + 158 - (p.look_ang >> 1), looking_arc + 220 - gun_pos, SHOTGUN + 3, gs, o,
                                    pal);

                            break;
                        case 13:
                        case 14:
                        case 15:
                            myospal(32 + weapon_xoffset + 166 - (p.look_ang >> 1), looking_arc + 210 - gun_pos, SHOTGUN + 4, gs,
                                    o, pal);
                            break;
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                            myospal(64 + weapon_xoffset + 170 - (p.look_ang >> 1), looking_arc + 196 - gun_pos, SHOTGUN + 5, gs,
                                    o, pal);
                            break;
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                            myospal(64 + weapon_xoffset + 176 - (p.look_ang >> 1), looking_arc + 196 - gun_pos, SHOTGUN + 6, gs,
                                    o, pal);
                            break;
                        case 24:
                        case 25:
                        case 26:
                        case 27:
                            myospal(64 + weapon_xoffset + 170 - (p.look_ang >> 1), looking_arc + 196 - gun_pos, SHOTGUN + 5, gs,
                                    o, pal);
                            break;
                        case 28:
                        case 29:
                        case 30:
                            myospal(32 + weapon_xoffset + 156 - (p.look_ang >> 1), looking_arc + 206 - gun_pos, SHOTGUN + 4, gs,
                                    o, pal);
                            break;
                    }
                    break;

                case CHAINGUN_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    if (kb > 0)
                        gun_pos -= sintable[(kb) << 7] >> 12;

                    if (kb > 0 && sprite[p.i].pal != 1)
                        weapon_xoffset += 1 - (engine.rand() & 3);

                    myospal(weapon_xoffset + 168 - (p.look_ang >> 1), looking_arc + 260 - gun_pos, CHAINGUN, gs, o, pal);
                    switch (kb) {
                        case 0:
                            myospal(weapon_xoffset + 178 - (p.look_ang >> 1), looking_arc + 234 - gun_pos, CHAINGUN + 1, gs, o,
                                    pal);
                            break;
                        default:
                            if (kb > 4 && kb < 12) {
                                i = 0;
                                if (sprite[p.i].pal != 1)
                                    i = engine.rand() & 7;
                                myospal(i + weapon_xoffset - 4 + 140 - (p.look_ang >> 1),
                                        i + looking_arc - ((kb) >> 1) + 208 - gun_pos, CHAINGUN + 5 + ((kb - 4) / 5), gs, o,
                                        pal);
                                if (sprite[p.i].pal != 1)
                                    i = engine.rand() & 7;
                                myospal(i + weapon_xoffset - 4 + 184 - (p.look_ang >> 1),
                                        i + looking_arc - ((kb) >> 1) + 208 - gun_pos, CHAINGUN + 5 + ((kb - 4) / 5), gs, o,
                                        pal);
                            }
                            if (kb < 8) {
                                i = engine.rand() & 7;
                                myospal(i + weapon_xoffset - 4 + 162 - (p.look_ang >> 1),
                                        i + looking_arc - ((kb) >> 1) + 208 - gun_pos, CHAINGUN + 5 + ((kb - 2) / 5), gs, o,
                                        pal);
                                myospal(weapon_xoffset + 178 - (p.look_ang >> 1), looking_arc + 234 - gun_pos,
                                        CHAINGUN + 1 + ((kb) >> 1), gs, o, pal);
                            } else
                                myospal(weapon_xoffset + 178 - (p.look_ang >> 1), looking_arc + 234 - gun_pos, CHAINGUN + 1, gs,
                                        o, pal);
                            break;
                    }
                    break;
                case PISTOL_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    if ((kb) < 5) {
                        short l = (short) (195 - 12 + weapon_xoffset);

                        if ((kb) == 2)
                            l -= 3;
                        myospal((l - (p.look_ang >> 1)), (looking_arc + 244 - gun_pos), FIRSTGUN + kb_frames[kb], gs, 2,
                                pal);
                    } else {
                        if ((kb) < 10)
                            myospal(194 - (p.look_ang >> 1), looking_arc + 230 - gun_pos, FIRSTGUN + 4, gs, o, pal);
                        else if ((kb) < 15) {
                            myospal(244 - ((kb) << 3) - (p.look_ang >> 1), looking_arc + 130 - gun_pos + ((kb) << 4),
                                    FIRSTGUN + 6, gs, o, pal);
                            myospal(224 - (p.look_ang >> 1), looking_arc + 220 - gun_pos, FIRSTGUN + 5, gs, o, pal);
                        } else if ((kb) < 20) {
                            myospal(124 + ((kb) << 1) - (p.look_ang >> 1), looking_arc + 430 - gun_pos - ((kb) << 3),
                                    FIRSTGUN + 6, gs, o, pal);
                            myospal(224 - (p.look_ang >> 1), looking_arc + 220 - gun_pos, FIRSTGUN + 5, gs, o, pal);
                        } else if ((kb) < 23) {
                            myospal(184 - (p.look_ang >> 1), looking_arc + 235 - gun_pos, FIRSTGUN + 8, gs, o, pal);
                            myospal(224 - (p.look_ang >> 1), looking_arc + 210 - gun_pos, FIRSTGUN + 5, gs, o, pal);
                        } else if ((kb) < 25) {
                            myospal(164 - (p.look_ang >> 1), looking_arc + 245 - gun_pos, FIRSTGUN + 8, gs, o, pal);
                            myospal(224 - (p.look_ang >> 1), looking_arc + 220 - gun_pos, FIRSTGUN + 5, gs, o, pal);
                        } else if ((kb) < 27)
                            myospal(194 - (p.look_ang >> 1), looking_arc + 235 - gun_pos, FIRSTGUN + 5, gs, o, pal);
                    }

                    break;
                case HANDBOMB_WEAPON: {
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    if (kb != 0) {

                        if ((kb) < 7)
                            gun_pos -= 10 * (kb); // D
                        else if ((kb) < 12)
                            gun_pos += 20 * ((kb) - 10); // U
                        else if ((kb) < 20)
                            gun_pos -= 9 * ((kb) - 14); // D

                        myospal(weapon_xoffset + 190 - (p.look_ang >> 1), looking_arc + 250 - gun_pos,
                                HANDTHROW + throw_frames[(kb)], gs, o, pal);
                    } else
                        myospal(weapon_xoffset + 190 - (p.look_ang >> 1), looking_arc + 260 - gun_pos, HANDTHROW, gs, o,
                                pal);
                }
                break;

                case HANDREMOTE_WEAPON: {
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    weapon_xoffset = -48;

                    if (kb != 0)
                        myospal(weapon_xoffset + 150 - (p.look_ang >> 1), looking_arc + 258 - gun_pos,
                                HANDREMOTE + remote_frames[(kb)], gs, o, pal);
                    else
                        myospal(weapon_xoffset + 150 - (p.look_ang >> 1), looking_arc + 258 - gun_pos, HANDREMOTE, gs, o,
                                pal);
                }
                break;
                case DEVISTATOR_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;

                    if (kb != 0) {

                        i = sgn((kb) >> 2);

                        if (p.hbomb_hold_delay != 0) {
                            myospal((cycloidy[kb] >> 1) + weapon_xoffset + 268 - (p.look_ang >> 1),
                                    cycloidy[kb] + looking_arc + 238 - gun_pos, DEVISTATOR + i, -32, o, pal);
                            myospal(weapon_xoffset + 30 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, DEVISTATOR, gs,
                                    o | 4, pal);
                        } else {
                            myospal(-(cycloidy[kb] >> 1) + weapon_xoffset + 30 - (p.look_ang >> 1),
                                    cycloidy[kb] + looking_arc + 240 - gun_pos, DEVISTATOR + i, -32, o | 4, pal);
                            myospal(weapon_xoffset + 268 - (p.look_ang >> 1), looking_arc + 238 - gun_pos, DEVISTATOR, gs,
                                    o, pal);
                        }
                    } else {
                        myospal(weapon_xoffset + 268 - (p.look_ang >> 1), looking_arc + 238 - gun_pos, DEVISTATOR, gs, o,
                                pal);
                        myospal(weapon_xoffset + 30 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, DEVISTATOR, gs, o | 4,
                                pal);
                    }
                    break;

                case FREEZE_WEAPON:
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else {
                        if (p.cursectnum < 0)
                            pal = 0;
                        else
                            pal = sector[p.cursectnum].floorpal;
                    }

                    if (kb != 0) {
                        if (sprite[p.i].pal != 1) {
                            weapon_xoffset += engine.rand() & 3;
                            looking_arc += engine.rand() & 3;
                        }
                        gun_pos -= 16;
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 261 - gun_pos, FREEZE + 2, -32, o,
                                pal);
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 235 - gun_pos,
                                FREEZE + 3 + cat_frames[kb % 6], -32, o, pal);
                    } else
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 261 - gun_pos, FREEZE, gs, o, pal);

                    break;
                case FLAMETHROWER_WEAPON: // Twentieth Anniversary World Tour XXX
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else {
                        if (p.cursectnum < 0)
                            pal = 0;
                        else
                            pal = sector[p.cursectnum].floorpal;
                    }

                    if (kb < 1 || sector[p.cursectnum].lotag == 2) {
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 261 - gun_pos, FLAMETHROWER, gs, o,
                                pal);
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 261 - gun_pos, FLAMETHROWERPILOT,
                                gs, o, pal);
                    } else {
                        if (sprite[p.i].pal != 1) {
                            weapon_xoffset += engine.rand() & 1;
                            looking_arc += engine.rand() & 1;
                        }
                        gun_pos -= 16;
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 261 - gun_pos, FLAMETHROWER + 1,
                                -32, o, pal);
                        myospal(weapon_xoffset + 210 - (p.look_ang >> 1), looking_arc + 235 - gun_pos,
                                FLAMETHROWER + 2 + cat_frames[kb % 6], -32, o, pal);
                    }
                    break;

                case SHRINKER_WEAPON:
                case GROW_WEAPON:
                    weapon_xoffset += 28;
                    looking_arc += 18;
                    if (sprite[p.i].pal == 1)
                        pal = 1;
                    else
                        pal = sector[p.cursectnum].floorpal;
                    if ((kb) == 0) {
                        if (cw == GROW_WEAPON) {
                            myospal(weapon_xoffset + 184 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, SHRINKER + 2,
                                    16 - (sintable[p.random_club_frame & 2047] >> 10), o, 2);

                            myospal(weapon_xoffset + 188 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, SHRINKER - 2, gs,
                                    o, pal);
                        } else {
                            myospal(weapon_xoffset + 184 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, SHRINKER + 2,
                                    16 - (sintable[p.random_club_frame & 2047] >> 10), o, 0);

                            myospal(weapon_xoffset + 188 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, SHRINKER, gs, o,
                                    pal);
                        }
                    } else {
                        if (sprite[p.i].pal != 1) {
                            weapon_xoffset += engine.rand() & 3;
                            gun_pos += (engine.rand() & 3);
                        }

                        if (cw == GROW_WEAPON) {
                            myospal(weapon_xoffset + 184 - (p.look_ang >> 1), looking_arc + 240 - gun_pos,
                                    SHRINKER + 3 + ((kb) & 3), -32, o, 2);

                            myospal(weapon_xoffset + 188 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, SHRINKER - 1, gs,
                                    o, pal);

                        } else {
                            myospal(weapon_xoffset + 184 - (p.look_ang >> 1), looking_arc + 240 - gun_pos,
                                    SHRINKER + 3 + ((kb) & 3), -32, o, 0);

                            myospal(weapon_xoffset + 188 - (p.look_ang >> 1), looking_arc + 240 - gun_pos, SHRINKER + 1, gs,
                                    o, pal);
                        }
                    }
                    break;
            }
    }
}
