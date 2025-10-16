package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val info = intent.getStringExtra("info")
        findViewById<TextView>(R.id.tvAbout).text = info ?: "Información de la app"
    }
}

