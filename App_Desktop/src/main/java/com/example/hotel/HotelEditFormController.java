package com.example.hotel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;

public class HotelEditFormController {
    @FXML private TextField hotelNameField;
    @FXML private TextField countryField;
    @FXML private TextField cityField;
    @FXML private TextField addressField;
    @FXML private TextField starsField;
    @FXML private ComboBox<Type> hotelTypeField;
    @FXML private Label errorLabel;
    @FXML private TextField Longitude;
    @FXML private TextField Latitude;

    private HotelManager hotelManager;
    private Hotel selectedHotel;

    // Enumération des types d'hôtels
    private enum Type {
        BUSINESS("Business"),
        LUXE("Luxe"),
        STANDARD("Standard"),
        FAMILIALE("Familiale"),
        MAISONS("Maisons");

        private final String label;
        Type(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @FXML
    public void initialize() {
        // Initialisation de la liste des types d'hôtels
        if (hotelTypeField != null) {
            hotelTypeField.getItems().addAll(Type.values());
        }
        errorLabel.setVisible(false);
        Utils.setNumericOnly(starsField);
    }
    public void setHotelData(Hotel hotel, HotelManager manager) {
        this.selectedHotel = hotel;
        this.hotelManager = manager;

        // Pré-remplir les champs avec les données actuelles de l'hôtel
        hotelNameField.setText(hotel.getNom());
        countryField.setText(hotel.getPays());
        cityField.setText(hotel.getVille());
        addressField.setText(hotel.getAdresse());
        starsField.setText(String.valueOf(hotel.getEtoile()));
        hotelTypeField.setValue(Type.valueOf(hotel.getType().toUpperCase()));
        Longitude.setText(String.valueOf(hotel.getLongitude()));
        Latitude.setText(String.valueOf(hotel.getLatitude()));
    }

    @FXML
    private void edit() {
        try {
            // Vérifier que tous les champs sont remplis
            if (hotelNameField.getText().isEmpty() || countryField.getText().isEmpty() ||
                    cityField.getText().isEmpty() || addressField.getText().isEmpty() ||
                    starsField.getText().isEmpty() || hotelTypeField.getValue() == null ||
                    Latitude.getText().isEmpty() || Longitude.getText().isEmpty()) {
                errorLabel.setVisible(true);
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            // Récupérer les valeurs du formulaire
            String newNom = hotelNameField.getText();
            String newCountry = countryField.getText();
            String newCity = cityField.getText();
            String newAddress = addressField.getText();
            int newStars = Integer.parseInt(starsField.getText());
            String newType = hotelTypeField.getValue().toString();
            double newLatitude = Double.parseDouble(Longitude.getText());
            double newLongitude = Double.parseDouble(Latitude.getText());

            // Sauvegarder les anciennes valeurs pour la mise à jour
            String oldNom = selectedHotel.getNom();
            String oldAdresse = selectedHotel.getAdresse();

            // Modifier les valeurs de l'hôtel
            selectedHotel.setNom(newNom);
            selectedHotel.setPays(newCountry);
            selectedHotel.setVille(newCity);
            selectedHotel.setAdresse(newAddress);
            selectedHotel.setEtoile(newStars);
            selectedHotel.setType(newType);
            selectedHotel.setLatitude(newLatitude);
            selectedHotel.setLongitude(newLongitude);

            // Mettre à jour l'hôtel dans la base de données
            hotelManager.modifyHotel(selectedHotel, oldNom, oldAdresse);

            // Fermer la fenêtre après modification
            ((Stage) hotelNameField.getScene().getWindow()).close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        // Fermer la fenêtre sans modifier
        ((Stage) hotelNameField.getScene().getWindow()).close();
    }
}
