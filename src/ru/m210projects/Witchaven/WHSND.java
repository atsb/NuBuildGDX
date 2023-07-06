package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Audio.HMIMIDIP.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.WH2MUS.*;
import static ru.m210projects.Witchaven.Weapons.*;
import static ru.m210projects.Witchaven.Globals.*;
import static java.lang.Math.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Audio.SourceCallback;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Witchaven.Types.Ambsounds;
import ru.m210projects.Witchaven.Types.Songtype;
import ru.m210projects.Witchaven.Types.Soundtype;

public class WHSND {

	private static SourceCallback<Integer> callback = new SourceCallback<Integer>() {
		@Override
		public void run(Integer ch) {
			stopsound(ch);
		}
	};

	public static int S_THUNDER1 = 0;
	public static int S_THUNDER2 = 1;
	public static int S_THUNDER3 = 2;
	public static int S_THUNDER4 = 3;
	public static int S_WINDLOOP1 = 4;
	public static int S_WAVELOOP1 = 5;
	public static int S_LAVALOOP1 = 6;
	public static int S_FIRELOOP1 = 7;
	public static int S_STONELOOP1 = 8;
	public static int S_BATSLOOP = 9;
	public static int S_PLRWEAPON0 = 10;
	public static int S_PLRWEAPON1 = 11;
	public static int S_PLRWEAPON2 = 12;
	public static int S_PLRWEAPON3 = 13;
	public static int S_PLRWEAPON4 = 14;
	public static int S_GOBLIN1 = 15;
	public static int S_GOBLIN2 = 16;
	public static int S_GOBLIN3 = 17;
	public static int S_GOBPAIN1 = 18;
	public static int S_GOBPAIN2 = 19;
	public static int S_GOBDIE1 = 20;
	public static int S_GOBDIE2 = 21;
	public static int S_GOBDIE3 = 22;
	public static int S_KSNARL1 = 23;
	public static int S_KSNARL2 = 24;
	public static int S_KSNARL3 = 25;
	public static int S_KSNARL4 = 26;
	public static int S_KPAIN1 = 27;
	public static int S_KPAIN2 = 28;
	public static int S_KDIE1 = 29;
	public static int S_KDIE2 = 30;
	public static int S_DEMON1 = 31;
	public static int S_DEMON2 = 32;
	public static int S_DEMON3 = 33;
	public static int S_DEMON4 = 34;
	public static int S_DEMON5 = 35;
	public static int S_DEMONDIE1 = 36;
	public static int S_DEMONDIE2 = 37;
	public static int S_MSNARL1 = 38;
	public static int S_MSNARL2 = 39;
	public static int S_MSNARL3 = 40;
	public static int S_MSNARL4 = 41;
	public static int S_MPAIN1 = 42;
	public static int S_MDEATH1 = 43;
	public static int S_DRAGON1 = 44;
	public static int S_DRAGON2 = 45;
	public static int S_DRAGON3 = 46;
	public static int S_RIP1 = 47;
	public static int S_RIP2 = 48;
	public static int S_RIP3 = 49;
	public static int S_SKELHIT1 = 50;
	public static int S_SKELHIT2 = 51;
	public static int S_SKELETONDIE = 52;
	public static int S_GUARDIAN1 = 53;
	public static int S_GUARDIAN2 = 54;
	public static int S_GUARDIANPAIN1 = 55;
	public static int S_GUARDIANPAIN2 = 56;
	public static int S_GUARDIANDIE = 57;
	public static int S_WISP = 58;
	public static int S_WISP2 = 59;
	public static int S_SPLASH1 = 60;
	public static int S_SPLASH2 = 61;
	public static int S_SPLASH3 = 62;
	public static int S_SPLASH4 = 63;
	public static int S_SPLASH5 = 64;
	public static int S_SPLASH6 = 65;
	public static int S_WILLOWDIE = 66;
	public static int S_FATWITCHDIE = 67;
	public static int S_JUDY1 = 68;
	public static int S_JUDY2 = 69;
	public static int S_JUDY3 = 70;
	public static int S_JUDY4 = 71;
	public static int S_JUDYDIE = 72;
	public static int S_SKULLWITCH1 = 73;
	public static int S_SKULLWITCH2 = 74;
	public static int S_SKULLWITCH3 = 75;
	public static int S_SKULLWITCHDIE = 76;
	public static int S_GRONDEATHA = 77;
	public static int S_GRONDEATHB = 78;
	public static int S_GRONDEATHC = 79;
	public static int S_CHAIN1 = 80;
	public static int S_FLAME1 = 81;
	public static int S_GRONPAINA = 82;
	public static int S_GRONPAINB = 83;
	public static int S_GRONPAINC = 84;
	public static int S_CLUNK = 85;
	public static int S_DEMONTHROW = 86;
	public static int S_WITCHTHROW = 87;
	public static int S_DOOR1 = 88;
	public static int S_DOOR2 = 89;
	public static int S_DOOR3 = 90;
	public static int S_CREAKDOOR1 = 91;
	public static int S_CREAKDOOR2 = 92;
	public static int S_CREAKDOOR3 = 93;
	public static int S_STING1 = 94;
	public static int S_STING2 = 95;
	public static int S_POTION1 = 96;
	public static int S_GENTHROW = 97;
	public static int S_GENSWING = 98;
	public static int S_ARROWHIT = 99;
	public static int S_WALLHIT1 = 100;
	public static int S_GONG = 101;
	public static int S_SPELL1 = 102;
	public static int S_FREEZE = 103;
	public static int S_FREEZEDIE = 104;
	public static int S_TRAP1 = 105;
	public static int S_TRAP2 = 106;
	public static int S_RATS1 = 107;
	public static int S_RATS2 = 108;
	public static int S_WINDLOOP2 = 109;
	public static int S_BREATH1 = 110;
	public static int S_BREATH2 = 111;
	public static int S_PUSH1 = 112;
	public static int S_PUSH2 = 113;
	public static int S_PLRPAIN1 = 114;
	public static int S_PLRPAIN2 = 115;
	public static int S_GORE1 = 116;
	public static int S_GORE2 = 117;
	public static int S_GORE1A = 118;
	public static int S_GORE1B = 119;
	public static int S_DEADSTEP = 120;
	public static int S_HEARTBEAT = 121;
	public static int S_SOFTCHAINWALK = 122;
	public static int S_SOFTCREAKWALK = 123;
	public static int S_LOUDCHAINWALK = 124;
	public static int S_GRATEWALK = 125;
	public static int S_SCARYDUDE = 126;
	public static int S_WATERY = 127;
	public static int S_GLASSBREAK1 = 128;
	public static int S_GLASSBREAK2 = 129;
	public static int S_GLASSBREAK3 = 130;
	public static int S_TREASURE1 = 131;
	public static int S_SWORD1 = 132;
	public static int S_SWORD2 = 133;
	public static int S_SWORDCLINK1 = 134;
	public static int S_SWORDCLINK2 = 135;
	public static int S_SWORDCLINK3 = 136;
	public static int S_SWORDCLINK4 = 137;
	public static int S_SOCK1 = 138;
	public static int S_SOCK2 = 139;
	public static int S_SOCK3 = 140;
	public static int S_SOCK4 = 141;
	public static int S_KOBOLDHIT = 142;
	public static int S_SPIDERBITE = 143;
	public static int S_FIREBALL = 144;
	public static int S_WOOD1 = 145;
	public static int S_CHAINDOOR1 = 146;
	public static int S_PULLCHAIN1 = 147;
	public static int S_PICKUPAXE = 148;
	public static int S_EXPLODE = 149;
	public static int S_SKELSEE = 150;
	public static int S_BARRELBREAK = 151;
	public static int S_WARP = 152;
	public static final int S_SPELL2 = 153;
	public static final int S_THROWPIKE = 154;
	public static int S_PICKUPFLAG = 155;
	public static int S_DROPFLAG = 156;
	public static int S_LAUGH = 157;
	public static int S_DRINK = 158;
	public static int S_PAGE = 159;
	public static int S_BOTTLES = 160;
	public static int S_CRACKING = 161;
	public static int S_PLRDIE1 = 162;
	public static int S_FATLAUGH = 163;

