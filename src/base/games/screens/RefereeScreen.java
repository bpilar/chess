package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefereeScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public RefereeScreen(AppWindow app, BodyScreen previous) {
        parent = app;
        previousScreen = previous;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        centerPanel.add(new JLabel("referee panel"));
        centerPanel.setBackground(Color.RED);

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

        if (object == returnButton)
        {
            parent.switchCurrentScreenTo(previousScreen);
        }
    }
}
