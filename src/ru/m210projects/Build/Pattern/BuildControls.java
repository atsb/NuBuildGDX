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

package ru.m210projects.Build.Pattern;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Settings.BuildConfig.*;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.Input.KeyInput;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.MenuKeys;

public abstract class BuildControls {

	public int oldPosX;
	public int oldPosY;
	public boolean[] maxisstatus;
	public Vector2 mouseMove;
	public Vector2 stick1;
	public Vector2 stick2;

	protected BuildControllers gpmanager;
	protected BuildConfig pCfg;

	public enum JoyStick { Turning, Moving };

	public BuildControls(BuildConfig cfg, BuildControllers gpmanager)
	{
		this.pCfg = cfg;
		this.gpmanager = gpmanager;
		this.gpmanager.setDeadZone(cfg.gJoyDeadZone / 65536f);
		this.maxisstatus = new boolean[cfg.keymap.length];
		this.mouseMove = new Vector2();
		this.stick1 = new Vector2();
		this.stick2 = new Vector2();
	}

	public float getScaleX()
	{
		return 4.0f;
	}

	public float getScaleY()
	{
		return 1 / 4.0f;
	}

	public void resetMousePos()
	{
		if(BuildGdx.app.isActive())
			BuildGdx.input.setCursorPosition(xdim / 2, ydim / 2);
		oldPosX = xdim / 2;
		oldPosY = ydim / 2;
	}

	public void ctrlMouseHandler()
	{
		mouseMove.set(0, 0);
		Arrays.fill(maxisstatus, false);

		if (pCfg.useMouse) {

			int dx = BuildGdx.input.getDeltaX(); //BuildGdx.input.getX() - oldPosX;
			int dy = BuildGdx.input.getDeltaY(); //BuildGdx.input.getY() - oldPosY;

			if(dx != 0) {
				if(dx > 0)
				{
					if(pCfg.mouseaxis[AXISRIGHT] != -1) {
						maxisstatus[pCfg.mouseaxis[AXISRIGHT]] = true;
					}
				} else {
					if(pCfg.mouseaxis[AXISLEFT] != -1) {
						maxisstatus[pCfg.mouseaxis[AXISLEFT]] = true;
					}
				}
			}

			if(dy != 0) {
				if(dy > 0)
				{
					if(pCfg.mouseaxis[AXISDOWN] != -1) {
						maxisstatus[pCfg.mouseaxis[AXISDOWN]] = true;
					}
				} else {
					if(pCfg.mouseaxis[AXISUP] != -1) {
						maxisstatus[pCfg.mouseaxis[AXISUP]] = true;
					}
				}
			}

			float sensscale = pCfg.gSensitivity / 65536.0f;

			float xscale = sensscale * getScaleX();
			float yscale = sensscale * getScaleY();

			mouseMove.set(dx * xscale, dy * yscale);

			resetMousePos();
		}
	}

	public float ctrlGetMouseMove()
	{
		return mouseMove.y * pCfg.gMouseMoveSpeed / 65536f;
	}

	public float ctrlGetMouseLook(boolean invert)
	{
		if(invert)
			return -mouseMove.y * pCfg.gMouseLookSpeed / 65536f;
		return mouseMove.y * pCfg.gMouseLookSpeed / 65536f;
	}

	public float ctrlGetMouseTurn()
	{
		return mouseMove.x * pCfg.gMouseTurnSpeed / 65536f;
	}

	public float ctrlGetMouseStrafe()
	{
		return mouseMove.x * pCfg.gMouseStrafeSpeed / 2097152f;
	}

	public void ctrlJoyHandler() {
		stick1.set(0, 0);
		stick2.set(0, 0);
		if(gpmanager.isValidDevice(pCfg.gJoyDevice)) {
			stick1.set(gpmanager.getStickValue(pCfg.gJoyDevice, pCfg.gJoyTurnAxis, pCfg.gJoyLookAxis));
			stick2.set(gpmanager.getStickValue(pCfg.gJoyDevice, pCfg.gJoyStrafeAxis, pCfg.gJoyMoveAxis));
		}
	}

