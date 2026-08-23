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
import java.util.Optional;

public class CreeHotelFormController {
    @FXML private StackPane MainPane;
    @FXML private TextField hotelName;
    @FXML private TextField hotelCountry;
    @FXML private TextField hotelCity;
    @FXML private TextField hotelAddress;
    @FXML private ComboBox<Type> hotelType;
    @FXML private TextField hotelStars;
    @FXML private TextField Latitude;
    @FXML private TextField Longitude;
    @FXML private Label ErrorMessage;

    private Entreprise entreprise;

    private enum Type {
        BUSINESS("Business"),
        LUXE("Luxe"),
        STANDARD("Standard"),
        FAMILIALE("Familiale"),
        MAISONS("Maisons");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    @FXML public void initialize() {
        if (hotelType != null) {
            hotelType.getItems().addAll(Type.values());
        }
        ErrorMessage.setVisible(false);
        Platform.runLater(() -> {
            // Limiter l'entrée à des valeurs numériques pour les champs spécifiques
            Utils.setNumericOnly(hotelStars);

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

    @FXML private void save() {
        // Vérification de la validité des champs
        if (hotelName.getText().isEmpty() || hotelCountry.getText().isEmpty() || hotelCity.getText().isEmpty()
                || hotelAddress.getText().isEmpty() || hotelType.getValue() == null || hotelStars.getText().isEmpty()
                || Latitude.getText().isEmpty() || Longitude.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Veuillez remplir tous les champs");
            return;
        }


        // Création de l'objet Hotel
        String hotelName = this.hotelName.getText();
        String hotelCountry = this.hotelCountry.getText();
        String hotelCity = this.hotelCity.getText();
        String hotelAddress = this.hotelAddress.getText();
        int hotelStars = Integer.parseInt(this.hotelStars.getText());
        if (hotelStars > 5 || hotelStars < 0) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Le champs étoile doit etre entre 0 et 5 ");
            return;
        }
        try {
        Double latitude = Double.parseDouble(this.Latitude.getText());
        double longitude = Double.parseDouble(this.Longitude.getText());
        String type = this.hotelType.getValue().toString();

        Hotel hotel = new Hotel(hotelName, hotelCountry, hotelCity, hotelAddress, 0, hotelStars, type, latitude, longitude);

        // Chargement de la page suivante

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("creeConnectionForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            CreeConnectionFormController controller = fxmlLoader.getController();

            controller.setData(entreprise, hotel);
            Stage stage = Engine.getStage();

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Impossible de charger la page : " + e.getMessage());
        }
    }

    @FXML private boolean confirmCancel() {
        // Fenêtre d'alerte pour confirmer l'annulation
        return showConfirmationDialog("Confirmation d'annulation", "Êtes-vous sûr de vouloir annuler ?",
                "Cela supprimera toutes les données que vous avez saisies.", this::Cancel);
    }

    private boolean showConfirmationDialog(String title, String header, String content, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onConfirm.run();
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
            System.err.println("Impossible de charger la page : " + e.getMessage());
        }
    }

    @FXML private void confirmSave() {
        // Fenêtre d'alerte pour confirmer la sauvegarde
        showConfirmationDialog("Confirmation de sauvegarde", "Êtes-vous sûr de vouloir passer à l'étape suivante ?",
                "Cette action est irréversible.", this::save);
    }

    private void closeDatabaseConnection() {
        Connection connection = BDD.getConnection();
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}

class FileUtils {

    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) { // Vérification pour éviter NullPointerException
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); // Suppression récursive des sous-dossiers
                    } else {
                        file.delete(); // Suppression des fichiers
                    }
                }
            }
        }
        return directory.delete(); // Supprime le dossier principal une fois vide
    }
}
