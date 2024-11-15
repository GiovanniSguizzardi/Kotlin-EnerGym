package br.com.energym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.energym.R
import br.com.energym.models.Reward

class RewardAdapter(private val rewards: List<Reward>) :
    RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val points: TextView = itemView.findViewById(R.id.tvPoints)
        val company: TextView = itemView.findViewById(R.id.tvCompany)
        val type: TextView = itemView.findViewById(R.id.tvType)
        val availability: TextView = itemView.findViewById(R.id.tvAvailability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.description.text = "Descrição: ${reward.description}"
        holder.points.text = "Pontos: ${reward.points}"
        holder.company.text = "Empresa: ${reward.company}"
        holder.type.text = "Tipo: ${reward.type}"
        holder.availability.text = if (reward.isAvailable) "Disponível" else "Indisponível"
        holder.availability.setBackgroundResource(
            if (reward.isAvailable) R.drawable.bg_available else R.drawable.bg_unavailable
        )
    }

    override fun getItemCount(): Int = rewards.size
}