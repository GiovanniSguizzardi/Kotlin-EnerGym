package br.com.energym.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.energym.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ForgotPasswordActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym/registro.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (isValidEmail(email)) {
                enviarSolicitacaoRecuperacao(email)
            } else {
                Toast.makeText(this, "Por favor, insira um email válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun enviarSolicitacaoRecuperacao(email: String) {
        val recoveryJson = """
            {
                "email": "$email",
                "message": "Solicitação de recuperação de senha"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(BASE_URL)
            .post(recoveryJson.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Erro ao enviar solicitação: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Solicitação enviada com sucesso! Verifique seu email.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Erro ao processar solicitação.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}