package ru.m210projects.Build.Settings;

import static ru.m210projects.Build.Engine.pow2long;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;

import com.badlogic.gdx.graphics.Texture.TextureFilter;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Render.GLInfo;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Render.Types.GLFilter;
import ru.m210projects.Build.Types.BuildVariable;
import ru.m210projects.Build.Types.BuildVariable.RespondType;

public class GLSettings extends BuildSettings {

	public static GLFilter[] glfiltermodes = {
			new GLFilter("None", TextureFilter.Nearest, TextureFilter.Nearest), // 0
			new GLFilter("Bilinear", TextureFilter.Linear, TextureFilter.Linear), // 1
			new GLFilter("Trilinear", TextureFilter.MipMapLinearLinear, TextureFilter.Linear) // 2
	};

	public static BuildVariable<GLFilter> textureFilter;
	public static BuildVariable<Integer> textureAnisotropy;
	public static BuildVariable<Boolean> useHighTile;
	public static BuildVariable<Boolean> useModels;
	public static BuildVariable<Boolean> usePaletteShader;

	public static BuildVariable<Integer> gamma;
//	public static BuildVariable<Integer> brightness;
//	public static BuildVariable<Integer> contrast;

	public static BuildVariable<Boolean> animSmoothing;

	public static void init(final Engine engine, final BuildConfig cfg) {
		textureFilter = new BuildVariable<GLFilter>(
				cfg.glfilter < glfiltermodes.length ? glfiltermodes[cfg.glfilter] : glfiltermodes[0],
				"Changes the texture filtering settings") {
			@Override
			public void execute(GLFilter value) {
				BuildGdx.app.postRunnable(new Runnable() { // it must be started at GLthread
					@Override
					public void run() {
						GLRenderer gl = engine.glrender();
						if (gl != null)
							gl.gltexapplyprops();
					}
				});

				for (int i = 0; i < glfiltermodes.length; i++)
					if (value.equals(glfiltermodes[i])) {
						cfg.glfilter = i;
						break;
					}
			}

			@Override
			public GLFilter check(Object value) {
				if (value instanceof GLFilter)
					return (GLFilter) value;
				return null;
			}
		};

		textureAnisotropy = new BuildVariable<Integer>(1, "Changes the texture anisotropy settings") {
			@Override
			public void execute(final Integer value) {
				BuildGdx.app.postRunnable(new Runnable() { // it must be started at GLthread
					@Override
					public void run() {
						GLRenderer gl = engine.glrender();
						if (gl != null)
							gl.gltexapplyprops();
						cfg.glanisotropy = value;
					}
				});
			}

			@Override
			public Integer check(Object value) {
				if (value instanceof Integer) {
					int anisotropy = (Integer) value;
					if (GLInfo.maxanisotropy > 1.0) {
						if (anisotropy <= 0 || anisotropy > GLInfo.maxanisotropy)
							anisotropy = (int) GLInfo.maxanisotropy;
					}
					return pow2long[checkAnisotropy(anisotropy)];
				}
				return null;
			}

			int checkAnisotropy(int anisotropy) {
				int anisotropysize = 0;
				for (int s = anisotropy; s > 1; s >>= 1)
					anisotropysize++;
				return anisotropysize;
			}
		};
		textureAnisotropy.set(cfg.glanisotropy);

		OSDCOMMAND R_texture = new OSDCOMMAND("r_texturemode",
				"r_texturemode: " + GLSettings.textureFilter.getDescription(), new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (Console.osd_argc != 2) {
							Console.Println("Current texturing mode is " + GLSettings.textureFilter.get().name);
							return;
						}
						try {
							int value = Integer.parseInt(osd_argv[1]);
							if (GLSettings.textureFilter.set(glfiltermodes[value]) == RespondType.Success)
								Console.Println(
										"Texture filtering mode changed to " + GLSettings.textureFilter.get().name);
							else
								Console.Println("Texture filtering mode out of range");
						} catch (Exception e) {
							Console.Println("r_texturemode: Out of range");
						}
					}
				});
		R_texture.setRange(0, 2);
		Console.RegisterCvar(R_texture);

		Console.RegisterCvar(new OSDCOMMAND("r_detailmapping", "r_detailmapping: use detail textures", 1, 0, 1));
		Console.RegisterCvar(new OSDCOMMAND("r_glowmapping", "r_glowmapping: use glow textures", 1, 0, 1));

		useHighTile = new BooleanVar(true, "Use true color textures from high resolution pack") {
			@Override
			public void execute(Boolean value) {
				BuildGdx.app.postRunnable(new Runnable() { // it must be started at GLthread
					@Override
					public void run() {
						GLRenderer gl = engine.glrender();
						if (gl != null)
							gl.gltexinvalidateall(GLInvalidateFlag.All, GLInvalidateFlag.Uninit);
					}
				});
			}
		};
		useModels = new BooleanVar(true, "Use md2 / md3 models from high resolution pack");

		usePaletteShader = new BooleanVar(true, "Use palette emulation") {
			@Override
			public void execute(Boolean value) {
				GLRenderer gl = engine.glrender();
				if (gl != null)
					gl.enableIndexedShader(value);
				cfg.paletteEmulation = value;
			}
		};
		usePaletteShader.set(cfg.paletteEmulation);

		OSDCOMMAND r_paletteshader = new OSDCOMMAND("r_paletteshader",
				"r_paletteshader: " + GLSettings.usePaletteShader.get(), new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (Console.osd_argc != 2) {
							Console.Println("r_paletteshader: " + GLSettings.usePaletteShader.get());
							return;
						}
						try {
							final int value = Integer.parseInt(osd_argv[1]);
							BuildGdx.app.postRunnable(new Runnable() { // it must be started at GLthread
								@Override
								public void run() {
									usePaletteShader.set(value == 1);
									Console.Println("r_paletteshader changed to " + GLSettings.usePaletteShader.get());
								}
							});
						} catch (Exception e) {
							Console.Println("r_paletteshader: out of range");
						}
					}
				});
		r_paletteshader.setRange(0, 1);
		Console.RegisterCvar(r_paletteshader);

		animSmoothing = new BooleanVar(true, "Use  model animation smoothing");

		gamma = new BuildVariable<Integer>((int) ((1 - cfg.fgamma) * 4096), "Global gamma") {
			@Override
			protected void execute(Integer value) {
				cfg.fgamma = (1 - (value / 4096.0f));
			}

			@Override
			protected Integer check(Object value) {
				if (value instanceof Integer) {
					try {
						float gamma = (Integer) value / 4096.0f;
						if (engine.glrender() == null || (Boolean) BuildGdx.graphics.extra(Option.GLSetConfiguration,
								//1 - gamma, cfg.fbrightness, cfg.fcontrast))
								1 - gamma, 0.0f, 1.0f))
							return (Integer) value;
					} catch(Throwable ignored) {
					}
				}
				return null;
			}
		};

