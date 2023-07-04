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

public interface Sound {
	
	enum SystemType { Mono, Stereo }
	
	//Driver
    boolean init(SystemType system, int kMaxSFXChannels, int softResampler);
	void uninit();
	boolean isInited();
	void dispose();
	String getName();
	Music getDigitalMusic();
	
	//EFX
    float getReverb();
	void setReverb(boolean enable, float delay);
	void setListener(int x, int y, int z, int ang);
	void resetListener();
	
	///Soft resampler
    String getSoftResamplerName(int num);
	int getNumResamplers();
	int getCurrentSoftResampler();
	void setSoftResampler(int num);
	
	//Source handler
    float getVolume();
	void setVolume(float vol);
	void stopAllSounds();
	boolean isAvailable(int priority);
	Source newSound(ByteBuffer data, int rate, int bits, int channels, int priority);
	SoundData decodeSound(byte[] data);
	void update();
}
