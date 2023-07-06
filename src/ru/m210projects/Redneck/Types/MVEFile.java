// A simple parser for Interplay MVE multimedia files
// by Mike Melanson (mike at multimedia.cx)

// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

//https://github.com/ubports/oxide_ffmpeg/blob/master/libavcodec/interplayvideo.c
//https://gitlab.freedesktop.org/gstreamer/gst-plugins-bad/blob/5dbec4ecf422943e0a9b7dadb16106b29e0753ca/gst/mve/mvevideodec8.c

package ru.m210projects.Redneck.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Types.LittleEndian;

public class MVEFile {

	private byte[] signature = ("Interplay MVE File" + (char) 0x1A + (char) 0x00).getBytes();
	private byte[] magic = { 0x1a, 0x00, 0x00, 0x01, 0x33, 0x11 };

	public static class FramePacket {

		public int pts;
//		public int stream_index;

		public byte frame_format;
		public boolean send_buffer;
		public short video_chunk_size;
		public byte[] video_chunk_data;
		public short decode_map_chunk_size;
		public byte[] decode_map_chunk_data;
		public short skip_map_chunk_size;
		public byte[] skip_map_chunk_data;
		public byte[] audio_chunk_data;

		public byte[] palette;
		public int pos;
	}

	private final int CHUNK_INIT_AUDIO = 0x0000;
	private final int CHUNK_AUDIO_ONLY = 0x0001;
	private final int CHUNK_INIT_VIDEO = 0x0002;
	private final int CHUNK_VIDEO = 0x0003;
	private final int CHUNK_SHUTDOWN = 0x0004;
	private final int CHUNK_END = 0x0005;
	private final int CHUNK_DONE = 0xFFFC;
	private final int CHUNK_EOF = 0xFFFE;
	private final int CHUNK_BAD = 0xFFFF;

	private final int OPCODE_END_OF_STREAM = 0x00;
	private final int OPCODE_END_OF_CHUNK = 0x01;
	private final int OPCODE_CREATE_TIMER = 0x02;
	private final int OPCODE_INIT_AUDIO_BUFFERS = 0x03;
	private final int OPCODE_START_STOP_AUDIO = 0x04;
	private final int OPCODE_INIT_VIDEO_BUFFERS = 0x05;
	private final int OPCODE_VIDEO_DATA_06 = 0x06;
	private final int OPCODE_SEND_BUFFER = 0x07;
	private final int OPCODE_AUDIO_FRAME = 0x08;
	private final int OPCODE_SILENCE_FRAME = 0x09;
	private final int OPCODE_INIT_VIDEO_MODE = 0x0A;
	private final int OPCODE_CREATE_GRADIENT = 0x0B;
	private final int OPCODE_SET_PALETTE = 0x0C;
	private final int OPCODE_SET_PALETTE_COMPRESSED = 0x0D;
	private final int OPCODE_SET_SKIP_MAP = 0x0E;
	private final int OPCODE_SET_DECODING_MAP = 0x0F;
	private final int OPCODE_VIDEO_DATA_10 = 0x10;
	private final int OPCODE_VIDEO_DATA_11 = 0x11;
	private final int OPCODE_UNKNOWN_12 = 0x12;
	private final int OPCODE_UNKNOWN_13 = 0x13;
	private final int OPCODE_UNKNOWN_14 = 0x14;
	private final int OPCODE_UNKNOWN_15 = 0x15;

	private final int AV_CODEC_ID_NONE = -1;
	private final int AV_CODEC_ID_INTERPLAY_DPCM = 1;
	private final int AV_CODEC_ID_PCM_S16LE = 2;
	private final int AV_CODEC_ID_PCM_U8 = 3;

	private final int VIDEO_DELTA_FRAME = 1;

