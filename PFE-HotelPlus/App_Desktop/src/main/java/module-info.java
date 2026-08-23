module com.example.hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;
    requires mysql.connector.j;

    opens com.example.hotel to javafx.fxml;
    exports com.example.hotel;
}