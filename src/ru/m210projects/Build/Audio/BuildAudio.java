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

package ru.m210projects.Build.Audio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BuildAudio {
	
	public enum MusicType { Midi, Digital }
	public enum Driver { Music, Sound }
	
	private Sound fx;
	private Music mx;
	
	private static boolean initDrivers;
	private static List<Sound> fxdrivers;
	private static List<Music> mxdrivers;

	private static void init()
	{
		if(initDrivers) return;
		
		if(fxdrivers == null) {
			fxdrivers = new ArrayList<Sound>();
			fxdrivers.add(new DummySound());
		}
		
		if(mxdrivers == null) {
			mxdrivers = new ArrayList<Music>();
			mxdrivers.add(new DummyMusic());
		}

		initDrivers = true;
	}
	
	public static void registerDriver(Driver type, Object drv)
	{
		init();
		
		if(drv == null)
			throw new NullPointerException();
		
		if(type == Driver.Sound && drv instanceof Sound) 
			fxdrivers.add((Sound) drv);
		if(type == Driver.Music && drv instanceof Music) 
			mxdrivers.add((Music) drv);
	}
	
	public static List<String> getDeviceslList(Driver type, List<String> list)
	{
		if(type == Driver.Sound) 
			for(Sound s : fxdrivers)
				list.add(s.getName());	

		if(type == Driver.Music) 
			for(Music m : mxdrivers)
				list.add(m.getName());	
		
		return list;
	}
	
	public BuildAudio() {
		init();
		
		this.fx = getSound(0);
		this.mx = getMusic(0);
	}
	
	public boolean IsInited(Driver type)
	{
		if(type == Driver.Sound) 
			return fx.isInited();
		if(type == Driver.Music) 
			return mx.isInited();
		
		return false;
	}
	
	private Sound getSound(int sndnum)
	{
		if(sndnum < 0 || sndnum >= fxdrivers.size())
			sndnum = 0;
		
		return fxdrivers.get(sndnum);
	}
	
	public Sound getSound()
	{
		return fx;
	}
	
	private Music getMusic(int musnum)
	{
		if(musnum < 0 || musnum >= mxdrivers.size())
			musnum = 0;
		
		return mxdrivers.get(musnum);
	}
	
	public Music getMusic()
	{
		return mx;
	}

	public int checkNum(Driver type, int drvnum)
	{
		if(type == Driver.Sound) 
		{
			if(drvnum < 0 || drvnum >= fxdrivers.size())
				drvnum = 0;
		}
		
		if(type == Driver.Music) 
		{
			if(drvnum < 0 || drvnum >= mxdrivers.size())
				drvnum = 0;
		}

		return drvnum;
	}
	
	public BuildAudio setDriver(Driver type, int drvnum)
	{
		if(type == Driver.Sound) 
		{
			if(this.fx != null) {
				this.fx.stopAllSounds();	
				this.fx.uninit();
			}
			
			this.fx = getSound(drvnum);
		}
		
		if(type == Driver.Music) 
		{
			if(this.mx != null) 
				this.mx.dispose();
			
			this.mx = getMusic(drvnum);
		}
		
		return this;
	}

	public int getDriver(Driver type)
	{
		if(type == Driver.Sound) 
		{
			for(int num = 0; num < fxdrivers.size(); num++)
				if(fxdrivers.get(num) == fx)
					return num;
		}
		
		if(type == Driver.Music) 
		{
			for(int num = 0; num < mxdrivers.size(); num++)
				if(mxdrivers.get(num) == mx)
					return num;
		}
		
		return 0;
	}

	public void setVolume(Driver type, float volume)
	{
		switch(type)
		{
		case Sound:
			if(fx.isInited()) 
				fx.setVolume(volume);
			break;
		case Music:
			if(fx.getDigitalMusic() != null && fx.getDigitalMusic().isInited()) 
				fx.getDigitalMusic().setVolume(volume);
			if(mx.isInited()) 
				mx.setVolume(volume);
			break;
		}
	}
	
	public void dispose() {
		for(Sound drv : fxdrivers)
			drv.dispose();
		for(Music drv : mxdrivers)
			drv.dispose();
		fxdrivers.clear();
		mxdrivers.clear();
		fx = null;
		mx = null;
	}
	
	public void update()
	{
		if(fx != null && fx.isInited()) 
			fx.update();
		if(mx != null && mx.isInited()) 
			mx.update();
	}
	
	public String getName(Driver type) {
		switch(type)
		{
		case Sound:
			return fx.getName();
		case Music:
			return mx.getName();
		}
		
		return null;
	}
	
	public SoundData decodeSound(byte[] data) {
		return fx.decodeSound(data);
	}
	
	public Source newSound(ByteBuffer data, int rate, int bits, int priority)
	{
		if(priority == 0) priority = 1;
		return fx.newSound(data, rate, bits, 1, priority);
	}
	
	public Source newSound(ByteBuffer data, int rate, int bits, int channels, int priority)
	{
		if(priority == 0) priority = 1;
		return fx.newSound(data, rate, bits, channels, priority);
	}
	
	public MusicSource newMusic(MusicType type, String file) {
		if(type == MusicType.Midi && mx.isInited()) 
			return mx.newMusic(file);
		if(type == MusicType.Digital && fx.getDigitalMusic() != null && fx.getDigitalMusic().isInited())
			return fx.getDigitalMusic().newMusic(file);
		
		return null;
	}
	
	public MusicSource newMusic(MusicType type, byte[] data) {
		if(type == MusicType.Midi && mx.isInited()) 
			return mx.newMusic(data);
		if(type == MusicType.Digital && fx.getDigitalMusic() != null && fx.getDigitalMusic().isInited())
			return fx.getDigitalMusic().newMusic(data);
		
		return null;
	}

}
