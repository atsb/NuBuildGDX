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


package ru.m210projects.Build.Script;

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.util.HashMap;

public class AudioInfo {

	private final HashMap<String, String> midToMusic;
	public AudioInfo()
	{
		midToMusic = new HashMap<String, String>();
	}
	
	public AudioInfo(AudioInfo src) {
		midToMusic = new HashMap<String, String>();
		midToMusic.putAll(src.midToMusic);
	}

	public void addDigitalInfo(String midiname, String oggfile) {
		midToMusic.put(midiname, oggfile);
	}
	
	public String getDigitalInfo(String midi)
	{
		if(midi != null)
			return midToMusic.get(toLowerCase(midi));
		
		return null;
	}
}
