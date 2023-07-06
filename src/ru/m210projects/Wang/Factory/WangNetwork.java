package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.MAXPAKSIZ;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Net.Mmulti.sendpacket;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Wang.Digi.DIGI_PMESSAGE;
import static ru.m210projects.Wang.Factory.WangMenuHandler.NETWORKGAME;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.QuitFlag;
import static ru.m210projects.Wang.Game.pNetInfo;
import static ru.m210projects.Wang.Game.rec;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_AUTO_AIM;
import static ru.m210projects.Wang.Gameutils.PF_LOCK_RUN;
import static ru.m210projects.Wang.Gameutils.SK_AUTO_AIM;
import static ru.m210projects.Wang.Gameutils.SK_CENTER_VIEW;
import static ru.m210projects.Wang.Gameutils.SK_HIDE_WEAPON;
import static ru.m210projects.Wang.Gameutils.SK_INV_HOTKEY_MASK;
import static ru.m210projects.Wang.Gameutils.SK_INV_LEFT;
import static ru.m210projects.Wang.Gameutils.SK_INV_RIGHT;
import static ru.m210projects.Wang.Gameutils.SK_INV_USE;
import static ru.m210projects.Wang.Gameutils.SK_OPERATE;
import static ru.m210projects.Wang.Gameutils.SK_PAUSE;
import static ru.m210projects.Wang.Gameutils.SK_QUIT_GAME;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.SK_WEAPON_MASK;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JPlayer.adduserquote;
import static ru.m210projects.Wang.JPlayer.computergetinput;
import static ru.m210projects.Wang.JWeapon.InitBloodSpray;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.STAT_MISSILE;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Palette.FORCERESET;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER0;
import static ru.m210projects.Wang.Palette.ResetPalette;
import static ru.m210projects.Wang.Player.DoPlayerSectorUpdatePostMove;
import static ru.m210projects.Wang.Player.DoPlayerSectorUpdatePreMove;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlaySoundRTS;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.ResourceHandler.GetEpisode;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.DataResource;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Menus.Network.MenuNetwork;
import ru.m210projects.Wang.Type.GameInfo;
import ru.m210projects.Wang.Type.Input;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.Predict;
import ru.m210projects.Wang.Type.USER;

public class WangNetwork extends BuildNet {

	public PlayerStr ppp = new PlayerStr();
	private USER PredictUser = new USER();
	private SPRITE PredictSprite = new SPRITE() {
		@Override
		public String toString() {
			String out = "Prediction sprite \r\n";
			out += super.toString();
			return out;
		}
	};
	
	public Predict[] predictFifo = new Predict[kNetFifoSize];
	public static boolean PredictionOn = true;
	public static boolean Prediction = false;
	private static boolean SavePrediction;
	
	public static int PlayerSyncTrail = -1;
	public static int PlayerSyncIndex = -1;
	public static PlayerStr PlayerSync = null;
	public static SPRITE PlayerSpriteSync = null;

	public static final short TimeLimitTable[] = { 0, 3, 5, 10, 15, 20, 30, 45, 60 };

	public enum MultiGameTypes {
		MULTI_GAME_NONE, MULTI_GAME_COMMBAT, MULTI_GAME_COMMBAT_NO_RESPAWN, // JUST a place holder for menus. DO NOT
																			// USE!!!
		MULTI_GAME_COOPERATIVE, MULTI_GAME_AI_BOTS
	};

	public static byte[] netbuf = new byte[MAXPAKSIZ];
	public final static short nNetVersion = 500;
	public static int CommPlayers = 0;

	private Main app;
	public int MoveThingsCount;

	public boolean FakeMultiplayer;
	public int FakeMultiNumPlayers;
	public boolean BotMode = false;
	public int BotSkill = -1;

