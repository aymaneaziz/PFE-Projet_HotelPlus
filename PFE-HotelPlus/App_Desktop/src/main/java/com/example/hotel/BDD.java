package com.example.hotel;


import javafx.beans.property.*;
import javafx.scene.control.Button;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BDD {
    public static final String URL = "jdbc:mysql://localhost:3306/hotel";
    public static final String USER = "root";
    public static final String PASS = "";
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Méthode pour obtenir une connexion à la base de données.
     * @return Connection si la connexion réussit, sinon null.
     */
    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trouvé : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
        return null;
    }

    public static void closeConnection(Connection connexion) {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException e) {
                System.err.println("Erreur de fermeture de la connexion a la BDD :" + e.getMessage());
            }
        }
    }
}

class Entreprise {
    private int id;
    private String nom;
    private String logo;
    private String email;

    public Entreprise(int id, String nom, String logo, String email) {
        this.id = id;
        this.nom = nom;
        this.logo = logo;
        this.email = email;
    }
    public Entreprise() {
        this(0,null,null,null);
    }

    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public String getLogo() {
        return logo;
    }
    public String getEmail() {
        return email;
    }
    public int getID_Entreprise(Connection connexion) {
        String sql = "select * from entreprise where Nom_Entreprise = ? and Email_Entreprise = ?";
        try {
            PreparedStatement stmt = connexion.prepareStatement(sql);
            stmt.setString(1, nom);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_entreprise");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setNom(String nom){
        this.nom = nom;
    }
    public void setLogo(String logo){
        this.logo = logo;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public void loadEntreprise(Connection connexion , int id) {
        String sql = "select * from entreprise where id_entreprise = ?";
        try {
            PreparedStatement statement = connexion.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                setNom(rs.getString("Nom_Entreprise"));
                setLogo(rs.getString("Logo"));
                setEmail(rs.getString("Email_Entreprise"));
                setId(rs.getInt("id_entreprise"));
            }
            statement.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateLogo(Connection connection, int IDEntreprise, String logoPath) throws SQLException {
        String sql = "UPDATE entreprise SET logo = ? WHERE ID_Entreprise = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (logoPath != null && !logoPath.trim().isEmpty()) {
                stmt.setString(1, logoPath);  // Mettez à jour le chemin du logo si un nouveau logo est fourni
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);  // Si aucun logo, on met une valeur nulle
            }

            stmt.setInt(2, IDEntreprise);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune ligne affectée. Mise à jour échouée.");
            }
        }
    }
}

class EntrepriseManager {
    private Connection connexion;

    public EntrepriseManager(Connection connexion) {
        this.connexion = connexion;
    }

