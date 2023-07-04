// On-screen Display
// for the Build Engine
// by Jonathon Fowler (jf@jonof.id.au)
//
// This file has been ported to Java and modified
// by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build.OnSceenDisplay;

import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Build.FileHandle.Compat.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.Input.InputCallback;
import ru.m210projects.Build.Input.Keymap;

import com.badlogic.gdx.Input.Keys;


import static ru.m210projects.Build.Input.KeyInput.gdxscantoasc;
import static ru.m210projects.Build.Input.KeyInput.gdxscantoascwithshift;
import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Strhandler.Bstrcmp;
import static ru.m210projects.Build.Strhandler.isdigit;

public class Console {

	public static int OSDTEXT_RED      = -2;
	public static int OSDTEXT_BLUE     = -1;
	public static int OSDTEXT_GREEN    = -1;
	public static int OSDTEXT_YELLOW   = -1;
	public static int OSDTEXT_BROWN    = -1;
	public static int OSDTEXT_GOLD     = -1;
	public static int OSDTEXT_WHITE    = -1;
	public static int OSDTEXT_GREY    = -1;

	public static final int MAXLINES = 512;

	public static final int OSD_EDITLENGTH = 511;
	public static final int OSD_HISTORYDEPTH = 32;

	public static final int OSD_INITIALIZED = 0x00000001;
	public static final int OSD_DRAW        = 0x00000002;
	public static final int OSD_CAPTURE     = 0x00000004;
	public static final int OSD_OVERTYPE    = 0x00000008;
	public static final int OSD_SHIFT       = 0x00000010;
	public static final int OSD_CTRL        = 0x00000020;
	public static final int OSD_CAPS        = 0x00000040;

	public static int BGTILE = 2051;
	public static int BGCTILE = 2046;
	public static int BGTILE_SIZEX = 128;
	public static int BGTILE_SIZEY = 128;
	public static int BORDTILE = 2205;	// BORDER
	public static int BITSTH = 1+32+8+16;	// high translucency
	public static int BITSTL = 1+8+16;	// low translucency
	public static int BITS = 8+16+64;		// solid
	public static int BORDERANG = 0;
	public static int SHADE = 50;
	public static int PALETTE = 5;

	static OSDFunc func;
	private static final ByteArrayOutputStream logStream = new ByteArrayOutputStream();


	// presentation parameters
	public static final OSDCOMMAND osdpromptshade = new  OSDCOMMAND( "osdpromptshade", "osdpromptshade: sets the shade of the OSD prompt", 0,  0, 255);
	public static final OSDCOMMAND osdpromptpal = new  OSDCOMMAND( "osdpromptpal", "osdpromptpal: sets the palette of the OSD prompt", 0, 0, MAXPALOOKUPS-1);
	public static final OSDCOMMAND osdeditshade = new  OSDCOMMAND( "osdeditshade", "osdeditshade: sets the shade of the OSD input text", 0, 0, 7);
	public static final OSDCOMMAND osdeditpal = new  OSDCOMMAND( "osdeditpal", "osdeditpal: sets the palette of the OSD input text", 0, 0, MAXPALOOKUPS-1);
	public static final OSDCOMMAND osdtextshade = new  OSDCOMMAND( "osdtextshade", "osdtextshade: sets the shade of the OSD text", 0, 0, 7);
	public static final OSDCOMMAND osdtextpal = new  OSDCOMMAND( "osdtextpal", "osdtextpal: sets the palette of the OSD text", 0, 0, MAXPALOOKUPS-1);

	static int osdflags = 0;

	// history display
	static char[][] osdtext = new char[MAXLINES][];
	static short[][] osdfmt = new short[MAXLINES][];
	static char[] osdver = new char[64];
	static int  osdverlen;
	static int  osdvershade;
	static int  osdverpal;
	static int  osdpos=0;           // position next character will be written at
	static int  osdlines=1;         // # lines of text in the buffer
	static int  osdrows=20;         // # lines of the buffer that are visible
	static int osdrowscur=-1;
	public static int  osdscroll=0;
	static int  osdcols=60;         // width of onscreen display in text columns
	static int  osdmaxrows=20;      // maximum number of lines which can fit on the screen
	static int  osdhead=0;          // topmost visible line number
	static int[] osdkey = new int[4];
	static int  keytime=0;
	static long osdscrtime = 0;
	static int osdtextscale = 65536;