	public int KillLimit;
	public int TimeLimit;
	public int TimeLimitClock;
	public MultiGameTypes MultiGameType = MultiGameTypes.MULTI_GAME_NONE; // used to be a stand alone global
	public boolean TeamPlay;
	public boolean HurtTeammate;
	public boolean SpawnMarkers;
	public boolean NoRespawn; // for commbat type games
	public boolean Nuke = true;

	private final static int TypeOffset = 8;

	public enum PacketType {
		LevelStart {
			@Override
			public int Get(int fromPlayer, byte[] buf, int len) {
				gNet.retransmit(fromPlayer, buf, len);
				ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
				bb.get(); // type
				int nCheckVersion = bb.getShort();

				pNetInfo.set(bb);

				if (nCheckVersion != nNetVersion) {
					Main.game.GameMessage("These versions of Shadow Warrior cannot play together.");
					gNet.NetDisconnect(myconnectindex);
					return -1;
				}

				if (gNet.WaitForAllPlayers(0))
					gGameScreen.newgame(true, ((MenuNetwork) Main.game.menu.mMenus[NETWORKGAME]).getFile(),
							pNetInfo.nEpisode, pNetInfo.nLevel, pNetInfo.nDifficulty);

				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
				bb.put((byte) (ordinal() + TypeOffset));
				bb.putShort(nNetVersion);
				bb.put(pNetInfo.getBytes());

				return bb.position();
			}
		},
		Profile {
			@Override
			public int Get(int fromPlayer, byte[] buf, int len) {
				gNet.retransmit(fromPlayer, buf, len);
				int nP = buf[1];
				boolean AutoRun = buf[2] == 1;
				byte NetColor = buf[3];
				boolean AutoAim = buf[4] == 1;

				len = 0;
				for (int i = 5; buf[i] != 0; i++, len++);
				PlayerStr pp = Player[nP];

				if (AutoRun)
					pp.Flags |= (PF_LOCK_RUN);
				else
					pp.Flags &= ~(PF_LOCK_RUN);

				if (AutoAim)
					pp.Flags |= (PF_AUTO_AIM);
				else
					pp.Flags &= ~(PF_AUTO_AIM);

				pp.PlayerName = new String(buf, 5, len);
				pp.TeamColor = NetColor;

				if (pp.PlayerSprite != -1 && pUser[pp.PlayerSprite] != null) { // if in the game
					sprite[pp.PlayerSprite].pal = (short) (PALETTE_PLAYER0 + pp.TeamColor);
					pUser[pp.PlayerSprite].spal = (byte) sprite[pp.PlayerSprite].pal;
				}
				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				buf[0] = (byte) (ordinal() + TypeOffset);
				buf[1] = (byte) myconnectindex;
				buf[2] = gs.AutoRun ? (byte) 1 : 0;
				buf[3] = gs.NetColor;
				buf[4] = gs.AutoAim ? (byte) 1 : 0;
				int l = 5;

				char[] name = toCharArray(gs.pName);
				for (int i = 0; i < gs.pName.length() && name[i] != 0; i++)
					buf[l++] = (byte) name[i];
				buf[l++] = 0;
				return l;
			}
		},
		Message {
			private int sendmessagecommand;
			private String message;

			public void setData(Object... opts) {
				if (opts.length < 2)
					return;
				sendmessagecommand = (int) opts[0];
				message = (String) opts[1];
			}

			@Override
			public int Get(int fromMaster, byte[] buf, int len) {
				byte fromPlayer = buf[1];
				byte nPlayer = buf[2];
				gNet.retransmit(fromMaster, buf, len);
				if(nPlayer == -1 || nPlayer == myconnectindex) {
					if(nPlayer != -1)
						adduserquote(Player[fromPlayer].getName() + ": " + new String(buf, 3, len - 3));
					else adduserquote(new String(buf, 3, len - 3));
					PlaySound(DIGI_PMESSAGE, null, v3df_dontpan);
				}
				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
				bb.put((byte) (ordinal() + TypeOffset));
				bb.put((byte) myconnectindex);
				bb.put((byte) sendmessagecommand);
				bb.put(message.getBytes());
				bb.put((byte) 0);
				return bb.position();
			}
		},
		RTS_Sound {
			int num = 0;

			public void setData(Object... opts) {
				this.num = (int) opts[0];
			}

			@Override
			public int Get(int fromPlayer, byte[] buf, int len) {
				gNet.retransmit(fromPlayer, buf, len);
				PlaySoundRTS(buf[1]);
				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				PlaySoundRTS(num);

				buf[0] = (byte) (ordinal() + TypeOffset);
				buf[1] = (byte) num;
				return 2;
			}
		},
		PlayerData { // Attention! Mmulti.java MAXPAKSIZ should be 2048 to make it works
			int num = 0;

			public void setData(Object... opts) {
				this.num = (int) opts[0];
			}

			@Override
			public int Get(int fromPlayer, byte[] buf, int len) {
				gNet.retransmit(fromPlayer, buf, len);
				DataResource res = new DataResource(buf);
				res.readByte(); // pack
				PlayerSyncTrail = res.readInt();
				PlayerSyncIndex = res.readInt();
                PlayerSpriteSync = new SPRITE();
				PlayerSpriteSync.buildSprite(res);
				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
				bb.put((byte) (ordinal() + TypeOffset));
				int trail = gNet.gNetFifoTail;
				if (myconnectindex == connecthead)
					trail += 1;
				bb.putInt(trail);
				bb.putInt(Player[num].PlayerSprite);
				bb.put(sprite[Player[num].PlayerSprite].getBytes());
                return bb.position();
			}
		},
		ContentRequest {
			private String filepath;
			private long crc32;

			public void setData(Object... opts) {
				filepath = (String) opts[0];
				crc32 = (long) opts[1];
			}

			@Override
			public int Get(int fromPlayer, byte[] p, int len) {
				gNet.retransmit(fromPlayer, p, len);
				ByteBuffer bb = ByteBuffer.wrap(p).order(ByteOrder.LITTLE_ENDIAN);
				bb.position(1); // packet

				int pathlen = bb.getInt();
				if (pathlen >= bb.remaining() - 5)
					pathlen = bb.remaining() - 6;

				int ptr = bb.position();
				int nextptr = ptr + pathlen;
				boolean isDirectory = false;
				if (p[ptr] == '<' && p[ptr + 1] == 'd' && p[ptr + 2] == '>') {
					isDirectory = true;
					ptr += 3;
					pathlen -= 3;
				}

				String path = FileUtils.getCorrectPath(new String(p, ptr, pathlen));
				bb.position(nextptr);
				long crc32 = bb.getInt() & 0xFFFFFFFFL;
				long mycrc = -1;

				FileEntry fil = BuildGdx.compat.checkFile(path);
				GameInfo ini = GetEpisode(path, !isDirectory);

				byte found = 0;
				if (fil != null || ini != null) {
					MenuNetwork network = (MenuNetwork) Main.game.menu.mMenus[NETWORKGAME];
					if (ini != null) {
						mycrc = ini.getChecksum();
						if (mycrc == crc32) {
							found = 1;
							network.setEpisode(ini);
						} else {
							found = 2;
							Console.Println("Player" + fromPlayer + " - " + Player[fromPlayer].getName()
									+ " tried to set user content. User content found, but has a different checksum!",
									OSDTEXT_RED);
							Console.Println("Make sure that you have the same content: " + File.separator + path,
									OSDTEXT_RED);
							if (!Console.IsShown())
								Console.toggle();
						}
					} else if (fil != null && fil.getExtension().equals("map")) {
						mycrc = fil.getChecksum();
						if (mycrc == crc32) {
							found = 1;
							network.setMap(fil);
						} else {
							found = 2;
							Console.Println("Player" + fromPlayer + " - " + Player[fromPlayer].getName()
									+ " tried to set user content. User content found, but has a different checksum!",
									OSDTEXT_RED);
							Console.Println("Make sure that you have the same content: " + File.separator + path,
									OSDTEXT_RED);
							if (!Console.IsShown())
								Console.toggle();
						}
					}
				} else {
					Console.Println("Player" + fromPlayer + " - " + Player[fromPlayer].getName()
							+ " tried to set user content. User content not found!", OSDTEXT_RED);
					Console.Println("Make sure that you have content at the same path: " + File.separator + path,
							OSDTEXT_RED);
					if (!Console.IsShown())
						Console.toggle();
				}

				ContentAnswer.setData(found);
				int l = ContentAnswer.Send(netbuf);
				sendpacket(fromPlayer, netbuf, l);
				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
				bb.put((byte) (ordinal() + TypeOffset));
				int len = Math.min(filepath.length(), 246); // 255 - 1 - 4 - 4
				bb.putInt(len);
				bb.put(filepath.getBytes());
				bb.putInt((int) crc32);

				return bb.position();
			}
		},
		ContentAnswer {
			private byte found;

			public void setData(Object... opts) {
				found = (byte) opts[0];
			}

			@Override
			public int Get(int fromPlayer, byte[] p, int len) {
				gNet.retransmit(fromPlayer, p, len);
				gNet.gContentFound[fromPlayer] = p[1];
				return 0;
			}

			@Override
			public int Send(byte[] buf) {
				ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
				bb.put((byte) (ordinal() + TypeOffset));
				bb.put(found);
				return bb.position();
			}
		};

