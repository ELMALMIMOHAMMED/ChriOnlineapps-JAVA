package ui;

import javax.swing.*;
import java.awt.*;

import network.Client;
import ui.panels.*;

public class MainFrame extends JFrame {

    private CardLayout layout;
    private JPanel container;

    private CartPanel cartPanel;
    private ProductsPanel productsPanel;

    private Client client;

    // page demandée après login
    private String pendingPage = null;

    public MainFrame() {

        setTitle("E-Commerce App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // =========================
        // CLIENT
        // =========================
        client = new Client("127.0.0.1", 6000);

        // =========================
        // LAYOUT
        // =========================
        layout = new CardLayout();
        container = new JPanel(layout);

        // =========================
        // PANELS
        // =========================
        LoginPanel loginPanel = new LoginPanel(this);
        HomePanel homePanel = new HomePanel(this);
        productsPanel = new ProductsPanel(this);
        cartPanel = new CartPanel(this);
        RegisterPanel registerPanel = new RegisterPanel(this);

        // =========================
        // ADD PAGES
        // =========================
        container.add(loginPanel, "LOGIN");
        container.add(homePanel, "HOME");
        container.add(productsPanel, "PRODUCTS");
        container.add(cartPanel, "CART");
        container.add(registerPanel, "REGISTER");
        add(container);

        // =========================
        // START
        // =========================
        showPage("HOME");
    }

    // =========================
    // NAVIGATION
    // =========================
    public void showPage(String name) {

        // 🔥 Sécurité null
        if (name == null) return;

        switch (name) {

            case "CART":
                if (cartPanel != null) {
                    cartPanel.reload();
                }
                break;

            case "PRODUCTS":
                if (productsPanel != null) {
                    productsPanel.reload();
                }
                break;
        }

        layout.show(container, name);

        // 🔥 refresh UI (évite bugs affichage)
        container.revalidate();
        container.repaint();
    }

    // =========================
    // LOGIN À LA DEMANDE
    // =========================
    public void requireLogin(String targetPage) {
        this.pendingPage = targetPage;
        showPage("LOGIN");
    }

    public void onLoginSuccess() {

        if (pendingPage != null) {
            showPage(pendingPage);
            pendingPage = null;
        } else {
            showPage("HOME");
        }
    }

    // =========================
    // CLIENT
    // =========================
    public Client getClient() {
        return client;
    }

    // =========================
    // REFRESH
    // =========================
    public void refreshCart() {
        if (cartPanel != null) {
            cartPanel.reload();
        }
    }

    public void refreshProducts() {
        if (productsPanel != null) {
            productsPanel.reload();
        }
    }
}