package com.example.studyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
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
        // fragment_chat 레이아웃을 인플레이트하여 뷰를 생성
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Firebase 데이터베이스 참조 초기화 (posts 노드 참조)
        database = FirebaseDatabase.getInstance().reference.child("posts")

        // ListView와 버튼 초기화
        postListView = view.findViewById(R.id.lvPosts)
        val newPostButton: Button = view.findViewById(R.id.btnNewPost)

        // '새 게시물 작성' 버튼 클릭 시 WriteFragment로 전환
        newPostButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, WriteFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // ArrayAdapter를 사용하여 ListView에 게시물 제목을 표시
        val postAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, ArrayList())
        postListView.adapter = postAdapter

        // Firebase 데이터베이스의 'posts' 노드에 리스너 추가
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 데이터가 변경되면 ListView 업데이트
                postAdapter.clear() // 기존 데이터 제거
                postKeys.clear() // 게시물 키 리스트 초기화
                for (postSnapshot in snapshot.children) {
                    // 각 게시물 데이터 가져오기
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        postAdapter.add(post.title) // 제목 추가
                        postKeys.add(postSnapshot.key ?: "") // 게시물 키 추가
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패 시 사용자에게 알림
                Toast.makeText(requireContext(), "데이터를 가져오는 데 실패했습니다: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("ChatFragment", "Database error: ${error.message}", error.toException())
            }
        })

        // ListView 아이템 클릭 시 PostDetailFragment로 전환
        postListView.setOnItemClickListener { _, _, position, _ ->
            val postKey = postKeys.getOrNull(position) ?: run {
                // 클릭된 항목이 유효하지 않을 경우 사용자에게 알림
                Toast.makeText(requireContext(), "잘못된 항목이 선택되었습니다.", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }
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
