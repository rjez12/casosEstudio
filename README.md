# Programación de Aplicaciones de Escritorio

Repositorio de tres aplicaciones JavaFX centradas en concurrencia, sincronización de recursos compartidos y cancelación cooperativa.

Los casos presentan una progresión: el primero protege un inventario compartido, el segundo controla la cancelación de un trabajador y el tercero integra múltiples trabajadores con registros y totales sincronizados.

Cada aplicación tiene su propio proyecto Maven, interfaz FXML, documentación y carpeta de evidencias.

## Resumen de los casos

| Caso | Aplicación | Procesamiento | Conceptos principales |
|---|---|---|---|
| 1 | Reserva de entradas | Tres agentes realizan treinta solicitudes sobre veinte entradas. | Concurrencia y sección crítica. |
| 2 | Procesamiento de pedidos | Un trabajador procesa treinta pedidos secuencialmente. | Cancelación, interrupción y ciclo de vida. |
| 3 | Caja de ventas | Tres vendedores realizan cinco ventas de C$100 cada uno. | Concurrencia, sincronización y cancelación conjunta. |

Cada tarea tiene un valor de 40 puntos.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje y plataforma de ejecución. |
| JavaFX | Interfaz de escritorio y tareas de segundo plano. |
| FXML | Definición de las vistas. |
| Maven | Dependencias, compilación y ejecución. |
| Git | Historial de cambios y ramas. |
| GitHub | Colaboración y revisión mediante Pull Requests. |

Las versiones de las dependencias se declaran en el `pom.xml` de cada caso.

## Estructura del repositorio

```text
casosEstudio/
├── README.md
├── caso-1/
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── docs/
│   ├── evidencias/
│   ├── pom.xml
├── caso-2/
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── docs/
│   ├── evidencias/
│   ├── pom.xml
└── caso-3/
    ├── src/main/java/
    ├── src/main/resources/
    ├── docs/
    ├── evidencias/
    ├── pom.xml
```

Las carpetas son proyectos independientes dentro de un mismo repositorio. Cada aplicación se compila y ejecuta mediante su propio `pom.xml`.

# Caso 1 — Reserva concurrente de entradas

## Propósito

Gestionar reservas sobre un recurso limitado cuando varios agentes atienden solicitudes simultáneamente.

**Indicadores:** IL3.1 e IL3.2.

## Escenario

El inventario comienza con veinte entradas. Tres agentes realizan diez solicitudes de una entrada cada uno.

Las reservas se confirman mientras exista disponibilidad. Cuando el inventario llega a cero, las solicitudes restantes se rechazan.

| Regla | Valor |
|---|---:|
| Entradas iniciales | 20 |
| Agentes concurrentes | 3 |
| Solicitudes por agente | 10 |
| Solicitudes totales | 30 |
| Confirmadas al completar | 20 |
| Rechazadas al completar | 10 |

## Diseño

- `Inventario` concentra los contadores.
- `Agente` representa una tarea de diez solicitudes.
- `ReservaController` coordina las tareas y los controles.
- `ReservaApplication` carga la ventana.
- `reserva-view.fxml` define la vista.

## Protección del inventario

La comprobación de disponibilidad y el descuento se realizan dentro del mismo método sincronizado.

Las lecturas también están sincronizadas, de modo que la interfaz consulta los contadores como un conjunto consistente.

Se mantiene:

```text
disponibles >= 0
disponibles + confirmadas = 20
```

## Comportamiento de la interfaz

La ventana muestra disponibilidad, confirmaciones, rechazos, agentes terminados y solicitudes procesadas.

Los agentes trabajan fuera del hilo JavaFX. Los mensajes se trasladan a ese hilo para actualizar los controles.

Los inicios duplicados se bloquean antes de crear tareas adicionales. Una nueva simulación se permite cuando terminan los tres agentes y comienza con los datos restablecidos.

## Ejecutar

Desde la raíz del repositorio:

```powershell
mvn -f caso-1/pom.xml clean javafx:run
```

