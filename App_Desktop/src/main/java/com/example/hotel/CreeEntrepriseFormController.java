package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.Optional;

public class CreeEntrepriseFormController {
    @FXML private StackPane MainPane;
    @FXML private TextField EntrepriseName;
    @FXML private TextField EntrepriseEmail;
    @FXML private Label EntrepriseLogo;
    @FXML private Button EntrepriseLogoButton;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML private Label ErrorMessage;

    private File tmpImage;
    private String logoPath;
    private String logoName;

    @FXML public void initialize() {
        ErrorMessage.setVisible(false);
        Platform.runLater(() -> {
            if (MainPane != null) {
                Stage stage = (Stage) MainPane.getScene().getWindow();
                stage.setOnCloseRequest((WindowEvent event) -> {
                    if (!confirmCancel()) {
                        event.consume();
                    } else {
                        closeDatabaseConnection();
                        stage.close();
                    }
                });
            } else {
                System.err.println("MainPane est null !");
            }
        });
    }

    private void Cancel() {
        if (logoName != null) {
            File tmpDir = new File("C:/xampp/htdocs/PFE_HOTEL/tmp");
            File tmpImage = new File(tmpDir, logoName); // Récupère l'image temporaire

            if (tmpImage.exists()) {
                boolean deleted = tmpImage.delete();
                if (deleted) {
                    System.out.println("Image supprimée du dossier tmp.");
                } else {
                    System.err.println("Erreur lors de la suppression de l'image.");
                }
            } else {
                System.out.println("L'image n'existe pas dans le dossier tmp.");
            }
        }
        toMainEngine();
    }

    private void toMainEngine(){
        try {
            Parent nouvelleScene = FXMLLoader.load(getClass().getResource("sceneLogin.fxml"));
            Stage stage = Engine.getStage();
            stage.setScene(new Scene(nouvelleScene, 1200, 800));
            stage.show();
        } catch (Exception e) {
            System.err.println("Impossible de charger la page : " + e.getMessage());
        }
    }

    @FXML private void choisirLogo() {
        File tmpDir = new File("C:/xampp/htdocs/PFE_HOTEL/tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        Pane dragPane = new Pane();
        dragPane.setPrefSize(400, 200);
        Text text = new Text("Glissez-déposez une image ici");
        text.setLayoutX(50);
        text.setLayoutY(100);
        dragPane.getChildren().add(text);

        dragPane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        // Créer la scène secondaire
        Scene scene = new Scene(dragPane, 250, 200);

        Stage dragStage = new Stage();
        dragStage.setTitle("HotelPlus");
        dragStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/iconApp.png")));
        dragStage.setScene(scene);
        dragStage.setResizable(false);

        dragStage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
        dragStage.show(); // Affiche la fenêtre secondaire et attend sa fermeture avant de reprendre la principale


        dragPane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                File sourceFile = dragboard.getFiles().get(0);
                logoName = sourceFile.getName();
                tmpImage = new File(tmpDir, logoName);

                try {
                    Files.copy(sourceFile.toPath(), tmpImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Image copiée dans tmp : " + tmpImage.getAbsolutePath());
                    EntrepriseLogo.setText("Le logo a été bien enregistré");
                    dragStage.close();
                } catch (IOException e) {
                    text.setText("Erreur lors de la copie de l'image.");
                    e.printStackTrace();
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void save() {
        if(EntrepriseName.getText().isEmpty() || EntrepriseEmail.getText().isEmpty() || tmpImage == null) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Veuillez remplir tous les champs");
            return;
        }

        String entrepriseNom = EntrepriseName.getText();
        String entrepriseEmail = EntrepriseEmail.getText();

        File tmpDir = new File("C:/xampp/htdocs/PFE_HOTEL/tmp");
        File tmpImage = new File(tmpDir, logoName);

        copyImg(tmpImage, entrepriseNom);
        Entreprise entreprise = new Entreprise();
        entreprise.setNom(entrepriseNom);
        entreprise.setEmail(entrepriseEmail);
        entreprise.setLogo(logoPath);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("creeHotelForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

            CreeHotelFormController controller = fxmlLoader.getController();
            controller.setEntreprise(entreprise);

            Stage stage = Engine.getStage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Impossible de charger la page : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void copyImg(File sourceFile, String entrepriseNom) {
        File entrepriseFolder = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entrepriseNom + "/logo");
        if (!entrepriseFolder.exists()) {
            entrepriseFolder.mkdirs();
        }

        File destinationFile = new File(entrepriseFolder, sourceFile.getName());

        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logoPath = "/PFE_HOTEL/App_Images/" + entrepriseNom + "/logo/" + sourceFile.getName();
            System.out.println("Chemin relatif de l'image : " + logoPath);

            if (tmpImage.exists()) {
                boolean deleted = tmpImage.delete();
                if (deleted) {
                    System.out.println("Image supprimée du dossier tmp.");
                } else {
                    System.err.println("Erreur lors de la suppression de l'image.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la copie de l'image");
            e.printStackTrace();
        }
    }


    @FXML private boolean confirmCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation d'annulation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler ?");
        alert.setContentText("Cela supprimera toutes les données que vous avez remplis.");

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
