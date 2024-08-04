package com.example.studyapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var time = 0 // 타이머의 현재 시간(초 단위)
    private var isRunning = false // 타이머가 실행 중인지 여부
    private var handler = Handler(Looper.getMainLooper()) // UI 스레드에서 작업을 수행할 핸들러
    private var timerTask: Runnable? = null // 타이머 작업을 위한 Runnable
    private var accumulatedTime = 0 // 타이머의 누적 시간(초 단위)

    // UI 요소
    lateinit var hourTextView: TextView
    lateinit var minTextView: TextView
    lateinit var secTextView: TextView
    lateinit var startTime: FloatingActionButton
    lateinit var stopTime: FloatingActionButton
    lateinit var finalHourtv: TextView
    lateinit var finalMintv: TextView
    lateinit var finalSectv: TextView
    lateinit var resetBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_home 레이아웃을 인플레이트하여 뷰를 생성
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // UI 요소 초기화
        hourTextView = view.findViewById(R.id.hourTextView)
        minTextView = view.findViewById(R.id.minTextView)
        secTextView = view.findViewById(R.id.secTextView)
        startTime = view.findViewById(R.id.startTime)
        stopTime = view.findViewById(R.id.stopTime)
        finalHourtv = view.findViewById(R.id.finalHourtv)
        finalMintv = view.findViewById(R.id.finalMintv)
        finalSectv = view.findViewById(R.id.finalSectv)
        resetBtn = view.findViewById(R.id.resetBtn)

        // 저장된 누적 시간을 복원
        loadAccumulatedTime()
        updateFinalTimeDisplay()

        // '시작/일시정지' 버튼 클릭 리스너
        startTime.setOnClickListener {
            try {
                if (isRunning) {
                    pause() // 실행 중이면 타이머를 일시정지
                } else {
                    start() // 실행 중이 아니면 타이머 시작
                }
            } catch (e: Exception) {
                // 예외 처리 및 사용자에게 오류 메시지 표시
                Toast.makeText(requireContext(), "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Start/Stop button error: ${e.message}", e)
            }
        }

        // '정지' 버튼 클릭 리스너
        stopTime.setOnClickListener {
            try {
                stop() // 타이머 정지
            } catch (e: Exception) {
                // 예외 처리 및 사용자에게 오류 메시지 표시
                Toast.makeText(requireContext(), "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Stop button error: ${e.message}", e)
            }
        }

        // '리셋' 버튼 클릭 리스너
        resetBtn.setOnClickListener {
            try {
                // 누적 시간과 타이머 표시 리셋
                accumulatedTime = 0
                saveAccumulatedTime() // 누적 시간 저장
                finalHourtv.text = "00 :"
                finalMintv.text = "00 :"
                finalSectv.text = "00"
            } catch (e: Exception) {
                // 예외 처리 및 사용자에게 오류 메시지 표시
                Toast.makeText(requireContext(), "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Reset button error: ${e.message}", e)
            }
        }

        return view
    }

    private fun loadAccumulatedTime() {
        val sharedPreferences = requireContext().getSharedPreferences("StudyAppPrefs", Context.MODE_PRIVATE)
        accumulatedTime = sharedPreferences.getInt("accumulatedTime", 0)
    }

    private fun updateFinalTimeDisplay() {
        val totalSeconds = accumulatedTime
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        finalHourtv.text = String.format("%02d :", hours)
        finalMintv.text = String.format("%02d :", minutes)
        finalSectv.text = String.format("%02d", seconds)
    }


    private fun start() {
        isRunning = true
        startTime.setImageResource(android.R.drawable.ic_media_pause) // 버튼 아이콘 변경
        timerTask = object : Runnable {
            override fun run() {
                if (isRunning) {
                    time++ // 타이머 시간 증가
                    // 현재 시간 업데이트
                    val hours = time / 3600
                    val minutes = (time % 3600) / 60
                    val seconds = time % 60
                    hourTextView.text = String.format("%02d :", hours)
                    minTextView.text = String.format("%02d :", minutes)
                    secTextView.text = String.format("%02d", seconds)
                    handler.postDelayed(this, 1000) // 1초 후에 다시 실행
                }
            }
        }
        // 타이머 작업 시작
        timerTask?.let {
            handler.postDelayed(it, 1000)
        } ?: run {
            Log.e("HomeFragment", "TimerTask is null in start() method.")
        }
    }

    private fun pause() {
        isRunning = false
        startTime.setImageResource(android.R.drawable.ic_media_play) // 버튼 아이콘 변경
        timerTask?.let { handler.removeCallbacks(it) } // 타이머 작업 중지
    }

    private fun stop() {
        try {
            pause() // 타이머 일시정지

            // 현재 시간을 누적 시간에 추가
            accumulatedTime += time

            // 누적 시간 SharedPreferences에 저장
            saveAccumulatedTime()

            // 누적 시간 업데이트
            val totalSeconds = accumulatedTime
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            finalHourtv.text = String.format("%02d :", hours)
            finalMintv.text = String.format("%02d :", minutes)
            finalSectv.text = String.format("%02d", seconds)

            // 타이머 리셋
            time = 0
            hourTextView.text = "00 :"
            minTextView.text = "00 :"
            secTextView.text = "00"
        } catch (e: Exception) {
            // 예외 처리 및 사용자에게 오류 메시지 표시
            Toast.makeText(requireContext(), "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("HomeFragment", "Stop method error: ${e.message}", e)
        }
    }

    private fun saveAccumulatedTime() {
        val sharedPreferences = requireContext().getSharedPreferences("StudyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("accumulatedTime", accumulatedTime)
        editor.apply()
    }

}
