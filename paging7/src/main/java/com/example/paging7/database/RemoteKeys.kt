package com.example.paging7.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/18
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val repoId: Long,
    val prevKey: Int?,
    val nextKey: Int?,
)
