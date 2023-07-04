package ru.m210projects.Wang.Type;

public class Remote_Control {
	public static final int sizeof = 54;
	
	public short cursectnum, lastcursectnum, pang;
	public int xvect, yvect, oxvect, oyvect, slide_xvect, slide_yvect;
	public int posx, posy, posz;
	public int oposx, oposy, oposz;
	
	public void copy(Remote_Control src)
	{
		this.cursectnum = src.cursectnum;
		this.lastcursectnum = src.lastcursectnum;
		this.pang = src.pang;

		this.xvect = src.xvect;
		this.yvect = src.yvect;
		this.oxvect = src.oxvect;
		this.oyvect = src.oyvect;
		this.slide_xvect = src.slide_xvect;
		this.slide_yvect = src.slide_yvect;

		this.posx = src.posx;
		this.posy = src.posy;
		this.posz = src.posz;

		this.oposx = src.oposx;
		this.oposy = src.oposy;
		this.oposz = src.oposz;
	}
}
