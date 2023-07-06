package ru.m210projects.Blood;

import static ru.m210projects.Blood.AI.AIUNICULT.*;
import static ru.m210projects.Blood.AI.AIBURN.*;
import static ru.m210projects.Blood.AI.AICULTIST.*;
import static ru.m210projects.Blood.AI.AIGILLBEAST.*;
import static ru.m210projects.Blood.AI.AIZOMBA.*;
import static ru.m210projects.Blood.AI.AIZOMBF.*;
import static ru.m210projects.Blood.AI.Ai.*;
import static ru.m210projects.Blood.DB.gProxySpritesCount;
import static ru.m210projects.Blood.DB.gProxySpritesList;
import static ru.m210projects.Blood.DB.gSightSpritesCount;
import static ru.m210projects.Blood.DB.gSightSpritesList;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.Trigger.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Gib.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Strings.*;
import static ru.m210projects.Blood.Tile.*;
import static ru.m210projects.Blood.Trig.*;
import static ru.m210projects.Blood.Types.DudeInfo.*;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Warp.*;
import ru.m210projects.Blood.Types.SPRITEMASS;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.isValidSector;

import ru.m210projects.Blood.Types.AMMOITEMDATA;
import ru.m210projects.Blood.Types.ARMORITEMDATA;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.EFFECT;
import ru.m210projects.Blood.Types.EXPLODE;
import ru.m210projects.Blood.Types.ITEMDATA;
import ru.m210projects.Blood.Types.MissileType;
import ru.m210projects.Blood.Types.POSTPONE;
import ru.m210projects.Blood.Types.POWERUPINFO;
import ru.m210projects.Blood.Types.SPRITEHIT;
import ru.m210projects.Blood.Types.SURFACE;
import ru.m210projects.Blood.Types.THINGINFO;
import ru.m210projects.Blood.Types.VECTORDATA;
import ru.m210projects.Blood.Types.WEAPONITEMDATA;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Blood.Types.Seq.SeqInst;
import ru.m210projects.Blood.Types.Seq.SeqType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.Hitscan;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Build.Types.Tile;

public class Actor {

	public static int gTNTCount;

	public static final int kAttrMove = 0x0001; // is affected by movement physics
	public static final int kAttrGravity = 0x0002; // is affected by gravity
	public static final int kAttrFalling = 0x0004; // in z motion
	public static final int kAttrAiming = 0x0008;
	public static final int kAttrRespawn = 0x0010;
	public static final int kAttrFree = 0x0020;
	public static final int kAttrSmoke = 0x0100; // receives tsprite smoke/steam

	public static final int kAttrFlipX = 0x0400;
	public static final int kAttrFlipY = 0x0800;

	public static final int kDamageFall = 0;
	public static final int kDamageBurn = 1;
	public static final int kDamageBullet = 2;
	public static final int kDamageExplode = 3;
	public static final int kDamageDrown = 4;
	public static final int kDamageSpirit = 5;
	public static final int kDamageTesla = 6;
	public static final int kDamageMax = 7;

	public static int gNoEnemies = 0;

	public static SPRITEHIT[] gSpriteHit = new SPRITEHIT[kMaxXSprites];
	public static int[] gWallExp = new int[kMaxWalls];
	public static int[] gSectorExp = new int[kMaxSectors];
	public static byte[] gSpriteExp = new byte[(kMaxSectors + 7) >> 3];

	public static long[] sprXVel = new long[kMaxSprites];
	public static long[] sprYVel = new long[kMaxSprites];
	public static long[] sprZVel = new long[kMaxSprites];

	public static long[] floorVel = new long[kMaxSectors];
	public static long[] ceilingVel = new long[kMaxSectors];

	public static final int[] pSkillShift = { 512, 384, 256, 208, 160 };
	public static final int[] pPlayerShift = { 144, 208, 256, 304, 368 };

	public static final int kScreamVel = 0x155555; // zvel at which player
													// screams
	public static final int kGruntVel = 700;
	public static final int kDudeDrag = 0x2A00;
	public static final int kMinDudeVel = 4096;

	public static int gPostCount = 0;
	public static POSTPONE[] gPost = new POSTPONE[kMaxSprites];

	public static final int kPowerUpTime = (kTimerRate * 30);
	public static final int kMaxPowerUpTime = ((kTimerRate * 60) * 60);

	public static final int kVectorTine = 0;
	public static final int kVectorShell = 1;
	public static final int kVectorBullet = 2;
	// 3
	// 4
	// 5
	public static final int kVectorBatBite = 6;
	public static final int kVectorBoneelBite = 7;
	// 8
	// 9
	public static final int kVectorAxe = 10;
	public static final int kVectorCleaver = 11;
	public static final int kVectorGhost = 12;
	public static final int kVectorGargSlash = 13;
	public static final int kVectorCerberusHack = 14;
	public static final int kVectorHoundBite = 15;
	public static final int kVectorRatBite = 16;
	public static final int kVectorSpiderBite = 17;
	// 18
	// 19
	// 20
	// 21
	public static final int kVectorMax = 23;

	public static SPRITEMASS[] gSpriteMass = new SPRITEMASS[kMaxSprites];

	public static final VECTORDATA[] gVectorData = { // 1.21 ok
			new VECTORDATA(2, 17, 174762, 1152, 10240, 0, 1, 20480, // 0 kVectorTine
					new SURFACE[] {
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(43, 6, -1, 502),
							new SURFACE(43, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, 7, 502),
							new SURFACE(43, 6, 7, 502),
							new SURFACE(-1, -1, -1, 503),
							new SURFACE(43, -1, -1, -1),
							new SURFACE(-1, 6, -1, 503),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1)
							},
					new int[] {1207, 1207}),

