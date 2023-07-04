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

package ru.m210projects.Build.Types;

public class Timer {
	private static long startTime;
	private static long spentTime;

	private static long startAvrTime;
	private static long LastSummTime;
	private static long Lastcount;

	private static long AvrResultTime;
	private static long AvrSummTime;
	private static long Avrcount;

	public static void start() {
		startTime = System.nanoTime();
	}

	public static long result() {
		spentTime = System.nanoTime() - startTime;
		System.out.println(spentTime / 1000000f + " microsec");
		return spentTime;
	}

	public static void startAverage() {
		startTime = System.nanoTime();
		if (startAvrTime == -1)
			startAvrTime = startTime;
	}

	public static long resultAverage(int updateTimeSec) {
		spentTime = System.nanoTime() - startTime;
		LastSummTime += spentTime;
		Lastcount++;
		if ((System.nanoTime() - startAvrTime) / 1000000f >= (updateTimeSec * 1000f)) {
			long result = AvrResultTime = (LastSummTime / Lastcount);
			AvrSummTime += result;
			Avrcount++;
			System.out.println(
					"T: " + (result / 1000000f) + " ms, Avr: " + ((AvrSummTime / Avrcount) / 1000000f) + " ms");

			startAvrTime = -1;
			LastSummTime = 0;
			Lastcount = 0;
			return result;
		}

		return AvrResultTime;
	}

	public static void resetAverage() {
		AvrSummTime = 0;
		Avrcount = 0;
	}

	public static float getAverage() {
		if(Avrcount != 0)
			return ((AvrSummTime / Avrcount) / 1000000f);
		return AvrSummTime / 1000000f;
	}

	public static long result(String comment) {
		spentTime = System.nanoTime() - startTime;

		System.out.println(comment + " : " + spentTime / 1000000f + " microsec");
		return spentTime;
	}

	public static void startFPS() {
		startTime = System.nanoTime();
	}

	public static int FPSresult() {
		spentTime = ((System.nanoTime() - startTime));
		long fps = (long) (1000000000.0 / spentTime);
		System.out.println(fps + " fps");
		return (int) fps;
	}
}