    // Récupérer toutes les entreprises
    public List<Entreprise> getAllEntreprises() {
        List<Entreprise> entreprises = new ArrayList<>();
        String query = "SELECT * FROM entreprise";
        try (PreparedStatement stmt = connexion.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_Entreprise");
                String nom = resultSet.getString("Nom_Entreprise");
                String logo = resultSet.getString("Logo");
                String email = resultSet.getString("Email_Entreprise");

                Entreprise entreprise = new Entreprise(id, nom, logo, email);
                entreprises.add(entreprise);
            }
        } catch ( Exception e ) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        return entreprises;
    }

    // Récupérer une entreprise par son ID
    public Entreprise getEntrepriseById(int id) {
        String query = "SELECT * FROM entreprise WHERE ID_Entreprise = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String nom = resultSet.getString("Nom_Entreprise");
                    String logo = resultSet.getString("Logo");
                    String email = resultSet.getString("Email_Entreprise");
                    return new Entreprise(id, nom, logo, email);
                }
            }
        } catch ( Exception e ) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null; // Retourne null si l'entreprise n'est pas trouvée
    }

    // Créer une nouvelle entreprise
    public Entreprise createEntreprise(Entreprise entreprise) throws SQLException {
        String query = "INSERT INTO entreprise (Nom_Entreprise, Logo, Email_Entreprise) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entreprise.getNom());
            stmt.setString(2, entreprise.getLogo());
            stmt.setString(3, entreprise.getEmail());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La création de l'entreprise a échoué, aucune ligne affectée.");
            }

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new Entreprise(id, entreprise.getNom(), entreprise.getLogo(), entreprise.getEmail());
                } else {
                    throw new SQLException("Échec de la récupération de l'ID de l'entreprise.");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Mettre à jour une entreprise existante
    public boolean updateEntreprise(Entreprise entreprise) {
        String query = "UPDATE entreprise SET Nom_Entreprise = ?, Logo = ?, Email_Entreprise = ? WHERE ID_Entreprise = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(query)) {
            stmt.setString(1, entreprise.getNom());
            stmt.setString(2, entreprise.getLogo());
            stmt.setString(3, entreprise.getEmail());
            stmt.setInt(4, entreprise.getId());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une entreprise
    public boolean deleteEntreprise(int id) {
        String query = "DELETE FROM entreprise WHERE ID_Entreprise = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

//------------------------------------------------------class hotel---------------------------------------
class Hotel {
    private int ID;
    private StringProperty nom;
    private StringProperty pays;
    private StringProperty ville;
    private StringProperty adresse;
    private IntegerProperty nbr;
    private IntegerProperty etoile;
    private StringProperty type;
    private DoubleProperty Latitude;
    private DoubleProperty Longitude;
    private ObjectProperty<Button> editButton;
    private ObjectProperty<Button> deleteButton;
    private ObjectProperty<Button> imageButton;

    private HotelImages image;

    // Constructeur
    public Hotel(int ID,String nom, String pays, String ville, String adresse, int nbr,int etoile, String type, Double latitude, Double longitude) {
        this.ID = ID;
        this.nom = new SimpleStringProperty(nom);
        this.pays = new SimpleStringProperty(pays);
        this.ville = new SimpleStringProperty(ville);
        this.adresse = new SimpleStringProperty(adresse);
        this.nbr = new SimpleIntegerProperty(nbr);
        this.etoile = new SimpleIntegerProperty(etoile);
        this.type = new SimpleStringProperty(type);
        this.Latitude = new SimpleDoubleProperty(latitude);
        this.Longitude = new SimpleDoubleProperty(longitude);

        // Boutons pour les actions
        this.editButton = new SimpleObjectProperty<>(new Button("✏"));
        this.deleteButton = new SimpleObjectProperty<>(new Button("\uD83D\uDDD1"));
        this.imageButton = new SimpleObjectProperty<>(new Button("\uD83D\uDCF8"));
    }
    public Hotel(String nom, String pays, String ville, String adresse, int nbr,int etoile, String type, Double latitude, Double longitude){
        this(-1,nom,pays,ville,adresse,nbr,etoile,type,latitude,longitude);
    }
    public Hotel(){
        this(-1,null,null,null,null,0,0,null,0.0,0.0);
    }

    // Getters pour TableView
    public StringProperty getNomProperty() { return nom; }
    public StringProperty getPaysProperty() { return pays; }
    public StringProperty getVilleProperty() { return ville; }
    public StringProperty getAdresseProperty() { return adresse; }
    public IntegerProperty getNbrProperty() { return nbr; }
    public IntegerProperty getEtoileProperty() { return etoile; }
    public StringProperty getTypeProperty() { return type; }
    public DoubleProperty getLatitudeProperty() { return Latitude; }
    public DoubleProperty getLongitudeProperty() { return Longitude; }
    public ObjectProperty<Button> getEditButtonProperty() { return editButton; }
    public ObjectProperty<Button> getDeleteButtonProperty() { return deleteButton; }
    public ObjectProperty<Button> getImageButtonProperty() { return imageButton; }

    // Getters classiques
    public int getId() { return ID; }
    public String getNom() { return nom.get(); }
    public String getPays() { return pays.get(); }
    public String getVille() { return ville.get(); }
    public String getAdresse() { return adresse.get(); }
    public int getNbr() { return nbr.get(); }
    public int getEtoile() { return etoile.get(); }
    public String getType() { return type.get(); }
    public Double getLatitude() { return Latitude.get(); }
    public Double getLongitude() { return Longitude.get(); }
    public int getID_Hotel(Connection conn) throws SQLException {
        String sql = "SELECT ID_Hotel FROM Hotel WHERE ID_Entreprise = ? AND Nom_Hotel = ? And Pays = ? And Ville = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ID);
            stmt.setString(2, nom.get());
            stmt.setString(3, pays.get());
            stmt.setString(4, ville.get());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Hotel");
            }
        } catch ( Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Setters
    public void setID(int ID) { this.ID = ID; }
    public void setNom(String nom) { this.nom.set(nom); }
    public void setPays(String pays) { this.pays.set(pays); }
    public void setVille(String ville) { this.ville.set(ville); }
    public void setAdresse(String adresse) { this.adresse.set(adresse); }
    public void setNbr(int nbr) { this.nbr.set(nbr); }
    public void setEtoile(int etoile) { this.etoile.set(etoile); }
    public void setType(String type) { this.type.set(type); }
    public void setLatitude(Double latitude) { this.Latitude.set(latitude); }
    public void setLongitude(Double longitude) { this.Longitude.set(longitude); }

    public HotelImages LoadImage(Connection connection) throws SQLException {
        String sql = "SELECT * FROM Image_h WHERE ID_hotel = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, getID_Hotel(connection));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                image = new HotelImages(
                        rs.getInt("ID_Image"),
                        rs.getInt("ID_Hotel"),
                        rs.getString("URL")
                );
            }
            return image;
        }
    }

    public void saveImage(Connection conn, String url,int id_Hotel){
        String sql = "INSERT INTO image_h (ID_Hotel,url) VALUES (?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_Hotel);
            stmt.setString(2, url); // Ajouter l'URL de l'image à la requête SQL
            stmt.executeUpdate();   // Exécuter la requête d'insertion
            System.out.println("Image ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public void deleteImage(Connection conn, String url,int id_Hotel) {
        String sql = "DELETE FROM image_h WHERE url = ? and ID_Hotel = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, url); // Ajouter l'URL de l'image à la requête SQL
            stmt.setInt(2, id_Hotel);
            int rowsAffected = stmt.executeUpdate();   // Exécuter la requête de suppression

            if (rowsAffected > 0) {
                System.out.println("Image supprimée avec succès !");
            } else {
                System.out.println("Aucune image trouvée avec cette URL.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'image : " + e.getMessage());
        }
    }
}

class HotelManager {
    private static Connection connexion;
    private List<Hotel> hotels = new ArrayList<>();

    public HotelManager(Connection connexion) throws SQLException{
        HotelManager.connexion = connexion;
    }
    public HotelManager(Connection connexion,int ID) throws SQLException {
        if (connexion == null) {
            throw new SQLException("Erreur de connexion à la base de données.");
        }
        this.connexion = connexion;
        loadHotels(ID);
    }

    private void loadHotels(int IDE) throws SQLException {
        String query = "SELECT * FROM hotel where ID_Entreprise = ?";
        try {
            PreparedStatement stmt = connexion.prepareStatement(query);
            stmt.setInt(1,IDE);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                hotels.add(new Hotel(
                        resultSet.getInt("ID_Entreprise"),
                        resultSet.getString("Nom_Hotel"),
                        resultSet.getString("Pays"),
                        resultSet.getString("Ville"),
                        resultSet.getString("Adresse_Hotel"),
                        resultSet.getInt("Nbr_Chambre"),
                        resultSet.getInt("Nbr_Etoile"),
                        resultSet.getString("Type_Hotel"),
                        resultSet.getDouble("Latitude"),
                        resultSet.getDouble("Longitude")
                ));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // Obtenir la liste des hôtels
    public List<Hotel> getHotels() {
        return hotels;
    }

    // Ajouter un hôtel à la base
    public void addHotel(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO hotel (ID_Entreprise, Nom_Hotel, Pays, Ville, Adresse_Hotel,  Nbr_Etoile, Type_Hotel, Latitude, Longitude) VALUES (?,?, ?, ?, ?, ?,?,?,?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, hotel.getId());
            stmt.setString(2, hotel.getNom());
            stmt.setString(3, hotel.getPays());
            stmt.setString(4, hotel.getVille());
            stmt.setString(5, hotel.getAdresse());
            stmt.setInt(6,hotel.getEtoile());
            stmt.setString(7, hotel.getType());
            stmt.setDouble(8, hotel.getLatitude());
            stmt.setDouble(9, hotel.getLongitude());
            stmt.executeUpdate();
            hotels.add(hotel);
            System.out.println("Hôtel ajouté avec succès !");
        }
    }

    // Modifier un hôtel
    public void modifyHotel(Hotel hotel, String oldNom, String oldAdresse) throws SQLException {
        String sql = "UPDATE hotel SET Nom_Hotel = ?, Pays = ?, Ville = ?, Adresse_Hotel = ?, Nbr_Etoile = ?" +
                ", Type_Hotel = ?, Latitude = ?, Longitude = ? WHERE Nom_Hotel = ? AND Adresse_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, hotel.getNom());
            stmt.setString(2, hotel.getPays());
            stmt.setString(3, hotel.getVille());
            stmt.setString(4, hotel.getAdresse());
            stmt.setInt(5, hotel.getEtoile());
            stmt.setString(6, hotel.getType());
            stmt.setDouble(7, hotel.getLatitude());
            stmt.setDouble(8, hotel.getLongitude());
            stmt.setString(9, oldNom);
            stmt.setString(10, oldAdresse);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("L'hôtel a été mis à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez les informations.");
            }
        }
    }

    // Supprimer un hôtel
    public void removeHotel(Hotel hotel) throws SQLException {
        String sql = "DELETE FROM hotel WHERE Nom_Hotel = ? AND Adresse_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, hotel.getNom());
            stmt.setString(2, hotel.getAdresse());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                hotels.remove(hotel);
                System.out.println("Hôtel supprimé avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée.");
            }
        }
    }

    public void closeConnection() {
        try {
            if (connexion != null) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

class HotelImages {
    private int ID_Image;
    private int ID_Hotel;
    private String URL;

    public HotelImages(int ID_Image, int ID_Hotel, String URL) {
        this.ID_Image = ID_Image;
        this.ID_Hotel = ID_Hotel;
        this.URL = URL;
    }
    public HotelImages() {
        this(0,0, null);
    }

    public String getURL() { return this.URL; }
}

// Classe représentant une chambre d'hôtel
class Chambre {
    private int idhotel;
    private StringProperty nomHotel;
    private IntegerProperty numChambre;
    private StringProperty type;
    private DoubleProperty prix;
    private StringProperty statutChambre;
    private StringProperty descriptionChambre;
    private StringProperty typeOffre;
    private StringProperty speciale;
    private DoubleProperty promotion;
    private ObjectProperty<Button> editButton;
    private ObjectProperty<Button> deleteButton;
    private ObjectProperty<Button> imageButton;
    private StringProperty aucun = new SimpleStringProperty("Aucun");
    private List<ChambreImage> images;
    private List<ChambreOption> options;

    public Chambre(int id,String nomHotel, int  numChambre, String type, double prix, String statutChambre, String descriptionChambre,
                   String typeOffre, String speciale, double promotion) {
        this.idhotel = id;
        this.nomHotel = new SimpleStringProperty(nomHotel);
        this.numChambre = new SimpleIntegerProperty(numChambre);
        this.type = new SimpleStringProperty(type);
        this.prix = new SimpleDoubleProperty(prix);
        this.statutChambre = new SimpleStringProperty(statutChambre);
        this.descriptionChambre = new SimpleStringProperty(descriptionChambre);
        this.typeOffre = new SimpleStringProperty(typeOffre);
        this.speciale = new SimpleStringProperty(speciale);
        this.promotion = new SimpleDoubleProperty(promotion);

        // Boutons pour les actions
        this.editButton = new SimpleObjectProperty<>(new Button("✏"));
        this.deleteButton = new SimpleObjectProperty<>(new Button("\uD83D\uDDD1"));
        this.imageButton = new SimpleObjectProperty<>(new Button("\uD83D\uDCF8"));
    }
    public Chambre(){ this(0,null,0,null,0,null,null,null,null,0);}

//getter de table
    public StringProperty getNomHotelProperty() { return nomHotel; }
    public IntegerProperty getNumChambreProperty() { return numChambre; }
    public StringProperty getTypeProperty() { return type; }
    public DoubleProperty getPrixProperty() { return prix; }
    public StringProperty getStatutChambreProperty() { return statutChambre; }
    public StringProperty getDescriptionChambreProperty() { return descriptionChambre; }
    public StringProperty getTypeOffreProperty() { return typeOffre; }
    public StringProperty getSpecialeProperty() { return speciale; }
    public DoubleProperty getPromotionProperty() { return promotion; }
    public ObjectProperty<Button> getEditButtonProperty() { return editButton; }
    public ObjectProperty<Button> getDeleteButtonProperty() { return deleteButton; }
    public ObjectProperty<Button> getImageButtonProperty() { return imageButton; }
    public StringProperty getAucunProperty() { return aucun; }

//getter
    public int getID_Chambre(Connection connection) {
        String sql = "Select ID_Chambre from chambre where ID_Hotel = ? and Num_Chambre = ? and Type = ? and  Prix = ? and Statut_Chambre = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, this.idhotel);
            stmt.setInt(2, this.numChambre.get());
            stmt.setString(3, this.type.get());
            stmt.setDouble(4, this.prix.get());
            stmt.setString(5, this.statutChambre.get());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("ID_Chambre");
            }
            return 0;
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public int getIdhotel() { return idhotel; }
    public String getNomHotel() { return nomHotel.get(); }
    public int getNumChambre() { return numChambre.get(); }
    public String getType() { return type.get(); }
    public Double getPrix() { return prix.get(); }
    public String getStatutChambre() { return statutChambre.get(); }
    public String getDescriptionChambre() { return descriptionChambre.get(); }
    public String getTypeOffre() { return typeOffre.get(); }
    public String getSpeciale() { return speciale.get(); }
    public Double getPromotion() { return promotion.get(); }

//setters
    public void setIdhotel(int idhotel) { this.idhotel = idhotel; }
    public void setNomHotel(String nomHotel) {
        this.nomHotel = new SimpleStringProperty(nomHotel);
    }
    public void setNumChambre(int numChambre) {
        this.numChambre = new SimpleIntegerProperty(numChambre);
    }
    public void setType(String type) {
        this.type = new SimpleStringProperty(type);
    }
    public void setPrix(double prix) {
        this.prix = new SimpleDoubleProperty(prix);
    }
    public void setStatutChambre(String statutChambre) {
        this.statutChambre = new SimpleStringProperty(statutChambre);
    }
    public void setDescriptionChambre(String descriptionChambre) {
        this.descriptionChambre = new SimpleStringProperty(descriptionChambre);
    }
    public void setTypeOffre(String typeOffre) {
        this.typeOffre = new SimpleStringProperty(typeOffre);
    }
    public void setSpeciale(String speciale) {
        this.speciale = new SimpleStringProperty(speciale);
    }
    public void setPromotion(double promotion) {
        this.promotion = new SimpleDoubleProperty(promotion);
    }


    public List<ChambreImage> LoadImage(Connection connection) throws SQLException {
        images = new ArrayList<>();
        String sql = "SELECT * FROM Image_c  WHERE ID_Chambre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, getID_Chambre(connection));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                images.add(new ChambreImage(
                        rs.getInt("ID_Image"),
                        rs.getInt("ID_Chambre"),
                        rs.getString("URL")
                ));
            }
            return images;
        }
    }
    public void saveImage(Connection conn, String url,int id_Chambre){
        String sql = "INSERT INTO image_C (ID_Chambre,url) VALUES (?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_Chambre);
            stmt.setString(2, url); // Ajouter l'URL de l'image à la requête SQL
            stmt.executeUpdate();   // Exécuter la requête d'insertion
            System.out.println("Image ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public void deleteImage(Connection conn, String url,int id_Chambre) {
        String sql = "DELETE FROM image_C WHERE url = ? and id_Chambre = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, url); // Ajouter l'URL de l'image à la requête SQL
            stmt.setInt(2, id_Chambre);
            int rowsAffected = stmt.executeUpdate();   // Exécuter la requête de suppression

            if (rowsAffected > 0) {
                System.out.println("Image supprimée avec succès !");
            } else {
                System.out.println("Aucune image trouvée avec cette URL.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'image : " + e.getMessage());
        }
    }

    public List<ChambreOption> LoadOption(Connection connection) throws SQLException {
        List<ChambreOption> options = new ArrayList<>();
        String sql = "SELECT * FROM Options_chambre WHERE ID_Chambre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, getID_Chambre(connection));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                options.add(new ChambreOption(
                        rs.getInt("ID_Chambre"),
                        rs.getInt("ID_Option"),
                        rs.getString("Option_Chambre")
                ));
            }
        }
        return options;
    }
    public void saveOption(Connection conn, String option){
        String sql = "Insert into Options_Chambre (ID_Chambre,Option_Chambre) values (?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(2, option);
            statement.setInt(1, getID_Chambre(conn));
            statement.executeUpdate();
            System.out.println("Option ajoute avec succes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteOption(Connection conn, String option) {
        String sql = "delete From Options_Chambre Where id_Chambre = ? and Option_Chambre = ?";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, getID_Chambre(conn));
            preparedStatement.setString(2, option);
            preparedStatement.executeUpdate();
            System.out.println("Option supprime avec succes");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

class ChambreOption{
    int ID_Chambre;
    int ID_Option;
    String option;

    public ChambreOption(int ID_Chambre, int ID_Option, String option) {
        this.ID_Chambre = ID_Chambre;
        this.ID_Option = ID_Option;
        this.option = option;
    }
    public ChambreOption() {
        this(0,0,null);
    }

    public int getID_Chambre() {
        return ID_Chambre;
    }
    public void setID_Chambre(int ID_Chambre) {
        this.ID_Chambre = ID_Chambre;
    }

    public int getID_Option() {
        return ID_Option;
    }
    public void setID_Option(int ID_Option) {
        this.ID_Option = ID_Option;
    }

    public String getOption() {
        return option;
    }
    public void setOption(String option) {
        this.option = option;
    }
}

class ChambreImage{
    private int ID_Image;
    private int ID_Chambre;
    private String URL;

    public ChambreImage(int ID_Image, int ID_Chambre, String URL) {
        this.ID_Image = ID_Image;
        this.ID_Chambre = ID_Chambre;
        this.URL = URL;
    }
    public ChambreImage() {
        this(0,0, null);
    }

    public String getURL() { return this.URL; }
}

// Classe pour gérer les chambres
class ChambreManager {
    private static Connection connexion;
    private List<Chambre> chambres;

    public ChambreManager (Connection connexion) {
        this.connexion = connexion;
    }

    public ChambreManager(Connection connexion,int IDE) throws SQLException {
        if (connexion == null) {
            throw new SQLException("Erreur de connexion à la base de données.");
        }
        this.connexion = connexion;
        this.chambres = new ArrayList<>();
        loadChambres(IDE);
    }

    private void loadChambres(int IDE) throws SQLException {
        String query = "SELECT * FROM chambre NATURAL JOIN hotel where ID_Entreprise = ?";
        try {
            PreparedStatement stmt = connexion.prepareStatement(query);
            stmt.setInt(1, IDE);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_Chambre");
                String nomHotel = "";
                // Requête sécurisée pour récupérer le nom de l'hôtel
                String sql = "SELECT nom_hotel FROM hotel NATURAL JOIN chambre WHERE ID_Chambre = ?";
                try (PreparedStatement stmt2 = connexion.prepareStatement(sql)) {
                    stmt2.setInt(1, id);
                    try (ResultSet rs = stmt2.executeQuery()) {
                        if (rs.next()) {
                            nomHotel = rs.getString("Nom_Hotel"); // Récupérer le nom de l'hôtel
                        }
                    }
                }
                // Ajout de l'objet Chambre à la liste
                chambres.add(new Chambre(
                        resultSet.getInt("ID_hotel"),
                        nomHotel,
                        resultSet.getInt("Num_Chambre"),
                        resultSet.getString("Type"),
                        resultSet.getDouble("Prix"),
                        resultSet.getString("Statut_Chambre"),
                        resultSet.getString("Description_Chambre"),
                        resultSet.getString("Type_Offre"),
                        resultSet.getString("Speciale"),
                        resultSet.getDouble("Promotion")
                ));
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Chambre> getChambres() {
        return chambres;
    }

    public void ajouterChambre(Chambre chambre) throws SQLException {
        String sql = "INSERT INTO chambre (ID_Hotel, Num_Chambre, Type, Prix," +
                " Statut_Chambre, Description_Chambre, Type_Offre, Speciale, Promotion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, chambre.getIdhotel());
            stmt.setInt(2, chambre.getNumChambre());
            stmt.setString(3, chambre.getType());
            stmt.setDouble(4, chambre.getPrix());
            stmt.setString(5, chambre.getStatutChambre());
            stmt.setString(6, chambre.getDescriptionChambre());
            stmt.setString(7, chambre.getTypeOffre());
            stmt.setString(8, chambre.getSpeciale());
            stmt.setDouble(9, chambre.getPromotion());
            stmt.executeUpdate();
            if (chambres != null) {
                chambres.add(chambre);
            }
        }
        int nbr = 0;
        String request = "SELECT COUNT(*) FROM Chambre Where ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(request);) {
            stmt.setInt(1, chambre.getIdhotel());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nbr = rs.getInt(1);
            }
        }
        String rqt = "UPDATE HOTEL SET Nbr_Chambre = ? WHERE ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(rqt)) {
            stmt.setInt(1, nbr);
            stmt.setInt(2, chambre.getIdhotel());
            stmt.executeUpdate();
        }
        System.out.println("Chambre ajoutée avec succès !");
    }

    public void modifyChambre(Chambre chambre, int oldNum, int oldHotel) throws SQLException {
        String sql = "UPDATE Chambre SET ID_Hotel = ?, Num_Chambre = ?, Type = ?," +
                " Prix = ?, Statut_Chambre = ?, Description_Chambre = ?, Type_Offre = ?, Speciale = ?, Promotion = ? " +
                "WHERE Num_Chambre = ? and ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, chambre.getIdhotel());
            stmt.setInt(2, chambre.getNumChambre());
            stmt.setString(3, chambre.getType());
            stmt.setDouble(4, chambre.getPrix());
            stmt.setString(5, chambre.getStatutChambre());
            stmt.setString(6, chambre.getDescriptionChambre());
            stmt.setString(7, chambre.getTypeOffre());
            stmt.setString(8, chambre.getSpeciale());
            stmt.setDouble(9, chambre.getPromotion());
            stmt.setInt(10, oldNum);
            stmt.setInt(11, oldHotel);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("L'hôtel a été mis à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez les informations.");
            }
        }
        int nbr = 0;
        String request = "SELECT COUNT(*) FROM Chambre Where ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(request);) {
            stmt.setInt(1, chambre.getIdhotel());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nbr = rs.getInt(1);
            }
        }
        String rqt = "UPDATE HOTEL SET Nbr_Chambre = ? WHERE ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(rqt)) {
            stmt.setInt(1, nbr);
            stmt.setInt(2, chambre.getIdhotel());
            stmt.executeUpdate();
        }
        int nbr2 = 0;
        String request2 = "SELECT COUNT(*) FROM Chambre Where ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(request2);) {
            stmt.setInt(1, oldHotel);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nbr2 = rs.getInt(1);
            }
        }
        String rqt2 = "UPDATE HOTEL SET Nbr_Chambre = ? WHERE ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(rqt2)) {
            stmt.setInt(1, nbr2);
            stmt.setInt(2, oldHotel);
            stmt.executeUpdate();
        }
    }

    // Supprimer une chambre
    public void supprimerChambre(Chambre chambre) throws SQLException {
        String sql = "DELETE FROM chambre WHERE Num_Chambre = ? and ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, chambre.getNumChambre());
            stmt.setInt(2, chambre.getIdhotel());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                chambres.remove(chambre);
                System.out.println("Chambre supprimée avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée.");
            }
        }int nbr = 0;
        String request = "SELECT COUNT(*) FROM Chambre WHERE ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(request);) {
            stmt.setInt(1, chambre.getIdhotel());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nbr = rs.getInt(1);
            }
        }
        String rqt = "UPDATE HOTEL SET Nbr_Chambre = ? WHERE ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(rqt)) {
            stmt.setInt(1, nbr);
            stmt.setInt(2, chambre.getIdhotel());
            stmt.executeUpdate();
        }
    }

    public void closeConnection() {
        try {
            if (connexion != null) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Classe pour representer les services dans un hotel
class Service {
    private int IdHotel;
    private StringProperty nomService;
    private StringProperty nomHotel;
    private DoubleProperty priceService;
    private StringProperty details;
    private ObjectProperty<Button> editButton;
    private ObjectProperty<Button> deleteButton;
    private ObjectProperty<Button> imageButton;
    private List<ServiceImage> images ;

    public Service(int IdHotel, String nomService, String nomHotel,double priceService, String details) {
        this.IdHotel = IdHotel;
        this.nomService = new SimpleStringProperty(nomService);
        this.nomHotel = new SimpleStringProperty(nomHotel);
        this.priceService = new SimpleDoubleProperty(priceService);
        this.details = new SimpleStringProperty(details);

        editButton = new SimpleObjectProperty<>(new Button("✏"));
        deleteButton = new SimpleObjectProperty<>(new Button("\uD83D\uDDD1"));
        imageButton = new SimpleObjectProperty<>(new Button("\uD83D\uDCF8"));
    }

    public Service() {
        this(0,null,null,0,null);
    }

    public int getID_Service(Connection connection) {
        String sql = "Select ID_Service from Services where ID_Hotel = ? and Nom_Service = ? and Prix = ? ";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, this.IdHotel);
            stmt.setString(2, this.getNomService());
            stmt.setDouble(3, this.getPriceService());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("ID_Service");
            }
            return 0;
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public StringProperty getNomServiceProperty() {
        return nomService;
    }
    public StringProperty getNomHotelProperty() {
        return nomHotel;
    }
    public DoubleProperty getPriceServiceProperty() {
        return priceService;
    }
    public StringProperty getDetailsProperty() {
        return details;
    }
    public ObjectProperty<Button> getEditButtonProperty() {
        return editButton;
    }
    public ObjectProperty<Button> getDeleteButtonProperty() {
        return deleteButton;
    }
    public ObjectProperty<Button> getImageButtonProperty() {
        return imageButton;
    }

    public int getIdHotel() {
        return IdHotel;
    }
    public String getNomService() {
        return nomService.get();
    }
    public String getNomHotel() {
        return nomHotel.get();
    }
    public Double getPriceService() {
        return priceService.get();
    }
    public String getDetails() {
        return details.get();
    }

    public void setIdHotel(int IdHotel) {
        this.IdHotel = IdHotel;
    }
    public void setNomService(String nomService) {
        this.nomService.set(nomService);
    }
    public void setNomHotel(String nomHotel) {
        this.nomHotel.set(nomHotel);
    }
    public void setPriceService(Double priceService) {
        this.priceService.set(priceService);
    }
    public void setDetails(String details) {
        this.details.set(details);
    }

    public List<ServiceImage> LoadImage(Connection connection) throws SQLException {
        images = new ArrayList<>();
        String sql = "SELECT * FROM Image_S  WHERE ID_Service = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, getID_Service(connection));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                images.add(new ServiceImage(
                        rs.getInt("ID_Image"),
                        rs.getInt("ID_Service"),
                        rs.getString("URL")
                ));
            }
            return images;
        }
    }

    public void saveImage(Connection conn, String url,int id_Service){
        String sql = "INSERT INTO image_s (ID_Service,url) VALUES (?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_Service);
            stmt.setString(2, url); // Ajouter l'URL de l'image à la requête SQL
            stmt.executeUpdate();   // Exécuter la requête d'insertion
            System.out.println("Image ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public void deleteImage(Connection conn, String url,int id_Service) {
        String sql = "DELETE FROM image_s WHERE url = ? and id_Service = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, url); // Ajouter l'URL de l'image à la requête SQL
            stmt.setInt(2, id_Service);
            int rowsAffected = stmt.executeUpdate();   // Exécuter la requête de suppression

            if (rowsAffected > 0) {
                System.out.println("Image supprimée avec succès !");
            } else {
                System.out.println("Aucune image trouvée avec cette URL.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'image : " + e.getMessage());
        }
    }
}

