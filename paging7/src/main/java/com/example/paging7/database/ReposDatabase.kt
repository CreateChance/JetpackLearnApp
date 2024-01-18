package com.example.paging7.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.paging7.model.Repo

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/18
 */
@Database(
    entities = [Repo::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ReposDatabase : RoomDatabase() {

    abstract fun reposDao(): RepoDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: ReposDatabase? = null

        fun getInstance(context: Context): ReposDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): ReposDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ReposDatabase::class.java,
                "github_repos.db",
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}