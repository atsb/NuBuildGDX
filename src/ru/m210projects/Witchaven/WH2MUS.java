package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Audio.HMIMIDIP.hmpinit;
import static ru.m210projects.Witchaven.Main.whcfg;
import static ru.m210projects.Witchaven.WHSND.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.FileHandle.Resource.Whence;

public class WH2MUS {

	public static int attacktheme = 0;
	public static final int NUMLEVELS = 17;
	private static final int SONGSPERLEVEL = 4;
	private static int oldsong;

	public static boolean loadlevelsongs(int which) {

		oldsong = which;
		return true;
	}

	public static boolean startsong(int which) // 0, 1, 2 or 3
	{
		if (!whcfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, whcfg.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);

		int index = (oldsong * SONGSPERLEVEL);
		songptr.offset = songlist[(index + which) * 3] * 4096;
		songptr.length = songlist[(index + which) * 3 + 1];

		if (sndPlayTrack(index + which))
			return true;
		
		if (songptr.length == 0)
			return false;
		
		if (currSong != -1 && currSong == songptr.offset)
			return true;
		
		sndStopMusic();
		
		// read data from file
		if (songptr.buffer == null) {
			fhsongs.seek(songptr.offset, Whence.Set);
			byte[] buffer = new byte[songptr.length];
			int rv = fhsongs.read(buffer);

			songptr.buffer = hmpinit(buffer);

			if (songptr.buffer == null || rv != songptr.length)
				return false;
		}

		currSong = songptr.offset;
		songptr.handle = BuildGdx.audio.newMusic(MusicType.Midi, songptr.buffer);
		if (songptr.handle != null) {
			songptr.handle.play(attacktheme != 0 ? false : true);
			if (which < 2)
				attacktheme = 0;

			return true;
		}

		return false;
	}
}
