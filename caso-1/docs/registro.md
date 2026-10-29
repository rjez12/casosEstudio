# Registro de trabajo — Reserva concurrente de entradas

## Organización del equipo

El trabajo del caso 1 se divide en dos áreas: lógica concurrente e interfaz gráfica.

Jesser se encarga del inventario compartido y los agentes. Jimmy se encarga de la interfaz JavaFX y la coordinación de la simulación.

La documentación y la preparación de la demostración son responsabilidades conjuntas.

## Distribución de responsabilidades

| Responsable | Componentes | Responsabilidades |
|---|---|---|
| Jesser | `Inventario.java` y `Agente.java` | Administrar las entradas, sincronizar las reservas y ejecutar las solicitudes en segundo plano. |
| Jimmy | `ReservaController.java`, `ReservaApplication.java` y `reserva-view.fxml` | Presentar los resultados, coordinar los agentes, bloquear inicios duplicados y gestionar el reinicio. |
| Ambos | Documentación y colaboración | Revisar la solución, documentar el funcionamiento e integrar los cambios mediante GitHub. |

## Trabajo de lógica concurrente

La implementación del inventario concentra las entradas disponibles, las reservas confirmadas y las solicitudes rechazadas.

La comprobación de disponibilidad y el descuento de entradas se protegen dentro de una misma sección crítica mediante `synchronized`.

Las consultas también se sincronizan para devolver los contadores como un estado consistente.

La clase Agente representa una tarea de segundo plano que realiza diez solicitudes. Los tres agentes utilizan una misma instancia del inventario.

## Trabajo de interfaz y coordinación

La interfaz se organiza mediante FXML y muestra los contadores, el registro de solicitudes y el historial de simulaciones.

El controlador crea los tres agentes y coordina su ejecución mediante un grupo de tres hilos.

Las notificaciones de los agentes se trasladan al hilo JavaFX para actualizar los controles.

La variable `enCurso` impide crear simulaciones duplicadas. El reinicio se permite después de finalizar los tres agentes y restablece los datos de la simulación.

## Integración del trabajo

La rama `logica-concurrente` reúne el inventario y los agentes. La rama `interfaz-javafx` incorpora la presentación y su coordinación.

El flujo de integración es:

```text
interfaz-javafx → logica-concurrente → main
```

Los Pull Requests permiten revisar los cambios antes de incorporarlos a la rama de destino.

## Trazabilidad de aportes

La autoría, las fechas y los cambios de cada integrante se identifican mediante el historial de commits y los Pull Requests del repositorio.

Los mensajes de los commits describen las actividades incorporadas, mientras que las diferencias de archivos permiten consultar el contenido de cada aporte.

## Documentación


El informe técnico describe el inventario, la sección crítica, la comunicación con JavaFX y el control de las simulaciones.

Este registro establece la distribución de responsabilidades y la organización de la colaboración.


## Evidencias

Las evidencias se encuentran en la carpeta [evidencias](../evidencias/) del caso 1.