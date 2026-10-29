package com.desarrollo.caso2;

import com.desarrollo.caso2.controller.PedidoController;
import com.desarrollo.caso2.controller.PedidoController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PedidosApplication extends Application {

    @Override
    public void start(Stage ventana) throws IOException {
        Platform.setImplicitExit(false);

        FXMLLoader loader = new FXMLLoader(
                PedidosApplication.class.getResource(
                        "/com/desarrollo/caso2/pedidos-view.fxml"
                )
        );

        Parent contenido = loader.load();
        PedidoController controller = loader.getController();

        ventana.setTitle("Procesamiento de pedidos");
        ventana.setScene(new Scene(contenido));
        ventana.setMinWidth(700);
        ventana.setMinHeight(500);

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