	// WH2 Sounds
	public static final int S_AGM_ATTACK = 129;
	public static final int S_AGM_PAIN1 = 130;
	public static final int S_AGM_PAIN2 = 131;
	public static final int S_AGM_PAIN3 = 132;
	public static final int S_AGM_DIE1 = 133;
	public static final int S_AGM_DIE2 = 134;
	public static final int S_AGM_DIE3 = 135;
	public static final int S_FIRESWING = 145;
	public static final int S_FIREWEAPONLOOP = 146;
	public static final int S_ENERGYWEAPONLOOP = 147;
	public static final int S_ENERGYSWING = 148;
	public static final int S_BIGGLASSBREAK1 = 149;
	public static final int S_IMPGROWL1 = 152;
	public static final int S_IMPGROWL2 = 153;
	public static final int S_IMPGROWL3 = 154;
	public static final int S_IMPDIE1 = 155;
	public static final int S_IMPDIE2 = 156;
	public static final int S_SWINGDOOR = 157;
	public static final int S_NUKESPELL = 158;
	public static final int S_DOORSPELL = 159;
	public static final int S_FIRESPELL = 160;
	public static final int S_GENERALMAGIC1 = 161;
	public static final int S_GENERALMAGIC2 = 162;
	public static final int S_GENERALMAGIC3 = 163;
	public static final int S_GENERALMAGIC4 = 164;

