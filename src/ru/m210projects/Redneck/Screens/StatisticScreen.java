// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Screens;

import static java.lang.Math.max;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH1;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH2;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH3;
import static ru.m210projects.Redneck.SoundDefs.BONUS_SPEECH4;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;
import static ru.m210projects.Redneck.Sounds.clearsoundlocks;
import static ru.m210projects.Redneck.Sounds.sndStopMusic;
import static ru.m210projects.Redneck.Sounds.sound;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.MapInfo;

public class StatisticScreen extends ScreenAdapter {

    protected char[] bonusbuf = new char[128];
    protected int bonuscnt = 0;

    private int[] checkSound = {
            8, 23, 24, 26 //Bubba pain
    };

    protected Main app;

    public StatisticScreen(Main app) {
        this.app = app;
    }

    @Override
    public void show() {
        engine.setbrightness(ud.brightness >> 2, palette, GLInvalidateFlag.All);
        totalclock = 0;

        for (int s = 0; s < checkSound.length; s++) {
            int num = checkSound[s];
            if (Sound[num].num == 0) continue;
            Source voice = SoundOwner[num][Sound[num].num - 1].voice;
            while (voice != null && voice.isActive()) ;
        }

        StopAllSounds();
        sndStopMusic();
        clearsoundlocks();

        if (currentGame.getCON().type == RRRA) {
            if (ud.volume_number != 0) {
                switch (ud.level_number) {
                    case 1:
                        gAnmScreen.init("lvl8.anm", -1);
                        break;
                    case 2:
                        gAnmScreen.init("lvl9.anm", -1);
                        break;
                    case 3:
                        gAnmScreen.init("lvl10.anm", -1);
                        break;
                    case 4:
                        gAnmScreen.init("lvl11.anm", -1);
                        break;
                    case 5:
                        gAnmScreen.init("lvl12.anm", -1);
                        break;
                    case 6:
                        gAnmScreen.init("lvl13.anm", -1);
                        break;
                    default:
                        break;
                }
            } else {
                switch (ud.level_number) {
                    case 1:
                        gAnmScreen.init("lvl1.anm", -1);
                        break;
                    case 2:
                        gAnmScreen.init("lvl2.anm", -1);
                        break;
                    case 3:
                        gAnmScreen.init("lvl3.anm", -1);
                        break;
                    case 4:
                        gAnmScreen.init("lvl4.anm", -1);
                        break;
                    case 5:
                        gAnmScreen.init("lvl5.anm", -1);
                        break;
                    case 6:
                        gAnmScreen.init("lvl6.anm", -1);
                        break;
                    default:
                        gAnmScreen.init("lvl7.anm", -1);
                        break;
                }
            }
        }

        bonuscnt = 0;
        game.pInput.ctrlResetKeyStatus();
    }

