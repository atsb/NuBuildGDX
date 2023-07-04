//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Render.Types;

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;

public abstract class GL10 implements com.badlogic.gdx.graphics.GL20 {

	protected IntBuffer tempInt;
	protected FloatBuffer tempFloat;
	protected DoubleBuffer tempDouble;

	public static final int GL_VERTEX_ARRAY = 32884;
	public static final int GL_NORMAL_ARRAY = 32885;
	public static final int GL_COLOR_ARRAY = 32886;
	public static final int GL_TEXTURE_COORD_ARRAY = 32888;

	public static final int GL_FOG = 2912;
	public static final int GL_FOG_START = 2915;
	public static final int GL_FOG_END = 2916;
	public static final int GL_FOG_MODE = 2917;
	public static final int GL_FOG_COLOR = 2918;
	public static final int GL_FOG_HINT = 3156;

	public static final int GL_FILL = 6914;
	public static final int GL_LINE = 6913;
	public static final int GL_POINT = 6912;
	public static final int GL_QUADS = 7;

	public static final int GL_SMOOTH = 7425;
	public static final int GL_LINE_SMOOTH_HINT = 3154;
	public static final int GL_PERSPECTIVE_CORRECTION_HINT = 3152;
	public static final int GL_PROJECTION = 5889;
	public static final int GL_MODELVIEW = 5888;
	public static final int GL_PROJECTION_MATRIX = 2983;
	public static final int GL_MODELVIEW_MATRIX = 2982;
	public static final int GL_CLAMP = 10496;
	public static final int GL_ALPHA_TEST = 3008;
	public static final int GL_INTENSITY = 0x8049;

	public static final int GL_TEXTURE0 = 33984;
	public static final int GL_COMBINE_ARB = 34160;
	public static final int GL_COMBINE_RGB_ARB = 34161;
	public static final int GL_COMBINE_ALPHA_ARB = 34162;
	public static final int GL_SOURCE0_RGB_ARB = 34176;
	public static final int GL_SOURCE1_RGB_ARB = 34177;
	public static final int GL_SOURCE2_RGB_ARB = 34178;
	public static final int GL_SOURCE0_ALPHA_ARB = 34184;
	public static final int GL_OPERAND0_RGB_ARB = 34192;
	public static final int GL_OPERAND1_RGB_ARB = 34193;
	public static final int GL_OPERAND2_RGB_ARB = 34194;
	public static final int GL_OPERAND0_ALPHA_ARB = 34200;
	public static final int GL_INTERPOLATE_ARB = 34165;
	public static final int GL_PREVIOUS_ARB = 34168;
	public static final int GL_RGB_SCALE_ARB = 34163;

	public static final int GL_MULTISAMPLE_FILTER_HINT_NV = 34100;
	public static final int GL_MULTISAMPLE = 32925;
	public static final int GL_TEXTURE_ENV = 8960;
	public static final int GL_TEXTURE_ENV_MODE = 8704;
	public static final int GL_MODULATE = 8448;
	public static final int GL_RGB_SCALE = 34163;

	public static final int GL_CLIP_PLANE0 = 12288;
	public static final int GL_MAX_CLIP_PLANES = 3378;

	public static final int GL_ANY_SAMPLES_PASSED = 35887;
	public static final int GL_SAMPLES_PASSED = 35092;
	public static final int GL_QUERY_RESULT = 34918;

	public GL10() {
		tempInt = BufferUtils.newIntBuffer(8);
		tempFloat = BufferUtils.newFloatBuffer(8);
		tempDouble = BufferUtils.newDoubleBuffer(4);
	}

	protected DoubleBuffer toPlaneBufferd(double a, double b, double c, double d) {
		tempDouble.clear();
		tempDouble.put(a);
		tempDouble.put(b);
		tempDouble.put(c);
		tempDouble.put(d);
		tempDouble.flip();
		return tempDouble;
	}

	protected FloatBuffer toPlaneBufferf(float a, float b, float c, float d) {
		tempFloat.clear();
		tempFloat.put(a);
		tempFloat.put(b);
		tempFloat.put(c);
		tempFloat.put(d);
		tempFloat.flip();
		return tempFloat;
	}

	protected IntBuffer toBuffer(int[] src, int offset) {
		int n = src.length - offset;
		if (tempInt.capacity() < n)
			tempInt = BufferUtils.newIntBuffer(n);
		else
			tempInt.clear();
		tempInt.put(src, offset, n);
		tempInt.flip();
		return tempInt;
	}

