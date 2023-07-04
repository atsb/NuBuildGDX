// This file is part of BuildSmacker.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildSmacker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildSmacker is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildSmacker.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.BuildSmacker;

import ru.m210projects.BuildSmacker.Header.AudioBits;
import ru.m210projects.BuildSmacker.Header.AudioChannels;

public class SMKAudio {
	protected final byte[] buffer;
	protected int size;
	private AudioChannels ch;
	private AudioBits bits;
	private int rate;

	public SMKAudio(int rate, AudioChannels ch, AudioBits bits, int size) {
		this.size = size;
		this.buffer = new byte[size];
		this.ch = ch;
		this.bits = bits;
		this.rate = rate;
	}
	
	public AudioChannels getChannels()
	{
		return ch;
	}
	
	public AudioBits getBits()
	{
		return bits;
	}
	
	public int getRate()
	{
		return rate;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public byte[] getData()
	{
		return buffer;
	}
}
