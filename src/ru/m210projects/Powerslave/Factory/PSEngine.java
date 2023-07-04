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

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Bullet.*;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Sector.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Types.Palette;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.SmallTextFont;
import ru.m210projects.Build.Types.TextFont;
import ru.m210projects.Powerslave.Fonts.StandartFont;

public class PSEngine extends BuildEngine {

	private short AngTable[] = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4,
			5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10,
			10, 10, 10, 11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 12, 12, 13, 13, 13, 13, 13, 13, 14, 14, 14, 14, 14, 14,
			14, 15, 15, 15, 15, 15, 15, 16, 16, 16, 16, 16, 16, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 19,
			19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 23, 23, 23,
			23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 26, 26, 27, 27, 27, 27, 27,
			27, 27, 28, 28, 28, 28, 28, 28, 29, 29, 29, 29, 29, 29, 30, 30, 30, 30, 30, 30, 30, 31, 31, 31, 31, 31, 31,
			32, 32, 32, 32, 32, 32, 33, 33, 33, 33, 33, 33, 33, 34, 34, 34, 34, 34, 34, 35, 35, 35, 35, 35, 35, 35, 36,
			36, 36, 36, 36, 36, 37, 37, 37, 37, 37, 37, 38, 38, 38, 38, 38, 38, 38, 39, 39, 39, 39, 39, 39, 40, 40, 40,
			40, 40, 40, 41, 41, 41, 41, 41, 41, 41, 42, 42, 42, 42, 42, 42, 43, 43, 43, 43, 43, 43, 43, 44, 44, 44, 44,
			44, 44, 45, 45, 45, 45, 45, 45, 46, 46, 46, 46, 46, 46, 46, 47, 47, 47, 47, 47, 47, 48, 48, 48, 48, 48, 48,
			48, 49, 49, 49, 49, 49, 49, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 51, 52, 52, 52, 52, 52, 52, 53,
			53, 53, 53, 53, 53, 53, 54, 54, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 56, 56, 56, 56, 56, 56, 57, 57,
			57, 57, 57, 57, 57, 58, 58, 58, 58, 58, 58, 59, 59, 59, 59, 59, 59, 59, 60, 60, 60, 60, 60, 60, 61, 61, 61,
			61, 61, 61, 61, 62, 62, 62, 62, 62, 62, 63, 63, 63, 63, 63, 63, 63, 64, 64, 64, 64, 64, 64, 65, 65, 65, 65,
			65, 65, 65, 66, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 68, 69, 69, 69, 69, 69,
			69, 69, 70, 70, 70, 70, 70, 70, 70, 71, 71, 71, 71, 71, 71, 72, 72, 72, 72, 72, 72, 72, 73, 73, 73, 73, 73,
			73, 74, 74, 74, 74, 74, 74, 74, 75, 75, 75, 75, 75, 75, 75, 76, 76, 76, 76, 76, 76, 77, 77, 77, 77, 77, 77,
			77, 78, 78, 78, 78, 78, 78, 78, 79, 79, 79, 79, 79, 79, 80, 80, 80, 80, 80, 80, 80, 81, 81, 81, 81, 81, 81,
			81, 82, 82, 82, 82, 82, 82, 83, 83, 83, 83, 83, 83, 83, 84, 84, 84, 84, 84, 84, 84, 85, 85, 85, 85, 85, 85,
			86, 86, 86, 86, 86, 86, 86, 87, 87, 87, 87, 87, 87, 87, 88, 88, 88, 88, 88, 88, 88, 89, 89, 89, 89, 89, 89,
			90, 90, 90, 90, 90, 90, 90, 91, 91, 91, 91, 91, 91, 91, 92, 92, 92, 92, 92, 92, 92, 93, 93, 93, 93, 93, 93,
			93, 94, 94, 94, 94, 94, 94, 94, 95, 95, 95, 95, 95, 95, 96, 96, 96, 96, 96, 96, 96, 97, 97, 97, 97, 97, 97,
			97, 98, 98, 98, 98, 98, 98, 98, 99, 99, 99, 99, 99, 99, 99, 100, 100, 100, 100, 100, 100, 100, 101, 101,
			101, 101, 101, 101, 101, 102, 102, 102, 102, 102, 102, 102, 103, 103, 103, 103, 103, 103, 103, 104, 104,
			104, 104, 104, 104, 104, 105, 105, 105, 105, 105, 105, 105, 106, 106, 106, 106, 106, 106, 106, 107, 107,
			107, 107, 107, 107, 107, 108, 108, 108, 108, 108, 108, 108, 109, 109, 109, 109, 109, 109, 109, 110, 110,
			110, 110, 110, 110, 110, 111, 111, 111, 111, 111, 111, 111, 112, 112, 112, 112, 112, 112, 112, 113, 113,
			113, 113, 113, 113, 113, 114, 114, 114, 114, 114, 114, 114, 115, 115, 115, 115, 115, 115, 115, 116, 116,
			116, 116, 116, 116, 116, 117, 117, 117, 117, 117, 117, 117, 117, 118, 118, 118, 118, 118, 118, 118, 119,
			119, 119, 119, 119, 119, 119, 120, 120, 120, 120, 120, 120, 120, 121, 121, 121, 121, 121, 121, 121, 122,
			122, 122, 122, 122, 122, 122, 122, 123, 123, 123, 123, 123, 123, 123, 124, 124, 124, 124, 124, 124, 124,
			125, 125, 125, 125, 125, 125, 125, 125, 126, 126, 126, 126, 126, 126, 126, 127, 127, 127, 127, 127, 127,
			127, 128, 128, 128, 128, 128, 128, 128, 128, 129, 129, 129, 129, 129, 129, 129, 130, 130, 130, 130, 130,
			130, 130, 131, 131, 131, 131, 131, 131, 131, 131, 132, 132, 132, 132, 132, 132, 132, 133, 133, 133, 133,
			133, 133, 133, 133, 134, 134, 134, 134, 134, 134, 134, 135, 135, 135, 135, 135, 135, 135, 135, 136, 136,
			136, 136, 136, 136, 136, 137, 137, 137, 137, 137, 137, 137, 137, 138, 138, 138, 138, 138, 138, 138, 139,
			139, 139, 139, 139, 139, 139, 139, 140, 140, 140, 140, 140, 140, 140, 141, 141, 141, 141, 141, 141, 141,
			141, 142, 142, 142, 142, 142, 142, 142, 142, 143, 143, 143, 143, 143, 143, 143, 144, 144, 144, 144, 144,
			144, 144, 144, 145, 145, 145, 145, 145, 145, 145, 145, 146, 146, 146, 146, 146, 146, 146, 146, 147, 147,
			147, 147, 147, 147, 147, 148, 148, 148, 148, 148, 148, 148, 148, 149, 149, 149, 149, 149, 149, 149, 149,
			150, 150, 150, 150, 150, 150, 150, 150, 151, 151, 151, 151, 151, 151, 151, 151, 152, 152, 152, 152, 152,
			152, 152, 153, 153, 153, 153, 153, 153, 153, 153, 154, 154, 154, 154, 154, 154, 154, 154, 155, 155, 155,
			155, 155, 155, 155, 155, 156, 156, 156, 156, 156, 156, 156, 156, 157, 157, 157, 157, 157, 157, 157, 157,
			158, 158, 158, 158, 158, 158, 158, 158, 159, 159, 159, 159, 159, 159, 159, 159, 160, 160, 160, 160, 160,
			160, 160, 160, 161, 161, 161, 161, 161, 161, 161, 161, 162, 162, 162, 162, 162, 162, 162, 162, 162, 163,
			163, 163, 163, 163, 163, 163, 163, 164, 164, 164, 164, 164, 164, 164, 164, 165, 165, 165, 165, 165, 165,
			165, 165, 166, 166, 166, 166, 166, 166, 166, 166, 167, 167, 167, 167, 167, 167, 167, 167, 167, 168, 168,
			168, 168, 168, 168, 168, 168, 169, 169, 169, 169, 169, 169, 169, 169, 170, 170, 170, 170, 170, 170, 170,
			170, 170, 171, 171, 171, 171, 171, 171, 171, 171, 172, 172, 172, 172, 172, 172, 172, 172, 173, 173, 173,
			173, 173, 173, 173, 173, 173, 174, 174, 174, 174, 174, 174, 174, 174, 175, 175, 175, 175, 175, 175, 175,
			175, 175, 176, 176, 176, 176, 176, 176, 176, 176, 177, 177, 177, 177, 177, 177, 177, 177, 177, 178, 178,
			178, 178, 178, 178, 178, 178, 178, 179, 179, 179, 179, 179, 179, 179, 179, 180, 180, 180, 180, 180, 180,
			180, 180, 180, 181, 181, 181, 181, 181, 181, 181, 181, 181, 182, 182, 182, 182, 182, 182, 182, 182, 183,
			183, 183, 183, 183, 183, 183, 183, 183, 184, 184, 184, 184, 184, 184, 184, 184, 184, 185, 185, 185, 185,
			185, 185, 185, 185, 185, 186, 186, 186, 186, 186, 186, 186, 186, 186, 187, 187, 187, 187, 187, 187, 187,
			187, 188, 188, 188, 188, 188, 188, 188, 188, 188, 189, 189, 189, 189, 189, 189, 189, 189, 189, 190, 190,
			190, 190, 190, 190, 190, 190, 190, 191, 191, 191, 191, 191, 191, 191, 191, 191, 192, 192, 192, 192, 192,
			192, 192, 192, 192, 192, 193, 193, 193, 193, 193, 193, 193, 193, 193, 194, 194, 194, 194, 194, 194, 194,
			194, 194, 195, 195, 195, 195, 195, 195, 195, 195, 195, 196, 196, 196, 196, 196, 196, 196, 196, 196, 197,
			197, 197, 197, 197, 197, 197, 197, 197, 197, 198, 198, 198, 198, 198, 198, 198, 198, 198, 199, 199, 199,
			199, 199, 199, 199, 199, 199, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 201, 201, 201, 201, 201,
			201, 201, 201, 201, 202, 202, 202, 202, 202, 202, 202, 202, 202, 202, 203, 203, 203, 203, 203, 203, 203,
			203, 203, 204, 204, 204, 204, 204, 204, 204, 204, 204, 204, 205, 205, 205, 205, 205, 205, 205, 205, 205,
			206, 206, 206, 206, 206, 206, 206, 206, 206, 206, 207, 207, 207, 207, 207, 207, 207, 207, 207, 207, 208,
			208, 208, 208, 208, 208, 208, 208, 208, 209, 209, 209, 209, 209, 209, 209, 209, 209, 209, 210, 210, 210,
			210, 210, 210, 210, 210, 210, 210, 211, 211, 211, 211, 211, 211, 211, 211, 211, 211, 212, 212, 212, 212,
			212, 212, 212, 212, 212, 212, 213, 213, 213, 213, 213, 213, 213, 213, 213, 213, 214, 214, 214, 214, 214,
			214, 214, 214, 214, 214, 215, 215, 215, 215, 215, 215, 215, 215, 215, 215, 216, 216, 216, 216, 216, 216,
			216, 216, 216, 216, 217, 217, 217, 217, 217, 217, 217, 217, 217, 217, 218, 218, 218, 218, 218, 218, 218,
			218, 218, 218, 219, 219, 219, 219, 219, 219, 219, 219, 219, 219, 219, 220, 220, 220, 220, 220, 220, 220,
			220, 220, 220, 221, 221, 221, 221, 221, 221, 221, 221, 221, 221, 222, 222, 222, 222, 222, 222, 222, 222,
			222, 222, 222, 223, 223, 223, 223, 223, 223, 223, 223, 223, 223, 224, 224, 224, 224, 224, 224, 224, 224,
			224, 224, 224, 225, 225, 225, 225, 225, 225, 225, 225, 225, 225, 226, 226, 226, 226, 226, 226, 226, 226,
			226, 226, 226, 227, 227, 227, 227, 227, 227, 227, 227, 227, 227, 228, 228, 228, 228, 228, 228, 228, 228,
			228, 228, 228, 229, 229, 229, 229, 229, 229, 229, 229, 229, 229, 229, 230, 230, 230, 230, 230, 230, 230,
			230, 230, 230, 230, 231, 231, 231, 231, 231, 231, 231, 231, 231, 231, 231, 232, 232, 232, 232, 232, 232,
			232, 232, 232, 232, 232, 233, 233, 233, 233, 233, 233, 233, 233, 233, 233, 233, 234, 234, 234, 234, 234,
			234, 234, 234, 234, 234, 234, 235, 235, 235, 235, 235, 235, 235, 235, 235, 235, 235, 236, 236, 236, 236,
			236, 236, 236, 236, 236, 236, 236, 237, 237, 237, 237, 237, 237, 237, 237, 237, 237, 237, 238, 238, 238,
			238, 238, 238, 238, 238, 238, 238, 238, 238, 239, 239, 239, 239, 239, 239, 239, 239, 239, 239, 239, 240,
			240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 241, 241, 241, 241, 241, 241, 241, 241, 241, 241,
			241, 242, 242, 242, 242, 242, 242, 242, 242, 242, 242, 242, 242, 243, 243, 243, 243, 243, 243, 243, 243,
			243, 243, 243, 244, 244, 244, 244, 244, 244, 244, 244, 244, 244, 244, 244, 245, 245, 245, 245, 245, 245,
			245, 245, 245, 245, 245, 245, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 246, 247, 247, 247,
			247, 247, 247, 247, 247, 247, 247, 247, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 249,
			249, 249, 249, 249, 249, 249, 249, 249, 249, 249, 249, 249, 250, 250, 250, 250, 250, 250, 250, 250, 250,
			250, 250, 250, 251, 251, 251, 251, 251, 251, 251, 251, 251, 251, 251, 251, 252, 252, 252, 252, 252, 252,
			252, 252, 252, 252, 252, 252, 253, 253, 253, 253, 253, 253, 253, 253, 253, 253, 253, 253, 254, 254, 254,
			254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 256, 256, 256, 256, 256, 256 };

	public PSEngine(BuildGame game) throws Exception {
		super(game, 4);
	}

