package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.DB.kMaxXSectors;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.Globals.kMaxSectors;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Build.Engine.sector;

import ru.m210projects.Build.Types.SECTOR;

public class CeilingInst extends SeqInst {
	@Override
	protected void updateInstance(int index) {
		if(!(index > 0 && index < kMaxXSectors))
			game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
		int nSector = xsector[index].reference;
		if(!(nSector >= 0 && nSector < kMaxSectors)) 
			game.dassert("nSector >= 0 && nSector < kMaxSectors");
		SECTOR pSector = sector[nSector];
		if(pSector.extra != index)
			game.dassert("pSector.extra == nXSector");

		SeqFrame pFrame = pSequence.getFrame(frameIndex);
		
		pSector.ceilingpicnum = (short)pFrame.nTile;
		pSector.ceilingshade = pFrame.shade;
		pSector.ceilingpal = (short)pFrame.pal;
	}
}