		public abstract int Get(int fromPlayer, byte[] buf, int len);

		public abstract int Send(byte[] buf);

		public void setData(Object... opts) {
			/* nothing */
		}
	}

	public WangNetwork(Main game) {
		super(game);
		this.app = game;
		
		for(int i = 0; i < kNetFifoSize; i++)
			predictFifo[i] = new Predict();
	}

	public void PauseMultiPlay() {
		// check for pause of multi-play game
		for (short pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			PlayerStr pp = Player[pnum];

			if (TEST_SYNC_KEY(pp, SK_PAUSE)) {
				if (FLAG_KEY_PRESSED(pp, SK_PAUSE)) {
					FLAG_KEY_RELEASE(pp, SK_PAUSE);

					game.gPaused = !game.gPaused;

					if (game.gPaused) {
						SavePrediction = PredictionOn;
						PredictionOn = false;
					} else {
						PredictionOn = SavePrediction;
					}
				}
			} else {
				FLAG_KEY_RESET(pp, SK_PAUSE);
			}
		}
	}

	public void InitNetPlayerProfile() {
		// if you don't have a name :(
		if (gs.pName == null)
			gs.pName = "PLAYER " + (myconnectindex + 1);
		Player[myconnectindex].PlayerName = gs.pName;
		Player[myconnectindex].TeamColor = gs.NetColor;
		if (gs.AutoAim)
			Player[myconnectindex].Flags |= (PF_AUTO_AIM);
		else
			Player[myconnectindex].Flags &= ~(PF_AUTO_AIM);

		int l = PacketType.Profile.Send(netbuf);

		WaitForSend();
		sendtoall(netbuf, l);

		GetPackets();
	}

