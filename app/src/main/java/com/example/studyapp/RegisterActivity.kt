package com.example.studyapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etName: EditText
    lateinit var spinnerDepartment: Spinner
    lateinit var btnRegister: Button
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

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


        etEmail=findViewById(R.id.etEmail)
        etPassword=findViewById(R.id.etPassword)
        etName=findViewById(R.id.etName)
        spinnerDepartment=findViewById(R.id.spinnerDepartment)
        btnRegister=findViewById(R.id.btnRegister)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        btnRegister.setOnClickListener{
            register()
        }
    }

    private fun register() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val name = etName.text.toString()
        val department = spinnerDepartment.selectedItem.toString()

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
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                showToast("회원정보 저장 실패")
                            }
                    }
                } else {
                    showToast("회원가입 실패: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}