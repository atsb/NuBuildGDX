package ru.m210projects.Wang.Type;

public class Break_Info implements Comparable<Object> {
	public short picnum, breaknum, shrap_type;
	public short flags, shrap_amt;
	
	public Break_Info(int picnum, int breaknum, int shrap_type)
	{
		this.picnum = (short) picnum;
		this.breaknum = (short) breaknum;
		this.shrap_type = (short) shrap_type;
	}
	

	public Break_Info(int picnum, int breaknum, int shrap_type, int flags)
	{
		this(picnum, breaknum, shrap_type);
		this.flags = (short) flags;
	}
	
	public Break_Info(int picnum, int breaknum, int shrap_type, int flags, int shrap_amt)
	{
		this(picnum, breaknum, shrap_type, flags);
		this.shrap_amt = (short) shrap_amt;
	}


	@Override
	public int compareTo(Object info) {
		if(info instanceof Short) {
			return((short) info - this.picnum);
		} else if(info instanceof Break_Info) {
			return(((Break_Info) info).picnum - this.picnum);
		}
		
		return 0;
	}
}