class ServiceImage{
    private int ID_Image;
    private int ID_Service;
    private String URL;

    public ServiceImage(int ID_Image, int ID_Service, String URL) {
        this.ID_Image = ID_Image;
        this.ID_Service = ID_Service;
        this.URL = URL;
    }
    public ServiceImage() {
        this(0,0, null);
    }

    public String getURL() { return this.URL; }
}

// Classe pour gerer les services
class ServiceManager {
    private static Connection connexion;
    private List<Service> services;

    public ServiceManager(Connection connexion) {
        this.connexion = connexion;
    }

    public ServiceManager(Connection connexion,int IDE) throws SQLException {
        if (connexion == null) {
            throw new SQLException("Erreur de connexion à la base de données.");
        }
        this.connexion = connexion;
        this.services = new ArrayList<>();
        loadService(IDE);
    }

    private void loadService(int IDE) {
        String query = "SELECT * FROM Services NATURAL JOIN hotel Where ID_Entreprise = ?";
        try {
            PreparedStatement stmt = connexion.prepareStatement(query);
            stmt.setInt(1, IDE);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_Service");
                String nomHotel = "";
                // Requête sécurisée pour récupérer le nom de l'hôtel
                String sql = "SELECT nom_hotel FROM hotel NATURAL JOIN services WHERE ID_Service = ?";
                try (PreparedStatement stmt2 = connexion.prepareStatement(sql)) {
                    stmt2.setInt(1, id);
                    try (ResultSet rs = stmt2.executeQuery()) {
                        if (rs.next()) {
                            nomHotel = rs.getString("Nom_Hotel"); // Récupérer le nom de l'hôtel
                        }
                    }
                }
                // Ajout de l'objet Chambre à la liste
                services.add(new Service(
                        resultSet.getInt("ID_hotel"),
                        resultSet.getString("NOM_Service"),
                        nomHotel,
                        resultSet.getDouble("Prix"),
                        resultSet.getString("Description_Service")
                ));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de chargement des services : " + e.getMessage());
        }
    }

