/*
 * High-colour textures support for Polymost
 * by Jonathon Fowler
 * See the included license file "BUILDLIC.TXT" for license info.
 */

package ru.m210projects.Build.Render.Types;

public class Hudtyp {
	public float xadd, yadd, zadd;
	public short angadd, flags, fov;

	@Override
	public Hudtyp clone() {
		Hudtyp out = new Hudtyp();

		out.xadd = xadd;
		out.yadd = yadd;
		out.zadd = zadd;

		out.angadd = angadd;
		out.flags = flags;
		out.fov = fov;

		return out;
	}

}
