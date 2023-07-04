// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Types;

import java.util.Arrays;

import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Globals.kAngle120;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle60;
import static ru.m210projects.Blood.Globals.kAngle90;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Blood.VERSION.VER100;

public class DudeInfo {

	public static DudeInfo[] dudeInfo = {
		new DudeInfo(), // random dude not used

		// 201 tommy cultist v1.21 ok
		new DudeInfo(
				256,		// start sequence ID 
				40,			// start health
				70,			// mass
				120,
				48,			// clip distance
				41,			// eye above z
				20, 
				M2X(20), 	// hear distance
				M2X(100),	// seeing distance
				kAngle90,	// vision periphery
				0,			// melee distance
				10,			// flee health
				8,			// hinder damage
				0x0100,		// change target chance
				0x0010,		// change target to kin chance
				0x8000,		// alertChance
				true,		// lockout
				46603,		// frontSpeed
			    34952,		// sideSpeed
			    13981,		// backSpeed
			    256,		// angSpeed
			    new int[] { 15, -1, -1 },			// nGibType
				new int[] { 256, 256, 96, 256, 256, 256, 192 }
		),
		// 202 shotgun cultist v1.21 ok
		new DudeInfo(
			    720,		// start sequence ID
			    40,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    41,			// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    5,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    34952,		// frontSpeed
			    34952,		// sideSpeed
			    13981,		// backSpeed
			    256,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 128, 256, 256, 256, 192 } 
		),
		
		// 203 axe zombie v1.21 ok
		new DudeInfo(
			    272,		// start sequence ID
			    60,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    46,			// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 112, 256, 256, 256, 160 } 
		),
		
