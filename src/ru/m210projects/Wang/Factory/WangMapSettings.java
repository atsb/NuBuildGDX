package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Wang.Draw.ScrollMode2D;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Palette.LT_GREY;
import static ru.m210projects.Wang.Palette.RED;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.DefaultMapSettings;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;

public class WangMapSettings extends DefaultMapSettings {

	@Override
	public int getWallColor(int w, int s) {
		WALL wal = wall[w];
		if (Gameutils.isValidSector(wal.nextsector)) // red wall
			return RED + 6;
		return LT_GREY + 2; // white wall
	}

	@Override
	public boolean isShowRedWalls() {
		return true;
	}

	@Override
	public boolean isSpriteVisible(MapView view, int index) {
		return false;
	}

	@Override
	public int getPlayerSprite(int player) {
		return Player[player].PlayerSprite;
	}

	@Override
	public boolean isShowAllPlayers() {
		return gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT;
	}

	@Override
	public boolean isFullMap() {
		return false;
	}

	@Override
	public boolean isScrollMode() {
		return ScrollMode2D;
	}

	@Override
	public int getViewPlayer() {
		return screenpeek;
	}
}
