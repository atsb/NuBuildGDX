// On-screen Display command
// for the Build Engine
// by Jonathon Fowler (jf@jonof.id.au)
//
// This file has been modified by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build.OnSceenDisplay;

public class OSDCOMMAND {
	
	private final int CVARTYPE = 1;
	private final int FUNCTYPE = 2;

	String name;
    String desc;
    OSDCVARFUNC func;
    
    public float value;
    int type;
    int min;
    int max;
    
    public OSDCOMMAND(String name, String desc, float value, OSDCVARFUNC func, int min, int max) {
    	this(name, desc, value, min, max);
	    if(func != null)
	    {
	    	this.func = func;
	    	this.type |= FUNCTYPE;
	    }
	}
    
    public OSDCOMMAND(String name, String desc, OSDCVARFUNC func) {
    	this.name = name;
    	this.desc = desc;
    	this.func = func;
    	this.type = FUNCTYPE;
	}
    
    public OSDCOMMAND(String name, String desc, float value, int min, int max) {
    	this.name = name;
    	this.desc = desc;
    	this.value = value;
    	this.type = CVARTYPE;
    	setRange(min, max);
	}
    
    public OSDCOMMAND setRange(int min, int max)
    {
    	this.min = min;
    	this.max = max;
    	
    	return this;
    }
    
    public boolean SetValue(float value)
    {
		if(value >= min && value <= max) {
			this.value = value;
			return true;
		}
		return false;
    }

    public int Set(String value)
    {
    	if(type == FUNCTYPE && func != null) {
			func.execute();
			return 2;
		}
    	
    	if(value == null) 
    		return 1; // description
    	try {
    		float var = Float.parseFloat(value);
    		if((type & CVARTYPE) != 0) {
    			if(SetValue(var))
    			{
    				if((type & FUNCTYPE) != 0 && func != null) {
    					func.execute();
        				return 2;
    				}
    				return 0;
    			}
    		}
        } catch (NumberFormatException e) {}
    	//out of range
    	return -1;
    }
}
