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

import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiReceiver implements Receiver  {
	
	private final int CONTROL_VOLUME = 7;
	private final int MAXCHANNELS = 16;
	private final byte MAXVOLUME = 127;

	private final Receiver receiver;
	private final byte[] chVolume;
	private float volume;
	
	
	public MidiReceiver(Receiver receiver)
	{
		this.receiver = receiver;
		chVolume = new byte[MAXCHANNELS];
		Arrays.fill(chVolume, MAXVOLUME);
	}

	@Override
	public void close() {
		receiver.close();
	}
	
	public void setVolume(float volume)
	{
		this.volume = Math.min(Math.max(volume, 0.0f), 1.0f);
		for(int i = 0; i < MAXCHANNELS; i++)
		{
			ShortMessage message = new ShortMessage();
	        try {
	            message.setMessage(ShortMessage.CONTROL_CHANGE | i, CONTROL_VOLUME, (byte) (chVolume[i] * volume));
	            receiver.send(message, -1);
	        } catch (InvalidMidiDataException e) {}
		}
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
		ShortMessage volumeMessage;
		if((volumeMessage = getVolumeMessage(message)) != null)
		{
			byte[] data = volumeMessage.getMessage();
			chVolume[volumeMessage.getChannel()] = data[2];
			try { volumeMessage.setMessage(data[0], data[1], (byte) (data[2] * volume));
			} catch (InvalidMidiDataException e) {}
			receiver.send(volumeMessage, timeStamp);
		} else receiver.send(message, timeStamp);
	}
	
	private ShortMessage getVolumeMessage(MidiMessage message) {
		if((message.getStatus() & 0xF0) == ShortMessage.CONTROL_CHANGE)
    	{
    		ShortMessage m = (ShortMessage) message;
    		if(m.getData1() == CONTROL_VOLUME) 
    			return m;
    	}
		  
    	return null;
    }
}