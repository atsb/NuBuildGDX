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

package ru.m210projects.Launcher.desktop;

import static ru.m210projects.Launcher.desktop.Main.headerPath;
import static ru.m210projects.Launcher.desktop.Main.iconPath;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import ru.m210projects.Launcher.desktop.Components.About;
import ru.m210projects.Launcher.desktop.Components.BloodPanel;
import ru.m210projects.Launcher.desktop.Components.DukePanel;
import ru.m210projects.Launcher.desktop.Components.FramePanel;
import ru.m210projects.Launcher.desktop.Components.GamePanel;
import ru.m210projects.Launcher.desktop.Components.ImagePanel;
import ru.m210projects.Launcher.desktop.Components.LSPPanel;
import ru.m210projects.Launcher.desktop.Components.NamPanel;
import ru.m210projects.Launcher.desktop.Components.PSPanel;
import ru.m210projects.Launcher.desktop.Components.RRPanel;
import ru.m210projects.Launcher.desktop.Components.RRRAPanel;
import ru.m210projects.Launcher.desktop.Components.SWPanel;
import ru.m210projects.Launcher.desktop.Components.Settings;
import ru.m210projects.Launcher.desktop.Components.TekwarPanel;
import ru.m210projects.Launcher.desktop.Components.WH2Panel;
import ru.m210projects.Launcher.desktop.Components.WHPanel;
import ru.m210projects.Launcher.desktop.Components.Buttons.AboutButton;
import ru.m210projects.Launcher.desktop.Components.Buttons.FrameButton;
import ru.m210projects.Launcher.desktop.Components.Buttons.GameButton;
import ru.m210projects.Launcher.desktop.Components.Buttons.SettingsButton;
import ru.m210projects.Launcher.desktop.GameEntries.BloodEntry;
import ru.m210projects.Launcher.desktop.GameEntries.DisabledEntry;
import ru.m210projects.Launcher.desktop.GameEntries.DukeEntry;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;
import ru.m210projects.Launcher.desktop.GameEntries.LSPEntry;
import ru.m210projects.Launcher.desktop.GameEntries.NamEntry;
import ru.m210projects.Launcher.desktop.GameEntries.PSlaveEntry;
import ru.m210projects.Launcher.desktop.GameEntries.RREntry;
import ru.m210projects.Launcher.desktop.GameEntries.RRRAEntry;
import ru.m210projects.Launcher.desktop.GameEntries.SWEntry;
import ru.m210projects.Launcher.desktop.GameEntries.TekwarEntry;
import ru.m210projects.Launcher.desktop.GameEntries.WH2Entry;
import ru.m210projects.Launcher.desktop.GameEntries.WHEntry;

public class JLauncher extends JFrame {

	private static final long serialVersionUID = 1L;

	private CardLayout cl_HeaderLogo;
	private CardLayout cl_RightPanel;
	private JPanel RightPanel, HeaderLogo;
	public Settings settingsPanel;
	public AboutButton about;

	public GameEntry[] entries;
	public HashMap<GameEntry, GameButton> buttonLinker;
	public GameServices services;

