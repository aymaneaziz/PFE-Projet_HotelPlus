package com.example.hotel;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EngineMain {

    @FXML private BorderPane GlobalPane; // Le BorderPane principal
    @FXML private AnchorPane MenuPane;

    @FXML private Button PersonnelBTN;

    @FXML private void GestionHotels() {loadPage("hotelScene.fxml");}
    @FXML private void GestionChambres() {loadPage("chambresScene.fxml");}
    @FXML private void GestionServices() {loadPage("servicesScene.fxml");}
    @FXML private void GestionReservation() {loadPage("reservationScene.fxml");}
    @FXML private void GestionPersonnel() {loadPage("personnelScene.fxml");}
    @FXML private void Parametre() {loadPage("parametreScene.fxml");}

    private int IDEntreprise;
    private int IDCompte;

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane newContent = loader.load();

            // Récupérer le contrôleur de la nouvelle page
            Object controller = loader.getController();

            // Vérifier si le contrôleur a une méthode "setData" et l'invoquer
            if (controller instanceof ReceveurDeDonnees) {
                ((ReceveurDeDonnees) controller).setData(IDEntreprise, IDCompte);
            }

            // Afficher la page dans le GlobalPane
            GlobalPane.setCenter(newContent);
        } catch (Exception e) {
            System.err.println("Impossible de charger la page : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML private void Logout(){
        try {
            Parent nouvelleScene = FXMLLoader.load(getClass().getResource("sceneLogin.fxml"));
            Stage stage = Engine.getStage();
            stage.setScene(new Scene(nouvelleScene));
            stage.show();
        } catch (Exception e) {
            System.err.println("Impossible de charger la page : " + e.getMessage());
        }
    }

    @FXML private Label GroupName;
    @FXML private ImageView Logo;

    @FXML public void initialize() throws SQLException {
        Platform.runLater(() -> {
            if (MenuPane != null) {
                Stage stage = (Stage) MenuPane.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                        closeDatabaseConnection();
                        stage.close();
                });
                Entreprise entreprise = new Entreprise();
                entreprise.loadEntreprise(BDD.getConnection(),IDEntreprise);
                GroupName.setText(entreprise.getNom());
                Image image = new Image("file:C:/xampp/htdocs/" + entreprise.getLogo());
                Logo.setImage(image);
                loadPage("hotelScene.fxml");

                try {
                    Compte compte = new Compte(BDD.getConnection(),IDCompte);
                    if (!compte.getTache(BDD.getConnection()).toLowerCase().equals("manager")) {
                        PersonnelBTN.setDisable(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
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

interface ReceveurDeDonnees {
    void setData(int IDEntreprise, int IDCompte);
}