	// For each chunk of DPCM data in an Interplay MVE file, the first 2 bytes
	// comprise an initial predictor stored in a signed, 16-bit, little-endian
	// format.
	// The remainder of the bytes are indices into the delta table. For each byte,
	// fetch a signed delta and apply it to the appropriate predictor
	// Saturate the predictor to a signed 16-bit range after each delta is applied.
	private int interplay_dpcm_delta_table[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 47, 51, 56,
			61, 66, 72, 79, 86, 94, 102, 112, 122, 133, 145, 158, 173, 189, 206, 225, 245, 267, 292, 318, 348, 379, 414,
			452, 493, 538, 587, 640, 699, 763, 832, 908, 991, 1081, 1180, 1288, 1405, 1534, 1673, 1826, 1993, 2175,
			2373, 2590, 2826, 3084, 3365, 3672, 4008, 4373, 4772, 5208, 5683, 6202, 6767, 7385, 8059, 8794, 9597, 10472,
			11428, 12471, 13609, 14851, 16206, 17685, 19298, 21060, 22981, 25078, 27367, 29864, 32589, -29973, -26728,
			-23186, -19322, -15105, -10503, -5481, -1, 1, 1, 5481, 10503, 15105, 19322, 23186, 26728, 29973, -32589,
			-29864, -27367, -25078, -22981, -21060, -19298, -17685, -16206, -14851, -13609, -12471, -11428, -10472,
			-9597, -8794, -8059, -7385, -6767, -6202, -5683, -5208, -4772, -4373, -4008, -3672, -3365, -3084, -2826,
			-2590, -2373, -2175, -1993, -1826, -1673, -1534, -1405, -1288, -1180, -1081, -991, -908, -832, -763, -699,
			-640, -587, -538, -493, -452, -414, -379, -348, -318, -292, -267, -245, -225, -206, -189, -173, -158, -145,
			-133, -122, -112, -102, -94, -86, -79, -72, -66, -61, -56, -51, -47, -43, -42, -41, -40, -39, -38, -37, -36,
			-35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15,
			-14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1 };

	private int audio_type;
	private int audio_sample_rate;
	private int audio_flags;
	private int audio_channels;
	private int audio_bits;
	private int audio_frame_count;

	private int width, height;
	private int frame_pts_inc;
	private int video_bpp;
	private byte frame_format;
	private byte[] palette = new byte[768];
	private boolean hasPalette;
	private boolean send_buffer;

	private int audio_chunk_offset;
	private int audio_chunk_size;
	private int skip_map_chunk_offset;
	private short skip_map_chunk_size;
	private int decode_map_chunk_offset;
	private short decode_map_chunk_size;
	private int video_chunk_offset;
	private short video_chunk_size;
	private int next_chunk_offset;

	private FramePacket pkt = new FramePacket();
	private int video_pts;

	private int back_buf1;
	private int back_buf2;
	private byte[] buffer;
	private byte[] frame;

	private int max_block_offset;

	private Resource file;

	public MVEFile(Resource bb) {
		this.file = bb;
		byte[] data = new byte[20];
		bb.read(data);
		for (int i = 0; i < 20; i++)
			if (data[i] != signature[i])
				return;
		bb.read(data, 0, 6);
		for (int i = 0; i < 6; i++)
			if (data[i] != magic[i])
				return;

		next_chunk_offset = bb.position();
		/* process the first chunk which should be CHUNK_INIT_VIDEO */
		if (process_chunk(bb) != CHUNK_INIT_VIDEO)
			return; // AVERROR_INVALIDDATA

		/*
		 * peek ahead to the next chunk-- if it is an init audio chunk, process it; if
		 * it is the first video chunk, this is a silent file
		 */
		
		bb.seek(2, Whence.Current);
		int chunk_type = bb.readShort();
		if (chunk_type == CHUNK_VIDEO) {
			audio_type = 0; /* no audio */
		} else if (process_chunk(bb) != CHUNK_INIT_AUDIO)
			return; // AVERROR_INVALIDDATA;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean process() {
		return process_chunk(file) == CHUNK_DONE;
	}

	public byte[] getPalette() {
		if (hasPalette)
			return palette;

		return null;
	}

	public byte[] getFrame() {
		if(frame != null)
			Arrays.fill(frame, (byte) 0);
		if (pkt.video_chunk_data == null)
			return frame;

		if (!decode())
			return frame;

		System.arraycopy(buffer, back_buf1, frame, 0, frame.length);

		return frame;
	}

	public int getRate() {
		if (frame_pts_inc == 0)
			return 1000;

		return frame_pts_inc;
	}
	
	public void close()
	{
		file.close();
	}

