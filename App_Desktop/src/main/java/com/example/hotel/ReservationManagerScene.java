package com.example.hotel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReservationManagerScene implements ReceveurDeDonnees{
    private int IDEntreprise;
    private int IDCompte;

    @Override
    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
    }
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> hotelNameColumn;
    @FXML private TableColumn<Reservation, String> chambreNameColumn;
    @FXML private TableColumn<Reservation, String> clientFirstNameColumn;
    @FXML private TableColumn<Reservation, String> clientLastNameColumn;
    @FXML private TableColumn<Reservation, String> dateDebutColumn;
    @FXML private TableColumn<Reservation, String> dateFinColumn;
    @FXML private TableColumn<Reservation, String> statueColumn;
    @FXML private TableColumn<Reservation, Button> editButton;
    @FXML private TableColumn<Reservation, Button> deleteButton;
    @FXML private Button refreshButton;

    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ReservationManager reservationManager;

    @FXML private TextField SearchBar;
    @FXML private ComboBox<ReservationManagerScene.SearchCriteria> searchCriteria;

    enum SearchCriteria {
        NCHAMBRE("Numéro de chambre"),
        HOTEL("Nom de hotel"),
        LASTNAME("Nom du client"),
        FIRSTNAME("Prénom du client"),
        DDB("Date de début"),
        DDF("Date de début"),
        STATUE("Statut");


        private final String label;
        SearchCriteria(String label) {
            this.label = label;
        }
        @Override
        public String toString() {
            return label;
        }
    }

    private void setSeartch(){

        searchCriteria.getItems().clear();
        searchCriteria.getItems().addAll(ReservationManagerScene.SearchCriteria.values());
        searchCriteria.setValue(SearchCriteria.HOTEL);


        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Reservation> filteredReservations = FXCollections.observableArrayList();
            for (Reservation reservation : reservations) {
                String searchField = "";
                switch (searchCriteria.getValue()) {
                    case NCHAMBRE -> searchField = String.valueOf(reservation.getID_Chambre());
                    case HOTEL -> searchField = reservation.getHotelName();
                    case FIRSTNAME -> searchField = reservation.getClientFirstName();
                    case LASTNAME -> searchField = reservation.getClientLastName();
                    case STATUE -> searchField = reservation.getStatut();
                    case DDB -> searchField = reservation.getDateDebut();
                    case DDF -> searchField = reservation.getDateFin();
                }
                if (searchField.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredReservations.add(reservation);
                }
            }
            reservationTable.setItems(filteredReservations);
        });
    }

    @FXML private void initialize(){
        Platform.runLater(() -> {
            try {
                Connection connection = BDD.getConnection();
                reservationManager = new ReservationManager(connection, IDEntreprise);
                setSeartch();
                settable();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void settable() {
        try{
            List<Reservation> reservation = reservationManager.getReservations();
            reservations.setAll(reservation);
            reservationTable.setItems(reservations);

            hotelNameColumn.setCellValueFactory(celldata -> celldata.getValue().getHotelNameProperty());
            chambreNameColumn.setCellValueFactory(celldata -> celldata.getValue().getNChambreProperty());
            clientFirstNameColumn.setCellValueFactory(celldata -> celldata.getValue().getClientFirstNameProperty());
            clientLastNameColumn.setCellValueFactory(celldata -> celldata.getValue().getClientLastNameProperty());
            dateDebutColumn.setCellValueFactory(celldata -> celldata.getValue().getDateDebutProperty());
            dateFinColumn.setCellValueFactory(celldata -> celldata.getValue().getDateFinProperty());
            statueColumn.setCellValueFactory(celldata -> celldata.getValue().getStatutProperty());

            editButton.setCellValueFactory( celldata -> celldata.getValue().getEditButtonProperty());
            deleteButton.setCellValueFactory( celldata -> celldata.getValue().getDeleteButtonProperty());

            for(Reservation reservation1 : reservation) {
                reservation1.getEditButtonProperty().get().setOnAction(event -> modifierReservation(reservation1));
                reservation1.getEditButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style

                reservation1.getDeleteButtonProperty().get().setOnAction(event ->supprimerReservation(reservation1));
                reservation1.getDeleteButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style

                if (reservation1.getStatut().equals("Confirmée") || reservation1.getStatut().equals("Annulée")) {
                    reservation1.getEditButtonProperty().get().setDisable(true);
                }
            }
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void modifierReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Accepter ou Refuser la Réservation");
        alert.setHeaderText("Voulez-vous Accepter ou Refuser cette réservation ?");
        alert.setContentText("Cette action est irreversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        ButtonType boutonAccepter = new ButtonType("Accepter", ButtonBar.ButtonData.OK_DONE);
        ButtonType boutonRefuser = new ButtonType("Refuser", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType boutonNon = new ButtonType("Annuler", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(boutonAccepter, boutonRefuser, boutonNon);

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonAccepter) {
                reservation.setStatut("Confirmée");
            } else if (reponse == boutonRefuser) {
                reservation.setStatut("Annulée");
            } else {
                return; // L'utilisateur a annulé, on ne fait rien.
            }

            try {
                reservationManager.modifierStatue(reservation); // Sauvegarde dans la base de données
            } catch (SQLException e) {
                Alert erreur = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification.");
                erreur.show();
            }
        });
    }


    private void supprimerReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette reservation ?");
        alert.setContentText("Cette action est irréversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        ButtonType boutonOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType boutonNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(boutonOui, boutonNon);

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                reservations.remove(reservation);
                try {
                    reservationManager.supprimer(reservation);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML private void refreshReservationTable() throws SQLException {
        initialize();
    }
}
