package com.example.coroutine

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.withTimeout

/**
 * 数据 Repo 类
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */
class MyRepository(private val titleService: ITitleService, private val titleDao: TitleDao) {

    // 将数据库原始数据转换为 String，不暴露原始 entity 类型
    val title: LiveData<String> = titleDao.titleLiveData.map { it.title }

    @Throws(TitleRefreshError::class)
    suspend fun refreshTitle() {
        try {
            // load next title from network.
            val result = withTimeout(5_000) {
                titleService.nextTitle()
            }
            Log.d("GAOCHAO", "拿到的 title: $result")
            // save it to database
            titleDao.insertTitle(Title(title = result))
        } catch (e: Throwable) {
            throw TitleRefreshError("Unable to refresh title, error: ${e.message}", e)
        }
    }
}

class TitleRefreshError(message: String, cause: Throwable?) : Throwable(message, cause)