	private int process_chunk(Resource bb) {
		/* see if there are any pending packets */
		int chunk_type = load_ipmovie_packet(bb);
		if (chunk_type != CHUNK_DONE)
			return chunk_type;

		int chunk_size = bb.readShort();
		chunk_type = bb.readShort();

		switch (chunk_type) {
		case CHUNK_INIT_AUDIO:
//			System.err.println("initialize audio");
			break;
		case CHUNK_AUDIO_ONLY:
//			System.err.println("audio only");
			break;
		case CHUNK_INIT_VIDEO:
//			System.err.println("initialize video");
			break;
		case CHUNK_VIDEO:
//			System.err.println("video (and audio)");
			break;
		case CHUNK_SHUTDOWN:
//			System.err.println("shutdown");
			break;
		case CHUNK_END:
//			System.err.println("end");
			break;
		default:
//			System.err.println("invalid chunk " + chunk_type);
			chunk_type = CHUNK_BAD;
			break;
		}

		while ((chunk_size > 0) && (chunk_type != CHUNK_BAD)) {
			int opcode_size = bb.readShort();
			int opcode_type = bb.readByte() & 0xFF;
			int opcode_version = bb.readByte() & 0xFF;

			chunk_size -= 4;
			chunk_size -= opcode_size;

			switch (opcode_type) {
			case OPCODE_END_OF_STREAM:
//				System.err.println("end of stream");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_END_OF_CHUNK:
//				System.err.println("end of chunk");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_CREATE_TIMER:
//				System.err.println("create timer");
				if ((opcode_version > 0) || (opcode_size != 6)) {
					System.err.println("bad create_timer opcode");
					chunk_type = CHUNK_BAD;
					break;
				}

				frame_pts_inc = bb.readInt(); // rate
				frame_pts_inc *= bb.readShort(); // subdivision
				break;
			case OPCODE_INIT_AUDIO_BUFFERS:
//				System.err.println("initialize audio buffers");
				if (opcode_version > 1 || opcode_size > 10 || opcode_size < 6) {
					System.err.println("bad init_audio_buffers opcode\n");
					chunk_type = CHUNK_BAD;
					break;
				}
				byte[] scratch = new byte[opcode_size];
				bb.read(scratch);

				audio_sample_rate = LittleEndian.getUShort(scratch, 4);
				audio_flags = LittleEndian.getUShort(scratch, 2);
				/* bit 0 of the flags: 0 = mono, 1 = stereo */
				audio_channels = (audio_flags & 1) + 1;
				/* bit 1 of the flags: 0 = 8 bit, 1 = 16 bit */
				audio_bits = (((audio_flags >> 1) & 1) + 1) * 8;
				/* bit 2 indicates compressed audio in version 1 opcode */
				if ((opcode_version == 1) && (audio_flags & 0x4) != 0)
					audio_type = AV_CODEC_ID_INTERPLAY_DPCM;
				else if (audio_bits == 16)
					audio_type = AV_CODEC_ID_PCM_S16LE;
				else
					audio_type = AV_CODEC_ID_PCM_U8;

				break;
			case OPCODE_START_STOP_AUDIO:
//				System.err.println("start/stop audio");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_INIT_VIDEO_BUFFERS:
//				System.err.println("initialize video buffers");
				if ((opcode_version > 2) || (opcode_size > 8) || opcode_size < 4
						|| opcode_version == 2 && opcode_size < 8) {
					System.err.println("bad init_video_buffers opcode");
					chunk_type = CHUNK_BAD;
					break;
				}
				width = bb.readShort() * 8;
				height = bb.readShort() * 8;
				if (opcode_version == 2)
					bb.readShort();
				if (opcode_version < 2 || bb.readShort() == 0) {
					video_bpp = 8;
				} else {
					video_bpp = 16;
				}

				int size = width * height * ((video_bpp == 8) ? 1 : 2);

				buffer = new byte[2 * size]; // ByteBuffer.allocate(2 * size).order(ByteOrder.LITTLE_ENDIAN);
				back_buf1 = 0;
				back_buf2 = size;
				max_block_offset = (height - 7) * width - 8;
				frame = new byte[size];

//				System.err.println("video resolution: " + width + " x " + height);
				break;
			case OPCODE_INIT_VIDEO_MODE:
//				System.err.println("initialize video mode");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_SEND_BUFFER:
//				System.err.println("send buffer");
				bb.seek(opcode_size, Whence.Current);
				send_buffer = true;
				break;
			case OPCODE_AUDIO_FRAME:
//				System.err.println("audio frame\n");
				/* log position and move on for now */
				audio_chunk_offset = bb.position();
				audio_chunk_size = opcode_size;
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_SILENCE_FRAME:
//				System.err.println("silence frame");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_CREATE_GRADIENT:
//				System.err.println("create gradient");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_SET_PALETTE_COMPRESSED:
//				System.err.println("set palette compressed");
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_SET_SKIP_MAP:
//				System.err.println("set skip map");
				/* log position and move on for now */
				skip_map_chunk_offset = bb.position();
				skip_map_chunk_size = (short) opcode_size;
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_SET_DECODING_MAP:
//				System.err.println("set decoding map");
				/* log position and move on for now */
				decode_map_chunk_offset = bb.position();
				decode_map_chunk_size = (short) opcode_size;
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_VIDEO_DATA_06:
//				System.err.println("set video data format 0x06");
				frame_format = 0x06;

				/* log position and move on for now */
				video_chunk_offset = bb.position();
				video_chunk_size = (short) opcode_size;
				bb.seek(opcode_size, Whence.Current);
				break;

			case OPCODE_VIDEO_DATA_10:
//	        	System.err.println("set video data format 0x10");
				frame_format = 0x10;

				/* log position and move on for now */
				video_chunk_offset = bb.position();
				video_chunk_size = (short) opcode_size;
				bb.seek(opcode_size, Whence.Current);
				break;

			case OPCODE_VIDEO_DATA_11:
//	        	System.err.println("set video data format 0x11");
				frame_format = 0x11;

				/* log position and move on for now */
				video_chunk_offset = bb.position();
				video_chunk_size = (short) opcode_size;
				bb.seek(opcode_size, Whence.Current);
				break;
			case OPCODE_SET_PALETTE:
//				System.err.println("set palette");
				if (opcode_size > 0x304 || opcode_size < 4) {
					System.err.println("demux_ipmovie: set_palette opcode with invalid size");
					chunk_type = CHUNK_BAD;
					break;
				}
				/* load the palette into internal data structure */
				int first_color = bb.readShort() & 0xFF;
				int last_color = first_color + (bb.readShort() & 0xFF) - 1;
				/* sanity check (since they are 16 bit values) */
				if ((first_color > 0xFF) || (last_color > 0xFF)
						|| (last_color - first_color + 1) * 3 + 4 > opcode_size) {
					System.err.println("demux_ipmovie: set_palette indexes out of range (" + first_color + " . "
							+ last_color + ")");
					chunk_type = CHUNK_BAD;
					break;
				}

				for (int i = first_color; i <= last_color; i++) {
					palette[3 * i + 0] = (byte) (bb.readByte() * 4);
					palette[3 * i + 1] = (byte) (bb.readByte() * 4);
					palette[3 * i + 2] = (byte) (bb.readByte() * 4);
				}

				hasPalette = true;
				break;

			case OPCODE_UNKNOWN_12:
			case OPCODE_UNKNOWN_13:
			case OPCODE_UNKNOWN_14:
			case OPCODE_UNKNOWN_15:
//				System.err.println("unknown (but documented) opcode 0x" + Integer.toHexString(opcode_type));
				bb.seek(opcode_size, Whence.Current);
				break;
			default:
//		        System.err.println("*** unknown opcode type 0x" + Integer.toHexString(chunk_type));
				chunk_type = CHUNK_BAD;
				break;
			}
		}

		/* make a note of where the stream is sitting */
		next_chunk_offset = bb.position();

		/* dispatch the first of any pending packets */
		if ((chunk_type == CHUNK_VIDEO) || (chunk_type == CHUNK_AUDIO_ONLY))
			chunk_type = load_ipmovie_packet(bb);

		return chunk_type;
	}

