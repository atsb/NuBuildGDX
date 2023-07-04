// This file has been modified from LibGDX's original release
// by Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.desktop.Lwjgl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.Types.GL10;

public class LwjglGL10 extends GL10 {

	@Override
	public void glActiveTexture(int texture) {
		if (BuildGdx.graphics.getGLVersion().isVersionEqualToOrHigher(1, 3))
			GL13.glActiveTexture(texture);
	}

	@Override
	public void glBindBuffer(int target, int buffer) {
		GL15.glBindBuffer(target, buffer);
	}

	@Override
	public void glBindFramebuffer(int target, int framebuffer) {
		EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
	}

	@Override
	public void glBindRenderbuffer(int target, int renderbuffer) {
		EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		GL11.glBindTexture(target, texture);
	}

	@Override
	public void glBlendColor(float red, float green, float blue, float alpha) {
		GL14.glBlendColor(red, green, blue, alpha);
	}

	@Override
	public void glBlendEquation(int mode) {
		GL14.glBlendEquation(mode);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		GL11.glBlendFunc(sfactor, dfactor);
	}

	@Override
	public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		GL14.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	@Override
	public void glBufferData(int target, int size, Buffer data, int usage) {
		if (data == null)
			GL15.glBufferData(target, size, usage);
		else if (data instanceof ByteBuffer)
			GL15.glBufferData(target, (ByteBuffer) data, usage);
		else if (data instanceof IntBuffer)
			GL15.glBufferData(target, (IntBuffer) data, usage);
		else if (data instanceof FloatBuffer)
			GL15.glBufferData(target, (FloatBuffer) data, usage);
		else if (data instanceof DoubleBuffer)
			GL15.glBufferData(target, (DoubleBuffer) data, usage);
		else if (data instanceof ShortBuffer) //
			GL15.glBufferData(target, (ShortBuffer) data, usage);
	}

	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data) {
		if (data == null)
			throw new GdxRuntimeException("Using null for the data not possible, blame LWJGL");
		else if (data instanceof ByteBuffer)
			GL15.glBufferSubData(target, offset, (ByteBuffer) data);
		else if (data instanceof IntBuffer)
			GL15.glBufferSubData(target, offset, (IntBuffer) data);
		else if (data instanceof FloatBuffer)
			GL15.glBufferSubData(target, offset, (FloatBuffer) data);
		else if (data instanceof DoubleBuffer)
			GL15.glBufferSubData(target, offset, (DoubleBuffer) data);
		else if (data instanceof ShortBuffer) //
			GL15.glBufferSubData(target, offset, (ShortBuffer) data);
	}

	@Override
	public int glCheckFramebufferStatus(int target) {
		return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
	}

	@Override
	public void glClear(int mask) {
		GL11.glClear(mask);
	}

	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
	}

	@Override
	public void glClearDepthf(float depth) {
		GL11.glClearDepth(depth);
	}

	@Override
	public void glClearStencil(int s) {
		GL11.glClearStencil(s);
	}

	@Override
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		GL11.glColorMask(red, green, blue, alpha);
	}

	@Override
	public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		if (data instanceof ByteBuffer) {
			GL13.glCompressedTexImage2D(target, level, internalformat, width, height, border, (ByteBuffer) data);
		} else {
			throw new GdxRuntimeException(
					"Can't use " + data.getClass().getName() + " with this method. Use ByteBuffer instead.");
		}
	}

	@Override
	public final void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int imageSize, Buffer data) {
		if (!(data instanceof ByteBuffer))
			throw new GdxRuntimeException(
					"Can't use " + data.getClass().getName() + " with this method. Use ByteBuffer. Blame LWJGL");
		GL13.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, (ByteBuffer) data);
	}

	@Override
	public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height,
			int border) {
		GL11.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	@Override
	public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width,
			int height) {
		GL11.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public void glCullFace(int mode) {
		GL11.glCullFace(mode);
	}

	@Override
	public void glDeleteBuffers(int n, IntBuffer buffers) {
		GL15.glDeleteBuffers(buffers);
	}

	@Override
	public void glDeleteBuffer(int buffer) {
		GL15.glDeleteBuffers(buffer);
	}

	@Override
	public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
		EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffers);
	}

	@Override
	public void glDeleteFramebuffer(int framebuffer) {
		EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer);
	}

	@Override
	public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
		EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffers);
	}

	@Override
	public void glDeleteRenderbuffer(int renderbuffer) {
		EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
	}

	@Override
	public void glDeleteTextures(int n, IntBuffer textures) {
		GL11.glDeleteTextures(textures);
	}

	@Override
	public void glDeleteTexture(int texture) {
		GL11.glDeleteTextures(texture);
	}

	@Override
	public void glDepthFunc(int func) {
		GL11.glDepthFunc(func);
	}

	@Override
	public void glDepthMask(boolean flag) {
		GL11.glDepthMask(flag);
	}

	@Override
	public void glDepthRangef(float zNear, float zFar) {
		GL11.glDepthRange(zNear, zFar);
	}

	@Override
	public void glDisable(int cap) {
		GL11.glDisable(cap);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		GL11.glDrawArrays(mode, first, count);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		if (indices instanceof ShortBuffer && type == GL11.GL_UNSIGNED_SHORT)
			GL11.glDrawElements(mode, (ShortBuffer) indices);
		else if (indices instanceof ByteBuffer && type == GL11.GL_UNSIGNED_SHORT)
			GL11.glDrawElements(mode, ((ByteBuffer) indices).asShortBuffer());
		else if (indices instanceof ByteBuffer && type == GL11.GL_UNSIGNED_BYTE)
			GL11.glDrawElements(mode, (ByteBuffer) indices);
		else
			throw new GdxRuntimeException("Can't use " + indices.getClass().getName()
					+ " with this method. Use ShortBuffer or ByteBuffer instead. Blame LWJGL");
	}

	@Override
	public void glEnable(int cap) {
		GL11.glEnable(cap);
	}

	@Override
	public void glFinish() {
		GL11.glFinish();
	}

	@Override
	public void glFlush() {
		GL11.glFlush();
	}

	@Override
	public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
		EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
	}

	@Override
	public void glFrontFace(int mode) {
		GL11.glFrontFace(mode);
	}

	@Override
	public void glGenBuffers(int n, IntBuffer buffers) {
		GL15.glGenBuffers(buffers);
	}

	@Override
	public int glGenBuffer() {
		return GL15.glGenBuffers();
	}

	@Override
	public void glGenFramebuffers(int n, IntBuffer framebuffers) {
		EXTFramebufferObject.glGenFramebuffersEXT(framebuffers);
	}

	@Override
	public int glGenFramebuffer() {
		return EXTFramebufferObject.glGenFramebuffersEXT();
	}

	@Override
	public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
		EXTFramebufferObject.glGenRenderbuffersEXT(renderbuffers);
	}

	@Override
	public int glGenRenderbuffer() {
		return EXTFramebufferObject.glGenRenderbuffersEXT();
	}

	@Override
	public void glGenTextures(int n, IntBuffer textures) {
		GL11.glGenTextures(textures);
	}

	@Override
	public int glGenTexture() {
		return GL11.glGenTextures();
	}

	@Override
	public void glGenerateMipmap(int target) {
		EXTFramebufferObject.glGenerateMipmapEXT(target);
	}

	@Override
	public void glGetBooleanv(int pname, Buffer params) {
		GL11.glGetBoolean(pname, (ByteBuffer) params);
	}

	@Override
	public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
		GL15.glGetBufferParameter(target, pname, params);
	}

	@Override
	public int glGetError() {
		return GL11.glGetError();
	}

	@Override
	public void glGetFloatv(int pname, FloatBuffer params) {
		GL11.glGetFloat(pname, params);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
		EXTFramebufferObject.glGetFramebufferAttachmentParameterEXT(target, attachment, pname, params);
	}

	@Override
	public void glGetIntegerv(int pname, IntBuffer params) {
		GL11.glGetInteger(pname, params);
	}

	@Override
	public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
		EXTFramebufferObject.glGetRenderbufferParameterEXT(target, pname, params);
	}

	@Override
	public String glGetString(int name) {
		return GL11.glGetString(name);
	}

	@Override
	public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
		GL11.glGetTexParameter(target, pname, params);
	}

	@Override
	public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
		GL11.glGetTexParameter(target, pname, params);
	}

	@Override
	public void glGetVertexAttribPointerv(int index, int pname, Buffer pointer) {
		throw new UnsupportedOperationException("unsupported, won't implement");
	}

	@Override
	public void glHint(int target, int mode) {
		GL11.glHint(target, mode);
	}

	@Override
	public boolean glIsBuffer(int buffer) {
		return GL15.glIsBuffer(buffer);
	}

	@Override
	public boolean glIsEnabled(int cap) {
		return GL11.glIsEnabled(cap);
	}

	@Override
	public boolean glIsFramebuffer(int framebuffer) {
		return EXTFramebufferObject.glIsFramebufferEXT(framebuffer);
	}

	@Override
	public boolean glIsRenderbuffer(int renderbuffer) {
		return EXTFramebufferObject.glIsRenderbufferEXT(renderbuffer);
	}

	@Override
	public boolean glIsTexture(int texture) {
		return GL11.glIsTexture(texture);
	}

	@Override
	public void glLineWidth(float width) {
		GL11.glLineWidth(width);
	}

	@Override
	public void glPixelStorei(int pname, int param) {
		GL11.glPixelStorei(pname, param);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		GL11.glPolygonOffset(factor, units);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		if (pixels instanceof ByteBuffer)
			GL11.glReadPixels(x, y, width, height, format, type, (ByteBuffer) pixels);
		else if (pixels instanceof ShortBuffer)
			GL11.glReadPixels(x, y, width, height, format, type, (ShortBuffer) pixels);
		else if (pixels instanceof IntBuffer)
			GL11.glReadPixels(x, y, width, height, format, type, (IntBuffer) pixels);
		else if (pixels instanceof FloatBuffer)
			GL11.glReadPixels(x, y, width, height, format, type, (FloatBuffer) pixels);
		else
			throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
					+ " with this method. Use ByteBuffer, ShortBuffer, IntBuffer or FloatBuffer instead. Blame LWJGL");
	}

	@Override
	public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
		EXTFramebufferObject.glRenderbufferStorageEXT(target, internalformat, width, height);
	}

	@Override
	public void glSampleCoverage(float value, boolean invert) {
		GL13.glSampleCoverage(value, invert);
	}

	@Override
	public void glScissor(int x, int y, int width, int height) {
		GL11.glScissor(x, y, width, height);
	}

	@Override
	public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
		throw new UnsupportedOperationException("unsupported, won't implement");
	}

	@Override
	public void glStencilFunc(int func, int ref, int mask) {
		GL11.glStencilFunc(func, ref, mask);
	}

	@Override
	public void glStencilMask(int mask) {
		GL11.glStencilMask(mask);
	}

	@Override
	public void glStencilOp(int fail, int zfail, int zpass) {
		GL11.glStencilOp(fail, zfail, zpass);
	}

	@Override
	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format,
			int type, Buffer pixels) {
		if (pixels == null)
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer) null);
		else if (pixels instanceof ByteBuffer)
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer) pixels);
		else if (pixels instanceof ShortBuffer)
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ShortBuffer) pixels);
		else if (pixels instanceof IntBuffer)
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (IntBuffer) pixels);
		else if (pixels instanceof FloatBuffer)
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (FloatBuffer) pixels);
		else if (pixels instanceof DoubleBuffer)
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type,
					(DoubleBuffer) pixels);
		else
			throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
					+ " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		// LwjglGraphics.major is should to be 1 if we are in LwjglGL10.
		if (BuildGdx.graphics.getGLVersion().getMinorVersion() < 2 && param == GL12.GL_CLAMP_TO_EDGE)
			param = GL11.GL_CLAMP;
		GL11.glTexParameterf(target, pname, param);
	}

	@Override
	public void glTexParameterfv(int target, int pname, FloatBuffer params) {
		GL11.glTexParameter(target, pname, params);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		GL11.glTexParameteri(target, pname, param);
	}

	@Override
	public void glTexParameteriv(int target, int pname, IntBuffer params) {
		GL11.glTexParameter(target, pname, params);
	}

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
			int type, Buffer pixels) {
		if (pixels instanceof ByteBuffer)
			GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ByteBuffer) pixels);
		else if (pixels instanceof ShortBuffer)
			GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ShortBuffer) pixels);
		else if (pixels instanceof IntBuffer)
			GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (IntBuffer) pixels);
		else if (pixels instanceof FloatBuffer)
			GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (FloatBuffer) pixels);
		else if (pixels instanceof DoubleBuffer)
			GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (DoubleBuffer) pixels);
		else
			throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
					+ " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
	}

	@Override
	public void glDrawElements(int mode, int count, int type, int indices) {
		GL11.glDrawElements(mode, count, type, indices);
	}

	@Override
	public void glAlphaFunc(int func, float ref) {
		GL11.glAlphaFunc(func, ref);
	}

	@Override
	public void glClientActiveTexture(int texture) {
		try {
			GL13.glClientActiveTexture(texture);
		} catch (Throwable ex) {

		}
	}

	@Override
	public void glColor4f(float red, float green, float blue, float alpha) {
		GL11.glColor4f(red, green, blue, alpha);
	}

	@Override
	public void glColorPointer(int size, int type, int stride, Buffer pointer) {
		if (pointer instanceof FloatBuffer && type == GL11.GL_FLOAT)
			GL11.glColorPointer(size, stride, (FloatBuffer) pointer);
		else if (pointer instanceof ByteBuffer && type == GL11.GL_FLOAT)
			GL11.glColorPointer(size, stride, ((ByteBuffer) pointer).asFloatBuffer());
		else if (pointer instanceof ByteBuffer && type == GL11.GL_UNSIGNED_BYTE)
			GL11.glColorPointer(size, true, stride, (ByteBuffer) pointer);
		else
			throw new GdxRuntimeException("Can't use " + pointer.getClass().getName()
					+ " with this method, use FloatBuffer or ByteBuffer. blame LWJGL");
	}

	@Override
	public void glDisableClientState(int array) {
		GL11.glDisableClientState(array);
	}

	@Override
	public void glEnableClientState(int array) {
		GL11.glEnableClientState(array);
	}

	@Override
	public void glFogf(int pname, float param) {
		GL11.glFogf(pname, param);
	}

	@Override
	public void glFogfv(int pname, FloatBuffer params) {
		GL11.glFog(pname, params);
	}

	@Override
	public void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar) {
		GL11.glFrustum(left, right, bottom, top, zNear, zFar);
	}

	@Override
	public void glLightModelf(int pname, float param) {
		GL11.glLightModelf(pname, param);
	}

	@Override
	public void glLightModelfv(int pname, FloatBuffer params) {
		GL11.glLightModel(pname, params);
	}

	@Override
	public void glLightf(int light, int pname, float param) {
		GL11.glLightf(light, pname, param);
	}

	@Override
	public void glLightfv(int light, int pname, FloatBuffer params) {
		GL11.glLight(light, pname, params);
	}

	@Override
	public void glLoadIdentity() {
		GL11.glLoadIdentity();
	}

	@Override
	public void glLoadMatrixf(FloatBuffer m) {
		GL11.glLoadMatrix(m);
	}

	@Override
	public void glLogicOp(int opcode) {
		GL11.glLogicOp(opcode);
	}

	@Override
	public void glMaterialf(int face, int pname, float param) {
		GL11.glMaterialf(face, pname, param);
	}

	@Override
	public void glMaterialfv(int face, int pname, FloatBuffer params) {
		GL11.glMaterial(face, pname, params);
	}

	@Override
	public void glMatrixMode(int mode) {
		GL11.glMatrixMode(mode);
	}

	@Override
	public void glMultMatrixf(FloatBuffer m) {
		GL11.glMultMatrix(m);
	}

	@Override
	public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		GL13.glMultiTexCoord4f(target, s, t, r, q);
	}

	@Override
	public void glNormal3f(float nx, float ny, float nz) {
		GL11.glNormal3f(nx, ny, nz);
	}

	@Override
	public void glNormalPointer(int type, int stride, Buffer pointer) {
		if (pointer instanceof FloatBuffer && type == GL11.GL_FLOAT)
			GL11.glNormalPointer(stride, (FloatBuffer) pointer);
		else if (pointer instanceof ByteBuffer && type == GL11.GL_FLOAT)
			GL11.glNormalPointer(stride, ((ByteBuffer) pointer).asFloatBuffer());
		else if (pointer instanceof ByteBuffer && type == GL11.GL_BYTE)
			GL11.glNormalPointer(stride, (ByteBuffer) pointer);
		else
			throw new GdxRuntimeException("Can't use " + pointer.getClass().getName()
					+ " with this method. GL10.GL_SHORT not supported. Use FloatBuffer instead. Blame LWJGL");
	}

	@Override
	public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
		GL11.glOrtho(left, right, bottom, top, zNear, zFar);
	}

	@Override
	public void glPointSize(float size) {
		GL11.glPointSize(size);
	}

	@Override
	public void glPopMatrix() {
		GL11.glPopMatrix();
	}

	@Override
	public void glPushMatrix() {
		GL11.glPushMatrix();
	}

	@Override
	public void glRotatef(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	@Override
	public void glShadeModel(int mode) {
		GL11.glShadeModel(mode);
	}

	@Override
	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		if (pointer instanceof ShortBuffer && type == GL11.GL_SHORT)
			GL11.glTexCoordPointer(size, stride, (ShortBuffer) pointer);
		else if (pointer instanceof ByteBuffer && type == GL11.GL_SHORT)
			GL11.glTexCoordPointer(size, stride, ((ByteBuffer) pointer).asShortBuffer());
		else if (pointer instanceof FloatBuffer && type == GL11.GL_FLOAT)
			GL11.glTexCoordPointer(size, stride, (FloatBuffer) pointer);
		else if (pointer instanceof ByteBuffer && type == GL11.GL_FLOAT)
			GL11.glTexCoordPointer(size, stride, ((ByteBuffer) pointer).asFloatBuffer());
		else
			throw new GdxRuntimeException("Can't use " + pointer.getClass().getName()
					+ " with this method. Use ShortBuffer or FloatBuffer or ByteBuffer instead with GL_FLOAT or GL_SHORT. GL_BYTE is not supported. Blame LWJGL");
	}

	@Override
	public void glTexEnvf(int target, int pname, float param) {
		GL11.glTexEnvf(target, pname, param);
	}

	@Override
	public void glTexEnvfv(int target, int pname, FloatBuffer params) {
		GL11.glTexEnv(target, pname, params);
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	@Override
	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		if (pointer instanceof FloatBuffer && type == GL11.GL_FLOAT)
			GL11.glVertexPointer(size, stride, ((FloatBuffer) pointer));
		else if (pointer instanceof ByteBuffer && type == GL11.GL_FLOAT)
			GL11.glVertexPointer(size, stride, ((ByteBuffer) pointer).asFloatBuffer());
		else
			throw new GdxRuntimeException("Can't use " + pointer.getClass().getName()
					+ " with this method. Use FloatBuffer or ByteBuffers with GL10.GL_FLOAT instead. Blame LWJGL");
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		GL11.glViewport(x, y, width, height);
	}

	@Override
	public void glDeleteTextures(int n, int[] textures, int offset) {
		GL11.glDeleteTextures(toBuffer(n, textures, offset));
	}

	@Override
	public void glFogfv(int pname, float[] params, int offset) {
		GL11.glFog(pname, toBuffer(params, offset));
	}

	@Override
	public void glGenTextures(int n, int[] textures, int offset) {
		for (int i = offset; i < offset + n; i++)
			textures[i] = GL11.glGenTextures();
	}

	IntBuffer getBuffer = BufferUtils.createIntBuffer(100);

	@Override
	public void glGetIntegerv(int pname, int[] params, int offset) {
		GL11.glGetInteger(pname, getBuffer);
		// FIXME Yeah, so. This sucks as well :D LWJGL does not set pos/lim.
		for (int i = offset, j = 0; i < params.length; i++, j++) {
			if (j == getBuffer.capacity())
				return;
			params[i] = getBuffer.get(j);
		}
	}

	@Override
	public void glLightModelfv(int pname, float[] params, int offset) {
		GL11.glLightModel(pname, toBuffer(params, offset));
	}

	@Override
	public void glLightfv(int light, int pname, float[] params, int offset) {
		GL11.glLight(light, pname, toBuffer(params, offset));
	}

	@Override
	public void glLoadMatrixf(float[] m, int offset) {
		GL11.glLoadMatrix(toBuffer(m, offset));
	}

	@Override
	public void glMaterialfv(int face, int pname, float[] params, int offset) {
		GL11.glMaterial(face, pname, toBuffer(params, offset));
	}

	@Override
	public void glMultMatrixf(float[] m, int offset) {
		GL11.glMultMatrix(toBuffer(m, offset));
	}

	@Override
	public void glTexEnvfv(int target, int pname, float[] params, int offset) {
		GL11.glTexEnv(target, pname, toBuffer(params, offset));
	}

	@Override
	public void glPolygonMode(int face, int mode) {
		GL11.glPolygonMode(face, mode);
	}

	@Override
	public void glDeleteBuffers(int n, int[] buffers, int offset) {
		GL15.glDeleteBuffers(toBuffer(n, buffers, offset));
	}

	@Override
	public void glGenBuffers(int n, int[] buffers, int offset) {
		for (int i = offset; i < offset + n; i++)
			buffers[i] = GL15.glGenBuffers();
	}

	@Override
	public void glGetLightfv(int light, int pname, FloatBuffer params) {
		GL11.glGetLight(light, pname, params);
	}

	@Override
	public void glGetMaterialfv(int face, int pname, FloatBuffer params) {
		GL11.glGetMaterial(face, pname, params);
	}

	@Override
	public void glGetTexEnviv(int env, int pname, IntBuffer params) {
		GL11.glGetTexEnv(env, pname, params);
	}

	@Override
	public void glPointParameterf(int pname, float param) {
		GL14.glPointParameterf(pname, param);
	}

	@Override
	public void glPointParameterfv(int pname, FloatBuffer params) {
		GL14.glPointParameter(pname, params);
	}

	@Override
	public void glTexEnvi(int target, int pname, int param) {
		GL11.glTexEnvi(target, pname, param);
	}

	@Override
	public void glTexEnviv(int target, int pname, int[] params, int offset) {
		GL11.glTexEnv(target, pname, toBuffer(params, offset));
	}

	@Override
	public void glTexEnviv(int target, int pname, IntBuffer params) {
		GL11.glTexEnv(target, pname, params);
	}

	@Override
	public void glTexParameterfv(int target, int pname, float[] params, int offset) {
		GL11.glTexParameter(target, pname, toBuffer(params, offset));
	}

	@Override
	public void glTexParameteriv(int target, int pname, int[] params, int offset) {
		GL11.glTexParameter(target, pname, toBuffer(params, offset));
	}

	@Override
	public void glColorPointer(int size, int type, int stride, int pointer) {
		GL11.glColorPointer(size, type, stride, pointer);
	}

	@Override
	public void glNormalPointer(int type, int stride, int pointer) {
		GL11.glNormalPointer(type, stride, pointer);
	}

	@Override
	public void glTexCoordPointer(int size, int type, int stride, int pointer) {
		GL11.glTexCoordPointer(size, type, stride, pointer);
	}

	@Override
	public void glVertexPointer(int size, int type, int stride, int pointer) {
		GL11.glVertexPointer(size, type, stride, pointer);
	}

	@Override
	public void glBegin(int type) {
		GL11.glBegin(type);
	}

	@Override
	public void glEnd() {
		GL11.glEnd();
	}

	@Override
	public void glFogi(int pname, int params) {
		GL11.glFogf(pname, params);
	}

	@Override
	public void glBindTexture(int target, IntBuffer texture) {
		GL11.glBindTexture(target, texture.get(0));
	}

	FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

	@Override
	public void glLoadMatrixf(float[][] m) {
		matrixBuffer.clear();
		for (int i = 0; i < m.length; i++)
			matrixBuffer.put(m[i]);
		matrixBuffer.rewind();
		GL11.glLoadMatrix(matrixBuffer);
	}

	@Override
	public void glLoadMatrix(Matrix4 m) {
		matrixBuffer.clear();
		matrixBuffer.put(m.getValues());
		matrixBuffer.rewind();
		GL11.glLoadMatrix(matrixBuffer);
	}

	@Override
	public void glVertex2i(int x, int y) {
		GL11.glVertex2i(x, y);
	}

	@Override
	public void glVertex2f(float x, float y) {
		GL11.glVertex2f(x, y);
	}

	@Override
	public void glVertex2d(double x, double y) {
		GL11.glVertex2d(x, y);
	}

	@Override
	public void glVertex3d(double x, double y, double z) {
		GL11.glVertex3d(x, y, z);
	}

	@Override
	public void glTexCoord2f(float s, float t) {
		GL11.glTexCoord2f(s, t);
	}

	@Override
	public void glTexCoord2d(double s, double t) {
		GL11.glTexCoord2d(s, t);
	}

	@Override
	public void glColor4ub(int red, int green, int blue, int alpha) {
		GL11.glColor4f((red & 0xFF) / 255f, (green & 0xFF) / 255f, (blue & 0xFF) / 255f, (alpha & 0xFF) / 255f);
	}

	@Override
	public void glPopAttrib() {
		GL11.glPopAttrib();
	}

	@Override
	public void glPushAttrib(int mask) {
		GL11.glPushAttrib(mask);
	}

	@Override
	public void glMultiTexCoord2d(int target, double s, double t) {
		GL13.glMultiTexCoord2d(target, s, t);
	}

	@Override
	public void glReadBuffer(int mode) {
		GL11.glReadBuffer(mode);
	}

	@Override
	public void glDepthRange(double near_val, double far_val) {
		GL11.glDepthRange(near_val, far_val);
	}

	@Override
	public void glDepthMask(int param) {
		GL11.glDepthMask(param != GL11.GL_FALSE);
	}

	@Override
	public void glClipPlanef(int plane, float a, float b, float c, float d) {
		GL11.glClipPlane(plane, toPlaneBufferd(a, b, c, d));
	}

	@Override
	public int glGetInteger(int pname) {
		return GL11.glGetInteger(pname);
	}

	@Override
	public int glGenQueries() {
		return GL15.glGenQueries();
	}

	@Override
	public void glBeginQuery(int target, int id) {
		GL15.glBeginQuery(target, id);
	}

	@Override
	public void glEndQuery(int target) {
		GL15.glEndQuery(target);
	}

	@Override
	public int glGetQueryObjecti(int id, int pname) {
		return GL15.glGetQueryObjecti(id, pname);
	}
}
