package ru.m210projects.Blood.Factory;

import static ru.m210projects.Blood.Mirror.MIRROR;
import static ru.m210projects.Blood.Mirror.MIRRORLABEL;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.GdxRender.GDXRenderer;

public class BloodPolygdx extends GDXRenderer {

	public BloodPolygdx(Engine engine) {
		super(engine, new BloodMapSettings());
	}

	@Override
	protected int[] getMirrorTextures() {
		return new int[] { MIRROR, MIRRORLABEL, MIRRORLABEL + 1, MIRRORLABEL + 2, MIRRORLABEL + 3, MIRRORLABEL + 4,
				MIRRORLABEL + 5, MIRRORLABEL + 6, MIRRORLABEL + 7, MIRRORLABEL + 8, MIRRORLABEL + 9, MIRRORLABEL + 10,
				MIRRORLABEL + 11, MIRRORLABEL + 12, MIRRORLABEL + 13, MIRRORLABEL + 14, MIRRORLABEL + 15, };
	}
}
