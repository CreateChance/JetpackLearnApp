package com.example.paging6

import java.time.LocalDateTime

/**
 * 文章数据源
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/16
 */
class ArticleRepository {

    fun articlePagingSource() = ArticlesPagingSource()
}