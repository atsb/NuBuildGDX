package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.automapping;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Game.ExitLevel;
import static ru.m210projects.Wang.Game.FinishedLevel;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.MapSetAll2D;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.SW_SHAREWARE;
import static ru.m210projects.Wang.Gameutils.PF_CLIP_CHEAT;
import static ru.m210projects.Wang.Gameutils.PF_PICKED_UP_AN_UZI;
import static ru.m210projects.Wang.Gameutils.PF_TWO_UZI;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Inv.InventoryData;
import static ru.m210projects.Wang.Inv.MAX_INVENTORY;
import static ru.m210projects.Wang.Inv.PlayerUpdateInventory;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Stag.SECT_LOCK_DOOR;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.FALSE;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.MyTypes.TRUE;
import static ru.m210projects.Wang.Weapon.DamageData;
import static ru.m210projects.Wang.Weapons.Uzi.InitWeaponUzi;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class Cheats {
	
	public static boolean InfinityAmmo = false;

	public static int LocationInfo = 0;
	private static int EveryCheat = 0;

	public enum Cheat {
		SWINFINITYAMMO {
			@Override
			public boolean invoke(int... opt) {
				InfinityAmmo = !InfinityAmmo;
				PutStringInfo(Player[myconnectindex], "Infinity ammo mode " + (InfinityAmmo ? "ON" : "OFF"));
				return true;
			}
		},
		SWGOD {
			@Override
			public boolean invoke(int... opt) {
				GodCheat(Player[myconnectindex]);
				return true;
			}
		},
		SWCHAN {
			@Override
			public boolean invoke(int... opt) {
				GodCheat(Player[myconnectindex]);
				return true;
			}
		},
		SWGIMME {
			@Override
			public boolean invoke(int... opt) {
				ItemCheat(Player[myconnectindex]);
				return true;
			}
		},
		SWMEDIC {
			@Override
			public boolean invoke(int... opt) {
				HealCheat(Player[myconnectindex]);
				return true;
			}
		},
		SWKEYS {
			@Override
			public boolean invoke(int... opt) {
				KeysCheat(Player[myconnectindex], -1, null);
				return true;
			}
		},
		SWREDCARD {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWBLUECARD {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWGREENCARD {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWYELLOWCARD {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWGOLDKEY {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWSILVERKEY {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWBRONZEKEY {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWREDKEY {
			@Override
			public boolean invoke(int... opt) {
				return SortKeyCheat(Player[myconnectindex], this);
			}
		},
		SWGUN {
			@Override
			public boolean invoke(int... opt) {
				if (opt.length < 1)
					return false;

				GunsCheat(Player[myconnectindex], opt[0]);
				return true;
			}
		},
		SWTREK {
			@Override
			public boolean invoke(int... opt) {
				boolean bWarp = false;
				switch (opt.length) {
				case 0:
					Console.Println("swtrek <level> or <episode> <level>");
					if (!Console.IsShown())
						Console.toggle();
					return true;
				case 1:
					bWarp = WarpCheat(Player[myconnectindex], opt[0]);
					break;
				case 2:
					bWarp = WarpCheat(Player[myconnectindex], ((opt[0] - 1) * 4) + opt[1]);
					break;
				}

				if (!bWarp)
					return false;

				if (Console.IsShown())
					Console.toggle();
				return true;
			}
		},
		SWGREED {
			@Override
			public boolean invoke(int... opt) {
				EveryCheatToggle(Player[myconnectindex]);
				return true;
			}
		},
		SWGHOST {
			@Override
			public boolean invoke(int... opt) {
				ClipCheat(Player[myconnectindex]);
				return true;
			}
		},
		SWSTART {
			@Override
			public boolean invoke(int... opt) {
				ExitLevel = true;
				FinishedLevel = true;
				return true;
			}
		},
		SWLOC {
			@Override
			public boolean invoke(int... opt) {
				LocCheat(Player[myconnectindex]);
				return true;
			}
		},
		SWMAP {
			@Override
			public boolean invoke(int... opt) {
				MapCheat(Player[myconnectindex]);
				return true;
			}
		};

		public abstract boolean invoke(int... opt);
	}

	public static Cheat IsCheatCode(String message) {
		Cheat[] arr = Cheat.values();
		if (message.toUpperCase().startsWith(Cheat.SWTREK.name()))
			return Cheat.SWTREK;

		for (int nCheatCode = 0; nCheatCode < arr.length; nCheatCode++) {
			Cheat pCheat = arr[nCheatCode];
			if (message.equalsIgnoreCase(pCheat.name())) {
				return pCheat;
			}
		}
		return null;
	}

	private static void HealCheat(PlayerStr pp) {
		String str = "";
		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			if (pUser[Player[pnum].PlayerSprite].Health < pp.MaxHealth)
				str = "ADDED HEALTH";
			pUser[Player[pnum].PlayerSprite].Health += 25;
		}
		PutStringInfo(pp, str);
	}

	private static void ItemCheat(PlayerStr pp) {
		//
		// Get all ITEMS
		//
		PlayerStr p;
		short pnum;
		short inv;
		int i;

		PutStringInfo(pp, "ITEMS");

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			p = Player[pnum];
			Arrays.fill(p.HasKey, (byte) 1);

			{
				p.Flags |= (PF_TWO_UZI);
				p.Flags |= (PF_PICKED_UP_AN_UZI);
				InitWeaponUzi(p);
			}

			p.WpnShotgunAuto = 50;
			p.WpnRocketHeat = 5;
			p.WpnRocketNuke = 1;
			p.Armor = 100;

			for (inv = 0; inv < MAX_INVENTORY; inv++) {
				p.InventoryPercent[inv] = 100;
				p.InventoryAmount[inv] = InventoryData[inv].MaxInv;
			}

			PlayerUpdateInventory(p, p.InventoryNum);
		}

		for (i = 0; i < numsectors; i++) {
			if (SectUser[i] != null && SectUser[i].stag == SECT_LOCK_DOOR)
				SectUser[i].number = 0; // unlock all doors of this type
		}

		WeaponCheat(pp);
	}

	private static void WeaponCheat(PlayerStr pp) {
		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr p = Player[pnum];
			USER u = pUser[p.PlayerSprite];

			// ALL WEAPONS
			if (!SW_SHAREWARE)
				p.WpnFlags = 0xFFFFFFFF;
			else
				p.WpnFlags = 0x0000207F; // Disallows high weapon cheat in shareware

			for (int i = 0; i < p.WpnAmmo.length; i++) {
				p.WpnAmmo[i] = DamageData[i].max_ammo;
			}

			PlayerUpdateWeapon(p, u.WeaponNum);
		}
	}

	private static void GodCheat(PlayerStr pp) {
		GodMode = !GodMode;
		PutStringInfo(pp, "GOD MODE " + (GodMode ? "ON" : "OFF"));
	}

	private static short gAmmo[] = { 0, 9, 12, 20, 3, 6, 5, 5, 10, 1 };

	private static void GunsCheat(PlayerStr pp, int gunnum) {
		String str = "GIVEN WEAPON ";
		if (gunnum < 2 || gunnum > 10)
			return;

		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr p = Player[pnum];
			USER u = pUser[p.PlayerSprite];
			int x = gAmmo[gunnum - 1];
			if (!TEST(p.WpnFlags, BIT(gunnum - 1)))
				p.WpnFlags += BIT(gunnum - 2) << 1;
			else
				str = "ADD AMMO TO WEAPON ";
			p.WpnAmmo[gunnum - 1] += x;
			if (p.WpnAmmo[gunnum - 1] > DamageData[gunnum - 1].max_ammo) {
				p.WpnAmmo[gunnum - 1] = DamageData[gunnum - 1].max_ammo;
				str = "";
			}
			PlayerUpdateWeapon(p, u.WeaponNum);
		}
		PutStringInfo(pp, str + gunnum);
	}

	private static boolean WarpCheat(PlayerStr pp, int level_num) {
		if (!SW_SHAREWARE) {
			if (level_num > 22 || level_num < 1)
				return false;
		} else {
			if (level_num > 4 || level_num < 1)
				return false;
		}

		Level = level_num;
		ExitLevel = true;
		PutStringInfo(pp, "ENTERING " + Level);

		return true;
	}

	private static void KeysCheat(PlayerStr pp, int keynum, String CheatKeyType) {
		String str = null;
		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr p = Player[pnum];
			if (keynum < 1 || keynum > 8) {
				Arrays.fill(p.HasKey, (byte) 1);
				PutStringInfo(pp, "Given all keys");
				return;
			} else {
				if (p.HasKey[keynum - 1] == FALSE) {
					p.HasKey[keynum - 1] = TRUE; // cards: 0=red 1=blue 2=green 3=yellow | keys: 4=gold 5=silver
													// 6=bronze 7=red
					str = "Given ";
				} else {
					p.HasKey[keynum - 1] = FALSE;
					str = "Removed ";
				}
			}
		}

		PutStringInfo(pp, str + CheatKeyType);
	}

	private static boolean SortKeyCheat(PlayerStr pp, Cheat sKey) {
		String CheatKeyType = null;
		int nKey = -1;

		if (sKey == Cheat.SWREDCARD) {
			nKey = 1;
			CheatKeyType = "Red Cardkey";
		} else if (sKey == Cheat.SWBLUECARD) {
			nKey = 2;
			CheatKeyType = "Blue Cardkey";
		} else if (sKey == Cheat.SWGREENCARD) {
			nKey = 3;
			CheatKeyType = "Green Cardkey";
		} else if (sKey == Cheat.SWYELLOWCARD) {
			nKey = 4;
			CheatKeyType = "Yellow Cardkey";
		} else if (sKey == Cheat.SWGOLDKEY) {
			nKey = 5;
			CheatKeyType = "Gold Key";
		} else if (sKey == Cheat.SWSILVERKEY) {
			nKey = 6;
			CheatKeyType = "Silver Key";
		} else if (sKey == Cheat.SWBRONZEKEY) {
			nKey = 7;
			CheatKeyType = "Bronze Key";
		} else if (sKey == Cheat.SWREDKEY) {
			nKey = 8;
			CheatKeyType = "Red Key";
		}

		if (nKey != -1) {
			KeysCheat(pp, nKey, CheatKeyType);
			return true;
		}

		return false;
	}

	private static void EveryCheatToggle(PlayerStr pp) {
		EveryCheat ^= 1;

		WeaponCheat(pp);
		GodCheat(pp);
		ItemCheat(pp);

		PutStringInfo(pp, "EVERY CHEAT " + (EveryCheat != 0 ? "ON" : "OFF"));
	}

	private static void ClipCheat(PlayerStr pp) {
		pp.Flags ^= (PF_CLIP_CHEAT);
		PutStringInfo(pp, "NO CLIP MODE " + (TEST(pp.Flags, PF_CLIP_CHEAT) ? "ON" : "OFF"));
	}

	private static void MapCheat(PlayerStr pp) {
		automapping ^= 1;

		if (automapping == 0)
			MapSetAll2D((byte) 0);
		else
			MapSetAll2D((byte) 0xFF);

		PutStringInfo(pp, "AUTOMAPPING " + (automapping != 0 ? "ON" : "OFF"));
	}

	private static void LocCheat(PlayerStr pp) {
		LocationInfo++;
		if (LocationInfo > 2)
			LocationInfo = 0;
	}

	public static boolean handleCheat(String message) {
		Integer o1 = null, o2 = null;
		Cheat pCheat = IsCheatCode(message);
		if (pCheat == null || pCheat == Cheat.SWTREK) {
			// check warp cheat

			int i = nextNumber(message, 0);
			pCheat = IsCheatCode(message.substring(0, i));
			if (pCheat != null) {
				message = message.replaceAll("[\\s]{2,}", " "); // remove multiply spaces

				int startpos = ++i;
				if ((i = nextNumber(message, startpos)) != -1) {
					String n1 = message.substring(startpos, i).replaceAll("[^0-9]", "");
					String n2 = null;
					if ((i = nextNumber(message, startpos = ++i)) != -1)
						n2 = message.substring(startpos, i).replaceAll("[^0-9]", "");
					if (n1 != null && n1.isEmpty())
						n1 = null;
					if (n2 != null && n2.isEmpty())
						n2 = null;

					try {
						if (n2 == null && n1 != null) { // n1 is level
							o1 = Integer.parseInt(n1);
						} else if (n2 != null && n1 != null) { // n1 is episode, n2 is level
							o1 = Integer.parseInt(n1);
							o2 = Integer.parseInt(n2);
						}
					} catch (Exception e) {
					}
				}
			}
		}

		if (pCheat != null) {
			if (!game.isCurrentScreen(gGameScreen)) {
				Console.Println(message + ": not in a game");
				return true;
			}

			if (o1 == null && o2 == null)
				return pCheat.invoke();
			else {
				if (o2 == null)
					return pCheat.invoke(o1);
				return pCheat.invoke(o1, o2);
			}
		}

		return false;
	}

	private static int nextNumber(String text, int pos) {
		while (pos < text.length() && text.charAt(pos) != 0 && text.charAt(pos) != ' ')
			pos++;

		if (pos <= text.length())
			return pos;

		return -1;
	}
}
