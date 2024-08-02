package com.example.studyapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class GoalFragment : Fragment() {

    private lateinit var tvGoalTime: TextView
    private lateinit var etGoalHours: EditText
    private lateinit var etGoalMinutes: EditText
    private lateinit var btnAddTodo: Button
    private lateinit var todoList: LinearLayout
    private lateinit var etLongTermPlans: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_goal, container, false)

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
            addTodoItem()
        }

        return view
    }

    private fun updateGoalTime() {
        val hours = etGoalHours.text.toString().ifEmpty { "0" }.toInt()
        val minutes = etGoalMinutes.text.toString().ifEmpty { "0" }.toInt()
        val totalMinutes = hours * 60 + minutes
        tvGoalTime.text = "Today's Study Goal Time: $hours hours $minutes minutes ($totalMinutes minutes)"
    }

    private fun addTodoItem() {
        val todoItemView = layoutInflater.inflate(R.layout.todo_item, null) as LinearLayout

        val checkBox = todoItemView.findViewById<CheckBox>(R.id.checkbox)
        val etTodoTask = todoItemView.findViewById<EditText>(R.id.et_todo_task)
        val btnDelete = todoItemView.findViewById<Button>(R.id.btn_delete)

        btnDelete.setOnClickListener {
            todoList.removeView(todoItemView)
        }

        todoList.addView(todoItemView)
    }
}
