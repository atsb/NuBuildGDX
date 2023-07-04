package ru.m210projects.Wang.Type;

import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Wang.Gameutils.MAX_LEVELS;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.Scriptfile;

public class CustomScript extends Scriptfile {

	private LevelInfo[] custommaps = new LevelInfo[MAX_LEVELS];
	private EpisodeInfo[] customepisodes = new EpisodeInfo[2];

	private enum Token {
		CM_MAP, CM_EPISODE, CM_TITLE, CM_FILENAME, CM_SONG, CM_CDATRACK, CM_BESTTIME, CM_PARTIME, CM_SUBTITLE,
//		CM_SKILL, CM_TEXT, CM_COOKIE, CM_GOTKEY, CM_NEEDKEY, CM_INVENTORY, CM_AMOUNT, CM_WEAPON, CM_AMMONAME, CM_MAXAMMO,
//		CM_DAMAGEMIN, CM_DAMAGEMAX, CM_SECRET, CM_QUIT, 
		T_EOF, T_ERROR,
	};

	private final Map<String, Token> cm_tokens = new HashMap<String, Token>() {
		private static final long serialVersionUID = 1L;
		{
			put("map", Token.CM_MAP);
			put("level", Token.CM_MAP);
			put("episode", Token.CM_EPISODE);
//			put("skill", Token.CM_SKILL);
//			put("cookie", Token.CM_COOKIE);
//			put("fortune", Token.CM_COOKIE);
//			put("gotkey", Token.CM_GOTKEY);
//			put("inventory", Token.CM_INVENTORY);
//			put("weapon", Token.CM_WEAPON);
//			put("needkey", Token.CM_NEEDKEY);
//			put("secret", Token.CM_SECRET);
//			put("quit", Token.CM_QUIT);
		}
	};

	private final Map<String, Token> cm_map_tokens = new HashMap<String, Token>() {
		private static final long serialVersionUID = 1L;
		{
			put("title", Token.CM_TITLE);
			put("name", Token.CM_TITLE);
			put("description", Token.CM_TITLE);
			put("filename", Token.CM_FILENAME);
			put("file", Token.CM_FILENAME);
			put("fn", Token.CM_FILENAME);
			put("levelname", Token.CM_FILENAME);
			put("song", Token.CM_SONG);
			put("music", Token.CM_SONG);
			put("songname", Token.CM_SONG);
			put("cdatrack", Token.CM_CDATRACK);
			put("cdtrack", Token.CM_CDATRACK);
			put("besttime", Token.CM_BESTTIME);
			put("partime", Token.CM_PARTIME);
		}
	};

	private final Map<String, Token> cm_episode_tokens = new HashMap<String, Token>() {
		private static final long serialVersionUID = 1L;
		{
			put("title", Token.CM_TITLE);
			put("name", Token.CM_TITLE);
			put("description", Token.CM_TITLE);
			put("subtitle", Token.CM_SUBTITLE);
		}
	};

//	private final Map<String, Token> cm_skill_tokens = new HashMap<String, Token>() {
//		private static final long serialVersionUID = 1L;
//		{
//			put("title", Token.CM_TITLE);
//			put("name", Token.CM_TITLE);
//			put("description", Token.CM_TITLE);
//		}
//	};
//
//	private final Map<String, Token> cm_inventory_tokens = new HashMap<String, Token>() {
//		private static final long serialVersionUID = 1L;
//		{
//			put("title", Token.CM_TITLE);
//			put("name", Token.CM_TITLE);
//			put("description", Token.CM_TITLE);
//			put("amount", Token.CM_AMOUNT);
//		}
//	};
//
//	private final Map<String, Token> cm_weapons_tokens = new HashMap<String, Token>() {
//		private static final long serialVersionUID = 1L;
//		{
//
//			put("title", Token.CM_TITLE);
//			put("name", Token.CM_TITLE);
//			put("description", Token.CM_TITLE);
//			put("ammoname", Token.CM_AMMONAME);
//			put("maxammo", Token.CM_MAXAMMO);
//			put("mindamage", Token.CM_DAMAGEMIN);
//			put("maxdamage", Token.CM_DAMAGEMAX);
//			put("pickup", Token.CM_AMOUNT);
//			put("weaponpickup", Token.CM_WEAPON);
//		}
//	};