	public JLauncher(String title, int portableEntry, GameEntry... entry) {

		setTitle(title);
		setResizable(false);
		setLayout(new MigLayout("", "[][grow 1]", "[::100px,fill][grow,top][bottom]"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/" + iconPath + "/build32.png")));

		this.entries = entry;
		buttonLinker = new HashMap<GameEntry, GameButton>(entry.length);

		services = new GameServices(Main.platform);

		// Logos

		cl_HeaderLogo = new CardLayout();

		HeaderLogo = new JPanel();
		HeaderLogo.setBorder(null);
		add(HeaderLogo, "cell 1 0 2 1,grow");
		HeaderLogo.setLayout(cl_HeaderLogo);

		HeaderLogo.add(new ImagePanel(this.getClass().getResource("/" + headerPath + "/headerbuild.png")), "logoBuild");
		for (int i = 0; i < entry.length; i++)
			HeaderLogo.add(new ImagePanel(entry[i].getLogo()), "logo " + i);
		HeaderLogo.setPreferredSize(new Dimension(380, 100));

		// Right Panel

		cl_RightPanel = new CardLayout();
		RightPanel = new JPanel();
		RightPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		RightPanel.setLayout(cl_RightPanel);

		JPanel buttons = new JPanel();
		// buttons.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttons.setLayout(new MigLayout("", "[fill]", ""));

		for (int i = 0; i < entry.length; i++) {
			GameButton b = new GameButton(this, i);
			// b.setToolTipText(entry[i].appname);
			buttonLinker.put(entries[i], b);
			RightPanel.add(buildPanel(entry[i], portableEntry != -1), "panel " + i);
			if (portableEntry == -1) {
				buttons.add(b, "cell 0 " + i);
				b.setHorizontalAlignment(SwingConstants.LEFT);

				/*
				 * if(entry.length > 4 && i != entry.length - 1)
				 * buttons.add(Box.createVerticalGlue());
				 */
			}
		}

		RightPanel.add(settingsPanel = new Settings(this, portableEntry != -1 ? entries[portableEntry] : null),
				"Settings");
		RightPanel.add(new About(), "About");
		add(about = new AboutButton(this, "logoBuild", "About"), "cell 1 2,alignx right,growy");

		add(RightPanel, "cell 1 1,grow");

		if (portableEntry == -1) {
			add(buttons, "cell 0 0,grow");
		}
	}

	private FramePanel buildPanel(GameEntry entry, boolean isPortable) {
		if (entry instanceof TekwarEntry)
			return new TekwarPanel(this, (TekwarEntry) entry, isPortable);
		if (entry instanceof WHEntry)
			return new WHPanel(this, (WHEntry) entry, isPortable);
		if (entry instanceof WH2Entry)
			return new WH2Panel(this, (WH2Entry) entry, isPortable);
		if (entry instanceof BloodEntry)
			return new BloodPanel(this, (BloodEntry) entry, isPortable);
		if (entry instanceof DukeEntry)
			return new DukePanel(this, (DukeEntry) entry, isPortable);
		if (entry instanceof RREntry)
			return new RRPanel(this, (RREntry) entry, isPortable);
		if (entry instanceof RRRAEntry)
			return new RRRAPanel(this, (RRRAEntry) entry, isPortable);
		if (entry instanceof PSlaveEntry)
			return new PSPanel(this, (PSlaveEntry) entry, isPortable);
		if (entry instanceof LSPEntry)
			return new LSPPanel(this, (LSPEntry) entry, isPortable);
		if (entry instanceof SWEntry)
			return new SWPanel(this, (SWEntry) entry, isPortable);
		if (entry instanceof NamEntry)
			return new NamPanel(this, (NamEntry) entry, isPortable);

		if (entry instanceof DisabledEntry) {
			GameButton button = buttonLinker.get(entry);
			button.setEnabled(false);
			return new GamePanel(entry) {
				private static final long serialVersionUID = 1L;

				@Override
				public void init() {
				}

				@Override
				public void update() {
				}
			};
		}

		System.err.println("GamePanel not found for " + entry.appname);
		return null;
	}

	public void showPanel(FrameButton button) {
		button.requestFocus();
		if (button.panelName != null)
			cl_RightPanel.show(RightPanel, button.panelName);
		if (button.logoName != null)
			cl_HeaderLogo.show(HeaderLogo, button.logoName);

		if (button == about || button instanceof SettingsButton)
			about.setAction(true);
		else
			about.setAction(false);
	}

	public void showEntry(GameEntry entry) {
		if (entry == null)
			return;

		settingsPanel.setEntry(entry);
		GameButton button = buttonLinker.get(entry);
		button.requestFocus();
		cl_RightPanel.show(RightPanel, button.panelName);
		cl_HeaderLogo.show(HeaderLogo, button.logoName);

		about.setEntry(entry);
		about.setAction(false);
	}

	public void start() {
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		showPanel(about);
	}
}