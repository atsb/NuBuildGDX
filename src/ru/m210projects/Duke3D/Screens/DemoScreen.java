// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Screens;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.MAIN;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.OPTIONS;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.QUIT;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.SOUNDSET;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.LoadSave.lastload;
import static ru.m210projects.Duke3D.Main.cfg;
import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Main.gDemoScreen;
import static ru.m210projects.Duke3D.Main.gMenuScreen;
import static ru.m210projects.Duke3D.Main.game;
import static ru.m210projects.Duke3D.Main.gPrecacheScreen;
import static ru.m210projects.Duke3D.Player.InitPlayers;
import static ru.m210projects.Duke3D.ResourceHandler.levelGetEpisode;
import static ru.m210projects.Duke3D.Screen.changepalette;
import static ru.m210projects.Duke3D.SoundDefs.THUD;
import static ru.m210projects.Duke3D.Sounds.sound;
import static ru.m210projects.Duke3D.View.operatefta;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Duke3D.Config.DukeKeys;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;
import ru.m210projects.Duke3D.Types.DemoFile;
import ru.m210projects.Duke3D.Types.GameInfo;

public class DemoScreen extends GameScreen {

    public static String firstdemofile;
    public int nDemonum = -1;
    public List<String> demofiles = new ArrayList<String>();
    public DemoFile demfile;

    public DemoScreen(Main game) {
        super(game);
    }

    @Override
    public void show() {
        lastload = null;
    }

    @Override
    public void hide() {
        ud.user_name[myconnectindex] = cfg.pName;
    }

    public boolean showDemo(String name, String ini) {
        demfile = null;
        try {
            demfile = new DemoFile(name);
        } catch (Exception ignored) {
        }

        if (demfile == null || demfile.reccnt == 0) {
            Console.Println("Can't play the demo file: " + name, OSDTEXT_RED);
            return false;
        }

        InitPlayers();
        mFakeMultiplayer = demfile.multimode > 1;
        if (mFakeMultiplayer)
            nFakePlayers = demfile.multimode;

        if (numplayers > 1)
            game.pNet.NetDisconnect(myconnectindex);

        ud.volume_number = demfile.volume_number;
        ud.level_number = demfile.level_number;
        ud.player_skill = demfile.player_skill;

        ud.coop = demfile.coop;
        ud.ffire = demfile.ffire;
        ud.multimode = demfile.multimode;
        ud.monsters_off = demfile.monsters_off;
        ud.respawn_monsters = demfile.respawn_monsters;
        ud.respawn_items = demfile.respawn_items;
        ud.respawn_inventory = demfile.respawn_inventory;
        ud.playerai = demfile.playerai;
        System.arraycopy(demfile.user_name, 0, ud.user_name, 0, MAXPLAYERS);

        boardfilename = demfile.boardfilename;

        for (int i = 0; i < ud.multimode; i++) {
            ps[i].aim_mode = demfile.aim_mode[i];
            if (demfile.version >= JFBYTEVERSION)
                ps[i].auto_aim = demfile.auto_aim[i];
            else ps[i].auto_aim = 1;
        }

        ud.god = false;
        ud.cashman = ud.eog = ud.showallmap = 0;
        ud.clipping = ud.scrollmode = false;
        ud.overhead_on = 0;
        ud.recstat = 2;

        GameInfo addon = levelGetEpisode(ini);
        if (demfile.addon != null)
            addon = demfile.addon;

        gDemoScreen.newgame(mFakeMultiplayer, addon, ud.volume_number, ud.level_number, ud.player_skill);

        Console.Println("Playing demo " + name);

        return true;
    }

    @Override
    protected void startboard(final Runnable startboard) {
        gPrecacheScreen.init(false, new Runnable() {
            @Override
            public void run() {
                startboard.run(); //call faketimehandler
                pNet.ResetTimers(); //reset ototalclock
                lockclock = 0;
                pNet.ready2send = false;
            }
        });
        game.changeScreen(gPrecacheScreen);
    }

