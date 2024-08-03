package com.example.studyapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {
    // 이메일
    lateinit var tvEmail: TextView
    // 이름
    lateinit var tvName: TextView
    // 학과
    lateinit var tvDepartment: TextView
    // 정보 업데이트 버튼
    lateinit var btnUpdateInfo: Button
    // 로그아웃 버튼
    lateinit var btnLogout: Button
    // Firebase 인증
    lateinit var auth: FirebaseAuth
    // Firestore 데이터베이스
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 프래그먼트 레이아웃 인플레이트
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //변수 연결
        tvEmail = view.findViewById(R.id.tvEmail)
        tvName = view.findViewById(R.id.tvName)
        tvDepartment = view.findViewById(R.id.tvDepartment)
        btnUpdateInfo = view.findViewById(R.id.btnUpdateInfo)
        btnLogout = view.findViewById(R.id.btnLogout)

        // Firebase 인증, Firestore 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 로그인 상태 확인
        checkLoginStatus()

        //사용자 정보 가져오기
        val user = auth.currentUser
        user?.let {
            tvEmail.text = "이메일: ${it.email}"
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        tvName.text = "이름: ${document.getString("name")}"
                        tvDepartment.text = "학과: ${document.getString("department")}"
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리
                    showToast("사용자 정보를 가져오지 못했습니다: ${exception.message}")
                }
        }

        // 정보 업데이트 버튼 clicklistner
        btnUpdateInfo.setOnClickListener {
            showUpdateDialog()
        }

        // 로그아웃 버튼 clicklistner
        btnLogout.setOnClickListener {
            logout()
        }
    }

    // 로그인 상태 확인 함수
    private fun checkLoginStatus() {
        if (auth.currentUser == null) {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // 정보 업데이트 다이얼로그 표시 함수
    private fun showUpdateDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
        val etUpdateName = dialogView.findViewById<EditText>(R.id.etUpdateName)
        val etUpdatePassword = dialogView.findViewById<EditText>(R.id.etUpdatePassword)
        val spinnerUpdateDepartment = dialogView.findViewById<Spinner>(R.id.spinnerUpdateDepartment)

        // 스피너 어댑터 설정
        val departments = resources.getStringArray(R.array.departments_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, departments)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateDepartment.adapter = adapter

        // 정보 업데이트 다이얼로그 표시
        AlertDialog.Builder(context)
            .setTitle("정보 변경")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val newName = etUpdateName.text.toString()
                val newPassword = etUpdatePassword.text.toString()
                val newDepartment = spinnerUpdateDepartment.selectedItem.toString()

                // 사용자 정보 업데이트 함수 호출
                updateUserInfo(newName, newPassword, newDepartment)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 사용자 정보 업데이트 함수
    private fun updateUserInfo(newName: String, newPassword: String, newDepartment: String) {
        val user = auth.currentUser

        user?.let {
            // 이름 업데이트
            val updates = mutableMapOf<String, Any>()

            if (newName.isNotEmpty()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()

                it.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("이름을 변경하였습니다!")
                            tvName.text = "이름: $newName"
                        } else {
                            showToast("이름 변경에 실패하였습니다: ${task.exception?.message}")
                        }
                    }
            }

            // 비밀번호 업데이트
            if (newPassword.isNotEmpty()) {
                it.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("비밀번호를 변경하였습니다!")
                        } else {
                            showToast("비밀번호 변경에 실패하였습니다: ${task.exception?.message}")
                        }
                    }
            }

            // 학과 업데이트
            if (newDepartment.isNotEmpty()) {
                updates["department"] = newDepartment
            }

            // Firestore 문서 업데이트
            if (updates.isNotEmpty()) {
                db.collection("users").document(it.uid)
                    .update(updates)
                    .addOnSuccessListener {
                        showToast("정보 변경 성공")
                        refreshUserInfo() // UI 업데이트
                    }
                    .addOnFailureListener { e ->
                        // Exception 객체 선언, 메시지를 출력
                        showToast("정보 변경 실패: ${e.message}")
                    }
            }
        } ?: run {
            showToast("사용자 정보를 업데이트하는 동안 오류가 발생했습니다.")
        }
    }

    // 사용자 정보 새로고침 함수
    private fun refreshUserInfo() {
        val user = auth.currentUser
        user?.let {
            tvEmail.text = "이메일: ${it.email}"
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        tvName.text = "이름: ${document.getString("name")}"
                        tvDepartment.text = "학과: ${document.getString("department")}"
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리
                    showToast("사용자 정보를 새로고침하는 동안 오류가 발생했습니다: ${exception.message}")
                }
        } ?: run {
            showToast("사용자 정보를 새로고침하는 동안 오류가 발생했습니다.")
        }
    }

    // 로그아웃 함수
    private fun logout() {
        try {
            auth.signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } catch (e: Exception) {
            showToast("로그아웃하는 동안 오류가 발생했습니다: ${e.message}")
        }
    }

    // 토스트 메시지 표시 함수
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
