package ru.m210projects.Build.Pattern.CommonMenus;

import static ru.m210projects.Build.Engine.pow2long;
import static ru.m210projects.Build.Render.GLInfo.maxanisotropy;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenuList;
import ru.m210projects.Build.Pattern.MenuItems.DummyItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Settings.GLSettings;
import ru.m210projects.Build.Types.BuildVariable;
import ru.m210projects.Build.Types.BuildVariable.RespondType;

public abstract class MenuRendererSettings extends BuildMenuList {

	private final MenuItem title;
	private Renderer currentRenderer;
	private PixelFormat currentFormat;
	public BuildGame app;

	public BuildFont style;
	public int width;
	public boolean fontShadow = false;
	public boolean listShadow = false;

	protected BuildMenuList GLHiresMenu;
	protected boolean RenderParamBuilt = false, GLRenderParamBuilt = false;
	protected MenuSlider palettedGamma;
	protected MenuSlider fovSlider;
	protected MenuSwitch vSync;
	protected MenuSwitch useVoxels;
	protected DummyItem separator;
	protected MenuSlider GLGamma;
protected MenuConteiner GLTextureFilter;
	protected MenuConteiner GLTextureAnisotropy;
	protected MenuSwitch GLUseHighTile;
	protected MenuSwitch GLUseModels;
	protected MenuButton GLHires;
	protected MenuSwitch GLPalette;

	@Override
	public abstract MenuTitle getTitle(BuildGame app, String text);

	public MenuRendererSettings(final BuildGame app, int x, int y, int width, int step, BuildFont style) {
		super(app, "Renderer settings", x, y, width, step, 15);

		this.app = app;
		this.style = style;
		this.width = width;

		title = m_pItems[0];
	}

	@Override
	public void mDraw(MenuHandler handler) {
		if (currentFormat != app.pEngine.getrender().getTexFormat())
			rebuild();
		super.mDraw(handler);
	}

	@Override
	public boolean mLoadRes(MenuHandler handler, MenuOpt opt) {
		if (opt == MenuOpt.Open && (currentRenderer != app.pEngine.getrender()
				|| currentFormat != app.pEngine.getrender().getTexFormat()))
			rebuild();
		return super.mLoadRes(handler, opt);
	}

	protected void rebuild() {
		this.clear();
		currentRenderer = app.pEngine.getrender();
		currentFormat = app.pEngine.getrender().getTexFormat();
		if (title != null)
			title.text = (app.pEngine.getrender().getType().getName() + " settings").toCharArray();

		BuildRenderParameters();
		if (currentRenderer instanceof GLRenderer) {
			BuildGLRenderParameters();
//			if (currentFormat == PixelFormat.Rgb) {
				this.addItem(GLGamma, true);
//				this.addItem(GLBrightness, false);
//				this.addItem(GLContrast, false);
//				this.addItem(GLReset, false);
//			} else
//				this.addItem(palettedGamma, true);

			this.addItem(separator, false);

			this.addItem(fovSlider, false);
			this.addItem(vSync, false);
            this.addItem(useVoxels, false);

            this.addItem(separator, false);
			this.addItem(GLPalette, false);
			this.addItem(GLHires, false);
		} else {
			this.addItem(palettedGamma, true);

			this.addItem(separator, false);
			this.addItem(fovSlider, false);
			this.addItem(vSync, false);
			this.addItem(useVoxels, false);
		}
	}

