module com.desarrollo.caso1 {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.desarrollo.caso1;

    opens com.desarrollo.caso1 to javafx.fxml;
    opens com.desarrollo.caso1.controller to javafx.fxml;
}