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

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class MidiSequencer {
	
	protected final Sequencer sequencer;
	private Transmitter transmitter;
	private MidiReceiver receiver;
	private MidiMusicSource currentSource;

	public MidiSequencer() throws MidiUnavailableException
	{
		this.sequencer = MidiSystem.getSequencer(false);
		sequencer.open();
		
		sequencer.addControllerEventListener(new ControllerEventListener() {
			@Override
			public void controlChange(ShortMessage message) {
				if(isLooping()) {
					if((message.getData1() == 116 || message.getData1() == 118) && message.getData2() == 0) {
						if(message.getChannel() == 0 && sequencer.getLoopStartPoint() == 0) 
							sequencer.setLoopStartPoint(sequencer.getTickPosition());
					}
					if((message.getData1() == 117 || message.getData1() == 119) && message.getData2() == 127/*&& loop_pos != 0*/) {
						if(message.getChannel() == 0) 
						sequencer.setTickPosition(sequencer.getLoopStartPoint());
					}
				}
			}
		}, null);
	}

	public void setReceiver(MidiReceiver receiver) throws MidiUnavailableException
	{
		if(transmitter != null)
			transmitter.close();
		if(this.receiver != null)
			this.receiver.close();
		
		transmitter = sequencer.getTransmitter();
		transmitter.setReceiver(receiver);
		this.receiver = receiver;
	}
	
	public void setVolume(float volume) {
		receiver.setVolume(volume);
	}
	
	public void play(MidiMusicSource source, long position)
	{
		checkSource(source);
		sequencer.setLoopStartPoint(0);
		sequencer.setMicrosecondPosition(position);
		sequencer.start();
	}
	
	public void play(MidiMusicSource source, long position, long start, long end)
	{
		checkSource(source);
		sequencer.setMicrosecondPosition(position);
		sequencer.setLoopStartPoint(start);
		sequencer.setLoopEndPoint(end);
		sequencer.start();
	}
	
	public void stop()
	{
		if(isOpen())
			sequencer.stop();
	}
	
	public void resume() {
		if(isOpen())
			sequencer.start();
	}
	
	public long getPosition()
	{
		return sequencer.getMicrosecondPosition();
	}

	public void setLooping(boolean looping)
	{
		if (looping)
        	sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        else
        	sequencer.setLoopCount(0);
	}
	
	public boolean isLooping()
	{
		return sequencer.getLoopCount() != 0;
	}

	public boolean isPlaying(MidiMusicSource source)
	{
		return sequencer.getSequence() == source.data && sequencer.isRunning();
	}

	private void checkSource(MidiMusicSource source) {
		if(sequencer.getSequence() != source.data)
		{
			try {
				if(currentSource != null)
					currentSource.pause();
				sequencer.setSequence(source.data);
				this.currentSource = source;
			} catch (InvalidMidiDataException e) {}
		}
	}
	
	public boolean isOpen()
	{
		return sequencer.isOpen();
	}
	
	public void dispose()
	{
		if(sequencer != null)
    	{
			stop();
			sequencer.close();
    	}
    	if(transmitter != null)
    		transmitter.close();
    	if(receiver != null) 
    		receiver.close();
	}
}
