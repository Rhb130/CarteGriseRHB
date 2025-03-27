package views;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Posseder;

public class PossederCRUDView extends JFrame {

    // Liste pour stocker les enregistrements de type "Posseder"
    private List<Posseder> possederList = new ArrayList<>();

    // Table pour afficher les données
    private JTable table;

    // Modèle de table pour gérer les données affichées dans la JTable
    private DefaultTableModel tableModel;

    // Format de date utilisé pour l'affichage et la saisie
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    // Objet pour parser et formater les dates
    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat(DATE_FORMAT);

    // Constructeur de la classe
    public PossederCRUDView() {
        // Configuration de la fenêtre principale
        setTitle("Gestion des Propriétaires et Véhicules"); // Titre de la fenêtre
        setSize(600, 400); // Taille de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fermer l'application quand la fenêtre est fermée
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        setLayout(new BorderLayout()); // Utiliser un BorderLayout pour organiser les composants

        // Initialisation de la table avec des colonnes prédéfinies
        String[] columnNames = {"Prénom et Nom", "Matricule", "Date Début", "Date Fin"};
        tableModel = new DefaultTableModel(columnNames, 0); // Modèle de table avec 0 ligne initiale
        table = new JTable(tableModel); // Création de la JTable avec le modèle
        add(new JScrollPane(table), BorderLayout.CENTER); // Ajouter la table dans un JScrollPane pour le défilement

        // Panneau pour les boutons en bas de la fenêtre
        JPanel buttonPanel = new JPanel();

        // Bouton "Ajouter"
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> openAddDialog()); // Ouvrir la boîte de dialogue pour ajouter un enregistrement
        buttonPanel.add(addButton);

        // Bouton "Modifier"
        JButton updateButton = new JButton("Modifier");
        updateButton.addActionListener(e -> openUpdateDialog()); // Ouvrir la boîte de dialogue pour modifier un enregistrement
        buttonPanel.add(updateButton);

