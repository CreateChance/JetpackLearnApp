package com.example.hilt.models

import android.app.Activity
import android.app.Application
import javax.inject.Inject

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/4
 */
class DataHolder @Inject constructor(
    val user: User,
    val app: Application, // hilt 内置 application 的 provider，可以实现自动注入
    val activity: Activity // hilt 内置 activity 的 provider，可以实现自动注入
) {
    override fun toString(): String {
        return "DataHolder(user=$user, app=$app, activity=$activity)"
    }
}