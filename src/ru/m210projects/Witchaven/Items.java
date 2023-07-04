package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Potions.potionspace;
import static ru.m210projects.Witchaven.Potions.updatepotion;
import static ru.m210projects.Witchaven.Spellbooks.changebook;
import static ru.m210projects.Witchaven.WHPLR.addarmor;
import static ru.m210projects.Witchaven.WHPLR.addhealth;
import static ru.m210projects.Witchaven.WHPLR.addscore;
import static ru.m210projects.Witchaven.WHPLR.justteleported;
import static ru.m210projects.Witchaven.WHSND.SND_Sound;
import static ru.m210projects.Witchaven.WHSND.S_EXPLODE;
import static ru.m210projects.Witchaven.WHSND.S_FIREBALL;
import static ru.m210projects.Witchaven.WHSND.S_PICKUPAXE;
import static ru.m210projects.Witchaven.WHSND.S_PLRPAIN1;
import static ru.m210projects.Witchaven.WHSND.S_POTION1;
import static ru.m210projects.Witchaven.WHSND.S_STING1;
import static ru.m210projects.Witchaven.WHSND.S_STING2;
import static ru.m210projects.Witchaven.WHSND.S_TREASURE1;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;
import static ru.m210projects.Witchaven.WHScreen.showmessage;
import static ru.m210projects.Witchaven.WHScreen.startredflash;
import static ru.m210projects.Witchaven.Weapons.autoweaponchange;
import static ru.m210projects.Witchaven.Weapons.droptheshield;
import static ru.m210projects.Witchaven.WHOBJ.*;

import ru.m210projects.Witchaven.Types.Item;
import ru.m210projects.Witchaven.Types.Item.Callback;
import ru.m210projects.Witchaven.Types.PLAYER;

public class Items {
	
	public static final int ITEMSBASE = 101;
	public static final int SILVERBAGTYPE = 101;
	public static final int GOLDBAGTYPE = 102;
	public static final int HELMETTYPE = 103;
	public static final int PLATEARMORTYPE = 104;
	public static final int CHAINMAILTYPE = 105;
	public static final int LEATHERARMORTYPE = 106;
	public static final int GIFTBOXTYPE = 107;
	public static final int FLASKBLUETYPE = 108;
	public static final int FLASKGREENTYPE = 109;
	public static final int FLASKOCHRETYPE = 110;
	public static final int FLASKREDTYPE = 111;
	public static final int FLASKTANTYPE = 112;
	public static final int DIAMONDRINGTYPE = 113;
	public static final int SHADOWAMULETTYPE = 114;
	public static final int GLASSSKULLTYPE = 115;
	public static final int AHNKTYPE = 116;
	public static final int BLUESCEPTERTYPE = 117;
	public static final int YELLOWSCEPTERTYPE = 118;
	public static final int ADAMANTINERINGTYPE = 119;
	public static final int ONYXRINGTYPE = 120;
	public static final int PENTAGRAMTYPE = 121;
	public static final int CRYSTALSTAFFTYPE = 122;
	public static final int AMULETOFTHEMISTTYPE = 123;
	public static final int HORNEDSKULLTYPE = 124;
	public static final int THEHORNTYPE = 125;
	public static final int SAPHIRERINGTYPE = 126;
	public static final int BRASSKEYTYPE = 127;
	public static final int BLACKKEYTYPE = 128;
	public static final int GLASSKEYTYPE = 129;
	public static final int IVORYKEYTYPE = 130;
	public static final int SCROLLSCARETYPE = 131;
	public static final int SCROLLNIGHTTYPE = 132;
	public static final int SCROLLFREEZETYPE = 133;
	public static final int SCROLLMAGICTYPE = 134;
	public static final int SCROLLOPENTYPE = 135;
	public static final int SCROLLFLYTYPE = 136;
	public static final int SCROLLFIREBALLTYPE = 137;
	public static final int SCROLLNUKETYPE = 138;
	public static final int QUIVERTYPE = 139;
	public static final int BOWTYPE = 140;
	public static final int WEAPON1TYPE = 141;
	public static final int WEAPON1ATYPE = 142;
	public static final int GOBWEAPONTYPE = 143;
	public static final int WEAPON2TYPE = 144;
	public static final int WEAPON3ATYPE = 145;
	public static final int WEAPON3TYPE = 146;
	public static final int WEAPON4TYPE = 147;
	public static final int THROWHALBERDTYPE = 148;
	public static final int WEAPON5TYPE = 149;
	public static final int GONZOSHIELDTYPE = 150;
	public static final int SHIELDTYPE = 151;
	public static final int WEAPON5BTYPE = 152;
	public static final int WALLPIKETYPE = 153;
	public static final int WEAPON6TYPE = 154; 
	public static final int WEAPON7TYPE = 155;
	public static final int GYSERTYPE = 156; //WH1
	public static final int SPIKEBLADETYPE = 157;
	public static final int SPIKETYPE = 158;
	public static final int SPIKEPOLETYPE = 159;
	public static final int MONSTERBALLTYPE = 160;
	public static final int WEAPON8TYPE = 161;
	public static final int MAXITEMS = 162;
	
