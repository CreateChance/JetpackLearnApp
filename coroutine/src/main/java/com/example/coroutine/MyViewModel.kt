package com.example.coroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel 类
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */
class MyViewModel(private val repository: MyRepository) : ViewModel() {

    companion object {
        /**
         * Factory for creating [MyViewModel]
         *
         * @param arg the repository to pass to [MyViewModel]
         */
        val FACTORY = singleArgViewModelFactory(::MyViewModel)
    }

    // public data.
    val title = repository.title
    val snackBar: LiveData<String?>
        get() = _snackBar
    val spinner: LiveData<Boolean>
        get() = _spinner
    val taps: MutableLiveData<String>
        get() = _taps

    // private data.
    private val _snackBar = MutableLiveData<String?>()
    private val _spinner = MutableLiveData(false)
    private var tapCount = 0
    private val _taps = MutableLiveData("$tapCount taps")

    fun onClicked() {
        refreshTitle()
        updateTaps()
    }

    private fun updateTaps() {
        viewModelScope.launch {
            tapCount++
            // 延迟 1s 后刷新界面
            delay(1_000)
            _taps.postValue("$tapCount taps")
        }
    }

    private fun refreshTitle() {
        launchDataLoad {
            repository.refreshTitle()
        }
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                // 先让等待转圈
                _spinner.value = true
                // 执行目标函数
                block()
            } catch (e: TitleRefreshError) {
                // 通过 snack bar 来提示用户错误情况
                _snackBar.value = e.message
            } finally {
                // 最后一定要让转圈停止
                _spinner.value = false
            }
        }
    }
}
