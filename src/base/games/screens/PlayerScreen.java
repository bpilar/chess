package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel;
    public AppWindow parent;
    public BodyScreen previousScreen;
    public PlayerScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
        displayPanel = new JPanel();
        displayPanel.add(new JLabel("player panel"));
        displayPanel.setBackground(Color.GREEN);

    }

    @Override
    public JPanel getDisplayPanel() {
        return displayPanel;
    }

    @Override
    public BodyScreen getPreviousScreen() {
        return previousScreen;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();


    }
}
