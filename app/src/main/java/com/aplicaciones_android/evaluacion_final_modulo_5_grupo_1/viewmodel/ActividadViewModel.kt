package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActividadViewModel : ViewModel() {
    private val _actividades = MutableLiveData<List<Actividad>>(emptyList())
    val actividades: LiveData<List<Actividad>> = _actividades

    fun agregarActividad(context: Context, actividad: Actividad) {
        viewModelScope.launch(Dispatchers.IO) {
            // Guardar en memoria
            val listaActual = _actividades.value ?: emptyList()
            val nuevaLista = listaActual + actividad
            _actividades.postValue(nuevaLista)
            // Guardar en CSV
            val csvFile = File(context.filesDir, "actividades.csv")
            try {
                val writer = BufferedWriter(FileWriter(csvFile, true)) // true para modo append
                writer.write("${actividad.nombre},${actividad.fecha},${actividad.hora},${actividad.descripcion}\n")
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun cargarActividadesDesdeCSV(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val csvFile = File(context.filesDir, "actividades.csv")
            val lista = mutableListOf<Actividad>()
            if (csvFile.exists()) {
                try {
                    val reader = csvFile.bufferedReader()
                    val lines = reader.readLines()
                    for (line in lines) {
                        val data = line.split(",")
                        if (data.size >= 4) {
                            lista.add(Actividad(data[0], data[1], data[2], data[3]))
                        }
                    }
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            _actividades.postValue(lista)
        }
    }

    fun eliminarActividad(context: Context, actividad: Actividad) {
        viewModelScope.launch(Dispatchers.IO) {
            // Eliminar de la memoria
            val listaActual = _actividades.value?.toMutableList() ?: mutableListOf()
            if (listaActual.remove(actividad)) {
                _actividades.postValue(listaActual)
                // Reescribir el archivo CSV
                val csvFile = File(context.filesDir, "actividades.csv")
                try {
                    val writer = BufferedWriter(FileWriter(csvFile, false)) // false para sobrescribir
                    listaActual.forEach {
                        writer.write("${it.nombre},${it.fecha},${it.hora},${it.descripcion}\n")
                    }
                    writer.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
