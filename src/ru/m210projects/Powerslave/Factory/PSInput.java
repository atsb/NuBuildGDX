// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Factory;

import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Powerslave.Type.Input;
import ru.m210projects.Powerslave.Type.PlayerStruct;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.View.*;

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Powerslave.Config.PsKeys;



public class PSInput extends BuildControls {

	public static final int nJump = 1;
	public static final int nPause = 1 << 1;
	public static final int nOpen = 1 << 2;
	public static final int nFire = 1 << 3;
	public static final int nCrouch = 1 << 4;
	public static final int nLookUp = 1 << 5;
	public static final int nLookDown = 1 << 6;
	public static final int nItemLeft = 1 << 7;
	public static final int nItemRight = 1 << 8;
	public static final int nItemUse = 1 << 9;
	public static final int nRun = 1 << 10;
	public static final int nTurnAround = 1 << 11;
	public static final int nAimCenter = 1 << 12;

	// 1 << 13 - 1 << 16 nWeapons

	public static final int nAimUp = 1 << 17;
	public static final int nAimDown = 1 << 18;
	
	public static final int nItemHand = 1 << 19;
	public static final int nItemEye = 1 << 20;
	public static final int nItemMask = 1 << 21;
	public static final int nItemHeart = 1 << 22;
	public static final int nItemTorch = 1 << 23;
	public static final int nItemScarab = 1 << 24;
	
	private int lPlayerXVel, lPlayerYVel;

	public PSInput(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
	}

	@Override
	public float getScaleX() {
		return 1 / 8.0f;
	}

	@Override
	public float getScaleY() {
		return 0.5f;
	}

	private short vel, svel;
	private float horiz, angvel;

	public final int NORMALKEYMOVE = 40;
	public final int MAXVEL = (NORMALKEYMOVE * 2);

	@Override
	public void ctrlGetInput(NetInput input) {
		Input gInput = (Input) input;

		gInput.Reset();
		svel = vel = 0;
		horiz = angvel = 0;

		gInput.bits |= ctrlGetInputKey(GameKeys.Open, true) ? nOpen : 0;

		if (PlayerList[nLocalPlayer].HealthAmount == 0) {
			lPlayerYVel = 0;
	        lPlayerXVel = 0;
			return;
		}
		
		if ( Console.IsShown() || game.menu.gShowMenu )
			 return;

		if (ctrlGetInputKey(GameKeys.Mouse_Aiming, true)) {
			cfg.gMouseAim = !cfg.gMouseAim;
			StatusMessage(500, "Mouse aiming " + (cfg.gMouseAim ? "ON" : "OFF"), nLocalPlayer);
		}

		gInput.bits |= ctrlGetInputKey(GameKeys.Jump, false) ? nJump : 0;
		gInput.bits |= ctrlKeyStatusOnce(KEY_PAUSE) ? nPause : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Weapon_Fire, false) ? nFire : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Crouch, false) ? nCrouch : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Look_Up, false) ? nLookUp : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Look_Down, false) ? nLookDown : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Aim_Up, false) ? nAimUp : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Aim_Down, false) ? nAimDown : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Aim_Center, false) ? nAimCenter : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Left, true) ? nItemLeft : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Right, true) ? nItemRight : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Use, true) ? nItemUse : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Run, false) ? nRun : 0;
		gInput.bits |= ctrlGetInputKey(GameKeys.Turn_Around, false) ? nTurnAround : 0;
		
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Hand, true) ? nItemHand : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Eye, true) ? nItemEye : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Mask, true) ? nItemMask : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Heart, true) ? nItemHeart : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Torch, true) ? nItemTorch : 0;
		gInput.bits |= ctrlGetInputKey(PsKeys.Inventory_Scarab, true) ? nItemScarab : 0;

		PlayerStruct p = PlayerList[myconnectindex];

		if ((gInput.bits & nCrouch) != 0)
			p.crouch_toggle = false;

		boolean bUnderwater = (SectFlag[nPlayerViewSect[myconnectindex]] & 0x2000) != 0;
		boolean CrouchMode = ctrlGetInputKey(PsKeys.Crouch_toggle, !bUnderwater);
		if (bUnderwater) {
			p.crouch_toggle = CrouchMode;
		} else if (CrouchMode)
			p.crouch_toggle = !p.crouch_toggle;

		if (p.crouch_toggle)
			gInput.bits |= nCrouch;

		for (int i = 0; i < 7; i++)
			if (ctrlGetInputKey(cfg.keymap[i + PsKeys.Weapon_1.getNum()], true))
				gInput.bits |= (i + 1) << 13; // 8192
		if (ctrlGetInputKey(GameKeys.Previous_Weapon, true))
			gInput.bits |= (8) << 13; // 65536
		if (ctrlGetInputKey(GameKeys.Next_Weapon, true))
			gInput.bits |= (9) << 13; // 73728
		if (ctrlGetInputKey(PsKeys.Last_Used_Weapon, true))
			gInput.bits |= (10) << 13; // 81920

		int keymove = 40;
		int keyturn = 8;

		boolean running = ((!cfg.gAutoRun && (gInput.bits & nRun) != 0) || ((gInput.bits & nRun) == 0 && cfg.gAutoRun));

		if (running) {
			keymove = 80;
			keyturn = 12;
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
		float looky = stick1.y;
		if (cfg.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 8.0f;
			horiz = BClipRange(horiz - k * looky * cfg.gJoyLookSpeed / 65536f, -(ydim >> 1), 100 + (ydim >> 1));
		}

		if (lookx != 0) {
			float k = 8.0f;
			angvel = BClipRange(angvel + k * lookx * cfg.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0)
			vel = (short) BClipRange(vel - (keymove * stick2.y), -4 * keymove, 4 * keymove);
		if (stick2.x != 0)
			svel = (short) BClipRange(svel - (keymove * stick2.x), -4 * keymove, 4 * keymove);

		angvel = BClipRange(angvel, -127, 127);
		horiz = BClipRange(horiz, -127, 127);

		if (followmode && nOverhead != 0) {
			followvel = vel;
			followsvel = svel;
			followang = angvel;
			return;
		}
		
		float k = 0.7f;
		float angle = BClampAngle(inita);
		
		double xvel = (vel * BCosAngle(angle) + svel * BSinAngle(angle));
		double yvel = (vel * BSinAngle(angle) - svel * BCosAngle(angle));
		double len = Math.sqrt(xvel * xvel + yvel * yvel);
		if(len > (MAXVEL << 14))
		{
			xvel = (xvel / len) * (MAXVEL << 14);
			yvel = (yvel / len) * (MAXVEL << 14);
		}
		
		lPlayerXVel += xvel * k;
		lPlayerYVel += yvel * k;

		lPlayerXVel = mulscale(lPlayerXVel, 0xD000, 16);
		lPlayerYVel = mulscale(lPlayerYVel, 0xD000, 16);

		if( klabs(lPlayerXVel) < 2048 && klabs(lPlayerYVel) < 2048 )
			lPlayerXVel = lPlayerYVel = 0;

		gInput.xvel = lPlayerXVel;
		gInput.yvel = lPlayerYVel;
		gInput.avel = angvel;
		gInput.horiz = horiz;
		gInput.nTarget = besttarget;
		Ra[nLocalPlayer].nTarget = besttarget;
		gInput.nWeaponAim = (short) p.horiz;
	}

}
