//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.CloseLogFile;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;
import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.GLSettings;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.MemLog;

public abstract class BuildGame extends Game {

	public final String appname;
	public final String sversion;
	public final char[] version;
	public final boolean release;
	public final String OS = System.getProperty("os.name");
	public final Date date;

	public BuildEngine pEngine;
	public BuildControls pInput;
	public BuildConfig pCfg;
	public MenuHandler pMenu;
	public FontHandler pFonts;
	public BuildNet pNet;
	public Interpolation pInt;
	public SaveManager pSavemgr;
	public SliderDrawable pSlider;

	public boolean gExit = false;
	public boolean gPaused = false;

	public DefScript baseDef;
	public DefScript currentDef;

	private Screen gCurrScreen;
	private Screen gPrevScreen;

	public enum NetMode { Single, Multiplayer }

	public NetMode nNetMode;

	public BuildGame(BuildConfig cfg, String appname, String sversion, boolean release)
	{
		this.appname = appname;
		this.sversion = sversion;
		this.release = release;
		this.version = sversion.toCharArray();
		this.pCfg = cfg;
		this.date = new Date("MMM dd, yyyy HH:mm:ss");
	}

	@Override
	public final void create() {
		InitScreen scr = new InitScreen(this);
		setScreen(scr);
		scr.start();
	}

	public abstract BuildFactory getFactory();

	public abstract boolean init() throws Exception;

	public abstract void show();

	@Override
	public void dispose() {
		pCfg.saveConfig(BuildGdx.compat, Path.Game.getPath());
		if(getScreen() instanceof InitScreen) {
			getScreen().dispose();
		}
		if(numplayers > 1)
			pNet.NetDisconnect(myconnectindex);

		if(pEngine != null)
			pEngine.uninit();
		System.out.println("disposed");
	}

	public BuildFont getFont(int i)
	{
		return pFonts.getFont(i);
	}

	@Override
	public void render() {
		try {
			if(!gExit)
				super.render();
			else BuildGdx.app.exit();
		} catch (OutOfMemoryError me) {
			System.gc();

			me.printStackTrace();
			String message = "Memory used: [ " + MemLog.used() + " / " + MemLog.total() + " mb ] \r\nPlease, increase the java's heap size.";
			Console.Println(message, Console.OSDTEXT_RED);
			BuildGdx.message.show("OutOfMemory!", message, MessageType.Info);
			System.exit(1);
		} catch (Throwable e) {
			if (!release) {
			e.printStackTrace();
			dispose();
			System.exit(1);
			} else {
				ThrowError(exceptionHandler(e) + "[BuildGame]: " + (e.getMessage() == null ? "" : e.getMessage())
						+ " \r\n" + stackTraceToString(e));
			}
		}
	}

	public void updateColorCorrection() {
		if (BuildGdx.graphics.getFrameType() == FrameType.GL) {
//			BuildGdx.graphics.extra(Option.GLSetConfiguration, 1 - (GLSettings.gamma.get() / 4096.0f), GLSettings.brightness.get() / 4096.0f, GLSettings.contrast.get() / 4096.0f);
			BuildGdx.graphics.extra(Option.GLSetConfiguration, 1 - (GLSettings.gamma.get() / 4096.0f), 0.0f, 1.0f);
		}
	}

	private String exceptionHandler(Throwable e) {
		if (e instanceof ArithmeticException)
			return "ArithmeticException";
		if (e instanceof ArrayIndexOutOfBoundsException)
			return "ArrayIndexOutOfBoundsException";
		if (e instanceof ArrayStoreException)
			return "ArrayStoreException";
		if (e instanceof ClassCastException)
			return "ClassCastException";
		if (e instanceof IllegalMonitorStateException)
			return "IllegalMonitorStateException";
		if (e instanceof IllegalStateException)
			return "IllegalStateException";
		if (e instanceof IllegalThreadStateException)
			return "IllegalThreadStateException";
		if (e instanceof IndexOutOfBoundsException)
			return "IndexOutOfBoundsException";
		if (e instanceof NegativeArraySizeException)
			return "NegativeArraySizeException";
		if (e instanceof NullPointerException)
			return "NullPointerException";
		if (e instanceof NumberFormatException)
			return "NumberFormatException";
		if (e instanceof SecurityException)
			return "SecurityException";

		return "Application exception (" + e.toString() + ") \n\r";
	}

	public void changeScreen(Screen screen)
	{
		gPrevScreen = gCurrScreen;
		gCurrScreen = screen;
		setScreen(screen);
	}

	public boolean isCurrentScreen(Screen screen)
	{
		return gCurrScreen == screen;
	}

	public void setPrevScreen()
	{
		gCurrScreen = gPrevScreen;
		setScreen(gPrevScreen);
	}

	public String getScrName()
	{
		if(gCurrScreen != null)
			return gCurrScreen.getClass().getSimpleName();

		if(BuildGdx.app != null)
			return "Create frame";

		return "Init frame";
	}

