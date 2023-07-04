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

public class MaskedWallInst extends SeqInst {

	@Override
	protected void updateInstance(int index) {
		if(!(index > 0 && index < kMaxXWalls))game.dassert("nXWall > 0 && nXWall < kMaxXWalls");
		int nWall = xwall[index].reference;
		if(!(nWall >= 0 && nWall < kMaxWalls)) game.dassert("nWall >= 0 && nWall < kMaxWalls");
		WALL pWall = wall[nWall];
		if(pWall.extra != index)
			game.dassert("pWall.extra == nXWall");
		if(pWall.nextwall < 0)
			game.dassert("pWall.nextwall >= 0");		// it must be a 2 sided wall
		WALL pWall2 = wall[pWall.nextwall];

		SeqFrame pFrame = pSequence.getFrame(frameIndex);
		
		pWall.overpicnum = pWall2.overpicnum = (short)pFrame.nTile;

		if(pFrame.pal != 0)
			pWall.pal = pWall2.pal = (short) pFrame.pal;

		if ( pFrame.translucent )
		{
			pWall.cstat |= kWallTranslucent;
			pWall2.cstat |= kWallTranslucent;
		}
		else
		{
			pWall.cstat &= ~kWallTranslucent;
			pWall2.cstat &= ~kWallTranslucent;
		}

		if ( pFrame.translucentR )
		{
			pWall.cstat |= kWallTranslucentR;
			pWall2.cstat |= kWallTranslucentR;
		}
		else
		{
			pWall.cstat &= ~kWallTranslucentR;
			pWall2.cstat &= ~kWallTranslucentR;
		}

		if ( pFrame.blocking )
		{
			pWall.cstat |= kWallBlocking;
			pWall2.cstat |= kWallBlocking;
		}
		else
		{
			pWall.cstat &= ~kWallBlocking;
			pWall2.cstat &= ~kWallBlocking;
		}

		if ( pFrame.hitscan )
		{
			pWall.cstat |= kWallHitscan;
			pWall2.cstat |= kWallHitscan;
		}
		else
		{
			pWall.cstat &= ~kWallHitscan;
			pWall2.cstat &= ~kWallHitscan;
		}
	}	
}
