package ru.m210projects.Tekwar;

import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.View.*;
import static ru.m210projects.Tekwar.Tekmap.*;
import static ru.m210projects.Tekwar.Tekprep.*;
import static ru.m210projects.Tekwar.Teksnd.*;
import static ru.m210projects.Tekwar.Tekspr.*;
import static ru.m210projects.Tekwar.Tekstat.*;
import static ru.m210projects.Tekwar.Tektag.*;
import static ru.m210projects.Tekwar.Tekchng.*;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Tekwar.Menus.MenuInterfaceSet;
import ru.m210projects.Tekwar.Types.Guntype;
import static ru.m210projects.Tekwar.Player.*;

public class Tekgun {
	public static boolean goreflag = true;

	public static final int NUMWEAPONS = 8;
	public static final short   FORCEPROJECTILESTAT   =   710;
	public static final short   DARTPROJECTILESTAT   =    712;
	public static final short   BOMBPROJECTILESTAT     =  714;
	public static final short   BOMBPROJECTILESTAT2   =   716;
	public static final short   ROCKETPROJECTILESTAT  =   718;
	public static final short   MATRIXPROJECTILESTAT  =   720;
	public static final short   MOVEBODYPARTSSTAT     =   900;
	public static final short VANISH                =   999;

	public static final int   CLASS_NULL            =   0;
	public static final int   CLASS_FCIAGENT        =   1;
	public static final int   CLASS_CIVILLIAN       =   2;
	public static final int   CLASS_SPIDERDROID     =   3;
	public static final int   CLASS_COP             =   4;
	public static final int   CLASS_MECHCOP         =   5;
	public static final int   CLASS_TEKBURNOUT      =   6;
	public static final int   CLASS_TEKGOON         =   7;
	public static final int   CLASS_ASSASSINDROID   =   8;
	public static final int   CLASS_SECURITYDROID   =   9;
	public static final int   CLASS_TEKBOSS         =   10;
	public static final int   CLASS_TEKLORD         =   11;

//	public static int[] oneshot = new int[MAXPLAYERS];

	public static Guntype guntype[] = {
		     //   pic         firepic    endfirepic rep /           \ pos  tics/frame
		     new Guntype(GUN07READY,GUN07FIRESTART,GUN07FIREEND,0,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*8),
		     new Guntype(GUN04READY,GUN04FIRESTART,GUN04FIREEND,1,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*4),
		     new Guntype(GUN01READY,GUN01FIRESTART,GUN01FIREEND,1,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*4),
		     new Guntype(GUN03READY,GUN03FIRESTART,GUN03FIREEND,1,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*2),
		     new Guntype(GUN02READY,GUN02FIRESTART,GUN02FIREEND,1,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*2),
		     new Guntype(GUN08READY,GUN08FIRESTART,GUN08FIREEND,1,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*2),
		     new Guntype(GUN05READY,GUN05FIRESTART,GUN05FIREEND,0,new byte[]{0,1,1,0,0,0,0,0},2,TICSPERFRAME*8),
		     // matrix hand
		     new Guntype(      3980,          3981,        3984,1,new byte[]{0,1,0,0,0,0,0,0},2,TICSPERFRAME*3)
	};

	public static int[]  gunbobx={0,2,4,6,8,8,8,6,4,2};
	public static int[]  gunboby={0,2,4,6,8,10,8,6,4,2};
	public static int  gunbob,bobtics;

	public static void restockammo(int snum)
	{
	     gPlayer[snum].ammo[0]=MAXAMMO;
	     gPlayer[snum].ammo[1]=MAXAMMO>>1;
	     gPlayer[snum].ammo[2]=20;
	     gPlayer[snum].ammo[3]=0;
	     gPlayer[snum].ammo[4]=MAXAMMO;
	     gPlayer[snum].ammo[5]=0;
	     gPlayer[snum].ammo[6]=0;
	     gPlayer[snum].ammo[7]=0;
	}

	public static boolean validplayer(int snum)
	{
	     int j;
	     for( j=connecthead ; j >= 0 ; j=connectpoint2[j] ) {
	          if( j == snum ) {
	               return(true);
	          }
	     }
	     return(false);
	}

	public static int tekexplodebody(int i)
	{
	     int  j,k,r,ext;

	     ext=sprite[i].extra;
	     if( (validext(ext) == 0) || (!goreflag) ) {
	          return(0);
	     }
	     switch( spriteXT[ext].basepic ) {
	     case RUBWALKPIC:
	     case JAKEWALKPIC:
	     case COP1WALKPIC:
	     case ANTWALKPIC:
	     case SARAHWALKPIC:
	     case MAWALKPIC:
	     case DIWALKPIC:
	     case ERWALKPIC:
	     case SAMWALKPIC:
	     case FRGWALKPIC:
	     case SUNGWALKPIC:
	     case COWWALKPIC:
	     case COPBWALKPIC:
	     case NIKAWALKPIC:
	     case REBRWALKPIC:
	     case TRENWALKPIC:
	     case WINGWALKPIC:
	     case HALTWALKPIC:
	     case REDHWALKPIC:
	     case ORANWALKPIC:
	     case BLKSWALKPIC:
	     case SFROWALKPIC:
	     case SSALWALKPIC:
	     case SGOLWALKPIC:
	     case SWATWALKPIC:
	          break;
	     default:
	          return(0);
	     }

	     r=(krand_intercept(" GUN 787")%72)+8;
	     for (k=0 ; k < r ; k++) {
	          j=jsinsertsprite(sprite[i].sectnum,MOVEBODYPARTSSTAT);
	          sprite[j].x=sprite[i].x;
	          sprite[j].y=sprite[i].y;
	          sprite[j].z=sprite[i].z+(8<<8);
	          sprite[j].cstat=0;
	          sprite[j].shade=0;
	          sprite[j].pal=0;
	          sprite[j].xrepeat=24;
	          sprite[j].yrepeat=24;
	          sprite[j].ang=sprite[i].ang;
	          sprite[j].xvel=(short) ((krand_intercept(" GUN 799")&511)-256);
	          sprite[j].yvel=(short) ((krand_intercept(" GUN 800")&511)-256);
	          sprite[j].zvel=(short) -((krand_intercept(" GUN 801")&8191)+4096);
	          sprite[j].owner=sprite[i].owner;
	          sprite[j].clipdist=32;
	          sprite[j].lotag=360;
	          sprite[j].hitag=0;
	          switch (k) {
	          case 0:
	               sprite[j].picnum=GOREHEAD;
	               break;
	          case 1:
	          case 10:
	               sprite[j].picnum=GOREARM;
	               break;
	          case 5:
	          case 15:
	               sprite[j].picnum=GORELEG;
	               break;
	          default:
	               sprite[j].picnum=GOREBLOOD;
	               break;
	          }
	     }
	     playsound(S_GORE1+(krand_intercept(" GUN 823")%2), sprite[i].x,sprite[i].y, 0, ST_NOUPDATE);

	     return(1);
	}

