package ru.m210projects.Build.Render.GdxRender.Scanner;

import static ru.m210projects.Build.Engine.*;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Render.GdxRender.Pool.Poolable;
import ru.m210projects.Build.Types.WALL;

public class WallFrustum2d implements Poolable {

	public enum FrustumStatus {
		Inside, Outside, Expanded
	}

	private static final Vector2 tmp = new Vector2();

	public final Plane[] planes = new Plane[3];
	public int sectnum;
	public WallFrustum2d next;
	public boolean isFullAngle = false;
	protected float sinA = 0.0f;
	public boolean handled = false;

	public WallFrustum2d() {
		for (int i = 0; i < planes.length; i++) {
			planes[i] = new Plane();
		}
	}

	public WallFrustum2d set(int sectnum) {
		this.sectnum = sectnum;
		return this.setFullAngle();
	}

	public WallFrustum2d set(WALL wal) {
		this.sectnum = wal.nextsector;

		float x1 = wal.x - globalposx;
		float y1 = wal.y - globalposy;
		WALL wal2 = wall[wal.point2];
		float x2 = wal2.x - globalposx;
		float y2 = wal2.y - globalposy;

		setPlane(0, x1, y1, 0); // left
		setPlane(1, -x2, -y2, 0); // right
		calcSinA();
		if (sinA == 0.0f) {
			float cosA = -planes[0].normal.y * planes[1].normal.y - planes[0].normal.x * planes[1].normal.x;
			if (cosA > 0.0f) // fov == 0
				return null;
		}
		return this;
	}

	public WallFrustum2d set(WallFrustum2d frustum) {
		planes[0].set(frustum.planes[0]);
		planes[1].set(frustum.planes[1]);
		planes[2].set(frustum.planes[2]);
		sinA = frustum.sinA;
		return this;
	}

	protected WallFrustum2d setFullAngle() {
		planes[0].normal.set(planes[1].normal);
		planes[0].normal.scl(-1);
		planes[0].d = -planes[1].d;
		isFullAngle = true;
		sinA = -1;

		return this;
	}

	public boolean isGreater180() {
		return sinA < 0;
	}

	protected void calcSinA() {
		this.sinA = planes[0].normal.y * planes[1].normal.x - planes[0].normal.x * planes[1].normal.y;
	}

	protected void setPlane(int num, float nx, float ny, float nz) {
		planes[num].getNormal().set(-ny, nx, nz);

//		Vector3 n = planes[num].getNormal().set(-ny, nx, nz).nor();
//		planes[num].d = -(globalposx * n.x + globalposy * n.y + globalposz * n.z);
	}

	public boolean wallInFrustum(WALL wal) {
		if (isFullAngle)
			return true;

		int x1 = wal.x - globalposx;
		int y1 = wal.y - globalposy;
		WALL wal2 = wall[wal.point2];
		int x2 = wal2.x - globalposx;
		int y2 = wal2.y - globalposy;

		// left / right planes check

		if (isGreater180()) { // greater 180 degrees
			for (int i = 0; i < 2; i++) {
				Plane plane = planes[i];

				if (plane.normal.dot(x1, y1, 0) >= 0)
					return true;

				if (plane.normal.dot(x2, y2, 0) >= 0)
					return true;
			}
			return false;
		}

		if (!NearPlaneCheck(wal))
			return false;

		for (int i = 0; i < 2; i++) {
			Plane plane = planes[i];
			if (plane.normal.dot(x1, y1, 0) >= 0)
				continue;

			if (plane.normal.dot(x2, y2, 0) >= 0)
				continue;

			return false;
		}

		return true;
	}

	private boolean NearPlaneCheck(WALL wal) {
		tmp.set(planes[0].normal.x, planes[0].normal.y);
		tmp.add(planes[1].normal.x, planes[1].normal.y);

		int x1 = wal.x - globalposx;
		int y1 = wal.y - globalposy;
		if (tmp.dot(x1, y1) >= 0)
			return true;

		int x2 = wall[wal.point2].x - globalposx;
		int y2 = wall[wal.point2].y - globalposy;
        return tmp.dot(x2, y2) >= 0;
    }