	private int load_ipmovie_packet(Resource bb) {
		int chunk_type;
		if (audio_chunk_offset != 0 && audio_channels != 0 && audio_bits != 0) {
			if (audio_type == AV_CODEC_ID_NONE) {
				System.err.println("Can not read audio packet before audio codec is known");
				return CHUNK_BAD;
			}

			/* adjust for PCM audio by skipping chunk header */
			if (audio_type != AV_CODEC_ID_INTERPLAY_DPCM) {
				audio_chunk_offset += 6;
				audio_chunk_size -= 6;
			}

			bb.seek(audio_chunk_offset, Whence.Set);
			audio_chunk_offset = 0;

			pkt.audio_chunk_data = new byte[audio_chunk_size];
			if (bb.position() + audio_chunk_size >= bb.size())
				return CHUNK_EOF;
			bb.read(pkt.audio_chunk_data);

//		    pkt.stream_index = audio_stream_index;
			pkt.pts = audio_frame_count;

			/* audio frame maintenance */
			if (audio_type != AV_CODEC_ID_INTERPLAY_DPCM)
				audio_frame_count += (audio_chunk_size / audio_channels / (audio_bits / 8));
			else
				audio_frame_count += (audio_chunk_size - 6 - audio_channels) / audio_channels;

//			System.err.println("sending audio frame with pts " + pkt.pts + " (" + audio_frame_count + " audio frames)");
			chunk_type = CHUNK_VIDEO;

		} else if (frame_format != 0) {
			/*
			 * send the frame format, decode map, the video data, skip map, and the
			 * send_buffer flag together
			 */
			pkt.video_chunk_data = new byte[video_chunk_size];
			pkt.decode_map_chunk_data = new byte[decode_map_chunk_size];
			pkt.skip_map_chunk_data = new byte[skip_map_chunk_size];

			if (hasPalette) {
				pkt.palette = new byte[768];
				System.arraycopy(palette, 0, pkt.palette, 0, 768);
				hasPalette = false;
			}
			pkt.frame_format = frame_format;
			pkt.send_buffer = send_buffer;
			pkt.video_chunk_size = video_chunk_size;
			pkt.decode_map_chunk_size = decode_map_chunk_size;
			pkt.skip_map_chunk_size = skip_map_chunk_size;

			frame_format = 0;
			send_buffer = false;

			pkt.pos = video_chunk_offset;
			bb.seek(video_chunk_offset, Whence.Set);
			video_chunk_offset = 0;

			if (bb.position() + video_chunk_size >= bb.size())
				return CHUNK_EOF;
			bb.read(pkt.video_chunk_data);

			if (decode_map_chunk_size != 0) {
				pkt.pos = decode_map_chunk_offset;
				bb.seek(decode_map_chunk_offset, Whence.Set);
				decode_map_chunk_offset = 0;

				if (bb.position() + decode_map_chunk_size >= bb.size())
					return CHUNK_EOF;
				bb.read(pkt.decode_map_chunk_data);
			}

			if (skip_map_chunk_size != 0) {
				pkt.pos = skip_map_chunk_offset;
				bb.seek(skip_map_chunk_offset, Whence.Set);
				skip_map_chunk_offset = 0;

				if (bb.position() + skip_map_chunk_size >= bb.size())
					return CHUNK_EOF;
				bb.read(pkt.skip_map_chunk_data);
			}

			video_chunk_size = 0;
			decode_map_chunk_size = 0;
			skip_map_chunk_size = 0;

//		    pkt.stream_index = video_stream_index;
			pkt.pts = video_pts;
//			System.err.println("sending video frame with pts " + pkt.pts);

			video_pts += frame_pts_inc;

			chunk_type = CHUNK_VIDEO;
		} else {
			bb.seek(next_chunk_offset, Whence.Set);
			chunk_type = CHUNK_DONE;
		}

		return chunk_type;
	}

