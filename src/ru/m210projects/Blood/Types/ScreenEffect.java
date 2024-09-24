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

import static ru.m210projects.Blood.Main.*;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class ScreenEffect {

	public static final String damagefade = "DAMAGE";
	public static final String drownfade = "DROWN";
	public static final String blindfade = "BLIND";
	public static final String pickupfade = "PICKUP";

	public static void FadeInit() {
		Console.Println("Initializing fade effects", 0);
	}

	public static void resetEffects() {
		engine.updateFade(damagefade, 0);
		engine.updateFade(drownfade, 0);
		engine.updateFade(blindfade, 0);
		engine.updateFade(pickupfade, 0);
	}
}
