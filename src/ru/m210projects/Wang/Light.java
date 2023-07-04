package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.RESET_BOOL1;
import static ru.m210projects.Wang.Gameutils.SET_BOOL1;
import static ru.m210projects.Wang.Gameutils.SET_BOOL9;
import static ru.m210projects.Wang.Gameutils.SPRX_BOOL10;
import static ru.m210projects.Wang.Gameutils.SPRX_BOOL5;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG4;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL4;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL6;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL8;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL9;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Names.STAT_LIGHTING;
import static ru.m210projects.Wang.Names.STAT_LIGHTING_DIFFUSE;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;

import ru.m210projects.Build.Types.SPRITE;

public class Light {

	public static final int LIGHT_Match(SPRITE sp) {
		return (SP_TAG2((sp)));
	}

	public static final int LIGHT_Type(SPRITE sp) {
		return (SP_TAG3((sp)));
	}

	public static final int LIGHT_MaxTics(SPRITE sp) {
		return (SP_TAG4((sp)));
	}

	public static final int LIGHT_MaxBright(SPRITE sp) {
		return (SP_TAG5((sp)));
	}

	public static final int LIGHT_MaxDark(SPRITE sp) {
		return (SP_TAG6((sp)));
	}

	public static final int LIGHT_ShadeInc(SPRITE sp) {
		return SP_TAG7(sp);
	}

	public static final boolean LIGHT_Dir(SPRITE sp) {
		return (!!(TEST((sp).extra, SPRX_BOOL10)));
	}

	public static final int LIGHT_DirChange(SPRITE sp) {
		return ((sp).extra ^= (SPRX_BOOL10));
	}

	public static final int LIGHT_Shade(SPRITE sp) {
		return ((sp).shade);
	}

	public static final int LIGHT_FloorShade(SPRITE sp) {
		return ((sp).xoffset);
	}

	public static final int LIGHT_CeilingShade(SPRITE sp) {
		return ((sp).yoffset);
	}

	public static final int LIGHT_Tics(SPRITE sp) {
		return ((sp).z);
	}

	public static final int LIGHT_DiffuseNum(SPRITE sp) {
		return (SP_TAG3((sp)));
	}

	public static final int LIGHT_DiffuseMult(SPRITE sp) {
		return (SP_TAG4((sp)));
	}

	public static final int LIGHT_CONSTANT = 0;
	public static final int LIGHT_FLICKER = 1;
	public static final int LIGHT_FADE = 2;
	public static final int LIGHT_FLICKER_ON = 3;
	public static final int LIGHT_FADE_TO_ON_OFF = 4;

	public static void SectorLightShade(int spnum, int intensity) {
		short w, startwall, endwall;
		byte[] wall_shade;
		short base_shade;
		short wallcount;

		SPRITE sp = sprite[spnum];

		if (TEST_BOOL8(sp))
			intensity = (short) -intensity;

		if (!TEST_BOOL2(sp)) {
			if (!TEST_BOOL6(sp))
				sector[sp.sectnum].floorpal = sp.pal;
			sector[sp.sectnum].floorshade = (byte) (LIGHT_FloorShade(sp) + intensity); // floor change
		}

		if (!TEST_BOOL3(sp)) {
			if (!TEST_BOOL6(sp))
				sector[sp.sectnum].ceilingpal = sp.pal;
			sector[sp.sectnum].ceilingshade = (byte) (LIGHT_CeilingShade(sp) + intensity); // ceiling change
		}

		// change wall
		if (!TEST_BOOL4(sp)) {
			wall_shade = pUser[spnum].WallShade;

			startwall = sector[sp.sectnum].wallptr;
			endwall = (short) (startwall + sector[sp.sectnum].wallnum - 1);

			for (w = startwall, wallcount = 0; w <= endwall; w++) {
				base_shade = wall_shade[wallcount];
				wall[w].shade = (byte) (base_shade + intensity);
				if (!TEST_BOOL6(sp))
					wall[w].pal = sp.pal;
				wallcount++;

				if (TEST(sp.extra, SPRX_BOOL5) && wall[w].nextwall >= 0) {
					base_shade = wall_shade[wallcount];
					wall[wall[w].nextwall].shade = (byte) (base_shade + intensity);
					if (!TEST_BOOL6(sp))
						wall[wall[w].nextwall].pal = sp.pal;
					wallcount++;
				}
			}
		}
	}