	// command prompt editing
	static char[] osdeditbuf = new char[OSD_EDITLENGTH+1];   // editing buffer
	static char[] osdedittmp = new char[OSD_EDITLENGTH+1];   // editing buffer temporary workspace
	static int  osdeditlen=0;       // length of characters in edit buffer
	static int  osdeditcursor=0;        // position of cursor in edit buffer
	static int  osdeditwinstart=0;
	static int  osdeditwinend=60-1-3;
	public static final int editlinewidth = (osdcols-1-3);

	// command processing
	static int osdhistorypos=-1;       // position we are at in the history buffer
	static char[][] osdhistorybuf = new char[OSD_HISTORYDEPTH][OSD_EDITLENGTH+1];  // history strings
	static int  osdhistorysize=0;       // number of entries in history
	static int  osdhistorytotal=0;      // number of total history entries

	public static String[] osd_argv = new String[80];
	public static int osd_argc = 0;
	// execution buffer
	// the execution buffer works from the command history
	static int  osdexeccount=0;     // number of lines from the head of the history buffer to execute

	static HashMap<String, OSDCOMMAND> osdvars;
	public static FileResource logfile = null;

	public static int RegisterCvar(OSDCOMMAND cvar)
	{
	    if ((osdflags & OSD_INITIALIZED) == 0)
	        Init();

	    if (cvar.name == null || cvar.name.isEmpty())
	    {
	        Println("RegisterCvar(): can't register null cvar");
	        return -1;
	    }

	    // check for illegal characters in name

	    for(int pos = 0; pos < cvar.name.length(); pos++)
	    {
	    	char cp = cvar.name.charAt(pos);
	        if ((cp >= '0') && (cp <= '9'))
	        {
	            Println("RegisterCvar(): first character of cvar name \"" + cvar.name + "\" must not be a numeral");
	            return -1;
	        }
	        if ((cp < '0') ||
	                (cp > '9' && cp < 'A') ||
	                (cp > 'Z' && cp < 'a' && cp != '_') ||
	                (cp > 'z'))
	        {
	            Println("RegisterCvar(): illegal character in cvar name \"" + cvar.name + "\"");
	            return -1;
	        }
	    }

	    osdvars.put(cvar.name, cvar);

	    return 0;
	}

	public static boolean Set(String cmd, int value)
    {
    	OSDCOMMAND cvar = Get(cmd);
    	if(cvar != null)
    		return cvar.SetValue(value);

    	return false;
    }

	public static int Geti(String osdvar)
    {
		OSDCOMMAND var = Get(osdvar);
		if(var != null)
			return (int) var.value;
    	return 0;
    }

	public static float Getf(String osdvar)
    {
		OSDCOMMAND var = Get(osdvar);
		if(var != null)
			return var.value;
    	return 0;
    }

	public static void setVersion(String version, int shade, int pal)
	{
		String fullname = version + " (BuildGdx: " + Engine.version + ")";
		System.arraycopy(fullname.toCharArray(), 0, osdver, 0, Math.min(64, fullname.length()));
	    osdverlen = fullname.length();
	    osdvershade = shade;
	    osdverpal = pal;
	}

	public static void setParameters(
	    int promptshade, int promptpal,
	    int editshade, int editpal,
	    int textshade, int textpal
	)
	{
	    osdpromptshade.SetValue(promptshade);
	    osdpromptpal.SetValue(promptpal);
	    osdeditshade.SetValue(editshade);
	    osdeditpal.SetValue(editpal);
	    osdtextshade.SetValue(textshade);
	    osdtextpal.SetValue(textpal);
	}

	public static void setFunction(OSDFunc func)
	{
		Console.func = func;
	}

