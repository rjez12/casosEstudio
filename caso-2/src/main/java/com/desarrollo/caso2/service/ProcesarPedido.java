package com.desarrollo.caso2.service;

import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class ProcesarPedido extends Task<Void> {

    public static final int TOTAL = 30;

    private final AtomicInteger completados = new AtomicInteger();

    @Override
    protected Void call() throws Exception {
        for (int pedido = 1; pedido <= TOTAL; pedido++) {
            if (isCancelled()) {
                return null;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                if (isCancelled()) {
                    return null;
                }

                Thread.currentThread().interrupt();
                throw e;
            }

            if (isCancelled()) {
                return null;
            }

            completados.incrementAndGet();
        }

        return null;
    }

    public int getCompletados() {
        return completados.get();
    }
}