	protected IntBuffer toBuffer(int n, int[] src, int offset) {
		if (tempInt.capacity() < n)
			tempInt = BufferUtils.newIntBuffer(n);
		else
			tempInt.clear();
		tempInt.put(src, offset, n);
		tempInt.flip();
		return tempInt;
	}

	protected FloatBuffer toBuffer(float[] src, int offset) {
		int n = src.length - offset;
		if (tempFloat.capacity() < n)
			tempFloat = BufferUtils.newFloatBuffer(n);
		else
			tempFloat.clear();
		tempFloat.put(src, offset, src.length - offset);
		tempFloat.flip();
		return tempFloat;
	}

	public abstract void glClipPlanef(int plane, float a, float b, float c, float d);

	public abstract void glDepthMask(int param);

	public abstract void glDepthRange(double near_val, double far_val);

	public abstract void glAlphaFunc(int func, float ref);

	public abstract void glClientActiveTexture(int texture);

	public abstract void glColor4f(float red, float green, float blue, float alpha);

	public abstract void glColorPointer(int size, int type, int stride, Buffer pointer);

	public abstract void glDisableClientState(int array);

	public abstract void glEnableClientState(int array);

	public abstract void glFogf(int pname, float param);

	public abstract void glFogfv(int pname, FloatBuffer params);

