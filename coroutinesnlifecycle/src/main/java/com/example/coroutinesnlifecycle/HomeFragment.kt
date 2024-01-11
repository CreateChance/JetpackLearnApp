package com.example.coroutinesnlifecycle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenStarted
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // launch task.
        Log.d("GAOCHAO", "HomeFragment onViewCreated")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    Log.d("GAOCHAO", "0. 拿到计算结果: ${doSomething(0)}")
                } catch (e: CancellationException) {
                    Log.d("GAOCHAO", "0. Task 被取消了！$e")
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            whenStarted {
                try {
                    Log.d("GAOCHAO", "1. 拿到计算结果: ${doSomething(1)}")
                } catch (e: CancellationException) {
                    Log.d("GAOCHAO", "1. Task 被取消了！$e")
                }
            }
        }

        testLiveData1().observe(viewLifecycleOwner) {
            Log.d("GAOCHAO", "testLiveData1 结果：$it")
        }
        testLiveData2().observe(viewLifecycleOwner) {
            Log.d("GAOCHAO", "testLiveData2 结果：$it")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("GAOCHAO", "HomeFragment onStart")
    }

    private fun testLiveData1(): LiveData<String> {
        val user: LiveData<String> = liveData {
            emit(doSomething(2))
            emit(doSomething(3))
        }
        return user
    }

    private fun testLiveData2(): LiveData<Int> {
        return liveData {
            val disposable = emitSource(
                getUserFromSlowerSource()
            )
            val userId = getUserFromFasterSource()
            disposable.dispose()
            emitSource(MutableLiveData(userId))
        }
    }

    private suspend fun getUserFromSlowerSource(): LiveData<Int> {
        return liveData {
            delay(1000)
            100
        }
    }

    private suspend fun getUserFromFasterSource(): Int {
        delay(5000)
        return 200
    }

    private suspend fun doSomething(seq: Int): String {
        Log.d("GAOCHAO", "$seq. 开始执行耗时操作...")
        delay(3000)
        Log.d("GAOCHAO", "$seq. 耗时操作执行完毕！")
        return "[$seq] ResultString."
    }
}