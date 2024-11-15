package br.com.energym.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.energym.R
import br.com.energym.models.Academia
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AcademiaDetailsActivity : AppCompatActivity() {

    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym"
    private val client = OkHttpClient()
    private var academiaId: String? = null
    private var userId: String? = null
    private lateinit var tvAcademiaNome: TextView
    private lateinit var academiaImage: ImageView
    private lateinit var tvCep: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvCidade: TextView
    private lateinit var tvRua: TextView
    private lateinit var tvNumero: TextView
    private lateinit var tvPontosAcumulados: TextView
    private lateinit var btnIniciarQRCode: Button
    private lateinit var btnEncerrarTimer: Button
    private var pontosAcumulados = 0
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_academia_details)

        tvAcademiaNome = findViewById(R.id.tvAcademiaNome)
        academiaImage = findViewById(R.id.academia_image)
        tvCep = findViewById(R.id.tvCep)
        tvEstado = findViewById(R.id.tvEstado)
        tvCidade = findViewById(R.id.tvCidade)
        tvRua = findViewById(R.id.tvRua)
        tvNumero = findViewById(R.id.tvNumero)
        tvPontosAcumulados = findViewById(R.id.tvPontosAcumulados)
        btnIniciarQRCode = findViewById(R.id.btnIniciarQRCode)
        btnEncerrarTimer = findViewById(R.id.btnEncerrarTimer)
        btnEncerrarTimer.isEnabled = false

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null)

        academiaId = intent.getStringExtra("ID")
        if (academiaId == null || userId == null) {
            Toast.makeText(this, "Erro ao carregar dados do usuário ou academia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDetalhesDaAcademia()
        carregarPontosAtuais()

        btnIniciarQRCode.setOnClickListener {
            iniciarLeituraQRCode()
        }

        btnEncerrarTimer.setOnClickListener {
            encerrarTimer()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDetalhesDaAcademia()
    }

    private fun iniciarLeituraQRCode() {
        Toast.makeText(this, "QR-Code lido! Temporizador iniciado.", Toast.LENGTH_SHORT).show()
        iniciarTimer()
    }

    private fun iniciarTimer() {
        btnIniciarQRCode.isEnabled = false
        btnEncerrarTimer.isEnabled = true
        timerHandler = Handler(Looper.getMainLooper())
        timerRunnable = object : Runnable {
            override fun run() {
                pontosAcumulados += 100
                tvPontosAcumulados.text = "Pontos acumulados: $pontosAcumulados"
                atualizarPontosFirebase()
                timerHandler?.postDelayed(this, 60000)
            }
        }
        timerHandler?.postDelayed(timerRunnable!!, 60000)
    }

    private fun encerrarTimer() {
        timerHandler?.removeCallbacks(timerRunnable!!)
        atualizarPontosFirebase()
        Toast.makeText(this, "Temporizador encerrado.", Toast.LENGTH_SHORT).show()
        btnIniciarQRCode.isEnabled = true
        btnEncerrarTimer.isEnabled = false
    }

    private fun carregarPontosAtuais() {
        val request = Request.Builder()
            .url("$BASE_URL/registro/$userId.json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AcademiaDetailsActivity", "Erro ao carregar pontos atuais", e)
                runOnUiThread {
                    Toast.makeText(this@AcademiaDetailsActivity, "Erro ao carregar pontos atuais", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(responseBody)
                        pontosAcumulados = json.optInt("pontos", 0)
                        runOnUiThread {
                            tvPontosAcumulados.text = "Pontos acumulados: $pontosAcumulados"
                        }
                    } catch (e: Exception) {
                        Log.e("AcademiaDetailsActivity", "Erro ao processar pontos atuais", e)
                    }
                }
            }
        })
    }

    private fun atualizarPontosFirebase() {
        if (userId == null) return

        val pontosData = mapOf("pontos" to pontosAcumulados)
        val json = JSONObject(pontosData).toString()
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$BASE_URL/registro/$userId.json")
            .patch(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AcademiaDetailsActivity, "Erro ao atualizar pontos", Toast.LENGTH_SHORT).show()
                }
                Log.e("AcademiaDetailsActivity", "Erro ao atualizar pontos", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@AcademiaDetailsActivity, "Pontos atualizados no Firebase", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AcademiaDetailsActivity, "Erro ao atualizar pontos no Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun carregarDetalhesDaAcademia() {
        val request = Request.Builder()
            .url("$BASE_URL/academias/$academiaId.json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@AcademiaDetailsActivity,
                        "Erro ao carregar dados: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("AcademiaDetailsActivity", "Erro ao carregar dados", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val academia = Gson().fromJson(responseBody, Academia::class.java)

                    runOnUiThread {
                        tvAcademiaNome.text = academia.name
                        tvCep.text = academia.cep
                        tvEstado.text = academia.estado
                        tvCidade.text = academia.cidade
                        tvRua.text = academia.rua
                        tvNumero.text = academia.numero
                        Glide.with(this@AcademiaDetailsActivity)
                            .load(academia.imageUrl)
                            .placeholder(R.drawable.academia_image_placeholder)
                            .into(academiaImage)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AcademiaDetailsActivity,
                            "Erro ao carregar dados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d("AcademiaDetailsActivity", "Erro na resposta ou dados vazios")
                }
            }
        })
    }
}