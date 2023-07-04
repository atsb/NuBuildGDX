//-------------------------------------------------------------------------
/*
Copyright (C) 1997, 2005 - 3D Realms Entertainment

This file is part of Shadow Warrior version 1.2

Shadow Warrior is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Original Source: 1997 - Frank Maddin and Jim Norwood
Prepared for public release: 03/28/2005 - Charlie Wiederhold, 3D Realms
*/
//-------------------------------------------------------------------------

package ru.m210projects.Wang.Type;

public class MyTypes {

	public static final int MAXINT32 = 0x7fffffff;
	public static final int MININT32 = -0x80000000;
	public static final long MAXUINT32 = 0xffffffff;
	public static final long MINUINT32 = 0;

	public static final int MAXINT16 = 0x7fff;
	public static final int MININT16 = -0x8000;
	public static final int MAXUINT16 = 0xffff;
	public static final int MINUINT16 = 0;
	// CTW ADDITION END

	public static final int FALSE = 0;
	public static final int TRUE = 1;

	public static final int OFF = 0;
	public static final int ON = 1;

	public static final byte MINCHAR = -128;
	public static final byte MAXCHAR = 127;

	public static final short MINUCHAR = 0;
	public static final short MAXUCHAR = 255;

	public static final short MINBYTE = 0;
	public static final short MAXBYTE = 255;

	public static final short MININT = (-32768);
	public static final short MAXINT = 32767;

	public static final int MINWORD = 0;
	public static final int MAXWORD = 65535;

	public static final int MINLONG = (-2147483648);
	public static final int MAXLONG = (2147483647);

	public static final long MINULONG = 0;
	public static final long MAXULONG = MAXUINT32;

	/*
	 * =========================== = = FAST calculations =
	 * ===========================
	 */

	// For fast DIVision of integers

	public static final int DIV2(int x) {
		return x >> 1;
	}

	public static final int DIV4(int x) {
		return x >> 2;
	}

	public static final int DIV8(int x) {
		return x >> 3;
	}

	public static final int DIV16(int x) {
		return x >> 4;
	}

	public static final int DIV32(int x) {
		return x >> 5;
	}

	public static final int DIV64(int x) {
		return x >> 6;
	}

	public static final int DIV128(int x) {
		return x >> 7;
	}

	public static final int DIV256(int x) {
		return x >> 8;
	}

	// Constants used in fast mods

	public static final int C_MOD2 = 1;
	public static final int C_MOD4 = 3;
	public static final int C_MOD8 = 7;
	public static final int C_MOD16 = 15;
	public static final int C_MOD32 = 31;
	public static final int C_MOD64 = 63;
	public static final int C_MOD128 = 127;
	public static final int C_MOD256 = 255;

	// Fast mods of select 2 power numbers

	public static final int MOD2(int x) {
		return ((x) & C_MOD2);
	}

	public static final int MOD4(int x) {
		return ((x) & C_MOD4);
	}

	public static final int MOD8(int x) {
		return ((x) & C_MOD8);
	}

	public static final int MOD16(int x) {
		return ((x) & C_MOD16);
	}

	public static final int MOD32(int x) {
		return ((x) & C_MOD32);
	}

	public static final int MOD64(int x) {
		return ((x) & C_MOD64);
	}

	public static final int MOD128(int x) {
		return ((x) & C_MOD128);
	}

	public static final int MOD256(int x) {
		return ((x) & C_MOD256);
	}

	// Fast mods of any power of 2

	public static final int MOD_P2(int number, int modby) {
		return ((number) & ((modby) - 1));
	}

	// Truncates to select 2 power numbers

	public static final int TRUNC2(int x) {
		return ((x) & ~C_MOD2);
	}

	public static final int TRUNC4(int x) {
		return ((x) & ~C_MOD4);
	}

	public static final int TRUNC8(int x) {
		return ((x) & ~C_MOD8);
	}

	public static final int TRUNC16(int x) {
		return ((x) & ~C_MOD16);
	}

	public static final int TRUNC32(int x) {
		return ((x) & ~C_MOD32);
	}

	public static final int TRUNC64(int x) {
		return ((x) & ~C_MOD64);
	}

	public static final int TRUNC128(int x) {
		return ((x) & ~C_MOD128);
	}

	public static final int TRUNC256(int x) {
		return ((x) & ~C_MOD256);
	}

	public static final int POWER2_TRUNC(int number, int truncby) {
		return ((number) & ~((truncby) - 1));
	}

	// moves value to closest power of 2 pixel boundry

	public static final int BOUND_2PIX(int x) {
		return (TRUNC2((x) + MOD2(x)));
	}

	public static final int BOUND_4PIX(int x) {
		return (TRUNC4((x) + MOD4(x)));
	}

	public static final int BOUND_8PIX(int x) {
		return (TRUNC8((x) + MOD8(x)));
	}

	public static final int BOUND_16PIX(int x) {
		return (TRUNC16((x) + MOD16(x)));
	}

	public static final int BOUND_32PIX(int x) {
		return (TRUNC32((x) + MOD32(x)));
	}

	public static final int BOUND_64PIX(int x) {
		return (TRUNC64((x) + MOD64(x)));
	}

	public static final int BOUND_128PIX(int x) {
		return (TRUNC128((x) + MOD128(x)));
	}

	public static final int BOUND_256PIX(int x) {
		return (TRUNC256((x) + MOD256(x)));
	}

//	public static final int BOUND_POWER2_PIX(int x, int bound) {
//		return POWER2_TRUNC(x, bound) + POWER2_MOD(x, bound);
//	}

	// A few muls with shifts and adds
	// probably not needed with good compiler
	public static final int MUL2(int x) {
		return ((x) * 2);
	}

	public static final int MUL3(int x) {
		return (((x) << 1) + (x));
	}

	public static final int MUL5(int x) {
		return (((x) << 2) + (x));
	}

	public static final int MUL6(int x) {
		return (((x) << 2) + (x) + (x));
	}

	public static final int MUL7(int x) {
		return (((x) << 2) + (x) + (x) + (x));
	}

	public static final int MUL8(int x) {
		return ((x) * 8);
	}

	public static final int MUL9(int x) {
		return (((x) << 3) + (x));
	}

	public static final int MUL10(int x) {
		return (((x) << 3) + (x) + (x));
	}

	public static final int MUL11(int x) {
		return (((x) << 3) + (x) + (x) + (x));
	}

	public static final int MUL12(int x) {
		return (((x) << 3) + ((x) << 2));
	}

	public static final int MUL13(int x) {
		return (((x) << 3) + ((x) << 2) + (x));
	}

	public static final int MUL14(int x) {
		return (((x) << 3) + ((x) << 2) + (x) + (x));
	}

	public static final int MUL15(int x) {
		return (((x) << 3) + ((x) << 2) + (x) + (x) + (x));
	}

	public static final int MUL16(int x) {
		return ((x) * 16);
	}

	/*
	 * =========================== = = Bit manipulation =
	 * ===========================
	 */

	public static final boolean TEST(int flags, int mask) {
		return ((flags) & (mask)) != 0;
	}
	
	public static final int DTEST(int flags, int mask) {
		return ((flags) & (mask));
	}

	// mask definitions

	public static final int BIT(int shift) {
		return (1 << (shift));
	}

	/*
	 * =========================== = = Miscellaneous = ===========================
	 */

	// public static final int ABS(num) ((num) < 0 ? -(num) : (num))

	public static final boolean BETWEEN(int x, int low, int high) {
		return (((x) >= (low)) && ((x) <= (high)));
	}

}