	private boolean decode() {
		ByteBuffer bb = ByteBuffer.wrap(pkt.video_chunk_data).order(ByteOrder.LITTLE_ENDIAN);

		/* short cur_frame = */bb.getShort();
		/* short last_frame = */bb.getShort();
		/* short x_offset = */bb.getShort();
		/* short y_offset = */bb.getShort();
		/* short x_size = */bb.getShort();
		/* short y_size = */bb.getShort();
		int flags = bb.getShort() & 0xFFFF;

		if ((flags & VIDEO_DELTA_FRAME) != 0) {
			int temp = back_buf1;
			back_buf1 = back_buf2;
			back_buf2 = temp;
		}

		boolean dec = false;
		if (video_bpp == 16) {
//		    dec = ipvideo_decode_frame16 (s, data, len);
		} else {
			if (palette == null) {
				System.err.println("No palette available");
				return false;
			}
			dec = decode_frame8(bb, pkt.video_chunk_size);
		}

		return dec;

	}

	private boolean decode_frame8(ByteBuffer data, int len) {
		boolean rc = true;
		int index = 0;
		int opcode;

		/* decoding is done in 8x8 blocks */
		int xx = width >> 3;
		int yy = height >> 3;

		int frameptr = back_buf1;

		for (int y = 0; y < yy; ++y) {
			for (int x = 0; x < xx; ++x) {
				/* decoding map contains 4 bits of information per 8x8 block */
				/* bottom nibble first, then top nibble */

				if ((index & 1) != 0)
					opcode = (pkt.decode_map_chunk_data[index >> 1] & 0xFF) >> 4;
				else
					opcode = pkt.decode_map_chunk_data[index >> 1] & 0x0F;
				++index;

				switch (opcode) {
				case 0x00:
					/* copy a block from the previous frame */
					rc = copy_block(frameptr, frameptr + (back_buf2 - back_buf1), 0);
					break;
				case 0x01:
					/*
					 * copy block from 2 frames ago; since we switched the back buffers we don't
					 * actually have to do anything here
					 */
					break;
				case 0x02:
					rc = decode_0x2(frameptr, data);
					break;
				case 0x03:
					rc = decode_0x3(frameptr, data);
					break;
				case 0x04:
					rc = decode_0x4(frameptr, data);
					break;
				case 0x05:
					rc = decode_0x5(frameptr, data);
					break;
				case 0x06:
					/* mystery opcode? skip multiple blocks? */
					rc = false;
					break;
				case 0x07:
					rc = decode_0x7(frameptr, data);
					break;
				case 0x08:
					rc = decode_0x8(frameptr, data);
					break;
				case 0x09:
					rc = decode_0x9(frameptr, data);
					break;
				case 0x0a:
					rc = decode_0xa(frameptr, data);
					break;
				case 0x0b:
					rc = decode_0xb(frameptr, data);
					break;
				case 0x0c:
					rc = decode_0xc(frameptr, data);
					break;
				case 0x0d:
					rc = decode_0xd(frameptr, data);
					break;
				case 0x0e:
					rc = decode_0xe(frameptr, data);
					break;
				case 0x0f:
					rc = decode_0xf(frameptr, data);
					break;
				}

				if (!rc)
					return false;

				frameptr += 8;
			}

			frameptr += 7 * width;
		}

		return true;
	}

