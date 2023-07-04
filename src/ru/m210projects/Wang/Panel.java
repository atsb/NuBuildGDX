package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Wang.Cheats.InfinityAmmo;
import static ru.m210projects.Wang.Digi.DIGI_HOTHEADSWITCH;
import static ru.m210projects.Wang.Digi.DIGI_SHOTGUN_UP;
import static ru.m210projects.Wang.Draw.dimensionmode;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.DebugPanel;
import static ru.m210projects.Wang.Game.Global_PLock;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.FindDistance2D;
import static ru.m210projects.Wang.Gameutils.MAX_PAIN;
import static ru.m210projects.Wang.Gameutils.MAX_YELLSOUNDS;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.PF_TWO_UZI;
import static ru.m210projects.Wang.Gameutils.PF_VIEW_FROM_CAMERA;
import static ru.m210projects.Wang.Gameutils.PF_VIEW_FROM_OUTSIDE;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_DOWN;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_ALL_PAGES;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_CORNER;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_IGNORE_START_MOST;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_VIEW_CLIP;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.SECTFU_DONT_COPY_PALETTE;
import static ru.m210projects.Wang.Gameutils.SK_HIDE_WEAPON;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.SK_WEAPON_BIT0;
import static ru.m210projects.Wang.Gameutils.SK_WEAPON_MASK;
import static ru.m210projects.Wang.Gameutils.SOBJ_HAS_WEAPON;
import static ru.m210projects.Wang.Gameutils.SO_SPEED_BOAT;
import static ru.m210projects.Wang.Gameutils.SO_TANK;
import static ru.m210projects.Wang.Gameutils.SO_TURRET;
import static ru.m210projects.Wang.Gameutils.SO_TURRET_MGUN;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_GRENADE;
import static ru.m210projects.Wang.Gameutils.WPN_HEART;
import static ru.m210projects.Wang.Gameutils.WPN_HOTHEAD;
import static ru.m210projects.Wang.Gameutils.WPN_MICRO;
import static ru.m210projects.Wang.Gameutils.WPN_MINE;
import static ru.m210projects.Wang.Gameutils.WPN_NAPALM;
import static ru.m210projects.Wang.Gameutils.WPN_RAIL;
import static ru.m210projects.Wang.Gameutils.WPN_RING;
import static ru.m210projects.Wang.Gameutils.WPN_SHOTGUN;
import static ru.m210projects.Wang.Gameutils.WPN_STAR;
import static ru.m210projects.Wang.Gameutils.WPN_SWORD;
import static ru.m210projects.Wang.Gameutils.WPN_UZI;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Inv.DrawInventory;
import static ru.m210projects.Wang.Inv.INVENTORY_BOX_X;
import static ru.m210projects.Wang.Inv.INVENTORY_BOX_Y;
import static ru.m210projects.Wang.Inv.INVENTORY_CLOAK;
import static ru.m210projects.Wang.Inv.InventoryKeys;
import static ru.m210projects.Wang.Inv.PanelInvTestSuicide;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Palette.PALETTE_DEFAULT;
import static ru.m210projects.Wang.Palette.PALETTE_DIVE;
import static ru.m210projects.Wang.Palette.PALETTE_DIVE_LAVA;
import static ru.m210projects.Wang.Palette.PALETTE_FOG;
import static ru.m210projects.Wang.Palette.SetFadeAmt;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerLowHealthPainVocs;
import static ru.m210projects.Wang.Sound.PlayerPainVocs;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.PlayerYellVocs;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Text.DisplayPanelNumber;
import static ru.m210projects.Wang.Text.DisplaySummaryString;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Weapon.DamageData;
import static ru.m210projects.Wang.Weapon.InitSobjGun;
import static ru.m210projects.Wang.Weapons.Chops.ChopsSaveable;
import static ru.m210projects.Wang.Weapons.Fist.FIST2_REST;
import static ru.m210projects.Wang.Weapons.Fist.FIST_REST;
import static ru.m210projects.Wang.Weapons.Fist.FIST_SWING0;
import static ru.m210projects.Wang.Weapons.Fist.FIST_SWING1;
import static ru.m210projects.Wang.Weapons.Fist.FIST_SWING2;
import static ru.m210projects.Wang.Weapons.Fist.FistSaveable;
import static ru.m210projects.Wang.Weapons.Fist.InitWeaponFist;
import static ru.m210projects.Wang.Weapons.Grenade.GrenadeSaveable;
import static ru.m210projects.Wang.Weapons.Grenade.InitWeaponGrenade;
import static ru.m210projects.Wang.Weapons.Heart.HeartSaveable;
import static ru.m210projects.Wang.Weapons.Heart.InitWeaponHeart;
import static ru.m210projects.Wang.Weapons.HotHead.HotHeadSaveable;
import static ru.m210projects.Wang.Weapons.HotHead.InitWeaponHothead;
import static ru.m210projects.Wang.Weapons.HotHead.pHotHeadOverlays;
import static ru.m210projects.Wang.Weapons.Micro.InitWeaponMicro;
import static ru.m210projects.Wang.Weapons.Micro.MicroSaveable;
import static ru.m210projects.Wang.Weapons.Mine.InitWeaponMine;
import static ru.m210projects.Wang.Weapons.Mine.MineSaveable;
import static ru.m210projects.Wang.Weapons.Rail.InitWeaponRail;
import static ru.m210projects.Wang.Weapons.Rail.RailSaveable;
import static ru.m210projects.Wang.Weapons.Shotgun.InitWeaponShotgun;
import static ru.m210projects.Wang.Weapons.Shotgun.SHOTGUN_RELOAD0;
import static ru.m210projects.Wang.Weapons.Shotgun.SHOTGUN_RELOAD1;
import static ru.m210projects.Wang.Weapons.Shotgun.SHOTGUN_RELOAD2;
import static ru.m210projects.Wang.Weapons.Shotgun.SHOTGUN_REST;
import static ru.m210projects.Wang.Weapons.Shotgun.ShotgunSaveable;
import static ru.m210projects.Wang.Weapons.Star.InitWeaponStar;
import static ru.m210projects.Wang.Weapons.Star.STAR_REST;
import static ru.m210projects.Wang.Weapons.Star.StarSaveable;
import static ru.m210projects.Wang.Weapons.Sword.BLOODYSWORD_REST;
import static ru.m210projects.Wang.Weapons.Sword.BLOODYSWORD_SWING0;
import static ru.m210projects.Wang.Weapons.Sword.BLOODYSWORD_SWING1;
import static ru.m210projects.Wang.Weapons.Sword.BLOODYSWORD_SWING2;
import static ru.m210projects.Wang.Weapons.Sword.InitWeaponSword;
import static ru.m210projects.Wang.Weapons.Sword.SWORD_REST;
import static ru.m210projects.Wang.Weapons.Sword.SWORD_SWING0;
import static ru.m210projects.Wang.Weapons.Sword.SWORD_SWING1;
import static ru.m210projects.Wang.Weapons.Sword.SWORD_SWING2;
import static ru.m210projects.Wang.Weapons.Sword.SwordSaveable;
import static ru.m210projects.Wang.Weapons.Uzi.InitWeaponUzi;
import static ru.m210projects.Wang.Weapons.Uzi.UziSaveable;

import com.badlogic.gdx.utils.Pool;

import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.List;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.USER;

public class Panel {

