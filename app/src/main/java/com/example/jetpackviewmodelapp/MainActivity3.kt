package com.example.jetpackviewmodelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 演示通过 live data 实现两个 fragment 之间的数据通讯功能
 */
class MainActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
    }
}

// view model，保存进度数据
class MyViewModel3 : ViewModel() {
    var progress: MutableLiveData<Int> = MutableLiveData(0)
}

// 第一个 fragment
class FragmentOne : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_one, container, false)
        val seekBar = root.findViewById<SeekBar>(R.id.seekBar)
        // 注意：这里必须使用 activity 作为 view model store owner
        // 这样才能确保多个 fragment 之间获取到的 view model 是同一个对象，才能实现数据协同
        val viewModel = ViewModelProvider(requireActivity())[MyViewModel3::class.java]
        viewModel.progress.observe(requireActivity()) {
            seekBar.progress = it
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.progress.value = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return root
    }
}

// 第二个 fragment
class FragmentTwo : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_two, container, false)
        val seekBar = root.findViewById<SeekBar>(R.id.seekBar)
        // 注意：这里必须使用 activity 作为 view model store owner
        // 这样才能确保多个 fragment 之间获取到的 view model 是同一个对象，才能实现数据协同
        val viewModel = ViewModelProvider(requireActivity())[MyViewModel3::class.java]
        viewModel.progress.observe(requireActivity()) {
            seekBar.progress = it
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.progress.value = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return root
    }
}

