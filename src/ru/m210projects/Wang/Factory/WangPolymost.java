package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.palookupfog;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Gameutils.SectorIsUnderwaterArea;
import static ru.m210projects.Wang.Palette.PALETTE_DIVE;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Polymost.Polymost;

public class WangPolymost extends Polymost {

	public WangPolymost(Engine engine) {
		super(engine, new WangMapSettings());
	}

	@Override
	protected void calc_and_apply_fog(int shade, int vis, int pal) {
		if (globalpal == PALETTE_DIVE || pal == PALETTE_DIVE) {
			palookupfog[PALETTE_DIVE][0] = 0;
			palookupfog[PALETTE_DIVE][1] = 0;
			if (SectorIsUnderwaterArea(Player[screenpeek].cursectnum))
				palookupfog[PALETTE_DIVE][2] = 15;
			else
				palookupfog[PALETTE_DIVE][2] = 0;
		}

		super.calc_and_apply_fog(shade, vis, pal);
	}
}
