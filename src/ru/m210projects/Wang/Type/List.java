package ru.m210projects.Wang.Type;

public class List {

	public Panel_Sprite Next, Prev;

	public static void Init(Panel_Sprite l) {
		l.Prev = l.Next = l;
	}

	public static void Insert(Panel_Sprite list, Panel_Sprite node) {
		node.Prev = list;
		node.Next = list.Next;
		list.Next = node;
		node.Next.Prev = node;
	}
	
	public static void InsertTrail(Panel_Sprite list, Panel_Sprite node) {
		node.Next = list;
		node.Prev = list.Prev;
		list.Prev = node;
		node.Prev.Next = node;
	}

	public static void Delete(Panel_Sprite node) {
		node.Prev.Next = node.Next;
		node.Next.Prev = node.Prev;
	}

	public static boolean IsEmpty(Panel_Sprite list) {
		return list.Next == list;
	}

	public static Panel_Sprite getFirst(Panel_Sprite list) {
		return list.Next;
	}

	public static Panel_Sprite getLast(Panel_Sprite list) {
		return list.Prev;
	}

	public static int PanelSpriteToNdx(Panel_Sprite PanelSpriteList, Panel_Sprite psprite) {
		int ndx = 0;
		Panel_Sprite next;
		for (Panel_Sprite psp = PanelSpriteList.Next; psp != PanelSpriteList; psp = next) {
			next = psp.Next;
			if (psp == psprite)
				return (ndx);

			ndx++;
		}

		// special case for pointing to the list head
		if (psprite == PanelSpriteList)
			return (9999);

		return (-1);
	}

	public static Panel_Sprite PanelNdxToSprite(Panel_Sprite PanelSpriteList, int ndx) {
		int count = 0;

		if (ndx == -1)
			return null;

		if (ndx == 9999)
			return (PanelSpriteList);

		Panel_Sprite next;
		for (Panel_Sprite psp = PanelSpriteList.Next; psp != PanelSpriteList; psp = next) {
			next = psp.Next;
			if (count == ndx)
				return (psp);

			count++;
		}

		return null;
	}
}
