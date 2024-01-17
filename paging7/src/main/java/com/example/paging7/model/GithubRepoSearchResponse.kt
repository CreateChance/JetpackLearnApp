package com.example.paging7.model

import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
data class GithubRepoSearchResponse(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<Repo> = emptyList(),
)
