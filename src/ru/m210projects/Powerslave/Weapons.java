// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Grenade.*;
import static ru.m210projects.Powerslave.Snake.*;
import static ru.m210projects.Powerslave.View.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Sound.*;

import java.util.Arrays;

import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Light.*;

import ru.m210projects.Powerslave.Type.PlayerStruct;
import ru.m210projects.Powerslave.Type.WeaponInfo;

public class Weapons {

	public static int ammodelay;
	public static final short[] nMinAmmo = { 0, 24, 51, 50, 1, 0, 0 };

	public static final WeaponInfo[] weaponInfo = new WeaponInfo[] {
			new WeaponInfo(1, new int[] { 0, 1, 3, 7, -1, 2, 4, 5, 6, 8, 9, 10 }, 0, 0, 0,
					new int[] { 1, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(2, new int[] { 0, 3, 2, 4, -1, 1, 0, 0, 0, 0, 0, 0 }, 1, 0, 1,
					new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(3, new int[] { 0, 5, 6, 16, -1, 21, 0, 0, 0, 0, 0, 0 }, 2, 0, 1,
					new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(4, new int[] { 0, 2, 5, 5, 6, 1, 0, 0, 0, 0, 0, 0 }, 3, 4, 1,
					new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(5, new int[] { 0, 2, 3, 4, -1, 1, 0, 0, 0, 0, 0, 0 }, 4, 0, 1,
					new int[] { 1, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(6, new int[] { 0, 1, 2, 2, -1, 4, 0, 0, 0, 0, 0, 0 }, 5, 0, 1,
					new int[] { 1, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(67, new int[] { 0, 1, 2, 3, -1, 4, 0, 0, 0, 0, 0, 0 }, 6, 0, 1,
					new int[] { 1, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(0, new int[] { 0, 1, 2, -1, -1, -1, 0, 0, 0, 0, 0, 0 }, 7, 0, 0,
					new int[] { 1, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(27, new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0, 1, 0,
					new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(28, new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0, 1, 0,
					new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }),
			new WeaponInfo(74, new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0, 1, 0,
					new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }), };

	public static void CheckClip(int num) {
		nPlayerClip[num] = (short) BClipRange(PlayerList[num].AmmosAmount[2], 0, 99);
	}

	public static int bobangle;

	public static void FireWeapon(int nPlayer) {
		if (PlayerList[nPlayer].weaponFire == 0)
			PlayerList[nPlayer].weaponFire = 1;
	}

	public static void StopFiringWeapon(int nPlayer) {
		PlayerList[nPlayer].weaponFire = 0;
	}

	private static int dword_96E22;

	public static void MoveWeapons(int nPlayer) {
		int nFlags = SectFlag[nPlayerViewSect[nPlayer]] & 0x2000;
		word_96E26 = !word_96E26;
		
		if (nFlags == 0 || (totalmoves & 1) == 0) {
			if (++nPilotLightFrame >= nPilotLightCount)
				nPilotLightFrame = 0;
			if (PlayerList[nPlayer].weaponFire == 0 || nFlags != 0) {
				nTemperature[nPlayer] = 0;
			}

			int currentWeapon = PlayerList[nPlayer].currentWeapon;
			int nSprite = PlayerList[nPlayer].spriteId;
			if (currentWeapon <= -1) {
				if (PlayerList[nPlayer].newWeapon != -1) {
					PlayerList[nPlayer].currentWeapon = PlayerList[nPlayer].newWeapon;
					PlayerList[nPlayer].weaponState = 0;
					PlayerList[nPlayer].seqOffset = 0;
					PlayerList[nPlayer].newWeapon = -1;
				}
				return;
			}

			int nSeq = weaponInfo[currentWeapon].field_2[PlayerList[nPlayer].weaponState]
					+ SeqOffsets[weaponInfo[currentWeapon].seq];
			int v10 = nPlayerDouble[nPlayer] > 0 ? 1 : 0;
			int v79 = v10 + 1;
			
			if(currentWeapon == 4 && nSeq == 64) //double bonus grenade fix
				v10 = 0;
	
			for (frames = v10;; --frames) {
				MoveSequence(nSprite, nSeq, PlayerList[nPlayer].seqOffset);
				if (++dword_96E22 >= 15)
					dword_96E22 = 0;

				if (++PlayerList[nPlayer].seqOffset >= SeqSize[nSeq]) {
					if (PlayerList[nPlayer].newWeapon != -1) {
						if (PlayerList[nPlayer].weaponState == 5) {
							int v15 = PlayerList[nPlayer].newWeapon;
							PlayerList[nPlayer].currentWeapon = (short) v15;
							PlayerList[nPlayer].newWeapon = -1;
							PlayerList[nPlayer].weaponState = 0;
						} else {
							PlayerList[nPlayer].weaponState = 5;
						}
						PlayerList[nPlayer].seqOffset = 0;
						if (frames == 0)
							return;
					}

					switch (PlayerList[nPlayer].weaponState) {
					case 0:
						PlayerList[nPlayer].weaponState = 1;
						SetWeaponStatus(nPlayer);
						break;
					case 1:
						if (PlayerList[nPlayer].weaponFire == 0)
							break;
						if (WeaponCanFire(nPlayer)) {
							if (currentWeapon == 6) {
								if (Ra[nPlayer].nTarget == -1)
									break;
								Ra[nPlayer].nState = 0;
								Ra[nPlayer].nSeq = 0;
								Ra[nPlayer].field_C = 1;
							}
							PlayerList[nPlayer].weaponState = 2;
							if (currentWeapon != 0) {
								if (currentWeapon == 4) {
									BuildGrenade(nPlayer);
									AddAmmo(nPlayer, 4, -1);
								} else if (currentWeapon == 7) {
									ShootStaff(nPlayer);
								}
							}
						} else if (dword_96E22 == 0) {
							D3PlayFX(StaticSound[4], PlayerList[nPlayer].spriteId);
						}
						break;
					case 2:
					case 6:
					case 7:
					case 8:
						if (currentWeapon == 1) {
							if (nPistolClip[nPlayer] <= 0) {
								PlayerList[nPlayer].weaponState = 3;
								PlayerList[nPlayer].seqOffset = 0;
								nPistolClip[nPlayer] = (short) Math.min(6, PlayerList[nPlayer].AmmosAmount[1]);
								break;
							}
						}
						if (currentWeapon == 4) {
							if (PlayerList[nPlayer].weaponFire != 0) {
								PlayerList[nPlayer].seqOffset = (short) (SeqSize[nSeq] - 1);
								if (frames == 0)
									return;
							}
							PlayerList[nPlayer].weaponState = 3;
							break;
						}
						if (currentWeapon == 7) {
							PlayerList[nPlayer].weaponState = 0;
							PlayerList[nPlayer].currentWeapon = PlayerList[nPlayer].lastWeapon;
							SetPlayerMummified(nPlayer, 0);
							break;
						}
						if (PlayerList[nPlayer].weaponFire != 0 && WeaponCanFire(nPlayer)) {
							if (currentWeapon != 2 && currentWeapon != 1)
								PlayerList[nPlayer].weaponState = 3;
							break;
						}
						if (weaponInfo[currentWeapon].field_2[4] != -1 && (currentWeapon != 3 || nFlags == 0)) {
							PlayerList[nPlayer].weaponState = 4;
							break;
						}

						PlayerList[nPlayer].weaponState = 1;
						break;
					case 3:
					case 9:
					case 0xA:
					case 0xB:
						switch (currentWeapon) {
						case 7:
							PlayerList[nPlayer].currentWeapon = PlayerList[nPlayer].lastWeapon;
							PlayerList[nPlayer].weaponState = 0;
							break;
						case 2:
							CheckClip(nPlayer);
							PlayerList[nPlayer].weaponState = 1;
							break;
						case 4:
							if (weaponInfo[currentWeapon].field_1E != 0
									&& PlayerList[nPlayer].AmmosAmount[weaponInfo[currentWeapon].field_1A] == 0) {
								SelectNewWeapon(nPlayer);
								PlayerList[nPlayer].weaponState = 5;
								PlayerList[nPlayer].seqOffset = (short) (SeqSize[SeqOffsets[weaponInfo[currentWeapon].seq]
										+ weaponInfo[4].field_2[5]] - 1);
								break;
							}
							PlayerList[nPlayer].weaponState = 0;
							break;
						case 0:
						case 1:
						case 3:
						case 5:
							if (PlayerList[nPlayer].weaponFire != 0 && WeaponCanFire(nPlayer))
								PlayerList[nPlayer].weaponState = 2;
							else if (weaponInfo[currentWeapon].field_2[4] == -1 || currentWeapon == 3 && nFlags != 0)
								PlayerList[nPlayer].weaponState = 1;
							else
								PlayerList[nPlayer].weaponState = 4;
							break;
						case 6:
							if (weaponInfo[currentWeapon].field_1E == 0
									|| PlayerList[nPlayer].AmmosAmount[weaponInfo[currentWeapon].field_1A] != 0) {
								if (PlayerList[nPlayer].weaponFire != 0)
									break;
								PlayerList[nPlayer].weaponState = 1;
							} else
								SelectNewWeapon(nPlayer);
							Ra[nPlayer].field_C = 0;
							break;
						}

						break;
					case 4:
						PlayerList[nPlayer].weaponState = 1;
						break;
					case 5:
						PlayerList[nPlayer].currentWeapon = PlayerList[nPlayer].newWeapon;
						PlayerList[nPlayer].weaponState = 0;
						PlayerList[nPlayer].newWeapon = -1;
						SetWeaponStatus(nPlayer);
						break;
					default:
						break;
					}

					nSeq = weaponInfo[currentWeapon].field_2[PlayerList[nPlayer].weaponState]
							+ SeqOffsets[weaponInfo[currentWeapon].seq];
					PlayerList[nPlayer].seqOffset = 0;
				}

				int v75 = GetFrameFlag(nSeq, PlayerList[nPlayer].seqOffset);
				if ((nFlags == 0 || currentWeapon == 6) && (v75 & 4) != 0) {
					BuildFlash(nPlayer, sprite[nSprite].sectnum, 512);
					AddFlash(sprite[nSprite].sectnum, sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, 0);
				}

				if ((v75 & 0x80) != 0) {
					int v69 = PlayerList[nPlayer].anim_;
					boolean v44 = v69 >= 10 && v69 <= 12;

					if (nPlayer == nLocalPlayer)
						bobangle = 512;
					if (currentWeapon == 3 && nFlags == 0) {
						if (++nTemperature[nPlayer] > 50) {
							nTemperature[nPlayer] = 0;
							PlayerList[nPlayer].weaponState = 4;
							PlayerList[nPlayer].seqOffset = 0;
						}
					}
					int v47 = nSprite;

					int nBulletType = weaponInfo[currentWeapon].field_1A;
					int v77 = sprite[v47].ang;
					int x = sprite[v47].x;
					int y = sprite[v47].y;
					int z = sprite[v47].z;
					int v49 = 8 * sprite[v47].clipdist;
					int wx = (v49 * sintable[(v77 + 512) & 0x7FF]) & 0x7FF;
					int v51 = sintable[v77];
					int wy = v51 * v49;
					int v53 = weaponInfo[currentWeapon].field_1C;
					if (v53 != 0) {
						v51 = totalmoves;
						int v55;
						int v54 = (v53 - 1) & (v51 + 101);
						if ((v54 & 1) != 0)
							v55 = -v54;
						else
							v55 = v54;
						int v56 = (v77 + 512) & 0x7FF;

						wx += v55 * (sintable[(v56 + 512) & 0x7FF] >> 11);
						wy += v55 * (sintable[v56] >> 11);
					}
					int h = -GetSpriteHeight(nSprite) >> 1;
					int wz;

					if (v69 >= 6) {
						if (v44)
							wz = h - 2560;
						else
							wz = h + 1024;
					} else {
						wz = h - 1792;
					}

					int sectnum = sprite[nSprite].sectnum;

					switch (currentWeapon) {
					case 0:
						int v61 = PlayerList[nPlayer].weaponState;
						int v99;

						if(isOriginal())
							z += ((92 - sPlayerInput[nPlayer].nWeaponAim) << 6) + wz;
						else {
							z += wz;
							if(v69 <= 6)
								z -= 3100;
						}

						if (v61 == 2)
							v99 = 6;
						else
							v99 = 9;
						int v62 = CheckCloseRange(nPlayer, x, y, z, isOriginal() ? 0 : (2048 * (92 - sPlayerInput[nPlayer].nWeaponAim)), sectnum);

						x = range_x;
						y = range_y;
						z = range_z;
						sectnum = range_sect;

						if (v62 != 0) {
							int damage = BulletInfo[0].force;
							if (nPlayerDouble[nPlayer] != 0)
								damage = 2 * BulletInfo[0].force;
							switch (v62 & 0xC000) {
							case 32768:
								v99 += 2;
								break;
							case 49152:
								int nObject = v62 & 0x3FFF;
								if ((sprite[nObject].cstat & 0x50) != 0) {
									v99 += 2;
									break;
								}
								int statnum = sprite[nObject].statnum;
								if (statnum > 90 && statnum <= 199) {
									if(sprite[nObject].picnum == 2060) //drum
										break;
									
									DamageEnemy(nObject, nSprite, damage);
								}

								if (statnum <= 90 || statnum > 199 || statnum == 102) {
									BuildAnim(-1, 12, 0, x, y, z, sectnum, 0x1E, 0);
								} else if (statnum == 141)
									v99 += 2;
								else
									v99++;
								break;
							}
						}

						PlayerList[nPlayer].weaponState = (short) v99;
						PlayerList[nPlayer].seqOffset = 0;
						break;
					case 3:
						if (nFlags != 0) {
							DoBubbles(nPlayer);
							PlayerList[nPlayer].weaponState = 1;
							PlayerList[nPlayer].seqOffset = 0;
							StopSpriteSound(nSprite);
							break;
						}
						if (v44)
							wz += 768;
						else
							wz -= 2560;
					case 2:
						if (currentWeapon != 3)
							nQuake[nPlayer] = 128;
					case 1:
						int horiz = sPlayerInput[nPlayer].nWeaponAim - 92;
						
						if(!isOriginal()) {
							horiz += horiz / 16;
							if(currentWeapon != 3)
								wz -= 5192;
						}
						
						int zAngle = 4 * horiz;
						int v81 = wz - zAngle;
				
						if ((game.isCurrentScreen(gDemoScreen) || cfg.gAutoAim) && sPlayerInput[nPlayer].nTarget >= 0)
							zAngle = sPlayerInput[nPlayer].nTarget + 10000;

						BuildBullet(nSprite, nBulletType, wx, wy, v81, v77, zAngle, v79);
						break;
					case 4:
						ThrowGrenade(nPlayer, wx, wy, wz - 2560, sPlayerInput[nPlayer].nWeaponAim - 92);
						break;
					case 5:
						BuildSnake(nPlayer, wz);
						nQuake[nPlayer] = 512;
						nXDamage[nPlayer] = nXDamage[nPlayer] - (sintable[(sprite[nSprite].ang + 512) & 0x7FF] << 9);
						nYDamage[nPlayer] = nYDamage[nPlayer] - (sintable[sprite[nSprite].ang & 0x7FF] << 9);
						break;
					case 7:
						int damage = 300;
						if (nPlayerDouble[nPlayer] != 0)
							damage *= 2;
						RadialDamageEnemy(nSprite, damage, 50);
						break;
					default:
						break;
					}

					if (currentWeapon < 7) {
						if (currentWeapon != 4) {
							int v61 = currentWeapon;
							v61 = weaponInfo[v61].field_1E;
							int v62 = -v61;
							if (v62 != 0) {
								AddAmmo(nPlayer, nBulletType, v62);
							}
							if (currentWeapon == 2) {
								nPlayerClip[nPlayer] -= weaponInfo[2].field_1E;
							} else if (currentWeapon == 1) {
								--nPistolClip[nPlayer];
							}
						}
						int v63 = currentWeapon;
						if (weaponInfo[v63].field_1E == 0
								|| PlayerList[nPlayer].AmmosAmount[weaponInfo[v63].field_1A] != 0) {
							if (currentWeapon == 2 && nPlayerClip[nPlayer] <= 0) {
								PlayerList[nPlayer].weaponState = 3;
								PlayerList[nPlayer].seqOffset = (short) 0;
								if (frames == 0)
									return;
							}
						} else if (currentWeapon != 4) {
							SelectNewWeapon(nPlayer);
						}
					}
				}
				if (frames == 0)
					return;
			}
		}
	}

	public static boolean WeaponCanFire(int a1) {
		if ((SectFlag[nPlayerViewSect[a1]] & 0x2000) == 0 || weaponInfo[PlayerList[a1].currentWeapon].field_20[0] != 0)
			return weaponInfo[PlayerList[a1].currentWeapon].field_1E <= PlayerList[a1].AmmosAmount[weaponInfo[PlayerList[a1].currentWeapon].field_1A];

		return false;
	}

	public static void SelectNewWeapon(int a1) {
		int v1 = a1;
		int v2 = 6;
		int v3 = 64;
		int v4 = nPlayerWeapons[a1];
		while (v3 != 0) {
			if ((v3 & v4) != 0) {
				if (weaponInfo[v2].field_1E == 0 || PlayerList[v1].AmmosAmount[weaponInfo[v2].field_1A] != 0) {
					break;
				}
			}
			v3 >>= 1;
			--v2;
		}
		if (v2 < 0)
			v2 = 0;
		PlayerList[v1].weaponFire = 0;
		SetNewWeapon(v1, v2);
		return;
	}

	public static void ResetPlayerWeapons(int nPlayer) {
		for (int i = 0; i < 7; i++)
			PlayerList[nPlayer].AmmosAmount[i] = 0;

		PlayerList[nPlayer].currentWeapon = 0;
		PlayerList[nPlayer].weaponState = 0;
		PlayerList[nPlayer].seqOffset = 0;
		nPlayerGrenade[nPlayer] = -1;
		nPlayerWeapons[nPlayer] = 1;
	}

	public static void SetNewWeapon(int nPlayer, int nWeapon) {
		if (nWeapon == 7) {
			PlayerList[nPlayer].lastWeapon = PlayerList[nPlayer].currentWeapon;
			PlayerList[nPlayer].weaponFire = 0;
			PlayerList[nPlayer].weaponState = 5;
			SetPlayerMummified(nPlayer, 1);
		} else {
			if (nWeapon < 0) {
				nPlayerOldWeapon[nPlayer] = PlayerList[nPlayer].currentWeapon;
			} else if (nWeapon == 4) {
				if (PlayerList[nPlayer].AmmosAmount[4] <= 0)
					return;
			}

			int currWeapon = PlayerList[nPlayer].currentWeapon;
			if (currWeapon != 7) {
				if (PlayerList[nPlayer].weaponFire != 0 || nWeapon == currWeapon)
					return;
			} else {
				PlayerList[nPlayer].currentWeapon = (short) nWeapon;
				PlayerList[nPlayer].seqOffset = 0;
			}
		}
		PlayerList[nPlayer].lastUsedWeapon = PlayerList[nPlayer].currentWeapon;
		PlayerList[nPlayer].newWeapon = (short) nWeapon;
		if (nPlayer == nLocalPlayer) {
			int ammo = 0;
			if (nWeapon >= 0 && nWeapon <= 6)
				ammo = PlayerList[nPlayer].AmmosAmount[nWeapon];
			SetCounterImmediate(ammo);
		}
	}
	
	public static int WeaponChange(int nPlayer, boolean next) {
		PlayerStruct pPlayer = PlayerList[nPlayer];
		int weap = pPlayer.currentWeapon;
		do {
			if (next) {
				if(++weap > 7) weap = 0;
			}
			else {
				if(--weap < 0) weap = 7;
			}

			if((SectFlag[nPlayerViewSect[nPlayer]] & 0x2000) != 0 && weap > 0 && weap < 4) 
				continue;

			if (((1 << weap) & nPlayerWeapons[nPlayer]) != 0)
		    {
		    	if ( weap == 0 || PlayerList[nPlayer].AmmosAmount[weap] > 0 )
		    		break;
		    }
		} while (pPlayer.currentWeapon != weap);

		return weap;
	}

	public static void SetNewWeaponIfBetter(int result, int a2) {
		if (a2 > PlayerList[result].currentWeapon)
			SetNewWeapon(result, a2);
	}

	public static void SetNewWeaponImmediate(int a1, int a2) {
		SetNewWeapon(a1, a2);
		PlayerList[a1].currentWeapon = (short) a2;
		PlayerList[a1].newWeapon = -1;
		PlayerList[a1].seqOffset = 0;
		PlayerList[a1].weaponState = 0;
	}

	public static void FillWeapons(int a1) {
		nPlayerWeapons[a1] = -1;
		StatusMessage(750, "All weapons loaded for players " + nLocalPlayer, 0);
		for (int i = 0; i < 7; ++i) {
			if (weaponInfo[i].field_1E != 0)
				PlayerList[a1].AmmosAmount[i] = 300;
		}

		CheckClip(a1);
		if (a1 == nLocalPlayer)
			SetCounter(PlayerList[a1].AmmosAmount[PlayerList[a1].currentWeapon]);
	}

	public static void RestoreMinAmmo(int num) {
		for (int i = 0; i < 7; i++) {
			if (i != 4 && (nPlayerWeapons[num] & (1 << i)) != 0) {
				if (nMinAmmo[i] > PlayerList[num].AmmosAmount[i])
					PlayerList[num].AmmosAmount[i] = nMinAmmo[i];
			}
		}
		CheckClip(num);
	}

	public static void InitWeapons() {
		Arrays.fill(nPlayerGrenade, (short) 0);
		Arrays.fill(nGrenadePlayer, (short) 0);
	}

	public static void SetWeaponStatus(int nPlayer) {
		if (nPlayer == nLocalPlayer) {
			if (PlayerList[nPlayer].currentWeapon < 0) {
				nCounterBullet = -1;
				SetCounterImmediate(0);
			} else {
				nCounterBullet = weaponInfo[PlayerList[nPlayer].currentWeapon].field_1A;
				SetCounterImmediate(PlayerList[nPlayer].AmmosAmount[nCounterBullet]);
			}
		}
	}

	public static int range_x, range_y, range_z, range_sect;

	public static int CheckCloseRange(int nPlayer, int x, int y, int z, int horiz, int sectnum) {
		range_x = x;
		range_y = y;
		range_z = z;
		range_sect = sectnum;
		int ang = sprite[PlayerList[nPlayer].spriteId].ang & 0x7FF;

		engine.hitscan(x, y, z, (short) sectnum, sintable[(ang + 512) & 0x7FF], sintable[ang], horiz, pHitInfo, CLIPMASK1);

		if (engine.ksqrt((pHitInfo.hitx - x) * (pHitInfo.hitx - x)
				+ (pHitInfo.hity - y) * (pHitInfo.hity - y)) >= sintable[150] >> 3)
			return 0;

		range_x = pHitInfo.hitx;
		range_y = pHitInfo.hity;
		range_z = pHitInfo.hitz;
		range_sect = pHitInfo.hitsect;

		if (pHitInfo.hitsprite > -1)
			return pHitInfo.hitsprite | 0xC000;

		if (pHitInfo.hitwall > -1)
			return pHitInfo.hitwall | 0x8000;

		return 0;
	}

	private static boolean word_96E26;

	public static void DrawWeapons() {
		if (bCamera)
			return;

		int currentWeapon = PlayerList[nLocalPlayer].currentWeapon;
		if (currentWeapon <= -1)
			return;

		int weapState = PlayerList[nLocalPlayer].weaponState;
		int seqBase = SeqOffsets[weaponInfo[currentWeapon].seq];
		short shade = sector[initsect].ceilingshade;
		int offset = weaponInfo[currentWeapon].field_2[weapState] + seqBase;
		int pal = 0;
		if (nPlayerDouble[nLocalPlayer] != 0) {
			if (word_96E26)
				pal = 5;
		}

		int vel = totalvel[nLocalPlayer] >> 1;
		int yoffs = vel * (sintable[bobangle & 0x3FF] >> 8) >> 9;
		int xoffs = 0;
		if (weapState == 1) {
			xoffs = (sintable[(bobangle + 512) & 0x7FF] >> 8) * vel >> 8;
		} else
			bobangle = 512;

		if (currentWeapon == 3 && weapState == 1)
			DrawPilotLightSeq(xoffs, yoffs);

		if (currentWeapon < 0)
			shade = sprite[PlayerList[nLocalPlayer].spriteId].shade;
		
		int stat = 0;
		if(currentWeapon == 8 || currentWeapon == 9)
			stat = 512;
		DrawGunSequence(offset, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, stat);

		if (currentWeapon != 2)
			return;

		switch (weapState) {
		case 0:
			if (nPlayerClip[nLocalPlayer] <= 0)
				return;

			if (nPlayerClip[nLocalPlayer] > 3) {
				if (nPlayerClip[nLocalPlayer] > 6) {
					if (nPlayerClip[nLocalPlayer] > 25)
						DrawGunSequence(seqBase + 4, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
					else
						DrawGunSequence(seqBase + 3, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
				} else
					DrawGunSequence(seqBase + 2, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
			} else
				DrawGunSequence(seqBase + 1, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
			return;
		case 1:
			int v15 = 4 * (nPlayerClip[nLocalPlayer] % 3);
			if (nPlayerClip[nLocalPlayer] > 0) {
				DrawGunSequence(seqBase + 8, v15, xoffs, yoffs, shade, pal, 0);
				if (nPlayerClip[nLocalPlayer] > 3) {
					DrawGunSequence(seqBase + 9, v15, xoffs, yoffs, shade, pal, 0);
					if (nPlayerClip[nLocalPlayer] > 6) {
						DrawGunSequence(seqBase + 10, v15, xoffs, yoffs, shade, pal, 0);
						if (nPlayerClip[nLocalPlayer] > 25)
							DrawGunSequence(seqBase + 11, v15, xoffs, yoffs, shade, pal, 0);
					}
				}
			}
			return;
		case 2:
			if (nPlayerClip[nLocalPlayer] > 0) {
				DrawGunSequence(seqBase + 8, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
				if (nPlayerClip[nLocalPlayer] > 3) {
					DrawGunSequence(seqBase + 9, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
					if (nPlayerClip[nLocalPlayer] > 6) {
						DrawGunSequence(seqBase + 10, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
						if (nPlayerClip[nLocalPlayer] > 25)
							DrawGunSequence(seqBase + 11, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
					}
				}
			}
			return;
		case 5:
			if (nPlayerClip[nLocalPlayer] <= 0)
				return;

			if (nPlayerClip[nLocalPlayer] <= 3) {
				DrawGunSequence(seqBase + 20, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
				return;
			}
			if (nPlayerClip[nLocalPlayer] <= 6) {
				DrawGunSequence(seqBase + 19, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
				return;
			}
			if (nPlayerClip[nLocalPlayer] > 25) {
				DrawGunSequence(seqBase + 17, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
				return;
			}
			DrawGunSequence(seqBase + 18, PlayerList[nLocalPlayer].seqOffset, xoffs, yoffs, shade, pal, 0);
			return;

		default:
			return;
		}
	}

	public static boolean AddAmmo(int player, int weapon, int amount) {
		if (amount == 0)
			amount = 1;

		if (PlayerList[player].AmmosAmount[weapon] < 300 || amount <= 0) {
			PlayerList[player].AmmosAmount[weapon] = (short) BClipHigh(PlayerList[player].AmmosAmount[weapon] + amount,
					300);
			if (player == nLocalPlayer && weapon == nCounterBullet)
				SetCounter(PlayerList[player].AmmosAmount[weapon]);

			if (weapon == 1) {
				if (nPistolClip[player] == 0)
					nPistolClip[player] = 6;
			}

			return true;
		}
		return false;
	}

	public static void ShootStaff(int player) {
		PlayerList[player].anim_ = 15;
		PlayerList[player].animCount = 0;
		PlayerList[player].seq = 25;
	}

	public static short GrabBodyGunSprite() {
		short spr = nBodyGunSprite[nCurBodyGunNum];
		if (spr == -1) {

			spr = engine.insertsprite((short) 0, (short) 899);
			nBodyGunSprite[nCurBodyGunNum] = spr;
			sprite[spr].lotag = -1;
			sprite[spr].owner = -1;
		} else {
			if (sprite[spr].owner != -1)
				DestroyAnim(sprite[spr].owner);
			sprite[spr].owner = -1;
			sprite[spr].lotag = -1;
		}
		if (++nCurBodyGunNum >= 50)
			nCurBodyGunNum = 0;
		sprite[spr].cstat = 0;
		return spr;
	}

}
