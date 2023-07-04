package ru.m210projects.Build.Pattern;

public abstract class FontHandler {

	protected BuildFont[] fonts;
	protected final int nFonts;
	
	public FontHandler(int nFonts)
	{
		this.nFonts = nFonts;
		fonts = new BuildFont[nFonts];
	}
	
	protected abstract BuildFont init(int i);
	
	public BuildFont getFont(int i) {
		if(i < 0 && i >= nFonts)
			throw new IllegalArgumentException("Wrong font number");
		
		if(fonts[i] == null)
			fonts[i] = init(i);
		return fonts[i];
	}

}
