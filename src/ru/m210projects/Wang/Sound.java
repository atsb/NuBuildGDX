package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Digi.DIGI_ALLINREFLEXES;
import static ru.m210projects.Wang.Digi.DIGI_BANZAI;
import static ru.m210projects.Wang.Digi.DIGI_COWABUNGA;
import static ru.m210projects.Wang.Digi.DIGI_EATTHIS;
import static ru.m210projects.Wang.Digi.DIGI_EVERYBODYDEAD;
import static ru.m210projects.Wang.Digi.DIGI_FIRECRACKERUPASS;
import static ru.m210projects.Wang.Digi.DIGI_GOTITEM1;
import static ru.m210projects.Wang.Digi.DIGI_HAHA1;
import static ru.m210projects.Wang.Digi.DIGI_HAHA2;
import static ru.m210projects.Wang.Digi.DIGI_HAHA3;
import static ru.m210projects.Wang.Digi.DIGI_HOLYCOW;
import static ru.m210projects.Wang.Digi.DIGI_HOLYPEICESOFCOW;
import static ru.m210projects.Wang.Digi.DIGI_HOLYPEICESOFSHIT;
import static ru.m210projects.Wang.Digi.DIGI_HOLYSHIT;
import static ru.m210projects.Wang.Digi.DIGI_HOWYOULIKEMOVE;
import static ru.m210projects.Wang.Digi.DIGI_HURTBAD1;
import static ru.m210projects.Wang.Digi.DIGI_HURTBAD2;
import static ru.m210projects.Wang.Digi.DIGI_HURTBAD3;
import static ru.m210projects.Wang.Digi.DIGI_HURTBAD4;
import static ru.m210projects.Wang.Digi.DIGI_HURTBAD5;
import static ru.m210projects.Wang.Digi.DIGI_KUNGFU;
import static ru.m210projects.Wang.Digi.DIGI_MOVELIKEYAK;
import static ru.m210projects.Wang.Digi.DIGI_NOCHARADE;
import static ru.m210projects.Wang.Digi.DIGI_NOMESSWITHWANG;
import static ru.m210projects.Wang.Digi.DIGI_NOTOURNAMENT;
import static ru.m210projects.Wang.Digi.DIGI_PAYINGATTENTION;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERPAIN1;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERPAIN2;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERPAIN3;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERPAIN4;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERPAIN5;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL1;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL2;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL3;
import static ru.m210projects.Wang.Digi.DIGI_RAWREVENGE;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI1;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI10;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI2;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI3;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI4;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI5;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI6;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI7;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI8;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI9;
import static ru.m210projects.Wang.Digi.DIGI_TIMETODIE;
import static ru.m210projects.Wang.Digi.DIGI_TINYDICK;
import static ru.m210projects.Wang.Digi.DIGI_WHIPME;
import static ru.m210projects.Wang.Digi.DIGI_WHOWANTSWANG;
import static ru.m210projects.Wang.Digi.DIGI_YOULOOKSTUPID;
import static ru.m210projects.Wang.Digi.DIST_NORMAL;
import static ru.m210projects.Wang.Digi.PRI_PLAYERDEATH;
import static ru.m210projects.Wang.Digi.PRI_PLAYERVOICE;
import static ru.m210projects.Wang.Digi.ambarray;
import static ru.m210projects.Wang.Digi.voc;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Gameutils.MAX_SW_PLAYERS;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.gMenuScreen;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Names.STAT_AMBIENT;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.RTS.RTS_GetSound;
import static ru.m210projects.Wang.Type.RTS.RTS_NumSounds;
import static ru.m210projects.Wang.Type.RTS.RTS_Started;
import static ru.m210projects.Wang.Type.RTS.rtsplaying;
import static ru.m210projects.Wang.Type.VOC_INFO.MAXSOURCEINTICK;

import java.io.File;
import java.util.Iterator;

import com.badlogic.gdx.utils.Pool;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.Audio.SoundData;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Audio.SourceCallback;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Loader.WAVLoader;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.CueScript;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Ai.Attrib_Snds;
import ru.m210projects.Wang.Main.UserFlag;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC;
import ru.m210projects.Wang.Type.VOC3D;
import ru.m210projects.Wang.Type.VOC_INFO;
import ru.m210projects.Wang.Type.Vector3i;

public class Sound {

	private static SourceCallback<VOC3D> callback = new SourceCallback<VOC3D>() {
		@Override
		public void run(VOC3D ch) {
			if (ch.handle != null) {
				ch.handle = null; // the handle already don't belong this voc
				if (ch.vp != null)
					ch.vp.lock++;
			}
		}
	};

	private static Pool<VOC3D> vocList = new Pool<VOC3D>() {
		@Override
		protected VOC3D newObject() {
			return new VOC3D();
		}
	};

	public static Vector3i sndCoords;

	private static class TVOC_Info {
		public VOC3D p;
		public short dist;
		public int priority;
	}

