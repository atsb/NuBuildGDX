package ru.m210projects.Build.Settings;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;

import ru.m210projects.Build.Types.BuildVariable;

public class BuildSettings {
	
	public static BuildVariable<Boolean> usenewaspect;
	public static BuildVariable<Boolean> useVoxels;
	public static BuildVariable<Integer> fpsLimit;
	public static BuildVariable<Integer> fov;
	public static BuildVariable<Boolean> vsync;
	public static BuildVariable<Integer> paletteGamma;
	
	public static final Integer[] fpslimits = { 0, 30, 60, 120, 144, 240, 320, 480};
	
	public static void init(final Engine engine, final BuildConfig cfg)
	{
		usenewaspect = new BooleanVar(false, "Use widescreen") {
			@Override
			public void execute(Boolean value) {
				engine.setaspect_new();
				cfg.widescreen = value ? 1: 0;
			}
		};
		
		useVoxels = new BooleanVar(true, "Use voxels");
		
		paletteGamma = new IntVar(0, "Gamma") {
			@Override
			public void execute(Integer value) { 
				cfg.paletteGamma = value;
				engine.setbrightness(cfg.paletteGamma, Engine.palette, true);
			}
		};
		
		fpsLimit = new IntVar(0, "Frames per second limit") {
			@Override
			public void execute(Integer value) { 
				cfg.fpslimit = value;
				BuildGdx.graphics.setFramesPerSecond(cfg.fpslimit);
			}
			
			@Override
			public Integer check(Object value) {
				if(value instanceof Integer) {
					int fps = (Integer) value;
					for(int i = 0; i < fpslimits.length; i++)
						if(fps == fpslimits[i])
							return fps;
				}
				return null;
			}
		};

		fov = new IntVar(90, "Field of view") {
			@Override
			public void execute(Integer value) { 
				cfg.gFov = value;
				engine.setFov(cfg.gFov);
			}
		};
		
		vsync = new BooleanVar(cfg.gVSync, "Use vertical synchronization") {
			@Override
			public void execute(Boolean value) {
				cfg.gVSync = value;
			}
			
			@Override
			public Boolean check(Object value) {
				if(value instanceof Boolean) {
					boolean vs = (Boolean) value;
					try { // crash if hires textures loaded
						BuildGdx.graphics.setVSync(vs);
						return vs;
					} catch (Exception e) { e.printStackTrace(); }
				}
				return null;
			}
		};
		
		
	}

	public static class BooleanVar extends BuildVariable<Boolean> {
		public BooleanVar(Boolean set, String description) {
			super(set, description);
		}

		@Override
		public void execute(Boolean value) { }
		
		@Override
		public Boolean check(Object value) {
			if(value instanceof Boolean)
				return (Boolean) value;
			return null;
		}
	}
	
	public static class IntVar extends BuildVariable<Integer> {
		public IntVar(Integer set, String description) {
			super(set, description);
		}

		@Override
		public void execute(Integer value) { }
		
		@Override
		public Integer check(Object value) {
			if(value instanceof Integer)
				return (Integer) value;
			return null;
		}
	}
}
