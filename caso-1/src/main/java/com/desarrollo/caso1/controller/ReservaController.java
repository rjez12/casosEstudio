package com.desarrollo.caso1.controller;

import com.desarrollo.caso1.model.Inventario;
import com.desarrollo.caso1.service.Agente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservaController {

    @FXML
    private Label disponibles;

    @FXML
    private Label confirmadas;

    @FXML
    private Label rechazadas;

    @FXML
    private Label estado;

    @FXML
    private Label agentes;

    @FXML
    private Label bloqueados;

    @FXML
    private Button iniciar;

    @FXML
    private TextArea registro;

    @FXML
    private TextArea historial;

    private Inventario inventario = new Inventario();
    private ExecutorService ejecutor;

    private boolean enCurso;
    private boolean huboError;
    private int simulacion;
    private int terminados;
    private int intentosBloqueados;

    @FXML
    private void initialize() {
        actualizarContadores();
    }

    @FXML
    private void iniciarSimulacion() {
        if (enCurso) {
            intentosBloqueados++;
            bloqueados.setText(
                    "Inicios bloqueados: " + intentosBloqueados
            );
            estado.setText("Ya hay una simulación en ejecución");
            return;
        }

        enCurso = true;
        huboError = false;
        terminados = 0;
        intentosBloqueados = 0;
        simulacion++;

        inventario = new Inventario();
        registro.clear();

        actualizarContadores();
        agentes.setText("Agentes creados: 3 | Terminados: 0/3");
        bloqueados.setText("Inicios bloqueados: 0");
        estado.setText("Simulación " + simulacion + " en ejecución");
        iniciar.setText("Intentar iniciar otra vez");

        ejecutor = Executors.newFixedThreadPool(3);

        for (int id = 1; id <= 3; id++) {
            int numeroAgente = id;

            Agente agente = new Agente(id, inventario, mensaje -> {
                actualizarContadores();
                registro.appendText(mensaje + System.lineSeparator());
            });

            agente.setOnSucceeded(evento -> finalizarAgente());

            agente.setOnFailed(evento -> {
                huboError = true;

                registro.appendText(
                        "Error del agente " + numeroAgente + ": "
                                + agente.getException()
                                + System.lineSeparator()
                );

                finalizarAgente();
            });

            ejecutor.execute(agente);
        }

        ejecutor.shutdown();
    }

    private void actualizarContadores() {
        Inventario.Estado datos = inventario.consultar();

        disponibles.setText(String.valueOf(datos.disponibles()));
        confirmadas.setText(String.valueOf(datos.confirmadas()));
        rechazadas.setText(String.valueOf(datos.rechazadas()));
    }

    private void finalizarAgente() {
        terminados++;

        agentes.setText(
                "Agentes creados: 3 | Terminados: " + terminados + "/3"
        );

        if (terminados < 3) {
            return;
        }

        actualizarContadores();
        Inventario.Estado datos = inventario.consultar();

        boolean correcta =
                !huboError
                        && datos.disponibles() == 0
                        && datos.confirmadas() == 20
                        && datos.rechazadas() == 10;

        historial.appendText(
                "Simulación " + simulacion
                        + " | Confirmadas: " + datos.confirmadas()
                        + " | Rechazadas: " + datos.rechazadas()
                        + " | Disponibles: " + datos.disponibles()
                        + " | Inicios bloqueados: " + intentosBloqueados
                        + " | " + (correcta ? "CORRECTA" : "REVISAR")
                        + System.lineSeparator()
        );

        estado.setText(
                correcta
                        ? "Simulación terminada correctamente"
                        : "Simulación terminada con errores"
        );

        enCurso = false;
        iniciar.setText("Nueva simulación");
    }

    public void cerrar() {
        if (ejecutor != null && !ejecutor.isTerminated()) {
            ejecutor.shutdownNow();
        }
    }
}