	private static TVOC_Info TmpVocArray[] = new TVOC_Info[32];

	public static final int TauntAIVocs[] = { DIGI_TAUNTAI1, DIGI_TAUNTAI2, DIGI_TAUNTAI3, DIGI_TAUNTAI4, DIGI_TAUNTAI5,
			DIGI_TAUNTAI6, DIGI_TAUNTAI7, DIGI_TAUNTAI8, DIGI_TAUNTAI9, DIGI_TAUNTAI10, DIGI_COWABUNGA, DIGI_NOCHARADE,
			DIGI_TIMETODIE, DIGI_EATTHIS, DIGI_FIRECRACKERUPASS, DIGI_HOLYCOW, DIGI_HAHA2, DIGI_HOLYPEICESOFCOW,
			DIGI_HOLYSHIT, DIGI_HOLYPEICESOFSHIT, DIGI_PAYINGATTENTION, DIGI_EVERYBODYDEAD, DIGI_KUNGFU,
			DIGI_HOWYOULIKEMOVE, DIGI_HAHA3, DIGI_NOMESSWITHWANG, DIGI_RAWREVENGE, DIGI_YOULOOKSTUPID, DIGI_TINYDICK,
			DIGI_NOTOURNAMENT, DIGI_WHOWANTSWANG, DIGI_MOVELIKEYAK, DIGI_ALLINREFLEXES };

	public static final int PlayerPainVocs[] = { DIGI_PLAYERPAIN1, DIGI_PLAYERPAIN2, DIGI_PLAYERPAIN3, DIGI_PLAYERPAIN4,
			DIGI_PLAYERPAIN5 };

	// 3D sound engine declarations //////////////////////////////////////////////
	// Flag settings used to turn on and off 3d sound options
	public static final int v3df_none = 0; // Default, take no action, use all defaults
	public static final int v3df_follow = 1; // 1 = Do coordinate updates on sound
	// Use this only if the sprite won't be deleted soon
	public static final int v3df_kill = 2; // 1 = Sound is to be deleted
	public static final int v3df_doppler = 4; // 1 = Don't use doppler pitch variance
	public static final int v3df_dontpan = 8; // 1 = Don't do panning of sound
	public static final int v3df_ambient = 16; // 1 = Sound is ambient, use ambient struct info.
	public static final int v3df_intermit = 32; // 1 = Intermittant sound
	public static final int v3df_init = 64; // 1 = First pass of sound, don't play it.
	// This is mainly used for intermittent sounds
	public static final int v3df_nolookup = 128; // don't use ambient table lookup

	public static final int MAXLEVLDIST = 19000; // The higher the number, the further away you can hear sound
	public static final int DECAY_CONST = 4000;

	public static final int MAX_AMBIENT_SOUNDS = 82;

	enum SoundType {
		SOUND_OBJECT_TYPE, SOUND_EVERYTHING_TYPE
	};

	// Don't have these sounds yet
	public static final int PlayerLowHealthPainVocs[] = { DIGI_HURTBAD1, DIGI_HURTBAD2, DIGI_HURTBAD3, DIGI_HURTBAD4,
			DIGI_HURTBAD5 };

	public static final int PlayerGetItemVocs[] = { DIGI_GOTITEM1, DIGI_HAHA1, DIGI_BANZAI, DIGI_COWABUNGA,
			DIGI_TIMETODIE };

	public static final int PlayerYellVocs[] = { DIGI_PLAYERYELL1, DIGI_PLAYERYELL2, DIGI_PLAYERYELL3 };

	public static final int NUM_SAMPLES = 10;

	public static final int SOUND_UNIT = MAXLEVLDIST / 255;

	// Global vars used by ambient sounds to set spritenum of ambient sounds for
	// later lookups in
	// the sprite array so FAFcansee can know the sound sprite's current sector
	// location
	private static boolean Use_SoundSpriteNum = false;
	private static short SoundSpriteNum = -1; // Always set this back to -1 for proper validity checking!