	public Vector2 ctrlGetStick(JoyStick stick) {

		if(stick == JoyStick.Turning)
			return stick1;

		return stick2;
	}

	public boolean ctrlPadStatusOnce(KeyType buttonCode)
	{
		if(buttonCode instanceof MenuKeys)
			return gpmanager.isValidDevice(pCfg.gJoyDevice) && gpmanager.buttonStatusOnce(pCfg.gJoyDevice, pCfg.gJoyMenukeys[((MenuKeys) buttonCode).getJoyNum()]);

		return gpmanager.isValidDevice(pCfg.gJoyDevice) && gpmanager.buttonStatusOnce(pCfg.gJoyDevice, pCfg.gpadkeys[buttonCode.getNum()]);
	}

	public boolean ctrlPadStatus(KeyType buttonCode)
	{
		if(buttonCode instanceof MenuKeys)
			return gpmanager.isValidDevice(pCfg.gJoyDevice) && gpmanager.buttonStatus(pCfg.gJoyDevice, pCfg.gJoyMenukeys[((MenuKeys) buttonCode).getJoyNum()]);

		return gpmanager.isValidDevice(pCfg.gJoyDevice) && gpmanager.buttonStatus(pCfg.gJoyDevice, pCfg.gpadkeys[buttonCode.getNum()]);
	}

	public boolean ctrlAxisStatusOnce(int keyId)
	{
		if(keyId >= 0 && maxisstatus[keyId]) {
			maxisstatus[keyId] = false;
			return true;
		}
		return false;
	}

	public boolean ctrlAxisStatus(int keyId)
	{
		if(keyId >= 0 && maxisstatus[keyId])
			return true;

		return false;
	}

	public boolean ctrlKeyStatusOnce(int keyId)
	{
		return getInput().keyStatusOnce(keyId);
	}

	public boolean ctrlKeyStatus(int keyId)
	{
		return getInput().keyStatus(keyId);
	}

	public boolean ctrlKeyPressed(int keyId)
	{
		return getInput().keyPressed(keyId);
	}

	public boolean ctrlGetInputKey(KeyType keyName, boolean once) {
		final KeyInput input = getInput();
		final int key1 = pCfg.primarykeys[keyName.getNum()];
		final int key2 = pCfg.secondkeys[keyName.getNum()];
		final int keyM = pCfg.mousekeys[keyName.getNum()];

		if (once) {
			return input.keyStatusOnce(key1)
					|| input.keyStatusOnce(key2)
					|| input.keyStatusOnce(keyM)
					|| ctrlAxisStatusOnce(keyName.getNum())
					|| /* keyName > Turn_Right && */ctrlPadStatusOnce(keyName);
		} else {
			return input.keyStatus(key1)
					|| input.keyStatus(key2)
					|| input.keyStatus(keyM)
					|| ctrlAxisStatus(keyName.getNum())
					|| /* keyName > Turn_Right && */ctrlPadStatus(keyName);
		}
	}

	public void ctrlResetKeyStatus() {
		getInput().resetKeyStatus();
		gpmanager.resetButtonStatus();
	}

	public void ctrlResetInput() {
		ctrlResetKeyStatus();
		Arrays.fill(getInput().hitkey, false);
	}

	public boolean ctrlMenuMouse()
	{
		return pCfg.menuMouse;
	}

	public BuildControllers ctrlGetGamepadManager()
	{
		return gpmanager;
	}

	public void ctrlSetDeadZone(float value)
	{
		gpmanager.setDeadZone(value);
	}

	public boolean ctrlIsValidDevice(int deviceIndex)
	{
		return gpmanager.isValidDevice(deviceIndex) && gpmanager.getControllers() > 0;
	}

	public int ctrlGetControllers()
	{
		return gpmanager.getControllers();
	}

	public String ctrlGetControllerName(int num)
	{
		return gpmanager.getControllerName(num);
	}

	public abstract void ctrlGetInput(NetInput input);

}
