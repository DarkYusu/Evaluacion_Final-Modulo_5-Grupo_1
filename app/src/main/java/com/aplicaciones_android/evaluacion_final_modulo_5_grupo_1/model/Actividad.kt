package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model

import java.io.Serializable

data class Actividad(
    val nombre: String,
    val fecha: String,
    val hora: String,
    val descripcion: String
) : Serializable
