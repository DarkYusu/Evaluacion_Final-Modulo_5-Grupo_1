package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        adapter = ActividadAdapter { actividad ->
            actividadViewModel.eliminarActividad(requireContext(), actividad)
        }

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Cargar actividades desde CSV (si aplica) y observar el LiveData
        actividadViewModel.cargarActividadesDesdeCSV(requireContext())
        actividadViewModel.actividades.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
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
