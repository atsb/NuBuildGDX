package ru.m210projects.Wang.Factory;

import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Types.SPRITE;

public abstract class WangInterpolation extends Interpolation {
	
	public abstract int getSkipValue();
	
	public abstract int getSkipMax();

	public void requestUpdating() {
		if(getSkipValue() != (getSkipMax() - 1)) return;
		requestUpdating = true;
	}
	
	@Override
	public void dospriteinterp(SPRITE tsp, int smoothratio) {
		smoothratio += getSkipValue() << 16;
		smoothratio /= getSkipMax();
		
		super.dospriteinterp(tsp, smoothratio);
	}
	
	@Override
	public void dointerpolations(float smoothratio) {
		smoothratio += getSkipValue() << 16;
		smoothratio /= getSkipMax();
		
		super.dointerpolations(smoothratio);
	}
}
