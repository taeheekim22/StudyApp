package com.example.studyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class WriteFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write, container, false)

        // Firebase 데이터베이스 참조 초기화
        database = FirebaseDatabase.getInstance().reference.child("posts")

        // UI 요소 초기화
        titleEditText = view.findViewById(R.id.etPostTitle)
        contentEditText = view.findViewById(R.id.etPostContent)
        val submitButton: Button = view.findViewById(R.id.btnSubmitPost)

        // 제출 버튼 클릭 리스너 설정
        submitButton.setOnClickListener {
            post()
        }

        return view
    }

    private fun post() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        // 제목과 내용이 모두 입력되었는지 확인
        if (title.isNotEmpty() && content.isNotEmpty()) {
            try {
                val postId = database.push().key
                if (postId != null) {
                    val post = Post(title, content)
                    database.child(postId).setValue(post).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 성공적으로 포스트가 저장되었을 때
                            Toast.makeText(requireContext(), "Post submitted successfully.", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack()
                        } else {
                            // 포스트 저장 실패 시
                            Toast.makeText(requireContext(), "Failed to submit post: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            Log.e("WriteFragment", "Post submission failed: ${task.exception?.message}", task.exception)
                        }
                    }
                }
            } catch (e: Exception) {
                // 포스트 저장 중 오류 발생 시
                Toast.makeText(requireContext(), "Error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("WriteFragment", "Error occurred during post submission: ${e.message}", e)
            }
        } else {
            // 제목 또는 내용이 비어 있는 경우 사용자에게 알림
            Toast.makeText(requireContext(), "Title and content cannot be empty.", Toast.LENGTH_SHORT).show()
        }
    }
}