    public List<Service> getServices() {
        return services;
    }

    public void ajouterService(Service service) throws SQLException {
        String sql = "INSERT INTO Services (ID_Hotel, Nom_Service, Description_Service, Prix) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, service.getIdHotel());
            stmt.setString(2, service.getNomService());
            stmt.setString(3, service.getDetails());
            stmt.setDouble(4, service.getPriceService());
            stmt.executeUpdate();
            if (services != null) {
                services.add(service);
            }
        }
        System.out.println("Service ajoutée avec succès !");
    }

    public void modifyService(Service service, String oldNum, int oldHotel) throws SQLException {
        String sql = "UPDATE Services SET ID_Hotel = ?, Nom_Service = ?, Description_Service = ?," +
                " Prix = ? WHERE Nom_Service = ? and ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, service.getIdHotel());
            stmt.setString(2, service.getNomService());
            stmt.setString(3, service.getDetails());
            stmt.setDouble(4, service.getPriceService());
            stmt.setString(5, oldNum);
            stmt.setInt(6, oldHotel);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le Service a été mis à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez les informations.");
            }
        }
    }

    // Supprimer une chambre
    public void supprimerService(Service service) throws SQLException {
        String sql = "DELETE FROM Services WHERE Nom_Service = ? and ID_Hotel = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, service.getNomService());
            stmt.setInt(2, service.getIdHotel());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                services.remove(service);
                System.out.println("Service supprimée avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée.");
            }
        }
    }

    public void closeConnection() {
        try {
            if (connexion != null) {
                connexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Reservation {
    private int ID_Chambre;
    private int ID_Client;
    private StringProperty hotelName;
    private StringProperty NChambre;
    private StringProperty clientFirstName;
    private StringProperty clientLastName;
    private StringProperty dateDebut;
    private StringProperty dateFin;
    private StringProperty Statut;

    private ObjectProperty<Button> editButton;
    private ObjectProperty<Button> deleteButton;

    public Reservation(int ID_Chambre,int ID_Client,String hotelName,String NChambre,String clientFirstName,
                       String clientLastName,String dateDebut,String dateFin,String statut) {
        this.ID_Chambre = ID_Chambre;
        this.ID_Client = ID_Client;
        this.hotelName = new SimpleStringProperty(hotelName);
        this.NChambre = new SimpleStringProperty(NChambre);
        this.clientFirstName = new SimpleStringProperty(clientFirstName);
        this.clientLastName = new SimpleStringProperty(clientLastName);
        this.dateDebut = new SimpleStringProperty(dateDebut);
        this.dateFin = new SimpleStringProperty(dateFin);
        this.Statut = new SimpleStringProperty(statut);

        editButton = new SimpleObjectProperty<>(new Button("✏"));
        deleteButton = new SimpleObjectProperty<>(new Button("\uD83D\uDDD1"));
    }

    public Reservation() {
        this(0,0,null,null,null,null,null,null,null);
    }

    //propertygetters
    public StringProperty getHotelNameProperty() {
        return hotelName;
    }
    public StringProperty getNChambreProperty() {
        return NChambre;
    }
    public StringProperty getClientFirstNameProperty() {
        return clientFirstName;
    }
    public StringProperty getClientLastNameProperty() {
        return clientLastName;
    }
    public StringProperty getDateDebutProperty() {
        return dateDebut;
    }
    public StringProperty getDateFinProperty() {
        return dateFin;
    }
    public StringProperty getStatutProperty() {
        return Statut;
    }
    public ObjectProperty<Button> getEditButtonProperty() {
        return editButton;
    }
    public ObjectProperty<Button> getDeleteButtonProperty() {
        return deleteButton;
    }

    //getters
    public int getID_Chambre() {
        return ID_Chambre;
    }
    public int getID_Client() {
        return ID_Client;
    }
    public String getHotelName() {
        return hotelName.get();
    }
    public String getNChambre() {
        return NChambre.get();
    }
    public String getClientFirstName() {
        return clientFirstName.get();
    }
    public String getClientLastName() {
        return clientLastName.get();
    }
    public String getDateDebut() {
        return dateDebut.get();
    }
    public String getDateFin() {
        return dateFin.get();
    }
    public String getStatut() {
        return Statut.get();
    }

    //setters
    public void setID_Chambre(int ID_Chambre) {
        this.ID_Chambre = ID_Chambre;
    }
    public void setID_Client(int ID_Client) {
        this.ID_Client = ID_Client;
    }
    public void setHotelName(String hotelName) {
        this.hotelName.set(hotelName);
    }
    public void setNChambre(String NChambre) {
        this.NChambre.set(NChambre);
    }
    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName.set(clientFirstName);
    }
    public void setClientLastName(String clientLastName) {
        this.clientLastName.set(clientLastName);
    }
    public void setDateDebut(String dateDebut) {
        this.dateDebut.set(dateDebut);
    }
    public void setDateFin(String dateFin) {
        this.dateFin.set(dateFin);
    }
    public void setStatut(String statut) {
        this.Statut.set(statut);
    }
}

class ReservationManager {
    private List<Reservation> reservations;
    private Connection connection;

    public ReservationManager(Connection connection,int IDE) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        this.connection = connection;
        this.reservations = new ArrayList<>();
        loadReservation(IDE);
    }

    private void loadReservation(int IDE) throws SQLException {
        String query = "SELECT r.ID_Chambre, r.ID_Client, h.Nom_Hotel, c.Num_Chambre, cl.Nom_Client, cl.Prenom_Client, " +
                "r.Date_Debut, r.Date_Fin, r.Statut_Reservation FROM reservation r NATURAL JOIN Chambre c NATURAL JOIN hotel h " +
                "NATURAL JOIN Client cl where h.ID_Entreprise = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1,IDE);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                reservations.add(new Reservation(
                        resultSet.getInt("ID_Chambre"),
                        resultSet.getInt("ID_Client"),
                        resultSet.getString("Nom_Hotel"),
                        resultSet.getString("Num_Chambre"),
                        resultSet.getString("Prenom_Client"),
                        resultSet.getString("Nom_Client"),
                        resultSet.getDate("Date_Debut").toString(),
                        resultSet.getDate("Date_Fin").toString(),
                        resultSet.getString("Statut_Reservation")
                ));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public void modifierStatue(Reservation reservation) throws SQLException {
        String sqlReservation = "UPDATE reservation SET Statut_Reservation = ? WHERE ID_Chambre = ? AND ID_Client = ?";
        String sqlChambre = "UPDATE chambre NATURAL JOIN reservation SET Statut_Chambre = 'Occupée' WHERE ID_Chambre = ?";

        try (PreparedStatement stmtReservation = connection.prepareStatement(sqlReservation);
             PreparedStatement stmtChambre = connection.prepareStatement(sqlChambre)) {

            // Mettre à jour le statut de la réservation
            stmtReservation.setString(1, reservation.getStatut());
            stmtReservation.setInt(2, reservation.getID_Chambre());
            stmtReservation.setInt(3, reservation.getID_Client());
            int rowsUpdatedReservation = stmtReservation.executeUpdate();

            if (rowsUpdatedReservation > 0) {
                System.out.println("La réservation a été mise à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour de réservation effectuée. Vérifiez les informations.");
            }

            // Mettre à jour le statut de la chambre
            stmtChambre.setInt(1, reservation.getID_Chambre());
            int rowsUpdatedChambre = stmtChambre.executeUpdate();

            if (rowsUpdatedChambre > 0) {
                System.out.println("Le statut de la chambre a été mis à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour de chambre effectuée. Vérifiez les informations.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
}}

    public void supprimer(Reservation reservation) throws SQLException {
        String sql = "DELETE FROM reservation WHERE ID_Chambre = ? And ID_Client = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getID_Chambre());
            stmt.setInt(2, reservation.getID_Client());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                reservations.remove(reservation);
                System.out.println("Service supprimée avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée.");
            }
        }
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}


class Compte {
    private int ID;
    private String User;
    private String Password;
    private String Type;
    private int IDEntreprise = 0;

    public Compte(int ID,String User, String Password, String Type) {
        this.ID = ID;
        this.User = User;
        this.Password = Password;
        this.Type = Type;
    }

    public Compte(Connection connection,int ID) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        String sql = "Select * from compte where ID_Compte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                this.ID = resultSet.getInt("ID_Compte");
                this.User = resultSet.getString("Username");
                this.Password = resultSet.getString("Password");
                this.Type = resultSet.getString("Type_Compte");
            }
        }
    }

    public int getID_Compte(Connection connection) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        String sql = "Select * from compte where Username = ? and Type_Compte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, User);
            stmt.setString(2, Type);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                this.ID = resultSet.getInt("ID_Compte");
                this.User = resultSet.getString("Username");
                this.Password = resultSet.getString("Password");
                this.Type = resultSet.getString("Type_Compte");
            }
        }
        return this.ID;
    }
    public int getID_Entreprise(Connection connection) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        String sql = "SELECT e.ID_Entreprise FROM Entreprise e " +
                "JOIN Hotel h ON e.ID_Entreprise = h.ID_Entreprise " +
                "JOIN Personnel p ON h.ID_Hotel = p.ID_Hotel WHERE p.ID_Compte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                this.IDEntreprise = resultSet.getInt("ID_Entreprise");
                return IDEntreprise;
            }
        }catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return IDEntreprise;
    }
    public String getTache(Connection connection) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        String sql = "Select Tache from personnel natural join Compte where ID_Compte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ID);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Tache");
            }
        }
        return null;
    }

    public int getID() {
        return ID;
    }
    public String getUser() {
        return User;
    }
    public String getPassword() {
        return Password;
    }
    public String getType() {return Type;}

    public boolean updateCompte(int idCompte, String username, String oldpassword, String password) {
        String query = "UPDATE compte SET username = ?, password = ? WHERE password = ? AND ID_Compte = ? ";
        try (Connection conn = BDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, oldpassword);
            pstmt.setInt(4, idCompte);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Retourne vrai si la mise à jour a réussi
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

class CompteManager {
    private List<Compte> comptes;
    private Connection connection;

    public CompteManager(Connection connection) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        this.connection = connection;
        comptes = new ArrayList<>();
        loadList();
    }

    private void loadList() throws SQLException {
        String sql = "SELECT * FROM compte WHERE Type_Compte = 'Admin' or Type_Compte = 'Manager'";
        try (Statement stmt = connection.createStatement();){
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Compte compte = new Compte(
                        resultSet.getInt("ID_Compte"),
                        resultSet.getString("Username"),
                        resultSet.getString("Password"),
                        resultSet.getString("Type_Compte")
                );
                comptes.add(compte);
            }
        }
    }

    public void createCompte(Compte compte) throws SQLException {
        String sql = "INSERT INTO Compte (Username, Password, Type_Compte) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, compte.getUser());
            stmt.setString(2, compte.getPassword());
            stmt.setString(3, compte.getType());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                comptes.add(compte);
                System.out.println("Succés !!");
            } else {
                System.out.println("Aucun creation de compte !!");
            }
        }
    }

    public void deleteCompte(Compte compte) throws SQLException {
        String sql = "DELETE FROM Compte WHERE ID_Compte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, compte.getID());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                comptes.remove(compte);
                System.out.println("Le compte a été bien supprimmé !!");
            } else {
                System.out.println("Error lors de suppression !!");
            }
        }
    }

    public List<Compte> getComptes() {
        return comptes;
    }
}

