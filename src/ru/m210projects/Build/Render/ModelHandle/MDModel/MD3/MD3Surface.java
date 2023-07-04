// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Render.ModelHandle.MDModel.MD3;

import java.nio.FloatBuffer;

public class MD3Surface {
	public int id; // IDP3(0x33806873)
	public String nam; // ascz surface name
	public int flags; // ?
	public int numframes, numshaders; // numframes same as md3head,max shade=~256,vert=~4096,tri=~8192
	public int numverts;
	public int numtris;
	public int ofstris;
	public int ofsshaders;
	public int ofsuv;
	public int ofsxyzn;
	public int ofsend;

	public int[][] tris;
	public FloatBuffer uv;
	public MD3Shader[] shaders;
	public MD3Vertice[][] xyzn;
}
