package com.example.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer

class Step1Activity : AppCompatActivity() {
    private lateinit var chronometer: Chronometer
    private var baseTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step1)

        chronometer = findViewById(R.id.chronometer)
    }

    override fun onResume() {
        super.onResume()
        chronometer.base = SystemClock.elapsedRealtime() - baseTime
        chronometer.start()
    }

    override fun onPause() {
        super.onPause()
        baseTime = SystemClock.elapsedRealtime() - chronometer.base
        chronometer.stop()
    }
}