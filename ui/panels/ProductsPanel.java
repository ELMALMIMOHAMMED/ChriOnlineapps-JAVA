package ui.panels;

import models.Produit;
import services.ProduitService;
import services.PanierService;
import services.SessionService;
import ui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public ProductsPanel(MainFrame frame) {

        setLayout(new BorderLayout());

        // =========================
        // TITLE
        // =========================
        JLabel title = new JLabel("Liste des Produits", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        // =========================
        // TABLE
        // =========================
        model = new DefaultTableModel(
                new Object[]{"ID", "Nom", "Prix", "Stock"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);

        // =========================
        // BUTTONS
        // =========================
        JPanel bottomPanel = new JPanel();

        JButton btnAdd = new JButton("Ajouter au panier");
        JButton btnCart = new JButton("Voir Panier");
        JButton btnBack = new JButton("Retour");

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnCart);
        bottomPanel.add(btnBack);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // =========================
        // LOAD DATA
        // =========================
        loadProduits();

        // =========================
        // ACTIONS
        // =========================
        btnBack.addActionListener(e -> frame.showPage("HOME"));

        btnCart.addActionListener(e -> {
            frame.refreshCart();
            frame.showPage("CART");
        });

        btnAdd.addActionListener(e -> ajouterAuPanier(frame));
    }

    // =========================
    // AJOUT AU PANIER
    // =========================
    private void ajouterAuPanier(MainFrame frame) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un produit !");
            return;
        }

        // 🔐 Vérifier login
        if (!SessionService.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Veuillez vous connecter !");
            frame.showPage("LOGIN");
            return;
        }

        int produitId = (int) model.getValueAt(selectedRow, 0);
        int stock = (int) model.getValueAt(selectedRow, 3);

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "Produit en rupture !");
            return;
        }

        try {
            PanierService panierService = new PanierService();

            boolean success = panierService.ajouterProduit(produitId, 1);

            if (success) {
                JOptionPane.showMessageDialog(this, "Produit ajouté au panier !");
                reload();
                frame.refreshCart();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout !");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur système !");
        }
    }

    // =========================
    // RELOAD
    // =========================
    public void reload() {
        loadProduits();
    }

    // =========================
    // LOAD PRODUITS
    // =========================
    private void loadProduits() {

        ProduitService service = new ProduitService();
        List<Produit> produits = service.getAllProduits();

        model.setRowCount(0);

        if (produits == null || produits.isEmpty()) {
            System.out.println("⚠ Aucun produit trouvé !");
            return;
        }

        for (Produit p : produits) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNom(),
                    p.getPrix(),
                    p.getStock()
            });
        }
    }
}