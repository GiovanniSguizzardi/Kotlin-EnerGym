package br.com.energym.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.energym.R
import br.com.energym.activity.AcademiaDetailsActivity
import br.com.energym.models.Academia
import com.bumptech.glide.Glide

class AcademiaAdapter(
    private val context: Context,
    private val academias: Map<String, Academia> // Alterado para Map para incluir o ID
) : RecyclerView.Adapter<AcademiaAdapter.AcademiaViewHolder>() {

    class AcademiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val academiaImage: ImageView = itemView.findViewById(R.id.academia_image)
        val academiaName: TextView = itemView.findViewById(R.id.academia_name)
        val viewButton: Button = itemView.findViewById(R.id.view_button)
    }

    private val keys = academias.keys.toList() // Lista dos IDs gerados pelo Firebase
    private val values = academias.values.toList() // Lista das academias

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcademiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_academia, parent, false)
        return AcademiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcademiaViewHolder, position: Int) {
        val academia = values[position]
        val academiaId = keys[position]

        holder.academiaName.text = academia.name
        Glide.with(context).load(academia.imageUrl).into(holder.academiaImage)

        holder.viewButton.setOnClickListener {
            val intent = Intent(context, AcademiaDetailsActivity::class.java).apply {
                putExtra("ID", academiaId) // Passa o ID gerado pelo Firebase
                putExtra("NOME", academia.name)
                putExtra("IMAGEM_URL", academia.imageUrl)
                putExtra("CEP", academia.cep)
                putExtra("ESTADO", academia.estado)
                putExtra("CIDADE", academia.cidade)
                putExtra("RUA", academia.rua)
                putExtra("NUMERO", academia.numero)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return academias.size
    }
}