package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.DB.kMaxXWalls;
import static ru.m210projects.Blood.DB.xwall;
import static ru.m210projects.Blood.Globals.kMaxWalls;
import static ru.m210projects.Blood.Globals.kWallBlocking;
import static ru.m210projects.Blood.Globals.kWallHitscan;
import static ru.m210projects.Blood.Globals.kWallTranslucent;
import static ru.m210projects.Blood.Globals.kWallTranslucentR;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Build.Engine.wall;

import ru.m210projects.Build.Types.WALL;

public class WallInst extends SeqInst {
	@Override
	protected void updateInstance(int index) {
		if(!(index > 0 && index < kMaxXWalls))
			game.dassert("nXWall > 0 && nXWall < kMaxXWalls");
		int nWall = xwall[index].reference;
		if(!(nWall >= 0 && nWall < kMaxWalls)) 
			game.dassert("nWall >= 0 && nWall < kMaxWalls");
		
		WALL pWall = wall[nWall];
		if(pWall.extra != index)
			game.dassert("pWall.extra == nXWall");
		
		SeqFrame pFrame = pSequence.getFrame(frameIndex);

		pWall.picnum = (short)pFrame.nTile;
		if(pFrame.pal != 0)
			pWall.pal = (short)pFrame.pal;

		if ( pFrame.translucent )
			pWall.cstat |= kWallTranslucent;
		else
			pWall.cstat &= ~kWallTranslucent;

		if ( pFrame.translucentR )
			pWall.cstat |= kWallTranslucentR;
		else
			pWall.cstat &= ~kWallTranslucentR;

		if ( pFrame.blocking )
			pWall.cstat |= kWallBlocking;
		else
			pWall.cstat &= ~kWallBlocking;

		if ( pFrame.hitscan )
			pWall.cstat |= kWallHitscan;
		else
			pWall.cstat &= ~kWallHitscan;
	}
}
