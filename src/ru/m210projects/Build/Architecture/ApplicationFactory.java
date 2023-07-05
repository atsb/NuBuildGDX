// This file is part of BuildGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Architecture;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.utils.Clipboard;

import ru.m210projects.Build.Architecture.BuildApplication.Platform;
import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.Input.BuildControllers;

public interface ApplicationFactory {

	int getVersion();

	BuildConfiguration getConfiguration();

	BuildMessage getMessage();

	BuildAudio getAudio();

	Files getFiles();

	BuildControllers getControllers();

	Platform getPlatform();

	BuildFrame getFrame(BuildConfiguration config, BuildFrame.FrameType type);

	ApplicationType getApplicationType();

	Clipboard getClipboard();

}
