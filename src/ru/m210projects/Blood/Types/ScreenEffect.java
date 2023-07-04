// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Types;

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Build.Render.Types.GL10.GL_DST_COLOR;
import static ru.m210projects.Build.Render.Types.GL10.GL_ONE_MINUS_SRC_ALPHA;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.Types.FadeEffect;

public class ScreenEffect {

	public static final String damagefade = "DAMAGE";
	public static final String drownfade = "DROWN";
	public static final String blindfade = "BLIND";
	public static final String pickupfade = "PICKUP";

	public static void FadeInit() {
		Console.Println("Initializing fade effects", 0);
		engine.registerFade(damagefade, new FadeEffect(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
				if (intensive > 0) {
					r = 3 * (intensive + 32);
					a = 2 * (intensive + 32);
				} else
					r = a = 0;
				if (r > 255)
					r = 255;
				if (a > 255)
					a = 255;
			}

			@Override
			public void draw(FadeShader shader) {
				FadeEffect.setParams(shader, r, g, b, a, sfactor, dfactor);
				FadeEffect.render(shader);

				int multiple = intensive / 2;
				if (multiple > 170)
					multiple = 170;

				FadeEffect.setParams(shader, multiple, 0, 0, 0, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				FadeEffect.render(shader);
			}
		});

		engine.registerFade(drownfade, new FadeEffect(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
				intensive >>= 5;
				if (intensive < 128) {
					r = b = 3 * intensive;
					a = 0;
				} else {
					r = b = 255;
					a = 2 * (intensive - 128);
				}

				if (r > 255)
					r = 255;
				if (b > 255)
					b = 255;
				if (a > 255)
					a = 255;
			}

			@Override
			public void draw(FadeShader shader) {
				FadeEffect.setParams(shader, r, g, b, a, sfactor, dfactor);
				FadeEffect.render(shader);

				if (intensive > 0) {

					int kintensive = intensive >> 6;
					int attenuation = 128 - kintensive;
					if (attenuation < 0)
						attenuation = 0;

					FadeEffect.setParams(shader, attenuation, attenuation, attenuation, 0, GL_DST_COLOR, GL_DST_COLOR);
					FadeEffect.render(shader);
					if (intensive > 2000) {
						FadeEffect.render(shader);
					}

					int multiple = intensive / 10;
					if (multiple > 160)
						multiple = 160;
					FadeEffect.setParams(shader, 0, 0, 0, multiple, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					FadeEffect.render(shader);
				}
			}
		});

		engine.registerFade(blindfade, new FadeEffect(GL_DST_COLOR, GL_DST_COLOR) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
			}

			@Override
			public void draw(FadeShader shader) {
				if (intensive > 0) {
					int attenuation = 128 - intensive;
					if (attenuation < 0)
						attenuation = 0;

					FadeEffect.setParams(shader, attenuation, attenuation, attenuation, 0, sfactor, dfactor);
					FadeEffect.render(shader);
					FadeEffect.render(shader);

					int multiple = intensive;
					if (multiple > 255)
						multiple = 255;

					FadeEffect.setParams(shader, 0, 0, 0, multiple, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					FadeEffect.render(shader);
				}
			}
		});

		engine.registerFade(pickupfade, new FadeEffect(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
				if (intensive > 0) {
					g = r = 4 * intensive;
					a = (intensive + 32);
				} else
					g = r = a = 0;

				if (r > 255)
					r = 255;
				if (g > 255)
					g = 255;
				if (a > 255)
					a = 255;
			}

			@Override
			public void draw(FadeShader shader) {
				FadeEffect.setParams(shader, r, g, b, a, sfactor, dfactor);
				FadeEffect.render(shader);

				if (intensive > 0) {
					int multiple = intensive;
					if (multiple > 255)
						multiple = 255;

					FadeEffect.setParams(shader, multiple, multiple, 0, 0, GL_ONE_MINUS_SRC_ALPHA,
							GL_ONE_MINUS_SRC_ALPHA);
					FadeEffect.render(shader);
				}
			}
		});
	}

	public static void resetEffects() {
		engine.updateFade(damagefade, 0);
		engine.updateFade(drownfade, 0);
		engine.updateFade(blindfade, 0);
		engine.updateFade(pickupfade, 0);
	}
}
