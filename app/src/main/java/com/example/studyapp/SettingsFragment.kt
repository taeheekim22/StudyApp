package com.example.studyapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {
    lateinit var tvEmail: TextView
    lateinit var tvName: TextView
    lateinit var tvDepartment: TextView
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
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
}