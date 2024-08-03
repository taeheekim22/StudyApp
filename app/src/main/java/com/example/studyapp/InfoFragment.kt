package com.example.studyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class InfoFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var spinner2: Spinner//학과 선택
    private lateinit var tvinfo: TextView//선택학과
    private lateinit var checkboxContainer: LinearLayout//학과 선택 시 체크박스

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        //스피너, 텍스트 뷰, 체크박스 초기화
        spinner2 = view.findViewById(R.id.spinner2)
        tvinfo = view.findViewById(R.id.tvinfo)
        checkboxContainer = view.findViewById(R.id.checkbox_container)

        //스피너 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.departments_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }

        spinner2.onItemSelectedListener = this

        return view
    }

    //아이템 선택 시 호출 함수
    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        val selectedItem = parent?.getItemAtPosition(position).toString()//선택된 아이템의 문자열 가져오기
            tvinfo.text = selectedItem//선택된 아이템 표시


        checkboxContainer.removeAllViews()//이전 체크박스 지우기

        //선택된 아이템에 따라 체크박스 추가
        val optionsArrayId = when (selectedItem) {
            "정보보호학과" -> R.array.정보보호학과
            "소프트웨어융합학과" -> R.array.소프트웨어융합학과
            else -> null
        }
        //체크박스 내용 가져와 추가
        optionsArrayId?.let {
            val options = resources.getStringArray(it)
            for (option in options) {
                addCheckbox(option)
            }
        }
    }


    //아무것도 선택되지 않았을 때
    override fun onNothingSelected(parent: AdapterView<*>?) {
        //텍스트 뷰와 체크박스 컨테이너 초기화
        tvinfo.text = ""
        checkboxContainer.removeAllViews()
    }

    ///체크박스 추가
    private fun addCheckbox(text: String) {
        val checkbox = CheckBox(context)
        checkbox.text = text
        checkboxContainer.addView(checkbox)
    }
}
