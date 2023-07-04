package ru.m210projects.Wang.Type;

public class Target_Sort implements Comparable<Target_Sort> {
	public int sprite_num;
	public int dang;
	public int dist;
	public int weight;

	@Override
	public int compareTo(Target_Sort tgt) {
		// will return a number less than 0 if tgt1 < tgt2
		return (this.weight - tgt.weight);
	}}
