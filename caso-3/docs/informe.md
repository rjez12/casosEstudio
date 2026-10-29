# Informe — Caja de ventas concurrente con control de hilos

## Datos generales

**Asignatura:** Programación de Aplicaciones de Escritorio  
**Actividad:** Tarea sumativa 3  
**Indicadores:** IL3.1, IL3.2 e IL3.3  
**Integrantes:** 
- Jesser Jadiel Rodriguez Chavarria
- Jimmy Leandro Selva Garcia
**Fecha de entrega:** 29/10/2026

## Objetivo

Desarrollar una aplicación JavaFX que simule una caja con tres vendedores concurrentes, proteja sus recursos compartidos y permita cancelar, reiniciar y cerrar de forma controlada.

## Herramientas utilizadas

- JDK 21.
- JavaFX 21.0.6.
- Maven.
- IntelliJ IDEA.
- Git y GitHub.

## Funcionamiento

Cada vendedor ejecuta una tarea en segundo plano y realiza hasta cinco ventas de C$100.

La interfaz permite iniciar y cancelar la simulación. También presenta el avance por vendedor, el total de ventas, la recaudación, el estado y los registros de ventas confirmadas.

Una ejecución completa produce quince ventas y C$1,500.

## Organización del proyecto

La clase Caja administra los datos compartidos. La clase Vendedor representa cada tarea concurrente.

CajaController coordina los trabajadores, actualiza la interfaz y gestiona la cancelación, el reinicio y el cierre.

CajaApplication carga el archivo FXML y configura la ventana.

## Sincronización

El método registrarVenta() utiliza synchronized para proteger conjuntamente el registro, el contador de ventas, la recaudación y el avance individual.

Cada venta confirmada contiene su número, vendedor, número de venta del vendedor y monto.

El método consultar() utiliza el mismo bloqueo y devuelve copias inmutables de los registros y avances. Esto permite consultar un estado consistente mientras trabajan los vendedores.

Se comprueba que el total de ventas coincida con la cantidad de registros y con la suma de las ventas individuales. También se verifica que la recaudación sea igual al total de ventas multiplicado por C$100.

## Cancelación cooperativa

La cancelación marca la caja como cancelada y solicita cancel(true) en las tres tareas.

Los vendedores comprueban la solicitud antes de continuar y gestionan la interrupción de sus esperas.

Si una venta ya se encontraba dentro de la sección crítica antes de la cancelación, puede terminar completamente. Las ventas posteriores se rechazan.

La sincronización impide publicar registros parciales o diferencias entre el contador y la recaudación.

No se utiliza Thread.stop().

## Ciclo de vida

El botón Iniciar permanece deshabilitado mientras existe una simulación activa. El controlador también comprueba la variable enCurso para impedir duplicados.

Un hilo supervisor espera mediante join() la terminación efectiva de los tres vendedores. Esa espera ocurre fuera del hilo JavaFX.

Después de confirmar la terminación se permite iniciar otra simulación.

Cada ejecución crea una caja, tres tareas y tres trabajadores nuevos. Los datos de la simulación se restablecen y el historial de ejecuciones se conserva.

## Interfaz y estados

La interfaz se define mediante FXML. Un Timeline consulta periódicamente la caja y actualiza los controles desde el hilo JavaFX.

Los estados contemplados son Listo, En ejecución, Cancelando, Cancelado, Completado y Error.

Estos estados representan la situación de la aplicación y son distintos de Thread.State.

## Cierre

Al cerrar la ventana se solicita la cancelación de los vendedores activos.

La espera de terminación se realiza de forma asíncrona. Cuando los tres trabajadores han terminado, se finaliza JavaFX.

La implementación utiliza hilos directamente y no requiere un ExecutorService.

## Responsabilidades

| Integrante | Responsabilidades |
|---|---|
| Jesser | Caja compartida, sincronización, vendedores y revisión de la consistencia. |
| Jimmy | Interfaz FXML, coordinación, seguimiento, cancelación, reinicio y cierre. |

Los integrantes participan en la validación, la documentación y la preparación de la demostración.

## Colaboración

Los aportes se documentan mediante commits y Pull Requests en GitHub.

El registro de actividades se encuentra en `docs/registro.md`.


## Ejecución

Desde la carpeta `caso-3`:

```powershell
mvn clean javafx:run
```

## Conclusión

La aplicación integra concurrencia, sincronización y cancelación cooperativa.

La protección conjunta de las ventas mantiene la correspondencia entre registros, contador y recaudación. La espera asíncrona de los trabajadores permite controlar el reinicio y el cierre sin bloquear la interfaz.