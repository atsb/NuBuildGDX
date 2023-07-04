package ru.m210projects.Build.Render.GdxRender.Shaders;

import static ru.m210projects.Build.Engine.numshades;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.GdxRender.BuildCamera;
import ru.m210projects.Build.Render.TextureHandle.IndexedShader;

public abstract class IndexedSkyShaderProgram extends IndexedShader {

	protected int cameraloc;
	protected int projTransloc;
	protected int transformloc;
	protected int mirrorloc;

	public IndexedSkyShaderProgram() throws Exception {
		super(SkyShader.vertex, SkyShader.fragment);
	}

	@Override
	protected void init() throws Exception {
		if (!isCompiled())
			throw new Exception("Shader compile error: " + getLog());

		this.paletteloc = getUniformLocation("u_palette");
		this.palookuploc = getUniformLocation("u_palookup");
		this.numshadesloc = getUniformLocation("u_numshades");
		this.shadeloc = getUniformLocation("u_shade");
		this.alphaloc = getUniformLocation("u_alpha");

		this.cameraloc = getUniformLocation("u_camera");
		this.projTransloc = getUniformLocation("u_projTrans");
		this.transformloc = getUniformLocation("u_transform");
		this.mirrorloc = getUniformLocation("u_mirror");
	}

	public void prepare(BuildCamera cam) {
		setUniformf(cameraloc, cam.position.x, cam.position.y, cam.position.z);
		setUniformMatrix(projTransloc, cam.combined);
	}

	public void transform(Matrix4 transform) {
		setUniformMatrix(transformloc, transform);
	}

	public void mirror(boolean mirror) {
		setUniformi(mirrorloc, mirror ? 1 : 0);
	}

	@Override
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

}
