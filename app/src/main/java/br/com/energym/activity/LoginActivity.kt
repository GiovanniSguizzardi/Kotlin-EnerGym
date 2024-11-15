package br.com.energym.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.energym.MainActivity
import br.com.energym.R
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val BASE_URL = "https://tdsr-d6c6c-default-rtdb.firebaseio.com/energym/registro.json"
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                realizarLogin(email, password)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun realizarLogin(email: String, password: String) {
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro ao conectar ao servidor: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val userMapType = object : TypeToken<Map<String, User>>() {}.type
                    val users = gson.fromJson<Map<String, User>>(responseBody, userMapType)
                    val userEntry = users.entries.find { it.value.email == email && it.value.password == password }

                    runOnUiThread {
                        if (userEntry != null) {
                            val userId = userEntry.key
                            val user = userEntry.value
                            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("userId", userId)
                            editor.apply()
                            Toast.makeText(this@LoginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Falha ao buscar registros", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    data class User(
        val name: String = "",
        val email: String = "",
        val address: Address = Address(),
        val password: String = ""
    )

    data class Address(
        val street: String = "",
        val number: String = ""
    )
}