# Aplicación Simple - Grupo 1 (nombres pendientes)

Descripción
- Aplicación Android sencilla para registrar actividades con título, descripción, fecha y hora.
- Permite agregar actividades y ver el listado de actividades registradas.

Características
- Crear una nueva actividad con: Título, Descripción, Fecha y Hora.
- Validación básica de formato de fecha (dd/MM/yyyy) y hora (HH:mm).
- Selección de fecha y hora mediante diálogos nativos (DatePicker y TimePicker).
- Almacenamiento y listado mediante un ViewModel compartido.

Tecnologías
- Kotlin
- Android SDK
- Gradle
- Android Studio

Requisitos
- Android Studio (recomendado)
- JDK 11 o superior
- Dispositivo Android o emulador con nivel de API compatible

Instalación y ejecución
1. Abre Android Studio y selecciona "Open an existing Android Studio project".
2. Navega a la carpeta del proyecto: `Evaluacion_FinalModulo_5Grupo_1` y ábrela.
3. Espera a que Gradle sincronice el proyecto.
4. Ejecuta la app desde Android Studio con el botón Run o usando un emulador/dispositivo.

Desde la terminal (Windows) también puedes compilar/instalar manualmente:

```cmd
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Uso
- Abrir la pantalla de nueva actividad.
- Completar: `Título`, `Descripción`, `Fecha` (dd/MM/yyyy) y `Hora` (HH:mm).
- Pulsar "Agregar actividad" para guardar y volver al listado.
- Pulsar "Ver actividades" para abrir el listado de actividades.

Estructura relevante del proyecto
- Código principal: `app/src/main/java/com/aplicaciones_android/evaluacion_final_modulo_5_grupo_1/`
  - `NuevaActividadFragment.kt` — pantalla para crear actividades.
  - `ActividadViewModel` — lógica de negocio y persistencia en memoria/almacenamiento.
  - `FragmentListadoActividades` — muestra la lista de actividades.
- Layouts: `app/src/main/res/layout/` — archivos XML de las interfaces.

Validaciones y reglas
- Todos los campos son obligatorios.
- Fecha debe respetar el formato `dd/MM/yyyy` y corresponder a una fecha real.
- Hora debe respetar el formato `HH:mm` (24 horas).

Contribuir
- Proyecto desarrollado por Grupo 1 (nombres pendientes).
- Para contribuir: abre issues o pull requests en el repositorio.

Licencia
- A definir por el equipo (pendiente).

Contacto
- Grupo 1 — (nombres pendientes)

Notas
- Este README es una plantilla básica; el equipo puede ampliarlo con instrucciones de pruebas, screenshots y detalles de la arquitectura.

