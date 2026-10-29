package com.desarrollo.caso3.controller;

import com.desarrollo.caso3.model.Caja;
import com.desarrollo.caso3.service.Vendedor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CajaController {

    @FXML private Label estado;
    @FXML private Label totalVentas;
    @FXML private Label recaudacion;
    @FXML private Label trabajadores;

    @FXML private Label avance1;
    @FXML private Label avance2;
    @FXML private Label avance3;

    @FXML private ProgressBar progreso1;
    @FXML private ProgressBar progreso2;
    @FXML private ProgressBar progreso3;

    @FXML private Button iniciar;
    @FXML private Button cancelar;

    @FXML private TextArea registro;
    @FXML private TextArea historial;

    private final int esperaMillis;

    private Caja caja = new Caja();
    private final List<Vendedor> tareas = new ArrayList<>();
    private final List<Thread> hilos = new ArrayList<>();

    private Timeline seguimiento;
    private Runnable alCerrar = Platform::exit;

    private boolean enCurso;
    private boolean cerrando;
    private boolean huboError;

    private int simulacion;
    private int ventasMostradas;

    public CajaController() {
        this(650);
    }

    public CajaController(int esperaMillis) {
        this.esperaMillis = esperaMillis;
    }

    @FXML
    private void initialize() {
        seguimiento = new Timeline(
                new KeyFrame(
                        Duration.millis(50),
                        evento -> actualizarVista()
                )
        );

        seguimiento.setCycleCount(Timeline.INDEFINITE);
        actualizarVista();
    }

    @FXML
    private void iniciarSimulacion() {
        if (enCurso || cerrando) {
            return;
        }

        caja = new Caja();
        tareas.clear();
        hilos.clear();
        registro.clear();

        ventasMostradas = 0;
        huboError = false;
        enCurso = true;
        simulacion++;

        iniciar.setDisable(true);
        cancelar.setDisable(false);
        estado.setText("En ejecución");

        actualizarVista();

        for (int id = 1; id <= Caja.VENDEDORES; id++) {
            Vendedor tarea = new Vendedor(id, caja, esperaMillis);

            tarea.setOnFailed(evento -> {
                huboError = true;
                estado.setText("Cancelando");
                detenerVendedores();
            });

            Thread hilo = new Thread(
                    tarea,
                    "caja-vendedor-" + id
            );

            hilo.setDaemon(false);

            tareas.add(tarea);
            hilos.add(hilo);
        }

        hilos.forEach(Thread::start);

        actualizarVista();
        seguimiento.play();

        esperarTerminacion(List.copyOf(hilos));
    }

    @FXML
    private void cancelarSimulacion() {
        if (!enCurso || cerrando) {
            return;
        }

        estado.setText("Cancelando");
        detenerVendedores();
    }

    private void detenerVendedores() {
        caja.solicitarCancelacion();
        cancelar.setDisable(true);

        tareas.forEach(tarea -> tarea.cancel(true));
    }

    private void esperarTerminacion(
            List<Thread> trabajadoresActuales
    ) {
        Thread supervisor = new Thread(() -> {
            boolean interrumpido = false;

            // La espera ocurre fuera del hilo JavaFX.
            for (Thread hilo : trabajadoresActuales) {
                while (hilo.isAlive()) {
                    try {
                        hilo.join();
                    } catch (InterruptedException e) {
                        interrumpido = true;
                    }
                }
            }

            Platform.runLater(this::finalizarSimulacion);

            if (interrumpido) {
                Thread.currentThread().interrupt();
            }
        }, "caja-supervisor");

        supervisor.setDaemon(true);
        supervisor.start();
    }

    private void actualizarVista() {
        Caja.Estado datos = caja.consultar();

        totalVentas.setText(
                datos.ventas() + " / " + Caja.TOTAL_VENTAS
        );

        recaudacion.setText(dinero(datos.recaudacion()));

        trabajadores.setText(
                "Trabajadores activos: " + contarTrabajadoresActivos()
        );

        List<ProgressBar> barras = List.of(
                progreso1, progreso2, progreso3
        );

        List<Label> etiquetas = List.of(
                avance1, avance2, avance3
        );

        for (int i = 0; i < Caja.VENDEDORES; i++) {
            int ventas = datos.porVendedor().get(i);

            barras.get(i).setProgress(
                    ventas / (double) Caja.VENTAS_POR_VENDEDOR
            );

            etiquetas.get(i).setText(
                    ventas + " / " + Caja.VENTAS_POR_VENDEDOR
            );
        }

        for (int i = ventasMostradas;
             i < datos.registro().size();
             i++) {

            Caja.Venta venta = datos.registro().get(i);

            registro.appendText(
                    "Venta " + venta.numero()
                            + " | Vendedor " + venta.vendedor()
                            + " | Venta del vendedor: "
                            + venta.ventaDelVendedor()
                            + " | " + dinero(venta.monto())
                            + " | CONFIRMADA"
                            + System.lineSeparator()
            );
        }

        ventasMostradas = datos.registro().size();
    }

    private void finalizarSimulacion() {
        seguimiento.stop();
        actualizarVista();

        Caja.Estado datos = caja.consultar();

        boolean error = huboError
                || tareas.stream().anyMatch(
                tarea -> tarea.getException() != null
        );

        boolean cancelada = caja.estaCancelada()
                || tareas.stream().anyMatch(Vendedor::isCancelled);

        if (error) {
            estado.setText("Error");

            tareas.stream()
                    .filter(tarea -> tarea.getException() != null)
                    .forEach(tarea -> historial.appendText(
                            "Error: " + tarea.getException()
                                    + System.lineSeparator()
                    ));

        } else if (cancelada) {
            estado.setText("Cancelado");

        } else if (datos.ventas() == Caja.TOTAL_VENTAS) {
            estado.setText("Completado");

        } else {
            estado.setText("Error");
        }

        historial.appendText(
                "Simulación " + simulacion
                        + " | " + estado.getText()
                        + " | Ventas: " + datos.ventas()
                        + " | " + dinero(datos.recaudacion())
                        + " | Registros: " + datos.registro().size()
                        + " | Trabajadores activos: "
                        + contarTrabajadoresActivos()
                        + System.lineSeparator()
        );

        enCurso = false;

        cancelar.setDisable(true);
        iniciar.setDisable(cerrando);
        iniciar.setText("Nueva simulación");

        if (cerrando) {
            alCerrar.run();
        }
    }

    public int contarTrabajadoresActivos() {
        return (int) hilos.stream()
                .filter(Thread::isAlive)
                .count();
    }

    public void configurarCierre(Runnable accion) {
        alCerrar = accion;
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
            detenerVendedores();
        } else {
            seguimiento.stop();
            alCerrar.run();
        }
    }

    private String dinero(int monto) {
        return "C$" + NumberFormat
                .getIntegerInstance(Locale.US)
                .format(monto);
    }
}