	public static void DiffuseLighting(SPRITE sp) {
		short i, nexti;
		short shade;
		SPRITE dsp;

		// diffused lighting
		for (i = headspritestat[STAT_LIGHTING_DIFFUSE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			dsp = sprite[i];

			// make sure matchs match
			if (LIGHT_Match(dsp) != LIGHT_Match(sp))
				continue;

			shade = (short) (sp.shade + ((LIGHT_DiffuseNum(dsp) + 1) * LIGHT_DiffuseMult(dsp)));

			if (shade > LIGHT_MaxDark(sp))
				shade = (short) LIGHT_MaxDark(sp);

			if (!TEST_BOOL6(dsp))
				dsp.pal = sp.pal;

			SectorLightShade(i, shade);
		}
	}

	public static void DoLightingMatch(int match, int state) {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_LIGHTING]; i != -1; i = nexti) {
			 nexti = nextspritestat[i];

			sp = sprite[i];

			if (LIGHT_Match(sp) != match)
				continue;

			switch (LIGHT_Type(sp)) {
			case LIGHT_CONSTANT:

				// initialized
				SET_BOOL9(sp);

				// toggle
				if (state == -1)
					state = (!TEST_BOOL1(sp)) ? 1 : 0;

				if (state == ON) {
					SET_BOOL1(sp);
					sp.shade = (byte) -LIGHT_MaxBright(sp);
					sp.pal = (short) pUser[i].spal; // on
					SectorLightShade(i, sp.shade);
					DiffuseLighting(sp);
				} else {
					RESET_BOOL1(sp);
					sp.shade = (byte) LIGHT_MaxDark(sp);
					sp.pal = 0; // off
					SectorLightShade(i, sp.shade);
					DiffuseLighting(sp);
				}
				break;

			case LIGHT_FLICKER:
			case LIGHT_FADE:
				// initialized
				SET_BOOL9(sp);

				// toggle
				if (state == -1)
					state = (!TEST_BOOL1(sp)) ? 1 : 0;

				if (state == ON) {
					// allow fade or flicker
					SET_BOOL1(sp);
				} else {
					RESET_BOOL1(sp);
					sp.shade = (byte) LIGHT_MaxDark(sp);
					SectorLightShade(i, sp.shade);
					DiffuseLighting(sp);
				}
				break;

			case LIGHT_FADE_TO_ON_OFF:

				// initialized
				SET_BOOL9(sp);

				if (state == ON) {
					if (LIGHT_Dir(sp)) {
						LIGHT_DirChange(sp);
					}
				} else if (state == OFF) {
					if (!LIGHT_Dir(sp)) {
						LIGHT_DirChange(sp);
					}
				}

				// allow fade or flicker
				SET_BOOL1(sp);
				break;

			case LIGHT_FLICKER_ON:

				// initialized
				SET_BOOL9(sp);

				// toggle
				if (state == -1)
					state = (!TEST_BOOL1(sp)) ? 1 : 0;

				if (state == ON) {
					// allow fade or flicker
					SET_BOOL1(sp);
				} else {
					// turn it off till next switch
					short spal = sp.pal;
					RESET_BOOL1(sp);
					sp.pal = 0;
					sp.shade = (byte) LIGHT_MaxDark(sp);
					SectorLightShade(i, sp.shade);
					DiffuseLighting(sp);
					sp.pal = spal;
				}
				break;
			}
		}
	}

	public static void InitLighting() {
		short i, nexti;
		SPRITE sp;

		// processed on level startup
		// puts lights in correct state
		for (i = headspritestat[STAT_LIGHTING]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (!TEST_BOOL9(sp)) {
				DoLightingMatch(LIGHT_Match(sp), TEST_BOOL1(sp) ? 1 : 0);
			}
		}
	}

	public static void DoLighting() {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_LIGHTING]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			// on/off test
			if (!TEST_BOOL1(sp))
				continue;

			switch (LIGHT_Type(sp)) {
			case LIGHT_CONSTANT:
				break;

			case LIGHT_FLICKER:

				sp.z += synctics;
				while (LIGHT_Tics(sp) >= LIGHT_MaxTics(sp)) {
					sp.z -= LIGHT_MaxTics(sp);

					if ((RANDOM_P2(128 << 8) >> 8) > 64) {
						sp.shade = (byte) (-LIGHT_MaxBright(sp)
								+ RANDOM_RANGE(LIGHT_MaxBright(sp) + LIGHT_MaxDark(sp)));
						SectorLightShade(i, sp.shade);
						DiffuseLighting(sp);
					} else {
						// turn off lighting - even colored lighting
						short spal = sp.pal;
						sp.pal = 0;
						sp.shade = (byte) LIGHT_MaxDark(sp);
						SectorLightShade(i, sp.shade);
						DiffuseLighting(sp);
						sp.pal = spal;
					}
				}

				break;

			case LIGHT_FADE:

				sp.z += synctics;

				while (LIGHT_Tics(sp) >= LIGHT_MaxTics(sp)) {
					sp.z -= LIGHT_MaxTics(sp);

					if (LIGHT_Dir(sp)) {
						sp.shade += LIGHT_ShadeInc(sp);
						if (sp.shade >= LIGHT_MaxDark(sp))
							LIGHT_DirChange(sp);
					} else {
						sp.shade -= LIGHT_ShadeInc(sp);
						if (sp.shade <= -LIGHT_MaxBright(sp))
							LIGHT_DirChange(sp);
					}

					SectorLightShade(i, sp.shade);
					DiffuseLighting(sp);
				}

				break;

			case LIGHT_FADE_TO_ON_OFF:

				sp.z += synctics;

				while (LIGHT_Tics(sp) >= LIGHT_MaxTics(sp)) {
					sp.z -= LIGHT_MaxTics(sp);

					if (LIGHT_Dir(sp)) {
						sp.shade += LIGHT_ShadeInc(sp);
						if (sp.shade >= LIGHT_MaxDark(sp)) {
							sp.pal = 0; // off
							LIGHT_DirChange(sp);
							// stop it until switch is hit
							RESET_BOOL1(sp);
						}
					} else {
						sp.shade -= LIGHT_ShadeInc(sp);
						sp.pal = pUser[i].spal; // on
						if (sp.shade <= -LIGHT_MaxBright(sp)) {
							LIGHT_DirChange(sp);
							// stop it until switch is hit
							RESET_BOOL1(sp);
						}
					}
					SectorLightShade(i, sp.shade);
					DiffuseLighting(sp);
				}

				break;

			case LIGHT_FLICKER_ON:

				sp.z += synctics;

				while (LIGHT_Tics(sp) >= LIGHT_MaxTics(sp)) {
					sp.z -= LIGHT_MaxTics(sp);

					if ((RANDOM_P2(128 << 8) >> 8) > 64) {
						sp.shade = (byte) (-LIGHT_MaxBright(sp)
								+ RANDOM_RANGE(LIGHT_MaxBright(sp) + LIGHT_MaxDark(sp)));
						SectorLightShade(i, sp.shade);
						DiffuseLighting(sp);
					} else {
						// turn off lighting - even colored lighting
						short spal = sp.pal;
						sp.pal = 0;
						sp.shade = (byte) LIGHT_MaxDark(sp);
						SectorLightShade(i, sp.shade);
						DiffuseLighting(sp);
						sp.pal = spal;
					}

					if ((RANDOM_P2(128 << 8) >> 8) < 8) {
						// set to full brightness
						sp.shade = (byte) -LIGHT_MaxBright(sp);
						SectorLightShade(i, sp.shade);
						DiffuseLighting(sp);
						// turn it off until a swith happens
						RESET_BOOL1(sp);
					}
				}
				break;
			}
		}
	}

}
