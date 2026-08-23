package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ParametreManagerScene implements ReceveurDeDonnees {

    private int IDEntreprise;
    private int IDCompte;

    @Override
    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
    }

    // Champs communs (Admin et Manager)
    @FXML private TextField usernameField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // Champs spécifiques au Manager
    @FXML private TitledPane managerSection;
    @FXML private ImageView logoImageView;
    @FXML private Button browseButton;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // Variable pour stocker le chemin du logo
    private String logoPath;

    // Variable pour déterminer le type d'utilisateur
    private String userType;

    @FXML private void initialize() {
        Platform.runLater(() -> {
            Personnel personnel = new Personnel();
            personnel.loadPersonnel(BDD.getConnection(), IDCompte);
            setUserType(personnel.getTache());

            Entreprise entree = new Entreprise();
            entree.loadEntreprise(BDD.getConnection(), IDEntreprise);

            logoPath = "file:/C:/xampp/htdocs" + entree.getLogo();
            logoImageView.setImage(new Image(logoPath));

            // Désactiver le bouton et l'image si l'utilisateur est Admin
            if (!userType.equals("Manager")) {
                browseButton.setDisable(true);
            }else {
                browseButton.setDisable(false);
            }
        });
    }

    public void setUserType(String userType) {
        this.userType = capitalizeFirstLetter(userType);


        // Afficher ou masquer la section Manager selon le type d'utilisateur
        if ("Admin".equals(userType)) {
            managerSection.setVisible(false);
            managerSection.setManaged(false); // Évite que l'espace soit réservé même si invisible
        } else {
            managerSection.setVisible(true);
            managerSection.setManaged(true);
        }
    }

    @FXML private void handleBrowseAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) browseButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Récupérer le nom de l'entreprise
                Entreprise entreprise = new Entreprise();
                entreprise.loadEntreprise(BDD.getConnection(), IDEntreprise);
                String entrepriseNom = entreprise.getNom().replaceAll("\\s+", "_"); // Supprimer espaces

                // Définir le chemin de destination relatif
                String relativePath = "/PFE_HOTEL/App_Images/" + entrepriseNom + "/logo.png";
                String destinationPath = "C:/xampp/htdocs" + relativePath;

                File dir = new File(new File(destinationPath).getParent());
                if (!dir.exists()) {
                    dir.mkdirs(); // Créer le dossier s'il n'existe pas
                }

                // Copier l'image et forcer le format PNG
                Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour uniquement le chemin relatif
                logoPath = relativePath; // Chemin enregistré dans la base de données
                logoImageView.setImage(new Image("file:/C:/xampp/htdocs" + logoPath));
                entreprise.updateLogo(BDD.getConnection(),entreprise.getID_Entreprise(BDD.getConnection()), logoPath);

            } catch (Exception e) {
                showAlert("Erreur lors de la sauvegarde de l'image.");
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        if (usernameField.getText().trim().isEmpty()) {
            showAlert("Le nom d'utilisateur ne peut pas être vide");
            return false;
        }

        if (oldPasswordField.getText().trim().isEmpty()) {
            showAlert("L'ancien mot de passe est requis");
            return false;
        }

        if (!newPasswordField.getText().isEmpty()) {
            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("Les nouveaux mots de passe ne correspondent pas");
                return false;
            }
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void handleSaveAction() {
        if (validateInputs()) {
            try {
                // Mise à jour des informations du compte
                Compte compte = new Compte(BDD.getConnection(), IDCompte);
                compte.updateCompte(IDCompte, usernameField.getText(), oldPasswordField.getText(), newPasswordField.getText());

                if ("Manager".equals(userType)) {
                    Entreprise entreprise = new Entreprise();
                    entreprise.updateLogo(BDD.getConnection(), IDEntreprise, logoPath);
                }

                // Message de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));
                alert.setContentText("Les modifications ont été enregistrées avec succès");
                alert.showAndWait();
            } catch (Exception e) {
                showAlert("Une erreur s'est produite lors de l'enregistrement des données.");
            }
        }
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}