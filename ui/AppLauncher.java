package ui;

public class AppLauncher {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}