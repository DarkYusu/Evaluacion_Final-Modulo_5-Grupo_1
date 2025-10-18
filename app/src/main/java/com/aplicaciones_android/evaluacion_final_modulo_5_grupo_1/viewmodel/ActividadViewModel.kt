package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel

//region Importaciones
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//endregion

//region Clase ActividadViewModel
class ActividadViewModel : ViewModel() {

    //region Propiedades (LiveData / Estado)
    private val _actividades = MutableLiveData<List<Actividad>>(emptyList())
    val actividades: LiveData<List<Actividad>> = _actividades

    // Estado de carga y errores para que la UI pueda reaccionar
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Mensajes de depuración para mostrar en la UI (ruta, conteo líneas, etc.)
    private val _debugInfo = MutableLiveData<String?>(null)
    val debugInfo: LiveData<String?> = _debugInfo
    //endregion

    //region Funciones públicas (API del ViewModel)
    // Agregar actividad: público, mantiene la API actual pero delega en helpers suspend
    fun agregarActividad(context: Context, actividad: Actividad) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val listaActual = _actividades.value ?: emptyList()
                val nuevaLista = listaActual + actividad
                _actividades.postValue(nuevaLista)
                // Guardar en CSV (suspend)
                try {
                    escribirActividadEnCSV(context, actividad)
                } catch (e: Exception) {
                    Log.e("ActividadVM", "Error al escribir actividad en CSV", e)
                    _error.postValue("Error al escribir CSV: ${e.message}")
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun actualizarActividad(context: Context, original: Actividad, actualizado: Actividad) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val listaActual = _actividades.value?.toMutableList() ?: mutableListOf()
                val index = listaActual.indexOf(original)
                if (index >= 0) {
                    listaActual[index] = actualizado
                    _actividades.postValue(listaActual)
                    try {
                        reescribirCSV(context, listaActual)
                    } catch (e: Exception) {
                        Log.e("ActividadVM", "Error al reescribir CSV", e)
                        _error.postValue("Error al reescribir CSV: ${e.message}")
                    }
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun cargarActividadesDesdeCSV(context: Context) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                try {
                    // Log de depuración con la ruta del archivo interno
                    val path = File(context.filesDir, "actividades.csv").absolutePath
                    Log.i("ActividadVM", "Intentando leer archivo en: $path")

                    val lista = leerActividadesDesdeCSV(context)
                    _actividades.postValue(lista)
                } catch (e: Exception) {
                    Log.e("ActividadVM", "Error al leer CSV", e)
                    _error.postValue("Error al leer CSV: ${e.message}")
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun eliminarActividad(context: Context, actividad: Actividad) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val listaActual = _actividades.value?.toMutableList() ?: mutableListOf()
                if (listaActual.remove(actividad)) {
                    _actividades.postValue(listaActual)
                    try {
                        reescribirCSV(context, listaActual)
                    } catch (e: Exception) {
                        Log.e("ActividadVM", "Error al reescribir CSV", e)
                        _error.postValue("Error al reescribir CSV: ${e.message}")
                    }
                }
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    //endregion

    //region Depuración (debugDumpCSV)
    // Función pública de depuración: devuelve log con existencia y (solo) las primeras líneas
    fun debugDumpCSV(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(context.filesDir, "actividades.csv")
                    Log.i("ActividadVM", "[DEBUG] Ruta archivo: ${file.absolutePath}")
                    if (!file.exists()) {
                        Log.i("ActividadVM", "[DEBUG] El archivo no existe")
                        _error.postValue("Archivo CSV no existe en: ${file.absolutePath}")
                        _debugInfo.postValue("Archivo no encontrado: ${file.absolutePath}")
                        return@withContext
                    }
                    // Leer y loguear hasta 20 líneas para inspección
                    var total = 0
                    var primera: String? = null
                    file.bufferedReader().use { reader ->
                        reader.forEachLine { line ->
                            if (total < 20) Log.i("ActividadVM", "[DEBUG] linea[$total]: $line")
                            if (total == 0) primera = line
                            total++
                        }
                    }
                    val info = "Archivo: ${file.absolutePath} — lineas: $total" + (primera?.let { " — primera: $it" } ?: "")
                    Log.i("ActividadVM", "[DEBUG] $info")
                    _debugInfo.postValue(info)
                } catch (e: Exception) {
                    Log.e("ActividadVM", "[DEBUG] Error durante dump CSV", e)
                    _error.postValue("Error debug CSV: ${e.message}")
                }
            }
        }
    }
    //endregion

