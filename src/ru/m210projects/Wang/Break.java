package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_WALL_1WAY;
import static ru.m210projects.Wang.Gameutils.CSTAT_WALL_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_WALL_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_WALL_MASKED;
import static ru.m210projects.Wang.Gameutils.CSTAT_WALL_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.SET_GOTPIC;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG2;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_X;
import static ru.m210projects.Wang.Gameutils.SPRX_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.SPRX_BURNABLE;
import static ru.m210projects.Wang.Gameutils.SPR_SO_ATTACHED;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.SP_TAG8;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.WALLFX_DONT_STICK;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Light.DoLightingMatch;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_BREAKABLE;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_SUICIDE;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSpawnSpotsForDamage;
import static ru.m210projects.Wang.Sector.DoSpawnSpotsForKill;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Sound.DeleteNoSoundOwner;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_HIT_MATCH;
import static ru.m210projects.Wang.Tags.TAG_WALL_BREAK;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.MAXLONG;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.CheckBreakToughness;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.SpawnBreakFlames;
import static ru.m210projects.Wang.Weapon.SpriteQueueDelete;

import java.util.Arrays;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Type.Break_Info;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.USER;

public class Break {

	public static Break_Info GlobBreakInfo;

	public static final int BF_TOUGH = (BIT(0));
	public static final int BF_KILL = (BIT(1));
	public static final int BF_BURN = (BIT(2));
	public static final int BF_OVERRIDE_BLOCK = (BIT(3));
	public static final int BF_FIRE_FALL = (BIT(4));
	public static final int BF_LEAVE_BREAK = (BIT(5));

	public static final int SHRAP_NONE = 0, SHRAP_GLASS = 1, //
			SHRAP_TREE_BARK = 2, // (NEED) outside tree bark
			SHRAP_SO_SMOKE = 3, // only used for damaged SO's
			SHRAP_PAPER = 4, //
			SHRAP_BLOOD = 5, // std blood from gibs
			SHRAP_EXPLOSION = 6, // small explosion
			SHRAP_LARGE_EXPLOSION = 7, // large explosion
			SHRAP_METAL = 8, //
			SHRAP_STONE = 9, // what we have might be ok
			SHRAP_PLANT = 10, // (NEED)
			SHRAP_GIBS = 11, // std blood and guts
			SHRAP_WOOD = 12, //
			SHRAP_GENERIC = 13, // what we have might be ok - sort of gray brown rock look
			SHRAP_TREE_PULP = 14, // (NEED) inside tree wood
			SHRAP_COIN = 15, SHRAP_METALMIX = 16, SHRAP_WOODMIX = 17, SHRAP_MARBELS = 18, SHRAP_PAPERMIX = 19,
			SHRAP_USER_DEFINED = 99;

