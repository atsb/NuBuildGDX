// This file is part of LSPGDX.
// Copyright (C) 2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.GdxRender.GDXRenderer;

public class LSPPolygdx extends GDXRenderer {

	public LSPPolygdx(Engine engine) {
		super(engine, new LSPMapSettings());
	}

}
