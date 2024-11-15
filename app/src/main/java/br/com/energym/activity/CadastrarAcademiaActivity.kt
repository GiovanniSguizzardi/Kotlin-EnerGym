package br.com.energym.activity

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.energym.R
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.regex.Pattern

class CadastrarAcademiaActivity : AppCompatActivity() {

    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym/academias.json"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_academia)
        val etNomeAcademia = findViewById<EditText>(R.id.etNomeAcademia)
        val etImagemUrl = findViewById<EditText>(R.id.etImagemUrl)
        val etCep = findViewById<EditText>(R.id.etCep)
        val etEstado = findViewById<EditText>(R.id.etEstado)
        val etCidade = findViewById<EditText>(R.id.etCidade)
        val etRua = findViewById<EditText>(R.id.etRua)
        val etNumero = findViewById<EditText>(R.id.etNumero)
        val btnCadastrarAcademia = findViewById<Button>(R.id.btnCadastrarAcademia)

        btnCadastrarAcademia.setOnClickListener {
            val nome = etNomeAcademia.text.toString().trim()
            val imagemUrl = etImagemUrl.text.toString().trim()
            val cep = etCep.text.toString().trim()
            val estado = etEstado.text.toString().trim()
            val cidade = etCidade.text.toString().trim()
            val rua = etRua.text.toString().trim()
            val numero = etNumero.text.toString().trim()

            if (areFieldsValid(nome, imagemUrl, cep, estado, cidade, rua, numero)) {
                val academia = mapOf(
                    "name" to nome,
                    "imageUrl" to imagemUrl,
                    "cep" to cep,
                    "estado" to estado,
                    "cidade" to cidade,
                    "rua" to rua,
                    "numero" to numero
                )

                val json = Gson().toJson(academia)
                val requestBody = json.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(this@CadastrarAcademiaActivity, "Erro ao cadastrar academia: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(this@CadastrarAcademiaActivity, "Academia cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@CadastrarAcademiaActivity, "Erro ao cadastrar academia", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Verifique os campos e preencha corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areFieldsValid(nome: String, imagemUrl: String, cep: String, estado: String, cidade: String, rua: String, numero: String): Boolean {
        return nome.isNotEmpty() && imagemUrl.isNotEmpty() && isValidCep(cep) &&
                estado.isNotEmpty() && cidade.isNotEmpty() && rua.isNotEmpty() && numero.isNotEmpty()
    }

    private fun isValidCep(cep: String): Boolean {
        val cepPattern = Pattern.compile("^[0-9]{5}-[0-9]{3}$")
        return cepPattern.matcher(cep).matches()
    }
}