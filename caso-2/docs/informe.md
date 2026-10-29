# Informe — Procesamiento de pedidos con cancelación

## Datos generales

**Asignatura:** Programación de Aplicaciones de Escritorio  
**Actividad:** Tarea sumativa 2  
**Indicadores:** IL3.1 e IL3.3  
**Integrantes:** 
- Jesser Jadiel Rodriguez Chavarria
- Jimmy Leandro Selva Garcia
**Fecha de entrega:** 29/10/26

## Objetivo

Desarrollar una aplicación JavaFX que simule el procesamiento secuencial de treinta pedidos en segundo plano, permita cancelar la operación y gestione correctamente el reinicio y el cierre de la aplicación.

## Herramientas utilizadas

- JDK 21.
- JavaFX 21.0.8.
- Maven.
- IntelliJ IDEA.
- Git y GitHub.

## Organización del proyecto

El proyecto se encuentra en la carpeta `caso-2` del repositorio compartido.

- `service`: contiene la tarea que procesa los pedidos.
- `controller`: administra los controles y el ciclo de vida del procesamiento.
- `PedidosApplication.java`: inicia la aplicación y carga la interfaz.
- `resources`: contiene el archivo `pedidos-view.fxml`.
- `docs`: contiene el informe y el registro de trabajo.
- `evidencias`: contiene las capturas de funcionamiento y colaboración.

## Funcionamiento de la aplicación

La interfaz incorpora los botones Iniciar y Cancelar, una barra de progreso, un contador de pedidos completados, un indicador de estado y un registro de ejecuciones.

Al pulsar Iniciar se crea una tarea nueva para procesar treinta pedidos. Cada pedido incluye una espera simulada de 200 milisegundos y se cuenta únicamente después de finalizar esa espera y comprobar que no se solicitó la cancelación.

El procesamiento se ejecuta fuera del hilo JavaFX, permitiendo que la ventana permanezca operativa.

## Procesamiento en segundo plano

La clase `ProcesadorPedidos` extiende `Task<Void>` y ejecuta el procesamiento en un hilo independiente.

El contador utiliza `AtomicInteger` para permitir su consulta segura desde la interfaz mientras el trabajador lo modifica.

Un `Timeline` consulta periódicamente el contador y actualiza los controles desde el hilo JavaFX. El progreso se calcula dividiendo los pedidos completados entre el total de treinta pedidos.

## Cancelación cooperativa

La cancelación se solicita mediante `Task.cancel(true)`.

La tarea comprueba `isCancelled()` antes de cada pedido y después de la espera. También gestiona `InterruptedException`, permitiendo responder a una cancelación durante `Thread.sleep(200)`.

Si la espera se interrumpe por una cancelación, el pedido en curso no se registra como completado. Los pedidos finalizados anteriormente se conservan en el contador.

No se utiliza `Thread.stop()`.

## Control del ciclo de vida

La variable `enCurso` impide iniciar otro procesamiento mientras existe una ejecución activa.

Después de solicitar la cancelación, un hilo supervisor espera mediante `join()` la terminación efectiva del trabajador. Esta espera se realiza fuera del hilo JavaFX.

Una vez confirmada la terminación, el controlador actualiza el estado y permite comenzar otra ejecución.

Cada nueva ejecución crea una tarea y un trabajador nuevos, restableciendo el contador y la barra de progreso.

## Estados de la aplicación

| Estado | Descripción |
|---|---|
| Listo | La aplicación está preparada para iniciar. |
| En ejecución | Los pedidos se están procesando. |
| Cancelando | Se solicitó la cancelación y se espera la terminación del trabajador. |
| Cancelado | El trabajador terminó después de solicitarse la cancelación. |
| Completado | Los treinta pedidos finalizaron correctamente. |
| Error | Ocurrió una excepción inesperada durante el procesamiento. |

Estos estados representan la situación de la aplicación y son distintos de los estados definidos por `Thread.State`.

## Cierre de la aplicación

Al cerrar la ventana se solicita la cancelación de la tarea activa.

La aplicación espera de forma asíncrona la terminación del trabajador y después finaliza JavaFX. Este procedimiento evita bloquear la interfaz durante el cierre.

La implementación utiliza hilos directamente, por lo que no necesita apagar un `ExecutorService`.

## Validación realizada

Se realizaron las comprobaciones de procesamiento completo, cancelación durante la ejecución, bloqueo de inicios duplicados, reinicio después de cancelar y cierre con una tarea activa.

Se verificó el procesamiento de los treinta pedidos, el progreso del 100 % y el estado Completado. También se comprobó que la cancelación conserva el contador parcial y que este deja de cambiar después de terminar el trabajador.

Asimismo, se verificó que una nueva ejecución restablece los datos y que el cierre finaliza sin dejar trabajadores activos.

Las evidencias correspondientes se encuentran en la carpeta `evidencias/`.

## Distribución de responsabilidades

| Integrante | Responsabilidades |
|---|---|
| Jesser | Implementación del procesamiento, cancelación cooperativa y manejo de interrupciones. |
| Jimmy | Interfaz FXML, controlador, progreso, estados, reinicio y cierre. |

Los integrantes participaron en la revisión, validación, documentación y preparación de la demostración.

## Evidencias de colaboración

Los aportes individuales se documentaron mediante commits y Pull Requests en GitHub.

El registro de actividades se encuentra en `docs/registro.md`, donde se identifican las tareas realizadas y sus responsables.

## Instrucciones de ejecución

Abrir una terminal en la carpeta `caso-2` y ejecutar:

```powershell
mvn clean javafx:run
```

Si el proyecto incluye Maven Wrapper, también puede utilizarse:

```powershell
.\mvnw.cmd clean javafx:run
```

La primera ejecución requiere conexión a Internet para descargar las dependencias. Maven debe utilizar JDK 21.

## Conclusión

La aplicación permite procesar pedidos en segundo plano y cancelar la operación de manera cooperativa, conservando la cantidad de pedidos completados.

La separación entre la solicitud de cancelación y la confirmación de terminación permite gestionar el reinicio y el cierre de forma segura. La interfaz presenta el progreso y los estados del procesamiento mediante actualizaciones realizadas desde el hilo JavaFX.