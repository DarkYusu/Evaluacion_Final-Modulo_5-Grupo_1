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

class FragmentRegistroActividad : Fragment() {
    private val actividadViewModel: ActividadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro_actividad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nombre = view.findViewById<EditText>(R.id.etNombre)
        val fecha = view.findViewById<EditText>(R.id.etFecha)
        val hora = view.findViewById<EditText>(R.id.etHora)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombreStr = nombre.text.toString().trim()
            val fechaStr = fecha.text.toString().trim()
            val horaStr = hora.text.toString().trim()
            val descripcionStr = descripcion.text.toString().trim()
            val fechaRegex = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
            val horaRegex = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val fechaValida = try { sdf.parse(fechaStr) != null } catch (e: Exception) { false }
            if (nombreStr.isEmpty() || fechaStr.isEmpty() || horaStr.isEmpty() || descripcionStr.isEmpty()) {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else if (!fechaRegex.matches(fechaStr)) {
                Toast.makeText(requireContext(), "Formato de fecha inválido. Usa dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            } else if (!fechaValida) {
                Toast.makeText(requireContext(), "Fecha inexistente. Verifica el día, mes y año.", Toast.LENGTH_SHORT).show()
            } else if (!horaRegex.matches(horaStr)) {
                Toast.makeText(requireContext(), "Formato de hora inválido. Usa HH:mm (24h)", Toast.LENGTH_SHORT).show()
            } else {
                val actividad = Actividad(nombreStr, fechaStr, horaStr, descripcionStr)
                actividadViewModel.agregarActividad(requireContext(), actividad)
                Toast.makeText(requireContext(), "Actividad registrada", Toast.LENGTH_SHORT).show()
                nombre.text.clear()
                fecha.text.clear()
                hora.text.clear()
                descripcion.text.clear()
                // Cambiar al fragmento de historial
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                    .commit()
            }
        }
    }

    companion object {
        fun newInstance(): FragmentRegistroActividad = FragmentRegistroActividad()
    }
}