	public static void SoundStartup() {
		if (gs.maxvoices < 8)
			gs.maxvoices = 8;

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, gs.maxvoices, gs.resampler_num)) {
			BuildGdx.audio.setVolume(Driver.Sound, gs.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
		}

		for (int i = 0; i < 32; i++)
			TmpVocArray[i] = new TVOC_Info();

		sndCoords = new Vector3i();
	}

	public static boolean sndRestart(int nvoices, int resampler) {
		BuildGdx.audio.getSound().stopAllSounds();
		BuildGdx.audio.getSound().uninit();
		gs.maxvoices = nvoices;

		Console.Println("Sound restarting...");

		for (int i = 0; i < voc.length; i++)
			if (voc[i] != null)
				voc[i].lock = MAXSOURCEINTICK;

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, nvoices, resampler)) {
			BuildGdx.audio.setVolume(Driver.Sound, gs.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
			return false;
		}
		return true;
	}

	public static void MusicStartup() {
//		if (!BuildGdx.audio.getMusic().init())
//			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
		BuildGdx.audio.setDriver(Driver.Music, 0); // set it to DummyMusic

		if (!gs.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, gs.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);
	}

	private static int SoundDist(int x, int y, int z, int basedist) {
		double tx, ty, tz;
		double sqrdist, retval;
		double decay, decayshift;

		tx = klabs(Player[screenpeek].posx - x);
		ty = klabs(Player[screenpeek].posy - y);
		tz = klabs((Player[screenpeek].posz - z) >> 4);

		// Use the Pythagreon Theorem to compute the magnitude of a 3D vector
		sqrdist = Math.abs(tx * tx + ty * ty + tz * tz);
		retval = Math.sqrt(sqrdist);

		if (basedist < 0) // if basedist is negative
		{
			short i;

			decayshift = 2;
			decay = klabs(basedist) / DECAY_CONST;

			for (i = 0; i < decay; i++)
				decayshift *= 2;

			if (Math.abs(basedist / decayshift) >= retval)
				retval = 0;
			else
				retval *= decay;
		} else {
			if (basedist > retval)
				retval = 0;
			else
				retval -= basedist;
		}

		retval = retval * 256 / MAXLEVLDIST;

		if (retval < 0)
			retval = 0;
		if (retval > 255)
			retval = 255;

		return (int) (retval);
	}

	public static boolean CacheSound(int num) {
		if (num < 0 || num >= voc.length || gs.noSound)
			return false;

		VOC_INFO vp = voc[num];

		// if no data we need to cache it in
		if (vp.data == null) {
			Resource fp = null;
			if (vp.name != null)
				fp = BuildGdx.cache.open(vp.name, 0);
			if (fp == null) {
				Console.Println("Could not open sound " + vp.name + ", num " + num + ", priority " + vp.priority);
				return (false);
			} else {
				vp.datalen = fp.size();
				vp.data = new VOC(fp.getBytes());
				fp.close();
			}
		}

		return (true);
	}

	public static void COVER_SetReverb(int amt) {
		if (amt != 0)
			BuildGdx.audio.getSound().setReverb(true, amt / 255.0f);
		else
			BuildGdx.audio.getSound().setReverb(false, amt);
	}

	public static void PlayerSound(int num, int flags, PlayerStr pp) {
		if (Prediction)
			return;

		if (pp.pnum < 0 || pp.pnum >= MAX_SW_PLAYERS) {

			game.ThrowError("Player Sound invalid player");
			System.exit(0);
		}

		if (TEST(pp.Flags, PF_DEAD))
			return; // You're dead, no talking!

		// If this is a player voice and he's already yacking, forget it.
		VOC_INFO vp = voc[num];
		if (vp == null) {
			Console.Println("vp == null in PlayerSound, num = " + num, Console.OSDTEXT_RED);
			return;
		}

		// Not a player voice, bail.
		if (vp.priority != PRI_PLAYERVOICE && vp.priority != PRI_PLAYERDEATH)
			return;

		// He wasn't talking, but he will be now.
		if (!pp.PlayerTalking) {
			pp.PlayerTalking = true;
			pp.TalkVocnum = num; // Set the voc number
			pp.TalkVocHandle = PlaySound(num, pp, flags); // Play the sound
			if (pp.TalkVocHandle == null) { // if don't have free sources
				pp.PlayerTalking = false;
				pp.TalkVocnum = -1;
			}
		}
	}

	public static void StartAmbientSound() {
		if (!gs.Ambient)
			return;

		int nexti;
		for (int i = headspritestat[STAT_AMBIENT]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			SPRITE sp = sprite[i];

			VOC3D voc = PlaySound(sp.lotag, sp, v3df_ambient | v3df_init | v3df_doppler | v3df_follow);
			// Ambient sounds need this to get sectnum for later processing
			Set3DSoundOwner(i, voc);
		}
	}

	public static void StopSound() {
		CDAudio_Stop();
		StopFX();
	}

	public static void StopFX() {
		BuildGdx.audio.getSound().stopAllSounds();
	}

	public static void sndHandlePause(boolean gPaused) {
		if (gPaused) {
			CDAudio_Pause();
			StopFX();
			StopAmbientSound();
		} else {
			CDAudio_Continue();
			StartAmbientSound();
		}
	}

	public static void Set3DSoundOwner(int spritenum, VOC3D p) {
		if (p == null)
			return;

		// Queue up sounds with ambient flag even if they didn't play right away!
		if (p.handle != null || TEST(p.flags, v3df_ambient))
			p.owner = (short) spritenum;
		else {
			p.deleted = true;
			p.flags = v3df_kill;
		}
	}

	public static void PlaySpriteSound(int spritenum, Attrib_Snds attrib_ndx, int flags) {
		SPRITE sp = sprite[spritenum];
		USER u = pUser[spritenum];

		PlaySound(u.Attrib.getSound(attrib_ndx), sp, flags);
	}

	// NOTE: If v3df_follow == 1, x,y,z are considered literal coordinates
	public static VOC3D PlaySound(int num, Object obj, int flags) {
		if (!BuildGdx.audio.IsInited(Driver.Sound) || Prediction || gs.noSound || num < 0 || num >= voc.length)
			return null;

		Source voice = null;
		SPRITE sp = null;
		// This is used for updating looping sounds in Update3DSounds
		if (Use_SoundSpriteNum && SoundSpriteNum >= 0)
			sp = sprite[SoundSpriteNum];

		if (gs.Ambient && TEST(flags, v3df_ambient) && !TEST(flags, v3df_nolookup)) // Look for invalid ambient numbers
		{
			if (num < 0 || num > MAX_AMBIENT_SOUNDS) {
				PutStringInfo(Player[screenpeek], "Invalid or out of range ambient sound number " + num);
				return null;
			}
		}

		// Call queue management to add sound to play list.
		// 3D sound manager will update playing sound 10x per second until
		// the sound ends, at which time it is removed from both the 3D
		// sound list as well as the actual cache.
		VOC3D v3p = Insert3DSound();

		// If the ambient flag is set, do a name conversion to point to actual
		// digital sound entry.
		v3p.num = num;
		v3p.priority = 0;

		if (gs.Ambient && TEST(flags, v3df_ambient) && !TEST(flags, v3df_nolookup)) {
			v3p.maxtics = STD_RANDOM_RANGE(ambarray[num].maxtics);
			flags |= ambarray[num].ambient_flags; // Add to flags if any
			num = ambarray[num].diginame;
		}

		// Assign voc to voc pointer
		VOC_INFO vp = voc[num];
		if (vp == null || vp.lock <= 0) {
			v3p.flags = v3df_kill;
			v3p.handle = null;
			v3p.dist = 0;
			v3p.deleted = true; // Sound init failed, remove it!
			return null;
		}

		int priority = vp.priority;
		if (game.pMenu.gShowMenu && obj == null) // Menus sound outdo everything
			priority = 100;
		v3p.vp = vp;

		// Assign voc info to 3d struct for future reference
		v3p.obj = obj;
		v3p.fx = v3p.getCoords().x;
		v3p.fy = v3p.getCoords().y;
		v3p.fz = v3p.getCoords().z;
		v3p.flags = flags;

		int tx = v3p.fx, ty = v3p.fy, tz = v3p.fz;
		if (((vp.voc_flags & 1) != 0) && Use_SoundSpriteNum && SoundSpriteNum >= 0 && sp != null) {
			tx = sp.x;
			ty = sp.y;
			tz = sp.z;
		}

		short sound_dist = 255;
		// Calculate sound distance
		if (tx != 0 || ty != 0 || tz != 0)
			sound_dist = (short) SoundDist(tx, ty, tz, vp.voc_distance);

		v3p.doplr_delta = sound_dist; // Save of distance for doppler effect

		// Can the ambient sound see the player? If not, tone it down some.
		if ((vp.voc_flags & 1) != 0 && Use_SoundSpriteNum && SoundSpriteNum >= 0) {
			PlayerStr pp = Player[screenpeek];

			if (!FAFcansee(tx, ty, tz, sp.sectnum, pp.posx, pp.posy, pp.posz, pp.cursectnum)) {
				sound_dist += ((sound_dist / 2) + (sound_dist / 4)); // Play more quietly
				if (sound_dist > 255)
					sound_dist = 255;

				// Special Cases
				if (num == DIGI_WHIPME)
					sound_dist = 255;
			}
		}

		// Assign ambient priorities based on distance
		// if (gs.Ambient && TEST(flags, v3df_ambient))
		priority = v3p.priority = vp.priority - (sound_dist / 26);

		if (!CacheSound(num)) {
			v3p.flags = v3df_kill;
			v3p.handle = null;
			v3p.dist = 0;
			v3p.deleted = true; // Sound init failed, remove it!
			return null;
		}

		int pitch = 0;
		if (vp.pitch_hi == vp.pitch_lo)
			pitch = vp.pitch_lo;
		else if (vp.pitch_hi != vp.pitch_lo)
			pitch = vp.pitch_lo + (STD_RANDOM_RANGE(vp.pitch_hi - vp.pitch_lo));

		float volume = 0.0f;
		// Request playback and play it as a looping sound if flag is set.
		if ((vp.voc_flags & 1) != 0) {
			short loopvol = 0;

			if ((loopvol = (short) (255 - sound_dist)) <= 0)
				loopvol = 0;

			if (sound_dist < 255 || (flags & v3df_init) != 0) {
				volume = loopvol / 255.0f;
				voice = BuildGdx.audio.newSound(vp.data.data, vp.data.rate + pitch, vp.data.bits, priority);
				if (voice != null)
					voice.setLooping(true, 0, -1);
			}
		} else if (tx == 0 && ty == 0 && tz == 0) {
			// It's a non-inlevel sound
			voice = BuildGdx.audio.newSound(vp.data.data, vp.data.rate + pitch, vp.data.bits, priority);
			if (voice != null) {
				voice.setGlobal(1);
				volume = 1.0f;
			}
		} else {
			// It's a 3d sound
			if (sound_dist < 255) {
				volume = (255 - sound_dist) / 255.0f;
				voice = BuildGdx.audio.newSound(vp.data.data, vp.data.rate + pitch, vp.data.bits, priority);
				if (voice != null) {
					if ((flags & v3df_dontpan) != 0 /* || sound_dist < 5 */) {// If true, don't do panning
						voice.setGlobal(1);
					}
				}
			}
		}

		// Assign voc info to 3d struct for future reference
		v3p.handle = voice; // Save the current voc handle in struct
		v3p.dist = sound_dist;
		v3p.tics = 0; // Reset tics
		if ((flags & v3df_init) != 0)
			v3p.flags ^= v3df_init; // Turn init off now

		if (voice != null) {
			if (!voice.isGlobal()) // source can be global if v3df_dontpan != 0
				voice.setPosition(tx, tz >> 4, ty);
			voice.play(volume);
			voice.setCallback(callback, v3p);
			v3p.vp.lock--;
		}
		return (v3p);
	}

	private static float calcPitch(int pitch) {
		float fp = 1.0f;
		fp += pitch / 2000.0f;

		return fp;
	}

	public static void Terminate3DSounds() {

		VOC3D vp = voc3dstart;

		while (vp != null) {
			vp.stop();
			vp.deleted = true;
			vp = vp.next;
		}

		Delete3DSounds(); // Now delete all remaining sounds
	}

	public static void DoUpdateSounds3D() {
		if (game.pMenu.gShowMenu)
			return;

		int cx = Player[screenpeek].posx;
		int cy = Player[screenpeek].posy;
		int cz = Player[screenpeek].posz;
		int ca = Player[screenpeek].getAnglei();

		BuildGdx.audio.getSound().setListener(cx, cz >> 4, cy, ca);

		for (int i = 0; i < 32; i++) {
			TmpVocArray[i].p = null;
			TmpVocArray[i].dist = 0;
			TmpVocArray[i].priority = 0;
		}

		VOC3D p = voc3dstart;
		while (p != null) {
			boolean looping = p.vp != null && (p.vp.voc_flags & 1) != 0;

			// If sprite owner is dead, kill this sound as long as it isn't ambient
			if (looping && p.owner == -1 && !TEST(p.flags, v3df_ambient))
				p.flags |= (v3df_kill);

			// Is the sound slated for death? Kill it, otherwise play it.
			if ((p.flags & v3df_kill) != 0) {
				p.stop();// Make sure to stop active sounds
				p.deleted = true;
			} else {
				if (!p.isActive() && !looping) {

					if ((p.flags & v3df_intermit) != 0) {
						DoTimedSound(p);
					} else {
						p.deleted = true;
					}
				} else if (p.isActive()) {
					int x, y, z;
					int dist = 0;
					if ((p.flags & v3df_follow) != 0) {
						Vector3i pos = p.getCoords();

						dist = SoundDist(pos.x, pos.y, pos.z, p.vp.voc_distance);

						x = pos.x;
						y = pos.y;
						z = pos.z;
					} else {
						if (p.fx == 0 && p.fy == 0 && p.fz == 0)
							dist = 0;
						else
							dist = SoundDist(p.fx, p.fy, p.fz, p.vp.voc_distance);

						x = p.fx;
						y = p.fy;
						z = p.fz;
					}

					// Can the ambient sound see the player? If not, tone it down some.
					if ((p.vp.voc_flags & 1) != 0 && p.owner != -1) {
						PlayerStr pp = Player[screenpeek];
						SPRITE sp = sprite[p.owner];

						if (!FAFcansee(sp.x, sp.y, sp.z, sp.sectnum, pp.posx, pp.posy, pp.posz, pp.cursectnum)) {
							dist += ((dist / 2) + (dist / 4)); // Play more quietly
							if (dist > 255)
								dist = 255;

							// Special cases
							if (p.num == 76 && TEST(p.flags, v3df_ambient)) {
								dist = 255; // Cut off whipping sound, it's secret
							}
						}
					}

					if (dist >= 255 && p.vp.voc_distance == DIST_NORMAL) {
						p.stop(); // Make sure to stop active sounds
					} else {
						if (!p.handle.isGlobal())
							p.handle.setVolume((255 - dist) / 255.0f);

						// Handle Doppler Effects
						if ((p.flags & v3df_doppler) == 0 && p.isActive()) {
							int pitch = -(dist - p.doplr_delta);
							int pitchmax;

							if (p.vp.pitch_lo != 0 && p.vp.pitch_hi != 0) {
								if (klabs(p.vp.pitch_lo) > klabs(p.vp.pitch_hi))
									pitchmax = klabs(p.vp.pitch_lo);
								else
									pitchmax = klabs(p.vp.pitch_hi);
							} else
								pitchmax = 400;

							if (pitch > pitchmax)
								pitch = pitchmax;
							if (pitch < -pitchmax)
								pitch = -pitchmax;

							p.doplr_delta = (short) dist; // Save new distance to struct
							p.handle.setPitch(calcPitch(pitch));
						}

						// Handle Panning Left and Right
						if ((p.flags & v3df_dontpan) == 0) {
							if (!p.handle.isGlobal()) // source can be global if v3df_dontpan != 0 or dist < 5
								p.handle.setPosition(x, z >> 4, y);
						}
					}
				} else if (!p.isActive() && looping) {
					int dist = 0;
					if ((p.flags & v3df_follow) != 0) {
						Vector3i pos = p.getCoords();
						dist = SoundDist(pos.x, pos.y, pos.z, p.vp.voc_distance);
					} else {
						dist = SoundDist(p.fx, p.fy, p.fz, p.vp.voc_distance);
					}

					// Sound was bumped from active sounds list, try to play
					// again.
					// Don't bother if voices are already maxed out.
					// Sort looping vocs in order of priority and distance
					if (dist <= 255) {
						for (int i = 0; i < TmpVocArray.length; i++) {
							if (p.priority >= TmpVocArray[i].priority) {
								if (TmpVocArray[i].p == null || dist < TmpVocArray[i].dist) {

									TmpVocArray[i].p = p;
									TmpVocArray[i].dist = (short) dist;
									TmpVocArray[i].priority = p.priority;
									break;
								}
							}
						}
					}
				}
			}
			p = p.next;
		}

		// Process all the looping sounds that said they wanted to get back in
		// Only update these sounds 5x per second! Woo hoo!, aren't we optimized now?
		for (int i = 0; i < TmpVocArray.length; i++) {
			p = TmpVocArray[i].p;

			if (p == null)
				break;

			if ((p.flags & v3df_follow) != 0) {
				if (p.owner == -1) {
					int enumber = p.num;
					game.ThrowError("Owner == -1 on looping sound with follow flag set!\r\n p.num = " + enumber);
					System.exit(0);
				}

				Use_SoundSpriteNum = true;
				SoundSpriteNum = p.owner;

				PlaySound(p.num, p.obj, p.flags);

				voc3dend.owner = p.owner; // Transfer the owner
				p.deleted = true;

				Use_SoundSpriteNum = false;
				SoundSpriteNum = -1;
			} else {
				if (p.owner == -1) {
					int enumber = p.num;
					game.ThrowError("Owner == -1 on looping sound, no follow flag.\r\n p.num = " + enumber);
					System.exit(0);
				}

				Use_SoundSpriteNum = true;
				SoundSpriteNum = p.owner;

				PlaySound(p.num, p, p.flags);
				voc3dend.owner = p.owner; // Transfer the owner
				p.deleted = true;

				Use_SoundSpriteNum = false;
				SoundSpriteNum = -1;
			}
		}
		// } // MoveSkip8

		// Clean out any deleted sounds now
		Delete3DSounds();
	}

	private static int ambrand[] = { 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67 };

	private static int RandomizeAmbientSpecials(int handle) {
		// If ambient sound is found in the array, randomly pick a new sound
		for (int i = 0; i < ambrand.length; i++) {
			if (handle == ambrand[i])
				return (ambrand[STD_RANDOM_RANGE(ambrand.length - 1)]);
		}

		return (handle); // Give back the sound, no new one was found
	}

	private static void DoTimedSound(VOC3D p) {
		p.tics += synctics;

		if (p.tics >= p.maxtics) {
			if (!p.isActive()) {
				// Check for special case ambient sounds
				p.num = RandomizeAmbientSpecials(p.num);

				// Sound was bumped from active sounds list, try to play again.
				// Don't bother if voices are already maxed out.

				if ((p.flags & v3df_follow) != 0) {
					PlaySound(p.num, p.obj, p.flags);
					p.deleted = true; // Mark old sound for deletion
				} else {
					PlaySound(p.num, p, p.flags);
					p.deleted = true; // Mark old sound for deletion
				}
			}

			p.tics = 0;
		}
	}

	public static void StopAmbientSound() {
		VOC3D p = voc3dstart;

		while (p != null) {
			// kill ambient sounds if Ambient is off
			if (TEST(p.flags, v3df_ambient))
				p.flags |= (v3df_kill);

			if ((p.flags & v3df_kill) != 0) {
				p.stop(); // Make sure to stop active sounds

				p.deleted = true;
			}

			p = p.next;
		}

		Delete3DSounds();
	}

	///////////////////////////////////////////////
	//
	// 3D sound engine
	// Sound management routines that keep a list of
	// all sounds being played in a level.
	// Doppler and Panning effects are achieved here.
	//
	///////////////////////////////////////////////
	// Declare and initialize linked list of vocs.
	private static VOC3D voc3dstart = null;
	private static VOC3D voc3dend = null;

	//////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////
	// Initialize new vocs in the 3D sound queue
	///////////////////////////////////////////////
	private static VOC3D InitNew3DSound(VOC3D v3p) {
		v3p.handle = null; // Initialize handle to new sound value
		v3p.owner = -1;
		v3p.deleted = false; // Used for when sound gets deleted

		return (v3p);
	}

	///////////////////////////////////////////////
	// Inserts new vocs in the 3D sound queue
	///////////////////////////////////////////////
	private static VOC3D Insert3DSound() {
		VOC3D vp, old;

		// Allocate memory for new sound
		// You can allocate new sounds as long as memory holds out.
		// If you run out of memory for sounds, you got problems anyway.
		vp = vocList.obtain();

		if (voc3dend == null) // First item in list
		{
			vp.next = vp.prev = null;
			voc3dend = vp;
			voc3dstart = vp;
			InitNew3DSound(vp);
			return (vp);
		}

		old = voc3dend; // Put it on the end
		old.next = vp;
		vp.next = null;
		vp.prev = old;
		voc3dend = vp;

		InitNew3DSound(vp);
		return (vp);
	}

	/////////////////////////////////////////////////////
	// Deletes vocs in the 3D sound queue with no owners
	/////////////////////////////////////////////////////
	public static void DeleteNoSoundOwner(int spritenum) {
		VOC3D vp, dp;

		vp = voc3dstart;

		while (vp != null) {
			dp = null;
			if (vp.owner == spritenum && vp.owner >= 0 && (vp.vp.voc_flags & 1) != 0) {
				// Make sure to stop active sounds
				vp.stop();

				dp = vp; // Point to sound to be deleted

				if (vp.prev != null) {
					vp.prev.next = vp.next;
				} else {
					voc3dstart = vp.next; // New first item
					if (voc3dstart != null)
						voc3dstart.prev = null;
				}

				if (vp.next != null) {
					vp.next.prev = vp.prev; // Middle element
				} else {
					voc3dend = vp.prev; // Delete last element
				}
			}

			vp = vp.next;

			if (dp != null) {
				vocList.free(dp); // Return memory to heap
			}
		}
	}

	///////////////////////////////////////////////
	// This is called from KillSprite to kill a follow sound with no valid sprite
	// owner
	// Stops and active sound with the follow bit set, even play once sounds.
	public static void DeleteNoFollowSoundOwner(int spritenum) {
		VOC3D vp, dp;
		SPRITE sp = sprite[spritenum];

		vp = voc3dstart;

		while (vp != null) {
			dp = null;
			// If the follow flag is set, compare the x and y addresses.
			Vector3i pos = vp.getCoords();
			if ((vp.flags & v3df_follow) != 0 && sp == vp.obj && pos.x == sp.x && pos.y == sp.y) {
				vp.stop();

				dp = vp; // Point to sound to be deleted

				if (vp.prev != null) {
					vp.prev.next = vp.next;
				} else {
					voc3dstart = vp.next; // New first item
					if (voc3dstart != null)
						voc3dstart.prev = null;
				}

				if (vp.next != null) {
					vp.next.prev = vp.prev; // Middle element
				} else {
					voc3dend = vp.prev; // Delete last element
				}
			}

			vp = vp.next;

			if (dp != null) {
				vocList.free(dp); // Return memory to heap
			}
		}
	}

	///////////////////////////////////////////////
	// Deletes vocs in the 3D sound queue
	///////////////////////////////////////////////
	private static void Delete3DSounds() {
		VOC3D vp, dp;
		PlayerStr pp;

		vp = voc3dstart;

		while (vp != null) {
			dp = null;
			if (vp.deleted) {
				vp.stop(); // Make sure call callback before it deletes the source
				// Reset Player talking flag if a voice was deleted
				if (vp.vp != null && (vp.vp.priority == PRI_PLAYERVOICE || vp.vp.priority == PRI_PLAYERDEATH)) {
					for (int pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
						pp = Player[pnum];

						if (vp.num == pp.TalkVocnum) {
							pp.PlayerTalking = false;
							pp.TalkVocnum = -1;
							pp.TalkVocHandle = null;
						}
					}
				}

				dp = vp; // Point to sound to be deleted
				if (vp.prev != null) {
					vp.prev.next = vp.next;
				} else {
					voc3dstart = vp.next; // New first item
					if (voc3dstart != null)
						voc3dstart.prev = null;
				}

				if (vp.next != null) {
					vp.next.prev = vp.prev; // Middle element
				} else {
					voc3dend = vp.prev; // Delete last element
				}
			}

			vp = vp.next;

			if (dp != null) {
				vocList.free(dp); // Return memory to heap
			}
		}
	}

	public static boolean midRestart() {
		CDAudio_Stop();

		BuildGdx.audio.setDriver(Driver.Music, 0);
//		if (!BuildGdx.audio.getMusic().init()) {
//			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
//			return false;
//		}

		return true;
	}

	public static boolean PlaySoundRTS(int rts_num) {
		if (gs.noSound || !BuildGdx.audio.IsInited(Driver.Sound) || !RTS_Started || (RTS_NumSounds() <= 0)
				|| rtsplaying != 0)
			return false;

		byte[] rtsptr = RTS_GetSound(rts_num);
		if (rtsptr != null && rtsptr.length > 0) {
			try {
				SoundData data = null;
				if (rtsptr[0] == 'C')
					data = new VOC(rtsptr);
				else
					data = new WAVLoader(rtsptr);

				Source voice = BuildGdx.audio.newSound(data.data, data.rate, data.bits, 255);
				if (voice != null) {
					voice.setGlobal(1);
					voice.play(1.0f);
					rtsplaying = 16;
					return true;
				}
			} catch (Exception e) {
			}
		}

		return false;
	}

	// My Stuff ////////////////////////////////////////////////////////////////
	// Never play track 1, that's the game.
	public static final int RedBookSong[] = { 2, 4, 9, 12, 10, // Title and ShareWare levels
			5, 6, 8, 11, 12, 5, 10, 4, 6, 9, 7, 10, 8, 7, 9, 10, 11, 5, // Registered levels
			11, 8, 7, 13, 5, 6, // Deathmatch levels
			13 // Fight boss
	};

	public static int playTrack = -1;
	public static final String[] cdtracks = { "", "track02.ogg", "track03.ogg", "track04.ogg", "track05.ogg",
			"track06.ogg", "track07.ogg", "track08.ogg", "track09.ogg", "track10.ogg", "track11.ogg", "track12.ogg",
			"track13.ogg", "track14.ogg" };
	public static MusicSource currMusic = null;

	public static void InitCDtracks() {
		CueScript cdTracks = null;
		String[] cds = new String[cdtracks.length];
		System.arraycopy(cdtracks, 0, cds, 0, cds.length);

		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it
				.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("cue")) {
				byte[] data = BuildGdx.compat.getBytes(file);
				if(data != null) {
					cdTracks = new CueScript(file.getName(), data);
					String[] newcds = cdTracks.getTracks();
					System.arraycopy(newcds, 0, cds, 0, Math.min(newcds.length, cds.length));
					break;
				}
			}
		}

		int numtracks = cds.length;
		for (int i = 0; i < cds.length; i++) {
			if (!BuildGdx.cache.contains(cds[i], 0)) {
				cds[i] = null;
				numtracks--;
			}
		}

		if (numtracks <= 0 && cdTracks == null) {
			DirectoryEntry mus = BuildGdx.compat.checkDirectory("music");
			if (mus == null)
				mus = BuildGdx.compat.checkDirectory("classic" + File.separator + "music");

			if (mus != null) {
				System.arraycopy(cdtracks, 0, cds, 0, cds.length);
				numtracks = cds.length;
				for (int i = 0; i < cds.length; i++) {
					FileEntry fil;
					if ((fil = mus.checkFile(cds[i])) == null) {
						cds[i] = null;
						numtracks--;
					} else
						cds[i] = fil.getPath();
				}
			}
		}

		System.arraycopy(cds, 0, cdtracks, 0, cds.length);
		if (numtracks > 0)
			Console.Println(numtracks + " cd tracks found...");
		else
			Console.Println("Cd tracks not found.");
	}

	public static void StartMusic() {
		if (game.isCurrentScreen(gMenuScreen)) {
			CDAudio_Play(RedBookSong[0], true);
		} else {
			if (mUserFlag == UserFlag.UserMap)
				CDAudio_Play(RedBookSong[4 + RANDOM_RANGE(10)], true); // track, loop - Level songs are looped
			else
				CDAudio_Play(RedBookSong[Level], true);
		}
	}

	public static void CDAudio_Pause() {
		if (currMusic != null)
			currMusic.pause();
	}

	public static void CDAudio_Continue() {
		if (!gs.muteMusic && currMusic != null)
			currMusic.resume();
	}

	public static boolean CDAudio_Play(int track, boolean looping) {
		if ( /* cfg.musicType == 0 || */ track < 0 || gs.muteMusic)
			return false;

		if (CDAudio_Playing() && playTrack == track)
			return true;

		MusicSource mus = null;

		CDAudio_Stop();
		if (cdtracks != null && track > 0 && track <= cdtracks.length && cdtracks[track - 1] != null
				&& (mus = BuildGdx.audio.newMusic(MusicType.Digital, cdtracks[track - 1])) != null) {
			currMusic = mus;
			playTrack = track;
			currMusic.play(looping);
			return true;
		}

		return false;
	}

	public static void CDAudio_Stop() {
		if (currMusic != null)
			currMusic.stop();

		playTrack = -1;
		currMusic = null;
	}

	public static boolean CDAudio_Playing() {
		return currMusic != null && currMusic.isPlaying();
	}

}