	public static boolean isItemSprite(int i) {
		return (sprite[i].detail & 0xFF) >= ITEMSBASE && (sprite[i].detail & 0xFF) < MAXITEMS;
	}
	
	public static Item[] items = { new Item(-1, -1, false, false, new Callback() { // SILVERBAG
		@Override
		public void pickup(PLAYER plr, short i) {
			showmessage("Silver!", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, engine.krand() % 100 + 10);
		}
	}), new Item(-1, -1, false, false, new Callback() { // GOLDBAG
		@Override
		public void pickup(PLAYER plr, short i) {
			showmessage("Gold!", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, engine.krand() % 100 + 10);
		}
	}), new Item(27, 28, true, false, new Callback() { // HELMET
		@Override
		public void pickup(PLAYER plr, short i) {
			showmessage("Hero Time", 360);
			engine.deletesprite(i);
			if (!game.WH2)
				addarmor(plr, 10);
			plr.helmettime = 7200;
			// JSA_DEMO3
			treasuresfound++;
			SND_Sound(S_STING1 + engine.krand() % 2);
			addscore(plr, 10);
		}
	}), new Item(26, 26, true, false, new Callback() { // PLATEARMOR
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.armor <= 149) {
				showmessage("Plate Armor", 360);
				engine.deletesprite(i);
				plr.armortype = 3;
				plr.armor = 0;
				addarmor(plr, 150);
				SND_Sound(S_POTION1);
				addscore(plr, 40);
				treasuresfound++;
			}
		}
	}), new Item(26, 26, true, false, new Callback() { // CHAINMAIL
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.armor <= 99) {
				showmessage("Chain Mail", 360);
				engine.deletesprite(i);
				plr.armortype = 2;
				plr.armor = 0;
				addarmor(plr, 100);
				SND_Sound(S_POTION1);
				addscore(plr, 20);
				treasuresfound++;
			}
		}
	}), new Item(47, 50, false, false, new Callback() { // LEATHERARMOR
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.armor <= 49) {
				showmessage("Leather Armor", 360);
				engine.deletesprite(i);
				plr.armortype = 1;
				plr.armor = 0;
				addarmor(plr, 50);
				SND_Sound(S_POTION1);
				addscore(plr, 10);
				treasuresfound++;
			}
		}
	}), new Item(56, 49, true, false, new Callback() { // GIFTBOX
		@Override
		public void pickup(PLAYER plr, short i) {
			sprite[i].detail = 0;
			treasuresfound++;
			playsound_loc(S_TREASURE1, sprite[i].x, sprite[i].y);
			int j = engine.krand() % 8;
			switch (j) {
			case 0:
				switch (engine.krand() % 5) {
				case 0:
					if (!potionspace(plr, 0))
						break;
					showmessage("Health Potion", 360);
					updatepotion(plr, HEALTHPOTION);
					plr.currentpotion = 0;
					SND_Sound(S_POTION1);
					addscore(plr, 10);
					break;
				case 1:
					if (!potionspace(plr, 1))
						break;
					showmessage("Strength Potion", 360);
					updatepotion(plr, STRENGTHPOTION);
					plr.currentpotion = 1;
					SND_Sound(S_POTION1);
					addscore(plr, 20);
					break;
				case 2:
					if (!potionspace(plr, 2))
						break;
					showmessage("Cure Poison Potion", 360);
					updatepotion(plr, ARMORPOTION);
					plr.currentpotion = 2;
					SND_Sound(S_POTION1);
					addscore(plr, 15);
					break;
				case 3:
					if (!potionspace(plr, 3))
						break;
					showmessage("Resist Fire Potion", 360);
					updatepotion(plr, FIREWALKPOTION);
					plr.currentpotion = 3;
					SND_Sound(S_POTION1);
					addscore(plr, 15);
					break;
				case 4:
					if (!potionspace(plr, 4))
						break;
					showmessage("Invisibility Potion", 360);
					updatepotion(plr, INVISIBLEPOTION);
					plr.currentpotion = 4;
					SND_Sound(S_POTION1);
					addscore(plr, 30);
					break;
				}
				sprite[i].picnum = OPENCHEST;
				break;
			case 1:
				switch (engine.krand() % 8) {
				case 0:
					if (plr.orbammo[0] < 10) {
						plr.orb[0] = 1;
						plr.orbammo[0]++;
						showmessage("Scare Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 1:
					if (plr.orbammo[1] < 10) {
						plr.orb[1] = 1;
						plr.orbammo[1]++;
						showmessage("Night Vision Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 2:
					if (plr.orbammo[2] < 10) {
						plr.orb[2] = 1;
						plr.orbammo[2]++;
						showmessage("Freeze Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 3:
					if (plr.orbammo[3] < 10) {
						plr.orb[3] = 1;
						plr.orbammo[3]++;
						showmessage("Magic Arrow Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 4:
					if (plr.orbammo[4] < 10) {
						plr.orb[4] = 1;
						plr.orbammo[4]++;
						showmessage("Open Door Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 5:
					if (plr.orbammo[5] < 10) {
						plr.orb[5] = 1;
						plr.orbammo[5]++;
						showmessage("Fly Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 6:
					if (plr.orbammo[6] < 10) {
						plr.orb[6] = 1;
						plr.orbammo[6]++;
						showmessage("Fireball Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				case 7:
					if (plr.orbammo[7] < 10) {
						plr.orb[7] = 1;
						plr.orbammo[7]++;
						showmessage("Nuke Scroll", 360);
						SND_Sound(S_POTION1);
					}
					break;
				}
				sprite[i].picnum = OPENCHEST;
				break;
			case 2:
				sprite[i].picnum = OPENCHEST;
				addscore(plr, (engine.krand() % 400) + 100);
				showmessage("Treasure Chest", 360);
				SND_Sound(S_POTION1);
				break;
			case 3:
				// random weapon
				switch ((engine.krand() % 5) + 1) {
				case 1:
					if (plr.ammo[1] < 12) {
						plr.weapon[1] = 1;
						plr.ammo[1] = 40;
						showmessage("Dagger", 360);
						SND_Sound(S_POTION1);
						if (plr.selectedgun < 1)
							autoweaponchange(plr, 1);
						addscore(plr, 10);
					}
					break;
				case 2:
					if (plr.ammo[3] < 12) {
						plr.weapon[3] = 1;
						plr.ammo[3] = 55;
						showmessage("Morning Star", 360);
						SND_Sound(S_POTION1);
						if (plr.selectedgun < 3)
							autoweaponchange(plr, 3);
						addscore(plr, 20);
					}
					break;
				case 3:
					if (plr.ammo[2] < 12) {
						plr.weapon[2] = 1;
						plr.ammo[2] = 30;
						showmessage("Short Sword", 360);
						SND_Sound(S_POTION1);
						if (plr.selectedgun < 2)
							autoweaponchange(plr, 2);
						addscore(plr, 10);
					}
					break;
				case 4:
					if (plr.ammo[5] < 12) {
						plr.weapon[5] = 1;
						plr.ammo[5] = 100;
						showmessage("Battle axe", 360);
						SND_Sound(S_POTION1);
						if (plr.selectedgun < 5)
							autoweaponchange(plr, 5);
						addscore(plr, 30);
					}
					break;
				case 5:
					if (plr.weapon[7] == 1) {
						plr.weapon[7] = 2;
						plr.ammo[7] = 1;
						showmessage("Pike axe", 360);
						engine.deletesprite(i);
						SND_Sound(S_POTION1);
						addscore(plr, 30);
					}
					if (plr.weapon[7] == 2) {
						plr.weapon[7] = 2;
						plr.ammo[7]++;
						showmessage("Pike axe", 360);
						engine.deletesprite(i);
						SND_Sound(S_POTION1);
						addscore(plr, 30);
					}
					if (plr.weapon[7] < 1) {
						if (plr.ammo[7] < 12) {
							plr.weapon[7] = 1;
							plr.ammo[7] = 30;
							showmessage("Pike axe", 360);
							engine.deletesprite(i);
							SND_Sound(S_POTION1);
							if (plr.selectedgun < 7)
								autoweaponchange(plr, 7);
							addscore(plr, 30);
						}
					}
					break;
				}
				sprite[i].picnum = OPENCHEST;
				break;
			case 4:
				// random armor
				switch (engine.krand() & 4) {
				case 0:
					showmessage("Hero Time", 360);
					addarmor(plr, 10);
					plr.helmettime = 7200;
					SND_Sound(S_STING1 + engine.krand() % 2);
					break;
				case 1:
					if (plr.armor <= 149) {
						showmessage("Plate Armor", 360);
						plr.armortype = 3;
						plr.armor = 0;
						addarmor(plr, 150);
						SND_Sound(S_POTION1);
						addscore(plr, 40);
					}
					break;
				case 2:
					if (plr.armor <= 99) {
						showmessage("Chain Mail", 360);
						plr.armortype = 2;
						plr.armor = 0;
						addarmor(plr, 100);
						SND_Sound(S_POTION1);
						addscore(plr, 20);
					}
					break;
				case 3:
					if (plr.armor <= 49) {
						showmessage("Leather Armor", 360);
						plr.armortype = 1;
						plr.armor = 0;
						addarmor(plr, 50);
						SND_Sound(S_POTION1);
						addscore(plr, 20);
					}
					break;
				}
				sprite[i].picnum = OPENCHEST;
				break;
			case 5:
				// poison chest
				if ((engine.krand() & 2) == 0) {
					plr.poisoned = 1;
					plr.poisontime = 7200;
					sprite[i].detail = GIFTBOXTYPE;
					addhealth(plr, -10);
					showmessage("Poisoned Chest", 360);
				} else {
					engine.deletesprite(i);
					addscore(plr, (engine.krand() & 400) + 100);
					showmessage("Treasure Chest", 360);
					SND_Sound(S_POTION1);
				}
				break;
			case 6:
				for (j = 0; j < 8; j++)
					explosion(i, sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].owner);
				playsound_loc(S_EXPLODE, sprite[i].x, sprite[i].y);
				engine.deletesprite(i);
				break;
			default:
				sprite[i].picnum = OPENCHEST;
				addscore(plr, (engine.krand() % 400) + 100);
				showmessage("Experience Gained", 360);
				SND_Sound(S_POTION1);
				break;
			}
		}
	}), new Item(-1, -1, true, false, new Callback() { // FLASKBLUE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (!potionspace(plr, 0))
				return;
			showmessage("Health Potion", 360);
			updatepotion(plr, HEALTHPOTION);
			plr.currentpotion = 0;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 10);
			treasuresfound++;
		}
	}), new Item(-1, -1, true, false, new Callback() { // FLASKGREEN
		@Override
		public void pickup(PLAYER plr, short i) {
			if (!potionspace(plr, 1))
				return;
			showmessage("Strength Potion", 360);
			updatepotion(plr, STRENGTHPOTION);
			plr.currentpotion = 1;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 15);
			treasuresfound++;
		}
	}), new Item(-1, -1, true, false, new Callback() { // FLASKOCHRE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (!potionspace(plr, 2))
				return;
			showmessage("Cure Poison Potion", 360);
			updatepotion(plr, ARMORPOTION);
			plr.currentpotion = 2;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 15);
			treasuresfound++;
		}
	}), new Item(-1, -1, true, false, new Callback() { // FLASKRED
		@Override
		public void pickup(PLAYER plr, short i) {
			if (!potionspace(plr, 3))
				return;
			showmessage("Resist Fire Potion", 360);
			updatepotion(plr, FIREWALKPOTION);
			plr.currentpotion = 3;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 20);
			treasuresfound++;
		}
	}), new Item(-1, -1, true, false, new Callback() { // FLASKTAN
		@Override
		public void pickup(PLAYER plr, short i) {
			if (!potionspace(plr, 4))
				return;
			showmessage("Invisibility Potion", 360);
			updatepotion(plr, INVISIBLEPOTION);
			plr.currentpotion = 4;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 30);
			treasuresfound++;
		}
	}), new Item(14, 14, true, false, new Callback() { // DIAMONDRING
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TDIAMONDRING] = 1;
			showmessage("DIAMOND RING", 360);
			plr.armor = 0;
			addarmor(plr, 200);
			plr.armortype = 3;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 25);
			treasuresfound++;
		}
	}), new Item(30, 23, true, false, new Callback() { // SHADOWAMULET
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TSHADOWAMULET] = 1;
			showmessage("SHADOW AMULET", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			plr.shadowtime = 7500;
			addscore(plr, 50);
			treasuresfound++;
		}
	}), new Item(-1, -1, true, false, new Callback() { // GLASSSKULL
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TGLASSSKULL] = 1;
			showmessage("GLASS SKULL", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			treasuresfound++;
			switch (plr.lvl) {
			case 1:
				plr.score = 2300;
				break;
			case 2:
				plr.score = 4550;
				break;
			case 3:
				plr.score = 9050;
				break;
			case 4:
				plr.score = 18050;
				break;
			case 5:
				plr.score = 36050;
				break;
			case 6:
				plr.score = 75050;
				break;
			case 7:
				plr.score = 180500;
				break;
			case 8:
				plr.score = 280500;
				break;
			}
			addscore(plr, 10);
		}
	}), new Item(51, 54, true, false, new Callback() { // AHNK
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TAHNK] = 1;
			showmessage("ANKH", 360);
			plr.health = 0;
			addhealth(plr, 250);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 100);
			treasuresfound++;
		}
	}), new Item(32, 32, true, false, new Callback() { // BLUESCEPTER
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TBLUESCEPTER] = 1;
			showmessage("Water walk scepter", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 10);
			treasuresfound++;
		}
	}), new Item(32, 32, true, false, new Callback() { // YELLOWSCEPTER
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TYELLOWSCEPTER] = 1;
			showmessage("Fire walk scepter", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 10);
			treasuresfound++;
		}
	}), new Item(14, 14, true, false, new Callback() { // ADAMANTINERING
		@Override
		public void pickup(PLAYER plr, short i) {
			// ring or protection +5
			plr.treasure[TADAMANTINERING] = 1;
			showmessage("ADAMANTINE RING", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 30);
			treasuresfound++;
		}
	}), new Item(42, 28, true, false, new Callback() { // ONYXRING
		@Override
		public void pickup(PLAYER plr, short i) {
			// protection from missile
			// anit-missile for level only
			// dont forget to cleanup values
			plr.treasure[TONYXRING] = 1;
			showmessage("ONYX RING", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 35);
			treasuresfound++;
		}
	}), new Item(-1, -1, true, true, new Callback() { // PENTAGRAM
		@Override
		public void pickup(PLAYER plr, short i) {
			if (sector[plr.sector].lotag == 4002)
				return;
			else {
				plr.treasure[TPENTAGRAM] = 1;
				showmessage("PENTAGRAM", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
			addscore(plr, 100);
		}
	}), new Item(64, 64, true, false, new Callback() { // CRYSTALSTAFF
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TCRYSTALSTAFF] = 1;
			showmessage("CRYSTAL STAFF", 360);
			plr.health = 0;
			addhealth(plr, 250);
			plr.armortype = 2;
			plr.armor = 0;
			addarmor(plr, 300);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			treasuresfound++;
			addscore(plr, 150);
		}
	}), new Item(26, 28, true, false, new Callback() { // AMULETOFTHEMIST
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TAMULETOFTHEMIST] = 1;
			showmessage("AMULET OF THE MIST", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			plr.invisibletime = 3200;
			addscore(plr, 75);
			treasuresfound++;
		}
	}), new Item(64, 64, true, false, new Callback() { // HORNEDSKULL
		@Override
		public void pickup(PLAYER plr, short i) {
			if (game.WH2) {
				final Runnable ending3 = new Runnable() {
					@Override
					public void run() {
						if (gCutsceneScreen.init("ending3.smk"))
							game.changeScreen(gCutsceneScreen.setSkipping(game.main).escSkipping(true));
						else
							game.changeScreen(gMenuScreen);
					}
				};
				Runnable ending2 = new Runnable() {
					@Override
					public void run() {
						if (gCutsceneScreen.init("ending2.smk"))
							game.changeScreen(gCutsceneScreen.setSkipping(ending3).escSkipping(true));
						else
							game.changeScreen(gMenuScreen);
					}
				};
				if (gCutsceneScreen.init("ending1.smk"))
					game.changeScreen(gCutsceneScreen.setSkipping(ending2).escSkipping(true));
				else
					game.changeScreen(gMenuScreen);
				return;
			}
			plr.treasure[THORNEDSKULL] = 1;
			showmessage("HORNED SKULL", 360);
			engine.deletesprite(i);
			SND_Sound(S_STING2);
			addscore(plr, 750);
			treasuresfound++;
		}
	}), new Item(32, 32, true, false, new Callback() { // THEHORN
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TTHEHORN] = 1;
			showmessage("Ornate Horn", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			plr.vampiretime = 7200;
			// gain 5-10 hp when you kill something
			// for 60 seconds
			addscore(plr, 350);
			treasuresfound++;
		}
	}), new Item(30, 20, true, false, new Callback() { // SAPHIRERING
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TSAPHIRERING] = 1;
			showmessage("SAPPHIRE RING", 360);
			plr.armortype = 3;
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 25);
			treasuresfound++;
		}
	}), new Item(24, 24, true, false, new Callback() { // BRASSKEY
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TBRASSKEY] = 1;
			showmessage("BRASS KEY", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 15);
			treasuresfound++;
		}
	}), new Item(24, 24, true, false, new Callback() { // BLACKKEY
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TBLACKKEY] = 1;
			showmessage("BLACK KEY", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 15);
		}
	}), new Item(24, 24, true, false, new Callback() { // GLASSKEY
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TGLASSKEY] = 1;
			showmessage("GLASS KEY", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 15);
			treasuresfound++;
		}
	}), new Item(24, 24, true, false, new Callback() { // IVORYKEY
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.treasure[TIVORYKEY] = 1;
			showmessage("IVORY KEY", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			addscore(plr, 15);
			treasuresfound++;
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLSCARE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[0] < 10) {
				plr.orb[0] = 1;
				plr.orbammo[0]++;
				changebook(plr, 0);
				showmessage("Scare Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLNIGHT
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[1] < 10) {
				plr.orb[1] = 1;
				plr.orbammo[1]++;
				changebook(plr, 1);
				showmessage("Night Vision Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLFREEZE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[2] < 10) {
				plr.orb[2] = 1;
				plr.orbammo[2]++;
				changebook(plr, 2);
				showmessage("Freeze Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLMAGIC
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[3] < 10) {
				plr.orb[3] = 1;
				plr.orbammo[3]++;
				changebook(plr, 3);
				showmessage("Magic Arrow Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLOPEN
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[4] < 10) {
				plr.orb[4] = 1;
				plr.orbammo[4]++;
				changebook(plr, 4);
				showmessage("Open Door Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLFLY
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[5] < 10) {
				plr.orb[5] = 1;
				plr.orbammo[5]++;
				changebook(plr, 5);
				showmessage("Fly Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLFIREBALL
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[6] < 10) {
				plr.orb[6] = 1;
				plr.orbammo[6]++;
				changebook(plr, 6);
				showmessage("Fireball Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(35, 36, true, true, new Callback() { // SCROLLNUKE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.orbammo[7] < 10) {
				plr.orb[7] = 1;
				plr.orbammo[7]++;
				changebook(plr, 7);
				showmessage("Nuke Scroll", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				treasuresfound++;
			}
		}
	}), new Item(27, 27, false, false, new Callback() { // QUIVER
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[6] < 100) {
				plr.ammo[6] += 20;
				if (plr.ammo[6] > 100)
					plr.ammo[6] = 100;
				showmessage("Quiver of magic arrows", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				addscore(plr, 10);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // WALLBOW BOW
		@Override
		public void pickup(PLAYER plr, short i) {
			plr.weapon[6] = 1;
			plr.ammo[6] += 10;
			if (plr.ammo[6] > 100)
				plr.ammo[6] = 100;
			showmessage("Magic bow", 360);
			engine.deletesprite(i);
			SND_Sound(S_POTION1);
			if (plr.selectedgun < 6)
				autoweaponchange(plr, 6);
			addscore(plr, 10);
		}
	}), new Item(34, 21, false, false, new Callback() { // WEAPON1
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[1] < 12) {
				plr.weapon[1] = 1;
				plr.ammo[1] = 40;
				showmessage("Dagger", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 1)
					autoweaponchange(plr, 1);
				addscore(plr, 10);
			}
		}
	}), new Item(34, 21, false, false, new Callback() { // WEAPON1A
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[1] < 12) {
				plr.weapon[1] = 3;
				plr.ammo[1] = 80;
				showmessage("Jeweled Dagger", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				autoweaponchange(plr, 1);
				addscore(plr, 30);
			}
		}
	}), new Item(34, 21, false, false, new Callback() { // GOBWEAPON
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[2] < 12) {
				plr.weapon[2] = 1;
				plr.ammo[2] = 20;
				showmessage("Short sword", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 2)
					autoweaponchange(plr, 2);
				addscore(plr, 10);
			}
		}
	}), new Item(26, 26, false, false, new Callback() { // WEAPON2
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[3] < 12) {
				plr.weapon[3] = 1;
				plr.ammo[3] = 55;
				showmessage("Morning Star", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 3)
					autoweaponchange(plr, 3);
				addscore(plr, 20);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // WALLSWORD WEAPON3A
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[4] < 12) {
				plr.weapon[4] = 1;
				plr.ammo[4] = 160;
				showmessage("Broad Sword", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				autoweaponchange(plr, 4);
				addscore(plr, 60);
			}
		}
	}), new Item(44, 39, false, false, new Callback() { // WEAPON3
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[4] < 12) {
				plr.weapon[4] = 1;
				plr.ammo[4] = 80;
				showmessage("Broad Sword", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 4)
					autoweaponchange(plr, 4);
				addscore(plr, 30);
			}
		}
	}), new Item(25, 20, false, false, new Callback() { // WALLAXE WEAPON4
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[5] < 12) {
				plr.weapon[5] = 1;
				plr.ammo[5] = 100;
				showmessage("Battle axe", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 5)
					autoweaponchange(plr, 5);
				addscore(plr, 30);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // THROWHALBERD
		@Override
		public void pickup(PLAYER plr, short i) {
			// EG fix: don't collect moving halberds, be hurt by them as you should be
			// ...but only if you don't have the Onyx Ring
			if (sprite[i].statnum != INACTIVE && plr.treasure[TONYXRING] == 0) {
				addhealth(plr, -((engine.krand() % 20) + 5)); // Inflict pain
				// make it look and sound painful, too
				if ((engine.krand() % 9) == 0) {
					playsound_loc(S_PLRPAIN1 + (engine.rand() % 2), sprite[i].x, sprite[i].y);
				}
				startredflash(10);
				engine.deletesprite(i);
				return;
			}
			if (sprite[i].statnum != INACTIVE && plr.treasure[TONYXRING] != 0) {
				// Can we grab?
				if (plr.ammo[9] < 12 && plr.weapon[9] != 3) {
					// You grabbed a halberd out of midair, so go ahead and be smug about it
					if ((engine.rand() % 10) > 6)
						SND_Sound(S_PICKUPAXE);
					// fall through to collect
				} else {
					// Can't grab, so just block getting hit
					engine.deletesprite(i);
					return;
				}
			}

			if (plr.ammo[9] < 12) {
				plr.weapon[9] = 1;
				plr.ammo[9] = 30;
				showmessage("Halberd", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 9)
					autoweaponchange(plr, 9);
				addscore(plr, 30);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // WEAPON5
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[9] < 12) {
				plr.weapon[9] = 1;
				plr.ammo[9] = 30;
				showmessage("Halberd", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 9)
					autoweaponchange(plr, 9);
				addscore(plr, 30);
			}
		}
	}), new Item(12, 12, false, false, new Callback() { // GONZOBSHIELD
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.shieldpoints < 100) {
				plr.shieldtype = 2;
				plr.shieldpoints = 200;
				droptheshield = false;
				engine.deletesprite(i);
				showmessage("Magic Shield", 360);
				SND_Sound(S_POTION1);
				addscore(plr, 50);
			}
		}
	}), new Item(26, 26, false, false, new Callback() { // SHIELD
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.shieldpoints < 100) {
				plr.shieldpoints = 100;
				plr.shieldtype = 1;
				engine.deletesprite(i);
				showmessage("Shield", 360);
				droptheshield = false; // EG 17 Oct 2017
				SND_Sound(S_POTION1);
				addscore(plr, 10);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // WEAPON5B
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[9] < 12) { // XXX orly?
				engine.deletesprite(i);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // WALLPIKE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.weapon[7] == 1) {
				plr.weapon[7] = 2;
				plr.ammo[7] = 2;
				showmessage("Pike axe", 360);
				engine.deletesprite(i);
				SND_Sound(S_PICKUPAXE);
				addscore(plr, 30);
			}
			if (plr.weapon[7] == 2) {
				plr.weapon[7] = 2;
				plr.ammo[7]++;
				showmessage("Pike axe", 360);
				engine.deletesprite(i);
				SND_Sound(S_PICKUPAXE);
				// score(plr, 30);
			}
			if (plr.weapon[7] < 1) {
				if (plr.ammo[7] < 12) {
					plr.weapon[7] = 1;
					plr.ammo[7] = 30;
					showmessage("Pike axe", 360);
					engine.deletesprite(i);
					SND_Sound(S_POTION1);
					if (plr.selectedgun < 7)
						autoweaponchange(plr, 7);
					addscore(plr, 30);
				}
			}
		}
	}), new Item(20, 15, false, true, new Callback() { // WEAPON6
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.weapon[7] == 1) {
				plr.weapon[7] = 2;
				plr.ammo[7] = 10;
				showmessage("Pike axe", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				addscore(plr, 30);
			}
			if (plr.weapon[7] == 2) {
				plr.weapon[7] = 2;
				plr.ammo[7] += 10;
				showmessage("Pike axe", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				// score(plr, 30);
			}
			if (plr.weapon[7] < 1) {
				if (plr.ammo[7] < 12) {
					plr.weapon[7] = 2;
					plr.ammo[7] = 10;
					showmessage("Pike axe", 360);
					engine.deletesprite(i);
					SND_Sound(S_POTION1);
					if (plr.selectedgun < 7)
						autoweaponchange(plr, 7);
					addscore(plr, 30);
				}
			}
		}
	}), new Item(41, 36, false, true, new Callback() { // WEAPON7
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[8] < 12) {
				plr.weapon[8] = 1;
				plr.ammo[8] = 250;
				showmessage("Magic sword", 360);
				engine.deletesprite(i);
				SND_Sound(S_POTION1);
				if (plr.selectedgun < 8)
					autoweaponchange(plr, 8);
				addscore(plr, 30);
			}
		}
	}), new Item(32, 18, false, false, new Callback() { // GYSER
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.manatime < 1 && plr.invincibletime <= 0 && !plr.godMode) {
				playsound_loc(S_FIREBALL, sprite[i].x, sprite[i].y);
				addhealth(plr, -1);
				startredflash(30);
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // SPIKEBLADE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.invincibletime <= 0 && !plr.godMode && !justteleported) {
				addhealth(plr, -plr.health);
				plr.horiz = 200;
				plr.spiked = 1;
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // SPIKE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.invincibletime <= 0 && !plr.godMode && !justteleported) {
				addhealth(plr, -plr.health);
				plr.horiz = 200;
				plr.spiked = 1;
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // SPIKEPOLE
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.invincibletime <= 0 && !plr.godMode && !justteleported) {
				addhealth(plr, -plr.health);
				plr.horiz = 200;
				plr.spiked = 1;
			}
		}
	}), new Item(-1, -1, false, false, new Callback() { // MONSTERBALL
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.manatime < 1 && plr.invincibletime <= 0 && !plr.godMode)
				addhealth(plr, -1);
		}
	}), new Item(-1, -1, false, false, new Callback() { // WEAPON8
		@Override
		public void pickup(PLAYER plr, short i) {
			if (plr.ammo[8] < 12) {
                plr.weapon[8] = 1;
                plr.ammo[8] = 250;
                showmessage("Two Handed Sword", 360);
                engine.deletesprite(i);
                SND_Sound(S_POTION1);
                autoweaponchange(plr, 8);
                addscore(plr, 30);
           }
		}
	}), 

	};

}
