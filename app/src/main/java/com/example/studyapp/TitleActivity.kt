package com.example.studyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TitleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        // Firebase Auth 인스턴스 가져오기
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Handler 생성하여 나중에 호출되게 함
        Handler(Looper.getMainLooper()).postDelayed({
            // 로그인 상태에 따라 이동할 액티비티 결정
            val intent = if (user != null) {
                // 로그인된 상태일 경우 MainActivity로 이동
                Intent(this@TitleActivity, MainActivity::class.java)
            } else {
                // 로그인되지 않은 상태일 경우 LoginActivity로 이동
                Intent(this@TitleActivity, LoginActivity::class.java)
            }

            // 액티비티 시작
            startActivity(intent)
            // 액티비티 종료
            finish()
        }, 1000) // 1초 후에 실행되도록 하기
    }
}
