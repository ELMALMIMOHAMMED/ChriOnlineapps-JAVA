package ui.panels;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.Message;
import services.SessionService;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(MainFrame frame) {

        setLayout(new GridLayout(6, 2, 10, 10));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton btnLogin = new JButton("Login");
        JButton btnBack = new JButton("Retour");

        // =========================
        // UI
        // =========================
        add(new JLabel("Email:"));
        add(emailField);

        add(new JLabel("Password:"));
        add(passwordField);

        add(new JLabel());
        add(new JLabel());

        add(btnLogin);
        add(btnBack);

        // =========================
        // LOGIN ACTION
        // =========================
        btnLogin.addActionListener(e -> {

            try {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // 🔒 VALIDATION
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Remplir tous les champs !");
                    return;
                }

                // =========================
                // REQUEST (FIX IMPORTANT)
                // =========================
                JsonObject data = new JsonObject();
                data.addProperty("email", email);
                data.addProperty("password", password);

                Message req = new Message(
                        "LOGIN",
                        "REQ_LOGIN",
                        "",
                        "",                  // ❌ data vide
                        data.toString()      // ✅ payload utilisé
                );

                Message res = frame.getClient().sendRequest(req);

                // DEBUG
                System.out.println("LOGIN RESPONSE = " + res);

                // =========================
                // RESPONSE
                // =========================
                if (res != null && "SUCCESS".equals(res.getStatus())) {

                    String raw = res.getData();

                    if (raw == null || raw.isEmpty()) {
                        throw new RuntimeException("Réponse serveur vide");
                    }

                    JsonObject json = JsonParser
                            .parseString(raw)
                            .getAsJsonObject();

                    int userId;

                    if (json.has("userId")) {
                        userId = json.get("userId").getAsInt();
                    } else if (json.has("id")) {
                        userId = json.get("id").getAsInt();
                    } else {
                        throw new RuntimeException("userId introuvable");
                    }

                    // =========================
                    // SESSION
                    // =========================
                    SessionService.setCurrentUserId(userId);

                    JOptionPane.showMessageDialog(this, "Login réussi !");
                    frame.onLoginSuccess();

                } else {
                    JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect !");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur serveur !");
            }
        });

        // =========================
        // RETOUR
        // =========================
        btnBack.addActionListener(e -> frame.showPage("HOME"));
    }
}