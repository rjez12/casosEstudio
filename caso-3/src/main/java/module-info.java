module com.desarrollo.caso3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.desarrollo.caso3 to javafx.fxml;
    exports com.desarrollo.caso3;
}