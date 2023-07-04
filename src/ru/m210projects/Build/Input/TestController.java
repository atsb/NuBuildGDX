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

package ru.m210projects.Build.Input;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;

import java.util.Arrays;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildController;

public class TestController implements BuildController {
	
	protected String controllerName;
	protected boolean[] buttonStatus;
	protected boolean[] hitButton;
	protected int buttonsNum;
	protected int allButtonsCount;
	protected boolean buttonPressed = false;
	protected Vector2 stickVector = new Vector2();

	public TestController() {
		buttonsNum = 10;

		controllerName = "Test controller";
		allButtonsCount = buttonsNum + 4 + 2;
		buttonStatus = new boolean[allButtonsCount];
		hitButton = new boolean[allButtonsCount];
	}

	@Override
	public Vector2 getStickValue(int aCode1, int aCode2, float deadZone)
	{
		float lx = getAxis(aCode1);
		float ly = getAxis(aCode2);
		float mag = (float) Math.sqrt(lx*lx + ly*ly);
		float nlx = lx / mag;
		float nly = ly / mag;
		float nlm = 0.0f;
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
			mag = 0.0f;
			nlm = 0.0f;
			return stickVector.set(0.0f, 0.0f);
		}
	}

	@Override
	public void update() {
		buttonPressed = false;
		for(int i = 0; i < buttonsNum; i++)
		{
			if (getTestButton(i)) {
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
	
	private boolean getTestButton(int i)
	{
		if(i == 0 && BuildGdx.input.isKeyPressed(Keys.K))
			return true;
		return i == 1 && BuildGdx.input.isKeyPressed(Keys.L);
	}
	
	private float getAxis(int value) {
		if(BuildGdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			value -= 2;
		
		if(value == 0) {
			if(BuildGdx.input.isKeyPressed(Keys.UP))
				return -1.0f;
			
			if(BuildGdx.input.isKeyPressed(Keys.DOWN))
				return 1.0f;
		}
		
		if(value == 1) {
			if(BuildGdx.input.isKeyPressed(Keys.LEFT))
				return -1.0f;
			
			if(BuildGdx.input.isKeyPressed(Keys.RIGHT))
				return 1.0f;
		}
		
		return 0;
	}

	@Override
	public boolean buttonPressed()
	{
		return buttonPressed;
	}

	@Override
	public boolean buttonStatus(int buttonCode)
	{
		return buttonCode >= 0 && buttonCode < allButtonsCount && buttonStatus[buttonCode];
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
	public int getButtonCount()
	{
		return allButtonsCount;
	}
	
	@Override
	public int getAxisCount()
	{
		return 1;
	}
	
	@Override
	public int getPovCount()
	{
		return 0;
	}

	@Override
	public String getName() {
		return controllerName;
	}
	
}