			new VECTORDATA(2, 4, 65536, 0, 8192, 0, 1, 12288, // 1 kVectorShell
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, 5, -1, -1),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(43, 6, -1, -1),
							new SURFACE(43, 0, -1, -1),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(43, 6, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, -1, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1)},
							new int[] {1001, 1001}),

			new VECTORDATA(2, 7, 21845, 0, 32768, 0, 1, 12288, // 2 kVectorBullet
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, 5, 7, 510),
							new SURFACE(-1, 5, 7, 511),
							new SURFACE(43, 6, -1, 512),
							new SURFACE(43, 0, -1, 513),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, 7, 512),
							new SURFACE(43, 6, 7, 512),
							new SURFACE(-1, -1, -1, 513),
							new SURFACE(43, -1, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1) },
							new int[] {4001, 4002}),

			new VECTORDATA(2, 20, 65536, 0, 16384, 0, 1, 20480, // 3
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, 5, 7, 510),
							new SURFACE(-1, 5, 7, 511),
							new SURFACE(43, 6, -1, 512),
							new SURFACE(43, 0, -1, 513),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, 7, 512),
							new SURFACE(43, 6, 7, 512),
							new SURFACE(-1, -1, -1, 513),
							new SURFACE(43, -1, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1) },
							new int[] {431, 431}),

			new VECTORDATA(2, 6, 87381, 0, 12288, 0, 1, 6144, // 4
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, 5, -1, -1),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(43, 6, -1, -1),
							new SURFACE(43, 0, -1, -1),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(43, 6, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, -1, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1) },
							new int[] {1002, 1002}),

			new VECTORDATA(2, 12, 65536, 0, 16384, 0, 1, 12288, // 5
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(43, 5, 7, 510),
							new SURFACE(-1, 5, 7, 511),
							new SURFACE(43, 6, -1, 512),
							new SURFACE(43, 0, -1, 513),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, 7, 512),
							new SURFACE(43, 6, 7, 512),
							new SURFACE(-1, -1, -1, 513),
							new SURFACE(43, -1, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, 6, -1, 513),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1) },
							new int[] {359, 359}),

			new VECTORDATA(2, 4, 0, 921, 0, 0, 1, 4096, // 6 kVectorBatBite
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1),
							new SURFACE(-1, 5, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {521, 521}),

			new VECTORDATA(2, 12, 0, 1177, 0, 0, 0, 0, // 7 kVectorBoneelBite
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {513, 513}),

			new VECTORDATA(2, 9, 0, 1177, 0, 0, 0, 0, // 8
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {499, 499}),

			new VECTORDATA(3, 50, 43690, 1024, 8192, 0, 4, 32768, // 9
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {9012, 9014}),

			new VECTORDATA(2, 18, 436906, 1024, 16384, 0, 2, 20480, // 10
																	// kVectorAxe
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1)},
							new int[] {1101, 1101}),

			new VECTORDATA(2, 9, 218453, 1024, 0, 0, 1, 24576, // 11
																// kVectorCleaver
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1)},
							new int[] {1207, 1207}),

			new VECTORDATA(2, 20, 436906, 1024, 16384, 0, 3, 24576, // 12
																	// kVectorGhost
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1)},
							new int[] {499, 495}),

			new VECTORDATA(2, 16, 218453, 1024, 8192, 0, 4, 20480, // 13
																	// kVectorGargSlash
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1)},
							new int[] {495, 496}),

			new VECTORDATA(2, 19, 218453, 614, 8192, 0, 2, 24576, // 14
																	// kVectorCerberusHack
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {9013, 499}),

			new VECTORDATA(2, 10, 218453, 614, 8192, 0, 2, 24576, // 15
																	// kVectorHoundBite
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {1307, 1308}),

			new VECTORDATA(2, 4, 0, 921, 0, 0, 1, 24576, // 16 kVectorRatBite
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, -1),
							new SURFACE(-1, 5, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {499, 499}),

			new VECTORDATA(2, 8, 0, 614, 0, 0, 1, 24576, // 17 kVectorSpiderBite
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {499, 499}),

			new VECTORDATA(2, 9, 0, 512, 0, 0, 0, 0, // 18
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, 5, -1, 500),
							new SURFACE(-1, 5, -1, 501),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 0, -1, 503),
							new SURFACE(-1, 4, -1, -1),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, 6, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 502),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {499, 499}),

			new VECTORDATA(-1, 0, 0, 2560, 0, 0, 0, 0, // 19
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, '\"', '#', -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {0, 0}),

			new VECTORDATA(1, 2, 0, 0, 0, 15, 0, 0, // 20
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {351, 351}),

			new VECTORDATA(5, 25, 0, 0, 0, 0, 0, 0, // 21
					new SURFACE[] { new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, -1)},
							new int[] {0, 0}),

			new VECTORDATA(0, 37, 874762, 620, 0, 0, 0, 0, // 22 kVectorGenDudePunch
					new SURFACE[] {
							new SURFACE(-1, -1, -1, -1),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357),
							new SURFACE(-1, -1, -1, 357)},
							new int[] {357, 499}),

							};

	public static final int kExplodeMax = 9; // Max explosions
	public static final EXPLODE[] gExplodeData = { // 1.21 ok
	new EXPLODE(40, 10, 10, 75, 450, 0, 60, 80, 40),
			new EXPLODE(80, 20, 10, 150, 900, 0, 60, 160, 60),
			new EXPLODE(120, 40, 15, 225, 1350, 0, 60, 240, 80),
			new EXPLODE(80, 5, 10, 120, 20, 10, 60, 0, 40),
			new EXPLODE(120, 10, 10, 180, 40, 10, 60, 0, 80),
			new EXPLODE(160, 15, 10, 240, 60, 10, 60, 0, 120),
			new EXPLODE(40, 20, 10, 120, 0, 10, 30, 60, 40),
			new EXPLODE(80, 20, 10, 150, 800, 5, 60, 160, 60) };

	public static final MissileType[] gMissileData = { // 1.21 ok
			new MissileType(2138, 978670, 512, 40, 40, 240, 16, new int[] {1207, 1207}),
			new MissileType(2424, 3145728, 0, 32, 32, 128, 32, new int[] {420, 420}),
			new MissileType(3056, 2796202, 0, 32, 32, 128, 32, new int[] {471, 471}),
			new MissileType(2424, 2446677, 0, 32, 32, 128, 4, new int[] {421, 421}),
			new MissileType(0, 1118481, 0, 24, 24, 128, 16, new int[] {1309, 351}),
			new MissileType(0, 1118481, 0, 32, 32, 128, 32, new int[] {480, 480}),
			new MissileType(2130, 2796202, 0, 32, 32, 128, 16, new int[] {470, 470}),
			new MissileType(870, 699050, 0, 32, 32, 232, 32, new int[] {489, 490}),
			new MissileType(0, 1118481, 0, 24, 24, 128, 16, new int[] {462, 351}),
			new MissileType(0, 838860, 0, 16, 16, 240, 16, new int[] {1203, 172}),
			new MissileType(0, 838860, 0, 8, 8, 0, 16, new int[] {0, 0}),
			new MissileType(3056, 2097152, 0, 32, 32, 128, 16, new int[] {1457, 249}),
			new MissileType(0, 2446677, 0, 30, 30, 128, 24, new int[] {480, 489}),
			new MissileType(0, 2446677, 0, 30, 30, 128, 24, new int[] {489, 480}),
			new MissileType(0, 1398101, 0, 24, 24, 128, 16, new int[] {480, 489}),
			new MissileType(2446, 2796202, 0, 32, 32, 128, 16, new int[] {491, 491}),
			new MissileType(3056, 2446677, 0, 16, 16, 128, 16, new int[] {520, 520}),
			new MissileType(3056, 1747626, 0, 32, 32, 128, 16, new int[] {520, 250}), };

	public static final int kFXMax = 57;
	public static final EFFECT[] gEffectInfo = { // 1.21 ok
			new EFFECT(-1, 0, 4107, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 4108, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 4109, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 4110, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 7, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 4102, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 4103, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 0, 4104, 1, -128, 8192, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 2, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 2, 4099, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 2, 4100, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 10
			new EFFECT(-1, 1, 4106, 3, -256, 8192, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 4118, 3, -256, 8192, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(14, 2, 0, 1, 46603, 2048, 480, 2154, 40, 40, 0, 244, 0),
			new EFFECT(-1, 2, 0, 3, 46603, 5120, 480, 2269, 24, 24, 0, 128, 0),
			new EFFECT(-1, 2, 0, 3, 46603, 5120, 480, 1720, 24, 24, 0, 128, 0),
			new EFFECT(-1, 1, 0, 1, 58254, 3072, 480, 2280, 48, 48, 0, 128, 0),
			new EFFECT(-1, 1, 0, 1, 58254, 3072, 480, 3135, 48, 48, 0, 128, 0),
			new EFFECT(-1, 0, 0, 3, 58254, 1024, 480, 3261, 32, 32, 0, 0, 0),
			new EFFECT(-1, 1, 0, 3, 58254, 1024, 480, 3265, 32, 32, 0, 0, 0),
			new EFFECT(-1, 1, 0, 3, 58254, 1024, 480, 3269, 32, 32, 0, 0, 0), // 20
			new EFFECT(-1, 1, 0, 3, 58254, 1024, 480, 3273, 32, 32, 0, 0, 0),
			new EFFECT(-1, 1, 0, 3, 58254, 1024, 480, 3277, 32, 32, 0, 0, 0),
			new EFFECT(-1, 2, 0, 1, -27962, 8192, 600, 1128, 16, 16, 514, 240, 0),
			new EFFECT(-1, 2, 0, 1, -18641, 8192, 600, 1128, 12, 12, 514, 240, 0),
			new EFFECT(-1, 2, 0, 1, -9320, 8192, 600, 1128, 8, 8, 514, 240, 0),
			new EFFECT(-1, 2, 0, 1, -18641, 8192, 600, 1131, 32, 32, 514, 240, 0),
			new EFFECT(14, 2, 0, 3, 27962, 4096, 480, 733, 32, 32, 0, 240, 0),
			new EFFECT(-1, 1, 0, 3, 18641, 4096, 120, 2261, 12, 12, 0, 128, 0),
			new EFFECT(-1, 0, 4105, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 0, 3, 58254, 3328, 480, 2185, 48, 48, 0, 0, 0), // 30
			new EFFECT(-1, 0, 0, 3, 58254, 1024, 480, 2620, 48, 48, 0, 0, 0),
			new EFFECT(-1, 1, 4113, 1, -13981, 5120, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 4114, 1, -13981, 5120, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 4115, 1, 0, 2048, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 4116, 1, 0, 2048, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 2, 0, 0, 0, 0, 960, 956, 32, 32, 610, 0, 0),
			new EFFECT(16, 2, 4120, 0, 46603, 1024, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(16, 2, 4121, 0, 46603, 1024, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(16, 2, 4122, 0, 46603, 1024, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(16, 2, 4123, 0, 46603, 1024, 0, 0, 0, 0, 0, 0, 0), // 40
			new EFFECT(16, 2, 4124, 0, 46603, 1024, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(16, 2, 4125, 0, 46603, 1024, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 0, 3, 0, 0, 0, 838, 16, 16, 80, 248, 0), // BullerHole
			new EFFECT(-1, 0, 0, 3, 34952, 8192, 0, 2078, 64, 64, 0, 248, 0),
			new EFFECT(-1, 0, 0, 3, 34952, 8192, 0, 1106, 64, 64, 0, 248, 0),
			new EFFECT(-1, 0, 0, 3, 58254, 3328, 480, 2406, 48, 48, 0, 0, 0),
			new EFFECT(-1, 1, 0, 3, 46603, 4096, 480, 3511, 64, 64, 0, 128, 0),
			new EFFECT(-1, 0, 8, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 2, 11, 3, -256, 8192, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 2, 11, 3, 0, 8192, 0, 0, 0, 0, 0, 0, 0), // 50
			new EFFECT(-1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
			new EFFECT(-1, 1, 30, 3, 0, 0, 0, 0, 40, 40, 80, 248, 0), // 52
			new EFFECT(19, 2, 0, 3, 27962, 4096, 480, 4023, 32, 32, 0, 240, 0),
			new EFFECT(19, 2, 0, 3, 27962, 4096, 480, 4028, 32, 32, 0, 240, 0),
			new EFFECT(-1, 2, 0, 0, 0, 0, 480, 926, 32, 32, 610, 244, 0),
			new EFFECT(-1, 1, 4113, 1, -13981, 5120, 0, 0, 0, 0, 0, 0, 0) };

	public static final THINGINFO[] thingInfo = { // 1.21 ok
		/*0*/new THINGINFO(25, 250, 32, 11, 4096, 80, 384, 907, 0, 0, 0, 0, new int[] { 256, 256, 128, 64, 0, 0, 128 },1), // kThingTNTBarrel
		/*1*/new THINGINFO(5, 5, 16, 3, 24576, 1600, 256, 3444, 240, 0, 32, 32, new int[] { 256, 256, 256, 64, 0, 0, 512 },1), // kThingTNTProxArmed
		/*2*/new THINGINFO(5, 5, 16, 3, 24576, 1600, 256, 3457, 240, 0, 32, 32, new int[] { 256, 256, 256, 64, 0, 0, 512 },1), // kThingTNTRemArmed
		/*3*/new THINGINFO(1, 20, 32, 3, 32768, 80, 0, 739, 0, 0, 0, 0, new int[] { 256, 0, 256, 128, 0, 0, 0 },0), // kThingBlueVase
		/*4*/new THINGINFO(1, 150, 32, 3, 32768, 80, 0, 642, 0, 0, 0, 0, new int[] { 256, 256, 256, 128, 0, 0, 0 },0), // kThingBrownVase
		/*5*/new THINGINFO(10, 0, 0, 0, 0, 0, 0, 462, 0, 0, 0, 0, new int[] { 0, 0, 0, 256, 0, 0, 0 },0), // kThingCrateFace
		/*6*/new THINGINFO(1, 0, 0, 0, 0, 0, 0, 266, 0, 0, 0, 0, new int[] { 256, 0, 256, 256, 0, 0, 0 },0), // kThingClearGlass
		/*7*/new THINGINFO(1, 0, 0, 0, 0, 0, 0, 796, 0, 0, 0, 0, new int[] { 256, 0, 256, 256, 0, 0, 512 },0), // kThingFluorescent
		/*8*/new THINGINFO(50, 0, 0, 0, 0, 0, 0, 1127, 0, 0, 0, 0, new int[] { 0, 0, 0, 256, 0, 0, 0 },0), //kThingWallCrack
		/*9*/new THINGINFO(8, 0, 0, 0, 0, 0, 0, 1142, 0, 0, 0, 0, new int[] { 256, 0, 256, 128, 0, 0, 0 },0), // kThingWoodBeam
		/*10*/new THINGINFO(4, 0, 0, 0, 0, 0, 0, 1069, 0, 0, 0, 0, new int[] { 256, 256, 64, 256, 0, 0, 128 },0), //kThingWeb 410
		/*11*/new THINGINFO(40, 0, 0, 0, 0, 0, 0, 483, 0, 0, 0, 0, new int[] { 64, 0, 128, 256, 0, 0, 0 },0), // kThingMetalGrate1
		/*12*/new THINGINFO(1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, new int[] { 0, 256, 0, 256, 0, 0, 128 },0), //kThingFlammableTree
		/*13*/new THINGINFO(1000, 0, 0, 8, 0, 0, 0, -1, 0, 0, 0, 0, new int[] { 0, 0, 128, 256, 0, 0, 512 },0), // kThingMachineGun
		/*14*/new THINGINFO(0, 15, 8, 3, 32768, 0, 0, -1, 0, 0, 0, 0, new int[] { 0, 0, 0, 0, 0, 0, 0 },0), // kThingFallingRock
		/*15*/new THINGINFO(0, 8, 48, 3, 49152, 0, 0, 3540, 0, 0, 0, 0, new int[] { 0, 0, 0, 0, 0, 0, 0 },1), // kThingPail
		/*16*/new THINGINFO(10, 2, 0, 0, 32768, 0, 0, -1, 0, 0, 0, 0, new int[] { 256, 0, 256, 256, 0, 0, 128 },0), // kThingGibObject
		/*17*/new THINGINFO(20, 2, 0, 0, 32768, 0, 0, -1, 0, 0, 0, 0, new int[] { 0, 0, 0, 256, 0, 0, 128 },0), //kThingExplodeObject
		/*18*/new THINGINFO(5, 14, 16, 3, 24576, 1600, 256, 3422, 224, 0, 32, 32, new int[] { 64, 256, 128, 64, 0, 0, 256 },1), //kThingTNTStick
		/*19*/new THINGINFO(5, 14, 16, 3, 24576, 1600, 256, 3433, 224, 0, 32, 32, new int[] { 64, 256, 128, 64, 0, 0, 256 },1), // kThingTNTBundle
		/*20*/new THINGINFO(5, 14, 16, 3, 32768, 1600, 256, 3467, 128, 0, 32, 32, new int[] { 64, 256, 128, 64, 0, 0, 256 },1), // SprayBundle 420
		/*21*/new THINGINFO(5, 6, 16, 3, 32768, 1600, 256, 1462, 0, 0, 32, 32, new int[] { 0, 0, 0, 0, 0, 0, 0 },1), // kThingBoneClub
		/*22*/new THINGINFO(8, 3, 16, 11, 32768, 1600, 256, -1, 0, 0, 0, 0, new int[] { 256, 0, 256, 256, 0, 0, 0 },0), //kThingZombieBones
		/*23*/new THINGINFO(0, 1, 1, 2, 0, 0, 0, 1147, 0, 10, 0, 0, new int[] { 0, 0, 0, 0, 0, 0, 0 },0), // kThingWaterDrip
		/*24*/new THINGINFO(0, 1, 1, 2, 0, 0, 0, 1160, 0, 2, 0, 0, new int[] { 0, 0, 0, 0, 0, 0, 0 },0), // kThingBloodDrip
		/*25*/new THINGINFO(15, 4, 4, 3, 24576, 0, 257, -1, 0, 0, 0, 0, new int[] { 128, 64, 256, 256, 0, 0, 256 },0), //425
		/*26*/new THINGINFO(30, 30, 8, 3, 8192, 0, 257, -1, 0, 0, 0, 0, new int[] { 128, 64, 256, 256, 0, 0, 64 },0), // 426 kThingGib
		/*27*/new THINGINFO(60, 5, 32, 3, 40960, 1280, 257, 3405, 0, 0, 40, 40, new int[] { 128, 64, 256, 256, 0, 0, 64 },1), //ok kThingZombieHead
		/*28*/new THINGINFO(80, 30, 32, 3, 57344, 1600, 256, 3281, 128, 0, 32, 32, new int[] { 0, 0, 0, 0, 0, 0, 0 },1), //428
		/*29*/new THINGINFO(80, 30, 32, 3, 57344, 1600, 256, 2020, 128, 0, 32, 32, new int[] { 256, 0, 256, 256, 0, 0, 0 },1), // kThingPodFire
		/*30*/new THINGINFO(80, 30, 32, 3, 57344, 1600, 256, 1860, 128, 0, 32, 32, new int[] { 256, 0, 256, 256, 0, 0, 0 },1), // kThingPodGreen
		/*31*/new THINGINFO(150, 30, 48, 3, 32768, 1600, 257, 800, 128, 0, 48, 48, new int[] { 64, 64, 112, 64, 0, 96, 96 },1), // 431
		/*32*/new THINGINFO(1, 30, 48, 3, 0, 32768, 1600, 0, 2443, 128, 16, 16, new int[] { 0, 0, 0, 0, 0, 0, 0 },0), // 432
		// GDX THINGS
		/*33*/new THINGINFO(5, 5, 16, 3, 24576, 1600, 256, 3444, 240, 7, 32, 32, new int[] { 256, 256, 256, 64, 0, 0, 512 },1), // kThingTNTProxArmed2 (Detects only players)
		/*34*/new THINGINFO(5, 6, 16, 3, 32768, 1600, 256, 1462, 0, 0, 32, 32, new int[] { 0, 0, 0, 0, 0, 0, 0 },1), // kThingThrowableRock (similar to kThingBoneClub)
		/*35*/new THINGINFO(150, 30, 48, 3, 32768, 1600, 257, 800, 128, 0, 48, 48, new int[] { 64, 64, 112, 64, 0, 96, 96 },1), // kThingCustomDudeLifeLeech
		/*36 new THINGINFO(150, 30, 48, 3, 32768, 1600, 257, 3833, 128, 0, 48, 48, new int[] { 64, 64, 112, 64, 0, 96, 96 },0),*/ // kGDXThingCalebHat
	};

	public static final AMMOITEMDATA[] gAmmoItemData = {
			new AMMOITEMDATA(0, 618, -8, 0, 48, 48, 480, 6, 7),
			new AMMOITEMDATA(0, 589, -8, 0, 48, 48, 1, 5, 6),
			new AMMOITEMDATA(0, 589, -8, 0, 48, 48, 1, 5, 6),
			new AMMOITEMDATA(0, 809, -8, 0, 48, 48, 5, 5, 6),
			new AMMOITEMDATA(0, 811, -8, 0, 48, 48, 1, 10, 11),
			new AMMOITEMDATA(0, 810, -8, 0, 48, 48, 1, 11, 12),
			new AMMOITEMDATA(0, 820, -8, 0, 24, 24, 10, 8, 0),
			new AMMOITEMDATA(0, 619, -8, 0, 48, 48, 4, 2, 0),
			new AMMOITEMDATA(0, 812, -8, 0, 48, 48, 15, 2, 0),
			new AMMOITEMDATA(0, 813, -8, 0, 48, 48, 15, 3, 0),
			new AMMOITEMDATA(0, 525, -8, 0, 48, 48, 100, 9, 10),
			new AMMOITEMDATA(0, 814, -8, 0, 48, 48, 15, 255, 0),
			new AMMOITEMDATA(0, 817, -8, 0, 48, 48, 100, 3, 0),
			new AMMOITEMDATA(0, 548, -8, 0, 24, 24, 32, 7, 0),
			new AMMOITEMDATA(0, 0, -8, 0, 48, 48, 6, 255, 0),
			new AMMOITEMDATA(0, 0, -8, 0, 48, 48, 6, 255, 0),
			new AMMOITEMDATA(0, 816, -8, 0, 48, 48, 8, 1, 0),
			new AMMOITEMDATA(0, 818, -8, 0, 48, 48, 8, 255, 0),
			new AMMOITEMDATA(0, 819, -8, 0, 48, 48, 8, 255, 0),
			new AMMOITEMDATA(0, 801, -8, 0, 48, 48, 6, 4, 0),
			new AMMOITEMDATA(0, 0, 0, 0, 0, 0, 0, 0, 0) };

	public static final WEAPONITEMDATA[] gWeaponItemData = {
			new WEAPONITEMDATA(0, 4096, 0, 0, 0, 0, 0, -1, 0), // None
			new WEAPONITEMDATA(0, 559, -8, 0, 48, 48, 3, 2, 8), // Shotgun
			new WEAPONITEMDATA(0, 558, -8, 0, 48, 48, 4, 3, 50), // Tommy
			new WEAPONITEMDATA(0, 524, -8, 0, 48, 48, 2, 1, 9), // Flare
			new WEAPONITEMDATA(0, 525, -8, 0, 48, 48, 10, 9, 100), // Voodoo
			new WEAPONITEMDATA(0, 539, -8, 0, 48, 48, 8, 7, 64), // Tesla
			new WEAPONITEMDATA(0, 526, -8, 0, 48, 48, 5, 4, 6), // Napalm
			new WEAPONITEMDATA(0, 4096, 0, 0, 0, 0, 1, -1, 0), // Reserved
			new WEAPONITEMDATA(0, 618, -8, 0, 48, 48, 7, 6, 480), // SprayCan
			new WEAPONITEMDATA(0, 589, -8, 0, 48, 48, 6, 5, 1), // TNT
			new WEAPONITEMDATA(0, 800, -8, 0, 48, 48, 9, 8, 35) // LifeLeach
	};

	public static final ARMORITEMDATA[] gArmorItemData = {
			new ARMORITEMDATA(new int[] { 800, 800, 800 }, new int[] { 1600, 1600, 1600 }),
			new ARMORITEMDATA(new int[] { 1600, 0, 0 }, new int[] { 1600, 1600, 1600 }),
			new ARMORITEMDATA(new int[] { 0, 1600, 0 }, new int[] { 1600, 1600, 1600 }),
			new ARMORITEMDATA(new int[] { 0, 0, 1600 }, new int[] { 1600, 1600, 1600 }),
			new ARMORITEMDATA(new int[] { 3200, 3200, 3200 }, new int[] { 3200, 3200, 3200 }), };

	public static final POWERUPINFO[] gPowerUpInfo = {
			new POWERUPINFO(-1, true, 1, 1), // kItemKey1
			new POWERUPINFO(-1, true, 1, 1), // kItemKey2
			new POWERUPINFO(-1, true, 1, 1), // kItemKey3
			new POWERUPINFO(-1, true, 1, 1), // kItemKey4
			new POWERUPINFO(-1, true, 1, 1), // kItemKey5
			new POWERUPINFO(-1, true, 1, 1), // kItemKey6
			new POWERUPINFO(-1, true, 1, 1), // kItemKey7
			new POWERUPINFO(-1, false, 100, 100), // kItemDoctorBag
			new POWERUPINFO(-1, false, 50, 100), // kItemMedPouch
			new POWERUPINFO(-1, false, 20, 100), // kItemLifeEssence
			new POWERUPINFO(-1, false, 100, 200), // kItemLifeSeed
			new POWERUPINFO(-1, false, 2, 200), // kItemPotion1
			new POWERUPINFO(783, false, kPowerUpTime, kMaxPowerUpTime), // kItemFeatherFall
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemLtdInvisibility
			new POWERUPINFO(-1, true, kPowerUpTime, kMaxPowerUpTime), // kItemInvulnerability
			new POWERUPINFO(827, false, kPowerUpTime, kMaxPowerUpTime), // kItemJumpBoots
			new POWERUPINFO(828, false, kPowerUpTime, kMaxPowerUpTime), // kItemRavenFlight
			new POWERUPINFO(829, false, kPowerUpTime, 4 * kMaxPowerUpTime), // kItemGunsAkimbo
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemDivingSuit
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemGasMask
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemClone
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemCrystalBall
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemDecoy
			new POWERUPINFO(851, false, kPowerUpTime, kMaxPowerUpTime), // kItemDoppleganger
			new POWERUPINFO(2428, false, kPowerUpTime, kMaxPowerUpTime), // kItemReflectiveShots
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemBeastVision
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemShadowCloak
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemShroomRage
			new POWERUPINFO(-1, false, kPowerUpTime / 4, kMaxPowerUpTime), // kItemShroomDelirium
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemShroomGrow
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemShroomShrink
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemDeathMask
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemWineGoblet
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemWineBottle
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemSkullGrail
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemSilverGrail
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemTome
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemBlackChest
			new POWERUPINFO(-1, false, kPowerUpTime, kMaxPowerUpTime), // kItemWoodenChest
			new POWERUPINFO(-1, true, kPowerUpTime, kMaxPowerUpTime), // kItemAsbestosArmor 139
			new POWERUPINFO(-1, false, 1, kMaxPowerUpTime),
			new POWERUPINFO(-1, false, 1, kMaxPowerUpTime),
			new POWERUPINFO(-1, false, 1, kMaxPowerUpTime),
			new POWERUPINFO(-1, false, 1, kMaxPowerUpTime),
			new POWERUPINFO(-1, false, 1, kMaxPowerUpTime),
			new POWERUPINFO(0, false, 0, 0), new POWERUPINFO(0, false, 0, 0),
			new POWERUPINFO(0, false, 0, 0), new POWERUPINFO(0, false, 0, 0),
			new POWERUPINFO(-1, false, 1, kMaxPowerUpTime), // kItemJetpack
			//new POWERUPINFO(-1, true, 1, 1), // kGDXItemMap
	};

	public static final ITEMDATA[] gItemInfo = {
			new ITEMDATA(-1, 2552, -8, 0, 32, 32),
			new ITEMDATA(-1, 2553, -8, 0, 32, 32),
			new ITEMDATA(-1, 2554, -8, 0, 32, 32),
			new ITEMDATA(-1, 2555, -8, 0, 32, 32),
			new ITEMDATA(-1, 2556, -8, 0, 32, 32),
			new ITEMDATA(-1, 2557, -8, 0, 32, 32),
			new ITEMDATA(-1, 0, -8, 0, 0, 0),
			new ITEMDATA(0, 519, -8, 0, 48, 48),
			new ITEMDATA(-1, 822, -8, 0, 40, 40),
			new ITEMDATA(-1, 2169, -8, 0, 40, 40),
			new ITEMDATA(-1, 2433, -8, 0, 40, 40),
			new ITEMDATA(-1, 517, -8, 0, 40, 40),
			new ITEMDATA(-1, 783, -8, 0, 40, 40),
			new ITEMDATA(-1, 896, -8, 0, 40, 40),
			new ITEMDATA(-1, 825, -8, 0, 40, 40),
			new ITEMDATA(4, 827, -8, 0, 40, 40),
			new ITEMDATA(-1, 828, -8, 0, 40, 40),
			new ITEMDATA(6, 829, -8, 0, 40, 40),
			new ITEMDATA(1, 830, -8, 0, 80, 64),
			new ITEMDATA(-1, 831, -8, 0, 40, 40),
			new ITEMDATA(-1, 863, -8, 0, 40, 40),
			new ITEMDATA(2, 760, -8, 0, 40, 40),
			new ITEMDATA(-1, 836, -8, 0, 40, 40),
			new ITEMDATA(-1, 851, -8, 0, 40, 40),
			new ITEMDATA(-1, 2428, -8, 0, 40, 40),
			new ITEMDATA(3, 839, -8, 0, 40, 40),
			new ITEMDATA(-1, 768, -8, 0, 64, 64),
			new ITEMDATA(-1, 840, -8, 0, 48, 48),
			new ITEMDATA(-1, 841, -8, 0, 48, 48),
			new ITEMDATA(-1, 842, -8, 0, 48, 48),
			new ITEMDATA(-1, 843, -8, 0, 48, 48),
			new ITEMDATA(-1, 683, -8, 0, 40, 40),
			new ITEMDATA(-1, 521, -8, 0, 40, 40),
			new ITEMDATA(-1, 604, -8, 0, 40, 40),
			new ITEMDATA(-1, 520, -8, 0, 40, 40),
			new ITEMDATA(-1, 803, -8, 0, 40, 40),
			new ITEMDATA(-1, 518, -8, 0, 40, 40),
			new ITEMDATA(-1, 522, -8, 0, 40, 40),
			new ITEMDATA(-1, 523, -8, 0, 40, 40),
			new ITEMDATA(-1, 837, -8, 0, 80, 64),
			new ITEMDATA(-1, 2628, -8, 0, 64, 64),
			new ITEMDATA(-1, 2586, -8, 0, 64, 64),
			new ITEMDATA(-1, 2578, -8, 0, 64, 64),
			new ITEMDATA(-1, 2602, -8, 0, 64, 64),
			new ITEMDATA(-1, 2594, -8, 0, 64, 64),
			new ITEMDATA(-1, 753, -8, 0, 64, 64),
			new ITEMDATA(-1, 753, -8, 7, 64, 64),
			new ITEMDATA(-1, 3558, 128, 0, 64, 64),
			new ITEMDATA(-1, 3558, 128, 7, 64, 64) };

	public static void AirDrag(int nSprite, int nDrag) {
		int windX = 0, windY = 0;

		SPRITE pSprite = sprite[nSprite];

		int nSector = pSprite.sectnum;

		if (!(nSector >= 0 && nSector < kMaxSectors))
			game.dassert("nSector >= 0 && nSector < kMaxSectors");
		if (sector[nSector].extra > 0) {
			int nXSector = sector[nSector].extra;
			if (!(nXSector > 0 && nXSector < kMaxXSectors))
				game.dassert("nXSector > 0 && nXSector < kMaxXSectors");

			XSECTOR pXSector = xsector[nXSector];

			if (pXSector.windVel != 0
					&& (pXSector.windAlways || pXSector.busy != 0)) {
				int windVel = pXSector.windVel << 12;
				if (!pXSector.windAlways && pXSector.busy != 0)
					windVel = mulscale(pXSector.busy, windVel, 16);

				windX = mulscale(Cos(pXSector.windAng), windVel, 30);
				windY = mulscale(Sin(pXSector.windAng), windVel, 30);
			}
		}

		sprXVel[pSprite.xvel] += mulscale(nDrag, windX - sprXVel[pSprite.xvel], 16);
		sprYVel[pSprite.xvel] += mulscale(nDrag, windY - sprYVel[pSprite.xvel], 16);
		sprZVel[pSprite.xvel] -= mulscale(nDrag, sprZVel[pSprite.xvel], 16);
	}

	public static boolean IsNormalWall(int nWall) {
		if (!(nWall >= 0 && nWall < kMaxWalls))
			game.dassert("nWall >= 0 && nWall < kMaxWalls");
		WALL pWall = wall[nWall];
		int cstat = pWall.cstat;
		if ((cstat & kWallMoveForward) != 0) {
			return false;
		} else if ((cstat & kWallMoveBackward) != 0) {
			return false;
		} else {
			if (pWall.lotag < 500 || pWall.lotag >= 512) {
				if (pWall.nextsector != -1) {
					return sector[pWall.nextsector].lotag < kSectorBase
							|| sector[pWall.nextsector].lotag >= kSectorMax;
				}
				return true;
			} else
				return false;
		}
	}

	public static void actFireVector(SPRITE pActor, int xOffset, int zOffset,
			int dx, int dy, int dz, int vectorType) {
		int hitCode;

		if (!(vectorType >= 0 && vectorType < kVectorMax))
			game.dassert("vectorType >= 0 && vectorType < kVectorMax");
		VECTORDATA pVectorData = gVectorData[vectorType];

		int maxDist = pVectorData.maxDist;
		if ((hitCode = VectorScan(pActor, xOffset, zOffset, dx, dy, dz,
				maxDist, 1)) == SS_SPRITE) {
			if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
				game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
			SPRITE pSprite = sprite[pHitInfo.hitsprite];

			if (IsPlayerSprite(pSprite)
					&& powerupCheck(gPlayer[pSprite.lotag - kDudePlayer1],
							kItemReflectiveShots - kItemBase) != 0) {
				pHitInfo.hitsprite = pActor.xvel;
				pHitInfo.hitx = pActor.x;
				pHitInfo.hity = pActor.y;
				pHitInfo.hitz = pActor.z;
			}
		}

		// determine a point just a bit back from the intersection to place the ricochet
		int rx = pHitInfo.hitx - mulscale(dx, 1 << 4, 14);
		int ry = pHitInfo.hity - mulscale(dy, 1 << 4, 14);
		int rz = pHitInfo.hitz - mulscale(dz, 1 << 8, 14);

		int nSurf = kSurfNone;
		short nSector = pHitInfo.hitsect;
		byte[] surfType = game.getCurrentDef().surfType;

		if (maxDist == 0
				|| engine.qdist(pHitInfo.hitx - pActor.x, pHitInfo.hity - pActor.y) < maxDist) {
			switch (hitCode) {
			case SS_CEILING:
				if ((sector[pHitInfo.hitsect].ceilingstat & kSectorParallax) != 0)
					nSurf = kSurfNone;
				else
					nSurf = surfType[sector[pHitInfo.hitsect].ceilingpicnum];
				break;

			case SS_FLOOR:
				if ((sector[pHitInfo.hitsect].floorstat & kSectorParallax) != 0)
					nSurf = kSurfNone;
				else
					nSurf = surfType[sector[pHitInfo.hitsect].floorpicnum];
				break;

			case SS_WALL: {
				int nWall = pHitInfo.hitwall;
				if (!(nWall >= 0 && nWall < kMaxWalls))
					game.dassert("nWall >= 0 && nWall < kMaxWalls");
				nSurf = surfType[wall[nWall].picnum];
				if (IsNormalWall(nWall)) {
					int wx = pHitInfo.hitx - mulscale(16, dx, 14);
					int wy = pHitInfo.hity - mulscale(16, dy, 14);
					int wz = pHitInfo.hitz - mulscale(256, dz, 14);
					if (!(nSurf < kSurfMax))
						game.dassert("nSurf < kSurfMax");
					if (pVectorData.impact[nSurf].nEffect1 >= 0) { // bullethole
						SPRITE pSpawn;
						if ((pSpawn = actSpawnEffect(
								pVectorData.impact[nSurf].nEffect1, nSector,
								wx, wy, wz, 0)) != null) {
							pSpawn.cstat |= kSpriteWall;
							pSpawn.ang = (short) ((GetWallAngle(nWall) + kAngle90) & kAngleMask);
						}
					}
				}
				break;
			}

			case SS_MASKED: {
				int nWall = pHitInfo.hitwall;
				if (!(nWall >= 0 && nWall < kMaxWalls))
					game.dassert("nWall >= 0 && nWall < kMaxWalls");
				nSurf = surfType[wall[nWall].overpicnum];
				int nXWall = wall[nWall].extra;
				if (nXWall > 0) {
					XWALL pXWall = xwall[nXWall];
					if (pXWall.triggerVector)
						trTriggerWall(nWall, pXWall, kCommandWallImpact);
				}
				break;
			}

			case SS_SPRITE: {
				int nSprite = pHitInfo.hitsprite;
				if (!(nSprite >= 0 && nSprite < kMaxSprites))
					game.dassert("nSprite >= 0 && nSprite < kMaxSprites");

				nSector = pHitInfo.hitsect;

				nSurf = surfType[sprite[nSprite].picnum];

				// back off ricochet (squibs) even more
				rx -= mulscale(dx, 7 << 4, 14);
				ry -= mulscale(dy, 7 << 4, 14);
				rz -= mulscale(dz, 7 << 8, 14);

				SPRITE pSprite = sprite[nSprite];

				int shift = 4;
				if (vectorType == 0) {
					if (!IsPlayerSprite(pSprite)) {
						shift = 3;
					}
				}

				actDamageSprite(pActor.xvel, pSprite, pVectorData.damageType,
						pVectorData.damageValue << shift);

				int nXSprite = sprite[nSprite].extra;
				if (nXSprite > 0) {
					XSPRITE pXSprite = xsprite[nXSprite];
					if (pXSprite.Vector) {
						trTriggerSprite(nSprite, pXSprite, kCommandSpriteImpact);
					}
				}

				if (pSprite.statnum == kStatThing) {
					if (pSprite.lotag >= kThingBase
							&& pSprite.lotag < kThingMax
							&& thingInfo[pSprite.lotag - kThingBase].mass > 0) {
						if (pVectorData.hitForce != 0) {
							int impulse = (pVectorData.hitForce << 8)
									/ thingInfo[pSprite.lotag - kThingBase].mass;
							sprXVel[nSprite] += mulscale(impulse, dx, 16);
							sprYVel[nSprite] += mulscale(impulse, dy, 16);
							sprZVel[nSprite] += mulscale(impulse, dz, 16);
						}
					}
					if (pVectorData.burn != 0) {
						XSPRITE pXSprite = xsprite[nXSprite];
						if (pXSprite.burnTime == 0)
							evPostCallback(nSprite, 3, 0, 0);

						pXSprite.burnSource = actSetBurnSource(pActor.xvel);
						pXSprite.burnTime = ClipHigh(pXSprite.burnTime + pVectorData.burn, 1200);
					}
				}

				if (pSprite.statnum == kStatDude) {
					int mass = 0;
					if (IsDudeSprite(pSprite)) {
						switch (pSprite.lotag) {
							case kGDXDudeUniversalCultist:
							case kGDXGenDudeBurning:
								mass = getDudeMassBySpriteSize(pSprite);
								break;
							default:
								mass = dudeInfo[pSprite.lotag - kDudeBase].mass;
								break;
						}
					}
					if (mass > 0) {
						if (pVectorData.hitForce != 0) {
							long impulse = divscale(pVectorData.hitForce, mass,
									8);

							sprXVel[nSprite] += mulscale(impulse, dx, 16);
							sprYVel[nSprite] += mulscale(impulse, dy, 16);
							sprZVel[nSprite] += mulscale(impulse, dz, 16);
						}
					}

					if (pVectorData.burn != 0) {
						XSPRITE pXSprite = xsprite[nXSprite];
						if (pXSprite.burnTime == 0)
							evPostCallback(nSprite, 3, 0, 0);

						pXSprite.burnSource = actSetBurnSource(pActor.xvel);
						pXSprite.burnTime = ClipHigh(pXSprite.burnTime
								+ pVectorData.burn, 2400);
					}

					do {
						if (!Chance(pVectorData.spawnBlood >> 1))
							break;

						maxDist = gVectorData[19].maxDist;
						int hx = BiRandom2(4000) + dx;
						int hy = BiRandom2(4000) + dy;
						int hz = BiRandom2(4000) + dz;

						if (HitScan(pSprite, pHitInfo.hitz, hx, hy, hz,
								pHitInfo, 16777280, maxDist) != 0)
							break;
						if (engine.qdist(pHitInfo.hitx - pSprite.x,
								pHitInfo.hity - pSprite.y) > maxDist)
							break;
						if (!IsNormalWall(pHitInfo.hitwall))
							break;

						int sx = pHitInfo.hitx - mulscale(16, hx, 14);
						int sy = pHitInfo.hity - mulscale(16, hy, 14);
						int sz = pHitInfo.hitz - mulscale(256, hz, 14);

						int nEffect2 = gVectorData[19].impact[surfType[wall[pHitInfo.hitwall].picnum]].nEffect2;
						int nEffect3 = gVectorData[19].impact[surfType[wall[pHitInfo.hitwall].picnum]].nEffect3;
						SPRITE pSpawn = null;

						if (nEffect2 == -1 && nEffect3 == -1)
							break;

						if (!Chance(0x2000))
							pSpawn = actSpawnEffect(nEffect3,
									pHitInfo.hitsect, sx, sy, sz, 0);
						else
							pSpawn = actSpawnEffect(nEffect2,
									pHitInfo.hitsect, sx, sy, sz, 0);

						if (pSpawn != null) {
							sprZVel[pSpawn.xvel] = 8738;
							pSpawn.ang = (short) ((GetWallAngle(pHitInfo.hitwall) + 512) & kAngleMask);
							pSpawn.cstat |= kSpriteWall;
						}
					} while (false);

					for (int i = 0; i < pVectorData.nBloodTrails; i++) {
						if (Chance(pVectorData.nBloodChance >> 1))
							actSpawnBlood(pSprite);
					}
				}
				break;
			}
			}
		}

		if (pVectorData.impact[nSurf].nEffect2 >= 0)
			actSpawnEffect(pVectorData.impact[nSurf].nEffect2, nSector, rx, ry,
					rz, 0);
		if (pVectorData.impact[nSurf].nEffect3 >= 0)
			actSpawnEffect(pVectorData.impact[nSurf].nEffect3, nSector, rx, ry,
					rz, 0);
		if (pVectorData.impact[nSurf].nSoundId >= 0)
			sfxCreate3DSound(rx, ry, rz, pVectorData.impact[nSurf].nSoundId,
					nSector);
	}

	public static SPRITE actFireThing(int nActor, int xOffset, int zOffset,
			int nSlope, int thingType, int velocity) {
		if (!(thingType >= kThingBase && thingType < kThingMax))
			game.dassert("thingType >= kThingBase && thingType < kThingMax");

		SPRITE pActor = sprite[nActor];

		int dx = mulscale(pActor.clipdist, Cos(pActor.ang), 28)
				+ mulscale(Cos(pActor.ang + kAngle90), xOffset, 30);
		int dy = mulscale(pActor.clipdist, Sin(pActor.ang), 28)
				+ mulscale(Sin(pActor.ang + kAngle90), xOffset, 30);

		int x = pActor.x + dx;
		int y = pActor.y + dy;
		int z = pActor.z + zOffset;

		if (HitScan(pActor, z, dx, dy, 0, pHitInfo, CLIPMASK0, pActor.clipdist) != -1) {
			x = pHitInfo.hitx - mulscale(pActor.clipdist << 1, Cos(pActor.ang), 28);
			y = pHitInfo.hity - mulscale(pActor.clipdist << 1, Sin(pActor.ang), 28);
		}

		SPRITE pThing = actSpawnThing(pActor.sectnum, x, y, z, thingType);
		actSetOwner(pThing, pActor);

		pThing.ang = pActor.ang;
		sprXVel[pThing.xvel] = mulscale(velocity, Cos(pActor.ang), 30);
		sprYVel[pThing.xvel] = mulscale(velocity, Sin(pActor.ang), 30);
		sprZVel[pThing.xvel] = mulscale(velocity, nSlope, 14);
		sprXVel[pThing.xvel] += sprXVel[pActor.xvel] / 2;
		sprYVel[pThing.xvel] += sprYVel[pActor.xvel] / 2;
		sprZVel[pThing.xvel] += sprZVel[pActor.xvel] / 2;

		return pThing;
	}

	public static boolean sfxPlayMissileSound(SPRITE pSprite, int missileId){
		MissileType pMissType = gMissileData[missileId - kMissileBase];
		if (Chance(0x4000))
			sfxStart3DSound(pSprite, pMissType.fireSound[0], -1, 0);
		else
			sfxStart3DSound(pSprite, pMissType.fireSound[1], -1, 0);

		return true;
	}


	public static boolean sfxPlayVectorSound(SPRITE pSprite, int vectorId){
		VECTORDATA pVectorData = gVectorData[vectorId];
		if (Chance(0x4000))
			sfxStart3DSound(pSprite, pVectorData.fireSound[0], -1, 0);
		else
			sfxStart3DSound(pSprite, pVectorData.fireSound[1], -1, 0);

		return true;
	}



	public static SPRITE actFireMissile(SPRITE pActor, int xoffset,
			int zoffset, int dx, int dy, int dz, int missileType) {
		boolean Impact = false;
		if (!(missileType >= kMissileBase && missileType < kMissileMax))
			game.dassert("missileType >= kMissileBase && missileType < kMissileMax");
		MissileType pMissType = gMissileData[missileType - kMissileBase];

		int sx = pActor.x
				+ mulscale(Cos(pActor.ang),
						(pActor.clipdist + pMissType.clipdist), 28)
				+ mulscale(Cos(pActor.ang + kAngle90), xoffset, 30);
		int sy = pActor.y
				+ mulscale(Sin(pActor.ang),
						(pActor.clipdist + pMissType.clipdist), 28)
				+ mulscale(Sin(pActor.ang + kAngle90), xoffset, 30);
		int sz = pActor.z + zoffset;

		int hitInfo = HitScan(pActor, sz, sx - pActor.x, sy - pActor.y, 0,
				pHitInfo, CLIPMASK0, pActor.clipdist + pMissType.clipdist);

		if (hitInfo != -1) {
			if (hitInfo != SS_SPRITE && hitInfo != SS_WALL) {
				sx = pHitInfo.hitx
						- mulscale(Cos(pActor.ang), pMissType.clipdist << 1, 28);
				sy = pHitInfo.hity
						- mulscale(Sin(pActor.ang), pMissType.clipdist << 1, 28);
			} else {
				Impact = true;
				sx = pHitInfo.hitx - mulscale(Cos(pActor.ang), 16, 30);
				sy = pHitInfo.hity - mulscale(Sin(pActor.ang), 16, 30);
			}
		}

		int nSprite = actSpawnSprite(pActor.sectnum, sx, sy, sz, kStatMissile,
				true);

		SPRITE pSprite = sprite[nSprite];

		show2dsprite[nSprite >> 3] |= 1 << (nSprite & 7);

		pSprite.lotag = (short) missileType;
		pSprite.shade = (byte) pMissType.shade;
		pSprite.pal = 0;
		pSprite.clipdist = pMissType.clipdist;
		pSprite.hitag = kAttrMove;
		pSprite.xrepeat = (short) pMissType.xrepeat;
		pSprite.yrepeat = (short) pMissType.yrepeat;
		pSprite.picnum = (short) pMissType.picnum;
		pSprite.ang = (short) ((pActor.ang + pMissType.angleOfs) & kAngleMask);

		sprXVel[pSprite.xvel] = mulscale(dx, pMissType.velocity, 14);
		sprYVel[pSprite.xvel] = mulscale(dy, pMissType.velocity, 14);
		sprZVel[pSprite.xvel] = mulscale(dz, pMissType.velocity, 14);
		actSetOwner(pSprite, pActor);
		pSprite.cstat |= 1;
		int nXSprite = pSprite.extra;
		if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
			game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
		xsprite[nXSprite].target = -1;

		evPostCallback(pSprite.xvel, SS_SPRITE, 600, 1);

		actBuildMissile(pSprite,nXSprite,pActor);

		if (Impact) {
			actImpactMissile(pSprite, hitInfo);
			pSprite = null;
		}
		return pSprite;
	}

	public static void actBuildMissile(SPRITE pSprite, int nXSprite, SPRITE pActor) {
		switch (pSprite.lotag) {
			case kMissileLifeLeech:
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 0);
				break;
			case kMissileAltTesla:
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 15);
				break;
			case kMissileGreenPuke:
				seqSpawn(29, SS_SPRITE, nXSprite, null);
				break;
			case kMissileButcherKnife:
				pSprite.cstat |= 16;
				break;
			case kMissileTesla:
				sfxStart3DSound(pSprite, 251, 0, 0);
				break;
			case kMissileEctoSkull:
				seqSpawn(2, SS_SPRITE, nXSprite, null);
				sfxStart3DSound(pSprite, 493, 0, 0);
				break;
			case kMissileNapalm:
				seqSpawn(getSeq(kNapalm), SS_SPRITE, nXSprite, callbacks[SmokeCallback]);
				sfxStart3DSound(pSprite, 441, 0, 0);
				break;
			case kMissileFireball:
				seqSpawn(22, SS_SPRITE, nXSprite, callbacks[FireballCallback]);
				sfxStart3DSound(pSprite, 441, 0, 0);
				break;
			case kMissileHoundFire:
				seqSpawn(27, SS_SPRITE, nXSprite, null);
				sprXVel[pSprite.xvel] += (sprXVel[pActor.xvel] / 2 + BiRandom(0x11111));
				sprYVel[pSprite.xvel] += (sprYVel[pActor.xvel] / 2 + BiRandom(0x11111));
				sprZVel[pSprite.xvel] += (sprZVel[pActor.xvel] / 2 + BiRandom(0x11111));
				break;
			case kMissileTchernobog:
				seqSpawn(getSeq(kNapalm), SS_SPRITE, nXSprite, callbacks[TchernobogCallback1]);
				sfxStart3DSound(pSprite, 441, 0, 0);
				break;
			case kMissileTchernobog2:
				seqSpawn(23, SS_SPRITE, nXSprite, callbacks[TchernobogCallback2]);
				sprXVel[pSprite.xvel] += (sprXVel[pActor.xvel] / 2 + BiRandom(0x11111));
				sprYVel[pSprite.xvel] += (sprYVel[pActor.xvel] / 2 + BiRandom(0x11111));
				sprZVel[pSprite.xvel] += (sprZVel[pActor.xvel] / 2 + BiRandom(0x11111));
				break;
			case kMissileSprayFlame:
				if (Chance(0x4000))
					seqSpawn(0, SS_SPRITE, nXSprite, null);
				else
					seqSpawn(1, SS_SPRITE, nXSprite, null);

				sprXVel[pSprite.xvel] += (sprXVel[pActor.xvel] + BiRandom(0x11111));
				sprYVel[pSprite.xvel] += (sprYVel[pActor.xvel] + BiRandom(0x11111));
				sprZVel[pSprite.xvel] += (sprZVel[pActor.xvel] + BiRandom(0x11111));
				break;
			case kMissileStarburstFlare:
				evPostCallback(pSprite.xvel, SS_SPRITE, 30, 2);
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 3);
				sfxStart3DSound(pSprite, 422, 0, 0);
				break;
			case kMissileFlare:
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 3);
				sfxStart3DSound(pSprite, 422, 0, 0);
				break;
			case kMissileBone:
				sfxStart3DSound(pSprite, 252, 0, 0);
				break;
			case kMissileAltLeech2:
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 7);
				break;
		}
	}


	public static void actTeslaImpact(SPRITE pSprite, int hitInfo) {
		int x = pSprite.x;
		int y = pSprite.y;
		int z = pSprite.z;
		int nSector = pSprite.sectnum;
		int nSource = actGetOwner(pSprite);

		gSectorExp[0] = -1;
		gWallExp[0] = -1;
		NearSectors(nSector, x, y, 300, gSectorExp, gSpriteExp, gWallExp);

		boolean shotDude = false;
		actHitInfo(hitInfo, pHitInfo);
		if (hitInfo == SS_SPRITE) {
			if (info_nSprite >= 0) {
				if (sprite[info_nSprite].statnum == kStatDude)
					shotDude = true;
			}
		}

		for (int nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			if (nSprite != nSource || !shotDude) {
				SPRITE pDude = sprite[nSprite];
				if ((pDude.hitag & kAttrFree) != 0)
					continue;

				if ((gSpriteExp[pDude.sectnum >> 3] & (1 << (pDude.sectnum & 7))) != 0) {
					if (CheckProximity(pDude, x, y, z, nSector, 300)) {
						int dx = x - pDude.x;
						int dy = y - pDude.y;

						int nDamage = (300 - (engine.ksqrt(dx * dx + dy * dy) >> 4) + 20) >> 1;
						if (nDamage < 0)
							nDamage = 10;
						if (nSprite == nSource)
							nDamage /= 2;

						actDamageSprite(nSource, pDude, kDamageTesla, nDamage << 4);
					}
				}
			}
		}

		for (int nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pThing = sprite[nSprite];
			if ((pThing.hitag & kAttrFree) != 0)
				continue;

			if ((gSpriteExp[pThing.sectnum >> 3] & (1 << (pThing.sectnum & 7))) != 0) {
				if (CheckProximity(pThing, x, y, z, nSector, 300)) {
					int nXThing = pThing.extra;
					if (nXThing > 0 && xsprite[nXThing].Locked == 0) {
						int dx = x - pThing.x;
						int dy = y - pThing.y;

						int nDamage = 300 - (engine.ksqrt(dx * dx + dy * dy) >> 4) + 20;
						if (nDamage < 0)
							nDamage = 20;

						actDamageSprite(nSource, pThing, kDamageTesla, nDamage << 4);
					}
				}
			}
		}
	}

	public static void actImpactMissile(SPRITE pMissile, int hitInfo) {
		int nXMissile = pMissile.extra, maxtime;
		if (!(nXMissile > 0 && nXMissile < kMaxXSprites))
			game.dassert("nXMissile > 0 && nXMissile < kMaxXSprites");
		XSPRITE pXMissile = xsprite[pMissile.extra];

		actHitInfo(hitInfo, pHitInfo);
		SPRITE pSprite = info_pSprite, pHit;
		XSPRITE pXSprite = info_pXSprite, pXHit;
		WALL pWall = info_pWall;
		int nSprite = info_nSprite;
		int nWall = info_nWall;
		DudeInfo pDudeInfo = null;
		THINGINFO pThingInfo = null;
		if (hitInfo == SS_SPRITE && pSprite != null) {
			if (pSprite.statnum == kStatThing) {
				if (pXSprite == null)
					game.dassert("pXSpriteHit != NULL");
				pThingInfo = thingInfo[pSprite.lotag - kThingBase];
			} else if (pSprite.statnum == kStatDude) {
				if (pXSprite == null)
					game.dassert("pXSpriteHit != NULL");
				if (IsDudeSprite(pSprite))
					pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
				else
					pThingInfo = thingInfo[pSprite.lotag - kThingBase];

			}
		}
		switch (pMissile.lotag) {
		case kMissileLifeLeech:
			if (hitInfo == 3 && pSprite != null
					&& (pThingInfo != null || pDudeInfo != null)) {

				int nDamage = 6;
				if (IsOriginalDemo())
					nDamage = 7;

				actDamageSprite(actGetOwner(pMissile), pSprite,
						Random(nDamage), 16 * Random(7) + 112);
				if (pThingInfo != null && pThingInfo.damageShift[1] != 0
						|| pDudeInfo != null && pDudeInfo.damageShift[1] != 0) {
					if (sprite[pXSprite.reference].statnum == kStatDude)
						maxtime = 2400;
					else
						maxtime = 1200;
					pXSprite.burnTime = ClipHigh(pXSprite.burnTime + 360,
							maxtime);
					pXSprite.burnSource = pMissile.owner;
				}
				// Deleted since v1.10
            }

			if (pMissile.extra <= 0) {
				actPostSprite(pMissile.xvel, kStatFree);
			} else {
				actPostSprite(pMissile.xvel, kStatDefault);
				if (pMissile.ang == 1024)
					sfxStart3DSound(pMissile, 307, -1, 0);
				pMissile.lotag = 0;
				seqSpawn(9, SS_SPRITE, pMissile.extra, null);
			}
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileAltTesla:
			actTeslaImpact(pMissile, hitInfo);
			if ((hitInfo == SS_WALL || hitInfo == SS_MASKED) && pWall != null) {
				SPRITE pEffect = actSpawnEffect(52, pMissile.sectnum,
						pMissile.x, pMissile.y, pMissile.z, 0);
				if (pEffect != null)
					pEffect.ang = (short) ((GetWallAngle(nWall) + kAngle90) & kAngleMask);
			}
			actGenerateGibs(pMissile, 24, null, null);
			actPostSprite(pMissile.xvel, kStatFree);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileGreenPuke:
			seqKill(SS_SPRITE, pMissile.extra);
			if (hitInfo == SS_SPRITE && pSprite != null
					&& (pThingInfo != null || pDudeInfo != null))
				actDamageSprite(actGetOwner(pMissile), pSprite, kDamageBullet,
						16 * Random(7) + 240);
			actPostSprite(pMissile.xvel, kStatFree);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileBone:
			sfxKill3DSound(pMissile, -1, -1);
			sfxCreate3DSound(pMissile.x, pMissile.y, pMissile.z, 306,
					pMissile.sectnum);
			actGenerateGibs(pMissile, 6, null, null);
			if (hitInfo == SS_SPRITE && pSprite != null
					&& (pThingInfo != null || pDudeInfo != null))
				actDamageSprite(actGetOwner(pMissile), pSprite, kDamageSpirit,
						16 * Random(20) + 400);
			actPostSprite(pMissile.xvel, kStatFree);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileAltLeech1:
		case kMissileAltLeech2:
			sfxKill3DSound(pMissile, -1, -1);
			sfxCreate3DSound(pMissile.x, pMissile.y, pMissile.z, 306,
					pMissile.sectnum);
			if (hitInfo == SS_SPRITE && pSprite != null
					&& (pThingInfo != null || pDudeInfo != null)) {
				int damage = 3;
				if (pMissile.lotag == kMissileAltLeech2)
					damage = 6;
				actDamageSprite(actGetOwner(pMissile), pSprite, kDamageSpirit,
						16 * (Random(damage) + damage));
			}
			actPostSprite(pMissile.xvel, kStatFree);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileFireball:
		case kMissileNapalm:
			if (hitInfo == SS_SPRITE && pSprite != null
					&& (pThingInfo != null || pDudeInfo != null)) {
				if (pThingInfo != null && pSprite.lotag == kThingTNTBarrel
						&& pXSprite.burnTime == 0)
					evPostCallback(pSprite.xvel, SS_SPRITE, 0, 0);
				actDamageSprite(actGetOwner(pMissile), pSprite, kDamageBullet,
						16 * Random(50) + 800);
			}
			actExplodeSprite(pMissile.xvel);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileStarburstFlare:
			sfxKill3DSound(pMissile, -1, -1);
			actExplodeSprite(pMissile.xvel);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileFlare:
			sfxKill3DSound(pMissile, -1, -1);
			if (hitInfo != SS_SPRITE || pSprite == null) {
				actGenerateGibs(pMissile, 17, null, null);
				actPostSprite(pMissile.xvel, kStatFree);
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}

			if (pThingInfo != null || pDudeInfo != null) {
				if (pThingInfo != null && pThingInfo.damageShift[1] != 0
						|| pDudeInfo != null && pDudeInfo.damageShift[1] != 0) {
					if (pThingInfo != null && pSprite.lotag == kThingTNTBarrel
							&& pXSprite.burnTime == 0)
						evPostCallback(nSprite, 3, 0, 0);

					if (sprite[pXSprite.reference].statnum == kStatDude)
						maxtime = 2400;
					else
						maxtime = 1200;

					pXSprite.burnTime = ClipHigh(pXSprite.burnTime + 480, maxtime);
					pXSprite.burnSource = pMissile.owner;

					actDistanceDamage(actGetOwner(pMissile), pMissile.x,
							pMissile.y, pMissile.z, pMissile.sectnum, 16, 20,
							2, 10, 6, 480);
				} else
					actDamageSprite(actGetOwner(pMissile), pSprite,
							kDamageBullet, 16 * Random(10) + 320);
			}

			if (game.getCurrentDef().surfType[pSprite.picnum] == 4) {
				pMissile.picnum = 2123;
				pXMissile.target = nSprite;
				pXMissile.targetZ = pMissile.z - pSprite.z;
				int nAngle = engine.getangle(pMissile.x - pSprite.x, pMissile.y
						- pSprite.y);
				pXMissile.goalAng = (short) ((nAngle - pSprite.ang) & 0x7FF);
				pXMissile.state = 1;
				actPostSprite(pMissile.xvel, kStatFlare);
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			} else {
				actGenerateGibs(pMissile, 17, null, null);
				actPostSprite(pMissile.xvel, kStatFree);
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			}
			break;
		case kMissileSprayFlame:
		case kMissileHoundFire:
			if (hitInfo != SS_SPRITE) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}
			if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
				game.dassert("nObject >= 0 && nObject < kMaxSprites");
			pHit = sprite[pHitInfo.hitsprite];
			if (pHit.extra <= 0) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}
			pXHit = xsprite[pHit.extra];
			if ((pHit.statnum == kStatThing || pHit.statnum == kStatDude)
					&& pXHit.burnTime == 0)
				evPostCallback(pHitInfo.hitsprite, SS_SPRITE, 0, 0);

			if (sprite[pXHit.reference].statnum == kStatDude)
				maxtime = 2400;
			else
				maxtime = 1200;
			pXHit.burnTime = ClipHigh(pXHit.burnTime
					+ (4 * pGameInfo.nDifficulty + 16), maxtime);
			pXHit.burnSource = pMissile.owner;
			actDamageSprite(actGetOwner(pMissile), pHit, kDamageBurn, 8);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileTchernobog:
			actExplodeSprite(pMissile.xvel);
			if (hitInfo == SS_SPRITE) {
				if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
					game.dassert("nObject >= 0 && nObject < kMaxSprites");
				pHit = sprite[pHitInfo.hitsprite];
				if (pHit.extra > 0) {
					pXHit = xsprite[pHit.extra];
					if ((pHit.statnum == kStatThing || pHit.statnum == kStatDude)
							&& pXHit.burnTime == 0)
						evPostCallback(pHitInfo.hitsprite, SS_SPRITE, 0, 0);

					if (sprite[pXHit.reference].statnum == kStatDude)
						maxtime = 2400;
					else
						maxtime = 1200;
					pXHit.burnTime = ClipHigh(pXHit.burnTime
							+ (4 * pGameInfo.nDifficulty + 16), maxtime);
					pXHit.burnSource = pMissile.owner;
					actDamageSprite(actGetOwner(pMissile), pHit, kDamageBurn, 8);
					actDamageSprite(actGetOwner(pMissile), pHit, kDamageBullet,
							16 * Random(10) + 400);
				}
			}
			actExplodeSprite(pMissile.xvel);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileTchernobog2:
			actExplodeSprite(pMissile.xvel);
			if (hitInfo == SS_SPRITE) {
				if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
					game.dassert("nObject >= 0 && nObject < kMaxSprites");
				pHit = sprite[pHitInfo.hitsprite];
				if (pHit.extra > 0) {
					pXHit = xsprite[pHit.extra];
					if ((pHit.statnum == kStatThing || pHit.statnum == kStatDude)
							&& pXHit.burnTime == 0)
						evPostCallback(pHitInfo.hitsprite, SS_SPRITE, 0, 0);

					if (sprite[pXHit.reference].statnum == kStatDude)
						maxtime = 2400;
					else
						maxtime = 1200;
					pXHit.burnTime = ClipHigh(pXHit.burnTime + 32, maxtime);
					pXHit.burnSource = pMissile.owner;
					actDamageSprite(actGetOwner(pMissile), pHit, kDamageSpirit,
							12);
					actDamageSprite(actGetOwner(pMissile), pHit, kDamageBullet,
							16 * Random(10) + 400);
				}
			}
			actExplodeSprite(pMissile.xvel);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileEctoSkull:
			sfxKill3DSound(pMissile, -1, -1);
			sfxCreate3DSound(pMissile.x, pMissile.y, pMissile.z, 522,
					pMissile.sectnum);
			actPostSprite(pMissile.xvel, kStatDebris);
			seqSpawn(20, 3, pMissile.extra, null);
			if (hitInfo != SS_SPRITE) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}
			if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
				game.dassert("nObject >= 0 && nObject < kMaxSprites");
			pHit = sprite[pHitInfo.hitsprite];

			if (pHit.statnum != kStatDude) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}
			actDamageSprite(actGetOwner(pMissile), pHit, kDamageSpirit,
					16 * Random(10) + 400);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileButcherKnife:
			actPostSprite(pMissile.xvel, kStatDebris);
			pMissile.cstat &= ~kSpriteWall;
			pMissile.lotag = 0;
			seqSpawn(20, SS_SPRITE, pMissile.extra, null);
			if (hitInfo != SS_SPRITE) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}
			if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
				game.dassert("nObject >= 0 && nObject < kMaxSprites");
			pHit = sprite[pHitInfo.hitsprite];

			if (pHit.statnum != kStatDude) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}
			actDamageSprite(actGetOwner(pMissile), pHit, kDamageSpirit,
					16 * Random(10) + 160);

			int nOwner = actGetOwner(pMissile);
			if (IsOriginalDemo()) {
				if(nOwner != -1) {
					pHit = sprite[nOwner];
					if (xsprite[pHit.extra].health != 0) {
						actHealDude(xsprite[pHit.extra], 10, dudeInfo[pHit.lotag
								- kDudeBase].startHealth);
					}
				}
			} else {
				if(nOwner != -1) {
					pHit = sprite[nOwner];
					if (pHit.extra >= 0 && xsprite[pHit.extra].health != 0 && IsDudeSprite(sprite[pHit.xvel])) {
						int lotag = pHit.lotag - kDudeBase;
						if (lotag >= 200)
							actHealDude(xsprite[pHit.extra], 10, dudeInfo[lotag].startHealth);
					}
				}
			}
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		case kMissileTesla:
			sfxKill3DSound(pMissile, -1, -1);
			sfxCreate3DSound(pMissile.x, pMissile.y, pMissile.z, 518,
					pMissile.sectnum);
			if (hitInfo == 2)
				actGenerateGibs(pMissile, 23, null, null);
			else
				actGenerateGibs(pMissile, 22, null, null);

			checkEventList(pMissile.xvel, SS_SPRITE);
			seqKill(SS_SPRITE, pMissile.extra);
			actPostSprite(pMissile.xvel, kStatFree);
			if (hitInfo != SS_SPRITE) {
				pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
				return;
			}

			if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
				game.dassert("nObject >= 0 && nObject < kMaxSprites");
			pHit = sprite[pHitInfo.hitsprite];
			actDamageSprite(actGetOwner(pMissile), pHit, kDamageTesla,
					16 * Random(10) + 240);
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		default:
			seqKill(SS_SPRITE, pMissile.extra);
			actPostSprite(pMissile.xvel, kStatFree);
			if (hitInfo == 3) {
				if (!(pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite < kMaxSprites))
					game.dassert("nObject >= 0 && nObject < kMaxSprites");
				actDamageSprite(actGetOwner(pMissile),
						sprite[pHitInfo.hitsprite], kDamageFall,
						16 * Random(10) + 160);
			}
			pMissile.cstat &= ~(kSpriteBlocking | kSpriteHitscan);
			break;
		}
	}

	static int info_nSprite = -1;
	static SPRITE info_pSprite = null;
	static XSPRITE info_pXSprite = null;
	static int info_nWall = -1;
	static WALL info_pWall = null;
	static XWALL info_pXWall = null;
	static int info_nSector = -1;
	static SECTOR info_pSector = null;
	static XSECTOR info_pXSector = null;

	public static void actHitInfo(int hitType, Hitscan pHitInfo) {
		if (pHitInfo == null)
			game.dassert("pHitInfo != NULL");

		info_nSprite = -1;
		info_pSprite = null;
		info_pXSprite = null;
		info_nWall = -1;
		info_pWall = null;
		info_pXWall = null;
		info_nSector = -1;
		info_pSector = null;
		info_pXSector = null;

		switch (hitType) {
		case SS_SPRITE:
		case 5:
			info_nSprite = pHitInfo.hitsprite;
			if (!(info_nSprite >= 0 && info_nSprite < kMaxSprites))
				game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
			info_pSprite = sprite[info_nSprite];
			if (info_pSprite.extra > 0)
				info_pXSprite = xsprite[info_pSprite.extra];
			break;
		case SS_WALL:
		case SS_MASKED:
			info_nWall = pHitInfo.hitwall;
			if (!(info_nWall >= 0 && info_nWall < kMaxWalls))
				game.dassert("nWall >= 0 && nWall < kMaxWalls");
			info_pWall = wall[info_nWall];
			if (info_pWall.extra > 0)
				info_pXWall = xwall[info_pWall.extra];
			break;
		case SS_SECTOR:
		case SS_FLOOR:
		case SS_CEILING:
			info_nSector = pHitInfo.hitsect;
			if (!(info_nSector >= 0 && info_nSector < kMaxSectors))
				game.dassert("nSector >= 0 && nSector < kMaxSectors");
			info_pSector = sector[info_nSector];
			if (info_pSector.extra > 0)
				info_pXSector = xsector[info_pSector.extra];
			break;
		default:
			break;
		}
	}

	public static void MoveThing(int nSprite) {
		SPRITE pSprite = sprite[nSprite];

		int nXSprite = pSprite.extra;
		if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
			game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
		XSPRITE pXSprite = xsprite[nXSprite];
		if (!(pSprite.lotag >= kThingBase && pSprite.lotag < kThingMax))
			game.dassert("pSprite.type >= kThingBase && pSprite.type < kThingMax");
		THINGINFO pThinkInfo = thingInfo[pSprite.lotag - kThingBase];
		int nSector = pSprite.sectnum;
		if (!(nSector >= 0 && nSector < kMaxSectors))
			game.dassert("nSector >= 0 && nSector < kMaxSectors");

		int zTop, zBot;
		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		int moveHit = 0;
		int floorDist = (zBot - pSprite.z) / 4;
		int ceilDist = (pSprite.z - zTop) / 4;

		int clipDist = pSprite.clipdist << 2;

		if (sprXVel[pSprite.xvel] != 0 || sprYVel[pSprite.xvel] != 0) {

			short oldcstat = pSprite.cstat;
			pSprite.cstat &= ~(kSpriteBlocking | kSpriteHitscan);

			gSpriteHit[nXSprite].moveHit = ClipMove(pSprite.x, pSprite.y,
					pSprite.z, (short) nSector, sprXVel[pSprite.xvel] >> 12,
					sprYVel[pSprite.xvel] >> 12, clipDist, ceilDist, floorDist,
					CLIPMASK0);

			moveHit = gSpriteHit[nXSprite].moveHit;
			pSprite.x = clipm_px;
			pSprite.y = clipm_py;
			pSprite.z = clipm_pz;
			nSector = (short) clipm_pnsectnum;

			pSprite.cstat = oldcstat;
			if (nSector < 0)
				game.dassert("nSector >= 0");

			if (pSprite.sectnum != nSector) {
				if (nSector < 0)
					game.dassert("nSector >= 0");
				changespritesect(pSprite.xvel, (short) nSector);
			}

			int nHitObject = gSpriteHit[nXSprite].moveHit & kHitTypeMask;
			if (nHitObject == kHitWall) {
				int nWall = moveHit & kHitIndexMask;
				ReflectVector(sprXVel[pSprite.xvel], sprYVel[pSprite.xvel],
						nWall, pThinkInfo.nFraction);

				sprXVel[pSprite.xvel] = refl_x;
				sprYVel[pSprite.xvel] = refl_y;
				if (pSprite.lotag == kThingPail) {
					sfxStart3DSound(pSprite, 374, 0, 0);
				} else if (pSprite.lotag == kThingZombieHead) {
					sfxStart3DSound(pSprite, 607, 0, 0);
					actDamageSprite(-1, pSprite, kDamageFall, 80);
				}
			}
		} else {
			if (!(nSector >= 0 && nSector < kMaxSectors))
				game.dassert("nSector >= 0 && nSector < kMaxSectors");
			FindSector(pSprite.x, pSprite.y, pSprite.z, (short) nSector);
			nSector = foundSector;
		}

		if (sprZVel[pSprite.xvel] != 0)
			pSprite.z += sprZVel[pSprite.xvel] >> 8;

		int ceilz, ceilhit, floorz, floorhit;
		GetZRange(pSprite, clipDist, CLIPMASK0);
		ceilz = gz_ceilZ;
		ceilhit = gz_ceilHit;
		floorz = gz_floorZ;
		floorhit = gz_floorHit;

		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;
		if ((pSprite.hitag & kAttrGravity) != 0) {
			if (zBot < floorz) {
				pSprite.z += 455;
				sprZVel[pSprite.xvel] += 58254;
				if (pSprite.lotag == kThingZombieHead) {
					SPRITE pSpawn = null;

					if ((pSpawn = actSpawnEffect(27, pSprite.sectnum,
							pSprite.x, pSprite.y, pSprite.z, 0)) != null) {

						int xvel = 279620;
						int yvel = 0;
						int zvel = 0;
						RotateVector(xvel, yvel, 11 * gFrameClock & 0x7FF);
						xvel = (int) rotated.x;
						yvel = (int) rotated.y;
						RotateVector(xvel, zvel, 5 * gFrameClock & 0x7FF);
						xvel = (int) rotated.x;
						zvel = (int) rotated.y;
						RotateVector(yvel, zvel, 3 * gFrameClock & 0x7FF);
						yvel = (int) rotated.x;
						zvel = (int) rotated.y;

						sprXVel[pSpawn.xvel] = xvel + sprXVel[pSprite.xvel];
						sprYVel[pSpawn.xvel] = yvel + sprYVel[pSprite.xvel];
						sprZVel[pSpawn.xvel] = zvel + sprZVel[pSprite.xvel];
					}
				}
			}
		}

		int warp = checkWarping(pSprite);
		if (warp != 0) {
			GetZRange(pSprite, clipDist, CLIPMASK0);
			ceilz = gz_ceilZ;
			ceilhit = gz_ceilHit;
			floorz = gz_floorZ;
			floorhit = gz_floorHit;
		}

		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if(!IsOriginalDemo() && (pSprite.lotag == kThingTNTBundle || pSprite.lotag == kThingSprayBundle)) { //bridge sprite hack
			if((floorhit & kHitTypeMask) == kHitSprite) {
				if((sprite[floorhit & kHitIndexMask].cstat & kSpriteRMask) == kSpriteFloor )
					if(klabs(zBot - floorz) < 1024) floorz -= 1024;
			 }
		}

		if (floorz > zBot) {
			gSpriteHit[nXSprite].floorHit = 0;
			if ((pSprite.hitag & kAttrGravity) != 0)
				pSprite.hitag |= kAttrFalling;
		} else { // hit floor?
			processTouchFloor(pSprite, pSprite.sectnum);

			gSpriteHit[nXSprite].floorHit = floorhit;
			pSprite.z += (floorz - zBot);

			int dZvel = (int) (sprZVel[pSprite.xvel] - floorVel[pSprite.sectnum]);
			if (dZvel <= 0) {
				if (sprZVel[pSprite.xvel] == 0)
					pSprite.hitag &= ~kAttrFalling;
			} else {
				pSprite.hitag |= kAttrFalling;
				long gravity = GravityVector(sprXVel[pSprite.xvel],
						sprYVel[pSprite.xvel], dZvel, pSprite.sectnum,
						pThinkInfo.nFraction);
				int fallDamage = mulscale(gravity, gravity, 30)
						- pThinkInfo.fallDamage;
				sprXVel[pSprite.xvel] = refl_x;
				sprYVel[pSprite.xvel] = refl_y;
				dZvel = (int) refl_z;

				if (fallDamage > 0)
					actDamageSprite(pSprite.xvel, pSprite, kDamageFall,
							fallDamage);

				sprZVel[pSprite.xvel] = dZvel;
				if (floorVel[pSprite.sectnum] == 0) {
					if (klabs(dZvel) < 65536) {
						sprZVel[pSprite.xvel] = 0;
						pSprite.hitag &= ~kAttrFalling;
					}
				}

				switch (pSprite.lotag) {
				case kThingAltNapalm:
					if (sprZVel[pSprite.xvel] == 0 || Chance(0x5000)) {
						actNapalm2Explode(pSprite, pXSprite);
					}
					break;
				case kThingPail:
					if (klabs(sprZVel[pSprite.xvel]) > 524288)
						sfxStart3DSound(pSprite, 374, 0, 0);
					break;
				case kThingZombieHead:
					if (klabs(sprZVel[pSprite.xvel]) > 524288) {
						sfxStart3DSound(pSprite, 607, 0, 0);
						actDamageSprite(-1, pSprite, kDamageFall, 80);
					}
					break;
				}
				moveHit = (nSector | kHitSector);
			}
		}
		if (ceilz < zTop) {
			gSpriteHit[nXSprite].ceilHit = 0;
		} else // hit ceiling
		{
			gSpriteHit[nXSprite].ceilHit = ceilhit;
			if (ceilz - zTop > 0)
				pSprite.z += ceilz - zTop;

			if (sprZVel[pSprite.xvel] < 0) {
				sprXVel[pSprite.xvel] = mulscale(49152, sprXVel[pSprite.xvel],
						16);
				sprYVel[pSprite.xvel] = mulscale(49152, sprYVel[pSprite.xvel],
						16);
				sprZVel[pSprite.xvel] = mulscale(16384, -sprZVel[pSprite.xvel],
						16);

				switch (pSprite.lotag) {
				case kThingPail:
					if (klabs(sprZVel[pSprite.xvel]) > 524288)
						sfxStart3DSound(pSprite, 374, 0, 0);
					break;
				case kThingZombieHead:
					if (klabs(sprZVel[pSprite.xvel]) > 524288) {
						sfxStart3DSound(pSprite, 607, 0, 0);
						actDamageSprite(-1, pSprite, kDamageFall, 80);
					}
					break;
				}
			}
		}

		if (zBot >= floorz) {
			int moveDist = (int) engine.qdist(sprXVel[pSprite.xvel],
					sprYVel[pSprite.xvel]);
			int oldDist = moveDist;
			if (moveDist > 0x11111)
				moveDist = 0x11111;

			if ((floorhit & kHitTypeMask) == kHitSprite) {
				int nUnderSprite = floorhit & kHitIndexMask;

				if ((sprite[nUnderSprite].cstat & kSpriteRMask) == kSpriteFace) {
					// push it off the face sprite
					sprXVel[pSprite.xvel] += mulscale(kFrameTicks, pSprite.x
							- sprite[nUnderSprite].x, 2);
					sprYVel[pSprite.xvel] += mulscale(kFrameTicks, pSprite.y
							- sprite[nUnderSprite].y, 2);
					moveHit = gSpriteHit[nXSprite].moveHit;
				}
			}
			if (moveDist > 0) {
				int kv = divscale(moveDist, oldDist, 16);
				sprXVel[pSprite.xvel] -= mulscale(sprXVel[pSprite.xvel], kv, 16);
				sprYVel[pSprite.xvel] -= mulscale(sprYVel[pSprite.xvel], kv, 16);
			}
		}

		if (sprXVel[pSprite.xvel] != 0 || sprYVel[pSprite.xvel] != 0)
			pSprite.ang = engine.getangle((int) sprXVel[pSprite.xvel],
					(int) sprYVel[pSprite.xvel]);

		if (moveHit != 0) {
			if (pSprite.extra != 0) {
				if (pXSprite.Impact)
					trTriggerSprite(pSprite.xvel, pXSprite, 0);

				switch (pSprite.lotag) {
				case kGDXThingThrowableRock:
					seqSpawn(24, SS_SPRITE, nXSprite, null);
					if ((moveHit & kHitTypeMask) == kHitSprite) {
						pSprite.xrepeat = 32;
						pSprite.yrepeat = 32;
						int nObject = moveHit & kHitIndexMask;
						if (!(nObject >= 0 && nObject < kMaxSprites))
							game.dassert("nObject >= 0 && nObject < kMaxSprites");
						actDamageSprite(actGetOwner(pSprite), sprite[nObject],
								kDamageFall, pXSprite.data1);
					}
					break;
				case kThingBoneClub:
					seqSpawn(24, SS_SPRITE, nXSprite, null);
					if ((moveHit & kHitTypeMask) == kHitSprite) {
						int nObject = moveHit & kHitIndexMask;
						if (!(nObject >= 0 && nObject < kMaxSprites))
							game.dassert("nObject >= 0 && nObject < kMaxSprites");
						actDamageSprite(actGetOwner(pSprite), sprite[nObject],
								kDamageFall, 12);
					}
					break;
				case kThingWaterDrip:
				case kThingBloodDrip:
					pSprite.z -= 1024;
					pSprite.hitag &= ~kAttrGravity;
					int surfType = game.getCurrentDef().GetSurfType(gSpriteHit[nXSprite].floorHit);
					if (pSprite.lotag == kThingWaterDrip) {
						if (surfType == 5) {
							seqSpawn(6, SS_SPRITE, nXSprite, null);
							sfxStart3DSound(pSprite, 356, -1, 0);
						} else {
							seqSpawn(7, SS_SPRITE, nXSprite, null);
							sfxStart3DSound(pSprite, 354, -1, 0);
						}
					}
					if (pSprite.lotag == kThingBloodDrip) {
						seqSpawn(8, SS_SPRITE, nXSprite, null);
						sfxStart3DSound(pSprite, 354, -1, 0);// .DRIPSURF
					}
					break;
				case kThingPodFire:
					actExplodeSprite(pSprite.xvel);
					break;
				case kThingPodGreen:
					if ((moveHit & kHitTypeMask) == kHitFloor) {
						actDistanceDamage(actGetOwner(pSprite), pSprite.x,
								pSprite.y, pSprite.z, pSprite.sectnum, 200, 1,
								kDamageExplode, 20, 6, 0);
					} else if (IsOriginalDemo() || (moveHit & kHitTypeMask) == kHitSprite) {
						int nObject = moveHit & kHitIndexMask;
						if (nObject >= 0 && nObject < kMaxSprites)
							actDamageSprite(actGetOwner(pSprite), sprite[nObject], kDamageFall, 12);
					}
					evPostCallback(pSprite.xvel, SS_SPRITE, 0, 19);
					break;
				}
			}
		}
	}

	public static int actSetOwner(SPRITE pTarget, SPRITE pSource) {
		int killerid;
		if (!(pTarget != null && pSource != null))
			game.dassert("pTarget != NULL && pSource != NULL");
		if (IsPlayerSprite(pSource)) {
			killerid = (pSource.lotag - kDudePlayer1) | 0x1000;
		} else
			killerid = pSource.xvel;
		pTarget.owner = (short) killerid;

		return killerid;
	}

	public static int actGetOwner(SPRITE pSprite) {
		if (pSprite == null)
			game.dassert("pSprite != null");
		if (pSprite.owner == -1)
			return -1;

		int result = (pSprite.owner) & 0xFFF;
		if ((pSprite.owner & 0x1000) != 0)
			result = gPlayer[result].pSprite.xvel;

		return result;
	}

	public static int actDamageSprite(int nSource, SPRITE pSprite,
			int nDamageType, int nDamage) {
		int nShift;
		if (nSource >= kMaxSprites)
			game.dassert("nSource < kMaxSprites " + nSource);

		if ((pSprite.hitag & kAttrFree) != 0)
			return 0;

		int nXSprite = pSprite.extra;
		if (nXSprite <= 0)
			return 0;

		if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
			game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
		XSPRITE pXSprite = xsprite[nXSprite];

		if (!(pXSprite.reference == pSprite.xvel))
			game.dassert("pXSprite.reference == pSprite.index");

		if (pXSprite.health == 0 && pSprite.statnum != kStatDude
				|| pXSprite.Locked != 0)
			return 0;

		if (nSource == -1)
			nSource = pSprite.xvel;

		PLAYER pPlayer = null;
		if (IsPlayerSprite(sprite[nSource]))
			pPlayer = gPlayer[sprite[nSource].lotag - kDudePlayer1];

		switch (pSprite.statnum) {
		case kStatThing:
			if (!(pSprite.lotag >= kThingBase && pSprite.lotag < kThingMax))
				game.dassert("pSprite.type >= kThingBase && pSprite.type < kThingMax");

			nShift = thingInfo[pSprite.lotag - kThingBase].damageShift[nDamageType];
			if (nShift == 0)
				return 0;
			if (nShift != 256)
				nDamage = mulscale(nShift, nDamage, 8);

			pXSprite.health = ClipLow(pXSprite.health - nDamage, 0);

			if (pXSprite.health != 0)
				return nDamage >> 4;

			if (pSprite.lotag == kThingLifeLeech/* || pSprite.lotag == kGDXThingCustomDudeLifeLeech*/) {
				actGenerateGibs(pSprite, 14, null, null);
				pXSprite.data1 = 0;
				pXSprite.data2 = 0;
				pXSprite.data3 = 0;
				pXSprite.data4 = 0;
				pXSprite.stateTimer = 0;
				pXSprite.isTriggered = false;
				pXSprite.DudeLockout = false;

            } else if ((pSprite.hitag & kAttrRespawn) == 0)
				actSetOwner(pSprite, sprite[nSource]);

			trTriggerSprite(pSprite.xvel, pXSprite, kCommandOff);

			switch (pSprite.lotag) {
			case kThingFluorescent:
				seqSpawn(12, SS_SPRITE, pSprite.extra, null);
				actGenerateGibs(pSprite, 6, null, null);
				break;
			case kThingWeb:
				seqSpawn(15, SS_SPRITE, pSprite.extra, null);
				break;
			case kThingMetalGrate1:
				seqSpawn(21, SS_SPRITE, pSprite.extra, null);
				actGenerateGibs(pSprite, 4, null, null);
				break;
			case kThingFlammableTree:
				switch (pXSprite.data1) {
				case 0:
					seqSpawn(25, SS_SPRITE, pSprite.extra, callbacks[DamageTreeCallback]);
					sfxStart3DSound(pSprite, 351, -1, 0);
					break;
				case 1:
					seqSpawn(26, SS_SPRITE, pSprite.extra, callbacks[DamageTreeCallback]);
					sfxStart3DSound(pSprite, 351, -1, 0);
					break;
				case -1:
					actGenerateGibs(pSprite, 14, null, null);
					sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, 312,
							pSprite.sectnum);
					actPostSprite(pSprite.xvel, kStatFree);
					break;
				}
				break;
			case kThingMachineGun:
				seqSpawn(28, SS_SPRITE, pSprite.extra, null);
				break;
			case 422:
				if (seqFrame(SS_SPRITE, pSprite.extra) < 0) {
					seqSpawn(19, 3, pSprite.extra, null);
				}
				break;
			case kThingGibObject:
			case kThingExplodeObject:
			case kThingGibSmall:
			case kThingGib:
			case kThingZombieHead:
				if (nDamageType == kDamageExplode && pPlayer != null
						&& gFrameClock > pPlayer.pLaughsCount && Chance(0x2000)) {
					int sndId = gLaughs[Random(10)];
					if(gPlayer[gViewIndex] == pPlayer)
						sndStartSample(sndId, 255, 1, false);
					else sfxStart3DSound(pPlayer.pSprite, sndId, 0, 0);
					pPlayer.pLaughsCount = gFrameClock + 3600;
				}
				break;
			}

			return nDamage >> 4;
		case kStatDude:
			if(!IsDudeSprite(pSprite))
				return 0;

			if (IsPlayerSprite(pSprite)
					&& (!IsDudeSprite(sprite[nSource]) || pSprite.xvel == nSource)) {
				nShift = mulscale(
						gPlayer[pSprite.lotag - kDudePlayer1].pDudeInfo.startDamage[nDamageType],
						pPlayerShift[pGameInfo.nDifficulty], 8);
			} else
				nShift = dudeInfo[pSprite.lotag - kDudeBase].damageShift[nDamageType];

			if((pGameInfo.nGameType == kNetModeCoop || pGameInfo.nGameType == kNetModeTeams) && IsPlayerSprite(pSprite)
					&& pPlayer != null && pSprite.xvel != nSource) {

				if(pGameInfo.nGameType == kNetModeCoop)
					nShift = pGameInfo.nFriendlyFire;
				else {
					if(pPlayer.teamID == gPlayer[pSprite.lotag - kDudePlayer1].teamID)
						nShift = pGameInfo.nFriendlyFire;
				}
			}

			if (nShift == 0)
				return 0;

			if (nShift != 256)
				nDamage = mulscale(nShift, nDamage, 8);

			if (IsPlayerSprite(pSprite)) {
				pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];

				if (pXSprite.health != 0 || checkPlayerSeq(pPlayer, getPlayerSeq(kPlayerFatality))) {
					playerDamageSprite(pPlayer, nSource, nDamageType, nDamage);
					return nDamage >> 4;
				}
				return nDamage >> 4;
			}
			if (pXSprite.health == 0)
				return 0;

			nDamage = aiDamageSprite(pSprite, pXSprite, nSource, nDamageType, nDamage);
			if (pXSprite.health != 0)
				return nDamage >> 4;

			if (nDamageType == kDamageExplode && nDamage < 160)
				nDamageType = kDamageFall;

			actKillSprite(nSource, pSprite, nDamageType, nDamage);
			return nDamage >> 4;
		}
		return 0;
	}

	public static void actKillSprite(int nSource, SPRITE pSprite,
			int nDamageType, int nDamage) {
		if (!IsDudeSprite(pSprite))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");

		int dudeIndex = pSprite.lotag - kDudeBase;
		DudeInfo pDudeInfo = dudeInfo[dudeIndex];

		int nSprite = pSprite.xvel;
		int nXSprite = pSprite.extra;
		if (!(nXSprite > 0))
			game.dassert("nXSprite > 0");
		XSPRITE pXSprite = xsprite[pSprite.extra];

		// handle first cerberus head death
		if (pSprite.lotag == kDudeCerberus) {
			seqSpawn(pDudeInfo.seqStartID + kSeqDudeDeath1, SS_SPRITE,
					nXSprite, null);
			return;
		}

		if (pSprite.lotag == kDudeTommyCultist
				|| pSprite.lotag == kDudeShotgunCultist) {
			if (nDamageType == kDamageBurn) {
				if(pXSprite.palette == 0) {
					pSprite.lotag = kDudeCultistBurning;
					aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
					actHealDude(pXSprite, dudeInfo[40].startHealth,
							dudeInfo[40].startHealth);
					if (!IsOriginalDemo())
						if (pXSprite.burnTime == 0)
							pXSprite.burnTime = 1200;
					aiClock[pSprite.xvel] = gFrameClock + 360;
					return;
				} else nDamageType = kDamageFall;
			}
		}

		if (pSprite.lotag == kDudeTheBeast) {
			if (nDamageType == kDamageBurn && pXSprite.palette == 0) {
				pSprite.lotag = kDudeTheBeastburning;
				aiNewState(pSprite, pXSprite, burnGoto[BEAST]);
				actHealDude(pXSprite, dudeInfo[53].startHealth,
						dudeInfo[53].startHealth);
				if (!IsOriginalDemo())
					if (pXSprite.burnTime == 0)
						pXSprite.burnTime = 1200;
				aiClock[pSprite.xvel] = gFrameClock + 360;
				return;
			}
		}

		if (pSprite.lotag == kDudeInnocent) {
			if (nDamageType == kDamageBurn && pXSprite.palette == 0) {
				pSprite.lotag = kDudeBurning;
				aiNewState(pSprite, pXSprite, burnGoto[INNOCENT]);
				actHealDude(pXSprite, dudeInfo[39].startHealth,
						dudeInfo[39].startHealth);
				if (!IsOriginalDemo())
					if (pXSprite.burnTime == 0)
						pXSprite.burnTime = 1200;
				aiClock[pSprite.xvel] = gFrameClock + 360;
				return;
			}
		}

		if (pSprite.lotag == kGDXDudeUniversalCultist){
			removeDudeStuff(pSprite);
			XSPRITE pXIncarnation = getNextIncarnation(pXSprite);
			if (pXIncarnation == null) {

				if (pXSprite.data1 >= kThingHiddenExploder && pXSprite.data1 < (kThingHiddenExploder + kExplodeMax) -1 &&
						Chance(0x4000) && nDamageType != kDamageSpirit && nDamageType != kDamageDrown) {

						doExplosion(pSprite,pXSprite.data1 - kThingHiddenExploder);
						if (Chance(0x9000))
							nDamageType = kDamageExplode;
				}

				if (nDamageType == kDamageBurn) {
					if ((BuildGdx.cache.contains(pXSprite.data2 + 15,seq) || BuildGdx.cache.contains(pXSprite.data2 + 16,seq)) && pXSprite.palette == 0) {

							if (BuildGdx.cache.contains(pXSprite.data2 + 3,seq)) {
								pSprite.lotag = kGDXGenDudeBurning;

		                        if (pXSprite.data2 == 11520) // don't inherit palette for burning if using default animation
		                              pSprite.pal = 0;

								aiNewState(pSprite, pXSprite, burnGoto[GDXGENDUDE]);
								actHealDude(pXSprite, dudeInfo[55].startHealth,dudeInfo[55].startHealth);
								if (pXSprite.burnTime <= 0) pXSprite.burnTime = 1200;
								aiClock[pSprite.xvel] = gFrameClock + 360;
								return;
							}

					} else {
						pXSprite.burnTime = 0;
						pXSprite.burnSource = -1;
						nDamageType = kDamageFall;
					}
				}

			} else {
				int seqId = pXSprite.data2 + 18;
				sfxPlayGDXGenDudeSound(pSprite,10);

				if (!BuildGdx.cache.contains(seqId,seq)) {
					seqKill(SS_SPRITE, nXSprite);

					SPRITE pEffect = actSpawnEffect(52,pSprite.sectnum,pSprite.x,pSprite.y,pSprite.z,pSprite.ang);
					if (pEffect != null) {
						pEffect.cstat = ru.m210projects.Blood.Globals.kSpriteFace;
						pEffect.pal = 6;
						pEffect.xrepeat = pSprite.xrepeat;
						pEffect.yrepeat = pSprite.yrepeat;
					}

					for (int i = 0; i < 3; i++) {
						if (Chance(0x3000)) actGenerateGibs(pSprite, 6, null, null);
						else if (Chance(0x2000)) actGenerateGibs(pSprite, 5, null, null);
						else actGenerateGibs(pSprite, 17, null, null);
					}

					return;
				}
				seqSpawn(seqId, SS_SPRITE,nXSprite, null);
				return;
			}
		}

		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			if (nSprite == gPlayer[i].fraggerID && gPlayer[i].deathTime > 0)
				gPlayer[i].fraggerID = -1;
		}
		if (pSprite.lotag != kDudeBeastCultist)
			trTriggerSprite(nSprite, pXSprite, kCommandOff); // trigger death message

		pSprite.hitag |= kAttrMove | kAttrGravity | kAttrFalling;

		if (IsPlayerSprite(sprite[nSource]) && pGameInfo.nGameType == kNetModeCoop) {
			PLAYER pPlayer = gPlayer[sprite[nSource].lotag - kDudePlayer1];
			pPlayer.fragCount++;
		}

		if (pXSprite.key > 0)
			DropPickupObject(pSprite, kItemKey1 + pXSprite.key - 1);
		if (pXSprite.dropMsg != 0) {
			DropPickupObject(pSprite, pXSprite.dropMsg);
			if (!IsOriginalDemo())
				pXSprite.dropMsg = 0;
		}

		// drop any items or weapons
		if (pSprite.lotag == kDudeTommyCultist) {
			int nDropCheck = Random(100);

			// constants? table?
			if (nDropCheck >= 10) {
				if (nDropCheck < 50)
					DropPickupObject(pSprite, 69);
			} else
				DropPickupObject(pSprite, 42);
		} else if (pSprite.lotag == kDudeShotgunCultist) {
			int nDropCheck = Random(100);
			if (nDropCheck > 10) {
				if (nDropCheck <= 50)
					DropPickupObject(pSprite, 67);
			} else
				DropPickupObject(pSprite, 41);
		}

		int deathType = 0;
		switch (nDamageType) {
			case kDamageBurn:
				deathType = kSeqDudeDeath3;
				sfxStart3DSound(pSprite, 351, -1, 0);
				break;

			case kDamageExplode:
				deathType = kSeqDudeDeath2;
				switch (pSprite.lotag) {
					case kGDXDudeUniversalCultist:
					case kGDXGenDudeBurning:
						sfxPlayGDXGenDudeSound(pSprite,4);
						break;
					case kDudeTommyCultist:
					case kDudeShotgunCultist:
					case kDudeFanaticProne:
					case kDudeBurning:
					case kDudeCultistBurning:
					case kDudeInnocent:
					case kDudeCultistProne:
					case kDudeTeslaCultist:
					case kDudeDynamiteCultist:
					case kDudeBeastCultist:
					case kDudeTinyCaleb:
					case kDudeTinyCalebburning:
						sfxStart3DSound(pSprite, 717, -1, 0);
						break;
				}
				break;
			case kDamageSpirit:
				switch (pSprite.lotag) {
				case kDudeAxeZombie:
				case kDudeEarthZombie:
					deathType = 14;
					break;
				case kDudeButcher:
					deathType = 11;
					break;
				default:
					deathType = kSeqDudeDeath1;
					break;
				}
				break;
			default:
				deathType = kSeqDudeDeath1;
				break;
		}

		// are we missing this sequence? if so, just delete it
		if (!BuildGdx.cache.contains(pDudeInfo.seqStartID + deathType, seq)) {
			System.out.println("sprite missing death sequence: deleted ");
			levelAddKills(pSprite);

			if (pSprite.owner != -1 && pSprite.owner != 32666) {
				int owner = actGetOwner(pSprite);
				switch(sprite[owner].lotag) {
					case kGDXDudeUniversalCultist:
						aiThinkTime[owner]--;
						break;
					default:
						break;
				}
			}

			seqKill(SS_SPRITE, nXSprite); // make sure we remove any active sequence
			actPostSprite(nSprite, kStatFree);
			return;
		}

		switch (pSprite.lotag) {
		case kDudeAxeZombie:
			sfxStart3DSound(pSprite, Random(2) + 1107, -1, 0);
			if (deathType == 2) {
				seqSpawn(pDudeInfo.seqStartID + 2, SS_SPRITE, nXSprite,
						callbacks[DamageDude]);
				GetSpriteExtents(pSprite);
				startPos.set(pSprite.x, pSprite.y, extents_zTop);
				startVel.set(sprXVel[pSprite.xvel] >> 1,
						sprYVel[pSprite.xvel] >> 1, -838860);
				actGenerateGibs(pSprite, 27, startPos, startVel);
			} else if (deathType != 1 || !Chance(0x2000)) {
				if (deathType == 14)
					seqSpawn(pDudeInfo.seqStartID + 14, SS_SPRITE, nXSprite, null);
				else if (deathType == 3)
					seqSpawn(pDudeInfo.seqStartID + 13, SS_SPRITE, nXSprite,
							callbacks[DamageDude2]);
				else {
					seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE,
							nXSprite, callbacks[DamageDude]);
				}
			} else {
				seqSpawn(pDudeInfo.seqStartID + 7, SS_SPRITE, nXSprite,
						callbacks[DamageDude]);
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 5);
				pXSprite.data1 = 35;
				pXSprite.data2 = 5;
				GetSpriteExtents(pSprite);
				startPos.set(pSprite.x, pSprite.y, extents_zTop);
				startVel.set(sprXVel[pSprite.xvel] >> 1,
						sprYVel[pSprite.xvel] >> 1, -1118481);
				actGenerateGibs(pSprite, 27, startPos, startVel);
				sfxStart3DSound(pSprite, 362, -1, 0);
			}
			break;

		case kDudeTommyCultist:
		case kDudeShotgunCultist:
		case kDudeTeslaCultist:
		case kDudeDynamiteCultist:
			sfxStart3DSound(pSprite, Random(2) + 1018, -1, 0);
			if (deathType == kSeqDudeDeath3)
				seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
			else
				seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude]);

			break;
		case kDudeCultistBurning:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 718, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1018, -1, 0);
			nDamageType = kDamageExplode;
			if (Chance(0x4000)) {
				for (int i = 0; i < 3; i++)
					actGenerateGibs(pSprite, 7, null, null);

				if (GAMEVER == VER100)
					seqSpawn(802 + Random(1), SS_SPRITE, nXSprite, callbacks[DamageDude]);
				else
					seqSpawn(16 + pDudeInfo.seqStartID - Random(1), SS_SPRITE,
							nXSprite, callbacks[DamageDude]);

			} else {
				seqSpawn(15 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite,
						callbacks[DamageDude2]);
			}
			break;
		case kGDXDudeUniversalCultist:
			sfxPlayGDXGenDudeSound(pSprite,2);

			if (deathType == kSeqDudeDeath3) {

				boolean seq15 = BuildGdx.cache.contains(pXSprite.data2 + 15, seq); boolean seq16 = BuildGdx.cache.contains(pXSprite.data2 + 16, seq);
				if (seq15 && seq16) seqSpawn((15 + Random(2)) + pXSprite.data2, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
				else if (seq16) seqSpawn(16 + pXSprite.data2, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
				else if (seq15) seqSpawn(15 + pXSprite.data2, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
				else if (BuildGdx.cache.contains(pXSprite.data2 + deathType, seq))
					seqSpawn(deathType + pXSprite.data2, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
				else
					seqKill(SS_SPRITE,nXSprite);

			} else {
				seqSpawn(deathType + pXSprite.data2, SS_SPRITE, nXSprite, callbacks[DamageDude]);
			}

			pXSprite.txID = 0; // to avoid second trigger.
			break;
		case kGDXGenDudeBurning:
			sfxPlayGDXGenDudeSound(pSprite,4);
			nDamageType = kDamageExplode;

			if (Chance(0x4000)) {
				for (int i = 0; i < 3; i++) {
					actGenerateGibs(pSprite, 7, null, null);
				}
			}


	        int seqId = pXSprite.data2;
	        boolean seq15 = BuildGdx.cache.contains(pXSprite.data2 + 15, seq); boolean seq16 = BuildGdx.cache.contains(pXSprite.data2 + 16, seq);

	        if (seq15 && seq16) seqId += (15 + Random(2));
	        else if (seq16) seqId+=16;
	        else seqId +=15;

	        seqSpawn(seqId, 3, nXSprite, callbacks[DamageDude]);
			break;

		case kDudeAxeZombieBurning:
			if (Chance(0x4000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1109, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1107, -1, 0);
			nDamageType = kDamageExplode;
			if (Chance(0x4000)) {
				seqSpawn(13 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite,
						callbacks[DamageDude]);
				GetSpriteExtents(pSprite);
				startPos.set(pSprite.x, pSprite.y, extents_zTop);
				startVel.set(sprXVel[pSprite.xvel] >> 1,
						sprYVel[pSprite.xvel] >> 1, -838860);
				actGenerateGibs(pSprite, 27, startPos, startVel);
			} else
				seqSpawn(13 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite,
						callbacks[DamageDude2]);
			break;

		case kDudeBloatedButcherBurning:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1206, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1204, -1, 0);
			seqSpawn(10 + dudeInfo[4].seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeBurning:
			nDamageType = kDamageExplode;
			seqSpawn(7 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude]);
			break;

		case kDudeButcher:
			if (deathType == 14) {
				sfxStart3DSound(pSprite, 1206, -1, 0);
				seqSpawn(11 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			} else {
				sfxStart3DSound(pSprite, Random(2) + 1204, -1, 0);
				if (deathType != kSeqDudeDeath3)
					seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE,
							nXSprite, null);
				else
					seqSpawn(10 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			}
			break;

		case kDudeFleshGargoyle:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1405, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1403, -1, 0);

			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeStoneGargoyle:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1455, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1453, -1, 0);

			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudePhantasm:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1605, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1603, -1, 0);

			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeHound:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1305, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1303, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeHand:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1905, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1903, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeBrownSpider:
		case kDudeRedSpider:
		case kDudeBlackSpider:
			if (pSprite.owner != -1)
				aiThinkTime[sprite[actGetOwner(pSprite)].xvel]--;
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1805, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1803, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;
		case kDudeMotherSpider:
			sfxStart3DSound(pSprite, 1850, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeGillBeast:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1705, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1703, -1, 0);

			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeEel:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1505, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1503, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeBat:
			if (Chance(0x2000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 2005, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 2003, -1, 0);

			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeRat:
			if (!Chance(0x2000) || deathType != kSeqDudeDeath3)
				sfxStart3DSound(pSprite, Random(2) + 2103, -1, 0);
			else
				sfxStart3DSound(pSprite, 2105, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeGreenPod:
		case 225:
		case 226:
			if (!Chance(0x2000) || deathType != kSeqDudeDeath3)
				sfxStart3DSound(pSprite, Random(2) + 2203, -1, 0);
			else
				sfxStart3DSound(pSprite, 2205, -1, 0);
			pSprite.cstat &= ~kSpriteFlipY;
			pSprite.cstat &= ~kSpriteFlipX;
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeGreenTentacle:
			if (nDamage == 5)
				sfxStart3DSound(pSprite, 2171, -1, 0);
			else
				sfxStart3DSound(pSprite, 2172, -1, 0);
			pSprite.cstat &= ~kSpriteFlipY;
			pSprite.cstat &= ~kSpriteFlipX;
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;
		case kDudeFirePod:
			if (nDamage == 5)
				sfxStart3DSound(pSprite, 2151, -1, 0);
			else
				sfxStart3DSound(pSprite, 2152, -1, 0);
			pSprite.cstat &= ~kSpriteFlipY;
			pSprite.cstat &= ~kSpriteFlipX;
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;
		case kDudeFireTentacle:
			sfxStart3DSound(pSprite, 2501, -1, 0);
			pSprite.cstat &= ~kSpriteFlipY;
			pSprite.cstat &= ~kSpriteFlipX;
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeCerberus:
		case kDudeCerberus2:
			if (!Chance(0x2000) || deathType != kSeqDudeDeath3)
				sfxStart3DSound(pSprite, Random(2) + 2305, -1, 0);
			else
				sfxStart3DSound(pSprite, 2305, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		default:
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeTchernobog:
			sfxStart3DSound(pSprite, 2380, -1, 0);
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;

		case kDudeTinyCalebburning:
			nDamageType = kDamageExplode;
			seqSpawn(11 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude]);
			break;

		case kDudeTheBeast:

			sfxStart3DSound(pSprite, Random(2) + 9000, -1, 0);
			if (deathType == kSeqDudeDeath3)
				seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite,
						callbacks[DamageDude2]);
			else
				seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite,
						callbacks[DamageDude]);
			break;

		case kDudeTheBeastburning:
			nDamageType = kDamageExplode;
			seqSpawn(12 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude]);
			break;
		}
									// 32666 = custom dude had once life leech
		if (pSprite.owner != -1 && pSprite.owner != 32666) {
			int owner = actGetOwner(pSprite);
			switch(sprite[owner].lotag) {
				case kGDXDudeUniversalCultist:
		        case kGDXGenDudeBurning:
					aiThinkTime[owner]--;
					break;
				default:
					break;
			}
		}

		if (nDamageType == kDamageExplode) {
			for (int i = 0; i < 3; i++) {
				int nGibType = pDudeInfo.nGibType[i];
				if (nGibType > -1)
					actGenerateGibs(pSprite, nGibType, null, null);

			}
			for (int j = 0; j < 4; ++j)
				actSpawnBlood(pSprite);
		}
		levelAddKills(pSprite);
		actCheckRespawn(pSprite);

		if (nXSprite > 0 && !IsOriginalDemo()) {
			if (xsprite[nXSprite].Proximity)
				xsprite[nXSprite].Proximity = false;
		}

		pSprite.lotag = 426;
		actPostSprite(nSprite, kStatThing);
	}

	public static void MoveDude(int nSprite) {
		SPRITE pSprite = sprite[nSprite];

		int nXSprite = pSprite.extra;
		XSPRITE pXSprite = xsprite[nXSprite];

		PLAYER pPlayer = null;
		if (IsPlayerSprite(pSprite))
			pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];

		if (!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int zTop, zBot;
		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		int floorDist = (zBot - pSprite.z) / 4;
		int ceilDist = (pSprite.z - zTop) / 4;

		int clipDist = pSprite.clipdist << 2;
		short nSector = pSprite.sectnum;
		if (!(nSector >= 0 && nSector < kMaxSectors))
			game.dassert("nSector >= 0 && nSector < kMaxSectors");

		if (sprXVel[pSprite.xvel] != 0 || sprYVel[pSprite.xvel] != 0) {
			if (pPlayer != null && gNoClip) {
				pSprite.x += sprXVel[pSprite.xvel] >> 12;
				pSprite.y += sprYVel[pSprite.xvel] >> 12;

				nSector = engine.updatesector(pSprite.x, pSprite.y, nSector);
				if (nSector == -1)
					nSector = pSprite.sectnum;
			} else {
				short oldcstat = pSprite.cstat;
				pSprite.cstat &= ~(kSpriteBlocking & kSpriteHitscan);

				gSpriteHit[nXSprite].moveHit = ClipMove(pSprite.x, pSprite.y,
						pSprite.z, nSector, sprXVel[pSprite.xvel] >> 12,
						sprYVel[pSprite.xvel] >> 12, clipDist, ceilDist,
						floorDist, CLIPMASK0 | 0x3000);

				pSprite.cstat = oldcstat;
				pSprite.x = clipm_px;
				pSprite.y = clipm_py;
				pSprite.z = clipm_pz;
				nSector = (short) clipm_pnsectnum;

				if (nSector == -1) {
					nSector = pSprite.sectnum;
					if (pSprite.statnum == kStatDude
							|| pSprite.statnum == kStatThing) {
						actDamageSprite(nSprite, pSprite, kDamageFall, 16000);
					}
				}

				if (sector[nSector].lotag >= kSectorPath
						&& sector[nSector].lotag <= kSectorRotate) {
					int push = engine.pushmove(pSprite.x, pSprite.y, pSprite.z,
							nSector, clipDist, ceilDist, floorDist, CLIPMASK0);
					pSprite.x = pushmove_x;
					pSprite.y = pushmove_y;
					pSprite.z = pushmove_z;

					if (push == -1) {
						actDamageSprite(nSprite, pSprite, kDamageFall, 16000);
					}

					if (pushmove_sectnum != -1)
						nSector = pushmove_sectnum;
				}

				if (!(nSector >= 0 && nSector < kMaxSectors))
					game.dassert("nSector >= 0 && nSector < kMaxSectors");
			}

			switch (gSpriteHit[nXSprite].moveHit & kHitTypeMask) {
			case kHitWall: {
				int nWall = gSpriteHit[nXSprite].moveHit & kHitIndexMask;
				WALL pWall = wall[nWall];
				if (pWall.extra > 0) {
					XWALL pXWall = xwall[pWall.extra];
					if (pXWall != null && pDudeInfo.lockOut
							&& pXWall.triggerPush && pPlayer == null
							&& pXWall.key == 0 && !pXWall.dudeLockout
							&& pXWall.state == 0 && pXWall.busy == 0) {
						trTriggerWall(nWall, pXWall, kCommandWallPush);
					}
				}

				if (pWall.nextsector != -1) {
					SECTOR pSector = sector[pWall.nextsector];

					if (pSector.extra > 0) {
						XSECTOR pXSector = xsector[pSector.extra];
						if (pDudeInfo.lockOut && pXSector.Wallpush
								&& pXSector.Key == 0 && !pXSector.dudelockout
								&& pXSector.state == 0 && pXSector.busy == 0
								&& pPlayer == null) {
							trTriggerSector(pWall.nextsector, pXSector,
									kCommandSectorPush);
						}
						// if ( zTop >= pSector.floorz ) ???
						// v354 = zBot < pSector.ceilingz;
					}
				}

				ReflectVector(sprXVel[pSprite.xvel], sprYVel[pSprite.xvel], nWall, 0);

				sprXVel[pSprite.xvel] = refl_x;
				sprYVel[pSprite.xvel] = refl_y;
				break;
			}
			case kHitSprite: {
				int nHSprite = gSpriteHit[nXSprite].moveHit & kHitIndexMask;
				SPRITE pHSprite = sprite[nHSprite];

				if (pHSprite.statnum == kStatMissile
						&& (pHSprite.hitag & kAttrFree) == 0
						&& actGetOwner(pHSprite) != pSprite.xvel) {
					short oldHitSect = pHitInfo.hitsect;
					short oldHitSprite = pHitInfo.hitsprite;
					int oldHitX = pHitInfo.hitx;
					int oldHitY = pHitInfo.hity;
					int oldHitZ = pHitInfo.hitz;
					pHitInfo.hitsprite = pSprite.xvel;
					actImpactMissile(pHSprite, SS_SPRITE);
					pHitInfo.hitsect = oldHitSect;
					pHitInfo.hitsprite = oldHitSprite;
					pHitInfo.hitx = oldHitX;
					pHitInfo.hity = oldHitY;
					pHitInfo.hitz = oldHitZ;
				}

				if (pHSprite.extra > 0) {
					XSPRITE pXHSprite = xsprite[pHSprite.extra];
					if (pXHSprite != null) {
											// this is why touch for things never worked; they always ON
						if (pXHSprite.Touch && /*pXHSprite.state == 0 &&*/ !pXHSprite.isTriggered) {
							if (!pXHSprite.DudeLockout || IsPlayerSprite(pSprite)) // allow dudeLockout for Touch flag
								trTriggerSprite(nHSprite, pXHSprite, kCommandSpriteTouch);
						}

						if (pDudeInfo.lockOut && pXHSprite.Push && pXHSprite.key == 0 && !pXHSprite.DudeLockout
								&& pXHSprite.state == 0 && pXHSprite.busy == 0 && pPlayer == null) {
									trTriggerSprite(nHSprite, pXHSprite, kCommandSpritePush);
						}

					}
				}
				break;
			}

			}
		} else {
			if (!(nSector >= 0 && nSector < kMaxSectors))
				game.dassert("nSector >= 0 && nSector < kMaxSectors");
			FindSector(pSprite.x, pSprite.y, pSprite.z, nSector);
			nSector = foundSector;
		}

		if (pSprite.sectnum != nSector) {
			if (!(nSector >= 0 && nSector < kMaxSectors))
				game.dassert("nSector >= 0 && nSector < kMaxSectors");

			int nXSector = sector[pSprite.sectnum].extra;
			if (nXSector > 0 && xsector[nXSector].Exit
					&& (pPlayer != null || !xsector[nXSector].dudelockout))
				trTriggerSector(pSprite.sectnum, xsector[nXSector],
						kCommandSectorExit);

			changespritesect(pSprite.xvel, nSector);

			nXSector = sector[nSector].extra;

			if (nXSector > 0 && xsector[nXSector].Enter
					&& (pPlayer != null || !xsector[nXSector].dudelockout)) {
				if (sector[nSector].lotag == 604) {
					if (pPlayer != null)
						xsector[nXSector].data = pSprite.xvel;
					else
						xsector[nXSector].data = -1;
				}

				trTriggerSector(nSector, xsector[nXSector], kCommandSectorEnter);
			}

			nSector = pSprite.sectnum;
		}

		boolean underwater = false, depth = false;
		if (sector[nSector].extra > 0) {
			if (xsector[sector[nSector].extra].Underwater)
				underwater = true;
			if (xsector[sector[nSector].extra].Depth != 0)
				depth = true;
		}

		int nUpper = gUpperLink[nSector], nLower = gLowerLink[nSector];
		if (nUpper >= 0) {
			if (sprite[nUpper].lotag == kStatPurge
					|| sprite[nUpper].lotag == kStatSpares)
				depth = true;
		}
		if (nLower >= 0) {
			if (sprite[nLower].lotag == kStatMarker
					|| sprite[nLower].lotag == kStatFlare)
				depth = true;
		}

		if (pPlayer != null)
			clipDist += 16;

		if (sprZVel[pSprite.xvel] != 0)
			pSprite.z += sprZVel[pSprite.xvel] >> 8;

		int ceilz, ceilhit, floorz, floorhit;

		GetZRange(pSprite, clipDist, CLIPMASK0 | 0x3000);
		ceilz = gz_ceilZ;
		ceilhit = gz_ceilHit;
		floorz = gz_floorZ;
		floorhit = gz_floorHit;
		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if ((pSprite.hitag & kAttrGravity) != 0) {
			int G = 58254;

			if (!depth) {
				if (underwater || zBot >= floorz)
					G = 0; // если ноги ниже уровня пола
			} else {
				if (underwater) {
					int ceilzofslope = engine.getceilzofslope(nSector,
							pSprite.x, pSprite.y);
					if (ceilzofslope > zTop) // если голова выше уровня воды
					{
						if (zBot != zTop)
							G += -80099 * (zBot - ceilzofslope) / (zBot - zTop);
					} else
						G = 0;
				} else {
					int florzofslope = engine.getflorzofslope(nSector,
							pSprite.x, pSprite.y);
					if (florzofslope < zBot && (zBot != zTop)) // если ноги ушли
																// под воду
						G += -80099 * (zBot - florzofslope) / (zBot - zTop);
				}
			}

			if (G != 0) {
				pSprite.z += 2 * G >> 8;
				sprZVel[pSprite.xvel] += G;
			}
		}

		if (pPlayer != null && sprZVel[pSprite.xvel] > kScreamVel
				&& !pPlayer.fScreamed && pXSprite.height != 0) {
			pPlayer.fScreamed = true;
			sfxStart3DSound(pSprite, 719, 0, 0);
		}

		// check for warping in linked sectors
		int warp = checkWarping(pSprite);
		if (warp != 0) {
			GetZRange(pSprite, clipDist, CLIPMASK0 | 0x3000);
			ceilz = gz_ceilZ;
			ceilhit = gz_ceilHit;
			floorz = gz_floorZ;
			floorhit = gz_floorHit;

			if (pPlayer != null) {
				viewUpdatePlayerLoc(pPlayer);
				sfxResetListener();
			}

			/* Make select palette for both lower and upper markers.
			   Currently now working for non-water sectors for some reason.
			if (warp >= kMarkerUpperLink && warp != kMarkerWarpDest && warp <= kMarkerLowerGoo){
				int gViewPal=0; int var1=-1;
				int nUpperTMP = gUpperLink[nSector]; int nLowerTMP = gLowerLink[nSector];

				if (nUpperTMP >= 0) var1 = nUpperTMP;
				else if (nLowerTMP >= 0) var1 = nLowerTMP;
				if (var1 != -1){
					SPRITE pLinkMarker = sprite[var1];
					if (pLinkMarker.extra >= 0) {
						XSPRITE pXLinkMarker = xsprite[pLinkMarker.extra];
						gViewPal = pXLinkMarker.data2;
						if (warp == kMarkerUpperStack)System.out.println("crush"+pXLinkMarker);
					}
					if (gViewPal <= 0) {
						if (warp == kMarkerUpperWater) pXSprite.palette = 1;
						else pXSprite.palette = 3;
						// *Real* Goo palette ID is 3, previously it was 2.
						// We need fixt it because there is no control of it in view anymore
					} else {
						pXSprite.palette = gViewPal;
					}
				}
			}*/
			switch (warp) {

			case kMarkerLowerStack:
				if (pPlayer != null && pPlayer.nPlayer == gViewIndex)
					engine.setgotpic(sector[pSprite.sectnum].floorpicnum);
				break;
			case kMarkerUpperStack:
				if (pPlayer != null && pPlayer.nPlayer == gViewIndex)
					engine.setgotpic(sector[pSprite.sectnum].ceilingpicnum);
				break;
			case kMarkerLowerWater:
			case kMarkerLowerGoo:
				pXSprite.palette = kPalNormal;
				if (pPlayer != null) {
					pPlayer.moveState = 0;
					pPlayer.bubbleTime = 0;
					if (!pPlayer.pJump && pPlayer.pInput.Jump) {
						sprZVel[pSprite.xvel] = -436906;
						pPlayer.pJump = true;
					}
					sfxStart3DSound(pSprite, 721, -1, 0);// out of water
				} else {
					switch (pSprite.lotag) {
					case kDudeTommyCultist:
					case kDudeShotgunCultist:
					case kDudeTeslaCultist:
					case kDudeDynamiteCultist:
					case kDudeBeastCultist:
						aiNewState(pSprite, pXSprite, cultistGoto[LAND]);
						break;
					case kDudeGillBeast:
						aiNewState(pSprite, pXSprite, gillBeastGoto[LAND]);
						pSprite.hitag |= (kAttrGravity | kAttrFalling);
						break;
					case kDudeEel:
						actKillSprite(pSprite.xvel, pSprite, 0, 16000);
						break;
					}
				}
				break;
			case kMarkerUpperWater:
			case kMarkerUpperGoo:
				// look for palette in data2 of marker. If value <= 0, use default ones.
				int gViewPal=0;
				SPRITE pUpper = sprite[gUpperLink[nSector]];
				if (pUpper.extra >= 0) gViewPal = xsprite[pUpper.extra].data2;
				if (gViewPal <= 0) {

					// *Real* Goo palette ID is 3, but monsters think it's 2, so we need leave it as is just for them,
					// so they can move normally in Goo and attack, while player can use any palette.
					if (warp == kMarkerUpperWater) pXSprite.palette = kPalWater;
					else if (pPlayer != null) pXSprite.palette = kPalSewer;
					else pXSprite.palette = 2;

				}
				else if (pPlayer != null) pXSprite.palette = gViewPal; // Player can use any palette.
				else if (gViewPal > 2) pXSprite.palette = 2; // Monsters should not use palette greater than 2, so they correct detect if they underwater.


				if (pPlayer != null) {
					pPlayer.moveState = 1;
					pPlayer.pXsprite.burnTime = 0;
					int zvel = (int) klabs(sprZVel[pSprite.xvel]);
					pPlayer.bubbleTime = zvel >> 12;
					evPostCallback(pSprite.xvel, SS_SPRITE, 0, 10);
					if (pPlayer.fScreamed)
						sfxKill3DSound(pSprite, -1, 719);
					sfxStart3DSound(pSprite, 720, -1, 0);// into water
				} else {
					switch (pSprite.lotag) {
					case kDudeTommyCultist:
					case kDudeShotgunCultist:
					case kDudeTeslaCultist:
					case kDudeDynamiteCultist:
					case kDudeBeastCultist:
						pXSprite.burnTime = 0;
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						sfxStart3DSound(pSprite, 720, -1, 0);
						aiNewState(pSprite, pXSprite, cultistGoto[WATER]);
						break;
					case kDudeAxeZombie:
						pXSprite.burnTime = 0;
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						sfxStart3DSound(pSprite, 720, -1, 0);
						aiNewState(pSprite, pXSprite, zombieAGoto);
						break;
					case kDudeGillBeast:
						pXSprite.burnTime = 0;
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						sfxStart3DSound(pSprite, 720, -1, 0);
						aiNewState(pSprite, pXSprite, gillBeastGoto[WATER]);
						pSprite.hitag &= ~(kAttrGravity | kAttrFalling);
						break;
					case kDudeFleshGargoyle:
					case kDudeHound:
					case kDudeBlackSpider:
					case kDudeBrownSpider:
					case kDudeRedSpider:
					case kDudeBat:
					case kDudeRat:
					case kDudeBurning:
						actKillSprite(pSprite.xvel, pSprite, 0, 16000);
						break;
					case kDudeCultistBurning:
						// There is no difference between water and goo except following chance:
						if (Chance(warp == kMarkerUpperGoo ? 0x200 : 0xa00)) pSprite.lotag = kDudeTommyCultist;
						else pSprite.lotag = kDudeShotgunCultist;
						pXSprite.burnTime = 0;
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						sfxStart3DSound(pSprite, 720, -1, 0);
						aiNewState(pSprite, pXSprite, cultistGoto[WATER]);
						break;
					case kDudeButcher:
						pXSprite.burnTime = 0;
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						sfxStart3DSound(pSprite, 720, -1, 0);
						aiNewState(pSprite, pXSprite, zombieFGoto);
						break;
					case kGDXDudeUniversalCultist:
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						if (!canSwim(pSprite)) actKillSprite(pSprite.xvel, pSprite, 0, 16000);
						break;
					}
				}

				break;
			}
		}

		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if (pPlayer != null && zBot >= floorz) {
			int ofloorz = floorz;
			int ofloorhit = floorhit;
			GetZRange(pSprite, pSprite.clipdist << 2, CLIPMASK0 | 0x3000);

			ceilz = gz_ceilZ;
			ceilhit = gz_ceilHit;
			floorz = gz_floorZ;
			floorhit = gz_floorHit;

			if (zBot <= floorz && pSprite.z - ofloorz < floorDist) {
				floorz = ofloorz;
				floorhit = ofloorhit;
			}
		}

		if (floorz > zBot) {
			gSpriteHit[nXSprite].floorHit = 0;
			if ((pSprite.hitag & kAttrGravity) != 0)
				pSprite.hitag |= kAttrFalling;
		} else { // hit floor?
			gSpriteHit[nXSprite].floorHit = floorhit;
			pSprite.z += (floorz - zBot);

			int dZvel = (int) (sprZVel[pSprite.xvel] - floorVel[pSprite.sectnum]);
			if (dZvel <= 0) {
				if (sprZVel[pSprite.xvel] == 0)
					pSprite.hitag &= ~kAttrFalling;
			} else {
				long gravity = GravityVector(sprXVel[pSprite.xvel],
						sprYVel[pSprite.xvel], dZvel, pSprite.sectnum, 0);
				int fallDamage = mulscale(gravity, gravity, 30);
				sprXVel[pSprite.xvel] = refl_x;
				sprYVel[pSprite.xvel] = refl_y;

				dZvel = (int) refl_z;
				if (pPlayer != null) {
					pPlayer.fScreamed = false;
					if (fallDamage > 240 && (pSprite.hitag & kAttrFalling) != 0) {
						playSurfSound(pPlayer);
					}

					if (fallDamage > 480) {
						sfxStart3DSound(pSprite, 701, 0, 0);
					}
				}
				fallDamage -= 1600;
				if (fallDamage > 0) {
					actDamageSprite(pSprite.xvel, pSprite, kDamageFall,
							fallDamage);
				}

				sprZVel[pSprite.xvel] = dZvel;
				if (klabs(dZvel) >= 65536) {
					pSprite.hitag |= kAttrFalling;
				} else {
					sprZVel[pSprite.xvel] = floorVel[pSprite.sectnum];
					pSprite.hitag &= ~kAttrFalling;
				}

				switch (game.getCurrentDef().GetSurfType(floorhit)) {
				case 5:
					actSpawnEffect(9, pSprite.sectnum, pSprite.x, pSprite.y,
							floorz, 0);
					break;
				case 14:
					SPRITE pSpawn = actSpawnEffect(10, pSprite.sectnum,
							pSprite.x, pSprite.y, floorz, 0);
					if (pSpawn != null) {
						for (int i = 0; i < 7; i++) {
							SPRITE pSpawn2 = actSpawnEffect(14, pSpawn.sectnum,
									pSpawn.x, pSpawn.y, pSpawn.z, 0);
							if (pSpawn2 != null) {
								sprXVel[pSpawn2.xvel] = BiRandom(436906);
								sprYVel[pSpawn2.xvel] = BiRandom(436906);
								sprZVel[pSpawn2.xvel] = -Random(873813);
							}
						}
					}
					break;
				}
			}
		}

		if (ceilz < zTop) {
			gSpriteHit[nXSprite].ceilHit = 0;
		} else // hit ceiling
		{
			gSpriteHit[nXSprite].ceilHit = ceilhit;
			if (ceilz - zTop > 0)
				pSprite.z += ceilz - zTop;

			if (sprZVel[pSprite.xvel] <= 0
					&& (pSprite.hitag & kAttrFalling) != 0)
				sprZVel[pSprite.xvel] = (-sprZVel[pSprite.xvel] / 8);
		}

		GetSpriteExtents(pSprite);
		zTop = extents_zTop;
		zBot = extents_zBot;

		if (floorz - zBot > 0)
			pXSprite.height = (floorz - zBot) >> 8;
		else
			pXSprite.height = 0;

		// drag and friction
		if (sprXVel[pSprite.xvel] != 0 || sprYVel[pSprite.xvel] != 0) {

			if ((floorhit & kHitTypeMask) == kHitSprite) {
				int nUnderSprite = floorhit & kHitIndexMask;
				if ((sprite[nUnderSprite].cstat & kSpriteRMask) == kSpriteFace) {
					// push it off the face sprite
					sprXVel[pSprite.xvel] += mulscale(kFrameTicks, pSprite.x
							- sprite[nUnderSprite].x, 2);
					sprYVel[pSprite.xvel] += mulscale(kFrameTicks, pSprite.y
							- sprite[nUnderSprite].y, 2);
					return;
				}
			}

			nSector = pSprite.sectnum;
			if (sector[nSector].extra <= 0
					|| !xsector[sector[nSector].extra].Underwater) {
				if (pXSprite.height < 256) {
					int kv = kDudeDrag;
					if (pXSprite.height != 0)
						kv -= mulscale(pXSprite.height, kv, 8);

					sprXVel[pSprite.xvel] -= dmulscale(kv,
							(int) sprXVel[pSprite.xvel], 0x8000, 1, 16);
					sprYVel[pSprite.xvel] -= dmulscale(kv,
							(int) sprYVel[pSprite.xvel], 0x8000, 1, 16);

					if (engine.qdist(sprXVel[pSprite.xvel],
							sprYVel[pSprite.xvel]) < kMinDudeVel) {
						sprXVel[pSprite.xvel] = sprYVel[pSprite.xvel] = 0;
					}
				}
			}
		}
	}

	public static final int kGlobalForceShift = 18; // arbitrary scale for all
													// concussion

	public static void ConcussSprite(int nSource, SPRITE pSprite, int x, int y,
			int z, int damage) {

		if (pSprite == null)
			game.dassert("pSprite != null");
		int dx = pSprite.x - x;
		int dy = pSprite.y - y;
		int dz = (pSprite.z - z) >> 4;

		int dist2 = dx * dx + dy * dy + dz * dz + (1 << kGlobalForceShift);

		Tile pic = engine.getTile(pSprite.picnum);
		int area = pic.getWidth() * pSprite.xrepeat * pic.getHeight()
				* pSprite.yrepeat >> 1;
		long force = divscale(damage, dist2, kGlobalForceShift);

		if ((pSprite.hitag & kAttrMove) != 0) {
			int mass = 0;
			if (IsDudeSprite(pSprite)) {
				mass = dudeInfo[pSprite.lotag - kDudeBase].mass;
				switch (pSprite.lotag) {
					case kGDXDudeUniversalCultist:
					case kGDXGenDudeBurning:
						mass = getDudeMassBySpriteSize(pSprite);
						break;
				}
			} else if (pSprite.lotag >= kThingBase && pSprite.lotag < kThingMax)
				mass = thingInfo[pSprite.lotag - kThingBase].mass;
			else Console.Println("Unexpected type encountered in ConcussSprite()");

			if (mass == 0)
				game.dassert("mass != 0, type: " + pSprite.lotag + " picnum: " + pSprite.picnum);
			int impulse = muldiv(force, area, klabs(mass));

			dx = mulscale(impulse, dx, 16);
			dy = mulscale(impulse, dy, 16);
			dz = mulscale(impulse, dz, 16);

			int xvel = pSprite.xvel;
			if (!(xvel >= 0 && xvel < kMaxSprites))
				game.dassert("xvel >= 0 && xvel < kMaxSprites ");

			sprXVel[xvel] += dx;
			sprYVel[xvel] += dy;
			sprZVel[xvel] += dz;
		}
		actDamageSprite(nSource, pSprite, kDamageExplode, (int) force);
	}

	/*******************************************************************************
	 * FUNCTION: ReflectVector()
	 *
	 * DESCRIPTION: Reflects a vector off a wall
	 *
	 * PARAMETERS: nFraction is elasticity (0x10000 == perfectly elastic)
	 *******************************************************************************/
	public static long refl_x, refl_y, refl_z;

	public static void ReflectVector(long dx, long dy, int nWall, int nFraction) {
		refl_x = dx;
		refl_y = dy;

		// calculate normal for wall
		GetWallNormal(nWall);
		long nx = (long) normal.x;
		long ny = (long) normal.y;

		long dotProduct = dmulscale(dx, nx, dy, ny, 16);
		long sd = dmulscale(nFraction + 0x10000, dotProduct, 0x8000, 1, 16);

		refl_x -= mulscale(nx, sd, 16);
		refl_y -= mulscale(ny, sd, 16);

		// return mulscale(0x10000 - nFraction, dotProduct + 0x8000, 16);
	}

	public static long GravityVector(long dx, long dy, long dz, int nSector,
			int nFraction) {
		long result = 0;
		if (sector[nSector].floorheinum != 0) {
			WALL pWall = wall[sector[nSector].wallptr];
			int nAngle = engine.getangle(wall[pWall.point2].x - pWall.x,
					wall[pWall.point2].y - pWall.y) + kAngle90;
			int nSlope = sector[nSector].floorheinum << 4;
			long dist = engine.qdist(nSlope, 65536);

			long mp = divscale(nSlope, dist, 16);
			long mg = divscale(-65536, dist, 16);

			int cx = mulscale(Cos(nAngle), mp, 30);
			int cy = mulscale(Sin(nAngle), mp, 30);

			long avel = (cx * dx + cy * dy + mg * dz) >> 16;
			long svel = mulscale(avel, nFraction + 0x10000, 16);

			dx -= mulscale(svel, cx, 16);
			dy -= mulscale(svel, cy, 16);
			dz -= mulscale(svel, mg, 16);

			result = mulscale(65536 - nFraction, avel, 16);

		} else {
			result = mulscale(65536 - nFraction, dz, 16);
			dz = result - dz;
		}
		refl_x = dx;
		refl_y = dy;
		refl_z = dz;
		return result;
	}

	public static void playSurfSound(PLAYER pPlayer) {
		SPRITE pSprite = pPlayer.pSprite;
		if (pPlayer.pSprite == null)
			return;
		SPRITEHIT pSpriteHit = gSpriteHit[pSprite.extra];
		if (pSpriteHit.floorHit != 0) {
			int surfType = game.getCurrentDef().GetSurfType(pSpriteHit.floorHit);
			int nHitObject = pSpriteHit.floorHit & kHitIndexMask;
			boolean IsDudeSprite = false;
			if ((pSpriteHit.floorHit & kHitTypeMask) == kHitSprite)
				IsDudeSprite = IsDudeSprite(sprite[nHitObject]);
			if (surfType != 0 && !IsDudeSprite)
				sfxStart3DSound(pSprite, surfSfxLand[surfType], -1, 0);
		}
	}

	public static void actKickObject(SPRITE pSprite, SPRITE pObject) {
		int moveDist = ClipLow((int) (2 * engine.qdist(sprXVel[pSprite.xvel],
				sprYVel[pSprite.xvel])), 0xAAAAA);

		sprXVel[pObject.xvel] = mulscale(Cos(pSprite.ang + BiRandom(85)),
				moveDist, 30);
		sprYVel[pObject.xvel] = mulscale(Sin(pSprite.ang + BiRandom(85)),
				moveDist, 30);
		sprZVel[pObject.xvel] = mulscale(-8192, moveDist, 14);

		pObject.hitag = (kAttrMove | kAttrGravity | kAttrFalling);
	}

	public static void ProcessTouchObjects(int nSprite, int nXSprite) {
		SPRITE pSprite = sprite[nSprite];
		XSPRITE pXSprite = xsprite[nXSprite];
		SPRITEHIT pSpriteHit = gSpriteHit[nXSprite];
		int nHitObject = pSpriteHit.ceilHit & kHitIndexMask;

		switch (pSpriteHit.ceilHit & kHitTypeMask) {
		case kHitSprite:
			if (sprite[nHitObject].extra > 0) {
				SPRITE pHit = sprite[nHitObject];
				XSPRITE pXHit = xsprite[pHit.extra];
				if (pHit.statnum == kStatThing || pHit.statnum == kStatDude) {
					if (sprXVel[pSprite.xvel] != 0 || sprYVel[pSprite.xvel] != 0 || sprZVel[pSprite.xvel] != 0) {
						if (pHit.statnum == kStatThing) {
							THINGINFO pThingInfo = thingInfo[pHit.lotag - kThingBase];
							if ((pThingInfo.flags & 1) != 0) pHit.hitag |= 1;
							if ((pThingInfo.flags & 2) != 0) pHit.hitag |= 4;

							sprXVel[pHit.xvel] += mulscale(4, pHit.x - pSprite.x, 2);
							sprYVel[pHit.xvel] += mulscale(4, pHit.y - pSprite.y, 2);

						} else {
							pHit.hitag |= 5;

							sprXVel[pHit.xvel] += mulscale(4, pHit.x - pSprite.x, 2);
							sprYVel[pHit.xvel] += mulscale(4, pHit.y - pSprite.y, 2);

                            if (pHit.lotag == kDudeTchernobog) {
								if (!IsPlayerSprite(pSprite) || !gPlayer[pSprite.lotag - kDudePlayer1].godMode) {
									actDamageSprite(pHit.xvel, pSprite, kDamageExplode, 4 * pXSprite.health);
								}
							}
						}
					}
				}

				switch (pHit.lotag) {
					case kTrapSawBlade:
						if (pXHit.state != 0) {
							pXHit.data1 = 1;
							pXHit.data2 = (short) ClipHigh(pXHit.data2 + 2 * kFrameTicks, kTimerRate * 5);
							actDamageSprite(nSprite, pSprite, kDamageBullet, 16);
						} else {
							actDamageSprite(nSprite, pSprite, kDamageBullet, 1);
						}
						break;
				}
			}
			break;

		case kHitWall:
		case kHitSector:
			break;
		}

		nHitObject = pSpriteHit.moveHit & kHitIndexMask;
		switch (pSpriteHit.moveHit & kHitTypeMask) {
		case kHitSprite:
			SPRITE pHitObject = sprite[nHitObject];
			if (pHitObject.extra > 0) {

                switch (pHitObject.lotag) {
				case kDudeBurning:
				case kDudeCultistBurning:
				case kDudeAxeZombieBurning:
				case kDudeBloatedButcherBurning:
				case kDudeTinyCalebburning:
				case kDudeTheBeastburning:
					pXSprite.burnTime = ClipLow(pXSprite.burnTime - kFrameTicks, 0);
					actDamageSprite(actGetBurnSource(pXSprite.burnSource),
							pSprite, kDamageBurn, 2 * kFrameTicks);
					break;
				case kThingPail:
				case kThingZombieHead:
					actKickObject(pSprite, pHitObject);
					if (pHitObject.lotag == kThingZombieHead) {
						sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, 357,
								pSprite.sectnum);
						actDamageSprite(-1, pHitObject, kDamageFall, 80);
					}
					break;
				}
			}
			break;

		case kHitWall:
		case kHitSector:
			break;
		}

		nHitObject = pSpriteHit.floorHit & kHitIndexMask;
		switch (pSpriteHit.floorHit & kHitTypeMask) {
		case kHitSprite:
			if (sprite[nHitObject].extra > 0) {
				SPRITE pHitObject = sprite[nHitObject];
				XSPRITE pXHit = xsprite[pHitObject.extra];
				if (IsDudeSprite(pHitObject)) {

                    if (pHitObject.lotag <= kDudePlayer8
							&& pHitObject.lotag != kDudeFleshStatue
							&& pHitObject.lotag != kDudeStoneStatue
							&& pHitObject.lotag != kDudeMotherSpider
							&& pHitObject.lotag != kDudeEel
							&& pHitObject.lotag != kDudeFanaticProne) {
						if (IsPlayerSprite(pSprite)) {
							actDamageSprite(nSprite, pHitObject, kDamageBullet, 8);
							return;
						}
					}
				}

				switch (pHitObject.lotag) {
				case kTrapSawBlade:
					if (pXHit.state != 0) {
						pXHit.data1 = 1;
						pXHit.data2 = (short) ClipHigh(pXHit.data2 + 2
								* kFrameTicks, kTimerRate * 5);
						actDamageSprite(nSprite, pSprite, kDamageBullet, 16);
					} else
						actDamageSprite(nSprite, pSprite, kDamageBullet, 1);
					break;

				case kThingPail:
				case kThingZombieHead:
					if (IsPlayerSprite(pSprite)) {
						PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];
						if (gFrameClock < pPlayer.kickTime)
							return;
						pPlayer.kickTime = gFrameClock + 60;
					}
					sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, 357,
							pSprite.sectnum);
					actKickObject(pSprite, pHitObject);
					if (pHitObject.lotag == kThingZombieHead)
						actDamageSprite(-1, pHitObject, kDamageFall, 80);
					else
						sfxStart3DSound(pSprite, 374, 0, 0);
					break;
				}
			}
			break;

		case kHitWall:
			break;
		case kHitFloor:
			processTouchFloor(pSprite, nHitObject);
			break;
		}

    }

	public static void processTouchFloor(SPRITE pSprite, int nSector) {
		if (pSprite == null)
			game.dassert("pSprite != null");
		if (!(nSector >= 0 && nSector < kMaxSectors))
			game.dassert("nSector >= 0 && nSector < kMaxSectors");

		SECTOR pSector = sector[nSector];
		XSECTOR pXSector = pSector.extra > 0 ? xsector[pSector.extra] : null;
		if (pXSector != null && (pSector.lotag == kSectorDamage || pXSector.damageType != 0)) {
			int nDamageType = pXSector.damageType;
			if (pSector.lotag == kSectorDamage) {
				nDamageType = ClipRange(nDamageType, 0, 6);
			} else
				nDamageType = ClipRange(nDamageType - 1, 0, 6);

			int nDamage = 1000;
			if (pXSector.data != 0)
				nDamage = ClipLow(pXSector.data, 0);

			actDamageSprite(pSprite.xvel, pSprite, nDamageType, (kFrameTicks * nDamage / kTimerRate) << 4);
		}

		int surfType = game.getCurrentDef().GetSurfType(nSector + kHitFloor);
		if (surfType == 14) {
			actDamageSprite(pSprite.xvel, pSprite, kDamageBurn, 16);
			sfxStart3DSound(pSprite, 352, 5, 2);
		}
	}

	/***********************************************************************
	 * actPostSprite()
	 *
	 * Postpones deletion or status list change for a sprite. An nStatus value
	 * of kStatFree passed to this function will postpone deletion of the sprite
	 * until the gPostpone list is next processed.
	 **********************************************************************/
	public static void actPostSprite(int nSprite, int nStatus) {
		if (!(gPostCount < kMaxSprites))
			game.dassert("gPostCount < kMaxSprites");
		if (!(nSprite < kMaxSprites && sprite[nSprite].statnum < kMaxStatus))
			game.dassert("nSprite < kMaxSprites && sprite[nSprite].statnum < kMaxStatus");
		if (!(nStatus >= 0 && nStatus <= kStatFree))
			game.dassert("nStatus >= 0 && nStatus <= kStatFree ");

		int n;
		if ((sprite[nSprite].hitag & kAttrFree) != 0) {
			// see if it is already in the list (we may want to semaphore with
			// an attr bit for speed)
			for (n = 0; n < gPostCount; n++)
				if (gPost[n].nSprite == nSprite)
					break;

			if (!(n < gPostCount))
				game.dassert("n < gPostCount");
		} else {
			n = gPostCount;
			sprite[nSprite].hitag |= kAttrFree;
			gPostCount++;
		}
		gPost[n].nSprite = (short) nSprite;
		gPost[n].nStatus = (short) nStatus;
	}

	/***********************************************************************
	 * actPostProcess()
	 *
	 * Processes postponed sprite events to ensure that sprite list processing
	 * functions normally when sprites are deleted or change status.
	 *
	 **********************************************************************/
	public static void actPostProcess() {
		if (gPostCount != 0) {
			for (int i = 0; i < gPostCount; i++) {
				POSTPONE pPost = gPost[i];
				int nSprite = pPost.nSprite;
				sprite[nSprite].hitag &= ~kAttrFree;
				if (pPost.nStatus == kStatFree) {
					checkEventList(nSprite, SS_SPRITE);
					if (sprite[nSprite].extra > 0)
						seqKill(SS_SPRITE, sprite[nSprite].extra);
					deletesprite(pPost.nSprite);
				} else
					changespritestat(pPost.nSprite, pPost.nStatus);
			}
			gPostCount = 0;
		}
	}

	public static void actProcessEffects() {
		for (int nSprite = headspritestat[kStatEffect]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			viewBackupSpriteLoc(nSprite, pSprite);
			short nSector = pSprite.sectnum;
			if (nSector < 0 || nSector >= kMaxSectors) {
				actDeleteEffect2(nSprite);
				continue;
			}

			if (pSprite.lotag >= kFXMax) {
				actDeleteEffect2(nSprite);
				continue;
			}

			EFFECT pFXData = gEffectInfo[pSprite.lotag];
			AirDrag(nSprite, pFXData.velocity);

			if (sprXVel[nSprite] != 0)
				pSprite.x += sprXVel[pSprite.xvel] >> 12;
			if (sprYVel[nSprite] != 0)
				pSprite.y += sprYVel[pSprite.xvel] >> 12;
			if (sprZVel[nSprite] != 0)
				pSprite.z += sprZVel[pSprite.xvel] >> 8;

			if (sprXVel[nSprite] == 0 && sprYVel[nSprite] == 0
					&& sprZVel[nSprite] == 0) {
				sprZVel[nSprite] += pFXData.gravity;
				continue;
			}

			boolean gravity = sprXVel[nSprite] == 0
					&& (sprYVel[nSprite] == 0 || pSprite.z < sector[nSector].floorz);
			if (!gravity) {
				nSector = engine.updatesector(pSprite.x, pSprite.y, nSector);
				if (nSector == -1) {
					actDeleteEffect2(nSprite);
					continue;
				}
			}

			if (gravity
					|| pSprite.z < engine.getflorzofslope(pSprite.sectnum,
							pSprite.x, pSprite.y)) {
				if (pSprite.sectnum != nSector) {
					if (!(nSector >= 0 && nSector < kMaxSectors))
						game.dassert("nSector >= 0 && nSector < kMaxSectors");
					changespritesect((short) nSprite, nSector);
				}
				engine.getzsofslope(nSector, pSprite.x, pSprite.y, zofslope);
				if (zofslope[CEIL] > pSprite.z
						&& (sector[nSector].ceilingstat & kSectorParallax) == 0) {
					actDeleteEffect2(nSprite);
					continue;
				}
				if (zofslope[FLOOR] >= pSprite.z) {
					sprZVel[nSprite] += pFXData.gravity;
					continue;
				}
			}

			int funcID = pFXData.funcID;
			if (funcID > -1 && funcID <= 22) {
				if (gCallback[pFXData.funcID] == null)
					game.dassert("gCallback[pFXData.funcID] != NULL");
				gCallback[pFXData.funcID].run(nSprite);
			} else
				actDeleteEffect2(nSprite);

			/*
			 * boolean LABEL13 = false; if ( sprXVel[nSprite] == 0 && (
			 * sprYVel[nSprite] == 0 || pSprite.z < sector[nSector].floorz ) ) {
			 * LABEL13 = true; } if(!LABEL13) { nSector =
			 * engine.updatesector(pSprite.x, pSprite.y, nSector); if ( nSector
			 * == -1 ) { actDeleteEffect2(nSprite); continue; } } if (LABEL13 ||
			 * engine.getflorzofslope(pSprite.sectnum, pSprite.x, pSprite.y) >
			 * pSprite.z) { if (!LABEL13 && pSprite.sectnum != nSector) {
			 * if(!(nSector >= 0 && nSector < kMaxSectors))
			 * game.dassert("nSector >= 0 && nSector < kMaxSectors");
			 * changespritesect((short) nSprite, nSector); }
			 *
			 * if ( sprXVel[nSprite] == 0 && sprYVel[nSprite] == 0 &&
			 * sprZVel[nSprite] == 0 ) { sprZVel[nSprite] += pFXData.gravity;
			 * continue; } engine.getzsofslope(nSector, pSprite.x, pSprite.y);
			 * if (zofslope[CEIL] > pSprite.z && (sector[nSector].ceilingstat &
			 * 1) == 0) { actDeleteEffect2(nSprite); continue; } if
			 * (zofslope[FLOOR] < pSprite.z) { int funcID = pFXData.funcID;
			 *
			 * if (funcID > -1 && funcID <= 22) { if(gCallback[pFXData.funcID]
			 * == null) game.dassert("gCallback[pFXData.funcID] != NULL");
			 * gCallback[pFXData.funcID].run(nSprite); } else {
			 * actDeleteEffect2(nSprite); continue; } } else sprZVel[nSprite] +=
			 * pFXData.gravity;
			 *
			 * continue; } int funcID = pFXData.funcID;
			 *
			 * if (funcID > -1 && funcID <= 22) { if(gCallback[pFXData.funcID]
			 * == null) game.dassert("gCallback[pFXData.funcID] != NULL");
			 * gCallback[pFXData.funcID].run(nSprite); } else
			 * actDeleteEffect2(nSprite);
			 */
		}
	}


	// by NoOne: this function removes non-existing or triggered sprites from proximity and sight lists
	// and updates their counter
	public static void rebuildGlobalArray(int arrayId) {
		short curCount = -1;  short newCount = 0; short[] array = new short[kMaxSightSprites];

		switch (arrayId) {
			case 1:
				curCount = gProxySpritesCount;
				array = gProxySpritesList;
				break;
			case 2:
				curCount = gSightSpritesCount;
				array = gSightSpritesList;
				break;
			default:
				return;
		}

		if (curCount > 0) {
			for (int i = 0; i < curCount; i++) {
				if (array[i] < 0 || sprite[array[i]].extra < 0 || sprite[array[i]].statnum == kStatFree)
					continue;

				XSPRITE pXSprite = xsprite[sprite[array[i]].extra];
				if (pXSprite.isTriggered || (arrayId == 1 && !pXSprite.Proximity) || (arrayId == 2 && !pXSprite.Sight))
					continue;
				// it's basically the same as isTriggered
				else if (pXSprite.rxID <= 0 && pXSprite.state != pXSprite.restState && pXSprite.waitTime <= 0 && !pXSprite.Interrutable)
					continue;

				array[newCount++] = array[i]; // place OK sprites to the top and refresh the counter
			}
		}

		switch (arrayId) {
			case 1:
				gProxySpritesList = array;
				gProxySpritesCount = newCount;
				gMaxBadProxySprites = (byte) (gProxySpritesCount >> 2);
				if (gMaxBadProxySprites <= 0) gMaxBadProxySprites = 1;
				return;
			case 2:
				gSightSpritesList = array;
				gSightSpritesCount = newCount;
				gMaxBadSightSprites = (byte) (gSightSpritesCount >> 2);
				if (gMaxBadSightSprites <= 0) gMaxBadSightSprites = 1;
				return;
		}
	}


	public static void actProcessSprites() {
		int nSprite;

		gTNTCount = 0;

        // process things for effects
		for (nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pThink = sprite[nSprite];
			if ((pThink.hitag & kAttrFree) != 0)
				continue;

			if(!IsOriginalDemo() && (pThink.lotag == kThingTNTRem || pThink.lotag == kThingTNTProx || pThink.lotag == kGDXThingTNTProx)) { //PriorityList overflow protect
				gTNTCount++;
				if(gTNTCount > kPQueueSize / 2) //fNodeCount < kPQueueSize protect
				{
					actPostSprite(nSprite, kStatFree);
					continue;
				}
			}

			int nXThink = pThink.extra;
			if (nXThink > 0) {
				int type = pThink.lotag;
				XSPRITE pXThink = xsprite[nXThink];
				if (type == 425 || type == 426 || type == kThingZombieHead) {
					if (pXThink.Locked != 0) {
						if (gFrameClock >= pXThink.targetX)
							pXThink.Locked = 0;
					}
				}
				if (actGetBurnTime(pXThink) > 0) {
					pXThink.burnTime = ClipLow(pXThink.burnTime - kFrameTicks, 0);
					actDamageSprite(actGetBurnSource(pXThink.burnSource),
							sprite[nSprite], kDamageBurn, 2 * kFrameTicks);
				}

									 // don't process sight flag for things which is locked or triggered

                if (pXThink.Proximity) {
					boolean isOriginal = IsOriginalDemo();
					// don't process locked or 1-shot things for proximity (if not demo)
					if(!isOriginal && (pXThink.Locked == 1 || pXThink.isTriggered))
						continue;

					if (pThink.lotag == kThingLifeLeech)
						pXThink.target = -1;

					int nAffected = headspritestat[kStatDude];
					int nextAffected = nextspritestat[nAffected];
					for (; nAffected >= 0; nAffected = nextAffected) {
						nextAffected = nextspritestat[nAffected];
						SPRITE pDude = sprite[nAffected];
						if ((pDude.hitag & kAttrFree) == 0 && xsprite[pDude.extra].health != 0) {

							// by NoOne: allow dudeLockout for proximity flag
							if (!isOriginal && (pThink.lotag != kThingLifeLeech && pXThink.DudeLockout && !IsPlayerSprite(pDude)))
								continue;

							int proxyDist = 96;
							if (pThink.lotag == kGDXThingCustomDudeLifeLeech)
								proxyDist = 512;
							else if ((pThink.lotag == kThingLifeLeech) && pXThink.target == -1) {
								int nOwner = actGetBurnSource(pThink.owner);
								if (nAffected == nOwner || nOwner == -1)
									continue;

								PLAYER pOwner = null;
								if (IsPlayerSprite(sprite[nOwner]))
									pOwner = gPlayer[sprite[nOwner].lotag - kDudePlayer1];
								PLAYER pTarget = null;
								if (IsPlayerSprite(pDude))
									pTarget = gPlayer[pDude.lotag - kDudePlayer1];

								if (pDude.lotag != kDudeEarthZombie
										&& pDude.lotag != kDudeRat
										&& pDude.lotag != kDudeBat
										&& (pGameInfo.nGameType != 3
										|| pTarget == null
										|| (pOwner != null && pTarget.teamID != pOwner.teamID))
										&& (pGameInfo.nGameType != 1 || pTarget == null))
											proxyDist = 512;
								else continue;
							}

							if (CheckProximity(pDude, pThink.x, pThink.y,pThink.z, pThink.sectnum, proxyDist)) {
								switch(pThink.lotag){
									case kGDXThingTNTProx:
										if (!isOriginal && !IsPlayerSprite(pDude)) continue;
										pThink.pal = 0;
										break;
									case kThingLifeLeech:
										if ((!Chance(0x2000) && nextspritestat[nAffected] >= 0)
											|| (pDude.cstat & CLIPMASK0) == 0)
											continue;
										pXThink.target = pDude.xvel;
										break;
									case kGDXThingCustomDudeLifeLeech:
										if (!isOriginal && pXThink.target != pDude.xvel) continue;
										break;
								}

								if (pThink.owner == -1) actSetOwner(pThink, pDude);
								trTriggerSprite(nSprite, pXThink, kCommandSpriteProximity);
							}
						}
					}
				}
			}
		}

		// process things for movement
		for (nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pThink = sprite[nSprite];

			if ((pThink.hitag & kAttrFree) != 0)
				continue;

			int nXSprite = pThink.extra;
			if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
				game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
			int nSector = pThink.sectnum;
			int nXSector = sector[nSector].extra;
			XSECTOR pXSector = null;
			if (nXSector > 0) {
				if (!(nXSector > 0 && nXSector < kMaxXSectors))
					game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
				if (!(xsector[nXSector].reference == nSector))
					game.dassert("xsector[nXSector].reference == nSector");
				pXSector = xsector[nXSector];
			}

			if (pXSector != null
					&& pXSector.panVel != 0
					&& (pXSector.panAlways || pXSector.busy != 0 || pXSector.state != 0)) {
				THINGINFO pThinkInfo = thingInfo[pThink.lotag - kThingBase];
				if ((pThinkInfo.flags & 1) != 0)
					pThink.hitag |= kAttrMove;
				if ((pThinkInfo.flags & 2) != 0)
					pThink.hitag |= kAttrFalling;
			}

			if ((pThink.hitag & (kAttrMove | kAttrGravity)) != 0) {
				viewBackupSpriteLoc(nSprite, pThink);
				if (pXSector != null && pXSector.panVel != 0) {
					GetSpriteExtents(pThink);

					if (engine.getflorzofslope(pThink.sectnum, pThink.x,
							pThink.y) <= extents_zBot) {
						int panVel = 0;
						int panAngle = pXSector.panAngle;
						if (pXSector.panAlways || pXSector.state != 0
								|| pXSector.busy != 0) {
							panVel = (pXSector.panVel & 0xFF) << 9;
							if (!pXSector.panAlways && pXSector.busy != 0)
								panVel = mulscale(panVel, pXSector.busy, 16);
						}

						if ((sector[nSector].floorstat & kSectorRelAlign) != 0) {
							panAngle += GetWallAngle(sector[nSector].wallptr) + kAngle90;
							panAngle &= kAngleMask;
						}

						int pushX = mulscale(panVel, Cos(panAngle), 30);
						int pushY = mulscale(panVel, Sin(panAngle), 30);
						sprXVel[nSprite] += pushX;
						sprYVel[nSprite] += pushY;
					}
				}

				AirDrag(nSprite, 128);
				if ((gFrame & 0xF) == (pThink.xvel >> 8)
						&& (pThink.hitag & kAttrGravity) != 0)
					pThink.hitag |= kAttrFalling;

				if ((pThink.hitag & kAttrFalling) == 0 && sprXVel[nSprite] == 0
						&& sprYVel[nSprite] == 0 && sprZVel[nSprite] == 0
						&& floorVel[pThink.sectnum] == 0
						&& ceilingVel[pThink.sectnum] == 0)
					continue;
				MoveThing(nSprite);
			}
		}

		// process missile sprites
		NEXTMISSILE: for (nSprite = headspritestat[kStatMissile]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			if ((pSprite.hitag & kAttrFree) != 0)
				continue;

			viewBackupSpriteLoc(nSprite, pSprite);

			int nXSprite = pSprite.extra;
			XSPRITE pXSprite = xsprite[nXSprite];
			short oldcstat = 0;
			SPRITE pOwner = null;

			if (pSprite.owner >= 0) {
				pOwner = sprite[actGetOwner(pSprite)];
				if (IsDudeSprite(pOwner)) {
					oldcstat = pOwner.cstat;
					pOwner.cstat &= 0xFEFE;
				} else
					pOwner = null;
			}

			pHitInfo.hitsect = -1;
			pHitInfo.hitwall = -1;
			pHitInfo.hitsprite = -1;
			if (pSprite.lotag == kMissileSprayFlame)
				AirDrag(nSprite, 4096);

			if (pXSprite.target != -1) {
				if (sprXVel[pSprite.xvel] != 0 || sprYVel[pSprite.xvel] != 0
						|| sprZVel[pSprite.xvel] != 0) {
					int nTarget = pXSprite.target;
					SPRITE pTarget = sprite[nTarget];
					int nXTarget = pTarget.extra;
					XSPRITE pXTarget = nXTarget <= 0 ? null : xsprite[nXTarget];

					if (pTarget.statnum == kStatDude && pXTarget != null
							&& pXTarget.health != 0) {
						int nAngle = engine.getangle(-(pTarget.y - pSprite.y),
								pTarget.x - pSprite.x);
						int x = gMissileData[pSprite.lotag - kMissileBase].velocity;
						int y = 0;

						Point out = RotatePoint(x, y, (nAngle + 1536) & kAngleMask, 0, 0);
						sprXVel[pSprite.xvel] = out.getX();
						sprYVel[pSprite.xvel] = out.getY();

						int dz = pTarget.z - pSprite.z;
						int zvel = dz / 10;
						if (pSprite.z > pTarget.z)
							zvel = dz / -10;
						sprZVel[pSprite.xvel] += zvel;
					}
				}
			}

			int hitInfo = -1;
			int nImpact = 1;

			int x, y, z;
			short nSector = pSprite.sectnum;
			int xvel = (int) (sprXVel[pSprite.xvel] >> 12);
			int yvel = (int) (sprYVel[pSprite.xvel] >> 12);
			int zvel = (int) (sprZVel[pSprite.xvel] >> 8);

			while (true) {
				x = pSprite.x;
				y = pSprite.y;
				z = pSprite.z;
				nSector = pSprite.sectnum;

				GetSpriteExtents(pSprite);

				clipmoveboxtracenum = 1;

				int moveHit = 0;
				if(!IsOriginalDemo()) {
					moveHit = engine.clipmove(x, y, z, nSector, xvel << 14, yvel << 14,
							pSprite.clipdist << 2, (pSprite.z - extents_zTop) / 4,
							(extents_zBot - pSprite.z) / 4, CLIPMASK0);

					 x = clipmove_x;
					 y = clipmove_y;
					 z = clipmove_z;
					 nSector = clipmove_sectnum;
				} else {
					moveHit = ClipMove(x, y, z, nSector, xvel, yvel,
							pSprite.clipdist << 2, (pSprite.z - extents_zTop) / 4,
							(extents_zBot - pSprite.z) / 4, CLIPMASK0);

					x = clipm_px;
					y = clipm_py;
					z = clipm_pz;
					nSector = (short) clipm_pnsectnum;
				}

				clipmoveboxtracenum = 3;

				if (nSector == -1) {
					hitInfo = -1;
					if (pOwner != null)
						pOwner.cstat = oldcstat;
					 if (!IsOriginalDemo() && (hitInfo = HitScan(pSprite, z, xvel, yvel, zvel, pHitInfo, CLIPMASK0, 0)) != -1) {
						 if (pOwner != null)
							 pOwner.cstat = oldcstat;
						 actImpactMissile(pSprite, hitInfo);
					 }

					continue NEXTMISSILE;
				}

				if (moveHit != 0) {
					int nHitType = moveHit & kHitTypeMask;
					if (nHitType == kHitSprite) {
						pHitInfo.hitsprite = (short) (moveHit & kHitIndexMask);
						hitInfo = SS_SPRITE;
					}
					if (nHitType == kHitWall) {
						pHitInfo.hitwall = (short) (moveHit & kHitIndexMask);
						if (wall[pHitInfo.hitwall].nextsector == -1) {
							hitInfo = SS_WALL;
						} else {
							engine.getzsofslope(
									wall[pHitInfo.hitwall].nextsector,
									pSprite.x, pSprite.y, zofslope);
							if (pSprite.z > zofslope[CEIL] && pSprite.z < zofslope[FLOOR])
								hitInfo = SS_MASKED;
							else
								hitInfo = SS_WALL;
						}
					}
				}

				if (hitInfo != SS_MASKED)
					break;

				int nXWall = wall[pHitInfo.hitwall].extra;
				if (nXWall <= 0)
					break;
				XWALL pXWall = xwall[nXWall];
				if (!pXWall.triggerVector)
					break;
				trTriggerWall(pHitInfo.hitwall, pXWall, kCommandWallImpact);
				if ((wall[pHitInfo.hitwall].cstat & kWallHitscan) != 0)
					break;

				hitInfo = -1;
				if (nImpact-- <= 0) {
					hitInfo = SS_WALL;
					if (pOwner != null)
						pOwner.cstat = oldcstat;
					actImpactMissile(pSprite, hitInfo);
					continue NEXTMISSILE;
				}
			}

			if (hitInfo != -1 && hitInfo != SS_SPRITE) {
				int nAngle = engine.getangle((int) sprXVel[pSprite.xvel],
						(int) sprYVel[pSprite.xvel]);
				x -= mulscale(Cos(nAngle), 16, 30);
				y -= mulscale(Sin(nAngle), 16, 30);
				int moveDist = (int) engine.qdist(sprXVel[pSprite.xvel],
						sprYVel[pSprite.xvel]);
				zvel -= (sprZVel[pSprite.xvel] << 8) / moveDist;
				nSector = engine.updatesector(x, y, nSector);
			}
			int ceilz, ceilhit, floorz, floorhit;
			GetZRange(x, y, z, nSector, pSprite.clipdist << 2, CLIPMASK0);
			ceilz = gz_ceilZ;
			ceilhit = gz_ceilHit;
			floorz = gz_floorZ;
			floorhit = gz_floorHit;
			GetSpriteExtents(pSprite);
			extents_zTop += zvel;
			extents_zBot += zvel;
			if (extents_zBot >= floorz) {
				gSpriteHit[pSprite.extra].floorHit = floorhit;
				hitInfo = 2;
				zvel += (floorz - extents_zBot);
			}
			if (extents_zTop <= ceilz) {
				gSpriteHit[pSprite.extra].ceilHit = ceilhit;
				hitInfo = 1;
				if (ceilz - extents_zTop > 0)
					zvel += ceilz - extents_zTop;
			}
			pSprite.x = x;
			pSprite.y = y;
			pSprite.z = zvel + z;

			int uSector = engine.updatesector(x, y, nSector);

			if (uSector >= 0 && nSector != pSprite.sectnum) {
				if (!(uSector < kMaxSectors))
					game.dassert("nSector >= 0 && nSector < kMaxSectors");
				changespritesect(pSprite.xvel, (short) uSector);
			}
			checkWarping(pSprite);
			pHitInfo.hitsect = pSprite.sectnum;
			pHitInfo.hitx = pSprite.x;
			pHitInfo.hity = pSprite.y;
			pHitInfo.hitz = pSprite.z;

			if (pOwner != null)
				pOwner.cstat = oldcstat;

			if (hitInfo != -1)
				actImpactMissile(pSprite, hitInfo);
		}

		// process explosions
		for (nSprite = headspritestat[kStatExplosion]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			if ((pSprite.hitag & kAttrFree) != 0)
				continue;

			int nOwner = actGetOwner(pSprite);

			int nType = pSprite.lotag;
			if (!(nType >= 0 && nType < 8))
				game.dassert("nType >= 0 && nType < kExplodeMax: " + nType);

			int nXSprite = pSprite.extra;
			if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
				game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites: " + nXSprite); //evict.map crash ?

			XSPRITE pXSprite = xsprite[nXSprite];
			EXPLODE pExpl = gExplodeData[nType];
			int x = pSprite.x;
			int y = pSprite.y;
			int z = pSprite.z;

			int nSector = pSprite.sectnum;
			int radius = pXSprite.data4;
			if (pXSprite.data4 <= 0)
				radius = pExpl.radius;

			gSectorExp[0] = -1;
			gWallExp[0] = -1;
			NearSectors(nSector, x, y, radius, gSectorExp, gSpriteExp, gWallExp);

			int nWall = -1;
			for (int n = 0; n < kMaxXWalls; trTriggerWall(gWallExp[n],
					xwall[wall[nWall].extra], kCommandWallImpact), n++) {
				nWall = gWallExp[n];
				if (nWall == -1)
					break;
			}

			for (int nAffected = headspritestat[kStatDude]; nAffected >= 0; nAffected = nextspritestat[nAffected]) {
				SPRITE pDude = sprite[nAffected];

				if ((pDude.hitag & kAttrFree) != 0)
					continue;

				if ((gSpriteExp[pDude.sectnum >> 3] & (1 << (pDude.sectnum & 7))) != 0
						&& pXSprite.data1 != 0
						&& CheckProximity(pDude, x, y, z, nSector, radius)) {
					if (pExpl.used1 != 0 && pXSprite.target == 0) {
						pXSprite.target |= 1;
						actDamageSprite(nOwner, pDude, kDamageFall,
								(Random(pExpl.used2) + pExpl.used1) << 4);
					}

					if (pExpl.damageType != 0)
						ConcussSprite(nOwner, pDude, x, y, z, pExpl.damageType);

					if (pExpl.burnCount != 0) {
						if (!(pDude.extra > 0 && pDude.extra < kMaxXSprites))
							game.dassert("pDude.extra > 0 && pDude.extra < kMaxXSprites");
						XSPRITE pXDude = xsprite[pDude.extra];

						if (pXDude.burnTime == 0)
							evPostCallback(nAffected, SS_SPRITE, 0, 0);

						pXDude.burnSource = pSprite.owner;
						pXDude.burnTime = ClipHigh(pXDude.burnTime + 4
								* pExpl.burnCount, 2400);
					}
				}
			}

			for (int nAffected = headspritestat[kStatThing]; nAffected >= 0; nAffected = nextspritestat[nAffected]) {
				SPRITE pThing = sprite[nAffected];

				if ((pThing.hitag & kAttrFree) != 0)
					continue;

				if ((gSpriteExp[pThing.sectnum >> 3] & (1 << (pThing.sectnum & 7))) != 0
						&& pXSprite.data1 != 0
						&& xsprite[pThing.extra].Locked == 0
						&& CheckProximity(pThing, x, y, z, nSector, radius)) {

					if (pExpl.damageType != 0)
						ConcussSprite(nOwner, pThing, x, y, z, pExpl.damageType);

					if (pExpl.burnCount != 0) {
						if (!(pThing.extra > 0 && pThing.extra < kMaxXSprites))
							game.dassert("pThing.extra > 0 && pThing.extra < kMaxXSprites");
						XSPRITE pXThing = xsprite[pThing.extra];

						if (pThing.lotag == kThingTNTBarrel
								&& pXThing.burnTime == 0)
							evPostCallback(nAffected, SS_SPRITE, 0, 0);

						pXThing.burnSource = pSprite.owner;
						pXThing.burnTime = ClipHigh(pXThing.burnTime + 4
								* pExpl.burnCount, 1200);
					}
				}
			}

			for (int nAffected = connecthead; nAffected >= 0; nAffected = connectpoint2[nAffected]) {
				SPRITE pPlayer = gPlayer[nAffected].pSprite;
				int dx = (pSprite.x - pPlayer.x) >> 4;
				int dy = (pSprite.y - pPlayer.y) >> 4;
				int dz = (pSprite.z - pPlayer.z) >> 8;
				int dist2 = dx * dx + dy * dy + dz * dz
						+ (1 << kGlobalForceShift);

				int force = pXSprite.data2 << 16;

				gPlayer[nAffected].explosion += force / dist2;
			}

			 // if data4 == 1, do not remove explosion. This can be useful
			 // when designer wants put explosion generator in map manually
			 // via sprite statnum 2.

			if (pSprite.hitag != 0x0001) {
				pXSprite.data1 = (short) ClipLow(pXSprite.data1 - kFrameTicks, 0);
				pXSprite.data2 = (short) ClipLow(pXSprite.data2 - kFrameTicks, 0);
				pXSprite.data3 = (short) ClipLow(pXSprite.data3 - kFrameTicks, 0);
			}

			if (pXSprite.data1 == 0 && pXSprite.data2 == 0 && pXSprite.data3 == 0 && seqFrame(SS_SPRITE, nXSprite) < 0)
				actPostSprite(nSprite, kStatFree);
		}

		// process traps for effects
		for (nSprite = headspritestat[kStatTraps]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pTrap = sprite[nSprite];

			if ((pTrap.hitag & kAttrFree) != 0)
				continue;

			int nXSprite = pTrap.extra;
			XSPRITE pXSprite = null;
			if (nXSprite > 0) {
				if (nXSprite >= kMaxXSprites)
					game.dassert("nXSprite < kMaxXSprites");
				pXSprite = xsprite[nXSprite];

				switch (pTrap.lotag) {
				case 452:
					if (pXSprite.state != 0
							&& seqFrame(SS_SPRITE, nXSprite) < 0) {
						int x = pTrap.x;
						int y = pTrap.y;
						int z = pTrap.z;

						int dx = mulscale(Cos(pTrap.ang),
								(pXSprite.data1 << 23) / 120, 30);
						int dy = mulscale(Sin(pTrap.ang),
								(pXSprite.data1 << 23) / 120, 30);

						for (int i = 0; i < 2; i++) {
							SPRITE pSpawn = actSpawnEffect(32, pTrap.sectnum,
									x, y, z, 0);
							if (pSpawn != null) {
								sprXVel[pSpawn.xvel] = dx + BiRandom(34952);
								sprYVel[pSpawn.xvel] = dy + BiRandom(34952);
								sprZVel[pSpawn.xvel] = BiRandom(34952);
							}
							x += dx / 2 >> 12;
							y += dy / 2 >> 12;
						}
						gVectorData[20].maxDist = pXSprite.data1 << 9;
						actFireVector(pTrap, 0, 0, Cos(pTrap.ang) >> 16,
								Sin(pTrap.ang) >> 16, BiRandom(34952), 20);
					}
					break;
				case 454:
					pXSprite.data2 = (short) ClipLow(pXSprite.data2
							- kFrameTicks, 0);
					break;
				}
			}
		}

		// process dudes for effects
		for (nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			int nXSprite = pSprite.extra;

			if (!IsDudeSprite(pSprite))
				continue;

			if ((pSprite.hitag & kAttrFree) == 0 && nXSprite > 0) {
				XSPRITE pXSprite = xsprite[nXSprite];
				if (actGetBurnTime(pXSprite) > 0) {
					if ((pSprite.lotag < kDudeBurning || pSprite.lotag > kDudeBloatedButcherBurning)
							&& pSprite.lotag != kDudeTinyCalebburning
							&& pSprite.lotag != kDudeTheBeastburning) {
						pXSprite.burnTime = ClipLow(pXSprite.burnTime - kFrameTicks, 0);
					}

					actDamageSprite(actGetBurnSource(pXSprite.burnSource),
							sprite[nSprite], kDamageBurn, 2 * kFrameTicks);
				}

				// handle incarnations of custom dude
				if (pSprite.lotag == kGDXDudeUniversalCultist && pXSprite.health <= 0 && seqFrame(SS_SPRITE,nXSprite) < 0) {
					XSPRITE pXIncarnation = getNextIncarnation(pXSprite);
					if (pXIncarnation != null) {
						SPRITE pIncarnation = sprite[pXIncarnation.reference];

						pSprite.lotag = pIncarnation.lotag;
						pSprite.pal = pIncarnation.pal;
						pSprite.shade = pIncarnation.shade;
						pSprite.clipdist = pIncarnation.clipdist;
						pSprite.xrepeat = pIncarnation.xrepeat;
						pSprite.yrepeat = pIncarnation.yrepeat;

						pXSprite.txID = pXIncarnation.txID;
						pXSprite.command = pXIncarnation.command;
						pXSprite.triggerOn = pXIncarnation.triggerOn;
						pXSprite.triggerOff = pXIncarnation.triggerOff;

						pXSprite.burnTime = 0;
						pXSprite.burnSource = -1;

						pXSprite.data1 = pXIncarnation.data1;
						pXSprite.data2 = pXIncarnation.data2;
						pXSprite.data3 = pXIncarnation.data3;
						pXSprite.data4 = pXIncarnation.data4;

						pXSprite.dudeGuard = pXIncarnation.dudeGuard;
						pXSprite.dudeDeaf = pXIncarnation.dudeDeaf;
						pXSprite.dudeAmbush = pXIncarnation.dudeAmbush;
						pXSprite.dudeFlag4 = pXIncarnation.dudeFlag4;

						pXSprite.busyTime = pXIncarnation.busyTime;

						switch(pSprite.lotag){
							case kGDXDudeUniversalCultist:
							case kGDXGenDudeBurning:
								seqSpawn(getSeqStartId(pXSprite), SS_SPRITE,nXSprite, null);
								break;
							default:
								seqSpawn(dudeInfo[pSprite.lotag - kDudeBase].seqStartID, SS_SPRITE,nXSprite, null);
								break;
						}

						if (pXSprite.data4 <= 0) pXSprite.health = (dudeInfo[pSprite.lotag - kDudeBase].startHealth << 4) & 0xfff;
						else {
		                    long hp = (pXSprite.data4 << 4) & 0xfff;
		                    pXSprite.health = (int) ((hp > 0) ? ((hp <= 65535) ? hp : 65535) : 1);
						}

						aiInit(pSprite,false);
						if (pXSprite.target == -1) aiSetTarget(pXSprite, 0, 0, 0);
						else aiSetTarget(pXSprite, pXSprite.target);
						aiActivateDude(pSprite, pXSprite);
					}
				}

				if (pSprite.lotag == kDudeCerberus && pXSprite.health == 0 && seqFrame(3, nXSprite) < 0) {
					pXSprite.health = dudeInfo[kDudeCerberus2 - kDudeBase].startHealth << 4;
					pSprite.lotag = kDudeCerberus2;
					if (pXSprite.target == -1) aiSetTarget(pXSprite, 0, 0, 0);
					else aiSetTarget(pXSprite, pXSprite.target);
					aiActivateDude(pSprite, pXSprite);
				}

				if (pXSprite.Proximity && !pXSprite.isTriggered) {
					for (int nrSprite = headspritestat[kStatDude]; nrSprite >= 0; nrSprite = nextspritestat[nrSprite]) {
						SPRITE prSprite = sprite[nrSprite];
						if ((prSprite.hitag & kAttrFree) == 0
								&& prSprite.extra > 0
								&& xsprite[prSprite.extra].health != 0
								&& IsPlayerSprite(prSprite)
								&& CheckProximity(prSprite, pSprite.x,
										pSprite.y, pSprite.z, pSprite.sectnum,
										128)) {
							trTriggerSprite(nSprite, pXSprite,
									kCommandSpriteProximity);
						}
					}
				}

				if (IsPlayerSprite(pSprite)) {
					int nPlayer = pSprite.lotag - kDudePlayer1;
					PLAYER pPlayer = gPlayer[nPlayer];

					/*
					 * Voodoo v1.00 if (pPlayer.voodooCount != 0) { int aimz =
					 * (int) pPlayer.aim.z; int dz = pPlayer.weaponAboveZ -
					 * pPlayer.pSprite.z; if(newHoriz) dz += 10 * pPlayer.horiz;
					 * if (UseAmmo(pPlayer, 9, 0) >= 8) { for (int i = 0; i < 4;
					 * i++) { int nAngle = ((pPlayer.voodooUnk +
					 * pPlayer.voodooAng) & 0x7FF) - 512;
					 * actFireVector(pPlayer.pSprite, 0, dz, costable[nAngle +
					 * 512] >> 16, costable[nAngle & 0x7FF] >> 16, aimz, 21);
					 * int v6 = ((pPlayer.voodooAng + 2048 - pPlayer.voodooUnk)
					 * & 0x7FF) - 512; actFireVector(pPlayer.pSprite, 0, dz,
					 * costable[v6 + 512] >> 16, costable[v6 & 0x7FF] >> 16,
					 * aimz, 21); pPlayer.voodooUnk += 5; } pPlayer.voodooCount
					 * = ClipLow( pPlayer.voodooCount - 1, 0); } else {
					 * pPlayer.voodooCount = 0; } }
					 */

					if (pPlayer.handDamage && Chance(0x4000))
						actDamageSprite(pSprite.xvel, pSprite, kDamageDrown, 12);
					if (pPlayer.Underwater) {
						boolean haveDivingSuit;
						if ((haveDivingSuit = inventoryCheck(pPlayer,
								kInventoryDivingSuit)) || pPlayer.godMode)
							pPlayer.airTime = 1200;
						else
							pPlayer.airTime = ClipLow(pPlayer.airTime
									- kFrameTicks, 0);

						if (pPlayer.airTime < 1080
								&& getInventoryAmount(pPlayer,
										kInventoryDivingSuit) != 0
								&& !haveDivingSuit)
							processInventory(pPlayer, kInventoryDivingSuit);
						if (pPlayer.airTime != 0) {
							pPlayer.drownEffect = 0;
						} else {
							if (pPlayer.pXsprite.health > 0)
								pPlayer.drownEffect += kFrameTicks;
							if (bRandom() < (pPlayer.drownEffect >> 1)) {
								actDamageSprite(pSprite.xvel, pSprite,
										kDamageDrown, 48);
							}
						}
						if (sprXVel[pSprite.xvel] != 0
								|| sprYVel[pSprite.xvel] != 0)
							sfxStart3DSound(pSprite, 709, 100, 2);// PL_UW_SW
						pPlayer.bubbleTime = ClipLow(pPlayer.bubbleTime
								- kFrameTicks, 0);
					} else if (pGameInfo.nGameType == 0
							&& pPlayer.pXsprite != null
							&& pPlayer.pXsprite.health != 0
							&& pPlayer.stayTime >= 1200 && Chance(256)) {
						pPlayer.stayTime = -1;
						sfxStart3DSound(pSprite, Random(11) + 3100, 0, 2);
					}
				}
				ProcessTouchObjects(nSprite, nXSprite);
			}
		}

		// process dudes for movement
		for (nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			int zBot;
			SPRITE pSprite = sprite[nSprite];
			int nSector = pSprite.sectnum;

			if ((pSprite.hitag & kAttrFree) != 0)
				continue;

			if (!IsDudeSprite(pSprite))
				continue;

			if(!IsOriginalDemo() && pGameInfo.nGameType == kNetModeOff && numplayers < 2)
			{
				if(gNoEnemies == 2) {
					pSprite.cstat |= kSpriteInvisible;
					pSprite.cstat &= ~(kSpriteBlocking | kSpriteHitscan | kSpritePushable);
				}
			}

			int nXSprite = pSprite.extra;
			if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
				game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");

			viewBackupSpriteLoc(nSprite, pSprite);

			int vel = 128;
			// special sector processing
			if (sector[nSector].extra > 0) {
				int nXSector = sector[nSector].extra;
				if (!(nXSector > 0 && nXSector < kMaxXSectors))
					game.dassert("nXSector > 0 && nXSector < kMaxXSectors");
				if (xsector[nXSector].reference != nSector)
					game.dassert("xsector[nXSector].reference == nSector");

				XSECTOR pXSector = xsector[nXSector];
				if (pXSector != null) {
					GetSpriteExtents(pSprite);
					zBot = extents_zBot;// zTop = extents_zTop;

					if (engine.getflorzofslope((short) nSector, pSprite.x,
							pSprite.y) <= zBot) {
						int panVel = 0;
						int panAngle = pXSector.panAngle;
						if (pXSector.panAlways || pXSector.state != 0
								|| pXSector.busy != 0) {
							panVel = (pXSector.panVel & 0xFF) << 9;
							if (!pXSector.panAlways && pXSector.busy != 0)
								panVel = mulscale(panVel, pXSector.busy, 16);
						}

						if ((sector[nSector].floorstat & kSectorRelAlign) != 0) {
							panAngle = (GetWallAngle(sector[nSector].wallptr)
									+ panAngle + kAngle90)
									& kAngleMask;
						}

						int pushX = mulscale(panVel, Cos(panAngle), 30);
						int pushY = mulscale(panVel, Sin(panAngle), 30);
						sprXVel[nSprite] += pushX;
						sprYVel[nSprite] += pushY;
					}

					if (pXSector != null && pXSector.Underwater)
						vel = 5376;
				}
			}

			AirDrag(nSprite, vel);
			if ((pSprite.hitag & kAttrFalling) == 0 && sprXVel[nSprite] == 0
					&& sprYVel[nSprite] == 0 && sprZVel[nSprite] == 0
					&& floorVel[pSprite.sectnum] == 0
					&& ceilingVel[pSprite.sectnum] == 0)
				continue;

			MoveDude(nSprite);
		}

		// process flares to keep them burning on dudes
		for (nSprite = headspritestat[kStatFlare]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {

			SPRITE pFlare = sprite[nSprite];
			if ((pFlare.hitag & kAttrFree) != 0)
				continue;

			int nXSprite = pFlare.extra;
			if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
				game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
			XSPRITE pXSprite = xsprite[nXSprite];
			int nTarget = pXSprite.target;
			if (nTarget < 0)
				game.dassert("nTarget >= 0");
			viewBackupSpriteLoc(nSprite, pFlare);
			if (nTarget >= kMaxSprites)
				game.dassert("nTarget < kMaxSprites");
			SPRITE pTarget = sprite[nTarget];
			if (pTarget.statnum == kStatFree) {
				actGenerateGibs(pFlare, 17, null, null);
				actPostSprite(pFlare.xvel, kStatFree);
			}
			if (pTarget.extra > 0 && xsprite[pTarget.extra].health != 0) {
				int nAngle = pTarget.ang + pXSprite.goalAng;
				int x = pTarget.x + mulscaler(Cos(nAngle), pTarget.clipdist << 1, 30); // halfway into clipdist
				int y = pTarget.y + mulscaler(Sin(nAngle), pTarget.clipdist << 1, 30);
				int z = pTarget.z + pXSprite.targetZ;

				setSprite(nSprite, x, y, z);
				sprXVel[nSprite] = sprXVel[nTarget];
				sprYVel[nSprite] = sprYVel[nTarget];
				sprZVel[nSprite] = sprZVel[nTarget];
			} else {
				actGenerateGibs(pFlare, 17, null, null);
				actPostSprite(pFlare.xvel, kStatFree);
			}
		}

		if(gNoEnemies == 0)
			aiProcessDudes();
		actProcessEffects();
	}

	public static void actDistanceDamage(int nSprite, int x, int y, int z,
			int sectnum, int Distance, int minHit, int damageType,
			int damageHit, int flags, int BurnTime) {
		gSectorExp[0] = -1;
		gWallExp[0] = -1;
		NearSectors(sectnum, x, y, Distance, gSectorExp, gSpriteExp, gWallExp);

		int mDist = Distance << 4;
		if ((flags & 2) != 0) {
			for (int i = headspritestat[kStatDude]; i >= 0; i = nextspritestat[i]) {
				if (i != nSprite || (flags & 1) != 0) {
					SPRITE pTarget = sprite[i];
					int nXTarget = pTarget.extra;

					if (nXTarget > 0
							&& nXTarget < 2048
							&& (pTarget.hitag & kAttrFree) == 0
							&& (gSpriteExp[pTarget.sectnum >> 3] & (1 << (pTarget.sectnum & 7))) != 0
							&& CheckProximity(pTarget, x, y, z, sectnum, mDist)) {
						int dx = klabs(x - pTarget.x);
						int dy = klabs(y - pTarget.y);
						int dz = klabs(z - pTarget.z) >> 4;
						int dist = engine.ksqrt(dx * dx + dy * dy + dz * dz);

						if (dist <= mDist) {
							int damage = (dist != 0) ? (damageHit * (mDist - dist) / mDist + minHit) : damageHit + minHit;
							actDamageSprite(nSprite, pTarget, damageType, damage << 4);
							if (BurnTime != 0) {
								XSPRITE pXTarget = xsprite[nXTarget];

								pXTarget.burnTime = ClipHigh(pXTarget.burnTime + BurnTime, 2400);
								pXTarget.burnSource = actSetBurnSource(nSprite);
							}
						}
					}
				}
			}
		}

		if ((flags & 4) != 0) {
			for (int i = headspritestat[kStatThing]; i >= 0; i = nextspritestat[i]) {
				SPRITE pTarget = sprite[i];

				int nXTarget = pTarget.extra;
				if (nXTarget > 0
						&& nXTarget < 2048
						&& (pTarget.hitag & kAttrFree) == 0
						&& (gSpriteExp[pTarget.sectnum >> 3] & (1 << (pTarget.sectnum & 7))) != 0
						&& CheckProximity(pTarget, x, y, z, sectnum, mDist)) {
					XSPRITE pXTarget = xsprite[nXTarget];
					if (pXTarget.Locked == 0) {
						int dx = klabs(x - pTarget.x);
						int dy = klabs(y - pTarget.y);
						int dist = engine.ksqrt(dx * dx + dy * dy);

						if (dist <= mDist) {
							int damage = (dist != 0) ? minHit + damageHit
									* (mDist - dist) / mDist : damageHit
									+ minHit;
							actDamageSprite(nSprite, pTarget, damageType,
									damage << 4);
							if (BurnTime != 0) {
								pXTarget.burnTime = ClipHigh(pXTarget.burnTime
										+ BurnTime, 1200);
								pXTarget.burnSource = actSetBurnSource(nSprite);
							}
						}
					}
				}
			}
		}
	}

	public static int actCheckSpawnType(int nType) {
		if (nType > 3 && nType < 13 || nType > 36)
			return nType;
		return -1;
	}

	public static void actSpawnBlood(SPRITE pSprite) {
		int nSector = pSprite.sectnum;
		if (nSector >= 0 && nSector <= numsectors) {
			boolean find = FindSector(pSprite.x, pSprite.y, pSprite.z,
					(short) nSector);
			nSector = foundSector;
			if (find) {
				if (!cfg.gParentalLock || pGameInfo.nGameType > 0) {
					SPRITE pSpawn = actSpawnEffect(27, pSprite.sectnum,
							pSprite.x, pSprite.y, pSprite.z, 0);
					if (pSpawn != null) {
						pSpawn.ang = 1024;
						sprXVel[pSpawn.xvel] = BiRandom(436906);
						sprYVel[pSpawn.xvel] = BiRandom(436906);
						sprZVel[pSpawn.xvel] = -Random(1092266) - 100;
						evPostCallback(pSpawn.xvel, 3, 8, 6);
					}
				}
			}
		}
	}

	public static void actSpawnTentacleBlood(SPRITE pSprite) {
		int nSector = pSprite.sectnum;
		if (nSector >= 0 && nSector <= numsectors) {
			boolean find = FindSector(pSprite.x, pSprite.y, pSprite.z,
					(short) nSector);
			nSector = foundSector;
			if (find) {
				if (!cfg.gParentalLock || pGameInfo.nGameType > 0) {
					int nType = 54;
					if (pSprite.lotag == kDudeGreenPod)
						nType = 53;

					SPRITE pSpawn = actSpawnEffect(nType, pSprite.sectnum,
							pSprite.x, pSprite.y, pSprite.z, 0);
					if (pSpawn != null) {
						pSpawn.ang = 1024;
						sprXVel[pSpawn.xvel] = BiRandom(436906);
						sprYVel[pSpawn.xvel] = BiRandom(436906);
						sprZVel[pSpawn.xvel] = -Random(1092266) - 100;
						evPostCallback(pSpawn.xvel, 3, 8, 18);
					}
				}
			}
		}
	}

	public static void actSpawnSSheels(SPRITE pSprite, int z, int offset,
			int vel) {
		int x = mulscale(Cos(pSprite.ang + kAngle90), offset, 30)
				+ mulscale(Sin(pSprite.ang + kAngle90),
						(pSprite.clipdist - 4) << 2, 30) + pSprite.x;
		int y = mulscale(Cos(pSprite.ang), offset, 30)
				+ mulscale(Sin(pSprite.ang), (pSprite.clipdist - 4) << 2, 30)
				+ pSprite.y;
		SPRITE pSpawn = actSpawnEffect(40 + Random(3), pSprite.sectnum, x, y,
				z, 0);
		if (pSpawn != null) {
			int velocity = (vel << 18) / 120 + BiRandom((vel / 4 << 18) / 120);
			int nAngle = pSprite.ang + BiRandom(56) + kAngle90;
			sprXVel[pSpawn.xvel] = mulscale(Cos(nAngle), velocity, 30);
			sprYVel[pSpawn.xvel] = mulscale(Sin(nAngle), velocity, 30);
			sprZVel[pSpawn.xvel] = sprZVel[pSprite.xvel] - 0x20000
					- (BiRandom(20) << 18) / 120;
		}
	}

	public static void actSpawnTSheels(SPRITE pSprite, int z, int offset,
			int vel) {
		int x = mulscale(Cos(pSprite.ang + kAngle90), offset, 30)
				+ mulscale(Sin(pSprite.ang + kAngle90),
						(pSprite.clipdist - 4) << 2, 30) + pSprite.x;
		int y = mulscale(Cos(pSprite.ang), offset, 30)
				+ mulscale(Sin(pSprite.ang), (pSprite.clipdist - 4) << 2, 30)
				+ pSprite.y;

		SPRITE pSpawn = actSpawnEffect(37 + Random(3), pSprite.sectnum, x, y,
				z, 0);

		if (pSpawn != null) {
			int velocity = (vel << 18) / 120 + BiRandom((vel / 4 << 18) / 120);
			int nAngle = pSprite.ang + BiRandom(56) + kAngle90;
			sprXVel[pSpawn.xvel] = mulscale(Cos(nAngle), velocity, 30);
			sprYVel[pSpawn.xvel] = mulscale(Sin(nAngle), velocity, 30);
			sprZVel[pSpawn.xvel] = sprZVel[pSprite.xvel] - 0x20000
					- (BiRandom(40) << 18) / 120;
		}
	}

	public static void actDeleteEffect(int nSprite) {
		if (nSprite >= 0 && nSprite < kMaxSprites) {
			checkEventList(nSprite, SS_SPRITE);
			if (sprite[nSprite].extra > 0) {
				seqKill(SS_SPRITE, sprite[nSprite].extra);
			}
			deletesprite((short) nSprite);
		}
	}

	public static void actDeleteEffect2(int nSprite) {
		if (nSprite >= 0 && nSprite < kMaxSprites) {
			int nXSprite = sprite[nSprite].extra;
			if (nXSprite > 0)
				seqKill(SS_SPRITE, nXSprite);
			if (sprite[nSprite].statnum != kStatFree)
				actPostSprite(nSprite, kStatFree);
		}
	}

	public static SPRITE actSpawnEffect(int nType, short nSector, int x, int y,
			int z, int nBusy) {
		if (nSector < 0 || nSector > numsectors)
			return null;
		if (!FindSector(x, y, z, nSector))
			return null;

		if(!IsOriginalDemo() && eventQ.getSize() >= 768) //priorityQueue protect
			return null;

		if (cfg.gParentalLock && pGameInfo.nGameType <= 0) {
			if (actCheckSpawnType(nType) == -1)
				return null;
		}
		if ((nType & 128) == 0 && nType < kFXMax) {
			EFFECT nEffect = gEffectInfo[nType];

			if (nStatSize[kStatEffect] == 512) {
				int nSprite;
				for (nSprite = headspritestat[kStatEffect];; nSprite = nextspritestat[nSprite]) {
					if ((sprite[nSprite].hitag & kAttrFree) == 0 || nSprite == -1)
						break;
				}
				if (nSprite == -1)
					return null;

				actDeleteEffect(nSprite);
			}
			int nSpawn = actSpawnSprite(nSector, x, y, z, 1, false);
			SPRITE pSpawn = sprite[nSpawn];

			pSpawn.lotag = (short) nType;
			pSpawn.picnum = (short) nEffect.picnum;
			pSpawn.cstat |= nEffect.cstat;
			pSpawn.shade = (byte) nEffect.shade;
			pSpawn.pal = (byte) nEffect.pal;
			pSpawn.detail = (byte) nEffect.detail;

			if (nEffect.xrepeat > 0)
				pSpawn.xrepeat = (short) nEffect.xrepeat;
			if (nEffect.yrepeat > 0)
				pSpawn.yrepeat = (short) nEffect.yrepeat;

			if ((nEffect.flags & 1) != 0 && Chance(0x4000))
				pSpawn.cstat ^= kSpriteFlipX;
			if ((nEffect.flags & 2) != 0 && Chance(0x4000))
				pSpawn.cstat ^= kSpriteFlipY;

			if (nEffect.seqId != 0)
				seqSpawn(nEffect.seqId, SS_SPRITE,
						dbInsertXSprite(pSpawn.xvel), null);

			if (nBusy == 0)
				nBusy = nEffect.count;

			if (nBusy != 0)
				evPostCallback(pSpawn.xvel, SS_SPRITE, BiRandom(nBusy >> 1)
						+ nBusy, 1);

			return pSpawn;
		}
		return null;
	}

	public static int GetDataVal(SPRITE pSprite,int data){
		if (pSprite.extra < 0) return -1;
		XSPRITE pXSprite = xsprite[pSprite.extra];
		int[] rData; rData = new int[4];

		rData[0] = pXSprite.data1; rData[2] = pXSprite.data3;
		rData[1] = pXSprite.data2; rData[3] = pXSprite.data4;

		return rData[data];
	}


	public static int GetRandDataVal(int[] rData,SPRITE pSprite){

		if (rData != null && pSprite != null) return -1;
		else if (pSprite != null) {

			if (pSprite.extra < 0)
				return -1;

			rData = new int[4]; XSPRITE pXSprite = xsprite[pSprite.extra];
			rData[0] = pXSprite.data1; rData[2] = pXSprite.data3;
			rData[1] = pXSprite.data2; rData[3] = pXSprite.data4;

		} else if (rData == null) {
			return -1;
		}

		int random = 0;
		// randomize only in case if at least 2 data fields are not empty
		int a = 1; int b = -1;
		for (int i = 0; i <= 3; i++){
			if (rData[i] == 0){
				if (a++ > 2)
					return -1;
			} else if (b == -1) {
				b++;
			}
		}

		// try randomize few times
		int maxRetries = 10;
		while(maxRetries > 0){
			if (pGameInfo.nGameType == kNetModeOff && numplayers < 2) random = (int) (4*Math.random());
			else random = Gameutils.Random(3);

			if (rData[random] > 0) return rData[random];
			maxRetries--;
		}

		// if nothing, get first found data value from top
		return rData[b];
	}

	public static SPRITE DropRandomPickupObject(SPRITE pSprite, short prevItem){
		SPRITE pSprite2 = null;

		int[] rData; rData = new int[4]; int selected = -1;
		rData[0] = xsprite[pSprite.extra].data1; rData[2] = xsprite[pSprite.extra].data3;
		rData[1] = xsprite[pSprite.extra].data2; rData[3] = xsprite[pSprite.extra].data4;

		// randomize only in case if at least 2 data fields fits.
		for (int i = 0; i <= 3; i++)
			if (rData[i] < kWeaponItemBase || rData[i] >= kItemMax)
				rData[i] = 0;

		int maxRetries = 9;
		while((selected = GetRandDataVal(rData,null)) == prevItem) if (maxRetries-- <= 0) break;
		if (selected > 0) {
			SPRITE pSource = pSprite; XSPRITE pXSource = xsprite[pSource.extra];
			pSprite2 = DropPickupObject(pSprite,selected);
			if (pSprite2 != null) {

				pSprite2.x = pSource.x;
				pSprite2.y = pSource.y;
				pSprite2.z = pSource.z;
				pXSource.dropMsg = pSprite2.lotag; // store dropped item lotag in dropMsg

				// sometimes voxels does not load.
				tileLoadVoxel(pSprite2.picnum);

				if ((pSource.hitag & 0x0001) != 0 && dbInsertXSprite(pSprite2.xvel) > 0) {
					XSPRITE pXSprite2 = xsprite[pSprite2.extra];

					// inherit spawn sprite trigger settings, so designer can send command when item picked up.
					pXSprite2.txID = pXSource.txID;
					pXSprite2.command = pXSource.command;
					pXSprite2.triggerOn = pXSource.triggerOn;
					pXSprite2.triggerOff = pXSource.triggerOff;

					pXSprite2.Pickup = true;
				}
			}

		}

		return pSprite2;
	}

	public static SPRITE spawnRandomDude(SPRITE pSprite){

		SPRITE pSprite2 = null;

		if (pSprite.extra >= 0) {
			int[] rData; rData = new int[4]; int selected = -1;
			rData[0] = xsprite[pSprite.extra].data1; rData[2] = xsprite[pSprite.extra].data3;
			rData[1] = xsprite[pSprite.extra].data2; rData[3] = xsprite[pSprite.extra].data4;

			// randomize only in case if at least 2 data fields fits.
			for (int i = 0; i <= 3; i++)
				if (rData[i] < kDudeBase || rData[i] >= kDudeMax)
					rData[i] = 0;

			if ((selected = GetRandDataVal(rData,null)) > 0)
				pSprite2 = actSpawnDude(pSprite,selected,-1);
		}

		return pSprite2;
	}

	public static void actInitStruct()
	{
		for (int i = 0; i < kMaxXSprites; i++) {
			if(gSpriteHit[i] == null)
				gSpriteHit[i] = new SPRITEHIT();
			else gSpriteHit[i].clear();
		}

		for (int i = 0; i < kMaxSprites; i++) {
			if(gPost[i] == null)
				gPost[i] = new POSTPONE();
			else gPost[i].clear();
		}
	}


	public static void actInit(boolean loadgame, boolean isOriginal) {
		actInitStruct();

		for (int nSprite = headspritestat[kStatItem]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			if (pSprite.lotag == 44) // Voodoo doll
				pSprite.lotag = 70;

			switch (pSprite.lotag) {
				case kItemKey1:
				case kItemKey2:
				case kItemKey3:
				case kItemKey4:
				case kItemKey5:
				case kItemKey6:
				case kItemKey7:
					if(pSprite.extra > 0)
						xsprite[pSprite.extra].respawn = 0; //keys should respawning always
					break;
			}

		}

		for (int nSprite = headspritestat[kStatTraps]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			int nXSprite = pSprite.extra;
			XSPRITE pXSprite = null;
			if (nXSprite > 0 && nXSprite < kMaxXSprites)
				pXSprite = xsprite[nXSprite];

			if (pSprite.lotag == kThingFlameTrap) {
				if (pSprite.picnum == 2183 && pXSprite != null
						&& pXSprite.state == 0) { // firing picnum and not actived
					seqSpawn(getSeq(kMGunClose), SS_SPRITE, pSprite.extra, null);
				}
			}

			if (pSprite.lotag == kThingHiddenExploder) {
				pXSprite.state = 0;

				if (pXSprite.waitTime < 1)
					pXSprite.waitTime = 1;

				pSprite.cstat &= ~kSpriteBlocking;
				pSprite.cstat |= kSpriteInvisible;
			}
		}

		// initialize all things
		for (int nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			SPRITE pSprite = sprite[nSprite];
			int nXSprite = pSprite.extra;
			if (!(nXSprite > 0 && nXSprite < kMaxXSprites)) {
				game.GameMessage("nXSprite > 0 && nXSprite < kMaxXSprites");
				return;
			}
			XSPRITE pXSprite = xsprite[nXSprite];
			THINGINFO pThinkInfo = thingInfo[pSprite.lotag - kThingBase];

			pXSprite.health = pThinkInfo.startHealth << 4;
			pSprite.clipdist = pThinkInfo.clipdist;
			pSprite.hitag = (short) pThinkInfo.flags;
			if ((pSprite.hitag & kAttrGravity) != 0)
				pSprite.hitag |= kAttrFalling;

			sprXVel[nSprite] = 0;
			sprYVel[nSprite] = 0;
			sprZVel[nSprite] = 0;

			switch (pSprite.lotag) {
			case kThingTNTProx:
			case kGDXThingTNTProx:
			case kThingMachineGun:
				pXSprite.state = 0;
				break;
			default:
				if (pSprite.lotag != kThingGib)
					pXSprite.state = 1;
				break;
			}

			SeqInst pInst = GetInstance(SS_SPRITE, nXSprite);
			if (pInst != null && pInst.isPlaying()) {
				if ((BuildGdx.cache.contains(pInst.getSeqIndex(), "SEQ")))
					seqSpawn(pInst.getSeqIndex(), SS_SPRITE, nXSprite, null);
			}
		}

		if (pGameInfo.nMonsterSettings != 0) {
			levelCalcKills();
			for (int i = 0; i < dudeInfo.length; i++)
				for (int j = 0; j < 7; j++)
					dudeInfo[i].damageShift[j] = mulscale(
							dudeInfo[i].startDamage[j],
							pSkillShift[pGameInfo.nEnemyDamage], 8);

			for (int nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
				SPRITE pSprite = sprite[nSprite];
				int nXSprite = pSprite.extra;

				if (!(nXSprite > 0 && nXSprite < kMaxXSprites)) {
					game.GameMessage("nXSprite > 0 && nXSprite < kMaxXSprites");
					return;
				}

				XSPRITE pXSprite = xsprite[nXSprite];
				int dudeIndex = pSprite.lotag - kDudeBase;
				if (!IsPlayerSprite(pSprite)) {
					if(!isOriginal) {
						if ((pSprite.cstat & 48) != 0) // GDX flat sprites fix
							pSprite.cstat &= ~(pSprite.cstat & 48);
						pSprite.xoffset = pSprite.yoffset = 0;
					}

					switch (pSprite.lotag) {
						case kDudeMotherPod: // Fake Dude type
							break;
						case kGDXDudeUniversalCultist:
						case kGDXGenDudeBurning:
							pSprite.cstat |= kSpriteBlocking | kSpriteHitscan | kSpritePushable;
							break;
						default:
							pSprite.cstat |= kSpriteBlocking | kSpriteHitscan | kSpritePushable;
							pSprite.clipdist = dudeInfo[dudeIndex].clipdist;
							break;
					}

					sprXVel[nSprite] = 0;
					sprYVel[nSprite] = 0;
					sprZVel[nSprite] = 0;

					if (!loadgame) {
						// By NoOne: add a way to set custom hp for every enemy.
						if (pXSprite.data4 <= 0 || IsOriginalDemo()) pXSprite.health = (dudeInfo[dudeIndex].startHealth << 4) & 0xfff;
						else {
		                    long hp = (pXSprite.data4 << 4) & 0xfff;
		                    pXSprite.health = (int) ((hp > 0) ? ((hp <= 65535) ? hp : 65535) : 1);
						}
					}

				}

				// by NoOne: Custom Dude stores it's SEQ in data2
				switch(pSprite.lotag) {
					case kGDXDudeUniversalCultist:
					case kGDXGenDudeBurning:
						seqSpawn(getSeqStartId(pXSprite),SS_SPRITE, nXSprite, null);
						break;
					default:
						int seqStartId = dudeInfo[dudeIndex].seqStartID + kSeqDudeIdle;
						if (BuildGdx.cache.contains(seqStartId, seq)) seqSpawn(seqStartId,SS_SPRITE, nXSprite, null);
						break;
				}
			}

			aiInit(isOriginal);

		} else {
			while ( headspritestat[kStatDude] >= 0 ) {
				SPRITE pSprite = sprite[headspritestat[kStatDude]];
				if (xsprite[pSprite.extra].key > 0)
					DropPickupObject(pSprite, kItemKey1 + xsprite[pSprite.extra].key - 1);
				deletesprite(headspritestat[kStatDude]);
			}
		}
	}

	public static int actGetRespawnTime( SPRITE pSprite )
	{
		if ( pSprite.extra > 0 )
		{
			XSPRITE pXSprite = xsprite[pSprite.extra];

			if(IsDudeSprite(pSprite)) {
				if ( !IsPlayerSprite(pSprite) && (pXSprite.respawn == 2 || (pXSprite.respawn != 1 && pGameInfo.nMonsterSettings == 2)) )
					return pGameInfo.nMonsterRespawnTime;
			}

			if ( IsWeaponSprite(pSprite) )
			{
				if ( pXSprite.respawn == 3 || pGameInfo.nWeaponSettings == 1 )
					return 0;
			    if ( pXSprite.respawn != 1 && pGameInfo.nWeaponSettings != 0 )
			    	return pGameInfo.nWeaponRespawnTime;
			}

			if( IsAmmoSprite(pSprite) )
			{
				if ( pXSprite.respawn == 2 || pXSprite.respawn != 1 && pGameInfo.nWeaponSettings != 0 )
					return pGameInfo.nWeaponRespawnTime;
			}

			if( IsItemSprite(pSprite) )
			{
				if ( pXSprite.respawn == 3 && pGameInfo.nGameType == 1 )
					return 0;

				if ( pXSprite.respawn == 2 || (pXSprite.respawn != 1 && pGameInfo.nItemSettings != 0 ))
				{
					switch ( pSprite.lotag )
					{
						case kItemLtdInvisibility:
					    case kItemGunsAkimbo:
					    case kItemReflectiveShots:
					    	return pGameInfo.nSpecialRespawnTime;
					    case kItemInvulnerability:
					    	return 2 * pGameInfo.nSpecialRespawnTime;
					    case kItemKey1:
					    case kItemKey2:
					    case kItemKey3:
					    case kItemKey4:
					    case kItemKey5:
					    case kItemKey6:
					    case kItemKey7:
					    	return 200;
					    default:
					    	return pGameInfo.nItemRespawnTime;
					}
				}
			}
		}

		return -1;
	}

	public static boolean actCheckRespawn(SPRITE pSprite) {
		int nSprite = pSprite.xvel;
		if (pSprite.extra > 0) {
			XSPRITE pXSprite = xsprite[pSprite.extra];

			int respawnTime = actGetRespawnTime(pSprite);
			if ( respawnTime < 0 )
				return false;

			pXSprite.respawnPending = 1;

			// don't go through respawn dot stages for things
			if ( pSprite.lotag >= kThingBase && pSprite.lotag < kThingMax )
			{
				pXSprite.respawnPending = 3;
				if ( pSprite.lotag == kThingTNTBarrel )
					pSprite.cstat |= kSpriteInvisible;
			}

			if  ( respawnTime > 0)
			{
				if ( pXSprite.respawnPending == 1 )
					respawnTime = mulscale(respawnTime, 0xA000, 16);

				pSprite.owner = pSprite.statnum; // store the sprite's status list for respawning
				actPostSprite(nSprite, kStatRespawn);
				pSprite.hitag |= kAttrRespawn;
				if(!IsDudeSprite(pSprite)) {
					pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;
					pSprite.x = (int) ksprite[nSprite].x;
					pSprite.y = (int) ksprite[nSprite].y;
					pSprite.z = (int) ksprite[nSprite].z;
				}
				evPostCallback(nSprite, SS_SPRITE, respawnTime, kCallbackRespawn);
			}
			return true;
		}
		return false; // indicate sprite will not respawn, and should be deleted, exploded, etc.
	}

	public static void actGibObject(SPRITE pSprite, XSPRITE pXSprite) {
		int gib1 = ClipRange(pXSprite.data1, 0, kGibMax);
		int gib2 = ClipRange(pXSprite.data2, 0, kGibMax);
		int gib3 = ClipRange(pXSprite.data3, 0, kGibMax);
		int sndId = pXSprite.data4;
		int drop = pXSprite.dropMsg;

		if (gib1 > 0)
			actGenerateGibs(pSprite, gib1 - 1, null, null);
		if (gib2 > 0)
			actGenerateGibs(pSprite, gib2 - 1, null, null);
		if (gib3 > 0 && pXSprite.burnTime != 0)
			actGenerateGibs(pSprite, gib3 - 1, null, null);
		if (sndId > 0)
			sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, sndId,
					pSprite.sectnum);
		if (drop > 0)
			DropPickupObject(pSprite, drop);

		if ((pSprite.cstat & kSpriteInvisible) == 0)
			if ((pSprite.hitag & kAttrRespawn) == 0)
				actPostSprite(pSprite.xvel, kStatFree);
	}

	public static SPRITE actCloneSprite(SPRITE pSourceSprite, int nStatus) {
		int nSprite = insertsprite(pSourceSprite.sectnum, (short) nStatus);
		if (nSprite < 0) {
			// dprintf("Out of sprites -- reclaiming sprite from purge list\n");
			nSprite = headspritestat[kStatPurge];
			if (nSprite < 0)
				game.dassert("nSprite >= 0");
			changespritesect((short) nSprite, pSourceSprite.sectnum);
			actPostSprite((short) nSprite, nStatus);
		}
		SPRITE pSprite = sprite[nSprite];
		pSprite.x = pSourceSprite.x;
		pSprite.y = pSourceSprite.y;
		pSprite.z = pSourceSprite.z;
		sprXVel[nSprite] = sprXVel[pSourceSprite.xvel];
		sprYVel[nSprite] = sprYVel[pSourceSprite.xvel];
		sprZVel[nSprite] = sprZVel[pSourceSprite.xvel];
		pSprite.hitag = 0;
		int nXSprite = dbInsertXSprite(nSprite);
		if(nXSprite <= 0) //Out of free Xsprites
			return null;

		gSpriteHit[nXSprite].floorHit = 0;
		gSpriteHit[nXSprite].ceilHit = 0;
		return pSprite;
	}

	public static SPRITE actSpawnDude(SPRITE pSprite, int nType, int nDist) {
		SPRITE pSource = pSprite; XSPRITE pXSource = xsprite[pSource.extra];
		SPRITE pDude = actCloneSprite(pSprite, kStatDude);

		XSPRITE pXDude = xsprite[pDude.extra];
		int x, y, z = pSprite.z, nAngle = pSprite.ang;
		if (nDist > 0) {
			x = pSprite.x + mulscaler(nDist, Cos(nAngle), 30);
			y = pSprite.y + mulscaler(nDist, Sin(nAngle), 30);
		} else {
			x = pSprite.x;
			y = pSprite.y;
		}

		pDude.lotag = (short) nType;
		pDude.ang = (short) nAngle;
		setSprite(pDude.xvel, x, y, z);
		pDude.cstat |= 0x1101;
		pDude.clipdist = dudeInfo[nType - kDudeBase].clipdist;
		pXDude.health = dudeInfo[nType - kDudeBase].startHealth << 4;
		pXDude.respawn = 1; //GDX 18.12.2019 CommonLoon102's fix: disable respawn for spawned dudes

		if ((BuildGdx.cache.contains(dudeInfo[nType - kDudeBase].seqStartID + kSeqDudeIdle, seq)))
			seqSpawn(dudeInfo[nType - kDudeBase].seqStartID + kSeqDudeIdle,
					SS_SPRITE, pDude.extra, null);

		if ((pSource.hitag & 0x0001) != 0 && pSource.lotag == kDudeSpawn) {
			//inherit pal?
			if (pDude.pal <= 0) pDude.pal = pSource.pal;

			// inherit spawn sprite trigger settings, so designer can count monsters.
			pXDude.txID = pXSource.txID;
			pXDude.command = pXSource.command;
			pXDude.triggerOn = pXSource.triggerOn;
			pXDude.triggerOff = pXSource.triggerOff;

			// inherit drop items
			pXDude.dropMsg = pXSource.dropMsg;

			// inherit dude flags
			pXDude.dudeDeaf = pXSource.dudeDeaf;
			pXDude.dudeGuard = pXSource.dudeGuard;
			pXDude.dudeAmbush = pXSource.dudeAmbush;
			pXDude.dudeFlag4 = pXSource.dudeFlag4;

			// the enemy can be available via rx command send.
        }

		aiInit(pDude, IsOriginalDemo());
		return pDude;
	}

	public static SPRITE actSpawnCustomDude(SPRITE pSprite, int nDist) {

		SPRITE pSource = pSprite; XSPRITE pXSource = xsprite[pSource.extra];
		SPRITE pDude = actCloneSprite(pSprite, kStatDude);
		XSPRITE pXDude = xsprite[pDude.extra];

		int x, y, z = pSprite.z, nAngle = pSprite.ang, nType = kGDXDudeUniversalCultist;

		if (nDist > 0) {
			x = pSprite.x + mulscaler(nDist, Cos(nAngle), 30);
			y = pSprite.y + mulscaler(nDist, Sin(nAngle), 30);
		} else {
			x = pSprite.x;
			y = pSprite.y;
		}

		pDude.lotag = (short) nType; pDude.ang = (short) nAngle;
		setSprite(pDude.xvel, x, y, z); pDude.cstat |= 0x1101;
		pDude.clipdist = dudeInfo[nType - kDudeBase].clipdist;

		// inherit weapon, seq and sound settings.
		pXDude.data1 = pXSource.data1;
		pXDude.data2 = pXSource.data2;
		pXDude.data3 = pXSource.data3;

		// spawn seq
		seqSpawn(getSeqStartId(pXDude),SS_SPRITE, pDude.extra, null);

		// inherit custom hp settings
		if (pXSource.data4 <= 0) pXDude.health = (dudeInfo[nType - kDudeBase].startHealth << 4) & 0xfff;
		else {
            long hp = (pXSource.data4 << 4) & 0xfff;
            pXDude.health = (int) ((hp > 0) ? ((hp <= 65535) ? hp : 65535) : 1);
		}

		// inherit movement speed.
		pXDude.busyTime = pXSource.busyTime;


		if ((pSource.hitag & 0x0001) != 0) {
			//inherit pal?
			if (pDude.pal <= 0) pDude.pal = pSource.pal;

			// inherit clipdist?
			if (pSource.clipdist > 0) pDude.clipdist = pSource.clipdist;

			// inherit spawn sprite trigger settings, so designer can count monsters.
			pXDude.txID = pXSource.txID;
			pXDude.command = pXSource.command;
			pXDude.triggerOn = pXSource.triggerOn;
			pXDude.triggerOff = pXSource.triggerOff;

			// inherit drop items
			pXDude.dropMsg = pXSource.dropMsg;

			// inherit required key so it can be dropped
			pXDude.key = pXSource.key;

			// inherit dude flags
			pXDude.dudeDeaf = pXSource.dudeDeaf;
			pXDude.dudeGuard = pXSource.dudeGuard;
			pXDude.dudeAmbush = pXSource.dudeAmbush;
			pXDude.dudeFlag4 = pXSource.dudeFlag4;
		}

		aiInit(pDude, false);
		return pDude;
	}

	public static SPRITE actSpawnThing(int nSector, int x, int y, int z,
			int nThingType) {
		if (!(nThingType >= kThingBase && nThingType < kThingMax))
			game.dassert("nThingType >= kThingBase && nThingType < kThingMax");

		int nThing = actSpawnSprite((short) nSector, x, y, z, kStatThing, true);
		SPRITE pThing = sprite[nThing];
		pThing.lotag = (short) nThingType;
		int nXThing = pThing.extra;
		if (!(nXThing > 0 && nXThing < kMaxXSprites))
			game.dassert("nXThing > 0 && nXThing < kMaxXSprites");
		XSPRITE pXThing = xsprite[nXThing];

		THINGINFO pThinkInfo = thingInfo[nThingType - kThingBase];

		pXThing.health = pThinkInfo.startHealth << 4;
		pThing.cstat |= pThinkInfo.cstat;
		pThing.clipdist = pThinkInfo.clipdist;
		pThing.hitag = (short) pThinkInfo.flags;
		if ((pThing.hitag & kAttrGravity) != 0)
			pThing.hitag |= kAttrFalling;

		pThing.picnum = (short) pThinkInfo.picnum;
		pThing.shade = (byte) pThinkInfo.shade;
		pThing.pal = (byte) pThinkInfo.pal;

		if (pThinkInfo.xrepeat != 0)
			pThing.xrepeat = (short) pThinkInfo.xrepeat;
		if (pThinkInfo.yrepeat != 0)
			pThing.yrepeat = (short) pThinkInfo.yrepeat;

		show2dsprite[pThing.xvel >> 3] |= 1 << (pThing.xvel & 7);
		switch (nThingType) {
		case 432:
			pXThing.data1 = 0;
			pXThing.data3 = 0;
			pXThing.data4 = 0;
			pXThing.state = 1;
			pXThing.isTriggered = false;
			pXThing.triggerOnce = true;
			break;
		case kThingLifeLeech:
		case kGDXThingCustomDudeLifeLeech:
			pXThing.data1 = 0;
			pXThing.data3 = 0;
			pXThing.data4 = 0;
			pXThing.state = 1;
			pXThing.isTriggered = false;
			pXThing.triggerOnce = false;
			break;
            case kThingZombieHead:
			pXThing.data1 = 8;
			pXThing.data4 = 318;
			pXThing.targetX |= gFrameClock + 180;
			pXThing.state = 1;
			pXThing.Locked = 1;
			pXThing.isTriggered = false;
			pXThing.triggerOnce = false;
			break;
		case kThingGibSmall:
		case kThingGib:
			if (nThingType == kThingGibSmall)
				pXThing.data1 = 19;
			else if (nThingType == kThingGib)
				pXThing.data1 = 8;
			pXThing.data2 = 0;
			pXThing.data3 = 0;
			pXThing.data4 = 319;

			pXThing.targetX |= gFrameClock + 180;
			pXThing.state = 1;
			pXThing.Locked = 1;
			pXThing.isTriggered = false;
			pXThing.triggerOnce = false;

			break;
		case kThingTNTStick:
		case kThingTNTBundle:
			evPostCallback(pThing.xvel, SS_SPRITE, 0, kCommandToggleLock);
			sfxStart3DSound(pThing, 450, 0, 0);
			break;
		case kThingSprayBundle:
			evPostCallback(pThing.xvel, SS_SPRITE, 0, kCommandToggleLock);
			break;
		}
		return pThing;
	}

	/***********************************************************************
	 * actSpawnSprite()
	 *
	 * Spawns a new sprite at the specified world coordinates.
	 **********************************************************************/
	public static int actSpawnSprite(short nSector, long x, long y, long z,
			int nStatus, boolean bAddXSprite) {
		if(!isValidSector(nSector)) return -1;

		int nSprite = insertsprite(nSector, (short) nStatus);
		if (nSprite >= 0)
			sprite[nSprite].extra = -1;
		else {
			nSprite = headspritestat[kStatPurge];
			if (nSprite < 0)
				game.dassert("nSprite >= 0");
			if (!(nSector >= 0 && nSector < kMaxSectors))
				game.dassert("nSector >= 0 && nSector < kMaxSectors");
			changespritesect((short) nSprite, nSector);
			actPostSprite(nSprite, nStatus);
		}

		setSprite(nSprite, x, y, z);

		SPRITE pSprite = sprite[nSprite];

		pSprite.lotag = 0;

		// optionally create an xsprite
		if (bAddXSprite && pSprite.extra == -1) {
			int nXSprite = dbInsertXSprite(nSprite);
			if(nXSprite == -1) return -1;
			gSpriteHit[nXSprite].floorHit = 0;
			gSpriteHit[nXSprite].ceilHit = 0;
		}

		return nSprite;
	}

	public static SPRITE DropPickupObject(SPRITE pActor, int nObject) {
		if (pActor.statnum >= kMaxStatus)
			return null;
		if (!((nObject >= kItemBase && nObject < kItemMax)
				|| (nObject >= kAmmoItemBase && nObject < kAmmoItemMax)
				|| (nObject >= kWeaponItemBase && nObject < kWeaponItemMax)))
			return null;

		short nSector = engine.updatesector(pActor.x, pActor.y, pActor.sectnum);
		if(!IsOriginalDemo() && !isValidSector(nSector))
		{
			if(isValidSector(pActor.sectnum))
				nSector = pActor.sectnum;
			else return null;
		}

		int floorz = engine.getflorzofslope(nSector, pActor.x, pActor.y);
		int nSprite = actSpawnSprite(nSector, pActor.x, pActor.y, floorz, kStatItem, false);
		if (nSprite != -1) {
			SPRITE pSprite = sprite[nSprite];
			pSprite.cstat &= ~kSpriteBlocking & ~kSpriteHitscan;

			if (nObject >= kItemBase && nObject < kItemMax) {
				int nItemIndex = nObject - kItemBase;
				pSprite.lotag = (short) nObject;
				pSprite.picnum = (short) gItemInfo[nItemIndex].picnum;
				pSprite.shade = (byte) gItemInfo[nItemIndex].shade;
				pSprite.xrepeat = (short) gItemInfo[nItemIndex].xrepeat;
				pSprite.yrepeat = (short) gItemInfo[nItemIndex].yrepeat;
				if (nObject >= kItemKey1 && nObject <= kItemKey7) {
					// PF/NN: should this be in bloodbath too?
					if (pGameInfo.nGameType == kNetModeCoop) // force permanent keys in Coop mode
					{
						if (pSprite.extra == -1)
							dbInsertXSprite(nSprite);
						if (pSprite.extra != -1) {
							XSPRITE pXSprite = xsprite[pSprite.extra];
							pXSprite.respawn = kRespawnPermanent;
							gSpriteHit[pSprite.extra].floorHit = 0;
							gSpriteHit[pSprite.extra].ceilHit = 0;
						}
					}
				}
				if (nObject == kItemBlueFlag || nObject == kItemRedFlag) {
					if (pGameInfo.nGameType == kNetModeTeams)
						evPostCallback(pSprite.xvel, SS_SPRITE, 1800, 17);
				}
			} else if (nObject >= kAmmoItemBase && nObject < kAmmoItemMax) {
				int nAmmoIndex = nObject - kAmmoItemBase;

				pSprite.lotag = (short) nObject;
				pSprite.picnum = (short) gAmmoItemData[nAmmoIndex].picnum;
				pSprite.shade = (byte) gAmmoItemData[nAmmoIndex].shade;
				pSprite.xrepeat = (short) gAmmoItemData[nAmmoIndex].xrepeat;
				pSprite.yrepeat = (short) gAmmoItemData[nAmmoIndex].yrepeat;
			} else if (nObject >= kWeaponItemBase && nObject < kWeaponItemMax) {
				int nWeaponIndex = nObject - kWeaponItemBase;

				pSprite.lotag = (short) nObject;
				pSprite.picnum = (short) gWeaponItemData[nWeaponIndex].picnum;
				pSprite.shade = (byte) gWeaponItemData[nWeaponIndex].shade;
				pSprite.xrepeat = (short) gWeaponItemData[nWeaponIndex].xrepeat;
				pSprite.yrepeat = (short) gWeaponItemData[nWeaponIndex].yrepeat;
			}

			GetSpriteExtents(pSprite);
			if (extents_zBot >= floorz)
				pSprite.z -= (extents_zBot - floorz);

			return pSprite;
		}
		return null;
	}

	public static boolean actHealDude(XSPRITE pXDude, int healValue,
			int maxHealthClip) {
		if (pXDude == null)
			game.dassert("pXDude != null");

		healValue <<= 4; // fix this later in the calling code
		maxHealthClip <<= 4;
		if (pXDude.health < maxHealthClip) {
			SPRITE pSprite = sprite[pXDude.reference];
			if (IsPlayerSprite(pSprite))
				sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, 780,
						pSprite.sectnum);
			pXDude.health = ClipHigh(pXDude.health + healValue, maxHealthClip);
			return true;
		}
		return false;
	}

	public static void actExplodeSprite(int nSprite) {
		SPRITE pSprite = sprite[nSprite];
		int nXSprite = pSprite.extra;
		int nType;
		if (!(nXSprite > 0 && nXSprite < kMaxXSprites))
			game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");

		// already exploding?
		if (pSprite.statnum == kStatExplosion)
			return;

		sfxKill3DSound(pSprite, -1, -1);
		checkEventList(nSprite, SS_SPRITE);

		switch (pSprite.lotag) {
		case kMissileNapalm:
			nType = 7;
			seqSpawn(4, SS_SPRITE, nXSprite, null);
			if (Chance(0x4000))
				pSprite.cstat |= kSpriteFlipX;
			sfxStart3DSound(pSprite, 303, -1, 0);
			actGenerateGibs(pSprite, 5, null, null);

			break;
		case kMissileStarburstFlare:
			nType = 3;
			seqSpawn(9, SS_SPRITE, nXSprite, null);
			if (Chance(0x4000))
				pSprite.cstat |= kSpriteFlipX;
			sfxStart3DSound(pSprite, 306, (pSprite.xvel & 3) + 24, 0);
			actGenerateGibs(pSprite, 5, null, null);
			break;
		case kMissileTchernobog:
		case kMissileTchernobog2:
			seqSpawn(5, SS_SPRITE, nXSprite, null);
			sfxStart3DSound(pSprite, 304, -1, 0);
			nType = 3;
			actGenerateGibs(pSprite, 5, null, null);
			break;
		case kThingTNTBarrel:
			// spawn an explosion effect
			 int nEffect = actSpawnSprite(pSprite.sectnum, pSprite.x, pSprite.y,
					pSprite.z, kStatDefault, true);
			sprite[nEffect].owner = pSprite.owner; // set owner for frag/targeting

			// place barrel on the respawn list or just delete it
			if (actCheckRespawn(pSprite)) {
				XSPRITE pXSprite = xsprite[nXSprite];
				pXSprite.state = 0;
				pXSprite.health = thingInfo[kThingTNTBarrel - kThingBase].startHealth << 4;
			} else
				actPostSprite(nSprite, kStatFree);

			// reset locals to point at the effect, not the barrel
			nType = 2;
			nSprite = nEffect;
			pSprite = sprite[nEffect];
			nXSprite = pSprite.extra;
			seqSpawn(4, SS_SPRITE, nXSprite, null); // kSeqExplodeC2L
			sfxStart3DSound(pSprite, 305, -1, 0);
			actGenerateGibs(pSprite, 14, null, null);
			break;

		case kThingPodFire:
			nType = 3;
			seqSpawn(9, SS_SPRITE, nXSprite, null);
			sfxStart3DSound(pSprite, 307, -1, 0);
			actGenerateGibs(pSprite, 5, null, null);
			actSpawnTentacleBlood(pSprite);
			break;

		case kThingTNTStick:
			nType = 0;
			if (gSpriteHit[nXSprite].floorHit != 0)
				seqSpawn(3, SS_SPRITE, nXSprite, null);
			else
				seqSpawn(4, SS_SPRITE, nXSprite, null);

			sfxStart3DSound(pSprite, 303, -1, 0);
			actGenerateGibs(pSprite, 5, null, null);
			break;
		case kThingTNTBundle:
		case kThingTNTProx:
		case kGDXThingTNTProx:
		case kThingTNTRem:
			nType = 1;
			if (gSpriteHit[nXSprite].floorHit != 0)
				seqSpawn(3, SS_SPRITE, nXSprite, null);
			else
				seqSpawn(4, SS_SPRITE, nXSprite, null);
			sfxStart3DSound(pSprite, 304, -1, 0);
			actGenerateGibs(pSprite, 5, null, null);
			break;
		case kThingSprayBundle:
			nType = 4;
			seqSpawn(5, SS_SPRITE, nXSprite, null);
			sfxStart3DSound(pSprite, 307, -1, 0);
			actGenerateGibs(pSprite, 5, null, null);
			break;
		case kThingHiddenExploder:

			// Defaults for exploder
			nType = 1; int nSnd = 304; int nSeq = 4;

			// Temp variables for override via data fields
			int tSnd = 0; int tSeq = 0;


			XSPRITE pXSPrite = xsprite[nXSprite];
			nType = pXSPrite.data1;  // Explosion type
			tSeq = pXSPrite.data2; // SEQ id
			tSnd = pXSPrite.data3; // Sound Id

			if (nType <= 1 || nType > kExplodeMax) { nType = 1;
			}
			else if (nType == 2) {
				nSnd = 305; }
			else if (nType == 3) { nSeq = 9; nSnd = 307; }
			else if (nType == 4) { nSeq = 5; nSnd = 307; }
			else if (nType <= 6) {
				nSnd = 303; }
			else if (nType == 7) {
				nSnd = 303; }
			else if (nType == 8) { nType = 0; nSeq = 3; nSnd = 303;}

			// Override previous sound and seq assigns
			if (tSeq > 0) nSeq = tSeq;
			if (tSnd > 0) nSnd = tSnd;

			if (BuildGdx.cache.contains(pXSPrite.data2, seq))
				seqSpawn(nSeq, SS_SPRITE, nXSprite, null);

			sfxStart3DSound(pSprite, nSnd, -1, 0);
			break;

		default:
			nType = 1;
			seqSpawn(4 /* kSeqExplodeC2M */, SS_SPRITE, nXSprite, null);
			if (Chance(0x4000))
				pSprite.cstat |= kSpriteFlipX;
			sfxStart3DSound(pSprite, 303, -1, 0);
			actGenerateGibs(pSprite, 5, null, null);
			break;
		}

		sprXVel[nSprite] = sprYVel[nSprite] = sprZVel[nSprite] = 0;
		actPostSprite(nSprite, kStatExplosion);
		pSprite.hitag &= ~(kAttrMove | kAttrGravity);

		EXPLODE pExpl = gExplodeData[nType];

		pSprite.yrepeat = pExpl.size;
		pSprite.xrepeat = pExpl.size;
		pSprite.lotag = (short) nType;

		xsprite[nXSprite].target = 0;
		xsprite[nXSprite].data1 = (short) pExpl.liveCount;
		xsprite[nXSprite].data2 = (short) pExpl.quake;
		xsprite[nXSprite].data3 = (short) pExpl.used3;
	}

	public static boolean IsItemSprite(SPRITE pSprite) {
		return pSprite.lotag >= kItemBase && pSprite.lotag < kItemMax;
	}

	public static boolean IsDudeSprite(SPRITE pSprite) {
		return pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax;
	}

	public static boolean IsBurningDude(SPRITE pSprite) {
		if (pSprite == null) return false;
		switch (pSprite.lotag){
			case kDudeBurning:
			case kDudeCultistBurning:
			case kDudeAxeZombieBurning:
			case kDudeBloatedButcherBurning:
			case kDudeTinyCalebburning:
			case kDudeTheBeastburning:
			case kGDXGenDudeBurning:
				return true;
		}

		return false;
	}

	public static boolean IsKillableDude(SPRITE pSprite) {
		switch (pSprite.lotag) {
			case kDudeFleshStatue:
			case kDudeStoneStatue:
				return false;
			default:
				return IsDudeSprite(pSprite) && xsprite[pSprite.extra].Locked != 1;
		}
	}

	public static boolean IsAmmoSprite(SPRITE pSprite) {
		return pSprite.lotag >= kAmmoItemBase && pSprite.lotag < kAmmoItemMax;
	}

	public static boolean IsWeaponSprite(SPRITE pSprite) {
		return pSprite.lotag >= kWeaponItemBase && pSprite.lotag < kWeaponItemMax;
	}

	public static boolean IsUnderwaterSector(int sectnum) {
		int nXSector = sector[sectnum].extra;
		return nXSector > 0 && xsector[nXSector].Underwater;
	}

	public static int actGetBurnTime(XSPRITE pXActor) {
		return pXActor.burnTime;
	}

	public static int actGetBurnSource(int nSource) {
		if (nSource != -1) {
			if ((nSource & 0x1000) != 0)
				nSource = gPlayer[nSource & 0xFFF].pSprite.xvel;
			else nSource &= 0xFFF;
		}
		return nSource;
	}

	public static int actSetBurnSource(int nSource) {
		if (nSource != -1) {
			if (!(nSource >= 0 && nSource < kMaxSprites))
				game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
			SPRITE pSprite = sprite[nSource];
			if (IsPlayerSprite(pSprite)) {
				nSource = pSprite.lotag - kDudePlayer1;
				nSource |= 0x1000;
			}
		}
		return nSource;
	}

	public static void TchernobogCallback1(int nXIndex) {
		int nSprite = xsprite[nXIndex].reference;
		SPRITE pSpawn = actSpawnEffect(32, sprite[nSprite].sectnum,
				sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, 0);
		if (pSpawn != null) {
			sprXVel[pSpawn.xvel] = sprXVel[nSprite];
			sprYVel[pSpawn.xvel] = sprYVel[nSprite];
			sprZVel[pSpawn.xvel] = sprZVel[nSprite];
		}
	}

	public static void TchernobogCallback2(int nXIndex) {
		int nSprite = xsprite[nXIndex].reference;
		SPRITE pSpawn = actSpawnEffect(33, sprite[nSprite].sectnum,
				sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, 0);
		if (pSpawn != null) {
			sprXVel[pSpawn.xvel] = sprXVel[nSprite];
			sprYVel[pSpawn.xvel] = sprYVel[nSprite];
			sprZVel[pSpawn.xvel] = sprZVel[nSprite];
		}
	}

	public static void FireballCallback(int nXIndex) {
		int nSprite = xsprite[nXIndex].reference;
		SPRITE pSpawn = actSpawnEffect(11, sprite[nSprite].sectnum,
				sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, 0);
		if (pSpawn != null) {
			sprXVel[pSpawn.xvel] = sprXVel[nSprite];
			sprYVel[pSpawn.xvel] = sprYVel[nSprite];
			sprZVel[pSpawn.xvel] = sprZVel[nSprite];
		}
	}

	public static void SmokeCallback(int nXIndex) {
		int nSprite = xsprite[nXIndex].reference;
		SPRITE pSpawn = actSpawnEffect(12, sprite[nSprite].sectnum,
				sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, 0);
		if (pSpawn != null) {
			sprXVel[pSpawn.xvel] = sprXVel[nSprite];
			sprYVel[pSpawn.xvel] = sprYVel[nSprite];
			sprZVel[pSpawn.xvel] = sprZVel[nSprite];
		}
	}

	public static void DamageTreeCallback(int nXIndex) {
		XSPRITE pXSprite = xsprite[nXIndex];
		SPRITE pSprite = sprite[pXSprite.reference];
		pSprite.lotag = kThingExplodeObject;
		pXSprite.state = 1;
		pXSprite.data1 = 15;
		pXSprite.data3 = 0;
		pXSprite.data4 = 312;

		pXSprite.health = thingInfo[17].startHealth;

		pSprite.cstat |= 0x101;
	}

	public static final int[] NapalmAmmo = new int[2];

	public static void actNapalm2Explode(SPRITE pSprite, XSPRITE pXSprite) {
		actPostSprite(pSprite.xvel, kStatDefault);
		seqSpawn(9, SS_SPRITE, pSprite.extra, null);

		if (Chance(0x4000))
			pSprite.cstat |= kSpriteFlipX;

		sfxStart3DSound(pSprite, 303, (pSprite.xvel & 3) + 24, 0);
		actDistanceDamage(actGetBurnSource(pSprite.owner), pSprite.x,
				pSprite.y, pSprite.z, pSprite.sectnum, 128, 0, 3, 60, 15, 120);

		if (pXSprite.data4 > 4) {
			actGenerateGibs(pSprite, 5, null, null);
			int nAngle = pSprite.ang;
			sprXVel[pSprite.xvel] = 0;
			sprYVel[pSprite.xvel] = 0;
			sprZVel[pSprite.xvel] = 0;

			NapalmAmmo[0] = pXSprite.data4 >> 3;
			NapalmAmmo[1] = (pXSprite.data4 >> 2) - NapalmAmmo[0];
			for (int i = 0; i < 2; i++) {
				int velocity = 0x33333 + Random(0x33333);
				pSprite.ang = (short) ((nAngle + BiRandom(113)) & kAngleMask);
				SPRITE pThing = actFireThing(pSprite.xvel, 0, 0, -37840,
						kThingAltNapalm, velocity);
				XSPRITE pXThing = xsprite[pThing.extra];
				pThing.owner = pSprite.owner;
				seqSpawn(getSeq(kNapalm), SS_SPRITE, pThing.extra, callbacks[SmokeCallback]);
				pXThing.data4 = 4 * (NapalmAmmo[i] & 0xFFFF);
			}
		}
	}

	public static void DamageDude(int nXIndex, int count) {
		XSPRITE pXSprite = xsprite[nXIndex];
		SPRITE pSprite = sprite[pXSprite.reference];
		pSprite.lotag = kThingGib;
		pXSprite.state = 1;
		pXSprite.data1 = (short) count;
		pXSprite.data2 = 0;
		pXSprite.data3 = 0;
		pXSprite.data4 = 319;
		pXSprite.targetX = gFrameClock;
		pXSprite.isTriggered = false;
		pXSprite.triggerOnce = false;
		pXSprite.Locked = 0;

		pXSprite.health = thingInfo[kThingGib - kThingBase].startHealth;
	}

	public static int getDudeMassBySpriteSize(SPRITE pSprite) {
		int mass = 0; int minMass = 5;
		if (!IsDudeSprite(pSprite)) return minMass;

		int seqStartId = dudeInfo[pSprite.lotag - kDudeBase].seqStartID;
		switch (pSprite.lotag) {
			case kGDXDudeUniversalCultist:
			case kGDXGenDudeBurning:
				seqStartId = xsprite[pSprite.extra].data2;
				break;
		}

		if (gSpriteMass[pSprite.xvel] == null)
			gSpriteMass[pSprite.xvel] = new SPRITEMASS();

		SPRITEMASS cachedMass = gSpriteMass[pSprite.xvel];
		if (seqStartId == cachedMass.seqId && pSprite.xrepeat == cachedMass.xrepeat &&
				pSprite.yrepeat == cachedMass.yrepeat && pSprite.clipdist == cachedMass.clipdist)
				return cachedMass.mass;

		SeqType pSeq = SeqType.getInstance(seqStartId);
		int picnum = (pSeq != null) ? pSeq.getFrame(0).nTile : pSprite.picnum;
		int clipDist = (pSprite.clipdist <= 0) ? dudeInfo[pSprite.lotag - kDudeBase].clipdist : pSprite.clipdist;

		Tile pic = engine.getTile(picnum);

		short xrepeat = pSprite.xrepeat;
		int x = pic.getWidth();
		if (xrepeat > 64) x+=((xrepeat - 64)*2);
		else if (xrepeat < 64) x-=((64 - xrepeat)*2);

		short yrepeat = pSprite.yrepeat;
		int y = pic.getHeight();
		if (yrepeat > 64) y+=((yrepeat - 64)*2);
		else if (yrepeat < 64) y-=((64 - yrepeat)*2);

		mass = ((x + y) * clipDist) / 25;
		//if ((mass+=(x+y)) > 200) mass+=((mass - 200)*16);

		cachedMass.seqId = seqStartId;	cachedMass.xrepeat = xrepeat;
		cachedMass.yrepeat = yrepeat;	cachedMass.mass = ClipRange(mass, minMass, 65535);
		cachedMass.clipdist = clipDist;

		return cachedMass.mass;
	}
}