class Personnel{
    private int ID_Hotel;
    private StringProperty Nom_Hotel;
    private StringProperty firstName;
    private StringProperty lastName;
    private ObjectProperty<LocalDate> DDN;
    private StringProperty Tache;
    private DoubleProperty Salaire;
    private ObjectProperty<Button> editButton;
    private ObjectProperty<Button> deleteButton;

    public Personnel(int ID_Hotel, String Nom_Hotel, String firstName, String lastName, LocalDate DDN, String Tache, double salaire) {
        this.ID_Hotel = ID_Hotel;
        this.Nom_Hotel = new SimpleStringProperty(Nom_Hotel);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.DDN = new SimpleObjectProperty<>(DDN);
        this.Tache = new SimpleStringProperty(Tache);
        this.Salaire = new SimpleDoubleProperty(salaire);

        editButton = new SimpleObjectProperty<>(new Button("✏"));
        deleteButton = new SimpleObjectProperty<>(new Button("\uD83D\uDDD1"));
    }
    public Personnel(){ this(0,null,null,null,null,null,0); }

    public StringProperty getNom_HotelProperty() {
        return Nom_Hotel;
    }
    public StringProperty getFirstNameProperty() {
        return firstName;
    }
    public StringProperty getLastNameProperty() {
        return lastName;
    }
    public ObjectProperty<LocalDate> getDDNProperty() {
        return DDN;
    }
    public StringProperty getTacheProperty() {
        return Tache;
    }
    public DoubleProperty getSalaireProperty() {
        return Salaire;
    }
    public ObjectProperty<Button> getEditButtonProperty() {
        return editButton;
    }
    public ObjectProperty<Button> getDeleteButtonProperty() {
        return deleteButton;
    }