	public static void tekchangestun(int snum,int deltastun)
	{
	     if( gPlayer[snum].godMode ) {
	          return;
	     }

	     switch (gDifficulty) {
	     case 1:
	          deltastun/=2;
	          break;
	     case 3:
	          deltastun*=2;
	          break;
	     }
	     stun[snum]+=deltastun;
	     if (stun[snum] < 0) {
	          stun[snum]=0;
	     }
	}

	public static void playerpainsound(int p)
	{
	     if( !validplayer(p) ) {
	    	 game.ThrowError("playerpainsnd: bad plr num", null);
	     }

	     if( krand_intercept(" GUN 341") < 1024 ) {
	          playsound(S_PAIN1+(krand_intercept(" GUN 342")&1),gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	     }
	}

	public static int tekhasweapon(int gun, short snum)
	{
	     int       hasit=0;

	     if(TEKDEMO && gun > 2)
	    	 return 0;

	     if( mission == 7 ) {
	          if( gun != 7 ) {
	               if( snum == screenpeek )
	                    notininventory=1;
	               return(0);
	          }
	          else return(1);
	     }
	     else {
	          if( gun == 7 ) {
	               if( snum == screenpeek ) {
	                    showmessage("ONLY IN MATRIX");
	               }
	               return(0);
	          }
	     }

	     hasit=(gPlayer[snum].weapons&flags32[gun+1]);
	     if( hasit != 0)
	          return(1);

	     notininventory=1;
	     return(0);
	}

	public static int tekgunrep(int gun)                // is "gun" an automatic weapon?
	{
	     return(guntype[gun].rep);
	}

	public static void tekfiregun(int gun,short p)  // this kicks off an animation sequence
	{
		 Guntype gunptr=guntype[gPlayer[p].lastgun];
	     int fseq=gPlayer[p].fireseq - (gunptr.firepic-gunptr.pic);
	     if((gun != gPlayer[p].lastgun && fseq < 0 ) || gPlayer[p].fireseq != 0) {
	          return;
	     }
	     if(tekgunrep(gun) != 0 || !gPlayer[p].ofire )
	    	 gPlayer[p].fireseq=1;
	}

	public static void tekdrawgun(int p)
	{
	    int  pic,x,apic;
	    int shade = 0;
	    int gun=gPlayer[p].lastgun;
		if(gPlayer[p].cursectnum >= 0)
			shade = sector[gPlayer[p].cursectnum].floorshade;

	     if( (gPlayer[p].fireseq != 0 || (game.menu.gShowMenu && game.menu.getCurrentMenu() instanceof MenuInterfaceSet)) && (tekcfg.gCrosshair && (gViewMode == kView3D)) ) {
	    	 apic=AIMPIC;
		     if( gDifficulty <= 1 )
		          apic=BIGAIMPIC;

	          int cx = windowx1+((windowx2-windowx1)>>1);
	          int cy = windowy1+((windowy2-windowy1)>>1);
	          int stat = 1;
	          engine.rotatesprite(cx << 16, cy << 16, tekcfg.gCrossSize, 0, apic,
	  				16, 0, (((stat & 1) ^ 1) << 4) , windowx1, windowy1, windowx2, windowy2);
	     }

	     if (gPlayer[p].fireseq == 0) {
	          pic=guntype[gun].pic;
	          return;
	     }

	     if( gPlayer[p].firedonetics > 0 ) {
	          pic=guntype[gun].firepic;
	     }
	     else {
	          pic=guntype[gun].pic+gPlayer[p].fireseq-1;
	     }

	     x=160;
	     int stat = 1|2|32;
	     if( (pic == 3981) ) {
	          if(  gPlayer[p].pInput.svel != 0 && gPlayer[p].pInput.svel < 10 ) {
	               overwritesprite(x,100,65536, 3990,shade,stat,0);
	               return;
	          }
	          else if( gPlayer[p].pInput.svel > 10 ) {
	               overwritesprite(x,100,65536,3986,shade,stat,0);
	               return;
	          }
	          else if( gPlayer[p].pInput.angvel != 0 && gPlayer[p].pInput.angvel < 10 ) {
	               overwritesprite(x,100,65536,3985,shade,stat,0);
	               return;
	          }
	          else if( gPlayer[p].pInput.angvel > 10 ) {
	               overwritesprite(x,100,65536,3989,shade,stat,0);
	               return;
	          }
	          else if( gPlayer[p].pInput.Jump ) {
	               overwritesprite(x,100,65536,3994+(gPlayer[p].pInput.Run?1:0),shade,stat,0);
	               return;
	          }
	          else if( gPlayer[p].pInput.Crouch ) {
	               overwritesprite(x,100,65536,3998+(gPlayer[p].pInput.Run?1:0),shade,stat,0);
	               return;
	          }
	    }

		if(gun == 5 || gun == 6)
			stat |= 512;

	    overwritesprite(x+gunbobx[gunbob],100+gunboby[gunbob],65536,pic,shade,stat,0);
	}

	public static void doweapanim(int p)
	{
		int pic =guntype[gPlayer[p].lastgun].pic+gPlayer[p].fireseq-1;
		if( gPlayer[p].firedonetics > 0 )
	    	pic=guntype[gPlayer[p].lastgun].firepic;

		if( (gPlayer[p].pInput.vel|gPlayer[p].pInput.svel) != 0 ) {
	          switch( pic ) {
	          case GUN01FIRESTART:
	          case GUN02FIRESTART:
	          case GUN03FIRESTART:
	          case GUN04FIRESTART:
	          case GUN05FIRESTART:
	          case GUN06FIRESTART:
	          case GUN07FIRESTART:
	          case GUN08FIRESTART:
	          case GUNDEMFIRESTART:

	               bobtics+=( 1 + (gPlayer[p].pInput.Run?4:0) );
	               if( bobtics > TICSPERFRAME ) {
	                    bobtics=0;
	                    gunbob++;
	                    if( gunbob > 9 )
	                         gunbob=0;
	               }

	               break;
	          default:
	               gunbob=0;
	               break;
	          }
	     }
	}

	public static final int  DIEFRAMETIME  = (160/(JAKETWITCHPIC-JAKEDEATHPIC));
	public static final int    DRAWWEAPSPEED   =    4;
	static short[] dieframe = new short[MAXPLAYERS], firepicdelay= new short[MAXPLAYERS];

	public static void tekanimweap(int p)
	{
	    int  ammo,firekey,fseq,seq,tics;
	    int usegun;
	    Guntype gunptr;

	     if (game.nNetMode == NetMode.Multiplayer) {
	          if (gPlayer[p].health < 0 && dieframe[p] == 0) {
	               dieframe[p]=(short) TICSPERFRAME;
	          }
	          else if (dieframe[p] > 0) {
	               sprite[gPlayer[p].playersprite].picnum=(short) (JAKEDEATHPIC+(dieframe[p]/DIEFRAMETIME));
	               dieframe[p]+=TICSPERFRAME;
	               return;
	          }
	          else {
	               if (gPlayer[p].pInput.Fire && firepicdelay[p] == 0) {
	                    sprite[gPlayer[p].playersprite].picnum=JAKEATTACKPIC;
	                    firepicdelay[p]=16;
	               }
	               else if (firepicdelay[p] > 0) {
	                    firepicdelay[p]--;
	                    if (firepicdelay[p] <= 0) {
	                         firepicdelay[p]=0;
	                         sprite[gPlayer[p].playersprite].picnum=JAKEWALKPIC;
	                    }
	               }
	               if (firepicdelay[p] == 0 && gPlayer[p].pInput.vel == 0 && gPlayer[p].pInput.svel == 0) {
	                    sprite[gPlayer[p].playersprite].picnum=JAKESTANDPIC;
	               }
	          }
	     }

	     int newgun = gPlayer[p].pInput.selectedgun;
	     int gun = gPlayer[p].lastgun;

	     if(newgun != -1) {
	    	 gPlayer[p].updategun = gun = newgun;
	    	 if(gPlayer[p].fireseq != 0)
	    		 gPlayer[p].updategun |= (1 << 8);
	     }

	     if ((seq=gPlayer[p].fireseq) == 0) {
	    	 if(gPlayer[p].updategun != -1) {
	    		 gPlayer[p].lastgun = (gPlayer[p].updategun & 0xFF);
	    		 if(((gPlayer[p].updategun & 0xFF00) >> 8) != 0)
	    			 gPlayer[p].drawweap = 1;
	    		 gPlayer[p].updategun = -1;
	    	 } else gPlayer[p].lastgun = gun;
	        if(gPlayer[p].drawweap == 0)
	        	return;
	     }

	     usegun=gPlayer[p].lastgun;
	     if (usegun != gun) {
	          gunptr=guntype[usegun];
	          fseq=gunptr.firepic-gunptr.pic;
	          if (gPlayer[p].firedonetics >= 0) {
	               gPlayer[p].firedonetics=2;
	          }
	          firekey=0;
	     }
	     else {
	          firekey=gPlayer[p].pInput.Fire?1:0;
	          if (firekey != 0) {
	               gPlayer[p].drawweap=1;
	          }
	     }

	     if( (firekey == 0) && gPlayer[p].pInput.Holster_Weapon && gPlayer[p].firedonetics == 1) {
	    	 gPlayer[p].firedonetics=2;
	    	 gPlayer[p].updategun = -1;
	     }

	     if (gPlayer[p].firedonetics == 2) {
	          gPlayer[p].firedonetics=-1;
	          gPlayer[p].drawweap=0;

	          if (gPlayer[p].firedonetics <= 0) {
	               gunptr=guntype[usegun];
	               fseq=gunptr.firepic-gunptr.pic;
	               gPlayer[p].fireseq=fseq;
	               return;
	          }
	     }
	     gunptr=guntype[usegun];
	     fseq=gunptr.firepic-gunptr.pic;

	     if (seq-1 >= fseq)
	          tics=gunptr.tics;
	     else
	          tics=DRAWWEAPSPEED;

	     if (lockclock >= gPlayer[p].lastchaingun+tics) {
	    	 gPlayer[p].lastchaingun=lockclock;
	          ammo=hasammo(usegun,p)?1:0;
	          if(ammo == 0)
	        	  firekey = 0;

	          if (seq-1 >= fseq && ammo != 0) {
	             if (gunptr.action[seq-fseq-1] != 0) {
	                    switch( gun+1 ) {
	//jsa friday
	                         case 1:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON1 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON1 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 2:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON2 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON2 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 3:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON3 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON3 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 4:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON4 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON4 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 5:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON5 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON5 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 6:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON6 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON6 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 7:
	                              if(game.nNetMode == NetMode.Multiplayer)
	                                   playsound(S_WEAPON7 ,gPlayer[p].posx,gPlayer[p].posy,0,ST_NOUPDATE);
	                              else
	                                   playsound(S_WEAPON7 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         case 8:
	                              playsound(S_WEAPON8 ,0,0,0,ST_IMMEDIATE);
	                              break;
	                         default:
	                              break;

	                    }
	                    shootgun(p,gPlayer[p].posx,gPlayer[p].posy,gPlayer[p].posz,(int)gPlayer[p].ang,(int)gPlayer[p].horiz,gPlayer[p].cursectnum,usegun);
	                    if (game.nNetMode == NetMode.Multiplayer) {
	                         sprite[gPlayer[p].playersprite].picnum=JAKEATTACKPIC+1;
	                         firepicdelay[p]=8;
	                    }
	               }

	               if (seq-1 >= gunptr.endfirepic-gunptr.pic) {
	                    gPlayer[p].fireseq=fseq+1;
	                    return;
	               }

	               if (firekey == 0) {
	                    gPlayer[p].firedonetics=1;
	                    gPlayer[p].fireseq=fseq;
	                    return;
	               }
	               else {
	                    gPlayer[p].firedonetics=0;
	               }
	          }
	          if (gPlayer[p].drawweap != 0) {
	               if (ammo !=0 || seq < fseq) {
	                    seq++;
	               }
	               else {
	                    if (firekey == 0) {
	                         gPlayer[p].firedonetics=1;
	                    }
	               }
	          }
	          else if (gPlayer[p].firedonetics < 0) {
	               seq--;
	          }

	          gPlayer[p].fireseq=seq;
	     }
	}

	public static int changegun(int p, int next) {
		Player plr = gPlayer[p];

		int weap = plr.lastgun;
		do {
			weap = (weap + next);

			if(weap > 6) weap = 0;
			if(weap < 0) weap = 6;

			if ((plr.weapons & flags32[weap + 1]) != 0 && hasammo(weap, p))
				break;
		} while(plr.lastgun != weap);

		if(plr.fireseq == 0 && plr.updategun != -1)
	    	 return -1;

		return weap;
	}

	public static boolean hasammo(int gun,int p)     // does player[p] have ammo for "gun"?
	{
		return gPlayer[p].ammo[gun] > 0;
	}

	public static void shootgun(int snum,int x,int y,int z,int daang,int dahoriz,
	     short dasectnum,int guntype)
	{
	     short hitsect,hitwall,hitsprite,daang2;
	     short bloodhitwall;
	     int  bloodhitx,bloodhity,bloodhitz;
	     int  cx,cy,i,j,daz2,hitx,hity,hitz,xydist,zdist;
	     int   rv,pnum;

	     if( gPlayer[snum].health <= 0 || !hasammo(guntype, snum) )
	          return;

	     guntype+=1;

	     switch (guntype) {
	     case GUN1FLAG:
	          cx=x+(sintable[(daang+2560+128)&2047]>>6);
	          cy=y+(sintable[(daang+2048+128)&2047]>>6);
	          j=jsinsertsprite(dasectnum, FORCEPROJECTILESTAT);
	          if( j != -1 ) {
	               fillsprite(j,cx,cy,z,128+2,-16,0,12,16,16,0,0,FORCEBALLPIC,daang,
	                          sintable[(daang+2560-11)&2047]>>5, // -17 = travel 3 degrees left
	                          sintable[(daang+2048-11)&2047]>>5, // -17 = travel 3 degrees left
	                          100-dahoriz,snum+MAXSPRITES,dasectnum,FORCEPROJECTILESTAT,0,0,-1);
	          }
	          break;
	     case GUN2FLAG:
	    	 gPlayer[snum].ammo[guntype - 1]--;
	    	 if(  gPlayer[snum].ammo[guntype - 1] < 0 ) {
	    		 gPlayer[snum].ammo[guntype - 1] = 0;
	    		 break;
	    	 }
	  	       daang2=(short) daang;
	  	       daz2=(100-dahoriz)*2000;

	          engine.hitscan(x,y,z,dasectnum,sintable[(daang2+2560)&2047],sintable[(daang2+2048)&2047],daz2,pHitInfo,CLIPMASK1);
	          hitsprite = pHitInfo.hitsprite; hitwall = pHitInfo.hitwall; hitsect = pHitInfo.hitsect;
	          hitx = pHitInfo.hitx; hity = pHitInfo.hity; hitz = pHitInfo.hitz;
	          if( (hitsprite >= 0) && (sprite[hitsprite].statnum < MAXSTATUS)) {
	               if( playerhit(hitsprite) != 0) {
	            	    pnum = playerhit;
	                    playerpainsound(pnum);
	                    playerwoundplayer(pnum,snum,2);
	               }
	               else {
	                    switch( sprite[hitsprite].picnum ) {
	                    default:
	                         rv=damagesprite(hitsprite,tekgundamage(guntype,x,y,z,hitsprite));

	                         if( (rv == 1) && (goreflag) ) {
	                              if( spewblood(hitsprite, hitz, daang) != 0 ) {
	                                   sprite[hitsprite].cstat&=0xFEFE;  // non hitscan and non block
	                                   // must preserve values from previous hitscan call,
	                                   // thus the bloodxhitx, bloodwall, etc...
	                                   engine.hitscan(x,y,z,dasectnum,sintable[(daang2+2560)&2047],sintable[(daang2+2048)&2047],daz2,pHitInfo,CLIPMASK1);
	                                   bloodhitwall = pHitInfo.hitwall;
	                                   bloodhitx = pHitInfo.hitx; bloodhity = pHitInfo.hity; bloodhitz = pHitInfo.hitz;
	                                   if( bloodhitwall != -1 ) {
	                                        bloodonwall(bloodhitwall,sprite[hitsprite].x,sprite[hitsprite].y,sprite[hitsprite].z,
	                                                    sprite[hitsprite].sectnum,daang2,bloodhitx,bloodhity,bloodhitz);
	                                   }
	                              }
	                         }
	                         if( rv == 1 ) {
	                              killscore(hitsprite,snum,guntype);
	                         }
	                    break;
	                    }
	               }
	          }
	          if( hitwall >= 0 ) {
	               j=jsinsertsprite(hitsect, (short)3);
	               if( j != -1 ) {
	                    fillsprite(j,hitx,hity,hitz+(8<<8),2,0,0,32,22,22,0,0,
	                               EXPLOSION,daang,0,0,0,snum+MAXSPRITES,hitsect,3,63,0,-1);

	                    movesprite((short)j,
	                               -((sintable[(512+daang)&2047]*TICSPERFRAME)<<4),
	                               -((sintable[daang&2047]*TICSPERFRAME)<<4),0,4<<8,4<<8,1);
	                    playsound(S_RIC1,hitx,hity,0,ST_NOUPDATE);
	               }
	          }
	          break;
	     case GUN3FLAG:
	    	 gPlayer[snum].ammo[guntype - 1]--;
	    	 if(  gPlayer[snum].ammo[guntype - 1] < 0 ) {
	    		 gPlayer[snum].ammo[guntype - 1] = 0;
	    		 break;
	    	 }
	          cx=x+(sintable[(daang+2560+256)&2047]>>6);
	          cy=y+(sintable[(daang+2048+256)&2047]>>6);
	          if( gPlayer[snum].invaccutrak > 0 ) {
	               j=jsinsertsprite(dasectnum, BOMBPROJECTILESTAT);
	               if( j != -1 ) {
	                    fillsprite(j,cx,cy,z+(4<<8),128,-16,0,32,16,16,0,0,BOMBPIC,daang,
	                         sintable[(daang+2560-11)&2047]>>5, // -17 = travel 3 degrees left
	                         sintable[(daang+2048-11)&2047]>>5, // -17 = travel 3 degrees left
	                         100-dahoriz,snum,dasectnum,BOMBPROJECTILESTAT,0,0,-1);
	                    sprite[j].owner=(short) snum;
	               }
	          }
	          else {
	               j=jsinsertsprite(dasectnum, BOMBPROJECTILESTAT2);
	               if( j != -1 ) {
	                    fillsprite(j,cx,cy,z+(4<<8),128,-16,0,32,16,16,0,0,BOMBPIC,daang,
	                         sintable[(daang+2560-11)&2047]>>5, // -17 = travel 3 degrees left
	                         sintable[(daang+2048-11)&2047]>>5, // -17 = travel 3 degrees left
	                         100-dahoriz,snum,dasectnum,BOMBPROJECTILESTAT2,0,0,-1);
	                    sprite[j].owner=(short) snum;
	               }
	          }
	          break;
	     case GUN4FLAG:
	    	 gPlayer[snum].ammo[guntype - 1]--;
	    	 if(  gPlayer[snum].ammo[guntype - 1] < 0 ) {
	    		 gPlayer[snum].ammo[guntype - 1] = 0;
	    		 break;
	    	 }
	          cx=x+(sintable[(daang+2560+128)&2047]>>6);
	          cy=y+(sintable[(daang+2048+128)&2047]>>6);
	          j=jsinsertsprite(dasectnum, DARTPROJECTILESTAT);
	          if( j != -1 ) {
	               fillsprite(j,cx,cy,z+(5<<8),128,-35,0,12,16,16,0,0, 338 ,daang,
	                    sintable[(daang+2560-6)&2047]>>5, // -17 = travel 3 degrees left
	                    sintable[(daang+2048-6)&2047]>>5, // -17 = travel 3 degrees left
	                    100-dahoriz,snum,dasectnum,DARTPROJECTILESTAT,0,0,-1);
	               sprite[j].owner=(short) snum;
	               playsound(S_RIC1,cx,cy,0,ST_NOUPDATE);

	          }
	          break;
	     case GUN5FLAG:
	          daang2=(short) daang;
	          daz2=(100-dahoriz)*2000;
	          engine.hitscan(x,y,z,dasectnum,sintable[(daang2+2560)&2047],sintable[(daang2+2048)&2047],daz2,pHitInfo,CLIPMASK1);
	          hitsect = pHitInfo.hitsect; hitwall = pHitInfo.hitwall; hitsprite = pHitInfo.hitsprite;
	          hitx = pHitInfo.hitx; hity = pHitInfo.hity; hitz = pHitInfo.hitz;


	          if( (hitsprite >= 0) && (sprite[hitsprite].statnum < MAXSTATUS)) {
	               xydist=klabs(gPlayer[snum].posx-sprite[hitsprite].x)+klabs(gPlayer[snum].posy-sprite[hitsprite].y);
	               zdist=klabs( (gPlayer[snum].posz>>8)-((sprite[hitsprite].z>>8)-(engine.getTile(sprite[hitsprite].picnum).getHeight()>>1)) );
	               if( (xydist > 768) || (zdist > 50) ) {
	                    break;
	               }
	               if( playerhit(hitsprite) != 0) {
	            	   pnum = playerhit;
	                    playerpainsound(pnum);
	                    playerwoundplayer(pnum,snum,5);
	               }
	               else {
	                    rv=damagesprite(hitsprite,tekgundamage(guntype,x,y,z,hitsprite));
	                    if( rv == 1 ) {
	                         killscore(hitsprite, snum, guntype);
	                    }
	               }
	          }
	          break;
	     case GUN6FLAG:
	    	 gPlayer[snum].ammo[guntype - 1]--;
	    	 if(  gPlayer[snum].ammo[guntype - 1] < 0 ) {
	    		 gPlayer[snum].ammo[guntype - 1] = 0;
	    		 break;
	    	 }
	          daang2=(short) daang;
	          daz2=(100-dahoriz)*2000;
	          engine.hitscan(x,y,z,dasectnum,sintable[(daang2+2560)&2047],sintable[(daang2+2048)&2047],daz2,pHitInfo,CLIPMASK1);
	          hitsect = pHitInfo.hitsect; hitwall = pHitInfo.hitwall; hitsprite = pHitInfo.hitsprite;
	          hitx = pHitInfo.hitx; hity = pHitInfo.hity; hitz = pHitInfo.hitz;
	          if( (hitsprite >= 0) && (sprite[hitsprite].statnum < MAXSTATUS)) {
	               xydist=klabs(gPlayer[snum].posx-sprite[hitsprite].x)+klabs(gPlayer[snum].posy-sprite[hitsprite].y);
	               zdist=klabs( (gPlayer[snum].posz>>8)-((sprite[hitsprite].z>>8)-(engine.getTile(sprite[hitsprite].picnum).getHeight()>>1)) );
	               if( (xydist > 2560) || (zdist > 576) ) {
	                    break;
	               }

	               if( playerhit(hitsprite) != 0 ) {
	            	   pnum = playerhit;
	                   if( playervirus(pnum, FIREPIC) != 0 ) {
	                   		playerwoundplayer(pnum,snum,6);
	                   }
	               }
	               else {
	                    attachvirus(hitsprite, FIREPIC);
	               }
	          }
	          break;
	     case GUN7FLAG:
	    	 gPlayer[snum].ammo[guntype - 1]--;
	    	 if(  gPlayer[snum].ammo[guntype - 1] < 0 ) {
	    		 gPlayer[snum].ammo[guntype - 1] = 0;
	    		 break;
	    	 }
	          for (i=0 ; i < 3 ; i++) {
	               cx=x+(sintable[(daang+2560+256)&2047]>>6);
	               cy=y+(sintable[(daang+2048+256)&2047]>>6);
	               j=jsinsertsprite(dasectnum,ROCKETPROJECTILESTAT);
	               if (j != -1) {
	                    fillsprite(j,cx,cy,z+(4<<8),128,-24,0,12,16,16,0,0,335,daang,
	                         sintable[(daang+2560-11)&2047]>>(2+i),
	                         sintable[(daang+2048-11)&2047]>>(2+i),
	                         (100-dahoriz)-(i<<2),
	                         snum,dasectnum,ROCKETPROJECTILESTAT,0,0,-1);
	               sprite[j].owner=(short) snum;
	               }
	          }
	          break;
	     case GUN8FLAG:
	    	 gPlayer[snum].ammo[guntype - 1]--;
	    	 if(  gPlayer[snum].ammo[guntype - 1] < 0 ) {
	    		 gPlayer[snum].ammo[guntype - 1] = 0;
	    		 break;
	    	 }
	          cx=x+(sintable[(daang+2560)&2047]>>6);
	          cy=y+(sintable[(daang+2048)&2047]>>6);
	          j=jsinsertsprite(dasectnum, MATRIXPROJECTILESTAT);
	          if( j != -1 ) {
	               fillsprite(j,cx,cy,z+(5<<8),128,-35,0,12,16,16,0,0,3765,daang,
	                    sintable[(daang+2560)&2047]>>5,
	                    sintable[(daang+2048)&2047]>>5,
	                    100-dahoriz,snum,dasectnum,MATRIXPROJECTILESTAT,0,0,-1);
	               sprite[j].owner=(short) snum;
	          }
	          break;
	     default:
	          break;
	    }

	    if( guntype != GUN1FLAG )
	          playergunshot(snum);
	}

	public static void killscore(int hs, int snum, int guntype)
	{
	     int       ext=sprite[hs].extra;
	     short     score = 0;

	     if( !validplayer(snum)) {
	          return;
	     }
	     if( validext(ext) ==0) {
	          return;
	     }

	     if( isanandroid(hs) != 0) {
	          score=200;
	     }
	     else if( isahologram(hs) != 0) {
	          score=100;
	     }
	     else {
	          switch( spriteXT[ext].sclass ) {
	          case CLASS_NULL:
	               return;
	          case CLASS_FCIAGENT:
	               score=-500;
	               break;
	          case CLASS_CIVILLIAN:
	               score=-500;
	               break;
	          case CLASS_SPIDERDROID:
	               score=50;
	               break;
	          case CLASS_COP:
	               score=-500;
	               break;
	          case CLASS_MECHCOP:
	               score=-50;
	               break;
	          case CLASS_TEKBURNOUT:
	               score=100;
	               break;
	          case CLASS_TEKGOON:
	               score=200;
	               break;
	          case CLASS_ASSASSINDROID:
	               score=300;
	               break;
	          case CLASS_SECURITYDROID:
	               score=100;
	               break;
	          case CLASS_TEKBOSS:
	               score=300;
	               break;
	          case CLASS_TEKLORD:
	               score=500;
	               break;
	          }
	          switch( guntype ) {
	          case 0:
	          case 1:
	               if( score < 0 ) {
	                    score=0;
	               }
	               else {
	                    score+=(score<<1);
	               }
	               break;
	          }
	     }

	     if( game.nNetMode == NetMode.Multiplayer ) {
	          score>>=4;
	     }
	     changescore(snum, score);
	}

	public static void playerwoundplayer(int plrhit, int plr, int guntype)
	{
	    boolean killed=false;
		int       damage=0,score=0;

	     if( !validplayer(plrhit) || !validplayer(plr) ) {
	          return;
	     }

	     switch( guntype ) {
	     case 2:  score=5;  damage=48;   break;
	     case 3:  score=10; damage=192;  break;
	     case 4:  score=10; damage=512;  break;
	     case 5:  score=15; damage=1024; break;
	     case 6:  score=10; damage=24;   break;
	     case 7:  score=10; damage=512;  break;
	     default: score=5;  damage=32;   break;
	     }

	     killed=changehealth(plrhit,-damage);

	     if( killed ) {
	          changescore(plr, (score<<1));
	     }
	     else {
	          changescore(plr, score);
	     }
	}

	public static int tekgundamage(int gun,int x,int y,int z,int hitsprite)
	{
	     int       damage;

	     switch( gun ) {
	     case 1:  damage= 2; break;
	     case 4:  damage=20; break;
	     default: damage= 2; break;
	     }
	     switch( gDifficulty ) {
	     case 0:
	     case 1:  damage+=4; break;
	     case 2:  damage+=4; break;
	     case 3:
	     default:  damage+=10; break;
	     }

	     return(damage);
	}

	public static void bloodonwall(int wn, int x,int y,int z, short sect, int daang, int hitx, int hity, int hitz)
	{
	     int  j;

	     if( wall[wn].lotag != 0 ) {
	          return;
	     }

	     switch( wall[wn].picnum ) {
	     case 15:
	     case 71:
	     case 72:
	     case 87:
	     case 93:
	     case 92:
	     case 95:
	     case 97:
	     case 98:
	     case 99:
	     case 124:
	     case 126:
	     case 147:
	     case 152:
	     case 214:
	     case 215:
	     case 235:
	     case 236:
	     case 636:
	     case 855:
	     case 1457:
	          return;
	     default:
	          break;
	     }

	     if( wall[wn].lotag == 0 ) {
	          wall[wn].lotag=1;
	          engine.neartag(x,y,z,sect,(short)daang,neartag,512,1);

	          if( neartag.tagwall != -1 ) {
	               j=jsinsertsprite(sect, BLOODFLOW);
	               if( j != -1 ) {
	                    sprite[j].picnum=WALLBLOOD;
	                    sprite[j].x=hitx;
	                    sprite[j].y=hity;
	                    sprite[j].z=hitz+2048;
	                    if( sprite[j].z > sector[sect].floorz )
	                         sprite[j].z=sector[sect].floorz-2048;
	                    sprite[j].xrepeat=16;
	                    sprite[j].yrepeat=16;
	                    sprite[j].xvel=0;
	                    sprite[j].yvel=0;
	                    sprite[j].zvel=0;
	                    sprite[j].cstat=0x0090;
	                    sprite[j].ang=wallnormal(wn);
	                    sprite[j].shade=0;
	                    sprite[j].extra=-1;
	                    sprite[j].lotag=0;
	                    sprite[j].hitag=0;
	               }
	          }
	          wall[wn].lotag=0;
	     }
	}

	public static void gunstatuslistcode()
	{
	     int     ext;
	     int      dax,day,daz,j;
	     int       pnum,rv;
	     short hitsprite, hitobject, nexti;

	     short i=headspritestat[FORCEPROJECTILESTAT];   //moving force ball
	     while (i >= 0) {
	          nexti=nextspritestat[i];
	          dax=(((sprite[i].xvel)*TICSPERFRAME)<<11);
	          day=(((sprite[i].yvel)*TICSPERFRAME)<<11);
	          daz=(((sprite[i].zvel)*TICSPERFRAME)<<3);
	          hitobject=movesprite(i,dax,day,daz,4<<8,4<<8,1);

	          if( (hitobject&0xC000) == 49152 ) {  // hit a sprite
	               hitsprite=(short) (hitobject&0x0FFF);
	               ext=sprite[hitsprite].extra;
	               if( validext(ext)!=0) {
	                    switch( spriteXT[ext].walkpic ) {
	                    case RUBWALKPIC:
	                    case FRGWALKPIC:
	                    case COP1WALKPIC:
	                    case ANTWALKPIC:
	                    case SARAHWALKPIC:
	                    case MAWALKPIC:
	                    case DIWALKPIC:
	                    case ERWALKPIC:
	                    case SAMWALKPIC:
	                    case SUNGWALKPIC:
	                    case COWWALKPIC:
	                    case COPBWALKPIC:
	                    case NIKAWALKPIC:
	                    case REBRWALKPIC:
	                    case TRENWALKPIC:
	                    case WINGWALKPIC:
	                    case HALTWALKPIC:
	                    case REDHWALKPIC:
	                    case ORANWALKPIC:
	                    case BLKSWALKPIC:
	                    case SFROWALKPIC:
	                    case SSALWALKPIC:
	                    case SGOLWALKPIC:
	                    case SWATWALKPIC:
	                         attachvirus(hitsprite, FORCEBALLPIC);
	                         break;
	                    default:
	                         forceexplosion(i);
	                         break;
	                    }
	                    jsdeletesprite(i);
	               }
	               else {
	                    forceexplosion(i);
	                    jsdeletesprite(i);
	               }
	          }
	          else if( hitobject != 0 ) {
	               forceexplosion(i);
	               jsdeletesprite(i);
	          }

	          i=nexti;
	     }

	     i=headspritestat[ROCKETPROJECTILESTAT];
	     while (i >= 0) {
	          nexti=nextspritestat[i];

	          dax=(((sprite[i].xvel)*TICSPERFRAME)<<10);
	          day=(((sprite[i].yvel)*TICSPERFRAME)<<10);
	          daz=(((sprite[i].zvel)*TICSPERFRAME)<<3);

	          hitobject=movesprite(i,dax,day,daz,4<<8,4<<8,1);
	          if( (hitobject&0xC000) == 49152 ) {  // hit a sprite
	               hitsprite=(short) (hitobject&0x0FFF);
	               int hit = playerhit(hitsprite);
	               pnum = playerhit;
	               if( hit != 0 ) {
	                    playerpainsound(pnum);
	                    playerwoundplayer(pnum,sprite[i].owner,7);
	               }
	               else if( sprite[i].picnum != sprite[hitsprite].picnum ) {
	                    if( isahologram(hitsprite) != 0) {
	                         showmessage("WAS A HOLOGRAM");
	                         killscore(hitsprite, sprite[i].owner, 3);
	                         engine.changespritestat(hitsprite, VANISH);
	                    }
	                    else if( isanandroid(hitsprite) != 0) {
	                         showmessage("WAS AN ANDROID");
	                         killscore(hitsprite, sprite[i].owner, 3);
	                         androidexplosion(hitsprite);
	                         engine.changespritestat(hitsprite, VANISH);
	                    }
	                    else {
	                         blastmark(hitsprite);
	                         rv=damagesprite(hitsprite, 500);
	                         if( rv == 1 ) {
	                              killscore(hitsprite,sprite[i].owner,3);
	                         }
	                    }
	               }
	          }
	          if( hitobject != 0 ) {
	               bombexplosion(i);
	               jsdeletesprite(i);
	          }

	          i=nexti;
	     }

	     i=headspritestat[MATRIXPROJECTILESTAT];
	     while (i >= 0) {
	          nexti=nextspritestat[i];

	          dax=(((sprite[i].xvel)*TICSPERFRAME)<<12);
	          day=(((sprite[i].yvel)*TICSPERFRAME)<<12);
	          daz=(((sprite[i].zvel)*TICSPERFRAME)<<4);

	          hitobject=movesprite(i,dax,day,daz,4<<8,4<<8,1);

	          if( (hitobject&0xC000) == 49152 ) {  // hit a sprite
	               hitsprite=(short) (hitobject&0x0FFF);
	               int hit = playerhit(hitsprite);
	               pnum = playerhit;
	               if( hit == 0 ) {
	                    rv=damagesprite(hitsprite, 500);
	                    if( rv == 1 ) {
	                         killscore(hitsprite,sprite[i].owner,8);
	                    }
	               }
	          }
	          if( hitobject != 0 ) {
	               jsdeletesprite(i);
	          }

	          i=nexti;
	     }

	     i=headspritestat[BOMBPROJECTILESTAT];
	     while (i >= 0) {
	          nexti=nextspritestat[i];

	          dax=(((sprite[i].xvel)*TICSPERFRAME)<<12);
	          day=(((sprite[i].yvel)*TICSPERFRAME)<<12);
	          daz=(((sprite[i].zvel)*TICSPERFRAME)<<4);

	          hitobject=movesprite(i,dax,day,daz,4<<8,4<<8,1);
	          if( (hitobject&0xC000) == 49152 ) {  // hit a sprite
	               hitsprite=(short) (hitobject&0x0FFF);
	               int hit = playerhit(hitsprite);
	               pnum = playerhit;
	               if( hit !=0 ) {
	                    playerpainsound(pnum);
	                    playerwoundplayer(pnum,sprite[i].owner,3);
	               }
	               else if( sprite[i].picnum != sprite[hitsprite].picnum ) {
	                    if( isahologram(hitsprite) != 0) {
	                         showmessage("WAS A HOLOGRAM");
	                         killscore(hitsprite, sprite[i].owner, 3);
	                         engine.changespritestat(hitsprite, VANISH);
	                         //jsdeletesprite(hitsprite);
	                    }
	                    else if( isanandroid(hitsprite) != 0) {
	                         showmessage("WAS AN ANDROID");
	                         killscore(hitsprite, sprite[i].owner, 3);
	                         androidexplosion(hitsprite);
	                         engine.changespritestat(hitsprite, VANISH);
	                         //jsdeletesprite(hitsprite);
	                    }
	                    else {
	                         blastmark(hitsprite);
	                         rv=damagesprite(hitsprite, 500);
	                         if( rv == 1 ) {
	                              killscore(hitsprite,sprite[i].owner,3);
	                         }
	                    }
	               }
	          }
	          if( hitobject != 0 ) {
	               bombexplosion(i);
	               jsdeletesprite(i);
	          }

	          i=nexti;
	     }

	     i=headspritestat[BOMBPROJECTILESTAT2];
	     while (i >= 0) {
	          nexti=nextspritestat[i];

	          sprite[i].xvel+=( ((krand_intercept(" GUN1025")&64)-32)>>1 );
	          sprite[i].yvel+=( ((krand_intercept(" GUN1026")&64)-32)>>1 );
	          sprite[i].zvel+=( ((krand_intercept(" GUN1027")&31)-16)>>1 );

	          dax=(((sprite[i].xvel)*TICSPERFRAME)<<12);
	          day=(((sprite[i].yvel)*TICSPERFRAME)<<12);
	          daz=(((sprite[i].zvel)*TICSPERFRAME)<<4);

	          hitobject=movesprite(i,dax,day,daz,4<<8,4<<8,1);
	          if( (hitobject&0xC000) == 49152 ) {  // hit a sprite
	               hitsprite=(short) (hitobject&0x0FFF);
	               int hit = playerhit(hitsprite);
	               pnum = playerhit;
	               if( hit != 0 ) {
	                    playerpainsound(pnum);
	                    playerwoundplayer(pnum,sprite[i].owner,3);
	               }
	               else if( sprite[i].picnum != sprite[hitsprite].picnum ) {
	                    if( isahologram(hitsprite)!=0 ) {
	                         showmessage("WAS A HOLOGRAM");
	                         killscore(hitsprite, sprite[i].owner, 3);
	                         engine.changespritestat(hitsprite, VANISH);
	                         //jsdeletesprite(hitsprite);
	                    }
	                    else if( isanandroid(hitsprite) !=0) {
	                         showmessage("WAS AN ANDROID");
	                         killscore(hitsprite, sprite[i].owner, 3);
	                         androidexplosion(hitsprite);
	                         engine.changespritestat(hitsprite, VANISH);
	                         //jsdeletesprite(hitsprite);
	                    }
	                    else {
	                         blastmark(hitsprite);
	                         rv=damagesprite(hitsprite, 500);
	                         if( rv == 1 ) {
	                              killscore(hitsprite,sprite[i].owner,3);
	                         }
	                    }
	               }
	          }
	          if( hitobject != 0 ) {
	               bombexplosion(i);
	               jsdeletesprite(i);
	          }

	          i=nexti;
	     }

	     i=headspritestat[DARTPROJECTILESTAT];
	     while (i >= 0) {
	          nexti=nextspritestat[i];
	          dax=(((sprite[i].xvel)*TICSPERFRAME)<<13);
	          day=(((sprite[i].yvel)*TICSPERFRAME)<<13);
	          daz=(((sprite[i].zvel)*TICSPERFRAME)<<5);

	          hitobject=movesprite(i,dax,day,daz,4<<8,4<<8,1);

	          if( (hitobject&0xC000) == 49152 ) {  // hit a sprite
	               hitsprite=(short) (hitobject&0x0FFF);
	               int hit = playerhit(hitsprite);
	               pnum = playerhit;
	               if( hit != 0 ) {
	                    playerpainsound(pnum);
	                    playerwoundplayer(pnum,sprite[i].owner,4);
	               }
	               else if( sprite[i].picnum != sprite[hitsprite].picnum ) {
	                    if( isahologram(hitsprite) !=0) {
	                         showmessage("WAS A HOLOGRAM");
	                         killscore(hitsprite, sprite[i].owner, 4);
	                         engine.changespritestat(hitsprite, VANISH);
	                    }
	                    else if( isanandroid(hitsprite) !=0) {
	                         showmessage("WAS AN ANDROID");
	                         killscore(hitsprite, sprite[i].owner, 4);
	                         androidexplosion(hitsprite);
	                         engine.changespritestat(hitsprite, VANISH);
	                    }
	                    else if( tekexplodebody(hitsprite)!=0) {
	                         missionaccomplished(hitsprite);
	                         killscore(hitsprite, sprite[i].owner, 4);
	                         engine.changespritestat(hitsprite, VANISH);
	                    }
	                    else {
	                         rv=damagesprite(hitsprite, 500);
	                         if( rv == 1 ) {
	                              killscore(hitsprite,sprite[i].owner,3);
	                         }
	                    }
	               }
	          }
	          if( hitobject != 0 ) {
	               j=jsinsertsprite(sprite[i].sectnum, (short)3);
	               if( j != -1 ) {
					fillsprite(j,sprite[i].x,sprite[i].y,sprite[i].z+(8<<8),0,-4,0,32,
	                         64,64,0,0,
						EXPLOSION,sprite[i].ang,0,0,0,i,sprite[i].sectnum,3,63,0,0);
	               }
	               jsdeletesprite(i);
	          }

	          i=nexti;
	     }

	     i=headspritestat[MOVEBODYPARTSSTAT];    //flying body parts (gore option)
	     while (i >= 0) {
	          nexti=nextspritestat[i];
	          sprite[i].x+=((sprite[i].xvel*TICSPERFRAME)>>5);
	          sprite[i].y+=((sprite[i].yvel*TICSPERFRAME)>>5);
	          sprite[i].z+=((sprite[i].zvel*TICSPERFRAME)>>5);
	          sprite[i].zvel+=(TICSPERFRAME<<7);
	          if (sprite[i].z < sector[sprite[i].sectnum].ceilingz+(4<<8)) {
	               sprite[i].z=sector[sprite[i].sectnum].ceilingz+(4<<8);
	               sprite[i].zvel=(short) -(sprite[i].zvel>>1);
	          }
	          if (sprite[i].z > sector[sprite[i].sectnum].floorz-(4<<8)) {
	               sprite[i].z=sector[sprite[i].sectnum].floorz-(4<<8);
	               if (sprite[i].picnum == GOREBLOOD) {
	                    sprite[i].xvel=0;
	                    sprite[i].yvel=0;
	                    sprite[i].zvel=0;
	                    sprite[i].cstat|=0x20;
	               }
	               else {
	                    sprite[i].zvel=(short) -(sprite[i].zvel>>1);
	               }
	          }
	          sprite[i].lotag-=TICSPERFRAME;
	          if (sprite[i].lotag < 0) {
	               jsdeletesprite(i);
	          }
	          i=nexti;
	     }
	}

	public static short wallnormal(int wn)
	{
	     int      w1x,w1y, w2x,w2y;
	     short     wnorm;

	     w1x=wall[wn].x; w1y=wall[wn].y;
	     wn=wall[wn].point2;
	     w2x=wall[wn].x; w2y=wall[wn].y;
	     wnorm=engine.getangle(w2x-w1x, w2y-w1y);
	     wnorm=(short) ((wnorm+512)&2047);

	 return(wnorm);
	}

}