	public abstract void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar);

	public abstract void glLightModelf(int pname, float param);

	public abstract void glLightModelfv(int pname, FloatBuffer params);

	public abstract void glLightf(int light, int pname, float param);

	public abstract void glLightfv(int light, int pname, FloatBuffer params);

	public abstract void glLoadIdentity();

	public abstract void glLoadMatrixf(FloatBuffer m);

	public abstract void glLogicOp(int opcode);

	public abstract void glMaterialf(int face, int pname, float param);

	public abstract void glMaterialfv(int face, int pname, FloatBuffer params);

	public abstract void glMatrixMode(int mode);

	public abstract void glMultMatrixf(FloatBuffer m);

	public abstract void glMultiTexCoord4f(int target, float s, float t, float r, float q);

	public abstract void glNormal3f(float nx, float ny, float nz);

	public abstract void glNormalPointer(int type, int stride, Buffer pointer);

	public abstract void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar);

	public abstract void glPointSize(float size);

	public abstract void glPopMatrix();

	public abstract void glPushMatrix();

	public abstract void glRotatef(float angle, float x, float y, float z);

	public abstract void glScalef(float x, float y, float z);

	public abstract void glShadeModel(int mode);

	public abstract void glTexCoordPointer(int size, int type, int stride, Buffer pointer);

	public abstract void glTexEnvf(int target, int pname, float param);

	public abstract void glTexEnvfv(int target, int pname, FloatBuffer params);

	public abstract void glTranslatef(float x, float y, float z);

	public abstract void glVertexPointer(int size, int type, int stride, Buffer pointer);

	@Override
	public abstract void glViewport(int x, int y, int width, int height);

	public abstract void glDeleteTextures(int n, int[] textures, int offset);

	public abstract void glFogfv(int pname, float[] params, int offset);

	public abstract void glGenTextures(int n, int[] textures, int offset);

	public abstract void glGetIntegerv(int pname, int[] params, int offset);

	public abstract int glGetInteger(int pname);

	public abstract void glLightModelfv(int pname, float[] params, int offset);

	public abstract void glLightfv(int light, int pname, float[] params, int offset);

	public abstract void glLoadMatrixf(float[] m, int offset);

	public abstract void glMaterialfv(int face, int pname, float[] params, int offset);

	public abstract void glMultMatrixf(float[] m, int offset);

	public abstract void glTexEnvfv(int target, int pname, float[] params, int offset);

	public abstract void glPolygonMode(int face, int mode);

	public abstract void glDeleteBuffers(int n, int[] buffers, int offset);

	public abstract void glGenBuffers(int n, int[] buffers, int offset);

	public abstract void glGetLightfv(int light, int pname, FloatBuffer params);

	public abstract void glGetMaterialfv(int face, int pname, FloatBuffer params);

	public abstract void glGetTexEnviv(int env, int pname, IntBuffer params);

	public abstract void glPointParameterf(int pname, float param);

	public abstract void glPointParameterfv(int pname, FloatBuffer params);

	public abstract void glTexEnvi(int target, int pname, int param);

	public abstract void glTexEnviv(int target, int pname, int[] params, int offset);

	public abstract void glTexEnviv(int target, int pname, IntBuffer params);

	public abstract void glTexParameterfv(int target, int pname, float[] params, int offset);

	public abstract void glTexParameteriv(int target, int pname, int[] params, int offset);

	public abstract void glColorPointer(int size, int type, int stride, int pointer);

	public abstract void glNormalPointer(int type, int stride, int pointer);

	public abstract void glTexCoordPointer(int size, int type, int stride, int pointer);

	public abstract void glVertexPointer(int size, int type, int stride, int pointer);

	public abstract void glBegin(int type);

	public abstract void glEnd();

	public abstract void glFogi(int pname, int params);

	public abstract void glBindTexture(int target, IntBuffer texture);

	public abstract void glLoadMatrixf(float[][] m);

	public abstract void glLoadMatrix(Matrix4 m);

	public abstract void glVertex2i(int x, int y);

	public abstract void glVertex2f(float x, float y);

	public abstract void glVertex2d(double x, double y);

	public abstract void glVertex3d(double x, double y, double z);

	public abstract void glTexCoord2f(float s, float t);

	public abstract void glTexCoord2d(double s, double t);

	public abstract void glColor4ub(int red, int green, int blue, int alpha);

	public abstract void glPopAttrib();

	public abstract void glPushAttrib(int mask);

	public abstract void glMultiTexCoord2d(int target, double s, double t);

	public abstract void glReadBuffer(int mode);

	public abstract int glGenQueries();

	public abstract void glBeginQuery(int target, int id);

	public abstract void glEndQuery(int target);

	public abstract int glGetQueryObjecti(int id, int pname);

	@Override
	public void glAttachShader(int program, int shader) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glBindAttribLocation(int program, int index, String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glCompileShader(int shader) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int glCreateProgram() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int glCreateShader(int type) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glDeleteProgram(int program) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glDeleteShader(int shader) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glDetachShader(int program, int shader) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glDisableVertexAttribArray(int index) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glEnableVertexAttribArray(int index) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String glGetActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String glGetActiveUniform(int program, int index, IntBuffer size, Buffer type) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int glGetAttribLocation(int program, String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetProgramiv(int program, int pname, IntBuffer params) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String glGetProgramInfoLog(int program) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetShaderiv(int shader, int pname, IntBuffer params) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String glGetShaderInfoLog(int shader) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetUniformfv(int program, int location, FloatBuffer params) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetUniformiv(int program, int location, IntBuffer params) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int glGetUniformLocation(int program, String name) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean glIsProgram(int program) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean glIsShader(int shader) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glLinkProgram(int program) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glShaderSource(int shader, String string) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glStencilMaskSeparate(int face, int mask) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform1f(int location, float x) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform1fv(int location, int count, FloatBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform1fv(int location, int count, float[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform1i(int location, int x) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform1iv(int location, int count, IntBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform1iv(int location, int count, int[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform2f(int location, float x, float y) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform2fv(int location, int count, FloatBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform2fv(int location, int count, float[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform2i(int location, int x, int y) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform2iv(int location, int count, IntBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform2iv(int location, int count, int[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform3f(int location, float x, float y, float z) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform3fv(int location, int count, FloatBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform3fv(int location, int count, float[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform3i(int location, int x, int y, int z) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform3iv(int location, int count, IntBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform3iv(int location, int count, int[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform4f(int location, float x, float y, float z, float w) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform4fv(int location, int count, FloatBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform4fv(int location, int count, float[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform4i(int location, int x, int y, int z, int w) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform4iv(int location, int count, IntBuffer v) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniform4iv(int location, int count, int[] v, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glUseProgram(int program) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glValidateProgram(int program) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib1f(int indx, float x) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib1fv(int indx, FloatBuffer values) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib2f(int indx, float x, float y) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib2fv(int indx, FloatBuffer values) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib3f(int indx, float x, float y, float z) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib3fv(int indx, FloatBuffer values) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttrib4fv(int indx, FloatBuffer values) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void glReleaseShaderCompiler() {
		throw new UnsupportedOperationException("not implemented");
	}
}
