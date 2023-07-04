// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Menus;

import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.LSP.Main;

public class LSPMenuUserContent extends BuildMenu {

	public LSPMenuUserContent(final Main app)
	{
		int width = 240;
		MenuFileBrowser list = new MenuFileBrowser(app, app.getFont(2), app.getFont(0), app.getFont(2), 40, 55, width, 1, 17, 611) {
			@Override
			protected void drawHeader(int x1, int x2, int y)
			{
				/*directories*/ app.getFont(1).drawText(x1, y, dirs, 32768, -32, topPal, TextAlign.Left, 2, fontShadow);
				/*files*/ app.getFont(1).drawText(x2 + 13, y, ffs, 32768, -32, topPal, TextAlign.Left, 2, fontShadow);
			}

			@Override
			public void init() {
				registerExtension("map", 0, 0);
			}
			
			@Override
			public void handleDirectory(DirectoryEntry dir) {
				/* nothing */
			}

			@Override
			public void handleFile(FileEntry file) {
				if(file.getExtension().equals("map"))
					addFile(file, file.getName());
			}

			@Override
			public void invoke(Object obj) {
				if(obj == null) return;
				
				if(obj instanceof FileEntry)  { 
					FileEntry fil = (FileEntry) obj;
					if((fil.getExtension().equals("map"))) {
						
						
						app.pMenu.mClose();
					}
				}
			}
		};

		list.topPal = 20;
		addItem(list, true);
	}
	
	
}
