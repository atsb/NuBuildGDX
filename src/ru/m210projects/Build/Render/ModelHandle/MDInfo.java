package ru.m210projects.Build.Render.ModelHandle;

import static ru.m210projects.Build.Engine.MAXPALOOKUPS;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDAnimation;
import ru.m210projects.Build.Render.ModelHandle.MDModel.MDSkinmap;

public class MDInfo extends ModelInfo {

	protected MDSkinmap skinmap;
	protected MDAnimation animations;
	protected String[] frames;
	protected int numframes;

	public MDInfo(String file, Type type) {
		super(file, type);
	}

	public int getFrames() {
		return numframes;
	}

	public MDSkinmap getSkins() {
		return skinmap;
	}

	public MDAnimation getAnimations() {
		return animations;
	}

	public int getFrameIndex(String framename) {
		for (int i = 0; i < numframes; i++) {
			String fr = frames[i];
			if (fr != null && fr.equalsIgnoreCase(framename)) {
				return i;
			}
		}

		return (-3); // frame name invalid
	}

	protected String readString(Resource bb, int len) {
		byte[] buf = new byte[len];
		bb.read(buf);

		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == 0)
				return new String(buf, 0, i);
		}
		return new String(buf);
	}

	protected MDSkinmap getSkin(int palnum, int skinnum, int surfnum) {
		for (MDSkinmap sk = skinmap; sk != null; sk = sk.next)
			if (sk.palette == palnum && skinnum == sk.skinnum && surfnum == sk.surfnum)
				return sk;

		return null;
	}

	protected void addSkin(MDSkinmap sk) {
		sk.next = skinmap;
		skinmap = sk;
	}

	public int setSkin(String skinfn, int palnum, int skinnum, int surfnum, double param, double specpower,
			double specfactor) {
		if (skinfn == null)
			return -2;
		if (palnum >= MAXPALOOKUPS)
			return -3;

		if (type == Type.Md2)
			surfnum = 0;

		MDSkinmap sk = getSkin(palnum, skinnum, surfnum);
		if (sk == null) // no replacement yet defined
			addSkin(sk = new MDSkinmap());

		sk.palette = palnum;
		sk.skinnum = skinnum;
		sk.surfnum = surfnum;
		sk.param = (float) param;
		sk.specpower = (float) specpower;
		sk.specfactor = (float) specfactor;
		sk.fn = skinfn;

		return 0;
	}

	public int setAnimation(String framestart, String frameend, int fpssc, int flags) {
		MDAnimation ma = new MDAnimation();
		int i;

		// find index of start frame
		i = getFrameIndex(framestart);
		if (i == numframes)
			return -2;
		ma.startframe = i;

		// find index of finish frame which must trail start frame
		i = getFrameIndex(frameend);
		if (i == numframes)
			return -3;
		ma.endframe = i;

		ma.fpssc = fpssc;
		ma.flags = flags;

		ma.next = animations;
		animations = ma;

		return 0;
	}
}
