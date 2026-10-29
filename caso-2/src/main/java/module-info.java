module com.desarrollo.caso2 {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.desarrollo.caso2;
    opens com.desarrollo.caso2.controller to javafx.fxml;
}