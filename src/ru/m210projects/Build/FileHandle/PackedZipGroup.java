//This file is part of BuildGDX.
//Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.FileHandle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.FileHandle.Resource.Whence;

import static ru.m210projects.Build.Strhandler.toLowerCase;

public class PackedZipGroup extends Group {
	
	private class ZipResource extends DataResource {
		public ZipResource(ZipEntry entry) {
			super(PackedZipGroup.this, entry.getName(), 0, null);

			String fileid = entry.getComment();
			if(fileid != null && !fileid.isEmpty()) {
				fileid = fileid.replaceAll("[^0-9]", ""); 
				this.fileid = Integer.parseInt(fileid);
			}
			this.size = (int) entry.getSize();

			if(debug && size > 0) System.out.println("\t" + filenamext + ", size: " +  size);
		}
		
		@Override
		protected void handleName(String fullname)
		{
			this.filenamext = PackedZipGroup.this.handleName(fullname);
			
			int point = filenamext.lastIndexOf('.');
			if(point != -1) {
				this.fileformat = filenamext.substring(point + 1);
				this.filename = filenamext.substring(0, point);
			} else {
				this.fileformat = "";
				this.filename = this.filenamext;
			}
		}
		
		@Override
		public void flush() { 
			close();
		}

		@Override
		public void close() { 
			synchronized(parent) {
				buffer.clear(); 
				buffer = null;
			}
		}
	}

	private final Resource file;

	public PackedZipGroup(Resource file) throws IOException
	{
		this.file = file;
		this.type = PackageType.PackedZip;
		
		file.seek(0, Whence.Set);
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.getBytes()));

		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			ZipResource res = new ZipResource(entry);
			if(res.size > 0) {
				add(res);
				numfiles++;
			}
			zis.closeEntry();
        }
		
		zis.close();
	}
	
	private boolean findEntry(ZipResource res)
	{
		if(res != null) {
			try {
				file.seek(0, Whence.Set);
				ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.getBytes()));
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if(handleName(entry.getName()).equals(res.filenamext)) {
						byte[] data = new byte[512];
						res.buffer = ByteBuffer.allocateDirect(res.size);
						res.buffer.order(ByteOrder.LITTLE_ENDIAN);
						int len;
						while((len = zis.read(data)) != -1) 
							res.buffer.put(data, 0, len);
						res.buffer.rewind();
						return true;
					}
					zis.closeEntry();
		        }
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	protected String handleName(String fullname) //zips can handle folders, so we must add separators to replacer
	{
		String filenamext = toLowerCase(fullname).replaceAll("[^a-zA-Z0-9_. /-]", "");
		if(filenamext.contains("/")) filenamext = filenamext.replace("/", File.separator);

		return filenamext;
	}
	
	public void removeFolders()
	{
		List<GroupResource> list = getList();
		filelist.clear();
		lookup.clear();
		
		for(GroupResource res : list) {
			int index = res.filenamext.lastIndexOf(File.separator);
			if(index != -1)
				res.filenamext = res.filenamext.substring(index + 1);
			
			int point = res.filenamext.lastIndexOf('.');
			if(point != -1) {
				res.filename = res.filenamext.substring(0, point);
			} else res.filename = res.filenamext;
			
			this.add(res);
		}
	}
	
	@Override
	protected boolean open(GroupResource res) {
		ZipResource available = (ZipResource) res;

		return findEntry(available);
	}
	
	@Override
	public int position() {
		return 0;
	}
}
