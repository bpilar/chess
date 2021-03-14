package base.games;

import base.games.screens.BodyScreen;
import base.games.screens.StartScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppWindow extends JFrame implements ActionListener {
    public static int width = 800;
    public static int height = 400;

    public JFrame mainFrame;
    public Container mainContentPane;
    public JPanel header;
    public JPanel body;
    public BodyScreen currentScreen;

    public JButton quitButton = new JButton("QUIT");

    public Connection conn = null;
    public boolean connected = false;

    public AppWindow() {

        mainFrame = new JFrame("Bazunia");
        mainContentPane = mainFrame.getContentPane();
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(width,height);
        mainFrame.setLayout(new BorderLayout());
        header = new JPanel();
        body = new JPanel();
        header.setBorder(BorderFactory.createEtchedBorder());
        body.setBorder(BorderFactory.createEtchedBorder());
        header.setLayout(new BorderLayout());
        body.setLayout(new GridBagLayout());

        mainContentPane.add(BorderLayout.NORTH,header);
        mainContentPane.add(BorderLayout.CENTER,body);
        quitButton.addActionListener(this);
        header.add(BorderLayout.CENTER,quitButton);
        mainFrame.setVisible(true);

            System.out.println("Created AppWindow");

        switchCurrentScreenTo(new StartScreen(this, currentScreen));

//        doAnything();
    }

    public void disconnectOracle() {
        currentScreen.getDisplayPanel().add(new JLabel("Disconnecting:"));
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                try {
                    conn.close();
                    connected = false;
                    currentScreen.getDisplayPanel().add(new JLabel("disconnected"));
                    System.out.println("Rozłączono z bazą danych");
                } catch (SQLException ex) {
                    currentScreen.getDisplayPanel().add(new JLabel("failed"));
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                body.updateUI();
            }
        });
    }

    public void doAnything() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                try (CallableStatement stmt = conn.prepareCall("{? = call PracNazw(?,?)}")){
                    stmt.setInt(2, 100);
                    stmt.registerOutParameter(1, Types.INTEGER);
                    stmt.registerOutParameter(3, Types.VARCHAR);
                    stmt.execute();
                    if(stmt.getInt(1)==1) {
                        System.out.println("Done: " + stmt.getString(3));
                    }
                    else {
                        System.out.println("Err...");
                    }
                } catch (SQLException ex) {
                    System.out.println("Błąd wykonania polecenia: "+ ex.getMessage());
                }
                body.updateUI();
            }
        });
    }

    public void switchCurrentScreenTo(BodyScreen scr) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                body.removeAll();
                body.add(scr.getDisplayPanel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                currentScreen = scr;
                body.updateUI();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();

        if(object == quitButton)
        {
            if(connected) disconnectOracle();
            System.exit(0);
        }
    }
}