package ru.m210projects.Wang.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Software.Software;

public class WangSoftware extends Software {

	public WangSoftware(Engine engine) {
		super(engine, new WangMapSettings());
	}
}
