package com.example.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * View model 的帮助类
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */
fun <T : ViewModel, A> singleArgViewModelFactory(constructor: (A) -> T): (A) -> ViewModelProvider.Factory {
    return { arg: A ->
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return constructor(arg) as T
            }
        }
    }
}
