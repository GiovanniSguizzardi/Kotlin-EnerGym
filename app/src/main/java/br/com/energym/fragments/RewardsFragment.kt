package br.com.energym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.energym.R
import br.com.energym.adapters.RewardAdapter
import br.com.energym.models.Reward

class RewardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rewards, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_rewards)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val rewards = listOf(
            Reward("Voucher 15% Off", 120000, "UltraGaz", "Voucher", true),
            Reward("Ingresso da F1 - Interlagos", 25000000, "FIA", "Ingresso", false),
        )

        recyclerView.adapter = RewardAdapter(rewards)
        return view
    }
}