package com.desarrollo.caso1;

import com.desarrollo.caso1.controller.ReservaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReservaApplication extends Application {

    private ReservaController controller;

    @Override
    public void start(Stage ventana) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ReservaApplication.class.getResource(
                        "/com/desarrollo/caso1/reserva-view.fxml"
                )
        );

        Parent contenido = loader.load();
        controller = loader.getController();

        ventana.setTitle("Reserva concurrente de entradas");
        ventana.setScene(new Scene(contenido));
        ventana.setMinWidth(800);
        ventana.setMinHeight(650);
        ventana.show();
    }

    @Override
    public void stop() {
        if (controller != null) {
            controller.cerrar();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}