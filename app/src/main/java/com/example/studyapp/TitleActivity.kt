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
        //Handler 생성하여 나중에 호출되게 함
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //Intent 생성하여 LoginActivity로 이동
            val i = Intent(this@TitleActivity, LoginActivity::class.java)
            //액티비티 시작
            startActivity(i)
            //액티비티 종료
            finish()
            //1초 후에 실행되도록 하기
        }, 1000)
    }
}