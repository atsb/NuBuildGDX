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

import static ru.m210projects.Build.Render.VideoMode.getmode;
import static ru.m210projects.Build.Render.VideoMode.getmodeindex;
import static ru.m210projects.Build.Render.VideoMode.strvmodes;
import static ru.m210projects.Build.Render.VideoMode.validmodes;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Graphics.DisplayMode;

import net.miginfocom.swing.MigLayout;
import ru.m210projects.Build.Audio.BuildAudio;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.DummySound;
import ru.m210projects.Build.FileHandle.Compat;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.desktop.DesktopFactory;
import ru.m210projects.Build.desktop.audio.midi.MidiMusicModule;
import ru.m210projects.Launcher.desktop.JLauncher;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;

public class Settings extends FramePanel {

	private static final long serialVersionUID = 1L;
//	private JLauncher main;
	public GameEntry entry;
	public GameEntry portableEntry;
	public BuildConfig portableConfig;

	public JComboBox<String> comboSound;
	public JComboBox<String> comboResolution;
	public JComboBox<String> comboMidi;

	public JComboBox<String> comboRenderer;
	public JCheckBox chckbxFullscreen;
	public JCheckBox chckbxBorderless;
	public JCheckBox chckbxEnableAutoloadFolder;
	public JCheckBox chckbxUseHomeFolder;
	public JCheckBox chckbxAlwaysShowThis;
	public JButton btnOpenFolder;
//	public JButton backButton;
	public JTextField Soundbox;

	public Settings(JLauncher main, GameEntry portableEntry) {
		super(true);
		this.portableEntry = portableEntry;
//		this.main = main;

		if (portableEntry != null) {
			this.entry = portableEntry;
			portableConfig = portableEntry.getConfig();
			if (portableConfig.userfolder)
				checkConfig(true);
		}
	}