	public static void Print(String text, int pal)
	{
		if(text == null || text.isEmpty())
			return;

		if ((osdflags & OSD_INITIALIZED) == 0)
	        Init();

		if(pal == OSDTEXT_RED)
			System.err.println(text);
		else System.out.println(text);

		LogPrint(text);

		int chp = 0;
		int s = (int)osdtextshade.value;
		do
	    {
	        if (text.charAt(chp) == '\n')
	        {
	            osdpos=0;
	            LineFeed();
	            continue;
	        }

	        if (text.charAt(chp) == '\r')
	        {
	            osdpos=0;
	            continue;
	        }

	        if (text.charAt(chp) == '\t')
	        {
	        	for(int i = 0; i < 2; i++)
	        		osdtext[0][osdpos++] = ' ';
	            continue;
	        }

	        if(text.charAt(chp) == '^')
    		{
    			String number = "";
    			if(chp+1 >= text.length()) continue;
    			char num1 = text.charAt(chp+1);
    			if(isdigit(num1))
    			{
    				number += num1;
					char num2 = ' ';
					if(++chp+1 < text.length())
						num2 = text.charAt(chp+1);
    				if(!isdigit(num2))
    				{
    					pal = Integer.parseInt(number, 10);
    					continue;
    				}
    				chp++;
    				number += num2;
    				pal = Integer.parseInt(number, 10);
                    continue;
    			}

    			if(num1 == 'S' || num1 == 's')
    			{
    				chp++;
    				char num = text.charAt(++chp);
    				if(isdigit(num))
    				{
    					number += num;
    					s = Integer.parseInt(number, 10);
    					continue;
    				}
    			}

    			if(num1 == 'O' || num1 == 'o')
    			{
    				chp++;
    				pal = (int) osdtextpal.value;
	                s = (int) osdtextshade.value;
	                continue;
    			}
    		}
	        osdtext[0][osdpos] = text.charAt(chp);
	        osdfmt[0][osdpos++] = (short) (pal+(s<<5));

	        if (osdpos == osdcols)
	        {
	            osdpos = 0;
	            if(chp < text.length() - 1 && text.charAt(chp+1) != 0)
	            	LineFeed();
	        }
	    }
	    while (++chp < text.length());
	}

	public static void Println(String text)
	{
		Print(text, (int)osdtextpal.value);
		osdpos = 0;
        LineFeed();
	}

	public static void Println(String text, int pal)
	{
		Print(text, pal);
		osdpos = 0;
        LineFeed();
	}

	public static void Print(String text)
	{
		Print(text, (int)osdtextpal.value);
	}

	public static char[][] getTextPtr()
	{
	    return osdtext;
	}

	public static short[][] getFmtPtr()
	{
	    return osdfmt;
	}

	public static void SetLogFile(String fn)
	{
		if (logfile != null) logfile.close();
		logfile = null;

		if (fn != null)
			logfile = BuildGdx.compat.open(toLowerCase(fn), Path.User, Mode.Write);
	}

	public static void CloseLogFile()
	{
		if (logfile != null) logfile.close();
		logfile = null;
	}

	public static void LogData(byte[] data)
	{
		if (logfile != null) {
			logfile.writeBytes(data, data.length);
	    }
	    StreamData(data);
	}

