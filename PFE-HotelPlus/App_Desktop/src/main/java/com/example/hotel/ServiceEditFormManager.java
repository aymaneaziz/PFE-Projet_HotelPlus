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

public class ServiceEditFormManager {
    @FXML
    private ComboBox<String> hotelName;
    @FXML private ComboBox<services> serviceName;
    @FXML private TextField price;
    @FXML private TextArea description;
    @FXML private Label errorLabel;

    private Service selectedService;
    private ServiceManager serviceManager;

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
        public static ServiceEditFormManager.services fromLabel(String label) {
            for (ServiceEditFormManager.services s : ServiceEditFormManager.services.values()) {
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

    @FXML private void Edit() {
        try {
            // Vérifier que tous les champs sont remplis
            if ( hotelName.getValue() == null || serviceName.getValue() == null ||
                    price.getText().isEmpty() || description.getText().isEmpty() ) {
                errorLabel.setVisible(true);
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            int oldid = selectedService.getIdHotel();
            String oldname = selectedService.getNomService();


            // Récupérer les valeurs du formulaire
            String newhotel = hotelName.getValue();
            int newIdhotel = hotelDAO.getIdHotel(newhotel);
            String newservice = serviceName.getValue().toString();
            double newprix = Double.parseDouble(price.getText());
            String newdescription = description.getText();

            // Modifier les valeurs du service
            selectedService.setIdHotel(newIdhotel);
            selectedService.setNomService(newservice);
            selectedService.setNomHotel(newhotel);
            selectedService.setPriceService(newprix);
            selectedService.setDetails(newdescription);

            // Mettre à jour le service dans la base de données
            serviceManager.modifyService(selectedService,oldname,oldid);

            // Fermer la fenêtre après modification
            ((Stage) hotelName.getScene().getWindow()).close();
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }

    public void setServiceData(Service service , ServiceManager serviceManager) {
        this.selectedService = service;
        this.serviceManager = serviceManager;

        // Pré_remplir les champs avec les données actuelles du Service
        hotelName.setValue(service.getNomHotel());
        serviceName.setValue(ServiceEditFormManager.services.fromLabel(service.getNomService()));
        price.setText(String.valueOf(service.getPriceService()));
        description.setText(service.getDetails());
    }

    @FXML private void cancel() {
        // Fermer la fenêtre sans modifier
        ((Stage) hotelName.getScene().getWindow()).close();
    }
}
