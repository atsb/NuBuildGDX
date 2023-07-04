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

package ru.m210projects.Build.desktop.audio.midi;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Music;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;

import javax.sound.midi.MidiDevice.Info;

public class MidiMusicModule implements Music {

	protected MidiSequencer sequencer;

	private String name = "Midi Music Module";
	private boolean inited;
	private int nDevice;
	private static List<MidiDevice> devices;
	private MidiDevice device;
	private Soundbank soundbank;
	
	public MidiMusicModule(int nDevice, File soundbank) {
		try {
			devices = getDevices();
			this.nDevice = nDevice;
			if(soundbank != null)
				this.soundbank = MidiSystem.getSoundbank(soundbank);
			else this.soundbank = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<MidiDevice> getDevices()
	{
		if(devices != null) return devices;
		
		devices = new ArrayList<MidiDevice>();
		Info[] dInfos = MidiSystem.getMidiDeviceInfo();
		try {
			for(Info dInfo : dInfos)
			{
				MidiDevice dev = MidiSystem.getMidiDevice(dInfo);
	            if (dev.getMaxReceivers() != 0)
	            	devices.add(dev);
			}
		} catch (Exception e) {
        	e.printStackTrace();
			return null;
        }
		
		return devices;
	}
	
	@Override
	public MusicSource newMusic(byte[] data) {
		try {
			return new MidiMusicSource(sequencer, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public MusicSource newMusic(String name) {
		Resource res = BuildGdx.cache.open(name, 0);
		if(res == null) return null;
		
		byte[] data = res.getBytes();
		res.close();
		return newMusic(data);
	}

	@Override
	public boolean init() {
		try {
			sequencer = new MidiSequencer();
			if(device != null)
	    		device.close();
			
			inited = false;
			if(devices == null) 
				return false;

			if(soundbank != null) {
				Console.Println("Soundbank initializing...");
				Synthesizer softsynth = MidiSystem.getSynthesizer();
				device = softsynth;
				device.open();
				softsynth.loadAllInstruments(soundbank);
			}
			else {
				device = devices.get(nDevice);
				device.open();
			}

            name = device.getDeviceInfo().getName();
            sequencer.setReceiver(new MidiReceiver(device.getReceiver()));
            
            Console.Println(getName() + " initialized", OSDTEXT_GOLD);
		    inited = true;
		    return true;
		} catch (Throwable e) {
			e.printStackTrace();
			name = "initialization failed";
			return false;
		}
	}
	
	@Override
	public void setVolume(float volume) {
		sequencer.setVolume(volume);
	}

	@Override
	public String getName() {
		if(soundbank != null) 
			return soundbank.getName();
		return name;
	}

	@Override
	public boolean isInited() {
		return inited;
	}

	@Override
	public void dispose() {
    	if(sequencer != null)
			sequencer.dispose();
    	if( device != null)
    		device.close();
//    	devices = null;
	}

	@Override
	public void update() { /* nothing */ }
}