## Documentación y evidencias
- [Informe técnico](caso-1/docs/informe.md).
- Las evidencias se encuentran en [caso-1/evidencias](caso-1/evidencias/).

# Caso 2 — Procesamiento de pedidos con cancelación

## Propósito

Procesar trabajo secuencial en segundo plano y gestionar correctamente su cancelación, reinicio y cierre.

**Indicadores:** IL3.1 e IL3.3.

## Escenario

Un trabajador procesa treinta pedidos, uno por uno. Cada pedido incluye una espera simulada de 200 milisegundos.

El contador aumenta únicamente después de terminar el procesamiento del pedido.

| Regla | Valor |
|---|---:|
| Pedidos | 30 |
| Trabajadores de procesamiento | 1 |
| Espera por pedido | 200 ms |
| Progreso al completar | 100 % |

## Diseño

- `ProcesadorPedidos` ejecuta el ciclo de procesamiento.
- `PedidosController` administra los controles y el ciclo de vida.
- `PedidosApplication` carga la interfaz y configura el cierre.
- `pedidos-view.fxml` define la vista.

## Cancelación cooperativa

El controlador solicita la cancelación mediante `Task.cancel(true)`.

La tarea consulta la solicitud antes de cada pedido y después de la espera. También gestiona `InterruptedException` para responder a una cancelación durante `Thread.sleep()`.

Un pedido cuya espera se interrumpe no se cuenta como completado. Los pedidos finalizados anteriormente se conservan.

No se utiliza `Thread.stop()`.

## Terminación y reinicio

La solicitud de cancelación y la terminación efectiva son momentos distintos.

Un hilo supervisor espera al trabajador mediante `join()`, fuera del hilo JavaFX. El reinicio se permite después de confirmar la terminación.

Cada ejecución crea una tarea y un trabajador nuevos, y restablece el contador y el progreso.

## Interfaz y estados

La interfaz incluye Iniciar, Cancelar, barra de progreso, contador y registro.

| Estado | Significado |
|---|---|
| Listo | Preparado para iniciar. |
| En ejecución | Procesando pedidos. |
| Cancelando | Cancelación solicitada; se espera la terminación. |
| Cancelado | El trabajador terminó después de cancelar. |
| Completado | Finalizaron los treinta pedidos. |
| Error | Ocurrió una excepción inesperada. |

El contador utiliza `AtomicInteger`. Un `Timeline` permite consultar su valor y actualizar los controles desde el hilo JavaFX.

## Cierre

Cerrar la ventana solicita la cancelación del procesamiento activo.

La aplicación espera la terminación de forma asíncrona y después finaliza JavaFX.

## Ejecutar

Desde la raíz del repositorio:

```powershell
mvn -f caso-2/pom.xml clean javafx:run
```

## Documentación y evidencias
- [Informe técnico](caso-2/docs/informe.md).
- Las evidencias se encuentran en [caso-2/evidencias](caso-2/evidencias/).

# Caso 3 — Caja de ventas concurrente con control de hilos

## Propósito

Integrar concurrencia, sincronización y cancelación cooperativa sobre una caja compartida.

**Indicadores:** IL3.1, IL3.2 e IL3.3.

## Escenario

Tres vendedores trabajan concurrentemente. Cada vendedor realiza cinco ventas de C$100.

| Regla | Valor |
|---|---:|
| Vendedores | 3 |
| Ventas por vendedor | 5 |
| Monto por venta | C$100 |
| Ventas al completar | 15 |
| Recaudación al completar | C$1,500 |

El orden de confirmación puede variar entre vendedores.

## Diseño

- `Caja` administra el recurso compartido y sus registros.
- `Vendedor` representa una tarea de cinco ventas.
- `CajaController` coordina trabajadores, progreso y controles.
- `CajaApplication` configura la ventana y el cierre.
- `caja-view.fxml` define la interfaz.

## Venta completa y sincronización

El registro, el contador, la recaudación y el avance individual se confirman dentro del mismo método sincronizado.

Cada venta contiene:

- Número general de venta.
- Identificador del vendedor.
- Número de venta del vendedor.
- Monto confirmado.

