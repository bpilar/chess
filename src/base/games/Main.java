package base.games;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Runnable app = new Runnable() {
            public void run()
            {
                new AppWindow();
            }
        };

        SwingUtilities.invokeLater(app);
    }
}