    public int getID_Hotel() {
        return ID_Hotel;
    }
    public String getNom_Hotel() {
        return Nom_Hotel.get();
    }
    public String getFirstName() {
        return firstName.get();
    }
    public String getLastName() {
        return lastName.get();
    }
    public LocalDate getDDN() {
        return DDN.get();
    }
    public String getTache() {
        return Tache.get();
    }
    public Double getSalaire() {
        return Salaire.get();
    }

    public int hasaccount(Connection connection) throws SQLException {
        String sql = "SELECT ID_Compte FROM Personnel WHERE Nom_Personnel = ? and ID_Hotel = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lastName.get());
            stmt.setInt(2, ID_Hotel);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Compte");
            }
        }
        return 0;
    }

    public void loadPersonnel(Connection connection, int ID_Compte) {
        String sql = "SELECT * FROM Personnel WHERE ID_Compte = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ID_Compte);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {  // Vérifier s'il y a un résultat
                    int ID = rs.getInt("ID_Hotel");
                    String nom = "";

                    // Récupération du nom de l'hôtel
                    String sql1 = "SELECT Nom_Hotel FROM Hotel WHERE ID_Hotel = ?";
                    try (PreparedStatement stmt1 = connection.prepareStatement(sql1)) {
                        stmt1.setInt(1, ID);
                        try (ResultSet resultSet1 = stmt1.executeQuery()) {
                            if (resultSet1.next()) {
                                nom = resultSet1.getString("Nom_Hotel");
                            }
                        }
                    }

                    // Mise à jour des attributs
                    setID_Hotel(ID);
                    setNom_Hotel(nom);
                    setFirstName(rs.getString("Prenom_Personnel"));
                    setLastName(rs.getString("Nom_Personnel"));
                    setDDN(rs.getDate("DD_Naissance").toLocalDate());
                    setTache(rs.getString("Tache"));
                    setSalaire(rs.getDouble("Salaire"));
                } else {
                    System.out.println("Aucun personnel trouvé pour ID_Compte: " + ID_Compte);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setID_Compte(Connection conn, int ID_Compte) throws SQLException {
        String sql = "UPDATE Personnel SET ID_Compte = ? WHERE Prenom_Personnel = ? and Nom_Personnel = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ID_Compte);
            stmt.setString(2, firstName.get());
            stmt.setString(3, lastName.get());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Personnel mis a jours");
            }
        }
    }
    public void setID_Hotel(int ID_Hotel) {
        this.ID_Hotel = ID_Hotel;
    }
    public void setNom_Hotel(String Nom_Hotel) {
        this.Nom_Hotel = new SimpleStringProperty(Nom_Hotel);
    }
    public void setFirstName(String firstName) {
        this.firstName = new SimpleStringProperty(firstName);
    }
    public void setLastName(String lastName) {
        this.lastName = new SimpleStringProperty(lastName);
    }
    public void setDDN(LocalDate DDN) {
        this.DDN = new SimpleObjectProperty<>(DDN);
    }
    public void setTache(String Tache) {
        this.Tache = new SimpleStringProperty(Tache);
    }
    public void setSalaire(Double salaire) {
        this.Salaire = new SimpleDoubleProperty(salaire);
    }

}


