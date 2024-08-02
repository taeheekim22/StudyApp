package com.example.studyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class InfoFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var spinner2: Spinner
    private lateinit var textview7: TextView
    private lateinit var checkboxContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        // Initialize views
        spinner2 = view.findViewById(R.id.spinner2)
        textview7 = view.findViewById(R.id.textView7)
        checkboxContainer = view.findViewById(R.id.checkbox_container)

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

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        textview7.text = selectedItem

        // Clear previous checkboxes
        checkboxContainer.removeAllViews()

        // Add checkboxes based on selected item
        val optionsArrayId = when (selectedItem) {
            "정보보호학과" -> R.array.정보보호학과
            "소프트웨어융합학과" -> R.array.소프트웨어융합학과
            else -> null
        }

        optionsArrayId?.let {
            val options = resources.getStringArray(it)
            for (option in options) {
                addCheckbox(option)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        textview7.text = ""
        checkboxContainer.removeAllViews()
    }

    private fun addCheckbox(text: String) {
        val checkbox = CheckBox(context)
        checkbox.text = text
        checkboxContainer.addView(checkbox)
    }
}
