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

class FragmentListadoActividades : Fragment() {
    private val actividadViewModel: ActividadViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActividadAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listado_actividades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerActividades)
        adapter = ActividadAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        actividadViewModel.cargarActividadesDesdeCSV(requireContext())
        actividadViewModel.actividades.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }
    }

    companion object {
        fun newInstance(): FragmentListadoActividades = FragmentListadoActividades()
    }
}
