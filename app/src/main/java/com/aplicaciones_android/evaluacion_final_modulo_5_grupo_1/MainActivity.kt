package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R
import com.google.android.material.bottomnavigation.BottomNavigationView

//region Constantes y configuración
private const val REQUEST_WRITE_STORAGE = 100
//endregion

class MainActivity : AppCompatActivity() {

    //region Ciclo de vida
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mostrar el fragmento de registro por defecto la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, NuevaActividadFragment.newInstance())
                .commit()
        }

        // Solicitar permiso de almacenamiento si corresponde
        solicitarPermisoAlmacenamiento()

        // Configurar navegación inferior
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
    //endregion

    //region Permisos
    /**
     * Solicita permiso de escritura en almacenamiento externo si no está concedido.
     */
    private fun solicitarPermisoAlmacenamiento() {
        val permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
        // Si no está concedido, solicitarlo al usuario
        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permiso), REQUEST_WRITE_STORAGE)
        }
    }

    /**
     * Maneja la respuesta del usuario a la petición de permisos.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de almacenamiento concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //endregion

    //region Navegación y helpers
    /**
     * Reemplaza el fragmento actual por el indicado y limpia la pila.
     */
    private fun mostrarFragmento(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    /**
     * Mostrar fragmento con la lista de actividades.
     */
    fun mostrarListado() {
        mostrarFragmento(FragmentListadoActividades.newInstance())
    }

    /**
     * Mostrar fragmento de registro de nueva actividad.
     */
    fun mostrarRegistro() {
        mostrarFragmento(NuevaActividadFragment.newInstance())
    }

    /**
     * Abrir la pantalla "About" con información de la app.
     */
    fun mostrarAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        intent.putExtra("info", "App de registro de actividades. Grupo 1. 2025")
        startActivity(intent)
    }
    //endregion

}