package br.com.energym.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.energym.R
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.util.regex.Pattern

class EditarAcademiaActivity : AppCompatActivity() {

    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym/academias"
    private var academiaId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_academia)

        val nomeEditText = findViewById<EditText>(R.id.etNomeAcademia)
        val cepEditText = findViewById<EditText>(R.id.etCep)
        val estadoEditText = findViewById<EditText>(R.id.etEstado)
        val cidadeEditText = findViewById<EditText>(R.id.etCidade)
        val ruaEditText = findViewById<EditText>(R.id.etRua)
        val numeroEditText = findViewById<EditText>(R.id.etNumero)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        academiaId = intent.getStringExtra("ID")
        val nome = intent.getStringExtra("NOME")
        val imagemUrl = intent.getStringExtra("IMAGEM_URL")
        val cep = intent.getStringExtra("CEP")
        val estado = intent.getStringExtra("ESTADO")
        val cidade = intent.getStringExtra("CIDADE")
        val rua = intent.getStringExtra("RUA")
        val numero = intent.getStringExtra("NUMERO")

        nomeEditText.setText(nome)
        cepEditText.setText(cep)
        estadoEditText.setText(estado)
        cidadeEditText.setText(cidade)
        ruaEditText.setText(rua)
        numeroEditText.setText(numero)

        btnSalvar.setOnClickListener {
            if (areFieldsValid(
                    nomeEditText.text.toString(),
                    cepEditText.text.toString(),
                    estadoEditText.text.toString(),
                    cidadeEditText.text.toString(),
                    ruaEditText.text.toString(),
                    numeroEditText.text.toString()
                )
            ) {
                val updatedAcademia = mapOf(
                    "name" to nomeEditText.text.toString(),
                    "imageUrl" to imagemUrl,
                    "cep" to cepEditText.text.toString(),
                    "estado" to estadoEditText.text.toString(),
                    "cidade" to cidadeEditText.text.toString(),
                    "rua" to ruaEditText.text.toString(),
                    "numero" to numeroEditText.text.toString()
                )
                salvarEdicaoAcademia(updatedAcademia)
            } else {
                Toast.makeText(this, "Verifique os campos e preencha corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areFieldsValid(nome: String, cep: String, estado: String, cidade: String, rua: String, numero: String): Boolean {
        return nome.isNotEmpty() && cep.isEmpty() &&
                estado.isNotEmpty() && cidade.isNotEmpty() &&
                rua.isNotEmpty() && numero.isNotEmpty()
    }

    private fun salvarEdicaoAcademia(updatedAcademia: Map<String, String?>) {
        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            Gson().toJson(updatedAcademia)
        )
        val request = Request.Builder()
            .url("$BASE_URL/$academiaId.json")
            .put(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@EditarAcademiaActivity, "Erro ao salvar academia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarAcademiaActivity, "Academia atualizada com sucesso", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditarAcademiaActivity, "Erro ao salvar academia", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}