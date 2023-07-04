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

package ru.m210projects.Build.Net;

import static ru.m210projects.Build.Net.Mmulti.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPSocket implements ISocket {

	public DatagramChannel sock;
	public int port;
	private final ByteBuffer buf = ByteBuffer.allocate(MAXPAKSIZ);
	
	public UDPSocket(int port) throws IOException
	{
		sock = DatagramChannel.open();
		sock.configureBlocking(false);
		sock.setOption(StandardSocketOptions.SO_RCVBUF, MAXPAKSIZ);
		sock.setOption(StandardSocketOptions.SO_SNDBUF, MAXPAKSIZ);
		this.port = port;
	}
	
	@Override
	public InetSocketAddress recvfrom(byte[] dabuf, int bufsiz) {
		try {
			buf.clear();
			InetSocketAddress recieve = (InetSocketAddress) sock.receive(buf);
			if(recieve != null) {
				buf.rewind();
				buf.get(dabuf, 0, bufsiz);
				return recieve;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void sendto(Object address, byte[] dabuf, int bufsiz) {
		buf.clear();
		buf.put(dabuf, 0, bufsiz);
		buf.flip();
		try {
			sock.send(buf, (InetSocketAddress) address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		try {
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
