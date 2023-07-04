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

package ru.m210projects.Build.desktop.Controllers;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.badlogic.gdx.controllers.ControlType;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Architecture.BuildController;

public class GdxController implements BuildController {

	private final PovDirection[] directions = {
		PovDirection.north,
		PovDirection.south,
		PovDirection.west,
		PovDirection.east,
		PovDirection.northWest, //up left
		PovDirection.northEast, //up rigth
		PovDirection.southWest, //down left
		PovDirection.southEast, //down rigth
	};
	
	protected Controller controller;
	protected String controllerName;
	protected boolean[] buttonStatus;
	protected boolean[] hitButton;
	protected int buttonsNum;
	protected int axisNum;
	protected int povNum;
	protected int allButtonsCount;
	protected boolean buttonPressed = false;
	protected Vector2 stickVector = new Vector2();

	public GdxController(int deviceIndex) throws Exception
	{
		this.controller = Controllers.getControllers().get(deviceIndex);
		
		Method controlCount = controller.getClass().getMethod("getControlCount", ControlType.class);  
		controlCount.setAccessible(true);
		buttonsNum = (Integer) controlCount.invoke(controller, ControlType.button);
		axisNum = (Integer) controlCount.invoke(controller, ControlType.axis);
		povNum = (Integer) controlCount.invoke(controller, ControlType.pov);

		controllerName = controller.getName();
		allButtonsCount = buttonsNum + povNum * 4 + ((axisNum > 3) ? 2 : 0); //4 - buttons in DPad(pov) and 2 - triggers (axis 4)
		buttonStatus = new boolean[allButtonsCount];
		hitButton = new boolean[allButtonsCount];
	}
	
	@Override
	public boolean buttonPressed()
	{
		return buttonPressed;
	}

	@Override
	public void resetButtonStatus()
	{
		Arrays.fill(buttonStatus, false);
	}

	@Override
	public boolean buttonPressed(int buttonCode)
	{
		if(buttonCode >= 0 && buttonCode < allButtonsCount)
			return hitButton[buttonCode];
		
		return false;
	}
	
	@Override
	public boolean buttonStatusOnce(int buttonCode)
	{
		if(buttonCode >= 0 && buttonCode < allButtonsCount && buttonStatus[buttonCode]) {
			buttonStatus[buttonCode] = false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean buttonStatus(int buttonCode)
	{
		return buttonCode >= 0 && buttonCode < allButtonsCount && buttonStatus[buttonCode];
	}
	
	@Override
	public int getButtonCount()
	{
		return allButtonsCount;
	}
	
	@Override
	public int getAxisCount()
	{
		return axisNum;
	}
	
	@Override
	public int getPovCount()
	{
		return povNum;
	}

	@Override
	public Vector2 getStickValue(int aCode1, int aCode2, float deadZone)
	{
		float lx = controller.getAxis(aCode1);
		float ly = controller.getAxis(aCode2);
		float mag = (float) Math.sqrt(lx*lx + ly*ly);
		float nlx = lx / mag;
		float nly = ly / mag;
		float nlm;
		if (mag > deadZone)
		{
			if (mag > 1.0f)
				mag = 1.0f;

			mag -= deadZone;
			nlm = mag / (1.0f - deadZone);
			float x1 = nlx * nlm;
			float y1 = nly * nlm;
			return stickVector.set(x1, y1);
		}
		else
		{
			return stickVector.set(0.0f, 0.0f);
		}
	}
	
	@Override
	public String getName() {
		return controllerName;
	}
	
	private void TriggerHandler()
	{
		if(axisNum < 4) return;
		
		float value = controller.getAxis(4);
		int num = buttonsNum + (4 * povNum);
		
		if(value >= 0.9f) {
			buttonPressed = true;
			if(!hitButton[num]) {
				getInput().setKey(ANYKEY, 1);
				buttonStatus[num] = true;
				hitButton[num] = true;
			}
		} else {
			buttonStatus[num] = false;
			hitButton[num] = false;
		}
		
		if(value <= -0.9f) {
			buttonPressed = true;
			if(!hitButton[num + 1]) {
				getInput().setKey(ANYKEY, 1);
				buttonStatus[num + 1] = true;
				hitButton[num + 1] = true;
			}
		} else {
			buttonStatus[num + 1] = false;
			hitButton[num + 1] = false;
		}
	}

	private void DPADHandler()
	{
		for(int i = 0; i < povNum; i++)
		{
			PovDirection dir = controller.getPov(i);
			if (dir != null && dir != PovDirection.center) 
			{
				int num = buttonsNum + (4 * i);
				for(int d = 0; d < 4; d++) 
				{
					if(dir == directions[d])
					{
						buttonPressed = true;
						if(!hitButton[num + d]) {
							getInput().setKey(ANYKEY, 1);
							buttonStatus[num + d] = true;
							hitButton[num + d] = true;
						}
					} else {
						buttonStatus[num + d] = false;
						hitButton[num + d] = false;
					}
				}

				for(int d = 0; d < 4; d++) 
				{
					int fbut = num + d / 2; //up down
					int sbut = (num + 2) + d % 2; //left right
					if(dir == directions[d + 4])
					{
						getInput().setKey(ANYKEY, 1);
						buttonStatus[fbut] = true;
						hitButton[fbut] = true;
						
						buttonStatus[sbut] = true;
						hitButton[sbut] = true;
					}
				}
			} else {
				for(int b = 0; b < 4; b++) {
					int num = buttonsNum + (4 * i) + b;
					buttonStatus[num] = false;
					hitButton[num] = false;
				}
			}
		}
	}

	@Override
	public void update() {
		buttonPressed = false;
		DPADHandler();
		TriggerHandler();
		for(int i = 0; i < buttonsNum; i++)
		{
			if (controller.getButton(i)) {
				buttonPressed = true;
				if (!hitButton[i]) {
					getInput().setKey(ANYKEY, 1);
					buttonStatus[i] = true;
					hitButton[i] = true;
				}
			} else {
				buttonStatus[i] = false;
				hitButton[i] = false;
			}
		}
	}
}
