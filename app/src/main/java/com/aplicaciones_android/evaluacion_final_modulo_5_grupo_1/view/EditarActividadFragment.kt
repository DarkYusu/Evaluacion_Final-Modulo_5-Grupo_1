package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel.ActividadViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//region Constantes
private const val ARG_ACTIVIDAD = "actividad"
//endregion

class EditarActividadFragment : Fragment() {

    //region Propiedades
    private val actividadViewModel: ActividadViewModel by activityViewModels()
    private var actividadOriginal: Actividad? = null
    private val calendario = Calendar.getInstance()
    //endregion

    //region Ciclo de vida
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            // Usar la nueva sobrecarga en API 33+ para evitar deprecated, con fallback para versiones anteriores
            actividadOriginal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(ARG_ACTIVIDAD, Actividad::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(ARG_ACTIVIDAD) as? Actividad
            }

            // Inicializar calendario con la fecha/hora de la actividad si existe
            actividadOriginal?.let { act ->
                try {
                    val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = sdfDate.parse(act.fecha)
                    date?.let { calendario.time = it }
                    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val time = sdfTime.parse(act.hora)
                    time?.let {
                        val calTime = Calendar.getInstance()
                        calTime.time = it
                        calendario.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
                        calendario.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
                    }
                } catch (_: Exception) {
                    // ignorar parse errors
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_editar_actividad, container, false)
    }
    //endregion

    //region Interacciones UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val campoTitulo = view.findViewById<EditText>(R.id.textViewTitulo)
        val textoFecha = view.findViewById<TextView>(R.id.textViewFecha)
        val textoHora = view.findViewById<TextView>(R.id.textViewHora)
        val campoDescripcion = view.findViewById<EditText>(R.id.textViewDescripcion)
        val botonConfirmar = view.findViewById<ImageButton>(R.id.botonConfirmar)
        val botonCancelar = view.findViewById<ImageButton>(R.id.botonCancelar)

        // Rellenar con la actividad original
        actividadOriginal?.let { act ->
            campoTitulo.setText(act.nombre)
            textoFecha.text = act.fecha
            textoHora.text = act.hora
            campoDescripcion.setText(act.descripcion)
        }

        // Fecha y hora: abrir pickers (ocultando teclado antes)
        textoFecha.setOnClickListener {
            ocultarTeclado(it)
            mostrarDatePickerDialog(textoFecha)
        }
        textoHora.setOnClickListener {
            ocultarTeclado(it)
            mostrarTimePickerDialog(textoHora)
        }

        botonConfirmar.setOnClickListener {
            val nuevoTitulo = campoTitulo.text.toString().trim()
            val nuevaFecha = textoFecha.text.toString().trim()
            val nuevaHora = textoHora.text.toString().trim()
            val nuevaDescripcion = campoDescripcion.text.toString().trim()

            if (nuevoTitulo.isEmpty() || nuevaFecha.isEmpty() || nuevaHora.isEmpty() || nuevaDescripcion.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val actualizado = Actividad(nuevoTitulo, nuevaFecha, nuevaHora, nuevaDescripcion)
            actividadOriginal?.let { original ->
                actividadViewModel.actualizarActividad(requireContext(), original, actualizado)
                Toast.makeText(requireContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show()
                // Volver al listado
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                    .commit()
            }
        }

        botonCancelar.setOnClickListener {
            // Volver sin cambios
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                .commit()
        }
    }
    //endregion

    //region Pickers
    private fun mostrarDatePickerDialog(targetView: TextView) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val formato = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(formato, Locale.getDefault())
            targetView.text = sdf.format(calendario.time)
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarTimePickerDialog(targetView: TextView) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendario.set(Calendar.MINUTE, minute)
            val formato = "HH:mm"
            val sdf = SimpleDateFormat(formato, Locale.getDefault())
            targetView.text = sdf.format(calendario.time)
        }

        TimePickerDialog(
            requireContext(),
            timeSetListener,
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }
    //endregion

    //region Utilidades
    // Oculta el teclado suave asociado a la ventana del view indicado
    private fun ocultarTeclado(view: View) {
        try {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val token = requireActivity().currentFocus?.windowToken ?: view.windowToken
            imm.hideSoftInputFromWindow(token, 0)
        } catch (_: Exception) {
            // Ignorar si no se puede ocultar
        }
    }
    //endregion

    //region Companion
    companion object {
        @JvmStatic
        fun newInstance(actividad: Actividad) =
            EditarActividadFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ACTIVIDAD, actividad)
                }
            }
    }
    //endregion
}