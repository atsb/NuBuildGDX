package ru.m210projects.Build.Render;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_CULL_FACE;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST;
import static com.badlogic.gdx.graphics.GL20.GL_POLYGON_OFFSET_FILL;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.Types.GL10;

public class ParameterManager {

	public void blendFunc(int sfactor, int dfactor) {
		BuildGdx.gl20.glBlendFunc(sfactor, dfactor);
	}

	public void pixelStorei(int pname, int param) {
		BuildGdx.gl20.glPixelStorei(pname, param);
	}

	public void depthFunc(int func) {
		BuildGdx.gl20.glDepthFunc(func);
	}

	public void depthRangef(float zNear, float zFar) {
		BuildGdx.gl20.glDepthRangef(zNear, zFar);
	}

	public void activeTexture(int texture) {
		BuildGdx.gl20.glActiveTexture(texture);
	}

	public void setDepthTest(boolean enable) {
		if (enable)
			BuildGdx.gl20.glEnable(GL_DEPTH_TEST);
		else
			BuildGdx.gl20.glDisable(GL_DEPTH_TEST);
	}

	public void setCullFace(boolean enable) {
		if (enable)
			BuildGdx.gl20.glEnable(GL_CULL_FACE);
		else
			BuildGdx.gl20.glDisable(GL_CULL_FACE);
	}

	public void viewport(int x, int y, int width, int height) {
		BuildGdx.gl20.glViewport(x, y, width, height);
	}

	public void setBlend(boolean enable) {
		if (enable)
			BuildGdx.gl20.glEnable(GL_BLEND);
		else
			BuildGdx.gl20.glDisable(GL_BLEND);
	}

	public void setTexture2D(boolean enable) {
		if (enable)
			BuildGdx.gl20.glEnable(GL_TEXTURE_2D);
		else
			BuildGdx.gl20.glDisable(GL_TEXTURE_2D);
	}

	public void frontFace(int mode) {
		BuildGdx.gl20.glFrontFace(mode);
	}

	public void depthMask(boolean flag) {
		BuildGdx.gl20.glDepthMask(flag);
	}

	public void polygonOffset(float factor, float units) {
		BuildGdx.gl20.glPolygonOffset(factor, units);
	}

	public void setPolygonOffset(boolean enable) {
		if (enable)
			BuildGdx.gl20.glEnable(GL_POLYGON_OFFSET_FILL);
		else
			BuildGdx.gl20.glDisable(GL_POLYGON_OFFSET_FILL);
	}

	// Deprecated method

	public void setMultisample(boolean enable) {
		if (enable)
			BuildGdx.gl.glEnable(GL10.GL_MULTISAMPLE);
		else
			BuildGdx.gl.glDisable(GL10.GL_MULTISAMPLE);
	}

	public void loadMatrixf(float[][] m) {
		BuildGdx.gl.glLoadMatrixf(m);
	}

	public void ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
		BuildGdx.gl.glOrthof(left, right, bottom, top, zNear, zFar);
	}

	public void setAlphaTest(boolean enable) {
		if (enable)
			BuildGdx.gl.glEnable(GL10.GL_ALPHA_TEST);
		else
			BuildGdx.gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	public void alphaFunc(int func, float ref) {
		BuildGdx.gl.glAlphaFunc(func, ref);
	}

	public void matrixMode(int mode) {
		BuildGdx.gl.glMatrixMode(mode);
	}

	public void loadIdentity() { // XXX choosed Matrix
		BuildGdx.gl.glLoadIdentity();
	}

	public void texEnvf(int target, int pname, float param) { // XXX ?
		BuildGdx.gl.glTexEnvf(target, pname, param);
	}

	public void fogi(int pname, int params) {

		BuildGdx.gl.glFogi(pname, params);
	}

	public void fogf(int pname, float param) {
		if (pname == GL10.GL_FOG_START) {

		}

		if (pname == GL10.GL_FOG_END) {

		}

		BuildGdx.gl.glFogf(pname, param);
	}

	public void fogfv(int pname, float[] params, int offset) {
		if (pname == GL10.GL_FOG_COLOR) {

		}
		BuildGdx.gl.glFogfv(pname, params, offset);
	}

	public void setFog(boolean enable) {
		if (enable)
			BuildGdx.gl.glEnable(GL10.GL_FOG);
		else
			BuildGdx.gl.glDisable(GL10.GL_FOG);
	}

	public void popMatrix() {

	}

	public void pushMatrix() {

	}

	public void rotate(float angle, float x, float y, float z) {
		BuildGdx.gl.glRotatef(angle, x, y, z);
	}

	public void scale(float x, float y, float z) {
		BuildGdx.gl.glScalef(x, y, z);
	}

	public void translate(float x, float y, float z) {
		BuildGdx.gl.glTranslatef(x, y, z);
	}

}
