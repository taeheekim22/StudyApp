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
    lateinit var tvEmail: TextView
    lateinit var tvName: TextView
    lateinit var tvDepartment: TextView
    lateinit var btnUpdateInfo: Button
    lateinit var btnLogout:Button
    lateinit var auth: FirebaseAuth
    lateinit var db:FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEmail = view.findViewById(R.id.tvEmail)
        tvName = view.findViewById(R.id.tvName)
        tvDepartment = view.findViewById(R.id.tvDepartment)
        btnUpdateInfo=view.findViewById(R.id.btnUpdateInfo)
        btnLogout=view.findViewById(R.id.btnLogout)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        checkLoginStatus()

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
                .addOnFailureListener {
                    // Handle failure
                }
        }

        btnUpdateInfo.setOnClickListener{
            showUpdateDialog()
        }

        btnLogout.setOnClickListener{
            logout()
        }
    }

    private fun checkLoginStatus(){
        if(auth.currentUser==null) {
            val intent=Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showUpdateDialog(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
        val etUpdateName = dialogView.findViewById<EditText>(R.id.etUpdateName)
        val etUpdatePassword = dialogView.findViewById<EditText>(R.id.etUpdatePassword)
        val spinnerUpdateDepartment = dialogView.findViewById<Spinner>(R.id.spinnerUpdateDepartment)

        val departments = resources.getStringArray(R.array.departments_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, departments)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUpdateDepartment.adapter = adapter

        AlertDialog.Builder(context)
            .setTitle("정보 변경")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val newName = etUpdateName.text.toString()
                val newPassword = etUpdatePassword.text.toString()
                val newDepartment = spinnerUpdateDepartment.selectedItem.toString()

                updateUserInfo(newName, newPassword, newDepartment)
            }
            .setNegativeButton("취소", null)
            .show()
    }

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
                            showToast("이름 변경에 실패하였습니다:  ${task.exception?.message}")
                        }
                    }
            }

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
                        // Exception 객체를 명시적으로 선언하고 메시지를 출력
                        showToast("정보 변경 실패: ${e.message}")
                    }
            }
        }
    }

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
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    private fun logout() {
        auth.signOut()
        val intent= Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity()
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}


