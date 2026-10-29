package com.desarrollo.caso3;

import com.desarrollo.caso3.controller.CajaController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CajaApplication extends Application {

    @Override
    public void start(Stage ventana) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                CajaApplication.class.getResource(
                        "/com/desarrollo/caso3/caja-view.fxml"
                )
        );

        Parent contenido = loader.load();
        CajaController controller = loader.getController();

        Platform.setImplicitExit(false);
        controller.configurarCierre(Platform::exit);

        ventana.setTitle("Caja de ventas concurrente");
        ventana.setScene(new Scene(contenido));
        ventana.setMinWidth(850);
        ventana.setMinHeight(740);

        ventana.setOnCloseRequest(evento -> {
            evento.consume();
            controller.solicitarCierre();
            ventana.hide();
        });

        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}