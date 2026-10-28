package com.desarrollo.caso1.service;

import com.desarrollo.caso1.model.Inventario;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Agente extends Task<Void> {

    private final int id;
    private final Inventario inventario;
    private final Consumer<String> alRegistrar;

    public Agente(
            int id,
            Inventario inventario,
            Consumer<String> alRegistrar
    ) {
        this.id = id;
        this.inventario = inventario;
        this.alRegistrar = alRegistrar;
    }

    @Override
    protected Void call() throws Exception {
        for (int intento = 1; intento <= 10; intento++) {
            if (isCancelled()) {
                return null;
            }

            Thread.sleep(
                    ThreadLocalRandom.current().nextInt(250, 551)
            );

            if (isCancelled()) {
                return null;
            }

            boolean aceptada = inventario.reservar();

            String mensaje =
                    "Agente " + id
                            + " | Solicitud " + intento
                            + " | " + (aceptada ? "CONFIRMADA" : "RECHAZADA");

            Platform.runLater(() -> alRegistrar.accept(mensaje));
        }

        return null;
    }
}