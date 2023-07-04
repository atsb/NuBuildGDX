package ru.m210projects.Tekwar.Factory;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Config.TOGGLE_HEALTH;
import static ru.m210projects.Tekwar.Config.TOGGLE_INVENTORY;
import static ru.m210projects.Tekwar.Config.TOGGLE_REARVIEW;
import static ru.m210projects.Tekwar.Config.TOGGLE_SCORE;
import static ru.m210projects.Tekwar.Config.TOGGLE_TIME;
import static ru.m210projects.Tekwar.Config.TOGGLE_UPRT;
import static ru.m210projects.Tekwar.Globals.GUN1FLAG;
import static ru.m210projects.Tekwar.Globals.GUN2FLAG;
import static ru.m210projects.Tekwar.Globals.GUN3FLAG;
import static ru.m210projects.Tekwar.Globals.GUN4FLAG;
import static ru.m210projects.Tekwar.Globals.GUN5FLAG;
import static ru.m210projects.Tekwar.Globals.GUN6FLAG;
import static ru.m210projects.Tekwar.Globals.GUN7FLAG;
import static ru.m210projects.Tekwar.Globals.GUN8FLAG;
import static ru.m210projects.Tekwar.Globals.MAXAMMO;
import static ru.m210projects.Tekwar.Globals.ST_IMMEDIATE;
import static ru.m210projects.Tekwar.Globals.TICSPERFRAME;
import static ru.m210projects.Tekwar.Names.S_HEALTHMONITOR;
import static ru.m210projects.Tekwar.Names.S_REARMONITOR;
import static ru.m210projects.Tekwar.Names.S_STATUS1;
import static ru.m210projects.Tekwar.Player.gPlayer;
import static ru.m210projects.Tekwar.Tekchng.changehealth;
import static ru.m210projects.Tekwar.Tekgun.*;
import static ru.m210projects.Tekwar.Tekmap.*;
import static ru.m210projects.Tekwar.Teksnd.playsound;
import static ru.m210projects.Tekwar.Tektag.flags32;
import static ru.m210projects.Tekwar.View.*;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Tekwar.Config.TekKeys;
import ru.m210projects.Tekwar.Main.UserFlag;
import ru.m210projects.Tekwar.Types.Input;

public class TekControl extends BuildControls {

	private int vel, svel;
	private float horiz, angvel;

	public TekControl(BuildConfig cfg, BuildControllers gpmanager) {
		super(cfg, gpmanager);
	}