	public CustomScript(byte[] data) {
		super("", data);
		parse();

		for (int i = 1; i < 5; i++) {
			EpisodeInfo ep = customepisodes[0];
			LevelInfo map = custommaps[i];
			if (map != null) {
				ep.gMapInfo[i - 1] = map;
				ep.nMaps++;
			}
		}

		for (int i = 5; i < MAX_LEVELS; i++) {
			EpisodeInfo ep = customepisodes[1];
			LevelInfo map = custommaps[i];
			if (map != null) {
				ep.gMapInfo[i - 5] = map;
				ep.nMaps++;
			}
		}
	}
	
	public void apply(GameInfo game)
	{
		String title = null;
		for(int i = 0; i < 2; i++) {
			if(customepisodes[i] != null && customepisodes[i].Title != null && !customepisodes[i].Title.isEmpty()) {
				title = customepisodes[i].Title;
				break;
			}
		}
		game.episode = customepisodes;
		if(title != null)
			game.Title = title;
	}

	private Token gettoken(Map<String, Token> list) {
		int tok;
		if ((tok = gettoken()) == -2)
			return Token.T_EOF;

		Token out = list.get(toLowerCase(textbuf.substring(tok, textptr)));
		if (out != null)
			return out;

		errorptr = textptr;
		return Token.T_ERROR;
	}

	private int getnumber() {
		String txt = getstring();
		if (txt == null)
			return -1;
		txt = txt.replaceAll("[^0-9]", "");

		try {
			return Integer.parseInt(txt);
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public String getstring() {
		int out = gettoken();
		if (out == -1 || out == -2) {
			return null;
		}

		String txt = textbuf.substring(out, textptr);
		txt = txt.replaceAll("\"", "");
		return txt.replace("/", File.separator);
	}

	private void parse() {
		char[] buf = new char[10];

		while (!eof()) {
			Token tokn = gettoken(cm_tokens);
			int line = getlinum(textptr);

			int braceend = -1;
			switch (tokn) {
			case CM_MAP:
				Arrays.fill(buf, (char) 0);
				int curmap = -1;
				if ((curmap = getnumber()) == -1)
					break;
				if ((braceend = getbraces()) == -1)
					break;

				if (curmap < 1 || curmap > MAX_LEVELS) {
					Console.Println("Error: map number " + curmap + " not in range 1-" + MAX_LEVELS + " on line "
							+ filename + ":" + line);
					textptr = braceend;
					break;
				}

				LevelInfo ptrmap = custommaps[curmap] = new LevelInfo("", "", "", "", "");
				while (textptr <= braceend) {
					switch (gettoken(cm_map_tokens)) {
					case CM_FILENAME:
						ptrmap.LevelName = toLowerCase(getstring());
						break;
					case CM_SONG:
						ptrmap.SongName = toLowerCase(getstring());
						break;
					case CM_TITLE:
						ptrmap.Description = getstring();
						break;
					case CM_BESTTIME: {
						int best = getnumber();
						buildString(buf, Bitoa(best / 60, buf), ":", best % 60, 2);
						ptrmap.BestTime = new String(buf).trim();
					}
						break;
					case CM_PARTIME: {
						int par = getnumber();
						buildString(buf, Bitoa(par / 60, buf), ":", par % 60, 2);
						ptrmap.ParTime = new String(buf).trim();
					}
						break;
					case CM_CDATRACK:
						ptrmap.CDtrack = getnumber();
						break;
					default:
						if (ltextptr == braceend + 1)
							break;

						Console.Println("Error on line " + filename + ":" + getlinum(textptr));
						break;
					}
				}
				break;
			case CM_EPISODE:
				int curep = -1;
				if ((curep = getnumber()) == -1)
					break;
				if ((braceend = getbraces()) == -1)
					break;

				if (curep < 1 || curep > 2) {
					Console.Println(
							"Error: episode number " + curep + " not in range 1-2 on line " + filename + ":" + line);
					textptr = braceend;
					break;
				}
				EpisodeInfo ep = customepisodes[--curep] = new EpisodeInfo();
				while (textptr <= braceend) {
					switch (gettoken(cm_episode_tokens)) {
					case CM_TITLE:
						ep.Title = getstring();
						break;
					case CM_SUBTITLE:
						ep.Description = getstring();
						break;
					default:
						if (ltextptr == braceend + 1)
							break;

						Console.Println("Error on line " + filename + ":" + getlinum(textptr));
						break;
					}
				}
				break;
			default:
				Console.Println("Error on line " + filename + ":" + getlinum(textptr));
				break;
			}
		}
	}
}
