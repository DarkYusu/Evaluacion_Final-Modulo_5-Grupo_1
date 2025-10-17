package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel.ActividadViewModel
import java.text.SimpleDateFormat
import java.util.Locale


//region Descripción
/**
 * Fragmento para registrar una actividad.
 * Contiene validaciones básicas de fecha y hora y usa un ViewModel compartido
 * para añadir la actividad al almacenamiento/local.
 */
//endregion

//region Clase FragmentRegistroActividad
class FragmentRegistroActividad : Fragment() {

    //region Propiedades (ViewModel)
    // ViewModel compartido entre fragments de la actividad
    private val actividadViewModel: ActividadViewModel by activityViewModels()
    //endregion

    //region onCreateView - inflar layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_registro_actividad, container, false)
    }
    //endregion

    //region onViewCreated - inicializar vistas y listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencias a las vistas del layout
        val nombre = view.findViewById<EditText>(R.id.etNombre)
        val fecha = view.findViewById<EditText>(R.id.etFecha)
        val hora = view.findViewById<EditText>(R.id.etHora)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        // Listener del botón registrar
        btnRegistrar.setOnClickListener {
            // Extraer y limpiar texto de los campos
            val nombreStr = nombre.text.toString().trim()
            val fechaStr = fecha.text.toString().trim()
            val horaStr = hora.text.toString().trim()
            val descripcionStr = descripcion.text.toString().trim()

            // Expresiones regulares para validar formato de fecha y hora
            val fechaRegex = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
            val horaRegex = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")

            // Formato de fecha estricto (no leniente) para detectar fechas inexistentes
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            // Usar '_' en el catch para indicar que la excepción no se utiliza (evita warning)
            val fechaValida = try { sdf.parse(fechaStr) != null } catch (_: Exception) { false }

            // Validaciones y mensajes al usuario (comentarios en español)
            if (nombreStr.isEmpty() || fechaStr.isEmpty() || horaStr.isEmpty() || descripcionStr.isEmpty()) {
                // Campos obligatorios
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else if (!fechaRegex.matches(fechaStr)) {
                // Formato de fecha incorrecto
                Toast.makeText(requireContext(), "Formato de fecha inválido. Usa dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            } else if (!fechaValida) {
                // Fecha inexistente (por ejemplo 31/02/2023)
                Toast.makeText(requireContext(), "Fecha inexistente. Verifica el día, mes y año.", Toast.LENGTH_SHORT).show()
            } else if (!horaRegex.matches(horaStr)) {
                // Formato de hora incorrecto
                Toast.makeText(requireContext(), "Formato de hora inválido. Usa HH:mm (24h)", Toast.LENGTH_SHORT).show()
            } else {
                // Si todo es válido, se crea la actividad y se guarda mediante el ViewModel
                val actividad = Actividad(nombreStr, fechaStr, horaStr, descripcionStr)
                actividadViewModel.agregarActividad(requireContext(), actividad)

                // Notificar al usuario
                Toast.makeText(requireContext(), "Actividad registrada", Toast.LENGTH_SHORT).show()

                // Limpiar campos
                nombre.text.clear()
                fecha.text.clear()
                hora.text.clear()
                descripcion.text.clear()

                // Cambiar al fragmento de listado/historial de actividades
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                    .commit()
            }
        }
    }
    //endregion
}
//endregion
