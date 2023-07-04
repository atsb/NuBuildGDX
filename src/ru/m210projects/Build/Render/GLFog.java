package ru.m210projects.Build.Render;

import static com.badlogic.gdx.graphics.GL20.GL_DONT_CARE;
import static com.badlogic.gdx.graphics.GL20.GL_LINEAR;
import static com.badlogic.gdx.graphics.GL20.GL_NICEST;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.palookupfog;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_COLOR;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_END;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_HINT;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_MODE;
import static ru.m210projects.Build.Render.Types.GL10.GL_FOG_START;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Render.TextureHandle.TextureManager;

public class GLFog {

	// For GL_LINEAR fog:
	protected int FOGDISTCONST = 48;
	protected final float FULLVIS_BEGIN = (float) 2.9e30;
	protected final float FULLVIS_END = (float) 3.0e30;

	public int shade, pal;
	public float combvis;

	public boolean nofog, isEnabled;
	protected TextureManager manager;

	protected final float[] color = new float[4];
	protected float start, end;
	protected float curstart;
	protected float curend;
	protected float[] curcolor = new float[4];

	public void init(TextureManager manager) {
		if (BuildGdx.graphics.getGLVersion().getVendorString().compareTo("NVIDIA Corporation") == 0) {
			BuildGdx.gl.glHint(GL_FOG_HINT, GL_NICEST);
		} else {
			BuildGdx.gl.glHint(GL_FOG_HINT, GL_DONT_CARE);
		}
		BuildGdx.gl.glFogi(GL_FOG_MODE, GL_LINEAR); // GL_EXP

		this.manager = manager;
	}

	public void copy(GLFog src) {
		this.shade = src.shade;
		this.combvis = src.combvis;
		this.pal = src.pal;
	}

	public void clear() {
		shade = 0;
		combvis = 0;
		pal = 0;
	}

	public void calc() {
		if (combvis == 0) {
			start = FULLVIS_BEGIN;
			end = FULLVIS_END;
		} else if (shade >= numshades - 1) {
			start = -1;
			end = 0.001f;
		} else {
			start = (shade > 0) ? 0 : -(FOGDISTCONST * shade) / combvis;
			end = (FOGDISTCONST * (numshades - 1 - shade)) / combvis;
		}

		color[0] = (palookupfog[pal][0] / 63.f);
		color[1] = (palookupfog[pal][1] / 63.f);
		color[2] = (palookupfog[pal][2] / 63.f);
		color[3] = 1;

//		if (manager.getShader() != null)
//			manager.getShader().setFogParams(true, start, end, color);
		BuildGdx.gl.glFogfv(GL_FOG_COLOR, color, 0);
		BuildGdx.gl.glFogf(GL_FOG_START, start);
		BuildGdx.gl.glFogf(GL_FOG_END, end);
	}

	public void setFogScale(int var) {
		FOGDISTCONST = var;
	}

	public void apply() {

	}

	public void enable() {
		if (!nofog) {
			isEnabled = true;
			BuildGdx.gl.glEnable(GL_FOG);
		}
	}

	public void disable() {
		isEnabled = false;
		BuildGdx.gl.glDisable(GL_FOG);
//		if (manager.getShader() != null)
//			manager.getShader().setFogParams(false, 0.0f, 0.0f, null);
	}
}