//		brightness = new BuildVariable<Integer>((int) (cfg.fbrightness * 4096), "Global brightness") {
//			@Override
//			protected void execute(Integer value) {
//				cfg.fbrightness = value / 4096.0f;
//			}
//
//			@Override
//			protected Integer check(Object value) {
//				if (value instanceof Integer) {
//					float brightness = (Integer) value / 4096.0f;
//					if (engine.glrender() == null || (Boolean) BuildGdx.graphics.extra(Option.GLSetConfiguration,
//							cfg.fgamma, brightness, cfg.fcontrast))
//						return (Integer) value;
//				}
//				return null;
//			}
//		};
//
//		contrast = new BuildVariable<Integer>((int) (cfg.fcontrast * 4096), "Global contrast") {
//			@Override
//			protected void execute(Integer value) {
//				cfg.fcontrast = value / 4096.0f;
//			}
//
//			@Override
//			protected Integer check(Object value) {
//				if (value instanceof Integer) {
//					float contrast = (Integer) value / 4096.0f;
//					if (engine.glrender() == null || (Boolean) BuildGdx.graphics.extra(Option.GLSetConfiguration,
//							cfg.fgamma, cfg.fbrightness, contrast))
//						return (Integer) value;
//				}
//				return null;
//			}
//		};
	}

}