class PersonnelManager{
    private List<Personnel> personnels;
    private Connection connection;

    public PersonnelManager(Connection connection) {
        this.connection = connection;
    }

    public PersonnelManager(Connection connection,int IDE) throws SQLException {
        if(connection == null) {
            throw new SQLException("Connection is null");
        }
        this.connection = connection;
        personnels = new ArrayList<>();
        loadList(IDE);
    }

    private void loadList(int IDE) throws SQLException {
        String sql = "SELECT * FROM personnel natural join hotel where ID_Entreprise = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, IDE);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String nom = "";
                int ID = resultSet.getInt("ID_Hotel");
                String sql1 = "SELECT Nom_Hotel FROM Hotel NATURAL JOIN Personnel Where ID_Hotel = ?";
                try (PreparedStatement stmt1 = connection.prepareStatement(sql1)) {
                    stmt1.setInt(1, ID);
                    ResultSet resultSet1 = stmt1.executeQuery();
                    if (resultSet1.next()) {
                        nom = resultSet1.getString("Nom_Hotel");
                    }
                }
                Personnel p = new Personnel(
                        ID,
                        nom,
                        resultSet.getString("Prenom_Personnel"),
                        resultSet.getString("Nom_Personnel"),
                        resultSet.getDate("DD_Naissance").toLocalDate(),
                        resultSet.getString("Tache"),
                        resultSet.getDouble("Salaire")
                );
                personnels.add(p);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Personnel> getPersonnels() {
        return personnels;
    }
    public void add(Personnel personnel) {
        String sql = "INSERT INTO Personnel (ID_Hotel,Nom_Personnel,Prenom_Personnel,DD_Naissance,Tache,Salaire) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, personnel.getID_Hotel());
            stmt.setString(2, personnel.getLastName());
            stmt.setString(3, personnel.getFirstName());
            stmt.setDate(4, Date.valueOf(personnel.getDDN()));
            stmt.setString(5, personnel.getTache());
            stmt.setDouble(6, personnel.getSalaire());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (personnels != null) {
            personnels.add(personnel);
        }
    }

    public void modifyPersonnel(Personnel personnel, String oldName, int oldHotel) {
        String sql = "UPDATE Personnel SET ID_Hotel = ?, Nom_Personnel = ?, Prenom_Personnel = ?, " +
                "DD_Naissance = ?, Tache = ?, Salaire = ? WHERE Nom_Personnel = ? AND ID_Hotel = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, personnel.getID_Hotel());
            stmt.setString(2, personnel.getLastName()); // Correction : Nom_Personnel
            stmt.setString(3, personnel.getFirstName()); // Correction : Prenom_Personnel
            stmt.setDate(4, Date.valueOf(personnel.getDDN()));
            stmt.setString(5, personnel.getTache());
            stmt.setDouble(6, personnel.getSalaire());
            stmt.setString(7, oldName);
            stmt.setInt(8, oldHotel);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le personnel a été mis à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez les informations.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du personnel : " + e.getMessage());
        }
    }

    // Supprimer une chambre
    public void supprimerPersonnel(Personnel personnel) throws SQLException {
        String sql = "DELETE FROM Personnel WHERE Nom_Personnel = ? and ID_Hotel = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, personnel.getLastName());
            stmt.setInt(2, personnel.getID_Hotel());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                personnels.remove(personnel);
                System.out.println("Personnel supprimée avec succès !");
            } else {
                System.out.println("Aucune suppression effectuée.");
            }
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
