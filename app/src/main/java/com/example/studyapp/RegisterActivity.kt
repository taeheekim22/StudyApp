package com.example.studyapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText //이메일 입력
    private lateinit var etPassword: EditText //비밀번호 입력
    private lateinit var etName: EditText //이름 입력
    private lateinit var spinnerDepartment: Spinner //학과 선택
    private lateinit var btnRegister: Button //회원가입 버튼
    private lateinit var auth: FirebaseAuth //파이어베이스 인증
    private lateinit var db: FirebaseFirestore //파이어베이스 데이터베이스

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

        //변수 연결
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etName = findViewById(R.id.etName)
        btnRegister = findViewById(R.id.btnRegister)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //회원가입 버튼 clicklistner
        btnRegister.setOnClickListener {
            register()
        }
    }

    //회원가입
    private fun register() {
        //입력된 이메일, 비밀번호, 이름, 학과 가져오기
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val name = etName.text.toString().trim()
        val department = spinnerDepartment.selectedItem.toString()

        //모든 필드 입력되었나 확인하기
        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //회원가입 성공할 때
                        val user = auth.currentUser
                        val userInfo = hashMapOf(
                            "name" to name,
                            "department" to department
                        )
                        user?.let {
                            db.collection("users").document(it.uid)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    showToast("회원가입에 성공!")
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish() //액티비티 종료
                                }
                                .addOnFailureListener {
                                    showToast("회원정보 저장에 실패하였습니다: ${it.message}")
                                }
                        }
                    } else {
                        //회원가입 실패
                        showToast("회원가입에 실패하였습니다: ${task.exception?.message}")
                    }
                }
        } else {
            //모든 필드 입력이 안되었을 때
            showToast("모든 필드를 입력하세요!")
        }
    }

    //토스트 메시지 함수
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
