package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Gameutils.SPR2_CHILDREN;
import static ru.m210projects.Wang.Gameutils.SPR2_VIS_SHADING;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG4;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Names.STAT_VIS_ON;
import static ru.m210projects.Wang.Palette.PALETTE_DIVE_LAVA;
import static ru.m210projects.Wang.Palette.PALETTE_FOG;
import static ru.m210projects.Wang.Player.NormalVisibility;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Sprites.KillSprite;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.PlayerStr;

public class Vis {

	private static final int VIS_VisCur(SPRITE sp) {
		return (SP_TAG2(sp));
	}

	private static final int VIS_VisDir(SPRITE sp) {
		return (SP_TAG3(sp));
	}

	private static final int VIS_VisGoal(SPRITE sp) {
		return (SP_TAG4(sp));
	}

	public static void ProcessVisOn() {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_VIS_ON]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (VIS_VisDir(sp) != 0) {
				// get brighter
				sp.lotag >>= 1;
				if (VIS_VisCur(sp) <= VIS_VisGoal(sp)) {
					sp.lotag = (short) VIS_VisGoal(sp);
					sp.clipdist ^= 1;
				}
			} else {
				// get darker
				sp.lotag <<= 1;
				sp.lotag += 1;

				if (VIS_VisCur(sp) >= NormalVisibility) {
					sp.lotag = NormalVisibility;
					if (sp.owner >= 0) {
						pUser[sp.owner].Flags2 &= ~(SPR2_VIS_SHADING);
					}
					KillSprite(i);
				}
			}
		}
	}

	public static void VisViewChange(PlayerStr pp, LONGp vis) {
		short i, nexti;
		SPRITE sp;
		short BrightestVis = NormalVisibility;
		int x, y, z;
		short sectnum;

		if (game.gPaused)
			return;

		// find the closest quake - should be a strength value
		for (i = headspritestat[STAT_VIS_ON]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.owner >= 0) {
				x = sprite[sp.owner].x;
				y = sprite[sp.owner].y;
				z = sprite[sp.owner].z;
				sectnum = sprite[sp.owner].sectnum;
			} else {
				x = sp.x;
				y = sp.y;
				z = sp.z;
				sectnum = sp.sectnum;
			}

			// save off the brightest vis that you can see
			if (FAFcansee(pp.posx, pp.posy, pp.posz, pp.cursectnum, x, y, z, sectnum)) {
				if (VIS_VisCur(sp) < BrightestVis)
					BrightestVis = (short) VIS_VisCur(sp);
			}
		}

		vis.value = BrightestVis;
	}

	public static int SpawnVis(int Parent, int sectnum, int x, int y, int z, int amt) {
		short SpriteNum;
		SPRITE sp;
		short i, nexti;

		if (Parent >= 0) {
			if (sector[sprite[Parent].sectnum].floorpal == PALETTE_FOG)
				return (-1);

			if (sector[sprite[Parent].sectnum].floorpal == PALETTE_DIVE_LAVA)
				return (-1);

			// kill any others with the same parent
			for (i = headspritestat[STAT_VIS_ON]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];
				if (sp.owner == Parent) {
					KillSprite(i);
				}
			}

			SpriteNum = (short) COVERinsertsprite(sprite[Parent].sectnum, STAT_VIS_ON);
			sp = sprite[SpriteNum];

			sp.owner = (short) Parent;

			pUser[Parent].Flags2 |= (SPR2_CHILDREN);

			sp.x = sprite[Parent].x;
			sp.y = sprite[Parent].y;
			sp.z = sprite[Parent].z;

			pUser[Parent].Flags2 |= (SPR2_VIS_SHADING);
		} else {
			if (sector[sectnum].floorpal == PALETTE_FOG)
				return (-1);

			SpriteNum = (short) COVERinsertsprite((short)sectnum, STAT_VIS_ON);
			sp = sprite[SpriteNum];

			sp.x = x;
			sp.y = y;
			sp.z = z - Z(20);
			sp.owner = -1;
		}

		sp.cstat = 0;
		sp.extra = 0;

		sp.clipdist = 1;
		sp.lotag = NormalVisibility;
		sp.ang = (short) amt;

		return (SpriteNum);
	}
}
