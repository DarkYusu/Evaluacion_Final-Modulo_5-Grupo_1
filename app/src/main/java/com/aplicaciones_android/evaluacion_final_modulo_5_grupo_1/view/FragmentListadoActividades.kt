package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.adapter.ActividadAdapter
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.viewmodel.ActividadViewModel


//region Descripción
/**
 * Fragmento que muestra el listado de actividades.
 * Inicializa un RecyclerView con un Adaptador y observa el ViewModel
 * para actualizar la lista cuando cambien las actividades.
 */
//endregion

//region FragmentListadoActividades
class FragmentListadoActividades : Fragment() {

    //region Propiedades
    // ViewModel compartido con la actividad
    private val actividadViewModel: ActividadViewModel by activityViewModels()
    // RecyclerView y su adaptador
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActividadAdapter
    //endregion

    //region onCreateView - inflar layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_listado_actividades, container, false)
    }
    //endregion

    //region onViewCreated - configurar RecyclerView y observar datos
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a vistas
        recyclerView = view.findViewById(R.id.recyclerActividades)
        adapter = ActividadAdapter(
            onEditClickListener = { actividad ->
                // Abrir el fragmento de edición pasando la actividad
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, EditarActividadFragment.newInstance(actividad))
                    .addToBackStack(null)
                    .commit()

                // Mostrar mensaje de confirmación
                Toast.makeText(requireContext(), "Actividad en edición", Toast.LENGTH_SHORT).show()
            },
            onDateEditListener = { editText ->
                // Configurar acción para ocultar el teclado al confirmar la edición de la fecha
                editText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                        true
                    } else {
                        false
                    }
                }
            },
            onDeleteClickListener = { actividad ->
                actividadViewModel.eliminarActividad(requireContext(), actividad)
            }
        )

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Cargar actividades desde CSV solo si aún no hay actividades cargadas
        if (actividadViewModel.actividades.value.isNullOrEmpty()) {
            actividadViewModel.cargarActividadesDesdeCSV(requireContext())
            // Volcar debug del CSV para inspección (ruta y primeras líneas) en logcat
            actividadViewModel.debugDumpCSV(requireContext())
        }

        // Observar LiveData de actividades para actualizar el adaptador
        actividadViewModel.actividades.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        // Observar mensajes de depuración (ruta y conteo de líneas) y mostrar en UI
        actividadViewModel.debugInfo.observe(viewLifecycleOwner) { info ->
            info?.let {
                Toast.makeText(requireContext(), "Debug CSV: $it", Toast.LENGTH_LONG).show()
            }
        }

        // Observar estado de carga y errores (mostrar mensajes simples)
        actividadViewModel.error.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
            }
        }

        actividadViewModel.isLoading.observe(viewLifecycleOwner) { cargando ->
            // Si se desea, aquí se puede mostrar/ocultar un ProgressBar en el layout
            // Por ahora mostramos un Toast breve cuando empieza la carga (opcional)
            if (cargando) {
                // evitamos spamear con toasts frecuentes; puedes comentar la siguiente línea si molesta
                Toast.makeText(requireContext(), "Cargando actividades...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //endregion

    //region Companion object - fábrica
    companion object {
        // Factory simple para crear una instancia del fragmento
        fun newInstance(): FragmentListadoActividades = FragmentListadoActividades()
    }
    //endregion
}
//endregion