	@Override
	public void ctrlGetInput(NetInput input) {
		Input gInput = (Input) input;

		if (Console.IsShown())
			return;

		gInput.Reset();
//		vel = svel = 0;
		horiz = angvel = 0;

		if (game.menu.gShowMenu)
			return;

		tekcheatkeys(gInput);

		// normal game keys active
		for (int i = 0; i < 8; i++)
			if (ctrlGetInputKey(tekcfg.keymap[i + TekKeys.Weapon_1.getNum()], true)
					&& tekhasweapon(i, myconnectindex) != 0)
				gInput.selectedgun = i;
		
		if (ctrlGetInputKey(GameKeys.Next_Weapon, true) && mission != 7) {
			int gun = changegun(myconnectindex, 1);
			if(gun != -1) gInput.selectedgun = gun;
		}

		if (ctrlGetInputKey(GameKeys.Previous_Weapon, true) && mission != 7) {
			int gun = changegun(myconnectindex, -1);
			if(gun != -1) gInput.selectedgun = gun;
		}

		if (ctrlGetInputKey(GameKeys.Map_Toggle, true))
			tekmapmode(gViewMode);

		boolean strafe = ctrlGetInputKey(GameKeys.Strafe, false);

		// keyboard survey - use to be keytimerstuff() called from keyhandler
		if (!strafe) { // Les 09/28/95
			if (ctrlGetInputKey(GameKeys.Turn_Left, false))
				angvel = max(angvel - 100 * TICSPERFRAME, -128); // Les 09/28/95
			if (ctrlGetInputKey(GameKeys.Turn_Right, false))
				angvel = min(angvel + 100 * TICSPERFRAME, 127); // Les 09/28/95

		} else { // Les 09/28/95
			if (ctrlGetInputKey(GameKeys.Turn_Left, false))
				svel = min(svel + 8 * TICSPERFRAME, 127); // Les 09/28/95
			if (ctrlGetInputKey(GameKeys.Turn_Right, false))
				svel = max(svel - 8 * TICSPERFRAME, -128); // Les 09/28/95
		}

		if (ctrlGetInputKey(GameKeys.Move_Forward, false))
			vel = min(vel + 8 * TICSPERFRAME, 127); // Les 09/28/95
		if (ctrlGetInputKey(GameKeys.Move_Backward, false))
			vel = max(vel - 8 * TICSPERFRAME, -128); // Les 09/28/95
		if (!tekcfg.gMouseAim) {
			float move = ctrlGetMouseMove();
			if (move != 0)
				vel = (byte) BClipRange(vel - move, -128, 127);
		}

		if (ctrlGetInputKey(GameKeys.Strafe_Left, false))
			svel = min(svel + 8 * TICSPERFRAME, 127); // Les 09/28/95
		if (ctrlGetInputKey(GameKeys.Strafe_Right, false))
			svel = max(svel - 8 * TICSPERFRAME, -128); // Les 09/28/95

		Vector2 stick1 = ctrlGetStick(JoyStick.Turning);
		Vector2 stick2 = ctrlGetStick(JoyStick.Moving);

		float lookx = stick1.x;
		float looky = stick1.y;
		if (tekcfg.gJoyInvert)
			looky *= -1;

		if (looky != 0) {
			float k = 4.0f;
			horiz = BClipRange(horiz + k * looky * tekcfg.gJoyLookSpeed / 65536f, -(ydim >> 1), 100 + (ydim >> 1));
		}

		if (lookx != 0) {
			float k = 80.0f;
			angvel = (short) BClipRange(gInput.angvel + k * lookx * tekcfg.gJoyTurnSpeed / 65536f, -1024, 1024);
		}

		if (stick2.y != 0) {
			float k = 8 * TICSPERFRAME;
			vel = (short) BClipRange(vel - (k * stick2.y), -128, 127);
		}
		if (stick2.x != 0) {
			float k = 8 * TICSPERFRAME;
			svel = (short) BClipRange(svel - (k * stick2.x), -128, 127);
		}

		if (angvel < 0)
			angvel = min(angvel + 12 * TICSPERFRAME, 0);
		if (angvel > 0)
			angvel = max(angvel - 12 * TICSPERFRAME, 0);
		if (svel < 0)
			svel = min(svel + 2 * TICSPERFRAME, 0);
		if (svel > 0)
			svel = max(svel - 2 * TICSPERFRAME, 0);
		if (vel < 0)
			vel = min(vel + 2 * TICSPERFRAME, 0);
		if (vel > 0)
			vel = max(vel - 2 * TICSPERFRAME, 0);

		gInput.vel = (byte) BClipRange(vel, -128 + 8, 127 - 8);
		gInput.svel = (short) BClipRange(svel, -128 + 8, 127 - 8);
		gInput.angvel = BClipRange(angvel, -512 + 16, 511 - 16);

		if (tekcfg.gMouseAim)
			gInput.mlook = BClipRange(horiz + ctrlGetMouseLook(tekcfg.gInvertmouse), -512, 511);

		if (!strafe)
			gInput.angvel = (short) BClipRange(gInput.angvel + ctrlGetMouseTurn(), -1024, 1024);
		else
			svel = BClipRange(svel - (int) ctrlGetMouseStrafe(), -128, 127);

		gInput.Jump = ctrlGetInputKey(GameKeys.Jump, false);
		gInput.Crouch = ctrlGetInputKey(GameKeys.Crouch, false);
		gInput.Look_Up = ctrlGetInputKey(GameKeys.Look_Up, false);
		gInput.Look_Down = ctrlGetInputKey(GameKeys.Look_Down, false);
		gInput.Center = ctrlGetInputKey(TekKeys.Aim_Center, true);
		gInput.Holster_Weapon = ctrlGetInputKey(TekKeys.Holster_Weapon, true);
		gInput.Run = ctrlGetInputKey(GameKeys.Run, false);
		gInput.Use = ctrlGetInputKey(GameKeys.Open, true);
		gInput.Fire = ctrlGetInputKey(GameKeys.Weapon_Fire, false);

		// overhead maps zoom in/out
		if (gViewMode != 3) {
			if (ctrlGetInputKey(TekKeys.Zoom_Out, false) && (zoom > 48))
				zoom -= (zoom >> 4);
			if (ctrlGetInputKey(TekKeys.Zoom_In, false) && (zoom < 4096))
				zoom += (zoom >> 4);
		}

		if (ctrlGetInputKey(TekKeys.Make_Screenshot, true)) {

			String name = "scrxxxx.png";
			if (mUserFlag == UserFlag.UserMap)
				name = "scr-" + FileUtils.getFullName(boardfilename) + "-xxxx.png";
			else
				name = "scr-mission" + mission + "-xxxx.png";

			String filename = engine.screencapture(name);
			if (filename != null)
				showmessage(filename + " saved");
			else
				showmessage("Screenshot not saved. Access denied!");
		}

		// if typing mode reset kbrd fifo
		if (ctrlGetInputKey(GameKeys.Send_Message, false)) {
//			typemode = 1;
			getInput().initMessageInput(null);
		}

		tekprivatekeys(gInput);
	}

