// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Render;

import static ru.m210projects.Build.Engine.MAXXDIM;
import static ru.m210projects.Build.Engine.MAXYDIM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Graphics.DisplayMode;

public class VideoMode {
	public static final int MAXVALIDMODES = 256;
	public static String[] strvmodes;
	public static List<VideoMode> validmodes = new ArrayList<VideoMode>();
	public DisplayMode mode;
	public int xdim,ydim;
	public byte bpp;
	
	public VideoMode(DisplayMode mode) {
		this.mode = mode;
		this.xdim = mode.width;
		this.ydim = mode.height;
		this.bpp = (byte) mode.bitsPerPixel;
	}
	
	public static void initVideoModes(DisplayMode[] modes, DisplayMode DesktopDisplayMode) {
		if(strvmodes != null)
			return;

		for (int i = 0; i < modes.length; i++) {
			VideoMode mode = new VideoMode(modes[i]);
			boolean exist = false;
			for(VideoMode savedmode: validmodes) {
				if(savedmode.xdim == mode.xdim && savedmode.ydim == mode.ydim) {
					exist = true;
					break;
				}
			}
			if(exist)
				continue;
			if(mode.xdim > MAXXDIM)
				continue;
			if(mode.ydim > MAXYDIM)
				continue;

			validmodes.add(mode);
		}
		
		if(validmodes.size() == 0)
			validmodes.add(new VideoMode(DesktopDisplayMode));

		Collections.sort(validmodes, new Comparator<VideoMode>() {
            public int compare(VideoMode lhs, VideoMode rhs) {
                return lhs.ydim > rhs.ydim ? -1 : (lhs.ydim > rhs.ydim ) ? 1 : 0;
            }
		});
		Collections.sort(validmodes, new Comparator<VideoMode>() {
            public int compare(VideoMode lhs, VideoMode rhs) {
                return lhs.xdim > rhs.xdim ? -1 : (lhs.xdim > rhs.xdim ) ? 1 : 0;
            }
		});
		Collections.reverse(validmodes);
		strvmodes = new String[validmodes.size()];
		for (int i = 0; i < validmodes.size(); i++) {
			VideoMode mode = validmodes.get(i);
			if(mode.xdim == DesktopDisplayMode.width && mode.ydim == DesktopDisplayMode.height)
				strvmodes[i] = "* " + mode.xdim + " x " + mode.ydim;
			else strvmodes[i] = mode.xdim + " x " + mode.ydim;
		}
	}
	
	public static DisplayMode getmode(int xdim, int ydim) {
		int j = 0;

		for (int i = 0; i < validmodes.size(); i++) {
			if ((validmodes.get(i).xdim == xdim) && (validmodes.get(i).ydim == ydim))
			{
				j = i;
				break;
			}
		}

		return validmodes.get(j).mode;
	}
	
	public static boolean setFullscreen(int xdim, int ydim, boolean fullscreen)
	{
		if(!fullscreen)
			return false;
		else
			return getmodeindex(xdim, ydim) != -1;
	}
	
	public static DisplayMode getmode(int index) {
		if(index >= 0 && index < validmodes.size())
			return validmodes.get(index).mode;
		
		return null;
	}
	
	public static int getmodeindex(int xdim, int ydim) {
		int j = -1;

		for (int i = 0; i < validmodes.size(); i++) {
			if ((validmodes.get(i).xdim == xdim) && (validmodes.get(i).ydim == ydim))
			{
				j = i;
				break;
			}
		}

		return j;
	}
}
