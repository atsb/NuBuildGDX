// This file is part of BuildGDX.
// Copyright (C) 2017-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Render.TextureHandle;

import static ru.m210projects.Build.Engine.*;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import ru.m210projects.Build.Architecture.BuildGdx;

public abstract class IndexedShader extends ShaderProgram {

	public static final String defaultFragment = "uniform sampler2D u_texture;" //
			+ "uniform sampler2D u_palette;" //
			+ "uniform sampler2D u_palookup;" //
			+ "uniform int u_shade;" //
			+ "uniform int u_numshades;" //
			+ "uniform float u_visibility;" //
			+ "uniform float u_alpha;" //
			+ "uniform int u_draw255;" //
//			+ "uniform int u_fogenable;" //
//			+ "uniform vec4 u_fogcolour;" //
//			+ "uniform float u_fogstart;" //
//			+ "uniform float u_fogend;" //
//			+ "uniform float u_cx1;" //
//			+ "uniform float u_cy1;" //
//			+ "uniform float u_cx2;" //
//			+ "uniform float u_cy2;" //
			+ "varying float v_dist;" //
			+ "varying vec2 v_texCoords;" //
//			+ "float fog(float dist) {" //
//			+ "	if(u_fogenable == 1)" //
//			+ "		return clamp(1.0 - (u_fogend - dist) / (u_fogend - u_fogstart), 0.0, 1.0);" //
//			+ "	else return 0.0;" //
//			+ "}" //
			+ "float getpalookup(int dashade) {" //
			+ "	float davis = v_dist * u_visibility;" //
//			+ "   if(u_fogenable != -1) davis = u_visibility / 64.0;" //
			+ "	float shade = (min(max(float(dashade) + davis, 0.0), float(u_numshades - 1)));" //
			+ "	return shade / 64.0;" //
			+ "}" //
			+ "void main()" //
			+ "{" //
//			+ "	if(gl_FragCoord.x < u_cx1 || gl_FragCoord.x > u_cx2" //
//			+ "		|| gl_FragCoord.y > u_cy1 || gl_FragCoord.y < u_cy2)" //
//			+ "		discard;" //
			+ "	float fi = texture2D(u_texture, v_texCoords).r;" //
			+ "	if(fi == 1.0)" //
			+ "	{" //
			+ "		if(u_draw255 == 0) discard;" //
			+ "		fi -= 0.5 / 256.0;" //
			+ "	}" //
			+ "	float index = texture2D(u_palookup, vec2(fi, getpalookup(u_shade))).r;" //
			+ "	if(index == 1.0) index -= 0.5 / 256.0;" //
			+ "	vec4 src = vec4(texture2D(u_palette, vec2(index, 0.0)).rgb, u_alpha);" //
//			+ "   if(u_fogenable == -1) " //
			+ "		gl_FragColor = src; " //
//			+ "	else gl_FragColor = mix(src, u_fogcolour, fog(v_dist));" //
			+ "}"; //

	public static final String defaultVertex = "varying float v_dist;" //
			+ "varying vec2 v_texCoords;" //
			+ "void main()" //
			+ "{" //
			+ "	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;" //
			+ "	gl_ClipVertex = gl_ModelViewMatrix * gl_Vertex;" //
			+ " v_dist = gl_ClipVertex.z / gl_ClipVertex.w;" //
			+ "	gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;" //
			+ " v_texCoords = gl_TexCoord[0].xy;" //
			+ "}"; //

	protected int paletteloc;
	protected int numshadesloc;
	protected int visibilityloc;
	protected int palookuploc;
	protected int shadeloc;
	protected int alphaloc;
	protected int draw255loc;

    protected boolean isBinded;
//	private boolean glfog = false; //

	protected int lastPal, lastShade, lastVisibility;
	protected float lastAlpha;
	protected boolean drawLastIndex;

	public IndexedShader() throws Exception {
		super(defaultVertex, defaultFragment);
		init();
	}

	public IndexedShader(String vertexShader, String fragmentShader) throws Exception {
		super(vertexShader, fragmentShader);
		init();
	}

	protected void init() throws Exception {
		if (!isCompiled())
			throw new Exception("Shader compile error: " + getLog());

		this.paletteloc = getUniformLocation("u_palette");
		this.numshadesloc = getUniformLocation("u_numshades");
		this.visibilityloc = getUniformLocation("u_visibility");
		this.palookuploc = getUniformLocation("u_palookup");
		this.shadeloc = getUniformLocation("u_shade");
		this.alphaloc = getUniformLocation("u_alpha");
		this.draw255loc = getUniformLocation("u_draw255");

    }

	public abstract void bindPalette(int unit);

	public abstract void bindPalookup(int unit, int pal);

	@Override
	public void begin() {
		super.begin();
		isBinded = true;
	}

	@Override
	public void end() {
		super.end();
		isBinded = false;
	}

	public boolean isBinded() {
		return isBinded;
	}

    public void setTextureParams(int pal, int shade) {
		setUniformi(numshadesloc, numshades);

		bindPalette(GL20.GL_TEXTURE1);
		setUniformi(paletteloc, 1);

		bindPalookup(GL20.GL_TEXTURE2, pal);
		this.lastPal = pal;
		setUniformi(palookuploc, 2);

		setUniformi(shadeloc, shade);
		this.lastShade = shade;
		BuildGdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
	}

	public void setTransparent(float alpha) {
		setUniformf(alphaloc, alpha);
		this.lastAlpha = alpha;
	}

	public void setDrawLastIndex(boolean enable) {
		setUniformi(draw255loc, enable ? 1 : 0);
		this.drawLastIndex = enable;
	}

	public void setVisibility(int vis) {
		setUniformf(visibilityloc, vis / 64.0f);
		this.lastVisibility = vis;
	}

	public int getPal() {
		return lastPal;
	}

	public int getShade() {
		return lastShade;
	}

	public int getVisibility() {
		return lastVisibility;
	}

	public float getTransparent() {
		return lastAlpha;
	}

	public boolean getDrawLastIndex() {
		return drawLastIndex;
	}

}
