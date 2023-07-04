package ru.m210projects.Build.Render.ModelHandle;

import java.util.Iterator;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import ru.m210projects.Build.Render.ModelHandle.ModelInfo.Type;
import ru.m210projects.Build.Render.TextureHandle.GLTile;

public interface GLModel {

	boolean render(int pal, int shade, int skinnum, int visibility, float alpha);

	ShaderProgram getShader();

	void dispose();

	Iterator<GLTile> getSkins();

	void clearSkins();

	Type getType();

	boolean isRotating();

	boolean isTintAffected();

	float getScale();

}
