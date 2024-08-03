package com.example.studyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import android.util.Log

class PostAdapter(
    context: Context,
    private val posts: List<Post>,
    private val postKeys: List<String>
) : ArrayAdapter<Post>(context, 0, posts) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("posts")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // null인 경우 view inflate
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_post, parent, false)

        // 현재 위치의 post key 가져오기
        val post = getItem(position)!!
        val postKey = postKeys.getOrNull(position) ?: run {
            Log.e("PostAdapter", "Invalid position: $position")
            return view
        }

        val titleTextView: TextView = view.findViewById(R.id.tvPostTitle)
        val contentTextView: TextView = view.findViewById(R.id.tvPostContent)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeletePost)

        titleTextView.text = post.title
        contentTextView.text = post.content

        // 삭제 버튼 눌렀을 때 게시글 관련 데이터 삭제 리스너
        deleteButton.setOnClickListener {
            try {
                database.child(postKey).removeValue().addOnSuccessListener {
                    Toast.makeText(context, "Post deleted successfully.", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to delete post: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("PostAdapter", "Error deleting post: ${e.message}", e)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("PostAdapter", "Error handling delete button click: ${e.message}", e)
            }
        }

        return view
    }
}
