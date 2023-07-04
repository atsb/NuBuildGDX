package ru.m210projects.Launcher.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

class PopupActionListener implements ActionListener {
  public void actionPerformed(ActionEvent actionEvent) {
    System.out.println("Selected: " + actionEvent.getActionCommand());
  }
}

class MyPopupMenuListener implements PopupMenuListener {
  public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
    System.out.println("Canceled");
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
    System.out.println("Becoming Invisible");
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
    System.out.println("Becoming Visible");
  }
}