// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Menus;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Redneck.Main;

public class CreditsMenu extends BuildMenu {

	public CreditsMenu(final Main app) {
		MenuItem mPages[] = new MenuItem[24];

		mPages[0] = new MenuPage(160, 90, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Original concept, design and direction", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Drew Markham", 0, 0, TextAlign.Center, 2, false);
			}
		};
		mPages[1] = new MenuPage(160, 90, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Produced by", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Greg Goodrich", 0, 0, TextAlign.Center, 2, false);
			}
		};
		mPages[2] = new MenuPage(160, 90, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Game programming", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Rafael PAIZ", 0, 0, TextAlign.Center, 2, false);
			}
		};
		mPages[3] = new MenuPage(160, 90, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "ART Directors", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Claire Praderie     Maxx Raufman", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[4] = new MenuPage(160, 80, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Lead Level Designer", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 10, "Alex Mayberry", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 25, "Level Design", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 35, "Mal BlackWell", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 45, "Sverre Kvernmo", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[5] = new MenuPage(160, 90, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Senior animatior and artist", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Jason Hoover", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[6] = new MenuPage(160, 90, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Technical Director", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Barry Dempsey", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[7] = new MenuPage(160, 60, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Motion Capture Specialist and", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 10, "character animation", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 20, "Amit Doron", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 35, "A.I. Programming", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 45, "Arthur Donavan", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 60, "Additional animation", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 70, "George Karl", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[8] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Character design", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 10, "Corkey Lehmkuhl", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 25, "Map painters", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 35, "Viktor Antonov", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 45, "Matthias Beeguer", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 55, "Stephan Burle", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 70, "Sculptors", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 80, "George Engel", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 90, "Jake Garber", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 100, "Jeff Himmel", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[9] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Character voices", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 15, "Leonard", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 25, "Burton Gilliam", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 40, "Bubba. Billy Ray. Skinny Ol'Coot", 0, 0, TextAlign.Center, 2,false);
				app.getFont(1).drawText(x, y + 50, "and the Turd Minion", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 60, "Drew Markham", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 75, "Sheriff Lester T.Hobbes", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 85, "Mojo Nixon", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 100, "Alien Vixen", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 110, "Peggy Jo Jacobs", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[10] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				app.getFont(1).drawText(x, y, "Sound Design", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 10, "Gary Bradfield", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 25, "Music", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 35, "Mojo Nixon", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 45, "The Beat Farmers", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 55, "The reverend Horton Heat", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 65, "Cement Pond", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 80, "Additional Sound Effects", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, y + 90, "Jim Spurgin", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[11] = new MenuPage(160, 70, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Motion capture actor", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "J.P. Manoux", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Motion capture vixen", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Shawn Wolfe", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[12] = new MenuPage(160, 40, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Production Assistance", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Minerva Mayberry", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Nuts and bolts", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Steve Goldberg", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Marcus Hutchinson", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Bean Counting", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Max Yoshikawa", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Administrative Assistance", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Serafin Lewis", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[13] = new MenuPage(160, 60, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Location Manager. Louisiana", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Rick Skinner", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Location Scout. Louisiana", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Brian Benos", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Photographer", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Carlos Serrao", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[14] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Additional 3D modelling by", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "3 name 3D", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "viewpoint datalabs international", 0, 0, TextAlign.Center, 2,false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Audio Recorded at", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Pacific ocean pos. Santa Monica. C.A", 0, 0, TextAlign.Center, 2,false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Cement pond tracks recorded at", 0, 0, TextAlign.Center, 2,false);
				app.getFont(1).drawText(x, pos += 10, "Dreamstate recording. Burbank. C.A.", 0, 0, TextAlign.Center, 2,false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Recording engeneer", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Dave Ahlert", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[15] = new MenuPage(160, 70, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "3D Build Engine licensed from", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "3D Realms Intertainment", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Build Engine and related tools", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Created by Ken Silverman", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[16] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "For Interplay", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Lead tester", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Darrell Jones", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Testers", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Tim Anderson", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Erick Lujan", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Tien Tran", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[17] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Is techs", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Bill Delk", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Aaron Meyers", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Compatibility techs", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Marc Duran", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Dan Forsyth", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Derek Gibbs", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Aaron Olaiz", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Jack Parker", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[18] = new MenuPage(160, 60, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Director of compatibility", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Phuong Nguyen", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Assistant QA Director", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Colin Totman", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "QA Director", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Chad Allison", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[19] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Interplay Producer", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Bill Dugan", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Interplay Line Produces", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Chris Benson", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Product Manager", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Jim Veevaert", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Public Relations", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Erika Price", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[20] = new MenuPage(160, 50, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Special thanks", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Jim Gauger", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Paul Vais", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Scott Miller", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Todd Replogle", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Chuck Bueche", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Carter Lipscomb", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "John Conley", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Don Maggi", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[21] = new MenuPage(160, 80, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Extra special thanks", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Brian Fargo", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[22] = new MenuPage(160, 70, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Redneck Rampage", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "(C) 1997 Xatrix Entertainment, inc.", 0, 0, TextAlign.Center, 2, false);
				pos += 5;
				app.getFont(1).drawText(x, pos += 10, "Redneck Rampage is a Trademark of", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "Interplay productions", 0, 0, TextAlign.Center, 2, false);
			}
		};

		mPages[23] = new MenuPage(160, 80, -1) {
			@Override
			public void draw(MenuHandler handler) {
				int pos = y;
				app.getFont(1).drawText(x, pos += 10, "Redneck Rampage GDX", 0, 0, TextAlign.Center, 2, false);
				app.getFont(1).drawText(x, pos += 10, "(C) 2018 by [M210] (http://m210.duke4.net)", 0, 0, TextAlign.Center, 2, false);
			}
		};

		addItem(mPages[mPages.length - 1], true);
		mPages[mPages.length - 1].flags |= 10;
		for (int i = 0; i < mPages.length - 1; i++) {
			addItem(mPages[i], false);
			mPages[i].flags |= 10;
		}
	}
}
