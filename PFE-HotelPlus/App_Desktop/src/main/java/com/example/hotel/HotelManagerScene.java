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
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HotelManagerScene implements ReceveurDeDonnees {

    private int IDEntreprise;
    private int IDCompte;

    @Override
    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
    }

    @FXML protected TableView<Hotel> hotelTable;
    @FXML private TableColumn<Hotel, String> hotelName;
    @FXML private TableColumn<Hotel, String> hotelCountry;
    @FXML private TableColumn<Hotel, String> hotelCity;
    @FXML private TableColumn<Hotel, String> hotelAddress;
    @FXML private TableColumn<Hotel, Integer> hotelChambre;
    @FXML private TableColumn<Hotel, Integer> hotelStars;
    @FXML private TableColumn<Hotel, String> hotelType;
    @FXML private TableColumn<Hotel, Button> deleteButton;
    @FXML private TableColumn<Hotel, Button> editButton;
    @FXML private TableColumn<Hotel, Button> imageButton;
    @FXML private Button addButton;

    @FXML private TextField SearchBar;
    @FXML private ComboBox<SearchCriteria> searchCriteria;

    private ObservableList<Hotel> hotelList = FXCollections.observableArrayList();
    private HotelManager hotelManager ;

    enum SearchCriteria {
        NOM_HOTEL("Nom de l'hôtel"),
        PAYS("Pays"),
        VILLE("Ville"),
        TYPE("Type"),
        CHAMBRES("Chambres"),
        ETOILES("Étoiles");

        private final String label;
        SearchCriteria(String label) {
            this.label = label;
        }
        @Override
        public String toString() {
            return label;
        }
    }


    @FXML public void initialize() {
        Platform.runLater(() -> {
            try {
                Connection connection = BDD.getConnection();
                hotelManager = new HotelManager(connection, IDEntreprise);
                setTable();
                setSeartch();
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
        });
    }

    private void setSeartch(){

        searchCriteria.getItems().clear();
        searchCriteria.getItems().addAll(SearchCriteria.values());
        searchCriteria.setValue(SearchCriteria.NOM_HOTEL);


        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Hotel> filteredHotels = FXCollections.observableArrayList();
            for (Hotel hotel : hotelList) {
                String searchField = "";
                switch (searchCriteria.getValue()) {
                    case NOM_HOTEL -> searchField = hotel.getNom();
                    case PAYS -> searchField = hotel.getPays();
                    case VILLE -> searchField = hotel.getVille();
                    case TYPE -> searchField = hotel.getType();
                    case CHAMBRES -> searchField = String.valueOf(hotel.getNbr());
                    case ETOILES -> searchField = String.valueOf(hotel.getEtoile());
                }
                if (searchField.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredHotels.add(hotel);
                }
            }
            hotelTable.setItems(filteredHotels);
        });
    }

    private void setTable() {
        try {
            List<Hotel> hotels = hotelManager.getHotels();
            hotelList.setAll(hotels);
            hotelTable.setItems(hotelList);

            // Lier les colonnes aux propriétés de la classe Hotel
            hotelName.setCellValueFactory(cellData -> cellData.getValue().getNomProperty());
            hotelCountry.setCellValueFactory(cellData -> cellData.getValue().getPaysProperty());
            hotelCity.setCellValueFactory(cellData -> cellData.getValue().getVilleProperty());
            hotelAddress.setCellValueFactory(cellData -> cellData.getValue().getAdresseProperty());
            hotelChambre.setCellValueFactory(cellData -> cellData.getValue().getNbrProperty().asObject());
            hotelStars.setCellValueFactory(cellData -> cellData.getValue().getEtoileProperty().asObject());
            hotelType.setCellValueFactory(cellData -> cellData.getValue().getTypeProperty());


            // Ajouter les boutons Modifier et Supprimer
            deleteButton.setCellValueFactory(cellData -> cellData.getValue().getDeleteButtonProperty());
            editButton.setCellValueFactory(cellData -> cellData.getValue().getEditButtonProperty());
            imageButton.setCellValueFactory(cellData -> cellData.getValue().getImageButtonProperty());

            // Ajouter les actions des boutons
            for (Hotel hotel : hotelList) {
                // Lier l'action du bouton Modifier
                hotel.getEditButtonProperty().get().setOnAction(event -> modifierHotel(hotel));
                hotel.getEditButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style
                // Lier l'action du bouton Supprimer
                hotel.getDeleteButtonProperty().get().setOnAction(event -> confirmerSuppression(hotel));
                hotel.getDeleteButtonProperty().get().getStyleClass().add("Button");
                //
                hotel.getImageButtonProperty().get().setOnAction(event -> showImages(hotel));
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void modifierHotel(Hotel hotel) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hotelEditForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui envoyer les données
            HotelEditFormController controller = loader.getController();
            controller.setHotelData(hotel, hotelManager);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier l'hôtel");
            stage.setScene(new Scene(root));
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossible de modifier l'hotel" + e.getMessage());
        }
    }

    private void supprimerHotel(Hotel hotel) {
        Entreprise entreprise = new Entreprise();
        entreprise.loadEntreprise(BDD.getConnection(),IDEntreprise);
        try {
            File tmpDir = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + hotel.getNom());
            if (tmpDir.exists()) {
                if (FileUtils.deleteDirectory(tmpDir)) {
                    System.out.println("Dossier supprimé avec succès.");
                } else {
                    System.err.println("Échec de la suppression du dossier.");
                }
            }
            hotelManager.removeHotel(hotel);
            hotelList.remove(hotel);  // Supprimer de la liste
            hotelTable.refresh();  // Rafraîchir la table après suppression
            System.out.println("Hôtel supprimé !");
        } catch (SQLException e) {
            System.err.println("Impossible de supprimer le hotel !" + e.getMessage());
        }
    }

    @FXML private void addHotel() {
        try {
            // Charger le formulaire FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hotelAddForm.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène pour le formulaire
            HotelAddFormController controller = loader.getController();
            controller.setIDE(IDEntreprise);
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre pour afficher le formulaire
            Stage stage = new Stage();
            stage.setTitle("Ajouter un hôtel");
            stage.setScene(scene);
            stage.setResizable(false);
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.show();
        } catch (IOException e) {
            System.err.println("Error lors de l'ajouter un hotel");
        }
    }

    @FXML private void refreshHotelTable() {
        initialize();
    }

    private void confirmerSuppression(Hotel hotel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer \"" + hotel.getNom() + "\" ?");
        alert.setContentText("Cette action est irréversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        ButtonType boutonOui = new ButtonType("Confirmer", ButtonBar.ButtonData.YES);
        ButtonType boutonNon = new ButtonType("Annuler", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(boutonOui, boutonNon);

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                supprimerHotel(hotel);  // Appeler la méthode de suppression
            }
        });
    }

    @FXML private void showImages(Hotel hotel) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Galerie d'images");

            VBox root = new VBox(10);
            root.getStyleClass().add("gallery-container");

            HBox imageBox = new HBox(10);
            imageBox.getStyleClass().add("image-box");

            Connection conn = BDD.getConnection();
            HotelImages image = hotel.LoadImage(conn);

            if (image != null) {
                addImageToBox(imageBox, image, hotel, conn);
            }

            Pane dragPane = createDragPane(imageBox, hotel, conn);

            root.getChildren().addAll(imageBox, dragPane);
            Scene scene = new Scene(root, 240, 340);
            scene.getStylesheets().add(getClass().getResource("CSS/gallery.css").toExternalForm());
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }
            stage.setScene(scene);
            stage.setResizable(false);

            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageToBox(HBox imageBox, HotelImages image, Hotel hotel, Connection conn) {
        VBox imageContainer = new VBox(5);
        imageContainer.getStyleClass().add("image-container");

        ImageView imageView = new ImageView("file:C:/xampp/htdocs/" + image.getURL());
        setImageSize(imageView);

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            deleteExistingImage(imageBox, hotel, conn, image.getURL());
        });

        imageContainer.getChildren().addAll(imageView, deleteButton);
        imageBox.getChildren().add(imageContainer);
    }

    private Pane createDragPane(HBox imageBox, Hotel hotel, Connection conn) {
        Pane dragPane = new Pane();
        dragPane.getStyleClass().add("drag-pane");
        dragPane.setPrefSize(400, 200);
        Text text = new Text("Glissez-déposez une image ici");
        text.getStyleClass().add("drag-text");
        dragPane.getChildren().add(text);

        dragPane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dragPane.setOnDragDropped(event -> handleImageDrop(event, imageBox, hotel, conn));

        return dragPane;
    }

    private void handleImageDrop(DragEvent event, HBox imageBox, Hotel hotel, Connection conn) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            File sourceFile = dragboard.getFiles().get(0);
            String imageName = sourceFile.getName();

            Entreprise entreprise = new Entreprise();
            entreprise.loadEntreprise(conn, IDEntreprise);
            File hotelFolder = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + hotel.getNom() + "/HotelImage");
            if (!hotelFolder.exists()) {
                hotelFolder.mkdirs();
            }

            File destinationFile = new File(hotelFolder, imageName);
            String logoPath = "/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + hotel.getNom() + "/HotelImage/" + imageName;

            try {
                HotelImages existingImage = hotel.LoadImage(conn);
                if (existingImage != null) {
                    deleteExistingImage(imageBox, hotel, conn, existingImage.getURL());
                }

                Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                hotel.saveImage(conn, logoPath, hotel.getID_Hotel(conn));
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }

            imageBox.getChildren().clear();
            addNewImage(imageBox, destinationFile, hotel, conn, logoPath);
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void addNewImage(HBox imageBox, File destinationFile, Hotel hotel, Connection conn, String logoPath) {
        Image newImage = new Image(destinationFile.toURI().toString());
        ImageView newImageView = new ImageView(newImage);
        setImageSize(newImageView);

        VBox newImageContainer = new VBox(5);
        newImageContainer.getStyleClass().add("image-container");

        Button newDeleteButton = new Button("Supprimer");
        newDeleteButton.getStyleClass().add("delete-button");
        newDeleteButton.setOnAction(event -> deleteExistingImage(imageBox, hotel, conn, logoPath));

        newImageContainer.getChildren().addAll(newImageView, newDeleteButton);
        imageBox.getChildren().add(newImageContainer);
    }

    private void deleteExistingImage(HBox imageBox, Hotel hotel, Connection conn, String imagePath) {
        imageBox.getChildren().clear();
        try {
            File imageFile = new File("C:/xampp/htdocs/" + imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            hotel.deleteImage(conn, imagePath, hotel.getID_Hotel(conn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setImageSize(ImageView imageView) {
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
    }

}

