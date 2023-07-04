package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.RESET_BOOL8;
import static ru.m210projects.Wang.Gameutils.SET_BOOL8;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SP_TAG1;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL11;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL4;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL8;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_SLIDOR;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Sector.AnimateSwitch;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Stag.SECT_LOCK_DOOR;
import static ru.m210projects.Wang.Stag.SECT_SLIDOR;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_SWITCH_VATOR;
import static ru.m210projects.Wang.Tags.TAG_WALL_SLIDOR_DOWN;
import static ru.m210projects.Wang.Tags.TAG_WALL_SLIDOR_LEFT;
import static ru.m210projects.Wang.Tags.TAG_WALL_SLIDOR_RIGHT;
import static ru.m210projects.Wang.Tags.TAG_WALL_SLIDOR_UP;
import static ru.m210projects.Wang.Text.KeyDoorMessage;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Vator.VatorSwitch;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.RotatorStr;
import ru.m210projects.Wang.Type.USER;

public class Slidor {

	public static void ReverseSlidor(int SpriteNum) {
		USER u = pUser[SpriteNum];
		RotatorStr r;

		r = u.rotator;

		// if paused go ahead and start it up again
		if (u.Tics != 0) {
			u.Tics = 0;
			SetSlidorActive(SpriteNum);
			return;
		}

		// moving toward to OFF pos
		if (r.tgt == 0) {
			r.tgt = r.open_dest;
		} else if (r.tgt == r.open_dest) {
			r.tgt = 0;
		}

		r.vel = -r.vel;
	}

	public static boolean SlidorSwitch(int match, int setting) {
		SPRITE sp;
		short i, nexti;
		boolean found = false;

		for (i = headspritestat[STAT_DEFAULT]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == TAG_SPRITE_SWITCH_VATOR && sp.hitag == match) {
				found = true;
				AnimateSwitch(sp, setting);
			}
		}

