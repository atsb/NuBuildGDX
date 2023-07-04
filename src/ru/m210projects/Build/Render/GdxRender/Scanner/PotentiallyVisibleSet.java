package ru.m210projects.Build.Render.GdxRender.Scanner;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.pow2char;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.wall;

import java.util.Arrays;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Build.Render.GdxRender.BuildCamera;
import ru.m210projects.Build.Render.GdxRender.Pool;
import ru.m210projects.Build.Render.GdxRender.WorldMesh;
import ru.m210projects.Build.Render.GdxRender.WorldMesh.Heinum;

public class PotentiallyVisibleSet {

	private final WallFrustum2d[] portqueue;
	private final int queuemask; // pay attention!
	private int pqhead, pqtail;
	private int[] sectorqueue;
	private int secindex = 0;

	private final byte[] handled;
	private final WallFrustum2d[] gotviewport;
	private final byte[] gotwall;

	private final RayCaster ray = new RayCaster();
	protected SectorInfo info = new SectorInfo();

	private final Pool<WallFrustum2d> pWallFrustumPool = new Pool<WallFrustum2d>() {
		@Override
		protected WallFrustum2d newObject() {
			return new WallFrustum2d();
		}
	};

	public PotentiallyVisibleSet() {
		portqueue = new WallFrustum2d[512];
		queuemask = portqueue.length - 1;
		gotviewport = new WallFrustum2d[MAXSECTORS];
		sectorqueue = new int[MAXSECTORS];
		handled = new byte[MAXSECTORS >> 3];
		gotwall = new byte[MAXWALLS >> 3];
		sectorqueue = new int[MAXSECTORS];
	}

	public void process(BuildCamera cam, WorldMesh mesh, int sectnum) {
		if (!Gameutils.isValidSector(sectnum))
			return;

		Arrays.fill(gotviewport, null);
		Gameutils.fill(gotwall, (byte) 0);
		Gameutils.fill(handled, (byte) 0);
		pWallFrustumPool.reset();

		secindex = 0;
		pqhead = pqtail = 0;

		portqueue[(pqtail++) & queuemask] = pWallFrustumPool.obtain().set(sectnum);
		WallFrustum2d pFrustum = portqueue[pqhead];
		gotviewport[sectnum] = pFrustum;

		while (pqhead != pqtail) {
			sectnum = pFrustum.sectnum;

			if (!pFrustum.handled) {
				pFrustum.handled = true;

				if (info.hasOccluders(sectnum)) {
					ray.init(false);

					int startwall = sector[sectnum].wallptr;
					int endwall = sector[sectnum].wallnum + startwall;
					for (int z = startwall; z < endwall; z++) {
						if (!WallFacingCheck(wall[z]))
							continue;
						ray.add(z, null);
					}
					ray.update();
				}

				int startwall = sector[sectnum].wallptr;
				int endwall = sector[sectnum].wallnum + startwall;
				for (int z = startwall; z < endwall; z++) {
					WALL wal = wall[z];
					int nextsectnum = wal.nextsector;

					if (!WallFacingCheck(wal))
						continue;

					if (!cam.polyInCamera(mesh.getPoints(Heinum.Max, sectnum, z)))
						continue;

					if (pFrustum.wallInFrustum(wal)) {
						if (info.hasOccluders(sectnum) && !ray.check(z))
							continue;

						if (nextsectnum != -1) {
							WallFrustum2d wallFrustum = pWallFrustumPool.obtain().set(wal);
							if (wallFrustum != null && wallFrustum.fieldOfViewClipping(pFrustum)) {
								if (gotviewport[nextsectnum] == null) {
									portqueue[(pqtail++) & queuemask] = wallFrustum;
									gotviewport[nextsectnum] = wallFrustum;
								} else {
									WallFrustum2d nextp = gotviewport[nextsectnum];
									if ((nextp = nextp.fieldOfViewExpand(wallFrustum)) != null) {
										if ((handled[nextsectnum >> 3] & pow2char[nextsectnum & 7]) != 0) {
											portqueue[(pqtail++) & queuemask] = nextp;
										}
									}
								}
							}
						}
						gotwall[z >> 3] |= pow2char[z & 7];
					}
				}
			}

			if (pFrustum.next != null)
				pFrustum = pFrustum.next;
			else
				pFrustum = portqueue[(++pqhead) & queuemask];

			if ((handled[sectnum >> 3] & pow2char[sectnum & 7]) == 0)
				sectorqueue[secindex++] = sectnum;
			handled[sectnum >> 3] |= pow2char[sectnum & 7];
		}

		ray.init(true);
//		for (int i = secindex - 1; i >= 0; i--) {
//		for (int i = 0; i < secindex; i++) {
//			sectnum = sectorqueue[i];
//			if ((pFrustum = gotviewport[sectnum]) != null) {
//				if(!World.info.isCorruptSector(sectnum)) {
//					int startwall = sector[sectnum].wallptr;
//					int endwall = sector[sectnum].wallnum + startwall;
//					for (int z = startwall; z < endwall; z++) {
//						if ((gotwall[z >> 3] & pow2char[z & 7]) != 0) {
//							ray.add(z, pFrustum);
//						}
//					}
//				}
//			}
//		}

		for (sectnum = 0; sectnum < MAXSECTORS; sectnum++) {
			if ((pFrustum = gotviewport[sectnum]) != null) {
				if (!info.isCorruptSector(sectnum)) {
					int startwall = sector[sectnum].wallptr;
					int endwall = sector[sectnum].wallnum + startwall;
					for (int z = startwall; z < endwall; z++) {
						if ((gotwall[z >> 3] & pow2char[z & 7]) != 0) {
							WALL w = wall[z];
//							if(w.nextsector != -1) { //XXX E2L8 near wall bug fix
							WALL p2 = wall[w.point2];
							int dx = p2.x - w.x;
							int dy = p2.y - w.y;
							float i = dx * (globalposx - w.x) + dy * (globalposy - w.y);
							if (i >= 0.0f) {
								float j = dx * dx + dy * dy;
								if (i < j) {
									i /= j;
									int px = (int) (dx * i + w.x);
									int py = (int) (dy * i + w.y);

									dx = Math.abs(px - globalposx);
									dy = Math.abs(py - globalposy);

									// closest to camera portals should be rendered
									if (dx + dy < 128)
										continue;
								}
							}
//							}

							ray.add(z, pFrustum);
						}
					}
				}
			}
		}

		ray.update();
	}

	public boolean checkWall(int z) {
		if ((gotwall[z >> 3] & pow2char[z & 7]) != 0)
			return ray.check(z);
		return false; // (gotwall[z >> 3] & pow2char[z & 7]) != 0;

//		return (gotwall[z >> 3] & pow2char[z & 7]) != 0;
	}

	public boolean checkSector(int z) {
		return gotviewport[z] != null;
	}

	private boolean WallFacingCheck(WALL wal) {
		float x1 = wal.x - globalposx;
		float y1 = wal.y - globalposy;
		float x2 = wall[wal.point2].x - globalposx;
		float y2 = wall[wal.point2].y - globalposy;

		return (x1 * y2 - y1 * x2) >= 0;
	}
}