	public boolean fieldOfViewClipping(WallFrustum2d frustum) {
		if (frustum.isFullAngle)
			return true;

		final float precise = 0.00001f;

		// frustum left testPoint by plane right
		float dot1 = planes[1].normal.x * frustum.planes[0].normal.y - planes[1].normal.y * frustum.planes[0].normal.x;
		// frustum right testPoint by plane left
		float dot2 = planes[0].normal.y * frustum.planes[1].normal.x - planes[0].normal.x * frustum.planes[1].normal.y;
		// frustum left testPoint by plane left
		float dot3 = planes[0].normal.x * frustum.planes[0].normal.y - planes[0].normal.y * frustum.planes[0].normal.x;
		// frustum right testPoint by plane right
		float dot4 = planes[1].normal.y * frustum.planes[1].normal.x - planes[1].normal.x * frustum.planes[1].normal.y;

		boolean isGreater180 = this.isGreater180();
		boolean frustumIsGreater180 = frustum.isGreater180();
		if (!isGreater180) {
			if (frustumInFrustum(dot1, dot2, dot3, dot4, isGreater180)) {
				if (!frustumIsGreater180) {
//					if (sinA <= precise && cosA <= -1.0f + precise) {
					if (sinA + frustum.sinA < precise) {
						float dirx = planes[0].normal.x + planes[1].normal.x;
						float diry = planes[0].normal.y + planes[1].normal.y;
						float frdirx = frustum.planes[0].normal.x + frustum.planes[1].normal.x;
						float frdiry = frustum.planes[0].normal.y + frustum.planes[1].normal.y;

						if ((dirx * frdirx + diry * frdiry) < 0) // 180 vs 180
							return false;
					}

					if (dot1 >= -precise && dot2 >= -precise) {
						set(frustum);
//						clipZ(frustum);
						return true;
					}
				}
			}

			if (frustumInFrustum(dot2, dot1, -dot3, -dot4, frustumIsGreater180)) {
				return true;
			}

			// contact check
			if (dot1 >= -precise && dot3 >= -precise) {
				planes[0].set(frustum.planes[0]);
				calcSinA();
				return true;
			} else if (dot2 >= -precise && dot4 >= -precise) {
				planes[1].set(frustum.planes[1]);
				calcSinA();
				return true;
			}
		} else {
			// frustum.frustumInFrustum(this)
			if (frustumInFrustum(dot2, dot1, -dot3, -dot4, frustumIsGreater180)) {
				if (dot1 >= -precise && dot2 >= -precise && dot3 <= precise && dot4 <= precise) {
					return true;
				}
			}

			// this.frustumInFrustum(frustum)
			if (frustumInFrustum(dot1, dot2, dot3, dot4, isGreater180)) {
				if ((!frustumIsGreater180 && (dot3 >= -precise || dot4 >= -precise))
						|| (frustumIsGreater180 && (dot1 <= precise || dot2 <= precise))) {
					set(frustum);
					return true;
				}
			}

			// contact check
			if (dot1 <= precise && dot3 <= precise && (dot2 >= -precise || dot4 >= -precise)) {
				planes[1].set(frustum.planes[1]);
				calcSinA();
				return true;
			} else if (dot2 <= precise && dot4 <= precise && (dot1 >= -precise || dot3 >= -precise)) {
				planes[0].set(frustum.planes[0]);
				calcSinA();
				return true;
			}
		}

		return false;
	}

