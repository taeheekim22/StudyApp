package com.example.studyapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

class RegisterActivity : AppCompatActivity() {
    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var etName: EditText
    lateinit var spinnerDepartment: Spinner
    lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //스피너 참조
        val spinner: Spinner =findViewById(R.id.spinnerDepartment)
        //스피너에 표시할 항목 배열 정의
        val departments=resources.getStringArray(R.array.departments_array)
        //어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        // 드롭다운 뷰 리소스 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 어댑터를 스피너에 적용
        spinner.adapter = adapter


        etUsername=findViewById(R.id.etUsername)
        etPassword=findViewById(R.id.etPassword)
        etName=findViewById(R.id.etName)
        spinnerDepartment=findViewById(R.id.spinnerDepartment)
        btnRegister=findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener{
            register()
        }
    }

    private fun register() {
        val username=etUsername.text.toString()
        val password = etPassword.text.toString()
        val name = etName.text.toString()
        val department = spinnerDepartment.selectedItem.toString()

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(username, password)
        editor.putString("${username}_name", name)
        editor.putString("${username}_department", department)
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}