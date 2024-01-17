package com.example.livedata

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap

class MainActivity : AppCompatActivity() {
    private val liveData1 = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val liveData2 = liveData1.distinctUntilChanged().map {
            "$it@append2"
        }

        val liveData3 = liveData1.distinctUntilChanged().switchMap {
            val ret = MutableLiveData("$it@append3")
            Log.d("GAOCHAO", "Ret: ${ret.hashCode()}")
            ret
        }

        liveData1.observe(this) {
            Log.d("GAOCHAO", "1: updated $it")
        }

        liveData2.observe(this) {
            Log.d("GAOCHAO", "2: updated $it")
        }
        liveData3.observe(this) {
            Log.d("GAOCHAO", "3: updated $it")
        }

        Log.d("GAOCHAO", "livedata1: ${liveData1.hashCode()}, livedata2: ${liveData2.hashCode()}, livedata3: ${liveData3.hashCode()}")
    }

    fun update(view: View) {
        liveData1.value = System.currentTimeMillis().toString()
    }
}