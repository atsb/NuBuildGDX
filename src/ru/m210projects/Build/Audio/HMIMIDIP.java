// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Audio;

import java.nio.*;

public class HMIMIDIP {
	public static final int NOTE_OFF = 0x80;
	public static final int NOTE_ON = 0x90;
	public static final int POLY_PRESSURE = 0xA0;
	public static final int CONTROL_CHANGE = 0xB0;
	public static final int PROGRAM_CHANGE = 0xC0;
	public static final int CHANNEL_PRESSURE = 0xD0;
	public static final int PITCH_BEND = 0xE0;

	public static final int HMP_TRACK_COUNT_OFFSET = 0x30;
	public static final int HMP_DIVISION_OFFSET = 0x38;
	public static final int HMP_TRACK_OFFSET_0 = 0x308; // original HMP
	public static final int HMP_TRACK_OFFSET_1 = 0x388; // newer HMP

	public static byte[] hmpinit(byte[] hmp) {
		ByteBuffer bb = ByteBuffer.wrap(hmp);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		byte[] tmp = new byte[32];
		bb.get(tmp);

		int TRACK_OFFSET;
		String signature = new String(tmp, 0, 32);
		int index = signature.indexOf((char) 0);
		signature = signature.substring(0, index).toUpperCase();
		if (signature.equals("HMIMIDIP013195")) {
			TRACK_OFFSET = HMP_TRACK_OFFSET_1;
		} else if (signature.equals("HMIMIDIP")) {
			TRACK_OFFSET = HMP_TRACK_OFFSET_0;
		} else
			return null;

		int nChunks = bb.getInt(HMP_TRACK_COUNT_OFFSET);
		int nBPM = bb.getInt(HMP_DIVISION_OFFSET);

		ByteBuffer mid = ByteBuffer.allocate(hmp.length);
		mid.putInt(0x4d546864); // MThd
		mid.putInt(6); // header length
		mid.putShort((short) 1); // type
		mid.putShort((short) nChunks);
		mid.putShort((short) (nBPM / 2));

		bb.position(TRACK_OFFSET);

		for (int i = 0; i < nChunks; i++) {
			bb.getInt(); // Number of chunk
			int chunk_length = bb.getInt() - 12;
			bb.getInt(); // Number of track
			mid.put(new byte[] { 'M', 'T', 'r', 'k' });
			int pos = mid.position();
			mid.putInt(pos, ProcessTrack(bb, chunk_length, mid)); // chunk size
		}

		return mid.array();
	}

	private static int ProcessTrack(ByteBuffer hmp, int size, ByteBuffer mid) {
		int runningStatus = -1;
		byte[] bytes = { 0, 0, 0, 0 };

		mid.putInt(0); // reserving for chunk size
		int len = mid.position();
		for (int i = 0; i < size; i++) {
			// MIDI Delta (Little -> Big) Endian Byte
			int n1 = 0;
			for (; ((bytes[n1] = hmp.get()) & 0x80) == 0; n1++);
			
			for (int n2 = 0; n2 <= n1; n2++) {
				byte out = (byte) (bytes[n1 - n2] & 0x7F);
				if (n2 != n1)
					(out) |= 0x80;
				mid.put(out);
			}

			// MIDI Event
			int sbyte = hmp.get() & 0xFF;
			if (sbyte == 0)
				break;

			if (sbyte == 0xFF) // Meta Message
			{
				if (hmp.get() == 0x2F && hmp.get() == 0x00) {
					mid.put((byte) 0xFF);
					mid.put((byte) 0x2F);
					mid.put((byte) 0);
					return mid.position() - len; // end of track
				}
				break;
			} else {
				switch (sbyte & 0xF0) {
				case NOTE_OFF:
				case NOTE_ON:
				case POLY_PRESSURE:
				case CONTROL_CHANGE:
				case PITCH_BEND:
					if (sbyte != runningStatus)
						mid.put((byte) sbyte);

					byte d1 = hmp.get();
					byte d2 = hmp.get();

					if ((sbyte & 0xF0) == CONTROL_CHANGE) {
						if (d1 == 110 && d2 == -1) { // HMP Loop Start
							d1 = 116;
							d2 = 0; // XMI Loop Start
						}

						if (d1 == 111 /* && d2 == -128 */) { // HMP Loop End
							d1 = 117;
							d2 = 127; // XMI Loop End
						}
					}

					mid.put(d1);
					mid.put(d2);
					break;
				case PROGRAM_CHANGE:
				case CHANNEL_PRESSURE:
					if (sbyte != runningStatus)
						mid.put((byte) sbyte);
					mid.put(hmp.get());
					break;
				default:
					return mid.position() - len;
				}
				runningStatus = sbyte;
			}
		}
		return mid.position() - len;
	}
}
