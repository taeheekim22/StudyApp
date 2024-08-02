package com.example.studyapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername=findViewById(R.id.etUsername)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        btnRegister=findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener{
            login()
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()

        val savedPassword = sharedPreferences.getString(username, null)

        if (savedPassword != null && savedPassword == password) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", username)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "아이디 또는 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show()
        }
    }

}
