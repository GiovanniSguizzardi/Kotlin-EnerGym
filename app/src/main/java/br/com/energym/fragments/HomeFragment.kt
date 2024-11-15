package br.com.energym.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.energym.R
import br.com.energym.activity.CadastrarAcademiaActivity
import br.com.energym.adapters.AcademiaAdapter
import br.com.energym.models.Academia
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class HomeFragment : Fragment() {

    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym/academias.json"
    private val client = OkHttpClient()
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AcademiaAdapter(requireContext(), emptyMap())
        setHasOptionsMenu(true)
        carregarAcademiasDoFirebase()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_academia -> {
                val intent = Intent(context, CadastrarAcademiaActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun carregarAcademiasDoFirebase() {
        Log.d("HomeFragment", "Iniciando a requisição ao Firebase")
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Erro ao buscar dados: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("HomeFragment", "Resposta recebida: $responseBody")

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    try {
                        val tipoAcademiaMapa = object : TypeToken<Map<String, Academia>>() {}.type
                        val academiaMap: Map<String, Academia> = Gson().fromJson(responseBody, tipoAcademiaMapa)
                        activity?.runOnUiThread {
                            recyclerView.adapter = AcademiaAdapter(requireContext(), academiaMap)
                            Log.d("HomeFragment", "Adapter atualizado com os dados das academias")
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Erro ao processar dados recebidos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e("HomeFragment", "Erro ao processar dados recebidos", e)
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao buscar dados ou dados vazios",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d("HomeFragment", "Resposta vazia ou erro na resposta")
                }
            }
        })
    }
}