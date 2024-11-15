package br.com.energym.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.energym.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {

    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/"
    private val client = OkHttpClient()
    private lateinit var tvUsername: TextView
    private lateinit var tvJoinDate: TextView
    private lateinit var tvRegistries: TextView
    private lateinit var tvTotalPoints: TextView
    private var pontosAcumulados = 0
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvUsername = view.findViewById(R.id.tvUsername)
        tvJoinDate = view.findViewById(R.id.tvJoinDate)
        tvRegistries = view.findViewById(R.id.tvRegistries)
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints)
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            loadUserData()
            loadAcademyRegistries()
            tvTotalPoints.text = "0"
        } else {
            Toast.makeText(requireContext(), "Erro ao obter ID do usuário", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadUserData() {
        if (userId == null) return
        val request = Request.Builder()
            .url("${BASE_URL}energym/registro/$userId.json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProfileFragment", "Erro ao carregar dados do usuário", e)
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(responseBody)
                        val name = json.optString("name", "Usuário")
                        pontosAcumulados = json.optInt("pontos", 0)

                        activity?.runOnUiThread {
                            tvUsername.text = "Olá, $name"
                            tvJoinDate.text = "Aqui estão suas informações"
                            tvTotalPoints.text = "$pontosAcumulados"
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileFragment", "Erro ao processar dados do usuário", e)
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Erro ao processar dados do usuário",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        tvUsername.text = "Olá, Usuário"
                        tvJoinDate.text = "Data de entrada não disponível"
                        Toast.makeText(requireContext(), "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun loadAcademyRegistries() {
        val request = Request.Builder()
            .url("${BASE_URL}energym/academias.json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProfileFragment", "Erro ao carregar registros de academias", e)
                activity?.runOnUiThread {
                    tvRegistries.text = "Erro"
                    Toast.makeText(requireContext(), "Erro ao carregar registros de academias", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(responseBody)
                        val count = json.length()

                        activity?.runOnUiThread {
                            tvRegistries.text = "$count"
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileFragment", "Erro ao processar registros de academias", e)
                        activity?.runOnUiThread {
                            tvRegistries.text = "Erro"
                            Toast.makeText(
                                requireContext(),
                                "Erro ao processar registros de academias",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        tvRegistries.text = "Nenhum registro"
                        Toast.makeText(
                            requireContext(),
                            "Nenhum registro encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    fun atualizarPontosUsuario(novosPontos: Int) {
        pontosAcumulados += novosPontos
        tvTotalPoints.text = "$pontosAcumulados"

        if (userId == null) return

        val pontosData = mapOf("pontos" to pontosAcumulados)
        val json = JSONObject(pontosData).toString()
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("${BASE_URL}energym/registro/$userId.json")
            .patch(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProfileFragment", "Erro ao atualizar pontos do usuário", e)
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Erro ao atualizar pontos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Pontos atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Falha ao atualizar pontos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}