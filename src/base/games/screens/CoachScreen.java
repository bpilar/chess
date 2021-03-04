package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CoachScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel;
    public AppWindow parent;
    public BodyScreen previousScreen;
    public CoachScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
        displayPanel = new JPanel();
        displayPanel.add(new JLabel("coach panel"));
        displayPanel.setBackground(Color.CYAN);

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
