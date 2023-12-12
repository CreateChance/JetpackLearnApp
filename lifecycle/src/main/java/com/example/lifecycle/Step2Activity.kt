package com.example.lifecycle

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class Step2Activity : AppCompatActivity() {
    private lateinit var chronometer: MyChronometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2)

        chronometer = findViewById(R.id.chronometer)
        lifecycle.addObserver(chronometer)
    }
}

class MyChronometer(context: Context, attr: AttributeSet) : Chronometer(context, attr), LifecycleEventObserver {

    private var baseTime: Long = 0
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_RESUME -> {
                base = SystemClock.elapsedRealtime() - baseTime
                start()
            }
            Lifecycle.Event.ON_PAUSE -> {
                baseTime = SystemClock.elapsedRealtime() - base
                stop()
            }
            else -> {}
        }
    }
}
