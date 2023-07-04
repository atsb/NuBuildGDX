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

package ru.m210projects.Build.Input;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Architecture.BuildController;
import ru.m210projects.Build.OnSceenDisplay.Console;

public abstract class BuildControllers {
	
	public final static int MAXBUTTONS = 64;
	public final static int MAXPOV = 4;
	public final static int MAXAXIS = 12;

	private List<BuildController> gamepads;
	private float deadZone = 0.01f;
	
	private final boolean TestGamepad = false;

	public BuildControllers init() {
		gamepads = new ArrayList<BuildController>();
		getControllers(gamepads);
		if(TestGamepad)
			gamepads.add(new TestController());
		
		if(gamepads.size() == 0)
			Console.Println("No gamepads found.", OSDTEXT_YELLOW);
		else {
			for(int i = 0; i < gamepads.size(); i++)
			{
				BuildController c = gamepads.get(i);
				Console.Println("Found controller: \"" + c.getName() + "\" [buttons: " + c.getButtonCount() + " axises: " + c.getAxisCount() + " povs: " + c.getPovCount() + "]", OSDTEXT_YELLOW);
			}
		}
		return this;
	}
	
	protected abstract void getControllers(List<BuildController> gamepads);
	
	public int getControllers()
	{
		return gamepads.size();
	}

	public boolean isValidDevice(int deviceIndex)
	{
		return gamepads.size() > 0 && deviceIndex >= 0 && deviceIndex < gamepads.size();
	}

	public String getControllerName(int num)
	{
		return gamepads.get(num).getName();
	}
	
	public void setDeadZone(float value)
	{
		this.deadZone = value;
	}
	
	public boolean buttonPressed()
	{
		for(int i = 0; i < gamepads.size(); i++) {
			if(gamepads.get(i).buttonPressed())
				return true;
		}
		return false;
	}
	
	public int getButtonCount(int num)
	{
		int size = getControllers();
		if(size > 0 && size > num)
			return gamepads.get(num).getButtonCount();
		return 0;
	}

	public void handler()
	{
		for(int i = 0; i < gamepads.size(); i++)
			gamepads.get(i).update();
	}
	
	public void resetButtonStatus()
	{
		for(int i = 0; i < gamepads.size(); i++) {
			gamepads.get(i).resetButtonStatus();	
		}
	}
	
	public boolean buttonStatusOnce(int deviceIndex, int buttonCode)
	{
		return gamepads.get(deviceIndex).buttonStatusOnce(buttonCode);
	}
	
	public boolean buttonPressed(int deviceIndex, int buttonCode)
	{
		return gamepads.get(deviceIndex).buttonPressed(buttonCode);
	}
	
	public boolean buttonStatus(int deviceIndex, int buttonCode)
	{
		return gamepads.get(deviceIndex).buttonStatus(buttonCode);
	}

	public Vector2 getStickValue(int deviceIndex, int aCode1, int aCode2)
	{
		// TODO
		// how come we are looping through an array in getAxisValue while it's single player ?
		// there should a parameter indicating which player pad is desired
		return gamepads.get(deviceIndex).getStickValue(aCode1, aCode2, deadZone);
	}
}
