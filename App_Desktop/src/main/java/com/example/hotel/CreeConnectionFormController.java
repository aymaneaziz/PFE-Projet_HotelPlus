package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;

public class CreeConnectionFormController {
    @FXML private StackPane MainPane;
    @FXML private TextField FirstName;
    @FXML private TextField LastName;
    @FXML private DatePicker DateOfBirth;
    @FXML private TextField Username;
    @FXML private PasswordField Password;
    @FXML private PasswordField ConfirmPassword;
    @FXML private Label ErrorMessage;

    private Connection connection;
    private Entreprise entreprise;
    private Hotel hotel;

    @FXML public void initialize() {
        ErrorMessage.setVisible(false);
        connection = BDD.getConnection();

        Platform.runLater(() -> {
            if (MainPane != null) {
                Stage stage = (Stage) MainPane.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    if (!confirmCancel()) {
                        event.consume();
                    } else {
                        closeDatabaseConnection();
                        stage.close();
                    }
                });
            }
        });
    }

    public void setData(Entreprise entreprise, Hotel hotel) {
        this.entreprise = entreprise;
        this.hotel = hotel;
    }

    @FXML private boolean confirmCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation d'annulation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler ?");
        alert.setContentText("Cela supprimera toutes les données que vous avez saisies.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Cancel();
            return true;
        } else {
            System.out.println("Annulation annulée.");
            return false;
        }
    }

    private void Cancel() {
        File tmpDir = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entreprise.getNom());

        if (FileUtils.deleteDirectory(tmpDir)) {
            System.out.println("Dossier supprimé avec succès.");
        } else {
            System.err.println("Échec de la suppression du dossier.");
        }
        toMainEngine();
    }

    private void toMainEngine() {
        try {
            Parent nouvelleScene = FXMLLoader.load(getClass().getResource("sceneLogin.fxml"));
            Stage stage = Engine.getStage();
            stage.setScene(new Scene(nouvelleScene, 1200, 800));
            stage.show();
        } catch (Exception e) {
            showErrorDialog("Erreur", "Impossible de charger la page", e.getMessage());
        }
    }

    @FXML private void confirmSave() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de sauvegarde");
        alert.setHeaderText("Êtes-vous sûr de vouloir passer à l'étape suivante ?");
        alert.setContentText("Cette action est irréversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            save();
        } else {
            System.out.println("Annulation annulée.");
        }
    }

    @FXML private void save() {
        if (!validateFields()) {
            return;
        }

        String firstName = FirstName.getText();
        String lastName = LastName.getText();
        LocalDate dateOfBirth = DateOfBirth.getValue();
        String username = Username.getText();
        String password = Password.getText();

        // Creating compte and personnel
        try {
            EntrepriseManager entrepriseManager = new EntrepriseManager(connection);
            entrepriseManager.createEntreprise(entreprise);

            HotelManager hotelManager = new HotelManager(connection);
            hotel.setID(entreprise.getID_Entreprise(connection));
            hotelManager.addHotel(hotel);

            PersonnelManager personnelManager = new PersonnelManager(connection);
            CompteManager compteManager = new CompteManager(connection);
            Compte compte = new Compte(0, username, password, "Manager");
            compteManager.createCompte(compte);

            Personnel personnel = new Personnel(hotel.getID_Hotel(connection), null, firstName, lastName, dateOfBirth, "MANAGER", 0);
            personnelManager.add(personnel);
            personnel.setID_Compte(connection, compte.getID_Compte(connection));

            navigateToNextPage(compte);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (FirstName.getText().isEmpty() || LastName.getText().isEmpty() || DateOfBirth.getValue() == null
                || Username.getText().isEmpty() || Password.getText().isEmpty() || ConfirmPassword.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Veuillez remplir tous les champs.");
            return false;
        }

        String password = Password.getText();
        String confirmPassword = ConfirmPassword.getText();
        if (!password.equals(confirmPassword)) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Les mots de passe ne correspondent pas.");
            return false;
        }

        if (DateOfBirth.getValue().isAfter(LocalDate.now())) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("La date de naissance ne peut pas être dans le futur.");
            return false;
        }

        return true;
    }

    private void navigateToNextPage(Compte compte) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sceneMain.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

            EngineMain engineMain = (EngineMain) fxmlLoader.getController();
            engineMain.setData(entreprise.getID_Entreprise(connection),compte.getID_Compte(connection));

            Stage stage = Engine.getStage();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            showErrorDialog("Erreur", "Impossible de charger la page suivante.", e.getMessage());
        }
    }

    private void closeDatabaseConnection() {
        Connection connection = BDD.getConnection();
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (Exception e) {
                showErrorDialog("Erreur de connexion", "Erreur lors de la fermeture de la connexion à la base de données.", e.getMessage());
            }
        }
    }

    private void showErrorDialog(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("file:/images/Error.png"));
        alert.showAndWait();
    }
}

