package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPRX_STAY_PUT_VATOR;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SP_TAG1;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL5;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL6;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Morth.SOBJ_AlignCeilingToPoint;
import static ru.m210projects.Wang.Morth.SOBJ_AlignFloorToPoint;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_SPIKE;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sector.AnimateSwitch;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Stag.SECT_SPIKE;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_SWITCH_VATOR;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Vator.InterpSectorSprites;
import static ru.m210projects.Wang.Vator.VatorSwitch;
import static ru.m210projects.Wang.Main.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class Spike {

	public static void ReverseSpike(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// if paused go ahead and start it up again
		if (u.Tics != 0) {
			u.Tics = 0;
			SetSpikeActive(SpriteNum);
			return;
		}

		// moving toward to OFF pos
		if (u.z_tgt == u.oz) {
			if (sp.z == u.oz)
				u.z_tgt = u.sz;
			else if (u.sz == u.oz)
				u.z_tgt = sp.z;
		} else if (u.z_tgt == u.sz) {
			if (sp.z == u.oz)
				u.z_tgt = sp.z;
			else if (u.sz == u.oz)
				u.z_tgt = u.sz;
		}

		u.vel_rate = (short) -u.vel_rate;
	}

	public static boolean SpikeSwitch(int match, int setting) {
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

	public static void SetSpikeActive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		InterpSectorSprites(sp.sectnum, true);

		// play activate sound
		DoSoundSpotMatch(SP_TAG2(sp), 1, SoundType.SOUND_OBJECT_TYPE);

		u.Flags |= (SPR_ACTIVE);
		u.Tics = 0;

		// moving to the ON position
		if (u.z_tgt == sp.z)
			VatorSwitch(SP_TAG2(sp), ON);
		else
		// moving to the OFF position
		if (u.z_tgt == u.sz)
			VatorSwitch(SP_TAG2(sp), OFF);
	}

	public static void SetSpikeInactive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		InterpSectorSprites(sp.sectnum, false);

		// play activate sound
		DoSoundSpotMatch(SP_TAG2(sp), 2, SoundType.SOUND_OBJECT_TYPE);

		u.Flags &= ~(SPR_ACTIVE);
	}

	// called for operation from the space bar
	public static short DoSpikeOperate(PlayerStr pp, short sectnum) {
		SPRITE fsp;
		short match;
		short i, nexti;

		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			fsp = sprite[i];

			if (fsp.statnum == STAT_SPIKE && SP_TAG1(fsp) == SECT_SPIKE && SP_TAG3(fsp) == 0) {
				sectnum = fsp.sectnum;

				match = (short) SP_TAG2(fsp);
				if (match > 0) {
					if (TestSpikeMatchActive(match))
						return (-1);
					else
						return (DoSpikeMatch(pp, match));
				}

				SetSpikeActive(i);
				break;
			}
		}

		return (i);
	}

	// called from switches and triggers
	// returns first spike found
	public static short DoSpikeMatch(PlayerStr pp, int match) {
		USER fu;
		SPRITE fsp;
		short first_spike = -1;

		short i, nexti;

		for (i = headspritestat[STAT_SPIKE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_SPIKE && SP_TAG2(fsp) == match) {
				fu = pUser[i];

				if (first_spike == -1)
					first_spike = i;

				if (TEST(fu.Flags, SPR_ACTIVE)) {
					ReverseSpike(i);
					continue;
				}

				SetSpikeActive(i);
			}
		}

		return (first_spike);
	}

	public static boolean TestSpikeMatchActive(int match) {
		USER fu;
		SPRITE fsp;

		short i, nexti;

		for (i = headspritestat[STAT_SPIKE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_SPIKE && SP_TAG2(fsp) == match) {
				fu = pUser[i];

				// door war
				if (TEST_BOOL6(fsp))
					continue;

				if (TEST(fu.Flags, SPR_ACTIVE) || fu.Tics != 0)
					return (true);
			}
		}

		return (false);
	}

	public static int DoSpikeMove(int SpriteNum, int lptr) {
		USER u = pUser[SpriteNum];
		int zval = lptr;

		// if LESS THAN goal
		if (zval < u.z_tgt) {
			// move it DOWN
			zval += (synctics * u.jump_speed);

			u.jump_speed += u.vel_rate * synctics;

			// if the other way make it equal
			if (zval > u.z_tgt)
				zval = u.z_tgt;
		}

		// if GREATER THAN goal
		if (zval > u.z_tgt) {
			// move it UP
			zval -= (synctics * u.jump_speed);

			u.jump_speed += u.vel_rate * synctics;

			if (zval < u.z_tgt)
				zval = u.z_tgt;
		}

		return zval;
	}

	public static void SpikeAlign(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// either work on single sector or all tagged in SOBJ
		if ((byte) SP_TAG7(sp) < 0) {
			if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP)) {
				game.pInt.setcheinuminterpolate(sp.sectnum, sector[sp.sectnum]);
				engine.alignceilslope(sp.sectnum, sp.x, sp.y, u.zclip);
			}
			else {
				game.pInt.setfheinuminterpolate(sp.sectnum, sector[sp.sectnum]);
				engine.alignflorslope(sp.sectnum, sp.x, sp.y, u.zclip);
			}
		} else {
			if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP))
				SOBJ_AlignCeilingToPoint(SectorObject[SP_TAG7(sp)], sp.x, sp.y, u.zclip);
			else
				SOBJ_AlignFloorToPoint(SectorObject[SP_TAG7(sp)], sp.x, sp.y, u.zclip);
		}
	}

	public static void MoveSpritesWithSpike(short sectnum) {
		SPRITE sp;
		short i, nexti;

		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			sp = sprite[i];

			if (pUser[i] != null)
				continue;

			if (TEST(sp.extra, SPRX_STAY_PUT_VATOR))
				continue;

			engine.getzsofslope(sectnum, sp.x, sp.y, zofslope);
			game.pInt.setsprinterpolate(i, sp);
			sp.z = zofslope[FLOOR];
		}
	}

	public static final Animator DoSpike = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoSpike(spr) != 0;
		}
	};

	public static int DoSpike(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		int lptr;

		// zclip = floor or ceiling z
		// oz = original z
		// z_tgt = target z - on pos
		// sz = starting z - off pos

		u.zclip = lptr = DoSpikeMove(SpriteNum, u.zclip);
		MoveSpritesWithSpike(sp.sectnum);
		SpikeAlign(SpriteNum);

		// EQUAL this entry has finished
		if (lptr == u.z_tgt) {
			// in the ON position
			if (u.z_tgt == sp.z) {
				// change target
				u.z_tgt = u.sz;
				u.vel_rate = (short) -u.vel_rate;

				SetSpikeInactive(SpriteNum);

				if (SP_TAG6(sp) != 0)
					DoMatchEverything(null, SP_TAG6(sp), -1);
			} else
			// in the OFF position
			if (u.z_tgt == u.sz) {
				int match = SP_TAG2(sp);

				// change target
				u.jump_speed = (short) u.vel_tgt;
				u.vel_rate = (short) klabs(u.vel_rate);
				u.z_tgt = sp.z;

				SetSpikeInactive(SpriteNum);

				// set owner swith back to OFF
				// only if ALL spikes are inactive
				if (!TestSpikeMatchActive(match)) {
					// SpikeSwitch(match, OFF);
				}

				if (SP_TAG6(sp) != 0 && TEST_BOOL5(sp))
					DoMatchEverything(null, SP_TAG6(sp), -1);
			}

			// operate only once
			if (TEST_BOOL2(sp)) {
				SetSpikeInactive(SpriteNum);
				KillSprite(SpriteNum);
				return (0);
			}

			// setup to go back to the original z
			if (lptr != u.oz) {
				if (u.WaitTics != 0)
					u.Tics = u.WaitTics;
			}
		} else {
			// if heading for the OFF (original) position and should NOT CRUSH
			if (TEST_BOOL3(sp) && u.z_tgt == u.oz) {
				int i, nexti;
				SPRITE bsp;
				USER bu;
				boolean found = false;

				for (i = headspritesect[sp.sectnum]; i != -1; i = nexti) {
					nexti = nextspritesect[i];
					bsp = sprite[i];
					bu = pUser[i];

					if (bu != null && TEST(bsp.cstat, CSTAT_SPRITE_BLOCK) && TEST(bsp.extra, SPRX_PLAYER_OR_ENEMY)) {
						ReverseSpike(SpriteNum);
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
							ReverseSpike(SpriteNum);
							found = true;
						}
					}
				}
			}
		}

		return (0);
	}

	public static final Animator DoSpikeAuto = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoSpikeAuto(spr) != 0;
		}
	};

	public static int DoSpikeAuto(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		int lptr;

		u.zclip = lptr = DoSpikeMove(SpriteNum, u.zclip);
		MoveSpritesWithSpike(sp.sectnum);
		SpikeAlign(SpriteNum);

		// EQUAL this entry has finished
		if (lptr == u.z_tgt) {
			// in the UP position
			if (u.z_tgt == sp.z) {
				// change target
				u.z_tgt = u.sz;
				u.vel_rate = (short) -u.vel_rate;
				u.Tics = u.WaitTics;

				if (SP_TAG6(sp) != 0)
					DoMatchEverything(null, SP_TAG6(sp), -1);
			} else
			// in the DOWN position
			if (u.z_tgt == u.sz) {
				// change target
				u.jump_speed = (short) u.vel_tgt;
				u.vel_rate = (short) klabs(u.vel_rate);
				u.z_tgt = sp.z;
				u.Tics = u.WaitTics;

				if (SP_TAG6(sp) != 0 && TEST_BOOL5(sp))
					DoMatchEverything(null, SP_TAG6(sp), -1);
			}
		}

		return (0);
	}

}
