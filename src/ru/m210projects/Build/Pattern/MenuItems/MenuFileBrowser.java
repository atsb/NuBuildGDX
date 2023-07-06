//This file is part of BuildGDX.
//Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.MenuItems;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Build.Engine.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Compat.Path;

public abstract class MenuFileBrowser extends MenuItem {

	private static class StringList extends LinkedList<String> {
		private static final long serialVersionUID = 1L;
	}

	protected StringList[] list = new StringList[2];

	private static class ExtProp {
		int pal;
		int priority;

		public ExtProp(int pal, int priority) {
			this.pal = pal;
			this.priority = priority;
		}
	}

	private final Comparator<String> fileComparator = new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			Object o1 = fileUnit.get(s1);
			Object o2 = fileUnit.get(s2);

			ExtProp p1 = getPropertie(o1);
			ExtProp p2 = getPropertie(o2);
			if (p1 != null && p2 != null) {
				int c = p2.priority - p1.priority;
				if (c != 0)
					return c;
			}

			if (o1 instanceof FileEntry && o2 instanceof FileEntry)
				return ((FileEntry) o1).compareTo((FileEntry) o2);

			return s1.compareTo(s2);
		}
	};

	protected HashMap<String, Object> fileUnit = new HashMap<String, Object>();
	protected HashMap<String, ExtProp> extensionProperties = new HashMap<String, ExtProp>();
	protected HashMap<Class<?>, ExtProp> classProperties = new HashMap<Class<?>, ExtProp>();

	private final int DIRECTORY = 0;
	private final int FILE = 1;
	public String back = "..";
	protected char[] dirs = "Directories".toCharArray();
	protected char[] ffs = "Files".toCharArray();

	private int touchY;
	private final int[] scrollX = new int[2];
	public boolean[] scrollTouch = new boolean[2];

	protected int[] l_nMin;
	protected int[] l_nFocus;
	protected final int nListItems;
	protected final int nItemHeight;

	public String path;
	protected int currColumn;
	protected DirectoryEntry currDir;

	private final BuildEngine draw;
	private final SliderDrawable slider;
	private int nBackground;
	private int scrollerHeight;
	protected BuildFont topFont, pathFont;
	public int topPal, pathPal, listPal, backgroundPal;
	public int transparent = 1;

	private long checkDirectory; // checking for new files

	public void registerExtension(String ext, int pal, int priority) {
		extensionProperties.put(ext, new ExtProp(pal, priority));
	}

	public void registerClass(Class<?> cl, int pal, int priority) {
		classProperties.put(cl, new ExtProp(pal, priority));
	}

	public MenuFileBrowser(BuildGame app, BuildFont font, BuildFont topFont, BuildFont pathFont, int x, int y,
			int width, int nItemHeight, int nListItems, int nBackground) {
		super(null, font);

		list[DIRECTORY] = new StringList();
		list[FILE] = new StringList();

		this.flags = 3 | 4;
		this.draw = app.pEngine;
		this.slider = app.pSlider;

		this.x = x;
		this.y = y;
		this.width = width;
		this.nItemHeight = nItemHeight;
		this.nListItems = nListItems;
		this.topFont = topFont;
		this.pathFont = pathFont;
		this.nBackground = nBackground;

		this.l_nMin = new int[2];
		this.l_nFocus = new int[2];
		this.currColumn = FILE;

		init();
		changeDir(BuildGdx.compat.getDirectory(Path.Game));
	}

	public abstract void init();

	public abstract void handleFile(FileEntry file);

	public abstract void invoke(Object fil);

	public abstract void handleDirectory(DirectoryEntry dir);

	public String getFileName() {
		return list[FILE].get(l_nFocus[FILE]);
	}

	public DirectoryEntry getDirectory() {
		return currDir;
	}

	public int mFontOffset() {
		return font.getHeight() + nItemHeight;
	}

	private void changeDir(DirectoryEntry dir) {
		if (!dir.checkCacheList() && currDir == dir)
			return;

		list[DIRECTORY].clear();
		fileUnit.clear();
		list[FILE].clear();

		currDir = dir;
		path = File.separator;
		if (dir.getRelativePath() != null)
			path += currDir.getRelativePath();

		for (Iterator<DirectoryEntry> it = dir.getDirectories().values().iterator(); it.hasNext();) {
			DirectoryEntry sdir = it.next();
			if (!sdir.getName().equals("<userdir>"))
				list[DIRECTORY].add(toLowerCase(sdir.getName()));
		}
		Collections.sort(list[DIRECTORY]);
		if (dir.getParent() != null)
			list[DIRECTORY].add(0, back);

		handleDirectory(dir);

		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (extensionProperties.get(file.getExtension()) != null)
				handleFile(file);
		}
		sortFiles();

		l_nFocus[DIRECTORY] = l_nMin[DIRECTORY] = 0;
		l_nFocus[FILE] = l_nMin[FILE] = 0;
	}

	public void addFile(Object file, String name) {
		fileUnit.put(name, file);
		list[FILE].add(name);
	}

	public void sortFiles() {
		Collections.sort(list[FILE], fileComparator);
	}

	protected void drawHeader(int x1, int x2, int y) {
		/* directories */ topFont.drawText(x1, y, dirs, -32, topPal, TextAlign.Left, 2, fontShadow);
		/* files */ topFont.drawText(x2, y, ffs, -32, topPal, TextAlign.Left, 2, fontShadow);
	}

	protected void drawPath(int x, int y) {
		brDrawText(pathFont, toCharArray("path: " + path), x, y, -32, pathPal, 0, this.x + this.width);
	}

	@Override
	public void draw(MenuHandler handler) {
		int yColNames = y + 3;
		int yPath = yColNames + topFont.getHeight() + 2;
		int yList = yPath + pathFont.getHeight() + 2;
		int scrollerWidth = slider.getScrollerWidth();

		draw.rotatesprite(x << 16, y << 16, 65536, 0, nBackground, 127, backgroundPal, 10 | 16 | transparent, 0, 0,
				coordsConvertXScaled(x + width, ConvertType.Normal),
				coordsConvertYScaled(yList + nListItems * mFontOffset() + 6));

		int px = x + 3;
		drawHeader(px, x - 3 + width - topFont.getWidth(ffs), yColNames);
		px += scrollerWidth + 3;
		drawPath(px, yPath);

		int py = yList;
		for (int i = l_nMin[DIRECTORY]; i >= 0 && i < l_nMin[DIRECTORY] + nListItems
				&& i < list[DIRECTORY].size(); i++) {
			int pal = listPal; // handler.getPal(font, item); //listPal;
			int shade = handler.getShade(
					currColumn == DIRECTORY && i == l_nFocus[DIRECTORY] ? m_pMenu.m_pItems[m_pMenu.m_nFocus] : null);
			if (currColumn == DIRECTORY && i == l_nFocus[DIRECTORY])
				pal = handler.getPal(font, m_pMenu.m_pItems[m_pMenu.m_nFocus]);

			text = toCharArray(list[DIRECTORY].get(i));
            brDrawText(font, text, px, py, shade, pal, 0, this.x + this.width / 2 - 4);
			py += mFontOffset();
		}

		py = yList;
		for (int i = l_nMin[FILE]; i >= 0 && i < l_nMin[FILE] + nListItems && i < list[FILE].size(); i++) {
			int pal = listPal;
			if (currColumn == FILE && i == l_nFocus[FILE])
				pal = handler.getPal(font, m_pMenu.m_pItems[m_pMenu.m_nFocus]);
			int shade = handler
					.getShade(currColumn == FILE && i == l_nFocus[FILE] ? m_pMenu.m_pItems[m_pMenu.m_nFocus] : null);

            String name = list[FILE].get(i);
			text = toChars(name);
			Object obj = fileUnit.get(name);
			ExtProp p = getPropertie(obj);
			if (p != null) {
				int itemPal = p.pal;
				if (itemPal != 0)
					pal = itemPal;
			}

			px = x + width - font.getWidth(text) - scrollerWidth - 5;
			brDrawText(font, text, px, py, shade, pal, this.x + this.width / 2 + 4, this.x + this.width);
			py += mFontOffset();
		}

		scrollerHeight = nListItems * mFontOffset();

		// Files scroll
		int nList = BClipLow(list[FILE].size() - nListItems, 1);
		int posy = yList + (scrollerHeight - slider.getScrollerHeight()) * l_nMin[FILE] / nList;

		scrollX[FILE] = x + width - scrollerWidth - 1;
		slider.drawScrollerBackground(scrollX[FILE], yList, scrollerHeight, 0, 0);
		slider.drawScroller(scrollX[FILE], posy,
				handler.getShade(currColumn == FILE ? m_pMenu.m_pItems[m_pMenu.m_nFocus] : null), 0);

		// Directory scroll
		nList = BClipLow(list[DIRECTORY].size() - nListItems, 1);
		posy = yList + (scrollerHeight - slider.getScrollerHeight()) * l_nMin[DIRECTORY] / nList;

		scrollX[DIRECTORY] = x + 2;
		slider.drawScrollerBackground(scrollX[DIRECTORY], yList, scrollerHeight, 0, 0);
		slider.drawScroller(scrollX[DIRECTORY], posy,
				handler.getShade(currColumn == DIRECTORY ? m_pMenu.m_pItems[m_pMenu.m_nFocus] : null), 0);

		if (System.currentTimeMillis() - checkDirectory >= 2000) {
			if (currDir.checkCacheList())
				refreshList();
			checkDirectory = System.currentTimeMillis();
		}
	}

	protected void brDrawText(BuildFont font, char[] text, int x, int y, int shade, int pal, int x1, int x2) {
		int tptr = 0, tx = 0;

		while (tptr < text.length && text[tptr] != 0) {
			if (tx + x > x1 && tx + x <= x2)
				x += font.drawChar(x, y, text[tptr], shade, pal, 2, fontShadow);
			else
				x += font.getWidth(text[tptr]);

			tptr++;
		}
	}

	protected char[] buffer = new char[40];

	protected char[] toChars(String text) {
		int symbols = 0;
		int pos = text.length() - 1;
		int len = Math.min(text.length(), buffer.length - 1);
		Arrays.fill(buffer, (char) 0);
		while (pos >= 1 && symbols < len - 1 && text.charAt(pos - 1) != File.separatorChar) {
			symbols++;
			pos--;
		}
		text.getChars(pos, pos + symbols + 1, buffer, 0);

        return buffer;
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		switch (opt) {
		case MWUP:
			if (l_nMin[currColumn] > 0)
				l_nMin[currColumn]--;
			return false;
		case MWDW:
			if (l_nMin[currColumn] < list[currColumn].size() - nListItems)
				l_nMin[currColumn]++;
			return false;
		case UP:
			l_nFocus[currColumn]--;
			if (l_nFocus[currColumn] >= 0 && l_nFocus[currColumn] < l_nMin[currColumn])
				if (l_nMin[currColumn] > 0)
					l_nMin[currColumn]--;
			if (l_nFocus[currColumn] < 0) {
				l_nFocus[currColumn] = list[currColumn].size() - 1;
				l_nMin[currColumn] = list[currColumn].size() - nListItems;
				if (l_nMin[currColumn] < 0)
					l_nMin[currColumn] = 0;
			}
			return false;
		case DW:
			l_nFocus[currColumn]++;
			if (l_nFocus[currColumn] >= l_nMin[currColumn] + nListItems
					&& l_nFocus[currColumn] < list[currColumn].size())
				l_nMin[currColumn]++;
			if (l_nFocus[currColumn] >= list[currColumn].size()) {
				l_nFocus[currColumn] = 0;
				l_nMin[currColumn] = 0;
			}
			return false;
		case LEFT:
			if (list[DIRECTORY].size() > 0)
				currColumn = DIRECTORY;
			return false;
		case RIGHT:
			if (list[FILE].size() > 0)
				currColumn = FILE;
			return false;
		case ENTER:
		case LMB:
			if (opt == MenuOpt.LMB && scrollTouch[FILE] || scrollTouch[DIRECTORY]) {
				if (list[currColumn].size() <= nListItems)
					return false;

				int nList = BClipLow(list[currColumn].size() - nListItems, 1);
				int nRange = scrollerHeight;

				int py = y + 3 + pathFont.getHeight() + 2 + topFont.getHeight() + 2;

				l_nFocus[currColumn] = -1;
				l_nMin[currColumn] = BClipRange(((touchY - py) * nList) / nRange, 0, nList);

				return false;
			}
			if (list[DIRECTORY].size() > 0 && currColumn == DIRECTORY) {
				if (l_nFocus[DIRECTORY] == -1)
					return false;
				String dirName = list[DIRECTORY].get(l_nFocus[DIRECTORY]);
				if (dirName.equals(back))
					changeDir(currDir.getParent());
				else
					changeDir(currDir.checkDirectory(dirName));
			} else if (list[FILE].size() > 0 && currColumn == FILE) {

				if (l_nFocus[FILE] == -1)
					return false;

				invoke(fileUnit.get(getFileName()));
			}
			getInput().resetKeyStatus();
			return false;
		case ESC:
		case RMB:
			return true;
		case BSPACE:
			if (currDir.getParent() != null) {
				changeDir(currDir.getParent());
			}
			return false;
		case PGUP:
			l_nFocus[currColumn] -= (nListItems - 1);
			if (l_nFocus[currColumn] >= 0 && l_nFocus[currColumn] < l_nMin[currColumn])
				if (l_nMin[currColumn] > 0)
					l_nMin[currColumn] -= (nListItems - 1);
			if (l_nFocus[currColumn] < 0 || l_nMin[currColumn] < 0) {
				l_nFocus[currColumn] = 0;
				l_nMin[currColumn] = 0;
			}
			return false;
		case PGDW:
			l_nFocus[currColumn] += (nListItems - 1);
			if (l_nFocus[currColumn] >= l_nMin[currColumn] + nListItems
					&& l_nFocus[currColumn] < list[currColumn].size())
				l_nMin[currColumn] += (nListItems - 1);
			if (l_nFocus[currColumn] >= list[currColumn].size()
					|| l_nMin[currColumn] > list[currColumn].size() - nListItems) {
				l_nFocus[currColumn] = list[currColumn].size() - 1;
				if (list[currColumn].size() >= nListItems)
					l_nMin[currColumn] = list[currColumn].size() - nListItems;
				else if (l_nFocus[currColumn] >= l_nMin[currColumn] + nListItems)
					l_nMin[currColumn] = list[currColumn].size() - 1;
			}
			return false;
		case HOME:
			l_nFocus[currColumn] = 0;
			l_nMin[currColumn] = 0;
			return false;
		case END:
			l_nFocus[currColumn] = list[currColumn].size() - 1;
			if (list[currColumn].size() >= nListItems)
				l_nMin[currColumn] = list[currColumn].size() - nListItems;
			else if (l_nFocus[currColumn] >= l_nMin[currColumn] + nListItems)
				l_nMin[currColumn] = list[currColumn].size() - 1;
			return false;
		default:
			return false;
		}
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if (mx >= x + width / 2)
			currColumn = 1;
		else
			currColumn = 0;

		if (!BuildGdx.input.isTouched()) {
			scrollTouch[DIRECTORY] = false;
			scrollTouch[FILE] = false;
		}

		touchY = my;
		if (mx > scrollX[currColumn] && mx < scrollX[currColumn] + slider.getScrollerWidth()) {
			scrollTouch[currColumn] = BuildGdx.input.isTouched();
			return true;
		}

		if ((!scrollTouch[DIRECTORY] && !scrollTouch[FILE]) && list[currColumn].size() > 0) {
			int py = y + 3 + pathFont.getHeight() + 2 + topFont.getHeight() + 2;

			for (int i = l_nMin[currColumn]; i >= 0 && i < l_nMin[currColumn] + nListItems
					&& i < list[currColumn].size(); i++) {
				if (mx > x && mx < scrollX[FILE])
					if (my > py && my < py + font.getHeight()) {
						l_nFocus[currColumn] = i;
						return true;
					}

				py += mFontOffset();
			}
		}
		return false;
	}

	private ExtProp getPropertie(Object obj) {
		if (obj instanceof FileEntry)
			return extensionProperties.get(((FileEntry) obj).getExtension());
		else if (obj != null)
			return classProperties.get(obj.getClass());

		return null;
	}

	public void refreshList() {
		DirectoryEntry dir = currDir;
		currDir = null;
		changeDir(dir);
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
		for (int i = 0; i < 2; i++)
			l_nFocus[i] = l_nMin[i] = 0;
	}

}
