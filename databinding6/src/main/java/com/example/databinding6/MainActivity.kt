package com.example.databinding6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.databinding6.databinding.ActivityMainBinding

/**
 * 演示 DataBinding + ViewModel + LiveData 的综合使用
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        // 这一步必须执行，因为 view model 中的数据是 livedata，它需要感知到 UI 的生命周期才能自定更新通知
        // 否则，UI 将不能更新
        dataBinding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        dataBinding.viewModel = viewModel
    }
}

class MyViewModel : ViewModel() {
    val aTeamScore = MutableLiveData(0)
    val bTeamScore = MutableLiveData(0)

    private var aLast: Int = aTeamScore.value!!
    private var bLast: Int = bTeamScore.value!!
    fun aTeamAdd(score: Int) {
        saveLastScore()
        aTeamScore.value = aTeamScore.value!! + score
    }

    fun bTeamAdd(score: Int) {
        saveLastScore()
        bTeamScore.value = bTeamScore.value!! + score
    }

    fun undo() {
        aTeamScore.value = aLast
        bTeamScore.value = bLast
    }

    fun reset() {
        aTeamScore.value = 0
        bTeamScore.value = 0
        saveLastScore()
    }

    private fun saveLastScore() {
        aLast = aTeamScore.value!!
        bLast = bTeamScore.value!!
    }
}
