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

public class ChambreAddFormController {
    @FXML private TextField Nchambre;
    @FXML private ComboBox<String> hotelName;
    @FXML private ComboBox<ChambreAddFormController.Type> typeChambre;
    @FXML private TextField prixChambre;
    @FXML private ComboBox<ChambreAddFormController.Offre> offreChambre;
    @FXML private TextField OffreDescription;
    @FXML private TextArea offreSpecial;
    @FXML private TextArea description;
    @FXML private ComboBox<ChambreAddFormController.Statut> Statue;
    @FXML private Label errorLabel;


    private enum Type {
        STANDARD("Standard"),
        FAMILIALE("Familiale"),
        SUITE("Suite"),
        DELUXE("Deluxe"),
        VIP("VIP");

        private final String label;
        Type(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }
    private enum Statut {
        LIBRE("Libre"),
        OCCUPEE("Occupée"),
        RESERVEE("Réservée"),
        EN_NETTOYAGE("En nettoyage"),
        EN_MAINTENANCE("En maintenance");

        private final String label;
        Statut(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }
    private enum Offre {
        PROMOTION("Promotion"),
        SPECIALE("Spéciale"),
        AUCUN("Aucun");

        private final String label;
        Offre(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }
    private HotelDAO hotelDAO = new HotelDAO();

    private int IDE;
    public void setIDE(int IDE) {
        this.IDE = IDE;
    }


    @FXML public void initialize() throws SQLException {
        Platform.runLater(() -> {
            hotelDAO.loadHotels(IDE);
            List<String> hotels = hotelDAO.getNamesHotel();
            // Initialisation de la liste des types d'hôtels
            if (typeChambre != null) {
                typeChambre.getItems().addAll(Type.values());
            }
            if (Statue != null) {
                Statue.getItems().addAll(Statut.values());
            }
            if (offreChambre != null) {
                offreChambre.getItems().addAll(Offre.values());
            }
            if (hotelName != null) {
                hotelName.getItems().addAll(hotels);
            }

            Utils.setNumericOnly(Nchambre);
            errorLabel.setVisible(false);
            offreSpecial.setVisible(false);
            OffreDescription.setEditable(false);
            Utils.setNumericOnly(OffreDescription);
            OffreDescription.setText("Aucun");
            Utils.setNumericOnly(prixChambre);

            offreChambre.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) { // Vérifier que la nouvelle valeur n'est pas nulle
                    switch (newValue.toString()) {
                        case "Promotion": OffreDescription.setText(""); OffreDescription.setEditable(true);
                            OffreDescription.setVisible(true); offreSpecial.setVisible(false); offreSpecial.setText("");break;
                        case "Spéciale": OffreDescription.setText("-1"); OffreDescription.setEditable(false); OffreDescription.setVisible(false); offreSpecial.setVisible(true); break;
                        case "Aucun": OffreDescription.setEditable(false); OffreDescription.setText("Aucun");
                            OffreDescription.setVisible(true); offreSpecial.setVisible(false); offreSpecial.setText("rien"); break;
                    }
                }
            });
        });
    }

    @FXML private void add() {
        try {
            // Vérifier que tous les champs sont remplis
            if ( Nchambre.getText().isEmpty() || hotelName.getValue() == null ||
                    typeChambre.getValue() == null || prixChambre.getText().isEmpty() ||
                    offreChambre.getValue() == null || description.getText().isEmpty() ||
                    (OffreDescription.getText().isEmpty() && offreSpecial.getText().isEmpty())
                    || Statue.getValue() == null ) {
                errorLabel.setVisible(true);
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            // Récupérer les valeurs du formulaire
            int newchambre = Integer.parseInt(Nchambre.getText());
            String newhotel = hotelName.getValue();
            int newIdhotel = hotelDAO.getIdHotel(newhotel);
            String newtype = typeChambre.getValue().toString();
            double newprix = Double.parseDouble(prixChambre.getText());
            String newoffre = offreChambre.getValue().toString();
            double newOffrePromo ;
            if (newoffre == "Aucun") {
                newOffrePromo = 0;
            }else {
                newOffrePromo = Double.parseDouble(OffreDescription.getText());
            }

            String newoffreSpecial = offreSpecial.getText();
            String newdescription = description.getText();
            String newstatue = Statue.getValue().toString();
            System.out.println(newIdhotel);

            Chambre selectedChambre = new Chambre();
            // Modifier les valeurs de l'hôtel
            selectedChambre.setIdhotel(newIdhotel);
            selectedChambre.setNumChambre(newchambre);
            selectedChambre.setNomHotel(newhotel);
            selectedChambre.setType(newtype);
            selectedChambre.setPrix(newprix);
            selectedChambre.setTypeOffre(newoffre);
            switch (newoffre){
                case "Promotion": selectedChambre.setPromotion(newOffrePromo);selectedChambre.setSpeciale(null); break;
                case "Speciale" : selectedChambre.setPromotion(0);selectedChambre.setSpeciale(newoffreSpecial); break;
                case "Aucun" : selectedChambre.setPromotion(0);selectedChambre.setSpeciale(null); break;
            }

            selectedChambre.setDescriptionChambre(newdescription);
            selectedChambre.setStatutChambre(newstatue);

            // Mettre à jour l'hôtel dans la base de données
            ChambreManager chambreManager = new ChambreManager(BDD.getConnection());
            chambreManager.ajouterChambre(selectedChambre);

            // Fermer la fenêtre après modification
            ((Stage) Nchambre.getScene().getWindow()).close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void cancel() {
        // Fermer la fenêtre sans modifier
        ((Stage) Nchambre.getScene().getWindow()).close();
    }
}