	public void InitNetPlayerOptions() {
		PlayerStr pp = Player[myconnectindex];

		// myconnectindex palette
		pp.TeamColor = gs.NetColor;
		pp.getSprite().pal = (short) (PALETTE_PLAYER0 + pp.TeamColor);
		pUser[pp.PlayerSprite].spal = (byte) pp.getSprite().pal;

		if (game.nNetMode != NetMode.Single)
			InitNetPlayerProfile();
	}

	public void SendMessage(int sendmessagecommand, String message) {
		PacketType.Message.setData(sendmessagecommand, message);
		int l = PacketType.Message.Send(netbuf);

		if (myconnectindex != connecthead) {
			sendpacket(connecthead, netbuf, l);
		} else sendtoall(netbuf, l);
	}

	public boolean MyCommPlayerQuit() {
		for (short i = connecthead; i != -1; i = connectpoint2[i]) {
			PlayerStr pp = Player[i];

			if (TEST_SYNC_KEY(pp, SK_QUIT_GAME)) {
				if (i == myconnectindex) {
					QuitFlag = true;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public NetInput newInstance() {
		return new Input();
	}

	@Override
	public int GetPackets(byte[] p, int ptr, int len, final int nPlayer) {
		PacketType types[] = PacketType.values();
		int nPacket = p[0];
		if (nPacket == kPacketDisconnect) {
			return GetDisconnectPacket(p, 1, len, nPlayer, new DisconnectCallback() {
				@Override
				public void invoke(int nDelete) {
					if(rec != null)
						rec.close();

					if (game.isCurrentScreen(gGameScreen)) {
						PlayerStr qpp = Player[nDelete];
						SPRITE psp = qpp.getSprite();
						psp.cstat |= (CSTAT_SPRITE_INVISIBLE);
						psp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN
								| CSTAT_SPRITE_BLOCK_MISSILE);
						InitBloodSpray(qpp.PlayerSprite, true, -2);
						InitBloodSpray(qpp.PlayerSprite, false, -2);
						psp.ang = NORM_ANGLE(psp.ang + 1024);
						InitBloodSpray(qpp.PlayerSprite, false, -1);
						InitBloodSpray(qpp.PlayerSprite, true, -1);

						qpp.input.bits |= (1 << SK_QUIT_GAME);
						adduserquote(qpp.getName() + " has quit the game.");
					}

					// for COOP mode
					if (screenpeek == nDelete) {
						screenpeek = connectpoint2[nDelete];
						if (screenpeek < 0)
							screenpeek = connecthead;

						ResetPalette(Player[screenpeek], FORCERESET);
						DoPlayerDivePalette(Player[screenpeek]);
						DoPlayerNightVisionPalette(Player[screenpeek]);
					}
					CommPlayers--;
				}
			});
		}
		if (nPacket >= TypeOffset) {
			return types[nPacket - TypeOffset].Get(nPlayer, p, len);
		}

		System.err.println("Unsupported packet " + nPacket);
		return 0;
	}

	public byte[] gContentFound = new byte[MAXPLAYERS];

	public boolean WaitForContentCheck(String filepath, long crc32, int timeout) {
		Arrays.fill(gContentFound, (byte) -1);
		if (numplayers < 2)
			return true;

		WaitForSend();

		PacketType.ContentRequest.setData(filepath, crc32);
		int l = PacketType.ContentRequest.Send(packbuf);
		sendtoall(packbuf, l);
		gContentFound[myconnectindex] = 1;

		long starttime = System.currentTimeMillis();
		while (true) {
			long time = System.currentTimeMillis() - starttime;
			if ((timeout != 0 && time > timeout)) {
				Console.Println("Connection timed out!", OSDTEXT_YELLOW);
				return false;
			}

			GetPackets();

			int i;
			for (i = connecthead; i >= 0; i = connectpoint2[i]) {
				if (gContentFound[i] == -1)
					break;
				if (myconnectindex != connecthead) {
					i = -1;
					break;
				} // slaves in M/S mode only wait for master
			}

			if (i < 0) {
				for (i = connecthead; i >= 0; i = connectpoint2[i]) {
					if (gContentFound[i] != 1)
						return false;
				}
				return true;
			}
		}
	}

	@Override
	public void ComputerInput(int i) {
		if (BotMode)
			computergetinput(i, (Input) gFifoInput[gNetFifoHead[i] & 0xFF][i]);
	}

	@Override
	public void UpdatePrediction(NetInput input) {
		// routine called from MoveLoop
		if (!PredictionOn) {
			gPredictTail++;
			return;
		}

		ppp.input.Copy(input);

		// get rid of input bits so it doesn't go into other code branches that would
		// get it out of sync
		ppp.input.bits &= ~(BIT(SK_SHOOT) | BIT(SK_OPERATE) | BIT(SK_INV_LEFT) | BIT(SK_INV_RIGHT) | BIT(SK_INV_USE)
				| BIT(SK_HIDE_WEAPON) | BIT(SK_AUTO_AIM) | BIT(SK_CENTER_VIEW) | SK_WEAPON_MASK | SK_INV_HOTKEY_MASK);

		ppp.KeyPressFlags |= (BIT(SK_SHOOT) | BIT(SK_OPERATE) | BIT(SK_INV_LEFT) | BIT(SK_INV_RIGHT) | BIT(SK_INV_USE)
				| BIT(SK_HIDE_WEAPON) | BIT(SK_AUTO_AIM) | BIT(SK_CENTER_VIEW) | SK_WEAPON_MASK | SK_INV_HOTKEY_MASK);

		// back up things so they won't get stepped on
		int bakrandomseed = engine.getrand();
		SPRITE spr = sprite[Player[myconnectindex].PlayerSprite];
		USER u = pUser[ppp.PlayerSprite];
		pUser[ppp.PlayerSprite] = PredictUser;
		sprite[ppp.PlayerSprite] = PredictSprite;
		sprite[ppp.PlayerSprite].cstat = 0;

		ppp.oang = ppp.getAnglef();
		ppp.oposx = ppp.posx;
		ppp.oposy = ppp.posy;
		ppp.oposz = ppp.posz;
		ppp.ohoriz = ppp.getHorizf();
		ppp.obob_z = ppp.bob_z;

		// go through the player MOVEMENT code only
		Prediction = true;
		DoPlayerSectorUpdatePreMove(ppp);
		ppp.DoPlayerAction.invoke(ppp);
		DoPlayerSectorUpdatePostMove(ppp);
		Prediction = false;

		// restore things
		pUser[ppp.PlayerSprite] = u;
		sprite[Player[myconnectindex].PlayerSprite] = spr;
		engine.srand(bakrandomseed);

		predictFifo[gPredictTail & kFifoMask].set(ppp);
		gPredictTail++;
	}

	public void InitPrediction(PlayerStr pp) {
		if (!PredictionOn)
			return;

		// make a copy of player struct and sprite
		ppp.copy(pp);
		PredictUser.copy(pUser[pp.PlayerSprite]);
		PredictSprite.set(sprite[pp.PlayerSprite]);
	}

	@Override
	public void CorrectPrediction() {
		if (numplayers < 2 || !PredictionOn)
			return;

		Predict predict = predictFifo[(gNetFifoTail - 1) & kFifoMask];
		PlayerStr p = Player[myconnectindex];

		if (predict.ang == p.getAnglef() && predict.horiz == p.getHorizf() && predict.x == p.posx && predict.y == p.posy
				&& predict.z == p.posz)
			return;

		InitPrediction(p);
		gPredictTail = gNetFifoTail;
		while (gPredictTail < gNetFifoHead[myconnectindex])
			UpdatePrediction(gFifoInput[gPredictTail & kFifoMask][myconnectindex]);
	}

	private long PlayerSync(PlayerStr pp) {
		long crc = pp.posx;
		crc += pp.posy;
		crc += pp.posz;
		crc += pp.getAnglei();
		crc += pp.getHorizi();
		crc += pUser[pp.PlayerSprite].Health;
		crc += pp.bcnt;

		return crc;
	}

	private long MissileSync() {
		long crc = 0;
		int nexti;
		for (int i = headspritestat[STAT_MISSILE]; i != -1; nexti = nextspritestat[i], i = nexti) {
			SPRITE spr = sprite[i];
			crc += spr.x;
			crc += spr.y;
			crc += spr.z;
			crc += spr.ang;
		}

		return crc;
	}

	@Override
	public void CalcChecksum() {
		if ((numplayers >= 2 || gNet.FakeMultiplayer) && ((gNetFifoTail & 7) == 7)) // build sync variables
		{
			Arrays.fill(gChecksum, 0);
			gChecksum[0] = engine.getrand();
			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				PlayerStr pp = Player[i];
				gChecksum[1] ^= PlayerSync(pp); // Checksum(pp.getBytes(), PlayerStr.sizeof);
				gChecksum[2] ^= Checksum(sprite[pp.PlayerSprite].getBytes(), SPRITE.sizeof);
			}
			gChecksum[3] ^= MissileSync();
			for (int i = 0; i < gChecksum.length; i++)
				LittleEndian.putInt(gCheckFifo[myconnectindex],
						CheckSize * (gCheckHead[myconnectindex] & kFifoMask) + 4 * i, gChecksum[i]);
			gCheckHead[myconnectindex]++;
		}
	}

	@Override
	public void CheckSync() {
		if (PlayerSync != null || PlayerSpriteSync != null) {
			if (gNetFifoTail < PlayerSyncTrail)
				return;

			Console.Println("gNetFifoTail: " + gNetFifoTail + ", PlayerSyncTrail: " + PlayerSyncTrail);
			if(PlayerSync != null)
				PlayerSync.compare(Player[PlayerSync.pnum]);
			if(PlayerSpriteSync != null)
				engine.compare(PlayerSpriteSync, sprite[PlayerSyncIndex]);

			PlayerSyncTrail = -1;
			PlayerSyncIndex = -1;
			PlayerSync = null;
			PlayerSpriteSync = null;
		}

//		if (AUTOCOMPARE && MAXPAKSIZ >= PlayerStr.sizeof) {
//			if (numplayers == 1)
//				return;
//
//			while (true) {
//				for (int nPlayer = connecthead; nPlayer >= 0; nPlayer = connectpoint2[nPlayer]) {
//					if (gCheckHead[nPlayer] <= gCheckTail)
//						return;
//				}
//
//				bOutOfSync = false;
//				for (int nPlayer = connectpoint2[connecthead]; nPlayer >= 0; nPlayer = connectpoint2[nPlayer]) {
//					for (int i = 0; i < CheckSize; i++) {
//						if (gCheckFifo[nPlayer][(CheckSize * (gCheckTail & kFifoMask))
//								+ i] != gCheckFifo[connecthead][(CheckSize * (gCheckTail & kFifoMask)) + i]) {
//							bOutOfSyncByte = i;
//							bOutOfSync = true;
//
//							if (i == 4) {
//								PacketType.PlayerData.setData(nPlayer);
//								int l = PacketType.PlayerData.Send(netbuf);
//								sendtoall(netbuf, l);
//							}
//						}
//					}
//				}
//				gCheckTail++;
//			}
//		} else
			super.CheckSync();
	}

	@Override
	public void NetDisconnect(int nPlayer) {
		super.NetDisconnect(nPlayer);
		app.Disconnect();
	}

	public void save(BufferResource fil) {
		fil.writeInt(KillLimit);
		fil.writeInt(TimeLimit);
		fil.writeInt(TimeLimitClock);

		fil.writeByte(MultiGameType != null ? MultiGameType.ordinal() : -1);

		fil.writeBoolean(TeamPlay);
		fil.writeBoolean(HurtTeammate);
		fil.writeBoolean(SpawnMarkers);
		fil.writeBoolean(NoRespawn);
		fil.writeBoolean(Nuke);
	}

	public void load(Resource res) {
		KillLimit = res.readInt();
		TimeLimit = res.readInt();
		TimeLimitClock = res.readInt();

		int i = res.readByte();
		MultiGameType = i != -1 ? MultiGameTypes.values()[i] : null;

		TeamPlay = res.readBoolean();
		HurtTeammate = res.readBoolean();
		SpawnMarkers = res.readBoolean();
		NoRespawn = res.readBoolean();
		Nuke = res.readBoolean();
	}
}