	public boolean frustumInFrustum(WallFrustum2d frustum) {
		// frustum left testPoint by plane right
		float dot1 = planes[1].normal.x * frustum.planes[0].normal.y - planes[1].normal.y * frustum.planes[0].normal.x;
		// frustum right testPoint by plane left
		float dot2 = planes[0].normal.y * frustum.planes[1].normal.x - planes[0].normal.x * frustum.planes[1].normal.y;
		// frustum left testPoint by plane left
		float dot3 = planes[0].normal.x * frustum.planes[0].normal.y - planes[0].normal.y * frustum.planes[0].normal.x;
		// frustum right testPoint by plane right
		float dot4 = planes[1].normal.y * frustum.planes[1].normal.x - planes[1].normal.x * frustum.planes[1].normal.y;

		return frustumInFrustum(dot1, dot2, dot3, dot4, isGreater180());
	}

	private boolean frustumInFrustum(float dot1, float dot2, float dot3, float dot4, boolean isGreater180) {
		final float precise = 0.00001f;

		if (isGreater180) {
			boolean point1InFrustum = dot3 >= -precise;
			if (!point1InFrustum)
				point1InFrustum = dot1 >= -precise;

			boolean point2InFrustum = dot2 >= -precise;
			if (!point2InFrustum)
				point2InFrustum = dot4 >= -precise;

			return point1InFrustum && point2InFrustum;
		}

        return dot3 >= -precise && dot2 >= -precise && dot1 >= -precise && dot4 >= -precise;
    }

	public FrustumStatus isExpanded(WallFrustum2d frustum) {
		if (isFullAngle)
			return FrustumStatus.Inside;

		if (frustum.isFullAngle) {
			setFullAngle();
			return FrustumStatus.Expanded;
		}

		final float precise = 0.00001f;

		// frustum left testPoint by plane right
		float dot1 = planes[1].normal.x * frustum.planes[0].normal.y - planes[1].normal.y * frustum.planes[0].normal.x;
		// frustum right testPoint by plane left
		float dot2 = planes[0].normal.y * frustum.planes[1].normal.x - planes[0].normal.x * frustum.planes[1].normal.y;
		// frustum left testPoint by plane left
		float dot3 = planes[0].normal.x * frustum.planes[0].normal.y - planes[0].normal.y * frustum.planes[0].normal.x;
		// frustum right testPoint by plane right
		float dot4 = planes[1].normal.y * frustum.planes[1].normal.x - planes[1].normal.x * frustum.planes[1].normal.y;

		boolean isGreater180 = this.isGreater180();
		boolean frustumIsGreater180 = frustum.isGreater180();
		if (!isGreater180) {
			// this.frustumInFrustum(frustum)
			if (frustumInFrustum(dot1, dot2, dot3, dot4, isGreater180)) {
				if (!frustumIsGreater180 && sinA + frustum.sinA >= precise) {
					return FrustumStatus.Inside;
				}

				float dirx = planes[0].normal.x + planes[1].normal.x;
				float diry = planes[0].normal.y + planes[1].normal.y;
				float frdirx = frustum.planes[0].normal.x + frustum.planes[1].normal.x;
				float frdiry = frustum.planes[0].normal.y + frustum.planes[1].normal.y;
				if ((dirx * frdirx + diry * frdiry) >= 0) // 180 vs 180
					return FrustumStatus.Inside;

				if (dot1 >= -precise && dot2 >= -precise) {
					setFullAngle();
					return FrustumStatus.Expanded;
				}
			}

			// frustum.frustumInFrustum(this)
			if (frustumInFrustum(dot2, dot1, -dot3, -dot4, frustumIsGreater180)) {
				set(frustum);
				return FrustumStatus.Expanded;
			}

			// contact check
			if (dot1 >= -precise && dot3 >= -precise) {
				planes[1].set(frustum.planes[1]);
				calcSinA();
				return FrustumStatus.Expanded;
			} else if (dot2 >= -precise && dot4 >= -precise) {
				planes[0].set(frustum.planes[0]);
				calcSinA();
				return FrustumStatus.Expanded;
			}
		} else {
			// frustum.frustumInFrustum(this)
			if (frustumInFrustum(dot2, dot1, -dot3, -dot4, frustumIsGreater180)) {
				if (frustumIsGreater180 && dot1 <= precise && dot2 <= precise && dot3 <= precise && dot4 <= precise) {
					set(frustum);
					return FrustumStatus.Expanded;
				}
			}

			// this.frustumInFrustum(frustum)
			if (frustumInFrustum(dot1, dot2, dot3, dot4, isGreater180)) {
				if ((!frustumIsGreater180 && (dot3 >= -precise || dot4 >= -precise))
						|| (frustumIsGreater180 && (dot1 <= precise || dot2 <= precise))) {
					return FrustumStatus.Inside;
				}
				setFullAngle();
				return FrustumStatus.Expanded;
			}

			// contact check
			if (dot1 <= precise && dot3 <= precise && (dot2 >= -precise || dot4 >= -precise)) {
				planes[0].set(frustum.planes[0]);
				calcSinA();
				return FrustumStatus.Expanded;
			} else if (dot2 <= precise && dot4 <= precise && (dot1 >= -precise || dot3 >= -precise)) {
				planes[1].set(frustum.planes[1]);
				calcSinA();
				return FrustumStatus.Expanded;
			}
		}

		return FrustumStatus.Outside;
	}

