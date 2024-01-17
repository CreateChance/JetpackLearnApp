package com.example.paging7.model

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
sealed class RepoSearchResult {

    data class Success(val data: List<Repo>) : RepoSearchResult()

    data class Error(val exception: Exception) : RepoSearchResult()
}