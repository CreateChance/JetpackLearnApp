package com.example.paging7.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.paging7.model.Repo

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/18
 */
@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Repo>)

    @Query("SELECT * FROM repos WHERE name LIKE :query OR description LIKE :query ORDER BY stars DESC, name ASC")
    fun getRepoPagingSourceByName(query: String): PagingSource<Int, Repo>

    @Query("DELETE FROM repos")
    suspend fun clearRepos()
}