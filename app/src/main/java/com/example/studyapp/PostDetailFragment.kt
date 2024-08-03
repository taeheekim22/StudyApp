package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var postKey: String
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val titleTextView: TextView = view.findViewById(R.id.tvPostTitle)
        val contentTextView: TextView = view.findViewById(R.id.tvPostContent)
        val commentEditText: EditText = view.findViewById(R.id.etComment)
        val submitCommentButton: Button = view.findViewById(R.id.btnSubmitComment)
        val deletePostButton: Button = view.findViewById(R.id.btnDeletePost)
        commentRecyclerView = view.findViewById(R.id.rvComments)

        postKey = arguments?.getString("postKey") ?: ""
        Log.d("PostDetailFragment", "Post key received: $postKey")
        database = FirebaseDatabase.getInstance().reference.child("posts").child(postKey)

        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentAdapter = CommentAdapter()
        commentRecyclerView.adapter = commentAdapter

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    titleTextView.text = post.title
                    contentTextView.text = post.content
                }

                val comments = ArrayList<Comment>()
                for (commentSnapshot in snapshot.child("comments").children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        comments.add(comment)
                    }
                }
                commentAdapter.submitList(comments)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        submitCommentButton.setOnClickListener {
            val commentContent = commentEditText.text.toString()
            if (commentContent.isNotEmpty()) {
                val commentId = database.child("comments").push().key
                if (commentId != null) {
                    val comment = Comment(userId = "anonymous", content = commentContent)
                    database.child("comments").child(commentId).setValue(comment)
                    commentEditText.text.clear()
                }
            }
        }

        deletePostButton.setOnClickListener {
            Log.d("PostDetailFragment", "Delete button clicked for post: $postKey")
            database.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("PostDetailFragment", "Post deleted successfully: $postKey")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Log.e("PostDetailFragment", "Failed to delete post: ${task.exception}")
                }
            }
        }

        return view
    }
}

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val comments = mutableListOf<Comment>()

    fun submitList(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(comment: Comment) {
            textView.text = comment.content
        }
    }
}
