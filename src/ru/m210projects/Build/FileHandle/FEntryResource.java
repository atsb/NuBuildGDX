package ru.m210projects.Build.FileHandle;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Architecture.BuildGdx;

public class FEntryResource implements Resource {

	protected FileEntry file;
	protected Resource res;
	
	public FEntryResource(FileEntry file)
	{
		this.file = file;
		this.res = null;
	}
	
	public boolean open()
	{
		if(res == null)
			res = BuildGdx.cache.open(file.getPath(), 0);
		return res != null;
	}
	
	@Override
	public String getExtension() {
		return file.getExtension();
	}

	@Override
	public Group getParent() {
		return null;
	}

	@Override
	public void close() {
		if(res != null)
			res.close();
		res = null;
	}

	@Override
	public boolean isClosed() {
		return res == null;
	}

	@Override
	public int seek(long offset, Whence whence) {
		if(isClosed()) open();
		return res.seek(offset, whence);
	}

	@Override
	public int read(byte[] buf, int offset, int len) {
		if(isClosed()) open();
		return res.read(buf, offset, len);
	}

	@Override
	public int read(byte[] buf) {
		if(isClosed()) open();
		return res.read(buf);
	}

	@Override
	public int read(ByteBuffer bb, int offset, int len) {
		if(isClosed()) open();
		return res.read(bb, offset, len);
	}

	@Override
	public String readString(int len) {
		if(isClosed()) open();
		return res.readString(len);
	}

	@Override
	public Integer readInt() {
		if(isClosed()) open();
		return res.readInt();
	}

	@Override
	public Short readShort() {
		if(isClosed()) open();
		return res.readShort();
	}

	@Override
	public Byte readByte() {
		if(isClosed()) open();
		return res.readByte();
	}

	@Override
	public Boolean readBoolean() {
		if(isClosed()) open();
		return res.readBoolean();
	}

	@Override
	public Long readLong() {
		if(isClosed()) open();
		return res.readLong();
	}

	@Override
	public Float readFloat() {
		if(isClosed()) open();
		return res.readFloat();
	}

	@Override
	public int size() {
		if(isClosed()) open();
		return res.size();
	}

	@Override
	public int position() {
		if(isClosed()) open();
		return res.position();
	}

	@Override
	public int remaining() {
		if(isClosed()) open();
		return res.remaining();
	}

	@Override
	public boolean hasRemaining() {
		if(isClosed()) open();
		return res.hasRemaining();
	}

	@Override
	public void toMemory() {
		if(isClosed()) open();
		res.toMemory();
	}

	@Override
	public byte[] getBytes() {
		if(isClosed()) open();
		return res.getBytes();
	}

	@Override
	public String getFullName() {
		return file.getName();
	}

}
