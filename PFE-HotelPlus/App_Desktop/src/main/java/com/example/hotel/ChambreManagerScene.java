package com.example.hotel;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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

public class ChambreManagerScene implements ReceveurDeDonnees{
    private int IDEntreprise;
    private int IDCompte;

    @Override
    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
    }

    @FXML protected TableView<Chambre> chambreTable;
    @FXML private TableColumn<Chambre, Integer> chambreNumbre;
    @FXML private TableColumn<Chambre, String> nomHotel;
    @FXML private TableColumn<Chambre, String> chambreType;
    @FXML private TableColumn<Chambre, Double> chambrePrix;
    @FXML private TableColumn<Chambre, String> chambreOffre;
    @FXML private TableColumn<Chambre, String> offreDescription;
    @FXML private TableColumn<Chambre, String> chambreDescription;
    @FXML private TableColumn<Chambre, String> chambreStatut;
    @FXML private TableColumn<Chambre, Button> deleteButton;
    @FXML private TableColumn<Chambre, Button> editButton;
    @FXML private TableColumn<Chambre, Button> imageButton;
    @FXML private Button addButton;

    private ObservableList<Chambre> chambreList = FXCollections.observableArrayList();
    private ChambreManager chambreManager;

    @FXML private TextField SearchBar;
    @FXML private ComboBox<ChambreManagerScene.SearchCriteria> searchCriteria;

    enum SearchCriteria {
        NCHAMBRE("Numéro de chambre"),
        HOTEL("Nom de hotel"),
        TYPE_OFFRE("Type de l'offre"),
        TYPE("Type"),
        STATUE("Statut"),
        PRIX("Prix");

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
        searchCriteria.getItems().addAll(ChambreManagerScene.SearchCriteria.values());
        searchCriteria.setValue(ChambreManagerScene.SearchCriteria.NCHAMBRE);


        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Chambre> filteredChambres = FXCollections.observableArrayList();
            for (Chambre chambre : chambreList) {
                String searchField = "";
                switch (searchCriteria.getValue()) {
                    case NCHAMBRE -> searchField = String.valueOf(chambre.getNumChambre());
                    case HOTEL -> searchField = chambre.getNomHotel();
                    case TYPE_OFFRE -> searchField = chambre.getTypeOffre();
                    case TYPE -> searchField = chambre.getType();
                    case STATUE -> searchField = chambre.getStatutChambre();
                    case PRIX -> searchField = String.valueOf(chambre.getPrix());
                }
                if (searchField.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredChambres.add(chambre);
                }
            }
            chambreTable.setItems(filteredChambres);
        });
    }

    @FXML public void initialize() {
        Platform.runLater(() -> {
            try {
                Connection connection = BDD.getConnection();
                chambreManager = new ChambreManager(connection, IDEntreprise);
                setSeartch();
                setTable();
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
        });
    }

    private void setTable() {
        try {
            List<Chambre> chambres = chambreManager.getChambres();
            chambreList.setAll(chambres);
            chambreTable.setItems(chambreList);

            // Lier les colonnes aux propriétés de la classe Chambre
            chambreNumbre.setCellValueFactory(cellData -> cellData.getValue().getNumChambreProperty().asObject());
            nomHotel.setCellValueFactory(cellData -> cellData.getValue().getNomHotelProperty());
            chambreType.setCellValueFactory(cellData -> cellData.getValue().getTypeProperty());
            chambrePrix.setCellValueFactory(cellData -> cellData.getValue().getPrixProperty().asObject());
            chambreDescription.setCellValueFactory(cellData -> cellData.getValue().getDescriptionChambreProperty());
            chambreStatut.setCellValueFactory(cellData -> cellData.getValue().getStatutChambreProperty());
            chambreOffre.setCellValueFactory(cellData -> cellData.getValue().getTypeOffreProperty());
            offreDescription.setCellValueFactory(cellData ->
                    Bindings.when(cellData.getValue().getTypeOffreProperty().isEqualTo("Promotion"))
                            .then(cellData.getValue().getPromotionProperty().asString())
                            .otherwise(Bindings.when(cellData.getValue().getTypeOffreProperty().isEqualTo("Speciale"))
                                            .then(cellData.getValue().getSpecialeProperty())
                                            .otherwise(cellData.getValue().getAucunProperty())
                            )
            );


            // Ajouter les boutons Modifier et Supprimer
            deleteButton.setCellValueFactory(cellData -> cellData.getValue().getDeleteButtonProperty());
            editButton.setCellValueFactory(cellData -> cellData.getValue().getEditButtonProperty());
            imageButton.setCellValueFactory(cellData -> cellData.getValue().getImageButtonProperty());

            // Ajouter les actions des boutons
            for (Chambre Chambre : chambres) {
                // Lier l'action du bouton Modifier
                Chambre.getEditButtonProperty().get().setOnAction(event -> modifierChambre(Chambre));
                Chambre.getEditButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style
                // Lier l'action du bouton Supprimer
                Chambre.getDeleteButtonProperty().get().setOnAction(event -> confirmerSuppression(Chambre));
                Chambre.getDeleteButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style

                Chambre.getImageButtonProperty().get().setOnAction(event -> showImages(Chambre));
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void modifierChambre(Chambre chambre) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chambreEditForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui envoyer les données
            ChambreEditFormController controller = loader.getController();
            controller.setIDE(IDEntreprise);
            controller.setChambreData(chambre, chambreManager);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier la chambre");
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
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void supprimerChambre(Chambre chambre) {
        try {
            // Supprimer la chambre de la base de données
            chambreManager.supprimerChambre(chambre);
            chambreList.remove(chambre);  // Supprimer de la liste
            chambreTable.refresh();  // Rafraîchir la table après suppression

            // Supprimer le répertoire des images de la chambre
            Entreprise entreprise = new Entreprise();
            entreprise.loadEntreprise(BDD.getConnection(), IDEntreprise);

            File chambreFolder = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" +
                    chambre.getNomHotel() + "/" + chambre.getNumChambre());

            if (chambreFolder.exists()) {
                deleteDirectory(chambreFolder);
            }

            System.out.println("Chambre et son répertoire supprimés !");
        } catch (SQLException e) {
            System.err.println("Impossible de supprimer la chambre !" + e.getMessage());
        }
    }

    // Méthode pour supprimer un dossier et son contenu
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }

    @FXML private void addChambre() {
        try {
            // Charger le formulaire FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chambreAddForm.fxml"));
            Parent root = loader.load();

            ChambreAddFormController controller = loader.getController();
            controller.setIDE(IDEntreprise);
            // Créer une nouvelle scène pour le formulaire
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre pour afficher le formulaire
            Stage stage = new Stage();
            stage.setTitle("Ajouter une chambre");
            stage.setScene(scene);
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
            System.err.println("Error lors de l'ajouter d'une chambre : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void refreshChambreTable() {
        initialize();
    }

    private void confirmerSuppression(Chambre chambre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette chambre ?");
        alert.setContentText("Cette action est irréversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        ButtonType boutonOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType boutonNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(boutonOui, boutonNon);

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                supprimerChambre(chambre);  // Appeler la méthode de suppression
            }
        });
    }

    @FXML private void showImages(Chambre chambre) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Galerie d'images");

            VBox root = new VBox(10);
            root.getStyleClass().add("gallery-container");

            HBox imageBox = new HBox(10);
            imageBox.getStyleClass().add("image-box");

            Connection conn = BDD.getConnection();
            List<ChambreImage> images = chambre.LoadImage(conn);

            if (images != null) {
                for (ChambreImage image : images) {
                    VBox imageContainer = createImageContainer(image, chambre, imageBox, conn);
                    imageBox.getChildren().add(imageContainer);
                }
            }

            Pane dragPane = createDragPane(chambre, imageBox, conn);

            root.getChildren().addAll(imageBox, dragPane);
            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(getClass().getResource("CSS/gallery.css").toExternalForm());
            stage.setScene(scene);
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createImageContainer(ChambreImage image, Chambre chambre, HBox imageBox, Connection conn) {
        VBox imageContainer = new VBox(5);
        imageContainer.getStyleClass().add("image-container");

        ImageView imageView = new ImageView("file:C:/xampp/htdocs/" + image.getURL());
        setImageSize(imageView);

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            imageBox.getChildren().remove(imageContainer);
            deleteImage(image, chambre, conn);
        });

        imageContainer.getChildren().addAll(imageView, deleteButton);
        return imageContainer;
    }

    private void deleteImage(ChambreImage image, Chambre chambre, Connection conn) {
        try {
            File imageFile = new File("C:/xampp/htdocs/" + image.getURL());
            if (imageFile.exists()) {
                imageFile.delete();
            }
            chambre.deleteImage(conn, image.getURL(), chambre.getID_Chambre(BDD.getConnection()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private Pane createDragPane(Chambre chambre, HBox imageBox, Connection conn) {
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

        dragPane.setOnDragDropped(event -> {
            handleImageDrop(chambre, event, imageBox, conn);
            event.setDropCompleted(true);
            event.consume();
        });

        return dragPane;
    }

    private void handleImageDrop(Chambre chambre, DragEvent event, HBox imageBox, Connection conn) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            File sourceFile = dragboard.getFiles().get(0);
            String imageName = sourceFile.getName();

            String imagePath = saveImageToFileSystem(chambre, sourceFile, imageName);
            if (imagePath != null) {
                saveImageToDatabase(chambre, imagePath, conn);
                addImageToGallery(imageBox, sourceFile, imagePath, chambre, conn);
            }
        }
    }

    private String saveImageToFileSystem(Chambre chambre, File sourceFile, String imageName) {
        Entreprise entreprise = new Entreprise();
        entreprise.loadEntreprise(BDD.getConnection(), IDEntreprise);

        File chambreFolder = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + chambre.getNomHotel() + "/" +
                chambre.getNumChambre() + "/ChambreImages");
        if (!chambreFolder.exists()) {
            chambreFolder.mkdirs();
        }

        File destinationFile = new File(chambreFolder, imageName);
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + chambre.getNomHotel() + "/" +
                    chambre.getNumChambre() + "/ChambreImages/" + imageName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void saveImageToDatabase(Chambre chambre, String imagePath, Connection conn) {
        try {
            chambre.saveImage(conn, imagePath, chambre.getID_Chambre(BDD.getConnection()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageToGallery(HBox imageBox, File sourceFile, String imagePath, Chambre chambre, Connection conn) {
        Image newImage = new Image(sourceFile.toURI().toString());
        ImageView newImageView = new ImageView(newImage);
        setImageSize(newImageView);

        VBox newImageContainer = new VBox(5);
        newImageContainer.getStyleClass().add("image-container");

        Button newDeleteButton = new Button("Supprimer");
        newDeleteButton.getStyleClass().add("delete-button");
        newDeleteButton.setOnAction(event -> {
            imageBox.getChildren().remove(newImageContainer);
            deleteImageFromGalleryAndFile(imageBox, newImageContainer, imagePath, chambre, conn);
        });

        newImageContainer.getChildren().addAll(newImageView, newDeleteButton);
        imageBox.getChildren().add(newImageContainer);
    }

    private void deleteImageFromGalleryAndFile(HBox imageBox, VBox imageContainer, String imagePath, Chambre chambre, Connection conn) {
        imageBox.getChildren().remove(imageContainer); // Accès à imageBox
        try {
            File imageFile = new File("C:/xampp/htdocs/" + imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            chambre.deleteImage(conn, imagePath, chambre.getID_Chambre(BDD.getConnection()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageSize(ImageView imageView) {
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
    }

}
