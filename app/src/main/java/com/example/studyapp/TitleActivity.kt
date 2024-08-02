package com.example.studyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class TitleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            // 앱의 main activity로 넘어가기
            val i = Intent(this@TitleActivity, LoginActivity::class.java)
            startActivity(i)
            // 현재 액티비티 닫기
            finish()
        }, 1000)
    }
}