        // Bouton "Supprimer"
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> deleteSelectedRecord()); // Supprimer l'enregistrement sélectionné
        buttonPanel.add(deleteButton);

        // Ajouter le panneau de boutons en bas de la fenêtre
        add(buttonPanel, BorderLayout.SOUTH);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    // Méthode pour ouvrir la boîte de dialogue d'ajout
    private void openAddDialog() {
        // Création d'une boîte de dialogue modale (bloque la fenêtre principale)
        JDialog addDialog = new JDialog(this, "Ajouter un enregistrement", true);
        addDialog.setSize(300, 250); // Taille de la boîte de dialogue
        addDialog.setLayout(new GridLayout(5, 2)); // Utiliser un GridLayout pour organiser les composants
        addDialog.setLocationRelativeTo(this); // Centrer la boîte de dialogue par rapport à la fenêtre principale

        // Champs de saisie pour les données
        JTextField ownerField = new JTextField(); // Champ pour le prénom et nom
        JTextField vehicleField = new JTextField(); // Champ pour le matricule
        JTextField startDateField = new JTextField(); // Champ pour la date de début
        JTextField endDateField = new JTextField(); // Champ pour la date de fin

        // Ajouter les labels et les champs de saisie à la boîte de dialogue
        addDialog.add(new JLabel("Prénom et Nom :"));
        addDialog.add(ownerField);

        addDialog.add(new JLabel("Matricule :"));
        addDialog.add(vehicleField);

        addDialog.add(new JLabel("Date Début (" + DATE_FORMAT + ") :"));
        addDialog.add(startDateField);

        addDialog.add(new JLabel("Date Fin (" + DATE_FORMAT + ") :"));
        addDialog.add(endDateField);

        // Bouton "Enregistrer" pour valider l'ajout
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            // Récupérer les valeurs saisies
            String owner = ownerField.getText();
            String vehicle = vehicleField.getText();
            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();

            try {
                // Parser les dates saisies
                Date startDate = DATE_PARSER.parse(startDateStr);
                Date endDate = endDateStr.isEmpty() ? null : DATE_PARSER.parse(endDateStr);

                // Vérifier si l'enregistrement existe déjà
                boolean exists = possederList.stream()
                        .anyMatch(p -> p.getIdProprietaire() == owner.hashCode() && p.getIdVehicule() == vehicle.hashCode());

                if (!exists) {
                    // Ajouter le nouvel enregistrement à la liste et à la table
                    possederList.add(new Posseder(owner.hashCode(), vehicle.hashCode(), startDate, endDate));
                    tableModel.addRow(new Object[]{owner, vehicle, startDateStr, endDateStr});
                    addDialog.dispose(); // Fermer la boîte de dialogue
                } else {
                    // Afficher un message d'erreur si l'enregistrement existe déjà
                    JOptionPane.showMessageDialog(addDialog, "Enregistrement déjà existant !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {
                // Afficher un message d'erreur si le format de date est invalide
                JOptionPane.showMessageDialog(addDialog, "Format de date invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ajouter un espace vide et le bouton "Enregistrer" à la boîte de dialogue
        addDialog.add(new JLabel()); // Espacement
        addDialog.add(saveButton);

        // Rendre la boîte de dialogue visible
        addDialog.setVisible(true);
    }

    // Méthode pour ouvrir la boîte de dialogue de modification
    private void openUpdateDialog() {
        // Vérifier si une ligne est sélectionnée dans la table
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Création d'une boîte de dialogue modale pour la modification
        JDialog updateDialog = new JDialog(this, "Modifier un enregistrement", true);
        updateDialog.setSize(300, 250);
        updateDialog.setLayout(new GridLayout(5, 2));
        updateDialog.setLocationRelativeTo(this);

        // Récupérer les données de la ligne sélectionnée
        String owner = (String) tableModel.getValueAt(selectedRow, 0);
        String vehicle = (String) tableModel.getValueAt(selectedRow, 1);
        String startDate = (String) tableModel.getValueAt(selectedRow, 2);
        String endDate = (String) tableModel.getValueAt(selectedRow, 3);

        // Pré-remplir les champs de saisie avec les données existantes
        JTextField ownerField = new JTextField(owner);
        JTextField vehicleField = new JTextField(vehicle);
        JTextField startDateField = new JTextField(startDate);
        JTextField endDateField = new JTextField(endDate);

        // Ajouter les labels et les champs de saisie à la boîte de dialogue
        updateDialog.add(new JLabel("Prénom et Nom :"));
        updateDialog.add(ownerField);

        updateDialog.add(new JLabel("Matricule :"));
        updateDialog.add(vehicleField);

        updateDialog.add(new JLabel("Date Début (" + DATE_FORMAT + ") :"));
        updateDialog.add(startDateField);

        updateDialog.add(new JLabel("Date Fin (" + DATE_FORMAT + ") :"));
        updateDialog.add(endDateField);

        // Bouton "Enregistrer" pour valider les modifications
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            // Récupérer les nouvelles valeurs saisies
            String newOwner = ownerField.getText();
            String newVehicle = vehicleField.getText();
            String newStartDateStr = startDateField.getText();
            String newEndDateStr = endDateField.getText();

            try {
                // Parser les nouvelles dates
                Date newStartDate = DATE_PARSER.parse(newStartDateStr);
                Date newEndDate = newEndDateStr.isEmpty() ? null : DATE_PARSER.parse(newEndDateStr);

                // Vérifier si l'enregistrement existe déjà (sauf s'il s'agit de la même ligne)
                boolean exists = possederList.stream()
                        .anyMatch(p -> p.getIdProprietaire() == newOwner.hashCode() && p.getIdVehicule() == newVehicle.hashCode());

                if (!exists || (owner.equals(newOwner) && vehicle.equals(newVehicle))) {
                    // Mettre à jour les données dans la liste et la table
                    possederList.get(selectedRow).setIdProprietaire(newOwner.hashCode());
                    possederList.get(selectedRow).setIdVehicule(newVehicle.hashCode());
                    possederList.get(selectedRow).setDateDebutPropriete(newStartDate);
                    possederList.get(selectedRow).setDateFinPropriete(newEndDate);

                    tableModel.setValueAt(newOwner, selectedRow, 0);
                    tableModel.setValueAt(newVehicle, selectedRow, 1);
                    tableModel.setValueAt(newStartDateStr, selectedRow, 2);
                    tableModel.setValueAt(newEndDateStr, selectedRow, 3);

                    updateDialog.dispose(); // Fermer la boîte de dialogue
                } else {
                    // Afficher un message d'erreur si l'enregistrement existe déjà
                    JOptionPane.showMessageDialog(updateDialog, "Enregistrement déjà existant !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {
                // Afficher un message d'erreur si le format de date est invalide
                JOptionPane.showMessageDialog(updateDialog, "Format de date invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ajouter un espace vide et le bouton "Enregistrer" à la boîte de dialogue
        updateDialog.add(new JLabel()); // Espacement
        updateDialog.add(saveButton);

        // Rendre la boîte de dialogue visible
        updateDialog.setVisible(true);
    }

    // Méthode pour supprimer un enregistrement sélectionné
    private void deleteSelectedRecord() {
        // Vérifier si une ligne est sélectionnée dans la table
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Supprimer l'enregistrement de la liste et de la table
        possederList.remove(selectedRow);
        tableModel.removeRow(selectedRow);
    }

    // Méthode principale pour lancer l'application
    public static void main(String[] args) {
        new PossederCRUDView(); // Créer une instance de la fenêtre principale
    }
}