	/* copy an 8x8 block from the stream to the frame buffer */
	private boolean copy_block(int frameptr, int src_offset, int offset) {
		long frame_offset = frameptr - back_buf1 + offset;

		if (frame_offset < 0) {
			System.err.println("frame offset < 0 (" + frame_offset + ")");
			return false;
		} else if (frame_offset > max_block_offset) {
			System.err.println("frame offset above limit (" + frame_offset + " > " + max_block_offset + ")");
			return false;
		}

		for (int i = 0; i < 8; ++i) {
			System.arraycopy(buffer, src_offset, buffer, frameptr, 8);
			frameptr += width;
			src_offset += width;
		}

		return true;
	}

	private boolean decode_0x2(int frame, ByteBuffer data) {
		if (!data.hasRemaining())
			return false;

		int x, y, B = data.get() & 0xFF;

		if (B < 56) {
			x = 8 + (B % 7);
			y = B / 7;
		} else {
			x = -14 + ((B - 56) % 29);
			y = 8 + ((B - 56) / 29);
		}
		int offset = y * width + x;

		return copy_block(frame, frame + offset, offset);
	}

	private boolean decode_0x3(int frame, ByteBuffer data) {
		if (!data.hasRemaining())
			return false;

		int x, y, B = data.get() & 0xFF;

		if (B < 56) {
			x = -(8 + (B % 7));
			y = -(B / 7);
		} else {
			x = -(-14 + ((B - 56) % 29));
			y = -(8 + ((B - 56) / 29));
		}

		int offset = y * width + x;

		return copy_block(frame, frame + offset, offset);
	}

	private boolean decode_0x4(int frame, ByteBuffer data) {
		if (!data.hasRemaining())
			return false;

		int B = data.get() & 0xFF;

		int x = -8 + (B & 0x0F);
		int y = -8 + ((B >> 4) & 0x0F);

		int offset = y * width + x;

		return copy_block(frame, frame + (back_buf2 - back_buf1) + offset, offset);
	}

