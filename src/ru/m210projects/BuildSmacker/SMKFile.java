// This file is part of BuildSmacker.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildSmacker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildSmacker is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildSmacker.  If not, see <http://www.gnu.org/licenses/>.
//
// Copyright (C) 2012-2013 Greg Kennedy

package ru.m210projects.BuildSmacker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.BuildSmacker.Header.AudioBits;
import ru.m210projects.BuildSmacker.Header.AudioChannels;
import ru.m210projects.BuildSmacker.Header.AudioCompression;
import ru.m210projects.BuildSmacker.Header.Signature;
import ru.m210projects.BuildSmacker.HuffmanTree.Node;

public class SMKFile {

	private static final byte palmap[] = { 0x00, 0x04, 0x08, 0x0C, 0x10, 0x14, 0x18, 0x1C, 0x20, 0x24, 0x28, 0x2C, 0x30,
			0x34, 0x38, 0x3C, 0x41, 0x45, 0x49, 0x4D, 0x51, 0x55, 0x59, 0x5D, 0x61, 0x65, 0x69, 0x6D, 0x71, 0x75, 0x79,
			0x7D, (byte) 0x82, (byte) 0x86, (byte) 0x8A, (byte) 0x8E, (byte) 0x92, (byte) 0x96, (byte) 0x9A,
			(byte) 0x9E, (byte) 0xA2, (byte) 0xA6, (byte) 0xAA, (byte) 0xAE, (byte) 0xB2, (byte) 0xB6, (byte) 0xBA,
			(byte) 0xBE, (byte) 0xC3, (byte) 0xC7, (byte) 0xCB, (byte) 0xCF, (byte) 0xD3, (byte) 0xD7, (byte) 0xDB,
			(byte) 0xDF, (byte) 0xE3, (byte) 0xE7, (byte) 0xEB, (byte) 0xEF, (byte) 0xF3, (byte) 0xF7, (byte) 0xFB,
			(byte) 0xFF };

