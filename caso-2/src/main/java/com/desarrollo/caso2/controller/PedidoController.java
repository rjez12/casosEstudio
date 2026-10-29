package com.desarrollo.caso2.controller;

import com.desarrollo.caso2.service.ProcesarPedido;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

public class PedidoController {

    @FXML
    private Label estado;

    @FXML
    private Label contador;

    @FXML
    private Label porcentaje;

    @FXML
    private ProgressBar progreso;

    @FXML
    private Button iniciar;

    @FXML
    private Button cancelar;

    @FXML
    private TextArea registro;

    private ProcesarPedido tarea;
    private Thread trabajador;
    private Timeline seguimiento;

    private boolean enCurso;
    private boolean cerrando;
    private int ejecucion;

    @FXML
    private void initialize() {
        seguimiento = new Timeline(
                new KeyFrame(
                        Duration.millis(50),
                        evento -> actualizarProgreso()
                )
        );

        seguimiento.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    private void iniciarProcesamiento() {
        if (cerrando) {
            return;
        }

        if (enCurso) {
            registrar("Inicio bloqueado: hay una ejecución activa.");
            return;
        }

        tarea = new ProcesarPedido();
        ejecucion++;
        enCurso = true;

        actualizarProgreso();
        estado.setText("En ejecución");
        cancelar.setDisable(false);
        registrar("Ejecución " + ejecucion + " iniciada.");

        trabajador = new Thread(tarea, "pedidos-trabajador");
        trabajador.setDaemon(false);

        trabajador.start();
        seguimiento.play();

        esperarTerminacion(trabajador);
    }

    @FXML
    private void cancelarProcesamiento() {
        if (!enCurso || tarea == null) {
            return;
        }

        boolean aceptada = tarea.cancel(true);

        if (aceptada) {
            estado.setText("Cancelando");
            cancelar.setDisable(true);
            registrar("Cancelación solicitada.");
        }
    }

    private void esperarTerminacion(Thread hilo) {
        Thread supervisor = new Thread(() -> {
            boolean interrumpido = false;

            // Espera fuera del hilo JavaFX hasta que el trabajador termine.
            while (hilo.isAlive()) {
                try {
                    hilo.join();
                } catch (InterruptedException e) {
                    interrumpido = true;
                }
            }

            Platform.runLater(this::finalizarProcesamiento);

            if (interrumpido) {
                Thread.currentThread().interrupt();
            }
        }, "pedidos-supervisor");

        supervisor.setDaemon(true);
        supervisor.start();
    }

    private void actualizarProgreso() {
        int cantidad = tarea == null ? 0 : tarea.getCompletados();
        double avance = cantidad / (double) ProcesarPedido.TOTAL;

        contador.setText(
                "Pedidos completados: " + cantidad
                        + " / " + ProcesarPedido.TOTAL
        );

        progreso.setProgress(avance);
        porcentaje.setText(
                Math.round(avance * 100) + " %"
        );
    }

    private void finalizarProcesamiento() {
        seguimiento.stop();
        actualizarProgreso();

        if (tarea.isCancelled()) {
            estado.setText("Cancelado");
        } else if (tarea.getException() != null) {
            estado.setText("Error");
            registrar("Error: " + tarea.getException().getMessage());
        } else {
            estado.setText("Completado");
        }

        registrar(
                "Ejecución " + ejecucion
                        + ": " + estado.getText()
                        + " | Completados: " + tarea.getCompletados()
                        + " | Trabajador activo: " + trabajador.isAlive()
        );

        enCurso = false;
        cancelar.setDisable(true);

        if (cerrando) {
            Platform.exit();
        }
    }

    private void registrar(String mensaje) {
        registro.appendText(mensaje + System.lineSeparator());
    }

    public void solicitarCierre() {
        if (cerrando) {
            return;
        }

        cerrando = true;
        iniciar.setDisable(true);
        cancelar.setDisable(true);

        if (enCurso) {
            estado.setText("Cancelando");
            tarea.cancel(true);
        } else {
            seguimiento.stop();
            Platform.exit();
        }
    }
}