	public boolean setDefs(DefScript script)
	{
		if(currentDef != script) {
			if(currentDef != null)
				currentDef.dispose();
			currentDef = script;
			currentDef.apply();
			pEngine.setDefs(script);
			return true;
		}

		return false;
	}

	protected String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append("\t").append(element.toString());
			sb.append("\r\n");
		}
		return sb.toString();
	}

	protected String getStackTrace()
	{
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			sb.append("\t").append(element.toString());
			sb.append("\r\n");
		}

		return sb.toString();
	}

	public void ThrowError(String msg, Throwable ex) {
		if(!release) {
			ex.printStackTrace();
			System.exit(0);
		} else {
			String stack = stackTraceToString(ex);
			Console.LogPrint(msg + "[" + exceptionHandler(ex) + "]: " + stack);
			System.err.println(msg + "[" + exceptionHandler(ex) + "]: " + stack);
			CloseLogFile();

			try {
				String currScreen = getScrName();
				String prevScreen = gPrevScreen != null ? gPrevScreen.getClass().getSimpleName() : null;

				if(nNetMode == NetMode.Multiplayer)
					pNet.NetDisconnect(myconnectindex);
				if (BuildGdx.message.show(msg, stack, MessageType.Crash))
					saveToFTP(currScreen, prevScreen);
			} catch (Exception e) {
			} finally {
				this.dispose();
				System.exit(0);
			}
		}
	}

	public void ThrowError(String msg) {

		msg += "\r\nFull stack trace: ";
		msg += getStackTrace();

		Console.LogPrint("FatalError: " + msg);
		System.err.println("FatalError: " + msg);
		CloseLogFile();

		try {
			String currScreen = getScrName();
			String prevScreen = gPrevScreen != null ? gPrevScreen.getClass().getSimpleName() : null;

			if(nNetMode == NetMode.Multiplayer)
				pNet.NetDisconnect(myconnectindex);
			if (BuildGdx.message.show("FatalError", msg, MessageType.Crash))
				saveToFTP(currScreen, prevScreen);
		} catch (Exception e) {
		} finally {
			this.dispose();
			System.exit(0);
		}
	}

	public void GameMessage(String msg) {
		BuildGdx.message.show("Message: ", msg, MessageType.Info);
		Console.Println("Message: " + msg, OSDTEXT_RED);
	}

	private final byte[] data1 = { 86, 10, 90, 88, 90 };
	private final byte[] data2 = { 87, 87, 89, 91, 91, 82, 84, 90 };

	private final String ftp = new String(new byte[] { 102, 116, 112, 58, 47, 47 });
	private final String address = new String(new byte[] { 64, 109, 50, 49, 48, 46, 117, 99, 111, 122, 46, 114, 117, 47, 70, 105, 108, 101, 115, 47, 76, 111, 103, 115 });

	private final byte[] data3 = { 102, 116, 116, 112 };
	private final int key = LittleEndian.getInt(data3);

	private String name = null;
	private String pass = null;

	private void initFTP() {
		if (name == null) {
			decryptBuffer(data1, data1.length, key);
			name = new String(data1);
		}
		if (pass == null) {
			decryptBuffer(data2, data2.length, key);
			pass = new String(data2);
		}
	}

	private byte[] decryptBuffer(byte[] buffer, int size, long key) {
		for (int i = 0; i < size; i++)
			buffer[i] ^= key + i;

		return buffer;
	}

	private void saveToFTP(String curr, String prev)
	{
		if(!release)
			return;

		initFTP();

		URL url;
		try {
			String filename = sversion + "_" + date.getLaunchDate();
			filename = toLowerCase(filename.replaceAll("[^a-zA-Z0-9_]", ""));
			url = new URL(ftp + name + ":" + pass + address + "/" + appname + "/" + filename + ".log;type=i");
			URLConnection urlc = url.openConnection();
			OutputStream os = urlc.getOutputStream();
			String text = Console.GetLog();
			text += "\r\n";
			text += "Screen: " + curr + "\r\n";
			if(prev != null)
				text += "PrevScreen: " + prev + "\r\n";
			if(pEngine.getrender() != null) {
				text += "Renderer: " + pEngine.getrender().getType().getName() + "\r\n";
				if(pEngine.getrender().getType() == RenderType.Software)
				{
					text += "	xdim: " + xdim + "\r\n";
					text += "	ydim: " + ydim + "\r\n";
				}
			}

			os.write(text.getBytes());
			try {
				byte[] report = reportData();
				if(report != null)
					os.write(report);
			} catch (Exception e) { text = "Crash in reportData: " + e.getMessage() + "\r\n"; os.write(text.getBytes()); }

			os.close();
		}
		catch (UnknownHostException e)
		{
			System.err.println("No internet connection");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract byte[] reportData();

}
