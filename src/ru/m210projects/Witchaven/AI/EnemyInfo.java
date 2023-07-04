package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import ru.m210projects.Build.Types.SPRITE;

public class EnemyInfo {

	public short sizx, sizy;
	protected int attackdist;
	public int attackdamage;
	public int attackheight;
	protected short health;
	public int clipdist;
	public boolean fly;
	public int score;
	
	public EnemyInfo(int sizx, int sizy, int dist, int height, int damage, int clipdist, boolean fly, int health, int score)
	{
		this.sizx = (short) sizx;
		this.sizy = (short) sizy;
		this.attackdist = dist;
		this.attackheight = height;
		this.attackdamage = damage;
		this.clipdist = clipdist;
		this.fly = fly;
		this.health = (short) health;
		this.score = score;
	}
	
	public short getHealth(SPRITE spr)
	{
		return adjusthp(health);
	}
	
	public int getAttackDist(SPRITE spr)
	{
		return attackdist;
	}
	
	public void set(SPRITE spr)
	{
		spr.clipdist = clipdist;
		spr.hitag = getHealth(spr);
		if(sizx != -1)
			spr.xrepeat = sizx;
		if(sizy != -1)
			spr.yrepeat = sizy;
		spr.lotag = 100;
		
		int tflag = 0;
		if((spr.cstat & 514) != 0) 
			tflag = spr.cstat & 514;

		spr.cstat = (short) (0x101 | tflag);
	}
}
