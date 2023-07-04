package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.Types.Seq.SeqHandling.GetInstance;

public class ActiveList {
	
	protected static class Node
	{
		private SeqInst inst;
		private short index;
		
		public boolean equals( SeqInst type )
		{
			return this.inst == type;
		}
		
		public void set( SeqInst type, int index ) {
			this.inst = type;
			this.index = (short) index;
		}
		
		public void set(Node src)
		{
			this.inst = src.inst;
			this.index = src.index;
		}
	}

	private int size;
	private final Node[] list;
	
	public ActiveList(int size)
	{
		list = new Node[size];
		for(int i = 0; i < size; i++)
			list[i] = new Node();
	}
	
	public void clear()
	{
		this.size = 0;
	}
	
	public int remove( SeqInst type )
	{
		int i = getPointer(type);
		if(i == -1) return -1;

		remove(i);
		return i;
	}
	
	public void remove( int num )
	{
		size--;
		list[num].set(list[size]);
	}
	
	public boolean add( SeqInst type, int index )
	{
		if(size >= list.length)
			return false;
		
		list[size].set(type, index);
		size++;
		
		return true;
	}
	
	protected int getPointer(SeqInst type)
	{
		for (int i = 0; i < size; i++)
			if (list[i].equals(type))
				return i;

		return -1;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public SeqInst getInst( int num )
	{
		return list[num].inst;
	}
	
	public short getIndex( int num )
	{
		return list[num].index;
	}
	
	public short getIndex(SeqInst pInst)
	{
		int i = getPointer(pInst);
		if(i != -1)
			return list[i].index;
		
		return -1;
	}

	public void set(byte[] types, short[] indexes, int size)
	{
		this.size = size;
		for (int i = 0; i < list.length; i++) {
			list[i].inst = i < size ? GetInstance(types[i], indexes[i]) : null;
			list[i].index = indexes[i];
		}
		
		for (int i = 0; i < size; i++) {
			SeqInst pInst = GetInstance(types[i], indexes[i]);

			if (pInst != null && pInst.isPlaying) {
				pInst.pSequence = SeqType.getInstance(pInst.nSeqID);
			}
		}
	}
}
