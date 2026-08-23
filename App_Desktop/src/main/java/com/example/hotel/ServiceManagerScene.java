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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServiceManagerScene implements ReceveurDeDonnees{
    private int IDEntreprise;
    private int IDCompte;

    @Override
    public void setData(int IDEntreprise, int IDCompte) {
        this.IDEntreprise = IDEntreprise;
        this.IDCompte = IDCompte;
    }
    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> serviceName;
    @FXML private TableColumn<Service, String> hotelName;
    @FXML private TableColumn<Service, String> serviceDescription;
    @FXML private TableColumn<Service, Double> servicePrice;
    @FXML private TableColumn<Service, Button> editButton;
    @FXML private TableColumn<Service, Button> deleteButton;
    @FXML private TableColumn<Service, Button> imageButton;
    @FXML private Button addButton;

    private ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private ServiceManager serviceManager;

    @FXML private TextField SearchBar;
    @FXML private ComboBox<ServiceManagerScene.SearchCriteria> searchCriteria;

    enum SearchCriteria {
        SERVICE("Nom du Service"),
        HOTEL("Nom de hotel"),
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

    @FXML private void initialize() {
        Platform.runLater(() -> {
            Connection connection = BDD.getConnection();
            try {
                serviceManager = new ServiceManager(connection, IDEntreprise);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            setSeartch();
            setTable();
        });
    }

    private void setSeartch(){

        searchCriteria.getItems().clear();
        searchCriteria.getItems().addAll(ServiceManagerScene.SearchCriteria.values());
        searchCriteria.setValue(SearchCriteria.SERVICE);


        SearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Service> filteredService = FXCollections.observableArrayList();
            for (Service service : serviceList) {
                String searchField = "";
                switch (searchCriteria.getValue()) {
                    case SERVICE -> searchField = service.getNomService();
                    case HOTEL -> searchField = service.getNomHotel();
                    case PRIX -> searchField = String.valueOf(service.getPriceService());
                }
                if (searchField.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredService.add(service);
                }
            }
            serviceTable.setItems(filteredService);
        });
    }

    private void setTable() {
        try {
            List<Service> services = serviceManager.getServices();
            serviceList.setAll(services);
            serviceTable.setItems(serviceList);

            // Lier les colonnes aux propriétés de la classe Chambre
            serviceName.setCellValueFactory(cellData -> cellData.getValue().getNomServiceProperty());
            hotelName.setCellValueFactory(cellData -> cellData.getValue().getNomHotelProperty());
            serviceDescription.setCellValueFactory(cellData -> cellData.getValue().getDetailsProperty());
            servicePrice.setCellValueFactory(cellData -> cellData.getValue().getPriceServiceProperty().asObject());

            // Ajouter les boutons Modifier et Supprimer
            deleteButton.setCellValueFactory(cellData -> cellData.getValue().getDeleteButtonProperty());
            editButton.setCellValueFactory(cellData -> cellData.getValue().getEditButtonProperty());
            imageButton.setCellValueFactory(cellData -> cellData.getValue().getImageButtonProperty());

            // Ajouter les actions des boutons
            for (Service service : services) {
                // Lier l'action du bouton Modifier
                service.getEditButtonProperty().get().setOnAction(event -> modifierService(service));
                service.getEditButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style

                // Lier l'action du bouton Supprimer
                service.getDeleteButtonProperty().get().setOnAction(event -> confirmerSuppression(service));
                service.getDeleteButtonProperty().get().getStyleClass().add("Button"); // Appliquer le style


                service.getImageButtonProperty().get().setOnAction(event -> showImages(service));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void modifierService(Service service) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("serviceEditForm.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui envoyer les données
            ServiceEditFormManager controller = loader.getController();
            controller.setIDE(IDEntreprise);
            controller.setServiceData(service, serviceManager);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier le service");
            stage.setTitle("Ajouter un service");
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Impossible de modifier le service : " + e.getMessage());
        }
    }

    private void supprimerService(Service service) {
        try {
            // Supprimer le service de la base de données
            serviceManager.supprimerService(service);

            // Supprimer le répertoire du service
            supprimerRepertoireService(service);

            // Supprimer de la liste observable et rafraîchir la table
            serviceList.remove(service);
            serviceTable.refresh();

            System.out.println("Service supprimé avec son dossier !");
        } catch (SQLException e) {
            System.err.println("Impossible de supprimer le service !" + e.getMessage());
        }
    }

    private void supprimerRepertoireService(Service service) {
        try {
            // Récupérer l'entreprise associée
            Entreprise entreprise = new Entreprise();
            entreprise.loadEntreprise(BDD.getConnection(), IDEntreprise);

            // Construire le chemin du dossier du service
            File serviceFolder = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/"
                    + entreprise.getNom() + "/"
                    + service.getNomHotel() + "/ServiceImages");

            // Vérifier si le dossier existe et le supprimer
            if (serviceFolder.exists()) {
                supprimerDossier(serviceFolder);
                System.out.println("Dossier du service supprimé : " + serviceFolder.getAbsolutePath());
            } else {
                System.out.println("Aucun dossier trouvé pour ce service.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la suppression du dossier du service.");
        }
    }

    private void supprimerDossier(File dossier) {
        File[] fichiers = dossier.listFiles();
        if (fichiers != null) { // Vérifier si le dossier n'est pas vide
            for (File fichier : fichiers) {
                if (fichier.isDirectory()) {
                    supprimerDossier(fichier); // Supprimer les sous-dossiers
                } else {
                    fichier.delete(); // Supprimer le fichier
                }
            }
        }
        dossier.delete(); // Supprimer le dossier principal
    }

    @FXML private void addService() {
        try {
            // Charger le formulaire FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("serviceAddForm.fxml"));
            Parent root = loader.load();


            ServiceAddFormManager controller = loader.getController();
            controller.setIDE(IDEntreprise);
            // Créer une nouvelle scène pour le formulaire
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre pour afficher le formulaire
            Stage stage = new Stage();
            stage.setTitle("Ajouter un service");
            InputStream iconStream = Engine.class.getResourceAsStream("/images/iconApp.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Erreur : Impossible de charger l'icône de l'application.");
            }

            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void refreshServiceTable() {
        initialize();
    }

    private void confirmerSuppression(Service service) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce Service ?");
        alert.setContentText("Cette action est irréversible.");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Engine.class.getResourceAsStream("/images/Alert.png")));

        ButtonType boutonOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType boutonNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(boutonOui, boutonNon);

        alert.showAndWait().ifPresent(reponse -> {
            if (reponse == boutonOui) {
                supprimerService(service);  // Appeler la méthode de suppression
            }
        });
    }

    @FXML private void showImages(Service service) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Galerie d'images");

            VBox root = new VBox(10);
            root.getStyleClass().add("gallery-container");

            HBox imageBox = new HBox(10);
            imageBox.getStyleClass().add("image-box");

            Connection conn = BDD.getConnection();
            List<ServiceImage> images = service.LoadImage(conn);

            if (images != null) {
                for (ServiceImage image : images) {
                    VBox imageContainer = createImageContainer(image, service, imageBox, conn);
                    imageBox.getChildren().add(imageContainer);
                }
            }

            Pane dragPane = createDragPane(service, imageBox, conn);

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

    private VBox createImageContainer(ServiceImage image, Service service, HBox imageBox, Connection conn) {
        VBox imageContainer = new VBox(5);
        imageContainer.getStyleClass().add("image-container");

        ImageView imageView = new ImageView("file:C:/xampp/htdocs/" + image.getURL());
        setImageSize(imageView);

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> {
            imageBox.getChildren().remove(imageContainer);
            deleteImage(image, service, conn);
        });

        imageContainer.getChildren().addAll(imageView, deleteButton);
        return imageContainer;
    }

    private void deleteImage(ServiceImage image, Service service, Connection conn) {
        try {
            File imageFile = new File("C:/xampp/htdocs/" + image.getURL());
            if (imageFile.exists()) {
                imageFile.delete();
            }
            service.deleteImage(conn, image.getURL(), service.getID_Service(BDD.getConnection()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private Pane createDragPane(Service service, HBox imageBox, Connection conn) {
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
            handleImageDrop(service, event, imageBox, conn);
            event.setDropCompleted(true);
            event.consume();
        });

        return dragPane;
    }

    private void handleImageDrop(Service service, DragEvent event, HBox imageBox, Connection conn) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            File sourceFile = dragboard.getFiles().get(0);
            String imageName = sourceFile.getName();

            String imagePath = saveImageToFileSystem(service, sourceFile, imageName);
            if (imagePath != null) {
                saveImageToDatabase(service, imagePath, conn);
                addImageToGallery(imageBox, sourceFile, imagePath, service, conn);
            }
        }
    }

    private String saveImageToFileSystem(Service service, File sourceFile, String imageName) {
        Entreprise entreprise = new Entreprise();
        entreprise.loadEntreprise(BDD.getConnection(), IDEntreprise);

        File serviceFolder = new File("C:/xampp/htdocs/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + service.getNomHotel() + "/ServiceImages");
        if (!serviceFolder.exists()) {
            serviceFolder.mkdirs();
        }

        File destinationFile = new File(serviceFolder, imageName);
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "/PFE_HOTEL/App_Images/" + entreprise.getNom() + "/" + service.getNomHotel() + "/ServiceImages/" + imageName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageToDatabase(Service service, String imagePath, Connection conn) {
        try {
            service.saveImage(conn, imagePath, service.getID_Service(BDD.getConnection()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageToGallery(HBox imageBox, File sourceFile, String imagePath, Service service, Connection conn) {
        Image newImage = new Image(sourceFile.toURI().toString());
        ImageView newImageView = new ImageView(newImage);
        setImageSize(newImageView);

        VBox newImageContainer = new VBox(5);
        newImageContainer.getStyleClass().add("image-container");

        Button newDeleteButton = new Button("Supprimer");
        newDeleteButton.getStyleClass().add("delete-button");
        newDeleteButton.setOnAction(event -> {
            imageBox.getChildren().remove(newImageContainer);
            deleteImageFromGalleryAndFile(imageBox, newImageContainer, imagePath, service, conn);
        });

        newImageContainer.getChildren().addAll(newImageView, newDeleteButton);
        imageBox.getChildren().add(newImageContainer);
    }

    private void deleteImageFromGalleryAndFile(HBox imageBox, VBox imageContainer, String imagePath, Service service, Connection conn) {
        imageBox.getChildren().remove(imageContainer); // Accès à imageBox
        try {
            File imageFile = new File("C:/xampp/htdocs/" + imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            service.deleteImage(conn, imagePath, service.getID_Service(BDD.getConnection()));
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
