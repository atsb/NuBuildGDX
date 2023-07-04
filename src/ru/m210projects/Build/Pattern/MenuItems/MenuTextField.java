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

package ru.m210projects.Build.Pattern.MenuItems;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Input.KeyInput.*;
import static ru.m210projects.Build.Input.Keymap.*;
import static ru.m210projects.Build.Strhandler.isalpha;
import static ru.m210projects.Build.Strhandler.isdigit;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Input.InputCallback;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuTextField extends MenuItem {

	public static final int LETTERS = 1;
	public static final int NUMBERS = 2;
	public static final int SYMBOLS = 4;
	public static final int POINT = 8;
	
	public char[] typingBuf = new char[16];
	public char[] otypingBuf = new char[16];
	public boolean typing;
	public String typed;
	public int inputlen;
	public int oinputlen;
	
	private final InputCallback inputCallback;
	private final MenuProc confirmCallback;
	
	public MenuTextField(Object text, String input, BuildFont font, int x, int y, int width, final int charFlag, MenuProc confirmCallback) {
		super(text, font);
		
		this.flags = 3 | 4;
		this.m_pMenu = null;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.typing = false;
		
		this.inputCallback = new InputCallback() {
			@Override
			public int run(int ch) {
				if (ch == Keys.ESCAPE) 
	                return -1;

				if (ch == Keys.BACKSPACE) { 
	            	if (inputlen == 0)
	            		return 0;
	            	inputlen--;
	            	typingBuf[inputlen]=0;
	            }
				
				if (ch == Keys.ENTER) 
					return 1;

				if(BuildGdx.input.isKeyPressed(Keys.CONTROL_LEFT) && ch == Keys.V)
				{
					if(BuildGdx.app.getClipboard() != null) {
						String content = BuildGdx.app.getClipboard().getContents();
						for(int i = 0; i < content.length(); i++)
							type(content.charAt(i), charFlag);
					}
					return 0;
				}

				if(ch == KEY_NUMDECIMAL) ch = Keys.PERIOD;
				if(ch >= Keys.NUMPAD_0 && ch <= Keys.NUMPAD_9)
					ch = ch - Keys.NUMPAD_0 + Keys.NUM_0;
				
				type(getChar(ch), charFlag);
				return 0;
			}
		};
		this.typed = input;
		inputlen = input.length();
		System.arraycopy(input.toCharArray(), 0, typingBuf, 0, inputlen);
		this.confirmCallback = confirmCallback;
	}
	
	private void type(char ch, int charFlag) {
		if (inputlen < 15 && ch != 0) {
        	boolean canType;
			canType = (isalpha(ch) && (charFlag & LETTERS) != 0)
					|| (isdigit(ch) && (charFlag & NUMBERS) != 0)
					|| (!isdigit(ch) && !isalpha(ch)
					&& ((charFlag & SYMBOLS) != 0 || (charFlag & POINT) != 0 && ch == '.'));

        	if (canType) 
        		typingBuf[inputlen++]= ch;
    	}
	}
	
	private char getChar(int ch)
	{
		if (ch < 128) 
			return gdxscantoasc[ch];
		return 0;
	}
	
	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		{
			int pal = handler.getPal(font, this);
			int shade = handler.getShade(this);
		    if ( !m_pMenu.mGetFocusedItem(this) )
		     	typing = false;

		    font.drawText(x, y, text, shade, pal, TextAlign.Left, 2, fontShadow);

	    	int px = x + width - 1;
			if(typing) {
				shade = -128;
				px -= 4;
			}

			font.drawText(px - font.getWidth(typingBuf), y, typingBuf, shade, pal, TextAlign.Left, 2, fontShadow);
		    if(typing && (totalclock & 0x20) != 0) 
		    	font.drawChar(px, y, '_', shade, pal, 2, false);
		}
		handler.mPostDraw(this);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		
		if(typing) 
		{
			if(opt != MenuOpt.ESC) {
				if(getInput().putMessage(inputCallback, true) == 1)
				{
					typed = new String(typingBuf, 0, inputlen);
					typing = false;
					
					if(typed.isEmpty()) {
						System.arraycopy(otypingBuf, 0, typingBuf, 0, 16);
						inputlen = oinputlen;
						return false;
					}
					
					if(confirmCallback != null) 
						confirmCallback.run(handler, this);	
				}
			} else {
				System.arraycopy(otypingBuf, 0, typingBuf, 0, 16);
				inputlen = oinputlen;
				typing = false;
			}
		}
		else
		{
			switch(opt)
			{
			case ENTER:
			case LMB:
				if ( (flags & 4) == 0 ) return false;
				
				getInput().initMessageInput(null);		
				System.arraycopy(typingBuf, 0, otypingBuf, 0, 16);
				oinputlen = inputlen;
				typing = true;
				break;
			case ESC:
			case RMB:
				return true;
			case UP:
				m_pMenu.mNavUp();
				return false;
			case DW:
				m_pMenu.mNavDown();
				return false;
			default: 
				return false;
			}
		}
		
		return false;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text != null)
		{
			if(mx > x && mx < x + font.getWidth(text))
				if(my > y && my < y + font.getHeight())
					return true;

			if(mx > x + width - font.getWidth(typingBuf) && mx < x + width - 1)
				return my > y && my < y + font.getHeight();
		}

		return false;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

}