	public void tekcheatkeys(Input gInput) {
		if (getInput().keyStatus(Keys.SHIFT_LEFT) && getInput().keyStatus(Keys.ALT_LEFT)) {
			if (getInput().keyStatusOnce(Keys.G))
				gPlayer[myconnectindex].godMode = !gPlayer[myconnectindex].godMode;

			if (getInput().keyStatusOnce(Keys.W)) {
				for (int i = 0; i < 8; i++)
					gPlayer[myconnectindex].ammo[i] = MAXAMMO;
				gPlayer[myconnectindex].invredcards = 1;
				gPlayer[myconnectindex].invbluecards = 1;
				gPlayer[myconnectindex].invaccutrak = 1;
				gPlayer[myconnectindex].weapons = (flags32[GUN1FLAG] | flags32[GUN2FLAG] | flags32[GUN3FLAG]
						| flags32[GUN4FLAG]);
				gPlayer[myconnectindex].weapons |= (flags32[GUN5FLAG] | flags32[GUN6FLAG] | flags32[GUN7FLAG]
						| flags32[GUN8FLAG]);
			}
			if (getInput().keyStatusOnce(Keys.H))
				changehealth(myconnectindex, 200);
			if (getInput().keyStatusOnce(Keys.J)) {
				symbols[0] = true;
				symbols[1] = true;
				symbols[2] = true;
				symbols[3] = true;
				symbols[4] = true;
				symbols[5] = true;
				symbols[6] = true;
			}
		}
	}

	public int tekprivatekeys(Input gInput) {
		if (ctrlGetInputKey(TekKeys.Toggle_Crosshair, true)) {
			tekcfg.gCrosshair = !tekcfg.gCrosshair;
		}
		if (ctrlGetInputKey(TekKeys.Elapsed_Time, true)) {
			tekcfg.toggles[TOGGLE_TIME] = !tekcfg.toggles[TOGGLE_TIME];
		}
		if (ctrlGetInputKey(TekKeys.Score, true)) {
			tekcfg.toggles[TOGGLE_SCORE] = !tekcfg.toggles[TOGGLE_SCORE];
		}
		if (ctrlGetInputKey(TekKeys.Rearview, true)) {
			tekcfg.toggles[TOGGLE_REARVIEW] = !tekcfg.toggles[TOGGLE_REARVIEW];
			playsound(S_REARMONITOR, 0, 0, 0, ST_IMMEDIATE);
			rvmoving = 1;
		}
		if (ctrlGetInputKey(TekKeys.Prepared_Item, true)) {
			tekcfg.toggles[TOGGLE_UPRT] = !tekcfg.toggles[TOGGLE_UPRT];
			playsound(S_STATUS1 + (tekcfg.toggles[TOGGLE_UPRT] ? 1 : 0), 0, 0, 0, ST_IMMEDIATE);
			wpmoving = 1;
		}
		if (ctrlGetInputKey(TekKeys.Health_Meter, true)) {
			tekcfg.toggles[TOGGLE_HEALTH] = !tekcfg.toggles[TOGGLE_HEALTH];
			playsound(S_HEALTHMONITOR, 0, 0, 0, ST_IMMEDIATE);
			hcmoving = 1;
		}
		if (ctrlGetInputKey(TekKeys.Inventory, true)) {
			tekcfg.toggles[TOGGLE_INVENTORY] = !tekcfg.toggles[TOGGLE_INVENTORY];
		}

		if (ctrlGetInputKey(GameKeys.Mouse_Aiming, true)) {
			if (!tekcfg.gMouseAim) {
				tekcfg.gMouseAim = true;
				showmessage("Mouse aiming on");
			} else {
				tekcfg.gMouseAim = false;
				gInput.Center = true;
				showmessage("Mouse aiming off");
			}
		}

		return 0;
	}

}
