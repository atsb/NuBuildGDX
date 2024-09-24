package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Tekwar.Globals.ST_IMMEDIATE;
import static ru.m210projects.Tekwar.Main.gCreditsScreen;
import static ru.m210projects.Tekwar.Names.BACKGROUND;
import static ru.m210projects.Tekwar.Names.S_BOOP;
import static ru.m210projects.Tekwar.Names.S_PICKUP_BONUS;
import static ru.m210projects.Tekwar.Teksnd.playsound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.BuildSmacker.SMKFile;
import ru.m210projects.BuildSmacker.SMKFile.Track;
import ru.m210projects.Tekwar.Main;

public abstract class SmkMenu extends ScreenAdapter {

	private final int kSMKTile = MAXTILES - 3;
	private SMKFile smkfil;

	protected int currmission;
	protected MessageType message = MessageType.NONE;
	private Runnable chooseCallback;
	private float delayclock, helpclock;
	private boolean rebuild;
	protected Main game;

	protected enum MessageType {
		NONE(-1), USER(0), EXIT(1), ACCESS(2), HELP(3);

		private int num;

		MessageType(int num) {
			this.num = num;
		}

		public int get() {
			return num;
		}

		public void set(int num) {
			this.num = num;
		}
	}

	public SmkMenu(Main game) {
		this.game = game;
	}

	public abstract int skip();

	public abstract void keyUp();

	public abstract void keyDown();

	public abstract void keyLeft();

	public abstract void keyRight();

	public abstract int loadGame();

	public abstract String init();

	public abstract void rebuildFrame();

	public abstract boolean mouseHandler(int x, int y);

	public SmkMenu setCallback(Runnable chooseCallback) {
		this.chooseCallback = chooseCallback;
		return this;
	}

	@Override
	public synchronized void show() {
		rebuild = true;
		delayclock = 0;
		helpclock = 0;
		message = MessageType.NONE;

		BuildGdx.input.setCursorCatched(true);
		game.pInput.ctrlResetKeyStatus();
		init(init());
	}

	@Override
	public void hide() {
		if (smkfil == null)
			return;

		BuildGdx.input.setCursorCatched(true);
		game.pEngine.getTile(kSMKTile).data = null;
		smkfil = null;
	}

	@Override
	public synchronized void render(float delta) {
		if (smkfil == null)
			return;

		if (rebuild || mouseHandler(BuildGdx.input.getX(), BuildGdx.input.getY())) {
			rebuildFrame();

			Tile pic = game.pEngine.getTile(kSMKTile);
			pic.data = smkfil.getVideoBuffer().array();
			game.pEngine.getrender().invalidatetile(kSMKTile, 0, -1);

			rebuild = false;
		}

		DrawMenu();
		keyhandler();
	}

