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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.FileHandle.Resource.Whence;

import static ru.m210projects.Build.FileHandle.Group.*;

public class Cache1D {

	public enum PackageType {
		Grp, Rff, Zip, User, PackedGrp, PackedRff, PackedZip
	}

	private final Compat compat;
	private final List<Group> groupfil = new ArrayList<Group>();

	private final int grpsign = 0x536E654B;
	private final int zipsign = 0x04034b50;
	private final int rffsign = 0x1A464652;

	public Cache1D(Compat compat) {
		this.compat = compat;
	}

	public List<Group> getGroupList() {
		return new ArrayList<Group>(groupfil);
	}

	public Group getGroup(String groupname) {
		for (Group gr : groupfil) {
			if (gr.name.equalsIgnoreCase(groupname))
				return gr;
		}

		return null;
	}

	public Group add(String filename) {
		Group res = isGroup(filename);
		if (res != null) {
			if (res.numfiles != 0) {
				Console.Println("Found " + res.numfiles + " files in " + filename + " archive");
				groupfil.add(res);
			}
			return res;
		}

		return null;
	}

	public Group add(Group res) {
		if (res != null && res.numfiles != 0)
			groupfil.add(res);

		return res;
	}

	public UserGroup add(String name, boolean dynamic) {
		UserGroup group = new UserGroup(compat);

		group.name = name;
		group.type = PackageType.User;
		if (dynamic)
			group.flags |= (DYNAMIC | REMOVABLE);
		groupfil.add(group);

		return group;
	}

	public Group add(Resource res, String name) {
		Group out = null;

		res.seek(0, Whence.Set);
		switch (res.readInt()) {
		case grpsign: // KenS
			byte[] tmp = new byte[8];
			res.read(tmp);
			if (new String(tmp).compareTo("ilverman") == 0) {
				try {
					out = new GrpGroup(res, PackageType.PackedGrp);
					out.name = name;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case zipsign:
			try {
				out = new PackedZipGroup(res);
				out.name = name;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case rffsign:
			try {
				out = new RffGroup(res, PackageType.PackedRff);
				out.name = name;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		if (out != null) {
			if (out.numfiles != 0) {
				Console.Println("Found " + out.numfiles + " files in " + out.name + " archive");
				groupfil.add(out);
			}
		}

		return out;
	}

	public Group add(GroupResource res, boolean removable) {
		Group out;
		if (res.isClosed())
			res.getParent().open(res);

		out = add(res, res.getParent().type + ":" + res.getFullName());
		out.setFlags(true, removable);

		return out;
	}

	public Group isGroup(String filename) {
		Group out = null;
		FileResource handle = compat.open(filename);
		if (handle != null && handle.size() > 4) {
			switch (handle.readInt()) {
			case grpsign: // KenS
				byte[] tmp = new byte[8];
				handle.read(tmp);
				if (new String(tmp).compareTo("ilverman") == 0) {
					try {
						out = new GrpGroup(handle, PackageType.Grp);
						out.name = filename;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case zipsign:
				try {
					String path = handle.getPath();
					handle.close();
					out = new ZipGroup(path);
					out.name = filename;
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case rffsign:
				try {
					out = new RffGroup(handle, PackageType.Rff);
					out.name = filename;
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				handle.close();
				break;
			}
		}

		return out;
	}

	public boolean remove(String filename) {
		for (Iterator<Group> iterator = groupfil.iterator(); iterator.hasNext();) {
			Group group = iterator.next();
			if (group.name.equals(filename)) {
				group.dispose();
				iterator.remove();
				return true;
			}
		}

		return false;
	}

	public boolean remove(Group group) {
		if (groupfil.remove(group)) {
			group.dispose();
			return true;
		}

		return false;
	}

	public void clearDynamicResources() {
		for (Iterator<Group> iterator = groupfil.iterator(); iterator.hasNext();) {
			Group group = iterator.next();
			if ((group.flags & (DYNAMIC | REMOVABLE)) == (DYNAMIC | REMOVABLE)) {
				System.err.println("remove dynamic group: " + group.name);
				group.dispose();
				iterator.remove();
			}
		}
	}

	public List<GroupResource> getDynamicResources() {
		List<GroupResource> list = new ArrayList<GroupResource>();
		for (Iterator<Group> iterator = groupfil.iterator(); iterator.hasNext();) {
			Group group = iterator.next();
			if ((group.flags & DYNAMIC) != 0) {
				list.addAll(group.getList());
			}
		}
		return list;
	}

	public boolean contains(String filename, int searchfirst) {
		switch (searchfirst) {
		case 0: // search in dynamic groups ang external files
			// Search in dynamic groups first
			for (int k = groupfil.size() - 1; k >= 0; k--) {
				Group group = groupfil.get(k);
				if ((group.flags & DYNAMIC) != 0 && group.contains(filename))
					return true;
			}
			if (compat.checkFile(filename) != null)
				return true;
		case -1: // search in groups
			for (int k = groupfil.size() - 1; k >= 0; k--) {
				if (groupfil.get(k).contains(filename))
					return true;
			}
			break;
		default: // search in the group with index
			int index = searchfirst - 1;
			if (index < groupfil.size() && groupfil.get(index).contains(filename))
				return true;
			break;
		}

		return false;
	}

	public boolean contains(int fileid, String type) {
		for (int k = groupfil.size() - 1; k >= 0; k--) {
			if (groupfil.get(k).contains(fileid, type))
				return true;
		}

		return false;
	}

	public Resource open(String filename, int searchfirst) {
		Resource res;

		switch (searchfirst) {
		case 0: // search in dynamic groups ang external files

			// Search in dynamic groups first
			for (int k = groupfil.size() - 1; k >= 0; k--) {
				Group group = groupfil.get(k);
				if ((group.flags & DYNAMIC) != 0) {
					if ((res = group.open(filename)) != null)
						return res;
				}
			}

			if ((res = compat.open(filename)) != null)
				return res;
		case -1: // search in groups
			for (int k = groupfil.size() - 1; k >= 0; k--) {
				if ((res = groupfil.get(k).open(filename)) != null)
					return res;
			}
			break;
		default: // search in the group with index
			int index = searchfirst - 1;
			if (index < groupfil.size() && (res = groupfil.get(index).open(filename)) != null)
				return res;
			break;
		}

		return null;
	}

	public Resource open(int fileid, String type) {
		Resource res;
		for (int k = groupfil.size() - 1; k >= 0; k--) {
			if ((res = groupfil.get(k).open(fileid, type)) != null)
				return res;
		}

		return null;
	}

	public byte[] getBytes(String filename, int searchfirst) {
		byte[] out = null;
		Resource res = open(filename, searchfirst);
		if (res != null) {
			out = res.getBytes();
			res.close();
		}

		return out;
	}

	public byte[] getBytes(int fileid, String type) {
		byte[] out = null;
		Resource res = open(fileid, type);
		if (res != null) {
			out = res.getBytes();
			res.close();
		}

		return out;
	}
}
