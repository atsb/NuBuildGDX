package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Witchaven.WHScreen.drawfloormirror;

import com.badlogic.gdx.graphics.Color;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Polymost.Polymost;

public class WHPolymost extends Polymost {

//	@Override
//	public void drawrooms()
//	{
//		globalvisibility = scale(visibility<<2, xdimen, 2200);
//		super.drawrooms();
//	}

	public WHPolymost(Engine engine) {
		super(engine, new WHMapSettings());
		globalfog.setFogScale(64);
	}

	@Override
	public Color getshadefactor(int shade, int method) {
		if (drawfloormirror) {
			switch (rendering) {
			case Sprite:
				shade = 20;
				break;
			case Wall:
			case MaskWall:
				int wal = rendering.getIndex();

				int dist = klabs(wall[wal].x - globalposx);
				dist += klabs(wall[wal].y - globalposy);

				shade = BClipLow(dist >> 7, 20);

				break;
			default:
				shade = 30;
				break;
			}
		}
		return super.getshadefactor(shade, method);
	}
}