	public WallFrustum2d fieldOfViewExpand(WallFrustum2d frustum) {
		WallFrustum2d n = this, prev;

		do {
			FrustumStatus stat = n.isExpanded(frustum);

			if (stat == FrustumStatus.Expanded) {

				// TODO build near plane
				n.handled = false;
				return n;
			}

			if (stat == FrustumStatus.Inside)
				return null;

			prev = n;
			n = n.next;
		} while (n != null);

		// Outside
		prev.next = frustum;
		return prev.next;
	}

	// Debug operations

	public Vector2 getPlane(int num) {
		if (num == 0)
			tmp.set(planes[num].normal.y, -planes[num].normal.x);
		else
			tmp.set(-planes[num].normal.y, planes[num].normal.x);
		return tmp;
	}

	public Vector2 getNormal(int num) {
		tmp.set(planes[num].normal.x, planes[num].normal.y);
		return tmp;
	}

	public float getHFov() {
		if (this.isFullAngle)
			return 360;

		float lang = getPlane(0).angle();
		float rang = getPlane(1).angle();
		if (lang > rang)
			lang -= 360;

		return rang - lang;
	}

//	public Vector3 getDirection(Vector3 n, int d) {
//		Vector3 dir;
//		if (d == 0)
//			dir = new Vector3(n.x * World.cam.direction.x, -n.z * World.cam.direction.y, n.y);
//		else
//			dir = new Vector3(n.x * World.cam.direction.x, n.z * World.cam.direction.y, -n.y);
//
//		return dir;
//	}

	public Vector2 getDirection(boolean normalized) {
		tmp.set(planes[0].normal.x, planes[0].normal.y);
		tmp.add(planes[1].normal.x, planes[1].normal.y);
		if (normalized)
			tmp.nor();
		return tmp;
	}

	public boolean isSameDirection(WallFrustum2d frustum) {
		float dirx = planes[0].normal.x + planes[1].normal.x;
		float diry = planes[0].normal.y + planes[1].normal.y;

		float frdirx = frustum.planes[0].normal.x + frustum.planes[1].normal.x;
		float frdiry = frustum.planes[0].normal.y + frustum.planes[1].normal.y;

		return (dirx * frdirx + diry * frdiry) >= 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append("fov : ").append(getHFov());
		sb.append(", dir: ").append(this.getDirection(true).angle());
		sb.append(", plane0 ").append(getPlane(0));
		sb.append(", plane1 ").append(getPlane(1));
		sb.append(']');

		return sb.toString();
	}

	@Override
	public void reset() {
		next = null;
		handled = false;
		isFullAngle = false;
		sinA = 0.0f;
	}
}
