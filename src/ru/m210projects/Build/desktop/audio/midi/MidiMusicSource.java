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

import java.io.ByteArrayInputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import ru.m210projects.Build.Audio.MusicSource;

public class MidiMusicSource extends MusicSource {

	private final MidiSequencer sequencer;
	protected Sequence data;

	public MidiMusicSource(MidiSequencer sequencer, byte[] buf) throws Exception
	{
		if(sequencer == null || !sequencer.isOpen())
			throw new Exception("Sequencer not ready!");
		
		this.sequencer = sequencer;
		this.data = MidiSystem.getSequence(new ByteArrayInputStream(buf));
	}
	
	private void setLooping(boolean looping)
	{
		sequencer.setLooping(looping);
	}

	@Override
	public void play(boolean looping) {
		setLooping(looping);
		sequencer.play(this, 0);
	}
	
	public void play(long start, long end)
	{
		setLooping(true);
		sequencer.play(this, start, start, end);
	}

	@Override
	public void stop() {
    	sequencer.stop();
	}

	@Override
	public void pause() {
		stop();
	}
	
	@Override
	public void resume() {
		sequencer.resume();
	}
	
	@Override
	public void dispose() {
		stop();
		data = null;
	}
	
	@Override
	public boolean isLooping() {
		return sequencer.isLooping();
	}

	@Override
	public boolean isPlaying() {
		return sequencer.isPlaying(this);
	}

	@Override
	public void update() { /* nothing */ }
}
