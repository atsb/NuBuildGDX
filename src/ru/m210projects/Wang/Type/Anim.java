package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Track.CallbackSOsink;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class Anim {

	public enum AnimCallback {
		SOsink {
			@Override
			public void invoke(Anim ap, int data) {
				CallbackSOsink(ap, SectorObject[data]);
			}
		};
		public abstract void invoke(Anim ap, int data);
	}

	public enum AnimType {
		FloorZ, CeilZ, SpriteZ, SectorObjectZ, UserZ, SectUserDepth
	};

	public Object ptr;
	public int index; // to set interpolation
	public AnimType type;

	public int goal;
	public int vel;
	public short vel_adj;

	public AnimCallback callback;
	public int callbackdata;

	public Anim copy(Anim src) {
		this.ptr = src.ptr;
		this.index = src.index;
		this.type = src.type;
		this.goal = src.goal;
		this.vel = src.vel;
		this.vel_adj = src.vel_adj;
		this.callback = src.callback;
		this.callbackdata = src.callbackdata;

		return this;
	}

	public void save(BufferResource fil) {
		fil.writeShort(index);
		fil.writeByte(type != null ? type.ordinal() : -1);
		fil.writeInt(goal);
		fil.writeInt(vel);
		fil.writeShort(vel_adj);
		fil.writeInt(callback != null ? callback.ordinal() : -1);
		fil.writeInt(callbackdata);
	}

	public void load(Resource res) {
		index = res.readShort();
		int i = res.readByte();
		type = (i != -1) ? AnimType.values()[i] : null;
		goal = res.readInt();
		vel = res.readInt();
		vel_adj = res.readShort();
		i = res.readInt();
		callback = (i != -1) ? AnimCallback.values()[i] : null;
		callbackdata = res.readInt();
	}

}
