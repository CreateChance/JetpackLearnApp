package com.example.paging6

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 文章数据类
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/16
 */
data class Article(
    val id: Int,
    val title: String,
    val desc: String,
    val created: LocalDateTime
) {
    private val articleDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val createdText: String = articleDateFormatter.format(created)
}
