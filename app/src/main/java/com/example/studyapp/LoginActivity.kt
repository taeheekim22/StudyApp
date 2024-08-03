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
    lateinit var etEmail: EditText //이메일 입력
    lateinit var etPassword: EditText //비밀번호 입력
    lateinit var btnLogin: Button //로그인 버튼
    lateinit var btnRegister: Button //회원가입 버튼
    lateinit var auth: FirebaseAuth //파이어베이스 인증

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //변수 연결
        etEmail=findViewById(R.id.etEmail)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        btnRegister=findViewById(R.id.btnRegister)
        auth= FirebaseAuth.getInstance()

        //로그인 버튼 clicklistner
        btnLogin.setOnClickListener{
            login()
        }

        //회원가입 버튼 clicklistner
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    //로그인 함수
    private fun login() {
        //입력된 메일, 비밀번호 가져오기
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            //파이어베이스 이용하여 이메일, 비밀번호로 로그인
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //로그인 성공할 때
                        val user = auth.currentUser
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("username", user?.email)
                        }
                        startActivity(intent)
                    } else {
                        showToast("이메일 또는 비밀번호를 다시 확인하세요!: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener {
                    showToast("로그인 중 오류가 발생했습니다!: ${it.message}")
                }
        } else {
            //로그인 실패할 때
            showToast("이메일과 비밀번호를 입력하세요!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



