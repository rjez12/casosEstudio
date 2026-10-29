module com.desarrollo.caso3 {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.desarrollo.caso3;
    opens com.desarrollo.caso3.controller to javafx.fxml;
}