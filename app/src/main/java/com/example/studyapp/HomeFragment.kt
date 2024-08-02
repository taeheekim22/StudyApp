package com.example.studyapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var time = 0
    private var isRunning = false
    private var handler = Handler(Looper.getMainLooper())
    private var timerTask: Runnable? = null
    private var accumulatedTime = 0 // 누적 시간

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        hourTextView = view.findViewById(R.id.hourTextView)
        minTextView = view.findViewById(R.id.minTextView)
        secTextView = view.findViewById(R.id.secTextView)
        startTime = view.findViewById(R.id.startTime)
        stopTime = view.findViewById(R.id.stopTime)
        finalHourtv = view.findViewById(R.id.finalHourtv)
        finalMintv = view.findViewById(R.id.finalMintv)
        finalSectv = view.findViewById(R.id.finalSectv)
        resetBtn = view.findViewById(R.id.resetBtn)

        startTime.setOnClickListener {
            if (isRunning) {
                pause()
            } else {
                start()
            }
        }

        stopTime.setOnClickListener {
            stop()
        }

        resetBtn.setOnClickListener {
            accumulatedTime = 0
            finalHourtv.text = "00 :"
            finalMintv.text = "00 :"
            finalSectv.text = "00"
        }
        return view
    }

    private fun start() {
        isRunning = true
        startTime.setImageResource(android.R.drawable.ic_media_pause)
        timerTask = object : Runnable {
            override fun run() {
                if (isRunning) {
                    time++
                    // 현재 시간 업데이트
                    val hours = time / 3600
                    val minutes = (time % 3600) / 60
                    val seconds = time % 60
                    hourTextView.text = String.format("%02d :", hours)
                    minTextView.text = String.format("%02d :", minutes)
                    secTextView.text = String.format("%02d", seconds)
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.postDelayed(timerTask!!, 1000)
    }

    private fun pause() {
        isRunning = false
        startTime.setImageResource(android.R.drawable.ic_media_play)
        timerTask?.let { handler.removeCallbacks(it) }
    }

    private fun stop() {
        pause() // 타이머 일시정지

        // 현재 시간을 누적 시간에 추가
        accumulatedTime += time

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
    }
}