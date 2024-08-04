package com.example.studyapp

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONException

class GoalFragment : Fragment() {

    // 뷰 요소 선언
    private lateinit var tvGoalTime: TextView // 목표 시간
    private lateinit var etGoalHours: EditText // 목표 시간 (hour)
    private lateinit var etGoalMinutes: EditText // 목표시간 (minute)
    private lateinit var btnAddTodo: Button // 투두 리스트 추가 버튼
    private lateinit var todoList: LinearLayout // 투두 리스트
    private lateinit var etLongTermPlans: EditText // 장기 계획

    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences 객체
    private lateinit var editor: SharedPreferences.Editor // SharedPreferences 편집기

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 이 프래그먼트의 레이아웃을 인플레이트
        val view = inflater.inflate(R.layout.fragment_goal, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireActivity().getSharedPreferences("goalPrefs", 0)
        editor = sharedPreferences.edit()

        // 뷰 요소 초기화
        tvGoalTime = view.findViewById(R.id.tv_goal_time)
        etGoalHours = view.findViewById(R.id.et_goal_hours)
        etGoalMinutes = view.findViewById(R.id.et_goal_minutes)
        btnAddTodo = view.findViewById(R.id.btn_add_todo)
        todoList = view.findViewById(R.id.todo_list)
        etLongTermPlans = view.findViewById(R.id.et_long_term_plans)

        // 목표 시간 입력 처리
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateGoalTime()
            }
        }

        etGoalHours.addTextChangedListener(textWatcher)
        etGoalMinutes.addTextChangedListener(textWatcher)

        // 투두리스트 아이템 추가 버튼 클릭 처리
        btnAddTodo.setOnClickListener {
            try {
                addTodoItem()
            } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
                Log.e("GoalFragment", "Error adding todo item", e)
                Toast.makeText(context, "투두리스트 아이템을 추가하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 저장된 데이터 불러오기
        loadSavedData()

        return view
    }

    // 목표시간 설정
    private fun updateGoalTime() {
        try {
            val hours = etGoalHours.text.toString().ifEmpty { "0" }.toInt()
            val minutes = etGoalMinutes.text.toString().ifEmpty { "0" }.toInt()
            val totalMinutes = hours * 60 + minutes
            tvGoalTime.text = "오늘 공부 목표 시간: $hours 시간 $minutes 분 ($totalMinutes 분)"

            // 목표 시간 저장
            editor.putInt("goalHours", hours)
            editor.putInt("goalMinutes", minutes)
            editor.apply()
        } catch (e: NumberFormatException) {  // 사용자에게 다시 입력받는 메시지 출력
            Log.e("GoalFragment", "Invalid input for hours or minutes", e)
            Toast.makeText(context, "시간과 분을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
            Log.e("GoalFragment", "Error updating goal time", e)
            Toast.makeText(context, "목표 시간을 업데이트하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 투두리스트 아이템 추가
    private fun addTodoItem(task: String = "", isChecked: Boolean = false) {
        try {
            val todoItemView = layoutInflater.inflate(R.layout.todo_item, null) as LinearLayout

            val checkBox = todoItemView.findViewById<CheckBox>(R.id.checkbox)
            val etTodoTask = todoItemView.findViewById<EditText>(R.id.et_todo_task)
            val btnDelete = todoItemView.findViewById<Button>(R.id.btn_delete)

            // 삭제 버튼 클릭 처리
            btnDelete.setOnClickListener {
                try {
                    todoList.removeView(todoItemView)
                    saveTodoList()
                } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
                    Log.e("GoalFragment", "Error deleting todo item", e)
                    Toast.makeText(context, "투두리스트 아이템을 삭제하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            etTodoTask.setText(task)
            checkBox.isChecked = isChecked

            checkBox.setOnCheckedChangeListener { _, _ ->
                saveTodoList()
            }

            etTodoTask.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    saveTodoList()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            todoList.addView(todoItemView)
            saveTodoList()
        } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
            Log.e("GoalFragment", "Error adding todo item", e)
            Toast.makeText(context, "투두리스트 아이템을 추가하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 투두리스트 저장
    private fun saveTodoList() {
        try {
            val todoArray = JSONArray()
            for (i in 0 until todoList.childCount) {
                val todoItemView = todoList.getChildAt(i) as LinearLayout
                val checkBox = todoItemView.findViewById<CheckBox>(R.id.checkbox)
                val etTodoTask = todoItemView.findViewById<EditText>(R.id.et_todo_task)
                val todoObject = org.json.JSONObject()
                todoObject.put("task", etTodoTask.text.toString())
                todoObject.put("isChecked", checkBox.isChecked)
                todoArray.put(todoObject)
            }
            editor.putString("todoList", todoArray.toString())
            editor.apply()
        } catch (e: JSONException) {
            Log.e("GoalFragment", "Error saving todo list", e)
        }
    }

    // 저장된 데이터 불러오기
    private fun loadSavedData() {
        // 목표 시간 불러오기
        val hours = sharedPreferences.getInt("goalHours", 0)
        val minutes = sharedPreferences.getInt("goalMinutes", 0)
        etGoalHours.setText(hours.toString())
        etGoalMinutes.setText(minutes.toString())
        updateGoalTime()

        // 투두리스트 불러오기
        val todoListString = sharedPreferences.getString("todoList", null)
        if (!todoListString.isNullOrEmpty()) {
            try {
                val todoArray = JSONArray(todoListString)
                for (i in 0 until todoArray.length()) {
                    val todoObject = todoArray.getJSONObject(i)
                    val task = todoObject.getString("task")
                    val isChecked = todoObject.getBoolean("isChecked")
                    addTodoItem(task, isChecked)
                }
            } catch (e: JSONException) {
                Log.e("GoalFragment", "Error loading todo list", e)
            }
        }

        // 장기 계획 불러오기
        val longTermPlans = sharedPreferences.getString("longTermPlans", "")
        etLongTermPlans.setText(longTermPlans)

        etLongTermPlans.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                editor.putString("longTermPlans", s.toString())
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
