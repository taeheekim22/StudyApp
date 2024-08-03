package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var postListView: ListView
    private val postKeys: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        database = FirebaseDatabase.getInstance().reference.child("posts")

        postListView = view.findViewById(R.id.lvPosts)
        val newPostButton: Button = view.findViewById(R.id.btnNewPost)

        newPostButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, WriteFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val postAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, ArrayList())
        postListView.adapter = postAdapter

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postAdapter.clear()
                postKeys.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        postAdapter.add(post.title)
                        postKeys.add(postSnapshot.key ?: "")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        postListView.setOnItemClickListener { _, _, position, _ ->
            val postKey = postKeys[position]
            Log.d("ChatFragment", "Post key clicked: $postKey")
            val fragment = PostDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("postKey", postKey)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
