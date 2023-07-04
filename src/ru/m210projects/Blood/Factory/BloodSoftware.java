package ru.m210projects.Blood.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Software.Software;

public class BloodSoftware extends Software {

	public BloodSoftware(Engine engine) {
		super(engine, new BloodMapSettings());
	}
}
