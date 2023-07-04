package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Wang.Digi.DIGI_AHH;
import static ru.m210projects.Wang.Digi.DIGI_GASPOP;
import static ru.m210projects.Wang.Digi.DIGI_GETMEDKIT;
import static ru.m210projects.Wang.Digi.DIGI_IAMSHADOW;
import static ru.m210projects.Wang.Digi.DIGI_NIGHTOFF;
import static ru.m210projects.Wang.Digi.DIGI_NIGHTON;
import static ru.m210projects.Wang.Digi.DIGI_NOREPAIRMAN;
import static ru.m210projects.Wang.Digi.DIGI_NOREPAIRMAN2;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.PF_CLIMBING;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_CORNER;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SK_INV_HOTKEY_BIT0;
import static ru.m210projects.Wang.Gameutils.SK_INV_HOTKEY_MASK;
import static ru.m210projects.Wang.Gameutils.SK_INV_LEFT;
import static ru.m210projects.Wang.Gameutils.SK_INV_RIGHT;
import static ru.m210projects.Wang.Gameutils.SK_INV_USE;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.JWeapon.PlayerInitCaltrops;
import static ru.m210projects.Wang.JWeapon.PlayerInitChemBomb;
import static ru.m210projects.Wang.JWeapon.PlayerInitFlashBomb;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Panel.BORDER_BAR;
import static ru.m210projects.Wang.Panel.PANF_SCREEN_CLIP;
import static ru.m210projects.Wang.Panel.PANF_STATUS_AREA;
import static ru.m210projects.Wang.Panel.PANF_SUICIDE;
import static ru.m210projects.Wang.Panel.PRI_FRONT;
import static ru.m210projects.Wang.Panel.PlayerUpdateHealth;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.ps_PanelCaltrops;
import static ru.m210projects.Wang.Panel.ps_PanelChemBomb;
import static ru.m210projects.Wang.Panel.ps_PanelCloak;
import static ru.m210projects.Wang.Panel.ps_PanelFlashBomb;
import static ru.m210projects.Wang.Panel.ps_PanelMedkit;
import static ru.m210projects.Wang.Panel.ps_PanelNightVision;
import static ru.m210projects.Wang.Panel.ps_PanelRepairKit;
import static ru.m210projects.Wang.Panel.ps_PanelSelectionBox;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Text.DisplayMiniBarSmString;
import static ru.m210projects.Wang.Text.DisplaySmString;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Player.Player_Action_Func;
import ru.m210projects.Wang.Type.INVENTORY_DATA;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class Inv {

	public interface Inventory_Stop_Func {
		public void invoke(PlayerStr pp, int num);
	}

	public static final int INVENTORY_MEDKIT = 0, INVENTORY_REPAIR_KIT = 1, INVENTORY_CLOAK = 2, // de-cloak when firing
			INVENTORY_NIGHT_VISION = 3, INVENTORY_CHEMBOMB = 4, INVENTORY_FLASHBOMB = 5, INVENTORY_CALTROPS = 6,
			MAX_INVENTORY = 7;

	private static final int INVF_AUTO_USE = (BIT(0));
	private static final int INVF_TIMED = (BIT(1));
	private static final int INVF_COUNT = (BIT(2));

	// indexed by gs.BorderNum 130,172
	private static final short InventoryBarXpos[] = { 110, 130, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80,
			80 };
	private static final short InventoryBarYpos[] = { 172, 172, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130, 130,
			130, 130, 130, 130 };

	private static final int INVENTORY_ICON_WIDTH = 28;

	private static final Inventory_Stop_Func StopInventoryCloak = new Inventory_Stop_Func() {
		@Override
		public void invoke(PlayerStr pp, int num) {
			StopInventoryCloak(pp, num);
		}
	};

	private static final Inventory_Stop_Func StopInventoryNightVision = new Inventory_Stop_Func() {
		@Override
		public void invoke(PlayerStr pp, int num) {
			StopInventoryNightVision(pp, num);
		}
	};

	public static final INVENTORY_DATA InventoryData[] = {
			new INVENTORY_DATA("PORTABLE MEDKIT", Player_Action_Func.UseInventoryMedkit, null, ps_PanelMedkit, 0, 1, (1 << 16), 0),
			new INVENTORY_DATA("REPAIR KIT", null, null, ps_PanelRepairKit, 100, 1, (1 << 16), INVF_AUTO_USE),
			new INVENTORY_DATA("SMOKE BOMB", Player_Action_Func.UseInventoryCloak, StopInventoryCloak, ps_PanelCloak, 4, 1, (1 << 16),
					INVF_TIMED),
			new INVENTORY_DATA("NIGHT VISION", Player_Action_Func.UseInventoryNightVision, StopInventoryNightVision, ps_PanelNightVision,
					3, 1, (1 << 16), INVF_TIMED),
			new INVENTORY_DATA("GAS BOMB", Player_Action_Func.UseInventoryChemBomb, null, ps_PanelChemBomb, 0, 1, (1 << 16), INVF_COUNT),
			new INVENTORY_DATA("FLASH BOMB", Player_Action_Func.UseInventoryFlashBomb, null, ps_PanelFlashBomb, 0, 2, (1 << 16),
					INVF_COUNT),
			new INVENTORY_DATA("CALTROPS", Player_Action_Func.UseInventoryCaltrops, null, ps_PanelCaltrops, 0, 3, (1 << 16),
					INVF_COUNT), };

	public static void PanelInvTestSuicide(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_SUICIDE)) {
			pKillSprite(psp);
		}
	}

	public static Panel_Sprite SpawnInventoryIcon(PlayerStr pp, int InventoryNum) {
		Panel_Sprite psp;
		int x, y;

		// check to see if its already spawned
		if (pp.InventorySprite[InventoryNum] != null)
			return (null);

		// check for Icon panel state
		if (InventoryData[InventoryNum].State == null)
			return (null);

		x = InventoryBarXpos[gs.BorderNum] + (InventoryNum * INVENTORY_ICON_WIDTH);
		y = InventoryBarYpos[gs.BorderNum];
		psp = pSpawnSprite(pp, InventoryData[InventoryNum].State, PRI_FRONT, x, y);
		pp.InventorySprite[InventoryNum] = psp;

		psp.x1 = 0;
		psp.y1 = 0;
		psp.x2 = (short) (xdim - 1);
		psp.y2 = (short) (ydim - 1);
		psp.scale = InventoryData[InventoryNum].Scale;
		psp.flags |= (PANF_STATUS_AREA | PANF_SCREEN_CLIP);

		return (psp);
	}

	private static void KillPlayerIcon(PlayerStr pp, Panel_Sprite pspp) {
		if(pspp != null)
			pspp.flags |= (PANF_SUICIDE);
	}

	private static void KillAllPanelInv(PlayerStr pp) {
		pp.InventoryBarTics = 0;
		for (short i = 0; i < MAX_INVENTORY; i++) {
			if (pp.InventorySprite[i] == null)
				continue;

			pp.InventoryTics[i] = 0;
			pp.InventorySprite[i].flags |= (PANF_SUICIDE);
			pp.InventorySprite[i].numpages = 0;
			pp.InventorySprite[i] = null;
		}
	}

	public static Panel_Sprite SpawnIcon(PlayerStr pp, Panel_State state) {
		Panel_Sprite psp = pSpawnSprite(pp, state, PRI_FRONT, 0, 0);

		psp.x1 = 0;
		psp.y1 = 0;
		psp.x2 = (short) (xdim - 1);
		psp.y2 = (short) (ydim - 1);
		psp.flags |= (PANF_STATUS_AREA | PANF_SCREEN_CLIP);
		return (psp);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// MEDKIT
	//
	//////////////////////////////////////////////////////////////////////

	private static void AutoPickInventory(PlayerStr pp) {
		int i;

		// auto pick only if run out of currently selected one

		if (pp.InventoryAmount[pp.InventoryNum] <= 0) {
			for (i = 0; i < MAX_INVENTORY; i++) {
				if (i == INVENTORY_REPAIR_KIT)
					continue;

				if (pp.InventoryAmount[i] != 0) {
					pp.InventoryNum = (short) i;
					return;
				}
			}

			// only take this if there is nothing else
			if (pp.InventoryAmount[INVENTORY_REPAIR_KIT] != 0)
				pp.InventoryNum = INVENTORY_REPAIR_KIT;
		}
	}

	public static void UseInventoryMedkit(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite];
		short diff;
		short inv = INVENTORY_MEDKIT;
		short amt;

		if (pp.InventoryAmount[inv] == 0)
			return;

		diff = (short) (100 - u.Health);
		if (diff <= 0)
			return;

		if (diff > pp.InventoryPercent[inv]) // If not enough to get to 100, use what's left
			amt = pp.InventoryPercent[inv];
		else
			amt = diff;

		PlayerUpdateHealth(pp, amt);

		pp.InventoryPercent[inv] -= diff;
		if (pp.InventoryPercent[inv] < 0) {
			pp.InventoryPercent[inv] = 0;
			pp.InventoryAmount[inv]--;
		}

		AutoPickInventory(pp);

		// percent
		PlayerUpdateInventory(pp, pp.InventoryNum);

		if (pp == Player[myconnectindex]) {
			if (amt >= 30)
				PlayerSound(DIGI_GETMEDKIT, v3df_follow | v3df_dontpan, pp);
			else
				PlayerSound(DIGI_AHH, v3df_follow | v3df_dontpan, pp);
		}
	}

	//////////////////////////////////////////////////////////////////////
	//
	// CHEMICAL WARFARE CANISTERS
	//
	//////////////////////////////////////////////////////////////////////

	public static void UseInventoryChemBomb(PlayerStr pp) {
		short inv = INVENTORY_CHEMBOMB;

		if (pp.InventoryAmount[inv] == 0)
			return;

		PlayerInitChemBomb(pp); // Throw a chemical bomb out there

		pp.InventoryPercent[inv] = 0;
		if (--pp.InventoryAmount[inv] < 0)
			pp.InventoryAmount[inv] = 0;

		AutoPickInventory(pp);

		PlayerUpdateInventory(pp, pp.InventoryNum);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// FLASH BOMBS
	//
	//////////////////////////////////////////////////////////////////////

	public static void UseInventoryFlashBomb(PlayerStr pp) {
		short inv = INVENTORY_FLASHBOMB;

		if (pp.InventoryAmount[inv] == 0)
			return;

		PlayerInitFlashBomb(pp);

		pp.InventoryPercent[inv] = 0;
		if (--pp.InventoryAmount[inv] < 0)
			pp.InventoryAmount[inv] = 0;

		AutoPickInventory(pp);

		PlayerUpdateInventory(pp, pp.InventoryNum);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// CALTROPS
	//
	//////////////////////////////////////////////////////////////////////

	public static void UseInventoryCaltrops(PlayerStr pp) {
		short inv = INVENTORY_CALTROPS;

		if (pp.InventoryAmount[inv] == 0)
			return;

		PlayerInitCaltrops(pp);

		pp.InventoryPercent[inv] = 0;
		if (--pp.InventoryAmount[inv] < 0)
			pp.InventoryAmount[inv] = 0;

		AutoPickInventory(pp);

		PlayerUpdateInventory(pp, pp.InventoryNum);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// REPAIR KIT
	//
	//////////////////////////////////////////////////////////////////////

	public static void UseInventoryRepairKit(PlayerStr pp) {
		short inv = INVENTORY_REPAIR_KIT;

		if (pp == Player[myconnectindex]) {
			if (STD_RANDOM_RANGE(1000) > 500)
				PlayerSound(DIGI_NOREPAIRMAN, v3df_follow | v3df_dontpan, pp);
			else
				PlayerSound(DIGI_NOREPAIRMAN2, v3df_follow | v3df_dontpan, pp);
		}

		pp.InventoryPercent[inv] = 0;
		pp.InventoryAmount[inv] = 0;

		AutoPickInventory(pp);

		// percent
		PlayerUpdateInventory(pp, pp.InventoryNum);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// CLOAK
	//
	//////////////////////////////////////////////////////////////////////

	public static void UseInventoryCloak(PlayerStr pp) {
		SPRITE sp = pp.getSprite();

		if (pp.InventoryActive[pp.InventoryNum]) {
			return;
		}

		pp.InventoryActive[pp.InventoryNum] = true;

		AutoPickInventory(pp);

		// on/off
		PlayerUpdateInventory(pp, pp.InventoryNum);

		sp.cstat |= (CSTAT_SPRITE_TRANSLUCENT);
		sp.shade = 100;

		PlaySound(DIGI_GASPOP, pp, v3df_none);
		if (pp == Player[myconnectindex])
			PlayerSound(DIGI_IAMSHADOW, v3df_follow | v3df_dontpan, pp);
	}

	private static void StopInventoryCloak(PlayerStr pp, int InventoryNum) {
		SPRITE sp = pp.getSprite();

		pp.InventoryActive[InventoryNum] = false;

		if (pp.InventoryPercent[InventoryNum] <= 0) {
			pp.InventoryPercent[InventoryNum] = 0;
			if (--pp.InventoryAmount[InventoryNum] < 0)
				pp.InventoryAmount[InventoryNum] = 0;
		}

		// on/off
		PlayerUpdateInventory(pp, InventoryNum);

		sp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
		sp.shade = 0;

		PlaySound(DIGI_GASPOP, pp, v3df_none);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// NIGHT VISION
	//
	//////////////////////////////////////////////////////////////////////

	public static void UseInventoryNightVision(PlayerStr pp) {
		if (pp.InventoryActive[pp.InventoryNum]) {
			StopInventoryNightVision(pp, pp.InventoryNum);
			return;
		}

		pp.InventoryActive[pp.InventoryNum] = true;

		// on/off
		PlayerUpdateInventory(pp, pp.InventoryNum);

		DoPlayerNightVisionPalette(pp);
		PlaySound(DIGI_NIGHTON, pp, v3df_dontpan | v3df_follow);
	}

	private static void StopInventoryNightVision(PlayerStr pp, int InventoryNum) {
		pp.InventoryActive[InventoryNum] = false;

		if (pp.InventoryPercent[InventoryNum] <= 0) {
			pp.InventoryPercent[InventoryNum] = 0;
			if (--pp.InventoryAmount[InventoryNum] < 0)
				pp.InventoryAmount[InventoryNum] = 0;
		}

		AutoPickInventory(pp);

		// on/off
		PlayerUpdateInventory(pp, pp.InventoryNum);

		DoPlayerNightVisionPalette(pp);
		DoPlayerDivePalette(pp);
		PlaySound(DIGI_NIGHTOFF, pp, v3df_dontpan | v3df_follow);
	}

	//////////////////////////////////////////////////////////////////////
	//
	// INVENTORY KEYS
	//
	//////////////////////////////////////////////////////////////////////

	public static void InventoryKeys(PlayerStr pp) {
		short inv_hotkey;

		// scroll SPELLs left
		if (TEST_SYNC_KEY(pp, SK_INV_LEFT)) {
			if (FLAG_KEY_PRESSED(pp, SK_INV_LEFT)) {
				FLAG_KEY_RELEASE(pp, SK_INV_LEFT);
				SpawnInventoryBar(pp);
				PlayerUpdateInventory(pp, pp.InventoryNum - 1);
				PutStringInfo(pp, InventoryData[pp.InventoryNum].Name);
				InventoryBarUpdatePosition(pp);
				InvBorderRefresh(pp);
			}
		} else {
			FLAG_KEY_RESET(pp, SK_INV_LEFT);
		}

		// scroll SPELLs right
		if (TEST_SYNC_KEY(pp, SK_INV_RIGHT)) {
			if (FLAG_KEY_PRESSED(pp, SK_INV_RIGHT)) {
				FLAG_KEY_RELEASE(pp, SK_INV_RIGHT);
				SpawnInventoryBar(pp);
				PlayerUpdateInventory(pp, pp.InventoryNum + 1);
				PutStringInfo(pp, InventoryData[pp.InventoryNum].Name);
				InventoryBarUpdatePosition(pp);
				InvBorderRefresh(pp);
			}
		} else {
			FLAG_KEY_RESET(pp, SK_INV_RIGHT);
		}

		if (TEST_SYNC_KEY(pp, SK_INV_USE)) {
			if (FLAG_KEY_PRESSED(pp, SK_INV_USE)) {
				FLAG_KEY_RELEASE(pp, SK_INV_USE);

				if (InventoryData[pp.InventoryNum].init != null) {
					if (pp.InventoryAmount[pp.InventoryNum] != 0) {
						InventoryUse(pp);
					} else {
						PutStringInfo(pp, "No " + InventoryData[pp.InventoryNum].Name); // DONT have message
					}
				}
			}
		} else {
			FLAG_KEY_RESET(pp, SK_INV_USE);
		}

		// get hotkey number out of input bits
		inv_hotkey = (short) (DTEST(pp.input.bits, SK_INV_HOTKEY_MASK) >> SK_INV_HOTKEY_BIT0);

		if (inv_hotkey != 0) {
			if (FLAG_KEY_PRESSED(pp, SK_INV_HOTKEY_BIT0)) {
				FLAG_KEY_RELEASE(pp, SK_INV_HOTKEY_BIT0);

				inv_hotkey -= 1;

				pp.InventoryNum = inv_hotkey;

				if (InventoryData[pp.InventoryNum].init != null && !TEST(pp.Flags, PF_CLIMBING)) {
					if (pp.InventoryAmount[pp.InventoryNum] != 0)
						InventoryUse(pp);
				}

			}
		} else {
			FLAG_KEY_RESET(pp, SK_INV_HOTKEY_BIT0);
		}
	}

	private static void InvBorderRefresh(PlayerStr pp) {
		if (pp != Player[myconnectindex])
			return;

		SetRedrawScreen(pp);
	}

	public static void InventoryTimer(PlayerStr pp) {
		// called every time through loop
		short inv = 0;
		INVENTORY_DATA id = InventoryData[0];

		// if bar is up
		if (pp.InventoryBarTics != 0) {
			InventoryBarUpdatePosition(pp);

			pp.InventoryBarTics -= synctics;
			// if bar time has elapsed
			if (pp.InventoryBarTics <= 0) {
				// get rid of the bar
				KillInventoryBar(pp);
				// don't update bar anymore
				pp.InventoryBarTics = 0;

				InvBorderRefresh(pp);
				// BorderRefresh(pp);
			}
		}

		for (int i = 0; i < InventoryData.length; i++, inv++) {
			id = InventoryData[i];
			// if timed and active
			if (TEST(id.Flags, INVF_TIMED) && pp.InventoryActive[inv]) {
				// dec tics
				pp.InventoryTics[inv] -= synctics;
				if (pp.InventoryTics[inv] <= 0) {
					// take off a percentage
					pp.InventoryPercent[inv] -= id.DecPerSec;
					if (pp.InventoryPercent[inv] <= 0) {
						// ALL USED UP
						pp.InventoryPercent[inv] = 0;
						InventoryStop(pp, inv);
						pp.InventoryActive[inv] = false;
					} else {
						// reset 1 sec tic clock
						pp.InventoryTics[inv] = (short) SEC(1);
					}

					// PlayerUpdateInventoryPercent(pp);
					PlayerUpdateInventory(pp, pp.InventoryNum);
				}
			} else
			// the idea behind this is that the USE function will get called
			// every time the player is in an AUTO_USE situation.
			// This code will decrement the timer and set the Item to InActive
			// EVERY SINGLE TIME. Relies on the USE function getting called!
			if (TEST(id.Flags, INVF_AUTO_USE) && pp.InventoryActive[inv]) {
				pp.InventoryTics[inv] -= synctics;
				if (pp.InventoryTics[inv] <= 0) {
					// take off a percentage
					pp.InventoryPercent[inv] -= id.DecPerSec;
					if (pp.InventoryPercent[inv] <= 0) {
						// ALL USED UP
						pp.InventoryPercent[inv] = 0;
						// should get rid if Amount - stop it for good
						InventoryStop(pp, inv);
					} else {
						// reset 1 sec tic clock
						pp.InventoryTics[inv] = (short) SEC(1);
						// set to InActive EVERY TIME THROUGH THE LOOP!
						pp.InventoryActive[inv] = false;
					}

					// PlayerUpdateInventoryPercent(pp);
					PlayerUpdateInventory(pp, pp.InventoryNum);
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////
	//
	// INVENTORY BAR
	//
	//////////////////////////////////////////////////////////////////////

	private static void SpawnInventoryBar(PlayerStr pp) {
		short inv = 0;
		INVENTORY_DATA id = InventoryData[0];
		Panel_Sprite psp;

		// its already up
		if (pp.InventoryBarTics != 0) {
			pp.InventoryBarTics = (short) SEC(2);
			return;
		}

		pp.InventorySelectionBox = SpawnIcon(pp, ps_PanelSelectionBox[0]);

		for (int i = 0; i < InventoryData.length && id.Name != null; i++, inv++) {
			id = InventoryData[i];
			psp = SpawnInventoryIcon(pp, inv);

			if (pp.InventoryAmount[inv] == 0) {
				psp.shade = 100; // Darken it
			}
		}

		pp.InventoryBarTics = (short) SEC(2);
	}

	public static void KillInventoryBar(PlayerStr pp) {
		KillAllPanelInv(pp);
		KillPlayerIcon(pp, pp.InventorySelectionBox);
		pp.InventorySelectionBox = null;
	}

	// In case the BorderNum changes - move the postions
	private static void InventoryBarUpdatePosition(PlayerStr pp) {
		short inv = 0;
		short x, y;
		INVENTORY_DATA id = InventoryData[0];

		x = (short) (InventoryBarXpos[gs.BorderNum] + (pp.InventoryNum * INVENTORY_ICON_WIDTH));
		y = InventoryBarYpos[gs.BorderNum];

		pp.InventorySelectionBox.x = x - 5;
		pp.InventorySelectionBox.y = y - 5;

		for (int i = 0; i < InventoryData.length && id.Name != null; i++, inv++) {
			id = InventoryData[i];
			x = (short) (InventoryBarXpos[gs.BorderNum] + (inv * INVENTORY_ICON_WIDTH));
			y = InventoryBarYpos[gs.BorderNum];

			pp.InventorySprite[inv].x = x;
			pp.InventorySprite[inv].y = y;
		}
	}

	private static void InventoryUse(PlayerStr pp) {
		INVENTORY_DATA id = InventoryData[pp.InventoryNum];

		if (id.init != null)
			(id.init).invoke(pp);
	}

	private static void InventoryStop(PlayerStr pp, int InventoryNum) {
		INVENTORY_DATA id = InventoryData[InventoryNum];

		if (id.stop != null)
			(id.stop).invoke(pp, InventoryNum);
	}

	/////////////////////////////////////////////////////////////////
	//
	// Inventory Console Area
	//
	/////////////////////////////////////////////////////////////////

	public static final int INVENTORY_BOX_X = 231;
	public static final int INVENTORY_BOX_Y = (176 - 8);

	private static short InventoryBoxX;
	private static short InventoryBoxY;

	private static final int INVENTORY_PIC_XOFF = 1;
	private static final int INVENTORY_PIC_YOFF = 1;

	private static final int INVENTORY_PERCENT_XOFF = 19;
	private static final int INVENTORY_PERCENT_YOFF = 13;

	private static final int INVENTORY_STATE_XOFF = 19;
	private static final int INVENTORY_STATE_YOFF = 1;
	
	public static void DrawInventory(PlayerStr pp, int x, int y, int flags)
	{
		// Check for items that need to go translucent from use
		if (pp.InventoryBarTics != 0) {
			short inv = 0;
			INVENTORY_DATA id = InventoryData[0];
			Panel_Sprite psp;

			// Go translucent if used
			for (int i = 0; i < InventoryData.length && id.Name != null; i++, inv++) {
				id = InventoryData[i];
				psp = pp.InventorySprite[inv];
				if (pp.InventoryAmount[inv] == 0) {
					psp.shade = 100; // Darken it
				} else {
					psp.shade = 0;
				}
			}
		}
		
		InventoryBoxX = (short) x;
		InventoryBoxY = (short) y;

		// put pic
		if (gs.BorderNum == BORDER_BAR) {
			if (pp.InventoryAmount[pp.InventoryNum] != 0)
				PlayerUpdateInventoryPic(pp, flags);
		}

		if (pp.InventoryAmount[pp.InventoryNum] != 0) {
			// Auto/On/Off
			PlayerUpdateInventoryState(pp, flags);
			// Percent count/Item count
			PlayerUpdateInventoryPercent(pp, flags);
		}
	}

	public static void PlayerUpdateInventory(PlayerStr pp, int InventoryNum) {
		pp.InventoryNum = (short) InventoryNum;

		if (pp.InventoryNum < 0)
			pp.InventoryNum = MAX_INVENTORY - 1;

		if (pp.InventoryNum >= MAX_INVENTORY)
			pp.InventoryNum = 0;
	}

	private static final char[] buffer = new char[32];

	private static void PlayerUpdateInventoryPercent(PlayerStr pp, int flags) {
		int x, y;
		INVENTORY_DATA id = InventoryData[pp.InventoryNum];

		x = InventoryBoxX + INVENTORY_PERCENT_XOFF;
		y = InventoryBoxY + INVENTORY_PERCENT_YOFF;

		if (TEST(id.Flags, INVF_COUNT)) {
			Bitoa(pp.InventoryAmount[pp.InventoryNum], buffer);
			InventoryDisplayString(pp, x, y, 0, buffer, flags);
		} else {
			int offs = Bitoa(pp.InventoryPercent[pp.InventoryNum], buffer);
			buildString(buffer, offs, "%");
			InventoryDisplayString(pp, x, y, 0, buffer, flags);
		}
	}

	private static void PlayerUpdateInventoryPic(PlayerStr pp, int flags) {
		INVENTORY_DATA id = InventoryData[pp.InventoryNum];

		int x = InventoryBoxX + INVENTORY_PIC_XOFF;
		int y = InventoryBoxY + INVENTORY_PIC_YOFF;

		engine.rotatesprite(x << 16, y << 16, id.Scale, 0, id.State.picndx, 0, 0, ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | flags, 0, 0, xdim - 1, ydim - 1);
	}

	private static void PlayerUpdateInventoryState(PlayerStr pp, int flags) {
		int x, y;
		INVENTORY_DATA id = InventoryData[pp.InventoryNum];

		x = InventoryBoxX + INVENTORY_STATE_XOFF;
		y = InventoryBoxY + INVENTORY_STATE_YOFF;

		if (TEST(id.Flags, INVF_AUTO_USE)) {
			buildString(buffer, 0, "AUTO");
			InventoryDisplayString(pp, x, y, 0, buffer, flags);
		} else if (TEST(id.Flags, INVF_TIMED)) {
			buildString(buffer, 0, pp.InventoryActive[pp.InventoryNum] ? "ON" : "OFF");
			InventoryDisplayString(pp, x, y, 0, buffer, flags);
		}
	}

	private static void InventoryDisplayString(PlayerStr pp, int x, int y, int pal, char[] buf, int flags) {
		if (gs.BorderNum != BORDER_BAR) {
			DisplayMiniBarSmString(pp, x, y, pal, buffer, flags);
		} else {
			DisplaySmString(pp, x, y, pal, buffer);
		}
	}

}
