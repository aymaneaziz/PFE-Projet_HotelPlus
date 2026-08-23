package com.example.hotel;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class HotelAddFormController {
    @FXML private TextField hotelNameField;
    @FXML private TextField countryField;
    @FXML private TextField cityField;
    @FXML private TextField addressField;
    @FXML private TextField starsField;
    @FXML private TextField Longitude;
    @FXML private TextField Latitude;

    private int IDE;
    public void setIDE(int IDE){
        this.IDE = IDE;
    }

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
    @FXML private ComboBox<Type> hotelTypeField;
    @FXML private Label Error;

    @FXML public void initialize() {
        if (hotelTypeField != null) { // Vérifier que le champ est bien injecté
            hotelTypeField.getItems().addAll(Type.values());
        }
        Error.setVisible(false);
        Utils.setNumericOnly(starsField);


    }

    @FXML private void add() {
        // Récupérer les valeurs du formulaire
        if (hotelNameField.getText().isEmpty() || countryField.getText().isEmpty() ||
                cityField.getText().isEmpty() || addressField.getText().isEmpty() ||
                starsField.getText().isEmpty() || Longitude.getText().isEmpty()
                || Latitude.getText().isEmpty()) {
            Error.setVisible(true);
            Error.setText("Veuillez remplir tous les champs");
            return;
        }

        String hotelName = hotelNameField.getText();
        String country = countryField.getText();
        String city = cityField.getText();
        String address = addressField.getText();
        int stars = Integer.parseInt(starsField.getText());
        if( stars < 0 || stars > 5 ) {
            Error.setVisible(true);
            Error.setText("Le champs étoiles doit étre entre 0 et 5");
            return;
        }
        String hotelType = hotelTypeField.getValue().toString();
        double latitude = Double.parseDouble(Latitude.getText().toString());
        double longitude = Double.parseDouble(Longitude.getText().toString());

        // Créer un nouvel hôtel
        Hotel newHotel = new Hotel(IDE, hotelName, country, city, address, 0, stars, hotelType, latitude,longitude);

        try {
            // Ajouter l'hôtel à la base de données
            HotelManager manager = new HotelManager(BDD.getConnection());
            manager.addHotel(newHotel);

            // Fermer la fenêtre de formulaire après ajout
            ((Stage) hotelNameField.getScene().getWindow()).close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        // Fermer la fenêtre sans ajouter l'hôtel
        ((Stage) hotelNameField.getScene().getWindow()).close();
    }
}

class Utils {
    public static void setNumericOnly(TextField textField) {
        textField.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]")) { // Autoriser uniquement les chiffres
                event.consume(); // Annuler l'entrée
            }
        });
    }
}
