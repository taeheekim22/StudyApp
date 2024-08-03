package com.example.studyapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var spinnerDepartment: Spinner
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 스피너 참조
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        // 스피너에 표시할 항목 배열 정의
        val departments = resources.getStringArray(R.array.departments_array)
        // 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        // 드롭다운 뷰 리소스 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 어댑터를 스피너에 적용
        spinnerDepartment.adapter = adapter

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etName = findViewById(R.id.etName)
        btnRegister = findViewById(R.id.btnRegister)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val name = etName.text.toString().trim()
        val department = spinnerDepartment.selectedItem.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userInfo = hashMapOf(
                            "name" to name,
                            "department" to department
                        )
                        user?.let {
                            db.collection("users").document(it.uid)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    showToast("회원가입 성공")
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish() // 현재 액티비티 종료
                                }
                                .addOnFailureListener {
                                    showToast("회원정보 저장 실패: ${it.message}")
                                }
                        }
                    } else {
                        showToast("회원가입 실패: ${task.exception?.message}")
                    }
                }
        } else {
            showToast("모든 필드를 입력하세요")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
