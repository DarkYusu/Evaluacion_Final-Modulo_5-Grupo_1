package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.databinding.FragmentNuevaActividadBinding
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel.ActividadViewModel
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view.FragmentListadoActividades
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Renombrar los argumentos para usar nombres más descriptivos
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val calendario = Calendar.getInstance()
class NuevaActividadFragment : Fragment() {

    private var _binding: FragmentNuevaActividadBinding? = null
    private val binding get() = _binding!!

    // Parámetros opcionales del fragment
    private var param1: String? = null
    private var param2: String? = null

    private val actividadViewModel: ActividadViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNuevaActividadBinding.inflate(inflater, container, false)
        // Devolver la raíz del binding para usar view binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val titulo = view.findViewById<EditText>(R.id.editTextTitulo)
        val descripcion = view.findViewById<EditText>(R.id.editTextDescripcion)
        val textFecha = view.findViewById<TextView>(R.id.textViewFecha)
        val textHora = view.findViewById<TextView>(R.id.textViewHora)
        val botonAgregar = view.findViewById<Button>(R.id.botonAgregarActividad)
        val botonActividades = view.findViewById<Button>(R.id.botonActividades)

        botonAgregar.setOnClickListener {
            val nombreStr = titulo.text.toString().trim()
            val fechaStr = textFecha.text.toString().trim()
            val horaStr = textHora.text.toString().trim()
            val descripcionStr = descripcion.text.toString().trim()

            val fechaRegex = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
            val horaRegex = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val fechaValida = try { sdf.parse(fechaStr) != null } catch (_: Exception) { false }

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
                titulo.text.clear()
                descripcion.text.clear()
                textFecha.text = ""
                textHora.text = ""

                // Ir al fragmento que muestra el listado de actividades
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                    .commit()
            }
        }

        botonActividades.setOnClickListener {
            // Mostrar la lista de actividades
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                .commit()
        }

        // Abrir selector de fecha al tocar el TextView
        binding.textViewFecha.setOnClickListener {
            mostrarDatePickerDialog()
        }

        // Abrir selector de hora al tocar el TextView
        binding.textViewHora.setOnClickListener {
            mostrarTimePickerDialog()
        }

    }

    companion object {
        /**
         * Método de fábrica para crear una instancia del fragment con parámetros.
         * @param param1 Parámetro 1 (opcional)
         * @param param2 Parámetro 2 (opcional)
         * @return Nueva instancia de NuevaActividadFragment
         */
        @Suppress("unused")
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NuevaActividadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        // Constructor de conveniencia sin parámetros
        fun newInstance(): NuevaActividadFragment = NuevaActividadFragment()
    }

    private fun mostrarDatePickerDialog() {
        // Listener que recibe la fecha seleccionada
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Actualizar el calendario con la fecha elegida
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Mostrar la fecha en el TextView
            actualizarFechaEnTextView()
        }

        // Mostrar el diálogo de selección de fecha
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun actualizarFechaEnTextView() {
        // Formatear la fecha como dd/MM/yyyy y mostrarla
        val formato = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(formato, Locale.getDefault())
        binding.textViewFecha.text = sdf.format(calendario.time)
    }

    private fun mostrarTimePickerDialog() {
        // Listener que recibe la hora seleccionada
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // Actualizar el calendario con la hora elegida
            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendario.set(Calendar.MINUTE, minute)

            // Mostrar la hora en el TextView
            actualizarHoraEnTextView()
        }

        // Mostrar el diálogo de selección de hora (formato 24h)
        TimePickerDialog(
            requireContext(),
            timeSetListener,
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun actualizarHoraEnTextView() {
        // Formatear la hora como HH:mm y mostrarla
        val formato = "HH:mm"
        val sdf = SimpleDateFormat(formato, Locale.getDefault())
        binding.textViewHora.text = sdf.format(calendario.time)
    }

}