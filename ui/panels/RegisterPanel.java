package ui.panels;

import javax.swing.*;
import java.awt.*;

import com.google.gson.JsonObject;
import common.Message;
import ui.MainFrame;

public class RegisterPanel extends JPanel {

    private MainFrame frame;

    public RegisterPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new GridLayout(7, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton btnRegister = new JButton("Créer compte");
        JButton btnBack = new JButton("Retour");

        // =========================
        // UI
        // =========================
        add(new JLabel("Nom:"));
        add(nameField);

        add(new JLabel("Email:"));
        add(emailField);

        add(new JLabel("Téléphone:"));
        add(phoneField);

        add(new JLabel("Mot de passe:"));
        add(passwordField);

        add(new JLabel());
        add(new JLabel());

        add(btnRegister);
        add(btnBack);

        // =========================
        // REGISTER ACTION
        // =========================
        btnRegister.addActionListener(e -> {

            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // 🔒 VALIDATION
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Remplir tous les champs !");
                    return;
                }

                // =========================
                // JSON
                // =========================
                JsonObject data = new JsonObject();
                data.addProperty("name", name);
                data.addProperty("email", email);
                data.addProperty("phone", phone);
                data.addProperty("password", password);

                // =========================
                // REQUEST (FIX IMPORTANT)
                // =========================
                Message request = new Message(
                        "REGISTER",
                        "REQ_REGISTER",
                        "",
                        "",                  // ❌ data vide
                        data.toString()      // ✅ payload
                );

                Message response = frame.getClient().sendRequest(request);

                // DEBUG
                System.out.println("REGISTER RESPONSE = " + response);

                if (response != null) {
                    System.out.println("STATUS = " + response.getStatus());
                    System.out.println("DATA = " + response.getData());
                }

                // =========================
                // RESPONSE
                // =========================
                if (response != null && "SUCCESS".equals(response.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Compte créé avec succès !");
                    frame.showPage("LOGIN");
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur inscription !");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur serveur !");
            }
        });

        // =========================
        // BACK
        // =========================
        btnBack.addActionListener(e -> frame.showPage("HOME"));
    }
}