	private boolean decode_0x5(int frame, ByteBuffer data) {
		if (data.remaining() < 2)
			return false;

		byte x = data.get();
		byte y = data.get();

		int offset = y * width + x;

		return copy_block(frame, frame + (back_buf2 - back_buf1) + offset, offset);
	}

	private boolean decode_0x7(int frame, ByteBuffer data) {
		if (data.remaining() < 2 + 2)
			return false;

		int x, y;
		byte[] P = new byte[2];
		int flags;

		/* 2-color encoding */
		data.get(P);

		if ((P[0] & 0xFF) <= (P[1] & 0xFF)) {
			/* need 8 more bytes from the stream */
			for (y = 0; y < 8; y++) {
				flags = (data.get() & 0xFF) | 0x100;
				for (; flags != 1; flags >>= 1)
					buffer[frame++] = P[flags & 1];
				frame += width - 8;
			}

		} else {
			/* need 2 more bytes from the stream */
			flags = data.getShort();
			for (y = 0; y < 8; y += 2) {
				for (x = 0; x < 8; x += 2, flags >>= 1) {
					buffer[frame + x] = buffer[frame + x
							+ 1] = buffer[frame + x + width] = buffer[frame + x + 1 + width] = P[flags & 1];
				}
				frame += width * 2;
			}
		}

		return true;
	}

	private boolean decode_0x8(int frame, ByteBuffer data) {
		int x, y;
		byte[] P = new byte[8];
		int flags = 0;

		if (data.remaining() < 12)
			return false;

		/*
		 * 2-color encoding for each 4x4 quadrant, or 2-color encoding on either top and
		 * bottom or left and right halves
		 */
		data.get(P, 0, 2);

		if ((P[0] & 0xFF) <= (P[1] & 0xFF)) {
			for (y = 0; y < 16; y++) {
				// new values for each 4x4 block
				if ((y & 3) == 0) {
					if (y != 0) {
						data.get(P, 0, 2);
					}
					flags = data.getShort();
				}
				for (x = 0; x < 4; x++, flags >>= 1)
					buffer[frame++] = P[flags & 1];
				frame += width - 4;
				// switch to right half
				if (y == 7)
					frame -= 8 * width - 4;
			}
		} else {
			flags = data.getInt();
			data.get(P, 2, 2);

			if ((P[2] & 0xFF) <= (P[3] & 0xFF)) {
				/* vertical split; left & right halves are 2-color encoded */
				for (y = 0; y < 16; y++) {
					for (x = 0; x < 4; x++, flags >>= 1)
						buffer[frame++] = P[flags & 1];
					frame += width - 4;
					// switch to right half
					if (y == 7) {
						frame -= 8 * width - 4;
						P[0] = P[2];
						P[1] = P[3];
						flags = data.getInt();
					}
				}
			} else {
				/* horizontal split; top & bottom halves are 2-color encoded */
				for (y = 0; y < 8; y++) {
					if (y == 4) {
						P[0] = P[2];
						P[1] = P[3];
						flags = data.getInt();
					}

					for (x = 0; x < 8; x++, flags >>= 1)
						buffer[frame++] = P[flags & 1];
					frame += width - 8;
				}
			}
		}

		return true;
	}

	private boolean decode_0x9(int frame, ByteBuffer data) {
		if (data.remaining() < 4 + 4)
			return false;

		byte[] P = new byte[4];

		/* 4-color encoding */
		data.get(P);

		int x, y;
		if ((P[0] & 0xFF) <= (P[1] & 0xFF)) {
			if ((P[2] & 0xFF) <= (P[3] & 0xFF)) {

				/* 1 of 4 colors for each pixel, need 16 more bytes */
				for (y = 0; y < 8; y++) {
					/* get the next set of 8 2-bit flags */
					int flags = data.getShort();
					for (x = 0; x < 8; x++, flags >>= 2)
						buffer[frame++] = P[flags & 0x03];
					frame += width - 8;
				}

			} else {
				/* 1 of 4 colors for each 2x2 block, need 4 more bytes */
				int flags = data.getInt();

				for (y = 0; y < 8; y += 2) {
					for (x = 0; x < 8; x += 2, flags >>= 2) {
						buffer[frame + x] = buffer[frame + x
								+ 1] = buffer[frame + x + width] = buffer[frame + x + 1 + width] = P[flags & 0x03];
					}
					frame += width * 2;
				}

			}
		} else {
			/* 1 of 4 colors for each 2x1 or 1x2 block, need 8 more bytes */
			long flags = data.getLong();
			if ((P[2] & 0xFF) <= (P[3] & 0xFF)) {
				for (y = 0; y < 8; y++) {
					for (x = 0; x < 8; x += 2, flags >>= 2) {
						buffer[frame + x] = buffer[frame + x + 1] = P[(int) (flags & 0x03)];
					}
					frame += width;
				}
			} else {
				for (y = 0; y < 8; y += 2) {
					for (x = 0; x < 8; x++, flags >>= 2) {
						buffer[frame + x] = buffer[frame + x + width] = P[(int) (flags & 0x03)];
					}
					frame += width * 2;
				}
			}
		}
		return true;
	}

