package ui.panels;

import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel(MainFrame frame) {

        setLayout(new BorderLayout());

        // =========================
        // TITLE
        // =========================
        JLabel title = new JLabel("Bienvenue dans E-Commerce App", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // =========================
        // CENTER PANEL (FIX ALIGNMENT)
        // =========================
        JPanel centerWrapper = new JPanel(new GridBagLayout()); // 🔥 CENTRAGE PRO
        JPanel panelButtons = new JPanel(new GridLayout(3, 1, 15, 15));

        panelButtons.setPreferredSize(new Dimension(250, 200));

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        JButton btnProducts = new JButton("Voir Produits");

        panelButtons.add(btnLogin);
        panelButtons.add(btnRegister);
        panelButtons.add(btnProducts);

        centerWrapper.add(panelButtons);

        // =========================
        // ADD
        // =========================
        add(title, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        // =========================
        // ACTIONS
        // =========================
        btnLogin.addActionListener(e -> frame.showPage("LOGIN"));

        btnRegister.addActionListener(e -> {
            System.out.println("CLICK REGISTER"); // 🔍 DEBUG
            frame.showPage("REGISTER");
        });

        btnProducts.addActionListener(e -> frame.showPage("PRODUCTS"));
    }
}