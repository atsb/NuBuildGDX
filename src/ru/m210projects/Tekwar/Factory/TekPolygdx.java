package ru.m210projects.Tekwar.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.GdxRender.GDXRenderer;

public class TekPolygdx extends GDXRenderer {

	public TekPolygdx(Engine engine) {
		super(engine, new TekMapSettings());
	}
}
