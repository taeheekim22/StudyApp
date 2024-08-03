package com.example.studyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WriteFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_write, container, false)

        database = FirebaseDatabase.getInstance().reference.child("posts")

        titleEditText = view.findViewById(R.id.etPostTitle)
        contentEditText = view.findViewById(R.id.etPostContent)
        val submitButton: Button = view.findViewById(R.id.btnSubmitPost)

        submitButton.setOnClickListener {
            post()
        }

        return view
    }

    private fun post() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()
        if (title.isNotEmpty() && content.isNotEmpty()) {
            val postId = database.push().key
            if (postId != null) {
                val post = Post(title, content)
                database.child(postId).setValue(post).addOnCompleteListener {
                    if (it.isSuccessful) {
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        // Handle failure
                    }
                }
            }
        }
    }
}
