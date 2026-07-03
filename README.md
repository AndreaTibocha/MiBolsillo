# MiBolsillo - Sistema de Gestión de Finanzas Personales

Este repositorio contiene el avance del proyecto de desarrollo para la aplicación móvil **MiBolsillo**, una herramienta diseñada para el control, registro y visualización de flujos financieros personales (Ingresos y Gastos) de manera local y eficiente.

El desarrollo está estructurado bajo principios de arquitectura limpia y modularidad en Android Nativo utilizando Java.

---

## Arquitectura del Proyecto

A fin de garantizar un código escalable, mantenible y con alta cohesión, la aplicación implementa una estructura organizativa basada en **Package by Feature** (Paquetes por Características). Los componentes se dividen de la siguiente manera:

* **`com.example.mibolsillo` (Capa de Datos y Modelamiento):**
    * `AdminSQLiteHelper.java`: Clase encargada de administrar el ciclo de vida de la base de datos relacional local (creación de esquemas y control de versiones).
    * `Movimiento.java`: Modelo de datos (POJO) que representa la abstracción de una transacción financiera.
* **`com.example.mibolsillo.historial` (Módulo de Consulta y Presentación):**
    * `MainActivity.java`: Actividad principal de arranque encargada de inicializar el listado y coordinar las consultas a la base de datos.
    * `MovimientoAdapter.java`: Adaptador especializado para el mapeo dinámico de datos dentro del contenedor de tarjetas.
* **`com.example.mibolsillo.registro` (Módulo de Entrada de Datos):**
    * `FormularioActivity.java`: Interfaz dedicada exclusivamente a la captura, validación e inserción de nuevos registros financieros.

---

## Tecnologías y Componentes Implementados

1.  **Persistencia Local (SQLite):** Utilización de la API nativa `SQLiteDatabase` para el almacenamiento permanente de las transacciones, garantizando la seguridad y disponibilidad de la información sin requerir conectividad a redes.
2.  **View Binding:** Integración activa del parámetro de compilación `viewBinding = true` en el módulo Gradle para el acceso directo y seguro a los elementos gráficos (evitando excepciones de tipo `NullPointerException` tradicionales de `findViewById`).
3.  **UI Dinámica (RecyclerView):** Implementación de layouts adaptativos mediante el uso de contenedores eficientes. El sistema evalúa el estado del objeto en tiempo de ejecución para modificar el estilo visual (colores y recursos vectoriales) dependiendo de si el flujo corresponde a un ingreso o a un gasto.

---

## Estatus del Avance de Desarrollo

Actualmente, el proyecto se encuentra en la **Fase de Integración de Ciclo de Vida**. 

* **Funcionalidades completadas:** Creación y persistencia relacional en base de datos local, navegación entre actividades, validación de formularios, diseño adaptativo de tarjetas según el tipo de movimiento y lectura inicial del historial de transacciones.
* **Módulo en desarrollo:** Sincronización reactiva inmediata entre actividades mediante el refresco automático de punteros en el adaptador al retornar al hilo principal del ciclo de vida (`onResume()`).

---
Este proyecto ha sido desarrollado con fines estrictamente académicos.
