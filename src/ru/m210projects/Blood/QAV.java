// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood;

import static ru.m210projects.Blood.Gameutils.ClipRange;

import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Tile.*;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Weapon.gWeaponCallback;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;

import com.badlogic.gdx.math.Vector2;

public class QAV {

	public static final String kQAVSig = "QAV\032";
	public static final int kQAVVersion	= 0x0200;
	public static final int kMaxLayers = 8;

	public static final int 	kQFrameNormal			= 0;		// no flags
	public static final int		kQFrameTranslucent		= 0x01;		// frame is translucent
	public static final int		kQFrameScale			= 0x02;		// frame is scaled to viewing window
	public static final int		kQFrameYFlip			= 0x04;		// frame is y-flipped
	public static final int		kQFrameUnclipped		= 0x08;		// frame is not clipped to umost/dmost
	public static final int		kQFrameStatus			= 0x0A;		// frame is not masked (used for status bar)
	public static final int		kQFrameCorner			= 0x10;		// frame is positioned by top left corner instead of origin
	public static final int		kQFrameTranslucentR		= 0x20;		// frame is translucent, using reverse translucency table
	public static final int		kQFrameNoMask			= 0x40;
	public static final int		kQFrameAllPages			= 0x80;
			// begin internal flags
	public static final int		kQFrameXFlip			= 0x100;	// frame is x-flipped
	public static final int		kQFrameWideLeft			= 0x200;


	public static final int kFlagSoundKill = 0x01; // Kill sound of the same priority in all frames
	public static final int kFlagSoundKillAll = 0x02; // Kill sound with any priority in all frames

	public String signature;
	public short version;
	public Vector2 origin;
	public int ticksPerFrame;
	public int duration;
	public FRAME[] frame;
	public int	nFrames;
	public SPRITE sprite;
	public int mindisp;

	public QAV(Resource bb) {
    	signature = bb.readString(4);
    	version = bb.readShort();
    	bb.seek(2, Whence.Current); //dummy
    	nFrames = bb.readInt();
    	frame = new FRAME[nFrames];
    	ticksPerFrame = bb.readInt();
    	duration = bb.readInt();
		origin = new Vector2(bb.readInt(), bb.readInt());
		bb.seek(8, Whence.Current); //reserved

		int maxsizy = 0;
		int tframey = 0;
		int yoffs = 0;

		for(int f = 0; f < nFrames; f++) {
			int trigger_frameid = bb.readInt();
			int	sound_frameid = bb.readInt();
			int	sound_frame_priority = bb.readByte() & 0xFF;
			int	sound_frame_flags = bb.readByte() & 0xFF;
			short sound_frame_range = bb.readShort();

			TILE_FRAME[] layer = new TILE_FRAME[kMaxLayers];
			for(int i = 0; i < kMaxLayers; i++) {
				layer[i] = new TILE_FRAME(bb);
				int picnum = layer[i].id;
				if(picnum >= 0 && picnum < MAXTILES) {
					Tile pic = engine.getTile(picnum);

					if(pic.getHeight() > maxsizy) {
						maxsizy = pic.getHeight();
						tframey = layer[i].y;
						yoffs = pic.getOffsetY();
					}
				}
			}
			frame[f] = new FRAME(trigger_frameid, sound_frameid, sound_frame_priority, sound_frame_flags, sound_frame_range, layer);
		}

		mindisp = (yoffs - maxsizy / 2) - tframey;
	}

	public void Preload()
	{
		for (int i = 0; i < nFrames; i++)
		{
			for (int j = 0; j < kMaxLayers; j++)
			{
				if ( frame[i].layer[j].id >= 0 ) {
					tilePreloadTile(frame[i].layer[j].id);
				}
			}
		}
	}

	private void DrawFrame( int x, int y, TILE_FRAME f, int shade, int flags, int pal, int nScale )
	{
		short nAngle = f.angle;
		flags |= f.flags;

		if ( (flags & kQFrameXFlip) != 0)
		{
			nAngle = (short)((nAngle + kAngle180) & kAngleMask);
			flags &= ~kQFrameXFlip;
			flags ^= kQFrameYFlip;
		}

		if ( (flags & kQFrameWideLeft) != 0)
		{
			flags &= ~kQFrameWideLeft;
			flags |= 256;
		}

		int nPal = f.pal;
		if ( pal > 0 )
			nPal = pal;

		int zoom = mulscale(f.zoom, nScale, 16);
		int fx = mulscale(f.x, nScale, 16);
		int fy = mulscale(f.y, nScale, 16);

		engine.rotatesprite(
			(x + fx) << 16,
			(y + fy) << 16,
			zoom,
			nAngle,
			(short)f.id,
			ClipRange(f.shade + shade, -128, 127),
			(char)nPal,
			(char)flags,
			windowx1, windowy1, windowx2, windowy2);
	}

