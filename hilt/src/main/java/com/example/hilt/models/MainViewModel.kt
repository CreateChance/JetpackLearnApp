package com.example.hilt.models

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 演示 view model 的字段注入方式
 * 通过 hilt 内置的 @HiltViewModel + @Inject 注解实现注入
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/4
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    val user: User,
    val app: Application, // hilt 内置 application 的 provider，可以实现自动注入
) : ViewModel() {
    override fun toString(): String {
        return "MainViewModel(user=$user, app=$app)"
    }
}