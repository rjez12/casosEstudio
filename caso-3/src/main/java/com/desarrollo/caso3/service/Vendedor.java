package com.desarrollo.caso3.service;

import com.desarrollo.caso3.model.Caja;
import javafx.concurrent.Task;

public class Vendedor extends Task<Void> {

    private final int id;
    private final Caja caja;
    private final int esperaMillis;

    public Vendedor(int id, Caja caja, int esperaMillis) {
        if (id < 1 || id > Caja.VENDEDORES || esperaMillis < 0) {
            throw new IllegalArgumentException(
                    "Configuración de vendedor inválida"
            );
        }

        this.id = id;
        this.caja = caja;
        this.esperaMillis = esperaMillis;
    }

    @Override
    protected Void call() throws Exception {
        for (int venta = 1;
             venta <= Caja.VENTAS_POR_VENDEDOR;
             venta++) {

            if (isCancelled() || caja.estaCancelada()) {
                return null;
            }

            try {
                Thread.sleep(esperaMillis);
            } catch (InterruptedException e) {
                if (isCancelled() || caja.estaCancelada()) {
                    return null;
                }

                Thread.currentThread().interrupt();
                throw e;
            }

            if (isCancelled() || !caja.registrarVenta(id)) {
                return null;
            }
        }

        return null;
    }
}