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

public abstract class Source implements Comparable<Source> {
	
	public static final int Locked = 1;

	public int bufferId;
	public int sourceId;
	public int priority;
	public boolean free;
	public int flags;
	
	public int format;
	public int rate;
	public ByteBuffer data;

	public LoopInfo loopInfo;
	public SourceCallback<Object> callback;
	public Object channel;
	
	public Source(int bufferId, int sourceId, int priority)
	{
		this.bufferId = bufferId;
		this.sourceId = sourceId;
		this.priority = priority;
		this.free = true;
		loopInfo = new LoopInfo();
	}

	@Override
	public int compareTo(Source source) {
		if((source.flags & Source.Locked) != 0) return -1;
		if(source.free) return 1;
		return (this.priority - source.priority);
	}
	
	public abstract void play(float volume);
	public abstract void stop();
	public abstract void pause();
	public abstract void resume();
	public abstract int  dispose();
	public abstract void setLooping(boolean loop, int loopstart, int loopend);
	public abstract void setPosition(float x, float y, float z);
	public abstract void setVolume( float volume );
	public abstract void setPitch( float pitch );
	public abstract void setGlobal(int num);
	public abstract void setPriority(int priority);
	
	public abstract float getVolume();
	public abstract float getPitch();

	public abstract boolean isActive();	
	public abstract boolean isLooping();
	public abstract boolean isPlaying();
	public abstract boolean isGlobal();
	
	public abstract <T> void setCallback(SourceCallback<T> callback, T num);
}
