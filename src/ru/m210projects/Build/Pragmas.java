// Function-wrapped Watcom pragmas
// by Jonathon Fowler (jonof@edgenetwk.com)
//
// These functions represent some of the more longer-winded pragmas
// from the original pragmas.h wrapped into functions for easier
// use since many jumps and whatnot make it harder to write macro-
// inline versions. I'll eventually convert these to macro-inline
// equivalents.		--Jonathon
//
// This file has been ported to Java and modified by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build;

public class Pragmas {
	
	public static long sqr(int eax) { return (eax) * (eax); }
	public static int scale(long eax, long edx, long ecx) { return (int)((eax * edx) / ecx); }
	public static int dmulscale(long eax, long edx, long esi, long edi, int ecx) { return (int)((eax * edx + esi * edi) >> ecx); }
	public static int dmulscaler(long eax, long edx, long esi, long edi, int ecx) { return (int)((eax * edx + esi * edi + 0x20000000) >> ecx); }
	public static int mulscale(long eax, long edx, int ecx) { return (int)((eax * edx) >> ecx); }
	public static int mulscaler(long eax, long edx, int ecx) { return (int)((eax * edx + 0x20000000) >> ecx); }
	public static int divscale(long eax, long ebx, int ecx) { return (int) ((eax << ecx) / ebx); }
	public static int tmulscale(long eax, long edx, long ebx, long ecx, long esi, long edi, int a) { return (int) (((eax * edx) + (ebx * ecx) + (esi * edi)) >> a); }
	
	public static int klabs(int a) { return (a < 0) ? -a : a; }
	public static long klabs(long a) { return (a < 0) ? -a : a; }
	public static int ksgn(int a)  { if (a > 0) return 1; if (a < 0) return -1; return 0; }
	public static int muldiv(long a, long b, long c) { return (int) ((a * b) / c); } 
}
