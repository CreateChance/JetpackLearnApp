package com.example.paging6

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.max

/**
 * Articles Paging 数据源
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/16
 */

private const val STARTING_KEY = 0

class ArticlesPagingSource : PagingSource<Int, Article>() {
    private val firstArticleCreatedTime = LocalDateTime.now()

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        Log.d("GAOCHAO", "getRefreshKey: $state")
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(article.id - (state.config.pageSize / 2))
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        Log.d("GAOCHAO", "load: ${params.key} ${params.loadSize}")
        val start = params.key ?: STARTING_KEY

        val range = start.until(start + params.loadSize)

        // pretend to delay
        if (start != STARTING_KEY) {
            delay(3000)
        }

        return LoadResult.Page(
            data = range.map {
                Article(
                    id = it,
                    title = "Article $it",
                    desc = "This describes article $it",
                    created = firstArticleCreatedTime.minusDays(it.toLong())
                )
            },
            prevKey = when (start) {
                STARTING_KEY -> null
                else -> ensureValidKey(range.first - params.loadSize)
            },
            nextKey = range.last + 1
        )
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}