    @Override
    public void render(float delta) {
        engine.clearview(0);
        engine.sampletimer();

        if (kGameCrash) {
            game.show();
            return;
        }

        if (numplayers > 1)
            game.pNet.GetPackets();

        if (dobonus(false)) {
            BuildGdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if ((uGameFlags & MODE_END) != 0 || mUserFlag == UserFlag.UserMap) {
                        if ((uGameFlags & MODE_END) != 0 && ud.volume_number == 0) {
                            ud.volume_number = 1;
                            ud.level_number = 0;
                            gGameScreen.enterlevel(gGameScreen.getTitle());
                            return;
                        }

                        game.show();
                    } else gGameScreen.enterlevel(gGameScreen.getTitle());
                }
            });
        }

        engine.nextpage();
    }

    public boolean dobonus(boolean disconnect) {
        int t, gfx;
        int i, y, xfragtotal, yfragtotal;
        int clockpad = 2;

        if (ud.multimode > 1 && ud.coop != 1 || disconnect) {
            engine.rotatesprite(0, 0, 65536, 0, MENUSCREEN, 16, 0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);
            engine.rotatesprite(160 << 16, 34 << 16, 65536, 0, INGAMELNRDTHREEDEE, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);

            app.getFont(1).drawText(160, 58 + 2, "MULTIPLAYER TOTALS", 0, 0, TextAlign.Center, 2, false);
            buildString(bonusbuf, 0, currentGame.episodes[ud.volume_number].gMapInfo[ud.level_number].title);
            app.getFont(1).drawText(160, 58 + 10, bonusbuf, 0, 0, TextAlign.Center, 2, false);

            t = 0;
            app.getFont(0).drawText(23, 80, "   NAME                                           KILLS", 0, 0, TextAlign.Left, 2, false);
            for (i = 0; i < numplayers; i++) {
                Bitoa(i + 1, bonusbuf);
                app.getFont(0).drawText(92 + (i * 23), 80, bonusbuf, 0, 3, TextAlign.Left, 2, false);
            }

            for (i = 0; i < numplayers; i++) {
                xfragtotal = 0;
                Bitoa(i + 1, bonusbuf);

                app.getFont(0).drawText(30, 90 + t, bonusbuf, 0, 3, TextAlign.Left, 2, false);
                app.getFont(0).drawText(38, 90 + t, ud.user_name[i], 0, ps[i].palookup, TextAlign.Left, 2, false);

                for (y = 0; y < numplayers; y++) {
                    if (i == y) {
                        Bitoa(ps[y].fraggedself, bonusbuf);
                        app.getFont(0).drawText(92 + (y * 23), 90 + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);
                        xfragtotal -= ps[y].fraggedself;
                    } else {
                        Bitoa(frags[i][y], bonusbuf);
                        app.getFont(0).drawText(92 + (y * 23), 90 + t, bonusbuf, 0, 0, TextAlign.Left, 2, false);
                        xfragtotal += frags[i][y];
                    }
                }

                Bitoa(xfragtotal, bonusbuf);
                app.getFont(0).drawText(101 + (8 * 23), 90 + t, bonusbuf, 0, 2, TextAlign.Left, 2, false);

                t += 7;
            }

            for (y = 0; y < numplayers; y++) {
                yfragtotal = 0;
                for (i = 0; i < numplayers; i++) {
                    if (i == y)
                        yfragtotal += ps[i].fraggedself;
                    yfragtotal += frags[i][y];
                }
                Bitoa(yfragtotal, bonusbuf);
                app.getFont(0).drawText(92 + (y * 23), 96 + (8 * 7), bonusbuf, 0, 2, TextAlign.Left, 2, false);
            }

            app.getFont(0).drawText(45, 96 + (8 * 7), "DEATHS", 0, 8, TextAlign.Left, 2, false);
            app.getFont(1).drawText(160, 165, "PRESS ANY KEY TO CONTINUE", 0, 2, TextAlign.Center, 2, false);

            if ((game.pInput.ctrlKeyStatusOnce(ANYKEY)) && totalclock > (60 * 2))
                return true;
        }

        if (!disconnect && (ud.multimode < 2 || ud.coop == 1)) {
            MapInfo gMapInfo = currentGame.episodes[ud.volume_number].gMapInfo[ud.last_level - 1];
            if (gMapInfo != null) {
                int ii, ij;

                for (ii = ps[myconnectindex].player_par / (26 * 60), ij = 1; ii > 9; ii /= 10, ij++) ;
                clockpad = max(clockpad, ij);
                for (ii = gMapInfo.partime / (26 * 60), ij = 1; ii > 9; ii /= 10, ij++) ;
                clockpad = max(clockpad, ij);
                for (ii = gMapInfo.designertime / (26 * 60), ij = 1; ii > 9; ii /= 10, ij++) ;
                clockpad = max(clockpad, ij);
            }

            if (totalclock >= (1000000000) && totalclock < (1000000320)) {
                if (((totalclock >> 4) % 15) == 0 && bonuscnt == 6) {
                    bonuscnt++;
                    sound(425);
                    Source voice = null;
                    switch (engine.rand() & 3) {
                        case 0:
                            voice = sound(BONUS_SPEECH1);
                            break;
                        case 1:
                            voice = sound(BONUS_SPEECH2);
                            break;
                        case 2:
                            voice = sound(BONUS_SPEECH3);
                            break;
                        case 3:
                            voice = sound(BONUS_SPEECH4);
                            break;
                    }
                    while (voice != null && voice.isActive()) ;
                }
            } else if (totalclock > (10240 + 120)) {
                if (currentGame.getCON().type == RRRA)
                    gAnmScreen.anmClose();
                return true;
            }

            String lastmapname = null;
            if (ud.volume_number == 2 && ud.last_level == 4 && boardfilename != null) {
                FileEntry file = BuildGdx.compat.checkFile(boardfilename);
                lastmapname = file.getName();
            } else if (gMapInfo != null)
                lastmapname = gMapInfo.title;

            int pal = 0;
            if (currentGame.getCON().type != RRRA) {
                int level = ud.level_number;
                if (ud.volume_number != 0)
                    gfx = 408 + level;
                else {
                    if (level == 0) level = 1;
                    gfx = 402 + level;
                }

                if (mUserFlag == UserFlag.UserMap && boardfilename != null && ud.level_number == 3 && ud.volume_number == 2) {
                    engine.rotatesprite(0, 0, 65536, 0, 403, 0, 0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);
                } else {
                    engine.rotatesprite(0, 0, 65536, 0, gfx, 0, 0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);
                }
            } else {
                if (gAnmScreen.isInited())
                    gAnmScreen.anmPlay();
                else engine.rotatesprite(0, 0, 65536, 0, 403, 0, 0, 2 + 8 + 16 + 64, 0, 0, xdim - 1, ydim - 1);
                pal = 2;
            }

            app.getFont(2).drawText(160, 10 - 8, lastmapname, 0, pal, TextAlign.Center, 2, false);
            app.getFont(2).drawText(160, 180, "PRESS ANY KEY TO CONTINUE", 0, pal, TextAlign.Center, 2, false);

            int pos = 30;
            if (totalclock > (60 * 3)) {
                app.getFont(2).drawText(10, pos, "Yer Time:", 0, pal, TextAlign.Left, 2, false);
                app.getFont(2).drawText(10, pos += 19, "Par time:", 0, pal, TextAlign.Left, 2, false);
                app.getFont(2).drawText(10, pos += 19, "Xatrix Time:", 0, pal, TextAlign.Left, 2, false);

                if (bonuscnt == 0)
                    bonuscnt++;

                if (totalclock > (60 * 4)) {
                    if (bonuscnt == 1) {
                        bonuscnt++;
                        sound(404);
                    }

                    pos = 30;
                    int num = Bitoa(ps[myconnectindex].player_par / (26 * 60), bonusbuf, 2);
                    buildString(bonusbuf, num, " : ", (ps[myconnectindex].player_par / 26) % 60, 2);
                    app.getFont(2).drawText(211, pos, bonusbuf, 0, pal, TextAlign.Left, 2, false);

                    if (gMapInfo != null) {
                        num = Bitoa(gMapInfo.partime / (26 * 60), bonusbuf, 2);
                        buildString(bonusbuf, num, " : ", (gMapInfo.partime / 26) % 60, 2);
                        app.getFont(2).drawText(211, pos += 19, bonusbuf, 0, pal, TextAlign.Left, 2, false);

                        num = Bitoa(gMapInfo.designertime / (26 * 60), bonusbuf, 2);
                        buildString(bonusbuf, num, " : ", (gMapInfo.designertime / 26) % 60, 2);
                        app.getFont(2).drawText(211, pos += 19, bonusbuf, 0, pal, TextAlign.Left, 2, false);
                    }
                }
            }
            if (totalclock > (60 * 6)) {
                pos = 95;
                app.getFont(2).drawText(10, pos, "Varmints Killed:", 0, pal, TextAlign.Left, 2, false);
                app.getFont(2).drawText(10, pos += 19, "Varmints Left:", 0, pal, TextAlign.Left, 2, false);

                if (bonuscnt == 2) {
                    bonuscnt++;
                }

                if (totalclock > (60 * 7)) {
                    if (bonuscnt == 3) {
                        bonuscnt++;
                        sound(422);
                    }

                    pos = 95;
                    Bitoa(ps[connecthead].actors_killed, bonusbuf);
                    app.getFont(2).drawText(251, pos, bonusbuf, 0, pal, TextAlign.Left, 2, false);
                    if (isPsychoSkill()) {
                        buildString(bonusbuf, 0, "N/A");
                        app.getFont(2).drawText(251, pos += 19, "N/A", 0, pal, TextAlign.Left, 2, false);
                    } else {
                        if ((ps[connecthead].max_actors_killed - ps[connecthead].actors_killed) < 0)
                            Bitoa(0, bonusbuf);
                        else Bitoa(ps[connecthead].max_actors_killed - ps[connecthead].actors_killed, bonusbuf);
                        app.getFont(2).drawText(251, pos += 19, bonusbuf, 0, pal, TextAlign.Left, 2, false);
                    }
                }
            }
            if (totalclock > (60 * 9)) {
                pos = 133;
                app.getFont(2).drawText(10, pos, "Secrets Found:", 0, pal, TextAlign.Left, 2, false);
                app.getFont(2).drawText(10, pos += 19, "Secrets Missed:", 0, pal, TextAlign.Left, 2, false);

                if (bonuscnt == 4) bonuscnt++;

                if (totalclock > (60 * 10)) {
                    if (bonuscnt == 5) {
                        bonuscnt++;
                        sound(404);
                    }
                    pos = 133;
                    Bitoa(ps[connecthead].secret_rooms, bonusbuf);
                    app.getFont(2).drawText(251, pos, bonusbuf, 0, pal, TextAlign.Left, 2, false);

                    Bitoa(ps[connecthead].max_secret_rooms - ps[connecthead].secret_rooms, bonusbuf);
                    app.getFont(2).drawText(251, pos += 19, bonusbuf, 0, pal, TextAlign.Left, 2, false);
                }
            }

            if (totalclock > 10240 && totalclock < 10240 + 10240)
                totalclock = 1024;

            if ((game.pInput.ctrlKeyStatusOnce(ANYKEY)) && totalclock > (60 * 2))    // JBF 20030809
            {
                if (totalclock < (60 * 13))
                    totalclock = (60 * 13);
                else if (totalclock < (1000000000))
                    totalclock = (1000000000);
            }
        }

        return false;
    }
}