		return (found);
	}

	public static void SetSlidorActive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		RotatorStr r;

		r = u.rotator;

		// play activate sound
		DoSoundSpotMatch(SP_TAG2(sp), 1, SoundType.SOUND_OBJECT_TYPE);

		u.Flags |= (SPR_ACTIVE);
		u.Tics = 0;

		// moving to the OFF position
		if (r.tgt == 0)
			VatorSwitch(SP_TAG2(sp), OFF);
		else
			VatorSwitch(SP_TAG2(sp), ON);
	}

	public static void SetSlidorInactive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// play inactivate sound
		DoSoundSpotMatch(SP_TAG2(sp), 2, SoundType.SOUND_OBJECT_TYPE);

		u.Flags &= ~(SPR_ACTIVE);
	}

	// called for operation from the space bar
	public static short DoSlidorOperate(PlayerStr pp, short sectnum) {
		short match = sector[sectnum].hitag;

		if (match > 0) {
			if (TestSlidorMatchActive(match))
				return (-1);
			else
				return (DoSlidorMatch(pp, match, true));
		}

		return (-1);
	}

	// called from switches and triggers
	// returns first vator found
	public static short DoSlidorMatch(PlayerStr pp, int match, boolean manual) {
		USER fu;
		SPRITE fsp;
		short sectnum;
		short first_vator = -1;

		short i, nexti;

		for (i = headspritestat[STAT_SLIDOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_SLIDOR && SP_TAG2(fsp) == match) {
				fu = pUser[i];

				// single play only vator
				// boolean 8 must be set for message to display
				if (TEST_BOOL4(fsp) && (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT
						|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_AI_BOTS)) {
					if (pp != null && TEST_BOOL11(fsp))
						PutStringInfo(pp, "This only opens in single play.");
					continue;
				}

				// switch trigger only
				if (SP_TAG3(fsp) == 1) {
					// tried to manually operat a switch/trigger only
					if (manual)
						continue;
				}

				if (first_vator == -1)
					first_vator = i;

				sectnum = fsp.sectnum;

				if (pp != null && SectUser[sectnum] != null && SectUser[sectnum].stag == SECT_LOCK_DOOR
						&& SectUser[sectnum].number != 0) {
					short key_num;

					key_num = SectUser[sectnum].number;

					PutStringInfo(pp, KeyDoorMessage[key_num - 1]);
					return (-1);

				}

				if (TEST(fu.Flags, SPR_ACTIVE)) {
					ReverseSlidor(i);
					continue;
				}

				SetSlidorActive(i);
			}
		}

		return (first_vator);
	}

	public static boolean TestSlidorMatchActive(int match) {
		USER fu;
		SPRITE fsp;

		short i, nexti;

		for (i = headspritestat[STAT_SLIDOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];

			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_SLIDOR && SP_TAG2(fsp) == match) {
				fu = pUser[i];

				// Does not have to be inactive to be operated
				if (TEST_BOOL6(fsp))
					continue;

				if (TEST(fu.Flags, SPR_ACTIVE) || fu.Tics != 0)
					return (true);
			}
		}

		return (false);
	}

	public static int DoSlidorMoveWalls(int SpriteNum, int amt) {
		int pw, startwall, endwall;

		short w = (short) (startwall = sector[sprite[SpriteNum].sectnum].wallptr);
		endwall = startwall + sector[sprite[SpriteNum].sectnum].wallnum - 1;

		do {
			switch (wall[w].lotag) {
			case TAG_WALL_SLIDOR_LEFT:

				// prev wall
				pw = w - 1;
				if (w < startwall)
					pw = endwall;

				if (wall[w].nextwall < 0) {
					// white wall - move 4 points
					engine.dragpoint(w, wall[w].x - amt, wall[w].y);
					engine.dragpoint((short) pw, wall[pw].x - amt, wall[pw].y);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x - amt, wall[wall[w].point2].y);
					engine.dragpoint(wall[wall[w].point2].point2, wall[wall[wall[w].point2].point2].x - amt,
							wall[wall[wall[w].point2].point2].y);
				} else {
					// red wall - move 2 points
					engine.dragpoint(w, wall[w].x - amt, wall[w].y);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x - amt, wall[wall[w].point2].y);
				}

				break;

			case TAG_WALL_SLIDOR_RIGHT:

				// prev wall
				pw = w - 1;
				if (w < startwall)
					pw = endwall;

				if (wall[w].nextwall < 0) {
					// white wall - move 4 points
					engine.dragpoint(w, wall[w].x + amt, wall[w].y);
					engine.dragpoint((short) pw, wall[pw].x + amt, wall[pw].y);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x + amt, wall[wall[w].point2].y);
					engine.dragpoint(wall[wall[w].point2].point2, wall[wall[wall[w].point2].point2].x + amt,
							wall[wall[wall[w].point2].point2].y);
				} else {
					// red wall - move 2 points
					engine.dragpoint(w, wall[w].x + amt, wall[w].y);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x + amt, wall[wall[w].point2].y);
				}

				break;

			case TAG_WALL_SLIDOR_UP:

				// prev wall
				pw = w - 1;
				if (w < startwall)
					pw = endwall;

				if (wall[w].nextwall < 0) {
					engine.dragpoint(w, wall[w].x, wall[w].y - amt);
					engine.dragpoint((short) pw, wall[pw].x, wall[pw].y - amt);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x, wall[wall[w].point2].y - amt);
					engine.dragpoint(wall[wall[w].point2].point2, wall[wall[wall[w].point2].point2].x,
							wall[wall[wall[w].point2].point2].y - amt);
				} else {
					engine.dragpoint(w, wall[w].x, wall[w].y - amt);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x, wall[wall[w].point2].y - amt);
				}

				break;

			case TAG_WALL_SLIDOR_DOWN:

				// prev wall
				pw = w - 1;
				if (w < startwall)
					pw = endwall;

				if (wall[w].nextwall < 0) {
					engine.dragpoint(w, wall[w].x, wall[w].y + amt);
					engine.dragpoint((short) pw, wall[pw].x, wall[pw].y + amt);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x, wall[wall[w].point2].y + amt);
					engine.dragpoint(wall[wall[w].point2].point2, wall[wall[wall[w].point2].point2].x,
							wall[wall[wall[w].point2].point2].y + amt);
				} else {
					engine.dragpoint(w, wall[w].x, wall[w].y + amt);
					engine.dragpoint(wall[w].point2, wall[wall[w].point2].x, wall[wall[w].point2].y + amt);
				}

				break;
			}

			w = wall[w].point2;
		} while (w != startwall);

		return (0);
	}

	public static int DoSlidorInstantClose(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		int w, startwall;
		int diff;

		w = startwall = sector[sprite[SpriteNum].sectnum].wallptr;

		do {
			switch (wall[w].lotag) {
			case TAG_WALL_SLIDOR_LEFT:
				diff = wall[w].x - sp.x;
				DoSlidorMoveWalls(SpriteNum, diff);
				break;

			case TAG_WALL_SLIDOR_RIGHT:
				diff = wall[w].x - sp.x;
				DoSlidorMoveWalls(SpriteNum, -diff);
				break;

			case TAG_WALL_SLIDOR_UP:
				diff = wall[w].y - sp.y;
				DoSlidorMoveWalls(SpriteNum, diff);
				break;

			case TAG_WALL_SLIDOR_DOWN:
				diff = wall[w].y - sp.y;
				DoSlidorMoveWalls(SpriteNum, -diff);
				break;
			}

			w = wall[w].point2;
		} while (w != startwall);

		return (0);
	}

	public static int DoSlidorMove(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		RotatorStr r;
		int i, nexti;
		int old_pos;
		boolean kill = false;

		r = u.rotator;

		// Example - ang pos moves from 0 to 512 <<OR>> from 0 to -512

		old_pos = r.pos;

		// control SPEED of swinging
		if (r.pos < r.tgt) {
			// Increment swing angle
			r.pos += r.speed;
			r.speed += r.vel;

			// if the other way make it equal
			if (r.pos > r.tgt)
				r.pos = r.tgt;
		}

		if (r.pos > r.tgt) {
			// Increment swing angle
			r.pos -= r.speed;
			r.speed += r.vel;

			// if the other way make it equal
			if (r.pos < r.tgt)
				r.pos = r.tgt;
		}

		if (r.pos == r.tgt) {
			// If ang is OPEN
			if (r.pos == r.open_dest) {
				// new tgt is CLOSED (0)
				r.tgt = 0;
				r.vel = -r.vel;
				SetSlidorInactive(SpriteNum);

				if (SP_TAG6(sp) != 0 && !TEST_BOOL8(sp))
					DoMatchEverything(null, SP_TAG6(sp), -1);

				// wait a bit and close it
				if (u.WaitTics != 0)
					u.Tics = u.WaitTics;
			} else
			// If ang is CLOSED then
			if (r.pos == 0) {
				int match = SP_TAG2(sp);

				// new tgt is OPEN (open)
				r.speed = r.orig_speed;
				r.vel = klabs(r.vel);

				r.tgt = r.open_dest;
				SetSlidorInactive(SpriteNum);

				RESET_BOOL8(sp);

				// set owner swith back to OFF
				// only if ALL vators are inactive
				if (!TestSlidorMatchActive(match)) {

				}

				if (SP_TAG6(sp) != 0 && TEST_BOOL8(sp))
					DoMatchEverything(null, SP_TAG6(sp), -1);
			}

			if (TEST_BOOL2(sp))
				kill = true;
		} else {
			// if heading for the OFF (original) position and should NOT CRUSH
			if (TEST_BOOL3(sp) && r.tgt == 0) {

				SPRITE bsp;
				USER bu;
				boolean found = false;

				for (i = headspritesect[sp.sectnum]; i != -1; i = nexti) {
					nexti = nextspritesect[i];
					bsp = sprite[i];
					bu = pUser[i];

					if (bu != null && TEST(bsp.cstat, CSTAT_SPRITE_BLOCK) && TEST(bsp.extra, SPRX_PLAYER_OR_ENEMY)) {
						// found something blocking so reverse to ON position
						ReverseSlidor(SpriteNum);
						SET_BOOL8(sp); // tell vator that something blocking door
						found = true;
						break;
					}
				}

				if (!found) {
					short pnum;
					PlayerStr pp;
					// go ahead and look for players clip box bounds
					for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
						pp = Player[pnum];

						if ((pp.lo_sectp != -1 && pp.lo_sectp == sp.sectnum)
								|| (pp.hi_sectp != -1 && pp.hi_sectp == sp.sectnum)) {
							ReverseSlidor(SpriteNum);

							u.vel_rate = (short) -u.vel_rate;
							found = true;
						}
					}
				}
			}
		}

		DoSlidorMoveWalls(SpriteNum, r.pos - old_pos);

		if (kill) {
			SetSlidorInactive(SpriteNum);
			KillSprite(SpriteNum);
			return (0);
		}

		return (0);
	}

	public static final Animator DoSlidor = new Animator() {
		@Override
		public boolean invoke(int spr) {
			DoSlidor(spr);
			return false;
		}
	};

	public static int DoSlidor(int SpriteNum) {
		DoSlidorMove(SpriteNum);

		return (0);
	}

}
