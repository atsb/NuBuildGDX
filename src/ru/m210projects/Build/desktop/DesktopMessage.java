// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.desktop;

import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import ru.m210projects.Build.Architecture.BuildFrame;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage;

public class DesktopMessage implements BuildMessage {
	private JOptionPane panel;
	private BuildFrame frame;
	private boolean update;

	public DesktopMessage(boolean update)
	{
		this.update = update;
	}

	@Override
	public void setFrame(BuildFrame frame) {
		this.frame = frame;
	}

	@Override
	public synchronized boolean show(String header, String message, MessageType type) {
		if(panel == null && (panel = InitPanel()) == null)
			return false;

		BuildGdx.input.setCursorCatched(false);

		switch(type)
		{
		case Question:
		case Crash:
			if(type == MessageType.Crash) {
				ShowPanel(JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION, header, message + "\r\n \r\n      Do you want to send a crash report?");
			} else {
				ShowPanel(JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION, header, message);
			}

	        Object selectedValue = panel.getValue();
	        if (selectedValue instanceof Integer) {
	        	if(((Integer)selectedValue).intValue() == JOptionPane.YES_OPTION)
					return true;
            }

			return false;
		case Info:
			ShowPanel(JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, header, message);
			return false;
		}

		return false;
	}

	protected JDialog ShowPanel(int messageType, int optionType, String header, String message)
	{
		panel.setMessageType(messageType);
		panel.setMessage(message);
		panel.setOptionType(optionType);
		final JDialog dialog = panel.createDialog(header);
		if(frame.icon != null)
			dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(frame.icon));
		panel.setBackground(dialog.getBackground());
		dialog.setLocation(frame.getX() + (BuildGdx.graphics.getWidth() - dialog.getWidth()) / 2,
				frame.getY() + (BuildGdx.graphics.getHeight() - dialog.getHeight()) / 2);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		dialog.dispose();

		return dialog;
	}

	protected JOptionPane InitPanel()
	{
		JOptionPane frame = null;
		try {
			frame = new JOptionPane();
			frame.setMessageType(JOptionPane.INFORMATION_MESSAGE);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { e.printStackTrace(); }
		return frame;
	}

	@Override
	public void dispose() {
		panel = null;
	}
}
