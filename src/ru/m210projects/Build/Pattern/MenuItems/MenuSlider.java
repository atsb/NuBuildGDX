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

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;

import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;

public class MenuSlider extends MenuItem {

	public int min;
	public int max;
	public int step;
	public int value;
	public boolean digital;
	public float digitalMax;
	public char[] dbuff; 
	public MenuProc callback;
	public BuildFont sliderNumbers;

	private int touchX;
	private boolean isTouched;
	private static MenuSlider touchedObj;
	private final SliderDrawable slider;
	
	public MenuSlider(SliderDrawable slider, Object text, BuildFont textStyle, int x, int y, int width, int value, int min, int max, 
			int step, MenuProc callback, boolean digital) {
		super(text, textStyle);

		this.slider = slider;
		this.flags = 3 | 4;
		this.x = x;
		this.y = y;
		
		this.width = width;
		this.min = min;
		this.max = max;
		this.step = step;
		this.value = BClipRange(value, min, max);

		this.digital = digital;
		this.digitalMax = 0;
		this.callback = callback;
		this.sliderNumbers = font;
		
		dbuff = new char[10];
	}

	@Override
	public void draw(MenuHandler handler) {
		int shade = handler.getShade(this);
		
		if ( text != null )
			font.drawText(x, y, text, shade, handler.getPal(font, this), TextAlign.Left, 2, fontShadow);

		slider.drawSliderBackground(x + width - slider.getSliderRange(), y, shade, handler.getPal(null, this));

		if(digital)
		{
			Arrays.fill(dbuff, (char)0);
			if(digitalMax == 0)
				Bitoa(value, dbuff);
			else {
				String val = Float.toString(value / digitalMax);
				int index = val.indexOf('.');
				buildString(dbuff, 0, val);
				Arrays.fill(dbuff, index + 4, dbuff.length, (char)0);
			}

			sliderNumbers.drawText(x + width - slider.getSliderRange() - sliderNumbers.getWidth(dbuff) - 5, y + (font.getHeight() - sliderNumbers.getHeight()) / 2, dbuff, shade, handler.getPal(sliderNumbers, this), TextAlign.Left, 2, fontShadow);
		}
		
		int xRange = slider.getSliderRange() - slider.getSliderWidth();
		int nRange = max - min;
		int dx = xRange * (value - min) / nRange - slider.getSliderRange();
	
		slider.drawSlider((x + width + dx), y, shade, handler.getPal(null, this));
		handler.mPostDraw(this);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		int val;

		switch(opt) {
		case UP:
			m_pMenu.mNavUp();
			break;
		case DW:
			m_pMenu.mNavDown();
			break;
		case LEFT:
		case MWDW:
			if ( (flags & 4) == 0 ) return false;
			
			if(value <= 0) {
				int dv = (value - step) % -step;
		        val = value - step - dv;
		        if ( dv < 0 )
		        	val += step;
			}
			else 
			{
		        int dv = (value - 1) % step;
		        val = value - 1 - dv;
		        if ( dv < 0 )
		        	val -= step;
			}
			value = BClipRange(val, min, max);
			if(callback != null) {
				callback.run(handler, this);
			}
			break;
		case RIGHT:
		case MWUP:
			if ( (flags & 4) == 0 ) return false;
			
			if ( value < 0 )
		    {
		        int dv = (value - 1) % -step;
		        val = value - 1 - dv;
		        if ( dv < 0 )
		        	val += step;
		    } else
		    {
		        int dv = (value + step) % step;
		        val = value + step - dv;
		        if ( dv < 0 )
		        	val -= step;
		    }
			value = BClipRange(val, min, max);
			if(callback != null) 
				callback.run(handler, this);
			break;
		case ENTER:
			if ( (flags & 4) == 0 ) return false;
			
			if(callback != null) 
				callback.run(handler, this);
			break;
		case LMB:
			if ( (flags & 4) == 0 ) return false;
			
			if(touchedObj == this)
			{
				int startx = x + width - slider.getSliderRange() + slider.getSliderWidth() / 2;
				float dr = (float)(touchX - startx) / (slider.getSliderRange() - slider.getSliderWidth() - 1);
				value = BClipRange((int) (dr * (max - min) + min), min, max);
				if(callback != null) 
					callback.run(handler, this);
			} 
			break;
		default:
			return m_pMenu.mNavigation(opt);
		}
		
		return false;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		touchX = mx;
		isTouched = false;
		if(!BuildGdx.input.isTouched() /*&& touchedObj != this*/)
			touchedObj = null;
		
		if(text != null) {
			if(mx > x && mx < x + font.getWidth(text))
				if(my > y && my < y + font.getHeight()) 
					return true;
		}

		if(touchedObj == null) {
			int cx = x + width - slider.getSliderRange();
			if(mx > cx && mx < cx + slider.getSliderRange())
				if(my > y && my < y + font.getHeight()) {
					isTouched = true;
					if(BuildGdx.input.isTouched())
						touchedObj = this;
				}
		}
		
		return isTouched;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

}