//	private boolean key = false;
//	@Override
//	public void sampletimer() {
//		if (timerfreq == 0)
//			return;
//
//		long n = (getticks() * timerticspersec / timerfreq) - timerlastsample;
//		if (n > 0) {
//			if(game.isCurrentScreen(gDemoScreen)) {
////				totalclock += 8;
//				if(BuildGdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
//					if(!key) {
//						totalclock += 4;
//						if(BuildGdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_RIGHT)) {
//							key = true;
//						}
//					}
//				} else key = false;
//			} else totalclock += n;
//
//			timerlastsample += n;
//		}
//	}

	@Override
	public boolean setrendermode(Renderer render) {
		if(this.render != null && this.render != render)
		{
			if(render.getType() != RenderType.Software)
			{
				((StandartFont) game.getFont(3)).reinit();
				final Screen screen = game.getScreen();
				if(screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if(game.isCurrentScreen(gGameScreen))
								game.pNet.ready2send = true;
						}
					});
					game.changeScreen(gPrecacheScreen);
				}
			}
		}

		return super.setrendermode(render);
	}

	@Override
	public byte getclosestcol(byte[] palette, int r, int g, int b) {
		int i;
		int dr, dg, db, dist, matchDist, match = 0;

		matchDist = 0x7FFFFFFF;

		for ( i = 0; i < 256; i++ )
		{
			dist = 0;

			dg = (palette[3 * i + 1] & 0xFF) - g;
			dist += 1 * dg * dg;
			if ( dist >= matchDist )
				continue;

			dr = (palette[3 * i] & 0xFF) - r;
			dist += 1 * dr * dr;
			if ( dist >= matchDist )
				continue;

			db = (palette[3 * i + 2] & 0xFF) - b;
			dist += 1 * db * db;
			if ( dist >= matchDist )
				continue;

			matchDist = dist;
			match = i;

			if (dist == 0)
				break;
		}

		return (byte) match;
	}

