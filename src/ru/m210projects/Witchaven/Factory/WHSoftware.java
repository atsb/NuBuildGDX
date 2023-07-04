package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Engine.radarang;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.viewingrange;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.xdimen;
import static ru.m210projects.Build.Engine.xdimscale;
import static ru.m210projects.Build.Engine.xyaspect;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Witchaven.Globals.lockclock;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Software.Software;

public class WHSoftware extends Software {

	public WHSoftware(Engine engine) {
		super(engine, new WHMapSettings());
	}

	@Override
	public void dosetaspect() {
		int i, j, k, x, xinc;

		if (xyaspect != oxyaspect) {
			oxyaspect = xyaspect;
			j = xyaspect * 320;
			lookups[horizlookup2 + horizycent - 1] = divscale(131072, j, 26);
			for (i = ydim * 4 - 1; i >= 0; i--)
				if (i != (horizycent - 1)) {
					lookups[i] = divscale(1, i - (horizycent - 1), 28);
					lookups[horizlookup2 + i] = divscale(klabs(lookups[i]), j, 14);
				}
		}
		if ((xdimen != oxdimen) || (viewingrange != oviewingrange)) {
			oxdimen = xdimen;
			oviewingrange = viewingrange;

			x = 0;
			xinc = xdimscale;

			for (i = 0; i < xdimen; i++) {
				j = x & 65535;
				k = x >> 16;
				x += xinc;
				if (k < 0 || k >= 1279)
					break;

				if (j != 0)
					j = mulscale(radarang[k + 1] - radarang[k], j, 16);
				radarang2[i] = (short) ((radarang[k] + j) >> 6);
			}

			for (i = 1; i < 65536; i++)
				distrecip[i] = divscale(xdimen, i, 20);
			nytooclose = xdimen * 2100;
			nytoofar = 65536 * 16384 - 1048576;
		}
	}

	public void TempHorizon(int horiz) {
		long l = scale(horiz - 100, xdim, 320) + (ydim >> 1);
		for (int y1 = 0, y2 = ydim - 1; y1 < y2; y1++, y2--) {
			int ptr = ylookup[y1];
			int ptr2 = ylookup[y2];
			int ptr3 = (int) (Math.min(klabs(y1 - l) >> 2, 31) << 8);
			int ptr4 = (int) (Math.min(klabs(y2 - l) >> 2, 31) << 8);

			int j = sintable[((y2 + lockclock) << 7) & 2047];
			j += sintable[((y2 - lockclock) << 8) & 2047];

			j >>= 14;
			ptr2 += j;

			try {
				byte[] frameplace = a.getframeplace();
				for (int x1 = 0; x1 < xdim - j; x1++) {
					byte ch = frameplace[ptr + x1];
					frameplace[ptr + x1] = palookup[0][(frameplace[ptr2 + x1] & 0xFF) + ptr3];
					frameplace[ptr2 + x1] = palookup[0][(ch & 0xFF) + ptr4];
				}
				a.setframeplace(frameplace);
			} finally {
			}
		}
	}
}
