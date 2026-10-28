module com.desarrollo.caso2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.desarrollo.caso2 to javafx.fxml;
    exports com.desarrollo.caso2;
}