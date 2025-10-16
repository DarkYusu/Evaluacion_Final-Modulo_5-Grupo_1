package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.NuevaActividadFragment
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view.FragmentListadoActividades
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view.AboutActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, NuevaActividadFragment.newInstance())
                .commit()
        }
        solicitarPermisoAlmacenamiento()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_registro -> {
                    mostrarRegistro()
                    true
                }
                R.id.nav_listado -> {
                    mostrarListado()
                    true
                }
                R.id.nav_about -> {
                    mostrarAbout()
                    true
                }
                else -> false
            }
        }
    }

    private fun solicitarPermisoAlmacenamiento() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permiso), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de almacenamiento concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarFragmento(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun mostrarListado() {
        mostrarFragmento(FragmentListadoActividades.newInstance())
    }

    fun mostrarRegistro() {
        mostrarFragmento(NuevaActividadFragment.newInstance())
    }

    fun mostrarAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        intent.putExtra("info", "App de registro de actividades. Grupo 1. 2025")
        startActivity(intent)
    }
}