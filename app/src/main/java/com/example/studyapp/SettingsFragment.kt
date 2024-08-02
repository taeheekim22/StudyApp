package com.example.studyapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsFragment : Fragment() {
    lateinit var tvUsername: TextView
    lateinit var tvName: TextView
    lateinit var tvDepartment: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername = view.findViewById(R.id.tvUsername)
        tvName = view.findViewById(R.id.tvName)
        tvDepartment = view.findViewById(R.id.tvDepartment)

        val sharedPreferences: SharedPreferences=requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val username=arguments?.getString("username")?:"없음"
        val name=sharedPreferences.getString("${username}_name", "없음")
        val department=sharedPreferences.getString("${username}_department", "없음")

        tvUsername.text = "아이디: $username"
        tvName.text = "이름: $name"
        tvDepartment.text = "학과: $department"
    }

    companion object {
        @JvmStatic
        fun newInstance(username: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString("username", username)
                }
            }
    }
}