	public static final int MAX_ACTIVE_SAMPLES = 256;

	public static final int MAX_AMB_SOUNDS = 8;
	public static final int MENUSONG = 0;
	public static final int BASE_SONG = 0;
	public static int lavasnd = -1, batsnd = -1, cartsnd = -1;

	public static final int WINDSNDBIT = 1;
	public static final int WAVESNDBIT = 2;
	public static final int WATERSNDBIT = 4;
	public static final int LAVASNDBIT = 8;
	public static final int GRATESNDBIT = 16;
	public static final int CARTSNDBIT = 32;
	public static final int FIRE1SNDBIT = 64;
	public static final int FIRE2SNDBIT = 128;

	public static final int TOTALSOUNDS = 164;

	public static final short songelements = 3;
	public static final short totallevels = 6;
	public static final short arrangements = 3;

	public static Resource fhsongs = null;
	public static Songtype songptr;
	public static int[] songlist = new int[4096];
	public static int songsperlevel;

	public static Resource fhsounds = null;
	public static ByteBuffer[] pSfx = new ByteBuffer[TOTALSOUNDS + 1];

	public static Ambsounds[] ambsoundarray = { new Ambsounds(0), new Ambsounds(S_WINDLOOP1),
			new Ambsounds(S_WINDLOOP2), new Ambsounds(S_WAVELOOP1), new Ambsounds(S_LAVALOOP1), new Ambsounds(S_WATERY),
			new Ambsounds(S_STONELOOP1), new Ambsounds(S_BATSLOOP), };

	public static Soundtype[] SampleRay = new Soundtype[MAX_ACTIVE_SAMPLES];
	public static int DigiList[] = new int[1024];