	private final static short[] block_runs = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
			21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
			48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 128, 256, 512, 1024, 2048 };

	private static final int TREE_MMAP = 0;
	private static final int TREE_MCLR = 1;
	private static final int TREE_FULL = 2;
	private static final int TREE_TYPE = 3;

	private static final int BLK_MONO = 0;
	private static final int BLK_FULL = 1;
	private static final int BLK_SKIP = 2;
	private static final int BLK_FILL = 3;

	private Header header;
	private Frame[] frames;
	private HuffmanTree tree[];
	private Node audTree[];

	private int currentFrame;
	private byte[] oldPalette;
	private byte[] currentPalette;
	private SMKAudio[] audio;
	private ByteBuffer frameBuffer;
	private BitBuffer bit;
	private boolean videoEnable, audioEnable[] = new boolean[7];

	public enum Track {
		Video(0x80), Audio(0x01), All(0xFF);

		private int mask;

		Track(int mask) {
			this.mask = mask;
		}

		public int mask() {
			return mask;
		}

		protected void setMask(int mask) {
			this.mask = mask;
		}
	}

	public SMKFile(ByteBuffer fp) throws Exception {
		header = new Header(fp);
		audio = new SMKAudio[7];

		int mask = 0;
		for (int i = 0; i < 7; i++) {
			if (header.isAudioExists(i)) {
				mask |= 0x01 << i;
			}
		}
		Track.Audio.setMask(mask);

		frames = new Frame[getFrames()];
		for (int i = 0; i < getFrames(); i++) {
			frames[i] = new Frame(fp.getInt() & ~(1 | 2)); // bit1 - keyframe, bit2 - unknown
		}
		for (int i = 0; i < getFrames(); i++) {
			frames[i].flags = fp.get();
		}

		bit = new BitBuffer();

		int pos = fp.position();
		bit.wrap(fp);

		tree = new HuffmanTree[4];
		audTree = new Node[4];
		for (int i = 0; i < 4; i++) {
			tree[i] = new HuffmanTree(bit) {
				@Override
				protected void message(String message) {
					SMKFile.this.message(message);
				}
			};
		}
		fp.position(pos + header.TreesSize);

		for (int i = 0; i < getFrames(); i++) {
			byte[] data = new byte[frames[i].size];
			fp.get(data);
			frames[i].buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		}
		currentFrame = -1;
	}

	public int setFrame(int frame) {
		if (frame >= getFrames()) {
			return 2;
		}

		int frameSize = frames[frame].size, tr;
		if (frameSize == 0) {
			message("setFrame() - Warning: frame " + frame + ": frameSize is 0");
			return -1;
		}

		if (frame == currentFrame) {
			return frames[currentFrame].flags & 1;
		}

		currentFrame = frame;
		frames[currentFrame].buf.rewind();

		if (!decodePalette(currentFrame, videoEnable)) {
			message("setFrame() - Error: frame " + currentFrame + ": insufficient data for a palette rec");
			return -1;
		}

		if ((tr = decodeAudio(currentFrame)) != -1) {
			message("setFrame() - Error: frame " + currentFrame + ": insufficient data for audio[" + tr + "] rec");
			return -1;
		}

		if (videoEnable) {
			decodeVideo(currentFrame);
		}

		return frames[currentFrame].flags & 1;
	}

	public byte[] getPalette() {
		if (currentPalette == null) {
			int palframe = -1;
			for (int i = 0; i < getFrames(); i++) {
				if ((frames[i].flags & 1) != 0) {
					palframe = i;
					frames[i].buf.rewind();
					break;
				}
			}
			decodePalette(palframe, true);
		}
		return currentPalette;
	}

	public int getWidth() {
		return header.Width;
	}

	public int getHeight() {
		return header.Height;
	}

	public int getFrames() {
		return header.Frames;
	}

	public Signature getSignature() {
		return header.getSignature();
	}

	public SMKAudio getAudio(int num) {
		if (audio[num] == null && isAudioExists(num)) {
			audio[num] = new SMKAudio(header.getAudioRate(num), header.getAudioChannels(num), header.getAudioBits(num),
					header.AudioSize[num]);
		}

		return audio[num];
	}

	public boolean isAudioExists(int num) {
		return audioEnable[num] && header.isAudioExists(num);
	}

	public void setEnable(Track opt, int mask) {
		switch (opt) {
		case Video:
			videoEnable = (mask & 0x80) != 0;
			break;
		case Audio:
			for (int i = 0; i < 7; i++) {
				audioEnable[i] = (mask & (0x01 << i)) != 0;
			}
			break;
		case All:
			videoEnable = (mask & 0x80) != 0;
			for (int i = 0; i < 7; i++) {
				audioEnable[i] = (mask & (0x01 << i)) != 0;
			}
			break;
		}
	}

	public int getFrame() {
		return currentFrame;
	}

	public int getRate() {
		return header.FrameRate;
	}

	public ByteBuffer getVideoBuffer() {
		return frameBuffer;
	}

	public ByteBuffer getAudioBuffer(int num) {
		int totalsize = 0;
		for (int fr = 0; fr < getFrames(); fr++) {
			ByteBuffer data = frames[fr].buf;
			data.rewind();
			int flags = frames[fr].flags;
			if ((flags & 1) != 0) {
				int pos = data.position();
				int size = 4 * (data.get() & 0xFF);
				data.position(pos + size);
			}

			for (int track = 0; track < 7; track++) {
				if ((flags & (0x02 << track)) != 0) {
					int pos = data.position();
					int size = data.getInt();

					if (audioEnable[track]) {
						if (header.getAudioCompressionType(track) == AudioCompression.PCM) {
							totalsize += size;
						} else {
							totalsize += data.getInt();
						}
					}
					data.position(pos + size);
				}
			}
		}

		if (totalsize == 0) {
			return null;
		}

		ByteBuffer bb = ByteBuffer.allocateDirect(totalsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for (int fr = 0; fr < getFrames(); fr++) {
			ByteBuffer data = frames[fr].buf;
			data.rewind();
			decodePalette(fr, false);
			decodeAudio(fr);
			if ((frames[fr].flags & (0x02 << num)) != 0) {
				SMKAudio aud = audio[num];
				if (aud != null) {
					bb.put(aud.buffer, 0, aud.size);
				}
			}
		}
		bb.rewind();
		return bb;
	}

	private boolean decodePalette(int frame, boolean enabled) {
		ByteBuffer data = frames[frame].buf;
		if ((frames[frame].flags & 1) != 0) {
			int pos = data.position();
			int size = 4 * (data.get() & 0xFF);
			if (size > data.remaining()) {
				return false;
			}
			if (enabled) {
				decodePalette(data);
			}
			data.position(pos + size);
		}
		return true;
	}

	private void decodePalette(ByteBuffer p) {
		if (currentPalette == null) {
			oldPalette = new byte[768];
			currentPalette = new byte[768];
		}
		System.arraycopy(currentPalette, 0, oldPalette, 0, 768);

		int sz = 0;
		while (sz < 256) {
			if ((p.get(p.position()) & 0x80) != 0) {
				sz += (p.get() & 0x7F) + 1;
			} else if ((p.get(p.position()) & 0x40) != 0) {
				int j = ((p.get() & 0x3F) + 1);
				int off = (p.get() & 0xFF) * 3;
				while (j-- != 0 && sz < 256) {
					System.arraycopy(oldPalette, off, currentPalette, 3 * sz, 3);
					off += 3;
					sz++;
				}
			} else {
				for (int c = 0; c < 3; c++) {
					currentPalette[3 * sz + c] = palmap[p.get() & 0x3F];
				}
				sz++;
			}
		}
	}

	private int decodeAudio(int frame) {
		ByteBuffer data = frames[frame].buf;

		for (int track = 0; track < 7; track++) {
			if ((frames[frame].flags & (0x02 << track)) != 0) {
				int pos = data.position();
				int size = data.getInt();
				if (size == 0 || size > data.remaining()) {
					return track;
				}

				if (audioEnable[track]) {
					decodeAudio(data, track);
				}
				data.position(pos + size);
			}
		}
		return -1;
	}

	private int decodeAudio(ByteBuffer data, int nTrack) {
		Arrays.fill(audTree, null);
		SMKAudio aud = getAudio(nTrack);

		switch (header.getAudioCompressionType(nTrack)) {
		case PCM:
			aud.size = data.capacity();
			data.get(aud.buffer);
			break;
		case DPCM:
			if (data.remaining() < 4) {
				message("audio() - Error: need 4 bytes to get unpacked output buffer size");
				return -1;
			}

			aud.size = data.getInt();
			bit.wrap(data);

			if (bit.getBit() == 0) {
				message("audio() - Error: initial getBit returned 0");
				return -1;
			}

			AudioChannels channels = aud.getChannels();

			if ((channels.get() - 1) != bit.getBit()) {
				message("audio() - Error: mono/stereo mismatch");
				return -1;
			}

			AudioBits bits = aud.getBits();

			if (bits.get() != (bit.getBit() == 1 ? 16 : 8)) {
				message("audio() - Error: 8-/16-bit mismatch");
				return -1;
			}

			int j = 1;
			int k = 1;
			try {
				audTree[0] = new Node(bit);
				if (bits == AudioBits.aud16bit) {
					audTree[1] = new Node(bit);
					k = 2;
				}
				if (channels == AudioChannels.Stereo) {
					audTree[2] = new Node(bit);
					j = 2;
					k = 2;
					if (bits == AudioBits.aud16bit) {
						audTree[3] = new Node(bit);
						k = 4;
					}
				}
			} catch (Exception e) {
				message("audio() - Error: trees initialization failed!");
				return -1;
			}

			int unpack;
			if (channels == AudioChannels.Stereo) {
				unpack = bit.getByte();
				if (bits == AudioBits.aud16bit) {
					aud.buffer[1] = (byte) bit.getByte();
					aud.buffer[1] |= (unpack << 8);
				} else {
					aud.buffer[1] = (byte) unpack;
				}
			}

			unpack = bit.getByte();
			if (bits == AudioBits.aud16bit) {
				aud.buffer[0] = (byte) bit.getByte();
				aud.buffer[0] |= (unpack << 8);
			} else {
				aud.buffer[0] = (byte) unpack;
			}

			while (k < aud.size) {
				if (bits == AudioBits.aud8bit) {
					unpack = audTree[0].lookup(bit);
					aud.buffer[j] = (byte) (unpack + aud.buffer[j - channels.get()] & 0xFF);
					j++;
					k++;
				} else {
					unpack = audTree[0].lookup(bit);
					int unpack2 = audTree[1].lookup(bit);

					short value = (short) ((unpack | (unpack2 << 8)) + aud.buffer[j - channels.get()] & 0xFF);
					aud.buffer[j++] = (byte) (value & 0xFF);
					aud.buffer[j++] = (byte) ( ( value >>> 8 ) & 0xFF );
					k += 2;
				}

				if (channels == AudioChannels.Stereo) {
					if (bits == AudioBits.aud8bit) {
						unpack = audTree[2].lookup(bit);
						aud.buffer[j] = (byte) (unpack + aud.buffer[j - 2] & 0xFF);
						j++;
						k++;
					} else {
						unpack = audTree[2].lookup(bit);
						int unpack2 = audTree[3].lookup(bit);

						short value = (short) ((unpack | (unpack2 << 8)) + aud.buffer[j - 2] & 0xFF);
						aud.buffer[j++] = (byte) (value & 0xFF);
						aud.buffer[j++] = (byte) ( ( value >>> 8 ) & 0xFF );
						k += 2;
					}
				}
			}
			break;
		case Bink:
			// unsupported
			break;
		}

		return 0;
	}

	private boolean decodeVideo(int frame) {
		if (frameBuffer == null) {
			frameBuffer = ByteBuffer.wrap(new byte[getWidth() * getHeight()]).order(ByteOrder.LITTLE_ENDIAN);
		}

		bit.wrap(frames[frame].buf);

		for (int i = 0; i < 4; i++) {
			tree[i].reset();
		}

		frameBuffer.rewind();

		int blk = 0, mode;
		int bw = getWidth() >> 2;
		int bh = getHeight() >> 2;
		int blocks = bw * bh;
		int stride = getWidth();
		while (blk < blocks) {
			int type = tree[TREE_TYPE].getCode(bit);
			int run = block_runs[(type >> 2) & 0x3F];
			switch (type & 3) {
			case BLK_MONO:
				while (run-- != 0 && blk < blocks) {
					int clr = tree[TREE_MCLR].getCode(bit);
					int map = tree[TREE_MMAP].getCode(bit);
					int ptr = (blk / bw) * (stride * 4) + (blk % bw) * 4;

					byte hi = (byte) (clr >> 8);
					byte lo = (byte) (clr & 0xFF);
					int shift = 1;
					for (int i = 0; i < 4; i++) {
						for (int k = 0; k < 4; k++) {
							frameBuffer.put(ptr + k, ((map & shift) != 0) ? hi : lo);
							shift <<= 1;
						}
						ptr += stride;
					}
					blk++;
				}
				break;
			case BLK_FULL:
				mode = 0;
				if (header.getSignature() == Signature.SMK4) {
					if (bit.getBit() != 0) {
						mode = 1;
					} else if (bit.getBit() != 0) {
						mode = 2;
					}
				}

				while (run-- != 0 && blk < blocks) {
					int ptr = (blk / bw) * (stride * 4) + (blk % bw) * 4;
					switch (mode) {
					case 0:
						for (int i = 0; i < 4; i++) {
							frameBuffer.putInt(ptr,
									(tree[TREE_FULL].getCode(bit) << 16) | tree[TREE_FULL].getCode(bit));
							ptr += stride;
						}
						break;
					case 1:
						for (int k = 0; k < 2; k++) {
							int pix = tree[TREE_FULL].getCode(bit);
							for (int j = 0; j < 2; j++) {
								for (int i = 0; i < 4; i++) {
									frameBuffer.put(ptr + i, (i < 2) ? (byte) pix : (byte) (pix >> 8));
								}
								ptr += stride;
							}
						}
						break;
					case 2:
						for (int i = 0; i < 2; i++) {
							int col = (tree[TREE_FULL].getCode(bit) << 16) | tree[TREE_FULL].getCode(bit);
							for (int j = 0; j < 2; j++) {
								frameBuffer.putInt(ptr, col);
								ptr += stride;
							}
						}
						break;
					}
					blk++;
				}
				break;
			case BLK_SKIP:
				while (run-- != 0 && blk < blocks) {
					blk++;
				}
				break;
			case BLK_FILL:
				mode = type >> 8;
				while (run-- != 0 && blk < blocks) {
					int ptr = (blk / bw) * (stride * 4) + (blk % bw) * 4;
					int col = mode * 0x01010101;
					for (int i = 0; i < 4; i++) {
						frameBuffer.putInt(ptr, col);
						ptr += stride;
					}
					blk++;
				}
				break;
			}
		}

		return true;
	}

	protected void message(String message) {
		System.err.println(message);
	}
}
