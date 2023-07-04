package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoBeginJump;
import static ru.m210projects.Wang.Break.GlobBreakInfo;
import static ru.m210projects.Wang.Break.SHRAP_BLOOD;
import static ru.m210projects.Wang.Break.SHRAP_COIN;
import static ru.m210projects.Wang.Break.SHRAP_EXPLOSION;
import static ru.m210projects.Wang.Break.SHRAP_GENERIC;
import static ru.m210projects.Wang.Break.SHRAP_GIBS;
import static ru.m210projects.Wang.Break.SHRAP_GLASS;
import static ru.m210projects.Wang.Break.SHRAP_LARGE_EXPLOSION;
import static ru.m210projects.Wang.Break.SHRAP_MARBELS;
import static ru.m210projects.Wang.Break.SHRAP_METAL;
import static ru.m210projects.Wang.Break.SHRAP_METALMIX;
import static ru.m210projects.Wang.Break.SHRAP_NONE;
import static ru.m210projects.Wang.Break.SHRAP_PAPER;
import static ru.m210projects.Wang.Break.SHRAP_PAPERMIX;
import static ru.m210projects.Wang.Break.SHRAP_SO_SMOKE;
import static ru.m210projects.Wang.Break.SHRAP_STONE;
import static ru.m210projects.Wang.Break.SHRAP_TREE_BARK;
import static ru.m210projects.Wang.Break.SHRAP_WOOD;
import static ru.m210projects.Wang.Break.SHRAP_WOODMIX;
import static ru.m210projects.Wang.Digi.DIGI_BREAKDEBRIS;
import static ru.m210projects.Wang.Digi.DIGI_BREAKGLASS;
import static ru.m210projects.Wang.Digi.DIGI_BREAKINGWOOD;
import static ru.m210projects.Wang.Digi.DIGI_BREAKMETAL;
import static ru.m210projects.Wang.Digi.DIGI_BREAKSTONES;
import static ru.m210projects.Wang.Digi.DIGI_COINS;
import static ru.m210projects.Wang.Digi.DIGI_GIBS1;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.ACTOR_GRAVITY;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PLAYER_DEATH_CRUMBLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPRX_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.SPR_BOUNCE;
import static ru.m210projects.Wang.Gameutils.SP_TAG10;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG8;
import static ru.m210projects.Wang.Gameutils.SP_TAG9;
import static ru.m210projects.Wang.Gameutils.WPN_NM_SECTOR_SQUISH;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JWeapon.InitPhosphorus;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R1;
import static ru.m210projects.Wang.Names.BREAK_BARREL;
import static ru.m210projects.Wang.Names.BREAK_BOTTLE1;
import static ru.m210projects.Wang.Names.BREAK_BOTTLE2;
import static ru.m210projects.Wang.Names.BREAK_LIGHT;
import static ru.m210projects.Wang.Names.BREAK_MUSHROOM;
import static ru.m210projects.Wang.Names.BREAK_PEDISTAL;
import static ru.m210projects.Wang.Names.COOLG_RUN_R0;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R0;
import static ru.m210projects.Wang.Names.GIRLNINJA_RUN_R0;
import static ru.m210projects.Wang.Names.GORO_RUN_R0;
import static ru.m210projects.Wang.Names.HORNET_RUN_R0;
import static ru.m210projects.Wang.Names.LAVA_BOULDER;
import static ru.m210projects.Wang.Names.NINJA_DEAD;
import static ru.m210projects.Wang.Names.NINJA_Head_R0;
import static ru.m210projects.Wang.Names.NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R0;
import static ru.m210projects.Wang.Names.SERP_RUN_R0;
import static ru.m210projects.Wang.Names.SKEL_RUN_R0;
import static ru.m210projects.Wang.Names.SKULL_R0;
import static ru.m210projects.Wang.Names.SKULL_SERP;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_MISC;
import static ru.m210projects.Wang.Names.STAT_SKIP4;
import static ru.m210projects.Wang.Names.SUMO_RUN_R0;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Palette.PALETTE_DEFAULT;
import static ru.m210projects.Wang.Palette.PALETTE_SKEL_GORE;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SetOwner;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_missile;
import static ru.m210projects.Wang.Stag.SPAWN_SPOT;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.BOLT_EXP;
import static ru.m210projects.Wang.Weapon.COIN_SHRAP;
import static ru.m210projects.Wang.Weapon.ELECTRO_ENEMY;
import static ru.m210projects.Wang.Weapon.ELECTRO_PLAYER;
import static ru.m210projects.Wang.Weapon.ELECTRO_SHARD;
import static ru.m210projects.Wang.Weapon.EMP;
import static ru.m210projects.Wang.Weapon.FIREBALL_EXP;
import static ru.m210projects.Wang.Weapon.GLASS_SHRAP_A;
import static ru.m210projects.Wang.Weapon.GORE_Arm;
import static ru.m210projects.Wang.Weapon.GORE_ChunkS;
import static ru.m210projects.Wang.Weapon.GORE_Drip;
import static ru.m210projects.Wang.Weapon.GORE_Head;
import static ru.m210projects.Wang.Weapon.GORE_Leg;
import static ru.m210projects.Wang.Weapon.GORE_Liver;
import static ru.m210projects.Wang.Weapon.GORE_Lung;
import static ru.m210projects.Wang.Weapon.GORE_SkullCap;
import static ru.m210projects.Wang.Weapon.GORE_Torso;
import static ru.m210projects.Wang.Weapon.GRENADE_EXP;
import static ru.m210projects.Wang.Weapon.LAVA_SHARD;
import static ru.m210projects.Wang.Weapon.MARBEL;
import static ru.m210projects.Wang.Weapon.METAL_SHRAP_A;
import static ru.m210projects.Wang.Weapon.MISSILEMOVETICS;
import static ru.m210projects.Wang.Weapon.PAPER_SHRAP_A;
import static ru.m210projects.Wang.Weapon.PLASMA_FOUNTAIN;
import static ru.m210projects.Wang.Weapon.SECTOR_EXP;
import static ru.m210projects.Wang.Weapon.STONE_SHRAP_A;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.SpawnLargeExp;
import static ru.m210projects.Wang.Weapon.TANK_SHELL_EXP;
import static ru.m210projects.Wang.Weapon.TRACER_EXP;
import static ru.m210projects.Wang.Weapon.Vomit1;
import static ru.m210projects.Wang.Weapon.WOOD_SHRAP_A;
import static ru.m210projects.Wang.Weapon.s_BreakBarrel;
import static ru.m210projects.Wang.Weapon.s_BreakBottle1;
import static ru.m210projects.Wang.Weapon.s_BreakBottle2;
import static ru.m210projects.Wang.Weapon.s_BreakLight;
import static ru.m210projects.Wang.Weapon.s_BreakPedistal;
import static ru.m210projects.Wang.Weapon.s_CoinShrap;
import static ru.m210projects.Wang.Weapon.s_EMPShrap;
import static ru.m210projects.Wang.Weapon.s_ElectroShrap;
import static ru.m210projects.Wang.Weapon.s_FastGoreDrip;
import static ru.m210projects.Wang.Weapon.s_GlassShrapA;
import static ru.m210projects.Wang.Weapon.s_GlassShrapB;
import static ru.m210projects.Wang.Weapon.s_GlassShrapC;
import static ru.m210projects.Wang.Weapon.s_GoreArm;
import static ru.m210projects.Wang.Weapon.s_GoreChunkS;
import static ru.m210projects.Wang.Weapon.s_GoreDrip;
import static ru.m210projects.Wang.Weapon.s_GoreFlame;
import static ru.m210projects.Wang.Weapon.s_GoreFlameChunkB;
import static ru.m210projects.Wang.Weapon.s_GoreHead;
import static ru.m210projects.Wang.Weapon.s_GoreLeg;
import static ru.m210projects.Wang.Weapon.s_GoreLiver;
import static ru.m210projects.Wang.Weapon.s_GoreLung;
import static ru.m210projects.Wang.Weapon.s_GoreSkullCap;
import static ru.m210projects.Wang.Weapon.s_GoreTorso;
import static ru.m210projects.Wang.Weapon.s_LavaShard;
import static ru.m210projects.Wang.Weapon.s_Marbel;
import static ru.m210projects.Wang.Weapon.s_MetalShrapA;
import static ru.m210projects.Wang.Weapon.s_MetalShrapB;
import static ru.m210projects.Wang.Weapon.s_MetalShrapC;
import static ru.m210projects.Wang.Weapon.s_PaperShrapA;
import static ru.m210projects.Wang.Weapon.s_PaperShrapB;
import static ru.m210projects.Wang.Weapon.s_PaperShrapC;
import static ru.m210projects.Wang.Weapon.s_StoneShrapA;
import static ru.m210projects.Wang.Weapon.s_StoneShrapB;
import static ru.m210projects.Wang.Weapon.s_StoneShrapC;
import static ru.m210projects.Wang.Weapon.s_Vomit1;
import static ru.m210projects.Wang.Weapon.s_WoodShrapA;
import static ru.m210projects.Wang.Weapon.s_WoodShrapB;
import static ru.m210projects.Wang.Weapon.s_WoodShrapC;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Player.PlayerStateGroup;
import ru.m210projects.Wang.Type.SHRAP;
import ru.m210projects.Wang.Type.USER;