	@Override
	public void init() {
		DesktopFactory.InitVideoModes();

		this.setLayout(new MigLayout("", "[left][grow]", "[][][][][][][][]"));
		this.add(new JLabel("Resolution:"), "cell 0 0");

		comboResolution = new JComboBox<String>();
		for (int i = 0; i < validmodes.size(); i++)
			comboResolution.addItem(strvmodes[i]);
		comboResolution.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					DisplayMode mode = getmode(comboResolution.getSelectedIndex());
					entry.getConfig().ScreenWidth = mode.width;
					entry.getConfig().ScreenHeight = mode.height;
					chckbxFullscreen.setEnabled(true);
				}
			}
		});
		this.add(comboResolution, "cell 1 0,growx");

		this.add(new JLabel("Renderer:"), "cell 0 1");

		comboRenderer = new JComboBox<String>();
		comboRenderer.addItem(RenderType.Software.getName());
		comboRenderer.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					switch (comboRenderer.getSelectedIndex()) {
					case 0:
						entry.getConfig().renderType = RenderType.Software;
						break;
					}
				}
			}
		});

		this.add(comboRenderer, "cell 1 1,growx");

		chckbxFullscreen = new JCheckBox("Fullscreen");
		chckbxFullscreen.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1)
					entry.getConfig().fullscreen = 1;
				else
					entry.getConfig().fullscreen = 0;
			}
		});
		this.add(chckbxFullscreen, "flowx,cell 1 2,alignx right");

		chckbxBorderless = new JCheckBox("Borderless");
		chckbxBorderless.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1)
					entry.getConfig().borderless = true;
				else
					entry.getConfig().borderless = false;
			}
		});
		this.add(chckbxBorderless, "cell 1 2,alignx right");

		this.add(new JLabel("Sound:"), "cell 0 3");

		comboSound = new JComboBox<String>();

		List<String> names = new ArrayList<String>();
		BuildAudio.getDeviceslList(Driver.Sound, names);
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equals(DummySound.name))
				comboSound.addItem("None");
			else
				comboSound.addItem(names.get(i));
		}
		comboSound.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					entry.getConfig().snddrv = comboSound.getSelectedIndex();
				}
			}
		});
		this.add(comboSound, "cell 1 3,growx");

		this.add(new JLabel("Midi:"), "cell 0 4");

		comboMidi = new JComboBox<String>();
		comboMidi.addItem("None");
		List<MidiDevice> devices = MidiMusicModule.getDevices();
		for (int i = 0; i < devices.size(); i++) {
			String name = devices.get(i).getDeviceInfo().getName();
			comboMidi.addItem(name);
		}

		comboMidi.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int midiDevice = comboMidi.getSelectedIndex();
					if (midiDevice != 0) {
						entry.getConfig().midiSynth = midiDevice - 1;
						entry.getConfig().middrv = 1;
					} else {
						entry.getConfig().midiSynth = 1;
						entry.getConfig().middrv = 0;
					}
				}
			}
		});
		this.add(comboMidi, "cell 1 4,growx");

		this.add(new JLabel("SoundFont:"), "cell 0 5");

		Soundbox = new JTextField();
		Soundbox.setToolTipText("Path to soundbank file");
		Soundbox.setEditable(false);
		this.add(Soundbox, "flowx,cell 1 5,growx");

		JButton browseSoundbox = new JButton("...");
		browseSoundbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Soundbox.setText("");
				entry.getConfig().soundBank = null;

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setCurrentDirectory(new File(GameEntry.getDirPath()));
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SoundFont bank (*.sf2)", "sf2"));
				fileChooser
						.addChoosableFileFilter(new FileNameExtensionFilter("Downloadable Sounds bank (*.dls)", "dls"));

				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (file != null) {
						Soundbox.setText(file.getAbsolutePath());
						entry.getConfig().soundBank = file;
					}
				}
			}
		});
		this.add(browseSoundbox, "cell 1 5");

		JButton clearSoundbox = new JButton("x");
		clearSoundbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Soundbox.setText("");
				entry.getConfig().soundBank = null;
			}
		});
		this.add(clearSoundbox, "cell 1 5");

		btnOpenFolder = new JButton("Open folder...");
		btnOpenFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					File file = new File(entry.getConfig().cfgPath);
					Desktop desktop = Desktop.getDesktop();
					desktop.open(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		this.add(btnOpenFolder, "flowx,cell 0 6 2 1,alignx left");

		chckbxUseHomeFolder = new JCheckBox("Use \"home\" directory");
		chckbxUseHomeFolder.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkConfig(e.getStateChange() == 1);
				update();
			}
		});
		this.add(chckbxUseHomeFolder, "cell 0 6 2 1");

		Box settingsBox = Box.createVerticalBox();

		chckbxEnableAutoloadFolder = new JCheckBox("Enable \"autoload\" folder");
		chckbxEnableAutoloadFolder.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				entry.getConfig().autoloadFolder = e.getStateChange() == 1;
			}
		});
		settingsBox.add(chckbxEnableAutoloadFolder);

		chckbxAlwaysShowThis = new JCheckBox("Always show this window at startup");
		chckbxAlwaysShowThis.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				entry.getConfig().startup = e.getStateChange() == 1;
			}
		});
		settingsBox.add(chckbxAlwaysShowThis);

		this.add(settingsBox, "cell 0 7 2 1");

	}

	public void checkConfig(boolean userHome) {
		if (portableEntry != null && portableEntry == entry) {
			if (portableConfig.userfolder != (userHome)) {
				portableConfig.userfolder = userHome;
				// ����� ��������� cfg.userfolder ���� � ����
				portableConfig.saveConfig(new Compat(null, null), portableConfig.path);
			}

			if (userHome) {
				BuildConfig cfg = portableEntry.buildConfig(portableEntry.getHomePath());
				cfg.userfolder = true;
			} else {
				portableEntry.setConfig(portableConfig);
			}
		}
	}

	@Override
	public void update() {
		BuildConfig cfg = entry.getConfig();

		int xdim = cfg.ScreenWidth;
		int ydim = cfg.ScreenHeight;
		int index = getmodeindex(xdim, ydim);
		if (index == -1) {
			comboResolution.setEditable(true);
			try {
				chckbxFullscreen.setEnabled(false);
				comboResolution.setSelectedItem(xdim + " x " + ydim + " 32bpp");
			} catch (Exception e) {
			}
			comboResolution.setEditable(false);
		} else {
			comboResolution.setSelectedIndex(index);
			chckbxFullscreen.setEnabled(true);
		}

		if (cfg.renderType == RenderType.Software)
			comboRenderer.setSelectedIndex(0);
		else {
			// unknown renderer
			cfg.renderType = RenderType.Software;
			comboRenderer.setSelectedIndex(0);
		}

		chckbxFullscreen.setSelected(cfg.fullscreen == 1);

		if (cfg.snddrv >= 0 && cfg.snddrv < comboSound.getItemCount())
			comboSound.setSelectedIndex(cfg.snddrv);
		else
			comboSound.setSelectedIndex(0);

		if (cfg.middrv != 0) {
			if (cfg.midiSynth + 1 >= comboMidi.getItemCount())
				cfg.midiSynth = 0;
			comboMidi.setSelectedIndex(cfg.midiSynth + 1);
		} else
			comboMidi.setSelectedIndex(0);

		chckbxEnableAutoloadFolder.setSelected(cfg.autoloadFolder);

		boolean canUseHomeFolder = portableEntry != null && portableEntry == entry;

		chckbxUseHomeFolder.setEnabled(canUseHomeFolder);
		chckbxUseHomeFolder.setSelected(portableEntry == null || (canUseHomeFolder && portableConfig.userfolder));

		btnOpenFolder.setEnabled(!canUseHomeFolder || (portableConfig.userfolder));

		if (!btnOpenFolder.isEnabled())
			btnOpenFolder.setText("Portable mode");
		else
			btnOpenFolder.setText("Open folder...");

		chckbxAlwaysShowThis.setEnabled(portableEntry != null);
		chckbxAlwaysShowThis.setSelected(cfg.startup);

		chckbxBorderless.setSelected(cfg.borderless);

		if (cfg.soundBank != null)
			Soundbox.setText(cfg.soundBank.getAbsolutePath());
		else
			Soundbox.setText("");
	}

	public void setEntry(GameEntry entry) {
		this.entry = entry;
	}

}