Las consultas utilizan el mismo bloqueo y devuelven copias inmutables de los datos.

Se mantiene:

```text
ventas confirmadas = cantidad de registros

ventas confirmadas = suma de ventas individuales

recaudación = ventas confirmadas × C$100
```

## Cancelación de vendedores

La cancelación marca primero la caja como cancelada y después solicita la interrupción de las tres tareas.

Una venta que ya se encontraba en la sección crítica puede terminar completamente antes de registrarse la cancelación. Las siguientes ventas se rechazan.

Los registros y totales se conservan sin publicar operaciones parciales.

## Terminación, reinicio y cierre

El botón Iniciar permanece deshabilitado durante la simulación y mientras se espera a los vendedores.

Un supervisor confirma la terminación de los tres hilos fuera del hilo JavaFX.

Después se permite comenzar otra simulación, creando una caja, tres tareas y tres trabajadores nuevos.

Al cerrar la ventana se solicita la cancelación y se finaliza JavaFX después de terminar los trabajadores.

## Interfaz

La ventana muestra:

- Barra de progreso y contador por vendedor.
- Total de ventas confirmadas.
- Recaudación.
- Estado de la simulación.
- Trabajadores activos.
- Registro de ventas.
- Historial de simulaciones.

Las actualizaciones visuales se realizan desde el hilo JavaFX.

## Ejecutar

Desde la raíz del repositorio:

```powershell
mvn -f caso-3/pom.xml clean javafx:run
```

## Documentación y evidencias


- [Informe técnico](caso-3/docs/informe.md).
- Las evidencias se encuentran en [caso-3/evidencias](caso-3/evidencias/).

# Requisitos y configuración

## Entorno

Se necesita JDK 21 y Maven instalado.

Para consultar las versiones:

```powershell
java -version
mvn -version
```

Maven debe utilizar JDK 21. La descarga inicial de dependencias requiere conexión a Internet.

## IntelliJ IDEA

Cada caso debe incorporarse como proyecto Maven mediante su archivo `pom.xml`.

Después de importar:

1. Seleccionar JDK 21.
2. Cargar las dependencias Maven.
3. Mantener `src/main/java` como carpeta de código fuente.
4. Mantener `src/main/resources` como carpeta de recursos.
5. Ejecutar el caso correspondiente.

Los archivos FXML se almacenan en recursos y se cargan mediante `FXMLLoader`. El archivo `module-info.java` declara las dependencias JavaFX y abre el paquete de controladores para FXML.

## Compilación independiente

Desde la raíz del repositorio:

```powershell
mvn -f caso-1/pom.xml clean compile
mvn -f caso-2/pom.xml clean compile
mvn -f caso-3/pom.xml clean compile
```

Cada comando compila únicamente el proyecto indicado.

# Organización y colaboración

El trabajo se distribuye entre lógica de procesamiento, interfaz y coordinación. La revisión, documentación y demostración se realizan conjuntamente.

Las ramas de lógica e interfaz permiten separar los cambios de cada caso. Los Pull Requests reúnen el trabajo antes de integrarlo en main.

Los commits y los registros de colaboración documentan los aportes. Cada carpeta de caso conserva su propia documentación y evidencias.

# Relación entre los casos

| Aspecto | Caso 1 | Caso 2 | Caso 3 |
|---|---|---|---|
| Trabajo principal | Reservar entradas. | Procesar pedidos. | Registrar ventas. |
| Trabajadores | Tres agentes. | Un procesador. | Tres vendedores. |
| Recurso compartido | Inventario y contadores. | Contador de avance. | Registros, ventas y recaudación. |
| Protección | Métodos sincronizados. | Contador atómico. | Confirmación y consulta sincronizadas. |
| Cancelación | Interrupción al cerrar. | Cancelación del trabajador. | Cancelación de los tres vendedores. |
| Reinicio | Después de finalizar los agentes. | Después de confirmar la terminación. | Después de confirmar la terminación de los tres vendedores. |

Los tres proyectos separan el trabajo de segundo plano de las actualizaciones visuales, respetando el hilo JavaFX y organizando el código por responsabilidades.