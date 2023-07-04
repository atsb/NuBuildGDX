package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.isalpha;
import static ru.m210projects.Build.Strhandler.isdigit;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_CORNER;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Gameutils.TEXT_XCENTER;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.ENDALPHANUM;
import static ru.m210projects.Wang.Names.STARTALPHANUM;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER0;
import static ru.m210projects.Wang.Panel.FRAG_BAR;
import static ru.m210projects.Wang.Panel.ID_TEXT;
import static ru.m210projects.Wang.Panel.PRI_FRONT_MAX;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateKills;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetSuicide;
import static ru.m210projects.Wang.Panel.pSpawnFullViewSprite;
import static ru.m210projects.Wang.Type.MyTypes.MOD4;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.PlayerStr;

public class Text {

	public static final int PANEL_FONT_G = 3636;
	public static final int PANEL_FONT_Y = 3646;
	public static final int PANEL_FONT_R = 3656;

	public static final int PANEL_SM_FONT_G = 3601;
	public static final int PANEL_SM_FONT_Y = 3613;
	public static final int PANEL_SM_FONT_R = 3625;

	public static final int TEXT_INFO_TIME = (3);
	public static final int TEXT_INFO_X = (16);
	public static final int TEXT_INFO_Y = (40);
	public static final int TEXT_INFO_YOFF = (10);

	public static final int TEXT_INFO_LINE(int line) {
		return (TEXT_INFO_Y + ((line) * TEXT_INFO_YOFF));
	}

	public static String KeyDoorMessage[] = { "You need a RED key for this door.", "You need a BLUE key for this door.",
			"You need a GREEN key for this door.", "You need a YELLOW key for this door.",
			"You need a GOLD key for this door.", "You need a SILVER key for this door.",
			"You need a BRONZE key for this door.", "You need a RED key for this door." };

	public static short GlobInfoStringTime = TEXT_INFO_TIME;

	public static short font_base[] = { PANEL_SM_FONT_G, PANEL_SM_FONT_Y, PANEL_SM_FONT_R };

	public static void DisplaySummaryString(PlayerStr pp, int xs, int ys, int color, int shade, char[] buffer, int flags) {
		short size, x;
		int ptr;
		short font_pic;

		for (ptr = 0, x = (short) xs; buffer[ptr] != 0; ptr++, x += size) {

			if (buffer[ptr] == ' ') {
				size = 4;
				continue;
			}

			switch (buffer[ptr]) {
			case '\\':
				buffer[ptr] = '0' - 1; // one pic before 0
				break;
			case ':':
				buffer[ptr] = '9' + 1; // one pic after nine
				break;
			}

			font_pic = (short) (font_base[color] + (buffer[ptr] - '0'));

			engine.rotatesprite(x << 16, ys << 16, 1 << 16, 0, font_pic, shade, 0, 26 | flags, 0, 0, xdim - 1, ydim - 1);
			size = (short) (engine.getTile(font_pic).getWidth() + 1);
		}
	}

	public static Panel_Sprite pClearTextLineID(PlayerStr pp, int id, int y, int pri) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;

			// early out
			if (psp.priority > pri)
				return (null);

