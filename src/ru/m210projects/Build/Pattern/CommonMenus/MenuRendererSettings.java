package ru.m210projects.Build.Pattern.CommonMenus;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenuList;
import ru.m210projects.Build.Pattern.MenuItems.DummyItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.BuildVariable;

public abstract class MenuRendererSettings extends BuildMenuList {

	private final MenuItem title;
	private Renderer currentRenderer;
	private PixelFormat currentFormat;
	public BuildGame app;

	public BuildFont style;
	public int width;
	public boolean fontShadow = false;
	public boolean listShadow = false;

	protected boolean RenderParamBuilt = false, GLRenderParamBuilt = false;
	protected MenuSlider palettedGamma;
	protected MenuSlider fovSlider;
	protected MenuSwitch vSync;
	protected MenuSwitch useVoxels;
	protected DummyItem separator;

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

		this.addItem(palettedGamma, true);
		this.addItem(separator, false);
		this.addItem(fovSlider, false);
		this.addItem(vSync, false);
		this.addItem(useVoxels, false);

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