	private static Pool<Panel_Sprite> panelSpriteList = new Pool<Panel_Sprite>() {
		@Override
		protected Panel_Sprite newObject() {
			return new Panel_Sprite();
		}
	};

	public static final int BORDER_NONE = 0;
	public static final int BORDER_MINI_BAR = 1;
	public static final int BORDER_ALTBAR = 2;
	public static final int BORDER_BAR = 3;

	public static final int FRAG_BAR = 2920;

	public static final int PRI_FRONT_MAX = 250;
	public static final int PRI_FRONT = 192;
	public static final int PRI_MID = 128;
	public static final int PRI_BACK = 64;
	public static final int PRI_BACK_MAX = 0;

	public static final int PANF_PRIMARY = (BIT(0)); // denotes primary weapon
	public static final int PANF_SECONDARY = (BIT(1)); // denotes secondary weapon
	public static final int PANF_BOB = (BIT(2));
	public static final int PANF_REST_POS = (BIT(3)); // used for certain weapons - fireball
	public static final int PANF_RELOAD = (BIT(4)); // reload flag used for uzi
	public static final int PANF_TRANS_FLIP = (BIT(5)); // translucent flip - matches rotate sprite
	public static final int PANF_ACTION_POS = (BIT(6)); // used for certain weapons - fireball
	public static final int PANF_WEAPON_HIDE = (BIT(7)); // hide when climbing/driving
	public static final int PANF_TRANSLUCENT = (BIT(8)); // turn invisible
	public static final int PANF_INVISIBLE = (BIT(9)); // turn invisible
	public static final int PANF_DEATH_HIDE = (BIT(10)); // hide done when dead
	public static final int PANF_KILL_AFTER_SHOW = (BIT(11)); // kill after showing numpages times
	public static final int PANF_SCREEN_CLIP = (BIT(12)); // maintain aspect to the screen
	public static final int PANF_STATUS_AREA = (BIT(13)); // maintain aspect to the screen
	public static final int PANF_IGNORE_START_MOST = (BIT(14)); // maintain aspect to the screen
	public static final int PANF_XFLIP = (BIT(15)); // xflip
	public static final int PANF_SUICIDE = (BIT(16)); // kill myself
	public static final int PANF_WEAPON_SPRITE = (BIT(17)); // its a weapon sprite - for V mode
	public static final int PANF_CORNER = (BIT(18)); // draw from the corner
	public static final int PANF_NOT_IN_VIEW = (BIT(19)); // not in view
	public static final int PANF_UNHIDE_SHOOT = (BIT(20)); // shoot after un-hiding a weapon
	public static final int PANF_JUMPING = (BIT(21));
	public static final int PANF_FALLING = (BIT(22));
	public static final int PANF_DRAW_BEFORE_VIEW = (BIT(30)); // draw before drawrooms
	public static final int PANF_NOT_ALL_PAGES = (BIT(31)); // DONT use permanentwritesprite bit for rotatesprite

	public static final int MICRO_SIGHT_NUM = 0;
	public static final int MICRO_SIGHT = 2075;

	public static final int MICRO_SHOT_NUM = 2;
	public static final int MICRO_SHOT_20 = 2076;
	public static final int MICRO_SHOT_1 = 2077;

	public static final int MICRO_HEAT_NUM = 1;
	public static final int MICRO_HEAT = 2084;

	public static final int UZI_COPEN = 2040;
	public static final int UZI_CCLOSED = 2041;
	public static final int UZI_CLIT = 2042;
	public static final int UZI_CRELOAD = 2043;

	public static final int HEAD_MODE1 = 2055;
	public static final int HEAD_MODE2 = 2056;
	public static final int HEAD_MODE3 = 2057;

	public static final int SHOTGUN_AUTO_NUM = 0;
	public static final int SHOTGUN_AUTO = 2078;

	// Panel State flags - also used for
	public static final int psf_Invisible = BIT(16);
	public static final int psf_QuickCall = BIT(23);
	public static final int psf_Xflip = BIT(24);
	public static final int psf_ShadeHalf = BIT(25);
	public static final int psf_ShadeNone = BIT(26);

	public static final int ID_BORDER_TOP = 1, ID_BORDER_BOTTOM = 2, ID_BORDER_LEFT = 3, ID_BORDER_RIGHT = 4,
			ID_BORDER_SHADE = 5, ID_TEXT = 6, ID_TEXT2 = 7, ID_TEXT3 = 8, ID_TEXT4 = 9;

