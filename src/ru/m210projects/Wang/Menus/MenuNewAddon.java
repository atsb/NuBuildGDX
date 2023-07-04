package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Factory.WangMenuHandler.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;
import ru.m210projects.Wang.Type.GameInfo;

public class MenuNewAddon extends BuildMenu {

	private static final int nMaxEpisodes = 2;
	private GameInfo game;
	private MenuList mSlot;
	private final List<char[]> mEpisodelist;
	private final List<char[]> mEpisodeDescr;
	private int[] episodeNum;
	
	public MenuNewAddon(final Main app)
	{
		final WangMenuHandler menu = app.menu;
		addItem(new WangTitle("USER EPISODE"), false);
		
		mEpisodelist = new ArrayList<char[]>();
		mEpisodeDescr = new ArrayList<char[]>();
		episodeNum = new int[nMaxEpisodes];
		MenuProc newEpProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuList button = (MenuList) pItem;
				MenuDifficulty next = (MenuDifficulty) menu.mMenus[DIFFICULTY];
				next.setEpisode(game, episodeNum[button.l_nFocus]);
				menu.mOpen(next, -1);
			}
		};
		
		mSlot = new MenuList(mEpisodelist, app.getFont(2), 35, 45, 320, 0, null, newEpProc, nMaxEpisodes) {
			@Override
			public int mFontOffset() {
				return font.getHeight() + 17;
			}
			
			@Override
			public void draw(MenuHandler handler) {
				super.draw(handler);
				
				if(text.size() > 0) {
					int py = y;
					for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {	
						app.getFont(1).drawText(x, py + 17, mEpisodeDescr.get(i), 0, 4, TextAlign.Left, 2, false);
						py += mFontOffset();
					}
				}
			}
		};
		addItem(mSlot, true);
	}
	
	public boolean setEpisode(GameInfo game)
	{
		this.game = game;
		mEpisodelist.clear();
		mEpisodeDescr.clear();
		
		Arrays.fill(episodeNum, -1);
		
		for (int i = 0; i < game.episode.length; i++) {
			if(game.episode[i] != null && !game.episode[i].Title.isEmpty() && game.episode[i].nMaps != 0) {
				episodeNum[mEpisodelist.size()] = i;
				mEpisodelist.add(game.episode[i].Title.toCharArray());
				mEpisodeDescr.add(game.episode[i].Description.toCharArray());
			}
		}
		mSlot.len = mEpisodelist.size();
		mSlot.l_nFocus = mSlot.l_nMin = 0;
		
		return mEpisodelist.size() == 1;
	}
}