	// render a complete frame for time t
	public void Draw( long t, int shade, int flags, int nPLU, int nScale )
	{
		if(ticksPerFrame < 0) game.dassert("ticksPerFrame > 0");
		int nFrame = (int)(t / ticksPerFrame);
		if(nFrame < 0 || nFrame > nFrames) game.dassert("nFrame >= 0 && nFrame < nFrames, nFrame: " + nFrame);

		FRAME f = frame[nFrame];
		for (int i = 0; i < kMaxLayers; i++)
		{
			if ( f.layer[i].id > 0 ) {
			 	DrawFrame((int)origin.x, (int)origin.y, f.layer[i], shade, flags, nPLU, nScale);
			}
		}
	}

	public void Play( int t0, int t1, int callback, Object data)
	{
		if(ticksPerFrame > 0) {
			int nFrame;
			// need to truncate toward -infinity
			if ( t0 < 0 ) nFrame = (t0 + 1) / ticksPerFrame;
			else nFrame = t0 / ticksPerFrame + 1;

			int t = nFrame * ticksPerFrame;
			for ( ; t <= t1; t += ticksPerFrame, nFrame++ ) {
				if ( nFrame >= 0 && nFrame < nFrames ) {
					FRAME f = frame[nFrame];

					if (f.sound_frame_flags > 0 && f.sound_frame_flags <= kFlagSoundKillAll) {
						for (int i = 0; i < nFrames; i++) {
							FRAME sFrame = frame[i];
							if (sFrame.sound_frameid != 0) {
								if (f.sound_frame_flags != kFlagSoundKillAll && sFrame.sound_frame_priority != f.sound_frame_priority)
									continue;
								else if (sprite != null) {
									// We need stop also sounds in a range
									for (int a = 0; a <= sFrame.sound_frame_range; a++)
										sfxKill3DSound(sprite,-1,sFrame.sound_frameid + a);
								} else {
									sndStopAllSamples();
								}
							}
						}
					}

					if (f.sound_frameid != 0) {
						int sound_frameid = f.sound_frameid;
						if(f.sound_frame_range > 0 && !IsOriginalDemo())
							sound_frameid += Random((f.sound_frame_range == 1) ? 2 : f.sound_frame_range);

						if (sprite != null ) {
							sfxStart3DSound(sprite, sound_frameid, f.sound_frame_priority + 16, 6);
					    	sprite = null;
						} else {
							sndStartSample(sound_frameid, -1, -1, false);
						}
					}

					// Callback triggers
					if ( f.trigger_frameid > 0 && callback != -1 ) {
						gWeaponCallback[callback].run( f.trigger_frameid, data );
					}
				}
			}
		}
	}

	class FRAME {
		int trigger_frameid;

		int	sound_frameid;
		int	sound_frame_priority;
		int	sound_frame_flags;
		short sound_frame_range;

		TILE_FRAME[] layer;

		public FRAME(int trigger_frameid, int sound_frameid, int sound_frame_priority,
				int sound_frame_flags, short sound_frame_range, TILE_FRAME[] layer)
		{
			this.trigger_frameid = trigger_frameid;
			this.sound_frameid = sound_frameid;
			this.sound_frame_priority = sound_frame_priority;
			this.sound_frame_flags = sound_frame_flags;
			this.sound_frame_range = sound_frame_range;

			this.layer = layer;
		}
	}

	static class TILE_FRAME
	{
		public static final int sizeof = 24;
		int		id;
		int		x, y;
		int		zoom;	// zoom in 16:16 fixed format

		int		flags;
		byte	shade;
		int	pal;
		short	angle;	// angle in Build units (0 is straight up)

		public TILE_FRAME(Resource bb) {
	    	id = bb.readInt();
	    	x = bb.readInt();
	    	y = bb.readInt();
	    	zoom = bb.readInt();
	    	flags = bb.readInt();
	    	shade = bb.readByte();
	    	pal = bb.readByte() & 0xFF;
	    	angle = bb.readShort();
		}
	}
}