public class Shrap {

	public static final int Z_TOP = 0;
	public static final int Z_MID = 1;
	public static final int Z_BOT = 2;

	private static final SHRAP CoinShrap[] = {
			new SHRAP(s_CoinShrap, COIN_SHRAP, 5, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapA, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapB, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapC, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP GlassShrap[] = {
			new SHRAP(s_GlassShrapA, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapB, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapC, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP WoodShrap[] = {
			new SHRAP(s_WoodShrapA, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_WoodShrapB, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_WoodShrapC, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP StoneShrap[] = {
			new SHRAP(s_StoneShrapA, STONE_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_StoneShrapB, STONE_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_StoneShrapC, STONE_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP PaperShrap[] = {
			new SHRAP(s_PaperShrapA, PAPER_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_PaperShrapB, PAPER_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_PaperShrapC, PAPER_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP MetalShrap[] = {
			new SHRAP(s_MetalShrapA, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapB, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapC, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP MetalMix[] = {
			new SHRAP(s_GlassShrapA, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapB, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapC, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapA, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapB, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapC, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP WoodMix[] = {
			new SHRAP(s_WoodShrapA, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_WoodShrapB, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_WoodShrapC, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapA, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapB, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_MetalShrapC, METAL_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP PaperMix[] = {
			new SHRAP(s_WoodShrapA, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_WoodShrapB, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_WoodShrapC, WOOD_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_PaperShrapA, PAPER_SHRAP_A, 2, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_PaperShrapB, PAPER_SHRAP_A, 2, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_PaperShrapC, PAPER_SHRAP_A, 2, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	private static final SHRAP Marbels[] = { new SHRAP(s_Marbel, MARBEL, 5, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapA, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapB, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),
			new SHRAP(s_GlassShrapC, GLASS_SHRAP_A, 1, Z_MID, 200, 600, 100, 500, true, 2048),

	};

	////
	// END - BREAK shrap types
	////

	private static final SHRAP EMPShrap[] = { new SHRAP(s_EMPShrap, EMP, 1, Z_MID, 500, 1100, 300, 600, false, 128),

	};

	private static final SHRAP StdShrap[] = { new SHRAP(s_GoreHead, GORE_Head, 1, Z_TOP, 400, 700, 20, 40, true, 2048),
			new SHRAP(s_GoreLung, GORE_Lung, 2, Z_TOP, 500, 800, 100, 300, true, 2048),
			new SHRAP(s_GoreLiver, GORE_Liver, 1, Z_MID, 300, 500, 100, 150, true, 2048),
			new SHRAP(s_GoreArm, GORE_Arm, 1, Z_MID, 300, 500, 250, 500, true, 2048),
			new SHRAP(s_GoreSkullCap, GORE_SkullCap, 1, Z_TOP, 300, 500, 250, 500, true, 2048),
			new SHRAP(s_FastGoreDrip, GORE_Drip, 8, Z_BOT, 600, 800, 50, 70, false, 2048),

	};

	private static final SHRAP HeartAttackShrap[] = // fewer gibs because of the plasma fountain sprites
			{ new SHRAP(s_GoreLung, GORE_Lung, 2, Z_TOP, 500, 1100, 300, 600, true, 2048),
					new SHRAP(s_GoreLiver, GORE_Liver, 1, Z_MID, 500, 1100, 300, 500, true, 2048),
					new SHRAP(s_GoreArm, GORE_Arm, 2, Z_MID, 500, 1100, 350, 600, true, 2048),

			};

	private static final SHRAP SkelGore[] = { new SHRAP(s_GoreHead, GORE_Head, 1, Z_TOP, 400, 700, 20, 40, true, 2048),
			new SHRAP(s_GoreLung, GORE_Lung, 2, Z_TOP, 500, 800, 100, 300, true, 2048),
			new SHRAP(s_GoreLiver, GORE_Liver, 1, Z_MID, 300, 500, 100, 150, true, 2048),
			new SHRAP(s_GoreSkullCap, GORE_SkullCap, 1, Z_TOP, 300, 500, 100, 150, true, 2048),
			new SHRAP(s_GoreArm, GORE_Arm, 1, Z_MID, 300, 500, 250, 500, true, 2048),
			new SHRAP(s_GoreLeg, GORE_Leg, 2, Z_BOT, 200, 400, 250, 500, true, 2048),
			new SHRAP(s_GoreChunkS, GORE_ChunkS, 4, Z_BOT, 200, 400, 250, 400, true, 2048),

	};

	private static final SHRAP UpperGore[] = { new SHRAP(s_GoreHead, GORE_Head, 1, Z_TOP, 400, 700, 20, 40, true, 2048),
			new SHRAP(s_GoreLung, GORE_Lung, 2, Z_TOP, 500, 800, 100, 300, true, 2048),
			new SHRAP(s_GoreLiver, GORE_Liver, 1, Z_MID, 300, 500, 100, 150, true, 2048),
			new SHRAP(s_GoreSkullCap, GORE_SkullCap, 1, Z_TOP, 300, 500, 100, 150, true, 2048),
			new SHRAP(s_GoreArm, GORE_Arm, 1, Z_MID, 300, 500, 250, 500, true, 2048),

	};

	private static final SHRAP SmallGore[] = {
			new SHRAP(s_GoreDrip, GORE_Drip, 3, Z_TOP, 600, 800, 50, 70, false, 2048),
			new SHRAP(s_FastGoreDrip, GORE_Drip, 3, Z_BOT, 600, 800, 70, 100, false, 2048),

	};

	private static final SHRAP FlamingGore[] = {
			new SHRAP(s_GoreFlame, GORE_Drip, 2, Z_TOP, 600, 800, 100, 200, false, 2048),
			new SHRAP(s_GoreFlameChunkB, GORE_Drip, 4, Z_MID, 300, 500, 100, 200, false, 2048),
			new SHRAP(s_GoreFlame, GORE_Drip, 2, Z_BOT, 100, 200, 100, 200, false, 2048),

	};

	private static final SHRAP FireballExpShrap1[] = {
			new SHRAP(s_GoreFlame, GORE_Drip, 1, Z_MID, 100, 300, 100, 200, true, 2048),

	};

	private static final SHRAP FireballExpShrap2[] = {
			new SHRAP(s_GoreFlame, GORE_Drip, 2, Z_MID, 100, 300, 100, 200, true, 2048),

	};

	static SHRAP[] FireballExpShrap[] = { FireballExpShrap1, FireballExpShrap2 };

	// state, id, num, zlevel, min_jspeed, max_jspeed, min_vel, max_vel,
	// random_disperse, ang_range;
	private static final SHRAP ElectroShrap[] = {
			new SHRAP(s_ElectroShrap, ELECTRO_SHARD, 12, Z_TOP, 200, 600, 100, 500, true, 2048), };

	// state, id, num, zlevel, min_jspeed, max_jspeed, min_vel, max_vel,
	// random_disperse, ang_range;
	private static final SHRAP LavaShrap1[] = {
			new SHRAP(s_GoreFlame, GORE_Drip, 1, Z_TOP, 400, 1400, 100, 400, true, 2048),

	};

	private static final SHRAP LavaShrap2[] = {
			new SHRAP(s_GoreFlameChunkB, GORE_Drip, 1, Z_TOP, 400, 1400, 100, 400, true, 2048),

	};

	private static final SHRAP[] LavaShrapTable[] = { LavaShrap1, LavaShrap2 };

	private static final SHRAP LavaBoulderShrap[] = {
			new SHRAP(s_LavaShard, LAVA_SHARD, 16, Z_MID, 400, 900, 200, 600, true, 2048),

	};

	//
	// PLAYER SHRAP
	//

	// state, id, num, zlevel, min_jspeed, max_jspeed, min_vel, max_vel,
	// random_disperse, ang_range;
	private static final SHRAP PlayerGoreFall[] = {
			new SHRAP(s_GoreSkullCap, GORE_SkullCap, 1, Z_TOP, 200, 300, 100, 200, true, 2048),
			new SHRAP(s_GoreLiver, GORE_Liver, 1, Z_MID, 200, 300, 100, 200, true, 2048),
			new SHRAP(s_GoreLung, GORE_Lung, 1, Z_MID, 200, 300, 100, 200, true, 2048),
			new SHRAP(s_GoreDrip, GORE_Drip, 10, Z_MID, 200, 300, 100, 200, false, 2048),
			new SHRAP(s_GoreArm, GORE_Arm, 1, Z_MID, 200, 300, 100, 200, true, 2048),
			new SHRAP(s_FastGoreDrip, GORE_Drip, 10, Z_BOT, 200, 300, 100, 200, false, 2048),

	};

	private static final SHRAP PlayerGoreFly[] = {
			new SHRAP(s_GoreSkullCap, GORE_SkullCap, 1, Z_TOP, 500, 1100, 300, 600, true, 2048),
			new SHRAP(s_GoreTorso, GORE_Torso, 1, Z_MID, 500, 1100, 300, 500, true, 2048),
			new SHRAP(s_GoreLiver, GORE_Liver, 1, Z_MID, 200, 300, 100, 200, true, 2048),
			new SHRAP(s_GoreArm, GORE_Arm, 1, Z_MID, 500, 1100, 350, 600, true, 2048),
			new SHRAP(s_FastGoreDrip, GORE_Drip, 16, Z_MID, 500, 1100, 350, 600, false, 2048),
			new SHRAP(s_FastGoreDrip, GORE_Drip, 16, Z_BOT, 500, 1100, 350, 600, false, 2048),

	};

	private static final SHRAP PlayerDeadHead[] = {
			new SHRAP(s_GoreDrip, GORE_Drip, 2, Z_TOP, 150, 400, 40, 80, true, 2048),
			new SHRAP(s_GoreDrip, GORE_Drip, 2, Z_MID, 150, 400, 40, 80, true, 2048),

	};

	// state, id, num, zlevel, min_jspeed, max_jspeed, min_vel, max_vel,
	// random_disperse, ang_range;
	private static final SHRAP PlayerHeadHurl1[] = {
			new SHRAP(s_Vomit1, Vomit1, 1, Z_BOT, 250, 400, 100, 200, true, 256),

	};

	public static final int WALL_FLOOR_SHRAP = 4097;

	// Individual shraps can be copied to this and then values can be changed
	private static SHRAP[] CustomShrap = new SHRAP[20];
	private static int[] hz = new int[3];

	private static class AutoShrapRet {
		SHRAP[] p;
		int size;
		boolean bounce;
	}

	private static final AutoShrapRet ret = new AutoShrapRet();

	private static AutoShrapRet AutoShrap(int ParentNum, int shrap_type, short shrap_amt, int shrap_delta_size) {
		SHRAP[] p = SmallGore;
		short shrap_xsize = 48;
		boolean shrap_bounce = false;

		SPRITE parent = sprite[ParentNum];
		switch (shrap_type) {
		case SHRAP_NONE:
			return null;

		case SHRAP_GLASS:
			PlaySound(DIGI_BREAKGLASS, parent, v3df_dontpan | v3df_doppler);
			p = GlassShrap;
			if (shrap_amt != 0) {
				for (int c = 0; c < GlassShrap.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(GlassShrap[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_GENERIC:
		case SHRAP_STONE:
			PlaySound(DIGI_BREAKSTONES, parent, v3df_dontpan | v3df_doppler);
			p = StoneShrap;
			if (shrap_amt != 0) {

				for (int c = 0; c < StoneShrap.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(StoneShrap[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;

			}

			shrap_xsize = (short) (8 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_WOOD:
			PlaySound(DIGI_BREAKINGWOOD, parent, v3df_dontpan | v3df_doppler);
			p = WoodShrap;
			if (shrap_amt != 0) {

				for (int c = 0; c < WoodShrap.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(WoodShrap[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;

			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_BLOOD:
			shrap_xsize = (short) (16 + shrap_delta_size);
			break;

		case SHRAP_GIBS:
			PlaySound(DIGI_GIBS1, parent, v3df_dontpan | v3df_doppler);
			p = SmallGore;
			shrap_xsize = 34;
			shrap_bounce = false;
			break;

		case SHRAP_TREE_BARK:
			PlaySound(DIGI_BREAKINGWOOD, parent, v3df_dontpan | v3df_doppler);
			p = WoodShrap;
			if (shrap_amt != 0) {

				for (int c = 0; c < WoodShrap.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(WoodShrap[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_PAPER:
			p = PaperShrap;
			if (shrap_amt != 0) {

				for (int c = 0; c < PaperShrap.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(PaperShrap[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			break;

		case SHRAP_METAL:
			PlaySound(DIGI_BREAKMETAL, parent, v3df_dontpan | v3df_doppler);
			p = MetalShrap;
			if (shrap_amt != 0) {

				for (int c = 0; c < p.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(p[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_COIN:
			PlaySound(DIGI_COINS, parent, v3df_dontpan | v3df_doppler);
			p = CoinShrap;
			if (shrap_amt != 0) {
				for (int c = 0; c < p.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(p[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_METALMIX:
			PlaySound(DIGI_BREAKMETAL, parent, v3df_dontpan | v3df_doppler);
			p = MetalMix;
			if (shrap_amt != 0) {
				for (int c = 0; c < p.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(p[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_MARBELS:
			p = Marbels;
			if (shrap_amt != 0) {
				for (int c = 0; c < p.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(p[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (10 + shrap_delta_size);
			shrap_bounce = true;
			break;
		case SHRAP_WOODMIX:
			PlaySound(DIGI_BREAKINGWOOD, parent, v3df_dontpan | v3df_doppler);
			p = WoodMix;
			if (shrap_amt != 0) {
				for (int c = 0; c < p.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(p[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = true;
			break;

		case SHRAP_PAPERMIX:
			PlaySound(DIGI_BREAKINGWOOD, parent, v3df_dontpan | v3df_doppler);
			p = PaperMix;
			if (shrap_amt != 0) {
				for (int c = 0; c < p.length; c++) {
					if (CustomShrap[c] == null)
						CustomShrap[c] = new SHRAP();
					CustomShrap[c].copy(p[c]);
				}

				CustomShrap[0].num = shrap_amt;
				p = CustomShrap;
			}

			shrap_xsize = (short) (16 + shrap_delta_size);
			shrap_bounce = false;
			break;

		case SHRAP_SO_SMOKE:
			return null;

		case SHRAP_EXPLOSION: {
			short spnum;
			short size;
			SPRITE ep;

			spnum = (short) SpawnLargeExp(ParentNum);
			ep = sprite[spnum];

			size = ep.xrepeat;
			ep.xrepeat = ep.yrepeat = (short) (size + shrap_delta_size);

			return null;
		}

		case SHRAP_LARGE_EXPLOSION:
			short spnum;
			short size;
			SPRITE ep;

			spnum = (short) SpawnLargeExp(ParentNum);
			ep = sprite[spnum];

			size = ep.xrepeat;
			ep.xrepeat = ep.yrepeat = (short) (size + shrap_delta_size);

			InitPhosphorus(spnum);

			return null;
		default:
			return null;
		}

		ret.p = p;
		ret.bounce = shrap_bounce;
		ret.size = shrap_xsize;

		return ret;
	}

	public static boolean SpawnShrap(int ParentNum, int Secondary) {
		SPRITE parent = sprite[ParentNum];
		SPRITE sp;
		USER u, pu = pUser[ParentNum];
		short SpriteNum;
		short i;

		SHRAP[] p = SmallGore;
		short shrap_shade = -15;
		short shrap_xsize = 48, shrap_ysize = 48;
		boolean retval = true;
		short shrap_pal = PALETTE_DEFAULT;
		int shrap_floor_dist = Z(2);
		int shrap_ceiling_dist = Z(2);
		int nx, ny;
		short jump_grav = ACTOR_GRAVITY;
		short start_ang = 0;
		short shrap_owner = -1;
		boolean shrap_bounce = false;
		short WaitTics = 64;
		short shrap_type;
		int shrap_rand_zamt = 0;
		short shrap_ang = parent.ang;
		short shrap_delta_size = 0;
		short shrap_amt = 0;

		if (Prediction)
			return false;

		// Don't spawn shrapnel in invalid sectors gosh dern it!
		if (parent.sectnum < 0 || parent.sectnum >= MAXSECTORS) {
			return false;
		}
		
		AutoShrapRet ret = null;

		if (GlobBreakInfo != null) {
			shrap_type = GlobBreakInfo.shrap_type;
			shrap_amt = GlobBreakInfo.shrap_amt;
			GlobBreakInfo = null;

			ret = AutoShrap(ParentNum, shrap_type, shrap_amt, shrap_delta_size);
			if(ret == null)
				return false;
			
			p = ret.p;
			shrap_bounce = ret.bounce;
			shrap_xsize = shrap_ysize = (short) ret.size;
		} else if (TEST(parent.extra, SPRX_BREAKABLE)) {
			// if no user
			if (pUser[ParentNum] == null) {
				// Jump to shrap type
				shrap_type = (short) SP_TAG8(parent);

				shrap_delta_size = (byte) SP_TAG10(parent);
				shrap_rand_zamt = SP_TAG9(parent);
				// Hey, better limit this in case mappers go crazy, like I did. :)
				// Kills frame rate!
				shrap_amt = (short) SP_TAG8(parent);
				if (shrap_amt > 5)
					shrap_amt = 5;

				ret = AutoShrap(ParentNum, shrap_type, shrap_amt, shrap_delta_size);
				if(ret == null)
					return false;
				
				p = ret.p;
				shrap_bounce = ret.bounce;
				shrap_xsize = shrap_ysize = (short) ret.size;

			} else {
				// has a user - is programmed
				change_sprite_stat(ParentNum, STAT_MISC);
				parent.extra &= ~(SPRX_BREAKABLE);
				parent.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			}
		}

		if(ret == null) {
			switch (pu.ID) {
			case ST1:
				switch (parent.hitag) {
				case SPAWN_SPOT:
					if (pu.LastDamage != 0)
						shrap_type = (short) SP_TAG3(parent);
					else
						shrap_type = (short) SP_TAG6(parent);
	
					shrap_delta_size = (byte) SP_TAG10(parent);
					shrap_rand_zamt = SP_TAG9(parent);
					// Hey, better limit this in case mappers go crazy, like I did. :)
					// Kills frame rate!
					shrap_amt = (short) SP_TAG8(parent);
					if (shrap_amt > 5)
						shrap_amt = 5;
	
					ret = AutoShrap(ParentNum, shrap_type, shrap_amt, shrap_delta_size);
					if(ret == null)
						return false;
					
					p = ret.p;
					shrap_bounce = ret.bounce;
					shrap_xsize = shrap_ysize = (short) ret.size;
					
					break;
	
				default:
					p = LavaShrapTable[RANDOM_P2(2 << 8) >> 8];
					break;
				}
				break;
	
			case BREAK_BARREL:
				PlaySound(DIGI_BREAKDEBRIS, parent, v3df_dontpan | v3df_doppler);
				p = WoodShrap;
				shrap_xsize = shrap_ysize = 24;
				shrap_bounce = true;
				ChangeState(ParentNum, s_BreakBarrel[0]);
				break;
			case BREAK_LIGHT:
				PlaySound(DIGI_BREAKGLASS, parent, v3df_dontpan | v3df_doppler);
				p = GlassShrap;
				shrap_xsize = shrap_ysize = 24;
				shrap_bounce = true;
				ChangeState(ParentNum, s_BreakLight[0]);
				break;
			case BREAK_PEDISTAL:
				PlaySound(DIGI_BREAKSTONES, parent, v3df_dontpan | v3df_doppler);
				p = StoneShrap;
				shrap_xsize = shrap_ysize = 24;
				shrap_bounce = true;
				ChangeState(ParentNum, s_BreakPedistal[0]);
				break;
			case BREAK_BOTTLE1:
				PlaySound(DIGI_BREAKGLASS, parent, v3df_dontpan | v3df_doppler);
				p = GlassShrap;
				shrap_xsize = shrap_ysize = 8;
				shrap_bounce = true;
				ChangeState(ParentNum, s_BreakBottle1[0]);
				break;
			case BREAK_BOTTLE2:
				PlaySound(DIGI_BREAKGLASS, parent, v3df_dontpan | v3df_doppler);
				p = GlassShrap;
				shrap_xsize = shrap_ysize = 8;
				shrap_bounce = true;
				ChangeState(ParentNum, s_BreakBottle2[0]);
				break;
			case BREAK_MUSHROOM:
				PlaySound(DIGI_BREAKDEBRIS, parent, v3df_dontpan | v3df_doppler);
				p = StoneShrap;
				shrap_xsize = shrap_ysize = 4;
				shrap_bounce = true;
				SetSuicide(ParentNum); // kill next iteration
				break;
			case BOLT_EXP:
				return (false);
			case TANK_SHELL_EXP:
				return (false);
			case TRACER_EXP:
				return (false);
			case BOLT_THINMAN_R1:
				p = MetalShrap;
				if (shrap_amt != 0) {
					for (int c = 0; c < p.length; c++) {
						if (CustomShrap[c] == null)
							CustomShrap[c] = new SHRAP();
						CustomShrap[c].copy(p[c]);
					}
	
					CustomShrap[0].num = 1;
					p = CustomShrap;
				}
	
				shrap_xsize = shrap_ysize = 10;
				break;
			case LAVA_BOULDER:
				PlaySound(DIGI_BREAKSTONES, parent, v3df_dontpan | v3df_doppler);
				p = LavaBoulderShrap;
				shrap_owner = parent.owner;
				shrap_xsize = shrap_ysize = 24;
				shrap_bounce = true;
				break;
			case SECTOR_EXP:
				return (false);
			case GRENADE_EXP:
				return (false);
			case FIREBALL_EXP:
				return (false);
			case ELECTRO_PLAYER:
			case ELECTRO_ENEMY:
				shrap_owner = parent.owner;
				p = ElectroShrap;
				shrap_xsize = shrap_ysize = 20;
				break;
			case COOLIE_RUN_R0:
				if (Secondary == WPN_NM_SECTOR_SQUISH)
					break;
				break;
			case NINJA_DEAD:
				return (false);
			case NINJA_Head_R0: {
				if (pu.Rot == PlayerStateGroup.sg_PlayerHeadHurl) {
					p = PlayerHeadHurl1;
				} else {
					p = PlayerDeadHead;
					shrap_xsize = shrap_ysize = 16 + 8;
					shrap_bounce = true;
				}
				break;
			}
			case GIRLNINJA_RUN_R0:
				p = StdShrap;
				break;
			case NINJA_RUN_R0: {
				p = StdShrap;
				if (pu.PlayerP != -1) {
	
					if (Player[pu.PlayerP].DeathType == PLAYER_DEATH_CRUMBLE)
						p = PlayerGoreFall;
					else
						p = PlayerGoreFly;
				}
				break;
			}
			case GORO_RUN_R0:
				p = StdShrap;
				shrap_xsize = shrap_ysize = 64;
				break;
			case COOLG_RUN_R0:
				p = UpperGore;
				break;
			case RIPPER_RUN_R0:
				p = StdShrap;
				if (pu.spal != 0)
					shrap_xsize = shrap_ysize = 64;
				else
					shrap_xsize = shrap_ysize = 32;
				break;
			case RIPPER2_RUN_R0:
				p = StdShrap;
				if (pu.spal != 0)
					shrap_xsize = shrap_ysize = 64;
				else
					shrap_xsize = shrap_ysize = 32;
				break;
			case SERP_RUN_R0:
				p = StdShrap;
				// return (false);
				break;
			case SUMO_RUN_R0:
				p = StdShrap;
				break;
			case SKEL_RUN_R0:
				p = SkelGore;
				shrap_pal = PALETTE_SKEL_GORE;
				break;
			case HORNET_RUN_R0:
				p = SmallGore;
				shrap_pal = PALETTE_SKEL_GORE;
				break;
			case SKULL_R0:
			case SKULL_R0 + 1:
				p = FlamingGore;
				break;
			case SKULL_SERP:
				return (false);
			case BETTY_R0:
			case TRASHCAN:
			case PACHINKO1:
			case PACHINKO2:
			case PACHINKO3:
			case PACHINKO4:
			case 623:
				PlaySound(DIGI_BREAKGLASS, parent, v3df_dontpan | v3df_doppler);
				p = MetalShrap;
				shrap_xsize = shrap_ysize = 10;
				break;
			case ZILLA_RUN_R0:
				p = MetalShrap;
				shrap_xsize = shrap_ysize = 10;
				break;
			case EMP:
				p = EMPShrap;
				shrap_xsize = shrap_ysize = 8;
				shrap_bounce = false;
				break;
			}
		}

		// second sprite involved
		// most of the time is is the weapon
		if (Secondary >= 0) {
			USER wu = pUser[Secondary];

			if (wu.PlayerP != -1 && Player[wu.PlayerP].sop_control != -1) {
				p = StdShrap;
			} else
				switch (wu.ID) {
				case PLASMA_FOUNTAIN:
					p = HeartAttackShrap;
					break;
				}
		}

		short dang = 0;

		hz[Z_TOP] = SPRITEp_TOS(parent); // top
		hz[Z_BOT] = SPRITEp_BOS(parent); // bottom
		hz[Z_MID] = DIV2(hz[0] + hz[2]); // mid

		for (int ptr = 0; ptr < p.length && p[ptr] != null; ptr++) {
			if (!p[ptr].random_disperse) {
				// dang = (2048 / p.num);
				start_ang = NORM_ANGLE(shrap_ang - DIV2(p[ptr].ang_range));
				dang = (short) (p[ptr].ang_range / p[ptr].num);
			}

			for (i = 0; i < p[ptr].num; i++) {
				SpriteNum = (short) SpawnSprite(STAT_SKIP4, p[ptr].id, p[ptr].state[0], parent.sectnum, parent.x,
						parent.y, hz[p[ptr].zlevel], shrap_ang, 512);

				sp = sprite[SpriteNum];
				u = pUser[SpriteNum];

				if (p[ptr].random_disperse) {
					sp.ang = (short) (shrap_ang + (RANDOM_P2(p[ptr].ang_range << 5) >> 5) - DIV2(p[ptr].ang_range));
					sp.ang = NORM_ANGLE(sp.ang);
				} else {
					sp.ang = (short) (start_ang + (i * dang));
					sp.ang = NORM_ANGLE(sp.ang);
				}

				// for FastShrap
				u.zchange = klabs(u.jump_speed * 4) - RANDOM_RANGE(klabs(u.jump_speed) * 8) * 2;
				u.WaitTics = (short) (WaitTics + RANDOM_RANGE(WaitTics / 2));

				switch (u.ID) {
				case GORE_Drip:
					shrap_bounce = false;
					break;
				case GORE_Lung:
					shrap_xsize = 20;
					shrap_ysize = 20;
					shrap_bounce = false;
					break;
				case GORE_Liver:
					shrap_xsize = 20;
					shrap_ysize = 20;
					shrap_bounce = false;
					break;
				case GORE_SkullCap:
					shrap_xsize = 24;
					shrap_ysize = 24;
					shrap_bounce = true;
					break;
				case GORE_Arm:
					shrap_xsize = 21;
					shrap_ysize = 21;
					shrap_bounce = false;
					break;
				case GORE_Head:
					shrap_xsize = 26;
					shrap_ysize = 30;
					shrap_bounce = true;
					break;
				case Vomit1:
					shrap_bounce = false;
					sp.z -= Z(4);
					shrap_xsize = (short) (u.sx = 12 + (RANDOM_P2(32 << 8) >> 8));
					shrap_ysize = (short) (u.sy = 12 + (RANDOM_P2(32 << 8) >> 8));
					u.Counter = (short) (RANDOM_P2(2048 << 5) >> 5);

					nx = sintable[NORM_ANGLE(sp.ang + 512)] >> 6;
					ny = sintable[sp.ang] >> 6;
					move_missile(SpriteNum, nx, ny, 0, Z(8), Z(8), CLIPMASK_MISSILE, MISSILEMOVETICS);

					if (RANDOM_P2(1024) < 700)
						u.ID = 0;

					break;
				case EMP:
					shrap_bounce = false;
					sp.z -= Z(4);
					// sp.ang = NORM_ANGLE(sp.ang + 1024);
					shrap_xsize = (short) (u.sx = 5 + (RANDOM_P2(4 << 8) >> 8));
					shrap_ysize = (short) (u.sy = 5 + (RANDOM_P2(4 << 8) >> 8));
					break;
				}

				sp.shade = (byte) shrap_shade;
				sp.xrepeat = shrap_xsize;
				sp.yrepeat = shrap_ysize;
				sp.clipdist = 16 >> 2;

				if (shrap_owner >= 0) {
					SetOwner(shrap_owner, SpriteNum);
				}

				if (shrap_rand_zamt != 0) {
					sp.z += Z(RANDOM_RANGE(shrap_rand_zamt) - (shrap_rand_zamt / 2));
				}

				sp.pal = u.spal = (byte) shrap_pal;

				sp.xvel = (short) (p[ptr].min_vel * 2);
				sp.xvel += RANDOM_RANGE(p[ptr].max_vel - p[ptr].min_vel);

				u.floor_dist = (short) shrap_floor_dist;
				u.ceiling_dist = (short) shrap_ceiling_dist;
				u.jump_speed = p[ptr].min_jspeed;
				u.jump_speed += RANDOM_RANGE(p[ptr].max_jspeed - p[ptr].min_jspeed);
				u.jump_speed = (short) -u.jump_speed;

				DoBeginJump(SpriteNum);
				u.jump_grav = jump_grav;

				u.xchange = MOVEx(sp.xvel, sp.ang);
				u.ychange = MOVEy(sp.xvel, sp.ang);

				if (!shrap_bounce)
					u.Flags |= (SPR_BOUNCE);
			}
		}

		return (retval);
	}

}
