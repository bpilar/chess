package base.games.screens;

import base.games.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorScreen implements BodyScreen, ActionListener {
    public JPanel displayPanel = new JPanel();
    public AppWindow parent;
    public BodyScreen previousScreen;
    public JButton returnButton = new JButton("RETURN");
    public JPanel centerPanel = new JPanel();
    public ErrorScreen(AppWindow app, BodyScreen previous, String msg) {
        parent = app;
        previousScreen = previous;
        displayPanel.setLayout(new BorderLayout());
        returnButton.addActionListener(this);
        displayPanel.add(BorderLayout.SOUTH,returnButton);
        displayPanel.add(BorderLayout.CENTER,centerPanel);
        centerPanel.add(new JLabel("ta operacja jest niepoprawna  "));
        centerPanel.add(new JLabel(msg));
        centerPanel.setBackground(Color.CYAN);

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
