// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Factory;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.LSP.Config.LSPKeys;
import ru.m210projects.LSP.Types.Input;

public class LSPInput extends BuildControls {

	private int vel, svel;
	private float horiz, angvel;
	private boolean crouch;
	private int lPlayerXVel, lPlayerYVel;

	public LSPInput(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
	}
	
	public void reset()
	{
		lPlayerXVel = lPlayerYVel = 0;
	}
	
	@Override
	public float getScaleX()
	{
		return 1.0f;
	}

	@Override
	public void ctrlGetInput(NetInput input) {
		Input gInput = (Input) input;

		if (game.menu.gShowMenu || Console.IsShown())
			return;

		gInput.Reset();
		svel = vel = 0;
		horiz = angvel = 0;

		gInput.bits |= ctrlGetInputKey(GameKeys.Jump, true) ? 1 : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Crouch, false) ? 2 : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Look_Up, false) ? 4 : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Look_Down, false) ? 8 : 0;
		//16, 32 - zoom in, out
		
		gInput.bits |= ctrlGetInputKey(GameKeys.Run, false) ? 256 : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Open, true) ? 1024 : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Weapon_Fire, false) ? 2048 : 0;
		gInput.bits |= ctrlGetInputKey(LSPKeys.Weapon_1, true) ? 1 << 14 : 0;
		gInput.bits |= ctrlGetInputKey(LSPKeys.Weapon_2, true) ? 2 << 14 : 0;
		gInput.bits |= ctrlGetInputKey(LSPKeys.Weapon_3, true) ? 3 << 14 : 0;
		if (ctrlGetInputKey(GameKeys.Previous_Weapon, true))
			gInput.bits |= (4) << 14;
		if (ctrlGetInputKey(GameKeys.Next_Weapon, true))
			gInput.bits |= (5) << 14;
		if (ctrlGetInputKey(LSPKeys.Last_Used_Weapon, true))
			gInput.bits |= (6) << 14;
		gInput.bits |=   ctrlKeyStatusOnce(KEY_PAUSE)? 1 << 17 : 0;
		if(ctrlGetInputKey(LSPKeys.Crouch_toggle, true))
			crouch = !crouch;
		
		if(crouch)
			gInput.bits |= 2;
		
		int keymove = 20 * TICSPERFRAME;
		int keyturn = 32 * TICSPERFRAME;
		
		boolean running = ((!cfg.gAutoRun && (gInput.bits & 256) != 0) || ((gInput.bits & 256) == 0 && cfg.gAutoRun));

		if (running) {
			keymove = 32 * TICSPERFRAME;
			keyturn = 48 * TICSPERFRAME;
		}
		
		if (ctrlGetInputKey(GameKeys.Strafe, false)) {
			if (ctrlGetInputKey(GameKeys.Turn_Left, false))
				svel -= -keymove;
			if (ctrlGetInputKey(GameKeys.Turn_Right, false))
				svel -= keymove;
			svel = (short) BClipRange(svel - (int) 20 * ctrlGetMouseStrafe(), -keymove, keymove);
		} else {
			if (ctrlGetInputKey(GameKeys.Turn_Left, false)) {
				angvel -= keyturn;
			} else if (ctrlGetInputKey(GameKeys.Turn_Right, false)) {
				angvel += keyturn;
			}
			angvel = BClipRange(angvel + ctrlGetMouseTurn(), -1024, 1024);
		}

		if (ctrlGetInputKey(GameKeys.Strafe_Left, false))
			svel += keymove;
		if (ctrlGetInputKey(GameKeys.Strafe_Right, false))
			svel += -keymove;
		if (ctrlGetInputKey(GameKeys.Move_Forward, false))
			vel += keymove;
		if (ctrlGetInputKey(GameKeys.Move_Backward, false))
			vel += -keymove;

		if (cfg.gMouseAim) {
			horiz = BClipRange(ctrlGetMouseLook(!cfg.gInvertmouse), -(ydim >> 1), 100 + (ydim >> 1));
		} else
			vel = (short) BClipRange(vel - ctrlGetMouseMove(), -4 * keymove, 4 * keymove);
	
		Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);

		float lookx = stick1.x;
		float looky = -stick1.y;
		
		if (cfg.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 4.0f;
			horiz = BClipRange(horiz + k * looky * cfg.gJoyLookSpeed / 65536f, -(ydim >> 1), 100 + (ydim >> 1));
		}

		if (lookx != 0) {
			float k = 80.0f;
			angvel = (short) BClipRange(angvel + k * lookx * cfg.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0) {
			float k = 80 * TICSPERFRAME;
			vel = (short) BClipRange(vel - (k * stick2.y), -128, 127);
		}
		if (stick2.x != 0) {
			float k = 80 * TICSPERFRAME;
			svel = (short) BClipRange(svel - (k * stick2.x), -128, 127);
		}

		if (svel < 0)
			svel = min(svel + 2 * TICSPERFRAME, 0);
		if (svel > 0)
			svel = max(svel - 2 * TICSPERFRAME, 0);
		if (vel < 0)
			vel = min(vel + 2 * TICSPERFRAME, 0);
		if (vel > 0)
			vel = max(vel - 2 * TICSPERFRAME, 0);
		
		if (followmode && gPlayer[myconnectindex].gViewMode != 3) {
			followvel = vel;
			followsvel = svel;
			followang = angvel;
			return;
		}
		
		float k = 0.17f;
		float angle = BClampAngle(gPlayer[myconnectindex].ang);
		
		double xvel = (vel * BCosAngle(angle) + svel * BSinAngle(angle));
		double yvel = (vel * BSinAngle(angle) - svel * BCosAngle(angle));

		double len = Math.sqrt(xvel * xvel + yvel * yvel);
		if(len > (keymove << 14))
		{
			xvel = (xvel / len) * (keymove << 14);
			yvel = (yvel / len) * (keymove << 14);
		}
		
		lPlayerXVel += xvel * k;
		lPlayerYVel += yvel * k;

		lPlayerXVel = mulscale(lPlayerXVel, 0xD000, 16);
		lPlayerYVel = mulscale(lPlayerYVel, 0xD000, 16);

		if( klabs(lPlayerXVel) < 2048 && klabs(lPlayerYVel) < 2048 )
			lPlayerXVel = lPlayerYVel = 0;

		gInput.xvel = lPlayerXVel;
		gInput.yvel = lPlayerYVel;
		gInput.angvel = angvel;
		gInput.horiz = horiz;
	}

}