	public static Break_Info WallBreakInfo[] = { new Break_Info(60, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(82, -1, SHRAP_METAL, BF_KILL), new Break_Info(1, 3593, SHRAP_METAL),
			new Break_Info(253, 255, SHRAP_GLASS), new Break_Info(254, 255, SHRAP_GLASS),
			new Break_Info(282, -1, SHRAP_GLASS), new Break_Info(283, 4974, SHRAP_METAL),
			new Break_Info(318, 599, SHRAP_GLASS), new Break_Info(486, -1, SHRAP_METAL),
			new Break_Info(487, 3676, SHRAP_METAL), new Break_Info(628, 3585, SHRAP_METAL),
			new Break_Info(630, 3586, SHRAP_METAL), new Break_Info(633, 608, SHRAP_GLASS),
			new Break_Info(634, 608, SHRAP_GLASS), new Break_Info(637, 3587, SHRAP_METAL),
			new Break_Info(640, 3588, SHRAP_METAL), new Break_Info(641, 3588, SHRAP_METAL),
			new Break_Info(665, 3588, SHRAP_METAL), new Break_Info(742, 3589, SHRAP_COIN),
			new Break_Info(743, 3590, SHRAP_COIN), new Break_Info(750, 608, SHRAP_GLASS),
			new Break_Info(2667, 608, SHRAP_GLASS), new Break_Info(2769, 3681, SHRAP_GLASS),
			new Break_Info(2676, 3591, SHRAP_GLASS), new Break_Info(2677, 3592, SHRAP_GLASS),
			new Break_Info(2687, 2727, SHRAP_GLASS), new Break_Info(2688, 2728, SHRAP_GLASS),
			new Break_Info(2732, 3594, SHRAP_GLASS), new Break_Info(2777, 3683, SHRAP_METAL),
			new Break_Info(2778, 2757, SHRAP_GLASS), new Break_Info(2801, 3591, SHRAP_GLASS),
			new Break_Info(2804, 3595, SHRAP_GLASS), new Break_Info(2807, 3596, SHRAP_GLASS),
			new Break_Info(2810, 4989, SHRAP_METAL), new Break_Info(4890, 4910, SHRAP_METAL),
			new Break_Info(4891, 4911, SHRAP_METAL), new Break_Info(4892, 4912, SHRAP_METAL),
			new Break_Info(4893, 4913, SHRAP_METAL), new Break_Info(4894, 4914, SHRAP_METAL),
			new Break_Info(4895, 4915, SHRAP_METAL), new Break_Info(3336, 4940, SHRAP_COIN),
			new Break_Info(3337, 4941, SHRAP_COIN), new Break_Info(4885, 4888, SHRAP_METAL),
			new Break_Info(4887, 4889, SHRAP_COIN), new Break_Info(3350, 4942, SHRAP_GLASS),
			new Break_Info(3351, 4943, SHRAP_METAL), new Break_Info(3352, 4944, SHRAP_METAL),
			new Break_Info(3353, 4945, SHRAP_METAL), new Break_Info(4896, 4898, SHRAP_METAL),
			new Break_Info(4897, 4899, SHRAP_METAL), new Break_Info(3385, 4981, SHRAP_METALMIX),
			new Break_Info(3389, 4982, SHRAP_METALMIX), new Break_Info(3393, 4984, SHRAP_METALMIX),
			new Break_Info(3397, 4983, SHRAP_METALMIX), new Break_Info(3401, 4985, SHRAP_METALMIX),
			new Break_Info(3405, 4986, SHRAP_METALMIX), new Break_Info(3409, 4988, SHRAP_METALMIX),
			new Break_Info(3413, 4987, SHRAP_METALMIX), new Break_Info(253, 255, SHRAP_METALMIX),
			new Break_Info(283, 4974, SHRAP_METALMIX), new Break_Info(299, 4975, SHRAP_METALMIX),
			new Break_Info(5078, 5079, SHRAP_METALMIX), new Break_Info(5080, 5092, SHRAP_MARBELS),
			new Break_Info(5083, 5093, SHRAP_MARBELS), new Break_Info(5086, 5094, SHRAP_MARBELS),
			new Break_Info(5089, 5095, SHRAP_MARBELS), new Break_Info(4970, 4973, SHRAP_METAL),
			new Break_Info(297, 4980, SHRAP_METAL), new Break_Info(1, 4976, SHRAP_METAL),
			new Break_Info(4917, 4918, SHRAP_METAL), new Break_Info(4902, 4903, SHRAP_METAL), };

	public static Break_Info SpriteBreakInfo[] = { new Break_Info(60, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(82, -1, SHRAP_METAL, BF_KILL), new Break_Info(138, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(253, 255, SHRAP_GLASS), new Break_Info(254, 255, SHRAP_GLASS),
			new Break_Info(270, -1, SHRAP_PAPER, BF_BURN), new Break_Info(271, -1, SHRAP_PAPER, BF_BURN),
			new Break_Info(272, -1, SHRAP_WOOD), new Break_Info(274, -1, SHRAP_PAPER, BF_BURN),
			new Break_Info(282, -1, SHRAP_GLASS), new Break_Info(283, -1, SHRAP_METAL),
			new Break_Info(297, -1, SHRAP_METAL), new Break_Info(299, -1, SHRAP_METAL),
			new Break_Info(363, -1, SHRAP_METAL, BF_KILL), new Break_Info(365, -1, SHRAP_STONE, BF_TOUGH | BF_KILL),
			new Break_Info(366, -1, SHRAP_METAL, BF_KILL, 5), new Break_Info(367, -1, SHRAP_WOOD, BF_KILL),
			new Break_Info(368, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(369, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(371, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(372, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(374, -1, SHRAP_STONE, BF_TOUGH | BF_KILL),
			new Break_Info(375, -1, SHRAP_STONE, BF_TOUGH | BF_KILL),
			new Break_Info(376, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(377, -1, SHRAP_STONE, BF_KILL), new Break_Info(379, -1, SHRAP_WOOD, BF_KILL),
			new Break_Info(380, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL), new Break_Info(385, -1, SHRAP_BLOOD, BF_KILL),
			new Break_Info(386, -1, SHRAP_GIBS, BF_KILL), new Break_Info(387, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(388, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), new Break_Info(391, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(392, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(393, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(394, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), new Break_Info(395, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(396, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL),
			new Break_Info(397, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL),
			new Break_Info(398, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL),
			new Break_Info(399, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL),
			new Break_Info(400, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(401, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(402, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(407, -1, SHRAP_METAL, BF_KILL), new Break_Info(408, -1, SHRAP_PAPER, BF_BURN),
			new Break_Info(409, -1, SHRAP_PAPER, BF_BURN),
			new Break_Info(415, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL, 5),
			new Break_Info(418, -1, SHRAP_GENERIC, BF_KILL | BF_FIRE_FALL, 5),
			new Break_Info(422, -1, SHRAP_METAL, BF_KILL), new Break_Info(423, -1, SHRAP_BLOOD, BF_KILL),
			new Break_Info(424, -1, SHRAP_BLOOD, BF_KILL), new Break_Info(425, -1, SHRAP_BLOOD, BF_KILL),
			new Break_Info(428, -1, SHRAP_METAL, BF_BURN), new Break_Info(430, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(431, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(432, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(433, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(434, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(435, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(436, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL),
			new Break_Info(437, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL), new Break_Info(438, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(441, -1, SHRAP_WOOD, BF_KILL), new Break_Info(442, -1, SHRAP_STONE, BF_KILL),
			new Break_Info(443, -1, SHRAP_STONE, BF_KILL | BF_FIRE_FALL), new Break_Info(453, -1, SHRAP_WOOD, BF_KILL),
			new Break_Info(458, -1, SHRAP_STONE, BF_KILL), new Break_Info(459, -1, SHRAP_STONE, BF_KILL),
			new Break_Info(460, -1, SHRAP_METAL, BF_KILL), new Break_Info(461, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(462, -1, SHRAP_METAL, BF_KILL), new Break_Info(463, -1, SHRAP_STONE, BF_KILL),
			new Break_Info(467, -1, SHRAP_STONE, BF_KILL), new Break_Info(468, -1, SHRAP_WOOD, BF_KILL),
			new Break_Info(475, -1, SHRAP_GLASS, BF_KILL), new Break_Info(481, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(482, -1, SHRAP_WOOD, BF_KILL), new Break_Info(483, -1, SHRAP_WOOD, BF_KILL | BF_TOUGH),
			new Break_Info(491, -1, SHRAP_WOOD, BF_KILL), new Break_Info(492, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(493, -1, SHRAP_METAL, BF_KILL), new Break_Info(498, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(500, -1, SHRAP_METAL, BF_KILL), new Break_Info(501, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(504, -1, SHRAP_METAL, BF_KILL, 5), new Break_Info(505, -1, SHRAP_BLOOD, BF_KILL, 5),
			new Break_Info(506, -1, SHRAP_GENERIC, BF_KILL, 5), new Break_Info(507, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(508, -1, SHRAP_GLASS, BF_KILL), new Break_Info(509, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(510, -1, SHRAP_GLASS, BF_KILL), new Break_Info(511, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(512, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL, 5),
			new Break_Info(516, -1, SHRAP_WOOD, BF_BURN), new Break_Info(517, -1, SHRAP_WOOD, BF_BURN),
			new Break_Info(518, -1, SHRAP_WOOD, BF_BURN),
			new Break_Info(519, -1, SHRAP_WOOD, BF_FIRE_FALL | BF_KILL, 5),
			new Break_Info(520, -1, SHRAP_WOOD, BF_KILL), new Break_Info(521, -1, SHRAP_WOOD, BF_KILL | BF_FIRE_FALL),
			new Break_Info(537, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL),
			new Break_Info(541, -1, SHRAP_WOOD, BF_KILL | BF_FIRE_FALL), new Break_Info(586, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(590, -1, SHRAP_METAL, BF_KILL), new Break_Info(591, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(593, 608, SHRAP_GLASS, BF_TOUGH), new Break_Info(604, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(613, -1, SHRAP_LARGE_EXPLOSION, BF_KILL), new Break_Info(614, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(615, -1, SHRAP_METAL, BF_KILL), new Break_Info(618, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(646, -1, SHRAP_METAL, BF_KILL), new Break_Info(647, -1, SHRAP_LARGE_EXPLOSION, BF_KILL),
			new Break_Info(648, -1, SHRAP_LARGE_EXPLOSION, BF_KILL), new Break_Info(649, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(656, -1, SHRAP_METAL, BF_KILL), new Break_Info(657, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(658, -1, SHRAP_LARGE_EXPLOSION, BF_KILL), new Break_Info(659, -1, SHRAP_METAL, BF_KILL, 5),
			new Break_Info(663, -1, SHRAP_METAL, BF_KILL, 10), new Break_Info(664, -1, SHRAP_METAL, BF_KILL, 5),
			new Break_Info(666, -1, SHRAP_PLANT, BF_KILL), new Break_Info(670, -1, SHRAP_METAL, BF_KILL | BF_FIRE_FALL),
			new Break_Info(671, -1, SHRAP_GLASS, BF_KILL | BF_FIRE_FALL), new Break_Info(673, -1, SHRAP_BLOOD, BF_KILL),
			new Break_Info(674, -1, SHRAP_GIBS, BF_KILL), new Break_Info(675, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(676, -1, SHRAP_GIBS, BF_KILL), new Break_Info(678, -1, SHRAP_GLASS, BF_KILL, 5),
			new Break_Info(679, -1, SHRAP_GLASS, BF_KILL, 5), new Break_Info(683, -1, SHRAP_GLASS, BF_KILL, 5),
			new Break_Info(684, -1, SHRAP_GLASS, BF_KILL, 5), new Break_Info(685, -1, SHRAP_GLASS, BF_KILL, 5),
			new Break_Info(686, -1, SHRAP_PAPER, BF_KILL, 5), new Break_Info(687, -1, SHRAP_STONE, BF_KILL | BF_TOUGH),
			new Break_Info(688, -1, SHRAP_STONE, BF_KILL | BF_TOUGH),
			new Break_Info(690, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(692, -1, SHRAP_WOOD, BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(695, -1, SHRAP_STONE, BF_KILL), new Break_Info(696, -1, SHRAP_STONE, BF_KILL),
			new Break_Info(697, -1, SHRAP_STONE, BF_KILL), new Break_Info(698, -1, SHRAP_STONE, BF_KILL),
			new Break_Info(699, -1, SHRAP_STONE, BF_TOUGH | BF_KILL),
			new Break_Info(702, -1, SHRAP_STONE, BF_TOUGH | BF_KILL),
			new Break_Info(703, -1, SHRAP_STONE, BF_TOUGH | BF_KILL),
			new Break_Info(704, -1, SHRAP_STONE, BF_TOUGH | BF_KILL), new Break_Info(706, -1, SHRAP_PLANT, BF_KILL),
			new Break_Info(707, -1, SHRAP_PLANT, BF_KILL), new Break_Info(710, -1, SHRAP_PLANT, BF_KILL),
			new Break_Info(711, -1, SHRAP_PLANT, BF_KILL), new Break_Info(714, -1, SHRAP_STONE, BF_KILL, 5),
			new Break_Info(721, -1, SHRAP_GIBS, BF_KILL), new Break_Info(722, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(723, -1, SHRAP_GIBS, BF_KILL), new Break_Info(724, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(725, -1, SHRAP_PLANT, BF_KILL), new Break_Info(730, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(744, -1, SHRAP_GLASS, BF_KILL, 5), new Break_Info(2563, -1, SHRAP_PAPER, BF_BURN),
			new Break_Info(2564, -1, SHRAP_PAPER, BF_BURN), new Break_Info(3570, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN),
			new Break_Info(3571, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(3572, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(3573, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(3574, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(3575, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(3576, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(3577, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(3578, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(3579, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(3580, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(3581, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN),
			new Break_Info(3582, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN), new Break_Info(2640, -1, SHRAP_STONE, BF_KILL, 5),
			new Break_Info(2641, -1, SHRAP_STONE, BF_KILL), new Break_Info(2642, -1, SHRAP_STONE, BF_KILL),
			new Break_Info(2680, -1, SHRAP_GENERIC, BF_KILL), new Break_Info(2681, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(2682, -1, SHRAP_GENERIC, BF_KILL), new Break_Info(2683, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(2684, -1, SHRAP_GENERIC, BF_KILL), new Break_Info(2685, -1, SHRAP_GENERIC, BF_KILL),
			new Break_Info(2687, 2727, SHRAP_GLASS), new Break_Info(2688, 2728, SHRAP_GLASS),
			new Break_Info(2699, -1, SHRAP_WOOD, BF_KILL), new Break_Info(2709, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(2720, -1, SHRAP_GIBS, BF_KILL), new Break_Info(2721, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(2722, -1, SHRAP_GIBS, BF_KILL), new Break_Info(2723, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(2724, -1, SHRAP_GIBS, BF_KILL), new Break_Info(2725, -1, SHRAP_BLOOD, BF_KILL),
			new Break_Info(2726, -1, SHRAP_BLOOD, BF_KILL), new Break_Info(2719, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(2750, -1, SHRAP_WOOD, BF_KILL), new Break_Info(2676, 3591, SHRAP_GLASS),
			new Break_Info(2769, 3681, SHRAP_GLASS), new Break_Info(2777, 3683, SHRAP_METAL, BF_TOUGH),
			new Break_Info(2778, 2757, SHRAP_GLASS), new Break_Info(3448, 3451, SHRAP_METAL, BF_TOUGH | BF_KILL),
			new Break_Info(3449, -1, SHRAP_PAPER, BF_KILL), new Break_Info(3497, -1, SHRAP_GENERIC, BF_KILL | BF_TOUGH),
			new Break_Info(3551, -1, SHRAP_METAL, BF_KILL), new Break_Info(3552, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(3553, -1, SHRAP_METAL, BF_KILL), new Break_Info(3554, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(3555, -1, SHRAP_METAL, BF_KILL), new Break_Info(3556, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(3557, -1, SHRAP_METAL, BF_KILL), new Break_Info(3558, -1, SHRAP_WOOD, BF_KILL),
			new Break_Info(3568, -1, SHRAP_WOOD, BF_BURN), new Break_Info(4994, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(4995, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(5010, -1, SHRAP_WOOD, BF_TOUGH | BF_BURN | BF_OVERRIDE_BLOCK),
			new Break_Info(5017, -1, SHRAP_PAPER, BF_KILL), new Break_Info(5018, -1, SHRAP_PAPER, BF_KILL),
			new Break_Info(5019, -1, SHRAP_PAPER, BF_KILL), new Break_Info(5060, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(5061, -1, SHRAP_METAL, BF_KILL), new Break_Info(5073, -1, SHRAP_GIBS, BF_KILL),

			// Evil ninja Hari-Kari - can gib
			new Break_Info(4218, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),

			// old ninja dead frames
			new Break_Info(1133, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),
			new Break_Info(1134, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),

			// dead actors
			new Break_Info(811, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),
			new Break_Info(1440, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),
			new Break_Info(1512, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),
			new Break_Info(1643, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),
			new Break_Info(1680, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),

			new Break_Info(4219 + 7, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH),
			new Break_Info(4236, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), // Evil Ninja cut in half
			new Break_Info(4421, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), // Dead Big Ripper
			new Break_Info(4312, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), // Dead Coolie Ghost
			new Break_Info(766, -1, SHRAP_COIN, BF_KILL), new Break_Info(767, -1, SHRAP_COIN, BF_KILL),
			new Break_Info(2700, -1, SHRAP_PAPER, BF_KILL), new Break_Info(2701, -1, SHRAP_PAPER, BF_KILL),
			new Break_Info(2702, -1, SHRAP_PAPER, BF_KILL), new Break_Info(2703, -1, SHRAP_PAPER, BF_KILL),
			new Break_Info(2704, -1, SHRAP_PAPER, BF_KILL), new Break_Info(2705, -1, SHRAP_PAPER, BF_KILL),
			new Break_Info(2218, -1, SHRAP_METAL, BF_KILL), // Caltrops are breakable
			new Break_Info(689, -1, SHRAP_METAL, BF_TOUGH | BF_KILL), new Break_Info(3354, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(3357, -1, SHRAP_PAPER, BF_KILL), new Break_Info(4886, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(646, 708, SHRAP_METAL, BF_TOUGH | BF_KILL),
			new Break_Info(708, -1, SHRAP_METAL, BF_TOUGH | BF_KILL), new Break_Info(656, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(663, -1, SHRAP_METAL, BF_KILL), new Break_Info(664, -1, SHRAP_METAL, BF_KILL),
			new Break_Info(691, -1, SHRAP_METAL, BF_KILL), new Break_Info(5021, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(712, -1, SHRAP_LARGE_EXPLOSION, BF_KILL),
			new Break_Info(713, -1, SHRAP_LARGE_EXPLOSION, BF_KILL),
			new Break_Info(693, -1, SHRAP_WOODMIX, BF_KILL | BF_TOUGH), new Break_Info(5041, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(5042, 5077, SHRAP_GIBS, BF_TOUGH | BF_KILL),
			new Break_Info(5077, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(3356, 3358, SHRAP_WOOD, BF_TOUGH | BF_KILL),
			new Break_Info(3358, -1, SHRAP_WOOD, BF_TOUGH | BF_KILL), new Break_Info(900, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(901, -1, SHRAP_GIBS, BF_KILL), new Break_Info(902, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(915, -1, SHRAP_GIBS, BF_KILL), new Break_Info(916, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(917, -1, SHRAP_GIBS, BF_KILL), new Break_Info(930, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(931, -1, SHRAP_GIBS, BF_KILL), new Break_Info(932, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(1670, -1, SHRAP_GIBS, BF_KILL), new Break_Info(2219, -1, SHRAP_METAL, BF_KILL | BF_TOUGH),
			new Break_Info(4768, -1, SHRAP_GLASS, BF_KILL), new Break_Info(4792, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(4816, -1, SHRAP_GLASS, BF_KILL), new Break_Info(4840, -1, SHRAP_GLASS, BF_KILL),
			new Break_Info(4584, -1, SHRAP_GIBS, BF_KILL), new Break_Info(5062, -1, SHRAP_WOOD, BF_KILL | BF_TOUGH),
			new Break_Info(5063, 4947, SHRAP_PAPERMIX, BF_KILL | BF_TOUGH | BF_LEAVE_BREAK),
			new Break_Info(4947, -1, SHRAP_PAPERMIX, BF_KILL | BF_TOUGH),
			new Break_Info(1160, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), new Break_Info(5104, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(3795, -1, SHRAP_GIBS, BF_KILL), new Break_Info(470, -1, SHRAP_GIBS, BF_KILL),
			new Break_Info(5205, -1, SHRAP_GIBS, BF_KILL | BF_TOUGH), };

	//////////////////////////////////////////////
	// SORT & SEARCH SUPPORT
	//////////////////////////////////////////////

	public static Break_Info FindWallBreakInfo(short picnum) {
		int i = Arrays.binarySearch(WallBreakInfo, picnum);
		if (i >= 0)
			return WallBreakInfo[i];
		return null;
	}

	public static Break_Info FindSpriteBreakInfo(short picnum) {
		int i = Arrays.binarySearch(SpriteBreakInfo, picnum);
		if (i >= 0)
			return SpriteBreakInfo[i];
		return null;
	}

	//////////////////////////////////////////////
	// SETUP
	//////////////////////////////////////////////

	public static void SortBreakInfo() {
		Arrays.sort(SpriteBreakInfo);
		Arrays.sort(WallBreakInfo);
	}

	public static Break_Info SetupWallForBreak(WALL WALL) {
		Break_Info break_info;

		break_info = FindWallBreakInfo(WALL.picnum);
		if (break_info != null) {
			WALL.lotag = TAG_WALL_BREAK;
			WALL.extra |= (WALLFX_DONT_STICK);

			// set for cacheing
			if (break_info.breaknum >= 0)
				SET_GOTPIC(break_info.breaknum);
		}

		if (WALL.overpicnum > 0 && TEST(WALL.cstat, CSTAT_WALL_MASKED)) {
			break_info = FindWallBreakInfo(WALL.overpicnum);
			if (break_info != null) {
				WALL.lotag = TAG_WALL_BREAK;
				WALL.extra |= (WALLFX_DONT_STICK);
			}
		}

		return (break_info);
	}

	public static Break_Info SetupSpriteForBreak(SPRITE sp) {
		short picnum = sp.picnum;
		Break_Info break_info;

		// ignore as a breakable if true
		if (sp.lotag == TAG_SPRITE_HIT_MATCH)
			return (null);

		break_info = FindSpriteBreakInfo(picnum);
		if (break_info != null) {

			// use certain sprites own blocking for determination
			if (TEST(break_info.flags, BF_OVERRIDE_BLOCK)) {
				// if not blocking then skip this code
				if (!TEST(sp.cstat, CSTAT_SPRITE_BLOCK)) {
					return null;
				}
			}

			if (TEST(break_info.flags, BF_BURN))
				sp.extra |= (SPRX_BURNABLE);
			else
				sp.extra |= (SPRX_BREAKABLE);

			sp.clipdist = SPRITEp_SIZE_X(sp);

			sp.cstat |= (CSTAT_SPRITE_BREAKABLE);

			// set for cacheing
			if (break_info.breaknum >= 0)
				SET_GOTPIC(break_info.breaknum);
		}

		return (break_info);
	}

//////////////////////////////////////////////
//ACTIVATE
//////////////////////////////////////////////

	public static short FindBreakSpriteMatch(short match) {
		short i, nexti;

		for (i = headspritestat[STAT_BREAKABLE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			if (SPRITE_TAG2(i) == match && sprite[i].picnum == ST1) {
				return (i);
			}
		}

		return (-1);
	}

	//
	// WALL
	//

	public static boolean AutoBreakWall(WALL WALL, int hitx, int hity, int hitz, short ang, int type) {
		Break_Info break_info;
		short BreakSprite;
		WALL nwp;
		SPRITE bsp;

		WALL.lotag = 0;
		if (WALL.nextwall >= 0) {
			nwp = wall[WALL.nextwall];

			// get rid of both sides
			// only break ONE of the walls

			if (nwp.lotag == TAG_WALL_BREAK && nwp.overpicnum > 0 && TEST(nwp.cstat, CSTAT_WALL_MASKED)) {
				nwp.lotag = 0;
			}
		}

		if (WALL.overpicnum > 0 && TEST(WALL.cstat, CSTAT_WALL_MASKED))
			break_info = FindWallBreakInfo(WALL.overpicnum);
		else
			break_info = FindWallBreakInfo(WALL.picnum);

		if (break_info == null) {
			return (false);
		}

		// Check to see if it should break with current weapon type
		if (!CheckBreakToughness(break_info, type))
			return (false);

		if (hitx != MAXLONG) {
			// need correct location for spawning shrap
			BreakSprite = (short) COVERinsertsprite((short) 0, STAT_DEFAULT);

			bsp = sprite[BreakSprite];
			bsp.cstat = 0;
			bsp.extra = 0;
			bsp.ang = ang;
			bsp.picnum = ST1;
			bsp.xrepeat = bsp.yrepeat = 64;
			engine.setspritez(BreakSprite, hitx, hity, hitz);

			// pass Break Info Globally
			GlobBreakInfo = break_info;
			SpawnShrap(BreakSprite, -1);
			GlobBreakInfo = null;

			KillSprite(BreakSprite);
		}

		// change the wall
		if (WALL.overpicnum > 0 && TEST(WALL.cstat, CSTAT_WALL_MASKED)) {
			if (break_info.breaknum == -1) {
				WALL.cstat &= ~(CSTAT_WALL_MASKED | CSTAT_WALL_1WAY | CSTAT_WALL_BLOCK_HITSCAN | CSTAT_WALL_BLOCK);
				WALL.overpicnum = 0;
				if (WALL.nextwall >= 0) {
					nwp = wall[WALL.nextwall];
					nwp.cstat &= ~(CSTAT_WALL_MASKED | CSTAT_WALL_1WAY | CSTAT_WALL_BLOCK_HITSCAN | CSTAT_WALL_BLOCK);
					nwp.overpicnum = 0;
				}
			} else {
				WALL.cstat &= ~(CSTAT_WALL_BLOCK_HITSCAN | CSTAT_WALL_BLOCK);
				WALL.overpicnum = break_info.breaknum;
				if (WALL.nextwall >= 0) {
					nwp = wall[WALL.nextwall];
					nwp.cstat &= ~(CSTAT_WALL_BLOCK_HITSCAN | CSTAT_WALL_BLOCK);
					nwp.overpicnum = break_info.breaknum;
				}
			}
		} else {
			if (break_info.breaknum == -1)
				WALL.picnum = 594; // temporary break pic
			else {
				WALL.picnum = break_info.breaknum;
				if (WALL.hitag < 0)
					DoWallBreakSpriteMatch(WALL.hitag);
			}
		}

		return (true);
	}

	public static boolean UserBreakWall(WALL wp, short ang) {
		short SpriteNum;
		SPRITE sp;
		short match = wp.hitag;
		int block_flags = CSTAT_WALL_BLOCK | CSTAT_WALL_BLOCK_HITSCAN;
		int type_flags = CSTAT_WALL_TRANSLUCENT | CSTAT_WALL_MASKED | CSTAT_WALL_1WAY;
		int flags = block_flags | type_flags;
		boolean ret = false;

		SpriteNum = FindBreakSpriteMatch(match);

		if (SpriteNum < 0) {
			// do it the old way and get rid of wall - assumed to be masked
			DoSpawnSpotsForKill(match);
			wp.cstat &= ~(flags);
			if (wp.nextwall >= 0)
				wall[wp.nextwall].cstat &= ~(flags);

			// clear tags
			wp.hitag = wp.lotag = 0;
			if (wp.nextwall >= 0)
				wall[wp.nextwall].hitag = wall[wp.nextwall].lotag = 0;
			return (true);
		}

		sp = sprite[SpriteNum];

		if (wp.picnum == SP_TAG5(sp))
			return (true);

		// make it BROKEN
		if (SP_TAG7(sp) <= 1) {
			DoSpawnSpotsForKill(match);
			DoLightingMatch(match, -1);

			if (SP_TAG8(sp) == 0) {
				wp.picnum = (short) SP_TAG5(sp);
				// clear tags
				wp.hitag = wp.lotag = 0;
				if (wp.nextwall >= 0)
					wall[wp.nextwall].hitag = wall[wp.nextwall].lotag = 0;
				ret = false;
			} else if (SP_TAG8(sp) == 1) {
				// clear flags
				wp.cstat &= ~(flags);
				if (wp.nextwall >= 0)
					wall[wp.nextwall].cstat &= ~(flags);
				// clear tags
				wp.hitag = wp.lotag = 0;
				if (wp.nextwall >= 0)
					wall[wp.nextwall].hitag = wall[wp.nextwall].lotag = 0;

				ret = true;
			} else if (SP_TAG8(sp) == 2) {
				// set to broken pic
				wp.picnum = (short) SP_TAG5(sp);

				// clear flags
				wp.cstat &= ~(block_flags);
				if (wp.nextwall >= 0)
					wall[wp.nextwall].cstat &= ~(block_flags);

				// clear tags
				wp.hitag = wp.lotag = 0;
				if (wp.nextwall >= 0)
					wall[wp.nextwall].hitag = wall[wp.nextwall].lotag = 0;

				ret = false;
			}

			return (ret);
		} else {
			// increment picnum
			wp.picnum++;

			DoSpawnSpotsForDamage(match);
		}

		return (false);
	}

	public static boolean WallBreakPosition(int hitwall, LONGp sectnum, LONGp x, LONGp y, LONGp z, LONGp ang) {
		short w = (short) hitwall;
		WALL wp = wall[w];

		short nw = wall[w].point2;
		int wall_ang = NORM_ANGLE(engine.getangle(wall[nw].x - wall[w].x, wall[nw].y - wall[w].y) + 512);

		sectnum.value = SectorOfWall(w);

		// midpoint of wall
		x.value = DIV2(wall[w].x + wall[w].x);
		y.value = DIV2(wall[w].y + wall[w].y);

		if (wp.nextwall < 0) {
			// white wall
			z.value = DIV2(sector[sectnum.value].floorz + sector[sectnum.value].ceilingz);
		} else {
			short next_sectnum = wp.nextsector;

			// floor and ceiling meet
			if (sector[next_sectnum].floorz == sector[next_sectnum].ceilingz)
				z.value = DIV2(sector[sectnum.value].floorz + sector[sectnum.value].ceilingz);
			else
			// floor is above other sector
			if (sector[next_sectnum].floorz < sector[sectnum.value].floorz)
				z.value = DIV2(sector[next_sectnum].floorz + sector[sectnum.value].floorz);
			else
			// ceiling is below other sector
			if (sector[next_sectnum].ceilingz > sector[sectnum.value].ceilingz)
				z.value = DIV2(sector[next_sectnum].ceilingz + sector[sectnum.value].ceilingz);
		}

		ang.value = wall_ang;

		int nx = MOVEx(128, wall_ang);
		int ny = MOVEy(128, wall_ang);

		x.value += nx;
		y.value += ny;

		sectnum.value = engine.updatesectorz(x.value, y.value, z.value, (short) sectnum.value);
		if (sectnum.value < 0) {
			x.value = MAXLONG; // don't spawn shrap, just change wall
			return (false);
		}

		return (true);
	}

	// If the tough parameter is not set, then it can't break tough walls and
	// sprites
	public static boolean HitBreakWall(int wp, int hitx, int hity, int hitz, int ang, int type) {
		short match = wall[wp].hitag;

		if (match > 0) {
			UserBreakWall(wall[wp], (short) ang);
			return (true);
		}

		WallBreakPosition(wp, tmp_ptr[4], tmp_ptr[0], tmp_ptr[1], tmp_ptr[2].set(hitz), tmp_ptr[3]);
		hitx = tmp_ptr[0].value;
		hity = tmp_ptr[1].value;
		hitz = tmp_ptr[2].value;
		ang = (short) tmp_ptr[3].value;

		AutoBreakWall(wall[wp], hitx, hity, hitz, (short) ang, type);
		return (true);
	}

	//
	// SPRITE
	//

	public static int KillBreakSprite(int BreakSprite) {
		SPRITE bp = sprite[BreakSprite];
		USER bu = pUser[BreakSprite];

		// Does not actually kill the sprite so it will be valid for the rest
		// of the loop traversal.

		// IMPORTANT: Do not change the statnum if possible so that NEXTI in
		// SpriteControl loop traversals will maintain integrity.

		SpriteQueueDelete(BreakSprite);

		if (bu != null) {
			if (bp.statnum == STAT_DEFAULT)
				// special case allow kill of sprites on STAT_DEFAULT list
				// a few things have users and are not StateControlled
				KillSprite(BreakSprite);
			else
				SetSuicide(BreakSprite);
		} else {
			change_sprite_stat(BreakSprite, STAT_SUICIDE);
		}

		return (0);
	}

	public static boolean UserBreakSprite(int BreakSprite) {
		SPRITE sp;
		SPRITE bp = sprite[BreakSprite];
		short match = bp.lotag;
		short match_extra;
		int SpriteNum = FindBreakSpriteMatch(match);

		if (SpriteNum < 0) {
			// even if you didn't find a matching ST1 go ahead and kill it and match
			// everything
			// its better than forcing everyone to have a ST1
			DoMatchEverything(null, match, -1);
			// Kill sound if one is attached
			DeleteNoSoundOwner(BreakSprite);
			// change_sprite_stat(BreakSprite, STAT_SUICIDE);
			KillBreakSprite(BreakSprite);
			return (true);
		}

		sp = sprite[SpriteNum];
		match_extra = (short) SP_TAG6(bp);

		if (bp.picnum == SP_TAG5(sp))
			return (true);

		// make it BROKEN
		if (SP_TAG7(sp) <= 1) {
			DoMatchEverything(null, match_extra, -1);
			// DoSpawnSpotsForKill(match_extra);
			DoLightingMatch(match_extra, OFF);

			if (SP_TAG8(sp) == 0) {
				bp.picnum = (short) SP_TAG5(sp);
				bp.extra &= ~(SPRX_BREAKABLE);
			} else
			// kill sprite
			if (SP_TAG8(sp) == 1) {
				// Kill sound if one is attached
				DeleteNoSoundOwner(BreakSprite);
				KillBreakSprite(BreakSprite);
                return (true);
			} else if (SP_TAG8(sp) == 2)
			// leave it
			{
				// set to broken pic
				bp.picnum = (short) SP_TAG5(sp);

				// reset
				if (SP_TAG8(sp) == 2) {
					bp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				}

				bp.extra &= ~(SPRX_BREAKABLE);
			}
		} else {
			// increment picnum
			bp.picnum++;

			DoSpawnSpotsForDamage(match_extra);
		}

		return (false);
	}

	public static boolean AutoBreakSprite(int BreakSprite, int type) {
		SPRITE bp = sprite[BreakSprite];
		Break_Info break_info = FindSpriteBreakInfo(bp.picnum);

		if (bp.hitag < 0)
			DoWallBreakMatch(bp.hitag);

		if (break_info == null) {
			return (false);
		}

		// Check to see if it should break with current weapon type
		if (!CheckBreakToughness(break_info, type)) {
			if (break_info.breaknum != -1) {
				if (!TEST(break_info.flags, BF_LEAVE_BREAK)) {
					bp.extra &= ~(SPRX_BREAKABLE);
					bp.cstat &= ~(CSTAT_SPRITE_BREAKABLE);
				}

				bp.picnum = break_info.breaknum;
				// pass Break Info Globally
				GlobBreakInfo = break_info;
				SpawnShrap(BreakSprite, -1);
				GlobBreakInfo = null;
				if (bp.picnum == 3683)
					bp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			}

			return (false);
		}

		bp.extra &= ~(SPRX_BREAKABLE);
		bp.cstat &= ~(CSTAT_SPRITE_BREAKABLE);

		// pass Break Info Globally
		GlobBreakInfo = break_info;
		SpawnShrap(BreakSprite, -1);
		GlobBreakInfo = null;

		// kill it or change the pic
		if (TEST(break_info.flags, BF_KILL) || break_info.breaknum == -1) {
			if (TEST(break_info.flags, BF_FIRE_FALL))
				SpawnBreakFlames(BreakSprite);

			bp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
			bp.cstat |= (CSTAT_SPRITE_INVISIBLE);
			// Kill sound if one is attached
			DeleteNoSoundOwner(BreakSprite);
			KillBreakSprite(BreakSprite);
            return (true);
		} else {
			bp.picnum = break_info.breaknum;
			if (bp.picnum == 3683)
				bp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		}

		return (false);
	}

	public static boolean NullActor(USER u) {
		// a Null Actor is defined as an actor that has no real controlling programming
		// attached

		// check to see if attached to SO
		if (TEST(u.Flags, SPR_SO_ATTACHED))
			return (true);

		// does not have a STATE or FUNC to control it
		if (u.State == null)
			return (true);

		// does not have a STATE or FUNC to control it
		if (u.ActorActionFunc == null)
			return (true);

		return (false);
	}

	public static boolean HitBreakSprite(int BreakSprite, int type) {
		SPRITE bp = sprite[BreakSprite];
		USER bu = pUser[BreakSprite];

		if (TEST_BOOL1(bp)) {
			if (TEST_BOOL2(bp))
				return (false);

			return (UserBreakSprite(BreakSprite));
		}

		if (bu != null && !NullActor(bu)) {
			// programmed animating type - without BOOL1 set
			if (bp.lotag != 0)
				DoLightingMatch(bp.lotag, -1);

			SpawnShrap(BreakSprite, -1);
			bp.extra &= ~(SPRX_BREAKABLE);
			return (false);
		}

		return (AutoBreakSprite(BreakSprite, type));
	}

	public static int SectorOfWall(short theline) {
		short i, startwall, endwall, sectnum;

		sectnum = -1;

		for (i = 0; i < numsectors; i++) {
			startwall = sector[i].wallptr;
			endwall = (short) (startwall + sector[i].wallnum - 1);
			if ((theline >= startwall) && (theline <= endwall)) {
				sectnum = i;
				break;
			}
		}

		return (sectnum);
	}

	public static void DoWallBreakMatch(short match) {
		short i;
		int x, y, z;
		short wall_ang;

		for (i = 0; i <= numwalls; i++) {
			if (wall[i] != null && wall[i].hitag == match) {
				WallBreakPosition(i, tmp_ptr[4], tmp_ptr[0], tmp_ptr[1], tmp_ptr[2].set(0), tmp_ptr[3]);
				x = tmp_ptr[0].value;
				y = tmp_ptr[1].value;
				z = tmp_ptr[2].value;
				wall_ang = (short) tmp_ptr[3].value;

				wall[i].hitag = 0; // Reset the hitag
				AutoBreakWall(wall[i], x, y, z, wall_ang, 0);
			}
		}
	}

	public static void DoWallBreakSpriteMatch(short match) {
		short i, nexti;

		for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			SPRITE sp = sprite[i];

			if (sp.hitag == match) {
				KillSprite(i);
			}
		}
	}

}
