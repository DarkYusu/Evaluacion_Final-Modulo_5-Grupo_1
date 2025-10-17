package com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.R
import com.aplicaciones_android.evaluacion_final_modulo_5_grupo_1.model.Actividad

class ActividadAdapter(
    private val onEditClickListener: (Actividad) -> Unit,
    private val onDeleteClickListener: (Actividad) -> Unit,
    private val onDateEditListener: (TextView) -> Unit // Nuevo parámetro para manejar la edición de la fecha
) : ListAdapter<Actividad, ActividadAdapter.ActividadViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = getItem(position)
        holder.bind(actividad)

        // Botón editar: invoca el callback de edición directamente
        holder.itemView.findViewById<ImageButton>(R.id.botonEditar).setOnClickListener {
            onEditClickListener(actividad)
        }

        // Configurar el listener para la edición de la fecha
        val fechaTextView = holder.itemView.findViewById<TextView>(R.id.textViewFecha)
        onDateEditListener(fechaTextView)

        // Botón eliminar: mostrar diálogo de confirmación antes de eliminar
        holder.itemView.findViewById<ImageButton>(R.id.botonEliminar).setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Eliminar actividad")
                .setMessage("¿Deseas eliminar esta actividad?")
                .setPositiveButton("Sí") { _, _ ->
                    onDeleteClickListener(actividad)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(actividad: Actividad) {
            itemView.findViewById<TextView>(R.id.textViewTitulo).text = actividad.nombre
            itemView.findViewById<TextView>(R.id.textViewFecha).text = actividad.fecha
            itemView.findViewById<TextView>(R.id.textViewHora).text = actividad.hora
            itemView.findViewById<TextView>(R.id.textViewDescripcion).text = actividad.descripcion
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Actividad>() {
        override fun areItemsTheSame(oldItem: Actividad, newItem: Actividad): Boolean =
            oldItem === newItem
        override fun areContentsTheSame(oldItem: Actividad, newItem: Actividad): Boolean =
            oldItem == newItem
    }
}
