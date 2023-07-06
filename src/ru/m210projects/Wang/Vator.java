package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_CLOSE_FLOOR;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.RESET_BOOL8;
import static ru.m210projects.Wang.Gameutils.SECTFU_VATOR_BOTH;
import static ru.m210projects.Wang.Gameutils.SET_BOOL8;
import static ru.m210projects.Wang.Gameutils.SPRITE_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPRX_STAY_PUT_VATOR;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_SKIP2;
import static ru.m210projects.Wang.Gameutils.SPR_SKIP4;
import static ru.m210projects.Wang.Gameutils.SP_TAG1;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL11;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL4;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL5;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL8;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_FLOORBLOOD_QUEUE;
import static ru.m210projects.Wang.Names.STAT_HOLE_QUEUE;
import static ru.m210projects.Wang.Names.STAT_ITEM;
import static ru.m210projects.Wang.Names.STAT_MINE_STUCK;
import static ru.m210projects.Wang.Names.STAT_NO_STATE;
import static ru.m210projects.Wang.Names.STAT_SKIP2_INTERP_END;
import static ru.m210projects.Wang.Names.STAT_SKIP4_INTERP_END;
import static ru.m210projects.Wang.Names.STAT_STAR_QUEUE;
import static ru.m210projects.Wang.Names.STAT_STATIC_FIRE;
import static ru.m210projects.Wang.Names.STAT_VATOR;
import static ru.m210projects.Wang.Names.STAT_WALLBLOOD_QUEUE;
import static ru.m210projects.Wang.Sector.AnimateSwitch;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Stag.SECT_LOCK_DOOR;
import static ru.m210projects.Wang.Stag.SECT_VATOR;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_SWITCH_VATOR;
import static ru.m210projects.Wang.Text.KeyDoorMessage;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.FALSE;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class Vator {

	public static void ReverseVator(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// if paused go ahead and start it up again
		if (u.Tics != 0) {
			u.Tics = 0;
			SetVatorActive(SpriteNum);
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

	public static boolean VatorSwitch(int match, int setting) {
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

	public static void SetVatorActive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SECTOR sectp = sector[sp.sectnum];

		if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP))
			game.pInt.setceilinterpolate(sp.sectnum, sectp);
		else
			game.pInt.setfloorinterpolate(sp.sectnum, sectp);

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

	public static void SetVatorInactive(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

        InterpSectorSprites(sp.sectnum, false);

		// play inactivate sound
		DoSoundSpotMatch(SP_TAG2(sp), 2, SoundType.SOUND_OBJECT_TYPE);

		u.Flags &= ~(SPR_ACTIVE);
	}

	// called for operation from the space bar
	public static short DoVatorOperate(PlayerStr pp, short sectnum) {
		SPRITE fsp;
		int match;
		short i, nexti;

		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			fsp = sprite[i];

			if (fsp.statnum == STAT_VATOR && SP_TAG1(fsp) == SECT_VATOR && SP_TAG3(fsp) == 0) {
				sectnum = fsp.sectnum;

				// single play only vator
				// boolean 8 must be set for message to display
				if (TEST_BOOL4(fsp) && (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT
						|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_AI_BOTS)) {
					if (pp != null && TEST_BOOL11(fsp))
						PutStringInfo(pp, "This only opens in single play.");
					continue;
				}

				match = SP_TAG2(fsp);
				if (match > 0) {
					if (TestVatorMatchActive(match))
						return (-1);
					else
						return (DoVatorMatch(pp, match));
				}

				if (pp != null && SectUser[sectnum] != null && SectUser[sectnum].stag == SECT_LOCK_DOOR
						&& SectUser[sectnum].number != 0) {
					short key_num;

					key_num = SectUser[sectnum].number;
					PutStringInfo(pp, KeyDoorMessage[key_num - 1]);
					return (FALSE);
				}

				SetVatorActive(i);
				break;
			}
		}

		return (i);
	}

	// called from switches and triggers
	// returns first vator found
	public static short DoVatorMatch(PlayerStr pp, int match) {
		USER fu;
		SPRITE fsp;
		short sectnum;
		short first_vator = -1;

		short i, nexti;

		// VatorSwitch(match, ON);

		for (i = headspritestat[STAT_VATOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_VATOR && SP_TAG2(fsp) == match) {
				fu = pUser[i];

				if (first_vator == -1)
					first_vator = i;

				// single play only vator
				// boolean 8 must be set for message to display
				if (TEST_BOOL4(fsp) && (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT
						|| gNet.MultiGameType == MultiGameTypes.MULTI_GAME_AI_BOTS)) {
					if (pp != null && TEST_BOOL11(fsp))
						PutStringInfo(pp, "This only opens in single play.");
					continue;
				}

				// lock code
				sectnum = fsp.sectnum;
				if (pp != null && SectUser[sectnum] != null && SectUser[sectnum].stag == SECT_LOCK_DOOR
						&& SectUser[sectnum].number != 0) {
					short key_num;

					key_num = SectUser[sectnum].number;
					PutStringInfo(pp, KeyDoorMessage[key_num - 1]);
					return (-1);
				}

				// remember the player than activated it
				fu.PlayerP = (pp != null) ? pp.pnum : -1;
				
				if (TEST(fu.Flags, SPR_ACTIVE)) {
					ReverseVator(i);
					continue;
				}

				SetVatorActive(i);
			}
		}

		return (first_vator);
	}

	public static boolean TestVatorMatchActive(int match) {
		USER fu;
		SPRITE fsp;

		short i, nexti;

		for (i = headspritestat[STAT_VATOR]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			fsp = sprite[i];

			if (SP_TAG1(fsp) == SECT_VATOR && SP_TAG2(fsp) == match) {
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

	public static void InterpSectorSprites(short sectnum, boolean state) {
		SPRITE sp;
		short i, nexti;

		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			sp = sprite[i];

			if (pUser[i] != null) {
				if (TEST(pUser[i].Flags, SPR_SKIP4) && sp.statnum <= STAT_SKIP4_INTERP_END)
					continue;

				if (TEST(pUser[i].Flags, SPR_SKIP2) && sp.statnum <= STAT_SKIP2_INTERP_END)
					continue;
			}

			if (state)
				game.pInt.setsprinterpolate(i, sp);
        }
	}

	public static void MoveSpritesWithSector(short sectnum, int z_amt, boolean type) {
		SPRITE sp;
		short i, nexti;
		boolean both = false;

		if (SectUser[sectnum] != null)
			both = !!TEST(SectUser[sectnum].flags, SECTFU_VATOR_BOTH);

		for (i = headspritesect[sectnum]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			sp = sprite[i];

			if (pUser[i] != null) {
				switch (sp.statnum) {
				case STAT_ITEM:
				case STAT_NO_STATE:
				case STAT_MINE_STUCK:
				case STAT_WALLBLOOD_QUEUE:
				case STAT_FLOORBLOOD_QUEUE:
				case STAT_STATIC_FIRE:
					break;
				default:
					continue;
				}
			} else {
				switch (sp.statnum) {
				case STAT_STAR_QUEUE:
				case STAT_HOLE_QUEUE:
					continue;
				}
			}

			if (TEST(sp.extra, SPRX_STAY_PUT_VATOR))
				continue;

			if (both) {
				// sprite started close to floor
				if (TEST(sp.cstat, CSTAT_SPRITE_CLOSE_FLOOR)) {
					// this is a ceiling
					if (type)
						continue;
				} else {
					// this is a floor
					if (!type)
						continue;
				}
			}
			game.pInt.setsprinterpolate(i, sp);
			sp.z += z_amt;
		}
	}

	private static int pDoVatorMove;

	public static int DoVatorMove(int SpriteNum, int lptr) {
		USER u = pUser[SpriteNum];
		int zval;
		int move_amt;

		pDoVatorMove = zval = lptr;

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

		move_amt = zval - lptr;
		pDoVatorMove = zval;

		return (move_amt);
	}

	public static final Animator DoVator = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoVator(spr) != 0;
		}
	};

	public static int DoVator(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SECTOR sectp = sector[sp.sectnum];
		int amt, lptr;

		// u.sz - where the sector z started
		// u.z_tgt - current target z
		// u.oz - original z - where it initally starts off
		// sp.z - z of the sprite
		// u.vel_rate - velocity

		if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP)) {
			amt = DoVatorMove(SpriteNum, sectp.ceilingz);
			game.pInt.setceilinterpolate(sp.sectnum, sectp);
			sectp.ceilingz = lptr = pDoVatorMove;
			MoveSpritesWithSector(sp.sectnum, amt, true); // ceiling
		} else {
			amt = DoVatorMove(SpriteNum, sectp.floorz);
			game.pInt.setfloorinterpolate(sp.sectnum, sectp);
			sectp.floorz = lptr = pDoVatorMove;
			MoveSpritesWithSector(sp.sectnum, amt, false); // floor
		}

		// EQUAL this entry has finished
		if (lptr == u.z_tgt) {
			// in the ON position
			if (u.z_tgt == sp.z) {
				// change target
				u.z_tgt = u.sz;
				u.vel_rate = (short) -u.vel_rate;

				SetVatorInactive(SpriteNum);

				// if tag6 and nothing blocking door
				if (SP_TAG6(sp) != 0 && !TEST_BOOL8(sp))
					DoMatchEverything(u.PlayerP != -1 ? Player[u.PlayerP] : null, SP_TAG6(sp), -1);
			} else
			// in the OFF position
			if (u.z_tgt == u.sz) {
				int match = SP_TAG2(sp);

				// change target
				u.jump_speed = (short) u.vel_tgt;
				u.vel_rate = (short) klabs(u.vel_rate);
				u.z_tgt = sp.z;

				RESET_BOOL8(sp);
				SetVatorInactive(SpriteNum);

				// set owner swith back to OFF
				// only if ALL vators are inactive
				if (!TestVatorMatchActive(match)) {
					// VatorSwitch(match, OFF);
				}

				if (SP_TAG6(sp) != 0 && TEST_BOOL5(sp))
					DoMatchEverything(u.PlayerP != -1 ? Player[u.PlayerP] : null, SP_TAG6(sp), -1);
			}

			// operate only once
			if (TEST_BOOL2(sp)) {
				SetVatorInactive(SpriteNum);
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

					if (bsp.statnum == STAT_ENEMY) {
						if (klabs(sectp.ceilingz - sectp.floorz) < SPRITE_SIZE_Z(i)) {
							InitBloodSpray(i, true, -1);
							UpdateSinglePlayKills(i, null);
							KillSprite(i);
							continue;
						}
					}

					if (bu != null && TEST(bsp.cstat, CSTAT_SPRITE_BLOCK) && TEST(bsp.extra, SPRX_PLAYER_OR_ENEMY)) {
						// found something blocking so reverse to ON position
						ReverseVator(SpriteNum);
						SET_BOOL8(sp); // tell vator that something blocking door
						found = true;
						break;
					}
				}

				if (!found) {
					PlayerStr pp;
					// go ahead and look for players clip box bounds
					for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
						pp = Player[pnum];

						if ((pp.lo_sectp != -1 && pp.lo_sectp == sp.sectnum)
								|| (pp.hi_sectp != -1 && pp.hi_sectp == sp.sectnum)) {
							ReverseVator(SpriteNum);

							u.vel_rate = (short) -u.vel_rate;
							found = true;
						}
					}
				}
			} else {
				int i, nexti;
				SPRITE bsp;

				for (i = headspritesect[sp.sectnum]; i != -1; i = nexti) {
					nexti = nextspritesect[i];
					bsp = sprite[i];

					if (bsp.statnum == STAT_ENEMY) {
						if (klabs(sectp.ceilingz - sectp.floorz) < SPRITE_SIZE_Z(i)) {
							InitBloodSpray(i, true, -1);
							UpdateSinglePlayKills(i, null);
							KillSprite(i);
							continue;
						}
					}
				}
			}
		}

		return (0);
	}

	public static final Animator DoVatorAuto = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoVatorAuto(spr) != 0;
		}
	};

	public static int DoVatorAuto(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		SECTOR sectp = sector[sp.sectnum];
		int amt, lptr;

		if (TEST(sp.cstat, CSTAT_SPRITE_YFLIP)) {
			lptr = sectp.ceilingz;
			game.pInt.setceilinterpolate(sp.sectnum, sectp);
			amt = DoVatorMove(SpriteNum, lptr);
			sectp.ceilingz = lptr = pDoVatorMove;
			MoveSpritesWithSector(sp.sectnum, amt, true); // ceiling
		} else {
			lptr = sectp.floorz;
			game.pInt.setfloorinterpolate(sp.sectnum, sectp);
			amt = DoVatorMove(SpriteNum, lptr);
			sectp.floorz = lptr = pDoVatorMove;
			MoveSpritesWithSector(sp.sectnum, amt, false); // floor
		}

		// EQUAL this entry has finished
		if (lptr == u.z_tgt) {
			// in the UP position
			if (u.z_tgt == sp.z) {
				// change target
				u.z_tgt = u.sz;
				u.vel_rate = (short) -u.vel_rate;
				u.Tics = u.WaitTics;

				if (SP_TAG6(sp) != 0)
					DoMatchEverything(u.PlayerP != -1 ? Player[u.PlayerP] : null, SP_TAG6(sp), -1);
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