		// 204 fat zombie v1.21 ok
		new DudeInfo(
			    288,		// start sequence ID
			    80,			// start health
			    200,		// mass
			    120,
			    48,			// clip distance
			    128,		// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    23301,		// frontSpeed
			    23301,		// sideSpeed
			    13981,		// backSpeed
			    256,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 32, 128, 256, 64, 128 } 
		),
		
		// 205 earth zombie v1.21 ok
		new DudeInfo(
			    272,		// start sequence ID
			    60,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    46,			// eye above z
			    20,
			    M2X(10),	// hear distance
			    0,			// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 112, 256, 256, 256, 256 } 
		),
		
		// 206 flesh gargoyle v1.21 ok
		new DudeInfo(
			    304,		// start sequence ID
			    110,		// start health
			    120,		// mass
			    120,
			    64,			// clip distance
			    13,			// eye above z
			    5,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    25,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    46603,		// frontSpeed
			    34952,		// sideSpeed
			    23301,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 30,	-1, -1 },		// nGibType
			    new int[] { 0, 128, 48, 208, 256, 256, 256 } 
		),
		
		// 207 stone gargoyle v1.21 ok
		new DudeInfo(
			    320,		// start sequence ID
			    200,		// start health
			    200,		// mass
			    120,
			    84,			// clip distance
			    13,			// eye above z
			    5,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    20,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    46603,		// frontSpeed
			    34952,		// sideSpeed
			    23301,		// backSpeed
			    256,		// angSpeed
//			    0,
			    new int[] { 19,	-1, -1 },		// nGibType
			    new int[] { 0, 0, 10, 10, 0, 128, 64 } 
		),
		
		// 208 flesh statue v1.21 ok
		new DudeInfo(
			    688,		// start sequence ID
			    100,		// start health
			    200,		// mass
			    120,
			    64,			// clip distance
			    13,			// eye above z
			    5,
			    M2X(4),		// hear distance
			    M2X(10),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    0,			// angSpeed
//			    0,
			    new int[] { -1,	-1, -1 },		// nGibType
			    new int[] { 0, 0, 0, 0, 0, 0, 0 }
		),
		
		// 209 stone statue v1.21 ok
		new DudeInfo(
			    704,		// start sequence ID
			    100,		// start health
			    200,		// mass
			    120,
			    64,			// clip distance
			    13,			// eye above z
			    5,
			    M2X(4),		// hear distance
			    M2X(10),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    0,			// angSpeed
//			    0,
			    new int[] { -1,	-1, -1 },		// nGibType
			    new int[] { 0, 0, 0, 0, 0, 0, 0 }
		),
		
		// 210 Phantasm v1.21 ok
		new DudeInfo(
			    336,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    64,			// clip distance
			    25,			// eye above z
			    15,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,			// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { -1,	-1, -1 },		// nGibType
			    new int[] { 0, 0, 48, 0, 0, 16, 0 } 
		),
		
		// 211 hell hound v1.21 ok
		new DudeInfo(
			    352,		// start sequence ID
			    70,			// start health
			    120,		// mass
			    120,
			    80,			// clip distance
			    6,			// eye above z
			    0,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    20,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    116508,		// frontSpeed
			    81555,		// sideSpeed
			    69905,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 29,	-1, -1 },		// nGibType
			    new int[] { 48, 0, 48, 48, 256, 128, 192 } 
		),
		
		// 212 hand v1.21 ok
		new DudeInfo(
			    368,		// start sequence ID
			    10,			// start health
			    70,			// mass
			    120,
			    32,			// clip distance
			    0,			// eye above z
			    0,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 64, 256, 256, 256, 0, 64, 256 } 
		),
		
		// 213 brown spider v1.21 ok
		new DudeInfo(
			    384,		// start sequence ID
			    10,			// start health
			    5,			// mass
			    120,
			    32,			// clip distance
			    -5,			// eye above z
			    -5,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 64, 256, 256, 96, 256, 64, 256 } 
		),
		
		// 214 red spider v1.21 ok
		new DudeInfo(
			    400,		// start sequence ID
			    25,			// start health
			    10,			// mass
			    120,
			    32,			// clip distance
			    -5,			// eye above z
			    -5,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 64, 128, 256, 96, 256, 64, 256 } 
		),
		
		// 215 black spider v1.21 ok
		new DudeInfo(
			    416,		// start sequence ID
			    75,			// start health
			    20,			// mass
			    120,
			    32,			// clip distance
			    -5,			// eye above z
			    -5,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 128, 256, 256, 96, 256, 64, 256 } 
		),
		
		// 216 mother spider v1.21 ok
		new DudeInfo(
			    432,		// start sequence ID
			    100,		// start health
			    40,			// mass
			    120,
			    32,			// clip distance
			    -5,			// eye above z
			    -5,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 32, 16, 16, 16, 32, 32, 32 } 
		),
		
		// 217 gill beast v1.21 ok
		new DudeInfo(
			    448,		// start sequence ID
			    50,			// start health
			    200,		// mass
			    120,
			    64,			// clip distance
			    37,			// eye above z
			    20,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 48, 80, 64, 128, 0, 128, 48 } 
		),
		
		// 218 eel v1.21 ok
		new DudeInfo(
			    464,		// start sequence ID
			    25,			// start health
			    30,			// mass
			    120,
			    32,			// clip distance
			    4,			// eye above z
			    0,
			    M2X(10),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    34952,		// frontSpeed
			    23301,		// sideSpeed
			    23301,		// backSpeed
			    128,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 0, 256, 192 } 
		),
		
		// 219 bat v1.21 ok
		new DudeInfo(
			    480,		// start sequence ID
			    10,			// start health
			    5,			// mass
			    120,
			    32,			// clip distance
			    2,			// eye above z
			    0,
			    M2X(20),	// hear distance
			    M2X(50),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    23301,		// frontSpeed
			    23301,		// sideSpeed
			    13981,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 64, 256 } 
		),
		
		// 220 rat v1.21 ok
		new DudeInfo(
			    496,		// start sequence ID
			    10,			// start health
			    5,			// mass
			    120,
			    32,			// clip distance
			    3,			// eye above z
			    0,
			    M2X(25),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 128, 256 } 
		),
		
		// 221 green pod v1.21 ok
		new DudeInfo(
			    512,		// start sequence ID
			    50,			// start health
			    65535,		// mass
			    120,
			    64,			// clip distance
			    40,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(22),	// seeing distance
			    kAngle180,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    384,			// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 160, 160, 128, 160, 0, 0, 256 } 
		),
		
		// 222 green tentacle v1.21 ok
		new DudeInfo(
			    528,		// start sequence ID
			    10,			// start health
			    65535,		// mass
			    120,
			    32,			// clip distance
			    0,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(10),	// seeing distance
			    kAngle180,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    384,			// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 80, 0, 0, 256  } 
		),
		
		// 223 fire pod v1.21 ok
		new DudeInfo(
			    544,		// start sequence ID
			    100,		// start health
			    65535,		// mass
			    120,
			    64,			// clip distance
			    40,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(30),	// seeing distance
			    kAngle180,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    384,			// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 96, 0, 128, 64, 256, 64, 160 } 
		),
		
		// 224 fire tentacle v1.21 ok
		new DudeInfo(
			    560,		// start sequence ID
			    20,			// start health
			    65535,		// mass
			    120,
			    32,			// clip distance
			    0,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(10),	// seeing distance
			    kAngle180,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 128, 0, 128, 128, 0, 0, 128 } 
		),
		
		// 225 mother pod v1.21 ok
		new DudeInfo(
			    576,		// start sequence ID
			    200,		// start health
			    65535,		// mass
			    120,
			    64,			// clip distance
			    40,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle180,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    0,			// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 226 mother tentacle v1.21 ok
		new DudeInfo(
			    592,		// start sequence ID
			    50,			// start health
			    65535,		// mass
			    120,
			    32,			// clip distance
			    0,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle180,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    0,			// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 128, 256, 128, 128, 256 } 
		),
		
		// 227 Cerberus v1.21 ok
		new DudeInfo(
			    608,		// start sequence ID
			    200,		// start health
			    1000,		// mass
			    120,
			    64,			// clip distance
			    29,			// eye above z
			    10,
			    M2X(80),		// hear distance
			    M2X(200),		// seeing distance
			    kAngle120,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,			// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    69905,		// frontSpeed
			    58254,		// sideSpeed
			    46603,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 16, 0, 16, 16, 0, 96, 48 } 
		),
		
		// 228 kDudeCerberus2 v1.21 ok
		new DudeInfo(
			    624,		// start sequence ID
			    100,		// start health
			    1000,		// mass
			    120,
			    64,			// clip distance
			    29,			// eye above z
			    10,
			    M2X(40),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle120,		// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0,			// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    34952,		// sideSpeed
			    25631,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 16, 0, 16, 16, 0, 96, 48 } 
		),
		
		// 229 Tchernobog v1.21 ok
		new DudeInfo(
			    640,		// start sequence ID
			    800,		// start health
			    1500,		// mass
			    120,
			    128,		// clip distance
			    0,			// eye above z
			    0,
			    M2X(50),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle90,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    58254,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 3, 1, 4, 4, 0, 4, 3 } 
		),
		
		// 230 TCultist prone v1.21 ok
		new DudeInfo(
			    656,		// start sequence ID
			    25,			// start health
			    20,			// mass
			    120,
			    32,			// clip distance
			    0,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 96, 256, 256, 256, 192 }  
		),
		
		// 231 Player 1 v1.21 ok
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 232 Player 2
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 233 Player 3
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 234 Player 4
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 235 Player 5
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 236 Player 6
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 237 Player 7
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 238 Player 8
		new DudeInfo(
			    752,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    0,			// eye above z
			    16,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 239 v1.21 ok
		new DudeInfo(
			    784,		// start sequence ID
			    25,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    41,			// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    100,		// flee health
			    100,		// hinder damage
			    0,			// change target chance
			    0,			// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    160,		// angSpeed
//			    0,
			    new int[] { 7,	5, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 240 kDudePlayerBurning v1.21 ok
		new DudeInfo(
			    256,		// start sequence ID
			    30,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    41,			// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    100,		// flee health
			    100,		// hinder damage
			    0,			// change target chance
			    0,			// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    46603,		// frontSpeed
			    34952,		// sideSpeed
			    13981,		// backSpeed
			    160,		// angSpeed
//			    0,
			    new int[] { 7,	5, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 241 kDudeAxeZombieBurning v1.21 ok
		new DudeInfo(
			    272,		// start sequence ID
			    12,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    46,			// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    160,		// angSpeed
//			    0,
			    new int[] { 7,	5, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 242 kDudeFatZombieBurning v1.21 ok
		new DudeInfo(
			    288,		// start sequence ID
			    25,			// start health
			    120,		// mass
			    120,
			    48,			// clip distance
			    44,			// eye above z
			    20,
			    M2X(20),	// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    39612,		// frontSpeed
			    27962,		// sideSpeed
			    13981,		// backSpeed
			    100,		// angSpeed
//			    0,
			    new int[] { 7,	5, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 243  v1.21 ok
		new DudeInfo(
			    256,		// start sequence ID
			    100,		// start health
			    70,			// mass
			    120,
			    64,			// clip distance
			    38,			// eye above z
			    20,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
		),
		
		// 244 Sleep Zombie v1.21 ok
		new DudeInfo(
			    272,		// start sequence ID
			    60,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    46,			// eye above z
			    20,
			    M2X(10),	// hear distance
			    0,			// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    15,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 112, 256, 256, 256, 256 } 
		),
		
		// 245 Innocent v1.21 ok
		new DudeInfo(
			    784,		// start sequence ID
			    50,			// start health
			    70,			// mass
			    120,
			    48,			// clip distance
			    46,			// eye above z
			    20,
			    M2X(5),		// hear distance
			    0,			// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    8,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,		// frontSpeed
			    46603,		// sideSpeed
			    34952,		// backSpeed
			    384,		// angSpeed
//			    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 288, 288, 288, 288, 288, 288, 288 } 
		),
		
		// 246 SCultist prone v1.21 ok
		new DudeInfo(
			    384,		// start sequence ID
			    25,			// start health
			    70,			// mass
			    120,
			    32,			// clip distance
			    -5,			// eye above z
			    0,
			    M2X(4),		// hear distance
			    M2X(100),	// seeing distance
			    kAngle60,	// vision periphery
//			    0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
//			    0,
			    new int[] { 7,	5, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
			 ),
			 
//Plasma Pak dudes
			 
		// 247 Cultist with Tesla v1.21 ok
		new DudeInfo(
				12800,		// start sequence ID
			    40,			// start health
			    70,			// mass
				120,
			    48,			// clip distance
			    41,			// eye above z
				20,
			    10240,		// hear distance
			    51200,	// seeing distance
			    kAngle90,	// vision periphery
//					    0,
			    0,			// melee distance
			    10,			// flee health
			    8,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    46603,			// frontSpeed
			    34952,			// sideSpeed
			    13981,			// backSpeed
			    256,			// angSpeed
//					    0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 96, 160, 256, 256, 12 } 
			 ),
				 
		// 248 Cultist with Dynamite v1.21 ok
		new DudeInfo(
				13056,		// start sequence ID
			    40,			// start health
			    70,			// mass
				120,
			    48,			// clip distance
			    41,			// eye above z
				20,
			    10240,		// hear distance
			    51200,	// seeing distance
			    kAngle90,	// vision periphery
//							    0,
			    0,			// melee distance
			    10,			// flee health
			    8,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    46603,			// frontSpeed
			    34952,			// sideSpeed
			    13981,			// backSpeed
			    256,			// angSpeed
//				0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 256, 160, 96, 64, 256, 256, 256 } 
			 ),
				 
		// 249 Beast cultist v1.21 ok
		new DudeInfo(
				13312,		// start sequence ID
			    40,			// start health
			    70,			// mass
				120,
			    48,			// clip distance
			    41,			// eye above z
				20,
			    10240,		// hear distance
			    51200,	// seeing distance
			    kAngle90,	// vision periphery
//				0,
			    0,			// melee distance
			    10,			// flee health
			    12,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    46603,			// frontSpeed
			    34952,			// sideSpeed
			    13981,			// backSpeed
			    256,			// angSpeed
//				0,
			    new int[] { 15,	-1, -1 },		// nGibType
			    new int[] { 128, 128, 16, 16, 0, 64, 48 } 
			 ),
			 
		// 250 Tiny caleb v1.21 ok
		new DudeInfo(
				13568,		// start sequence ID
			    10,			// start health
			    5,			// mass
				120,
			    32,			// clip distance
			    3,			// eye above z
				0,
			    12800,		// hear distance
			    51200,		// seeing distance
			    kAngle90,	// vision periphery
//				0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,			// frontSpeed
			    46603,			// sideSpeed
			    34952,			// backSpeed
			    384,			// angSpeed
//				0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 160, 160, 160, 160, 256, 128, 288 } 
			 ),
			 
		// 251 Beast v1.21 ok
		new DudeInfo(
				10752,		// start sequence ID
			    120,		// start health
			    70,			// mass
				120,
			    48,			// clip distance
			    41,			// eye above z
				20,
			    12800,		// hear distance
			    51200,		// seeing distance
			    kAngle60,	// vision periphery
//				0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    116508,			// frontSpeed
			    81555,			// sideSpeed
			    69905,			// backSpeed
			    384,			// angSpeed
//				0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 5, 5, 15, 8, 0, 15, 15 } 
			 ),
			 
		// 252 v1.21 ok
		new DudeInfo(
				13568,		// start sequence ID
			    10,			// start health
			    5,			// mass
				120,
			    32,			// clip distance
			    3,			// eye above z
				0,
			    12800,		// hear distance
			    51200,		// seeing distance
			    kAngle90,	// vision periphery
//				0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
			    58254,			// frontSpeed
			    46603,			// sideSpeed
			    34952,			// backSpeed
			    384,			// angSpeed
//				0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
			 ),
		// 253
		new DudeInfo(
				10752,		// start sequence ID
			    25,			// start health
			    5,			// mass
				120,
			    48,			// clip distance
			    41,			// eye above z
				20,
			    12800,		// hear distance
			    51200,		// seeing distance
			    kAngle60,	// vision periphery
//						0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    116508,			// frontSpeed
			    81555,			// sideSpeed
			    69905,			// backSpeed
			    384,			// angSpeed
//				0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
			 ),

		// 254  = kGDXUniversalCultist
		new DudeInfo(
			    720,		// start sequence ID
			    85,		// start health
			    45,			// mass
			    120,
			    48,			// clip distance
			    48,			// eye above z
			    20,
			    10240,		// hear distance
			    51200,	// seeing distance
			    kAngle120,	// vision periphery
//			    0,
				618,		// melee distance
			    5,			// flee health
			    5,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    false,		// lockout
				46603,		// frontSpeed
			    34952,		// sideSpeed
			    13981,		// backSpeed
			    256,		// angSpeed
//			    0,
			    new int[] { 7,	-1, 18 },		// nGibType
			    new int[] { 256, 256, 128, 256, 256, 256, 192 }
			    //new int[] { 256, 256, 96, 256, 256, 256, 192 }
		),
		
		// 255 = kGDXGenDudeBurning
		new DudeInfo(
				256,		// start sequence ID
			    25,			// start health
			    5,			// mass
				120,
			    48,			// clip distance
			    41,			// eye above z
				20,
			    12800,		// hear distance
			    51200,		// seeing distance
			    kAngle60,	// vision periphery
//						0,
			    0,			// melee distance
			    10,			// flee health
			    10,			// hinder damage
			    0x0100,		// change target chance
			    0x0010,		// change target to kin chance
			    0x8000,		// alertChance
			    true,		// lockout
			    58254,			// frontSpeed
			    46603,			// sideSpeed
			    34952,			// backSpeed
			    384,			// angSpeed
//				0,
			    new int[] { 7,	-1, -1 },		// nGibType
			    new int[] { 256, 256, 256, 256, 256, 256, 256 } 
			 )
	};
	
	public static DudeInfo[] gPlayerTemplate = {
		
		// normal human
		new DudeInfo(
				752,		// start sequence ID 
				100,		// start health
				70,			// mass
				120,
				48,			// clip distance
				0,			// eye above z
				16, 
				M2X(4), 	// hear distance
				M2X(100),	// seeing distance
				kAngle60,	// vision periphery
				0,			// melee distance
				10,			// flee health
				10,			// hinder damage
				0x0100,		// change target chance
				0x0010,		// change target to kin chance
				0x8000,		// alertChance
				true,		// lockout
				0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
			    new int[] { 15,	-1, -1 },		// nGibType
				new int[] { 256, 256, 256, 256, 256, 256, 288 }
		),
		
		// normal beast
		new DudeInfo(
				672,		// start sequence ID 
				100,		// start health
				70,			// mass
				120,
				48,			// clip distance
				36,			// eye above z
				20,
				M2X(4), 	// hear distance
				M2X(100),	// seeing distance
				kAngle60,	// vision periphery
				0,			// melee distance
				10,			// flee health
				10,			// hinder damage
				0x0100,		// change target chance
				0x0010,		// change target to kin chance
				0x8000,		// alertChance
				true,		// lockout
				0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
			    new int[] { 7,	-1, -1 },		// nGibType
				new int[] { 256, 256, 256, 256, 256, 256, 288 }
		),
		
		// shrink human
		new DudeInfo(
				(GAMEVER == VER100) ? 752 : 12032,	// start sequence ID (NoOne: can't get it work normally with VERSION)
				100,		// start health
				10,			// mass
				120,
				16,			// clip distance
				0,			// eye above z
				16, 
				M2X(4), 	// hear distance
				M2X(100),	// seeing distance
				kAngle60,	// vision periphery
				0,			// melee distance
				10,			// flee health
				10,			// hinder damage
				0x0100,		// change target chance
				0x0010,		// change target to kin chance
				0x8000,		// alertChance
				true,		// lockout
				0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    32,			// angSpeed
			    new int[] { 15,	-1, -1 },		// nGibType
				//new int[] { 256, 256, 256, 256, 256, 256, 288 }
			    new int[] { 1024, 1024, 1024, 1024, 256, 1024, 1024 }
		),
		
		// grown human
		new DudeInfo(
				(GAMEVER == VER100) ? 752 : 12032,	// start sequence ID (NoOne: can't get it work normally with VERSION)
				100,		// start health
				1100,		// mass
				120,
				100,		// clip distance
				0,			// eye above z
				16, 
				M2X(4), 	// hear distance
				M2X(100),	// seeing distance
				kAngle60,	// vision periphery
				0,			// melee distance
				10,			// flee health
				10,			// hinder damage
				0x0100,		// change target chance
				0x0010,		// change target to kin chance
				0x8000,		// alertChance
				true,		// lockout
				0,			// frontSpeed
			    0,			// sideSpeed
			    0,			// backSpeed
			    64,			// angSpeed
			    new int[] { 15,	7, 7 },		// nGibType
				new int[] { 64, 64, 64, 64, 256, 64, 64 }
		),
	};
	
	public int		seqStartID;
	public int		startHealth;
	public int		mass;			// in KG
	public int		clipdist;
	public int 		eyeHeight;		// (in pixels?) relative to origin //68
	public int 		aimHeight;
	public int		hearDist;		// how far it can hear
	public int		seeDist;		// how far it can see
	public int		periphery;		// peripheral vision in angle units
	public int		meleeDist;		// how close it needs to be to melee attack
	public int		fleeHealth;
	public int		hinderDamage;	// amount of damage necessary to "hinder"
	public int		changeTarget;	// probably of changing target per hit point (1/2^16)
	public int		changeTargetKin;// probably of changing target to same species
	public int		alertChance;	// chance of noticing player and activating
	public boolean	lockOut;
	public int		frontSpeed;
	public int		sideSpeed;
	public int		backSpeed;
	public int		angSpeed;
	public int[] nGibType;
	public int[] startDamage = new int[7];
	public int[] damageShift = new int[7];
	
	public DudeInfo(int seqStartID, int startHealth, int plasmamass, int mass, int clipdist, int eyeHeight,
					int aimHeight, int hearDist, int seeDist, int periphery, int meleeDist, int fleeHealth,
					int hinderDamage, int changeTarget, int changeTargetKin, int alertChance, boolean lockOut,
					int frontSpeed, int sideSpeed, int backSpeed, int angSpeed, int[] nGibType, int[] startDamage)
	{
		this.seqStartID = seqStartID;
		this.startHealth = startHealth;
		this.mass = plasmamass;			
		this.clipdist = clipdist;
		this.eyeHeight = eyeHeight;	
		this.aimHeight = aimHeight;
		this.hearDist = hearDist;		
		this.seeDist = seeDist;		
		this.periphery = periphery;		
		this.meleeDist = meleeDist;		
		this.fleeHealth = fleeHealth;
		this.hinderDamage = hinderDamage;	
		this.changeTarget = changeTarget;	
		this.changeTargetKin = changeTargetKin; 
		this.alertChance = alertChance;	
		this.lockOut = lockOut;
		this.frontSpeed = frontSpeed;
		this.sideSpeed = sideSpeed;
		this.backSpeed = backSpeed;
		this.angSpeed = angSpeed;
		this.nGibType = nGibType;
		this.startDamage = startDamage;
	}
	

	public DudeInfo() { } // random dude not used
	
	public void copy(DudeInfo source)
	{
		this.seqStartID = source.seqStartID;
		this.startHealth = source.startHealth;
		this.mass = source.mass;			
		this.clipdist = source.clipdist;
		this.eyeHeight = source.eyeHeight;	
		this.hearDist = source.hearDist;		
		this.seeDist = source.seeDist;		
		this.periphery = source.periphery;		
		this.meleeDist = source.meleeDist;		
		this.fleeHealth = source.fleeHealth;
		this.hinderDamage = source.hinderDamage;	
		this.changeTarget = source.changeTarget;	
		this.changeTargetKin = source.changeTargetKin; 
		this.alertChance = source.alertChance;	
		this.lockOut = source.lockOut;
		this.frontSpeed = source.frontSpeed;
		this.sideSpeed = source.sideSpeed;
		this.backSpeed = source.backSpeed;
		this.angSpeed = source.angSpeed;
		this.nGibType = source.nGibType;
		this.startDamage = source.startDamage;
	}
	
	public void info() {
		System.out.println("seqStartID " + seqStartID);
		System.out.println("startHealth " + startHealth);
		System.out.println("mass " + mass);
		System.out.println("clipdist " + clipdist);
		System.out.println("eyeHeight " + eyeHeight);
		System.out.println("hearDist " + hearDist);
		System.out.println("seeDist " + seeDist);
		System.out.println("periphery " + periphery);
		System.out.println("meleeDist " + meleeDist);
		System.out.println("fleeHealth " + fleeHealth);
		System.out.println("hinderDamage " + hinderDamage);
		System.out.println("changeTarget " + changeTarget);
		System.out.println("changeTargetKin " + changeTargetKin);
		System.out.println("alertChance " + alertChance); 
		System.out.println("lockOut " + lockOut);
		System.out.println("frontSpeed " + (frontSpeed));
		System.out.println("sideSpeed " + (sideSpeed));
		System.out.println("backSpeed " + (backSpeed));
		System.out.println("angSpeed " + angSpeed);
		System.out.println("nGibType " + Arrays.toString(nGibType));
		for(int i = 0; i < 7; i++) {
		    System.out.println("damageShift" + i + " " + damageShift[i]);
		}
	}
}
