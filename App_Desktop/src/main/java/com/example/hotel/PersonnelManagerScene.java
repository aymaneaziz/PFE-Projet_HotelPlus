package com.example.hotel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PersonnelManagerScene implements ReceveurDeDonnees{
    private int IDEntreprise;
    private int IDCompte;

    @Override
    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
    }
    @FXML private TableView<Personnel> personnelTable;
    @FXML private TableColumn<Personnel, String> hotelNameColumn;
    @FXML private TableColumn<Personnel, String> firstNameColumn;
    @FXML private TableColumn<Personnel, String> lastNameColumn;
    @FXML private TableColumn<Personnel, String> DDNColumn;
    @FXML private TableColumn<Personnel, String> tacheColumn;
    @FXML private TableColumn<Personnel, Double> salaryColumn;
    @FXML private TableColumn<Personnel, Button> editButton;
    @FXML private TableColumn<Personnel, Button> deleteButton;
    @FXML private Button ImageSettings;

    protected ObservableList<Personnel> personnelList = FXCollections.observableArrayList();
    protected PersonnelManager personnelManager;

    @FXML private TextField SearchBar;
    @FXML private ComboBox<PersonnelManagerScene.SearchCriteria> searchCriteria;

    enum SearchCriteria {
        HOTEL("Nom de hotel"),
        LASTNAME("Nom du Personnel"),
        FIRSTNAME("Prénom du Personnel"),
        DDN("Date de Naissance"),
        SALARY("Salaire"),
        TACHE("Tache");
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
        searchCriteria.getItems().addAll(PersonnelManagerScene.SearchCriteria.values());
        searchCriteria.setValue(SearchCriteria.HOTEL);


        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Personnel> filteredPersonnel = FXCollections.observableArrayList();
            for (Personnel personnel : personnelList) {
                String searchField = "";
                switch (searchCriteria.getValue()) {
                    case HOTEL -> searchField = personnel.getNom_Hotel();
                    case LASTNAME -> searchField = personnel.getLastName();
                    case FIRSTNAME -> searchField = personnel.getFirstName();
                    case DDN -> searchField = personnel.getDDN().toString();
                    case SALARY -> searchField = Double.toString(personnel.getSalaire());
                    case TACHE -> searchField = personnel.getTache();
                }
                if (searchField.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredPersonnel.add(personnel);
                }
            }
            personnelTable.setItems(filteredPersonnel);
        });
    }

    @FXML public void initialize() {
        Platform.runLater(() -> {
            try {
                Connection connection = BDD.getConnection();
                personnelManager = new PersonnelManager(connection, IDEntreprise);
                setSeartch();
                setTable();
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
        });
    }
    private void setTable() {
        try {
            List<Personnel> personnels = personnelManager.getPersonnels();
            personnelList.setAll(personnels);
            personnelTable.setItems(personnelList);

            hotelNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNom_HotelProperty());
            firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFirstNameProperty());
            lastNameColumn.setCellValueFactory(celldata -> celldata.getValue().getLastNameProperty());
            DDNColumn.setCellValueFactory(cellData -> cellData.getValue().getDDNProperty().asString());
            tacheColumn.setCellValueFactory(cellData -> cellData.getValue().getTacheProperty());
            salaryColumn.setCellValueFactory(celldata -> celldata.getValue().getSalaireProperty().asObject());

            // Ajouter les boutons Modifier et Supprimer
            deleteButton.setCellValueFactory(cellData -> cellData.getValue().getDeleteButtonProperty());
            editButton.setCellValueFactory(cellData -> cellData.getValue().getEditButtonProperty());

            // Ajouter les actions des boutons
            for (Personnel personnel : personnelList) {
                // Lier l'action du bouton Modifier
                personnel.getEditButtonProperty().get().setOnAction(event -> modifierPersonnel(personnel));
                personnel.getEditButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style

                // Lier l'action du bouton Supprimer
                personnel.getDeleteButtonProperty().get().setOnAction(event -> confirmerSuppression(personnel));
                personnel.getDeleteButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style

                if (personnel.getTache().equals("MANAGER")) {
                    personnel.getEditButtonProperty().get().setDisable(true);
                    personnel.getDeleteButtonProperty().get().setDisable(true);
                }


            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void modifierPersonnel(Personnel personnel) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personnelEditForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui envoyer les données
            PersonnelEditFormController controller = loader.getController();
            controller.setIDE(IDEntreprise);
            controller.setPersonnelData(personnel, personnelManager);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();

            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.setTitle("Modifier le personnel");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossible de modifier l'hotel" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void supprimerPersonnel(Personnel personnel) {
        try {
            personnelManager.supprimerPersonnel(personnel);
            personnelList.remove(personnel);  // Supprimer de la liste
            personnelTable.refresh();  // Rafraîchir la table après suppression
            System.out.println("Personnel supprimé !");
        } catch (SQLException e) {
            System.err.println("Impossible de supprimer le personnel !" + e.getMessage());
        }
    }

    @FXML private void addPersonnel() {
        try {
            // Charger le formulaire FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personnelAddForm.fxml"));
            Parent root = loader.load();

            PersonnelAddFormController controller = loader.getController();
            controller.setIDE(IDEntreprise);

            // Créer une nouvelle scène pour le formulaire
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre pour afficher le formulaire
            Stage stage = new Stage();

            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.setTitle("Ajouter un personnel");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error lors de l'ajouter un personnel" + e.getMessage());
        }
    }

    @FXML private void refreshPersonnelTable() {
        initialize();
    }

    private void confirmerSuppression(Personnel personnel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce personnel ?");
        alert.setContentText("Cette action est irréversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        ButtonType boutonOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType boutonNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(boutonOui, boutonNon);

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                supprimerPersonnel(personnel);  // Appeler la méthode de suppression
            }
        });
    }

}
