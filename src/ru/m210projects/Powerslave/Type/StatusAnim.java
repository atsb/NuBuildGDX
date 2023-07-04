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

package ru.m210projects.Powerslave.Type;

import static ru.m210projects.Powerslave.Globals.StatusAnim;
import static ru.m210projects.Powerslave.Globals.nAnimsFree_X_1;
import static ru.m210projects.Powerslave.Globals.nFirstAnim;
import static ru.m210projects.Powerslave.Globals.nLastAnim;

public class StatusAnim {

	public short field_0;
	public short field_2;
	public short field_4;
	public byte field_6;
	public byte field_7;

	public static byte StatusAnimsFree[] = new byte[50];
	public static byte StatusAnimFlags[] = new byte[50];

	public static int BuildStatusAnim(int a1, int a2) {
		int v4 = nAnimsFree_X_1;
		byte result;
		if (nAnimsFree_X_1 > 0) {
			--nAnimsFree_X_1;
			result = StatusAnimsFree[v4 - 1];
			StatusAnim[result].field_6 = -1;
			StatusAnim[result].field_7 = (byte) nLastAnim;
			if (nLastAnim < 0)
				nFirstAnim = result;
			else
				StatusAnim[nLastAnim].field_6 = result;
			nLastAnim = result;
			StatusAnim[result].field_0 = (short) a1;
			StatusAnim[result].field_2 = 0;
			StatusAnimFlags[result] = (byte) a2;
//		    StatusAnim[result].field_4 = numpages; XXX
		} else {
			result = -1;
		}
		return result;
	}

}
