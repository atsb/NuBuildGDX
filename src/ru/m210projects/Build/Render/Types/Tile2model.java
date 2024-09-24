/*
* Tile2model for Polymost
* by Jonathon Fowler
* See the included license file "BUILDLIC.TXT" for license info.
*
* This file has been modified
* by Alexander Makarov-[M210] (m210-2007@mail.ru)
*/

package ru.m210projects.Build.Render.Types;




public class Tile2model {
	// maps build tiles to particular animation frames of a model

	public Tile2model next;
	public int skinnum; //another skin file for the same model with the same palette (normal, bloody)
	public int framenum; // calculate the number from the name when declaring
	public int palette;
	public float smoothduration;
	public boolean disposable;

	public Tile2model clone(boolean disposable) {
		Tile2model out = new Tile2model();
		out.skinnum = skinnum;
		out.framenum = framenum;
		out.smoothduration = smoothduration;
		out.palette = palette;
		if (next != null)
			out.next = next.clone(disposable);
		out.disposable = disposable;

		return out;
	}
}
