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

public class MemLog {
	
	final static int mb = 1024 * 1024; 
	
	public static void log(String id) {
		System.out.println("***** Heap utilization statistics [MB] *****\n on id : " + id);
		
		// get Runtime instance
		Runtime instance = Runtime.getRuntime();
		// available memory
		System.out.println("Total Memory: " + instance.totalMemory() / mb);
		// free memory
		System.out.println("Free Memory: " + instance.freeMemory() / mb);
		// used memory
		System.out.println("Used Memory: "
				+ (instance.totalMemory() - instance.freeMemory()) / mb);
		// Maximum available memory
		System.out.println("Max Memory: " + instance.maxMemory() / mb);
	}
	
	public static void logTotal(String id) {
		System.out.println("***** Heap utilization statistics [MB] *****\n on id : " + id);
		
		// get Runtime instance
		Runtime instance = Runtime.getRuntime();
		// available memory
		System.out.println("Total Memory: " + instance.totalMemory() / mb);
	}
	
	public static long startMem;
	public static void start()
	{
		Runtime instance = Runtime.getRuntime();
		startMem = (instance.totalMemory() - instance.freeMemory());
	}
	
	public static void result(String txt)
	{
		int kb = 1024; 
		Runtime instance = Runtime.getRuntime();
		long mem = (instance.totalMemory() - instance.freeMemory()) - startMem;
		
		System.out.println(txt + " : " + mem / kb +" kb");
	}
	
	public static int used()
	{
		Runtime instance = Runtime.getRuntime();
		return (int) ((instance.totalMemory() - instance.freeMemory()) / mb);
	}
	
	public static int total()
	{
		Runtime instance = Runtime.getRuntime();
		return (int) (instance.maxMemory() / mb);
	}
}