	protected MenuConteiner BuildConteiner(String text, final BuildVariable<?> var, String[] names,
			final Object[] values) {
		MenuConteiner conteiner = new MenuConteiner(text, style, 0, 0, width, names, 0, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				var.set(values[item.num]);
			}
		}) {
			@Override
			public void draw(MenuHandler handler) {
				this.num = -1;
				for (int i = 0; i < values.length; i++)
					if (var.get().equals(values[i]))
						this.num = i;

				super.draw(handler);
			}
		};
		conteiner.fontShadow = fontShadow;
		conteiner.listShadow = listShadow;
		return conteiner;
	}

	protected MenuSlider BuildSlider(String text, final BuildVariable<Integer> var, int min, int max, int step,
			Integer digmax) {
		MenuSlider slider = new MenuSlider(app.pSlider, text, style, 0, 0, width, 0, min, max, step, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSlider slider = (MenuSlider) pItem;
				var.set(slider.value);
			}
		}, true) {

			@Override
			public void draw(MenuHandler handler) {
				this.value = var.get();
				super.draw(handler);
			}
		};
		slider.fontShadow = fontShadow;
		if (digmax != null)
			slider.digitalMax = digmax;

		return slider;
	}

	protected MenuSwitch BuildSwitch(String text, final BuildVariable<Boolean> var) {
		MenuSwitch sw = new MenuSwitch(text, style, 0, 0, width, false, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch ss = (MenuSwitch) pItem;
				var.set(ss.value);
			}
		}, null, null) {
			@Override
			public void draw(MenuHandler handler) {
				this.value = var.get();
				super.draw(handler);
			}
		};

		sw.fontShadow = fontShadow;
		return sw;
	}

	protected MenuButton BuildButton(String text, MenuProc callback) {
		MenuButton sw = new MenuButton(text, style, 0, 0, width, 0, 0, null, 0, callback, -1);
		sw.fontShadow = fontShadow;
		return sw;
	}

	protected void BuildGLRenderParameters() {
		if (GLRenderParamBuilt)
			return;

		GLGamma = BuildSlider("Gamma", GLSettings.gamma, 0, 4096, 64, 4096);
		int ogamma = GLSettings.gamma.get();
		if (GLSettings.gamma.set(64) == RespondType.Fail)
			GLGamma.mCheckEnableItem(false);
		else
			GLSettings.gamma.set(ogamma);

        GLHires = BuildButton("Hires settings", new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				handler.mOpen(GLHiresMenu, -1);
			}
		});

		GLPalette = BuildSwitch("Palette emulation", GLSettings.usePaletteShader);

		{ // Hires menu
			GLHiresMenu = new BuildMenuList(app, "Hires settings", this.list.x, this.list.y, width,
					this.list.mFontOffset(), 10) {
				@Override
				public MenuTitle getTitle(BuildGame app, String text) {
					return MenuRendererSettings.this.getTitle(app, text);
				}
			};

			String[] filters = new String[GLSettings.glfiltermodes.length];
			for (int i = 0; i < filters.length; i++)
				filters[i] = GLSettings.glfiltermodes[i].name;
			GLTextureFilter = BuildConteiner("Texture filtering", GLSettings.textureFilter, filters,
					GLSettings.glfiltermodes);

			int anisotropysize = 0;
			for (int s = (int) maxanisotropy; s > 1; s >>= 1)
				anisotropysize++;
			Integer[] list = new Integer[anisotropysize + 1];
			String[] anisotropies = new String[anisotropysize + 1];
			for (int i = 0; i < list.length; i++) {
				list[i] = pow2long[i];
				anisotropies[i] = i == 0 ? "None" : list[i] + "x";
			}
			GLTextureAnisotropy = BuildConteiner("Anisotropy", GLSettings.textureAnisotropy, anisotropies, list);
			GLUseHighTile = BuildSwitch("True color textures", GLSettings.useHighTile);
			GLUseModels = BuildSwitch("3d models", GLSettings.useModels);

			GLHiresMenu.addItem(GLTextureFilter, true);
			GLHiresMenu.addItem(GLTextureAnisotropy, false);
			GLHiresMenu.addItem(GLUseHighTile, false);
			GLHiresMenu.addItem(GLUseModels, false);
		}

		GLRenderParamBuilt = true;
	}

	protected void BuildRenderParameters() {
		if (RenderParamBuilt)
			return;

		separator = new DummyItem();
		palettedGamma = BuildSlider("Gamma", BuildSettings.paletteGamma, 0, 15, 1, null);
		fovSlider = BuildSlider("Field of view", BuildSettings.fov, BuildConfig.MINFOV, BuildConfig.MAXFOV, 5, null);
		vSync = BuildSwitch("Vsync", BuildSettings.vsync);
		String[] limits = new String[BuildSettings.fpslimits.length];
		for (int i = 0; i < limits.length; i++)
			limits[i] = i == 0 ? "None" : BuildSettings.fpslimits[i] + " fps";
		useVoxels = BuildSwitch("Voxels", BuildSettings.useVoxels);

		RenderParamBuilt = true;
	}

}
