// This file is part of BuildGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.desktop.Controllers;

import java.util.List;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

import ru.m210projects.Build.Architecture.BuildController;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Input.BuildControllers;

public class GdxControllers extends BuildControllers {

	@Override
	protected void getControllers(List<BuildController> gamepads) {
		Array<Controller> controllers = null;

		if(BuildGdx.graphics.getFrameType() != FrameType.Canvas) {
			try {
				controllers = Controllers.getControllers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (controllers != null && controllers.size > 0) {
			for (int i = 0; i < controllers.size; i++) {
				try {
					gamepads.add(new GdxController(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
