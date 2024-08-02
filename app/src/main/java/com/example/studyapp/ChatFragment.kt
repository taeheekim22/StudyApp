package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Post(val title: String = "", val content: String = "")

class ChatFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var postListView: ListView
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().reference.child("posts")

        postListView = view.findViewById(R.id.lvPosts)
        titleEditText = view.findViewById(R.id.etPostTitle)
        contentEditText = view.findViewById(R.id.etPostContent)
        val postButton: Button = view.findViewById(R.id.btnPost)

        // 게시글 추가 버튼 클릭 이벤트 처리
        postButton.setOnClickListener {
            post()
        }

        // 게시글을 리스트뷰에 표시하기
        val postAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, ArrayList())
        postListView.adapter = postAdapter

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postAdapter.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        postAdapter.add("${post.title}\n${post.content}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ChatFragment", "Failed to read value.", error.toException())
            }
        })
    }

    private fun post() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()
        if (title.isNotEmpty() && content.isNotEmpty()) {
            val postId = database.push().key
            if (postId != null) {
                val post = Post(title, content)
                database.child(postId).setValue(post)
            }
        }
    }
}
