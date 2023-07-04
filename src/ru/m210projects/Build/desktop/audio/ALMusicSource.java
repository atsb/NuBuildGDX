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

package ru.m210projects.Build.desktop.audio;

import ru.m210projects.Build.Audio.MusicSource;

public class ALMusicSource extends MusicSource {

	private final OpenALMusic music;
	public ALMusicSource(OpenALMusic music)
	{
		this.music = music;
	}
	
	@Override
	public void play(boolean loop) {
		music.setLooping(loop);
		music.play();
	}

	@Override
	public void stop() {
		music.stop();
	}

	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void resume() {
		if(!isPlaying())
			play(isLooping());
	}

	@Override
	public void dispose() {
		music.reset();
	}
	
	@Override
	public void update() {
		music.update();
	}
	@Override
	public boolean isPlaying() {
		return music.isPlaying();
	}

	@Override
	public boolean isLooping() {
		return music.isLooping();
	}
	
	public void setVolume(float volume) {
		music.setVolume(volume);
	}
}
