package ru.m210projects.Build.Render.ModelHandle.Voxel;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;

public class VoxelData {

	public static final int MAXVOXMIPS = 5;

	public int[] xsiz, ysiz, zsiz;
	public int[] xpiv, ypiv, zpiv;
	public short[][][] xyoffs;
	public int[][] slabxoffs;
	public byte[][] data;
	public int[] pal;

	public VoxelData(Resource dat) throws Exception {
		int mip = 0;

		xsiz = new int[MAXVOXMIPS];
		ysiz = new int[MAXVOXMIPS];
		zsiz = new int[MAXVOXMIPS];

		xpiv = new int[MAXVOXMIPS];
		ypiv = new int[MAXVOXMIPS];
		zpiv = new int[MAXVOXMIPS];

		xyoffs = new short[MAXVOXMIPS][][];
		slabxoffs = new int[MAXVOXMIPS][];
		data = new byte[MAXVOXMIPS][];

		while (dat.position() < dat.size() - 768) {
			int mip1leng = dat.readInt();
			int xs = xsiz[mip] = dat.readInt();
			int ys = ysiz[mip] = dat.readInt();
			zsiz[mip] = dat.readInt();

			xpiv[mip] = dat.readInt();
			ypiv[mip] = dat.readInt();
			zpiv[mip] = dat.readInt();

			int offset = ((xs + 1) << 2) + (xs * (ys + 1) << 1);
			slabxoffs[mip] = new int[xs + 1];
			for (int i = 0; i <= xs; i++)
				slabxoffs[mip][i] = dat.readInt() - offset;

			xyoffs[mip] = new short[xs][ys + 1];
			for (int i = 0; i < xs; ++i)
				for (int j = 0; j <= ys; ++j)
					xyoffs[mip][i][j] = dat.readShort();

			int i = dat.size() - dat.position() - 768;
			if (i < mip1leng - (24 + offset))
				break;

			data[mip] = new byte[mip1leng - (24 + offset)];
			dat.read(data[mip]);

			mip++;
		}

		if (mip == 0)
			throw new Exception("Can't load voxel");

		this.pal = new int[256];
		dat.seek(dat.size() - 768, Whence.Set);

		byte[] buf = new byte[768];
		dat.read(buf);

		for (int i = 0; i < 256; i++)
			pal[i] = ((buf[3 * i + 0]) << 18) + ((buf[3 * i + 1]) << 10) + ((buf[3 * i + 2]) << 2) + (i << 24);
	}
}
