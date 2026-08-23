package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;

public class EngineLogin {

    private Scene scene;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label create;
    @FXML private Button login;
    @FXML private Label ErrorMessage;

    private Compte compte;

    @FXML public void initialize() {
        Platform.runLater(() -> {
            if (username != null) {
                Stage stage = (Stage) username.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    closeDatabaseConnection();
                    stage.close();
                });
            }
        });
    }

    @FXML private void setConx() {
        try {
            CompteManager c = new CompteManager(BDD.getConnection());
            List<Compte> compteList = c.getComptes();
            if(username.getText().isEmpty() || password.getText().isEmpty()) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText("Veuillez remplir tous les champs");
                return;
            }
            for (Compte compte : compteList) {
                if(username.getText().equals(compte.getUser()) && password.getText().equals(compte.getPassword())) {
                    ErrorMessage.setText("Connected");
                    this.compte = compte;
                    setMainScene();
                    return;
                }
            }
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Nom d'utilisateur ou mot de passe incorrect");
        } catch (Exception e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    private void setMainScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sceneMain.fxml"));
            Scene scene = new Scene(fxmlLoader.load(),1200,800);
            EngineMain controller = fxmlLoader.getController();
            int IDCompte = compte.getID();
            int IDEntreprise = compte.getID_Entreprise(BDD.getConnection());
            System.out.println(IDEntreprise);
            controller.setData(IDEntreprise,IDCompte);
            Stage stage = Engine.getStage();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("Impossible de charger la page : " + e.getMessage());
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

    @FXML void create() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Engine.class.getResource("creeEntrepriseForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load(),1200,800);
            Stage stage = Engine.getStage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
