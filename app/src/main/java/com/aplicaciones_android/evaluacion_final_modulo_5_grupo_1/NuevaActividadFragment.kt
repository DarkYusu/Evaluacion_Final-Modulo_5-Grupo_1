package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1

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
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel.ActividadViewModel
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view.FragmentListadoActividades
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NuevaActividadFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nueva_actividad, container, false)
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
                titulo.text.clear()
                descripcion.text.clear()
                textFecha.text = ""
                textHora.text = ""

                // Navegar al listado
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                    .commit()
            }
        }

        botonActividades.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentListadoActividades.newInstance())
                .commit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NuevaActividadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NuevaActividadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        // Added: convenience no-arg constructor to match previous usage pattern
        fun newInstance(): NuevaActividadFragment = NuevaActividadFragment()
    }
}