	public static final Panel_Sprite_Func pSuicide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite p) {
			pKillSprite(p);
		}
	};

	public static int UziRecoilYadj = 0;

	public static Panel_Sprite pFindMatchingSprite(PlayerStr pp, int x, int y, int pri) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;

			// early out
			if (psp.priority > pri)
				return (null);

			if (psp.x == x && psp.y == y && psp.priority == pri) {
				return (psp);
			}
		}

		return null;
	}

	public static Panel_Sprite pFindMatchingSpriteID(PlayerStr pp, int id, int x, int y, int pri) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;
			// early out
			if (psp.priority > pri)
				return (null);

			if (psp.ID == id && psp.x == x && psp.y == y && psp.priority == pri) {
				return (psp);
			}
		}

		return (null);
	}

	public static boolean pKillScreenSpiteIDs(PlayerStr pp, int id) {
		boolean found = false;

		// Kill ALL sprites with the correct id
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;
			if (psp.ID == id) {
				pKillSprite(psp);
				found = true;
			}
		}

		return (found);
	}

	public static Panel_Sprite pSpawnFullViewSprite(PlayerStr pp, int pic, int pri, int x, int y) {
		Panel_Sprite nsp;

		if ((nsp = pFindMatchingSprite(pp, x, y, pri)) == null) {
			nsp = pSpawnSprite(pp, null, pri, x, y);
		}

		nsp.numpages = 1; // numpages;
		nsp.picndx = -1;
		nsp.picnum = (short) pic;
		nsp.x1 = 0;
		nsp.y1 = 0;
		nsp.x2 = (short) (xdim - 1);
		nsp.y2 = (short) (ydim - 1);
		nsp.flags |= (PANF_STATUS_AREA | PANF_SCREEN_CLIP);

		return (nsp);
	}

	public static void pSetSuicide(Panel_Sprite psp) {
		psp.PanelSpriteFunc = pSuicide;
	}

	public static void pToggleCrosshair(PlayerStr pp) {
		if (gs.Crosshair)
			gs.Crosshair = false;
		else
			gs.Crosshair = true;
	}

	// Player has a chance of yelling out during combat, when firing a weapon.
	public static void DoPlayerChooseYell(PlayerStr pp) {
		int choose_snd = 0;
		int weapon = DTEST(pp.input.bits, SK_WEAPON_MASK);

		if (weapon == WPN_FIST) {
			if (RANDOM_RANGE(1000) < 900)
				return;
		} else if (RANDOM_RANGE(1000) < 990)
			return;

		choose_snd = STD_RANDOM_RANGE(MAX_YELLSOUNDS);

		if (pp == Player[myconnectindex])
			PlayerSound(PlayerYellVocs[choose_snd], v3df_follow | v3df_dontpan, pp);
	}

	public static final int PANEL_HEALTH_BOX_X = 20;
	public static final int PANEL_BOX_Y = (174 - 6);
	public static final int PANEL_HEALTH_XOFF = 2;
	public static final int PANEL_HEALTH_YOFF = 4;
	public static final int HEALTH_ERASE = 2401;

	public static final int WeaponIsAmmo = BIT(WPN_STAR) | BIT(WPN_SWORD) | BIT(WPN_MINE) | BIT(WPN_FIST);

	public static void ArmorCalc(int damage_amt, LONGp armor_damage, LONGp player_damage) {
		int damage_percent;

		if (damage_amt == 1) {
			player_damage.value = RANDOM_P2(2 << 8) >> 8;
			armor_damage.value = 1;
			return;
		}

		// note: this could easily be converted to a mulscale and save a
		// bit of processing for floats
		damage_percent = (int) ((0.6 * damage_amt) + 0.5);

		player_damage.value = damage_amt - damage_percent;
		armor_damage.value = damage_percent;
	}

	public static void PlayerUpdateHealth(PlayerStr pp, int value) {
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		if (GodMode) {
			if (value < 0)
				return;
		}

		if (TEST(pp.Flags, PF_DEAD))
			return;

		if (value < 0) {
			boolean IsChem = false;
			boolean NoArmor = false;

			if (value <= -2000) {
				value += 2000;
				NoArmor = true;
			} else if (value <= -1000) {

				value += 1000;
				IsChem = true;
			}

			// TAKE SOME DAMAGE
			u.LastDamage = (short) -value;

			// adjust for armor
			if (pp.Armor != 0 && !NoArmor) {
				ArmorCalc(klabs(value), tmp_ptr[0], tmp_ptr[1]);

				PlayerUpdateArmor(pp, -tmp_ptr[0].value);
				value = -tmp_ptr[1].value;
			}

			u.Health += value;

			if (value < 0) {
				char choosesnd = 0;

				choosesnd = (char) RANDOM_RANGE(MAX_PAIN);

				if (u.Health > 50) {
					PlayerSound(PlayerPainVocs[choosesnd], v3df_dontpan | v3df_doppler | v3df_follow, pp);
				} else {
					PlayerSound(PlayerLowHealthPainVocs[choosesnd], v3df_dontpan | v3df_doppler | v3df_follow, pp);
				}

			}
			// Do redness based on damage taken.
			if (pp == Player[screenpeek]) {
				if (IsChem)
					SetFadeAmt(pp, -40, 144); // ChemBomb green color
				else {
					if (value <= -10)
						SetFadeAmt(pp, value, 112);
					else if (value > -10 && value < 0)
						SetFadeAmt(pp, -20, 112);
				}
			}
			if (u.Health <= 100)
				pp.MaxHealth = 100; // Reset max health if sank below 100
		} else {
			// ADD SOME HEALTH
			if (value > 1000)
				u.Health += (value - 1000);
			else {
				if (u.Health < pp.MaxHealth) {
					u.Health += value;
				}
			}

			if (value >= 1000)
				value -= 1000; // Strip out high value
		}

		if (u.Health < 0)
			u.Health = 0;

		if (u.Health > pp.MaxHealth)
			u.Health = pp.MaxHealth;
	}

	public static final int PANEL_AMMO_BOX_X = 197;
	public static final int PANEL_AMMO_XOFF = 1;
	public static final int PANEL_AMMO_YOFF = 4;
	public static final int AMMO_ERASE = 2404;

	public static void PlayerUpdateAmmo(PlayerStr pp, int UpdateWeaponNum, int value) {
		if (Prediction)
			return;

		if (DamageData[UpdateWeaponNum].max_ammo == -1) {
			if (gs.BorderNum < BORDER_BAR || pp.pnum != screenpeek)
				return;
			return;
		}

		short WeaponNum = (short) UpdateWeaponNum;

		// get the WeaponNum of the ammo
		if (DamageData[UpdateWeaponNum].with_weapon != -1) {
			WeaponNum = DamageData[UpdateWeaponNum].with_weapon;
		}

		if(value < 0 && InfinityAmmo)
			value = 0;
		pp.WpnAmmo[WeaponNum] += value;

		if (pp.WpnAmmo[WeaponNum] <= 0) {
			// star and mine
			if (TEST(WeaponIsAmmo, BIT(WeaponNum)))
				pp.WpnFlags &= ~(BIT(WeaponNum));

			pp.WpnAmmo[WeaponNum] = 0;
		}

		if (pp.WpnAmmo[WeaponNum] > DamageData[WeaponNum].max_ammo) {
			pp.WpnAmmo[WeaponNum] = DamageData[WeaponNum].max_ammo;
		}
	}

	public static final int WSUM_X = 93;
	public static final int WSUM_Y = PANEL_BOX_Y + 1;
	public static final int WSUM_XOFF = 25;
	public static final int WSUM_YOFF = 6;

	public static short wsum_xoff[] = { 0, 36, 66 };
	public static short wsum_back_pic[] = { 2405, 2406, 2406 };
	public static char[] wsum_fmt1 = new char[5];
	public static char[] wsum_fmt2 = new char[30];

	private static void PlayerUpdateWeaponSummary(PlayerStr pp, int UpdateWeaponNum) {
		USER u = pUser[pp.PlayerSprite];
		short x, y;
		short pos;
		short column;

		short WeaponNum, wpntmp;
		short color, shade;

		if (Prediction)
			return;

		WeaponNum = (short) UpdateWeaponNum;

		if (DamageData[WeaponNum].with_weapon != -1) {
			WeaponNum = DamageData[WeaponNum].with_weapon;
		}

		if (gs.BorderNum < BORDER_BAR || pp.pnum != screenpeek)
			return;

		pos = (short) (WeaponNum - 1);
		column = (short) (pos / 3);
		if (column > 2)
			column = 2;
		x = (short) (WSUM_X + wsum_xoff[column]);
		y = (short) (WSUM_Y + (WSUM_YOFF * (pos % 3)));

		if (UpdateWeaponNum == u.WeaponNum) {
			shade = 0;
			color = 0;
		} else {
			shade = 11;
			color = 0;
		}

		wpntmp = (short) (WeaponNum + 1);
		if (wpntmp > 9)
			wpntmp = 0;

		Bitoa(wpntmp, wsum_fmt1);

		if (TEST(pp.WpnFlags, BIT(WeaponNum)))
			DisplaySummaryString(pp, x, y, 1, shade, wsum_fmt1, 0);
		else
			DisplaySummaryString(pp, x, y, 2, shade + 6, wsum_fmt1, 0);

		int offs = Bitoa(pp.WpnAmmo[WeaponNum], wsum_fmt2);
		buildString(wsum_fmt2, offs, "/", DamageData[WeaponNum].max_ammo);
		DisplaySummaryString(pp, x + 6, y, color, shade, wsum_fmt2, 0);
	}

	private static void PlayerUpdateWeaponSummaryAll(PlayerStr pp) {
		if (Prediction)
			return;

		for (int i = WPN_STAR; i <= WPN_HEART; i++) {
			PlayerUpdateWeaponSummary(pp, i);
		}
	}

	public static void PlayerUpdateWeapon(PlayerStr pp, int WeaponNum) {
		USER u = pUser[pp.PlayerSprite];

		// Weapon Change
		if (Prediction)
			return;

		byte oldwpn = (byte) pp.getWeapon();
		if (oldwpn != pp.last_used_weapon && oldwpn != WeaponNum)
			pp.last_used_weapon = oldwpn;
		u.WeaponNum = (short) WeaponNum;
	}

	public static final int PANEL_KILLS_X = 31;
	public static final int PANEL_KILLS_Y = 164;

	public static void PlayerUpdateKills(int ppnum, int value) {
		if (Prediction)
			return;

		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE)
			return;

		PlayerStr pp = Player[ppnum];
		// Team play
		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT && gNet.TeamPlay
				&& game.getScreen() instanceof GameAdapter) {
			short pnum;
			PlayerStr opp;
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				opp = Player[pnum];

				// for everyone on the same team
				if (opp != pp && pUser[opp.PlayerSprite].spal == pUser[pp.PlayerSprite].spal) {
					opp.Kills += value;
					if (opp.Kills > 999)
						opp.Kills = 0;
					if (opp.Kills < -99)
						opp.Kills = -99;
				}
			}
		}

		pp.Kills += value;
		if (pp.Kills > 999)
			pp.Kills = 0;
		if (pp.Kills < -99)
			pp.Kills = -99;
	}

	public static final int PANEL_ARMOR_BOX_X = 56;
	public static final int PANEL_ARMOR_XOFF = 2;
	public static final int PANEL_ARMOR_YOFF = 4;
	public static final int ARMOR_ERASE = 2401;

	public static void PlayerUpdateArmor(PlayerStr pp, int value) {
		if (Prediction)
			return;

		if (value == 1050) // Got 50 armor
			pp.Armor = 50;
		else if (value == 1100) // Got 100 armor
			pp.Armor = 100;
		else
			pp.Armor += value;

		if (pp.Armor > 100)
			pp.Armor = 100;
		if (pp.Armor < 0)
			pp.Armor = 0;
	}

	public static final int PANEL_KEYS_BOX_X = 276;
	public static final int PANEL_KEYS_XOFF = 0;
	public static final int PANEL_KEYS_YOFF = 2;
	public static final int KEYS_ERASE = 2402;

	public static final int PANEL_KEY_RED = 2392;
	public static final int PANEL_KEY_GREEN = 2393;
	public static final int PANEL_KEY_BLUE = 2394;
	public static final int PANEL_KEY_YELLOW = 2395;
	public static final int PANEL_SKELKEY_GOLD = 2448;
	public static final int PANEL_SKELKEY_SILVER = 2449;
	public static final int PANEL_SKELKEY_BRONZE = 2458;
	public static final int PANEL_SKELKEY_RED = 2459;

	private static short StatusKeyPics[] = { PANEL_KEY_RED, PANEL_KEY_BLUE, PANEL_KEY_GREEN, PANEL_KEY_YELLOW,
			PANEL_SKELKEY_GOLD, PANEL_SKELKEY_SILVER, PANEL_SKELKEY_BRONZE, PANEL_SKELKEY_RED };

	public static boolean PlayerUpdateKeys(PlayerStr pp, int xorig, int yorig, int flags) {
		int x, y;
		short row, col;

		if (Prediction)
			return false;

		if (pp.pnum != screenpeek)
			return false;

		boolean hasKeys = false;
		int xsize = engine.getTile(PANEL_KEY_RED).getWidth() + 1;
		int ysize = engine.getTile(PANEL_KEY_RED).getHeight() + 2;

		int i = 0;
		for (row = 0; row < 2; row++) {
			for (col = 0; col < 2; col++) {
				if (pp.HasKey[i] != 0) {
					x = (xorig + (row * xsize));
					y = (yorig + (col * ysize));

					engine.rotatesprite(x << 16, y << 16, 1 << 16, 0, StatusKeyPics[i], 0, 0,
							ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | flags, 0, 0, xdim - 1, ydim - 1);
					hasKeys = true;
				}

				i++;
			}
		}

		// Check for skeleton keys
		i = 0;
		for (row = 0; row < 2; row++) {
			for (col = 0; col < 2; col++) {
				if (pp.HasKey[i + 4] != 0) {
					x = (xorig + (row * xsize));
					y = (yorig + (col * ysize));

					engine.rotatesprite(x << 16, y << 16, 1 << 16, 0, StatusKeyPics[i + 4], 0, 0,
							ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | flags, 0, 0, xdim - 1, ydim - 1);
					hasKeys = true;
				}

				i++;
			}
		}

		return hasKeys;
	}

	private static void PlayerUpdateTimeLimit(PlayerStr pp) {
		if (Prediction)
			return;

		if (gs.BorderNum < BORDER_BAR || pp.pnum != screenpeek)
			return;

		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT)
			return;

		if (gNet.TimeLimit == 0)
			return;

		int seconds = gNet.TimeLimitClock / 120;
		int offs = Bitoa(seconds / 60, wsum_fmt2, 2);
		buildString(wsum_fmt2, offs, ":", seconds % 60, 2);
		DisplaySummaryString(pp, PANEL_KEYS_BOX_X + 3, PANEL_BOX_Y + 7, 0, 0, wsum_fmt2, 0);
	}

	public static void PlayerUpdatePanelInfo(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];

		if (Prediction)
			return;

		int x = PANEL_HEALTH_BOX_X + PANEL_HEALTH_XOFF;
		int y = PANEL_BOX_Y + PANEL_HEALTH_YOFF;
		DisplayPanelNumber(pp, x, y, u.Health, 0);

		DrawInventory(pp, INVENTORY_BOX_X, INVENTORY_BOX_Y, 0);

		if (u.WeaponNum != WPN_SWORD && u.WeaponNum != WPN_FIST) {
			x = PANEL_AMMO_BOX_X + PANEL_AMMO_XOFF;
			y = PANEL_BOX_Y + PANEL_AMMO_YOFF;
			DisplayPanelNumber(pp, x, y, pp.WpnAmmo[u.WeaponNum], 0);
		}

		if (gs.BorderNum >= BORDER_BAR && gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT)
			PlayerUpdateKeys(pp, PANEL_KEYS_BOX_X + PANEL_KEYS_XOFF, PANEL_BOX_Y + PANEL_KEYS_YOFF, 0);

		x = PANEL_ARMOR_BOX_X + PANEL_ARMOR_XOFF;
		y = PANEL_BOX_Y + PANEL_ARMOR_YOFF;
		DisplayPanelNumber(pp, x, y, pp.Armor, 0);

		PlayerUpdateWeaponSummaryAll(pp);

		PlayerUpdateTimeLimit(pp);
	}

	public static void WeaponOperate(PlayerStr pp) {
		short weapon;
		USER u = pUser[pp.PlayerSprite];

		InventoryKeys(pp);

		// UziType >= 3 means they are reloading
		if (pp.WpnUziType >= 3)
			return; // (true);

		if (pp.FirePause != 0) {
			pp.FirePause -= synctics;
			if (pp.FirePause <= 0)
				pp.FirePause = 0;
		}

		if (pp.sop != -1) {
			switch (SectorObject[pp.sop].track) {
			case SO_TANK:
			case SO_TURRET:
			case SO_TURRET_MGUN:
			case SO_SPEED_BOAT:

				if (!TEST(SectorObject[pp.sop].flags, SOBJ_HAS_WEAPON))
					break;

				if (TEST_SYNC_KEY(pp, SK_SHOOT)) {
					if (FLAG_KEY_PRESSED(pp, SK_SHOOT)) {

						if (pp.FirePause == 0) {
							InitSobjGun(pp);
						}
					}
				}

				return; // (0);
			}
		}

		weapon = (short) DTEST(pp.input.bits, SK_WEAPON_MASK);
		if (weapon != 0) {
			if (FLAG_KEY_PRESSED(pp, SK_WEAPON_BIT0)) {
				FLAG_KEY_RELEASE(pp, SK_WEAPON_BIT0);

				weapon -= 1;
				// Special uzi crap
				if (weapon != WPN_UZI)
					pp.WpnUziType = 2; // This means we aren't on uzi

				switch (weapon) {
				case WPN_FIST:
					if (u.WeaponNum == WPN_FIST || u.WeaponNum == WPN_SWORD) {
						// toggle
						if (pp.WpnFirstType == WPN_FIST)
							pp.WpnFirstType = WPN_SWORD;
						else if (pp.WpnFirstType == WPN_SWORD)
							pp.WpnFirstType = WPN_FIST;
					}

					switch (pp.WpnFirstType) {
					case WPN_SWORD:
						InitWeaponSword(pp);
						break;
					case WPN_FIST:
						InitWeaponFist(pp);
						break;
					}
					break;
				case WPN_STAR:
					InitWeaponStar(pp);
					break;
				case WPN_UZI:
					if (u.WeaponNum == WPN_UZI) {
						if (TEST(pp.Flags, PF_TWO_UZI)) {
							pp.WpnUziType++;
							if (pp.WpnUziType > 1)
								pp.WpnUziType = 0;
						} else
							pp.WpnUziType = 1; // Use retracted state for single uzi
					}
					InitWeaponUzi(pp);
					break;
				case WPN_MICRO:
					if (u.WeaponNum == WPN_MICRO) {
						pp.WpnRocketType++;
						if (pp.WpnRocketType > 2)
							pp.WpnRocketType = 0;
						if (pp.WpnRocketType == 2 && pp.WpnRocketNuke == 0)
							pp.WpnRocketType = 0;
						if (pp.WpnRocketType == 2)
							pp.TestNukeInit = true; // Init the nuke
						else
							pp.TestNukeInit = false;
					}
					InitWeaponMicro(pp);
					break;
				case WPN_SHOTGUN:
					if (u.WeaponNum == WPN_SHOTGUN) {
						pp.WpnShotgunType++;
						if (pp.WpnShotgunType > 1)
							pp.WpnShotgunType = 0;
						PlaySound(DIGI_SHOTGUN_UP, pp, v3df_follow);
					}
					InitWeaponShotgun(pp);
					break;
				case WPN_RAIL:
					InitWeaponRail(pp);
					break;
				case WPN_HOTHEAD:

					if (u.WeaponNum == WPN_HOTHEAD || u.WeaponNum == WPN_RING || u.WeaponNum == WPN_NAPALM) {
						pp.WpnFlameType++;
						if (pp.WpnFlameType > 2)
							pp.WpnFlameType = 0;
						pHotHeadOverlays(pp.Wpn[WPN_HOTHEAD], pp.WpnFlameType);
						PlaySound(DIGI_HOTHEADSWITCH, pp, v3df_dontpan | v3df_follow);
					}

					InitWeaponHothead(pp);

					break;
				case WPN_HEART:

					InitWeaponHeart(pp);

					break;
				case WPN_GRENADE:
					InitWeaponGrenade(pp);
					break;
				case WPN_MINE:
					// InitChops(pp);
					InitWeaponMine(pp);
					break;
				case 13:
					pp.WpnFirstType = WPN_FIST;
					InitWeaponFist(pp);
					break;
				case 14:
					pp.WpnFirstType = WPN_SWORD;
					InitWeaponSword(pp);
					break;
				}
			}
		} else {
			FLAG_KEY_RESET(pp, SK_WEAPON_BIT0);
		}

		// Shut that computer chick up if weapon has changed!
		if (pp.WpnRocketType != 2 || pp.CurWpn != pp.Wpn[WPN_MICRO]) {
			pp.InitingNuke = false;
			if (pp.nukevochandle != null && pp.nukevochandle.handle != null) {
				pp.nukevochandle.stop();
				pp.nukevochandle = null;
			}
		}
	}

	public static final short wpn_order[] = { 2, 3, 4, 5, 6, 7, 8, 9, 1, 0 };

	public static boolean WeaponOK(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		short min_ammo, WeaponNum, FindWeaponNum;

		short wpn_ndx = 0;

		// sword
		if (DamageData[u.WeaponNum].max_ammo == -1)
			return (true);

		WeaponNum = u.WeaponNum;
		FindWeaponNum = u.WeaponNum;

		min_ammo = DamageData[WeaponNum].min_ammo;

		// if ran out of ammo switch to something else
		if (pp.WpnAmmo[WeaponNum] < min_ammo) {
			pp.last_used_weapon = (byte) WeaponNum;
			if (u.WeaponNum == WPN_UZI)
				pp.WpnUziType = 2; // Set it for retract

			// Still got a nuke, it's ok.
			if (WeaponNum == WPN_MICRO && pp.WpnRocketNuke != 0) {
				if (!pp.NukeInitialized)
					pp.TestNukeInit = true;

				u.WeaponNum = WPN_MICRO;
				DamageData[u.WeaponNum].init.invoke(pp);

				return (true);
			}

			FLAG_KEY_RELEASE(pp, SK_SHOOT);

			FindWeaponNum = WPN_SHOTGUN; // Start at the top

			while (true) {
				// ran out of weapons - choose SWORD
				if (wpn_ndx >= wpn_order.length) {
					FindWeaponNum = WPN_SWORD;
					break;
				}

				// if you have the weapon and the ammo is greater than 0
				if (TEST(pp.WpnFlags, BIT(FindWeaponNum)) && pp.WpnAmmo[FindWeaponNum] >= min_ammo)
					break;

				wpn_ndx++;
				FindWeaponNum = wpn_order[wpn_ndx];
			}
			u.WeaponNum = FindWeaponNum;

			if (u.WeaponNum == WPN_HOTHEAD) {
				pp.WeaponType = WPN_HOTHEAD;
				pp.WpnFlameType = 0;
			}

			DamageData[u.WeaponNum].init.invoke(pp);

			return (false);
		}

		return (true);
	}

	private static final Panel_Sprite_Func SpecialUziRetractFunc = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite p) {
			SpecialUziRetractFunc(p);
		}
	};

	public static void SpecialUziRetractFunc(Panel_Sprite psp) {
		psp.y += 4 * synctics;

		if (psp.y >= 200 + engine.getTile(psp.picnum).getHeight()) {
			pKillSprite(psp);
		}
	}

	public static void RetractCurWpn(PlayerStr pp) {
		// Retract old weapon
		if (pp.CurWpn != null) {
			if ((pp.CurWpn == pp.Wpn[WPN_UZI] && pp.WpnUziType == 2) || pp.CurWpn != pp.Wpn[WPN_UZI]) {
				pSetState(pp.CurWpn, pp.CurWpn.RetractState);
				pp.Flags |= (PF_WEAPON_RETRACT);
			}

			if (pp.CurWpn.sibling != null) {
				// primarily for double uzi to take down the second uzi
				pSetState(pp.CurWpn.sibling, pp.CurWpn.sibling.RetractState);
			} else {
				// check for any outstanding siblings that need to go away also
				Panel_Sprite n;
				for (Panel_Sprite cur = pp.PanelSpriteList.Next; cur != pp.PanelSpriteList; cur = n) {
					n = cur.Next;

					if (cur.sibling != null && cur.sibling == pp.CurWpn) {
						// special case for uzi reload pieces
						cur.picnum = cur.picndx;
						cur.State = null;
						cur.PanelSpriteFunc = SpecialUziRetractFunc;
						cur.sibling = null;
					}
				}
			}
		}
	}

	public static int castLongX(Panel_Sprite psp) {
		return (psp.xfract & 0xFFFF) | (psp.x << 16);
	}

	public static int castLongY(Panel_Sprite psp) {
		return (psp.yfract & 0xFFFF) | (psp.y << 16);
	}

	public static void setLong(Panel_Sprite psp, int x, int y) {
		psp.xfract = (short) x;
		psp.yfract = (short) y;
		psp.x = (short) (x >> 16);
		psp.y = (short) (y >> 16);
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// PANEL SPRITE GENERAL ROUTINES
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	public static void pWeaponForceRest(PlayerStr pp) {
		pSetState(pp.CurWpn, pp.CurWpn.RestState);
	}

	public static boolean pWeaponUnHideKeys(Panel_Sprite psp, Panel_State state) {
		// initing the other weapon will take care of this
		if (TEST(psp.flags, PANF_DEATH_HIDE)) {
			return (false);
		}

		PlayerStr pp = Player[psp.PlayerP];
		if (TEST(psp.flags, PANF_WEAPON_HIDE)) {
			if (!TEST(pp.Flags, PF_WEAPON_DOWN)) {
				psp.flags &= ~(PANF_WEAPON_HIDE);
				pSetState(psp, state);
				return (true);
			}

			return (false);
		}

		if (TEST_SYNC_KEY(pp, SK_HIDE_WEAPON)) {
			if (FLAG_KEY_PRESSED(pp, SK_HIDE_WEAPON)) {
				FLAG_KEY_RELEASE(pp, SK_HIDE_WEAPON);
				pSetState(psp, state);
				return (true);
			}
		} else {
			FLAG_KEY_RESET(pp, SK_HIDE_WEAPON);
		}

		if (TEST_SYNC_KEY(pp, SK_SHOOT)) {
			if (FLAG_KEY_PRESSED(pp, SK_SHOOT)) {
				psp.flags |= (PANF_UNHIDE_SHOOT);
				pSetState(psp, state);
				return (true);
			}
		}

		return (false);
	}

	public static boolean pWeaponHideKeys(Panel_Sprite psp, Panel_State state) {
		PlayerStr pp = Player[psp.PlayerP];

		if (TEST(pp.Flags, PF_DEAD)) {
			psp.flags |= (PANF_DEATH_HIDE);
			pSetState(psp, state);
			return (true);
		}

		if (TEST(pp.Flags, PF_WEAPON_DOWN)) {
			psp.flags |= (PANF_WEAPON_HIDE);
			pSetState(psp, state);
			return (true);
		}

		if (TEST_SYNC_KEY(pp, SK_HIDE_WEAPON)) {
			if (FLAG_KEY_PRESSED(pp, SK_HIDE_WEAPON)) {
				PutStringInfo(pp, "Weapon Holstered");
				FLAG_KEY_RELEASE(pp, SK_HIDE_WEAPON);
				pSetState(psp, state);
				return (true);
			}
		} else {
			FLAG_KEY_RESET(pp, SK_HIDE_WEAPON);
		}

		return (false);

	}

	public static void InsertPanelSprite(PlayerStr pp, Panel_Sprite psp) {
		// if list is empty, insert at front
		if (List.IsEmpty(pp.PanelSpriteList)) {
			List.Insert(pp.PanelSpriteList, psp);
			return;
		}

		// if new pri is <= first pri in list, insert at front
		if (psp.priority <= pp.PanelSpriteList.Next.priority) {
			List.Insert(pp.PanelSpriteList, psp);
			return;
		}

		// search for first pri in list thats less than the new pri
		Panel_Sprite n;
		for (Panel_Sprite cur = pp.PanelSpriteList.Next; cur != pp.PanelSpriteList; cur = n) {
			n = cur.Next;
			// if the next pointer is the end of the list, insert it
			if (cur.Next == pp.PanelSpriteList) {
				List.Insert(cur, psp);
				return;
			}

			// if between cur and next, insert here
			if (psp.priority >= cur.priority && psp.priority <= cur.Next.priority) {
				List.Insert(cur, psp);
				return;
			}
		}
	}

	public static Panel_Sprite pSpawnSprite(PlayerStr pp, Panel_State state, int priority, int x, int y) {
		int i;
		Panel_Sprite psp = panelSpriteList.obtain();

		psp.priority = priority;
		InsertPanelSprite(pp, psp);

		psp.PlayerP = pp.pnum;

		psp.x = x;
		psp.y = y;
		pSetState(psp, state);
		if (state == null)
			psp.picndx = -1;
		else
			psp.picndx = state.picndx;

		psp.ang = 0;
		psp.vel = 0;
		psp.rotate_ang = 0;
		psp.scale = 1 << 16;
		psp.ID = 0;

		for (i = 0; i < psp.over.length; i++) {
			psp.over[i].State = null;
			psp.over[i].pic = -1;
			psp.over[i].xoff = -1;
			psp.over[i].yoff = -1;
		}

		return (psp);
	}

	public static void pSuicide(Panel_Sprite psp) {
		pKillSprite(psp);
	}

	public static void pKillSprite(Panel_Sprite psp) {
		List.Delete(psp);
		panelSpriteList.free(psp);
	}

	public static void pClearSpriteList(PlayerStr pp) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;
			pKillSprite(psp);
		}
	}

	public static void pWeaponBob(Panel_Sprite psp, boolean condition) {
		int xdiff = 0, ydiff = 0;
		short bob_amt, bob_ndx;
		short bobvel;
		PlayerStr pp = Player[psp.PlayerP];

		bobvel = (short) (FindDistance2D(pp.xvect, pp.yvect) >> 15);
		bobvel = (short) (bobvel + DIV4(bobvel));
		bobvel = (short) Math.min(bobvel, 128);

		if (condition) {
			psp.flags |= (PANF_BOB);
		} else {
			if (klabs((psp.sin_ndx & 1023) - 0) < 70) {
				psp.flags &= ~(PANF_BOB);
				psp.sin_ndx = (short) ((RANDOM_P2(1024) < 512) ? 1024 : 0);
			}
		}

		if (TEST(psp.flags, PANF_BOB)) {
			// //
			// sin_xxx moves the weapon left-right
			// //

			// increment the ndx into the sin table
			psp.sin_ndx = (short) (psp.sin_ndx + (synctics << 3));
			// add a random factor to it
			psp.sin_ndx += (RANDOM_P2(8) * synctics);
			// wrap
			psp.sin_ndx &= 2047;

			// get height
			xdiff = psp.sin_amt * sintable[psp.sin_ndx] >> 14;

			// //
			// bob_xxx moves the weapon up-down
			// //

			// as the weapon moves left-right move the weapon up-down in the same
			// proportion
			bob_ndx = (short) ((psp.sin_ndx + 512) & 1023);

			// base bob_amt on the players velocity - Max of 128
			bob_amt = (short) (bobvel >> psp.bob_height_shift);
			ydiff = bob_amt * sintable[bob_ndx] >> 14;
		}

		psp.x = psp.xorig + xdiff;
		psp.y = psp.yorig + ydiff + UziRecoilYadj;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// PANEL SPRITE CONTROL ROUTINES
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	public static boolean DrawBeforeView = false;

	public static void pDisplaySprites(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		short shade, picnum, overlay_shade = 0;
		int x, y;
		int i;

		if (!isValidSector(pp.cursectnum))
			return;

		Sect_User sectu = SectUser[pp.cursectnum];
		int pal = 0;
		short ang;
		int flags;
		int x1, y1, x2, y2;

		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;

			ang = psp.rotate_ang;
			shade = 0;
			flags = 0;
			x = psp.x;
			y = psp.y;
			// initilize pal here - jack with it below
			pal = psp.pal;

			if (DrawBeforeView)
				if (!TEST(psp.flags, PANF_DRAW_BEFORE_VIEW))
					continue;

			if (TEST(psp.flags, PANF_SUICIDE)) {
				continue;
			}

			// force kill before showing again
			if (TEST(psp.flags, PANF_KILL_AFTER_SHOW) && psp.numpages == 0) {
				pKillSprite(psp);
				continue;
			}

			// if the state is null get the picnum for other than picndx
			if (psp.picndx == -1 || psp.State == null)
				picnum = psp.picnum;
			else
				picnum = psp.picndx;

			// UK panzies have to have darts instead of shurikens.
			if (gs.UseDarts)
				switch (picnum) {
				case STAR_REST:
					picnum = 2510;
					break;
				case STAR_REST + 1:
					picnum = 2511;
					break;
				case STAR_REST + 2:
					picnum = 2512;
					break;
				case STAR_REST + 3:
					picnum = 2513;
					break;
				case STAR_REST + 4:
					picnum = 2514;
					break;
				case STAR_REST + 5:
					picnum = 2515;
					break;
				case STAR_REST + 6:
					picnum = 2516;
					break;
				case STAR_REST + 7:
					picnum = 2517;
					break;
				}

			if (pp.Bloody && !gs.ParentalLock && !Global_PLock) {
				switch (picnum) {
				case SWORD_REST:
					picnum = BLOODYSWORD_REST;
					break;
				case SWORD_SWING0:
					picnum = BLOODYSWORD_SWING0;
					break;
				case SWORD_SWING1:
					picnum = BLOODYSWORD_SWING1;
					break;
				case SWORD_SWING2:
					picnum = BLOODYSWORD_SWING2;
					break;
				case FIST_REST:
					picnum = 4077;
					break;
				case FIST2_REST:
					picnum = 4051;
					break;
				case FIST_SWING0:
					picnum = 4074;
					break;
				case FIST_SWING1:
					picnum = 4075;
					break;
				case FIST_SWING2:
					picnum = 4076;
					break;
				case STAR_REST:
					if (!gs.UseDarts)
						picnum = 2138;
					else
						picnum = 2518; // Bloody Dart Hand
					break;
				}
			}

			if (pp.WpnShotgunType == 1) {
				switch (picnum) {
				case SHOTGUN_REST:
				case SHOTGUN_RELOAD0:
					picnum = 2227;
					break;
				case SHOTGUN_RELOAD1:
					picnum = 2226;
					break;
				case SHOTGUN_RELOAD2:
					picnum = 2225;
					break;
				}
			}

			// don't draw
			if (TEST(psp.flags, PANF_INVISIBLE))
				continue;

			if (psp.State != null && psp.State.testFlag(psf_Invisible))
				continue;

			// if its a weapon sprite and the view is set to the outside don't draw the
			// sprite
			if (TEST(psp.flags, PANF_WEAPON_SPRITE)) {
				pal = sector[pp.cursectnum].floorpal;
				flags |= ROTATE_SPRITE_SCREEN_CLIP;

				if (gs.BorderNum >= BORDER_BAR)
					y -= 24;

				x -= (pp.lookang >> 1);
				y += (klabs(pp.lookang) / 9);

				if (sector[pp.cursectnum].floorpal != PALETTE_DEFAULT) {
					sectu = SectUser[pp.cursectnum];
					if (sectu != null && TEST(sectu.flags, SECTFU_DONT_COPY_PALETTE))
						pal = PALETTE_DEFAULT;
				}

				if (pal == PALETTE_FOG || pal == PALETTE_DIVE || pal == PALETTE_DIVE_LAVA)
					pal = psp.pal; // Set it back

				///////////

				if (pp.InventoryActive[INVENTORY_CLOAK]) {
					flags |= (ROTATE_SPRITE_TRANSLUCENT);
				}

				shade = overlay_shade = (short) (sector[pp.cursectnum].floorshade - 10);
				// !FRANK - this was moved from BELOW this IF statement
				// if it doesn't have a picflag or its in the view
				if (sectu != null && TEST(sectu.flags, SECTFU_DONT_COPY_PALETTE))
					pal = 0;
			}

			flags |= (ROTATE_SPRITE_VIEW_CLIP);

			if (TEST(psp.flags, PANF_TRANSLUCENT))
				flags |= (ROTATE_SPRITE_TRANSLUCENT);

			flags |= (DTEST(psp.flags, PANF_TRANS_FLIP));

			if (TEST(psp.flags, PANF_CORNER))
				flags |= (ROTATE_SPRITE_CORNER);

			if (TEST(psp.flags, PANF_STATUS_AREA)) {
				flags |= (ROTATE_SPRITE_CORNER);
				flags &= ~(ROTATE_SPRITE_VIEW_CLIP);

				if (TEST(psp.flags, PANF_SCREEN_CLIP))
					flags |= (ROTATE_SPRITE_SCREEN_CLIP);

				if (TEST(psp.flags, PANF_IGNORE_START_MOST))
					flags |= (ROTATE_SPRITE_IGNORE_START_MOST);

				x1 = psp.x1;
				y1 = psp.y1;
				x2 = psp.x2;
				y2 = psp.y2;
				shade = psp.shade;
			} else {
				x1 = windowx1;
				y1 = windowy1;
				x2 = windowx2;
				y2 = windowy2;
			}

			if ((psp.State != null && psp.State.testFlag(psf_Xflip)) || TEST(psp.flags, PANF_XFLIP)) {
				// this is what you have to do to x-flip
				ang = NORM_ANGLE(ang + 1024);
				flags |= (ROTATE_SPRITE_YFLIP);
			}

			// shading
			if (psp.State != null && psp.State.testFlag(psf_ShadeHalf | psf_ShadeNone)) {
				if (psp.State.testFlag(psf_ShadeNone))
					shade = 0;
				else if (psp.State.testFlag(psf_ShadeHalf))
					shade /= 2;
			}

			if (pal == PALETTE_DEFAULT) {
				switch (picnum) {
				case 4080:
				case 4081:
				case 2220:
				case 2221:
					pal = u.spal;
					break;
				}
			}

			if (TEST(psp.flags, PANF_KILL_AFTER_SHOW) && !TEST(psp.flags, PANF_NOT_ALL_PAGES)) {
				psp.numpages = 0;
				flags |= (ROTATE_SPRITE_ALL_PAGES);
			}

			engine.rotatesprite(x << 16, y << 16, psp.scale, ang, picnum, shade, pal, flags, x1, y1, x2, y2);

			// do overlays (if any)
			for (i = 0; i < psp.over.length; i++) {
				// get pic from state
				if (psp.over[i].State != null)
					picnum = psp.over[i].State.picndx;
				else
				// get pic from over variable
				if (psp.over[i].pic >= 0)
					picnum = psp.over[i].pic;
				else
					continue;

				if (TEST(psp.over[i].flags, psf_ShadeNone))
					overlay_shade = 0;

				if (picnum != 0) {
					engine.rotatesprite((x + psp.over[i].xoff) << 16, (y + psp.over[i].yoff) << 16, psp.scale, ang,
							picnum, overlay_shade, pal, flags, x1, y1, x2, y2);
				}
			}

			if (TEST(psp.flags, PANF_KILL_AFTER_SHOW)) {
				psp.numpages--;
				if (psp.numpages <= 0)
					pKillSprite(psp);
			}
		}
	}

	public static void pFlushPerms(PlayerStr pp) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;

			// force kill before showing again
			if (TEST(psp.flags, PANF_KILL_AFTER_SHOW)) {
				pKillSprite(psp);
			}
		}
	}

	public static void pSpriteControl(PlayerStr pp) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;
			// reminder - if these give an assertion look for pKillSprites
			// somewhere else other than by themselves
			// RULE: Sprites can only kill themselves

			if (psp.State != null)
				pStateControl(psp);
			else
			// for sprits that are not state controled but still need to call
			// something
			if (psp.PanelSpriteFunc != null) {
				psp.PanelSpriteFunc.invoke(psp);
			}
		}
	}

	public static void pSetState(Panel_Sprite psp, Panel_State Panel_State) {
		psp.tics = 0;
		psp.State = Panel_State;
		if (Panel_State != null)
			psp.picndx = Panel_State.picndx;
	}

	public static void pNextState(Panel_Sprite psp) {
		// Transition to the next state
		psp.State = psp.State.getNext();

		if (psp.State.testFlag(psf_QuickCall)) {
			if (psp.State.Animator != null)
				psp.State.Animator.invoke(psp);
			psp.State = psp.State.getNext();
		}
	}

	public static void pStatePlusOne(Panel_Sprite psp) {
		psp.tics = 0;
		psp.State = psp.State.getPlusOne();

		if (psp.State.testFlag(psf_QuickCall)) {
			if (psp.State.Animator != null)
				psp.State.Animator.invoke(psp);
			psp.State = psp.State.getNext();
		}
	}

	public static void pStateControl(Panel_Sprite psp) {
		short i;
		short tics = synctics;

		psp.tics += tics;

		// Skip states if too much time has passed
		while (psp.tics >= psp.State.tics) {
			// Set Tics
			psp.tics -= psp.State.tics;
			pNextState(psp);
		}

		// Set picnum to the correct pic
		psp.picndx = psp.State.picndx;

		// do overlay states
		for (i = 0; i < psp.over.length; i++) {
			if (psp.over[i].State == null)
				continue;

			psp.over[i].tics += tics;

			// Skip states if too much time has passed
			while (psp.over[i].tics >= psp.over[i].State.tics) {
				// Set Tics
				psp.over[i].tics -= psp.over[i].State.tics;
				psp.over[i].State = psp.over[i].State.getNext();

				if (psp.over[i].State == null)
					break;
			}
		}

		// Call the correct animator
		if (psp.State.Animator != null)
			psp.State.Animator.invoke(psp);
	}

	public static void UpdatePanel() {
		if (DebugPanel)
			return;

		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			if (dimensionmode != 6 && pnum == screenpeek
					&& !TEST(Player[pnum].Flags, PF_VIEW_FROM_OUTSIDE | PF_VIEW_FROM_CAMERA))
				pDisplaySprites(Player[pnum]);
		}
	}

	public static void PreUpdatePanel() {
		if (DebugPanel)
			return;

		DrawBeforeView = true;

		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			if (dimensionmode != 2 && pnum == screenpeek)
				pDisplaySprites(Player[pnum]);
		}

		DrawBeforeView = false;
	}

	public static final int EnvironSuit_RATE = 10;
	public static final int Fly_RATE = 10;
	public static final int Cloak_RATE = 10;
	public static final int Night_RATE = 10;
	public static final int Box_RATE = 10;
	public static final int Medkit_RATE = 10;
	public static final int RepairKit_RATE = 10;
	public static final int ChemBomb_RATE = 10;
	public static final int FlashBomb_RATE = 10;
	public static final int SmokeBomb_RATE = 10;
	public static final int Caltrops_RATE = 10;

	private static final Panel_Sprite_Func PanelInvTestSuicide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite p) {
			PanelInvTestSuicide(p);
		}
	};

	public static final int ID_PanelEnvironSuit = 2397;
	public static final Panel_State ps_PanelEnvironSuit[] = {
			new Panel_State(ID_PanelEnvironSuit, EnvironSuit_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelCloak = 2397; // 2400
	public static final Panel_State ps_PanelCloak[] = {
			new Panel_State(ID_PanelCloak, Cloak_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelRepairKit = 2399;
	public static final Panel_State ps_PanelRepairKit[] = {
			new Panel_State(ID_PanelRepairKit, RepairKit_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelMedkit = 2396;
	public static final Panel_State ps_PanelMedkit[] = {
			new Panel_State(ID_PanelMedkit, Medkit_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelNightVision = 2398;
	public static final Panel_State ps_PanelNightVision[] = {
			new Panel_State(ID_PanelNightVision, Night_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelChemBomb = 2407;
	public static final Panel_State ps_PanelChemBomb[] = {
			new Panel_State(ID_PanelChemBomb, ChemBomb_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelFlashBomb = 2408;
	public static final Panel_State ps_PanelFlashBomb[] = {
			new Panel_State(ID_PanelFlashBomb, FlashBomb_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_PanelCaltrops = 2409;
	public static final Panel_State ps_PanelCaltrops[] = {
			new Panel_State(ID_PanelCaltrops, Caltrops_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_SelectionBox = 2435;
	public static final Panel_State ps_PanelSelectionBox[] = {
			new Panel_State(ID_SelectionBox, Box_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_KeyRed = 2392;
	public static final Panel_State ps_PanelKeyRed[] = {
			new Panel_State(ID_KeyRed, Box_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_KeyGreen = 2393;
	public static final Panel_State ps_PanelKeyGreen[] = {
			new Panel_State(ID_KeyGreen, Box_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_KeyBlue = 2394;
	public static final Panel_State ps_PanelKeyBlue[] = {
			new Panel_State(ID_KeyBlue, Box_RATE, PanelInvTestSuicide).setNext(), };

	public static final int ID_KeyYellow = 2395;
	public static final Panel_State ps_PanelKeyYellow[] = {
			new Panel_State(ID_KeyYellow, Box_RATE, PanelInvTestSuicide).setNext(), };

	public static void PanelSaveable() {
		SwordSaveable();
		StarSaveable();
		UziSaveable();
		ShotgunSaveable();
		RailSaveable();
		HotHeadSaveable();
		MicroSaveable();
		HeartSaveable();
		GrenadeSaveable();
		MineSaveable();
		ChopsSaveable();
		FistSaveable();

		SaveData(pSuicide);
		SaveData(SpecialUziRetractFunc);
		SaveData(PanelInvTestSuicide);

		SaveData(ps_PanelEnvironSuit);
		SaveData(ps_PanelCloak);
		SaveData(ps_PanelRepairKit);
		SaveData(ps_PanelMedkit);
		SaveData(ps_PanelNightVision);
		SaveData(ps_PanelChemBomb);
		SaveData(ps_PanelFlashBomb);
		SaveData(ps_PanelCaltrops);
		SaveData(ps_PanelSelectionBox);
		SaveData(ps_PanelKeyRed);
		SaveData(ps_PanelKeyGreen);
		SaveData(ps_PanelKeyBlue);
		SaveData(ps_PanelKeyYellow);
	}
}