	private boolean decode_0xa(int frame, ByteBuffer data) {
		byte[] P = new byte[8];
		int x, y;

		if (data.remaining() < 16)
			return false;

		data.get(P, 0, 4);

		if ((P[0] & 0xFF) <= (P[1] & 0xFF)) {
			int flags = 0;

			/* 4-color encoding for each quadrant; need 32 bytes */
			for (y = 0; y < 16; y++) {
				// new values for each 4x4 block
				if ((y & 3) == 0) {
					if (y != 0)
						data.get(P, 0, 4);
					flags = data.getInt();
				}

				for (x = 0; x < 4; x++, flags >>= 2)
					buffer[frame++] = P[flags & 0x03];

				frame += width - 4;
				// switch to right half
				if (y == 7)
					frame -= 8 * width - 4;
			}

		} else {
			// vertical split?
			long flags = data.getLong();

			data.get(P, 4, 4);
			boolean vert = (P[4] & 0xFF) <= (P[5] & 0xFF);

			/*
			 * 4-color encoding for either left and right or top and bottom halves
			 */

			for (y = 0; y < 16; y++) {
				for (x = 0; x < 4; x++, flags >>= 2)
					buffer[frame++] = P[(int) (flags & 0x03)];

				if (vert) {
					frame += width - 4;
					// switch to right half
					if (y == 7)
						frame -= 8 * width - 4;
				} else if ((y & 1) != 0)
					frame += width - 8;

				// load values for second half
				if (y == 7) {
					System.arraycopy(P, 4, P, 0, 4);
					flags = data.getLong();
				}
			}
		}
		return true;
	}

	private boolean decode_0xb(int frame, ByteBuffer data) {
		/* 64-color encoding (each pixel in block is a different color) */
		if (data.remaining() < 64)
			return false;

		for (int y = 0; y < 8; ++y) {
			data.get(buffer, frame, 8);
			frame += width;
		}
		return true;
	}

	private boolean decode_0xc(int frame, ByteBuffer data) {
		/* 16-color block encoding: each 2x2 block is a different color */
		if (data.remaining() < 16)
			return false;

		for (int y = 0, x; y < 8; y += 2) {
			for (x = 0; x < 8; x += 2) {
				buffer[frame + x] = buffer[frame + x
						+ 1] = buffer[frame + width + x] = buffer[frame + width + x + 1] = data.get();
			}
			frame += width * 2;
		}

		return true;
	}

	private boolean decode_0xd(int frame, ByteBuffer data) {
		if (data.remaining() < 4)
			return false;

		byte[] P = new byte[2];
		for (int y = 0; y < 8; ++y) {
			if ((y & 3) == 0) {
				data.get(P);
			}

			Arrays.fill(buffer, frame, frame + 4, P[0]);
			Arrays.fill(buffer, frame + 4, frame + 8, P[1]);
			frame += width;
		}

		return true;
	}

	private boolean decode_0xe(int frame, ByteBuffer data) {
		if (data.remaining() < 1)
			return false;

		byte pix = data.get();
		for (int y = 0; y < 8; ++y) {
			Arrays.fill(buffer, frame, frame + 8, pix);
			frame += width;
		}

		return true;
	}

	private boolean decode_0xf(int frame, ByteBuffer data) {
		if (data.remaining() < 2)
			return false;

		byte[] P = new byte[2];
		data.get(P);

		for (int y = 0, x; y < 8; ++y) {
			for (x = 0; x < 4; ++x) {
				buffer[frame++] = P[y & 1];
				buffer[frame++] = P[(y & 1) ^ 1];

			}
			frame += width - 8;
		}

		return true;
	}
}
