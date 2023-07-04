package ru.m210projects.Wang.Type;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;

public class GameInfo {

	public String Title;
	public DirectoryEntry resDir;
	public EpisodeInfo[] episode;
	public String skills[] = { "Tiny grasshopper", "I Have No Fear", "Who Wants Wang", "No Pain, No Gain", };
	public FileEntry file;
	private boolean pack;

	public GameInfo(String title, DirectoryEntry dir, EpisodeInfo... eps) {
		this.Title = title;
		this.resDir = dir;
		this.episode = eps;
	}

	public GameInfo(String title, Group res, EpisodeInfo... eps) {
		this.Title = title;
		this.file = res != null ? BuildGdx.compat.checkFile(res.name) : null;
		this.resDir = this.file != null ? file.getParent() : null;
		this.pack = true;
		this.episode = eps;
	}

	public String getPath() {
		if (isPackage())
			return file.getPath();
		return resDir.getRelativePath();
	}

	public long getChecksum() {
		if (isPackage())
			return file.getChecksum();

		long sum = 0;
		for (EpisodeInfo i : episode) {
			if (i == null)
				continue;
			for (LevelInfo l : i.gMapInfo) {
				if (l != null)
					sum ^= resDir.checkFile(l.LevelName).getChecksum();
			}
		}
		return sum;
	}

	public FileEntry getFile() {
		return file;
	}

	public boolean isPackage() {
		return pack;
	}

	public GameInfo setPackage(boolean pack) {
		this.pack = pack;
		return this;
	}

	public void setDirectory(DirectoryEntry resDir) {
		this.resDir = resDir;
	}

	public DirectoryEntry getDirectory() {
		return resDir;
	}

	public LevelInfo getLevel(int num) {
		if (num < 1)
			return null;

		if (num < 5)
			return episode[0] != null ? episode[0].gMapInfo[num - 1] : null;
		return episode[1] != null ? episode[1].gMapInfo[num - 5] : null;
	}

	public int getNumEpisode(int level) {
		if (level < 5)
			return 0;
		return 1;
	}

	public int getNumLevel(int level) {
		if (level < 5)
			return level - 1;

		return level - 5;
	}

	public String getMapTitle(int num) {
		LevelInfo info = getLevel(num);
		if (info != null)
			return info.Description;
		return null;
	}

	public String getMapPath(int num) {
		LevelInfo info = getLevel(num);
		if (info != null)
			return info.LevelName;
		return null;
	}
}
