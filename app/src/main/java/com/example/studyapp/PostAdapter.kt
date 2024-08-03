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

class PostAdapter(
    context: Context,
    private val posts: List<Post>,
    private val postKeys: List<String>
) : ArrayAdapter<Post>(context, 0, posts) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("posts")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_post, parent, false)

        val post = getItem(position)!!
        val postKey = postKeys[position]  // Assuming you keep track of the keys

        val titleTextView: TextView = view.findViewById(R.id.tvPostTitle)
        val contentTextView: TextView = view.findViewById(R.id.tvPostContent)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeletePost)

        titleTextView.text = post.title
        contentTextView.text = post.content

        deleteButton.setOnClickListener {
            database.child(postKey).removeValue()
        }

        return view
    }
}
