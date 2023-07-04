package ru.m210projects.Build.Render.Software;

public class JniA implements A {

	public JniA(int xdim, int ydim, int[] reciptable) {
		System.load("D:\\Workspace\\BuildGDX\\core\\jni\\a.dll");

		jniinit(xdim, ydim, reciptable);
	}

	public native void jniinit(int xdim, int ydim, int[] reciptable);

	public native byte[] jnigetframeplace();

	public native void jnisetframeplace(byte[] newframeplace);

	public native void jniclearframe(byte col);

	public native void jnisetvlinebpl(int dabpl);

	public native void jnifixtransluscence(byte[] datrans);

	public native void jnisettransnormal();

	public native void jnisettransreverse();

	public native void jnidrawpixel(int ptr, byte col);

	public native void jnisethlinesizes(int logx, int logy, byte[] bufplc);

	public native void jnisetpalookupaddress(byte[] paladdr);

	public native void jnisetuphlineasm4(int bxinc, int byinc);

	public native void jnihlineasm4(int cnt, int skiploadincs, int paloffs, int by, int bx, int p);

	public native void jnisetupslopevlin(int logylogx, byte[] bufplc, int pinc, int bzinc);

	public native void jnislopevlin(int p, byte[] pal, int slopaloffs, int cnt, int bx, int by, int x3, int y3,
			int[] slopalookup, int bz);

	public native void jnisetupvlineasm(int neglogy);

	public native void jnivlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs,
			int p);

	public native void jnisetupmvlineasm(int neglogy);

	public native void jnimvlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs,
			int p);

	public native void jnisetuptvlineasm(int neglogy);

	public native void jnitvlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs,
			int p);

	public native void jnisethlineincs(int x, int y);

	public native void jnisetuphline(byte[] pal, int shade);

	public native void jnimsethlineshift(int logx, int logy);

	public native void jnimhline(byte[] bufplc, int bx, int cntup16, int junk, int by, int p);

	public native void jnitsethlineshift(int logx, int logy);

	public native void jnithline(byte[] bufplc, int bx, int cntup16, int junk, int by, int p);

	public native void jnisetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz);

	public native void jnispritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p);

	public native void jnimsetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz);

	public native void jnimspritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p);

	public native void jnitsetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz);

	public native void jnitspritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p);

	public native void jnisetupdrawslab(int dabpl, byte[] pal, int shade, int trans);

	public native void jnidrawslab(int dx, int v, int dy, int vi, byte[] data, int vptr, int p);

	@Override
	public void setframeplace(byte[] newframeplace) {
		jnisetframeplace(newframeplace);
	}

	@Override
	public void setvlinebpl(int dabpl) {
		jnisetvlinebpl(dabpl);
	}

	@Override
	public void fixtransluscence(byte[] datrans) {
		jnifixtransluscence(datrans);
	}

	@Override
	public void settransnormal() {
		jnisettransnormal();
	}

	@Override
	public void settransreverse() {
		jnisettransreverse();
	}

	@Override
	public void drawpixel(int ptr, byte col) {
		jnidrawpixel(ptr, col);
	}

	@Override
	public void sethlinesizes(int logx, int logy, byte[] bufplc) {
		jnisethlinesizes(logx, logy, bufplc);
	}

	@Override
	public void setpalookupaddress(byte[] paladdr) {
		jnisetpalookupaddress(paladdr);
	}

	@Override
	public void setuphlineasm4(int bxinc, int byinc) {
		jnisetuphlineasm4(bxinc, byinc);
	}

	@Override
	public void hlineasm4(int cnt, int skiploadincs, int paloffs, int by, int bx, int p) {
		jnihlineasm4(cnt, skiploadincs, paloffs, by, bx, p);
	}

	@Override
	public void setupslopevlin(int logylogx, byte[] bufplc, int pinc, int bzinc) {
		jnisetupslopevlin(logylogx, bufplc, pinc, bzinc);
	}

	@Override
	public void slopevlin(int p, byte[] pal, int slopaloffs, int cnt, int bx, int by, int x3, int y3, int[] slopalookup,
			int bz) {
		jnislopevlin(p, pal, slopaloffs, cnt, bx, by, x3, y3, slopalookup, bz);
	}

	@Override
	public void setupvlineasm(int neglogy) {
		jnisetupvlineasm(neglogy);
	}

	@Override
	public void vlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs, int p) {
		jnivlineasm1(vinc, pal, shade, cnt, vplc, bufplc, bufoffs, p);
	}

	@Override
	public void setupmvlineasm(int neglogy) {
		jnisetupmvlineasm(neglogy);
	}

	@Override
	public void mvlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs, int p) {
		jnimvlineasm1(vinc, pal, shade, cnt, vplc, bufplc, bufoffs, p);
	}

	@Override
	public void setuptvlineasm(int neglogy) {
		jnisetuptvlineasm(neglogy);
	}

	@Override
	public void tvlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs, int p) {
		jnitvlineasm1(vinc, pal, shade, cnt, vplc, bufplc, bufoffs, p);
	}

	@Override
	public void sethlineincs(int x, int y) {
		jnisethlineincs(x, y);
	}

	@Override
	public void setuphline(byte[] pal, int shade) {
		jnisetuphline(pal, shade);
	}

	@Override
	public void msethlineshift(int logx, int logy) {
		jnimsethlineshift(logx, logy);
	}

	@Override
	public void mhline(byte[] bufplc, int bx, int cntup16, int junk, int by, int p) {
		jnimhline(bufplc, bx, cntup16, junk, by, p);
	}

	@Override
	public void tsethlineshift(int logx, int logy) {
		jnitsethlineshift(logx, logy);
	}

	@Override
	public void thline(byte[] bufplc, int bx, int cntup16, int junk, int by, int p) {
		jnithline(bufplc, bx, cntup16, junk, by, p);
	}

	@Override
	public void setupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz) {
		jnisetupspritevline(pal, shade, bxinc, byinc, ysiz);
	}

	@Override
	public void spritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p) {
		jnispritevline(bx, by, cnt, bufplc, bufoffs, p);
	}

	@Override
	public void msetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz) {
		jnimsetupspritevline(pal, shade, bxinc, byinc, ysiz);
	}

	@Override
	public void mspritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p) {
		jnimspritevline(bx, by, cnt, bufplc, bufoffs, p);
	}

	@Override
	public void tsetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz) {
		jnitsetupspritevline(pal, shade, bxinc, byinc, ysiz);
	}

	@Override
	public void tspritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p) {
		jnitspritevline(bx, by, cnt, bufplc, bufoffs, p);
	}

	@Override
	public void setupdrawslab(int dabpl, byte[] pal, int shade, int trans) {
		jnisetupdrawslab(dabpl, pal, shade, trans);
	}

	@Override
	public void drawslab(int dx, int v, int dy, int vi, byte[] data, int vptr, int p) {
		jnidrawslab(dx, v, dy, vi, data, vptr, p);
	}

	@Override
	public byte[] getframeplace() {
		return jnigetframeplace();
	}

	@Override
	public void clearframe(byte col) {
		jniclearframe(col);
	}
}
