package com.example.hotel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PersonnelAddFormController {
    @FXML private ComboBox<String> hotelName;
    @FXML private TextField lastName;
    @FXML private TextField firstName;
    @FXML private DatePicker dateOfBirth;
    @FXML private TextField tache;
    @FXML private TextField salary;
    @FXML private Button addButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;

    private HotelDAO hotelDAO = new HotelDAO();

    private int IDE;
    public void setIDE(int IDE) {
        this.IDE = IDE;
    }

    @FXML public void initialize() throws SQLException {
        Platform.runLater(()-> {
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

    @FXML public void add() {
        if (hotelName.getValue() == null || lastName.getText().isEmpty() || firstName.getText().isEmpty() ||
                dateOfBirth.getValue() == null || salary.getText().isEmpty() || tache.getText().isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        String name = hotelName.getValue();
        String last = lastName.getText();
        String first = firstName.getText();
        LocalDate date = dateOfBirth.getValue();
        double sal = Double.parseDouble(salary.getText());
        String ta = tache.getText();
        int newIdhotel = hotelDAO.getIdHotel(name);

        Personnel personnel = new Personnel();
        personnel.setID_Hotel(newIdhotel);
        personnel.setNom_Hotel(name);
        personnel.setLastName(last);
        personnel.setFirstName(first);
        personnel.setDDN(date);
        personnel.setSalaire(sal);
        personnel.setTache(ta);
        try {
            PersonnelManager manager = new PersonnelManager(BDD.getConnection());
            manager.add(personnel);
            ((Stage) hotelName.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
