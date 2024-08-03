package com.example.studyapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail=findViewById(R.id.etEmail)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        btnRegister=findViewById(R.id.btnRegister)
        auth= FirebaseAuth.getInstance()

        btnLogin.setOnClickListener{
            login()
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login() {
        val username = etEmail.text.toString()
        val password = etPassword.text.toString()

        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this) {
                task -> if(task.isSuccessful) {
            val user = auth.currentUser
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", user?.email) }
            startActivity(intent)
        } else{
            Toast.makeText(this, "이메일 또는 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show()}
        }
    }
}



