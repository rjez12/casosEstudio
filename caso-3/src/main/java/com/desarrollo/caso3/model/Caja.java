package com.desarrollo.caso3.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Caja {

    public static final int VENDEDORES = 3;
    public static final int VENTAS_POR_VENDEDOR = 5;
    public static final int PRECIO = 100;
    public static final int TOTAL_VENTAS =
            VENDEDORES * VENTAS_POR_VENDEDOR;

    private int ventas;
    private int recaudacion;
    private boolean cancelada;

    private final int[] porVendedor = new int[VENDEDORES];
    private final List<Venta> registro = new ArrayList<>();

    public synchronized boolean registrarVenta(int vendedor) {
        if (vendedor < 1 || vendedor > VENDEDORES) {
            throw new IllegalArgumentException("Vendedor inválido");
        }

        if (cancelada
                || porVendedor[vendedor - 1] >= VENTAS_POR_VENDEDOR) {
            return false;
        }

        // El registro y los totales se confirman bajo el mismo bloqueo.
        Venta venta = new Venta(
                ventas + 1,
                vendedor,
                porVendedor[vendedor - 1] + 1,
                PRECIO
        );

        registro.add(venta);
        ventas++;
        recaudacion += PRECIO;
        porVendedor[vendedor - 1]++;

        verificar();
        return true;
    }

    public synchronized void solicitarCancelacion() {
        cancelada = true;
    }

    public synchronized boolean estaCancelada() {
        return cancelada;
    }

    public synchronized Estado consultar() {
        verificar();

        List<Integer> avances =
                Arrays.stream(porVendedor).boxed().toList();

        return new Estado(
                ventas,
                recaudacion,
                avances,
                List.copyOf(registro)
        );
    }

    private void verificar() {
        if (ventas < 0
                || ventas > TOTAL_VENTAS
                || ventas != registro.size()
                || ventas != Arrays.stream(porVendedor).sum()
                || recaudacion != ventas * PRECIO) {
            throw new IllegalStateException(
                    "Los datos de la caja no coinciden"
            );
        }
    }

    public record Venta(
            int numero,
            int vendedor,
            int ventaDelVendedor,
            int monto
    ) {}

    public record Estado(
            int ventas,
            int recaudacion,
            List<Integer> porVendedor,
            List<Venta> registro
    ) {}
}