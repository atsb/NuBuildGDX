package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Wang.Cheats.handleCheat;
import static ru.m210projects.Wang.Draw.Follow_posx;
import static ru.m210projects.Wang.Draw.Follow_posy;
import static ru.m210projects.Wang.Draw.ScrollMode2D;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Draw.dimensionmode;
import static ru.m210projects.Wang.Factory.WangNetwork.CommPlayers;
import static ru.m210projects.Wang.Factory.WangNetwork.netbuf;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.MessageInputMode;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.MAX_WEAPONS_KEYS;
import static ru.m210projects.Wang.Gameutils.PF_LOCK_RUN;
import static ru.m210projects.Wang.Gameutils.SK_CENTER_VIEW;
import static ru.m210projects.Wang.Gameutils.SK_CRAWL;
import static ru.m210projects.Wang.Gameutils.SK_CRAWL_LOCK;
import static ru.m210projects.Wang.Gameutils.SK_FLY;
import static ru.m210projects.Wang.Gameutils.SK_HIDE_WEAPON;
import static ru.m210projects.Wang.Gameutils.SK_INV_HOTKEY_BIT0;
import static ru.m210projects.Wang.Gameutils.SK_INV_LEFT;
import static ru.m210projects.Wang.Gameutils.SK_INV_RIGHT;
import static ru.m210projects.Wang.Gameutils.SK_INV_USE;
import static ru.m210projects.Wang.Gameutils.SK_JUMP;
import static ru.m210projects.Wang.Gameutils.SK_LOOK_DOWN;
import static ru.m210projects.Wang.Gameutils.SK_LOOK_UP;
import static ru.m210projects.Wang.Gameutils.SK_OPERATE;
import static ru.m210projects.Wang.Gameutils.SK_PAUSE;
import static ru.m210projects.Wang.Gameutils.SK_RUN;
import static ru.m210projects.Wang.Gameutils.SK_RUN_LOCK;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.SK_SNAP_DOWN;
import static ru.m210projects.Wang.Gameutils.SK_SNAP_UP;
import static ru.m210projects.Wang.Gameutils.SK_SPACE_BAR;
import static ru.m210projects.Wang.Gameutils.SK_TILT_LEFT;
import static ru.m210projects.Wang.Gameutils.SK_TILT_RIGHT;
import static ru.m210projects.Wang.Gameutils.SK_TURN_180;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_HEART;
import static ru.m210projects.Wang.Gameutils.WPN_STAR;
import static ru.m210projects.Wang.Gameutils.WPN_SWORD;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Inv.INVENTORY_CALTROPS;
import static ru.m210projects.Wang.Inv.INVENTORY_CHEMBOMB;
import static ru.m210projects.Wang.Inv.INVENTORY_CLOAK;
import static ru.m210projects.Wang.Inv.INVENTORY_FLASHBOMB;
import static ru.m210projects.Wang.Inv.INVENTORY_MEDKIT;
import static ru.m210projects.Wang.Inv.INVENTORY_NIGHT_VISION;
import static ru.m210projects.Wang.JPlayer.adduserquote;
import static ru.m210projects.Wang.JPlayer.computergetinput;
import static ru.m210projects.Wang.JPlayer.quotebot;
import static ru.m210projects.Wang.JPlayer.quotebotgoal;
import static ru.m210projects.Wang.Main.NETTEST;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Sector.x_max_bound;
import static ru.m210projects.Wang.Sector.x_min_bound;
import static ru.m210projects.Wang.Sector.y_max_bound;
import static ru.m210projects.Wang.Sector.y_min_bound;
import static ru.m210projects.Wang.Sound.PlaySoundRTS;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Settings.BuildConfig.KeyType;
import ru.m210projects.Wang.Config.SwKeys;
import ru.m210projects.Wang.Factory.WangNetwork.PacketType;
import ru.m210projects.Wang.Type.Input;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class WangInput extends BuildControls {

	public final int TURBOTURNTIME = (120 / 8);
	public final int NORMALTURN = (12 + 6);
	public final int RUNTURN = (28);
	public final int PREAMBLETURN = 3;
	public final int NORMALKEYMOVE = 35;
	public final int MAXVEL = ((NORMALKEYMOVE * 2) + 10);
	public final int MAXSVEL = ((NORMALKEYMOVE * 2) + 10);
	public static final int MAXANGVEL = 100;

	public int turnheldtime; // MED

	private short vel, svel;
	private float horiz, angvel;

	public WangInput(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
	}

	@Override
	public float getScaleX() {
		return 1.0f;
	}

	@Override
	public float getScaleY() {
		return 1.0f;
	}

	private void SET_LOC_KEY(Input loc, int flags, KeyType key) {
		loc.bits |= ctrlGetInputKey(key, false) ? 1 << flags : 0;
	}

	@Override
	public void ctrlGetInput(NetInput input) {
		float daang;
		int tics = 4;
		boolean running = true;
		int turnamount;
		int keymove;

		PlayerStr pp = Player[myconnectindex];
		if(pp.PlayerSprite == -1)
			return;

		Input loc = (Input) input;
		loc.Reset();

		if (game.menu.gShowMenu || Console.IsShown() || MessageInputMode
				|| (game.gPaused && !ctrlKeyStatus(KEY_PAUSE))) {
			loc.vel = vel = 0;
			loc.svel = svel = 0;
			loc.angvel = angvel = 0;
			loc.aimvel = horiz = 0;
			loc.bits = 0;

			if (Console.IsShown())
				MessageInputMode = false;

			if (MessageInputMode) {
				int out = getInput().putMessage(getInput().getMessageBuffer().length, false, false, true);
				if (out != 0)
					MessageInputMode = false;
				if (out == 1) {
					char[] lockeybuf = getInput().getMessageBuffer();
					String message = new String(lockeybuf, 0, getInput().getMessageLength());

					if (game.net.TeamPlay) {
						for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
							if (pnum != myconnectindex) {
//								if (game.net.TeamSendAll)
//									game.net.SendMessage(pnum, message);
//								else 
									if (pUser[pp.PlayerSprite].spal == pUser[Player[pnum].PlayerSprite].spal)
									game.net.SendMessage(pnum, message);
							}
						}
					} else
						for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
							if (pnum != myconnectindex) {
								game.net.SendMessage(pnum, message);
							}
						}

					if (numplayers > 1 || !handleCheat(message)) {
						adduserquote(message);
						quotebot += 8;
						quotebotgoal = quotebot;
					}
				}
			}
			return;
		}

		if (ctrlKeyStatus(Keys.SHIFT_LEFT) || ctrlKeyStatus(Keys.CONTROL_LEFT)) {
			int fkey = -1;
			for (int i = 0; i < 10; i++) {
				if (ctrlKeyStatusOnce(i + Keys.F1)) {
					fkey = i;
					break;
				}
			}

			if (fkey >= 0) {
				if (ctrlKeyStatus(Keys.CONTROL_LEFT)) {
					if (PlaySoundRTS(fkey)) {
						if (CommPlayers > 1) {
							PacketType.RTS_Sound.setData(fkey);
							int l = PacketType.RTS_Sound.Send(netbuf);
							game.net.sendtoall(netbuf, l);
						}
					}
				}

				if (ctrlKeyStatus(Keys.SHIFT_LEFT)) {
					String message = gs.WangBangMacro[fkey];
					adduserquote(message);

					if (CommPlayers > 1) {
						PacketType.Message.setData(-1, message);
						int l = PacketType.Message.Send(netbuf);
						game.net.sendtoall(netbuf, l);
					}
				}
				return;
			}
		}

		svel = vel = 0;
		horiz = angvel = 0;

		if (ctrlGetInputKey(GameKeys.Mouse_Aiming, true)) {
			gs.gMouseAim = !gs.gMouseAim;
			if (!gs.gMouseAim) {
				loc.bits |= (1 << SK_LOOK_UP);
				PutStringInfo(pp, "Mouse Aiming Off");
			} else {
				PutStringInfo(pp, "Mouse Aiming On");
			}
		}

		// MAP KEY
		if (ctrlGetInputKey(GameKeys.Map_Toggle, true)) {

			// Init follow coords
			Follow_posx = pp.posx;
			Follow_posy = pp.posy;

			if (dimensionmode == 3)
				dimensionmode = 5;
			else if (dimensionmode == 5)
				dimensionmode = 6;
			else {
				dimensionmode = 3;
				ScrollMode2D = false;
				SetRedrawScreen(pp);
			}
		}

		if (NETTEST) {
			gNet.BotSkill = 3;
			if (myconnectindex != connecthead) {
				computergetinput(myconnectindex, loc);
				return;
			}
		}
		
		if(GodMode)
			loc.bits |= ctrlKeyStatusOnce(Keys.J) ? (1 << SK_FLY) : 0;
			
		if(ctrlGetInputKey(SwKeys.Show_Opp_Weapon, true))
			gs.ShowWeapon = !gs.ShowWeapon;
			
		SET_LOC_KEY(loc, SK_CENTER_VIEW, SwKeys.Aim_Center);

		SET_LOC_KEY(loc, SK_RUN, GameKeys.Run);
		SET_LOC_KEY(loc, SK_SHOOT, GameKeys.Weapon_Fire);

		// actually snap
		SET_LOC_KEY(loc, SK_SNAP_UP, SwKeys.Aim_Up);
		SET_LOC_KEY(loc, SK_SNAP_DOWN, SwKeys.Aim_Down);

		// actually just look
		SET_LOC_KEY(loc, SK_LOOK_UP, GameKeys.Look_Up);
		SET_LOC_KEY(loc, SK_LOOK_DOWN, GameKeys.Look_Down);
		
		SET_LOC_KEY(loc, SK_TILT_LEFT, SwKeys.Tilt_Left);
		SET_LOC_KEY(loc, SK_TILT_RIGHT, SwKeys.Tilt_Right);
		
		for (int i = 0; i < MAX_WEAPONS_KEYS; i++)
			if (ctrlGetInputKey(gs.keymap[i + SwKeys.Weapon_1.getNum()], true)) {
				loc.bits |= (i + 1);
				break;
			}

		if (ctrlGetInputKey(SwKeys.Last_Weap_Switch, true))
			loc.bits |= pp.last_used_weapon + 1;
		
		if (ctrlGetInputKey(SwKeys.Special_Fire, true)) {
			USER u = pUser[pp.PlayerSprite];
			loc.bits |= u.WeaponNum + 1;
		}
	
		if (ctrlGetInputKey(GameKeys.Previous_Weapon, true)) {
			USER u = pUser[pp.PlayerSprite];
			int prev_weapon = u.WeaponNum - 1;
			int start_weapon;

			start_weapon = u.WeaponNum - 1;

			if (u.WeaponNum == WPN_SWORD) {
				prev_weapon = 13;
			} else if (u.WeaponNum == WPN_STAR) {
				prev_weapon = 14;
			} else {
				prev_weapon = -1;
				for (int i = start_weapon;; i--) {
					if (i <= -1)
						i = WPN_HEART;

					if (TEST(pp.WpnFlags, BIT(i)) && pp.WpnAmmo[i] != 0) {
						prev_weapon = i;
						break;
					}
				}
			}
			loc.bits |= prev_weapon + 1;
		}

		if (ctrlGetInputKey(GameKeys.Next_Weapon, true)) {
			USER u = pUser[pp.PlayerSprite];
			int next_weapon = u.WeaponNum + 1;
			int start_weapon;

			start_weapon = u.WeaponNum + 1;

			if (u.WeaponNum == WPN_SWORD)
				start_weapon = WPN_STAR;

			if (u.WeaponNum == WPN_FIST) {
				next_weapon = 14;
			} else {
				next_weapon = -1;
				for (int i = start_weapon;; i++) {
					if (i >= MAX_WEAPONS_KEYS) {
						next_weapon = 13;
						break;
					}

					if (TEST(pp.WpnFlags, BIT(i)) && pp.WpnAmmo[i] != 0) {
						next_weapon = i;
						break;
					}
				}
			}
			loc.bits |= next_weapon + 1;
		}

		int inv_hotkey = 0;
		if (ctrlGetInputKey(SwKeys.MedKit, true))
			inv_hotkey = INVENTORY_MEDKIT + 1;
		if (ctrlGetInputKey(SwKeys.SmokeBomb, true))
			inv_hotkey = INVENTORY_CLOAK + 1;
		if (ctrlGetInputKey(SwKeys.NightVision, true))
			inv_hotkey = INVENTORY_NIGHT_VISION + 1;
		if (ctrlGetInputKey(SwKeys.GasBomb, true))
			inv_hotkey = INVENTORY_CHEMBOMB + 1;
		if (ctrlGetInputKey(SwKeys.FlashBomb, true) && dimensionmode == 3)
			inv_hotkey = INVENTORY_FLASHBOMB + 1;
		if (ctrlGetInputKey(SwKeys.Caltrops, true))
			inv_hotkey = INVENTORY_CALTROPS + 1;

		loc.bits |= (inv_hotkey << SK_INV_HOTKEY_BIT0);

		SET_LOC_KEY(loc, SK_INV_USE, SwKeys.Inventory_Use);

		SET_LOC_KEY(loc, SK_OPERATE, GameKeys.Open);
		SET_LOC_KEY(loc, SK_JUMP, GameKeys.Jump);
		SET_LOC_KEY(loc, SK_CRAWL, GameKeys.Crouch);

		SET_LOC_KEY(loc, SK_TURN_180, GameKeys.Turn_Around);

		SET_LOC_KEY(loc, SK_INV_LEFT, SwKeys.Inventory_Left);
		SET_LOC_KEY(loc, SK_INV_RIGHT, SwKeys.Inventory_Right);

		SET_LOC_KEY(loc, SK_HIDE_WEAPON, SwKeys.Holster_Weapon);

		SET_LOC_KEY(loc, SK_CRAWL_LOCK, SwKeys.Crouch_toggle);

		SET_LOC_KEY(loc, SK_RUN_LOCK, SwKeys.AutoRun);

		loc.bits |= (ctrlGetInputKey(GameKeys.Open, false) || ctrlKeyStatus(Keys.SPACE)) ? 1 << SK_SPACE_BAR : 0;
		loc.bits |= ctrlKeyStatusOnce(KEY_PAUSE) ? (1 << SK_PAUSE) : 0;

		running = ((!TEST(pp.Flags, PF_LOCK_RUN) && TEST(loc.bits, BIT(SK_RUN)))
				|| (!TEST(loc.bits, BIT(SK_RUN)) && TEST(pp.Flags, PF_LOCK_RUN)));

		if (running) {
			turnamount = NORMALTURN << 1;
			keymove = NORMALKEYMOVE << 1;
		} else {
			turnamount = NORMALTURN;
			keymove = NORMALKEYMOVE;
		}

		if (pp.sop_control != -1)
			turnamount *= 3;

		if (ctrlGetInputKey(GameKeys.Strafe, false) && pp.sop == -1) {
			if (ctrlGetInputKey(GameKeys.Turn_Left, false))
				svel -= -keymove;
			if (ctrlGetInputKey(GameKeys.Turn_Right, false))
				svel -= keymove;
			svel = (short) BClipRange(svel - (int) 20 * ctrlGetMouseStrafe(), -keymove, keymove);
		} else {
			if (ctrlGetInputKey(GameKeys.Turn_Left, false)) {
				turnheldtime += tics;
				if (turnheldtime >= TURBOTURNTIME)
					angvel -= turnamount;
				else
					angvel -= PREAMBLETURN;
			} else if (ctrlGetInputKey(GameKeys.Turn_Right, false)) {
				turnheldtime += tics;
				if (turnheldtime >= TURBOTURNTIME)
					angvel += turnamount;
				else
					angvel += PREAMBLETURN;
			} else
				turnheldtime = 0;
			angvel = BClipRange(angvel + ctrlGetMouseTurn(), -1024, 1024);
		}

		if (ctrlGetInputKey(GameKeys.Strafe_Left, false)) {
			if (pp.sop == -1)
				svel += keymove;
			else
				angvel += -turnamount;
		}
		if (ctrlGetInputKey(GameKeys.Strafe_Right, false)) {
			if (pp.sop == -1)
				svel += -keymove;
			else
				angvel += turnamount;
		}

		if (ctrlGetInputKey(GameKeys.Move_Forward, false))
			vel += keymove;
		if (ctrlGetInputKey(GameKeys.Move_Backward, false))
			vel += -keymove;

		if (gs.gMouseAim) {
			horiz = BClipRange(ctrlGetMouseLook(!gs.gInvertmouse), -(ydim >> 1), 100 + (ydim >> 1));
		} else
			vel = (short) BClipRange(vel - ctrlGetMouseMove(), -4 * keymove, 4 * keymove);

		Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);

		float lookx = stick1.x;
		float looky = stick1.y;
		if (gs.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 8.0f;
			horiz = BClipRange(horiz - k * looky * gs.gJoyLookSpeed / 65536f, -(ydim >> 1), 100 + (ydim >> 1));
		}

		if (lookx != 0) {
			float k = 8.0f;
			angvel = BClipRange(angvel + k * lookx * gs.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0) {
			vel = (short) BClipRange(vel - (keymove * stick2.y), -4 * keymove, 4 * keymove);
		}

		if (stick2.x != 0) {
			svel = (short) BClipRange(svel - (keymove * stick2.x), -4 * keymove, 4 * keymove);
		}

		if (vel < -MAXVEL)
			vel = -MAXVEL;
		if (vel > MAXVEL)
			vel = MAXVEL;
		if (svel < -MAXSVEL)
			svel = -MAXSVEL;
		if (svel > MAXSVEL)
			svel = MAXSVEL;
		if (angvel < -MAXANGVEL)
			angvel = -MAXANGVEL;
		if (angvel > MAXANGVEL)
			angvel = MAXANGVEL;
		if (horiz < -128)
			horiz = -128;
		if (horiz > 127)
			horiz = 127;

		daang = pp.getAnglef();

		int momx = (int) (vel * BCosAngle(BClampAngle(daang)) / 512.0f);
		int momy = (int) (vel * BSinAngle(BClampAngle(daang)) / 512.0f);

		momx += (int) (svel * BSinAngle(BClampAngle(daang)) / 512.0f);
		momy += (int) (svel * BCosAngle(BClampAngle(daang + 1024)) / 512.0f);

		if (ScrollMode2D && (dimensionmode == 5 || dimensionmode == 6)) {
			Follow_posx += momx / 4;
			Follow_posy += momy / 4;

			Follow_posx = Math.max(Follow_posx, x_min_bound);
			Follow_posy = Math.max(Follow_posy, y_min_bound);
			Follow_posx = Math.min(Follow_posx, x_max_bound);
			Follow_posy = Math.min(Follow_posy, y_max_bound);

			loc.vel = 0;
			loc.svel = 0;
			loc.angvel = 0;
			loc.aimvel = 0;
			return;
		}

		loc.vel = (short) momx;
		loc.svel = (short) momy;
		loc.angvel = angvel;
		loc.aimvel = horiz;
	}

}