//	@Override
//	public short insertsprite(short sectnum, short statnum)
//	{
//		 short i = super.insertsprite(sectnum, statnum);
//		 sprite[i].clipdist = 0;
//		 sprite[i].xrepeat = 0;
//		 sprite[i].yrepeat = 0;
//		 sprite[i].extra = 0;
//		 sprite[i].owner = 0;
//		 return i;
//	}

//	@Override
//	public void loadtables() throws Exception {
//		super.loadtables();
//	}

	private int next = 1;

	@Override
	public int rand() {
		next = (int) ((next & 0xFFFFFFFFL) * 1103515245 + 12345);
		return (next >> 16) & 32767;
	}

	public short GetMyAngle(int a1, int a2) {
		short result;
		int v2 = a2;
		int v3 = -a1;
		int v4 = a2 << 11;
		if (a1 <= 0) {
			int v5 = -2048 * a1;
			if (v2 < 0) {
				int v7 = -v2;
				if (v3 == v7)
					return 1280;
				if (v3 > v7) {
					return (short) ((AngTable[(v7 << 11) / v3 & 0x7FF] + 1024) & 0x7FF);
				}
				result = (short) (1024 - AngTable[v5 / v7 & 0x7FF]);
			} else {
				if (v3 == v2)
					return 768;
				if (v2 <= v3)
					result = (short) (512 - AngTable[v4 / v3 & 0x7FF]);
				else
					result = AngTable[v5 / v2 & 0x7FF];
			}
			result += 512;
			result &= 0x7FF;
			return result;
		}
		if (a2 < 0) {
			int v8 = -a2;
			if (a1 == -a2)
				return 1792;
			if (a1 < v8) {
				result = AngTable[(a1 << 11) / v8 & 0x7FF];
				result += 1536;
				result &= 0x7FF;
				return result;
			}
			result = (short) (1536 - AngTable[-2048 * a2 / a1 & 0x7FF]);
			result += 512;
			result &= 0x7FF;
			return result;
		}
		if (a1 == a2)
			return 256;
		if (a1 <= a2) {
			result = (short) (2048 - AngTable[(a1 << 11) / a2 & 0x7FF]);
			result += 512;
		} else {
			result = AngTable[v4 / a1 & 0x7FF];
			result += 2048;
		}

		result &= 0x7FF;
		return result;
	}

	public short GetWallNormal(int nWall) {
		nWall &= 0x3FFF;
		return (short) ((GetMyAngle(wall[wall[nWall].point2].x - wall[nWall].x,
				wall[wall[nWall].point2].y - wall[nWall].y) + 512) & 0x7FF);
	}

	public short mydeletesprite(short num) {
		if (num < 0 || num >= MAXSPRITES)
			game.ThrowError("bad sprite value " + num + " handed to mydeletesprite");

		game.pInt.clearspriteinterpolate(num);
		short spr = deletesprite(num);
		if (num == besttarget)
			besttarget = -1;
		return spr;
	}

	public void mychangespritesect(short spritenum, short newsectnum) {
		DoKenTest();
		changespritesect(spritenum, newsectnum);
		DoKenTest();
	}

	private void DoKenTest() {
		for (short i = headspritesect[sprite[PlayerList[0].spriteId].sectnum];; i = nextspritesect[i]) {
			if (i == -1)
				return;
			if (nextspritesect[i] == i)
				break;
		}
		game.ThrowError("ERROR in Ken's linked list!");
	}

	public int movesprite(short spritenum, int dx, int dy, int dz, int ceildist, int flordist, int cliptype) {
		if (cliptype == 1)
			cliptype = CLIPMASK1;
		else
			cliptype = CLIPMASK0;

		SPRITE spr = sprite[spritenum];
		game.pInt.setsprinterpolate(spritenum, spr);

		bTouchFloor[0] = false;
		int sx = spr.x;
		int sy = spr.y;
		int sz = spr.z;
		short sectnum = spr.sectnum;
		if (!isValidSector(sectnum))
			return 0;

		int height = GetSpriteHeight(spritenum);
		if ((SectFlag[spr.sectnum] & 0x2000) != 0 || sector[spr.sectnum].floorz < spr.z) {
			dx >>= 1;
			dy >>= 1;
		}
		int zhit = movespritez(spritenum, dz, height, flordist, spr.clipdist * 4);
		sectnum = spr.sectnum;

		if (spr.statnum == 100) {
			int plr = GetPlayerFromSprite(spritenum);
			Vector2 floorz = CheckSectorFloor(overridesect, spr.z, 0, 0);
			if (floorz != null) {
				nXDamage[plr] = (int) floorz.x;
				nYDamage[plr] = (int) floorz.y;
			}
			dx += nXDamage[plr];
			dy += nYDamage[plr];
		} else {
			Vector2 floorz = CheckSectorFloor(overridesect, spr.z, dx, dy);
			if (floorz != null) {
				dx = (int) floorz.x;
				dy = (int) floorz.y;
			}
		}

		int movehit = engine.clipmove(spr.x, spr.y, spr.z, sectnum, dx, dy, 4 * spr.clipdist, height, flordist, cliptype);
		spr.x = clipmove_x;
		spr.y = clipmove_y;
		spr.z = clipmove_z;
		sectnum = clipmove_sectnum;

		int sumhit = (movehit | zhit);
		if (sectnum != spr.sectnum && sectnum >= 0) {
			if ((sumhit & 0x20000) != 0)
				dz = 0;
			if (sector[sectnum].floorz - sz >= flordist + dz) {
				mychangespritesect(spritenum, sectnum);
				if (spr.pal < 5 && spr.hitag == 0)
					spr.pal = sector[spr.sectnum].ceilingpal;
			} else {
				spr.x = sx;
				spr.y = sy;
			}
		}

		if ((sumhit & 0xC000) == 0xC000) {
			if(!isValidSprite(sumhit & 0x3FFF))
				sumhit = movehit;
		}

		return sumhit;
	}

	private int movespritez(short spritenum, int dz, int ceildist, int flordist, int cliptype) {
		SPRITE pSprite = sprite[spritenum];
		short sectnum = pSprite.sectnum;
		overridesect = sectnum;
		short v6 = sectnum;
		short cstat = pSprite.cstat;
		short oldcstat = cstat;
		pSprite.cstat &= ~1;

		int movehit = 0;
		if ((SectFlag[sectnum] & 0x2000) != 0)
			dz >>= 1;
		int sz = pSprite.z;
		int floorz = sector[sectnum].floorz;
		int height = dz + sz;
		int v12 = (ceildist >> 1) + sector[sectnum].ceilingz;
		if ((SectFlag[sectnum] & 0x2000) != 0 && height < v12)
			height = v12;

		while (true) {
			int v13 = pSprite.sectnum;
			if (height <= sector[v13].floorz || SectBelow[v13] < 0)
				break;
			v6 = SectBelow[v13];
			mychangespritesect(spritenum, SectBelow[v13]);
		}

		if (v6 == sectnum) {
			while (true) {
				int v15 = pSprite.sectnum;
				if (height >= sector[v15].ceilingz)
					break;
				if (SectAbove[v15] < 0)
					break;
				v6 = SectAbove[v15];
				mychangespritesect(spritenum, v6);
			}
		} else {
			pSprite.z = height;
			if ((SectFlag[v6] & 0x2000) != 0) {
				if (spritenum == PlayerList[nLocalPlayer].spriteId)
					D3PlayFX(StaticSound[2], spritenum);
				if (sprite[spritenum].statnum <= 107)
					sprite[spritenum].hitag = 0;
			}
		}

		getzrange(pSprite.x, pSprite.y, pSprite.z - 256, pSprite.sectnum, 128, CLIPMASK0);
		int lohit = zr_florhit;
		int hihit = zr_ceilhit;
		int sprceiling = zr_ceilz;

		int v17 = zr_florz;
		if ((lohit & 0xC000) != 49152)
			v17 = (SectDepth[pSprite.sectnum] & 0xFFFF) + zr_florz;
		if (height > v17) {
			if (dz > 0) {
				bTouchFloor[0] = true;
				if ((lohit & 0xC000) == 49152) {
					int lospr = lohit & 0x3FFF;

					if (pSprite.statnum == 100 && sprite[lospr].statnum != 0 && sprite[lospr].statnum < 100) {
						if ((dz >> 9) != 0)
							DamageEnemy(lospr, spritenum, 2 * (dz >> 9));
						pSprite.zvel = (short) -dz;
					} else {
						if (sprite[lospr].statnum != 0 && sprite[lospr].statnum <= 199)
							movehit = lohit;
						else
							movehit = 0x20000;
						pSprite.zvel = 0;
					}
				} else if (SectBelow[pSprite.sectnum] == -1) {
					movehit = 0x20000;
					if (SectDamage[pSprite.sectnum] != 0) {
						if (pSprite.hitag < 15) {
							IgniteSprite(spritenum);
							pSprite.hitag = 20;
						}
						if (SectDamage[pSprite.sectnum] >> 2 != SectDamage[pSprite.sectnum] >> 4)
							DamageEnemy(spritenum, -1,
									(SectDamage[pSprite.sectnum] >> 2) - (SectDamage[pSprite.sectnum] >> 4));
					}
					pSprite.zvel = 0;
				}
			}
			height = v17;
			pSprite.z = v17;
		} else if (height - ceildist < sprceiling && ((hihit & 0xC000) == 49152 || SectAbove[pSprite.sectnum] == -1)) {
			height = sprceiling + ceildist;
			movehit = 0x10000;
		}

		if (sz <= floorz && height > floorz
				&& (SectDepth[sectnum] != 0 || v6 != sectnum && (SectFlag[v6] & 0x2000) != 0))
			BuildSplash(spritenum, sectnum);
		pSprite.cstat = oldcstat;
		pSprite.z = height;
		if (pSprite.statnum == 100) {
			BuildNear(pSprite.x, pSprite.y, cliptype / 2 + cliptype, pSprite.sectnum);
			movehit |= BelowNear(spritenum, lohit);
		}
		return movehit;
	}

	private void loadremaps() throws Exception {
		palookup = new byte[MAXPALOOKUPS][];
		numshades = 64;

		String[] flookups = { "normal.rmp", "nodim.rmp", "torch.rmp", "notorch.rmp", "brite.rmp", "redbrite.rmp",
				"grnbrite.rmp", "normal.rmp", "nodim.rmp", "torch.rmp", "notorch.rmp", "brite.rmp" };

		Resource fil;
		for (int i = 0; i < 12; i++) {
			if ((fil = BuildGdx.cache.open(flookups[i], 0)) == null)
				throw new Exception("Error reading palette lookup " + "\"" + flookups[i] + "\"!");

			makepalookup(i, null, 0, 0, 0, 0);
			fil.read(palookup[i], 0, 0x4000);
			fil.close();

			origpalookup[i] = palookup[i];
		}
	}

	@Override
	public void loadtables() throws Exception {
		if (tablesloaded == 0) {
			initksqrt();

			sintable = new short[2048];
			textfont = new byte[2048];
			smalltextfont = new byte[2048];
			radarang = new short[1280]; // 1024

			Resource res = BuildGdx.cache.open("tables.dat", 0);
			if (res != null) {
				byte[] buf = new byte[2048 * 2];

				res.read(buf);
				ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sintable);

				res.seek(4096, Whence.Current); // tantable

				buf = new byte[640];
				res.read(buf);
				ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(radarang, 0, 320);
				radarang[320] = 0x4000;

				res.read(textfont, 0, 1024);
				res.read(smalltextfont, 0, 1024);

				pTextfont = new TextFont();
				pSmallTextfont = new SmallTextFont();

				/* kread(fil, britable, 1024); */

				calcbritable();
				res.close();
			} else
				throw new Exception("ERROR: Failed to load TABLES.DAT!");

			tablesloaded = 1;
		}
	}

	@Override
	public short getangle(int xvect, int yvect) {
		if ((xvect | yvect) == 0)
			return (0);
		if (xvect == 0)
			return (short) (512 + ((yvect < 0 ? 1 : 0) << 10));
		if (yvect == 0)
			return (short) ((xvect < 0 ? 1 : 0) << 10);
		if (xvect == yvect)
			return (short) (256 + ((xvect < 0 ? 1 : 0) << 10));
		if (xvect == -yvect)
			return (short) (768 + ((xvect > 0 ? 1 : 0) << 10));

		if (Math.abs((long) xvect) > Math.abs((long) yvect))
			return (short) (((radarang[160 + scale(160, yvect, xvect)] >> 6) + ((xvect < 0 ? 1 : 0) << 10)) & 2047);
		return (short) (((radarang[160 - scale(160, xvect, yvect)] >> 6) + 512 + ((yvect < 0 ? 1 : 0) << 10)) & 2047);
	}

	@Override
	public void loadpalette() throws Exception {
		Resource fil;
		if (paletteloaded != 0)
			return;

		palette = new byte[768];
		curpalette = new Palette();
		palookup = new byte[MAXPALOOKUPS][];

		Console.Println("Loading palettes");
		if ((fil = BuildGdx.cache.open("palette.dat", 0)) == null)
			throw new Exception("Failed to load \"palette.dat\"!");

		fil.read(palette);

		boolean hastransluc = false;
		int file_len = fil.size();
		numshades = (short) ((file_len - 768) >> 7);
		if ((((file_len - 768) >> 7) & 1) <= 0)
			numshades >>= 1;
		else {
			numshades = (short) ((numshades - 255) >> 1);
			hastransluc = true;
		}

		if (palookup[0] == null)
			palookup[0] = new byte[numshades << 8];
		if (transluc == null)
			transluc = new byte[65536];

		globalpal = 0;
		Console.Println("Loading gamma correction tables");
		fil.read(palookup[globalpal], 0, numshades << 8);
		Console.Println("Loading translucency table");

		if (hastransluc) {
			byte[] tmp = new byte[256];
			for (int i = 0; i < 255; i++) {
				fil.read(tmp, 0, 255 - i);
				System.arraycopy(tmp, 0, transluc, (i << 8) + i + 1, 255 - i);
				for (int j = i + 1; j < 256; j++)
					transluc[(j << 8) + i] = transluc[(i << 8) + j];
			}
			for (int i = 0; i < 256; i++)
				transluc[(i << 8) + i] = (byte) i;
		}

		fil.close();

		initfastcolorlookup(30, 59, 11);

		paletteloaded = 1;

		loadremaps();
	}
}
