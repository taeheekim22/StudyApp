package com.example.studyapp

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

class GoalFragment : Fragment() {

    // 뷰 요소 선언
    private lateinit var tvGoalTime: TextView // 목표 시간
    private lateinit var etGoalHours: EditText // 목표 시간 (hour)
    private lateinit var etGoalMinutes: EditText // 목표시간 (minute)
    private lateinit var btnAddTodo: Button // 투두 리스트 추가 버튼
    private lateinit var todoList: LinearLayout // 투두 리스트
    private lateinit var etLongTermPlans: EditText // 장기 계획

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 이 프래그먼트의 레이아웃을 인플레이트
        val view = inflater.inflate(R.layout.fragment_goal, container, false)

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

        return view
    }

    // 목표시간 설정
    private fun updateGoalTime() {
        try {
            val hours = etGoalHours.text.toString().ifEmpty { "0" }.toInt()
            val minutes = etGoalMinutes.text.toString().ifEmpty { "0" }.toInt()
            val totalMinutes = hours * 60 + minutes
            tvGoalTime.text = "오늘 공부 목표 시간: $hours 시간 $minutes 분 ($totalMinutes 분)"
        } catch (e: NumberFormatException) {  // 사용자에게 다시 입력받는 메시지 출력
            Log.e("GoalFragment", "Invalid input for hours or minutes", e)
            Toast.makeText(context, "시간과 분을 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
            Log.e("GoalFragment", "Error updating goal time", e)
            Toast.makeText(context, "목표 시간을 업데이트하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 투두리스트 아이템 추가
    private fun addTodoItem() {
        try {
            val todoItemView = layoutInflater.inflate(R.layout.todo_item, null) as LinearLayout

            val checkBox = todoItemView.findViewById<CheckBox>(R.id.checkbox)
            val etTodoTask = todoItemView.findViewById<EditText>(R.id.et_todo_task)
            val btnDelete = todoItemView.findViewById<Button>(R.id.btn_delete)

            // 삭제 버튼 클릭 처리
            btnDelete.setOnClickListener {
                try {
                    todoList.removeView(todoItemView)
                } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
                    Log.e("GoalFragment", "Error deleting todo item", e)
                    Toast.makeText(context, "투두리스트 아이템을 삭제하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            todoList.addView(todoItemView)
        } catch (e: Exception) { // 예외처리 및 사용자에게 오류 메시지 출력
            Log.e("GoalFragment", "Error adding todo item", e)
            Toast.makeText(context, "투두리스트 아이템을 추가하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
