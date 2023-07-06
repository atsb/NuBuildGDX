package ru.m210projects.Build.Render.GdxRender;

import static ru.m210projects.Build.Gameutils.AngleToRadians;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.radiansToBuildAngle;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Plane.PlaneSide;
import com.badlogic.gdx.math.Vector3;

public class BuildCamera extends PerspectiveCamera {

	public final Frustum frustum;
	public final float xscale, yscale;
	private final Vector3 tmp = new Vector3();

	private final Vector3 projectorX = new Vector3();
	private final Vector3 projectorY = new Vector3();
	private float halfwidth, halfheight, divhalfw, divhalfh;

	public BuildCamera(float fov, int width, int height, final float xscale, final float yscale) {
		this.setFieldOfView(fov);
		this.viewportWidth = width;
		this.viewportHeight = height;

		this.frustum = new Frustum() {
			@Override
			public void update(Matrix4 inverseProjectionView) {
				System.arraycopy(clipSpacePlanePointsArray, 0, planePointsArray, 0, clipSpacePlanePointsArray.length);
				Matrix4.prj(inverseProjectionView.val, planePointsArray, 0, 8, 3);
				for (int i = 0, j = 0; i < 8; i++) {
					Vector3 v = planePoints[i];
					v.x = planePointsArray[j++] * xscale;
					v.y = planePointsArray[j++] * xscale;
					v.z = planePointsArray[j++] * yscale;
				}

                set(planes[0], planePoints[1], planePoints[0], planePoints[2]);
				set(planes[1], planePoints[4], planePoints[5], planePoints[7]);
				set(planes[2], planePoints[0], planePoints[4], planePoints[3]);
				set(planes[3], planePoints[5], planePoints[1], planePoints[6]);
				set(planes[4], planePoints[2], planePoints[3], planePoints[6]);
				set(planes[5], planePoints[4], planePoints[0], planePoints[1]);
			}

			private void set(Plane p, Vector3 pos, Vector3 p1, Vector3 p2) {
				p.normal.set(pos).sub(p1).crs(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
				p.d = -p.normal.dot(pos);
			}
		};

		this.xscale = xscale;
		this.yscale = yscale;

		this.rotate(-90, 1, 0, 0);
		this.up.set(0, 0, -1);

		this.near = 0.01f;
		this.far = 400;

		this.update();
	}

	public void setPosition(int x, int y, int z) {
		this.position.set(x / xscale, y / xscale, z / yscale);
	}

	public float getX() {
		return position.x * xscale;
	}

	public float getY() {
		return position.y * xscale;
	}

	public float getZ() {
		return position.z * yscale;
	}

	public float getAngle() {
		float angle = MathUtils.atan2(direction.y, direction.x);
		if (angle < 0)
			angle += MathUtils.PI2;
		return BClampAngle(angle * radiansToBuildAngle);
	}

	public void setDirection(double ang, double horiz, double tilt) {
		float yaw = (float) AngleToRadians(ang);
		float gcosang = MathUtils.cos(yaw);
		float gsinang = MathUtils.sin(yaw);

		float pitch = -MathUtils.atan2((float) horiz, 128.0f);
		float gchang = MathUtils.cos(pitch);
		float gshang = MathUtils.sin(pitch);

		direction.set(gcosang * gchang, gsinang * gchang, gshang);
		direction.nor();

		up.set(0, 0, -1);
		up.rotate(direction, (float) -tilt);
	}

	public void setDirection(Vector3 dir, double tilt) {
		direction.set(dir);
		direction.nor();

		up.set(0, 0, -1);
		up.rotate(direction, (float) -tilt);
	}

	public void setDirection(float dirx, float diry, float dirz, double tilt) {
		direction.set(dirx, diry, dirz);
		direction.nor();

		up.set(0, 0, -1);
		up.rotate(direction, (float) -tilt);
	}

	public boolean polyInCamera(ArrayList<? extends Vector3> list) {
		if (list == null)
			return false;

		Plane[] planes = this.frustum.planes;
		Plane: for (int i = 2; i < planes.length; i++) {
			Plane plane = planes[i];
			for (int p = 0; p < list.size(); p++)
				if (plane.testPoint(list.get(p)) != PlaneSide.Back)
					continue Plane;

			return false;
		}
		return true;
	}

	public void setFieldOfView(float fov) {
		float aspect = 4.0f / 3.0f;
		this.fieldOfView = (float) Math.toDegrees((2 * Math.atan(Math.tan(Math.toRadians(fov / 2)) / aspect)));
	}

	@Override
	public void update(boolean updateFrustum) {
		float aspect = viewportWidth / viewportHeight;
		projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
		view.setToLookAt(position, tmp.set(position).add(direction), up);
		combined.set(projection);
		Matrix4.mul(combined.val, view.val);

		if (updateFrustum) {
			invProjectionView.set(combined);
			Matrix4.inv(invProjectionView.val);
			frustum.update(invProjectionView);

			float[] mat = combined.val;
			projectorX.set(mat[Matrix4.M00], mat[Matrix4.M01], mat[Matrix4.M02]);
			projectorY.set(-mat[Matrix4.M10], -mat[Matrix4.M11], -mat[Matrix4.M12]);

			halfwidth = viewportWidth / 2;
			halfheight = viewportHeight / 2;

			divhalfw = 1.0f / halfwidth;
			divhalfh = 1.0f / halfheight;
		}
	}

	@Override
	public Vector3 project(Vector3 worldCoords) {
		worldCoords.scl(1.0f / xscale, 1.0f / xscale, 1.0f / yscale);

		float dx = (worldCoords.x - position.x);
		float dy = (worldCoords.y - position.y);
		float dz = (worldCoords.z - position.z);

		float l_w = 1.0f / direction.dot(dx, dy, dz);

		worldCoords.x = projectorX.dot(dx, dy, dz) * l_w;
		worldCoords.y = projectorY.dot(dx, dy, dz) * l_w;

		worldCoords.x = halfwidth * (worldCoords.x + 1);
		worldCoords.y = halfheight * (worldCoords.y + 1);

		return worldCoords;
	}

	@Override
	public Vector3 unproject(Vector3 screenCoords) {
		float x = screenCoords.x, y = viewportHeight - screenCoords.y;
		screenCoords.x = x * divhalfw - 1;
		screenCoords.y = y * divhalfh - 1;
		screenCoords.z = 1;
		screenCoords.prj(invProjectionView);
		screenCoords.scl(xscale, xscale, yscale);
		return screenCoords;
	}
}