			if (psp.ID == id && psp.y == y && psp.priority == pri) {
				SetRedrawScreen(psp.PlayerP != -1 ? Player[psp.PlayerP] : null);
				pSetSuicide(psp);
			}
		}

		return null;
	}

	public static void pClearTextLine(PlayerStr pp, int y) {
		SetRedrawScreen(pp);
		pClearTextLineID(pp, ID_TEXT, y, PRI_FRONT_MAX);
	}

	public static final Panel_Sprite_Func StringTimer = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite p) {
			StringTimer(p);
		}
	};

	public static void StringTimer(Panel_Sprite psp) {
		if ((psp.kill_tics -= synctics) <= 0) {
			SetRedrawScreen(psp.PlayerP != -1 ? Player[psp.PlayerP] : null);
			pKillSprite(psp);
			return;
		}
	}

	public static void PutStringTimer(PlayerStr pp, int x, int y, String string, int seconds) {
		int ndx;
		char c;
		Panel_Sprite nsp;
		int kill_tics;
		short id, ac;
		Panel_Sprite_Func func;

		int offset = x;

		if (seconds == 999) {
			pClearTextLineID(pp, ID_TEXT, y, PRI_FRONT_MAX);
			func = null;
			kill_tics = 0;
			id = ID_TEXT;
		} else {
			pClearTextLineID(pp, ID_TEXT, y, PRI_FRONT_MAX);
			func = StringTimer;
			kill_tics = seconds * 120;
			id = ID_TEXT;
		}

		for (ndx = 0; ndx < string.length() && (c = string.charAt(ndx)) != 0; ndx++) {
			ac = (short) (c - '!' + STARTALPHANUM);
			if ((ac < STARTALPHANUM || ac > ENDALPHANUM) && c != 32)
				break;

			if (c > 32 && c < 127) {
				nsp = pSpawnFullViewSprite(pp, ac, PRI_FRONT_MAX, offset, y);
				nsp.PanelSpriteFunc = func;
				nsp.kill_tics = (short) kill_tics;
				nsp.ID = id;
				offset += engine.getTile(ac).getWidth();
			} else if (c == 32)
				offset += 4; // Special case for space char
		}
	}

	public static void KillString(PlayerStr pp, short y) {
		pClearTextLineID(pp, ID_TEXT, y, PRI_FRONT_MAX);
	}

	Panel_Sprite pClearSpriteXY(PlayerStr pp, short x, short y) {
		Panel_Sprite n;
		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;

			if (psp.x == x && psp.y == y)
				pSetSuicide(psp);
		}

		return (null);
	}

	public static Panel_Sprite pClearSpriteID(PlayerStr pp, int id) {
		Panel_Sprite n;

		for (Panel_Sprite psp = pp.PanelSpriteList.Next; psp != pp.PanelSpriteList; psp = n) {
			n = psp.Next;

			if (psp.ID == id)
				pSetSuicide(psp);
		}

		return (null);
	}

	public static void pSpawnFullScreenSprite(int pic, int pri, int x, int y, int shade, int pal, int scale) {
		int flags = 26;

		engine.rotatesprite(x << 16, y << 16, scale, 0, pic, shade, pal, flags, 0, 0, xdim - 1, ydim - 1);
	}

	private static final char[] buffer = new char[32];

	public static void DisplayPanelNumber(PlayerStr pp, int xs, int ys, int number, int flags) {
		int size;

		Bitoa(number, buffer, 3);

		for (int ptr = 0, x = xs; buffer[ptr] != 0; ptr++, x += size) {
			if (!isdigit(buffer[ptr])) {
				size = 0;
				continue;
			}

			engine.rotatesprite(x << 16, ys << 16, 1 << 16, 0, PANEL_FONT_G + (buffer[ptr] - '0'), 0, 0, 26 | flags, 0, 0, xdim - 1, ydim - 1);

			size = engine.getTile(PANEL_FONT_G + (buffer[ptr] - '0')).getWidth() + 1;
		}
	}

	public static void DrawFragBar() {
		int num_frag_bars = ((numplayers - 1) / 4) + 1;
		if(gNet.FakeMultiplayer)
			num_frag_bars = ((gNet.FakeMultiNumPlayers - 1) / 4) + 1;

		for (int i = 0, y = 0; i < num_frag_bars; i++) {
			pSpawnFullScreenSprite(FRAG_BAR, PRI_MID, 0, y, 0, 0, 65536);
			y += engine.getTile(FRAG_BAR).getHeight() - 2;
		}

		// write each persons kill info to everybody
		for (int i = connecthead; i != -1; i = connectpoint2[i]) {
			PlayerUpdateKills(i, 0);
			DrawFragNumbers(i);
			DrawFragNames(i);
		}
	}

	private static final int xoffs2[] = { 7, 85, 163, 241 };

	public static void DrawFragNames(int ppnum) {
		PlayerStr pp = Player[ppnum];

		// frag bar 0 or 1
		int frag_bar = ((ppnum) / 4);
		// move y down according to frag bar number
		int ys = FRAG_YOFF + (engine.getTile(FRAG_BAR).getHeight() - 2) * frag_bar;

		// move x over according to the number of players
		int xs = xoffs2[MOD4(ppnum)];

		DisplayMiniBarSmString(pp, xs, ys, game.getScreen() instanceof GameAdapter ? pUser[pp.PlayerSprite].spal : (PALETTE_PLAYER0 + pp.TeamColor), toCharArray(pp.getName()), 0);
	}

	public static void DisplayMiniBarNumber(PlayerStr pp, int xs, int ys, int number, int flags) {
		int size;
		int pic;

		Bitoa(number, buffer, 3);
		for (int ptr = 0, x = xs; buffer[ptr] != 0; ptr++, x += size) {

			if (!isdigit(buffer[ptr])) {
				size = 0;
				continue;
			}

			pic = PANEL_FONT_G + (buffer[ptr] - '0');
			engine.rotatesprite(x << 16, ys << 16, (1 << 16), 0, pic, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | flags, 0, 0, xdim - 1, ydim - 1);

			size = engine.getTile(PANEL_FONT_G + (buffer[ptr] - '0')).getWidth() + 1;
		}
	}

	public static final int FRAG_FIRST_TILE = 2930; // exclamation point

	public static void DisplayMiniBarSmString(PlayerStr pp, int xs, int ys, int pal, char[] buffer, int flags) {
		int size = 4;
		int pic;

		for (int ptr = 0, x = xs; buffer[ptr] != 0; ptr++, x += size) {
			if (buffer[ptr] == ' ')
				continue;

			pic = FRAG_FIRST_TILE + (buffer[ptr] - '!');
			engine.rotatesprite(x << 16, ys << 16, (1 << 16), 0, pic, 0, pal,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | flags, 0, 0, xdim - 1, ydim - 1);
		}
	}

	public static void DisplaySmString(PlayerStr pp, int xs, int ys, int pal, char[] buffer) {
		short size = 4;

		for (int ptr = 0, x = xs; ptr < buffer.length && buffer[ptr] != 0; ptr++, x += size) {
			if (buffer[ptr] == ' ')
				continue;

			pSpawnFullScreenSprite(FRAG_FIRST_TILE + (buffer[ptr] - '!'), PRI_FRONT_MAX, x, ys, 0, pal, 1 << 16);
		}
	}

	public static void DrawFragString(PlayerStr pp, int xs, int ys, char[] buffer) {
		short size = 4;

		for (int ptr = 0, x = xs; buffer[ptr] != 0; ptr++, x += size) {
			if (buffer[ptr] == ' ')
				continue;

			engine.rotatesprite(x << 16, ys << 16, (1 << 16), 0, FRAG_FIRST_TILE + (buffer[ptr] - '!'), 0,
					game.getScreen() instanceof GameAdapter ? pUser[pp.PlayerSprite].spal : (PALETTE_PLAYER0 + pp.TeamColor), ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER, 0, 0, xdim - 1,
					ydim - 1);
		}
	}

	private static final int xoffs[] = { 69, 147, 225, 303 };

	// black tile to erase frag count
	public static final int FRAG_YOFF = 2;

	public static void DrawFragNumbers(int ppnum) {
		PlayerStr pp = Player[ppnum];

		// frag bar 0 or 1
		int frag_bar = ((ppnum) / 4);
		// move y down according to frag bar number
		int ys = FRAG_YOFF + (engine.getTile(FRAG_BAR).getHeight() - 2) * frag_bar;

		// move x over according to the number of players
		int xs = xoffs[MOD4(ppnum)];

		Bitoa(pp.Kills, buffer, pp.Kills >= 0 ? 3 : 2);

		DrawFragString(pp, xs, ys, buffer);
	}

	public static void PutStringInfo(PlayerStr pp, String string) {
		if (pp != Player[myconnectindex])
			return;

		if (!gs.Messages)
			return;

		Console.Println(string); // Put it in the console too
		PutStringInfoLine(pp, string);
	}

	public static void PutStringInfoLine(PlayerStr pp, String string) {
		if (pp.pnum != myconnectindex)
			return;

		MeasureString(string);

		int x = TEXT_XCENTER(text_w);
		int y = TEXT_INFO_LINE(0);

		PutStringTimer(pp, x, y, string, GlobInfoStringTime);
	}

	private static byte lg_xlat_num[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private static int text_w;

	public static void MeasureString(String string) {
		int ndx, width, height;
		char c;
		int pic;

		width = 0;
		height = 0;

		for (ndx = 0; ndx < string.length() && (c = string.charAt(ndx)) != 0; ndx++) {
			{
				if (isalpha(c)) {
					c = Character.toUpperCase(c);
					pic = STARTALPHANUM + (c - 'A');
				} else if (isdigit(c)) {
					pic = STARTALPHANUM + lg_xlat_num[(c - '0')];
				} else if (c == ' ') {
					width += 10; // Special case for space char
					continue;
				} else {
					continue;
				}

				width += engine.getTile(pic).getWidth() + 1;
				if (height < engine.getTile(pic).getHeight())
					height = engine.getTile(pic).getHeight();
			}

			text_w = width;
//			text_h = height;
		}
	}

}
