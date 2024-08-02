package com.example.studyapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var homeFragment: HomeFragment
    lateinit var goalFragment: GoalFragment
    lateinit var infoFragment: InfoFragment
    lateinit var chatFragment: ChatFragment
    lateinit var settingsFragment: SettingsFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavi)

        //전달된 username 저장
        val username=intent.getStringExtra("username")
        if(username!=null){
            val sharedPreferences:SharedPreferences=getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor=sharedPreferences.edit()
            editor.putString("logged_in_username", username)
            editor.apply()
        }

        // 프래그먼트 초기화
        homeFragment = HomeFragment()
        goalFragment = GoalFragment()
        infoFragment = InfoFragment()
        chatFragment = ChatFragment()
        settingsFragment = SettingsFragment.newInstance(username ?: "example_username")

        // (기존)처음 시작 화면을 HomeFragment로 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, homeFragment)
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.navigation_goal -> {
                    switchFragment(goalFragment)
                    true
                }
                R.id.navigation_info -> {
                    switchFragment(infoFragment)
                    true
                }
                R.id.navigation_chat -> {
                    switchFragment(chatFragment)
                    true
                }
                R.id.navigation_set -> {
                    val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs",
                        MODE_PRIVATE)
                    val username = sharedPreferences.getString("logged_in_username",
                        "example_username")
                    switchFragment(SettingsFragment.newInstance(username!!))
                    true
                }
                else -> false
            }
        }
    }

    // 화면전환 시 프래그먼트 상태를 유지하도록 하는 함수
    private fun switchFragment(fragment: Fragment): Boolean {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // 현재 보여지고 있는 프래그먼트를 숨김
        fragmentManager.fragments.forEach { currentFragment ->
            transaction.hide(currentFragment)
        }

        // 선택한 프래그먼트를 추가하거나 보여줌
        if (!fragment.isAdded) {
            transaction.add(R.id.nav_host_fragment, fragment)
        } else {
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        return true
    }
}
