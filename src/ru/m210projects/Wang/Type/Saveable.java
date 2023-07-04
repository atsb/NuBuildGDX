package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Actor.DoActorDebris;
import static ru.m210projects.Wang.Ai.AiSaveable;
import static ru.m210projects.Wang.Enemies.Bunny.BunnySaveable;
import static ru.m210projects.Wang.Enemies.Coolg.CoolgSaveable;
import static ru.m210projects.Wang.Enemies.Coolie.CoolieSaveable;
import static ru.m210projects.Wang.Enemies.Eel.EelSaveable;
import static ru.m210projects.Wang.Enemies.GirlNinj.GirlNinjSaveable;
import static ru.m210projects.Wang.Enemies.Goro.GoroSaveable;
import static ru.m210projects.Wang.Enemies.Hornet.HornetSaveable;
import static ru.m210projects.Wang.Enemies.Lava.LavaSaveable;
import static ru.m210projects.Wang.Enemies.Ninja.NinjaSaveable;
import static ru.m210projects.Wang.Enemies.Ripper.RipperSaveable;
import static ru.m210projects.Wang.Enemies.Ripper2.Ripper2Saveable;
import static ru.m210projects.Wang.Enemies.Serp.SerpSaveable;
import static ru.m210projects.Wang.Enemies.Skel.SkelSaveable;
import static ru.m210projects.Wang.Enemies.Skull.SkullSaveable;
import static ru.m210projects.Wang.Enemies.Sumo.SumoSaveable;
import static ru.m210projects.Wang.Enemies.Zilla.ZillaSaveable;
import static ru.m210projects.Wang.Enemies.Zombie.ZombieSaveable;
import static ru.m210projects.Wang.JWeapon.JWeaponSaveable;
import static ru.m210projects.Wang.MiscActr.MiscSaveable;
import static ru.m210projects.Wang.Panel.PanelSaveable;
import static ru.m210projects.Wang.Player.PlayerSaveable;
import static ru.m210projects.Wang.Rotator.DoRotator;
import static ru.m210projects.Wang.Sector.DoSpawnSpot;
import static ru.m210projects.Wang.Slidor.DoSlidor;
import static ru.m210projects.Wang.Spike.DoSpike;
import static ru.m210projects.Wang.Spike.DoSpikeAuto;
import static ru.m210projects.Wang.Sprites.SpritesSaveable;
import static ru.m210projects.Wang.Text.StringTimer;
import static ru.m210projects.Wang.Vator.DoVator;
import static ru.m210projects.Wang.Vator.DoVatorAuto;
import static ru.m210projects.Wang.Weapon.WeaponSaveable;

import java.util.ArrayList;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Wang.Sprites.StateGroup;

public class Saveable {

	private static ArrayList<StateGroup> saveable_stategroup = new ArrayList<StateGroup>();
	private static ArrayList<Saveable> saveable;

	private int index = -1;

	public int ordinal() {
		if (index == -1)
			Console.Println("Error in savefile: " + stack, Console.OSDTEXT_RED);
		return index;
	}

	private String stack;

	public Saveable() {
		StringBuilder sb = new StringBuilder();
		sb.append(Thread.currentThread().getStackTrace()[4]);
		sb.append("\r\n");

		stack = sb.toString();
	}

	public static void SaveData(Saveable data) {
		saveable.add(data);
		data.index = saveable.size() - 1;
	}

	public static void SaveData(Saveable[] data) {
		for (Saveable d : data)
			SaveData(d);
	}

	public static void SaveData(Saveable[][] data) {
		for (Saveable[] arr : data)
			SaveData(arr);
	}

	public static void SaveGroup(StateGroup group) {
		saveable_stategroup.add(group);
		group.setIndex(saveable_stategroup.size() - 1);
	}

	public static void InitSaveable() {
		saveable = new ArrayList<Saveable>();

		SaveData(StringTimer);
		SaveData(DoSpawnSpot);
		SaveData(DoActorDebris);

		AiSaveable();

		// Enemies
		BunnySaveable();
		CoolgSaveable();
		CoolieSaveable();
		EelSaveable();
		GirlNinjSaveable();
		GoroSaveable();
		HornetSaveable();
		LavaSaveable();
		NinjaSaveable();
		RipperSaveable();
		Ripper2Saveable();
		SerpSaveable();
		SkelSaveable();
		SkullSaveable();
		SumoSaveable();
		ZillaSaveable();
		ZombieSaveable();

		// MiscActors
		MiscSaveable();

		PanelSaveable();
		PlayerSaveable();
		JWeaponSaveable();
		WeaponSaveable();

		SpritesSaveable();

		SaveData(DoRotator);
		SaveData(DoVator);
		SaveData(DoVatorAuto);
		SaveData(DoSpike);
		SaveData(DoSpikeAuto);
		SaveData(DoSlidor);
	}

	public static Saveable valueOf(int index) {
		if (index < 0 || index >= saveable.size())
			return null;
		return saveable.get(index);
	}

	public static StateGroup getGroup(int index) {
		if (index < 0 || index >= saveable_stategroup.size())
			return null;
		return saveable_stategroup.get(index);
	}
}
