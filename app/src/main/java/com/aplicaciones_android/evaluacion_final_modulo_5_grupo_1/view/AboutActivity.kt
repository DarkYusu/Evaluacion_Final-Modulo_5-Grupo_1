package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val info = intent.getStringExtra("info")
        // Mensaje por defecto del grupo si no se pasa info
        findViewById<TextView>(R.id.tvAbout).text = info ?: "Aplicación desarrollada por el Grupo 1"

        // Botón para volver (cerrar la Activity)
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }
}
