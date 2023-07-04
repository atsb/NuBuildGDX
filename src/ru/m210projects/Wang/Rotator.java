package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SP_TAG1;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL11;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL4;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL5;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL6;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_ROTATOR;
import static ru.m210projects.Wang.Names.STAT_ROTATOR_PIVOT;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Sector.AnimateSwitch;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Stag.SECT_LOCK_DOOR;
import static ru.m210projects.Wang.Stag.SECT_ROTATOR;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_SWITCH_VATOR;
import static ru.m210projects.Wang.Text.KeyDoorMessage;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Vator.VatorSwitch;

import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.RotatorStr;
import ru.m210projects.Wang.Type.USER;

public class Rotator {

	public static void ReverseRotator(short SpriteNum) {
		USER u = pUser[SpriteNum];
		RotatorStr r;

		r = u.rotator;

		// if paused go ahead and start it up again
		if (u.Tics != 0) {
			u.Tics = 0;
			SetRotatorActive(SpriteNum);
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

	public static boolean RotatorSwitch(short match, short setting) {
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

	public static void SetRotatorActive(int SpriteNum) {
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

	public static void SetRotatorInactive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// play inactivate sound
		DoSoundSpotMatch(SP_TAG2(sp), 2, SoundType.SOUND_OBJECT_TYPE);

		u.Flags &= ~(SPR_ACTIVE);
	}

// called for operation from the space bar
	public static short DoRotatorOperate(PlayerStr pp, short sectnum) {
		short match = sector[sectnum].hitag;

		if (match > 0) {
			if (TestRotatorMatchActive(match))
				return (-1);
			else
				return (DoRotatorMatch(pp, match, true));
		}

		return (-1);
	}

	// called from switches and triggers
	// returns first vator found
	public static short DoRotatorMatch(PlayerStr pp, int match, boolean manual) {
		USER fu;
		SPRITE fsp;
		short sectnum;
		short first_vator = -1;
		short i, nexti;

		for (i = headspritestat[STAT_ROTATOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_ROTATOR && SP_TAG2(fsp) == match) {
				fu = pUser[i];

				// single play only vator
				// boolean 8 must be set for message to display
				if (TEST_BOOL4(fsp) && (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT
						|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_AI_BOTS)) {
					if (pp != null && TEST_BOOL11(fsp)) {
						PutStringInfo(pp, "This only opens in single play.");
					}
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

					{
						PutStringInfo(pp, KeyDoorMessage[key_num - 1]);
						return (-1);
					}
				}

				if (TEST(fu.Flags, SPR_ACTIVE)) {
					ReverseRotator(i);
					continue;
				}

				SetRotatorActive(i);
			}
		}

		return (first_vator);
	}

	public static boolean TestRotatorMatchActive(int match) {
		USER fu;
		SPRITE fsp;

		short i, nexti;

		for (i = headspritestat[STAT_ROTATOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_ROTATOR && SP_TAG2(fsp) == match) {
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

	private static int DoRotatorMove(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		RotatorStr r;
		short ndx, w, startwall, endwall;
		SPRITE pivot = null;
		int i, nexti;
		int dist, closest;
		boolean kill = false;

		r = u.rotator;

		// Example - ang pos moves from 0 to 512 <<OR>> from 0 to -512

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
				SetRotatorInactive(SpriteNum);

				if (SP_TAG6(sp) != 0)
					DoMatchEverything(null, SP_TAG6(sp), -1);

				// wait a bit and close it
				if (u.WaitTics != 0)
					u.Tics = u.WaitTics;
			} else
			// If ang is CLOSED then
			if (r.pos == 0) {
				int match = SP_TAG2(sp);

				// new tgt is OPEN (open)
				r.tgt = r.open_dest;
				r.speed = r.orig_speed;
				r.vel = klabs(r.vel);

				SetRotatorInactive(SpriteNum);

				// set owner swith back to OFF
				// only if ALL vators are inactive
				if (!TestRotatorMatchActive(match)) {
					// RotatorSwitch(match, OFF);
				}

				if (SP_TAG6(sp) != 0 && TEST_BOOL5(sp))
					DoMatchEverything(null, SP_TAG6(sp), -1);
			}

			if (TEST_BOOL2(sp))
				kill = true;
		}

		closest = 99999;
		for (i = headspritestat[STAT_ROTATOR_PIVOT]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			if (sprite[i].lotag == sp.lotag) {
				dist = Distance(sp.x, sp.y, sprite[i].x, sprite[i].y);
				if (dist < closest) {
					closest = dist;
					pivot = sprite[i];
				}
			}
		}

		if (pivot == null)
			return (0);

		startwall = sector[sp.sectnum].wallptr;
		endwall = (short) (startwall + sector[sp.sectnum].wallnum - 1);

		// move points
		for (w = startwall, ndx = 0; w <= endwall; w++) {
			Point p = engine.rotatepoint(pivot.x, pivot.y, r.orig[ndx].x, r.orig[ndx].y, (short) r.pos);
			engine.dragpoint(w, p.getX(), p.getY());
			ndx++;
		}

		if (kill) {
			SetRotatorInactive(SpriteNum);
			KillSprite(SpriteNum);
			return (0);
		}

		return (0);
	}
	
	public static final Animator DoRotator = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoRotator(spr) != 0;
		}
	};

	public static int DoRotator(int SpriteNum) {
		// could move this inside sprite control
		DoRotatorMove(SpriteNum);

		return (0);
	}

}
