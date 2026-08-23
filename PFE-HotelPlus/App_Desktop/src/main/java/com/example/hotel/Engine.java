package com.example.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import javafx.scene.layout.Pane;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;

public class Engine extends Application {

    private static Stage stage;
    private static Connection connection;

    // Getter et Setter pour la connexion
    public static Connection getConnection() {
        return connection;
    }
    public static void setConnection(Connection connection) {
        Engine.connection = connection;
    }

    // Getter et Setter pour le stage principal
    public static Stage getStage() {
        return stage;
    }
    public static void setStage(Stage stage) {
        Engine.stage = stage;
    }

    @Override
    public void start(Stage stage) {
        try {
            // Chargement du fichier FXML
            FXMLLoader fxmlLoader = new FXMLLoader(Engine.class.getResource("sceneLogin.fxml"));
            Pane root = fxmlLoader.load();

            // Création de la scène
            Scene scene = new Scene(root, 1200, 800);
            Engine.stage = stage;
            stage.setTitle("HotelPlus");

            // Chargement et ajout de l'icône de l'application
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            // Définition de la scène et gestion de la fermeture de la fenêtre
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(this::handleWindowClose);

            // Affichage de la fenêtre principale
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'application :");
            e.printStackTrace();
        }
    }

    // Gestion de la fermeture de la fenêtre (fermeture de la connexion BDD)
    private void handleWindowClose(WindowEvent event) {
        closeDatabaseConnection();
    }

    // Fermeture sécurisée de la connexion à la base de données
    private void closeDatabaseConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion :");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