	public static void StreamData(byte[] data)
	{
		try {
			logStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void LogPrint(String str)
	{
		if (logfile != null) {
			synchronized (Engine.lock) {
				logfile.writeBytes(str.getBytes(), str.getBytes().length);
				logfile.writeBytes("\r\n".getBytes(), 2);
			}
	    }
	    StreamPrint(str);
	}

	public static void StreamPrint(String str)
	{
		try {
			logStream.write(str.getBytes());
			logStream.write("\r\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String GetLog()
	{
		return logStream.toString();
	}

	public static byte[] GetData()
	{
		return logStream.toByteArray();
	}

	public static boolean IsMoving()
	{
	    return (osdrowscur!=-1 && osdrowscur!=osdrows);
	}

	public static boolean IsShown()
	{
	    return (osdflags & OSD_DRAW) != 0;
	}

	public static boolean IsInited()
	{
		return (osdflags & OSD_INITIALIZED) != 0;
	}

	private static void Init()
	{
//		osdnumsymbols = osdnumcvars = 0;

		osdlines = 1;
		osdflags |= OSD_INITIALIZED;

		osdtext[0] = new char[osdcols];
        osdfmt[0] = new short[osdcols];
		Arrays.fill(  osdfmt[0], (short) ((int)(osdtextpal.value + osdtextshade.value)<<5));

		osdvars = new HashMap<String, OSDCOMMAND>();

		RegisterCvar(osdpromptshade);
		RegisterCvar(osdpromptpal);
		RegisterCvar(osdeditshade);
		RegisterCvar(osdeditpal);
		RegisterCvar(osdtextshade);
		RegisterCvar(osdtextpal);

		RegisterCvar(new OSDCOMMAND("help",
			"listsymbols: lists all registered functions, cvars and aliases",
			new OSDCVARFUNC() {
			@Override
			public void execute() {
				int maxwidth = 0;
				foundText.clear();
				for (String key : osdvars.keySet()) {
					foundText.add(key);
					maxwidth = Math.max(maxwidth, key.length());
				}

				maxwidth += 3;
				if(foundText.size() > 1) {
					Collections.sort(foundText);
					Println("Symbol listing:", OSDTEXT_RED);
					StringBuilder msg = new StringBuilder("  ");
					for(int i = 0; i < foundText.size(); i ++)
		            {
						msg.append(foundText.get(i));
						for(int j = 0; j < maxwidth - foundText.get(i).length(); j++)
							msg.append(" ");
		                if (msg.length() > (osdcols - maxwidth))
		                {
		                    msg.append("\n");
		                    Print(msg.toString());
		                    if (i < foundText.size())
		                    	msg = new StringBuilder("  ");
		                }
		            }
					if(msg.length() > 2)
		        	  Println(msg.toString());
					Println("Found " + foundText.size() + " symbols", OSDTEXT_RED);
				}
			}
		}));

		RegisterCvar(new OSDCOMMAND("osdrows",
			"osdrows: sets the number of visible lines of the OSD", osdrows,
			new OSDCVARFUNC() {
			@Override
			public void execute() {
				osdrows = Integer.parseInt(osd_argv[1]);

				if (osdrows > osdmaxrows) {
					osdrows = osdmaxrows;
					Set("osdrows", osdrows);
				}
	            if (osdrowscur!=-1) osdrowscur = osdrows;
			}
		}, 0, MAXPALOOKUPS-1));

		RegisterCvar(new OSDCOMMAND("osdtextscale",
				"osdtextscale: sets the OSD text scale",
				new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (Console.osd_argc != 2) {
						Console.Println("\"osdtextscale\" is " + "\"" + String.format("%.2f", osdtextscale / 65535.0f).replace(',', '.') + "\"");
						Console.Println("osdtextscale: sets the OSD text scale");
						return;
					}
					try {
						float value = Float.parseFloat(osd_argv[1]);
						if(value > 0.5) {
							setTextScale((int) (value * 65536.0f));
							osdtextscale = (int) (value * 65536.0f);
							Console.ResizeDisplay(xdim, ydim);
							if (osdrowscur!=-1) osdrowscur = osdrows;
						} else Console.Println("osdtextscale: out of range");
					}  catch(Exception e) {
						Console.Println("osdtextscale: out of range");
					}
				}
			}));
	}

	public static void setTextScale(int scale)
	{
		boolean isFullscreen = osdrowscur == osdmaxrows;
		osdtextscale = scale;
		if(!Console.IsInited() || func == null) return;

		Console.ResizeDisplay(xdim, ydim);
		if(isFullscreen)
			fullscreen(true);
	}

	public static int getTextScale()
	{
		return osdtextscale;
	}

	public static void setCaptureKey(int sc, int num)
	{
		osdkey[num] = sc;
	}

	private static void showDisplay(int onf)
	{
	    osdflags = (osdflags & ~OSD_DRAW) | (-onf & OSD_DRAW);
	}

	private static void CaptureInput(boolean cap)
	{
	    osdflags = (osdflags & ~(OSD_CAPTURE|OSD_CTRL|OSD_SHIFT)) | (-(cap?1:0) & OSD_CAPTURE);

	    func.showosd(cap?1:0);

	    if(getInput() != null)
	    	getInput().initMessageInput(null);
	}

	private static final InputCallback osdcallback = new InputCallback() {
		@Override
		public int run(int ch) {
			if (osdeditlen < OSD_EDITLENGTH && ch < 128) {
				if((osdflags & OSD_SHIFT) != 0)
					ch=gdxscantoascwithshift[ch];
				else
					ch=gdxscantoasc[ch];
                if (ch != 0) {
                	 if (osdeditcursor <= osdeditlen-1)
                	 {
                		 if((osdflags & OSD_OVERTYPE) != 0)
                			 osdeditbuf[osdeditcursor++]=(char)ch;
                		 else {
                    		 System.arraycopy(osdeditbuf, osdeditcursor, osdeditbuf, osdeditcursor+1, osdeditlen-osdeditcursor);
                    		 osdeditbuf[osdeditcursor++]=(char)ch;
                    		 osdeditlen++;
                		 }
                	 } else {
                    	osdeditbuf[osdeditlen++]=(char)ch;
                    	osdeditcursor = osdeditlen;
                	 }
                }
        	}
			return 0;
		}
	};

	public static void toggle()
	{
		osdscroll = -osdscroll;
        if (osdrowscur == -1)
            osdscroll = 1;
        else if (osdrowscur == osdrows)
            osdscroll = -1;
        osdrowscur += osdscroll;
        CaptureInput(osdscroll == 1);
        osdscrtime = func.getticksfunc();
	}

	public static void fullscreen(boolean show)
	{
		if(show)
			osdrowscur = osdmaxrows;
		else osdrowscur = -1;

		showDisplay(show?1:0);
		if(func != null)
			func.showosd(show?1:0);
	}

	static boolean lastmatch;
	static int tabc;
	public static void HandleScanCode()
	{
		if ((osdflags & OSD_INITIALIZED) == 0)
	        return;

		for(int i = 0; i < 4; i++) {
			if(getInput().keyStatusOnce(osdkey[i]))
			{
				toggle();
	            return;
			}
		}

		if ((osdflags & OSD_CAPTURE) == 0)
			return;

		keytime = func.gettime();

		if(getInput().keyStatusOnce(Keys.ESCAPE))
		{
			osdscroll = -1;
	        osdrowscur--;
	        CaptureInput(false);
	        osdscrtime = func.getticksfunc();
		}

		if(getInput().keyStatusOnce(Keys.PAGE_UP))
		{
			if (osdhead < osdlines-2)
	            osdhead++;
		}

		if(getInput().keyStatusOnce(Keys.PAGE_DOWN))
		{
			if (osdhead > 0)
	            osdhead--;
		}

		if(getInput().keyStatusOnce(Keys.HOME))
		{
			if ((osdflags & OSD_CTRL) != 0)
	            osdhead = osdlines-2;
	        else
	        {
	            osdeditcursor = 0;
	            osdeditwinstart = osdeditcursor;
	            osdeditwinend = osdeditwinstart+editlinewidth;
	        }
		}

		if(getInput().keyStatusOnce(Keys.END))
		{
			if ((osdflags & OSD_CTRL) != 0)
	            osdhead = 0;
	        else
	        {
	            osdeditcursor = osdeditlen;
	            osdeditwinend = osdeditcursor;
	            osdeditwinstart = osdeditwinend-editlinewidth;
	            if (osdeditwinstart<0)
	            {
	                osdeditwinstart=0;
	                osdeditwinend = editlinewidth;
	            }
	        }
		}

		if(getInput().keyStatusOnce(Keys.INSERT))
		{
			osdflags = (osdflags & ~OSD_OVERTYPE) | (-(((osdflags & OSD_OVERTYPE) == 0)?1:0) & OSD_OVERTYPE);
		}

		if(getInput().keyStatusOnce(Keys.LEFT))
		{
			if (osdeditcursor>0)
	        {
				if ((osdflags & OSD_CTRL) != 0)
	            {
	                while (osdeditcursor>0)
	                {
	                    if (osdeditbuf[osdeditcursor-1] != Keys.SPACE)
	                        break;
	                    osdeditcursor--;
	                }
	                while (osdeditcursor>0)
	                {
	                    if (osdeditbuf[osdeditcursor-1] == Keys.SPACE)
	                        break;
	                    osdeditcursor--;
	                }
	            }
	            else osdeditcursor--;
	        }
	        if (osdeditcursor<osdeditwinstart)
	        {
	            osdeditwinend-=(osdeditwinstart-osdeditcursor);
	            osdeditwinstart-=(osdeditwinstart-osdeditcursor);
	        }
		}

		if(getInput().keyStatusOnce(Keys.RIGHT))
		{
			if (osdeditcursor<osdeditlen)
	        {
	            if ((osdflags & OSD_CTRL) != 0)
	            {
	                while (osdeditcursor<osdeditlen)
	                {
	                    if (osdeditbuf[osdeditcursor] == Keys.SPACE)
	                        break;
	                    osdeditcursor++;
	                }
	                while (osdeditcursor<osdeditlen)
	                {
	                    if (osdeditbuf[osdeditcursor] != Keys.SPACE)
	                        break;
	                    osdeditcursor++;
	                }
	            }
	            else osdeditcursor++;
	        }
	        if (osdeditcursor>=osdeditwinend)
	        {
	            osdeditwinstart+=(osdeditcursor-osdeditwinend);
	            osdeditwinend+=(osdeditcursor-osdeditwinend);
	        }
		}

		if(getInput().keyStatusOnce(Keys.UP))
		{
			HistoryPrev();
		}

		if(getInput().keyStatusOnce(Keys.DOWN))
		{
			HistoryNext();
		}

		if(getInput().keyStatus(Keys.SHIFT_LEFT) || getInput().keyStatus(Keys.SHIFT_RIGHT)) {
			osdflags |= OSD_SHIFT;
		} else osdflags &= ~OSD_SHIFT;

		if(getInput().keyStatus(Keys.CONTROL_LEFT) || getInput().keyStatus(Keys.CONTROL_RIGHT)) {
			osdflags |= OSD_CTRL;
		} else osdflags &= ~OSD_CTRL;

		if(getInput().keyStatusOnce(KEY_CAPSLOCK))
		{
			osdflags = (osdflags & ~OSD_CAPS) | (-(((osdflags & OSD_CAPS) == 0)?1:0) & OSD_CAPS);
		}

		if(getInput().keyStatusOnce(Keys.DEL)) //backspace
		{
			if (/*osdeditcursor == osdeditlen ||*/ osdeditcursor == 0 || osdeditlen == 0)
	            return;
	         if (osdeditcursor <= osdeditlen-1)
	        	System.arraycopy(osdeditbuf, osdeditcursor, osdeditbuf, osdeditcursor-1, osdeditlen-osdeditcursor);

	         osdeditlen--;
	         osdeditcursor--;
		}

		if(getInput().keyStatusOnce(Keys.ENTER))
		{
			if (osdeditlen>0)
	        {
	            osdeditbuf[osdeditlen] = 0;
	            if (Bstrcmp(osdhistorybuf[0], osdeditbuf) != 0)
	            {
	            	for(int i = OSD_HISTORYDEPTH - 1; i >= 1; i--)
	            		System.arraycopy( osdhistorybuf[i-1], 0, osdhistorybuf[i], 0, OSD_EDITLENGTH+1 );
	            	System.arraycopy( osdeditbuf, 0, osdhistorybuf[0], 0, OSD_EDITLENGTH+1 );

	                if (osdhistorysize < OSD_HISTORYDEPTH) osdhistorysize++;
	                osdhistorytotal++;
				}
				osdexeccount++;
				osdhistorypos=-1;
	            String input = new String(osdeditbuf, 0, osdeditlen);

	            if(Dispatch(input) == -1) {
	            	Print("\"" + input + "\" is not a valid command or cvar\n", OSDTEXT_RED);
	            }
	        }

			osdhead = 0;
	        osdeditlen=0;
	        osdeditcursor=0;
	        osdeditwinstart=0;
	        osdeditwinend=editlinewidth;
		}

		getInput().putMessage(osdcallback, false);

		if(getInput().keyStatusOnce(Keymap.ANYKEY)) {
			if(getInput().keyStatusOnce(Keys.TAB)) {
				ListCommands();
			} else lastmatch = false;
		}
	}

	private static void HistoryPrev()
	{
	    if (osdhistorypos >= osdhistorysize-1) return;

	    osdhistorypos++;
	    System.arraycopy( osdhistorybuf[osdhistorypos], 0, osdeditbuf, 0, OSD_EDITLENGTH+1 );

	    osdeditcursor = 0;
	    while (osdeditbuf[osdeditcursor] != 0) osdeditcursor++;
	    osdeditlen = osdeditcursor;

	    if (osdeditcursor<osdeditwinstart)
	    {
	        osdeditwinend = osdeditcursor;
	        osdeditwinstart = osdeditwinend-editlinewidth;

	        if (osdeditwinstart<0)
	        {
	            osdeditwinend-=osdeditwinstart;
	            osdeditwinstart=0;
	        }
	    }
	}

	private static void HistoryNext()
	{
	    if (osdhistorypos < 0) return;

	    if (osdhistorypos == 0)
	    {
	        osdeditlen=0;
	        osdeditcursor=0;
	        osdeditwinstart=0;
	        osdeditwinend=editlinewidth;
	        osdhistorypos = -1;
	        return;
	    }

	    osdhistorypos--;
	    System.arraycopy( osdhistorybuf[osdhistorypos], 0, osdeditbuf, 0, OSD_EDITLENGTH+1 );

	    osdeditcursor = 0;
	    while (osdeditbuf[osdeditcursor] != 0) osdeditcursor++;
	    osdeditlen = osdeditcursor;

	    if (osdeditcursor<osdeditwinstart)
	    {
	        osdeditwinend = osdeditcursor;
	        osdeditwinstart = osdeditwinend-editlinewidth;

	        if (osdeditwinstart<0)
	        {
	            osdeditwinend-=osdeditwinstart;
	            osdeditwinstart=0;
	        }
	    }
	}

	private static OSDCOMMAND Get(String osdvar)
	{
		if(osdvar == null || osdvar.isEmpty() || osdvars == null)
			return null;

		return osdvars.get(osdvar);
	}

	private static void TokenizeString(String text)
	{
		osd_argc = 0;
		if(text == null || text.isEmpty())
			return;

		osd_argc++;
		Arrays.fill(osd_argv, null);
		String osdvar = toLowerCase(text).trim();
		osd_argv[osd_argc-1] = osdvar;
    	String var;
    	int index = osdvar.indexOf(" ");
    	while(index != -1) {
    		var = osdvar.substring(0, index);
    		osdvar = osdvar.substring(index).trim();
    		osd_argv[osd_argc-1] = var;
    		osd_argv[osd_argc++] = osdvar;
    		index = osdvar.indexOf(" ");
    	}
	}

	private static void LineFeed()
	{
        System.arraycopy( osdtext, 0, osdtext, 1, Math.min(osdlines, MAXLINES-1) );
        System.arraycopy( osdfmt, 0, osdfmt, 1, Math.min(osdlines, MAXLINES-1) );
        osdtext[0] = new char[osdcols];
        osdfmt[0] = new short[osdcols];
		Arrays.fill(  osdfmt[0], (short) ((int)(osdtextpal.value + osdtextshade.value)<<5));

        if (osdlines < MAXLINES) osdlines++;
	}

	private static final List<String> foundText = new ArrayList<String>();
	private static void ListCommands()
	{
		if(!lastmatch) {
			char[] buf = osdeditbuf;
			int len = osdeditlen;
			int maxwidth = 0;
			String inputText = new String(buf, 0, len);
			if(inputText.isEmpty())
				return;
			foundText.clear();
			for (String key : osdvars.keySet()) {
				if(key.startsWith(inputText)) {
					foundText.add(key);
					maxwidth = Math.max(maxwidth, key.length());
				}
			}
			maxwidth += 3;
			if(foundText.size() == 1)
			{
				osdeditlen = foundText.get(0).length();
				System.arraycopy(foundText.get(0).toCharArray(), 0, osdeditbuf, 0, osdeditlen);
				osdeditcursor = osdeditlen;
			} else if(foundText.size() > 1) {
				Collections.sort(foundText);
				Println("Found " + foundText.size() + " possible completions for " + "\"" + inputText + "\"", OSDTEXT_RED);
				StringBuilder msg = new StringBuilder("  ");
				for(int i = 0; i < foundText.size(); i ++)
	            {
					msg.append(foundText.get(i));
					for(int j = 0; j < maxwidth - foundText.get(i).length(); j++)
						msg.append(" ");
	                if (msg.length() > (osdcols - maxwidth))
	                {
	                    msg.append("\n");
	                    Print(msg.toString());
	                    if (i < foundText.size() - 1)
	                    	msg = new StringBuilder("  ");
	                }
	            }
				if(msg.length() > 2)
	        	  Println(msg.toString());
				Println("Press TAB again to cycle through matches", OSDTEXT_RED);
				tabc = 0;
				lastmatch = true;
			}
		} else {
			String msg = foundText.get(tabc);
			if(tabc < foundText.size() - 1)
				tabc++;
			else tabc = 0;
			osdeditlen = msg.length();
			System.arraycopy(msg.toCharArray(), 0, osdeditbuf, 0, osdeditlen);
			osdeditcursor = osdeditlen;
		}
	}

	private static int Dispatch(String text)
	{
		if(osdvars != null) {
        	TokenizeString(text);
        	if(osd_argc >= 1) {
	        	OSDCOMMAND cvar = Get(osd_argv[0]);
	        	if(cvar != null)
	        	{
	        		int out = cvar.Set(osd_argv[1]);
	        		switch(out)
	        		{
		        		case -1:
		        			text = cvar.name + " value out of range\n";
	        				Print(text);
	        				return 0;
		        		case 0:
		        			Println(text); //cvar set
		        			return 0;
		        		case 1:
		        			text = "\"" + cvar.name + "\"" + " is " + "\"" + (int) cvar.value + "\"\n" + cvar.desc + "\n";
		        			Print(text);
		        			return 0;
		        		case 2: //function message
		        			return 0;
	        		}
	        	}
        	}
        }
		if(func.textHandler(text))
			return 0;

		return -1; //unknown command
	}

	public static void ResizeDisplay(int w, int h)
	{
	    int newcols = func.getcolumnwidth(divscale(w, osdtextscale, 16)); //XXX

	    char[][] newosdtext = new char[MAXLINES][];
		short[][] newosdfmt = new short[MAXLINES][];

		int line = 0;
		int newline = 0;
		while(line != osdlines && newline < MAXLINES)
		{
			int pos = 0;
			newosdtext[newline] = new char[newcols];
			newosdfmt[newline] = new short[newcols];
			int swapline = newline;
			for(int ch = 0; ch < osdtext[line].length && osdtext[line][ch] != 0; ch++, pos++)
			{
				if(pos >= newcols) {
					newosdtext[++newline] = newosdtext[swapline];
					newosdfmt[newline] = newosdfmt[swapline];

					newosdtext[swapline] = new char[newcols];
					newosdfmt[swapline] = new short[newcols];
					pos = 0;
				}
				newosdtext[swapline][pos] = osdtext[line][ch];
				newosdfmt[swapline][pos] = osdfmt[line][ch];
			}
			line++;
			newline++;
		}

		osdtext = newosdtext;
		osdfmt = newosdfmt;
		osdlines = newline;
	    osdcols = newcols;
	    osdmaxrows = func.getrowheight(divscale(h, osdtextscale, 16))-2;
	    if (osdrows > osdmaxrows) {
	    	osdrows = osdmaxrows;
	    	Set("osdrows", osdrows);
	    }

	    osdpos = 0;
	    osdhead = 0;
	    osdeditwinstart = 0;
	    osdeditwinend = editlinewidth;
	}

	public static void draw()
	{
	    if ((osdflags & OSD_INITIALIZED) == 0 || func == null)
	        return;

	    if (osdrowscur == 0)
	        showDisplay(((osdflags & OSD_DRAW) != 0) ? 0 : 1);

	    if (osdrowscur == osdrows || osdrowscur == osdmaxrows)
	        osdscroll = 0;
	    else
	    {
	        if ((osdrowscur < osdrows && osdscroll == 1) || osdrowscur < -1)
	        {
	            long j = (func.getticksfunc()-osdscrtime);
	            while (j > -1)
	            {
	                osdrowscur++;
	                j -= 200/osdrows;
	                if (osdrowscur > osdrows-1)
	                    break;
	            }
	        }
	        if ((osdrowscur > -1 && osdscroll == -1) || osdrowscur > osdrows)
	        {
	        	long j = (func.getticksfunc()-osdscrtime);
	            while (j > -1)
	            {
	                osdrowscur--;
	                j -= 200/osdrows;
	                if (osdrowscur < 1)
	                    break;
	            }
	        }
	        osdscrtime = func.getticksfunc();
	    }

	    if ((osdflags & OSD_DRAW) == 0 || osdrowscur <= 0) return;

	    int topoffs = osdhead;
	    int row = osdrowscur-1;
	    int lines = Math.min(osdlines-osdhead, osdrowscur);

	    func.clearbg(osdcols, mulscale(osdrowscur+1, osdtextscale, 16));
	    for (; lines>0; lines--, row--)
	    {
	        func.drawosdstr(0,row,topoffs,osdcols,(int)osdtextshade.value,(int)osdtextpal.value, osdtextscale);
	        topoffs++;
	    }

	    {
	        int offset = ((osdflags & (OSD_CAPS|OSD_SHIFT)) == (OSD_CAPS|OSD_SHIFT) && osdhead > 0)?1:0;
	        int shade = ((int)osdpromptshade.value!=0)?(int)osdpromptshade.value:(sintable[(totalclock<<4)&2047]>>11);

	        if (osdhead == osdlines-1) func.drawchar(0,osdrowscur,'~',shade,(int)osdpromptpal.value, osdtextscale);
	        else if (osdhead > 0) func.drawchar(0,osdrowscur,'^',shade,(int)osdpromptpal.value, osdtextscale);
	        if ((osdflags & OSD_CAPS) != 0) func.drawchar((osdhead > 0)?1:0,osdrowscur,'C',shade,(int)osdpromptpal.value, osdtextscale);
	        if ((osdflags & OSD_SHIFT) != 0) func.drawchar(1+(((osdflags & OSD_CAPS) != 0 && osdhead > 0)?1:0),osdrowscur,'H',shade,(int)osdpromptpal.value, osdtextscale);

	        func.drawchar(2+offset,osdrowscur,'>',shade,(int)osdpromptpal.value, osdtextscale);

	        int len = Math.min(osdcols-1-3-offset, osdeditlen-osdeditwinstart);
	        for (int x=len-1; x>=0; x--)
	        	func.drawchar(3+x+offset,osdrowscur,osdeditbuf[x],(int)(osdeditshade.value)<<1,(int)osdeditpal.value, osdtextscale);

	        offset += 3+osdeditcursor-osdeditwinstart;

	        func.drawcursor(offset,osdrowscur,osdflags & OSD_OVERTYPE,keytime, osdtextscale);

	        if (osdver != null)
	        	func.drawstr(osdcols-osdverlen + 2, osdrowscur - ((offset >= osdcols-osdverlen+2)?1:0),
	                      osdver ,osdverlen,(sintable[(totalclock<<4)&2047]>>11),osdverpal, osdtextscale);
	     }
	}
}