	public static void sndInit() throws Exception {
		// Init music
		if (whcfg.maxvoices < 4)
			whcfg.maxvoices = 4;

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, whcfg.maxvoices, whcfg.resampler_num)) {
			BuildGdx.audio.setVolume(Driver.Sound, whcfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
		}

		if (!BuildGdx.audio.getMusic().init())
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);

		if (fhsongs == null)
			setupmidi();
		if (fhsounds == null)
			setupdigi();

		if (game.WH2) {
			S_WINDLOOP1 = 3;
			S_WAVELOOP1 = 4;
			S_LAVALOOP1 = 5;
			S_FIRELOOP1 = 6;
			S_STONELOOP1 = 7;
			S_BATSLOOP = 8;
			S_PLRWEAPON0 = 9;
			S_PLRWEAPON1 = 10;
			S_PLRWEAPON2 = 11;
			S_PLRWEAPON3 = 12;
			S_PLRWEAPON4 = 13;
			S_KSNARL1 = 14;
			S_KSNARL2 = 15;
			S_KSNARL3 = 16;
			S_KSNARL4 = 17;
			S_KPAIN1 = 18;
			S_KPAIN2 = 19;
			S_KDIE1 = 20;
			S_KDIE2 = 21;
			S_DEMON1 = 22;
			S_DEMON2 = 23;
			S_DEMON3 = 24;
			S_DEMON4 = 25;
			S_DEMON5 = 26;
			S_DEMONDIE1 = 27;
			S_DEMONDIE2 = 28;
			S_MSNARL1 = 29;
			S_MSNARL2 = 30;
			S_MSNARL3 = 31;
			S_MSNARL4 = 32;
			S_MPAIN1 = 33;
			S_MDEATH1 = 34;
			S_RIP1 = 35;
			S_RIP2 = 36;
			S_RIP3 = 37;
			S_SKELHIT1 = 38;
			S_SKELHIT2 = 39;
			S_SKELETONDIE = 40;
			S_SKELSEE = 41;
			S_GUARDIAN1 = 42;
			S_GUARDIAN2 = 43;
			S_GUARDIANPAIN1 = 44;
			S_GUARDIANPAIN2 = 45;
			S_GUARDIANDIE = 46;
			S_WISP = 47;
			S_WISP2 = 48;
			S_SPLASH1 = 49;
			S_SPLASH2 = 50;
			S_SPLASH3 = 51;
			S_SPLASH4 = 52;
			S_SPLASH5 = 53;
			S_WILLOWDIE = 54;
			S_JUDY1 = 55;
			S_JUDYDIE = 56;
			S_CHAIN1 = 57;
			S_FLAME1 = 58;
			S_CLUNK = 59;
			S_DEMONTHROW = 60;
			S_DOOR1 = 61;
			S_DOOR2 = 62;
			S_DOOR3 = 63;
			S_CREAKDOOR1 = 64;
			S_CREAKDOOR2 = 65;
			S_CREAKDOOR3 = 66;
			S_STING1 = 67;
			S_STING2 = 68;
			S_POTION1 = 69;
			S_GENTHROW = 70;
			S_GENSWING = 71;
			S_ARROWHIT = 72;
			S_WALLHIT1 = 73;
			S_GONG = 74;
			S_SPELL1 = 75;
			S_FREEZE = 76;
			S_FREEZEDIE = 77;
			S_TRAP1 = 78;
			S_TRAP2 = 79;
			S_RATS1 = 80;
			S_RATS2 = 81;
			S_WINDLOOP2 = 82;
			S_BREATH1 = 83;
			S_BREATH2 = 84;
			S_PUSH1 = 85;
			S_PUSH2 = 86;
			S_PLRPAIN1 = 87;
			S_PLRPAIN2 = 88;
			S_GORE1 = 89;
			S_GORE2 = 90;
			S_GORE1A = 91;
			S_GORE1B = 92;
			S_DEADSTEP = 93;
			S_HEARTBEAT = 94;
			S_SOFTCHAINWALK = 95;
			S_SOFTCREAKWALK = 96;
			S_LOUDCHAINWALK = 97;
			S_SCARYDUDE = 98;
			S_WATERY = 99;
			S_GLASSBREAK1 = 100;
			S_GLASSBREAK2 = 101;
			S_GLASSBREAK3 = 102;
			S_TREASURE1 = 103;
			S_SWORD1 = 104;
			S_SWORD2 = 105;
			S_SWORDCLINK1 = 106;
			S_SWORDCLINK2 = 107;
			S_SWORDCLINK3 = 108;
			S_SWORDCLINK4 = 109;
			S_SOCK1 = 110;
			S_SOCK2 = 111;
			S_SOCK3 = 112;
			S_SOCK4 = 113;
			S_KOBOLDHIT = 114;
			S_FIREBALL = 115;
			S_PULLCHAIN1 = 116;
			S_PICKUPAXE = 117;
			S_EXPLODE = 118;
			S_BARRELBREAK = 119;
			S_WARP = 120;
			S_PICKUPFLAG = 121;
			S_DROPFLAG = 122;
			S_LAUGH = 123;
			S_DRINK = 124;
			S_PAGE = 125;
			S_BOTTLES = 126;
			S_CRACKING = 127;
			S_PLRDIE1 = 128;
			S_GRONDEATHA = 136;
			S_GRONDEATHB = 137;
			S_GRONDEATHC = 138;
			S_GRONPAINA = 142;
		}
	}

	public static boolean midRestart() {
		sndStopMusic();

		if (!BuildGdx.audio.getMusic().init()) {
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
			return false;
		}

		return true;
	}

	public static void sndStopMusic() {
		if (songptr.handle != null)
			songptr.handle.stop();
		songptr.buffer = null;
		currSong = -1;
		currTrack = -1;
	}

	public static void setupmidi() throws Exception {
		String songs = "SONGS";
		if (game.WH2)
			songs = "F_SONGS";

		if ((fhsongs = BuildGdx.cache.open(songs, 0)) == null)
			throw new Exception("setupmidi: cant open songs");

		fhsongs.seek(-4096, Whence.End);
		for (int i = 0; i < 1024; i++)
			songlist[i] = fhsongs.readInt();

		songptr = new Songtype();

		if (game.WH2)
			songsperlevel = 4;
		else
			songsperlevel = songelements * arrangements;
	}

	public static void sndHandlePause(boolean gPaused) {
		if (gPaused) {
			if (songptr.handle != null)
				songptr.handle.pause();
			BuildGdx.audio.getSound().stopAllSounds();
		} else {
			if (!whcfg.muteMusic && songptr.handle != null)
				songptr.handle.resume();
		}
	}

	public static int currTrack = -1;
	public static int currSong = -1;

	public static boolean sndPlayTrack(int nTrack) {
		if (whcfg.musicType == 0 || whcfg.muteMusic)
			return false;

		if (songptr.handle != null && songptr.handle.isPlaying() && currTrack == nTrack)
			return true;

		if (game.currentDef != null) { // music from def file
			String CD = game.currentDef.audInfo.getDigitalInfo(Integer.toString(nTrack));
			if (CD != null && !CD.isEmpty()
					&& (songptr.handle = BuildGdx.audio.newMusic(MusicType.Digital, CD)) != null) {
				sndStopMusic();
				currTrack = nTrack;
				songptr.handle.play(true);
				return true;
			}
		}

		return false;
	}

	public static void SND_MenuMusic() {
		if (game.WH2) {
			startmusic(NUMLEVELS - 1);
			return;
		}

		int which = (totallevels * songsperlevel) + BASE_SONG + 2;
		songptr.length = songlist[(which * 3) + 1];
		songptr.offset = songlist[(which * 3)] * 4096;

		playsong(7);
	}

	public static void startmusic(int level) {
		if (!game.WH2) {
			level %= 6;
			int index = (songsperlevel * level) + (songelements * 2) + BASE_SONG;

			songptr.offset = songlist[index * 3] * 4096;
			songptr.length = songlist[index * 3 + 1];

			playsong(level);
		} else {
			if (level < 0 || level > NUMLEVELS - 1)
				level = 0;

			loadlevelsongs(level);
			startsong(0);
		}
	}

	private static int playsong(int track) {
		if (!whcfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, whcfg.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);

		if (sndPlayTrack(track))
			return 1;

		if (songptr.length == 0)
			return 0;

		if (currSong != -1 && currSong == songptr.offset)
			return 1;

		sndStopMusic();

		if (songptr.buffer == null) {
			fhsongs.seek(songptr.offset, Whence.Set);
			byte[] buffer = new byte[songptr.length];
			int rv = fhsongs.read(buffer);

			songptr.buffer = hmpinit(buffer);

			if (songptr.buffer == null || rv != songptr.length) {
				Console.Println("playsong: bad read");
				return 0;
			}
		}

		currSong = songptr.offset;
		songptr.handle = BuildGdx.audio.newMusic(MusicType.Midi, songptr.buffer);
		if (songptr.handle != null) {
			songptr.handle.play(true);
			return 1;
		}

		return 0;
	}

	public static void setupdigi() throws Exception {
		if ((fhsounds = BuildGdx.cache.open("JOESND", 0)) == null)
			throw new Exception("setupdigi: cant open sounds");

		fhsounds.seek(-4096, Whence.End);
		for (int i = 0; i < 1024; i++) {
			Integer var = fhsounds.readInt();
			if (var == null) {
				game.ThrowError("setupdigi: bad read of digilist");
				return;
			}
			DigiList[i] = var;
		}

		for (int i = 0; i < MAX_ACTIVE_SAMPLES; i++) {
			SampleRay[i] = new Soundtype();
			SampleRay[i].sndnum = -1;
		}
	}

	public static void updatesounds() {
		if (Console.IsShown() || whcfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		BuildGdx.audio.getSound().setListener(player[pyrn].x, player[pyrn].z >> 4, player[pyrn].y,
				(int) player[pyrn].ang);

		for (int i = 0; i < MAX_ACTIVE_SAMPLES; i++) {
			if (SampleRay[i].hVoice == null)
				continue;

			if (SampleRay[i].hVoice.isLooping()) {
				if (SampleRay[i].loop > 0) {
					if (SampleRay[i].loop >= (TICSPERFRAME / 360f))
						SampleRay[i].loop -= (TICSPERFRAME / 360f);
					else
						SampleRay[i].loop = 0;
				}

				if (SampleRay[i].loop == 0) {
					stopsound(i);
					return;
				}
			}

			if (SampleRay[i].x == 0 && SampleRay[i].y == 0)
				continue;

			int sqrdist = abs(player[pyrn].x - SampleRay[i].x) + abs(player[pyrn].y - SampleRay[i].y);
			int wVol = 39000 - (sqrdist << 2);
			if (sqrdist < 1500)
				wVol = 0x7fff;
			else if (sqrdist > 8500)
				wVol = 0x1f00;

			wVol = BClipRange((int) ((wVol / 32767f) * 255), 0, 255);
			SampleRay[i].hVoice.setVolume(wVol / 255.0f);
		}
	}

	public static void SND_CheckLoops() {
		// special case loops
		if (cartsnd != -1) {
			cartsnd = -1;
		}
		if (lavasnd != -1) {
			lavasnd = -1;
		}
		if (batsnd != -1) {
			batsnd = -1;
		}

		if (enchantedsoundhandle != -1) {
			SND_StopLoop(enchantedsoundhandle);
		}

		// ambient sound array
		for (int wIndex = 0; wIndex < MAX_AMB_SOUNDS; wIndex++) {
			if (ambsoundarray[wIndex].hsound != -1) {
				ambsoundarray[wIndex].hsound = -1;
			}
		}

		stopallsounds();
	}

	public static void SND_StopLoop(int which) {
		if (whcfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		stopsound(which);

	}

	public static int SND_Sound(int sn) {
		return playsound(sn, 0, 0, 0);
	}

	public static int playsound_loc(int sn, int x, int y) {
		return playsound(sn, x, y, 0);
	}

	public static int playsound(int sn, int x, int y, int loop) {
		if (whcfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return -1;

		if (sn >= TOTALSOUNDS) {
			Console.Println("wrong sound: " + sn);
			return -1;
		}

		int prioritize = DigiList[(sn * 3) + 2];
		if (sn == S_STONELOOP1) {
			for (int wIndex = 0; wIndex < MAX_ACTIVE_SAMPLES; wIndex++)
				if (sn == SampleRay[wIndex].sndnum)
					return -1;
		}

		int sound = 0;
		SOUND: while (SampleRay[sound].hVoice != null && SampleRay[sound].hVoice.isActive()) {
			sound++;
			if (sound == MAX_ACTIVE_SAMPLES) {
				if (prioritize < 9)
					return -1;

				for (int wIndex = 0; wIndex < MAX_ACTIVE_SAMPLES; wIndex++) {
					if (SampleRay[wIndex].priority < 9 && SampleRay[wIndex].loop != -1) {
						stopsound(wIndex);
						sound = wIndex;
						break SOUND;
					}
				}
				return -1;
			}
		}

		boolean globalVoice = false;
		int wVol = 0x7fff;
		if (((x != 0) && (y != 0)) && ((player[pyrn].x != x) && (player[pyrn].y != y))) {
			int sqrdist = (int) (klabs(player[pyrn].x - x) + klabs(player[pyrn].y - y));
			if (sqrdist < 1500)
				wVol = 0x7fff;
			else if (sqrdist > 8500)
				wVol = 0x1f00;
			else
				wVol = 39000 - (sqrdist << 2);
		} else
			globalVoice = true;

		if (pSfx[sn] == null) { // no longer in cache
			int srclen = DigiList[sn * 3 + 1];
			int srcoffset = DigiList[sn * 3] * 4096;

			fhsounds.seek(srcoffset, Whence.Set);
			ByteBuffer data = BufferUtils.newByteBuffer(srclen);
			data.order(ByteOrder.LITTLE_ENDIAN);

			byte[] buf = new byte[srclen];
			fhsounds.read(buf);
			data.put(buf);
			pSfx[sn] = data;
		}
		pSfx[sn].rewind();

		wVol = BClipRange((int) ((wVol / 32767f) * 255), 1, 255);

		Source hVoice = null;
		if (loop != 0) {
			if ((hVoice = BuildGdx.audio.newSound(pSfx[sn], 11025, 8, Integer.MAX_VALUE)) != null)
				hVoice.setLooping(true, 0, -1);
		} else
			hVoice = BuildGdx.audio.newSound(pSfx[sn], 11025, 8, 80 * (wVol + 1));

		if (hVoice != null) {
			hVoice.setPosition(x, 0, y);
			if (globalVoice)
				hVoice.setGlobal(1);
			hVoice.play(wVol / 255.0f);
			hVoice.setCallback(callback, sound);

			SampleRay[sound].hVoice = hVoice;
			SampleRay[sound].loop = loop;
			SampleRay[sound].sndnum = sn;
			SampleRay[sound].priority = prioritize;
			SampleRay[sound].x = x;
			SampleRay[sound].y = y;

			return sound;
		}

		return -1;
	}

	public static void stopallsounds() {
		if (!BuildGdx.audio.IsInited(Driver.Sound))
			return;

		for (int i = 0; i < MAX_ACTIVE_SAMPLES; i++)
			stopsound(i);

		BuildGdx.audio.getSound().stopAllSounds();
	}

	public static void stopsound(int i) {
		if ((i < 0) || (i >= TOTALSOUNDS))
			return;

		SampleRay[i].sndnum = -1;
		SampleRay[i].priority = 0;
		SampleRay[i].x = 0;
		SampleRay[i].y = 0;

		if (SampleRay[i].hVoice == null || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		Source hVoice = SampleRay[i].hVoice;
		SampleRay[i].hVoice = null;

		if (SampleRay[i].loop != 0) {
			hVoice.setLooping(false, 0, 0);
			SampleRay[i].loop = 0;
		}

		hVoice.dispose();
	}

	public static boolean sndRestart(int nvoices, int resampler) {
		stopallsounds();

		BuildGdx.audio.getSound().uninit();
		whcfg.maxvoices = nvoices;

		Console.Println("Sound restarting...");

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, nvoices, resampler)) {
			BuildGdx.audio.setVolume(Driver.Sound, whcfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
			return false;
		}

		return true;
	}
}