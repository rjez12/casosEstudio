package com.desarrollo.caso1.model;

public class Inventario {

    public static final int TOTAL = 20;

    private int disponibles = TOTAL;
    private int confirmadas;
    private int rechazadas;

    public synchronized boolean reservar() {
        boolean aceptada = disponibles > 0;

        if (aceptada) {
            disponibles--;
            confirmadas++;
        } else {
            rechazadas++;
        }

        verificarEstado();
        return aceptada;
    }

    public synchronized Estado consultar() {
        verificarEstado();
        return new Estado(disponibles, confirmadas, rechazadas);
    }

    private void verificarEstado() {
        if (disponibles < 0 || disponibles + confirmadas != TOTAL) {
            throw new IllegalStateException(
                    "Inventario inconsistente: disponibles=" + disponibles
                            + ", confirmadas=" + confirmadas
            );
        }
    }

    public record Estado(
            int disponibles,
            int confirmadas,
            int rechazadas
    ) {}
}