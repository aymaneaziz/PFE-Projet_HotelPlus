package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PersonnelEditFormController {
    @FXML
    private ComboBox<String> hotelName;
    @FXML private TextField lastName;
    @FXML private TextField firstName;
    @FXML private DatePicker dateOfBirth;
    @FXML private TextField tache;
    @FXML private TextField salary;
    @FXML private Button addButton;
    @FXML private Button cancelButton;
    @FXML private Button createButton;
    @FXML private Label errorLabel;

    private Personnel personnel;
    private PersonnelManager personnelManager;
    private HotelDAO hotelDAO = new HotelDAO();

    private int IDE;
    public void setIDE(int IDE) {
        this.IDE = IDE;
    }

    @FXML public void initialize() throws SQLException {
        Platform.runLater(() ->  {
            hotelDAO.loadHotels(IDE);
            List<String> hotels = hotelDAO.getNamesHotel();
            if (hotelName != null) {
                hotelName.getItems().addAll(hotels);
            }
            Utils.setNumericOnly(salary);
            errorLabel.setVisible(false);

        });
    }

    @FXML public void cancel() {
        ((Stage) hotelName.getScene().getWindow()).close();
    }

    @FXML public void edit() {
        if (hotelName.getValue() == null || lastName.getText().isEmpty() || firstName.getText().isEmpty() ||
                dateOfBirth.getValue() == null || salary.getText().isEmpty() || tache.getText().isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        String oldName = personnel.getLastName();
        int oldHotel = personnel.getID_Hotel();

        String name = hotelName.getValue();
        String last = lastName.getText();
        String first = firstName.getText();
        LocalDate date = dateOfBirth.getValue();
        double sal = Double.parseDouble(salary.getText());
        String ta = tache.getText();
        int newIdhotel = hotelDAO.getIdHotel(name);

        personnel.setID_Hotel(newIdhotel);
        personnel.setNom_Hotel(name);
        personnel.setLastName(last);
        personnel.setFirstName(first);
        personnel.setDDN(date);
        personnel.setSalaire(sal);
        personnel.setTache(ta);
        try {
            personnelManager.modifyPersonnel(personnel,oldName,oldHotel);
            ((Stage) hotelName.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPersonnelData(Personnel personnel,PersonnelManager personnelManager) throws SQLException {
        this.personnel = personnel;
        this.personnelManager = personnelManager;
        lastName.setText(personnel.getLastName());
        firstName.setText(personnel.getFirstName());
        dateOfBirth.setValue(personnel.getDDN());
        salary.setText(String.valueOf(personnel.getSalaire()));
        tache.setText(personnel.getTache());
        hotelName.setValue(personnel.getNom_Hotel());

        try {
            if (!(capitalizeFirstLetter(personnel.getTache()).equals("Admin") ||
                    capitalizeFirstLetter(personnel.getTache()).equals("Manager")) ||
                    personnel.hasaccount(BDD.getConnection()) != 0) {
                createButton.setDisable(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML private void create() {
        try {
            CompteManager compteManager = new CompteManager(BDD.getConnection());
            Compte compte = new Compte(0,lastName.getText(),firstName.getText(),capitalizeFirstLetter(tache.getText()));
            compteManager.createCompte(compte);
            personnel.setID_Compte(BDD.getConnection(), compte.getID_Compte(BDD.getConnection()));
            ((Stage) hotelName.getScene().getWindow()).close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}



