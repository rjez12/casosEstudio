# Informe técnico — Reserva concurrente de entradas

## Información de la actividad

**Asignatura:** Programación de Aplicaciones de Escritorio  
**Actividad:** Tarea sumativa 1  
**Valor:** 40 puntos  
**Indicadores:** IL3.1 e IL3.2

## Introducción

La reserva de entradas es una operación que requiere controlar el acceso a un recurso limitado. Cuando varios agentes atienden solicitudes al mismo tiempo, una comprobación de disponibilidad sin protección puede ocasionar que se confirme más de una reserva sobre la misma entrada.

La aplicación desarrollada representa este escenario mediante tres agentes concurrentes que comparten un inventario inicial de veinte entradas. Cada agente realiza diez solicitudes, por lo que se generan treinta solicitudes durante una simulación completa.

El sistema confirma las reservas mientras exista disponibilidad y rechaza las solicitudes restantes. La interfaz JavaFX presenta los resultados y permanece operativa durante el procesamiento.

## Objetivo general

Implementar una aplicación de escritorio que utilice concurrencia y sincronización para gestionar reservas sobre un inventario compartido.

## Objetivos específicos

- Ejecutar tres agentes en segundo plano.
- Proteger conjuntamente la comprobación y el descuento de entradas.
- Mantener consistentes los contadores del inventario.
- Actualizar la interfaz desde el hilo JavaFX.
- Evitar la creación de simulaciones duplicadas.
- Permitir nuevas ejecuciones después de finalizar los tres agentes.

## Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Java 21 | Lenguaje y plataforma de ejecución. |
| JavaFX | Ventana, controles y tareas de segundo plano. |
| FXML | Definición de la interfaz. |
| Maven | Administración de dependencias y ejecución. |
| Git | Control de versiones. |
| GitHub | Repositorio y revisión de cambios mediante Pull Requests. |

Las versiones de las dependencias se encuentran declaradas en el archivo `pom.xml`.

## Organización de la solución

La aplicación separa la administración de datos, el procesamiento y la presentación.

| Componente | Responsabilidad |
|---|---|
| `Inventario` | Almacenar entradas disponibles, reservas confirmadas y solicitudes rechazadas. |
| `Agente` | Realizar diez solicitudes sobre el inventario compartido. |
| `ReservaController` | Iniciar los agentes, manejar controles y registrar la finalización. |
| `ReservaApplication` | Cargar el FXML y configurar la ventana. |
| `reserva-view.fxml` | Definir los elementos visuales. |

El paquete `model` contiene el recurso compartido, `service` contiene los agentes y `controller` administra la interacción con la ventana.

## Reglas de funcionamiento

La simulación comienza con veinte entradas disponibles, cero reservas confirmadas y cero solicitudes rechazadas.

Cada solicitud corresponde a una entrada. Si existe disponibilidad, se descuenta una entrada y se incrementa el contador de reservas confirmadas. Si no quedan entradas, se incrementa el contador de solicitudes rechazadas.

Cada agente realiza diez solicitudes. Una simulación completa procesa treinta solicitudes y corresponde a veinte reservas confirmadas, diez solicitudes rechazadas y cero entradas disponibles.

El orden de atención no está determinado. Un agente puede confirmar más reservas que otro debido a la planificación de los hilos.

## Ejecución concurrente

La clase `Agente` extiende `Task<Void>`.

El controlador crea tres tareas y las envía a un `ExecutorService` configurado con tres hilos. Cada tarea recibe la misma instancia de `Inventario`, por lo que todas las solicitudes afectan al mismo recurso.

Las esperas simuladas y las solicitudes se ejecutan en segundo plano. El hilo JavaFX queda disponible para atender los eventos y mostrar los cambios.

Después de enviar las tres tareas, se llama a `shutdown()`. Esta operación impide enviar trabajo adicional al ejecutor, pero permite finalizar las tareas que ya fueron aceptadas.

## Sección crítica

El método `reservar()` utiliza `synchronized`.

La comprobación de disponibilidad y el descuento se realizan dentro del mismo método sincronizado. Ningún agente puede intervenir entre ambas operaciones.

Cuando existe disponibilidad, el descuento y el incremento de reservas confirmadas se realizan antes de liberar el bloqueo. Cuando no existe disponibilidad, el rechazo también se registra dentro de esa sección.

Esta organización evita que dos agentes observen simultáneamente una última entrada y ambos confirmen su reserva.

## Lecturas consistentes

El método `consultar()` también está sincronizado.

Los contadores se consultan conjuntamente y se devuelven mediante el registro `Estado`. La interfaz recibe una representación consistente de los valores, sin acceder directamente a los campos internos.

La implementación comprueba las siguientes condiciones:

```text
entradas disponibles >= 0

entradas disponibles + reservas confirmadas = 20
```

Estas condiciones representan la conservación del inventario.

## Comunicación con la interfaz

Los agentes generan mensajes con su identificador, el número de solicitud y el resultado.

La notificación se envía mediante `Platform.runLater`, permitiendo que el controlador actualice el registro visual y los contadores desde el hilo JavaFX.

Los manejadores de finalización de `Task` también se ejecutan en el hilo JavaFX. Desde ellos se actualiza el número de agentes terminados y se determina cuándo concluyó la simulación.

## Prevención de inicios duplicados

El controlador mantiene la variable `enCurso`.

Antes de crear tareas se consulta esa variable. Si existe una simulación activa, el inicio se bloquea y se incrementa el contador de intentos bloqueados.

La comprobación ocurre antes de crear otro inventario o nuevos agentes, evitando que una segunda solicitud altere la ejecución actual.

## Finalización y reinicio

Cada agente comunica su finalización al controlador. La aplicación permite reiniciar cuando los tres agentes han terminado.

La nueva simulación crea un inventario con los valores iniciales y restablece los contadores de seguimiento. También limpia el registro de solicitudes de la ejecución anterior.

El historial de simulaciones se conserva para mostrar los resultados de ejecuciones anteriores.

## Manejo de errores y cierre

Si una tarea termina con una excepción, el controlador registra el error y evita presentar la simulación como correcta.

Al cerrar la aplicación se solicita la interrupción del ejecutor mediante `shutdownNow()` si todavía existen tareas activas. Las esperas de los agentes son interrumpibles.

El cierre no realiza una espera bloqueante desde el hilo JavaFX.

## Organización del trabajo

El desarrollo se divide en dos áreas:

- Administración del inventario, sincronización y agentes.
- Interfaz FXML, coordinación de tareas y seguimiento de la ejecución.

La revisión, documentación y demostración se realizan de forma conjunta. Los cambios se conservan mediante commits y se integran mediante Pull Requests.

## Evidencias

Las evidencias se encuentran en la carpeta [evidencias](../evidencias/) del caso 1.

## Instrucciones de ejecución

Desde la carpeta `caso-1`:

```powershell
mvn clean javafx:run
```

Desde la raíz del repositorio:

```powershell
mvn -f caso-1/pom.xml clean javafx:run
```

Se requiere JDK 21 y Maven. La primera ejecución puede descargar las dependencias.

## Conclusión

La aplicación utiliza tres tareas concurrentes para atender solicitudes sobre un inventario limitado.

La sincronización conjunta de la comprobación y el descuento protege las entradas frente a accesos simultáneos. Las consultas sincronizadas permiten obtener contadores consistentes y las notificaciones al hilo JavaFX mantienen la separación entre procesamiento y presentación.

El control de ejecución impide duplicados y permite comenzar otra simulación después de finalizar los tres agentes.