package ru.m210projects.Witchaven.Types;

public class Delayitem {
     public int  item;
     public int  timer;
     public boolean  func;
     
     public void memmove(Delayitem source)
     {
    	 this.item = source.item;
    	 this.timer = source.timer;
    	 this.func = source.func;
     }
}
