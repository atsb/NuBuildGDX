package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.WHFX.*;
import static ru.m210projects.Witchaven.Whldsv.gAutosaveRequest;

import java.io.FileNotFoundException;

import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.WHTAG.*;
import static ru.m210projects.Witchaven.Items.*;

import static ru.m210projects.Witchaven.AI.Ai.*;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Main.UserFlag;
import ru.m210projects.Witchaven.Types.Item;
import ru.m210projects.Witchaven.Types.MapInfo;
import ru.m210projects.Witchaven.Types.PLAYER;

public class Whmap {

	public static boolean nextlevel;

	public static void loadnewlevel(int mapon) {
		MapInfo nextmap;
		if (gCurrentEpisode != null && (nextmap = gCurrentEpisode.getMap(mapon)) != null) {
			boardfilename = nextmap.path;
			Console.Println("Entering level " + boardfilename);
			nextlevel = true;
			gGameScreen.loadboard(boardfilename, null);
		} else
			game.show();
	}

	public static void preparesectors() {
		int endwall, j, k = 0, startwall;
		int dax, day;
		int dasector;
		int dax2, day2;

		for (int i = 0; i < numsectors; i++) {
			SECTOR s = sector[i];

			ceilingshadearray[i] = s.ceilingshade;
			floorshadearray[i] = s.floorshade;

			if (game.WH2) {
				if (mapon == 13) {
					s = sector[i];
					if (i == 209 && s.lotag == 62)
						s.lotag = 0;
					if (i == 435 && s.lotag == 0)
						s.lotag = 62;
				}
			}

			if (sector[i].lotag == 70)
				skypanlist[skypancnt++] = (short) i;

			if (sector[i].lotag >= 80 && sector[i].lotag <= 89)
				floorpanninglist[floorpanningcnt++] = (short) i;

			if (sector[i].lotag >= 900 && sector[i].lotag <= 999) {
				lavadrylandsector[lavadrylandcnt] = (short) i;
				lavadrylandcnt++;
			}

			if (sector[i].lotag >= 2100 && sector[i].lotag <= 2199) {
				System.out.println("revolvepivotx");
				startwall = sector[i].wallptr;
				endwall = startwall + sector[i].wallnum - 1;
				dax = 0;
				day = 0;
				for (j = startwall; j <= endwall; j++) {
					dax += wall[j].x;
					day += wall[j].y;
				}
				revolvepivotx[revolvecnt] = dax / (endwall - startwall + 1);
				revolvepivoty[revolvecnt] = day / (endwall - startwall + 1);

				k = 0;
				for (j = startwall; j <= endwall; j++) {
					revolvex[revolvecnt][k] = wall[j].x;
					revolvey[revolvecnt][k] = wall[j].y;
					k++;
				}
				revolvesector[revolvecnt] = (short) i;
				revolveang[revolvecnt] = 0;

				revolveclip[revolvecnt] = 1;
				if (sector[i].ceilingz == sector[wall[startwall].nextsector].ceilingz)
					revolveclip[revolvecnt] = 0;

				revolvecnt++;
			}

			switch (sector[i].lotag) {
			case 131:
			case 132:
			case 133:
			case 134:
			case DOORSWINGTAG:
				startwall = sector[i].wallptr;
				endwall = startwall + sector[i].wallnum - 1;
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].lotag == 4) {
						k = wall[wall[wall[wall[j].point2].point2].point2].point2;
						if ((wall[j].x == wall[k].x) && (wall[j].y == wall[k].y)) {
							swingdoor[swingcnt].wall[0] = j;
							swingdoor[swingcnt].wall[1] = wall[j].point2;
							swingdoor[swingcnt].wall[2] = wall[wall[j].point2].point2;
							swingdoor[swingcnt].wall[3] = wall[wall[wall[j].point2].point2].point2;
							swingdoor[swingcnt].angopen = 1536;
							swingdoor[swingcnt].angclosed = 0;
							swingdoor[swingcnt].angopendir = -1;
						} else {
							swingdoor[swingcnt].wall[0] = wall[j].point2;
							swingdoor[swingcnt].wall[1] = j;
							swingdoor[swingcnt].wall[2] = engine.lastwall(j);
							swingdoor[swingcnt].wall[3] = engine.lastwall(swingdoor[swingcnt].wall[2]);
							swingdoor[swingcnt].angopen = 512;
							swingdoor[swingcnt].angclosed = 0;
							swingdoor[swingcnt].angopendir = 1;
						}
						for (k = 0; k < 4; k++) {
							swingdoor[swingcnt].x[k] = wall[swingdoor[swingcnt].wall[k]].x;
							swingdoor[swingcnt].y[k] = wall[swingdoor[swingcnt].wall[k]].y;
						}
						swingdoor[swingcnt].sector = i;
						swingdoor[swingcnt].ang = swingdoor[swingcnt].angclosed;
						swingdoor[swingcnt].anginc = 0;
						swingcnt++;
					}
				}
				break;
			case 11:
				xpanningsectorlist[xpanningsectorcnt++] = (short) i;
				break;
			case 12:
				dasector = i;
				dax = 0x7fffffff;
				day = 0x7fffffff;
				dax2 = 0x80000000;
				day2 = 0x80000000;
				startwall = sector[i].wallptr;
				endwall = startwall + sector[i].wallnum - 1;
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dax)
						dax = wall[j].x;
					if (wall[j].y < day)
						day = wall[j].y;
					if (wall[j].x > dax2)
						dax2 = wall[j].x;
					if (wall[j].y > day2)
						day2 = wall[j].y;
					if (wall[j].lotag == 3)
						k = j;
				}
				if (wall[k].x == dax)
					dragxdir[dragsectorcnt] = -16;
				if (wall[k].y == day)
					dragydir[dragsectorcnt] = -16;
				if (wall[k].x == dax2)
					dragxdir[dragsectorcnt] = 16;
				if (wall[k].y == day2)
					dragydir[dragsectorcnt] = 16;

				dasector = wall[startwall].nextsector;
				dragx1[dragsectorcnt] = 0x7fffffff;
				dragy1[dragsectorcnt] = 0x7fffffff;
				dragx2[dragsectorcnt] = 0x80000000;
				dragy2[dragsectorcnt] = 0x80000000;
				startwall = sector[dasector].wallptr;
				endwall = startwall + sector[dasector].wallnum - 1;
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dragx1[dragsectorcnt])
						dragx1[dragsectorcnt] = wall[j].x;
					if (wall[j].y < dragy1[dragsectorcnt])
						dragy1[dragsectorcnt] = wall[j].y;
					if (wall[j].x > dragx2[dragsectorcnt])
						dragx2[dragsectorcnt] = wall[j].x;
					if (wall[j].y > dragy2[dragsectorcnt])
						dragy2[dragsectorcnt] = wall[j].y;
				}

				dragx1[dragsectorcnt] += (wall[sector[i].wallptr].x - dax);
				dragy1[dragsectorcnt] += (wall[sector[i].wallptr].y - day);
				dragx2[dragsectorcnt] -= (dax2 - wall[sector[i].wallptr].x);
				dragy2[dragsectorcnt] -= (day2 - wall[sector[i].wallptr].y);
				System.out.println("dragsectorlist");
				dragfloorz[dragsectorcnt] = sector[i].floorz;

				dragsectorlist[dragsectorcnt++] = (short) i;
				break;
			case 10:
			case 14:
				// case 15:
				// captureflag sector
			case 4002:
				warpsectorlist[warpsectorcnt++] = (short) i;
				break;
			case 10000:
				System.out.println("bobbingsectorlist");
				bobbingsectorlist[bobbingsectorcnt++] = (short) i;
			}
			if (sector[i].floorpicnum == TELEPAD && sector[i].lotag == 0)
				warpsectorlist[warpsectorcnt++] = (short) i;
			if (sector[i].floorpicnum == FLOORMIRROR)
				floormirrorsector[floormirrorcnt++] = (short) i;
		}

		ypanningwallcnt = 0;
		for (int i = 0; i < numwalls; i++) {
			wallshadearray[i] = wall[i].shade;
			if (wall[i].lotag == 1) {
				if (ypanningwallcnt < ypanningwalllist.length)
					ypanningwalllist[ypanningwallcnt++] = (short) i;
			}
		}

	}

	public static boolean prepareboard(String fname) {

		short i;
		short treesize;

		PLAYER plr = player[0];

		engine.srand(17);

		BuildPos out = null;
		try {
			out = engine.loadboard(fname);
		} catch (FileNotFoundException | InvalidVersionException | RuntimeException e) {
			game.GameMessage(e.getMessage());
			return false;
		}

		plr.x = out.x;
		plr.y = out.y;
		plr.z = out.z;
		plr.ang = out.ang;
		plr.sector = out.sectnum;

//		int ratcnt = 0;
		swingcnt = 0;
		xpanningsectorcnt = 0;
		ypanningwallcnt = 0;
		floorpanningcnt = 0;
//	    crushsectorcnt=0;
		revolvecnt = 0;
		warpsectorcnt = 0;
		dragsectorcnt = 0;
		ironbarscnt = 0;
		bobbingsectorcnt = 0;
		playertorch = 0;

		damage_angvel = 0;
		damage_svel = 0;
		damage_vel = 0;

		hours = 0;
		minutes = 0;
		seconds = 0;
		fortieth = 0;

//		goblinwarcnt = 0;
		treasurescnt = 0;
		treasuresfound = 0;
		killcnt = 0;
		kills = 0;
		expgained = 0;
		// numinterpolations=0;

		// the new mirror code
		floormirrorcnt = 0;
		engine.getTile(FLOORMIRROR).clear();

		for (i = 0; i < ARROWCOUNTLIMIT; i++)
			arrowsprite[i] = -1;
		for (i = 0; i < THROWPIKELIMIT; i++)
			throwpikesprite[i] = -1;

		lavadrylandcnt = 0;
		aiInit();

		for (i = 0; i < MAXSPRITES; i++) { // setup sector effect options
			if (sprite[i].statnum >= MAXSTATUS)
				continue;

			SPRITE spr = sprite[i];

			if (!game.WH2 && mapon == 5 && i == 0 && spr.lotag == 0 && spr.hitag == 0) {
				spr.lotag = 1;
				spr.hitag = 34;
			}

			if (sprite[i].picnum == CONE) {
				sparksx = sprite[i].x;
				sparksy = sprite[i].y;
				sparksz = sprite[i].z;
				for (int j = 0; j < 10; j++) {
					makesparks(i, 1);
				}
				for (int j = 10; j < 20; j++) {
					makesparks(i, 2);
				}
				for (int j = 20; j < 30; j++) {
					makesparks(i, 3);
				}
				sprite[i].cstat &= ~3;
				sprite[i].cstat |= 0x8000;
				sprite[i].clipdist = 4;
				engine.changespritestat(i, (short) 0);
				sector[sprite[i].sectnum].lotag = 50;
				sector[sprite[i].sectnum].hitag = sprite[i].hitag;
				if (sector[sprite[i].sectnum].hitag == 0)
					sector[sprite[i].sectnum].hitag = 1;
			}

			if ((spr.cstat & (16 + 32)) == (16 + 32))
				spr.cstat &= ~(16 | 32);

			if (spr.picnum == SPAWN) {
				engine.deletesprite(i);
			}

			if (spr.picnum == TORCH) {
				spr.cstat &= ~3;
				engine.changespritestat(i, TORCHLIGHT);
			}

			if (spr.picnum == STANDINTORCH || spr.picnum == BOWLOFFIRE) {
				engine.changespritestat(i, TORCHLIGHT);
			}

			if (spr.picnum == GLOW) {
				engine.changespritestat(i, GLOWLIGHT);
			}

			if (spr.picnum == SNDEFFECT) {
				sector[spr.sectnum].extra = spr.lotag;
				engine.deletesprite(i);
			}

			if (spr.picnum == SNDLOOP) { // loop on
				sector[spr.sectnum].extra = (short) (32768 | (spr.lotag << 1) | 1);
				engine.deletesprite(i);
			}

			if (spr.picnum == SNDLOOPOFF) { // loop off
				sector[spr.sectnum].extra = (short) (32768 | (spr.lotag << 1));
				engine.deletesprite(i);
			}

			if (spr.lotag == 80) {
				ironbarsector[ironbarscnt] = spr.sectnum;
				ironbarsdone[ironbarscnt] = 0;
				ironbarsanim[ironbarscnt] = i;
				ironbarsgoal[ironbarscnt] = 0;
				ironbarscnt++;
			}

			if (game.WH2) {
				switch (spr.picnum) {
				case WH2HELMET:
					spr.detail = HELMETTYPE;
					break;
				case WH2PLATEARMOR:
					spr.detail = PLATEARMORTYPE;
					break;
				case WH2CHAINMAIL:
					spr.detail = CHAINMAILTYPE;
					break;
				case WH2LEATHERARMOR:
					spr.detail = LEATHERARMORTYPE;
					break;
				case WH2PENTAGRAM:
					spr.detail = PENTAGRAMTYPE;
					break;
				case WH2CRYSTALSTAFF:
					spr.detail = CRYSTALSTAFFTYPE;
					break;
				case WH2AMULETOFTHEMIST:
					spr.detail = AMULETOFTHEMISTTYPE;
					break;
				case WH2HORNEDSKULL:
					spr.detail = HORNEDSKULLTYPE;
					break;
				case WH2THEHORN:
					spr.detail = THEHORNTYPE;
					break;
				case WH2BRASSKEY:
					spr.detail = BRASSKEYTYPE;
					break;
				case WH2BLACKKEY:
					spr.detail = BLACKKEYTYPE;
					break;
				case WH2GLASSKEY:
					spr.detail = GLASSKEYTYPE;
					break;
				case WH2IVORYKEY:
					spr.detail = IVORYKEYTYPE;
					break;
				case WH2SCROLLSCARE:
					spr.detail = SCROLLSCARETYPE;
					break;
				case WH2SCROLLNIGHT:
					spr.detail = SCROLLNIGHTTYPE;
					break;
				case WH2SCROLLFREEZE:
					spr.detail = SCROLLFREEZETYPE;
					break;
				case WH2SCROLLMAGIC:
					spr.detail = SCROLLMAGICTYPE;
					break;
				case WH2SCROLLOPEN:
					spr.detail = SCROLLOPENTYPE;
					break;
				case WH2SCROLLFLY:
					spr.detail = SCROLLFLYTYPE;
					break;
				case WH2SCROLLFIREBALL:
					spr.detail = SCROLLFIREBALLTYPE;
					break;
				case WH2SCROLLNUKE:
					spr.detail = SCROLLNUKETYPE;
					break;
				case WH2QUIVER:
					spr.detail = QUIVERTYPE;
					break;
				case WALLBOW:
				case WH2BOW:
					spr.detail = BOWTYPE;
					break;
				case WH2WEAPON1:
					spr.detail = WEAPON1TYPE;
					break;
				case WH2WEAPON1A:
					spr.detail = WEAPON1ATYPE;
					break;
				case WH2GOBWEAPON:
					spr.detail = GOBWEAPONTYPE;
					break;
				case WH2WEAPON2:
					spr.detail = WEAPON2TYPE;
					break;
				case WALLAXE:
				case WH2WEAPON4:
					spr.detail = WEAPON4TYPE;
					break;
				case WH2THROWHALBERD:
					spr.detail = THROWHALBERDTYPE;
					break;
				case WH2WEAPON5:
					spr.detail = WEAPON5TYPE;
					break;
				case WH2SHIELD:
					spr.detail = SHIELDTYPE;
					break;
				case WH2WEAPON5B:
					spr.detail = WEAPON5BTYPE;
					break;
				case WALLPIKE:
				case WH2THROWPIKE + 1:
					spr.detail = WALLPIKETYPE;
					break;
				case WH2WEAPON6:
					spr.detail = WEAPON6TYPE;
					break;
				case WH2WEAPON7:
					spr.detail = WEAPON7TYPE;
					break;
				case WEAPON8:
					spr.detail = WEAPON8TYPE;
					break;
				}
			} else {
				switch (spr.picnum) {
				case WH1HELMET:
					spr.detail = HELMETTYPE;
					break;
				case WH1PLATEARMOR:
					spr.detail = PLATEARMORTYPE;
					break;
				case WH1CHAINMAIL:
					spr.detail = CHAINMAILTYPE;
					break;
				case WH1LEATHERARMOR:
					spr.detail = LEATHERARMORTYPE;
					break;
				case WH1PENTAGRAM:
					spr.detail = PENTAGRAMTYPE;
					break;
				case WH1CRYSTALSTAFF:
					spr.detail = CRYSTALSTAFFTYPE;
					break;
				case WH1AMULETOFTHEMIST:
					spr.detail = AMULETOFTHEMISTTYPE;
					break;
				case WH1HORNEDSKULL:
					spr.detail = HORNEDSKULLTYPE;
					break;
				case WH1THEHORN:
					spr.detail = THEHORNTYPE;
					break;
				case WH1BRASSKEY:
					spr.detail = BRASSKEYTYPE;
					break;
				case WH1BLACKKEY:
					spr.detail = BLACKKEYTYPE;
					break;
				case WH1GLASSKEY:
					spr.detail = GLASSKEYTYPE;
					break;
				case WH1IVORYKEY:
					spr.detail = IVORYKEYTYPE;
					break;
				case WH1SCROLLSCARE:
					spr.detail = SCROLLSCARETYPE;
					break;
				case WH1SCROLLNIGHT:
					spr.detail = SCROLLNIGHTTYPE;
					break;
				case WH1SCROLLFREEZE:
					spr.detail = SCROLLFREEZETYPE;
					break;
				case WH1SCROLLMAGIC:
					spr.detail = SCROLLMAGICTYPE;
					break;
				case WH1SCROLLOPEN:
					spr.detail = SCROLLOPENTYPE;
					break;
				case WH1SCROLLFLY:
					spr.detail = SCROLLFLYTYPE;
					break;
				case WH1SCROLLFIREBALL:
					spr.detail = SCROLLFIREBALLTYPE;
					break;
				case WH1SCROLLNUKE:
					spr.detail = SCROLLNUKETYPE;
					break;
				case WH1QUIVER:
					spr.detail = QUIVERTYPE;
					break;
				case WALLBOW:
				case WH1BOW:
					spr.detail = BOWTYPE;
					break;
				case WH1WEAPON1:
					spr.detail = WEAPON1TYPE;
					break;
				case WH1WEAPON1A:
					spr.detail = WEAPON1ATYPE;
					break;
				case WH1GOBWEAPON:
					spr.detail = GOBWEAPONTYPE;
					break;
				case WH1WEAPON2:
					spr.detail = WEAPON2TYPE;
					break;
				case WALLAXE:
				case WH1WEAPON4:
					spr.detail = WEAPON4TYPE;
					break;
				case WH1THROWHALBERD:
					spr.detail = THROWHALBERDTYPE;
					break;
				case WH1WEAPON5:
					spr.detail = WEAPON5TYPE;
					break;
				case WH1SHIELD:
					spr.detail = SHIELDTYPE;
					break;
				case WH1WEAPON5B:
					spr.detail = WEAPON5BTYPE;
					break;
				case WALLPIKE:
				case WH1THROWPIKE + 1:
					spr.detail = WALLPIKETYPE;
					break;
				case WH1WEAPON6:
					spr.detail = WEAPON6TYPE;
					break;
				case WH1WEAPON7:
					spr.detail = WEAPON7TYPE;
					break;
				}
			}

			switch (spr.picnum) {
			case SILVERBAG:
			case SILVERCOINS:
				spr.detail = SILVERBAGTYPE;
				break;
			case GOLDBAG:
			case GOLDBAG2:
			case GOLDCOINS:
			case GOLDCOINS2:
				spr.detail = GOLDBAGTYPE;
				break;
			case GIFTBOX:
				spr.detail = GIFTBOXTYPE;
				break;
			case FLASKBLUE:
				spr.detail = FLASKBLUETYPE;
				break;
			case FLASKRED:
				spr.detail = FLASKREDTYPE;
				break;
			case FLASKGREEN:
				spr.detail = FLASKGREENTYPE;
				break;
			case FLASKOCHRE:
				spr.detail = FLASKOCHRETYPE;
				break;
			case FLASKTAN:
				spr.detail = FLASKTANTYPE;
				break;
			case DIAMONDRING:
				spr.detail = DIAMONDRINGTYPE;
				break;
			case SHADOWAMULET:
				spr.detail = SHADOWAMULETTYPE;
				break;
			case GLASSSKULL:
				spr.detail = GLASSSKULLTYPE;
				break;
			case AHNK:
				spr.detail = AHNKTYPE;
				break;
			case BLUESCEPTER:
				spr.detail = BLUESCEPTERTYPE;
				break;
			case YELLOWSCEPTER:
				spr.detail = YELLOWSCEPTERTYPE;
				break;
			case ADAMANTINERING:
				spr.detail = ADAMANTINERINGTYPE;
				break;
			case ONYXRING:
				spr.detail = ONYXRINGTYPE;
				break;
			case SAPHIRERING:
				spr.detail = SAPHIRERINGTYPE;
				break;
			case WALLSWORD:
			case WEAPON3A:
				spr.detail = WEAPON3ATYPE;
				break;
			case WEAPON3:
				spr.detail = WEAPON3TYPE;
				break;
			case GONZOBSHIELD:
			case GONZOCSHIELD:
			case GONZOGSHIELD:
				spr.detail = GONZOSHIELDTYPE;
				break;
			case SPIKEBLADE:
				spr.detail = SPIKEBLADETYPE;
				break;
			case SPIKE:
				spr.detail = SPIKETYPE;
				break;
			case SPIKEPOLE:
				spr.detail = SPIKEPOLETYPE;
				break;
			case MONSTERBALL:
				spr.detail = MONSTERBALLTYPE;
				break;
			case WH1HANGMAN + 1:
			case WH2HANGMAN + 1:
				if ((spr.picnum == (WH1HANGMAN + 1) && game.WH2) || (spr.picnum == WH2HANGMAN + 1 && !game.WH2))
					break;

				spr.xrepeat = 28;
				spr.yrepeat = 28;
				break;
			case GOBLINDEAD:
				if (game.WH2)
					break;
				spr.xrepeat = 36;
				spr.yrepeat = 36;
				break;
			case STONEGONZOBSH:
			case STONEGONZOBSW2:
			case STONEGONZOCHM:
			case STONEGONZOGSH:
			case STONEGRONDOVAL:
			case STONEGONZOBSW:
				spr.xrepeat = 24;
				spr.yrepeat = 24;
				break;
			case GONZOHMJUMP:
			case GONZOSHJUMP:
				spr.xrepeat = 24;
				spr.yrepeat = 24;
				spr.clipdist = 32;
				spr.extra = spr.lotag;
				spr.lotag = 20;
				if (spr.extra == 3) {
					spr.lotag = 80;
				}
				spr.cstat |= 0x101;
				break;

			case PINE:
				spr.xrepeat = treesize = (short) (((engine.krand() % 5) + 3) << 4);
				spr.yrepeat = treesize;
				break;

			case GYSER:
				if (game.WH2)
					break;
				spr.xrepeat = 32;
				spr.yrepeat = 18;
				spr.shade = -17;
				spr.pal = 0;
				spr.detail = GYSERTYPE;
				// engine.changespritestat(i,DORMANT);
				break;
			case PATROLPOINT:
				spr.xrepeat = 24;
				spr.yrepeat = 32;

				spr.cstat &= ~3;
				spr.cstat |= 0x8000;
				spr.clipdist = 4;
				engine.changespritestat(i, APATROLPOINT);
				break;
			case BARREL:
			case VASEA:
			case VASEB:
			case VASEC:
			case STAINGLASS1:
			case STAINGLASS2:
			case STAINGLASS3:
			case STAINGLASS4:
			case STAINGLASS5:
			case STAINGLASS6:
			case STAINGLASS7:
			case STAINGLASS8:
			case STAINGLASS9:
				spr.cstat |= 0x101;
				spr.clipdist = 64;
				break;
			case 2232: // team flags
				if (!game.WH2)
					break;
//               netmarkflag(i);
				break;
			case 2233: // team flags
				if (game.WH2)
					break;
				// XXX netmarkflag(i);
				break;
			}

			if (isItemSprite(i)) {
				Item item = items[(spr.detail & 0xFF) - ITEMSBASE];
				if (item.sizx != -1 && item.sizy != -1) {
					spr.xrepeat = (short) item.sizx;
					spr.yrepeat = (short) item.sizy;
				}

				if (item.treasures)
					treasurescnt++;
				spr.cstat &= ~1;
				if (item.cflag)
					spr.cstat &= ~3;
			}

		}

		preparesectors();

		automapping = 1;

		if (game.WH2) {
			if (mapon == 5) {
				SPRITE spr = sprite[185];
				if (spr != null && spr.picnum == 172 && spr.x == -36864 && spr.y == -53504)
					engine.deletesprite((short) 185);
			}

			if (mapon == 13) {
				if (sector[427] != null && sector[427].floorpicnum == 291) {
					int s = engine.insertsprite((short) 427, (short) 0);
					if (s != -1) {
						SPRITE sp = sprite[s];
						sp.x = 27136;
						sp.y = 51712;
						sp.z = 7168;
						sp.picnum = WH2PENTAGRAM;
						sp.cstat = 515;
						sp.shade = -3;
						sp.xrepeat = sp.yrepeat = 64;
					}
				}
			}
		}

		if (justteleported) { // next level
			plr.hvel = 0;
			plr.spritenum = engine.insertsprite(plr.sector, (short) 0);
			plr.oldsector = plr.sector;

			sprite[plr.spritenum].x = plr.x;
			sprite[plr.spritenum].y = plr.y;
			sprite[plr.spritenum].z = sector[plr.sector].floorz;
			sprite[plr.spritenum].cstat = 1 + 256;
			sprite[plr.spritenum].picnum = game.WH2 ? GRONSW : FRED;
			sprite[plr.spritenum].shade = 0;
			sprite[plr.spritenum].xrepeat = 36;
			sprite[plr.spritenum].yrepeat = 36;
			sprite[plr.spritenum].ang = (short) plr.ang;
			sprite[plr.spritenum].xvel = 0;
			sprite[plr.spritenum].yvel = 0;
			sprite[plr.spritenum].zvel = 0;
			sprite[plr.spritenum].owner = (short) (4096 + myconnectindex);
			sprite[plr.spritenum].lotag = 0;
			sprite[plr.spritenum].hitag = 0;
			sprite[plr.spritenum].pal = (short) (game.WH2 ? 10 : 1);
			if (game.WH2)
				sprite[plr.spritenum].clipdist = 48;

			engine.setsprite(plr.spritenum, plr.x, plr.y, plr.z + (getPlayerHeight() << 8));

			warpfxsprite(plr.spritenum);
			plr.treasure[TBRASSKEY] = plr.treasure[TBLACKKEY] = plr.treasure[TGLASSKEY] = plr.treasure[TIVORYKEY] = 0;
			plr.treasure[TBLUESCEPTER] = plr.treasure[TYELLOWSCEPTER] = 0;

			// protection from missile
			// anit-missile for level only
			// dont forget to cleanup values
			plr.treasure[TONYXRING] = 0;
			SND_CheckLoops();

			justteleported = false;
		} else {
			initplayersprite(plr);
			dimension = 3;
			SND_Sound(S_SCARYDUDE);
		}

		if (nextlevel) {
			gAutosaveRequest = true;
			nextlevel = false;
		}

		if (mUserFlag != UserFlag.UserMap)
			startmusic(mapon - 1);

		return true;
	}

	/*
	 * public static void searchmap(short startsector) { int i, j, dasect, splc,
	 * send, startwall, endwall; short dapic; WALL wal;
	 *
	 * if ((startsector < 0) || (startsector >= numsectors)) return; for (i = 0; i <
	 * (MAXSECTORS >> 3); i++) show2dsector[i] = 0; for (i = 0; i < (MAXWALLS >> 3);
	 * i++) show2dwall[i] = 0; for (i = 0; i < (MAXSPRITES >> 3); i++)
	 * show2dsprite[i] = 0;
	 *
	 * automapping = 1;
	 *
	 * // Search your area recursively & set all show2dsector/show2dwalls
	 * tempshort[0] = startsector; if (automapping == 0) { show2dsector[startsector
	 * >> 3] |= (1 << (startsector & 7)); } dapic =
	 * sector[startsector].ceilingpicnum; if (waloff[dapic] == null)
	 * engine.loadtile(dapic); dapic = sector[startsector].floorpicnum; if
	 * (waloff[dapic] == null) engine.loadtile(dapic); for (splc = 0, send = 1; splc
	 * < send; splc++) { dasect = tempshort[splc]; startwall =
	 * sector[dasect].wallptr; endwall = startwall + sector[dasect].wallnum; for (i
	 * = startwall, wal = wall[startwall]; i < endwall; i++, wal++) { if
	 * (automapping == 0) { show2dwall[i >> 3] |= (1 << (i & 7)); } dapic =
	 * wall[i].picnum; if (waloff[dapic] == null) engine.loadtile(dapic); dapic =
	 * wall[i].overpicnum; if (((dapic & 0xfffff000) == 0) && (waloff[dapic] ==
	 * null)) engine.loadtile(dapic);
	 *
	 * j = wal.nextsector; if ((j >= 0) && ((show2dsector[j >> 3] & (1 << (j & 7)))
	 * == 0)) { show2dsector[j >> 3] |= (1 << (j & 7)); dapic =
	 * sector[j].ceilingpicnum; if (waloff[dapic] == null) engine.loadtile(dapic);
	 * dapic = sector[j].floorpicnum; if (waloff[dapic] == null)
	 * engine.loadtile(dapic);
	 *
	 * tempshort[send++] = (short) j; } }
	 *
	 * for (i = headspritesect[dasect]; i >= 0; i = nextspritesect[i]) { if
	 * (automapping == 0) { show2dsprite[i >> 3] |= (1 << (i & 7)); } dapic =
	 * sprite[i].picnum; if (waloff[dapic] == null) engine.loadtile(dapic); } } }
	 *
	 */

}
