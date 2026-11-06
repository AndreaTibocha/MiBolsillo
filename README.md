# Mi Bolsillo - Aplicación de Gestión Financiera y Auditoría

Aplicación móvil desarrollada en **Android Studio** (Java) que permite a los usuarios registrar ingresos y gastos con captura de ubicación GPS y evidencia fotográfica, incorporando control de acceso basado en roles (RBAC) con **Firebase Authentication** y persistencia en **Cloud Firestore**.

---

## Características Principales

* **Autenticación y Control de Roles:**
* Inicio de sesión y registro de usuarios con Firebase Authentication.
* Roles diferenciados (`cliente` y `admin`) gestionados en Firestore.
* Restricción de acceso a la pantalla de auditoría e historial exclusivamente para usuarios administradores.


* **Registro de Transacciones Financieras:**
* Registro de movimientos clasificables como `INGRESO` o `GASTO`.
* Captura de monto y descripción detallada.


* **Integración de Hardware Móvil:**
* **GPS / Ubicación:** Captura automática de coordenadas (latitud y longitud) mediante Google Play Services (`FusedLocationProviderClient`).
* **Cámara:** Captura de fotografías de respaldo/recibos almacenadas de forma segura a través de `FileProvider`.


* **Auditoría y Consulta:**
* Visualización de movimientos en lista dinámica (`RecyclerView`).
* Reglas de seguridad en Firestore configuradas para garantizar la privacidad y consistencia de los datos.



---

## Tecnologías Utilizadas

* **Lenguaje:** Java
* **IDE:** Android Studio
* **Backend como Servicio (BaaS):**
* Firebase Authentication (Autenticación por email/password)
* Cloud Firestore (Base de datos NoSQL en tiempo real)


* **Librerías de Android / Jetpack:**
* `androidx.appcompat:appcompat`
* `com.google.android.material:material`
* `com.google.android.gms:play-services-location`
* `androidx.core:core` (`FileProvider`)



---

## Estructura del Proyecto

```text
app/src/main/
├── java/com/example/mibolsillo/
│   ├── LoginActivity.java          # Inicio de sesión, registro y direccionamiento
│   ├── MainActivity.java           # Registro de transacciones, GPS, cámara y cierre de sesión
│   ├── Movimiento.java             # Modelo de datos de las transacciones
│   ├── AuditoriaActivity.java      # Pantalla protegida de auditoría para administradores
│   └── historial/
│       └── MovimientoAdapter.java  # Adaptador para el RecyclerView del historial
├── res/
│   ├── layout/                     # Archivos de interfaz de usuario (XML)
│   └── xml/
│       └── file_paths.xml          # Configuración de rutas para FileProvider
└── AndroidManifest.xml             # Declaración de permisos, actividades y componentes

```

---

## Modelo de Datos (Cloud Firestore)

### Colección: `usuarios`

Identificador de documento: `UID` del usuario en Firebase Auth.

```json
{
  "email": "usuario@ejemplo.com",
  "rol": "admin" // o "cliente"
}

```

### Colección: `movimientos`

Identificador de documento: Automático / Hash Firestore.

```json
{
  "id": "abc123xyz",
  "descripcion": "Pago mensualidad",
  "monto": 2000000.0,
  "tipo": "INGRESO",
  "latitud": 37.42200,
  "longitud": -122.08400,
  "fotoPath": "/storage/emulated/0/.../foto.jpg",
  "fecha": "06/11/2026 11:55",
  "usuarioEmail": "usuario@ejemplo.com"
}

```

---

## Instalación y Configuración

1. **Clonar el repositorio:**
```bash
git clone <URL_DEL_REPOSITORIO>

```


2. **Abrir en Android Studio:**
* Seleccionar **File > Open** y elegir la carpeta raíz del proyecto.
* Esperar a que la sincronización de Gradle finalice con éxito.


3. **Configuración de Firebase:**
* Crear un proyecto en [Firebase Console](https://console.firebase.google.com/).
* Habilitar **Authentication** (método Correo electrónico/Contraseña).
* Crear una base de datos en **Firestore Database**.
* Descargar el archivo `google-services.json` y colocarlo dentro del directorio `/app`.


4. **Ejecución:**
* Conectar un dispositivo físico con depuración USB o iniciar un emulador de Android.
* Presionar **Run 'app'** (`Shift + F10`).
