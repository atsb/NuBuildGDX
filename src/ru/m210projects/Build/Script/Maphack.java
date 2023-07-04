// This file is part of BuildGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Script;

import java.util.HashMap;
import java.util.Map;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Strhandler.toLowerCase;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.Types.Spriteext;

public class Maphack extends Scriptfile {

	private long MapCRC;
	private final Spriteext[] spriteext;

	private enum Token {
		MapCRC, Sprite,

		AngleOffset, XOffset, YOffset, ZOffset, NoModel,

		Error, EOF
    }

	private final static Map<String, Token> basetokens = new HashMap<String, Token>() {
		private static final long serialVersionUID = 1L;
		{
			put("sprite", Token.Sprite);
			put("crc32", Token.MapCRC);
		}
	};

	private final static Map<String, Token> sprite_tokens = new HashMap<String, Token>() {
		private static final long serialVersionUID = 1L;
		{
			put("angoff", Token.AngleOffset);
			put("mdxoff", Token.XOffset);
			put("mdyoff", Token.YOffset);
			put("mdzoff", Token.ZOffset);
			put("notmd", Token.NoModel);
		}
	};

	public Maphack() { // new maphack
		super("", new byte[0]);

		spriteext = new Spriteext[MAXSPRITES + MAXUNIQHUDID];
		for (int i = 0; i < spriteext.length; i++)
			spriteext[i] = new Spriteext();
	}

	public Maphack(String filename) {
		super(filename, BuildGdx.cache.getBytes(filename, 0));

		spriteext = new Spriteext[MAXSPRITES + MAXUNIQHUDID];
		for (int i = 0; i < spriteext.length; i++)
			spriteext[i] = new Spriteext();

		Integer value;
		while (true) {
			switch (gettoken(basetokens)) {
			case MapCRC:
				Integer crc32 = getsymbol();
				if (crc32 == null)
					break;

				MapCRC = crc32 & 0xFFFFFFFFL;
				break;
			case Sprite:
				Integer sprnum = getsymbol();
				if (sprnum == null)
					break;

				switch (gettoken(sprite_tokens)) {
				default:
					break;
				case AngleOffset:
					if ((value = getsymbol()) != null)
						spriteext[sprnum].angoff = value.shortValue();
					break;
				case XOffset:
					if ((value = getsymbol()) != null)
						spriteext[sprnum].xoff = value;
					break;
				case YOffset:
					if ((value = getsymbol()) != null)
						spriteext[sprnum].yoff = value;
					break;
				case ZOffset:
					if ((value = getsymbol()) != null)
						spriteext[sprnum].zoff = value;
					break;
				case NoModel:
					spriteext[sprnum].flags |= 1; // SPREXT_NOTMD;
					break;
				}

				break;
			case Error:
				break;
			case EOF:
				return;
			default:
				break;
			}
		}
	}

	private Token gettoken(Map<String, Token> list) {
		int tok;
		if ((tok = gettoken()) == -2)
			return Token.EOF;

		Token out = list.get(toLowerCase(textbuf.substring(tok, textptr)));
		if (out != null)
			return out;

		errorptr = textptr;
		return Token.Error;
	}

	public Spriteext getSpriteInfo(int spriteid) {
		if (spriteid < 0 || spriteid >= spriteext.length)
			return null;
		return spriteext[spriteid];
	}

	public long getMapCRC() {
		return MapCRC;
	}
}
