//This file is part of BuildGDX.
//Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//                         fgsfds
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Launcher.desktop.Components;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.miginfocom.swing.MigLayout;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Launcher.desktop.BoundsPopupMenuListener;
import ru.m210projects.Launcher.desktop.CheckFiles;
import ru.m210projects.Launcher.desktop.ComboItem;
import ru.m210projects.Launcher.desktop.JLauncher;
import ru.m210projects.Launcher.desktop.*;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;
import ru.m210projects.Launcher.desktop.GameEntries.RREntry;
import ru.m210projects.Launcher.desktop.Components.Buttons.OpenButton;
import ru.m210projects.Launcher.desktop.Components.Buttons.SettingsButton;

public class RRPanel extends GamePanel {

	private static final long serialVersionUID = 1L;
	private JTextField pathField;
	private JButton launchButton;
	private OpenButton openButton;
	private JLauncher main;
	private JComboBox<ComboItem> gameFoldersList;
	private JLabel messageField = new JLabel();
	private boolean isPortable;
	private boolean Init = false;
	private boolean initialized = false;

	public RRPanel(JLauncher main, RREntry entry, boolean isPortable) {
		super(entry);
		this.main = main;
		this.isPortable = isPortable;

		main.services.registerGame(GameServices.Service.GOG, entry,
				"[GOG]:\\" + "GOGREDNECKRAMPAGE");

		main.services.registerGame(GameServices.Service.Steam, entry,
				"[Steam]:\\" + "Redneck Rampage" + File.separator + "Redneck");
	}

	@Override
	public void init() {
		this.setLayout(new MigLayout("", "[grow][]", "[][grow][]"));

		if (!isPortable) {
			final ArrayList<GameServices.GameLabel> labels = new ArrayList<>(main.services.getPaths(entry));
			if (labels.size() > 0) {
				gameFoldersList = new JComboBox<ComboItem>();
				final BuildConfig cfg = entry.getConfig();
				final ComboItem cfgPath = new ComboItem(cfg.path, cfg.path);
				if (cfg.path != null) {
					gameFoldersList.addItem(cfgPath);
				}

				PopupMenuListener popupMenuListener = new PopupMenuListener() {
					@Override
					public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
						if (!Init) {
							for (GameServices.GameLabel label : labels) {
								ComboItem path = new ComboItem(label.path, label.label + " " + label.path);
								if (Objects.equals(label.path, cfg.path)) {
									gameFoldersList.insertItemAt(path, 0);
									gameFoldersList.removeItem(cfgPath);
								} else
									gameFoldersList.addItem(path);
							}

							Init = true;

						}
					}

					@Override
					public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					}

					@Override
					public void popupMenuCanceled(PopupMenuEvent e) {
					}
				};

				gameFoldersList.addPopupMenuListener(popupMenuListener);
				gameFoldersList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						path = ((ComboItem) gameFoldersList.getSelectedItem()).getValue();

						CheckFiles result = entry.checkResources(path, true);
						boolean access = result.getValue();
						messageField.setText(result.getLabel());
						entry.getConfig().path = path;
						startButtonStatus(access);

					}
				});

				BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
				gameFoldersList.addPopupMenuListener(listener);
				gameFoldersList.setPrototypeDisplayValue(new ComboItem("", ""));

				add(gameFoldersList, "cell 0 0,grow");
			} else {
				pathField = new JTextField();
				pathField.setToolTipText("Path to " + entry.getResourceName() + " files");
				pathField.setEditable(false);
				add(pathField, "cell 0 0,grow");
				pathField.setColumns(10);
			}
		}

		JButton browseTW = new JButton("...");
		browseTW.setToolTipText("Choose folder");
		browseTW.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = DirectoryBrowser.showDirectoryChooser(Main.fr.getFrame(), path, entry.getResourceName(),
						entry.getIcon16());
				if (f != null)
					path = f.getAbsolutePath() + File.separator;

				if (gameFoldersList != null) {
					ComboItem addedPath;
					gameFoldersList.addItem(addedPath = new ComboItem(path, path));
					gameFoldersList.setSelectedItem(addedPath);
				} else if (pathField != null) {
					pathField.setText(path);
				}

				CheckFiles result = entry.checkResources(path, true);
				boolean access = result.getValue();
				messageField.setText(result.getLabel());
				entry.getConfig().path = path;

				startButtonStatus(access);
			}
		});
		if (!isPortable)
			add(browseTW, "cell 2 0,grow");

		add((openButton = new OpenButton()), "cell 3 0,grow");

		add(messageField, "cell 0 1 4, grow");
		messageField.setVerticalAlignment(JLabel.TOP);

		launchButton = new JButton();
		launchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				launchButton.setText("Loading...");
				launchButton.setEnabled(false);

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						entry.startGame(path);
					}
				});
			}
		});
		add(launchButton, "cell 0 3 3 1,grow");

		add(new SettingsButton(main, entry, null, "Settings"), "cell 3 3,alignx right,growy");
	}

	@Override
	public void update() {
		BuildConfig cfg = entry.getConfig();
		path = cfg.path != null ? cfg.path : GameEntry.getDirPath();
		openButton.update(path);

		if (!initialized) {
			CheckFiles result = entry.checkResources(path, true);
			boolean access = result.getValue();
			messageField.setText(result.getLabel());

			startButtonStatus(access);
			if (pathField != null)
				pathField.setText(path);
			initialized = true;
		}
	}

	public void startButtonStatus(boolean access) {
		launchButton.setEnabled(access);
		if (access) {
			launchButton.setText("Play " + entry.getResourceName());
		} else
			launchButton.setText(entry.getResourceName() + " resources not found!");
	}
}
