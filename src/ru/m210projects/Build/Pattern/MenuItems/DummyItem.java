package ru.m210projects.Build.Pattern.MenuItems;

import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class DummyItem extends MenuItem {

	public DummyItem() {
		super(null, null);
	}

	@Override
	public void draw(MenuHandler handler) {
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		return false;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		return false;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}
}