	private int keyhandler() {

		int mission = skip();
		if (mission != -1)
			return mission;

		if (message == MessageType.EXIT) {
			if (game.pInput.ctrlKeyStatusOnce(Keys.Y))
				game.changeScreen(gCreditsScreen);

			if (game.pInput.ctrlKeyStatusOnce(Keys.N) || game.pInput.ctrlKeyStatusOnce(Keys.ESCAPE))
				resetStatus();

			return -1;
		}

		if (delayclock > 0) { // can't access to matrix
			delayclock = BClipLow(delayclock - BuildGdx.graphics.getDeltaTime(), 0);
			if (delayclock == 0)
				resetStatus();

			return -1;
		}

		helpclock += BuildGdx.graphics.getDeltaTime();
		if (helpclock > 8 && message != MessageType.HELP) // Help state
			HelpMessage();

		if (game.pInput.ctrlKeyStatusOnce(ANYKEY)) {
			if (game.pInput.ctrlKeyStatusOnce(Keys.H))
				HelpMessage();
			else if (game.pInput.ctrlKeyStatusOnce(Keys.ESCAPE)) {
				if (message == MessageType.HELP)
					resetStatus();
				else
					ExitMessage();
			} else if (game.pInput.ctrlKeyStatusOnce(Keys.LEFT)) {
				message = MessageType.NONE;
				keyLeft();
				helpclock = 0;
				rebuild = true;
			} else if (game.pInput.ctrlKeyStatusOnce(Keys.RIGHT)) {
				message = MessageType.NONE;
				keyRight();
				helpclock = 0;
				rebuild = true;
			} else if (game.pInput.ctrlKeyStatusOnce(Keys.UP)) {
				message = MessageType.NONE;
				keyUp();
				helpclock = 0;
				rebuild = true;
			} else if (game.pInput.ctrlKeyStatusOnce(Keys.DOWN)) {
				message = MessageType.NONE;
				keyDown();
				helpclock = 0;
				rebuild = true;
			} else if (game.pInput.ctrlKeyStatusOnce(Keys.L)) {
				loadGame();
				if (chooseCallback != null)
					chooseCallback.run();

				return 8;
			} else if (game.pInput.ctrlKeyStatusOnce(Keys.ENTER) || game.pInput.ctrlKeyStatusOnce(Keys.SPACE)) {
				helpclock = 0;
				rebuild = true;

				if (chooseCallback != null)
					chooseCallback.run();

				return currmission;
			}
		}

		return -1;
	}

	protected void DrawFrame(int fn) {
		if (smkfil != null) {
			if (fn < smkfil.getFrames()) {
				smkfil.setFrame(fn - 1);
			}
		}
	}

	protected void resetStatus() {
		helpclock = 0;
		message = MessageType.NONE;
		rebuild = true;
	}

	protected void AccessWarning() {
		playsound(S_BOOP, 0, 0, 0, ST_IMMEDIATE);
		message = MessageType.ACCESS;
		delayclock = 1;
		rebuild = true;
	}

	protected void HelpMessage() {
		message = MessageType.HELP;
		playsound(S_PICKUP_BONUS, 0, 0, 0, ST_IMMEDIATE);
		rebuild = true;
	}

	protected void ExitMessage() {
		message = MessageType.EXIT;
		playsound(S_PICKUP_BONUS, 0, 0, 0, ST_IMMEDIATE);
		rebuild = true;
	}

	private void init(String name) {
		byte[] smkbuf = BuildGdx.cache.getBytes(name, 0);
		if (smkbuf == null)
			return;

		ByteBuffer bb = ByteBuffer.wrap(smkbuf);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		try {
			smkfil = new SMKFile(bb);
		} catch (Exception e) {
			return;
		}

		smkfil.setEnable(Track.All, Track.Video.mask());

		Tile pic = game.pEngine.getTile(kSMKTile);
		pic.setWidth(smkfil.getHeight());
		pic.setHeight(smkfil.getWidth());
	}

	private void DrawMenu() {
		game.pEngine.clearview(0);
		if (smkfil == null)
			return;
		game.pEngine.setview(0, 0, xdim - 1, ydim - 1);
		Tile pic = game.pEngine.getTile(BACKGROUND);

		int frames = xdim / pic.getWidth();
		int x = 160;
		for (int i = 0; i <= frames; i++) {
			game.pEngine.rotatesprite(x << 16, 100 << 16, 0x10000, 0, BACKGROUND, 0, 0, 2 + 8 + 256, 0, 0, xdim - 1,
					ydim - 1);
			x += pic.getWidth();
		}

		game.pEngine.rotatesprite(xdim << 15, ydim << 15, divscale(ydim, game.pEngine.getTile(kSMKTile).getWidth(), 16),
				512, kSMKTile, 0, 0, 4 | 8, 0, 0, xdim - 1, ydim - 1);

		game.menu.mDrawMouse(BuildGdx.input.getX(), BuildGdx.input.getY());

		game.pEngine.nextpage();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
		game.updateColorCorrection();
	}

}
