package ru.m210projects.Build.Render;

public interface IOverheadMapSettings {

	enum MapView {
		Polygons, Lines
	}

    boolean isFullMap();

	boolean isScrollMode();

	int getViewPlayer();

	boolean isShowSprites(MapView view);

	boolean isShowFloorSprites();

	boolean isShowRedWalls();

	boolean isShowAllPlayers();

	boolean isSpriteVisible(MapView view, int index);

	boolean isWallVisible(int w, int ses);

	int getWallColor(int w, int sec);

	int getWallX(int w);

	int getWallY(int w);

	int getSpriteColor(int s);

	int getSpriteX(int spr);

	int getSpriteY(int spr);

	int getSpritePicnum(int spr);

	int getPlayerSprite(int player);

	int getPlayerPicnum(int player);

	int getPlayerZoom(int player, int czoom);

}
