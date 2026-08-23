package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChambreEditFormController {
    @FXML private TextField Nchambre;
    @FXML private ComboBox<String> hotelName;
    @FXML private ComboBox<ChambreEditFormController.Type> typeChambre;
    @FXML private TextField prixChambre;
    @FXML private ComboBox<ChambreEditFormController.Offre> offreChambre;
    @FXML private TextField OffreDescription;
    @FXML private TextArea offreSpecial;
    @FXML private TextArea description;
    @FXML private ComboBox<ChambreEditFormController.Statut> Statue;
    @FXML private Label errorLabel;

    private Chambre selectedChambre;
    private ChambreManager chambreManager;

    // Enumération des types d'hôtels
    private enum Type {
        STANDARD("Standard"),
        FAMILIALE("Familiale"),
        SUITE("Suite"),
        DELUXE("Deluxe"),
        VIP("VIP");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        public static Type fromLabel(String label) {
            for (Type s : Type.values()) {
                if (s.label.equalsIgnoreCase(label)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Statut inconnu : " + label);
        }
    }

    private enum Statut {
        LIBRE("Libre"),
        OCCUPEE("Occupée"),
        RESERVEE("Réservée"),
        EN_NETTOYAGE("En nettoyage"),
        EN_MAINTENANCE("En maintenance");

        private final String label;

        Statut(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        public static Statut fromLabel(String label) {
            for (Statut s : Statut.values()) {
                if (s.label.equalsIgnoreCase(label)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Statut inconnu : " + label);
        }
    }

    private enum Offre {
        PROMOTION("Promotion"),
        SPECIALE("Speciale"),
        AUCUN("Aucun");

        private final String label;

        Offre(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        public static Offre fromLabel(String label) {
            for (Offre s : Offre.values()) {
                if (s.label.equalsIgnoreCase(label)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Statut inconnu : " + label);
        }
    }

    private HotelDAO hotelDAO = new HotelDAO();

    private int IDE;

    public void setIDE(int IDE) {
        this.IDE = IDE;
    }


    @FXML public void initialize() throws SQLException {
        offreChambre.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) { // Vérifier que la nouvelle valeur n'est pas nulle
                switch (newValue.toString()) {
                    case "Promotion":
                        OffreDescription.setText("");
                        OffreDescription.setEditable(true);
                        OffreDescription.setVisible(true);
                        offreSpecial.setVisible(false);
                        offreSpecial.setEditable(false);
                        offreSpecial.setText("");
                        break;
                    case "Speciale":
                        OffreDescription.setText("-1");
                        OffreDescription.setEditable(false);
                        OffreDescription.setVisible(false);
                        offreSpecial.setVisible(true);
                        offreSpecial.setEditable(true);
                        offreSpecial.setText("");
                        break;
                    case "Aucun":
                        OffreDescription.setEditable(false);
                        OffreDescription.setText("Aucun");
                        OffreDescription.setVisible(true);
                        offreSpecial.setVisible(false);
                        offreSpecial.setText("rien");
                        break;
                }
            }
        });
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
            errorLabel.setVisible(false);
            offreSpecial.setVisible(false);
            OffreDescription.setEditable(false);
            Utils.setNumericOnly(OffreDescription);
            OffreDescription.setText("Aucun");
            Utils.setNumericOnly(prixChambre);


        });
    }

    public void setChambreData(Chambre chambre, ChambreManager chambreManager) {
        this.selectedChambre = chambre;
        this.chambreManager = chambreManager;

        // Pré_remplir les champs avec les données actuelles de l'hôtel
        Nchambre.setText(String.valueOf(chambre.getNumChambre()));
        hotelName.setValue(chambre.getNomHotel());
        typeChambre.setValue(ChambreEditFormController.Type.fromLabel(chambre.getType()));
        prixChambre.setText(String.valueOf(chambre.getPrix()));
        description.setText(chambre.getDescriptionChambre());
        Statue.setValue(ChambreEditFormController.Statut.fromLabel(chambre.getStatutChambre()));
        offreChambre.setValue(ChambreEditFormController.Offre.fromLabel(chambre.getTypeOffre()));
        Platform.runLater(() -> {
            switch (chambre.getTypeOffre()) {
                case "Promotion":
                    OffreDescription.setText(chambre.getPromotion().toString());
                    OffreDescription.setEditable(true);
                    OffreDescription.setVisible(true);
                    offreSpecial.setVisible(false);
                    offreSpecial.setText("");
                    break;
                case "Speciale":
                    OffreDescription.setText("-1");
                    OffreDescription.setEditable(false);
                    offreSpecial.setVisible(true);
                    offreSpecial.setText(chambre.getSpeciale());
                    break;
                case "Aucun":
                    OffreDescription.setEditable(false);
                    OffreDescription.setText("Aucun");
                    OffreDescription.setVisible(true);
                    offreSpecial.setVisible(false);
                    offreSpecial.setText("rien");
                    break;
            }
        });
    }

    @FXML private void edit() {
        try {
            // Vérifier que tous les champs sont remplis
            if (Nchambre.getText().isEmpty() || hotelName.getValue() == null ||
                    typeChambre.getValue() == null || prixChambre.getText().isEmpty() ||
                    offreChambre.getValue() == null || description.getText().isEmpty() ||
                    (OffreDescription.getText().isEmpty() && offreSpecial.getText().isEmpty())
                    || Statue.getValue() == null) {
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
            double newOffrePromo;
            if (newoffre.equals("Aucun")) {
                newOffrePromo = 0;
            } else {
                newOffrePromo = Double.parseDouble(OffreDescription.getText());
            }

            String newoffreSpecial = offreSpecial.getText();
            String newdescription = description.getText();
            String newstatue = Statue.getValue().toString();

            int oldchambre = selectedChambre.getNumChambre();
            int oldhotel = selectedChambre.getIdhotel();


            // Modifier les valeurs de l'hôtel
            selectedChambre.setIdhotel(newIdhotel);
            selectedChambre.setNumChambre(newchambre);
            selectedChambre.setNomHotel(newhotel);
            selectedChambre.setType(newtype);
            selectedChambre.setPrix(newprix);
            selectedChambre.setTypeOffre(newoffre);
            switch (newoffre) {
                case "Promotion":
                    selectedChambre.setPromotion(newOffrePromo);
                    selectedChambre.setSpeciale(null);
                    break;
                case "Speciale":
                    selectedChambre.setPromotion(0);
                    selectedChambre.setSpeciale(newoffreSpecial);
                    break;
                case "Aucun":
                    selectedChambre.setPromotion(0);
                    selectedChambre.setSpeciale(null);
                    break;
            }

            selectedChambre.setDescriptionChambre(newdescription);
            selectedChambre.setStatutChambre(newstatue);

            // Mettre à jour l'hôtel dans la base de données
            ChambreManager chambreManager = new ChambreManager(BDD.getConnection());
            chambreManager.modifyChambre(selectedChambre, oldchambre, oldhotel);

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

    @FXML private void option() {
        try {
            Stage optionStage = new Stage();
            optionStage.initModality(Modality.APPLICATION_MODAL);
            optionStage.setTitle("Gérer les options de la chambre");

            // Liste des options disponibles
            String[] optionsDisponibles = {"Espace extérieur", "Vue sur l'océan", "Vue sur la ville", "Terrasse", "Climatisation", "Chauffage", "Wi-Fi inclus", "Cuisine"};
            ComboBox<String> choiceBox = new ComboBox<>();
            choiceBox.setPromptText("Choisir une option");
            choiceBox.getItems().addAll(optionsDisponibles);

            // Liste des options déjà enregistrées
            ListView<String> listView = new ListView<>();
            List<ChambreOption> optionsExistantes = selectedChambre.LoadOption(BDD.getConnection()); // Chargement des options depuis la BDD
            for (ChambreOption opt : optionsExistantes) {
                listView.getItems().add(opt.getOption());
            }

            // Bouton Ajouter
            Button btnAjouter = new Button("Ajouter");
            btnAjouter.setOnAction(e -> {
                String selectedOption = choiceBox.getValue();
                if (selectedOption != null && !listView.getItems().contains(selectedOption)) {
                    listView.getItems().add(selectedOption);
                    selectedChambre.saveOption(BDD.getConnection(), selectedOption);
                }
            });

            // Bouton Supprimer
            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setOnAction(e -> {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    listView.getItems().remove(selectedItem);
                    selectedChambre.deleteOption(BDD.getConnection(), selectedItem);
                }
            });

            VBox layout = new VBox(10, choiceBox, btnAjouter, listView, btnSupprimer);
            Scene scene = new Scene(layout, 400, 300);
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                optionStage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }
            scene.getStylesheets().add(getClass().getResource("CSS/option.css").toExternalForm());
            optionStage.setScene(scene);
            optionStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class H {
    private String nomHotel;
    private int idHotel;

    public H(String nomHotel, int idHotel) {
        this.nomHotel = nomHotel;
        this.idHotel = idHotel;
    }
    public String getNomHotel() {
        return nomHotel;
    }
    public int getIdHotel() {
        return idHotel;
    }
}

class HotelDAO {
    private List<H> hotels;
    public HotelDAO() {
        hotels = new ArrayList<>();
    }

    public void loadHotels(int IDE) {
        try {
            HotelManager manager = new HotelManager(BDD.getConnection(),IDE);
            List<Hotel> hotelList = manager.getHotels();
            for (Hotel hotel : hotelList) {
                hotels.add(new H(hotel.getNom(), hotel.getID_Hotel(BDD.getConnection())));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des hôtels", e);
        }
    }

    public List<String> getNamesHotel() {
        List<String> names = new ArrayList<>();
        for (H h : hotels) {
            names.add(h.getNomHotel());
        }
        return names;
    }

    public int getIdHotel(String nomHotel) {
        for (H h : hotels) {
            if (h.getNomHotel().equals(nomHotel)) {
                return h.getIdHotel();
            }
        }
        return -1; // Retourne -1 au lieu de 0 pour éviter toute confusion avec un ID valide
    }
}
