module com.desarrollo.caso1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.desarrollo.caso1 to javafx.fxml;
    exports com.desarrollo.caso1;
}