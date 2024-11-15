package br.com.energym.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.energym.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym/registro.json"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val streetEditText = findViewById<EditText>(R.id.streetEditText)
        val numberEditText = findViewById<EditText>(R.id.numberEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginHereButton = findViewById<Button>(R.id.loginHereButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val street = streetEditText.text.toString()
            val number = numberEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (isValidName(name) && isValidEmail(email) && isValidStreet(street) && isValidPassword(password) && number.isNotEmpty()) {
                val userJson = """
                    {
                        "name": "$name",
                        "email": "$email",
                        "address": {
                            "street": "$street",
                            "number": "$number"
                        },
                        "password": "$password",
                        "pontos": 0
                    }
                """.trimIndent()

                enviarDadosFirebase(userJson)
            } else {
                Toast.makeText(this, "Verifique os campos e preencha corretamente", Toast.LENGTH_SHORT).show()
            }
        }

        loginHereButton.setOnClickListener {
            // Navegar para a tela de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidName(name: String): Boolean {
        return name.length >= 2
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidStreet(street: String): Boolean {
        return street.isNotEmpty()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[@#\$%^&+=!]).{6,}$")
        return passwordPattern.matcher(password).matches()
    }

    private fun enviarDadosFirebase(userJson: String) {
        val request = Request.Builder()
            .url(BASE_URL)
            .post(userJson.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Erro ao registrar usuário: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registro realizado com sucesso!",
                            Toast.LENGTH_LONG
                        ).show()
                        limparDadosLocais()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Falha no registro",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun limparDadosLocais() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        Toast.makeText(this, "Dados locais limpos!", Toast.LENGTH_SHORT).show()
    }
}