    @Override
    public void KeyHandler() {
        pEngine.handleevents();

        DukeMenuHandler menu = game.menu;
        if (menu.gShowMenu) {
            menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
            return;
        }

        if (Console.IsShown())
            return;

        BuildControls input = game.pInput;
        if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
            menu.mOpen(menu.mMenus[MAIN], -1);
        if (input.ctrlGetInputKey(DukeKeys.Show_Sounds, true))
            menu.mOpen(menu.mMenus[SOUNDSET], -1);

        if (input.ctrlGetInputKey(DukeKeys.Show_Options, true))
            menu.mOpen(menu.mMenus[OPTIONS], -1);

        if (input.ctrlGetInputKey(DukeKeys.Gamma, true))
            openGamma(menu);

        if (input.ctrlGetInputKey(DukeKeys.Quit, true))
            menu.mOpen(menu.mMenus[QUIT], -1);

        if (input.ctrlGetInputKey(DukeKeys.Screenshot, true))
            makeScreenshot();

        if (input.ctrlGetInputKey(DukeKeys.See_Coop_View, true)) {
            if (ud.coop == 1 || mFakeMultiplayer) {
                screenpeek = connectpoint2[screenpeek];
                if (screenpeek < 0) screenpeek = connecthead;

                changepalette = 1; //if player has other palette
            }
        }

        if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true)) {
            if (ud.screen_size > 0) {
                sound(THUD);
                enlargeScreen();
            }
        }
        if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, true)) {
            if (ud.screen_size < 3) {
                sound(THUD);
                shrinkScreen();
            }
        }
    }

    @Override
    public void render(float delta) {
        KeyHandler();

        if (mFakeMultiplayer)
            pEngine.faketimerhandler();

        if (numplayers > 1)
            pNet.GetPackets();

        DemoRender();

        float smoothratio = 65536;
        if (!game.gPaused) {
            smoothratio = pEngine.getsmoothratio();
            if (smoothratio < 0 || smoothratio > 0x10000) {
                smoothratio = BClipRange(smoothratio, 0, 0x10000);
            }
        }

        game.pInt.dointerpolations(smoothratio);
        DrawWorld(smoothratio);

        DrawHud(smoothratio);
        game.pInt.restoreinterpolations();

        operatefta();

        if (ud.last_camsprite != ud.camerasprite) {
            ud.last_camsprite = ud.camerasprite;
            ud.camera_time = totalclock + (TICRATE * 2);
        }

        if (pMenu.gShowMenu)
            pMenu.mDrawMenu();

        PostFrame(pNet);

        if (pCfg.gShowFPS)
            pEngine.printfps(pCfg.gFpsScale);

        pEngine.sampletimer();
        pEngine.nextpage();
    }


    private void DemoRender() {
        pNet.ready2send = false;

        if (!game.isCurrentScreen(this))
            return;

        if (!game.gPaused && demfile != null) {
            while (totalclock >= (lockclock + TICSPERFRAME)) {
                for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
                    pNet.gFifoInput[pNet.gNetFifoHead[j] & 0xFF][j].Copy(demfile.recsync[demfile.rcnt][j]);
                    pNet.gNetFifoHead[j]++;
                    demfile.reccnt--;
                }

                if (demfile.reccnt <= 0) {
                    if (!showDemo())
                        game.changeScreen(gMenuScreen);
                    return;
                }

                demfile.rcnt++;
                engine.updatesmoothticks();
                game.pInt.clearinterpolations();
                ProcessFrame(pNet);
            }
        } else lockclock = totalclock;
    }

    public boolean showDemo() {
        switch (cfg.gDemoSeq) {
            case 0: //OFF
                return false;
            case 1: //Consistently
                if (nDemonum < (demofiles.size() - 1))
                    nDemonum++;
                else
                    nDemonum = 0;
                break;
            case 2: //Accidentally
                int nextnum = nDemonum;
                if (demofiles.size() > 1) {
                    while (nextnum == nDemonum)
                        nextnum = (int) (Math.random() * (demofiles.size()));
                }
                nDemonum = nextnum;
                break;
        }

        if (demofiles != null && demofiles.size() > 0)
            return showDemo(demofiles.get(nDemonum), null);

        return false;
    }

    public void demoscan() {
        byte[] buf = new byte[4];

        Resource fil = null;
        for (FileEntry file : BuildGdx.compat.getDirectory(Path.Game).getFiles().values()) {
            if (file.getExtension().equals("dmo")) {
                String name = file.getFile().getName();
                if ((fil = BuildGdx.compat.open(file)) != null) {
                    fil.read(buf, 0, 4);
                    fil.read(buf, 0, 1);
                    int version = buf[0] & 0xFF;
                    if (version == BYTEVERSION15 || version == BYTEVERSION15 + 1 || version == JFBYTEVERSION
                            || version == GDXBYTEVERSION)
                        demofiles.add(name);
                    fil.close();
                }
            }
        }

        if (demofiles.size() == 0) //try to find it in mainGrp
        {
            fil = null;
            int which_demo = 1;
            do {
                char[] d = "demo_.dmo".toCharArray();
                if (which_demo == 10)
                    d[4] = 'x';
                else d[4] = (char) ('0' + which_demo);
                String name = new String(d);
                if ((fil = BuildGdx.cache.open(name, 0)) != null) {
                    fil.read(buf, 0, 4);
                    fil.read(buf, 0, 1);
                    int version = buf[0] & 0xFF;
                    if (version == BYTEVERSION15 || version == BYTEVERSION15 + 1 || version == JFBYTEVERSION
                            || version == GDXBYTEVERSION)
                        demofiles.add(name);
                    fil.close();
                }
                which_demo++;
            } while (fil != null);
        }

        if (demofiles.size() != 0)
            Collections.sort(demofiles);
        Console.Println("There are " + demofiles.size() + " demo(s) in the loop", OSDTEXT_GOLD);
    }

    @Override
    public boolean IsOriginalGame() {
        return (demfile.version <= JFBYTEVERSION);
    }
}
