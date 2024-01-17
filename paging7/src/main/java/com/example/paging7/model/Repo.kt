package com.example.paging7.model

import com.google.gson.annotations.SerializedName

/**
 * Github 仓库
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
data class Repo(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("full_name") val fullName: String,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("html_url") val url: String,
    @field:SerializedName("stargazers_count") val stars: Int,
    @field:SerializedName("forks_count") val forks: Int,
    @field:SerializedName("language") val language: String?
)
