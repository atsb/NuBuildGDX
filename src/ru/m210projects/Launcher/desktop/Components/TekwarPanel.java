//This file is part of BuildGDX.
//Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Launcher.desktop.CheckFiles;
import ru.m210projects.Launcher.desktop.JLauncher;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;
import ru.m210projects.Launcher.desktop.GameEntries.TekwarEntry;
import ru.m210projects.Launcher.desktop.Main;
import ru.m210projects.Launcher.desktop.Components.Buttons.OpenButton;
import ru.m210projects.Launcher.desktop.Components.Buttons.SettingsButton;

public class TekwarPanel extends GamePanel {

	private static final long serialVersionUID = 1L;
	private JTextField pathField;
	private JButton launchButton;
	private OpenButton openButton;
	private JLauncher main;
	private JLabel messageField = new JLabel();
	private boolean isPortable;
	private boolean initialized = false;

	public TekwarPanel(JLauncher main, TekwarEntry entry, boolean isPortable) {
		super(entry);
		this.main = main;
		this.isPortable = isPortable;

	}

	public void init() {
		this.setLayout(new MigLayout("", "[grow][]", "[][grow][]"));

		pathField = new JTextField();
		pathField.setToolTipText("Path to " + entry.getResourceName() + " files");
		pathField.setEditable(false);
		add(pathField, "cell 0 0,grow");
		pathField.setColumns(10);

		JButton browseTW = new JButton("...");
		browseTW.setToolTipText("Choose folder");
		browseTW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = DirectoryBrowser.showDirectoryChooser(Main.fr.getFrame(), path, entry.getResourceName(),
						entry.getIcon16());
				if (f != null)
					path = f.getAbsolutePath() + File.separator;
				pathField.setText(path);

				CheckFiles result = entry.checkResources(path, true);
				boolean access = result.getValue();
				messageField.setText(result.getLabel());

				entry.getConfig().path = path;

				startButtonStatus(access);
			}
		});

		if (!isPortable)
			add(browseTW, "cell 1 0,grow");

		add((openButton = new OpenButton()), "cell 2 0,grow");

		add(messageField, "cell 0 1 4, grow");
		messageField.setVerticalAlignment(JLabel.TOP);

		launchButton = new JButton();
		launchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchButton.setText("Loading...");
				launchButton.setEnabled(false);

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						entry.startGame(path);
					}
				});
			}
		});
		add(launchButton, "cell 0 3 2 1,grow");

		add(new SettingsButton(main, entry, null, "Settings"), "cell 2 3,alignx right,growy");
	}

	@Override
	public void update() {
		BuildConfig cfg = entry.getConfig();
		path = cfg.path != null ? cfg.path : GameEntry.getDirPath();
		openButton.update(path);
		
		if (!initialized) {
			CheckFiles result = entry.checkResources(path, true);
			boolean access = result.getValue();
			if (result.getLabel() != null)
				messageField.setText(result.getLabel());

			pathField.setText(path);
			startButtonStatus(access);
			initialized = true;
		}
	}

	public void startButtonStatus(boolean access) {
		launchButton.setEnabled(access);
		if (access) {
			if (((TekwarEntry) entry).isDemo)
				launchButton.setText("Play " + entry.getResourceName() + " Demo");
			else
				launchButton.setText("Play " + entry.getResourceName());
			launchButton.requestFocus();
		} else
			launchButton.setText(entry.getResourceName() + " resources not found!");
	}
}