    //region Helpers suspend (I/O en Dispatchers.IO)
    // --- Helpers suspend que ejecutan la I/O en Dispatchers.IO ---
    // Ahora usamos openFileOutput/openFileInput para mayor fiabilidad en almacenamiento interno
    private suspend fun escribirActividadEnCSV(context: Context, actividad: Actividad) = withContext(Dispatchers.IO) {
        try {
            // Añadido: esperar 2 segundos antes de escribir el archivo (no bloqueante)
            delay(2000L)
            // Modo append para añadir líneas
            context.openFileOutput("actividades.csv", Context.MODE_APPEND).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { osw ->
                    BufferedWriter(osw).use { writer ->
                        writer.write("${actividad.nombre},${actividad.fecha},${actividad.hora},${actividad.descripcion}\n")
                        writer.flush()
                    }
                }
            }
        } catch (e: Exception) {
            // Re-lanzar para que el llamador lo capture y lo registre
            throw IOException("No se pudo escribir en archivo interno: ${e.message}", e)
        }
    }

    private suspend fun reescribirCSV(context: Context, lista: List<Actividad>) = withContext(Dispatchers.IO) {
        try {
            // Abrir en modo privado (crea o sobrescribe)
            context.openFileOutput("actividades.csv", Context.MODE_PRIVATE).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { osw ->
                    BufferedWriter(osw).use { writer ->
                        lista.forEach {
                            writer.write("${it.nombre},${it.fecha},${it.hora},${it.descripcion}\n")
                        }
                        writer.flush()
                    }
                }
            }
        } catch (e: Exception) {
            throw IOException("No se pudo reescribir archivo interno: ${e.message}", e)
        }
    }

    private suspend fun leerActividadesDesdeCSV(context: Context): List<Actividad> = withContext(Dispatchers.IO) {
        val lista = mutableListOf<Actividad>()
        try {
            // Añadido: esperar 2 segundos antes de leer el archivo
            delay(2000L)

            // Intentar abrir el archivo interno; si no existe, se lanza FileNotFoundException
            context.openFileInput("actividades.csv").use { fis ->
                InputStreamReader(fis, Charsets.UTF_8).use { isr ->
                    BufferedReader(isr).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            val data = line.split(",")
                            if (data.size >= 4) {
                                lista.add(Actividad(data[0], data[1], data[2], data[3]))
                            }
                            line = reader.readLine()
                        }
                    }
                }
            }
        } catch (_: java.io.FileNotFoundException) {
            // Archivo no existe: intentar crear uno vacío para futuras operaciones
            Log.i("ActividadVM", "Archivo actividades.csv no encontrado, creando uno vacío")
            try {
                context.openFileOutput("actividades.csv", Context.MODE_PRIVATE).use { fos ->
                    // Archivo creado vacío
                }
                _debugInfo.postValue("Archivo creado: ${File(context.filesDir, "actividades.csv").absolutePath}")
            } catch (ce: Exception) {
                Log.e("ActividadVM", "No se pudo crear archivo vacio", ce)
                _error.postValue("No se pudo crear archivo CSV: ${ce.message}")
            }
        } catch (e: Exception) {
            Log.e("ActividadVM", "Error leyendo archivo interno", e)
            throw IOException("No se pudo leer el archivo interno: ${e.message}", e)
        }
        lista
    }
    //endregion

}
//endregion
