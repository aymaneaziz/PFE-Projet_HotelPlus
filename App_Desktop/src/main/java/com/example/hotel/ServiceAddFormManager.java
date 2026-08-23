package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ServiceAddFormManager {
    @FXML private ComboBox<String> hotelName;
    @FXML private ComboBox<services> serviceName;
    @FXML private TextField price;
    @FXML private TextArea description;
    @FXML private Label errorLabel;

    public enum services {
        RESTAURANT("Restaurant"),
        SALLE_DE_SPORT("Salle de Sport"),
        PISCINE("Piscine"),
        PARKING("Parking"),
        MASSAGES("Massages");

        private final String label;

        services(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        // Méthode pour retrouver un Service à partir de son label
        public static services fromLabel(String label) {
            for (services s : services.values()) {
                if (s.label.equalsIgnoreCase(label)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Service inconnu : " + label);
        }
    }
    private HotelDAO hotelDAO = new HotelDAO();

    private int IDE;
    public void setIDE(int IDE) {
        this.IDE = IDE;
    }

    @FXML private void initialize(){
        Platform.runLater(() -> {
            hotelDAO.loadHotels(IDE);
            List<String> hotels = hotelDAO.getNamesHotel();
            if (serviceName != null) {
                serviceName.getItems().addAll(services.values());
            }
            if (hotelName != null) {
                hotelName.getItems().addAll(hotels);
            }
            errorLabel.setVisible(false);
            Utils.setNumericOnly(price);
        });
    }

    @FXML private void add() {
        try {
            // Vérifier que tous les champs sont remplis
            if ( hotelName.getValue() == null || serviceName.getValue() == null ||
                    price.getText().isEmpty() || description.getText().isEmpty() ) {
                errorLabel.setVisible(true);
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            // Récupérer les valeurs du formulaire
            String newhotel = hotelName.getValue();
            int newIdhotel = hotelDAO.getIdHotel(newhotel);
            String newservice = serviceName.getValue().toString();
            double newprix = Double.parseDouble(price.getText());
            String newdescription = description.getText();

            Service service = new Service();
            // Modifier les valeurs du service
            service.setIdHotel(newIdhotel);
            service.setNomService(newservice);
            service.setNomHotel(newhotel);
            service.setPriceService(newprix);
            service.setDetails(newdescription);

            // Mettre à jour le service dans la base de données
            ServiceManager serviceManager = new ServiceManager(BDD.getConnection());
            serviceManager.ajouterService(service);

            // Fermer la fenêtre après modification
            ((Stage) hotelName.getScene().getWindow()).close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }

    @FXML private void cancel() {
        // Fermer la fenêtre sans modifier
        ((Stage) hotelName.getScene().getWindow()).close();
    }
}
