package ru.m210projects.Wang.Type;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Render.ModelHandle.VoxelInfo;
import ru.m210projects.Build.Render.ModelHandle.Voxel.VoxelData;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Script.Scriptfile;

public class VoxelScript extends Scriptfile {

	private String[] aVoxelArray = new String[MAXTILES];
	public VoxelScript(Resource res) {
		super("", res.getBytes());

		parse();
	}

	private void parse()
	{
		while (!eof()) {
			int lTile = getnumber();
			getnumber(); //lNumber
			String filename = getstring();
			if(filename != null && isValidTile(lTile))
				aVoxelArray[lTile] = filename;
		}
	}

	public void apply(DefScript def)
	{
		for(int i = 0; i < MAXTILES; i++) {
			if(aVoxelArray[i] != null) {
				Resource buffer = BuildGdx.cache.open(aVoxelArray[i], 0);
				if(buffer != null) {
					try {
						VoxelData vox = new VoxelData(buffer);
						def.mdInfo.addVoxelInfo(new VoxelInfo(vox), i);
					} catch (Exception e) {
						e.printStackTrace();
					}
					buffer.close();
				}
			}
		}
	}

	private int getnumber() {
		String txt = getstring();
		if (txt == null)
			return -1;
		txt = txt.replaceAll("[^0-9]", "");

		try {
			return Integer.parseInt(txt);
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	protected void preparse(byte[] data, int flen) {
		// Count number of lines
		int numcr = 1;
		for (int i = 0; i < flen; i++) {
			// detect all 4 types of carriage return (\r, \n, \r\n, \n\r :)
			int cr = 0;
			if (data[i] == '\r') {
				i += ((data[i + 1] == '\n') ? 1 : 0);
				cr = 1;
			} else if (data[i] == '\n') {
				i += ((data[i + 1] == '\r') ? 1 : 0);
				cr = 1;
			}
			if (cr != 0) {
				numcr++;
				continue;
			}
		}

		linenum = numcr;
		lineoffs = new int[numcr];

		// Preprocess file for comments (// and /*...*/, and convert all whitespace to
		// single spaces)
		int nflen = 0, space = 0, cs = 0, inquote = 0;
		numcr = 0;
		for (int i = 0; i < flen; i++) {
			// detect all 4 types of carriage return (\r, \n, \r\n, \n\r :)
			int cr = 0;
			if (data[i] == '\r') {
				i += ((data[i + 1] == '\n') ? 1 : 0);
				cr = 1;
			} else if (data[i] == '\n') {
				i += ((data[i + 1] == '\r') ? 1 : 0);
				cr = 1;
			}
			if (cr != 0) {
				// Remember line numbers by storing the byte index at the start of each line
				// Line numbers can be retrieved by doing a binary search on the byte index :)
				lineoffs[numcr++] = nflen;
				if (cs == 1)
					cs = 0;
				space = 1;
				continue; // strip CR/LF
			}

			if ((inquote == 0) && ((data[i] == ' ') || (data[i] == '\t'))) {
				space = 1;
				continue;
			} // strip Space/Tab
			if ((data[i] == '#') && (cs == 0))
				cs = 1;
			if ((data[i] == '/') && (data[i + 1] == '/') && (cs == 0))
				cs = 1;
			if ((data[i] == '\\') && (data[i + 1] == '\\') && (cs == 0))
				cs = 1;
			if ((data[i] == '/') && (data[i + 1] == '*') && (cs == 0)) {
				space = 1;
				cs = 2;
			}
			if ((data[i] == '*') && (data[i + 1] == '/') && (cs == 2)) {
				cs = 0;
				i++;
				continue;
			}
			if (cs != 0)
				continue;

			if (space != 0) {
				data[nflen++] = 0;
				space = 0;
			}

			// quotes inside strings: \"
			if ((data[i] == '\\') && (data[i + 1] == '\"')) {
				i++;
				data[nflen++] = '\"';
				continue;
			}
			if (data[i] == '\"') {
				inquote ^= 1;
				continue;
			}
			data[nflen++] = data[i];
		}
		data[nflen++] = 0;
		lineoffs[numcr] = nflen;
		data[nflen++] = 0;

		flen = nflen;

		textbuf = new String(data);
		textptr = 0;
		eof = nflen - 1;
	}
}
