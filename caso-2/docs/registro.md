# Caso 2 — Procesamiento de pedidos con cancelación

Aplicación JavaFX que simula el procesamiento secuencial de treinta pedidos en segundo plano. Cada pedido requiere una espera de 200 ms y se cuenta como completado únicamente después de finalizar su procesamiento.

## Integrantes

- Jimmy Leandro Selva Garcia
- Jesser Jadiel Rodriguez Chavarria

**Asignatura:** Programación de Aplicaciones de Escritorio  
**Indicadores:** IL3.1 e IL3.3  
**Repositorio:** [Enlace al repositorio GitHub]

## Herramientas

- JDK 21.
- JavaFX 21.0.8.
- Maven: [Versión utilizada].
- IntelliJ IDEA como entorno de desarrollo.

Las versiones efectivamente utilizadas deben comprobarse con `java -version`, `mvn -version` y la configuración de `pom.xml`.

## Ejecución

Desde la carpeta principal del repositorio:

```powershell
cd caso-2
mvn clean javafx:run
```

Si el proyecto incluye Maven Wrapper, puede ejecutarse desde `caso-2` con:

```powershell
.\mvnw.cmd clean javafx:run
```

La primera ejecución necesita conexión a Internet para descargar las dependencias. Maven debe utilizar JDK 21.

## Uso

1. Pulsar **Iniciar** para procesar los treinta pedidos.
2. Observar el contador, la barra de progreso y el estado.
3. Pulsar **Cancelar** para solicitar la cancelación.
4. Esperar hasta que el estado indique **Cancelado**.
5. Pulsar **Iniciar** para comenzar una ejecución nueva.
6. Cerrar la ventana para cancelar cualquier procesamiento activo y finalizar la aplicación.

Un inicio durante una ejecución activa se bloquea y queda registrado.

## Estados de la aplicación

| Estado | Significado |
|---|---|
| Listo | La aplicación está preparada para comenzar. |
| En ejecución | El trabajador está procesando pedidos. |
| Cancelando | Se solicitó la cancelación y se espera la terminación del trabajador. |
| Cancelado | El trabajador terminó después de solicitarse la cancelación. |
| Completado | Los treinta pedidos finalizaron correctamente. |
| Error | La tarea terminó con una excepción no correspondiente a una cancelación esperada. |

Estos estados describen la aplicación y son distintos de `Thread.State`.

## Organización del código

- `service/ProcesarPedidos.java`: tarea de procesamiento y cancelación cooperativa.
- `controller/PedidoController.java`: controles, seguimiento, inicio, cancelación y coordinación del cierre.
- `PedidoApplication.java`: carga del FXML y gestión del cierre de la ventana.
- `resources/com/desarrollo/caso2/pedidos-view.fxml`: interfaz.
- `docs/informe.md`: explicación y resultados.
- `docs/registro.md`: aportes del equipo.
- `evidencias/`: capturas de las pruebas.

## Cancelación y seguimiento

Cada ejecución crea una tarea y un trabajador nuevos. La tarea comprueba la cancelación antes de cada pedido y después de la espera de 200 ms.

La cancelación utiliza `Task.cancel(true)`. Si la espera se interrumpe por una cancelación, el pedido en curso no se cuenta.

Un hilo supervisor espera mediante `join()` la terminación efectiva del trabajador. Esta espera ocurre fuera del hilo JavaFX. Solo después se permite una nueva ejecución.

El contador utiliza `AtomicInteger`. La interfaz consulta su valor mediante un `Timeline` y actualiza los controles desde el hilo JavaFX.

Al cancelar se conserva la cantidad completada. Al comenzar una nueva ejecución, el contador se restablece mediante una tarea nueva.

## Cierre

Al cerrar la ventana se solicita la cancelación de la tarea activa. La aplicación termina después de confirmar que el trabajador finalizó, sin bloquear el hilo JavaFX.

Esta implementación utiliza hilos directamente y no crea un `ExecutorService`.

## Pruebas

Se documentan cuatro escenarios:

1. Procesamiento completo.
2. Cancelación durante una espera.
3. Reinicio después de cancelar y bloqueo de inicios duplicados.
4. Cierre con procesamiento activo.

Los resultados reales y las capturas se encuentran en `docs/informe.md` y `evidencias/`.