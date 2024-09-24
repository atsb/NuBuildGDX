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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.FileHandle.Cache1D;
import ru.m210projects.Build.FileHandle.Compat;
import ru.m210projects.Build.Input.BuildControllers;


public class BuildGdx {

	public static BuildApplication app;
	public static BuildGraphics graphics;
	public static BuildAudio audio;
	public static BuildInput input;
	public static BuildMessage message;
	public static BuildControllers controllers;
	public static Files files;

	public static GL20 gl20;
	public static GL30 gl30;
	
	public static Compat compat;
	public static Cache1D cache;
}
