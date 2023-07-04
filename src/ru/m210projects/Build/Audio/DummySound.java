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

import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.nio.ByteBuffer;

import ru.m210projects.Build.OnSceenDisplay.Console;

public class DummySound implements Sound {

	public static final String name = "Dummy sound";
	
	@Override
	public boolean init(SystemType system, int kMaxSFXChannels, int softResampler) {
		Console.Println(getName() + " initialized", OSDTEXT_GOLD);
		return true;
	}

	@Override
	public boolean isInited() {
		return true;
	}

	@Override
	public void dispose() {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setListener(int x, int y, int z, int ang) {
	}

	@Override
	public void resetListener() {
	}

	@Override
	public float getVolume() {
		return 1.0f;
	}

	@Override
	public void setVolume(float vol) {
	}

	@Override
	public void stopAllSounds() {
	}

	@Override
	public Source newSound(ByteBuffer data, int rate, int bits, int channels, int priority) {
		return null;
	}
	
	@Override
	public SoundData decodeSound(byte[] data) {
		return null;
	}

	@Override
	public void update() {
	}

	@Override
	public Music getDigitalMusic() {
		return null;
	}

	@Override
	public boolean isAvailable(int priority) {
		return true;
	}

	@Override
	public float getReverb() {
		return 0;
	}

	@Override
	public void setReverb(boolean enable, float delay) {
	}

	@Override
	public int getCurrentSoftResampler() {
		return 0;
	}

	@Override
	public void setSoftResampler(int num) {
	}

	@Override
	public String getSoftResamplerName(int num) {
		return "Not supported";
	}

	@Override
	public void uninit() {
	}

	@Override
	public int getNumResamplers() {
		return 1;
	}
}
