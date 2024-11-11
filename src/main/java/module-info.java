module org.example.fuel_consumption_calc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.example.fuel_consumption_calc to javafx.fxml;
    exports org.example.fuel_consumption_calc;
}